# vcell - a modeling and simulation framework for computational cell biology

[![Join the chat at https://gitter.im/virtualcell/vcell](https://badges.gitter.im/virtualcell/vcell.svg)](https://gitter.im/virtualcell/vcell?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

The VCell is a comprehensive framework for modeling and simulation of cell biology from biological pathways down to 
cell biophysics. VCell supports biochemical network and rule-based modeling and electrophysiology in compartmental 
modeing and within cellular geometry.  

Simulation capabilities include ODEs, Reaction-Diffusion equations within 
cellular geometry, Gillespie and Hybrid stochastic solvers, Particle based spatial simulations, network free simulations
and cell kinetimatics (moving boundary problems).  

The VCell software consists of:
1) a standalone modeling and simulation tool (vcell-client)
2) a shared server providing a centralized model database, cluster computing and shared storage (vcell-server, vcell-api)
3) simulation solvers written in C++/Fortran/Python and developed as part of the project as well as third party solvers and analysis. 

## Download VCell
Prebuilt installers for vcell-client are available for Windows, Mac and Linux at http://vcell.org which hosts a free VCell Server 
for cluster computing and shared database.

## Building VCell
This VCell github project includes all Java/Python source code required to build both the VCell client and the VCell Server.  
The simulation solver source code is available as a separate project (http://github.com/jcschaff/vcell-solvers).

### Building VCell Client as a standalone tool
Requirements:  Git, Maven, and Java JDK 1.8 or later

```bash
git clone https://github.com/virtualcell/vcell
cd vcell
mvn clean install dependency:copy-dependencies
./vcell.sh
```

#### scripts to run the standalone client will be available soon. ####

### Building VCell Client/Server
Requirements:
  * Linux or Macos
  * Git, Maven, and Java JDK 1.8 or later to build vcell-client and vcell-server
  * Docker (swarm mode)
  * Singularity (Linux) or Singularity in a Virtual Machine (Macos needs VirtualBox and Vagrant)
  * PostgreSQL or Oracle database
  * SLURM service for batch scheduling
  * Obtain an Install4J license if creating client installers

```bash
$ git clone https://github.com/jcschaff/vcell
$ cd vcell/docker
$ ./build.sh <args>
$ ./serverconfig.sh <args>
$ ./deploy.sh <args>
# see README in vcell/docker
```
serverconfig.sh file must be customized for your configuration (see vcell/docker/serverconfig-uch.sh).  

## License
Virtual Cell software is licensed under the MIT open source license.
