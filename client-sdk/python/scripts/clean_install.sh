#!/bin/bash

# Exit immediately if a command fails
set -e

echo "Cleaning previous installation..."
rm -rf $(poetry env info --path) || true  # Remove Poetry's virtual environment if it exists
poetry cache clear pypi --all             # Clear Poetry's cache

echo "Installing dependencies..."
poetry install

echo "Installation complete!"
