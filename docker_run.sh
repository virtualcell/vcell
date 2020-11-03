#!/bin/bash

java -classpath '/usr/local/app/vcell/lib/*' -Dvcell.installDir=/usr/local/app/vcell/installDir org.vcell.cli.CLIStandalone $1 $2 $3 $4 $5 $6
