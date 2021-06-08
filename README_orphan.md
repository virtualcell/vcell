##Random Info

**Rel restart, login vcellapi**  
sudo docker service update --force --detach=false vcellrel_activemqint  
sudo docker service update --force --detach=false vcellrel_activemqsim  
sudo docker service update --force --detach=false vcellrel_mongodb  
sudo docker service update --force --detach=false vcellrel_db  
sudo docker service update --force --detach=false vcellrel_data  
sudo docker service update --force --detach=false vcellrel_sched  
sudo docker service update --force --detach=false vcellrel_submit  
sudo docker service update --force --detach=false vcellrel_api  
(not used) sudo docker service update --force --detach=false vcellrel_opt  


**Alpha restart, login vcellapi-beta**  
sudo docker service update --force --detach=false vcellalpha_activemqint  
sudo docker service update --force --detach=false vcellalpha_activemqsim  
sudo docker service update --force --detach=false vcellalpha_mongodb  
sudo docker service update --force --detach=false vcellalpha_db  
sudo docker service update --force --detach=false vcellalpha_data  
sudo docker service update --force --detach=false vcellalpha_sched  
sudo docker service update --force --detach=false vcellalpha_submit  
sudo docker service update --force --detach=false vcellalpha_api
(not used) sudo docker service update --force --detach=false vcellalpha_opt  

**Build quickrun linux solvers**  
do this in a pristine checkout (cloned).  
git clone https://github.com/virtualcell/vcell-solvers.git  
cd vcell-solvers  
<put attached Dockerfile-local here>  
sudo docker build --tag=frm/vcell-solvers:latest -f Dockerfile-local .  
sudo docker run -it --rm -v /Users/schaff/.vcell/simdata/temp:/vcelldata frm/vcell-solvers:latest SundialsSolverStandalone_x64 /vcelldata/SimID_1460763637_0_.cvodeInput /vcelldata/SimID_1460763637_0_.ida  
-----where /Users/schaff/.vcell/simdata/temp is the simulation data directory (input file) mapped to /vcelldata inside the Docker container.  

**Docker Change number of service tasks running, increase tasks for a service in a swarm**  

sudo docker service ls  
docker service scale <SERVICE-ID>=<NUMBER-OF-TASKS>  
Example:  
----sudo docker service scale vcellrel_data=2  
----sudo docker service ps vcellrel_data  



**Remove Broken Swarm**  
On each docker node that is part of broken swarm:  
  260  sudo systemctl stop docker.service  
  261  sudo rm -rf /var/lib/docker/swarm  
  264  sudo systemctl start docker.service  
Recreate swarm  
  273  hostname -i (on vcellapi or vcellapi-beta)  
  274  sudo docker swarm init --advertise-addr {155.37.255.68 or 155.37.255.90}  
  276  sudo docker swarm join-token manager (to create join token command)  
  	//On other nodes run following command to add them as managers  
  278  Example: sudo docker swarm join --token SWMTKN-1-18b6etsn3yn562xxxxxirf6uqnlmlmlbcvc4m5nxh-05d2jflrf5ox1z0w9y0nt1f7l 155.37.255.68:2377  
  //On vcellapi redeploy  
   sudo env $(cat  /usr/local/deploy/config/server_rel_7.2.0_39_0b71a4d.config | xargs) docker stack deploy --prune -c /usr/local/deploy/config/docker-compose_0b71a4d.yml vcellrel  
  286  sudo docker stack ps vcellalpha | grep -i running  
  287  exit  
  288  sudo docker node update --label-add zone=INTERNAL 8yfu0lrwfkoil8k038rkm3edj  
  289  sudo docker node inspect | grep -i label  
  290  sudo docker node inspect self | grep -i label  


**vcellNagios monitoring**  
Nagios monitor calls Nagios plugin manager at vcellapi.cam.uchc.edu which executes script:  
   vcell@vcellapi:/usr/lib64/nagios/plugins/vcellNagios.sh (returns status info to Nagios monitor)  
