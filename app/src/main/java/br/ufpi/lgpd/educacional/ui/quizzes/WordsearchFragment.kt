package br.ufpi.lgpd.educacional.ui.quizzes

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.FragmentWordsearchBinding
import br.ufpi.lgpd.educacional.util.UserPreferences

class WordsearchFragment : Fragment() {

    private var _binding: FragmentWordsearchBinding? = null
    private val binding get() = _binding!!

    // ── Game config ─────────────────────────────────────────────────────────
    private val gridSize = 10
    private val wordsToFind = listOf("DADOS", "BASES", "RISCO", "MULTA", "FINAL")
    private val foundWords = mutableSetOf<String>()

    // ── Grid state ──────────────────────────────────────────────────────────
    private val grid = Array(gridSize) { CharArray(gridSize) { ' ' } }
    private lateinit var cells: Array<Array<TextView>>

    // ── Touch/drag state ────────────────────────────────────────────────────
    private val selectedCells = mutableListOf<Pair<Int, Int>>()
    private var startRow = -1
    private var startCol = -1

    private val colorFound   = Color.parseColor("#538D4E")   // green
    private val colorSelect  = Color.parseColor("#B59F3B")   // yellow while selecting
    private val colorDefault = Color.parseColor("#33FFFFFF")  // glass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordsearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildGrid()
        setupWordChips()
        binding.btnNewWordSearch.setOnClickListener { resetGame() }
    }

    // ── Build 10×10 grid ────────────────────────────────────────────────────
    private fun buildGrid() {
        cells = Array(gridSize) { arrayOfNulls<TextView>(gridSize) as Array<TextView> }
        placeWordsInGrid()
        fillRemainingCells()
        renderGrid()
    }

    private fun placeWordsInGrid() {
        for (word in wordsToFind) {
            var placed = false
            var attempts = 0
            while (!placed && attempts < 100) {
                attempts++
                val dir = listOf(0 to 1, 1 to 0, 1 to 1).random()
                val row = (0 until gridSize).random()
                val col = (0 until gridSize).random()
                if (canPlace(word, row, col, dir)) {
                    placeWord(word, row, col, dir)
                    placed = true
                }
            }
        }
    }

    private fun canPlace(word: String, row: Int, col: Int, dir: Pair<Int, Int>): Boolean {
        for (i in word.indices) {
            val r = row + i * dir.first
            val c = col + i * dir.second
            if (r < 0 || r >= gridSize || c < 0 || c >= gridSize) return false
            if (grid[r][c] != ' ' && grid[r][c] != word[i]) return false
        }
        return true
    }

    private fun placeWord(word: String, row: Int, col: Int, dir: Pair<Int, Int>) {
        for (i in word.indices) {
            grid[row + i * dir.first][col + i * dir.second] = word[i]
        }
    }

    private fun fillRemainingCells() {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        for (r in 0 until gridSize)
            for (c in 0 until gridSize)
                if (grid[r][c] == ' ') grid[r][c] = letters.random()
    }

    // ── Render grid into GridLayout ──────────────────────────────────────────
    private fun renderGrid() {
        val gridLayout = binding.searchGrid
        gridLayout.removeAllViews()
        gridLayout.columnCount = gridSize
        gridLayout.rowCount = gridSize

        val cellSizePx = (resources.displayMetrics.density * 28).toInt()

        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val tv = TextView(requireContext()).apply {
                    text = grid[r][c].toString()
                    textSize = 13f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    setBackgroundColor(colorDefault)
                    val params = GridLayout.LayoutParams().apply {
                        width = cellSizePx
                        height = cellSizePx
                        setMargins(2, 2, 2, 2)
                        rowSpec = GridLayout.spec(r)
                        columnSpec = GridLayout.spec(c)
                    }
                    layoutParams = params
                }
                cells[r][c] = tv
                gridLayout.addView(tv)
            }
        }

        setupTouchDetection()
    }

    // ── Touch drag detection ─────────────────────────────────────────────────
    private fun setupTouchDetection() {
        binding.searchGrid.setOnTouchListener { v, event ->
            val cellSize = binding.searchGrid.width / gridSize.toFloat()
            val col = (event.x / cellSize).toInt().coerceIn(0, gridSize - 1)
            val row = (event.y / cellSize).toInt().coerceIn(0, gridSize - 1)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    clearSelection()
                    startRow = row; startCol = col
                    selectCell(row, col)
                }
                MotionEvent.ACTION_MOVE -> {
                    clearSelection()
                    selectLineBetween(startRow, startCol, row, col)
                }
                MotionEvent.ACTION_UP -> {
                    val word = getSelectedWord()
                    checkWord(word)
                    clearSelection()
                }
            }
            true
        }
    }

    private fun selectCell(row: Int, col: Int) {
        selectedCells.add(row to col)
        cells[row][col].setBackgroundColor(colorSelect)
    }

    private fun clearSelection() {
        selectedCells.forEach { (r, c) ->
            if (!isCellFound(r, c)) cells[r][c].setBackgroundColor(colorDefault)
        }
        selectedCells.clear()
    }

    private fun isCellFound(row: Int, col: Int): Boolean {
        return foundWords.any { word ->
            // check if this cell belongs to a found word (simplified check)
            cells[row][col].currentTextColor == colorFound
        }
    }

    private fun selectLineBetween(r1: Int, c1: Int, r2: Int, c2: Int) {
        val dr = (r2 - r1).coerceIn(-1, 1)
        val dc = (c2 - c1).coerceIn(-1, 1)
        var r = r1; var c = c1
        while (r != r2 || c != c2) {
            selectCell(r, c)
            if (r == r2 && c == c2) break
            if (r != r2) r += dr
            if (c != c2) c += dc
        }
        selectCell(r2, c2)
    }

    private fun getSelectedWord(): String {
        return selectedCells.joinToString("") { (r, c) -> grid[r][c].toString() }
    }

    private fun checkWord(word: String) {
        val reversed = word.reversed()
        val matched = when {
            wordsToFind.contains(word) && !foundWords.contains(word) -> word
            wordsToFind.contains(reversed) && !foundWords.contains(reversed) -> reversed
            else -> null
        }
        matched?.let {
            foundWords.add(it)
            highlightFoundWord(it)
            markChipFound(it)
            binding.tvWordsLeft.text = (wordsToFind.size - foundWords.size).toString()
            if (foundWords.size == wordsToFind.size) onAllWordsFound()
        }
    }

    private fun highlightFoundWord(word: String) {
        selectedCells.forEach { (r, c) ->
            cells[r][c].setBackgroundColor(colorFound)
        }
    }

    // ── Word chips (labels abaixo do grid) ───────────────────────────────────
    private val chipIds = listOf(R.id.chip_0, R.id.chip_1, R.id.chip_2, R.id.chip_3, R.id.chip_4)

    private fun setupWordChips() {
        wordsToFind.forEachIndexed { i, word ->
            if (i < chipIds.size) {
                binding.root.findViewById<TextView>(chipIds[i])?.text = word
            }
        }
    }

    private fun markChipFound(word: String) {
        val idx = wordsToFind.indexOf(word)
        if (idx >= 0 && idx < chipIds.size) {
            binding.root.findViewById<TextView>(chipIds[idx])?.apply {
                setBackgroundColor(colorFound)
                paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    private fun onAllWordsFound() {
        binding.btnNewWordSearch.visibility = View.VISIBLE
        UserPreferences(requireContext()).addPoints(20)
        Toast.makeText(requireContext(), "🎉 Parabéns! +20 pontos!", Toast.LENGTH_LONG).show()
    }

    private fun resetGame() {
        foundWords.clear()
        grid.forEach { it.fill(' ') }
        placeWordsInGrid()
        fillRemainingCells()
        renderGrid()
        setupWordChips()
        binding.tvWordsLeft.text = wordsToFind.size.toString()
        binding.btnNewWordSearch.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
