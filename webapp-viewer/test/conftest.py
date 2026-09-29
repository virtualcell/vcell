"""
Fixtures for the field viewer's browser tests.

The page is served by the real `FieldViewerServer`, started over vcell-client's test fixtures by
`org.vcell.client.viz.FieldViewerFixtureServer` (a Java main in vcell-client's test sources): finite-volume
runs in 2D and 3D, FEniCSx bundles in 2D (fixed and moving) and 3D, and a stand-in MovingBoundary run.
Every test runs once per browser engine (Chromium, WebKit, Firefox), and fails if the console shows an
`is not permitted` refusal from the wasm invoker (README, "Notes for anyone editing this").
"""
import json
import os
import pathlib
import subprocess
import threading
import time

import pytest
from playwright.sync_api import sync_playwright

HERE = pathlib.Path(__file__).resolve().parent
WEBAPP = HERE.parent
REPO = WEBAPP.parent
BUNDLE = WEBAPP / 'assets' / 'vtk-wasm' / 'vcell-vtk-wasm32-emscripten.tar.gz'
CLASSPATH_FILE = REPO / 'vcell-client' / 'target' / 'fixture-classpath.txt'
ENGINES = ('chromium', 'webkit', 'firefox')
# headless Chromium has no GPU: WebGL 2 through SwiftShader, as the README's checks did
CHROMIUM_ARGS = ['--use-angle=swiftshader', '--enable-unsafe-swiftshader']


def pytest_addoption(parser):
    parser.addoption('--engine', action='append', choices=ENGINES,
                     help='run in this browser engine only (repeatable); default: all three')
    parser.addoption('--no-build', action='store_true',
                     help="reuse vcell-client's compiled classes and classpath instead of running Maven first")


def pytest_generate_tests(metafunc):
    if 'engine' in metafunc.fixturenames:
        metafunc.parametrize('engine', metafunc.config.getoption('engine') or ENGINES, scope='session')


@pytest.fixture(scope='session')
def fixture_server(pytestconfig):
    """The fixture server, compiled if needed: {'port': …, 'datasets': {role: {'sim': …, 'job': …}}}."""
    if not BUNDLE.is_file():
        pytest.exit('the vtk.wasm bundle is missing: run `npm run fetch:vtk-wasm` in webapp-viewer/ first', 2)
    if not pytestconfig.getoption('no_build') or not CLASSPATH_FILE.is_file():
        # compile vcell-client's tests and everything they need, and write their classpath
        subprocess.run(['mvn', '-q', '--batch-mode', '-pl', 'vcell-client', '-am', 'test-compile',
                        'dependency:build-classpath', '-Dmdep.outputFile=target/fixture-classpath.txt',
                        '-Dmdep.includeScope=test'], cwd=REPO, check=True)
    classpath = os.pathsep.join([str(REPO / 'vcell-client' / 'target' / 'test-classes'),
                                 str(REPO / 'vcell-client' / 'target' / 'classes'),
                                 CLASSPATH_FILE.read_text().strip()])
    proc = subprocess.Popen(['java', '-cp', classpath, 'org.vcell.client.viz.FieldViewerFixtureServer', str(WEBAPP)],
                            stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    info = None
    lines = []
    deadline = time.monotonic() + 120
    while info is None and time.monotonic() < deadline:
        line = proc.stdout.readline()
        if not line:
            break
        lines.append(line)
        if line.startswith('FIXTURE '):
            info = json.loads(line[len('FIXTURE '):])
    if info is None:
        proc.kill()
        pytest.exit('the fixture server did not start:\n' + ''.join(lines[-40:]), 2)
    # keep reading, so the server never blocks on a full pipe
    threading.Thread(target=lambda: [None for _ in proc.stdout], daemon=True).start()
    yield info
    proc.terminate()
    try:
        proc.wait(10)
    except subprocess.TimeoutExpired:
        proc.kill()


@pytest.fixture(scope='session')
def pw():
    with sync_playwright() as p:
        yield p


@pytest.fixture(scope='session')
def browser(pw, engine):
    b = getattr(pw, engine).launch(args=CHROMIUM_ARGS if engine == 'chromium' else [])
    yield b
    b.close()


class Viewer:
    """One viewer page on one fixture dataset, with the handful of reads and gestures the tests need."""

    def __init__(self, page, logs):
        self.page = page
        self.logs = logs

    @property
    def canvas(self):
        return self.page.locator('#canvas')

    def centre(self, dx=0, dy=0):
        box = self.canvas.bounding_box()
        return {'x': box['width'] / 2 + dx, 'y': box['height'] / 2 + dy}

    def click(self, dx=0, dy=0, add=False):
        self.canvas.click(position=self.centre(dx, dy), modifiers=['Shift'] if add else [])

    def wait_probes(self, n):
        """Until the panel shows n probes with their traces (the fetch is debounced, then answered)."""
        self.page.wait_for_function(
            """n => document.querySelectorAll('#probeList li').length === n
                 && document.querySelectorAll('#probeSvg .trace-group').length === n
                 && !document.getElementById('probeTitle').textContent.includes('loading')""",
            arg=n, timeout=20000)

    def markers(self):
        """{probe id: (x, y)} of the overlay's markers, in canvas pixels."""
        return self.page.eval_on_selector_all(
            '#overlay .marker',
            "gs => Object.fromEntries(gs.map(g => [g.dataset.probe, [+g.querySelector('circle').getAttribute('cx'),"
            " +g.querySelector('circle').getAttribute('cy')]]))")

    def rows(self):
        return self.page.eval_on_selector_all('#probeList li', 'els => els.map(e => e.textContent)')

    def refusals(self):
        return [line for line in self.logs if 'is not permitted' in line]


@pytest.fixture
def open_viewer(browser, fixture_server):
    """open_viewer(role) → a Viewer on that fixture dataset, rendered and ready."""
    pages = []

    def open_(role):
        dataset = fixture_server['datasets'][role]
        page = browser.new_page(viewport={'width': 1000, 'height': 1000})
        logs = []
        page.on('console', lambda m: logs.append(m.text))
        page.on('pageerror', lambda e: logs.append(f'page error: {e}'))
        pages.append((page, logs))
        page.goto(f"http://127.0.0.1:{fixture_server['port']}/?sim={dataset['sim']}&job={dataset['job']}")
        page.wait_for_function(
            "document.getElementById('status').textContent.includes('rendered')"
            " || document.getElementById('status').classList.contains('err')", timeout=120000)
        status = page.inner_text('#status')
        assert 'rendered' in status, status
        return Viewer(page, logs)

    yield open_
    for page, logs in pages:
        refusals = [line for line in logs if 'is not permitted' in line]
        errors = [line for line in logs if line.startswith('page error')]
        page.close()
        assert not refusals, f'the wasm invoker refused calls: {refusals[:5]}'
        assert not errors, f'uncaught errors on the page: {errors[:5]}'
