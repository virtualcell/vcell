#!/usr/bin/env bash

shopt -s -o nounset

show_help() {
  echo "Performs server maintenance tasks"
  echo ""
  echo "usage: vcell-services [OPTIONS] command site"
  echo ""
  echo "  REQUIRED-ARGUMENTS"
  echo "    command          restart | status | health | shutdown | restore"
  echo ""
  echo "                     Common Commands:"
  echo "                        restart  - Restarts swarm services (takes optional -a | --all option)"
  echo "                        status   - Shows status of all swarm services (e.g. vcellrel_api)"
  echo "                        health   - Prints the login and simulation health checks"
  echo ""
  echo "                     Advanced Commands:"
  echo "                        shutdown - Scales down swarm services to 0 replicas each (takes optional -a | --all option)."
  echo "                                      needed to swap secrets which cannot be done while services are running"
  echo "                        restore  - Scales up swarm services to 1 replica each (takes optional -a | --all option)."
  echo "                                      Restores always to 1 replica each, even if the deployed swarm configuration"
  echo "                                      calls for more than 1 replica for some services."
  echo ""
  echo "    site             vcell site (e.g. rel or alpha or test)"
  echo "                        rel - must be run on vcellapi.cam.uchc.edu"
  echo "                        alpha - must be run on vcellapi-beta.cam.uchc.edu"
  echo "                        test - must be run on vcellapi-test.cam.uchc.edu"
  echo ""
  echo "  [OPTIONS]"
  echo "    -a | --all       (for restart, shutdown or restore commands) effects messaging and mongodb services as well"
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
  -a | --all)
    all=true
    ;;
  -h | --help)
    show_help
    exit
    ;;
  -d | --debug)
    debug=true
    ;;
  -?*)
    printf 'ERROR: Unknown option: %s\n' "$1" 1>&2
    echo ""
    show_help
    ;;
  *) # Default case: No more options, so break out of the loop.
    break ;;
  esac
  shift
done

if [[ $# -ne 2 ]]; then
  show_help
fi

# if debug is true print all shell commands
if [[ "${debug}" == true ]]; then
  set -x
fi

command=$1
site=$2

declare -a commands=("restart" "status" "health" "shutdown" "restore")

for i in "${commands[@]}"; do
  if [[ "${command}" == "${i}" ]]; then
    command_valid=true
    break
  fi
done
if [[ "${command_valid}" != true ]]; then
  echo "command must be one of the following: ${commands[*]}" 1>&2
  exit 1
fi

declare -a sites=("rel" "alpha" "test")

case "${site}" in
rel)
  head_node="vcellapi.cam.uchc.edu"
  api_port="443"
  api_path_prefix=""
  ;;
alpha)
  head_node="vcellapi-beta.cam.uchc.edu"
  api_port="8080"
  api_path_prefix="/api/v0"
  ;;
test)
  head_node="vcellapi-test.cam.uchc.edu"
  api_port="443"
  api_path_prefix="/api/v0"
  ;;
*)
  echo "site must be one of the following: ${sites[*]}" 1>&2
  exit 1
  ;;
esac

swarm_cluster_name="vcell${site}"

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

# rescale a service
# $1 is the service name
# $2 is the number of replicas
function rescale_service {
  service=$1
  replicas=$2
  if [[ "${debug}" == true ]]; then
    echo "rescaling ${service} service to ${replicas} replicas"
  fi
  sudo docker service scale ${service}=${replicas}
  retcode=$?
  # if service scale failed, then exit
  if [[ ${retcode} -ne 0 ]]; then
    echo "" 1>&2
    echo "failed to scale ${service} service to ${replicas} replicas" 1>&2
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
    restart_service ${swarm_cluster_name}_s3proxy
  fi
  restart_service ${swarm_cluster_name}_db
  restart_service ${swarm_cluster_name}_data
  restart_service ${swarm_cluster_name}_sched
  restart_service ${swarm_cluster_name}_submit
  restart_service ${swarm_cluster_name}_api
  exit 0
fi

# if command is shutdown or restore, then scale services
if [[ "${command}" == "shutdown" || "${command}" == "restore" ]]; then
  if [[ "${command}" == "shutdown" ]]; then
    replicas=0
  else
    replicas=1
  fi
  # scale messaging and mongodb services if requested
  if [[ "${all}" == true ]]; then
    rescale_service ${swarm_cluster_name}_activemqsim ${replicas}
    rescale_service ${swarm_cluster_name}_activemqint ${replicas}
    rescale_service ${swarm_cluster_name}_mongodb ${replicas}
    rescale_service ${swarm_cluster_name}_s3proxy ${replicas}
  fi
  rescale_service ${swarm_cluster_name}_db ${replicas}
  rescale_service ${swarm_cluster_name}_data ${replicas}
  rescale_service ${swarm_cluster_name}_sched ${replicas}
  rescale_service ${swarm_cluster_name}_submit ${replicas}
  rescale_service ${swarm_cluster_name}_api ${replicas}
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
  echo "curl https://${head_node}:${api_port}${api_path_prefix}/health?check=login"
  curl "https://${head_node}:${api_port}${api_path_prefix}/health?check=login"
  retcode=$?
  if [[ ${retcode} -ne 0 ]]; then
    echo "" 1>&2
    echo "failed to connect to api service at ${head_node}:${api_port} and path prefix ${api_path_prefix}" 1>&2
    exit 1
  fi
  echo ""
  echo ""
  echo "-- sim check (status should be 'OK') -- "
  echo "curl https://${head_node}:${api_port}${api_path_prefix}/health?check=sim"
  curl "https://${head_node}:${api_port}${api_path_prefix}/health?check=sim"
  retcode=$?
  if [[ ${retcode} -ne 0 ]]; then
    echo "" 1>&2
    echo "failed to connect to api service at ${head_node}:${api_port} and path prefix ${api_path_prefix}" 1>&2
    exit 1
  fi
  echo ""
  echo ""
  echo "-- for all health events, run the following -- "
  echo "curl https://${head_node}:${api_port}${api_path_prefix}/health?check=all | jq ."
  exit 0
fi
