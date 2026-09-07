# Debug-bridge tooling

Committed fixtures around the [Swing debug bridge](../../vcell-client/src/main/java/org/vcell/client/debug/README.md)
so getting a live, drivable client takes one command instead of a rediscovered recipe.

| Script | Purpose |
|---|---|
| `launch-client.sh` | Launch the client **from `target/classes`** (no packaging) with the bridge on, using the bundled install4j JRE and a cached dependency classpath. Backgrounds itself and waits for `/health`. |
| `bridge.sh` | CLI over the bridge's HTTP endpoints: `tree`, `find`, `menu`, `wait`, `assert`, `shot`, `log`, … URL-encodes arguments, pretty-prints via `jq` when available; `wait`/`assert` exit non-zero on failure so scenarios are plain shell. |
| `replay.py` | Replay a recorded script (`bridge.sh replay`). Issues each step through the endpoint that already implements that verb, retries a step until it takes, and waits for a window a step opened rather than sleeping. `--from`/`--to` play a slice; `--speed`/`--max-delay` re-time it. |
| `scenarios/` | Reusable end-to-end scripts built on `bridge.sh` — `smoke.sh` checks bridge, main frame, menus, EDT and screenshots using only semantic selectors; `detach-window.sh` and its recorded twin `detach-window-recorded.sh` drive the modeless-child-window round trip. |
| `doc-scaffold.py` | Turn a recording plus its captures into a VCell help-page skeleton (`<vcelldoc>` XML for `UserDocumentation/originalXML`). Writes the mechanical parts — ordered steps, image references — and leaves the prose to a person. |
| `scenarios/recordings/` | Captured UI scripts. JSON, hand-editable, replayed by `replay.py`. |
| `scenarios/tutorials/` | The vcell.org tutorial PDFs, as storylines and as scripts that rebuild them. See its [README](scenarios/tutorials/README.md). |

## Quick start

```bash
mvn compile -pl vcell-client -am -DskipTests   # after code changes
tools/debug-bridge/launch-client.sh            # client up, bridge on :9123
tools/debug-bridge/bridge.sh menus             # read the menu structure
tools/debug-bridge/bridge.sh menu "Account>Login"
tools/debug-bridge/bridge.sh wait --type JDialog --contains Login --timeout 5000
tools/debug-bridge/scenarios/smoke.sh          # end-to-end sanity
```

`launch-client.sh` defaults to the dev server (`vcell-dev.cam.uchc.edu:443`) and a
local install at `~/Applications/VCell_Alpha` for the JRE/native libs; override with
`VCELL_API_HOST` / `VCELL_INSTALL_DIR`. The client's real log is
`~/.vcell/logs/vcellrun_<site>.log` — or just `bridge.sh log 100`.

## Addressing things

A selector is one of `name=Foo`, `text=Foo`, `type=JSortTable`, a `c<id>`, or a node path
like `0/0/1/0`. Each accepts an optional `[n]` suffix to pick among duplicates, and an
unqualified selector resolves against the **active window** first — which is what makes
`text=OK` mean the button in the dialog in front of you rather than one behind it.

Prefer them in that order. `name=` survives a relayout; `text=` survives a relayout but
not a relabelling, and is the only handle a pop-up menu item has, since VCell builds those
on the fly without naming them; `type=` is for the components a dialog builds generically
(the geometry chooser is a bare `JSortTable` in a `JOptionPane`); a node path breaks the
next time that panel is rearranged. A recording marks which tier each step needed, so the
naming debt is readable off a fresh capture.

Rows, columns and tabs follow the same rule — say what a thing **reads**, not where it
sat:

| Instead of | Say |
|---|---|
| `trow <table> 3` | `findrow <table> "Dex"` then act on that row |
| column index `5` | `findcol <table> "Initial Condition"` |
| `tab <pane> 3` | `tab <pane> "Simulations"` |

The application tab strip is the concrete reason for the last one: a spatial application
shows four tabs and a non-spatial one five, so index 3 is a different tab in each.

## Editing values

```bash
bridge.sh setcell <table> <row> <col> <value>   # through the table's own model
bridge.sh combo   <selector> "IDA (Variable Order, Variable Time Step, ODE/DAE)"
bridge.sh settext <selector> <text> [--enter]
```

