#!/bin/bash

# Resolve the root directory of the SDK
SDK_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SDK_DIR="$SDK_ROOT/sdk"

echo "🔧 Building VertexCache Java SDK from $SDK_DIR"

cd "$SDK_DIR" || {
    echo "Failed to enter $SDK_DIR"
    exit 1
}

# Build using Maven
if mvn clean install -DskipTests; then
    echo "Build successful."
else
    echo "Build failed."
    exit 1
fi
