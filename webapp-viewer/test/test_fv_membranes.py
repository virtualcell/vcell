"""
Finite-volume membrane variables (docs/plan-plotting.md P7): drawn on their membrane's faces (segments in 2D, quads
in 3D), probed there, and curves along the membrane whose kymographs are the desktop's. Fixtures: `fv2d` with its
test membrane function xy_PM = x + 2y + 10t on Cyt_EC_membrane, and `fvMembrane3d` (MembraneFrap3D: r_PM and
rf_PM on a ball's membrane, and no volume variables).
"""
import pytest

from test_kymograph import GOLDEN, download_text, golden_matrix, kymo_state, samples_in_title, type_line, wait_kymograph

MEM_2D = '&domain=Cyt_EC_membrane&var=xy_PM'
MEM_3D = '&domain=subdomain0_subdomain1_membrane&var=r_PM'
# the 2D membrane is a ring of voxel faces about 9.5 µm from the centre, which the view shows at ~26.7 px/µm
RIM_2D = 254


def probe_response(v, dx, dy, add=False):
    with v.page.expect_response(lambda r: '/timeseries?' in r.url and r.status == 200) as response:
        v.click(dx, dy, add=add)
    return response.value.json()


def curve_by_clicks(v, clicks):
    """The Line tool: a click per pick, then Enter; returns the /kymograph response."""
    v.page.click('#lineTool')
    for dx, dy in clicks:
        v.click(dx, dy)
    v.page.wait_for_function(
        f"document.getElementById('lineCoords').value.split(';').length === {len(clicks)}", timeout=10000)
    with v.page.expect_response(lambda r: '/kymograph?' in r.url and r.status == 200) as response:
        v.page.keyboard.press('Enter')
    wait_kymograph(v)
    return response.value.json()


def crop(v, axis):
    v.page.select_option('#sliceAxis', str('xyz'.index(axis)))
    v.page.wait_for_function("document.getElementById('sliceReadout').textContent !== ''", timeout=20000)


def test_a_2d_membrane_variable_is_drawn_on_its_faces_and_probed_there(open_viewer):
    v = open_viewer('fv2d', MEM_2D)
    assert 'on Cyt_EC_membrane' in v.page.inner_text('#status')
    assert v.page.is_disabled('#smoothing') and 'membrane faces' in v.page.inner_text('#smoothingReadout')
    data = probe_response(v, RIM_2D, 0)
    s = data['series'][0]
    assert s['inDomain'] and s['cell'] >= 0 and isinstance(s['membraneIndex'], int), s
    assert 'snapped' in s
    v.wait_probes(1)
    # xy_PM = x + 2y + 10t on the right edge of the ring (x ≈ 9.9 in the solver's frame, y ≈ 0): about 10 + 10t
    assert all(8 < value - 10 * t < 12 for t, value in zip(data['times'], s['values'])), s['values']


def test_a_2d_membrane_curve_follows_the_membrane(open_viewer):
    v = open_viewer('fv2d', MEM_2D)
    assert 'along the membrane' in v.page.get_attribute('#lineTool', 'title')
    data = curve_by_clicks(v, [(RIM_2D, -60), (0, -RIM_2D)])
    assert data['sampling'] == 'membrane' and data['location'] == 'cell'
    n = len(data['samples']['arcLength'])
    assert n >= 6 and all(m >= 0 for m in data['samples']['membraneIndex'])
    assert all(i == -1 for i in data['samples']['volumeIndex'])
    k = kymo_state(v)
    assert samples_in_title(k['title']) == n and 'along the membrane' in k['title']
    assert 'one per membrane element' in k['note'], k['note']
    assert k['opaque'] == k['width'] * k['height'], 'no gaps along a membrane'
    # the overlay follows the returned samples on the drawn faces, one piece per interval, a dot per pick
    pieces = v.page.eval_on_selector_all('#overlay .kymo-line', 'ls => ls.length')
    marks = v.page.eval_on_selector_all('#overlay .kymo-vertex', 'cs => cs.length')
    assert pieces == n - 1 and marks == 2
    assert v.page.is_visible('#kymoDesktopCsv'), 'a finite-volume curve has the desktop resampling'


