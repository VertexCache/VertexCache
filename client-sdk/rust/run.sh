#!/bin/bash
# run.sh - Run VertexCache Client

set -e

WORKSPACE_DIR="$(cd "$(dirname "$0")" && pwd)"
CLIENT_DIR="$WORKSPACE_DIR/client"

# Ensure Cargo is installed
if ! command -v cargo &> /dev/null; then
    echo "Cargo (Rust package manager) is not installed. Please install Rust first."
    exit 1
fi

# Run the client binary
echo "Running VertexCache Client..."
cargo run --release --manifest-path "$CLIENT_DIR/Cargo.toml"
