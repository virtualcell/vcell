# VCell Windowing (Logical Window) Design & Best-Practice Compliance

Status: reference. Scope: `vcell-client` desktop Swing UI.
Package: `org.vcell.client.logicalwindow` (the "LW" framework).

## 1. The problem this solves

VCell's desktop client is a **multi-window** Swing application: a document window
(BioModel / MathModel / Geometry), plus many child frames and dialogs (viewers,
editors, wizards, option/error dialogs, progress popups). Plain Swing/AWT has no
notion of a *logical* window hierarchy across top-level `Frame`s — a `JFrame` is
always a root, and a `JDialog`/`JOptionPane` created with a **null owner** is
parented to nothing.

The user-visible symptom (the recurring **z-order bug**): a window appears
**behind** the window that spawned it — an error dialog hidden behind the editor,
a child viewer lost behind the document window — because nothing ties it to its
logical parent, brings it to front on open, or lets the user find it.

The **Logical Window (LW) framework** layers a logical parent/child tree over
Swing so that:

- every window has a known **logical parent** (`getlwParent()`),
- a child is **brought to front and positioned** (staggered/centered) on its
  parent when shown,
- closing a parent **closes its children recursively**,
- all live windows are enumerable in a global **"Window" menu** so nothing is
  truly lost, and
- **modality is normalized** to parent-only (never application/toolkit-wide).

## 2. Architecture

### Core interfaces (`org.vcell.client.logicalwindow`)

| Type | Role |
|------|------|
| `LWHandle` | A logical window (frame *or* modal dialog). Knows its `getWindow()`, `getlwParent()`, `getLWModality()`, menu description, and can `closeRecursively()`. Iterable over its logical children. |
| `LWContainerHandle` extends `LWHandle` | A handle that can **have children** — adds `manage(LWHandle child)`. |
| `LWFrameOrDialog` | The `JFrame`/`JDialog` method surface used by `ChildWindowManager` so both can be treated uniformly. |
| `LWManager` (package-private) | Mix-in implementing container behavior: stores children, positions them (`WindowPositioner`), brings a child `toFront()` on `windowOpened`, removes them on close, `closeRecursively()`. |
| `LWTraits` | `InitialPosition` = `STAGGERED_ON_PARENT` (child frames) / `CENTERED_ON_PARENT` (dialogs) / `NOT_LW_MANAGED` (top frames). |
| `LWNamespace` | Static utilities — most importantly **`findLWOwner(Component)`**, which walks up the Swing parent chain to find the nearest `LWContainerHandle`. Also `positionChildren`, `stagger`, modality mapping, multi-monitor screen size. |

### Base classes to extend

| Base class | Use for | Modality | Initial position |
|------------|---------|----------|------------------|
| `LWTopFrame` (abstract `JFrame`) | **Top-level** application windows (roots). Registers in a global weak list, provides the **"Window" menu**, tracks focus order. | MODELESS | not LW-positioned |
| `LWChildFrame` (abstract `JFrame`) | **Modeless child** windows of a top frame. | MODELESS | staggered on parent |
| `LWDialog` (abstract `JDialog`) | **Dialogs**. Forces `DOCUMENT_MODAL` (parent-only), `DISPOSE_ON_CLOSE`. | PARENT_ONLY | centered on parent |
| `LWTitledDialog` / `LWTitledChildFrame` | as above, title used as the menu description | | |
| `LWOptionPaneDialog` / `LWTitledOptionPaneDialog` / `LWOkCancelDialog` | `JOptionPane`-backed dialogs (what `DialogUtils` builds) | PARENT_ONLY | centered |

The app's real top frames all extend `LWTopFrame`: `DocumentWindow`, `BNGWindow`,
`TestingFrameworkWindow`, `FieldDataWindow`, `VirtualFrapMainFrame`,
`VirtualFrapBatchRunFrame`.

### The two gateways most code should use

