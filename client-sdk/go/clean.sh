#!/bin/bash
set -e  # Exit immediately if a command exits with a non-zero status

echo "Cleaning Go project..."

# Remove Go module cache (only locally)
go clean -modcache

# Remove compiled binaries and temporary files
rm -rf bin/ pkg/ client-sdk/go/client/*.exe client-sdk/go/client/*.out client-sdk/go/client/*.test client-sdk/go/sdk/*.exe client-sdk/go/sdk/*.out client-sdk/go/sdk/*.test

# Remove temporary test and log files
find . -name "*.log" -type f -delete
find . -name "*.tmp" -type f -delete
find . -name "*.cov" -type f -delete

echo "Clean-up complete!"
