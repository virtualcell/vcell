package cbit.vcell.client.desktop.simulation;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.solvers.SimulationStatus;

import javax.swing.table.TableCellEditor;
import java.util.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 3:41:07 PM)
 * @author: Ion Moraru
 */
public class SimulationListPanel extends JPanel {
	private JPanel ivjButtonPanel = null;
	private JButton ivjCopyButton = null;
	private JButton ivjDeleteButton = null;
	private JButton ivjEditButton = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JButton ivjNewButton = null;
	private JButton ivjResultsButton = null;
	private JButton ivjRunButton = null;
	private org.vcell.util.gui.JTableFixed ivjScrollPaneTable = null;
	private JButton ivjStopButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationListTableModel ivjSimulationListTableModel1 = null;
	private SimulationWorkspace fieldSimulationWorkspace = null;
	private boolean ivjConnPtoP2Aligning = false;
	private ListSelectionModel ivjselectionModel1 = null;
	private cbit.vcell.solver.ode.gui.SimulationSummaryPanel ivjSimulationSummaryPanel1 = null;
	private DefaultCellEditor ivjcellEditor1 = null;
	private java.awt.Component ivjComponent1 = null;
	private JButton ivjStatusDetailsButton = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener, javax.swing.event.TableModelListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SimulationListPanel.this.getNewButton()) 
				connEtoC2(e);
			if (e.getSource() == SimulationListPanel.this.getEditButton()) 
				connEtoC3(e);
			if (e.getSource() == SimulationListPanel.this.getCopyButton()) 
				connEtoC4(e);
			if (e.getSource() == SimulationListPanel.this.getDeleteButton()) 
				connEtoC5(e);
			if (e.getSource() == SimulationListPanel.this.getRunButton()) 
				connEtoC6(e);
			if (e.getSource() == SimulationListPanel.this.getStopButton()) 
				connEtoC7(e);
			if (e.getSource() == SimulationListPanel.this.getResultsButton()) 
				connEtoC8(e);
			if (e.getSource() == SimulationListPanel.this.getStatusDetailsButton()) 
				connEtoC13(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == SimulationListPanel.this.getComponent1()) 
				connEtoC11(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimulationListPanel.this && (evt.getPropertyName().equals("simulationWorkspace"))) 
				connEtoM1(evt);
			if (evt.getSource() == SimulationListPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == SimulationListPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM4(evt);
		};
		public void tableChanged(javax.swing.event.TableModelEvent e) {
			if (e.getSource() == SimulationListPanel.this.getSimulationListTableModel1()) 
				connEtoC10(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == SimulationListPanel.this.getselectionModel1()) 
				connEtoC9(e);
		};
	};

public SimulationListPanel() {
	super();
	initialize();
}

/**
 * Comment
 */
private void component1_FocusLost(java.awt.event.FocusEvent focusEvent) {
	if(getcellEditor1() != null){
		getcellEditor1().stopCellEditing();
	}
}


