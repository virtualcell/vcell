# Independent audit of the color-accessibility plans, 2026-10-15

Auditor: Perch (Saffron), independent of the implementation.
Scope: `.agents/uconn-color-blind-accessibility-verified.md` (master plan) and
`.agents/wcag-color-aa-finish-plan.md` (finish plan), checked against the working
tree at `chore/vcell#1605` @ `83677ea161` (== `origin/chore/vcell#1605`).

## Verdict

The color work is ~80% complete and well-evidenced. The app does **not** meet
WCAG 2.1 AA today, and neither plan claims it does. The narrow claim in finish
plan §6 (criteria 1.3.3, 1.4.1, 1.4.3, 1.4.11) is one open item away from
supportable; full 2.1 AA (master plan §13, 50 criteria, work packages B–F) has
not started.

## What was independently verified

### Contrast math (recomputed from source colors, WCAG 2.x relative luminance)

| Pair | Ratio | Threshold | Result |
|---|---|---|---|
| `#A40000` on `#FFFFFF` (ERROR_TEXT_COLOR, GuiConstants.java:132) | 8.14:1 | 4.5:1 | pass |
| `#8A4B00` on `#FFFFFF` (WARNING_TEXT_COLOR, GuiConstants.java:138) | 6.80:1 | 4.5:1 | pass |
| `#5C0000` on `#FFAAAA` (FRAP NOT_IDENTIFIABLE_TEXT, AnalysisTableRenderer.java:31) | 7.96:1 | 4.5:1 | pass |
| `#FFFFFF` on `#BF360C` (webapp-ng badge, lowest) | 5.60:1 | 4.5:1 | pass |
| `#157347` on white (webapp-viewer light) | 5.87:1 | 4.5:1 | pass |
| `#4dc47d` on `#121212` (webapp-viewer dark) | 8.48:1 | 4.5:1 | pass |
| `#ff6b6b` on `#121212` (webapp-viewer dark error) | 6.75:1 | 4.5:1 | pass |

webapp-viewer probe labels use `currentColor` at 0.7 fill-opacity (index.html
line 55), not the series color, so the series colors only need the 3:1
non-text threshold (1.4.11), which both palettes clear.

### Full webapp-viewer pytest suite (closes the R7 evidence gap)

Two independent full-suite runs on 2026-10-15:

1. `docs/accessibility/evidence/2026-10-step1-webapp-viewer-pytest/record.txt`:
   193 passed in 343.76s (5:43), `--no-build`, Python 3.14.7, Java 17.
2. This audit: fresh venv (`webapp-viewer/test/.venv-audit`, since removed),
   full Maven build of vcell-client, Playwright chromium:
   **193 passed, 0 failed in 364.46s (6:04)** at current HEAD.

Environment note for future runs: Maven on PATH resolves to JDK 27
(Homebrew openjdk 27), where `Thread.stop()` no longer exists and
`vcell-client` fails to compile (`AsynchProgressPopup.java:141,169`). Pin
`JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.20.1/libexec/openjdk.jdk/Contents/Home`
before running the suite. This matches the phase-6 log's Java 17 requirement.

### Other checks

- Cividis BSD-3 attribution present in `vcell-core/src/main/java/cbit/image/CividisColorTable.java`
  and `webapp-viewer/colormap.js`.
- `docs/accessibility/evidence/2026-10-c12-audit/color-audit-rows.md` exists and
  reproduces the PR #2141 rows as claimed (PR still OPEN, not merged).
- Claimed test files exist: `MolecularTypeErrorMarkTest.java`,
  `SpatialMatchLabelTest.java`, `ConstraintPanelReachabilityTest.java`.
- The plan's "branch not on origin" note is stale: local HEAD equals
  `origin/chore/vcell#1605`.

## Still open (finish plan §6 unchecked boxes)

- **R2**: real old-server/new-server export run for Cividis; labels must match pixels.
- **R6**: published help still fails. The live vcell.org Observables page
  (fetched 2026-10-15) still states "A site that has a defined state is always
  shown in green" with no non-color cue — a genuine 1.4.1/1.3.3 failure on
  published help. `webhelp-deploy.yml` has not been run; the branch is on
  origin, so the deploy is no longer blocked by that.
- **R10**: two-reviewer manual pass (macOS/Windows/Linux, CVD simulation) never ran.

## Full WCAG 2.1 AA status

Not met, not claimed. Master plan §13 matrix is mostly NOT TESTED; work packages
B–F not started; §16.1 support matrix (NVDA/JAWS/VoiceOver/Orca) unwritten.
Open GitHub items: PR #2141, issues #2132, #2135, #2140, #1605.
