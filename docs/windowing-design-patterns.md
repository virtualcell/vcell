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
