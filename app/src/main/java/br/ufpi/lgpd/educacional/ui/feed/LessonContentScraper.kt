package br.ufpi.lgpd.educacional.ui.feed

import br.ufpi.lgpd.educacional.util.NetworkConstants
import org.jsoup.Jsoup
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

/**
 * Resultado de scraping de conteúdo complementar de uma lição.
 */
data class LessonContentResult(
    val lessonKey: String,
    val supplementaryUrl: String?,
    val summary: String?
)

/**
 * LessonContentScraper – Busca conteúdos complementares da LGPD no portal da ANPD
 * usando Jsoup + Semaphore (1 permitido por vez, como NewsScraper).
 *
 * Procura links e resumos associados aos tópicos das aulas no site gov.br/anpd.
 * Quando offline ou sem resultado, usa dados estáticos de fallback.
 */
object LessonContentScraper {

    private const val ANPD_CONTENT_URL =
        "https://www.gov.br/anpd/pt-br/assuntos/noticias"

    private const val ANPD_PORTAL_URL = "https://www.gov.br/anpd/pt-br"

    private val semaphore = Semaphore(1)

    /**
     * Dados estáticos de fallback por categoria/tópico.
     * Usados quando o scraping falha (offline, timeout, etc).
     */
    private val fallbackData = mapOf(
        "Introdução à LGPD" to LessonContentResult(
            lessonKey = "Introdução à LGPD",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/lgpd-o-que-e",
            summary = "A LGPD (Lei nº 13.709/2018) regula o tratamento de dados pessoais no Brasil. " +
                "Ela protege os direitos fundamentais de liberdade e privacidade dos cidadãos."
        ),
        "Dados pessoais e sensíveis" to LessonContentResult(
            lessonKey = "Dados pessoais e sensíveis",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/dados-pessoais",
            summary = "Dado pessoal é qualquer informação que identifique uma pessoa natural. " +
                "Dados sensíveis incluem saúde, origem racial, convicção religiosa e dados genéticos."
        ),
        "10 princípios da LGPD" to LessonContentResult(
            lessonKey = "10 princípios da LGPD",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/lgpd-principios",
            summary = "Os 10 princípios da LGPD incluem finalidade, adequação, necessidade, livre acesso, " +
                "qualidade, transparência, segurança, prevenção, não discriminação e responsabilização."
        ),
        "Bases legais" to LessonContentResult(
            lessonKey = "Bases legais",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/bases-legais",
            summary = "As bases legais são autorizações legais para o tratamento de dados: consentimento, " +
                "obrigação legal, execução de contrato, legítimo interesse, entre outras."
        ),
        "Direitos dos titulares" to LessonContentResult(
            lessonKey = "Direitos dos titulares",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/direitos-dos-titulares",
            summary = "Os titulares podem solicitar acesso, correção, eliminação, portabilidade e " +
                "revogação do consentimento de forma gratuita."
        ),
        "Atores da LGPD" to LessonContentResult(
            lessonKey = "Atores da LGPD",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/atores-da-lgpd",
            summary = "Os principais atores são: titular, controlador, operador, encarregado (DPO) e ANPD. " +
                "Cada um possui responsabilidades bem definidas na lei."
        ),
        "Segurança e prevenção" to LessonContentResult(
            lessonKey = "Segurança e prevenção",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/seguranca-da-informacao",
            summary = "Medidas técnicas (criptografia, backups, firewall) e administrativas " +
                "(políticas de senhas, treinamento) são essenciais para proteger dados."
        ),
        "Incidentes de segurança" to LessonContentResult(
            lessonKey = "Incidentes de segurança",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/comunicacao-de-incidentes",
            summary = "A LGPD exige comunicação à ANPD e aos titulares quando houver incidente " +
                "que possa acarretar risco ou dano relevante."
        ),
        "LGPD no contexto acadêmico" to LessonContentResult(
            lessonKey = "LGPD no contexto acadêmico",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/lgpd-no-ambiente-academico",
            summary = "Universidades tratam dados de matrícula, avaliação e pesquisa. " +
                "O acesso deve ser hierarquizado e pesquisas devem respeitar princípios éticos."
        ),
        "Checklist de conformidade" to LessonContentResult(
            lessonKey = "Checklist de conformidade",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/programa-de-protecao-de-dados",
            summary = "Passos: mapeamento de dados, definição de bases legais, política de privacidade, " +
                "canal de atendimento e atualização de segurança."
        )
    )

