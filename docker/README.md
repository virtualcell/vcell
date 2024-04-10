# deploy vcell using docker swarm mode.

Service configuration info [detailed instructions](README_serviceInfo.md)

## cluster configuration (<vcellroot>/docker)

1) shared folder for simulation results, singularity images, SLURM submit scripts and logs (TMPDIR=/state/partition1 currently hard-coded for SLURM nodes).
2) set up docker swarm mode

```bash
docker swarm init
docker swarm 
docker node update --label-add zone=INTERNAL `docker node ls -q`
sudo sudo sysctl -w vm.max_map_count=262144 ### for elasticsearch (ELK stack)
```

2a) set up a build machine.  Install java jdk 1.8, maven 3.5, and singularity on a Linux or Macos build machine (use Vagrant Box in vcell/docker/singularity-vm for singularity on Macos).

```bash
git clone https://github.com/singularityware/singularity.git
cd singularity
./autogen.sh
./configure --prefix=/usr/local
make
sudo make install
sudo ln -s /usr/local/bin/singularity /usr/bin/singularity 
sudo yum install squashfs-tools.x86_64
```

3) deploy a vcell production stack (named "local") on a single machine Docker swarm mode

```bash
./localconfig_mockslurm-not-used.sh test localhost:5000 dev 7.0.0 4 local_mockslurm.config
env $(cat local_mockslurm.config | xargs) docker stack deploy -c docker-compose.yml -c docker-compose-swarm.yml local
```

or deploy a development version using docker-compose (from <vcellroot>/docker)

```bash
pushd ..
mvn clean install dependency:copy-dependencies
popd
./build.sh all localhost:5000 dev
./localconfig_mockslurm-not-used.sh test localhost:5000 dev 7.0.0 4 local_mockslurm.config
env $(cat local_mockslurm.config | xargs) docker-compose -f docker-compose-clientgen.yml rm --force
env $(cat local_mockslurm.config | xargs) docker-compose -f docker-compose-clientgen.yml up
open ./generated_installers/VCell_Test_macos_7_0_0_4_64bit.dmg


env $(cat local_mockslurm.config | xargs) docker stack deploy -c docker-compose.yml local

or

env $(cat local_mockslurm.config | xargs) docker-compose -f docker-compose.yml rm --force
env $(cat local_mockslurm.config | xargs) docker-compose -f docker-compose.yml up

```

4) useful commands

```bash
> docker stack services local
ID                  NAME                MODE                REPLICAS            IMAGE                               PORTS
5hrlf3u1kdgr        local_visualizer    replicated          1/1                 dockersamples/visualizer:stable     *:30013->8080/tcp
cflee8p55yph        local_mongodb       replicated          1/1                 schaff/vcell-mongo:7.0.0-alpha.4    *:27020->27017/tcp
evpnt4b10dbs        local_activemq      replicated          1/1                 webcenter/activemq:5.14.3           *:61619->61616/tcp
tpo0wfjmgqeo        local_api           replicated          1/1                 schaff/vcell-api:7.0.0-alpha.4      *:30014->8000/tcp,*:8083->8080/tcp
xkb1tp4znjha        local_master        replicated          1/1                 schaff/vcell-master:7.0.0-alpha.4   *:30012->8000/tcp
```

