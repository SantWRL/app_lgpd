package br.ufpi.lgpd.educacional.data

import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.data.model.Quiz

object LgpdContent {
    val lessons = listOf(
        Lesson(
            1,
            "Introdução à LGPD",
            "Entenda objetivo, alcance e conceitos centrais da lei.",
            "A Lei Geral de Proteção de Dados (Lei nº 13.709/2018) surgiu para regulamentar o tratamento de dados pessoais no Brasil, tanto em meios físicos quanto digitais.\n\n" +
                "O principal objetivo da lei não é impedir o uso dos dados, mas sim criar um ambiente de segurança jurídica, promovendo o desenvolvimento econômico e tecnológico ao mesmo tempo em que protege os direitos fundamentais de liberdade e de privacidade dos cidadãos.\n\n" +
                "Ela se aplica a qualquer operação de tratamento realizada por pessoa natural ou jurídica, seja de direito público ou privado, independentemente de onde o titular esteja localizado, desde que os dados tenham sido coletados no Brasil ou o tratamento seja realizado no país.",
            "Fundamentos",
            1,
            12,
            "BEGINNER",
            videoId = "n3e0HVcNml0"
        ),
        Lesson(
            2,
            "Dados pessoais e sensíveis",
            "Aprenda a diferenciar dado comum, sensível, anonimizado e pseudonimizado.",
            "Na LGPD, é essencial diferenciar os tipos de dados:\n\n" +
                "**Dado Pessoal:** É qualquer informação que identifique ou possa identificar uma pessoa natural. Exemplos: nome, RG, CPF, e-mail, IP do computador, placa do carro e endereço.\n\n" +
                "**Dado Pessoal Sensível:** É uma categoria especial de dados pessoais que pode gerar discriminação se vazada ou mal utilizada. Engloba origem racial ou étnica, convicção religiosa, opinião política, filiação a sindicato, dados referentes à saúde ou à vida sexual, dado genético ou biométrico.\n\n" +
                "**Dado Anonimizado:** É um dado relativo a titular que não possa ser identificado, considerando a utilização de meios técnicos razoáveis disponíveis na época. Dados anonimizados não são considerados dados pessoais para os fins da LGPD.\n\n" +
                "**Dado Pseudonimizado:** É o dado que perde a possibilidade de associação direta a um indivíduo, a não ser pelo uso de informação adicional mantida separadamente em ambiente seguro. Diferentemente do dado anonimizado, a pseudonimização ainda é considerada tratamento de dados sujeito à LGPD.",
            "Fundamentos",
            2,
            18,
            "BEGINNER",
            videoId = "h-z9Y3V_R_M"
        ),
        Lesson(
            3,
            "10 princípios da LGPD",
            "Veja como finalidade, necessidade e transparência guiam o tratamento.",
            "Os 10 princípios funcionam como o coração da lei. O tratamento de dados só é regular se seguir essas diretrizes:\n\n" +
                "1. **Finalidade:** O propósito deve ser legítimo, específico e explícito.\n" +
                "2. **Adequação:** O tratamento precisa ser compatível com as finalidades informadas.\n" +
                "3. **Necessidade:** O tratamento deve se limitar ao mínimo necessário para a realização da finalidade.\n" +
                "4. **Livre Acesso:** Garantia, aos titulares, de consulta facilitada e gratuita.\n" +
                "5. **Qualidade:** Garantia de exatidão, clareza e atualização dos dados.\n" +
                "6. **Transparência:** Informações claras e acessíveis sobre a realização do tratamento e os agentes envolvidos.\n" +
                "7. **Segurança:** Uso de medidas técnicas e administrativas para proteger os dados.\n" +
                "8. **Prevenção:** Adoção de medidas preventivas contra danos.\n" +
                "9. **Não Discriminação:** Impossibilidade de tratamento para fins discriminatórios ilícitos ou abusivos.\n" +
                "10. **Responsabilização e Prestação de Contas:** Demonstração da adoção de medidas eficazes para cumprir a lei.",
            "Fundamentos",
            3,
            22,
            "BEGINNER",
            videoId = "Xm0PnyU0R90"
        ),
        Lesson(
            4,
            "Bases legais",
            "Conheça consentimento, obrigação legal, legítimo interesse e outras.",
            "Bases legais, ou hipóteses de tratamento, são as autorizações que a lei oferece para o uso de dados pessoais. Sem enquadrar o tratamento em uma das bases legais do artigo 7º, ele é ilegal.\n\n" +
                "As mais conhecidas incluem:\n" +
                "- **Consentimento:** manifestação livre, informada e inequívoca do titular.\n" +
                "- **Obrigação Legal:** quando a empresa precisa tratar o dado para cumprir uma lei.\n" +
                "- **Execução de Contrato:** para prestar o serviço que o cliente contratou.\n" +
                "- **Legítimo Interesse:** quando há apoio a atividades do controlador, de forma balanceada e sem violar direitos do titular.\n" +
                "- **Proteção da Vida:** proteção da vida ou da integridade física.\n" +
                "- **Exercício Regular de Direitos:** em processos judiciais, administrativos ou arbitrais.",
            "Conformidade",
            4,
            24,
            "INTERMEDIATE"
        ),
        Lesson(
            5,
            "Direitos dos titulares",
            "Saiba como exercer acesso, correção, eliminação e portabilidade.",
            "Os titulares possuem papel central e podem requerer, a qualquer momento e de forma gratuita, direitos como:\n\n" +
                "- Confirmação de que o tratamento existe.\n" +
                "- Acesso completo aos dados tratados.\n" +
                "- Correção de dados incompletos, inexatos ou desatualizados.\n" +
                "- Anonimização, bloqueio ou eliminação de dados desnecessários ou excessivos.\n" +
                "- Portabilidade para outro fornecedor.\n" +
                "- Informação sobre as entidades com as quais os dados foram compartilhados.\n" +
                "- Informação sobre a possibilidade de não fornecer consentimento e sobre as consequências da negativa.\n" +
                "- Revogação do consentimento.",
            "Direitos",
            5,
            24,
            "INTERMEDIATE"
        ),
        Lesson(
            6,
            "Atores da LGPD",
            "Entenda titular, controlador, operador, encarregado e ANPD.",
            "No ecossistema da LGPD, os atores possuem responsabilidades bem definidas:\n\n" +
                "**1. Titular:** Você, a pessoa natural a quem os dados pertencem.\n" +
                "**2. Controlador:** pessoa ou organização, pública ou privada, responsável pelas decisões sobre o tratamento de dados. Exemplo: a loja em que você comprou ou a universidade onde você estuda.\n" +
                "**3. Operador:** quem realiza o tratamento em nome do controlador. Exemplo: o provedor de nuvem contratado pela universidade.\n" +
                "**4. Encarregado (DPO):** pessoa indicada para atuar como canal de comunicação entre controlador, titulares e ANPD.\n" +
                "**5. ANPD:** Autoridade Nacional de Proteção de Dados, órgão responsável por zelar, fiscalizar, orientar e aplicar sanções relacionadas à LGPD no Brasil.",
            "Atores",
            6,
            18,
            "INTERMEDIATE"
        ),
        Lesson(
            7,
            "Segurança e prevenção",
            "Aprenda medidas técnicas e administrativas para reduzir riscos.",
            "Tratar dados de forma lícita não basta se eles forem roubados ou vazarem. A LGPD exige medidas de segurança rigorosas.\n\n" +
                "Boas práticas de segurança da informação incluem:\n" +
                "- **Medidas técnicas:** criptografia de discos, bancos de dados e tráfego; backups regulares e testados; antivírus; firewall e autenticação multifator.\n" +
                "- **Medidas administrativas:** políticas de senhas; controles de acesso restritos pelo princípio do menor privilégio; acordos de confidencialidade; e treinamento contínuo dos colaboradores.",
            "Segurança",
            7,
            28,
            "INTERMEDIATE"
        ),
        Lesson(
            8,
            "Incidentes de segurança",
            "Veja como reconhecer, registrar e comunicar vazamentos.",
            "Um incidente de segurança não é apenas o roubo de dados por hackers. Pode envolver:\n" +
                "- Um pen drive perdido com planilhas de pacientes.\n" +
                "- Um e-mail enviado incorretamente, com destinatários em cópia aberta.\n" +
                "- Um colaborador que apagou um banco de dados por acidente, sem backup.\n\n" +
                "A LGPD determina que o controlador deve comunicar a ANPD e os titulares quando ocorrer incidente que possa acarretar risco ou dano relevante, em prazo razoável conforme a regulamentação aplicável.",
            "Segurança",
            8,
            22,
            "ADVANCED"
        ),
        Lesson(
            9,
            "LGPD no contexto acadêmico",
            "A lei em matrícula, pesquisa, eventos e sistemas universitários.",
            "Universidades tratam um volume muito alto de dados diariamente, desde registros de matrícula, avaliações e informações financeiras até dados sensíveis em pesquisas científicas.\n\n" +
                "- **Na pesquisa:** a LGPD permite o tratamento de dados para fins de pesquisa, preferencialmente com anonimização. Ainda assim, princípios éticos devem ser respeitados.\n" +
                "- **No cotidiano:** listas de presença e notas não devem expor dados desnecessariamente. O ideal é disponibilizar resultados em ambiente restrito e seguro.\n" +
                "- **Nos sistemas universitários:** o acesso deve ser hierarquizado. Um aluno monitor, por exemplo, não deve ter acesso ao sistema financeiro da instituição.",
            "Aplicação",
            9,
            25,
            "ADVANCED"
        ),
        Lesson(
            10,
            "Checklist de conformidade",
            "Monte um plano prático para revisar processos.",
            "Se você precisar aplicar a LGPD em um negócio, siga estes passos primordiais:\n\n" +
                "1. **Mapeamento de Dados:** descubra e catalogue onde a organização guarda dados, como planilhas, papéis, ERPs e sistemas web.\n" +
                "2. **Definição das Bases Legais:** vincule cada dado coletado a uma base legal. Se não houver justificativa, exclua o dado.\n" +
                "3. **Transparência:** elabore ou atualize a Política de Privacidade informando como os dados são usados e com quem são compartilhados.\n" +
                "4. **Canal de Atendimento:** tenha um meio claro, como um e-mail dedicado, para que os titulares exerçam seus direitos.\n" +
                "5. **Segurança:** atualize a infraestrutura cibernética e treine funcionários.",
            "Aplicação",
            10,
            20,
            "ADVANCED"
        )
    )

