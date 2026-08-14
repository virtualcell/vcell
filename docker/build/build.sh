#!/usr/bin/env bash

shopt -s -o nounset

ssh_user=$(whoami)
ssh_key=
skip_push=false
skip_maven=false
SUDO_CMD=sudo

show_help() {
	echo "usage: build.sh [OPTIONS] target repo tag"
	echo "  ARGUMENTS"
	echo "    target                ( batch | service | rest | webapp | mongo | clientgen | opt | appservices | admin | all)"
	echo "                              where service = api, data, db, sched and submit in one image"
	echo "                              and appservices = (service, rest, webapp, mongo)"
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

# One image serves api, data, db, sched and submit; the container's first argument picks
# which. It replaced five near-identical Dockerfiles -- see Dockerfile-service-dev.
build_service() {
	echo "building $repo/vcell-service:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-service-dev --tag $repo/vcell-service:$tag ../.."
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f Dockerfile-service-dev --tag $repo/vcell-service:$tag ../..
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-service:$tag
	fi
}


build_rest() {
	echo "building $repo/vcell-rest:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../vcell-rest/src/main/docker/Dockerfile.jvm --tag $repo/vcell-rest:$tag ../../vcell-rest"
	mvn clean install -DskipTests -Dvcell.exporter=false -f ../../vcell-rest/pom.xml
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../vcell-rest/src/main/docker/Dockerfile.jvm --tag $repo/vcell-rest:$tag ../../vcell-rest
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-rest:$tag
	fi
}

build_exporter() {
	echo "building $repo/vcell-exporter:$tag"
	echo "$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../vcell-rest/src/main/docker/Dockerfile.jvm --tag $repo/vcell-exporter:$tag ../../vcell-rest"
	mvn clean install -DskipTests -Dvcell.exporter=true -f ../../vcell-rest/pom.xml
	$SUDO_CMD docker buildx build --platform=linux/amd64 -f ../../vcell-rest/src/main/docker/Dockerfile.jvm --tag $repo/vcell-exporter:$tag ../../vcell-rest
	if [[ $? -ne 0 ]]; then echo "docker buildx build --platform=linux/amd64 failed"; exit 1; fi
	if [ "$skip_push" == "false" ]; then
		$SUDO_CMD docker push $repo/vcell-exporter:$tag
	fi
}


build_webapp_common() {
  config=$1
  export BUILD_COMMAND="build_$1"
  echo "building $repo/vcell-webapp-${config}:$tag"
  echo "$SUDO_CMD docker buildx build --build-arg BUILD_COMMAND=build_$1 --platform=linux/amd64 -f ../../webapp-ng/Dockerfile-webapp --tag $repo/vcell-webapp-${config}:$tag ../../webapp-ng"
  $SUDO_CMD docker buildx build --build-arg BUILD_COMMAND=build_$1 --platform=linux/amd64 -f ../../webapp-ng/Dockerfile-webapp --tag $repo/vcell-webapp-${config}:$tag ../../webapp-ng
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
  build_webapp_common remote
  if [[ $? -ne 0 ]]; then echo "failed to build remote"; exit 1; fi
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
	mvn --batch-mode clean install dependency:copy-dependencies -DskipTests=true
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
	service)
		build_service
		exit $?
		;;
	rest)
		build_rest
		build_exporter
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
		build_service && build_rest && build_exporter && build_webapp && build_mongo && build_batch && build_opt && build_clientgen && build_admin
		exit $?
		;;
	appservices)
		build_service && build_rest && build_exporter && build_webapp && build_mongo
		exit $?
		;;
	*)
		printf 'ERROR: Unknown target: %s\n' "$target" >&2
		echo ""
		show_help
		exit 1
		;;
esac





