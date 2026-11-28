# Taking apart `getTimeSeriesValues`: detailed plan

> **Decided 2026-11-27: the target is `/api/v2`,** not `/api/v1` (style guide §6). `/api/v1` is frozen;
> wherever this document says "v1" for new endpoints, read "v2". The style guide is
> [`api-style-guide.md`](api-style-guide.md).

Status: **in progress**, updated 2026-11-28. §3 (decomposition) and §4 (P-1 to P-5) are **decided**
(P-1 to P-5 confirmed by the lead architect 2026-11-28); the DTO sketches stay PROPOSED until built.
This is the style guide's worked example (plan Phase 1 item 2). First catch-all RPC worked through under the style
guide's rule §1.3 ("one endpoint, one purpose, one response type",
[`api-style-guide.md`](api-style-guide.md)). Endpoint shapes below are **PROPOSED — needs
agreement**; paths are placeholders.

## 1. What the RPC does today

`DataSetController.getTimeSeriesValues(OutputContext, VCDataIdentifier, TimeSeriesJobSpec)`
→ `TimeSeriesJobResults` (data service; `DataSetControllerImpl.getTimeSeriesValues`).

**Request (`TimeSeriesJobSpec`, `org.vcell.util.document`):**

| Field | Meaning |
|---|---|
| `variableNames[]` | one or more variables (or functions) |
| `indices[var][]` | mesh indices per variable: volume or membrane element numbers |
| `roi[var]` (`BitSet`) | alternative to `indices`: a region given as a bitmask over the mesh |
| `startTime`, `endTime`, `step` | time window, with every `step`-th saved time |
| `calcSpaceStats` | per time point, statistics over the indices or region |
| `calcTimeStats` | per index, statistics over the time window |
| `crossingMembraneIndices[var][]` | for volume variables sampled at a membrane: which membrane element each index crosses (interpolates across it) |
| `vcDataJobID` (with `isBackgroundTask`) | when "background", the server sends progress `DataJobEvent`s |

Plus `OutputContext`: the user's own functions, which the server evaluates if they are requested
(decision D-9).

**Response: three subclasses, chosen by the two flags:**

| `calcSpaceStats` | `calcTimeStats` | Result | Shape |
|---|---|---|---|
| false | false | `TSJobResultsNoStats` | `values[var][1 + index][time]` (row 0 holds the times) |
| true | false | `TSJobResultsSpaceStats` | per var, per time: min, max, unweighted and weighted mean and sum; per var: total space |
| false | true | `TSJobResultsTimeStats` | per var, per index: min, max, mean over time |
| true | true | `TSJobResultsTimeStats` | time statistics combined with spatial weights |

**Server paths inside:** post-processing data, function variables (evaluated from their
arguments), a "special" path for very large index sets (`ABSOLUTE_SIZE_LIMIT`), where time statistics
throw "not yet implemented", and membrane-crossing interpolation.

**Background is not what the name says.** The desktop calls synchronously (vcell-core
`RpcDataServerProxy`): the full results come back in the RPC reply. "Background" only makes the
server emit progress `DataJobEvent`s, which reach the desktop through `/api/v0/events`
(`DataJobEvent` carries no results). The fire-and-forget branch exists only in vcell-api's own
internal proxy. _This corrects the inventory, which describes the background variant as delivering
its result by event._

## 2. Who calls it, and for what

| # | Caller | Inputs actually used | Result used | Purpose |
|---|---|---|---|---|
| C1 | `PDEDataViewer:2672`, `PdeTimePlotMultipleVariablesPanel:191` | 1..n vars, point indices, all times | values | time plot at chosen points |
| C2 | `FieldViewerServer:310, 1335, 1397, 1469` | 1 var, points (one or many), crossing indices, `tstep` | values | field viewer `/timeseries`, probes, line samples |
| C3 | `KymographPanel:2027`, field viewer `/kymograph` (via C2) | 1 var, indices along a line, crossing indices, resampled step | values | kymograph (line × time) |
| C4 | `PDEDataViewer:1207` | 1 var, ROI `BitSet`, space stats | space stats | statistics of a drawn region over time |
| C5 | `FieldViewerServer:1989` | vars, region indices, space stats | space stats | field viewer `/stats` |
| C6 | `FrapDataUtils:129`, vmicro `ImportRawTimeSeriesFrom2DVCellConcentrationsOp:78` | 1 var, all-mesh `BitSet`, space stats | space stats | FRAP / virtual microscopy averages |
| C7 | `PDEDataViewer.plotTimeSeriesData` (from `DataSelectRetrieve`, surface viewer) | var, indices, user-chosen space and/or **time** stats | any of the three | the only caller of time statistics |
| C8 | `PDEDataViewer:3123` | 1 var, **all mesh indices**, stepped times | values | movie export: a whole-field bulk read routed through the time-series call |
| C9 | `ASCIIExporter:565` (server side, not over RPC) | vars, indices, time window | values | CSV export of time series |

