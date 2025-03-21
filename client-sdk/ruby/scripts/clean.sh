#!/bin/bash
echo "Cleaning project..."

# Remove logs, temp files, and previous builds
find . -type f \( -name "*.log" -o -name "*.gem" -o -name "*.tmp" \) -delete
find . -type d -name "*.gem" -exec rm -rf {} +
find . -type d -name "pkg" -exec rm -rf {} +

echo "Project cleaned!"
