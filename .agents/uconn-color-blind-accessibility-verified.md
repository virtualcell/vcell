# VCell Accessibility Implementation and Verification Plan — Complete #1605 and WCAG AA Conformance

**Scope revision: 2026-10-01. Status: PLAN, NOT A CONFORMANCE CLAIM.**
The user expanded this plan from selected color fixes to full #1605 remediation and project-wide
accessibility. The release target is **WCAG 2.2 Level AA plus the UConn WCAG 2.1 Level AA baseline**;
this means every applicable A and AA criterion, not AAA or a guarantee of meeting every disability
need. Apply WCAG to web content directly and use WCAG2ICT to interpret it for desktop software and
non-web documents. WCAG2ICT is informative guidance, not a certification standard.

On 2026-10-01, §7 and §11 were aligned to §15. Phases 0–7 and the historical Gemini cuts are not
the full scope. The WCAG target, the AA level, and §13 are unchanged.

All criteria in §13, surfaces in §14, work packages in §15, and release gates in §16 are mandatory.
Historical code findings and test reports below are retained as dated evidence, not current passes.
No existing checkbox establishes compliance for the revised scope. A known applicable failure,
untested requirement, missing environment, disabled test, or open remediation dependency blocks the
corresponding completion claim. An approved backlog deferral cannot turn a WCAG failure into a pass.

Only this plan is being edited now. It does not authorize deployment, issue closure, or publication
of a conformance statement in this editing session.

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

**Technical scope and institutional interpretation.** UConn's procedures name WCAG 2.1 AA.
This project adopts WCAG 2.2 AA additionally so the release addresses its newer A/AA requirements.
The field viewer and other browser interfaces are web content. For Swing and non-web documents,
apply the same target through WCAG2ICT and document the interpretation per criterion. Historical
regulatory references in §2 are context, not a current legal determination or a reason to defer
technical work. Verify applicable institutional obligations through the §14 ledger before release.

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
8. WCAG Understanding 1.4.11 permits an essential-presentation exception for measurement
   gradients. Evaluate its conditions per surface; it does not exempt the entire viewer or
   its legends, special states, controls and keyboard/assistive-technology access. A hover
   readout alone does not establish full conformance. Cividis remains a required project
   enhancement, with criterion-specific evidence for any standards exception.
9. The help text contains color-only instructions ("values that have changed appear in red"),
   which falls under SC 1.3.3.

**How #1605 and #1603 relate.** This revision adopts the strict reading of #1605: evaluate
**all** views, menus, windows, states, and workflows and remediate every applicable color-accessibility
failure. #2132–#2140 are implementation work packages, not permission to leave color failures open.
#1604 and #1606 remain separate ownership tracks for appearance and text scaling, but their relevant
requirements are mandatory dependencies of full project conformance. Color work alone cannot close
#1603. Supported light/dark/high-contrast environments must be tested; a disabled dark-palette test
is scaffolding, not evidence. Dark mode is a project/epic requirement, not a standalone WCAG criterion.

**Recommended direction.** Fix *identity by color alone* at the root rendering paths, not color by
color:
1. Per-series line style and marker in the shared plot renderers, with legends that show them.
2. A measured CVD-separable categorical palette for plots, as a new `ColorUtil` entry point.
   `generateAutoColor` stays untouched because Smoldyn input and FRAP depend on it.
3. A non-color selection cue for diagram edges.
4. Region names in the geometry viewer's hover readout.
5. An opt-in CVD-optimised perceptually uniform colormap (cividis), registered through one shared
   helper on both client and export server.
6. A complete surface inventory, remediation ledger, and release-specific evidence proving all
   #1605 requirements, followed by the full WCAG programme in §§13–16.

**Completion contract.** This plan now covers all #1605 color findings and all applicable WCAG
A/AA criteria across the VCell release boundary (§14). The plan is complete only when every applicable
criterion has a mapped implementation task and test, and the implementation is complete only when
§9 and §16 pass. Keyboard access, focus, assistive technology, contrast, scaling, documents, media,
input, and complete workflows are in scope. #1605 closure establishes its color-accessibility scope;
only the separate full conformance gate permits a project-wide conformance statement.

---

## 2. Sources and Governing Accessibility Requirements

This source table began with the 2026-09-30 color audit. The complete current technical target
is §13; deadline/legal/procurement applicability statements below require institutional verification
in §14 and cannot substitute for release evidence.

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
| Understanding 1.4.11: "each line in a graph" is a graphical object; not required when the info is available as text or a table; **"color gradients that represent a measurement, such as heat maps" are essential (exempt)** | A | Informative | Apply essential-presentation exceptions narrowly; evaluate all remaining graphics and interaction | https://www.w3.org/WAI/WCAG21/Understanding/non-text-contrast.html |
| WCAG2ICT (W3C Group Note, 2024-11-15): how to read WCAG for non-web software | A | Informative | Interpreting WCAG for the Swing client | https://www.w3.org/TR/wcag2ict-22/ |
| Perceptually uniform, CVD-robust colormaps (cividis: Nuñez et al., PLOS ONE 2018; Crameri et al., *Nat. Commun.* 2020) | BP | Best practice (papers not retrieved this session) | Spatial results colormap | — |

The pinned WCAG editions used for this revision are listed in §13. In particular, account for
the updated treatment of SC 4.1.1 rather than asserting every criterion is unchanged since 2018.

---

## 3. Historical Gemini Research Verification (2026-09-30)

This section preserves the original comparison. Its exclusions and follow-up priorities are
superseded by the expanded scope, §9, and §§13–16; its measurements require release revalidation.

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

Historical issue research used `gh api`. As checked 2026-10-01, #1605 is open and now has an
audit/remediation comment linking PR #2141; that PR is open with review required. Re-fetch issue
state and dependencies at release time. This plan adopts the strict all-areas closure contract
below; the earlier interpretation permitting unresolved color failures is withdrawn.

### Issue #1605 — "VCell GUI needs to be evaluated for Colorblindness"

- **Original text (verbatim):** "We need to verify that all views, menus, windows, etc. are
  colorblind-safe. The obvious targets are model and results viewers but all areas should be
  evaluated."
- **Plan acceptance criteria:** §9. All areas must be evaluated and all applicable color-accessibility
  failures fixed and verified, including failures outside the model/results viewers.
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
- **Scope:** this expanded plan covers the full accessibility programme. #1604, #1605, #1606 and
  all additional applicable remediation dependencies must be satisfied before epic closure.
- **Implicit requirements:**
  - Every *categorical* and *continuous* palette the GUI owns has a CVD-validated option (and a
    default, subject to §10 Q3).
  - Palettes must be verifiable against light **and** dark backgrounds. Dark exists today only in
    the field viewer; the Swing dark mode depends on #1604.
- **Relevant code:** `ColorUtil`, `DisplayAdapterService`, `webapp-viewer/viewer.js` LUT and
  series colors.
- **Objective acceptance criteria:** §9 and §§13–16, including the palette clause.

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
item. These are historical findings, not an exhaustive surface inventory or current passes. Recheck
all PASS/None dispositions on the release build. §14 expands discovery beyond color literals.
Every required color fix is a #1605 blocker; tracking in another issue does not exclude it.

