# `/api/v0` RPC and legacy resources: inventory

Status: analysis, 2026-11-27, against `master` at `1c336da65f` (prod runs the 8.1 line from
`vc8.1-prod`, `3d52748d2a`). Companion plan: [`api-v0-to-v1-migration-plan.md`](api-v0-to-v1-migration-plan.md).

This document describes what exists. It does not propose endpoint designs; those are deferred to
the design-agreement step in the plan.

Line numbers are for `master` at the commit above. "Desktop" means the Java client in client/server
mode (`vcell-client` + `vcell-apiclient`); standalone mode runs everything in-process and does not
touch `/api/v0`.

Security note: security-relevant issues are handled through the process in `SECURITY-FIXES.md`
(branch `docs/security-fixes`, #2239) and are not recorded in this inventory. Where security affects
the design, the plan and the style guide state the requirement (for example, the authorization rules
in the style guide §10).

---

## 1. Summary

- `/api/v0/rpc` is a generic Java-serialization tunnel. The desktop serializes
  `(destination queue, method name, Object[] args)`, the Restlet service in `vcell-api` forwards it
  over JMS (ActiveMQ Classic, `activemqint`) to one of three backend services, and the backend
  invokes the method by reflection and returns a Java-serialized result.
- The desktop's client stubs name **53 distinct methods** (25 data, 24 database, 4 dispatch; 55 with
  overloads). **46 are live** in the current desktop (23 data, 19 database, 4 dispatch); the other 7
  are dead stubs or only used by test harnesses.
