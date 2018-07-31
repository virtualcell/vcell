## Start with standard UCH Centos 7.3 distribution, login as administrative (frmadmin,jsadmin) and run update (to Centos 7.4)

```bash
sudo yum -y update 
sudo reboot
```
## install java dev tools, python packages, and utilities

```bash
sudo yum install java-1.8.0-openjdk-devel
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

### final configuration
give vcell user sudo permission to machine, and passwordless sudo for docker and singularity

```bash
sudo visudo

# insert following rows
vcell   ALL=(ALL)       ALL
vcell   ALL=(ALL)       NOPASSWD:/usr/bin/docker
vcell   ALL=(ALL)       NOPASSWD:/usr/local/bin/singularity
```

### All subsequent operations should be done as user 'vcell' which now has sudo privilege

**Login** as as user vcell to one manager node choose (vcellapi,vcell-node{1,2,3,4}), create secrets archive

```bash
ssh vcell@manager-node
manager-node>  cd /usr/local/deploy
manager-node>  tar czf /tmp/vcellsecrets.tgz .
manager-node> exit
```

**Login** to your new node,  
**copy** vcell secrets from previous manager node to local protected directory (will be replaced with proper secret management later ... e.g. Vault),  

```bash
su - vcell
scp {the node where you archived secrets}:/tmp/vcellsecrets.tgz /tmp
sudo mkdir -p /usr/local/deploy
sudo chown vcell /usr/local/deploy
chmod 700 /usr/local/deploy
cd /usr/local/deploy
tar xzf /tmp/vcellsecrets.tgz .
rm /tmp/vcellsecrets.tgz
```

**Login** previous manager node, remove tmp secrets archive

```bash
ssh vcell@manager-node
manager-node> rm /tmp/vcellsecrets.tgz
manager-node> exit
```

install self-signed cert as trusted CA trusting self signed certificate on Macos (https://github.com/docker/distribution/issues/2295), and Linux/Windows (https://docs.docker.com/registry/insecure/#failing).  
For our internal Docker Registry server so we can use https because singularity requires  
For example, to trust the self-signed certificate on UCHC server nodes using Centos 7.2:

```bash
sudo scp vcell@vcell-docker.cam.uchc.edu:/usr/local/deploy/registry_certs/domain.cert /etc/pki/ca-trust/source/anchors/vcell-docker.cam.uchc.edu.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```

make sure ganglia is set properly (send_metadata_interval = 60)

```bash
sudo vi /etc/ganglia/gmond.conf
sudo systemctl restart gmond
```


​

