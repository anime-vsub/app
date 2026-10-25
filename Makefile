.PHONY: help submodule-pull submodule-push spull spush format check test ci warp-native warp-bindings build-debug build-release build-bundle clean

ifeq ($(OS),Windows_NT)
    GRADLEW = gradlew.bat
else
    GRADLEW = ./gradlew
endif

# --- Cloudflare WARP native library ---------------------------------------
# Rust toolchain, cargo-ndk and the Android NDK are required to build it.
# `local.properties` usually only carries `sdk.dir`; CI exports ANDROID_HOME.
SDK_DIR ?= $(or $(ANDROID_HOME),$(ANDROID_SDK_ROOT),$(shell grep -s '^sdk.dir=' local.properties 2>/dev/null | cut -d= -f2-))
WARP_NDK_VERSION ?= 28.2.13676358
WARP_NDK_HOME ?= $(SDK_DIR)/ndk/$(WARP_NDK_VERSION)
WARP_ABIS ?= arm64-v8a armeabi-v7a x86_64
WARP_TARGET_DIR ?= $(or $(CARGO_TARGET_DIR),$(CURDIR)/warp-native/target)
# cargo-ndk takes one -t flag per ABI.
WARP_TARGET_FLAGS ?= $(foreach abi,$(WARP_ABIS),-t $(abi))
# 16 KB page size requirement for Android 15+ (targetSdk 36).
WARP_RUSTFLAGS ?= -C link-arg=-Wl,-z,max-page-size=16384

# Default target
help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "Submodule commands:"
	@echo "  submodule-pull (spull)  - Update all submodules from remote"
	@echo "  submodule-push (spush)  - Push changes in all submodules"
	@echo ""
	@echo "Check & Format commands:"
	@echo "  format                  - Run ktlintFormat to auto-fix code style"
	@echo "  check                   - Run ktlintCheck and detekt for linting"
	@echo "  test                    - Run Unit Tests"
	@echo "  ci                      - Run full CI pipeline (check + test)"
	@echo ""
	@echo "Build commands:"
	@echo "  build-debug             - Build Debug APK"
	@echo "  build-release           - Build Release APK"
	@echo "  build-bundle            - Build Release Bundle (AAB)"
	@echo "  clean                   - Clean build artifacts"
	@echo ""
	@echo "Cloudflare WARP native library:"
	@echo "  warp-native             - Cross-compile libwarp_native.so for Android"
	@echo "                            (ABIs: $(WARP_ABIS))"
	@echo "  warp-bindings           - Regenerate the uniffi Kotlin bindings"
	@echo ""
	@echo "WARP variables (override on the command line):"
	@echo "  WARP_ABIS, WARP_NDK_HOME, WARP_TARGET_DIR"

# Submodule targets
submodule-pull spull:
	git submodule update --init --recursive --remote

submodule-push spush:
	git submodule foreach 'git push origin $$(git rev-parse --abbrev-ref HEAD)'

# Format & Lint targets
format:
	$(GRADLEW) ktlintFormat

check:
	$(GRADLEW) ktlintCheck detekt lintDebug

test:
	$(GRADLEW) testDebugUnitTest

ci:
	$(GRADLEW) ktlintCheck detekt lintDebug testDebugUnitTest --parallel --build-cache

# Build targets
build-debug: warp-native
	$(GRADLEW) assembleDebug

build-release: warp-native
	$(GRADLEW) :app:assembleRelease

build-bundle: warp-native
	$(GRADLEW) :app:bundleRelease

# Cloudflare WARP native library
warp-native:
	@command -v cargo >/dev/null 2>&1 || { echo "cargo not found; install Rust (rustup.rs)" >&2; exit 1; }
	@command -v cargo-ndk >/dev/null 2>&1 || { echo "cargo-ndk not found; run: cargo install cargo-ndk" >&2; exit 1; }
	@test -d "$(WARP_NDK_HOME)" || { echo "Android NDK not found at $(WARP_NDK_HOME); set WARP_NDK_HOME or SDK_DIR" >&2; exit 1; }
	cd warp-native && \
	  ANDROID_NDK_HOME="$(WARP_NDK_HOME)" \
	  CARGO_TARGET_DIR="$(WARP_TARGET_DIR)" \
	  RUSTFLAGS="$(WARP_RUSTFLAGS)" \
	  cargo ndk $(WARP_TARGET_FLAGS) -o ../app/src/main/jniLibs build --release --lib
	@# `gotatun` (the WireGuard implementation) also emits a JNI cdylib that
	@# nothing loads; cargo-ndk copies every cdylib it finds. Only the
	@# uniffi library is linked into the app.
	@find "$(CURDIR)/app/src/main/jniLibs" -name 'libgotatun-*.so' -delete

# Only needed when the exported uniffi API changes.
warp-bindings:
	cd warp-native && \
	  CARGO_TARGET_DIR="$(WARP_TARGET_DIR)" \
	  cargo build --quiet --lib
	cd warp-native && \
	  CARGO_TARGET_DIR="$(WARP_TARGET_DIR)" \
	  cargo run --quiet --bin uniffi-bindgen -- generate \
	    --library "$(WARP_TARGET_DIR)/debug/libwarp_native.so" \
	    --language kotlin \
	    --out-dir ../app/src/main/java \
	    --no-format

clean:
	$(GRADLEW) clean
