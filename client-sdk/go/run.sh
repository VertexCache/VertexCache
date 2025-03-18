#!/bin/bash
set -e  # Exit on any error

echo "Running Go client application..."

# Navigate to the client directory and run the application
cd client
go run .

echo "Application finished!"
