# VCell Color & Color-Blindness Accessibility — Verified Plan for #1605 / #1603

Verification of `.agents/uconn-color-blind-accessibility.md` (the "Gemini" draft). Research date:
2026-09-30, against `master` at `11b1f83d69`. Quantitative evidence is reproducible with
`.agents/cvd_analysis.py` (output in `.agents/cvd_analysis_output.txt`; needs `numpy`,
`colorspacious`; the colormap comparison below also used `matplotlib`).

Method notes, once:
- Contrast = WCAG 2.1 relative-luminance formula (2025 edition, 0.04045 threshold).
- CVD simulation = Machado, Oliveira & Fernandes 2009, severity 100 (dichromacy) and 50
  (anomalous trichromacy); achromatopsia = luminance only.
- Perceptual difference = distance in CAM02-UCS (ΔE′). Working rule: < 10 is hard to tell apart as
  thin lines; < 5 is effectively identical. These distances come from large patches, so they
  *overstate* how distinct thin 1.5 px plot lines look. That's another reason line style, not
  palette, has to carry curve identity.

---

## 1. Executive Summary

**What UConn requires.** The UConn *Digital Accessibility Policy* (approved 2026-03-04, effective
2026-03-10) covers the development and maintenance of all UConn ICT on every campus, including UConn
Health. The policy text doesn't name a technical standard. Its *Policy Procedures* adopt **WCAG 2.1
Level AA** and require it for "digital content – websites, mobile apps, and social media" by
**2027-04-26**, with new content accessible at launch. Section 508 appears only as the standard for
**procurement**. UConn's color-specific material (the Top-Ten page, the Colors training module and
the Content Guides) is guidance: 4.5:1 text contrast, "add labels or icons in addition to color",
"avoid red/green and red/black". It restates WCAG and isn't a separate binding rule.

**External standards behind it.** ADA Title II (28 CFR 35.200) and the HHS §504 rule
(45 CFR 84.84) both require WCAG 2.1 AA for *web content and mobile apps*. The current dates are
2027-04-26 (DOJ interim final rule of 2026-04-20) and 2027-05-11 (current eCFR text). Neither
technical standard names downloadable desktop software. The VCell Swing client is therefore bound
by UConn *policy* (and, per an unfetchable UConn Health page, by UConn Health's scope, which reportedly
covers "applications developed for/by UConn Health"). WCAG 2.1 AA, applied through W3C's
informative WCAG2ICT note, is the only named, measurable target. The **field viewer**
(`webapp-viewer`, an HTML page the desktop client serves and opens in a browser) is web content, and
WCAG applies to it directly.

**Was Gemini broadly correct?** On the standards, partly. On the code, it was uneven and its
priorities were wrong. Its SC quotations are accurate, and its colormap and plot-legend findings
hold up quantitatively. However:
- It **mislabels UConn training guidance as "Strict Requirement"** (red/green avoidance, link
  underlines, brand matrix). It also extends Section 508 to software "developed" by UConn, but UConn
  applies 508 only to procurement, and 508 references WCAG **2.0**, which has no 1.4.11.
- **Four of its code findings are wrong:**
  - F-07: every red-border validation path already shows a text error dialog.
  - F-08: `ConstraintPanel` is unreachable code, yet Gemini made it Phase 1.
  - F-09: job status is always shown as text next to the icon.
  - F-14: white-on-white dark mode is unsubstantiated, because the Swing client has no dark mode.
- **Its central palette recommendation fails measurement.** `ColorUtil.COLORBLIND20` is *not*
  CVD-safe: the minimum ΔE′ among its first 8 colors is 2.9 under deuteranopia, and 6 of 20
  colors are below 3:1 on white.
- Some of its "compliant" replacement colors fail too. White on `#e65100` is 3.79:1, not the claimed
  4.52:1. Several of its contrast figures are off; for example, `.status.err` on the dark background
  is 3.44:1, not 2.44:1.

**What Gemini missed (most important first).**
1. The legacy plot palette comes from `ColorUtil.generateAutoColor(n, bg, 0)`, a seeded-random
   generator that isn't CVD-aware. It often produces lines under 3:1 on white; for example,
   `(4,255,236)` measures 1.27:1.
2. Plot segments are drawn one `Line2D` at a time, so **a naïve dash-pattern fix renders solid on
   dense data**.
3. There are **two plotting frameworks** (`Plot2DPanel` and `AbstractPlotPanel`/`PlotRenderers`),
   and `Plot2DPanel` reaches 21 production panels.
