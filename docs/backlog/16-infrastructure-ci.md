# Group: Infrastructure, CI/CD & Release Ops — 19 issues

Build, test harnesses, dependency upgrades, release mechanics, and configuration hygiene. The
smallest group with the highest proportion of recent, well-diagnosed work — **six of the 2026
issues here are off-board.**

**Epic:** `#1039` (Solver Regression Tests, 0/4 done, last touched Nov 2023).

---

## Sub-themes

### Test harness and regression coverage (7)

`#1082` (the testing framework hits an RPC `DataAccessException` — **Priority 1 / Importance 1**,
the extreme of the ranked slate, though which extreme depends on the polarity question in
[03](03-board-hygiene.md)), `#1472` (nightly testing fixes — a checklist with two items already
COMPLETE), `#185` (update the math testing framework, `High Priority`, `Shelved`), `#189`
(automatic sim testing for all solvers on each build, Priority 9/5), `#201` (apply the test-suite
data comparator across simulators, `Shelved`), `#728` (collect stochastic validation tests into
JUnit integration tests), `#1039` (epic), `#1503` (automate SBML import against all BMDB models
weekly with a Slack discrepancy report, Priority 6/3).

Three of these — `#185`, `#189`, `#201` — are 2022 issues about "test all the solvers on every
build" and are split across `Shelved` and `Queued` with no coordination. `#1039` is meant to be
their epic and has not moved since 2023. Meanwhile `#1503` and `#1472` describe *newer* testing
automation that partly overlaps.

> This is the same pattern as elsewhere: an old epic holding old issues, a newer effort doing
> similar work, and no link between them. Recommend consolidating the testing story into one
> current epic and closing `#1039` or repurposing it.

`#1472` is worth reading closely because it **records completed work inline** (`[COMPLETE]` on two
of its items). That is a good habit and also a warning: the remaining scope is smaller than the
title suggests, so its `Intricate (2)` rating may now be wrong.

### Dependency upgrades (4)

`#1635` (upgrading biology network generation — the umbrella: *"Due to having older versions of
BioNetGen / NFSim, there are limitations on how models can be designed"*), `#1636` (upgrade
BioNetGen), `#1637` (upgrade NFSim), `#1978` (JSBML fork upgrade plan — a 7.7k-char analysis of the
`virtualcell/vcell-jsbml` fork at `1.6.1-VCELL-4`, what to catch up on, and what to contribute
upstream).

`#1636` and `#1637` say only *"Self explanatory, we need to update."* They are not self-explanatory:
a BioNetGen or NFSim version change alters simulation results, so what is missing is the target
version and the validation plan. See [02-needs-refinement.md](02-needs-refinement.md).

**This trio gates the NFSim feature work** in [15-math-solvers.md](15-math-solvers.md) — `#1635`
explicitly says the current versions constrain what models can express. Sequence accordingly.

`#1978` is the best-documented dependency issue in the repo and is off-board.

### Release and CI mechanics (3)

`#1888` (release runs intermittently fail on ghcr.io secondary rate limits, with no retry — it
happened **twice in one run** during 8.0.10.01), `#1926` (`regression-gate` never actually gates,
because admin merges bypass the merge queue), `#1244` (add URL validation to CI to catch
`MalformedURLException` — BMDB and Pathway Commons imports broken on both Rel and Alpha,
`High Priority`).

`#1888` and `#1926` are both about the release path being less reliable than it appears, and both
are off-board. `#1926` is explicitly framed as documentation of a real state rather than a
misconfiguration — *"the current state is easy to misread as protection that exists"* — which makes
it a decision to confirm rather than a bug to fix.

`#1244` is `High Priority` with a concrete user-visible symptom (two import paths broken in
production) and is unranked in `Pool`. That combination is worth a second look.

### Configuration hygiene (2)

