package br.ufpi.lgpd.educacional.ui.quizzes

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.FlashcardContent
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentFlashcardsBinding
import br.ufpi.lgpd.educacional.util.FlashcardsConstants
import br.ufpi.lgpd.educacional.util.PointsConstants
import br.ufpi.lgpd.educacional.util.getUserRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Tela de flashcards de estudo sobre a LGPD.
 * Visual claro inspirado no template "Online Learning Mobile App".
 */
class FlashcardsFragment : Fragment() {

    private var _binding: FragmentFlashcardsBinding? = null
    private val binding get() = _binding!!

    private val repository: UserRepository by lazy { getUserRepository() }

    private lateinit var flashcards: List<FlashcardContent.Flashcard>
    private var currentIndex = 0
    private var isFront = true
    private var isAnimating = false
    private var xpAwarded = false

    private lateinit var frontAnimOut: AnimatorSet
    private lateinit var frontAnimIn: AnimatorSet
    private lateinit var backAnimOut: AnimatorSet
    private lateinit var backAnimIn: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlashcardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAnimations()
        setupClickListeners()
        setupCategoryPills()
        loadCategory(FlashcardContent.categories.first())
    }

    private fun loadAnimations() {
        val context = requireContext()
        val scale = context.resources.displayMetrics.density
        binding.cardFront.cameraDistance = FlashcardsConstants.CAMERA_DISTANCE * scale
        binding.cardBack.cameraDistance = FlashcardsConstants.CAMERA_DISTANCE * scale

        frontAnimOut = AnimatorInflater.loadAnimator(
            context, R.animator.flip_out_left
        ) as AnimatorSet
        frontAnimIn = AnimatorInflater.loadAnimator(
            context, R.animator.flip_in_right
        ) as AnimatorSet
        backAnimOut = AnimatorInflater.loadAnimator(
            context, R.animator.flip_out_right
        ) as AnimatorSet
        backAnimIn = AnimatorInflater.loadAnimator(
            context, R.animator.flip_in_left
        ) as AnimatorSet
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.cardFlipArea.setOnClickListener { flipCard() }
        binding.btnPrevious.setOnClickListener { moveCard(-1) }
        binding.btnNext.setOnClickListener { moveCard(1) }
        binding.btnRestart.setOnClickListener {
            xpAwarded = false
            currentIndex = 0
            isFront = true
            resetCardVisuals()
            showCurrentCard()
        }
    }

    private fun setupCategoryPills() {
        val pills = listOf(
            binding.catFundamentos,
            binding.catDireitos,
            binding.catSeguranca,
            binding.catConformidade
        )
        val categories = FlashcardContent.categories

        pills.forEachIndexed { index, pill ->
            pill.setOnClickListener { loadCategory(categories[index]) }
        }
    }

    private fun setActivePill(selected: TextView) {
        val pills = listOf(
            binding.catFundamentos,
            binding.catDireitos,
            binding.catSeguranca,
            binding.catConformidade
        )
        pills.forEach { pill ->
            if (pill == selected) {
                pill.setBackgroundResource(R.drawable.bg_pill_active)
                pill.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                pill.setBackgroundResource(R.drawable.bg_pill_inactive)
                pill.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
        }
    }

    private fun loadCategory(category: String) {
        val pills = listOf(
            binding.catFundamentos,
            binding.catDireitos,
            binding.catSeguranca,
            binding.catConformidade
        )
        val categories = FlashcardContent.categories
        setActivePill(pills[categories.indexOf(category)])

        flashcards = FlashcardContent.cardsByCategory(category)
        currentIndex = 0
        isFront = true
        xpAwarded = false
        resetCardVisuals()
        showCurrentCard()
    }

    private fun flipCard() {
        if (isAnimating) return
        isAnimating = true
        val context = requireContext()

        if (isFront) {
            frontAnimOut.setTarget(binding.cardFront)
            backAnimIn.setTarget(binding.cardBack)
            binding.cardBack.visibility = View.VISIBLE
            backAnimIn.addListener(onEnd = { isAnimating = false })
            frontAnimOut.start()
            backAnimIn.start()
            binding.tvFlipHint.text = getString(R.string.flashcards_tap_to_term)
        } else {
            backAnimOut.setTarget(binding.cardBack)
            frontAnimIn.setTarget(binding.cardFront)
            backAnimOut.start()
            frontAnimIn.start()
            frontAnimIn.addListener(onEnd = {
                binding.cardBack.visibility = View.INVISIBLE
                isAnimating = false
            })
            binding.tvFlipHint.text = getString(R.string.flashcards_tap_to_definition)
        }
        isFront = !isFront
    }

    private fun moveCard(delta: Int) {
        if (isAnimating) return
        val newIndex = currentIndex + delta
        if (newIndex < 0) return
        if (newIndex >= flashcards.size) {
            awardCompletionXp()
            return
        }

        currentIndex = newIndex
        isFront = true
        resetCardVisuals()
        binding.tvFlipHint.text = getString(R.string.flashcards_tap_to_definition)
        showCurrentCard()
    }

    /**
     * Restaura o estado visual dos dois lados do cartão, limpando
     * rotação/alfa deixados pelas animações de flip.
     */
    private fun resetCardVisuals() {
        binding.cardFront.alpha = 1f
        binding.cardFront.rotationY = 0f
        binding.cardBack.alpha = 1f
        binding.cardBack.rotationY = 0f
        binding.cardBack.visibility = View.INVISIBLE
    }

    private fun showCurrentCard() {
        val card = flashcards[currentIndex]
        binding.cardFrontTerm.text = card.term
        binding.cardBackDefinition.text = card.definition
        binding.cardCategoryBadge.text = card.category

        val progress = ((currentIndex + 1) * 100) / flashcards.size
        binding.flashcardProgressBar.progress = progress
        binding.tvProgressCounter.text =
            getString(R.string.flashcards_counter, currentIndex + 1, flashcards.size)

        binding.tvFlipHint.text = getString(R.string.flashcards_tap_to_definition)
    }

    private fun awardCompletionXp() {
        if (xpAwarded) return
        xpAwarded = true
        viewLifecycleOwner.lifecycleScope.launch {
            repository.addBonusPoints(PointsConstants.FLASHCARDS_COMPLETION)
        }
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.flashcards_done_title))
            .setMessage(
                getString(R.string.flashcards_done_message, PointsConstants.FLASHCARDS_COMPLETION)
            )
            .setPositiveButton(getString(R.string.flashcards_review_again)) { _, _ ->
                currentIndex = 0
                isFront = true
                showCurrentCard()
            }
            .setNegativeButton(getString(R.string.flashcards_keep_going), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun android.animation.Animator.addListener(onEnd: () -> Unit) {
        addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onEnd()
            }
        })
    }
}
