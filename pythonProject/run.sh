#!/usr/bin/env bash

if (( $# < 2 )); then
    echo "expecting at least two parameters (e.g. optProblem.json and results.json)"
    exit 2
fi

optFile=$1
resultsFile=$2

docker run -it --rm -v $PWD:/data/ vcell_opt /data/${optFile} /data/${resultsFile}
