#!/bin/bash
set -euo pipefail

CONFIGURATION="${1:-Release}"
if [[ "$CONFIGURATION" != "Debug" && "$CONFIGURATION" != "Release" ]]; then
  echo "❌ Invalid build configuration: '$CONFIGURATION'"
  echo "   Allowed values: Debug, Release"
  exit 1
fi

echo "⚙️  Using build configuration: $CONFIGURATION"

# Resolve path to client-sdk/csharp
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."

SDK_PROJECT="$ROOT_DIR/sdk/VertexCache.Sdk.csproj"
CLIENT_PROJECT="$ROOT_DIR/sdk-client/VertexCache.SdkClient.csproj"
BUILD_DIR="$ROOT_DIR/build"

# Format: "platform:rid"
RUNTIMES=(
  "mac/intel:osx-x64"
  "mac/silicon:osx-arm64"
  "linux/x86_64:linux-x64"
  "linux/arm64:linux-arm64"
  "win/x86_64:win-x64"
  "win/arm64:win-arm64"
)

echo "📦 Building VertexCache SDK and Client for all target platforms..."

for entry in "${RUNTIMES[@]}"; do
  PLATFORM="${entry%%:*}"
  RID="${entry##*:}"

  echo "🔧 Building for $PLATFORM ($RID)..."

  SDK_OUT="$BUILD_DIR/sdk/$PLATFORM"
  dotnet publish "$SDK_PROJECT" -c "$CONFIGURATION" -r "$RID" --self-contained false -o "$SDK_OUT"

  CLIENT_OUT="$BUILD_DIR/client/$PLATFORM"
  dotnet publish "$CLIENT_PROJECT" -c "$CONFIGURATION" -r "$RID" --self-contained true -p:PublishSingleFile=true -o "$CLIENT_OUT"
done

echo "✅ Build complete for configuration: $CONFIGURATION"
echo "📁 Output stored in: $BUILD_DIR"
