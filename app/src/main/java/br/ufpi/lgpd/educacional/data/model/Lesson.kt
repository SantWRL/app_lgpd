package br.ufpi.lgpd.educacional.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de dados para uma Lição
 */
@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val content: String,
    val category: String,
    val orderIndex: Int,
    val estimatedTime: Int, // em minutos
    val difficulty: String, // BEGINNER, INTERMEDIATE, ADVANCED
    val videoId: String? = null, // YouTube video ID, se houver
    var isCompleted: Boolean = false
)

/**
 * Detalhes completos de uma lição
 */
data class LessonDetail(
    val id: Int,
    val title: String,
    val description: String,
    val content: String,
    val sections: List<LessonSection>,
    val keyPoints: List<String>,
    val references: List<String>,
    val difficulty: String
)

/**
 * Seção de uma lição
 */
data class LessonSection(
    val id: String,
    val title: String,
    val content: String,
    val type: String // TEXT, IMAGE, VIDEO, INTERACTIVE
)
