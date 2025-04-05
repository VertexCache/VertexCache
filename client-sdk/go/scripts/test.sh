#!/bin/bash
set -euo pipefail

echo "🧪 Running Go tests..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."

cd "$ROOT_DIR"  # Ensure we're running from go/ folder (module root)

go test ./tests/... -v

echo "✅ All tests completed."
