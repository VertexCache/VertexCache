#!/bin/bash

# Resolve the sdk/ directory (where pom.xml lives)
SDK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../sdk" && pwd)"

cd "$SDK_DIR" || {
    echo "Error: Cannot find SDK directory at $SDK_DIR"
    exit 1
}

echo "Running mvn clean in $SDK_DIR"
mvn clean
