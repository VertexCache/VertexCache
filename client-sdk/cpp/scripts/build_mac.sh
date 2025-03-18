#!/bin/bash
set -e  # Stop on error

ARCH=$(uname -m)  # Detect architecture

echo "Building for macOS ($ARCH)..."
mkdir -p build && cd build

if [[ "$ARCH" == "arm64" ]]; then
    echo "Targeting macOS Apple Silicon (M1/M2)..."
    cmake -DCMAKE_OSX_ARCHITECTURES=arm64 ..
else
    echo "Targeting macOS Intel x86_64..."
    cmake -DCMAKE_OSX_ARCHITECTURES=x86_64 ..
fi

cmake --build .
echo "Build complete."