Application code usually should **not** construct LW windows directly. Two
gateways already do it correctly:

1. **`org.vcell.util.gui.DialogUtils`** — all message/warning/error/input/
   ok-cancel/list-selection dialogs. Every method resolves
   `LWContainerHandle lwParent = LWNamespace.findLWOwner(requester)` from the
   triggering component and builds an `LWTitledOptionPaneDialog(lwParent, …)`.
   **Prefer `DialogUtils.showXxx(requester, …)` over raw `JOptionPane`.**

2. **`cbit.vcell.client.ChildWindowManager`** — child viewers/editors. Its
   `ModelessChild extends LWChildFrame` and `ParentModalChild extends
   LWTitledDialog` are the managed child implementations; `addChildWindow(...)`
   wires them to the owning `LWContainerHandle`. (`JDiagAdapter` is a
   transitional raw-`JDialog` adapter — legacy, not a model to copy.)

## 3. Correct usage

**Construct a dialog from an existing component** (the canonical pattern from
`package-info.java`):

```java
LWContainerHandle lwParent = LWNamespace.findLWOwner(requester); // requester = a Component in the UI
LWDialog d = new LWTitledDialog(lwParent, "Define New Subdomain Shape");
```

**Show a message/error/input dialog** — use `DialogUtils`, pass the real
triggering component (never `null`):

```java
DialogUtils.showWarningDialog(requester, "…");
int r = DialogUtils.showOKCancelWarningDialog(requester, "Title", "…");
String s = DialogUtils.showInputDialog0(requester, "Prompt", defaultValue);
```

**Open a child viewer/editor** — go through the document window's
`ChildWindowManager.addChildWindow(...)`, not a bare `new JFrame`.

**A new top-level window** — extend `LWTopFrame`.

## 4. Best-practice compliance rules

1. **No unparented top-level windows.** Don't `extends JFrame`/`extends JDialog`
   for a window shown inside the running app — extend `LWTopFrame` /
   `LWChildFrame` / `LWDialog` (or route through a gateway). Raw subclasses are
   allowed only for standalone tools with their own `main()`.
2. **Never construct a shown `JDialog`/`JFrame`/`Frame`/`JWindow` with a `null`
   owner** and make it visible. If you must build one, resolve a parent via
   `findLWOwner(requester)` and pass it.
3. **Never `JOptionPane.showXxxDialog(null, …)`.** Use `DialogUtils.showXxx(
   requester, …)`. Even `JOptionPane` with a real parent bypasses LW — prefer
   `DialogUtils`.
4. **Always pass a real `requester`/`parentComponent`.** The whole system hinges
   on `findLWOwner` finding an `LWContainerHandle` ancestor; a `null` requester
   defeats it.
5. **Modality = parent-only.** Use `DOCUMENT_MODAL` (what `LWDialog` enforces via
   `normalizeModality`). Never `APPLICATION_MODAL` / `TOOLKIT_MODAL` — they block
   unrelated VCell windows.
6. **`setAlwaysOnTop(true)` is a red flag.** It forces a window above *every*
   application, not just VCell, and almost always masks a missing logical parent.
   Prefer making the window an LW-managed child (which gets `toFront()` on open).
7. **Reuse, don't re-root.** A window that logically belongs to a document should
   be a child of that document's `LWTopFrame`, not a new root.

### Anti-pattern → fix quick reference

| Anti-pattern | Why it breaks z-order | Fix |
|--------------|-----------------------|-----|
| `class X extends JFrame` shown in-app | untracked root, no toFront, not in Window menu | `extends LWChildFrame` (child) or `LWTopFrame` (root) |
| `class X extends JDialog` shown in-app | null/none owner, floats free | `extends LWDialog`/`LWTitledDialog` |
| `new JDialog((Frame)null, …)` + `setVisible` | no owner → behind parent | `findLWOwner(requester)` → `LWDialog`, or `DialogUtils` |
| `JOptionPane.showMessageDialog(null, …)` | null owner | `DialogUtils.showXxx(requester, …)` |
| `setAlwaysOnTop(true)` to "fix" hiding | above all apps; hides real bug | make it an LW child |
| `APPLICATION_MODAL` dialog | blocks other VCell windows | `DOCUMENT_MODAL` via `LWDialog` |

