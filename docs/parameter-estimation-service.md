# Parameter Estimation Service

## Requirements

Parameter estimation allows VCell users to fit model parameters to experimental data. The service must satisfy the following requirements:

### Functional requirements

1. **Submit optimization job.** An authenticated user submits an `OptProblem` (SBML model, experimental data, parameter bounds, optimization method) and receives a job ID. The problem is dispatched to a SLURM cluster for execution using the COPASI optimization engine.

2. **Real-time progress reporting.** While the solver runs, the client receives periodic updates showing:
   - Objective function value vs. function evaluations (displayed as a graph)
   - Current best parameter values (displayed in a table)
   - Progress updates every ~2 seconds via client polling

3. **Retrieve results.** When optimization completes, the client retrieves the final optimized parameter values, objective function value, and the full progress history.

4. **Stop running job.** A user can stop an optimization mid-run. The best parameters found up to the stop point are returned to the client, allowing the user to accept partial results when convergence is sufficient.

5. **Job ownership.** Only the user who submitted a job can query its status, retrieve results, or stop it.

6. **List jobs.** A user can list their optimization jobs with current status.

### Non-functional requirements

7. **Survive pod restarts.** Job state must be persisted so that in-flight jobs are not lost when the REST service or submit service restarts. This is routine in Kubernetes.

8. **No single point of failure for job dispatch.** Communication between the REST service and the batch submission service must use a durable message broker, not in-memory connections.

9. **Collision-free job IDs.** Job identifiers must be unique, using database-sequence keys rather than random numbers.

10. **Auto-generated API clients.** The REST API must have an OpenAPI spec, and Java/Python/TypeScript clients must be auto-generated from it to stay in sync with the server.

11. **Shared filesystem for solver I/O.** The optimization problem input, progress report, and result output are files on a shared NFS mount — matching the Python solver's file-based interface. The database tracks metadata and status; the filesystem holds the data.

## Architecture

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
SLURM → Singularity container → vcell-opt Python solver
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

## Cross-protocol messaging through Artemis

The optimization messaging uses **cross-protocol communication** through an Apache Artemis broker.

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

**Important configuration notes:**

1. **`address` is required.** Without explicit `address=opt-request`, SmallRye defaults to using the channel name (`publisher-opt-request`) as the AMQP address. The OpenWire consumer listens on queue `opt-request`, so messages are lost silently.

2. **`capabilities=queue` is required.** Artemis deploys queues as ANYCAST by default. Without this, SmallRye AMQP creates a MULTICAST subscription that never receives messages from the ANYCAST queue.

3. **vcell-submit needs Artemis connection properties.** `vcell.jms.artemis.host.internal` and `vcell.jms.artemis.port.internal` must be set in the vcell-submit container. These are separate from the existing `activemqint` connection used for simulation job dispatch.

**K8s configuration** (in vcell-fluxcd `shared.env`):
```
jmshost_artemis_internal=artemismq
jmsport_artemis_internal=61616
```

## Key design decisions

**Database-backed job tracking.** Every optimization job gets a row in `vc_optjob`. The database is the source of truth for job lifecycle state (SUBMITTED → QUEUED → RUNNING → COMPLETE/FAILED/STOPPED). This survives pod restarts and supports multiple API replicas.

**Filesystem for data, database for state.** The OptProblem input, result output, and progress report are files on NFS — this matches the Python solver's file-based interface and avoids putting large blobs in the database.

**Database-sequence job IDs.** Bigint keys from the shared `newSeq` database sequence, consistent with every other VCell table. Uses the existing `KeyValue` type and `KeyFactory.getNewKey()`.

**Filesystem-driven status promotion.** vcell-submit only sends a single `QUEUED` status message back after submitting the SLURM job. All subsequent status transitions are driven by vcell-rest reading the filesystem on each client poll:
- **SUBMITTED/QUEUED → RUNNING**: when the progress report file appears on NFS
- **RUNNING → COMPLETE**: when the result output file appears on NFS

**COPASI progress flushing.** The Python solver uses `basico.assign_report(..., confirm_overwrite=False)` to ensure COPASI flushes progress lines incrementally during execution. Without this, COPASI buffers the entire report until completion, preventing real-time progress updates.

## Database schema

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

Status transitions:
```
SUBMITTED → QUEUED → RUNNING → COMPLETE
                             → FAILED
                             → STOPPED (user-initiated)
```

## REST API

```
POST   /api/v1/optimization           Submit optimization job
GET    /api/v1/optimization            List user's optimization jobs
GET    /api/v1/optimization/{id}       Get job status, progress, or results
POST   /api/v1/optimization/{id}/stop  Stop a running job
```

