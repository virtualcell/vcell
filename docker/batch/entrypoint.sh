#!/usr/bin/env bash

show_help() {
	echo "*************************************************"
	echo "** Virtual Cell command line tools and solvers **"
	echo "*************************************************"
	echo ""
	echo "usage: docker run --rm -e v1=a -e v2=b -v /host/path/to/file:/simdata schaff/vcell-batch:latest [OPTIONS] command <args>"
	echo "  or   singularity run --bind /host/path/to/file:/simdata docker://schaff/vcell-batch:latest [OPTIONS] command <args>"
	echo ""
	echo "  Script Commands"
	echo "    JavaPreprocessor64"
	echo "    JavaPostprocessor64"
	echo "    JavaSimExe64"
	echo "    SendErrorMsg                 requires all --msg- and --msg-job options below)"
	echo ""
	echo "  Solver Commands - see https://github.com/virtualcell or http://vcell.org"
	echo "    FiniteVolume_x64"
	echo "    FiniteVolume_PETSc_x64"
	echo "    MovingBoundary_x64"
	echo "    NFsim_x64"
	echo "    smoldyn_x64"
	echo "    SundialsSolverStandalone_x64"
	echo "    VCellStoch_x64"
	echo ""
	echo "  [OPTIONS]"
	echo "    -h | --help                  show this message"
	echo "    -e | --env var=value         add environment variable (needed for Singularity only)"
	echo "                                    this option can be repeated to define multiple variables"
	echo "    --msg-userid  userid         messaging userid"
	echo "    --msg-password  pswd         messaging password"
	echo "    --msg-host  host             messaging host"
	echo "    --msg-port  port             messaging port"
	echo "    --msg-job-host  host         messaging job host"
	echo "    --msg-job-userid  userid     messaging job userid"
	echo "    --msg-job-simkey  simkey     messaging job simkey"
	echo "    --msg-job-jobindex  index    messaging job jobindex"
	echo "    --msg-job-taskid  taskid     messaging job taskid"
	echo "    --msg-job-errmsg  msg        messaging job error message"
	exit 1
}

if [ "$#" -lt 1 ]; then
    show_help
fi

rawurlencode() {
  local string="${1}"
  local strlen=${#string}
  local encoded=""
  local pos c o

  for (( pos=0 ; pos<strlen ; pos++ )); do
     c=${string:$pos:1}
     case "$c" in
        [-_.~a-zA-Z0-9] ) o="${c}" ;;
        * )               printf -v o '%%%02x' "'$c"
     esac
     encoded+="${o}"
  done
  echo "${encoded}"
}


msg_userid=
msg_password=
msg_host=
msg_port=
msg_job_host=
msg_job_userid=
msg_job_simkey=
msg_job_jobindex=
msg_job_taskid=
msg_job_errmsg=
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
		--msg-userid)
			shift
			msg_userid=$1
			;;
		--msg-password)
			shift
			msg_password=$1
			;;
		--msg-host)
			shift
			msg_host=$1
			;;
		--msg-port)
			shift
			msg_port=$1
			;;
		--msg-job-host)
			shift
			msg_job_host=$1
			;;
		--msg-job-userid)
			shift
			msg_job_userid=$1
			;;
		--msg-job-simkey)
			shift
			msg_job_simkey=$1
			;;
		--msg-job-jobindex)
			shift
			msg_job_jobindex=$1
			;;
		--msg-job-taskid)
			shift
			msg_job_taskid=$1
			;;
		--msg-job-errmsg)
			shift
			msg_job_errmsg=$( rawurlencode "$1" )
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
echo $@
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
	FiniteVolume_PETSc_x64)
		/vcellbin/FiniteVolume_PETSc_x64 $arguments
		exit $?
		;;
	MovingBoundary_x64)
		/vcellbin/MovingBoundary_x64 $arguments
		exit $?
		;;
	NFsim_x64)
		/vcellbin/NFsim_x64 $arguments
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
	SendErrorMsg)
		#
		# Send solver or Slurm script error message to VCell messaging infrastructure
		#
		# curl -XPOST http://admin:admin@vcellapi-beta.cam.uchc.edu:8162/api/message/workerEvent?type=queue&JMSPriority=5\
		# &JMSTimeToLive=600000&JMSDeliveryMode=persistent&MessageType=WorkerEvent&UserName=adthomas\
		# &HostName=shangrila14&SimKey=128934428&TaskID=0&JobIndex=6&WorkerEvent_Status=1002\
		# &WorkerEvent_StatusMsg=CVODE%20solver%20failed%20%3A%20at%20time%205%2C%20discontinuity%20%28t%20%3E%3D%205%29%20evaluated%20to%20TRUE%2C%20solver%20assumed%20FALSE\
		# &WorkerEvent_Progress=0&WorkerEvent_TimePoint=0
		#
		#
		# failure code is 1002 (see cbit.rmi.event.WorkerEvent.JOB_FAILURE)
		#
		failurecode=1002
		url="http://${msg_userid}:${msg_password}@${msg_host}:${msg_port}/api/message/workerEvent"
		args="?type=queue"
		args="${args}&JMSPriority=5"
		args="${args}&JMSTimeToLive=600000"
		args="${args}&JMSDeliveryMode=persistent"
		args="${args}&MessageType=WorkerEvent"
		args="${args}&UserName=${msg_job_userid}"
		if [ ! -z ${msg_job_host+x} ]; then
			args="${args}&HostName=${msg_job_host}"
		fi
		args="${args}&SimKey=${msg_job_simkey}"
		args="${args}&TaskID=${msg_job_taskid}"
		args="${args}&JobIndex=${msg_job_jobindex}"
		args="${args}&WorkerEvent_Status=${failurecode}"
		args="${args}&WorkerEvent_StatusMsg=${msg_job_errmsg}"
		args="${args}&WorkerEvent_Progress=0"
		args="${args}&WorkerEvent_TimePoint=0"
		echo curl -XPOST "${url}${args}"
		curl -XPOST "${url}${args}"
		exit $?
		;;
	*)
		printf 'ERROR: Unknown command: %s\n' "$command" >&2
		echo ""
		show_help
		;;

esac
