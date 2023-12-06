## cluster configuration (<vcellroot>/docker)  

## VCell Project Software Service configuration info (All docker images are stored in Docker registry on vcell-docker.cam.uchc.edu)
# Important local directory on Docker Swarm nodes
**deploy directory (on DMZ machines ... vcellapi.cam.uchc.edu for RELEASE or vcellapi-beta.cam.uchc.edu for ALPHA).**  
/usr/local/deploy/config which stores the deployment configurations.  
**All swarm nodes**  
/usr/local/deploy local secrets directory which holds clear text passwords and certificates and Java JRE images (.install4j subdir)  

# Slurm Nodes  
singularity module must be installed on all VCell hpc nodes (script must have access to the following script command "module load singularity/2.4.2") michael wilson  
need 2 local dirs on each:  
-----/state/partition1/singularityImages  (has singularity images contains all the solvers, copy of vcell-batch docker container converted to singularity)  
-----/scratch/vcell  (solver output dir, holds sim data that is then packaged into zip and moved to /share/apps/vcell3/users/{user})  
needed remote dirs  
-----/share/apps/vcell3/htclogs (needed by vcell hpc partition nodes)  
----------vcell-submit service puts submision script /share/apps/vcell3/htclogs/V_{REL,ALPHA}_{VCSimKey}_{jobid}_{task}.slurm.sub, e.g. /share/apps/vcell3/htclogs/V_REL_137662584_2_0.slurm.sub)  
----------slurm writes job log (stdout of the submission script, includes solver stdout) /share/apps/vcell3/htclogs/V_{REL,ALPHA}_{VCSimKey}_{jobid}_{task}.slurm.log, , e.g. /share/apps/vcell3/htclogs/V_REL_137662584_2_0.slurm.log  
-----/share/apps/vcell3/users  (sim results written to during solver execution)
-----/share/apps/vcell3/singularityImages (place where singularity solver container is stored during VCell deploy, copied if necessay to hpc via slurm submit script)

 currently hard-coded for SLURM nodes) needed by solvers, contains **singularityImages** (singularity container images, pushed to each hpc node during deployment),**tmp** (solver temp dir)  

#Shared Directories
1a) shared folder (**/share/apps/vcell3**) has subdirectories - **users(simData), htclogs(submit,logs), singularity(container mages), apache_webroot(deployed clients), export(exported simData for download)**  
-----1aa) applies to all hpc nodes (xanadu{1-xxx},shangrala{1-xxx}) (normal linux nfs mount mount,they are root-squashed), needs **users,htclogs**  
-----1ab) vcell swarm nodes needs (excluding dmz machines - vcellapi, vcellapi-beta) **users, htclogs, export** (usings volumes, when mapping volumes to real locations, Docker daemon runs as root so must have a different share that is not root squashed)  
-----1ac) vcell build node (vcell-node1) **singularity(not used), apache_weboot**  

**--VCell Stack--(VCell docker swarm cluster) Docker VCell** [see vcell stack definition](swarm/docker-compose.yml)  
[script that creates environment variables file](docker/swarm/serverconfig-uch.sh)  
**api** (org.vcell.rest.VCellApiMain) [DockerFile](build/Dockerfile-api-dev) (REST web service provides external connectivity for VCell clients)  
**db** (cbit.vcell.message.server.db.DatabaseServer) [DockerFile](build/Dockerfile-db-dev) (services database requests)  
**data** (cbit.vcell.message.server.data.SimDataServer) [DockerFile](build/Dockerfile-data-dev) (services data request)  
**sched** (cbit.vcell.message.server.dispatcher.SimulationDispatcher) [DockerFile](build/Dockerfile-sched-dev) (manages solver status, decides which simulations run in what order)  
**submit** (cbit.vcell.message.server.batch.sim.HtcSimulationWorker) [DockerFile](build/Dockerfile-submit-dev) (creates solver run arguments and config, slurm submission script, writes to /share/apps/vcell3/users/{user}/simtask.xml, sends script to slurm)  
**mongodb** [DockerFile](build/mongo/Dockerfile) (hosts gridfs for temp store of large messages, error logging)  
**activemqint** (webcenter/activemq:5.14.3) (handles messages between services)  
**activemqsim** (webcenter/activemq:5.14.3) (handles messages from running solvers and 'sched' service)  
**vcell-opt** [DockerFile](build/Dockerfile-opt-dev) (Provides REST service to running optimization with copasi)  

