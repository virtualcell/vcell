"""
Kymographs (docs/plan-plotting.md §4.2, §4.4, §4.5): the Line tool, the typed line, the kymograph panel (image,
colour bar, time-row cursor, line profile), click → time and shift-click → probe, the exports, the automatic
retries, and the desktop cross-check (§6.3) against the golden files of vcell-client's
KymographDesktopResampleTest.
"""
import csv
import io
import json
import pathlib

import pytest

REPO = pathlib.Path(__file__).resolve().parents[2]
GOLDEN = REPO / 'vcell-client' / 'src' / 'test' / 'resources' / 'org' / 'vcell' / 'client' / 'viz' / 'kymo'
FV = ['fv2d', 'fv3d']


def wait_kymograph(v):
    """Until the kymograph is drawn for the current line: its title names the samples, and nothing is loading."""
    v.page.wait_for_function(
        """() => { const t = document.getElementById('kymoTitle').textContent;
                   return t.includes('samples ×') && !t.includes('loading'); }""", timeout=30000)


def draw_line(v, clicks):
    """The Line tool: a click per vertex, then Enter."""
    v.page.click('#lineTool')
    for dx, dy in clicks:
        v.click(dx, dy)
    v.page.wait_for_function(
        f"document.getElementById('lineCoords').value.split(';').length === {len(clicks)}", timeout=10000)
    v.page.keyboard.press('Enter')
    wait_kymograph(v)


def type_line(v, text):
    """A typed line: the Line tool opens the panel with the line's field; Enter draws it."""
    if v.page.is_hidden('#kymoPanel'):
        v.page.click('#lineTool')
    v.page.fill('#lineCoords', text)
    v.page.press('#lineCoords', 'Enter')
    wait_kymograph(v)


def kymo_state(v):
    """What the page drew: the image's size and how many of its pixels are opaque, the cursor, the rows."""
    return v.page.evaluate("""() => {
        const c = document.getElementById('kymoCanvas');
        const d = c.getContext('2d').getImageData(0, 0, c.width, c.height).data;
        let opaque = 0;
        for (let i = 3; i < d.length; i += 4) if (d[i] === 255) opaque++;
        const cursor = document.querySelector('#kymoSvg .time-cursor');
        return { width: c.width, height: c.height, opaque, row: c.dataset.row,
                 cursorY: cursor ? +cursor.getAttribute('y1') : null,
                 title: document.getElementById('kymoTitle').textContent,
                 note: document.getElementById('kymoNote').textContent,
                 profile: document.querySelectorAll('#kymoProfile .trace').length,
                 overlay: document.querySelectorAll('#overlay .kymo-line').length };
    }""")


def samples_in_title(title):
    return int(title.split(' samples ×')[0].split('·')[-1].strip())


def download_text(v, button):
    with v.page.expect_download() as download:
        v.page.click(button)
    return open(download.value.path(), encoding='utf-8').read()


def csv_rows(text):
    return list(csv.reader(io.StringIO('\n'.join(line for line in text.splitlines() if not line.startswith('#')))))


@pytest.mark.parametrize('role', FV)
def test_two_clicks_and_enter_draw_a_kymograph(open_viewer, role):
    v = open_viewer(role)
    draw_line(v, [(-120, -60), (110, 70)])
    k = kymo_state(v)
    n = samples_in_title(k['title'])
    times = int(v.page.get_attribute('#time', 'max')) + 1
    assert k['width'] == min(1024, 4 * n) and k['height'] == times, k
    assert k['opaque'] > 0, 'the image is not blank'
    assert k['cursorY'] is not None and k['row'] == str(times - 1), 'the cursor sits on the current (last) time'
    assert k['profile'] >= 1, 'a line profile at the current time'
    assert k['overlay'] > 0, 'the line is drawn over the view'
    assert len(v.page.input_value('#lineCoords').split(';')) == 2