| # | Path · class/method | What / why relevant | How color is used now | Gemini? | Change? |
|---|---|---|---|---|---|
| P1 | `vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java` · `drawLinePlot` `:1342`, `drawHistogram` `:1390`, `getVisiblePlotPaint` `:1778`, `pointerMoved` `:2205` | Line plots for 21 production panels (13 direct, 7 via `MultisourcePlotPane`, 1 via `TimeFunctionPanel`: ODE, PDE time, parameter estimation, BNG, FRAP, kymograph, electrical stimulus) | Hue only. 1.5 px solid stroke. Segments drawn one by one with `g.draw(line)`. Nodes are 2–3 px circles. Colors from `ColorUtil.generateAutoColor(n,bg,0)`. Crosshair status shows x,y but no series name | Yes (partly) | **MUST** |
| P2 | `vcell-client/src/main/java/cbit/plot/gui/PlotPane.java` · `LineIcon` `:55`, `updateLegend` `:1321` | Legend for P1 | 50×2 px solid swatch in the series color | Yes | **MUST** |
| P3 | `vcell-util/src/main/java/org/vcell/util/ColorUtil.java` · `generateAutoColor`, `TABLEAU20`, `COLORBLIND20`, `DARK20` | Palette source. `generateAutoColor` has 14 callers, including `SmoldynFileWriter.java:219` (solver input) and FRAP ROI panels | Seeded random with an RGB-sum heuristic; not CVD-aware. At n=6, protan ΔE′ is 3.9 and 2 lines are <3:1 on white | Partly | **MUST**: add a new method; don't modify `generateAutoColor` |
| P4 | `vcell-client/src/main/java/cbit/plot/gui/PlotRenderers.java`, `AbstractPlotPanel.java`; `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/{Molecule,Cluster}VisualizationPanel.java` (`initializeGlobalPalette` `:423`/`:206`), `AbstractVisualizationPanel.LineIcon` `:21` | Langevin results plots | `TABLEAU20` (protan ΔE′ 1.6) / `DARK20` (1.6); hue-only curves; legend hover dims other series | Yes | Palette **MUST** (#1603); line styles **MUST** |
| P5 | `vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java` `:80,:257` | Parameter-estimation plot and list | Own `generateAutoColor`; data drawn as points, model as lines | No | MUST (use the P3 palette and styles) |
| C1 | `vcell-core/src/main/java/cbit/image/DisplayAdapterService.java` · `createBlueRedColorModel0` `:226`, special colors `:269`, `addColorModel` guard `:149`, `setActiveColorModelID` `:721` | Spatial colormaps | Rainbow (see F-01 row). Below-min black vs map minimum `(0,0,128)` is 1.31:1 | Yes | **MUST** (#1603): add cividis via a shared registration helper |
| C2 | Colormap registration sites: `PDEDataContextPanel.java:1091`, `KymographPanel.java:2290`, `PDEOffscreenRenderer.java:293` (server), `RasterExporter.java:539` (server), `IMGExporter.java:94`, `vcell-vmicro/.../DisplayImageOp.java:124`, `ImagePaneScrollerTest.java:174` | Duplicated registration | Each registers Gray and BlueRed by hand; the server throws on an unknown ID | No | **MUST**: one helper `DisplayAdapterService.addStandardColorModels(das)` |
| C3 | `vcell-client/src/main/java/cbit/image/gui/DisplayAdapterServicePanel.java` · `updateColorModelRadioButtons` `:1494` | Colormap selector | Buttons built from `Hashtable` key order (unstable) | No | MUST: deterministic order |
| C4 | `vcell-client/src/main/java/cbit/image/gui/ImagePlaneManagerPanel.java` · `updateInfo` `:1437` | Hover readout for spatial images | Text: value, coordinates, volume/membrane names | No | MUST retain numeric access and add keyboard/programmatic access; evaluate remaining criteria |
| D1 | `vcell-core/src/main/java/cbit/vcell/graph/ReactionParticipantShape.java` · `paintSelf` `:174` | Reaction-diagram edges | Selected = red; edges of a selected species = `red.darker()` (178,0,0) vs black = **2.89:1**, below 1.4.1's lightness threshold. Protan-simulated dark red vs black ≈2.0:1 | No | **MUST** |
| D2 | `vcell-core/src/main/java/cbit/gui/graph/EdgeShape.java` `:52-63,:259`; `vcell-core/src/main/java/cbit/vcell/graph/RuleParticipantEdgeDiagramShape.java` `:138` | Generic and rule-diagram edges | `defaultFGselect = red` vs black (5.25:1 normative pass, but the UConn "avoid red/black" pair). Catalysts already dashed (#176) | No | **MUST** (same helper as D1) |
| D3 | `vcell-core/src/main/java/cbit/vcell/graph/{SpeciesContextShape,MolecularTypeLargeShape,MolecularComponent*Shape}.java` | Species/molecule glyphs | Plain species green vs rule-based blue (6.5:1 lightness difference, passes); error red vs OK yellow (3.7:1, passes); selected species gets a raised label box; molecule identity is labeled with text | No | None (document as passing) |
| G1 | `vcell-client/src/main/java/cbit/vcell/geometry/gui/GeometryViewer.java` · `setColorMap` `:607`, `refreshSourceDataInfo` `:245` + `ImagePlaneManagerPanel.updateInfo` | Geometry (model) image | Regions colored by `createContrastColorModel`. Hover shows only the handle index; the subvolume table (`GeometrySubVolumeTableCellRenderer.java:37-44`) shows name and swatch but no handle → **region→name by color only** | No | **MUST**: show the subvolume name in the hover readout |
| G2 | `GeometrySubVolumeTableCellRenderer`, `CSGObjectTreeCellRenderer:61`, `StructureMappingTableModel:433`, `StructureMappingTableRenderer:121`, `GeometrySummaryPanel:973` | Swatches next to names | Swatch plus text (passes 1.4.1); palette weak under deutan (ΔE′ 2.0) | Partly | REQUIRED WORK PACKAGE (palette) |
| S1 | `vcell-client/src/main/java/org/vcell/util/gui/DefaultScrollTableCellRenderer.java` `issueRenderer` `:265-347` | Shared table issue decoration | Border color plus **shape-distinct icon** (error ⊗, warning ⚠, none) plus tooltip | No | None (passes) |
| S2 | `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/MathOverridesTableCellRenderer.java:54` | Simulation-editor overrides table (core workflow) | Changed rows in `Color.red` text: 4.00:1 on white, 3.43:1 on the alternating row `#e8edff` (fails 1.4.3). Non-color cue exists (override column filled) | No | **MUST** |
| S3 | `vcell-client/src/main/java/cbit/vcell/client/desktop/biomodel/SimulationConsolePanel.java:127-143` | Rule-based network-generation console | Error/Warning/Stopped all `Color.RED` (4.00:1); warning vs normal text differs by hue only | Yes | **MUST** |
| S4 | `ViewJobsPanel.java`, `StatusIcon.java` | Job status | Icon plus status text plus tooltip | Yes (wrong verdict) | None |
| S5 | `OutputOptionsPanel`, `MeshSpecificationPanel`, `StochSimOptionsPanel`, `TableCellEditorAutoCompletion` + `GuiConstants.ProblematicTextFieldBorder` | Input validation | Text dialog, then red border and refocus | Yes (wrong verdict) | MUST verify retained error identification; add persistent accessible text where required (§15) |
| S6 | `vcell-client/src/main/java/cbit/vcell/constraints/gui/*` | General constraints UI | Unreachable | Yes (wrong priority) | MUST prove unreachable in shipped builds; remove or remediate if exposed |
| S7 | `DefaultScrollTableCellRenderer.java:58,:101-111` | Non-editable text brown `#964B00`; spatial-process match rows yellow | Brown vs black 3.3:1 (passes, marginally); yellow vs white row 1.07:1 is **hue-only** | No | REQUIRED WORK PACKAGE |
| W1 | `webapp-viewer/viewer.js` (`:792` LUT, `:2329` kymograph LUT, `:1478` `SERIES_COLORS`, probe/stat plots), `webapp-viewer/index.html` (`:41,:43,:44,:53,:62,:89-91`) | Browser 3D field viewer launched from `PDEDataViewer` "View in 3D" (default on) | Rainbow LUT; probe traces are hue-only (list has labels); text `#2a7` on white 2.96:1; `.stale` white on `#d70` 3.13:1; `.status.err` on dark 3.44:1; follows OS dark mode | Yes (missed it's desktop) | **MUST** (LUT option, trace styles, text contrast) |
| X1 | `vcell-core/src/main/java/org/vcell/util/springsalad/Colors.java`; `SpringSaladViewerCanvas.java` | SpringSaLaD particles | Colors persisted per site and written to SpringSaLaD files; species legend with toggles exists | Yes | REQUIRED WORK PACKAGE |
| X2 | `vcell-client/src/main/java/cbit/vcell/geometry/gui/OverlayEditorPanelJAI.java:95` `CONTRAST_COLORS` | Image-geometry ROI painting | Red, green, blue… ROI identity via swatch list | No | REQUIRED WORK PACKAGE |
| X3 | `vcell-client/src/main/java/cbit/vcell/microscopy/gui/estparamwizard/AnalysisTableRenderer.java:85-121` | FRAP analysis: non-identifiable results | Text "NOT IDENTIFIABLE" (1.4.1 passes) plus red text on `(255,170,170)` = 2.21:1 (1.4.3 fails). The "significant in green" code comment is stale; no green is drawn | No | REQUIRED WORK PACKAGE ([#2134](https://github.com/virtualcell/vcell/issues/2134)) |
| X4 | `webapp-ng/.../publication-edit.component.css`, `footer.component.html` | Public website | Badge contrast; missing UConn link | Yes | REQUIRED WORK PACKAGE (separate issue) |
| X5 | `vcell-client/src/main/java/cbit/vcell/client/VCellLookAndFeel.java` | L&F, fonts | No dark mode; macOS fonts −2 pt | Yes | #1604 / #1606 |
| H1 | `vcell-client/UserDocumentation/originalXML/topics/...`: `simulationEditor.xml:27` ("changed appear in red"), `simulations.xml:55`, `PP_Species.xml:23`, `PP_ReactionRulesEditor.xml:14`, `PathwayDiagramView.xml:45`, `PathLink.xml:24`, `SimResultsDataRange.xml:28` | In-app help | Color-only instructions (SC 1.3.3) | No | **MUST** (all affected help pages) |

---

## 6. Accessibility Gap Matrix

| Requirement | VCell Component | Current Behavior | Accessibility Gap | Required Fix | Issue |
|---|---|---|---|---|---|
| WCAG 1.4.1 (N) | P1/P2 legacy plots | Curves differ only by hue; legend swatch is color only | Curve↔legend mapping fails for CVD users and in grayscale; nothing replaces hue | Per-series dash pattern (continuous `Path2D`), marker shape when nodes are shown, legend icon draws both | #1605 |
| WCAG 1.4.11 (N) | P1/P3 | Auto colors often <3:1 on white (e.g. 1.27:1) | Low-vision users can't see some lines. (The Data table view is an alternative for values, but not for reading the graph) | Palette with every entry ≥3:1 on white | #1605/#1603 |
| #1603 "use color blind palettes" (issue req.) + UConn guidance (U) | P3/P4 | TABLEAU20/DARK20/random | Measured protan/deutan ΔE′ 1.6–7.5 | Validated palette `ColorUtil.CVD_SAFE_LIGHT` (§7 task 1) | #1603 |
| WCAG 1.4.1 (N) + UConn red/black (U) | D1/D2 diagram edges | Selection = color change only; dark red vs black 2.89:1 | Selected/related edges can't be distinguished without hue | Thicker stroke for selected (and selected-neighbor) edges | #1605 |
| WCAG 1.4.1 (N) | G1 geometry viewer | Region→subvolume only via swatch color | Compartment identity can't be read without hue | Hover readout appends the subvolume name (and handle) | #1605 |
| #1603 palette clause (issue req.) + BP; gradient exemption only when justified; all other applicable criteria remain | C1/C2 spatial colormap | Rainbow only; server registers only Gray/BlueRed | Non-monotonic; protan ΔE′ 0.2 between distinct values | Add "Cividis" through a shared helper on client **and** server, with its own special colors | #1603 (#1605 benefit) |
| WCAG 1.4.1 / 1.4.3 / 1.4.11 (N, web content) | W1 field viewer | Hue-only probe traces; three text colors below 4.5:1; rainbow LUT | As above, directly under WCAG | `stroke-dasharray` per probe, legend dash sample; fix three CSS colors; LUT option matching desktop | #1605/#1603 |
| WCAG 1.4.3 (N) | S2 overrides table | Red text 3.43–4.00:1 | Text contrast | Accessible dark red constant | #1605 (MUST) |
| WCAG 1.4.3 (N) + UConn red/black (U) | S3 console | Red for 3 severities | Contrast; warning vs normal by hue | `[Error]`/`[Warning]`/`[Stopped]` prefixes plus compliant colors | #1605 (MUST) |
| WCAG 1.3.3 (N) | H1 help pages | "appear in red", "sites in green" | Instructions rely on color | Reword to name the non-color cue | #1605 (MUST) |
| Best practice | P1 crosshair | Status shows x,y only | Identity needs the legend | Prefix the status with the series name | #1605 (MUST) |
| Best practice / scientific integrity | P1 styles | New dashes might hide narrow spikes | Interpretability risk | Plot settings "Vary line styles" toggle (default on); series 0 solid | #1605 (MUST) |

---

## 7. Final Recommended Implementation Plan

Phases 0–7 are the shared color primitives and the first code changes. Completion is §9 and §16.
§15 C finishes every color package, including repeated styles, styles turned off, custom colors,
keyboard-accessible isolation, #2132–#2140, and every S8 renderer. §§15 B, D, E, and F, together
with the §13 matrix, are required for WCAG 2.2 Level AA. Where this section and §15 differ, §15
governs. Finishing Phases 0–7 alone leaves #1605 open and leaves the WCAG claim unmet.

Colors referenced below were measured with `.agents/cvd_analysis.py`.

**Palette `CVD_SAFE_LIGHT`, in greedy maximum-separation order:** `#000000`, `#999933`, `#004488`,
`#8C510A`, `#0072B2`, `#CC6677`.
- Contrast on white: 21.0, 3.02, 9.62, 6.37, 5.19 and 3.66:1.
- Min ΔE′ (CAM02-UCS): 21.0 normal, 17.5 protan, 16.3 deutan, 18.9 tritan.
- Min CIELAB ΔE76 after Machado simulation: ≥16.3.
- Exhaustive search over 24 established CVD-oriented colors shows the best *6*-color set scores
  15.5 and the best *8*-color set only 11.9. Use these six colors as the categorical primitive.
  Cycling them with the Phase 2 dash patterns yields 24 unique `(color, dash)` pairs. That bound
  is the primitive tested by 8.2-a. §15 C requires another non-color discriminator, or
  keyboard-accessible selection and isolation, when a pattern repeats, when styles or nodes are
  off, and when a custom color is in use.
- Palette entries are indistinguishable in grayscale. Dash pattern, markers, labels, and
  keyboard-accessible isolation carry identity, as §15 C requires.
- `#999933` measures 3.02:1 on white. §16 forbids treating a rendered pair below 3:1 as a pass.
  Replace that entry if a rendered measurement fails.

### Phase 0 — Audit record (MUST, #1605) — historical seed complete; expanded audit OPEN

**0.1** `docs/accessibility/color-audit.md` (new) — ✅ done
- **Change:** check in the §5 inventory: one row per view with its verdict and evidence, and a
  "PASS by non-color cue" list (S1, S4, S5, D3, C4). Link it from #1605.
- **Scope note:** that PASS list is a 2026-09-30 disposition. §9 requires those passes to be
  reverified. §15 reopens S5 (persistent accessible error text, #2136) and C4 (keyboard and
  programmatic numeric access).
- **Rationale:** the audit establishes discovery coverage; remediation and release evidence in §9
  are also required before closure.
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

**0.2** GitHub issue filing — ✅ filed (2026-09-30); remediation remains open under §15 C
- **Historical step:** file X1–X5, S5–S7, G2 and the `ConstraintPanel` reachability issue. Label
  them `Accessibility Requirement` and make them sub-issues of #1603.
- **Current requirement:** filing is not closure. Each open color failure in that set is a #1605
  blocker under §15 C. #1604 and #1606 remain dependencies of the full WCAG gate (§15 B, §10 Q6).
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

**Completed sections (2026-09-30).** Tasks 1.1 and 1.2 are complete. The primitives are what
Phase 2 and Phase 3 call. Committed on `chore/vcell#1605` in `4f0d5efc20` (not pushed; no PR
yet). The dark-background test stays disabled until #1604. Copying the palette into the
field viewer remains Phase 6.

| Section | Completed work | Still open |
|---|---|---|
| **1.1** `ColorUtil` | `CVD_SAFE_LIGHT`, `seriesColor`, and the 8-slot `seriesDash`. `generateAutoColor`, `TABLEAU20`, `DARK20`, and the `COLORBLIND20` entries are unchanged. Javadoc on `COLORBLIND20` says it failed CVD validation. | Committed in `4f0d5efc20`, not pushed. Field-viewer JavaScript mirror is Phase 6. |
| **1.2** `ColorAccessibilityTest` | Fast test: contrast on white, Machado protan/deutan/tritan ΔE76, 24 unique pairs, and the `TABLEAU20` entries `0..7` failing that check. 8 run, 0 failures, 1 skipped. | `cvdSafeDarkPaletteMeetsContrastOnDarkBackground` stays `@Disabled` until #1604. |

**1.1** `vcell-util/src/main/java/org/vcell/util/ColorUtil.java` — ✅ done (2026-09-30)
- **Change:**
  - Add `public static final Color[] CVD_SAFE_LIGHT` (above).
  - Add `public static Color seriesColor(int i)` (cycles the palette).
  - Add `public static float[] seriesDash(int i)`. The first cut used `(i / 6) % 4`. On
    2026-09-30 that rule was replaced (see Phase 2, C1) by an 8-slot cycle so series 1 is
    non-solid and the first 24 `(color, dash)` pairs stay unique.
  - Leave `generateAutoColor`, `TABLEAU20`, `DARK20` and `COLORBLIND20` **unchanged**:
    `SmoldynFileWriter` and the FRAP/vmicro ROI panels depend on their exact output. Add Javadoc
    on `COLORBLIND20` stating it failed CVD validation.
- **Rationale:** one measured palette and style sequence shared by both plot frameworks and the
  field viewer (mirrored in JS).
- **Identity bound:** uniqueness for the first 24 `(seriesColor, seriesDash)` pairs is the
  primitive in 8.2-a. §15 C still requires identification when a pair repeats or styles are off.
- **Requirement:** 1.4.1, 1.4.11, #1603, completed only under §15 C.
- **Risk:** low (additive).
- **Verify:** unit test 8.2-a.
- **Result:**
  - `CVD_SAFE_LIGHT` is the six measured colors in order (`#000000`, `#999933`, `#004488`,
    `#8C510A`, `#0072B2`, `#CC6677`). `seriesColor` cycles that array. `seriesDash` was first
    `(i / 6) % 4` (`null`, `{6,3}`, `{2,2}`, `{8,3,2,3}`). Phase 2 replaced that on 2026-09-30
    with the 8-slot cycle below. `ColorAccessibilityTest` was updated in the same change and
    still passes (8 run, 0 failures, 1 skipped).
  - The diff against the previous `ColorUtil` is additive. `generateAutoColor`, `TABLEAU20`,
    `DARK20`, and the `COLORBLIND20` entries are unchanged. Javadoc on `COLORBLIND20` states
    that it failed CVD validation.
  - Test 8.2-a (`ColorAccessibilityTest`) passed:
    `mvn test -pl vcell-util -Dgroups=Fast -Dtest=ColorAccessibilityTest` — 8 run, 0 failures,
    1 skipped (the #1604 dark-palette hook). The module Fast group also passed (50 run, 0
    failures, that same test skipped), so the new class is picked up by `@Tag("Fast")`.
  - Committed on `chore/vcell#1605` in `4f0d5efc20` (not pushed; no PR yet). Legacy plot
    wiring is Phase 2 and Langevin wiring is Phase 3; both now call these primitives. The
    field-viewer JavaScript mirror remains Phase 6.

**1.2** `vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java` (new, `@Tag("Fast")`) — ✅ done (2026-09-30)
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
- **Result:**
  - The class implements WCAG contrast, the three Machado severity-1.0 matrices, and CIELAB ΔE76
    with no new dependencies. Constants match `.agents/cvd_analysis.py`.
  - Assertions cover ≥3.0:1 on white, pairwise ΔE76 ≥15 under protan, deutan, and tritan, 24 unique
    `(seriesColor, seriesDash)` pairs, and `TABLEAU20` entries `0..7` failing that predicate.
  - `cvdSafeDarkPaletteMeetsContrastOnDarkBackground` is `@Disabled` until #1604 adds a
    dark-background constant.
  - `mvn --batch-mode test -pl vcell-util -Dgroups=Fast` on 2026-09-30: 50 run, 0 failures, 1
    skipped. `ColorAccessibilityTest` itself: 8 run, 0 failures, 1 skipped.
  - Committed on `chore/vcell#1605` in `4f0d5efc20` (not pushed; no PR yet). The #1603
    dark-background definition-of-done checkbox stays open: it also requires the field
    viewer's dark-scheme text colors (Phase 6).

### Phase 2 — Legacy plot framework (MUST, #1605)

**Completed sections (automated checks 2026-10-01).** Tasks 2.1 through 2.5 are complete for
the legacy plot framework. Manual 8.7 (two reviewers on macOS, Windows, and Linux) is not run.
8.6-b for this phase is the legacy-plot grayscale check only; reaction-diagram, geometry,
spatial, and field-viewer scenes remain later phases. Machado protan/deutan/tritan image filters
were not applied. A pixel compare against a pinned pre-change commit is not a pass: path joins,
markers, and histogram outlines differ on purpose. Committed on `chore/vcell#1605` — primitives
and tasks 2.1–2.5 in `4f0d5efc20`, keyboard-isolation additions in `fc8ebd3aea` (not pushed; no
PR yet).

| Section | Completed work | Still open |
|---|---|---|
| **2.1** `Plot2DPanel` | Auto color uses `seriesColor`. One `Path2D` per curve. Markers cycle five shapes at 6 px. Histogram bars use the series stroke. Styles off keeps keyboard naming and Ctrl+I isolation, including after the 8-slot dash cycle repeats. | Manual 8.7. |
| **2.2** `PlotPane` legend | `LineIcon` is at least 50×12 and draws the series stroke and marker. Click, Enter, and Space select the series. Accessible name is the raw plot name. Test 8.3-c passed. | Manual 8.7. |
| **2.3** Vary line styles | Checkbox `Vary line styles`, default on, saved on the `Plot2DSettings` bean. | None inside this task. Styles off still depends on the 2.1 keyboard identity. |
| **2.4** Pointer and keyboard status | Status text starts with the series name. Ctrl+N and Ctrl+P name the series with no pointer event. Test 8.3-d passed. | Manual 8.7. |
| **2.5** `MultisourcePlotPane` | Auto colors are `seriesColor`. List icons use the same stroke. `generateAutoColor` is gone from this class and from `Plot2DPanel` (check 8.4-b). | Manual 8.7. |

**Dash contract (C1), decided 2026-09-30.** `ColorUtil.seriesDash` is an 8-slot cycle,
`i % 8`: solid, `{6,3}`, `{2,2}`, `{8,3,2,3}`, `{6,3}`, `{2,2}`, `{8,3,2,3}`, solid. Series 0
is solid and series 1 is non-solid, which is what 8.3-a/b require. The color period is 6, so
the first 24 `(seriesColor, seriesDash)` pairs stay unique. A solid stroke returns at `i = 7`,
`15`, `16`, and so on. The older sentence “every `i ≥ 1` is non-solid” is withdrawn. The plot
client calls `ColorUtil.seriesDash` directly.

**Raster contract (C2).** One `Path2D` per curve, round joins, 6 px markers, and histogram
outlines change geometry relative to per-segment `BasicStroke(1.5f)` drawing. With “Vary line
styles” off, the stroke is `new BasicStroke(1.5f)`. A channel-only compare to `master` was not
run and would not be limited to color. That difference is intentional.

**2.1** `vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java` — ✅ automated checks done (2026-09-30); 8.7 not run
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
- **Styles off:** a solid stroke is allowed so a scientist can inspect undashed curves. With
  styles off, Ctrl+N and Ctrl+P still name the current series in the status label, and Ctrl+I
  isolates that series (a second Ctrl+I shows every visible series). Legend labels accept
  Enter and Space. On this legacy plot, a repeated dash after 24 series is identified by that
  same keyboard selection and isolation. W1 still needs the same rule.
- **Rationale:** 1.4.1 sufficient technique G111 (color **and** pattern), completed for repeated
  and suppressed styles only by §15 C.
- **Risk:**
  - Visual change in 21 panels.
  - Performance with 10⁵-point stochastic runs (one `Path2D` is typically faster than many
    `draw` calls; measure it).
  - Dashes can mask narrow spikes (mitigated by 2.3 and series 0 staying solid).
- **Verify:** tests 8.3-a/b, manual QA 8.7.
- **Result:** `getVisiblePlotPaint` uses `ColorUtil.seriesColor` when auto-color is on and the
  custom array does not cover that index. `PlotSeriesStyle.stroke` builds the enabled stroke
  (`1.5f`, `CAP_BUTT`, `JOIN_ROUND`, miter `10`, `seriesDash`, phase `0`) and
  `new BasicStroke(1.5f)` when styles are off. Each visible curve is one `Path2D` drawn once
  under the plot clip. Markers cycle circle, square, triangle, diamond, cross at 6 px. Histogram
  bars are filled and then drawn with the series stroke; histogram nodes stay gray.
  `Plot2DPanelAccessibilityTest.sixCurveDashAcceptance_8_3_a` and
  `denseDashAcceptance_8_3_b` (6 curves, 5,000 points) passed: series 1 background gaps are
  ≤12 px. On 2026-10-01, `sixCurveStyleNodeStepSizeMatrix` (400×300 and 800×600, styles,
  nodes, and step), `scientificMappingSurvivesStyleNodesStepHistogramAndClip`,
  `grayscaleKeepsSeriesOneDashGaps`, and `keyboardIsolationNamesSeriesWithoutPointer` also
  passed. Manual 8.7 was not run.

**2.2** `vcell-client/src/main/java/cbit/plot/gui/PlotPane.java` · `LineIcon` (`:55`) and
`updateLegend` (`:1349`) — ✅ automated checks done (2026-09-30)
- **Change:** `LineIcon(Paint, Stroke, int markerIndex)` draws the dashed sample and a centred
  marker (icon ≥ 50×12 px). `updateLegend` passes
  `getPlot2DPanel1().getVisiblePlotStroke(i)`.
- **Verify:** test 8.3-c.
- **Result:** `LineIcon` is a public static icon of at least 50×12, drawn with the series stroke
  and a centered marker. Legend clicks store the raw plot name and pass that to `setCurrentPlot`.
  `PlotPaneAccessibilityTest` passed: icon size, stroke equal to `getVisiblePlotStroke`, a
  click on the `s2` label selects model index 2, and Enter and Space on that focusable label
  do the same. The label's accessible name is the raw plot name.

**2.3** `vcell-client/src/main/java/cbit/plot/gui/Plot2DSettingsPanel.java` (+ `Plot2DSettings` bean)
(MUST) — ✅ automated checks done (2026-09-30)
- **Change:** add a "Vary line styles" checkbox, default **on**, beside the existing
  crosshair/nodes/snap checkboxes.
- **Rationale:** scientists can inspect undashed curves, and the accessible default stays on.
  Turning the checkbox off remains subject to the §15 C identity requirement in 2.1.
- **Result:** Checkbox text is `Vary line styles`, name `JCheckBoxVaryLineStyles`, default on.
  The bean property `varyLineStyles` is in `saveSettings` / `restoreSavedSettings` and does not
  fire when the value is unchanged. Setting the bean false before binding leaves the checkbox
  false. `Plot2DSettingsTest` and `Plot2DSettingsPanelTest` passed.

**2.4** `Plot2DPanel.pointerMoved` (`:2205`) (MUST) — ✅ automated checks done (2026-09-30)
- **Change:** prefix the status text with the current series name.
- **Keyboard:** Ctrl+N and Ctrl+P set the status label to the series name with no pointer
  event. Ctrl+I isolates the current series and sets the status to `name (only this series)`.
  A second Ctrl+I restores every visible series. Replacing the plot clears isolation.
- **Verify:** test 8.3-d.
- **Result:** A successful readout is `name + ": " + coordinates`. A blank name omits the prefix.
  `getGraphics()` is used only after the status text is set, and a null graphics object skips the
  crosshair. `pointerStatusNamesTheSeriesWithoutGraphics` passed (`s1: ...`).

**2.5** `vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java` (`:80`,
list renderer `:257`) (MUST) — ✅ automated checks done (2026-09-30)
- **Change:** replace `generateAutoColor` with `ColorUtil.seriesColor`, and show the style icon in
  the list, so its swatches stay in sync with 2.1.
- **Result:** Auto colors are `seriesColor(unsortedIndex)`. A caller-supplied color array replaces
  that palette. Selected rows use `createVisiblePlotIcon`; other rows use
  `createSeriesStyleIcon` and the tooltip `Not plotted; style shown for selection.`
  `MultisourcePlotPaneAccessibilityTest` passed. `rg generateAutoColor` on `Plot2DPanel.java` and
  `MultisourcePlotPane.java` returns no matches (8.4-b).

Command for the focused set, Java 17, headless, 2026-10-01: `mvn --batch-mode test -pl vcell-client -am -Dgroups=Fast -Djava.awt.headless=true -Dtest=ColorAccessibilityTest,Plot2DSettingsTest,PlotSeriesStyleTest,Plot2DPanelAccessibilityTest,PlotPaneAccessibilityTest,Plot2DSettingsPanelTest,MultisourcePlotPaneAccessibilityTest -Dsurefire.failIfNoSpecifiedTests=false`. `ColorAccessibilityTest`: 8 run, 0 failures, 1 skipped. `Plot2DSettingsTest`: 2 run, 0 failures. `Plot2DPanelAccessibilityTest`: 12 run, 0 failures, including the six-curve / two-size / styles / nodes / step matrix, the 8.8-c scientific-mapping check, the grayscale dash check, and keyboard isolation. `PlotPaneAccessibilityTest`: 2 run, 0 failures. One 100,000-point curve still paints in under 5 seconds inside `histogramAndLargeCurvePaint`.

Gate 8.1 on 2026-10-01, `mvn --batch-mode test -Dgroups=Fast -pl vcell-util,vcell-core,vcell-client -am -Djava.awt.headless=true`: `vcell-util` 50 run, 0 failures, 1 skipped. `vcell-core` 644 run, 0 failures, 10 errors, 26 skipped. Those 10 errors are the existing Python-environment set (`MathOverrideRoundTripTest` 7, `CopasiOptimizationSolverTest` 2, `VCellDataTest` 1), the same set that fails when the Poetry environments are absent. The reactor stopped before `vcell-client`; the focused client accessibility tests above passed in a separate run. No new plot-test failure.

### Phase 3 — Langevin plot framework (MUST for palette, styles, and accessible interaction)

Automated checks for 3.1 and 3.2 passed on 2026-10-01, re-run green on 2026-10-02 (V1: 11
tests, 0 failures; 8.4-a and the extended residue scan: no matches). Committed on
`chore/vcell#1605` in `fc8ebd3aea` together with the Phase 2 keyboard-isolation work (not
pushed; no PR yet). Evidence for the Langevin surfaces was produced on 2026-10-02 under
`docs/accessibility/evidence/2026-10-phase-3-langevin/`: capture set S1–S10 from a local
Langevin Quick Run of the `biomodel_315318780.vcml` fixture (`SimID_122317207_0`; `allosteric`
and `transition_free` rules disabled to work around an NPE in the bundled local solver; 1-job
batch, so SD/min-max envelopes are degenerate — recorded), Machado protan/deutan/tritan and
grayscale filters applied to all captures via the new `.agents/cvd_analysis.py image` mode
(self-checked; 60 filtered images). **Manual 8.7 (two reviewers on macOS, Windows, Linux) and
the 8.6-b human identification task (T1–T6 on the filtered images) are PENDING HUMAN REVIEW**
with prepared checklists in the evidence directory; the S2 legend focus-ring capture is BLOCKED
in the automated session (the raw-`java` client instance never receives AWT window activation;
see the evidence manifest) and remains a reviewer item (C6). Windows/Linux captures were not
produced (this session ran on macOS only). This phase does not close #1605 or the §9 / §16
gate, and **Phase 3 done ≠ #1605 closed ≠ §9/§16 gate passed**.

**Corrections after the capture session (2026-10-02).** The first legend correction is committed
in `9897dc37bf`. The shared legend wiring, the marker spacing, and their tests are in the working
tree on top of it (not committed). None of this is part of `fc8ebd3aea`.

- **The legend follows "Vary line styles".** `LineIcon` paints
  `AbstractPlotPanel.strokeForSeriesIndex`, the same method `AvgRenderer` and the empty-bubble
  outline use. `AbstractVisualizationPanel` keeps the plot it creates and registers the legend
  repaint on that plot, so both Langevin panels share one wiring.
  `LangevinLegendAccessibilityTest.legendIconBecomesSolidWhenStylesAreOff` builds the legend
  through that base class: series 1's icon is solid with styles off and dashed with styles on,
  and the legend repaints on each change. With the registration removed, the test fails.
- **Dense markers no longer hide the dash.** Nodes are on by default and the minimum marker is
  6 px. At Langevin output density (201 samples across about 400 px), a marker at every sample
  merged into a solid band: on S1 the horizontal runs of the dotted and dash-dot series measured
  0 px gaps. With lines on, `AvgRenderer` now leaves at least four marker diameters (24 px at
  the minimum) between markers, which shows more than one period of the longest dash (16 px).
  With lines off, the markers are the data and every sample keeps one. Samples, crosshair
  snapping, and the data view are unchanged; only marker density changes. In a six-series plot
  the pairs that share a dash (indices 2 and 5, 3 and 6) are told apart by marker shape.
  `PlotRenderersAccessibilityTest.denseSeriesWithNodesKeepsDashGapsBetweenMarkers` failed before
  the change (0 px gap) and passes after it.
- **Tests:** renderer 8, legend 3, identity 2; 0 failures. The Phase 1–2 set (V4) is unchanged:
  28 run, 0 failures, 1 expected skip.
- **Line-plot evidence is stale.** S1, S2, S3, S3a, S4, S6, S6b, and S10a predate both corrections.
  They and their filtered images must be recaptured from the commit that contains them. S1 as
  captured fails the style-identity condition (see the evidence manifest). S4a, S5, S7, S8, S8b,
  S9, and S10b are unaffected: with styles on, the legend stroke is unchanged, and the bubble and
  table views draw no markers.
- **Still open, not human work:** that recapture; S6 with ACO and SD plotted; a multi-series
  bubble fixture; the Quick Run post-processing helper (`langevin_postprocess_watch.sh`) and its
  log in the evidence set; push and PR. Single-color bubbles still have no product checkbox.
- **Human or environment work:** multi-job SD envelopes, the S2 focus ring, Windows/Linux
  captures, manual 8.7, and 8.6-b T1–T6.

**3.1** `vcell-client/src/main/java/cbit/vcell/solver/ode/gui/MoleculeVisualizationPanel.java`
(`:423`) and `ClusterVisualizationPanel.java` (`:206`)
- **Change:** `globalPalette` ← `ColorUtil.CVD_SAFE_LIGHT`; pass the stable full series index to
  `seriesDash` (never palette-slot division or modulo as the identity).
  Keep `deriveEnvelopeColor` for SD bands.
- **Result:** both `initializeGlobalPalette` methods assign `ColorUtil.CVD_SAFE_LIGHT`.
  `LangevinSeriesIdentity` stores the full assignment index and calls `ColorUtil.seriesColor`.
  SD still uses `deriveEnvelopeColor` and does not take a series index. A custom color already
  in the map is kept and still receives that full index. `LangevinSeriesIdentityTest` passed
  (ACS/ACO/SD slot rule, index 8 rather than palette slot 0, stable reassignment, custom color).

**3.2** `vcell-client/src/main/java/cbit/plot/gui/PlotRenderers.java` (`AvgRenderer`, `:90,:96`)
and `AbstractVisualizationPanel.LineIcon` (`:21`) (MUST)
- **Change:** apply `seriesDash`, and draw the dash in the legend icon. Band and bubble renderers
  must expose series identity and values through keyboard-accessible selection, labels, and the
  accessible data view; hover dimming alone is insufficient evidence.
- **§15 C also applies here:** the same repeated-pattern, styles-off, and custom-color identity
  rules as Phase 2. Test 8.3-e checks that two strokes differ; it does not close §15 C.
- **Verify:** test 8.3-e, then the §15 C series-count and styles-off checks.
- **Result:** `AvgRenderer` draws one `Path2D` with `strokeForSeriesIndex(seriesIndex)`, which is
  `PlotSeriesStyle.stroke(seriesIndex, varyLineStyles)`. With lines on, its markers are at least
  four diameters apart; with lines off, every sample has one. The legend icon is 80×12. It paints
  `strokeForSeriesIndex`, so a styles-on series uses `seriesDash` and a styles-off series is the
  solid `BasicStroke(1.5f)` the curve uses. Ctrl+N / Ctrl+P name
  the series and Ctrl+I isolates it while that plot has focus, so the molecule and cluster plots
  in one window do not both move. Legend labels accept Enter and Space. The data table accessible
  name is "Series data". Hover dimming remains, and keyboard selection is a separate cue.
  `PlotRenderersAccessibilityTest.avgRendererSeriesStrokesDiffer_8_3_e` passed (series 0 solid,
  series 1 dash, painted gap >0 and ≤12 px with nodes off). Series counts 1, 6, 8, 24, 25, and 30
  use `seriesDash(i)` for index `i`; series 6 does not reuse the palette-slot-0 dash; series 0 and
  24 share a dash and are still named separately. Styles off, overlapping custom colors, and
  hidden-series isolation passed. Band and bubble selection return the series name and values,
  including single-color bubbles. `grayscaleKeepsSeriesOneDashGaps` passed for the olive series
  after a luminance conversion. `LangevinLegendAccessibilityTest` passed. Static check 8.4-a:
  `rg -n -e "ColorUtil.TABLEAU20" -e "ColorUtil.DARK20" --glob "*VisualizationPanel.java"` under
  `vcell-client/src/main/java` returned no matches.
  Command, Java 17, headless, 2026-10-01: `mvn --batch-mode test -pl vcell-client -am -Dgroups=Fast -Djava.awt.headless=true -Dtest=PlotRenderersAccessibilityTest,LangevinSeriesIdentityTest,LangevinLegendAccessibilityTest -Dsurefire.failIfNoSpecifiedTests=false`.
  11 tests, 0 failures. Same command after the 2026-10-02 corrections above: 13 tests (renderer
  8, legend 3, identity 2), 0 failures. `denseSeriesWithNodesKeepsDashGapsBetweenMarkers` covers
  201 samples with nodes on, nodes-only mode, and sparse samples.

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
- **§15 C governs 4.1–4.3:** width is the non-color selection cue. Also required: a shape or label
  where width does not identify the state, adjacent-color contrast of at least 3:1 for the
  indicator that carries the state, keyboard and assistive-technology exposure of that state, and
  the D4 glyph-error and S8 red-text fixes in #2140. Keeping the red hues is acceptable when those
  checks pass.
- **Risk:** edge hit-testing is unaffected (it uses the curve geometry, not the stroke); verify
  arrowheads still align.
- **Verify:** test 8.3-f, manual QA 8.7, and the §15 C diagram checks.

**4.4** `vcell-client/src/main/java/cbit/vcell/geometry/gui/GeometryViewer.java` +
`vcell-client/src/main/java/cbit/image/gui/ImagePlaneManagerPanel.java`
- **Change:** give `ImagePlaneManagerPanel` an optional `IntFunction<String> indexLabelProvider`.
  `GeometryViewer.refreshSourceDataInfo` (`:245`) sets it to
  `handle -> subVolumeForHandle(handle).getName()`. `updateInfo` (`:1437`) appends
  `" \"" + name + "\""` for `SourceDataInfo.INDEX_TYPE` data.
- **Rationale:** 1.4.1: compartment identity is readable without hue. This mirrors how the results
  viewer already names compartments.
- **§15 C governs 4.4:** the hover name is required. G1/G2 also require an accessible region list
  and a keyboard-selected readout, so identification does not depend on hover, plus measured
  palette and boundary contrast (#2139).
- **Risk:** low for the added hover text. The region list and keyboard readout are additional work.
- **Verify:** test 8.3-g, then the §15 C no-hover identification check.

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
- **Why:** it protects requests reaching an upgraded server. A change in the new server cannot
  modify an already deployed old server. Before exposing Cividis, implement and test client-side
  capability negotiation or gate on an upgraded server; otherwise warn and send a supported ID.
  Never silently mislabel exported colors. Test the actual mixed-version deployment.
- **Deploy order:** export server (`vcell-server`/data service) **before** a client that exposes
  Cividis. Deployment requires the normal release authorization; see §10 Q5.

**5.4** Default colormap
- **Change:** keep **BlueRed as the default** (`PDEDataContextPanel.java:1099`,
  `KymographPanel.java:2292`) unless §10 Q3 records an approved change.
- **Rationale:** changing the default changes every user's figures and movies.
- **Acceptance:** a retained BlueRed default still has to pass every applicable criterion. §15 C
  limits the heat-map essential-presentation exception to a measurement gradient that meets the
  exception's conditions. Axes, legends, controls, focus, selection, NaN and out-of-range states,
  and numeric access stay in scope. A failing default blocks closure.

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
- **§15 C and E govern Phase 6:** the Phase 2 identity rules apply when a dash pattern repeats or
  a style cannot be shown. These color and contrast edits cover the field viewer's color findings.
  The remaining §13 rows for this web surface are §15 E.

**6.2** `webapp-viewer/index.html`
- **Change:** replace `#2a7` text/pressed background with a color ≥4.5:1 (for example `#157347`,
  5.87:1 on white). `.stale` background ≥4.5:1 with white text. `.status.err` gets a dark-scheme
  override ≥4.5:1 (for example `#ff6b6b`, 6.75:1 on `#121212`), using `@media
  (prefers-color-scheme: dark)`.
- **Verify:** test 8.5 (computed on source colors).

### Phase 7 — Text contrast and wording (MUST, #1605)

**7.1** `vcell-core/src/main/java/cbit/vcell/client/constants/GuiConstants.java`
- **Change:** add `ERROR_TEXT_COLOR = new Color(0xA4,0,0)` (8.14:1 on white, 6.98:1 on the
  alternating row `#e8edff`, 7.81:1 on the hover row `#FDFCDC`) and
  `WARNING_TEXT_COLOR = new Color(0x8A,0x4B,0)` (6.80, 5.83 and 6.52:1).
- **Change:** use them in `MathOverridesTableCellRenderer.java:54` and
  `SimulationConsolePanel.java:127-143`. The console also prepends `[Error] `, `[Warning] ` and
  `[Stopped] `.
- **§15 C governs 7.1:** these constants are shared tokens. Apply them to every S8 renderer and
  console, with textual severity and programmatic status, including selected backgrounds. The two
  call sites above are the first consumers, not the full set (#2140).
- **Verify:** tests 8.2-d, 8.3-h, then every remaining S8 consumer.

**7.2** Help text (`vcell-client/UserDocumentation/originalXML/...`, H1 list)
- **Change:** name the non-color cue. For example, "changed values appear in red **and have an
  entry in the Override column**"; "Blue-Red, Gray or Cividis color map". Rebuild the help per the
  repo's doc tooling.
- **§15 F governs 7.2:** reword every sensory-only instruction, then rebuild and verify the shipped
  JavaHelp and HTML. The H1 examples are the seed list. §14 discovery can add further pages.
- **Verify:** `rg -n -i "appear(s)? in red|in green|in red" vcell-client/UserDocumentation` returns
  only reworded lines, and the shipped help matches §15 F.

### Color work required by §15 C and not completed by Phases 0–7

These items are #1605 blockers when a color failure exists. Phases 0–7 do not implement them.
The issue numbers are ownership. They do not move the work out of #1605.

| §15 C item | Issue | Required beyond Phases 0–7 |
|---|---|---|
| Repeated dash patterns; custom colors; other frameworks | P1–P5, W1 | Phase 2 and Phase 3 isolate the current legacy or Langevin series from the keyboard when styles are off or the 8-slot dash cycle repeats. The same identity rules for W1 are still required |
| D4 glyph errors and S8 red status text | #2140 | Shape or label cues, and the shared error/warning tokens on every affected renderer |
| G2 palette and keyboard region list | #2139 | Region list and keyboard readout. The hover name in 4.4 is the first cue |
| X1 SpringSaLaD | #2132 | Presentation-only labels, outlines, or patterns, and keyboard site isolation. Persisted scientific colors stay unchanged |
| X2 ROI editor | #2133 | Named ROI list, non-color boundaries, and keyboard editing |
| X3 FRAP table | #2134 | Text contrast and accessible cell semantics on every row state |
| S5 validation | #2136 | Persistent accessible error text where recovery needs it |
| S6 constraints UI | #2137 | Shipped-artifact reachability proof, or remediation |
| S7 match-row highlight | #2138 | Match label or icon, and accessible cell state, including selected rows |
| X4 webapp-ng | #2135 | Badge contrast, the UConn accessibility link, and the §15 E route audit |
| C1–C4 numeric and special states | — | Keyboard and programmatic value access, and special-state cues, on the client and the export server |

Keyboard, focus, names and roles, reflow, media, input, and the remaining §13 rows are §§15 B, D,
E, and F. Those packages are mandatory for the full WCAG conformance gate.

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
| 8.2-c | same | Each special state is distinguishable by label/pattern and accessible value/state readout; required graphical indicators pass ≥3:1 against actual adjacent colors. Test endpoints and interior neighbors | State ambiguous, required contrast fails, or exemption lacks criterion-specific evidence |
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
| 8.4-a | `rg -n -e "ColorUtil.TABLEAU20" -e "ColorUtil.DARK20" --glob "*VisualizationPanel.java"` | No remaining palette references in production visualization panels | Any match |
| 8.4-b | `rg -n "generateAutoColor" vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java` | No matches | Any match |
| 8.4-c | `rg -n "createBlueRedColorModel\(\), *DisplayAdapterService\.createBlueRedSpecialColors" --type java` | Only inside `addStandardColorModels` | Any other hand-registration site |
| 8.4-d | Search both affected files for `Color.red` and `Color.RED`; inspect all remaining foreground assignments | No inaccessible red text assignments remain | Any failing foreground/background combination |

Treat `rg` exit code 1 as the expected no-match result for negative scans; any command error is
BLOCKED, not PASS. Static scans supplement runtime validation and cannot prove the absence of all
color failures.

### Contrast Verification

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.5 | Re-run `.agents/cvd_analysis.py` (extended with the final hex values) for every changed text/background pair in `webapp-viewer/index.html` and the Java constants | Text ≥4.5:1 (≥3:1 only for ≥18 pt / 14 pt bold); non-text indicators ≥3:1 | Below threshold |

Contrast tooling alone doesn't validate CVD accessibility: 1.4.1 is checked by 8.3 and 8.6.
Test 8.5 is the changed-pair regression subset; full release requires all applicable pairs/states
in §14, including retained defaults and custom/theme backgrounds, under §§15–16.

### Color-Vision-Deficiency Verification

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.6-a | Automated: 8.2-a metric on the final palettes (every supported light/dark background; #1604 delivery is a release dependency) | Thresholds met | Not met |
| 8.6-b | Screenshot simulation: capture via `tools/debug-bridge` (ODE plot with 6+ species, PDE time plot, reaction diagram with a species selected, geometry viewer, spatial viewer with Cividis, field viewer with 3 probes); apply Machado protan/deutan/tritan and grayscale filters (extend the script with Pillow) | In every filtered image, a reviewer can match each legend entry to its curve by style, identify the selected edges by width, and name a geometry region from the readout | Any mapping needs hue |

### Manual Visual QA

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.7-a | Two reviewers (one ideally with CVD, otherwise using Sim Daltonism / Color Oracle) run a scripted checklist on macOS, Windows and Linux | Checklist items all "yes"; screenshots attached to the PR | Any "no" |
| 8.7-b | Windows forced/high-contrast and each supported theme across all inventoried surfaces | Controls, focus, selection, text, and data remain operable and distinguishable | Any loss; logging alone is not a pass |

### Scientific Visualization Regression Testing

| ID | Check | PASS | FAIL |
|---|---|---|---|
| 8.8-a | Default BlueRed output: render a fixed `SourceDataInfo` through `DisplayAdapterService` before and after | Identical pixel arrays (no default change) | Any diff |
| 8.8-b | Real export server: BlueRed before/after, Cividis with upgraded server, new client with old server | BlueRed identical; Cividis succeeds when supported; unsupported server is detected client-side and a supported mode offered with an explicit notice | Exception, incorrect labels, silent palette substitution or untested mixed versions |
| 8.8-c | Compare fixed plots to a pinned pre-change commit with styles/nodes on and off, linear/log/step/histogram and clipping | Scientific values, transforms, axes, extrema and sample/series mapping unchanged; reviewed raster differences limited to documented paths/joins/markers/outlines | Lost/reordered data, shifted coordinates, hidden extrema or unexplained raster difference |
| 8.8-d | `SmoldynFileWriter` golden output (existing tests / MathGen regression) | Unchanged | Changed (means `generateAutoColor` was touched) |
| 8.8-e | Narrow-spike check: a 1-sample spike on a dashed series is visible with styles on, or with the toggle off | Visible in at least one mode, and the toggle documented | Spike invisible in both |

---

## 9. Definition of Done — mandatory release gates

No checkbox is pre-completed for the expanded 2026-10-01 scope. Prior unit-test passes are supporting
historical evidence only. Test omissions must be recorded as BLOCKED/NOT TESTED, never PASS.

### #1605 — complete color-accessibility evaluation and remediation

- [ ] The §14 inventory accounts for every shipped GUI view, menu, dialog, renderer, workflow and
      state, including resources, generated content, inherited platform colors and dependencies.
      Each entry maps to applicable color criteria, tests and evidence; no undispositioned entries.
- [ ] P1–P5, D1–D4, G1–G2, S1–S8, W1, X1–X5, H1 and C1–C4 are mapped using the §7 Phase 0
      ID crosswalk. Existing passing findings are reverified. Dead-code claims have reachability
      evidence from the shipped artifact. Palette enhancements and standards exceptions are labeled
      distinctly from failures; no unverified blanket heat-map exemption.
- [ ] All actual color/contrast/sensory-cue failures are fixed, including work tracked by
      #2132–#2140 and the color-dependent portions of #1604/#1606. An open unrelated cleanup task
      may remain only if the evidence proves it contains no unresolved #1605 requirement.
- [ ] Both plot frameworks, every consumer, field-viewer traces, histograms, SD bands and bubble
      plots support unambiguous identity without hue for all supported data sizes. Required labels,
      markers or keyboard-accessible isolation survive style toggles and custom colors.
- [ ] Selected diagram edges/nodes, error glyphs, geometry/ROI regions, SpringSaLaD sites,
      FRAP states and all table/status/validation decorations have tested non-color cues.
- [ ] All text/background and required non-text/adjacent-color pairs pass SC 1.4.3/1.4.11 in
      supported themes and states; S2/S3/H1 and all help-page corrections are mandatory.
- [ ] Numeric/state/region/series alternatives work without a mouse; CVD simulation and grayscale
      reviewers can complete the same identification tasks. No reliance on screenshots alone.
- [ ] Relevant §8 tests, §15 work-package tests, §16 platform/theme evidence, and scientific
      regressions pass on the release candidate. 8.6-b's six scenarios are a minimum, not coverage
      of the whole application. Every additional inventory family has recorded representative
      runtime coverage, with shared-renderer equivalence justified per consumer/state.
- [ ] The expanded color audit and fixes are merged and independently reviewed. Issue owners
      receive a criterion-to-evidence closure report showing zero unresolved #1605 failures.
      Closure is based on these gates, never merely on filing follow-up issues.

### Full VCell accessibility — WCAG 2.2 AA plus UConn 2.1 AA

- [ ] §13 has one row per criterion per applicable surface/environment: PASS or justified N/A,
      with reviewer, release SHA, execution date, test and evidence. No FAIL/BLOCKED/NOT TESTED.
- [ ] All §14 user-facing release surfaces and complete processes are covered, including web
      authentication dependencies, desktop custom components, help and generated outputs.
- [ ] All §15 work packages and §16 gates pass, including keyboard, assistive technology, scaling,
      focus, media, input, timing and error prevention; color tests alone cannot satisfy this gate.
- [ ] The WCAG conformance requirements for full pages, complete processes, accessibility-supported
      technologies and non-interference are evidenced for web surfaces. Document WCAG2ICT
      interpretation and platform accessibility support for desktop/software/document evaluations.
- [ ] #1604/#1606 and every other conformance dependency have delivered the applicable verified
      behavior. A disabled dark-mode hook or an approved deferral does not fulfill a dependency.
- [ ] All supported light/dark/high-contrast combinations and scaling modes pass; inaccessible
      user customization can always be reset through an accessible, persistent control.
- [ ] UConn-specific website link, document/media and other applicable procedure obligations are
      mapped and satisfied in the institutional ledger (§14). WCAG results are not a legal opinion.
- [ ] Independent accessibility review validates the evidence, including users of assistive
      technology/CVD participants where available; simulations supplement rather than replace it.
- [ ] A release-specific conformance report and accessibility statement identify exact versions,
      scope, technologies, tested combinations, criteria and limitations. No unresolved applicable
      failure is described as conformance. Only then may the full claim and #1603 closure be made.

---

## 10. Decisions, dependencies and unresolved work

| ID | Decision / dependency | Required disposition | Blocks |
|---|---|---|---|
| Q1 | #1605 scope | Resolved by this revision: all GUI areas evaluated; all applicable color failures fixed. Record this contract on the issue during implementation | #1605 until §9 passes |
| Q2 | Desktop interpretation / institutional review | Use all applicable 2.1/2.2 A/AA criteria through WCAG2ICT now; document criterion-specific interpretation. Obtain institutional clarification where needed without narrowing scope silently | Any unresolved applicability or institutional requirement blocks full claim |
| Q3 | Cividis default | Keep scientific defaults unless an approved change is documented; offer an accessible option and equivalent data access. Test every retained default; replace/remediate one that fails an applicable criterion | Any failing default blocks closure |
| Q4 | Special colors | Implement non-color state cues plus actual adjacent-color checks; essential-gradient exception does not automatically cover NaN/selection/controls. No waiver-as-PASS | Color and conformance gates |
| Q5 | Export deployment compatibility | Test upgraded server and real old-server/client negotiation or gating before exposing Cividis; no claim that new server code repairs an old server | Cividis enablement/export gate |
| Q6 | Themes/fonts | Implement #1604/#1606 work under §15; test actual supported OS themes, font scaling and high contrast | Full release and affected #1605 checks |
| Q7 | Cividis attribution | Verify upstream data license and include required attribution before distribution | Shipping affected data |
| Q8 | Rendering fidelity/performance | Replace incompatible channel-only raster assertion with revised 8.8-c; benchmark pinned datasets and document performance budgets before implementation | Scientific acceptance |
| Q9 | Platform/AT support | Record real OS/JDK/browser/AT versions and supported bridges; add missing semantics or functional accessible alternatives. Unsupported combinations are limitations, never silent N/A | Full claim |
| Q10 | Unknown surfaces/criteria | Phase A discovery assigns a named implementer and reviewer to every row; discovery remains open as new views are found | Inventory and release gate |

---

## 11. Historical Gemini Plan Improvements (2026-09-30)

Retained as provenance. The 2026-09-30 cuts below are not the current scope. §§7, 9–10, and
§§13–16 govern, and §15 restores the items this section once called follow-ups.

- **Corrected**
  - The standards hierarchy: UConn color rules are guidance; WCAG 2.1 AA is binding through the
    Procedures for web/mobile; 508 applies to procurement only.
  - WCAG edition, and six contrast figures.
  - F-07, F-09 and F-14 verdicts. F-08 (`ConstraintPanel`) was unreachable in the 2026-09-30
    search; §15 S6 / #2137 still requires a shipped-artifact reachability proof or remediation.
  - The `COLORBLIND20` "accessible" claim.
  - The "strict requirement" labels.
  - Gemini's failing replacement color (`#e65100`).
- **Expanded**
  - Measured CVD/contrast evidence for every palette.
  - The server-side export dependency and fallback.
  - Deterministic colormap ordering.
  - Special-color problem for new colormaps.
  - Test design with pass/fail thresholds.
- **Historical cuts (2026-09-30) and the current requirement**
  - `ConstraintTableCellRenderer`: treated as unreachable. §15 S6 / #2137 requires a
    shipped-artifact reachability proof or remediation.
  - StatusIcon redesign: still unnecessary. Icon plus status text meets 1.4.1 (S4), subject to
    §9 re-verification.
  - Red-border replacement: a text dialog already exists. §15 S5 / #2136 still requires persistent
    accessible error text where recovery needs it.
  - webapp-ng badge/footer and SpringSaLaD shapes: called FOLLOW-UP on 2026-09-30. That exclusion
    is withdrawn. §15 C requires #2135 and #2132.
  - `axe-core` on Swing: still inapplicable to Swing. §15 E requires a repeatable accessibility
    scan on `webapp-ng` and `webapp-viewer`.
  - A new desktop colormap selector: still unnecessary, because the desktop selector is already
    dynamic. Phase 5.5 still adds the field-viewer selector.
  - REC-04 global theme setting: outside the color-only slice of #1605. §15 B requires the #1604
    theme and contrast work for the WCAG gate.
- **Reprioritized**
  - Legacy plot line styles remain the first color change. Cividis and the shared colormap
    registration remain required (§15 C1–C4). The heat-map essential-presentation exception covers
    only a measurement gradient that meets its conditions. Axes, legends, controls, focus,
    selection, NaN and out-of-range states, and numeric access stay in scope.
  - BlueRed stays the default only while it passes applicable criteria (§10 Q3). A failing default
    is remediated before closure.
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

## 12. Execution order and evidence policy

1. **A — Discover and baseline:** complete §14 inventory, populate §13 matrix, reconcile historical
   findings, pin release/fixture commits, assign implementer/reviewer and establish baseline tests.
2. **B — Shared accessibility foundations:** implement theme/contrast tokens, font/layout scaling,
   accessible component contracts, keyboard/focus behavior and reusable plot identity controls.
3. **C — Complete color work:** finish §7 Phases 1–7 and §15 C work packages, including all previously
   deferred failures. Preserve scientific inputs and model serialization while changing presentation.
4. **D — Full desktop accessibility:** execute §15 D across every inventory family and workflow;
   custom graphics receive real accessible semantics and equivalent keyboard operations.
5. **E — Full web accessibility:** execute §15 E for both web interfaces and every other shipped
   browser surface, including login, errors, dialogs and visualization controls.
6. **F — Help, media and generated outputs:** execute §15 F, rebuilding and verifying distributed
   artifacts rather than merely editing their source text.
7. **G — Release validation:** execute §16 on the release candidate; remediate and rerun failed
   checks and impacted regressions. Refresh evidence after any change that invalidates it.
8. **H — Closure/reporting:** independently review and merge the evidence; close #1605 only on its
   §9 gates, and make a full conformance claim/close #1603 only on all full-project gates.

Artifacts to create during implementation (not created by this planning edit):
`docs/accessibility/surface-inventory.md`, `wcag-matrix.csv`, `color-audit.md`,
`institutional-requirements.md`, `test-protocols.md`, `support-matrix.md`, and
`conformance-report.md`. Store versioned logs, screenshots, accessibility-tree captures and manual
results under an evidence directory or durable linked CI artifacts, with no credentials/user data.
Each finding needs owner, criterion, surface/state, reproduction, proposed fix, test, evidence,
status, reviewer and dependency. Unknown owners/statuses block readiness; project managers assign
names before work starts. Parallel delivery through other issues is allowed; closure requirements
remain unchanged.

## 13. Complete criterion coverage matrix

**Target:** WCAG 2.2 AA and UConn's WCAG 2.1 AA baseline. The table includes all 50 A/AA entries
traditionally listed for 2.1 (including the updated treatment of 4.1.1), plus all six new 2.2 A/AA
criteria. The normative criterion and its exceptions govern; the VCell actions below are engineering
work assignments, not replacements for the standard. AAA is outside this defined conformance level.

Primary sources, checked 2026-10-01:
- [WCAG 2.1, 2025 Recommendation](https://www.w3.org/TR/2025/REC-WCAG21-20250506/)
- [WCAG 2.2, 2024 Recommendation](https://www.w3.org/TR/2024/REC-WCAG22-20241212/)
- [WCAG2ICT](https://www.w3.org/TR/wcag2ict-22/) (record the dated version used in the release report)
- [UConn policy](https://policy.uconn.edu/2019/08/02/digital-accessibility-policy/)
- [UConn procedures](https://accessibility.its.uconn.edu/policy-procedures/)
- [UConn color guidance](https://accessibility.its.uconn.edu/self-paced-learning/colors/)

For **each** row, expand `wcag-matrix.csv` by surface, state and support-matrix environment.
Initial status is **NOT TESTED**. Allowed final outcomes: PASS with evidence, or N/A with concrete
feature/reachability/criterion-exception evidence and independent review. FAIL and BLOCKED are
non-passing. A criterion cannot be N/A just because implementation is difficult, a feature uses
canvas/Swing, testing equipment is unavailable, or another issue owns the work. Test failure history
must remain visible after remediation. Apply WCAG2ICT's actual substitutions/notes, not web CSS
assumptions, to native components and non-web documents; retain equivalent platform test evidence.

Work packages B–F are defined in §15. Each row's evidence includes both automated checks where useful
and manual behavior checks; an automated accessibility scanner does not decide conformance.

| SC | Level | VCell implementation and acceptance evidence | Work |
|---|---|---|---|
| 1.1.1 | A | Give meaningful diagrams, plots, icons and images accessible descriptions/data; verify tasks through the accessibility tree and equivalent data controls | C,D,E,F |
| 1.2.1 | A | Inventory shipped silent movies/audio; supply usable equivalents and test the delivered media package | F |
| 1.2.2 | A | Caption instructional recordings; review timing and scientific vocabulary against the audio | F |
| 1.2.3 | A | Explain visual-only steps in tutorials through narration or an equivalent media alternative | F |
| 1.2.4 | AA | Evaluate any project-operated live media and verify live caption provision; absence needs inventory evidence | F |
| 1.2.5 | AA | Add descriptions for significant visual information in recorded synchronized media; a transcript alone does not automatically satisfy this row | F |
| 1.3.1 | A | Expose table headers, form groups, trees, relationships and plot/data associations programmatically | D,E,F |
| 1.3.2 | A | Verify reading sequence in split panes, dialogs, generated help and responsive pages | D,E,F |
| 1.3.3 | A | Replace hue/location/shape-only instructions throughout help and UI; follow them without the referenced sensory cue | C,D,E,F |
| 1.3.4 | AA | Exercise supported web/mobile orientations and document any essential scientific-view exception specifically | E,F |
| 1.3.5 | AA | Mark supported personal-data input purposes in account forms; inspect browser/AT recognition | E |
| 1.4.1 | A | Identify all states/series/regions/sites without hue; test toggles, repeated styles and custom palettes | C,D,E,F |
| 1.4.2 | A | Check launches, embedded media and notifications for unsolicited audio and usable audio controls | D,E,F |
| 1.4.3 | AA | Measure every meaningful text/state/background combination, including selected rows and dark themes | B,C,D,E,F |
| 1.4.4 | AA | Use 200% text size without lost controls or data; desktop font changes must not require lowering display resolution | B,D,E,F |
| 1.4.5 | AA | Replace rasterized UI/help wording with real text unless a documented criterion exception applies | D,E,F |
| 1.4.10 | AA | Test web content at 320 CSS px width / 256 CSS px height as applicable; document essential 2D scientific regions while surrounding controls reflow; apply WCAG2ICT to desktop | B,D,E,F |
| 1.4.11 | AA | Measure required controls, focus/state cues and graphics against their actual adjacent colors | B,C,D,E |
| 1.4.12 | AA | Apply web text-spacing overrides (1.5 line height, 2 paragraph spacing, .12em letters, .16em words); evaluate native applicability through WCAG2ICT | B,D,E,F |
| 1.4.13 | AA | Exercise tooltips, legends and popovers by pointer and keyboard, including dismissal and continued reading | D,E |
| 2.1.1 | A | Complete model editing, simulation and results tasks through keyboard operations, including custom canvases | D,E |
| 2.1.2 | A | Enter and leave every pane, embedded viewer and dialog using platform keyboard conventions | D,E |
| 2.1.4 | A | Audit single-character graph/viewer shortcuts; scope, disable or remap them and test with dictation | D,E |
| 2.2.1 | A | Test authentication/session and dialog time limits; support extension/recovery and document genuine exceptions | D,E |
| 2.2.2 | A | Pause/stop updating visualization and moving content where required without losing controls | D,E,F |
| 2.3.1 | A | Inspect animation, playback and rapid status changes for flashing; use measured analysis when uncertain | D,E,F |
| 2.4.1 | A | Provide bypass navigation for repeated web regions; evaluate native equivalents/applicability explicitly | D,E,F |
| 2.4.2 | A | Verify distinct, meaningful page/document titles, including errors, result instances and help | D,E,F |
| 2.4.3 | A | Test focus progression, modal entry/exit and restoration after updates and deletion | D,E |
| 2.4.4 | A | Review links in context, including exported reports and repeated model/result links | D,E,F |
| 2.4.5 | AA | Provide appropriate navigation/search alternatives for web/help page sets; document process-step exceptions | E,F |
| 2.4.6 | AA | Check headings and control labels with representative modeling tasks and accessible names | D,E,F |
| 2.4.7 | AA | Track visible focus through all controls and custom drawing surfaces in every supported theme | B,D,E |
| 2.5.1 | A | Add simple pointer controls for pan/zoom and other multipoint/path gestures where required | D,E |
| 2.5.2 | A | Test cancellation/undo behavior for pointer actions, including geometry editing and destructive controls | D,E |
| 2.5.3 | A | Match accessible names to visible control wording and verify speech targeting | D,E |
| 2.5.4 | A | Audit device-motion features; provide conventional controls and disablement if present | D,E |
| 3.1.1 | A | Set document/UI language metadata or platform equivalent and verify pronunciation | D,E,F |
| 3.1.2 | AA | Mark language changes in help/web/documents where supported and required | D,E,F |
| 3.2.1 | A | Focus each control without unexpectedly launching, submitting or navigating | D,E |
| 3.2.2 | A | Changing inputs does not unexpectedly alter context; disclose necessary behavior before use | D,E |
| 3.2.3 | AA | Keep repeated navigation consistent across applicable page/document sets | E,F |
| 3.2.4 | AA | Use consistent names/icons for shared operations across model and results views | B,D,E,F |
| 3.3.1 | A | Trigger each validation failure; identify the field and problem in text and accessible feedback | D,E |
| 3.3.2 | A | Make units, required fields, format constraints and instructions available before submission | D,E |
| 3.3.3 | AA | Provide actionable corrections for invalid expressions, values and account inputs where known | D,E |
| 3.3.4 | AA | Test confirmation, review or reversal for deletion and modification of stored user models/data and other qualifying actions | D,E |
| 4.1.1 | A (2.1 record) | Record the 2025 2.1 note: always satisfied for HTML/XML; removed in 2.2. Evaluate other cases with the applicable guidance. Still test real semantic failures under 1.3.1/4.1.2, not as waived parsing errors | D,E,F |
| 4.1.2 | A | Inspect names, roles, states, values and actions of every custom component with actual assistive technology | D,E |
| 4.1.3 | AA | Announce simulation progress, validation, stale data and completion without forcing focus changes | D,E |
| 2.4.11 | AA (2.2) | Verify focused controls are not entirely hidden by author-created overlays, sticky panes or dialogs | D,E |
| 2.5.7 | AA (2.2) | Provide click/tap alternatives for dragging objects, sliders and splitters where required; keyboard alone does not satisfy the pointer alternative | D,E |
| 2.5.8 | AA (2.2) | Measure web pointer targets against 24 CSS px sizing/spacing or an actual criterion exception; document native interpretation | D,E |
| 3.2.6 | A (2.2) | Keep repeated help/contact mechanisms in consistent relative order | D,E,F |
| 3.3.7 | A (2.2) | Reuse information already entered during a process unless a documented exception applies | D,E |
| 3.3.8 | AA (2.2) | Test login/recovery with password managers and paste; resolve cognitive-test barriers and provider dependencies | D,E |

Conformance also requires **full pages and complete processes**, accessibility-supported technology
use, and non-interference. A passing component sample does not prove its containing page/workflow.
Third-party login/widgets and embedded views cannot simply be ignored. If using a conforming
alternate version, establish every formal requirement, equivalent information/function, accessible
discovery, synchronized updates and complete-process coverage. A CSV download, static screenshot,
hover label or telephone contact is not automatically a conforming alternate version.

## 14. Complete project and GUI inventory

The release boundary includes **all user-facing VCell functionality and VCell-maintained content
shipped, hosted or distributed with the assessed release**. Inventory actual hosts/routes/packages,
not just repository directories. Record externally maintained dependencies used in complete
processes (including hosted authentication). Pure computation/internal APIs have no visual UI to
assess, but their user-facing errors, generated outputs and integration workflows are included.
A service's lack of GUI is an evidenced applicability decision, not an exclusion for its UI clients.
Do not claim coverage of unknown versions, future features or unsupported deployments.

| Inventory family | Discovery anchors | Required states and workflows |
|---|---|---|
| Desktop shell | `vcell-client`, `VCellLookAndFeel`, shared Swing widgets | Startup/login, menus/context menus, toolbar, preferences, tabs, dialogs, file pickers, help, disconnected/error/empty/busy states |
| Modeling | `cbit/vcell/graph`, geometry and biomodel editors | Create/open/edit/save model; reaction/rule/species/pathway diagrams, parameter tables, geometry/CSG/structure mapping, import and validation |
| Simulation | Solver settings, override tables, console, job/status views | Configure/validate/submit/cancel/retry jobs, notifications and connection/session recovery |
| Results | Both plot frameworks and all consumers | ODE/PDE, dense and sparse data, parameter estimation, Langevin, spatial/kymograph, legend interactions, selectors, scales, SD bands, histogram, bubble and table views |
| Specialist desktop | SpringSaLaD, ROI editor, FRAP/vmicro | Site identity, image painting, region selection, analysis warnings and non-identifiable results |
| Browser field viewer | `webapp-viewer` | Launch from desktop; 3D controls, probes, stats, kymograph, selection, loading/error/stale states and both color schemes |
| Web application | `webapp-ng` and every shipped browser UI found in discovery | All public/authenticated routes, navigation, publications, model/data management, forms, badges, modal dialogs, account/login/logout/recovery |
| Supporting tools | `tools/geometry-server`, `tools/vtk-wasm`, other distributed user tools | Record whether each is shipped/user-facing; assess exposed pages, controls and outputs when it is |
| Documentation/media | `vcell-client/UserDocumentation`, maintained web help/tutorials, bundled documents | Navigation, search, links, diagrams, rebuilt JavaHelp/HTML, downloadable documents, recordings and captions |
| Generated artifacts | Export server, image/movie/report/data export paths | Equivalent accessible descriptions/data, meaningful labels, special states, metadata and accessible output-selection workflow |
| Platform/integration | OS dialogs, embedded libraries, hosted identity provider | Actual supported keyboard/AT interoperability and complete-process transitions |

Discovery method (Phase A):
1. Combine route/menu/dialog entry-point enumeration, runtime walkthroughs and source/resource
   searches for renderers, hard-coded colors, images, CSS, validation, focus, accessible contexts,
   generated forms and localization. The historical 77 chromatic-color files are a seed only;
   inherited colors and raster/SVG resources require separate coverage.
2. Map shared components to **all** consumers, including optional/plugin paths and feature flags.
   Prove unreachable/dead-code dispositions against build packaging and runtime entry points.
3. Exercise normal, hover, keyboard focus, selected, disabled, error, success, loading, stale,
   empty, readonly, custom-theme and scaled-text states wherever they exist. Record justified
   absent states; never infer all states from one default screenshot.
4. Create representative scientific fixtures for every family and inspect every unique component
   and workflow. Shared-test reuse needs documented equivalence, not an assumed blanket pass.
5. Track newly found surfaces and defects until inventory and matrix have no unowned/unresolved
   items. No inventory row can disappear merely to obtain a passing completion percentage.

Institutional ledger: map the live UConn policy/procedures to the actual project surfaces, including
website accessibility links, accessible documents/instructional media, procurement obligations if
purchased components apply, ownership and reporting processes. Obtain institutional interpretation
where applicable. Record verified sources and retrieval dates; the earlier inaccessible UConn Health
page and historical regulatory dates are not independent proof. Temporary institutional exceptions
or alternative-access arrangements must be disclosed and do not establish WCAG conformance.

## 15. Implementation work packages beyond the existing color phases

### A — Baseline and traceability

Create §12 artifacts and execute §14 discovery. Assign a named implementer and reviewer to every
work item, retaining issue IDs for ownership. Re-run historical tests on the selected commit before
reusing evidence. Pin baseline screenshots, source data, JDK/browser versions, OS themes and the
scientific reference commit. Publish failing tests as baseline findings, not acceptable residual
failures for applicable accessibility requirements. Define measurable latency/paint budgets from
baseline datasets before renderer changes.

### B — Shared presentation and interaction foundation

Implement reusable semantic theme tokens, contrast-safe state variants, visible focus and scalable
font/layout behavior. Trace tokens into custom Java painting, Swing renderers, SVG/canvas and CSS.
Replace clipped fixed-size layouts and text-as-image controls where needed. Integrate #1604/#1606
rather than waiting on them indefinitely. Persist accessible display preferences, respond correctly
to supported theme/contrast changes and provide an accessible reset. Inspect disabled/decorative
exceptions individually; readonly meaningful text is not automatically exempt.

Contract tests cover text at 100%/200%, theme changes, actual background/alpha compositing and
normal/selected/focused/error/hover combinations. Do not round a below-threshold contrast value up
to PASS. The categorical palette metric is an engineering aid, not a WCAG success criterion.

### C — Finish every color work package (all #1605 blockers when a failure exists)

| Work item | Required implementation | Verification |
|---|---|---|
| P1–P5 / W1 | Stable series identity across filtering/reordering and all plot frameworks; expose direct labels/markers and keyboard-accessible series isolation/data access | Exercise 1, 6, 8, 24, 25 and larger realistic series counts; no identity collision left dependent on hue |
| Repeated dash patterns | The existing eight-slot dash cycle is a primitive, not the identity guarantee. Add another non-color discriminator or accessible selection/isolation when patterns repeat | Match **every** displayed curve to its name with grayscale/CVD simulation; test nodes off, styles off, custom colors, overlaps and hidden series |
| D1/D2/D4 and #2140 | Width/shape/label cues for selected edges, selected neighbors and glyph errors; fix all newly found red status text | Both reaction/rule diagrams, arrowheads, dense diagrams, error and selection co-occurrence; keyboard/AT state inspection |
| G1/G2 and #2139 | Region names/handles plus accessible region list and keyboard-selected readout; measured palette/boundaries | Identify every region with no hue and no hover dependency; contrast-check meaningful boundaries |
| X1 / #2132 | Presentation-only labels/outlines/patterns and keyboard site selection/isolation in SpringSaLaD; preserve persisted scientific colors | Distinguish sites under CVD/grayscale; unchanged model serialization and solver input |
| X2 / #2133 | Named ROI list, non-color boundary/selection cues and keyboard alternatives for region management/editing where required | Create/select/edit regions without hue; keyboard completion and actual ROI data equality |
| X3 / #2134 | FRAP text/background fixes plus readable status and accessible cell semantics | Normal/selected/alternate/hover/disabled states and non-identifiable results |
| S2/S3/S8 | Shared error/warning tokens, textual severity and programmatic status feedback | Every affected renderer/console, including selected backgrounds and AT announcements |
| S5 / #2136 | Verify dialogs identify errors accessibly; persist linked text where needed for recovery | Invalid-input keyboard/AT task, focus restoration and correction; avoid duplicate disruptive announcements |
| S6 / #2137 | Prove constraints UI absent from shipped reachable functionality, or remediate/remove it | Packaging/entry-point evidence; deletion not required solely to satisfy an unreachable UI finding |
| S7 / #2138 | Add explicit match-state label/icon and accessible table-cell state | Identify match rows without yellow background, including selected rows |
| X4 / #2135 | Repair web badge contrast and required UConn accessibility link; include all routes in web audit | Browser contrast/state tests, link navigation and AT reading |
| H1 | Fix all sensory-only help instructions and rebuild delivered help | Compare instructions with actual UI and verify HTML/JavaHelp/document output |
| C1–C4 | Complete client/server Cividis registration, capability handling, state cues and equivalent numeric access | Actual new/old server combinations, output identity, special-state readout and unchanged scientific values |

The heat-map essential-presentation exception applies only where its actual conditions hold. It
never automatically exempts axes, legends, controls, focus, selection, NaN/out-of-range states or
alternative access to scientific information. Record criterion-specific decisions. A project waiver
cannot waive a normative success criterion. Custom colors and optional style suppression must not
eliminate the remaining means of identification; keep labels/selection/data access available.

### D — Desktop semantics, keyboard and assistive technology

Audit Swing `AccessibleContext` and custom `Accessible` implementations; name/describe controls,
associate labels, expose roles/states/values/actions and relationships, and make dynamic changes
available to platform accessibility bridges. Implement keyboard bindings, logical focus traversal,
visible focus, predictable modal focus restoration and accessible validation/status handling.

For custom plot/geometry/graph canvases, expose selectable entities and meaningful descriptions,
coordinates/values, relationships and equivalent operations through keyboard-accessible controls or
structured tables/trees. A static textual dump is insufficient when users need interactive editing.
Where path-dependent drawing has a legitimate criterion exception, document it narrowly; region
selection, deletion, labeling and configuration still require appropriate alternatives.

Run complete create/edit/save/submit/cancel/read-results/export workflows with actual screen
readers and keyboard alone. Verify clipboard, dialogs, tables, expression editors, shortcuts,
context menus, progress, timeouts, recovery and destructive-action confirmation. If an OS/JDK
accessibility bridge is inadequate, remediate the bridge/component or deliver a demonstrably
complete accessible workflow. Do not mark a supported platform PASS using another OS's results.

### E — Web semantics, responsiveness and input

Apply semantic HTML and native controls first; supply ARIA only where required by custom widgets.
For `webapp-ng` and `webapp-viewer`, remediate every §13 row: landmarks/headings, forms/errors,
focus/dialog behavior, keyboard operation, live status, zoom/reflow, spacing, touch targets,
drag/gesture alternatives, input cancellation and consistent help. Ensure canvas/WebGL/SVG
scientific controls and results have accessible interactive equivalents and programmatic names.

Include real authentication/recovery and permissions-dependent routes in end-to-end tests. Resolve
hosted-provider barriers with provider configuration or an equivalent conforming process; inability
to change a provider is BLOCKED, not PASS. Test password-manager/paste and session recovery.
Add repeatable browser accessibility scans and keyboard/task tests for every route and relevant
state. `axe-core` is appropriate for these web surfaces and does not validate Swing or all WCAG.

### F — Documentation, media and generated artifacts

Rebuild help and verify headings, navigation, language, link meaning, image descriptions and
sensory-independent instructions in the shipped form. For distributed documents, test actual
reading order, tagging/structure, tables and alternative descriptions using format-appropriate
assistive technology. Add captions/audio descriptions/transcripts as required for each inventoried
media type; absence of media requires evidence before N/A.

For scientific images/movies that cannot themselves carry all required semantics, deliver the
necessary descriptions and structured data through a clearly associated accessible package or
workflow; test that users can find, understand and use the equivalent information. Preserve units,
series/region identities, time coordinates and exceptional values. Assess each output format and
its context rather than claiming that offering an unrelated CSV makes every export conformant.

## 16. Verification, release evidence and conformance reporting

### 16.1 Required environment matrix

Before implementation, `support-matrix.md` must list exact supported OS, JDK, look-and-feel, browser,
assistive-technology and accessibility-bridge versions for the release. At minimum evaluate the
project's Windows, macOS and Linux desktop support; establish working combinations with NVDA/JAWS
on Windows, VoiceOver on macOS and Orca on Linux as applicable to claimed support. Test capability
instead of assuming those Java/AT combinations work. Record each result and unresolved bridge gap.

For web surfaces test supported Chromium, Firefox and Safari engines, keyboard-only usage and
representative screen-reader/browser combinations; include mobile/touch views where supported.
Exercise normal/light, dark and Windows forced/high-contrast modes, text scaling, zoom/reflow and
text-spacing overrides as applicable. Record platform differences and every criterion exception.
An inaccessible advertised platform cannot be silently dropped to pass this gate.

### 16.2 Automated and scientific gates

- Run §8 unit/rendering tests, expanded for every series/state and §15 package, plus the repository
  Fast group and relevant module regressions. Existing failures must be classified; no unresolved
  applicable accessibility failure is allowed merely because it predates this work.
- Preserve baseline scientific data, transforms, units, extrema, labels, sample/series mapping,
  clipping and serialization. Use reviewed raster tolerances for intentional styling changes;
  maintain exact default-colormap/export expectations where behavior is explicitly unchanged.
- Test sparse/dense/large datasets, one-sample spikes, log/linear/step plots, histograms, bands,
  filtering/reordering and custom colors. Specify performance budgets before changes and record
  actual timings; “under five seconds” for one curve is not general responsiveness evidence.
- Integrate browser route/state scans and behavioral tests into CI; add accessible component
  contract tests for native widgets. Scans, headless BufferedImages and mocked providers never
  substitute for native AT, real browser workflows or actual export-server gates.

Existing commands to use at implementation time after documented build prerequisites:

```sh
# Repository root, Java 17 and required Python/native dependencies installed:
mvn test -Dgroups=Fast -pl vcell-util,vcell-core,vcell-client -am
# Broader repository Fast gate, where required by the changed modules:
mvn test -Dgroups=Fast
# Run these from webapp-ng, after installing its dependencies:
npm run build_prod
npm run test:ci
npm run lint
```

`webapp-viewer/package.json` currently has build/serve scripts but no test script. Follow
`webapp-viewer/test/README.md` for its existing harness and add/document a reproducible accessibility
runner before treating web test coverage as complete. Inspect configured Angular test/e2e builders
before relying on a script name. Record exact commands, exit codes, logs and environment in evidence;
missing browsers, native libraries, providers or displays leave their gates BLOCKED.

### 16.3 Manual and independent acceptance

Two reviewers execute scripted tasks across the complete inventory, with at least one independent
of the implementation. Include participants using screen readers and participants with CVD where
available; record participant coverage honestly. Manual keyboard/AT testing is mandatory even when
such participants are unavailable. CVD simulations include protan/deutan/tritan and grayscale, and
contrast checks use actual rendered backgrounds. Review all inventory families, not just the six
original screenshots, and include default and altered states and larger scientific datasets.

Every test record contains criterion IDs, surface/workflow/state, expected and observed results,
fixture, release SHA, OS/JDK/browser/AT/theme, date, operator/reviewer and durable evidence links.
Screenshots alone cannot prove keyboard or programmatic accessibility; attach task logs and
accessibility-tree/AT evidence. Recheck repaired failures and affected workflows on the same release
candidate. Sample reuse needs a written component-equivalence rationale; unresolved coverage gaps
block the claim.

### 16.4 Final release and maintenance contract

The release reviewer checks that §9 is entirely satisfied and every matrix entry is evidenced PASS
or justified N/A. Publish a scoped report distinguishing direct web WCAG conformance from desktop
and document assessment against WCAG using WCAG2ICT. Include release/build/date, exact URLs and
artifact scope, target versions/level, technologies relied upon, accessibility-supported combinations,
test methods, criterion results and known limitations. Do not describe a failing scoped feature as
conformant, nor call a plan or unit-test run a certification. Do not claim AAA or universal usability.

Link the report to #1605/#1603 during authorized implementation/closure work. A broad project claim
requires all shipped/hosted surfaces in §14; a narrower intermediate report must explicitly state
its narrower scope and cannot be used to mark the project complete. Maintain a user-facing
accessibility statement, accessible feedback path and named triage owner. Every UI/content change
updates the inventory/matrix and reruns affected automated/manual checks. Reassess at releases and
when supported platforms, browser/AT versions, themes or dependencies change. New failures reopen
the relevant work and prevent an unsupported conformance claim for the affected release.
