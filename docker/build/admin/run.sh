#!/bin/bash

cd /share/apps/vcell3/deployed_github/config

show_help() {
  echo "vcell administrative cli - USE THIS POWER WITH CAUTION - for vcell admins only"
  echo "   example commands:"
  echo "      help"
  echo "      simdata-verifier help"
  echo "      simdata-verifier -u boris -o /opt/vcelldata/repair_reports --model-visibility PUBLIC "
  echo "      simdata-verifier -u schaff -o /opt/vcelldata/repair_reports --simulation-id 252697124 --run-never-ran"
  echo "      simdata-verifier -u schaff -o /opt/vcelldata/repair_reports --simulation-id 252697124 --run-never-ran --rerun-lost-data"
	exit 1
}

if [ "$#" -lt 1 ]; then
    show_help
fi

sudo docker pull ghcr.io/virtualcell/vcell-admin:latest

arguments=$*

sudo env $(cat ./server_alpha_7.5.0_1_1fb3b5c.config | xargs) \
  docker run -it --rm \
  --env-file=./server_alpha_7.5.0_1_1fb3b5c.config \
  -v /usr/local/deploy:/run/secrets \
  -v /opt/vcelldata/users:/simdata \
  -v /share/apps/vcell10/users:/simdata_secondary \
  -v /opt/vcelldata/repair_reports:/opt/vcelldata/repair_reports \
  ghcr.io/virtualcell/vcell-admin:latest \
  $arguments
