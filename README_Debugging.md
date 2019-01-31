**Reset simulation hasdata status (easier method)**  
If VCell client simulation list view says 'hasdata=no' but there is data  
-----Find sim id (click 'i' button when sim is selected) -> theSimID  
-----log into vcell-node1 (or any node not in DMZ, not vcellapi or vcellapi-beta)  
-----Check data exists, give cmd " ls /share/apps/vcell3/users/boris/SimID_theSimID* "  
-----open oracle db tool (toad,sql,squirrel), log into vcell@vcell-db.cam.uchc.edu and do query " select * from vc_simulationjob where simref=theSimID; "  
-----If the query column 'hasdata' is blank then do update " update vc_simulationJob set hasdata='Y' where simref=theSimID; "  and " commit; "  
-----log into vcellapi(Rel) or vcellapi-beta(Alpha)  
-----Restart VCell docker scheduler service  with cmd " sudo docker service update --force --detach=false vcell{rel,alpha}_sched "  

**Reset simulation hasdata status (if easier method doesn't work, try this)**  
//-----------------------------------------------------------------------------------------------------------------------  
In SimulationJobTable.getSimulationJobStatus(...) find line where 'parsedHasData' is set, on next line(~147) put conditional breakpoint (to monitor hasdata value):  
//-----  
if(parsedSimKey.toString().equals("140980969") || parsedSimKey.toString().equals("140984604")) {  
	System.out.println("-----hasData---------------------------------------------");  
	System.out.println("SimulationJobTable.getSimulationJobStatus(...) "+parsedSimKey.toString()+" "+parsedHasData);  
	System.out.println("-----hasData---------------------------------------------");  
}  
return false;  
//-----  
In SimulationJobTable.getSQLUpdateList(...) find line '//hasData' block of code, after that block (~275) insert conditional breakpoint as follows (change simids as appropriate):  
//-----  
String parsedSimKey = simulationJobStatus.getVCSimulationIdentifier().getSimulationKey().toString();
if(parsedSimKey.toString().equals("149162204") ||
		parsedSimKey.toString().equals("149162470") ||
		parsedSimKey.toString().equals("149162959") ||
		parsedSimKey.toString().equals("149163092") ||
		parsedSimKey.toString().equals("149163722")) {
	System.out.println("-----set hasdta------------------------------------------------------------");  
	System.out.println("-----SimulationJobTable.getSqlUpdateList(...) "+simulationJobStatus);  
	String changeStr = "hasData=null,";  
	if(buffer.toString().endsWith(changeStr)) {  
		System.out.println("--ORIGINAL="+buffer.toString());  
		buffer.setLength(buffer.length()-changeStr.length());  
		buffer.append("hasData='Y',");  
		System.out.println("--CHANGED="+buffer.toString());		  
	}  
	System.out.println("-----set hasdata-----------------------------------------------------------");  
}  
return false;  
//-----  
Start remote debug session on the vcell-sched service (using vcell-server project)
-----sudo docker stack ps vcellrel | grep -i running ( to find vcell-sched host for remote debug)  
-----sudo docker service ls (to find service to attach to)  
-----ssh to node servcie is running on and do sudo docker ps (find container name of vcell-sched  
----- sudo docker container logs -f {containerID}  
Let conditional code run for awhile until the database and services get synchronized (a few minutes)
-----Restart VCell docker scheduler service  with cmd " sudo docker service update --force --detach=false vcell{rel,alpha}_sched "  
//-----------------------------------------------------------------------------------------------------------------------


**Find slurm sim logs**  
login vcell@vcell-service  
sacct --user=vcell --format="JobID,JobName%30,NodeList,State,Submit,start,end,ExitCode" -S 2018-09-01 | grep -i -e vcellSimID  
--e.g. sacct --user=vcell --format="JobID,JobName%30,NodeList,State,Submit,start,end,ExitCode" -S 2018-09-01 | grep -i -e 140980636 -e 140980969  
ls -U -1 /share/apps/vcell3/htclogs/V\_[REL,ALPHA]\_{vcellSimID*,...}  
--e.g. ls -U -1 /share/apps/vcell3/htclogs/V\_REL\_{140980636*,140980969*,140981229^,140983532*,140984604*,140981909*,140983177*}  
less /share/apps/vcell3/htclogs/{JobName}.slurm.log  //JobName from sacct query
--e.g less /share/apps/vcell3/htclogs/V\_ALPHA\_139363583\_0\_0.slurm.log  
grep 'job running on host'  /share/apps/vcell3/htclogs/V\_[REL,ALPHA]\_{vcellSimID*,...}.slurm.log  
--e.g.grep 'job running on host'  /share/apps/vcell3/htclogs/V\_REL\_{140980636*,140980969*,140981229*,140983532*,140984604*,140981909*,140983177*}.slurm.log  
-----------------------------------------------------  
**Find where simjob xxx.slurm.submit:TMPDIR originates in VCell deployment process**  
select 'vcell' project in eclipse, Search->File..., for "using TMPDIR=$TMPDIR" (no double quotes), in *.java, whole workspace  
Finds SlurmProxy.java, line references local variable "slurm_tmpdir"  
slurm_tmpdir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir)  
PropertyLoader.slurm_tmpdir = "vcell.slurm.tmpdir"  
Search {vcellroot}/docker for "vcell.slurm.tmpdir" (no double quotes)  
Finds {vcellroot}/docker/build/Dockerfile-submit-dev, -Dvcell.slurm.tmpdir="${slurm_tmpdir}"  
Search {vcellroot}/docker for "slurm_tmpdir" (no double quotes)  
Finds {vcellroot}/docker/swarm/docker-compose.yml, slurm_tmpdir=${VCELL_SLURM_TMPDIR}  
Search {vcellroot}/docker for "VCELL_SLURM_TMPDIR" (no double quotes)  
Finds {vcellroot}/docker/swarm/serverconfig-uch.sh, VCELL_SLURM_TMPDIR=/scratch/vcell  
-----------------------------------------------------   
 **See if VCell hpc nodes are alive (may hang if a node can't be reached)**  
 login vcell@vcell-service   
 sinfo -N -h -p vcell2 --Format='nodelist' |   xargs  -n 1  -I {} sh -c 'echo {}; ssh {} date || (true);'  
----------------------------------------------------- 

sudo yum install -y epel-release  
sudo yum-config-manager --enable epel  
sudo yum install hdf5-devel  
sudo yum group install "Development Tools"  

install clion from jetbrains.com (nees license)  

//Refer to these for detailed descriptions of solvers
//cbit.vcell.solver.SolverDescription (defines solvers in VCell)  
//cbit.vcell.solver.SolverExecutable (defines actual executables used in SolverDescription)  

start clion, when version control ask give "https://github.com/virtualcell/vcell-solvers.git"  
clion will attempt to build default solver to begin with  
open top-level CmakeLists.txt, look for option(....) that defines targets to build  

// https://github.com/virtualcell/vcell-solvers/DockerFile (docker definition for building vcell-solver, usually built on DockerHub, could be made local->vcell-docker.cam.uchc.edu)  

option(OPTION_TARGET_MESSAGING "Messaging (requires libcurl)" off) // Flag, tells VCellMessaging sub-project to use/not-use Activemq, (All Solvers use)  
option(OPTION_TARGET_PARALLEL "Parallel Solver (only chombo for now)" off) // Flag, tells VCellChombo sub-project to whether to use 'parallell' version of chombo (doesn't build now) (VCellChombo2D,VCellChombo3D)  
option(OPTION_TARGET_PETSC "PETSc Solver (only FV for now)" off) // Flag, tells the VCell sub-project to build FiniteVolume_PETSc solver instead of FiniteVolume  
option(OPTION_TARGET_DOCS "Generate Doxygen documentation" on) // creates web page heirarchy for c++ projects (Not Used)  
**build solver executables**  
option(OPTION_TARGET_CHOMBO2D_SOLVER "Chombo2D Solver" off) // tells VCellChombo sub-project to build 2D version (VCellChombo2D solver)  
option(OPTION_TARGET_CHOMBO3D_SOLVER "Chombo3D Solver" off) // tells VCellChombo sub-project to build 3D version (VCellChombo3D solver)  
option(OPTION_TARGET_SMOLDYN_SOLVER "only smoldyn" on) // tells bridgeVCellSmoldyn(defines both a library and executable) project to build standalone smoldyn executable (smoldyn solver)  
option(OPTION_TARGET_FV_SOLVER on)  // tells VCell sub-project to build FiniteVolume executables (FiniteVolume, FiniteVolume_PETSc depending on OPTION_TARGET_PETSC flag)  
----- make 2 solvers 1 semi-implicit and 1 fully-implict  
option(OPTION_TARGET_STOCHASTIC_SOLVER on) // tell Stochastic sub-project project to build Gibson non-spatial-stochastic (VCellStoch solver)  
option(OPTION_TARGET_NFSIM_SOLVER on) // tells NFsim_v1.11 sub-project to build NFsim executable (NFsim solver)  
option(OPTION_TARGET_MOVINGBOUNDARY_SOLVER on) // MBSolver sub-project to build MovingBoundary executable (MovingBoundary solver)  
option(OPTION_TARGET_SUNDIALS_SOLVER on) // tells IDAWin sub-project to build SundialsSolver executable (SundialsSolverStandalone solver)  
option(OPTION_TARGET_HY3S_SOLVERS on) // Hy3S sub-project to build Hybrid executables (Hybrid_EM, Hybrid_MIL, Hybrid_MIL_Adaptive)  

**Debugging a particular solver in a clion**  
open https://github.com/virtualcell/vcell-solvers/DockerFile, copy all RUN /usr/bin/cmake arguments  
File->Settings...->Build,Execution,Deployment->CMake->panelGUI  
-----open list window for 'CMake options', paste the copied options, remove trailing slashes
-----turn all options off execpt the solver you want to debug
-----apply->OK
-----Run->build
-----Run->Edit Configurations...
-----Choose executable to debug from the list, 

Service configuration info including **java class** [detailed instructions](docker/README_serviceInfo.md)

**Definition of required properties**  
[script that creates server environment variables file](docker/swarm/serverconfig-uch.sh)  
client local run  alpha  
-Dvcell.primarySimdatadir.internal=\\cfs05\vcell\users  
-Dvcell.secondarySimdatadir.internal=\\cfs05\vcell\users  
-Dvcell.server.dbPassword=  
-Dvcell.server.dbUserid=vcell  
-Dvcell.server.dbDriverName=oracle.jdbc.driver.OracleDriver  
-Dvcell.server.dbConnectURL=jdbc:oracle:thin:@vcell-db.cam.uchc.edu:1521:vcelldborcl  
-Dvcell.server.id=alpha\_7.0.0\_51  
-Dvcell.mongodb.database=TEST  
-Dvcell.mongodb.host.internal=vcellapi-beta.cam.uchc.edu  
-Dvcell.mongodb.port.internal=27019  
-Dvcell.installDir=C:\Users\frm\VCellTrunkGitWorkspace2\vcell  
-Dvcell.softwareVersion="frm\_VCell\_7.0"  
-Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.4-jar-with-dependencies.jar  
-Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.4-jar-with-dependencies.jar  
// required to run locally simulation with green button (dan)  
-Dvcell.server.maxJobsPerScan=1  
-Dvcell.limit.jobMemoryMB=100  

**Debug network container activity (host 'netstat' command may not show Docker container activity)**  
//Show network interfaces  
sudo tcpdump -D  
//Show docker networks  
sudo docker network ls  
//Show network activity on docker network  
sudo tcpdump -A -i docker_gwbridge  
//Show nodes in a docker network  
sudo docker network inspect 8faccc2c5056  
//Show info about container  
sudo docker container inspect dd29d35fba3e94fa476020f4f2c3de860f745fc3be32f76107ca368f4fffbfd5  


**Debug slurm jobs**  
--List slurm nodes, login vcell-service as vcell  
----abc=$(sinfo -N -h -p vcell2 --Format='nodelist' | xargs)  
--Get info for VCell simid, login vcell-service as vcell  
----sacct --user=vcell --format="JobID,JobName%30,State,Submit,start,end,ExitCode" -S 2018-09-01 | grep -i 139363583  

**command line interface in python ... (similar to old vcell console)**  
-----login build machine (vcell-node1), run /opt/build/vcell/docker/swarm/cli.py  

**Useful docker commands**  
sudo docker node ls ({vcellapi(rel), vcellapi-beta(alpha)}shows all nodes in swarm that host belongs to, must be on manager node)  
sudo docker stack ls (shows all running stacks in the swarm)  
sudo docker stack ps {stack} (shows 'task' info for particular stack)  
sudo docker service ls (shows all services and port mappings for the swarm)  
sudo docker system df (shows disk usage on host)  
sudo docker system prune (gets rid of unused/dangling components)  
sudo docker stats (must be run on each host of swarm, show cpu,mem,... for each container)  



