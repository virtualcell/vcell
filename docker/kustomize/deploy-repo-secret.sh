#!/usr/bin/env bash

show_help() {
	echo "usage: deploy-repo-secret.sh repo_user repo_token"
	exit 1
}

if [[ $# -lt 2 ]]; then
    show_help
fi

while :; do
	case $1 in
		-h|--help)
			show_help
			exit
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

repo_user=$1
repo_pswd=$2

AUTH=$(echo -n ${repo_user}:${repo_pswd} | base64)

kubectl delete secret dockerconfigjson-github-com

echo "{\"auths\":{\"ghcr.io\":{\"auth\":\"${AUTH}\"}}}" | kubectl create secret generic \
 dockerconfigjson-github-com --type=kubernetes.io/dockerconfigjson --from-file=.dockerconfigjson=/dev/stdin


