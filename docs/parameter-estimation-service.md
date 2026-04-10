# Parameter Estimation Service Migration

## Motivation

Parameter estimation (curve fitting) is a core VCell capability that allows users to optimize model parameters against experimental data using the COPASI optimization engine. The current implementation has been unreliable in production (GitHub #1653), and the root cause is architectural: the REST-to-batch-server communication relies on persistent in-memory TCP socket connections that are inherently fragile.

### Problems with the current design

1. **In-memory socket map loses state on restart.** The legacy `vcell-api` stores active optimization connections in a `Hashtable<String, OptSocketStreams>` (`paramOptActiveSockets`). When the pod restarts — which is routine in Kubernetes — all running optimization jobs become unqueryable, producing `"Can't find connection for optimization ID=..."` errors.

2. **No persistence.** There is no database record of optimization jobs. If the API process dies, all knowledge of submitted jobs is lost. Users see a failure with no way to recover results that may have already been computed and written to disk.

3. **Custom TCP socket protocol.** The REST layer opens a raw TCP socket to `OptimizationBatchServer:8877` on the submit service, using Java object serialization for messages (`OptMessage.*`). This is fragile (connection drops, timeouts), difficult to debug, and a security concern (deserialization of untrusted objects).

4. **DNS-based service discovery via `nslookup`.** The REST layer shells out to `nslookup` to resolve service hostnames. The code still references Docker swarm DNS naming conventions (`tasks.vcell{SITE}_submit`) from before the migration to Kubernetes, though it works in practice because the hostname is overridden via the `vcell.submit.service.host` property. This indirection is unnecessary — Kubernetes service DNS is the standard mechanism.

5. **Collision-prone job IDs.** Job IDs are random integers in the range 0–999,999, generated with `new Random().nextInt(1000000)`. Collisions are possible.

6. **Legacy API.** The optimization endpoints live on the legacy Restlet-based `vcell-api` (`/api/v0/`), which is being phased out in favor of the Quarkus-based `vcell-rest` (`/api/v1/`). The legacy API has no OpenAPI spec and no auto-generated clients.

### What works well (and should not change)

- **The Python solver** (`pythonCopasiOpt/vcell-opt`) is correct and well-tested. It reads an `OptProblem` JSON file, runs COPASI parameter estimation, writes results to an output JSON file, and writes intermediate progress (iteration count, objective function value, best parameters) to a TSV report file. This file-based interface is clean and should be preserved exactly as-is.

- **SLURM submission** via `SlurmProxy.submitOptimizationJob()` works. The Singularity container, bind mounts, and SLURM configuration are all correct.

- **The shared NFS filesystem** (`/simdata`) is already mounted on both `vcell-rest` and `vcell-submit` pods, and the Python solver writes results there. This is the natural communication channel for results.

## Design

### Architecture overview

```
Desktop Client / webapp
        |
        | POST /api/v1/optimization  (OptProblem JSON)
        v
vcell-rest (Quarkus)
        | 1. Validate OptProblem
        | 2. Get bigint job ID from database sequence (newSeq)
        | 3. Write OptProblem JSON to NFS: /simdata/parest_data/CopasiParest_{id}_optProblem.json
        | 4. Insert vc_optjob row (status=SUBMITTED)
        | 5. Publish OptRequestMessage to Artemis "opt-request" queue (AMQP 1.0 via SmallRye)
        | 6. Return OptimizationJobStatus to client
        v
Artemis broker (artemismq:61616)
        | ANYCAST queue "opt-request"
        | Cross-protocol: AMQP 1.0 (vcell-rest) ↔ OpenWire JMS (vcell-submit)
        v
vcell-submit (OpenWire JMS via ActiveMQConnectionFactory)
        | 1. Consume OptRequestMessage from "opt-request" queue
        | 2. Read OptProblem file from NFS path in message
        | 3. Submit SLURM job via SlurmProxy.submitOptimizationJob()
        | 4. Publish OptStatusMessage to "opt-status" queue (QUEUED + htcJobId, or FAILED + error)
        v
SLURM → Singularity container → vcell-opt Python solver (UNCHANGED)
        | Writes to NFS:
        |   - CopasiParest_{id}_optReport.txt  (progress, written incrementally)
        |   - CopasiParest_{id}_optRun.json    (final results)
        v
Artemis broker
        | ANYCAST queue "opt-status"
        | vcell-submit publishes via OpenWire JMS
        | vcell-rest consumes via AMQP 1.0 (SmallRye @Incoming)
        v
vcell-rest (OptimizationMQ.consumeOptStatus)
        | Updates vc_optjob: status=QUEUED, htcJobId from message
        v
vcell-rest (polling on client request)
        | 1. Client polls GET /api/v1/optimization/{id} every 2 seconds
        | 2. Check vc_optjob status in database
        | 3. For any active status (SUBMITTED/QUEUED/RUNNING):
        |    a. Read progress from report file on NFS (CopasiUtils.readProgressReportFromCSV)
        |    b. If progress exists and status is SUBMITTED/QUEUED: auto-promote to RUNNING
        |    c. If result file exists: auto-promote to COMPLETE, read Vcellopt results
        | 4. Return OptimizationJobStatus with progress/results
        v
Client displays progress (objective function vs iteration graph, best parameter values)
```

### Cross-protocol messaging through Artemis

The optimization messaging uses **cross-protocol communication** through an Apache Artemis broker. This is a critical architectural detail that has been a source of bugs.

**Protocol mapping:**
- **vcell-rest** uses **AMQP 1.0** via Quarkus SmallRye Reactive Messaging (`quarkus-smallrye-reactive-messaging-amqp`)
- **vcell-submit** uses **OpenWire JMS** via ActiveMQ 5.x client (`org.apache.activemq.ActiveMQConnectionFactory`)
- **Artemis** accepts both protocols on port 61616 (all-protocol acceptor) and routes messages between them

**Queue configuration:**
- `opt-request` — ANYCAST queue. vcell-rest produces (AMQP 1.0), vcell-submit consumes (OpenWire JMS)
- `opt-status` — ANYCAST queue. vcell-submit produces (OpenWire JMS), vcell-rest consumes (AMQP 1.0)

**Critical SmallRye AMQP settings** (in `application.properties`):

```properties
mp.messaging.outgoing.publisher-opt-request.connector=smallrye-amqp
mp.messaging.outgoing.publisher-opt-request.address=opt-request
mp.messaging.outgoing.publisher-opt-request.capabilities=queue

mp.messaging.incoming.subscriber-opt-status.connector=smallrye-amqp
mp.messaging.incoming.subscriber-opt-status.address=opt-status
mp.messaging.incoming.subscriber-opt-status.capabilities=queue
```

**Lessons learned from deployment bugs:**

1. **`address` is required.** Without explicit `address=opt-request`, SmallRye defaults to using the channel name (`publisher-opt-request`) as the AMQP address. The OpenWire consumer listens on queue `opt-request`, so messages are lost silently. The broker accepts and acknowledges the message (the AMQP disposition shows `Accepted`), but no consumer ever sees it.

2. **`capabilities=queue` is required.** Artemis deploys queues as ANYCAST (point-to-point) by default, which is what OpenWire `session.createQueue()` creates. But SmallRye AMQP 1.0 consumers default to MULTICAST (pub-sub) semantics when attaching without specifying capabilities. This causes Artemis to create a separate MULTICAST subscription that never receives messages from the ANYCAST queue. The `capabilities=queue` annotation tells the AMQP client to attach with the "queue" capability, which Artemis interprets as ANYCAST.

3. **vcell-submit needs Artemis connection properties.** The `vcell.jms.artemis.host.internal` and `vcell.jms.artemis.port.internal` Java system properties must be set in the vcell-submit Dockerfile and K8s deployment. These are separate from the existing `activemqint` connection used for simulation job dispatch.

**K8s configuration** (in vcell-fluxcd `shared.env`):
```
jmshost_artemis_internal=artemismq
jmsport_artemis_internal=61616
```

These are consumed by:
- vcell-rest: `application.properties` `%prod.amqp-host=${jmshost_artemis_internal}` (AMQP connection)
- vcell-submit: `Dockerfile-submit-dev` `-Dvcell.jms.artemis.host.internal="${jmshost_artemis_internal}"` (OpenWire connection)

### Key design decisions

**Database-backed job tracking.** Every optimization job gets a row in `vc_optjob`. The database is the source of truth for job lifecycle state (SUBMITTED → QUEUED → RUNNING → COMPLETE/FAILED/STOPPED). This survives pod restarts, supports multiple API replicas, and provides an audit trail.

**Filesystem for data, database for state.** The OptProblem input, result output, and progress report are files on NFS — this matches the Python solver's file-based interface and avoids putting large blobs in the database. The database tracks job metadata and status; the filesystem holds the actual data.

**Artemis for job dispatch (cross-protocol).** vcell-rest sends a message to vcell-submit via Artemis to trigger SLURM submission, and vcell-submit sends a status message back. This replaces the persistent TCP socket with a durable message broker. The messages are small JSON payloads (job ID + file paths). The cross-protocol design (AMQP 1.0 ↔ OpenWire JMS) is necessary because vcell-rest uses Quarkus SmallRye AMQP while vcell-submit uses the ActiveMQ 5.x OpenWire client.

**Database-sequence job IDs.** Replace the random 0–999,999 integer with `bigint` keys from the shared `newSeq` database sequence, consistent with every other VCell table (`vc_biomodel`, `vc_simulation`, etc.). Uses the existing `KeyValue` type and `KeyFactory.getNewKey()` infrastructure.

**Progress reporting via filesystem polling.** The Python solver already writes a TSV report file incrementally as COPASI iterates. vcell-rest reads this file directly from NFS using `CopasiUtils.readProgressReportFromCSV()` (in `vcell-core`, already a dependency). This eliminates the batch server as a middleman for progress data. Each row contains: function evaluation count, best objective value, and best parameter vector.

**Filesystem-driven status promotion.** vcell-submit only sends a single `QUEUED` status message back after submitting the SLURM job. All subsequent status transitions are driven by vcell-rest reading the filesystem on each client poll:
- **SUBMITTED/QUEUED → RUNNING**: when the progress report file appears on NFS (meaning the SLURM job has started writing)
- **RUNNING → COMPLETE**: when the result output file appears on NFS (meaning the solver finished)

This design avoids the need for vcell-submit to monitor SLURM job state and send incremental status updates. The filesystem is the source of truth for solver progress, and the database tracks the lifecycle state.

**User-initiated stop via messaging.** When a user determines the optimization has sufficiently converged and stops the job, vcell-rest sends a stop message (with the SLURM job ID from the database) via Artemis to vcell-submit, which calls `killJobSafe()` → `scancel`. The report file retains all progress up to the kill point, so the client can read the best parameters found.

### Real-time progress reporting

The desktop client displays a real-time graph of objective function value vs. function evaluations, along with the current best parameter values. This updates every 2 seconds as the solver runs.

**How it works:**

1. The Python COPASI solver writes a TSV report file incrementally (`CopasiParest_{id}_optReport.txt`) with one row per sampled iteration. Format:
   ```
   ["k1","k2"]                    ← header: JSON array of parameter names
   10	0.5	1.0	2.0            ← numEvals, objFuncValue, param1, param2, ...
   20	0.1	1.3	2.4
   30	0.01	1.5	2.5
   ```

2. On each client poll, `OptimizationRestService.getOptimizationStatus()` reads this file via `CopasiUtils.readProgressReportFromCSV()`, which returns an `OptProgressReport` containing:
   - `progressItems`: list of `OptProgressItem` (numFunctionEvaluations, objFuncValue) — one per sampled iteration
   - `bestParamValues`: map of parameter name → best value found so far

3. The client receives the `OptProgressReport` in the `OptimizationJobStatus` response and dispatches it to `CopasiOptSolverCallbacks.setProgressReport()`, which fires a `PropertyChangeEvent` to the UI.

4. `RunStatusProgressDialog` (in `ParameterEstimationRunTaskPanel`) listens for these events and updates the objective function vs. evaluations plot and the best parameter table.

**Key design choice:** Progress is read from the filesystem on every poll, not sent through the message queue. This means progress is available as soon as the SLURM job starts writing, even before the `QUEUED` status message arrives from vcell-submit. The server auto-promotes the status from `SUBMITTED`/`QUEUED` → `RUNNING` when it detects progress data on disk. This replicates the behavior of the legacy socket-based design where every status query re-read the report file.

### Database schema

```sql
CREATE TABLE vc_optjob (
    id              bigint        PRIMARY KEY,
    ownerRef        bigint        REFERENCES vc_userinfo(id),
    status          varchar(32)   NOT NULL,
    optProblemFile  varchar(512)  NOT NULL,
    optOutputFile   varchar(512)  NOT NULL,
    optReportFile   varchar(512)  NOT NULL,
    htcJobId        varchar(128),
    statusMessage   varchar(4000),
    insertDate      timestamp     NOT NULL,
    updateDate      timestamp     NOT NULL
);
```

| Column | Purpose |
|--------|---------|
| `id` | Database sequence key (bigint, from `newSeq`) |
| `ownerRef` | User who submitted (for access control) |
| `status` | SUBMITTED, QUEUED, RUNNING, COMPLETE, FAILED, STOPPED |
| `optProblemFile` | NFS path to input OptProblem JSON |
| `optOutputFile` | NFS path to result Vcellopt JSON |
| `optReportFile` | NFS path to progress report TSV |
| `htcJobId` | SLURM job ID (set when vcell-submit confirms submission) |
| `statusMessage` | Error description on failure, cancellation reason, etc. |
| `insertDate` | When the job was created |
| `updateDate` | Last status transition |

Status transitions:
```
SUBMITTED → QUEUED → RUNNING → COMPLETE
                             → FAILED
                             → STOPPED (user-initiated)
```

### REST API

```
POST   /api/v1/optimization           Submit optimization job
GET    /api/v1/optimization            List user's optimization jobs
GET    /api/v1/optimization/{id}       Get job status, progress, or results
POST   /api/v1/optimization/{id}/stop  Stop a running job
```

**POST /api/v1/optimization** — Requires authenticated user. Accepts `OptProblem` JSON body. Writes input file to NFS, creates database record, publishes dispatch message to Artemis. Returns `OptimizationJobStatus` with the job ID and status=SUBMITTED.

**GET /api/v1/optimization** — Requires authenticated user. Returns array of `OptimizationJobStatus` for the user's jobs, most recent first. Lightweight (no progress/results).

**GET /api/v1/optimization/{id}** — Requires authenticated user (must be job owner). Returns `OptimizationJobStatus` which includes:
- `status` — current job state
- `progressReport` — (when RUNNING/QUEUED) iteration count, objective value, best parameters from the report file
- `results` — (when COMPLETE) the full `Vcellopt` result
- `statusMessage` — (when FAILED/STOPPED) error or cancellation description

**POST /api/v1/optimization/{id}/stop** — Requires authenticated user (must be job owner). Sends stop message to vcell-submit via Artemis, updates database status to STOPPED. The client can then GET the job to read the last progress report for the best parameters found.

### Response DTO

```java
public record OptimizationJobStatus(
    KeyValue id,
    OptJobStatus status,
    String statusMessage,
    String htcJobId,
    OptProgressReport progressReport,
    Vcellopt results
) {}
```

The client checks `status` and reads the appropriate nullable field. This avoids the current string-prefix parsing pattern (`"QUEUED:"`, `"RUNNING:"`, etc.).

### Message types

Shared in `vcell-core` (`org.vcell.optimization` package), serialized as JSON:

```java
public class OptRequestMessage {
    public String jobId;
    public String command;             // "submit" or "stop"
    public String optProblemFilePath;   // for submit
    public String optOutputFilePath;    // for submit
    public String optReportFilePath;    // for submit
    public String htcJobId;            // for stop (SLURM job to cancel)
}

public class OptStatusMessage {
    public String jobId;
    public OptJobStatus status;
    public String statusMessage;
    public String htcJobId;            // set when SLURM job is submitted
}
```

## Desktop client architecture (current)

The desktop client has a layered architecture for parameter estimation:

### UI layer

- **`ParameterEstimationPanel`** (`vcell-client/.../biomodel/ParameterEstimationPanel.java`) — Container panel with tabs for data, parameters, and run configuration.
- **`ParameterEstimationRunTaskPanel`** (`vcell-client/.../optimization/gui/ParameterEstimationRunTaskPanel.java`) — Main run panel. The `solve()` method (line 1126) dispatches an async task chain that calls `CopasiOptimizationSolverRemote.solveRemoteApi()`.
- **`RunStatusProgressDialog`** (inner class of `ParameterEstimationRunTaskPanel`, line 101) — Modal dialog showing real-time progress: number of evaluations, objective function value, and a log10(error) vs evaluations plot.

### Solver coordination layer

- **`CopasiOptimizationSolverRemote`** (`vcell-client/src/main/java/copasi/CopasiOptimizationSolverRemote.java`) — Orchestrates the remote optimization call. The `solveRemoteApi()` method:
  1. Converts `ParameterEstimationTask` → `OptProblem` via `CopasiUtils.paramTaskToOptProblem()`
  2. Converts to generated client model type and calls `optApi.submitOptimization(optProblem)`
  3. Polls `optApi.getOptimizationStatus(jobId)` every 2 seconds with a 200-second timeout
  4. Uses typed `OptimizationJobStatus` fields (`status`, `progressReport`, `results`) instead of string-prefix parsing
  5. Updates `CopasiOptSolverCallbacks` with progress via a pluggable `progressDispatcher` (SwingUtilities::invokeLater in GUI, Runnable::run in tests)
  6. Handles user stop via `optApi.stopOptimization(jobId)`

### Callback / event layer

- **`CopasiOptSolverCallbacks`** (`vcell-core/.../optimization/CopasiOptSolverCallbacks.java`) — Bridges the solver and the UI via `PropertyChangeListener`. Carries `OptProgressReport` (progress data) and `stopRequested` (user stop signal).

### API client layer

- **`OptimizationResourceApi`** (auto-generated in `vcell-restclient`) — Typed REST client for `/api/v1/optimization` endpoints. Generated from the OpenAPI spec via `tools/openapi-clients.sh`.

- **`VCellApiClient`** (`vcell-apiclient/.../api/client/VCellApiClient.java`) — Provides access to the generated `OptimizationResourceApi` via `getOptimizationApi()`.

## Implementation status

### Completed (parest-bug branch)

All commits listed below are on the `parest-bug` branch, ahead of `master`.

#### Commit 1: Database schema and service layer
- `OptJobTable` in `vcell-core` with Oracle-compatible SQL generation
- `OptimizationRestService` (`@ApplicationScoped`) with database CRUD and filesystem read methods
- `OptimizationJobStatus` record DTO, `OptJobRecord`, `OptJobStatus` enum
- Bigint job IDs from database sequence via `KeyFactory.getNewKey()`

#### Commit 2: REST endpoints
- `OptimizationResource.java` — submit, status, list, stop endpoints
- Authentication via `@RolesAllowed("user")`, ownership checks via user ID
- `POST /api/v1/optimization`, `GET /api/v1/optimization/{id}`, `GET /api/v1/optimization`, `POST /api/v1/optimization/{id}/stop`
- CodeQL path traversal fixes for optimization file paths

#### Commit 3: ActiveMQ messaging (vcell-rest side)
- `OptimizationMQ.java` — SmallRye AMQP producer (`publisher-opt-request`) and consumer (`subscriber-opt-status`)
- `OptRequestMessage` and `OptStatusMessage` shared types in `vcell-core`
- AMQP channel config in `application.properties` with `capabilities=queue` for ANYCAST routing
- Artemis broker connection: `%prod.amqp-host`, `%prod.amqp-port`

#### Commit 4: ActiveMQ messaging (vcell-submit side)
- `OptimizationBatchServer.initOptimizationQueue()` — OpenWire JMS listener on `opt-request` queue
- `handleSubmitRequest()` — reads OptProblem from NFS, submits to SLURM, sends QUEUED status back on `opt-status`
- `handleStopRequest()` — kills SLURM job via `HtcProxy.killJobSafe()`
- `sendStatusMessage()` — sends `OptStatusMessage` JSON on `opt-status` queue
- Path validation via `validateParestPath()` to prevent traversal
- Artemis connection configured via `vcell.jms.artemis.host.internal` / `vcell.jms.artemis.port.internal` system properties
- `Dockerfile-submit-dev` updated with Artemis env vars and `-D` flags

#### Commit 5: Tests

**Level 1: REST + DB + filesystem** (`OptimizationResourceTest.java`, `@Tag("Quarkus")`)
- 10 tests covering submit, status polling, completion detection, stop, authorization, error handling
- Uses testcontainers for PostgreSQL and Keycloak
- Simulates solver by writing mock report/output files to temp directory

**Level 1.5: E2E client flow** (`OptimizationE2ETest.java`, `@Tag("Quarkus")`)
- Tests the same code path as `CopasiOptimizationSolverRemote.solveRemoteApi()`
- Uses the generated `OptimizationResourceApi` client
- Mock vcell-submit directly updates database (bypasses messaging)
- Tests submit+poll+complete and submit+stop flows

**Level 2: Cross-protocol messaging** (`OptimizationCrossProtocolTest.java`, `@Tag("Quarkus")`)
- Full AMQP 1.0 ↔ OpenWire JMS round-trip through a real Artemis testcontainer
- `ArtemisTestResource` — `QuarkusTestResourceLifecycleManager` that starts Artemis with both AMQP (5672) and OpenWire (61616) ports
- `OpenWireOptSubmitStub` — mirrors `OptimizationBatchServer.handleSubmitRequest()` using `ActiveMQConnectionFactory` (same OpenWire protocol as production vcell-submit)
- Validates: address mapping, ANYCAST/MULTICAST routing, JSON serialization across protocols, filesystem handoff (stub reads OptProblem file written by vcell-rest)
- This test would have caught both production bugs (wrong AMQP address and MULTICAST subscription)

#### Commit 6: OpenAPI client regeneration
- Consolidated `tools/generate.sh` + `tools/compile-and-build-clients.sh` into `tools/openapi-clients.sh`
- Regenerated Java (`vcell-restclient`), Python (`python-restclient`), TypeScript (`webapp-ng`) clients
- New `OptimizationResourceApi` class with `submitOptimization()`, `getOptimizationStatus()`, `stopOptimization()`, `listOptimizationJobs()`

#### Commit 7: Desktop client update
- `CopasiOptimizationSolverRemote.solveRemoteApi()` rewritten to use generated `OptimizationResourceApi`
- Typed `OptimizationJobStatus` fields replace string-prefix parsing
- `POST /{id}/stop` replaces `bStop` query parameter
- Testable overload accepts `OptimizationResourceApi` and `Consumer<Runnable>` dispatcher directly (no Swing dependency)
- 200-second timeout, 2-second poll interval

#### Deployment fixes (discovered during dev deployment)
- AMQP address mapping: added `address=opt-request` / `address=opt-status` to production channel config (without this, SmallRye sent to channel name instead of queue name)
- ANYCAST routing: added `capabilities=queue` to all AMQP channel configs (without this, SmallRye created MULTICAST subscriptions that missed OpenWire messages)
- Artemis connection for vcell-submit: added `vcell.jms.artemis.host.internal` / `vcell.jms.artemis.port.internal` to `Dockerfile-submit-dev`
- JDBC resource leak: wrapped Statement/ResultSet in try-with-resources in `getOptJobRecord()` and `listOptimizationJobs()`

#### Progress reporting fix
- Server: `SUBMITTED` status now reads the progress report file (the SLURM job may already be running before the async QUEUED message arrives). Auto-promotes SUBMITTED/QUEUED → RUNNING when progress data appears on disk.
- Client: `SUBMITTED`/`QUEUED`/`RUNNING` all dispatch progress to `CopasiOptSolverCallbacks.setProgressReport()` when available, so the objective function graph and best parameter values update as soon as the solver starts writing.

### Remaining work

#### Deploy and validate on dev
- Deploy latest release to dev (vcell-dev.cam.uchc.edu)
- Test full round-trip: desktop client → vcell-rest → Artemis → vcell-submit → SLURM → NFS → vcell-rest → client
- Verify progress reporting works (client sees iteration updates)
- Verify stop works (SLURM job is cancelled, client gets last progress)
- Monitor for JDBC leak warnings (should be gone)

#### Commit 8: Remove legacy optimization code (deferred until new path validated)
- Delete `OptimizationRunServerResource.java` from `vcell-api`
- Delete `OptimizationRunResource.java` (interface) from `vcell-api`
- Delete `OptMessage.java` from `vcell-core`
- Remove socket server from `OptimizationBatchServer` (`initOptimizationSocket()`, `OptCommunicationThread`)
- Remove socket initialization from `HtcSimulationWorker.init()`
- Remove `submitOptimization()` / `getOptRunJson()` from `VCellApiClient`
- Remove optimization route registration from `VCellApiApplication.java`
- Remove port 8877 exposure from vcell-submit

#### Level 3 integration test (future)
- Full end-to-end test with SLURM (`@Tag("SLURM_IT")`) — requires NFS and SSH access to SLURM cluster
- Not run in CI; skipped if required system properties are missing
- Submits a small, fast-converging OptProblem with low max iterations
- Verifies real progress reports and optimized parameter values

#### Future improvements
- Consider migrating vcell-submit from ActiveMQ 5.x OpenWire client to Artemis JMS client (`jakarta.jms`) for protocol consistency
- Add dead letter and expiry address configuration for opt-request/opt-status queues in Artemis
- Add monitoring/alerting for optimization job failures
- Consider increasing the 200-second client timeout or making it configurable

## Key files

| File | Purpose |
|------|---------|
| `vcell-rest/.../handlers/OptimizationResource.java` | REST endpoints |
| `vcell-rest/.../services/OptimizationRestService.java` | DB CRUD and filesystem reads |
| `vcell-rest/.../activemq/OptimizationMQ.java` | AMQP 1.0 producer/consumer |
| `vcell-rest/src/main/resources/application.properties` | AMQP channel config |
| `vcell-server/.../batch/opt/OptimizationBatchServer.java` | OpenWire JMS listener, SLURM dispatch |
| `vcell-server/.../batch/sim/HtcSimulationWorker.java` | Starts opt queue listener in `init()` |
| `vcell-core/.../optimization/OptRequestMessage.java` | Request message type |
| `vcell-core/.../optimization/OptStatusMessage.java` | Status message type |
| `vcell-core/.../optimization/OptJobStatus.java` | Status enum |
| `vcell-core/.../modeldb/OptJobTable.java` | Database table definition |
| `vcell-client/.../copasi/CopasiOptimizationSolverRemote.java` | Desktop client solver |
| `docker/build/Dockerfile-submit-dev` | vcell-submit container with Artemis config |
| `tools/openapi-clients.sh` | OpenAPI client generation script |

## Decommissioning the legacy `/api/v0/optimization` endpoints

The legacy endpoints must remain available during a transition period because deployed desktop clients (already installed on user machines) will continue to call `/api/v0/optimization` until they update. The decommissioning plan:

### Phase 1: Parallel operation (CURRENT)

Both old and new endpoints are live. The new `/api/v1/optimization` endpoints are deployed and being validated on dev. The old `/api/v0/optimization` endpoints continue to work as before (same socket-based implementation, same bugs). The desktop client on the `parest-bug` branch uses the new API.

### Phase 2: Client migration (after dev validation)

Merge `parest-bug` to `master` and release. New client builds use the new API exclusively. Old client installs still use `/api/v0/`.

VCell uses a managed client update mechanism — when users launch the desktop client, it checks for updates and prompts to download the latest version. This means the transition window depends on how quickly users update.

### Phase 3: Deprecation monitoring

After the updated client is released:
- Add logging to the legacy `/api/v0/optimization` endpoints to track usage
- Monitor for a period (e.g., 2–4 weeks) to see if any clients are still hitting the old endpoints
- Communicate the deprecation via the VCell user mailing list if needed

### Phase 4: Removal (commit 8)

Once legacy endpoint usage drops to zero (or an acceptable threshold):
- Remove the legacy optimization endpoints, socket server, and related code
- Remove port 8877 from the vcell-submit Kubernetes service
- The `VCellApiClient` class itself is not deleted (it may still be used for other legacy endpoints), but its optimization methods are removed
