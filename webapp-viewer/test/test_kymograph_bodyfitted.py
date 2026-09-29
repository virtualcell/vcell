"""
Kymographs of body-fitted runs (docs/plan-plotting.md P5): FEniCSx 2D, 3D and ALE, and Chombo 2D. The line is
sampled evenly; FEniCSx samples are interpolated (P1), Chombo samples take their cell's value; samples outside
the variable's domain are gaps; on a moving mesh the line is fixed in the lab frame, and says so.
"""
import pytest

from test_kymograph import csv_rows, download_text, draw_line, kymo_state, samples_in_title, type_line, wait_kymograph

BODY_FITTED_LINES = ['fenics2d', 'fenicsMoving', 'fenics3d', 'chombo2d']


def typed_kymograph(v, text):
    """Type a line and return the /kymograph response the page drew."""
    with v.page.expect_response(lambda r: '/kymograph?' in r.url and r.status == 200) as response:
        type_line(v, text)
    return response.value.json()


@pytest.mark.parametrize('role', BODY_FITTED_LINES)
def test_two_clicks_and_enter_draw_a_body_fitted_kymograph(open_viewer, role):
    v = open_viewer(role)
    assert v.page.is_enabled('#lineTool')
    with v.page.expect_response(lambda r: '/kymograph?' in r.url and r.status == 200) as response:
        draw_line(v, [(-60, -30), (55, 35)])
    k = kymo_state(v)
    data = response.value.json()
    n = samples_in_title(k['title'])
    times = int(v.page.get_attribute('#time', 'max')) + 1
    assert data['sampling'] == 'uniform' and len(data['samples']['arcLength']) == n
    assert data['location'] == ('cell' if role.startswith('chombo') else 'point')
    assert k['width'] == min(1024, 4 * n) and k['height'] == times, k
    assert k['opaque'] > 0, 'the image is not blank'
    assert k['cursorY'] is not None and k['row'] == str(times - 1)
    assert k['profile'] >= 1 and k['overlay'] > 0
    assert 'evenly spaced samples' in k['note'], k['note']
    # the desktop's resampling of raw values is finite-volume parity only
    assert v.page.is_hidden('#kymoDesktopCsv')
    moving = role == 'fenicsMoving'
    assert data['movingMesh'] is moving
    assert ('fixed line (lab frame)' in k['title']) is moving, k['title']
    assert ('fixed line (lab frame)' in k['note']) is moving, k['note']


def test_a_3d_line_through_two_domains_is_a_gap_in_the_other(open_viewer):
    v = open_viewer('fenics3d')
    line = '-0.9,0.05,0.03; 0.9,0.05,0.03'  # through the ball (cyto_dom) and the box around it (ext_dom)
    v.page.select_option('#variable', 's_cyto')
    v.page.wait_for_function("document.getElementById('status').textContent.includes('✓')", timeout=60000)
    inner = typed_kymograph(v, line)
    with v.page.expect_response(lambda r: '/kymograph?' in r.url and 's_ext' in r.url and r.status == 200) as response:
        v.page.select_option('#variable', 's_ext')
        v.page.wait_for_function("document.getElementById('kymoTitle').textContent.startsWith('s_ext ·')"
                                 " && document.getElementById('kymoTitle').textContent.includes('samples ×')",
                                 timeout=60000)
    outer = response.value.json()
    ball = inner['samples']['inDomain']
    box = outer['samples']['inDomain']
    assert len(ball) == len(box)
    xs = inner['samples']['points'][0::3]
    for x, a, b in zip(xs, ball, box):
        assert not (a and b), f'one domain or the other at x = {x}'
        if abs(x) < 0.4:
            assert a and not b, x
        elif abs(x) > 0.55:
            assert b and not a, x
    k = kymo_state(v)
    assert 0 < k['opaque'] < k['width'] * k['height'], 'the ball is a gap in the box'


def test_a_chombo_kymograph_is_a_step_per_cell_and_its_samples_export(open_viewer):
    v = open_viewer('chombo2d')
    data = typed_kymograph(v, '0.6,3.1; 7.4,3.1')
    assert data['location'] == 'cell' and not data['movingMesh']
    n = len(data['samples']['arcLength'])
    samples = csv_rows(download_text(v, '#kymoSamplesCsv'))
    assert samples[0] == ['i', 'arcLength', 'x', 'y', 'z', 'volumeIndex', 'membraneIndex', 'inDomain']
    assert len(samples) == 1 + n and all(r[5] == '' and r[6] == '' for r in samples[1:]), 'no voxel indices here'
    assert samples[1][7] == 'false' and samples[n // 2][7] == 'true', 'the ends lie outside the disk'
    # shift-click on the image probes the sample's own point
    canvas = v.page.locator('#kymoCanvas')
    box = canvas.bounding_box()
    canvas.click(position={'x': box['width'] * 0.5, 'y': box['height'] * 0.5}, modifiers=['Shift'])
    v.wait_probes(1)
    assert v.rows()[0].startswith('P1 (')


@pytest.mark.parametrize('role,why', [('movingBoundary', 'MovingBoundary'), ('chombo3d', 'picker')])
def test_the_line_tool_waits_where_it_cannot_work_yet(open_viewer, role, why):
    v = open_viewer(role)
    assert v.page.is_disabled('#lineTool')
    assert why in v.page.get_attribute('#lineTool', 'title')
