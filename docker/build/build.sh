#!/usr/bin/env bash

shopt -s -o nounset

ssh_user=$(whoami)
ssh_key=
skip_push=false
skip_maven=false
SUDO_CMD=sudo
mvn_repo=$HOME/.m2

show_help() {
	echo "usage: build.sh [OPTIONS] target repo tag"
	echo "  ARGUMENTS"
	echo "    target                ( batch | api | rest | webapp | db | sched | submit | data | mongo | clientgen | opt | appservices | admin | all)"
	echo "                              where appservices = (api, rest, webapp, db, sched, submit, data)"
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
	echo "    --skip-maven          skip vcell software build prior to building containers"
	echo ""
	echo "    --skip-push           skip pushing containers to repository"
	echo ""
	echo "    --skip-sudo           skip sudo for docker commands"
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
		--skip-sudo)
			SUDO_CMD=
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
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-api-dev --tag $repo/vcell-api:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-api-dev --tag $repo/vcell-api:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-api:$tag
	fi
}


build_rest() {
	echo "building $repo/vcell-rest:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../vcell-rest/src/main/docker/Dockerfile.jvm --tag $repo/vcell-rest:$tag ../../vcell-rest"
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../vcell-rest/src/main/docker/Dockerfile.jvm --tag $repo/vcell-rest:$tag ../../vcell-rest
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-rest:$tag
	fi
}


build_webapp_common() {
  config=$1
  echo "building $repo/vcell-webapp-${config}:$tag"
  echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../webapp-ng/Dockerfile-webapp-${config} --tag $repo/vcell-webapp-${config}:$tag ../../webapp-ng"
  $SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../webapp-ng/Dockerfile-webapp-${config} --tag $repo/vcell-webapp-${config}:$tag ../../webapp-ng
  if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
  if [ "$skip_push" == "false" ]; then
    $SUDO_CMD docker push $repo/vcell-webapp-${config}:$tag
  fi
}

build_webapp() {
  build_webapp_common dev
  if [[ $? -ne 0 ]]; then echo "failed to build dev"; exit 1; fi
  build_webapp_common stage
  if [[ $? -ne 0 ]]; then echo "failed to build stage"; exit 1; fi
  build_webapp_common prod
  if [[ $? -ne 0 ]]; then echo "failed to build prod"; exit 1; fi
  build_webapp_common island
  if [[ $? -ne 0 ]]; then echo "failed to build island"; exit 1; fi
}

build_batch() {
	echo "building $repo/vcell-batch:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-batch-dev --tag $repo/vcell-batch:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-batch-dev --tag $repo/vcell-batch:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-batch:$tag
	fi
}

build_clientgen() {
	echo "building $repo/vcell-clientgen:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-clientgen-dev --tag $repo/vcell-clientgen:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-clientgen-dev --tag $repo/vcell-clientgen:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-clientgen:$tag
	fi
}

build_db() {
	echo "building $repo/vcell-db:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-db-dev --tag $repo/vcell-db:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-db-dev --tag $repo/vcell-db:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-db:$tag
	fi
}

build_sched() {
	echo "building $repo/vcell-sched:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-sched-dev --tag $repo/vcell-sched:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-sched-dev --tag $repo/vcell-sched:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-sched:$tag
	fi
}

build_submit() {
	echo "building $repo/vcell-submit:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-submit-dev --tag $repo/vcell-submit:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-submit-dev --tag $repo/vcell-submit:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-submit:$tag
	fi
}

build_data() {
	echo "building $repo/vcell-data:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-data-dev --tag $repo/vcell-data:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-data-dev --tag $repo/vcell-data:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-data:$tag
	fi
}

build_admin() {
	echo "building $repo/vcell-admin:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-admin-dev --tag $repo/vcell-admin:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-admin-dev --tag $repo/vcell-admin:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-admin:$tag
	fi
}


build_opt() {
	echo "building $repo/vcell-opt:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../pythonCopasiOpt/Dockerfile --tag $repo/vcell-opt:$tag ../../pythonCopasiOpt"
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../pythonCopasiOpt/Dockerfile --tag $repo/vcell-opt:$tag ../../pythonCopasiOpt
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-opt:$tag
	fi
}

build_mongo() {
	echo "building $repo/vcell-mongo:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f mongo/Dockerfile --tag $repo/vcell-mongo:$tag mongo"
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f mongo/Dockerfile --tag $repo/vcell-mongo:$tag mongo
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-mongo:$tag
	fi
}



shift

if [ "$skip_maven" == "false" ]; then
	pushd ../..
	mvn --batch-mode -Dmaven.repo.local=$mvn_repo clean install dependency:copy-dependencies -DskipTests=true
	popd
fi

case $target in
	batch)
		build_batch
		exit $?
		;;
	opt)
		build_opt
		exit $?
		;;
	api)
		build_api
		exit $?
		;;
	rest)
		build_rest
		exit $?
		;;
	webapp)
		build_webapp
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
	admin)
		build_admin
		exit $?
		;;
	all)
#		build_api && build_rest && build_db && build_sched && build_submit && build_data && build_mongo && build_batch && build_opt && build_clientgen && build_admin
		build_api && build_rest && build_webapp && build_db && build_sched && build_submit && build_data && build_mongo && build_batch && build_opt && build_clientgen && build_admin
		exit $?
		;;
	appservices)
		build_api && build_rest && build_webapp && build_db && build_sched && build_submit && build_data && build_mongo
		exit $?
		;;
	*)
		printf 'ERROR: Unknown target: %s\n' "$target" >&2
		echo ""
		show_help
		exit 1
		;;
esac





