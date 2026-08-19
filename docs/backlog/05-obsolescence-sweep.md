# Obsolescence Sweep

Does the code an issue names still exist? Run 2026-08-19 over all 272 open issues, against the
working tree at `d083a0ee41`.

Reproduce with `python3 tools/obsolescence-sweep.py`.

**A missing reference is a signal, not a verdict.** Code gets renamed, moved, or lives in a sibling
repo. Everything asserted below was confirmed by hand after the tool flagged it; the tool's raw
output is not trustworthy on its own, and the false-positive modes are documented at the end.

---

## Headline: the sweep can only reach a fifth of the backlog

**Only 58 of 272 open issues name anything checkable at all** — a class, a method, a file path, or
a library. The rest name nothing a machine can look up, which is the same 39%-thin-body problem
that [02-needs-refinement.md](02-needs-refinement.md) documents, showing up in a different guise.

That is the honest ceiling on automated obsolescence checking here. It cannot triage the 2022
cohort, because the 2022 cohort does not say anything specific enough to check.

---

## What it settled

### `#366` — very likely obsolete → close candidate

`seaborn` is **gone** from the tree. Its only remaining appearance is a transitive extras line in
`vcell-cli-utils/poetry.lock` belonging to another package — not a VCell dependency.

The issue asks to restructure "CLI-python code for creating plots" to control the seaborn palette.
`#1472` independently records *"Python reliance removed, and plotting / logging done java side
[COMPLETE]"*. Two independent lines of evidence that the subject of this issue no longer exists.

→ **Confirm with @CodeByDrescher and close.** This was flagged "verify before ranking" in
[13-export-visualization.md](13-export-visualization.md); it is now verified.

### `#877` — NOT obsolete. My earlier guess was wrong in the other direction

I had flagged this as *"the dependency picture has changed substantially since 2023 — may be
obsolete."* The sweep says the opposite: **`jhdf` is present** (`io.jhdf 0.13.0` in the root pom),
and **`ncsa.hdf` is gone**.

So jHDF is not merely still used — it is now the *only* HDF5 reader, the native binding having
been deleted. A JRE bug affecting jHDF on Windows is therefore **more** consequential than when
the issue was filed, not less.

→ Correct the note in [13-export-visualization.md](13-export-visualization.md); re-rank upward
rather than closing.

### `#1341` and `#1470` — jlibsedml is **vendored**, not a dependency

There is no jlibsedml artifact in any pom. Instead there are **148 Java source files** under
`vcell-core/src/main/java/org/jlibsedml/`, carrying `jlibsedmlVersion = "3.0.0"`, plus vendored
upstream site docs at `vcell-core/src/main/java/site/`.

This materially changes both issues:

- `#1470` ("upgrade jlibsedml to read SED-ML L1V5") is **not a version bump**. There is no version
  to bump. Upstream changes have to be merged into 148 vendored files, or the fork has to be
  reconciled with upstream the way [`#1978`](https://github.com/virtualcell/vcell/issues/1978)
  proposes for JSBML.
- `#1341` ("jlibsedml should accept relative paths, not just URI syntax") is therefore a **local
  patch we can simply make**, not an upstream request to file and wait on. That makes it much
  cheaper than its framing suggests.

→ Record the vendoring in both issues. `#1341` is a good small win; `#1470` is larger than it looks.

### `#1635` / `#1636` — the missing target version, supplied

Both issues say only *"Self explanatory, we need to update"*, which
[02-needs-refinement.md](02-needs-refinement.md) flagged as unactionable. The facts:

| | In tree | Upstream latest | Gap |
|---|---|---|---|
| **BioNetGen** | **2.3.0** (`bionetgen/VERSION`, untouched since **2017-06-05**) | **2.9.3** (2025-04-21) | 6 minor versions, ~8 years |
| **NFSim** | not in this repo — solver binary from `vcell-solvers` | **v1.14.3** (2025-04-17) | unknown from here |

BioNetGen is **vendored Perl** (`bionetgen/BNG2.pl`), so upgrading means replacing an in-tree
distribution, not editing a manifest.

→ `#1636` can now be written properly. `#1637` should be reassigned to the `vcell-solvers` repo, or
at least state that the version lives there. Both still need the validation plan flagged in
[02](02-needs-refinement.md) — a solver version change moves simulation results.

### `#1964` — premise confirmed

`ncsa.hdf` is absent from every manifest, `io.jhdf` is present. Exactly what the issue asserts.
Useful mainly as a check that the tool detects a real removal.

---

## What it could not settle

`#1384` (dedicated submit node), `#842` and `#1674` (invalid XML character on save), `#1552`
(Singularity token) name no code, so the sweep is silent on them. They were flagged
verify-before-ranking in [17-execution-hpc.md](17-execution-hpc.md) and
[11-standards-interop.md](11-standards-interop.md) and **remain unverified** — they need a person,
not a script.

---

## False-positive modes (read before trusting a re-run)

The raw tool output flagged roughly a third more issues than survived checking. The failure modes,
all confirmed:

| Mode | Example | What happened |
|---|---|---|
| **Case sensitivity** | `#912` | Issue names `SedmlJob.java`; the file is `SedMLJob.java`. Renamed case, not deleted. |
| **Forward-looking references** | `#1921` | Names `vcell-legacy-env-names.properties` and `LegacyEnvironmentNamesTest` as things *to create*. Absence is the point, not a finding. |
| **Prose that looks like code** | `#986` | `ExportedImage`, `SpatialResultsViewer` are a user describing UI navigation, not class names. |
| **Sibling repos** | `#1578` | `vcellroot/MBSolver/Solver/src/Voronoi32.cpp` lives in `vcell-solvers`. Not checkable from here. |
| **Attachments and docs** | `#648` | `.pptx` filenames are not code. |
| **Upstream library APIs** | `#1964`, `#1859` | `DatasetCreationOptions` is jhdf's; `MediaRecorder` is a browser API. |

The tool filters most of these, but not all — `#912`'s case-rename in particular is a bug worth
fixing before any re-run (make basename matching case-insensitive).

---

## Recommended actions

| # | Action | Confidence |
|---|---|---|
| `#366` | Close as obsolete (seaborn gone; `#1472` corroborates) | High — two independent signals |
| `#877` | Re-rank **upward**; jHDF is now the sole HDF5 reader | High — verified in the pom |
| `#1341` | Note that jlibsedml is vendored; this is a local patch, not an upstream ask | High — 148 files in-tree |
| `#1470` | Re-scope: no version to bump, this is a fork-reconciliation job like `#1978` | High |
| `#1636` | Rewrite with the real numbers: 2.3.0 (2017) → 2.9.3 (2025) | High |
| `#1637` | Move to `vcell-solvers`, or state that the version lives there | Medium — NFSim's in-tree version not established from here |
| `#912` | No action; the file was renamed, not removed | High |
| `#1384`, `#842`, `#1674`, `#1552` | Still need human verification | — |
