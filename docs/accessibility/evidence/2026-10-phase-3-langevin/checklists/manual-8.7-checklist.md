# Manual 8.7 checklist — Phase 3 Langevin surfaces (PENDING HUMAN REVIEW)

Plan reference: `.agents/uconn-color-blind-accessibility-verified.md` §8 8.7-a, executed per
`.freebuff/phase-3-langevin-completion-plan.md` §6. Definition: two reviewers (one ideally with
CVD, otherwise using Sim Daltonism / Color Oracle) run this checklist on macOS, Windows and
Linux. **PASS = all items "yes" with screenshots attached; any "no" fails.**

Evidence session context: `../manifest.md` (fixture, SimID, SHA `fc8ebd3aea`, capture inventory
S1–S10). Reviewers use the keyboard for all C-items.

| Reviewer | CVD / aid | OS | Date | Client build/SHA | Result |
|---|---|---|---|---|---|
| _name_ | CVD / Sim Daltonism / Color Oracle / none | macOS | | `fc8ebd3aea` | |
| _name_ | | macOS | | | |
| _name_ | | Windows | | | |
| _name_ | | Windows | | | |
| _name_ | | Linux | | | |
| _name_ | | Linux | | | |

Setup per OS: build (`mvn compile -pl vcell-client -am -DskipTests`), launch via
`tools/debug-bridge/launch-client.sh` (or a packaged client — required for C6, see note), load
the Langevin model/data per `../manifest.md` §1 (record exactly what you used; the captured
session used the `biomodel_315318780.vcml` fixture with `allosteric`/`transition_free` disabled
and a 1-job Quick Run — or use a server-side 20-job batch for visible SD envelopes).

Execute on the molecule plot, then the cluster plot ("both" = both plots):

| # | Action | Expected (all "yes" = pass) | macOS | Windows | Linux |
|---|---|---|---|---|---|
| C1 | Read the legend | Every entry shows a line sample whose **style** (solid/long-dash/dot/dash-dot) is discernible at actual size; entries pair style + color + name | ☐ | ☐ | ☐ |
| C2 | Hover a legend entry | Others dim (existing behavior) — note it; dimming is a bonus, not the pass condition | ☐ | ☐ | ☐ |
| C3 | Click into the plot; press Ctrl+N / Ctrl+P repeatedly | Status label names the series **with no pointer event**; order wraps | ☐ | ☐ | ☐ |
| C4 | Press Ctrl+I | Status `"name (only this series)"`; all other series hidden; press Ctrl+I again — all series return | ☐ | ☐ | ☐ |
| C5 | **Focus isolation:** with the molecule plot focused, Ctrl+N; then focus the cluster plot (same window), Ctrl+N | Only the focused plot's status/selection changes; the other plot is unaffected | ☐ | ☐ | ☐ |
| C6 | Tab to a legend text label; press Enter, then Space | Each selects that series (status updates); **focus ring visible on the label** | ☐ | ☐ | ☐ |
| C7 | Toggle the Data button | Data table appears; keyboard-navigable; columns headed by series names | ☐ | ☐ | ☐ |
| C8 | Right-click plot → options → uncheck "Vary line styles" | Lines all solid; repeat C3 — series still named; re-check | ☐ | ☐ | ☐ |
| C9 | (Cluster COUNTS mode) Ctrl+N/Ctrl+I with bubbles, including single-color mode | Bubbles select/isolate by name even when all one color | ☐ | ☐ | ☐ |
| C10 | (Grayscale reviewer pass, or Color Oracle/Sim Daltonism grayscale) | Each legend entry still matches its curve by style alone | ☐ | ☐ | ☐ |
| C11 | Reviewer with CVD (or filter glasses) views S1–S10 unaided | Can perform C1/C3/C4 tasks | ☐ | ☐ | ☐ |
| C12 | Scan for regressions | No layout breakage, no unrecoverable state, status label never blank when a series is selected | ☐ | ☐ | ☐ |

Notes for reviewers:

- **C6 note (S2 BLOCKED in the automated session):** the automated session could not capture the
  legend focus ring because the raw `java` client instance never received AWT window activation
  (details in `../manifest.md` §2). Run the packaged client normally and confirm the ring shows
  when a legend label has keyboard focus. The label is focusable with accessible
  name/description = series name (verified statically).
- **C9 note:** the single-color bubble option (`showBubbleSingleColor`) has **no UI control** in
  the product; the S8 capture state was set programmatically (same call the accessibility test
  makes). To reproduce: use the test or a debug hook, or treat C9's single-color half as
  covered by `PlotRenderersAccessibilityTest.bandAndBubbleExposeNameAndValuesFromTheKeyboard`
  and note the missing UI control as an observation for §15 C.
- Screenshot attachments: use `../screenshots/S1..S10*` as the reference states; attach your own
  per-OS captures to the PR as well.

Per-OS verdicts (reviewer signs): ________________________________________________
