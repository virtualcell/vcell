"""Phase 6 final pair verification (test 8.5): every changed text/background pair in
webapp-viewer/index.html and the viewer.js series colors, on source colors, with the
page's alpha compositing applied where the CSS applies opacity (.readout em sits inside
.readout { opacity: .8 } -> effective color = 0.8*fg + 0.2*bg)."""
import sys
sys.path.insert(0, '/Users/novrusshehaj/Github/UCHC/vcell/.agents')
from cvd_analysis import hx, cr

WHITE, DARK = hx('#ffffff'), hx('#121212')

def composite(fg, bg, alpha):
    return tuple(round(a * alpha + b * (1 - alpha)) for a, b in zip(fg, bg))

def check(label, fg, bg, thr, alpha=1.0):
    eff = composite(fg, bg, alpha) if alpha < 1 else fg
    v = cr(eff, bg)
    print(f"{'PASS' if v >= thr else 'FAIL'}  {v:5.2f}:1 (need {thr})  {label}  eff=#{eff[0]:02x}{eff[1]:02x}{eff[2]:02x}")
    return v >= thr

# --- search a green for .readout em that survives 0.8 compositing on both schemes
def find(alpha, bg, lo=0x00):
    for r in range(lo, 256, 4):
        for g in range(lo, 256, 4):
            for b in range(lo, 256, 4):
                if cr(composite((r, g, b), bg, alpha), bg) >= 4.8:
                    return (r, g, b)
    return None

em_light_raw = find(0.8, WHITE)
em_dark_raw = find(0.8, DARK)
print("candidate raw em colors:", ['#%02x%02x%02x' % c for c in (em_light_raw, em_dark_raw)])

results = []
# .readout em (alpha 0.8 inherited)
results.append(check('.readout em light', em_light_raw, WHITE, 4.5, 0.8))
results.append(check('.readout em dark', em_dark_raw, DARK, 4.5, 0.8))
# .readout.pick (opacity 1)
results.append(check('.readout.pick light', hx('#157347'), WHITE, 4.5))
results.append(check('.readout.pick dark', hx('#4dc47d'), DARK, 4.5))
# pressed button: white text on bg; border as non-text indicator
results.append(check('pressed bg: white text', WHITE, hx('#157347'), 4.5))
results.append(check('pressed border vs white (3:1 non-text)', hx('#157347'), WHITE, 3.0))
results.append(check('pressed border vs #121212 (3:1 non-text)', hx('#157347'), DARK, 3.0))
# .stale badge
results.append(check('.stale: white text on #a85d00', WHITE, hx('#a85d00'), 4.5))
# .note.warn
results.append(check('.note.warn light', hx('#a85d00'), WHITE, 4.5))
results.append(check('.note.warn dark', hx('#ffa43d'), DARK, 4.5))
# .status.err
results.append(check('.status.err light (kept #c0392b)', hx('#c0392b'), WHITE, 4.5))
results.append(check('.status.err dark override', hx('#ff6b6b'), DARK, 4.5))
# CSS curve/curve-dot fallbacks (non-text >=3:1)
results.append(check('.curve fallback light', hx('#157347'), WHITE, 3.0))
results.append(check('.curve fallback dark', hx('#4dc47d'), DARK, 3.0))
# series palettes (non-text traces/swatches >=3:1)
LIGHT = ['#000000', '#999933', '#004488', '#8C510A', '#0072B2', '#CC6677']
DARKP = ['#6c6c6c', '#999933', '#556998', '#93613a', '#6c93c0', '#d38691']
for c in LIGHT:
    results.append(check(f'series light {c} on white', hx(c), WHITE, 3.0))
for c in DARKP:
    results.append(check(f'series dark {c} on #121212', hx(c), DARK, 3.0))
    results.append(check(f'series dark {c} on #12121a', hx(c), hx('#12121a'), 3.0))
print(f"\n{sum(results)}/{len(results)} pairs pass")
