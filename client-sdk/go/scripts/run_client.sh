#!/bin/bash
set -euo pipefail

clear

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."
ENV_FILE="$ROOT_DIR/config/.env"

# Default values
HOST="127.0.0.1"
PORT="50505"
TIMEOUT=2

# Load .env values manually (safe for PEMs)
if [[ -f "$ENV_FILE" ]]; then
  while IFS='=' read -r key value; do
    case "$key" in
      server_host) HOST="$value" ;;
      server_port) PORT="$value" ;;
    esac
  done < <(grep -v '^#' "$ENV_FILE" | grep '=')
fi

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
CLIENT_EXECUTABLE="$ROOT_DIR/build/client/$TARGET/vertexcache_sdk_client"
[[ "$PLATFORM" == "win" ]] && CLIENT_EXECUTABLE="${CLIENT_EXECUTABLE}.exe"

if [[ ! -f "$CLIENT_EXECUTABLE" ]]; then
  echo "❌ Client binary not found: $CLIENT_EXECUTABLE"
  echo "💡 Run ./scripts/build.sh first."
  exit 1
fi

if ! nc -z -w $TIMEOUT "$HOST" "$PORT" >/dev/null 2>&1; then
  echo "❌ VertexCache server not reachable at $HOST:$PORT (timeout ${TIMEOUT}s)"
  echo "💡 Make sure the server is running before starting the client."
  exit 1
fi

"$CLIENT_EXECUTABLE"
