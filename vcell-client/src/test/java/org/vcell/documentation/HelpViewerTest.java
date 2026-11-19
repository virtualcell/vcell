package org.vcell.documentation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The help window with no page open: the page area shows the table of contents, reached from the
 * Home button, by deselecting the open entry or Escape in the contents tree, and by Back and
 * Forward; its links open pages. Drives the viewer through its own controls, on the event thread.
 */
@Tag("Fast")
public class HelpViewerTest {
	private static final String CONTENTS = "Help contents";
	private static HelpBook book;

	@BeforeAll
	public static void loadBook() throws IOException {
		HelpViewerTest.book = HelpBook.fromDirectory(new File("UserDocumentation"));
	}

	@Test
	public void showsTheContentsWhenNoPageIsOpen() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			HelpViewer viewer = new HelpViewer(HelpViewerTest.book);
			JTree tree = HelpViewerTest.find(viewer, "HelpContents", JTree.class);
			String introductionTitle = HelpViewerTest.book.findPage(HelpIds.INTRODUCTION).title();
			// Help > VCell Help opens here
			viewer.showTableOfContents();
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));
			assertNull(tree.getSelectionPath());
			viewer.showPage(HelpIds.INTRODUCTION);
			assertEquals(introductionTitle, HelpViewerTest.shown(viewer));
			assertNotNull(tree.getSelectionPath(), "the open page is selected in the contents");

			HelpViewerTest.find(viewer, "HelpHome", AbstractButton.class).doClick();
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));
			assertNull(tree.getSelectionPath(), "no page is open, so none is selected");

			// a link on the contents page, as the page pane reports a click on it
			HelpPage target = HelpViewerTest.book.findPage("GeneralNavAndWinSetup");
			JEditorPane page = HelpViewerTest.page(viewer);
			for (HyperlinkListener listener : page.getHyperlinkListeners()) {
				listener.hyperlinkUpdate(new HyperlinkEvent(page, HyperlinkEvent.EventType.ACTIVATED, null, target.path()));
			}
			assertEquals(target.title(), HelpViewerTest.shown(viewer));
			assertNotNull(tree.getSelectionPath());

			HelpViewerTest.find(viewer, "HelpBack", AbstractButton.class).doClick();
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer), "the contents are a place in the history");
			assertNull(tree.getSelectionPath());
			HelpViewerTest.find(viewer, "HelpForward", AbstractButton.class).doClick();
			assertEquals(target.title(), HelpViewerTest.shown(viewer));

			// deselecting the open entry (Cmd/Ctrl-click) leaves no page open
			tree.clearSelection();
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));

			// so does Escape in the tree
			viewer.showPage(HelpIds.INTRODUCTION);
			tree.getActionMap().get("showContents").actionPerformed(new ActionEvent(tree, ActionEvent.ACTION_PERFORMED, "escape"));
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));

			// choosing an entry opens its page; a click in the empty space below the entries
			// deselects it again (Swing's own tree handling of a press asks the toolkit for the
			// menu shortcut key, which a headless run refuses, so the press goes to the viewer's
			// listener alone)
			tree.setSize(300, 2000);
			tree.setSelectionRow(0);
			assertNotNull(tree.getSelectionPath());
			assertNotEquals(CONTENTS, HelpViewerTest.shown(viewer), "the entry's page is open");
			HelpViewerTest.pressForViewer(tree, 20, 1900);
			assertNull(tree.getSelectionPath());
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));
		});
	}

	@Test
	public void menuBarOffersTheViewersCommands() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			HelpViewer viewer = new HelpViewer(HelpViewerTest.book);
			JMenuBar menuBar = viewer.createMenuBar(viewer);
			List<String> menus = new ArrayList<>();
			for (int index = 0; index < menuBar.getMenuCount(); index++) menus.add(menuBar.getMenu(index).getText());
			assertEquals(List.of("File", "Go", "View", "Window"), menus);

			viewer.showPage(HelpIds.INTRODUCTION);
			JMenuItem back = HelpViewerTest.item(menuBar, "Go", "Back");
			JMenuItem home = HelpViewerTest.item(menuBar, "Go", "Home");
			assertFalse(back.isEnabled(), "nothing to go back to yet");
			assertNotNull(home.getAccelerator(), "the menu shows Home's shortcut");
			home.doClick();
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));
			assertTrue(back.isEnabled(), "the menu item follows the toolbar's Back");
			back.doClick();
			assertNotEquals(CONTENTS, HelpViewerTest.shown(viewer));

			assertFalse(HelpViewerTest.item(menuBar, "View", "Problems").isEnabled(), "problems only for a source folder");
			assertFalse(HelpViewerTest.item(menuBar, "View", "Use the Built-in Help").isEnabled(), "already the built-in help");

			JTree tree = HelpViewerTest.find(viewer, "HelpContents", JTree.class);
			int collapsedRows = tree.getRowCount();
			HelpViewerTest.item(menuBar, "View", "Expand All").doClick();
			assertTrue(tree.getRowCount() > collapsedRows, "every entry is showing");
			HelpViewerTest.item(menuBar, "View", "Collapse All").doClick();
			assertEquals(HelpViewerTest.book.getTableOfContents().size(), tree.getRowCount(), "only the top entries");
		});
	}

	@Test
	public void problemsFollowTheOpenPageAndTheFilters(@TempDir Path helpRoot) throws Exception {
		Files.writeString(helpRoot.resolve("toc.md"), "# Help\n\n- [Broken](pages/Broken.md)\n- [Clean](pages/Clean.md)\n", StandardCharsets.UTF_8);
		Files.createDirectories(helpRoot.resolve("pages"));
		Files.createDirectories(helpRoot.resolve("images"));
		Files.writeString(helpRoot.resolve("pages/Broken.md"), "# Broken\n\nSee [nowhere](Nowhere.md).\n\n![](../images/Large.png)\n", StandardCharsets.UTF_8);
		Files.writeString(helpRoot.resolve("pages/Clean.md"), "# Clean\n", StandardCharsets.UTF_8);
		HelpBookTest.writePng(helpRoot.resolve("images/Large.png"), 600, 600);
		HelpBookTest.writePng(helpRoot.resolve("images/Unused.png"), 20, 20);
		// all of them: this small help also lacks the pages VCell opens by name (errors in no page)
		List<HelpProblem> all = HelpChecks.check(HelpBook.fromDirectory(helpRoot.toFile()));
		long allErrors = HelpChecks.errors(all).size();
		assertEquals(2, all.size() - allErrors, "the large and the unused image");

		Preferences preferences = Preferences.userNodeForPackage(HelpViewer.class);
		String savedFolder = preferences.get("sourceFolder", null); // loading a folder remembers it
		HelpViewer[] viewer = new HelpViewer[1];
		try {
			SwingUtilities.invokeAndWait(() -> {
				viewer[0] = new HelpViewer(HelpViewerTest.book);
				viewer[0].loadSourceFolder(helpRoot.toFile(), true);
			});
			JTabbedPane tabs = HelpViewerTest.find(viewer[0], "HelpNavigation", JTabbedPane.class);
			for (int waited = 0; waited < 200 && HelpViewerTest.onEdt(() -> tabs.getTabCount()) < 2; waited++) Thread.sleep(50);

			SwingUtilities.invokeAndWait(() -> {
				JList<?> list = HelpViewerTest.find(viewer[0], "HelpProblemList", JList.class);
				JCheckBox errors = HelpViewerTest.find(viewer[0], "HelpShowErrors", JCheckBox.class);
				JCheckBox warnings = HelpViewerTest.find(viewer[0], "HelpShowWarnings", JCheckBox.class);
				assertEquals(2, tabs.getTabCount(), "the folder loaded");

				viewer[0].showTableOfContents();
				assertEquals("Problems (" + allErrors + "/2)", tabs.getTitleAt(1), "errors/warnings, all pages");
				assertEquals(all.size(), list.getModel().getSize());

				viewer[0].showPage("Broken");
				assertEquals("Problems (1/1)", tabs.getTitleAt(1), "only this page's: the broken link and the large image");
				assertEquals(2, list.getModel().getSize());
				errors.doClick();
				assertEquals(1, list.getModel().getSize(), "errors filtered out");
				assertTrue(((HelpProblem) list.getModel().getElementAt(0)).isWarning());
				assertEquals("Problems (1/1)", tabs.getTitleAt(1), "a hidden error still counts");
				warnings.doClick();
				assertEquals(0, list.getModel().getSize());
				errors.doClick();
				warnings.doClick();

				viewer[0].showPage("Clean");
				assertEquals("Problems (0/0)", tabs.getTitleAt(1));
				assertEquals(0, list.getModel().getSize());

				// choosing a problem shows it in a tab of its own; the next one reuses that tab
				JTabbedPane pages = HelpViewerTest.find(viewer[0], "HelpPages", JTabbedPane.class);
				viewer[0].showPage("Broken");
				int pageTabs = HelpViewerTest.pageTabCount(pages);
				list.setSelectedIndex(0); // errors come first: the broken link
				assertEquals(pageTabs + 1, HelpViewerTest.pageTabCount(pages), "a problem tab");
				assertEquals("Broken link", pages.getTitleAt(pages.getSelectedIndex()));
				assertEquals(2, list.getModel().getSize(), "the list keeps the page's problems");
				list.setSelectedIndex(1);
				assertEquals(pageTabs + 1, HelpViewerTest.pageTabCount(pages), "the problem tab is reused");
				assertEquals("Large image file", pages.getTitleAt(pages.getSelectedIndex()));

				// Go to opens the problem's page in that tab, and Back returns to the problem
				HelpViewerTest.find((Container) pages.getSelectedComponent(), "HelpGoToProblem", AbstractButton.class).doClick();
				assertEquals("Broken", HelpViewerTest.shown(viewer[0]));
				assertEquals("Broken", pages.getTitleAt(pages.getSelectedIndex()));
				HelpViewerTest.find(viewer[0], "HelpBack", AbstractButton.class).doClick();
				assertEquals("Large image file", pages.getTitleAt(pages.getSelectedIndex()));

				// a problem on no page (the unused image) can't be gone to
				viewer[0].showTableOfContents();
				for (int index = 0; index < list.getModel().getSize(); index++) {
					if (((HelpProblem) list.getModel().getElementAt(index)).kind() == HelpProblem.Kind.UNUSED_IMAGE) list.setSelectedIndex(index);
				}
				assertFalse(HelpViewerTest.find((Container) pages.getSelectedComponent(), "HelpGoToProblem", AbstractButton.class).isEnabled());
			});
		} finally {
			if (savedFolder == null) preferences.remove("sourceFolder");
			else preferences.put("sourceFolder", savedFolder);
		}
	}

	@Test
	public void tabsOpenCloseAndKeepTheirOwnHistory() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			HelpViewer viewer = new HelpViewer(HelpViewerTest.book);
			JTabbedPane pages = HelpViewerTest.find(viewer, "HelpPages", JTabbedPane.class);
			JMenuBar menuBar = viewer.createMenuBar(viewer);
			HelpPage introduction = HelpViewerTest.book.findPage(HelpIds.INTRODUCTION);

			// opening the help from VCell: a tab per place, reused when the place is already open
			viewer.showInTab(HelpTab.Location.contents());
			viewer.showInTab(HelpTab.Location.page(introduction, null));
			assertEquals(2, HelpViewerTest.pageTabCount(pages));
			assertEquals(introduction.title(), HelpViewerTest.shown(viewer));
			viewer.showInTab(HelpTab.Location.contents());
			assertEquals(2, HelpViewerTest.pageTabCount(pages), "the contents' tab, again");
			assertEquals(0, pages.getSelectedIndex());

			// File > New Tab opens Home, with a history of its own
			HelpViewerTest.item(menuBar, "File", "New Tab").doClick();
			assertEquals(3, HelpViewerTest.pageTabCount(pages));
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));
			assertFalse(HelpViewerTest.item(menuBar, "Go", "Back").isEnabled());

			// Cmd/Ctrl-click on a link opens it in a new tab behind this one
			HelpPage target = HelpViewerTest.book.findPage("GeneralNavAndWinSetup");
			JEditorPane page = HelpViewerTest.page(viewer);
			MouseEvent click = new MouseEvent(page, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), HelpViewer.menuMask(), 5, 5, 1, false);
			for (HyperlinkListener listener : page.getHyperlinkListeners()) {
				listener.hyperlinkUpdate(new HyperlinkEvent(page, HyperlinkEvent.EventType.ACTIVATED, null, target.path(), null, click));
			}
			assertEquals(4, HelpViewerTest.pageTabCount(pages));
			assertEquals(2, pages.getSelectedIndex(), "the new tab opens behind");
			assertEquals(CONTENTS, HelpViewerTest.shown(viewer));
			assertEquals(target.title(), pages.getTitleAt(3));

			// the "+" tab, last, can't be selected; its button opens a new tab before it
			int last = pages.getTabCount() - 1;
			assertFalse(pages.isEnabledAt(last));
			HelpViewerTest.find((Container) pages.getTabComponentAt(last).getParent(), "HelpNewTab", AbstractButton.class).doClick();
			assertEquals(5, HelpViewerTest.pageTabCount(pages));
			assertEquals(4, pages.getSelectedIndex(), "the new tab, just before the plus");
			HelpViewerTest.item(menuBar, "File", "Close Tab").doClick();
			assertEquals(4, HelpViewerTest.pageTabCount(pages));
			assertTrue(pages.getSelectedComponent() instanceof HelpTab, "closing the tab next to the plus selects a page, not the plus");
			pages.setSelectedIndex(2);

			// Go > Show Next Tab, then File > Close Tab
			HelpViewerTest.item(menuBar, "Go", "Show Next Tab").doClick();
			assertEquals(target.title(), HelpViewerTest.shown(viewer));
			HelpViewerTest.item(menuBar, "File", "Close Tab").doClick();
			assertEquals(3, HelpViewerTest.pageTabCount(pages));

			// each tab's Back is its own: the Introduction tab has nowhere to go back to
			pages.setSelectedIndex(1);
			assertEquals(introduction.title(), HelpViewerTest.shown(viewer));
			assertFalse(HelpViewerTest.item(menuBar, "Go", "Back").isEnabled());
		});
	}

	private static <T> T onEdt(java.util.function.Supplier<T> supplier) throws Exception {
		Object[] result = new Object[1];
		SwingUtilities.invokeAndWait(() -> result[0] = supplier.get());
		@SuppressWarnings("unchecked") T value = (T) result[0];
		return value;
	}

	private static JMenuItem item(JMenuBar menuBar, String menuText, String itemText) {
		for (int index = 0; index < menuBar.getMenuCount(); index++) {
			JMenu menu = menuBar.getMenu(index);
			if (!menuText.equals(menu.getText())) continue;
			for (int itemIndex = 0; itemIndex < menu.getItemCount(); itemIndex++) {
				JMenuItem item = menu.getItem(itemIndex);
				if (item != null && itemText.equals(item.getText())) return item;
			}
		}
		throw new AssertionError("no " + menuText + " > " + itemText);
	}

	/**
	 * A left-button press, given only to the viewer's own mouse listeners: Swing's look-and-feel
	 * listeners need a display (they ask the toolkit for the menu shortcut key).
	 */
	private static void pressForViewer(Component component, int x, int y) {
		MouseEvent press = new MouseEvent(component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
				InputEvent.BUTTON1_DOWN_MASK, x, y, 1, false, MouseEvent.BUTTON1);
		for (java.awt.event.MouseListener listener : component.getMouseListeners()) {
			if (listener.getClass().getName().startsWith("javax.swing.")) continue;
			listener.mousePressed(press);
		}
	}

	/** The open pages: every tab but the last, "+", which opens a new one. */
	private static int pageTabCount(JTabbedPane pages) {
		int count = 0;
		for (int index = 0; index < pages.getTabCount(); index++) {
			if (pages.getComponentAt(index) instanceof HelpTab) count++;
		}
		return count;
	}

	/** The selected tab's page pane. */
	private static JEditorPane page(HelpViewer viewer) {
		JTabbedPane pages = HelpViewerTest.find(viewer, "HelpPages", JTabbedPane.class);
		return HelpViewerTest.find((Container) pages.getSelectedComponent(), "HelpPage", JEditorPane.class);
	}

	/** What the selected tab shows: a page's title, or "Help contents". */
	private static String shown(HelpViewer viewer) {
		return HelpViewerTest.page(viewer).getAccessibleContext().getAccessibleDescription();
	}

	private static <T extends Component> T find(Container container, String name, Class<T> type) {
		for (Component child : container.getComponents()) {
			if (type.isInstance(child) && name.equals(child.getName())) return type.cast(child);
			if (!(child instanceof Container inner)) continue;
			T found = HelpViewerTest.find(inner, name, type);
			if (found != null) return found;
		}
		return null;
	}
}
