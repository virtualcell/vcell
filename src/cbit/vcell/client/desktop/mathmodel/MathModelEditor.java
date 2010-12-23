package cbit.vcell.client.desktop.mathmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelMetaDataPanel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.GeometryMetaDataPanel;
import cbit.vcell.desktop.MathModelMetaDataPanel;
import cbit.vcell.geometry.GeometryInfo;
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
	private JPanel rightBottomEmptyPanel = null;
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
		rightBottomEmptyPanel = new JPanel(new GridBagLayout());
		rightBottomEmptyPanel.setBackground(Color.white);
		JLabel label = new JLabel("Select only one object (e.g. species, reaction) to show properties.");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(10,10,4,4);
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;
		rightBottomEmptyPanel.add(label, gbc);

		rightBottomEmptyPanel.setMinimumSize(new java.awt.Dimension(198, 148));		
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
	JComponent bottomComponent = rightBottomEmptyPanel;
	boolean bShowBottom = true;
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
			if (bioModelMetaDataPanel == null) {
				bioModelMetaDataPanel = new BioModelMetaDataPanel();
				bioModelMetaDataPanel.setDocumentManager(mathModelWindowManager.getRequestManager().getDocumentManager());
			}
			bioModelMetaDataPanel.setBioModelInfo((BioModelInfo) singleSelection);
			bottomComponent = bioModelMetaDataPanel;
		} else if (singleSelection instanceof MathModelInfo) {
			if (mathModelMetaDataPanel == null) {
				mathModelMetaDataPanel = new MathModelMetaDataPanel();
				mathModelMetaDataPanel.setDocumentManager(mathModelWindowManager.getRequestManager().getDocumentManager());
			}
			mathModelMetaDataPanel.setMathModelInfo((MathModelInfo) singleSelection);
			bottomComponent = mathModelMetaDataPanel;
		} else if (singleSelection instanceof GeometryInfo) {
			if (geometryMetaDataPanel == null) {
				geometryMetaDataPanel = new GeometryMetaDataPanel();
				geometryMetaDataPanel.setDocumentManager(mathModelWindowManager.getRequestManager().getDocumentManager());
			}
			geometryMetaDataPanel.setGeometryInfo((GeometryInfo) singleSelection);
			bottomComponent = geometryMetaDataPanel;
		} else if (singleSelection instanceof Simulation) {
			bottomComponent = simulationSummaryPanel;
		} else {
			bShowBottom = false;
		}
	}
	if (bShowBottom) {
		if (rightSplitPane.getBottomComponent() != bottomComponent) {
			rightSplitPane.setBottomComponent(bottomComponent);
		}
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
			simulationListPanel.setSimulationWorkspace(mathModelWindowManager.getSimulationWorkspace());			
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
	mathModelEditorTreeCellRenderer.setMathModel(mathModel);
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
	
	DatabaseWindowManager dbWindowManager = new DatabaseWindowManager(databaseWindowPanel, mathModelWindowManager.getRequestManager());
	databaseWindowPanel.setDatabaseWindowManager(dbWindowManager);
	DocumentManager documentManager = mathModelWindowManager.getRequestManager().getDocumentManager();
	databaseWindowPanel.setDocumentManager(documentManager);
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

}