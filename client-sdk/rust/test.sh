#!/bin/bash
# test.sh - Run Tests for SDK and Client

set -e

WORKSPACE_DIR="$(cd "$(dirname "$0")" && pwd)"

# Ensure Cargo is installed
if ! command -v cargo &> /dev/null; then
    echo "Cargo (Rust package manager) is not installed. Please install Rust first."
    exit 1
fi

# Run tests
echo "Running tests for SDK and Client..."
cargo test --manifest-path "$WORKSPACE_DIR/sdk/Cargo.toml"
cargo test --manifest-path "$WORKSPACE_DIR/client/Cargo.toml"

echo "All tests passed successfully."