    val quizzes = listOf(
        Quiz(1, "Fundamentos da LGPD", "Conceitos essenciais, objetivo da lei e tipos de dados.", "Fundamentos", "BEGINNER", 8),
        Quiz(2, "Princípios e bases legais", "Finalidade, necessidade, transparência e hipóteses de tratamento.", "Conformidade", "BEGINNER", 10),
        Quiz(3, "Direitos dos titulares", "Pedidos de acesso, correção, eliminação, portabilidade e revisão.", "Direitos", "INTERMEDIATE", 10),
        Quiz(4, "Atores e responsabilidades", "Titular, controlador, operador, encarregado e ANPD.", "Atores", "INTERMEDIATE", 8),
        Quiz(5, "Segurança e incidentes", "Boas práticas, prevenção, resposta e comunicação de incidentes.", "Segurança", "INTERMEDIATE", 12),
        Quiz(6, "LGPD na universidade", "Situações acadêmicas, pesquisa, matrícula e eventos.", "Aplicação", "ADVANCED", 12),
        Quiz(7, "Revisão geral", "Um simulado com temas essenciais para fixação.", "Revisão", "ADVANCED", 20)
    )

    val quizQuestions = mapOf(
        1 to listOf(
            QuizQuestionContent("Qual é o principal objetivo da LGPD?", listOf("Eliminar o uso de dados pessoais", "Proteger direitos fundamentais de liberdade e privacidade", "Permitir venda livre de bases de dados", "Substituir todas as políticas internas"), 1, "A LGPD regula o tratamento de dados pessoais para proteger liberdade, privacidade e desenvolvimento da personalidade."),
            QuizQuestionContent("Um dado pessoal é uma informação que:", listOf("Sempre precisa ser secreta", "Identifica ou pode identificar uma pessoa natural", "Só existe em sistemas digitais", "Pertence apenas a empresas"), 1, "Dado pessoal é qualquer informação relacionada a pessoa natural identificada ou identificável."),
            QuizQuestionContent("Qual exemplo representa dado pessoal sensível?", listOf("Nome completo", "E-mail institucional", "Dado de saúde", "Número de matrícula"), 2, "Dados de saúde são sensíveis e exigem proteção reforçada."),
            QuizQuestionContent("A LGPD se aplica a:", listOf("Somente empresas privadas", "Somente aplicativos móveis", "Tratamento de dados em meios físicos ou digitais", "Apenas dados financeiros"), 2, "A lei alcança o tratamento de dados pessoais em meios físicos e digitais, no setor público e privado.")
        ),
        2 to listOf(
            QuizQuestionContent("O princípio da necessidade orienta a organização a:", listOf("Coletar todos os dados possíveis", "Usar apenas dados necessários à finalidade", "Guardar dados para sempre", "Pedir consentimento para tudo"), 1, "A necessidade limita o tratamento ao mínimo necessário para cumprir a finalidade."),
            QuizQuestionContent("Consentimento válido deve ser:", listOf("Livre, informado e inequívoco", "Automático e permanente", "Presumido quando o usuário navega", "Obrigatório em qualquer tratamento"), 0, "Quando usado, o consentimento precisa ser livre, informado e inequívoco."),
            QuizQuestionContent("Qual alternativa é uma base legal da LGPD?", listOf("Curiosidade institucional", "Obrigação legal ou regulatória", "Facilidade operacional", "Interesse sem justificativa"), 1, "Obrigação legal ou regulatória é uma das hipóteses legais para o tratamento."),
            QuizQuestionContent("Transparência significa:", listOf("Explicar finalidades e formas de tratamento", "Exibir código-fonte do sistema", "Evitar qualquer coleta", "Publicar dados pessoais"), 0, "O titular deve receber informações claras, precisas e acessíveis.")
        ),
        3 to listOf(
            QuizQuestionContent("Qual é um direito do titular?", listOf("Impedir toda obrigação legal", "Acessar dados pessoais tratados", "Apagar registros públicos obrigatórios sempre", "Exigir senha de outro usuário"), 1, "O acesso aos dados é um direito previsto para o titular."),
            QuizQuestionContent("Se um dado estiver incorreto, o titular pode pedir:", listOf("Correção", "Venda", "Duplicação", "Bloqueio da ANPD"), 0, "A correção de dados incompletos, inexatos ou desatualizados é um direito do titular."),
            QuizQuestionContent("A revogação do consentimento permite:", listOf("Cancelar consentimento antes dado", "Apagar qualquer lei", "Transferir obrigação ao titular", "Autorizar compartilhamento automático"), 0, "O consentimento pode ser revogado mediante manifestação do titular."),
            QuizQuestionContent("Decisões automatizadas podem ser:", listOf("Revisadas quando afetarem interesses do titular", "Sempre secretas", "Usadas sem critério", "Proibidas em todos os casos"), 0, "A LGPD prevê revisão de decisões tomadas unicamente com base em tratamento automatizado em certas situações.")
        ),
        4 to listOf(
            QuizQuestionContent("Quem decide a finalidade e os meios do tratamento?", listOf("Operador", "Controlador", "Titular", "Usuário visitante"), 1, "O controlador toma as principais decisões sobre o tratamento."),
            QuizQuestionContent("O operador atua:", listOf("Em nome do controlador", "Como titular dos dados", "Sempre como ANPD", "Sem seguir instruções"), 0, "O operador realiza tratamento conforme instruções do controlador."),
            QuizQuestionContent("O encarregado tem papel de:", listOf("Ponte entre controlador, titulares e ANPD", "Excluir todos os dados", "Criar multas", "Substituir o titular"), 0, "O encarregado atua como canal de comunicação em temas de proteção de dados."),
            QuizQuestionContent("A ANPD é responsável por:", listOf("Orientar e fiscalizar a aplicação da LGPD", "Guardar senhas dos usuários", "Autorizar todo login", "Coletar dados de alunos"), 0, "A Autoridade Nacional de Proteção de Dados orienta, fiscaliza e pode aplicar sanções.")
        ),
        5 to listOf(
            QuizQuestionContent("Qual prática melhora a segurança de dados?", listOf("Compartilhar senha da equipe", "Usar mínimo privilégio", "Enviar planilhas abertas por grupos", "Guardar documentos sem controle"), 1, "O princípio do mínimo privilégio reduz acessos indevidos."),
            QuizQuestionContent("Um incidente de segurança pode envolver:", listOf("Acesso não autorizado a dados pessoais", "Troca de tema visual", "Atualização de ícone", "Apenas erro de digitação"), 0, "Acesso indevido, perda ou divulgação não autorizada podem caracterizar incidente."),
            QuizQuestionContent("Ao suspeitar de vazamento, a primeira postura adequada é:", listOf("Ignorar se ninguém perguntou", "Conter, registrar e avaliar riscos", "Publicar os dados", "Apagar evidências"), 1, "A resposta deve conter danos, documentar fatos e avaliar riscos aos titulares."),
            QuizQuestionContent("Backup ajuda porque:", listOf("Substitui transparência", "Permite recuperar dados em falhas", "Dispensa controle de acesso", "Remove obrigação legal"), 1, "Backups bem geridos apoiam a continuidade e a recuperação.")
        ),
        6 to listOf(
            QuizQuestionContent("Em pesquisa acadêmica, é importante:", listOf("Coletar mais dados por garantia", "Informar finalidade e proteger participantes", "Divulgar respostas individuais", "Ignorar dados sensíveis"), 1, "Projetos acadêmicos devem informar finalidades e proteger participantes."),
            QuizQuestionContent("Lista de presença em evento deve conter:", listOf("Apenas dados necessários à finalidade", "Dados de saúde sem motivo", "Senha dos participantes", "Informações familiares completas"), 0, "A coleta deve respeitar necessidade e finalidade."),
            QuizQuestionContent("Dados de alunos devem ser acessados:", listOf("Por qualquer pessoa curiosa", "Somente por quem precisa para a atividade", "Sempre publicamente", "Sem registro de acesso"), 1, "Controle de acesso é medida básica de segurança."),
            QuizQuestionContent("Ao usar formulário online, a instituição deve:", listOf("Explicar o uso dos dados coletados", "Pedir documentos sem finalidade", "Compartilhar respostas sem aviso", "Desativar toda segurança"), 0, "Transparência sobre finalidade e tratamento é essencial.")
        )
    )

    fun questionsForQuiz(quizId: Int): List<QuizQuestionContent> {
        return quizQuestions[quizId] ?: quizQuestions.values.flatten().take(8)
    }
}

data class QuizQuestionContent(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String
)
