```bash
export ORACLE_USER=system
export ORACLE_SECRET=secret
docker run  \
    --platform linux/amd64 \
    --name ora2pg \
    -e CONFIG_LOCATION=/config/ora2pg.conf  \
    -e OUTPUT_LOCATION=/data/myfolder  \
    -e ORA_HOST=dbi:Oracle:host=vcell-oracle.cam.uchc.edu;service_name=ORCLPDB1;port=1521  \
    -e ORA_USER=${ORACLE_USER}  \
    -e ORA_PWD=${ORACLE_SECRET}  \
    -it \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/config:/config \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/data:/data \
    georgmoser/ora2pg:24.3 \
    ora2pg --debug -c /config/ora2pg.conf
```