#!/usr/bin/env bash

show_help() {
	echo "*************************************************"
	echo "** Virtual Cell command line tools and solvers **"
	echo "*************************************************"
	echo ""
	echo "usage: docker run --rm -v /host/path/to/file:/simdata schaff/vcell-batch:latest [OPTIONS] command <args>"
	echo "  or   singularity run --bind /host/path/to/file:/simdata docker://schaff/vcell-batch:latest [OPTIONS] command <args>"
	echo ""
	echo "  Script Commands"
	echo "    JavaPreprocessor64"
	echo "    JavaPostprocessor64"
	echo "    JavaSimExe64"
	echo ""
	echo "  Solver Commands - see https://github.com/virtualcell or http://vcell.org"
	echo "    FiniteVolume_x64"
	echo "    MovingBoundary_x64"
	echo "    NFSim_x64"
	echo "    smoldyn_x64"
	echo "    SundialsSolverStandalone_x64"
	echo "    VCellStoch_x64"
	echo ""
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
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

if [ "$#" -lt 1 ]; then
    show_help
fi

export PATH="/vcellbin:/vcellscripts:${PATH}"
export LD_LIBRARY_PATH="/vcellbin:${LD_LIBRARY_PATH}"

shopt -s -o nounset

command=$1
shift

arguments=$*

arguments=${arguments//$datadir_external/$datadir_internal}
arguments=${arguments//$htclogdir_external/$htclogdir_internal}

case $command in
	FiniteVolume_x64)
		/vcellbin/FiniteVolume_x64 $arguments
		exit $?
		;;
	MovingBoundary_x64)
		/vcellbin/MovingBoundary_x64 $arguments
		exit $?
		;;
	NFSim_x64)
		/vcellbin/NFSim_x64 $arguments
		exit $?
		;;
	smoldyn_x64)
		/vcellbin/smoldyn_x64 $arguments
		exit $?
		;;
	SundialsSolverStandalone_x64)
		/vcellbin/SundialsSolverStandalone_x64 $arguments
		exit $?
		;;
	VCellStoch_x64)
		/vcellbin/VCellStoch_x64 $arguments
		exit $?
		;;
	JavaPreprocessor64)
		/vcellscripts/JavaPreprocessor64 $arguments
		exit $?
		;;
	JavaPostprocessor64)
		/vcellscripts/JavaPostprocessor64 $arguments
		exit $?
		;;
	JavaSimExe64)
		/vcellscripts/JavaSimExe64 $arguments
		exit $?
		;;
	*)
		printf 'ERROR: Unknown command: %s\n' "$command" >&2
		echo ""
		show_help
		;;

esac
