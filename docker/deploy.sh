#!/usr/bin/env bash

shopt -s -o nounset

show_help() {
	echo "Deploys or updates a deployment of VCell on remote Docker swarm cluster"
	echo ""
	echo "usage: deploy.sh [OPTIONS] REQUIRED-ARGUMENTS"
	echo ""
	echo "  REQUIRED-ARGUMENTS"
	echo "    manager-node          swarm node with manager role ( vcellapi.cam.uchc.edu )"
	echo ""
	echo "    local-config-file     local config file for deployment, copied to manager-node including:"
	echo "                              VCELL_REPO_NAMESPACE=(repo/namespace | namespace)"
	echo "                                  (e.g. schaff or vcell-docker.cam.uchc.edu:5000/schaff )"
	echo "                                  (must be reachable from swarm nodes and include namespace)"
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
	echo "    --upload-singularity  optionally upload Singularity image for vcell-batch container"
	echo ""
	echo "    --build-installers    optionally build client installers and place in ./generated_installers dir"
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
upload_singularity=false
build_installers=false
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
			ssh_key="-i $1"
			;;
		--installer-deploy)
			shift
			installer_deploy=$1
			;;
		--upload-singularity)
			upload_singularity=true
			;;
		--build-installers)
			build_installers=true
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
cmd="scp $ssh_key $local_config_file $ssh_user@$manager_node:$remote_config_file"
echo $cmd
($cmd) || (echo "failed to upload config file" && exit 1)

echo ""
echo "coping $local_compose_file to $manager_node:$remote_compose_file as user $ssh_user"
cmd="scp $ssh_key $local_compose_file $ssh_user@$manager_node:$remote_compose_file"
echo $cmd
($cmd) || (echo "failed to upload docker-compose file" && exit 1)



## BEGIN SINGULARITY UPLOAD
if [ "$upload_singularity" == "true" ]; then
	echo ""
	cmd="cd singularity-vm"
	cd singularity-vm
	echo ""
	echo "CURRENT DIRECTORY IS $PWD"

	#
	# get configuration from config file and load into current bash environment
	#
	echo ""
	singularity_filename=`cat ../$local_config_file | grep VCELL_SINGULARITY_FILENAME | cut -d"=" -f2`
	singularity_remote_path=`cat ../$local_config_file | grep VCELL_SINGULARITY_IMAGE_EXTERNAL | cut -d"=" -f2`

	if [ ! -e "./${singularity_filename}" ]; then
		echo "failed to find local singularity image file $singularity_filename in ./singularity-vm directory"
		exit 1
	fi

	# copy singularity image from singularity-vm directory to remote destination
	echo ""
	echo "coping ./$singularity_filename to $singularity_remote_path on $manager_node as user $ssh_user"
	cmd="scp $ssh_key ./$singularity_filename ${ssh_user}@${manager_node}:${singularity_remote_path}"
	echo $cmd
	($cmd) || (echo "failed to upload generated singularity image for vcell-batch" && exit 1)

	echo "cd .."
	cd ..
fi
## END SINGULARITY BUILD



#
# deploy the stack on remote cluster
#
echo ""
echo "deploying stack $stack_name to $manager_node using config in $manager_node:$remote_config_file"
localmachine=`hostname`
if [ "$localmachine" == "$manager_node" ]; then
	echo "env \$(cat $remote_config_file | xargs) docker stack deploy -c $remote_compose_file $stack_name"
	env $(cat $remote_config_file | xargs) docker stack deploy -c $remote_compose_file $stack_name
	if [[ $? -ne 0 ]]; then echo "failed to deploy stack" && exit 1; fi
else
	cmd="ssh $ssh_key -t $ssh_user@$manager_node sudo env \$(cat $remote_config_file | xargs) docker stack deploy -c $remote_compose_file $stack_name"
	echo $cmd
	($cmd) || (echo "failed to deploy stack" && exit 1)
fi

#
# generate client installers, placing then in ./generated_installers
#
if [ "$build_installers" == "true" ]; then
	# remove old installers
	if [ -e "./generated_installers" ]; then
		cmd="rm -f ./generated_installers/*"
		echo $cmd
		$cmd
	fi

	# run vcell-clientgen to generate new installers (placed into ./generated_installers)
	echo ""
	echo "./generate_installers.sh $local_config_file"
	./generate_installers.sh $local_config_file
	if [[ $? -ne 0 ]]; then echo "failed to run vcell-clientgen" && exit 1; fi

	#
	# if --installer-deploy, then scp the installers to the web directory
	#
	if [ ! -z $installer_deploy ]; then
		echo ""
		echo "coping installers to $installer_deploy as user $ssh_user"
		cmd="scp $ssh_key ./generated_installers/* $ssh_user@$installer_deploy"
		echo $cmd
		($cmd) || (echo "failed to upload generated client installers" && exit 1)
	fi
fi

echo "exited normally"

exit 0


