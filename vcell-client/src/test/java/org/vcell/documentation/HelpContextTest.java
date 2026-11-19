package org.vcell.documentation;

import org.junit.jupiter.api.Assertions;

import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Contextual help: which page explains what fills a window, and what a part of it explains.
 */
@Tag("Fast")
public class HelpContextTest {

	@Test
	public void theOutermostPanelWithAPageExplainsTheWindow() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JPanel window = new JPanel(new BorderLayout());
			JPanel editor = new JPanel(new BorderLayout());
			JPanel part = new JPanel();
			JLabel deep = new JLabel("a field");
			part.add(deep);
			editor.add(part, BorderLayout.SOUTH);
			window.add(editor, BorderLayout.CENTER);

			Assertions.assertNull(HelpContext.pageWithin(window), "nothing says which page explains it");
			Assertions.assertFalse(HelpContext.hasPageWithin(window));
			Assertions.assertNull(HelpContext.windowHelpButton(window), "no \"?\" where no page explains anything");

			HelpContext.setPage(part, HelpIds.PROBLEMS);
			Assertions.assertEquals(HelpIds.PROBLEMS, HelpContext.pageWithin(window), "a part's page, when nothing around it has one");
			HelpContext.setPage(editor, HelpIds.REACTIONS);
			Assertions.assertEquals(HelpIds.REACTIONS, HelpContext.pageWithin(window), "the panel that fills the window wins over its parts");
			Assertions.assertEquals(HelpIds.PROBLEMS, HelpContext.pageAround(deep), "a control's page is that of the nearest panel around it");

			// a panel whose page depends on what it shows can answer none; the panels inside it are asked then
			AtomicReference<String> shown = new AtomicReference<>(null);
			HelpContext.setPage(editor, shown::get);
			Assertions.assertTrue(HelpContext.hasPage(editor));
			Assertions.assertEquals(HelpIds.PROBLEMS, HelpContext.pageWithin(window));
			shown.set(HelpIds.SPECIES);
			Assertions.assertEquals(HelpIds.SPECIES, HelpContext.pageWithin(window), "asked again each time, so it follows what is shown");

			// hidden panels explain nothing
			editor.setVisible(false);
			Assertions.assertNull(HelpContext.pageWithin(window));
			editor.setVisible(true);

			HelpButton windowHelp = HelpContext.windowHelpButton(window);
			Assertions.assertNotNull(windowHelp, "a \"?\" where a page explains what the window shows");
			Assertions.assertEquals("WindowHelp", windowHelp.getName());
			Assertions.assertEquals(HelpIds.SPECIES, windowHelp.page());

			HelpContext.setPage(editor, (String) null);
			Assertions.assertFalse(HelpContext.hasPage(editor), "a null page takes it away");
		});
	}

	@Test
	public void helpButtonsSayWhatTheyExplainAndFollowWhatIsShown() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			HelpButton fixed = HelpButton.forPage("ReactionDiagramHelp", "the reaction diagram", HelpIds.REACTION_DIAGRAM);
			Assertions.assertEquals("Help on the reaction diagram", fixed.getAccessibleContext().getAccessibleName());
			Assertions.assertEquals("Open VCell Help on the reaction diagram", fixed.getToolTipText());
			Assertions.assertEquals(HelpIds.REACTION_DIAGRAM, fixed.page());
			Assertions.assertTrue(fixed.isFocusable(), "reachable from the keyboard");

			fixed.setExplanation("<html>Drag to pan.</html>");
			Assertions.assertEquals("<html>Drag to pan.</html>", fixed.getToolTipText(), "a short explanation is its tooltip");

			JTabbedPane tabs = new JTabbedPane();
			tabs.addTab("Object Properties", new JPanel());
			tabs.addTab("Problems", new JPanel());
			HelpButton dynamic = HelpButton.forPage("TabsHelp", "the open tab",
					() -> tabs.getSelectedIndex() == 1 ? HelpIds.PROBLEMS : HelpIds.PROPERTIES_PANES);
			Assertions.assertEquals(HelpIds.PROPERTIES_PANES, dynamic.page());
			tabs.setSelectedIndex(1);
			Assertions.assertEquals(HelpIds.PROBLEMS, dynamic.page(), "the page of the tab open when clicked");

			JComponent withHelp = dynamic.besideTabs(tabs);
			withHelp.setSize(600, 300);
			withHelp.doLayout();
			Assertions.assertSame(withHelp, tabs.getParent(), "the tabs fill it");
			Assertions.assertSame(withHelp, dynamic.getParent(), "the \"?\" sits over them");
			Assertions.assertEquals(600, tabs.getWidth());
			Assertions.assertEquals(600 - 4, dynamic.getX() + dynamic.getWidth(), "at the right end of the tab row");
			Assertions.assertTrue(dynamic.getY() < tabs.getBoundsAt(0).y + tabs.getBoundsAt(0).height, "level with the tabs");
		});
	}

	@Test
	public void theHelpMenuItemUsesF1() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			JMenuItem item = HelpContext.createHelpMenuItem();
			Assertions.assertEquals("VCell Help", item.getText());
			Assertions.assertEquals(HelpContext.HELP_KEY, item.getAccelerator());
			Assertions.assertEquals("VCellHelpMenuItem", item.getName());
			JButton unrelated = new JButton();
			Assertions.assertNull(HelpContext.pageAround(unrelated));
		});
	}
}
