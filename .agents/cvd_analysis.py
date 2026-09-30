"""Quantitative color checks for the VCell color-blind audit (issues #1605/#1603).

- WCAG 2.1 relative luminance / contrast ratio (uses the 0.04045 threshold of the 2025 edition).
- CVD simulation: Machado, Oliveira & Fernandes (2009) via colorspacious, severity 100 (dichromat
  approximation) for protan/deutan/tritan, plus severity 50 (anomalous trichromacy).
- Perceptual distance: Euclidean distance in CAM02-UCS (Delta E', ~1 unit = JND scale used by
  viridis/cividis authors). Rule of thumb used here: < 10 = hard to tell apart as thin lines,
  < 5 = effectively the same.
- Exact port of org.vcell.util.ColorUtil.generateAutoColor incl. java.util.Random, so the colors
  measured are the ones VCell really draws.
"""
import itertools, math, sys
import numpy as np
import warnings
warnings.filterwarnings("ignore")
from colorspacious import cspace_convert

# ---------------------------------------------------------------- WCAG
def lin(c):
    c = c / 255.0
    return c / 12.92 if c <= 0.04045 else ((c + 0.055) / 1.055) ** 2.4
def lum(rgb):
    r, g, b = rgb
    return 0.2126 * lin(r) + 0.7152 * lin(g) + 0.0722 * lin(b)
def cr(a, b):
    la, lb = lum(a), lum(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)
def hx(s):
    s = s.lstrip('#')
    if len(s) == 3: s = ''.join(ch * 2 for ch in s)
    return tuple(int(s[i:i + 2], 16) for i in (0, 2, 4))

# ---------------------------------------------------------------- CVD
def sim(rgb, kind, sev=100):
    a = np.array(rgb, float) / 255.0
    if kind == 'normal':
        out = a
    elif kind == 'achroma':
        y = lum(rgb)
        v = 1.055 * y ** (1 / 2.4) - 0.055 if y > 0.0031308 else 12.92 * y
        out = np.array([v, v, v])
    else:
        out = cspace_convert(a, {"name": "sRGB1+CVD", "cvd_type": kind, "severity": sev}, "sRGB1")
    return np.clip(out, 0, 1)
def ucs(rgb01):
    return cspace_convert(rgb01, "sRGB1", "CAM02-UCS")
def dE(a, b, kind, sev=100):
    return float(np.linalg.norm(ucs(sim(a, kind, sev)) - ucs(sim(b, kind, sev))))
KINDS = [('normal', 100), ('protanomaly', 100), ('deuteranomaly', 100), ('tritanomaly', 100),
         ('protanomaly', 50), ('deuteranomaly', 50), ('achroma', 100)]
KLAB = {('normal', 100): 'normal', ('protanomaly', 100): 'protan', ('deuteranomaly', 100): 'deutan',
        ('tritanomaly', 100): 'tritan', ('protanomaly', 50): 'protan50', ('deuteranomaly', 50): 'deutan50',
        ('achroma', 100): 'achrom'}

def palette_report(name, cols, bg=(255, 255, 255), n=None, show_worst=True):
    cols = cols[:n] if n else cols
    print(f"\n### {name}  (n={len(cols)})")
    line = []
    for k in KINDS:
        best = (1e9, None)
        for i, j in itertools.combinations(range(len(cols)), 2):
            d = dE(cols[i], cols[j], *k)
            if d < best[0]: best = (d, (i, j))
        line.append(f"{KLAB[k]}={best[0]:.1f}" + (f"[{best[1][0]}v{best[1][1]}]" if show_worst else ""))
    print("  min pairwise dE(CAM02-UCS): " + "  ".join(line))
    fails = [(i, c, round(cr(c, bg), 2)) for i, c in enumerate(cols) if cr(c, bg) < 3.0]
    print(f"  vs bg {bg}: {len(cols) - len(fails)}/{len(cols)} >= 3:1; below 3:1 -> {fails}")

