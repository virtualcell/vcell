#!/bin/bash

echo -n "<"
echo -n $1
echo ">"

rawCommand="$(echo -n "$1" | sed -E 's/(^(\s*))|((\s*)$)//g')" # Strip whitespace
command="biosimulations" # default

echo -n "<"
echo -n $rawCommand
echo ">"

case rawCommand in
  convert)
    echo 'convert mode requested'
    command="convert"
    shift
    ;;
  export-omex)
    echo 'export-omex mode requested'
    command="export-omex"
    shift
    ;;
  export-omex-batch)
    echo 'export-omex-batch mode requested'
    command="export-omex-batch"
    shift
    ;;
  model)
    echo 'model mode requested'
    command="model"
    shift
    ;;
  execute)
    echo 'execute mode requested'
    command="execute"
    shift
    ;;
  version)
  echo 'version mode requested'
    command="version"
    shift
    ;;
  biosimulations)
  echo 'biosimulations mode requested'
    command="biosimulations"
    shift
    ;;
  help)
    command="help"
    shift
    ;;
  *)               # Default case: No more options, so break out of the loop.
    echo "Default case selected"
    ;;
esac

echo "VCell shall execute <$command" "$@>"

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
  -Dvcell.server.dbUserid=vcell \
  -Dvcell.server.dbPassword=cbittech \
  -Dvcell.server.dbDriverName=oracle.jdbc.driver.OracleDriver \
  -Dvcell.server.dbConnectURL=jdbc:oracle:thin:@VCELL-DB.cam.uchc.edu:1521/vcelldborcl.cam.uchc.edu \
  -Dcli.workingDir=/usr/local/app/vcell/installDir/python/vcell_cli_utils/ \
  org.vcell.cli.CLIStandalone "$command" "$@"
