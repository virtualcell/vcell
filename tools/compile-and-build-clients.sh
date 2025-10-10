#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Get the current working directory
CURRENT_DIR="$(pwd)"

if [[ "$CURRENT_DIR" == "$SCRIPT_DIR" ]]; then
  pushd ../
fi

mvn clean install dependency:copy-dependencies -DskipTests=true

# Check the result of the compile command
if [[ $? -ne 0 ]]; then
  exit 1
fi

cp ./vcell-rest/target/generated/openapi.yaml ./tools/openapi.yaml
./tools/generate.sh
./tools/python-fix.sh

if [[ "$CURRENT_DIR" == "$SCRIPT_DIR" ]]; then
  popd || exit
fi

