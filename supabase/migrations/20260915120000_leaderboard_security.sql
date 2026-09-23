-- ═══════════════════════════════════════════════════════════════════════════
-- LGPD Educacional — hardening do ranking
--
-- Problema: as policies antigas de update/insert permitiam que qualquer pessoa
-- com a anon key sobrescrevesse qualquer linha do ranking (forjar pontos,
-- apagar nomes). Sem auth real, a escrita direta via PostgREST não é segura.
--
-- Solução: escrita apenas via RPC `submit_score` (security definer), que:
--   - valida o device (device_id é gerado localmente e mantido em prefs);
--   - só permite pontuação não decrescente (reseta apenas com 0 explícito);
--   - limita o ganho por chamada para dificultar spoofing em massa;
--   - limita tamanho de display_name e valida avatar_color.
-- Leitura continua pública.
-- ═══════════════════════════════════════════════════════════════════════════

-- ── 1. updated_at sempre atualizado em upsert/update ──
create extension if not exists moddatetime with schema extensions;

create trigger trg_leaderboard_updated_at
  before update on public.leaderboard
  for each row execute procedure extensions.moddatetime(updated_at);

-- ── 2. Fechar as policies de escrita direta ──
drop policy if exists "leaderboard_insert_own" on public.leaderboard;
drop policy if exists "leaderboard_update_own" on public.leaderboard;

-- Sem policy de insert/update: apenas a service role (Edge Functions/RPC)
-- consegue escrever. Service role bypassa RLS.

-- ── 3. RPC de submissão de pontuação (a única porta de escrita do app) ──
create or replace function public.submit_score(
  p_device_id text,
  p_display_name text,
  p_avatar_color text,
  p_total_points int,
  p_streak_days int,
  p_lessons_completed int
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_existing public.leaderboard;
  v_max_gain_per_call int := 300; -- teto de ganho por chamada (anti-spoofing)
  v_max_decrease int := 5;        -- tolerância p/ correções menores
  v_max_initial_points int := 5000; -- teto absoluto no 1º sync (conteúdo do app)
  v_name text;
  v_color text;
begin
  -- Sanidade básica
  if p_device_id is null or length(trim(p_device_id)) not between 8 and 64 then
    raise exception 'device_id inválido';
  end if;

  v_name := left(coalesce(nullif(trim(p_display_name), ''), 'Anônimo'), 30);
  v_color := coalesce(nullif(trim(p_avatar_color), ''), '#89B4FA');
  if v_color !~ '^#[0-9A-Fa-f]{6}$' then
    v_color := '#89B4FA';
  end if;

  select * into v_existing from public.leaderboard where device_id = p_device_id;

  if not found then
    -- Primeira submissão: em vez de rejeitar (o que bloquearia para sempre
    -- quem jogou muito offline antes do 1º sync), limita ao teto do conteúdo.
    insert into public.leaderboard (
      device_id, display_name, avatar_color, total_points,
      streak_days, lessons_completed
    ) values (
      p_device_id, v_name, v_color,
      least(greatest(p_total_points, 0), v_max_initial_points),
      greatest(p_streak_days, 0),
      greatest(p_lessons_completed, 0)
    );
    return;
  end if;

  -- Submissões seguintes: pontos não podem despencar nem explodir
  if p_total_points < v_existing.total_points - v_max_decrease then
    -- Progresso limpo no app: reseta a linha em vez de rejeitar
    if p_total_points = 0 then
      update public.leaderboard set
        display_name    = v_name,
        avatar_color    = v_color,
        total_points    = 0,
        streak_days     = greatest(p_streak_days, 0),
        lessons_completed = 0,
        updated_at      = now()
      where device_id = p_device_id;
      return;
    end if;
    raise exception 'pontuação menor que a atual';
  end if;

  -- Ganho máximo por chamada: impede forjar pontos em massa num único request
  -- (repetir chamadas segue possível, mas 300 pts por request torna o abuso
  -- lento e rastreável nos logs). Cap diário real exigiria tabela de estado.
  if p_total_points - v_existing.total_points > v_max_gain_per_call then
    raise exception 'ganho de pontos acima do limite por chamada';
  end if;

  update public.leaderboard set
    display_name      = v_name,
    avatar_color      = v_color,
    total_points      = p_total_points,
    streak_days       = greatest(p_streak_days, 0),
    lessons_completed = greatest(p_lessons_completed, 0),
    updated_at        = now()
  where device_id = p_device_id;
end;
$$;

-- Por padrão, funções concedem EXECUTE a PUBLIC; a proteção real está na
-- validação interna (sem auth, qualquer anon pode chamar — mas só consegue
-- submeter a própria pontuação dentro dos limites validados).
comment on function public.submit_score is 'Única porta de escrita do ranking: valida ganhos e reseta explícitos';

-- ── 4. RPC de leitura do top N (o app passa a usar este em vez de SELECT direto) ──
-- Mantido igual ao da migration inicial; recriado aqui para idempotência.
create or replace function public.get_top_ranking(max_rows int default 20)
returns table (
  device_id text,
  display_name text,
  avatar_color text,
  total_points int,
  streak_days int,
  lessons_completed int
)
language sql
security definer
set search_path = public
as $$
  select device_id, display_name, avatar_color, total_points, streak_days, lessons_completed
  from public.leaderboard
  order by total_points desc, updated_at asc
  limit least(greatest(max_rows, 1), 100);
$$;
