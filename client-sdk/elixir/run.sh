#!/bin/bash

set -e  # Exit on error

echo "Running VertexCacheClient..."

# Navigate to client and execute the run function
cd client
mix run -e "VertexCacheClient.run()"

echo "Execution complete!"
