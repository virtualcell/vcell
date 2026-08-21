# Releasing VCell

This document defines the release-notes and versioning conventions for VCell.
It is the source of truth for how a VCell release is described, versioned,
and communicated to its three audiences.

## Audiences and artifacts

VCell has three release-notes audiences. Each is served by a distinct artifact,
but all artifacts derive from one canonical source in this repository.

| Artifact | Audience | Granularity | Location | Source of truth? |
|---|---|---|---|---|
| `CHANGELOG.md` | Developers, API integrators | One section per **tagged build** | repo root | Yes — canonical engineer-track log |
| `release-notes/major/<version>.md` | End users / scientists | One file per **public release** (e.g. `7.7.md`, `8.0.md`) | this repo | Yes — canonical user-facing narrative |
| GitHub release body | Developers (existing channel) | Per tag | github.com | No — copy-pasted from `CHANGELOG.md` at release-cut |
| vcell.org accordion (`/run-vcell-software`) | End users | One entry per public release | external website | No — transcribed from `release-notes/major/<version>.md` when prod rolls |

If the website ever drifts from the repo, the repo wins and the website is
corrected. Never edit the website without also updating the repo doc.

## Versioning

VCell uses a 4-part version: `MAJOR.MINOR.PATCH.BUILD`.

| Part | Meaning | Triggered by |
|---|---|---|
| `MAJOR` | A very large new capability. Rare. | Release-engineering decision. |
| `MINOR` | A public release event — a version announced to users. | Release-engineering decision. |
| `PATCH` | **Every ordinary release.** The default. | Release-engineering decision. |
| `BUILD` | CI auto-incrementing build counter | Automatic. Each push that produces a release artifact increments this. |

**Cutting "a release" means a PATCH release.** That is the normal case and the
one to assume unless told otherwise: 8.1.0.01 → 8.1.1.01 → 8.1.2.01. A release
is not held back from PATCH because it contains a new feature, and does not earn
a MINOR by containing one. Almost every release VCell ships is a PATCH.

MINOR marks a **public release event**: a version given a name, announced to
users, and written up as its own narrative under `release-notes/major/` and its
own vcell.org accordion entry. It is a decision about announcing, not about the
size of the diff.

MAJOR is for a very large new capability and is rare.

The first three parts are intentional human decisions; the fourth is
mechanical. `7.7.0.77` and `7.7.0.78` are the 77th and 78th builds of the
`7.7.0` line. `8.0.0.01` is the first build of the `8.0.0` line.

**What counts as a public release event:**
- A new modeling modality with its own identity (e.g. SpringSaLaD GA)
- A major UI or workflow overhaul worth highlighting on vcell.org

**A user-facing breaking change** — a file-format incompatibility, a silent
change in simulation results, the removal of a feature users depend on — is
called out prominently in the release's `Highlights.` paragraph. It does not by
itself force a MAJOR or MINOR bump.

API breaking changes are tracked under `CHANGELOG.md` `### Changed` and
`### Removed` rather than forcing a MAJOR bump, since API consumers track
the OpenAPI spec separately. However, a release that includes a removed
or renamed `/api/v1/` endpoint should be called out prominently in
`Highlights.` so integrators notice.

## `CHANGELOG.md` format

