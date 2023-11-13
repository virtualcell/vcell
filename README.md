<p align="center" width="100%">
 <a href="https://vcell.org">
    <img width="10%" src="https://github.com/biosimulations/biosimulations/blob/dev/docs/src/assets/images/about/partners/vcell.svg">
 </a>
</p>

---
![CI](https://github.com/virtualcell/vcell/actions/workflows/ci_cd.yml/badge.svg)
# Virtual Cell
> ### VCell - a modeling and simulation framework for computational cell biology

The VCell is a comprehensive framework for modeling and simulating cell biology from biological pathways down to cell biophysics. VCell supports the biochemical network and rule-based modeling and electrophysiology in compartmental modeling and within cellular geometry.  

Simulation capabilities include ODEs, Reaction-Diffusion equations within cellular geometry, Gillespie and Hybrid stochastic solvers, Particle-based spatial simulations, network free simulations, and cell kinematics (moving boundary problems).  

## Contents
* [VCell software](#vcell-software-consists-of)
* [Download VCell](#download-vcell)
* [Building VCell](#building-vcell)
* [Building and Running VCell Client as a standalone tool](#building-and-running-vcell-client-as-a-standalone-tool)
* [Eclipse Setup for VCell Client on Windows/Mac](#vcell-client-eclipse-setup-for-windows-and-mac)
* [Building and Running VCell Server](#building-and-running-vcell-server)
* [CLI Requirements](#vcell-cli-requirements)
* [Other Details](#other-details)
* [License](#license)

### VCell software consists of
1) a standalone modeling and simulation tool (vcell-client)
2) a shared server providing a centralized model database, cluster computing and shared storage (vcell-server, vcell-api)
3) simulation solvers written in C++/Fortran/Python and developed as part of the project as well as third party solvers and analysis. 

### Download VCell
- [VCell](http://vcell.org) which hosts a free VCell Server for cluster computing and shared database.  
- Prebuilt installers for vcell-client are available for [Windows](https://vcell.org/webstart/Rel/VCell_Rel_windows-x64_latest_64bit.exe), [Mac](https://vcell.org/webstart/Rel/VCell_Rel_macos_latest_64bit.dmg) and [Linux](https://vcell.org/webstart/Rel/VCell_Rel_unix_latest_64bit.sh).  
- BioFormats jar (used by running VCell clients when importing image data, can't be shipped directly with VCell, license issue)

```
git clone https://github.com/virtualcell/vcell
cd vcell
```

### Build/test/run VCell on command line
- This VCell github project includes all Java/Python source code required to build both the VCell client and the VCell Server.  
- The simulation solver source code is available as a separate project as [vcell-solvers](https://github.com/virtualcell/vcell-solvers).
- Requirements:  Git, Maven, Poetry, Python 3.10, Java 17

#### Build Java and Python
```bash
mvn clean install -DskipTests

INSTALL_DIR=$(pwd)
cd ${INSTALL_DIR}/pythonCopasiOpt/vcell-opt
poetry env use 3.10
poetry install

cd ${INSTALL_DIR}/docker/swarm/vcell-admin
poetry env use 3.10
poetry install

cd ${INSTALL_DIR}/pythonVtk
poetry env use 3.10
poetry install

cd ${INSTALL_DIR}/vcell-cli-utils
poetry env use 3.10
poetry install
```

#### Test Java and Python
```bash
mvn test -Dgroups=org.vcell.test.Fast

INSTALL_DIR=$(pwd)
cd ${INSTALL_DIR}/pythonCopasiOpt/vcell-opt
poetry run python -m pytest

cd ${INSTALL_DIR}/docker/swarm/vcell-admin
poetry run python -m pytest

cd ${INSTALL_DIR}/pythonVtk
poetry run python -m pytest

cd ${INSTALL_DIR}/vcell-cli-utils
poetry run python -m pytest

cd ${INSTALL_DIR}
```

#### Run the VCell client (connects to the VCell servers)
```bash
./vcell.sh
```

### VCell client Eclipse setup for Windows and Mac command line instructions first)
Requirements:  Eclipse IDE for Java Developers and Java 17, Python 3.10 and poetry

  * Follow instructions above for building python packages with poetry
  * Start Eclipse
  * In Eclipse: Project Explorer -> Import -> Git -> Projects from Git (with smart import) -> Next -> Clone URI -> Paste under URI `https://github.com/virtualcell/vcell.git` -> Next -> Deselect all, Select `master` -> Next -> Next.
      * Alternative way of installing: If git is installed, you can install VCell client from a local repository:
          * Open CommandPrompt, navigate to the Eclipse workspace folder.
          * Clone the VCell client using git: `git clone https://github.com/virtualcell/vcell`
          * During Eclipse setup, use an option -> Existing local repository, follow all next steps
  * Make sure the project comes in as a Maven project (letter `M` on the top of the project, not `J`), otherwise you need to add it to Maven
  * Build the project in Eclipse (should start automatically, may come with several errors due to different build order).
  * Errors in individual projects can be fixed by Maven -> Update Project for individual projects that display errors. 
  * Create a Debug configuration as a Java Application.
     * Name: VCellClient
     * Main: 
         * Project: `vcell-client`
         * Main Class: `cbit.vcell.client.VCellClientMain`
     * Arguments:
         * Program arguments: `vcellapi-beta.cam.uchc.edu:8080`
         * VM arguments are:
             * the installation directory: `-Dvcell.installDir=<your install dir>`
             * the software version: `-Dvcell.softwareVersion=...`
             * (optional) the ImageJ plugin: `-Dvcell.imagej.plugin.url=http://vcell.org/webstart/`
             * for example:
           
               WINDOWS
               ```
               -Dvcell.installDir=G:\dan\jprojects\git\vcell 
               -Dvcell.imagej.plugin.url=http://vcell.org/webstart/
               -Dvcell.softwareVersion=DanDev_Version_7.0_build_99
               ```
               MAC:
               ```
               -Dvcell.installDir=/Users/mike/eclipse-workspace2/vcell
               -Dvcell.imagej.plugin.url=http://vcell.org/webstart/
               -Dvcell.softwareVersion=MikeDev_Version_7.0_build_99           
               ```
      * Make sure JRE is 17 or newer (build-in Eclipse JRE may case problems)

### Building and Running VCell Server
1. Service has 1 image and configuration, manages 1 or more containers, container is a running image  
2. All services defined in ./swarm/docker-compose.yml (collection of services,volumes, dependencies,...)  

VCell Server Installation General Requirements
  * Linux
  * Git, Maven, and Java JDK 17 or later to build vcell-client and vcell-server
  * Docker (swarm mode)
  * Singularity (Linux) or Singularity in a Virtual Machine (Macos needs VirtualBox and Vagrant)
  * PostgreSQL or Oracle database
  * SLURM service for batch scheduling
  * Obtain an Install4J license if creating client installers
  
### VCell CLI Requirements
1. VCell-CLI module requires python 3.10 and poetry


### Other Details
1. Creating certs [details](docs-misc/README_certs.md)  
    1.1. (TLS/SSL website for (vcellapi.cam.uchc.edu,vcell-api.cam.uchc.edu)  
    1.2. Microsoft authenticode codesigning (to prevent install4j from being rejected on windows clients)  
    1.3. Apple codesigning for macos (prevents from mac refusing install4j install))  
2. Understanding VCell [services detailed instructions](docker/README_serviceInfo.md)   
3. NEW VCell Server Node [configuration detailed instructions](docker/swarm/README_DockerSwarmConfig.md)  
    3.1. Create new Docker swarm with node or Add node to existing  [Docker swarm detailed instructions](docker/swarm/README_NodeAndSwarm.md)  
    3.2. Finish NEW VCell Server Node [configuration detailed instructions](docker/swarm/README_new_node_final_steps.md)  
4. Building VCell Client/Server [Software detailed instructions](docker/build/README.md)  
    4.1.  Github/Travis/Appveyor/Dockerhub [Instructions](README_git_trav_appv_dhub.md)  
5. Deploying VCell Client/Server software [detailed instructions](docker/swarm/README.md)  
    5.1. VCell System [Flow-Control](docs-misc/README_flow_control.md)  
6. Server Administration [detailed instructions](docker/swarm/README_admin.md) (.e.g restarting VCell services)  
7.  [Debugging detailed instructions](docs-misc/README_Debugging.md) (.e.g debug vcell client/server)  
8. serverconfig.sh file must be customized for your configuration (see vcell/docker/swarm/serverconfig-uch.sh).  


### License
Virtual Cell software is licensed under the MIT open source license.
