# Stop script on first error
$ErrorActionPreference = "Stop"

# Define project paths
$SolutionFile = "VertexCache.sln"
$SdkProject = "sdk/VertexCache.Sdk.csproj"
$ClientProject = "client/VertexCache.Client.csproj"
$TestProject = "tests/VertexCache.Tests.csproj"

# Function to print messages in color
function Write-Info {
    param ([string]$Message)
    Write-Host $Message -ForegroundColor Cyan
}

# Clean previous builds
Write-Info "Cleaning previous builds..."
dotnet clean $SolutionFile

# Restore dependencies
Write-Info "Restoring dependencies..."
dotnet restore $SolutionFile

# Build SDK and Client
Write-Info "Building SDK and Client..."
dotnet build $SolutionFile --configuration Release

# Run tests
Write-Info "Running tests..."
dotnet test $TestProject --configuration Release --no-build

# Pack SDK (for NuGet)
Write-Info "Packing SDK..."
dotnet pack $SdkProject --configuration Release --no-build --output ./artifacts

Write-Info "Build completed successfully!"
