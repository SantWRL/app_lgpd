package br.ufpi.lgpd.educacional.ui.feed

/**
 * Representa uma postagem/notícia no feed.
 * @property category Categoria temática (ex: "Fiscalização", "Direitos", "Regulatório", "Internacional", "Educação").
 *                      Usada para filtrar e exibir badges coloridos.
 */
data class FeedPost(
    val id: Int,
    val authorName: String,
    val authorUsername: String,
    val authorInitials: String,
    val timeAgo: String,
    val content: String,
    val category: String = "Geral",
    val linkTitle: String? = null,
    val linkUrl: String? = null,
    val commentsCount: Int,
    val repostsCount: Int,
    var likesCount: Int,
    var isLiked: Boolean = false
)
