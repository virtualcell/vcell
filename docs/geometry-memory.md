# Geometry memory: the incident, what we measured, and what to do about it

**Status: planning document. Nothing here is committed to.**
Three prototype branches exist (#2022, #2023, #2024). They are evidence that the measurements are
real and the fixes are feasible — **not** merge candidates. Decisions belong in this document
first.

Written 2026-08-23 after the 2026-08-22 prod incident. Supersedes `docs/geometry-memory-notes.md`
from #2023, which contains an error corrected below.

---

## 1. What happened

At 06:36:57Z on 2026-08-22, two production `api` pods died within seven seconds of each other:

```
06:36:57.750  challenge response was null                      <- unauthenticated request
06:36:58.244  WARNING: image size 61920000 pixels exceeded limit of 4000000
   ... 10 more, ~370 ms apart ...
              java.lang.OutOfMemoryError: Java heap space
              Heap dump ... [1024595375 bytes]
              Terminating due to java.lang.OutOfMemoryError
api-76b48c8cdb-4l5jz  06:36:57Z  exit 3
api-76b48c8cdb-crsfw  06:37:04Z  exit 3
```

The request was `GET /api/v0/biomodel/101963252/simulation/98916046`, issued by **PetalBot**, a
search-engine crawler walking links from `/api/v0/biomodel/101963252`. The model has 15 simulation
contexts and a 61,920,000-pixel geometry. The api heap is **1000 MB**.

An identical, non-fatal repeat at 07:48 completed in 4022 ms, which tells us the request is
survivable when the pod is otherwise idle — this is a margin problem, not an impossibility.

Two things are worth stating plainly:

- **This is remotely reachable and needs no credentials.** Any crawler, or any user, who fetches a
  published model can do this. It is a robustness issue, not a nuisance warning.
- **#1899 is why we can see it at all.** Before the heap was sized to fit the container, the kernel
  OOM-killed the pod (exit 137) while the Java heap sat mostly free, so `HeapDumpOnOutOfMemoryError`
  could never fire. This is the first time that machinery produced evidence.

---

## 2. Why it costs so much: the VCML shape

`Xmlproducer.java:1416` writes the **complete `<Geometry>` element, image included, inside every
`<SimulationContext>`**, and `XmlReader.java:6024` parses each copy independently.

So a model with 11 spatial applications over one geometry carries eleven copies of the same image
on the wire and decodes eleven copies into the heap. At 62 MP that is **11 × 62 MB ≈ 680 MB of
retained pixel arrays**, on a 1000 MB heap that is also serving everything else.

That is the incident, in one sentence.

---

## 3. What we measured

Instrumentation lives on #2023: `GeometryMemoryProfiler` (per-phase peak / retained / allocated,
with a warmup pass and explicit retention holds) and `XmlGeometrySharingBenchmark`. A heap class
histogram was taken with `jcmd GC.class_histogram` during a hold window.

### 3.1 Parsing a document with N applications

