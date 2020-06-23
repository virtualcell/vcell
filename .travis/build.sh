#!/bin/bash

# export TRAVIS_TAG=${TRAVIS_TAG:-$(date +'%Y%m%d%H%M%S')-$(git log --format=%h -1)}
# git tag $TRAVIS_TAG

if deploy
then

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:$tag_name . 
        docker push crbm/biosimulations_vcell_api:$tag_name

else

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:latest .

fi
