#!/bin/bash

# Function to ask for confirmation before proceeding
confirm_execution() {
    local message="$1"  # Accept a custom message as an argument

    # Default message if none is provided
    if [ -z "$message" ]; then
        message="Are you sure you want to continue? (yes/no): "
    fi

    read -p "$message" CONFIRMATION

    # Convert input to lowercase
    CONFIRMATION=$(echo "$CONFIRMATION" | tr '[:upper:]' '[:lower:]')

    # Check if the user entered "yes"
    if [[ "$CONFIRMATION" != "yes" ]]; then
        echo "Operation canceled."
        exit 0
    fi
}
