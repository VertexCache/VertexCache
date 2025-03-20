#!/bin/bash
set -e

# Function to display usage
usage() {
    echo "Usage: $0 <platform>-<architecture>"
    echo "Available options:"
    echo "  macos-intel      (macOS x86_64)"
    echo "  macos-silicon    (macOS arm64)"
    echo "  linux-x86_64     (Linux x86_64)"
    echo "  linux-arm64      (Linux ARM64)"
    echo "  win-x86_64       (Windows x86_64)"
    echo "  win-arm64        (Windows ARM64)"
    exit 1
}

# Validate input argument
if [[ $# -ne 1 ]]; then
    usage
fi

PLATFORM_ARCH="$1"
DOCKERFILE_PATH="$(pwd)/Dockerfile.$PLATFORM_ARCH"
DOCKER_IMAGE="vertexcache-$PLATFORM_ARCH"
SDK_FILE="MacOSX14.0.sdk.tar.xz"

# Ensure the Dockerfile exists
if [[ ! -f "$DOCKERFILE_PATH" ]]; then
    echo "❌ Missing Dockerfile: $DOCKERFILE_PATH"
    usage
fi

# Ensure macOS SDK file exists for macOS builds
if [[ "$PLATFORM_ARCH" == "macos-silicon" || "$PLATFORM_ARCH" == "macos-intel" ]]; then
    if [[ ! -f "$(pwd)/$SDK_FILE" ]]; then
        echo "❌ Missing macOS SDK: $(pwd)/$SDK_FILE"
        echo "➡️ Please move it here: mv MacOSX14.0.sdk.tar.xz client-sdk/cpp/"
        exit 1
    fi
fi

echo "🐳 Running build inside Docker for $PLATFORM_ARCH..."

# Build Docker image if not exists
if [[ "$(docker images -q $DOCKER_IMAGE 2> /dev/null)" == "" ]]; then
    echo "📦 Building Docker image..."
    docker build -t $DOCKER_IMAGE -f "$DOCKERFILE_PATH" .
fi

docker run --rm -v "$(pwd)":/project -w /project $DOCKER_IMAGE bash -c "
    mkdir -p build && cd build
    cmake ..
    cmake --build . -- VERBOSE=1
"

echo "✅ Build ($PLATFORM_ARCH) complete in Docker!"
