#!/usr/bin/env bash

#
# build.sh is to be run in the Vagrant guest OS.
# and refers to directories which are mapped to the Vagrant Guest OS
# as specified in the Vagrantfile.
#
# /vagrant - this directory (with the Vagrantfile) ... mapped by default
# /vagrant_maven - directory of VCell project sources (maven root)
# /vagrant_keys - directory holding install4j.include (secrets file) and keystores
# /vagrant_jres - directory holding install4j jre bundles
#

#
# Vagrantfile maps default location of directory /vagrant_keys to ~/vcellkeys 
# see 
#   config.vm.synced_folder "~/vcellkeys", "/vagrant_keys"
#
# must provide a private install file /vagrant_keys/install4j.include containing:
#    winKeystorePassword=<MY_WIN_KEYSTORE_PASSWORD>
#    macKeystorePassword=<MY_MAC_KEYSTORE_PASSWORD>
#    licenseKey=123456789
#    windowsKeystoreFilePath=/vagrant_keys/path/MY_WINOWS_KEYSTORE.pfx
#    macKeystoreFilePath=/vagrant_keys/path/MY_MAC_KEYSTORE.p12
#
. /vagrant_keys/install4j.include

. /vagrant_maven/target/install4j-working/DeploySettings.include

shopt -s -o nounset

cd /vagrant_maven

mkdir -p /home/vagrant/.install4j6/jres
cp /vagrant_jres/* /home/vagrant/.install4j6/jres/

/home/vagrant/install4j6/bin/install4jc -L $licenseKey
/home/vagrant/install4j6/bin/install4jc \
	--win-keystore-password=$winKeystorePassword \
	--mac-keystore-password=$macKeystorePassword \
	-D \
macKeystore=$compiler_macKeystore,\
winKeystore=$compiler_winKeystore,\
applicationId=$compiler_applicationId,\
softwareVersionString=$compiler_softwareVersionString,\
Site=$compiler_Site,\
vcellVersion=$compiler_vcellVersion,\
vcellBuild=$compiler_vcellBuild,\
vcellAllJarFileSourcePath=$compiler_vcellAllJarFileSourcePath,\
rmiHosts=$compiler_rmiHosts,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL\
	/vagrant/VCell.install4j

