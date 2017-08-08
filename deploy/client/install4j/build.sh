#!/usr/bin/env bash

jredir="$HOME/vcelljres"

if [ ! -d "$jredir" ]; then
	echo "expecting to find directory $jredir with downloaded JREs compatible with Install4J configuration"
	exit -1
fi

echo "starting Vagrant box to run Install4J to target all platforms"
vagrant up

echo "invoking script on vagrant box to build installers"
vagrant ssh -c /vagrant/build_installers.sh
buildretcode=$?

echo "shutting down vagrant"
vagrant halt

#
# get the return code from the script
#
exit $buildretcode
