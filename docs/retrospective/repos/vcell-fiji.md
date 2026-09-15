# vcell-fiji — Fiji/ImageJ plugin for VCell simulation results

A Fiji (ImageJ) plugin, **"VCell View Simulation Results"**, that reads VCell spatial
simulation results exported in the **N5** chunked-array format from VCell's S3 storage
and opens them as ImageJ stacks for downstream analysis.

**Integrations & ImageJ · 23 PRs (non-bot) · 5 releases · 2023-07 → 2025-04 · Ezequiel-Valencia (lead), jcschaff, paulricky**

## Project background

VCell produces spatial simulation results that, historically, could only be inspected
inside the VCell desktop client. This repo adds a second analysis avenue: VCell exports
spatial results to the [N5](https://github.com/saalfeldlab/n5) format (Janelia's chunked
N-dimensional array format) on remote S3-backed storage, and this Fiji plugin reads them
remotely and renders them in ImageJ. The codebase is almost entirely **Java** (~163 KB),
packaged as a Maven module `org.vcell.vcellfiji:view-simulation-results` against the
SciJava/Fiji parent POM, and distributed not as a GitHub release artifact but via a
**Fiji update site** (`VCell-Simulations-Result-Viewer`, advertised as "VCell" in the
Fiji updater). It depends on Saalfeld lab's `n5`, `n5-imglib2`, and `n5-aws-s3`
libraries for remote chunked reads.

## Timeline

### 2023 H2 — Bootstrap and the N5/S3 pivot