def test_switching_between_membrane_and_volume_variables(open_viewer):
    v = open_viewer('fv2d', MEM_2D)
    v.page.select_option('#variable', 'Dex')
    v.page.wait_for_function("!document.getElementById('smoothing').disabled", timeout=60000)
    v.page.wait_for_function("document.getElementById('status').textContent.includes('✓')", timeout=60000)
    assert 'on Cyt' in v.page.inner_text('#status')
    v.page.select_option('#variable', 'xy_PM')
    v.page.wait_for_function("document.getElementById('smoothing').disabled", timeout=60000)
    v.page.wait_for_function("document.getElementById('status').textContent.includes('✓')", timeout=60000)
    assert 'on Cyt_EC_membrane' in v.page.inner_text('#status')
    assert 'err' not in (v.page.get_attribute('#status', 'class') or '')


def test_a_3d_membrane_is_probed_on_its_surface(open_viewer):
    v = open_viewer('fvMembrane3d')
    assert v.page.input_value('#variable') == 'r_PM', 'a run of membrane variables opens on one'
    data = probe_response(v, 0, 0)
    s = data['series'][0]
    assert s['inDomain'] and isinstance(s['membraneIndex'], int), s
    assert data['location'] == 'cell'
    v.wait_probes(1)
    assert len(v.markers()) == 1


def test_a_3d_membrane_curve_lies_in_the_cut_plane(open_viewer):
    v = open_viewer('fvMembrane3d')
    assert v.page.is_disabled('#lineTool')
    assert 'turn on the crop' in v.page.get_attribute('#lineTool', 'title')
    crop(v, 'z')
    v.page.wait_for_function("!document.getElementById('lineTool').disabled", timeout=10000)
    data = curve_by_clicks(v, [(180, 0), (0, -180)])
    picks = [[float(x) for x in p.split(',')] for p in v.page.input_value('#lineCoords').split(';')]
    assert all(p[2] == 5 for p in picks), picks  # projected onto the cut at z = 5
    assert data['slice'] == {'axis': 'z', 'index': 10}
    assert data['sampling'] == 'membrane' and len(data['samples']['arcLength']) > 8
    assert 'in the z slice 10' in kymo_state(v)['note']
    # the samples lie in that slice
    assert all(abs(z - 5) < 1e-9 for z in data['samples']['points'][2::3])


def test_stats_of_membrane_variables(open_viewer):
    v = open_viewer('fvMembrane3d')
    with v.page.expect_response(lambda r: '/stats?' in r.url) as response:
        v.page.click('#statsBtn')
    assert response.value.status == 200, response.value.text()
    names = [s['name'] for s in response.value.json()['series']]
    assert 'r_PM' in names and 'rf_PM' in names


# KymographDesktopResampleTest.MEMBRANE_CASES, typed as a viewer user types them (the crop axis for 3D)
MEMBRANE_DESKTOP_CASES = [
    ('fv2d', MEM_2D, None, '597714292-xy_PM-m0.csv', '2.93333,-9.53333; 8.8,-3.66667'),
    ('fvMembrane3d', MEM_3D, 'z', '956955326-r_PM-m1.csv', '8.57143,4.04762,5; 6.90476,8.09524,5'),
]


@pytest.mark.parametrize('role,query,axis,golden,line', MEMBRANE_DESKTOP_CASES)
def test_the_desktop_csv_of_a_membrane_curve_is_what_the_desktop_shows(open_viewer, role, query, axis, golden, line):
    """§6.3 for membranes: the viewer's desktop-resampled export equals the desktop's membrane kymograph."""
    v = open_viewer(role, query)
    if axis:
        crop(v, axis)
    type_line(v, line)
    got = golden_matrix(download_text(v, '#kymoDesktopCsv'))
    want = golden_matrix((GOLDEN / golden).read_text())
    assert got.keys() == want.keys()
    for key in want:
        assert got[key] == want[key], key
