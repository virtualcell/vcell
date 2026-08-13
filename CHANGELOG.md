# Changelog

All notable changes to VCell are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
VCell uses a 4-part version `MAJOR.MINOR.PATCH.BUILD`; see
[`docs/RELEASING.md`](docs/RELEASING.md) for what each part means, the
release-cut procedure, and how this file relates to the user-facing
narratives under `release-notes/major/`.

Each section opens with a `Highlights.` paragraph (the user-facing story)
followed by flat Keep-a-Changelog categories. API consumers should scan
`Changed` and `Removed` for breaking changes.

## [Unreleased]

_(Release-manager scratchpad. Populated at release-cut time.)_

## [8.0.18.01] - 2026-08-13

**Highlights.** Nothing changes for anyone using VCell. The consolidated service image now
supplies configuration under the same modern names as everything else, which was the last piece
missing before the legacy name fallback can be retired.

### Fixed
- The consolidated `vcell-service` image defined its configuration only under the historical
  short names, while the ConfigMaps, the deployment manifests and the five per-service images
  had all gained modern equivalents. It is the image the services actually run, so a setting
  reachable only by its old name was still relying on the compatibility fallback. 75 modern
  names added, each with the same value as the name beside it. (#1935)

### Notes for API consumers
No API changes. Deployment configuration is unchanged; both names are set and resolve to the
same value.

## [8.0.17.01] - 2026-08-13

**Highlights.** Nothing changes for anyone using VCell. Configuration is now supplied under one
set of names end to end, a missing or wrong secret path is reported at startup instead of
surfacing later as an unreadable password, and the HTTPS support in the legacy API — dead since
the ingress began terminating TLS — is removed along with the keys it needed. CI stops
re-analysing code it has already analysed.

### Added
- Every legacy environment variable the images define now has an `UPPER_SNAKE` twin carrying the
  same value, matching the names the deployment supplies. This is the last of three layers, and
  it means no service setting is reachable only by a legacy name. (#1931)
- A `FILE` property type. `PropertyLoader` already checked directories and executables at
  startup but had no way to say "this file must exist and be readable", so every secret path was
  unchecked. Five now use it: the database and JMS password files, the HPC ssh key, and the two
  JWT signing keys. (#1932)

### Removed
- HTTPS support in the legacy API, and the keystore configuration it required. It ran only when
  the service was asked for `https`; every deployment asks for `http` because the ingress
  terminates TLS, and the keystore file is not mounted in any container. A JKS keystore whose
  password was itself encrypted with the database password goes with it. (#1932)
- `vcell.jms.rest.pswdfile`, which appeared in one service's required-property list and was read
  by nothing. (#1932)

### CI
- CodeQL analyses a language only when the change touches a file of that language, and skips a
  merge whose tree was already analysed on the branch. The Java analysis compiles the whole
  project and takes 8–10 minutes; three of the five preceding merges touched no Java. The weekly
  full scan is unaffected — it re-analyses everything against current queries, which no
  per-change filter should be able to suppress. (#1933)

### Notes for API consumers
No API changes. The legacy API has never served HTTPS in any deployment; TLS is terminated by
the ingress, as before.

## [8.0.16.01] - 2026-08-13

**Highlights.** Nothing changes for anyone using VCell. This release finishes what 8.0.15.01
started: the five server services no longer receive their configuration as a wall of `-D` flags,
because they read it from their environment directly. 157 flags and 59 placeholder defaults are
gone, and a service that is missing a required setting now says so at startup instead of booting
on a value like `db-url-not-set` and failing later. CI also stops re-running the test suite on
merges that changed nothing it had not already tested.

### Changed
- The five service Dockerfiles no longer pass 157 `-D` flags whose only purpose was renaming
  container environment variables into system properties. What remains is the handful that are
  not renames — values fixed by the image layout rather than the deployment. Both the individual
  images and the consolidated `vcell-service` image now pass identical flags per service. (#1927)
- `sched` now honours `vcell.slurm.cmd.*`. Its Dockerfile never passed those flags, so it
  silently used built-in defaults while the deployment's `slurm_cmd_sacct` and friends were
  ignored — which mattered once job-status reconciliation moved into `sched`. Reading the
  environment directly closes it. (#1927)

### Fixed
- A missing required setting now fails at startup naming the property, rather than starting the
  service on a placeholder. `ENV dburl="db-url-not-set"` satisfied the startup check, so a
  service with no database URL configured would boot and then fail trying to connect to a URL
  literally called `db-url-not-set`. 59 such placeholders are removed, for exactly the settings
  each service declares as required. (#1927)

### CI
- The fast lane is skipped when it has nothing to test: a merge whose tree is identical to the
  branch that already passed it, or a change touching only documentation. It fails open, and
  still runs whenever master has moved — the case where post-merge testing can actually catch
  something. (#1925)

### Notes for API consumers
No API changes. Deployment configuration is unchanged: services still read the same environment
variable names they always have.

## [8.0.15.01] - 2026-08-13

**Highlights.** Nothing changes for anyone using VCell; this release is entirely about how the
server services are configured and packaged. Each service used to receive its configuration as
a wall of `-D` flags whose only job was renaming environment variables the container already
had — 98 of them across five services, and the reason five nearly identical Dockerfiles had
drifted into 804 lines. Services can now read that configuration directly from their
environment, and the five images are consolidated into one that selects a service by argument.
A misspelled setting, which used to be silently ignored, is now reported.

### Added
- `EnvironmentConfigProvider`: standalone services resolve configuration from their environment
  using the same naming rules MicroProfile applies, so the legacy services and vcell-rest answer
  to the same variable names instead of each needing its own. Existing `-D` flags still take
  precedence, so services can migrate one at a time. (#1920)
- A single `vcell-service` image serving api, data, db, sched and submit, choosing one by its
  first argument. Published alongside the five images it will replace; no deployment refers to
  it yet. (#1920)
- Unrecognised `VCELL_*` environment variables are reported at startup with the nearest matching
  property — `VCELL_SERVER_DBCONNECTUR` now says "did you mean vcell.server.dbConnectURL". A
  misspelling previously resolved to nothing and the property quietly took its default, so a
  service ran on configuration nobody intended while the deployment file read as though it were
  set. Advisory by default; `-Dvcell.config.strictEnvironmentNames=true` makes it fatal. (#1920)

### Fixed
- Startup validation resolves each declared property rather than enumerating configuration
  names. An environment variable cannot be reversed into a property name, so the old check would
  have reported environment-supplied configuration as missing and failed startup on settings that
  were in fact present. (#1920)
- `sched` and `submit` no longer disagree about the sim broker's internal port; both use 61616,
  which is what `activemqsim` listens on and what every deployment sets. (#1920)

### Changed
- `CI-full` no longer opens an interactive tmate session when a job fails. Both were left over
  from earlier debugging and held a runner until it timed out — including in `tag-and-push`, the
  job that creates the image tags every deploy pulls. (#1920)

### Notes for API consumers
No API changes. The `vcell-service` image is additive and unreferenced; the five existing service
images are unchanged and still published.

## [8.0.14.01] - 2026-08-13

**Highlights.** When a simulation is killed on the cluster, VCell now says why. A job the
scheduler ends — out of time, out of memory, node lost, preempted — never gets to report
back, so previously it sat "running" until a ten-minute silence timer failed it with
"solver stopped unexpectedly". VCell now asks slurm directly and reports the actual reason
within a minute, and a job cancelled on purpose is recorded as stopped rather than as a
failure, so a deliberate stop no longer looks like something went wrong with the model.

### Fixed
- A simulation ended by the cluster is reported with its real cause — `TIMEOUT`,
  `OUT_OF_MEMORY`, `NODE_FAIL`, `PREEMPTED` — instead of `failed: timed out` ten minutes
  later. For a SpringSaLaD run that reached its per-task time limit, this is the difference
  between knowing what happened and looking for a fault in the model. (#1917)
- A job cancelled on the cluster is recorded as **stopped**, not failed. Cancelling is
  deliberate, and reporting it as an error told the person who asked for it that something
  had gone wrong. A user's own stop was never affected. (#1914)
- Job states the scheduler reports were being misclassified: only the state literally named
  `FAILED` counted as a failure, so a job ended by `TIMEOUT` or `OUT_OF_MEMORY` read as
  though it were still running, and `OUT_OF_MEMORY` was not recognised at all. (#1915)
- Tracking of running cluster jobs no longer survives on a text file that could be
  corrupted or truncated. It is rebuilt from the database, so a restart recovers correctly
  on its own. The file had a defect that silently dropped every job listed after a damaged
  line, which is fixed and then removed entirely. (#1909, #1917)
- Job status is read once through a single shared query rather than a second hand-written
  one, so a multi-task SpringSaLaD run no longer trips a parsing fault, and a recycled
  cluster job id cannot be mistaken for the job that previously held it. (#1915)
- Hundreds of spurious `PROPERTY: … not marked required` errors per hour. The message was
  accurate — those services were fetching properties they had never declared — so the
  declarations were completed rather than the message silenced. A missing value now fails
  at startup, naming itself, instead of at first use. (#1918)

### Changed
- Cluster job status is read as structured output (`--json`) rather than parsed from
  columns, so the steps of a multi-task job arrive nested inside it rather than as rows to
  be filtered, and a slurm upgrade that moves a field is detected instead of misread. Job
  liveness is still read from the compact text form, which is far smaller for the one
  question it answers. (#1916)
- If slurm's accounting service is unavailable, VCell falls back to asking the scheduler
  directly for what it still holds in memory, and says so. Previously that outage silently
  produced no failures at all — indistinguishable from every job being healthy. (#1916)

### Notes for API consumers
- No `/api/v1/` schema changes; `tools/openapi.yaml` is unchanged and the generated Java,
  Python and TypeScript clients are unaffected.
- A simulation cancelled on the cluster now reports `SchedulerStatus.STOPPED` where it
  previously reported `FAILED`. Anything treating "not completed" as "failed" should treat
  stopped as a third outcome.

## [8.0.13.01] - 2026-08-12

**Highlights.** Simulation results stored in HDF5 — Chombo and MovingBoundary — can now be
read on an Apple-silicon Mac. Both readers used a JNI binding with no arm64 build, so every
such read threw `UnsatisfiedLinkError` and the only way through was a `linux/amd64`
container; the read paths are now pure Java. Chombo meshes are also correct and far smaller:
a whole voxel meeting a cut cell no longer leaves an unshared face, so surface extraction
stops reporting interior walls as though they were the domain surface. On the server side,
a JVM sizing mistake that had been masked for six months by an hourly restart is fixed, and
the Java debug agent is no longer switched on by default in every service.

### Fixed
- Chombo and MovingBoundary results read through the pure-java `io.jhdf` library rather than
  the native `ncsa.hdf` binding, which has no arm64 build. Reading a stored result no longer
  requires a machine with that library built, and the explicit "skip on macos arm64" guards
  are gone. (#1903, #1906)
- Chombo cut cells are written as polyhedra, so a face shared with a neighbouring whole voxel
  is genuinely shared rather than appearing once as a quad and once as two triangles. Interior
  walls are no longer extracted as domain surface, the non-manifold edges along them are gone,
  and the mesh is roughly an order of magnitude smaller. Completes #1896. (#1900)
- Server JVMs no longer size their heap larger than their container can hold. With
  `MaxRAMPercentage=80` on a 2000Mi limit the heap alone could reach 1600Mi against ~700Mi of
  non-heap, so the kernel killed the container while the Java heap was ~95% free — which also
  meant `HeapDumpOnOutOfMemoryError` could never fire, and the cause stayed invisible. Heap is
  now 50%, an idle JVM returns memory rather than accumulating garbage, and native memory
  tracking is on so the remaining footprint can be accounted for. (#1899)
- The BMDB nightly reports its result. It ended in an unconditional `return 0`, and its only
  alarm compared against `"$(cat report.md)"` — the file's contents used as a filename — so it
  passed whatever it found. (#1901)

### Changed
- The Java debug agent (JDWP) is opt-in rather than always-on. A JDWP listener authenticates
  nobody: whoever reaches the port can invoke methods, read and write fields and load classes.
  Set `VCELL_DEBUG_OPTS` and restart the service to enable it for a session. Note it cannot be
  attached to a running JVM — `libjdwp` provides only `Agent_OnLoad` — so a heap dump, JFR
  recording or thread dump via `jcmd` remains the way to inspect a process without restarting
  it. (#1904)
- HDF5 table exports are written with the pure-java writer. `ASCIIExporter` keeps the native
  writer deliberately: it streams a dataset one time slice at a time, and `io.jhdf` can only
  write a dataset whole. (#1907)
- `io.jhdf` 0.8.4 to 0.13.0, required for slice reads of chunked datasets, which MovingBoundary
  needs and 0.8.4 refuses. Adds one transitive dependency, `com.ning:compress-lzf`. (#1906)

### Added
- `tools/release/` — the scripts the release procedure depends on, checked in next to the
  deploy skill rather than living in a scratch directory. They gate on every required check
  being present and green, on regression passing for the exact commit being tagged, and on
  CI-full's `tag-and-push` having produced the image tags a deploy pulls. (#1898)

### Notes for API consumers
- No `/api/v1/` schema changes; `tools/openapi.yaml` is unchanged and the generated Java,
  Python and TypeScript clients are unaffected.
- The HDF5 files VCell writes are unchanged in structure. What changed is the library used to
  read and write them, so anything reading VCell's HDF5 output needs no adjustment.
- Anything that relied on a debug port being open on a VCell service will no longer find one.

## [8.0.12.01] - 2026-08-11

**Highlights.** API access tokens are no longer written to the server logs. VCell builds
its SQL by string concatenation, so a token passed as a value ended up in the statement
text, and the database layer logged whole statements — 40 live tokens reached the alpha
site's logs over seven days before this was found. Separately, server-side VTU generation
through the Python VTK service has been silently broken since July 2023: every mesh was
"written" to a directory path instead of a file, so Chombo, smoothed finite-volume and
comsol meshes never appeared. Both are fixed.

### Fixed
- API access tokens are no longer logged. Per-statement SQL logging moved from DEBUG to
  TRACE, so it is off unless an operator opts in; the statements that remain at ERROR or
  INFO — where an unexpected row count is a genuine anomaly worth reporting — keep their
  level and have their values masked. This was not limited to debug builds: the ERROR
  path fires regardless of log level. (#1892)
- The Python VTK service writes each mesh to the requested file rather than to its parent
  directory. Broken since the thrift-to-CLI refactor in July 2023, which made the write a
  silent no-op and left the caller failing with "No such file or directory". Affects
  Chombo volume and membrane, smoothed finite-volume, and comsol meshes; MovingBoundary
  was unaffected because its VTU writer is pure Java. (#1893)
- Chombo cut cells are tetrahedralized from their own faces, so a cut cell's tetrahedra
  cover exactly the cell's own volume and the mesh stays watertight. (#1896)

### Added
- Field viewer (flag-off): Chombo solutions render through the VTU seam, including static
  embedded-boundary meshes with mixed cell types. (#1890)
- Field viewer (flag-off): lab-frame point plots and moving-domain statistics for
  MovingBoundary runs. (#1891)

### Changed
- Field viewer (flag-off): a MovingBoundary run no longer boots with its geometry and
  field data drawn from different time points. (#1889)

### Notes for API consumers
- No `/api/v1/` schema changes; `tools/openapi.yaml` is unchanged and the generated Java,
  Python and TypeScript clients are unaffected.
- Operational note, not an API change: tokens already captured in existing log storage
  remain valid until they expire. This release stops new ones being written; it does not
  invalidate what was already recorded.

## [8.0.11.01] - 2026-08-10

**Highlights.** SpringSaLaD simulation results open again. Since 8.0.6.01, viewing
the results of any SpringSaLaD run whose solver output lacked a `SiteIDs.csv` failed
outright with `DataAccessException-f != cbit.vcell.parser.Expression`, taking the
whole results viewer down rather than just the legend. Separately, a long-running
simulation stopped by its configured time limit now says so: the message used to
read "solver stopped unexpectedly, solver exited (code=124)", which describes a
crash, when in fact the run was healthy and had simply exhausted the time it was
allotted — a distinction that cost a user four days of waiting to discover. On the
server side, the data service no longer opens a JMS connection per data event, and
the messaging framework gained an Artemis implementation.

### Added
- `VCMessagingServiceArtemis`, an Artemis implementation of the `VCMessagingService`
  framework, alongside the existing ActiveMQ Classic one. The optimization bridge in
  `OptimizationBatchServer` — the one caller that had been using hand-rolled JMS —
  now goes through it, and so gains the bounded failover and `JmsFailoverWatchdog`
  supervision every other messaging path already had. The wire contract is
  unchanged: JSON text on `opt-request` and `opt-status`. (#1872)
- Field viewer (flag-off, #1883): MovingBoundary runs render as body-fitted,
  time-varying 2D geometry, with the solver's own mesh shown as computed. Picking,
  time series and statistics are gated off for this mode, because per-cell ordinals
  are not stable across time on a moving mesh.

### Changed
- A solver stopped by its per-task time limit now reports the limit and says the
  simulation did not fail, instead of "solver stopped unexpectedly". Exit code 124
  is what `timeout` returns when it kills the process it wraps, and every Langevin
  task runs inside one. The limit itself is unchanged and remains a deployment
  setting. (#1881)
- The VTK.wasm bundle is built and released by
  [virtualcell/vcell-vtk-wasm](https://github.com/virtualcell/vcell-vtk-wasm); this
  repository consumes the published release, and its golden test now validates the
  artifact VCell actually ships rather than a locally built one. (#1824)

### Fixed
- SpringSaLaD simulation results open again. The site radius is an `Expression` in
  the math description and a `double` on the trajectory side, and the legend passed
  the `Expression` straight to a `%.5f` format — an `IllegalFormatConversionException`
  that propagated out of the results viewer's constructor. Affected any run without a
  `SiteIDs.csv`, which is where the model-side naming path runs. A radius that cannot
  be evaluated now leaves the site unnamed rather than aborting the legend. (#1882)
- The data service no longer opens a JMS connection for every data event. Each
  event built its own producer session, which connects eagerly — measured at 209
  connections in a single minute for one user browsing simulation data. Sends now
  take a JMS session of their own from the shared connection, which also removes the
  concurrent `createProducer`/`send`/`commit` race on a session shared across the
  pooled consumers' worker threads. (#1880)

### Notes for API consumers
- No `/api/v1/` schema changes; `tools/openapi.yaml` is unchanged and the generated
  Java, Python and TypeScript clients are unaffected.
- `SimulationMessage.WorkerExited(int)` returns different text for exit code 124.
  Anything matching on the literal string "solver exited (code=124)" should instead
  use `SimulationMessage.describesTimeLimit(String)`.

## [8.0.10.01] - 2026-08-10

**Highlights.** Editing a SpringSaLaD model now behaves correctly when the model
is saved. Comparison of two SpringSaLaD math descriptions was never implemented —
three of its methods returned a hard-coded "not equal" — so VCell could neither
tell that a molecule, site or bond had changed nor tell that it had not. Changes
to site radius, diffusion rate, colour, internal links, the 2D flag, and reaction
subtype/condition are now compared properly, and each of the three outcomes has
the consequence it should: an identical math is recognised as identical, an
equivalent one keeps existing simulation results attached, and a genuinely
different one hides results that no longer correspond to the math. A comparison
that fails now also reports *which* object and attribute differ, rather than only
that something did. Separately, an ActiveMQ client setting that could silently
drop RPC replies — leaving the caller to wait out its full timeout — is fixed.

### Added
- SpringSaLaD math descriptions report their differences: `MathCompareResults`
  carries a list of (path, attribute, old, new) entries, so a non-equivalent
  result names the molecular type, site or jump process that changed. (#1873)
- `docs/architecture-layers.md` — the responsibilities of, and permitted
  dependencies between, the biological, mathematical and solver layers, with each
  rule marked as stated or inferred so the inferred ones can be corrected. (#1873)
- Field viewer (flag-off, #1868): 2D simulation results, using the same
  deformation convention as 3D via `boundarySmoothingOn`, with orthogonal pan and
  zoom; 1D results explain themselves in a dialog rather than failing.
- `docs/MESSAGING.md` — the three brokers, the three client stacks, and why
  Artemis is the intended destination for all of them. (#1864, #1870)

### Changed
- Field viewer (flag-off, #1866): default boundary smoothing is now 16
  iterations, and the enable flag is discoverable in the installed
  `.vmoptions` file instead of having to be known in advance.
- `cbit.vcell.math` no longer imports anything from the biological or solver
  layers — 18 violations to zero, enforced by a test that also fails when an
  accepted exception goes stale. SpringSaLaD scalars in the math layer are now
  `Expression`s rather than raw doubles, and `.lngv` solver-input writing moved
  out of the math classes into `LangevinLngvWriter`, which fails loudly when a
  value does not resolve to a number. (#1873)

### Fixed
- The ActiveMQ client no longer decides for itself whether a temporary queue
  still exists. It answered from its own advisory-populated state rather than
  from the broker, so a connection that had not yet heard about someone else's
  reply queue refused to publish to it while it was alive — measured at 3.4 ms
  after creation, with the genuine removal advisory arriving 60 s later. Because
  a connection is opened per message, RPC replies could be dropped and the caller
  would wait out its entire timeout. (#1871, issue #1863)
- `is2D` on a SpringSaLaD molecular type now survives a save and reload. It was
  read back with `Boolean.getBoolean`, which reads a system property rather than
  the stored token, and it had no XML path at all despite its tag being
  declared — so it always came back false. (#1873)

### Notes for API consumers
- No `/api/v1/` schema changes; `tools/openapi.yaml` is unchanged and the
  generated Java, Python and TypeScript clients are unaffected.
- `RuleAnalysis` and `RuleAnalysisReport` moved from `org.vcell.model.rbm` to
  `cbit.vcell.math`, and the BNGL writing methods that take math types moved from
  `RbmUtils` to a new `ParticleBnglStringWriter`. These are internal Java types,
  not part of the REST surface, but they are a source-breaking change for anything
  compiling against `vcell-core`.
- `RulebasedTransformer.compareOutputs`, which cross-checked VCell's generated
  NFSim XML against BioNetGen's, has been removed. It was unreachable, and its
  loss is noted in `docs/nfsim-abstractions.md` as worth resurrecting as a
  maintained tool.

## [8.0.9.01] - 2026-08-08

**Highlights.** Experimental 3D field viewer feature release — everything in it
is behind `-Dvcell.fieldViewer.enabled=true` and invisible in normal use. The
browser-based viewer for finite-volume results now renders VCell's display
convention faithfully: the volume mesh's boundary vertices are smoothed, so
boundary cells are distorted hexahedra reaching the smoothed surface, and the
shell, the cut plane and the on-screen statistics all derive from that one
mesh. The viewer gains run labeling, a color bar, tick-labeled axes, an
orthogonal cut plane, value-under-mouse picking with click-to-plot time
courses, and two documented statistics families.

### Added
- Field viewer (flag-off, #1860): each viewer window is labeled with its
  simulation's `model::app::sim` name and `SimID`, in the page and the browser
  tab; the canvas now shrinks with the window so several viewers tile on one
  screen.
- Field viewer (flag-off, #1861): color bar and camera-tracking tick-labeled
  axes, both with toggles; an axis-aligned cut plane implemented as a clip of
  the deformed display mesh — the cut face is flat and sharp and its rim lies
  on the smoothed surface at every smoothing setting; value under the mouse
  (plain-JS lattice ray walk, crop-aware) with click-to-plot of a picked
  point's full time course; a Stats button plotting per-variable spatial
  min/mean/max over time; and a live cropped-region readout
  (min / volume-weighted mean / max / volume of the smoothed mesh).
- Field server (loopback, flag-off): `/timeseries` and `/stats` endpoints
  reduce time-course and spatial-statistics queries server-side through the
  same `VCDataManager.getTimeSeriesValues` path as the desktop's own plots;
  `/info` now carries the simulation's display name and job index. (#1861)
- Custom VTK.wasm bundle v1.2.0 (vcell-vtk-wasm): marshals the volumetric clip
  filters and the statistics operators, and adds `vtkVCellDeformGridToSurface`,
  the write-back filter that produces the deformed display mesh. (#1861)

### Notes for API consumers
- No `/api/v1/` schema changes; `tools/openapi.yaml` is unchanged and the
  generated clients are unaffected. The new endpoints exist only on the desktop
  client's loopback field-viewer server, which is off by default.

## [8.0.8.01] - 2026-08-07

**Highlights.** Backend stability release. A review of a day's production error
logs found a single export whose reported progress came out `Infinity`; JSON
cannot represent that, so the event was undeliverable, was redelivered forever,
and produced roughly 4,900 error lines a minute until the pods were rolled.
Fixing it exposed a chain of messaging problems behind it: a JMS connection and
temporary queue were opened for every message even when the consumer never used
them, every RPC opened another connection, and all RPCs shared one JMS session
that is not thread safe. The SED-ML export id collisions that had been failing
the nightly suite are fixed, and the Langevin solver is updated to 1.4.12.

### Added
- Internal: `-Dtest.only` to narrow the long sharded test suites when chasing a
  single case. (#1841)
- Internal: the 3D field viewer's web content is now packaged into the
  installers. The viewer itself remains a **flag-off proof of concept** and is
  not reachable in normal use. (#1829, #1854)

### Changed
- Langevin solver updated to 1.4.12. Logging configuration is now compiled into
  the native image and cannot be redirected at runtime; the 1.4.11 binary shipped
  without GraalVM logging metadata, which left its logging unreliable. (#1857)

### Fixed
- An export reporting non-finite progress no longer wedges event delivery. The
  progress value is clamped rather than dropped, and the divisions that produced
  `Infinity` are guarded — a geometry selection with no curves and no points made
  the denominator zero. (#1837, #1838)
- A JMS producer session now opens its connection on first use instead of in its
  constructor. Five of the seven consumers never send anything through the
  session handed to them, so they were opening a connection, session and
  temporary queue per message and using none of it. (#1842)
- RPC requests no longer open a second JMS connection just to build the request
  message. (#1844)
- Concurrent RPCs no longer corrupt each other. All request threads shared one
  `javax.jms.Session`, which is not thread safe, so one caller's commit could
  commit another caller's in-flight send. Each call now gets its own session.
  (#1845)
- Removing a message consumer now closes it. It was only asked to stop, leaving
  the broker dispatching to a consumer nobody read, where prefetched messages sat
  undelivered until the connection died. (#1847)
- Publishing to a topic no longer leaks a `MessageProducer` per message on
  sessions that live as long as the server. (#1850)
- `transportResumed` is logged at INFO only after a genuine interruption. It also
  fires on a first connect, which with a connection per message meant thousands
  of lines a minute against zero interruptions. (#1839)
- SED-ML export no longer produces documents with duplicate ids: every id is
  minted from one document-wide allocator, applications get unique ids when their
  names collide, and an `initialAssignment` is targeted when a species has no
  `initialConcentration`. (#1840, #1843, #1846)
- OMEX validation errors now appear in the exception message instead of being
  discarded. (#1827)
- Internal: JMS test classes no longer interfere with each other through
  ActiveMQ's JVM-global broker registry, and the simulated-outage test no longer
  poisons the service the other tests share. (#1853, #1856, #1835)

### Notes for API consumers
- No `/api/v1/` schema changes in this build; `tools/openapi.yaml` is unchanged
  since 8.0.5.01, and the generated Java, Python and TypeScript clients are
  unaffected.

## [8.0.7.01] - 2026-08-06

**Highlights.** Maintenance release. The desktop update alert no longer opens
behind the main window, and a cancelled or failed update check can no longer
stop VCell from starting.

### Added
- Internal: a `swing-debug` skill and supporting tooling for driving the desktop
  client during development. (#1834)

### Changed
- Internal: slurm submission gained the variables needed for the upcoming script
  refactoring, with the Langevin fixture and its tests updated to match. (#1794)
- Internal: the Dockerfile refreshes the apt index in the same layer as the
  install, so a cached layer cannot pair a stale index with a new install.
  (#1832)

### Fixed
- The update alert now stays on top: the update check runs before the GUI is
  built and is waited for, and a check that is cancelled or fails no longer
  prevents VCell from starting. (#1833)

### Notes for API consumers
- No `/api/v1/` schema changes in this build.

## [8.0.6.01] - 2026-08-05

**Highlights.** Desktop UI bug-fix release. Five reported client bugs are
fixed: the geometry viewer opening behind the main window and appearing
blank, a crash while sizing species-pattern text, PathwayCommons links
landing on the wrong site, Constructed Solid Geometry being offered for
2D geometries, and a failed import crashing instead of explaining itself.
The SpringSaLaD 3D viewer gains a species selector and MP4 export, login
no longer fails when a second client is already running, and a
third-party outage can no longer block CI.

### Added
- SpringSaLaD 3D viewer: per-species visibility selector and MP4 export of
  trajectory animations, plus a color correction. (#1814)
- Swing debug bridge — a developer tool for inspecting and driving the
  desktop client, **off unless `-Dvcell.debugBridge=true`** and bound to
  loopback only. Semantic `name=`/type selectors, deterministic waits,
  popup-free menu activation, JTree/JTable row interaction, component
  property inspection, and committed launch/CLI/scenario tooling under
  `tools/debug-bridge/`. (#1817, #1823, #1830)
- Internal: CI build of a custom VTK.wasm and a golden-file test for
  client-side WindowedSinc smoothing, feeding the upcoming web field
  visualization; not shipped in any release artifact. (#1812, #1822)
- Internal: test coverage for MathModel references on publications, which
  previously had none — every publication test passed an empty list. (#1828)

### Fixed
- Opening a geometry from the Database window's Geometries tab no longer
  puts the viewer behind the main window, and it now shows the geometry
  immediately. The window was never raised (only BioModel/MathModel windows
  were, via a path a geometry has no equivalent for), and its editor toggle
  started selected while no viewer had been opened, so the first click
  merely deselected it. (#1818, issue #1737)
- Client no longer crashes with a `NullPointerException` while sizing
  species-pattern text before the drawing canvas is displayable. The same
  fault existed at 17 call sites across the shape classes and was fixed at
  all of them. (#1820, issue #1749)
- PathwayCommons "Open Web Link" now opens the pathway's Reactome page
  instead of the PathwayCommons site root. (#1820, issue #1744)
- Constructed Solid Geometry is no longer offered for 1D/2D geometries,
  where it produced meaningless results; it is 3D-only. (#1820, issue #1739)
- A document import that produces nothing now reports what went wrong —
  naming the file, or noting that a remote fetch returned no usable
  content — instead of failing with a `NullPointerException`. (#1821,
  issue #1748)
- Login no longer fails when another VCell client is already running; the
  authentication callback server now binds its port up front rather than
  probing for a free one. (#1815)
- Pathway Commons searches in the model editor now time out instead of
  hanging when the service is slow or unreachable. (#1826)
- Internal: `SimulationDispatcherTest` flakiness (#1816), and
  `PathwaySearchTest` now skips when PathwayCommons, Reactome or NCBI is
  unreachable or failing, instead of failing the required CI check — a
  multi-hour third-party outage had been blocking every merge. (#1819, #1825)

### Notes for API consumers
- No `/api/v1/` schema changes in this build; `tools/openapi.yaml` is
  unchanged since 8.0.5.01, and the generated Java, Python and
  TypeScript clients are unaffected.

## [8.0.5.01] - 2026-07-31

**Highlights.** Hardens publications against the null publication-date bug
that broke every desktop-client login on production 8.0.0.03, and fixes
SpringSaLaD bond rendering so bonds stop at sphere surfaces.

### Added
- Internal: CI scaffolding for a custom VTK.wasm (WebGL2) build feeding the
  upcoming web field visualization; not shipped in any release artifact.
  (#1805, #1806, #1807, #1808, #1809)

### Fixed
- Publication date is now required end-to-end. A curator could clear the Date
  field in the webapp publication editor and save; the resulting `NULL`
  `vc_publication.pubdate` crashed every desktop client at login
  (`SimpleDateFormat.format(null)` NPE while sorting the published-models
  tree). The webapp form now requires the field, the REST API rejects a null
  date with 400, the declared schema is `NOT NULL`, and the client tolerates
  null dates from legacy rows (sorts last, renders `?` with a warning icon).
  (#1810)
- SpringSaLaD 3D viewer: bond edges are truncated by ball radius in world
  space, so bonds stop at sphere surfaces instead of being drawn into the
  balls. (#1802)

### Notes for API consumers
- `createPublication` and `updatePublication` now return **400** when `date`
  is null — an additive `400` response in the OpenAPI spec (Java/Python
  clients regenerated, TypeScript client unchanged). No other `/api/v1/`
  schema changes in this build. A `NOT NULL` constraint on
  `vc_publication.pubdate` accompanies this in the production database.

## [8.0.2.07] - 2026-07-27

**Highlights.** Sixth hotfix on the 8.0.2 line, and a **server-only** deploy.
Fixes an HTTP 500 (`ORA-01489`) that broke `GET /api/v1/vcInfoContainer`
entirely for users whose visible models include one with many simulations.

### Fixed
- `getVCInfoContainer` no longer 500s with `ORA-01489: result of string
  concatenation is too long`. The issue #1746 `simKeys` aggregation used an
  Oracle `LISTAGG` subquery whose `VARCHAR2` result (4000-byte cap) overflowed
  for a model with ~450+ simulations. `simKeys` are now fetched as raw
  `(modelRef, simRef)` rows and grouped in Java — no length limit, and one
  grouped query per ~1000 models instead of a per-row subquery. Oracle-only;
  Postgres `string_agg` (dev/test) was never affected. (#1782)

### Notes for API consumers
- No `/api/v1/` schema change and no wire-format change (`simKeys` values are
  identical, only the DB aggregation changed). Server-only fix; existing clients
  are unaffected.

## [8.0.2.06] - 2026-07-27

**Highlights.** Fifth hotfix on the 8.0.2 line. Fixes a desktop-client
`NullPointerException` in **model search** that affected a large fraction of a
real user's models against the 8.0.2 API. Reconstructing a BioModel's child
summary from the wire DTO left the per-simulation-context arrays null when the
DTO omitted an (empty) one; the search serialization indexes those arrays by
the sim-context count, so it crashed.

### Fixed
- Client-side reconstruction (`DtoModelTransforms.dtoToBioModelChildSummary`) now
  rebuilds the per-sim-context parallel arrays (`scAnnots`, `geoNames`, `geoDims`,
  `simNames`, `simAnnots`) as empty-but-correctly-sized arrays instead of null,
  matching the native database invariant so `toDatabaseSerialization()` (desktop
  search) and other consumers don't NPE. Also hardens the MathModel child-summary
  transform against null simulation-name/annotation lists. (#1780)

### Notes for API consumers
- No `/api/v1/` schema change. Client-side (generated Java client consumer) fix;
  the desktop client and server should run matching builds.

## [8.0.2.05] - 2026-07-27

**Highlights.** Fourth hotfix on the 8.0.2 line. Fixes a desktop-client
`NullPointerException` that aborted loading the model list
(`GET /api/v1/vcInfoContainer`) for real accounts: reconstructing native objects
from the summary DTOs crashed on models whose optional fields the database
legitimately leaves null. Found by round-tripping the full production data set
through the client transforms — 1481 of 6073 models failed before the fix.

### Fixed
- Client-side reconstruction (`DtoModelTransforms`) now null-guards three cases
  that occur in real data: a Geometry with no image reference (analytic geometry),
  a BioModel with no child summary, and a MathModel with no model-info summary.
  Each rebuilds the native object with the same null the database path produces
  instead of throwing. (#1778)

### Notes for API consumers
- No `/api/v1/` schema change. Client-side (generated Java client consumer) fix;
  the desktop client and server should run matching builds.

## [8.0.2.04] - 2026-07-27

**Highlights.** Third hotfix on the 8.0.2 line, replacing the
`GET /api/v1/vcInfoContainer` summary serialization with dedicated DTOs. The
8.0.2.02/03 fixes patched individual symptoms of serializing core domain objects
directly; this build eliminates the whole class of bug by introducing flat DTO
records whose fields, getters, and OpenAPI schema all agree. It also fixes a
remaining desktop-client `NullPointerException` on shared models — the client's
`hiddenMembers` came back null because the wire carried
`normalGroupMembers`/`hiddenGroupMembers` while the schema declared
`groupMembers`/`hiddenMembers`.

### Changed
- `GET /api/v1/vcInfoContainer` and the per-type summary endpoints
  (BioModel/MathModel/Geometry/VCImage `summary`/`summaries`) now serialize
  dedicated DTOs (`VersionRep`, `UserRep`, `GroupAccessRep` + `GroupMemberRep`,
  `PublicationInfoRep`, `PreviewRep`) instead of core domain objects. `GroupAccess`
  is a single record discriminated by a `type` string (`all`/`none`/`some`); user
  keys, version keys, and dates are plain strings / epoch-millis; the preview
  thumbnail is base64. (#1775)

### Fixed
- Shared-model summaries no longer crash the desktop client with a
  `NullPointerException`: the polymorphic `GroupAccessSome`/`hiddenMembers` path,
  whose serialized shape diverged from its own schema, is removed. (#1775)
- CD-sites deploy workflow: the `server_only` deploy no longer fails downloading an
  installer artifact it never built, and the `tmate`-on-failure steps that masked
  real errors were removed. (#1775)

### Notes for API consumers
- **Breaking `/api/v1/` schema change** on the summary endpoints. All three
  generated clients (Java / Python / TypeScript-Angular) were regenerated; the
  desktop client and server must run matching builds.

## [8.0.2.03] - 2026-07-27

**Highlights.** Second hotfix on the 8.0.2 line, completing the
`GET /api/v1/vcInfoContainer` stabilization. A VCImage whose preview thumbnail
could not be decoded caused a serialization NullPointerException that aborted the
whole (streamed) response mid-way, so the desktop client received truncated JSON.

### Fixed
- `GIFImage.getSize()` no longer throws when a preview gif cannot be decoded
  (`ImageIO.read()` returns null); it returns a null size so the `vcInfoContainer`
  response serializes completely. Also corrects a latent bug where image width was
  computed from the height. (#1773)

### Notes for API consumers
- No `/api/v1/` schema changes in this build. Server-side serialization fix only;
  no client regeneration required.

## [8.0.2.02] - 2026-07-27

**Highlights.** Hotfix on the 8.0.2 line. Restores `GET /api/v1/vcInfoContainer`
for clients whose visible model list includes a **shared** BioModel — the
`GroupAccess` polymorphic discriminator (`type`) was being omitted for shared-access
records, causing the desktop client to fail deserialization.

### Fixed
- `GroupAccessSome` (shared-model access) now serializes its `type` discriminator,
  matching `GroupAccessAll`/`GroupAccessNone`. Its field was `private` (no getter),
  so a shared BioModel's `version.groupAccess` serialized without `type` and the
  generated REST client threw `InvalidTypeIdException` on `/api/v1/vcInfoContainer`.
  (#1771)

### Notes for API consumers
- No `/api/v1/` schema changes in this build. Server-side serialization fix only;
  no client regeneration required.

## [8.0.2.01] - 2026-07-27

**Highlights.** A stability and desktop-UX release on the 8.0 line. Fixes a
crash (and incorrect metadata) in the ODE/PDE results viewers when a model was
edited while results were open (issue #1746), and overhauls desktop window
management so dialogs and child windows are OS-owned by their parent — they no
longer hide behind the main window on recent macOS and Windows. Internally,
`getVCInfoContainer` moved from legacy RPC to REST, and CI was re-architected
into a faster two-tier pipeline.

### Added
- Dev-only Swing debug bridge for live UI introspection during development
  (off by default). (#1750, #1757)

### Changed
- Desktop windows: child windows and dialogs are now natively owned by their
  logical parent, so they stay in front of it (and minimize/travel with it)
  instead of relying on `toFront()`, which modern macOS/Windows no longer honor
  for a background app. All dialogs are standardized on the DialogUtils path.
  (#1763, #1765, #1766, #1767, #1768, #1769)
- Migrated `getVCInfoContainer` from legacy RPC to REST
  `GET /api/v1/vcInfoContainer`. (#1759, #1761)
- Re-architected CI into a two-tier pipeline (fast lane + deferred, sharded
  regression). (#1752, #1753, #1754, #1755)

### Removed
- Legacy RPC `getVCInfoContainer` endpoint (replaced by the REST endpoint). (#1759)

### Fixed
- ODE/PDE results viewers: fixed a crash (NullPointerException) and wrong
  metadata when the model was edited or re-saved while results were open;
  viewer metadata now resolves against the submit-time model. (#1758, #1761)
- Structure rename: the name field now reverts to the real name when a rename
  is vetoed (e.g. a duplicate name) instead of keeping the rejected value. (#1756)

### Notes for API consumers
- `getVCInfoContainer` is now `GET /api/v1/vcInfoContainer`; the legacy RPC
  endpoint is removed. `BioModelChildSummary` and `MathModelChildSummary` gained
  a `simKeys` field. Java/Python/TypeScript clients were regenerated. (#1759, #1761)

## [8.0.0.03] - 2026-05-21

**Highlights.** Patch release on the 8.0.0 line accompanying the
SpringSaLaD GA public release event. BioNetGen BNGL imports now
preprocess tilde-prefixed bare digits into the `~s<digit>` form,
resolving a parser ambiguity with state labels.

### Changed
- BioNetGen BNGL import: rewrite tilde-prefixed bare digits to the
  `~s<digit>` form during parser preprocessing, e.g. `~0` → `~s0`.
  Adjusts how state labels in BNGL input are read into the
  rule-based model. (#1698)

## [8.0.0.02] - 2026-05-19

**Highlights.** A small follow-on on the 8.0.0 line. Tightens Langevin
(SpringSaLaD) simulation-options limits — lower defaults (total jobs
100 → 20) and new hard caps (max concurrent 20, max total 200) — to
reduce inadvertent cluster-resource consumption. Quick Run now refuses
multi-job Langevin configurations with a clear error rather than
failing later. Internal model code gains defensive null-checks and
extended `StructuralSite` support in link handling.

### Changed
- Langevin simulation-options limits and defaults:
  `DefaultTotalNumberOfJobs` lowered from 100 to 20; new caps
  `MaxNumberOfConcurrentJobs = 20` and `MaxTotalNumberOfJobs = 200`.
  The options panel now enforces the invariant `concurrentJobs ≤
  totalJobs` and clamps values above the new caps. Users who had
  previously set values above these caps will see them adjusted
  on next edit. (#1695)
- Quick Run blocks Langevin simulations configured with more than one
  concurrent or total job, with an explicit error dialog
  ("Concurrent Jobs not supported for Quick Run."). Previously the
  job would launch and fail later. (#1695)
- `initializeForSpringSaLaD` and the link-update logic in
  `SpeciesContextSpec` extended to handle `StructuralSite` as a
  `LinkNode` in addition to `MolecularComponentPattern`. Internal;
  builds on the Structural Sites work that shipped in 7.7.0.78. (#1695)

### Fixed
- Model issue-gathering now warns on links with null
  `SiteAttributesSpec`, null `LinkNode`, or null link endpoints,
  surfacing malformed-link errors at validation time instead of
  failing deep in SQL generation. (#1695)

### Notes for API consumers
- No `/api/v1/` schema changes in this build.
- No database schema changes. (The `SpeciesContextSpecTable.java`
  diff is internal Java refactoring — `SpeciesContextSpec.internalLinkSet`
  was made `private` and call sites migrated to `getInternalLinkSet()`.)

## [8.0.0.01] - 2026-05-14

**Highlights.** First build of the 8.0.0 line. SpringSaLaD is now
enabled by default — particle-based reaction-diffusion modeling
transitions from an opt-in property to a first-class VCell modality.
This release also introduces a new Plot Options dialog and adds an
`admincli report` subcommand kit for period-bounded NIH-RPPR-style
usage reports. The MAJOR bump from 7.7 → 8.0 marks the SpringSaLaD GA
public release event; the underlying Structural Sites work that preceded
this build shipped in 7.7.0.78.

### Added
- New Plot Options dialog (`PlotOptionsPanel`) for configuring plot
  appearance across visualization panels. (#1691)
- `admincli report` subcommand kit for period-bounded usage reports
  over arbitrary `[start, end]` windows, with four single-purpose
  commands (`count-new-users`, `count-sim-jobs-in-db`, `count-asof`,
  `list-active-users`). Designed to be extended for next year's
  reporting request without requiring new command classes for new
  as-of metrics. Driver script `tools/report/rppr-2026.sh` runs the
  suite end-to-end. (#1690)

### Changed
- SpringSaLaD is now enabled by default. The previously required
  opt-in property in `PropertyLoader` is no longer needed for users
  to access SpringSaLaD modeling. (#1693)

### Notes for API consumers
- No `/api/v1/` schema changes in this build.

## [7.7] - 2024-09-27 to 2026-05-11

_Consolidated backfill section covering builds 7.7.0.0 through 7.7.0.78
(78 tagged builds across ~20 months). This is a curated summary of
notable changes since 7.6, not an exhaustive enumeration. See the
GitHub releases page for the per-build PR list._

**Public-release epoch breakdown** (see `release-notes/major/` for the
user-facing accordion entries):

| Epoch | Prod-rolled | Approx. build range |
|---|---|---|
| VCell 7.7 — Initial Release | 2024-09-27 → 2025-02-17 | 7.7.0.0 / .9 / .15 in prod |
| VCell 7.7 — Feature Update | 2026-02-04 | 7.7.0.47 in prod (consolidates 7.7.0.16-7.7.0.47 dev) |
| VCell 7.7 — Stability Update | 2026-04-20 → 2026-05-07 | 7.7.0.73 / .77 in prod (7.7.0.78 tagged but not prod-rolled) |

**Highlights.** The 7.7 line laid the foundation for the SpringSaLaD GA
public release event that follows in 8.0.0. Major themes:
(a) substantial SpringSaLaD evolution — UI redesign, multi-run support
with random seed and SLURM array submission, batch results
visualization, Structural Sites, integration with the upgraded
Langevin solver;
(b) migration of the REST API from legacy Restlet to Quarkus, with
new `/api/v1/` endpoints for BioModel, MathModel, Geometry, Image,
Field Data, and publications, plus database-backed parameter
estimation;
(c) infrastructure modernization — Apptainer/Singularity solver
images, new SLURM cluster, native `vcell-nativelib` compilation
pipeline for pyvcell;
(d) a continuous stream of stability fixes, capped by the
JmsFailoverWatchdog and OOM exit handler that closed out a wedge
incident near the end of the line.

### Added

_SpringSaLaD (Langevin) modeling._
- Structural Sites: anchor points within a molecule with positions in
  space, distinct from molecular components. Implemented in
  `SpeciesContextSpec` and `MolecularInternalLinkSpec` with UI for
  defining and linking sites, plus database persistence in
  `SpeciesContextSpecTable`. Closes #1687 (MVP system for Structural
  Sites). Shipped in the final 7.7.0.78 build and is the anchor
  feature of the SpringSaLaD GA story in 8.0.0. (#1685)
- Redesigned SpringSaLaD-specific user interface: large shape view
  for site attributes, molecular-structure properties panel, new
  tables/table-models for site attributes and links. (#1387)
- Editable links in the molecular-structure tables. (#1391)
- Improvements to the SpringSaLaD-specific properties panel and shape
  representation. (#1401)
- Multi-run support: `numTrials` and `randomSeed` for Langevin
  simulations, with SLURM array submission for concurrent runs
  and post-processing of multi-run results. (#1403, #1413, #1424,
  #1439, #1525, #1611)
- Transfer of Langevin batch simulation results from server to
  client. (#1623)
- Batch run results visualization: plots and tables for general
  statistics (average, min/max, standard deviation) and cluster
  analysis (ACS, SD, ACO). (#1661)
- Filter for SpringSaLaD simulation results: presents linear
  combinations of each molecule's site bond/state pairs and
  highlights trivial result sets where data does not change. (#1397)
- Physical-limits checking for site size, diffusion rate, and
  partition size. (#1478)
- Particle-displacement calculation: given diffusion rate, time step,
  and probability, computes the max distance a particle can travel —
  used to refine binding-event detection across partition boundaries.
  (#1492)
- SpringSaLaD graphic-editor fixes and UI bug fixes / enhancements.
  (#1638, #1642)
- Stricter integrator filtering: integrator choices in the solver
  panel are now filtered by required features per application type,
  removing unsuitable options for SpringSaLaD. (#1418, #1420)

_Quarkus REST API (new `/api/v1/` endpoints, replacing legacy Restlet)._
- Save BioModel endpoint with delete companion; Java GUI client
  switched to the REST endpoint for save and delete. (#1500)
- BioModelInfo retrieval endpoint with JSON serialization for
  `Version`, `GroupAccess`, `PublicationInfo`; client now uses
  the REST endpoint for biomodel summaries. (#1514)
- MathModel endpoints: info retrieval, save, delete, get; GUI
  client end-to-end. (#1526)
- Geometry endpoints for VCML geometry retrieval and modification.
  (#1533)
- Image (VCImage) resource. (#1544)
- Field Data endpoints: get IDs, get shape, analyze file, create
  from analyzed file, delete by ID; "simple" upload-and-create
  endpoint; derive Field Data from simulation results; copy Field
  Data from BioModel. (#1406, #1457, #1463, #1466, #1490, #1499)
- `PUT /api/v1/publications/{id}/publish` endpoint plus webapp UI
  for publishing BioModels and MathModels linked to a publication.
  On publish, two changes are made atomically per model:
  `GroupAccess` is set to `GroupAccessAll` (public read access) and
  `VersionFlag` is set to `Published` (curation status). Selective
  publishing of specific model keys is supported via
  `PublishModelsRequest`. (#1651)
- Centralized error-handling architecture for the new API: standard
  `VCellHTTPError` response object, per-endpoint declared error
  types, factory-based error generation, and inclusion in the
  generated OpenAPI spec. Closes #1502. (#1506)
- API endpoint to generate FVSolver input from VCML. (#1445)
- Server-side field-data analysis: the algorithm that analyzes
  uploaded field-data files and writes per-channel pixel data
  moved from client-side to server-side execution, with memory-
  management improvements that fixed large-file failures.
  Closes #1448. (#1477)
- `vcell-cli` connection reuse for parameter estimation: a single
  authenticated connection is reused across estimation requests
  instead of re-authenticating per request. Closes #1353
  (Parameter Estimation Results in Login Request). (#1498)
- Parameter estimation migrated to Quarkus REST with database-backed
  job tracking. (#1659)
- User-authentication middleware overhaul: the new API servers no
  longer thread an anonymous "VCell guest" user object through
  every endpoint. Specific endpoints can still permit anonymous
  access where appropriate (e.g. public-publication reads). (#1419)
- Login-prompt parameter in URLs to force the OIDC login flow. (#1520)
- VCell Support role added to the auth model. (#1536)
- Quarkus filter to sort HTTP response codes in the OpenAPI spec
  for stable client generation. (#1426)

_Tooling and CLI._
- Plotting in Java for VCell CLI; the Python live-shell is removed
  and the Python plotting bridge is deprecated. (#1427)
- `users-query` admin CLI command lists users who ran simulations
  since a start date. (#1415)
- `scan-xml-control-chars` admin CLI for detecting malformed XML
  in stored documents. (#1678)
- Server-side biosim log written directly from Java, including a
  fast in-memory variant. (#1396, #1400)
- Field Data writing in `FiniteVolumeFileWriter` is overridable for
  alternative storage layouts. (#1466)
- Python expression syntax support in VCell expressions — step 1
  toward PyVCell supporting infix notation in Python. (#1645, #1649)
- `/loki-query` slash command for VCell log access across
  prod/stage/dev namespaces, with direct-kubectl fallback. (#1680,
  #1682)
- Support-email parsing and triage tooling for processing user
  error reports. (#1670)
- VCell Alert blinking-icon now shows the alert title next to the
  icon, pulled from `https://vcell.org/webstart/VCell_alert/VCell_Alert.html`.
  The message fades when the icon stops blinking (about a minute
  after client open). Closes #1619. (#1618)

_Infrastructure._
- Native compilation of `vcell-nativelib` for the pyvcell Python
  client; nativelib deployed via the separate `libvcell` repo.
  (#1452, #1455)
- VCell on the new SLURM cluster: SSH command options configurable
  from K8s ConfigMaps; SLURM node read from property; default
  Singularity binary on batch-run node. (#1467, #1621, #1625)
- Langevin solver upgraded across the 7.7 line (1.4.0 → 1.4.2 →
  1.4.4). (#1532, #1558, #1588)
- Pure-Java `io.jhdf` library replaces the legacy native HDF5
  library for nonspatial HDF5 writes during OMEX execution. (#1371,
  #1374, #1383)
- BSTS (BioSimulations) container compatibility maintained across
  the line. (#1378, #1444, #1496)
- Restored simplified server-side Kubernetes logging across the
  prod/stage/dev deployments, closing a gap in observability that
  had hampered confident release pushes. Resolved via deploy-side
  changes (no closing PR in this repo). Closes #1583.

### Changed
- BioModel opening: any biomodel can be opened unless it is a
  duplicated leaf. (#1435)
- Spatial results export selection changed to checkbox-based.
  (#1667)
- Annotations: legacy "Annotations" synchronized with formal
  annotations in simulations. (#1414)
- External links rendered in parentheses in annotation displays.
  (#1566)
- KiSAO ontology refreshed; new KiSAO_694 parsing added. (#1393)
- Simulation `JobIndex` redefined in terms of `ScanIndex` and
  `TrialIndex` to support multi-trial Langevin runs. (#1398)
- DB classes renamed to Services as part of the Quarkus REST
  refactor. (#1512)
- Trivial `numTrials` value (1) no longer saved to XML/VCML.
  (#1421)
- Geometry viewer cropping logic. (#1437)
- Mac installer packaging: duplicate-installer copy/send flow.
  (#1615)
- VCell client can now be installed **without administrator
  privileges**. Removes a long-standing pain point for users on
  locked-down corporate machines who previously had to escalate to
  IT for every VCell update. Closes #805. (#1602)
- Pathway Commons annotation lookup upgraded to the new Pathway
  Commons URLs and feature set: recovers the list of pathways and
  loads most entity types. Reaction loading remains incompatible
  with the VCell internal model. Closes #1563. (#1574)
- The `enableSpringSaLaD` default was hard-coded toggled during the
  7.7 line to coordinate release-site state — enabled in #1446
  (2025-02-28), reverted in #1584 (2025-08-27) so a release could
  ship with SpringSaLaD opt-in. Subsequent work made the
  SpringSaLaD-default a site-aware setting (configurable per
  deployment), removing the need for source-level toggling. The
  setting is enabled across all deployments in 8.0.0.01 (#1693).
  (#1446, #1584)

### Deprecated
- Python live-shell in vcell-cli (functionality replaced by
  Java-based plotting). (#1427)

### Removed
- Field Data Specs (replaced by the new Field Data REST endpoints).
  (#1484)
- Python live-shell plotting bridge in vcell-cli. (#1427)
- Various dead functions cleaned up during the Quarkus migration.
  (#1530)

### Fixed

_Stability and concurrency._
- `InternalUnitDefinition.round` made thread-safe by serializing
  `DecimalFormat` access. Concurrent simulation generation could
  produce inconsistent unit strings. (#1669)
- `MathSymbolMapping` `TreeMap` concurrent-modification NPE. (#1465)
- NPE in `ParticleMathMapping` for non-mass-action kinetics. (#1671)
- NPE in `StochMathMapping` for symbol-less rate expressions. (#1672)
- NPE when inserting a compartment. (#1541)
- NPE-class issues when renaming entities in the document tree.
  (#1665)
- JMS failover wedge after broker reconnect: `JmsFailoverWatchdog`
  bounds reconnect attempts and triggers process exit on persistent
  failure. (#1681)
- Server JVMs now exit on `OutOfMemoryError` and dump heap so the
  pod is restarted instead of remaining wedged. (#1683)
- Desktop client no longer caches failed DNS lookups, allowing
  recovery after transient DNS outages. (#1684)
- Linux (Ubuntu) FiniteVolume Solver buffer overflow on certain
  SBML/OMEX simulations through the SASCO model and published
  models VCDB_101962320 / VCDB_101981216. Closes #1101.
  _[needs verification: confirm closing PR for #1101 was within the
  7.7 line; issue closed 2026-04-15, near end of line.]_
- Observable-generation performance: rewrote
  `NetworkTransformer` and supporting `Model` paths so the
  generate-observables step during rule-based-model save / network
  flatten no longer takes disproportionately long. Closes #1336.
  (#1380)

_XML / charset / format handling._
- Oracle CLOB reads now use a charset-aware character stream
  (previously assumed default platform charset, which corrupted
  non-ASCII content). (#1679)
- UTF-8 used explicitly when reading SBML / SED-ML XML. (#1676)
- XML chars validated in `name` and `sbmlName` setters to prevent
  control-character injection from imported documents. (#1677)
- SBML reserved-character reaction names mangled (e.g. `x` →
  `RESERVED_x`). (#1369)
- SBML structure names accommodated on import; passes BIOMD0001-0071.
  (#1368)
- Infinity and NaN supported in SBML values and MathML. (#1381)
- Different jump processes correctly reported in math description.
  (#1379)
- OMEX manifest separator fixed on Windows. (#1372)
- OMEX execution failure for repeated tasks of size 1. (#1392)
- Plots reported as Reports for BioSimulations compatibility. (#1444)

_Client and desktop._
- VCell client requests focus after OIDC login/logout flow. (#1395)
- HTML help: generation fix and dead-link repair pass over
  `vcell-client/UserDocumentation/`. Closes #1279 (dead links in
  VCell HTML help). (#1411, #1412)
- Geometry tab: "Constructed solid geometry" UI label corrected to
  "Constructive solid geometry". Closes #1560. (#1561)
- BioModel save: KeyValue serialization changed from object to
  string form, fixing a save bug surfaced after the move to the
  Quarkus save endpoint. Closes #1501, #1517. (#1523)
- Math Model Export error. (#1594)
- `OutputFunctions` ignored bug. (#1595)
- Membrane Langevin jump processes not loading from VCML. (#1362)
- Membrane reactions placed at correct location. (#1616)
- Export service held old state across runs. (#1364)
- Inability to export locally loaded simulation results. (#1367)
- PubMed links disappeared from Annotations tabs. (#1576)
- VCell connection failure on transient errors. (#1471)
- Export-test failure on local runs (Quarkus CDI context).
  _[needs verification: see project_export_test_bug.md]_ (#1620)
- Langevin input file particle-location name. (#1454)

### Notes for API consumers
- The 7.7 line is the primary REST-API transition window. New
  `/api/v1/` endpoints (Quarkus) coexist with legacy `/api/v0/`
  (Restlet); new client code should target `/api/v1/`. See
  `tools/openapi.yaml` for the current schema.
- BioModel save/delete, BioModel info, MathModel CRUD, Geometry
  modification, VCImage retrieval, Field Data lifecycle, parameter
  estimation, and the new `PUT .../publish` resource all live on
  `/api/v1/`.
- OIDC authentication via Auth0 is required for `/api/v1/`; the
  new auth middleware (#1419) removed anonymous "VCell guest" from
  the new API servers.
- OpenAPI spec is now generated by Quarkus SmallRye OpenAPI and
  stored in `tools/openapi.yaml`; Java, Python, and TypeScript
  clients are auto-generated. CI verifies the spec is stable
  via `Openapi spec ci test`. (#1521)

## [7.6-patches] - 2024-07-13 to 2024-09-26

_Consolidated backfill section covering bug-fix and incremental
patches to the 7.6 line that were rolled to prod after the
"VCell 7.6" public release (2024-07-12, build 7.6.0.19) and
before the 7.7 line opened in prod (2024-09-27, build 7.7.0.0).
This is a curated summary, not a per-build enumeration; see the
GitHub releases page for the 7.6.0.20 through 7.6.0.50 detail._

**Highlights.** Patch releases on the 7.6 line after its July 2024
public release. Notable themes:
(a) significant progress on SpringSaLaD imports / partitions /
bound-transitions / help content (laying groundwork for the 7.7
line and ultimately 8.0.0 GA);
(b) generalized stochastic simulation of non-mass-action reactions;
(c) N5 file-format polish for export;
(d) expanded SED-ML compatibility (URI/Path refs, MathML xor,
multiple master SED-ML);
(e) security cleanup (passwords removed from the desktop client)
and early Quarkus REST groundwork (structured logging in Elastic
Common Schema format).

### Added
- Generalized Stochastic simulation of reactions that are not
  Mass-Action — broadens the set of models that can be run with
  the Gillespie stochastic solver. (#1355)
- N5 file-format export polish: PDE Export GUI can export
  post-processing variables for N5; mask is now the last channel;
  pixel length and unit data plus extra domain metadata included.
  (#1329, #1357)
- SED-ML compatibility: URI or Path references supported in SedML;
  MathML `xor` support added; SED-ML files with more than one
  master-marked SedML can run. (#1345, #1347)
- SpringSaLaD (pre-7.7) groundwork:
  - Importing from standalone SpringSalad (`.ssld`) format with
    accompanying UI / export / data-representation adjustments.
    (#1349)
  - Partitions concept implemented in VCell (UI, in-memory
    representation, persistence) with round-tripping against the
    standalone SpringSalad app. (#1354)
  - Refactored conditional bound-transition reaction so the
    implicit bond end is the transitioning site; automatic
    reaction-type recognition, export/import, and issue generation
    updated. (#1360)
  - SpringSaLaD in-app help content with screenshots. (#1323)
- Structured logging in the Quarkus vcell-rest service, emitted in
  Elastic Common Schema format. (#1312)
- Unit tests baselining current SlurmProxy behavior. (#1337)

### Changed
- Publication edit form properly handles BioModel and MathModel
  reference IDs. (#1281)
- All passwords removed from the VCell desktop client (security
  cleanup; see #1315). (#1317)
- `sim-health` status mapping refined for BetterStack monitor
  alerting: `OK` → `UP`, `Warning` → `UP WARNING`, `Unknown` →
  `WARNING`, `Critical` → `DOWN`. (#1322)
- Help-menu separator cleanup; duplicate "Rule-Based Features"
  entry removed. (#1313, #1314, #1326)
- Dimension info now passed to the `Spec` in a human-readable
  form. (#1358)

### Deprecated
- Legacy `/api/v0/biomodel/<id>/biomodel.omex` now returns
  `HTTP 501 Not Implemented`. (#1328)

### Removed
- Field Data geometry editor temporarily disabled, pending a fix
  for its Swing-thread issues (later tracked separately as #1318).
  (#1319)

### Fixed
- `vmoptions.txt` `--add-export` syntax required a trailing
  newline. (#1325)
- Bio-Formats library fix. (#1321)
- NFSim remote-execution failure (fixes #1187). (#1333)
- Deprecated GitHub Actions in CI updated. (#1351)

### Notes for API consumers
- The legacy Restlet `/api/v0/biomodel/<id>/biomodel.omex` endpoint
  is no longer implemented; it returns `HTTP 501`. (#1328)
- The Quarkus vcell-rest service began emitting structured logs
  in Elastic Common Schema format during this window, simplifying
  log aggregation in centralized log stores. (#1312)
