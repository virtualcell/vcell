# build VCell Client installers
required files:

Java jre bundles which are compatible with installed version of
Install4J
linux-amd64-1.8.0_66.tar.gz
macosx-amd64-1.8.0_66.tar.gz
windows-x86-1.8.0_66.tar.gz
linux-x86-1.8.0_66.tar.gz	
windows-amd64-1.8.0_66.tar.gz

## for dev
edit docker-compose-dev.yml for parameters (e.g. build version) and jre location

```bash
docker-compose -f docker-compose-dev.yml build
docker-compose -f docker-compose-dev.yml up
docker-compose -f docker-compose-dev.yml rm
```

## to rebuild a tag from github
edit docker-compose.yml for parameters (e.g. build version, DockerHub tag) and jre location

```bash
docker-compose -f docker-compose.yml build
docker-compose -f docker-compose.yml up
docker-compose -f docker-compose.yml rm
```

