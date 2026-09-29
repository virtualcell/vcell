"""
Probes (docs/plan-plotting.md §4.1, §4.3, §4.5): click to probe, shift-click (or the Add toggle) to add,
one trace per probe in the probe panel, a marker per probe over the canvas, the time cursor, CSV export.
"""
import csv
import io

import pytest

# every mode that can place probes: finite volume 2D and 3D, FEniCSx 2D (fixed and moving) and 3D,
# MovingBoundary 2D, and Chombo 2D and 3D (stand-ins served through the real VTU seam)
ALL = ['fv2d', 'fv3d', 'fenics2d', 'fenicsMoving', 'fenics3d', 'movingBoundary', 'chombo2d', 'chombo3d']
# modes whose probe is the very point clicked (body-fitted); a finite-volume probe is its voxel's centre
BODY_FITTED = ['fenics2d', 'fenicsMoving', 'fenics3d', 'movingBoundary', 'chombo2d', 'chombo3d']


@pytest.mark.parametrize('role', ALL)
def test_shift_click_adds_a_probe_and_its_trace(open_viewer, role):
    v = open_viewer(role)
    v.click()
    v.wait_probes(1)
    v.click(40, 25, add=True)
    v.wait_probes(2)
    assert len(v.markers()) == 2
    rows = v.rows()
    assert rows[0].startswith('P1 (') and rows[1].startswith('P2 (')
    # a plain click replaces the probes
    v.click(-30, -20)
    v.wait_probes(1)
    assert list(v.markers()) == ['1']
    # ✕ removes one
    v.click(40, 25, add=True)
    v.wait_probes(2)
    v.page.locator('#probeList li[data-probe="1"] button[title^="remove"]').click()
    v.wait_probes(1)
    assert list(v.markers()) == ['2']
    assert v.rows()[0].startswith('P2 (')
    # Clear empties the panel and the overlay
    v.page.click('#probeClear')
    v.page.wait_for_function("document.getElementById('probePanel').hidden")
    assert v.markers() == {}


def test_the_add_toggle_adds_on_a_plain_click(open_viewer):
    v = open_viewer('fenics2d')
    v.page.click('#addProbes')
    assert v.page.get_attribute('#addProbes', 'aria-pressed') == 'true'
    v.click()
    v.click(30, 30)
    v.click(-30, 30)
    v.wait_probes(3)


@pytest.mark.parametrize('role', BODY_FITTED)
def test_markers_sit_where_the_probes_were_clicked(open_viewer, role):
    # the overlay inverts the camera's projection; a drift here would put markers off their points
    v = open_viewer(role)
    clicks = [(0, 0), (60, -35), (-45, 50)]
    for k, (dx, dy) in enumerate(clicks):
        v.click(dx, dy, add=k > 0)
    v.wait_probes(3)
    markers = v.markers()
    for k, (dx, dy) in enumerate(clicks):
        want = v.centre(dx, dy)
        x, y = markers[str(k + 1)]
        assert abs(x - want['x']) < 1.5 and abs(y - want['y']) < 1.5, (k, (x, y), want)


@pytest.mark.parametrize('role', ['fv3d', 'fenics3d', 'chombo3d'])
def test_markers_follow_the_camera(open_viewer, role):
    v = open_viewer(role)
    v.click()
    v.click(40, 25, add=True)
    v.wait_probes(2)
    before = v.markers()
    box = v.canvas.bounding_box()
    x0, y0 = box['x'] + box['width'] / 2, box['y'] + box['height'] * 0.8
    v.page.mouse.move(x0, y0)
    v.page.mouse.down()
    for step in range(1, 9):
        v.page.mouse.move(x0 + 10 * step, y0)
    v.page.mouse.up()
    v.page.wait_for_timeout(400)  # the occlusion check is debounced
    after = v.markers()
    assert set(after) == set(before)
    assert any(abs(after[k][0] - before[k][0]) > 3 for k in before), (before, after)


@pytest.mark.parametrize('role', ['fv3d', 'fenics3d'])
def test_a_marker_behind_the_geometry_is_drawn_hollow(open_viewer, role):
    v = open_viewer(role)
    v.click()
    v.wait_probes(1)
    v.page.wait_for_timeout(400)  # the occlusion check is debounced
    hidden = lambda: v.page.eval_on_selector_all('#overlay .marker.occluded', 'gs => gs.length')
    assert hidden() == 0, 'a probe picked on the surface faces the camera'
    # orbit half way round: the probe is now on the far side
    box = v.canvas.bounding_box()
    x0, y0 = box['x'] + box['width'] / 2, box['y'] + box['height'] * 0.85
    v.page.mouse.move(x0, y0)
    v.page.mouse.down()
    for step in range(1, 37):
        v.page.mouse.move(x0 + 10 * step, y0)  # 0.5° per pixel: 180°
    v.page.mouse.up()
    v.page.wait_for_function("document.querySelectorAll('#overlay .marker.occluded').length === 1", timeout=5000)


def test_the_time_cursor_follows_the_slider_and_a_plot_click_moves_it(open_viewer):
    v = open_viewer('fenicsMoving')
    v.click()
    v.wait_probes(1)
    cursor = lambda: float(v.page.get_attribute('#probeSvg .time-cursor', 'x1'))
    last = int(v.page.get_attribute('#time', 'max'))
    assert v.page.input_value('#time') == str(last)
    at_end = cursor()
    v.page.eval_on_selector('#time', "el => { el.value = '0'; el.dispatchEvent(new Event('input')); }")
    at_start = cursor()
    assert at_start < at_end - 100
    # clicking near the plot's right edge moves the slider to the last time
    plot = v.page.locator('#probeSvg')
    box = plot.bounding_box()
    plot.click(position={'x': box['width'] - 14, 'y': box['height'] / 2})
    v.page.wait_for_function(f"document.getElementById('time').value === '{last}'")
    assert cursor() == pytest.approx(at_end, abs=0.2)


@pytest.mark.parametrize('role', ['fv2d', 'fenics3d'])
def test_csv_has_one_column_per_probe_and_one_row_per_time(open_viewer, role):
    v = open_viewer(role)
    v.click()
    v.click(40, 25, add=True)
    v.wait_probes(2)
    with v.page.expect_download() as download:
        v.page.click('#probeCsv')
    text = open(download.value.path(), encoding='utf-8').read()
    comments = [line for line in text.splitlines() if line.startswith('#')]
    assert [c.split(':')[0] for c in comments] == ['# sim', '# job', '# var', '# domain']
    rows = list(csv.reader(io.StringIO('\n'.join(line for line in text.splitlines() if not line.startswith('#')))))
    header, body = rows[0], rows[1:]
    assert header[0] == 'time'
    assert header[1].startswith('P1(') and header[2].startswith('P2(') and len(header) == 3
    times = int(v.page.get_attribute('#time', 'max')) + 1
    assert len(body) == times
    assert all(len(r) == 3 for r in body)


def test_a_variable_switch_keeps_the_probes_and_refetches(open_viewer):
    v = open_viewer('fenics3d')
    v.click()
    v.click(40, 25, add=True)
    v.wait_probes(2)
    v.page.select_option('#variable', 's_ext')
    v.page.wait_for_function(
        "document.getElementById('probeTitle').textContent.startsWith('s_ext')"
        " && [...document.querySelectorAll('#probeSvg text')].some(t => t.textContent === 's_ext [ext_dom]')",
        timeout=30000)
    v.wait_probes(2)
    assert len(v.markers()) == 2
