---
name: build
description: Build VCell from source the way CI does — the Python packages, the Maven reactor, and any of the Docker images (service, rest, exporter, batch, webapp, opt, clientgen, admin) — and check what a built image contains. Use when asked to "build vcell", build or test a module, build or rebuild a Docker image locally, test a Dockerfile change, or when a build or test fails in a fresh checkout or worktree.
---

# Building VCell

**`docs/BUILDING.md` is the reference for the source build; `.github/workflows/CI-full.yml`
(images) and `.github/workflows/ci.yml` (source + tests) are authoritative.** If this skill and
CI disagree, CI is right — re-read the workflow, then fix this file.

## 1. Source build (every checkout and every worktree)

Two steps, in this order. Each `git worktree` needs both again: Poetry environments, `target/`
and `localsolvers/` are per worktree; only `~/.m2` is shared.

```bash
# Python first — Fast tests shell out to these Poetry environments at run time
pip install -r requirements.txt
for p in vcell-cli-utils docker/swarm/vcell-admin pythonCopasiOpt/vcell-opt \
         pythonVtk python-utils python-restclient pythonData ; do
  ( cd "$p" && poetry env use 3.10 && poetry install ) || { echo "FAILED: $p"; break; }
done

# Then the whole reactor. dependency:copy-dependencies is required: it fills
# <module>/target/maven-jars/, which vcell.sh and the Dockerfiles put on the classpath.
mvn --batch-mode clean install dependency:copy-dependencies -DskipTests=true
```

- **Java 17**, as CI and releases. On macOS, if `java`/`javap` fail through a broken jenv shim,
  use `export JAVA_HOME=$(/usr/libexec/java_home -v 17)` and `$JAVA_HOME/bin/java`.
- Python is not needed to *compile* (`-DskipTests`); it is needed for the tests to mean anything.
  Java tests failing on a missing Python module or solver binary mean step 1 was skipped.
- Testing a module whose dependency you changed: always `-am`
  (`mvn test -pl vcell-client -am -Dtest=SomeTest`), or Maven silently uses the jar last
  installed into `~/.m2` — possibly from another worktree.
- `Could not build dependency tree` from the enforcer hides the real error:
  `mvn --batch-mode -X install -DskipTests 2>&1 | grep "Caused by"` (docs/BUILDING.md).

## 2. Docker images

Every image is `linux/amd64`. The recipe per image is the `docker-build` matrix in
`CI-full.yml`; as of this writing:

| Image | Maven before the image | Dockerfile | Context |
|---|---|---|---|
| `vcell-service` (api, data, db, sched, submit) | full reactor (§1) | `docker/build/Dockerfile-service-dev` | `.` |
| `vcell-rest` | full reactor **with `-Dvcell.exporter=false`** | `vcell-rest/src/main/docker/Dockerfile.jvm` | `vcell-rest` |
| `vcell-exporter` | full reactor **with `-Dvcell.exporter=true`** | `vcell-rest/src/main/docker/Dockerfile.jvm` | `vcell-rest` |
| `vcell-batch` | full reactor | `docker/build/Dockerfile-batch-dev` | `.` |
| `vcell-clientgen` | full reactor | `docker/build/Dockerfile-clientgen-dev` | `.` |
| `vcell-admin` | full reactor | `docker/build/Dockerfile-admin-dev` | `.` |
| `vcell-webapp` | none (npm inside the image) | `webapp-ng/Dockerfile-webapp`, `--build-arg BUILD_COMMAND=build_prod` | `webapp-ng` |
| `vcell-opt` | none | `pythonCopasiOpt/Dockerfile` | `pythonCopasiOpt` |

Build one exactly as CI does, from the repo root, with `--load` so it lands in the local
Docker and is **not pushed**:

```bash
docker buildx build --platform=linux/amd64 --load \
  -f docker/build/Dockerfile-service-dev --tag vcell-service:local .
```

### `vcell-rest` and `vcell-exporter` are one Dockerfile, two Maven builds

