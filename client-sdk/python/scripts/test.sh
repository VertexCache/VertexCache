#!/bin/bash

# Exit if any test fails
set -e

echo "Running tests..."
poetry run pytest tests

echo "All tests passed!"
