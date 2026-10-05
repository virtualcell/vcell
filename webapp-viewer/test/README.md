# Browser tests for the field viewer

End-to-end tests of `webapp-viewer/` in real browsers. The page is served by VCell's own
`FieldViewerServer`, over the fixture runs in vcell-client's test resources, by a small Java main in
vcell-client's test sources, `org.vcell.client.viz.FieldViewerFixtureServer`. It registers:

| role | run |
|---|---|
| `fv2d` | finite volume, 2D (15 × 15, `Cyt` in `EC`; the test membrane function `xy_PM = x + 2y + 10t` on their membrane) |
| `fv3d` | finite volume, 3D (5 × 5 × 5, two compartments) |
| `fvMembrane3d` | finite volume, 3D: MembraneFrap3D (21³, membrane variables `r_PM`, `rf_PM` on a ball's membrane, no volume variables) |
| `fenics2d` | FEniCSx, a 2D disk |
| `fenicsMoving` | FEniCSx, a 2D disk moving along x (ALE) |
| `fenics3d` | FEniCSx, a sphere in a box with its membrane (3D) |
| `fenics2dMembrane` | FEniCSx, a 2D disk in a box with its membrane, a closed curve of line cells (`receptor_2d.fenics`) |
| `fenicsParticles` | FEniCSx, the 3D receptor bundle with molecule positions added (the bundle's `particles` extension): species A with 12, 8 and 4 molecules at the three times, B with 3 (`receptor_3d_particles.fenics`) |
| `movingBoundary` | a stand-in MovingBoundary run (`FakeMovingBoundaryRun`), served through the real VTU seam |
| `chombo2d` | a stand-in Chombo run (`FakeChomboRun`), 2D: a disk of quads and cut pentagons, through the VTU seam |
| `chombo3d` | the same, 3D: a ball of voxels with polyhedra at its surface |
| `chomboRun2d` | a real Chombo run, 2D (a disk on an 8 × 8 mesh) with its membrane (`s2` on `subdomain1.vol0_Membrane`), read as the desktop reads a local run (vcell-core's `org/vcell/vis/chombo` fixture) |
| `chomboRun3d` | the same, 3D (a ball on an 8 × 8 × 8 mesh: voxels and cut-cell polyhedra, and a membrane of triangles) |

Every test runs in Chromium (WebGL 2 through SwiftShader), WebKit and Firefox, Playwright's own builds,
and fails if the console shows an `is not permitted` refusal from the wasm invoker or an uncaught error.

| module | covers |
|---|---|
| `test_probes.py` | probes: click, shift-click, the Add toggle, markers, the time cursor, CSV |
| `test_kymograph.py` | kymographs: the Line tool and the typed line, the image, the crosshair (click, drag, arrow keys) and its line scan and time series, click → time, shift-click → probe, the exports, the retries, and the desktop cross-check |
| `test_kymograph_bodyfitted.py` | kymographs of FEniCSx (2D, 3D, ALE), Chombo (2D, 3D) and MovingBoundary runs: evenly spaced samples, the lab-frame label, gaps in the other domain |
| `test_fv_membranes.py` | finite-volume membrane variables: drawn on their faces, probed there, curves along them in 2D and in the 3D cut plane, Stats, and the desktop cross-check for membrane curves |
| `test_chombo_membranes.py` | real Chombo runs: a 3D membrane drawn as a surface in 3D and probed on it, the 3D volume with its cut cells, switching between them, a 2D membrane drawn in the plane |
| `test_membrane_curves.py` | curves along a 2D FEniCSx membrane: the curve follows the membrane between the snapped picks, the overlay follows its samples, the long way round, the tool off on a 3D membrane surface |
| `test_particles.py` | the particle layer of a hybrid PDE/particle run: offered only when `/info` lists `particleSpecies`, follows the time slider, hides the molecules beyond a cut, toggles |

The desktop cross-check compares the viewer's *Desktop CSV* with the golden files in
`vcell-client/src/test/resources/org/vcell/client/viz/kymo/`. Those are what the desktop kymograph shows for
the same lines, computed by `KymographDesktopResampleTest` with the desktop's own sampling and resampling; that
Java test checks them, and rewrites them with `-Dvcell.kymographGolden.write=<dir>`. The `-m<n>` files are membrane
curves: desktop selections of membrane segments, with the lines typed for them (which that test checks select the
same samples).

## Running them

Once:

```bash
python3 -m venv .venv && . .venv/bin/activate    # any Python 3.10+
pip install -r webapp-viewer/test/requirements.txt
playwright install chromium webkit firefox
(cd webapp-viewer && npm run fetch:vtk-wasm)     # the vtk.wasm bundle the page loads
```

Then, from the repository root:

```bash
pytest webapp-viewer/test                        # all three engines
pytest webapp-viewer/test --engine webkit        # one engine (repeatable)
pytest webapp-viewer/test --no-build             # skip the Maven step, reuse vcell-client/target
```

Before starting the server, the tests run Maven to compile vcell-client's tests and everything they
depend on (`mvn -pl vcell-client -am test-compile dependency:build-classpath`, about half a minute on a
warm `~/.m2`). The classpath goes to `vcell-client/target/fixture-classpath.txt`. The server then runs
as `java -cp <that classpath> org.vcell.client.viz.FieldViewerFixtureServer webapp-viewer` on a free
loopback port. To look at the fixtures by hand, start it the same way and open the
`http://127.0.0.1:<port>/?sim=<sim>&job=0` it prints.

The tests are not in CI yet. Run them by hand before merging a change to the viewer.
