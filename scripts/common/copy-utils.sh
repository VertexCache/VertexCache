#!/bin/bash

# Utility function: copies a file from $1 (src) to $2 (dest), ensures dest dir exists
copy_file() {
    local src="$1"
    local dest="$2"
    local dest_dir
    dest_dir=$(dirname "$dest")

    if [ ! -f "$src" ]; then
        echo "❌ Error: Source file does not exist: $src"
        return 1
    fi

    if [ ! -d "$dest_dir" ]; then
        echo "📂 Creating destination directory: $dest_dir"
        mkdir -p "$dest_dir"
    fi

    cp -f "$src" "$dest"
    echo "✅ Copied $(basename "$src") to $dest (overwritten if it existed)"
}
