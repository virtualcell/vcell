#!/usr/bin/env bash

shopt -s -o nounset

ssh_user=$(whoami)
ssh_key=
skip_push=false
skip_maven=false
skip_singularity=false
mvn_repo=$HOME/.m2

show_help() {
	echo "usage: build.sh [OPTIONS] target repo tag"
	echo "  ARGUMENTS"
	echo "    target                ( batch | api | db | sched | submit | data | mongo | clientgen | all)"
	echo ""
	echo "    repo                  ( schaff | localhost:5000 | vcell-docker.cam.uchc.edu:5000 )"
	echo ""
	echo "    tag                   ( dev | 7.0.0-alpha-new | f98dfe3) last option for git commit hash"
	echo ""
	echo "  [OPTIONS]"
	echo ""
	echo "    -h | --help           show this message"
	echo ""
	echo "    --ssh-user user       user for ssh to node [defaults to current user id using whoami]"
	echo "                          (user must have passwordless sudo for docker commands on manager-node)"
	echo ""
	echo "    --ssh-key  keyfile    ssh key for passwordless ssh to node"
	echo ""
	echo "    --skip-singularity    skip build of Singularity image for vcell-batch container (stored in ./singularity/)"
	echo ""
	echo "    --skip-maven          skip vcell software build prior to building containers"
	echo ""
	echo "    --skip-push           skip pushing containers to repository"
	echo ""
	echo "    --mvn-repo REPO_DIR   override local maven repository (defaults to $HOME/.m2)"
	exit 1
}

if [[ $# -lt 3 ]]; then
    show_help
fi

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
		--mvn-repo)
			shift
			mvn_repo=$1
			;;
		--skip-maven)
			skip_maven=true
			;;
		--skip-push)
			skip_push=true
			;;
		--skip-singularity)
			skip_singularity=true
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

