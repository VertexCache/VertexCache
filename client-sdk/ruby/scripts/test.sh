#!/bin/bash
echo "Running tests..."

bundle exec ruby sdk/test/test_vertex_cache_sdk.rb

echo "Testing complete!"
