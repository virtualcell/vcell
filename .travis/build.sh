#!/bin/bash

if $TRAVIS_TAG
then

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:"$TRAVIS_TAG" . 
        docker push crbm/biosimulations_vcell_api:"$TRAVIS_TAG"

else

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:latest .

fi