## 3. Decomposition (decided 2026-11-27)

Two endpoints replace the RPC, each with one response type (style guide §1.3). Paths follow §1.4 and
D-2 and D-22: a simulation's results are in the **data area**, under
`/api/v2/data/simulations/{simulationId}/jobs/{jobIndex}` (written `{job}` below), and field data has
the same sub-resources under `/api/v2/data/field-data/{fieldDataId}`.
Access follows the reachability rule (style guide §10.4); the server resolves where the data is stored
(including `parentSimRef`).

| Purpose | Read (`GET`, cacheable) | Query (`POST`, for inputs that do not fit a URL, §1.2) |
|---|---|---|
| Time series at points (C1, C2, C3, C9) | `GET {job}/time-series?variable=…&point=…&from=…&to=…&every=…` | `POST {job}/time-series/query` with a `TimeSeriesQuery` |
| Region statistics over time (C4, C5, C6) | `GET {job}/region-statistics?variable=…&subdomain=…&from=…&to=…` | `POST {job}/region-statistics/query` with a `RegionStatisticsQuery` |

Both answer JSON for small results and the binary form (§2.4) for large ones, by `Accept`.

- **Time statistics per point (C7) are dropped from the server API.** Min, max and mean over time at a
  point are a cheap reduction of a time series; the client computes them (D-4). This removes two of
  today's four response shapes and the server path that throws "not yet implemented".
- **Whole-field reads over time (C8) are not a time series.** The movie maker reads the field per
  time (the "Field values" group, plan §4) or uses an export.
- **No "background" flag.** A request within the caps (D-10) answers directly; a larger one is a job
  (D-12). This takes the RPC's progress events off `/api/v0/events`.

## 4. Decisions

_Decided 2026-11-28._

**P-1. Points are coordinates first, mesh indices as an alternative.** A request's points are a
discriminated union (`PointSpec`, §1.3):
- `coordinate`: a tuple whose length equals the dataset's spatial dimension (`[x]`, `[x,y]` or
  `[x,y,z]`), never padded with zeros. A wrong length is a `400` with a field error. The dataset's
  metadata (`DatasetInfo`) reports `dimension`, `origin`, `extent` and the length unit. The server
  locates the point (as the FEniCSx work already does); conversion to VCell's internal 3D
  `Coordinate` happens once, in `vcell-api-mapping`, using the geometry's convention for the unused
  axes. Optional `snap` moves a point onto the nearest membrane.
- `index`: a mesh element (volume or membrane) by number, **qualified by its mesh** (P-2), for the
  desktop's pickers.
- `line` (P-3): `start`, `end` (dimension-length tuples) and a sample count.
- Membrane crossing (a volume variable sampled at a membrane) is an attribute of the point.
- The response echoes, per point, what it resolved to (mesh, element, position, as a tuple of the
  same dimension). A point that cannot be resolved (outside the domain) gets a **per-point status**,
  not a failed request (§9.5).
- In `GET` URLs a coordinate is one parameter value, `point=x`, `point=x,y` or `point=x,y,z` to
  match the dataset's dimension; points repeat (§1.2).

**P-2. Regions: by name, or by number within a named mesh.** `RegionSpec` is a discriminated union,
from most to least stable:
- `subdomain` (a subdomain or membrane **name**): survives any topology change; resolved to elements
  at each time.
- `meshRegion` (`meshId`, subdomain, `regionIndex`): one numbered connected region within one mesh.
- `elements` (`meshId`, element kind, index **ranges** `[[start,end], …]`): a drawn ROI.

Every numbered reference (regions, elements, and P-1's `index` points) names the mesh it refers to.
`DatasetInfo` lists the dataset's meshes: one for a fixed mesh, one per segment for remeshed or
moving-boundary runs, each with the time range it covers and its regions. A numbered reference used
over a time range where its mesh does not apply is a `409` (`mesh-changed`), naming the times where
the mesh changes; numbers are never mapped onto a new mesh silently. Region size is reported per time.
New kinds of region (a geometric shape, a threshold, a region tracked through topology changes) can be
added later as further variants (§6.1).

**P-3. Kymographs are time series along a line.** No separate endpoint: the `line` point variant
makes the server generate the samples and locate them at each time (including on moving meshes); the
response is the usual `values[variable][point][time]` with the sample positions.

**P-4. Statistics, the contract.** Per variable, per time: `min`, `max`, `mean` (unweighted),
`weightedMean` (weighted by element volume for volume variables, element area for membrane
variables), `sum`, `integral` (the weighted sum, approximately the total amount), `regionSize` (total
volume or area), `count` (values used) and `missingCount`; plus the mesh that applied and the unit.
- A region applies only to elements of its variable's own domain; mixing volume and membrane elements
  is a `400`.
- Missing values are skipped and counted; a non-finite input makes the statistic non-finite (style
  guide §2.3).
- These are today's definitions (`DataSetControllerImpl.calcSpatialStatsInfo`: element volume or
  membrane-element area as the weight), now written down.

