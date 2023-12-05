## Start with Ubuntu 22.04

### Install Docker
install docker according to https://docs.docker.com/engine/install/ubuntu/

### Configure docker swarm cluster
configure swarm according to [README_RebuildSwarm.md](README_RebuildSwarm.md)

### install 
install paramiko, requests, and tabulate python packages (used by cli.py)

```bash
sudo yum install -y python-devel
sudo yum install -y libffi-devel
sudo yum install -y openssl-devel
echo "may need command 'yum -y install epel-release' for python-pip"
sudo yum install -y python-pip
sudo pip install --upgrade pip
sudo pip install paramiko
sudo pip install requests
sudo pip install tabulate
```

install jq (utility for json processing)
install poetry (for building/installing vcell-admin)

```bash
sudo apt-get install -y jq
sudo apt-get install -y python3-poetry
```

## Install singularity [more info linux](https://singularity.lbl.gov/install-linux)
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


## Must add non-root-squashed share (that ultimately points to same physical place as /share/apps/vcell3) to machines that are non-DMZ (behind firewall)
**Do this for vcell-node{1,2,3,4}, NOT vcellapi or vcellapi-beta**  
**(machines configured to be internal are labeled when joined to swarm by adding node label 'zone=INTERNAL')** [see node label instructions](README_NodeAndSwarm.md)  

```bash

# 155.37.248.131:/vcellroot mounted as /opt/vcelldata 
sudo su -
mkdir /opt/vcelldata
echo "/opt/vcelldata -fstype=nfs,tcp,hard,intr,noatime,nfsvers=3 cfs05:/vcellroot" > /etc/auto.docker
#Make sure the following 2 lines are present in /etc/auto.master
#/share/apps auto.vcellapps --ghost
#/- auto.docker  --ghost
sudo nano /etc/auto.master
#make nfs automounter re-read configs
systemctl restart autofs
```

​**Contact Terry,Sophon,Ion to add the current node ip address to the list of allowed nfs clients (cfs05/vcellroot)**  
**Do this for vcell-node{1,2,3,4}, NOT vcellapi or vcellapi-beta**  


##make sure ganglia is set properly (send\_metadata\_interval = 60)

```bash
sudo vi /etc/ganglia/gmond.conf
sudo systemctl restart gmond
```
