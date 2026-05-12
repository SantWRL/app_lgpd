# LGPD Educacional - Aplicativo Android Nativo

## 📱 Sobre o Projeto

**LGPD Educacional** é um aplicativo mobile educacional desenvolvido nativamente em Android (Kotlin) para capacitar usuários sobre a Lei Geral de Proteção de Dados (LGPD). O projeto foi concebido como parte de um Trabalho de Conclusão de Curso (TCC) na UFPI, com foco em formar usuários digitais conscientes sobre seus direitos e responsabilidades na proteção de dados pessoais.

Este aplicativo é uma migração evoluída da versão original em React Native, trazendo uma interface moderna baseada em **Glassmorphism**, performance otimizada e novos recursos de gamificação.

### Objetivo Principal

Desenvolver uma estrutura metodológica para formação de usuários digitais conscientes, materializada em uma aplicação educacional prática que articule conhecimento jurídico da LGPD com situações cotidianas do ambiente universitário e digital através de gamificação.

---

## 🎯 Funcionalidades Implementadas

### 1. **Onboarding Interativo**
- Fluxo de 5 telas apresentando os pilares do projeto.
- Introdução aos conceitos de privacidade e direitos do titular.
- Persistência de estado para exibição única.

### 2. **Tela Inicial (Home) - Dashbord Premium**
- **Hero Banner:** Visualização do progresso percentual total e nível de XP.
- **Categorias:** Filtragem dinâmica de conteúdos por área (Fundamentos, Direitos, etc.).
- **Atalhos Rápidos:** Acesso direto a recursos externos (Portal ANPD) e resumos legais.
- **Gamificação:** Cards interativos para os mini-jogos.

### 3. **Módulo de Lições e Estudo**
- 10 lições estruturadas cobrindo desde a introdução até o contexto acadêmico.
- **Detalhes da Lição:** Tela dedicada com conteúdo rico e botão de conclusão.
- **Feedback Visual:** Ícones de conclusão na lista e ganho de +10 XP por aula.

### 4. **Jogos Educativos (Mini-Games)**
- **Termo da Privacidade (Wordle):** Jogo de adivinhação de palavras de 5 letras com vocabulário jurídico e tecnológico (mais de 100 termos).
- **Caça-Palavras Legal:** Grade procedural 10x10 para busca de conceitos centrais da lei com detecção de toque por arrasto.

### 5. **Perfil do Usuário Completo**
- **Avatar Customizável:** Seletor de cores para o perfil (6 opções vibrantes).
- **Estatísticas Reais:** Ofensiva (Streak), lições concluídas, testes realizados e média de acertos.
- **Conquistas:** Sistema de medalhas horizontais desbloqueáveis por marcos de aprendizado.
- **Privacidade:** Diálogo dedicado explicando o tratamento de dados local e conformidade com a LGPD.
- **Gestão:** Edição de nome e reset total de progresso.

---

## 🏗️ Arquitetura e Tecnologias

### Tecnologias Utilizadas
- **Linguagem:** Kotlin 1.9.0
- **UI Framework:** Android View System com XML (Material Design 3)
- **Estilo:** Glassmorphism UI (transparências, gradientes e blur simulado)
- **Navegação:** Jetpack Navigation Component
- **Persistência:** SharedPreferences (via `UserPreferences.kt`)
- **Arquitetura:** MVVM (Model-View-ViewModel) com StateFlow
- **Logging:** Timber

### Estrutura de Diretórios
```
app/src/main/java/br/ufpi/lgpd/educacional/
├── data/
│   ├── model/          # Lesson, Quiz, AchievementItem
│   └── LgpdContent     # Repositório de dados estáticos (aulas/quizzes)
├── ui/
│   ├── MainActivity    # Host da navegação e Toolbar dinâmica
│   ├── home/           # Dashboard e Lógica da Home
│   ├── lessons/        # Lista de lições e Detalhes
│   ├── quizzes/        # Quizzes e Mini-jogos (Wordle/Wordsearch)
│   ├── profile/        # Perfil, Conquistas e Preferências
│   ├── onboarding/     # Fluxo de introdução
│   └── adapter/        # Adaptadores de listas e cards
└── util/
    └── UserPreferences # Hub central de persistência e lógica de XP
```

---

## 💾 Gamificação e XP

O sistema de gamificação foi projetado para manter o engajamento:
- **Lição Concluída:** +10 XP
- **Vitória no Termo:** +15 XP
- **Vitória no Caça-Palavras:** +20 XP
- **Primeiro Quiz:** +20 XP
- **Melhoria de Nota:** +5 XP

### Níveis de Usuário
- **Nível 1:** 0 - 149 XP
- **Nível 2:** 150 - 499 XP
- **Nível 3:** 500 - 999 XP
- **Nível 4:** 1000 - 1499 XP
- **Nível 5:** 1500+ XP

---

## 📋 Como Executar

### Pré-requisitos
- Android Studio Iguana ou superior
- SDK API 34
- JDK 17

### Passos
1. Clone o repositório.
2. Sincronize o Gradle.
3. Execute no emulador ou dispositivo físico com Android 7.0+.

---

## 👥 Autores

- **Aluno**: Patrick Do Nascimento Santos
- **Orientadora**: Patricia Vieira da Silva Barros
- **Instituição**: UFPI - Universidade Federal do Piauí (Campus Picos)
- **Programa**: PET - Programa de Educação Tutorial

---

## 📅 Status do Projeto
**Versão 1.0 (Finalizada)** ✨
O aplicativo encontra-se funcionalmente completo, com todas as metas de migração e paridade técnica atingidas.

---
*Este projeto colabora para a efetividade da Lei Geral de Proteção de Dados através da alfabetização digital na Sociedade Digital.*