## 5. How to find violations

```bash
cd vcell-client/src/main/java
# raw window subclasses (exclude the framework itself)
grep -rnE 'class[[:space:]]+\w+[[:space:]]+extends[[:space:]]+(JFrame|JDialog)\b' --include='*.java' . | grep -v logicalwindow
# unparented constructions (check the owner arg at each)
grep -rn 'new JDialog(\|new JFrame(\|new Frame(\|new JWindow(' --include='*.java' .
# null-parent option dialogs
grep -rnE 'JOptionPane\.show[A-Za-z]+Dialog\([[:space:]]*null' --include='*.java' .
# always-on-top smell
grep -rn 'setAlwaysOnTop(true)' --include='*.java' .
```
Exclude the gateways themselves from results — raw construction is expected
*inside* `org.vcell.client.logicalwindow`, `DialogUtils`, and `ChildWindowManager`.

## 6. Compliance audit (2026-07, `vcell-client/src/main/java`)

Full-codebase audit against the rules in §4. Findings are ranked by user-visible
z-order impact. "Shown live" = reachable in the running desktop (not a `main()`
demo). Line numbers are as of the audit date — re-grep (§5) before fixing.

### P1 — High: mis-parented windows shown live

| # | Site | Problem | Fix |
|---|------|---------|-----|
| 1 | `org/vcell/documentation/VcellHelpViewer.java:118` | User **Help window** is a null-owner `JFrame`, made visible, not LW-tracked → hides behind desktop | make `VcellHelpViewer` an `LWChildFrame` (or `ChildWindowManager.addChildWindow`) |
| 2 | `cbit/vcell/client/VCellGuiInteractiveContext.java:20` | error dialog: throw-away `new JDialog()` + `setAlwaysOnTop(true)` as `JOptionPane` owner | `DialogUtils.showErrorDialog(topLevelWindowManager.getComponent(), msg)` |
| 3 | `cbit/vcell/client/VCellGuiInteractiveContext.java:27` | warning dialog, same pattern (sibling `showConnectWarning:42` already does it right) | `DialogUtils.showWarningDialog(topLevelWindowManager.getComponent(), msg)` |
| 4 | `cbit/vcell/client/ClientRequestManager.java:358` | close/save option dialog, orphan `JDialog` + alwaysOnTop | `DialogUtils.showOKCancelWarningDialog(windowManager.getComponent(), …)` |
| 5 | `cbit/vcell/client/ClientRequestManager.java:484` | guest-user error, same pattern | `DialogUtils.showErrorDialog(windowManager.getComponent(), …)` |
| 6 | `cbit/vcell/client/ClientRequestManager.java:661` | logout confirm, same pattern | `DialogUtils.showOKCancelWarningDialog(requester.getComponent(), …)` |
| 7 | `cbit/vcell/solver/ode/gui/SolverTaskDescriptionAdvancedPanel.java:110` | timeout-help, orphan `JDialog` + alwaysOnTop | `DialogUtils.showInfoDialog(this, title, msg)` |
| 8 | `cbit/vcell/client/desktop/biomodel/annotations/AddAnnotationsPanel.java:25` | modeless top-level `JFrame`, no logical owner; "Search Ref" from annotation editor. Commented-out `setAlwaysOnTop` at `:53` = devs already hit this | `extends LWChildFrame` or via `ChildWindowManager` |
| 9 | `cbit/vcell/client/desktop/QuickFixSimulation.java:71` (shown `VCDocumentDecorator.java:286`) | `JDialog` whose owner is **null** when `sc == null`/`getActivated()` null | `extends LWTitledDialog` with `findLWOwner(requester)`; never null owner |

