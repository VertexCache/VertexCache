#!/bin/bash
set -euo pipefail

CONFIGURATION="${1:-Debug}"

echo "🧪 Running tests with configuration: $CONFIGURATION"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
UNIT_TEST_PROJECT="$ROOT_DIR/tests/unit/VertexCache.UnitTests.csproj"
INTEGRATION_TEST_PROJECT="$ROOT_DIR/tests/integration/VertexCache.IntegrationTests.csproj"
CONFIG_DIR="$ROOT_DIR/config"
CERT_FILE="$CONFIG_DIR/test-certificate.pem"

# Ensure config and test-certificate.pem are present
if [[ ! -d "$CONFIG_DIR" || ! -f "$CERT_FILE" ]]; then
  echo "📦 Installing test config..."
  "$ROOT_DIR/scripts/install_config.sh"
fi

# Run Unit Tests
echo "🔧 Running Unit Tests..."
dotnet test "$UNIT_TEST_PROJECT" -c "$CONFIGURATION" --verbosity minimal

# Run Integration Tests
echo "🔧 Running Integration Tests..."
dotnet test "$INTEGRATION_TEST_PROJECT" -c "$CONFIGURATION" --verbosity minimal

echo "✅ All tests completed."