if [[ $# -ne 3 ]] ; then
    show_help
fi

target=$1
repo=$2
tag=$3

build_api() {
	echo "building $repo/vcell-api:$tag"
	echo "sudo docker build -f Dockerfile-api-dev --tag $repo/vcell-api:$tag .."
	sudo docker build -f Dockerfile-api-dev --tag $repo/vcell-api:$tag ..
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-api:$tag
	fi
}

build_batch() {
	echo "building $repo/vcell-batch:$tag"
	echo "sudo docker build -f Dockerfile-batch-dev --tag $repo/vcell-batch:$tag .."
	sudo docker build -f Dockerfile-batch-dev --tag $repo/vcell-batch:$tag ..
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-batch:$tag
	fi
}

build_clientgen() {
	echo "building $repo/vcell-clientgen:$tag"
	echo "sudo docker build -f Dockerfile-clientgen-dev --tag $repo/vcell-clientgen:$tag .."
	sudo docker build -f Dockerfile-clientgen-dev --tag $repo/vcell-clientgen:$tag ..
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-clientgen:$tag
	fi
}

# build_master() {
# 	echo "building $repo/vcell-master:$tag"
# 	echo "sudo docker build -f Dockerfile-master-dev --tag $repo/vcell-master:$tag .."
# 	sudo docker build -f Dockerfile-master-dev --tag $repo/vcell-master:$tag ..
# 	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
# 	if [ "$skip_push" == "false" ]; then
# 		sudo docker push $repo/vcell-master:$tag
# 	fi
# }

build_db() {
	echo "building $repo/vcell-db:$tag"
	echo "sudo docker build -f Dockerfile-db-dev --tag $repo/vcell-db:$tag .."
	sudo docker build -f Dockerfile-db-dev --tag $repo/vcell-db:$tag ..
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-db:$tag
	fi
}

build_sched() {
	echo "building $repo/vcell-sched:$tag"
	echo "sudo docker build -f Dockerfile-sched-dev --tag $repo/vcell-sched:$tag .."
	sudo docker build -f Dockerfile-sched-dev --tag $repo/vcell-sched:$tag ..
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-sched:$tag
	fi
}

build_submit() {
	echo "building $repo/vcell-submit:$tag"
	echo "sudo docker build -f Dockerfile-submit-dev --tag $repo/vcell-submit:$tag .."
	sudo docker build -f Dockerfile-submit-dev --tag $repo/vcell-submit:$tag ..
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-submit:$tag
	fi
}

build_data() {
	echo "building $repo/vcell-data:$tag"
	echo "sudo docker build -f Dockerfile-data-dev --tag $repo/vcell-data:$tag .."
	sudo docker build -f Dockerfile-data-dev --tag $repo/vcell-data:$tag ..
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-data:$tag
	fi
}

build_mongo() {
	echo "building $repo/vcell-mongo:$tag"
	echo "sudo docker build -f mongo/Dockerfile --tag $repo/vcell-mongo:$tag mongo"
	sudo docker build -f mongo/Dockerfile --tag $repo/vcell-mongo:$tag mongo
	if [[ $? -ne 0 ]]; then echo "docker build failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		sudo docker push $repo/vcell-mongo:$tag
	fi
}

build_singularity() {
	if [ "$skip_singularity" == "false" ]; then
		if [ -x "$(command -v singularity)" ]; then
			build_singularity_direct
			if [[ $? -ne 0 ]]; then echo "failed to build singularity image using singularity commands"; exit 1; fi
		else 
			if [ -x "$(command -v vagrant)" ]; then
				build_singularity_vagrant
				if [[ $? -ne 0 ]]; then echo "failed to build singularity image using singularity vagrant box"; exit 1; fi
			else
				echo "found neither singularity nor vagrant, cannot build singularity image"
				exit 1
			fi
		fi
	fi
}

build_singularity_direct() {

	echo ""
	cmd="cd singularity-vm"
	cd singularity-vm
	echo ""
	echo "CURRENT DIRECTORY IS $PWD"

	#
	# create temporary Singularity file which imports existing docker image from registry and adds a custom entrypoint
	#
	_vcell_batch_docker_name="${repo}/vcell-batch:${tag}"
	_singularity_image_file="${_vcell_batch_docker_name//[\/:]/_}.img"
	_singularity_file="Singularity_${_vcell_batch_docker_name//[\/:]/_}"

cat <<EOF >$_singularity_file
Bootstrap: docker
From: $_vcell_batch_docker_name

%runscript

    exec /vcellscripts/entrypoint.sh "\$@"

%labels

AUTHOR jcschaff
EOF

	echo ""
	echo "wrote Singularity file $_singularity_file"
	cat $_singularity_file

	#
	# build the singularity image and place in singularity-vm directory
	#
	echo ""
	remote_cmd="sudo singularity build $_singularity_image_file $_singularity_file"
	echo "$remote_cmd"
	($remote_cmd)
	if [[ $? -ne 0 ]]; then echo "failed to build singularity image" && exit 1; fi

	echo ""
	echo "created Singularity image for vcell-bash ./$_singularity_image_file locally (in ./singularity-vm folder), can be pushed to remote server during deploy"
	echo ""
	echo "cd .."
	cd ..
}

build_singularity_vagrant() {
	echo ""
	cmd="cd singularity-vm"
	cd singularity-vm
	echo ""
	echo "CURRENT DIRECTORY IS $PWD"

	#
	# prepare Vagrant Singularity box for building the singularity image (bring up, install cert)
	#
	echo ""
	echo "generating singularity image for vcell-batch and uploading to remote server for HTC cluster"
	cmd="sudo scp $ssh_key vcell@vcell-docker.cam.uchc.edu:/usr/local/deploy/registry_certs/domain.cert ."
	echo $cmd
	($cmd) || (echo "failed to download cert from vcell-docker private Docker registry")

	echo ""
	echo "vagrant up"
	vagrant up
	if [[ $? -ne 0 ]]; then echo "failed to bring vagrant up"; fi

	echo ""
	remote_cmd="sudo cp /vagrant/domain.cert /usr/local/share/ca-certificates/vcell-docker.cam.uchc.edu.crt"
	echo "vagrant ssh -c \"$remote_cmd\""
	vagrant ssh -c "$remote_cmd"
	if [[ $? -ne 0 ]]; then echo "failed to upload domain.cert to trust the private Docker registry" && exit 1; fi

	echo ""
	remote_cmd="sudo update-ca-certificates"
	echo "vagrant ssh -c \"$remote_cmd\""
	vagrant ssh -c "$remote_cmd"
	if [[ $? -ne 0 ]]; then
	    echo "failed to update ca certificates in vagrant box" && exit 1
	fi
	#
	# create temporary Singularity file which imports existing docker image from registry and adds a custom entrypoint
	#
	_vcell_batch_docker_name="${repo}/vcell-batch:${tag}"
	_singularity_image_file="${_vcell_batch_docker_name//[\/:]/_}.img"
	_singularity_file="Singularity_${_vcell_batch_docker_name//[\/:]/_}"

cat <<EOF >$_singularity_file
Bootstrap: docker
From: $_vcell_batch_docker_name

%runscript

    exec /vcellscripts/entrypoint.sh "\$@"

%labels

AUTHOR jcschaff
EOF

	echo ""
	echo "wrote Singularity file $_singularity_file"
	cat $_singularity_file

	#
	# build the singularity image and place in singularity-vm directory
	#
	echo ""
	remote_cmd="sudo singularity build /vagrant/$_singularity_image_file /vagrant/$_singularity_file"
	echo "vagrant ssh -c \"$remote_cmd\""
	vagrant ssh -c "$remote_cmd"
	if [[ $? -ne 0 ]]; then echo "failed to build singularity image from vagrant" && exit 1; fi

	#
	# bring down Vagrant Singularity box
	#
	echo ""
	echo "vagrant halt"
	vagrant halt
	if [[ $? -ne 0 ]]; then echo "failed to stop vagrant box"; fi

	echo ""
	echo "created Singularity image for vcell-bash ./$local_singularity_image_name locally (in ./singularity-vm folder), can be pushed to remote server during deploy"
	echo ""
	echo "cd .."
	cd ..
}


shift

if [ "$skip_maven" == "false" ]; then
	pushd ..
	mvn -Dmaven.repo.local=$mvn_repo clean install dependency:copy-dependencies
	popd
fi

case $target in
	batch)
		build_batch && build_singularity
		exit $?
		;;
	api)
		build_api
		exit $?
		;;
	# master)
	# 	build_master
	# 	exit $?
	# 	;;
	db)
		build_db
		exit $?
		;;
	sched)
		build_sched
		exit $?
		;;
	submit)
		build_submit
		exit $?
		;;
	data)
		build_data
		exit $?
		;;
	mongo)
		build_mongo
		exit $?
		;;
	clientgen)
		build_clientgen
		exit $?
		;;
	all)
		# build_batch && build_api && build_master && build_db && build_sched && build_submit && build_data && build_clientgen && build_mongo && build_singularity
		build_batch && build_api && build_master && build_db && build_sched && build_submit && build_data && build_clientgen && build_mongo && build_singularity
		exit $?
		;;
	*)
		printf 'ERROR: Unknown target: %s\n' "$target" >&2
		echo ""
		show_help
		exit 1
		;;
esac






