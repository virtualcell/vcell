"""Shared field-viewer colormap: Cividis endpoints and strictly increasing lightness."""
import json
import pathlib
import subprocess

VIEWER = pathlib.Path(__file__).resolve().parents[1]


def test_cividis_lut_endpoints_and_monotonic_lightness():
    completed = subprocess.run(
        ['node', 'test/colormap_lut.mjs'],
        cwd=VIEWER,
        check=True,
        capture_output=True,
        text=True,
    )
    report = json.loads(completed.stdout)
    assert report['cividisLength'] == 256 * 3
    assert report['cividisLow'] == [0, 34, 78]
    assert report['cividisHigh'] == [254, 232, 56]
    assert report['cividisMonotonic'] is True
    assert report['rainbowLength'] == 256 * 3
    assert report['rainbowHigh'] == [255, 0, 0]
    assert report['rainbowLow'][2] > report['rainbowLow'][0]
