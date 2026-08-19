# Group: User Documentation & Materials — 9 issues

In-app help (`vcell-client/UserDocumentation/`, compiled to JavaHelp and to HTML at
vcell.org/webstart), plus website integration and the VCML specification.

**Epic:** `#1031` (enhance user docs and training, 4/9 done, two of its remaining items point at
the separate `virtualcell/usermaterials` repo).

---

## The version-lag problem

Three of the nine issues name a version we shipped long ago:

- `#207` — "Update RBM help from version 6 to **version 7.5**" (2022, empty body)
- `#1534` — "General Help Updates for **7.7**" (2025, `Active`, one-line body)
- `#1708` — spatial geometry mapping help (2026, `Active`)

We are on **8.0.27.01**. `#207` and `#1534` are stale in their titles, but the *underlying* need is
probably still real — if RBM help was on version-6 content in 2022, it is unlikely to have been
updated since. The stale version number hides a live gap rather than indicating the gap closed.

> **Recommendation:** restate `#207` and `#1534` against the current release, or fold both into a
> single "help content audit against 8.0" item. A recurring per-release help-review step in
> `docs/RELEASING.md` would stop this pattern regenerating.

---

## Sub-themes

### In-app help content (4)

`#207` (RBM help), `#1534` (general 7.7 updates, `Active`), `#1370` (instructions for converting to
OME-TIFF for image import — Priority 8/4, needed because **bioformats was removed from the VCell
runtime**, so users must now convert externally), `#1708` (spatial geometry mapping for distributed
compartments needs help text, a tooltip, or a popup explaining what surface-to-volume and volume
fractions refer to — `Active`, 5 comments).

`#1370` is the one with a forcing function behind it: a capability was removed and users need to be
told what to do instead. Until that help exists, image import is harder than it was. It is
correctly the highest-ranked doc issue.

`#1708` is a good example of documentation as a bug fix rather than a chore — the underlying
complaint is that users misinterpret what a field means, which produces wrong models.

### VCML specification (1)

`#315` — *"We need to post an updated, even if minimal, documentation of VCML (plus update our
namespace URI)."* `Shelved` since 2022.

VCML is VCell's native model format and it has no published specification. That matters more than
its `Shelved` status suggests: `docs/architecture-layers.md` and the SBML/SED-ML interop work in
[11-standards-interop.md](11-standards-interop.md) all rest on VCML semantics that exist only in
code. This is also the kind of artifact that academic users and reviewers ask for.

Worth an explicit decision rather than indefinite shelving: either it is not worth writing, or it
is — but leaving the flagship format undocumented by default is a choice being made passively.

### Website and model-browser integration (3)

`#192` (integrate the VCell model browser with vcell.org, the CCB website, and RunBioSimulations —
empty body, `Shelved`), `#204` (from the website, let users load a model in VCell or run the sim in
BioSimulations — *"coordinate with Michael regarding use of the Modelbricks interface"*, `Shelved`),
`#205` (improve how published models are assigned in VCellDB and posted to the website — empty
body).

All three are 2022, all three concern the boundary between VCell and its public web presence, and
all three are `Shelved` or unranked with thin bodies. They should be handled as one question —
*what is the intended relationship between vcell.org, the model database, and BioSimulations?* —
rather than three vague issues.

Note `#1654` ("Update vcell.org stack", in [16-infrastructure-ci.md](16-infrastructure-ci.md)) is
`Active` and touches the same territory. If the WordPress stack is being rebuilt, that is the
moment to answer this question, not after.

### Epic (1)

`#1031` — 4 of 9 items done. Two remaining items are issues in the separate
`virtualcell/usermaterials` repository, and one is *"update README.md for vcell-solvers repo."*

An epic whose remaining scope lives mostly in other repositories is hard to track from here. Either
the cross-repo items move onto a shared board, or the epic should be scoped to this repo and the
others tracked where they live.

---

## Observation

Documentation issues in this backlog are consistently thin, old, and unranked — six of the nine
have bodies under 200 characters, four are `Shelved` or `Pool` with no rank, and only one carries a
numeric priority.

That is a recognisable pattern and not unique to VCell. It is worth noting explicitly because for
academic software the documentation *is* part of the product: it is what lets other groups
reproduce results and cite the tool correctly. The `User Materials` label exists on 9 issues;
there is no evidence in the board data of any of them being scheduled.

---

## Recommendations

1. **Restate `#207` and `#1534`** against 8.0, or merge into one help-content audit.
2. **Add a help-review step to `docs/RELEASING.md`** so version lag stops accumulating.
3. **Decide on `#315` (VCML spec)** explicitly rather than leaving it shelved.
4. **Merge `#192`, `#204`, `#205`** into one question about VCell ↔ web presence, and answer it
   alongside `#1654` while the stack is being updated.
5. **Rescope `#1031`** to this repo, or move its cross-repo children somewhere they are visible.

---

## All 9

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1370](https://github.com/virtualcell/vcell/issues/1370) | Create instructions for users within dialog(s) on how to convert to OME-TIFF to import… | 2024-10 | Queued | 8/4 | Moderate&nbsp;(4) | — |
| [#192](https://github.com/virtualcell/vcell/issues/192) | Integrate VCell Model Browser with VCell Website, CCB website and RunBioSimulations | 2022-07 | Shelved | — | Unknown&nbsp;(0) | REF thin |
| [#204](https://github.com/virtualcell/vcell/issues/204) | From website create ability to either load model inVCell website or run sim in BioSimu… | 2022-07 | Shelved | — | — | REF |
| [#205](https://github.com/virtualcell/vcell/issues/205) | Improve methods for assigning published models in VCellDB and posting to website | 2022-07 | Pool | — | — | REF thin |
| [#207](https://github.com/virtualcell/vcell/issues/207) | Update RBM help from version 6 to version 7.5 | 2022-07 | Pool | — | — | thin |
| [#315](https://github.com/virtualcell/vcell/issues/315) | Create Documentation of VCML | 2022-09 | Shelved | — | — | — |
| [#1031](https://github.com/virtualcell/vcell/issues/1031) | Epic: enhance user docs and training | 2023-11 | Pool | — | — | EPIC |
| [#1534](https://github.com/virtualcell/vcell/issues/1534) | General Help Updates for 7.7 | 2025-06 | Active | — | — | thin |
| [#1708](https://github.com/virtualcell/vcell/issues/1708) | Spatial geometry mapping for distributed compartments needs updated HELP section,"tool… | 2026-06 | Active | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
