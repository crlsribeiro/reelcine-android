# 🎬 ReelCine

> **Descubra. Compartilhe. Conecte.**
> Uma rede social para cinéfilos — descubra filmes, compartilhe recomendações e conecte-se com amigos através de grupos privados.

## 📱 Sobre o Projeto

ReelCine é um aplicativo Android nativo desenvolvido com **Jetpack Compose** e arquitetura moderna. Consome a API do TMDB para exibir filmes em tempo real e usa o Firebase como backend completo para autenticação, banco de dados e armazenamento.

## ✨ Features

- 🔐 **Autenticação** — Login com e-mail/senha e Google Sign-In
- 🏠 **Home** — Filmes em alta, em cartaz, em breve e mais populares
- 🔍 **Busca** — Pesquisa de filmes em tempo real
- 🎬 **Detalhe do Filme** — Informações completas, trailer e avaliações
- 📌 **Watchlist** — Salve filmes para assistir depois
- 👥 **Grupos** — Crie grupos privados e compartilhe recomendações
- 📢 **Feed** — Veja recomendações de todos os usuários
- 👤 **Perfil** — Edite nome, bio e foto de perfil
- 🌙 **Dark Mode** — Interface totalmente adaptada ao modo escuro

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** com separação em camadas:

\`\`\`
app/
├── data/
│   ├── remote/          # Retrofit, DTOs, API services
│   └── repository/      # Implementações dos repositórios
├── domain/
│   ├── model/           # Entidades de domínio
│   ├── repository/      # Interfaces dos repositórios
│   └── usecase/         # Casos de uso
└── presentation/
    ├── navigation/      # NavGraph, Screen routes
    ├── screens/         # Screens + ViewModels
    └── theme/           # MaterialTheme, cores, tipografia
\`\`\`

## 🛠️ Tech Stack

| Categoria | Tecnologia |
|-----------|-----------|
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM + Clean Architecture |
| DI | Hilt 2.56.1 |
| Navegação | Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Rede | Retrofit + OkHttp |
| Imagens | Coil |
| Auth | Firebase Authentication |
| Banco de Dados | Firebase Firestore |
| API de Filmes | TMDB API |
| Testes | JUnit4 + MockK |
| CI/CD | GitHub Actions |
| Linguagem | Kotlin 2.0.21 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 |

## 🚀 Como Rodar

1. Clone o repositório:
\`\`\`bash
git clone https://github.com/crlsribeiro/reelcine-android.git
\`\`\`

2. Crie o arquivo \`local.properties\` na raiz:
\`\`\`properties
sdk.dir=/caminho/para/seu/android/sdk
TMDB_API_KEY=sua_api_key_aqui
\`\`\`

3. Adicione o \`google-services.json\` em \`app/\`

4. Rode:
\`\`\`bash
./gradlew installDebug
\`\`\`

## 🧪 Testes

\`\`\`bash
./gradlew test
\`\`\`

## 👨‍💻 Autor

**Carlos Ribeiro**
[![GitHub](https://img.shields.io/badge/GitHub-crlsribeiro-181717?style=flat&logo=github)](https://github.com/crlsribeiro)
