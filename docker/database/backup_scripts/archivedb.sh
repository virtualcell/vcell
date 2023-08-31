#!/bin/bash

# run export of oracle 19c database running in the container named 'oracle-database' using the Data Pump 'expdp' command
sudo docker exec oracle-database /opt/oracle/backup/scripts/run.sh 2>&1 | tee -a /data/backup/archivedb_$(date +%Y_%m_%d_%H_%M_%S).log

# change permission so that vcell can move them in a separate script and cron job
chmod 666 /data/backup/*.log
chmod 666 /data/backup/*.dmp

# copy move dump file to remote storage
echo "vcell runs separate script to archive to ~/vcell/database_backups"