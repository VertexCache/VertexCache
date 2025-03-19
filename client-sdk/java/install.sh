#!/bin/bash

# Set script to exit on any command failure
set -e

# Navigate to the project root directory
cd "$(dirname "$0")"

echo "Cleaning and installing the project..."

# Clean and install the project
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "Build and installation successful!"
else
    echo "Build failed!"
    exit 1
fi
