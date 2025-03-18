#!/bin/bash
set -e  # Exit on any error

echo "Running tests..."

# Function to run tests in a given directory
run_tests() {
    local dir=$1
    if [ -f "$dir/go.mod" ]; then
        echo "Running tests in $dir ..."
        cd "$dir"
        go test -v -race -cover ./...
        cd - > /dev/null  # Return to previous directory
    else
        echo "⚠Warning: No go.mod file found in $dir. Skipping..."
    fi
}

# Run tests for SDK and Client
run_tests "sdk"
run_tests "client"

echo "All tests passed successfully!"
