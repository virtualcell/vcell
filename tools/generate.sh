#!/usr/bin/env bash

generatorCliImage=openapitools/openapi-generator-cli:v7.1.0

scriptDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parentDir="$(dirname "$scriptDir")"


docker run --rm -v ${scriptDir}:/local ${generatorCliImage} validate -i /local/openapi.yaml --recommend
if [ $? -ne 0 ]; then
    echo "openapi.yaml is not valid"
    exit 1
fi

#docker run --rm -v ${scriptDir}:/local ${generatorCliImage} config-help -g java

docker run --rm -v ${parentDir}:/vcell \
    ${generatorCliImage} \
    generate \
    -g java \
    -i /vcell/tools/openapi.yaml \
    -o /vcell/vcell-restclient \
    -c /vcell/tools/java-config.yaml

#docker run --rm -v ${parentDir}:/vcell ${generatorCliImage} generate \
#    -i /vcell/tools/openapi.yaml \
#    -g python \
#    -o /vcell/python-restclient
#