Sites 2–7 are the highest-signal cluster: they combine **two** anti-patterns
(orphan `new JDialog()` owner + `setAlwaysOnTop(true)`), independently flagged by
two separate audit passes. All six have a real `WindowManager` component in scope
to pass to `DialogUtils`.

### P2 — Medium: null-parent `JOptionPane.showXxxDialog(null, …)`

Route each through `DialogUtils.showXxx(requester, …)`, threading a real
component in from the owning panel/editor.

- `cbit/vcell/mapping/gui/MolecularTypeSpecsTableModel.java` — **7 sites**
  (`225, 246, 266, 295, 299, 315, 319`), all validation errors.
- `cbit/vcell/mapping/gui/LinkSpecsTableModel.java:109`.
- `cbit/vcell/export/gui/N5SettingsPanel.java:63` (`this` is a `JPanel` — easy fix).
- `cbit/vcell/desktop/ClientLogin.java:29, 47, 127` — startup/login (low-med; often
  no window exists yet, but `:127` runs post-connect when one may).
- `cbit/vcell/client/VCellClientMain.java:169, 187` — fatal/startup (low; no LW yet).

### P3 — Structural / transitional

- `cbit/vcell/client/ChildWindowManager.java:95` `JDiagAdapter` — the `owner == null`
  fallback (`findLWOwner` failed), marked "remove eventually". AWT-parented so not
  fully orphaned. **Retire** once every `ChildWindowManager` host frame extends
  `LWTopFrame` so `findLWOwner` always resolves; then all children go through
  `ModelessChild`/`ParentModalChild`.

### P4 — Low: bypasses LW but passes a real parent (~50 sites)

Not z-order bugs today, but they skip the LW/DialogUtils path; migrate gradually.
- `new JOptionPane(...)` + `createDialog(realComponent, …)` — ~14 sites
  (`DocumentWindow`, `NetworkConstraintsPanel`, `DatabaseWindowManager`,
  `TestingFrameworkWindowManager`, `PDEDataViewer`, …).
- `JOptionPane.showXxxDialog(realComponent, …)` — ~35 sites (many `…mapping.gui`,
  `…microscopy.gui.*wizard`, `MolecularTypePropertiesPanel`, DB tree panels).
- `setAlwaysOnTop(true)` on a **properly parented** dialog, Mac-only front hack:
  `org/vcell/util/gui/VCFileChooser.java:159`, `ROIMultiPaintManager.java:636` —
  acceptable, but confirm the parent is always a real LW component.
- `new JDialog(getFrameForComponent(…)/getWindowAncestor(…), …)` — `AbstractPlotPanel`,
  `OutputFunctionsPanel`, `ElectricalStimulusPanel`, `ROIMultiPaintManager` — OK; the
  owner resolves to a real window. Prefer `LWDialog` if a parentless component can
  reach them.

### Not violations

- Standalone tools with their own `main()`: `XmlTreeViewerApplication(+AboutBox)`,
  `ProgressPopup`, `DropTest2`, and ~10 `main()` demo harnesses (`ScalePanel`,
  `N5SettingsPanel` demo, `MathModelEditorAnnotationPanel`, …).
- Deprecated/dead: `BNGLDebugger` (live BNGL UI is `BNGLDebuggerPanel`, a `JPanel`).
- Already correct: everything through `DialogUtils.*` / `PopupGenerator.*`
  (which delegates to `DialogUtils`).

### Recommended order of work

1. **P1 sites 2–7** — one mechanical pass (orphan-`JDialog`+`alwaysOnTop` →
   `DialogUtils`), highest impact, lowest risk, real parents already in scope.
2. **P1 site 1** (`VcellHelpViewer`) and **site 8** (`AddAnnotationsPanel`) —
   convert to `LWChildFrame`; both are visible, interactive, top-level.
