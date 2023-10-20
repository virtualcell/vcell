## vcell-batch: a standalone container to run vcell commands outside of container orchestration (e.g. docker swarm, K8s, docker-compose)

### with Docker

ghcr.io/virtualcell/vcell-batch is built within the GitHub workflow of the github.com/virtualcell/vcell repo.

```bash
docker run -v /path/to/data:/simdata ghcr.io/virtualcell/vcell-batch:latest <COMMAND>
```

### with Singularity 2.4

vcell-batch is not yet integrated with Singularity Hub, but can "bootstrap" from a docker image.

```bash
singularity build /path/to/image/vcell-batch.img docker://ghcr.io/virtualcell/vcell-batch:latest
singularity run --bind /path/to/data:/simdata /path/to/image/vcell-batch.img <COMMAND>
```
or
```bash
singularity run --bind /path/to/data:/simdata docker://ghcr.io/virtualcell/vcell-batch:latest <COMMAND>
```

