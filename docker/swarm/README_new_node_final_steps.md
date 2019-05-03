### All subsequent operations should be done as user 'vcell' which now has sudo privilege

**Login** as as user 'vcell' to previously configured manager node, choose (vcellapi,vcellapi-beta,vcell-node{1,2,3,4}), **copy** secrets archive

```bash
ssh vcell@manager-node
cd /usr/local/deploy
tar czf /tmp/vcellsecrets.tgz .
exit
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
exit
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
[registry install, make self-signed certificate](../build/README_Registry.md)  

```bash
theRegistryHost=vcell-docker.cam.uchc.edu
sudo scp vcell@${theRegistryHost}:/usr/local/deploy/registry_certs/domain.cert /etc/pki/ca-trust/source/anchors/${theRegistryHost}.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```

**Must have password-less ssh, containers (vcell-sched,vcell-submit) need to communicate with hpc-ext-1.cam.uchc.edu and invoke slurm commands using ssh**
**DO NOT DO THIS, IT HAS ALREADY BEEN DONE by system admins**

```
# Create ssh key pair(-C is comment to help identify key for human)
ssh-keygen -t rsa -C vcell@vcell-docker.cam.uchc.edu
# 2 files exist now, private key and *.pub public key
#Copy public key to machine you want to ssh to
ssh-copy-id vcell@hpc-ext-1.cam.uchc.edu
```
