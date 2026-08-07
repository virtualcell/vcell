# Migration from Oracle to PostgreSQL

## Create local snapshot of Oracle database
download ARM64 19c from https://www.oracle.com/database/technologies/oracle19c-linux-arm64-downloads.html
clone Oracle's https://github.com/oracle/docker-images.git
```bash
cd docker-images/OracleDatabase/SingleInstance/dockerfiles
mv LINUX.ARM64_1919000_db_home.zip 19.3.0/
./buildContainerImage.sh -v 19.3.0 -e
docker tag oracle/database:19.3.0-ee ghcr.io/virtualcell/oracle-database:19.3.0-ee-arm64
docker push ghcr.io/virtualcell/oracle-database:19.3.0-ee-arm64
docker run -d --name oracle -e ORACLE_PWD=YourPass321 oracle/database:19.3.0-ee
```

```bash
### configure local Oracle using container

#### specify disk space for Oracle data
```bash
export ORACLE_DATA=/Users/jimschaff/oracle-local/oradata
export ORACLE_BACKUP=/Users/jimschaff/oracle-local/backup
```

#### download vcell database backup archive
```bash
export BACKUP_DUMP_FILE=orclpdb1_2024_06_21_05_00_02.dmp.gz
export BACKUP_LOG_FILE=orclpdb1_2024_06_21_05_00_02.log
#scp vcell@vcellapi.cam.uchc.edu:/home/FCAM/vcell/database_backups/$BACKUP_DUMP_FILE ${ORACLE_BACKUP}/$BACKUP_DUMP_FILE
#scp vcell@vcellapi.cam.uchc.edu:/home/FCAM/vcell/database_backups/$BACKUP_LOG_FILE ${ORACLE_BACKUP}/$BACKUP_LOG_FILE
scp host:/path/to/backups/$BACKUP_DUMP_FILE ${ORACLE_BACKUP}/$BACKUP_DUMP_FILE
scp host:/path/to/backups/$BACKUP_LOG_FILE ${ORACLE_BACKUP}/$BACKUP_LOG_FILE
gunzip ${ORACLE_BACKUP}/$BACKUP_FILE


```

```bash
export LOCAL_ORA_PSWD=tiger
docker run --detach --restart=always \
--name oracle-database \
-p 1521:1521 -p 5500:5500 \
--ulimit nofile=1024:65536 --ulimit nproc=2047:16384 --ulimit stack=10485760:33554432 --ulimit memlock=3221225472 \
-e ORACLE_SID=ORCLCDB \
-e ORACLE_PDB=ORCLPDB1 \
-e ORACLE_PWD=${LOCAL_ORA_PSWD} \
-e INIT_CPU_COUNT=4 \
-e ORACLE_EDITION=enterprise \
-e ENABLE_TCPS=false \
-e ORACLE_CHARACTERSET=WE8MSWIN1252 \
-e ENABLE_ARCHIVELOG=true \
-v ${ORACLE_DATA}:/opt/oracle/oradata \
-v ${ORACLE_BACKUP}:/opt/oracle/backup \
ghcr.io/virtualcell/oracle-database:19.3.0-ee-arm64
```
check logs
```bash
docker logs -f oracle-database
docker exec -it oracle-database /bin/bash
sqlplus system/tiger@ORCLPDB1
> create or replace directory EXT_DATA_PUMP_DIR as '/opt/oracle/backup';
> exit
impdp system/tiger@ORCLPDB1 schemas=vcell \
    table_exists_action=REPLACE directory=EXT_DATA_PUMP_DIR \
    dumpfile=orclpdb1_2024_06_21_05_00_02.dmp \
    logfile=orclpdb1_2024_06_21_05_00_02.log
```

### Initialize
see https://www.siriusopensource.com/en-us/blog/oracle-postgresql-migration-using-ora2pg

#### Create a new migration project

```bash
docker run --platform linux/amd64 -it \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration:/base \
    georgmoser/ora2pg:24.3 \
    ora2pg --project_base /base --init_project migv1
```

#### Estimate costs of migration

```bash
docker run --platform linux/amd64 -it \
    -w /base \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/migv1:/base \
    georgmoser/ora2pg:24.3 \
    ora2pg --type show_report --estimate_cost --conf config/ora2pg_all.conf --basedir data --dump_as_html > ora2pg.html
```

#### Run export_schema.sh

```bash
docker run --platform linux/amd64 -it \
    -w /base \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/migv1:/base \
    georgmoser/ora2pg:24.3 \
    /bin/sh -c "cd /base && ./export_schema.sh" > export_schema.log
```

### exporting data
#### Export all data
To extract data use the following command:

ora2pg -t COPY -o data.sql -b /base/data -c /base/config/ora2pg.conf

#### snapshot
--scn    SCN : Allow to set the Oracle System Change Number (SCN) to use to export data. It will be used in the WHERE clause to get the data. It is used with action COPY or INSERT.

#### disable triggers

```bash
disable_triggers.sh
```

```bash

docker run --platform linux/amd64 -it \
    -w /base \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/migv1:/base \
    georgmoser/ora2pg:24.3 \
    /bin/sh -c "cd /base && ora2pg --debug --type COPY -o data.sql --conf config/ora2pg_filtered.conf --basedir data" > copy_local.log
        
```

