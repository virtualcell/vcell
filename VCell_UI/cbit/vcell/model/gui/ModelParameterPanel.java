package cbit.vcell.model.gui;

import org.vcell.expression.ui.ScopedExpression;
import org.vcell.expression.ui.ScopedExpressionTableCellRenderer;

/**
 * Insert the type's description here.
 * Creation date: (9/23/2003 12:23:30 PM)
 * @author: Jim Schaff
 */
public class ModelParameterPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private ModelParameterTableModel ivjmodelParameterTableModel = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjScrollPaneTable = null;
	private cbit.vcell.model.Model fieldModel = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ModelParameterPanel.this && (evt.getPropertyName().equals("model"))) 
				connPtoP3SetTarget();
		};
	};
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.vcell.model.Model ivjmodel1 = null;

/**
 * ModelParameterPanel constructor comment.
 */
public ModelParameterPanel() {
	super();
	initialize();
}

/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ModelParameterPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ModelParameterPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ModelParameterPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ModelParameterPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM5:  (model1.this --> modelParameterTableModel.model)
 * @param value cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.model.Model value) {
	try {
		// user code begin {1}
		// user code end
		getmodelParameterTableModel().setModel(getmodel1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (ModelParameterPanel.initialize() --> modelParameterTableModel.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6() {
	try {
		// user code begin {1}
		// user code end
		if ((getmodel1() != null)) {
			getmodelParameterTableModel().setModel(getmodel1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (modelParameterTableModel.this <--> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getmodelParameterTableModel());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (ModelParameterPanel.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getmodel1() != null)) {
				this.setModel(getmodel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (ModelParameterPanel.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setmodel1(this.getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 9:10:39 PM)
 */
public void cleanupOnClose() {
	getmodelParameterTableModel().setModel(null);
}
/**
 * connEtoC7:  (ModelParameterPanel.initialize() --> ModelParameterPanel.modelParameterPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.modelParameterPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}


/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}


/**
 * Return the model1 property value.
 * @return cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Model getmodel1() {
	// user code begin {1}
	// user code end
	return ivjmodel1;
}


/**
 * Return the modelParameterTableModel property value.
 * @return cbit.vcell.model.gui.ModelParameterTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ModelParameterTableModel getmodelParameterTableModel() {
	if (ivjmodelParameterTableModel == null) {
		try {
			ivjmodelParameterTableModel = new cbit.vcell.model.gui.ModelParameterTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmodelParameterTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
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
		setName("ModelParameterPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(655, 226);
		add(getJScrollPane1(), "Center");
		initConnections();
		connEtoM6();
		connEtoC7();
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
		ModelParameterPanel aModelParameterPanel;
		aModelParameterPanel = new ModelParameterPanel();
		frame.setContentPane(aModelParameterPanel);
		frame.setSize(aModelParameterPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}
/**
 * Comment
 */
private void modelParameterPanel_Initialize() {
	
	getScrollPaneTable().setDefaultRenderer(ScopedExpression.class,new ScopedExpressionTableCellRenderer());
	
	getmodelParameterTableModel().addPropertyChangeListener(
		new java.beans.PropertyChangeListener(){
			public void propertyChange(java.beans.PropertyChangeEvent evt){
				if(evt.getPropertyName().equals("model")){
					ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
				}
			}
		}
	);
	getmodelParameterTableModel().addTableModelListener(
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				//System.out.println((
					//e.getType() == javax.swing.event.TableModelEvent.INSERT?"INSERT":"")+
					//(e.getType() == javax.swing.event.TableModelEvent.UPDATE?"UPDATE":"")+
					//(e.getType() == javax.swing.event.TableModelEvent.DELETE?"DELETE":""));
				//if(e.getType() == javax.swing.event.TableModelEvent.UPDATE){
					ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
				//}
			}
		}
	);
}

/**
 * Set the model1 to a new value.
 * @param newValue cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodel1(cbit.vcell.model.Model newValue) {
	if (ivjmodel1 != newValue) {
		try {
			cbit.vcell.model.Model oldValue = getmodel1();
			ivjmodel1 = newValue;
			connPtoP3SetSource();
			connEtoM5(ivjmodel1);
			firePropertyChange("model", oldValue, newValue);
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


}