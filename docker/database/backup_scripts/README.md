## Nightly Oracle database backups

###  Data Pump dump to /data/backup
cron job runs `archivedb.sh` every 1:00am as `root`

```
0  1  * * * /data/backup/scripts/archivedb.sh >> /data/backup/backup.cron-log 2>&1
```

### Compress and Archive to ~vcell/database_backups
cron job runs `move_files_to_vcellhome.sh` every 9:00pm as `vcell`

```
0  21 * * * /data/backup/scripts/move_files_to_vcellhome.sh >> /data/backup/move_files.cron-log 2>&1
```