/**
 * connEtoC1:  (SimulationListPanel.initialize() --> SimulationListPanel.setSelectionMode()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.customizeTable();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (SimulationListTableModel1.tableModel.tableChanged(javax.swing.event.TableModelEvent) --> SimulationListPanel.refreshButtonsAndSummary()V)
 * @param arg1 javax.swing.event.TableModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(javax.swing.event.TableModelEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refreshButtonsAndSummary();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> SimulationListPanel.component1_FocusLost(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.component1_FocusLost(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  ( (EditButton,action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel,editSimulation()V).normalResult --> SimulationListPanel.refreshButtonsAndSummary()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12() {
	try {
		// user code begin {1}
		// user code end
		this.refreshSimListTable();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC13:  (StatusDetailsButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.showStatusDetails(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showSimulationStatusDetails(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC2:  (NewButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.newSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newSimulation();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (EditButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.editSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.editSimulation();
		connEtoC12();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (CopyButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.copySimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copySimulations();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (DeleteButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.deleteSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deleteSimulations();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (RunButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.runSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.runSimulations();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC7:  (StopButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.stopSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopSimulations();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC8:  (ResultsButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.showSimulationResults()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showSimulationResults();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SimulationListPanel.refreshButtons()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refreshButtonsAndSummary();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (SimulationListPanel.simulationWorkspace --> SimulationListTableModel1.simulationWorkspace)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSimulationListTableModel1().setSimulationWorkspace(this.getSimulationWorkspace());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (cellEditor1.this --> Component1.this)
 * @param value javax.swing.table.TableCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(javax.swing.DefaultCellEditor value) {
	try {
		// user code begin {1}
		// user code end
		if ((getcellEditor1() != null)) {
			setComponent1(getcellEditor1().getComponent());
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
 * connEtoM4:  (ScrollPaneTable.cellEditor --> cellEditor1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setcellEditor1((javax.swing.DefaultCellEditor)getScrollPaneTable().getCellEditor());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (SimulationListTableModel1.this <--> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getSimulationListTableModel1());
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
 * connPtoP2SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable().setSelectionModel(getselectionModel1());
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
 * connPtoP2SetTarget:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setselectionModel1(getScrollPaneTable().getSelectionModel());
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
 * Comment
 */
private void copySimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector v = new Vector();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] toCopy = (Simulation[])org.vcell.util.BeanUtils.getArray(v, Simulation.class);
	int index = -1;
	try {
		index = getSimulationWorkspace().copySimulations(toCopy);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		cbit.vcell.client.PopupGenerator.showErrorDialog(this, "Could not copy all simulations\n"+exc.getMessage());
	}
	// set selection to the last copied one
	getScrollPaneTable().getSelectionModel().setSelectionInterval(index, index);
}


/**
 * Comment
 */
private void customizeTable() {
	getScrollPaneTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	getScrollPaneTable().setDefaultRenderer(Object.class, new org.vcell.util.gui.DefaultTableCellRendererEnhanced());
}


/**
 * Comment
 */
private void deleteSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector v = new Vector();
	for (int i = 0; i < selections.length; i++){
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(getSimulationWorkspace().getSimulations()[selections[i]]);
		if (!simStatus.isRunning()){
			v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
		}
	}
	Simulation[] toDelete = (Simulation[])org.vcell.util.BeanUtils.getArray(v, Simulation.class);
	try {
		getSimulationWorkspace().deleteSimulations(toDelete);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		cbit.vcell.client.PopupGenerator.showErrorDialog(this, "Could not delete all simulations\n"+exc.getMessage());
	}
	// unset selection - may not be needed...
	getScrollPaneTable().clearSelection();
}


/**
 * Comment
 */
private void editSimulation() {
	// this should not be possible to call unless exactly one row is selected, but check anyway
	int[] selectedRows = getScrollPaneTable().getSelectedRows();
	if (selectedRows.length > 0) { // make sure something is selected...
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(getSimulationWorkspace().getSimulations()[selectedRows[0]]);
		if (!simStatus.isRunning()){
			getSimulationWorkspace().editSimulation(getSimulationWorkspace().getSimulations()[selectedRows[0]]); // just the first one if more than one selected...
		}
	}
}


/**
 * Return the ButtonPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonPanel() {
	if (ivjButtonPanel == null) {
		try {
			ivjButtonPanel = new javax.swing.JPanel();
			ivjButtonPanel.setName("ButtonPanel");
			ivjButtonPanel.setLayout(getButtonPanelFlowLayout());
			getButtonPanel().add(getNewButton(), getNewButton().getName());
			getButtonPanel().add(getEditButton(), getEditButton().getName());
			getButtonPanel().add(getCopyButton(), getCopyButton().getName());
			getButtonPanel().add(getDeleteButton(), getDeleteButton().getName());
			getButtonPanel().add(getRunButton(), getRunButton().getName());
			getButtonPanel().add(getStopButton(), getStopButton().getName());
			getButtonPanel().add(getResultsButton(), getResultsButton().getName());
			getButtonPanel().add(getStatusDetailsButton(), getStatusDetailsButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonPanel;
}

/**
 * Return the ButtonPanelFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getButtonPanelFlowLayout() {
	java.awt.FlowLayout ivjButtonPanelFlowLayout = null;
	try {
		/* Create part */
		ivjButtonPanelFlowLayout = new java.awt.FlowLayout();
		ivjButtonPanelFlowLayout.setVgap(5);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjButtonPanelFlowLayout;
}


