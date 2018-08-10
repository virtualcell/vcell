##Random Info

**Reset simulation hasdata status**  
If VCell client simulation list view says 'hasdata=no' but there is data  
-----Find sim id (click 'i' button when sim is selected) -> theSimID  
-----log into vcell-node1 (or any node not in DMZ, not vcellapi or vcellapi-beta)  
-----Check data exists, give cmd " ls /share/apps/vcell3/users/boris/SimID_theSimID* "  
-----open oracle db tool (toad,sql,squirrel), log into vcell@vcell-db.cam.uchc.edu and do query " select * from vc_simulationjob where simref=theSimID; "  
-----If the query column 'hasdata' is blank then do update " update vc_simulationJob set hasdata='Y' where simref=theSimID; "  and " commit; "  
-----log into vcellapi(Rel) or vcellapi-beta(Alpha)  
-----Restart VCell docker scheduler service  with cmd " sudo docker service update --force --detach=false vcell{rel,alpha}_sched "  

**Rel restart, login vcellapi**  
sudo docker service update --force --detach=false vcellrel_activemqint  
sudo docker service update --force --detach=false vcellrel_activemqsim  
sudo docker service update --force --detach=false vcellrel_mongodb  
sudo docker service update --force --detach=false vcellrel_db  
sudo docker service update --force --detach=false vcellrel_data  
sudo docker service update --force --detach=false vcellrel_sched  
sudo docker service update --force --detach=false vcellrel_submit  
sudo docker service update --force --detach=false vcellrel_api  
​

**Alpha restart, login vcellapi-beta**  
sudo docker service update --force --detach=false vcellalpha_activemqint  
sudo docker service update --force --detach=false vcellalpha_activemqsim  
sudo docker service update --force --detach=false vcellalpha_mongodb  
sudo docker service update --force --detach=false vcellalpha_db  
sudo docker service update --force --detach=false vcellalpha_data  
sudo docker service update --force --detach=false vcellalpha_sched  
sudo docker service update --force --detach=false vcellalpha_submit  
sudo docker service update --force --detach=false vcellalpha_api  ​

**Build quickrun linux solvers**  
do this in a pristine checkout (cloned).  
git clone https://github.com/virtualcell/vcell-solvers.git  
cd vcell-solvers  
<put attached Dockerfile-local here>  
sudo docker build --tag=frm/vcell-solvers:latest -f Dockerfile-local .  
sudo docker run -it --rm -v /Users/schaff/.vcell/simdata/temp:/vcelldata frm/vcell-solvers:latest SundialsSolverStandalone_x64 /vcelldata/SimID_1460763637_0_.cvodeInput /vcelldata/SimID_1460763637_0_.ida  
-----where /Users/schaff/.vcell/simdata/temp is the simulation data directory (input file) mapped to /vcelldata inside the Docker container.  

 


## useful commands

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

