# AGENTS.md - AnimeVsub Android App

## Build Commands

```bash
# Using Make (cross-platform)
make build-debug    # Debug APK
make build-release  # Release APK
make build-bundle   # Release AAB bundle

# Or use Gradle directly
./gradlew assembleDebug
./gradlew :app:assembleRelease
./gradlew :app:bundleRelease
```

## Code Quality

```bash
make check          # Run ktlintCheck + detekt + lintDebug
make format         # Auto-fix ktlint issues
make ci             # Full CI pipeline (check + test --parallel --build-cache)
```

**CI Order:** ktlintCheck → lintDebug → detekt → testDebugUnitTest → assembleDebug

## Testing

```bash
make test           # Run unit tests
./gradlew testDebugUnitTest
```

## Local Setup Requirements

Create `local.properties` with:
```
sdk.dir=<path-to-android-sdk>
SUPABASE_URL=<supabase-url>
SUPABASE_KEY=<supabase-key>
PASSWORD_UNLOCK_DEVELOPER=<dev-password>
```

The app reads from `local.properties` at build time to inject build config values.

## Project Architecture

- **Language:** Kotlin 2.1.0
- **UI:** Jetpack Compose with Material 3
- **DI:** Hilt (Dagger)
- **Build:** Gradle Kotlin DSL with KSP
- **Key libs:** Media3 ExoPlayer, Supabase, Coil, DataStore, WorkManager
- **Code location:** `app/src/main/java/git/shin/animevsub/`
- **Package:** `git.shin.animevsub`

## Lint Tools

- **ktlint** - Kotlin code style (configured in root build.gradle.kts)
- **detekt** - Static code analysis (config at `config/detekt/detekt.yml`)

## Release Process

Semantic-release on `main` branch auto-increments version in `gradle.properties` (versionName, versionCode) and builds AAB + APK.

## Submodules

Project uses git submodules. Pull with:
```bash
make submodule-pull  # or: git submodule update --init --recursive --remote
```

## Windows Note

Use `gradlew.bat` instead of `./gradlew` on Windows. Makefile handles this automatically.

## Excluded from VCS

`local.properties`, `google-services.json`, and release keystores must NOT be committed.