/**
 * Return the cellEditor1 property value.
 * @return javax.swing.table.TableCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultCellEditor getcellEditor1() {
	// user code begin {1}
	// user code end
	return ivjcellEditor1;
}

/**
 * Return the Component1 property value.
 * @return java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Component getComponent1() {
	// user code begin {1}
	// user code end
	return ivjComponent1;
}


/**
 * Return the CopyButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCopyButton() {
	if (ivjCopyButton == null) {
		try {
			ivjCopyButton = new javax.swing.JButton();
			ivjCopyButton.setName("CopyButton");
			ivjCopyButton.setText("Copy");
			ivjCopyButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyButton;
}


/**
 * Return the DeleteButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteButton() {
	if (ivjDeleteButton == null) {
		try {
			ivjDeleteButton = new javax.swing.JButton();
			ivjDeleteButton.setName("DeleteButton");
			ivjDeleteButton.setText("Delete");
			ivjDeleteButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteButton;
}


/**
 * Return the EditButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getEditButton() {
	if (ivjEditButton == null) {
		try {
			ivjEditButton = new javax.swing.JButton();
			ivjEditButton.setName("EditButton");
			ivjEditButton.setText("Edit");
			ivjEditButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditButton;
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
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(453, 150));
			ivjJScrollPane1.setMinimumSize(new java.awt.Dimension(100, 100));
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
 * Return the NewButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNewButton() {
	if (ivjNewButton == null) {
		try {
			ivjNewButton = new javax.swing.JButton();
			ivjNewButton.setName("NewButton");
			ivjNewButton.setText("New");
			ivjNewButton.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewButton;
}


/**
 * Return the ResultsButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getResultsButton() {
	if (ivjResultsButton == null) {
		try {
			ivjResultsButton = new javax.swing.JButton();
			ivjResultsButton.setName("ResultsButton");
			ivjResultsButton.setText("Results");
			ivjResultsButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResultsButton;
}


/**
 * Return the RunButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRunButton() {
	if (ivjRunButton == null) {
		try {
			ivjRunButton = new javax.swing.JButton();
			ivjRunButton.setName("RunButton");
			ivjRunButton.setText("Run");
			ivjRunButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunButton;
}


/**
 * Return the ScrollPaneTable property value.
 * @return cbit.gui.JTableFixed
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JTableFixed getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new org.vcell.util.gui.JTableFixed();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjScrollPaneTable.setModel(new cbit.vcell.client.desktop.simulation.SimulationListTableModel());
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setPreferredScrollableViewportSize(new java.awt.Dimension(450, 100));
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(false);
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
 * Insert the method's description here.
 * Creation date: (6/8/2004 2:00:46 PM)
 * @return int[]
 */
public int[] getSelectedRows() {
	return getScrollPaneTable().getSelectedRows();
}


/**
 * Return the selectionModel1 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Return the SimulationListTableModel1 property value.
 * @return cbit.vcell.client.desktop.biomodel.SimulationListTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationListTableModel getSimulationListTableModel1() {
	if (ivjSimulationListTableModel1 == null) {
		try {
			ivjSimulationListTableModel1 = new cbit.vcell.client.desktop.simulation.SimulationListTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimulationListTableModel1;
}

/**
 * Return the SimulationSummaryPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.SimulationSummaryPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.SimulationSummaryPanel getSimulationSummaryPanel1() {
	if (ivjSimulationSummaryPanel1 == null) {
		try {
			ivjSimulationSummaryPanel1 = new cbit.vcell.solver.ode.gui.SimulationSummaryPanel();
			ivjSimulationSummaryPanel1.setName("SimulationSummaryPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimulationSummaryPanel1;
}


/**
 * Gets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @return The simulationWorkspace property value.
 * @see #setSimulationWorkspace
 */
