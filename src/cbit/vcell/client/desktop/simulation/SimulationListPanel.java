package cbit.vcell.client.desktop.simulation;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import javax.swing.table.TableCellEditor;
import java.util.*;
import cbit.vcell.client.desktop.*;
import cbit.vcell.solver.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 3:41:07 PM)
 * @author: Ion Moraru
 */
public class SimulationListPanel extends JPanel {
	private JPanel buttonsAndSimSummaryPanel;
	private JScrollPane scrollPane;
	private JPanel ivjButtonPanel = null;
	private java.awt.FlowLayout ivjButtonPanelFlowLayout = null;
	private JButton ivjCopyButton = null;
	private JButton ivjDeleteButton = null;
	private JButton ivjEditButton = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JButton ivjNewButton = null;
	private JButton ivjResultsButton = null;
	private JButton ivjRunButton = null;
	private cbit.gui.JTableFixed ivjScrollPaneTable = null;
	private JButton ivjStopButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationListTableModel ivjSimulationListTableModel1 = null;
	private SimulationWorkspace simulationWorkspace = null;
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
	final GridBagConstraints gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.fill = GridBagConstraints.BOTH;
	gridBagConstraints.weighty = 1;
	gridBagConstraints.weightx = 1;
	gridBagConstraints.gridy = 1;
	gridBagConstraints.gridx = 0;
	add(getScrollPane(), gridBagConstraints);
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
	Simulation[] toCopy = (Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
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
	getScrollPaneTable().setDefaultRenderer(Object.class, new cbit.gui.DefaultTableCellRendererEnhanced());
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
	Simulation[] toDelete = (Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
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
			ivjButtonPanel.setPreferredSize(new Dimension(750, 40));
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
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(750, 125));
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
private cbit.gui.JTableFixed getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new cbit.gui.JTableFixed();
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
			ivjSimulationSummaryPanel1.setPreferredSize(new Dimension(750, 390));
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
		setSize(750, 560);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.weighty = 0;
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.fill = GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		add(getJScrollPane1(), constraintsJScrollPane1);
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
	Simulation[] toRun = (Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
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
	Simulation[] toShow = (Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
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
	Simulation[] sims = (Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
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
	Simulation[] toStop = (Simulation[])cbit.util.BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().stopSimulations(toStop);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8DC5515D8B1CD5BD8311B56E8539AED2C5422CB5DD853ED5AAF0DE90D9AAB213126CD22EC0D15F6E31B36E93F349B5747B0A42079A19A955B2828349203C800A404108482C4B0848698E0788B8C4C039998E60619B77CA4A459F3FF4EFDEFE65EB0CAB75FAE5FF7F86F1EFB4E39671EFB6E393FEF243DEFAEC8CBB16C152414B4097F9DC9112465BD12B4EB6C160D08ABF9B7B1C3527C3D8530C7
	DAAB9961D983F9B93392B3B225B6A99D52DD105E921D1871AA3C3FA46D9C0C690617B06558A16F3786A01B369CCFB316331D26ED9F6C0067AE00DE4011E75C3B247C298F32B8BE1A6391D28C12B4AF17505C7041BE0E5B84B46900B38358F399FD94BC1982571416F03A2E6BE3254D8FFA8DBB099C0DCC04A44EEB5C4F1E5952DFD26276133216D2BD89CF992495GD279D469DB85A7884EB4BCF8223F4F21640F754BCEE7FE3F53A13B14FC1B6C9BD65C1EFC1FE3446F34AA8E372B3434D93C57B9FC0A496A121D
	12147B9EA9A33DA01D4A047A007CC594E7F013F21D709E8DB07FBD44FFFE14603710778309993987302E475FAD63F531DC38D7DADDB8E25C9D262E6587D89D6D87543AC60D2CB73CC65BA55ECC6C3F9072D6G2C83107292B332814A817EC67D615E667760D95127D43AC7C65C2E8A0F67145366BD6B3649CEF8AFAD059C0EEB127B5DDE1BA431B67F5187F9BA7ACC870CEF75A766E3B609AD42760FEB3ACBDA77EFDFB2EC5731493A588B86CB9E3305A7CF3589ABFBE7C13832E372827DB3306CB6C9DB36F167CF
	D353F44A4E7E4B23298B391C451A32376411369E23BE30865E895F8B245DA9BE1A624B5ED7714CD753A91E243BA0EF697B689B16DD28CB1D71D6A973608D3AF61837EC0EA1A71F69D0169F2C4BDDDEA25B1E4F4A5CAFF4F9086217EB70CC173954CFC9BA7B03448CE7C1E286732F6F34123E3B78E0E246EA000DG46C208BDG65G66C274B1497C414774B113552B1C72BB1CB659ABB15E6F18DF0127BC21F82D35AE1FE2F5754B2DD62F435A6714FD9227D9E6A6B6688B603B1F7AFEBF1471216C123D343302F4
	6B082C48DE9F23C9B06FA1B4DE19661A9DA3B294B06211D06E9AB38904CE2BCFE971582C0A2CD2307CA4B3E913B1EBABC88481704E6C724BB6E2AF856ABF87E03E30C3E78B11F7C676C23CA8AD2DF33B077D9EB3290934311058B907460EF8F8DFD108B67E0505608A0A92B3EA0AD04E97DADE544829F27493752D5EC96CE3D36E3D44EF00FE8AE0A140D200B5C568C33F31208FD56D3CD34AFB740B3ABEB4977CF9D391731D7462603E74D9DA865133G72CCG9E0018E29C93F639AD9AD9D3DCCE9757A7DCDFDF
	4A7356CCC3234612F7834AC8634FD41EBFDDBC59457B56F4E5B07B1BBAC8DB46C33DD682ECAC46B6F838A51B7974A92BCFC6AFEE7002834AB635C5181E0B3CA6DEAEA9130CDB3BA91E768D69E500C600D7F99FFB0D7B5F1BGC7G8EG9CA76D8D70F70023G6F8A9B2C22320D0792B372G4CGD3G8B81D682EC8658F7B8B1238A4089B097E0894086009DC792B30A813AG228162GD683ECBD02EDF853E8F90A3E7D425563D50D7E3BF89D106F6FDC6F7D3CCEFAB6BF426B370B57B9DC5B1C513CB7C6E8477F
	EBE036FCC6571644D753B8CD30DDDF5258F3174626E84B7D5C66EF06A977F5EEEF727E56B47A3D23F95F737F40DE53816BFB7735927B48E3B0BD2B12BD324B86AFBE1E77E0AB31A39FAF865C02A70162BDB03ED4C2C077897CCFDBC91D2D0A62F574799539D9D69407EBD04DFF1C16657377F95421894BFA2A15341DFB9C4697C17F6BD6921B7B5DAE17CC0300E061799B6958A0BB6591D03A526DF7A98C7F7BB6B2279E16A77958742736ED9299DB1CFEB24E31F9EA3C157807476B980150C206CAC9A9A1634631
	02AC0D0DF2GC7665BB5706CAA4118BF7E8E467C1C735F101676F88CFAF318F937DDE7D8D242E2EFC2099A7319CECFDB74F5FF060ED19C6FE33A7EF89B71DF3D09F2AA657932713510FCBA9652E941C15EB7DE120A5A297DC1B8B5A74FE5F9CF7A09BD48E43D5A66D05CDE445FBA74BCBCE13261F13BC02303984BBF53C9745F8475B29E0538F894E339231D6463F84A03F8255B494B043C6823444E2B68180736AABE4A6CCC5EB7537CE7C57ECAC8EF88493F550379E525902BCB037333C6B1BF9A7266EB72191E
	2D9D19549623FE9926C144D8E7043FF4F73C9814574AFD07654B9D44D6CE4D1C43FC0C1479328F57DFCB213CB5GDBF839A547D8590C6EE702EE8124979FD369568450DD2BA05D9612AED3698A4A34F495FE24B3815E2E211B9DC077C7C1978B78259A3A24G3AEF0CA11DF4BCB1A36B38269E4735F45D63C8E7813CC7C3974369F61CA0F4FBA610EE51F1163F72B81B6BEC64F451146EE9C1E7B94146DA4F8915BEDF147B65C924CB6079497CA91D546A3776B252E51FE4732A12130CAEBA006ED6896962CEB23D
	16875131FA58D310EEC7B91B23651433F99AF9927CD4B921ABB4A0DDF1B91B9F10FA907ABACED7D0C168D68B3A9500CF82B0D6B0BD33617906207BEAAA52CDD5B07D66D6B07A051C0E69B7E2C43A45BCBF21C255D32D6F51B424CB2EE07ACB27F87B1E52527D17201BB84566C4B11CAEB6006EAB5B10EEA560B7G98AB99DDD5A523E3F16C8F7BC9EC25FB83C356B16BC43EEF9C460752520FE8B0E3EB7E5B683C1D504F2944357D2AA23A160274BA004D159807D76D44357D5C61D8A9E96723296176B1F6D4F13B
	D729F1F85D3F0E2732D87613AED2268772222AB00EA5F5E8579795BE1F3B5FC103AB0F8157F5EA63B4434D6DA431F2406175A935B010CEE03C38291368CB56BE9C4D6D111CD6A97D93FBA5EA0867EB6B0B273576D8327B226105B076184B6D30C8E30F653F7AD3AADB2366F8F6C8EC4C588C79F955099936EA0237FA4838CAFC141557DF0465451C3FCD6A7D62CDE14B0B2DE6652C28566CA33DFF08177700A8CFBA8DBEF89A77128E1CFAC757DF2A64812B5F29D40A410A2F2DCF0E621E1487E4448AB9CFCF8578
	DD3EF5DCA9AD95C39A63BD615B415BA5897832BE847F25634897CD5AF14A8478890079GECFC3C4DD788CFAB9D5C725943A4FB875C5E9159160B25655379D5FE854D2EA66F7B5187CC7FA06310CB9ED75BB6F7FFAB336F2DAF15E33F3A6BD6296C72B9035E5E556C5DF6C37C07FC2D7721EADFE575FA43ECGD6BF0302754B290139CC8D56EFC523DE7D6656206D5EBEC266F4B218B9C0C18977B33EAD644682CF12103B2CC5CFEED6AD4A2DEFE3F3BB4F24AA57C785B339C3FE141B87BC36DA147B3BD65D7690F2
	A7AF10FC6893B2CCC083A4B339EF0C215CF860C996F277D97464E6FF04F2DF59CE6C60753B0255657AA68A394540637C8865FE582EA7F721107B2C114EB520276B5BE156B84ADD8CBC1B045C318EBD3939E7D0EED39F71F91F5D2DF12CA659875DC635C56984763BB260D34E20CF5FB2C1E21F9AF85A5C5EE11F475AAF73B96143E173195F5E790656EF532D57687A6D1CDF8D9A624EB0FFDDFDC6931741E7B7893F4D1CC49D775541BC2F8E6B377784F952B93D04FB0BD36DD8EE544AC5D256CB576A16CB4B2E63
	712FCEADB776976B8DCBFE0165FEF4996DEEBC1B18D1F696ED73AD0AF7389C0A436AF41C17F377B3FD22FB697E179FDAF28450B4B6EB7D43A821BC85E44589F94392515989613C9967657934D615F62BEB1047909E79F89829539233E842F44838C0469B2FBC6015FD76D3FEC581D72AF0591A7DA3E411C334E13ABCA4F459GFA6C2BC7DD8AD3CA09FEE2D849272F6195D187A00CE9CC7E75A958E6E610BDC7485F12C2468F456E70B13ACE8352AD871A2D755836F9B5EF43F3D0D6C40C4E5DDFA7E229C09E43F6
	D7CA305D63926F11ACC31F8F5B6E458DFC6F2CC1ED774CCD77EF4F81E0BABDABF40A851AA4G36AE1C5520DD97E61992B34A81ECG93A62C5F380764852E2FF06DB5BB956B4EF47EFD05360FAC5EBEC7D7E77B6D738CB1A63E8FEED2F56EFDF23DC1F992FD757ED454B93D9174EAC4FD720DD8A6CB379A09FDC718DA3C2D3EE4C4DF18G3EF8417B2C114CE55475A0527725A1FDB250E6B7A17D83E9646CE3408DBDA01F7E2FF37B14608170B449889C7F3E59C4464FFE313E6CB9AD186B42F29FD363E34EB65447
	823A44B4218F1DE8A4F8970C43D6A70BFB8C7FDE5FABEA9F0150A8A6D21A187BA3A1F38548131AB1665E522997F3CB1A51866FEF7F6766821BDC247F85466CE6452A78FDD532E2F5B8FDA1A651C821B6E17BE3F7041C4D54B52BFB403D1AF784F6E6D1DDCC775A1B591945F601EF2478B99ABC3353E3F4DECE528BA1AF5D0C7B6C653D242FB8A1BD9BE08140E200B5GDB4CB83FFE3B2BC80AF00ED16BD2E07AEDF5B687EC9861F9C297317930BC49536B3A3463815363AEABE92F3DAD30E6822881688508EAC19D
	7A3BA36AD0BD212B833373FB9EE26785AD536F2FEB017115D03E451F000F450C7D9D5AF3DB631E0F8C29E162DCB2173D25C50D994CA6ED7D44A6A52DB01682B8812281963622CD6E6C4E0BE493BCB36246E0F5FAE40636D87F09ED41782AC7E7F6E65ABCC33EAADFB05F7256C01D1302520C2FA904AFBFA8EDEC8B64E331E0F7B3E9AF1D35B29B57A5096FD73EDE9F3CDFC94E1176014C82G3626DEE9CA677A5434E19F3D53C6566CD1101E8F3088A081E0B3C026A5B12358027E70176E63917B8889BFE6F7A560
	EA5C6E614A607DDAE10BF4E5E6B6FCA504AF5806E6CBA09F2B77F30DD8EF27854FE1164CD00787A36A30D0D707BF37208ECB048E9E7F4CF4389442972C43E6DD9DF25BD0079D6D28C34E584CF4A88C618B56212ADDCF07E68B6A50A5F4D8B3C39DFE99D107393ABADCEAC79D62048EF346E72643CDA1FC41BAEC5055614E8E54E12B5021FCA2D2CC525721BEA2DFF1C7A09F6B7BB983246F57C11E9DA08AE0BE40D200F5GD2A7767DB367E00A7D317ABE1B070476FC5EFFA7E7D63F1C08FC591DFA75DBB8C86AD7
	82F9E6GA740EC00B80095G9BC57D3EF26ED4247A09EB8ED0C7BC575289F0CCDF536519795239903EE0DFB2F66971BDAF4D2C3C2C903E607276691637EA0665BD91313CE25D72EEC819195F5C96112FCE37BC2BE1E66539A372593A747C74ED877153A9481B87908F3092E093400EEE983F3B51CFB71DBB79C97CF45A3E181ABA33BA6E0948E76A561B477C4177D7C93B47BD68F740A4062E38CEBBA6E49B4EE3BEDB2C1D47583B59BCE6AEBCE33BF19E737D8692BB499E7352EEBC9B98BACB6F7DC1BA739C501D
	43794D0DC3443665106E82D0GE2G9681A4GECBE07362D6C099883C4477FB8B16061995AF7EDC43E9DBDFAFC730DB36B13F704703D91144E51AD4F1EB6337246C278024BAB53AD6F3DEDB3AB2FA404AFBFA8ED56ADEF658C4B7BF988DFF07D1CC165B11FDE53097301E848E7F757FEC04F4016C0BAC1603EDAC8F01BA13D2397678DE7683A3CG52B5G3D825FC671E4FF0A6CCFC48B7C7AA26D9DAFC2BF1D3F6981A927A08C9FAB63CF81E5CCF7F7AC9C3038211F477AECEDEF78BBAB26DE7DBBAB2D3D7AF7D6
	3A683EAD60C1067646A4665BFA273F53626C8DFF27655B5670FAFE4F2A2F67FDD6FDBD9F30CE2F67AAEBB8BDDF27767CA95D57747ADD0AE3C42622191D6589F21F8922DC135F45C2A0116E8BDE6BCBB8E6BDB6CC7694E3FBB1FE5A7B8A25507849768CD108D936FA9B8686C470EC76487D28F7CFFFF89B8D746B5B48592FEFA3EF7F74B6BA5FBF3D0D9EF8E5B7657779E4364FD8E5D52CBC6FE9055C8F8B390BD48F232E04FB3C97685EAE2129D65C1505220F375463197672DE9C373E5DC05AE3AB2477DA93B372
	2C58C7D719744601AAAB6BF396F8BA2D017D0F5FBD3705376377ED7AF6DCEEA36765BE375F5BAF875B6AA1DB1648D3873BDAC59EA1D5E6B42BA93FE2758E4AA136DECB79D9DEB87E8D36F06D416CF9D2515A33C84498E3131E0D2238DD6642B3B648C6CC5E9F8264958BF97F5D22A7EF891711G4F8D3A72EE77EB659D92725EEF531337054B58511718115D27A72FF3CCAB6F3010E7316849AB66B2EA605925ABEF7738D65E91A16F6AF6BDF99EAEA39A1E73F4655D97A02FC4483B3BC3CFDE9C1731941E2BF565
	0DCFE8641DBDA8FCA7FFA03CAF9F9A507765E38361FD7954C0E4DFBEB3905E171B86A27BF26740743EFCF67285DED7530057BDE2BA48E55FB5983E2E778C6A57753B03616B3AECB0F2DD9F9A8CDF57444148F5DDBBC86A526730B93C7CD69F2B6327AFEB7DC33DF7D8A1E971C785BEBD65F9ADFD8162FF1C22750BC2C1BF4F90E04333E843BCC7F89B96B974EDF854915E06650E48B62CF5043721499159066D0E697D25B495EDE0D25CA6E283B6DE8FD61371C8E7DF38395F6BF6BA49CDAA89471DE54538DECA6E
	93F7296A4938135D1F18D182E06A47F1673E8652076D101E6A8F1D5F45E8F08B61FDB1GBB9BFA1C2E3FD6763333A1828C7FB4452F516059191167AC5E5159GF9B5B61CC7FCEDA8FC9B5EB1245F0671C3615B705E2148ED38F4A8FC9B5EBF94398D9306747B015128EDBB359F7C4428756BE2013FB9AD209F9CC43CF5DBG3E9071295B8370C562AC25D3B78E5BF89C0667EA1BDE9CDED3C06EC7BB2D13EE3F826BA2193453EF45BD68E46011G178BD0A633F5B97F56EF1C54FF40EB9D819724133A028142FBCF
	A1724E877AF8008400CDC46EG1E15A65792BF50F86DC1067F5EEEA2139DF2E86FB8BFBECC6E6FEB4EC5592586CAF2106FA1BC5A255DC31898E03AAE01E742G7A595A611D5AFE4767EF4CFE9D754176CB9ED06D35F5A034DFB0DFBFEDA2F5499CE43E3EC7605BA83ECC03E7FDE0891D7B11349D72B68F629AAF191E53C45993B3E26C08BB5B086B3E7982B7C2F1CBA13DDA607E56C4F049105EA2F0D1CD48EBF4A0EE814565C23ACC60DEEEA5B89324FB856EFCAB72BA856E26D6ACB7C6602A5A10F721405D5606
	3C71827743B621334039AD481BACF08996645DACF01BAC481BB5A4F421FB79F910AE9338675A1137C6605E6CC0DE2740BD500165C6895CFB1D48BBC760BE6AC45E78A10C137797230FAC875C9AC2BB247A05B48C737961C0BF61E76B344DF30719AF248AFC834517E8707CEC3D9E6FF454C0DE5CB06A74F33A87166904B50393F19FD03FA90734D96076B6E3FDA6846E24196066C3FA05409DB9CBF06B1C6A597E9EE943D9C93CBF23F99FBB2BDD5B7FEF83AB633AABDA862B73E65A2E4691E82B9134C3FB975AAB
	DB60A6A92E8A5296G56AE2F9E44714B893805A3988B8F76127D4620BB269FFA9D36D35641BA9AD959185CF3B01D732F9B41B179DAAAF32F8B5A94204A0577EBA77E0177EB633EFE2B34BAC37FFEF3127922214B45FCAE4A251E774F9DD9EFE0E52EAD44B217C07EE69741E594E96F3290BF6BB132BDB216FFACA01F44AA720D141ABF9210CF747DF41AB67F66007CB48DBF2B5BC3FF473AEDFDA8DEF25E939D769B74B2B72BD3AFFF46EC38B1E55EF367D3793DCC57E1A9748E354F6D9C133D79EE1B4CB61C7547
	8D1CA76D6C466F4C673931CD9ADC24CD92A01D8CA0F950A7C669DABE8B52F982D7E2C25FB1F9B0CE5419326139G520B8192G128124D108E1G85GF523C85BCDE94977A20B79F79EF67EFDC6967F2E025C29E7344B9A22026CC36456F1193BF81992FFA7E5875BB3C18777F1A0EBD465E33ABC9522CB8677155D9A3F42EFC353F4E44EE4FF10404EA05FFDC9FA4EC46F3A41A451B9CAFEE7619DB2151662C7C97B589A3326114578239B09AE320BECF65924F4AF7D4E24917DE24B2838A35994D84EE9C5AD6706
	2648655C5B94DA4E0F1B30FF56F9311C3C5640F29CFE351C6BDB27AB07493C2F95E576C21A4938E5CC153124AD320C5F34218CC548D8B52E4AF85A92D946EB6DA8E336285B375A836B36F9C21579CAC7E419B59DA8F33E50AB6F3CAA2322B3328CDFA74A0877E2BFFE1C2E1716C3BAD9601EB9C4F0DBA11D63C3DC4DE102AB0134D960D60CE18C308BDCFC9F41AD06F402CF6C59B7103AEF9069961AA65F6A70B9FC3DF67CA3F4B3699F61006F51871491EE2FFD26405A78F78D645D6AD75CCD324FF11EDE93C65B
	BFEF42FEA5A9689327CD5A3B29EC8D52ED42F6AC81BA720D20891EB605FD5B44569A871A11E68A70B1G71G89G1B046CD6B3792E57631587E42FD73631B5877D2C4FA10FFB5CDE259994C43F20F324CCFFE246BEBF72571F7DAB2FCBA579FD83D8CFBA7DA3AE5FE93287213D637BC2D720DF4F3B207A2427ABF2EC08698A0D8D37F7211F567851C7DEBFB833BBEC87E648E77B44FCECEE5BD3C84650AEFF705E001D626D7E603D01FF91F3AB8572567871AC77827BFEE78C66E1G9E0059G31G4B4744DDCEE7
	0194619C576C765449E332D3F3B4406A17D478C96B4766B0BF580EF318C57FFE0734F8D6D5586F1F560C717B187C197B640DA964496A775AA5D23F5CF1187B81588122G6682440DE37D3E696477B24951CA0522D87B6D744BB3F1D0832E2C2249E21C556D456E696A36F4BC5C1A76BEBA7FD9BD9E5CF6ABA9BEE9BC386DDADC58F69BA14FB401FDE7F6FD685E424D624E62A220DB893099A0FD121DD71AA7697C4BD495213F60B9A101EC60A986F81963EBA6D57D789E478952E675723838BF49704F6CA2FBA944
	2818417015AD6C3BE74FE4A0FE59DB241ED5747BA2164370CF0C9279CDFED7107C34CCA227D9F1FB8265EC267B4D7C0B1CA01EBF57536F3D35B73FB985733BC7BA357700B3EFF29A302E41F61ABF495AB7F6D2DD97B099AB3A35736F6C838D3A736FAD5D678D6BB82FF4DE732D60E8F6AA53756EC69A733CFE1949DDB02E552D6479CF69FE9BCF20184B2B614F2DAF5F1442E4BE55C4F8862CCE1FFA36767356D0DC1AA594D7509E0A6B698845FD36B3947747E23247848B874551EFF532E1621455EBF2BB9D7D13
	BC9EE75279GD94BC41DC79FB79EA67D586E76BA4E3BDDCAA8A70EFB59F45EC06E53AE953CBD07095D5531090CC4C8BFC1690D97607D82760BA746A2758BAD14811F69025AAFEC97027B457DFDA4E60DB8DC30C29A510C03CF50390B876827AE202E0F1CA773FAF54B0EFD7B4E725EB2116FBF345F8831CBBE7995AD66061E266A665A0E6A1E7AEA0B392732212E65ECBD460D7FEC945F5BC0B9C997702ECF111893E7CD611A4C5AC4E8CD103683C4CDA1EDE3B31E2F4E1B42756DE71A0357375727E857275FED8E
	DE5F1232547CE71A035737017CEF1A035737484F467E7534B770397C59EC09FE6319ADBE5F28F2C018A6BFF414DBFC28A0AF7BE0C141FEC67B7DAE2DDF3298AE136DFF7E6E523C5FF8704A6F475CFDC54BE0E3DB74D4783D7F4FCD696F7DDFBF25FF87600629696FEB5CBC957E3E465727343FC1426F492710F6CEG3F5A8810F9915681GA597F1DCBEF911FC2B46EE09850F495536C1F276C0FEE9CB41725F3E943E2E7BAFC57EFD2E42CBE4BC69F343F0B0C264A33E6492E9AF98E6A812610EDFA2B672BA866D
	8AA365784ACB243FB865810AE6EB157D5D38FEB1DF44F96683AE555792B56F1D2EAB6FF74A95AD03FFB7D70FF31605D067F597F1AF2F28B0605B8B3617F75A699E2F6353983EEF343B8157B1FBAFE10C3A066E6756C15A86B08570A63539C4FFBF127DEDBA990A433851CA17C5ECAC7D0F9DB8C66F7B6657242A472A8C6FC8E13E673F4446508D1754B1FA8D0C514C07BFA2C3B4EF7B1FDCA23FF754EA9D523D297391D43721EFC86ED74C56C151872CE9E43E366FB278BDC0970082B09BE041E56C87D917F34349
	85A4B39C6D8BDE2B4325B039515BC87D97010C84A1675C65835348B1F95D9E981EA799167EAA8B0F316B8F1173340071C79D2872DB2B1B4C351595F5BD4D15CD8DF5F527AA1AFAAA1AE1B82A2E2A2EE27CC78E9F1E063F2621293623215E2CAF815B6E0F44F539BE5F9B4B16260979C590738F956C7FE43E1115213EDFE7D14BDAD4BFCDD9B034307105752B5FB5C6DA6BEC3A2CF73F713908FC41779D79BDCC5D3BBA5381DBE7BCCA477A9DD1416B0F47A9BEABAAF87DB15804FDB98772E6C7E18CEBED56163F91
	7052D5536F29703B97A9616374121448F17A07A97477C1A03E1AA7BD7C3C7841943296F28F8C78E4B19EBC1212A9F035BE135B67203F1FC867FA655DB8575B2B23B37EB69F4E75F201266CAAB657A3E953D52A0D785D6410734D0F970B574E10EFEF43F4FCDDBAF5E2ED7CE79AC3BDD785373D011E27CEDD955C761FEB40361F8DF99B2F42FD030B6D44FE26CFA5E6B881E2GE28116G2C86C886588A10F9B54CA3814A814CGF600A900B9G8BG96DD0D31694B29E45E9FAEB671439EB54889DF9ACA8B6FCB23
	E911FD69FC1A7E189F1596BA66DF1326BF66FFAECDBB66B35CE736911B0B39928B271CBEE69B1977085FB9605FA2FBA1B0327B10CBDF5AC9731D8E17DC49FE99D47517A6C93CEF503C0FB4DD79F9432CABDA86BFFF276B0D956097E4CD40F0DE0ACB62B8B6BF986D46F549562BBF6EBCE053C9965F5BBBA5D15E81BA4FA80ECE4C280A463876DC03EA57F4137AFEFD431537719DD7348CBE3FEF8F5FE76635C76EB337346B77195B5BC37BCCDC3BFE1F393BBD344F5CCB779786212FDB845D9201EB97389FD01984
	576630A9F6013F1F6AC570B5B2A990718FD33D46A94E02F771B438F60EFB2C1D0CA1D0E5BB79CD867E6DBBBDCF99F0B81D7C372BA80D4385C30A3047D3D4C707472A517B578257AE61FE1A759FBBA4516F296A8C3FEFE2270AC75571DEC0409E2385FC5499BD7D984B66AD2768594758205543769CAC627B1E28E8BCC339369B7B1ABB957B43973A312F9DC9C5D96E86927F499DDC765B6CCD175FGC45CB3D21AA225564B630AE4A4619A9EE4CFCF113631ADBC48EC72C328B6129DBAC55A4EB76494E98F5BFE4B
	667BEF797979BE2F74E2687C2A6273BFFBAC2E27EA2FC1B254CA5B6C54B035D25AB8315C847979E39B0CCC4B00F737602D258D13B4D6D41A7A6457D28D129198192356CAC662ED406FE5F2D2C1ABAAA5604FB7AADD22A163CBA07B07B475FCD89A9B81FD9A713B821735FBF6DB2E698F3BE7E71F455AF5EAD6706F8AA452794EE2402F1EA5FE63A165FDA97CDA23F9446AF4AAD667F0054BC6569D27A695D9DD27BFC67747FDC843B3D95E63F48FF3C84373BFD0CB878842BB970C819FGG6CDFGGD0CB818294
	G94G88G88GC4FBB0B642BB970C819FGG6CDFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGBB9FGGGG
**end of data**/
}
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(new Dimension(750, 450));
			scrollPane.setViewportView(getButtonsAndSimSummaryPanel());
		}
		return scrollPane;
	}
	/**
	 * @return
	 */
	protected JPanel getButtonsAndSimSummaryPanel() {
		if (buttonsAndSimSummaryPanel == null) {
			buttonsAndSimSummaryPanel = new JPanel();
			buttonsAndSimSummaryPanel.setPreferredSize(new Dimension(750, 400));
			buttonsAndSimSummaryPanel.setLayout(new BorderLayout());
			buttonsAndSimSummaryPanel.add(getButtonPanel(), BorderLayout.NORTH);
			buttonsAndSimSummaryPanel.add(getSimulationSummaryPanel1());
		}
		return buttonsAndSimSummaryPanel;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"