The repo started as a thin "import data from the VCell beta web page" plugin: the initial
code drop ([#4](https://github.com/virtualcell/vcell-fiji/pull/4), paulricky) added a
`WebSearch`/`OnlineModelSearch`/`OnlineImageLoad` stack that browsed models on the web and
pulled images. Ezequiel-Valencia then took over and reframed the project around the N5
export ([#6](https://github.com/virtualcell/vcell-fiji/pull/6),
[#7](https://github.com/virtualcell/vcell-fiji/pull/7)). The pivotal architectural change
was [#12](https://github.com/virtualcell/vcell-fiji/pull/12) "Plugin with s3 service",
which introduced `N5ImageHandler` reading directly from the S3 endpoint over VCell's URI
protocol and the first `N5ImageHandlerTest` — establishing the N5-over-S3 read path that
remained the spine of the plugin. The original `Ricky/` web-search classes lingered as
dead code until [#45](https://github.com/virtualcell/vcell-fiji/pull/45) deleted them.
GUI work ([#17](https://github.com/virtualcell/vcell-fiji/pull/17),
[#22](https://github.com/virtualcell/vcell-fiji/pull/22)) addressed the recurring problem
of long-running remote reads freezing Swing/ImageJ by moving loads off the EDT.

### 2024 H1 — Packaging as a Fiji update site, first release

The release mechanics were solved here rather than the science. [#25](https://github.com/virtualcell/vcell-fiji/pull/25)
"Publish workflow" added `.github/workflows/publish_N5_plugin.yml`, a manually-dispatched
GitHub Action that builds the module under JDK 8, downloads a fresh Fiji, registers the
hosted update site via `ImageJ-linux64 --headless --update edit-update-site`, and uploads
the built plugin over WebDAV (`webdav:<user>:<pass>`) to `sites.imagej.net` — the standard
SciJava automatic-upload pattern (modeled on saalfeldlab/n5-ij). [#29](https://github.com/virtualcell/vcell-fiji/pull/29)
fixed that workflow to actually compile the Java and bundled an in-app `Help.html`. With
packaging working, the plugin shipped its first tag **[v0.8.0-alpha](https://github.com/virtualcell/vcell-fiji/releases/tag/v0.8.0-alpha)**
(2024-02). The remainder of the half-year was UX consolidation: a single-panel UI
([#36](https://github.com/virtualcell/vcell-fiji/pull/36),
[#39](https://github.com/virtualcell/vcell-fiji/pull/39)) and a fix for an infinite
"forever loading" spinner ([#42](https://github.com/virtualcell/vcell-fiji/pull/42)).

### 2024 H2 — "Final mile" to v1.0.0 public release

[#45](https://github.com/virtualcell/vcell-fiji/pull/45) "Final mile" was the cleanup
before going public: it deleted the legacy `Ricky/` web-search classes (~1100 lines), read
the full set of N5 export metadata, and — most substantively — added a **custom
`SimCacheLoader`** that intercepts ImageJ's block read requests so chunks are fetched
lazily/cached rather than pulled eagerly, plus a richer `N5ExportTable`. The plugin then
reached **[v1.0.0](https://github.com/virtualcell/vcell-fiji/releases/tag/v1.0.0)**
(2024-09, "Public Release"). Quick follow-ups handled real S3-deployment details: an "open
in memory" path ([#50](https://github.com/virtualcell/vcell-fiji/pull/50)) and a switch to
**bucket-domain-style S3 URLs** ([#48](https://github.com/virtualcell/vcell-fiji/pull/48),
reworking `N5ImageHandler` URL parsing). **[v1.1.0](https://github.com/virtualcell/vcell-fiji/releases/tag/v1.1.0)**
([#54](https://github.com/virtualcell/vcell-fiji/pull/54)) added a clear loading
indicator and a cancel-loading control.

### 2024 Q4 – 2025 — Data reduction / measurement scripting

The largest single feature was [#64](https://github.com/virtualcell/vcell-fiji/pull/64)
"Measurement Script" (~2100 additions): an entire `org.vcell.N5.reduction` subsystem
(`DataReductionManager`, `DataReductionWriter`, `ReductionCalculations`, and a multi-step
GUI wizard) that takes simulation results alongside lab/experimental results, computes
reduction statistics over selected ROIs and time ranges, and exports CSV — purpose-built
to compare simulation against experiment. This shipped as **[v2.0](https://github.com/virtualcell/vcell-fiji/releases/tag/v2.0)**
(2024-12). It also restructured loading into a `LoadingManager`/listener model (replacing
the older `LoadingFactory`) and added search/time-filter UI. [#68](https://github.com/virtualcell/vcell-fiji/pull/68)
then integrated ImageJ's native **ROI Manager** for ROI selection (new `AvailableROIs`
GUI) and added tabs, shipping as **[v2.0.1](https://github.com/virtualcell/vcell-fiji/releases/tag/v2.0.1)**
(2025-01). The last change, [#73](https://github.com/virtualcell/vcell-fiji/pull/73)
(2025-04), was a maintenance fix: bumping the Saalfeld N5 libraries (`n5` 3.2→3.5,
`n5-aws-s3` 4.1.1→4.3.0) and adapting the plugin's `S3KeyValueAccess` subclass to the
upstream constructor signature change (now taking a separate bucket name and base `URI`) —
i.e. "fix simulations not opening" was an N5-library API drift fix.

## Notable PRs

| PR | Date | Author | Why it matters |
|----|------|--------|----------------|
| [#4](https://github.com/virtualcell/vcell-fiji/pull/4) | 2023-07 | paulricky | Initial plugin drop — web-search/image-import design (later replaced) |
| [#12](https://github.com/virtualcell/vcell-fiji/pull/12) | 2023-11 | Ezequiel-Valencia | The pivot: `N5ImageHandler` reads N5 directly from S3; defines the core read path |
| [#25](https://github.com/virtualcell/vcell-fiji/pull/25) | 2024-01 | Ezequiel-Valencia | Fiji update-site publish workflow (WebDAV upload to sites.imagej.net) |
| [#29](https://github.com/virtualcell/vcell-fiji/pull/29) | 2024-02 | Ezequiel-Valencia | Made the publish workflow build Java; added in-app Help.html |
| [#42](https://github.com/virtualcell/vcell-fiji/pull/42) | 2024-05 | Ezequiel-Valencia | Fixed "forever loading" hang on remote reads |
| [#45](https://github.com/virtualcell/vcell-fiji/pull/45) | 2024-07 | Ezequiel-Valencia | "Final mile": custom `SimCacheLoader` for lazy chunk reads; deleted legacy web-search code |
| [#48](https://github.com/virtualcell/vcell-fiji/pull/48) | 2024-09 | Ezequiel-Valencia | Switched to bucket-domain S3 URL parsing |
| [#50](https://github.com/virtualcell/vcell-fiji/pull/50) | 2024-09 | Ezequiel-Valencia | "Open in memory" load path |
| [#54](https://github.com/virtualcell/vcell-fiji/pull/54) | 2024-10 | Ezequiel-Valencia | Loading status + cancel control (v1.1.0) |
| [#64](https://github.com/virtualcell/vcell-fiji/pull/64) | 2024-12 | Ezequiel-Valencia | Data-reduction/measurement subsystem: ROI/time stats → CSV vs lab results (v2.0) |
| [#68](https://github.com/virtualcell/vcell-fiji/pull/68) | 2025-01 | Ezequiel-Valencia | Native ImageJ ROI Manager integration + tabbed UI (v2.0.1) |
| [#73](https://github.com/virtualcell/vcell-fiji/pull/73) | 2025-04 | Ezequiel-Valencia | N5-library bump + `S3KeyValueAccess` constructor fix — "sims not opening" repair |

## Key contributors

- **Ezequiel-Valencia** — primary author (258 commits / 21 of 23 non-bot PRs); built the
  N5-over-S3 reader, the Fiji update-site publishing pipeline, the full GUI, and the
  data-reduction/measurement scripting subsystem.
- **paulricky** — initial plugin code (web-search/import prototype, later removed).
- **jcschaff** (Jim Schaff) — repo/CI bootstrap (`maven.yml`, [#2](https://github.com/virtualcell/vcell-fiji/pull/2)) and oversight.

## Tech & stack notes

- **Language/build:** Java, Maven, against the SciJava/Fiji parent POM; module
  `org.vcell.vcellfiji:view-simulation-results`. Built and run under **JDK 8** (Fiji
  target). HTML present only as in-app help (`Help.html`).
- **Key deps:** Saalfeld lab `n5`, `n5-imglib2`, `n5-aws-s3` (chunked-array reads from
  S3); ImageJ/Fiji APIs (ROI Manager, ImagePlus stacks).
- **Release/distribution:** *not* via GitHub release artifacts — a manually-triggered
  GitHub Action (`publish_N5_plugin.yml`) downloads Fiji headless, registers the hosted
  update site, and uploads the plugin over WebDAV to `sites.imagej.net/VCell-Simulations-Result-Viewer`.
  GitHub releases (v0.8.0-alpha → v2.0.1) serve as version markers/changelog only.
- **Coupling to VCell:** depends on VCell's server-side N5 export feature and S3 URI/bucket
  conventions; URL-format and N5-library churn drove several fixes (#48, #73).

---

## Sibling: `vcell-bioformats` — BioFormats image service (archived, no longer used)

**Integrations · 0 PRs · 0 releases · 2017-09 → 2024-05 (only a README touch after 2018) · ctrueden, jcschaff, Frank Morgan**

An earlier, unrelated image-integration effort: an **ImageDataset service built on the
[Bio-Formats](https://www.openmicroscopy.org/bio-formats/) library** for importing
microscopy image files into VCell. The repo's README states plainly that "this library and
the bioformats library are no longer used in VCell" and that the repo is **archived and may
be removed**.

All real work predates this retrospective's 2018-09 window: from `git log`, the repo was
first committed 2017-09 (jcschaff), with Curtis Rueden ([ctrueden](https://github.com/ctrueden),
of the OME/SciJava community) doing the Maven/SciJava POM hardening (upgrade to Bio-Formats
5.7.1, dependency cleanup, CI config) in late 2017. Frank Morgan added the functional core
in 2018 — a command-line interface that returns image metadata as XML
(`Create cmdline interface that returns xml results`, Aug 2018) plus a run of microscopy
import bug-fixes (channel interpretation, image interleave, NPEs on specific `.lsm`/`.tif`
files), ending at version 0.0.8 in Nov 2018. The only commit after that is a 2024-05 README
update marking the repo dead. Tiny codebase (~34 KB Java + a shell script); no PRs, no
GitHub releases. Effectively legacy — superseded as VCell's image-handling needs shifted,
and unrelated to the N5/Fiji plugin above.
