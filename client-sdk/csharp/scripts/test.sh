#!/bin/bash
set -euo pipefail

CONFIGURATION="${1:-Debug}"
echo "🧪 Running tests with configuration: $CONFIGURATION"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."

SOLUTION_FILE="$ROOT_DIR/VertexCache.sln"

if [ ! -f "$SOLUTION_FILE" ]; then
  echo "❌ Solution file not found at: $SOLUTION_FILE"
  exit 1
fi

dotnet test "$SOLUTION_FILE" -c "$CONFIGURATION" --no-build