vcellNagios.sh source code located in github project https://github.com/virtualcell/vcell.git at location /vcell/docker/swarm/vcellNagios.sh  
vcellNagios.sh calls Rel web service https://vcellapi/health?check={login,sim}  
  web service defined by org.vcell.rest.VCellApiApplication->  
      org.vcell.rest.health.HealthRestlet.HealthRestlet->  
        org.vcell.rest.health.HealthService(started as thread in org.vcell.rest.VCellApiMain)->  
          cbit.vcell.server.VCellConnection->{login,runsims}  


**DBBackupAndClean**  
vcell-node1:/opt/build/frm/dbbackupclean  
//Normal, without debug  
java -cp ./maven-jars/*:./vcell-server-0.0.1-SNAPSHOT.jar:./ojdbc6-11.2.0.4.jar:./ucp-11.2.0.4.jar:./vcell-oracle-0.0.1-SNAPSHOT.jar -Dvcell.server.dbDriverName=oracle.jdbc.driver.OracleDriver cbit.vcell.modeldb.DBBackupAndClean delsimsdisk vcell-db vcell /opt/build/frm/dbpw.txt  vcelldborcl.cam.uchc.edu /tmp /share/apps/vcell3/users  

//Normal, without debug, single user only  
java -Xms200m -Xmx1500m -cp ./maven-jars/*:./vcell-server-0.0.1-SNAPSHOT.jar:./ojdbc6-11.2.0.4.jar:./ucp-11.2.0.4.jar:./vcell-oracle-0.0.1-SNAPSHOT.jar -Dvcell.server.dbDriverName=oracle.jdbc.driver.OracleDriver cbit.vcell.modeldb.DBBackupAndClean delsimsdisk2 vcell-db vcell /opt/build/frm/dbpw.txt  vcelldborcl.cam.uchc.edu /tmp /share/apps/vcell3/users Juliajessica  

//With debug  (note: eclipse debug config: vcell-server,vcell-node1,30301)  
java  -agentlib:jdwp=transport=dt_socket,server=y,address=30301,suspend=y  -cp ./maven-jars/*:./vcell-server-0.0.1-SNAPSHOT.jar:./ojdbc6-11.2.0.4.jar:./ucp-11.2.0.4.jar:./vcell-oracle-0.0.1-SNAPSHOT.jar -Dvcell.server.dbDriverName=oracle.jdbc.driver.OracleDriver cbit.vcell.modeldb.DBBackupAndClean delsimsdisk vcell-db vcell /opt/build/frm/dbpw.txt  vcelldborcl.cam.uchc.edu /tmp /share/apps/vcell3/users  

//DBBackupAndClean Scheduled Task (vcell-db.cam.uchc.edu->'scheduled task'->'Task Scheduler Library'->cleanAndBackupDB->properties->Actions->'Edit...')
Program/script: "C:\Program Files (x86)\Java\jre1.8.0_121\bin\java.exe"
Add arguments: -jar DBBackupAndClean_Ampli.jar cleanandbackup vcell-db vcell cbittech vcelldborcl.cam.uchc.edu \\cfs05\vcell\temp \\cfs03\shared\archive\vcell\VCDBdumps
Start in: C:\fromDBS3


**Delete large listener.log file**  
log in to vcell-db (a windows host) as admin user using 'Remote Desktop Connection'  
start 'cmd' window as system administrator (start->'command prompt->'rt-click, 'run as administrator')  
//make sure of path to listener log  
dir C:\Oracle\diag\tnslsnr\VCELL-DB\listener\trace\listener.log  
//Start database listener control app and turn off logging (so log file can be deleted)  
lsnrctl  
   show log_staus  
   set log_status off  
   exit  
//Delete database listener log file  
del C:\Oracle\diag\tnslsnr\VCELL-DB\listener\trace\listener.log  
//Log off from vcell-db DO NOT SHUTDOWN (or just quit/close the 'Remote Desktop Connection')  


  
***Info pub models with results, published models vcml with sims removed, IonItems----------------------------***  

Committed class vcell-server:cbit.vcell.tools.IonItems.java to generate info in .csv format  

1. Create debug configuration for IonItems, Export from eclipse as runnable jar, choose 'extract required libs into jar'  

2. transfer runnable jar to vcell-node1 (already done):  

    example: scp /c/temp/cbit_vcell_tools_IonItems.jar vcell@vcell-node1:/opt/build/frm  

3. make sure 'natlibs' dir exist (for hdf5), copy libs from VCellGitProjectRoot\vcell\nativelibs\linux64 to 'natlibs' dir  
   make sure oracle,ojdbc,ucp libs are present, copy jars from  
       VCellGitProjectRoot/vcell\vcell-oracle\target\vcell-oracle-0.0.1-SNAPSHOT-sources.jar  
       VCellGitProjectRoot/vcell\vcell-oracle\target\maven-jars\ojdbc6-11.2.0.4.jar  
       VCellGitProjectRoot/vcell\vcell-oracle\target\maven-jars\ucp-11.2.0.4.jar  

NOTE: enter following after 'java' for debug (-agentlib:jdwp=transport=dt_socket,server=y,address=30301,suspend=y)  

4a. Run to generate .csv report, expects vcellDB password to be enetered on console  

    java -cp ./dbbackupclean/ojdbc6-11.2.0.4.jar:./dbbackupclean/ucp-11.2.0.4.jar:./dbbackupclean/vcell-oracle-0.0.1-SNAPSHOT.jar:./cbit_vcell_tools_IonItems.jar  
       cbit.vcell.tools.IonItems 'jdbc:oracle:thin:@vcell-db.cam.uchc.edu:1521:vcelldborcl' '/share/apps/vcell3/users' publicmodelwithsims false | tee ./pubModelsSimIdOnly.txt  

4b. Run to save vcml of published modesl with sims that have no data or exceptions removed from vcml  

    java -cp ./dbbackupclean/ojdbc6-11.2.0.4.jar:./dbbackupclean/ucp-11.2.0.4.jar:./dbbackupclean/vcell-oracle-0.0.1-SNAPSHOT.jar:./cbit_vcell_tools_IonItems.jar:./maven-  jars/jhdf5_2.10-2.9.jar  
      -Djava.library.path="./natlibs"  cbit.vcell.tools.IonItems "jdbc:oracle:thin:@vcell-db.cam.uchc.edu:1521:vcelldborcl" "/share/apps/vcell3/users" publishedmodelvcml   /share/apps/vcell3/users/ionvcml 0 0  

4c. Run to read vcml saved from 4b and compare to vcml from db and print the sims that had to be removed  

    java -cp ./dbbackupclean/ojdbc6-11.2.0.4.jar:./dbbackupclean/ucp-11.2.0.4.jar:./dbbackupclean/vcell-oracle-0.0.1-SNAPSHOT.jar:./cbit_vcell_tools_IonItems.jar:./maven-jars/jhdf5_2.10-2.9.jar  
      -Djava.library.path="./natlibs"  cbit.vcell.tools.IonItems "jdbc:oracle:thin:@vcell-db.cam.uchc.edu:1521:vcelldborcl" /share/apps/vcell3/users/ionvcml publishedmodelvcmldiff  
      
4d.  Run to save vcml from TestSuit

    java  -cp ./dbbackupclean/ojdbc6-11.2.0.4.jar:./dbbackupclean/ucp-11.2.0.4.jar:./dbbackupclean/vcell-oracle-0.0.1-SNAPSHOT.jar:./cbit_vcell_tools_IonItems.jar:./maven-jars/jhdf5_2.10-2.9.jar  
    -Djava.library.path="./natlibs"  cbit.vcell.tools.IonItems "jdbc:oracle:thin:@vcell-db.cam.uchc.edu:1521:vcelldborcl"  /share/apps/vcell3/users/ionts testsuitemodelvcml

***----------------------------------------------------------------------------------------------------------***

## useful commands

```bash
//Cleanup docker log files, disk space problem
//ssh to each of Alpha(vcellapi-beta,vcell-node3,vcell-node4) and Rel(vcellapi,vcell-node1,vcell-node2)

//find log file for specific container
sudo docker ps
sudo docker inspect --format='{{.LogPath}}' DOCKER_CONTAINER_ID

//Find all docker container logs larger than 100 megabytes
sudo find /var/lib/docker -name '*.log' -size +100M -print | sudo xargs -L 1 ls -la

//Clear data from docker log file
sudo su
cat /dev/null >DOCKER_LOG_FILE_PATH

//create/edit docker config to limit log size
sudo nano /etc/docker/daemon.json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m",
    "max-file": "3",
    "labels": "vcell-node3",
    "env": "os,customer"
  }
}
sudo systemctl restart docker //restart docker to read in new configuration
sudo journalctl -r -u docker //view systemd logs for docker to check for problems during start
systemctl status docker //make sure docker started

//print size of root filesystem on all VCell nodes
ssh vcell@vcellapi df -H | grep ".*/$" ; ssh vcell@vcell-node1 df -H | grep ".*/$" ; ssh vcell@vcell-node2 df -H | grep ".*/$" ; ssh vcell@vcellapi-beta df -H | grep ".*/$" ; ssh vcell@vcell-node3 df -H | grep ".*/$" ; ssh vcell@vcell-node4 df -H | grep ".*/$"
```

```bash
docker stack services local
ID                  NAME                MODE                REPLICAS            IMAGE                               PORTS
5hrlf3u1kdgr        local_visualizer    replicated          1/1                 dockersamples/visualizer:stable     *:30013->8080/tcp
cflee8p55yph        local_mongodb       replicated          1/1                 schaff/vcell-mongo:7.0.0-alpha.4    *:27020->27017/tcp
evpnt4b10dbs        local_activemq      replicated          1/1                 webcenter/activemq:5.14.3           *:61619->61616/tcp
tpo0wfjmgqeo        local_api           replicated          1/1                 schaff/vcell-api:7.0.0-alpha.4      *:30014->8000/tcp,*:8083->8080/tcp
xkb1tp4znjha        local_master        replicated          1/1                 schaff/vcell-master:7.0.0-alpha.4   *:30012->8000/tcp
```

```bash
docker stack ps local
ID               NAME                   IMAGE                               NODE                    DESIRED STATE   CURRENT STATE           ERROR  PORTS
mlvxxlvxedvh     local_master.1         schaff/vcell-master:7.0.0-alpha.4   linuxkit-025000000001   Running         Running 4 minutes ago
z164vsxiorox     local_api.1            schaff/vcell-api:7.0.0-alpha.4      linuxkit-025000000001   Running         Running 4 minutes ago
y0v50e8r31ce      \_ local_api.1        schaff/vcell-api:7.0.0-alpha.4      linuxkit-025000000001   Shutdown        Shutdown 4 minutes ago
9pjpi1lvi46r     local_visualizer.1     dockersamples/visualizer:stable     linuxkit-025000000001   Running         Running 25 minutes ago
3gdwdlijfxdz     local_mongodb.1        schaff/vcell-mongo:7.0.0-alpha.4    linuxkit-025000000001   Running         Running 26 minutes ago
k2hyfkcyqwpx     local_activemq.1       webcenter/activemq:5.14.3           linuxkit-025000000001   Running         Running 26 minutes ago
w059r4hbdgq9     local_master.1         schaff/vcell-master:7.0.0-alpha.4   linuxkit-025000000001   Shutdown        Shutdown 4 minutes ago
```

```bash
deploy schaff$ docker service ls
ID                  NAME                MODE                REPLICAS            IMAGE                               PORTS
evpnt4b10dbs        local_activemq      replicated          1/1                 webcenter/activemq:5.14.3           *:61619->61616/tcp
tpo0wfjmgqeo        local_api           replicated          1/1                 schaff/vcell-api:7.0.0-alpha.4      *:30014->8000/tcp,*:8083->8080/tcp
xkb1tp4znjha        local_master        replicated          1/1                 schaff/vcell-master:7.0.0-alpha.4   *:30012->8000/tcp
cflee8p55yph        local_mongodb       replicated          1/1                 schaff/vcell-mongo:7.0.0-alpha.4    *:27020->27017/tcp
5hrlf3u1kdgr        local_visualizer    replicated          1/1                 dockersamples/visualizer:stable     *:30013->8080/tcp
```

```bash
docker service logs -f local_api
```

```bash
curl --verbose --insecure "https://vcellapi.cam.uchc.edu/admin/jobs?completed=false&stopped=false&serverId=REL" | jq '.'
(see AdminJobsRestlet for details)
```

