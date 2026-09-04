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
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import cbit.vcell.mapping.SimulationContext;

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
	private static final int MAX_TREE_ROWS = 100;

	// ---------------------------------------------------------------------
	// Stable component registry: each component seen in a tree/window dump is
	// assigned a stable id ("c0", "c1", ...) so callers can reference it in
	// later requests without re-resolving a fragile path selector. Non-invasive
	// (does not touch Component.getName(), which VCell uses itself). Ids persist
	// for the session; a stale id whose component is gone just fails the action.
	// ---------------------------------------------------------------------
	private static final Object REGISTRY_LOCK = new Object();
	private static final java.util.Map<Component, String> idByComponent = new java.util.IdentityHashMap<>();
	private static final java.util.Map<String, Component> componentById = new java.util.HashMap<>();
	private static int idSeq = 0;

	/** @return a stable id for the component, assigning one on first sighting. */
	static String idFor(Component c) {
		synchronized (REGISTRY_LOCK) {
			String id = idByComponent.get(c);
			if (id == null) {
				id = "c" + (idSeq++);
				idByComponent.put(c, id);
				componentById.put(id, c);
			}
			return id;
		}
	}

	/** @return the component previously registered under this id, or null. */
	public static Component findById(String id) {
		synchronized (REGISTRY_LOCK) {
			return componentById.get(id);
		}
	}

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
		kv(sb, "id", idFor(c));
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
		if (c instanceof Window) {
			// Window ownership and iconified state, so a scenario script can assert on the
			// two things that actually decide whether a user can get a window out of the way:
			//   owner     - an OWNED window is held above its owner by the OS and gets no
			//               taskbar/dock button of its own.
			//   canIconify- only a Frame has an iconified state at all; a Dialog cannot be
			//               minimized, however much the user would like to.
			Window w = (Window) c;
			Window owner = w.getOwner();
			comma(sb);
			if (owner == null) {
				sb.append("\"owner\":null");
			} else {
				kv(sb, "owner", nz(textOf(owner)));
			}
			comma(sb);
			raw(sb, "canIconify", c instanceof Frame);
			if (c instanceof Frame) {
				comma(sb);
				raw(sb, "iconified", (((Frame) c).getExtendedState() & Frame.ICONIFIED) != 0);
			}
		}
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
		} else if (c instanceof JTree) {
			appendTree(sb, (JTree) c);
		} else if (c instanceof JMenu) {
			// popup contents are not in the component hierarchy until shown;
			// serialize them statically so menus are discoverable without popups
			sb.append(",\"menuItems\":");
			appendMenuItems(sb, (JMenu) c, 0);
		}
	}

	private static final int MAX_MENU_DEPTH = 5;

	private static void appendMenuItems(StringBuilder sb, JMenu menu, int depth) {
		sb.append('[');
		for (int i = 0; i < menu.getItemCount(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			JMenuItem item = menu.getItem(i);
			if (item == null) {
				sb.append("{\"separator\":true}");
				continue;
			}
			sb.append('{');
			kv(sb, "text", nz(item.getText()));
			comma(sb);
			kv(sb, "id", idFor(item));
			comma(sb);
			raw(sb, "enabled", item.isEnabled());
			if (item.getAccelerator() != null) {
				comma(sb);
				kv(sb, "accelerator", String.valueOf(item.getAccelerator()));
			}
			if (item instanceof JMenu && depth < MAX_MENU_DEPTH) {
				sb.append(",\"items\":");
				appendMenuItems(sb, (JMenu) item, depth + 1);
			}
			sb.append('}');
		}
		sb.append(']');
	}

	/**
	 * The text a {@link JTree} row actually displays.
	 *
	 * <p>A node's {@code toString()} is useless in VCell: its database trees hold domain
	 * objects and render them through a custom {@link javax.swing.tree.TreeCellRenderer},
	 * so the raw value serializes as {@code PublicationInfo@415f5f4f} while the user sees
	 * "Lee 2026 Systems-level consequences…". Ask the renderer what it would paint, so a
	 * driver reads the same labels a person does.
	 *
	 * <p>Falls back through {@link JTree#convertValueToText} (honoured by trees that
	 * override it instead of supplying a renderer) to {@code toString()}, and never
	 * propagates a renderer's exception — a renderer that assumes a paint context must
	 * not break the whole tree dump.
	 */
	/** The text a JTree row displays, for a caller that has only the row number. */
	static String rowText(JTree tree, int row) {
		javax.swing.tree.TreePath path = tree.getPathForRow(row);
		return (path == null) ? null : treeRowText(tree, row, path);
	}

	private static String treeRowText(JTree tree, int row, javax.swing.tree.TreePath path) {
		Object value = path.getLastPathComponent();
		boolean selected = tree.isRowSelected(row);
		boolean expanded = tree.isExpanded(row);
		boolean leaf = tree.getModel().isLeaf(value);

		javax.swing.tree.TreeCellRenderer renderer = tree.getCellRenderer();
		if (renderer != null) {
			try {
				Component rendered = renderer.getTreeCellRendererComponent(
						tree, value, selected, expanded, leaf, row, false);
				String text = renderedText(rendered);
				if (text != null && !text.isEmpty()) {
					return stripHtml(text);
				}
			} catch (RuntimeException e) {
				// fall through to the plainer forms below
			}
		}
		try {
			String converted = tree.convertValueToText(value, selected, expanded, leaf, row, false);
			if (converted != null && !converted.isEmpty()) {
				return stripHtml(converted);
			}
		} catch (RuntimeException e) {
			// fall through
		}
		return String.valueOf(value);
	}

	/**
	 * What the row IS, as opposed to what it says.
	 *
	 * <p>VCell's trees carry domain objects, and the rendered label often does not identify
	 * them: four applications called Application0/2 and two copies say nothing about being
	 * NFSim, SpringSaLaD, ODE or PDE, and only the row's icon distinguishes them on screen.
	 * The model knows exactly, so report it rather than making a caller guess from an icon
	 * or from which tabs happen to appear.
	 *
	 * <p>Read through {@link SimulationContext} directly. This class lives in
	 * {@code vcell-client}, which already depends on the biology model, so naming the type
	 * costs nothing a reflective lookup would have saved - and it gets compiler checking,
	 * so renaming {@code getApplicationType} or the enum breaks the build here rather than
	 * silently emptying a field at runtime.
	 */
	private static void appendUserObject(StringBuilder sb, javax.swing.tree.TreePath path) {
		Object node = path.getLastPathComponent();
		Object userObject = node;
		if (node instanceof javax.swing.tree.DefaultMutableTreeNode) {
			userObject = ((javax.swing.tree.DefaultMutableTreeNode) node).getUserObject();
		}
		if (userObject == null) {
			return;
		}
		sb.append(",\"userType\":\"").append(escape(userObject.getClass().getSimpleName())).append('"');
		String appType = applicationTypeOf(path);
		if (appType != null) {
			sb.append(",\"applicationType\":\"").append(escape(appType)).append('"');
		}
	}

	/** @return the row's application type, or null if the row is not an application. */
	private static String applicationTypeOf(javax.swing.tree.TreePath path) {
		Object node = path.getLastPathComponent();
		Object userObject = node;
		if (node instanceof javax.swing.tree.DefaultMutableTreeNode) {
			userObject = ((javax.swing.tree.DefaultMutableTreeNode) node).getUserObject();
		}
		if (!(userObject instanceof SimulationContext)) {
			return null; // most rows are not applications
		}
		SimulationContext.Application type = ((SimulationContext) userObject).getApplicationType();
		return (type == null) ? null : type.name();
	}

	/** Text carried by a renderer's component; composite renderers are flattened. */
	private static String renderedText(Component c) {
		if (c instanceof JLabel) {
			return ((JLabel) c).getText();
		}
		if (c instanceof AbstractButton) {
			return ((AbstractButton) c).getText();
		}
		if (c instanceof JTextComponent) {
			return ((JTextComponent) c).getText();
		}
		if (c instanceof Container) {
			StringBuilder sb = new StringBuilder();
			for (Component kid : ((Container) c).getComponents()) {
				String kidText = renderedText(kid);
				if (kidText != null && !kidText.isEmpty()) {
					if (sb.length() > 0) {
						sb.append(' ');
					}
					sb.append(kidText);
				}
			}
			return sb.toString();
		}
		return null;
	}

	/** VCell renderers often emit {@code <html>…} for styling; report the readable text. */
	private static String stripHtml(String s) {
		if (s == null || !s.regionMatches(true, 0, "<html", 0, 5)) {
			return s;
		}
		String text = s.replaceAll("(?i)<br\\s*/?>", " ")
				.replaceAll("<[^>]*>", "")
				.replace("&nbsp;", " ")
				.replace("&amp;", "&")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replaceAll("\\s+", " ")
				.trim();
		return text.isEmpty() ? s : text;
	}

	private static void appendTree(StringBuilder sb, JTree t) {
		int rows = t.getRowCount();
		int maxR = Math.min(rows, MAX_TREE_ROWS);
		sb.append(",\"tree\":{\"rowCount\":").append(rows);
		sb.append(",\"selectedRow\":").append(t.getMinSelectionRow());
		sb.append(",\"rows\":[");
		for (int r = 0; r < maxR; r++) {
			if (r > 0) {
				sb.append(',');
			}
			javax.swing.tree.TreePath path = t.getPathForRow(r);
			String text;
			try {
				text = treeRowText(t, r, path);
			} catch (Exception e) {
				text = "?";
			}
			sb.append("{\"row\":").append(r);
			sb.append(",\"depth\":").append(path.getPathCount() - 1);
			sb.append(",\"expanded\":").append(t.isExpanded(r));
			sb.append(",\"text\":\"").append(escape(truncate(text))).append('"');
			appendUserObject(sb, path);
			sb.append('}');
		}
		sb.append("],\"truncated\":").append(rows > maxR).append('}');
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

	private static final java.util.regex.Pattern NAME_WITH_INDEX =
			java.util.regex.Pattern.compile("^(.*)\\[(\\d+)\\]$");

	/**
	 * Resolve a selector to a live component. Every endpoint that takes a
	 * {@code path} parameter accepts any of these three forms, which are
	 * syntactically distinct:
	 *
	 * <ul>
	 * <li><b>registry id</b> — {@code c42}, as emitted in each node's {@code id}.
	 *     Stable across dumps.</li>
	 * <li><b>name</b> — {@code name=SearchButton}, matching
	 *     {@link Component#getName()}. The most robust form: it survives layout
	 *     changes entirely. When several components share a name (VCell reuses
	 *     panels — e.g. one database search panel per tab), a <i>showing</i> match
	 *     wins over a hidden one; add an index, {@code name=SearchButton[1]}, to
	 *     pick a specific one out of the {@code /find} ordering.</li>
	 * <li><b>node path</b> — {@code 0/3/2}, as emitted in {@code path}. First
	 *     segment indexes {@link #showingWindows()}, the rest index
	 *     {@link Container#getComponents()}. Brittle; prefer the forms above.</li>
	 * </ul>
	 *
	 * @return the component, or {@code null} if the selector does not resolve
	 *         (including a malformed selector — resolution never throws)
	 */
	public static Component findByPath(final String path) {
		if (path == null || path.isEmpty()) {
			return null;
		}
		if (path.matches("c\\d+")) {
			return findById(path);
		}
		if (path.startsWith("name=")) {
			return findByNameSelector(path.substring("name=".length()));
		}
		return onEdt(() -> {
			String[] segs = path.split("/");
			List<Window> windows = new ArrayList<>();
			for (Window w : Window.getWindows()) {
				if (w.isShowing()) {
					windows.add(w);
				}
			}
			Component cur = null;
			for (int s = 0; s < segs.length; s++) {
				int i;
				try {
					i = Integer.parseInt(segs[s]);
				} catch (NumberFormatException e) {
					return null; // not a node path after all; report "did not resolve"
				}
				if (s == 0) {
					if (i < 0 || i >= windows.size()) {
						return null;
					}
					cur = windows.get(i);
					continue;
				}
				if (!(cur instanceof Container)) {
					return null;
				}
				Component[] kids = ((Container) cur).getComponents();
				if (i < 0 || i >= kids.length) {
					return null;
				}
				cur = kids[i];
			}
			return cur;
		});
	}

	/** Resolve the {@code name=...} selector form, with optional {@code [index]} suffix. */
	private static Component findByNameSelector(String spec) {
		String name = spec;
		int index = -1;
		java.util.regex.Matcher m = NAME_WITH_INDEX.matcher(spec);
		if (m.matches()) {
			name = m.group(1);
			index = Integer.parseInt(m.group(2));
		}
		final String targetName = name;
		final int wanted = index;
		return onEdt(() -> {
			List<PathMatch> matches = collectAll(null, targetName, null, null);
			if (matches.isEmpty()) {
				return null;
			}
			if (wanted >= 0) {
				return wanted < matches.size() ? matches.get(wanted).component : null;
			}
			// an unqualified name should act on what the user can actually see
			for (PathMatch pm : matches) {
				if (pm.component.isShowing()) {
					return pm.component;
				}
			}
			return matches.get(0).component;
		});
	}

	/**
	 * Dump the listeners registered on the component at the given path as JSON:
	 * action listeners (with their class names) plus counts of the generic
	 * component/mouse listeners. Answers "is this control actually wired up?".
	 */
	public static String dumpListenersJson(final String path) {
		return onEdt(() -> {
			Component c = findByPath(path);
			if (c == null) {
				return "{\"error\":\"path did not resolve\"}";
			}
			StringBuilder sb = new StringBuilder(256);
			sb.append('{');
			kv(sb, "class", c.getClass().getName());
			sb.append(',');
			kv(sb, "name", String.valueOf(c.getName()));
			if (c instanceof AbstractButton) {
				sb.append(",\"actionListeners\":[");
				java.awt.event.ActionListener[] als = ((AbstractButton) c).getActionListeners();
				for (int i = 0; i < als.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append('"').append(escape(als[i].getClass().getName())).append('"');
				}
				sb.append(']');
				sb.append(",\"actionCommand\":\"")
						.append(escape(String.valueOf(((AbstractButton) c).getActionCommand()))).append('"');
			}
			sb.append(",\"mouseListeners\":").append(c.getMouseListeners().length);
			sb.append('}');
			return sb.toString();
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

	// ---------------------------------------------------------------------
	// Semantic finders: locate components by what they ARE (type/name/text)
	// instead of where they sit (index path), so automation survives layout
	// shifts between builds and sessions.
	// ---------------------------------------------------------------------

	/** A matched component plus the index path it was found at. */
	private static final class PathMatch {
		final Component component;
		final String path;

		PathMatch(Component component, String path) {
			this.component = component;
			this.path = path;
		}
	}

	/**
	 * JSON array of components matching ALL given criteria (null criteria are
	 * ignored). Each element is a full node as in the tree dump, minus children.
	 *
	 * @param type         simple class name matched against the component's class
	 *                     or any superclass (e.g. "JButton", "AbstractButton")
	 * @param name         exact {@link Component#getName()} match
	 * @param text         exact match on the node's {@code text} (button/label
	 *                     text, text-component content, window title)
	 * @param textContains case-insensitive substring of that text
	 * @param limit        maximum matches to emit (&lt;=0 = unlimited)
	 */
	public static String findMatchesJson(String type, String name, String text, String textContains, int limit) {
		return onEdt(() -> emitMatches(collectAll(type, name, text, textContains),
				limit <= 0 ? Integer.MAX_VALUE : limit));
	}

	/**
	 * Poll the semantic finder until the requested state holds or the timeout
	 * elapses. States: {@code showing} (default) — at least one match is showing;
	 * {@code enabled} — at least one match is showing and enabled; {@code gone} —
	 * no showing match. Callers must be off the EDT (each poll hops onto it).
	 *
	 * @return JSON {@code {"satisfied":bool,"state":..,"elapsedMs":N,"matches":[..]}}
	 */
	public static String waitFor(String type, String name, String text, String textContains,
			String state, long timeoutMs, long intervalMs) {
		String st = (state == null || state.isEmpty()) ? "showing" : state;
		boolean wantGone = "gone".equals(st);
		boolean needEnabled = "enabled".equals(st);
		long start = System.currentTimeMillis();
		while (true) {
			String matches = matchesInStateJson(type, name, text, textContains, needEnabled, 10);
			boolean present = !"[]".equals(matches);
			boolean satisfied = wantGone != present;
			long elapsed = System.currentTimeMillis() - start;
			if (satisfied || elapsed >= timeoutMs) {
				return "{\"satisfied\":" + satisfied + ",\"state\":\"" + escape(st) + "\",\"elapsedMs\":" + elapsed
						+ ",\"matches\":" + matches + '}';
			}
			try {
				Thread.sleep(intervalMs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return "{\"satisfied\":false,\"interrupted\":true,\"elapsedMs\":"
						+ (System.currentTimeMillis() - start) + '}';
			}
		}
	}

	/**
	 * Wait for the EDT to drain: two no-op round-trips (the first flushes events
	 * queued before the call, the second anything the first batch enqueued).
	 *
	 * @return milliseconds the round-trips took
	 */
	public static long waitForIdle() {
		long start = System.currentTimeMillis();
		onEdt(() -> null);
		onEdt(() -> null);
		return System.currentTimeMillis() - start;
	}

	/** Matches that are showing (and optionally enabled), as a JSON array. EDT-hopping. */
	private static String matchesInStateJson(String type, String name, String text, String textContains,
			boolean needEnabled, int limit) {
		return onEdt(() -> {
			List<PathMatch> filtered = new ArrayList<>();
			for (PathMatch m : collectAll(type, name, text, textContains)) {
				if (m.component.isShowing() && (!needEnabled || m.component.isEnabled())) {
					filtered.add(m);
				}
			}
			return emitMatches(filtered, limit);
		});
	}

	/** DFS every showing window for criteria matches. Must run on the EDT. */
	private static List<PathMatch> collectAll(String type, String name, String text, String textContains) {
		List<PathMatch> out = new ArrayList<>();
		List<Window> windows = new ArrayList<>();
		for (Window w : Window.getWindows()) {
			if (w.isShowing()) {
				windows.add(w);
			}
		}
		for (int i = 0; i < windows.size(); i++) {
			collectMatches(windows.get(i), Integer.toString(i), type, name, text, textContains, out);
		}
		return out;
	}

	private static void collectMatches(Component c, String path, String type, String name, String text,
			String textContains, List<PathMatch> out) {
		if (matchesCriteria(c, type, name, text, textContains)) {
			out.add(new PathMatch(c, path));
		}
		if (c instanceof Container) {
			Component[] kids = ((Container) c).getComponents();
			for (int i = 0; i < kids.length; i++) {
				collectMatches(kids[i], path + '/' + i, type, name, text, textContains, out);
			}
		}
	}

	private static boolean matchesCriteria(Component c, String type, String name, String text, String textContains) {
		if (type != null) {
			boolean hit = false;
			for (Class<?> k = c.getClass(); k != null; k = k.getSuperclass()) {
				if (k.getSimpleName().equals(type)) {
					hit = true;
					break;
				}
			}
			if (!hit) {
				return false;
			}
		}
		if (name != null && !name.equals(c.getName())) {
			return false;
		}
		String t = textOf(c);
		if (text != null && !text.equals(t)) {
			return false;
		}
		if (textContains != null && (t == null || !t.toLowerCase().contains(textContains.toLowerCase()))) {
			return false;
		}
		return true;
	}

	/** Emit matches as a JSON array of childless nodes. Must run on the EDT. */
	private static String emitMatches(List<PathMatch> hits, int limit) {
		StringBuilder sb = new StringBuilder(1024);
		sb.append('[');
		int shown = Math.min(hits.size(), limit);
		for (int i = 0; i < shown; i++) {
			if (i > 0) {
				sb.append(',');
			}
			// depth == maxDepth, so appendNode emits the node without children
			appendNode(sb, hits.get(i).component, hits.get(i).path, 0, 0);
		}
		sb.append(']');
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	// Menus: enumerate and activate by visible text, no popup choreography
	// ---------------------------------------------------------------------

	/**
	 * @return JSON array: for every showing window with a {@link JMenuBar}, its
	 *         complete menu structure (nested items, separators, accelerators),
	 *         read from the models without opening any popup.
	 */
	public static String menusJson() {
		return onEdt(() -> {
			StringBuilder sb = new StringBuilder(2048);
			sb.append('[');
			List<Window> windows = showingWindows();
			boolean first = true;
			for (int i = 0; i < windows.size(); i++) {
				JMenuBar bar = menuBarOf(windows.get(i));
				if (bar == null) {
					continue;
				}
				if (!first) {
					sb.append(',');
				}
				first = false;
				sb.append("{\"window\":").append(i).append(',');
				kv(sb, "title", nz(textOf(windows.get(i))));
				sb.append(",\"menus\":[");
				for (int m = 0; m < bar.getMenuCount(); m++) {
					if (m > 0) {
						sb.append(',');
					}
					JMenu menu = bar.getMenu(m);
					if (menu == null) {
						sb.append("{}");
						continue;
					}
					sb.append('{');
					kv(sb, "text", nz(menu.getText()));
					comma(sb);
					kv(sb, "id", idFor(menu));
					comma(sb);
					raw(sb, "enabled", menu.isEnabled());
					sb.append(",\"items\":");
					appendMenuItems(sb, menu, 0);
					sb.append('}');
				}
				sb.append("]}");
			}
			sb.append(']');
			return sb.toString();
		});
	}

	/**
	 * Activate a menu item addressed by its visible text, e.g.
	 * {@code "Account > Login"} — segments separated by {@code '>'}, matched
	 * case-insensitively against menu/item text. The leaf item's action fires
	 * via {@code doClick()} without animating popups, so callers no longer walk
	 * transient popup windows by index. Menus that build their items lazily in
	 * a {@link javax.swing.event.MenuListener} get that listener fired first,
	 * the same way opening the menu would.
	 *
	 * @param windowIndex restrict the search to one window (index into
	 *                    {@link #showingWindows()}), or -1 for all windows
	 * @return JSON describing what was clicked, or an error
	 */
	public static String clickMenu(final String menuPath, final int windowIndex) {
		return onEdt(() -> {
			String[] segs = menuPath.split(">");
			for (int i = 0; i < segs.length; i++) {
				segs[i] = segs[i].trim();
			}
			if (segs.length == 0 || segs[0].isEmpty()) {
				return "{\"clicked\":false,\"error\":\"empty menu path\"}";
			}
			List<Window> windows = showingWindows();
			for (int i = 0; i < windows.size(); i++) {
				if (windowIndex >= 0 && windowIndex != i) {
					continue;
				}
				JMenuBar bar = menuBarOf(windows.get(i));
				if (bar == null) {
					continue;
				}
				JMenu top = null;
				for (int m = 0; m < bar.getMenuCount(); m++) {
					JMenu menu = bar.getMenu(m);
					if (menu != null && segs[0].equalsIgnoreCase(nz(menu.getText()).trim())) {
						top = menu;
						break;
					}
				}
				if (top == null) {
					continue;
				}
				if (segs.length == 1) {
					return "{\"clicked\":false,\"error\":\"path must name an item inside menu '"
							+ escape(segs[0]) + "', e.g. " + escape(segs[0]) + ">Item\"}";
				}
				JMenuItem cur = top;
				StringBuilder resolved = new StringBuilder(nz(top.getText()));
				for (int s = 1; s < segs.length; s++) {
					if (!(cur instanceof JMenu)) {
						return "{\"clicked\":false,\"error\":\"'" + escape(resolved.toString())
								+ "' is not a submenu\"}";
					}
					JMenuItem next = childItem((JMenu) cur, segs[s]);
					if (next == null) {
						return "{\"clicked\":false,\"error\":\"no item '" + escape(segs[s]) + "' under '"
								+ escape(resolved.toString()) + "'\"}";
					}
					cur = next;
					resolved.append(" > ").append(nz(cur.getText()));
				}
				if (!cur.isEnabled()) {
					return "{\"clicked\":false,\"error\":\"item '" + escape(resolved.toString())
							+ "' is disabled\"}";
				}
				final JMenuItem target = cur;
				UiRecorder.noteMenu(resolved.toString(), target);
				// fire-and-forget: the action may open a modal dialog
				SwingUtilities.invokeLater(target::doClick);
				return "{\"clicked\":true,\"item\":\"" + escape(resolved.toString()) + "\",\"id\":\""
						+ idFor(target) + "\",\"window\":" + i + '}';
			}
			return "{\"clicked\":false,\"error\":\"no showing window has a menu '" + escape(segs[0]) + "'\"}";
		});
	}

	private static JMenuBar menuBarOf(Window w) {
		if (w instanceof JFrame) {
			return ((JFrame) w).getJMenuBar();
		}
		if (w instanceof JDialog) {
			return ((JDialog) w).getJMenuBar();
		}
		return null;
	}

	/** Find a direct child item by text, firing lazy-population MenuListeners if needed. */
	private static JMenuItem childItem(JMenu menu, String text) {
		JMenuItem hit = scanItems(menu, text);
		if (hit == null && menu.getMenuListeners().length > 0) {
			// menus populated on menuSelected (e.g. recent-file lists) are empty
			// until "opened"; fire their listeners the way opening them would
			javax.swing.event.MenuEvent ev = new javax.swing.event.MenuEvent(menu);
			for (javax.swing.event.MenuListener l : menu.getMenuListeners()) {
				try {
					l.menuSelected(ev);
				} catch (RuntimeException e) {
					// a listener assuming a visible popup may object; matching below still gets its chance
				}
			}
			hit = scanItems(menu, text);
		}
		return hit;
	}

	private static JMenuItem scanItems(JMenu menu, String text) {
		for (int i = 0; i < menu.getItemCount(); i++) {
			JMenuItem item = menu.getItem(i);
			if (item != null && text.equalsIgnoreCase(nz(item.getText()).trim())) {
				return item;
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
	/**
	 * Ask a window to iconify (minimize) or restore, and report what the OS actually did.
	 *
	 * Deliberately NOT "did we call setExtendedState" - the caller needs to know whether the
	 * window really minimized, because the interesting case is the one where it CANNOT. A
	 * Dialog has no iconified state at all, so an owned child window silently refuses to
	 * minimize; that refusal is the thing worth asserting on, and it is invisible from the
	 * Java call alone.
	 *
	 * @return the window's iconified state after the attempt, or null if there is no such window
	 */
	public static Boolean iconify(String path, boolean iconified) {
		Component c = findByPath(path);
		if (!(c instanceof Window)) {
			return null;
		}
		final Window w = (Window) c;
		onEdt(() -> {
			if (w instanceof Frame) {
				Frame f = (Frame) w;
				int st = f.getExtendedState();
				f.setExtendedState(iconified ? (st | Frame.ICONIFIED) : (st & ~Frame.ICONIFIED));
				if (!iconified) {
					// de-iconifying is not enough on its own for every window manager
					f.toFront();
				}
			}
			// a Dialog has no iconified state; nothing to do, and that is the point
			return null;
		});
		// Poll rather than sleep a fixed amount. Minimize/restore is animated and asynchronous
		// (the macOS dock genie, Windows' own transition), and how long it takes is not ours to
		// predict - a fixed wait either flakes on a slow machine or wastes time on a fast one.
		// A window that is never going to honour the request simply costs the full timeout once.
		final long deadline = System.currentTimeMillis() + 3000;
		Boolean state;
		do {
			state = onEdt(() -> (w instanceof Frame)
					&& ((((Frame) w).getExtendedState() & Frame.ICONIFIED) != 0));
			if (state != null && state == iconified) {
				return state;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		} while (System.currentTimeMillis() < deadline);
		return state;
	}

	/**
	 * Move/resize a window, so a scenario can put it somewhere the user might have dragged it
	 * to. Worth having as a first-class action: a window that is never moved sits wherever it
	 * was opened, which is exactly where "helpfully" re-centring it would put it back - so a
	 * test that does not move the window cannot see that bug at all.
	 *
	 * @return the window's bounds afterwards, or null if there is no window at that path
	 */
	public static Rectangle setWindowBounds(String path, Rectangle r) {
		Component c = findByPath(path);
		if (!(c instanceof Window)) {
			return null;
		}
		final Window w = (Window) c;
		return onEdt(() -> {
			w.setBounds(r);
			return w.getBounds();
		});
	}

	public static boolean click(String path) {
		Component c = findByPath(path);
		if (c == null) {
			return false;
		}
		if (c instanceof AbstractButton) {
			// Report before acting: the action may open a modal dialog, and the step
			// belongs in the script whether or not anything blocks afterwards.
			UiRecorder.noteClick(c);
			// fire-and-forget: the action may open a modal dialog, which would
			// block invokeAndWait (and with it the whole bridge) until dismissed
			SwingUtilities.invokeLater(((AbstractButton) c)::doClick);
			return true;
		}
		// non-button: synthesize a real mouse click at the component center
		Point screenPt = centerOnScreen(c);
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

	// ---------------------------------------------------------------------
	// JTable rows — the counterpart to the JTree row operations below. Needed
	// for any list-of-things rendered as a table rather than a tree, including
	// the file chooser, where selecting a row is the only way to pick a file.
	// ---------------------------------------------------------------------

	/**
	 * Select (and scroll to) a row of the {@link JTable} at the given path. Rows and
	 * columns are as reported by the {@code table} block in the JSON dump — note that
	 * both are <b>view</b> indices, so they follow the user's current sort order.
	 *
	 * @param column column to make lead, or -1 to select the whole row
	 * @return true if the table resolved and the row was in range
	 */
	public static boolean selectTableRow(final String path, final int row, final int column) {
		return Boolean.TRUE.equals(onEdt(() -> {
			JTable table = tableAt(path, row, column);
			if (table == null) {
				return false;
			}
			int col = column < 0 ? 0 : column;
			Object cell = (table.getColumnCount() > 0) ? table.getValueAt(row, 0) : null;
			UiRecorder.noteRow("selectTableRow", table, row, cell == null ? null : String.valueOf(cell));
			table.setRowSelectionInterval(row, row);
			if (column >= 0 && table.getColumnSelectionAllowed()) {
				table.setColumnSelectionInterval(col, col);
			}
			table.scrollRectToVisible(table.getCellRect(row, col, true));
			return true;
		}));
	}

	/**
	 * Double-click a row of the {@link JTable} at the given path. A synthetic
	 * {@link Robot} click pair is required rather than a selection change because
	 * table-backed UIs commonly act on the raw {@code MouseEvent} click count — the
	 * file chooser opens the selected file that way.
	 *
	 * @return true if the table/row resolved and the double-click was issued
	 */
	public static boolean doubleClickTableRow(final String path, final int row, final int column) {
		return clickTableRow(path, row, column, InputEvent.BUTTON1_DOWN_MASK, 2);
	}

	/**
	 * Right-click a row of the {@link JTable} at the given path, to open its context
	 * menu. The row is selected first, as a real right-click would.
	 *
	 * @return true if the table/row resolved and the right-click was issued
	 */
	public static boolean rightClickTableRow(final String path, final int row, final int column) {
		return clickTableRow(path, row, column, InputEvent.BUTTON3_DOWN_MASK, 1);
	}

	private static boolean clickTableRow(final String path, final int row, final int column,
			final int buttonMask, final int clickCount) {
		Point screenPt = onEdt(() -> {
			JTable table = tableAt(path, row, column);
			if (table == null || !table.isShowing()) {
				return null;
			}
			int col = column < 0 ? 0 : column;
			table.setRowSelectionInterval(row, row);
			Rectangle cell = table.getCellRect(row, col, true);
			table.scrollRectToVisible(cell);
			// re-read: scrolling moves the cell under the viewport
			cell = table.getCellRect(row, col, true);
			Point loc = table.getLocationOnScreen();
			return new Point(loc.x + cell.x + Math.min(cell.width / 2, 60),
					loc.y + cell.y + cell.height / 2);
		});
		if (screenPt == null) {
			return false;
		}
		try {
			Robot robot = new Robot();
			robot.mouseMove(screenPt.x, screenPt.y);
			for (int i = 0; i < clickCount; i++) {
				robot.mousePress(buttonMask);
				robot.mouseRelease(buttonMask);
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException("robot table click failed at " + screenPt, e);
		}
	}

	/** Resolve a selector to a JTable and bounds-check row/column. Must run on the EDT. */
	private static JTable tableAt(String path, int row, int column) {
		Component c = findByPath(path);
		if (!(c instanceof JTable)) {
			return null;
		}
		JTable table = (JTable) c;
		if (row < 0 || row >= table.getRowCount()) {
			return null;
		}
		if (column >= table.getColumnCount()) {
			return null;
		}
		return table;
	}

	/**
	 * Select (and scroll to) a specific row of the {@link JTree} at the given
	 * path. Rows are as reported by the {@code tree} block in the JSON dump.
	 *
	 * @return true if the tree resolved and the row was in range
	 */
	public static boolean selectTreeRow(final String path, final int row) {
		return onEdt(() -> {
			Component c = findByPath(path);
			if (!(c instanceof JTree)) {
				return false;
			}
			JTree tree = (JTree) c;
			if (row < 0 || row >= tree.getRowCount()) {
				return false;
			}
			UiRecorder.noteRow("selectTreeRow", tree, row, rowText(tree, row));
			tree.setSelectionRow(row);
			tree.scrollRowToVisible(row);
			return true;
		});
	}

	/**
	 * Expand or collapse a row of the {@link JTree} at the given path. Needed to reach
	 * nodes that are not visible yet: row indices only cover currently-expanded rows,
	 * so a driver walks down a tree by expanding and re-reading the rows.
	 *
	 * @return true if the tree resolved and the row was in range
	 */
	public static boolean expandTreeRow(final String path, final int row, final boolean expand) {
		return Boolean.TRUE.equals(onEdt(() -> {
			Component c = findByPath(path);
			if (!(c instanceof JTree)) {
				return false;
			}
			JTree tree = (JTree) c;
			if (row < 0 || row >= tree.getRowCount()) {
				return false;
			}
			UiRecorder.noteExpand(tree, row, rowText(tree, row), expand);
			if (expand) {
				tree.expandRow(row);
			} else {
				tree.collapseRow(row);
			}
			tree.scrollRowToVisible(row);
			return true;
		}));
	}

	/**
	 * Double-click a row of the {@link JTree} at the given path with a synthetic
	 * {@link Robot} click pair. A real double-click is required (rather than firing a
	 * listener directly) because VCell's database trees open a document from the raw
	 * {@link java.awt.event.MouseEvent} — {@code MOUSE_PRESSED} with
	 * {@code getClickCount() == 2} — so no higher-level API reproduces it.
	 *
	 * @return true if the tree/row resolved and the double-click was issued
	 */
	public static boolean doubleClickTreeRow(final String path, final int row) {
		Point screenPt = onEdt(() -> {
			Component c = findByPath(path);
			if (!(c instanceof JTree)) {
				return null;
			}
			JTree tree = (JTree) c;
			if (row < 0 || row >= tree.getRowCount() || !tree.isShowing()) {
				return null;
			}
			tree.setSelectionRow(row);
			tree.scrollRowToVisible(row);
			Rectangle rb = tree.getRowBounds(row);
			if (rb == null) {
				return null;
			}
			Point loc = tree.getLocationOnScreen();
			return new Point(loc.x + rb.x + Math.min(rb.width / 2, 40), loc.y + rb.y + rb.height / 2);
		});
		if (screenPt == null) {
			return false;
		}
		try {
			Robot robot = new Robot();
			robot.mouseMove(screenPt.x, screenPt.y);
			for (int i = 0; i < 2; i++) {
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException("robot double-click failed at " + screenPt, e);
		}
	}

	/**
	 * Right-click a specific row of the {@link JTree} at the given path, which is
	 * how VCell's tree explorer opens a node's context menu (e.g. right-clicking
	 * the "Applications" node to create a new Application). The row is selected
	 * first, then a synthetic {@link Robot} right-click (button 3) is issued at
	 * the row's on-screen location so the resulting {@link javax.swing.JPopupMenu}
	 * can be driven with {@link #click(String)}.
	 *
	 * @return true if the tree/row resolved and the right-click was issued
	 */
	public static boolean rightClickTreeRow(final String path, final int row) {
		Point screenPt = onEdt(() -> {
			Component c = findByPath(path);
			if (!(c instanceof JTree)) {
				return null;
			}
			JTree tree = (JTree) c;
			if (row < 0 || row >= tree.getRowCount() || !tree.isShowing()) {
				return null;
			}
			tree.setSelectionRow(row);
			tree.scrollRowToVisible(row);
			Rectangle rb = tree.getRowBounds(row);
			if (rb == null) {
				return null;
			}
			Point loc = tree.getLocationOnScreen();
			return new Point(loc.x + rb.x + Math.min(rb.width / 2, 24), loc.y + rb.y + rb.height / 2);
		});
		return rightClickAt(screenPt);
	}

	/**
	 * Right-click the center of the (non-tree) component at the given path, to
	 * open its context menu.
	 *
	 * @return true if a showing component resolved and the right-click was issued
	 */
	public static boolean rightClick(final String path) {
		Point screenPt = onEdt(() -> {
			Component c = findByPath(path);
			if (c == null || !c.isShowing()) {
				return null;
			}
			Point loc = c.getLocationOnScreen();
			Dimension d = c.getSize();
			return new Point(loc.x + d.width / 2, loc.y + d.height / 2);
		});
		return rightClickAt(screenPt);
	}

	private static boolean rightClickAt(Point screenPt) {
		if (screenPt == null) {
			return false;
		}
		try {
			Robot robot = new Robot();
			robot.mouseMove(screenPt.x, screenPt.y);
			robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("robot right-click failed at " + screenPt, e);
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
		UiRecorder.noteSetText((JTextComponent) c, text, commit);
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
			UiRecorder.noteTab(tp, index);
			tp.setSelectedIndex(index);
			return true;
		}));
	}

	// ---------------------------------------------------------------------
	// Deep inspection of a single component
	// ---------------------------------------------------------------------

	/**
	 * Extended property dump for one component — everything the tree dump
	 * deliberately omits for brevity: full class hierarchy, focus state,
	 * colors/font, accessible role/name/description, widget-specific detail
	 * and listener counts.
	 */
	public static String propsJson(final String selector) {
		return onEdt(() -> {
			Component c = findByPath(selector);
			if (c == null) {
				return "{\"error\":\"selector did not resolve\"}";
			}
			StringBuilder sb = new StringBuilder(1024);
			sb.append('{');
			kv(sb, "id", idFor(c));
			sb.append(",\"classChain\":[");
			boolean first = true;
			for (Class<?> k = c.getClass(); k != null && k != Object.class; k = k.getSuperclass()) {
				if (!first) {
					sb.append(',');
				}
				first = false;
				sb.append('"').append(escape(k.getName())).append('"');
			}
			sb.append(']');
			comma(sb);
			kv(sb, "name", nz(c.getName()));
			String text = textOf(c);
			if (text != null) {
				comma(sb);
				kv(sb, "text", text.length() <= 2000 ? text : text.substring(0, 2000) + "…");
			}
			comma(sb);
			raw(sb, "visible", c.isVisible());
			comma(sb);
			raw(sb, "showing", c.isShowing());
			comma(sb);
			raw(sb, "enabled", c.isEnabled());
			comma(sb);
			raw(sb, "focusable", c.isFocusable());
			comma(sb);
			raw(sb, "hasFocus", c.hasFocus());
			comma(sb);
			raw(sb, "opaque", c.isOpaque());
			Rectangle b = c.getBounds();
			sb.append(",\"bounds\":{\"x\":").append(b.x).append(",\"y\":").append(b.y)
					.append(",\"w\":").append(b.width).append(",\"h\":").append(b.height).append('}');
			if (c.isShowing()) {
				Point p = c.getLocationOnScreen();
				sb.append(",\"screen\":{\"x\":").append(p.x).append(",\"y\":").append(p.y).append('}');
			}
			java.awt.Font f = c.getFont();
			if (f != null) {
				sb.append(",\"font\":\"").append(escape(f.getName() + ' ' + f.getSize()
						+ (f.isBold() ? " bold" : "") + (f.isItalic() ? " italic" : ""))).append('"');
			}
			sb.append(",\"foreground\":\"").append(hexColor(c.getForeground()));
			sb.append("\",\"background\":\"").append(hexColor(c.getBackground())).append('"');
			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				if (jc.getToolTipText() != null) {
					comma(sb);
					kv(sb, "tooltip", jc.getToolTipText());
				}
				if (jc.getBorder() != null) {
					comma(sb);
					kv(sb, "border", jc.getBorder().getClass().getName());
				}
			}
			javax.accessibility.AccessibleContext ac = c.getAccessibleContext();
			if (ac != null) {
				sb.append(",\"accessible\":{");
				kv(sb, "role", ac.getAccessibleRole().toDisplayString());
				comma(sb);
				kv(sb, "name", nz(ac.getAccessibleName()));
				comma(sb);
				kv(sb, "description", nz(ac.getAccessibleDescription()));
				sb.append('}');
			}
			if (c instanceof AbstractButton) {
				AbstractButton btn = (AbstractButton) c;
				comma(sb);
				kv(sb, "actionCommand", nz(btn.getActionCommand()));
				comma(sb);
				raw(sb, "selected", btn.isSelected());
			}
			if (c instanceof JTextComponent) {
				JTextComponent tc = (JTextComponent) c;
				comma(sb);
				raw(sb, "editable", tc.isEditable());
				sb.append(",\"caretPosition\":").append(tc.getCaretPosition());
				sb.append(",\"documentLength\":").append(tc.getDocument().getLength());
			}
			sb.append(",\"listeners\":{");
			if (c instanceof AbstractButton) {
				sb.append("\"action\":").append(((AbstractButton) c).getActionListeners().length).append(',');
			}
			sb.append("\"mouse\":").append(c.getMouseListeners().length);
			sb.append(",\"key\":").append(c.getKeyListeners().length);
			sb.append(",\"focus\":").append(c.getFocusListeners().length);
			sb.append('}');
			sb.append('}');
			return sb.toString();
		});
	}

	private static String hexColor(java.awt.Color c) {
		return c == null ? "" : String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}

	// ---------------------------------------------------------------------
	// Highlight: show a human where a selector points
	// ---------------------------------------------------------------------

	private static final class GlassState {
		final Component pane;
		final boolean wasVisible;

		GlassState(Component pane, boolean wasVisible) {
			this.pane = pane;
			this.wasVisible = wasVisible;
		}
	}

	/** Original glass pane per root, saved while a highlight overlay is up. EDT-confined. */
	private static final java.util.Map<javax.swing.JRootPane, GlassState> savedGlass = new java.util.IdentityHashMap<>();

	/**
	 * Flash a translucent red overlay over the component so a human watching the
	 * screen can see what a selector resolves to. Swaps the window's glass pane
	 * for the duration and restores the original (and its visibility — VCell
	 * uses visible glass panes to block input during long tasks) afterwards.
	 *
	 * @return true if the component resolved and is showing
	 */
	public static boolean highlight(final String selector, final int durationMs) {
		return Boolean.TRUE.equals(onEdt(() -> {
			Component c = findByPath(selector);
			if (c == null || !c.isShowing()) {
				return false;
			}
			javax.swing.JRootPane root = SwingUtilities.getRootPane(c);
			if (root == null) {
				return false;
			}
			if (!savedGlass.containsKey(root)) {
				Component old = root.getGlassPane();
				savedGlass.put(root, new GlassState(old, old != null && old.isVisible()));
			}
			final Rectangle r = (c.getParent() == null) ? new Rectangle(0, 0, c.getWidth(), c.getHeight())
					: SwingUtilities.convertRectangle(c.getParent(), c.getBounds(), root.getGlassPane());
			JComponent overlay = new JComponent() {
				@Override
				protected void paintComponent(java.awt.Graphics g) {
					java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
					g2.setColor(new java.awt.Color(255, 0, 0, 60));
					g2.fillRect(r.x, r.y, r.width, r.height);
					g2.setStroke(new java.awt.BasicStroke(3f));
					g2.setColor(java.awt.Color.RED);
					g2.drawRect(r.x, r.y, r.width, r.height);
				}
			};
			overlay.setOpaque(false);
			root.setGlassPane(overlay);
			overlay.setVisible(true);
			javax.swing.Timer timer = new javax.swing.Timer(durationMs, e -> {
				GlassState orig = savedGlass.remove(root);
				if (orig != null && orig.pane != null) {
					root.setGlassPane(orig.pane);
					orig.pane.setVisible(orig.wasVisible);
				}
			});
			timer.setRepeats(false);
			timer.start();
			return true;
		}));
	}

	// ---------------------------------------------------------------------
	// Screenshots
	// ---------------------------------------------------------------------

	/**
	 * Render a window to a PNG by asking Swing to paint it into an off-screen
	 * buffer ({@link Component#printAll}). Unlike a Robot screen grab this shows
	 * the window's own content even when other applications overlap it, and it
	 * does not require the window to be front-most.
	 *
	 * @param windowIndex index into {@link #showingWindows()}, or -1 for the
	 *                    active/focused window
	 * @param outDir      directory to write into (created if needed)
	 * @return the written file
	 */
	public static File screenshot(int windowIndex, File outDir) throws Exception {
		return screenshot(windowIndex, outDir, 1.0, null);
	}

	/**
	 * As above, but scaled and named.
	 *
	 * <p>Scaling belongs here rather than in a caller because the help system caps images at
	 * {@code DocumentCompiler.MAX_IMG_FILE_SIZE} (500KB), and a full-size capture of a
	 * maximised window blows past that. Doing it in Java keeps documentation capture free of
	 * platform image tools.
	 *
	 * @param scale multiplier, 1.0 for full size
	 * @param name  base file name without extension, or null for the default
	 */
	public static File screenshot(int windowIndex, File outDir, double scale, String name) throws Exception {
		if (GraphicsEnvironment.isHeadless()) {
			throw new IllegalStateException("cannot screenshot in a headless environment");
		}
		final BufferedImage img = onEdt(() -> {
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
			if (target == null || target.getWidth() <= 0 || target.getHeight() <= 0) {
				return null;
			}
			BufferedImage buf = new BufferedImage(target.getWidth(), target.getHeight(), BufferedImage.TYPE_INT_ARGB);
			java.awt.Graphics2D g = buf.createGraphics();
			try {
				target.printAll(g);
			} finally {
				g.dispose();
			}
			return buf;
		});
		if (img == null) {
			throw new IllegalStateException("no showing window to capture (index=" + windowIndex + ")");
		}
		BufferedImage written = img;
		if (scale > 0 && scale != 1.0) {
			int w = Math.max(1, (int) Math.round(img.getWidth() * scale));
			int h = Math.max(1, (int) Math.round(img.getHeight() * scale));
			BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			java.awt.Graphics2D g2 = scaled.createGraphics();
			try {
				g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
						java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.drawImage(img, 0, 0, w, h, null);
			} finally {
				g2.dispose();
			}
			written = scaled;
		}
		outDir.mkdirs();
		String base = (name != null && !name.isEmpty()) ? name
				: "vcell-window-" + (windowIndex < 0 ? "active" : windowIndex);
		File out = new File(outDir, base + ".png");
		ImageIO.write(written, "png", out);
		return out;
	}

	// ---------------------------------------------------------------------
	// EDT + JSON helpers
	// ---------------------------------------------------------------------

	// ---------------------------------------------------------------------
	// Selector emission and the real-input (Robot) driver.
	//
	// The endpoints above answer "act on the component I name". These two
	// sections answer the inverse questions a recorder and a demo replay ask:
	// "what selector names THIS component?" and "drive it the way a hand would,
	// with the cursor actually moving there".
	// ---------------------------------------------------------------------

	/**
	 * Find a row of a {@link JTree} or {@link JTable} by what it displays, searching the
	 * whole model rather than the capped serialization in {@code /tree}.
	 *
	 * <p>Two reasons this is not optional. The dump stops at {@link #MAX_TABLE_ROWS} /
	 * {@link #MAX_TREE_ROWS} rows and says so, so anything further down is unreachable from
	 * it - a file chooser in a 137-entry directory, a database tree of a thousand models.
	 * And a row INDEX is not a durable way to name a thing: the same model sits at a
	 * different row as soon as anything above it changes, so a script that has to survive
	 * needs to ask for a row by its text.
	 *
	 * @param exact  match the displayed text exactly, else case-insensitive substring
	 * @return JSON {@code {"row": N, "text": "..."}}, or {@code {"row": -1}} if not found
	 */
	public static String findRowJson(String path, final String query, final boolean exact) {
		return findRowJson(path, query, exact, null);
	}

	/**
	 * As above, but able to match on the row's application type rather than its label.
	 *
	 * <p>This is the only reliable way to find, say, the SpringSaLaD application: a model
	 * carries zero or more applications of any type in any order, the names are whatever
	 * the author chose ("Application2", "Copy of Application0"), and on screen only the
	 * icon distinguishes them. The type is a property of the model, so ask the model.
	 *
	 * @param appType a {@link SimulationContext.Application} name - {@code SPRINGSALAD},
	 *                {@code RULE_BASED_STOCHASTIC}, {@code NETWORK_DETERMINISTIC},
	 *                {@code NETWORK_STOCHASTIC} - or null to match on text instead
	 */
	public static String findRowJson(String path, final String query, final boolean exact,
			final String appType) {
		Component c = findByPath(path);
		if (c == null) {
			return "{\"error\":\"selector did not resolve\",\"row\":-1}";
		}
		String[] found = onEdt(() -> {
			int count;
			if (c instanceof JTree) {
				count = ((JTree) c).getRowCount();
			} else if (c instanceof JTable) {
				count = ((JTable) c).getRowCount();
			} else {
				return null;
			}
			for (int row = 0; row < count; row++) {
				if (appType != null) {
					if (!(c instanceof JTree)) {
						return null;
					}
					javax.swing.tree.TreePath tp = ((JTree) c).getPathForRow(row);
					if (tp != null && appType.equalsIgnoreCase(applicationTypeOf(tp))) {
						return new String[] { String.valueOf(row), rowText((JTree) c, row) };
					}
					continue;
				}
				String text;
				if (c instanceof JTree) {
					text = rowText((JTree) c, row);
				} else {
					JTable t = (JTable) c;
					Object cell = (t.getColumnCount() > 0) ? t.getValueAt(row, 0) : null;
					text = (cell == null) ? null : String.valueOf(cell);
				}
				if (text == null) {
					continue;
				}
				// A file chooser reports absolute paths; callers think in file names, so a
				// trailing path segment counts as a match too.
				String tail = text;
				int slash = Math.max(tail.lastIndexOf('/'), tail.lastIndexOf('\\'));
				if (slash >= 0 && slash < tail.length() - 1) {
					tail = tail.substring(slash + 1);
				}
				boolean hit = exact
						? (text.equals(query) || tail.equals(query))
						: (text.toLowerCase().contains(query.toLowerCase())
								|| tail.toLowerCase().contains(query.toLowerCase()));
				if (hit) {
					return new String[] { String.valueOf(row), text };
				}
			}
			return new String[] { "-1", null };
		});
		if (found == null) {
			return "{\"error\":\"not a JTree or JTable\",\"row\":-1}";
		}
		String text = found[1];
		return "{\"row\":" + found[0]
				+ (text == null ? "" : ",\"text\":\"" + escape(text) + "\"") + '}';
	}

	/** @return the screen centre of one tree row, table row or tab, or null if unresolvable. */
	private static Point rowCenterOnScreen(final Component c, final int row) {
		return onEdt(() -> {
			if (!c.isShowing()) {
				return null;
			}
			Rectangle r;
			if (c instanceof JTabbedPane) {
				JTabbedPane tp = (JTabbedPane) c;
				if (row < 0 || row >= tp.getTabCount()) {
					return null;
				}
				r = tp.getBoundsAt(row); // a tab is a "row" for aiming purposes
			} else if (c instanceof JTree) {
				r = ((JTree) c).getRowBounds(row);
			} else if (c instanceof JTable) {
				JTable t = (JTable) c;
				if (row < 0 || row >= t.getRowCount()) {
					return null;
				}
				r = t.getCellRect(row, 0, true);
			} else {
				return null;
			}
			if (r == null) {
				return null;
			}
			Point loc = c.getLocationOnScreen();
			return new Point(loc.x + r.x + Math.min(r.width / 2, 60), loc.y + r.y + r.height / 2);
		});
	}

	/** @return the centre of the component in screen coordinates, or null if it is not showing. */
	private static Point centerOnScreen(final Component c) {
		return onEdt(() -> {
			if (!c.isShowing()) {
				return null;
			}
			Point loc = c.getLocationOnScreen();
			Dimension d = c.getSize();
			return new Point(loc.x + d.width / 2, loc.y + d.height / 2);
		});
	}

	/**
	 * The node path of a live component — the inverse of {@link #findByPath(String)}'s
	 * third selector form, computed by walking up to the owning window.
	 *
	 * @return e.g. {@code "0/3/2"}, or null if the component is not inside a showing window
	 */
	static String pathOf(final Component c) {
		if (c == null) {
			return null;
		}
		return onEdt(() -> {
			LinkedList<Integer> indices = new LinkedList<>();
			Component cur = c;
			while (!(cur instanceof Window)) {
				Container parent = cur.getParent();
				if (parent == null) {
					return null; // detached from any window
				}
				Component[] kids = parent.getComponents();
				int found = -1;
				for (int i = 0; i < kids.length; i++) {
					if (kids[i] == cur) {
						found = i;
						break;
					}
				}
				if (found < 0) {
					return null;
				}
				indices.addFirst(found);
				cur = parent;
			}
			List<Window> windows = showingWindows();
			int windowIndex = -1;
			for (int i = 0; i < windows.size(); i++) {
				if (windows.get(i) == cur) {
					windowIndex = i;
					break;
				}
			}
			if (windowIndex < 0) {
				return null;
			}
			StringBuilder sb = new StringBuilder().append(windowIndex);
			for (int i : indices) {
				sb.append('/').append(i);
			}
			return sb.toString();
		});
	}

	/**
	 * The most durable selector that resolves to this component, for a recorder writing a
	 * script meant to survive into later sessions.
	 *
	 * <p>Preference order is deliberate and is NOT the same as "most specific":
	 * <ol>
	 * <li>{@code name=Foo} — survives layout changes, so it still works after the UI is
	 *     rearranged. An {@code [index]} is added only when a bare name would resolve to a
	 *     different component than this one.</li>
	 * <li>{@code 0/3/2} — structural, so it at least survives a restart.</li>
	 * <li>{@code c42} — last resort. Registry ids are stable only <i>within</i> a session, so
	 *     a recording that leans on one replays correctly today and resolves to nothing
	 *     tomorrow.</li>
	 * </ol>
	 *
	 * @return a selector accepted by {@link #findByPath(String)}, never null
	 */
	static String bestSelector(final Component c) {
		if (c == null) {
			return null;
		}
		String name = onEdt(c::getName);
		if (name != null && !name.isEmpty()) {
			final String targetName = name;
			String selector = onEdt(() -> {
				List<PathMatch> matches = collectAll(null, targetName, null, null);
				if (matches.size() <= 1) {
					return "name=" + targetName;
				}
				// does the unqualified form land on THIS component? (a showing match wins,
				// mirroring findByNameSelector, so the common case needs no index at all)
				Component unqualified = null;
				for (PathMatch pm : matches) {
					if (pm.component.isShowing()) {
						unqualified = pm.component;
						break;
					}
				}
				if (unqualified == null) {
					unqualified = matches.get(0).component;
				}
				if (unqualified == c) {
					return "name=" + targetName;
				}
				for (int i = 0; i < matches.size(); i++) {
					if (matches.get(i).component == c) {
						return "name=" + targetName + "[" + i + "]";
					}
				}
				return null; // named, but not reachable by that name — fall through
			});
			if (selector != null) {
				return selector;
			}
		}
		String path = pathOf(c);
		return path != null ? path : idFor(c);
	}

	/**
	 * Move the real cursor to the component, easing in and out the way a hand does.
	 *
	 * <p>Only useful for a watched replay: {@link #click(String)} fires buttons through
	 * {@code doClick()} and never moves the pointer, which is right for a test and wrong for
	 * a recording someone is going to film — the UI changes with no cursor anywhere near it.
	 *
	 * @return true if the component resolved and is on screen
	 */
	public static boolean glide(String path, int ms) {
		Component c = findByPath(path);
		if (c == null) {
			return false;
		}
		Point target = centerOnScreen(c);
		if (target == null) {
			return false;
		}
		try {
			Robot robot = new Robot();
			java.awt.PointerInfo pi = MouseInfo.getPointerInfo();
			Point from = (pi == null) ? target : pi.getLocation();
			int hops = Math.max(1, ms / 15);
			for (int i = 1; i <= hops; i++) {
				double t = (double) i / hops;
				double eased = t * t * (3 - 2 * t); // smoothstep: accelerate, then settle
				robot.mouseMove((int) Math.round(from.x + (target.x - from.x) * eased),
						(int) Math.round(from.y + (target.y - from.y) * eased));
				robot.delay(15);
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException("robot glide failed towards " + target, e);
		}
	}

	/**
	 * Click via real native press/release rather than {@code doClick()}.
	 *
	 * <p>Two things need this. A filmed replay needs the cursor to be where the click lands.
	 * And {@link UiRecorder} can only see input that reaches the AWT event queue — a
	 * {@code doClick()} calls its listeners directly and is invisible to it — so this is the
	 * only way to drive a button while recording.
	 *
	 * <p>Unlike {@link #click(String)} this blocks until the press is delivered, so a button
	 * that opens a modal dialog will hold the calling request until the dialog is up.
	 *
	 * @return true if the component resolved and is on screen
	 */
	public static boolean robotClick(String path, int glideMs) {
		return robotClick(path, glideMs, -1);
	}

	/**
	 * As above, but aimed at one row of a {@link JTree} or {@link JTable}, or one tab of a
	 * {@link JTabbedPane}.
	 *
	 * <p>Needed because {@code /selectTreeRow} and {@code /selectTableRow} act through the
	 * selection model, which posts no input event and so is invisible to
	 * {@link UiRecorder}. Tree navigation is how most of VCell is reached, so without this
	 * a scripted recording session could not capture the app's commonest interaction.
	 *
	 * @param row row to aim at, or -1 for the component's centre
	 */
	public static boolean robotClick(String path, int glideMs, final int row) {
		Component c = findByPath(path);
		if (c == null) {
			return false;
		}
		Point rowPoint = (row < 0) ? null : rowCenterOnScreen(c, row);
		if (row >= 0 && rowPoint == null) {
			return false;
		}
		if (rowPoint == null && glideMs > 0 && !glide(path, glideMs)) {
			return false;
		}
		Point target = (rowPoint != null) ? rowPoint : centerOnScreen(c);
		if (target == null) {
			return false;
		}
		try {
			Robot robot = new Robot();
			robot.mouseMove(target.x, target.y);
			robot.delay(40);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(40);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("robot click failed at " + target, e);
		}
	}

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

	/** Package-private so {@link UiRecorder} reuses it rather than carrying a third copy. */
	static String escape(String s) {
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
