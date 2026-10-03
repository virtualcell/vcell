"""
Real Chombo results (`chomboRun2d`, `chomboRun3d`: vcell-core's org/vcell/vis/chombo fixture, a disk of radius 3
and a ball of radius 4 about (5, 5, 5), read as the desktop reads a local run), whose grids the data layer writes in
Java. A membrane variable (`s2`) is drawn on the membrane: a surface of triangles in 3D, a curve of segments in 2D.
`s2` is constant in time and names the 1.25 µm voxel it was computed in, floor(x/1.25) + 100 floor(y/1.25) +
10000 floor(z/1.25).
"""
import math

MEMBRANE = '&domain=subdomain1.vol0_Membrane&var=s2'
VOLUME = '&domain=subdomain1.vol0&var=RanC_nuc'


def code(x, y, z):
    return math.floor(x / 1.25) + 100 * math.floor(y / 1.25) + 10000 * math.floor(z / 1.25)


def codes_at(x, y, z, e=1e-6):
    """the codes of the voxels touching (x, y, z): a click in the middle of the view lies on voxel faces"""
    return {code(x + dx, y + dy, z + dz) for dx in (-e, e) for dy in (-e, e) for dz in (-e, e)}


def probe_response(v, dx, dy):
    with v.page.expect_response(lambda r: '/timeseries?' in r.url and r.status == 200) as response:
        v.click(dx, dy)
    return response.value.json()


def test_a_3d_membrane_is_drawn_as_a_surface_in_3d_and_probed_on_it(open_viewer):
    v = open_viewer('chomboRun3d', MEMBRANE)
    status = v.page.inner_text('#status')
    assert 'on subdomain1.vol0_Membrane' in status and 'drag to rotate' in status, status
    assert 'not supported yet' in v.page.get_attribute('#lineTool', 'title'), 'no curves on a 3D membrane surface'
    # the middle of the view is the near side of the ball: the click lands on (or snaps onto) its surface
    data = probe_response(v, 0, 0)
    s = data['series'][0]
    assert s['inDomain'] and s['cell'] >= 0, s
    x, y, z = s.get('snapped', s['point'])
    assert abs(math.dist((x, y, z), (5, 5, 5)) - 4) < 0.2, (x, y, z)
    assert len(set(s['values'])) == 1 and s['values'][0] in codes_at(x, y, z), s
    v.wait_probes(1)
    assert len(v.markers()) == 1


def test_a_3d_volume_with_cut_cells_is_drawn_and_probed(open_viewer):
    v = open_viewer('chomboRun3d', VOLUME)
    status = v.page.inner_text('#status')
    assert 'on subdomain1.vol0' in status and 'drag to rotate' in status, status
    data = probe_response(v, 0, 0)
    s = data['series'][0]
    assert s['inDomain'] and s['cell'] >= 0, s
    x, y, z = s['point']
    # at t = 0, RanC_nuc is 1e-8 × the code of the cell the click landed in
    assert round(s['values'][0] * 1e8) in codes_at(x, y, z), s


def test_switching_between_a_chombo_membrane_and_its_volume(open_viewer):
    v = open_viewer('chomboRun3d', MEMBRANE)
    v.page.select_option('#variable', 'RanC_nuc')
    v.page.wait_for_function(
        "document.getElementById('status').textContent.includes('✓')"
        " && document.getElementById('status').textContent.includes('on subdomain1.vol0 ')", timeout=60000)
    v.page.select_option('#variable', 's2')
    v.page.wait_for_function(
        "document.getElementById('status').textContent.includes('✓')"
        " && document.getElementById('status').textContent.includes('on subdomain1.vol0_Membrane')", timeout=60000)
    assert 'err' not in (v.page.get_attribute('#status', 'class') or '')


def test_a_2d_membrane_is_drawn_as_a_curve_in_the_plane(open_viewer):
    v = open_viewer('chomboRun2d', MEMBRANE)
    status = v.page.inner_text('#status')
    assert 'on subdomain1.vol0_Membrane' in status and 'drag to pan' in status, status
