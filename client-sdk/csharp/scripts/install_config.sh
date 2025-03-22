#!/bin/bash
set -euo pipefail

# Resolve paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

ROOT_DIR="$SCRIPT_DIR/../../.."
COPY_UTIL_PATH="$ROOT_DIR/scripts/common/copy-utils.sh"

TLS_CERT_SRC_FILE="$ROOT_DIR/etc/config/test_server_certificate.pem"
TLS_CERT_DEST_FILE="$SCRIPT_DIR/../config/test_server_certificate.pem"

ENV_EXAMPLE_SRC_FILE="$ROOT_DIR/etc/config/env-example-client"
ENV_EXAMPLE_DEST_FILE="$SCRIPT_DIR/../config/.env"

# Source shared functions
if [ -f "$COPY_UTIL_PATH" ]; then
    # shellcheck source=/dev/null
    source "$COPY_UTIL_PATH"
else
    echo "❌ Could not find utility script at $COPY_UTIL_PATH"
    exit 1
fi



# Copy using shared function
copy_file "$TLS_CERT_SRC_FILE" "$TLS_CERT_DEST_FILE"

copy_file "$ENV_EXAMPLE_SRC_FILE" "$ENV_EXAMPLE_DEST_FILE"
