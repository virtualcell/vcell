#!/usr/bin/env bash

show_help() {
	echo "usage: rebuild.sh target repo tag"
	echo "  ARGUMENTS"
	echo "    target                ( batch | api | master | mongo | clientgen | all)"
	echo "    repo                  ( schaff | localhost:5000 | vcell-docker.cam.uchc.edu:5000 )"
	echo "    tag                   ( dev | 7.0.0-alpha-new )"
	exit 1
}

if [[ $# -ne 3 ]] ; then
    show_help
    exit 1
fi

target=$1
repo=$2
tag=$3

build_api() {
	echo "building $repo/vcell-api:$tag"
	docker build -f Dockerfile-api-dev --tag $repo/vcell-api:$tag ..
	docker push $repo/vcell-api:$tag
}

build_batch() {
	echo "building $repo/vcell-batch:$tag"
	docker build -f Dockerfile-batch-dev --tag $repo/vcell-batch:$tag ..
	docker push $repo/vcell-batch:$tag
}

build_clientgen() {
	echo "building $repo/vcell-clientgen:$tag"
	docker build -f Dockerfile-clientgen-dev --tag $repo/vcell-clientgen:$tag ..
	docker push $repo/vcell-clientgen:$tag
}

build_master() {
	echo "building $repo/vcell-master:$tag"
	docker build -f Dockerfile-master-dev --tag $repo/vcell-master:$tag ..
	docker push $repo/vcell-master:$tag
}

build_mongo() {
	echo "building $repo/vcell-mongo:$tag"
	docker build -f mongo/Dockerfile --tag $repo/vcell-mongo:$tag mongo
	docker push $repo/vcell-mongo:$tag
}

shift

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




