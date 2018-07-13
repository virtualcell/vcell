#!/usr/bin/env bash

show_help() {
	echo "*************************************************"
	echo "** Virtual Cell command line tools and solvers **"
	echo "*************************************************"
	echo ""
	echo "usage: vcellNagios [OPTIONS] host port checkType"
	echo ""
	echo "  Arguments:"
	echo "    host                  hostname of vcellapi"
	echo "    port                  port of vcellapi (different for rel/beta/alpha...)"
	echo "    type                  type of check (sim|login)"
	echo ""
	echo "  [OPTIONS]"
	echo "    -h | --help           show this message"
	echo "    -d | --debug          verbose logging to debug"
	exit 1
}

if [ "$#" -lt 3 ]; then
    show_help
fi

debug=false
while :; do
	case $1 in
		-h|--help)
			show_help
			exit
			;;
		-d|--debug)
			debug=true
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

if [ "$#" -ne 3 ]; then
    show_help
fi

#
# {
#  "nagiosStatusName": "Critical",
#  "nagiosStatusCode": 2,
#  "message": "simulation failed (616): simulation took longer than 120000 to complete",
#  "elapsedTime_MS": 123957
# }
#
host=$1
port=$2
checkType=$3
if [ "$debug" == "true" ]; then
	curlcmd="curl"
else
	curlcmd="curl -s"
fi

simStatus=`$curlcmd https://$host:$port/health?check=$checkType`
if [[ $? -ne 0 ]]; then
	echo "failed to contact server"
	exit 3
fi

if [ "$debug" == "true" ]; then
	echo "raw: $simStatus"
	echo "pretty: $(jq '.' <<< $simStatus)"
fi

statusCode=$(echo $simStatus | jq '.nagiosStatusCode')
statusName=$(echo $simStatus | jq '.nagiosStatusName' | tr -d '"' )
elapsedTime=$(echo $simStatus | jq '.elapsedTime_MS')
message=$(echo $simStatus | jq '.message' | tr -d '"' )

if [ "$debug" == "true" ]; then
	echo "status code is $statusCode"
	echo "status name is $statusName"
	echo "elapsed time is $elapsedTime"
	echo "message is $message"
fi

if [ "$elapsedTime" == "null" ]; then
	elpasedTime=""
fi
if [ "$message" == "null" ]; then
	message="no message"
fi


echo "status=${statusName}(${statusCode}), elapsed time='$elapsedTime', message='$message'"
exit $statusCode
