#!/usr/bin/env bash

DIR=${0%/*}

cd $DIR
#
# get win64 solver binaries from url stored in win64/url.txt (assumes single archive that is packaged as tar.gz)
#
if [ -s "${DIR}/win64/url.txt" ]; then
	win64_url=$(head -n 1 "${DIR}/win64/url.txt")
	echo "curl -L -o win64.tgz $win64_url"
	curl -L -o win64.tgz $win64_url
	if [ $? -eq 0 ]; then
		pushd win64
		tar xzvf ../win64.tgz
		popd
		rm win64.tgz
	fi
fi

#
# get win32 solver binaries from url stored in win32/url.txt (assumes single archive that is packaged as tar.gz)
#
if [ -s "${DIR}/win32/url.txt" ]; then
	win32_url=$(head -n 1 "${DIR}/win32/url.txt")
	echo "curl -L -o win32.tgz $win32_url"
	curl -L -o win32.tgz $win32_url
	if [ $? -eq 0 ]; then
		pushd win32
		tar xzvf ../win32.tgz
		popd
		rm win32.tgz
	fi
fi

#
# get macos (64-bit) solver binaries from url stored in mac64/url.txt (assumes single archive that is packaged as tar.gz)
#
if [ -s "${DIR}/mac64/url.txt" ]; then
	mac64_url=$(head -n 1 "${DIR}/mac64/url.txt")
	echo "curl -L -o mac64.tgz $mac64_url"
	curl -L -o mac64.tgz $mac64_url
	if [ $? -eq 0 ]; then
		pushd mac64
		tar xzvf ../mac64.tgz
		popd
		rm mac64.tgz
	fi
fi

#
# get linux64 solver binaries from url stored in linux64/url.txt (assumes single archive that is packaged as tar.gz)
# note that these solvers are the full server-side binaries with the ActiveMQ libraries linked for messaging.
#
if [ -s "${DIR}/linux64/url.txt" ]; then
	linux64_url=$(head -n 1 "${DIR}/linux64/url.txt")
	echo "curl -L -o linux64.tgz $linux64_url"
	curl -L -o linux64.tgz $linux64_url
	if [ $? -eq 0 ]; then
		pushd linux64
		tar xzvf ../linux64.tgz
		popd
		rm linux64.tgz
	fi
fi

#
# get linux32 solver binaries from url stored in linux32/url.txt (assumes single archive that is packaged as tar.gz)
#
if [ -s "${DIR}/linux32/url.txt" ]; then
	linux32_url=$(head -n 1 "${DIR}/linux32/url.txt")
	echo "curl -L -o linux32.tgz $linux32_url"
	curl -L -o linux32.tgz $linux32_url
	if [ $? -eq 0 ]; then
		pushd linux32
		tar xzvf ../linux32.tgz
		popd
		rm linux32.tgz
	fi
fi


