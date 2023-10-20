#!/bin/bash

echo ""
echo "========= starting move of database backups at $(date) ========"

cd /data/backup || echo "failed to cd to /data/backup" || exit 1

echo ""
echo "---- before compressing/moving files ----"
ls -oth
echo "-----------------------------------------"
echo ""

echo "compressing all .dmp files in /data/backup"
gzip ./*.dmp

# move
echo "moving all .log and .dmp.gz files to ~/vcell/database_backups"
mv /data/backup/*.log ~vcell/database_backups
mv /data/backup/*.dmp.gz ~vcell/database_backups

echo ""
echo "---- after compressing/moving files ----"
ls -oth
echo "-----------------------------------------"
echo ""

echo "========== done moving files $(date) ========================"
echo ""
