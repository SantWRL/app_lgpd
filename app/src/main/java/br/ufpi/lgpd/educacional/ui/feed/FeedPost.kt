package br.ufpi.lgpd.educacional.ui.feed

data class FeedPost(
    val id: Int,
    val authorName: String,
    val authorUsername: String,
    val authorInitials: String,
    val timeAgo: String,
    val content: String,
    val linkTitle: String? = null,
    val linkUrl: String? = null,
    val commentsCount: Int,
    val repostsCount: Int,
    var likesCount: Int,
    var isLiked: Boolean = false
)
