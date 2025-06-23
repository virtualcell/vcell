#!/bin/bash

echo "Cleaning up old subfolders inside vcell-batch-job..."

# Define base path
DEST_BASE="/home/vasilescu/vcell-batch-job"

# Remove specific subfolders
rm -rf "$DEST_BASE/input"
rm -rf "$DEST_BASE/logs"
rm -rf "$DEST_BASE/output"
rm -rf "$DEST_BASE/scripts"

# Recreate cleaned subfolders
echo "Recreating folders: input/, logs/, output/, scripts/"
mkdir -p "$DEST_BASE/input" "$DEST_BASE/logs" "$DEST_BASE/output" "$DEST_BASE/scripts"

# Source files from Windows-side repo
SRC_BASE="/mnt/c/dan/projects/git/vcell/vcell-server/src/test/resources/slurm_fixtures/langevin"

# Copy simulation and Slurm script files
echo "Copying files..."
cp "$SRC_BASE/SimID_999999999_0_.langevinInput" "$DEST_BASE/input/"
cp "$SRC_BASE/SimID_999999999_0__0.simtask.xml" "$DEST_BASE/input/"
cp "$SRC_BASE/slurm_array_poc/submit_vcell_batch.slurm.sub" "$DEST_BASE/scripts/"

echo "Project folders populated successfully."