def count_confusable(cols, kind, sev=100, thr=10.0):
    return sum(1 for i, j in itertools.combinations(range(len(cols)), 2) if dE(cols[i], cols[j], kind, sev) < thr)

# ---------------------------------------------------------------- java.util.Random port
class JRandom:
    MULT, ADD, MASK = 0x5DEECE66D, 0xB, (1 << 48) - 1
    def __init__(self, seed): self.seed = (seed ^ self.MULT) & self.MASK
    def _next(self, bits):
        self.seed = (self.seed * self.MULT + self.ADD) & self.MASK
        r = self.seed >> (48 - bits)
        if r & (1 << 31): r -= 1 << 32
        return r
    def nextInt(self, bound):
        if bound & (-bound) == bound:
            return (bound * self._next(31)) >> 31
        while True:
            bits = self._next(31); val = bits % bound
            if bits - val + (bound - 1) < (1 << 31): return val

def calcBrightness(r, g, b): return (r * 299 + g * 587 + b * 114) // 1000
def isContrastOK(ct, bt, r, nr, g, ng, b, nb):
    contr = abs(r - nr) + abs(g - ng) + abs(b - nb)
    if contr > ct or abs(calcBrightness(r, g, b) - calcBrightness(nr, ng, nb)) > bt:
        if nr > 210 and ng > 200 and nb < 40: return False
        return True
    return False
def generateAutoColor(n, bg=(255, 255, 255), seed=0):
    rnd = JRandom(seed); colorV = None
    for ct in range(300, -1, -10):
        colorV = []; failed = False; it = 0
        while len(colorV) < n:
            it += 1
            if it > 200 * n: failed = True; break
            nr, ng, nb = rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)
            ok = isContrastOK(ct, 200, bg[0], nr, bg[1], ng, bg[2], nb) and isContrastOK(ct, 200, 0, nr, 0, ng, 0, nb)
            if ok:
                for c in colorV:
                    if not isContrastOK(ct, 200, c[0], nr, c[1], ng, c[2], nb): ok = False; break
            if ok: colorV.append((nr, ng, nb))
        if not failed: break
    s = sorted(colorV, key=lambda c: calcBrightness(*c))
    alt = [None] * len(s)
    for i in range(len(s)):
        e = len(s) - 1 - i
        if e < i: break
        alt[i * 2] = s[i]
        if e > i: alt[i * 2 + 1] = s[e]
    return alt

# ---------------------------------------------------------------- palettes from the repo
TABLEAU20 = [(214,39,40),(31,119,180),(255,127,14),(44,160,44),(148,103,189),(23,190,207),(140,86,75),(227,119,194),(127,127,127),(188,189,34),(174,199,232),(255,187,120),(152,223,138),(255,152,150),(197,176,213),(219,219,141),(196,156,148),(247,182,210),(199,199,199),(158,218,229)]
COLORBLIND20 = [(0,114,178),(213,94,0),(0,158,115),(170,51,119),(0,170,170),(153,153,153),(0,150,255),(0,170,0),(200,55,0),(102,0,153),(86,180,233),(120,190,32),(230,159,0),(204,121,167),(0,200,200),(102,102,102),(0,90,160),(40,120,40),(153,102,204),(136,34,85)]
DARK20 = [(31,119,180),(214,39,40),(44,160,44),(255,127,14),(148,103,189),(200,55,0),(140,86,75),(23,190,207),(213,94,0),(110,112,22),(127,127,127),(57,59,121),(165,35,50),(82,84,163),(107,110,207),(99,121,57),(5,130,140),(153,102,204),(140,162,82),(140,109,49),(136,34,85)]
MOLTYPE = [(255,0,0),(0,255,255),(255,0,255),(255,200,0),(255,175,175),(0,255,0),(0,0,255)]  # MolecularTypeLargeShape.colorTable
SPRINGSALAD_FIRST8 = [(255,0,0),(0,0,255),(0,255,0),(255,200,0),(0,255,255),(255,0,255),(255,175,175),(255,255,0)]
OKABE_ITO = [hx(c) for c in ['#000000','#E69F00','#56B4E9','#009E73','#F0E442','#0072B2','#D55E00','#CC79A7']]
TOL_BRIGHT = [hx(c) for c in ['#4477AA','#EE6677','#228833','#CCBB44','#66CCEE','#AA3377','#BBBBBB']]
VIEWER_SERIES = [hx(c) for c in ['#2a7','#d70','#07c','#c2c','#a33','#578','#e6b800','#0aa','#85f','#b60','#6a0','#f58']]
# candidate: Okabe-Ito hues darkened where needed so every entry is >= 3:1 on white (proposal, verified below)
CANDIDATE = [hx(c) for c in ['#000000','#0072B2','#D55E00','#009E73','#CC79A7','#8C6D00','#56B4E9','#7F3C8D']]

