## vcell-batch: a standalone container to run vcell commands outside of container orchestration (e.g. docker swarm, K8s, docker-compose)

### with Docker

schaff/vcell-batch is built with Dockerhub (https://hub.docker.com/r/schaff/vcell-batch/).

```bash
docker run -v /path/to/data:/simdata schaff/vcell-batch:remoting <COMMAND>
```


### with Singularity 2.3

vcell-batch is not yet integrated with Singularity Hub, but can "bootstrap" from a docker image.

```bash
singularity create /path/to/image/vcell-batch.img
singularity import /path/to/image/vcell-batch.img docker://schaff/vcell-batch:remoting
singularity run --bind /path/to/data:/simdata /path/to/image/vcell-batch.img <COMMAND>
```


### with Singularity 2.4

vcell-batch is not yet integrated with Singularity Hub, but can "bootstrap" from a docker image.

```bash
singularity build /path/to/image/vcell-batch.img docker://schaff/vcell-batch:remoting
singularity run --bind /path/to/data:/simdata /path/to/image/vcell-batch.img <COMMAND>
```
or
```bash
singularity run --bind /path/to/data:/simdata docker://schaff/vcell-batch:remoting <COMMAND>
```

