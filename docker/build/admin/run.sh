#!/bin/bash

sudo docker pull ghcr.io/virtualcell/vcell-admin:latest

cd /share/apps/vcell3/deployed_github/config

echo "example runs"

sudo env $(cat ./server_alpha_7.5.0_1_1fb3b5c.config | xargs) \
  docker run -it --rm \
  --env-file=./server_alpha_7.5.0_1_1fb3b5c.config \
  -v /usr/local/deploy:/run/secrets \
  -v /opt/vcelldata/users:/simdata \
  -v /share/apps/vcell10/users:/simdata_secondary \
  -v /opt/vcelldata/repair_reports:/opt/vcelldata/repair_reports \
  ghcr.io/virtualcell/vcell-admin:latest \
  simdata-verifier -u boris -o /opt/vcelldata/repair_reports --model-visibility PUBLIC


sudo env $(cat ./server_alpha_7.5.0_1_1fb3b5c.config | xargs) \
  docker run -it --rm \
  --env-file=./server_alpha_7.5.0_1_1fb3b5c.config \
  -v /usr/local/deploy:/run/secrets \
  -v /opt/vcelldata/users:/simdata \
  -v /share/apps/vcell10/users:/simdata_secondary \
  -v /opt/vcelldata/repair_reports:/opt/vcelldata/repair_reports \
  ghcr.io/virtualcell/vcell-admin:latest \
  simdata-verifier -u schaff -o /opt/vcelldata/repair_reports --simulation-id 252697124 --run-never-ran

sudo env $(cat ./server_alpha_7.5.0_1_1fb3b5c.config | xargs) \
  docker run -it --rm \
  --env-file=./server_alpha_7.5.0_1_1fb3b5c.config \
  -v /usr/local/deploy:/run/secrets \
  -v /opt/vcelldata/users:/simdata \
  -v /share/apps/vcell10/users:/simdata_secondary \
  -v /opt/vcelldata/repair_reports:/opt/vcelldata/repair_reports \
  ghcr.io/virtualcell/vcell-admin:latest \
  simdata-verifier -u schaff -o /opt/vcelldata/repair_reports --simulation-id 252697124 --run-never-ran --rerun-lost-data

