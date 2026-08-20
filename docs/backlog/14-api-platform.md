# Group: API, Platform & Database — 30 issues

The server side: the Quarkus REST service, the legacy API being retired, authentication, the
database layer, and messaging.

**Epics:** `#1040`, `#1147`, `#1339` — **three overlapping containers for one migration**, see
[04-epic-map.md](04-epic-map.md#d-1040--1147--1339--three-quarkusapi-epics).

**Strategic decision required** — see
[04-epic-map.md](04-epic-map.md#decision-1--is-the-postgresql-migration-still-happening). Whether
`#172`, `#1994` and much of the DB work matters depends on it.

---

## Sub-themes

### The legacy-API migration (7)

`#1147` (epic, `Blocked`, rated **`Byzantine (1)`**), `#1152` (health check → API package),
`#1548` (group modification, Priority 8/4), `#1549` (species and reactions control, Priority 9/5),
`#1555` (export control — filed under
[13-export-visualization.md](13-export-visualization.md)), `#1511` (remove the legacy guest-token
requirement), `#1488` (give the API client access to the User object).

This is the largest coherent programme in the group and it is progressing endpoint by endpoint.
`#1147`'s checklist names the remaining areas: Events, Common, Client, Admin, RPC, Server, Users.

`#1488` reads as a **blocker for the others** — *"The VCell API Client currently can not create a
user object, which is needed for specific object creation within the messaging level"* — but it is
unranked in `Pool` while `#1548`/`#1549` are `Queued`. Worth checking that dependency direction.

`#1147` is `Blocked` but the body does not say by what. That is worth a sentence; a blocked epic
with no named blocker cannot be unblocked by anyone but its author.

### Auth (4)

`#1037` (epic, `Active`, **untouched since July 2024**, 7/11 done), `#1292` (the webapp still uses
**Auth0 dev keys** — Priority 12 / Importance 9, one of the highest-ranked items on the board),
`#1365` (client update dialog steals focus after Auth0 login), `#1423` (feature flags in user
preferences, via a JWT claim).

`#1037`'s four unchecked items are exactly `#1292`'s subject: create a production Auth0 tenant,
register VCell as a Google App for a proper client id, migrate users off the development tenant,
update configuration. **`#1292` is `#1037`'s remaining work, filed separately.** Link them, and
either the epic closes when `#1292` does or `#1292` becomes the epic's checklist.

Running production authentication on a development tenant with Google dev keys is a real operational
exposure, which the Priority 12/9 rank presumably reflects.

### Database (5)

`#840` (DatabaseConstraint exceptions on cleanup and pathological saves — a detailed 1k-char
analysis: *"~260 times a day, the health monitor saves the exact same BioModel"*, Priority 9/7),
`#172` (Oracle→PostgreSQL migration scripts), `#1540` (index the DB for common joins, specifically
the user-ACL joins added with user roles), `#1487` (reduce DB-layer boilerplate), `#1994` (run the
Quarkus suite against Oracle — **55 dialect branches across 33 files tested only on PostgreSQL**).

`#840` describes real, quantified production misbehaviour and is well-ranked. Note it is also a
child of the Postgres epic `#1032`, which makes it hostage to Decision 1 — it should not be, since
the constraint violations happen on Oracle today.

`#1994` is the cost of the half-finished migration made explicit. It is off-board.

### Messaging (2)

`#1153` (integrate VCell messaging with Quarkus, `Shelved`), `#1340` (simulation JMS causes brittle
coupling — a 984-char analysis of the JMS + ActiveMQ + MongoDB arrangement, `Shelved`).

Both `Shelved`, and `docs/MESSAGING.md` records that Artemis is the intended destination for all
three broker stacks. These two should be re-read against that document before either is revived —
`#1153`'s framing (evaluate Quarkus messaging implementations) may already be settled.

### API ergonomics and internals (5)

`#1474` (improve API ergonomics — *"hyper specific or vague names… input too granular"*, rated
`Simple (5)`), `#1504` (the Java client object mapper reads any string as a workaround for XML
inside JSON — *"This proves problematic"*), `#1539` (server-side user caching), `#1487`,
`#1551` (users shared on a model cannot remove themselves).

`#1504` describes a workaround that is known to be wrong and is still in place; those tend to get
more expensive, not less.

`#1551` is a small, self-contained user-facing fix with a clear description — another
starter-issue candidate.

### Publication pipeline (3)

`#1706` (the publication submission form generates no confirmation email — reproduced by two
people, `High Priority`, `Active`), `#1707` (the GUI's database panel shows the wrong date for
published models — *"this is not a problem with the website, this is a problem with how the
publication is recorded in the VCell client"*, `High Priority`), `#1885` (published MathModels not
shown in Alpha but fine in Rel).

Three `High Priority`-adjacent publication defects, none of them ranked numerically. They are
plausibly related — all three concern how publication state reaches the client — and worth
investigating together.

### Deployment and compliance (3)

`#1601` (guest login immediately closes the DB connection on Alpha — `High Priority`, Priority 4/2,
with the full error text), `#1608` (a multi-user install can disrupt the main admin install —
`Shelved`, but the described failure mode is that non-admin users find no VCell installed),
`#1648` (California law 1798.501(b) age-collection requirement).

`#1648` is a legal-compliance question, not an engineering one, and it needs a lawyer's or
administrator's answer before any code is written. It sits unranked in `Pool` with no assignee.
→ escalate rather than rank.

### Cross-cutting (1)

`#1786` — shared BioModels returned duplicated from `/api/v0/biomodel` due to a two-level
group-access join fan-out. Precise 2.8k-char diagnosis, off-board. Note this is the *legacy* v0
API, so its value depends on how long v0 survives the `#1147` migration.

---

## Recommendations

1. **Collapse the three epics to one** (`#1147`); close `#1040` (the service exists and ships).
2. **Link `#1292` to `#1037`** — they are the same remaining work. Then close `#1037` when
   `#1292` closes, or convert `#1037` into `#1292`.
3. **Take Decision 1 (Postgres)** before ranking `#172`, `#1994`, `#1540`, `#1487`.
4. **Detach `#840` from the Postgres epic** — it is an Oracle problem happening now.
5. **Check the `#1488` → `#1548`/`#1549` dependency** and reorder if confirmed.
6. **Name `#1147`'s blocker** or unblock it.
7. **Escalate `#1648`** out of engineering.
8. **Investigate `#1706`/`#1707`/`#1885` together** as one publication-state problem.
9. **Board the 4 off-board issues** (`#1365`, `#1423`, `#1786`, `#1994`).

---

## All 30

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1601](https://github.com/virtualcell/vcell/issues/1601) | [Alpha] Connection to database immediately closes when signing in as a guest user | 2025-12 | Queued | 4/2 | Intricate&nbsp;(2) | HP |
| [#1548](https://github.com/virtualcell/vcell/issues/1548) | Group Modification Through The New API | 2025-06 | Queued | 8/4 | Moderate&nbsp;(4) | — |
| [#840](https://github.com/virtualcell/vcell/issues/840) | fix DatabaseConstraint exceptions upon DB Cleanup and pathological Biomodel saves. | 2023-03 | Queued | 9/7 | Intricate&nbsp;(2) | — |
| [#1549](https://github.com/virtualcell/vcell/issues/1549) | Control of Species And Reactions Through The New API | 2025-06 | Queued | 9/5 | Moderate&nbsp;(4) | — |
| [#1292](https://github.com/virtualcell/vcell/issues/1292) | VCell Webapp uses Dev keys  | 2024-07 | Queued | 12/9 | Complex&nbsp;(3) | — |
| [#172](https://github.com/virtualcell/vcell/issues/172) | develop oracle to postgresql data migration and backup scripts | 2022-07 | Pool | — | — | thin |
| [#1037](https://github.com/virtualcell/vcell/issues/1037) | Epic: modern auth with Keycloak and Auth0 OIDC | 2023-11 | Active | — | — | EPIC |
| [#1040](https://github.com/virtualcell/vcell/issues/1040) | Epic: new vcell-rest with Quarkus | 2023-11 | Active | — | — | EPIC |
| [#1147](https://github.com/virtualcell/vcell/issues/1147) | Epic: Use Quarkus Framework and Implement REST API Endpoints For VCell API | 2024-02 | Blocked | — | Byzantine&nbsp;(1) | EPIC |
| [#1152](https://github.com/virtualcell/vcell/issues/1152) | VCell Health Check  in Quarkus needs conversion to API Package | 2024-02 | Pool | — | Moderate&nbsp;(4) | thin |
| [#1153](https://github.com/virtualcell/vcell/issues/1153) | Integrate VCell Messaging with Quarkus | 2024-02 | Shelved | — | Intricate&nbsp;(2) | — |
| [#1339](https://github.com/virtualcell/vcell/issues/1339) | Epic: Simulation Control Update | 2024-08 | Pool | — | Complex&nbsp;(3) | EPIC |
| [#1340](https://github.com/virtualcell/vcell/issues/1340) | Simulation JMS causes brittle coupling | 2024-08 | Shelved | — | Complex&nbsp;(3) | — |
| [#1365](https://github.com/virtualcell/vcell/issues/1365) | VCell Client Update appears after login looses focus with auth0 login | 2024-10 | **off-board** | — | — | thin |
| [#1423](https://github.com/virtualcell/vcell/issues/1423) | Feature Flags in User Preferences | 2025-01 | **off-board** | — | — | — |
| [#1474](https://github.com/virtualcell/vcell/issues/1474) | Improve API Ergonomics | 2025-04 | Pool | — | Simple&nbsp;(5) | — |
| [#1487](https://github.com/virtualcell/vcell/issues/1487) | Introduce Functional Programming to DB Layer | 2025-04 | Pool | — | Moderate&nbsp;(4) | — |
| [#1488](https://github.com/virtualcell/vcell/issues/1488) | Give VCell Api Client Access to User Object | 2025-04 | Pool | — | Moderate&nbsp;(4) | — |
| [#1504](https://github.com/virtualcell/vcell/issues/1504) | Less Heavy Handed Middleware for Java Client Object Mapper | 2025-05 | Pool | — | — | — |
| [#1511](https://github.com/virtualcell/vcell/issues/1511) | Remove Legacy Guest Token Requirement with Requests | 2025-05 | Pool | — | — | — |
| [#1539](https://github.com/virtualcell/vcell/issues/1539) | User Caching on the Server | 2025-06 | Pool | — | — | — |
| [#1540](https://github.com/virtualcell/vcell/issues/1540) | Indexing the DB For Common Joins | 2025-06 | Pool | — | — | — |
| [#1551](https://github.com/virtualcell/vcell/issues/1551) | Users who've been added to a shared model should be able to remove themselves | 2025-06 | Pool | — | — | — |
| [#1608](https://github.com/virtualcell/vcell/issues/1608) | Multi-User Install Can Distrupt Main Admin Install | 2025-12 | Shelved | — | — | — |
| [#1648](https://github.com/virtualcell/vcell/issues/1648) | VCell does not comply with California Law 1798.501(b) | 2026-02 | Pool | — | — | — |
| [#1706](https://github.com/virtualcell/vcell/issues/1706) | Publication Submission Pipeline seems to be broken | 2026-06 | Active | — | — | HP |
| [#1707](https://github.com/virtualcell/vcell/issues/1707) | VCell database panel on front page of GUI shows incorrect date for list for published … | 2026-06 | Pool | — | — | HP |
| [#1786](https://github.com/virtualcell/vcell/issues/1786) | Shared BioModels duplicated in /api/v0/biomodel — group-access join fan-out (duplicate… | 2026-07 | **off-board** | — | — | — |
| [#1885](https://github.com/virtualcell/vcell/issues/1885) | Published MathModels in not shown in VCell Alpha. VCell-Rel is OK | 2026-08 | Pool | — | — | — |
| [#1994](https://github.com/virtualcell/vcell/issues/1994) | Run the Quarkus test suite against Oracle too: 55 dialect branches across 33 files are… | 2026-08 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
