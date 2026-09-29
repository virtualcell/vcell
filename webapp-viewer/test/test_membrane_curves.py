"""
Membrane curves (docs/plan-plotting.md P7): on a 2D membrane the Line tool draws a curve along the membrane,
between picks the server snaps onto it, and the overlay follows the curve's own samples. The fixture
`fenics2dMembrane` is a disk of radius 0.5 in a 2 × 2 box with the receptor R on its membrane (a closed curve of
line cells), shown filling most of the view.
"""
import math

from test_kymograph import kymo_state, samples_in_title, wait_kymograph

MEMBRANE = '&domain=mem_dom&var=R'


def overlay_curve(v):
    """The overlay's line pieces [(x1, y1, x2, y2)] and its vertex marks [(x, y)], in canvas pixels."""
    return v.page.evaluate("""() => ({
        pieces: [...document.querySelectorAll('#overlay .kymo-line')].map(l =>
            ['x1', 'y1', 'x2', 'y2'].map(a => +l.getAttribute(a))),
        marks: [...document.querySelectorAll('#overlay .kymo-vertex')].map(c => [+c.getAttribute('cx'), +c.getAttribute('cy')]),
    })""")


def draw_curve(v, clicks):
    """The Line tool on a membrane: a click per point, then Enter; returns the /kymograph response."""
    v.page.click('#lineTool')
    for dx, dy in clicks:
        v.click(dx, dy)
    v.page.wait_for_function(
        f"document.getElementById('lineCoords').value.split(';').length === {len(clicks)}", timeout=10000)
    with v.page.expect_response(lambda r: '/kymograph?' in r.url and r.status == 200) as response:
        v.page.keyboard.press('Enter')
    wait_kymograph(v)
    return response.value.json()


def test_a_curve_follows_the_membrane_between_two_picks(open_viewer):
    v = open_viewer('fenics2dMembrane', MEMBRANE)
    assert 'along the membrane' in v.page.get_attribute('#lineTool', 'title')
    r = v.centre()['x'] / 2  # the membrane's radius on screen, roughly: it fills about half the view's width
    # a quarter turn: beside the membrane on the right, and just inside it at the top
    data = draw_curve(v, [(0.51 * 2 * r, 0), (0, -0.46 * 2 * r)])
    assert data['sampling'] == 'membrane' and data['location'] == 'point'
    n = len(data['samples']['arcLength'])
    assert 12 <= n <= 24, n
    assert abs(data['pathLength'] - math.pi / 4) < 0.02, data['pathLength']
    assert all(data['samples']['inDomain'])

    k = kymo_state(v)
    assert samples_in_title(k['title']) == n
    assert 'along the membrane' in k['title'] and 'along the membrane' in k['note'], (k['title'], k['note'])
    assert k['width'] == min(1024, 4 * n) and k['opaque'] == k['width'] * k['height'], 'no gaps on the membrane'

    # the overlay: one piece per sample interval along the curve, and a mark at each snapped pick
    drawn = overlay_curve(v)
    assert len(drawn['pieces']) == n - 1, (len(drawn['pieces']), n)
    assert len(drawn['marks']) == 2
    centre = v.centre()
    radius = [math.hypot(x - centre['x'], y - centre['y']) for p in drawn['pieces'] for x, y in (p[:2], p[2:])]
    radius += [math.hypot(x - centre['x'], y - centre['y']) for x, y in drawn['marks']]
    assert max(radius) - min(radius) < 0.05 * max(radius), 'every piece lies on the circle, not on the chord'
    # the curve bulges away from the straight line between the picks
    (ax, ay), (bx, by) = drawn['marks']
    chord = math.hypot(bx - ax, by - ay)
    bulge = max(abs((bx - ax) * (ay - y) - (ax - x) * (by - ay)) / chord for p in drawn['pieces'] for x, y in (p[:2], p[2:]))
    assert bulge > 0.2 * chord, bulge


def test_a_third_pick_takes_the_long_way_round(open_viewer):
    v = open_viewer('fenics2dMembrane', MEMBRANE)
    r = v.centre()['x'] / 2
    short = draw_curve(v, [(r, -0.2 * r), (r, 0.2 * r)])
    long = draw_curve(v, [(r, -0.2 * r), (-r, 0), (r, 0.2 * r)])
    assert long['pathLength'] > 5 * short['pathLength'], (short['pathLength'], long['pathLength'])
    assert len(overlay_curve(v)['marks']) == 3


def test_a_pick_far_from_the_membrane_says_so(open_viewer):
    v = open_viewer('fenics2dMembrane', MEMBRANE)
    v.page.click('#lineTool')
    v.page.fill('#lineCoords', '0.9,0.9; 0.5,0')
    v.page.press('#lineCoords', 'Enter')
    v.page.wait_for_function("document.getElementById('kymoNote').textContent.includes('not on the membrane')",
                             timeout=20000)


def test_a_domain_in_the_url_opens_one_of_its_variables(open_viewer):
    v = open_viewer('fenics2dMembrane', '&domain=mem_dom')
    assert v.page.input_value('#variable') == 'R'


def test_the_tool_is_off_on_a_3d_membrane_surface(open_viewer):
    v = open_viewer('fenics3d', MEMBRANE)
    assert v.page.is_disabled('#lineTool')
    assert '3D membrane surface' in v.page.get_attribute('#lineTool', 'title')
    # and back on for a volume variable
    v.page.select_option('#variable', 's_cyto')
    v.page.wait_for_function("!document.getElementById('lineTool').disabled", timeout=60000)


def test_a_click_on_a_3d_membrane_surface_probes_it(open_viewer):
    """The surface pick (P7): a 3D membrane's triangles are pickable, so a click there places a membrane probe."""
    v = open_viewer('fenics3d', MEMBRANE)
    with v.page.expect_response(lambda r: '/timeseries?' in r.url and r.status == 200) as response:
        v.click()
    s = response.value.json()['series'][0]
    assert s['inDomain'] and all(x is not None for x in s['values']), s
    v.wait_probes(1)
    assert len(v.markers()) == 1
