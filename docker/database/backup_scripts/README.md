## Nightly Oracle database backups

###  Data Pump dump to /data/backup
cron job runs `archivedb.sh` every 1:00am as `root`

### Compress and Archive to ~vcell/database_backups
cron job runs `move_files_to_vcellhome.sh` every 9:00pm as `vcell`