3. **P1 site 9** (`QuickFixSimulation`) — remove the null-owner path.
4. **P2** — thread requesters into the table models; `N5SettingsPanel:63` first.
5. **P3** — retire `JDiagAdapter` after verifying host frames are `LWTopFrame`.
6. **P4** — opportunistic `DialogUtils` migration; not urgent.

## 7. Operating-system requirements & platform support (2026 research)

The framework does **not** use a single mechanism to keep windows in order — it
relies on **two OS contracts with very different durability**. Understanding which
path a window takes explains why "windows behind windows" trouble is concentrated
where it is, and whether the cause is OS regression or our own compliance drift.

### 7.1 The two OS contracts

| Path | Windows in this path | Mechanism | OS contract | Durability |
|------|----------------------|-----------|-------------|------------|
| **A — Native owned window** | `LWDialog`/`LWTitledDialog` (all `DialogUtils` dialogs), and any `JDialog` created with a real owner | `super(ownerWindow, …, DOCUMENT_MODAL)` → a genuine Win32 owned window / Cocoa child `NSWindow` | The **OS itself** keeps an owned window above its owner, regardless of which app is active. No app action needed. | **Strong.** Survives background/foreground changes. |
| **B — Un-owned frame + `toFront()`** | `LWChildFrame` (and `LWTopFrame`) — these extend `JFrame`, which **cannot have an owner** in AWT | one `java.awt.Window.toFront()` at `windowOpened` (also from the "Window" menu / `positionChildren`). No `alwaysOnTop`, no continuous enforcement | `toFront()` maps to `SetForegroundWindow`/`BringWindowToTop` (Windows) and `orderFront:`/activation (macOS) — **best-effort, and refused for a non-foreground app**. | **Weak.** Fails whenever VCell is not the active app. |

`Window.toFront()`'s own Javadoc warns it *"may not permit the Java VM to place
its windows above native applications … depending on whether a window in the VM is
already focused."* Path B leans entirely on that unguaranteed behavior.

### 7.2 macOS — cause is **OS tightening** (that exposed a fragile pattern)

- **macOS 13.3 (Ventura)** began *enforcing* "a window ordered front from a
  non-active application may order **beneath** the active app's windows." Previously
  lenient.
- **macOS 14 (Sonoma)** introduced **cooperative activation**: self-activation is
  now "a request, not a command" the system can deny (`activateIgnoringOtherApps:`
  deprecated). A background app cannot pull its own windows forward unless the
  active app yields.
- JDK evidence: **JDK-8315657** (window not activated on Sonoma; the old
  reactivation hack now deadlocks, disabled on macOS 14+), **JDK-8283590**,
  **JDK-6640082** (`toFront()` not working). **Compose #4231**: on Sonoma 14.2.1 /
  OpenJDK 17.0.10, `toFront()` **and** `requestFocus()` have **no effect** for a
  background app (open, unfixed).
- Path A (owned modal dialogs) stays intact — the OS enforces owner-above-owner
  regardless of activation — **except** full-screen / Spaces / multi-monitor edge
  cases: **JDK-8005011** (modal behind full-screen owner), **JDK-8236162**,
  **JDK-8040659** (modal behind a *non-owner* frame — our mixed model is exposed),
  **JDK-8198684**.
- Aggravator: **Stage Manager** (Ventura+) groups windows per app and can park
  un-owned frames (low-confidence — no JDK bug pins it, treat as secondary).
- Stability aside: **macOS 14.4** turned a JVM JIT `SIGBUS` into an untrappable
  `SIGKILL`, intermittently killing Java on Apple silicon (Oracle advised delaying
  the update). Not z-order, but compounds "macOS broke Java" if on 14.4+.

**macOS verdict:** primarily **OS tightening**. macOS used to tolerate Path B; from
13.3/Sonoma it no longer does. The pattern was never guaranteed, so "OS tightening
exposed a fragile design" is the precise framing.

