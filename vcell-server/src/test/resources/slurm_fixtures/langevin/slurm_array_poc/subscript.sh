#!/bin/bash

# ensure the script receives the required argument
if [ -z "$1" ]; then
    echo "Error: No job index provided."
    exit 1
fi

job_index=$1
input_file="dynamic.langevinInput"  # file containing job details
output_file="subscript_${job_index}.output"

# locate the row with the matching job index
read duration exit_code < <(awk -v idx="$job_index" '$1 == idx {print $2, $3}' "$input_file")

# ensure the job index is found
if [ -z "$duration" ] || [ -z "$exit_code" ]; then
    echo "Error: No matching job entry for index $job_index"
    exit 1
fi

# log job start
echo "Job $job_index started at $(date)" > "$output_file"

# simulate workload based on parsed duration
sleep "$duration"

# log job completion
echo "Job $job_index finished at $(date) with exit code $exit_code" >> "$output_file"

# exit with the specified exit code
exit "$exit_code"
