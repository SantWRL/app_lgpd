package br.ufpi.lgpd.educacional.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de dados para um Quiz
 */
@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val totalQuestions: Int,
    val passingScore: Int = 70,
    val timeLimit: Int = 0, // em segundos, 0 = sem limite
    var isCompleted: Boolean = false
)

/**
 * Pergunta de um Quiz
 */
@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: Int,
    val quizId: Int,
    val question: String,
    val options: String, // JSON array
    val correctAnswer: Int, // índice da resposta correta
    val explanation: String,
    val orderIndex: Int
)

/**
 * Resposta do usuário em uma pergunta
 */
@Entity(tableName = "user_answers")
data class UserAnswer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val questionId: Int,
    val selectedOption: Int,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Resultado do quiz
 */
data class QuizResult(
    val quizId: Int,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Double,
    val passed: Boolean,
    val timeSpent: Int, // em segundos
    val timestamp: Long = System.currentTimeMillis()
)
