#!/bin/bash
set -e

echo "🚀 Building VertexCache SDK for macOS Apple Silicon (M2) locally..."

# Ensure CMake is installed
if ! command -v cmake &> /dev/null; then
    echo "❌ CMake is not installed. Please install it with: brew install cmake"
    exit 1
fi

# Create local build directory
BUILD_DIR="./build/local"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

# Run CMake configuration
cmake -B "$BUILD_DIR" -DCMAKE_BUILD_TYPE=Debug

# Build the project
cmake --build "$BUILD_DIR" -- VERBOSE=1

echo "✅ Local macOS Apple Silicon build complete!"
