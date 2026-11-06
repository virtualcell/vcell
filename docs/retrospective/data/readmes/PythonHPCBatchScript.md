# UCONN CCAM Batch-run OMEX files through HPC Center Script

### Goal
Automatically create slurm job to process a number of omex archives in a specified directory

### Requirements
Python 3
Slurm
VCell
File Permissions

### Installation
Drag the files to wherever you want to store the script

### Use
Execute "biosim_batch_exec.py" with proper arguments (see: "python3 biosim_batch_exec.py --help") to launch VCell processing slurm jobs for all omex files
If for any reason you want the previous batch killed, execute "biosim_batch_kill.py"
## !IMPORTANT! Executing biosim_batch_exec.py will overrite resources to kill already running batch jobs made by this script! Kill jobs before re-running script or manually kill jobs if needed

