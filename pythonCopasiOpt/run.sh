#!/usr/bin/env bash

if (( $# < 3 )); then
    echo "expecting at least three parameters (e.g. optProblem.json results.json report.txt)"
    exit 2
fi

optFile=$1
resultsFile=$2
reportFile=$3

docker run -it --rm -v $PWD:/data/ vcell_opt /data/${optFile} /data/${resultsFile} /data/${reportFile}
