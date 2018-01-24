# deploy vcell using docker swarm mode.

## cluster configuration (<vcellroot>/docker)

1) shared folder for simulation results, singularity images, SLURM submit scripts and logs
2) set up docker swarm mode

```bash
sudo docker swarm init
sudo docker node update --label-add zone=INTERNAL `docker node ls -q`
```

3) deploy a vcell production stack (called "local") on a single machine Docker swarm mode

```bash
./localconfig.sh test 7.0.0-alpha.4 7.0.0 4 local.config
sudo env $(cat local.config | xargs) docker stack deploy -c docker-compose-swarm.yml local
```

or deploy a development version using docker-compose

```bash
cd <vcellroot>
mvn clean install dependency:copy-dependencies
cd docker
./localconfig.sh test 7.0.0-alpha.4 7.0.0 4 local.config
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-build.yml rm
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-build.yml build
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-build.yml up
```

4) useful commands

```bash
> sudo docker stack services local
ID                  NAME                MODE                REPLICAS            IMAGE                               PORTS
5hrlf3u1kdgr        local_visualizer    replicated          1/1                 dockersamples/visualizer:stable     *:30013->8080/tcp
cflee8p55yph        local_mongodb       replicated          1/1                 schaff/vcell-mongo:7.0.0-alpha.4    *:27020->27017/tcp
evpnt4b10dbs        local_activemq      replicated          1/1                 webcenter/activemq:5.14.3           *:61619->61616/tcp
tpo0wfjmgqeo        local_api           replicated          1/1                 schaff/vcell-api:7.0.0-alpha.4      *:30014->8000/tcp,*:8083->8080/tcp
xkb1tp4znjha        local_master        replicated          1/1                 schaff/vcell-master:7.0.0-alpha.4   *:30012->8000/tcp
```

```bash
> sudo docker stack ps local
ID               NAME                   IMAGE                               NODE                    DESIRED STATE   CURRENT STATE           ERROR  PORTS
mlvxxlvxedvh     local_master.1         schaff/vcell-master:7.0.0-alpha.4   linuxkit-025000000001   Running         Running 4 minutes ago
z164vsxiorox     local_api.1            schaff/vcell-api:7.0.0-alpha.4      linuxkit-025000000001   Running         Running 4 minutes ago
y0v50e8r31ce      \_ local_api.1        schaff/vcell-api:7.0.0-alpha.4      linuxkit-025000000001   Shutdown        Shutdown 4 minutes ago
9pjpi1lvi46r     local_visualizer.1     dockersamples/visualizer:stable     linuxkit-025000000001   Running         Running 25 minutes ago
3gdwdlijfxdz     local_mongodb.1        schaff/vcell-mongo:7.0.0-alpha.4    linuxkit-025000000001   Running         Running 26 minutes ago
k2hyfkcyqwpx     local_activemq.1       webcenter/activemq:5.14.3           linuxkit-025000000001   Running         Running 26 minutes ago
w059r4hbdgq9     local_master.1         schaff/vcell-master:7.0.0-alpha.4   linuxkit-025000000001   Shutdown        Shutdown 4 minutes ago
```

```bash
> sudo deploy schaff$ docker service ls
ID                  NAME                MODE                REPLICAS            IMAGE                               PORTS
evpnt4b10dbs        local_activemq      replicated          1/1                 webcenter/activemq:5.14.3           *:61619->61616/tcp
tpo0wfjmgqeo        local_api           replicated          1/1                 schaff/vcell-api:7.0.0-alpha.4      *:30014->8000/tcp,*:8083->8080/tcp
xkb1tp4znjha        local_master        replicated          1/1                 schaff/vcell-master:7.0.0-alpha.4   *:30012->8000/tcp
cflee8p55yph        local_mongodb       replicated          1/1                 schaff/vcell-mongo:7.0.0-alpha.4    *:27020->27017/tcp
5hrlf3u1kdgr        local_visualizer    replicated          1/1                 dockersamples/visualizer:stable     *:30013->8080/tcp
```

```bash
> docker service logs -f local_api
```


# build VCell Client installers (this uses docker-compose rather than docker stack)
required files:

Java jre bundles which are compatible with installed version of
Install4J
linux-amd64-1.8.0_66.tar.gz
macosx-amd64-1.8.0_66.tar.gz
windows-x86-1.8.0_66.tar.gz
linux-x86-1.8.0_66.tar.gz	
windows-amd64-1.8.0_66.tar.gz

## for dev
edit ../deploy/localconfig.sh

to build production clients (from <vcellroot>/docker)

```bash
./localconfig.sh test 7.0.0-alpha.4 7.0.0 4 local.config
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen.yml up
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen.yml rm
```

to build dev clients (from <vcellroot>/docker)

```bash
./localconfig.sh test 7.0.0-alpha.4 7.0.0 4 local.config
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen-dev.yml rm
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen-dev.yml build
sudo env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen-dev.yml up
```

