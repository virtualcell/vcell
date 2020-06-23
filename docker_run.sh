#!/bin/bash

java -classpath '/usr/local/app/vcell/lib/*' -Dvcell.installDir=/usr/local/app/vcell/installDir -Djava.library.path=/usr/local/app/vcell/installDir/libcombine_java/linux64 org.vcell.cli.CLIStandalone $1 $2 $3 $4 $5 $6
