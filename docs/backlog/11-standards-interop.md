# Group: Standards & Interop — SBML / SED-ML / OMEX / BioSimulators — 42 issues

The second-largest group, and the most internally coherent: nearly all of it is about VCell
exchanging models and results with the wider systems-biology ecosystem without losing fidelity.

**Epics:** `#1036` (public VCell models as SBML/OMEX, 28 children), `#1046` (biosimulators
nonspatial, **19/21 done**), `#1069` (biosimulators spatial, **0/2 done, both blocked**).

**Strategic decision required** — see
[04-epic-map.md](04-epic-map.md#decision-5--do-we-still-pursue-full-biosimulatorssed-ml-conformance).
The nonspatial track is two issues from finished; the spatial track is blocked on an external
standard that has not materialized since Dec 2023. They should stop being managed as one programme.

---

## Sub-themes

### Roundtrip fidelity — the core of `#1036` (8)

`#522` (math equivalency failures on 18 simcontexts), `#523` (one-of-a-kind failures on 7
simcontexts, 7 named biomodels), `#563` (CLI conversion should use saved math), `#490` (species
sharing a name with a local parameter — *"solver runs but produces incorrect results"*), `#356`,
`#642`, `#718`, `#430`.

`#522` and `#523` are the residue of a systematic campaign: *"All math equivalency failures on
roundtrip in published models have been fixed. However, extending the validation test suite to
1,038 public models identified additional failures."* That is a mature, well-instrumented effort —
these are the long tail of it, and they are correctly ranked together (Priority 2, the tightest
cluster on the board).

`#490` deserves separate attention regardless of the epic's fate: it can produce **silently
incorrect simulation results**, not just an export failure. That is a different severity class
from the rest of this group.

### SED-ML export defects (7)

`#890` (bad XPaths, 3 named models), `#891` (non-unique SIds), `#1237` (empty/incomplete SED-ML),
`#1148` (RepeatedTasks with multiple ranges), `#863` (HDF5 missing datasets for repeated tasks),
`#1581` (log axes not honored), `#1582` (species-vs-species plot points connected in the wrong
order).

`#890` and `#891` were both **caught by `SEDMLExporterTest`** — the test suite is finding these,
which means they are reproducible and have a ready-made verification harness. Both are `Queued`
with low Importance (2) despite that. Cheap wins.

`#1581` and `#1582` come from @luciansmith with screenshots and exact XML — externally reported,
precisely described, and about output correctness that other people can see.

### SED-ML/jlibsedml capability gaps (5)

`#498` (sequential / multi-sub-task RepeatedTasks — Priority 3, `Intricate (2)`), `#1469`
(many-SubTasks in RepeatedTask), `#1470` (upgrade jlibsedml to handle L1V5), `#1341` (jlibsedml
rejects plain relative paths, accepting only URI syntax), `#1571` (stochastic test suite OMEX).

These are a ladder: `#1470` (parse newer SED-ML at all) plausibly precedes `#498`/`#1469`
(support the features). Sequencing them as a chain beats ranking them independently.

**jlibsedml is vendored, not a dependency** — 148 source files under
`vcell-core/src/main/java/org/jlibsedml/` at version 3.0.0
([05-obsolescence-sweep.md](05-obsolescence-sweep.md)). That cuts both ways: `#1341` is a **local
patch we can just make**, not an upstream request to file and wait on, which makes it much cheaper
than its framing suggests; while `#1470` is **not a version bump** — there is no version to bump,
so it is a fork-reconciliation job of the kind `#1978` proposes for JSBML. Both issues should say
so.

### SBML import/export correctness (9)

`#423` (CSG geometry roundtrip NPE, with the exact test model cited), `#515` (SBMLImporter
silently consumes errors — *"should instead throw and abort with informative error message"*),
`#956` / `#1460` (SBML ids used as VCell names, hurting usability of imported models), `#354`
(disabled reactions, with a concrete proposed encoding), `#804` (MathML parse errors on 10 named
BMDB models), `#1674` (invalid XML character when re-saving a BMDB model), `#1984` (SBMLExporter
drops `SimulationContextParameter`), `#211` (SBML spatial compliance — empty body).

`#515` is worth promoting on principle: an importer that logs and continues produces silently
wrong models, and every other issue in this section is harder to diagnose because of it. Fixing
the error discipline first would make the rest of this list cheaper.

`#956` and `#1460` are the same complaint from two angles (ids vs names on import) and should be
merged.

### BioSimulations / BSTS integration (7)

`#912` (stochastic simulation fails in the biosimulators container — full repro with a docker
command and an attached OMEX), `#1573` (models stuck in endless `PROCESSING` — a 3.1k-char report
with a model list), `#972` (SED-ML for remaining biomodels, with a CSV of every failing model),
`#1074`, `#1343`, `#1344`, `#91`.

`#912`, `#1573` and `#972` are all externally reported by ecosystem partners with reproduction
data attached. These are the issues where other projects can see whether VCell works.

### Recent findings from the BMDB pipeline (3)

`#1905` (BIOMD0000000459/460/461 — CSV export fails mapping an SBML compartment size to a VCell
structure size parameter), `#1981` (four BMDB models failing at a second point, revealed once the
COPASI annotation cast was fixed), `#1984` (SBMLExporter drops `SimulationContextParameter`).

All three are 2026, all three are precise, **all three are off-board.** They came out of the
nightly BMDB gate, which means they have a standing regression harness behind them — the most
actionable subset of this entire group, and the least visible.

---

## Recommended sequencing within this group

1. **Close `#1069`** as externally blocked (with revival criteria), per Decision 5.
2. **Finish `#1046`** — 19/21 done; the remaining children are `#863` and `#1074`.
3. **`#515` first** among the SBML fixes — error discipline before error fixes.
4. **Merge `#956` + `#1460`**; merge the `#890`/`#891` test-caught pair into one work item.
5. **Board the three 2026 BMDB findings** (`#1905`, `#1981`, `#1984`) — they have harnesses.
6. **Sequence the jlibsedml ladder** (`#1470` → `#498` / `#1469`) rather than ranking flat.
7. Leave `#1036`'s 28-child roundtrip tail as a background campaign; it is progressing and does not
   need reprioritizing so much as a decision on how complete "complete" needs to be.

---

## All 42

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#522](https://github.com/virtualcell/vcell/issues/522) | roundtrip fails due to math equivalency failure | 2022-10 | Queued | 2/2 | Unknown&nbsp;(0) | — |
| [#523](https://github.com/virtualcell/vcell/issues/523) | roundtrip fails due to various errors | 2022-10 | Queued | 2/2 | Unknown&nbsp;(0) | — |
| [#912](https://github.com/virtualcell/vcell/issues/912) | VCell Biosimulators CLI: stochastic simulation not working | 2023-06 | Queued | 2/2 | Unknown&nbsp;(0) | — |
| [#498](https://github.com/virtualcell/vcell/issues/498) | VCell does not support Sequential/Multi-sub-task Repeated Tasks  | 2022-10 | Queued | 3/1 | Intricate&nbsp;(2) | REF |
| [#423](https://github.com/virtualcell/vcell/issues/423) | SBML export/import for CSG Geometry not working | 2022-09 | Queued | 4/2 | Intricate&nbsp;(2) | — |
| [#804](https://github.com/virtualcell/vcell/issues/804) | MathML parsing errors during BMDB import | 2023-02 | Queued | 4/2 | Intricate&nbsp;(2) | — |
| [#719](https://github.com/virtualcell/vcell/issues/719) | update importer and executor for VCML language format combine archives | 2022-12 | Queued | 5/2 | Complex&nbsp;(3) | — |
| [#891](https://github.com/virtualcell/vcell/issues/891) | SEDML Exporter provides non-unique IDs  | 2023-05 | Queued | 5/2 | Complex&nbsp;(3) | REF |
| [#1237](https://github.com/virtualcell/vcell/issues/1237) | VCell SEDML Export sometimes results in an "empty"/incomplete SEDML File | 2024-05 | Queued | 5/2 | Complex&nbsp;(3) | — |
| [#1344](https://github.com/virtualcell/vcell/issues/1344) | SBML-OMEX archived VCell publications should use normalized ASCII names | 2024-08 | Queued | 5/1 | Moderate&nbsp;(4) | thin |
| [#1428](https://github.com/virtualcell/vcell/issues/1428) | CLI Strict Mode toggle needs to be created | 2025-01 | Queued | 5/2 | Complex&nbsp;(3) | — |
| [#1573](https://github.com/virtualcell/vcell/issues/1573) | Endless 'PROCESSING' of VCell results on Biosimulations | 2025-08 | Queued | 5/2 | Complex&nbsp;(3) | — |
| [#1581](https://github.com/virtualcell/vcell/issues/1581) | SED-ML: plot log axes | 2025-08 | Queued | 5/2 | Complex&nbsp;(3) | — |
| [#1582](https://github.com/virtualcell/vcell/issues/1582) | Species vs. species plot not connected correctly | 2025-08 | Queued | 5/2 | Complex&nbsp;(3) | — |
| [#1673](https://github.com/virtualcell/vcell/issues/1673) | Solver Handler needs clearer and/or better handling of vcml-backed SedML vs. sbml-back… | 2026-04 | Queued | 5/2 | Complex&nbsp;(3) | — |
| [#863](https://github.com/virtualcell/vcell/issues/863) | Sedml HDF5 is missing some data sets with repeated tasks. | 2023-04 | Queued | 6/2 | Moderate&nbsp;(4) | — |
| [#1148](https://github.com/virtualcell/vcell/issues/1148) | Bug: RepeatedTasks with multiple ranges exported funny | 2024-02 | Queued | 6/2 | Moderate&nbsp;(4) | — |
| [#890](https://github.com/virtualcell/vcell/issues/890) | SedML export gives bad XPATH | 2023-05 | Queued | 7/2 | Simple&nbsp;(5) | — |
| [#1343](https://github.com/virtualcell/vcell/issues/1343) | Biomodels of the same publication should be packaged into a single sbml-omex | 2024-08 | Queued | 7/2 | Simple&nbsp;(5) | thin |
| [#91](https://github.com/virtualcell/vcell/issues/91) | Review simulator specs for model changes and observables | 2021-09 | Pool | — | Complex&nbsp;(3) | — |
| [#140](https://github.com/virtualcell/vcell/issues/140) | Open zip files as omex files | 2022-04 | Pool | — | — | — |
| [#211](https://github.com/virtualcell/vcell/issues/211) | VCell compliance with SBML spatial | 2022-07 | Pool | — | — | thin |
| [#218](https://github.com/virtualcell/vcell/issues/218) | Export rule-based applications in OMEX format via BNGL | 2022-07 | Shelved | — | — | REF thin |
| [#354](https://github.com/virtualcell/vcell/issues/354) | Round trip Disabled reactions to SBML (especially in SEDML context). | 2022-09 | Pool | — | — | — |
| [#515](https://github.com/virtualcell/vcell/issues/515) | SBMLImporter consumes errors | 2022-10 | Pool | — | — | HP |
| [#562](https://github.com/virtualcell/vcell/issues/562) | Importer brings in unused models | 2022-11 | Pool | — | — | REF |
| [#956](https://github.com/virtualcell/vcell/issues/956) | SBML id's are used as VCell names | 2023-08 | Pool | — | Unknown&nbsp;(0) | — |
| [#972](https://github.com/virtualcell/vcell/issues/972) | Support the SED-ML for remaining biomodels | 2023-09 | Pool | — | Moderate&nbsp;(4) | — |
| [#1036](https://github.com/virtualcell/vcell/issues/1036) | Epic: public vcell models as SBML/Omex | 2023-11 | Queued | — | — | EPIC |
| [#1046](https://github.com/virtualcell/vcell/issues/1046) | Epic: fix remaining issues in biosimulators_vcell for nonspatial simulations | 2023-11 | Queued | — | — | EPIC |
| [#1048](https://github.com/virtualcell/vcell/issues/1048) | VCell needs to output spatial simulation data in a way that Biosimulations/Biosimulato… | 2023-11 | Blocked | — | Moderate&nbsp;(4) | thin |
| [#1068](https://github.com/virtualcell/vcell/issues/1068) | Spatial HDF5 needs to conform to Biosimulators Standard | 2023-12 | Blocked | — | Complex&nbsp;(3) | BLK |
| [#1069](https://github.com/virtualcell/vcell/issues/1069) | Epic: fix remaining issues with biosimulators_vcell for spatial simulations | 2023-12 | Queued | — | — | EPIC |
| [#1074](https://github.com/virtualcell/vcell/issues/1074) | Upload All Nonspatial VCell Projects to BioSimulations | 2023-12 | Pool | — | Moderate&nbsp;(4) | thin |
| [#1341](https://github.com/virtualcell/vcell/issues/1341) | Jlibsedml needs to accept common file path syntax, not just URI syntax | 2024-08 | **off-board** | — | — | — |
| [#1469](https://github.com/virtualcell/vcell/issues/1469) | Update VCell to handle many-SubTasks in SedML's RepeatedTask | 2025-04 | Pool | — | Intricate&nbsp;(2) | — |
| [#1470](https://github.com/virtualcell/vcell/issues/1470) | Jlibsedml and SedmlImporter should be upgraded to be able to read and appropriately ha… | 2025-04 | Pool | — | Complex&nbsp;(3) | — |
| [#1571](https://github.com/virtualcell/vcell/issues/1571) | Handle stochastic test suite OMEX files | 2025-08 | Pool | — | — | — |
| [#1674](https://github.com/virtualcell/vcell/issues/1674) | Errors when saving updated models from BioModels DB | 2026-05 | Pool | — | — | — |
| [#1905](https://github.com/virtualcell/vcell/issues/1905) | BIOMD0000000459/460/461: results CSV export fails to map an SBML compartment size to a… | 2026-08 | **off-board** | — | — | — |
| [#1981](https://github.com/virtualcell/vcell/issues/1981) | Four BMDB models fail at a second point, revealed once the COPASI annotation cast was … | 2026-08 | **off-board** | — | — | — |
| [#1984](https://github.com/virtualcell/vcell/issues/1984) | SBMLExporter drops SimulationContextParameter; exporting it needs a rule for names tha… | 2026-08 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
