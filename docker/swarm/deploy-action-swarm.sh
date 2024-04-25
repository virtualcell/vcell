#!/usr/bin/env bash

set -ux

show_help() {
	echo "Deploys or updates a deployment of VCell on remote Docker swarm cluster"
	echo ""
	echo "usage: deploy-action-swarm.sh [OPTIONS] REQUIRED-ARGUMENTS"
	echo ""
	echo "  REQUIRED-ARGUMENTS"
	echo "    manager-node          swarm node with manager role ( vcellapi.cam.uchc.edu or vcellapi-beta.cam.uchc.edu )"
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
	echo "    stack-name            name of Docker swarm stack"
	echo ""
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
	echo ""
	echo "    --ssh-user user       user for ssh to node [defaults to current user id using whoami]"
	echo "                          (user must have passwordless sudo for docker commands on manager-node)"
	echo ""
	echo "    --ssh-key  keyfile    ssh key for passwordless ssh to node"
	echo ""
	echo "    --build-installers    optionally build client installers and place in ./generated_installers dir"
	echo ""
	echo "    --installer-deploy-dir /path/to/installer/dir"
	echo "                          directory for installers accessible to users"
	echo "                          typically a web-accessible location to download the client installers for each platform"
	echo ""
	echo "    --install-singularity  optionally install batch and opt singularity images on each compute node in 'vcell' SLURM partition"
	echo ""
	echo ""
	echo "example:"
	echo ""
	echo "deploy-action-swarm.sh \\"
	echo "   --ssh-user vcell \\"
	echo "   --ssh-key ~/.ssh/schaff_rsa \\"
	echo "   --install_singularity \\"
	echo "   --build_installers --installer_deploy_dir /share/apps/vcell3/apache_webroot/htdocs/webstart/Rel \\"
	echo "   vcellapi.cam.uchc.edu \\"
	echo "   ./server.config \\"
	echo "   /usr/local/deploy/Test/server_01.config \\"
	echo "   ./docker-compose.yml \\"
	echo "   /usr/local/deploy/Test/docker-compose_01.yml \\"
	echo "   vcellrel"
	exit 1
}

if [[ $# -lt 6 ]]; then
    show_help
fi

ssh_user=$(whoami)
ssh_key=
installer_deploy_dir=
build_installers=false
#link_installers=false
install_singularity=false
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
		--installer-deploy-dir)
			shift
			installer_deploy_dir=$1
			;;
		--install-singularity)
			install_singularity=true
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

# get settings from config file
vcell_siteCamel=$(grep VCELL_SITE_CAMEL "$local_config_file" | cut -d"=" -f2)
vcell_version=$(grep VCELL_VERSION_NUMBER "$local_config_file" | cut -d"=" -f2)
vcell_build=$(grep VCELL_BUILD_NUMBER "$local_config_file" | cut -d"=" -f2)
batch_singularity_filename=$(grep VCELL_BATCH_SINGULARITY_FILENAME "$local_config_file" | cut -d"=" -f2)
batch_singularity_image_external=$(grep VCELL_BATCH_SINGULARITY_IMAGE_EXTERNAL "$local_config_file" | cut -d"=" -f2)
opt_singularity_filename=$(grep VCELL_OPT_SINGULARITY_FILENAME "$local_config_file" | cut -d"=" -f2)
opt_singularity_image_external=$(grep VCELL_OPT_SINGULARITY_IMAGE_EXTERNAL "$local_config_file" | cut -d"=" -f2)
#partitionName=$(grep VCELL_SLURM_PARTITION "$local_config_file" | cut -d"=" -f2)
batchHost=$(grep VCELL_BATCH_HOST "$local_config_file" | cut -d"=" -f2)
slurm_singularity_central_dir=$(grep VCELL_SLURM_CENTRAL_SINGULARITY_DIR "$local_config_file" | cut -d"=" -f2)


echo ""
echo "coping $local_config_file to $manager_node:$remote_config_file as user $ssh_user"
cmd="scp $ssh_key $local_config_file $ssh_user@$manager_node:$remote_config_file"
($cmd) || (echo "failed to upload config file" && exit 1)

