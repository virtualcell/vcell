# cronjob script 

## cron_vcell.sh created in `/home/CAM/crbmapi/cron_scripts/` of crbmapi service user and cron job is executed on the cron_vcell.sh
### The script builds the singularity image for VCELL simulator from the docker image stored on docker hub
```
0 0,13 * * * source /home/CAM/crbmapi/cron_scripts/cron_vcell.sh
```

cron doesn't load `$module load singularity` because  it has its separate environment. 
Inside script, absolute path is provided to avoid any confusions
