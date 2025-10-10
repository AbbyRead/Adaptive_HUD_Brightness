#!/bin/bash
# Usage: ./dedup.sh input_file max_single max_pair
# Example: ./dedup.sh myfile.txt 3 3

if [ "$#" -ne 3 ]; then
    echo "Usage: $0 input_file max_single max_pair"
    exit 1
fi

input_file="$1"
max_single="$2"
max_pair="$3"

# Step 1: Remove lines repeating more than max_single times consecutively
awk -v max="$max_single" '
{
    if ($0 == prev) {
        count++
    } else {
        count = 1
    }
    if (count <= max) {
        print
    }
    prev = $0
}' "$input_file" > temp_single.txt

# Step 2: Remove repeated two-line pairs more than max_pair times consecutively
awk -v max="$max_pair" '
{
    pair = prev "\n" $0
    if (NR > 1) {
        if (pair == last_pair) {
            pair_count++
        } else {
            pair_count = 1
        }
        if (pair_count <= max) {
            print prev
        }
    }
    prev = $0
    last_pair = pair
}
END {
    if (NR > 0) print prev
}' temp_single.txt

rm -f temp_single.txt
