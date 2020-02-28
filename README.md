![Logo](https://raw.githubusercontent.com/reproducible-biomedical-modeling/CRBM-Viz/master/CRBM-Viz/src/assets/logo/logo-white.svg?sanitize=true)
# Biosimulations VCell Simulator
![Build the Docker Image](https://github.com/reproducible-biomedical-modeling/Biosimulations_vcell/workflows/Build%20the%20Docker%20Image/badge.svg)  ![Publish Docker To Hub](https://github.com/reproducible-biomedical-modeling/Biosimulations_vcell/workflows/Publish%20Docker%20To%20Hub/badge.svg)   [![GitHub issues](https://img.shields.io/github/issues/reproducible-biomedical-modeling/Biosimulations_vcell?logo=GitHub)](https://github.com/reproducible-biomedical-modeling/Biosimulations_vcell/issues)

---
## The VCell Simulator for [BioSimulations](https://biosimulations.io)

This repo builds a docker image of the VCell Simulator for BioSimulations web application using SEDML Solver as an entrypoint.
The docker image is eventually converted to Singularity image in CRBM service user at HPC which further is used to perform actual simulation. The image is created automatically on  **crbmapi.cam.uchc.edu** server via service root user using cron jobs (cronjob scheduled with the latest docker hub image tag)


### :whale: To push latest image to docker hub
Create a new release tag version that pushes latest and version images to docker hub

### To check it manually with docker image locally:
1. ```docker pull crbm/biosimulations_vcell_api:latest```
2. use env.sample as `.env` file with all variables filled
3. run this command  ```docker run -v <LOCAL_DIR_WITH_SEDML AND SBML>:/usr/local/app/vcell/simulation --env-file <PATH_OF_ENV_FILE> crbm/biosimulations_vcell_api:latest```


### For Building and compiling VCELL Standalone application via SEDML Solver
#### Steps to build the project
   * Requirements:  
        Git, Maven, Eclipse IDE and Java JDK 1.8
   * Clone the repo
        ```
        cd Biosimulations_vcell
        mvn clean install dependency:copy-dependencies
        mvn install:install-file -Dfile=./src/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar
        mvn install:install-file -Dfile=./src/ucp.jar -DgroupId=com.oracle -DartifactId=ucp -Dversion=11.2.0.4 -Dpackaging=jar
        ```
#### Steps to setup project in Eclipse
  * Requirements:  
        Git, Maven, Eclipse IDE and Java JDK 1.8
  * Open terminal, navigate to the Eclipse workspace folder.
  * Clone the repo
  * ``` mvn clean install dependency:copy-dependencies ```
  * Open Eclipse, Import the project using Maven. Depending on the Eclipse version there'll be small differences with the importing steps.
  * :heavy_exclamation_mark: Important! Deselect the ojdbc6 and ucp subprojects, then Finish to start importing.
  * Once importing is finished (it takes a while) there will be errors.
  * Open terminal again, navigate to the `ojdbc6` folder and execute: 
  `mvn install:install-file -Dfile=./src/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar` 
  * Go to `ucp` direcotry and execute: 
  `mvn install:install-file -Dfile=./src/ucp.jar -DgroupId=com.oracle -DartifactId=ucp -Dversion=11.2.0.4 -Dpackaging=jar`
  * Rebuild the project in Eclipse, there should be no more errors.
  * Create a Debug configuration as a Java Application.
     * the Main Class is `org.vcell.sbml.test.VCellSedMLSolver.java`
     * Leave the 'Program Arguments' blank.
     * The VM needed arguments are:
         * the installation directory: -Dvcell.installDir=<CLONED_REPO_PATH>
         * the software version: -Dvcell.softwareVersion=...
         
           for example:
           -Dvcell.installDir=/usr/projects/git/Biosimulations_vcell/
           -Dvcell.softwareVersion=DanDev_Version_7.0_build_99
