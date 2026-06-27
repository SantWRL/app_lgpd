package br.ufpi.lgpd.educacional.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
 * Migrações explícitas evitam perda de dados do usuário em futuras atualizações.
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

        /** Migração 1→2: sem alterações de schema, dados preservados. */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) { }
        }

        /** Migração 2→3: sem alterações de schema, dados preservados. */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) { }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lgpd_educacional.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
