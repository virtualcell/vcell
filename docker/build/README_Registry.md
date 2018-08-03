# Private Docker repository

## to set up a private Docker repository with self-signed certificate (on vcell-docker.cam.uchc.edu)

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


## install self-signed cert as trusted CA to enable use of Docker registry by Singularity
trusting self signed certificate on Macos (https://github.com/docker/distribution/issues/2295), and Linux/Windows (https://docs.docker.com/registry/insecure/#failing).  For example, to trust the self-signed certificate on UCHC server nodes using Centos 7.2 (e.g. vcellapi, vcell-node1, vcell-node2, vcellapi-beta, vcell-node3, vcell-node4):

```bash
sudo scp vcell@vcell-docker.cam.uchc.edu:/usr/local/deploy/registry_certs/domain.cert /etc/pki/ca-trust/source/anchors/vcell-docker.cam.uchc.edu.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```

[registry api, NOT USED](https://vcell-docker.cam.uchc.edu:5000/v2/_catalog) https://vcell-docker.cam.uchc.edu:5000/v2/_catalog  

for a simple web-based UI to the private repository see http://vcell-docker.cam.uchc.edu:5001/home [registry gui, web-based registry viewer](http://vcell-docker.cam.uchc.edu:5001/home), this UI was recently ported to registry:2 and has limited functionality (as of Jan 25, 2018).

```bash
sudo docker run \
  -d --restart=always --name registry-ui \
  -e ENV_DOCKER_REGISTRY_HOST=vcell-docker.cam.uchc.edu \
  -e ENV_DOCKER_REGISTRY_PORT=5000 \
  -e ENV_DOCKER_REGISTRY_USE_SSL=1 \
  -p 5001:80 \
  konradkleine/docker-registry-frontend:v2

open http://localhost:5001
```

# During build, supply internal registry as part of Docker image namespace (e.g. vcell-docker.cam.uchc.edu:5000/schaff/<container_name>:<tag_name>)

#### Build VCell and deploy to production servers (from ./docker/build/ directory)

current partition of SLURM for vcell is shangrila[13-14], xanadu-[22-23]

build the containers (e.g. vcell-docker.cam.uchc.edu:5000/schaff/vcell-api:f18b7aa) and upload to a private Docker registry (e.g. vcell-docker.cam.uchc.edu:5000).  A Singularity image for vcell-batch is also generated and temporarily stored locally on the build machine as a Singularity Image in directory (vcell-node1.cam.uchc.edu:/opt/build/vcell/docker/build/singularity-vm) as no local Singularity repository is available yet.  Later in the deploy stage, the Singularity image is uploaded to the server file system and invoked for numerical simulation on the HPC cluster. 


The following environment variable is used by the build process (on vcell-node1) to name the containers and instruct which repository to push them to.  This environment variable is also used during deploy to swarm 

```bash
export VCELL_REPO_NAMESPACE=vcell-docker.cam.uchc.edu:5000/schaff
```
