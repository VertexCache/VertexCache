#!/bin/bash
set -euo pipefail

clear

HOST="127.0.0.1"
PORT=50505
TIMEOUT=2

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."

# Platform detection
ARCH=$(uname -m)
OS=$(uname -s)

if [[ "$OS" == "Darwin" ]]; then
  PLATFORM="mac"
  CPU_TYPE=$([[ "$ARCH" == "arm64" ]] && echo "silicon" || echo "intel")
elif [[ "$OS" == "Linux" ]]; then
  PLATFORM="linux"
  CPU_TYPE=$([[ "$ARCH" == "aarch64" ]] && echo "arm64" || echo "x86_64")
elif [[ "$OS" == "MINGW"* || "$OS" == "CYGWIN"* || "$OS" == "MSYS"* ]]; then
  PLATFORM="win"
  CPU_TYPE=$([[ "$ARCH" == "aarch64" ]] && echo "arm64" || echo "x86_64")
else
  echo "❌ Unsupported OS: $OS"
  exit 1
fi

TARGET="$PLATFORM/$CPU_TYPE"
CLIENT_EXECUTABLE="$ROOT_DIR/build/client/$TARGET/VertexCache.SdkClient"

# Add .exe for Windows
[[ "$PLATFORM" == "win" ]] && CLIENT_EXECUTABLE="${CLIENT_EXECUTABLE}.exe"

# Check for client executable
if [[ ! -f "$CLIENT_EXECUTABLE" ]]; then
  echo "❌ Client binary not found: $CLIENT_EXECUTABLE"
  echo "💡 Run ./scripts/build.sh first."
  exit 1
fi

# Check if server is reachable (quiet success, verbose failure)
if ! nc -z -w $TIMEOUT "$HOST" "$PORT" >/dev/null 2>&1; then
  echo "❌ VertexCache server not reachable at $HOST:$PORT (timeout ${TIMEOUT}s)"
  echo "💡 Make sure the server is running before starting the client."
  exit 1
fi

"$CLIENT_EXECUTABLE"
