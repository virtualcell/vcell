#!/usr/bin/env bash

docker run -it -v $PWD:/vcelldata --rm ghcr.io/virtualcell/biosimulators_vcell:dev $COMMAND $@