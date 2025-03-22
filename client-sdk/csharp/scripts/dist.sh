#!/bin/bash
set -euo pipefail

ZIP_OUTPUT="${1:-false}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."

BUILD_DIR="$ROOT_DIR/build"
DIST_DIR="$ROOT_DIR/dist"
ZIP_DIR="$DIST_DIR/zips"

echo "📦 Copying build artifacts to dist directory..."

mkdir -p "$DIST_DIR"
mkdir -p "$ZIP_DIR"

# Copy SDK and Client binaries
for target in mac/intel mac/silicon linux/x86_64 linux/arm64 win/x86_64 win/arm64; do
  echo "🔄 Copying for $target..."

  SDK_SRC="$BUILD_DIR/sdk/$target"
  CLIENT_SRC="$BUILD_DIR/client/$target"

  SDK_DEST="$DIST_DIR/$target"
  CLIENT_DEST="$DIST_DIR/$target"

  mkdir -p "$SDK_DEST"

  cp -r "$SDK_SRC"/* "$SDK_DEST" 2>/dev/null || echo "⚠️  No SDK artifacts for $target"
  cp -r "$CLIENT_SRC"/* "$CLIENT_DEST" 2>/dev/null || echo "⚠️  No client artifacts for $target"

  if [[ "$ZIP_OUTPUT" == "true" ]]; then
    ZIP_NAME="VertexCache-sdk-${target//\//-}.zip"
    ZIP_PATH="$ZIP_DIR/$ZIP_NAME"
    echo "📦 Creating archive: $ZIP_NAME"
    (cd "$DIST_DIR/$target" && zip -r "$ZIP_PATH" . > /dev/null)
  fi
done

echo "✅ Distribution complete."
echo "📁 Artifacts copied to: $DIST_DIR"
[[ "$ZIP_OUTPUT" == "true" ]] && echo "📦 Zips stored in: $ZIP_DIR"
