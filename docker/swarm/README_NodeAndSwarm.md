##Pre-swarm -- Firewall information, General requirement for swarms
TBD


##A. JOIN the new node to the swarm as a worker (check that all docker swarm nodes are accessible from DMZ machines through the firewall)

```bash
ssh vcell@manager-node
#Generate new node token to join existing swarm
manager-node>  sudo docker swarm join-token worker
# Produces a swarm command as output, example below
To add a worker to this swarm, run the following command:
    docker swarm join --token SWMTKN-1-.... xxx.xxx.xxx.xxx:pppp

exit
#Paste previous command output as command for new node to join existing cluster
new-worker-node>  sudo docker swarm join --token SWMTKN-1-.... xxx.xxx.xxx.xxx:pppp
```


##B. CREATE a new swarm with a new node
TBD


##Post-swarm -- Firewall information, General requirement for swarms
set as an "internal node" to schedule "vcell-master it needs a non-root-squashed share".  

```bash
sudo docker node update --label-add zone=INTERNAL `sudo docker node ls -q`

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
