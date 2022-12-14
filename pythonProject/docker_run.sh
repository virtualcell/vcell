#!/bin/bash

show_help() {
	echo "usage: docker run --rm -e v1=a -e v2=b -v /host/path/to/file:/simdata ghcr.io/virtualcell/vcell-opt:latest [OPTIONS] optProblem.json results.json"
	echo "  or   singularity run --bind /host/path/to/file:/simdata ghcr_io_virtualcell_vcell_opt_latest.img [OPTIONS] optProblem.json results.json"
	echo "  [OPTIONS]"
	echo "    -h | --help                  show this message"
	echo "    -e | --env var=value         add environment variable (needed for Singularity only)"
	echo "                                    this option can be repeated to define multiple variables"
	exit 1
}

if [ "$#" -lt 1 ]; then
    show_help
fi


while :; do
	case $1 in
		-h|--help)
			show_help
			exit
			;;
		-e|--env)
			shift
			export $1
			;;
		-?*)
			printf 'ERROR: Unknown option: %s\n' "$1" >&2
			echo ""
			show_help
			;;
		*)               # Default case: No more options, so break out of the loop.
			break
	esac
	shift
done

echo "$@"

if [ "$#" -lt 1 ]; then
    show_help
fi

#shopt -s -o nounset

arguments=$*
echo "script args = $arguments"
#arguments=${arguments//$datadir_external/$datadir_internal}
#arguments=${arguments//$htclogdir_external/$htclogdir_internal}

cd /usr/local/app/vcell/installDir/python/vcell_opt || exit 1
poetry run python -m vcell_opt.optService $arguments
exit $?