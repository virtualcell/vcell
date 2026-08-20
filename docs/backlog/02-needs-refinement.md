# Needs Refinement — 50 issues that cannot be worked as written

**107 of 272 open issues (39%) have a body under 200 characters.** This document covers the worst
50 — those whose body is **under 80 characters**, meaning the title is effectively the entire
issue. Median body length across the whole backlog is 295 characters.

These are not necessarily bad issues. Most are real problems. But none of them can be handed to an
engineer who was not in the room when they were written, and several cannot be handed to anyone at
all any more, because the room was four years ago.

**The disposition for every issue here is the same: a refiner adds one paragraph, or the issue is
closed.** Refinement is not implementation — it should take two minutes per issue from the right
person, and the right person is usually the author.

---

## Why this is concentrated in 2022

34 of these 50 were opened on **2022-07-19 or 2022-07-20**. That is a bulk import: a pre-existing
to-do list (probably a spreadsheet) converted into GitHub issues one row per issue, titles intact,
bodies empty. `#201`'s entire body is the giveaway — *"Original Todo list has ?? by this item."*

That import was a reasonable thing to do at the time. The consequence four years later is that a
quarter of the backlog is written in a shorthand only its authors can read. **`#176`, `#180`,
`#182`, `#192`, `#194`, `#205`, `#207`, `#210`, `#211`, `#215`, `#218`, `#221`, `#224`, `#225`
have a completely empty body.**

**Recommendation:** do not refine these one at a time through the normal process. Book one
90-minute session with @ACowan0105 and @vcellmike (authors of most of them) and walk the list.
Expected outcome: roughly half close outright, half get a one-sentence criterion. Doing this
individually, by ticket, will take a year and will not finish.

---

## Grouped by what is actually missing

### 1. Empty body — meaning lives only with the author (14)

Nothing can be inferred. These need their author or they need to be closed.

