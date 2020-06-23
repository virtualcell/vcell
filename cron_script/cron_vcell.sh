#!/bin/bash -l

/isg/shared/apps/singularity/3.5.2/bin/singularity build /home/CAM/crbmapi/vcell_latest.img docker://crbm/biosimulations_vcell_api:latest <<<y  1> /home/CAM/crbmapi/cron_scripts/cron_logs/cron_vcell.log 2>&1 &