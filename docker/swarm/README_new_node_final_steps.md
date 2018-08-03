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
sudo scp vcell@vcell-docker.cam.uchc.edu:/usr/local/deploy/registry_certs/domain.cert /etc/pki/ca-trust/source/anchors/vcell-docker.cam.uchc.edu.crt
sudo update-ca-trust
sudo service docker stop
sudo service docker start
```

