#!/bin/bash
set -e

PLATFORM="unknown"
ARCH=$(uname -m)

case "$(uname -s)" in
    Darwin)
        if [[ "$ARCH" == "x86_64" ]]; then
            PLATFORM="macos-intel"
        else
            PLATFORM="macos-silicon"
        fi
        ;;
    Linux)
        PLATFORM="linux-x86_64"
        ;;
    MINGW* | CYGWIN* | MSYS*)
        PLATFORM="win-x86_64"
        ;;
esac

if [[ "$PLATFORM" == "unknown" ]]; then
    echo "❌ Unsupported platform: $(uname -s) $ARCH"
    exit 1
fi

echo "📦 Creating distribution package for $PLATFORM..."

DOCKER_IMAGE="vertexcache-$PLATFORM"

docker run --rm -v "$(pwd)":/project -w /project $DOCKER_IMAGE bash -c "
    mkdir -p dist/$PLATFORM
    cp build/sdk/*.so dist/$PLATFORM/ || true
    cp build/sdk/*.dylib dist/$PLATFORM/ || true
    cp build/sdk/*.dll dist/$PLATFORM/ || true
    cp sdk/include/*.h dist/$PLATFORM/
"

echo "✅ Distribution package created for $PLATFORM!"
