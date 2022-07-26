#!/bin/bash

command="biosimulations"
case $1 in
  convert)
    command="convert"
    shift
    ;;
  execute)
    command="execute"
    shift
    ;;
  version)
    command="version"
    shift
    ;;
  biosimulations)
    command="biosimulations"
    shift
    ;;
  help)
    command="help"
    shift
    ;;
  *)               # Default case: No more options, so break out of the loop.
    ;;
esac

#echo "$command" "$@"

java \
  -classpath '/usr/local/app/vcell/lib/*' \
  -Dlog4j.configurationFile=/usr/local/app/vcell/installDir/biosimulations_log4j2.xml \
  -Dvcell.softwareVersion=$ENV_SIMULATOR_VERSION \
  -Dvcell.installDir=/usr/local/app/vcell/installDir \
  -Dvcell.server.id="7.3.0.16" \
  -Dvcell.cli="true" \
  -Dvcell.python.executable=/usr/bin/python3 \
  -Dvcell.mongodb.database="localhost" \
  -Dvcell.mongodb.host.internal="localhost" \
  -Dvcell.mongodb.port.internal=27017 \
  -Dcli.workingDir=/usr/local/app/vcell/installDir/python/vcell_cli_utils/ \
  org.vcell.cli.CLIStandalone "$command" "$@"
