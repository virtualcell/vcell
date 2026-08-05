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

Every endpoint's `path=` parameter accepts **three interchangeable selector forms**:

| Form | Example | Notes |
|---|---|---|
| **name** | `name=DatabaseSearchButton` | Most robust — survives layout changes entirely. Where several components share a name (VCell reuses panels, e.g. one search panel per database tab), a **showing** match wins over a hidden one; disambiguate with an index, `name=DatabaseSearchButton[1]`, using the `/find` ordering. |
| **id** | `c42` | Stable registry id emitted as `id` on every node in `/tree` and `/windows`; valid across dumps. The registry is non-invasive — it never touches `Component.getName()`. |
| **node path** | `0/3/2` | Positional (window 0 → child 3 → child 2). Brittle; prefer the forms above. |

A selector that doesn't resolve reports `did not resolve` (or `false`) rather than erroring — a malformed selector never throws.

> Naming widgets is what makes the `name=` form work: see the tip below on `setName`.

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
| *(JTree rows in `/tree`)* | Row text is what the tree **displays**, obtained from its `TreeCellRenderer` — VCell's database trees hold domain objects whose `toString()` is useless (`PublicationInfo@415f5f4f`), so this reports "Lee 2026 Systems-level consequences…" as a user sees it. `<html>` markup is stripped; falls back to `convertValueToText` then `toString` |
| `GET /selectTableRow?path=&row=N[&column=M]` | select row N of a `JTable` and scroll it into view; JSON `{"selected": bool}`. Row/column are **view** indices, matching the `table` block in `/tree` and the user's current sort order |
| `GET /doubleClickTableRow?path=&row=N[&column=M]` | synthetic double-click on a table row; JSON `{"doubleClicked": bool}`. Table-backed UIs commonly act on the raw `MouseEvent` click count — this is how you pick a file in the file chooser |
| `GET /rightClickTableRow?path=&row=N[&column=M]` | select row N then right-click it (opens its context menu); JSON `{"rightClicked": bool}` |
| `GET /expandTreeRow?path=&row=N[&expand=false]` | expand (or collapse) row N — row indices only cover *expanded* rows, so this is how a driver walks down a tree; JSON `{"expanded": bool}` |
| `GET /doubleClickTreeRow?path=&row=N` | synthetic double-click on row N; JSON `{"doubleClicked": bool}`. Needed because VCell's database trees open a document straight from the `MouseEvent` (`MOUSE_PRESSED` + `getClickCount()==2`), which no higher-level API reproduces |
| `GET /rightClickTreeRow?path=&row=N` | right-click row N (opens its context menu); JSON `{"rightClicked": bool}` |
| `GET /rightClick?path=` | right-click a component's center; JSON `{"rightClicked": bool}` |
| `GET /find?type=&name=&text=&textContains=&limit=` | JSON: components matching ALL given criteria (each param optional, at least one required). `type` is a simple class name matched against the class **or any superclass** (`JButton`, `AbstractButton`); `text` is exact, `textContains` case-insensitive. Returns full nodes (path + id + state), no children |
| `GET /waitFor?{find params}&state=&timeoutMs=&intervalMs=` | Poll the same selector until `state` holds: `showing` (default), `enabled`, or `gone`. JSON `{"satisfied": bool, "elapsedMs": N, "matches": [...]}` — the deterministic-wait primitive for automation (no more sleep-and-hope) |
| `GET /idle` | Wait for the EDT to drain (two no-op round-trips); JSON `{"idle": true, "waitedMs": N}`. Call between act and observe |
| `GET /menus` | JSON: complete menu-bar structure of every window (nested items, separators, accelerators), read from the menu models — **no popups are opened**, so this works even for menus you'd otherwise have to click through |
| `GET /menu?path=Account>Login[&window=N]` | Activate a menu item by visible text (case-insensitive, `>`-separated). Fires the leaf item's `doClick()` directly — replaces the old open-popup-then-click-by-index dance. Lazily-populated menus get their `MenuListener` fired first |
| `GET /listeners?path=0/3/2` | JSON: registered `ActionListener` classes, action command, mouse-listener count — "is this control actually wired up?" |
| `GET /props?path=` | JSON: extended properties of one component — full class chain, focus state, colors/font, accessible role/name/description, button/text-component detail, listener counts. The "inspect element" panel to `/tree`'s DOM |
| `GET /highlight?path=[&ms=2000]` | Flash a translucent red overlay over the component so a human watching the screen sees what a selector resolves to. Glass-pane based; restores the original glass pane (and visibility) afterwards |
| `GET /log[?lines=N]` | text/plain tail (default 200 lines) of the client's real log — VCell redirects System.out/err to `<vcellHome>/logs/vcellrun_<site>.log`, so exceptions never appear on the launcher's stdout |

Buttons/checkboxes are clicked via `doClick()` posted with `invokeLater` (no
cursor movement, and the request returns immediately even if the action opens a
modal dialog); other components get a synthetic `Robot` click at their center.

## Tooling

Committed fixtures live in [`tools/debug-bridge/`](../../../../../../../tools/debug-bridge/README.md):
`launch-client.sh` (run the client from `target/classes` with the bridge on),
`bridge.sh` (CLI with `wait`/`assert` exit codes for scripting), and
`scenarios/` (reusable end-to-end scripts, e.g. `smoke.sh`).

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
