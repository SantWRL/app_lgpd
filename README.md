# LGPD Educacional

Aplicativo Android nativo em Kotlin para ensino da LGPD com aulas, quizzes e flashcards de estudo.

## Objetivo

O projeto foi desenvolvido como parte de um TCC da UFPI para apoiar a formacao de usuarios digitais mais conscientes sobre privacidade, protecao de dados e direitos do titular.

## O que o app tem hoje

- Onboarding introdutorio.
- Trilha de aulas sobre fundamentos da LGPD.
- Quizzes com resultado salvo no perfil.
- Flashcards de estudo com categorias, animacao de virar cartao e recompensa de XP.
- Videos educacionais com player do YouTube embutido.
- Backend opcional no Supabase: scrap agendado de noticias da ANPD, sincronizacao de videos e ranking global de XP (veja supabase/README.md).
- Perfil com pontos, nivel, streak e progresso.
- Feed com noticias da ANPD via leitura da pagina oficial.

## Arquitetura

- Linguagem: Kotlin 1.9
- UI: Android Views + XML
- Navegacao: Navigation Component
- Persistencia principal: Room
- Estado de onboarding: SharedPreferences
- Padrao: MVVM
- Logging: Timber

## Regras de pontuacao

- Aula concluida: +10 XP
- Primeiro resultado em um quiz: +20 XP
- Melhorar a melhor nota de um quiz: +5 XP
- Revisao completa de um baralho de flashcards: +10 XP

## Backend (Supabase)

- Noticias da ANPD: scrap agendado no servidor (Edge Function `scrape-news`).
- Videos: sincronizacao diaria via RSS do YouTube (Edge Function `sync-videos`).
- Ranking global: tabela `leaderboard` atualizada pelo proprio app.
- Sem configuracao, o app continua 100% funcional com o conteudo estatico offline.
- Instrucoes completas em `supabase/README.md`.

## Executando o projeto

Requisitos:

- Android Studio Iguana ou superior
- SDK 34
- JDK 17

Passos:

1. Abra o projeto no Android Studio.
2. Sincronize o Gradle.
3. Execute em emulador ou dispositivo Android 7.0+.

## Observacoes tecnicas

- O progresso e a pontuacao usam o Room como fonte de verdade.
- O feed depende de acesso a internet ao portal oficial da ANPD.
- O onboarding continua salvo localmente para nao reaparecer a cada abertura.

## Autoria

- Aluno: Patrick Do Nascimento Santos
- Orientadora: Patricia Vieira da Silva Barros
- Instituicao: UFPI - Campus Picos
