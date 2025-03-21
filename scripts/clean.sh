#!/bin/bash
set -e

echo "🧹 Cleaning dist, vertex-cache-config directories..."
rm -rf vertex-cache-config/ dist/


echo "🧹 Cleaning VertexCache Server and VertexCache Console Client builds..."
mvn clean

echo "✅ Clean complete!"
