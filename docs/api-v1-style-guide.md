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
