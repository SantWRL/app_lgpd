package br.ufpi.lgpd.educacional.data.remote

import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.util.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Repositório remoto de vídeos educacionais (tabela `educational_videos`).
 * Mapeia as linhas do Supabase para objetos [Lesson] do app, reaproveitando
 * o player do YouTube que já existe na LessonDetailFragment.
 */
object VideoRepository {

    data class RemoteVideo(
        val videoId: String,
        val title: String,
        val channel: String,
        val topic: String
    )

    /** Busca vídeos do Supabase ordenados por publicação. Lista vazia se indisponível. */
    suspend fun fetchRemoteVideos(): List<RemoteVideo> = withContext(Dispatchers.IO) {
        if (!SupabaseConfig.isEnabled) return@withContext emptyList()
        try {
            val rows = SupabaseClient.get(
                table = "educational_videos",
                columns = "video_id,title,channel_title,lesson_topic",
                order = "published_at.desc",
                limit = 24
            )
            (0 until rows.length()).map { i ->
                val row = rows.getJSONObject(i)
                RemoteVideo(
                    videoId = row.optString("video_id"),
                    title = row.optString("title"),
                    channel = row.optString("channel_title", "YouTube"),
                    topic = row.optString("lesson_topic", "LGPD")
                )
            }
        } catch (e: Exception) {
            Timber.w(e, "fetchRemoteVideos falhou — usando fallback local")
            emptyList()
        }
    }

    /**
     * Converte um vídeo remoto em [Lesson] para navegar para a LessonDetailFragment,
     * que já tem o player embutido do YouTube.
     */
    fun toLesson(video: RemoteVideo, index: Int): Lesson = Lesson(
        id = 10_000 + index, // IDs sintáticos, longe dos IDs das lições estáticas
        title = video.title,
        description = "Vídeo de ${video.channel}",
        content = "Vídeo educacional sobre LGPD publicado por ${video.channel}.",
        category = video.topic,
        orderIndex = index,
        estimatedTime = 10,
        difficulty = "BEGINNER",
        videoId = video.videoId
    )
}
