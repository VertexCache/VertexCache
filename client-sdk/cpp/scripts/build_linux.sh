#!/bin/bash
set -e  # Stop on error

echo "Building for Linux..."
mkdir -p build && cd build

cmake ..
cmake --build .
echo "Build complete."