4. The **reaction/rule diagrams** (the "model viewer" #1605 names) show edge selection by color
   alone. Dark red `(178,0,0)` against black is 2.89:1.
5. The **geometry viewer** maps image regions to subvolume names only by color.
6. The spatial colormap is also used by **server-side export** (`PDEOffscreenRenderer`, which throws
   on an unknown colormap ID). A client-only colormap addition would break movie/image export.
7. The field viewer is part of the desktop results viewer (enabled by default) and is the *only*
   dark-mode-aware VCell surface.
8. WCAG Understanding 1.4.11 **explicitly exempts heat maps** ("color gradients that represent a
   measurement"). VCell's image viewer already reports the numeric value and compartment name
   under the cursor, so replacing the rainbow map is a #1603 request and best practice, not a WCAG
   failure.
9. The help text contains color-only instructions ("values that have changed appear in red"),
   which falls under SC 1.3.3.

**How #1605 and #1603 relate.** #1603 is an **epic** with three children: #1604 (cross-OS
appearance and dark mode), #1605 (colorblindness) and #1606 (font sizes). Color work can close
**#1605** and satisfy the **color-palette clause of #1603** ("use color blind palettes — both for
dark and light modes"). It **cannot close #1603**, which also needs #1604 and #1606. The Swing
client has no dark mode today, so "dark mode" palette verification can only be completed once #1604
delivers one. This plan ships palettes that pass on light backgrounds and adds a test hook so dark
variants can be added and verified under #1604.

**Recommended direction.** Fix *identity by color alone* at the root rendering paths, not color by
color:
1. Per-series line style and marker in the shared plot renderers, with legends that show them.
2. A measured CVD-separable categorical palette for plots, as a new `ColorUtil` entry point.
   `generateAutoColor` stays untouched because Smoldyn input and FRAP depend on it.
3. A non-color selection cue for diagram edges.
4. Region names in the geometry viewer's hover readout.
5. An opt-in CVD-optimised perceptually uniform colormap (cividis), registered through one shared
   helper on both client and export server.
6. An audit record, checked into the repo, that closes #1605 with evidence and spawns follow-up
   issues.

**Does the plan fully address both issues?** It fully addresses #1605's *evaluation* mandate
("all views… evaluated") for the views inventoried in §5. It remediates every normative
color-dependence failure found in the model and results viewers, and files the remainder as
follow-ups. Whether that counts as "fully" resolving #1605 is a stakeholder call (see §10, Q1). For
#1603 it delivers the palette clause for light mode only. This plan does **not** claim WCAG 2.1 AA
conformance for VCell: contrast, keyboard, focus and screen-reader criteria are outside its scope.

---

## 2. Sources and Governing Accessibility Requirements

"Authority" legend: **U** = explicit UConn document · **I** = standard UConn incorporates or
references · **N** = normative WCAG success criterion · **A** = advisory/informative W3C material ·
**BP** = best practice.

| Requirement | Authority | Normative / Advisory | Applicability to VCell | Source |
|---|---|---|---|---|
| Digital Accessibility Policy: ICT must be "designed, developed, and procured to meet accessibility standards"; all campuses; interim "equally effective alternative access" if not yet compliant. Approved 2026-03-04, effective 2026-03-10. Names ADA, §504/§508 and CT policy; **does not name WCAG** | U | Binding institutional policy | Applies to VCell (UConn Health–developed ICT) | https://policy.uconn.edu/2019/08/02/digital-accessibility-policy/ |
| Policy Procedures: follows ADA Title II, §504, **WCAG 2.1 AA**, CT Universal Website Accessibility Policy. "digital content – websites, mobile apps, and social media – must comply with WCAG 2.1 AA" by **2027-04-26**; new content after 2024-04-24 accessible at launch; temporary exception requires an EEAAP. (The policy links `/ict-policy-procedures/`, which returns 404; the live page is below.) | U | Binding procedure; WCAG named for web/mobile/social | Field viewer (web content): direct. Swing client: WCAG is the only named technical benchmark (see §10 Q2) | https://accessibility.its.uconn.edu/policy-procedures/ |
| Procedures, Procurement: purchased software judged by **US Access Board 508 Standards – Software** | U → I | Binding for *procurement* only | Not binding for in-house VCell; useful benchmark | same, "Procurement" section |
| Procedures, University Websites: link to Accessibility at UConn, "typically in the footer" | U | Binding for university websites | `webapp-ng` only, **not** #1603/#1605 | same |
| Top Ten #01: 4.5:1 text/background; "Add labels or icons in addition to color". Page says it is "not a comprehensive list" | U | Guidance | All VCell text/state cues | https://accessibility.its.uconn.edu/guidelines-standards/ |
| Colors module: 4.5:1 below 18 pt, 3:1 at ≥18 pt; "Avoid using color alone"; "Avoid using Red and Black or Red and Green"; simulate color blindness. Checklist wording: "used cautiously" | U | Training guidance | Red/black selection cues, red/green palettes | https://accessibility.its.uconn.edu/self-paced-learning/colors/ |
| Content Guides: 4.5:1 baseline; "combinations… like red and green" avoided; form messages combine "color, icons, and text"; links "ideally" underlined | U | Guidance | Validation feedback, console | https://accessibility.its.uconn.edu/content-guides/ |
| UConn Health Digital Accessibility page: scope reportedly includes "applications developed for/by UConn Health"; WCAG 2.1 AA | U | **Unverified** (HTTP 403; known only from a search-engine snippet) | Would bring VCell desktop explicitly in scope | https://prod-uconn-hub.vercel.app/administrative/health-marketing-and-communications/digital-accessibility/digital-accessibility |
| Brand accessible color combinations (marketing palette, 4.5:1 body text) | U | Brand guidance | Not a software requirement | https://brand.uconn.edu/visual-identity/uconn-accessible-color-combinations/ |
| ADA Title II rule, 28 CFR 35.200: WCAG 2.1 AA for web content and mobile apps; DOJ IFR (2026-04-20) moved the ≥50k-population date to **2027-04-26** | I | Federal regulation (web/mobile) | Field viewer if public-facing; desktop not named | https://www.ada.gov/resources/2024-03-08-web-rule/ ; IFR dates from UConn Procedures and https://www.jacksonlewis.com/insights/doj-extends-public-entities-compliance-deadline-ada-related-website-accessibility-hhss-may-2026-deadline-still-looms (Federal Register text not retrieved) |
| HHS §504, 45 CFR 84.84: WCAG 2.1 AA for recipients' web content and mobile apps; current eCFR date **2027-05-11** (≥15 employees) | I (not cited by UConn) | Federal regulation (web/mobile) | Likely binds UConn Health as an institution; web/mobile only | https://www.ecfr.gov/current/title-45/part-84/section-84.84 |
| 36 CFR 1194 App. A E207.2: software UI conforms to **WCAG 2.0** A/AA; 502.2.2 don't disrupt platform accessibility features; **503.2 honor platform color/contrast preferences** | I (procurement) | Normative for federal/procured ICT | Benchmark only; 503.2 matters for #1604 | https://www.access-board.gov/ict/ |
| **SC 1.4.1 Use of Color (A)**: color is not the only visual means of conveying information, indicating an action, prompting a response, or distinguishing a visual element | N | Normative | Core of #1605 | https://www.w3.org/TR/WCAG21/#use-of-color |
| **SC 1.4.3 Contrast (Minimum) (AA)**: text 4.5:1; large text 3:1; inactive/decorative exempt | N | Normative | Status/error text colors | https://www.w3.org/TR/WCAG21/#contrast-minimum |
| **SC 1.4.11 Non-text Contrast (AA)**: UI component/state indicators and graphical objects "required to understand the content" ≥3:1 vs adjacent colors, "except when a particular presentation… is essential" | N | Normative (not in WCAG 2.0) | Plot lines vs background; selection indicators | https://www.w3.org/TR/WCAG21/#non-text-contrast |
| **SC 1.3.3 Sensory Characteristics (A)**: instructions don't rely solely on shape, **color**, size, location… | N | Normative | Help text "appear in red" | https://www.w3.org/TR/WCAG21/#sensory-characteristics |
| SC 3.3.1 Error Identification (A); SC 2.4.7 Focus Visible (AA) | N | Normative | Validation (already met, §5); focus is #1604 territory | https://www.w3.org/TR/WCAG21/#error-identification |
| Understanding 1.4.1: a lightness difference ≥3:1 counts as a non-hue cue, but "red for invalid / green for valid" still needs another indicator. Sufficient technique G111 (color **and** pattern) | A | Informative | Tests below use the 3:1 rule | https://www.w3.org/WAI/WCAG21/Understanding/use-of-color.html |
| Understanding 1.4.11: "each line in a graph" is a graphical object; not required when the info is available as text or a table; **"color gradients that represent a measurement, such as heat maps" are essential (exempt)** | A | Informative | Colormaps exempt; plot lines not | https://www.w3.org/WAI/WCAG21/Understanding/non-text-contrast.html |
| WCAG2ICT (W3C Group Note, 2024-11-15): how to read WCAG for non-web software | A | Informative | Interpreting WCAG for the Swing client | https://www.w3.org/TR/wcag2ict-22/ |
| Perceptually uniform, CVD-robust colormaps (cividis: Nuñez et al., PLOS ONE 2018; Crameri et al., *Nat. Commun.* 2020) | BP | Best practice (papers not retrieved this session) | Spatial results colormap | — |

WCAG 2.1's current edition is **W3C Recommendation 06 May 2025**. The SC text is unchanged from 2018.

---

## 3. Gemini Research Verification

| Gemini Claim | Verdict | Evidence | Correction / Addition |
|---|---|---|---|
| Policy approved 2026-03-04, effective 2026-03-10, all campuses | CONFIRMED | policy.uconn.edu page | — |
| Policy "mandat[es]… WCAG 2.1 AA" | PARTIALLY CONFIRMED | Policy text names no standard; Procedures name WCAG 2.1 AA for websites/mobile/social | Cite the Procedures, and note the web/mobile scope |
| Deadline 2027-04-26; new content after 2024-04-24 accessible at launch | CONFIRMED | Procedures | The date comes from the DOJ IFR of 2026-04-20; UConn's own Title II page still says "April 2026" (stale) |
| "8% of males, 0.5% of females" | UNSUBSTANTIATED | No source given | Context only, not a requirement |
| Core mandate: "*Every* color cue must be accompanied by a redundant non-color discriminator" | PARTIALLY CONFIRMED | Understanding 1.4.1 accepts a ≥3:1 lightness difference, and 1.4.1 applies only when color conveys information | Overgeneralized |
| 3:1 for graphical objects "chart series, plot lines, diagram nodes" | PARTIALLY CONFIRMED | 1.4.11 applies only to parts "required to understand"; text/table alternatives and essential presentations (heat maps) are exempt | Heat-map exemption was missed |
| Guidelines & Standards page "outlin[es] 3:1 and dual-coding" | PARTIALLY CONFIRMED | Page states 4.5:1 and "labels or icons"; 3:1 appears only in the Colors module | — |
| Procedures: procurement uses 508 Software standards | CONFIRMED | Procurement section | — |
| UCONN-01 "no color alone" = Strict Requirement | PARTIALLY CONFIRMED | Binding force comes from WCAG 1.4.1 through the Procedures; UConn's own text is guidance | Relabel as "WCAG 1.4.1 (normative), restated in UConn guidance" |
| UCONN-02 avoid red/green, red/black = "Strict Institutional Requirement" | INCORRECT | Module says "Avoid"; checklist says "used cautiously" | Guidance. Still relevant: VCell's red-vs-black selection cue |
| UCONN-03 4.5:1 / 3:1 = Strict Requirement | PARTIALLY CONFIRMED | Restates WCAG 1.4.3; UConn wording ("18 pt and smaller: 4.5:1") differs slightly from WCAG's large-text definition | Use the WCAG definition |
| UCONN-04 links must be underlined = Strict Requirement | INCORRECT | Content Guide says "ideally"; WCAG allows 3:1 contrast plus non-color focus/hover (G183) | Not relevant to the Swing client |
| UCONN-05 footer accessibility link | CONFIRMED | Procedures, University Websites | Applies to `webapp-ng` → out of scope (FOLLOW-UP) |
| UCONN-06 brand color matrix is an institutional standard | MISSING IMPORTANT CONTEXT | Brand/marketing page | "Red `#BE2D2D` fails on white": INCORRECT (5.83:1 passes; 3.60:1 on black fails). "Dark Grey `#7E868C` fails on black and white": PARTIALLY (3.70:1 on white fails, 5.68:1 on black passes) |
| WCAG 2.1 = "Recommendation 05 June 2018" | OUTDATED | Current edition is 06 May 2025 | SC text unchanged |
| STD-01…05 SC text | CONFIRMED | Matches the normative text | Interpretations sometimes prescribe one technique (e.g. line patterns) as if it were the only way to comply |
| STD-03 exception for "real-world microscopy" | PARTIALLY CONFIRMED | Understanding exempts photos *and* measurement heat maps | Heat maps are the relevant exemption |
| STD-05: VCell "merely paint[s] the text box border red" | INCORRECT (as applied) | `OutputOptionsPanel.java:559-575,845-859`, `MeshSpecificationPanel.java:421-429`, `StochSimOptionsPanel.java:338-346`, `TableCellEditorAutoCompletion.java:104-118` all show a text dialog before the border | Rule correct, application wrong |
| STD-06: §508 is a "Mandatory Legal Standard for software procured or developed by UConn" | INCORRECT | UConn applies 508 to procurement only; 508 E207.2 references WCAG **2.0** (no 1.4.11) | Missed §508 **503.2** (honor platform color/contrast prefs), which is the most relevant 508 clause (for #1604) |
| REC-01…04 labelled best practice | CONFIRMED | — | REC-04 (theme selector) is scope creep for #1605 |
| F-01 BlueRed is a rainbow; non-monotonic; red/green confusion | CONFIRMED | `DisplayAdapterService.java:226-261`. Measured J′ peaks at yellow (97.3) and falls to 60.0 at red; under protan, values 60 steps apart measure ΔE′ 0.2; in grayscale, 0.0 | Also used by `KymographPanel`, `PDEOffscreenRenderer`, `RasterExporter`, `IMGExporter`, `DisplayImageOp` (vmicro) |
| F-01 remedy: "Provide a UI selector in `DisplayAdapterServicePanel`" | INCORRECT (unnecessary) | `DisplayAdapterServicePanel.updateColorModelRadioButtons()` (`:1494`) already builds a radio button per registered ID | Registering the model is enough |
| F-01 (missing context) | MISSING IMPORTANT CONTEXT | 1.4.11 heat-map exemption; hover readout gives value and compartment (`ImagePlaneManagerPanel.updateInfo`, `:1437-1530`); `PDEOffscreenRenderer` registers only Gray/BlueRed and `setActiveColorModelID` throws on an unknown ID (`DisplayAdapterService.java:721-725`) | A new colormap needs a server deploy first, plus its own out-of-range colors (black and white measure ≈1.3:1 against the viridis/cividis endpoints) |
| F-02 `createContrastColorModel` hue cycling | CONFIRMED | `DisplayAdapterService.java:290-299`; deutan ΔE′ 2.0 between handles 1 and 4 | Missed consumer `GeometrySummaryPanel.java:973`. The tables pair each swatch with a name (fine); the real gap is the geometry image (§5) |
| F-02 remedy: Okabe-Ito for subvolumes | PARTIALLY CONFIRMED | Okabe-Ito min ΔE′ ≥14 under protan/deutan, but 3 entries are <3:1 on white (yellow 1.32:1) and grayscale separation is 0.8 | Name the region in text instead (MUST); palette change is a FOLLOW-UP |
| F-03 `Plot2DPanel` uses a single solid stroke, 2–3 px circles, color-only legend `LineIcon` | CONFIRMED | `Plot2DPanel.java:1342-1386`, `PlotPane.java:55-68,1349` | Missed: palette source `generateAutoColor` (`:1790`); per-segment drawing; 21 consumer panels; legend click only moves the crosshair target |
| F-04 `PlotRenderers` curves are color-only | PARTIALLY CONFIRMED | `PlotRenderers.java:80-97` | Missed mitigation: legend hover dims the other series (`MoleculeVisualizationPanel.java:368-380`) |
| F-05 `COLORBLIND20` exists and is unused | CONFIRMED | Only `TABLEAU20`/`DARK20` are referenced | — |
| F-05 `COLORBLIND20` is "accessible"/"colorblind-safe" and the fix is to switch to it | INCORRECT | First 8: deutan ΔE′ 2.9, tritan 3.3, grayscale 0.2. All 20: 6 are <3:1 on white. Near-duplicates: `(213,94,0)`/`(200,55,0)`, `(0,114,178)`/`(0,90,160)` | Use a measured palette (§6) |
| F-06 SpringSaLaD palette has red/green/lime and color-only spheres | PARTIALLY CONFIRMED | `Colors.java:18-95`; default site order has deutan ΔE′ 2.2 | Colors are model data written into SpringSaLaD input; a species legend with show/hide already exists (`SpringSaladSpeciesLegendTest`). FOLLOW-UP |
| F-07 red border with "zero feedback" | INCORRECT | See STD-05 row. Of 7 `ProblematicTextFieldBorder` references, 3 are commented out | Residual issue only: the persistent state after the dialog is a red border (low priority) |
| F-08 `ConstraintTableCellRenderer` red text; 3.998:1 | CONFIRMED (code and ratio) / MISSING IMPORTANT CONTEXT | `ConstraintPanel` has no references outside `cbit.vcell.constraints.gui`, so it's unreachable | Gemini's Phase-1 priority is INCORRECT. Delete or ignore |
| F-09 `StatusIcon` color-only; non-compliant in the filter bar | INCORRECT | Status cell text = `SchedulerStatus.getDescription()` (`SimulationJobsTableModel.java:127`); icon plus tooltip; filter icons sit next to text checkboxes (`ViewJobsPanel.java:152-158`) | Meets 1.4.1 |
| F-10 console uses `Color.RED` for Error/Warning/Stopped | CONFIRMED | `SimulationConsolePanel.java:127-143`; 4.00:1 on white | Warning (red, not bold) vs notification (black) is also the UConn "red/black" pair |
| F-11 badge contrast failures | PARTIALLY CONFIRMED | Measured: published 2.78, archived **2.16** (not 1.98), current 2.68, unknown 4.35, public **3.12**, private 3.68, shared 6.30 | Proposed `#e65100`+white is **3.79:1 (fails)**. Badges carry text, so 1.4.1 is met. Out of scope → FOLLOW-UP |
| F-12 footer lacks the UConn link | CONFIRMED | `footer.component.html` still shows the Auth0 sample text | Out of scope → FOLLOW-UP |
| F-13 field viewer rainbow LUT, series colors, CSS contrast | CONFIRMED / partly INCORRECT | `viewer.js:792,1478,2329`; `index.html:41,43,62,90`. `.status.err` on `#121212` is **3.44:1** (not 2.44) | Missed that this is a *desktop* results viewer (`PDEDataViewer` "View in 3D", default on: `PropertyLoader.java:364`) and the only dark-mode surface (`color-scheme: light dark`) |
| F-14 hard-coded white backgrounds make text white-on-white in dark mode | UNSUBSTANTIATED | Client uses Aqua/Windows/Metal L&F (`VCellLookAndFeel.java:33-36`) with no `apple.awt.application.appearance`; Swing doesn't follow macOS dark mode. Windows High Contrast is plausible but untested | Belongs to #1604 |
| F-14 macOS font shrunk by 2 pt | CONFIRMED | `VCellLookAndFeel.java:40-45` | Belongs to #1606 |
| "Physical print/grayscale export" of 2D plots | UNSUBSTANTIATED | Plot panels have no image export path | Applies to spatial image/movie export |
| CI `axe-core` gate | MISSING IMPORTANT CONTEXT | Web-only; cannot test Swing | Use the Java tests in §8 |
| Scientific section: viridis/cividis "PASS"; Okabe-Ito "universally separable" | CONFIRMED / PARTIALLY | Viridis/cividis min ΔE′ ≥16.7 across CVD types and grayscale, monotonic. Okabe-Ito fails 3:1 for 3 of 8 and grayscale | — |

---

## 4. Issue Analysis

Both issues were retrieved with `gh api`. Neither has comments, linked PRs or attachments. The only
cross-reference is commit `1ef1667926` (a backlog note). Sub-issue links were checked: #1603's
children are #1604, #1605 and #1606. Both are assigned to danv61 and CodeByDrescher.

### Issue #1605 — "VCell GUI needs to be evaluated for Colorblindness"

- **Original text (verbatim):** "We need to verify that all views, menus, windows, etc. are
  colorblind-safe. The obvious targets are model and results viewers but all areas should be
  evaluated."
- **Explicit acceptance criteria:** none. Implicitly, (a) an evaluation covering all areas and (b)
  the model and results viewers are "colorblind-safe".
- **Affected functionality:** model viewers (reaction diagram, rule-based molecule/species
  diagrams, geometry viewer) and results viewers (ODE/PDE plots, spatial image/kymograph, Langevin
  molecule/cluster plots, SpringSaLaD particle viewer, the browser field viewer).
- **Accessibility implications:** WCAG 1.4.1 (primary), 1.4.11 (plot lines and state indicators),
  1.3.3 (help text) and UConn's red/green, red/black guidance.
- **Relevant code paths:** §5 rows P1–P4, D1–D3, G1–G2, S1–S3, W1.
- **Objective acceptance criteria:** §9.

### Issue #1603 — "[EPIC] VCell GUI must adhere to new UConn Health Accessibility Guidelines"

- **Original text (verbatim):** "…ensure all our GUI components are satisfactorily displayed, and
  use color blind pallets - both for dark and light modes. This is a massive endeavor… Sub-issues
  will be added as we come across them."
- **Scope:** an epic. Closing it needs #1604, #1605 and #1606. The **color-palette clause** is the
  only part this plan owns.
- **Implicit requirements:**
  - Every *categorical* and *continuous* palette the GUI owns has a CVD-validated option (and a
    default, subject to §10 Q3).
  - Palettes must be verifiable against light **and** dark backgrounds. Dark exists today only in
    the field viewer; the Swing dark mode depends on #1604.
- **Relevant code:** `ColorUtil`, `DisplayAdapterService`, `webapp-viewer/viewer.js` LUT and
  series colors.
- **Objective acceptance criteria (palette clause):** §9.

### Shared vs. Issue-Specific Scope

- **Shared root causes.**
  - Identity is encoded by hue alone in shared renderers: `Plot2DPanel`, `PlotRenderers`,
    `EdgeShape` subclasses.
  - Palettes were chosen by aesthetics or a random generator with no CVD check.
  - There is no single registry for spatial colormaps (five-plus duplicated registration sites).
- **Shared implementation work.**
  - The categorical palette in `ColorUtil` is used for plot line colors (#1605) and serves as the
    palette deliverable (#1603).
  - The cividis colormap satisfies #1603's palette clause and improves results-viewer CVD
    readability (#1605).
- **#1605-only work.** Line styles and markers, legend icons, diagram edge selection cue, geometry
  region names, the audit record, help-text wording.
- **#1603-only work.** Dark-background palette variants and their verification (blocked on #1604
  for Swing), plus coordinating closure with #1604 and #1606.

---

## 5. VCell Code Audit Findings

All paths are relative to the repo root. "Gemini?" means whether the Gemini draft identified the
item.

| # | Path · class/method | What / why relevant | How color is used now | Gemini? | Change? |
|---|---|---|---|---|---|
| P1 | `vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java` · `drawLinePlot` `:1342`, `drawHistogram` `:1390`, `getVisiblePlotPaint` `:1778`, `pointerMoved` `:2205` | Line plots for 21 production panels (13 direct, 7 via `MultisourcePlotPane`, 1 via `TimeFunctionPanel`: ODE, PDE time, parameter estimation, BNG, FRAP, kymograph, electrical stimulus) | Hue only. 1.5 px solid stroke. Segments drawn one by one with `g.draw(line)`. Nodes are 2–3 px circles. Colors from `ColorUtil.generateAutoColor(n,bg,0)`. Crosshair status shows x,y but no series name | Yes (partly) | **MUST** |
| P2 | `vcell-client/src/main/java/cbit/plot/gui/PlotPane.java` · `LineIcon` `:55`, `updateLegend` `:1321` | Legend for P1 | 50×2 px solid swatch in the series color | Yes | **MUST** |
| P3 | `vcell-util/src/main/java/org/vcell/util/ColorUtil.java` · `generateAutoColor`, `TABLEAU20`, `COLORBLIND20`, `DARK20` | Palette source. `generateAutoColor` has 14 callers, including `SmoldynFileWriter.java:219` (solver input) and FRAP ROI panels | Seeded random with an RGB-sum heuristic; not CVD-aware. At n=6, protan ΔE′ is 3.9 and 2 lines are <3:1 on white | Partly | **MUST**: add a new method; don't modify `generateAutoColor` |
| P4 | `vcell-client/src/main/java/cbit/plot/gui/PlotRenderers.java`, `AbstractPlotPanel.java`; `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/{Molecule,Cluster}VisualizationPanel.java` (`initializeGlobalPalette` `:423`/`:206`), `AbstractVisualizationPanel.LineIcon` `:21` | Langevin results plots | `TABLEAU20` (protan ΔE′ 1.6) / `DARK20` (1.6); hue-only curves; legend hover dims other series | Yes | Palette **MUST** (#1603); line styles **SHOULD** |
| P5 | `vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java` `:80,:257` | Parameter-estimation plot and list | Own `generateAutoColor`; data drawn as points, model as lines | No | SHOULD (use the P3 palette and styles) |
| C1 | `vcell-core/src/main/java/cbit/image/DisplayAdapterService.java` · `createBlueRedColorModel0` `:226`, special colors `:269`, `addColorModel` guard `:149`, `setActiveColorModelID` `:721` | Spatial colormaps | Rainbow (see F-01 row). Below-min black vs map minimum `(0,0,128)` is 1.31:1 | Yes | **MUST** (#1603): add cividis via a shared registration helper |
| C2 | Colormap registration sites: `PDEDataContextPanel.java:1091`, `KymographPanel.java:2290`, `PDEOffscreenRenderer.java:293` (server), `RasterExporter.java:539` (server), `IMGExporter.java:94`, `vcell-vmicro/.../DisplayImageOp.java:124`, `ImagePaneScrollerTest.java:174` | Duplicated registration | Each registers Gray and BlueRed by hand; the server throws on an unknown ID | No | **MUST**: one helper `DisplayAdapterService.addStandardColorModels(das)` |
| C3 | `vcell-client/src/main/java/cbit/image/gui/DisplayAdapterServicePanel.java` · `updateColorModelRadioButtons` `:1494` | Colormap selector | Buttons built from `Hashtable` key order (unstable) | No | SHOULD: deterministic order |
| C4 | `vcell-client/src/main/java/cbit/image/gui/ImagePlaneManagerPanel.java` · `updateInfo` `:1437` | Hover readout for spatial images | Text: value, coordinates, volume/membrane names | No | None; this is the reason C1 isn't a WCAG failure |
| D1 | `vcell-core/src/main/java/cbit/vcell/graph/ReactionParticipantShape.java` · `paintSelf` `:174` | Reaction-diagram edges | Selected = red; edges of a selected species = `red.darker()` (178,0,0) vs black = **2.89:1**, below 1.4.1's lightness threshold. Protan-simulated dark red vs black ≈2.0:1 | No | **MUST** |
| D2 | `vcell-core/src/main/java/cbit/gui/graph/EdgeShape.java` `:52-63,:259`; `vcell-core/src/main/java/cbit/vcell/graph/RuleParticipantEdgeDiagramShape.java` `:138` | Generic and rule-diagram edges | `defaultFGselect = red` vs black (5.25:1 normative pass, but the UConn "avoid red/black" pair). Catalysts already dashed (#176) | No | **MUST** (same helper as D1) |
| D3 | `vcell-core/src/main/java/cbit/vcell/graph/{SpeciesContextShape,MolecularTypeLargeShape,MolecularComponent*Shape}.java` | Species/molecule glyphs | Plain species green vs rule-based blue (6.5:1 lightness difference, passes); error red vs OK yellow (3.7:1, passes); selected species gets a raised label box; molecule identity is labeled with text | No | None (document as passing) |
| G1 | `vcell-client/src/main/java/cbit/vcell/geometry/gui/GeometryViewer.java` · `setColorMap` `:607`, `refreshSourceDataInfo` `:245` + `ImagePlaneManagerPanel.updateInfo` | Geometry (model) image | Regions colored by `createContrastColorModel`. Hover shows only the handle index; the subvolume table (`GeometrySubVolumeTableCellRenderer.java:37-44`) shows name and swatch but no handle → **region→name by color only** | No | **MUST**: show the subvolume name in the hover readout |
| G2 | `GeometrySubVolumeTableCellRenderer`, `CSGObjectTreeCellRenderer:61`, `StructureMappingTableModel:433`, `StructureMappingTableRenderer:121`, `GeometrySummaryPanel:973` | Swatches next to names | Swatch plus text (passes 1.4.1); palette weak under deutan (ΔE′ 2.0) | Partly | FOLLOW-UP (palette) |
| S1 | `vcell-client/src/main/java/org/vcell/util/gui/DefaultScrollTableCellRenderer.java` `issueRenderer` `:265-347` | Shared table issue decoration | Border color plus **shape-distinct icon** (error ⊗, warning ⚠, none) plus tooltip | No | None (passes) |
| S2 | `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/MathOverridesTableCellRenderer.java:54` | Simulation-editor overrides table (core workflow) | Changed rows in `Color.red` text: 4.00:1 on white, 3.43:1 on the alternating row `#e8edff` (fails 1.4.3). Non-color cue exists (override column filled) | No | **SHOULD** |
| S3 | `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/SimulationConsolePanel.java:127-143` | Rule-based network-generation console | Error/Warning/Stopped all `Color.RED` (4.00:1); warning vs normal text differs by hue only | Yes | **SHOULD** |
| S4 | `ViewJobsPanel.java`, `StatusIcon.java` | Job status | Icon plus status text plus tooltip | Yes (wrong verdict) | None |
| S5 | `OutputOptionsPanel`, `MeshSpecificationPanel`, `StochSimOptionsPanel`, `TableCellEditorAutoCompletion` + `GuiConstants.ProblematicTextFieldBorder` | Input validation | Text dialog, then red border and refocus | Yes (wrong verdict) | None required; FOLLOW-UP (persistent inline text) |
| S6 | `vcell-client/src/main/java/cbit/vcell/constraints/gui/*` | General constraints UI | Unreachable | Yes (wrong priority) | FOLLOW-UP (delete) |
| S7 | `DefaultScrollTableCellRenderer.java:58,:101-111` | Non-editable text brown `#964B00`; spatial-process match rows yellow | Brown vs black 3.3:1 (passes, marginally); yellow vs white row 1.07:1 is **hue-only** | No | FOLLOW-UP |
| W1 | `webapp-viewer/viewer.js` (`:792` LUT, `:2329` kymograph LUT, `:1478` `SERIES_COLORS`, probe/stat plots), `webapp-viewer/index.html` (`:41,:43,:44,:53,:62,:89-91`) | Browser 3D field viewer launched from `PDEDataViewer` "View in 3D" (default on) | Rainbow LUT; probe traces are hue-only (list has labels); text `#2a7` on white 2.96:1; `.stale` white on `#d70` 3.13:1; `.status.err` on dark 3.44:1; follows OS dark mode | Yes (missed it's desktop) | **MUST** (LUT option, trace styles, text contrast) |
| X1 | `vcell-core/src/main/java/org/vcell/util/springsalad/Colors.java`; `SpringSaladViewerCanvas.java` | SpringSaLaD particles | Colors persisted per site and written to SpringSaLaD files; species legend with toggles exists | Yes | FOLLOW-UP |
| X2 | `vcell-client/src/main/java/cbit/vcell/geometry/gui/OverlayEditorPanelJAI.java:95` `CONTRAST_COLORS` | Image-geometry ROI painting | Red, green, blue… ROI identity via swatch list | No | FOLLOW-UP |
| X3 | `vcell-client/src/main/java/cbit/vcell/microscopy/gui/estparamwizard/AnalysisTableRenderer.java:85-121` | FRAP analysis: non-identifiable results | Text "NOT IDENTIFIABLE" (1.4.1 passes) plus red text on `(255,170,170)` = 2.21:1 (1.4.3 fails). The "significant in green" code comment is stale; no green is drawn | No | FOLLOW-UP ([#2134](https://github.com/virtualcell/vcell/issues/2134)) |
| X4 | `webapp-ng/.../publication-edit.component.css`, `footer.component.html` | Public website | Badge contrast; missing UConn link | Yes | FOLLOW-UP (separate issue) |
| X5 | `vcell-client/src/main/java/cbit/vcell/client/VCellLookAndFeel.java` | L&F, fonts | No dark mode; macOS fonts −2 pt | Yes | #1604 / #1606 |
| H1 | `vcell-client/UserDocumentation/originalXML/topics/...`: `simulationEditor.xml:27` ("changed appear in red"), `simulations.xml:55`, `PP_Species.xml:23`, `PP_ReactionRulesEditor.xml:14`, `PathwayDiagramView.xml:45`, `PathLink.xml:24`, `SimResultsDataRange.xml:28` | In-app help | Color-only instructions (SC 1.3.3) | No | **SHOULD** (MUST only for the pages whose UI this plan changes) |

---

## 6. Accessibility Gap Matrix

| Requirement | VCell Component | Current Behavior | Accessibility Gap | Required Fix | Issue |
|---|---|---|---|---|---|
| WCAG 1.4.1 (N) | P1/P2 legacy plots | Curves differ only by hue; legend swatch is color only | Curve↔legend mapping fails for CVD users and in grayscale; nothing replaces hue | Per-series dash pattern (continuous `Path2D`), marker shape when nodes are shown, legend icon draws both | #1605 |
| WCAG 1.4.11 (N) | P1/P3 | Auto colors often <3:1 on white (e.g. 1.27:1) | Low-vision users can't see some lines. (The Data table view is an alternative for values, but not for reading the graph) | Palette with every entry ≥3:1 on white | #1605/#1603 |
| #1603 "use color blind palettes" (issue req.) + UConn guidance (U) | P3/P4 | TABLEAU20/DARK20/random | Measured protan/deutan ΔE′ 1.6–7.5 | Validated palette `ColorUtil.CVD_SAFE_LIGHT` (§7 task 1) | #1603 |
| WCAG 1.4.1 (N) + UConn red/black (U) | D1/D2 diagram edges | Selection = color change only; dark red vs black 2.89:1 | Selected/related edges can't be distinguished without hue | Thicker stroke for selected (and selected-neighbor) edges | #1605 |
| WCAG 1.4.1 (N) | G1 geometry viewer | Region→subvolume only via swatch color | Compartment identity can't be read without hue | Hover readout appends the subvolume name (and handle) | #1605 |
| #1603 palette clause (issue req.) + BP; **not** a WCAG failure (1.4.11 heat-map exemption; C4 provides text) | C1/C2 spatial colormap | Rainbow only; server registers only Gray/BlueRed | Non-monotonic; protan ΔE′ 0.2 between distinct values | Add "Cividis" through a shared helper on client **and** server, with its own special colors | #1603 (#1605 benefit) |
| WCAG 1.4.1 / 1.4.3 / 1.4.11 (N, web content) | W1 field viewer | Hue-only probe traces; three text colors below 4.5:1; rainbow LUT | As above, directly under WCAG | `stroke-dasharray` per probe, legend dash sample; fix three CSS colors; LUT option matching desktop | #1605/#1603 |
| WCAG 1.4.3 (N) | S2 overrides table | Red text 3.43–4.00:1 | Text contrast | Accessible dark red constant | #1605 (SHOULD) |
| WCAG 1.4.3 (N) + UConn red/black (U) | S3 console | Red for 3 severities | Contrast; warning vs normal by hue | `[Error]`/`[Warning]`/`[Stopped]` prefixes plus compliant colors | #1605 (SHOULD) |
| WCAG 1.3.3 (N) | H1 help pages | "appear in red", "sites in green" | Instructions rely on color | Reword to name the non-color cue | #1605 (SHOULD) |
| Best practice | P1 crosshair | Status shows x,y only | Identity needs the legend | Prefix the status with the series name | #1605 (SHOULD) |
| Best practice / scientific integrity | P1 styles | New dashes might hide narrow spikes | Interpretability risk | Plot settings "Vary line styles" toggle (default on); series 0 solid | #1605 (SHOULD) |

---

## 7. Final Recommended Implementation Plan

Colors referenced below were measured with `.agents/cvd_analysis.py`.

**Palette `CVD_SAFE_LIGHT`, in greedy maximum-separation order:** `#000000`, `#999933`, `#004488`,
`#8C510A`, `#0072B2`, `#CC6677`.
- Contrast on white: 21.0, 3.02, 9.62, 6.37, 5.19 and 3.66:1.
- Min ΔE′ (CAM02-UCS): 21.0 normal, 17.5 protan, 16.3 deutan, 18.9 tritan.
- Min CIELAB ΔE76 after Machado simulation: ≥16.3.
- Exhaustive search over 24 established CVD-oriented colors shows the best *6*-color set scores
  15.5 and the best *8*-color set only 11.9. So cycle 6 colors × 4 dash patterns (24 unique styles)
  rather than stretching the palette.
- Palette entries are indistinguishable in grayscale by design; the dash pattern carries identity.

### Phase 0 — Audit record (MUST, #1605) — ✅ COMPLETE (2026-09-30)

**0.1** `docs/accessibility/color-audit.md` (new) — ✅ done
- **Change:** check in the §5 inventory: one row per view with its verdict and evidence, and a
  "PASS by non-color cue" list (S1, S4, S5, D3, C4). Link it from #1605.
- **Rationale:** #1605 literally asks for an evaluation; the audit is the evidence that closes it.
- **Dependency:** none.
- **Risk:** none.
- **Verify:** the file exists and every §5 row appears in it.
- **Result:**
  - Committed on branch `docs/1605-color-audit`, based on `origin/master` `5407a5548c` (commits
    `ca98b3cbde`, `d41affa75f`), and opened as **PR [#2141](https://github.com/virtualcell/vcell/pull/2141)**.
    It was kept off `chore/vcell#1605`, whose local commit `11b1f83d69` (only
    `.github/copilot-instructions.md`, with a SlurmProxy message) is unrelated.
  - Linked from #1605:
    [comment](https://github.com/virtualcell/vcell/issues/1605#issuecomment-5917394255).
  - Beyond the §5 highlights, the record adds a sweep of **all 77 production files** that set a
    chromatic color, each one dispositioned. That sweep found D4, S8 and C3 (below), and corrected
    the plot-consumer count and the X3 row.
  - **Remaining:** PR #2141 needs one approving review and the merge queue (the ruleset requires
    it). The "merged" item in §9 stays open until then.
- **Row mapping (verifies "every §5 row appears").** The audit record uses its own stable IDs:

  | Plan §5 | Audit record | Note |
  |---|---|---|
  | P1, P3 | P1 | Palette source (`generateAutoColor`) folded into the plot row |
  | P2, P4 | P2, P4 | — |
  | P5 | P3 | `MultisourcePlotPane` |
  | C1, C2, C3 | C1 | Registration sites and radio-button order are implementation notes inside C1's remediation |
  | C4 | C2 | Hover readout (PASS) |
  | — | C3 | **New:** below-minimum black vs map minimum, 1.31:1 |
  | D1–D3, G1, G2, S1–S7, W1, H1, X1–X5 | same IDs | — |
  | — | D4 | **New:** rule-based glyph error outline is hue-only, 1.43:1 |
  | — | S8 | **New:** other red status text below 4.5:1 (6 renderers/panels) |

**0.2** GitHub follow-up issues — ✅ done
- **Planned:** file follow-up issues X1–X5, S5–S7, G2 and the `ConstraintPanel` removal. Label
  them `Accessibility Requirement` and make them sub-issues of #1603.
- **Verify:** `gh api repos/virtualcell/vcell/issues/1603/sub_issues` lists them.
- **Result:** all filed with `Accessibility Requirement` (plus `User Interface` / `Java-based`
  where they apply), and all linked as sub-issues of #1603. The `sub_issues` API now returns
  #1604, #1605, #1606 and #2132–#2140.

  | Audit row | Issue |
  |---|---|
  | X1 SpringSaLaD site colors | [#2132](https://github.com/virtualcell/vcell/issues/2132) (references #2072) |
  | X2 image-geometry ROI editor | [#2133](https://github.com/virtualcell/vcell/issues/2133) |
  | X3 FRAP analysis table | [#2134](https://github.com/virtualcell/vcell/issues/2134) |
  | X4 webapp-ng badges and UConn footer link | [#2135](https://github.com/virtualcell/vcell/issues/2135) |
  | S5 persistent validation message | [#2136](https://github.com/virtualcell/vcell/issues/2136) |
  | S6 = `ConstraintPanel` removal (one issue; the plan listed it twice) | [#2137](https://github.com/virtualcell/vcell/issues/2137) |
  | S7 yellow match-row highlight | [#2138](https://github.com/virtualcell/vcell/issues/2138) |
  | G2 geometry subvolume/CSG palette | [#2139](https://github.com/virtualcell/vcell/issues/2139) |
  | D4 + S8 (new findings) | [#2140](https://github.com/virtualcell/vcell/issues/2140) |
  | X5 look and feel | **Not re-filed.** Already owned by #1604 and #1606, which are sub-issues of #1603; evidence posted as comments on [#1604](https://github.com/virtualcell/vcell/issues/1604#issuecomment-5917385633) and [#1606](https://github.com/virtualcell/vcell/issues/1606#issuecomment-5917386076) |

### Phase 1 — Shared palette and style primitives (MUST, #1603 and #1605)

**1.1** `vcell-util/src/main/java/org/vcell/util/ColorUtil.java`
- **Change:**
  - Add `public static final Color[] CVD_SAFE_LIGHT` (above).
  - Add `public static Color seriesColor(int i)` (cycles the palette).
  - Add `public static float[] seriesDash(int i)` returning, per `i / 6`: `null` (solid), `{6,3}`,
    `{2,2}`, `{8,3,2,3}`.
  - Leave `generateAutoColor`, `TABLEAU20`, `DARK20` and `COLORBLIND20` **unchanged**:
    `SmoldynFileWriter` and the FRAP/vmicro ROI panels depend on their exact output. Add Javadoc
    on `COLORBLIND20` stating it failed CVD validation.
- **Rationale:** one measured palette and style sequence shared by both plot frameworks and the
  field viewer (mirrored in JS).
- **Requirement:** 1.4.1, 1.4.11, #1603.
- **Risk:** low (additive).
- **Verify:** unit test 8.2-a.

**1.2** `vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java` (new, `@Tag("Fast")`)
- **Change:** implement WCAG luminance/contrast, the Machado 2009 severity-1.0 matrices (protan,
  deutan, tritan) and CIELAB ΔE76 (≈60 lines, no dependencies).
- **Assert:**
  - Every `CVD_SAFE_LIGHT` entry is ≥3.0:1 vs white.
  - Pairwise min ΔE76 is ≥15 under each simulation.
  - `seriesColor`/`seriesDash` pairs are unique for i < 24.
  - A reference assertion that `TABLEAU20[:8]` *fails*, proving the test discriminates.
- **Hook for #1603 dark mode:** a disabled test `cvdSafeDarkPaletteMeetsContrastOnDarkBackground()`
  that #1604 enables when a dark background constant exists.
- **Verify:** `mvn test -pl vcell-util -Dgroups=Fast`.

### Phase 2 — Legacy plot framework (MUST, #1605)

**2.1** `vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java`
- **`getVisiblePlotPaint` (`:1778`):** when `userDefinedColors` is null and `getAutoColor()` is
  true, return `ColorUtil.seriesColor(visiblePlotIndex)` instead of `generateAutoColor(...)`.
  `userDefinedColors` still wins.
- **New `getVisiblePlotStroke(int)`:** returns a `BasicStroke(1.5f, CAP_BUTT, JOIN_ROUND, 10f,
  seriesDash(i), 0f)`, or the current solid stroke when "vary line styles" is off.
- **`drawLinePlot` (`:1342`):** build **one `Path2D.Double` per curve** from the mapped points
  (the clip is already set by `g.setClip(plotRectHolder)`), then `g.draw(path)` once. That keeps
  the dash phase continuous. Keep per-segment `intersects` culling only as a performance guard, so
  the path isn't split.
- **Nodes:** when shown, draw marker shape `i % 5` (circle, square, triangle, diamond, cross) at
  ≥6 px, so the shape is legible.
- **`drawHistogram` (`:1390`):** outline bars with the series stroke.
- **Rationale:** 1.4.1 sufficient technique G111 (color **and** pattern).
- **Risk:**
  - Visual change in 21 panels.
  - Performance with 10⁵-point stochastic runs (one `Path2D` is typically faster than many
    `draw` calls; measure it).
  - Dashes can mask narrow spikes (mitigated by 2.3 and series 0 staying solid).
- **Verify:** tests 8.3-a/b, manual QA 8.7.

**2.2** `vcell-client/src/main/java/cbit/plot/gui/PlotPane.java` · `LineIcon` (`:55`) and
`updateLegend` (`:1349`)
- **Change:** `LineIcon(Paint, Stroke, int markerIndex)` draws the dashed sample and a centred
  marker (icon ≥ 50×12 px). `updateLegend` passes
  `getPlot2DPanel1().getVisiblePlotStroke(i)`.
- **Verify:** test 8.3-c.

**2.3** `vcell-client/src/main/java/cbit/plot/gui/Plot2DSettingsPanel.java` (+ `Plot2DSettings` bean)
(SHOULD)
- **Change:** add a "Vary line styles" checkbox, default **on**, beside the existing
  crosshair/nodes/snap checkboxes.
- **Rationale:** lets a scientist inspect undashed curves without losing the accessible default.

**2.4** `Plot2DPanel.pointerMoved` (`:2205`) (SHOULD)
- **Change:** prefix the status text with the current series name.
- **Verify:** test 8.3-d.

**2.5** `vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java` (`:80`,
list renderer `:257`) (SHOULD)
- **Change:** replace `generateAutoColor` with `ColorUtil.seriesColor`, and show the style icon in
  the list, so its swatches stay in sync with 2.1.

### Phase 3 — Langevin plot framework (MUST for the palette, SHOULD for styles)

**3.1** `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/MoleculeVisualizationPanel.java`
(`:423`) and `ClusterVisualizationPanel.java` (`:206`)
- **Change:** `globalPalette` ← `ColorUtil.CVD_SAFE_LIGHT` (with style index = palette slot / 6).
  Keep `deriveEnvelopeColor` for SD bands.

**3.2** `vcell-client/src/main/java/cbit/plot/gui/PlotRenderers.java` (`AvgRenderer`, `:90,:96`)
and `AbstractVisualizationPanel.LineIcon` (`:21`) (SHOULD)
- **Change:** apply `seriesDash`, and draw the dash in the legend icon. Band and bubble renderers
  stay as they are (they carry legend hover-dim).
- **Risk:** low.
- **Verify:** test 8.3-e.

### Phase 4 — Model viewer (MUST, #1605)

**4.1** `vcell-core/src/main/java/cbit/gui/graph/EdgeShape.java`
- **Change:** add `protected static final BasicStroke SELECTED_STROKE = new BasicStroke(2.5f)` and
  `SELECTED_DASHED_STROKE` (2.5 px, same dash). In `paintSelf` (`:259`), use them when
  `isSelected()`.

**4.2** `vcell-core/src/main/java/cbit/vcell/graph/ReactionParticipantShape.java` · `paintSelf`
(`:174`)
- **Change:** use the 2.5 px stroke when `isSelected()` **or** `startShape.isSelected()`, keeping
  the dashed variant for catalysts. Keep the red hues; width becomes the non-color cue.

**4.3** `vcell-core/src/main/java/cbit/vcell/graph/RuleParticipantEdgeDiagramShape.java` ·
`paintSelf` (`:138`)
- **Change:** same as 4.2.
- **Rationale for 4.1–4.3:** 1.4.1 (dark red vs black is 2.89:1) and UConn red/black guidance.
- **Risk:** edge hit-testing is unaffected (it uses the curve geometry, not the stroke); verify
  arrowheads still align.
- **Verify:** test 8.3-f, manual QA 8.7.

**4.4** `vcell-client/src/main/java/cbit/vcell/geometry/gui/GeometryViewer.java` +
`vcell-client/src/main/java/cbit/image/gui/ImagePlaneManagerPanel.java`
- **Change:** give `ImagePlaneManagerPanel` an optional `IntFunction<String> indexLabelProvider`.
  `GeometryViewer.refreshSourceDataInfo` (`:245`) sets it to
  `handle -> subVolumeForHandle(handle).getName()`. `updateInfo` (`:1437`) appends
  `" \"" + name + "\""` for `SourceDataInfo.INDEX_TYPE` data.
- **Rationale:** 1.4.1: compartment identity is readable without hue. This mirrors how the results
  viewer already names compartments.
- **Risk:** low (additive text).
- **Verify:** test 8.3-g.

### Phase 5 — Spatial colormap (MUST for the #1603 palette clause)

**5.1** `vcell-core/src/main/java/cbit/image/DisplayAdapterService.java`
- **Change:**
  - Add `public static final String CIVIDIS = "Cividis"` and `createCividisColorModel()`: 248 data
    entries from the published cividis table (Nuñez et al. 2018 / matplotlib `_cividis_data`,
    BSD-compatible — confirm the license note), plus `NUM_SPECIAL_COLORS` reserved slots.
  - Add `createCividisSpecialColors()`. Out-of-range colors need to be distinguishable from both
    endpoints; the §10 Q4 proposal must pass test 8.2-c.
  - Extend the `addColorModel` guard (`:149`) to `CIVIDIS`.
  - Add `public static void addStandardColorModels(DisplayAdapterService das)` registering Gray,
    BlueRed and Cividis **in that order**.
  - Make `fetchColorModelIDs` (`:353`) return registration order (switch `colorModels` to a
    `LinkedHashMap`, or sort by a fixed order).
- **Verify:** tests 8.2-b/c.

**5.2** Replace the hand registration with `addStandardColorModels`:
- `vcell-client/.../simdata/gui/PDEDataContextPanel.java:1091`
- `vcell-client/.../client/data/KymographPanel.java:2290` (keep its Gray/BlueRed toggle at `:2521`)
- `vcell-core/.../export/server/PDEOffscreenRenderer.java:293`
- `vcell-core/.../export/server/RasterExporter.java:539` (also fix the pre-existing Gray↔BlueRed
  special-color swap there)
- `vcell-vmicro/.../op/display/DisplayImageOp.java:124`

Also update `IMGExporter`, and `MediaSettingsPanel.java:518`'s "click 'Gray' or 'BlueRed'" text.

**5.3** Export compatibility guard: in `ExportSpecs.setupDisplayAdapterService` (`vcell-core`,
`:190`), if the requested `colorMode` isn't registered, log a warning and fall back to `BLUERED`
instead of throwing.
- **Why:** it protects a new client talking to an old export server.
- **Deploy order:** export server (`vcell-server`/data service) **before** a client that exposes
  Cividis. Coordinate with the `deploy` skill; see §10 Q5.

**5.4** Default colormap
- **Change:** keep **BlueRed as the default** (`PDEDataContextPanel.java:1099`,
  `KymographPanel.java:2292`) until the stakeholder decision in §10 Q3.
- **Rationale:** changing the default changes every user's figures and movies.

**5.5** `webapp-viewer/viewer.js` (`:792`, `:2329`)
- **Change:** add a colormap selector (Rainbow | Cividis), backed by a 256-entry cividis table
  shared by the vtkLookupTable (`setTable` with explicit RGBA) and `KYMO_LUT`, so the 3D view and
  the kymograph stay in agreement.
- **Verify:** extend `webapp-viewer/test` with a LUT-endpoint and monotonic-lightness test.

### Phase 6 — Field viewer text and traces (MUST, web content)

**6.1** `webapp-viewer/viewer.js` (`:1478`)
- **Change:** `SERIES_COLORS` ← the six `CVD_SAFE_LIGHT` colors. On dark `color-scheme`, use a dark
  variant verified at ≥3:1 on `#12121a`/`#121212` (compute with the script; §10 Q6).
- **Change:** probe and stats traces get `stroke-dasharray` from the same dash sequence, and list
  swatches become short SVG line samples.

**6.2** `webapp-viewer/index.html`
- **Change:** replace `#2a7` text/pressed background with a color ≥4.5:1 (for example `#157347`,
  5.87:1 on white). `.stale` background ≥4.5:1 with white text. `.status.err` gets a dark-scheme
  override ≥4.5:1 (for example `#ff6b6b`, 6.75:1 on `#121212`), using `@media
  (prefers-color-scheme: dark)`.
- **Verify:** test 8.5 (computed on source colors).

### Phase 7 — Text contrast and wording (SHOULD, #1605)

**7.1** `vcell-core/src/main/java/cbit/vcell/client/constants/GuiConstants.java`
- **Change:** add `ERROR_TEXT_COLOR = new Color(0xA4,0,0)` (8.14:1 on white, 6.98:1 on the
  alternating row `#e8edff`, 7.81:1 on the hover row `#FDFCDC`) and
  `WARNING_TEXT_COLOR = new Color(0x8A,0x4B,0)` (6.80, 5.83 and 6.52:1).
- **Change:** use them in `MathOverridesTableCellRenderer.java:54` and
  `SimulationConsolePanel.java:127-143`. The console also prepends `[Error] `, `[Warning] ` and
  `[Stopped] `.
- **Verify:** tests 8.2-d, 8.3-h.

**7.2** Help text (`vcell-client/UserDocumentation/originalXML/...`, H1 list)
- **Change:** name the non-color cue. For example, "changed values appear in red **and have an
  entry in the Override column**"; "Blue-Red, Gray or Cividis color map". Rebuild the help per the
  repo's doc tooling.
- **Verify:** `rg -n -i "appear(s)? in red|in green|in red" vcell-client/UserDocumentation` returns
  only reworded lines.

---

## 8. Testing and Validation Plan

### Automated Tests

| ID | Test | PASS | FAIL |
|---|---|---|---|
| 8.1 | `mvn test -Dgroups=Fast -pl vcell-util,vcell-core,vcell-client -am` | No new failures vs `master` baseline | Any new failure |

### Unit Tests

| ID | Test (new) | PASS | FAIL |
|---|---|---|---|
| 8.2-a | `ColorAccessibilityTest` (vcell-util) | All `CVD_SAFE_LIGHT` ≥3.0:1 vs white; min Machado-simulated CIELAB ΔE76 ≥15 for protan/deutan/tritan; 24 unique (color,dash) pairs; `TABLEAU20[:8]` reference case fails the metric | Any assertion fails |
| 8.2-b | `DisplayAdapterServiceColormapTest` (vcell-core) | `addStandardColorModels` registers exactly Gray, BlueRed, Cividis in order; Cividis CAM-free proxy check: CIELAB L\* strictly non-decreasing over the 248 entries; `setActiveColorModelID("Cividis")` works on a fresh DAS | Order unstable, L\* decreases, or throws |
| 8.2-c | same | Each Cividis special color (below/above/NaN/not-in-domain) has ≥3:1 contrast vs **both** map endpoints, or the decision in §10 Q4 is recorded as an explicit waiver in the test | Special color within 3:1 of an endpoint with no waiver |
| 8.2-d | `GuiConstantsContrastTest` | `ERROR_TEXT_COLOR` and `WARNING_TEXT_COLOR` ≥4.5:1 vs white and vs `#e8edff` | < 4.5:1 |
| 8.2-e | `ExportSpecsColorModeFallbackTest` | Unknown `colorMode` → BlueRed, with no exception | Exception |

### Integration / UI Tests (headless `BufferedImage` rendering, `@Tag("Fast")`)

| ID | Test | PASS | FAIL |
|---|---|---|---|
| 8.3-a | Render `Plot2DPanel` (400×300) with 6 curves | Series 0 pixels form a continuous run; series 1 line shows ≥1 background gap every ≤12 px along its length | Series 1 has no gaps |
| 8.3-b | Same with 5,000 points/curve (dense stochastic) | Series 1 still shows periodic gaps (proves the continuous `Path2D` dash phase) | Solid rendering (the per-segment regression) |
| 8.3-c | `PlotPane` legend | Icon *i* stroke equals `getVisiblePlotStroke(i)`; icon height ≥12 | Mismatch |
| 8.3-d | `pointerMoved` synthetic event | Status text starts with the series name | Missing name |
| 8.3-e | `AvgRenderer` two series | Stroke dash arrays differ | Identical |
| 8.3-f | `ReactionParticipantShape` selected vs unselected | Selected stroke width ≥2.5, unselected 1.0; also when only the start species is selected | Width unchanged |
| 8.3-g | `ImagePlaneManagerPanel.updateInfo` with an index provider | Info label contains the subvolume name for a pixel of handle *h* | Absent |
| 8.3-h | `SimulationConsolePanel.appendToConsole` | Error/Warning/Stopped lines begin with the tag; foreground equals the constants | Missing tag |

### Static Analysis / Repository Checks

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.4-a | `rg -n "ColorUtil\.(TABLEAU20\|DARK20)" --type java` | No matches in `*VisualizationPanel` | Any match |
| 8.4-b | `rg -n "generateAutoColor" vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java` | No matches | Any match |
| 8.4-c | `rg -n "createBlueRedColorModel\(\), *DisplayAdapterService\.createBlueRedSpecialColors" --type java` | Only inside `addStandardColorModels` | Any other hand-registration site |
| 8.4-d | `rg -n "setForeground\((java\.awt\.)?Color\.(red\|RED)\)" vcell-client/src/main/java/cbit/vcell/solver/ode/gui/MathOverridesTableCellRenderer.java vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/SimulationConsolePanel.java` | No matches | Any match |

### Contrast Verification

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.5 | Re-run `.agents/cvd_analysis.py` (extended with the final hex values) for every changed text/background pair in `webapp-viewer/index.html` and the Java constants | Text ≥4.5:1 (≥3:1 only for ≥18 pt / 14 pt bold); non-text indicators ≥3:1 | Below threshold |

Contrast tooling alone doesn't validate CVD accessibility: 1.4.1 is checked by 8.3 and 8.6.

### Color-Vision-Deficiency Verification

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.6-a | Automated: 8.2-a metric on the final palettes (light, plus dark once #1604 lands) | Thresholds met | Not met |
| 8.6-b | Screenshot simulation: capture via `tools/debug-bridge` (ODE plot with 6+ species, PDE time plot, reaction diagram with a species selected, geometry viewer, spatial viewer with Cividis, field viewer with 3 probes); apply Machado protan/deutan/tritan and grayscale filters (extend the script with Pillow) | In every filtered image, a reviewer can match each legend entry to its curve by style, identify the selected edges by width, and name a geometry region from the readout | Any mapping needs hue |

### Manual Visual QA

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.7-a | Two reviewers (one ideally with CVD, otherwise using Sim Daltonism / Color Oracle) run a scripted checklist on macOS, Windows and Linux | Checklist items all "yes"; screenshots attached to the PR | Any "no" |
| 8.7-b | Windows High Contrast smoke test of the changed panels (informational, feeds #1604) | Logged | — |

### Scientific Visualization Regression Testing

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.8-a | Default BlueRed output: render a fixed `SourceDataInfo` through `DisplayAdapterService` before and after | Identical pixel arrays (no default change) | Any diff |
| 8.8-b | Export server: produce a GIF/PNG export with BlueRed before and after; then with Cividis after the server deploy | BlueRed identical; Cividis succeeds; old-server + Cividis falls back without error | Diff or exception |
| 8.8-c | Plot data fidelity: with "Vary line styles" off, `Plot2DPanel` output equals the `master` rendering of the same data aside from color | Only color channels differ | Geometry differs |
| 8.8-d | `SmoldynFileWriter` golden output (existing tests / MathGen regression) | Unchanged | Changed (means `generateAutoColor` was touched) |
| 8.8-e | Narrow-spike check: a 1-sample spike on a dashed series is visible with styles on, or with the toggle off | Visible in at least one mode, and the toggle documented | Spike invisible in both |

---

## 9. Definition of Done

### #1605 Definition of Done

- [ ] `docs/accessibility/color-audit.md` is merged and lists every view in §5 with a verdict and
      evidence reference. *(Written, and linked from #1605; awaiting review in PR #2141.)*
- [x] Every audit item not fixed in this work has an open issue labelled `Accessibility Requirement`
      and linked as a sub-issue of #1603. *(#2132–#2140; X5 is owned by #1604 and #1606.)*
- [ ] Tests 8.3-a and 8.3-b pass: in `Plot2DPanel`, series *i* ≥ 1 renders with a non-solid dash
      pattern, including at 5,000 points per curve.
- [ ] Test 8.3-c passes: each `PlotPane` legend icon renders the same stroke (and marker, when nodes
      are shown) as its curve.
- [ ] Test 8.2-a passes for the palette used by `Plot2DPanel` and the Langevin panels; 8.4-a and
      8.4-b return no matches.
- [ ] Test 8.3-f passes: a selected reaction/rule edge, and an edge attached to a selected species,
      has stroke width ≥2.5 px.
- [ ] Test 8.3-g passes: hovering a geometry-viewer region shows its subvolume name.
- [ ] Field viewer: probe traces have distinct `stroke-dasharray` values, and the changed
      `index.html` text colors pass 8.5.
- [ ] 8.6-b screenshots (normal, protan, deutan, tritan, grayscale) for the six scenarios are
      attached to the PR, and each passes.
- [ ] 8.8-a, 8.8-c and 8.8-d pass (no scientific regressions).
- [ ] `mvn test -Dgroups=Fast` shows no new failures, and the regression gate passes in the merge
      queue.

### #1603 Definition of Done (color-palette clause only; the epic closes with #1604 and #1606)

- [ ] `ColorUtil.CVD_SAFE_LIGHT` exists and passes 8.2-a (≥3:1 on white; ΔE76 ≥15 under protan,
      deutan and tritan simulation).
- [ ] Both desktop plot frameworks and the field viewer draw categorical series from it
      (8.4-a/b plus a code review of `viewer.js`).
- [ ] "Cividis" is selectable in the desktop spatial viewer, the kymograph and the field viewer.
      Server export with Cividis succeeds, and an old server falls back (8.2-b, 8.2-e, 8.8-b).
- [ ] Cividis special colors pass 8.2-c, or a recorded waiver is signed off.
- [ ] Dark background: the field viewer's dark-scheme series colors and text pass 8.5 against
      `#121212`/`#12121a`. For the Swing client, a `@Disabled` dark-palette test exists and is
      referenced from #1604.
- [ ] The default-colormap decision (§10 Q3) is recorded in #1603.
- [ ] #1603 stays open until #1604 and #1606 are closed.

---

## 10. Risks and Open Questions

| # | Question | Why unresolved | Blocks? | Resolved by |
|---|---|---|---|---|
| Q1 | Does "evaluate all views" plus fixing model/results-viewer failures close #1605, or must every follow-up (SpringSaLaD, ROI editor, FRAP) be fixed first? | Issue has no acceptance criteria | Closure only | Issue owners (CodeByDrescher, danv61) |
| Q2 | Does UConn IT Accessibility measure the **desktop** client against WCAG 2.1 AA (via WCAG2ICT), and is there an audit or deadline? | Procedures scope WCAG to web/mobile/social; UConn Health scope page not retrievable (403) | No (plan targets WCAG either way) | Email itaccessibility@uconn.edu; fetch the UConn Health page on-network |
| Q3 | Should Cividis become the **default** colormap? | Changes every user's figures and movies; affects comparability with published VCell images | No (ships opt-in) | PI / stakeholder decision; release note |
| Q4 | Cividis out-of-range special colors: black/white measure ≈1.3:1 against the endpoints | No achromatic color separates from both ends; a chromatic choice needs CVD checking | Blocks 8.2-c | Candidate evaluation with the script, then design sign-off |
| Q5 | Deploy order: can the export server ship before the client exposes Cividis? | Separate release trains | Blocks enabling Cividis in the client | Release manager; 5.3 fallback mitigates |
| Q6 | Dark-mode palette for Swing plots | No Swing dark mode exists (#1604) | Blocks the #1603 dark clause for Swing only | #1604 |
| Q7 | Cividis data licensing note | matplotlib's cividis table is published with the paper; confirm attribution text | No | Check the `matplotlib` LICENSE / paper supplement |
| Q8 | Does the 5,000-point `Path2D` render stay within current paint times? | Not measured | No | Benchmark in 8.3-b |

---

## 11. Gemini Plan Improvements

- **Corrected**
  - The standards hierarchy: UConn color rules are guidance; WCAG 2.1 AA is binding through the
    Procedures for web/mobile; 508 applies to procurement only.
  - WCAG edition, and six contrast figures.
  - F-07, F-08 (dead code), F-09 and F-14 verdicts.
  - The `COLORBLIND20` "accessible" claim.
  - The "strict requirement" labels.
  - Gemini's failing replacement color (`#e65100`).
- **Expanded**
  - Measured CVD/contrast evidence for every palette.
  - The server-side export dependency and fallback.
  - Deterministic colormap ordering.
  - Special-color problem for new colormaps.
  - Test design with pass/fail thresholds.
- **Removed**
  - `ConstraintTableCellRenderer` fix (unreachable).
  - StatusIcon redesign (already dual-coded).
  - Red-border replacement (text already provided).
  - webapp-ng badge/footer and SpringSaLaD 3D shapes (moved to FOLLOW-UP).
  - `axe-core` CI (not applicable to Swing).
  - A new colormap selector UI (already dynamic).
  - REC-04 global theme setting.
- **Reprioritized**
  - Legacy plot line styles are first, and the colormap is second (it isn't a WCAG failure given
    the heat-map exemption and text readout).
  - The colormap default stays unchanged pending a decision.
- **Newly discovered**
  - `generateAutoColor` produces <3:1 lines, and its callers include solver input.
  - The per-segment dash-phase pitfall.
  - Two plot frameworks, and 21 panels using the legacy one.
  - Reaction/rule diagram edge selection is color-only.
  - Geometry-viewer region naming.
  - The field viewer is a desktop surface and the only dark-mode surface.
  - Math-override red text (1.4.3).
  - Color-only help text (1.3.3).
  - Updated regulatory dates (DOJ IFR 2027-04-26; HHS 84.84 2027-05-11).
  - The HHS §504 rule.
  - §508 503.2.
  - #1603 is an epic that color work can't close.

---

## 12. Final Implementation Plan (ordered checklist)

1. ✅ **`docs/accessibility/color-audit.md`** — record the §5 inventory and verdicts. *#1605.* Validate:
   file review; every §5 row present. **Done: PR #2141**, awaiting review.
2. ✅ **GitHub follow-ups** — open issues for X1–X5, S5–S7, G2 and `ConstraintPanel` removal as #1603
   sub-issues. **Done: #2132–#2140**; X5 recorded on #1604/#1606. *#1605.* Validate: the `sub_issues` API lists them.
3. **`vcell-util/.../ColorUtil.java`** — add `CVD_SAFE_LIGHT` (`#000000,#999933,#004488,#8C510A,
   #0072B2,#CC6677`), `seriesColor(i)` and `seriesDash(i)`; leave `generateAutoColor` untouched.
   *#1603/#1605.* Validate: test 8.2-a.
4. **`vcell-util/src/test/.../ColorAccessibilityTest.java`** — contrast, Machado and ΔE76
   assertions, plus the `@Disabled` dark hook. *#1603.* Validate: it runs in the Fast group.
5. **`vcell-client/.../plot/gui/Plot2DPanel.java`** — palette via `seriesColor`;
   `getVisiblePlotStroke`; single `Path2D` per curve; marker shapes ≥6 px; histogram outlines.
   *#1605.* Validate: 8.3-a, 8.3-b, 8.8-c.
6. **`vcell-client/.../plot/gui/PlotPane.java`** — `LineIcon` draws stroke and marker. *#1605.*
   Validate: 8.3-c.
7. **`Plot2DSettingsPanel` / `Plot2DSettings`** — "Vary line styles" (default on). **`Plot2DPanel
   .pointerMoved`** — series name in the status. *#1605.* Validate: 8.3-d, 8.8-e.
8. **`MultisourcePlotPane.java`** — same palette and style icons. *#1605.* Validate: 8.4-b.
9. **`MoleculeVisualizationPanel` / `ClusterVisualizationPanel`** — `CVD_SAFE_LIGHT` palette;
   **`PlotRenderers.AvgRenderer` + `AbstractVisualizationPanel.LineIcon`** — dash styles.
   *#1603/#1605.* Validate: 8.4-a, 8.3-e.
10. **`vcell-core/.../gui/graph/EdgeShape.java`, `vcell/graph/ReactionParticipantShape.java`,
    `RuleParticipantEdgeDiagramShape.java`** — 2.5 px stroke for selected and selected-neighbor
    edges (dashed variant kept). *#1605.* Validate: 8.3-f, then screenshots.
11. **`ImagePlaneManagerPanel.java` + `GeometryViewer.java`** — index-label provider; hover shows
    the subvolume name. *#1605.* Validate: 8.3-g.
12. **`vcell-core/.../image/DisplayAdapterService.java`** — `CIVIDIS`, its model and special colors,
    the guard, `addStandardColorModels`, ordered IDs. *#1603.* Validate: 8.2-b, 8.2-c.
13. **Registration sites** (`PDEDataContextPanel`, `KymographPanel`, `PDEOffscreenRenderer`,
    `RasterExporter`, `IMGExporter`, `DisplayImageOp`, `MediaSettingsPanel` text) → the shared
    helper; **`ExportSpecs.setupDisplayAdapterService`** gets an unknown-ID fallback. Keep the
    BlueRed default. *#1603.* Validate: 8.4-c, 8.2-e, 8.8-a, 8.8-b.
14. **`webapp-viewer/viewer.js` + `index.html`** — Cividis LUT option (3D view and kymograph);
    CVD-safe `SERIES_COLORS` plus a dark variant; `stroke-dasharray` traces; fix the three
    sub-4.5:1 text colors and add a dark-scheme override. *#1603/#1605.* Validate: 8.5, a
    `webapp-viewer/test` LUT test, and 8.6-b.
15. **`GuiConstants.java`** — error/warning text colors; apply them in
    `MathOverridesTableCellRenderer` and `SimulationConsolePanel` (with tags). *#1605 (SHOULD).*
    Validate: 8.2-d, 8.3-h, 8.4-d.
16. **UserDocumentation** — reword the H1 color-only instructions and the Cividis mention.
    *#1605 (SHOULD).* Validate: `rg` check (7.2).
17. **Full validation** — 8.1, 8.6-b screenshots (normal, protan, deutan, tritan, grayscale), 8.7
    manual QA on three OSes, 8.8 scientific regression. Attach the evidence to the PR, then close
    #1605 per §9 and comment on #1603 with the palette-clause evidence.
