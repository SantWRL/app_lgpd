package br.ufpi.lgpd.educacional.ui.quizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.data.repository.UserRepository
import br.ufpi.lgpd.educacional.databinding.FragmentWordleBinding
import br.ufpi.lgpd.educacional.util.PointsConstants
import br.ufpi.lgpd.educacional.util.WordleConstants
import br.ufpi.lgpd.educacional.util.getUserRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class WordleFragment : Fragment() {

    private var _binding: FragmentWordleBinding? = null
    private val binding get() = _binding!!

    private var dictionary: Map<String, String> = emptyMap()
    private var targetWord = ""
    private var targetDefinition = ""
    private var currentRow = 0
    private var currentCol = 0
    private var isGameOver = false
    private val guessLetters = Array(WordleConstants.GRID_ROWS) { CharArray(WordleConstants.GRID_COLS) { ' ' } }

    private lateinit var wordleCellGrid: Array<Array<TextView>>
    private val keyboardLetterButtons = mutableMapOf<Char, Button>()
    private lateinit var repository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = getUserRepository()
        loadDictionary()
        initCells()
        initKeyboard()
        startNewGame()
    }

    private fun loadDictionary() {
        try {
            val json = requireContext().assets.open("wordle_dictionary.json")
                .bufferedReader().use { it.readText() }
            val obj = JSONObject(json).getJSONObject("words")
            dictionary = obj.keys().asSequence().associateWith { obj.getString(it) }
        } catch (_: Exception) {
            dictionary = mapOf(
                "DADOS" to "Informacao sobre pessoa identificada ou identificavel.",
                "BASES" to "Hipoteses legais que autorizam o tratamento de dados.",
                "RISCO" to "Probabilidade de eventos negativos a privacidade.",
                "MULTA" to "Penalidade pecuniaria que pode chegar a 50 milhoes.",
                "LEGAL" to "Em conformidade com o ordenamento juridico vigente.",
                "FINAL" to "Principio da finalidade: uso com proposito legitimo.",
                "CHAVE" to "Recurso de criptografia para a segurança dos dados.",
                "TERMO" to "Instrumento de manifestacao de vontade ou uso."
            )
        }
    }

    private fun initCells() {
        val ids = arrayOf(
            arrayOf(R.id.cell_0_0, R.id.cell_0_1, R.id.cell_0_2, R.id.cell_0_3, R.id.cell_0_4),
            arrayOf(R.id.cell_1_0, R.id.cell_1_1, R.id.cell_1_2, R.id.cell_1_3, R.id.cell_1_4),
            arrayOf(R.id.cell_2_0, R.id.cell_2_1, R.id.cell_2_2, R.id.cell_2_3, R.id.cell_2_4),
            arrayOf(R.id.cell_3_0, R.id.cell_3_1, R.id.cell_3_2, R.id.cell_3_3, R.id.cell_3_4),
            arrayOf(R.id.cell_4_0, R.id.cell_4_1, R.id.cell_4_2, R.id.cell_4_3, R.id.cell_4_4),
            arrayOf(R.id.cell_5_0, R.id.cell_5_1, R.id.cell_5_2, R.id.cell_5_3, R.id.cell_5_4)
        )
        wordleCellGrid = Array(WordleConstants.GRID_ROWS) { row ->
            Array(WordleConstants.GRID_COLS) { col ->
                binding.root.findViewById(ids[row][col])
            }
        }
    }

    private fun initKeyboard() {
        val letterIds = mapOf(
            'Q' to R.id.key_Q, 'W' to R.id.key_W, 'E' to R.id.key_E, 'R' to R.id.key_R,
            'T' to R.id.key_T, 'Y' to R.id.key_Y, 'U' to R.id.key_U, 'I' to R.id.key_I,
            'O' to R.id.key_O, 'P' to R.id.key_P, 'A' to R.id.key_A, 'S' to R.id.key_S,
            'D' to R.id.key_D, 'F' to R.id.key_F, 'G' to R.id.key_G, 'H' to R.id.key_H,
            'J' to R.id.key_J, 'K' to R.id.key_K, 'L' to R.id.key_L, 'Z' to R.id.key_Z,
            'X' to R.id.key_X, 'C' to R.id.key_C, 'V' to R.id.key_V, 'B' to R.id.key_B,
            'N' to R.id.key_N, 'M' to R.id.key_M
        )
        letterIds.forEach { (char, id) ->
            val btn = binding.root.findViewById<Button>(id)
            keyboardLetterButtons[char] = btn
            btn.setOnClickListener { onLetterTyped(char) }
        }
        binding.keyENTER.setOnClickListener { onEnter() }
        binding.keyDEL.setOnClickListener { onDelete() }
        binding.btnNewGame.setOnClickListener { startNewGame() }
        binding.btnHint.setOnClickListener { showHint() }
    }

    fun startNewGame() {
        val entry = dictionary.entries.random()
        targetWord = entry.key.uppercase()
        targetDefinition = entry.value
        currentRow = 0
        currentCol = 0
        isGameOver = false
        guessLetters.forEach { it.fill(' ') }

        wordleCellGrid.forEach { row ->
            row.forEach { cell ->
                cell.text = ""
                cell.setBackgroundResource(R.drawable.bg_wordle_cell_empty)
                cell.setTextColor(ContextCompat.getColor(requireContext(), R.color.wordle_text_light))
            }
        }

        keyboardLetterButtons.values.forEach { btn ->
            btn.setBackgroundResource(R.drawable.bg_wordle_key)
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.wordle_text_light))
        }

        binding.keyENTER.setBackgroundResource(R.drawable.bg_wordle_key_action)
        binding.keyENTER.setTextColor(ContextCompat.getColor(requireContext(), R.color.wordle_text_dark))
        binding.keyDEL.setBackgroundResource(R.drawable.bg_wordle_key_action)
        binding.keyDEL.setTextColor(ContextCompat.getColor(requireContext(), R.color.wordle_text_dark))
        binding.definitionCard.visibility = View.GONE
        binding.tvAttemptsLeft.text = WordleConstants.GRID_ROWS.toString()
        binding.btnHint.visibility = View.VISIBLE
        binding.tvHint.visibility = View.GONE
    }

    private fun showHint() {
        binding.tvHint.text = "Dica: ${targetDefinition.take(WordleConstants.HINT_PREVIEW_LENGTH)}..."
        binding.tvHint.visibility = View.VISIBLE
        binding.btnHint.visibility = View.GONE
    }

    private fun onLetterTyped(char: Char) {
        if (isGameOver) return
        if (currentCol >= WordleConstants.GRID_COLS || currentRow >= WordleConstants.GRID_ROWS) return
        guessLetters[currentRow][currentCol] = char
        wordleCellGrid[currentRow][currentCol].text = char.toString()
        currentCol++
    }

    private fun onDelete() {
        if (isGameOver) return
        if (currentCol <= 0) return
        currentCol--
        guessLetters[currentRow][currentCol] = ' '
        wordleCellGrid[currentRow][currentCol].text = ""
    }

    private fun onEnter() {
        if (isGameOver) return
        if (currentCol < WordleConstants.GRID_COLS) {
            Toast.makeText(requireContext(), "Complete as ${WordleConstants.GRID_COLS} letras!", Toast.LENGTH_SHORT).show()
            return
        }
        evaluateGuess(String(guessLetters[currentRow]).uppercase())
    }

    private fun evaluateGuess(guess: String) {
        val result = IntArray(WordleConstants.GRID_COLS)
        val targetChars = targetWord.toCharArray()
        val guessChars = guess.toCharArray()
        val usedTarget = BooleanArray(WordleConstants.GRID_COLS)

        for (i in 0 until WordleConstants.GRID_COLS) {
            if (guessChars[i] == targetChars[i]) {
                result[i] = 2
                usedTarget[i] = true
            }
        }

        for (i in 0 until WordleConstants.GRID_COLS) {
            if (result[i] == 2) continue
            for (j in 0 until WordleConstants.GRID_COLS) {
                if (!usedTarget[j] && guessChars[i] == targetChars[j]) {
                    result[i] = 1
                    usedTarget[j] = true
                    break
                }
            }
        }

        val colorTextDark = ContextCompat.getColor(requireContext(), R.color.wordle_text_dark)
        val colorTextLight = ContextCompat.getColor(requireContext(), R.color.wordle_text_light)

        for (i in 0 until WordleConstants.GRID_COLS) {
            val cell = wordleCellGrid[currentRow][i]
            val keyBtn = keyboardLetterButtons[guessChars[i]]
            when (result[i]) {
                2 -> {
                    cell.setBackgroundResource(R.drawable.bg_wordle_cell_correct)
                    cell.setTextColor(colorTextDark)
                    keyBtn?.setBackgroundResource(R.drawable.bg_wordle_key_correct)
                    keyBtn?.setTextColor(colorTextDark)
                }
                1 -> {
                    cell.setBackgroundResource(R.drawable.bg_wordle_cell_present)
                    cell.setTextColor(colorTextDark)
                    if (keyBtn?.background?.constantState !=
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_wordle_key_correct)?.constantState
                    ) {
                        keyBtn?.setBackgroundResource(R.drawable.bg_wordle_key_present)
                        keyBtn?.setTextColor(colorTextDark)
                    }
                }
                else -> {
                    cell.setBackgroundResource(R.drawable.bg_wordle_cell_absent)
                    cell.setTextColor(colorTextLight)
                    if (keyBtn?.background?.constantState ==
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_wordle_key)?.constantState
                    ) {
                        keyBtn?.setBackgroundResource(R.drawable.bg_wordle_key_absent)
                        keyBtn?.setTextColor(colorTextLight)
                    }
                }
            }
        }

        val won = result.all { it == 2 }
        currentRow++
        currentCol = 0
        binding.tvAttemptsLeft.text = (WordleConstants.GRID_ROWS - currentRow).toString()

        when {
            won -> showResult(true)
            currentRow >= WordleConstants.GRID_ROWS -> showResult(false)
        }
    }

    private fun showResult(won: Boolean) {
        isGameOver = true
        binding.tvDefinitionTitle.text =
            if (won) "Correto! A palavra era: $targetWord" else "A palavra era: $targetWord"
        binding.tvDefinition.text = targetDefinition
        binding.definitionCard.visibility = View.VISIBLE

        if (won) {
            viewLifecycleOwner.lifecycleScope.launch {
                repository.ensureUserExists()
                repository.addBonusPoints(PointsConstants.WORDLE_WIN)
            }
            Toast.makeText(
                requireContext(),
                "+${PointsConstants.WORDLE_WIN} pontos!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
 
