#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."

echo "🧹 Cleaning build, dist, and config directories..."

rm -rf "$ROOT_DIR/build"
rm -rf "$ROOT_DIR/dist"
rm -rf "$ROOT_DIR/config"

echo "✅ Clean complete."
