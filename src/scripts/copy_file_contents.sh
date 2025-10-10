#!/bin/bash
# copy_file_contents.sh
# This script prints all files matching a pattern under the current directory,
# their contents, separates them with a line, and copies the output to the Windows clipboard.
# Usage: ./copy_file_contents.sh [pattern]
# Example: ./copy_file_contents.sh "*.j*"  # finds .java, .json, etc.

# Check if clip.exe is available
if ! command -v clip.exe &> /dev/null; then
    echo "Error: clip.exe not found. Are you running this in WSL?"
    exit 1
fi

# Set file pattern: use argument if provided, otherwise default to "*"
PATTERN="${1:-*}"

# Iterate over matching files safely and copy to clipboard
find . -type f -name "$PATTERN" -print0 | while IFS= read -r -d '' file; do
    echo "$file:"
    cat "$file"
    echo "-----------------------"
done | clip.exe

echo "All matching file contents copied to clipboard!"
