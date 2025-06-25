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
echo "= Initialize dist folder                                                     ="
echo "=============================================================================="
echo
rm -rf ./dist

cp -r ./etc/dist-shell ./dist


cp -r ./vertex-cache-config ./dist/vertex-cache-config



cp ./vertex-cache-server/target/vertex-cache-server-*.jar ./dist/vertex-cache-server.jar

cp ./vertex-cache-console/target/vertex-cache-console-*.jar ./dist/vertex-cache-console.jar

echo "done"