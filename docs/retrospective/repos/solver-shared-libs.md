# Solver shared libraries — `vcell-messaging` & `vcell-expressionparser`

Two small shared C++ libraries peeled off the legacy [`vcell-solvers`](https://github.com/virtualcell/vcell-solvers) monorepo in June 2026, so that the per-solver repos can consume them as git submodules instead of carrying their own copies. Both are infrastructure dependencies bundled or consumed by the extracted solvers (fvsolver / stochastic / mbsolver / ode).

**Solvers · 0 PRs (both direct-commit) · 0 releases · 2026-06 only · sole contributor: bontempiuchc (Chris Bontempi)**

## Project background

When the solver monorepo was broken up (see [`vcell-solvers`](https://github.com/virtualcell/vcell-solvers)), most extracted solvers initially **bundled** the monorepo's shared C++ sources (`ExpressionParser`, `vcommons`, `VCellMessaging`) so each repo built standalone with no external VCell paths. In June 2026 two of those shared sources were given their own repos so they could be shared as git submodules rather than duplicated. Both repos were stood up by one author via direct commits over a few days (no PRs, no releases, stub READMEs); the content is a straight lift of the corresponding monorepo submodule, re-laid into a `src/` + `include/` layout with a standalone `CMakeLists.txt`. Their substantive code history lives in `vcell-solvers`, not here.

## Timeline

### 2026-06 — extraction into standalone submodule repos

- **`vcell-expressionparser`** (first commit 2026-06-14). Five files of housekeeping the same day it was created: an `a41090bee`/`72646dbe2` "Initial commit" lift of the monorepo's `ExpressionParser` submodule, then `fd5fb0def` moves the files into `src/`/`include/`, followed by a run of CMake fix-ups (`258ef3f1a`, `46d8f9b65`, `928e1aed0`, `4f0b3e375` install rules, `c480c2e06` library name → `vcellexpressionparser`, `eadfa8bfa` add `<cstring>` include for the test). The build (`CMakeLists.txt`) globs `src/*.cpp`/`include/*.h`, excludes `ExpressionTest.*`, and produces a single `STATIC` library `vcellexpressionparser` with `install(... ARCHIVE DESTINATION lib)`. Content is VCell's C++ math-expression engine — `Expression`, `SymbolTable`, `StackMachine`, the `AST*` node classes, and exception types — **generated from a JavaCC/JJTree grammar** (`Parser.jjt` is the grammar source; the `ExpressionParser*.h`, `Token*.h`, `*CharStream.h` files are generated output). ~163 KB of C++.

- **`vcell-messaging`** (first commit 2026-06-12). Two "Initial commit"s (`a58d7c833`, `381ff3793`) lift the monorepo's `VCellMessaging` submodule, then `40ec75f99` fixes the Linux build/test, and a same-day pair toggles Windows curl support — `a9043c7e9` "Allowed the use of curl library on Windows" then `48dbd6c46` "Removed the use of curl library on Windows" (both one-line edits to `src/SimulationMessaging.cpp`, i.e. the second reverts the first). Content is just `SimulationMessaging.{h,cpp}` (the live solver-progress reporter) plus a `GitDescribe.h`/`GitDescribe.cpp.in` configured-file pair that stamps the build's git version. `CMakeLists.txt` builds the `vcellmessaging` library, links `Threads::Threads`, and — only when `OPTION_TARGET_MESSAGING` is set — links `${CURL_LIBRARIES}` and compiles in `USE_MESSAGING=1`. So **libcurl is the optional progress-messaging transport**: with messaging off, the library still builds (matching the solvers' library-call mode, which `#undef USE_MESSAGING`). ~28 KB of C++.

### How they're consumed

Confirmed from the consuming repos' `.gitmodules`/CMake (ground truth, not READMEs): [`vcell-mbsolver`](https://github.com/virtualcell/vcell-mbsolver) registers **both** as git submodules (`.gitmodules` pins `vcell-messaging` and `vcell-expressionparser` to these repos) and links the `vcellexpressionparser` static lib from its top-level `CMakeLists.txt` and `ExpressionParserTest/`; its Python sdist bundles the submodule sources so the wheel is self-contained. Other extracted solvers still carry the equivalent sources inline rather than as these submodules (e.g. `vcell-ode`/`vcell-stochastic` have no/empty `.gitmodules`), so as of June 2026 the submodule split is only partly adopted.

## Notable commits

| Commit | Date | Author | Why it matters |
|--------|------|--------|----------------|
| `a41090bee` ([vcell-expressionparser](https://github.com/virtualcell/vcell-expressionparser)) | 2026-06-14 | bontempiuchc | Initial lift of the `ExpressionParser` submodule into a standalone repo |
| `fd5fb0def` | 2026-06-14 | bontempiuchc | Reorganized into `src/`/`include/` (the layout the standalone CMake build expects) |
| `c480c2e06` | 2026-06-14 | bontempiuchc | Settled the library name `vcellexpressionparser` |
| `a58d7c833` ([vcell-messaging](https://github.com/virtualcell/vcell-messaging)) | 2026-06-12 | bontempiuchc | Initial lift of the `VCellMessaging` submodule |
| `40ec75f99` | 2026-06-14 | bontempiuchc | Fixed the Linux standalone build/test |
| `48dbd6c46` | 2026-06-14 | bontempiuchc | Settled Windows curl behavior (reverts the preceding `a9043c7e9` one-liner) |

## Key contributors

- **bontempiuchc** (Chris Bontempi) — sole author of both repos; performed the extraction lift, the `src/`/`include/` reorg, and the CMake/build fix-ups. (Same author who owns the broader solver-extraction work in the monorepo.)

## Tech & stack notes

- **Languages:** C++ with a thin CMake build. `vcell-expressionparser` ~163 KB C++ (JavaCC/JJTree-generated parser from `Parser.jjt`); `vcell-messaging` ~28 KB C++.
- **Build:** standalone `CMakeLists.txt` each. `vcellexpressionparser` → one `STATIC` lib (`install ... lib`). `vcellmessaging` → one lib linking `Threads::Threads`, with libcurl + `USE_MESSAGING=1` gated behind `OPTION_TARGET_MESSAGING`; ships a `GitDescribe.cpp.in` for build-time version stamping.
- **Consumption:** git submodules (confirmed in `vcell-mbsolver`'s `.gitmodules`), bundled into consumers' Python sdists for self-contained wheels. No Conan packaging of these two libs themselves (Conan in the solver repos manages *external* deps like hdf5/libcurl, not these).
- **Release mechanics:** none — no GitHub releases, no PRs, no CI in either repo; they are built transitively as part of each consuming solver's build.
