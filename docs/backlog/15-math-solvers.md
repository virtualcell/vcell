# Group: Math Generation & Solvers — 26 issues

Math generation from biology, the expression engine, and the solver lineup. This is the group
where defects are most likely to produce **silently wrong scientific results** rather than visible
failures, which argues for weighting it above its raw count.

**Epics:** `#1033` (improve NFSim multitrial statistics), `#1035` (general reactions with
stochastic simulations, `Active` but untouched since Jan 2024), `#1556` (add COPASI-style steady
state via CVODE — labelled an epic, 0/7 done, no children).

---

## Sub-themes

### Math generation failures (6) — the correctness core

`#490` (species sharing a name with a local parameter — Priority 8/5, `Complex (3)`), `#642`
(indirect referencing of species concentrations through a global parameter), `#356` (a global
parameter `J_r0` colliding with a reaction named `r0`), `#718` (the override resolver does not
handle distributed volume fractions, orphaning the override), `#506` (membrane voltage namescoping
inconsistent), `#475` (zero diffusion rates omitted from the constant list, so they cannot be
overridden at simulation level).

`#490` is the most serious issue in this group and arguably in the backlog. Its body enumerates
the outcomes: math generation fails; *or* generated math is incorrect and the solver hangs in an
infinite loop; *or* **the solver runs and produces incorrect results.** A naming collision that
silently changes the science is a different class of defect from anything else here, and it is
currently Priority 8 of 12.

`#356`, `#490` and `#642` share a root: **the symbol-resolution rules across scopes are not
consistent**, so names collide or resolve differently in INITIAL vs runtime context. Worth
considering whether one piece of work on scoping addresses several of them rather than patching
each collision.

`#475` includes an unusually clear statement of design intent — *"This is a feature, not a bug"* —
followed by why it is nonetheless a problem. Preserve that reasoning; do not let a refinement pass
flatten it into a plain bug report.

### The expression engine (3)

`#430` (improve `ExpressionUtils.functionallyEquivalent()` for expressions with discontinuities —
a careful 1.5k-char writeup of why random sampling misses discontinuities), `#551`
(`getLinearFactor()` should use `SymbolTableEntries` — one line, completely actionable), `#214`
(warn users on circular expressions, e.g. `Kr = Kd*Kr`).

`#430` is `Shelved`. It underpins the roundtrip-equivalence work in
[11-standards-interop.md](11-standards-interop.md) (`#522` is *"math equivalency failure"*), so
shelving it while ranking `#522` at Priority 2 is worth a second look — they may be the same
problem seen from two ends.

### Rule-based modelling / NFSim (8)

`#1033` (epic), `#180` (clusters in NFSim), `#206` (spurious default-compartment observable),
`#209` (turn off complex bookkeeping), `#708` (multiple anchors in rule-based compartments — a good
624-char reproduction with a named model), `#178` (better default D for generated species in
spatial RBM), `#182` (choose concentrations vs counts for observables), `#215` (parameter
expressions in RBM for reversible reactions).

Six of these eight are from the 2022 cohort with thin or empty bodies, and five are `Shelved`.
`#708` is the exception — it has a real reproduction and is unranked in `Pool`.

`#1033`'s value depends on Decision-adjacent questions about NFSim's future: `#1635`/`#1636`/`#1637`
in [16-infrastructure-ci.md](16-infrastructure-ci.md) propose upgrading BioNetGen and NFSim, which
would change the ground under this epic. **Sequence the version upgrade question before the NFSim
feature work.**

### Stochastic simulation (4)

`#1035` (epic), `#1464` (models with stochastic applications but no reactions cannot be saved —
*"There is no reason for this requirement"*, `Simple (5)`, Priority 7), `#181` (Force Continuous
should suppress particle-count display), `#179` (species with D=0 excluded from post-processing
stats).

`#1464` is a small, clearly-argued fix with a `Simple (5)` rating — good value at Priority 7.

`#1035` is `Active` with three unchecked solver-specific tasks and has not been touched in over
two years. See also `#166` in [01-close-and-verify.md](01-close-and-verify.md), which is marked
`Done` on the board while being a live child of this epic — those two facts contradict.

### Solvers (2)

`#1663` (hook up the new FiniteVolume and CVODES/IDAS solvers — Priority 10/7, `Complex (3)`,
*"both working across all our standard operating systems"*), `#1556` (add COPASI-style steady state
through CVODE, 7 unchecked tasks spanning solver, GUI, VCML, and SED-ML import/export).

`#1663` is the higher-value of the two and the more tractable: the solvers already exist and work,
the work is integration. `#1556` is a genuine multi-layer feature that would need real scoping
before it could be scheduled — it is currently `Queued` with no numeric rank, which understates its
size.

### Verification (2)

`#1644` (Python infix generation needs verification — lists specific suspicions: chained
trigonometry diverging between VCell and Python, `atan2` sign issues, division handling), `#563`
(CLI conversion should use saved math for validation, Priority 2/2).

`#1644` is off-board and is the natural follow-on from the `acot` bug in `#1647` (closed —
[01](01-close-and-verify.md)) and the `-0.0` handling in `#1646`. Those two were found; `#1644`
asks whether more of the same remain. **Given that two arithmetic errors were confirmed in the same
area this year, this is a systematic-check candidate rather than a nice-to-have.**

### Parameter exploration (1)

`#942` — pseudorandom sampling of simulation parameters (uniform, log-uniform, normal, log-normal;
possibly Latin Hypercube or Sobol). A well-written 1.8k-char feature proposal with clear
motivation, unranked in `Pool` since 2023.

---

## Recommendations

