#!/bin/bash

# Navigate to the script directory
cd "$(dirname "$0")"

# Ensure Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "🚨 Maven is not installed. Please install Maven and try again."
    exit 1
fi

echo "🚀 Cleaning and building the project..."
mvn clean package -DskipTests

# Check if the build was successful
if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the error logs."
    exit 1
fi

echo "✅ Build successful!"

# Locate the Uber JAR
JAR_FILE="client/target/VertexCacheSDKClient-1.0.0.jar"

if [ -f "$JAR_FILE" ]; then
    echo "🚀 Running VertexCacheSDKClient..."
    java -jar "$JAR_FILE"
else
    echo "❌ JAR file not found! Did the build complete successfully?"
    exit 1
fi