11 applications over one 7872×7872 image (61,968,384 px — the incident's pixel count):

| | peak | retained | time | Geometry objects | VCImage objects |
|---|--:|--:|--:|--:|--:|
| share nothing (today) | 787.1 MB | 661.4 MB | 2689 ms | 11 | 11 |
| share decoded images | **162.1 MB** | **60.4 MB** | **287 ms** | 11 | 1 |
| share whole geometries | 162.1 MB | 60.3 MB | 273 ms | 1 | 1 |

Retained falls by exactly the application count. **Sharing images alone captures the entire win**;
sharing the mutable `Geometry` on top adds nothing measurable.

### 3.2 Building a RegionImage from scratch (a different operation — see §4.1)

`GeometryMemoryProfiler`, one image geometry:

| pixels | peak | retained | allocated | time |
|--:|--:|--:|--:|--:|
| 262,144 | 40.2 MB | 7.9 MB | 22.6 MB | 47 ms |
| 2,097,152 | 101.7 MB | 53.5 MB | 101.8 MB | 99 ms |
| 4,096,000 | 151.6 MB | 73.5 MB | 185.9 MB | 193 ms |
| 62,099,136 | **1,431 MB** | 810 MB | 1,962 MB | 1930 ms |

Region finding is volume-linear at ~4 B/px retained. Surface cost scales with interface *area*, so
its share per pixel falls as the image grows while its absolute cost dominates.

### 3.3 Where the bytes are

Heap histogram at 62 MP, retained:

| structure | instances | bytes |
|---|--:|--:|
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

Two facts fall out:

- The 497.7 MB of `int[]` in 2,121 arrays is `mapImageIndexToLinkRegion` — **one `int` per pixel,
  236.9 MB per RegionImage** — against a measured **16,706 link regions, which fit in two bytes**.
- **480,200 quadrilaterals carry ~180 MB of objects, ~375 bytes per quad**, where a quad's
  geometric content is four node indices (16 bytes).

### 3.4 How VCell compares to a mature labeller

Prompted by the suggestion to look at `cv2.connectedComponents`. Same synthetic volume, one process
per case (`ru_maxrss` is a monotonic high-water mark, so several cases in one process cannot be
measured independently — an earlier attempt that ignored this reported cv2 at 1.04 B/px, an
artefact):

| | pixels | added peak | time | B/px |
|---|--:|--:|--:|--:|
| `scipy.ndimage.label` 3D | 62,099,136 | 580.6 MB | 725 ms | 9.80 |
| `cv2.connectedComponents` 2D | 61,968,384 | 335.5 MB | 74 ms | 5.68 |
| `scipy.ndimage.label` 2D | 61,968,384 | 280.2 MB | 445 ms | 4.74 |
| VCell region finding, 3D | 62,099,136 | 452 MB peak | 472 ms | 8.32 alloc |

**VCell's connected-component labelling is competitive with scipy and not the problem.** Everyone
pays ~4–5 B/px because everyone materialises a 32-bit label array. cv2 is much faster in 2D but
uses more memory, and is 2-D only. The conclusion is that the labelling *algorithm* is not where
VCell is losing — which redirects effort to §5.3 and §5.5.

### 3.5 Pixel classes are the wrong thing to limit

Tested because a limit of ~16 pixel classes was proposed as a way to reject unsegmented images.
256³ volume, one subvolume per concentric shell, regions only:

| pixel classes | 2 | 4 | 16 | 32 | 64 | 128 |
|---|--:|--:|--:|--:|--:|--:|
| regions | 2 | 4 | 16 | 32 | 64 | **14,050** |
| peak | 124 MB | 196 MB | 244 MB | 209 MB | **300 MB** | **1,652 MB** |

**Memory is flat in pixel-class count.** A 64-subvolume geometry is unremarkable. The jump at 128 is
not the class count: at that resolution the shells fall below one voxel thick and **fragment**, and
it is the 14,050 resulting regions that cost 1.65 GB. Measured separately, a **random 2-class image
costs 2,111 MB at under 1 MP**.

So a class limit would reject the cheap 64-subvolume case and still admit the expensive fragmented
one. **The predictor is region count, not class count.** This also confirms the observation that
doing N binary passes would not blow up — VCell's labelling already does not scale with class count.

### 3.6 Prod database: duplicate image rows

Read-only queries against prod (see §7 for the account rule that governs this).

A first pass grouped on "name with trailing digits stripped" and reported 12,629 surplus rows.
**That was wrong** — it collapsed `N1E for PIP2` / `PIP3` / `PIP4`, which are deliberately distinct
images. Discarded.

Tightened: `TokenMangler.getNextRandomToken` appends `abs(random × Integer.MAX_VALUE)`, effectively
always 6+ digits, which humans do not type. Combined with same owner, same dimensions, and written
within one save:

| | groups | surplus rows |
|---|--:|--:|
| copies written **≤ 5 s apart** (one save) | 453 | **944** |
| …requiring ≥1 machine-mangled name | 445 | 936 |
| copies **> 5 s apart** (ordinary save-as, not a bug) | 2,196 | 8,965 |

Of 21,978 images, 14,083 carry a mangled name, but most of that is twenty years of legitimate
save-as collisions.

The mechanism is confirmed by the reference counts — image rows vs. geometries referencing them:

```
sus_arj_10um_nov   17 image rows   17 geometries
img_20190130_      15              15
10spines           15              15
img_20180606_      12              12
fix_simple         11              11
img_20250616_       8               8      <- October 2025
```

**Exactly one image row per referencing geometry**, which is what N inserts inside one
`saveBioModel` produces and not what save-as produces.

Cause: `saveBioModel` keys `memoryToDatabaseHash` by the image object, and `VCImage` overrides
neither `equals` nor `hashCode`. The XML round-trip inside the save produces N distinct-but-equal
objects for one image, so a **new** image (`key == null`) falls into the insert branch N times.

Not yet confirmed: that the referencing geometries all belong to a single BioModel. One more query
would settle it.

### 3.7 How compressible this data actually is

Measured after the question was raised: if the expensive things can be *held* compressed and
rehydrated on demand, the size of the finished artefact stops being the constraint it looked like.

**Real geometry images, straight out of the VCML test corpus** (the `<ImageData>` element carries
`CompressedSize`, so the deflate ratio VCell already achieves is readable without decompressing
anything). 62 distinct images:

| | min | median | max |
|---|--:|--:|--:|
| all 62 images | 23.3× | **73.6×** | 287.7× |
| the 32 images ≥ 1 MP | 53.6× | **63.5×** | 287.7× |

Aggregate: 87,874,955 px would be **83.8 MB raw, 1.1 MB compressed — 78.8×**.

Synthetic images get this property badly wrong in both directions, which is why these are real ones.

**Rehydration cost**, four real images, best of seven inflates with the cache cleared each time:

| image | pixels | ratio | inflate | throughput |
|---|--:|--:|--:|--:|
| 564×160×31 | 2,797,440 | 234.3× | 3.9 ms | 676 MB/s |
| 208×153×83 | 2,641,392 | 63.5× | 2.6 ms | 951 MB/s |
| 600×300×22 | 3,960,000 | 114.0× | 4.9 ms | 775 MB/s |
| 256×256×34 | 2,228,224 | 106.3× | 2.4 ms | 888 MB/s |

**A 62 MP image rehydrates in roughly 65–90 ms** — against the ~2.7 s the eleven-application parse
takes today.

**The derived label array compresses too**, which is the more surprising result. Same four images,
`mapImageIndexToLinkRegion` deflated:

| image | entries | max label | regions | as `int[]` | as `short[]` |
|---|--:|--:|--:|--:|--:|
| 564×160×31 | 2,797,440 | 1,523 | 2 | 93.2× | 71.0× |
| 600×300×22 | 3,960,000 | 3,557 | 3 | 86.6× | 63.6× |
| 256×256×34 | 2,228,224 | 2,305 | 3 | 76.8× | 55.0× |
| 208×153×83 | 2,641,392 | 3,830 | 6 | 53.1× | 36.8× |

Scaled to 62 MP, **the 236.9 MB `int[]` — the single largest retained object in §3.3 — becomes
2.5–4.5 MB.** Deflating it costs 13–23 ms per 2–4 M entries.

Incidentally, the corpus corroborates §2 directly: `biomodel_27192717.vcml` embeds the same image
element **nine times**, `biomodel_26455186.vcml` eight times.

### 3.8 The `getPixels()` audit

**Method.** Grep cannot answer this: `getPixels()` also exists on `ByteImage`, `UShortImage`,
`FloatImage`, `ShortImage` and `ImageDataset`, and text search cannot tell which receiver a call is
on. The compiler already resolved every receiver, so the call sites were read out of the **bytecode**
(`javap -c` over all 13 built modules, matching `invoke*` whose receiver is `VCImage`,
`VCImageUncompressed` or `VCImageCompressed`), then located in source. An earlier attempt also
mapped bytecode offsets to line numbers via `LineNumberTable` and got several lines wrong; that step
was dropped rather than trusted, and class-level attribution — which is exact — was used instead.
Test sources are excluded.

**Scale — an earlier figure in this document was wrong.** It said 178 call sites. That was a raw
grep count dominated by the unrelated image classes. The real surface:

| | |
|---|--:|
| call instructions with a `VCImage` receiver | **39** |
| source files containing them | **22** |
| distinct source expressions | ~31 |

**Classification.**

| Category | Sites | Which |
|---|--:|---|
| Read-only, transient | ~27 | the large majority — copy out, iterate, write to a file, build a display buffer |
| Already defensive | 2 | `RayCaster:1643` and `VCImageUncompressed:31` call `.clone()` |
| **Retains the array** | 3 | see below |
| **Mutates the array** | **1** | see below |

**The one mutation** is `DatabaseWindowManager:1004`:

```java
currentValue.getPixels()[j] = (byte) (newPC[i].getPixel() & 0x000000FF);
```

`currentValue` is a `VCImageUncompressed` produced locally by `createSampledImage` for display
scaling — never a stored, compressed image. So it does **not** block softening
`VCImageCompressed.uncompressed`. It does mean **`getPixels()` returning the live array is
load-bearing**: any variant that returns a defensive copy silently loses these writes.

**The three retention sites**, only one of which matters:

- `GeometrySpec:962` — `uncompressedPixels = getImage().getPixels()` stores the **stored image's**
  array in a field. This is the blocker already identified in §5.3b, now confirmed to be the only
  one of its kind.
- `GeometryViewer:257` — the array is handed to `SourceDataInfo`, which keeps it in a
  `private Serializable data` field for as long as the viewer is open.
- `GeometrySummaryPanel:395` — handed to `MemoryImageSource`, which retains it.

Both of the latter are on the *sampled* image, which has no compressed form to be softened against.

**A hazard specific to soft references, not visible as either category.** Four loops call
`getPixels()` in the loop header itself:

```java
for (int i = 0; i < dbImage.getPixels().length; i++)      // ClientRequestManager:1169
for (int j = 0; j < currentValue.getPixels().length; j++) // DatabaseWindowManager:1002
for (int i=0;i<vcImage.getPixels().length;i++)            // ImageFile:153
for (int i = 0; i < initImage.getPixels().length; i++)    // ROIMultiPaintManager:2036
```

Under the memory pressure that clears a soft reference — precisely the condition this is designed
for — such a loop can re-inflate on every iteration. Worse, and deeper than any of the call sites
above: `ImageSubVolume.isInside(x,y,z,spec)` calls `geometrySpec.getUncompressedPixels()[index]`
for **one pixel per call**, and is invoked from sampling loops (`GeometrySpec:583`). That is exactly
why the `uncompressedPixels` cache exists. Softening it without giving those loops a way to hoist
the array once would turn a pointer dereference into an inflate.

**Verdict.** The correctness risk is much smaller than the raw grep suggested: one mutation, on a
class that cannot be softened anyway. The real work is not correctness but two design problems —
removing the second strong reference at `GeometrySpec:962`, and giving the per-pixel `isInside` path
a bulk or hoisted accessor so it cannot thrash.

---

## 4. What we got wrong along the way

Recorded deliberately — each of these was believed, acted on, and then disproved by measurement.
They are the reason this document exists instead of three merged PRs.

### 4.1 "Surfaces are rebuilt on every XML read" — false

`XmlReader:2086` assigns the parsed surface description to a variable named `dummy`, which looks
like the document's stored surfaces are discarded and recomputed. They are not.
`getGeometrySurfaceDescription(sd, newgeometry)` **mutates the geometry it is passed** —
`setVolumeSampleSize`, `setFilterCutoffFrequency`, `setGeometricRegions`. Only the *return value*
is discarded. Afterwards `getGeometricRegions()` is non-null, so:

```java
// Geometry:457
if (getDimension()>0 && (bForcePrecomputeSurfaces || getGeometrySurfaceDescription().getGeometricRegions()==null))
```

is false and `updateAll()` never runs. **A saved model reuses its stored surfaces.**

Consequence: the 1,431 MB in §3.2 is the real cost of `new RegionImage(...)`, but **an XML read of a
stored model does not pay it**. The incident was 11 image decodes and 11 retained pixel arrays —
which matches both the measured 661 MB and the ~370 ms spacing of the prod warnings
(hex-decode-plus-inflate time, not the ~1.1 s a 62 MP RegionImage takes).

### 4.2 "One parse needs 1.4 GB, so the pods were doomed" — overstated

Followed from 4.1. One *read* of that model is affordable; eleven copies of its pixels are not.

### 4.3 "Enforce the image-size veto" — would have broken the model it protects

`GeometrySpec.vetoableChange` fires when **deserializing**, and the constructor converts
`PropertyVetoException` into `RuntimeException`. Any enforced ceiling there makes stored geometries
above it impossible to **open**. BioModel 101963252 is 61,920,000 px and loads today; an enforced
50 M ceiling would have broken exactly the model the fix was aimed at. This is why enforcement has
to move to the submission boundary (§5.4).

It is also, almost certainly, why the throw was commented out in 2017 (`1bf1b2a551`) rather than
fixed: at the historical limit of 4,000,000 px — only 159³, or 2000×2000 in 2D — enforcement
rejects ordinary models.

### 4.4 "Limit pixel classes" — measured to be the wrong quantity

See §3.5. Implemented, measured, abandoned.

### 4.5 "178 `getPixels()` call sites"

A raw grep count, quoted in this document before the audit was done. It counted `getPixels()` on
`ByteImage`, `UShortImage`, `FloatImage` and `ImageDataset`, which are unrelated classes. The real
figure is **39 call instructions in 22 files**. See §3.8.

### 4.6 The first duplicate-row query over-matched

See §3.6. 12,629 → ~944.

---

## 5. Options

Ordered by measured value, not by appeal. Each states what it buys, what it costs, and what is
unresolved.

### 5.1 Share the decoded `VCImage` within one document parse — **recommended first**

Cache decoded images by digest of their `<Image>` element, for the life of one `XmlReader` (one
document).

- **Buys:** the whole measured win — peak 787 → 162 MB, retained 661 → 60 MB, 8.7× faster (§3.1).
- **Risk:** low. A `VCImage` is immutable payload: compressed pixels are `final`,
  `VCPixelClass implements Immutable` with no setters, and everything editable — subvolumes, their
  names, extent, origin, surfaces — lives on the per-application `GeometrySpec`.
- **Also fixes** the duplicate-row insert (§3.6), because the identity-keyed hashtable then sees one
  object.
- **Prototype:** #2024. `MathGen_IT` 1045 tests green.

**Explicitly not** sharing the `Geometry` object. Five applications sharing one mutable `Geometry`
would see each other's subvolume renames — unexpected, and measured to buy nothing on top of image
sharing. The prototype keeps it behind an off-by-default property for read-only server use only.

### 5.2 Stop writing the geometry once per application (VCML format)

Emit geometries once at BioModel level and reference them by key.

- **Buys:** ~11× smaller VCML for these models, on top of the parse win. Removes the root cause
  rather than compensating for it.
- **Costs:** a format change. Old clients must still read new documents, and the migration story
  needs design. Bigger than anything else on this list.
- **Unresolved:** whether the reference should be a database key or a document-local id; what
  happens for a model whose applications genuinely differ.

### 5.3 Narrow the per-pixel label array

`mapImageIndexToLinkRegion` is `int[N]` (237 MB at 62 MP) against 16,706 labels.

- **Option A — width promotion.** `RegionImage:86-146` already contains a commented-out
  `CompactUnsignedIntStorage` doing byte → short → int promotion. 237 → 118 MB (or 59 MB).
  Someone reached this conclusion before; it was never wired in.
- **Option B — run-length encoding.** Segmented volumes are highly run-coherent, so RLE could be
  orders of magnitude smaller, not 2×. `isIndexInRegion(index)` is the only random-access consumer
  and a binary search over runs serves it. Riskier, much larger payoff.
- **Note:** `RegionImage:855` already throws above 65535 *distinct* regions, so 16 bits is already
  the working assumption downstream — though that cap is on post-merge regions, not the pre-merge
  labels this array holds.

**Do this before any tiling work** (§5.5): streaming the computation while still emitting a
full-size output array saves peak but not retention.

### 5.3b Hold it compressed, rehydrate on demand — **most promising unexplored direction**

Raised as: `VCImageCompressed` already keeps compressed pixels strongly and the uncompressed copy
as a `transient` cache, so take it one step further and let the uncompressed copy be reclaimable.

§3.7 says the arithmetic works, and works better than expected. Real geometry images compress
**50–100×**, rehydrate at **~700–950 MB/s**, and — the part that was not obvious — the derived
label array compresses **53–93×** as well.

**Use `SoftReference`, not `WeakReference`.** This distinction decides whether the idea works. A
weak reference is cleared at the next GC *regardless of memory pressure*, so a hot geometry would
re-inflate on essentially every collection: 90 ms of CPU repeatedly, for no benefit. A soft
reference is cleared only when the heap is actually under pressure, and the JVM ages them by
`-XX:SoftRefLRUPolicyMSPerMB`. Soft is the tool that gives "does not contribute to memory
pressure" without giving up the cache.

Three things block a naive version, all verified:

1. **`GeometrySpec.uncompressedPixels` (line 67) holds a second strong reference to the very same
   array** (`getImage().getPixels()` at line 962). Softening `VCImageCompressed.uncompressed` alone
   changes nothing while that field pins it. They have to move together.
2. **`VCImageUncompressed` has no compressed form** — `private final byte pixels[]`. The sampled
   image from `createSampledImage` is one of these, so it cannot be softened as-is; it would need
   either a compressed backing or a documented recompute path (it *is* derivable from the image
   plus subvolume handles).
3. **A compressed label map is not randomly accessible**, and `RegionInfo.isIndexInRegion(index)`
   needs random access. Two ways out: compress in fixed blocks and cache a few decompressed blocks,
   or use RLE, which supports random access by binary search over runs and is a natural fit for
   run-coherent segmented volumes. The second doubles as option §5.3.

**The `getPixels()` audit — done, and the surface is far smaller than feared.** See §3.8.

**Precedent worth knowing about:** `VCImageCompressed.nullifyUncompressedPixels()` already exists
and has exactly one caller, `GeomDbDriver:490`. Someone hit this problem before and solved it by
hand in a single place.

**Why this is worth the deep investigation:** it attacks retained memory rather than peak, it needs
no format change, and correctness is verifiable against a large corpus of stored models — the
inflated bytes either match or they do not.

### 5.4 Limit new submissions, grandfather what is stored

Enforce at `ServerDocumentManager.saveGeometry / saveBioModel / saveMathModel`, keyed on
`image.getKey() == null` — content that has never been persisted.

- **Buys:** the problem stops growing, without breaking a single existing model (§4.3).
- **Limits:** image size, and **region count** rather than pixel classes (§3.5). Region count is
  free at save time — the geometry already computed a `RegionImage` while parsing.
- **Prototype:** #2022, defaults 16,000,000 px and 2,000 regions, both properties.
- **Unresolved, and the reason this is not ready:** *both numbers are guesses calibrated against
  today's implementation, not against what the science needs.* See §6.

### 5.5 Reduce the surface representation

~375 bytes per quadrilateral for 16 bytes of content (§3.3). A struct-of-arrays layout —
`int[4*numQuads]` node indices, `float[3*numNodes]` coordinates — is ~28 B/quad, a >10× reduction,
and turns 2 M scattered objects into a handful of arrays G1 never has to trace.

- **Costs:** ripples through `SurfaceCollection`, `OrigSurface`, `Polygon`, `TaubinSmoothing`, the
  STL exporter and the VTK path. Worth costing before committing.
- **Smaller, separable first step:** the `edgeMap`
  (`HashMap<Integer, HashMap<Integer, TreeSet<MembraneElementIdentifier>>>`, `RegionImage:1744`) is
  built for every edge of every quad and used once. A sorted `long[]` of packed `(node1, node2)`
  keys replaces the whole structure.

### 5.6 Tiled / slab-wise region finding

Two variants were discussed.

**Slab-wise inside `RegionImage`.** The algorithm is already two-pass connected components — a
z-major labelling pass plus equivalence resolution — and it is already **6-connected** (forward x,
y, z only), so face-only reconciliation suffices and no halo is needed. The surface pass already
streams two z-planes of nodes (`mapImageIndexToNode[2][numY+1][numX+1]`). The structure is largely
there; what blocks it is the full-volume state: `regionPixels` `int[N]`, three `BitSet(N)`, and the
retained label map. **Sequence after §5.3.**

One concrete inefficiency to fix along the way: equivalences go into `Vector<Integer>[]` with a
linear `contains()` scan per pixel-pair (`createLink`), then a clone-and-BFS merge. That is not
union-find, and union-find over ~16,706 labels is trivially small.

**Tiling whole geometries and stitching them.** Appealing because it reuses `RegionImage` unchanged
per tile. Three specific obstacles, all verified:

1. **Taubin smoothing is global.** `RegionImage:930` smooths the entire `SurfaceCollection`.
   Smoothing per tile then stitching gives *different node positions* near seams — a correctness
   difference, not a cosmetic seam.
2. **Watertight stitching has bitten this codebase before.** #1890 / #1895 / #1896: independently
   processed pieces produced a non-watertight surface with 464 exposed interior-grid-plane walls.
   Nodes on a shared face are created twice with different global indices, and membrane adjacency
   across the seam must be recomputed.
3. **Membrane element ordering is persisted.** Stitching changes quad ordering, and membrane element
   indices map stored simulation results.

An earlier objection here — that tiling bounds the working set but the finished artefact still has
to fit in memory — is **weakened by §3.7**. If the accumulated result is held compressed (2.5–4.5 MB
for a 62 MP label map rather than 237 MB), the finished artefact is no longer the binding
constraint, and tiling becomes considerably more attractive than it first appeared. The three
obstacles above are about *correctness of the stitch*, not about size, and they still stand.

### 5.7 Lazy surface generation

Low value, given §4.1 — stored models already skip it. Only helps documents with no stored
`<SurfaceDescription>` (older VCML, some geometry-only documents).

### 5.8 Clean up the 944 duplicate image rows

Separate from the code fix, and a data change: needs an owner, a dry run, and a decision about
whether rows referenced by geometries can be repointed and collapsed.

### 5.9 Operational levers

Independent of all the above, and cheap:

- **Crawler traffic.** `/api/v0/biomodel/{id}/simulation/{id}` is being walked by PetalBot. A
  `robots.txt`, or rate limiting on the legacy api, removes the trigger without touching geometry
  code. It does not fix the underlying fragility.
- **api heap.** 1000 MB, sized in #1899. Whether that is still right is a separate question from
  whether one request should need 680 MB.

---

## 6. Open questions — decisions needed before implementation

1. **What image size should a new geometry be allowed to have?** The prototype says 16 M px, derived
   from today's memory profile against a 1000 MB heap. That is an implementation artefact, not a
   scientific limit. **Needs a survey of image sizes actually in the database.**
2. **What region count?** Same problem; 2,000 is a guess. A tissue image with hundreds of separate
   cells is ordinary science. **Needs a survey of region counts.**
3. **Is the VCML format change (§5.2) on the table at all?** Everything else is compensation for it.
4. **Sequencing.** Suggested: §5.1 → §5.4 (with real numbers) → §5.3/§5.3b → §5.5 → §5.6. §5.9 can
   happen immediately and independently. §5.3b is the one worth a real investigation rather than a
   quick implementation: the numbers are strong, and the risk is entirely in the 178-call-site
   `getPixels()` audit rather than in the arithmetic.
5. **Who owns the duplicate-row cleanup**, and should it wait for §5.1 to stop new ones first?
6. **Do the duplicate images in a group belong to one BioModel?** One query settles it; needs
   `vcell_dev` credentials.

Items 1, 2 and 6 are all answerable with read-only queries once someone runs them.

---

## 7. Working notes

**Database access.** Investigations use the **`vcell_dev`** account. Not `vcell_service` — that is
for running services only, and locking it would take down every environment — and not `vcell`, the
schema owner. Its password is **not** in any Kubernetes secret (the cluster secrets hold the
`vcell_service` password); pointing a script at `vcell_dev` while sourcing that secret is a failed
login, which is the exact lockout risk. Supply it explicitly and try once.

**Prototype branches — do not merge as-is:**

| PR | Branch | Contains |
|---|---|---|
| #2022 | `fix/geometryspec-image-veto` | Submission-time limits (§5.4), grandfathering |
| #2023 | `perf/geometry-memory-profiler` | `GeometryMemoryProfiler` + the notes file this supersedes |
| #2024 | `perf/parse-geometry-once` | Image sharing (§5.1) + `XmlGeometrySharingBenchmark` |

The instrumentation in #2023 is the piece most worth keeping regardless of which direction is
chosen: every number in this document came from it, and the corrections in §4 were only possible
because it existed.

**Reproducing the measurements:**

```bash
mvn test-compile -pl vcell-core -am
mvn -q -pl vcell-core exec:java -Dexec.classpathScope=test \
    -Dexec.mainClass=cbit.vcell.geometry.GeometryMemoryProfiler
mvn -q -pl vcell-core exec:java -Dexec.classpathScope=test \
    -Dexec.mainClass=cbit.vcell.xml.XmlGeometrySharingBenchmark
```

Add `-Dprofiler.hold=120` to keep structures reachable for `jcmd <pid> GC.class_histogram`.

**Related:** #2021 (the issue — note its framing of "eleven parses exhausted the heap" is corrected
by §4.1/§4.2), #1899 (heap sizing, why this was diagnosable), #1890/#1895/#1896 (the watertightness
precedent), virtualcell/vcell-fluxcd#51 (the suspended restart cron this was found under).
