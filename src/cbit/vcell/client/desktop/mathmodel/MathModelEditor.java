package cbit.vcell.client.desktop.mathmodel;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.client.desktop.biomodel.TabCloseIcon;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
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
	private GeometrySummaryViewer geometrySummaryViewer = null;
	private OutputFunctionsPanel outputFunctionsPanel = null;
//	private EquationViewerPanel equationViewerPanel;
	private VCMLEditorPanel vcmlEditorPanel;
	
	private MathModelEditorTreeCellRenderer mathModelEditorTreeCellRenderer = null;
	private MathModelEditorTreeModel mathModelEditorTreeModel = null;
	
	private SimulationSummaryPanel simulationSummaryPanel = null;
	private MathModelPropertiesPanel mathModelPropertiesPanel = new MathModelPropertiesPanel();
	private MathModelEditorAnnotationPanel mathModelEditorAnnotationPanel = new MathModelEditorAnnotationPanel();
	
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

		rightSplitPane.setBottomComponent(null);
		
		mathModelEditorTreeModel = new MathModelEditorTreeModel(documentEditorTree);
		mathModelEditorTreeCellRenderer = new MathModelEditorTreeCellRenderer(documentEditorTree);
		documentEditorTree.setModel(mathModelEditorTreeModel);
		documentEditorTree.setCellRenderer(mathModelEditorTreeCellRenderer);
		
		vcmlEditorPanel = new VCMLEditorPanel();
		vcmlEditorPanel.setMinimumSize(new java.awt.Dimension(198, 148));
		rightSplitPane.setTopComponent(vcmlEditorPanel);
		geometrySummaryViewer = new GeometrySummaryViewer();		
		simulationListPanel = new SimulationListPanel();
		simulationSummaryPanel = new SimulationSummaryPanel();		
		outputFunctionsPanel  = new OutputFunctionsPanel();
		simulationSummaryPanel = new SimulationSummaryPanel();
		
		mathModelEditorAnnotationPanel.setSelectionManager(selectionManager);
		outputFunctionsPanel.setSelectionManager(selectionManager);
		mathModelEditorTreeModel.setSelectionManager(selectionManager);		
		simulationListPanel.setSelectionManager(selectionManager);
		simulationSummaryPanel.setSelectionManager(selectionManager);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

