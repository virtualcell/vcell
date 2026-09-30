# VCell repository guide

A map of this checkout: what the software is, which pieces live where, how a model
becomes a running simulation, and how to build and change the code. Deeper design
notes live under `docs/`; this file is the orientation layer.

VCell (Virtual Cell, since 1997) is a modeling and simulation platform for cell
biology. Users build biological models, VCell turns them into mathematics, and
native or third-party solvers run those equations — locally on the desktop, or on
a shared cluster. The public site is [vcell.org](https://vcell.org). License is MIT.
Main branch is `master`. Remote is `git@github.com:virtualcell/vcell.git`.

## Tech stack

| Layer | What it is |
|---|---|
| Java | 17, Maven 3.8+, source and reporting encoding UTF-8 |
| Desktop UI | Swing, in `vcell-client` |
| Current HTTP API | Quarkus 3.5.2 (`vcell-rest`), port 9000 in dev |
| Legacy HTTP API | Restlet (`vcell-api`), path prefix `/api/v0/` |
| Auth | OIDC via Auth0 in dev/prod; Keycloak testcontainers in Quarkus tests |
| Databases | Oracle in production; PostgreSQL in dev and tests. MongoDB/GridFS for large message blobs and job logs |
| Messaging | ActiveMQ Classic (legacy destinations) and Apache Artemis (intended destination). See [Messaging](#messaging) |
| Python | 3.10, Poetry 1.2.2+. Seven packages. Java tests and the CLI shell out to them |
| Frontend | Angular 17 in `webapp-ng`, generated TypeScript client |
| Native solvers | Separate repos (historically `virtualcell/vcell-solvers`). Binaries are downloaded into `localsolvers/` at build time; HPC runs them inside Apptainer/Singularity images via SLURM |
| Containers | Root `Dockerfile` (Ubuntu Jammy, Java 17, Python 3.10). Swarm compose under `docker/swarm/`. Kubernetes overlays live in the separate `vcell-fluxcd` repo |
| CI | GitHub Actions: fast lane `ci.yml`, heavy suites `regression.yml`, image push `cd.yml`, CodeQL |

## How a model becomes a simulation

Three layers, and dependencies point only one way. This is the rule that keeps
the domain readable. Full statement: [docs/architecture-layers.md](../docs/architecture-layers.md).

```
biological  --math mapping-->  mathematical  --solver writer-->  solver input
```

| Layer | Packages | Owns |
|---|---|---|
| Biological | `cbit.vcell.model`, `org.vcell.model.rbm`, `cbit.vcell.mapping`, `cbit.vcell.biomodel` | What the user edits: species, reactions, rules, and applications (`SimulationContext`) |
| Mathematical | `cbit.vcell.math` | A `MathDescription`: a complete statement of what is to be solved, with no reference back to the application that produced it |
| Solver | `org.vcell.solver.*`, `cbit.vcell.solver` | Turning that math into one solver's input file, running it, reading the output |

`cbit.vcell.math` must not import the biological packages or the solver packages.
`MathNamespaceSeparationTest` fails the build if it does. Where math needs a
concept that also exists in biology, the type is duplicated under a `Particle*`
name rather than imported. Every scalar in a math description is an `Expression`,
even when generation only ever writes a float literal. The solver evaluates it
and must fail loudly if the value does not resolve to a number.

The user-facing container is a `BioModel` (`cbit.vcell.biomodel.BioModel`):

- a `Model` (physiology: compartments, species, reactions, rule-based molecules)
- one or more `SimulationContext`s, shown in the UI as **Applications** (how that physiology is mapped: compartmental ODE, spatial PDE, stochastic, NFSim, SpringSaLaD, …)
- one or more `Simulation`s (time bounds, solver choice, output options) attached to an application

A `MathModel` is the other document type: mathematics entered directly, without
a biological model in front of it. Both are serialized as VCML (VCell's XML).
Import and export also speak SBML, SED-ML, BNGL, OMEX, and diagram formats.

Supported simulation kinds, from the project README: ODEs, reaction-diffusion
in cellular geometry, Gillespie and hybrid stochastic, particle-based spatial,
network-free (NFSim), and moving-boundary problems.

## How the running system connects

Two user-facing programs share one model database and one compute cluster.

```
desktop client (Swing)  ----\
                              +-->  legacy API  /api/v0/   (vcell-api, Restlet)
Angular webapp              /          |
                              \         |  JMS (VCMessagingService)
                               \        v
                                +-->  services: db, data, sched, submit
                                         |
                                         +--> ActiveMQ Classic (sim/db/data/worker)
                                         +--> Artemis (optimization, export)
                                         |
                                         v
                                      SLURM + Singularity solver images
                                      shared storage  /share/apps/vcell3/...

Angular webapp  -->  current API  /api/v1/   (vcell-rest, Quarkus)
                         |
                         +--> Oracle or PostgreSQL
                         +--> Artemis over AMQP (opt, export)
```

Ingress in Kubernetes sends `/api/v1/` to `vcell-rest` and `/api/v0/` to the
legacy API. The desktop client launched by `./vcell.sh` talks to the API host
in `VCELL_API_HOST` (default `vcell.cam.uchc.edu:443`, production). Dev is
`vcell-dev.cam.uchc.edu:443`.

### Server processes

These are the long-running services in the Swarm stack (`docker/README_serviceInfo.md`).
The class names are the entry points.

| Service | Class | Job |
|---|---|---|
| `api` | `org.vcell.rest.VCellApiMain` | External HTTP for desktop clients. Legacy Restlet app |
| `db` | `cbit.vcell.message.server.db.DatabaseServer` | Database requests from the message bus |
| `data` | `cbit.vcell.message.server.data.SimDataServer` | Simulation result reads and writes |
| `sched` | `cbit.vcell.message.server.dispatcher.SimulationDispatcher` | Queue order and solver status |
| `submit` | `cbit.vcell.message.server.batch.sim.HtcSimulationWorker` | Writes the SLURM script and solver task files, submits the job |
| `vcell-rest` | Quarkus | Current REST API used by the Angular app and generated clients |
| `mongodb` | | GridFS for oversized JMS payloads; job-log store |
| `activemqint` | ActiveMQ Classic | `simReq`, `dataReq`, `dbReq`, `simJob`, topic `clientStatus` |
| `activemqsim` | ActiveMQ Classic | `workerEvent`, topic `serviceControl` |
| `artemismq` | Artemis | `opt-request`, `opt-status`, export and client-status addresses |

A simulation run, compressed:

1. The client saves the model. Versioned rows go through the database layer into Oracle (prod) or PostgreSQL (dev/test).
2. Start simulation posts work onto `simReq`.
3. `SimulationDispatcher` decides what runs and talks to workers on `simJob` / `workerEvent`.
4. `HtcSimulationWorker` writes `simtask.xml` and a `.slurm.sub` script under `/share/apps/vcell3/`, then `sbatch`s it.
5. The HPC node runs a Singularity image (built from the batch/solver images). Solver stdout lands in `htclogs`; results land in `users/{user}`.
6. Status flows back on the worker and client-status topics. The data service serves results to the client.

Local (non-cluster) runs use solver binaries unpacked into `localsolvers/{linux64,mac64,win64}/` during `generate-test-resources`. Those binaries are gitignored and are per worktree.

### Current REST API (`/api/v1/`)

Resources live in `vcell-rest/src/main/java/org/vcell/restq/handlers/`. Each class
is a JAX-RS resource with its path on the class.

| Path | Resource |
|---|---|
| `/api/v1/bioModel` | `BioModelResource` — get, save, VCML/SBML/OMEX/BNGL/diagram download |
| `/api/v1/mathModel` | `MathModelResource` |
| `/api/v1/Simulation` | `SimulationResource` — start, stop, status |
| `/api/v1/geometry` | `GeometryResource` |
| `/api/v1/image` | `VCImageResource` |
| `/api/v1/fieldData` | `FieldDataResource` |
| `/api/v1/export` | `ExportResource` |
| `/api/v1/optimization` | `OptimizationResource` |
| `/api/v1/solver` | `SolverResource` — finite-volume solver input |
| `/api/v1/publications` | `PublicationResource` |
| `/api/v1/users` | `UsersResource` — login mapping, guest tokens, recovery |
| `/api/v1/admin` | `AdminResource` |
| `/api/v1/vcInfoContainer` | `VCInfoContainerResource` |

Config is `vcell-rest/src/main/resources/application.properties`. Dev port is
9000. OpenAPI is served at `/openapi`, Swagger UI at `/q/swagger-ui/`. The
checked-in spec is `tools/openapi.yaml`.

Auth: Auth0 OIDC for `%dev` and prod (`application-type=service`). Tests start
Keycloak and PostgreSQL with Quarkus dev services / testcontainers, so Docker
must be running for `mvn test -pl vcell-rest`.

### Generated clients

`vcell-rest`'s SmallRye OpenAPI output is the only source. Three clients are
generated with OpenAPI Generator 7.1.0 and should not be hand-edited:

| Client | Path |
|---|---|
| Java | `vcell-restclient/` |
| Python | `python-restclient/` |
| Angular | `webapp-ng/src/app/core/modules/openapi/` |

```bash
./tools/openapi-clients.sh                 # spec already current
./tools/openapi-clients.sh --update-spec   # rebuild vcell-rest, refresh tools/openapi.yaml, regenerate
mvn compile test-compile -pl vcell-rest -am
```

Change the resource or DTO, regenerate, then compile downstream. The Angular
app and any Python caller of the new API pick up the change only after that.

### Messaging

Read [docs/MESSAGING.md](../docs/MESSAGING.md) before touching `cbit.vcell.message`.
Short version:

- **Legacy stack.** Interfaces in `vcell-core` (`VCMessagingService`, `VCMessageSession`, `VCQueueConsumer`). JMS implementation in `vcell-server` (`cbit.vcell.message.jms`). Callers never touch `javax.jms` directly. `VCMessagingServiceActiveMQ` talks to Classic; `VCMessagingServiceArtemis` talks to Artemis over OpenWire. Destination names are `VCellQueue` / `VCellTopic`, overridable with `vcell.jms.queue.*` and `vcell.jms.topic.*`.
- **Quarkus stack.** SmallRye Reactive Messaging over AMQP 1.0, Artemis only. Used for optimization (`opt-request` / `opt-status`) and export. The optimization worker (`HtcSimulationWorker` via `VCMessagingServiceArtemis`) and `vcell-rest` share those queues; Artemis translates OpenWire and AMQP.

Session ownership is load-bearing. `RpcService` in `vcell-api` holds one JMS
session for the life of the process, shared by every HTTP thread. `RestDatabaseService`
opens a fresh session per request and closes it. `javax.jms.Session` is not
thread-safe. Do not close a shared session from request or consumer code, and
do not add ActiveMQ Classic redelivery machinery — Artemis is where new
messaging work belongs.

### Database

Pattern, from [docs/database-design-patterns.md](../docs/database-design-patterns.md):

```
DatabaseServerImpl
  └── DBTopLevel / AdminDBTopLevel     connection and transactions
        └── DbDriver subclasses        SQL
              └── Table subclasses     schema and value mapping
```

`cbit.sql.Table` is the base. `VersionTable` is for domain objects (BioModel,
Simulation, MathModel, Geometry) and carries `privacy` and `versionFlag`.
Operational tables (`SimulationJobTable`, users, API clients) extend `Table`
directly and are not versioned. Each table is a singleton (`public static final … table`)
with `public final Field` columns. SQL types are the `SQLDataType` enum so the
same Java schema emits Oracle or PostgreSQL.

### Privacy vs curation

These two columns are easy to mix up and they are not the same thing.

- `GroupAccess` is privacy: who may read or write the document.
- `versionFlag` is curation: Published, Archived, or Current. It is not an access check.

## Repository map

Maven reactor (`pom.xml`, artifact `org.vcell:vcell-pom`). Version on snapshots
is `0.0.1-SNAPSHOT`.

| Module | Role |
|---|---|
| `vcell-core` | Domain model, VCML, math, mapping, solver-facing types, messaging interfaces, simulation data. Most of the science lives here |
| `vcell-math` | Expression parser and lower-level math (`cbit.vcell.parser` and related). Distinct from the `cbit.vcell.math` package, which lives in `vcell-core` |
| `vcell-util` | Shared utilities, including `DialogUtils` |
| `vcell-client` | Swing desktop client. Entry used by `vcell.sh` is `cbit.vcell.client.VCellClientMain`. Dev main is `org.vcell.standalone.VCellClientDevMain` |
| `vcell-server` | Database, dispatcher, data server, SLURM submit, JMS implementations |
| `vcell-api` | Legacy Restlet API, `/api/v0/` |
| `vcell-api-types` | Types shared with the legacy API |
| `vcell-apiclient` | Legacy API client |
| `vcell-cli` | Command-line model and simulation workflows. Calls Python via `cli.workingDir` |
| `vcell-admin` | Java administration tools. The IntelliJ client run config uses this module's SDK as the classpath root |
| `vcell-vmicro` | Virtual FRAP / microscopy |
| `vcell-rest` | Quarkus API, `/api/v1/` |
| `vcell-restclient` | Generated Java client |

Python packages, each its own Poetry project. Install all of them even for
Java-only work; Fast tests invoke them.

| Package | Role |
|---|---|
| `vcell-cli-utils` | Helpers the Java CLI drives (`org.vcell.cli.CLIUtils`, env `cli.workingDir`) |
| `python-utils` | BioSimulations upload / run / compare / publish pipeline |
| `pythonData` | Result and data handling |
| `pythonVtk` | Mesh processing |
| `pythonCopasiOpt/vcell-opt` | COPASI parameter estimation |
| `python-restclient` | Generated Python client for `/api/v1/` |
| `docker/swarm/vcell-admin` | Operator CLI: `health`, `showjobs`, `slurmjobs`, `logjobs`, `killjobs` |

Other trees:

| Path | Role |
|---|---|
| `webapp-ng/` | Angular 17 app. Per-deployment URLs are compiled in via `src/environments/environment.*.ts`, so each site (prod, stage, island, remote) is its own `ng build` |
| `webapp-viewer/` | Browser 3D field viewer. No build step; `./vcell.sh --field-viewer` serves this directory |
| `tools/` | `openapi-clients.sh`, geometry server, debug bridge, VTK-WASM fetch, release helpers |
| `docker/` | Images, Swarm stack, batch/solver image, install4j installer build |
| `nativelibs/` | Platform native libraries shipped with the client |
| `bionetgen/` | BioNetGen sources used by rule-based modeling |
| `localsolvers/` | Downloaded solver binaries (gitignored, per worktree) |
| `docs/` | Design notes. Start with `BUILDING.md`, `architecture-layers.md`, `MESSAGING.md` |
| `vcell-client/UserDocumentation/` | Desktop help XML, compiled to JavaHelp and published as HTML |

Solvers themselves are not in this repo. Desktop builds download archives into
`localsolvers/`. HPC jobs run Singularity images selected per solver. The split
into per-solver repos (`vcell-ode`, `vcell-fvsolver`, `vcell-nfsim`, …) is
described in [docs/plan-solver-repos.md](../docs/plan-solver-repos.md).

## Desktop client

`./vcell.sh` is the from-source launcher. It puts
`vcell-client/target/maven-jars` and `vcell-client/target` on the classpath and
starts `cbit.vcell.client.VCellClientMain`. Flags:

- `--debug-bridge[=PORT]` — loopback HTTP bridge for driving Swing from outside the process. Default port 9123. See `vcell-client/src/main/java/org/vcell/client/debug/README.md`.
- `--field-viewer[=URL]` — enables the in-client "View in 3D" path and the loopback field server.

The windowing layer is `org.vcell.client.logicalwindow`. Swing has no parent
relationship between top-level frames, so dialogs otherwise open behind the
window that created them. New UI should go through `DialogUtils` (message and
option dialogs) or `ChildWindowManager` (child viewers), both of which attach
a logical parent. Details: [docs/windowing-design-patterns.md](../docs/windowing-design-patterns.md).

When a desktop feature is removed or renamed, update
`vcell-client/UserDocumentation/` so the in-app help and the published HTML
stay in step.

## Build and test

Two phases, in this order. Python first, then Maven. CI
(`.github/workflows/ci.yml`) is the authority if this file and the workflow
ever disagree. Full write-up: [docs/BUILDING.md](../docs/BUILDING.md).

Prerequisites: Java 17, Maven 3.8+, Python 3.10 exactly, Poetry (from
`pip install -r requirements.txt`). HDF5 tools (`h5dump`) to run tests. Docker
for Quarkus tests.

```bash
pip install -r requirements.txt

for p in vcell-cli-utils docker/swarm/vcell-admin pythonCopasiOpt/vcell-opt \
         pythonVtk python-utils python-restclient pythonData ; do
  ( cd "$p" && poetry env use 3.10 && poetry install ) || { echo "FAILED: $p"; break; }
done

mvn --batch-mode clean install dependency:copy-dependencies -DskipTests=true
```

`dependency:copy-dependencies` fills each module's `target/maven-jars/`.
`vcell.sh` and the root Dockerfile put that directory on the classpath. A
plain `mvn clean install` compiles, then fails at launch with missing classes.

```bash
mvn test -Dgroups="Fast"
mvn test -pl vcell-rest                       # needs Docker
mvn test -pl vcell-client -am -Dtest=SomeTest
mvn test -pl vcell-client -am -Dtest=SomeTest#someMethod

( cd python-utils && poetry run python -m pytest )
( cd python-utils && poetry run python -m pytest tests/path/to/test_file.py::test_name )

cd webapp-ng && npm install
npm start
npm run build_prod    # also build_dev, build_stage, build_island, build_remote
npm run test:ci
```

The same `poetry run python -m pytest` form works in all seven Python packages.

### Traps that look like code bugs

- **Missing Python module or solver binary in a Fast test.** The Poetry install was skipped. In a fresh worktree, `vcell-core` Fast tests (`MathOverrideRoundTripTest`, `CopasiOptimizationSolverTest`, `VCellDataTest`) fail until the environments exist. Nothing in the POMs runs Poetry; the coupling is at test runtime.
- **A change in module A seems to have no effect when testing module B.** Maven used the last jar in `~/.m2`. Pass `-am` (`mvn test -pl vcell-client -am`).
- **`Could not build dependency tree` from the enforcer.** Re-run with `-X` and read the deepest `Caused by`. The rule drops the real `ArtifactResolutionException`. A repository 503 fails the build even for an artifact no module needs; a 404 does not, because Maven tries the next repo.
- **A new `git worktree` is a new build.** Poetry envs, `target/`, and `localsolvers/` are per worktree. Only `~/.m2` is shared, and the last worktree to `mvn install` is the one whose SNAPSHOT jars the others see without `-am`.
- **A green `CI-Test-group-Fast` check.** The `should-run` job skips the lane when only docs changed and still reports pass. Read that job's summary before treating green as "tests ran".

If a test run was saved to `./output.txt`, grep that file instead of rerunning the suite.

## CI and landing on master

| Workflow | When | What |
|---|---|---|
| `ci.yml` | Every push and the merge queue (~4 min) | Compile, Docker image smoke, Python package tests, Fast JUnit (sharded: `vcell-core` vs the rest), Quarkus tests |
| `regression.yml` | Merge queue, nightly, and manual `workflow_dispatch` (~9 min) | MathGen, SBML, SED-ML, BSTS, and related integration suites. Not triggered by `pull_request`, so a PR can enter the queue once the fast lane and review pass |
| `cd.yml` | Release | Push the image to `ghcr.io` |
| `codeql-analysis.yml` | Push and PR to `master` | Security scan |

`master` requires one review and the checks `build`, `CI-Test-group-Fast`,
`CI-Test-group-Quarkus`, and `regression-gate`. The merge queue runs regression
before the commit lands. Force-push is blocked.

An admin merge (`gh pr merge --admin`) skips the queue, so regression does not
run. Kick it off with `gh workflow run regression.yml --ref master`.

Ad-hoc regression on a branch, before it is queued:
`gh workflow run regression.yml --ref <pr-branch>`.

## Where to start changing things

| You want to… | Start here |
|---|---|
| Change physiology, applications, or VCML | `vcell-core`, packages `cbit.vcell.model`, `cbit.vcell.biomodel`, `cbit.vcell.mapping` |
| Change what gets solved, or comparison of two maths | `cbit.vcell.math` in `vcell-core`. Read `docs/architecture-layers.md` first |
| Add or adjust a solver input format | The solver writer (`org.vcell.solver`, `cbit.vcell.solver`), not the math class |
| Change the desktop UI | `vcell-client`. Dialogs via `DialogUtils` or `ChildWindowManager` |
| Add an HTTP endpoint the webapp calls | A resource under `vcell-rest/.../handlers/`, then `./tools/openapi-clients.sh --update-spec` |
| Change save/load or a table | `vcell-server` database layer. Read `docs/database-design-patterns.md` |
| Change job dispatch or SLURM | `vcell-server` `cbit.vcell.message.server.dispatcher` and `...batch.sim` |
| Change queues or consumers | `docs/MESSAGING.md`, then `cbit.vcell.message` |
| Change the public website UI | `webapp-ng/` |

## Further reading

- [docs/BUILDING.md](../docs/BUILDING.md) — build order, worktrees, the enforcer trap
- [docs/architecture-layers.md](../docs/architecture-layers.md) — bio / math / solver rules
- [docs/MESSAGING.md](../docs/MESSAGING.md) — brokers, sessions, measured redelivery behavior
- [docs/database-design-patterns.md](../docs/database-design-patterns.md) — tables, CRUD, transactions
- [docs/windowing-design-patterns.md](../docs/windowing-design-patterns.md) — Swing logical windows
- [docs/springsalad-abstractions.md](../docs/springsalad-abstractions.md) and [docs/nfsim-abstractions.md](../docs/nfsim-abstractions.md) — two solver pipelines in detail
- [docs/plan-solver-repos.md](../docs/plan-solver-repos.md) — where native solver binaries and images come from
- [docker/README_serviceInfo.md](../docker/README_serviceInfo.md) — deployed service list and shared directories
- [README.md](../README.md) — IntelliJ and Eclipse client run configurations