echo ""
echo "coping $local_compose_file to $manager_node:$remote_compose_file as user $ssh_user"
cmd="scp $ssh_key $local_compose_file $ssh_user@$manager_node:$remote_compose_file"
($cmd) || (echo "failed to upload docker-compose file" && exit 1)


#
# install the singularity images on the cluster nodes
#
if [ "$install_singularity" == "true" ]; then

	echo ""
	pushd ../build/singularity-vm || (echo "pushd ../build/singularity-vm failed"; exit 1)
	echo ""
	echo "CURRENT DIRECTORY IS $PWD"

	#
	# get configuration from config file and load into current bash environment
	#
	echo ""

	if [ ! -e "./${batch_singularity_filename}" ]; then
		echo "failed to find local batch singularity image file $batch_singularity_filename in ./singularity-vm directory"
		exit 1
	fi

	if ! scp "./${batch_singularity_filename}" "$ssh_user@$manager_node:${slurm_singularity_central_dir}"; then
    echo "failed to copy batch singularity image to server"
    exit 1
  fi

	if [ ! -e "./${opt_singularity_filename}" ]; then
		echo "failed to find local opt singularity image file $opt_singularity_filename in ./singularity-vm directory"
		exit 1
	fi

	if ! scp "./${opt_singularity_filename}" "$ssh_user@$manager_node:${slurm_singularity_central_dir}"; then
	  echo "failed to copy opt singularity image to server"
	  exit 1
	fi

	echo "popd"
	popd || (echo "popd failed"; exit 1)
fi


#
# deploy the stack on remote cluster
#
echo ""
echo "deploying stack $stack_name to $manager_node using config in $manager_node:$remote_config_file"
localmachine=$(hostname)
if [ "$localmachine" == "$manager_node" ]; then
	if ! env $(xargs < "$remote_config_file") docker stack deploy -c "$remote_compose_file" "$stack_name";
	then echo "failed to deploy stack" && exit 1; fi
else
	cmd="ssh $ssh_key -t $ssh_user@$manager_node sudo env \$(cat $remote_config_file | xargs) docker stack deploy -c $remote_compose_file $stack_name --with-registry-auth"
	($cmd) || (echo "failed to deploy stack" && exit 1)
fi

#
# if --build-installers, then generate client installers, placing then in ./generated_installers
#    if --installer-deploy-dir, then also copy installers to $installer_deploy_dir
#  *** unimplemented *** (if --link-installers, then also link installers to version independent installer names for each platform)
#
if [ "$build_installers" == "true" ]; then
	#
	# if --installer-deploy-dir, then copy the installers from ./generated_installers directory to the installer deploy directory
	#
	if [ ! -z "$installer_deploy_dir" ]; then
		# vcell_siteCamel=Alpha
		# vcell_version=7.0.0
		# vcell_build=19
		# version=7_0_0_19
		version=$(echo "${vcell_version}_${vcell_build}" | tr '.' _)
		if ! scp "./generated_installers/VCell_${vcell_siteCamel}_windows-x64_${version}_64bit.exe" \
			"./generated_installers/VCell_${vcell_siteCamel}_unix_${version}_32bit.sh" \
			"./generated_installers/VCell_${vcell_siteCamel}_macos_${version}_64bit.dmg" \
			"./generated_installers/VCell_${vcell_siteCamel}_windows-x32_${version}_32bit.exe" \
			"./generated_installers/VCell_${vcell_siteCamel}_unix_${version}_64bit.sh" \
			"./generated_installers/updates.xml" \
			"./generated_installers/updates_linux32.xml" \
			"./generated_installers/updates_linux64.xml" \
			"./generated_installers/updates_mac64.xml" \
			"./generated_installers/updates_win32.xml" \
			"./generated_installers/updates_win64.xml" \
			"./generated_installers/output.txt" \
			"./generated_installers/md5sums" \
			"./generated_installers/sha256sums" \
				"$ssh_user@$manager_node:${installer_deploy_dir}";
		then
			echo "failed to copy installers"; 
			exit 1;
		fi

	fi
fi


echo "exited normally"

exit 0
