-- ═══════════════════════════════════════════════════════════════════════════
-- LGPD Educacional — schema inicial do Supabase
-- Tabelas: notícias (scrap ANPD), vídeos educacionais (scrap YouTube) e ranking.
-- ═══════════════════════════════════════════════════════════════════════════

-- ── Notícias da ANPD (populadas pela Edge Function `scrape-news`) ──
create table if not exists public.news_articles (
  id uuid primary key default gen_random_uuid(),
  source text not null default 'anpd',
  title text not null,
  summary text not null,
  url text not null unique,
  category text not null default 'Geral',
  author_name text not null default 'ANPD Oficial',
  published_at timestamptz,
  created_at timestamptz not null default now()
);

comment on table public.news_articles is 'Notícias da ANPD coletadas pelo scrap agendado';

-- ── Vídeos educacionais (populadas pela Edge Function `sync-videos`) ──
create table if not exists public.educational_videos (
  id uuid primary key default gen_random_uuid(),
  video_id text not null unique,          -- ID do YouTube (11 chars)
  title text not null,
  channel_title text not null,
  description text,
  lesson_topic text,                       -- categoria temática opcional
  published_at timestamptz,
  created_at timestamptz not null default now()
);

comment on table public.educational_videos is 'Vídeos educacionais sobre LGPD sincronizados via RSS do YouTube';

-- ── Ranking de XP (upsert anônimo por dispositivo) ──
create table if not exists public.leaderboard (
  device_id text primary key,              -- identificador local do dispositivo
  display_name text not null default 'Anônimo',
  avatar_color text not null default '#89B4FA',
  total_points int not null default 0 check (total_points >= 0),
  streak_days int not null default 0,
  lessons_completed int not null default 0,
  updated_at timestamptz not null default now()
);

comment on table public.leaderboard is 'Ranking global de XP dos usuários do app';

-- ═══════════════════════════════════════════════════════════════════════════
-- Índices para as queries do app
-- ═══════════════════════════════════════════════════════════════════════════
create index if not exists idx_news_published on public.news_articles (published_at desc);
create index if not exists idx_news_category on public.news_articles (category);
create index if not exists idx_videos_published on public.educational_videos (published_at desc);
create index if not exists idx_leaderboard_points on public.leaderboard (total_points desc);

-- ═══════════════════════════════════════════════════════════════════════════
-- RLS: leitura pública; escrita apenas via Service Role (Edge Functions)
-- e upsert autosservido no ranking (sem auth, por device_id).
-- ═══════════════════════════════════════════════════════════════════════════
alter table public.news_articles enable row level security;
alter table public.educational_videos enable row level security;
alter table public.leaderboard enable row level security;

-- Notícias: qualquer um lê; só a service role (functions) escreve
create policy "news_select_all" on public.news_articles
  for select using (true);

-- Vídeos: qualquer um lê; só a service role (functions) escreve
create policy "videos_select_all" on public.educational_videos
  for select using (true);

-- Ranking: leitura pública + upsert anônimo limitado (cada device só mexe na própria linha)
create policy "leaderboard_select_all" on public.leaderboard
  for select using (true);

create policy "leaderboard_insert_own" on public.leaderboard
  for insert with check (true);

create policy "leaderboard_update_own" on public.leaderboard
  for update using (true) with check (true);

-- ═══════════════════════════════════════════════════════════════════════════
-- RPC para top N do ranking (ordena e limita no banco)
-- ═══════════════════════════════════════════════════════════════════════════
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
  limit least(max_rows, 100);
$$;
