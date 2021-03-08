#!/bin/bash

show_help() {
	echo "usage: VCell [-h] [-q] -i ARCHIVE [-o OUT_DIR] [-v]"
	echo "  OPTIONS and ARGUMENTS:"
	echo "    -h,--help             show this help message and exit"
	echo ""
	echo "    -i,--archive <arg>    Path to OMEX/COMBINE Archive file which contains one or more SED-ML encoded simulation experiments"
	echo ""
	echo "    -o,--out-dir <arg>    Directory to save outputs"
	echo ""
	echo "    -q,--quiet            Suppress all console output"
	echo ""
	echo "    -v,--version          Shows program's version number and exit"
	echo ""
	exit 1
}

if [[ $# -lt 1 ]]; then
    show_help
fi

java \
  -classpath '/usr/local/app/vcell/lib/*' \
  -Dvcell.installDir=/usr/local/app/vcell/installDir \
  -Dvcell.server.id="7.3.0.16" \
  -Dvcell.python.executable=/usr/bin/python3 \
  -Dvcell.mongodb.database="localhost" \
  -Dvcell.mongodb.host.internal="localhost" \
  -Dvcell.mongodb.port.internal=27017 \
  org.vcell.cli.CLIStandalone "$1" "$2" "$3" "$4" "$5" "$6"
