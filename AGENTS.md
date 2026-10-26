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

## Cloudflare WARP (streaming)

Video traffic can be routed through an embedded Cloudflare WARP tunnel
(WireGuard or MASQUE, no `VpnService`). The native library lives in
`warp-native/` (Rust + uniffi) and is built into `app/src/main/jniLibs`
(gitignored):

```bash
make warp-native   # needs Rust Android targets, cargo-ndk 4.1.2, NDK 28.2.13676358
```

`make build-debug`, `build-release` and `build-bundle` depend on it. If the
`.so` is missing, the app still builds and the WARP setting hides itself.
Regenerate the Kotlin bindings after changing the uniffi API with
`make warp-bindings`.

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

WARP transports are verified from the host (no emulator needed):

```bash
cd warp-native
cargo test --test transports -- --ignored --test-threads=1 --nocapture
# or one transport at a time:
cargo run --example smoke -- wg   # wireguard | h3 | h2
```

The tests bring the tunnel up and request
`http://www.cloudflare.com/cdn-cgi/trace` through the local SOCKS5 proxy,
asserting the response says `warp=on`. They register a WARP device on first
run, so they are `#[ignore]`d by default. MASQUE HTTP/3 additionally needs
UDP/443; on networks that block it, H2 (same endpoint, TCP) is used instead.

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

## AnimeDataSource
- `app\src\main\java\git\shin\animevsub\data\remote\api\AnimeDataSource.kt` - Interface to get data from anime source
- `app\src\main\java\git\shin\animevsub\data\remote\api_example\GogoAnimeDataSource.kt` - Example implementation of AnimeDataSource for GogoAnime
