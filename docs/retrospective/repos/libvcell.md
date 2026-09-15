# libvcell — VCell Java core algorithms packaged as a Python-loadable native library

A subset of VCell's Java algorithms (VCML/SBML handling, math generation, and
solver-input translation) compiled by **GraalVM native-image** into a platform
shared library (`libvcell.so` / `.dylib` / `.dll`) and called from Python via
`ctypes`. It is the load-bearing Java-core dependency under
[`pyvcell`](https://pypi.org/project/pyvcell/).

**Group:** Python ecosystem · **PRs (non-bot):** 23 · **Releases:** 20 · **Active span:** 2025-03 → 2026-06 · **Key contributors:** jcschaff (lead), CodeByDrescher

## Project background

`libvcell` is *not* a reimplementation of VCell logic — it is a thin Java
adapter (`vcell-native/`) that depends on the full `vcell` monorepo (pinned as a
git submodule `vcell_submodule`, built with Maven) and re-exposes a handful of
core algorithms as C entry points. The packaging trick: a single class
`Entrypoints.java` annotates static methods with GraalVM's `@CEntryPoint`, and a
`-P shared-dll` native-image build (`--shared`) compiles the whole Java
dependency graph ahead-of-time into a C-callable shared library. On the Python
side, `libvcell/_internal/native_utils.py` loads that library with
`ctypes.CDLL`, declares each function's `argtypes`/`restype`, and wraps calls in
a GraalVM **isolate** (`graal_create_isolate` / `graal_tear_down_isolate`) per
invocation. Each entry point takes/returns UTF-8 `CCharPointer` strings;
results come back as a JSON `{success, message}` envelope. The FFI boundary is
therefore **GraalVM native-image + ctypes** (no JPype/JNI, no running JVM —
the Java is AOT-compiled into native code). The Java side is ~90 KB; the Python
glue ~31 KB.

## Timeline (themed milestones)

### 2025-H1 — Bootstrapping the native-image → wheel pipeline (jcschaff, 0.0.1–0.0.13)

The repo began as an `fpgmaas/cookiecutter-poetry` stub (the README is still that
stub). The real work landed immediately as a burst of jcschaff PRs that stood up
the entire packaging mechanism:

- [#1](https://github.com/virtualcell/libvcell/pull/1) wired up the GitHub
  Actions that set up GraalVM 23 (`graalvm-community`, `native-image` component),
  built the `vcell` submodule with Maven, ran a `MainRecorder` jar under the
  `native-image-agent` to **record reflection/resource config**, then packaged
  the `-P shared-dll` library and copied it next to the Python package. It also
  introduced the `ctypes` calling convention and the `IsolateManager` context
  manager (`native_utils.py`). Note the submodule was repointed from `vcell` to
  `vcell_submodule` (branch `remove-nativelib`) in the same PR.
- [#2](https://github.com/virtualcell/libvcell/pull/2) moved the native-image
  build into Poetry's `build.py`/`build-backend` so that **multiplatform wheels**
  would be produced correctly, yielding [0.0.4](https://github.com/virtualcell/libvcell/releases/tag/0.0.4)
  ("Proper multiplatform wheels to PyPI").
- [#3](https://github.com/virtualcell/libvcell/pull/3) and
  [0.0.6](https://github.com/virtualcell/libvcell/releases/tag/0.0.6) hardened the
  AOT path — error handling, Python typing, and fixing **reflection errors in
  native compilation from jSBML** (a recurring native-image hazard: the recorded
  config must cover everything reflected at runtime, e.g. the Xerces SAX parser,
  later pinned via `--initialize-at-build-time=org.apache.xerces.parsers.SAXParser`).

The first real algorithms exposed were the solver-input translators.
[#4](https://github.com/virtualcell/libvcell/pull/4) added
`vcml_to_finite_volume_input` with **local field-data** support, introducing
`LocalFVSolverStandalone` / `LocalFiniteVolumeFileWriter` — standalone reimplementations
that write FV solver input files without the server-side messaging/native-executable
lookups that the stock VCell solver path performs.
[#6](https://github.com/virtualcell/libvcell/pull/6) added the **SBML↔VCML
import/export/validation** entry points (`sbmlToVcml`, `vcmlToSbml`,
`vcmlToVcml`, `sbmlToFiniteVolumeInput`) via a new `ModelUtils.java`, and
[#8](https://github.com/virtualcell/libvcell/pull/8) refined SBML export with
unit-compatibility handling and optional validation.
[#7](https://github.com/virtualcell/libvcell/pull/7) added image-based field data
fed from `pyvcell`. By [0.0.13](https://github.com/virtualcell/libvcell/releases/tag/0.0.13)
(2025-04-10) the FV/SBML/VCML surface was complete and a serialization bug in the
native layer ([#10](https://github.com/virtualcell/libvcell/pull/10)) fixed.

### 2026-H1 — Expression translation for the Python stack (CodeByDrescher, 0.0.14–0.0.15)

After a ~10-month gap, CodeByDrescher extended the *model-utility* surface so
`pyvcell` could translate VCell math expressions into Python-evaluable forms:

- [#11](https://github.com/virtualcell/libvcell/pull/11) /
  [#13](https://github.com/virtualcell/libvcell/pull/13) exposed VCell's
  `Expression.infix_Python()` as `vcell_infix_to_python_infix` (e.g. `^`→`**`,
  `csc(x)`→`1.0/math.sin(x)`), shipping in
  [0.0.14.4](https://github.com/virtualcell/libvcell/releases/tag/0.0.14.4). This PR
  also fixed a latent cloning bug by setting `XmlHelper.cloneUsingXML = true`
  consistently across all entry methods.
- [#14](https://github.com/virtualcell/libvcell/pull/14) added
  `vcellInfixToNumExprInfix` for NumExpr-compatible post-processing expressions
  ([0.0.15.1](https://github.com/virtualcell/libvcell/releases/tag/0.0.15.1)).

This era was dominated by build/release reconciliation. The 4-part version
numbers (`0.0.15.x`) are telling: PyPI had wheels (`0.0.15.4`) that `main` never
contained. [#16](https://github.com/virtualcell/libvcell/pull/16) explicitly
reconciled `main` with PyPI — adding **Python 3.10** support, restoring
**manylinux_2_28 (glibc 2.28)** wheels, and re-merging the tagged commits for
[0.0.15.3](https://github.com/virtualcell/libvcell/releases/tag/0.0.15.3) /
[0.0.15.4](https://github.com/virtualcell/libvcell/releases/tag/0.0.15.4) (merge,
not squash, to keep the tags valid).

### 2026-06 — Error reporting, Moving Boundary, and wheel-matrix cleanup (jcschaff, 0.0.16–0.0.17)

jcschaff returned for a final feature + release-hygiene sweep:

- [#17](https://github.com/virtualcell/libvcell/pull/17) added
  `generateErrorReport(String, Throwable)` — walks the exception cause chain and
  emits a structured, de-duplicated stack report instead of an opaque native
  error, surfacing real diagnostics across the ctypes boundary
  ([0.0.16](https://github.com/virtualcell/libvcell/releases/tag/0.0.16)).
- [#18](https://github.com/virtualcell/libvcell/pull/18) exposed
  `libvcell.__version__` for downstream consumers.
- [#19](https://github.com/virtualcell/libvcell/pull/19) added
  `vcml_to_moving_boundary_input`, analogous to the FV translator, emitting a
  `MovingBoundarySetup` `mb.xml` consumed by the separately-packaged
  [`pyvcell-mbsolver`](https://pypi.org/project/pyvcell-mbsolver/). It introduced
  `LocalMovingBoundarySolverStandalone` (mirroring the FV standalone writer,
  skipping the native-executable lookup) and guarded the new Python symbol with
  `hasattr` so the package still loads against older shared libraries.

A cluster of small release-engineering PRs the same day fixed a structural
PyPI-size problem. [#22](https://github.com/virtualcell/libvcell/pull/22) is the
key one: because the wheel is pure-Python + a ctypes-loaded native lib (no
CPython extension / ABI), it switched from a per-Python-version matrix to **one
`py3-none-<platform>` wheel per platform** — building a single `cp311` wheel with
cibuildwheel (keeping auditwheel/delocate/delvewheel repair for valid platform
tags) then retagging it `py3-none`. This cut ~34 wheels/~1.45 GB per release down
to ~7 wheels/~0.28 GB, ~5× faster, and pulled the project back from the PyPI
10 GB project-size ceiling. [#23](https://github.com/virtualcell/libvcell/pull/23)
dropped macOS-Intel and confined the glibc matrix; [#21](https://github.com/virtualcell/libvcell/pull/21)
stopped failed release jobs hanging 6 h on a stray `tmate` step. Final tag:
[0.0.17](https://github.com/virtualcell/libvcell/releases/tag/0.0.17).

## Notable PRs/commits

| PR | Date | Author | Why it matters |
|----|------|--------|----------------|
| [#1](https://github.com/virtualcell/libvcell/pull/1) | 2025-03-10 | jcschaff | Stood up the whole mechanism: GraalVM native-image `-P shared-dll`, native-image-agent config recording, ctypes loader, GraalVM isolates |
| [#2](https://github.com/virtualcell/libvcell/pull/2) | 2025-03-12 | jcschaff | Moved native build into Poetry `build.py` → proper multiplatform wheels (0.0.4) |
| [#4](https://github.com/virtualcell/libvcell/pull/4) | 2025-03-20 | jcschaff | First algorithm: `vcml_to_finite_volume_input` + `LocalFVSolverStandalone` (server-less FV input writer) |
| [#6](https://github.com/virtualcell/libvcell/pull/6) | 2025-04-03 | jcschaff | SBML↔VCML import/export/validation entry points (`ModelUtils.java`) |
| [#7](https://github.com/virtualcell/libvcell/pull/7) | 2025-04-04 | jcschaff | Image-based field data fed from pyvcell |
| [#11](https://github.com/virtualcell/libvcell/pull/11) | 2026-02-27 | CodeByDrescher | `vcell_infix_to_python_infix` — VCell expression → Python infix; fixed `cloneUsingXML` bug |
| [#14](https://github.com/virtualcell/libvcell/pull/14) | 2026-03-12 | CodeByDrescher | `vcellInfixToNumExprInfix` for NumExpr post-processing expressions |
| [#16](https://github.com/virtualcell/libvcell/pull/16) | 2026-05-21 | CodeByDrescher | Python 3.10 + manylinux_2_28; reconciled `main` with PyPI-only 0.0.15.x tags |
| [#17](https://github.com/virtualcell/libvcell/pull/17) | 2026-06-18 | jcschaff | `generateErrorReport` — structured cause-chain reports across the ctypes boundary (0.0.16) |
| [#19](https://github.com/virtualcell/libvcell/pull/19) | 2026-06-24 | jcschaff | `vcml_to_moving_boundary_input` → `mb.xml` for pyvcell-mbsolver; `hasattr`-guarded for old libs |
| [#22](https://github.com/virtualcell/libvcell/pull/22) | 2026-06-24 | jcschaff | One `py3-none-<platform>` wheel per platform: 34→7 wheels, 1.45 GB→0.28 GB, dodged PyPI 10 GB cap |

## Key contributors

- **jcschaff (Jim Schaff)** — 159 commits. Created the repo and the entire
  GraalVM-native-image-to-wheel pipeline; authored all the solver-input
  translators (FV, Moving Boundary), SBML/VCML import-export, error reporting,
  and the late release-engineering cleanup.
- **CodeByDrescher** — 23 commits. The 2026 expression-translation work
  (Python-infix and NumExpr entry points) and the Python 3.10 / glibc
  build-matrix reconciliation.

## Tech & stack notes

- **FFI mechanism:** GraalVM native-image (`@CEntryPoint`, `--shared`,
  graal isolates) → C shared library → Python `ctypes.CDLL`. AOT-compiled,
  no JVM at runtime, **no JPype/JNI**. String-in/JSON-out calling convention.
- **Languages:** Java 90 KB (the `vcell-native` adapter), Python 31 KB
  (ctypes glue + typed API), plus Makefile/Shell build scripts.
- **Build:** Poetry with a custom `build.py` backend that (1) Maven-builds the
  `vcell_submodule`, (2) Maven-builds `vcell-native`, (3) runs the
  `native-image-agent` (`MainRecorder`) to record reflection config, (4)
  native-image-compiles the `-P shared-dll` library. GraalVM 23
  (`graalvm-community`). Native-image reflection config is the dominant
  fragility (jSBML, Xerces SAX parser pinned via `--initialize-at-build-time`).
- **Release:** cibuildwheel with auditwheel/delocate/delvewheel repair, then a
  `py3-none-<platform>` retag; published to PyPI on GitHub release.
  20 releases over the span, `0.0.x` (with 4-part `0.0.15.x` sub-versions during
  the 2026 PyPI reconciliation). `Requires-Python >= 3.10`.
- **Dependencies / relationships:** consumes the full `vcell` monorepo via git
  submodule (the source of the algorithms); consumed by `pyvcell` (the primary
  downstream); produces input for the extracted standalone solvers
  (`pyvcell-fvsolver` via FV input, `pyvcell-mbsolver` via Moving Boundary
  `mb.xml`).
