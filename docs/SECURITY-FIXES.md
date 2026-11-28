# Landing a security fix

How to fix a vulnerability in VCell without telling the world about it before production is
patched. Use this instead of the usual PR → merge queue → release flow (`docs/RELEASING.md`)
whenever the change fixes something an attacker could use.

## The constraint: everything pushed is public

`virtualcell/vcell` is a public repository. A commit is visible to anyone the moment it is pushed,
whether to `master`, to a branch, or as a tag, and whether or not it is in a PR. Pushing directly
to `master` hides nothing that a PR would reveal.

So what matters is the **exposure window**: the time from the first public push of the fix to
production running it. The procedure below keeps everything local or private until the moment it
releases, and then releases straight to production. The window becomes the time CI-full takes to
build the images plus the deploy, about 30 minutes: 27 minutes from publishing the release to prod on
2026-11-20.

Once production runs the fix, nothing is urgent any more. The fix reaches `master` through an
ordinary PR, with review and the merge queue.

## Procedure

### 1. Record it privately

Open a **draft security advisory**: repo → Security → Advisories → New draft security advisory.
The details live there: what is vulnerable, how it is reached, its severity and the affected
versions. Drafts are visible only to repository admins and the collaborators you add.

Keep those details **out of** commit messages, branch names, PR descriptions, issues and Slack
until the advisory is published. A commit message says what the code now does ("Bind
caller-supplied values in user SQL"), not how to attack the old code.

### 2. Write the fix privately

Choose one of these:

- **Locally.** Commit on local branches and do not push.
- **In the advisory's temporary private fork.** Use the draft advisory page →
  "Start a temporary private fork". Use it when someone else should review the fix before release:
  pushes and PRs there stay private. GitHub Actions **does not run** in these forks, so run the tests
  locally (`mvn test -pl <module> -am`, plus the Fast group).

You need the fix on two lines:

| Line | Branch from | Example (2026-11-20) |
|---|---|---|
| **Production's** | the tag prod runs: `newTag` in vcell-fluxcd `kustomize/overlays/prod/kustomization.yaml` | `security/rpc-user-binding-8.1` from `8.1.8.06` |
| **`master`** | `master` | the same commits, rebased |

Production often runs an older MAJOR.MINOR line than `master` (8.1.8 vs 8.3.3 in the example).
Patching only `master` leaves production exposed until the next prod roll, while the fix sits in
public.

### 3. Release to production (this opens the window)

Do these steps in one sitting, without stopping:

1. Push the production-line branch.
2. Tag and publish its tip. The next BUILD on prod's line is `X.Y.Z.(BB+1)`; see
   `docs/RELEASING.md` § Versioning. `tools/release/release-and-deploy.sh` only releases from
   `master`, so do this by hand:

   ```bash
   SHA=$(git rev-parse security/<topic>-<MAJOR.MINOR>)
   gh release create 8.1.8.07 --repo virtualcell/vcell --target "$SHA" \
     --title "8.1.8.07" --notes "Security update."
   ```

   Keep the notes neutral. The CHANGELOG entry and the advisory follow in steps 4 and 5.
3. Publishing the release triggers **CI-full**, which builds the images. Do not cancel it before
   its `tag-and-push` job has run (see `.claude/skills/deploy/SKILL.md`).
4. Deploy to prod:
   `gh workflow run site_deploy.yml --repo virtualcell/vcell --ref master -f vcell_version=8.1.8 -f vcell_build=07 -f vcell_site=rel -f server_only=true`.
   Then verify it with `tools/release/verify-deploy.sh 8.1.8.07 rel`. CD-sites reporting green
   only means vcell-fluxcd was asked to deploy.

   `server_only=true` skips the desktop installers. Rebuild them only if the fix is client-side.
5. Check the fix against the live site, the way the advisory describes reaching the bug.

### 4. Bring `master` up to date (the normal way)

Production is patched, so take the normal route:

1. Open a PR to `master` with the `master`-line commits. A neutral description is enough; the
   advisory holds the details.
2. Get a review, add the PR to the merge queue, and let regression run. **Do not bypass the
   rules**, and do not push to `master` directly: nothing is gained by it any more.
3. The dev site picks the fix up from the next `master` release.

### 5. Disclose

1. In the next `master` release's CHANGELOG section, add a `### Security` entry naming the
   advisory and the fixed versions (`docs/RELEASING.md`).
2. Fill in the advisory's patched versions, then publish it. Request a CVE from the advisory page
   if the vulnerability merits one.

## Don't

- **Don't push a fix branch early.** A branch in a public repo is public, even with no PR. Push it
  in step 3, or keep the work in the advisory's private fork until then.
- **Don't open the `master` PR before production is patched.** Its diff publishes the fix.
- **Don't fix `master` only** when production runs an older line.
- **Don't put the attack in a commit message.** Old messages cannot be taken back.

## Worked example: 2026-11-20

Four advisories, fixed together; their details stay in the advisories.

| Time (UTC) | Step |
|---|---|
| — | Fix written on `security/rpc-user-binding-8.1`, branched from `8.1.8.06` (prod) |
| 21:43 | `8.1.8.07` release drafted at that branch's tip |
| 22:04 | release published (the tag becomes public); CI-full builds the images |
| 22:22–22:24 | CD-sites to `rel` |
| 22:31 | vcell-fluxcd flips prod to 8.1.8.07 |

The same commits reached `master` by direct push no later than 22:02, when `8.3.3.03` was cut from
it. That was before the 8.1.8.07 release was even published, so the window that day started at the
`master` push and ran 30 minutes or more. Under this procedure `master` gets the fix by PR after
22:31, and the window shrinks to the 27 minutes from publishing the release to prod.
