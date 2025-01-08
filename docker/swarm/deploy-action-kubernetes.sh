#!/usr/bin/env bash

set -ux

show_help() {
	echo "Deploys vcell client installers for a Kubernetes deploy"
	echo ""
	echo "usage: deploy-action-kubernetes.sh [OPTIONS] REQUIRED-ARGUMENTS"
	echo ""
	echo "  REQUIRED-ARGUMENTS"
	echo "    manager-node          any node for ssh access (e.g. vcellapi.cam.uchc.edu)"
	echo ""
	echo "    local-config-file     local config file for setting environment"
	echo ""
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
	echo ""
	echo "    --ssh-user user       user for ssh to node [defaults to current user id using whoami]"
	echo "                          (user must have passwordless sudo for docker commands on manager-node)"
	echo ""
	echo "    --build-installers    optionally build client installers and place in ./generated_installers dir"
	echo ""
	echo "    --installer-deploy-dir /path/to/installer/dir"
	echo "                          directory for installers accessible to users"
	echo "                          typically a web-accessible location to download the client installers for each platform"
	echo ""
	echo ""
	echo ""
	echo "example:"
	echo ""
	echo "deploy-action-kubernetes.sh \\"
	echo "   --ssh-user vcell \\"
	echo "   --build_installers --installer_deploy_dir /share/apps/vcell3/apache_webroot/htdocs/webstart/Alpha \\"
	echo "   vcellapi.cam.uchc.edu \\"
	echo "   ./server.config"
	exit 1
}

if [[ $# -lt 2 ]]; then
    show_help
fi

ssh_user=$(whoami)
installer_deploy_dir=
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
		--installer-deploy-dir)
			shift
			installer_deploy_dir=$1
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

if [[ $# -ne 2 ]] ; then
    show_help
fi

manager_node=$1
local_config_file=$2

# get settings from config file
vcell_siteCamel=$(grep VCELL_SITE_CAMEL "$local_config_file" | cut -d"=" -f2)
vcell_version=$(grep VCELL_VERSION_NUMBER "$local_config_file" | cut -d"=" -f2)
vcell_build=$(grep VCELL_BUILD_NUMBER "$local_config_file" | cut -d"=" -f2)

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
