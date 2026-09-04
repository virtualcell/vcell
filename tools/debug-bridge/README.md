# Debug-bridge tooling

Committed fixtures around the [Swing debug bridge](../../vcell-client/src/main/java/org/vcell/client/debug/README.md)
so getting a live, drivable client takes one command instead of a rediscovered recipe.

| Script | Purpose |
|---|---|
| `launch-client.sh` | Launch the client **from `target/classes`** (no packaging) with the bridge on, using the bundled install4j JRE and a cached dependency classpath. Backgrounds itself and waits for `/health`. |
| `bridge.sh` | CLI over the bridge's HTTP endpoints: `tree`, `find`, `menu`, `wait`, `assert`, `shot`, `log`, … URL-encodes arguments, pretty-prints via `jq` when available; `wait`/`assert` exit non-zero on failure so scenarios are plain shell. |
| `replay.py` | Replay a recorded script (`bridge.sh replay`). Issues each step through the endpoint that already implements that verb, retries a step until it takes, and waits for a window a step opened rather than sleeping. `--from`/`--to` play a slice; `--speed`/`--max-delay` re-time it. |
| `scenarios/` | Reusable end-to-end scripts built on `bridge.sh` — `smoke.sh` checks bridge, main frame, menus, EDT and screenshots using only semantic selectors; `detach-window.sh` and its recorded twin `detach-window-recorded.sh` drive the modeless-child-window round trip. |
| `scenarios/recordings/` | Captured UI scripts. JSON, hand-editable, replayed by `replay.py`. |

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

## Recording and replaying

```bash
tools/debug-bridge/bridge.sh record start                  # or: record start <file>
# ... drive the UI by hand: click, type, pick menu items ...
tools/debug-bridge/bridge.sh record stop scenarios/recordings/my-flow.json

tools/debug-bridge/bridge.sh replay scenarios/recordings/my-flow.json --dry-run
tools/debug-bridge/bridge.sh replay scenarios/recordings/my-flow.json
DRIVER=robot ...                       # or --driver robot: the cursor visibly moves
```

**The recorder only sees input that reaches the AWT event queue.** Real mouse and keyboard
input qualifies; so does `bridge.sh rbclick`, which posts native events. `bridge.sh click`
does **not** — it fires buttons through `doClick()`, which calls its listeners directly. So
a session driven by `click` records nothing at all. If a recording comes out empty, check
`bridge.sh record status`: `rawEvents` tells you whether the recorder saw events and
rejected them, or never saw any (a modal dialog blocking input will do the latter).

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
