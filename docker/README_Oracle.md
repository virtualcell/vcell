## VCell Database Notes
### database installation and data migration
* [data dump from retired 11g/Windows](#data-dump-from-retired-11gwindows)
* [build Oracle 19.3.0 Docker image](#build-oracle-1930-docker-image)
* [configure database host](#configure-database-host)
* [import database dump](#import-database-dump)

### database user and roles creation
* [Create vcell_role to access VCELL schema](#create-vcell_role-to-access-vcell-schema)
* [Create service users with role vcell_role](#create-service-users-with-role-vcell_role)
* [add triggers to set default schema upon login](#add-triggers-to-set-default-schema-upon-login)

### database user management
* [Unlock user vcell](#unlock-user-vcell)
* [Reset password for user vcell](#reset-password-for-user-vcell)
* [reset system passwords (if needed)](#reset-system-passwords-if-needed)
* [review password policy](#review-password-policy)
* [review vcell user account status](#review-vcell-user-account-status)
* [review system account status](#review-system-account-status)

===========================================================

## data dump from retired 11g/Windows
- VCell-db.cam.uchc.edu, windows 2008
- Oracle 11g enterprise
- character set is WE8MSWIN1252
- in vcell-db database, named directory ```E_DATA_PUMP_DIR``` maps to ```e:\db```

to get a snapshot, we need a scn (system change number) from the database.
scn = 1430710053 (compute from sql below)
```sql 
select current_scn from v$database
```
using this scn, export the vcell schema
```windows cmd
expdp system/<<password>>@vcelldborcl directory=E_DATA_PUMP_DIR dumpfile=vcelldb_dump_2023_08_19.dmp logfile=vcelldb_dump_2023_08_19.log schemas=vcell flashback_scn=1430710053
```

## build Oracle 19.3.0 Docker image
1. check out github repo for oracle docker containers
2. download ```LINUX.X64_193000_db_home.zip``` and install in ./19.3.0/ directory
3. build the container (from docker-images/OracleDatabase/SingleInstance/)

   ```bash
   ./buildContainerImage.sh -s -v 19.3.0 -o '--build-arg SLIMMING=false'
   ```
4. tag and push image to container repository
   ```bash
   docker tag <<image name>> ghcr.io/virtualcell/oracle-database:19.3.0-se2
   docker push ghcr.io/virtualcell/oracle-database:19.3.0-se2
   ```
   
## configure database host
1. create /data/oradata, /data/backup directories and set permissions to allow container oracle/dba rw access.
2. first time - run the container on host (e.g. vcell-oracle)
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
    check logs
    ```bash
    sudo docker logs -f oracle-database
    ```

3. register directory object EXT_DATA_PUMP_DIR for /opt/oracle/backup (host /data/backup)
    ```sql
    create or replace directory EXT_DATA_PUMP_DIR as '/opt/oracle/backup';
    ```

4. add user vcell (Command?)
5. set up the datapump directory for imports/exports
    ```bash
    sqlplus system/<<password>>@ORCLPDB1
    ```
    then in sqlplus:
    ```sql
    create directory EXT_DATA_PUMP_DIR as '/opt/oracle/backup';
    ```

## import database dump
```bash
docker exec -it oracle-database /bin/bash
```
then
```bash
impdp system/<<password>>@ORCLPDB1 schemas=vcell \
    table_exists_action=REPLACE directory=EXT_DATA_PUMP_DIR \
    dumpfile=FRMDEV2ORCL_VCELL_2023_08_19_17_05_08.DMP \
    logfile=FRMDEV2ORCL_VCELL_2023_08_19_17_05_08.log
```
===========================================================

# Database user and roles creation

## Create vcell_role to access VCELL schema
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
ALTER SESSION SET CONTAINER = ORCLPDB1;

CREATE ROLE vcell_role;

BEGIN

FOR r IN (SELECT table_name FROM all_tables WHERE owner = 'VCELL') LOOP
    EXECUTE IMMEDIATE 'GRANT SELECT, INSERT, UPDATE, DELETE ON VCELL.' || r.table_name || ' TO vcell_role';
END LOOP;

FOR r IN (SELECT object_name FROM all_procedures WHERE owner = 'VCELL' AND object_type IN ('PROCEDURE', 'FUNCTION', 'PACKAGE', 'PACKAGE BODY')) LOOP
BEGIN
EXECUTE IMMEDIATE 'GRANT EXECUTE ON VCELL.' || r.object_name || ' TO vcell_role';
EXCEPTION
      WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error granting EXECUTE on ' || r.object_name || ': ' || SQLERRM);
END;
END LOOP;

FOR r IN (SELECT sequence_name FROM all_sequences WHERE sequence_owner = 'VCELL') LOOP
BEGIN
EXECUTE IMMEDIATE 'GRANT SELECT ON VCELL.' || r.sequence_name || ' TO VCELL_ROLE';
EXECUTE IMMEDIATE 'GRANT USAGE ON VCELL.' || r.sequence_name || ' TO VCELL_ROLE';
EXCEPTION WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error granting access to sequence ' || r.sequence_name || ': ' || SQLERRM);
END;
END LOOP;

END;
/

GRANT CREATE SESSION TO vcell_role;

```

## Create service and dev users with role vcell_role
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
ALTER SESSION SET CONTAINER = ORCLPDB1;

CREATE USER vcell_service IDENTIFIED BY strong_password_1;
ALTER USER vcell_service QUOTA UNLIMITED ON USERS;
GRANT vcell_role TO vcell_service;

CREATE USER vcell_dev IDENTIFIED BY strong_password_2;
ALTER USER vcell_dev QUOTA UNLIMITED ON USERS;
GRANT vcell_role TO vcell_dev;

-- not sure if need to commit
commit;  
```

## add triggers to set default schema upon login
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
CREATE OR REPLACE TRIGGER set_vcell_service_schema
AFTER LOGON ON vcell_service.SCHEMA
BEGIN
EXECUTE IMMEDIATE 'ALTER SESSION SET CURRENT_SCHEMA = VCELL';
END;
/

CREATE OR REPLACE TRIGGER set_vcell_dev_schema
AFTER LOGON ON vcell_dev.SCHEMA
BEGIN
EXECUTE IMMEDIATE 'ALTER SESSION SET CURRENT_SCHEMA = VCELL';
END;
/
```

# Database user maintenance

## Unlock user vcell
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
ALTER SESSION SET CONTAINER = ORCLPDB1;
ALTER USER vcell ACCOUNT UNLOCK;
```

## Reset password for user vcell
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
ALTER SESSION SET CONTAINER = ORCLPDB1;
ALTER USER vcell IDENTIFIED BY <<new password>>;
```

## reset system passwords (if needed)
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
ALTER USER sys IDENTIFIED BY <<new password>>;
ALTER USER system IDENTIFIED BY <<new password>>;
```

## review password policy
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
SELECT PROFILE, RESOURCE_NAME, LIMIT from DBA_PROFILES WHERE RESOURCE_NAME like 'PASSWORD%' order by PROFILE;
```
note that 'DEFAULT' profile is for regular users like 'vcell' and 'ORA_STIG_PROFILE' is for system users like 'SYS' and 'SYSTEM'

## review vcell user account status
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
ALTER SESSION SET CONTAINER = ORCLPDB1;
SELECT ACCOUNT_STATUS, EXPIRY_DATE FROM DBA_USERS WHERE USERNAME = 'VCELL';
```

## review system account status
from within the container
```bash
sqlplus / as sysdba
```
then
```sql
SELECT ACCOUNT_STATUS, EXPIRY_DATE from DBA_USERS where USERNAME = 'SYSTEM' or USERNAME = 'SYS';
```
