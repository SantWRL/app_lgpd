package br.ufpi.lgpd.educacional.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpi.lgpd.educacional.data.dao.UserDao
import br.ufpi.lgpd.educacional.data.model.Achievement
import br.ufpi.lgpd.educacional.data.model.LessonProgress
import br.ufpi.lgpd.educacional.data.model.Question
import br.ufpi.lgpd.educacional.data.model.Quiz
import br.ufpi.lgpd.educacional.data.model.QuizResultRecord
import br.ufpi.lgpd.educacional.data.model.User
import br.ufpi.lgpd.educacional.data.model.UserAchievement
import br.ufpi.lgpd.educacional.data.model.UserAnswer

/**
 * Banco de dados Room do aplicativo LGPD Educacional.
 *
 * Versão 3 — adiciona unicidade em lesson_progress por userId + lessonId.
 */
@Database(
    entities = [
        User::class,
        LessonProgress::class,
        Achievement::class,
        UserAchievement::class,
        QuizResultRecord::class,
        Quiz::class,
        Question::class,
        UserAnswer::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lgpd_educacional.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