@Override
protected void setRightBottomPanelOnSelection(Object[] selections) {
	if (selections == null || selections.length == 0) {
		return;
	}
	JComponent bottomComponent = rightBottomEmptyPanel;
	boolean bShowBottom = true;
	int destComponentIndex = RIGHT_BOTTOM_TAB_PROPERTIES_INDEX;
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
			} else {
				bShowBottom = false;
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
		} else {
			bShowBottom = false;
		}
	}
	if (bShowBottom) {
		if (bShowInDatabaseProperties) {
			for (destComponentIndex = 0; destComponentIndex < rightBottomTabbedPane.getComponentCount(); destComponentIndex ++) {
				if (rightBottomTabbedPane.getTitleAt(destComponentIndex) == DATABASE_PROPERTIES_TAB_TITLE) {
					break;
				}
			}
			if (rightBottomTabbedPane.getComponentCount() == destComponentIndex) {
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
	} else {
		rightSplitPane.setBottomComponent(null);
	}
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
	    if (selectedObject instanceof MathModel) {
	    	setRightTopPanel(null, selectedObject);
	    } else if (selectedObject instanceof DocumentEditorTreeFolderNode) { // it's a folder	    	
	    	setRightTopPanel((DocumentEditorTreeFolderNode)selectedObject, null);
	    } else {
	        Object leafObject = selectedObject;
			BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
			Object parentObject =  parentNode.getUserObject();
			if (!(parentObject instanceof DocumentEditorTreeFolderNode)) {
				return;
			}
			DocumentEditorTreeFolderNode parent = (DocumentEditorTreeFolderNode)parentObject;
			setRightTopPanel(parent, leafObject);
	    }
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

private void setRightTopPanel(DocumentEditorTreeFolderNode folderNode, Object leafObject) {
	JComponent newTopPanel = emptyPanel;
	double dividerLocation = DEFAULT_DIVIDER_LOCATION;
	if (folderNode == null) {
		if (leafObject == mathModel) {
			newTopPanel = mathModelPropertiesPanel;
		}
	} else {
		DocumentEditorTreeFolderClass folderClass = folderNode.getFolderClass();
		if (folderClass == DocumentEditorTreeFolderClass.MATH_ANNOTATION_NODE) {
			newTopPanel = mathModelPropertiesPanel;
			dividerLocation = 1.0;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_VCML_NODE) {
			newTopPanel = vcmlEditorPanel;
			dividerLocation = 1.0;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_GEOMETRY_NODE) {
			newTopPanel = geometrySummaryViewer;
			dividerLocation = 1.0;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE) {
			newTopPanel = simulationListPanel;
			dividerLocation = 0.4;
		} else if(folderClass == DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE) {
			dividerLocation = 1.0;
			newTopPanel = outputFunctionsPanel;
			outputFunctionsPanel.setSimulationWorkspace(mathModelWindowManager.getSimulationWorkspace());
		}
	}
	Component rightTopComponent = rightSplitPane.getTopComponent();
	if (rightTopComponent != newTopPanel) {
		rightSplitPane.setTopComponent(newTopPanel);
	}
	if (dividerLocation < 1.0) {
		rightSplitPane.setDividerLocation(dividerLocation);
	}
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
//	mathModelEditorTreeCellRenderer.setMathModel(mathModel);
	vcmlEditorPanel.setMathModel(mathModel);
	geometrySummaryViewer.setGeometryOwner(mathModel);
	mathModelPropertiesPanel.setMathModel(mathModel);
	mathModelEditorTreeModel.setMathModel(mathModel);
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
	geometrySummaryViewer.addActionListener(mathModelWindowManager);
	mathModelPropertiesPanel.setMathModelWindowManager(mathModelWindowManager);
	simulationListPanel.setSimulationWorkspace(mathModelWindowManager.getSimulationWorkspace());
	
	DatabaseWindowManager dbWindowManager = new DatabaseWindowManager(databaseWindowPanel, mathModelWindowManager.getRequestManager());
	databaseWindowPanel.setDatabaseWindowManager(dbWindowManager);
	DocumentManager documentManager = mathModelWindowManager.getRequestManager().getDocumentManager();
	databaseWindowPanel.setDocumentManager(documentManager);
	
	geometryMetaDataPanel.setDocumentManager(documentManager);
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
					AsynchClientTask task1 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
						
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							mathModel.refreshMathDescription();
						}
					};
					AsynchClientTask task2 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
						
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							Object newsim = mathModel.addNewSimulation();
							selectionManager.setSelectedObjects(new Object[]{newsim});
						}
					};
					ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});					
					break;
				case MATH_OUTPUT_FUNCTIONS_NODE:
					break;
				}				
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
		break;
	case delete:
		try {
			TreePath[] selectedPaths = documentEditorTree.getSelectionPaths();
			List<Simulation> simulationList = new ArrayList<Simulation>();
			List<AnnotatedFunction> outputFunctionList = new ArrayList<AnnotatedFunction>();
			StringBuilder sb = new StringBuilder();
			for (TreePath tp : selectedPaths) {
				Object obj = tp.getLastPathComponent();
				if (obj == null || !(obj instanceof BioModelNode)) {
					continue;
				}				
				BioModelNode selectedNode = (BioModelNode) obj;
				Object userObject = selectedNode.getUserObject();
				if (userObject instanceof Simulation) {
					Simulation simulation = (Simulation)userObject;
					simulationList.add(simulation);
				} else if (userObject instanceof AnnotatedFunction) {
					AnnotatedFunction annotatedFunction = (AnnotatedFunction)userObject;
					outputFunctionList.add(annotatedFunction);
				}
			}
			for (Simulation	simulation : simulationList) {
				sb.append("\t" + simulation.getName() + "\n");
			}
			if (outputFunctionList.size() > 0) {
				sb.append("Output Function(s): \n");
			}
			for (AnnotatedFunction annotatedFunction: outputFunctionList) {
				sb.append("\t" + annotatedFunction.getName() + "\n");
			}			
			if (sb.length() > 0) {
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "You are going to delete the following:\n\n" + sb.toString() + "\n Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}				
				for (Simulation simulation : simulationList) {
					mathModel.removeSimulation(simulation);
				}
				if (outputFunctionList.size() > 0) {
					for (AnnotatedFunction annotatedFunction: outputFunctionList) {
						mathModel.getOutputFunctionContext().removeOutputFunction(annotatedFunction);					
					}
				}
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
		break;
	}	
}

}