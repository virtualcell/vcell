# VCell REST API style guide (`/api/v2`)

Status: **in progress**, started 2026-11-27. These rules are decided by the lead architect, one
design question at a time; the questions and their options are in
[`api-v0-to-v1-migration-plan.md`](api-v0-to-v1-migration-plan.md) §3 (D-1 to D-24). Each rule
below names the question it settles. Questions not listed here are still open.

Every `/api/v2` endpoint is reviewed against this guide (§6).

## Scope

**The guide defines `/api/v2`** (§6). Endpoints are designed to it from the start; existing
`/api/v1` endpoints that are inconsistent with it (D-1) are not restyled in place but get guide-conformant
`/api/v2` equivalents, and `/api/v1` is frozen and retired (§6.2). The `/api/v0` RPC functionality moves
straight to `/api/v2`, so nothing is ported twice.

## 1. Resources and URLs

### 1.1 Resources are nouns; HTTP methods carry the verb (D-1)

Paths name resources, never actions: `GET /biomodels/{id}`, `DELETE /fielddata/{id}`, not
`GET /fieldData/delete/{id}`. The method says what happens:

| Method | Meaning | Properties relied on |
|---|---|---|
| `GET` | read | safe (no side effects) and repeatable: cacheable, retried automatically |
| `POST` | create a resource, or start work | neither safe nor repeatable |
| `PUT` | replace a resource | repeatable |
| `PATCH` | change part of a resource | |
| `DELETE` | remove a resource | repeatable |

An operation that is not plain create/read/update/delete is modelled as the resource it creates or
changes before anything else: starting a simulation creates a **run**
(`POST /simulations/{id}/runs`); an export is an **export job** (`POST /exports`, then
`GET /exports/{id}`); sharing changes an **access setting** (`PUT /biomodels/{id}/access`).

**Named actions are allowed only as listed exceptions.** When an operation has no natural resource, it
may be a named action, written `POST /{resource}/{resourceId}/{action}` (a slash, never Google's
`:action` form). Each one is listed below with its reason, and Spectral's verb rule accepts only the
listed paths (§4.3). Prefer a resource first; an action added because it was quicker is not a reason.

| Path | Reason |
|---|---|
| _(none yet)_ | |

### 1.2 Queries use `GET` (D-3, D-11)

Queries use `GET` with query parameters. **Arrays are repeated parameters** (`?id=1&id=2`: OpenAPI
`style: form`, `explode: true`, the default), never comma-joined lists, which break on values that
contain commas and need client-side configuration.

A query may use `POST` to a dedicated query resource (`POST /{collection}/search` or similar)
**only** when at least one of these holds:

1. its inputs exceed URL limits (practical limit about 8 KB, set by the ingress and proxies), for
   example thousands of sample points;
2. its inputs are structurally nested beyond flat filters, for example boolean combinations of
   criteria;
3. its inputs are sensitive and must not appear in URLs, logs or browser history.

Such an endpoint must still be safe (no side effects), and its OpenAPI description must say which
condition applies. **Searches driven by an ordinary form are always `GET`**, so their results can be
linked, bookmarked, cached and retried.

A request body on `GET` is never used: HTTP leaves its meaning undefined and proxies and clients
drop or reject it.

Why: a `POST` that only reads gives up HTTP caching (finished simulations and published models never
change), automatic retries, shareable links, and the read/write distinction that ingress logging and
rate limiting rely on. Generated clients and users also assume a `POST` may change something.

### 1.3 One endpoint, one purpose, one response type (D-3, D-12)

A request never selects the operation: no operation-type field, and no mode flags that change what
is computed or the shape of the response.

- Operations that differ in response, authorization, cost or caller are **separate endpoints**.
- One concept with variants that share a lifecycle and a response (for example export formats) is
  **one resource**, whose variant-specific options are a discriminated union (OpenAPI `oneOf` with a
  `discriminator`).
- Running work in the background is a **job resource** (D-12), never a flag on a read.
- An **aggregate view** (one response combining several resources, such as the desktop's
  `vcInfoContainer`) is allowed only as a named, documented view built for a measured need, and only
  alongside the per-resource endpoints it combines.

Why: the legacy RPC interface packs unrelated operations into operation objects
(`doDataOperation`, `getTimeSeriesValues`, `doTestSuiteOP`). Their responses change shape with the
request, which generated clients cannot type, reviewers cannot reason about, and caches and rate
limits cannot tell apart.

### 1.4 Naming

| Element | Convention | Example |
|---|---|---|
| Collections | plural nouns | `/api/v2/biomodels`, `/simulations` |
| Path segments | lowercase; **kebab-case** for several words | `/field-data`, `/simulations/{simulationId}/runs` |
| Path parameters | descriptive **camelCase**, never a bare `{id}` | `{bioModelId}`, `{simulationId}` |
| Query parameters and JSON fields | **camelCase** | `?includeShared=true`, `"simulationId": "123"` |
| `operationId`s | camelCase, verb + noun, unique and stable | `listBioModels`, `getBioModelVcml` |
| **Acronyms, everywhere** (paths, fields, operationIds, DTO class names) | **written as words** | `Vcml`, `Sbml`, `Omex`, `FvSolverInput`, `simulationIds` (not `VCML`, `IDs`) |

Acronyms are written as words because generators split names on capitals: `getVCML` becomes
`get_v_c_m_l` in some Python clients, `getVcml` becomes `get_vcml` predictably. This follows
Google's API and Java style guides and Microsoft's REST guidelines.

## 2. Representations

### 2.1 Wire types live in one shared DTO module (D-5)

The contract is **code-first**: Java records in a shared DTO module are the source of truth, and the
OpenAPI spec (`tools/openapi.yaml`) is generated from them. Design-first (hand-written spec,
generated code) was considered and not chosen, because the server and its main client are both Java
and generated Java models are verbose, mutable and churn with generator upgrades.