def contrast_table():
    W, K = (255,255,255), (0,0,0)
    rows = [
      ("Color.red text on white (console, constraint renderer)", (255,0,0), W),
      ("Color.red.darker() (178,0,0) vs Color.black (diagram edge selected vs not)", (178,0,0), K),
      ("Color.red vs Color.black (selected edge vs unselected)", (255,0,0), K),
      ("ProblematicTextFieldBorder red vs white field bg", (255,0,0), W),
      ("issue error border HSB(0,.4,1)=(255,153,153) vs white", (255,153,153), W),
      ("issue warning border Color.orange vs white", (255,200,0), W),
      ("DisplayAdapterService below-min black vs BlueRed min (0,0,128)", (0,0,0), (0,0,128)),
      ("webapp-viewer .readout em #2a7 on white", hx('#2a7'), W),
      ("webapp-viewer aria-pressed #fff on #2a7", W, hx('#2a7')),
      ("webapp-viewer .stale #fff on #d70", W, hx('#d70')),
      ("webapp-viewer .status.err #c0392b on dark #121212", hx('#c0392b'), hx('#121212')),
      ("webapp-viewer .status.err #c0392b on white", hx('#c0392b'), W),
      ("webapp-ng badge-published white on #4caf50", W, hx('#4caf50')),
      ("webapp-ng badge-archived white on #ff9800", W, hx('#ff9800')),
      ("webapp-ng badge-current white on #9e9e9e", W, hx('#9e9e9e')),
      ("webapp-ng badge-unknown #666 on #e0e0e0", hx('#666'), hx('#e0e0e0')),
      ("webapp-ng badge-public white on #2196f3", W, hx('#2196f3')),
      ("webapp-ng badge-private white on #f44336", W, hx('#f44336')),
      ("webapp-ng badge-shared white on #9c27b0", W, hx('#9c27b0')),
      ("Gemini proposed #B71C1C on white", hx('#B71C1C'), W),
      ("Gemini proposed #2e7d32 bg + white", W, hx('#2e7d32')),
      ("Gemini proposed #e65100 bg + white", W, hx('#e65100')),
      ("Gemini proposed #157347 on white", hx('#157347'), W),
      ("Gemini proposed #ff6b6b on #121212", hx('#ff6b6b'), hx('#121212')),
      ("Gemini proposed console #990000 on white", hx('#990000'), W),
      ("Gemini proposed console #7A4B00 on white", hx('#7A4B00'), W),
      ("Gemini proposed console #003399 on white", hx('#003399'), W),
      ("Gemini: brand Red #BE2D2D on white", hx('#BE2D2D'), W),
      ("Gemini: brand Red #BE2D2D on black", hx('#BE2D2D'), K),
      ("Candidate error text #A40000 on white", hx('#A40000'), W),
      ("Candidate warning text #8A4B00 on white", hx('#8A4B00'), W),
    ]
    print("## WCAG contrast ratios")
    for label, a, b in rows:
        print(f"  {cr(a,b):5.2f}:1  {label}")

