#!/bin/bash

echo "Cleaning up old subfolders inside vcell-batch-job..."

DEST_BASE="/home/vasilescu/vcell-batch-job"
SRC_BASE="/mnt/c/dan/projects/git/vcell/vcell-server/src/test/resources/slurm_fixtures/langevin"

# Explicitly remove individual folders
rm -rf "$DEST_BASE/input"
rm -rf "$DEST_BASE/logs"
rm -rf "$DEST_BASE/output"
rm -rf "$DEST_BASE/scripts"

# Recreate each directory separately
echo "Recreating folders: input/, logs/, output/, scripts/"
mkdir -p "$DEST_BASE/input"
mkdir -p "$DEST_BASE/logs"
mkdir -p "$DEST_BASE/output"
mkdir -p "$DEST_BASE/scripts"

# Copy files into the correct folders
echo "Copying files..."
cp "$SRC_BASE/SimID_999999999_0_.langevinInput" "$DEST_BASE/input/"
cp "$SRC_BASE/SimID_999999999_0__0.simtask.xml" "$DEST_BASE/input/"
cp "$SRC_BASE/slurm_array_poc/submit_vcell_batch.slurm.sub" "$DEST_BASE/scripts/"

# Normalize line endings for WSL
dos2unix "$DEST_BASE/scripts/submit_vcell_batch.slurm.sub" > /dev/null 2>&1

echo "Project folders populated successfully."
