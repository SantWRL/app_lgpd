package br.ufpi.lgpd.educacional.ui.lessons

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentLessonDetailBinding
import br.ufpi.lgpd.educacional.ui.feed.LessonContentScraper
import br.ufpi.lgpd.educacional.util.AnimationUtils
import br.ufpi.lgpd.educacional.util.getUserRepository
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.launch

class LessonDetailFragment : Fragment() {

    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!

    private var lessonId: Int = 0
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lessonId = it.getInt(ARG_LESSON_ID, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = getUserRepository()

        setupUI()
        loadLesson()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMarkCompleted.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                repository.ensureUserExists()
                repository.markLessonCompleted(lessonId)
                Toast.makeText(requireContext(), "Lição concluída! +10 XP", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun loadLesson() {
        val lesson = LgpdContent.lessons.find { it.id == lessonId }
        if (lesson == null) {
            Toast.makeText(requireContext(), "Lição não encontrada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        binding.apply {
            tvLessonCategory.text = lesson.category.uppercase()
            tvLessonTitle.text = lesson.title
            tvLessonDescription.text = lesson.description

            val textWithHtml = lesson.content
                .replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
                .replace("\n", "<br>")
            tvLessonContent.text = HtmlCompat.fromHtml(textWithHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)

            // Animate content entrance
            AnimationUtils.slideUpFadeIn(tvLessonCategory, delay = 0L)
            AnimationUtils.slideUpFadeIn(tvLessonTitle, delay = 80L)
            AnimationUtils.slideUpFadeIn(tvLessonDescription, delay = 160L)
            AnimationUtils.slideUpFadeIn(tvLessonContent, delay = 240L)
        }

        if (lesson.videoId != null) {
            binding.videoCard.visibility = View.VISIBLE
            setupYoutubePlayer(lesson.videoId)
        } else {
            binding.videoCard.visibility = View.GONE
            // Try to fetch supplementary content from the scraper as fallback
            fetchSupplementaryContent(lesson)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repository.ensureUserExists()
            if (repository.isLessonCompleted(lessonId)) {
                binding.btnMarkCompleted.text = "Lição concluída"
                binding.btnMarkCompleted.isEnabled = false
                binding.btnMarkCompleted.alpha = 0.5f
            }
        }
    }

    private fun fetchSupplementaryContent(lesson: br.ufpi.lgpd.educacional.data.model.Lesson) {
        LessonContentScraper.fetchLessonContent(
            topics = listOf(lesson.title, lesson.category),
            onSuccess = { results ->
                val content = results.firstOrNull()
                if (content?.supplementaryUrl != null && isAdded) {
                    activity?.runOnUiThread {
                        // Show supplementary content card with summary
                        binding.videoCard.visibility = View.VISIBLE
                        binding.youtubePlayerView.visibility = View.GONE
                        binding.youtubePlayerView.removeAllViews()

                        // Insert summary text into the YouTube player container
                        if (content.summary != null) {
                            val summaryView = android.widget.TextView(requireContext()).apply {
                                text = content.summary
                                setTextColor(requireContext().getColor(R.color.text_primary))
                                textSize = 14f
                                setPadding(16, 12, 16, 8)
                            }
                            binding.youtubePlayerView.addView(summaryView)
                        }

                        binding.videoCard.setOnClickListener {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(content.supplementaryUrl)))
                            } catch (_: Exception) {
                                Toast.makeText(requireContext(), "Link indisponível", Toast.LENGTH_SHORT).show()
                            }
                        }
                        AnimationUtils.slideUpFadeIn(binding.videoCard, delay = 300L)
                    }
                }
            }
        )
    }

    private fun setupYoutubePlayer(videoId: String) {
        if (!isAdded) return
        val youtubePlayerView = binding.youtubePlayerView
        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (isAdded) {
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_LESSON_ID = "lessonId"
    }
}
