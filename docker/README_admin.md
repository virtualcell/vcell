building and deploying two scripts
  vcell/docker/build/build.sh (building portable self contained container with vcell software)
  vcell/docker/swarm/deploy.sh (add configuration for deployment specific installation, install and restart services (stack))

administration
  vcell/docker/swarm/cli.py

  invoke (login to vcell-node1 as vcell, /opt/build/vcell/docker/swarm/cli.py)

  dont reboot more than one Docker swarm "manager" node at a time. 
  	(vcellapi, vcell-node1, vcell-node2)  
  	or (vcellapi-beta, vcell-node3, vcell-node4)

curl https://vcellapi.cam.uchc.edu/health?check=login


admin logs for docker daemons

on each swarm node:
sudo docker event log


for debugging slurm jobs, look in the Slurm log for that job (Slurm Job Name is "V_REL_{simid}_{jobid}_{taskid}"):

log file is /share/apps/vcell3/htclogs/V_REL_{simid}_{jobid}_{taskid}.slurm.log 
submission script is /share/apps/vcell3/htclogs/V_REL_{simid}_{jobid}_{taskid}.slurm.sub


open https://vcellapi.cam.uchc.edu/biomodel (ugly api browser for vcell REST services ... incomplete coverage of API).

for activemq website:
[vcell@vcell-node1 ~  13] $ sudo docker service ls | grep activemq
nndtf3phq7v7        vcellrel_activemqint   replicated          1/1                 webcenter/activemq:5.14.3   *:30005->61616/tcp, *:30006->8161/tcp
mbniasxc2byn        vcellrel_activemqsim   replicated          1/1                 webcenter/activemq:5.14.3   *:8161->8161/tcp, *:61616->61616/tcp

open http://vcellapi.cam.uchc.edu:30006  (for vcellrel_activemqint ... note port mappings above)
open http://vcellapi.cam.uchc.edu:8161   (for vcellrel_activemqsim ... note port mappings above)

Nagios logs (http://nagios.cam.uchc.edu/nagios/).

open http://elk.cam.uchc.edu:5601/

there are two other "docker stacks" for monitoring and debugging.
1) very lightweight graphical view of which containers run on which nodes ... equivalent to "sudo docker stack ps <stack_name> (vcellrel, vcellalpha)"
2) more interesting dashboard for resources used by docker containers (cadvisor, influxdb, graphana) ... see Frank to enable this stack (some configuration required).



log4j xml file for each container specifying the logging levels on a java package granularity
for vcell-api
look in <vcell-root>/docker/build/Dockerfile-api-dev
     WORKDIR /usr/local/app
     COPY ./docker/build/vcell-api.log4j.xml .
     (vcell api java code can find the log4j configuration at /usr/local/app ... a path inside the container)

     ENTRYPOINT java \
     	...
		-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
		-Dlog4j.configurationFile=/usr/local/app/vcell-api.log4j.xml \
		...

what happens when the events that I want to see are not logged to stdout or log4j (so wont get to ELK)?

copy log4j xml file out, change logging level, copy back in ... within 5 seconds the new log configuration will take effect.

sudo docker cp 7e8c543f6211:/usr/local/app/vcell-api.log4j.xml /tmp/local.xml
sudo docker cp 7e8c543f6211:/usr/local/app/vcell-api.log4j.xml /tmp/local.xml

REBOOTING:




TODO:
find a way of storing some info from HTC logs in ELK.
NAGIOS server is not showing VCell services. (see http://nagios.cam.uchc.edu/nagios/ ... vcell critical)