`#1921` (rename the 12 unprefixed config env vars to `VCELL_*`, requiring vcell-fluxcd
coordination), `#1922` (normalize environment naming — *"Three environments are referred to by four
different vocabularies, and the most common one implies the opposite of what is true. It has
already caused one wrong decision"*).

`#1922` is unusual in that it documents a naming problem that has **already caused an incorrect
decision**. That is a concrete cost, not a tidiness preference. Both off-board.

### Build environment (2)

`#1849` (`vcell.sh` does not pin a JDK, so dev clients can run on a different Java than we ship —
every other environment pins Java 17), `#1654` (update the vcell.org WordPress stack, `Active`).

`#1849` is small, self-contained, and prevents a class of "works on my machine" confusion.

---

## Observation: this group is where the recent good work lives

Six issues here (`#1849`, `#1888`, `#1921`, `#1922`, `#1926`, `#1978`) were all written in
August 2026, all carry detailed analysis with tables and citations, and **all six are off the
board with no assignee and no rank.**

Meanwhile the ranked items in this group are 2022–2025 issues, several of them one line long.

The board is not capturing current work, and this group shows it most starkly. That is the
argument for making the off-board sweep ([03](03-board-hygiene.md), Step 3) an early step rather
than a tidy-up at the end.

---

## Recommendations

1. **Board all six 2026 issues** — they are the most actionable items in the group.
2. **Consolidate the testing story:** one current epic covering `#185`, `#189`, `#201`, `#728`,
   `#1039`, `#1472`, `#1503`; close or repurpose `#1039`.
3. **Specify `#1636`/`#1637`** with target versions and a validation plan before scheduling; treat
   `#1635` as their parent.
4. **Sequence the BioNetGen/NFSim upgrade before NFSim feature work** in
   [15-math-solvers.md](15-math-solvers.md).
5. **Re-rank `#1244`** — `High Priority`, production-visible, unranked.
6. **`#1926` needs a decision, not an implementation** — confirm the current merge-gate posture is
   intended, then close or fix.
7. **Re-check `#1472`'s complexity rating** now that two of its items are done.

---

## All 19

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1082](https://github.com/virtualcell/vcell/issues/1082) | Testing Framework encounteres a RPC DataAccessException | 2023-12 | Queued | 1/1 | Unknown&nbsp;(0) | — |
| [#1503](https://github.com/virtualcell/vcell/issues/1503) | Automate running SBML import against all BMDB models | 2025-05 | Queued | 6/3 | Complex&nbsp;(3) | — |
| [#189](https://github.com/virtualcell/vcell/issues/189) | Create Automatic sim testing for all solvers for new build | 2022-07 | Queued | 9/5 | Moderate&nbsp;(4) | thin |
| [#185](https://github.com/virtualcell/vcell/issues/185) | Update Math Testing Framework | 2022-07 | Shelved | — | Intricate&nbsp;(2) | HP thin |
| [#201](https://github.com/virtualcell/vcell/issues/201) | Apply data comparator algorithm from test suite to compare results from different simu… | 2022-07 | Shelved | — | Intricate&nbsp;(2) | REF thin |
| [#728](https://github.com/virtualcell/vcell/issues/728) | collect stochastic validation tests into JUnit integration tests. | 2023-01 | Pool | — | — | — |
| [#1039](https://github.com/virtualcell/vcell/issues/1039) | Epic: Solver Regression Tests | 2023-11 | Pool | — | — | EPIC |
| [#1244](https://github.com/virtualcell/vcell/issues/1244) | Add URL validation to CI/CD to catch MalformedURLException | 2024-05 | Pool | — | — | HP |
| [#1472](https://github.com/virtualcell/vcell/issues/1472) | Nightly testing needs further fixing / upgrades | 2025-04 | Pool | — | Intricate&nbsp;(2) | — |
| [#1635](https://github.com/virtualcell/vcell/issues/1635) | Upgrading Biology Network Generation | 2026-02 | Pool | — | — | — |
| [#1636](https://github.com/virtualcell/vcell/issues/1636) | Upgrade BioNetGen Version | 2026-02 | Pool | — | — | thin |
| [#1637](https://github.com/virtualcell/vcell/issues/1637) | Upgrade NFSim Version | 2026-02 | Pool | — | — | thin |
| [#1654](https://github.com/virtualcell/vcell/issues/1654) | Update vcell.org stack | 2026-04 | Active | — | — | — |
| [#1849](https://github.com/virtualcell/vcell/issues/1849) | vcell.sh does not pin a JDK — dev clients can run on a different Java than we ship | 2026-08 | **off-board** | — | — | — |
| [#1888](https://github.com/virtualcell/vcell/issues/1888) | Release runs intermittently fail on ghcr.io secondary rate limits (push and pull), wit… | 2026-08 | **off-board** | — | — | — |
| [#1921](https://github.com/virtualcell/vcell/issues/1921) | Rename the 12 unprefixed config env vars to VCELL_* (needs vcell-fluxcd coordination) | 2026-08 | **off-board** | — | — | — |
| [#1922](https://github.com/virtualcell/vcell/issues/1922) | Normalize environment naming: four vocabularies for three environments, and the names … | 2026-08 | **off-board** | — | — | — |
| [#1926](https://github.com/virtualcell/vcell/issues/1926) | regression-gate never gates: the merge queue is bypassed by admin merges | 2026-08 | **off-board** | — | — | — |
| [#1978](https://github.com/virtualcell/vcell/issues/1978) | JSBML fork upgrade plan: catch up to upstream, and what to send back | 2026-08 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
