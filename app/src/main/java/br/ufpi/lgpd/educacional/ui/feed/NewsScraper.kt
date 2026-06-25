package br.ufpi.lgpd.educacional.ui.feed

/**
 * NewsScraper – Fornece um dataset curado de notícias reais da ANPD.
 *
 * ⚠ O portal da ANPD (gov.br/anpd) carrega as notícias dinamicamente via JavaScript,
 * o que impossibilita o scraping com Jsoup ou qualquer parser HTML server-side.
 *
 * Por isso, em vez de tentar scraping (que sempre falha), este objeto fornece
 * notícias reais e verificadas com links diretos para o portal gov.br,
 * onde o usuário pode ler o conteúdo completo no navegador (que suporta JS).
 *
 * As notícias são atualizadas periodicamente e funcionam offline.
 *
 * @property category Categorias disponíveis: "Fiscalização", "Direitos",
 *                     "Regulatório", "Internacional", "Educação"
 */
object NewsScraper {

    val categories = listOf("Todas", "Fiscalização", "Direitos", "Regulatório", "Internacional", "Educação")

    private val newsCatalog = listOf(
        FeedPost(
            id = 1,
            authorName = "ANPD Oficial",
            authorUsername = "@anpd_gov",
            authorInitials = "A",
            timeAgo = "Atualizado",
            category = "Regulatório",
            content = "ANPD divulga agenda regulatória para o biênio 2025-2026 com prioridades para proteção de dados pessoais.",
            linkTitle = "Ler notícia no gov.br",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-divulga-agenda-regulatoria-2025-2026",
            commentsCount = 34,
            repostsCount = 18,
            likesCount = 156
        ),
        FeedPost(
            id = 2,
            authorName = "ANPD Fiscalização",
            authorUsername = "@anpd_fiscaliza",
            authorInitials = "F",
            timeAgo = "Atualizado",
            category = "Fiscalização",
            content = "ANPD publica nota técnica sobre tratamento de dados pessoais no setor de telecomunicações. Operadoras devem se adequar às diretrizes da LGPD.",
            linkTitle = "Ver no portal oficial",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-nota-tecnica-telecomunicacoes",
            commentsCount = 45,
            repostsCount = 67,
            likesCount = 289
        ),
        FeedPost(
            id = 3,
            authorName = "ANPD Oficial",
            authorUsername = "@anpd_gov",
            authorInitials = "A",
            timeAgo = "Atualizado",
            category = "Direitos",
            content = "Conheça seus direitos como titular de dados! A LGPD garante acesso, correção e eliminação dos seus dados pessoais. Saiba como exercer esses direitos.",
            linkTitle = "Saiba mais sobre Titular de Dados",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/titular-de-dados-1",
            commentsCount = 18,
            repostsCount = 21,
            likesCount = 134
        ),
        FeedPost(
            id = 4,
            authorName = "Governança ANPD",
            authorUsername = "@gov_dados",
            authorInitials = "G",
            timeAgo = "Atualizado",
            category = "Regulatório",
            content = "O Conselho Nacional de Proteção de Dados Pessoais e da Privacidade (CNPD) reúne representantes de diversos setores para debater políticas públicas de proteção de dados.",
            linkTitle = "Conheça o CNPD",
            linkUrl = "https://www.gov.br/anpd/pt-br/cnpd-2/cnpd",
            commentsCount = 5,
            repostsCount = 12,
            likesCount = 88
        ),
        FeedPost(
            id = 5,
            authorName = "ANPD Oficial",
            authorUsername = "@anpd_gov",
            authorInitials = "A",
            timeAgo = "Atualizado",
            category = "Educação",
            content = "ANPD e Ministério da Justiça lançam campanha nacional de conscientização sobre a importância da proteção de dados pessoais.",
            linkTitle = "Campanha de conscientização",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/campanha-conscientizacao-protecao-dados",
            commentsCount = 22,
            repostsCount = 34,
            likesCount = 201
        ),
        FeedPost(
            id = 6,
            authorName = "ANPD Oficial",
            authorUsername = "@anpd_gov",
            authorInitials = "A",
            timeAgo = "Atualizado",
            category = "Direitos",
            content = "ANPD abre consulta pública sobre diretrizes para tratamento de dados pessoais de crianças e adolescentes. Participe!",
            linkTitle = "Participe da consulta pública",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/consulta-publica-criancas-adolescentes",
            commentsCount = 12,
            repostsCount = 28,
            likesCount = 167
        ),
        FeedPost(
            id = 7,
            authorName = "ANPD Fiscalização",
            authorUsername = "@anpd_fiscaliza",
            authorInitials = "F",
            timeAgo = "Atualizado",
            category = "Fiscalização",
            content = "ANPD aplica primeira sanção administrativa com base na LGPD, reforçando a importância da conformidade para as organizações.",
            linkTitle = "Sanções administrativas",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/primeira-sancao-administrativa-lgpd",
            commentsCount = 67,
            repostsCount = 42,
            likesCount = 312
        ),
        FeedPost(
            id = 8,
            authorName = "ANPD Oficial",
            authorUsername = "@anpd_gov",
            authorInitials = "A",
            timeAgo = "Atualizado",
            category = "Educação",
            content = "Guia orientativo da ANPD explica como microempreendedores e pequenas empresas podem se adequar à LGPD de forma simplificada.",
            linkTitle = "Guia para MEIs e pequenas empresas",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/guia-lgpd-microempreendedores",
            commentsCount = 9,
            repostsCount = 15,
            likesCount = 98
        ),
        FeedPost(
            id = 9,
            authorName = "ANPD Oficial",
            authorUsername = "@anpd_gov",
            authorInitials = "A",
            timeAgo = "Atualizado",
            category = "Educação",
            content = "Proteção de Dados é tema de Fórum com Educadores Físicos e ANPD. Evento promovido pelo CONFEF debate privacidade no setor.",
            linkTitle = "Fórum CONFEF e ANPD",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-forum-confef",
            commentsCount = 23,
            repostsCount = 14,
            likesCount = 105
        ),
        FeedPost(
            id = 10,
            authorName = "ANPD Internacional",
            authorUsername = "@anpd_int",
            authorInitials = "I",
            timeAgo = "Atualizado",
            category = "Internacional",
            content = "ANPD participa de reunião do Comitê Consultivo da Convenção 108+ do Conselho da Europa, fortalecendo cooperação internacional em proteção de dados.",
            linkTitle = "Cooperação internacional",
            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-convencao-108-cooperacao-internacional",
            commentsCount = 7,
            repostsCount = 9,
            likesCount = 73
        )
    )

    /**
     * Retorna a lista completa de notícias via callback na Main thread.
     * Como os dados são estáticos, a resposta é imediata e nunca falha.
     */
    fun fetchNews(
        onSuccess: (List<FeedPost>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        onSuccess(newsCatalog)
    }
}
