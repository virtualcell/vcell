package org.vcell.documentation;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;

/**
 * Which help page explains what the reader is looking at: contextual help.
 *
 * <p>A panel says which page explains it with {@link #setPage(JComponent, String)}, or, when that
 * depends on what it shows (a tab, the selected object), {@link #setPage(JComponent, Supplier)}.
 * Pages are {@link HelpIds} constants, so a renamed page fails HelpBookTest instead of a button.
 *
 * <p>Help &gt; VCell Help ({@link #createHelpMenuItem()}) and F1 open the page for the window in
 * front: the page of the outermost showing panel in it that has one, so the panel that fills the
 * window, not whatever control has focus. A window with no page of its own (a small child window)
 * takes its owner's; with none at all, the help opens on its contents. A part of a window that has
 * a page of its own gets a {@link HelpButton}.
 */
public final class HelpContext {
	private static final String PAGE_PROPERTY = "vcell.helpPage";

	/** F1 opens the help for the window in front, as Help &gt; VCell Help does. */
	public static final KeyStroke HELP_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);

	private static boolean shortcutInstalled = false;

	private HelpContext() {
	}

	/** The page that explains this panel, a {@link HelpIds} constant; null removes it. */
	public static void setPage(JComponent component, String pageId) {
		HelpContext.setPage(component, pageId == null ? null : () -> pageId);
	}

	/**
	 * The page that explains this panel, asked for when help opens; the supplier may answer null
	 * when no page explains what the panel shows now, and the panels around it are asked instead.
	 */
	public static void setPage(JComponent component, Supplier<String> pageId) {
		component.putClientProperty(HelpContext.PAGE_PROPERTY, pageId);
	}

	/** Whether this panel says which page explains it (whatever that is now). */
	public static boolean hasPage(JComponent component) {
		return component.getClientProperty(HelpContext.PAGE_PROPERTY) instanceof Supplier<?>;
	}

	/** The page this component says explains it now; null when it says none. */
	public static String pageOf(Component component) {
		if (!(component instanceof JComponent jComponent)) return null;
		if (!(jComponent.getClientProperty(HelpContext.PAGE_PROPERTY) instanceof Supplier<?> supplier)) return null;
		Object pageId = supplier.get();
		return pageId instanceof String id ? id : null;
	}

	/**
	 * The page for a component: its own, else the nearest one of the panels around it; null when
	 * none of them has one.
	 */
	public static String pageAround(Component component) {
		for (Component c = component; c != null && !(c instanceof Window); c = c.getParent()) {
			String pageId = HelpContext.pageOf(c);
			if (pageId != null) return pageId;
		}
		return null;
	}

	/**
	 * The page for what fills this container: that of the outermost showing component in it that
	 * has a page now (the panel nearest the window's top wins over the parts inside it); null when
	 * none has.
	 */
	public static String pageWithin(Container root) {
		Deque<Component> queue = new ArrayDeque<>();
		queue.add(root);
		while (!queue.isEmpty()) {
			Component component = queue.removeFirst();
			if (!component.isVisible()) continue;
			String pageId = HelpContext.pageOf(component);
			if (pageId != null) return pageId;
			if (!(component instanceof Container container)) continue;
			for (Component child : container.getComponents()) queue.addLast(child);
		}
		return null;
	}

	/** Whether anything in this container says which page explains it. */
	public static boolean hasPageWithin(Container root) {
		Deque<Component> queue = new ArrayDeque<>();
		queue.add(root);
		while (!queue.isEmpty()) {
			Component component = queue.removeFirst();
			if (component instanceof JComponent jComponent && HelpContext.hasPage(jComponent)) return true;
			if (!(component instanceof Container container)) continue;
			for (Component child : container.getComponents()) queue.addLast(child);
		}
		return false;
	}

	/**
	 * The page for a window: that of what fills it, else its owner's (a small child window is part
	 * of its owner's work); null when none of them has one.
	 */
	public static String pageFor(Window window) {
		for (Window w = window; w != null; w = w.getOwner()) {
			Container root = w instanceof RootPaneContainer container ? container.getContentPane() : w;
			String pageId = HelpContext.pageWithin(root);
			if (pageId != null) return pageId;
		}
		return null;
	}

	/**
	 * A "?" for the small menu bar inside a window with no menu bar of its own (a child window, a
	 * dialog), that opens the page for what fills it; null when nothing in it has a page.
	 *
	 * @param content what the window shows
	 */
	public static HelpButton windowHelpButton(Container content) {
		if (!HelpContext.hasPageWithin(content)) return null;
		HelpButton button = HelpButton.forPage("WindowHelp", "this window", () -> HelpContext.pageWithin(content));
		button.setLeadingGap(4);
		return button;
	}

	/** Open the help at the page for this window, or at its contents when it has none. */
	public static void showHelpFor(Window window) {
		String pageId = window == null ? null : HelpContext.pageFor(window);
		if (pageId == null) {
			HelpViewer.showHelp();
		} else {
			HelpViewer.showHelp(pageId);
		}
	}

	/** Open the help at the page for the window in front (the one the reader is working in). */
	public static void showHelpForActiveWindow() {
		HelpContext.showHelpFor(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow());
	}

	/**
	 * The window a menu item works on: the window in front, when that is its menu bar's window or
	 * one of that window's child windows (which have no menu bar and use their owner's); else its
	 * menu bar's window.
	 */
	static Window windowFor(JMenuItem item) {
		Window menuWindow = null;
		for (Component c = item; c != null; ) {
			if (c instanceof Window window) {
				menuWindow = window;
				break;
			}
			c = c instanceof JPopupMenu popup ? popup.getInvoker() : c.getParent();
		}
		Window active = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		if (menuWindow == null) return active;
		for (Window w = active; w != null; w = w.getOwner()) {
			if (w == menuWindow) return active;
		}
		return menuWindow;
	}

	/**
	 * Help &gt; VCell Help: opens the help at the page for the window in front (F1), and makes F1
	 * work in every VCell window, those with no menu bar too.
	 */
	public static JMenuItem createHelpMenuItem() {
		HelpContext.installShortcut();
		JMenuItem item = new JMenuItem("VCell Help");
		item.setName("VCellHelpMenuItem");
		item.setAccelerator(HelpContext.HELP_KEY);
		item.getAccessibleContext().setAccessibleDescription("Opens VCell's help at the page about the window in front");
		item.addActionListener(e -> HelpContext.showHelpFor(HelpContext.windowFor(item)));
		return item;
	}

	/** A Help menu holding only VCell Help, for a window whose other menus have no help items. */
	public static JMenu createHelpMenu() {
		JMenu help = new JMenu("Help");
		help.setName("HelpMenu");
		help.add(HelpContext.createHelpMenuItem());
		return help;
	}

	/**
	 * F1 in any window opens the help for that window. Child windows and dialogs have no menu bar
	 * for an accelerator to live in, so the key is caught before any window sees it. A menu that
	 * shows F1 as its accelerator may fire too; opening the same page twice shows the one tab.
	 */
	public static synchronized void installShortcut() {
		if (HelpContext.shortcutInstalled) return;
		HelpContext.shortcutInstalled = true;
		KeyEventDispatcher dispatcher = event -> {
			if (event.getID() != KeyEvent.KEY_PRESSED) return false;
			if (event.getKeyCode() != KeyEvent.VK_F1 || event.getModifiersEx() != 0) return false;
			Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
			if (window == null || HelpViewer.isHelpWindow(window)) return false; // the help needs no help
			HelpContext.showHelpFor(window);
			event.consume();
			return true;
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
	}
}
