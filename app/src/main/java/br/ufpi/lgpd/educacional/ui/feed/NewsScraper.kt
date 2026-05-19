package br.ufpi.lgpd.educacional.ui.feed

import org.jsoup.Jsoup
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

/**
 * Utilitário de Scraping (Extração de Dados) para buscar notícias reais da ANPD.
 * Conforme requisitado, utiliza Threads nativas e Semáforos para demonstrar controle de concorrência.
 */
object NewsScraper {
    // URL oficial de notícias da ANPD
    private const val URL = "https://www.gov.br/anpd/pt-br/assuntos/noticias"
    
    // Semáforo para garantir que apenas uma extração ocorra por vez (Mutex virtual)
    // Isso previne sobrecarga de rede e condições de corrida caso o usuário aperte "atualizar" várias vezes rapidamente.
    private val semaphore = Semaphore(1) 

    fun fetchNews(onSuccess: (List<FeedPost>) -> Unit, onError: (Exception) -> Unit) {
        // Dispara uma nova Thread explicitamente para a operação de I/O de rede
        thread(name = "NewsScraperThread") {
            try {
                // Tenta adquirir a permissão do semáforo. Bloqueia a thread se já estiver em uso.
                semaphore.acquire()
                
                // Conecta e baixa o HTML da página (com User-Agent para não ser bloqueado por firewalls do gov)
                val doc = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .ignoreHttpErrors(true)
                    .sslSocketFactory(null) // Para evitar erros de certificado antigos, se houver
                    .timeout(20000)
                    .get()
                
                val posts = mutableListOf<FeedPost>()
                
                // No portal Gov.br (baseado em Plone), os títulos geralmente ficam na tag h2 com classe 'tileHeadline'
                // Ou 'a.summary.url'. Vamos tentar capturar links de notícias:
                val elements = doc.select("a.summary.url")
                
                // Fallback: se não achar 'a.summary.url', tenta pegar links de h2 ou links dentro de article
                var items = if (elements.isNotEmpty()) elements else doc.select("h2 a")
                if (items.isEmpty()) {
                    items = doc.select("article a")
                }

                for ((index, item) in items.withIndex()) {
                    if (index >= 20) break // Limita aos 20 itens mais recentes
                    
                    val title = item.text().trim()
                    val link = item.attr("abs:href")
                    
                    // A descrição normalmente fica em um <span class="description"> logo após o título
                    var description = item.parent()?.parent()?.select("span.description")?.text()?.trim()
                    
                    if (description.isNullOrEmpty()) {
                        description = "Toque no card para ler a matéria completa diretamente no portal oficial da ANPD."
                    }

                    if (title.isNotEmpty() && link.isNotEmpty() && !title.contains("Carregar", ignoreCase = true)) {
                        posts.add(
                            FeedPost(
                                id = index + 100, // ID artificial
                                authorName = "ANPD Oficial",
                                authorUsername = "@anpd_gov",
                                authorInitials = "A",
                                timeAgo = "Hoje", // Simulando recência
                                content = "$title\n\n$description",
                                linkTitle = "Ler artigo completo no gov.br",
                                linkUrl = link,
                                commentsCount = (1..30).random(),
                                repostsCount = (5..80).random(),
                                likesCount = (20..500).random()
                            )
                        )
                    }
                }
                
                // Libera o semáforo para futuras extrações
                semaphore.release()
                
                if (posts.isNotEmpty()) {
                    onSuccess(posts)
                } else {
                    throw Exception("Lista de notícias vazia. Falha ao analisar o DOM.")
                }
                
            } catch (e: Exception) {
                // Garante que o semáforo seja liberado mesmo em caso de erro (ex: timeout de rede)
                if (semaphore.availablePermits() == 0) {
                    semaphore.release()
                }
                onError(e)
            }
        }
    }
}
