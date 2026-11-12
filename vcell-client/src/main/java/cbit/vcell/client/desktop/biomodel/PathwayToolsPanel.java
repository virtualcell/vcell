package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.vcell.pathway.BioPaxObject;

import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;

/**
 * The Pathway Tools window's content: the pathway's tabs (its diagram, objects, BioPAX summary and tree, and Pathway
 * Lookup) above, and beneath them what's chosen there: a pathway object's properties, or a Lookup result's preview,
 * from which it's imported. It used to fill the model window; it follows the same selection, so links from the model
 * (a species' or reaction's pathway links) still lead into it.
 */
@SuppressWarnings("serial")
public class PathwayToolsPanel extends DocumentEditorSubPanel {
	/** The folder classes that are the pathway's views: asking for one opens this window. */
	public static boolean isPathwayView(DocumentEditorTreeFolderClass folderClass) {
		if (folderClass == null) return false;
		return switch (folderClass) {
			case PATHWAY_NODE, PATHWAY_DIAGRAM_NODE, PATHWAY_OBJECTS_NODE, BIOPAX_SUMMARY_NODE, BIOPAX_TREE_NODE, PATHWAY_LOOKUP_NODE -> true;
			default -> false;
		};
	}

	static final String OBJECT_PROPERTIES = "Object Properties";
	static final String PATHWAY_PREVIEW = "Pathway Preview";

	private final JTabbedPane lowerTabs = new JTabbedPane();

	public PathwayToolsPanel(BioModelEditorPathwayDiagramPanel pathwayPanel, BioPaxObjectPropertiesPanel objectProperties, BioModelEditorPathwayPanel pathwayPreview) {
		super();
		this.setLayout(new BorderLayout());
		this.setName("PathwayToolsPanel");
		this.lowerTabs.setName("PathwayToolsLowerTabs");
		this.lowerTabs.addTab(PathwayToolsPanel.OBJECT_PROPERTIES, objectProperties);
		this.lowerTabs.addTab(PathwayToolsPanel.PATHWAY_PREVIEW, pathwayPreview);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pathwayPanel, this.lowerTabs);
		split.setName("PathwayToolsSplit");
		split.setResizeWeight(0.65);
		split.setContinuousLayout(true);
		this.add(split, BorderLayout.CENTER);
	}

	JTabbedPane getLowerTabs() {
		return this.lowerTabs;
	}

	// beneath, whichever shows what was just chosen: a pathway object, or a Lookup result
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) return;
		if (selectedObjects[0] instanceof PathwayData) {
			this.lowerTabs.setSelectedIndex(this.lowerTabs.indexOfTab(PathwayToolsPanel.PATHWAY_PREVIEW));
		} else if (selectedObjects[0] instanceof BioPaxObject) {
			this.lowerTabs.setSelectedIndex(this.lowerTabs.indexOfTab(PathwayToolsPanel.OBJECT_PROPERTIES));
		}
	}
}
