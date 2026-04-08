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
        | 2. Generate UUID job ID
        | 3. Write OptProblem JSON to NFS: /simdata/parest_data/CopasiParest_{id}_optProblem.json
        | 4. Insert vc_optjob row (status=SUBMITTED)
        | 5. Send ActiveMQ message to vcell-submit
        | 6. Return OptimizationJobStatus to client
        v
ActiveMQ (activemqint)
        |
        v
vcell-submit
        | 1. Receive message
        | 2. Submit SLURM job via SlurmProxy (reuses existing code)
        | 3. Send status update message back (QUEUED + htcJobId, or FAILED + error)
        v
SLURM → Singularity container → vcell-opt Python solver (UNCHANGED)
        | Writes to NFS:
        |   - CopasiParest_{id}_optReport.txt  (progress, written incrementally)
        |   - CopasiParest_{id}_optRun.json    (final results)
        v
vcell-rest (polling on client request)
        | 1. Client polls GET /api/v1/optimization/{id}
        | 2. Check vc_optjob status in database
        | 3. If RUNNING: read progress from report file on NFS (CopasiUtils.readProgressReportFromCSV)
        | 4. If COMPLETE: read results from output file on NFS
        | 5. Return OptimizationJobStatus with progress/results
        v
Client displays progress or results
```

### Key design decisions

**Database-backed job tracking.** Every optimization job gets a row in `vc_optjob`. The database is the source of truth for job lifecycle state (SUBMITTED → QUEUED → RUNNING → COMPLETE/FAILED/STOPPED). This survives pod restarts, supports multiple API replicas, and provides an audit trail.

**Filesystem for data, database for state.** The OptProblem input, result output, and progress report are files on NFS — this matches the Python solver's file-based interface and avoids putting large blobs in the database. The database tracks job metadata and status; the filesystem holds the actual data.

**ActiveMQ for job dispatch (fire-and-forget).** vcell-rest sends a message to vcell-submit to trigger SLURM submission, and vcell-submit sends a status message back. This replaces the persistent TCP socket with a reliable message broker that already exists in the deployment (`activemqint`). The messages are small (job ID + file paths), and the pattern follows the existing `ExportRequestListenerMQ` in vcell-rest.

**UUID job IDs.** Replace the random 0–999,999 integer with UUIDs. No collision risk, no sequential enumeration.

**Progress reporting via filesystem polling.** The Python solver already writes a TSV report file incrementally as COPASI iterates. vcell-rest reads this file directly from NFS using `CopasiUtils.readProgressReportFromCSV()` (in `vcell-core`, already a dependency). This eliminates the batch server as a middleman for progress data. Each row contains: function evaluation count, best objective value, and best parameter vector.

**User-initiated stop via messaging.** When a user determines the optimization has sufficiently converged and stops the job, vcell-rest sends a stop message (with the SLURM job ID from the database) via ActiveMQ to vcell-submit, which calls `killJobSafe()` → `scancel`. The report file retains all progress up to the kill point, so the client can read the best parameters found.

### Database schema

```sql
CREATE TABLE vc_optjob (
    id              varchar(64)   PRIMARY KEY,
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
| `id` | UUID job identifier |
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
GET    /api/v1/optimization/{id}       Get job status, progress, or results
POST   /api/v1/optimization/{id}/stop  Stop a running job
```

**POST /api/v1/optimization** — Requires authenticated user. Accepts `OptProblem` JSON body. Writes input file to NFS, creates database record, sends dispatch message. Returns `OptimizationJobStatus` with the job ID and status=SUBMITTED.

**GET /api/v1/optimization/{id}** — Requires authenticated user (must be job owner). Returns `OptimizationJobStatus` which includes:
- `status` — current job state
- `progressReport` — (when RUNNING) iteration count, objective value, best parameters from the report file
- `results` — (when COMPLETE) the full `Vcellopt` result
- `statusMessage` — (when FAILED/STOPPED) error or cancellation description

**POST /api/v1/optimization/{id}/stop** — Requires authenticated user (must be job owner). Sends stop message to vcell-submit, updates database status to STOPPED. The client can then GET the job to read the last progress report for the best parameters found.

### Response DTO

```java
public record OptimizationJobStatus(
    String id,
    String status,
    String statusMessage,
    OptProgressReport progressReport,
    Vcellopt results
) {}
```

The client checks `status` and reads the appropriate nullable field. This avoids the current string-prefix parsing pattern (`"QUEUED:"`, `"RUNNING:"`, etc.).

## Desktop client architecture (current)

The desktop client has a layered architecture for parameter estimation:

### UI layer

- **`ParameterEstimationPanel`** (`vcell-client/.../biomodel/ParameterEstimationPanel.java`) — Container panel with tabs for data, parameters, and run configuration.
- **`ParameterEstimationRunTaskPanel`** (`vcell-client/.../optimization/gui/ParameterEstimationRunTaskPanel.java`) — Main run panel. The `solve()` method (line 1126) dispatches an async task chain that calls `CopasiOptimizationSolverRemote.solveRemoteApi()`.
- **`RunStatusProgressDialog`** (inner class of `ParameterEstimationRunTaskPanel`, line 101) — Modal dialog showing real-time progress: number of evaluations, objective function value, and a log10(error) vs evaluations plot.

### Solver coordination layer

- **`CopasiOptimizationSolverRemote`** (`vcell-client/.../copasi/CopasiOptimizationSolverRemote.java`) — Orchestrates the remote optimization call. The `solveRemoteApi()` method (line 27):
  1. Converts `ParameterEstimationTask` → `OptProblem` via `CopasiUtils.paramTaskToOptProblem()`
  2. Calls `apiClient.submitOptimization(optProblemJson)` → gets back optimization ID
  3. Polls `apiClient.getOptRunJson(optId, bStopRequested)` every ~2 seconds
  4. Parses string-prefixed responses (`"QUEUED:"`, `"RUNNING:"`, etc.) and raw JSON for `OptProgressReport` and `Vcellopt`
  5. Updates `CopasiOptSolverCallbacks` with progress, which fires property change events to the UI

### Callback / event layer

- **`CopasiOptSolverCallbacks`** (`vcell-core/.../optimization/CopasiOptSolverCallbacks.java`) — Bridges the solver and the UI via `PropertyChangeListener`. Carries `OptProgressReport` (progress data) and `stopRequested` (user stop signal).

### API client layer

- **`VCellApiClient`** (`vcell-apiclient/.../api/client/VCellApiClient.java`) — HTTP client for the legacy `/api/v0/` endpoints:
  - `submitOptimization()` (line 235) — POST to `/api/v0/optimization`, returns optimization ID as plain text
  - `getOptRunJson()` (line 219) — GET to `/api/v0/optimization/{id}?bStop={bStop}`, returns raw JSON string that the caller must parse by inspecting string prefixes

- **`ClientServerManager`** (`vcell-apiclient/.../server/ClientServerManager.java`) — Provides the `VCellApiClient` instance to the desktop client.

### What changes in the migration

The new auto-generated `OptimizationResourceApi` (from `vcell-restclient`) replaces `VCellApiClient` for optimization. The key improvements:

1. **Typed responses.** `getOptimizationStatus()` returns `OptimizationJobStatus` with explicit `status`, `progressReport`, and `results` fields — replacing the error-prone string-prefix parsing in `CopasiOptimizationSolverRemote`.
2. **Separate stop endpoint.** `POST /{id}/stop` replaces the `bStop` query parameter hack on the GET endpoint.
3. **Auto-generated.** The Java client is generated from the OpenAPI spec, so it stays in sync with the server automatically.

The UI layer (`ParameterEstimationRunTaskPanel`, `RunStatusProgressDialog`) and the callback layer (`CopasiOptSolverCallbacks`) are **unchanged** — they already consume `OptProgressReport` and `Vcellopt` objects, which are the same types the new API returns.

## Implementation plan

### Commit 1: Database schema and service layer

- Add `vc_optjob` table to `vcell-rest/src/main/resources/scripts/init.sql`
- Create `OptimizationJobStatus` DTO in `vcell-rest`
- Create `OptimizationRestService` (`@ApplicationScoped`) with database CRUD operations and filesystem read methods
- Oracle migration DDL script for production

### Commit 2: REST endpoints

- Create `OptimizationResource.java` in `vcell-rest/src/main/java/org/vcell/restq/handlers/`
- Endpoints: submit, status, stop
- Authentication and ownership checks
- Follow patterns from `SimulationResource.java`

### Commit 3: ActiveMQ messaging (vcell-rest side)

- Add AMQP channel configuration to `application.properties`
- Create `OptimizationMQProducer` for sending submit/stop commands
- Create `OptimizationMQConsumer` for receiving status updates from vcell-submit
- Update `vc_optjob` status on incoming messages

### Commit 4: ActiveMQ messaging (vcell-submit side)

- Add JMS queue listener in `HtcSimulationWorker` for optimization requests
- On "submit": call `SlurmProxy.submitOptimizationJob()` (existing code), send back QUEUED + htcJobId
- On "stop": call `killJobSafe()` with SLURM job ID from message
- Reuse existing `OptimizationBatchServer.submitOptProblem()` logic

### Commit 5: Tests (three levels)

Three levels of integration testing, each building on the previous:

#### Level 1: REST + DB + filesystem — `@Tag("Quarkus")`

Runs in CI. Uses testcontainers for PostgreSQL and Keycloak (already configured for other Quarkus tests). The ActiveMQ producer is mocked. Simulates solver completion by writing fake report/output files to a temp directory.

Create `OptimizationApiTest.java` (`@QuarkusTest`):
- Test submit: POST OptProblem, verify DB record created, verify OptProblem file written to disk
- Test status polling: write mock report file with progress rows, call GET, verify `OptimizationJobStatus` contains correct `OptProgressReport` (iteration count, objective value, best parameters)
- Test completion: write mock output file, call GET, verify status=COMPLETE with `Vcellopt` results
- Test stop: POST stop, verify DB status updated to STOPPED
- Test authorization: verify user can only access their own jobs
- Test error: verify FAILED status with `statusMessage` error description

#### Level 2: REST + DB + ActiveMQ round-trip — `@Tag("Quarkus")`

Runs in CI. Adds an ActiveMQ testcontainer. Both the vcell-rest producer and a test-harness consumer run in the same JVM. The test consumer simulates vcell-submit: it receives the submit message, writes mock result files to the temp directory, and sends a status update message back.

Create `OptimizationMQTest.java` (`@QuarkusTest`):
- Test submit → message received by consumer → status update sent back → DB updated to QUEUED with htcJobId
- Test stop → stop message received by consumer → DB updated to STOPPED
- Test failure → consumer sends FAILED status with error description → DB and GET endpoint reflect failure

This tests the full messaging contract between vcell-rest and vcell-submit without needing SLURM.

#### Level 3: Full end-to-end with SLURM — `@Tag("SLURM_IT")`

Does NOT run in CI. Requires a developer on the network with NFS mounted and SSH access to the SLURM cluster. Configured via system properties:

```
vcell.test.slurm.host                  SLURM login node (e.g. login.hpc.cam.uchc.edu)
vcell.test.slurm.user                  SSH user for SLURM submission (e.g. vcell)
vcell.test.slurm.keypath               Path to SSH private key (e.g. /path/to/id_rsa)
vcell.test.slurm.singularity.image     Path to vcell-opt Singularity image on cluster
vcell.test.nfs.parestdir               NFS path for optimization data, accessible from
                                       both test machine and SLURM (e.g. /simdata/parest_data)
```

The test is skipped (via JUnit `Assumptions.assumeTrue`) if any required property is missing.

Create `OptimizationSlurmIT.java` (`@QuarkusTest`):
- Uses testcontainers for PostgreSQL and ActiveMQ
- Includes a real submit-side handler (in-process) that SSHes to SLURM and submits the job using `SlurmProxy`
- Submits a small, fast-converging OptProblem (few parameters, few data points, low max iterations) to minimize SLURM runtime
- Polls the status endpoint with a generous timeout (5 minutes, to account for SLURM queue wait)
- Verifies that:
  - Progress reports arrive with real iteration counts and objective function values
  - The final result contains optimized parameter values
  - The parameter values are reasonable (within expected bounds)
- Tests user-initiated stop: submit a longer-running problem, poll until RUNNING with progress, stop, verify report file has partial progress

This test exercises the complete production path: REST → DB → ActiveMQ → SSH → SLURM → Singularity → Python/COPASI → NFS → filesystem polling → REST response.

### Commit 6: Regenerate OpenAPI clients

- Run `tools/generate.sh`
- Verify downstream: `mvn compile test-compile -pl vcell-rest -am`
- New `OptimizationResourceApi` class generated for Java, Python, TypeScript clients

### Commit 7: Update desktop client

- Modify `CopasiOptimizationSolverRemote.solveRemoteApi()` to use the auto-generated `OptimizationResourceApi` instead of `VCellApiClient`
- Replace string-prefix parsing with typed `OptimizationJobStatus` fields:
  - `status` field replaces parsing for `"QUEUED:"`, `"RUNNING:"`, `"FAILED:"` prefixes
  - `progressReport` field replaces JSON deserialization of embedded progress strings
  - `results` field replaces JSON deserialization of the final `Vcellopt`
- Use `POST /{id}/stop` for stop requests instead of the `bStop` query parameter on GET
- The UI layer (`ParameterEstimationRunTaskPanel`, `RunStatusProgressDialog`) and the callback layer (`CopasiOptSolverCallbacks`) require no changes — they already consume `OptProgressReport` and `Vcellopt` objects

### Commit 8: Remove legacy optimization code

- Delete `OptimizationRunServerResource.java` from `vcell-api`
- Delete `OptimizationRunResource.java` (interface) from `vcell-api`
- Delete `OptMessage.java` from `vcell-core`
- Remove socket server from `OptimizationBatchServer` (`initOptimizationSocket()`, `OptCommunicationThread`)
- Remove socket initialization from `HtcSimulationWorker.init()`
- Remove `submitOptimization()` / `getOptRunJson()` from `VCellApiClient`
- Remove optimization route registration from `VCellApiApplication.java`
- Refactor remaining `OptimizationBatchServer` methods into a focused `OptimizationJobSubmitter` class

### Commit 9: Kubernetes configuration

- Remove port 8877 from vcell-submit Service
- Verify NFS mount paths for vcell-rest pod include `parest_data` directory
- Update ingress if needed for new `/api/v1/optimization` route

## Decommissioning the legacy `/api/v0/optimization` endpoints

The legacy endpoints must remain available during a transition period because deployed desktop clients (already installed on user machines) will continue to call `/api/v0/optimization` until they update. The decommissioning plan:

### Phase 1: Parallel operation (commits 1–6)

Both old and new endpoints are live. The new `/api/v1/optimization` endpoints are deployed and functional. The old `/api/v0/optimization` endpoints continue to work as before (same socket-based implementation, same bugs). No client changes yet.

### Phase 2: Client migration (commit 7)

The desktop client is updated to call `/api/v1/optimization`. New client builds use the new API exclusively. Old client installs still use `/api/v0/`.

VCell uses a managed client update mechanism — when users launch the desktop client, it checks for updates and prompts to download the latest version. This means the transition window depends on how quickly users update.

### Phase 3: Deprecation monitoring

After the updated client is released:
- Add logging to the legacy `/api/v0/optimization` endpoints to track usage
- Monitor for a period (e.g., 2–4 weeks) to see if any clients are still hitting the old endpoints
- Communicate the deprecation via the VCell user mailing list if needed

### Phase 4: Removal (commit 8)

Once legacy endpoint usage drops to zero (or an acceptable threshold):
- Remove the legacy optimization endpoints, socket server, and related code
- Remove port 8877 from the vcell-submit Kubernetes service (commit 9)
- The `VCellApiClient` class itself is not deleted (it may still be used for other legacy endpoints), but its optimization methods are removed
