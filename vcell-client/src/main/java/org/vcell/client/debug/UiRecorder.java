/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.client.debug;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Records what a person does to the running UI as an editable, replayable script.
 *
 * <p>The bridge is already a complete <i>replay</i> vocabulary — {@code /click},
 * {@code /menu}, {@code /setText}, {@code /selectTreeRow} and friends. This is the missing
 * half: a passive {@link AWTEventListener} that watches real input and writes out steps in
 * that same vocabulary, so a recording is replayed by the endpoints that already exist.
 *
 * <h2>Semantic capture, not coordinates</h2>
 * Raw {@code (x, y)} is deliberately never stored. Pixel coordinates break on a different
 * screen, a resized window, or any layout change — which is the exact problem
 * {@code name=} selectors were introduced to solve. Every step therefore names its target
 * with {@link SwingInspector#bestSelector(Component)} and says what was <i>done</i> to it,
 * not where it happened to be.
 *
 * <h2>What it can and cannot see</h2>
 * Only input that reaches the AWT event queue. Real mouse and keyboard input qualifies, and
 * so does {@link SwingInspector#robotClick(String, int)}, which posts native events. A
 * {@code doClick()} does <b>not</b>: it calls its listeners directly, so the bridge's own
 * {@code /click} on a button is invisible here. That is why driving a recording session has
 * to go through {@code /robotClick}.
 *
 * <h2>Timing</h2>
 * Each step carries the real gap that preceded it, taken from the event timestamps. It is
 * meant to be edited afterwards — the pause where someone went for coffee wants shortening,
 * and the beat before the click a viewer needs to notice wants stretching.
 *
 * <p>Dev-only: reachable solely through {@link SwingDebugBridge}, which is off unless
 * {@code -Dvcell.debugBridge=true}.
 */
public final class UiRecorder {

	private static final Logger LG = LogManager.getLogger(UiRecorder.class);

	/** How long after a click a second one still counts as a double-click. */
	private static final long DOUBLE_CLICK_WINDOW_MS = 700;

	/** Ancestors to walk looking for something actionable under the pointer. */
	private static final int ANCESTOR_LIMIT = 8;

	private static final Object LOCK = new Object();

	private static AWTEventListener listener;
	private static final List<Step> steps = new ArrayList<>();
	private static long startedAt;
	private static long lastStepAt;

	/** Source of the most recent step, kept only to coalesce a double-click onto it. */
	private static Component lastSource;

	/** Raw events seen, so "captured nothing" can be distinguished from "saw nothing". */
	private static int rawEvents;

	/** Target resolved at MOUSE_PRESSED, acted on at MOUSE_RELEASED. */
	private static Component pressTarget;
	private static Point pressPoint;
	private static Component pressSource;

	/** Text field currently being typed into; its step is emitted when focus moves on. */
	private static JTextComponent pendingText;
	private static long pendingTextStartedAt;

	/** Windows showing when the last step was emitted, to attribute a new window to it. */
	private static List<Window> windowsAtLastStep = new ArrayList<>();

	/** Where the script is being written, fixed when recording starts. */
	private static File destination;

	/** True when the caller named the destination, so stop() must not move it. */
	private static boolean destinationNamed;

	/** Ordered off-EDT writes: serialize on the EDT, do the file I/O elsewhere. */
	private static ExecutorService writer;

	private UiRecorder() {
	}

	/** One recorded action, in the bridge's own replay vocabulary. */
	private static final class Step {
		String verb;
		String selector;   // every verb except menu
		String menuPath;   // menu only, e.g. "Help>VCell Properties ..."
		String text;       // setText
		boolean enter;     // setText committed with Enter
		int row = -1;      // tree/table
		String rowText;    // what that row DISPLAYS - a row number alone documents nothing
		int index = -1;    // tab
		String tabTitle;   // what that tab READS - an index documents nothing, same as a row
		long delayMs;
		String opensWindow; // a window that appeared after this step, for a replay wait
		String durability;  // "name" | "path" | "id" - how the target had to be addressed
		String note;        // human-readable, for whoever edits the script
	}

	// ---------------------------------------------------------------------
	// Lifecycle
	// ---------------------------------------------------------------------

	/**
	 * Begin recording.
	 *
	 * @param out where to write, or null for {@code <outputDir>/recording-<ts>.json}. The
	 *            destination is fixed now rather than at stop, because the script is
	 *            flushed to it after every step.
	 */
	public static synchronized String start(File out) {
		if (listener != null) {
			return "{\"recording\":true,\"note\":\"already recording\",\"steps\":" + stepCount()
					+ ",\"path\":\"" + SwingInspector.escape(String.valueOf(destination)) + "\"}";
		}
		synchronized (LOCK) {
			steps.clear();
		}
		destinationNamed = (out != null);
		destination = destinationNamed ? out
				: new File(SwingDebugBridge.outputDir(), "recording-" + System.currentTimeMillis() + ".json");
		File parent = destination.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}
		writer = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "ui-recorder-writer");
			t.setDaemon(true);
			return t;
		});
		startedAt = System.currentTimeMillis();
		lastStepAt = startedAt;
		lastSource = null;
		pendingText = null;
		rawEvents = 0;
		pressTarget = null;
		windowsAtLastStep = SwingInspector.showingWindows();
		listener = UiRecorder::onEvent;
		Toolkit.getDefaultToolkit().addAWTEventListener(listener,
				AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
		// Write immediately, so the file exists and is valid even if nothing is ever
		// captured - an empty script is a clearer answer than a missing one.
		flush();
		LG.warn("UI recorder started -> {}", destination.getAbsolutePath());
		return "{\"recording\":true,\"startedAt\":" + startedAt + ",\"steps\":0"
				+ ",\"path\":\"" + SwingInspector.escape(destination.getAbsolutePath()) + "\"}";
	}

	/**
	 * Stop recording and write the script.
	 *
	 * @param out file to write, or null for {@code <outputDir>/recording-<ts>.json}
	 */
	public static synchronized String stop(File out) throws IOException {
		if (listener == null) {
			return "{\"recording\":false,\"note\":\"not recording\"}";
		}
		Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
		listener = null;
		runOnEdt(() -> {
			flushPendingText(System.currentTimeMillis(), false);
			attributeNewWindows(); // a window opened by the final step still belongs to it
		});

		// Drain the incremental writes before deciding anything about the file.
		ExecutorService w = writer;
		writer = null;
		if (w != null) {
			w.shutdown();
			try {
				w.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		File file = destination;
		if (out != null && !out.equals(destination)) {
			File parent = out.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}
			// stop(file) names the final destination. An auto-named working file in the
			// scratch directory is moved there rather than left behind; one the caller
			// named at start is left alone, since they asked for it to be where it is.
			File previous = destination;
			file = out;
			if (!destinationNamed && previous != null && previous.exists() && !previous.delete()) {
				LG.warn("could not remove the working recording at {}", previous.getAbsolutePath());
			}
		}
		writeAtomically(file, toJson());
		destination = null;
		LG.warn("UI recorder stopped: {} step(s) -> {}", stepCount(), file.getAbsolutePath());
		return "{\"recording\":false,\"steps\":" + stepCount()
				+ ",\"path\":\"" + SwingInspector.escape(file.getAbsolutePath()) + "\"}";
	}

	public static synchronized String status() {
		return "{\"recording\":" + (listener != null) + ",\"steps\":" + stepCount()
				+ ",\"rawEvents\":" + rawEvents
				+ (listener != null ? ",\"startedAt\":" + startedAt : "")
				+ (destination != null
						? ",\"path\":\"" + SwingInspector.escape(destination.getAbsolutePath()) + "\"" : "")
				+ '}';
	}

	private static int stepCount() {
		synchronized (LOCK) {
			return steps.size();
		}
	}

	// ---------------------------------------------------------------------
	// Capture
	// ---------------------------------------------------------------------

	/**
	 * Never let a recorder fault reach the application. This runs on the EDT for every
	 * mouse and key event in the process, so an exception escaping here would take out
	 * the UI it is supposed to be observing.
	 */
	private static void onEvent(AWTEvent e) {
		try {
			rawEvents++;
			if (e instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) e;
				// Resolve the target on press and act on release. MOUSE_CLICKED is not
				// usable here: it is synthesized after the release, and a click that
				// dismisses the popup it landed in - every menu pick - destroys its own
				// component first, so the CLICKED never arrives.
				if (me.getID() == MouseEvent.MOUSE_PRESSED) {
					pressTarget = actionableTarget(me.getComponent());
					pressSource = me.getComponent();
					pressPoint = me.getPoint();
				} else if (me.getID() == MouseEvent.MOUSE_RELEASED) {
					onClick(me);
				}
			} else if (e instanceof KeyEvent) {
				KeyEvent ke = (KeyEvent) e;
				if (ke.getID() == KeyEvent.KEY_TYPED) {
					onKeyTyped(ke);
				}
			}
		} catch (Throwable t) {
			LG.error("UI recorder swallowed an error while capturing " + e.getID(), t);
		}
	}

	private static void onClick(MouseEvent me) {
		if (me.getClickCount() > 2) {
			return; // a triple click adds nothing the double already said
		}
		// the press-time target: by release, a menu item's popup may already be gone
		Component target = (pressTarget != null) ? pressTarget : actionableTarget(me.getComponent());
		Component pointSource = (pressTarget != null && pressSource != null) ? pressSource : me.getComponent();
		Point point = (pressTarget != null && pressPoint != null) ? pressPoint : me.getPoint();
		pressTarget = null;
		if (target == null) {
			return;
		}
		long now = System.currentTimeMillis();

		// Typing then clicking elsewhere ends the edit, and the text step must land first.
		if (target != pendingText) {
			flushPendingText(now, false);
		}

		if (target instanceof JMenuItem) {
			JMenuItem item = (JMenuItem) target;
			// A menu that has a popup under it is only being opened. The leaf item carries
			// the whole intent, and the popup it lives in will not exist at replay time,
			// so the opening click is noise.
			if (item instanceof JMenu && ((JMenu) item).getMenuComponentCount() > 0) {
				return;
			}
			String path = menuPathOf(item);
			if (path != null) {
				Step s = newStep("menu", now, target);
				s.selector = null; // addressed by menuPath; the popup's node path is dead by replay
				s.menuPath = path;
				s.note = describe(target);
				emit(s, now, target);
				return;
			}
			// No visible text, so /menu (which matches items by their text) cannot address
			// it. That is not an exotic case: VCell puts icon-only controls straight into
			// the menu bar - the detach toggle is a JMenuItem whose whole label is a
			// tooltip. Fall through and record it as a click on its name instead.
		}

		boolean right = SwingUtilities.isRightMouseButton(me);
		boolean doubleClick = me.getClickCount() == 2;

		// A second click on the same target inside the double-click window upgrades the
		// step already recorded, rather than adding a phantom single click before it.
		if (doubleClick && upgradeLastToDouble(target, now)) {
			return;
		}

		Step s;
		if (target instanceof JTree) {
			JTree tree = (JTree) target;
			Point p = SwingUtilities.convertPoint(pointSource, point, tree);
			int row = tree.getRowForLocation(p.x, p.y);
			if (row < 0) {
				return; // clicked the empty area below the rows
			}
			s = newStep(right ? "rightClickTreeRow" : (doubleClick ? "doubleClickTreeRow" : "selectTreeRow"), now, target);
			s.row = row;
			s.rowText = SwingInspector.rowText(tree, row);
		} else if (target instanceof JTable) {
			JTable table = (JTable) target;
			Point p = SwingUtilities.convertPoint(pointSource, point, table);
			int row = table.rowAtPoint(p);
			if (row < 0) {
				return;
			}
			s = newStep(right ? "rightClickTableRow" : (doubleClick ? "doubleClickTableRow" : "selectTableRow"), now, target);
			s.row = row;
			if (table.getColumnCount() > 0) {
				Object cell = table.getValueAt(row, 0);
				s.rowText = (cell == null) ? null : String.valueOf(cell);
			}
		} else if (target instanceof JTabbedPane) {
			JTabbedPane tabs = (JTabbedPane) target;
			Point p = SwingUtilities.convertPoint(pointSource, point, tabs);
			int index = tabs.indexAtLocation(p.x, p.y);
			if (index < 0) {
				return; // clicked the tab strip's empty run, not a tab
			}
			s = newStep("selectTab", now, target);
			s.index = index;
			s.tabTitle = tabs.getTitleAt(index);
		} else if (target instanceof JTextComponent) {
			// Clicking into a field is only ever a prelude to typing; the setText step
			// that follows carries the whole result, so the click itself is dropped.
			pendingText = (JTextComponent) target;
			pendingTextStartedAt = now;
			return;
		} else {
			s = newStep(right ? "rightClick" : "click", now, target);
		}
		s.note = describe(target);
		emit(s, now, target);
	}

	private static void onKeyTyped(KeyEvent ke) {
		Component c = ke.getComponent();
		if (!(c instanceof JTextComponent)) {
			return;
		}
		JTextComponent tc = (JTextComponent) c;
		if (tc instanceof JPasswordField) {
			// Never capture a password. Account>Login is on the happy path of most
			// tutorials, and a faithful keystroke recorder would write the user's
			// password into a file they are about to commit.
			pendingText = null;
			return;
		}
		long now = System.currentTimeMillis();
		if (pendingText != tc) {
			flushPendingText(now, false);
			pendingText = tc;
			pendingTextStartedAt = now;
		}
		char ch = ke.getKeyChar();
		if (ch == '\n' || ch == '\r') {
			flushPendingText(now, true);
		}
	}

	/**
	 * Emit the accumulated edit as one {@code setText} step carrying the field's final
	 * contents. Storing the result rather than the keystrokes means backspaces, selection
	 * replacement and autocomplete all come out right for free.
	 */
	private static void flushPendingText(long now, boolean enter) {
		JTextComponent tc = pendingText;
		pendingText = null;
		if (tc == null || tc instanceof JPasswordField) {
			return;
		}
		String text = tc.getText();
		if (text == null) {
			text = "";
		}
		Step s = new Step();
		s.verb = "setText";
		s.selector = SwingInspector.bestSelector(tc);
		s.durability = durabilityOf(s.selector);
		s.text = text;
		s.enter = enter;
		s.delayMs = Math.max(0, pendingTextStartedAt - lastStepAt);
		s.note = describe(tc);
		emit(s, now, tc);
	}

	private static Step newStep(String verb, long now, Component target) {
		Step s = new Step();
		s.verb = verb;
		s.selector = SwingInspector.bestSelector(target);
		s.durability = durabilityOf(s.selector);
		s.delayMs = Math.max(0, now - lastStepAt);
		return s;
	}

	/**
	 * Which tier {@link SwingInspector#bestSelector} had to fall back to. Recorded so the
	 * naming debt is visible in the artifact: a step marked "path" will break the next time
	 * that panel is rearranged, and the fix is a setName() at the component's construction
	 * site. Better to read that off a fresh recording than to discover it a year later.
	 */
	private static String durabilityOf(String selector) {
		if (selector == null) {
			return null;
		}
		if (selector.startsWith("name=")) {
			return "name";
		}
		return selector.matches("c\\d+") ? "id" : "path";
	}

	private static void emit(Step s, long now, Component source) {
		attributeNewWindows();
		synchronized (LOCK) {
			steps.add(s);
		}
		lastStepAt = now;
		lastSource = source;
		windowsAtLastStep = SwingInspector.showingWindows();
		flush();
	}

	private static boolean upgradeLastToDouble(Component target, long now) {
		synchronized (LOCK) {
			if (steps.isEmpty() || lastSource != target || (now - lastStepAt) > DOUBLE_CLICK_WINDOW_MS) {
				return false;
			}
			Step last = steps.get(steps.size() - 1);
			switch (last.verb) {
				case "selectTreeRow":
					last.verb = "doubleClickTreeRow";
					return true;
				case "selectTableRow":
					last.verb = "doubleClickTableRow";
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * If a window has appeared since the last step, record it on that step. Replay then
	 * waits for it instead of sleeping, which is the difference between a script that
	 * works on the machine it was recorded on and one that works in CI.
	 */
	private static void attributeNewWindows() {
		List<Window> now = SwingInspector.showingWindows();
		Step last;
		synchronized (LOCK) {
			if (steps.isEmpty()) {
				windowsAtLastStep = now;
				return;
			}
			last = steps.get(steps.size() - 1);
		}
		if (last.opensWindow != null) {
			return;
		}
		for (Window w : now) {
			String title = titleOf(w);
			if (title == null || title.isEmpty()) {
				continue;
			}
			// Compare by TITLE, not window identity. Detaching a child window swaps an
			// owned dialog for an un-owned frame, so a brand-new Window object appears
			// carrying a title that was already on screen. By identity that reads as
			// "this step opened a window", and replay would then wait for something that
			// never went away - a wait that always passes and therefore guards nothing.
			boolean titleWasShowing = false;
			for (Window before : windowsAtLastStep) {
				if (title.equals(titleOf(before))) {
					titleWasShowing = true;
					break;
				}
			}
			if (!titleWasShowing) {
				last.opensWindow = title;
				return;
			}
		}
	}

	// ---------------------------------------------------------------------
	// Target resolution
	// ---------------------------------------------------------------------

	/**
	 * Walk up from the component the event reached to the thing the user meant to act on —
	 * a click lands on the label inside a button, but the button is the actionable target.
	 * Falls back to the original component when nothing recognizable is above it.
	 */
	private static Component actionableTarget(Component c) {
		Component cur = c;
		for (int i = 0; cur != null && i < ANCESTOR_LIMIT; i++) {
			if (cur instanceof JMenuItem || cur instanceof AbstractButton || cur instanceof JTextComponent
					|| cur instanceof JTree || cur instanceof JTable || cur instanceof JTabbedPane) {
				return cur;
			}
			cur = cur.getParent();
		}
		return c;
	}

	/**
	 * The {@code /menu} path of an item — {@code "Help>VCell Properties ..."} — built by
	 * hopping from each popup to the menu that invoked it, since a popup's parent is the
	 * popup layer rather than its menu.
	 *
	 * @return the path, or null if the item has no visible text for {@code /menu} to match
	 */
	private static String menuPathOf(JMenuItem item) {
		String leaf = item.getText();
		if (leaf == null || leaf.isEmpty()) {
			return null; // an icon-only control; /menu has nothing to match it by
		}
		LinkedList<String> parts = new LinkedList<>();
		Component cur = item;
		int guard = 0;
		while (cur != null && guard++ < 16) {
			if (cur instanceof JMenuItem) {
				String text = ((JMenuItem) cur).getText();
				if (text != null && !text.isEmpty()) {
					parts.addFirst(text);
				}
			}
			if (cur instanceof JPopupMenu) {
				cur = ((JPopupMenu) cur).getInvoker();
			} else {
				cur = cur.getParent();
			}
		}
		return parts.isEmpty() ? null : String.join(">", parts);
	}

	/** A short human-readable note so a person editing the script can tell what a step is. */
	private static String describe(Component c) {
		// An anonymous subclass - and VCell builds plenty of them - has an EMPTY simple
		// name, which would leave the note blank. Walk up to the first class that has one.
		Class<?> type = c.getClass();
		while (type != null && type.getSimpleName().isEmpty()) {
			type = type.getSuperclass();
		}
		StringBuilder sb = new StringBuilder(type == null ? "Component" : type.getSimpleName());
		String label = null;
		if (c instanceof AbstractButton) {
			label = ((AbstractButton) c).getText();
			if (label == null || label.isEmpty()) {
				label = ((AbstractButton) c).getToolTipText();
			}
		} else if (c instanceof javax.swing.JComponent) {
			label = ((javax.swing.JComponent) c).getToolTipText();
		}
		if (label != null && !label.isEmpty()) {
			sb.append(" '").append(label.length() > 60 ? label.substring(0, 60) + "..." : label).append('\'');
		}
		return sb.toString();
	}

	private static String titleOf(Window w) {
		if (w instanceof Frame) {
			return ((Frame) w).getTitle();
		}
		if (w instanceof Dialog) {
			return ((Dialog) w).getTitle();
		}
		return null;
	}

	private static void runOnEdt(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
			return;
		}
		try {
			SwingUtilities.invokeAndWait(r);
		} catch (Exception e) {
			LG.error("UI recorder failed on the EDT", e);
		}
	}

	// ---------------------------------------------------------------------
	// Output
	// ---------------------------------------------------------------------

	/**
	 * Write the script so far. Called after every step, so killing the client mid-session
	 * costs at most the step in progress rather than the whole take - which matters most
	 * for exactly the long recording someone least wants to redo.
	 *
	 * <p>Serializing happens here, on the EDT, where the step list is consistent; the file
	 * I/O is handed to a single background thread so a slow or networked filesystem cannot
	 * stutter the UI being recorded. One thread, so writes stay in order.
	 */
	private static void flush() {
		final File file = destination;
		final ExecutorService w = writer;
		if (file == null || w == null) {
			return;
		}
		final String json = toJson();
		try {
			w.submit(() -> writeAtomically(file, json));
		} catch (RejectedExecutionException e) {
			// stop() is draining; its own synchronous write carries the final content
		}
	}

	/**
	 * Write via a temporary file and rename. A crash during a plain write would leave a
	 * half-written file, which is worse than no file at all: the recording would look
	 * present and fail to parse. A rename means what is on disk is always a whole script.
	 */
	private static void writeAtomically(File file, String json) {
		File part = new File(file.getParentFile(), file.getName() + ".part");
		try {
			Files.write(part.toPath(), json.getBytes(StandardCharsets.UTF_8));
			try {
				Files.move(part.toPath(), file.toPath(),
						StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (IOException atomicUnsupported) {
				Files.move(part.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			LG.error("failed to write the recording to " + file.getAbsolutePath(), e);
		}
	}

	private static String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n  \"recordedAt\": \"").append(Instant.now().toString()).append("\",\n");
		sb.append("  \"durationMs\": ").append(System.currentTimeMillis() - startedAt).append(",\n");
		sb.append("  \"steps\": [\n");
		List<Step> snapshot;
		synchronized (LOCK) {
			snapshot = new ArrayList<>(steps);
		}
		for (int i = 0; i < snapshot.size(); i++) {
			Step s = snapshot.get(i);
			sb.append("    {");
			sb.append("\"verb\": \"").append(s.verb).append('"');
			if (s.selector != null) {
				sb.append(", \"selector\": \"").append(SwingInspector.escape(s.selector)).append('"');
			}
			if (s.menuPath != null) {
				sb.append(", \"path\": \"").append(SwingInspector.escape(s.menuPath)).append('"');
			}
			if (s.text != null) {
				sb.append(", \"text\": \"").append(SwingInspector.escape(s.text)).append('"');
				sb.append(", \"enter\": ").append(s.enter);
			}
			if (s.row >= 0) {
				sb.append(", \"row\": ").append(s.row);
				if (s.rowText != null) {
					sb.append(", \"rowText\": \"").append(SwingInspector.escape(s.rowText)).append('"');
				}
			}
			if (s.index >= 0) {
				sb.append(", \"index\": ").append(s.index);
				if (s.tabTitle != null) {
					sb.append(", \"tabTitle\": \"").append(SwingInspector.escape(s.tabTitle)).append('"');
				}
			}
			sb.append(", \"delayMs\": ").append(s.delayMs);
			if (s.durability != null && !"name".equals(s.durability)) {
				// only worth saying when it is NOT the durable form
				sb.append(", \"durability\": \"").append(s.durability).append('"');
			}
			if (s.opensWindow != null) {
				sb.append(", \"opensWindow\": \"").append(SwingInspector.escape(s.opensWindow)).append('"');
			}
			if (s.note != null) {
				sb.append(", \"note\": \"").append(SwingInspector.escape(s.note)).append('"');
			}
			sb.append('}');
			if (i < snapshot.size() - 1) {
				sb.append(',');
			}
			sb.append('\n');
		}
		sb.append("  ]\n}\n");
		return sb.toString();
	}
}
