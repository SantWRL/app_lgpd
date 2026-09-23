package br.ufpi.lgpd.educacional.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlashcardContentTest {

    @Test
    fun `deck has cards`() {
        assertTrue(FlashcardContent.cards.isNotEmpty())
    }

    @Test
    fun `every card has term, definition and valid category`() {
        FlashcardContent.cards.forEach { card ->
            assertTrue("Termo vazio no cartão: ${card.term}", card.term.isNotBlank())
            assertTrue(
                "Definição muito curta no cartão: ${card.term}",
                card.definition.length >= 20
            )
            assertTrue(
                "Categoria inválida '${card.category}' no cartão ${card.term}",
                card.category in FlashcardContent.categories
            )
        }
    }

    @Test
    fun `terms are unique`() {
        val terms = FlashcardContent.cards.map { it.term }
        assertEquals(terms.size, terms.distinct().size)
    }

    @Test
    fun `filter by category returns only that category`() {
        val fundamentos = FlashcardContent.cardsByCategory("Fundamentos")
        assertTrue(fundamentos.isNotEmpty())
        assertTrue(fundamentos.all { it.category == "Fundamentos" })
    }

    @Test
    fun `filter by All returns every card`() {
        assertEquals(FlashcardContent.cards.size, FlashcardContent.cardsByCategory("Todos").size)
    }

    @Test
    fun `every declared category has at least one card`() {
        FlashcardContent.categories.forEach { category ->
            assertTrue(
                "Categoria sem cartões: $category",
                FlashcardContent.cardsByCategory(category).isNotEmpty()
            )
        }
    }

    @Test
    fun `sum of categories equals total cards`() {
        val total = FlashcardContent.categories.sumOf {
            FlashcardContent.cardsByCategory(it).size
        }
        assertEquals(FlashcardContent.cards.size, total)
    }
}
