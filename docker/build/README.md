# VCell uses Docker containers to automate the building and packaging of all software.


**During build, supply internal registry as part of Docker image namespace (e.g. vcell-docker.cam.uchc.edu:5000/schaff/<container_name>:<tag_name>)**  
**Build VCell and deploy to production servers (from ./docker/build/ directory)**  
current partition of SLURM for vcell is xanadu-[01-05]

build the containers (e.g. vcell-docker.cam.uchc.edu:5000/schaff/vcell-api:f18b7aa) and upload to a private Docker registry (e.g. vcell-docker.cam.uchc.edu:5000).  A Singularity image for vcell-batch is also generated and temporarily stored locally on the build machine as a Singularity Image in directory (vcell-node1.cam.uchc.edu:/opt/build/vcell/docker/build/singularity-vm) as no local Singularity repository is available yet.  Later in the deploy stage, the Singularity image is uploaded to the server file system and invoked for numerical simulation on the HPC cluster. 

The following environment variable is used by the build process (on vcell-node1) to name the containers and instruct which repository to push them to.  This environment variable is also used during deploy to swarm 

```bash
theRegistryHost=vcell-docker.cam.uchc.edu
export VCELL_REPO_NAMESPACE=${theRegistryHost}:5000/schaff
```

## Linux build machine configuration

**Set up a Linux build machine (if necessaty)**  
Install java jdk 1.8, maven 3.5, and Singularity on a Linux or Macos build machine (use Vagrant Box in vcell/docker/singularity-vm for singularity on Macos).

```bash
git clone https://github.com/singularityware/singularity.git
cd singularity
git checkout tags/2.5.2
./autogen.sh
#might have to yum install libarchive-devel
./configure --prefix=/usr/local
make
sudo make install
sudo ln -s /usr/local/bin/singularity /usr/bin/singularity 
sudo yum install squashfs-tools.x86_64
```

**Deployment note for vcell-clientgen**:  
it is assumed that during deployment there is a directory which is mapped to the /jre volume in the vcset up "build secrets" directory (e.g. /usr/local/deploy/.install4j6/jres/ on {theBuildHost}  
Java jre bundles which are compatible with installed version of Install4J  
/usr/local/deploy/.install4j6/jres/linux-amd64-1.8.0_66.tar.gz  
/usr/local/deploy/.install4j6/jres/macosx-amd64-1.8.0_66.tar.gz  
/usr/local/deploy/.install4j6/jres/windows-x86-1.8.0_66.tar.gz  
/usr/local/deploy/.install4j6/jres/linux-x86-1.8.0_66.tar.gz  
/usr/local/deploy/.install4j6/jres/windows-amd64-1.8.0_66.tar.gz  

**Install Trust certificate** to enable use of Docker registry by Singularity [details](README_Registry.md)

```bash
export theRegistryHost=vcell-docker.cam.uchc.edu
sudo scp vcell@{theRegistryHost}:/usr/local/deploy/registry_certs/domain.cert /etc/pki/ca-trust/source/anchors/{theRegistryHost}.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```