public SimulationWorkspace getSimulationWorkspace() {
	return fieldSimulationWorkspace;
}


/**
 * Return the StatusDetailsButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStatusDetailsButton() {
	if (ivjStatusDetailsButton == null) {
		try {
			ivjStatusDetailsButton = new javax.swing.JButton();
			ivjStatusDetailsButton.setName("StatusDetailsButton");
			ivjStatusDetailsButton.setText("Status Details...");
			ivjStatusDetailsButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusDetailsButton;
}

/**
 * Return the StopButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStopButton() {
	if (ivjStopButton == null) {
		try {
			ivjStopButton = new javax.swing.JButton();
			ivjStopButton.setName("StopButton");
			ivjStopButton.setText("Stop");
			ivjStopButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopButton;
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
	getNewButton().addActionListener(ivjEventHandler);
	getEditButton().addActionListener(ivjEventHandler);
	getCopyButton().addActionListener(ivjEventHandler);
	getDeleteButton().addActionListener(ivjEventHandler);
	getRunButton().addActionListener(ivjEventHandler);
	getStopButton().addActionListener(ivjEventHandler);
	getResultsButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getSimulationListTableModel1().addTableModelListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getStatusDetailsButton().addActionListener(ivjEventHandler);
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
		setName("SimulationListPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(707, 559);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJScrollPane1.weightx = 1.0;
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsButtonPanel = new java.awt.GridBagConstraints();
		constraintsButtonPanel.gridx = 1; constraintsButtonPanel.gridy = 2;
		constraintsButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsButtonPanel.weightx = 1.0;
		add(getButtonPanel(), constraintsButtonPanel);

		java.awt.GridBagConstraints constraintsSimulationSummaryPanel1 = new java.awt.GridBagConstraints();
		constraintsSimulationSummaryPanel1.gridx = 1; constraintsSimulationSummaryPanel1.gridy = 3;
		constraintsSimulationSummaryPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSimulationSummaryPanel1.weightx = 1.0;
		constraintsSimulationSummaryPanel1.weighty = 1.0;
		add(getSimulationSummaryPanel1(), constraintsSimulationSummaryPanel1);
		initConnections();
		connEtoC1();
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
		JFrame frame = new javax.swing.JFrame();
		SimulationListPanel aSimulationListPanel;
		aSimulationListPanel = new SimulationListPanel();
		frame.setContentPane(aSimulationListPanel);
		frame.setSize(aSimulationListPanel.getSize());
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
 * Comment
 */
private void newSimulation() {
	try {
		int newSimIndex = getSimulationWorkspace().newSimulation();
		getScrollPaneTable().getSelectionModel().setSelectionInterval(newSimIndex, newSimIndex);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		cbit.vcell.client.PopupGenerator.showErrorDialog(this, "Could not create new simulation\n"+exc.getMessage());
	}
}


/**
 * Comment
 */
public void refreshButtonsAndSummary() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	refreshButtonsLax(selections);
	if (selections.length != 1) {
		getSimulationSummaryPanel1().setSimulation(null);
	} else {
		getSimulationSummaryPanel1().setSimulation(getSimulationWorkspace().getSimulations()[selections[0]]);
	}
}


/**
 * Comment
 */
