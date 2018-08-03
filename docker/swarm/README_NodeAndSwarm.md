
## 1) CREATE and JOIN a new swarm cluster --OR-- JOIN an existing swarm cluster
**Do 1a or 1b, not both**  
**1a. CREATE a new swarm using a new node that is not already part of a swarm (node automatically JOINS new swarm as a manager)**  

```bash
docker swarm init
```

**1b. JOIN a node to the swarm as a {worker or manager}**  
**(check that all docker swarm nodes are accessible from DMZ machines through the firewall)**  

```bash
ssh vcell@{manager-node}
#Generate new node token to join existing swarm
sudo docker swarm join-token {worker,manager}
# Produces a swarm command as output, example below
--To add a worker to this swarm, run the following command:
----docker swarm join --token SWMTKN-1-.... xxx.xxx.xxx.xxx:pppp

ssh vcell@{the node that you're going to join to swarm}
#Paste previous command output as command for new node to join existing cluster
e.g. sudo docker swarm join --token SWMTKN-1-.... xxx.xxx.xxx.xxx:pppp
#Note: may fail, check make sure firewall allows connectionsto-from the ports in join command
```


## 2) Important to set flag (zone=INTERNAL) on swarm node (manager node) that indicates which side of firewall a container is allowed to be started  
**{vcellapi,vcellapi-beta} are not INTERNAL and MUST NOT HAVE this label**   
**{vcell-node1,vcell-node2,vcell-node3,vcell-node4} MUST HAVE the following label set**  
  
```bash
sudo docker node ls
--NODE_ID                       HOSTNAME                   STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
--0x290pfnvtq8gf9a8uzlih8jb *   vcell-node1.cam.uchc.edu   Ready               Active              Reachable           18.03.0-ce
--pznhvvcqu89xzao84jud7ci9i     vcell-node2.cam.uchc.edu   Ready               Active              Leader              18.03.0-ce
--y6bshmo0nrkm6skwk1d5k63a0     vcellapi.cam.uchc.edu      Ready               Active              Reachable           18.03.0-ce

sudo docker node update --label-add zone=INTERNAL {NODE_ID of created/joined node, * is current node}
```
