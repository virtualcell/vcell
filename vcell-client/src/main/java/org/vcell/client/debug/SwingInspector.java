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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * Read-only (mostly) introspection of the live Swing UI, intended to give an
 * automated agent / developer a text and pixel view of the running application
 * without a running debugger.
 *
 * <p>This is the Swing analog of a browser's DOM + accessibility tree. Every
 * node in {@link #dumpWindowsJson()} carries a stable {@code path} selector
 * (e.g. {@code "0/3/2"} = window 0, child 3, child 2) that can be handed back to
 * {@link #findByPath(String)} / {@link #click(String)} to drive the component.
 *
 * <p>All AWT/Swing state is read on the Event Dispatch Thread; callers may be on
 * any thread. Enabled only via {@link SwingDebugBridge}; has no effect in a
 * normal production launch.
 */
public final class SwingInspector {

	private static final int MAX_TEXT = 200;
	private static final int MAX_TABLE_ROWS = 25;
	private static final int MAX_TABLE_COLS = 15;
	private static final int MAX_LIST_ITEMS = 50;

	private SwingInspector() {
	}

	// ---------------------------------------------------------------------
	// Window enumeration
	// ---------------------------------------------------------------------

	/**
	 * @return the showing top-level windows, in a stable order (AWT creation
	 *         order). Index into this list is the first segment of a node path.
	 */
	public static List<Window> showingWindows() {
		return onEdt(() -> {
			List<Window> out = new ArrayList<>();
			for (Window w : Window.getWindows()) {
				if (w.isShowing()) {
					out.add(w);
				}
			}
			return out;
		});
	}

	// ---------------------------------------------------------------------
	// Tree serialization
	// ---------------------------------------------------------------------

	/** @return a JSON array of the full component tree of every showing window. */
	public static String dumpWindowsJson() {
		return dumpWindowsJson(Integer.MAX_VALUE);
	}

	/**
	 * @param maxDepth maximum component nesting depth to emit (root window = depth 0)
	 * @return a JSON array of the component tree of every showing window
	 */
	public static String dumpWindowsJson(final int maxDepth) {
		return onEdt(() -> {
			List<Window> windows = new ArrayList<>();
			for (Window w : Window.getWindows()) {
				if (w.isShowing()) {
					windows.add(w);
				}
			}
			StringBuilder sb = new StringBuilder(4096);
			sb.append('[');
			for (int i = 0; i < windows.size(); i++) {
				if (i > 0) {
					sb.append(',');
				}
				appendNode(sb, windows.get(i), Integer.toString(i), 0, maxDepth);
			}
			sb.append(']');
			return sb.toString();
		});
	}

	private static void appendNode(StringBuilder sb, Component c, String path, int depth, int maxDepth) {
		sb.append('{');
		kv(sb, "path", path);
		comma(sb);
		kv(sb, "class", c.getClass().getName());

		String name = c.getName();
		if (name != null && !name.isEmpty()) {
			comma(sb);
			kv(sb, "name", name);
		}
		String text = textOf(c);
		if (text != null && !text.isEmpty()) {
			comma(sb);
			kv(sb, "text", truncate(text));
		}
		if (c instanceof JComponent) {
			String tip = ((JComponent) c).getToolTipText();
			if (tip != null && !tip.isEmpty()) {
				comma(sb);
				kv(sb, "tooltip", tip);
			}
		}
		comma(sb);
		raw(sb, "visible", c.isVisible());
		comma(sb);
		raw(sb, "showing", c.isShowing());
		comma(sb);
		raw(sb, "enabled", c.isEnabled());
		if (c instanceof AbstractButton) {
			comma(sb);
			raw(sb, "selected", ((AbstractButton) c).isSelected());
		}

		Rectangle b = c.getBounds();
		sb.append(",\"bounds\":{\"x\":").append(b.x)
				.append(",\"y\":").append(b.y)
				.append(",\"w\":").append(b.width)
				.append(",\"h\":").append(b.height).append('}');

		appendTypeSpecific(sb, c);

		if (c instanceof Container && depth < maxDepth) {
			Component[] kids = ((Container) c).getComponents();
			if (kids.length > 0) {
				sb.append(",\"children\":[");
				for (int i = 0; i < kids.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					appendNode(sb, kids[i], path + '/' + i, depth + 1, maxDepth);
				}
				sb.append(']');
			}
		}
		sb.append('}');
	}

	private static String textOf(Component c) {
		if (c instanceof AbstractButton) {
			return ((AbstractButton) c).getText();
		}
		if (c instanceof JLabel) {
			return ((JLabel) c).getText();
		}
		if (c instanceof JTextComponent) {
			return ((JTextComponent) c).getText();
		}
		if (c instanceof JFrame) {
			return ((JFrame) c).getTitle();
		}
		if (c instanceof JDialog) {
			return ((JDialog) c).getTitle();
		}
		return null;
	}

	/**
	 * Emit compact structural detail for content-bearing widgets that the plain
	 * component hierarchy does not otherwise reveal: tabbed-pane tab titles,
	 * combo/list selection, and table cell values (capped).
	 */
	private static void appendTypeSpecific(StringBuilder sb, Component c) {
		if (c instanceof JTabbedPane) {
			JTabbedPane tp = (JTabbedPane) c;
			sb.append(",\"tabs\":{\"selected\":").append(tp.getSelectedIndex()).append(",\"titles\":[");
			for (int i = 0; i < tp.getTabCount(); i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append('"').append(escape(nz(tp.getTitleAt(i)))).append('"');
			}
			sb.append("]}");
		} else if (c instanceof JComboBox) {
			JComboBox<?> cb = (JComboBox<?>) c;
			sb.append(",\"combo\":{\"selectedIndex\":").append(cb.getSelectedIndex());
			sb.append(",\"selectedItem\":\"").append(escape(truncate(String.valueOf(cb.getSelectedItem())))).append('"');
			sb.append(",\"itemCount\":").append(cb.getItemCount()).append('}');
		} else if (c instanceof JList) {
			JList<?> jl = (JList<?>) c;
			int size = jl.getModel().getSize();
			sb.append(",\"list\":{\"size\":").append(size).append(",\"selectedIndices\":[");
			int[] sel = jl.getSelectedIndices();
			for (int i = 0; i < sel.length; i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(sel[i]);
			}
			sb.append("],\"items\":[");
			int shown = Math.min(size, MAX_LIST_ITEMS);
			for (int i = 0; i < shown; i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append('"').append(escape(truncate(String.valueOf(jl.getModel().getElementAt(i))))).append('"');
			}
			sb.append("],\"truncated\":").append(size > shown).append('}');
		} else if (c instanceof JTable) {
			appendTable(sb, (JTable) c);
		}
	}

	private static void appendTable(StringBuilder sb, JTable t) {
		int rows = t.getRowCount();
		int cols = t.getColumnCount();
		int maxR = Math.min(rows, MAX_TABLE_ROWS);
		int maxC = Math.min(cols, MAX_TABLE_COLS);
		sb.append(",\"table\":{\"rowCount\":").append(rows).append(",\"columnCount\":").append(cols);
		sb.append(",\"selectedRow\":").append(t.getSelectedRow());
		sb.append(",\"columns\":[");
		for (int col = 0; col < maxC; col++) {
			if (col > 0) {
				sb.append(',');
			}
			sb.append('"').append(escape(nz(t.getColumnName(col)))).append('"');
		}
		sb.append("],\"cells\":[");
		for (int r = 0; r < maxR; r++) {
			if (r > 0) {
				sb.append(',');
			}
			sb.append('[');
			for (int col = 0; col < maxC; col++) {
				if (col > 0) {
					sb.append(',');
				}
				String cell;
				try {
					cell = String.valueOf(t.getValueAt(r, col));
				} catch (Exception e) {
					cell = "?";
				}
				sb.append('"').append(escape(truncate(cell))).append('"');
			}
			sb.append(']');
		}
		sb.append("],\"truncated\":").append(rows > maxR || cols > maxC).append('}');
	}

	// ---------------------------------------------------------------------
	// Lookup + interaction
	// ---------------------------------------------------------------------

	/**
	 * Resolve a node path (as emitted by {@link #dumpWindowsJson()}) to a live
	 * component. First segment indexes {@link #showingWindows()}; remaining
	 * segments index {@link Container#getComponents()}.
	 *
	 * @return the component, or {@code null} if the path does not resolve
	 */
	public static Component findByPath(final String path) {
		return onEdt(() -> {
			String[] segs = path.split("/");
			List<Window> windows = new ArrayList<>();
			for (Window w : Window.getWindows()) {
				if (w.isShowing()) {
					windows.add(w);
				}
			}
			int wi = Integer.parseInt(segs[0]);
			if (wi < 0 || wi >= windows.size()) {
				return null;
			}
			Component cur = windows.get(wi);
			for (int s = 1; s < segs.length; s++) {
				if (!(cur instanceof Container)) {
					return null;
				}
				Component[] kids = ((Container) cur).getComponents();
				int ci = Integer.parseInt(segs[s]);
				if (ci < 0 || ci >= kids.length) {
					return null;
				}
				cur = kids[ci];
			}
			return cur;
		});
	}

	/** Depth-first search for the first component whose {@link Component#getName()} matches. */
	public static Component findByName(final String targetName) {
		return onEdt(() -> {
			for (Window w : Window.getWindows()) {
				if (w.isShowing()) {
					Component hit = searchByName(w, targetName);
					if (hit != null) {
						return hit;
					}
				}
			}
			return null;
		});
	}

	private static Component searchByName(Component c, String targetName) {
		if (targetName.equals(c.getName())) {
			return c;
		}
		if (c instanceof Container) {
			for (Component kid : ((Container) c).getComponents()) {
				Component hit = searchByName(kid, targetName);
				if (hit != null) {
					return hit;
				}
			}
		}
		return null;
	}

	/**
	 * Click a component identified by node path. Buttons/checkboxes use
	 * {@link AbstractButton#doClick()} (EDT-safe, no native cursor movement);
	 * anything else gets a synthetic {@link Robot} mouse click at its center.
	 *
	 * @return true if a component resolved and was clicked
	 */
	public static boolean click(String path) {
		Component c = findByPath(path);
		if (c == null) {
			return false;
		}
		if (c instanceof AbstractButton) {
			onEdt(() -> {
				((AbstractButton) c).doClick();
				return null;
			});
			return true;
		}
		// non-button: synthesize a real mouse click at the component center
		Point screenPt = onEdt(() -> {
			if (!c.isShowing()) {
				return null;
			}
			Point loc = c.getLocationOnScreen();
			Dimension d = c.getSize();
			return new Point(loc.x + d.width / 2, loc.y + d.height / 2);
		});
		if (screenPt == null) {
			return false;
		}
		try {
			Robot robot = new Robot();
			robot.mouseMove(screenPt.x, screenPt.y);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("robot click failed at " + screenPt, e);
		}
	}

	/**
	 * Set the text of a {@link JTextComponent} at the given path. This drives the
	 * document model directly (firing document listeners); it does not simulate
	 * per-key events.
	 *
	 * @param commit if true and the target is a {@link JTextField}, also fire its
	 *               action event (equivalent to pressing Enter), which is how many
	 *               VCell fields commit their value
	 * @return true if a text component resolved and was updated
	 */
	public static boolean setText(String path, String text, boolean commit) {
		Component c = findByPath(path);
		if (!(c instanceof JTextComponent)) {
			return false;
		}
		onEdt(() -> {
			JTextComponent tc = (JTextComponent) c;
			tc.setText(text);
			if (commit && tc instanceof JTextField) {
				((JTextField) tc).postActionEvent();
			}
			return null;
		});
		return true;
	}

	/**
	 * Select a tab by index on a {@link JTabbedPane} at the given path.
	 *
	 * @return true if the pane resolved and the index was valid
	 */
	public static boolean selectTab(String path, int index) {
		Component c = findByPath(path);
		if (!(c instanceof JTabbedPane)) {
			return false;
		}
		return Boolean.TRUE.equals(onEdt(() -> {
			JTabbedPane tp = (JTabbedPane) c;
			if (index < 0 || index >= tp.getTabCount()) {
				return false;
			}
			tp.setSelectedIndex(index);
			return true;
		}));
	}

	// ---------------------------------------------------------------------
	// Screenshots
	// ---------------------------------------------------------------------

	/**
	 * Capture the on-screen pixels of a window to a PNG.
	 *
	 * @param windowIndex index into {@link #showingWindows()}, or -1 for the
	 *                    active/focused window
	 * @param outDir      directory to write into (created if needed)
	 * @return the written file
	 */
	public static File screenshot(int windowIndex, File outDir) throws Exception {
		if (GraphicsEnvironment.isHeadless()) {
			throw new IllegalStateException("cannot screenshot in a headless environment");
		}
		final Rectangle bounds = onEdt(() -> {
			List<Window> windows = new ArrayList<>();
			for (Window w : Window.getWindows()) {
				if (w.isShowing()) {
					windows.add(w);
				}
			}
			Window target = null;
			if (windowIndex >= 0 && windowIndex < windows.size()) {
				target = windows.get(windowIndex);
			} else {
				for (Window w : windows) {
					if (w.isActive()) {
						target = w;
						break;
					}
				}
				if (target == null && !windows.isEmpty()) {
					target = windows.get(windows.size() - 1);
				}
			}
			return target == null ? null : target.getBounds();
		});
		if (bounds == null) {
			throw new IllegalStateException("no showing window to capture (index=" + windowIndex + ")");
		}
		outDir.mkdirs();
		BufferedImage img = new Robot().createScreenCapture(bounds);
		File out = new File(outDir, "vcell-window-" + (windowIndex < 0 ? "active" : windowIndex) + ".png");
		ImageIO.write(img, "png", out);
		return out;
	}

	// ---------------------------------------------------------------------
	// EDT + JSON helpers
	// ---------------------------------------------------------------------

	private static <T> T onEdt(Supplier<T> supplier) {
		if (SwingUtilities.isEventDispatchThread()) {
			return supplier.get();
		}
		final AtomicReference<T> result = new AtomicReference<>();
		final AtomicReference<Throwable> error = new AtomicReference<>();
		try {
			SwingUtilities.invokeAndWait(() -> {
				try {
					result.set(supplier.get());
				} catch (Throwable t) {
					error.set(t);
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("interrupted waiting on EDT", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		}
		if (error.get() != null) {
			throw new RuntimeException(error.get());
		}
		return result.get();
	}

	private static void comma(StringBuilder sb) {
		sb.append(',');
	}

	private static void kv(StringBuilder sb, String key, String value) {
		sb.append('"').append(key).append("\":\"").append(escape(value)).append('"');
	}

	private static void raw(StringBuilder sb, String key, boolean value) {
		sb.append('"').append(key).append("\":").append(value);
	}

	private static String truncate(String s) {
		return s.length() <= MAX_TEXT ? s : s.substring(0, MAX_TEXT) + "…";
	}

	private static String nz(String s) {
		return s == null ? "" : s;
	}

	private static String escape(String s) {
		StringBuilder sb = new StringBuilder(s.length() + 8);
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
				case '"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				default:
					if (ch < 0x20) {
						sb.append(String.format("\\u%04x", (int) ch));
					} else {
						sb.append(ch);
					}
			}
		}
		return sb.toString();
	}
}
