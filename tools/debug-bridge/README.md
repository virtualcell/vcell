# Debug-bridge tooling

Committed fixtures around the [Swing debug bridge](../../vcell-client/src/main/java/org/vcell/client/debug/README.md)
so getting a live, drivable client takes one command instead of a rediscovered recipe.

| Script | Purpose |
|---|---|
| `launch-client.sh` | Launch the client **from `target/classes`** (no packaging) with the bridge on, using the bundled install4j JRE and a cached dependency classpath. Backgrounds itself and waits for `/health`. |
| `bridge.sh` | CLI over the bridge's HTTP endpoints: `tree`, `find`, `menu`, `wait`, `assert`, `shot`, `log`, … URL-encodes arguments, pretty-prints via `jq` when available; `wait`/`assert` exit non-zero on failure so scenarios are plain shell. |
| `scenarios/` | Reusable end-to-end scripts built on `bridge.sh` — `smoke.sh` checks bridge, main frame, menus, EDT and screenshots using only semantic selectors. |

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
