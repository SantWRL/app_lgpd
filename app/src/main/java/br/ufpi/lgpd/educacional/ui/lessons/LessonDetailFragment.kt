package br.ufpi.lgpd.educacional.ui.lessons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.ufpi.lgpd.educacional.data.LgpdContent
import br.ufpi.lgpd.educacional.data.database.AppDatabase
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentLessonDetailBinding
import br.ufpi.lgpd.educacional.util.UserPreferences
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.launch

class LessonDetailFragment : Fragment() {

    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!

    private var lessonId: Int = 0
    private lateinit var repository: UserRepository
    private lateinit var userPreferences: UserPreferences

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
        repository = UserRepository(AppDatabase.getInstance(requireContext()).userDao())
        userPreferences = UserPreferences(requireContext())
        
        setupUI()
        loadLesson()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMarkCompleted.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                repository.markLessonCompleted(lessonId)
                userPreferences.completeLesson(lessonId) // Sync with prefs for now
                Toast.makeText(requireContext(), "Lição concluída! +10 XP", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun loadLesson() {
        val lesson = LgpdContent.lessons.find { it.id == lessonId }
        if (lesson != null) {
            binding.apply {
                tvLessonCategory.text = lesson.category.uppercase()
                tvLessonTitle.text = lesson.title
                tvLessonDescription.text = lesson.description
                
                // Formatar Markdown simples para Negrito e Quebras de linha
                val textWithHtml = lesson.content
                    .replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
                    .replace("\n", "<br>")
                
                tvLessonContent.text = HtmlCompat.fromHtml(textWithHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)

                // Carrega vídeo do YouTube se existir
                if (lesson.videoId != null) {
                    videoCard.visibility = View.VISIBLE
                    setupYoutubePlayer(lesson.videoId)
                } else {
                    videoCard.visibility = View.GONE
                }

                // Check completion
                if (userPreferences.isLessonCompleted(lessonId)) {
                    btnMarkCompleted.text = "Lição Concluída"
                    btnMarkCompleted.isEnabled = false
                    btnMarkCompleted.alpha = 0.5f
                }
            }
        } else {
            Toast.makeText(requireContext(), "Lição não encontrada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun setupYoutubePlayer(videoId: String) {
        val youtubePlayerView = binding.youtubePlayerView
        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0f)
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