    /**
     * Fallback por categoria quando o tópico exato não está no mapa.
     */
    private val categoryFallback = mapOf(
        "Fundamentos" to LessonContentResult(
            lessonKey = "Fundamentos",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/lgpd",
            summary = "Explore os fundamentos da LGPD: conceitos, princípios e bases legais."
        ),
        "Conformidade" to LessonContentResult(
            lessonKey = "Conformidade",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/canais-de-atendimento",
            summary = "Saiba como adequar sua organização às exigências da LGPD."
        ),
        "Direitos" to LessonContentResult(
            lessonKey = "Direitos",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/direitos-dos-titulares",
            summary = "Conheça os direitos que a LGPD garante aos titulares de dados pessoais."
        ),
        "Atores" to LessonContentResult(
            lessonKey = "Atores",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/quem-e-quem-na-lgpd",
            summary = "Entenda o papel de cada ator no ecossistema da proteção de dados."
        ),
        "Segurança" to LessonContentResult(
            lessonKey = "Segurança",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/seguranca-da-informacao",
            summary = "Medidas de segurança da informação para proteger dados pessoais."
        ),
        "Aplicação" to LessonContentResult(
            lessonKey = "Aplicação",
            supplementaryUrl = "$ANPD_PORTAL_URL/assuntos/lgpd",
            summary = "Como aplicar a LGPD na prática, incluindo contexto acadêmico e empresarial."
        )
    )

    private val defaultFallback = LessonContentResult(
        lessonKey = "LGPD",
        supplementaryUrl = ANPD_PORTAL_URL,
        summary = "Acesse o portal da ANPD para mais informações sobre a LGPD."
    )

    /** Retorna dados de fallback para um tópico. */
    private fun getFallback(topics: List<String>): List<LessonContentResult> {
        // Procura fallback por título da aula primeiro, depois por categoria
        val titleFallback = topics.firstNotNullOfOrNull { fallbackData[it] }
        val categoryKey = topics.lastOrNull()
        val catFallback = categoryKey?.let { categoryFallback[it] }

        return topics.map { topic ->
            fallbackData[topic] ?: catFallback ?: titleFallback ?: defaultFallback
        }
    }

    /**
     * Busca conteúdos complementares para os tópicos informados.
     * Em caso de falha, retorna dados estáticos de fallback.
     */
    fun fetchLessonContent(
        topics: List<String>,
        onSuccess: (List<LessonContentResult>) -> Unit
    ) {
        thread(name = "LessonContentScraperThread") {
            var acquired = false
            try {
                semaphore.acquire()
                acquired = true

                val doc = Jsoup.connect(ANPD_CONTENT_URL)
                    .userAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36"
                    )
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(NetworkConstants.SCRAPER_TIMEOUT_MS)
                    .get()

                // Extrai links e títulos de notícias da ANPD
                val anchors = sequenceOf(
                    doc.select("a.summary.url"),
                    doc.select("h2 a"),
                    doc.select("article a")
                ).firstOrNull { it.isNotEmpty() }.orEmpty()

                val articles = anchors
                    .mapNotNull { element ->
                        val title = element.text().trim()
                        val link = element.attr("abs:href").trim()
                        if (title.isBlank() || link.isBlank()) null
                        else title.lowercase() to link
                    }
                    .distinctBy { it.first }
                    .toList()

                val results = mutableListOf<LessonContentResult>()

                for (topic in topics) {
                    val topicLower = topic.lowercase()
                    val matched = articles.firstOrNull { (title, _) ->
                        topicLower.split(" ").any { word ->
                            word.length > 3 && title.contains(word)
                        }
                    }

                    results.add(
                        LessonContentResult(
                            lessonKey = topic,
                            supplementaryUrl = matched?.second,
                            summary = matched?.first?.replaceFirstChar { it.uppercase() }
                        )
                    )
                }

                if (results.isEmpty()) {
                    // Retorna fallback em vez de erro quando não encontra conteúdo online
                    onSuccess(getFallback(topics))
                    return@thread
                }

                // Preenche tópicos sem resultado com fallback
                val filledResults = results.map { result ->
                    if (result.supplementaryUrl == null) {
                        fallbackData[result.lessonKey] ?: result
                    } else result
                }

                onSuccess(filledResults)
            } catch (e: Exception) {
                // Em caso de erro de rede, retorna fallback
                onSuccess(getFallback(topics))
            } finally {
                if (acquired) {
                    semaphore.release()
                }
            }
        }
    }
}