**Linux Logging elk client services** [see ELK definition](swarm/README_ELK.yml) **(elk.cam.uchc.edu)**  
**elastic-agent** (installed directly on all nodes, forwards logging events and metrics directly to elasticsearch)  
**fleet** (orchestrates and managed 'fleet' of elastic-agents)  
**elasticsearch** (searchables database of logging events)  
**kibana** (users interface for elastic search and managing elastic-agent's via fleet)


**Not a --VCell Stack-- service but sent to all hpc nodes during deployment**  
**Docker Solvers,Preprocess,Postprocess image** (for batch processing, includes java code and Linux solver executables)   
**vcell-batch** (Never run standalone in docker, used as source for singularity image) [DockerFile](build/Dockerfile-batch-dev)  

**Singularity image (during build, created from vcell-batch docker image)**  
vcell-batch.img  => singularity image temporarily stored /opt/build/vcell/docker/build/singularity-vm/ 

**vcell-clientgen (generates Install4J installers during deployment)**  
Docker container containing install4j and built (on-the-fly) vcell-client software => docker image in registry  

**elastic-agent service**  
installed directly on nodes, managed by elastic fleet server on elk.cam.uchc.edu

```
sudo docker stack deploy -c vcell/docker/swarm/docker-stack-logspout.yml logspout
```

 (docker swarm managed after manually deployed, runs on each swarm node, forwards logs to elk from each swarm node, deployed as monitor stack)  


**Visualizer tool, github.com/dockersamples/docker-swarm-visualizer shows nodes, containers running on each node, follow directions**

```
#Add visualizer tool to swarm
sudo docker stack deploy -c {vcellroot}/docker/swarm/docker-stack-visualizer.yml vis
#Remove visulaizer tool from swarm
sudo docker stack rm vis
```

**Monitoring tool**
https://botleg.com/stories/monitoring-docker-swarm-with-cadvisor-influxdb-and-grafana/  

```
#Follow setup instuction on website to install monitor tool
#Remove monitor tool
sudo docker stack rm monitor
```


# VCell project Servers

## production Docker Swarm Cluster (RELEASE)

```bash
[vcell@vcell-node1 /opt/build/vcell/docker/build  38] $ sudo docker node ls
ID                            HOSTNAME                   STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
0x290pfnvtq8gf9a8uzlih8jb *   vcell-node1.cam.uchc.edu   Ready               Active              Reachable           18.03.0-ce
pznhvvcqu89xzao84jud7ci9i     vcell-node2.cam.uchc.edu   Ready               Active              Leader              18.03.0-ce
y6bshmo0nrkm6skwk1d5k63a0     vcellapi.cam.uchc.edu      Ready               Active              Reachable           18.03.0-ce
```

## development Docker Swarm Cluster (ALPHA)

```bash
[vcell@vcell-node3 ~  1] $ sudo docker node ls
ID                            HOSTNAME                     STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
q3ghtrdt0qnz8i3gb56y4zbue *   vcell-node3.cam.uchc.edu     Ready               Active              Leader              18.03.0-ce
nmym8qspowiu1d850ok9vwtk2     vcell-node4.cam.uchc.edu     Ready               Active              Reachable           18.03.0-ce
kn3y3t1rw8skyua80j841ohng     vcellapi-beta.cam.uchc.edu   Ready               Active              Reachable           18.03.0-ce
```

## vcell-oracle.cam.uchc.edu, NOT Docker, (VCell database)  
Oracle database, has all models, sim status, all user data,  everything  

## vcell-docker.cam.uchc.edu, NOT Swarm node, (VCell docker registry) [details](build/README_Registry.md)
(internal docker registry, contains images for vcell system, tagged by git-hub commit hashes)  

# vcell-service.cam.uchc.edu, NOT Docker (SLURM submit node)
Slurm partition "vcell2"  
sbatch /share/apps/vcell3/htclogs/V_REL_39393939393_0_0.slurm.sub  
squeue  
sinfo  
sdelete  

Xanadu01 ...  
Shangrila-01 ...  

# elk.cam.uchc.edu, NOT Docker, (Logging system) [details](swarm/README_ElasticStack.md)  
logstash (NOT docker, on elk.cam.uchc.edu, collects logs from nodes)  
elasticsearch (NOT docker, on elk.cam.uchc.edu, search engine for logs)  
kibana (NOT docker, on elk.cam.uchc.edu, GUI to query logs)  


