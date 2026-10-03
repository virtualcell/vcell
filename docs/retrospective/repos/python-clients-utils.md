# Python clients & utilities (three small satellite repos)

Three small, single-purpose Python repositories in the VCell ecosystem: a generated REST API client, the original CLI helper package, and an HPC batch-submission script.

**Group:** Python clients & utilities · **PRs (non-bot):** 1 total (cli_utils #2) · **Releases:** 6 (cli_utils only) · **Active span:** 2021-01 → 2024-05 (per repo, see below) · **Key contributors:** GMarupilla, bilalshaikh42, jcschaff, CodeByDrescher

All three are direct-commit repos (effectively 0 PRs of substance), so the history below is reconstructed from `git log`/commit diffs and PyPI release tags rather than PRs. Each is now inactive; their current status is given per-repo.

---

## `vcell-api-client` — generated Python REST client

A Python client library for the modern VCell REST API (`/api/v1/…`), produced by [OpenAPI Generator](https://openapi-generator.tech) (`PythonClientCodegen`) from the Quarkus `vcell-rest` OpenAPI spec. Published to PyPI as [`vcell-api-client`](https://pypi.org/project/vcell-api-client/). The package wraps the BioModel, MathModel, Publication, Users, HelloWorld, and Admin/usage resources (API version 1.0.1), defaulting to host `https://vcellapi-test.cam.uchc.edu`.

**Status: superseded / effectively abandoned.** The entire repo is a *single-day* burst of 7 commits on **2024-05-30** by jcschaff — bootstrap from the `waynerv/cookiecutter-pypackage` template, the OpenAPI generation tooling, the generated client, and a demo `example_latest.ipynb` notebook plus an interactive-login helper. There are no commits before or after that day; the README's Features section is still the cookiecutter placeholder ("TODO"). The same generated client was subsequently regenerated and vendored directly inside the [`pyvcell`](https://github.com/virtualcell/pyvcell) package at `pyvcell/_internal/api/vcell_client/`, where it covers a strictly larger API surface (adds Geometry, Export, FieldData, Solver, Simulation, and VCImage resources). The standalone `vcell-api-client` repo only ever exposed the smaller 2024 endpoint set and was never updated, so the pyvcell-vendored copy is the live one; the standalone repo is best read as a 2024 proof-of-concept that pyvcell absorbed.

| commit | date | author | why it matters |
| --- | --- | --- | --- |
| [`dfb0b5a`](https://github.com/virtualcell/vcell-api-client/commit/dfb0b5a75) | 2024-05-30 | jcschaff | initial commit |
| [`883968c`](https://github.com/virtualcell/vcell-api-client/commit/883968cd2) | 2024-05-30 | jcschaff | cookiecutter project bootstrap |
| [`ffc0e8f`](https://github.com/virtualcell/vcell-api-client/commit/ffc0e8f67) | 2024-05-30 | jcschaff | OpenAPI generation tooling |
| [`3836186`](https://github.com/virtualcell/vcell-api-client/commit/3836186e7) | 2024-05-30 | jcschaff | generated client from current `openapi.yaml` |
| [`eb343e2`](https://github.com/virtualcell/vcell-api-client/commit/eb343e2ea) | 2024-05-30 | jcschaff | add interactive login + example Jupyter notebook |

Stack: Python 3.9+, OpenAPI Generator v7-era `urllib3` client, Makefile + Shell helpers, GitHub Actions (`python.yml`) — though the cookiecutter CI triggers were deliberately disabled at bootstrap (`d8232d3`).

---

## `vcell_cli_utils` — original CLI helper package

The first home of the Python helper utilities used by the VCell CLI for BioSimulators-style execution of COMBINE/OMEX archives and SED-ML. It handles the post-processing the Java CLI cannot do conveniently in-process: writing per-task / per-dataset **status YAML** (queued/running/succeeded/failed), converting simulation results to **CSV** and **HDF5** (including nested OMEX structures), and generating **2-D plots**. Published to PyPI as [`vcell-cli-utils`](https://pypi.org/project/vcell-cli-utils/).

**Status: absorbed into the main `vcell` monorepo.** Development ran from the initial commit on **2021-01-25** through the last standalone release [v0.7.1](https://github.com/virtualcell/vcell_cli_utils/releases/tag/v0.7.1) on **2021-06-24**. Nearly all of the work was done by **GMarupilla** (Gajendra Marupilla) — building out status tracking ([`f77a9e6`](https://github.com/virtualcell/vcell_cli_utils/commit/f77a9e645) and the long Feb-2021 status-YAML series), HDF5 conversion for nested archives ([`08ef10f`](https://github.com/virtualcell/vcell_cli_utils/commit/08ef10f6d)), Java `ProcessBuilder` error handling ([`622dd42`](https://github.com/virtualcell/vcell_cli_utils/commit/622dd4285)), CSV/plot generation, and the master-file fallback so a combine archive without a master file runs all SED documents ([`84c6035`](https://github.com/virtualcell/vcell_cli_utils/commit/84c6035bb)). **bilalshaikh42** set up the PyPI release plumbing — the CD pipeline, semantic-release config, and the package's only merged PR, [#2](https://github.com/virtualcell/vcell_cli_utils/pull/2) (a one-line `requirements.txt` fix) — and cut the final `v0.7.1`. Development then moved into the main `virtualcell/vcell` monorepo under `vcell-cli-utils/`: that copy's `pyproject.toml` declares `version = "0.7.2"` and points `repository` back to `github.com/virtualcell/vcell`, and its `CHANGELOG.md` continues directly from this repo's v0.7.1 history — confirming the standalone repo was retired in favor of the in-tree package (later development through Dec 2024 includes jcschaff removing `status.py` once the Java side took over archive logging).

| commit / PR | date | author | why it matters |
| --- | --- | --- | --- |
| [`bc972c1`](https://github.com/virtualcell/vcell_cli_utils/commit/bc972c14d) | 2021-01-25 | GMarupilla | initial commit |
| [`80c4475`](https://github.com/virtualcell/vcell_cli_utils/commit/80c44b757) | 2021-02-10 | GMarupilla | generate status YAML (core BioSimulators reporting) |
| [`08ef10f`](https://github.com/virtualcell/vcell_cli_utils/commit/08ef10f6d) | 2021-03-15 | GMarupilla | HDF5 conversion for nested OMEX structures |
| [`0cfd4cb`](https://github.com/virtualcell/vcell_cli_utils/commit/0cfd4cab6) | 2021-05-08 | GMarupilla | set up CD pipeline → PyPI |
| [`84c6035`](https://github.com/virtualcell/vcell_cli_utils/commit/84c6035bb) | 2021-05-26 | GMarupilla | no-master-file archive runs all SED documents |
| [`fba4b3f`](https://github.com/virtualcell/vcell_cli_utils/commit/fba4b3f01) | 2021-06-24 | bilalshaikh42 | rel-path length fix; release config for v0.7.1 |

Stack: pure Python (~24 KB), Fire CLI, pandas/numpy, PyYAML, h5py; `python-semantic-release` + GitHub Actions for PyPI publishing. Releases: `0.1`, `0.2`, `0.6.01`, `0.6.02`, `0.6.03`, `v0.7.1`.

---

## `PythonHPCBatchScript` — HPC OMEX batch submission

A tiny stand-alone utility (~18 KB Python) that generates a SLURM job to batch-process every OMEX archive in a directory through VCell on the UConn CCAM HPC cluster. `biosim_batch_exec.py` launches the jobs (with a `--help`-documented argument set and a validation mode); `biosim_batch_kill.py` tears down a previous batch — with the README warning that re-running `exec` overwrites the kill resources, so a prior batch must be killed first.

**Status: complete and inactive (single-author scratch tool).** Six commits, all by **CodeByDrescher** (Logan Drescher), between **2022-10-12** (initial upload + "flexible modes") and **2022-11-11** (bug fixes to file searching and validation mode, then removal of a test file). No releases, no PyPI package, no PRs — a self-contained operational script that hasn't changed since late 2022.

| commit | date | author | why it matters |
| --- | --- | --- | --- |
| [`70decf6`](https://github.com/virtualcell/PythonHPCBatchScript/commit/70decf680) | 2022-10-12 | CodeByDrescher | initial upload |
| [`b208e25`](https://github.com/virtualcell/PythonHPCBatchScript/commit/b208e2598) | 2022-10-12 | CodeByDrescher | added flexible run modes |
| [`b18ec6e`](https://github.com/virtualcell/PythonHPCBatchScript/commit/b18ec6e77) | 2022-11-11 | CodeByDrescher | fix file-searching + validation-mode bugs |

Stack: plain Python 3 + SLURM CLI; no build system or packaging — "drag the files to wherever you want to store the script."

---

## Key contributors (across the three repos)

- **GMarupilla** (Gajendra Marupilla) — built essentially all of `vcell_cli_utils`: status YAML, HDF5/CSV conversion, plotting, OMEX execution logic.
- **bilalshaikh42** (Bilal Shaikh) — `vcell_cli_utils` release engineering (CD → PyPI, semantic-release, v0.7.1).
- **jcschaff** (Jim Schaff) — one-day bootstrap of the generated `vcell-api-client`.
- **CodeByDrescher** (Logan Drescher) — sole author of `PythonHPCBatchScript`.

## Corrections to catalog

- **`vcell_cli_utils` top contributor:** the catalog lists `bilalshaikh42`, but by commit volume the primary author is **GMarupilla** (most feature work); bilalshaikh42 owns the releases and the lone PR. The "absorbed into the main vcell repo's `vcell-cli-utils/`" note is confirmed (monorepo copy continues at v0.7.2 from this repo's v0.7.1 CHANGELOG).
- **`vcell-api-client`:** confirmed the standalone client is superseded by the regenerated client vendored inside `pyvcell` (`pyvcell/_internal/api/vcell_client/`), which covers a larger API surface; the standalone repo is a single-day (2024-05-30) bootstrap that was never maintained.
