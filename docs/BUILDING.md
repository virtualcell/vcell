# Building VCell

The build has two halves that must run **in this order**: the Python packages first,
then the Maven reactor. This is the order [`.github/workflows/ci.yml`](../.github/workflows/ci.yml)
uses, and it is the recipe this page mirrors — if the two ever disagree, CI is right.

Build the Python packages even if you only care about Java. Many Java tests shell out
to the Poetry environments at runtime, so skipping them produces failures that look
environmental but are not. See [Why Python first](#why-python-first).

## Prerequisites

| | Version | Notes |
|---|---|---|
| Java | 17 | What CI and releases use; `maven.compiler.source`/`target` are 17. Newer JDKs do build the project (verified on 25), but build with 17 if you want to match CI. |
| Maven | 3.8+ | |
| Python | 3.10 | Exactly 3.10 — the Poetry environments are created against it. |
| Poetry | 1.2.2+ | `pip install -r requirements.txt` |
| HDF5 tools | any | Only to run the tests (`h5dump`). macOS `brew install hdf5`, Ubuntu `sudo apt-get install hdf5-tools`. |
| Docker | any | Only for the `vcell-rest` Quarkus tests, which use testcontainers. |

## 1. Python packages

Seven packages, each its own Poetry project:

```bash
pip install -r requirements.txt     # installs poetry

for p in vcell-cli-utils docker/swarm/vcell-admin pythonCopasiOpt/vcell-opt \
         pythonVtk python-utils python-restclient pythonData ; do
  ( cd "$p" && poetry env use 3.10 && poetry install ) || { echo "FAILED: $p"; break; }
done
```

`poetry env use 3.10` is needed locally but absent from CI, which gets 3.10 from
`actions/setup-python`. If you manage Python with pyenv, make 3.10 visible first
(`pyenv install 3.10` and either `pyenv global`/`pyenv local` it, or point Poetry
straight at it: `poetry env use "$(pyenv root)/versions/3.10.14/bin/python3.10"`).

Takes about a minute from cold on a laptop.

## 2. Maven

```bash
mvn --batch-mode clean install dependency:copy-dependencies -DskipTests=true
```

`dependency:copy-dependencies` is **not optional**. It populates
`<module>/target/maven-jars/`, which is on the classpath of the things that
actually run:

- `vcell.sh` runs the desktop client with `-cp "./vcell-client/target/maven-jars/*:./vcell-client/target/*"`
- the root `Dockerfile` copies `./vcell-cli/target/maven-jars/*.jar` into the image

A plain `mvn clean install` compiles fine and then fails at run time with missing
classes, which is a confusing way to find out.

## Running the tests

```bash
mvn test -Dgroups="Fast"                 # fast unit tests
mvn test -pl vcell-rest                  # Quarkus REST tests (needs Docker running)
```

To reproduce a CI Fast-test shard exactly, including its class-level parallelism:

```bash
mvn --batch-mode test -pl vcell-core -Dgroups="Fast" \
  -Djunit.jupiter.execution.parallel.enabled=true \
  -Djunit.jupiter.execution.parallel.mode.default=same_thread \
  -Djunit.jupiter.execution.parallel.mode.classes.default=concurrent \
  -Djunit.jupiter.execution.parallel.config.strategy=dynamic
```

CI shards these as `vcell-core` on one runner and everything else on another.

### Testing a single module

Pass `-am` whenever you have changed a module that the one under test depends on:

```bash
mvn test -pl vcell-client -am -Dtest=SomeTest
```

Without `-am`, Maven resolves the dependency from the **last installed jar in
`~/.m2`**, not your working tree. Your change is silently ignored, and compile
errors in the dependent module can go unnoticed.

## Why Python first

Nothing in the POMs invokes Poetry, so Maven will happily compile without it. The
coupling is at **test run time**: Fast tests shell out to the Poetry environments
for OMEX validation, VTK and CoPaSi. Without step 1 they fail with errors that
point at the wrong thing — `ModuleNotFoundError: No module named
'biosimulators_utils'` wrapped in `RuntimeException: OMEX VALIDATION FAILED`, or
missing native solver errors.

Concretely, in a freshly created worktree `vcell-core`'s Fast group reported 10
errors across `MathOverrideRoundTripTest`, `CopasiOptimizationSolverTest` and
`VCellDataTest`. After step 1, all 433 passed. Nothing else changed.

So: if Java tests fail in a new checkout and the message mentions Python, a missing
module, or a solver binary, run step 1 before investigating anything else.

## Each git worktree is its own build environment

`git worktree` gives you an isolated checkout, and the build state is isolated with
it. **Both steps above must be repeated in every new worktree** — none of this is
shared with the main checkout:

- a Poetry environment per Python project — either inside the worktree as `.venv/`
  if you set `virtualenvs.in-project`, or in Poetry's shared cache. Either way it is
  keyed by project path, and a worktree is a different path, so it gets its own
- `target/` per module, including `target/maven-jars/`
- `localsolvers/` — solver binaries are downloaded per worktree during
  `generate-test-resources` (gitignored, so they do not come across with the checkout)

What *is* shared is `~/.m2`, which is why a worktree build is fast once the main
checkout has populated it: the full reactor takes well under a minute on a
warm cache, and the Python step about a minute.

Note that installing from a worktree publishes `0.0.1-SNAPSHOT` jars into that
shared `~/.m2`, so the last worktree to run `mvn install` is the one whose jars
another worktree picks up when you build without `-am`.

## Related

- [`README.md`](../README.md) — quick start and IDE setup
- [`docker/swarm/README_stack_on_mac.md`](../docker/swarm/README_stack_on_mac.md) — building and deploying a full local stack
- [`docs/RELEASING.md`](RELEASING.md) — cutting a release
- [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) — the authoritative build
