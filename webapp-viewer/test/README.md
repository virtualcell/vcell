# Browser tests for the field viewer

End-to-end tests of `webapp-viewer/` in real browsers. The page is served by VCell's own
`FieldViewerServer`, over the fixture runs in vcell-client's test resources, by a small Java main in
vcell-client's test sources, `org.vcell.client.viz.FieldViewerFixtureServer`. It registers:

| role | run |
|---|---|
| `fv2d` | finite volume, 2D (15 × 15, `Cyt` in `EC`) |
| `fv3d` | finite volume, 3D (5 × 5 × 5, two compartments) |
| `fenics2d` | FEniCSx, a 2D disk |
| `fenicsMoving` | FEniCSx, a 2D disk moving along x (ALE) |
| `fenics3d` | FEniCSx, a sphere in a box with its membrane (3D) |
| `movingBoundary` | a stand-in MovingBoundary run (`FakeMovingBoundaryRun`), served through the real VTU seam |

Every test runs in Chromium (WebGL 2 through SwiftShader), WebKit and Firefox, Playwright's own builds,
and fails if the console shows an `is not permitted` refusal from the wasm invoker or an uncaught error.

| module | covers |
|---|---|
| `test_probes.py` | probes: click, shift-click, the Add toggle, markers, the time cursor, CSV |
| `test_kymograph.py` | kymographs: the Line tool and the typed line, the image and cursor, click → time, shift-click → probe, the exports, the retries, and the desktop cross-check |

The desktop cross-check compares the viewer's *Desktop CSV* with the golden files in
`vcell-client/src/test/resources/org/vcell/client/viz/kymo/`. Those are what the desktop kymograph shows for
the same lines, computed by `KymographDesktopResampleTest` with the desktop's own sampling and resampling; that
Java test checks them, and rewrites them with `-Dvcell.kymographGolden.write=<dir>`.

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
