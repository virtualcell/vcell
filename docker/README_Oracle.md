# grab dump from VCell-db.cam.uchc.edu, windows 2008, Oracle 11g enterprise
### character set is WE8MSWIN1252

### export from vcell-db using datapump for vcell schema
in vcell-db database, named directory ```E_DATA_PUMP_DIR``` maps to ```e:\db```

scn = 1430710053 (compute from sql below)
```sql 
select current_scn from v$database
```
expdp system/<<password>>@vcelldborcl directory=E_DATA_PUMP_DIR dumpfile=vcelldb_dump_2023_08_19.dmp logfile=vcelldb_dump_2023_08_19.log schemas=vcell flashback_scn=1430710053

# build new oracle database image on vcell-oracle.cam.uchc.edu

### check out github repo for oracle docker containers
### download ```LINUX.X64_193000_db_home.zip``` and install in ./19.3.0/ directory
### build the container (from docker-images/OracleDatabase/SingleInstance/)

```bash
./buildContainerImage.sh -s -v 19.3.0 -o '--build-arg SLIMMING=false'
```
### tag and push image to container repository
```bash
docker tag <<image name>> ghcr.io/virtualcell/oracle-database:19.3.0-se2
docker push ghcr.io/virtualcell/oracle-database:19.3.0-se2
```

## create /data/oradata, /data/backup directories and set permissions to allow container oracle/dba rw access.

## running the container on vcell-oracle (for the first time)
```bash
sudo docker run --detach --restart=always \
    --name oracle-database \
    -p 1521:1521 -p 5500:5500 \
    --ulimit nofile=1024:65536 --ulimit nproc=2047:16384 --ulimit stack=10485760:33554432 --ulimit memlock=3221225472 \
    -e ORACLE_SID=ORCLCDB \
    -e ORACLE_PDB=ORCLPDB1 \
    -e ORACLE_PWD=<<password>> \
    -e INIT_CPU_COUNT=4 \
    -e ORACLE_EDITION=standard \
    -e ENABLE_TCPS=false \
    -e ORACLE_CHARACTERSET=WE8MSWIN1252 \
    -e ENABLE_ARCHIVELOG=true \
    -v /data/oradata:/opt/oracle/oradata \
    -v /data/backup:/opt/oracle/backup \
    ghcr.io/virtualcell/oracle-database:19.3.0-se2
```

```bash
sudo docker logs -f oracle-database
```

## register directory object EXT_DATA_PUMP_DIR for /opt/oracle/backup (host /data/backup)
```sql
create or replace directory EXT_DATA_PUMP_DIR as '/opt/oracle/backup';
```

## add user vcell (((Command doesn't work)))
sqlplus system/<<password>>@ORCLPDB1
```sql
create directory EXT_DATA_PUMP_DIR as '/opt/oracle/backup';
```

## run the import
```bash
docker exec -it oracle-database /bin/bash
impdp system/<<password>>@ORCLPDB1 schemas=vcell table_exists_action=REPLACE directory=EXT_DATA_PUMP_DIR dumpfile=FRMDEV2ORCL_VCELL_2023_08_19_17_05_08.DMP logfile=FRMDEV2ORCL_VCELL_2023_08_19_17_05_08.log

impdp system/<<password>>@ORCLPDB1 schemas=vcell table_exists_action=REPLACE directory=EXT_DATA_PUMP_DIR dumpfile=FRMDEV2ORCL_VCELL_2023_08_19_17_05_08.DMP logfile=FRMDEV2ORCL_VCELL_2023_08_19_17_05_08.log.3
```

