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
            videoId = "OIPqTk0fQGs"
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
            "INTERMEDIATE",
            videoId = "QumrhZd61l4"
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
            "INTERMEDIATE",
            videoId = "EKpwHMVahwE"
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
            "INTERMEDIATE",
            videoId = "UAdmZB04IYk"
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
            "INTERMEDIATE",
            videoId = "CxoFFnBVrJg"
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
            "ADVANCED",
            videoId = "81D99me8A1Q"
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
            "ADVANCED",
            videoId = "6IuzaDstu9g"
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
            "ADVANCED",
            videoId = "fWSqXoIrtP4"
        ),
        Lesson(
            11,
            "Estatuto Digital da Criança e do Adolescente (Lei 15.211/2025)",
            "Conheça a proteção de crianças e adolescentes em ambientes digitais.",
            "A Lei nº 15.211, de 17 de setembro de 2025, instituiu o **Estatuto Digital da Criança e do Adolescente (e-CAD)**, estabelecendo regras para proteção de crianças e adolescentes em ambientes digitais.\n\n" +
                "**O que é?**\n" +
                "A lei dispõe sobre a proteção de crianças e adolescentes em ambientes digitais, aplicando-se a todo produto ou serviço de tecnologia da informação direcionado a esse público ou de acesso provável por eles.\n\n" +
                "**Principais pontos:**\n" +
                "- Aplica-se a apps, redes sociais, jogos eletrônicos e qualquer serviço digital com acesso provável por crianças/adolescentes.\n" +
                "- Define 'caixa de recompensa' (loot box) como funcionalidade que permite adquirir itens virtuais aleatórios mediante pagamento.\n" +
                "- Proíbe perfilamento de crianças e adolescentes para fins de publicidade comportamental.\n" +
                "- Exige mecanismos de supervisão parental integrados aos produtos e serviços.\n" +
                "- Redes sociais devem garantir proteção prioritária, tendo como parâmetro o melhor interesse da criança.\n" +
                "- Proíbe a monetização de conteúdo gerado por crianças sem autorização dos pais.\n" +
                "- Estabelece a obrigação de verificação de idade para acesso a determinados conteúdos.\n" +
                "- Cria uma autoridade administrativa autônoma para fiscalizar o cumprimento da lei.\n\n" +
                "**Relação com a LGPD:**\n" +
                "O e-CAD complementa a LGPD (Lei nº 13.709/2018) ao tratar especificamente da proteção de dados de crianças e adolescentes, reforçando o tratamento de dados pessoais como medida de segurança e privacidade.",
            "Direitos",
            11,
            20,
            "INTERMEDIATE",
            videoId = "2Xp0QpRglD0"
        ),
        Lesson(
            12,
            "Aplicação prática do e-CAD na escola e em apps",
            "Como transformar a Lei 15.211/2025 em medidas concretas.",
            "A aplicação prática do e-CAD começa com o mapeamento de riscos para crianças e adolescentes em cada ambiente digital.\n\n" +
                "**Passo 1 - Mapeie riscos por fluxo:** identifique coleta de dados, uso de câmera/microfone, chat e geolocalização.\n" +
                "**Passo 2 - Defina controles por idade:** aplique proteção por padrão e experiências seguras.\n" +
                "**Passo 3 - Evite perfilamento comportamental:** bloqueie segmentação publicitária para menores.\n" +
                "**Passo 4 - Ative supervisão parental:** ofereça mecanismos claros para responsáveis.\n" +
                "**Passo 5 - Resposta a incidentes:** mantenha fluxo de contenção, registro e comunicação.\n\n" +
                "Base legal de referência: Lei nº 15.211/2025 (e-CAD) e Lei nº 13.709/2018 (LGPD).",
            "Aplicação",
            12,
            24,
            "ADVANCED",
            videoId = "2Xp0QpRglD0"
        ),
        Lesson(
            13,
            "ISO 27001 (SGSI)",
            "Entenda o sistema de gestão de segurança da informação.",
            "A ISO/IEC 27001 define requisitos para implementar, manter e melhorar continuamente um SGSI.\n\n" +
                "Pontos-chave:\n" +
                "- Contexto da organização e escopo do SGSI.\n" +
                "- Liderança, papéis e responsabilidades.\n" +
                "- Avaliação e tratamento de riscos.\n" +
                "- Controles de segurança e melhoria contínua.\n\n" +
                "No contexto da LGPD, a ISO 27001 ajuda a estruturar governança e evidências de conformidade em segurança.",
            "Conformidade",
            13,
            22,
            "INTERMEDIATE",
            videoId = "QumrhZd61l4"
        ),
        Lesson(
            14,
            "ISO 27701 (Privacidade)",
            "Amplie o SGSI para gestão de informações de privacidade.",
            "A ISO/IEC 27701 complementa a ISO 27001 com controles e requisitos para informações pessoais.\n\n" +
                "Você aprende:\n" +
                "- Papéis de controlador e operador.\n" +
                "- Requisitos para tratamento lícito e transparente.\n" +
                "- Gestão de solicitações de titulares.\n" +
                "- Evidências para auditoria de privacidade.\n\n" +
                "É uma ponte prática entre governança de segurança e obrigações de privacidade da LGPD.",
            "Conformidade",
            14,
            20,
            "INTERMEDIATE",
            videoId = "EKpwHMVahwE"
        ),
        Lesson(
            15,
            "ISO 27002 (Controles)",
            "Conheça controles de referência para proteger informação.",
            "A ISO/IEC 27002 traz um catálogo de controles organizacionais, físicos e tecnológicos.\n\n" +
                "Exemplos de controles:\n" +
                "- Gestão de acessos e privilégio mínimo.\n" +
                "- Criptografia e proteção de dados em trânsito e repouso.\n" +
                "- Segurança em desenvolvimento e fornecedores.\n" +
                "- Monitoramento e resposta a incidentes.\n\n" +
                "Na prática, ela orienta como implementar medidas que sustentam princípios de segurança e prevenção da LGPD.",
            "Segurança",
            15,
            24,
            "ADVANCED",
            videoId = "CxoFFnBVrJg"
        ),
        Lesson(
            16,
            "ISO 27005 (Gestão de Riscos)",
            "Aplique um método estruturado para riscos de segurança.",
            "A ISO/IEC 27005 foca na gestão de riscos de segurança da informação.\n\n" +
                "Etapas principais:\n" +
                "- Identificação de ativos, ameaças e vulnerabilidades.\n" +
                "- Análise de probabilidade e impacto.\n" +
                "- Avaliação e priorização de riscos.\n" +
                "- Definição de tratamento e aceitação de risco residual.\n\n" +
                "Ela fortalece decisões de compliance e continuidade operacional, alinhando segurança com risco de negócio.",
            "Segurança",
            16,
            18,
            "ADVANCED",
            videoId = "81D99me8A1Q"
        ),
        Lesson(
            17,
            "ISO 29100 (Framework de Privacidade)",
            "Use princípios de privacidade para orientar decisões de produto.",
            "A ISO/IEC 29100 define um framework de privacidade com princípios e terminologia para dados pessoais.\n\n" +
                "Princípios abordados:\n" +
                "- Consentimento e escolha.\n" +
                "- Limitação de coleta e minimização.\n" +
                "- Uso, retenção e descarte adequados.\n" +
                "- Transparência, responsabilização e segurança.\n\n" +
                "É útil para design de soluções digitais com privacidade desde a concepção.",
            "Direitos",
            17,
            16,
            "INTERMEDIATE",
            videoId = "EKpwHMVahwE"
        )
    )

    val quizzes = listOf(
        Quiz(1, "Fundamentos da LGPD", "Conceitos essenciais, objetivo da lei e tipos de dados.", "Fundamentos", "BEGINNER", 8),
        Quiz(2, "Princípios e bases legais", "Finalidade, necessidade, transparência e hipóteses de tratamento.", "Conformidade", "BEGINNER", 10),
        Quiz(3, "Direitos dos titulares", "Pedidos de acesso, correção, eliminação, portabilidade e revisão.", "Direitos", "INTERMEDIATE", 10),
        Quiz(4, "Atores e responsabilidades", "Titular, controlador, operador, encarregado e ANPD.", "Atores", "INTERMEDIATE", 8),
        Quiz(5, "Segurança e incidentes", "Boas práticas, prevenção, resposta e comunicação de incidentes.", "Segurança", "INTERMEDIATE", 12),
        Quiz(6, "LGPD na universidade", "Situações acadêmicas, pesquisa, matrícula e eventos.", "Aplicação", "ADVANCED", 12),
        Quiz(7, "Revisão geral", "Um simulado com temas essenciais para fixação.", "Revisão", "ADVANCED", 20),
        Quiz(8, "Estatuto Digital (e-CAD)", "Proteção de crianças e adolescentes em ambientes digitais - Lei 15.211/2025.", "Direitos", "INTERMEDIATE", 10),
        Quiz(9, "e-CAD na prática", "Cenários reais de aplicação da Lei 15.211/2025 em escola e plataformas.", "Aplicação", "ADVANCED", 10),
        Quiz(10, "ISO na prática", "Questões sobre ISO 27001, 27701, 27002, 27005 e 29100.", "Conformidade", "ADVANCED", 10)
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
        ),
        7 to listOf(
            QuizQuestionContent("Qual o número da Lei Geral de Proteção de Dados no Brasil?", listOf("Lei nº 12.965/2014", "Lei nº 13.709/2018", "Lei nº 13.853/2019", "Lei nº 14.129/2021"), 1, "A LGPD é a Lei nº 13.709, promulgada em 14 de agosto de 2018."),
            QuizQuestionContent("Segundo a LGPD, dado pessoal é:", listOf("Apenas dados financeiros", "Informação relacionada a pessoa natural identificada ou identificável", "Todo conteúdo publicado na internet", "Dados de empresas e organizações"), 1, "Dado pessoal é toda informação que identifica ou torna identificável uma pessoa natural."),
            QuizQuestionContent("Qual princípio determina que o tratamento deve ser limitado ao mínimo necessário?", listOf("Transparência", "Necessidade", "Livre Acesso", "Qualidade"), 1, "O princípio da necessidade limita o tratamento ao mínimo necessário para atingir a finalidade."),
            QuizQuestionContent("O consentimento do titular deve ser:", listOf("Presumido", "Livre, informado e inequívoco", "Válido apenas por escrito", "Irrevogável"), 1, "O consentimento deve ser uma manifestação livre, informada e inequívoca pela qual o titular concorda com o tratamento."),
            QuizQuestionContent("Controlador é:", listOf("Quem fiscaliza a ANPD", "Quem decide sobre o tratamento de dados", "O titular dos dados", "O advogado da empresa"), 1, "O controlador é a pessoa natural ou jurídica que toma as decisões sobre o tratamento de dados pessoais."),
            QuizQuestionContent("Qual destes é um dado pessoal sensível?", listOf("Nome completo", "Endereço residencial", "Dado biométrico", "Número de telefone"), 2, "Dados biométricos são considerados sensíveis por poderem gerar discriminação."),
            QuizQuestionContent("O titular pode solicitar a eliminação de seus dados:", listOf("Em qualquer hipótese", "Quando os dados forem desnecessários ou excessivos", "Apenas mediante pagamento de taxa", "Somente após 5 anos"), 1, "A eliminação pode ser solicitada quando os dados são desnecessários, excessivos ou tratados em desconformidade com a lei."),
            QuizQuestionContent("A ANPD é responsável por:", listOf("Criar leis de proteção de dados", "Orientar, fiscalizar e aplicar sanções", "Julgar crimes cibernéticos", "Gerenciar dados do governo"), 1, "A ANPD orienta, fiscaliza e aplica sanções relacionadas ao cumprimento da LGPD."),
            QuizQuestionContent("O que é um incidente de segurança?", listOf("Apenas ataques hackers", "Qualquer evento que comprometa a segurança dos dados", "Troca de senha do sistema", "Atualização de software"), 1, "Incidente de segurança inclui acesso não autorizado, perda, roubo ou qualquer comprometimento de dados."),
            QuizQuestionContent("A portabilidade de dados permite:", listOf("Vender dados para terceiros", "Transferir dados para outro fornecedor", "Apagar dados da internet", "Criptografar informações"), 1, "A portabilidade permite ao titular solicitar a transferência de seus dados a outro fornecedor."),
            QuizQuestionContent("Dados anonimizados são considerados:", listOf("Dados pessoais comuns", "Não são considerados dados pessoais para a LGPD", "Dados sensíveis", "Sempre públicos"), 1, "Dados anonimizados não são considerados dados pessoais, pois não permitem identificar o titular."),
            QuizQuestionContent("Qual base legal permite tratar dados sem consentimento para cumprir uma lei?", listOf("Legítimo Interesse", "Obrigação Legal", "Execução de Contrato", "Consentimento"), 1, "A obrigação legal ou regulatória é base legal que dispensa o consentimento quando há dever legal de tratar os dados."),
            QuizQuestionContent("O encarregado (DPO) é:", listOf("O dono dos dados", "O canal entre controlador, titulares e ANPD", "O operador do sistema", "O advogado do titular"), 1, "O encarregado atua como canal de comunicação entre o controlador, os titulares e a ANPD."),
            QuizQuestionContent("A LGPD se aplica a:", listOf("Apenas empresas com mais de 100 funcionários", "Qualquer operação de tratamento, física ou digital", "Somente órgãos públicos", "Apenas empresas de tecnologia"), 1, "A LGPD se aplica a qualquer tratamento de dados pessoais, em meio físico ou digital, por pessoa natural ou jurídica."),
            QuizQuestionContent("O princípio da finalidade exige que:", listOf("Os dados sejam usados para qualquer propósito", "O propósito seja legítimo, específico e explícito", "Os dados sejam públicos", "O tratamento seja gratuito"), 1, "A finalidade deve ser legítima, específica, explícita e informada ao titular."),
            QuizQuestionContent("Em caso de vazamento de dados, o controlador deve:", listOf("Apenas registrar internamente", "Comunicar a ANPD e os titulares afetados", "Apagar todos os dados", "Pagar multa imediatamente"), 1, "O controlador deve comunicar a ANPD e os titulares sobre incidentes que possam causar risco ou dano relevante."),
            QuizQuestionContent("O prazo para comunicação de incidentes na LGPD:", listOf("24 horas", "Prazo razoável conforme regulamentação", "72 horas", "Não há prazo definido"), 1, "A LGPD determina que a comunicação deve ocorrer em prazo razoável, conforme definido pela regulamentação."),
            QuizQuestionContent("No contexto acadêmico, a LGPD:", listOf("Não se aplica a universidades", "Aplica-se a matrículas, pesquisas e sistemas", "Só se aplica a alunos maiores de idade", "Só vale para dados financeiros"), 1, "A LGPD se aplica a todas as operações de tratamento de dados pessoais em universidades."),
            QuizQuestionContent("A pseudonimização é:", listOf("O mesmo que anonimização", "Técnica que reduz a associação direta a um indivíduo, mas ainda é dado pessoal", "Eliminação completa dos dados", "Compartilhamento de dados"), 1, "Dados pseudonimizados perdem a associação direta, mas ainda são considerados dados pessoais."),
            QuizQuestionContent("O que NÃO é uma base legal da LGPD?", listOf("Consentimento", "Obrigação Legal", "Interesse Comercial Exclusivo", "Execução de Contrato"), 2, "'Interesse Comercial Exclusivo' não é uma base legal prevista na LGPD. O interesse deve ser legítimo e balanceado.")
        ),
        8 to listOf(
            QuizQuestionContent("A Lei nº 15.211/2025 institui:", listOf("O Marco Civil da Internet", "O Estatuto Digital da Criança e do Adolescente (e-CAD)", "A nova LGPD", "O Código de Defesa do Consumidor Digital"), 1, "A Lei 15.211/2025 criou o Estatuto Digital da Criança e do Adolescente (e-CAD), voltado à proteção de crianças e adolescentes em ambientes digitais."),
            QuizQuestionContent("O e-CAD se aplica a:", listOf("Apenas redes sociais", "Todo produto ou serviço digital com acesso provável por crianças/adolescentes", "Somente jogos eletrônicos", "Apenas aplicativos educacionais"), 1, "A lei abrange qualquer produto ou serviço de tecnologia da informação direcionado a crianças/adolescentes ou de acesso provável por eles."),
            QuizQuestionContent("'Caixa de recompensa' (loot box) é definida como:", listOf("Um prêmio por desempenho escolar", "Funcionalidade que permite adquirir itens virtuais aleatórios mediante pagamento", "Um sistema de avaliação de professores", "Um método de ensino digital"), 1, "Caixa de recompensa é funcionalidade em jogos que permite aquisição, mediante pagamento, de itens virtuais aleatórios sem conhecimento prévio do conteúdo."),
            QuizQuestionContent("O e-CAD proíbe:", listOf("O uso de internet por crianças", "O perfilamento de crianças para publicidade comportamental", "A criação de redes sociais", "O acesso a aplicativos educacionais"), 1, "A lei proíbe o perfilamento de crianças e adolescentes para fins de publicidade comportamental."),
            QuizQuestionContent("Produtos digitais devem ter:", listOf("Acesso ilimitado para crianças", "Mecanismos de supervisão parental", "Publicidade direcionada", "Sistemas de apostas"), 1, "O e-CAD exige mecanismos de supervisão parental integrados aos produtos e serviços digitais."),
            QuizQuestionContent("A monetização de conteúdo gerado por crianças:", listOf("É livre e irrestrita", "É proibida sem autorização dos pais", "É obrigatória", "Depende apenas da plataforma"), 1, "A lei proíbe a monetização de conteúdo gerado por crianças sem autorização dos pais ou responsáveis legais."),
            QuizQuestionContent("O princípio que deve guiar a proteção no e-CAD é:", listOf("O lucro da empresa", "A popularidade do serviço", "O melhor interesse da criança", "A liberdade total de expressão"), 2, "O melhor interesse da criança é o parâmetro que deve guiar a proteção prioritária nos ambientes digitais."),
            QuizQuestionContent("O e-CAD complementa qual legislação?", listOf("Código Civil", "LGPD (Lei nº 13.709/2018)", "CLT", "Código Tributário"), 1, "O e-CAD complementa a LGPD ao tratar especificamente da proteção de dados de crianças e adolescentes."),
            QuizQuestionContent("Quem fiscaliza o cumprimento do e-CAD?", listOf("A polícia federal", "Uma autoridade administrativa autônoma específica", "As próprias empresas", "O Ministério da Educação"), 1, "A lei cria uma autoridade administrativa autônoma responsável por zelar pela aplicação e fiscalizar o cumprimento do e-CAD."),
            QuizQuestionContent("Redes sociais devem garantir:", listOf("Liberdade total sem restrições", "Proteção prioritária de crianças e adolescentes", "Coleta irrestrita de dados", "Remoção de todos os perfis"), 1, "Redes sociais e demais serviços devem garantir proteção prioritária, tendo como parâmetro o melhor interesse da criança.")
        ),
        9 to listOf(
            QuizQuestionContent("Em um app escolar para menores, qual medida segue melhor o e-CAD?", listOf("Publicidade comportamental por histórico de uso", "Coleta mínima de dados e controles parentais", "Cadastro obrigatório com redes sociais", "Compartilhamento com parceiros sem aviso"), 1, "A lei prioriza proteção de menores, minimização de dados e supervisão parental."),
            QuizQuestionContent("Ao projetar um jogo com loot box para adolescentes, a conduta mais adequada é:", listOf("Ocultar chances dos itens", "Comprar com um clique sem confirmação", "Implementar controles, transparência e proteção por idade", "Vincular recompensa a dados sensíveis"), 2, "O e-CAD impõe salvaguardas e foco no melhor interesse da criança e do adolescente."),
            QuizQuestionContent("Qual prática viola diretamente o e-CAD?", listOf("Canal de contato para responsáveis", "Perfilamento para publicidade comportamental de menores", "Configuração de privacidade por padrão", "Política em linguagem simples"), 1, "A lei proíbe o perfilamento de crianças e adolescentes para publicidade comportamental."),
            QuizQuestionContent("Se houver incidente com dados de menores, a primeira ação recomendada é:", listOf("Ignorar até repercussão pública", "Conter, registrar e comunicar conforme risco", "Apagar logs para reduzir impacto", "Suspender todo o sistema sem análise"), 1, "A resposta adequada envolve contenção, rastreabilidade e comunicação responsável."),
            QuizQuestionContent("Qual combinação mostra alinhamento com e-CAD e LGPD?", listOf("Mais dados para personalizar anúncios", "Dark patterns para obter consentimento", "Verificação de idade proporcional e minimização de dados", "Compartilhar dados de menores por padrão"), 2, "A aplicação conjunta de e-CAD e LGPD prioriza proteção, necessidade e transparência."),
            QuizQuestionContent("Em plataforma educacional, o melhor interesse da criança exige:", listOf("Priorizar engajamento a qualquer custo", "Privacidade por padrão e segurança reforçada", "Acesso aberto a dados por todos os professores", "Coleta de geolocalização contínua sem finalidade"), 1, "O design deve proteger menores desde a configuração padrão do serviço."),
            QuizQuestionContent("Monetização de conteúdo gerado por criança sem autorização de responsável é:", listOf("Permitida se houver termo longo", "Permitida com opção de opt-out", "Vedada pelo e-CAD", "Obrigatória para plataformas"), 2, "A lei proíbe monetização sem autorização dos pais ou responsáveis legais."),
            QuizQuestionContent("Para uma escola contratar fornecedor digital conforme e-CAD, é essencial:", listOf("Aceitar contrato sem análise", "Avaliar cláusulas de proteção de menores e tratamento de dados", "Delegar tudo ao suporte técnico", "Desabilitar controles parentais"), 1, "A governança exige avaliação contratual e técnica focada em proteção de menores."),
            QuizQuestionContent("Qual opção reduz risco jurídico em app para adolescentes?", listOf("Política obscura e extensa", "Configurações abertas por padrão", "Registro de decisões de proteção e revisão periódica", "Segmentação comercial agressiva"), 2, "Prestação de contas e melhoria contínua reduzem riscos e fortalecem conformidade."),
            QuizQuestionContent("A referência legal correta para o Estatuto Digital da Criança e do Adolescente é:", listOf("Lei nº 12.965/2014", "Lei nº 13.709/2018", "Lei nº 15.211/2025", "Lei nº 14.129/2021"), 2, "O e-CAD foi instituído pela Lei nº 15.211/2025.")
        ),
        10 to listOf(
            QuizQuestionContent("Qual norma define requisitos para um SGSI?", listOf("ISO 29100", "ISO 27001", "ISO 27002", "ISO 27701"), 1, "A ISO/IEC 27001 estabelece requisitos de sistema de gestão de segurança da informação."),
            QuizQuestionContent("A ISO 27701 é principalmente voltada para:", listOf("Gestão de continuidade de negócios", "Gestão de privacidade e PII", "Criptografia de banco de dados", "Gestão de data center"), 1, "A ISO/IEC 27701 amplia a 27001 para gestão de informações de privacidade."),
            QuizQuestionContent("A ISO 27002 fornece:", listOf("Leis obrigatórias", "Catálogo de controles de segurança", "Regras fiscais", "Modelo de contrato padrão"), 1, "A ISO/IEC 27002 é guia de controles para tratamento de riscos."),
            QuizQuestionContent("A ISO 27005 trata de:", listOf("Design de interfaces", "Gestão de riscos de segurança da informação", "Assinatura digital", "Governança de TI financeira"), 1, "A ISO/IEC 27005 fornece abordagem estruturada para gestão de riscos."),
            QuizQuestionContent("Qual norma apresenta princípios e framework de privacidade?", listOf("ISO 27001", "ISO 29100", "ISO 27005", "ISO 19011"), 1, "A ISO/IEC 29100 traz princípios e estrutura de privacidade."),
            QuizQuestionContent("Na ISO 27001, a melhoria contínua ocorre por meio de:", listOf("PDCA e revisão periódica", "Ações únicas anuais", "Auditoria externa opcional sem plano", "Checklist sem indicadores"), 0, "O ciclo de melhoria contínua é parte central da norma."),
            QuizQuestionContent("Qual combinação está correta?", listOf("27001=controles e 27002=requisitos", "27001=requisitos e 27002=controles", "27005=privacidade e 27701=riscos", "29100=SGSI e 27001=framework"), 1, "A 27001 define requisitos de gestão; a 27002 detalha controles."),
            QuizQuestionContent("Um benefício direto da ISO 27701 para organizações é:", listOf("Eliminar toda obrigação legal", "Melhor governança de dados pessoais", "Dispensar consentimento", "Substituir políticas internas"), 1, "A norma melhora estrutura, papéis e evidências para privacidade."),
            QuizQuestionContent("Na gestão de riscos ISO 27005, após analisar riscos, o próximo passo é:", listOf("Ignorar riscos baixos", "Tratar e monitorar riscos", "Publicar dados", "Cancelar o SGSI"), 1, "Após análise e avaliação, a organização define tratamento e monitora."),
            QuizQuestionContent("Para estudos de privacidade by design, a referência mais adequada é:", listOf("ISO 29100", "ISO 22301", "ISO 9001", "ISO 14001"), 0, "A ISO/IEC 29100 orienta princípios de privacidade desde a concepção.")
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
