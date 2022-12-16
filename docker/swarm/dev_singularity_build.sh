#!/usr/bin/env bash

#
# build vcell-opt and vcell-batch singularity images as user vcell on xanadu-76 (or equiv)
#

#
# load singularity (latest is probably okay)
#
module load singularity

#
# perform singularity remote login if needed to authenticate to ghcr.io
# uncomment and add auth info
#
#singularity remote login -u <github_user> -p <github_token> docker://ghcr.io


#
# build vcell-opt singularity container
#
export docker_image=ghcr.io/virtualcell/vcell-opt:test
export singularity_file=ghcr.io_virtualcell_vcell-opt_test.img
singularity build --force ${singularity_file} docker://${docker_image}
cp ${singularity_file} /state/partition1/singularityImages/

#
# build vcell-batch singularity container
#
export docker_image=ghcr.io/virtualcell/vcell-batch:test
export singularity_file=ghcr.io_virtualcell_vcell-batch_test.img
singularity build --force ${singularity_file} docker://${docker_image}
cp ${singularity_file} /state/partition1/singularityImages/