| Module | Java package | Contents | May depend on |
|---|---|---|---|
| **`vcell-api-dto`** | `org.vcell.api.dto` (sub-packages per resource family) | Java records only: the request and response bodies of every `/api/v2` endpoint | Jackson and MicroProfile OpenAPI **annotations** only. No VCell module. |
| **`vcell-api-mapping`** | `org.vcell.api.mapping` | Mappers between DTOs and VCell domain objects, in both directions | `vcell-api-dto` and the domain modules (vcell-core, …) |

Rules:

1. **Every `/api/v2` request and response body is a type from `vcell-api-dto`.** Resources never
   return domain objects, and never declare records of their own.
2. **No domain type in a DTO**, not even as a field: no `KeyValue`, `User`, `Expression`,
   `BigString`, `VersionFlag`. Identifiers are strings, expressions are strings, enums are DTO enums
   with stable wire values. The build enforces the dependency rule (Maven enforcer or an ArchUnit
   test), so a domain import fails the build.
3. **Both ends use the same mappers.** The server (vcell-rest) maps domain → DTO and the desktop maps
   DTO → domain with the same `vcell-api-mapping` classes, so the two directions cannot drift.
4. **Each mapper has a DB-free round-trip test** (domain → DTO → domain, and DTO → domain → DTO where
   both directions exist), in `vcell-api-mapping`.
5. **The generated spec is the reviewed contract.** Every PR that changes the API includes the
   regenerated `tools/openapi.yaml`, and the reviewer reviews its diff as the contract change.
6. **Clients.** TypeScript (webapp-ng), Python (`python-restclient`) and pyvcell are generated from the
   spec. The Java client uses the `vcell-api-dto` records directly rather than generating a second
   Java model (openapi-generator schema/import mappings; verified by the spike in §2.2).

**Package names are chosen for clarity, not to mirror vcell-core.** vcell-core's packages
(`cbit.vcell.*`, `org.vcell.*`, mixed) are historical, and renaming them wholesale would be
disruptive. The DTO and mapping modules get clean, consistent names of their own; the mapping
module is the one place that bridges the two naming worlds.

They are built for `/api/v2`, and replace v1's records scattered across `vcell-rest` (`models/`, `handlers/`,
`handlers/dto/`, `Simulations/`) and the generated Java models in `vcell-restclient`. The v0
equivalents (`vcell-api-types`, `DTOModelTransformerV0`) retire with v0.

**DTO names** are bare nouns, with a suffix only for a distinct role:

| Role | Name |
|---|---|
| full representation | `BioModel` |
| list item | `BioModelSummary` |
| request bodies | `BioModelCreate`, `BioModelUpdate` |
| query body (a `POST` query, §1.2) | `TimeSeriesQuery` |

No `Dto`, `Rep`, `Record` or `Representation` suffixes; the package says they are DTOs. **Simple names
are unique across all of `vcell-api-dto`** (an ArchUnit test checks this), because SmallRye names
schemas by simple class name and two `Status` records in different sub-packages would collide in the
spec. In `vcell-api-mapping`, where a DTO and a domain class share a name, one of them is written
fully qualified.

### 2.2 The Java client uses the shared records (spike, 2026-11-27)

The generated Java client works directly on `vcell-api-dto` records, with no generated Java model and no
hand patches. Spike: `tools/api-spikes/java-client/` (`run.sh` regenerates, compiles and runs 14 checks
against a stub server).

**How it is generated:** openapi-generator (the version `vcell-restclient` uses, 7.1.0), Java `native`
library (Java `HttpClient` + Jackson), with every schema mapped onto its record:

```bash
--schema-mappings BioModel=org.vcell.api.dto.BioModel,... --import-mappings <same list>
```

The generator then skips every model ("skipped prior to model processing due to schema mapping") and
the API methods take and return the records (`org.vcell.api.dto.BioModel getBioModel(String)`). The
mapping list is produced from the DTO module, not maintained by hand.

**Verified:**
- records with nested records, lists, enums and `OffsetDateTime` timestamps deserialize;
- unknown response fields are ignored (the generated client sets `FAIL_ON_UNKNOWN_PROPERTIES=false`),
  so additive changes do not break it (§6.1);
- array query parameters go out as repeated parameters (§1.2);
- a discriminated union (a sealed interface whose records carry the discriminator, with
  `@JsonTypeInfo`/`@JsonSubTypes`) serializes and deserializes to the right record (§1.3);
- `application/problem+json` errors arrive as `ApiException` with the status, and the body parses into
  the `Problem` record (§9).

**Rules that came out of it:**
1. **DTO enums tolerate values they do not know.** Each DTO enum has an `UNKNOWN` constant and a
   `@JsonCreator` factory that returns it for an unrecognized value, so an older client survives a new
   enum value (§6.1). The tolerance lives in the DTO, not in client configuration: the generated
   `ApiClient.getObjectMapper()` returns a *copy*, so enabling a Jackson feature on it has no effect.
   The server never sends `UNKNOWN`.
2. **Absent means null.** Clients omit null fields from request bodies; servers treat an absent field
   as null.
3. Cosmetic: the generator names request-body parameters after the mapped class
   (`orgVcellApiDtoBioModelCreate`). Java callers pass arguments by position, so this is left alone.

Not covered by the spike: the server side (SmallRye generating the same spec from these records,
including the `oneOf` discriminator) and the Python and TypeScript clients; both are checked in the
pilot (plan Phase 2).

### 2.3 Missing values and non-finite numbers

Storage does not change: simulation data stays as it is stored (text or binary floats, which may
contain NaN and ±Infinity). These rules govern how responses represent it.

1. **Missing is `null`.** A value is missing when there is legitimately nothing there: outside the
   variable's domain, not yet computed in a running simulation, a region that is empty at that time.
   Never a sentinel number, never zero-filled, never interpolated.