Follows [Keep a Changelog](https://keepachangelog.com/). One section per
tagged build, newest first. Each section opens with a `**Highlights.**`
paragraph and is followed by flat Keep-a-Changelog categories.

### Section template

```markdown
## [8.0.0.02] - 2026-05-19

**Highlights.** One-to-three sentence prose summary of the release.
This paragraph is the user-facing story and is the source copy for the
vcell.org accordion entry. Lead with the most impactful change.

### Added
- New feature description. (#PR)

### Changed
- Behavior change description. (#PR)

### Deprecated
- Soon-to-be-removed feature description. (#PR)

### Removed
- Removed feature or endpoint description. (#PR)

### Fixed
- Bugfix description. (#PR)

### Security
- Security-relevant change description. (#PR)

### Notes for API consumers
- Schema/endpoint changes in `/api/v1/`, breaking client-generation
  diffs, or deprecation timelines for integrators. Use "No
  `/api/v1/` schema changes in this build." when there are none, so
  silence is unambiguous.
```

Rules:
- Omit any category that has no entries for the build.
- Bullets are imperative or descriptive, not first-person.
- Reference PRs with `(#1693)` style, not full URLs.
- No per-bullet audience tags (`[user]`, `[api]`, etc.). API consumers
  scan `Changed` and `Removed` for breaking changes; the `Highlights.`
  paragraph carries the end-user narrative.

### Unreleased section

The top of `CHANGELOG.md` keeps an `## [Unreleased]` section as a
scratchpad. The release manager may pre-populate it between cuts but is
not required to — the formal write-up happens at release-cut time.

## Major-release documents

Each public release gets a file at `release-notes/major/<version>.md`.
By default, one file per `MAJOR.MINOR` line, not per build.

- `release-notes/major/8.0.md` — VCell 8.0
- `release-notes/major/8.1.md` — VCell 8.1

### The narrative name and the build version may differ

The name a release is given for users is a separate decision from the
version its builds carry, and the two are allowed to diverge. Build
numbers are never restated once published; the narrative decides which
builds it covers.

This has already happened once. **VCell 8.0** is the 8.0.0 builds only
— 8.0.0.01 through 8.0.0.03, the SpringSaLaD GA that ran in production
from 2026-05-21 to 2026-08-14. Everything released after 8.0.0.03 is
**VCell 8.1**, even though those builds are numbered 8.0.2.01 through
8.0.28.01. The numbering was realigned at **8.1.0.01**, so the two agree
again from that build onward; the divergence covers 8.0.2.01–8.0.28.01
only.

So, at release-cut time, add the user-facing items to the narrative
file for the release **currently being accumulated** — today
`8.1.md` — which is not necessarily the file whose name matches the
build's `MAJOR.MINOR`. `CHANGELOG.md` is unaffected: it is organised strictly
by build version and always records the real number.

When a long-running `MAJOR.MINOR` line accumulates multiple distinct
production rollouts that each warrant their own public release event,
split the file into named epoch files and keep a short index file at
the canonical `<MAJOR.MINOR>.md` path:

- `release-notes/major/7.7.md` — index pointing at three epochs:
  - `release-notes/major/7.7-initial-release.md`
  - `release-notes/major/7.7-feature-update.md`
  - `release-notes/major/7.7-stability-update.md`

The epoch boundary is "another prod-roll after a significant gap or
content batch." Each epoch file is its own vcell.org accordion
entry; the index file is repo-only navigation.

These are curated narratives written for end users. They are the source
of truth for the vcell.org `/run-vcell-software` accordion. When prod
rolls to a new publicly-released version, the matching file is transcribed to
the website.

### File template

```markdown
# VCell <version>

**Released:** <date prod rolled, or "in progress" until then>

<One-paragraph headline. Why does this release matter to a VCell user?
What can they do now that they could not before? Lead with the feature
that justified the MAJOR or MINOR bump.>

## What's new

### <Major feature 1>

<Two to four paragraphs. What it is, who it's for, how to access it.
Include screenshots if available. Link to UserDocumentation pages and
tutorials.>

### <Major feature 2>

<...>

## Improvements

<Bulleted list of smaller user-visible improvements rolled up across
the public release.>

## Bug fixes

<Bulleted list of notable user-visible bug fixes.>

## API and integration changes

<Notes for users of vcell-rest, vcell-cli, the Python/Java/TypeScript
clients, or BioModels integrations. Breaking changes called out
explicitly with migration guidance.>

## Known issues

<Open issues users should be aware of in this release.>

## Acknowledgements

<Optional: contributors, funding citations, PI credits for major features.>
```

The file may be drafted while the public release is still in
development (e.g., partially complete on a `stage`-deployed build). It
is not transcribed to the website until prod rolls.

## Release-cut procedure

When a new build is tagged (`MAJOR.MINOR.PATCH.BUILD`), the release
manager performs the following steps. All steps happen in the `vcell`
repo unless otherwise noted.

1. **Update `CHANGELOG.md`.** Add a `## [<version>] - <date>` section.
   Move relevant items out of `[Unreleased]` if any are there. Write
   the `**Highlights.**` paragraph last, once the categorized bullets
   are known.

2. **Update the major-release doc.** If this build is on a new MAJOR
   or MINOR line, create `release-notes/major/<MAJOR.MINOR>.md` from
   the template. Otherwise, fold notable items into the existing file's
   `Improvements`, `Bug fixes`, or `API and integration changes`
   sections.

3. **Edit the GitHub release body.** GitHub auto-generates a "PR title
   by author" body when a release is created. Replace it with the
   matching `CHANGELOG.md` section (paste markdown verbatim). For
   historical releases before this convention, leave auto-generated
   bodies as-is.

4. **Commit and tag** in the usual way. The CHANGELOG.md and
   release-notes updates should land in the same commit that bumps the
   version, so the tag points to a self-describing tree.

5. **vcell.org transcription** — see below.

## vcell.org transcription

The vcell.org accordion is updated **only when prod is rolled** to a
new publicly-released version (a new `MAJOR.MINOR` line). Patch and build
increments within an already-deployed line do not produce new accordion
entries — they are visible only via `CHANGELOG.md` and GitHub releases.

When prod is rolled to a new publicly-released version, the release manager:

1. Confirms `release-notes/major/<MAJOR.MINOR>.md` is final and reflects
   the build that just went to prod.
2. Sets the `**Released:**` field at the top of the file to the prod-roll
   date.
3. Transcribes the file into a new accordion entry on
   `https://vcell.org/run-vcell-software`, preserving the section
   structure.
4. Verifies the rendered entry on the live site.

If the repo doc and the website ever disagree, the repo doc is correct
and the website is updated to match.

## Out of scope

The following are intentionally not part of this convention:

- **PR-time release-notes capture.** Contributors do not add entries
  to `CHANGELOG.md` in their PRs. All release-notes content is
  authored at release-cut time by the release manager from the PR
  list between tags.
- **Towncrier / changesets-style fragment files.** Not used.
- **Per-component versioning.** VCell ships as one monorepo with one
  version. Sub-packages with their own changelogs (e.g.
  `vcell-cli-utils/CHANGELOG.md`) are owned by their respective
  packages and not consolidated here.
- **Backfilling historical GitHub release bodies.** Releases predating
  this convention keep their auto-generated bodies. The convention
  applies forward from the first build cut after this document lands.
