![Build the Docker Image](https://github.com/reproducible-biomedical-modeling/Biosimulations_vcell/workflows/Build%20the%20Docker%20Image/badge.svg)  ![Publish Docker To Hub](https://github.com/reproducible-biomedical-modeling/Biosimulations_vcell/workflows/Publish%20Docker%20To%20Hub/badge.svg)   [![GitHub issues](https://img.shields.io/github/issues/reproducible-biomedical-modeling/Biosimulations_vcell?logo=GitHub)](https://github.com/reproducible-biomedical-modeling/Biosimulations_vcell/issues)

---
# Biosimulations_vcell
BioSimulations-compliant command-line interface to the [VCELL](http://vcell.org/) simulation program.

## Contents
* [Installation](#installation)
    * [Project setup](#to-setup-the-project-in-intellij-idea)
    * [Maven installation](#maven-installation-package)
* [Usage](#local-usage)
* [License](#license)
* [Development team](#development-team)
* [Questions and comments](#questions-and-comments)

## Installation

### To setup the project in IntelliJ IDEA

1. Requirements: Git, Maven, Jetbrains IntelliJ IDEA and Oracle Java JDK 1.8
2. Clone the repo
3. Open the project as new project in IntelliJ
4. Go to `files` > `project settings` > `Modules` and select all modules and apply
5. :heavy_exclamation_mark: Important! Unselect `ojdbc6` and `ucp` modules after building
6. Navigate to project directory from terminal and execute this:
	`mvn clean install dependency:copy-dependencies`
7. For creating Run/Debug Configurations:
	* Go to `Run/Debug Configurations` > `+` > select `Application`
	* Name it `VCellCLIStandalone`
	* Now setup the configuration
		* Add `org.vcell.cli.CLIStandalone` for `Main class:`
		* For `VM options:` 
			```
			-Dvcell.imagej.plugin.url=http://vcell.org/webstart/vcell-imagej-helper-1.jar
			-Dvcell.installDir=<Your-Project-Directory-Path>
			-Dvcell.softwareVersion="frm_VCell_7.2"
			-Dvcell.serverHost=vcellapi-beta.cam.uchc.edu:8080
			-Dvcell.onlineResourcesURL=http://vcell.org
			-Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.8-jar-with-dependencies.jar
			-Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.8-jar-with-dependencies.jar
			-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
			-Djava.library.path=libcombine_java/osx
			```
		* eg for <Your-Project-Directory-Path> `/Users/akhil/projects/Biosimulations_vcell`
		* Add `-h` for `Program arguments:`
		* `Working Directory:` is `<Your-Project-Directory-Path>`
		* Select `vcell-cli` as `Use classpath of module:`
8. Click `apply` and `build`

### Maven installation package

` `



### Install Docker image
```
docker pull crbm/biosimulations_vcell
```

## Local usage
```
usage: vcell [-h] [-d] [-q] -i ARCHIVE [-o OUT_DIR] [-v]

BioSimulations-compliant command-line interface to the VCELL simulation program <http://vcell.org>.

optional arguments:
  -h, --help            show this help message and exit
  -d, --debug           full application debug mode
  -q, --quiet           suppress all console output
  -i ARCHIVE, --archive ARCHIVE
                        Path to OMEX file which contains one or more SED-ML-
                        encoded simulation experiments
  -o OUT_DIR, --out-dir OUT_DIR
                        Directory to save outputs
  -v, --version         show program's version number and exit
```

## Usage through Docker container
```
docker run \
  --tty \
  --rm \
  --mount type=bind,source="$(pwd)"/tests/fixtures,target=/root/in,readonly \
  --mount type=bind,source="$(pwd)"/tests/results,target=/root/out \
  crbm/biosimulations_vcell:latest \
    -i /root/in/BIOMD0000000297.omex \
    -o /root/out
```

## License
This package is released under the [MIT license](LICENSE).

## Development team
This package was developed by the [Center for Reproducible Biomedical Modeling](http://reproduciblebiomodels.org).

## Questions and comments
Please contact the [Center for Reproducible Biomedical Modeling](mailto:info@reproduciblebiomodels.org) with any questions or comments.
 