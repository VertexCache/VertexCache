#!/bin/bash

set -e  # Exit on error

echo "Cleaning and installing dependencies for SDK and Client..."

# Navigate to SDK
echo "Cleaning SDK..."
cd sdk
rm -rf _build deps mix.lock
mix deps.get
cd ..

# Navigate to Client
echo "Cleaning Client..."
cd client
rm -rf _build deps mix.lock
mix deps.get
cd ..

echo "Clean installation complete!"
