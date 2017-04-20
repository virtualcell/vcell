/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.mathmodel;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.client.desktop.biomodel.TabCloseIcon;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.geometry.gui.CSGObjectPropertiesPanel;
import cbit.vcell.geometry.gui.GeometryViewer;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.ode.gui.SimulationSummaryPanel;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class MathModelEditor extends DocumentEditor {
	private MathModelWindowManager mathModelWindowManager = null;
	private MathModel mathModel = new MathModel(null);
	
	private SimulationListPanel simulationListPanel = null;
	private GeometryViewer geometryViewer = null;
	private OutputFunctionsPanel outputFunctionsPanel = null;
//	private EquationViewerPanel equationViewerPanel;
	private VCMLEditorPanel vcmlEditorPanel;
	
	private MathModelEditorTreeCellRenderer mathModelEditorTreeCellRenderer = null;
	private MathModelEditorTreeModel mathModelEditorTreeModel = null;
	
	private SimulationSummaryPanel simulationSummaryPanel = null;
	private MathModelPropertiesPanel mathModelPropertiesPanel = new MathModelPropertiesPanel();
	private MathModelEditorAnnotationPanel mathModelEditorAnnotationPanel = new MathModelEditorAnnotationPanel();
	
	private CSGObjectPropertiesPanel csgObjectPropertiesPanel;

/**
 * BioModelEditor constructor comment.
 */
public MathModelEditor() {
	super();
	initialize();
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		rightSplitPane.setDividerLocation(400);
		rightSplitPane.setBottomComponent(rightBottomTabbedPane);
		
		mathModelEditorTreeModel = new MathModelEditorTreeModel(documentEditorTree);
		mathModelEditorTreeModel.setSelectionManager(selectionManager);		
		
		mathModelEditorTreeCellRenderer = new MathModelEditorTreeCellRenderer();
		documentEditorTree.setModel(mathModelEditorTreeModel);
		documentEditorTree.setCellRenderer(mathModelEditorTreeCellRenderer);
		
		vcmlEditorPanel = new VCMLEditorPanel();
		vcmlEditorPanel.setMinimumSize(new java.awt.Dimension(198, 148));
		rightSplitPane.setTopComponent(vcmlEditorPanel);
		
		geometryViewer = new GeometryViewer();	
		geometryViewer.setSelectionManager(selectionManager);
		geometryViewer.setIssueManager(issueManager);
		
		simulationListPanel = new SimulationListPanel();
		simulationListPanel.setSelectionManager(selectionManager);
		simulationListPanel.setIssueManager(issueManager);

		outputFunctionsPanel  = new OutputFunctionsPanel();
		outputFunctionsPanel.setSelectionManager(selectionManager);
		outputFunctionsPanel.setIssueManager(issueManager);
		
		simulationSummaryPanel = new SimulationSummaryPanel();
		simulationSummaryPanel.setSelectionManager(selectionManager);
		simulationSummaryPanel.setIssueManager(issueManager);
		
		mathModelEditorAnnotationPanel.setSelectionManager(selectionManager);
		mathModelEditorAnnotationPanel.setIssueManager(issueManager);
				
		csgObjectPropertiesPanel = new CSGObjectPropertiesPanel();
		csgObjectPropertiesPanel.setSelectionManager(selectionManager);
		csgObjectPropertiesPanel.setIssueManager(issueManager);
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

@Override
protected void setRightBottomPanelOnSelection(Object[] selections) {
	if (selections == null) {
		return;
	}
	JComponent bottomComponent = rightBottomEmptyPanel;
	int destComponentIndex = DocumentEditorTabID.object_properties.ordinal();
	boolean bShowInDatabaseProperties = false;
	if (selections != null && selections.length == 1) {
		Object singleSelection = selections[0];
		if (singleSelection == mathModel) {
			bottomComponent = mathModelEditorAnnotationPanel;
		} else if (singleSelection instanceof DocumentEditorTreeFolderNode) {
			DocumentEditorTreeFolderNode folderNode = (DocumentEditorTreeFolderNode) singleSelection;
			if (folderNode.getFolderClass() == DocumentEditorTreeFolderClass.MATH_ANNOTATION_NODE) {		
				bottomComponent = mathModelEditorAnnotationPanel;
			} else if (folderNode.getFolderClass() == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE) {
				bottomComponent = simulationSummaryPanel;			
			}
		} else if (singleSelection instanceof BioModelInfo) {
			bShowInDatabaseProperties = true;
			bottomComponent = bioModelMetaDataPanel;
		} else if (singleSelection instanceof MathModelInfo) {
			bShowInDatabaseProperties = true;
			bottomComponent = mathModelMetaDataPanel;
		} else if (singleSelection instanceof GeometryInfo) {
			bShowInDatabaseProperties = true;
			bottomComponent = geometryMetaDataPanel;
		} else if (singleSelection instanceof Simulation) {
			bottomComponent = simulationSummaryPanel;
		} else if (singleSelection instanceof CSGObject) {
			bottomComponent = csgObjectPropertiesPanel;
			csgObjectPropertiesPanel.setSimulationContext(getSelectedSimulationContext());
		}
	}
	if (bShowInDatabaseProperties) {
		for (destComponentIndex = 0; destComponentIndex < rightBottomTabbedPane.getTabCount(); destComponentIndex ++) {
			Component c = rightBottomTabbedPane.getComponentAt(destComponentIndex);
			if (c == bioModelMetaDataPanel
				|| c == mathModelMetaDataPanel
				|| c == geometryMetaDataPanel) {
				break;
			}
		}
		if (rightBottomTabbedPane.getTabCount() == destComponentIndex) {
			rightBottomTabbedPane.addTab(DATABASE_PROPERTIES_TAB_TITLE, new TabCloseIcon(), bottomComponent);
		}
	}
	if (rightSplitPane.getBottomComponent() != rightBottomTabbedPane) {	
		rightSplitPane.setBottomComponent(rightBottomTabbedPane);
	}	
	if (rightBottomTabbedPane.getComponentAt(destComponentIndex) != bottomComponent) {
		rightBottomTabbedPane.setComponentAt(destComponentIndex, bottomComponent);
		rightBottomTabbedPane.repaint();
	}
	rightBottomTabbedPane.setSelectedComponent(bottomComponent);
}

@Override
protected void treeSelectionChanged() {
	try {
		Object lastSelectedPathComponent = documentEditorTree.getLastSelectedPathComponent();
		if (lastSelectedPathComponent == null || !(lastSelectedPathComponent instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)lastSelectedPathComponent;
	    Object selectedObject = selectedNode.getUserObject();
	    setRightTopPanel(selectedObject);
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

private void setRightTopPanel(Object selectedObject) {
	JComponent newTopPanel = emptyPanel;
	int dividerLocation = rightSplitPane.getDividerLocation();
	if (selectedObject == mathModel) {
		newTopPanel = mathModelPropertiesPanel;
		mathModelPropertiesPanel.setMathModel(mathModel);
	} else if (selectedObject instanceof DocumentEditorTreeFolderNode) {
		DocumentEditorTreeFolderNode folderNode = (DocumentEditorTreeFolderNode)selectedObject;
		DocumentEditorTreeFolderClass folderClass = folderNode.getFolderClass();
		if (folderClass == DocumentEditorTreeFolderClass.MATH_ANNOTATION_NODE) {
			newTopPanel = mathModelPropertiesPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_VCML_NODE) {
			newTopPanel = vcmlEditorPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_GEOMETRY_NODE) {
			newTopPanel = geometryViewer;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE) {
			newTopPanel = simulationListPanel;
		} else if(folderClass == DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE) {
			newTopPanel = outputFunctionsPanel;
			outputFunctionsPanel.setSimulationWorkspace(mathModelWindowManager.getSimulationWorkspace());
		}
	}
	Component rightTopComponent = rightSplitPane.getTopComponent();
	if (rightTopComponent != newTopPanel) {
		newTopPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		rightSplitPane.setTopComponent(newTopPanel);
	}
	rightSplitPane.setDividerLocation(dividerLocation);
}

/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setMathModel(MathModel newValue) {
	if (this.mathModel == newValue) {
		return;
	}
	this.mathModel = newValue;
	vcmlEditorPanel.setMathModel(mathModel);
	updateGeometryViewerOwner();	
	mathModelEditorTreeModel.setMathModel(mathModel);
	mathModelEditorAnnotationPanel.setMathModel(mathModel);
	
	issueManager.setVCDocument(mathModel);
}

/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 5:40:13 PM)
 * @param newBioModelWindowManager cbit.vcell.client.desktop.BioModelWindowManager
 */
public void setMathModelWindowManager(MathModelWindowManager newValue) {
	if (this.mathModelWindowManager == newValue) {
		return;
	}
	this.mathModelWindowManager = newValue;
	geometryViewer.addActionListener(mathModelWindowManager);
	mathModelPropertiesPanel.setMathModelWindowManager(mathModelWindowManager);
	simulationListPanel.setSimulationWorkspace(mathModelWindowManager.getSimulationWorkspace());
	
	DatabaseWindowManager dbWindowManager = new DatabaseWindowManager(databaseWindowPanel, mathModelWindowManager.getRequestManager());
	databaseWindowPanel.setDatabaseWindowManager(dbWindowManager);
	DocumentManager documentManager = mathModelWindowManager.getRequestManager().getDocumentManager();
	databaseWindowPanel.setDocumentManager(documentManager);
	
	geometryMetaDataPanel.setDocumentManager(documentManager);
	updateGeometryViewerOwner();
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new javax.swing.JFrame();
		MathModelEditor aBioModelEditor = new MathModelEditor();
		frame.setContentPane(aBioModelEditor);
		frame.setSize(aBioModelEditor.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

public boolean hasUnappliedChanges() {
	if (vcmlEditorPanel.hasUnappliedChanges()) {
		return true;
	}
	return false;
}

@Override
protected void popupMenuActionPerformed(DocumentEditorPopupMenuAction action, String actionCommand) {
	switch (action) {
	case add_new: 
		try {
			Object obj = documentEditorTree.getLastSelectedPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				return;
			}
			BioModelNode selectedNode = (BioModelNode) obj;
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof DocumentEditorTreeFolderNode) {
				DocumentEditorTreeFolderClass folderClass = ((DocumentEditorTreeFolderNode) userObject).getFolderClass();
				switch (folderClass) {
				case MATH_SIMULATIONS_NODE:
					Object newsim = mathModel.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX);
					selectionManager.setSelectedObjects(new Object[]{newsim});
					break;
				case MATH_OUTPUT_FUNCTIONS_NODE:
					break;
				}				
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
		break;
	}	
}


private void updateGeometryViewerOwner(){
	geometryViewer.setGeometryOwner(mathModel,mathModelWindowManager);
}

}