### 7.3 Windows — cause is **long-standing fragility**, amplified by Win11

- The blocker is not new. `SetForegroundWindow` has **since Windows 2000** refused
  to raise a window for a process that doesn't own the foreground — *"An
  application cannot force a window to the foreground while the user is working with
  another window. Instead, Windows flashes the taskbar button."* The
  `SPI_SETFOREGROUNDLOCKTIMEOUT` foreground-lock (large default) governs this. Doc
  reaffirmed 2025 — **unchanged** API contract.
- So Path B's `toFront()` has **always** been unreliable on Windows for a background
  app: **JDK-6640082**, **JDK-8124521** (after a window has been focused, `toFront()`
  stops even flashing), **JDK-8128222**. These are treated as works-as-designed
  against the OS, not AWT defects.
- What Windows 11 changed is the *surrounding volatility*, making the symptom more
  frequent: **22H2** spontaneous Explorer-to-front restacking; **24H2** a TSF/
  `explorer.exe` focus-stealing regression (VCell loses foreground more often → its
  later `toFront()` calls are demoted more often); **Snap Layouts** and **virtual
  desktops** restack/relocate windows and do **not** bind an un-owned child frame to
  its logical parent (only true owner relationships travel together); **Focus assist/
  DND** can swallow even the taskbar-flash fallback, so the user gets no cue the
  window opened behind.
- Path A holds: Win32 still guarantees an owned window stays above its owner
  (independent of the foreground-lock rules, which govern *activation* not
  *stacking*). Edge cases: **JDK-8005011** (full-screen owner), **JDK-8258600** /
  **JDK-8273918** (HiDPI / dual-screen placement), **JDK-8040659** (modal behind a
  non-owner frame).

**Windows verdict:** primarily **app-design fragility** (Path B was never safe),
amplified by Win11's busier window management. Not a new OS clampdown.

### 7.4 Combined verdict & remediation

The trouble is **both** — and the two causes reinforce each other:
1. **OS tightening** (macOS 13.3/Sonoma) and **amplified volatility** (Win11) have
   made Path B (`toFront()` on un-owned frames) go from "usually works" to
   "frequently fails."
2. **Compliance drift** (§6) makes it worse: every P1 violation (raw un-owned
   `JFrame`, `JOptionPane(null,…)`, orphan-`JDialog` + `alwaysOnTop`) pushes a window
   that *should* be on the strong Path A down onto the weak Path B — and the
   `setAlwaysOnTop(true)` sprinkled around is precisely the field-standard hack for
   "toFront won't raise it," i.e. direct evidence the team already hit this.

**The durable fix is to move non-modal children off Path B onto Path A** — give them
a **real native owner** so the OS enforces stacking instead of the app fighting the
foreground lock:
- Reparent modeless children as **owned** windows (owned modeless `JDialog`, or an
  owned `JWindow`/`Window(owner)`), not bare `JFrame`s. Trade-off: an owned window
  loses its independent taskbar button / independent minimize — which is exactly why
  the original design chose `JFrame`. That design choice is what modern macOS/Win11
  invalidated; it needs revisiting.
- Where a frame must stay a top-level (taskbar presence), the only OS-honored raise
  from a possibly-background app is the `setAlwaysOnTop(true)` → `toFront()` →
  `setAlwaysOnTop(false)` toggle (macOS still honors `orderFrontRegardless`-style
  raises; Windows honors a bounded always-on-top). Centralize this in `LWManager`
  rather than scattering ad-hoc `setAlwaysOnTop` calls.
- First, close the §6 compliance gaps so every dialog is at least on Path A; that
  alone removes a large share of the reports without an architectural change.

**Assumptions to retire:** (a) that `toFront()` reliably raises a window — it does
not for a background app on any current OS; (b) that an un-owned `JFrame` will stay
with its logical parent under Spaces/Stage Manager/Snap/virtual-desktops — only true
owner relationships travel together.
