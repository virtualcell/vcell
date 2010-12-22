package cbit.vcell.desktop;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import org.vcell.util.document.MathModelInfo;

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
public class MathModelMetaDataPanel extends JPanel {
	private JTree ivjJTree1 = null;
	private MathModelMetaDataCellRenderer ivjmathModelMetaDataCellRenderer = null;
	private MathModelMetaDataTreeModel ivjmathModelMetaDataTreeModel = null;
	private DocumentManager fieldDocumentManager = null;
	private boolean fieldPopupMenuDisabled = false;
	private MathModelInfo fieldMathModelInfo = null;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private MathModelInfo ivjmathModelInfo1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathModelMetaDataPanel.this && (evt.getPropertyName().equals("mathModelInfo"))) 
				connPtoP1SetTarget();
		};
	};
/**
 * BioModelMetaDataPanel constructor comment.
 */
public MathModelMetaDataPanel() {
	super();
	initialize();
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MathModelMetaDataPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MathModelMetaDataPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * BioModelMetaDataPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public MathModelMetaDataPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  ( (mathModelInfo1,this --> mathModelMetaDataTreeModel,mathModelInfo).normalResult --> MathModelMetaDataPanel.expandAllRows()V)
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
 * connEtoC2:  (MathModelMetaDataPanel.initialize() --> MathModelMetaDataPanel.enableToolTips(Ljavax.swing.JTree;)V)
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
 * connEtoC8:  (MathModelMetaDataPanel.initialize() --> MathModelMetaDataPanel.mathModelMetaDataPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8() {
	try {
		// user code begin {1}
		// user code end
		this.mathModelMetaDataPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (mathModelInfo1.this --> mathModelMetaDataTreeModel.mathModelInfo)
 * @param value cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(org.vcell.util.document.MathModelInfo value) {
	try {
		// user code begin {1}
		// user code end
		getmathModelMetaDataTreeModel().setMathModelInfo(getmathModelInfo1());
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
 * connPtoP1SetSource:  (MathModelMetaDataPanel.mathModelInfo <--> mathModelInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getmathModelInfo1() != null)) {
				this.setMathModelInfo(getmathModelInfo1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (MathModelMetaDataPanel.mathModelInfo <--> mathModelInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setmathModelInfo1(this.getMathModelInfo());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
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
		getJTree1().setModel(getmathModelMetaDataTreeModel());
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
		getJTree1().setCellRenderer(getmathModelMetaDataCellRenderer());
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
public void enableToolTips(JTree tree) {
	ToolTipManager.sharedInstance().registerComponent(tree);
}
/**
 * Comment
 */
public void expandAllRows() {
	for (int i=0;i<3;i++){	
		int numRows = getJTree1().getRowCount();
		for (int row=0;row<numRows;row++){
			getJTree1().expandRow(row);
		}
	}
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
 * @return cbit.gui.JTreeFancy
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new JTree();
			ivjJTree1.setName("JTree1");
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
 * Gets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The mathModelInfo property value.
 * @see #setMathModelInfo
 */
public org.vcell.util.document.MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
}
/**
 * Return the mathModelInfo1 property value.
 * @return cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.document.MathModelInfo getmathModelInfo1() {
	// user code begin {1}
	// user code end
	return ivjmathModelInfo1;
}
/**
 * Return the mathModelMetaDataCellRenderer property value.
 * @return cbit.vcell.desktop.MathModelMetaDataCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataCellRenderer getmathModelMetaDataCellRenderer() {
	if (ivjmathModelMetaDataCellRenderer == null) {
		try {
			ivjmathModelMetaDataCellRenderer = new cbit.vcell.desktop.MathModelMetaDataCellRenderer();
			ivjmathModelMetaDataCellRenderer.setName("mathModelMetaDataCellRenderer");
			ivjmathModelMetaDataCellRenderer.setText("mathModelMetaDataCellRenderer");
			ivjmathModelMetaDataCellRenderer.setBounds(414, 318, 190, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataCellRenderer;
}
/**
 * Return the mathModelMetaDataTreeModel property value.
 * @return cbit.vcell.desktop.MathModelMetaDataTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataTreeModel getmathModelMetaDataTreeModel() {
	if (ivjmathModelMetaDataTreeModel == null) {
		try {
			ivjmathModelMetaDataTreeModel = new cbit.vcell.desktop.MathModelMetaDataTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataTreeModel;
}
/**
 * Gets the popupMenuDisabled property (boolean) value.
 * @return The popupMenuDisabled property value.
 * @see #setPopupMenuDisabled
 */
public boolean getPopupMenuDisabled() {
	return fieldPopupMenuDisabled;
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
	connPtoP3SetTarget();
	connPtoP2SetTarget();
	connPtoP1SetTarget();
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
		connEtoC8();
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
 * Comment
 */
private void mathModelMetaDataPanel_Initialize() {
	getJTree1().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
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
/**
 * Sets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param mathModelInfo The new value for the property.
 * @see #getMathModelInfo
 */
public void setMathModelInfo(org.vcell.util.document.MathModelInfo mathModelInfo) {
	org.vcell.util.document.MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
}
/**
 * Set the mathModelInfo1 to a new value.
 * @param newValue cbit.vcell.mathmodel.MathModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathModelInfo1(org.vcell.util.document.MathModelInfo newValue) {
	if (ivjmathModelInfo1 != newValue) {
		try {
			org.vcell.util.document.MathModelInfo oldValue = getmathModelInfo1();
			ivjmathModelInfo1 = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjmathModelInfo1);
			firePropertyChange("mathModelInfo", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Sets the popupMenuDisabled property (boolean) value.
 * @param popupMenuDisabled The new value for the property.
 * @see #getPopupMenuDisabled
 */
public void setPopupMenuDisabled(boolean popupMenuDisabled) {
	boolean oldValue = fieldPopupMenuDisabled;
	fieldPopupMenuDisabled = popupMenuDisabled;
	firePropertyChange("popupMenuDisabled", new Boolean(oldValue), new Boolean(popupMenuDisabled));
}

}
