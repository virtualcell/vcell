#!/bin/bash

echo "Cleaning up old subfolders inside vcell-batch-job..."

# Explicitly remove individual folders
rm -rf ~/vcell-batch-job/input
rm -rf ~/vcell-batch-job/logs
rm -rf ~/vcell-batch-job/output
rm -rf ~/vcell-batch-job/scripts

# Recreate each directory separately
echo "Recreating folders: input/, logs/, output/, scripts/"
mkdir -p ~/vcell-batch-job/input
mkdir -p ~/vcell-batch-job/logs
mkdir -p ~/vcell-batch-job/output
mkdir -p ~/vcell-batch-job/scripts

echo "Project folders populated successfully."
