#!/bin/bash

# ensure the script receives the required argument
if [ -z "$1" ]; then
    echo "Error: No job index provided."
    exit 1
fi

job_index=$1
output_file="subscript_${job_index}.output"

# log job start
echo "Job $job_index start at $(date)" > "$output_file"

# simulating workload (replace with actual job logic)
sleep $(( job_index + 1 ))  # variable sleep time to simulate different workloads

# log job completion
echo "Job $job_index finished at $(date)" >> "$output_file"

exit 0