```bash
> docker stack ps local
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
> deploy schaff$ docker service ls
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

```bash
> curl --verbose --insecure "https://vcellapi.cam.uchc.edu/api/v0/admin/jobs?completed=false&stopped=false&serverId=REL" | jq '.'
(see AdminJobsRestlet for details)
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
./serverconfig_uch.sh test schaff 7.0.0-alpha.4 7.0.0 4 server.config
env $(cat server.config | xargs) docker-compose -f docker-compose-clientgen.yml rm
env $(cat server.config | xargs) docker-compose -f docker-compose-clientgen.yml up
```

to build dev clients (from <vcellroot>/docker)

```bash
./localconfig.sh test vcell-docker:5000 dev 7.0.0 4 local.config
env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen.yml rm
env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen.yml build
env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen.yml up
env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen.yml push
env $(cat local.config | xargs) docker-compose -f docker-compose-clientgen.yml pull
```

to build dev vcell-batch containers (from <vcellroot>/docker)

```bash
docker build -f Dockerfile-batch-dev --tag localhost:5000/vcell-batch-dev ..
```

# Private Docker repository

## to set up a private Docker repository with self-signed certificate

how to set up a registry with self-signed certificate  http://ralph.soika.com/how-to-setup-a-private-docker-registry/

```bash
ssh vcell-docker.cam.uchc.edu
mkdir -p /usr/local/deploy/registry_certs
cd /usr/local/deploy/registry_certs
openssl req -newkey rsa:4096 -nodes -sha256 \
                -keyout registry_certs/domain.key -x509 -days 356 \
                -out registry_certs/domain.cert

sudo docker run -d -p 5000:5000 \
 -v $(pwd)/registry_certs:/certs \
 -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/domain.cert \
 -e REGISTRY_HTTP_TLS_KEY=/certs/domain.key \
 --restart=always --name registry registry:2
```

for removing old docker images from registry (error upon delete):

```bash
go get github.com/fraunhoferfokus/deckschrubber
export GOPATH=$HOME/go
$GOPATH/bin/deckschrubber -day 30 -registry https://vcell-docker.cam.uchc.edu:5000
```


## install self-signed cert as trusted CA
trusting self signed certificate on Macos (https://github.com/docker/distribution/issues/2295), and Linux/Windows (https://docs.docker.com/registry/insecure/#failing).  For example, to trust the self-signed certificate on UCHC server nodes using Centos 7.2:

```bash
sudo scp vcell@vcell-docker.cam.uchc.edu:/usr/local/deploy/registry_certs/domain.cert /etc/pki/ca-trust/source/anchors/vcell-docker.cam.uchc.edu.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```

https://vcell-docker.cam.uchc.edu:5000/v2/_catalog

for a simple web-based UI to the private repository see http://vcell-docker.cam.uchc.edu:5001/home, this UI was recently ported to registry:2 and has limited functionality (as of Jan 25, 2018).

```bash
sudo docker run \
  -d --restart=always --name registry-ui \
  -e ENV_DOCKER_REGISTRY_HOST=vcell-docker.cam.uchc.edu \
  -e ENV_DOCKER_REGISTRY_PORT=5000 \
  -e ENV_DOCKER_REGISTRY_USE_SSL=1 \
  -p 5001:80 \
  --restart always
  konradkleine/docker-registry-frontend:v2

