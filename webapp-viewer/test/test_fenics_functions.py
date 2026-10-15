"""
VCell functions of a FEniCSx run: the `fenics2d` fixture carries the function u_times_x = u·(x + 2), which the
viewer offers next to the stored variable u and evaluates at the mesh vertices (FenicsFunctions): its field
draws, its kymograph and its probes answer, and they are the vertex values interpolated, not u interpolated.
"""
from test_kymograph import kymo_state, type_line


def select(v, name):
    with v.page.expect_response(lambda r: '/field?' in r.url and f'var={name}' in r.url) as response:
        v.page.select_option('#variable', name)
    v.page.wait_for_function("document.getElementById('status').textContent.includes('✓')"
                             " || document.getElementById('status').classList.contains('err')", timeout=60000)
    assert 'err' not in (v.page.get_attribute('#status', 'class') or ''), v.page.inner_text('#status')
    return response.value


def test_a_function_is_offered_drawn_kymographed_and_probed(open_viewer):
    v = open_viewer('fenics2d')
    options = v.page.eval_on_selector_all('#variable option', 'os => os.map(o => o.value)')
    assert 'u' in options and 'u_times_x' in options, options

    field = select(v, 'u_times_x')
    assert field.status == 200
    f = field.json()
    assert f['name'] == 'u_times_x' and f['domain'] == 'cytosol_dom' and f['location'] == 'point'
    lo, hi = f['range']
    assert lo < hi, 'the function varies over the disk'

    with v.page.expect_response(lambda r: '/kymograph?' in r.url and 'var=u_times_x' in r.url and r.status == 200) as response:
        type_line(v, '-0.45,0.013; 0.45,0.013')
    k = response.value.json()
    assert k['name'] == 'u_times_x' and k['location'] == 'point'
    assert any(x is not None for row in k['values'] for x in row)
    assert kymo_state(v)['opaque'] > 0

    # the same line for u: where u is drawn, the function is u·(x + 2) at the vertices, interpolated -- close to,
    # but (u being nonlinear in neither) not the product of the interpolated u and x in general; here, check the scale
    with v.page.expect_response(lambda r: '/kymograph?' in r.url and 'var=u&' in r.url and r.status == 200) as response:
        select(v, 'u')
    u = response.value.json()
    xs = k['samples']['points'][0::3]
    for row_f, row_u in zip(k['values'], u['values']):
        for x, fv, uv in zip(xs, row_f, row_u):
            if fv is not None and uv is not None:
                assert abs(fv - uv * (x + 2)) <= 0.05 * max(1.0, abs(fv)), (x, fv, uv)

    # a probe answers for the function too
    select(v, 'u_times_x')
    with v.page.expect_response(lambda r: '/timeseries?' in r.url and 'var=u_times_x' in r.url and r.status == 200) as response:
        v.click(10, 5)
    s = response.value.json()['series'][0]
    assert any(x is not None for x in s['values'])
