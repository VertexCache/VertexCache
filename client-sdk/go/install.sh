#!/bin/bash
set -e  # Exit on any error

echo "Installing dependencies for Go projects..."

# Function to install dependencies in a given directory
install_deps() {
    local dir=$1
    if [ -f "$dir/go.mod" ]; then
        echo "Installing dependencies in $dir ..."
        cd "$dir"
        go mod tidy
        go get -u ./...
        go mod verify
        cd - > /dev/null  # Return to previous directory
    else
        echo "⚠Warning: No go.mod file found in $dir. Skipping..."
    fi
}

# Install dependencies for SDK and Client
install_deps "sdk"
install_deps "client"

echo "Dependencies installed successfully for both SDK and Client!"
