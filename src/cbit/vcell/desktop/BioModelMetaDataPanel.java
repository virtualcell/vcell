package cbit.vcell.desktop;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.vcell.util.document.BioModelInfo;

import cbit.vcell.clientdb.DocumentManager;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (3/29/01 10:39:36 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelMetaDataPanel extends JPanel {

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelMetaDataPanel.this && (evt.getPropertyName().equals("bioModelInfo"))) 
				connEtoM1(evt);
		};
	}
	private JTree ivjJTree1 = null;
	private BioModelInfoCellRenderer ivjbioModelInfoCellRenderer = null;
	private DocumentManager fieldDocumentManager = null;
	private BioModelInfoTreeModel ivjbioModelInfoTreeModel = null;
	private BioModelInfo fieldBioModelInfo = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * BioModelMetaDataPanel constructor comment.
 */
public BioModelMetaDataPanel() {
	super();
	initialize();
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public BioModelMetaDataPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public BioModelMetaDataPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public BioModelMetaDataPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Comment
 */
private void bioModelMetaDataPanel_Initialize() {
	getJTree1().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
}
/**
 * connEtoC1:  ( (BioModelMetaDataPanel,bioModelInfo --> bioModelInfoTreeModel,bioModelInfo).normalResult --> BioModelMetaDataPanel.expandAllRows()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.expandAllRows();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (BioModelMetaDataPanel.initialize() --> BioModelMetaDataPanel.enableToolTips(Ljavax.swing.JTree;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.enableToolTips(getJTree1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (BioModelMetaDataPanel.initialize() --> BioModelMetaDataPanel.bioModelMetaDataPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.bioModelMetaDataPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (BioModelMetaDataPanel.bioModelInfo --> bioModelInfoTreeModel.bioModelInfo)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getbioModelInfoTreeModel().setBioModelInfo(this.getBioModelInfo());
		connEtoC1();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (bioModelMetaDataTreeModel.this <--> JTree1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setModel(getbioModelInfoTreeModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetTarget:  (bioModelMetaDataCellRenderer.this <--> JTree1.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setCellRenderer(getbioModelInfoCellRenderer());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void enableToolTips(javax.swing.JTree tree) {
	javax.swing.ToolTipManager.sharedInstance().registerComponent(tree);
}
/**
 * Comment
 */
public void expandAllRows() {
	for (int row=0;row<getJTree1().getRowCount();row++){
		getJTree1().expandRow(row);
	}
}
/**
 * Gets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @return The bioModelInfo property value.
 * @see #setBioModelInfo
 */
public org.vcell.util.document.BioModelInfo getBioModelInfo() {
	return fieldBioModelInfo;
}
/**
 * Return the bioModelInfoCellRenderer property value.
 * @return cbit.vcell.desktop.BioModelInfoCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelInfoCellRenderer getbioModelInfoCellRenderer() {
	if (ivjbioModelInfoCellRenderer == null) {
		try {
			ivjbioModelInfoCellRenderer = new cbit.vcell.desktop.BioModelInfoCellRenderer();
			ivjbioModelInfoCellRenderer.setName("bioModelInfoCellRenderer");
			ivjbioModelInfoCellRenderer.setText("bioModelInfoCellRenderer");
			ivjbioModelInfoCellRenderer.setBounds(446, 285, 179, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbioModelInfoCellRenderer;
}
/**
 * Return the bioModelMetaDataTreeModel property value.
 * @return cbit.vcell.desktop.BioModelMetaDataTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelInfoTreeModel getbioModelInfoTreeModel() {
	if (ivjbioModelInfoTreeModel == null) {
		try {
			ivjbioModelInfoTreeModel = new cbit.vcell.desktop.BioModelInfoTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbioModelInfoTreeModel;
}
/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}

/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setToolTipText("Contents of saved BioModel");
			ivjJTree1.setEnabled(true);
			ivjJTree1.setRootVisible(false);
			ivjJTree1.setRequestFocusEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BioModelMetaDataPanel");
		setLayout(new BorderLayout());
//		setSize(379, 460);

		add(new JScrollPane(getJTree1()), BorderLayout.CENTER);
		initConnections();
		connEtoC2();
		connEtoC3();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelMetaDataPanel aBioModelMetaDataPanel;
		aBioModelMetaDataPanel = new BioModelMetaDataPanel();
		frame.setContentPane(aBioModelMetaDataPanel);
		frame.setSize(aBioModelMetaDataPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Perform the refresh method.
 */
public void refresh() {
	getbioModelInfoTreeModel().setBioModelInfo(getBioModelInfo());
	expandAllRows();
	return;
}
/**
 * Sets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @param bioModelInfo The new value for the property.
 * @see #getBioModelInfo
 */
public void setBioModelInfo(org.vcell.util.document.BioModelInfo bioModelInfo) {
	org.vcell.util.document.BioModelInfo oldValue = fieldBioModelInfo;
	fieldBioModelInfo = bioModelInfo;
	firePropertyChange("bioModelInfo", oldValue, bioModelInfo);
}
/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}

}
