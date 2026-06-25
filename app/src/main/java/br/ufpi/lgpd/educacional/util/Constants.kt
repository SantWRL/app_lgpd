package br.ufpi.lgpd.educacional.util

/**
 * Constantes centralizadas do aplicativo para evitar magic numbers.
 */
object WordleConstants {
    const val GRID_ROWS = 6
    const val GRID_COLS = 5
    const val HINT_PREVIEW_LENGTH = 60
}

object WordsearchConstants {
    const val GRID_SIZE = 10
}

object PointsConstants {
    const val LESSON_COMPLETION = 10
    const val FIRST_QUIZ_ATTEMPT = 20
    const val QUIZ_SCORE_IMPROVEMENT = 5
    const val WORDLE_WIN = 15
    const val WORDSEARCH_WIN = 20
}

object LevelConstants {
    const val LEVEL_1_THRESHOLD = 0
    const val LEVEL_2_THRESHOLD = 150
    const val LEVEL_3_THRESHOLD = 500
    const val LEVEL_4_THRESHOLD = 1000
    const val LEVEL_5_THRESHOLD = 1500
}

object StreakConstants {
    const val MS_PER_DAY = 86_400_000L
    const val MAX_BREAK_STREAK_MS = 2 * MS_PER_DAY
}

object CategoryConstants {
    const val ALL = "Todos"
    const val FUNDAMENTOS = "Fundamentos"
    const val CONFORMIDADE = "Conformidade"
    const val DIREITOS = "Direitos"
    const val ATORES = "Atores"
    const val SEGURANCA = "Segurança"

    fun getAll(): List<String> = listOf(
        ALL, FUNDAMENTOS, CONFORMIDADE, DIREITOS, ATORES, SEGURANCA
    )
}

object AvatarConstants {
    val COLORS = listOf(
        "#89B4FA", "#89DCEB", "#A6E3A1", "#F9E2AF",
        "#F38BA8", "#CBA6F7", "#F5C2E7", "#FAB387"
    )

    val EMOJIS = listOf("🦊", "🐱", "🐶", "🐼", "🦁", "🐸", "🦉", "🐺")
}

object QuizConstants {
    const val DEFAULT_QUIZ_ID = 1
    const val PASSING_SCORE = 70
    const val CORRECT_ANSWER_BG = "#E5F9D1"
    const val INCORRECT_ANSWER_BG = "#FFDFE0"
}

object NetworkConstants {
    const val SCRAPER_TIMEOUT_MS = 20_000
    const val MAX_NEWS_ITEMS = 20
    const val SCRAPER_RETRY_MAX_ATTEMPTS = 3
    const val SCRAPER_RETRY_DELAY_MS = 2_000L
}
