
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

## Real location of required properties

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

