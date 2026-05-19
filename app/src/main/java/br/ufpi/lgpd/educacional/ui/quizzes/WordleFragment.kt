package br.ufpi.lgpd.educacional.ui.quizzes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.FragmentWordleBinding
import br.ufpi.lgpd.educacional.util.UserPreferences
import org.json.JSONObject

class WordleFragment : Fragment() {

    private var _binding: FragmentWordleBinding? = null
    private val binding get() = _binding!!

    // ── Game State ─────────────────────────────────────────────────────────
    private var dictionary: Map<String, String> = emptyMap()
    private var targetWord = ""
    private var targetDefinition = ""
    private var currentRow = 0
    private var currentCol = 0
    private val guessLetters = Array(6) { CharArray(5) { ' ' } }

    // ── All cells and key buttons ───────────────────────────────────────────
    private lateinit var cells: Array<Array<TextView>>
    private val keyMap = mutableMapOf<Char, Button>()

    // Colors — paleta suave
    private val colorCorrect  = Color.parseColor("#A6E3A1")  // green pastel
    private val colorPresent  = Color.parseColor("#F9E2AF")  // yellow pastel
    private val colorAbsent   = Color.parseColor("#45475A")  // surface variant
    private val colorKey      = Color.parseColor("#89B4FA")  // primary pastel
    private val colorTextDark = Color.parseColor("#11111B")  // texto escuro p/ contraste

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDictionary()
        initCells()
        initKeyboard()
        startNewGame()
    }

    // ── Load Dictionary from assets/wordle_dictionary.json ─────────────────
    private fun loadDictionary() {
        try {
            val json = requireContext().assets.open("wordle_dictionary.json")
                .bufferedReader().use { it.readText() }
            val obj = JSONObject(json).getJSONObject("words")
            dictionary = obj.keys().asSequence().associateWith { obj.getString(it) }
        } catch (e: Exception) {
            // fallback hardcoded
            dictionary = mapOf(
                "DADOS" to "Informação sobre pessoa identificada ou identificável.",
                "BASES" to "Hipóteses legais que autorizam o tratamento de dados.",
                "RISCO" to "Probabilidade de eventos negativos à privacidade.",
                "MULTA" to "Penalidade pecuniária que pode chegar a 50 milhões.",
                "LEGAL" to "Em conformidade com o ordenamento jurídico vigente.",
                "FINAL" to "Princípio da Finalidade: uso com propósito legítimo.",
                "CHAVE" to "Recurso de criptografia para a segurança dos dados.",
                "TERMO" to "Instrumento de manifestação de vontade ou uso."
            )
        }
    }

    // ── Map cell IDs ────────────────────────────────────────────────────────
    private fun initCells() {
        val ids = arrayOf(
            arrayOf(R.id.cell_0_0, R.id.cell_0_1, R.id.cell_0_2, R.id.cell_0_3, R.id.cell_0_4),
            arrayOf(R.id.cell_1_0, R.id.cell_1_1, R.id.cell_1_2, R.id.cell_1_3, R.id.cell_1_4),
            arrayOf(R.id.cell_2_0, R.id.cell_2_1, R.id.cell_2_2, R.id.cell_2_3, R.id.cell_2_4),
            arrayOf(R.id.cell_3_0, R.id.cell_3_1, R.id.cell_3_2, R.id.cell_3_3, R.id.cell_3_4),
            arrayOf(R.id.cell_4_0, R.id.cell_4_1, R.id.cell_4_2, R.id.cell_4_3, R.id.cell_4_4),
            arrayOf(R.id.cell_5_0, R.id.cell_5_1, R.id.cell_5_2, R.id.cell_5_3, R.id.cell_5_4)
        )
        cells = Array(6) { row -> Array(5) { col -> binding.root.findViewById(ids[row][col]) } }
    }

    // ── Map keyboard buttons ─────────────────────────────────────────────────
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
            keyMap[char] = btn
            btn.setOnClickListener { onLetterTyped(char) }
        }
        binding.keyENTER.setOnClickListener { onEnter() }
        binding.keyDEL.setOnClickListener { onDelete() }
        binding.btnNewGame.setOnClickListener { startNewGame() }
        binding.btnHint.setOnClickListener { showHint() }
    }

    // ── Start / Reset ────────────────────────────────────────────────────────
    fun startNewGame() {
        val entry = dictionary.entries.random()
        targetWord = entry.key.uppercase()
        targetDefinition = entry.value
        currentRow = 0
        currentCol = 0
        guessLetters.forEach { it.fill(' ') }

        // Reset cells
        cells.forEach { row ->
            row.forEach { cell ->
                cell.text = ""
                cell.setBackgroundResource(R.drawable.bg_wordle_cell_empty)
            }
        }
        // Reset keys
        keyMap.values.forEach { btn ->
            btn.backgroundTintList = requireContext().getColorStateList(R.color.primary)
        }
        // Reset UI state
        binding.definitionCard.visibility = View.GONE
        binding.tvAttemptsLeft.text = "6"
        binding.btnHint.visibility = View.VISIBLE
        binding.tvHint.visibility = View.GONE
    }

    // ── Sistema de Dicas ──────────────────────────────────────────────────────
    private fun showHint() {
        val hint = "Dica: ${targetDefinition.take(60)}..."
        binding.tvHint.text = hint
        binding.tvHint.visibility = View.VISIBLE
        binding.btnHint.visibility = View.GONE
    }

    // ── Input Handlers ───────────────────────────────────────────────────────
    private fun onLetterTyped(char: Char) {
        if (currentCol >= 5 || currentRow >= 6) return
        guessLetters[currentRow][currentCol] = char
        cells[currentRow][currentCol].text = char.toString()
        currentCol++
    }

    private fun onDelete() {
        if (currentCol <= 0) return
        currentCol--
        guessLetters[currentRow][currentCol] = ' '
        cells[currentRow][currentCol].text = ""
    }

    private fun onEnter() {
        if (currentCol < 5) {
            Toast.makeText(requireContext(), "Complete as 5 letras!", Toast.LENGTH_SHORT).show()
            return
        }
        val guess = String(guessLetters[currentRow]).uppercase()
        evaluateGuess(guess)
    }

    // ── Wordle Evaluation Logic (Green / Yellow / Grey) ──────────────────────
    private fun evaluateGuess(guess: String) {
        val result = IntArray(5) { 0 }  // 0=absent, 1=present, 2=correct
        val targetChars = targetWord.toCharArray()
        val guessChars = guess.toCharArray()

        // Pass 1: correct positions (green)
        val usedTarget = BooleanArray(5)
        for (i in 0..4) {
            if (guessChars[i] == targetChars[i]) {
                result[i] = 2
                usedTarget[i] = true
            }
        }
        // Pass 2: present but wrong position (yellow)
        for (i in 0..4) {
            if (result[i] == 2) continue
            for (j in 0..4) {
                if (!usedTarget[j] && guessChars[i] == targetChars[j]) {
                    result[i] = 1
                    usedTarget[j] = true
                    break
                }
            }
        }

        // Apply colors to cells and keyboard
        for (i in 0..4) {
            val cell = cells[currentRow][i]
            val keyBtn = keyMap[guessChars[i]]
            when (result[i]) {
                2 -> {
                    cell.setBackgroundColor(colorCorrect)
                    cell.setTextColor(colorTextDark)
                    keyBtn?.setBackgroundColor(colorCorrect)
                    keyBtn?.setTextColor(colorTextDark)
                }
                1 -> {
                    cell.setBackgroundColor(colorPresent)
                    cell.setTextColor(colorTextDark)
                    if (keyBtn?.currentTextColor != colorTextDark) {
                        keyBtn?.setBackgroundColor(colorPresent)
                        keyBtn?.setTextColor(colorTextDark)
                    }
                }
                else -> {
                    cell.setBackgroundColor(colorAbsent)
                    keyBtn?.setBackgroundColor(colorAbsent)
                }
            }
        }

        val won = result.all { it == 2 }
        currentRow++
        currentCol = 0
        binding.tvAttemptsLeft.text = (6 - currentRow).toString()

        when {
            won -> showResult(true)
            currentRow >= 6 -> showResult(false)
        }
    }

    private fun showResult(won: Boolean) {
        binding.tvDefinitionTitle.text = if (won) "✅ Correto! A palavra era: $targetWord" else "❌ A palavra era: $targetWord"
        binding.tvDefinition.text = targetDefinition
        binding.definitionCard.visibility = View.VISIBLE

        if (won) {
            UserPreferences(requireContext()).addPoints(UserPreferences.POINTS_PER_WORDLE_WIN)
            Toast.makeText(requireContext(), "+${UserPreferences.POINTS_PER_WORDLE_WIN} pontos!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
