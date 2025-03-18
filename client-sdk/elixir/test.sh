#!/bin/bash

set -e  # Exit on error

echo "Running tests for SDK and Client..."

# Test SDK
echo "Testing SDK..."
cd sdk
mix test
cd ..

# Test Client
echo "Testing Client..."
cd client
mix test
cd ..

echo "All tests passed!"
