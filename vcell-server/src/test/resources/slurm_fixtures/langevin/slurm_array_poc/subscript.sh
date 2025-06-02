#!/bin/bash

# Ensure the script receives the required argument
if [ -z "$1" ]; then
    echo "Error: No job index provided."
    exit 1
fi

job_index=$1
input_file="dynamic.langevinInput"  # File containing job details
output_file="subscript_${job_index}.output"

# Locate the row with the matching job index
read duration exit_code < <(awk -v idx="$job_index" '$1 == idx {print $2, $3}' "$input_file")

# Ensure the job index is found
if [ -z "$duration" ] || [ -z "$exit_code" ]; then
    echo "Error: No matching job entry for index $job_index"
    exit 1
fi

# Log job start
echo "Job $job_index started at $(date)" > "$output_file"

# Simulate workload based on parsed duration
sleep "$duration"

# Log job completion
echo "Job $job_index finished at $(date) with exit code $exit_code" >> "$output_file"

# Exit with the specified exit code
exit "$exit_code"
