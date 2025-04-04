#!/bin/bash
set -euo pipefail

CONFIGURATION="${1:-Release}"
if [[ "$CONFIGURATION" != "Debug" && "$CONFIGURATION" != "Release" ]]; then
  echo "❌ Invalid build configuration: '$CONFIGURATION'"
  echo "   Allowed values: Debug, Release"
  exit 1
fi

echo "⚙️  Using build configuration: $CONFIGURATION"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."
BUILD_DIR="$ROOT_DIR/build"

APP_NAME="vertexcache_sdk_client"
SRC_PATH="$ROOT_DIR/sdk-client"

# Format: "platform:GOOS:GOARCH"
TARGETS=(
  "mac/intel:darwin:amd64"
  "mac/silicon:darwin:arm64"
  "linux/x86_64:linux:amd64"
  "linux/arm64:linux:arm64"
  "win/x86_64:windows:amd64"
  "win/arm64:windows:arm64"
)

echo "📦 Building VertexCache Go SDK Client..."

for target in "${TARGETS[@]}"; do
  PLATFORM="${target%%:*}"
  REST="${target#*:}"
  GOOS="${REST%%:*}"
  GOARCH="${REST##*:}"

  OUT_DIR="$BUILD_DIR/client/$PLATFORM"
  BIN_NAME="$APP_NAME"
  [[ "$GOOS" == "windows" ]] && BIN_NAME="${BIN_NAME}.exe"

  mkdir -p "$OUT_DIR"
  echo "🔧 Building for $PLATFORM ($GOOS/$GOARCH)..."
  env GOOS="$GOOS" GOARCH="$GOARCH" go build -o "$OUT_DIR/$BIN_NAME" "$SRC_PATH"
done

echo "✅ Build complete for configuration: $CONFIGURATION"
echo "📁 Output stored in: $BUILD_DIR/client"
