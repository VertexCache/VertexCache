#!/bin/bash
set -e

VERSION=$1

if [[ -z "$VERSION" ]]; then
  echo "[elixir] ❌ ERROR: Missing version argument. Usage: ./scripts/dist.sh 1.0.0"
  exit 1
fi

DIST_DIR="dist"
PKG_DIR="$DIST_DIR/vertex_cache_sdk-$VERSION"
ZIP_FILE="$DIST_DIR/vertex_cache_sdk-$VERSION.zip"

echo "[elixir] Creating SDK-only dist package (version $VERSION)..."

# Clean existing
rm -rf "$DIST_DIR"
mkdir -p "$PKG_DIR"

# Copy SDK files only
cp -r lib "$PKG_DIR/"
cp mix.exs "$PKG_DIR/"
cp mix.lock "$PKG_DIR/" 2>/dev/null || true
cp README.md "$PKG_DIR/" 2>/dev/null || true

# Create zip archive
cd "$DIST_DIR"
zip -r "vertex_cache_sdk-$VERSION.zip" "vertex_cache_sdk-$VERSION" >/dev/null
cd ..

echo "[elixir] Dist package created:"
ls -lh "$ZIP_FILE"
