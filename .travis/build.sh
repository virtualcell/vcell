#!/bin/bash

if $TRAVIS_TAG
then

        docker build -f docker/build/Dockerfile-sedml-solver -t crbm/biosimulations_vcell_api:"$TRAVIS_TAG" . 
        docker push crbm/biosimulations_vcell_api:"$TRAVIS_TAG"

else

        docker build -f docker/build/Dockerfile-sedml-solver -t crbm/biosimulations_vcell_api:latest .

fi
