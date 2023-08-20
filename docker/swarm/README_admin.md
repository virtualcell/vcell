## Restart sites

### Restart Release Site (from production swarm cluster, e.g. vcellapi.cam.uchc.edu or vcell-node1 or vcell-node2)

sudo docker service update --force --detach=false vcellrel_activemqint  
sudo docker service update --force --detach=false vcellrel_activemqsim  
sudo docker service update --force --detach=false vcellrel_mongodb  
sudo docker service update --force --detach=false vcellrel_db  
sudo docker service update --force --detach=false vcellrel_data  
sudo docker service update --force --detach=false vcellrel_sched  
sudo docker service update --force --detach=false vcellrel_submit  
sudo docker service update --force --detach=false vcellrel_api  
sudo docker service update --force --detach=false vcellrel_web  
(not used) sudo docker service update --force --detach=false vcellrel_opt  

### Restart Alpha Site (from development swarm cluster, e.g. vcellapi-beta.cam.uchc.edu or vcell-node3 or vcell-node4)  

sudo docker service update --force --detach=false vcellalpha_activemqint  
sudo docker service update --force --detach=false vcellalpha_activemqsim  
sudo docker service update --force --detach=false vcellalpha_mongodb  
sudo docker service update --force --detach=false vcellalpha_db  
sudo docker service update --force --detach=false vcellalpha_data  
sudo docker service update --force --detach=false vcellalpha_sched  
sudo docker service update --force --detach=false vcellalpha_submit  
sudo docker service update --force --detach=false vcellalpha_api  
sudo docker service update --force --detach=false vcellalpha_web  
(not used) sudo docker service update --force --detach=false vcellalpha_opt  


## Simulation Message Queue administration
see also: [serverconfig-uch.sh](serverconfig-uch.sh) for current definition of VCELL_JMS_SIM_RESTPORT_EXTERNAL.

### Release site activemq console (not exposed to the public internet)
```bash
_site_port_offset=0 # 0 for production, 1 for beta, 2 for alpha, 3 for test
VCELL_JMS_SIM_RESTPORT_EXTERNAL=$((8161 + _site_port_offset))
open http://vcellapi.cam.uchc.edu:${VCELL_JMS_SIM_RESTPORT_EXTERNAL}/admin/queues.jsp
# login with admin/admin
```

### Alpha site activemq console (not exposed to the public internet)
```bash
_site_port_offset=2 # 0 for production, 1 for beta, 2 for alpha, 3 for test
VCELL_JMS_SIM_RESTPORT_EXTERNAL=$((8161 + _site_port_offset))
open http://vcellapi-beta.cam.uchc.edu:${VCELL_JMS_SIM_RESTPORT_EXTERNAL}/admin/queues.jsp
# login with admin/admin
```

## cron job to restart scheduler every 6 hours
see vcellapi.cam.uchc.edu:/root/vcell_cron_service_update.sh

## force alpha site to replicas=0
```bash
sudo docker service update --force --detach=false --replicas=0 vcellalpha_activemqint
sudo docker service update --force --detach=false --replicas=0 vcellalpha_activemqsim
sudo docker service update --force --detach=false --replicas=0 vcellalpha_mongodb
sudo docker service update --force --detach=false --replicas=0 vcellalpha_db
sudo docker service update --force --detach=false --replicas=0 vcellalpha_data
sudo docker service update --force --detach=false --replicas=0 vcellalpha_sched
sudo docker service update --force --detach=false --replicas=0 vcellalpha_submit
sudo docker service update --force --detach=false --replicas=0 vcellalpha_api
```

## force alpha site to replicas=1
```bash
sudo docker service update --force --detach=false --replicas=1 vcellalpha_activemqint
sudo docker service update --force --detach=false --replicas=1 vcellalpha_activemqsim
sudo docker service update --force --detach=false --replicas=1 vcellalpha_mongodb
sudo docker service update --force --detach=false --replicas=1 vcellalpha_db
sudo docker service update --force --detach=false --replicas=1 vcellalpha_data
sudo docker service update --force --detach=false --replicas=1 vcellalpha_sched
sudo docker service update --force --detach=false --replicas=1 vcellalpha_submit
sudo docker service update --force --detach=false --replicas=1 vcellalpha_api
```

## force release site to replicas=0
```bash
sudo docker service update --force --detach=false --replicas=0 vcellrel_activemqint
sudo docker service update --force --detach=false --replicas=0 vcellrel_activemqsim
sudo docker service update --force --detach=false --replicas=0 vcellrel_mongodb
sudo docker service update --force --detach=false --replicas=0 vcellrel_db
sudo docker service update --force --detach=false --replicas=0 vcellrel_data
sudo docker service update --force --detach=false --replicas=0 vcellrel_sched
sudo docker service update --force --detach=false --replicas=0 vcellrel_submit
sudo docker service update --force --detach=false --replicas=0 vcellrel_api
```

## force release site to replicas=1
```bash
sudo docker service update --force --detach=false --replicas=1 vcellrel_activemqint
sudo docker service update --force --detach=false --replicas=1 vcellrel_activemqsim
sudo docker service update --force --detach=false --replicas=1 vcellrel_mongodb
sudo docker service update --force --detach=false --replicas=1 vcellrel_db
sudo docker service update --force --detach=false --replicas=1 vcellrel_data
sudo docker service update --force --detach=false --replicas=1 vcellrel_sched
sudo docker service update --force --detach=false --replicas=1 vcellrel_submit
sudo docker service update --force --detach=false --replicas=1 vcellrel_api
```

