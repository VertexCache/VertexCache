#!/bin/bash

# Resolve SDK directory
SDK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/sdk"
cd "$SDK_DIR" || {
    echo "Error: Failed to enter $SDK_DIR"
    exit 1
}

if [[ "$1" == "--with-live" ]]; then
    echo "---------------------------------------------------"
    echo "Running live test only: VertexCacheSDKLiveTest"
    echo "VC_LIVE_TEST=true"
    echo "---------------------------------------------------"

    VC_LIVE_TEST=true mvn clean -Dtest=VertexCacheSDKLiveTest test \
      -Dsurefire.printSummary=true \
      -Dsurefire.useFile=false

    if [[ $? -eq 0 ]]; then
        echo "[VertexCache Java SDK] Live test PASSED"
    else
        echo "[VertexCache Java SDK] Live test FAILED"
        exit 1
    fi

else
    echo "---------------------------------------------------"
    echo "Running full test suite (unit tests only)"
    echo "---------------------------------------------------"

    mvn clean test \
      -Dsurefire.printSummary=true \
      -Dsurefire.useFile=false

    if [[ $? -eq 0 ]]; then
        echo "[VertexCache Java SDK] Unit tests PASSED"
    else
        echo "[VertexCache Java SDK] Unit tests FAILED"
        exit 1
    fi
fi
