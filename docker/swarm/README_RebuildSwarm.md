# DESTROY and RECREATE Docker Swarm (alpha site example)

## destroy current swarm
#### shutdown current stack (run on vcellapi-beta)
```bash
sudo docker stack rm vcellalpha
```
#### log into each node and leave the current swarm (run on vcellapi-beta,vcell-node3,vcell-node4)
```bash
sudo docker swarm leave --force
```
## create new swarm and add nodes
### create new swarm, current node joins cluster (run on vcellapi-beta)
```bash
sudo docker swarm init
sudo docker swarm join-token manager
sudo docker swarm join-token worker
```
### join other 2 nodes as managers
using **manager** "join token" from above (run on vcell-node3 and vcell-node4). 
All 3 nodes are added as managers (must be odd number).  
This way can shutdown one node at a time and not loose quorum.
```bash
sudo docker swarm join --token SWMTKN-1-... 155.37.255.68:2377
```
### confirm that there are 3 nodes in this cluster (run on any node)
```bash
sudo docker node ls
ID                            HOSTNAME                     STATUS    AVAILABILITY   MANAGER STATUS   ENGINE VERSION
o1p1u8b0f8ghzdrxevdnfaoqk     vcell-node3.cam.uchc.edu     Ready     Active         Reachable        20.10.10
xd2k5pr8q6suoiqknexz31z68 *   vcell-node4.cam.uchc.edu     Ready     Active         Reachable        20.10.10
6jw2v85djrf33b2ws9ax2a3q5     vcellapi-beta.cam.uchc.edu   Ready     Active         Leader           20.10.22
```
### label INTERNAL nodes used to schedule stack services (run on vcell-node3,vcell-node4)
DMZ nodes (e.g. vcellapi,vcellapi-beta) cannot talk to some external resources (e.g. submit nodes)
```bash
sudo docker node update --label-add zone=INTERNAL {NODE_ID of created/joined node, * is current node}
```
### confirm that nodes are labeled (run on any node)
```bash
sudo docker node inspect --pretty o1p1u8b0f8ghzdrxevdnfaoqk
sudo docker node inspect --pretty xd2k5pr8q6suoiqknexz31z68
```