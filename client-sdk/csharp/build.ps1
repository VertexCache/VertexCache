# Stop execution on any error
$ErrorActionPreference = "Stop"

# Define project paths
$SolutionFile = "VertexCache.sln"
$SdkProject = "sdk/VertexCache.Sdk.csproj"
$ClientProject = "client/VertexCache.Client.csproj"
$TestProject = "tests/VertexCache.Tests.csproj"

# Clean previous builds
Write-Host "Cleaning previous builds..."
dotnet clean $SolutionFile

Write-Host "Cleaning artifacts and temporary files..."
Remove-Item -Recurse -Force artifacts/ -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force sdk/bin/, sdk/obj/ -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force client/bin/, client/obj/ -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force tests/bin/, tests/obj/ -ErrorAction SilentlyContinue

Write-Host "Clean completed!"

# Restore dependencies
Write-Host "Restoring dependencies..."
dotnet restore $SolutionFile

Write-Host "Restoration completed!"

# Build SDK and Client
Write-Host "Building SDK and Client..."
dotnet build $SolutionFile --configuration Release

Write-Host "Build completed!"

# Run tests
Write-Host "Running tests..."
dotnet test $TestProject --configuration Release --no-build

Write-Host "Tests completed!"

# Run Client Application
Write-Host "Running Client Application..."
dotnet run --project $ClientProject

Write-Host "Execution completed!"
