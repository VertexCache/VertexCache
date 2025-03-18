#!/bin/bash
set -e  # Exit on error

echo "Running unit tests..."
cd build
ctest --output-on-failure
echo "All tests passed."
