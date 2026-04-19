# AnimeVsub Technical Knowledge Base for Gemini

This document provides a deep dive into the architecture, coding styles, and logic patterns of the AnimeVsub Android project to ensure AI-generated code is consistent and context-aware.

## 1. Project Architecture
- **Framework**: Modern Android with Jetpack Compose.
- **Dependency Injection**: Hilt (Standard `@AndroidEntryPoint`, `@Inject`, `@Module`).
- **Data Flow**: MVVM (Model-View-ViewModel).
    - `Repositories` handle data fetching (Supabase, Scrapers, Local DB).
    - `ViewModels` expose `StateFlow` or `MutableState` for UI.
    - `Screens` (Composables) observe state and emit events.
- **Navigation**: Type-safe navigation using `Destinations` (or standard Compose Navigation with route strings).

## 2. UI & Styling (The "AnimeVsub Look")
- **Theme**: Custom `AnimeVsubTheme` based on Material 3.
- **Colors**:
    - `AccentMain`: The primary brand color (used for buttons, active states).
    - Backgrounds are typically dark/neutral to make anime posters pop.
- **Components**:
    - **Buttons**: Use `RoundedCornerShape` (usually 8dp to 12dp). Special buttons like "Update Now" use `AccentMain`.
    - **Player**: Custom ExoPlayer implementation with a focused, minimal UI.
    - **Pills/Indicators**: Double-tap skip indicators use `RoundedCornerShape(50%)` on the inner edge with 42dp padding.
- **Layout Logic**:
    - Use `Box` for layering (e.g., video player controls over content).
    - Use `Column`/`Row` with `Modifier.fillMaxWidth()`/`wrapContentSize()`.
    - **Critical**: WebViews in Dialogs must have explicit layout params to prevent overflow.

## 3. Data Models & Logic
- **Anime**: `Models.kt` contains the core `Anime` data class.
- **Logic Quirks**:
    - Cloudflare bypass uses a specific `WebView` orchestration.
    - Video playback involves "Servers" and "Sources" (multi-source logic).
    - Syncing is handled via Supabase (History, Playlists).

## 4. AI Framework (The `ai` package)
- **Base**: `Skill` interface for atomic actions.
- **Agents**: `CuratorAgent`, `SupportAgent` orchestrate multiple skills.
- **Logic**: Use AI to "translate" natural language into structured skill calls.

## 5. Coding Standards for AI
- **Conciseness**: Avoid boilerplate. Use Kotlin's idiomatic features (`let`, `apply`, `extension functions`).
- **Stability**: Always check for `null` in repository responses. Use `Loading`/`Success`/`Error` states for UI.
- **Localization**: Use `stringResource(R.string...)` for all UI text.
