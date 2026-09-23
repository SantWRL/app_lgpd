# Setup Supabase — LGPD Educacional

Guia para colocar o backend no ar. O app funciona **mesmo sem Supabase**
(fallback offline), mas com ele ligado você ganha notícias frescas via scrap
agendado, vídeos sincronizados e ranking global de XP.

## 1. Criar o projeto

1. Acesse https://supabase.com e crie um projeto (free tier serve).
2. Anote em **Project Settings → API**:
   - `Project URL` (ex.: `https://abcdefgh.supabase.co`)
   - `anon public key`

## 2. Criar as tabelas

Abra **SQL Editor** no Dashboard do Supabase, cole o conteúdo das migrations
**na ordem** e execute:

1. `supabase/migrations/20260915000000_init.sql` — cria as tabelas, policies
e o RPC `get_top_ranking`
2. `supabase/migrations/20260915120000_leaderboard_security.sql` — hardening:
   fecha a escrita direta do ranking, cria o RPC `submit_score` (validação de
ganhos) e o trigger de `updated_at`

Isso cria:

- `news_articles` — notícias da ANPD (scrap agendado)
- `educational_videos` — vídeos educacionais (RSS do YouTube)
- `leaderboard` — ranking global de XP (escrita só via RPC `submit_score`)
- Policies de RLS: leitura pública, escrita do ranking apenas via RPC validado

## 3. Fazer deploy das Edge Functions

Requisito: [Deno](https://deno.com) e o Supabase CLI (`npx supabase --version`).

```bash
npx supabase login
npx supabase link --project-ref <SEU_PROJECT_REF>

# Functions
npx supabase functions deploy scrape-news
npx supabase functions deploy sync-videos
```

## 4. Agendar o scrap (roda sozinho)

No **SQL Editor**, crie os agendamentos (ajuste `<PROJECT_REF>` e `<SERVICE_ROLE_KEY>`,
que fica em Project Settings → API):

```sql
-- Notícias: de hora em hora
select cron.schedule('scrape-news', '0 * * * *', $$
  select net.http_post(
    url     := 'https://<PROJECT_REF>.supabase.co/functions/v1/scrape-news',
    headers := '{"Authorization": "Bearer <SERVICE_ROLE_KEY>", "Content-Type": "application/json"}'::jsonb
  );
$$);

-- Vídeos: todo dia às 6h30
select cron.schedule('sync-videos', '30 6 * * *', $$
  select net.http_post(
    url     := 'https://<PROJECT_REF>.supabase.co/functions/v1/sync-videos',
    headers := '{"Authorization": "Bearer <SERVICE_ROLE_KEY>", "Content-Type": "application/json"}'::jsonb
  );
$$);
```

> As extensões `pg_cron` e `pg_net` podem ser ativadas em
> Database → Extensions (o cron do Edge Runtime também funciona pelo Dashboard).

### Testar manualmente

```bash
curl -X POST 'https://<PROJECT_REF>.supabase.co/functions/v1/scrape-news' \
  -H 'Authorization: Bearer <SERVICE_ROLE_KEY>'

curl -X POST 'https://<PROJECT_REF>.supabase.co/functions/v1/sync-videos' \
  -H 'Authorization: Bearer <SERVICE_ROLE_KEY>'
```

## 5. Ligar o app

Cole a URL e a anon key em:

```
app/src/main/java/br/ufpi/lgpd/educacional/util/SupabaseConfig.kt
```

```kotlin
object SupabaseConfig {
    const val URL = "https://abcdefgh.supabase.co"
    const val ANON_KEY = "eyJhbGciOi..."
}
```

Pronto: feed passa a puxar do banco, a tela de **Vídeos** lista os vídeos
sincronizados (player do YouTube embutido) e o **ranking global** sobe o XP
do dispositivo automaticamente ao abrir o app.

## Segurança

- A `anon key` pode ir no app: as policies de RLS permitem só leitura
  pública de notícias/vídeos e o ranking só é escrito via RPC `submit_score`,
  que valida ganhos (teto diário) e reseta explícitos.
- A `service_role key` **nunca** vai no app — só no agendador/Edge Functions.
- As functions só aceitam a **service role key** como token (comparação em
  tempo constante) — a anon key não consegue disparar os scrapers sob demanda.
- Opt-out de ranking: o app tem toggle em Configurações; com participação
desativada, o XP sobe como “Anônimo”.
