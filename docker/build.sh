#!/usr/bin/env bash

skip_push=false
skip_maven=false
mvn_repo=$HOME/.m2

show_help() {
	echo "usage: build.sh [OPTIONS] target repo tag"
	echo "  ARGUMENTS"
	echo "    target                ( batch | api | master | mongo | clientgen | all)"
	echo "    repo                  ( schaff | localhost:5000 | vcell-docker.cam.uchc.edu:5000 )"
	echo "    tag                   ( dev | 7.0.0-alpha-new | f98dfe3) last option for git commit hash"
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
	echo "    --skip-maven          skip vcell software build prior to building containers"
	echo "    --skip-push           skip pushing containers to repository"
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
	docker build -f Dockerfile-api-dev --tag $repo/vcell-api:$tag ..
	if [ "$skip_push" == "false" ]; then
		docker push $repo/vcell-api:$tag
	fi
}

build_batch() {
	echo "building $repo/vcell-batch:$tag"
	docker build -f Dockerfile-batch-dev --tag $repo/vcell-batch:$tag ..
	if [ "$skip_push" == "false" ]; then
		docker push $repo/vcell-batch:$tag
	fi
}

build_clientgen() {
	echo "building $repo/vcell-clientgen:$tag"
	docker build -f Dockerfile-clientgen-dev --tag $repo/vcell-clientgen:$tag ..
	if [ "$skip_push" == "false" ]; then
		docker push $repo/vcell-clientgen:$tag
	fi
}

build_master() {
	echo "building $repo/vcell-master:$tag"
	docker build -f Dockerfile-master-dev --tag $repo/vcell-master:$tag ..
	if [ "$skip_push" == "false" ]; then
		docker push $repo/vcell-master:$tag
	fi
}

build_mongo() {
	echo "building $repo/vcell-mongo:$tag"
	docker build -f mongo/Dockerfile --tag $repo/vcell-mongo:$tag mongo
	if [ "$skip_push" == "false" ]; then
		docker push $repo/vcell-mongo:$tag
	fi
}

shift

if [ "$skip_maven" == "false" ]; then
	pushd ..
	mvn -Dmaven.repo.local=$mvn_repo clean install dependency:copy-dependencies
	popd
fi

case $target in
	batch)
		build_batch
		exit $?
		;;
	api)
		build_api
		exit $?
		;;
	master)
		build_master
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
		build_batch && build_api && build_master && build_clientgen && build_mongo
		exit $?
		;;
	*)
		printf 'ERROR: Unknown target: %s\n' "$target" >&2
		echo ""
		show_help
		exit 1
		;;
esac




