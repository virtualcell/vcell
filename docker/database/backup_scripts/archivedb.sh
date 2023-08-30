#!/bin/bash

# run export of oracle 19c database running in the container named 'oracle-database' using the Data Pump 'expdp' command
sudo docker exec -it oracle-database /opt/oracle/backup/scripts/run.sh 2>&1 | tee -a ../archivedb_$(date +%Y_%m_%d_%H_%M_%S).log

# copy move dump file to remote storage
echo "move dump files to remote storage - not yet mounted - do this manually"
