#!/bin/bash

# Stop execution on any error
set -e

# Define project paths
SOLUTION_FILE="VertexCache.sln"
SDK_PROJECT="sdk/VertexCache.Sdk.csproj"
CLIENT_PROJECT="client/VertexCache.Client.csproj"
TEST_PROJECT="tests/VertexCache.Tests.csproj"

# Clean previous builds
echo "Cleaning previous builds..."
dotnet clean $SOLUTION_FILE

echo "Cleaning artifacts and temporary files..."
rm -rf artifacts/
rm -rf sdk/bin/ sdk/obj/
rm -rf client/bin/ client/obj/
rm -rf tests/bin/ tests/obj/

echo "Clean completed!"

# Restore dependencies
echo "Restoring dependencies..."
dotnet restore $SOLUTION_FILE

echo "Restoration completed!"

# Build SDK and Client
echo "Building SDK and Client..."
dotnet build $SOLUTION_FILE --configuration Release

echo "Build completed!"

# Run tests
echo "Running tests..."
dotnet test $TEST_PROJECT --configuration Release --no-build

echo "Tests completed!"

# Run Client Application
echo "Running Client Application..."
dotnet run --project $CLIENT_PROJECT

echo "Execution completed!"
