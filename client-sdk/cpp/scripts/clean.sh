#!/bin/bash
set -e

echo "🧹 Cleaning build directories..."
rm -rf build/ dist/

echo "🧹 Removing Docker images..."
docker image prune -f

echo "✅ Clean complete!"
