#!/bin/bash
set -e  # Exit on error

echo "Installing dependencies..."
brew install cmake googletest || echo "✔️ Dependencies already installed."

echo "Configuring and building project..."
mkdir -p build && cd build
cmake ..
cmake --build .
echo "Build complete."
