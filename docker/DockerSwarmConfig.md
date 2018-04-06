## install docker [on centos](https://docs.docker.com/install/linux/docker-ce/centos/#install-docker-ce-1)

set up yum docker stable repository (first time only)

```bash
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-complete-transaction --cleanup-only
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

install appropriate version of docker and start docker (should be same across the swarm)

```bash
sudo yum install -y docker-ce-18.03.0.ce
sudo systemctl start docker
```


### install singularity [instructions](https://singularity.lbl.gov/install-linux)
install to /opt/build local directory to avoid root-squash on shared file systems.

```bash
sudo mkdir -p /opt/build
cd /opt/build
sudo chown $(id -u):$(id -g) .
VERSION=2.4.5
wget https://github.com/singularityware/singularity/releases/download/$VERSION/singularity-$VERSION.tar.gz
tar xvf singularity-$VERSION.tar.gz
cd singularity-$VERSION
./configure --prefix=/usr/local
make
sudo make install
singularity selftest
```

install squashfs-tools (if not already present)

```bash
sudo yum install -y squashfs-tools-4.3-0.21.gitaae0aff4.el7.x86_64
```

### final configuration
give vcell user sudo permission to machine, and passwordless sudo for docker and singularity

```bash
sudo visudo

# insert following rows
vcell   ALL=(ALL)       ALL
vcell   ALL=(ALL)       NOPASSWD:/usr/bin/docker
vcell   ALL=(ALL)       NOPASSWD:/usr/local/bin/singularity
```

copy vcell secrets to local protected directory (will be replaced with proper secret management later ... e.g. Vault)

```bash
manager-node>  cd /usr/local/deploy
manager-node>  tar czf /tmp/vcellsecrets.tgz .

new-worker-node> 
scp manager-node:/tmp/vcellsecrets.tgz /tmp
sudo mkdir -p /usr/local/deploy
sudo chown vcell /usr/local/deploy
chmod 700 /usr/local/deploy
cd /usr/local/deploy
tar xzf /tmp/vcellsecrets.tgz .
rm /tmp/vcellsecrets.tgz

manager-node> rm /tmp/vcellsecrets.tgz
```

install self-signed cert as trusted CA trusting self signed certificate on Macos (https://github.com/docker/distribution/issues/2295), and Linux/Windows (https://docs.docker.com/registry/insecure/#failing).  For example, to trust the self-signed certificate on UCHC server nodes using Centos 7.2:

```bash
sudo scp vcell@vcell-docker.cam.uchc.edu:/usr/local/deploy/registry_certs/domain.cert /etc/pki/ca-trust/source/anchors/vcell-docker.cam.uchc.edu.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```

set as an "internal node" to schedule "vcell-master it needs a non-root-squashed share".  

```bash
docker node update --label-add zone=INTERNAL `docker node ls -q`

# 155.37.248.131:/vcellroot mounted as /opt/vcelldata 
#sudo su -
#mkdir /opt/vcelldata
#echo "/opt/vcelldata -fstype=nfs,tcp,hard,intr,noatime,nfsvers=3 cfs05:/vcellroot" > /etc/auto.docker
contents of /etc/auto.master from vcell-node1

/-      auto.default    --ghost
/-      auto.tgc        --ghost
/share/apps auto.vcellapps --ghost
/- auto.docker  --ghost

#systemctl restart autofs
```


join the new node to the swarm as a worker (check that all docker swarm nodes are accessible from DMZ machines through the firewall)

```bash
manager-node>  sudo docker swarm join-token worker
To add a worker to this swarm, run the following command:

    docker swarm join --token SWMTKN-1-.... xxx.xxx.xxx.xxx:pppp

new-worker-node>  sudo docker swarm join --token SWMTKN-1-.... xxx.xxx.xxx.xxx:pppp
```


