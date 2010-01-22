package cbit.vcell.mapping.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.util.BeanUtils;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.gui.SimpleReactionPanel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:31:14 PM)
 * @author: 
 */
public class ReactionSpecsPanel extends javax.swing.JPanel {
	private JSplitPane outerSplitPane;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private SimpleReactionPanel simpleReactionPanel = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private ReactionSpecsTableModel ivjReactionSpecsTableModel = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext ivjsimulationContext1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReactionSpecsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP2SetTarget();
		};
	};

/**
 * ReactionSpecsPanel constructor comment.
 */
public ReactionSpecsPanel() {
	super();
	initialize(false);
}

public ReactionSpecsPanel(boolean expanded) {
	super();
	initialize(expanded);
}

/**
 * Initialize the class.
 */
private void initialize(boolean expanded) {
	try {
		setName("ReactionSpecsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(456, 539);

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getOuterSplitPane(expanded), constraintsJSplitPane1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

protected JSplitPane getOuterSplitPane(boolean expanded) {
	if (outerSplitPane == null) {
		outerSplitPane = new JSplitPane();
		outerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		outerSplitPane.setDividerLocation(300);
		outerSplitPane.setTopComponent(getJScrollPane1());
		if(expanded) {
			outerSplitPane.setBottomComponent(getSimpleReactionPanel());	// reaction kinetics editor
		} else {
			outerSplitPane.setBottomComponent(null);
		}
	}
	return outerSplitPane;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setViewportView(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
private SimpleReactionPanel getSimpleReactionPanel() {
	if (simpleReactionPanel == null) {
		try {
			simpleReactionPanel = new SimpleReactionPanel(true);
			simpleReactionPanel.setName("SimpleReactionPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	BeanUtils.enableComponents(simpleReactionPanel, false);
	return simpleReactionPanel;
}

/**
 * connEtoM4:  (simulationContext1.this --> ReactionSpecsTableModel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getReactionSpecsTableModel().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (ScrollPaneTable.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getReactionSpecsTableModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (ReactionSpecsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (ReactionSpecsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setsimulationContext1(this.getSimulationContext());
			//amended on 14th June, 2007. fast column in reactionSpecTable is not needed for stochastic applications.
			if(getsimulationContext1() != null && getsimulationContext1().isStoch())
			{
				TableColumn fastColumn = getScrollPaneTable().getColumn(getScrollPaneTable().getModel().getColumnName(ReactionSpecsTableModel.COLUMN_FAST));
				fastColumn.setMaxWidth(0);
				fastColumn.setMinWidth(0);
				fastColumn.setPreferredWidth(0);
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/*
 * Return the ReactionSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.ReactionSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionSpecsTableModel getReactionSpecsTableModel() {
	if (ivjReactionSpecsTableModel == null) {
		try {
			ivjReactionSpecsTableModel = new cbit.vcell.mapping.gui.ReactionSpecsTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionSpecsTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
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
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationContext getsimulationContext1() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext1;
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
	connPtoP2SetTarget();
	getScrollPaneTable().setDefaultRenderer(ReactionStep.class, new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof ReactionStep) {
				setText(((ReactionStep)value).getName());
			}
			return this;
		}
	});
}

public void setScrollPaneTableCurrentRow(ReactionStep selection) {
	if (selection == null) {
		return;
	}

	int numRows = getScrollPaneTable().getRowCount();
	for(int i=0; i<numRows; i++) {
		ReactionStep valueAt = (ReactionStep)getScrollPaneTable().getValueAt(i, ReactionSpecsTableModel.COLUMN_NAME);
		if(valueAt.equals(selection)) {
			getScrollPaneTable().changeSelection(i, 0, false, false);
			break;
		}
	}
	
    ReactionStep[] reactionSteps = getSimulationContext().getModel().getReactionSteps();
    for (int i = 0; i < reactionSteps.length; i++) {
    	if(selection.equals(reactionSteps[i])) {
    		System.out.println(selection);
    		if(reactionSteps[i] instanceof SimpleReaction ) {
    			getSimpleReactionPanel().setSimpleReaction((SimpleReaction)reactionSteps[i]);
    			break;
    		}
    	}
    }
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReactionSpecsPanel aReactionSpecsPanel;
		aReactionSpecsPanel = new ReactionSpecsPanel(true);
		frame.setContentPane(aReactionSpecsPanel);
		frame.setSize(aReactionSpecsPanel.getSize());
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
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			SimulationContext oldValue = getsimulationContext1();
			ivjsimulationContext1 = newValue;
			connPtoP2SetSource();
			connEtoM4(ivjsimulationContext1);
			firePropertyChange("simulationContext", oldValue, newValue);
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