package br.ufpi.lgpd.educacional.ui.feed

import br.ufpi.lgpd.educacional.util.NetworkConstants
import org.jsoup.Jsoup
import java.io.IOException
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

object NewsScraper {
    private const val URL = "https://www.gov.br/anpd/pt-br/assuntos/noticias"
    private val semaphore = Semaphore(1)

    fun fetchNews(onSuccess: (List<FeedPost>) -> Unit, onError: (Exception) -> Unit) {
        thread(name = "NewsScraperThread") {
            var acquired = false
            try {
                semaphore.acquire()
                acquired = true

                val doc = Jsoup.connect(URL)
                    .userAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36"
                    )
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(NetworkConstants.SCRAPER_TIMEOUT_MS)
                    .get()

                val anchors = sequenceOf(
                    doc.select("a.summary.url"),
                    doc.select("h2 a"),
                    doc.select("article a")
                ).firstOrNull { it.isNotEmpty() }.orEmpty()

                val posts = anchors
                    .asSequence()
                    .mapNotNull { item ->
                        val title = item.text().trim()
                        val link = item.attr("abs:href").trim()
                        val description = item.parents()
                            .select("span.description, p.description, .summary")
                            .firstOrNull()
                            ?.text()
                            ?.trim()
                            .orEmpty()

                        if (title.isBlank() || link.isBlank()) return@mapNotNull null
                        if (title.contains("Carregar", ignoreCase = true)) return@mapNotNull null

                        title to FeedPost(
                            id = link.hashCode(),
                            authorName = "ANPD Oficial",
                            authorUsername = "@anpd_gov",
                            authorInitials = "A",
                            timeAgo = "Atualizado",
                            content = buildString {
                                append(title)
                                append("\n\n")
                                append(
                                    if (description.isNotBlank()) description
                                    else "Toque no card para abrir a noticia oficial no portal gov.br."
                                )
                            },
                            linkTitle = "Ler noticia no gov.br",
                            linkUrl = link,
                            commentsCount = 0,
                            repostsCount = 0,
                            likesCount = 0
                        )
                    }
                    .distinctBy { it.first }
                    .map { it.second }
                    .take(NetworkConstants.MAX_NEWS_ITEMS)
                    .toList()

                if (posts.isEmpty()) {
                    throw IOException("Nao foi possivel extrair noticias da ANPD.")
                }

                onSuccess(posts)
            } catch (e: Exception) {
                onError(e)
            } finally {
                if (acquired) {
                    semaphore.release()
                }
            }
        }
    }
}