**P-5. Functions.**
1. **Saved functions** (the simulation's output functions, stored with the model) are requested by
   name like variables and evaluated on the server, so the `GET` form stays cacheable.
2. **Unsaved expressions** are accepted only in the `POST` query form (`expressions`), with limits on
   length, number of arguments and cost (§8.3), and evaluated with VCell's expression engine. This keeps
   the desktop's "define a function in the viewer and plot it" working; client-side evaluation would
   need every argument over the whole region for statistics.
3. Clients may evaluate on their side where it is cheap, as the field viewer does for FEniCSx.

## 5. DTO sketches (`org.vcell.api.dto`, PROPOSED — refined in the pilot)

Records per style guide §2; `Float64` is a `Double` on the wire as in §2.3; `kind` is the
discriminator of each union.

```java
// --- dataset metadata
// GET {job}/info
record DatasetInfo(String simulationId, int jobIndex, String dataSimulationId /* holds the data: itself or its parent */,
                   String resultsVersion /* basis of the ETag */, int dimension, List<Double> origin, List<Double> extent,
                   String lengthUnit, List<Double> times, List<VariableInfo> variables, List<MeshInfo> meshes) {}
record VariableInfo(String name, ElementKind domainKind, String domain, String unit, boolean function) {}
record MeshInfo(String meshId, double fromTime, double toTime, List<RegionInfo> regions) {}
record RegionInfo(String subdomain, int regionIndex, ElementKind kind, double size) {}
enum ElementKind { VOLUME, MEMBRANE, UNKNOWN }          // UNKNOWN + @JsonCreator fallback, §2.2

// --- points (P-1, P-3)
sealed interface PointSpec permits CoordinatePoint, IndexPoint, LinePoints { String kind(); }
record CoordinatePoint(String kind /* "coordinate" */, List<Double> coordinate, Boolean snap,
                       Boolean crossMembrane) implements PointSpec {}
record IndexPoint(String kind /* "index" */, String meshId, ElementKind element, int index,
                  Integer crossingMembraneIndex) implements PointSpec {}
record LinePoints(String kind /* "line" */, List<Double> start, List<Double> end, int samples) implements PointSpec {}

// --- regions (P-2)
sealed interface RegionSpec permits SubdomainRegion, MeshRegion, ElementRanges { String kind(); }
record SubdomainRegion(String kind /* "subdomain" */, String subdomain) implements RegionSpec {}
record MeshRegion(String kind /* "meshRegion" */, String meshId, String subdomain, int regionIndex) implements RegionSpec {}
record ElementRanges(String kind /* "elements" */, String meshId, ElementKind element,
                     List<List<Integer>> ranges) implements RegionSpec {}

// --- queries
record TimeWindow(Double from, Double to, Integer every) {}
record FunctionDefinition(String name, String expression) {}       // POST only (P-5)
record TimeSeriesQuery(List<String> variables, List<PointSpec> points, TimeWindow window,
                       List<FunctionDefinition> expressions) {}
record RegionStatisticsQuery(List<String> variables, RegionSpec region, TimeWindow window,
                             List<FunctionDefinition> expressions) {}

// --- results (JSON form; the binary form carries the same data)
record TimeSeries(List<String> variables, List<Double> times, List<ResolvedPoint> points,
                  List<List<List<Double>>> values /* [variable][point][time], null where missing */) {}
record ResolvedPoint(int requestIndex, PointStatus status, String meshId, ElementKind element,
                     Integer index, List<Double> position) {}
enum PointStatus { RESOLVED, OUTSIDE_DOMAIN, NOT_IN_VARIABLE_DOMAIN, UNKNOWN }
record RegionStatistics(List<Double> times, List<VariableStatistics> variables) {}
record VariableStatistics(String variable, String unit, List<TimeStatistics> perTime) {}
record TimeStatistics(double time, String meshId, double regionSize, long count, long missingCount,
                      Double min, Double max, Double mean, Double weightedMean, Double sum, Double integral) {}
```

Problem types this family adds to the catalogue (§9.3): `dimension-mismatch` (400),
`mixed-element-kinds` (400), `mesh-changed` (409), `expression-rejected` (400), `too-large` (413,
pointing to the binary form or a job).

## 6. Depends on (plan §3)

D-2 (identifiers: decided), D-10 (caps and partial answers), D-12 (jobs), D-19 (who may read a dataset:
decided, style guide §10), D-20 (caching: a finished simulation's series never change).

## 7. Next steps

1. Fix the caps (D-10): measure points × times × variables for C1–C7 (D-2 is decided).
2. Write these DTOs in `vcell-api-dto`, and mappers in `vcell-api-mapping` from and to
   `TimeSeriesJobSpec` / `TimeSeriesJobResults`, so the server reuses `DataSetControllerImpl` at first.
3. Equivalence tests: for each caller pattern C1–C7, the new endpoints return the same numbers as the
   RPC on the same dataset (style guide §3.2).
