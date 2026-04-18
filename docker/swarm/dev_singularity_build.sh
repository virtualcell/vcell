#!/usr/bin/env bash

#
# Developer fallback: build vcell-opt and vcell-batch SIF images locally.
#
# This is for dev/test clusters where the CI-built SIFs are not available.
# For production, SIFs are built in CI and pre-pulled by a post-deploy
# Kubernetes Job. See docs/apptainer-image-build.md for details.
#
# Usage:
#   ./dev_singularity_build.sh <tag> [output_dir]
#
# Example:
#   ./dev_singularity_build.sh 7.7.0.71 /share/apps/vcell3/singularityImages
#

set -eux

TAG=${1:?Usage: $0 <tag> [output_dir]}
OUTPUT_DIR=${2:-$(pwd)}

module load singularity

# Uncomment and fill in to authenticate to ghcr.io if images are private:
#singularity remote login -u <github_user> -p <github_token> docker://ghcr.io

for IMG in vcell-opt vcell-batch; do
  DOCKER_IMAGE=ghcr.io/virtualcell/${IMG}:${TAG}
  ORAS_NAME=ghcr.io/virtualcell/${IMG}_singularity:${TAG}
  SIF_FILE=${OUTPUT_DIR}/$(echo "${ORAS_NAME}" | tr '/:' '__').img
  echo "Building ${SIF_FILE} from ${DOCKER_IMAGE}"
  singularity build --force "${SIF_FILE}" "docker://${DOCKER_IMAGE}"
done
