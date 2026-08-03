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
