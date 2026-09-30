# Copilot instructions for VCell

VCell is a Java 17 Maven monorepo for computational cell-biology modeling and
simulation, with seven Poetry-managed Python packages and an Angular 17
frontend. Read the relevant module README and `docs/BUILDING.md` before
changing build, runtime, or deployment behavior.

## Build and test

The repository build has two required phases, in this order:

```bash
pip install -r requirements.txt
for p in vcell-cli-utils docker/swarm/vcell-admin pythonCopasiOpt/vcell-opt \
         pythonVtk python-utils python-restclient pythonData ; do
  ( cd "$p" && poetry env use 3.10 && poetry install ) || { echo "FAILED: $p"; break; }
done

mvn --batch-mode clean install dependency:copy-dependencies -DskipTests=true
```

`dependency:copy-dependencies` is required: it populates each module's
`target/maven-jars/`, used by `vcell.sh` and the Docker image. Each git
worktree needs its own Poetry environments, Maven `target/` directories, and
downloaded `localsolvers/`; only the Maven repository in `~/.m2` is shared.

### Java

```bash
mvn test -Dgroups="Fast"
mvn test -pl vcell-rest                 # Quarkus tests; Docker is required
mvn test -pl vcell-client -am -Dtest=SomeTest
```

Use `-am` when the tested module depends on a module changed in the working
tree. For one JUnit method, use
`-Dtest=SomeTest#someMethod`; for a module-specific test, replace
`vcell-client` and `SomeTest` with the target module and test class. CI's fast
tests are split between `vcell-core` and the other fast-test modules; the
parallelized shard command is documented in `docs/BUILDING.md`.

Many Java fast tests invoke the Poetry environments or native solvers at
runtime. Missing Python modules or solver binaries in a fresh checkout usually
mean the Python setup phase was skipped, not that the Java code is broken.
`vcell-rest` tests use Quarkus, PostgreSQL, and Keycloak test containers.

### Python and frontend

Run a package's tests from its package directory:

```bash
( cd python-utils && poetry run python -m pytest )
( cd python-utils && poetry run python -m pytest tests/path/to/test_file.py::test_name )
```

The same `poetry run python -m pytest` command applies to
`vcell-cli-utils`, `docker/swarm/vcell-admin`, `pythonCopasiOpt/vcell-opt`,
`pythonVtk`, `python-restclient`, and `pythonData`.

For the Angular application:

```bash
cd webapp-ng
npm install
npm run build_prod
npm run test:ci
npm run lint
```

Use `npm test -- --include='**/path/to/example.spec.ts'` to target one Angular
spec when using the Angular CLI test runner. The frontend also has
`npm start`, `npm run build_dev`, and environment-specific builds
(`build_stage`, `build_island`, `build_remote`).

## Architecture

- The Maven reactor defines the Java platform. `vcell-core` contains the
  domain model, VCML, simulation data, solver-facing logic, and the messaging
  interfaces. `vcell-math` and `vcell-util` provide lower-level math and
  shared utilities.
- `vcell-client` is the standalone Swing modeling/simulation client.
  `vcell-server` contains server-side simulation/database/dispatch services;
  `vcell-api` is the legacy Restlet API at `/api/v0/`.
- `vcell-rest` is the current Quarkus REST API at `/api/v1/`, using OIDC,
  Oracle in production, and PostgreSQL plus Keycloak test services. Its
  SmallRye OpenAPI output is the source for generated Java, Python, and
  Angular clients.
- `vcell-cli` runs command-line model and simulation workflows. The Python
  packages provide CLI support, VTK/data handling, optimization, REST-client,
  and administration functionality; Java tests can call them as external
  tools.
- `webapp-ng` is the Angular application and consumes the generated
  TypeScript client. `webapp-viewer` and `tools/` contain supporting browser,
  geometry, debug-bridge, and VTK tooling.
- Messaging spans the legacy `VCMessagingService` JMS abstraction in
  `vcell-core`/`vcell-server` and Quarkus SmallRye Reactive Messaging in
  `vcell-rest`. ActiveMQ Classic still carries legacy destinations while
  Artemis is the intended broker and carries the REST optimization/export
  flows. Read `docs/MESSAGING.md` before changing message destinations,
  consumer/session ownership, failover, or redelivery behavior.
- Runtime deployment is containerized: the root `Dockerfile` builds the CLI
  image, Docker/Swarm files describe local/server stacks, and Kubernetes
  overlays live in the separate `vcell-fluxcd` repository.

## Repository-specific conventions

- Keep `versionFlag` (Published/Archived/Current curation state) separate from
  privacy and authorization, which use `GroupAccess`.
- OpenAPI clients under `vcell-restclient`, `python-restclient`, and
  `webapp-ng/src/app/core/modules/openapi` are generated. Change the REST
  endpoint/model source and regenerate with
  `./tools/openapi-clients.sh`; use `--update-spec` when `tools/openapi.yaml`
  must be rebuilt. Then run
  `mvn compile test-compile -pl vcell-rest -am`.
- Preserve the distinction between long-lived shared JMS sessions and
  per-request/per-message sessions. JMS sessions are not thread-safe; do not
  close shared sessions from request or consumer code. Destination names are
  centralized in `VCellQueue`/`VCellTopic` and may be overridden by
  `vcell.jms.queue.*` and `vcell.jms.topic.*` properties.
- User-facing desktop help is source XML under
  `vcell-client/UserDocumentation/`; update it when removing or renaming
  client features so the compiled JavaHelp and published web help stay
  consistent.
- Java source/target and source/reporting encoding are Java 17 and UTF-8.
  The frontend uses single-quoted TypeScript/HTML formatting conventions
  configured in `webapp-ng/package.json`.
- CI is authoritative for the build order and test matrix in
  `.github/workflows/ci.yml`; heavier integration suites are in
  `.github/workflows/regression.yml`. A green CI aggregate can mean the
  documentation-only lane was intentionally skipped, so inspect the
  `should-run` summary when relevant.
- For troubleshooting Maven dependency-resolution failures, rerun with
  `-X` and inspect the deepest `Caused by` entry rather than trusting the
  top-level enforcer message. If test output is saved to `output.txt`, inspect
  it instead of rerunning the suite.