open http://localhost:5001
```



#### Build VCell and deploy to production servers (from ./docker/swarm/ directory)

current partition of SLURM for vcell is shangrila[13-14], xanadu-[22-23]

build the containers (e.g. vcell-docker.cam.uchc.edu:5000/schaff/vcell-api:f18b7aa) and upload to a private Docker registry (e.g. vcell-docker.cam.uchc.edu:5000).  A Singularity image for vcell-batch is also generated and stored locally (VCELL_ROOT/docker/singularity-vm) as no local Singularity repository is available yet.  Later in the deploy stage, the Singularity image is uploaded to the server file system and invoked for numerical simulation on the HPC cluster. 

Get VCell project, login to vcell-node1 as user 'vcell'

```bash
ssh vcell@vcell-node1.cam.uchc.edu
cd /opt/build
rm -rf vcell (if necessary)
git clone https://github.com/virtualcell/vcell.git
cd vcell/docker
```

Run the following bash commands in your terminal (sets the Docker tags to first 7 characters of Git commit hash)

```bash
export VCELL_TAG=`git rev-parse HEAD | cut -c -7`
export VCELL_REPO_NAMESPACE=vcell-docker.cam.uchc.edu:5000/schaff
./build.sh all $VCELL_REPO_NAMESPACE $VCELL_TAG
```

//Helper for current install4j VCell software $VCELL_BUILD number, increment version if deploying client, otherwise if server only do not increment version #

```bash
echo Alpha `curl --silent http://vcell.org/webstart/Alpha/updates.xml | xmllint --xpath '//updateDescriptor/entry/@newVersion' - | awk '{print $1;}'` && \
echo Beta `curl --silent http://vcell.org/webstart/Beta/updates.xml | xmllint --xpath '//updateDescriptor/entry/@newVersion' - | awk '{print $1;}'` && \
echo Rel `curl --silent http://vcell.org/webstart/Rel/updates.xml | xmllint --xpath '//updateDescriptor/entry/@newVersion' - | awk '{print $1;}'`
```

//Choose 1 of the following blocks depending on site {Alpha,Beta,Rel}, and set VCELL_BUILD= if necessary (deploying client), execute all commands in bash
	increment if pushing new client, otherwise no increment for VCELL_BUILD

create deploy configuration file (e.g. Test 7.0.0 build 8) file for server. Note that some server configuration is hard-coded in the **serverconfig-uch.sh** script.

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=9 VCELL_SITE=rel
export MANAGER_NODE=vcellapi.cam.uchc.edu
export VCELL_INSTALLER_REMOTE_DIR="/share/apps/vcell3/apache_webroot/htdocs/webstart/Rel"
export VCELL_CONFIG_FILE_NAME=server_${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./serverconfig-uch.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=10 VCELL_SITE=beta
export MANAGER_NODE=vcellapi-beta.cam.uchc.edu
export VCELL_INSTALLER_REMOTE_DIR="/share/apps/vcell3/apache_webroot/htdocs/webstart/Beta"
export VCELL_CONFIG_FILE_NAME=server_${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./serverconfig-uch.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=49 VCELL_SITE=alpha
export MANAGER_NODE=vcellapi-beta.cam.uchc.edu
export VCELL_INSTALLER_REMOTE_DIR="/share/apps/vcell3/apache_webroot/htdocs/webstart/Alpha"
export VCELL_CONFIG_FILE_NAME=server_${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./serverconfig-uch.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=7 VCELL_SITE=test
export MANAGER_NODE=vcellapi-beta.cam.uchc.edu
export VCELL_INSTALLER_REMOTE_DIR="/share/apps/vcell3/apache_webroot/htdocs/webstart/Test"
export VCELL_CONFIG_FILE_NAME=server_${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./serverconfig-uch.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```
using deploy configuration and Docker images, generate client installers and deploy server as a Docker stack in swarm mode.  Note that the Docker and Singularity images and docker-compose.yml file remain independent of the deployed configuration.  Only the final deployed configuration file vcellapi.cam.uchc.edu:/usr/local/deploy/config/$VCELL_CONFIG_FILE_NAME contains server dependencies.  get platform installer from web site (e.g. http://vcell.org/webstart/Test/VCell_Test_macos_7.0.0_7_64bit.dmg)

Choose 1 of the following:

//CLIENT and SERVER deploy commands (may request password at some point)

```bash
./deploy.sh \
   --ssh-user vcell --ssh-key ~/.ssh/id_rsa --install-singularity --build-installers --installer-deploy-dir $VCELL_INSTALLER_REMOTE_DIR --link-installers \
   ${MANAGER_NODE} \
   ./${VCELL_CONFIG_FILE_NAME} /usr/local/deploy/config/${VCELL_CONFIG_FILE_NAME} \
   ./docker-compose.yml        /usr/local/deploy/config/docker-compose_${VCELL_TAG}.yml \
   vcell${VCELL_SITE}
```

```bash
./deploy.sh \
   --ssh-user vcell --ssh-key ~/.ssh/id_rsa --build-installers --installer-deploy-dir $VCELL_INSTALLER_REMOTE_DIR --link-installers \
   ${MANAGER_NODE} \
   ./${VCELL_CONFIG_FILE_NAME} /usr/local/deploy/config/${VCELL_CONFIG_FILE_NAME} \
   ./docker-compose.yml        /usr/local/deploy/config/docker-compose_${VCELL_TAG}.yml \
   vcell${VCELL_SITE}
```

//SERVER only deploy commands (may request password at some point)

```bash
./deploy.sh \
   --ssh-user vcell --ssh-key ~/.ssh/id_rsa --install-singularity  \
   ${MANAGER_NODE} \
   ./${VCELL_CONFIG_FILE_NAME} /usr/local/deploy/config/${VCELL_CONFIG_FILE_NAME} \
   ./docker-compose.yml        /usr/local/deploy/config/docker-compose_${VCELL_TAG}.yml \
   vcell${VCELL_SITE}
```

```bash
./deploy.sh \
   --ssh-user vcell --ssh-key ~/.ssh/id_rsa  \
   ${MANAGER_NODE} \
   ./${VCELL_CONFIG_FILE_NAME} /usr/local/deploy/config/${VCELL_CONFIG_FILE_NAME} \
   ./docker-compose.yml        /usr/local/deploy/config/docker-compose_${VCELL_TAG}.yml \
   vcell${VCELL_SITE}
```

debug installers on Macos?

```bash
INSTALL4J_LOG=yes myLauncher.app/Contents/MacOS/JavaApplicationStub
````

edit local configuration (vi $VCELL_CONFIG_FILE_NAME) and redeploy (copies local config to server and restarts).

```bash
./deploy.sh \
  --ssh-user vcell --ssh-key ~/.ssh/schaff_rsa \
  ${MANAGER_NODE} \
  ./${VCELL_CONFIG_FILE_NAME} /usr/local/deploy/config/${VCELL_CONFIG_FILE_NAME} \
  ./docker-compose.yml        /usr/local/deploy/config/docker-compose_${VCELL_TAG}.yml \
  vcell${VCELL_SITE}
```

```bash
sudo env ${cat server_alpha_7.0.0_10_a9a83bb-2.config | xargs) docker stack deploy -c docker-compose_a9a83bb-2.yml vcellalpha
```

### Deploy local stack to Docker Swarm (with local mock SLURM/Docker simulations)

build the containers (e.g. localhost:5000/schaff/vcell-api:dev1) and upload to local registry Docker registry (e.g. localhost:5000).  

```bash
export VCELL_REPO_NAMESPACE=localhost:5000/schaff VCELL_TAG=dev13
./build.sh --skip-singularity all $VCELL_REPO_NAMESPACE $VCELL_TAG
```

create local deploy configuration file (e.g. Test2 7.0.0 build 7) file for local debugging. Note that some local configuration is hard-coded in **localconfig_mockslurm.sh** script.

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=9 VCELL_SITE=test2
export VCELL_CONFIG_FILE_NAME=local-${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./localconfig_mockslurm-not-used.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

Deploy VCell Docker containers on local machine in Swarm Mode with mocked SLURM scripts and generated local client installers.  This uses the local deploy configuration and local Docker registry and defaults to Docker instead of Singularity for SLURM simulations.  Note that the Docker and Singularity images and docker-compose.yml file remain independent of the deployed configuration.  Only the local configuration file ./$VCELL_CONFIG_FILE_NAME contains server dependencies.  Local client installers are generated (e.g. open ./generated_installers/VCell_Test_macos_7.0.0_7_64bit.dmg)

```bash
./deploy.sh \
  --ssh-user `whoami` --ssh-key ~/.ssh/id_rsa --build-installers \
  `hostname` \
  ./$VCELL_CONFIG_FILE_NAME $PWD/${VCELL_CONFIG_FILE_NAME}.config \
  ./docker-compose-dev.yml $PWD/local-docker-compose-dev-${VCELL_TAG}.yml \
  vcell${VCELL_SITE}
```

```bash
./deploy.sh \
  --ssh-user `whoami` --ssh-key ~/.ssh/id_rsa \
  `hostname` \
  ./$VCELL_CONFIG_FILE_NAME $PWD/${VCELL_CONFIG_FILE_NAME}.config \
  ./docker-compose-dev.yml $PWD/local-docker-compose-dev-${VCELL_TAG}.yml \
  vcell${VCELL_SITE}
```

### Deploy local stack to Docker Swarm (with remote SLURM/Singularity simulations)

build the containers (e.g. localhost:5000/schaff/vcell-api:dev1) and upload to local registry Docker registry (e.g. localhost:5000).  

```bash
export VCELL_REPO_NAMESPACE=localhost:5000/schaff VCELL_TAG=dev13
./build.sh --ssh-user vcell --ssh-key ~/.ssh/id_rsa all $VCELL_REPO_NAMESPACE $VCELL_TAG
```

create local deploy configuration file (e.g. Test2 7.0.0 build 7) file for local debugging. Note that some local configuration is hard-coded in **localconfig_mockslurm.sh** script.

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=8 VCELL_SITE=test2
export VCELL_CONFIG_FILE_NAME=${VCELL_TAG}.config
./localconfig_realslurm_postgres.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

Deploy VCell Docker containers on local machine in Swarm Mode with mocked SLURM scripts and generated local client installers.  This uses the local deploy configuration and local Docker registry and defaults to Docker instead of Singularity for SLURM simulations.  Note that the Docker and Singularity images and docker-compose.yml file remain independent of the deployed configuration.  Only the local configuration file ./$VCELL_CONFIG_FILE_NAME contains server dependencies.  Local client installers are generated (e.g. open ./generated_installers/VCell_Test_macos_7.0.0_7_64bit.dmg)

```bash
./deploy.sh \
  --ssh-user `whoami` --ssh-key ~/.ssh/schaff_rsa --build-installers --install-singularity \
  `hostname` \
  ./$VCELL_CONFIG_FILE_NAME $PWD/local-${VCELL_TAG}.config \
  ./docker-compose-dev.yml $PWD/local-docker-compose-${VCELL_TAG}.yml \
  vcell${VCELL_SITE}
```

### Deploy SBML Solvers

build the containers (e.g. localhost:5000/schaff/vcell-api:dev1) and upload to local registry Docker registry (e.g. localhost:5000).  

```bash
export VCELL_REPO_NAMESPACE=localhost:5000/schaff VCELL_TAG=dev13
./build.sh --skip-singularity sbmlsolvergen $VCELL_REPO_NAMESPACE $VCELL_TAG
```

create local deploy configuration file (e.g. Test2 7.0.0 build 7) file for local debugging. Note that some local configuration is hard-coded in **localconfig_mockslurm.sh** script.

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=9 VCELL_SITE=test2
export VCELL_CONFIG_FILE_NAME=sbmlsolvers_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./localconfig_mockslurm-not-used.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

Deploy VCell Docker containers on local machine in Swarm Mode with mocked SLURM scripts and generated local client installers.  This uses the local deploy configuration and local Docker registry and defaults to Docker instead of Singularity for SLURM simulations.  Note that the Docker and Singularity images and docker-compose.yml file remain independent of the deployed configuration.  Only the local configuration file ./$VCELL_CONFIG_FILE_NAME contains server dependencies.  Local client installers are generated (e.g. open ./generated_installers/VCell_Test_macos_7.0.0_7_64bit.dmg)

```bash
./deploy.sh \
  --ssh-user `whoami` --ssh-key ~/.ssh/id_rsa \
  `hostname` \
  ./$VCELL_CONFIG_FILE_NAME $PWD/${VCELL_CONFIG_FILE_NAME} \
  ./docker-compose-dev.yml $PWD/local-docker-compose-dev-${VCELL_TAG}.yml \
  vcell${VCELL_SITE}
./deploy.sh \
  --ssh-user `whoami` --ssh-key ~/.ssh/schaff_rsa --build-installers --install-singularity \
  `hostname` \
  ./$VCELL_CONFIG_FILE_NAME $PWD/local-${VCELL_TAG}.config \
  ./docker-compose-dev.yml $PWD/local-docker-compose-${VCELL_TAG}.yml \
  vcell${VCELL_SITE}
```

