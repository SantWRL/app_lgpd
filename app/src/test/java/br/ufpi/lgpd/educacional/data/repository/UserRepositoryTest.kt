package br.ufpi.lgpd.educacional.data.repository

import br.ufpi.lgpd.educacional.data.dao.UserDao
import br.ufpi.lgpd.educacional.data.model.LessonProgress
import br.ufpi.lgpd.educacional.data.model.QuizResultRecord
import br.ufpi.lgpd.educacional.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UserRepositoryTest {

    // Simples Fake DAO para testar o UserRepository sem banco de dados
    class FakeUserDao : UserDao {
        private var user: User? = null
        private val quizResults = mutableListOf<QuizResultRecord>()

        override fun observeUser(userId: String): Flow<User?> = flowOf(user)
        override suspend fun getUser(userId: String): User? = user
        override suspend fun upsertUser(user: User) { this.user = user }
        override suspend fun deleteUser(userId: String) { this.user = null }

        override suspend fun insertQuizResult(record: QuizResultRecord): Long {
            quizResults.add(record)
            return 1L
        }
        override fun observeQuizResults(userId: String): Flow<List<QuizResultRecord>> = flowOf(quizResults)
        override suspend fun getBestQuizScore(userId: String, quizId: Int): Int? = quizResults.filter { it.quizId == quizId }.maxOfOrNull { it.score }
        override suspend fun getHighestScore(userId: String): Int? = quizResults.maxOfOrNull { it.score }
        override suspend fun getAverageScore(userId: String): Double? = if (quizResults.isEmpty()) 0.0 else quizResults.map { it.score }.average()
        override suspend fun countDistinctQuizzesCompleted(userId: String): Int = quizResults.map { it.quizId }.distinct().size
        override suspend fun getCompletedQuizIds(userId: String): List<Int> = quizResults.map { it.quizId }.distinct()
        override suspend fun deleteQuizResults(userId: String) { quizResults.clear() }

        // Mocks vazios para as outras funções não testadas
        override fun observeLessonProgress(userId: String): Flow<List<LessonProgress>> = flowOf(emptyList())
        override suspend fun getLessonProgress(userId: String, lessonId: Int): LessonProgress? = null
        override suspend fun upsertLessonProgress(progress: LessonProgress) {}
        override suspend fun getCompletedLessonIds(userId: String): List<Int> = emptyList()
        override suspend fun countCompletedLessons(userId: String): Int = 0
        override suspend fun deleteLessonProgress(userId: String) {}

        override suspend fun deleteAllUserAnswers() {}
        override suspend fun deleteUserAchievements(userId: String) {}
    }

    @Test
    fun testEnsureUserExists() = runBlocking {
        val dao = FakeUserDao()
        val repository = UserRepository(dao)

        assertNull(dao.getUser(UserRepository.DEFAULT_USER_ID))

        repository.ensureUserExists()
        val user = dao.getUser(UserRepository.DEFAULT_USER_ID)
        
        assertNotNull(user)
        assertEquals(UserRepository.DEFAULT_USER_ID, user?.id)
    }

    @Test
    fun testUpdateUserName() = runBlocking {
        val dao = FakeUserDao()
        val repository = UserRepository(dao)

        repository.ensureUserExists()
        repository.updateUserName("Maria Silva")

        val user = dao.getUser(UserRepository.DEFAULT_USER_ID)
        assertEquals("Maria Silva", user?.name)
    }

    @Test
    fun testAddBonusPoints() = runBlocking {
        val dao = FakeUserDao()
        val repository = UserRepository(dao)

        repository.ensureUserExists()
        repository.addBonusPoints(50)

        val user = dao.getUser(UserRepository.DEFAULT_USER_ID)
        assertEquals(50, user?.totalPoints)
    }

    @Test
    fun testClearAllProgress() = runBlocking {
        val dao = FakeUserDao()
        val repository = UserRepository(dao)

        repository.ensureUserExists()
        repository.updateUserName("João")
        
        // Remove tudo
        repository.clearAllProgress()

        assertNull(dao.getUser(UserRepository.DEFAULT_USER_ID))
    }
}
