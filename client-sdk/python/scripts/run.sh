#!/bin/bash

# Exit immediately if a command fails
set -e

echo "Running VertexCache client..."
poetry run python client/main.py
