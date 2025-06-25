#!/bin/bash
set -e

source ./scripts/common/copy-utils.sh

echo
echo "=============================================================================="
echo "Build, Compile VertexCache Server and Console Client...                      ="
echo "=============================================================================="
mvn clean package -Dmaven.test.skip=true

echo
echo "=============================================================================="
echo "= init_config invoked...                                                     ="
echo "=============================================================================="
echo
source ./scripts/init_config.sh

echo
echo "=============================================================================="
echo "= Initialize dist folder for VertexCache                                     ="
echo "=============================================================================="
echo
rm -rf ./dist-vertex-cache

cp -r ./etc/dist-shell-vertex-cache ./dist-vertex-cache

# Note ./vertex-cache-config is probably assembed by init_config.sh which why we copy from here instead
cp -r ./vertex-cache-config ./dist-vertex-cache/vertex-cache-config

cp ./vertex-cache-server/target/vertex-cache-server-*.jar ./dist-vertex-cache/vertex-cache-server.jar
cp ./vertex-cache-console/target/vertex-cache-console-*.jar ./dist-vertex-cache/vertex-cache-console.jar

echo
echo "=============================================================================="
echo "= Initialize dist folder for VertexBench                                     ="
echo "=============================================================================="
echo
rm -rf ./dist-vertex-bench

cp -r ./etc/dist-shell-vertex-bench ./dist-vertex-bench
cp ./vertex-bench/target/vertex-bench-*.jar ./dist-vertex-bench/vertex-bench.jar

echo "DONE"