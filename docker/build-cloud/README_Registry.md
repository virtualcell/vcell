# Setup private Docker Registry with self-signed certificate (http://ralph.soika.com/how-to-setup-a-private-docker-registry)
**Registry service**  

```bash
ssh vcell-docker.cam.uchc.edu
mkdir -p /usr/local/deploy/registry_certs
cd /usr/local/deploy
openssl req -newkey rsa:4096 -nodes -sha256 -keyout registry_certs/domain.key -x509 -days 356 -out registry_certs/domain.cert
sudo docker run -d -p 5000:5000 -v $(pwd)/registry_certs:/certs -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/domain.cert -e REGISTRY_HTTP_TLS_KEY=/certs/domain.key --restart=always --name registry registry:2
```


**Registry simple web-based UI** to the private repository see http://vcell-docker.cam.uchc.edu:5001/home [registry gui, web-based registry viewer](http://vcell-docker.cam.uchc.edu:5001/home), this UI was recently ported to registry:2 and has limited functionality (as of Jan 25, 2018).

```bash
# Set the name of the registry host (theRegistryHost=vcell-docker.cam.uchc.edu for CCAM VCell installation)
export theRegistryHost=vcell-docker.cam.uchc.edu
sudo docker run -d --restart=always --name registry-ui -e ENV_DOCKER_REGISTRY_HOST=${theRegistryHost} -e ENV_DOCKER_REGISTRY_PORT=5000 -e ENV_DOCKER_REGISTRY_USE_SSL=1 -p 5001:80 konradkleine/docker-registry-frontend:v2

open http://localhost:5001
```
**Registry API, NOT USED** [catalog](https://vcell-docker.cam.uchc.edu:5000/v2/_catalog) https://vcell-docker.cam.uchc.edu:5000/v2/_catalog  


for removing old docker images from registry (error upon delete):

```bash
go get github.com/fraunhoferfokus/deckschrubber
export GOPATH=$HOME/go
$GOPATH/bin/deckschrubber -day 30 -registry https://vcell-docker.cam.uchc.edu:5000
```

##Renew self-signed certs for docker registry

```bash
common ssl comands -> https://www.sslshopper.com/article-most-common-openssl-commands.html
ssh vcell@vcell-docker
cd /usr/local/deploy
//check when current cert ends
openssl x509 -in ./registry_certs/new.crt -noout -enddate
//create new request using the same key as old cert
openssl req -new -key ./registry_certs/domain.key -out ./registry_certs/new.csr
	Country Name (2 letter code) [XX]:US
	State or Province Name (full name) []:.
	Locality Name (eg, city) [Default City]:.
	Organization Name (eg, company) [Default Company Ltd]:UCHC
	Organizational Unit Name (eg, section) []:CCAM
	Common Name (eg, your name or your server's hostname) []:vcell-docker.cam.uchc.edu
	Email Address []:.
	A challenge password[]:enter
	An optional company name []:enter
//check new.csr
openssl req -in ./registry_certs/new.csr  -noout -text
//create new certificate using csr and old key
openssl x509 -req -days 365 -in ./registry_certs/new.csr -signkey ./registry_certs/domain.key -out ./registry_certs/newer.crt
//check new certificate
openssl x509 -in registry_certs/newer.crt -noout -enddate
//create temp test server for certificate running foreground
openssl s_server -key ./registry_certs/domain.key -cert ./registry_certs/newer.crt -www -accept 4567
//on vcellapi-beta, vcell-node3, vcell-node4, vcellapi, vcell-node1, vcell-node2 check new cert is trusted (new one won't be)
wget https://vcell-docker.cam.uchc.edu:4567

//First follow directions lower down under "install self-signed cert as trusted CA to enable use of Docker registry by Singularity" to enable new cert on each server

//Then restart docker registry with new certificate
sudo docker ps -> get registryContainerID of image "registry:2"
sudo docker container stop {registryContainerID}
sudo docker container rm {registryContainerID}
sudo docker run -d -p 5000:5000 -v /usr/local/deploy/registry_certs:/certs -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/newer.crt -e REGISTRY_HTTP_TLS_KEY=/certs/domain.key --restart=always --name registry registry:2

//OPTIONAL Restart vcell-docker web-ui server frontend on vcell-docker (OPTIONAL, not necessary for VCell, used to view repo from web browser)
sudo docker ps -> look for container name containing 'frontend:v2'
sudo docker container restart {frontend:v2_ContainerID}
NOTE: if you did not find an 'frontend:v2' container you can start one with the command:
sudo docker run -d --restart=always --name registry-ui -e ENV_DOCKER_REGISTRY_HOST=vcell-docker.cam.uchc.edu -e ENV_DOCKER_REGISTRY_PORT=5000 -e ENV_DOCKER_REGISTRY_USE_SSL=1 -p 5001:80 konradkleine/docker-registry-frontend:v2
Test web-ui server, try to view the repository from a web browser with 'http://vcell-docker.cam.uchc.edu:5001', (yes it's http not https and 5001 not 5000)
example_Repo_Image_Tag_Format=schaff/vcell-submit:6a3c83f (find an old image:tag from one of the vcell nodes with 'sudo docker images')
Test push an image to repo using 'sudo docker image push vcell-docker.cam.uchc.edu:5000/example_Repo_Image_Tag_Format' (do not include https://, yes it's 5000 not 50001)
Test pull an image from repo 'sudo docker pull vcell-docker.cam.uchc.edu:5000/example_Repo_Image_Tag_Format' (do not include https://, yes it's 5000 not 50001)
```

## install self-signed cert as trusted CA to enable use of Docker registry by Singularity
trusting self signed certificate on Macos (https://github.com/docker/distribution/issues/2295), and Linux/Windows (https://docs.docker.com/registry/insecure/#failing).  For example, to trust the self-signed certificate on UCHC server nodes using Centos 7.2 (e.g. vcellapi, vcell-node1, vcell-node2, vcellapi-beta, vcell-node3, vcell-node4):

```bash
export theRegistryHost=vcell-docker.cam.uchc.edu
sudo scp vcell@{theRegistryHost}:/usr/local/deploy/registry_certs/newer.crt /etc/pki/ca-trust/source/anchors/{theRegistryHost}.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```
