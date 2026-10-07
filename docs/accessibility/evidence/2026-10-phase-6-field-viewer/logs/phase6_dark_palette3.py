"""Round 3 (cheap): per-color lightness floors at 3.4:1, then pairwise repair — push the
lighter-in-original-palette member further toward white until min dE' >= 15 under
protan/deutan/tritan, same bar ColorAccessibilityTest holds CVD_SAFE_LIGHT to."""
import itertools, sys
sys.path.insert(0, '/Users/novrusshehaj/Github/UCHC/vcell/.agents')
import numpy as np
from cvd_analysis import hx, cr, dE

LIGHT = ['#000000', '#999933', '#004488', '#8C510A', '#0072B2', '#CC6677']
DARK_BGS = [hx('#12121a'), hx('#121212')]
TS = np.arange(0.0, 1.0001, 0.01)

def to_linear(rgb):
    a = np.array(rgb, float) / 255.0
    return np.where(a <= 0.04045, a / 12.92, ((a + 0.055) / 1.055) ** 2.4)
def from_linear(l):
    l = np.clip(l, 0, 1)
    return np.where(l <= 0.0031308, 12.92 * l, 1.055 * l ** (1 / 2.4) - 0.055)
def lighten(rgb, t):
    lin = to_linear(np.array(rgb, float))
    out = lin + (1.0 - lin) * t
    return tuple(int(round(v * 255)) for v in from_linear(out))
def min_de(cols, kinds=('protanomaly', 'deuteranomaly', 'tritanomaly')):
    pairs = list(itertools.combinations(range(len(cols)), 2))
    return min(((dE(cols[i], cols[j], k), i, j, k) for i, j in pairs for k in kinds))

# lightness order of the ORIGINAL palette (index sorted by linear luminance)
order = sorted(range(6), key=lambda i: sum(to_linear(hx(LIGHT[i]))))

cols = []
for c in LIGHT:
    for t in TS:
        v = lighten(hx(c), t)
        if all(cr(v, bg) >= 3.4 for bg in DARK_BGS):
            cols.append(v)
            break

de0, i0, j0, k0 = min_de(cols)
print(f"start: min dE' {de0:.1f} (pair {i0}v{j0}, {k0})")

for step in range(80):
    de, i, j, k = min_de(cols)
    if de >= 15.0:
        print(f"repaired after {step} pushes; final min dE' = {de:.1f}")
        break
    a, b = (i, j) if order.index(i) > order.index(j) else (j, i)
    # push `a` to the next clearly lighter candidate
    idx = next((n for n, t in enumerate(TS)
                if all(cr(lighten(hx(LIGHT[a]), t), bg) >= 3.0 for bg in DARK_BGS)
                and cr(lighten(hx(LIGHT[a]), t), DARK_BGS[0]) > cr(cols[a], DARK_BGS[0]) + 0.05),
               None)
    if idx is None:
        print("cannot push", a, "further; stopping with dE", de)
        break
    cols[a] = lighten(hx(LIGHT[a]), TS[idx])

print()
for i, c in enumerate(cols):
    print(f"{LIGHT[i]} -> #%02x%02x%02x  cr(#12121a)={cr(c, DARK_BGS[0]):.2f} cr(#121212)={cr(c, DARK_BGS[1]):.2f}" % c)
for k in ['normal', 'protanomaly', 'deuteranomaly', 'tritanomaly']:
    bestk = min(dE(cols[i], cols[j], k) for i, j in itertools.combinations(range(6), 2))
    print(f"  min dE' {k}: {bestk:.1f}")
ok = all(cr(c, bg) >= 3.0 for c in cols for bg in DARK_BGS)
print("all >=3:1 on both dark bgs:", ok)