2. **Non-finite is never "missing".** NaN and ±Infinity mean the math produced a non-finite value,
   which the caller should see.
   - **In JSON** they are the strings **`"NaN"`, `"Infinity"`, `"-Infinity"`**, in place: the
     Protocol Buffers JSON mapping and Zarr convention, and Jackson's default (`WRITE_NAN_AS_STRINGS`).
     **Never** the bare tokens `NaN`/`Infinity` (not valid JSON; browsers' `JSON.parse` rejects them),
     and never sentinel numbers.
   - The spec defines this once, as a reusable schema `Float64` (a number, or one of those three
     strings), and every numeric-data field uses it.
   - **In binary responses** (§2.4) they are the native IEEE-754 values; missing values use the
     format's null mechanism (Arrow's validity bitmap).
3. **The server tells the two apart where storage does not.** Where stored data uses NaN for "no data"
   (for example outside a variable's domain), the server, which knows the domain from the mesh,
   returns `null`; genuine non-finite results pass through as above.
4. **Statistics** skip missing values and report how many they used (`count`, `missingCount`). A
   non-finite input makes the statistic non-finite (`"NaN"`) rather than being silently skipped.
5. **Non-finite values appear in responses only.** Request bodies and parameters carry finite numbers;
   a non-finite number in a request is a `400`. (The generated Python client cannot write them
   correctly; §2.5.)

### 2.4 Bulk numeric data is binary (D-6)

Measured on one field of 10⁶ doubles (2026-11-27): JSON text is 20.9 MB (9.0 MB gzipped) and takes
170 ms to parse in Python; binary float64 is 8.0 MB and parses in 9 ms (`struct`), or zero-copy with
NumPy. Large numeric APIs (Zarr/N5 over HTTP, HSDS, Arrow Flight) use JSON for metadata and binary
for arrays; so does this API. **The guide sets requirements, not a particular library.**

1. **Metadata and small results are JSON** (mesh information, statistics, a probe of a few points),
   with §2.3's `null` and non-finite strings.
2. **Bulk numeric arrays are binary**, and the binary format must:
   - carry values as **IEEE-754** with their native NaN and ±Infinity;
   - represent **missing values separately from the numbers** (a validity mask), never as NaN or a
     sentinel (§2.3);
   - be **self-describing**: element type, shape and order, **byte order**, and the request it answers
     (variables, points, times), so a reader needs no out-of-band convention;
   - be a **widely used open standard with maintained readers in Java, Python and JavaScript**, and
     readable straight into NumPy (and pandas or Polars) from pyvcell;
   - stream or chunk large results, and compress well (byte-shuffled zstd or LZ4, or HTTP
     `Content-Encoding`).
3. **One resource, two representations, chosen by `Accept`** (content negotiation, not a second
   endpoint; §1.3). Both carry the same data and meaning.
4. **A size threshold:** above a published number of values, the JSON form answers `413` with a problem
   pointing to the binary form.
5. **The format is chosen in the pilot** (plan Phase 2), against these requirements, from these
   candidates:

| Candidate | Fit | Notes |
|---|---|---|
| **Zarr / N5 chunks** | already used by VCell's exports and by pyvcell | chunked and compressed by design; missing values need a convention (a separate mask array; Zarr's `fill_value` alone is a sentinel) |
| **Apache Arrow IPC** | meets every requirement out of the box (validity bitmap, native non-finite, byte order recorded, schema metadata) | a new dependency for VCell; spike in §2.5 passed in Java and Python |
| Raw little-endian float64 + JSON header (and a separate validity bitmask) | simplest to produce | every convention (shape, order, mask, metadata) is VCell's own, so clients write custom readers |

### 2.5 Client behaviour with non-finite values and Arrow (spike, 2026-11-27)

Spikes: `tools/api-spikes/java-client/` (now 20 checks), `tools/api-spikes/python-ts-client/`,
`tools/api-spikes/arrow/`; each has a `run.sh`.

| Client | Reading `null`, `"NaN"`, `"Infinity"` in a `Float64` field | Writing them |
|---|---|---|
| **Java** (generated, shared records, `Float64` mapped onto `Double`) | ✓ `null` → `null`; strings → `Double.NaN`, ±`Infinity` | ✓ writes the strings and `null` (Jackson default) |
| **Python**, `Float64` as generated (an `anyOf` wrapper) | parses, but non-finite values stay Python **strings** (`'NaN'`), mixed with floats | ✗ broken: numbers become `"0.0"`, `null` becomes `"null"` |
| **Python**, `Float64` mapped onto `float` (`--schema-mappings Float64=float`) | ✓ strings → real `nan`, ±`inf`; ✗ `null` rejected (the generator drops `nullable` here) | ✗ `to_json()` writes bare `NaN` (invalid JSON); Pydantic's `model_dump_json()` turns non-finite into `null` |
| **TypeScript** (typescript-angular) | `Float64` is generated as an empty interface `{}`: no type safety | — |
| **Arrow IPC** (one binary candidate), Python (pyarrow 26) ↔ Java (arrow-vector 15) | ✓ both directions: `null` via the validity bitmap stays distinct from NaN; ±Infinity and finite values bit-exact; schema metadata carried | ✓ |

**Rules from it:**
1. **Numeric arrays of any size use the binary form** (§2.4); the JSON `Float64` form is for small
   results and statistics. The binary candidate tested (Arrow) had no problems in either language.
2. **Non-finite values appear in responses only** (§2.3 rule 5), so no client has to write them.
3. **Python client:** `Float64` is mapped onto `float`, and nullable numeric fields get their own
   schema (or a post-generation fix, as `tools/python-fix.sh` already applies) so `null` is accepted.
   _Verified in the pilot._
4. **TypeScript client:** `Float64` is mapped (import mapping) onto a hand-written type
   `number | "NaN" | "Infinity" | "-Infinity"` with a `toNumber()` helper. _Verified in the pilot._
5. **If Arrow is chosen:** Arrow Java needs `--add-opens=java.base/java.nio=ALL-UNNAMED` on the JVM
   (server and desktop launcher).
