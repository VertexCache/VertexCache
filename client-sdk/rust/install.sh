#!/bin/bash
# install.sh - Clean install script for VertexCache Rust SDK and Client

set -e

WORKSPACE_DIR="$(cd "$(dirname "$0")" && pwd)"
SDK_DIR="$WORKSPACE_DIR/sdk"
CLIENT_DIR="$WORKSPACE_DIR/client"

# Ensure Cargo is installed
if ! command -v cargo &> /dev/null; then
    echo "Cargo (Rust package manager) is not installed. Please install Rust first."
    exit 1
fi

# Remove previous build artifacts
echo "Cleaning previous builds..."
cargo clean --manifest-path "$WORKSPACE_DIR/Cargo.toml"

# Install dependencies and build the workspace
echo "Building SDK and Client..."
cargo build --release

echo "Installation completed successfully."
