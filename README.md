# 🎬 ReelCine

App social de filmes em Android com Jetpack Compose.

## Stack
- Kotlin + Jetpack Compose + Material3
- Clean Architecture (Domain / Data / Presentation)
- Hilt 2.56.1 (NUNCA mudar essa versão)
- Firebase Auth + Firestore
- TMDB API
- Coroutines + Flow
- JUnit4 + MockK + Turbine

## Setup
1. Clone o repo
2. Crie `local.properties` com `TMDB_API_KEY=sua_chave` e `sdk.dir=seu_sdk`
3. Adicione `google-services.json` em `app/`
4. `./gradlew installDebug`

## Features
- Auth (Email + Google Sign-In)
- Home, Search, Watchlist, Feed, Groups, Profile
- Edit Profile, Add Recommendation
- CI/CD GitHub Actions
- 14 Unit Tests

## Pendente
- GroupDetail screen
- Notificações push (Firebase Messaging)
- Índice Firestore watchlist (aguardando ativação no Firebase Console)

## Versões
- Kotlin: 2.0.21 | AGP: 8.10.1 | compileSdk: 36 | JDK: 17
- Firebase: reelcine-d2ba6
- SHA-1: 4e:47:06:5d:0d:d8:ba:40:b0:a6:15:8f:e7:d5:17:57:a6:56:f9:31
