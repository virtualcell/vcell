#!/usr/bin/env bash



show_help() {
	echo "Deploys or updates a deployment of VCell on remote Docker swarm cluster"
	echo ""
	echo "usage: deploy.sh [OPTIONS] REQUIRED-ARGUMENTS"
	echo ""
	echo "  REQUIRED-ARGUMENTS"
	echo "    manager-node          swarm node with manager role ( vcellapi.cam.uchc.edu )"
	echo ""
	echo "    local-config-file     local config file for deployment, copied to manager-node including:"
	echo "                              VCELL_REPO=repo (e.g. schaff or vcell-docker.cam.uchc.edu:5000 )"
	echo "                                              (must be reachable from swarm nodes)"
	echo "                              VCELL_TAG=tag (e.g. dev | 7.0.0-alpha.4 | f98dfe3)"
	echo ""
	echo "    remote-config-file    absolute path of target config file on remote manager-node"
	echo "                          WARNING: will overwrite remote file"
	echo ""
	echo "    local-compose-file    local docker-compose.yml file for deployment, copied to manager-node"
	echo ""
	echo "    remote-compose-file   absolute path of target docker-compose.yml file on remote manager-node"
	echo "                          WARNING: will overwrite remote file"
	echo ""
	echo "    stack-name            name of swarm stack"
	echo ""
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
	echo ""
	echo "    --ssh-user user       user for ssh to node [defaults to current user id using whoami]"
	echo "                          (user must have passwordless sudo for docker commands on manager-node)"
	echo ""
	echo "    --ssh-key  keyfile    ssh key for passwordless ssh to node"
	echo ""
	echo "    --installer-deploy remote-location"
	echo "                          where remote-location is machine:/path/to/intstaller/dir should be"
	echo "                          a web-accessible location to download the client installers for each platform"
	echo "                          WARNING replaces contents of ./generated_installers directory"
	echo ""
	echo ""
	echo "example:"
	echo ""
	echo "deploy.sh --ssh-user vcell --ssh-key ~/.ssh/schaff_rsa \\"
	echo "  vcell-service.cam.uchc.edu \\"
	echo "  ./server.config /usr/local/deploy/Test/server_01.config \\"
	echo "  ./docker-compose.yml /usr/local/deploy/Test/docker-compose_01.yml \\"
	echo "  vcelltest"
	exit 1
}

if [[ $# -lt 6 ]]; then
    show_help
fi

ssh_user=$(whoami)
ssh_key=
installer_deploy=
while :; do
	case $1 in
		-h|--help)
			show_help
			exit
			;;
		--ssh-user)
			shift
			ssh_user=$1
			;;
		--ssh-key)
			shift
			ssh_key=$1
			;;
		--installer-deploy)
			shift
			installer_deploy=$1
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

if [[ $# -ne 6 ]] ; then
    show_help
fi

manager_node=$1
local_config_file=$2
remote_config_file=$3
local_compose_file=$4
remote_compose_file=$5
stack_name=$6

echo ""
echo "coping $local_config_file to $manager_node:$remote_config_file as user $ssh_user"
retcode=1
if [ ! -z $ssh_key ]; then
	echo "scp -i $ssh_key $local_config_file $ssh_user@$manager_node:$remote_config_file"
	scp -i $ssh_key $local_config_file $ssh_user@$manager_node:$remote_config_file
	retcode=$?
else
	echo "scp $local_config_file $ssh_user@$manager_node:$remote_config_file"
	scp $local_config_file $ssh_user@$manager_node:$remote_config_file
	retcode=$?
fi
if [[ $retcode -ne 0 ]]; then
	echo "failed to upload config file, returned $retcode"
	exit 1
fi

echo ""
echo "coping $local_compose_file to $manager_node:$remote_compose_file as user $ssh_user"
retcode=1
if [ ! -z $ssh_key ]; then
	echo "scp -i $ssh_key $local_compose_file $ssh_user@$manager_node:$remote_compose_file"
	scp -i $ssh_key $local_compose_file $ssh_user@$manager_node:$remote_compose_file
	retcode=$?
else
	echo "scp $local_compose_file $ssh_user@$manager_node:$remote_compose_file"
	scp $local_compose_file $ssh_user@$manager_node:$remote_compose_file
	retcode=$?
fi
if [[ $retcode -ne 0 ]]; then
	echo "failed to upload docker-compose file, returned $retcode"
	exit 1
fi

echo ""
echo "deploying stack $stack_name to $manager_node using config in $manager_node:$remote_config_file"
deploy_cmd="sudo env \$(cat $remote_config_file | xargs) docker stack deploy -c $remote_compose_file $stack_name"
retcode=1
if [ ! -z $ssh_key ]; then
	echo "ssh -i $ssh_key -t $ssh_user@$manager_node $deploy_cmd"
	ssh -i $ssh_key -t $ssh_user@$manager_node $deploy_cmd
	retcode=$?
else	
	echo "ssh -t $ssh_user@$manager_node $deploy_cmd"
	ssh -t $ssh_user@$manager_node $deploy_cmd
	retcode=$?
fi
if [[ $retcode -ne 0 ]]; then
	echo "failed to deploy stack, returned $retcode"
	exit 1
fi

#
# generate client installers, placing then in ./generated_installers
#
retcode=1
# remove old installers
if [ -e "./generated_installers" ]; then
	echo "rm ./generated_installers/*"
	rm ./generated_installers/*
fi
# remove old installer Docker container
echo "env \$(cat $local_config_file | xargs) docker-compose -f ./docker-compose-clientgen.yml rm --force"
env $(cat $local_config_file | xargs) docker-compose -f ./docker-compose-clientgen.yml rm --force
retcode=$1
if [[ $retcode -ne 0 ]]; then
	echo "failed to remove previous container for vcell-clientgen, returned $retcode"
	exit 1
fi
# run vcell-clientgen to generate new installers (placed into ./generated_installers)
retcode=1
echo "env \$(cat $local_config_file | xargs) docker-compose -f ./docker-compose-clientgen.yml up"
env $(cat $local_config_file | xargs) docker-compose -f ./docker-compose-clientgen.yml up
retcode=$?
if [[ $retcode -ne 0 ]]; then
	echo "failed to run vcell-clientgen, returned $retcode"
	exit 1
fi

#
# if --installer-deploy, then scp the installers to the web directory
#
retcode=1
if [ ! -z $installer_deploy ]; then
	echo ""
	echo "coping installers to $installer_deploy as user $ssh_user"
	retcode=1
	if [ ! -z $ssh_key ]; then
		echo "scp -i $ssh_key ./generated_installers/* $ssh_user@$installer_deploy"
		scp -i $ssh_key ./generated_installers/* $ssh_user@$installer_deploy
		retcode=$?
	else
		echo "scp ./generated_installers/* $ssh_user@$installer_deploy"
		scp ./generated_installers/* $ssh_user@$installer_deploy
		retcode=$?
	fi
	if [[ $retcode -ne 0 ]]; then
		echo "failed to upload generated client installers, returned $retcode"
		exit 1
	fi
fi


echo "exited normally"

exit 0


