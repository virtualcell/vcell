#!/bin/bash

export TRAVIS_TAG=${TRAVIS_TAG:-$(date +'%Y%m%d%H%M%S')-$(git log --format=%h -1)}
git tag $TRAVIS_TAG

if $TRAVIS_TAG
then

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:$TRAVIS_TAG . 
        docker push crbm/biosimulations_vcell_api:$TRAVIS_TAG

else

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:latest .

fi
