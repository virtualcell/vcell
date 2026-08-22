# Geometry memory: where it goes, and what could be done about it

Measured 2026-08-22 with `vcell-core/src/test/java/cbit/vcell/geometry/GeometryMemoryProfiler.java`
on an Apple-silicon JDK 23, G1, `-Xmx6g`. Numbers below are from that profiler unless a heap
histogram is cited.

Background: #2021 / PR #2022. This document is about the *second* question — not "should an
oversized image be refused" but "why does it cost so much in the first place".

## What actually happened in prod

`GET /api/v0/biomodel/101963252/simulation/98916046`, issued by **PetalBot** (a search-engine
crawler) walking links from `/api/v0/biomodel/101963252`. Prod api log, 2026-08-22:

```
06:36:57.750  challenge response was null                       <- unauthenticated crawler request
06:36:58.244  WARNING: image size 61920000 pixels exceeded ...   <- geometry parse #1
   ... 11 more, ~370 ms apart ...
06:37:02.105  WARNING:::: MathMapping.refreshMathDescription()
06:36:57Z     api-76b48c8cdb-4l5jz  OutOfMemoryError, exit 3
06:37:04Z     api-76b48c8cdb-crsfw  OutOfMemoryError, exit 3
```

The same request logged **15** `getting simulation context rep for scKey=...` lines and **11**
image warnings. An identical, non-fatal repeat at 07:48 completed in 4022 ms.

**The geometry is parsed once per SimulationContext because the VCML stores it once per
SimulationContext.** `Xmlproducer.java:1416` writes the full `<Geometry>` element — image
included — inside each `<SimulationContext>`, and `XmlReader.java:6024` parses each one
independently. Eleven spatial SimulationContexts sharing one geometry means eleven full copies on
the wire and eleven independent region computations on read.

Each parse then runs surface generation unconditionally:

```
XmlReader:2090   newgeometry.precomputeAll(factory, false, false)
  -> Geometry:457    getGeometrySurfaceDescription().updateAll()
    -> GeometrySurfaceUtils:295   geometrySpec.createSampledImage(...)   // full-size copy
    -> GeometrySurfaceUtils:297   new RegionImage(...)                   // the expensive part
```

## The measurement

`GeometryMemoryProfiler` reports three different things per phase, because they answer three
different questions:

- **peak** — high-water heap needed to get through the phase (`MemoryPoolMXBean.getPeakUsage()`,
  peak reset at phase entry). Decides whether the JVM survives.
- **retained** — live heap still held afterwards, across a settled GC with every intermediate
  explicitly held. Decides how many concurrent requests fit.
- **allocated** — bytes the thread allocated (`ThreadMXBean.getThreadAllocatedBytes`). Decides
  collector pressure.

A warmup pass runs first and is discarded; without it the first measured phase absorbs class
loading and JIT and reads tens of MB high.

### 396³ = 62,099,136 pixels — the size that killed the pods

```
phase                                        peak     retained     B/px    allocated     B/px      ms
0. synthetic pixels (byte[N])             69.9 MB      60.0 MB     1.01      59.2 MB     1.00      75
1. new VCImageUncompressed                69.9 MB        808 B     0.00       1.0 KB     0.00     110
2. new Geometry(name,image) [setImage]    69.9 MB      71.8 KB     0.00     829.3 KB     0.01      14
3. createSampledImage                    130.0 MB      60.0 MB     1.01      59.2 MB     1.00     156
4. RegionImage regions only (dim 0)      452.1 MB     240.1 MB     4.05     492.9 MB     8.32     472
5. RegionImage + surfaces (dim 3)      1,431.5 MB     450.3 MB     7.60   1,349.5 MB    22.79    1103
TOTAL (peak=max, rest=sum)             1,431.5 MB     810.4 MB    13.68   1,961.6 MB    33.12    1930
```

**A single parse peaks at ~1.4–1.6 GB against a 1000 MB heap.** Not eleven parses — one. The pods
were never going to survive this request; the repetition only decided how far it got first.
(Runs vary between 1.43 and 1.60 GB depending on when G1 collects.)

### Scaling

| edge | pixels | peak | retained | allocated |
|-----:|-------:|-----:|---------:|----------:|
| 64 | 262,144 | 40.2 MB | 7.9 MB | 22.6 MB |
| 96 | 884,736 | 55.6 MB | 21.8 MB | 51.4 MB |
| 128 | 2,097,152 | 101.7 MB | 53.5 MB | 101.8 MB |
| 160 | 4,096,000 | 151.6 MB | 73.5 MB | 185.9 MB |
| 396 | 62,099,136 | 1,431.5 MB | 810.4 MB | 1,961.6 MB |