They differ only in `vcell.exporter`, a Quarkus **build-time** property that selects which export
beans are compiled in (`@IfBuildProperty` in `ExportRequestListenerMQ`, `ServerExportEventController`,
`ExportStatusListener`). The Dockerfile just copies `vcell-rest/target/quarkus-app`, and the
context is `vcell-rest/` only, so:

- run the full reactor build with the flag, then build the image **immediately** — whichever
  Maven run happened last owns `target/quarkus-app`. Building `rest` after an exporter Maven
  build silently ships exporter beans.
- CI does it in one command per image:
  ```bash
  mvn --batch-mode clean install dependency:copy-dependencies -DskipTests -Dvcell.exporter=false
  docker buildx build --platform=linux/amd64 --load \
    -f vcell-rest/src/main/docker/Dockerfile.jvm --tag vcell-rest:local vcell-rest
  ```
  `docker/build/build.sh` instead runs the reactor once, then
  `mvn clean install -DskipTests -Dvcell.exporter=<bool> -f vcell-rest/pom.xml` per image —
  equivalent.

### `docker/build/build.sh` pushes by default

`build.sh <target> <repo> <tag>` runs the Maven build, builds, **pushes to `<repo>`**, and uses
`sudo docker`. For a local build pass `--skip-push --skip-sudo` (and `--skip-maven` if §1 is
current). Release images are built by CI-full from the release tag, never from a laptop.

## 3. Check what an image contains

A Dockerfile change is not verified until the built image is compared with the one it replaces.
The deployed tags per site are in `../vcell-fluxcd/kustomize/overlays/<site>/kustomization.yaml`
(`prod`, `stage`, `dev`).

```bash
img=ghcr.io/virtualcell/vcell-rest:8.3.3.05            # or your :local build
docker run --rm --platform linux/amd64 --entrypoint java "$img" -version
docker run --rm --platform linux/amd64 --entrypoint sh "$img" -c 'id; echo HOME=$HOME PWD=$(pwd)'
docker image inspect "$img" --format '{{.Config.User}} {{.Config.WorkingDir}} {{json .Config.Entrypoint}}'
```

- **Effective JVM flags under a memory limit** — heap sizing differs between launchers (Red Hat's
  `run-java.sh` defaults `MaxRAMPercentage=50`; plain `java` uses 25):
  `docker run --rm --platform linux/amd64 --memory=2g -e JAVA_OPTS_APPEND=-XX:+PrintCommandLineFlags "$img"`
  (images whose entrypoint honours `JAVA_OPTS_APPEND`, i.e. `vcell-rest`/`vcell-exporter`).
- **Startup without a cluster**: `vcell-rest` started bare stops at
  `quarkus.datasource."postgresql".jdbc.url has not been defined`. Old and new images stopping at
  the same line is a meaningful equivalence check; a new error earlier is a regression.
- Base images that float (`eclipse-temurin:17`, `17-jre`) pick up Java patches at image build time;
  a pinned base does not. Check `java -version` in the *deployed* tag, not the Dockerfile.

## 4. Desktop installers (install4j)

Not built locally for releases: CD-sites (`site_deploy.yml`) builds them from the release tag in
the `vcell-clientgen` image, one media per runner. To validate an edit to
`docker/build/installers/VCell.install4j` without the release secrets, dry-run **one media per
invocation** with the local install4j (`/Applications/install4j.app/.../bin/install4jc`):

```bash
install4jc -t -u -j --disable-notarization -b <media id> -D <compiler variables> VCell.install4j
```

`-t` dry run, `-u` no signing, `-j` no JRE download. Supply the `${compiler:...}` variables the
project references with dummy values and a scratch `mavenRootDir`. Building several media in one
dry run fails intermittently with "recursive replacement of variable" even on an unmodified
project; CI never does that. The file is CRLF — edit it byte-preserving.

## Pitfalls

- macOS has no `timeout`; use the tool's own timeout flag (`kubectl --request-timeout`, `curl --max-time`).
- `mvn clean` already deletes `target/`; don't `rm -rf` it first.
- A green CI fast lane can mean "nothing ran" (docs-only change): check the `should-run` job summary.