| # | Title | Group | Author |
|---|---|---|---|
| [#176](https://github.com/virtualcell/vcell/issues/176) | Expand identification of catalysts with dashed line to situations where catalyst species is part of a global parameter | UI | ACowan0105 |
| [#180](https://github.com/virtualcell/vcell/issues/180) | Allow for clusters in NFSim | MATH | ACowan0105 |
| [#182](https://github.com/virtualcell/vcell/issues/182) | Allow choice of concentrations or counts for observables and generated species for RBM | MATH | ACowan0105 |
| [#192](https://github.com/virtualcell/vcell/issues/192) | Integrate VCell Model Browser with VCell Website, CCB website and RunBioSimulations | DOCS | ACowan0105 |
| [#194](https://github.com/virtualcell/vcell/issues/194) | Geometry editor bug: padding adds new "background" compartment if original background was renamed | UI | ACowan0105 |
| [#205](https://github.com/virtualcell/vcell/issues/205) | Improve methods for assigning published models in VCellDB and posting to website | DOCS | ACowan0105 |
| [#207](https://github.com/virtualcell/vcell/issues/207) | Update RBM help from version 6 to version 7.5 | DOCS | ACowan0105 |
| [#210](https://github.com/virtualcell/vcell/issues/210) | Create list of operations for "undo" ability | UI | ACowan0105 |
| [#211](https://github.com/virtualcell/vcell/issues/211) | VCell compliance with SBML spatial | STD | ACowan0105 |
| [#215](https://github.com/virtualcell/vcell/issues/215) | Allow parameter expressions in RBM for reversible reactions | MATH | ACowan0105 |
| [#218](https://github.com/virtualcell/vcell/issues/218) | Export rule-based applications in OMEX format via BNGL | STD | ACowan0105 |
| [#221](https://github.com/virtualcell/vcell/issues/221) | Add progress bar or hourglass to time plot to indicate something is happening | UI | ACowan0105 |
| [#224](https://github.com/virtualcell/vcell/issues/224) | Revive SabioRK using their native search tools | UI | ACowan0105 |
| [#225](https://github.com/virtualcell/vcell/issues/225) | Allow other panes besides reaction diagram to tear off from main workspace | UI | ACowan0105 |

Two of these deserve individual attention rather than batch treatment:

- **`#194`** is `Queued` at **Priority 8 / Importance 8** — well up the ranked slate — with an
  empty body and 5 comments. Whatever justifies that rank is in the
  comments or in someone's head, not in the issue. It also has the most specific title in the set,
  so it is probably a genuine, reproducible geometry-editor bug worth writing up properly.
- **`#207`** asks to update RBM help "from version 6 to version 7.5". We ship 8.0. The version
  numbers in the title are stale even if the underlying need is not — the help may well still be
  on version 6 content. Restate against the current release.

### 2. Insider shorthand — a sentence that assumes context (12)

There is a body, but it is a note-to-self, not a specification.

| # | Body, in full | Group |
|---|---|---|
| [#213](https://github.com/virtualcell/vcell/issues/213) | *"Actually assigned to Les but he does not yet have a handle."* | EXEC |
| [#201](https://github.com/virtualcell/vcell/issues/201) | *"Original Todo list has ?? by this item."* | INFRA |
| [#206](https://github.com/virtualcell/vcell/issues/206) | *"May need to clarify issue"* | MATH |
| [#189](https://github.com/virtualcell/vcell/issues/189) | *"Ion will create test model"* | INFRA |
| [#172](https://github.com/virtualcell/vcell/issues/172) | *"Needs move to new backend (Moraru)"* | API |
| [#191](https://github.com/virtualcell/vcell/issues/191) | *"New design created and approved by group. Need to identify who will code"* | VIZ |
| [#185](https://github.com/virtualcell/vcell/issues/185) | *"In addition to current, compare runs of published models."* | INFRA |
| [#178](https://github.com/virtualcell/vcell/issues/178) | *"Needs algorithm to base on D given to seed species."* | MATH |
| [#209](https://github.com/virtualcell/vcell/issues/209) | *"contact Jim Faeder for potential link to NFSim on VCell Github"* | MATH |
| [#222](https://github.com/virtualcell/vcell/issues/222) | *"May currently work only when adding field data"* | UI |
| [#184](https://github.com/virtualcell/vcell/issues/184) | *"Replicate right click search used in reaction diagram"* | UI |
| [#153](https://github.com/virtualcell/vcell/issues/153) | *"change current default setting / decide whether colors for species need to be changed"* | UI |

`#213` is the clearest case for closing: it is a four-year-old note that a named person did not
yet understand a problem. Whatever it referred to, that state no longer exists.

`#191` is the opposite — it says a **design was created and approved by the group**, and the only
open question was staffing. It carries Importance 6. If that design still exists somewhere, this
is a well-understood piece of work with a missing artifact link, not a vague idea. Find the design,
attach it. (See also the desktop-vs-web viewer decision in [04-epic-map.md](04-epic-map.md) —
that decision may moot this entirely, which is a reason to take the decision first.)

### 3. Title is the whole spec, and might be enough (13)

These are terse but arguably self-describing. The refinement needed is small: acceptance criteria,
or confirmation the behavior is still current.

| # | Title | Group | Board |
|---|---|---|---|
| [#167](https://github.com/virtualcell/vcell/issues/167) | API endpoint for field data retrieval for local runs | VIZ | Queued 12/8 |
| [#168](https://github.com/virtualcell/vcell/issues/168) | Improve efficiency of slurm job arrays for multiple stochastic trajectories | EXEC | Shelved |
| [#169](https://github.com/virtualcell/vcell/issues/169) | Investigate whether parameter scans can be improved in Slurm | EXEC | Shelved |
| [#174](https://github.com/virtualcell/vcell/issues/174) | Create results view with envelope and SD for multiple stochastic runs | VIZ | Pool |
| [#175](https://github.com/virtualcell/vcell/issues/175) | Create annotation fields for Application elements | UI | Pool |
| [#181](https://github.com/virtualcell/vcell/issues/181) | When "Force Continuous" applied in spatial stochastic app, don't allow species displayed as particle counts | MATH | Pool |
| [#551](https://github.com/virtualcell/vcell/issues/551) | `ExpressionUtils.getLinearFactor()` should use SymbolTableEntries | MATH | Pool |
| [#1268](https://github.com/virtualcell/vcell/issues/1268) | Set z coordinate of membrane-bound site to 0 | SALAD | off-board |
| [#1299](https://github.com/virtualcell/vcell/issues/1299) | VCell Guest should not have file browser items of "My BioModels" nor "Shared With Me" | UI | Queued 6/1 |
| [#1343](https://github.com/virtualcell/vcell/issues/1343) | Biomodels of the same publication should be packaged into a single sbml-omex | STD | Queued 7/2 |
| [#1344](https://github.com/virtualcell/vcell/issues/1344) | SBML-OMEX archived VCell publications should use normalized ASCII names | STD | Queued 5/1 |
| [#1449](https://github.com/virtualcell/vcell/issues/1449) | Maintain ordering when toggling global flag on variable | UI | Queued 5/2 |
| [#1494](https://github.com/virtualcell/vcell/issues/1494) | Lazy load microscopy data in PDE data viewer | VIZ | off-board |

`#551` is the best-formed issue in this whole document despite being one line: it names the exact
method and the exact change. It needs nothing except someone to do it. **Good candidate for the
contributor in `#1598`.**

### 4. Terse but recent — refine at the source (11)

Opened 2024 or later, so the author is present and the context is fresh. Cheap to fix now,
expensive in two years.

| # | Title | Group | Opened |
|---|---|---|---|
| [#1048](https://github.com/virtualcell/vcell/issues/1048) | VCell needs to output spatial simulation data in a way Biosimulations approves of | STD | 2023-11 |
| [#1074](https://github.com/virtualcell/vcell/issues/1074) | Upload All Nonspatial VCell Projects to BioSimulations | STD | 2023-12 |
| [#1152](https://github.com/virtualcell/vcell/issues/1152) | VCell Health Check in Quarkus needs conversion to API Package | API | 2024-02 |
| [#1260](https://github.com/virtualcell/vcell/issues/1260) | Sites that lack states in physiology are not accepted by SS application | SALAD | 2024-05 |
| [#1365](https://github.com/virtualcell/vcell/issues/1365) | VCell Client Update appears after login, loses focus with auth0 login | API | 2024-10 |
| [#1534](https://github.com/virtualcell/vcell/issues/1534) | General Help Updates for 7.7 | DOCS | 2025-06 |
| [#1636](https://github.com/virtualcell/vcell/issues/1636) | Upgrade BioNetGen Version | INFRA | 2026-02 |
| [#1637](https://github.com/virtualcell/vcell/issues/1637) | Upgrade NFSim Version | INFRA | 2026-02 |
| [#1657](https://github.com/virtualcell/vcell/issues/1657) | simularium export for SpringSaLaD | SALAD | 2026-04 |
| [#1658](https://github.com/virtualcell/vcell/issues/1658) | export SpringSaLaD sim results for external viewing/analysis | SALAD | 2026-04 |
| [#429](https://github.com/virtualcell/vcell/issues/429) | Local sim results window pops behind the main VCell window | UI | 2022-09 |

Notes:

- **`#1048`** is `Blocked` with an **empty body**, and it is one of only two children of epic
  `#1069`. An epic whose content is a blocked, bodyless issue is not tracking anything. See
  [04-epic-map.md](04-epic-map.md).
- **`#1636` / `#1637`** ("Self explanatory, we need to update") are the two children of `#1635`.
  The missing target versions have since been established
  ([05-obsolescence-sweep.md](05-obsolescence-sweep.md)): BioNetGen is **vendored Perl at 2.3.0,
  untouched since 2017-06-05**, against upstream **2.9.3** (2025-04-21); NFSim is not in this repo
  at all — it is a `vcell-solvers` binary, upstream **v1.14.3**. What is still missing is the blast
  radius: a solver version change moves simulation results, so both need a validation plan.
- **`#1534`** is `Active` and asks for help updates for 7.7, which has shipped. Same stale-version
  problem as `#207`; restate for 8.0 or close.
- **`#1657` / `#1658`** are both `Active` with empty bodies, both about getting SpringSaLaD results
  out for external viewing. These two are probably one issue — see [12-springsalad.md](12-springsalad.md).
- **`#429`** is old but has 6 comments and a specific, checkable symptom. The window z-order
  family it belongs to (`#429`, `#1441`, `#1078`) is being actively worked under `#1751`; refine it
  by linking those together.

---

## The other 57

Beyond these 50 there are 57 more issues with bodies between 80 and 200 characters. They are less
acute — usually one real sentence — and are listed inline in their group documents rather than
here. The same rule applies: if a `Queued` issue cannot be started from its body, it is not
actually ready, whatever the board says.

**Suggested gate:** nothing moves from `Pool` to `Queued` without a body that states the observed
behavior, the expected behavior, and how to tell when it is fixed. Applying that rule retroactively
to today's 63 `Queued` issues would move a good number of them back.
