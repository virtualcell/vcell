#!/usr/bin/env bash

DIR=${0%/*}
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi
cd $DIR

command="singularity $@"
echo "running '$command' in singularity vagrant box"
vagrant ssh -c "$command"
retcode=$?
if [[ $retcode -ne 0 ]]; then
	echo "command failed"
	exit $retcode
else
	echo "command worked"
	exit 0
fi
