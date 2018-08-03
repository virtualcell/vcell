## Start with standard UCH Centos 7.3 distribution, login (ssh to avoid copy/paste problems on minimal centos7) as administrative (frmadmin,jsadmin) and run update (to Centos 7.4)

```bash
sudo yum -y update 
sudo reboot
```
## install java dev tools, python packages, and utilities

```bash
sudo yum -y install java-1.8.0-openjdk-devel
echo "export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")" | sudo tee -a /etc/profile
source /etc/profile
```

install maven (using /tmp because of root squashed home directories)

```bash
cd /tmp
wget http://www-us.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz
tar -zxvf apache-maven-3.5.3-bin.tar.gz
sudo mv /tmp/apache-maven-3.5.3 /opt
sudo chown -R root:root /opt/apache-maven-3.5.3
sudo ln -s /opt/apache-maven-3.5.3 /opt/apache-maven
echo 'export PATH=$PATH:/opt/apache-maven/bin' | sudo tee -a /etc/profile
source /etc/profile
mvn --version
rm /tmp/apache-maven-3.5.3-bin.tar.gz
```

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

```bash
sudo yum install -y jq
```

## install docker [on centos](https://docs.docker.com/install/linux/docker-ce/centos/#install-docker-ce-1)

set up yum by adding yum repo (docker stable repository) first time only

```bash
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-complete-transaction --cleanup-only
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

install appropriate version of docker and start docker (should be same across the swarm)

```bash
sudo yum install -y docker-ce-18.03.0.ce
sudo systemctl enable docker
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

### Make vcell sudoer
give vcell user sudo permission to machine, and passwordless sudo for docker and singularity

```bash
sudo visudo

# insert following rows
vcell   ALL=(ALL)       ALL
vcell   ALL=(ALL)       NOPASSWD:/usr/bin/docker
vcell   ALL=(ALL)       NOPASSWD:/usr/local/bin/singularity
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