Region finding is volume-linear at ~4 B/px retained and ~8 B/px allocated. Surface cost scales
with interface *area*, so its per-pixel share falls as the image grows (25.4 → 19.1 → 16.8 → 12.7 →
7.6 B/px) even as its absolute cost dominates.

### What is on the heap

`jcmd <pid> GC.class_histogram` during a `-Dprofiler.hold` window at 396³, retained state:

| structure | instances | bytes |
|---|---:|---:|
| `int[]` | 2,121 | **497.7 MB** |
| `byte[]` | 22,869 | 130.4 MB |
| `RegionImage$MembraneEdgeNeighbor` | 1,920,800 | 61.5 MB |
| `RegionImage$MembraneElementIdentifier` | 1,919,138 | 46.1 MB |
| `Object[]` | 482,989 | 31.8 MB |
| `surface.Node` | 479,866 | 23.0 MB |
| `Node[]` | 480,201 | 17.3 MB |
| `ArrayList` | 480,458 | 11.5 MB |
| `RegionImage$SurfAndFace` | 480,200 | 11.5 MB |
| `surface.Quadrilateral` | 480,200 | 11.5 MB |

The 497.7 MB of `int[]` in only 2,121 arrays is two copies (the profiler holds two RegionImages) of
`mapImageIndexToLinkRegion` — **one `int` per pixel, 236.9 MB each**.

Census from the same run:

```
link regions=16,706 (max index 16,705 -> fits in 2 bytes), distinct regions=3
mapImageIndexToLinkRegion int[62,099,136] = 236.9 MB; at 2 byte/px it would be 118.4 MB
surfaces=3 polygons=480,200 nodes=479,866 (0.0077 polygons/pixel)
```

480,200 quadrilaterals carry ~180 MB of objects — **~375 bytes per quad**, where the geometric
content of a quad is four node indices, i.e. 16 bytes.

## Opportunities, largest first

### 1. Parse the geometry once per document, not once per SimulationContext

11× → 1× on everything above, and an ~11× cut in VCML size for multi-SimulationContext spatial
models. Nothing else on this list comes close, and it does not touch the geometry algorithms at
all. Two sub-options:

- **Read side only** (compatible, no format change): during one document parse, cache parsed
  geometries by the content of their `<Geometry>` element and hand out the same `Geometry` to
  every SimulationContext whose element matches. Needs a check on whether SimulationContexts
  mutate their geometry after parse — if they do, share the expensive parts (`VCImage`,
  `RegionImage`, `SurfaceCollection`) rather than the `Geometry` object.
- **Write side too** (format change, needs care with old clients): emit geometries once at
  BioModel level and reference them by key.

### 2. Do not generate surfaces on XML read

`XmlReader:2090` runs `precomputeAll` on every parse. Surfaces are needed by solvers, math
generation and the viewer — not by an API request that serializes a model back out. Phase 5 is
1.4 GB peak and 1.35 GB of churn; skipping it when nothing asks for surfaces removes the largest
single cost.

Note that the `<SurfaceDescription>` element is already parsed at `XmlReader:2086` into a variable
named `dummy` and **discarded** — so the document's own stored surface description is read and
thrown away, immediately before surfaces are recomputed from scratch.

Making `updateAll()` lazy (triggered by the first `getSurfaceCollection()` / `getGeometricRegions()`
that actually needs it) is the shape of the fix. The risk is a lazy trigger firing on a thread
that cannot afford it — `VCellThreadChecker.checkCpuIntensiveInvocation()` exists for exactly this
reason and would need attention.

### 3. Narrow the per-pixel label arrays — 237 MB → 118 MB, or 59 MB

`mapImageIndexToLinkRegion` is `int[N]` (`RegionImage:904`, aliased from `regionPixels`). With
16,706 link regions the top two bytes of every entry are zero.

There is already a **commented-out `CompactUnsignedIntStorage`** class at `RegionImage:86-146` that
does exactly this — `byte[]` promoted to `short[]` promoted to `int[]` as the label count grows.
Someone reached this conclusion before; it just never got wired in.

The class already commits to a 16-bit world elsewhere: `RegionImage:855` throws
`"image segmentation contains more than 65535 distinct regions"`, with the comment "must match
getShortEncodedRegionIndexImage()". That cap is on *distinct* (post-merge) regions, not on the
*link* (pre-merge) labels this array holds, so it does not license a blind `short[]` — but it does
mean 16 bits is already the working assumption downstream, and the promotion scheme handles the
rest.

