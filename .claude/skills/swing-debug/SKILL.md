---
name: swing-debug
description: Drive and inspect the running VCell desktop client (Swing) through the debug bridge — launch it from a source build, read the live UI as JSON, click/type/select, and verify a fix against the real app. Use when investigating or fixing a vcell-client UI bug, reproducing a user-reported desktop problem, or when a change needs checking in the running client rather than only in tests.
---

# Debugging the VCell desktop client

The client ships a dev-only HTTP control surface — the Swing analogue of a DOM
inspector plus Playwright. It is **off unless `-Dvcell.debugBridge=true`** and
binds to loopback only.

Reference, kept next to the code (do not duplicate it here):

- `tools/debug-bridge/README.md` — the launcher, the `bridge.sh` CLI, scenarios.
- `vcell-client/src/main/java/org/vcell/client/debug/README.md` — every endpoint,
  the selector forms, node fields.

This skill is the **workflow and the judgment calls** — the things that are not
obvious from the endpoint list and that have burned real time.

## The loop

```bash
mvn compile -pl vcell-client -am -DskipTests     # after any code change
tools/debug-bridge/launch-client.sh              # client up, bridge on :9123
tools/debug-bridge/bridge.sh tree 8              # read the UI as JSON
tools/debug-bridge/bridge.sh menu "File>Open>Local..."
tools/debug-bridge/bridge.sh wait --type JDialog --contains Error --timeout 5000
```

`launch-client.sh` runs from `target/classes`, so a compile is enough — no
packaging step. It defaults to vcell-dev; override with `VCELL_API_HOST`.

## Selectors: prefer `name=`

`name=SearchButton` survives layout changes; `c42` (registry id) is stable within
a session; `0/3/2` is positional and brittle. Where several components share a
name — VCell reuses panels, e.g. one search panel per database tab — a **showing**
match wins over a hidden one, so `name=JTree1` follows the selected tab. Add an
index (`name=Foo[2]`) to pick a specific one.

If a widget has no name, adding `setName(...)` at its construction site is a
legitimate, inert change that makes it addressable here and in future tests.

## Verifying a fix — the part that matters

**1. Run a pre-fix control.** A fix that "works" proves nothing unless the bug
demonstrably happened before. Compile the original sources into a separate
directory and put it first on the classpath:

```bash
git show origin/master:path/To/File.java > /tmp/orig/File.java
javac --release 17 -cp "<module classes>:<deps>" -d /tmp/orig-classes /tmp/orig/File.java
java -cp "/tmp/orig-classes:<module classes>:<deps>" YourHarness   # expect the failure
java -cp "<module classes>:<deps>" YourHarness                     # expect it fixed
```

**2. Z-order needs a REAL screen capture.** `/screenshot` renders via
`Component.printAll` and is deliberately occlusion-free — it shows a window's own
content even when buried, so it **cannot** tell you what is in front. Use
`screencapture -x out.png` (macOS) and look at the image. Getting this wrong
inverts the conclusion.

**3. A focused harness often beats driving the GUI.** For code reachable only
through awkward UI state, construct it directly. A `java.lang.reflect.Proxy`
implementing `LargeShapeCanvas` whose `getGraphics()` returns null reproduced a
paint-time NPE exactly, in milliseconds, with no client running.

**4. Read the real log.** The client redirects stdout/stderr to
`~/.vcell/logs/vcellrun_<site>.log`; the launcher's own output only has
pre-redirect lines. `bridge.sh log 200` serves the tail.

## Known limits

- **Lightweight popup menus** (e.g. an "Add Subdomain" `JPopupMenu`) vanish on
  focus change and usually cannot be captured. Verify that logic through the
  owning panel's API instead of the popup.
- **Menus are the exception**: `/menus` and `menu "File>New>BioModel"` read and
  fire menu items from the models, opening no popup at all.
- `JTree` rows report the text the **renderer** draws, so VCell's database trees
  read as "Lee 2026 Systems-level…" rather than `PublicationInfo@415f5f4f`.

## Reusable recipes

**Open a corrupt document** (import/error paths): drop a bad file into
`exampleModels/`, then `menu "File>Open>Local..."`, read the chooser's `JTable`
from `/tree`, `trow <table> <row>` then `dtrow <table> <row>`. **Delete the file
afterwards — `exampleModels/` is tracked by git.**

**Exercise install4j code paths from a source build**: pass
`-Dinstall4j.launcherId=999`. Outside an installation
`ApplicationLauncher.launchApplication` throws `FileNotFoundException` on
`i4jparams.conf` in ~150ms, so failure branches are easy to reach.

**Open a database document**: `tab name=DatabaseTabbedPane <n>` →
`expand name=JTree1 <row>` → `drow name=JTree1 <row>`. VCell's database trees open
documents from the raw `MouseEvent` (`MOUSE_PRESSED` + `clickCount==2`), so a real
synthetic double-click is required.

## Housekeeping

Stop the client when finished (`pkill -f vcell.debugBridge.port=9123`), and remove
any file you planted in a tracked directory. `tools/debug-bridge/scenarios/smoke.sh`
is a quick end-to-end sanity check after changing the bridge itself.
