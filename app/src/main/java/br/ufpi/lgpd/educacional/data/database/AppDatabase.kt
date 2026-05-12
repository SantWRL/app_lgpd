package br.ufpi.lgpd.educacional.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpi.lgpd.educacional.data.dao.UserDao
import br.ufpi.lgpd.educacional.data.model.*

/**
 * Banco de dados Room do aplicativo LGPD Educacional.
 *
 * Versão 2 — adicionados: bio, avatarColorIndex, streakDays, lastStreakDate e tabela quiz_results.
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
    version = 2,
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
                    .fallbackToDestructiveMigration()   // simples p/ desenvolvimento
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
