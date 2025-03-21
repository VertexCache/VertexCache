#!/bin/bash
set -e

echo "🧹 Cleaning dist, logs/, vertex-cache-config directories..."
rm -rf vertex-cache-config/ logs/ dist/


echo "🧹 Cleaning VertexCache Server and VertexCache Console Client builds..."
mvn clean

echo "✅ Clean complete!"
