package cbit.vcell.mapping.gui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.desktop.biomodel.SPPRPanel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.gui.FluxReactionPanel;
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
	private SPPRPanel spprPanel = null;
	private JSplitPane outerSplitPane;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private SimpleReactionPanel simpleReactionPanel = null;
	private FluxReactionPanel fluxReactionPanel = null;
	private JSortTable ivjScrollPaneTable = null;
	private ReactionSpecsTableModel ivjReactionSpecsTableModel = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext ivjsimulationContext1 = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
// java.awt.event.ActionListener, java.awt.event.MouseListener
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReactionSpecsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP2SetTarget();
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() instanceof DefaultListSelectionModel) {
//				System.out.println("ReactionSpecsPanel:  valueChanged()");
				connEtoM3(e);
			}
		};
	};

/**
 * ReactionSpecsPanel constructor comment.
 */
public ReactionSpecsPanel() {
	super();
	spprPanel = null;
	initialize(false);
}

public ReactionSpecsPanel(boolean expanded) {
	super();
	spprPanel = null;
	initialize(expanded);
}

public ReactionSpecsPanel(SPPRPanel aPanel) {
	super();
	spprPanel = aPanel;
	initialize(true);
}

private SPPRPanel getSPPRPanel() {
	return spprPanel;
}

/**
 * Initialize the class.
 */
private void initialize(boolean expanded) {
	try {
		setName("ReactionSpecsPanel");
		setLayout(new java.awt.GridBagLayout());
		//setSize(456, 539);

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

private JSplitPane getOuterSplitPane(boolean expanded) {
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
private FluxReactionPanel getFluxReactionPanel() {
	if (fluxReactionPanel == null) {
		try {
			fluxReactionPanel = new FluxReactionPanel(true);
			fluxReactionPanel.setName("FluxReactionPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	BeanUtils.enableComponents(fluxReactionPanel, false);
	return fluxReactionPanel;
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

/**
 * connPtoP5SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			ivjConnPtoP5Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable().setSelectionModel(getselectionModel1());
			}
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetTarget:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			ivjConnPtoP5Aligning = true;
			setselectionModel1(getScrollPaneTable().getSelectionModel());
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		handleException(ivjExc);
	}
}
/**
 */
private void connEtoM3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		int row = getselectionModel1().getLeadSelectionIndex();
		if((row >= 0) && (getSPPRPanel() != null)) {
			getSPPRPanel().setScrollPaneTreeCurrentRow(getReactionSpecsTableModel().getValueAt(row, 0));
		}

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Return the selectionModel1 property value.
 */
private javax.swing.ListSelectionModel getselectionModel1() {
	return ivjselectionModel1;
}
/**
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
private void setselectionModel1(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addListSelectionListener(ivjEventHandler);
			}
			connPtoP5SetSource();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}



private void notifyRelatedPanels(javax.swing.event.ListSelectionEvent arg1) {
	try {
		System.out.println("Reaction Spec selection changed");
	} catch (java.lang.Throwable ivjExc) {
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
			ivjReactionSpecsTableModel = new ReactionSpecsTableModel(getScrollPaneTable(), false);
		} catch (java.lang.Throwable ivjExc) {
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
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
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
	connPtoP5SetTarget();
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
	if((selection instanceof SimpleReaction) && (outerSplitPane.getBottomComponent() != getSimpleReactionPanel())) {
//		System.out.println("Simple reaction panel");
		outerSplitPane.setBottomComponent(getSimpleReactionPanel());
		outerSplitPane.setDividerLocation(300);
	} else if((selection instanceof FluxReaction) && (outerSplitPane.getBottomComponent() != getFluxReactionPanel())) {
//		System.out.println("Flux reaction panel");
		outerSplitPane.setBottomComponent(getFluxReactionPanel());
		outerSplitPane.setDividerLocation(300);
	}
    ReactionStep[] reactionSteps = getSimulationContext().getModel().getReactionSteps();
    for (int i = 0; i < reactionSteps.length; i++) {
    	if(selection.equals(reactionSteps[i])) {
    		if(reactionSteps[i] instanceof SimpleReaction ) {
//        		System.out.println("Simple reaction: " + selection.getName());
    			getSimpleReactionPanel().setSimpleReaction((SimpleReaction)reactionSteps[i]);
    			break;
    		} else if(reactionSteps[i] instanceof FluxReaction) {
//        		System.out.println("Flux reaction: " + selection.getName());
    			getFluxReactionPanel().setFluxReaction1((FluxReaction)reactionSteps[i]);
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