1. **Re-rank `#490` upward** on severity grounds — it can silently produce wrong results.
2. **Consider `#356` / `#490` / `#642` as one scoping problem** rather than three collisions.
3. **Reconsider `#430`'s `Shelved` status** against `#522`'s Priority 2 — likely the same problem.
4. **Sequence the BioNetGen/NFSim upgrade** (`#1635`–`#1637`) before `#1033`'s feature work.
5. **Board and act on `#1644`** — two confirmed arithmetic bugs this year make it a systematic
   verification question, not a speculative one.
6. **Scope `#1556` properly or move it out of `Queued`** — a 7-task cross-layer feature with no
   estimate is not queued in any meaningful sense.
7. **Resolve the `#166` / `#1035` contradiction** (see [01](01-close-and-verify.md)).
8. Most of the RBM/NFSim 2022 cohort belongs in the batch refinement session
   ([02](02-needs-refinement.md)), not in individual ranking.

---

## All 26

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#563](https://github.com/virtualcell/vcell/issues/563) | CLI conversion should use saved math for validation | 2022-11 | Queued | 2/2 | Unknown&nbsp;(0) | REF |
| [#1464](https://github.com/virtualcell/vcell/issues/1464) | Models with stochastic applications without reactions can not be saved | 2025-03 | Queued | 7/2 | Simple&nbsp;(5) | — |
| [#490](https://github.com/virtualcell/vcell/issues/490) | Math Generation Fails or is Incorrect when species have the same name as local paramet… | 2022-10 | Queued | 8/5 | Complex&nbsp;(3) | REF |
| [#1663](https://github.com/virtualcell/vcell/issues/1663) | Hook-up New Deterministic Spatial (FiniteVolume) and Non-Spatial (CVODES/IDAS) Solvers | 2026-04 | Queued | 10/7 | Complex&nbsp;(3) | — |
| [#178](https://github.com/virtualcell/vcell/issues/178) | Provide better value for D for generated species in spatial RBM | 2022-07 | Pool | — | Complex&nbsp;(3) | REF thin |
| [#179](https://github.com/virtualcell/vcell/issues/179) | Include species with D=0 in "Post Processing Stats Data" | 2022-07 | Shelved | — | — | — |
| [#180](https://github.com/virtualcell/vcell/issues/180) | Allow for clusters in NFSim | 2022-07 | Shelved | — | — | REF thin |
| [#181](https://github.com/virtualcell/vcell/issues/181) | When "Force Continuous" applied in spatial stochastic application, don't allow species… | 2022-07 | Pool | — | Moderate&nbsp;(4) | REF thin |
| [#182](https://github.com/virtualcell/vcell/issues/182) | Allow choice of concentrations or counts for observables and generated species for RBM | 2022-07 | Pool | — | Simple&nbsp;(5) | REF thin |
| [#206](https://github.com/virtualcell/vcell/issues/206) | NFSim immediately creates an observable for total in a default compartment when molecu… | 2022-07 | Shelved | — | — | REF thin |
| [#209](https://github.com/virtualcell/vcell/issues/209) | Allow choice to turn off complex bookkeeping in NFSim | 2022-07 | Shelved | — | — | REF thin |
| [#214](https://github.com/virtualcell/vcell/issues/214) | Need warning when users enter circular expressions  | 2022-07 | Pool | — | Intricate&nbsp;(2) | thin |
| [#215](https://github.com/virtualcell/vcell/issues/215) | Allow parameter expressions in RBM for reversible reactions | 2022-07 | Pool | — | — | REF thin |
| [#356](https://github.com/virtualcell/vcell/issues/356) | math generation fails with global parameter J_r0 and Reaction(r0) | 2022-09 | Pool | — | — | REF |
| [#430](https://github.com/virtualcell/vcell/issues/430) | improve ExpressionUtils.functionllyEquivalent() for expressions with discontinuities | 2022-09 | Shelved | — | — | — |
| [#475](https://github.com/virtualcell/vcell/issues/475) | Species diffusion rates with value zero are not in constant list | 2022-10 | Shelved | — | Byzantine&nbsp;(1) | — |
| [#506](https://github.com/virtualcell/vcell/issues/506) | Membrane voltage namescoping inconsistent | 2022-10 | Pool | — | — | REF |
| [#551](https://github.com/virtualcell/vcell/issues/551) | ExpressionUtils.getLinearFactor() should use SymbolTableEntries | 2022-11 | Pool | — | — | REF thin |
| [#642](https://github.com/virtualcell/vcell/issues/642) | Indirect referencing of species concentrations fails math generation | 2022-12 | Pool | — | — | — |
| [#708](https://github.com/virtualcell/vcell/issues/708) | Bug when using multiple anchors in rule-based compartments NFSim | 2022-12 | Pool | — | Intricate&nbsp;(2) | — |
| [#718](https://github.com/virtualcell/vcell/issues/718) | override resolver does not handle distributed volume fractions | 2022-12 | Pool | — | — | — |
| [#942](https://github.com/virtualcell/vcell/issues/942) | pseudorandom sampling of vcell simulation parameters | 2023-07 | Pool | — | — | — |
| [#1033](https://github.com/virtualcell/vcell/issues/1033) | Epic: improve NFSim multitrial statistics | 2023-11 | Pool | — | — | EPIC |
| [#1035](https://github.com/virtualcell/vcell/issues/1035) | Epic: general reactions with stochastic simulations | 2023-11 | Active | — | — | EPIC |
| [#1556](https://github.com/virtualcell/vcell/issues/1556) | Add Steady State (COPASI-style) to VCell through CVODE (VCellODE) | 2025-06 | Queued | — | — | EPIC |
| [#1644](https://github.com/virtualcell/vcell/issues/1644) | Python Infix generation needs additional verification | 2026-02 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
