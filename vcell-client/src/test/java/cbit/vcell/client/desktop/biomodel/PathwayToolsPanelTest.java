package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;

@Tag("Fast")
public class PathwayToolsPanelTest {

	@Test
	public void everyPathwayViewOpensTheWindow() {
		for (DocumentEditorTreeFolderClass view : new DocumentEditorTreeFolderClass[] {
				DocumentEditorTreeFolderClass.PATHWAY_NODE, DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE,
				DocumentEditorTreeFolderClass.PATHWAY_OBJECTS_NODE, DocumentEditorTreeFolderClass.BIOPAX_SUMMARY_NODE,
				DocumentEditorTreeFolderClass.BIOPAX_TREE_NODE, DocumentEditorTreeFolderClass.PATHWAY_LOOKUP_NODE }) {
			assertTrue(PathwayToolsPanel.isPathwayView(view), view.name());
		}
	}

	@Test
	public void theModelsOwnViewsStayInItsWindow() {
		for (DocumentEditorTreeFolderClass view : new DocumentEditorTreeFolderClass[] {
				DocumentEditorTreeFolderClass.MODEL_NODE, DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE,
				DocumentEditorTreeFolderClass.SPECIES_NODE, DocumentEditorTreeFolderClass.BIOCHEMISTRY_NODE,
				DocumentEditorTreeFolderClass.APPLICATIONS_NODE }) {
			assertFalse(PathwayToolsPanel.isPathwayView(view), view.name());
		}
		assertFalse(PathwayToolsPanel.isPathwayView(null));
	}
}