def colormap_luminance():
    """BlueRed colormap from DisplayAdapterService.createBlueRedColorModel0(true): luminance monotonicity."""
    full = []
    for i in range(128, 256): full.append((0, 0, i))
    for i in range(1, 256): full.append((0, i, 255))
    for i in range(1, 256): full.append((0, 255, 255 - i))
    for i in range(1, 256): full.append((i, 255, 0))
    for i in range(1, 256): full.append((255, 255 - i, 0))
    NS = 8
    cmap = [full[i * 1147 // (255 - NS)] for i in range(256 - NS)]
    J = [float(ucs(np.array(c) / 255.0)[0]) for c in cmap]
    peak = int(np.argmax(J))
    print("\n## BlueRed colormap (248 data entries) CAM02-UCS lightness J'")
    for idx in [0, 31, 62, 93, 124, 155, 186, 217, 247]:
        print(f"  idx {idx:3d} rgb={cmap[idx]}  J'={J[idx]:.1f}")
    print(f"  peak J' at idx {peak} rgb={cmap[peak]} J'={J[peak]:.1f}; last idx J'={J[-1]:.1f} -> non-monotonic: {J[-1] < J[peak]}")
    # CVD: distance between a low-mid ('green' idx ~124) and high ('red' idx 247) value
    for k in KINDS:
        d1 = dE(cmap[93], cmap[217], *k)   # cyan-green vs orange-red
        d2 = dE(cmap[155], cmap[247], *k)  # yellow-green vs red
        print(f"  {KLAB[k]:9s} dE(idx93,idx217)={d1:5.1f}   dE(idx155,idx247)={d2:5.1f}")
    # does any pair of far-apart data values look alike under deutan?
    worst = (1e9, None)
    for i in range(0, 248, 4):
        for j in range(i + 60, 248, 4):
            d = dE(cmap[i], cmap[j], 'deuteranomaly', 100)
            if d < worst[0]: worst = (d, (i, j))
    print(f"  deutan: most-similar pair >=60 steps apart: dE={worst[0]:.1f} at idx {worst[1]} rgb {cmap[worst[1][0]]} vs {cmap[worst[1][1]]}")
    worst = (1e9, None)
    for i in range(0, 248, 4):
        for j in range(i + 60, 248, 4):
            d = dE(cmap[i], cmap[j], 'protanomaly', 100)
            if d < worst[0]: worst = (d, (i, j))
    print(f"  protan: most-similar pair >=60 steps apart: dE={worst[0]:.1f} at idx {worst[1]} rgb {cmap[worst[1][0]]} vs {cmap[worst[1][1]]}")

def contrast_model():
    cols = []
    for i in range(256):
        h = (i % 10) / 10.0; s = 1.0 - (i % 2) * 0.5; v = 1.0 - (i % 3) * 0.25
        import colorsys
        r, g, b = colorsys.hsv_to_rgb(h, s, v)
        cols.append((int(r * 255 + 0.5), int(g * 255 + 0.5), int(b * 255 + 0.5)))
    return cols

# ---------------------------------------------------------------- JUnit-portable metric
# Same metric the proposed ColorAccessibilityTest should implement in Java: Machado 2009 severity-1.0
# matrices applied in linear sRGB, then CIELAB (D65) Delta E 1976. No external library needed.
MACHADO = {
    'protan': np.array([[0.152286, 1.052583, -0.204868], [0.114503, 0.786281, 0.099216], [-0.003882, -0.048116, 1.051998]]),
    'deutan': np.array([[0.367322, 0.860646, -0.227968], [0.280085, 0.672501, 0.047413], [-0.011820, 0.042940, 0.968881]]),
    'tritan': np.array([[1.255528, -0.076749, -0.178779], [-0.078411, 0.930809, 0.147602], [0.004733, 0.691367, 0.303900]]),
}
def _tolin(c):
    c = np.array(c) / 255.0
    return np.where(c <= 0.04045, c / 12.92, ((c + 0.055) / 1.055) ** 2.4)
def _lab(lin):
    lin = np.clip(lin, 0, 1)
    X, Y, Z = np.array([[0.4124, 0.3576, 0.1805], [0.2126, 0.7152, 0.0722], [0.0193, 0.1192, 0.9505]]) @ lin
    f = lambda t: t ** (1 / 3) if t > 216 / 24389 else (24389 / 27 * t + 16) / 116
    fx, fy, fz = f(X / 0.95047), f(Y / 1.0), f(Z / 1.08883)
    return np.array([116 * fy - 16, 500 * (fx - fy), 200 * (fy - fz)])
def min_de76(P, kind):
    sim_ = (lambda c: _tolin(c)) if kind == 'normal' else (lambda c: MACHADO[kind] @ _tolin(c))
    return min(np.linalg.norm(_lab(sim_(a)) - _lab(sim_(b))) for a, b in itertools.combinations(P, 2))
PROPOSED6 = [hx(c) for c in ['#000000', '#999933', '#004488', '#8C510A', '#0072B2', '#CC6677']]
def java_threshold_check():
    print("\n## JUnit-portable metric: min CIELAB dE76 after Machado(1.0) simulation; pass = >=15 for protan/deutan/tritan AND all >=3:1 on white")
    for name, P in [('PROPOSED6', PROPOSED6), ('TABLEAU20[:8]', TABLEAU20[:8]), ('DARK20[:8]', DARK20[:8]),
                    ('COLORBLIND20[:8]', COLORBLIND20[:8]), ('autoColor n=6', generateAutoColor(6)), ('Okabe-Ito', OKABE_ITO)]:
        de = {k: min_de76(P, k) for k in ['normal', 'protan', 'deutan', 'tritan']}
        ok3 = all(cr(c, (255, 255, 255)) >= 3.0 for c in P)
        verdict = 'PASS' if ok3 and min(de['protan'], de['deutan'], de['tritan']) >= 15 else 'FAIL'
        print(f"  {name:16s} " + " ".join(f"{k}={v:5.1f}" for k, v in de.items()) + f"  all>=3:1={ok3}  -> {verdict}")


if __name__ == '__main__':
    contrast_table()
    colormap_luminance()
    print("\n## Categorical palettes: min pairwise perceptual distance under CVD (higher is better)")
    palette_report("ColorUtil.TABLEAU20 first 8 (MoleculeVisualizationPanel)", TABLEAU20, n=8)
    palette_report("ColorUtil.DARK20 first 8 (ClusterVisualizationPanel)", DARK20, n=8)
    palette_report("ColorUtil.COLORBLIND20 first 8", COLORBLIND20, n=8)
    palette_report("ColorUtil.COLORBLIND20 all 20", COLORBLIND20)
    palette_report("Okabe-Ito 8", OKABE_ITO)
    palette_report("Paul Tol bright 7", TOL_BRIGHT)
    palette_report("CANDIDATE 8 (proposed)", CANDIDATE)
    palette_report("MolecularTypeLargeShape.colorTable 7", MOLTYPE)
    palette_report("SpringSaLaD Colors first 8 (default site order)", SPRINGSALAD_FIRST8)
    palette_report("webapp-viewer SERIES_COLORS 12", VIEWER_SERIES)
    cm = contrast_model()
    palette_report("DisplayAdapterService.createContrastColorModel handles 0..7 (geometry subvolumes)", cm, n=8)
    print("\n## generateAutoColor(n, white, seed 0) -- what Plot2DPanel actually draws")
    for n in [2, 3, 4, 5, 6, 8, 10]:
        cols = generateAutoColor(n)
        print(f"  n={n}: {cols}")
        palette_report(f"autoColor n={n}", cols, show_worst=True)
        print(f"   confusable pairs (dE<10): deutan={count_confusable(cols,'deuteranomaly')} protan={count_confusable(cols,'protanomaly')} tritan={count_confusable(cols,'tritanomaly')} normal={count_confusable(cols,'normal')}")
    for n in [4, 6, 8, 10]:
        c = COLORBLIND20[:n]
        print(f"  COLORBLIND20[:{n}] confusable (dE<10): deutan={count_confusable(c,'deuteranomaly')} protan={count_confusable(c,'protanomaly')} tritan={count_confusable(c,'tritanomaly')}")
    java_threshold_check()
