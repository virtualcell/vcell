# VCell - a modeling and simulation framework for computational cell biology
The vcell documentation in Markdown format (*.md) is edited in eclipse using the WikiText plugin


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
**http://vcell.org** which hosts a free VCell Server for cluster computing and shared database.  
Prebuilt installers for vcell-client are available for Windows, Mac and Linux.  
**http://vcell.org/webstart** Location of VCell clients web page links to   
-----**Public installers** path naming scheme= {Alpha,Beta,Rel,Test,...}/VCell_{Alpha,Beta,Rel}_{macos,unix,windows-x64,windows}_latest_{64,32}bit.{dmg,sh.exe}  
-----**Install4J updates.xml** (read remotely by VCell client when starting to determine if a newer version of VCell has been deployed)  
-----**BioFormats jar** (used by running VCell clients when importing image data, can't be shiiped directly with VCell, license issue)

## Building VCell
This VCell github project includes all Java/Python source code required to build both the VCell client and the VCell Server.  
The simulation solver source code is available as a separate project (http://github.com/jcschaff/vcell-solvers).

### Building and Running VCell Client as a standalone tool
Requirements:  Git, Maven, and Java JDK 1.8 or later

```bash
# to build vcell client
git clone https://github.com/virtualcell/vcell
cd vcell
mvn clean install dependency:copy-dependencies
# to run vcell client quickly
./vcell.sh
```

## Building and Running VCell Client/Server
service has 1 image and configuration, manages 1 or more containers, container is a running image  
All services defined in ./swarm/docker-compose.yml (collection of services,volumes, dependencies,...)  

VCell Server Installation General Requirements
  * Linux
  * Git, Maven, and Java JDK 1.8 or later to build vcell-client and vcell-server
  * Docker (swarm mode)
  * Singularity (Linux) or Singularity in a Virtual Machine (Macos needs VirtualBox and Vagrant)
  * PostgreSQL or Oracle database
  * SLURM service for batch scheduling
  * Obtain an Install4J license if creating client installers





0b) Understanding VCell services [detailed instructions](docker/README_serviceInfo.md)  
1) NEW VCell Server Node configuration [detailed instructions](docker/swarm/README_DockerSwarmConfig.md)  
-----1a) Create new Docker swarm with node or Add node to existing Docker swarm [detailed instructions](docker/swarm/README_NodeAndSwarm.md)  
-----1b) Finish NEW VCell Server Node configuration [detailed instructions](docker/swarm/README_new_node_final_steps.md)  
2) Building VCell Client/Server Software [detailed instructions](docker/build/README.md)  
3) Deploying VCell Client/Server software [detailed instructions](docker/swarm/README.md)  
4) Server Administration [detailed instructions](docker/swarm/README_admin.md) (.e.g restarting VCell services)  
5) Debugging [detailed instructions](README_Debugging.md) (.e.g debug vcell client/server)  

serverconfig.sh file must be customized for your configuration (see vcell/docker/swarm/serverconfig-uch.sh).  



## License
Virtual Cell software is licensed under the MIT open source license.
