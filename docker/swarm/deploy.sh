#!/usr/bin/env bash

shopt -s -o nounset

show_help() {
	echo "Deploys or updates a deployment of VCell on remote Docker swarm cluster"
	echo ""
	echo "usage: deploy.sh [OPTIONS] REQUIRED-ARGUMENTS"
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
	echo "    --installer-deploy-dir /path/to/intstaller/dir"
	echo "                          directory for installers accessible to users"
	echo "                          typically a web-accessible location to download the client installers for each platform"
	echo ""
	echo "    --link-installers     optionally create symbolic links for newly created client installers"
	echo "                          for permanent 'latest' web links fr each platform"
	echo ""
	echo "    --install-singularity  optionally install singularity image on each compute node in 'vcell' SLURM partition"
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
installer_deploy_dir=
build_installers=false
link_installers=false
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
		--link-installers)
			link_installers=true
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
vcell_siteCamel=`cat $local_config_file | grep VCELL_SITE_CAMEL | cut -d"=" -f2`
vcell_version=`cat $local_config_file | grep VCELL_VERSION_NUMBER | cut -d"=" -f2`
vcell_build=`cat $local_config_file | grep VCELL_BUILD_NUMBER | cut -d"=" -f2`
singularity_filename=`cat $local_config_file | grep VCELL_SINGULARITY_FILENAME | cut -d"=" -f2`
singularity_image_external=`cat $local_config_file | grep VCELL_SINGULARITY_IMAGE_EXTERNAL | cut -d"=" -f2`
#partitionName=`cat $local_config_file | grep VCELL_SLURM_PARTITION | cut -d"=" -f2`
batchHost=`cat $local_config_file | grep VCELL_BATCH_HOST | cut -d"=" -f2`
slurm_singularity_central_dir=`cat $local_config_file | grep VCELL_SLURM_CENTRAL_SINGULARITY_DIR | cut -d"=" -f2`


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


#
# install the singularity image on the cluster nodes
#
if [ "$install_singularity" == "true" ]; then

	echo ""
	cmd="pushd ../build/singularity-vm"
	pushd ../build/singularity-vm
	echo ""
	echo "CURRENT DIRECTORY IS $PWD"

	#
	# get configuration from config file and load into current bash environment
	#
	echo ""

	if [ ! -e "./${singularity_filename}" ]; then
		echo "failed to find local singularity image file $singularity_filename in ./singularity-vm directory"
		exit 1
	fi

	echo "mkdir -p ${slurm_singularity_central_dir}"
	mkdir -p ${slurm_singularity_central_dir}
	echo "cp ./${singularity_filename} ${slurm_singularity_central_dir}"
	cp ./${singularity_filename} ${slurm_singularity_central_dir}

	echo "popd"
	popd
fi


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
# if --build-installers, then generate client installers, placing then in ./generated_installers
#    if --installer-deploy-dir, then also copy installers to $installer_deploy_dir
#        if --link-installers, then also link installers to version independent installer names for each platform
#
if [ "$build_installers" == "true" ]; then
	#
	# if --installer-deploy-dir, then copy the installers from ./generated_installers directory to the installer deploy directory
	#
	if [ ! -z $installer_deploy_dir ]; then
		# vcell_siteCamel=Alpha
		# vcell_version=7.0.0
		# vcell_build=19
		# version=7_0_0_19
		version=$(echo "${vcell_version}_${vcell_build}" | tr '.' _)
		cp ./generated_installers/VCell_${vcell_siteCamel}_windows-x64_${version}_64bit.exe \
			./generated_installers/VCell_${vcell_siteCamel}_windows-x64_${version}_64bit.dat/* \
			./generated_installers/VCell_${vcell_siteCamel}_unix_${version}_32bit.sh \
			./generated_installers/VCell_${vcell_siteCamel}_macos_${version}_64bit.dmg \
			./generated_installers/VCell_${vcell_siteCamel}_windows_${version}_32bit.exe \
			./generated_installers/VCell_${vcell_siteCamel}_unix_${version}_64bit.sh \
			./generated_installers/updates.xml \
			./generated_installers/output.txt \
			./generated_installers/md5sums \
				${installer_deploy_dir}
		if [[ $? -ne 0 ]]; then 
			echo "failed to copy installers"; 
			exit 1;
		fi

		#
		# if --link-installers, then create symbolic links from permanent paths to most recent installers (for durable web urls).
		#
		if [ "$link_installers" == "true" ]; then

			pushd ${installer_deploy_dir}

			rm VCell_${vcell_siteCamel}_windows-x64_latest_64bit.exe && \
			ln -s VCell_${vcell_siteCamel}_windows-x64_${version}_64bit.exe \
				  VCell_${vcell_siteCamel}_windows-x64_latest_64bit.exe
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Win64 installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_unix_latest_32bit.sh && \
			ln -s VCell_${vcell_siteCamel}_unix_${version}_32bit.sh \
				  VCell_${vcell_siteCamel}_unix_latest_32bit.sh
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Linux32 installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_macos_latest_64bit.dmg && \
			ln -s VCell_${vcell_siteCamel}_macos_${version}_64bit.dmg \
				  VCell_${vcell_siteCamel}_macos_latest_64bit.dmg
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Macos installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_windows_latest_32bit.exe && \
			ln -s VCell_${vcell_siteCamel}_windows_${version}_32bit.exe \
				  VCell_${vcell_siteCamel}_windows_latest_32bit.exe
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Win32 installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_unix_latest_64bit.sh && \
			ln -s VCell_${vcell_siteCamel}_unix_${version}_64bit.sh \
				  VCell_${vcell_siteCamel}_unix_latest_64bit.sh
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Linux64 installer"; exit 1; fi

			popd
		fi
	fi
fi


echo "exited normally"

exit 0


