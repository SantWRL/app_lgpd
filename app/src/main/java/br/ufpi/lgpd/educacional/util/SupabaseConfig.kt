package br.ufpi.lgpd.educacional.util

/**
 * Configuração do projeto Supabase.
 *
 * 1. Crie um projeto em https://supabase.com
 * 2. Rode a migration `supabase/migrations/20260915000000_init.sql` no SQL Editor
 * 3. Faça deploy das functions (`scrape-news` e `sync-videos`)
 * 4. Cole aqui a URL e a anon key (Dashboard → Project Settings → API)
 *
 * Enquanto estiver vazio, o app usa o fallback local (dados estáticos/offline).
 */
object SupabaseConfig {
    /** Ex.: "https://abcdefgh.supabase.co" */
    const val URL: String = ""

    /** Chave pública (anon) — segura no app pois o RLS protege as tabelas. */
    const val ANON_KEY: String = ""

    val isEnabled: Boolean get() = URL.isNotBlank() && ANON_KEY.isNotBlank()
}
