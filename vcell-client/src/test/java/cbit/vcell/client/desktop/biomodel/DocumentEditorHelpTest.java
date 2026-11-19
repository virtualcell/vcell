package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.documentation.HelpBook;
import org.vcell.documentation.HelpIds;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;

/**
 * Help &gt; VCell Help in a model window opens the page about the view in front: every page it
 * can name exists, and the views a reader uses most open the page about them.
 */
@Tag("Fast")
public class DocumentEditorHelpTest {

	@Test
	public void everyViewOpensAPageThatExists() throws IOException {
		HelpBook book = HelpBook.fromDirectory(new File("UserDocumentation"));
		List<String> missing = new ArrayList<>();
		for (ActiveViewID id : ActiveViewID.values()) {
			String page = DocumentEditorHelp.pageFor(new ActiveView(null, null, id));
			if (page != null && book.findPage(page) == null) missing.add(id + " -> " + page);
		}
		for (DocumentEditorTreeFolderClass folder : DocumentEditorTreeFolderClass.values()) {
			String page = DocumentEditorHelp.pageFor(new ActiveView(null, folder, null));
			if (page != null && book.findPage(page) == null) missing.add(folder + " -> " + page);
		}
		assertEquals(List.of(), missing);
	}

	@Test
	public void viewsOpenThePageAboutThem() {
		assertEquals(HelpIds.REACTIONS, DocumentEditorHelp.pageFor(new ActiveView(null, DocumentEditorTreeFolderClass.REACTIONS_NODE, ActiveViewID.reactions)));
		assertEquals(HelpIds.REACTION_DIAGRAM, DocumentEditorHelp.pageFor(new ActiveView(null, null, ActiveViewID.reaction_diagram)));
		assertEquals(HelpIds.EVENTS, DocumentEditorHelp.pageFor(new ActiveView(null, DocumentEditorTreeFolderClass.PROTOCOLS_NODE, ActiveViewID.events)));
		assertEquals(HelpIds.NONSPATIAL_STRUCTURE_MAPPING, DocumentEditorHelp.pageFor(new ActiveView(null, null, ActiveViewID.structure_mapping)),
				"with no application, as a compartmental one");
		// a tab with no page of its own takes its tree node's
		assertEquals(HelpIds.SPECIFICATIONS, DocumentEditorHelp.pageFor(new ActiveView(null, DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, ActiveViewID.membrane_setting)));
		// the pathway views are Pathway Tools', which has its own help
		assertNull(DocumentEditorHelp.pageFor(new ActiveView(null, null, ActiveViewID.pathway_diagram)));
		assertNull(DocumentEditorHelp.pageFor(null));
	}

	@Test
	public void theTabsUnderTheViewOpenThePageAboutTheOpenTab() {
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Object Properties", new JPanel());
		tabs.addTab(DocumentEditor.TAB_TITLE_ANNOTATIONS, new JPanel());
		tabs.addTab(" " + DocumentEditor.TAB_TITLE_PROBLEMS + " (0 Errors, 0 Warnings) ", new JPanel()); // as the editor titles it
		assertEquals(HelpIds.PROPERTIES_PANES, DocumentEditorHelp.pageForBottomTab(tabs, null), "nothing selected: the panes' overview");
		tabs.setSelectedIndex(1);
		assertEquals(HelpIds.ANNOTATIONS, DocumentEditorHelp.pageForBottomTab(tabs, null));
		tabs.setSelectedIndex(2);
		assertEquals(HelpIds.PROBLEMS, DocumentEditorHelp.pageForBottomTab(tabs, null));
		assertNotNull(DocumentEditorHelp.pageForBottomTab(new JTabbedPane(), null));
	}
}
