package br.ufpi.lgpd.educacional.ui.lessons

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.remote.VideoRepository
import br.ufpi.lgpd.educacional.databinding.FragmentVideosBinding
import br.ufpi.lgpd.educacional.util.SupabaseConfig
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Tela de vídeos educacionais. A lista vem do Supabase (tabela `educational_videos`,
 * sincronizada pela Edge Function `sync-videos`). Ao tocar, o vídeo toca no player
 * embutido no topo da tela (biblioteca youtubeplayer que o app já usa).
 */
class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: VideosAdapter
    private var youTubePlayer: YouTubePlayer? = null
    private var playerReady = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackVideos.setOnClickListener { findNavController().navigateUp() }

        adapter = VideosAdapter(
            scope = viewLifecycleOwner.lifecycleScope,
            onClick = { video -> playVideo(video.videoId) }
        )
        binding.videosRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@VideosFragment.adapter
        }

        setupPlayer()
        loadVideos()
    }

    private fun setupPlayer() {
        lifecycle.addObserver(binding.youtubePlayer)
        binding.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@VideosFragment.youTubePlayer = youTubePlayer
                playerReady = true
            }
        })
    }

    private fun playVideo(videoId: String) {
        binding.playerCard.visibility = View.VISIBLE
        if (playerReady) {
            youTubePlayer?.loadVideo(videoId, 0f)
            binding.videosRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun loadVideos() {
        if (!SupabaseConfig.isEnabled) {
            showOfflineState()
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val videos = VideoRepository.fetchRemoteVideos()
            if (videos.isEmpty()) {
                showOfflineState()
            } else {
                binding.videosEmptyState.visibility = View.GONE
                binding.tvVideosOffline.visibility = View.GONE
                adapter.submitList(videos)
            }
        }
    }

    private fun showOfflineState() {
        binding.videosEmptyState.visibility = View.VISIBLE
        binding.tvVideosOffline.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youTubePlayer = null
        playerReady = false
        lifecycle.removeObserver(binding.youtubePlayer)
        _binding = null
    }

    // ── Adapter com thumbnail carregada sem bibliotecas extras ────────────────
    class VideosAdapter(
        private val scope: CoroutineScope,
        private val onClick: (VideoRepository.RemoteVideo) -> Unit
    ) : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>() {

        private val items = mutableListOf<VideoRepository.RemoteVideo>()
        private val thumbCache = mutableMapOf<String, Bitmap>()
        private val inflight = mutableMapOf<String, Job>()

        fun submitList(list: List<VideoRepository.RemoteVideo>) {
            items.clear()
            items.addAll(list)
            notifyDataSetChanged()
        }

        class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val thumb: ImageView = view.findViewById(R.id.videoThumb)
            val title: TextView = view.findViewById(R.id.videoTitle)
            val channel: TextView = view.findViewById(R.id.videoChannel)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_video_card, parent, false)
            return VideoViewHolder(view)
        }

        override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
            val video = items[position]
            holder.title.text = video.title
            holder.channel.text = video.channel

            val cached = thumbCache[video.videoId]
            if (cached != null) {
                holder.thumb.setImageBitmap(cached)
            } else {
                holder.thumb.setImageResource(android.R.drawable.ic_media_play)
                loadThumb(video.videoId, holder)
            }

            holder.itemView.setOnClickListener { onClick(video) }
        }

        /** Baixa a thumbnail do CDN do YouTube (sem API key) fora da main thread. */
        private fun loadThumb(videoId: String, holder: VideoViewHolder) {
            if (inflight.containsKey(videoId)) return
            inflight[videoId] = scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    runCatching {
                        val conn = URL(THUMB_URL.format(videoId)).openConnection() as HttpURLConnection
                        conn.connectTimeout = 8000
                        conn.readTimeout = 8000
                        conn.inputStream.use { BitmapFactory.decodeStream(it) }
                    }.getOrNull()
                }
                inflight.remove(videoId)
                if (bitmap != null) {
                    thumbCache[videoId] = bitmap
                    // Só aplica se o holder ainda exibe esse vídeo (recycling-safe)
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION && items[pos].videoId == videoId) {
                        holder.thumb.setImageBitmap(bitmap)
                    }
                }
            }
        }

        override fun getItemCount(): Int = items.size
    }

    companion object {
        private const val THUMB_URL = "https://i.ytimg.com/vi/%s/mqdefault.jpg"
    }
}
