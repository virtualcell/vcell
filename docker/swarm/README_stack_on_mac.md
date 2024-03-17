1. Build Java and Python

```bash
pushd ../../
mvn clean install dependency:copy-dependencies -DskipTests=true
popd
```
```bash
pushd ../../pythonCopasiOpt/vcell-opt
poetry install
popd

pushd ../swarm/vcell-admin
poetry install
popd

pushd ../../pythonVtk
poetry install
popd

pushd ../../vcell-cli-utils
poetry install
popd

```

2. Build and push Docker and Singularity containers (without building clients)

```bash
pushd ../build
./build.sh --skip-maven --skip-singularity --skip-sudo all ghcr.io/virtualcell dev
popd
```

on cluster (e.g. xanadu-76) as user vcell build vcell-opt singularity container
```bash
export docker_image=ghcr.io/virtualcell/vcell-opt:dev
export singularity_file=ghcr.io_virtualcell_vcell-opt_dev.img
module load singularity
# singularity remote login -u <github_user> -p <github_token> docker://ghcr.io
singularity build --force ${singularity_file} docker://${docker_image}
cp ${singularity_file} /state/partition1/singularityImages/
cp ${singularity_file} /share/apps/vcell3/singularityImages/

```

also, make sure vcell-batch container is current.
1. build vcell-batch container as usual
2. create vcell-batch singularity image on server

   on cluster (e.g. xanadu-76) as user vcell build vcell-batch singularity container

    ```bash
    export docker_image=ghcr.io/virtualcell/vcell-batch:dev
    export singularity_file=ghcr.io_virtualcell_vcell-batch_dev.img
    module load singularity
    #singularity remote login -u <github_user> -p <github_token> docker://ghcr.io
    singularity build --force ${singularity_file} docker://${docker_image}
    cp ${singularity_file} /state/partition1/singularityImages/
    cp ${singularity_file} /share/apps/vcell3/singularityImages/
   
    ```


3. Update local configuration to run locally (stored in <vcell>/docker_env.txt)

```bash
./localconfig_realslurm_postgres.sh TEST ghcr.io/virtualcell dev 7.5.0 1234 ../../docker_env_postgres.txt
./localconfig_realslurm_oracle.sh TEST ghcr.io/virtualcell dev 7.5.0 1234 ../../docker_env_oracle.txt
```

5. run local middleware services
   1. Postgres on 0.0.0.0 (not just loopback adapter)
   2. run local MongoDB on 0.0.0.0 (not just loopback adapter)

6. make sure ActiveMQ is still running on UCHC cluster according to config in [docker-compose.yml](./docker-compose.yml) 
    * for example:
       * VCELL_JMS_SIM_HOST_EXTERNAL=vcell-node3.cam.uchc.edu
       * VCELL_JMS_SIM_PORT_EXTERNAL=61619
       * VCELL_JMS_SIM_RESTPORT_EXTERNAL=8164
    * this is needed so that batch and opt jobs running on Slurm can send status, and listen for kills.


5. execute docker compose using [docker-compose.yml](./docker-compose.yml) 
   with environment variable configuration in [docker_env_postgres.txt](../../docker_env_postgres.txt) or [docker_env_oracle.txt](../../docker_env_oracle.txt) in IDE (intellij)


6. execute local VCell GUI (VCellClientMain) pointing to local backend (vcell-api port as defined in [docker-compose.yml](./docker-compose.yml))
