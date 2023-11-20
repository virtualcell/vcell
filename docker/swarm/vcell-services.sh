#!/usr/bin/env bash

shopt -s -o nounset

show_help() {
	echo "Performs server maintenance tasks"
	echo ""
	echo "usage: vcell-services [OPTIONS] command site"
	echo ""
	echo "  REQUIRED-ARGUMENTS"
	echo "    command          restart | status | health"
	echo "                        restart - restarts all swarm services (takes optional -a | --all option)"
	echo "                        status  - shows status of all swarm services (e.g. vcellrel_api)"
	echo "                        health  - prints the login and simulation health checks"
	echo "    site             vcell site (e.g. rel or alpha)"
	echo "                        rel - must be run on vcellapi.cam.uchc.edu"
	echo "                        alpha - must be run on vcellapi-beta.cam.uchc.edu"
	echo ""
	echo "  [OPTIONS]"
	echo "    -a | --all       (for restart command) restart messaging and mongodb services as well"
	echo "    -h | --help      show this message"
	echo "    -d | --debug     print debug information"
	echo ""
	echo "example:"
	echo ""
	echo "vcell-services restart rel"
	exit 1
}

if [[ $# -lt 2 ]]; then
    show_help
fi


debug=false
all=false
while :; do
	case $1 in
		-a|--all)
			all=true
			;;
		-h|--help)
			show_help
			exit
			;;
		-d|--debug)
			debug=true
			;;
		-?*)
			printf 'ERROR: Unknown option: %s\n' "$1" 1>&2
			echo ""
			show_help
			;;
		*)               # Default case: No more options, so break out of the loop.
			break
	esac
	shift
done

if [[ $# -ne 2 ]] ; then
    show_help
fi

# if debug is true print all shell commands
if [[ "${debug}" == true ]]; then
  set -x
fi

command=$1
site=$2

# check that command is either 'restart' or 'status'
if [[ "${command}" != "restart" && "${command}" != "status" && "${command}" != "health" ]]; then
    echo "command must be either 'restart' or 'status' or 'health'" 1>&2
    exit 1
fi

# check that site is either 'rel' or 'alpha'
if [[ "${site}" != "rel" && "${site}" != "alpha" ]]; then
    echo "site must be either 'rel' or 'alpha'" 1>&2
    exit 1
fi

swarm_cluster_name="vcell${site}"

head_node="vcellapi.cam.uchc.edu"
api_port="443"
if [[ "${site}" == "alpha" ]]; then
  head_node="vcellapi-beta.cam.uchc.edu"
  api_port="8080"
fi

# restarts a service
# $1 is the service name
function restart_service {
    service=$1
    if [[ "${debug}" == true ]]; then
      echo "restarting ${service} service"
    fi
    sudo docker service update --force --detach=false ${service}
    retcode=$?
    # if service update failed, then exit
    if [[ ${retcode} -ne 0 ]]; then
      echo "" 1>&2
      echo "failed to restart ${service} service" 1>&2
      echo "" 1>&2
      echo "note: for ${site} site commands, run this script on ${head_node}" 1>&2
      exit 1
    fi
}

# if command is restart, then stop and start all services
if [[ "${command}" == "restart" ]]; then

  # restart messaging and mongodb services if requested
  if [[ "${all}" == true ]]; then
    restart_service ${swarm_cluster_name}_activemqsim
    restart_service ${swarm_cluster_name}_activemqint
    restart_service ${swarm_cluster_name}_mongodb
  fi
  restart_service ${swarm_cluster_name}_db
  restart_service ${swarm_cluster_name}_data
  restart_service ${swarm_cluster_name}_sched
  restart_service ${swarm_cluster_name}_submit
  restart_service ${swarm_cluster_name}_api
  exit 0
fi

# if command is status, then show status of all services
if [[ "${command}" == "status" ]]; then
  sudo docker stack ps -f "desired-state=running" ${swarm_cluster_name}
  retcode=$?
  if [[ ${retcode} -ne 0 ]]; then
    echo "" 1>&2
    echo "failed to show status of services" 1>&2
    echo "" 1>&2
    echo "note: for ${site} site commands, run this script on ${head_node}" 1>&2
    exit 1
  fi
  exit 0
fi

# if command is health, then show health of login and sim checks
if [[ "${command}" == "health" ]]; then
  echo "-- login check (status should be 'OK') -- "
  echo "curl https://${head_node}:${api_port}/health?check=login"
  curl "https://${head_node}:${api_port}/health?check=login"
  retcode=$?
  if [[ ${retcode} -ne 0 ]]; then
    echo "" 1>&2
    echo "failed to connect to api service at ${head_node}:${api_port}" 1>&2
    exit 1
  fi
  echo ""
  echo ""
  echo "-- sim check (status should be 'OK') -- "
  echo "curl https://${head_node}:${api_port}/health?check=sim"
  curl "https://${head_node}:${api_port}/health?check=sim"
  retcode=$?
  if [[ ${retcode} -ne 0 ]]; then
    echo "" 1>&2
    echo "failed to connect to api service at ${head_node}:${api_port}" 1>&2
    exit 1
  fi
  echo ""
  echo ""
  echo "-- for all health events, run the following -- "
  echo "curl https://${head_node}:${api_port}/health?check=all | jq ."
  exit 0
fi
