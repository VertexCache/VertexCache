#!/bin/bash
set -e

echo "🚀 Preparing macOS SDK for Docker build..."

# Locate the latest macOS SDK
SDK_DIR="/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs"
LATEST_SDK=$(ls -d $SDK_DIR/MacOSX*.sdk | sort -V | tail -n 1)

if [[ -z "$LATEST_SDK" ]]; then
    echo "❌ No macOS SDK found in $SDK_DIR"
    exit 1
fi

echo "📦 Found macOS SDK: $LATEST_SDK"

# Define the destination SDK archive file
SDK_ARCHIVE="MacOSX.sdk.tar.xz"
PROJECT_ROOT="$(pwd)"
ARCHIVE_PATH="$PROJECT_ROOT/$SDK_ARCHIVE"

# Check if the SDK archive already exists, if not, create it
if [[ ! -f "$ARCHIVE_PATH" ]]; then
    echo "📂 Creating SDK archive: $SDK_ARCHIVE"
    tar -cJf "$ARCHIVE_PATH" -C "$SDK_DIR" "$(basename "$LATEST_SDK")"
else
    echo "✅ SDK archive already exists: $ARCHIVE_PATH"
fi

# Run the main build script
echo "🚀 Starting macOS Apple Silicon build inside Docker..."
./scripts/build.sh macos-silicon