All endpoints require authentication (`@RolesAllowed("user")`) and enforce job ownership.

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

## Progress reporting

The desktop client displays a real-time graph of objective function value vs. function evaluations, along with the current best parameter values.

1. The Python COPASI solver writes a TSV report file incrementally (`CopasiParest_{id}_optReport.txt`):
   ```
   ["k1","k2"]                    ← header: JSON array of parameter names
   10	0.5	1.0	2.0            ← numEvals, objFuncValue, param1, param2, ...
   20	0.1	1.3	2.4
   30	0.01	1.5	2.5
   ```

2. On each client poll, `OptimizationRestService.getOptimizationStatus()` reads this file via `CopasiUtils.readProgressReportFromCSV()`, returning an `OptProgressReport` with `progressItems` and `bestParamValues`.

3. The client dispatches progress to `CopasiOptSolverCallbacks.setProgressReport()` via `SwingUtilities.invokeLater`, which fires a `PropertyChangeEvent` to the `RunStatusProgressDialog`.

## Desktop client architecture

### UI layer

- **`ParameterEstimationRunTaskPanel`** — Main run panel. `solve()` dispatches an async task chain calling `CopasiOptimizationSolverRemote.solveRemoteApi()`.
- **`RunStatusProgressDialog`** (inner class) — Modal dialog showing evaluations, objective value, and log10(error) vs evaluations plot.

### Solver coordination

- **`CopasiOptimizationSolverRemote`** — Orchestrates the remote call: converts `ParameterEstimationTask` → `OptProblem`, submits via generated API client, polls every 2 seconds (10-minute timeout), dispatches progress to callbacks, handles stop.

### Callback layer

- **`CopasiOptSolverCallbacks`** — Bridges solver and UI via `PropertyChangeListener`. Carries `OptProgressReport` and `stopRequested`.

### API client

- **`OptimizationResourceApi`** (auto-generated in `vcell-restclient`) — Typed REST client generated from the OpenAPI spec via `tools/openapi-clients.sh`.
- **`VCellApiClient.getOptimizationApi()`** — Factory method providing access to the generated client.

## Python solver (vcell-opt)

The COPASI parameter estimation solver runs as a Singularity container on the SLURM cluster.

- **Location:** `pythonCopasiOpt/vcell-opt/`
- **Dependencies:** `copasi-basico ^0.86`, `python-copasi ^4.45.298`, Python `^3.10`
- **Docker image:** `ghcr.io/virtualcell/vcell-opt:<tag>` (Dockerfile at `pythonCopasiOpt/Dockerfile`, Debian bookworm base)
- **Entry point:** `vcell_opt.optService.run_command(opt_file, result_file, report_file)`

The solver reads an `OptProblem` JSON, runs COPASI parameter estimation, writes results to `_optRun.json`, and writes incremental progress to `_optReport.txt`.

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
| `pythonCopasiOpt/vcell-opt/vcell_opt/optService.py` | Python COPASI solver |
| `tools/openapi-clients.sh` | OpenAPI client generation script |

## Tests

| Test | Level | What it covers |
|------|-------|----------------|
| `OptimizationResourceTest` (`@Tag("Quarkus")`) | REST + DB + filesystem | Submit, poll, completion, stop, authorization, errors. Testcontainers for PostgreSQL + Keycloak. |
| `OptimizationE2ETest` (`@Tag("Quarkus")`) | Client flow | Same code path as `CopasiOptimizationSolverRemote.solveRemoteApi()` using generated API client. |
| `OptimizationCrossProtocolTest` (`@Tag("Quarkus")`) | Cross-protocol messaging | Full AMQP 1.0 ↔ OpenWire JMS round-trip through real Artemis testcontainer with OpenWire stub. |
| `SlurmProxyTest` (`@Tag("Fast")`) | SLURM script generation | Verifies optimization SLURM job script matches expected fixture. |
| `vcellopt_test.py::test_incremental_report_writing` | Solver progress flushing | Multiprocessing test verifying COPASI flushes report file incrementally. |

## Future improvements

- Migrate vcell-submit from ActiveMQ 5.x OpenWire client to Artemis JMS client (`jakarta.jms`) for protocol consistency
- Add dead letter and expiry address configuration for opt-request/opt-status queues in Artemis
- Add monitoring/alerting for optimization job failures
- Make the 10-minute client timeout configurable
- Add Level 3 SLURM integration test (`@Tag("SLURM_IT")`) for full end-to-end validation with real SLURM
