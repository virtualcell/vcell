# Migrating `/api/v0` (RPC and legacy resources) to `/api/v1`: plan

> **Decided 2026-11-27: the target is `/api/v2`,** not `/api/v1` (style guide §6). `/api/v1` is frozen;
> wherever this document says "v1" for new endpoints, read "v2". The style guide is
> [`api-style-guide.md`](api-style-guide.md).

Status: **draft for discussion**, 2026-11-27. Nothing here is agreed. Inventory and evidence:
[`api-v0-rpc-inventory.md`](api-v0-rpc-inventory.md) (section numbers below like "inv §3.1" refer
to it).

**Ground rule from the lead architect:** new `/api/v1` endpoints are designed and agreed with him
before any is built. He has his own design patterns and quality requirements to formalize. So this
plan:

- does not fix any path, payload shape, naming, pagination, error model, auth model or DTO
  convention. Every target below is a candidate, marked **PROPOSED — needs agreement**, with
  alternatives;
- collects every design decision the migration depends on in §3, as open questions with options;
- puts the design-agreement step (Phase 1) before any endpoint work.

---

## 1. Goals and non-goals

**Goals**

1. Every capability the desktop reaches through `/api/v0/rpc` (46 live methods, inv §3.4) is
   available from `/api/v1`, in a form usable by the desktop, webapp-ng, pyvcell and a hosted 3D
   field viewer, not only the desktop.
2. No Java serialization crosses the public network. Domain objects are translated at the API
   boundary into dedicated, documented DTOs (the VCInfoContainer lesson, inv §6).
3. The desktop stops using `/api/v0` (`/rpc`, `/events`, `/swversion`) in a released client.
4. `/api/v0` can be switched off on evidence (telemetry), with its public URLs either redirected
   or retired deliberately.
5. The `api` pod (Restlet) and its in-pod services (`HealthService`, `RestEventService`,
   `AdminService`) are rehomed or retired, removing one service image and one OOM-prone pod.

**Non-goals**

- Reproducing the RPC shape over HTTP (a generic "invoke method X" endpoint). That would keep the
  coupling this migration exists to remove.
- Redesigning the backend services (data, db, sched) beyond what the API needs. Where the plan needs
  a backend change (e.g. a new way for vcell-rest to reach the data service), it is called out as a
  decision, not assumed.
- Porting the admin testing framework (B5, B16, B17) unless agreed; see D-21.
- Moving ActiveMQ Classic machinery forward. Per `docs/MESSAGING.md`, Artemis is the destination.

---

## 2. Where things stand (short)

- v1 already covers model/geometry/image CRUD, the model lists, field data, N5 export, parameter
  estimation, publications, users. The desktop moved those off RPC in 2025–2026.
