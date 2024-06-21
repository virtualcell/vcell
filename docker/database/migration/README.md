### Initialize
see https://www.siriusopensource.com/en-us/blog/oracle-postgresql-migration-using-ora2pg

```bash
docker run --platform linux/amd64 -it \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration:/base \
    georgmoser/ora2pg:24.3 \
    ora2pg --project_base /base --init_project migv1
```

```bash
docker run --platform linux/amd64 -it \
    -w /base \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/migv1:/base \
    georgmoser/ora2pg:24.3 \
    ora2pg --type show_report --estimate_cost --conf config/ora2pg_all.conf --basedir data --dump_as_html > ora2pg.html
```

```bash
docker run --platform linux/amd64 -it \
    -w /base \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/migv1:/base \
    georgmoser/ora2pg:24.3 /bin/sh -c "cd /base && ./export_schema.sh" > export_schema.log
```

### exporting data
#### snapshot
--scn    SCN : Allow to set the Oracle System Change Number (SCN) to use to export data. It will be used in the WHERE clause to get the data. It is used with action COPY or INSERT.


```bash

docker run --platform linux/amd64 -it \
    -w /base \
    -v /Users/jimschaff/Documents/workspace/vcell/docker/database/migration/migv1:/base \
    georgmoser/ora2pg:24.3 \
    ora2pg --type table --where "ROWNUM < 100" --conf config/ora2pg_all.conf --basedir data --dump_as_html > ora2pg.html
```

