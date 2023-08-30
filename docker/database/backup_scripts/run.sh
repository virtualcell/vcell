#!/bin/bash
# export the oracle 19c database running in the container named 'oracle-database' using the Data Pump 'expdp' command
# the export is written to the directory /data/backup on the database host with a timestamped .dmp file and .log file
#
# this script (archive.sh) is to be placed in /data/backup/scripts on the database host, along with newscn.sql and dbpswd.txt
# usage
#     sudo docker exec -it oracle-database /opt/oracle/backup/scripts/run.sh
#   or
#     sudo docker exec -it oracle-database /opt/oracle/backup/scripts/run.sh 2>&1 | tee -a archivedb_$(date +%Y_%m_%d_%H_%M).log
#

cd /opt/oracle/backup/scripts || echo "failed to cd to /opt/oracle/backup/scripts" || exit 1

dateString=$(date +%Y_%m_%d_%H_%M_%S)
logfile=orclpdb1_${dateString}.log
dumpfile=orclpdb1_${dateString}.dmp

password=$(cat /opt/oracle/backup/scripts/password.txt)

# data the database SCN (system change number) at the time of the export
SCN=$(sqlplus -S -L "system/${password}@ORCLPDB1" @newscn.sql | xargs) || echo "error getting scn" || exit 1
echo SCN is "'${SCN}'"

# dump to EXT_DATA_PUMP_DIR /opt/oracle/backup mounted to host /data/backup with timestamped .dmp and .log files
expdp \
  "system/${password}@localhost:1521/ORCLPDB1" \
  directory=EXT_DATA_PUMP_DIR \
  dumpfile="${dumpfile}" \
  logfile="${logfile}" \
  schemas=vcell \
  flashback_scn="${SCN}"

expdp_returnCode=$?
if [ $expdp_returnCode != 0 ]; then
  echo "Error exporting database"
  exit 1
else
  echo "database dump to ${dumpfile} complete, please check logs in ${logfile}"
  exit 0
fi
