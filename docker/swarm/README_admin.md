## Restart sites

### Restart a site (from production swarm cluster, e.g. vcellapi.cam.uchc.edu or vcell-node1 or vcell-node2)

#### using vcell-services administrative script as user vcell on cluster nodes.
```bash
vcell-services.sh restart rel
#vcell-services.sh restart alpha  
#vcell-services.sh restart test  
```

#### Manually restart all services 
(from a vcellapi, vcellapi-beta, vcellapi-test for rel, alpha, test respectively)
```bash
```bash
export SITE=rel
#export SITE=alpha
#export SITE=test
sudo docker service update --force --detach=false vcell${SITE}_activemqint  
sudo docker service update --force --detach=false vcell${SITE}_activemqsim  
sudo docker service update --force --detach=false vcell${SITE}_mongodb  
sudo docker service update --force --detach=false vcell${SITE}_db  
sudo docker service update --force --detach=false vcell${SITE}_data  
sudo docker service update --force --detach=false vcell${SITE}_sched  
sudo docker service update --force --detach=false vcell${SITE}_submit  
sudo docker service update --force --detach=false vcell${SITE}_api  
```


## Simulation Message Queue administration
see also: [serverconfig-uch.sh](serverconfig-uch.sh) for current definition of VCELL_JMS_SIM_RESTPORT_EXTERNAL.

### activemq console (not exposed to the public internet)
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

### Test site activemq console (not exposed to the public internet)
```bash
_site_port_offset=3 # 0 for production, 1 for beta, 2 for alpha, 3 for test
VCELL_JMS_SIM_RESTPORT_EXTERNAL=$((8161 + _site_port_offset))
open http://vcellapi-test.cam.uchc.edu:${VCELL_JMS_SIM_RESTPORT_EXTERNAL}/admin/queues.jsp
# login with admin/admin
```

## cron job to restart scheduler on release site every 6 hours
see vcellapi.cam.uchc.edu:/root/vcell_cron_service_update.sh
