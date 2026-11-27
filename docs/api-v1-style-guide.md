# `/api/v1` style guide

Status: **in progress**, started 2026-11-27. These rules are decided by the lead architect, one
design question at a time; the questions and their options are in
[`api-v0-to-v1-migration-plan.md`](api-v0-to-v1-migration-plan.md) §3 (D-1 to D-24). Each rule
below names the question it settles. Questions not listed here are still open.

Every new `/api/v1` endpoint is reviewed against this guide.

## Scope

**The guide applies to existing `/api/v1` endpoints as well as new ones** (D-1). Existing endpoints
that are inconsistent with it are brought into line. Each change to an existing endpoint needs a
transition plan, because webapp-ng, pyvcell and the desktop call these endpoints; that plan follows
the compatibility rules (D-15, still open).

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

_Open: whether a named action (`POST /{resource}/{id}/{action}`) is ever allowed when no natural
resource exists, and if so, how exceptions are listed._

### 1.2 Queries use `GET` (D-3, D-11)

Queries use `GET` with query parameters. Arrays are passed as repeated parameters
(`?id=1&id=2`); _the exact array encoding is still to agree_.

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

## 2. Representations

### 2.1 Wire types live in one shared DTO module (D-5)

The contract is **code-first**: Java records in a shared DTO module are the source of truth, and the
OpenAPI spec (`tools/openapi.yaml`) is generated from them. Design-first (hand-written spec,
generated code) was considered and not chosen, because the server and its main client are both Java
and generated Java models are verbose, mutable and churn with generator upgrades.

| Module | Java package | Contents | May depend on |
|---|---|---|---|
| **`vcell-api-dto`** | `org.vcell.api.dto` (sub-packages per resource family) | Java records only: the request and response bodies of every `/api/v1` endpoint | Jackson and MicroProfile OpenAPI **annotations** only. No VCell module. |
| **`vcell-api-mapping`** | `org.vcell.api.mapping` | Mappers between DTOs and VCell domain objects, in both directions | `vcell-api-dto` and the domain modules (vcell-core, …) |

Rules:

1. **Every `/api/v1` request and response body is a type from `vcell-api-dto`.** Resources never
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
   Java model (target: openapi-generator schema/import mappings with model generation off; see
   open items).

**Package names are chosen for clarity, not to mirror vcell-core.** vcell-core's packages
(`cbit.vcell.*`, `org.vcell.*`, mixed) are historical, and renaming them wholesale would be
disruptive. The DTO and mapping modules get clean, consistent names of their own; the mapping
module is the one place that bridges the two naming worlds.

These modules replace v1's records scattered across `vcell-rest` (`models/`, `handlers/`,
`handlers/dto/`, `Simulations/`) and the generated Java models in `vcell-restclient`. The v0
equivalents (`vcell-api-types`, `DTOModelTransformerV0`) retire with v0.

_Open:_
- _DTO type naming (proposed: bare nouns within `org.vcell.api.dto`, e.g. `BioModelSummary`,
  `SimulationStatus`; today's code mixes `*Representation`, `*Rep`, `*Record`, `*Dto` and bare names)._
- _A spike to confirm the generated Java client can use the shared records (schema/import mappings);
  fallback is a small hand-written Java client over the same records._

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
