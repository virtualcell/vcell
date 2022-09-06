#!/usr/bin/env bash

show_help() {
	echo "usage: deploy-db-secret.sh db_user db_token"
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

db_user=$1
db_pswd=$2

kubectl delete secret database-creds

kubectl create secret generic database-creds --type=Opaque \
  --from-literal=database-user=${db_user} \
  --from-literal=database-password=${db_pswd}


