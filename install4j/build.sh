#!/usr/bin/env bash

vagrant up
vagrant ssh -c /vagrant/build_installers.sh
vagrant halt
