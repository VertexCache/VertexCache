#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/../.."
SRC_DIR="$ROOT_DIR/sdk"

TARGETS=(
  "darwin amd64"
  "darwin arm64"
  "linux amd64"
  "linux arm64"
  "windows amd64"
  "windows arm64"
)

echo "🔍 Validating VertexCache SDK build (no build binary output)..."

for target in "${TARGETS[@]}"; do
  read -r GOOS GOARCH <<< "$target"

  echo "🚧 Checking for $GOOS/$GOARCH..."
  (
    cd "$SRC_DIR"
    GOOS=$GOOS GOARCH=$GOARCH CGO_ENABLED=0 \
      go build ./...
  )

  echo "✅ $GOOS/$GOARCH build passed"
done

echo "🎉 SDK code compiles cleanly across all platforms (no build binary output)"
