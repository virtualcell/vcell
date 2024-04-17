### test database (local/ephemeral PostgreSQL)
* used during Quarkus test mode
* launched by devservices (using test containers)
* ephemeral data - database is destroyed after test run
* schema is bootstrapped via init.sql (see `%test.quarkus.datasource.devservices.init-script-path=scripts/init.sql` in `application.properties`)

### dev database (local/persistent PostrgeSQL)
* used during Quarkus dev mode (e.g. `quarkus dev`)
* launched via ./scripts/start_dev_postgresql.sh
* data stored to container - (mount `/var/lib/postgresql/data` to local volume if want to persist).
* schema is bootstrapped via init.sql (see `%test.quarkus.datasource.devservices.init-script-path=scripts/init.sql` in `application.properties`)

### prod database (external/persistent Oracle)
* used during Quarkus prod mode (e.g. `quarkus build` then `java -jar target/vcell-rest-1.0.0-SNAPSHOT-runner.jar`)