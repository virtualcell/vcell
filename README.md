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
2) a shared server providing a centralized model database, cluster computing and shared storage (vcell-server)
3) simulation solvers written in C++/Fortran/Python and developed as part of the project as well as third party solvers and analysis. 

## Download VCell
Prebuilt installers for vcell-client are available for Windows, Mac and Linux at http://vcell.org which hosts a free VCell Server 
for cluster computing and shared database.

## Building VCell
This VCell github project includes all Java/Python source code required to build both the VCell client and the VCell Server.  
The simulation solver source code is available as a separate project, but the executables currently bundled within this repository for 
your convenience, but are separately available in the vcell-solvers project (http://github.com/jcschaff/vcell-solvers).

### Building VCell Client as a standalone tool
Requirements:  Git, Maven, and Java JDK 1.8 or later

```bash
git clone https://github.com/virtualcell/vcell
cd vcell
mvn clean verify
```

#### scripts to run the standalone client will be available soon. ####

### Building VCell Client/Server
Requirements:
  * Git, Maven, and Java JDK 1.8 or later to build vcell-client and vcell-server
  * PostgreSQL or Oracle database
  * ActiveMQ messaging service
  * MongoDB nosql database (for distributed logging)
  * SLURM service for batch scheduling
  * Install4J, VirtualBox, Vagrant to build platform installers for Windows, Mac, Linux.
  ** Install VirtualBox for your platform
  ** Install Vagrant for your platform (using the VirtualBox provider.
  ** Obtain an Install4J license if creating client installers

```bash
$ git clone https://github.com/jcschaff/vcell
$ cd vcell
$ cd deploy
$ ./deploy.sh server-config.include
```

server-config.include file must be customized for your configuration (see examples in /deploy directory).  

**a Vagrant box virtual machine will act as a reference implementation for a VCell Server**

## License
Virtual Cell software is licensed under the MIT open source license.
