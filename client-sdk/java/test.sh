#!/bin/bash

# Set script to exit on any command failure
set -e

# Navigate to the project root directory
cd "$(dirname "$0")"

echo "Running tests..."

# Run Maven tests
mvn test

if [ $? -eq 0 ]; then
    echo "All tests passed!"
else
    echo "Tests failed!"
    exit 1
fi