private void refreshButtonsLax(int[] selections) {
	// newButton always available...
	getCopyButton().setEnabled(selections.length > 0);
	boolean bEditable = false;
	if (selections.length==1){
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(getSimulationWorkspace().getSimulations()[selections[0]]);
		if (!simStatus.isRunning()){
			bEditable = true;
		}
	}
	boolean bDeletable = false;
	boolean bRunnable = false;
	boolean bStoppable = false;
	boolean bHasData = false;
	boolean bStatusDetails = false;
	// we make'em true if at least one sim satisfies criterion (lax policy)
	for (int i = 0; i < selections.length; i++){
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(getSimulationWorkspace().getSimulations()[selections[i]]);
		bDeletable = bDeletable == false ? !simStatus.isRunning() : bDeletable;
		bRunnable = bRunnable == false ? simStatus.isRunnable() : bRunnable;
		bStoppable = bStoppable == false ? simStatus.isStoppable() : bStoppable;
		bHasData = bHasData == false ? simStatus.getHasData() : bHasData;
		bStatusDetails = !simStatus.isNeverRan() ? true : bStatusDetails;
	}
	getEditButton().setEnabled(bEditable);
	getDeleteButton().setEnabled(bDeletable);
	getRunButton().setEnabled(bRunnable);
	getStatusDetailsButton().setEnabled(bStatusDetails);
	getStopButton().setEnabled(bStoppable);
	getResultsButton().setEnabled(bHasData);
}


/**
 * Comment
 */
private void refreshSimListTable() {
	getScrollPaneTable().repaint();
}


/**
 * Comment
 */
private void runSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector v = new Vector();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] toRun = (Simulation[])org.vcell.util.BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().runSimulations(toRun);
}


/**
 * Comment
 */
public void scrollPaneTable_FocusLost(java.awt.event.FocusEvent focusEvent) {
	int row = getScrollPaneTable().getSelectedRow();
	int col = getScrollPaneTable().getSelectedColumn();
	TableCellEditor ce = getScrollPaneTable().getCellEditor(row, col);
	if (ce != null) {
		ce.stopCellEditing();
	}
}


/**
 * Set the cellEditor1 to a new value.
 * @param newValue javax.swing.table.TableCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setcellEditor1(javax.swing.DefaultCellEditor newValue) {
	if (ivjcellEditor1 != newValue) {
		try {
			ivjcellEditor1 = newValue;
			connEtoM2(ivjcellEditor1);
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
 * Set the Component1 to a new value.
 * @param newValue java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setComponent1(java.awt.Component newValue) {
	if (ivjComponent1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjComponent1 != null) {
				ivjComponent1.removeFocusListener(ivjEventHandler);
			}
			ivjComponent1 = newValue;

			/* Listen for events from the new object */
			if (ivjComponent1 != null) {
				ivjComponent1.addFocusListener(ivjEventHandler);
			}
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
 * Insert the method's description here.
 * Creation date: (6/8/2004 2:00:46 PM)
 * @return int[]
 */
public void setSelectedRows(int[] indices) {
	if (indices != null) {
		getScrollPaneTable().clearSelection();
		for (int i = 0; i < indices.length; i++){
			if (indices[i] < getScrollPaneTable().getRowCount()) {
				getselectionModel1().addSelectionInterval(indices[i], indices[i]);
			}	
		}
	}
}


/**
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
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
			connPtoP2SetSource();
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
 * Sets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @param simulationWorkspace The new value for the property.
 * @see #getSimulationWorkspace
 */
public void setSimulationWorkspace(SimulationWorkspace simulationWorkspace) {
	SimulationWorkspace oldValue = fieldSimulationWorkspace;
	fieldSimulationWorkspace = simulationWorkspace;
	firePropertyChange("simulationWorkspace", oldValue, simulationWorkspace);
}


/**
 * Comment
 */
private void showSimulationResults() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector v = new Vector();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] toShow = (Simulation[])org.vcell.util.BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().showSimulationResults(toShow);
}


/**
 * Comment
 */
public void showSimulationStatusDetails(java.awt.event.ActionEvent actionEvent) {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector v = new Vector();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] sims = (Simulation[])org.vcell.util.BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().showSimulationStatusDetails(sims);
	return;
}


/**
 * Comment
 */
private void stopSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector v = new Vector();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] toStop = (Simulation[])org.vcell.util.BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().stopSimulations(toStop);
}
}