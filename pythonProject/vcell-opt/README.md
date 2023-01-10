### Build and install singularity container on cluster (for testing)

on local machine
```bash
docker build -t ghcr.io/virtualcell/vcell-opt:dev
docker push ghcr.io/virtualcell/vcell-opt:dev
```

on cluster (e.g. xanadu-76) as user vcell build vcell-opt singularity container
```bash
export docker_image=ghcr.io/virtualcell/vcell-opt:test
export singularity_file=ghcr.io_virtualcell_vcell-opt_test.img
module load singularity
#singularity remote login -u <github_user> -p <github_token> docker://ghcr.io
singularity build --force ${singularity_file} docker://${docker_image}
cp ${singularity_file} /state/partition1/singularityImages/
cp ${singularity_file} /share/apps/vcell3/singularityImages/

```

also, make sure vcell-batch container is current.
1. build vcell-batch container as usual
2. create vcell-batch singularity image on server
   
    on cluster (e.g. xanadu-76) as user vcell build vcell-batch singularity container

    ```bash
    export docker_image=ghcr.io/virtualcell/vcell-batch:test
    export singularity_file=ghcr.io_virtualcell_vcell-batch_test.img
    module load singularity
    #singularity remote login -u <github_user> -p <github_token> docker://ghcr.io
    singularity build --force ${singularity_file} docker://${docker_image}
    cp ${singularity_file} /state/partition1/singularityImages/
    cp ${singularity_file} /share/apps/vcell3/singularityImages/
   
    ```