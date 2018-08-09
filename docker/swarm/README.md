#### 1.  Build VCell containers (from {vcell\_project\_dir}/docker/build/ directory)

**Login to vcell-node1.cam.uchc.edu as user vcell**  

**Get VCell project**, login to {theBuildHost} as user 'vcell'

```bash
theBuildHost=vcell-node1.cam.uchc.edu
ssh vcell@${theBuildHost}
cd /opt/build
rm -rf vcell (if necessary)
git clone https://github.com/virtualcell/vcell.git
cd vcell/docker/build
```

**Build ALL containers** (sets the Docker tags to first 7 characters of Git commit hash)

```bash
export VCELL_TAG=`git rev-parse HEAD | cut -c -7`
theRegistryHost=vcell-docker.cam.uchc.edu
export VCELL_REPO_NAMESPACE=${theRegistryHost}:5000/schaff
./build.sh all $VCELL_REPO_NAMESPACE $VCELL_TAG
```

Info: build the containers (e.g. vcell-docker.cam.uchc.edu:5000/schaff/vcell-api:f18b7aa) and upload to a private Docker registry (e.g. vcell-docker.cam.uchc.edu:5000).  
A Singularity image for vcell-batch is also generated and stored locally (VCELL_ROOT/docker/build/singularity-vm) as no local Singularity repository is available yet.  Later in the deploy stage, the Singularity image is uploaded to the server file system and invoked for numerical simulation on the HPC cluster. 


# 2.  Deploy vcell using docker swarm mode


//Requirements during deployment
while building the vcell-clientgen container  
it is assumed that during deployment there is a directory (/usr/local/deploy/.install4j6/jres) which is mapped to the VOLUME /jre   
defined in Dockerfile-clientgen-dev and used inside vcell-clientgen container, the vcset up "build secrets" directory  
(e.g. /usr/local/deploy/.install4j6/jres/ on vcell-node1.cam.uchc.edu) Java jre bundles which are compatible with installed version of Install4J  
-----/usr/local/deploy/.install4j6/jres/linux-amd64-1.8.0_66.tar.gz  
-----/usr/local/deploy/.install4j6/jres/macosx-amd64-1.8.0_66.tar.gz  
-----/usr/local/deploy/.install4j6/jres/windows-x86-1.8.0_66.tar.gz  
-----/usr/local/deploy/.install4j6/jres/linux-x86-1.8.0_66.tar.gz  
-----/usr/local/deploy/.install4j6/jres/windows-amd64-1.8.0_66.tar.gz  



#### Build VCell and deploy to production servers (from ./docker/swarm/ directory)  
NOTE: current partition of SLURM for vcell is found by this command run "sinfo -N -h -p vcell2 --Format='nodelist'" (must run on vcell-service, or other slurm node)  
Assume step 1 has completed successfully  

**login to vcell-node1 as user 'vcell'**



```bash
cd /opt/build/vcell/docker/swarm
```

**Run** the following bash commands in your terminal (sets the Docker tags to first 7 characters of Git commit hash)

```bash
export VCELL_TAG=`git rev-parse HEAD | cut -c -7`
export VCELL_REPO_NAMESPACE=vcell-docker.cam.uchc.edu:5000/schaff
```

**Determine build number for deploying**  
-----**a.** Get currently deployed client

```bash
echo Alpha `curl --silent http://vcell.org/webstart/Alpha/updates.xml | xmllint --xpath '//updateDescriptor/entry/@newVersion' - | awk '{print $1;}'` && \
echo Beta `curl --silent http://vcell.org/webstart/Beta/updates.xml | xmllint --xpath '//updateDescriptor/entry/@newVersion' - | awk '{print $1;}'` && \
echo Rel `curl --silent http://vcell.org/webstart/Rel/updates.xml | xmllint --xpath '//updateDescriptor/entry/@newVersion' - | awk '{print $1;}'`
```

-----**b.** Create final build number  
if deploy server only, theBuildNumber=(number from above)  
----theBuildNumber=number from above (the 4th digit),  e.g. **Alpha newVersion="7.0.0.51" theBuildNumber=51**  
If deploying client, theBuildNumber=(number from above) + 1  
----theBuildNumber= 1 + number from above (the 4th digit),  e.g. **Alpha newVersion="7.0.0.51" theBuildNumber=52**  
edit 'VCELL_BUILD='theBuildNumber in the appropriate site block below  

**To create deploy configuration file, Choose the site block being deployed**  
Info: create deploy configuration file (e.g. Test 7.0.0 build 8) file for server. Note that some server configuration is hard-coded in the **serverconfig-uch.sh** script.  

**MUST EDIT VCELL_BUILD=${theBuildNumber} to be correct**  

**REL**  

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=${theBuildNumber} VCELL_SITE=rel
export MANAGER_NODE=vcellapi.cam.uchc.edu
export VCELL_INSTALLER_REMOTE_DIR="/share/apps/vcell3/apache_webroot/htdocs/webstart/Rel"
export VCELL_CONFIG_FILE_NAME=server_${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./serverconfig-uch.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

**BETA (not used)**  

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=10 VCELL_SITE=beta
export MANAGER_NODE=vcellapi-beta.cam.uchc.edu
export VCELL_INSTALLER_REMOTE_DIR="/share/apps/vcell3/apache_webroot/htdocs/webstart/Beta"
export VCELL_CONFIG_FILE_NAME=server_${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./serverconfig-uch.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

**ALPHA**  

```bash
export VCELL_VERSION=7.0.0 VCELL_BUILD=51 VCELL_SITE=alpha
export MANAGER_NODE=vcellapi-beta.cam.uchc.edu
export VCELL_INSTALLER_REMOTE_DIR="/share/apps/vcell3/apache_webroot/htdocs/webstart/Alpha"
export VCELL_CONFIG_FILE_NAME=server_${VCELL_SITE}_${VCELL_VERSION}_${VCELL_BUILD}_${VCELL_TAG}.config
./serverconfig-uch.sh $VCELL_SITE $VCELL_REPO_NAMESPACE \
  $VCELL_TAG $VCELL_VERSION $VCELL_BUILD $VCELL_CONFIG_FILE_NAME
```

**TEST**  

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

//SERVER only deploy commands (may request password at some point)

```bash
./deploy.sh \
   --ssh-user vcell --ssh-key ~/.ssh/id_rsa --install-singularity  \
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
