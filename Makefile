.PHONY: help submodule-pull submodule-push spull spush format check test ci build-debug build-release build-bundle clean

ifeq ($(OS),Windows_NT)
    GRADLEW = gradlew.bat
else
    GRADLEW = ./gradlew
endif

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
build-debug:
	$(GRADLEW) assembleDebug

build-release:
	$(GRADLEW) :app:assembleRelease

build-bundle:
	$(GRADLEW) :app:bundleRelease

clean:
	$(GRADLEW) clean
