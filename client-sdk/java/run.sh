#!/bin/bash

# Set script to exit on any command failure
set -e

# Navigate to the project root directory
cd "$(dirname "$0")"

# Define the Client JAR file path
JAR_FILE="client/target/VertexCacheSDKClient-1.0.0-shaded.jar"

# Ensure the JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found! Building the project first..."
    ./install.sh  # Build the project first
fi

echo "Running VertexCacheSDKClient..."
java -jar "$JAR_FILE"