- **v1 coverage of the 46 live methods:** 2 have usable `/api/v1` equivalents today
  (`startSimulation`, `stopSimulation`, both with semantic differences, issue #2184), 3 have partial
  ones (`getSimulationStatus`, `makeRemoteFile` for N5 only, `getSpecialUsers` via token roles),
  and **41 have none**. All 23 live data methods except N5 export have no v1 equivalent.
- The other big consumer was model CRUD, already moved to v1 in 2025 (7.7.0.26–7.7.0.36) and
  `getVCInfoContainer` in 2026-07 (8.0.2.01). The server still answers those methods over RPC for
  older installed clients.
- Besides `/rpc`, the desktop uses two other v0 resources: `/events` (status/export/data-job event
  polling) and `/swversion` (version check at login). Neither has a v1 equivalent.
- The remaining v0 resources are public HTML/JSON pages and downloads (`/biomodel…`,
  `/publication…`, `/simdata…`), the health check used by monitoring, admin reports, and legacy
  login/registration forms. External sites link to them heavily (ModelBricks and vcell.org pages:
  ~850 links each to `/api/v0/biomodel/{id}/diagram` and `/biomodel.vcml`), and crawlers drive the
  api pod's OOM crashes (#2106, the 2026-11-08 ingress outage).
- The riskiest areas to migrate: simulation-data reads (large binary payloads, `OutputContext`
  carrying user-defined functions, hot paths in the data viewer), `getTimeSeriesValues` and
  `makeRemoteFile` (asynchronous with results delivered over `/events`), and the event stream itself.
  The 3D field viewer adds a third consumer of the same data reads (section 9).

---

## 2. The transport

### 2.1 Request path

```
desktop                                vcell-api (Restlet, "api" pod)             backend (JMS consumer)
-------                                ------------------------------             ----------------------
Rpc{Data,Db,Sim}ServerProxy.rpc()      RpcRestlet.handle()  POST /api/v0/rpc      VCRpcMessageHandler
  -> RpcSender (RemoteProxyRpcSender)    auth: legacy bearer token                  -> VCRpcRequest.rpc(impl)
  -> VCellApiClient.sendRpcMessage()     guest allowlist on Method header             reflection on impl
     POST, body = deflate(Java ser.      deserialize body (JEP 290 filter)         reply: Java object or
     of VCellApiRpcBody)                 bind User args to the caller              exception, via temp queue
                                         special cases (status, special users)
                                         else RpcService.sendRpcMessage -> JMS
                                         reply re-serialized (deflate + Java ser.)
```

| Step | Where | Notes |
|---|---|---|
| Client stubs | `vcell-core/.../message/server/bootstrap/client/RpcDataServerProxy.java`, `RpcDbServerProxy.java`, `RpcSimServerProxy.java`, base `AbstractRpcServerProxy.java:65-69` | Build `VCRpcRequest(user, serviceType, method, args)`; timeout from `vcell.clientTimeoutMS`, default 120 s |
| Client transport | `vcell-apiclient/.../messaging/RemoteProxyVCellConnectionFactory.java:82-104` (`RemoteProxyRpcSender`) | Client-side guest check (`:86`), maps `VCellQueue` → `VCellApiRpcBody.RpcDestination` (`:89-98`) |
| HTTP | `vcell-apiclient/.../client/VCellApiClient.java:333-428` | `POST {v0}/rpc`, body `application/octet-stream` = deflate(Java serialization of `VCellApiRpcBody`); headers `username`, `destination`, `method`, `returnRequired`, `timeoutMS`, `compressed`, `class`, `Authorization: Bearer <legacy token>`, optional `specialProperties` (`:346-358`) |
| Envelope types | `vcell-api-types/.../rpc/VCellApiRpcBody.java`, `VCellApiRpcRequest.java` | `RpcDestination {DataRequestQueue, DbRequestQueue, SimReqQueue}` |
| Server entry | `vcell-api/.../rest/rpc/RpcRestlet.java:120-255`, routed at `VCellApiApplication.java:321` | See 2.2 |
| Forwarder | `vcell-api/.../rest/rpc/RpcService.java:25-36` | One producer session; timeout from server property (client `timeoutMS` header is ignored) |
| JMS send/receive | `vcell-server/.../message/jms/MessageProducerSessionJms.java:194-265` | Temp reply queue + correlation ID; replies over 100 KB (`jmsBlobMessageMinSize`) travel as files on shared storage (`:413-441`) |
| Backend dispatch | `vcell-core/.../message/VCRpcMessageHandler.java:19-76`, `VCRpcRequest.java:147-245` | Method resolved **by name and argument types over `getClass().getMethods()`** of the implementation (`:151`); exceptions are returned as the reply value (`VCRpcMessageHandler.java:36-39`) |
| Backend services | `SimDataServer.java:112` (`DataServerImpl`, `DataRequestQueue`, `data` pod), `DatabaseServer.java:102` (`DatabaseServerImpl`, `DbRequestQueue`, `db` pod), `SimulationDispatcher.java:742` (`SimulationServiceImpl`, `SimReqQueue`, `sched` pod) | Data service splits export traffic by the `DATAEXPORT` property (`SimDataServer.java:80-108`) |

### 2.2 What `RpcRestlet` does, in order

1. `GET` with `?stats=` returns `getBasicStatistics()` HTML for seven hard-coded user names
   (`RpcRestlet.java:122-150`). Duplicates `/api/v0/admin/stats`.
2. `POST`: authenticates with `AuthenticationPolicy.prohibitInvalidCredentials` (`:157`); a missing
   or invalid legacy token is a 401.
3. Guest check on the `Method` **header**, before the body is read (`:174-176`). Guest = the shared
   `vcellguest` account (#2220).
4. Deserializes the body with `CompressionUtils.fromCompressedSerialized(bytes, RPC_BODY_FILTER)`
   (`:179`). The JEP 290 filter (`:69-89`) allows `java.lang.*`, `java.util.**`, `java.math.*`,
   `java.time.**`, `java.io.File`, `cbit.**`, `org.vcell.**` and the model libraries VCell types
   hold, and rejects everything else; overridable with `-Dvcell.rpc.deserializationFilter`. Added
   2026-11-20 (`cb703da796`).
5. The body's method must equal the header's (`:185-187`). The destination comes from the body; the
   `Destination` header is only logged.
6. Special cases answered inside the api pod without JMS:
   - `getSimulationStatus` (any destination): `args[1]` is `KeyValue[]` or `KeyValue`, answered by
     `RestDatabaseService.getSimulationStatus` → `SimulationDatabaseDirect` (`:218-223`,
     `RestDatabaseService.java:124-130`). So the desktop's status polls never reach the dispatcher.
     The DB-queue variant (`RpcDbServerProxy.getSimulationStatus`, one argument) would fail here with
     an index error; it is only used by `HybridSolverTester`.
   - `getSpecialUsers` → `DatabaseServerImpl.getSpecialUsers` directly (`:224-225`).
   - Commented-out special cases for `getVCInfoContainer`, `getBioModelXML`, `getMathModelXML`
     (`:212-217`).
7. Otherwise `bindUserArguments` (`:102-118`) replaces every top-level `User` argument with the
   database's `SpecialUser` for the caller and refuses a different user, then
   forwards over JMS (`:227-234`). The backend checks again in `VCRpcRequest.verifyUserArguments`
   (`:137-145`).
8. The reply is re-serialized with `DTOModelTransformerV0.toCompressedSerialized`
   (`vcell-api-types/.../utils/DTOModelTransformerV0.java:321`) into one `byte[]` and returned 200.
   Errors are 500 with a text body, except a `ServerRejectedSaveException` cause, which is returned
   serialized so the client can rethrow it (`:239-253`; client side `VCellApiClient.java:453-475`).

### 2.3 Properties that matter for migration

- **Everything is Java serialization.** Requests and replies are whole domain object graphs
  (`CartesianMesh`, `SimDataBlock`, `ODESimData`, `ExportSpecs`, `OutputContext` with
  `AnnotatedFunction`s, `TestSuiteNew`, ...). Client and server must share class versions; a field
  change in any of these classes is a wire change. The client deserializes replies without a filter
  (`VCellApiClient.java:431-439`).
- **Whole payloads are buffered three times**: in the data service (serialize), on shared storage
  when over 100 KB (blob file), and in the api pod's heap (deserialize JMS reply, re-serialize, hold
  the byte array for the response). Large replies therefore load the api pod, which already has OOM
  history (#2106).
- **Feature detection by failure.** A newer client calling a method an older server lacks gets
  `No such method: ...` (`VCRpcRequest.java:213-223`); the FEniCSx bundle store relies on this to fall
  back (`RpcDataServerProxy.java:80`, commit `86dbff3cb1`). Any replacement needs an equivalent.
- **Fire-and-forget calls.** `makeRemoteFile` and background `getTimeSeriesValues` send with
  `returnRequired=false`; their result arrives later as an `ExportEvent` / `DataJobEvent` on the
  client status topic, which the desktop polls through `/api/v0/events`.
- **Two guest allowlists that must match, and do not.** Server `RpcRestlet.java:42-62` still allows
  `getVCInfoContainer`; client `RemoteProxyVCellConnectionFactory.java:50-70` has retired it. Both
  still list `getBioModelXML` / `getMathModelXML`, which the current client fetches over v1. Neither
  lists `getFunctions`, `getLineScan`, `getTimeSeriesValues`, VTU, Langevin, particle-block or FEniCSx
  calls, so a guest cannot use those viewers (not verified in a running client).
- **Auth is a legacy token.** v0 validates VCell-issued API access tokens against the database
  (`BearerTokenVerifier`, wired at `VCellApiApplication.java:350-356`); the desktop obtains one from v1
  (`POST /api/v1/users/bearerToken` or `/guestBearerToken`,
  `RemoteProxyVCellConnectionFactory.java:197-200`). Issue #1979 asks whether v0 bearer auth should
  survive at all.
- **JMS dependency.** `vcell-rest` has no `javax.jms` client in production; it talks to Artemis over
  AMQP only (`vcell-rest/pom.xml:255-280`, test-scoped JMS jars only). The RPC queues live on
  ActiveMQ Classic (`activemqint`). See `docs/MESSAGING.md` §7 for the Artemis direction and its open
  RPC questions (temp-queue reply routing, selectors).

### 2.4 Dead code in the transport

- `vcell-api/.../rest/rpc/RpcDbServerProxy.java` has no callers. `vcell-api`'s own
  `RpcDataServerProxy` and `RpcSimServerProxy` are used by `RestDatabaseService` for the non-RPC
  `/simdata`, `/simstatus`, `/simtask` and start/stop resources (`RestDatabaseService.java:222,253,295,329,809,874`).
- `RpcDbServerProxy` (vcell-core) `deleteGeometry` (`:41`), `deleteResultSetExport` (`:45`),
  `getAllReferences` (`:58`), `saveSimulation` (`:156`), and both one-argument
  `getSimulationStatus` overloads (`:86,90`) have no desktop caller.

---

## 3. RPC method inventory

Columns: **Args → return** lists the Java types on the wire (all Java-serialized; "domain" marks a
VCell domain object graph rather than primitives/arrays/strings). **Callers** are the main desktop
call sites; almost all data calls go through `VCDataManager` (`vcell-core`). **G** = in the guest
allowlist. **v1** = existing `/api/v1` equivalent. **Size** = typical reply size class. **Diff** =
migration difficulty (L/M/H), mostly driven by payload shape and statefulness.

### 3.1 Data service (`DataRequestQueue` → `DataServerImpl`, `data` pod)

Client stub lines refer to `vcell-core/.../bootstrap/client/RpcDataServerProxy.java`; server lines to
`vcell-core/.../simdata/DataServerImpl.java`. Every data method takes a `VCDataIdentifier` (domain:
`VCSimulationDataIdentifier` sim key + owner + job index, or field-data / merged-data identifiers).

| # | Method (stub / impl line) | Args → return | Purpose | Callers | G | v1 | Size | Diff |
|---|---|---|---|---|---|---|---|---|
| D1 | `getDataIdentifiers` (38 / 90) | `OutputContext`, `VCDataIdentifier` → `DataIdentifier[]` (domain) | Variables and functions in a dataset | PDE/ODE data managers, `PDEDataViewer`, exporters, `FieldViewerServer:2266` | yes | none | small | M (`OutputContext` carries user functions) |
| D2 | `getDataSetTimes` (47 / 105) | id → `double[]` | Saved time points | everywhere; field viewer on every route | yes | none | small | L |
| D3 | `getFunctions` (52 / 124) | `OutputContext`, id → `AnnotatedFunction[]` (domain, expressions) | Functions of the dataset | `PDEDataManager`, `ODEDataManager`, `PDEDataViewer` | no | none | small | M |
| D4 | `getLineScan` (57 / 144) | `OutputContext`, id, var, time, `SpatialSelection` (domain) → `PlotData` | Line/curve profile | `PDEDataViewer`, `ImageContainerPanelTool` | no | none | medium | H (selection is mesh-bound domain object) |
| D5 | `getMesh` (61 / 162) | id → `CartesianMesh` (domain) | Mesh, regions, membranes | data viewers, exporters, field viewer `/info`,`/grid` | yes | none | medium–large (membrane-rich meshes) | H (needs a mesh representation) |
| D6 | `getODEData` (65 / 180) | id → `ODESimData` (domain) | Whole non-spatial result table | `ODEDataManager`, `ASCIIExporter` | yes | none | up to tens of MB | M |
| D7 | `getLangevinBatchResultSet` (69 / 190) | id → `LangevinBatchResultSet` | SpringSaLaD batch statistics | `ODEDataManager` | no | none | medium–large | M |
| D8 | `getLangevinTrajectory` (73 / 200) | id → `SpringSaladTrajectory` | SpringSaLaD viewer file | `ODEDataManager`, `SimResultsViewer` | no | none | large | M |
| D9 | `getFenicsBundleFile` (77 / 210) | id, relative path → `byte[]` | One file of a FEniCSx bundle (path-contained, `DataSetControllerImpl.java:2545-2575`) | `DataServerBundleStore` (field viewer) | no | none | per file | L |
| D10 | `getFenicsBundleLocatedSamples` (82 / 221) | id, mesh path, coords path, `String[]`, `double[] points`, snap, `int[] rows` → `FenicsLocatedSamples` | Locate points in a moving mesh and sample (commit `7e78a552a5`, **not on prod 8.1**) | `DataServerBundleStore` | no | none | capped | M |
| D11 | `getFenicsBundleSamples` (87 / 232) | id, `String[]`, `int[] indices`, `int[] rows` → `FenicsSamples` | Values at given vertices/rows; ≤1 M values (8 MB) per reply, answered as a prefix (`cab64b4cde`) | `DataServerBundleStore` | no | none | ≤8 MB | M |
| D12 | `getFenicsBundleFiles` (92 / 243) | id, `String[]` → `byte[][]` | Batch file read; ≤256 paths, prefix once 8 MB (`86dbff3cb1`) | `DataServerBundleStore` | no | none | ≤8 MB | L |
| D13 | `getParticleDataBlock` (96 / 259) | id, time → `ParticleDataBlock` | Particle positions at a time | data viewer, field viewer `/particles` | no | none | medium | M |
| D14 | `getParticleDataExists` (100 / 320) | id → `boolean` | Has particle output | viewers | yes | none | tiny | L |
| D15 | `getSimDataBlock` (105 / 336) | `OutputContext`, id, var, time → `SimDataBlock` (`double[]` over the mesh) | One variable at one time — the data viewer's hot path | ~25 sites; field viewer `/field` | yes | none | 8 B × mesh size per call | M (binary array) |
| D16 | `getTimeSeriesValues` (109,111 / 358) | `OutputContext`, id, `TimeSeriesJobSpec` → `TimeSeriesJobResults`; background variant returns nothing and delivers a `DataJobEvent` | Point/region time series, statistics | data viewer, field viewer `/timeseries`,`/stats`,`/kymograph` | no | none | variable, can be large | H (async + events) |
| D17 | `makeRemoteFile` (127 / 375) | `OutputContext`, `ExportSpecs` (domain) → `ExportEvent`; sent `returnRequired=false` with `DATAEXPORT` property | Server-side export (CSV, images, movies, HDF5, …); progress via events | `ClientExportController`, `PDEExportDataPanel` | no | partial: N5 only, `POST /api/v1/export/N5` (`LocalDataSetControllerMessaging.java:387-395`) | n/a (async) | H |
| D18 | `doDataOperation` (42 / 275) | `DataOperation` (read-only post-processing subclasses) → `DataOperationResults` | Post-processing data (FRAP, image ROI stats) | `PDEDataViewerPostProcess`, FRAP panels | yes | none | medium | M |
| D19 | `getDataSetMetadata` (142 / 387) | id → `DataSetMetadata` | Variable metadata | **no desktop caller**; used inside vcell-api for `GET /api/v0/simdata/{id}` | no | none | small | L |
| D20 | `getDataSetTimeSeries` (147 / 397) | id, `String[]` → `DataSetTimeSeries` | Whole-domain time series | **no desktop caller**; used by public `GET /api/v0/simdata/{id}/jobindex/{n}` (scraped) | no | none | large | M |
| D21 | `getEmptyVtuMeshFiles` (169 / 524) | id, time index → `VtuFileContainer` | VTU mesh for Chombo / MovingBoundary | `VtkManager`, field viewer `/grid` (VTU) | no | none | large | M |
| D22 | `getVtuMeshData` (174 / 502) | `OutputContext`, id, `VtuVarInfo`, time → `double[]` | Values on the VTU mesh | `VtkManager`, field viewer VTU routes | no | none | medium | M |
| D23 | `getVtuVarInfos` (180 / 547) | `OutputContext`, id → `VtuVarInfo[]` | Variables of a VTU dataset | `VtkManager`, field viewer `/info` (VTU) | no | none | small | L |
| D24 | `getVtuTimes` (186 / 484) | id → `double[]` | Times of a VTU dataset | `VtkManager` | no | none | small | L |
| D25 | `getNFSimMolecularConfigurations` (193 / 569) | id → `NFSimMolecularConfigurations` | NFSim species output | `ODEDataManager`, `OutputSpeciesResultsPanel` | yes | none | medium | M |

Live from the desktop: 23 (D19, D20 are only called server-side inside vcell-api). The client-side
facade is `vcell-apiclient/.../messaging/LocalDataSetControllerMessaging.java`; field-data operations
in the same class already go to v1 (`:58-105`).

### 3.2 Database service (`DbRequestQueue` → `DatabaseServerImpl`, `db` pod)

Stub lines: `vcell-core/.../bootstrap/client/RpcDbServerProxy.java`; impl lines:
`vcell-server/.../modeldb/DatabaseServerImpl.java`; client facade
`vcell-apiclient/.../messaging/LocalUserMetaDbServerMessaging.java` (LUMDSM).

| # | Method (stub / impl) | Args → return | Purpose | Callers | G | v1 | Diff |
|---|---|---|---|---|---|---|---|
| B1 | `getSpecialUsers` (32 / 99) | — → `TreeMap<SPECIAL_CLAIM, TreeMap<User,String>>` | Who has admin/powerUser/publicationEditor/support claims; gates UI features | `SimulationWorkspace:817`, `SolverTaskDescriptionAdvancedPanel:1129` (via LUMDSM:65); **answered inside RpcRestlet** | no | partial: Auth0 roles claim (`CustomSecurityIdentityAugmentor.java:39-66`), `GET /users/me` | L |
| B2 | `curate` (36 / 121) | `CurateSpec` → `VCDocumentInfo` (domain) | Archive / publish a model | `ClientRequestManager`, `ClientDocumentManager` (LUMDSM:77) | no | none (publications `PUT /publications/{id}/publish` is a different, curator-only operation) | M |
| B3 | `deleteGeometry` (41 / 242) | key → void | — | **dead stub**; client uses `DELETE /api/v1/geometry/{id}` | no | yes | — |
| B4 | `deleteResultSetExport` (45 / —) | key → void | — | **no caller** | no | none | — |
| B5 | `doTestSuiteOP` (50 / 270) | `TestSuiteOP` → `TestSuiteOPResults` | Numerics testing framework (admin) | `TestingFrameworkWindowManager`, `DatabaseWindowManager` | no | none | H, or drop |
| B6 | `findReferences` (54 / 290) | `ReferenceQuerySpec` → `ReferenceQueryResult` | "What uses this" before deleting a geometry / field data / reaction | `DatabaseWindowManager`, `FieldDataWindowManager`, `ReactionCartoonTool` | no | none | M |
| B7 | `getAllReferences` (58 / 314) | `VersionableType`, key → `VersionableFamily` | — | only `DependencyTest` (a dev tool in vcell-server main) | no | none | — |
| B8 | `getBoundSpecies` (62 / 479) | `DBFormalSpecies` → `DBSpecies` | Species dictionary | `ClientDocumentManager` | no | none | M |
| B9 | `getDatabaseSpecies` (66 / 497) | like-string, flags, type, limits → `DBFormalSpecies[]` | Species search | `DatabaseSearchPanel`, `SpeciesQueryPanel` | no | none | M |
| B10 | `getDictionaryReactions` (70 / 525) | `ReactionQuerySpec` → `ReactionDescription[]` | Reaction dictionary search | `DBReactionWizardPanel` | no | none | M |
| B11 | `getPreferences` (74 / 677) | — → `Preference[]` | User preferences at login | `ClientServerManager`, `ClientDocumentManager` | yes | none | L |
| B12 | `getReactionStepAsModel` (78 / 691) | key → `String` (VCML) | Import a reaction from the DB | `DBReactionWizardPanel` | no | none | L |
| B13 | `getReactionStepInfos` (82 / 711) | `KeyValue[]` → `ReactionStepInfo[]` | Reaction metadata | `ClientDocumentManager` | no | none | M |
| B14 | `getSimulationStatus` ×2 (86, 90 / 738, 775) | `KeyValue[]` / `KeyValue` → `SimulationStatusPersistent` | — | only `HybridSolverTester`; broken through RpcRestlet (2.2 item 6) | no | — | — |
| B15 | `getSimulationXML` (94 / 790) | key → `BigString` (VCML) | Load one simulation (e.g. from the job list) | `ClientDocumentManager.java:1186-1191` | no | none | L |
| B16 | `getTestSuite` (98 / 812) | `BigDecimal` → `TestSuiteNew` (domain) | Testing framework | testing-framework UI | no | none | H, or drop |
| B17 | `getTestSuiteInfos` (103 / 835) | — → `TestSuiteInfoNew[]` | Testing framework | testing-framework UI | no | none | M, or drop |
| B18 | `getUserReactionDescriptions` (107 / 854) | `ReactionQuerySpec` → `ReactionDescription[]` | User's reactions search | `DBReactionWizardPanel` | no | none | M |
| B19 | `groupAddUser` (111 / 989) | `VersionableType`, key, user name, hidden → `VersionInfo` (domain) | Share a document with a user | `ClientDocumentManager` (permissions dialog) | no | none | M |
| B20 | `groupRemoveUser` (115 / 1015) | same → `VersionInfo` | Unshare | same | no | none | M |
| B21 | `groupSetPrivate` (119 / 1042) | `VersionableType`, key → `VersionInfo` | Make private | same | no | none | M |
| B22 | `groupSetPublic` (123 / 1068) | same → `VersionInfo` | Make public | same | no | none | M |
| B23 | `replacePreferences` (127 / 1130) | `Preference[]` → void | Save preferences | `UserPreferences`, `ClientDocumentManager` | no | none | L |
| B24 | `saveSimulation` (156 / 1298) | `BigString`, boolean → `BigString` | — | **no desktop caller** (v0 `/biomodel/../simulation/../save` uses the DB directly) | no | none | — |

Live: 19 (B1, B2, B5, B6, B8–B13, B15–B23). B19–B22 together are the document-sharing model;
B8–B10, B12, B13, B18 are the species/reaction dictionary; B5, B16, B17 are the admin testing
framework.

**Retired from the desktop but still answered by the server** (for clients older than the versions
shown): `getBioModelXML`, `getBioModelInfo(s)`, `saveBioModel(As)`, `deleteBioModel` (v1 since
7.7.0.26); `getMathModelXML/Info(s)`, `saveMathModel(As)`, `deleteMathModel`,
`getGeometryXML/Info(s)`, `saveGeometry(As)`, `deleteGeometry`, `getVCImageXML/Info(s)`,
`saveVCImage(As)`, `deleteVCImage`, field-data operations (v1 by 7.7.0.28–7.7.0.36);
`getVCInfoContainer` (v1 since 8.0.2.01). Tags from `git tag --contains` on `67af4b9577`,
`1d42dd9a7a`, `396d7f2e3b`, `61eb37749e`. Every one of these has a v1 equivalent.

### 3.3 Dispatch service (`SimReqQueue` → `SimulationDispatcher.SimulationServiceImpl`, `sched` pod)

Stubs: `vcell-core/.../bootstrap/client/RpcSimServerProxy.java`; impl
`vcell-server/.../dispatcher/SimulationDispatcher.java:111-221`; client facade
`LocalSimulationControllerMessaging.java`.

| # | Method (stub / impl) | Args → return | Purpose | Callers | G | v1 | Diff |
|---|---|---|---|---|---|---|---|
| S1 | `startSimulation` (69 / 141) | `User`, `VCSimulationIdentifier`, `int` scan jobs → `SimulationStatus` | Run on the cluster | `ClientJobManager`, `ClientRequestManager` | no | `POST /api/v1/Simulation/{simID}/startSimulation` — takes the scan count from the DB, returns `StatusMessage[]`, uses a copied state machine (#2184) | M |
| S2 | `stopSimulation` (83 / 114) | `User`, `VCSimulationIdentifier` → `SimulationStatus` | Stop; also kills batch jobs (`:128-135`) | `ClientJobManager`, `ViewJobsPanel` | no | `POST .../stopSimulation` — does not kill batch jobs itself (unverified whether sched does it on its next pass) | M |
| S3 | `getSimulationStatus` ×2 (94,105 / 170,187) | `User`, `KeyValue[]` or `KeyValue` → `SimulationStatus` | Status of a model's sims (on open, polling) | `ClientDocumentManager.java:1154,1690`, `ClientSimManager` | yes | partial: `GET /api/v1/Simulation/{simID}/simulationStatus?bioModelID=` — one sim, needs a BioModel id, `@RolesAllowed("user")`, ignores `mathModelID` (`SimulationResource.java:56-72`, `SimulationRestService.java:112-117`). **Never reaches the dispatcher** (2.2 item 6) | L |
| S4 | `getSimpleJobStatus` (115 / 205) | `User`, `SimpleJobStatusQuerySpec` → `SimpleJobStatus[]` | "Simulation jobs" dialog | `ViewJobsPanel` (`DocumentWindow.java:1993`) | no | none (v0 REST `/simtask` and `/admin/jobs` exist) | M |

### 3.4 Totals

| Destination | Stub methods | Live from desktop | Usable v1 | Partial v1 | No v1 |
|---|---|---|---|---|---|
| Data | 25 | 23 | 0 | 1 (D17, N5 only) | 22 |
| Database | 24 | 19 | 0 (B3 has one but is dead) | 1 (B1) | 18 |
| Dispatch | 4 | 4 | 2 (S1, S2) | 1 (S3) | 1 |
| **Total** | **53** | **46** | **2** | **3** | **41** |

Prod (`vc8.1-prod`) has the same set minus D9–D12 (FEniCSx); verified with `git grep` on the
branch's `LocalDataSetControllerMessaging` / `LocalUserMetaDbServerMessaging`.

---

## 4. Other `/api/v0` resources

Routed in `vcell-api/.../rest/VCellApiApplication.java:280-331`, under prefix `/api/v0`
(`vcell.serverPrefix.v0`). Ingress sends `/api/v0/` to the `api` service (vcell-fluxcd
`kustomize/overlays/*/vcell-ingress.yaml`). Auth policy: "optional" = `ignoreInvalidCredentials`
(anonymous gets public data), "required" = `prohibitInvalidCredentials`.

| Path | Class | Auth | Users | v1 equivalent |
|---|---|---|---|---|
| `/swversion`, `/` (and bare `/swversion` outside the prefix) | `SWVersionRestlet` | none | **desktop** login version check (`VCellApiClient.java:441-451`, `ClientServerManager.java:299-330`) | none |
| `/events?beginTimestamp=` | `EventsRestlet`, `RestEventService` | required | **desktop** `AsynchMessageManager` poll: sim-job status, export, data-job, broadcast events | partial: export events only, `GET /api/v1/export/status` (and hidden SSE `/export/{id}/status-sse`) |
| `/rpc` | `RpcRestlet` | required | desktop | section 3 |
| `/access_token` | `AuthenticationTokenRestlet` | user+password in query string | pre-Auth0 clients | Auth0; `POST /api/v1/users/bearerToken` mints legacy tokens |
| `/login`, `/loginform`, `/logout`, `/registrationform`, `/newuser`, `/newuserverify`, `/lostpassword`, `/contactus`, `/auth/user` | `users/*` restlets, cookie authenticator | form / cookie | browsers using the v0 HTML pages; `/lostpassword` refuses since `c1fc88cd00` | Auth0 + `/api/v1/users/*` |
| `/biomodel` (HTML/JSON list) | `BiomodelsServerResource` | optional | public web, linked from webapp-ng home (`home-content.component.html:46`), crawlers | `GET /api/v1/bioModel/summaries` (different shape) |
| `/biomodel/{id}` | `BiomodelServerResource` | optional | public web, publication list links | `GET /api/v1/bioModel/{id}` |
| `/biomodel/{id}/biomodel.vcml` | `BiomodelVCMLServerResource` | optional | ~850 external links (ModelBricks, vcell.org), pyvcell-era scripts | `GET /api/v1/bioModel/{id}/vcml_download` |
| `/biomodel/{id}/biomodel.sbml`, `.omex`, `.bngl` | `Biomodel{SBML,OMEX,BNGL}ServerResource` | optional | public web, crawlers (ingress rate-limits sbml/omex on prod) | **none**: v1 `sbml_download`, `omex_download`, `bngl_download` exist but return 501 and are hidden (`BioModelResource.java:129-152`); #2220 lists them as working, which is wrong |
| `/biomodel/{id}/diagram` | `BiomodelDiagramServerResource` | optional | ~850 external links | **none** (v1 `diagram_download` is a 501 stub, `:154-160`) |
| `/modelbrick` | `BiomodelVCMLModelInfoResource` | none | ModelBricks | none |
| `/biomodel/{id}/simulation/{simid}` | `BiomodelSimulationServerResource` | optional | public web; crawlers (parses the full model; the 62 MP geometry OOM, 2026-11) | none |
| `/biomodel/{id}/simulation/{simid}/save`, `/startSimulation`, `/stopSimulation` | `BiomodelSimulation{Save,Start,Stop}ServerResource` | required | old v0 web UI / scripts (no in-repo caller found) | start/stop: yes; save-with-overrides: none |
| `/simstatus`, `/simtask`, `/simtask/{id}` | `SimulationStatusServerResource`, `SimulationTasks…` | optional / required | old AngularJS pages in `vcell-api/webapp/`, scripts | none |
| `/simdata/{simid}`, `/simdata/{simid}/jobindex/{n}` | `SimDataServerResource`, `SimDataValuesServerResource` (`RestDatabaseService.java:259-335`) | optional | public; crawlers (issue #1967: 904 of 907 requested sims had no data); a distributed scraper recently hammered `jobindex` | none |
| `/publication`, `/publication/{id}` | `Publication(s)ServerResource` | optional | public web, vcell.org links | `GET /api/v1/publications`, `/publications/{id}` |
| `/health?check=sim\|all` | `HealthRestlet`, `HealthService` | none | Nagios / Better Stack canary (`docs/sim-monitor-warning-2026-06-01.md`); `HealthService` saves and runs a canary model through `DatabaseServerImpl` and the dispatcher | none |
| `/admin/stats` | `AdminStatsRestlet` | required, admin claim | admins | `GET /api/v1/admin/usage` (PDF, different report; `@RolesAllowed("user")`) |
| `/admin/jobs` | `AdminJobsRestlet` | — | `docker/swarm/vcell-admin` CLI (`cli.py:184-189`), `docker/README.md:98` | none |
| `/n5data/{simid}/export`, `/n5data/{simid}/info` | `N5ExportRestlet`, `N5GetInfoRestlet` | optional | none found; `N5Service.exportToN5` body is commented out | `POST /api/v1/export/N5` |
| `/scripts`, `/webapp` | static directories | none | old AngularJS UI (`vcell-api/webapp/`) | webapp-ng |

Other observations:

- `webapp-ng/src/app/components/publication-list/publication-list.component.html:69` links to
  `/api/v0/mathmodel/{key}`, which has no route in `VCellApiApplication` (no `mathmodel` path). The
  link is dead.
- `vcell-api/vcellapi.yaml` is a Swagger 2.0 description of v0 "as of VCell 6.2 build 23"; stale.
- `RestEventService` keeps events in a static `ConcurrentLinkedDeque` that appears never to be pruned
  except for broadcast messages (`RestEventService.java:89-108,159`), and each replica stamps events
  with its own clock. Pod restarts (frequent on prod, #2106) mask the growth. Not measured.
- `HealthService`, `RestEventService` (client-status topic subscriber) and `AdminService` live in the
  api pod (`VCellApiMain`), so retiring the Restlet service means rehoming them, not only the routes.

---

## 5. Existing `/api/v1` (vcell-rest)

Spec: `tools/openapi.yaml` (OpenAPI 3.0.3, `info.version 1.0.1`), generated by SmallRye from
`vcell-rest`; clients generated with OpenAPI Generator 7.1.0 into `vcell-restclient/` (Java),
`python-restclient/` (Python) and `webapp-ng/src/app/core/modules/openapi/` (TypeScript-Angular).
pyvcell (`../pyvcell`) keeps its own generated client from its own copy of the spec
(`pyvcell/_internal/api/vcell_client/`, regenerated with `scripts/generate.sh` plus a fix-up script).

Resources (60 operations): `Simulation` (start, stop, status), `admin/usage`, `bioModel` (save,
get, delete, summaries, summary, `vcml_download`), `export` (`N5`, `status`), `fieldData` (9 ops),
`geometry`, `image`, `mathModel` (each save/get VCML/delete/summary/summaries), `optimization` (list,
submit, status, stop), `publications` (CRUD + publish), `solver` (FV solver input from SBML/VCML),
`users` (token minting, identity mapping, recovery), `vcInfoContainer`, `helloworld`.

### 5.1 Conventions observed (not prescribed)

| Topic | What the existing endpoints do |
|---|---|
| Base path and casing | `/api/v1/<resource>`; mostly lowerCamel singular (`bioModel`, `mathModel`, `fieldData`, `vcInfoContainer`, `image`, `geometry`), but `Simulation` is capitalized and `publications`, `users` are plural |
| Identifiers | Database keys as strings in the path; names vary: `{bioModelID}`, `{id}`, `{simID}`, `{fieldDataID}`, `{optId}`, `{userName}` |
| Verbs in paths | Common: `/startSimulation`, `/stopSimulation`, `/optimization/{id}/stop`, `/fieldData/save`, `/fieldData/delete/{id}` (with `DELETE`), `/fieldData/advancedCreate`, `/users/mapUser`, `/solver/getFVSolverInput` |
| Download suffixes | `vcml_download` (snake case), alongside plain `GET /mathModel/{id}` returning VCML |
| Scoping | `includePublicAndShared` query flag on `*/summaries`; no pagination, filtering or sorting anywhere |
| Payloads | JSON DTOs; VCML as XML strings (`application/xml` or `text/xml`, inconsistently); multipart for uploads; `application/octet-stream` for solver input; PDF for admin usage |
| DTOs | Java `record`s, often nested in the resource class; dedicated `handlers/dto/*Rep` records since the VCInfoContainer refactor (`VersionRep`, `GroupAccessRep`, `UserRep`, …); some responses still expose domain-shaped types (`SimulationStatusPersistentRecord`, `VCSimulationIdentifier`, `ExportEvent`, `StatusMessage`) |
| Errors | `VCellHTTPError {exceptionType, message}` for 401/403/404/422/500 via `errors/handlers/*ExceptionHandler`; `DataAccessException` → 500 "Data Access Exception" |
| AuthN | Auth0 OIDC bearer JWT (`quarkus.oidc`, `application-type=service`); Keycloak devservices in tests |
| AuthZ | `@RolesAllowed("user")` on writes; reads often unannotated and resolve `getUserOrAnonymousFromIdentity` → anonymous `DUMMY_USER` sees public only; every authenticated caller gets role `user`, extra roles (`curator`, `owner`, `admin`) come from the `vcellapi.cam.uchc.edu/roles` claim (`CustomSecurityIdentityAugmentor.java:39-66`) — a second role source next to the DB's `SPECIAL_CLAIM`s |
| Async jobs | Submit returns an id; status by polling (`/optimization/{id}`, `/export/status?timestamp=`); a hidden SSE stream exists for exports |
| operationIds | Become generated method names; several are generic (`getSummary`, `getVCML`, `save`, `delete`, `getAllIDs`) |
| Limits | `quarkus.http.limits.max-body-size=20M`; CORS open to any origin; no rate limiting in the service (ingress limits only v0 sbml/omex on prod) |
| Compatibility | Python clients (in-repo and pyvcell) reject unknown fields; Java and TS tolerate them (recorded in the RPC→REST procedure, below) |
| Tests | `@QuarkusTest` HTTP tests with RestAssured + generated-client tests under `vcell-rest/src/test/java/org/vcell/restq/apiclient/` |

### 5.2 v1 consumers

| Consumer | Uses today | Would want from the migration |
|---|---|---|
| Desktop (`vcell-apiclient`) | model/geometry/image CRUD and summaries, `vcInfoContainer`, field data, N5 export + export status, legacy-token minting | all of section 3, events, version check |
| webapp-ng | `users/*`, `publications/*`, `admin/usage`, `bioModel/{id}`; links out to v0 HTML (`/api/v0/biomodel`) | model browsing that replaces the v0 HTML pages; simulation status and results for public models; the remote field viewer (section 9) |
| pyvcell | `bioModel` save / `vcml_download` / `summaries` (`pyvcell/vcml/utils.py:338,406`), `Simulation` start / status (`vcml_remote.py:202-245`, `session.py:58-62`), N5 export + `export/status` polling, then reads N5 via TensorStore (`vcml_remote.py:118-160`) | status by simulation id alone (today it must also pass the BioModel id), stop, job listing, direct reads of remote results without an export round trip (it already reads local VCell, zarr and VTU data in `pyvcell/sim_results/`), events or a blocking wait |
| python-restclient | generated only | — |

---

## 6. Prior art: the `getVCInfoContainer` port

Done in PR #1759 (merged 2026-07-25, `f86a129c41`, commit `61eb37749e` "Migrate desktop
getVCInfoContainer from RPC to REST; retire the RPC client stub"), followed by the DTO refactor in
PR #1775 / #1776 (8.0.2.04). The procedure, as it was carried out:

1. Locate the method in four places: the interface (`UserMetaDbServer`), the client stub
   (`RpcDbServerProxy`), the server handler (`DatabaseServerImpl`), and the client facade
   (`LocalUserMetaDbServerMessaging`), plus its guest-allowlist entry.
2. Add a resource (`VCInfoContainerResource`, `@Path("/api/v1/vcInfoContainer")`) and a service that
   reuses the existing server-side bulk query in `DatabaseServerImpl` (one call, never a fan-out of
   N client calls). Optional auth: anonymous gets public models.
3. Regenerate the spec and all three clients (`tools/openapi-clients.sh --update-spec`), then wire
   the facade to the generated API and map DTO → domain in `DtoModelTransforms`.
4. Retire in two phases: remove the client stub and allowlist entry now; keep the server handler
   until old clients age out.
5. Test at two levels: HTTP (`@QuarkusTest` + RestAssured, including anonymous) and generated client.

What it taught:

- **Serializing domain objects with Jackson broke four times** (GroupAccess type discriminator,
  `GIFImage` NPE mid-stream, `User.key` as `{value:N}`, `hiddenMembers` null). The fix was dedicated
  DTO records whose fields equal their getters equal the schema, with a DB-free round-trip test
  (native → server DTO → JSON → client DTO → native). The lead architect's direction: "stop trying
  to fix Jackson and do this using dedicated DTOs"; "/api/v1 should be your guide, not /api/v0".
- **The TypeScript webapp is a consumer too.** The first image build failed because a webapp
  component read a field whose shape changed; compiling Java downstream was not enough.
- **Live data finds what fixtures miss.** A live round-trip test against prod data found 1,481 of
  6,073 records failing three null cases the tests had not covered.
- **Anonymous identity and empty collections** each caused an NPE caught only in CI.
- **A wire-format change forced a full client release** (installers rebuilt), because old clients
  could not read the new shape.
- Strict Python clients mean new response fields must be opt-in or coordinated.

Issue #2184 proposes the next port (simulation start/stop), and notes the two copies of
`SimulationStateMachine` (sched and `org.vcell.restq.Simulations`).

---

## 7. Cross-cutting concerns

| Concern | Facts |
|---|---|
| Java serialization | Security (deserialization of client-supplied graphs; mitigated by the 2026-11-20 filter) and version coupling (every transported class is wire format). Moving a method to v1 removes both for that method. |
| Large binary payloads | `getSimDataBlock`, `getODEData`, `getMesh`, `getTimeSeriesValues`, VTU, Langevin and FEniCSx reads. The FEniCSx work showed per-call overhead matters (201 sequential RPCs per kymograph before batching, 53.9 MB moved cold before server-side sampling) and introduced reply caps answered as prefixes. JSON number arrays would multiply size and parse time. |
| Backward compatibility | Prod runs 8.1.x (`vc8.1-prod`), dev runs master (8.3.x). The client warns on a MAJOR/MINOR/PATCH mismatch but keeps working (`ClientServerManager.java:299-330`), so old installers keep calling v0 indefinitely. No client version is sent on RPC requests, so who still calls what is not measurable today. _(Superseded 2026-11-28: RPC calls are logged by method and client version from 8.3.5.02 on dev and R1 on prod; plan Phase 0 item 1.)_ |
| Guest / public access | Shared `vcellguest` account with a legacy token, two allowlists; #2220 proposes anonymous access instead. v1 already has an anonymous identity for public reads. |
| Authorization of data reads | Must be designed explicitly for v1 (see the plan's design questions). See the style guide §10. |
| Rate limiting and abuse | Public v0 resources are crawled (#1967, #2106, the 2026-11-08 outage); robots.txt is served by the webapp since #2030; ingress limits only cover v0 sbml/omex. vcell-rest has no limits of its own. |
| JMS | RPC rides ActiveMQ Classic `activemqint`. Artemis is the destination (`docs/MESSAGING.md`); do not invest in Classic-side machinery. vcell-rest speaks AMQP only. |
| Memory | The api pod buffers every RPC reply; the data pod has an 8 GB limit, rest 1 GB, api 2–4 GB (vcell-fluxcd `kustomize/base/*.yaml`, prod overlay differs). |
| Telemetry | The api access log records `POST /api/v0/rpc` but not the method (it is a header) nor the client version; the method appears only in the response status text and at TRACE. _(Superseded 2026-11-28: one structured line per RPC call, by method and client version; plan Phase 0 item 1.)_ |

---

## 8. Findings (non-security)

1. Guest allowlists disagree (server still allows `getVCInfoContainer`) and both carry entries the
   current client no longer sends over RPC (`getBioModelXML`, `getMathModelXML`).
2. Desktop status polling (`getSimulationStatus`, dispatch destination) is answered from the
   database inside the api pod, bypassing the dispatcher's access check; the DB-destination variant
   fails in `RpcRestlet` because it reads `args[1]` of a one-element array.
3. Seven client stubs are dead (B3, B4, B7, B14 ×2, B24) and vcell-api's `RpcDbServerProxy` is unused.
4. v1 `sbml_download`, `omex_download`, `bngl_download`, `diagram_download` are hidden 501 stubs;
   #2220's table lists them as available.
5. v1 `GET /Simulation/{simID}/simulationStatus` requires a BioModel id and ignores `mathModelID`;
   pyvcell has to carry the BioModel id around to poll a simulation.
6. webapp-ng links to a non-existent `/api/v0/mathmodel/{key}`.
7. `RestEventService`'s event deque appears unbounded.
8. `RpcRestlet`'s `GET ?stats=` hard-codes seven user names; `/admin/stats` already checks the admin
   claim.
9. The client's `timeoutMS` and `Destination` headers are ignored by the server.

---

## 9. The 3D field viewer

The desktop's field viewer (`vcell-client/.../org/vcell/client/viz/FieldViewerServer.java`, 2,394
lines; `FenicsBundleViews.java`, `FenicsFunctions.java`, `BodyFittedKymograph.java`,
`FvLineSampler.java`, `FvMembraneCurve.java`, `PointSeries.java`) is a loopback HTTP server
(`127.0.0.1:9124`) that serves the vtk.wasm page in `webapp-viewer/` (`index.html`, `viewer.js`) and
its data. It is plain HTTP GET with query parameters returning JSON (not JSON-RPC); errors are JSON
`{"error":…}` with 400/404/500/503 (`FieldViewerServer.java:2319-2339`). Requests name their dataset
(`sim`, `job`), so one server can serve several windows. Design record: `docs/3d-renderer-design.md`
("Mode A" is this; "Mode B", a web client talking only to the remote API, is not implemented).

It reads data only through `VCDataManager` (local or remote, same code) or, for FEniCSx, a
`BundleStore` (`vcell-core/.../solver/fenics/BundleStore.java`; the remote one is
`DataServerBundleStore.java`). Three data families: finite-volume (FV), the VTU seam (Chombo
static mesh, MovingBoundary time-varying mesh, `FieldViewerServer.java:160-200,740-1230`), and
FEniCSx bundles.

### 9.1 Routes and what they read

Query parameters seen across handlers: `sim`, `job`, `var`, `domain`, `time`, `points`, `x`, `y`, `z`,
`path`, `samples`, `tstep`, `snap`, `raw`, `max`, `plane`, `cell`.

| Route (handler line) | Returns | FV reads (data RPCs) | VTU reads (Chombo / MovingBoundary) | FEniCSx reads |
|---|---|---|---|---|
| `/health` (410) | `ok` | — | — | — |
| `/info` (552) | times, domains, variables, solver kind, particle availability | D2 `getDataSetTimes`, D5 `getMesh`, D1 `getDataIdentifiers` (`:2253,2266`), D14 | D2, D23 `getVtuVarInfos` (`:193,755-770`) | bundle manifest via D9/D12 (`FenicsBundleViews.info`, `:571`) |
| `/grid` (672) | points + cells of one domain (whole-voxel unstructured grid built in the client, cached per sim+domain) | D5, then client-side `CartesianMeshMapping` / `VisMesh` | D21 `getEmptyVtuMeshFiles` per time index (`:849`) | mesh arrays via D9/D12 (`:630`) |
| `/field` (1236) | one variable at one time on that grid | D15 `getSimDataBlock` (`:1258`) | D22 `getVtuMeshData` (`:988`) | bundle chunks via D9/D12; **functions evaluated client-side** from the open document's MathDescription (`FenicsFunctions.java:32-60`) |
| `/timeseries` (1297) | values over time at points / along a membrane | D16 `getTimeSeriesValues` (`:1339,1401,1473`) | D22 per saved time (`:1068`) | D11 `getFenicsBundleSamples`; moving mesh: D10 `getFenicsBundleLocatedSamples` |
| `/stats` (1933) | min/max/mean over time | D16 (`:1993`) | D22 per time (`:1178`) | whole rows via D12 prefetch (`:894`) |
| `/kymograph` (1562) | values along a path × time; heavy jobs serialized one at a time (`:232`) | D5 + D16 with line crossings (`FvLineSampler`) | `BodyFittedKymograph` + D22 per time (`:1643-1680`) | D11 / D10 (`:773`) |
| `/particles` (1917) | particle positions at a time | D14, D13 `getParticleDataBlock` (`:625-653`) | — | bundle particle files (`:834`) |
| `/` (421) | the static viewer page and wasm bundle | — | — | — |

### 9.2 What exists only in the client today

- Grid construction from `CartesianMesh` (whole-voxel cells, membrane quads/lines), domain index
  and per-domain caches (`meshCache`, `domainIndexCache`, `mbGridCache`).
- Point location on fixed meshes, membrane curves (`FvMembraneCurve`), line sampling with
  crossings (`FvLineSampler`), body-fitted kymographs (`BodyFittedKymograph`).
- FEniCSx function evaluation (needs the simulation's MathDescription, which the desktop has from the
  open document).
- JSON encoding of large numeric arrays, and the job serialization that keeps one heavy request at a
  time.
- The page itself loads only from the loopback origin; `base` may point elsewhere but only to
  loopback (`webapp-viewer/README.md`).

### 9.3 Relevance to the migration

Every route reads through the same data RPCs inventoried in 3.1, so a v1 data API serves the desktop
viewer and a web viewer alike. FEniCSx reads (D9–D12) are not in the guest allowlist, so a guest
cannot view a FEniCSx run remotely today. The plan treats a hosted viewer as a first-class consumer
(plan §7).
