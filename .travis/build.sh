#!/bin/bash

if tag_name
then

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:tag_name . 
        docker push crbm/biosimulations_vcell_api:tag_name

else

        docker build --file docker/build/Dockerfile-sedml-solver --tag crbm/biosimulations_vcell_api:latest .

fi
