![Build the Docker Image](https://github.com/biosimulators/Biosimulators_vcell/workflows/Build%20the%20Docker%20Image/badge.svg)  
![Publish Docker To Hub](https://github.com/biosimulators/Biosimulators_vcell/workflows/Publish%20Docker%20To%20Hub/badge.svg)   
[![GitHub issues](https://img.shields.io/github/issues/biosimulators/Biosimulators_vcell?logo=GitHub)](https://github.com/biosimulators/Biosimulators_vcell/issues)

---
# Biosimulators_vcell
BioSimulators-compliant command-line interface to the [VCell](http://vcell.org/) simulation program.

## Contents
* [Installation](#installation)
    * [Project setup IntelliJ IDEA](#to-setup-the-project-in-intellij-idea)
    * [Project setup Eclipse](#to-setup-the-project-in-eclipse)
    * [Maven installation](#maven-installation-package)
* [Usage](#local-usage)
* [License](#license)
* [Development team](#development-team)
* [Questions and comments](#questions-and-comments)

## Installation

### To set up the project in IntelliJ IDEA

1. Requirements: Git, Maven, Jetbrains IntelliJ IDEA and Oracle Java JDK 1.8
2. Clone the repo
3. Open the project as new project in IntelliJ
4. Go to `Files` > `Project Structure...` > `Modules` and select all modules and then click apply.
5. Run `mvn clean install dependency:copy-dependencies`
6. For creating Run/Debug Configurations:
	* Go to `Run/Debug Configurations` > `+` > select `Application`
	* Name it `VCell-CLI`
	* Now setup the configuration
		* Add `org.vcell.cli.CLIStandalone` for `Main class:`
		* For `VM options:` 
			```
			-Dvcell.installDir=<Your-Project-Directory-Path>
			-Dvcell.softwareVersion="frm_VCell_7.2"
			```
		* eg for `<Your-Project-Directory-Path>`: `/Users/akhil/projects/Biosimulators_vcell`
		* Add `-h` for `Program arguments:` (refer [Local Usage](#local-usage) for more options)
		* `Working Directory:` is `<Your-Project-Directory-Path>`
		* Select `vcell-cli` as `Use classpath of module:`
8. Click `apply` and `build`

### To setup the project in eclipse
  * Requirements:  
        Git, Maven, Eclipse IDE and Java JDK 1.8
  1. Open terminal, navigate to the Eclipse workspace folder.
  2. Clone the repo
  3. ``` mvn clean install dependency:copy-dependencies ```
  4. Open Eclipse, Import the project using Maven. Depending on the Eclipse version there'll be small differences with the importing steps.
  5. Create a `Debug configuration` as a `Java Application`.
     * the Main Class is `org.vcell.cli.CLIStandalone`
     * Leave the 'Program Arguments' blank.
     * The VM needed arguments are:
         ```
        -Dvcell.installDir=<Your-Project-Directory-Path>
        -Dvcell.softwareVersion="frm_VCell_7.2"
        ```
           
           
### Maven installation package

` `



### Install Docker image
```
docker pull ghcr.io/biosimulators/vcell
```

## Local usage
```
usage: vcell [-h] [-d] [-q] -i ARCHIVE [-o OUT_DIR] [-v]

BioSimulators-compliant command-line interface to the VCELL simulation program <http://vcell.org>.

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
  ghcr.io/biosimulators/vcell:latest \
    -i /root/in/BIOMD0000000297.omex \
    -o /root/out
```

## License
This package is released under the [MIT license](LICENSE).

## Development team
This package was developed by the BioSimulators Team of the [Center for Reproducible Biomedical Modeling](https://reproduciblebiomodels.org).

## Questions and comments
Please contact the [BioSimulators Team](mailto:info@biosimulators.org) with any questions or comments.
 
