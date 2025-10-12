#!/bin/bash

# Usage: ./find_copy_java.sh <search_word> <source_dir> <destination_dir>
# Example: ./find_copy_java.sh MyClass ./src ./matched_files

SEARCH_WORD="$1"
SOURCE_DIR="$2"
DEST_DIR="$3"

# Create destination directory if it doesn't exist
mkdir -p "$DEST_DIR"

# Find .java files containing the search word and copy them
find "$SOURCE_DIR" -type f -name "*.java" -exec grep -q "$SEARCH_WORD" {} \; -exec cp --parents {} "$DEST_DIR" \;

echo "Files containing '$SEARCH_WORD' have been copied to '$DEST_DIR'."