def test_clicking_a_row_moves_the_slider_and_the_cursor_follows(open_viewer):
    v = open_viewer('fv3d')
    draw_line(v, [(-120, -60), (110, 70)])
    before = kymo_state(v)['cursorY']
    canvas = v.page.locator('#kymoCanvas')
    box = canvas.bounding_box()
    canvas.click(position={'x': box['width'] / 2, 'y': 2})  # the top row: the first time
    v.page.wait_for_function("document.getElementById('time').value === '0'")
    after = kymo_state(v)
    assert after['row'] == '0' and after['cursorY'] < before - 50, (before, after)


def test_shift_clicking_the_kymograph_probes_that_point(open_viewer):
    v = open_viewer('fv2d')
    type_line(v, '-10,-9; 9,8')
    canvas = v.page.locator('#kymoCanvas')
    box = canvas.bounding_box()
    canvas.click(position={'x': box['width'] * 0.5, 'y': box['height'] * 0.5}, modifiers=['Shift'])
    v.wait_probes(1)
    assert len(v.markers()) == 1


def test_backspace_removes_a_vertex_and_escape_cancels(open_viewer):
    v = open_viewer('fv2d')
    v.page.click('#lineTool')
    assert v.page.get_attribute('#lineTool', 'aria-pressed') == 'true'
    for dx, dy in [(-100, 0), (0, 50), (100, 0)]:
        v.click(dx, dy)
    v.page.wait_for_function("document.getElementById('lineCoords').value.split(';').length === 3")
    v.page.keyboard.press('Backspace')
    v.page.wait_for_function("document.getElementById('lineCoords').value.split(';').length === 2")
    v.page.keyboard.press('Escape')
    v.page.wait_for_function("document.getElementById('kymoPanel').hidden")
    assert v.page.get_attribute('#lineTool', 'aria-pressed') == 'false'
    assert v.page.eval_on_selector_all('#overlay .kymo-line', 'ls => ls.length') == 0
    # with the tool off, a click is a probe again
    v.click()
    v.wait_probes(1)


def test_a_double_click_finishes_the_line(open_viewer):
    v = open_viewer('fv2d')
    v.page.click('#lineTool')
    v.click(-100, -40)
    v.canvas.dblclick(position=v.centre(90, 60))
    wait_kymograph(v)
    assert len(v.page.input_value('#lineCoords').split(';')) == 2


def test_the_exports(open_viewer):
    v = open_viewer('fv3d')
    type_line(v, '0,0.3,2; 4,3.4,2')
    n = samples_in_title(kymo_state(v)['title'])
    times = int(v.page.get_attribute('#time', 'max')) + 1
    samples = download_text(v, '#kymoSamplesCsv')
    assert '# sampling: voxel-crossing' in samples and '# path: 0,0.3,2; 4,3.4,2' in samples
    rows = csv_rows(samples)
    assert rows[0] == ['i', 'arcLength', 'x', 'y', 'z', 'volumeIndex', 'membraneIndex', 'inDomain']
    assert len(rows) == 1 + n
    assert any(r[6] != '-1' for r in rows[1:]), 'the line crosses the membrane'
    matrix = csv_rows(download_text(v, '#kymoMatrixCsv'))
    assert matrix[0][0] == 'time\\arcLength' and len(matrix[0]) == 1 + n
    assert len(matrix) == 1 + times and all(len(r) == 1 + n for r in matrix)
    assert any(cell == '' for r in matrix[1:] for cell in r[1:]), 'samples in the other compartment are gaps'
    with v.page.expect_download() as download:
        v.page.click('#kymoPng')
    assert open(download.value.path(), 'rb').read(8) == b'\x89PNG\r\n\x1a\n'


def golden_matrix(text):
    """{'Distances': [...], time: [...]} from a golden file or the viewer's Desktop CSV (comments and 'Times' dropped)."""
    out = {}
    for line in text.splitlines():
        if not line or line.startswith('#') or line == 'Times':
            continue
        key, *values = line.split(',')
        out[key if key == 'Distances' else float(key)] = [float(x) for x in values]
    return out


