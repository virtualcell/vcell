# pyvcell

**The modern, scriptable Python front door to VCell** — load/edit/save VCML, SBML and Antimony models, run local spatial simulations (finite-volume and moving-boundary), and analyze/visualize results as NumPy/Zarr/VTK with Matplotlib and PyVista, plus access to VCell's remote REST APIs.

*Python ecosystem · 49 PRs (non-bot) · 35 releases · active 2024-08 → 2026-06 · jcschaff, AlexPatrie, CodeByDrescher (Logan Drescher)*

## Project background

`pyvcell` (PyPI: [`pyvcell`](https://pypi.org/project/pyvcell/)) is a pure-Python package that wraps two kinds of VCell technology behind a Pythonic API. For **local** work it leans on two native dependencies extracted from the VCell monorepo: `libvcell` (the Java core — VCML/SBML translation, math generation, geometry/region computation — shipped as a wheel) and the extracted solver wheels (`pyvcell-fvsolver` for finite-volume reaction/diffusion/advection PDEs, `pyvcell-mbsolver` for moving-boundary problems). For **remote** work it carries a generated OpenAPI client for the VCell REST service. The public package is organized into `pyvcell.vcml` (model authoring, reader/writer, local simulation, remote session), `pyvcell.sbml` (SBML/Antimony spatial models), and `pyvcell.sim_results` (Zarr/VTK result objects, Matplotlib/PyVista plotters); generated REST client and simdata internals live under `pyvcell._internal`. It is Java-free at runtime — the heavy lifting comes from binary wheels.

## Timeline

### Origins (Aug 2024) — a wrapper around extracted simdata + solver

The repo's first real commits (2024-08-13/15) seeded it from the monorepo: `ef6429f6` imported "vcell simdata library from github.com/virtualcell/vcell at /pythonData", and `4f87a896` wrapped `pyvcell-fvsolver` in `pyvcell.solvers.fvsolver` with safe typing. Release [0.0.1](https://github.com/virtualcell/pyvcell/releases/tag/0.0.1) describes exactly this scope: "generates zarr stores and VTK files from FiniteVolume data" and "wraps finite volume PDE solver from pyvcell-fvsolver (currently requires proprietary input files)." At this stage pyvcell could *run* a solver and *read* its output, but could not yet *author* a model — input files had to come from elsewhere.

### Early 2025 — from data-reader to full modeling toolkit

The package became a real modeling tool in a burst of work Jan–Feb 2025. [#1](https://github.com/virtualcell/pyvcell/pull/1) added the generated VCell API client and a notebook (release [0.0.2](https://github.com/virtualcell/pyvcell/releases/tag/0.0.2)). [#3](https://github.com/virtualcell/pyvcell/pull/3) and [#9](https://github.com/virtualcell/pyvcell/pull/9) (AlexPatrie) added high-level wrappers and fixed early usability gaps (e.g. `plot_concentrations` returning nothing, Path/str model constructors) ahead of a Colab demo. [#4](https://github.com/virtualcell/pyvcell/pull/4)/[#6](https://github.com/virtualcell/pyvcell/pull/6) added programmatic model editing and discovery "beyond parameter values," and [#10](https://github.com/virtualcell/pyvcell/pull/10) added simple VCML authoring.

[#5](https://github.com/virtualcell/pyvcell/pull/5) (642 files) was pivotal: it built high-level wrappers over the low-level VTK Python library to give VTK-driven visualization and analysis, shipping as [0.1.0](https://github.com/virtualcell/pyvcell/releases/tag/0.1.0). [0.1.1](https://github.com/virtualcell/pyvcell/releases/tag/0.1.1) dropped `ipython` as an explicit dependency to unbreak Google Colab — the first of many Colab-compatibility fixes that recur through the history, since Colab/workshops were the primary distribution channel.

[#11](https://github.com/virtualcell/pyvcell/pull/11) (857 files, +2.5k/−94.6k) was the structural turning point: it reorganized the package "to emphasis the distinction between public API and internal implementation packages," establishing the `pyvcell._internal` boundary that still holds. Around this time the SBML/Antimony round-trip began routing through `libvcell` (`sbml_to_vcml`, `vcml_to_sbml`), tying pyvcell's translation layer to the Java core rather than reimplementing it in Python.

### Spring 2025 — geometry, field data, and the "sysbio course" push

[#12](https://github.com/virtualcell/pyvcell/pull/12) added simulating a VCML model with image-based geometry; [#14](https://github.com/virtualcell/pyvcell/pull/14)/[#16](https://github.com/virtualcell/pyvcell/pull/16)/[#17](https://github.com/virtualcell/pyvcell/pull/17) added field-data support (spatially-varying inputs), and [#13](https://github.com/virtualcell/pyvcell/pull/13) (AlexPatrie) added trame widgets for interactive in-notebook 3D VTK visualization (released as [0.1.7](https://github.com/virtualcell/pyvcell/releases/tag/0.1.7), "interactive 3D visualization with trame/vtk"). A dense run of releases 0.1.11–0.1.18 in April 2025 ([#18](https://github.com/virtualcell/pyvcell/pull/18) "prepare pyvcell for broader use" through [#28](https://github.com/virtualcell/pyvcell/pull/28)) were driven by a systems-biology course: convenience parameter setters ([#25](https://github.com/virtualcell/pyvcell/pull/25)), loading models from URLs ([#26](https://github.com/virtualcell/pyvcell/pull/26)), and geometry import. [#22](https://github.com/virtualcell/pyvcell/pull/22) bumped `pyvcell-fvsolver` 0.1.0→0.1.1 and started regenerating the math (via libvcell) before each solve — confirming the local-simulation pipeline: **libvcell generates solver input from VCML, then the fvsolver wheel runs it**.

### Late 2025 → workshop 2026 — remote simulation and a session API

After a quiet summer/fall (only [#32](https://github.com/virtualcell/pyvcell/pull/32), parsing VCML application parameters, → [0.1.19](https://github.com/virtualcell/pyvcell/releases/tag/0.1.19) in Oct 2025), activity resumed for a March 2026 workshop. [#33](https://github.com/virtualcell/pyvcell/pull/33) (CodeByDrescher) extended VCell-expression support to work within Zarr's two constraints: expressions must use NumExpr notation, and booleans must be encoded as floats (Zarr can't JSON-serialize bools). [#35](https://github.com/virtualcell/pyvcell/pull/35) (335 files) was the big workshop-prep PR: a documentation overhaul (hand-written guides + companion notebooks replacing auto-generated docs), a **regenerated** OpenAPI client (fixing base-URL doubling, Accept headers, XML deserialization, plus a post-generation patch script), and the first `vcml_remote` module — `run_remote`/`save_and_start`/`export_n5` to authenticate, save a model, run a remote sim, poll, and read results via TensorStore. Released as workshop tags [0.1.21](https://github.com/virtualcell/pyvcell/releases/tag/0.1.21) ("workshop-03.2026").

[#37](https://github.com/virtualcell/pyvcell/pull/37) then refactored the remote surface into a cleaner **session model**: `vc.connect()` as a single entry point (anonymous by default, `login=True` for authenticated), returning a `VCellSession` with `run_sim`/`start_sim`/`save_biomodel`/`load_biomodel`/`list_biomodels`, and a `SimulationJob` handle (`.status`/`.wait()`/`.export()`/`.result()`) for non-blocking control. It also built tiered notebook-execution tests (`--run-interactive` for trame, `--run-remote` for authenticated paths). This shipped in [0.2.0](https://github.com/virtualcell/pyvcell/releases/tag/0.2.0).

### Mid 2026 — build-system modernization, packaging hygiene, moving-boundary solver

[#38](https://github.com/virtualcell/pyvcell/pull/38) (CodeByDrescher) switched the build from Poetry to **UV** (uv_build backend, `uv.lock`). [#39](https://github.com/virtualcell/pyvcell/pull/39)/[#47](https://github.com/virtualcell/pyvcell/pull/47) hardened the Zarr writer against volume-variable shape/type surprises (scalar results, already-sized arrays, optional `domain::` prefixes) and bumped `libvcell` so the wheel actually produces the new shapes — [#47](https://github.com/virtualcell/pyvcell/pull/47) also fixed a malformed `uv.lock` libvcell entry and aligned the CI Python matrix to 3.12–3.14 against `requires-python = ">=3.12"`.

A June 2026 cluster restructured the package for **lazy, optional dependencies**: [#42](https://github.com/virtualcell/pyvcell/pull/42)/[#46](https://github.com/virtualcell/pyvcell/pull/46) made `pyvcell.vcml` imports lazy (PEP 562) and split heavy runtime deps into optional-dependency extras (`solver`, `viz`, `remote`, `io`, `convert`, `native`, `mb`, and `all`), so the core install is just lxml/numpy/pydantic and users opt into the solver, visualization, or remote stacks. [#44](https://github.com/virtualcell/pyvcell/pull/44) added a lenient VCML reader (best-effort parse, preserving MathDescription + Geometry) and [#41](https://github.com/virtualcell/pyvcell/pull/41) added the MathDescription data model to the reader/writer. [#43](https://github.com/virtualcell/pyvcell/pull/43)/[#45](https://github.com/virtualcell/pyvcell/pull/45) forced PyVista off-screen / headless plotting so notebook-execution tests and CI don't block on GUI windows. [#50](https://github.com/virtualcell/pyvcell/pull/50) added a `py.typed` marker.

Finally, [#48](https://github.com/virtualcell/pyvcell/pull/48) added advection-velocity to species mapping and a "user defined" parameter role, and [#53](https://github.com/virtualcell/pyvcell/pull/53) added **moving-boundary solver** support ([0.4.0](https://github.com/virtualcell/pyvcell/releases/tag/0.4.0)). The moving-boundary pipeline is the most intricate solver integration in the repo: author via `Application.add_moving_boundary_sim`/`set_moving_boundary_front`, then `vc.simulate_moving_boundary` does a two-pass libvcell round-trip (round-trip 1 lets VCell compute geometry regions and SpatialObjects; round-trip 2 produces the moving-boundary math with a `MembraneSubDomain` velocity), emits a `MovingBoundarySetup` XML, and runs `pyvcell-mbsolver` — collecting output through the solver's **observer callbacks** (the binding writes no HDF5) into a `MovingBoundaryResult` carrying, per output time, the moving-front polygon and per-element `(x, y, grid_i, grid_j, concentration)` on the moving mesh.

## Notable PRs/commits

| PR / commit | Date | Author | Why it matters |
|---|---|---|---|
| [`ef6429f6`](https://github.com/virtualcell/pyvcell/commit/ef6429f6) + [0.0.1](https://github.com/virtualcell/pyvcell/releases/tag/0.0.1) | 2024-08 | jcschaff | Seeded repo from monorepo `pythonData` + wrapped `pyvcell-fvsolver`; generates Zarr/VTK from FV data |
| [#1](https://github.com/virtualcell/pyvcell/pull/1) | 2025-01 | jcschaff | Added the generated VCell REST API client (remote access foundation) |
| [#5](https://github.com/virtualcell/pyvcell/pull/5) | 2025-02 | jcschaff | VTK visualization + analysis wrappers (→ [0.1.0](https://github.com/virtualcell/pyvcell/releases/tag/0.1.0)) |
| [#11](https://github.com/virtualcell/pyvcell/pull/11) | 2025-03 | jcschaff | Package reorg establishing the `_internal` public/private boundary |
| [#12](https://github.com/virtualcell/pyvcell/pull/12) / [#17](https://github.com/virtualcell/pyvcell/pull/17) | 2025-03 | jcschaff | Image-based geometry simulation + field-data support |
| [#13](https://github.com/virtualcell/pyvcell/pull/13) | 2025-03 | AlexPatrie | trame widgets → interactive in-notebook 3D VTK |
| [#22](https://github.com/virtualcell/pyvcell/pull/22) | 2025-04 | jcschaff | fvsolver bump + regenerate math (via libvcell) before solving — confirms local-sim pipeline |
| [#33](https://github.com/virtualcell/pyvcell/pull/33) | 2026-03 | CodeByDrescher | VCell expressions in NumExpr notation + bool-as-float for Zarr |
| [#35](https://github.com/virtualcell/pyvcell/pull/35) | 2026-03 | jcschaff | Workshop prep: docs overhaul, regenerated OpenAPI client, first `vcml_remote` module |
| [#37](https://github.com/virtualcell/pyvcell/pull/37) | 2026-03 | jcschaff | Session-based remote API (`vc.connect()`, `VCellSession`, `SimulationJob`) → [0.2.0](https://github.com/virtualcell/pyvcell/releases/tag/0.2.0) |
| [#38](https://github.com/virtualcell/pyvcell/pull/38) | 2026-05 | CodeByDrescher | Migrated build from Poetry to UV |
| [#46](https://github.com/virtualcell/pyvcell/pull/46) | 2026-06 | jcschaff | Lazy heavy deps + optional-dependency extras (`solver`/`viz`/`remote`/`io`/`convert`/`native`/`mb`) |
| [#47](https://github.com/virtualcell/pyvcell/pull/47) | 2026-06 | jcschaff | Volume-variable shape handling in Zarr writer + libvcell lock fix |
| [#53](https://github.com/virtualcell/pyvcell/pull/53) | 2026-06 | jcschaff | Moving-boundary solver (`pyvcell-mbsolver`) via two-pass libvcell round-trip + observer-callback results → [0.4.0](https://github.com/virtualcell/pyvcell/releases/tag/0.4.0) |

## Key contributors

- **jcschaff** (Jim Schaff, 280 commits) — lead author of nearly everything: original wrapper, VCML authoring/reader/writer, local-sim pipeline, geometry/field data, remote session API, packaging modernization, moving-boundary solver.
- **AlexPatrie** (52 commits) — early high-level wrappers and Colab-demo readiness; VTK/trame interactive visualization widgets.
- **CodeByDrescher / LDre398** (Logan Drescher, 23 commits) — VCell-expression/NumExpr support, the Poetry→UV build migration, volume-variable shape fixes, and output-function support.

## Tech & stack notes

- **Language:** Python (≈1.9 MB; small Shell/Makefile glue). `requires-python >= 3.12`; CI matrix 3.12–3.14.
- **Native dependencies (the load-bearing relationships):** `libvcell` (Java core as a wheel — VCML/SBML translation, math generation, geometry/region computation, `*_to_finite_volume_input`, `*_to_moving_boundary_input`), `pyvcell-fvsolver` (extracted finite-volume PDE solver, related to the monorepo's vcell-fvsolver/libvcell C++ work), `pyvcell-mbsolver` (extracted moving-boundary solver). pyvcell itself ships **no Java/C++**; it orchestrates these wheels.
- **Local-sim pipeline:** VCML → `libvcell.vcml_to_finite_volume_input` → `pyvcell._internal.solvers.fvsolver.solve` (the fvsolver wheel) → solver output dir → `Result` (Zarr + VTK + Matplotlib/PyVista plotter). Moving-boundary uses a two-pass libvcell round-trip + `pyvcell-mbsolver` with observer-callback output.
- **Data/viz stack:** Zarr (v2; a v3 upgrade is flagged as future work), TensorStore, h5py, VTK 9, PyVista, trame (interactive notebook 3D), Matplotlib, NumPy.
- **Model I/O:** VCML (native), SBML (python-libsbml), Antimony, all round-tripping through libvcell.
- **Packaging:** moved Poetry → **UV** (`uv_build` backend, `uv.lock`) in [#38](https://github.com/virtualcell/pyvcell/pull/38). Heavy deps are **optional extras** — core install is just lxml/numexpr/numpy/pydantic; users opt into `pyvcell[solver,viz,remote,io,convert,native,mb]` or `pyvcell[all]`.
- **Release mechanics:** semantic-release-style tagging via GitHub; **35 releases in ~22 months** (0.0.1 in 2024-08 → 0.4.0 in 2026-06). The cadence is bursty and event-driven: clusters of patch releases precede Colab demos and the systems-biology course (Apr 2025) and the March 2026 workshop, reflecting pyvcell's role as a teaching/scripting front end where each demo needs a publishable PyPI build.
- **Quality gates:** ruff + ruff-format, mypy (strict, ~310 files), deptry (dependency hygiene), `uv lock --check`, pytest + pytest-cov (codecov), pre-commit, tox; plus tiered **notebook-execution tests** (default / `--run-interactive` / `--run-remote`) that lint and run the example notebooks. `py.typed` ships a typed package.

*Catalog note:* the brief lists "2025-01→2026-06"; the actual repo origin is **2024-08** (release 0.0.1, seeded from the monorepo `pythonData` library) — the Jan-2025 date is when PR-based development and the API client began. Release count is **35** and non-bot PR count **49**, matching the brief.
