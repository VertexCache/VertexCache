#!/bin/bash

set -e  # Stop script on first error

echo "Cleaning project..."
./run_clean.sh

echo "Installing dependencies..."
./run_install.sh

echo "Running SDK tests..."
./run_tests.sh

echo "Running client..."
./run_client.sh

echo "All tasks completed successfully!"