# vcell uses Docker containers to automate the building and packaging of all software.

## builds the Docker images (1-9 below) and pushes them into a Docker registry (e.g. namespace = "vcell-docker.cam.uchc.edu:5000") with the image tag derived from the Git commit hash at build time (e.g. tag = "392af4d").  The vcell-batch Singularity image (item 10 below) is built from the vcell-batch Docker image for use within a HPC environment.

1) vcell-api         => docker image in registry    (api)  
2) vcell-db          => docker image in registry    (db)  
3) vcell-sched       => docker image in registry    (sched)  
4) vcell-submit      => docker image in registry    (submit)  
5) vcell-mongodb     => docker image in regsitry    (mongodb)  
6) vcell-activemqint => docker image in registry    (activemqint)  
7) vcell-activemqsim => docker image in registry    (activemqsim)  
8) vcell-clientgen   => docker image in registry    (generates Install4J installers during deployment)  
9) vcell-batch       => docker image in registry    (for batch processing, includes java code and Linux solver executables)  
10) vcell-batch.img  => singularity image in ./singularity-vm/   (built from vcell-batch docker image)  

## build Singularity image for Linux solvers

builds a Singularity image named ./singularity-vm/${namespace}_vcell-batch_${tag}.img from the Docker image ${namespace}/vcell-batch:${tag}

## Linux build machine configuration

1) set up a Linux build machine.  Install java jdk 1.8, maven 3.5, and Singularity on a Linux or Macos build machine (use Vagrant Box in vcell/docker/singularity-vm for singularity on Macos).

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

2) while building the clientgen container, it is assumed that during deployment there is a directory which is mapped to the /jre volume in the vcset up "build secrets" directory (e.g. /usr/local/deploy/.install4j6/jres/ on vcell-node1.cam.uchc.edu).


Java jre bundles which are compatible with installed version of
Install4J
/usr/local/deploy/.install4j6/jres/linux-amd64-1.8.0_66.tar.gz
/usr/local/deploy/.install4j6/jres/macosx-amd64-1.8.0_66.tar.gz
/usr/local/deploy/.install4j6/jres/windows-x86-1.8.0_66.tar.gz
/usr/local/deploy/.install4j6/jres/linux-x86-1.8.0_66.tar.gz	
/usr/local/deploy/.install4j6/jres/windows-amd64-1.8.0_66.tar.gz


to build dev vcell-batch containers (from <vcellroot>/docker)

```bash
docker build -f Dockerfile-batch-dev --tag localhost:5000/vcell-batch-dev ..
```

## install self-signed cert as trusted CA to enable use of Docker registry by Singularity (see REAMDE_Registry.md)


#### Build VCell containers (from ./docker/build/ directory)

build the containers (e.g. vcell-docker.cam.uchc.edu:5000/schaff/vcell-api:f18b7aa) and upload to a private Docker registry (e.g. vcell-docker.cam.uchc.edu:5000).  A Singularity image for vcell-batch is also generated and stored locally (VCELL_ROOT/docker/singularity-vm) as no local Singularity repository is available yet.  Later in the deploy stage, the Singularity image is uploaded to the server file system and invoked for numerical simulation on the HPC cluster. 

Get VCell project, login to vcell-node1 as user 'vcell'

```bash
ssh vcell@vcell-node1.cam.uchc.edu
cd /opt/build
rm -rf vcell (if necessary)
git clone https://github.com/virtualcell/vcell.git
cd vcell/docker/build
```

Run the following bash commands in your terminal (sets the Docker tags to first 7 characters of Git commit hash)

```bash
export VCELL_TAG=`git rev-parse HEAD | cut -c -7`
export VCELL_REPO_NAMESPACE=vcell-docker.cam.uchc.edu:5000/schaff
./build.sh all $VCELL_REPO_NAMESPACE $VCELL_TAG
```