- Still RPC-only: all simulation-data reads (23), the document-sharing, curation, preferences,
  species/reaction dictionary and testing-framework database calls (19), and simulation dispatch (4;
  start/stop already exist on v1 but the desktop does not use them, #2184).
- Still v0-only and used by the desktop: `/events`, `/swversion`.
- v0-only and used by the public / monitoring: model pages and downloads (SBML/OMEX/BNGL/diagram
  have no working v1 equivalent), `/simdata`, `/health`, `/admin/*`.
- vcell-rest cannot reach the RPC queues today (AMQP to Artemis only; RPC queues are on ActiveMQ
  Classic).

---

## 3. Design patterns & quality requirements to agree on

**These are open questions awaiting the lead architect's decisions.** Options are listed to make
the trade-offs visible, not to recommend; where an option is marked "(today)" it is what existing
v1 endpoints do (inv §5.1). The intent is that the agreed answers become a short API style guide
(Phase 1 deliverable) that every new endpoint is reviewed against.

### 3.1 Resource modelling and URLs

**D-1. URL style.** Options: (a) resource nouns, HTTP methods carry the verb
(`POST /simulations/{id}/runs`, `DELETE …`); (b) nouns plus explicit action sub-resources where an
operation is not CRUD (`POST /simulations/{id}:start` or `/simulations/{id}/start`); (c) today's
mixed style (`/startSimulation`, `/fieldData/delete/{id}`). Sub-questions: plural vs singular;
casing (lowerCamel today, `Simulation` capitalized); whether to fix existing inconsistencies as part
of this work or leave existing endpoints alone.

**D-2. Identifiers.** A simulation dataset is identified today by sim key + owner + job index
(`VCSimulationDataIdentifier`), and field data / merged data by other identifier types. Options:
(a) `/simulations/{simKey}/jobs/{jobIndex}/…` with owner always resolved server-side; (b) an opaque
dataset id; (c) separate resource families per identifier type (sim results, field data, merged).
Also: should path parameter names be uniform (`{id}` everywhere vs `{bioModelID}`)?

**D-3. Granularity.** Many RPCs are batch-shaped (status for N sims, N bundle files per call, samples
for N points). Options: (a) per-item endpoints only; (b) batch endpoints where a measured fan-out
cost exists (FEniCSx went from 201 calls to 8, inv §7); (c) both. If batch: request shape (repeated
query params vs POST body for reads), and caps.

**D-4. Where computation lives.** Some RPCs return raw data, others do server-side work (time-series
extraction, line scans, FEniCSx locate/sample, post-processing operations). Options per family: keep
server-side compute behind the API, or ship raw data and compute client-side. Applies strongly to
the field viewer (§7).

### 3.2 Representations

**D-5. DTO policy.** Options: (a) dedicated DTO records for every request/response, never a domain
class, with a DB-free round-trip test per DTO (the VCInfoContainer direction, inv §6); (b) allow
domain-shaped records where they already serialize cleanly (today: `SimulationStatusPersistentRecord`,
`ExportEvent`). Sub-questions: naming suffix (`*Rep`, `*Summary`, `*Record`, `*Dto`); where DTOs live
(nested in resources vs `handlers/dto`); where the DTO ↔ domain transformers live on the client
(`DtoModelTransforms`) and on the server.

**D-6. Numeric and binary payloads.** _Decided 2026-11-27: JSON for metadata and small results; bulk arrays in a binary format chosen in the pilot against the requirements in the style guide §2.4 (candidates: Zarr/N5, Arrow IPC, raw float64 + header); missing values separate from numbers; non-finite values as the strings `"NaN"`/`"Infinity"`/`"-Infinity"` in JSON (§2.3)._ Simulation data is large arrays of doubles. Options:
(a) JSON arrays (simple, big, slow to parse); (b) `application/octet-stream` raw little-endian
float64 with metadata in headers or a sibling JSON call; (c) a container format with metadata
(zarr/N5 chunks — the export path and pyvcell already use N5/zarr; HDF5; Arrow IPC; CBOR/MessagePack);
(d) mixed: JSON metadata + binary blocks. Sub-questions: compression (HTTP `Content-Encoding` vs
format-internal), endianness, NaN/Infinity (JSON cannot carry them; the event stream already had an
Infinity bug, memory: #1837/#1838), `Range` requests.

**D-7. Mesh and geometry representation.** `CartesianMesh`, VTU containers and FEniCSx meshes are
rich domain objects. Options: (a) a VCell-specific JSON/binary mesh DTO; (b) standard formats (VTU /
VTK XML, XDMF, zarr layout of the FEniCSx bundle as-is); (c) the field viewer's existing `/grid`
shape (points + cells) as the public format.

**D-8. Model documents.** VCML/SBML/OMEX/BNGL travel as XML strings or files. Agree content types
(`application/xml` vs `text/xml` vs `application/vcml+xml`), download vs inline
(`Content-Disposition`), and naming (`vcml_download` vs content negotiation on one URL).

**D-9. Functions and `OutputContext`.** Data requests carry the user's own functions
(`OutputContext` of `AnnotatedFunction`s) so the server can evaluate them. Options: (a) stateless —
send function expressions with each request (as an expression DTO); (b) store user functions
server-side per dataset and reference them by name; (c) only serve stored variables and solver-written
functions, evaluate user functions client-side (the field viewer already does this for FEniCSx).

### 3.3 Large results, async work and events

**D-10. Size limits and partial results.** The FEniCSx calls cap replies (8 MB, 1 M values) and answer
a prefix, telling the caller to ask again. Options: (a) adopt that pattern generally (with a
continuation token or "rows answered" field); (b) server-side streaming (chunked responses); (c) hard
4xx above a limit plus pagination. Also the max request body (20 MB today).

**D-11. Pagination, filtering, sorting.** No v1 endpoint paginates. Options: offset/limit, cursor,
or none for bounded collections. Needed for job lists (S4), dictionary searches (B9, B10, B18),
event history.

**D-12. Long-running operations.** Exports and background time series are asynchronous. Options:
(a) generic job resource (`POST` returns a job id; `GET` job status; result link) shared by export,
optimization, time-series and future work; (b) per-feature job endpoints (today: export and
optimization each have their own). Retention and cleanup of job records.

**D-13. Event delivery.** The desktop polls `/api/v0/events` for sim-job status, export and data-job
events and broadcasts. Options: (a) polling with a cursor (`?after=<eventId>`, today uses wall-clock
timestamps from per-replica clocks); (b) Server-Sent Events (a hidden SSE endpoint already exists for
exports); (c) WebSocket; (d) no general stream — clients poll per-resource status. Sub-questions:
event schema (one envelope with a type field vs typed streams), persistence (in-memory per pod today,
lost on restart, appears unbounded) vs Artemis/DB-backed, multi-replica consistency, anonymous users
(none needed per #2220).

### 3.4 Errors, versioning, discovery

**D-14. Error model.** Options: (a) keep `VCellHTTPError {exceptionType, message}` (today); (b) RFC
9457 `application/problem+json` with stable machine-readable `type`/`code`; (c) (a) plus a code field.
Agree status-code mapping (today `DataAccessException` → 500, which makes "no results" look like a
server fault; #1967 changed that to 404 on v0), and whether exception class names belong on the wire.

**D-15. Compatibility rules within v1.** Additive-only changes? How to add response fields given
strict Python clients (in-repo and pyvcell reject unknown keys; the procedure today gates new fields
behind opt-in flags)? Options: (a) regenerate strict clients and require lockstep; (b) configure the
generators to ignore unknown fields; (c) opt-in flags per field (today). When, if ever, is `/api/v2`
warranted?

**D-16. Capability discovery.** RPC clients detect missing server methods by catching "No such method"
(FEniCSx fallback, inv §2.3). Options: (a) rely on 404 for unknown routes; (b) a `GET /api/v1/version`
or `/capabilities` endpoint (would also replace `/api/v0/swversion`); (c) server version header on
every response.

**D-17. Generated-client ergonomics.** operationIds become method names in three generated clients and
pyvcell's. Agree naming (verb + noun, unique, stable), tags (one per resource), and whether
operationIds may change for existing endpoints. Agree OpenAPI documentation requirements (summary,
description, examples, units for numeric fields).

### 3.5 Security and operations

**D-18. Authentication.** Options: (a) Auth0 JWTs on every v1 call (today) and retire the legacy
token mint (`/users/bearerToken`) with v0; (b) keep legacy tokens for a transition. Plus the guest
question (#2220): (a) anonymous access for public data, retire `vcellguest`; (b) keep the guest
account on v1.

**D-19. Authorization model for data and documents.** Agree, per resource family, who may read: owner,
group members (`GroupAccess`), public, anonymous. Simulation results especially need an explicit rule.
Also the role source: Auth0 `roles` claim
(`curator`, `owner`, `admin`) vs database `SPECIAL_CLAIM`s (`admins`, `powerUsers`,
`publicationEditors`, `vcellSupport`) — one source, or a defined mapping.

**D-20. Rate limiting, quotas, caching.** Public endpoints are crawled and have taken prod down
(inv §4, §7). Options: per-identity and anonymous rate limits in vcell-rest vs at the ingress; request
cost limits (mesh size, rows, points); `Cache-Control`/`ETag` for immutable results (a finished
simulation's data does not change; a running one does); robots policy for the new public URLs.

**D-21. Scope cuts.** Which RPC families are not worth a public API? Candidates to drop or keep
admin-only: testing framework (B5, B16, B17); `getSpecialUsers` (replaceable by roles on
`/users/me`); dead stubs (inv §2.4).

**D-22. Where the server code runs.** vcell-rest cannot reach the RPC queues today. Options:
(a) vcell-rest forwards to the existing data/db/sched services over messaging (requires a JMS or AMQP
RPC path from Quarkus; `docs/MESSAGING.md` §7 lists the RPC risks on Artemis); (b) vcell-rest calls
`DatabaseServerImpl` / `DataSetControllerImpl` in-process (it already does this for the database and
mounts `/simdata`; data reads would move data-pod memory and caching load into rest pods, limit 1 GB
today); (c) the data service becomes its own HTTP service behind the ingress, with vcell-rest only
authorizing (the capability-token idea in `docs/3d-renderer-design.md`, "geometry-server spike").
This decision drives Phase 3 effort more than any other.

### 3.6 Quality requirements

**D-23. Tests required per endpoint.** Today's procedure (inv §6) requires an HTTP-level
`@QuarkusTest` and a generated-client test. Candidates to add: DB-free DTO round-trip test;
anonymous/guest and cross-user permission cases; a live-service test against real data (gated, as
`LiveVCInfoContainerRoundTripTest`); payload-size and latency budgets for data endpoints; webapp-ng
`ng build` and pyvcell regeneration as part of "downstream compile".

**D-24. Definition of done for a port.** Candidates: spec + three clients regenerated; desktop facade
switched; RPC client stub removed; server handler deprecated, removed after telemetry shows no use;
docs (user help if behaviour changed); CHANGELOG.

### 3.7 Observed v1 conventions (input, not prescription)

Summarized in inv §5.1: lowerCamel resource paths with exceptions; verbs in paths; string DB keys
with varied parameter names; no pagination; JSON records, VCML as XML strings; `VCellHTTPError`;
Auth0 with `@RolesAllowed("user")` on writes and anonymous public reads; submit-and-poll jobs;
CORS open; no service-level rate limits; strict Python clients.

---

## 4. Candidate v1 resource groups (PROPOSED — needs agreement)

Grouping only; names are placeholders chosen to be easy to rename. Each group lists the RPC methods it
would replace and the design questions it depends on.

| Group (placeholder) | Replaces | Depends on | Notes |
|---|---|---|---|
| **Simulation run control** | S1 start, S2 stop | D-1, D-2, D-14 | v1 endpoints exist (#2184). Decide whether to keep their shape or reshape now; reconcile scan-count handling, return type (`StatusMessage[]` vs status), batch-job kill on stop, and the duplicate state machine |
| **Simulation status and jobs** | S3 status (single + batch), S4 job query; v0 `/simstatus`, `/simtask`, `/admin/jobs` | D-3, D-11, D-19 | Status by sim id alone (pyvcell need); batch form for the desktop's model-open poll |
| **Events** | `/api/v0/events`; result delivery for D16 background and D17 | D-13, D-12 | Prerequisite for moving the desktop off v0 entirely |
| **Dataset metadata** | D1, D2, D3, D14, D19, D23, D24, D25 (metadata parts) | D-2, D-9 | Small, read-only; good pilot candidate |
| **Field values** | D15, D22, D13 | D-6, D-10, D-20 | Hot path; binary format decision |
| **Mesh / grid** | D5, D21; FEniCSx mesh files | D-7 | Shared with the field viewer |
| **Time series and derived data** | D16, D4, D18, D11, D10 | D-4, D-9, D-12 | Server-side compute; sync vs job |
| **Bulk results** | D6, D7, D8, D25, D9, D12 | D-6, D-10 | Whole-result downloads; may overlap with export |
| **Export** | D17 (non-N5 formats) | D-12 | Extend existing `/export` (N5 already there) |
| **Document sharing** | B19–B22 | D-19, D-5 | Applies to biomodel, mathmodel, geometry, image alike |
| **Curation** | B2 | D-19 | Relation to `publications/{id}/publish` |
| **Preferences** | B11, B23 | D-18 (anonymous → local only, per #2220) | Small, low-risk; good pilot candidate |
| **Simulation document** | B15 | D-8 | VCML of one simulation |
| **References** | B6 | D-2 | "What uses this", before delete |
| **Species and reaction dictionary** | B8, B9, B10, B12, B13, B18 | D-11, D-5 | Search endpoints |
| **Identity and roles** | B1; `/api/v0/swversion` | D-16, D-19 | Possibly folded into `/users/me` and a version endpoint |
| **Testing framework** | B5, B16, B17 | D-21 | Drop, admin-only, or keep on a non-public path |
| **Public model pages and downloads** | v0 `/biomodel…`, `/publication…`, `/modelbrick` | D-8, D-20 | Finish the 501 stubs; decide on redirects for ~850 external links per URL pattern |
| **Health** | v0 `/health` | — | Rehome `HealthService` (into vcell-rest or a small service); keep the monitor URL stable or move monitors |
| **Field viewer data** | the field viewer's routes (§7) | D-4, D-6, D-7, D-20 | Overlaps Dataset metadata / Field values / Mesh / Time series |

---

## 5. Phases

Ordering principle: agree the design before building; measure before switching off; move read-only
and low-risk first; keep the old server path until telemetry says it is unused.

### Phase 0 — Preparation (no new endpoints)

1. **Telemetry on v0.** Log one structured line per RPC with destination, method, caller identity
   class (user / guest / anonymous), outcome, duration and reply size; log the same for each non-RPC
   v0 route. Add a client-version header to the desktop's v0 and v1 requests (none is sent today) and
   log it. Without this, no switch-off date is defensible.
2. **Harden v0 while it lives.** _Done in 8.1.9.02 and 8.3.5.01:_ RPC dispatch accepts only the
   methods listed per service queue (`RpcMethodAllowlist`, covering live methods and the
   retired-but-supported ones in inv §3.2). Further hardening follows `SECURITY-FIXES.md`, not this
   plan's PRs.
3. **Small v0 fixes** found in the inventory (inv §8): reconcile the guest allowlists, drop dead
   client stubs, fix the dead webapp-ng `/mathmodel` link, bound the event deque. Each independent.
4. **Correct #2220's table** (v1 SBML/OMEX/BNGL/diagram downloads are 501 stubs).

### Phase 1 — Design agreement (gate)

1. Walk through §3 with the lead architect; record each decision.
2. Produce a short **API style guide** (`docs/api-style-guide.md` or similar) from the decisions,
   with one worked example endpoint written against it.
3. Agree the candidate grouping in §4, the scope cuts (D-21), and D-22 (where server code runs).
4. Agree the phase order below (or change it).

No endpoint work starts before this phase closes.

### Phase 2 — Pilot (one small, read-only group)

Pick one group that exercises the style guide without the hard decisions — candidates: Preferences,
Dataset metadata, or Simulation status. Build it end to end: endpoint, DTOs, round-trip test, three
regenerated clients plus pyvcell, desktop facade switch, RPC stub retired, server handler kept.
Review the result against the style guide and amend the guide.

### Phase 3 — Simulation data reads

Depends on D-6, D-7, D-9, D-10, D-22. Order inside the phase: metadata → field values → mesh → time
series and derived data → bulk results. Measure payload size and latency against the RPC baseline
for the desktop's data viewer and the field viewer before switching the desktop. This phase also
delivers what the hosted field viewer needs (§7).

### Phase 4 — Database families

Sharing, curation, simulation document, references, dictionary, (testing framework if kept).
Mostly CRUD/search over `DatabaseServerImpl`, which vcell-rest already embeds.

### Phase 5 — Dispatch and events

Run control (#2184), status and jobs, events (D-13), and result delivery for async data calls and
exports. Could move earlier: start/stop already exist on v1, and pyvcell already depends on them.
Events must land before the desktop can drop v0 entirely.

### Phase 6 — Desktop cut-over and release

Switch any remaining facades, version endpoint replaces `/swversion` (D-16), stop requesting legacy
tokens, ship a client release, and keep the v0 server path. Coordinate with the clientgen/installer
release (desktop installers must match server version).

### Phase 7 — Deprecation

Announce; add deprecation signals (e.g. `Deprecation`/`Sunset` headers on v0, a client warning
for old versions); watch telemetry by method and client version. Agree thresholds (e.g. no RPC calls
from supported versions for N weeks) before removing anything.

### Phase 8 — Retirement of v0

Remove server handlers for unused methods; rehome `HealthService` and admin reports; put redirects
(or deliberate 410s) on public v0 URLs per pattern; delete `vcell-api`, the `api` deployment and
its ingress route (vcell-fluxcd); remove `RpcRestlet`'s deserialization path and the v0 legacy-token
machinery (#1979).

The public-pages track (model pages and downloads, publications, health) can run in parallel from
Phase 2 on, since it has no desktop dependency.

---

## 6. Compatibility strategy for old desktop clients

- **Server keeps answering v0 until telemetry says otherwise.** Old installers keep calling v0
  forever; the version check only warns (inv §7). Prod runs 8.1, dev runs master; clients in the
  field go back further (pre-7.7.0.26 clients still save models over RPC).
- **Two-phase retirement per method** (as in the VCInfoContainer port): remove the client stub when
  the desktop switches; keep and deprecate the server handler; delete it when telemetry by client
  version shows no use.
- **Feature detection for new clients against old servers**: a new desktop may meet an older server
  (prod lags master). Either keep the RPC fallback in the facade for one release cycle, or gate on the
  server version/capabilities (D-16). FEniCSx's "No such method" fallback is the existing example.
- **Supported-version policy.** Decide the oldest client version the server must keep serving, and
  whether a too-old client is refused with a clear message (today it is warned). Needs agreement;
  ties to Phase 7 thresholds.
- **Prod-line backports.** Endpoints added on master do not exist on prod (8.1) until a prod roll; a
  master desktop that switches to them breaks against prod. Ship the client switch only after the
  server side reaches prod, or keep the fallback.
- **Public URLs.** v0 HTML and download URLs are cited from ModelBricks, vcell.org and papers. Keep
  them working (redirects to v1 or to webapp pages) on a per-pattern basis rather than breaking them.

---

## 7. The hosted 3D field viewer (PROPOSED — needs agreement)

Target: a version of the vtk.wasm viewer (`webapp-viewer/`) that runs in a browser against
`/api/v1`, with no desktop running, for simulations the viewer is authorized to read (public ones at
least). The desktop's loopback server would keep working for local runs; ideally both would speak
the same route semantics so `viewer.js` changes only its base URL and auth.

### 7.1 What it needs

From the inventory (inv §9): `/info`, `/grid`, `/field`, `/timeseries`, `/stats`, `/kymograph`,
`/particles`, for three data families (finite-volume, Chombo/MovingBoundary VTU, FEniCSx). Each maps
onto the data RPCs D1, D2, D5, D9–D16, D21–D23. Several pieces exist only in the desktop today
(inv §9.2): grid construction from `CartesianMesh`, point location, membrane curves, line sampling,
body-fitted kymographs, FEniCSx function evaluation (needs the MathDescription), heavy-job
serialization.

### 7.2 Candidate shapes

| Option | Description | For | Against |
|---|---|---|---|
| A. Same routes, server-hosted | Port `FieldViewerServer`'s handlers into vcell-rest under a viewer resource, reading through the same `VCDataManager`/`BundleStore` seams (server-side: `DataSetControllerImpl`, local `BundleStore`) | Viewer changes little; one implementation of grid building and sampling | Moves CPU-heavy work (kymograph, locate, grid build) into rest pods; JSON arrays as today |
| B. General data API, viewer computes | Expose only the generic data groups (§4: metadata, field values, mesh, time series); the browser builds grids and samples | One API for all clients; server stays thin | Grid building / locate / function evaluation must be reimplemented in JS or wasm |
| C. Hybrid | Generic data API plus a few compute endpoints where data volume makes client-side compute impractical (kymograph, locate on moving meshes, time series at points — FEniCSx already moved these server-side for that reason) | Matches where the FEniCSx work landed | Two kinds of endpoint to keep consistent |
| D. Geometry server | Server extracts and smooths surfaces (the spike in `tools/geometry-server/`, `docs/3d-renderer-design.md` Mode B); a light WebGL client | 6.8 KB client vs 12 MB wasm | No interactive slicing/volume work; pyvcell `get_data()` performance blocks it today |

### 7.3 Open questions specific to the viewer

- **Auth and public access.** Anonymous access to public simulations' data (D-18, #2220)? The current
  guest allowlist has none of the FEniCSx calls, VTU calls, time series or particle blocks, so the
  guest experience today is narrower than what a public web viewer would offer. Authorization of
  data reads must be explicit (D-19); `docs/3d-renderer-design.md` notes authorization lives in Java
  and the DB ("do not reimplement `GroupAccess` in Python"), which argues against a separate data
  service validating tokens itself unless vcell-rest issues it a scoped capability.
- **Payload size and streaming.** Field arrays and meshes as JSON vs binary (D-6, D-7). FEniCSx
  bundles are already zarr-like chunked arrays; serving chunks as-is (with HTTP caching) is an option.
  VTU containers for Chombo/MovingBoundary are large and per-timestep for MovingBoundary.
- **Server- vs client-side compute** (D-4): locate, kymograph, function evaluation, statistics.
  FEniCSx function evaluation needs the MathDescription; server-side that means loading the
  simulation document per request (cost, caching).
- **Caching.** Finished simulations are immutable, so grids and field blocks are cacheable
  (`ETag`/`Cache-Control`, CDN, server-side caches); running simulations are not. The desktop caches
  grids per sim+domain; a server needs bounded caches (the data pod already has a `Cachetable`).
- **Rate limiting and cost control** (D-20). A public viewer endpoint that builds grids or kymographs
  is far costlier per request than `/api/v0/simdata/{id}/jobindex/{n}`, which a scraper has already
  hammered. Needs per-request cost limits (mesh size, rows, points), concurrency limits for heavy
  jobs (the desktop serializes them), and anonymous quotas.
- **Hosting the page.** Same origin as the API (served by webapp or rest) vs separate; the 12 MB wasm
  bundle on the web deploy path; browser support (JSPI, WebGL 2 — inv §9 / `webapp-viewer/README.md`).
- **Two pipelines must agree.** If both desktop and web viewers exist, the golden-file check between
  them should run in CI (`docs/3d-renderer-design.md`, "If both modes exist").

---

## 8. Testing strategy

- **Per endpoint** (pending D-23): HTTP-level `@QuarkusTest` (authenticated, anonymous, other-user
  denied), generated-client test, DB-free DTO round-trip test both directions.
- **Equivalence against RPC.** For each ported data method, a test that reads the same dataset
  through the RPC path and the v1 path and compares values (exact for raw data, tolerance where
  formats differ). Reuse the field viewer's test fixtures (`vcell-client/src/test/.../viz`,
  `FieldViewerRunServer`) and the FEniCSx bundle tests (`FenicsBundleDataServerTest`,
  `FenicsBundleSamplingTest`).
- **Live-service tests** gated on an environment variable against dev (pattern:
  `LiveVCInfoContainerRoundTripTest`), because fixtures miss real-data shapes (inv §6).
- **Downstream builds**: Java (`mvn compile test-compile -pl vcell-rest -am`, plus `vcell-apiclient`,
  `vcell-client`), `webapp-ng` `ng build`, pyvcell client regeneration and its `make check`.
- **Old-client compatibility**: run a released prod-line client (8.1) against a master server and
  the reverse, for the methods being switched, before each release that switches a facade.
- **Performance budgets** for data endpoints (payload bytes, latency) measured against the RPC
  baseline, using the stage timing the field viewer already logs.
- **Telemetry checks** before any removal: the Phase 0 logs, by method and client version.

---

## 9. Risks

| Risk | Mitigation |
|---|---|
| Building endpoints before conventions are agreed, then reworking them | Phase 1 gate; pilot in Phase 2 |
| Data endpoints slower or larger than RPC, regressing the desktop viewer | Binary format decision (D-6), measured budgets, keep RPC fallback until parity |
| vcell-rest cannot reach backend services the way it needs (D-22) | Decide in Phase 1; prototype the chosen path in the pilot if Phase 3 depends on it |
| Moving data compute into rest pods causes OOMs (rest limit 1 GB) | Size and limit per request; consider D-22 (c) |
| Public data endpoints attract the same crawling that broke v0 | D-20 before any public data endpoint ships |
| Authorization mistakes when opening data to anonymous users | D-19 explicit model; cross-user and anonymous tests on every endpoint |
| Wire-format changes force full client releases (as 8.0.2.04 did) | Compatibility rules (D-15); additive changes; version gating |
| Old clients broken by premature removal | Telemetry by client version; supported-version policy; two-phase retirement |
| Two state machines diverge further while start/stop is dual-pathed | Resolve in the run-control group (#2184 item 4) |
| Events: losing or duplicating status events during the switch | Run both paths during transition; cursor-based delivery (D-13) |
| ActiveMQ Classic work creeping in | Follow `docs/MESSAGING.md`: no new Classic machinery |
| Disclosure of v0 weaknesses through migration PRs | Security fixes follow `SECURITY-FIXES.md`; plan PRs state neutral requirements only |

---

## 10. Decisions needed from the lead architect

1. All of §3 (D-1 to D-24), in particular the four that gate the most work: **D-6** (binary data
   format), **D-13** (event delivery), **D-19** (authorization model for data reads and the role
   source), **D-22** (where data-serving code runs).
2. Whether to restyle existing v1 endpoints to the new guide or leave them (D-1).
3. The candidate grouping in §4, and the pilot group for Phase 2.
4. Scope cuts (D-21): testing framework, `getSpecialUsers`, dead stubs.
5. Phase order, in particular whether dispatch/events (Phase 5) goes before data (Phase 3).
6. The oldest client version the server must keep serving, and the telemetry threshold for removing
   a v0 method.
7. What happens to public v0 URLs (redirect, keep, 410) per pattern.
8. The hosted field viewer: whether it is in scope for this migration, and which of options A–D
   (§7.2) to explore.
9. Anonymous access vs the guest account (#2220), since it decides how public endpoints are
   authorized.
10. Where `HealthService` and the admin reports live after the `api` pod is gone.
12. **vcell-ai is a v0 and v1 client** (found 2026-11-27, `../vcell-ai` `backend/app/services/`):
    it calls `/api/v0/biomodel`, `/api/v0/biomodel/{id}/…` (8 call sites) and `/api/v0/publication`,
    and `/api/v1/users/*` and `/api/v1/publications`, forwarding the user's Auth0 token. The inventory's
    caller list missed it; v0 retirement must include it.
11. **The direct-connect developer client** (`vcell-admin/.../standalone/VCellClientDevMain.java`):
    it runs the desktop wired straight to the backend (database and remote files), bypassing the API,
    for debugging. **Decided 2026-11-27: retired**, on the schedule in the style guide §11 (developer
    path is `VCellClientMain` against the dev site or a local stack now; no in-process twins for v2
    endpoints; deleted when the desktop stops using RPC).