`setcell` goes through `setValueAt`, which is what the cell editor itself calls on Enter —
so validation and side effects are the real ones, including creating a row from an
"(add new here)" placeholder. It is the only way to drive these cells, because the editor
is a transient component created on edit and destroyed on commit, with no name to hold.

`settext --enter` also delivers a `focusLost` to the field. Much of VCell's older GUI
commits on focus loss rather than on Enter (`TimeBoundsPanel`, the mesh and output
panels), and `setText` moves no focus — so without it a value is displayed and then
silently discarded, and the dialog reopens showing the old number.

## Recording and replaying

```bash
tools/debug-bridge/bridge.sh record start                  # or: record start <file>
# ... drive the UI by hand: click, type, pick menu items ...
tools/debug-bridge/bridge.sh record stop scenarios/recordings/my-flow.json

tools/debug-bridge/bridge.sh replay scenarios/recordings/my-flow.json --dry-run
tools/debug-bridge/bridge.sh replay scenarios/recordings/my-flow.json
DRIVER=robot ...                       # or --driver robot: the cursor visibly moves
```

**Both routes are recorded.** Real mouse and keyboard input reaches the AWT event queue and
is captured by the listener. The bridge's own model-based endpoints (`click` on a button,
`menu`, `settext`, `tab`, `row`, `trow`, `expand`) post no event at all, so they report the
step themselves — a session scripted entirely with `bridge.sh` records correctly. Robot-driven
helpers (`rbclick`, `drow`, `dtrow`, …) do not report themselves, because the listener already
sees their real events.

Two consequences worth knowing: **scripted setup performed while recording ends up in the
script** — start with `--no-bridge-actions`, or do setup before `record start`. And if a
recording still comes out empty, `bridge.sh record status` reports `rawEvents`, which
separates "saw nothing" (a modal dialog blocking input will do that) from "captured nothing".

**The file is written as you go.** `record start` creates it immediately and rewrites it
after every step, so a client that crashes — or that you kill — costs at most the step in
progress, not the take. `record start <file>` chooses where that happens; `record stop
<file>` names the final destination and moves the auto-named working file there. Every
reply, `status` included, tells you the current path.

**One recording, two replay drivers.** `semantic` (the default) is fast and moves no
cursor — right for CI. `robot` glides the pointer to each target and clicks for real —
the only mode worth filming, since a click with no pointer near it reads as broken. The
recording is identical; only playback differs.

Replay handles the timing traps a fixed `sleep` cannot: it **retries a step until it
takes** (a menu item sits disabled for a moment after a modal dialog is dismissed) and
**waits for a window a step opened** before moving on.

The recording holds the navigation; assertions stay in the scenario. `detach-window-recorded.sh`
shows the split — it replays one step at a time with `--from`/`--to` so it can check state
*between* clicks, which a single end-to-end playback could not.

## Capturing a feature for the help system

```bash
bridge.sh record start scenarios/recordings/my-feature.json
# ... drive the feature by hand ...
bridge.sh record stop

bridge.sh replay scenarios/recordings/my-feature.json --shots /tmp/shots --shot-scale 0.5
doc-scaffold.py scenarios/recordings/my-feature.json --shots /tmp/shots \
                --target MyFeature --title "My Feature" --out page.xml
```

Then a person copies the captures worth keeping into
`vcell-client/UserDocumentation/originalXML/topics/image/` under names that mean something,
updates the `imgReference` targets, writes the prose, and adds a `<tocitem>` to `TOC.xml`.

Three things this pipeline learned the hard way:

- **`--shot-delay` matters.** `/idle` drains the EDT, but VCell fills many panels from
  background tasks, so an idle EDT does not mean the pixels are final. Captured too early,
  two different steps produce byte-identical images and the page documents the wrong screen.
- **Scale the captures.** `DocumentCompiler` rejects any image over 500,000 bytes. At
  `--shot-scale 0.5` a full window lands around 30KB; at full size it will not.
- **The help build is skipped when it looks done.** The `build-documentation` profile
  activates only when `target/classes/vcellDoc` is *missing*, so a plain rebuild silently
  leaves doc changes untested. Remove that directory, or run `DocumentCompiler` directly.
