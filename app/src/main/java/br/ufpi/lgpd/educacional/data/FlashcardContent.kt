package br.ufpi.lgpd.educacional.data

/**
 * Conteúdo dos flashcards de estudo sobre a LGPD.
 * Cada cartão relaciona um conceito à sua definição.
 */
object FlashcardContent {

    data class Flashcard(
        val term: String,
        val definition: String,
        val category: String
    )

    val categories: List<String> = listOf(
        "Fundamentos", "Direitos", "Segurança", "Conformidade"
    )

    val cards: List<Flashcard> = listOf(
        // ── Fundamentos ──
        Flashcard(
            "Dados Pessoais",
            "Informação referente a pessoa natural identificada ou identificável.",
            "Fundamentos"
        ),
        Flashcard(
            "Tratamento",
            "Toda operação realizada com dados pessoais: coleta, classificação, utilização, acesso, transmissão ou armazenamento.",
            "Fundamentos"
        ),
        Flashcard(
            "Titular",
            "Pessoa natural a quem os dados pessoais se referem.",
            "Fundamentos"
        ),
        Flashcard(
            "Controlador",
            "Pessoa ou empresa que decide sobre o tratamento dos dados pessoais.",
            "Fundamentos"
        ),
        Flashcard(
            "Operador",
            "Quem trata os dados pessoais em nome do controlador.",
            "Fundamentos"
        ),
        Flashcard(
            "Encarregado (DPO)",
            "Canal de comunicação entre o controlador, os titulares e a ANPD.",
            "Fundamentos"
        ),
        Flashcard(
            "ANPD",
            "Autoridade Nacional de Proteção de Dados: órgão que fiscaliza e regulamenta a LGPD.",
            "Fundamentos"
        ),
        Flashcard(
            "Dados Sensíveis",
            "Dados sobre origem racial, convicção religiosa, saúde, biometria e outros que exigem proteção especial.",
            "Fundamentos"
        ),
        Flashcard(
            "Anonimização",
            "Transformação dos dados para que não seja mais possível identificar a pessoa.",
            "Fundamentos"
        ),
        Flashcard(
            "Finalidade",
            "Princípio: o tratamento deve ter propósito legítimo, específico e explícito.",
            "Fundamentos"
        ),

        // ── Direitos ──
        Flashcard(
            "Consentimento",
            "Autorização livre, informada e inequívoca do titular para o tratamento dos dados.",
            "Direitos"
        ),
        Flashcard(
            "Acesso aos Dados",
            "Direito do titular de consultar, de forma facilitada e gratuita, os dados tratados sobre ele.",
            "Direitos"
        ),
        Flashcard(
            "Correção",
            "Direito de corrigir dados incompletos, inexatos ou desatualizados.",
            "Direitos"
        ),
        Flashcard(
            "Portabilidade",
            "Direito de solicitar a transferência dos seus dados para outro fornecedor.",
            "Direitos"
        ),
        Flashcard(
            "Eliminação",
            "Direito de pedir a exclusão dos dados tratados com base no consentimento.",
            "Direitos"
        ),
        Flashcard(
            "Revisão Automatizada",
            "Direito de revisar decisões tomadas exclusivamente de forma automatizada.",
            "Direitos"
        ),
        Flashcard(
            "Revogação",
            "O titular pode retirar o consentimento a qualquer momento, por procedimento gratuito.",
            "Direitos"
        ),
        Flashcard(
            "Informationagem",
            "Direito de ser informado sobre com quem os dados foram compartilhados.",
            "Direitos"
        ),

        // ── Segurança ──
        Flashcard(
            "Incidente de Segurança",
            "Evento que compromete a segurança dos dados, como vazamento ou acesso não autorizado.",
            "Segurança"
        ),
        Flashcard(
            "Criptografia",
            "Técnica que protege os dados transformando-os em código ilegível sem a chave correta.",
            "Segurança"
        ),
        Flashcard(
            "Pseudonimização",
            "Tratamento que reduz a identificação do titular sem depender de informações separadas.",
            "Segurança"
        ),
        Flashcard(
            "Data Mapping",
            "Mapeamento detalhado do ciclo de vida dos dados dentro da organização.",
            "Segurança"
        ),
        Flashcard(
            "Privacy by Design",
            "Privacidade desde a concepção: incorporar a proteção de dados desde o início do projeto.",
            "Segurança"
        ),
        Flashcard(
            "Plano de Resposta",
            "Plano de ação (PRI) para lidar com incidentes de segurança.",
            "Segurança"
        ),

        // ── Conformidade ──
        Flashcard(
            "Accountability",
            "Prova: documentar as medidas de conformidade para demonstrá-las à ANPD.",
            "Conformidade"
        ),
        Flashcard(
            "Relatório de Impacto",
            "RIPD: documento que descreve os riscos e as medidas de proteção do tratamento.",
            "Conformidade"
        ),
        Flashcard(
            "Bases Legais",
            "As 10 hipóteses do Art. 7º que autorizam o tratamento de dados pessoais.",
            "Conformidade"
        ),
        Flashcard(
            "Sanções",
            "Advertência, multa de até 2% do faturamento (limitada a R$ 50 milhões) e bloqueio dos dados.",
            "Conformidade"
        ),
        Flashcard(
            "Registro de Operações",
            "Relatório que documenta todas as operações de tratamento realizadas.",
            "Conformidade"
        ),
        Flashcard(
            "LIA",
            "Legitimate Interest Assessment: teste do interesse legítimo como base legal.",
            "Conformidade"
        ),
        Flashcard(
            "Bloqueio",
            "Suspensão temporária de qualquer operação de tratamento.",
            "Conformidade"
        ),
        Flashcard(
            "Pequeno Porte",
            "Empresas de pequeno porte têm regras flexibilizadas de conformidade.",
            "Conformidade"
        )
    )

    /** Retorna os cartões filtrados por categoria. */
    fun cardsByCategory(category: String): List<Flashcard> =
        if (category == "Todos") cards else cards.filter { it.category == category }
}
