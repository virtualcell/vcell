# Taking apart `getTimeSeriesValues`: detailed plan

Status: **draft for discussion**, 2026-11-27. First catch-all RPC worked through under the style
guide's rule §1.3 ("one endpoint, one purpose, one response type",
[`api-v1-style-guide.md`](api-v1-style-guide.md)). Endpoint shapes below are **PROPOSED — needs
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

## 3. Proposed decomposition (PROPOSED — needs agreement)

Four purposes, each with one response type. Under rule §1.3 none of them takes flags that change
the response.

### 3.1 Time series at points (C1, C2, C3, C9)

`GET` (or `POST` query when the point list exceeds URL limits, rule §1.2)
`/datasets/{id}/timeseries?variable=…&point=…&from=…&to=…&every=…`

- Response: times, plus `values[variable][point][time]`. Format per D-6.
- Points: **open question P-1**, below.
- Membrane crossing (sampling a volume variable at a membrane point, interpolated across it) becomes
  a **per-point attribute** of the input, not a parallel array.
- A kymograph is this endpoint with points along a line (C3). Its line → points construction stays
  client-side, as in the field viewer, unless P-3 decides otherwise.

### 3.2 Region statistics over time (C4, C5, C6)

`GET`/`POST` `/datasets/{id}/regionstats?variable=…&region=…&from=…&to=…`

- Response per variable, per time: min, max, mean, volume- or area-weighted mean, sums; per
  variable: region size. One fixed shape.
- Region: **open question P-2**.

### 3.3 Time statistics per point (C7): **drop from the server API**

Min, max and mean over time at a point are a cheap reduction of §3.1's result. The client computes
them (D-4). The combined space-and-time case is §3.2 followed by the same reduction. This removes
the third and fourth response shapes and the server path that throws "not yet implemented".

### 3.4 Whole-field reads over time (C8): **not a time-series endpoint**

The movie maker needs the whole field at each of several times. That is the field-values read
("Field values" group, plan §4) called per time, or an export (`exports` resource). It is not a
time series at "all points".

### 3.5 Progress and long requests

- A request whose cost is within the caps (D-10) answers synchronously. No progress events.
- Larger requests become a **job** (D-12): `POST` returns a job, `GET` reports progress, the result
  is a link. This replaces the "background" flag and the `DataJobEvent` progress path, and takes
  this RPC's traffic off `/api/v0/events`.

## 4. Open questions specific to this family

**P-1. How points are named.** (a) Mesh indices, as today: exact, cheap, but they tie every client
to VCell's mesh numbering and need the mesh first. (b) Coordinates `(x,y,z)`, located on the server
(the FEniCSx precedent, #2173): mesh-independent, and works for pyvcell and the web viewer without
a mesh, but needs a locate step and a rule for points outside the domain. (c) Both, as
alternatives of one input type (a discriminated union, rule §1.3).

**P-2. How a region is named.** (a) An index list. (b) A compact mask (run-length or base64
bitmap): today's `BitSet`, re-encoded. (c) A named region: a subdomain or membrane, resolved on the
server. Can be combined with (a) or (b).

**P-3. Kymograph on the server?** Keep line → points on the client (one endpoint, §3.1), or add a
kymograph computation endpoint, as the field viewer moved FEniCSx kymographs server-side for moving
meshes.

**P-4. Weighted statistics definition.** Which weights, volume and area, are part of the contract,
and how statistics are defined for a region that spans compartments. Document what
`SpatialStatsInfo` does today.

**P-5. User functions** (D-9): send expressions with the request, store them per dataset, or
evaluate on the client. This decides whether §3.1 and §3.2 accept functions at all.

## 5. Depends on (plan §3)

D-2 (dataset identifier), D-6 (binary format for `values`), D-9 (user functions), D-10 (caps and
partial answers), D-12 (jobs), D-19 (who may read a dataset), D-20 (caching: a finished
simulation's series never change, so these `GET`s are cacheable).

## 6. Next steps

1. Decide P-1 to P-5 and the dependencies above.
2. Measure request sizes (points × times × variables) for C1–C7 to set the caps (D-10) and decide
   when a request becomes a job.
3. Write the DTOs for §3.1 and §3.2 in `vcell-api-dto`, and their mappers in `vcell-api-mapping` from
   and to `TimeSeriesJobSpec` / `TimeSeriesJobResults`, so the server can reuse
   `DataSetControllerImpl` unchanged at first.
4. Equivalence tests: for each caller pattern C1–C7, the new endpoint returns the same numbers as the
   RPC on the same dataset.
