#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status

# Define project directories
SOLUTION_FILE="VertexCache.sln"
SDK_PROJECT="sdk/VertexCache.Sdk.csproj"
CLIENT_PROJECT="client/VertexCache.Client.csproj"
TEST_PROJECT="tests/VertexCache.Tests.csproj"

# Clean previous builds
echo "Cleaning previous builds..."
dotnet clean $SOLUTION_FILE

# Restore dependencies
echo "Restoring dependencies..."
dotnet restore $SOLUTION_FILE

# Build the SDK and Client
echo "Building SDK and Client..."
dotnet build $SOLUTION_FILE --configuration Release

# Run tests
echo "Running tests..."
dotnet test $TEST_PROJECT --configuration Release --no-build

# Pack SDK (for NuGet)
echo "Packing SDK..."
dotnet pack $SDK_PROJECT --configuration Release --no-build --output ./artifacts

# Done
echo "Build completed successfully!"
