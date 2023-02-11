#!/usr/bin/env bash

show_help() {
	echo "VCell admin CLI"
	echo "type help for usage"
	exit 1
}

if [ "$#" -lt 1 ]; then
    show_help
fi

shopt -s -o nounset

arguments=$*

# to debug, EXPOSE port 8000 (in Dockerfile) and include the following JVM arguments
# -Xdebug
# -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n

java \
  -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -Xms64M \
	-Dvcell.softwareVersion="$VCELL_VERSION" \
	-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
	-Dlog4j.configurationFile=/usr/local/app/vcell-admin.log4j.xml \
	-Dvcell.server.id="$VCELL_SITE" \
	-Dvcell.python.executable=/usr/bin/python \
	-Dvcell.installDir=/usr/local/app \
	-Dvcell.primarySimdatadir.internal=/simdata \
	-Dvcell.secondarySimdatadir.internal=/simdata_secondary \
	-Dvcell.server.dbConnectURL="$VCELL_DB_URL" \
	-Dvcell.server.dbDriverName="$VCELL_DB_DRIVER" \
	-Dvcell.server.dbUserid="$VCELL_DB_USER" \
	-Dvcell.db.pswdfile=/run/secrets/dbpswd.txt \
	-cp	'./lib/*' \
	org.vcell.admin.cli.AdminCLI $arguments

exit $?
