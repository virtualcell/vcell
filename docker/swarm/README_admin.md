### Restart Release Site (from production swarm cluster, e.g. vcellapi.cam.uchc.edu or vcell-node1 or vcell-node2)

sudo docker service update --force --detach=false vcellrel_activemqint  
sudo docker service update --force --detach=false vcellrel_activemqsim  
sudo docker service update --force --detach=false vcellrel_mongodb  
sudo docker service update --force --detach=false vcellrel_db **  
sudo docker service update --force --detach=false vcellrel_data  **  
sudo docker service update --force --detach=false vcellrel_sched  **  
sudo docker service update --force --detach=false vcellrel_submit **  
sudo docker service update --force --detach=false vcellrel_api  **  

### Restart Alpha Site (from development swarm cluster, e.g. vcellapi-beta.cam.uchc.edu or vcell-node3 or vcell-node4)  

sudo docker service update --force --detach=false vcellalpha_activemqint  
sudo docker service update --force --detach=false vcellalpha_activemqsim  
sudo docker service update --force --detach=false vcellalpha_mongodb  
sudo docker service update --force --detach=false vcellalpha_db  
sudo docker service update --force --detach=false vcellalpha_data  
sudo docker service update --force --detach=false vcellalpha_sched  
sudo docker service update --force --detach=false vcellalpha_submit  
sudo docker service update --force --detach=false vcellalpha_api  

