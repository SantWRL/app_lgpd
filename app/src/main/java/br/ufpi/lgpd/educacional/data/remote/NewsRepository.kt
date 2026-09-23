package br.ufpi.lgpd.educacional.data.remote

import br.ufpi.lgpd.educacional.ui.feed.FeedPost
import br.ufpi.lgpd.educacional.util.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Repositório remoto de notícias da ANPD (tabela `news_articles` no Supabase,
 * populada pela Edge Function agendada `scrape-news`).
 * Converte as linhas do PostgREST para o [FeedPost] já usado pelo feed.
 */
object NewsRepository {

    /** Busca as notícias do banco. Lista vazia se Supabase não configurado ou indisponível. */
    suspend fun fetchRemoteNews(): List<FeedPost> = withContext(Dispatchers.IO) {
        if (!SupabaseConfig.isEnabled) return@withContext emptyList()
        try {
            val rows = SupabaseClient.get(
                table = "news_articles",
                columns = "id,title,summary,url,category,author_name,published_at",
                order = "published_at.desc",
                limit = 30
            )
            (0 until rows.length()).map { i ->
                val row = rows.getJSONObject(i)
                FeedPost(
                    id = row.optString("id").hashCode(),
                    authorName = row.optString("author_name", "ANPD Oficial"),
                    authorUsername = "@anpd_gov",
                    authorInitials = "A",
                    timeAgo = formatTimeAgo(row.optString("published_at")),
                    content = row.optString("summary"),
                    category = row.optString("category", "Regulatório"),
                    linkTitle = "Ler notícia no gov.br",
                    linkUrl = row.optString("url"),
                    commentsCount = 0,
                    repostsCount = 0,
                    likesCount = 0
                )
            }
        } catch (e: Exception) {
            Timber.w(e, "fetchRemoteNews falhou — usando fallback estático")
            emptyList()
        }
    }

    private fun formatTimeAgo(iso: String): String = try {
        val instant = java.time.Instant.parse(iso)
        val mins = (System.currentTimeMillis() - instant.toEpochMilli()) / 60000
        when {
            mins < 1 -> "Agora"
            mins < 60 -> "Há ${mins}min"
            mins < 60 * 24 -> "Há ${mins / 60}h"
            else -> "Há ${mins / (60 * 24)}d"
        }
    } catch (_: Exception) {
        "Atualizado"
    }
}