The transient `regionPixels` (`RegionImage:645`) is the same array before it is aliased, so the
saving applies to peak as well as retained.

Also commented out, at `RegionImage:880-885`: a per-region `BitSet` alternative that would drop the
map to `numDistinctRegions × N bits` — 23 MB for the 3 regions here. **Do not take this one
unconditionally**: it is `numDistinctRegions × N` bits, so it wins hugely for a clean segmentation
and loses catastrophically for a noisy one. It presumably got commented out for that reason. If
used at all it should be chosen at runtime from the measured region count.

### 4. Stop materialising 480 k object graphs for the surface

`Quadrilateral` + `Node[]` + `Node` + `SurfAndFace` + `ArrayList` + two `MembraneEdgeNeighbor` /
`MembraneElementIdentifier` per edge comes to ~375 bytes per quad. A struct-of-arrays layout —
`int[4*numQuads]` node indices, `float[3*numNodes]` coordinates — is ~28 bytes per quad, a >10×
reduction, and turns 2 M scattered objects into a handful of arrays that G1 never has to trace.

This is the biggest change on the list and the one most likely to ripple: `SurfaceCollection`,
`OrigSurface`, `Polygon`, `TaubinSmoothing`, the STL exporter and the VTK path all consume these
objects. Worth costing before committing to it.

The `edgeMap` (`RegionImage:1744`,
`HashMap<Integer, HashMap<Integer, TreeSet<MembraneElementIdentifier>>>`) deserves attention
first and separately: nested boxed maps keyed by node index, built for every edge of every quad,
then used once in `calculateNeighbors()`. A sorted `long[]` of packed `(node1, node2)` edge keys
would replace the whole structure.

### 5. Streaming / slab-wise region finding

`calculateRegions_New` is already a two-pass connected-components algorithm — a single z-major
labelling pass plus an equivalence resolution — and the surface pass already streams on the node
side, keeping only two z-planes of nodes (`mapImageIndexToNode[2][numY+1][numX+1]`,
`RegionImage:1413`). The structure for slab-wise processing is largely there.

What stops it is the full-volume state: `regionPixels` `int[N]`, three `BitSet(N)` for x/y/z
surface elements (`RegionImage:646-648`), and the retained label map. A slab-wise version would
keep provisional labels for the current slab plus one boundary plane, union-find over label ids
(16,706 here — trivially small), and emit surface quads per slab.

The honest caveat: this only pays off **if the retained label map goes away too** (item 3, or
regenerating it on demand). Streaming the computation while still keeping a 237 MB output array
saves peak but not retention. Sequence it after item 3, not before.

### 6. Two smaller items

- `createSampledImage` (`GeometrySpec:407`) allocates a second full-size `byte[]` even when the
  sample size equals the image size and the subvolume handles are an identity mapping — 60 MB here
  for a copy that could be the original.
- `getShortEncodedRegionIndexImage` (`RegionImage:187`) builds a `byte[2*N]` — 124 MB at this size
  — to encode indices it already holds. Callers should stream it rather than receive it whole.

## Not a bug, but dead weight

`FloodFill2DLine` (`RegionImage:213`) contains an inverted guard —
`if (sp + 4 < MAXDEPTH_TIMES_4) throw new RuntimeException("stack overflow")` — which would throw
whenever there *is* room. It never fires because the code is unreachable: its only caller
`calculateRegions3Dfaster` is invoked solely from a commented-out line (`RegionImage:554`), inside
`calculateRegions`, which is itself commented out at `RegionImage:407`. Worth deleting so nobody
loses time on it, but it is not a live defect.

## Reproducing

```bash
mvn test-compile -pl vcell-core -am
mvn -q -pl vcell-core exec:java -Dexec.classpathScope=test \
    -Dexec.mainClass=cbit.vcell.geometry.GeometryMemoryProfiler
```

Arguments are cube edge lengths (`64 96 128 160`; pixels = edge³). To reproduce the prod-scale
figure you need a heap of at least 2 GB and must raise the #2022 ceiling, which otherwise refuses
the image — as it is meant to:

```bash
java -Xmx6g -Dvcell.geometry.imageSizeLimit=200000000 \
     -cp vcell-core/target/test-classes:vcell-core/target/classes:$(cat cp.txt) \
     cbit.vcell.geometry.GeometryMemoryProfiler 396
```

Add `-Dprofiler.hold=120` to keep every structure reachable while you run
`jcmd <pid> GC.class_histogram`.

All line numbers are against `master` at the time of writing (`d083a0ee41`, plus PR #1997). `GeometrySpec` shifts by ~+26 lines once PR #2022 lands.
