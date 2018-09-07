#!/usr/bin/env bash

shopt -s -o nounset

show_help() {
	echo "Command Line Interface to VCell SBML Solvers"
	echo ""
	echo "usage: cli.sh [OPTIONS] COMMAND REQUIRED-ARGUMENTS"
	echo ""
	echo "  COMMAND"
	echo "    submit                submit simulation job"
	echo "    meshinfo              retrieve mesh info (e.g. size)"
	echo "    simdata               retrieve simulation data"
	echo "    status                retrieve simulation status"
	echo "    timepoints            retrieve timepoints"
	echo "    varlist               retrieve varlist"
	echo "    tosbml                convert VCML file into SBML file"
	echo ""
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
	echo ""
	echo "    --simhandle handle    simulation identifier returned by submit and used by all other commands"
	echo ""
	echo "    --sbmlfile path       path to SBML file (required by submit and tosbml commands)"
	echo ""
	echo "    --simspec json        inlined JSON of simspec (required by submit command)"
	echo ""
	echo "    --vcmlfile path       path to VCML file (required by tosbml command)"
	echo ""
	echo "    --timeindex index     0-based index of time point (required by simdata command)"
	echo ""
	echo "    --varinfo json        inlined JSON of varinfo (required by simdata command)"
	echo ""
	exit 1
}

script=`basename "$0"`
invocation="$script $@"
echo "$invocation"

if [[ $# -lt 2 ]]; then
    show_help
fi

simhandle=
sbmlfile=
simspec=
vcmlfile=
simspec=
vcmlfile=
timeindex=
varinfo=
while :; do
	case $1 in
		-h|--help)
			show_help
			exit
			;;
		--simhandle)
			shift
			simhandle=$1
			;;
		--sbmlfile)
			shift
			sbmlfile=$1
			;;
		--simspec)
			shift
			simspec=$1
			;;
		--vcmlfile)
			vcmlfile=$1
			;;
		--timepoint)
			timepoint=$1
			;;
		--varinfo)
			varinfo=$1
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

if [[ $# -ne 1 ]] ; then
    show_help
fi

runjava()
	CLASSPATH=./target/maven-jars/*:../lib/*
	MAIN_CLASS=org.vcell.sbmlsim.VCellSbmlSimCLI

	props="-Dvcell.installDir=$PWD"
	# props="${props} -Dvcell.softwareVersion=standalone_VCell_7.0"
	# props="${props} -Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.5-jar-with-dependencies.jar"
	# props="${props} -Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.5-jar-with-dependencies.jar"

	echo java ${props} -cp $CLASSPATH $MAIN_CLASS $@
	java ${props} -cp $CLASSPATH $MAIN_CLASS $@
}

command=$1

case $command in
	submit)
		echo "command '$command' not yet implemented" >>/dev/stderr
		exit -1
		;;
	meshinfo)
		echo "command '$command' not yet implemented" >>/dev/stderr
		exit -1
		;;
	simdata)
		echo "command '$command' not yet implemented" >>/dev/stderr
		exit -1
		;;
	status)
		echo "command '$command' not yet implemented" >>/dev/stderr
		exit -1
		;;
	timepoints)
		echo "command '$command' not yet implemented" >>/dev/stderr
		exit -1
		;;
	varlist)
		echo "command '$command' not yet implemented" >>/dev/stderr
		exit -1
		;;
	tosbml)
		echo "command '$command' not yet implemented" >>/dev/stderr
		exit -1
		;;
	*)
		echo "unknown command '$command'" >>/dev/stderr
		exit -1
		;;
esac
