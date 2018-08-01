##Service configuration info

service has 1 image and configuration, manages 1 or more containers, container is a running image  
All services defined in ./swarm/docker-compose.yml (collection of services,volumes, dependencies,...)  


**--VCell Stack--(VCell docker swarm cluster) Docker VCell** [see vcell stack definition](swarm/docker-compose.yml)  
[script that creates environment variables file](docker/swarm/serverconfig-uch.sh)  
**api** (org.vcell.rest.VCellApiMain) [DockerFile](build/Dockerfile-api-dev) (REST web service provides external connectivity for VCell clients)  
**db** (cbit.vcell.message.server.db.DatabaseServer) [DockerFile](build/Dockerfile-db-dev) (services database requests)  
**data** (cbit.vcell.message.server.data.SimDataServer) [DockerFile](build/Dockerfile-data-dev) (services data request)  
**sched** (cbit.vcell.message.server.dispatcher.SimulationDispatcher) [DockerFile](build/Dockerfile-sched-dev) (manages solver status, decides which simulations run in what order)  
**submit** (cbit.vcell.message.server.batch.sim.HtcSimulationWorker) [DockerFile](build/Dockerfile-submit-dev) (creates solver run arguments and config, slurm submission script, writes to users/users/simtask.xml, sends script to slurm)  
**mongodb** [DockerFile](build/mongo/Dockerfile) (hosts gridfs for temp store of large messages, error logging)  
**activemqint** (webcenter/activemq:5.14.3) (handles messages between services)  
**activemqsim** (webcenter/activemq:5.14.3) (handles messages from running solvers and 'sched' service)  

**--Logger Stack--(VCell docker swarm cluster) (Docker Logging,not necressary for VCell) ** [see logger stack definition](swarm/docker-stack-logspout.yml)  
**logspout** (reads from docker daemon logs wich come from stdout of services, global, starts on every node, gets log stream from all running containers, sends to logstash)


**Linux Logging elk client services** [see ELK definition](swarm/README_ELK.yml)  
(elk.cam.uchc.edu)  
**logstash** (listens for logging events, forwards to elasticsearch)  
**elasticsearch** (searchables database of logging events)
**kibana** (users interface for elastic search)


**Not a service but sent to all hpc nodes during deployment**
**Docker Solvers,Preprocess,Postprocess image**  
**vcell-batch** [DockerFile](build/Dockerfile-batch-dev)  


