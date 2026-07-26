# Swing Debug Bridge

A dev-only introspection + control surface for the running VCell desktop client —
the Swing analog of a browser's DOM inspector + Playwright. It lets a developer
(or an automated coding agent) **see the live UI as text and pixels** and
**drive it**, without attaching a debugger.

- `SwingInspector` — EDT-safe primitives: serialize every showing window to JSON,
  find/click a component by path, screenshot a window to PNG.
- `SwingDebugBridge` — a loopback-only HTTP server exposing those primitives.

## Why

Java Swing has no built-in text serialization of the widget tree the way the web
has the DOM. This bridge manufactures one: every node carries a stable `path`
selector (e.g. `0/3/2` = window 0 → child 3 → child 2) that you hand back to
`/click` or `findByPath(...)`. That closes the observe→act→observe loop that web
tooling gets for free.

## Safety

- **Off by default.** Nothing starts unless `-Dvcell.debugBridge=true` (or env
  `VCELL_DEBUG_BRIDGE=true`). A normal production launch is completely unaffected.
- **Loopback only.** Binds to `127.0.0.1`, never reachable off the machine.
- **Never fatal.** Startup/handler failures are logged, never propagated.

## Enable

Easiest, from a source build:

```bash
./vcell.sh --debug-bridge            # bridge on :9123
./vcell.sh --debug-bridge=9200       # bridge on a custom port
```

Or add JVM args to any launch (IDE run config, or install4j vmoptions):

```
-Dvcell.debugBridge=true
-Dvcell.debugBridge.port=9123          # optional, default 9123
-Dvcell.debugBridge.dir=/tmp/vcell-debug   # optional screenshot dir
```

On startup you'll see a `WARN` log line: `Swing debug bridge listening on http://127.0.0.1:9123`.

## Endpoints

Every node in `/tree` and `/windows` carries a stable **`id`** (`c0`, `c1`, …) in addition to its `path`. Any endpoint's `path=` parameter accepts either form — prefer the `id`, which stays valid across dumps and doesn't break when the component tree shifts. (Registry is non-invasive; it never touches `Component.getName()`.)

| Endpoint | Returns |
|---|---|
| `GET /health` | `ok` |
| `GET /windows` | JSON: showing top-level windows only (depth 0) |
| `GET /tree[?maxDepth=N]` | JSON: full component tree of every showing window |
| `GET /screenshot[?window=N]` | JSON `{"path": "...png"}`; omit `window` for the active window. Rendered off-screen via `printAll`, so overlapping applications never bleed into the capture |
| `GET /click?path=0/3/2` | JSON `{"clicked": true\|false}` |
| `GET /setText?path=&text=&enter=` | JSON `{"set": true\|false}` |
| `GET /selectTab?path=&index=` | JSON `{"selected": true\|false}` |
| `GET /selectTreeRow?path=&row=N` | select row N of the JTree; JSON `{"selected": bool}` |
| `GET /rightClickTreeRow?path=&row=N` | right-click row N (opens its context menu); JSON `{"rightClicked": bool}` |
| `GET /rightClick?path=` | right-click a component's center; JSON `{"rightClicked": bool}` |
| `GET /listeners?path=0/3/2` | JSON: registered `ActionListener` classes, action command, mouse-listener count — "is this control actually wired up?" |
| `GET /log[?lines=N]` | text/plain tail (default 200 lines) of the client's real log — VCell redirects System.out/err to `<vcellHome>/logs/vcellrun_<site>.log`, so exceptions never appear on the launcher's stdout |

Buttons/checkboxes are clicked via `doClick()` posted with `invokeLater` (no
cursor movement, and the request returns immediately even if the action opens a
modal dialog); other components get a synthetic `Robot` click at their center.

## Typical loop

```bash
curl -s localhost:9123/tree?maxDepth=8 | jq          # read the UI as text
curl -s localhost:9123/screenshot | jq -r .path       # -> PNG to open/inspect
curl -s "localhost:9123/click?path=0/0/1/0/0/0"       # act
curl -s localhost:9123/tree | jq                       # observe the result
```

## Node fields

`path`, `class`, `name` (from `Component.getName()`), `text`, `tooltip`,
`visible`, `showing`, `enabled`, `selected` (buttons only), `bounds{x,y,w,h}`,
`children[]`.

> Tip: components with a `name` (~28% of client files call `setName`) are the
> most reliable to target and to reason about. When adding UI, `setName(...)`
> on interactive widgets makes them addressable here and in future AssertJ-Swing
> tests.

## Smoke test

A standalone demo (no VCell server / login needed) lives in the scratchpad during
development; the pattern: set `vcell.debugBridge=true`, show a `JFrame` with named
widgets, call `SwingDebugBridge.startIfEnabled()`, then curl the endpoints.