# the cases of KymographDesktopResampleTest.CASES, typed as a viewer user types them
DESKTOP_CASES = [
    ('fv2d', '597714292-Dex-0.csv', '-10,-9; 9,8'),
    ('fv2d', '597714292-Dex-1.csv', '-10,0; 0,0; 3,10'),
    ('fv3d', '868220316-s0-2.csv', '0,0.3,2; 4,3.4,2'),
    ('fv3d', '868220316-s0-3.csv', '0,2,2; 4,2,2'),
]


@pytest.mark.parametrize('role,golden,line', DESKTOP_CASES)
def test_the_desktop_csv_is_what_the_desktop_shows(open_viewer, role, golden, line):
    """§6.3: the viewer's desktop-resampled export equals the desktop kymograph (KymographDesktopResampleTest)."""
    v = open_viewer(role)
    type_line(v, line)
    got = golden_matrix(download_text(v, '#kymoDesktopCsv'))
    want = golden_matrix((GOLDEN / golden).read_text())
    assert got.keys() == want.keys()
    for key in want:
        assert got[key] == want[key], key  # value for value: same doubles, same arithmetic


def test_a_line_outside_the_domain_says_so(open_viewer):
    v = open_viewer('fv3d')
    type_line(v, '1.8,2,2; 2.2,2,2')  # inside the ball of subdomain1; the variable lives in subdomain0
    k = kymo_state(v)
    assert 'the line lies outside subdomain0' in k['note']
    assert k['opaque'] == 0, 'every pixel is a gap'


def test_over_the_value_limit_the_viewer_retries_with_the_suggested_stride(open_viewer):
    v = open_viewer('fv3d')
    answered = []

    def once_too_many(route):
        if not answered:
            answered.append(route.request.url)
            route.fulfill(status=400, content_type='application/json',
                          body=json.dumps({'error': 'too many values; use tstep=2', 'suggestedTstep': 2}))
        else:
            route.continue_()

    v.page.route('**/kymograph?**', once_too_many)
    type_line(v, '0,0.3,2; 4,3.4,2')
    k = kymo_state(v)
    assert k['height'] == 3, 'every 2nd of the 6 saved times: 0, 2, 4'
    assert 'every 2nd saved time' in k['note']


def test_a_busy_server_is_retried_once(open_viewer):
    v = open_viewer('fv2d')
    answered = []

    def once_busy(route):
        if not answered:
            answered.append(route.request.url)
            route.fulfill(status=503, content_type='application/json', body='{"error":"busy","busy":true}')
        else:
            route.continue_()

    v.page.route('**/kymograph?**', once_busy)
    type_line(v, '-10,-9; 9,8')
    assert len(answered) == 1


def test_a_variable_switch_refetches_the_kymograph(open_viewer):
    v = open_viewer('fv3d')
    type_line(v, '0,0.3,2; 4,3.4,2')
    assert kymo_state(v)['title'].startswith('s0 ·')
    v.page.select_option('#variable', 's1')
    v.page.wait_for_function("document.getElementById('kymoTitle').textContent.startsWith('s1 ·')"
                             " && document.getElementById('kymoTitle').textContent.includes('samples ×')",
                             timeout=30000)


def test_the_line_follows_the_camera(open_viewer):
    v = open_viewer('fv3d')
    draw_line(v, [(-120, -60), (110, 70)])
    first = lambda: v.page.eval_on_selector('#overlay .kymo-vertex', "c => [+c.getAttribute('cx'), +c.getAttribute('cy')]")
    before = first()
    box = v.canvas.bounding_box()
    x0, y0 = box['x'] + box['width'] / 2, box['y'] + box['height'] * 0.85
    v.page.mouse.move(x0, y0)
    v.page.mouse.down()
    for step in range(1, 9):
        v.page.mouse.move(x0 + 10 * step, y0)
    v.page.mouse.up()
    v.page.wait_for_timeout(400)  # the occlusion check is debounced
    after = first()
    assert abs(after[0] - before[0]) > 3, (before, after)


def test_the_line_tool_waits_for_later_versions_on_body_fitted_runs(open_viewer):
    v = open_viewer('fenics2d')
    assert v.page.is_disabled('#lineTool')
