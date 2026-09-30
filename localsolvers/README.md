# localsolvers

Native solver executables for local (desktop) runs, one directory per platform:
`linux64/`, `mac64/` (universal x86_64 + arm64) and `win64/`. Nothing in them is tracked in git.

The build downloads them: `vcell-core/pom.xml` has one `download-maven-plugin` execution per solver
repo and platform, bound to `generate-test-resources`, so `mvn clean install dependency:copy-dependencies`
fills all three directories on any build OS. Each archive is checked against its sha256 and unpacked flat,
where `ResourceUtil.findSolverExecutable` looks for `<name>_x64` (`.exe` on Windows).

| solver repo | executables |
|---|---|
| [vcell-fvsolver](https://github.com/virtualcell/vcell-fvsolver) | `FiniteVolume_x64`, `smoldyn_x64` |
| [vcell-ode](https://github.com/virtualcell/vcell-ode) | `SundialsSolverStandalone_x64` |
| [vcell-stochastic](https://github.com/virtualcell/vcell-stochastic) | `VCellStoch_x64` |
| [vcell-nfsim](https://github.com/virtualcell/vcell-nfsim) | `NFsim_x64` |
| [vcell-hy3s](https://github.com/virtualcell/vcell-hy3s) | `Hybrid_EM_x64`, `Hybrid_MIL_x64`, `Hybrid_MIL_Adaptive_x64` |
| [vcell-chombo](https://github.com/virtualcell/vcell-chombo) | `VCellChombo2D_x64`, `VCellChombo3D_x64` (linux64 and mac64 only) |
| [vcell-mbsolver](https://github.com/virtualcell/vcell-mbsolver) | `MovingBoundary_x64` |
| [cam-center/LangevinNoVis01](https://github.com/cam-center/LangevinNoVis01) | `langevin_x64` |

Each repo's `LICENSE`, `VERSION` and other license files land in `<platform>/licenses/<repo>/`.

**Versions and checksums** are properties in the root `pom.xml` (`solvers-<repo>.version` and
`solvers-<repo>.sha256.<platform>`). To bump a solver, change its tag and copy the three checksums from
that release's `SHA256SUMS` asset:

```bash
gh release download <tag> -R virtualcell/<repo> -p SHA256SUMS -O -
```

A stale file from an older layout stays in these directories until you delete it (`mvn clean` does not
touch `localsolvers/`), so clear the platform directories if a solver behaves unexpectedly after a bump.

HPC runs don't use these binaries: each solver family runs from its own container image
(see `docs/plan-solver-repos.md`).
