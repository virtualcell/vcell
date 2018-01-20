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

command=$1
shift

export PATH="/vcellbin:/vcellscripts:${PATH}"
export LD_LIBRARY_PATH="/vcellbin:${LD_LIBRARY_PATH}"

case $command in
	FiniteVolume_x64)
		/vcellbin/FiniteVolume_x64 "$@"
		exit $?
		;;
	MovingBoundary_x64)
		/vcellbin/MovingBoundary_x64 "$@"
		exit $?
		;;
	NFSim_x64)
		/vcellbin/NFSim_x64 "$@"
		exit $?
		;;
	smoldyn_x64)
		/vcellbin/smoldyn_x64 "$@"
		exit $?
		;;
	SundialsSolverStandalone_x64)
		/vcellbin/SundialsSolverStandalone_x64 "$@"
		exit $?
		;;
	VCellStoch_x64)
		/vcellbin/VCellStoch_x64 "$@"
		exit $?
		;;
	JavaPreprocessor64)
		/vcellscripts/JavaPreprocessor64 "$@"
		exit $?
		;;
	JavaPostprocessor64)
		/vcellscripts/JavaPostpocessor64 "$@"
		exit $?
		;;
	*)
		printf 'ERROR: Unknown command: %s\n' "$command" >&2
		echo ""
		show_help
		;;

esac
