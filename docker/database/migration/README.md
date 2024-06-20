### Initialize
see https://www.siriusopensource.com/en-us/blog/oracle-postgresql-migration-using-ora2pg

```bash
docker run  \
    --platform linux/amd64 \
    -it \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration:/instance1/ora2pg \
    georgmoser/ora2pg:24.3 \
    ora2pg --project_base /instance1/ora2pg --init_project migv1
    
```


```bash
export ORACLE_USER=system
docker run  \
    --platform linux/amd64 \
    -e ORACLE_USER=${ORACLE_USER} \
    -e ORACLE_PWD=${ORACLE_SECRET} \
    -it \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/config:/config \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/data:/data \
    georgmoser/ora2pg:24.3 \
    ora2pg --debug -c /config/ora2pg.conf > data/ora2pg.log
```

```bash
export ORACLE_USER=system
export ORACLE_SECRET=secret
docker run  \
    --platform linux/amd64 \
    -e ORACLE_USER=${ORACLE_USER} \
    -e ORACLE_PWD=${ORACLE_SECRET} \
    -it \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/config:/config \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/data:/data \
    georgmoser/ora2pg:24.3 \
    ora2pg --debug -c /config/ora2pg.conf -t TEST > data/ora2pg_TEST.log
```

```bash
cat ../../../vcell-rest/src/main/resources/scripts/init.sql | grep "CREATE TABLE" | tr "(" " " | cut -d' ' -f3 >> config/tables_to_export.txt
```

```bash
docker run  \
    --platform linux/amd64 \
    -e ORACLE_USER=${ORACLE_USER} \
    -e ORACLE_PWD=${ORACLE_SECRET} \
    -it \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/config:/config \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/data:/data \
    georgmoser/ora2pg:24.3 \
    ora2pg --debug -c /config/ora2pg.conf -t TABLE > data/ora2pg_TABLE.log
    
```

