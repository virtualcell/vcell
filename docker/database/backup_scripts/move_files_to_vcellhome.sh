#!/bin/bash

# compress all *.dmp files in /data/backup
cd /data/backup || echo "failed to cd to /data/backup" || exit 1
gzip *.dmp

# move
mv /data/backup/*.log ~vcell/database_backups
mv /data/backup/*.dmp.gz ~vcell/database_backups
