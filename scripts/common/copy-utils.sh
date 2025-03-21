#!/bin/bash

# Function to copy a file from source to destination
copy_file() {
    local SRC=$1
    local DEST=$2

    if [ -f "$SRC" ]; then
        cp "$SRC" "$DEST"
        echo "Copied '$SRC' to '$DEST' successfully."
    else
        echo "Error: '$SRC' does not exist!"
        exit 1
    fi
}
