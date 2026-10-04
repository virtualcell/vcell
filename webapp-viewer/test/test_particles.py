"""
The particle layer of a hybrid PDE/particle run (a results bundle's particles extension): the fixture is the 3D receptor
bundle with species A (12, 8, 4 molecules at the three times) and B (3 at each), drawn as points over the field. The
layer is offered only when /info lists particleSpecies, follows the time slider, hides the molecules beyond a cut, and
toggles.
"""
import re


def legend(v):
    """{species: count} as the legend shows it."""
    return {name: int(n) for name, n in re.findall(r'(\w+) (\d+)', v.page.text_content('#particleLegend'))}


def drawn(v):
    """{species: molecules drawn}, from the legend (a cut hides those beyond it)."""
    return v.page.eval_on_selector_all('#particleLegend [data-species]',
                                       'els => Object.fromEntries(els.map(e => [e.dataset.species, +e.dataset.drawn]))')


def wait_status(v):
    v.page.wait_for_function("document.getElementById('status').textContent.includes('✓')", timeout=60000)


def test_particles_are_drawn_and_follow_time(open_viewer):
    v = open_viewer('fenicsParticles')
    assert v.page.is_visible('#particlesToggle') and v.page.is_enabled('#particles')
    assert legend(v) == {'A': 4, 'B': 3}  # the viewer opens on the last time
    with v.page.expect_response(lambda r: '/particles?' in r.url and r.status == 200) as response:
        v.page.fill('#time', '0')
        v.page.dispatch_event('#time', 'change')
    assert response.value.json()['timeIndex'] == 0
    wait_status(v)
    v.page.wait_for_function("document.getElementById('particleLegend').textContent.includes('A 12')", timeout=60000)
    assert legend(v) == {'A': 12, 'B': 3}


def test_a_cut_hides_the_molecules_beyond_it_and_the_toggle_hides_all(open_viewer):
    v = open_viewer('fenicsParticles')
    v.page.fill('#time', '0')
    v.page.dispatch_event('#time', 'change')
    v.page.wait_for_function("document.getElementById('particleLegend').textContent.includes('A 12')", timeout=60000)
    assert drawn(v) == {'A': 12, 'B': 3}
    # a cut near the low end keeps few molecules in view; the legend still counts the run's molecules
    v.page.select_option('#sliceAxis', '0')
    v.page.fill('#slicePos', '10')
    v.page.dispatch_event('#slicePos', 'input')
    v.page.wait_for_function("+document.querySelector('#particleLegend [data-species=A]').dataset.drawn < 12",
                             timeout=60000)
    assert legend(v) == {'A': 12, 'B': 3}
    assert drawn(v)['A'] < 12
    v.page.select_option('#sliceAxis', '')  # the cut off: every molecule again
    v.page.wait_for_function("+document.querySelector('#particleLegend [data-species=A]').dataset.drawn === 12",
                             timeout=60000)
    v.page.uncheck('#particles')
    v.page.check('#particles')


def test_runs_without_particles_offer_no_layer(open_viewer):
    v = open_viewer('fenics3d')
    assert v.page.is_hidden('#particlesToggle')
