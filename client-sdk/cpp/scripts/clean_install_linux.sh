#!/bin/bash
set -e  # Stop on error

echo "Cleaning build directory..."
rm -rf build
echo "Clean complete."

echo "Building for Linux..."
mkdir -p build && cd build

cmake ..
cmake --build .
echo "Build complete."
