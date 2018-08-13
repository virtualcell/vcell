##Local installations of VCell server services for Debugging

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
./localconfig_mockslurm.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
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
./localconfig_realslurm.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
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
./localconfig_mockslurm.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
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

**(optional)deploy a vcell production stack (named "local") on a single machine Docker swarm mode**

```bash
./localconfig_mockslurm.sh test localhost:5000 dev 7.0.0 4 local_mockslurm.config
env $(cat local_mockslurm.config | xargs) docker stack deploy -c docker-compose.yml -c docker-compose-swarm.yml local
```