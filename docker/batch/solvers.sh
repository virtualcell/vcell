#!/usr/bin/env bash

EXISTING_LD_LIBRARY_PATH=$LD_LIBRARY_PATH

shopt -s -o nounset

echo "there are $# arguments"

show_help() {
	echo "usage: deploy.sh [OPTIONS] vcellusername vcelluserkey simtaskfile simkey jobindex taskid solver solverinput submitfile"
	echo "  ARGUMENTS"
	echo "    vcellusername         VCell User Name"
	echo "    vcelluserkey          VCell User Key"
	echo "    simtaskfile           VCell SimulationTask XML file"
	echo "    simkey                VCell Simulation key"
	echo "    jobindex              VCell Simulation jobindex (0-based index into parameter scan)"
	echo "    taskid                VCell Simulation taskid - 0, 16, 32, ... tracks execution requests number"
	echo "    solver                VCell solver executable name (without path)"
	echo "                             MovingBoundary_x64"
	echo "    solverinput           VCell solver input file name (without path)"
	echo "    submitfile            <SLURM> submit file (optionally holds post-processing instructions)"
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
	exit 1
}

if [ "$#" -lt 9 ]; then
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

if [ "$#" -ne 9 ]; then
    show_help
fi

vcellusername=$1
vcelluserkey=$2
simtaskfile=$3
simkey=$4
jobindex=$5
taskid=$6
solver=$7
solverinput=$8
submitfile=$9


basedatadir="${solverprimarydata}"
datadir="${basedatadir}${vcellusername}"
htclogsdir="/htclogs"

case $solver in
	MovingBoundary_x64)
		solver_args="--config ${datadir}/${solverinput} -tid ${taskid}" 
		;;
	-?*)
		printf 'ERROR: Unknown solver: %s\n' "$solver" >&2
		echo ""
		show_help
		;;
	*)               # Default case: No more options, so break out of the loop.
esac



preprocessor_mainclass=cbit.vcell.message.server.sim.SolverPreprocessor
postprocessor_mainclass=cbit.vcell.message.server.sim.SolverPostprocessor
jre_bin=java
installdir='/usr/local/app'
jvm_jars='/usr/local/app/lib/*'
nativelib_dir="/usr/local/app/nativelibs/linux64"
solver_dir="/vcellbin"
jvm_props="-Dvcell.mongodb.database=test"
jvm_props="${jvm_props} -Dvcell.solver.primarySimdatadir=${basedatadir}"
jvm_props="${jvm_props} -Dvcell.solver.secondarySimdatadir=${basedatadir}"
jvm_props="${jvm_props} -Dvcell.server.id=${serverid}"
jvm_props="${jvm_props} -Dvcell.softwareVersion=${softwareVersion}"
jvm_props="${jvm_props} -Dvcell.installDir=${installdir}"
jvm_props="${jvm_props} -Dvcell.jms.url=${jmsurl}"
jvm_props="${jvm_props} -Dvcell.jms.user=${jmsuser}"
jvm_props="${jvm_props} -Dvcell.jms.password=${jmspswd}"
jvm_props="${jvm_props} -Dvcell.mongodb.host=${mongodbhost}"
jvm_props="${jvm_props} -Dvcell.mongodb.port=${mongodbport}"
jvm_props="${jvm_props} -Dvcell.jms.blobMessageUseMongo=true"


echo "props = ${jvm_props}"

#
# Call Preprocessor
#
echo
echo
echo "command = ${jre_bin} -cp ${jvm_jars} ${jvm_props} ${preprocessor_mainclass} ${datadir}/${simtaskfile} ${datadir}"
(
	export LD_LIBRARY_PATH=${nativelib_dir}:$EXISTING_LD_LIBRARY_PATH
    ${jre_bin} -cp "${jvm_jars}" ${jvm_props} ${preprocessor_mainclass} ${datadir}/${simtaskfile} ${datadir}
)
stat=$?
echo "${jre_bin} -cp ${jvm_jars} ${jvm_props} ${preprocessor_mainclass} ${datadir}/${simtaskfile} ${datadir} returned $stat"
echo
echo
if [ $stat -ne 0 ]; then
	#
	# error in pre-processor, invoke post-processor to send error and cleanup
	#
	(
	export LD_LIBRARY_PATH=${nativelib_dir}:$EXISTING_LD_LIBRARY_PATH
	postprocessor_args="${simkey} ${vcellusername} ${vcelluserkey} ${jobindex} ${taskid} ${stat} ${htclogsdir}/${submitfile}"
	echo "exitCommand = ${jre_bin} -cp ${jvm_jars} ${jvm_props} ${postprocessor_mainclass} ${postprocessor_args}"
	${jre_bin} -cp "${jvm_jars}" ${jvm_props} ${postprocessor_mainclass} ${postprocessor_args}
	)

	echo returning $stat to Slurm
	exit $stat
fi


#
# Call Solver
#
echo
echo command = ${solver_dir}/${solver} ${solver_args}
(
    export LD_LIBRARY_PATH=${solver_dir}:$EXISTING_LD_LIBRARY_PATH
    ${solver_dir}/${solver} ${solver_args}
)
stat=$?
echo ${solver_dir}/${solver} ${solver_args} returned $stat
if [ $stat -ne 0 ]; then
	#
	# error in solver, invoke post-processor to send error and cleanup
	#
	(
	export LD_LIBRARY_PATH=${nativelib_dir}:$EXISTING_LD_LIBRARY_PATH
	postprocessor_args="${simkey} ${vcellusername} ${vcelluserkey} ${jobindex} ${taskid} ${stat} ${htclogsdir}/${submitfile}"
	echo "exitCommand = ${jre_bin} -cp ${jvm_jars} ${jvm_props} ${postprocessor_mainclass} ${postprocessor_args}"
	${jre_bin} -cp "${jvm_jars}" ${jvm_props} ${postprocessor_mainclass} ${postprocessor_args}
	)
	
	echo returning $stat to Slurm
	exit $stat
fi


#
# exit normally, invoke post-processor to send complete status and cleanup
#
(
export LD_LIBRARY_PATH=${nativelib_dir}:$EXISTING_LD_LIBRARY_PATH
stat=0
postprocessor_args="${simkey} ${vcellusername} ${vcelluserkey} ${jobindex} ${taskid} ${stat} ${htclogsdir}/${submitfile}"
echo "exitCommand = ${jre_bin} -cp ${jvm_jars} ${jvm_props} ${postprocessor_mainclass} ${postprocessor_args}"
${jre_bin} -cp "${jvm_jars}" ${jvm_props} ${postprocessor_mainclass} ${postprocessor_args}
)
	

