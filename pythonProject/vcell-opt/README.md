### Build and install singularity container on cluster (for testing)

on local machine
```bash
docker build -t ghcr.io/virtualcell/vcell-opt:dev
docker push ghcr.io/virtualcell/vcell-opt:dev
```

on cluster as user vcell
```bash
export docker_image=ghcr.io/virtualcell/vcell-opt:dev
export singularity_file=ghcr_io_virtualcell_vcell_opt_dev.img
module load singularity
singularity remote login -u <github_user> -p <github_token> docker://ghcr.io
singularity build --force ${singularity_file} docker://${docker_image}
cp ghcr_io_virtualcell_vcell_opt_dev.img /state/partition1/singularityImages/
```