6. **Byte order:** IEEE-754 fixes the bit layout of a value, not byte order, so the binary format must
   record it (§2.4): Arrow records it in the stream's schema (`endianness`), Zarr in the array's
   `dtype` (`<f8`). Servers write little-endian. Every platform VCell supports is little-endian; big-endian clients are still handled
   correctly by Arrow.

## 3. Testing (D-23)

vcell-rest's tests already run against real dependencies (`@QuarkusTest` with Dev Services:
Keycloak and PostgreSQL in Testcontainers), test through both raw HTTP (RestAssured) and the
generated Java client, check the database behind a response, and fail CI when the committed spec
drifts from the code (`tools/validate-openapi-spec.sh`). These rules build on that.

### 3.1 Required for every endpoint

1. **A generated-client test** of its normal behaviour, through the published contract.
2. **An access-matrix test.** One parameterized test per endpoint, fed by a table of callers
   (anonymous, owner, another user, a group member where groups apply, admin) and the status each
   must get. The table is the endpoint's authorization rule (D-19), written down.
3. **Error-case tests** for invalid input (`400`) and missing resources (`404`), and for size caps
   where the endpoint has them (D-10). They assert the status and the stable error code (D-14),
   **not** message text or Java exception class names.
4. **Responses validated against `tools/openapi.yaml`.** The raw-HTTP tests attach an OpenAPI
   validation filter (for example `swagger-request-validator`'s RestAssured filter), so every response
   is checked for field names, required fields, status codes and content types.
5. **A DB-free round-trip test for each mapper** the endpoint uses, in `vcell-api-mapping`
   (§2.1 rule 4). These run in milliseconds without Docker.

### 3.2 Additional for data endpoints

6. **An equivalence test** against the RPC method it replaces: the same dataset read both ways
   returns the same values (exactly for raw data; within a stated tolerance where formats differ).
7. **Payload-size and latency budgets**, measured against the RPC baseline.

### 3.3 How tests are written

- **One behaviour per test**, named after it (`saveRejectsUnchangedModelWithSameName`), with shared
  setup in fixtures. A test that checks several behaviours in sequence hides which one broke.
- **`assertThrows`** for expected failures, never try/catch followed by a forced failure.
- **No shared static state.** Configuration goes through Quarkus test profiles (`@TestProfile`),
  not `PropertyLoader` statics or system properties, so results do not depend on test order.
- **Each test cleans up the data it creates** (as today's `@AfterEach` cleanup does).
- **Tests against a real deployment** (live data shapes that fixtures miss) are gated on an
  environment variable, following `LiveVCInfoContainerRoundTripTest`, and never run by default.
- Manual harnesses do not live in `src/test` (move or delete `ManualTestExport`).

_State on 2026-11-27: 51 of the spec's 60 operations are called through the generated client in
some test; cross-user and anonymous cases exist in some classes but not as a per-endpoint matrix; no
response validation against the spec yet._

## 4. Tooling: rules as code

Every rule in this guide that a tool can check is checked by one, so it does not depend on a
reviewer (human or AI) remembering it. Tools are established, widely used projects; versions are
pinned.

### 4.1 Which tool enforces which rule

| Rule | Tool | Where it runs |
|---|---|---|
| §1.1 nouns not verbs; §1.2 no `GET` body; §1.3 discriminated `oneOf`, mode-flag review; §2.1 named DTO schemas; §3.1 documented 401/404; operationId casing | **Spectral** with `tools/spectral/vcell-api.spectral.yaml` | Fast lane, on `tools/openapi.yaml` |
| D-15 compatibility (no silent breaking change, including when restyling existing endpoints) | **oasdiff**: PR spec vs master spec; a breaking change needs an explicit label | Fast lane |
| §3.1.4 responses match the spec | **swagger-request-validator** (RestAssured filter) in the HTTP tests; **Schemathesis** (spec-driven generated requests) against a test deployment | Quarkus tests; nightly |
| §2.1 `vcell-api-dto` has no domain dependency; resources return only DTOs; no records in vcell-rest; mappers only in `vcell-api-mapping` | **ArchUnit** tests, and **Maven Enforcer** `bannedDependencies` | Fast lane |
| Java compatibility of `vcell-api-dto` for the desktop | **japicmp** (or Revapi) against the previous release | Fast lane |
| SQL built by concatenation, reflective dispatch on caller input, other VCell-specific bans | **Semgrep** (VCell rules in the repo) and **SpotBugs + FindSecBugs**; **CodeQL** already runs | Fast lane (Semgrep), CI-full (SpotBugs) |
| Compile-time bug patterns, nullness | **Error Prone** (+ **NullAway** in new modules) | build |
| Workflows and scripts | **actionlint** (runs ShellCheck on `run:` blocks), **ShellCheck** on `tools/` and `docker/` scripts | Fast lane |
| Dockerfiles | **hadolint** | Fast lane |
| Vulnerable software in images (e.g. an old JRE) | **Trivy** on the built images | CI-full |
| Secrets committed by mistake | **gitleaks** | Fast lane |
| Python (pyvcell, python-restclient, utilities) | **ruff** (lint + format), **mypy** strict | per package |
| TypeScript (webapp-ng, webapp-viewer) | **ESLint** with `typescript-eslint` strict-type-checked and `angular-eslint`; `tsc --strict` | per package |
| Kubernetes manifests (vcell-fluxcd) | **kubeconform**, **kube-linter** | vcell-fluxcd CI |

### 4.2 Rollout on legacy code

- **Baseline, then hold the line.** Each tool records today's findings as a baseline and fails only
  on new ones, so new code meets the rules immediately and the backlog shrinks over time without
  blocking work.
- **New code has no baseline.** `vcell-api-dto`, `vcell-api-mapping` and every `/api/v2` endpoint
  must pass every check from its first commit. The frozen `/api/v1` keeps a baseline.
- **Fast checks run in the Fast lane** (seconds each: Spectral, oasdiff, ArchUnit, Semgrep,
  actionlint, ShellCheck); slower ones (SpotBugs, Schemathesis, Trivy) run in CI-full or nightly.
- **The same checks run locally**: a pre-commit configuration for the fast ones, and Claude Code
  hooks that run the relevant linter after each edit.

### 4.3 Spectral ruleset

`tools/spectral/vcell-api.spectral.yaml` extends Spectral's recommended OpenAPI rules with the
guide's rules. Rules for questions still open in this guide are kept in the file, commented out, until decided;
listed named actions (§1.1) are exempted from the verb rule by `overrides` entries.

```bash
npx @stoplight/spectral-cli@6.13.1 lint tools/openapi.yaml \
    --ruleset tools/spectral/vcell-api.spectral.yaml
```

First run against master's `/api/v1` spec (2026-11-27, 52 paths, 60 operations). Under §6 these
endpoints are not fixed in place; the list shows what their `/api/v2` equivalents must do differently.

| Finding | Count | Notes |
|---|---|---|
| `vcell-path-no-verbs` (error) | 17 | `Simulation/{simID}/startSimulation`, `stopSimulation`; `fieldData/analyzeFile`, `copyModelsFieldData`, `createFromFile`, `createFromSimulation`, `delete/{fieldDataID}`, `save`; `optimization/{optId}/stop`; `publications/{id}/publish`; `solver/getFVSolverInput`, `getFVSolverInputFromVCML`; `users/forgotLegacyPassword`, `mapUser`, `processMagicLink`, `requestRecoveryEmail`, `unmapUser/{userName}` |
| `vcell-documents-404-for-path-ids` (warn) | 6 | e.g. `GET /bioModel/{bioModelID}/summary`, `GET /publications/{id}` |
| `vcell-operation-id-camel` (warn) | 11 | acronyms written in capitals (`getVCML`, `getFVSolverInput`, `IDs`); §1.4 writes them as words |
| `vcell-boolean-query-param-review` (info) | 4 | `includePublicAndShared` on the four summary lists: a filter, acceptable |
| `vcell-operation-summary` (warn) | 17 | operations without a `summary` (§5.2) |
| `vcell-tag-not-class-name` (warn) | 59 | every tag is a class name ("Bio Model Resource"); §5.1 |
| `vcell-error-is-problem-json` (error) | 179 | every v1 error response is `application/json` (`VCellHTTPError`), not Problem Details; §9 |
| `vcell-documents-error-responses` (warn) | 3 | operations that document no error response; §9.3 |
| `vcell-path-casing` (error) | 34 | capitalised or camelCase segments (`Simulation`, `bioModel`, `fieldData`) and parameters (`{simID}`); §1.4 |
| `vcell-path-param-descriptive` (warn) | 8 | bare `{id}` parameters; §1.4 |
| Spectral built-ins | 144 | operations without a tag (60) or description (44); security scope declarations (33, a SmallRye output detail); unused components (6); one malformed `termsOfService` |

Already compliant: no `GET` has a body, no `oneOf` lacks a discriminator, no request or response
uses an inline schema, and every secured operation documents 401.

## 5. Documentation

The published spec is read by people (Swagger UI) as much as by generators. It must be navigable.

### 5.1 Groups are resource families, named for users

- **Each resource class carries an `@Tag`** with a user-facing name and a one-sentence
  description. Never the default derived from the Java class name ("Bio Model Resource").
- **Tags are declared once at the top level of the spec, in a deliberate order:** core documents
  first, admin last. Proposed order: *BioModels, MathModels, Geometries, Images, Simulations,
  Simulation data, Exports, Field data, Publications, Optimization, Users & accounts, Admin.*
- **Exactly one tag per operation.**

### 5.2 Every operation is self-describing

- A **`summary`** of 3–6 words (shown in the collapsed list) and a **`description`** (shown when
  expanded): what it does, who may call it (the access matrix, §3.1), size limits, and anything
  non-obvious (for a `POST` query, which §1.2 condition applies).
- `operationId`s are stable and readable: they are method names in four generated clients (D-17).
- Deprecated operations say what replaces them (§6.1).

### 5.3 Swagger UI configuration

```properties
quarkus.swagger-ui.doc-expansion=none                # start collapsed
quarkus.swagger-ui.filter=true                       # search box
quarkus.swagger-ui.operations-sorter=method
quarkus.swagger-ui.default-models-expand-depth=-1    # hide the full schema list
quarkus.swagger-ui.deep-linking=true                 # links to one operation
quarkus.swagger-ui.display-operation-id=true         # the generated-client method name
# tags-sorter is NOT set, so the declared tag order (5.1) is kept
```

A second level of grouping (`x-tagGroups`, e.g. "Models" → BioModels, MathModels, Geometries,
Images) is shown by Redoc or Scalar, which read the same spec; one of them may be published next to
Swagger UI as the browsable reference.

### 5.4 Audiences

Admin and internal operations are in the `Admin` tag, last, or filtered out of the public document
with a SmallRye `OASFilter`; the generated clients may use the full document.

The spec's `info` block is real: actual terms of service (or none), and a working contact address.

Spectral enforces: an `@Tag` on every operation that is declared at the top level, a `summary` and a
`description` on every operation (§4).

## 6. Versioning

### 6.1 Within a major version: additive only

- **Allowed without a new version:** new endpoints; new optional request fields and parameters; new
  response fields; new enum values, provided clients tolerate unknown ones.
- **Not allowed:** removing or renaming paths, fields or enum values; making something required;
  changing a type or a meaning. **oasdiff** fails the build on these (§4.1).
- **A necessary break is done per endpoint by expand, migrate, contract:** add the new form beside
  the old; mark the old `deprecated: true` in the spec and send `Deprecation` (RFC 9745) and
  `Sunset` (RFC 8594) response headers naming the replacement; move the clients; remove the old form
  when telemetry shows it unused by supported clients.
- **Generated clients tolerate additions** (D-15). The Python generators (in-repo and pyvcell) are
  configured to ignore unknown response fields, and the TypeScript and Java clients likewise, so an
  additive change never breaks a client. Without this, every addition is a breaking change.
- `info.version` versions the API contract (semantic: minor for additions, major only with a new path
  version), separately from the VCell build version. API changes are listed under "Notes for API
  consumers" in CHANGELOG.md.

### 6.2 Major versions: `/api/v2` is the clean break

- A major version is a new path prefix. It is rare and deliberate; after `/api/v2` the expectation is
  additive evolution (§6.1), not `/api/v3`.
- **`/api/v2` is the API this guide defines.** `/api/v1` is frozen: security and correctness fixes
  only, no new endpoints. Each v1 endpoint gets a v2 equivalent when its area is worked on; v1 is
  retired by telemetry (§6.3), as v0 is. v1 and v2 run side by side in vcell-rest during the
  transition.
- The `/api/v0` RPC functionality (the migration plan) is built directly in `/api/v2`.

### 6.3 Long-lived clients

- **Every client sends its version** on each request (a header such as `VCell-Client-Version`),
  and the server logs it with the endpoint. Desktop installers live in the field for years; this is
  the evidence for removing anything.
- **A published supported-version policy:** the oldest desktop and pyvcell versions the server
  serves, and a clear error (not a failure deep in a call) for older ones.
- **Capability discovery** (D-16): a version endpoint (replacing `/api/v0/swversion`) reports the
  API versions and optional capabilities the server offers, so a new client can fall back against an
  older server (prod lags master).

## 7. Observability

Every `/api/v2` endpoint gets the same observability from one shared implementation, not per-endpoint
code. It builds on what the cluster already runs: structured ECS JSON logs collected into Loki, and
Prometheus.

### 7.1 One structured log line per request

A single JAX-RS filter writes, for every request:

| Field | Notes |
|---|---|
| method, **route template** | `/simulations/{id}/runs`, never the raw path |
| status, duration, response bytes | |
| caller class | anonymous / user / admin / automation (§8.1) |
| user id | the VCell user, when authenticated |
| client name and version | from the client-version header (§6.3) |
| request id, trace id | §7.2 |

Questions such as "who is crawling the data endpoints" or "which client versions still call v1"
are then one LogQL query.

### 7.2 Request ids and tracing

- Accept a W3C `traceparent` (or create one) and return the request id in a response header and in
  **every error body**, so a user's support message can quote it.
- **OpenTelemetry** (`quarkus-opentelemetry`) instruments HTTP, REST clients, JDBC and AMQP
  messaging, and adds trace and span ids to the logs. Traces go to Tempo (Grafana stack).
- Sampling: a small fraction of requests, plus **every error and every slow request** (tail
  sampling in the OpenTelemetry collector).
- The legacy JMS services (data, db, sched) receive the trace context as a message property, so one
  trace spans the API call and the backend work it waited on.

### 7.3 Metrics

- **Micrometer → Prometheus** (`quarkus-micrometer-registry-prometheus`): request rate, errors and
  duration per route template and status; JVM and connection-pool metrics; domain metrics where they
  matter (bytes served per data endpoint, queue depth, job duration).
- **Labels are route templates, methods and status codes, never ids or user names**, so the number
  of series stays bounded.
- Alerts at least on: 5xx rate, p95 latency per route, out-of-memory pod restarts.

### 7.4 Health

SmallRye Health **liveness** and **readiness** checks that test real dependencies (database reachable,
message consumers alive), so Kubernetes restarts a pod that is up but not working (#2032).

### 7.5 Hygiene

- Never log tokens, passwords, or request bodies containing personal data.
- Log levels mean the same everywhere: `ERROR` needs attention, `WARN` is unexpected but handled,
  `INFO` is the per-request line and lifecycle events, `DEBUG` is off in production.
- **Writes are audited:** who changed what, when, and through which client, kept separately from
  debug logs.

## 8. Automated and agentic clients

Scripts, pipelines and AI agents use the API in patterns people don't: parallel fan-out, retry loops,
breadth-first crawling, and casual use of expensive computations, often with a person's own
credentials. They are welcome as clients; the API makes them **identifiable, bounded and
observable**.

### 8.1 Identification

- Automation uses **its own credentials**: Auth0 machine-to-machine clients or personal access
  tokens, separate from interactive login, with a claim that marks them as automation. The caller
  class in §7.1 comes from this.
- Every client sends the client-version header (§6.3); anonymous clients without one get the
  strictest limits (§8.3).

### 8.2 Scopes and least privilege

- Automation tokens are **read-only unless a write scope is granted explicitly**.
- Destructive or publicly visible actions (delete, publish, share, start large runs) need a scope
  that is never a default, or an interactive token. _To agree with D-18 and D-19._

### 8.3 Rate, quota and cost limits

- **Token-bucket limits per identity and per caller class**, with expensive endpoints weighted by
  cost (a kymograph costs more than a status read), and **concurrency limits** on heavy work.
- Over the limit: `429 Too Many Requests` with `Retry-After`. Limits are published in
  `RateLimit-Policy` / `RateLimit` response headers so a well-behaved client can pace itself.
- Coarse per-IP limits at the ingress; per-identity and cost limits in the application.
- Anonymous access has the strictest tier.
- Every collection is paginated with a bounded page size, and every data read has size caps
  (D-10, D-11); no single request or crawl can walk an unbounded collection.

### 8.4 Safe retries

- `POST`s that create work (runs, exports, jobs) accept an **`Idempotency-Key`** header; a retry with
  the same key returns the original result instead of starting the work twice.
- Finished, immutable results carry `ETag` and `Cache-Control`, so repeated reads are cheap.

### 8.5 Watching for patterns

Dashboards and alerts per identity and caller class, on the §7.1 log line: distinct resources touched
per hour, fan-out, error ratio, compute cost. For example, more than N distinct simulations read per
hour by one identity is an alert, which would have flagged the November 2026 crawl of public
simulation data on its first day.

### 8.6 A sanctioned path

- A documented bulk-data route (exports, Zenodo archives) and a published usage policy, with
  `robots.txt` (and `ai.txt`) for the public pages, so large-scale readers have a legitimate option.
- Good operation descriptions (§5) so agents call the API correctly.
- _Proposed:_ a small, scoped MCP server over `/api/v2` (through pyvcell) as the supported way for LLM
  agents to work with VCell: a known, observable entry point instead of agents improvising against
  raw endpoints.

## 9. Errors (D-14)

Errors use **RFC 9457 Problem Details** (`Content-Type: application/problem+json`). They replace v1's
`VCellHTTPError { exceptionType, message }`, which put Java class names and raw exception messages on
the wire.

### 9.1 The error body

```json
{
  "type": "/api/problems/name-conflict",
  "title": "A model with this name already exists",
  "status": 409,
  "detail": "You already have a BioModel named 'TestBioModel'.",
  "instance": "/api/v2/biomodels",
  "requestId": "4bf92f3577b34da6a3ce929d0e0e4736",
  "errors": [ { "field": "name", "code": "duplicate", "message": "Choose a different name." } ]
}
```

| Member | Rule |
|---|---|
| `type` | **The machine-readable error kind.** A relative URI `/api/problems/<slug>` from the problem catalogue (§9.3); stable forever once published; clients branch on it. (It may later resolve to a help page.) |
| `title` | The fixed human-readable label of that type. |
| `status` | The HTTP status, repeated. |
| `detail` | Written for the caller and safe to show to a user: **never** SQL, file paths, stack traces, Java class names, or internal host names. |
| `instance` | The request path. |
| `requestId` | Extension: the request id (§7.2), so a user's report can be matched to the server log. |
| `errors[]` | Extension, for validation failures: one entry per offending field (`field`, `code`, `message`). |

The full internal detail (exception, stack trace, cause) goes to the server log only, under the same
`requestId`.

### 9.2 Status codes

| Status | Use |
|---|---|
| `400` | Malformed request **or failed validation**, with `errors[]`. (`422` is not used.) |
| `401` | Not authenticated, or the token is invalid or expired. |
| `403` | Authenticated, and the caller **can see** the resource but may not do this to it. |
| `404` | Not found, **or the caller cannot see it** (a private resource of another user), so its existence is not revealed. |
| `409` | Conflict: duplicate name, or the resource's state does not allow the operation. |
| `412` | Precondition failed (`If-Match` with a stale ETag). |
| `413` | Request or result over the size limits (D-10). |
| `429` | Rate or cost limit exceeded (§8.3), with `Retry-After`. |
| `500` | Unexpected server fault: a bug. |
| `503` | A dependency is unavailable (database, message broker, simulation storage), with `Retry-After`. Clients retry later instead of reporting a bug. |

A domain exception never maps to one status wholesale (v1 sends every `DataAccessException` as `500`);
each cause maps to its own problem type and status.

### 9.3 The problem catalogue is part of the contract

- Problem types are defined once, in `vcell-api-dto` (a `ProblemType` catalogue with stable slugs,
  titles and statuses; the `Problem` record is the RFC 9457 body).
- The spec lists the catalogue, and **every operation documents which problem types it can return**.
- Adding a problem type is an additive change; renaming or removing one is a breaking change (§6.1).

### 9.4 Mapping, on both ends (§2.1)

- **Server:** one exception mapper in vcell-rest turns domain exceptions into problem types, through
  `vcell-api-mapping`. It replaces v1's per-type handlers and catch-all.
- **Desktop:** the same mapping module turns a problem type back into the domain exception the
  desktop already handles (e.g. `name-conflict` → `ServerRejectedSaveException`), so existing desktop
  code keeps working while Java class names leave the wire.
- Python, TypeScript and pyvcell clients branch on `type`.

### 9.5 Logging and batches

- `4xx` are client outcomes, logged at `INFO` (the per-request line, §7.1) or `DEBUG`; `5xx` are logged
  at `ERROR` with the stack trace and `requestId`.
- Batch endpoints report results **per item** where items can fail independently, rather than failing
  the whole batch for one bad item.

Spectral enforces: every `4xx`/`5xx` response uses `application/problem+json`, and every operation
documents at least one error response (§4).

## 10. Authorization (D-19)

Authorization follows the OWASP API Security Top 10, whose first and fifth risks are **broken
object-level authorization** (a caller passes an id that is not theirs and gets the object) and
**broken function-level authorization** (a caller reaches an operation meant for others). Both have
occurred in VCell's legacy API.

### 10.1 Two layers, both deny-by-default

1. **Function level: who may call this operation at all.** Declared on every endpoint: public
   (anonymous allowed), authenticated, a role, or a token scope (§8.2). An endpoint without an
   explicit declaration refuses every call, and a build check (ArchUnit, §4.1) fails if any resource
   method lacks one.
2. **Object level: may this caller act on this object.** Checked **every time an id arrives**, by
   one central policy; never re-implemented in an endpoint.

### 10.2 One `AccessPolicy`

- A single Java policy component (`canRead`, `canWrite`, `canShare`, `canPublish`, …) used by every
  `/api/v2` endpoint, over VCell's existing model: **owner**, **`GroupAccess`** (*All* = public,
  *None* = private, *Some* = shared with named users) and roles.
- List queries apply the same rules as filters, so a list and a direct fetch cannot disagree.
- Curation status (`versionFlag`: Current, Archived, Published) is **not** access control; it
  restricts changes (e.g. a published model cannot be deleted), separately.
- External engines (Zanzibar/OpenFGA, OPA) were considered and are not needed for VCell's
  ownership-plus-sharing model.

### 10.3 Rules

| Object | Read | Change |
|---|---|---|
| Model documents (BioModel, MathModel, Geometry, Image) | owner; group members if shared; anyone if public | owner only |
| **Simulation results** | **inherit** the read access of the simulation's documents: public and published models' results are readable by anyone, including anonymous callers; private results by the owner and group | — |
| Exports, jobs, runs | their creator | their creator |
| Field data | owner, and those it is shared with | owner |
| Publications | anyone | curator / publication editor role |
| Admin operations | admin role | admin role |

- **Group sharing is read-only.** Group members can read a shared document; only the owner changes
  it.
- **Anonymous callers** see public and published objects only, under the strictest rate tier
  (§8.3). The shared `vcellguest` account is retired in `/api/v2` (D-18, #2220).
- **Not visible → `404`; visible but not allowed → `403`** (§9.2), so ids reveal nothing.
- **Token scopes and object rules both apply** (§8.2): a scope limits what a token may do, the
  policy limits which objects.

### 10.4 Identifiers

Models, simulations and other existing objects keep their **numeric ids** for now (they are cited in
papers and links). Because they are sequential, authorization never relies on an id being hard to
guess, and the pattern monitoring of §8.5 watches for enumeration.

### 10.5 Audit and tests

Writes are audited (§7.5). Every endpoint's access-matrix test (§3.1) is its rule in executable form.

### 10.6 Where roles come from (decided: VCell's database)

Four kinds of authorization fact, each with one home:

| Kind | Examples | Home |
|---|---|---|
| Identity | the person (Auth0 `sub`, email), linked to a VCell user | Auth0, plus VCell's user mapping |
| **VCell domain roles** | admin, publication editor (curator), support, power user | **VCell's database** |
| Application roles | vcell-ai's admin and LLM budgets | each application (vcell-ai: its Supabase) |
| Object access | owner, group sharing, public | VCell's database (`GroupAccess`); never in a token |

VCell's Auth0 tenant provides single sign-on for several applications. It **proves identity only**;
it does not carry VCell roles.

1. **VCell's database is the single source of VCell domain roles.** Today's Auth0 roles are mapped
   onto the database claims once and then no longer used:
   - Auth0 `admin` → `admins`; Auth0 `curator` → `publicationEditors`;
   - Auth0 `owner` is dropped: it is not a VCell role (ownership is per object, §10.3);
   - `user` is not assigned: it is **derived**, meaning authenticated and linked to a VCell account.
2. **A Quarkus `SecurityIdentityAugmentor`** loads the caller's roles from the database for each
   request (cached for about a minute) and adds them to the security identity, so `@RolesAllowed` and
   the `AccessPolicy` work unchanged. Revoking a role takes effect within the cache lifetime, not at
   token expiry; that is the main reason for this choice.
3. **Other applications ask VCell.** `GET /api/v2/users/me` returns the caller's linked VCell user and
   VCell roles (it replaces v1's `/users/mappedUser`); an application caches the answer for its
   session.
4. **Application roles stay in each application.** VCell's database does not hold roles for other
   applications.
5. **Optional later:** if an application genuinely needs VCell roles inside its tokens (for example,
   one that cannot call VCell), an Auth0 Action may copy them from VCell at login. VCell's database
   stays the source; the copy is only as fresh as the token.

**No superuser role.** Auth0's `owner` role was apparently meant as a superuser above admin; a single
all-powerful role is avoided. Roles stay narrow (least privilege), and an operation that needs more
than admin gets a **specific permission** (for example `canEditPublishedModel`), granted to the few
who need it. Emergency access belongs outside the application (database or cluster access, with an
audit trail).

Tests set roles in the test database (as `TestEndpointUtils` already creates users), not in the
Keycloak dev-service configuration.

Facts behind the decision (2026-11-27): vcell-ai keeps its own application roles in Supabase, reads no
VCell roles or role claims, and forwards the user's Auth0 token to VCell's API, which decides object
access from the mapped VCell user.

## 11. One path to the server: direct-connect mode is retired

The desktop reaches the server only through the API. **Direct-connect mode is retired** (decided
2026-11-27).

**What it is.** `VCellClientDevMain` (vcell-admin) runs the desktop with the server classes
(`DatabaseServerImpl`, `DataSetControllerImpl`) in the same process, talking to Oracle, `/simdata` and
MongoDB directly with database credentials on the developer's machine. It is served by
`LocalVCellConnection*` (vcell-server) and by special cases in the API client
(`ClientServerManager.isVCellClientDevMain()`, `AsynchMessageManager`).

**Why it goes.**
- It is a **second implementation of every desktop-to-server interface.** Keeping it would mean an
  in-process twin for every `/api/v2` endpoint, or a development path that behaves differently from
  production.
- It **bypasses the API and all authorization**, and needs database credentials on laptops; pointed at
  the wrong database it is a direct line to production data.
- **Problems in the API layer** (serialization, authorization, error mapping) never appear in
  development, because that layer is skipped.

**Schedule.**
1. **Now:** the documented developer launch path is `VCellClientMain` against the dev site or a local
   stack (vcell-rest in Quarkus dev mode, or the local stack in `docker/swarm/README_stack_on_mac.md`),
   with its API base URL configurable. Look-and-feel and font setup lives in `VCellClientMain`.
   `README.md` is updated to say so. `VCellClientDevMain` keeps working, frozen, while the desktop still
   uses RPC.
2. **From the first `/api/v2` endpoint:** no in-process implementations. New endpoints have exactly one
   implementation, in vcell-rest, and the desktop facades call only the API.
3. **When the desktop no longer uses RPC:** delete `VCellClientDevMain`, the `isVCellClientDevMain()`
   branches and `LocalVCellConnection*`.

**What replaces it.**
- Debugging client and server together: vcell-rest in Quarkus dev mode locally (live reload, debugger
  attach, database containers), with the desktop pointed at `localhost`.
- Quick UI work: `VCellClientMain` against the dev site.
- AI-assisted UI debugging already uses `VCellClientMain` (the swing-debug bridge,
  `tools/debug-bridge/launch-client.sh`).

**Admin tools are separate.** Server-side tools run by administrators that genuinely need the database
(for example `MathGenerationDebugger`) may keep direct database access as admin tooling; they are not the
desktop client and are not covered by this section.
