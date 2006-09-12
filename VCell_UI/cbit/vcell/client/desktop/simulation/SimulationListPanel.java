package cbit.vcell.client.desktop.simulation;
import javax.swing.table.TableCellEditor;
import java.util.*;
import cbit.vcell.client.desktop.*;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.*;

import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 3:41:07 PM)
 * @author: Ion Moraru
 */
public class SimulationListPanel extends JPanel {
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
			ivjButtonPanel.setName("ButtonPanel");
			ivjButtonPanel.setLayout(getButtonPanelFlowLayout());
			getButtonPanel().add(getNewButton(), getNewButton().getName());
			getButtonPanel().add(getEditButton(), getEditButton().getName());
			getButtonPanel().add(getCopyButton(), getCopyButton().getName());
			getButtonPanel().add(getDeleteButton(), getDeleteButton().getName());
			getButtonPanel().add(getRunButton(), getRunButton().getName());
			getButtonPanel().add(getStopButton(), getStopButton().getName());
			getButtonPanel().add(getResultsButton(), getResultsButton().getName());
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
	// we make'em true if at least one sim satisfies criterion (lax policy)
	for (int i = 0; i < selections.length; i++){
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(getSimulationWorkspace().getSimulations()[selections[i]]);
		bDeletable = bDeletable == false ? !simStatus.isRunning() : bDeletable;
		bRunnable = bRunnable == false ? simStatus.isRunnable() : bRunnable;
		bStoppable = bStoppable == false ? simStatus.isStoppable() : bStoppable;
		bHasData = bHasData == false ? simStatus.getHasData() : bHasData;
	}
	getEditButton().setEnabled(bEditable);
	getDeleteButton().setEnabled(bDeletable);
	getRunButton().setEnabled(bRunnable);
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
	D0CB838494G88G88G4D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8FDC14D536ECD1DACB0525C5EA3B24E6E4D4D6D834E2110BC565163B71DA6A47AB5B5C276D33FE34CB6F715B5877BE7E18987E4132221EDBD45AE23AAC7E2948B5938143C2A598C6D08C0619EF1801E1E61879C606BF66BB774F395FB7B35FB045DB77777C7D0E5F5CF34FB9775CF34FBD675E7B5D8FE9533B712B728BB6C9D27EAA097F7BB4DF1296F7C8521DD243FC4445BA0DA512665FFBG9B25
	EF2CC5700C073A68870DA525522D0545D0CE02326381E3491B707BB5695011449370A3CCBB51D0D7B57BBB4B586D64B6E1BB2BE9F9FF4BDAF82E87C8869CF99639C87B4616B20E4F62F8047C82C94A26B483AD1BB92E814ADDG09G29AEC61F894FB534658B76D6F33A0EC989D24E031E02F5C40EC6A6C2351B71F670E73974477C22815256D42AA761198CF50B81C8678B25DB2B7643F3DFD3DF4FFEF337DD29BCED169D0ECA3343AEBB15CA0B6C6DD3DC6ECA2F3D5F67B0A9F617F36F5E96713B516ED51ACCCE
	59A1C9D1340D5F3494D319509FA82F75905C23E652EE9A1433GF2843E53CA70CD705B869065413EFE7E17FD3CAFEDA3F7CA4BFA962D5990262FF19E5647D40F5A57680F3A8B5F2263D25DCC6C1F87F565DEE3499E00B6GBFC09C407FD0FF78621B0F60D9532D543A7A7BDD4E9A377B204362B96232488E783DF7AF54F0DC33ECF6F940B3581847376C5051E7ACE0FCB77FE0BEE613045DB87E094DB7CBD937DFD930DA47A6D989A385F33D4C9669DE55A62C6D65E15BDE618D744F40360FCB5A3625E7F72DDE25
	53766647F62D4E63F2CA95356DA6050C751796B256D5701B7011C945702D94BFD903E73E7AB87599D2CE023AB29F7AC65BFA542531E806D43A73AADDBB44AD0CA99875B19DA67B02F57176905929BE5666DF04AE17D1FC59E9954FF411291F12329B6A92870DA54C3FEA8CE46E2E709BCB0A060CA51B8176G98G5CG51C368E3933F795B777431A613C7B96833BBAC32C7E23CB54DEF40D376AB9E53E127D7B1B94D325164311B3A9D32D762B4DFB5939BF487709D233EEF06B63E141D3207CED610EE6A179559
	63E5B4ED4D9B890DC7263586FB3F8C8D743BA5145B557C9ABC9DA62F526A361894D925E07536E6B2A627CDC610888260B7330B331558EB9674BF85E0313043B3D45E57328762455E3D0DAED71F4FEDA0BD11B68F93BB7F970D9DCB01BEF788ED5CEBA438AE28778F231C37DBDE554829331B097AA64F904E31A396886CD28C50A782E4GE483940F208FBD5F06BED4376EA6E9473FCC517521D87067729166BB55A341F369A4ED03685985F5D1G71G29A318931EB237E9E491BD56F31DB20675679A01ECDED7BC
	921EC60DA53981ED2C624FC2DEBFD6BCD94F6756D8EDB07BE79EA7E3399474498358BC0AE3505AD24EFC7A2049AB23979F7300834A96F5C4181EC3BD0D3CDD52A649DB9BA81E48112457813E81A079F99BEF173471AE4027G5B81BEA7638D309560B3517FA9D46E9E502983A09AA091A083A08FE04B99E3C9A30082B09DA08DE0A540266F0CA57BGACGB1GC9GD9GC5E7F16C7A2CD5797AF68B277F1B9A3D57F35D11EFABFFEE63FD513375273CDF6BF9DF430D49571A5F5FC4983FFFB6B05B7DCC57F644A7
	D7F11AE0BB3E26315FFA0D8D3536FBBD4826DA783344E2B3797D5E987AFD20793D717F013D344066545786E28F79B4AC3B6AE4376C34408FAF2FEBB6903B71BCE0F5891EB60AF7C35E2805C06D9578EF8D240FA6C571583BFD0A5CA2AB0A5D592356DBE9DBDEDF37DBCDB958D63F010C15EB907206207798C84CB53B1CCE19CEEE41426B959A73E5075C8FCA573AFCCE056107DB49DA39CF9E62B9673B5617A512B39CBE123F587AF32E0978035BE36F0710C1D220B4FA166403578DE59A9B6DG9CD9C7B74153F2
	96E3796087984B378C5FA025F63A8B7456A6F157DDD610F6162F294F2A311C6964B26A6B6E233907633DCC5707DFA67E2A37GAE24BC530A5E8A2927B90E267B1DFCAE3CA66DEE27768761541C3C1655BD63A376A00B70FA0BDDF1F990BF237795F842A2426DF202C6BBB1C6EFECA77A67C23F56C6C15C0B42987D021154E31E644139566560EDC2DDCC9431738C1A4B50D67B22181D496FBCDA7F30284F00724A10FA2F0C75F5BF02D87C23607AE7C5FD8C544D5254B3BD1FBCDECAEDB16013E1F9C30C75357017
	5F9EFFB5284E48FD0755BFF71C584A21D9CBF41CC75A4C6C41FDD5BA343794C0BA0F35FB60BC3EBF22F453EDC817G655473D43A2581F4CDF624134E07756879AADD7579DA3A22DE24EB833CDBC3979BC017AC686682BEDDC337A2004E5107F44551300F095674A3DACBD76EC03AAE407BB5F4F11CEE538504AEC35025C4337A0CE836064961F4B114EECE3F10F7814B237E8BD47ACA516E37CE244B6475597CD9FC21D63FB29752D5DC4856CB87AEE4F4B181F4CF893A248B19DE0B0368D8BF361FC63ACD93585A
	EB4784367EA2CF020F1EC068DE94F47BA6305CCF7AC1688D1C2EFAA2213BEA906916GFE85405A09CC4F8AF83EA368CCFE240B1E48741BBE1151A7F2BA26DF55905225727A4C092A1EEAFF7384DD76C426FF71C5FCFCAF52525DBD0CF4D1973175CE9C271B4B69D8FC7A43B692B369DE3E57F45A642F748EC25C5F3B77AB9A24589E7D2C211853E7DC04FB719B69BAEF6D4590BFG76DD0C7175176BF0AFBE39AFC14AFAF6676AF0678E060B199E7E0B55789A1BDD73B20BD1A7CF10B61321AE7DE20CCF36B66DFE
	20466BF5196DB4E872397DE63BB67EB25C66F692832DF60FD7B98C89520FF1607DF622AF592BF0B437470AD535527F61ECC38D5DFCAFFC6610569EA99B4E94140431C79637C31E469E87DC0655ECCF79A25D7712383E6F4730C685C87CB1416F12C93EA43E475AB36F4676E2072F133A26CC8D5BDE760F795EBBC6EDEF47F7392B78590F0C6DB5C0FDD48C1E7DECBF78012E3F5449D6134F21540AA444774225D6BCC3CA869959C24E7251G3F2BB48DAAFB770AD445F873EDEB7938D4DC82F38700651F8C1BB67F
	4481BE95E099G4BFBB65BAEF81AE8522AE40FA659E3F5F97AE5CB8536D6C957CD15B5343A1E7C5E06BE5074A149AFCEF9D09D5B0AEDC6E65F992F9D606DAE3EF906346F6C3785FAE7CD93B7588AF2AF61F63DD435EF62655D0529G2CFF0BC47F9CC0B36BD26C5F6ADE3D7EE5DD0A36FB7FD332D613414C818ACAF87EB0E4C7395940D3F1994A4DF76A496D3A8C659EBD4E56EC6EA1D52E178AE6F27F5A0BF29560C994F22BDC3A63A1648E0D10FA1893B2ACAC83A4B339CF8B393940D3950BF2CF3A75645AE2D1
	6E9B2B099DBCBEE7303ADC5FA9FDA8F794F812045C8B8774642E94F27FD4C45790B053756DF05801F225C9104FA7215CF99EBD396EC9A837391B783C576652B8D6336C05E9235A62CF8E1CF7514017B289FD5A52CFE21F9AF80E3BBCFDDE3749AC7335DEEC58FA6637B73D23755BE253853AFE9B73ECCFC14EA4662F1BAFD77DF653955D0595D7205FBE5A0FBADA00E67A65583FCD7B49132E55A5BC8B9CED47F62396A7C9E52FDD225BAEDDAB727672B46DAE0D6FAE4C0947F6A7B8516E0DD7407E7D8A344D7394
	EFF75A953B49E19F16AB36B1FD223B68F9158FC6329F50B4B6EB7D23460572D2C0D60E101760A2BABBA01C3760FA3B12762A56E6F276709842A39F8FA30DDAE296CD188EEBC6C83E7148D60F6C359D74A98A38D20D5352626BA71B97228D5365D29F6A32E3B26CD7A623AEAF7A761279A652CAA57D99DE91B581E1CCE3728D27F14C26036C8CA1FF7EE912BF941B5D4B685E99C43A35D30CA5F5D3F0ECF7B43C8F4F9ED991B13AE2DB2308258279F79CF7259A47BD71D77324365ECB430E7B6894B65E332628639E
	93773B02F8G26D32A50A99BE8AA2EB416307D5E93035A7DDE9760A782A48224DE097DEB10C9DD603E8977D4DF7B316FCC675F57E867C8726A98DD1DED574795E4DE49F4152ED2F50E3B363BB071DA74550B7D28F39350CC3C8A75F9C134494AEF8D917B76B33578D8358F21AF2482DF2E60CD9DA2EB99F51F0774DB07113EAA8E62E39C52478C13F795D6974C00CA7AFF234B2B84A740C324A2B07F5D58CC7227D96C9BBB8F896646305CFFD76363F221CFBC68129907BE347F9B02F7C29EB6B9D85CE3780F3A5F
	D0678804C631102213151D93C59B025FC514DE2D1EF9EE517CC6E0E7746F3611B62B2FE6E7742B85FEBB45B7E970CC77DB693A1614EDD03778EABCD7CEBFC5FC2DEAAA6CEDGACG23G5381D226627AF4CE7BEEA942397DE127824BD71323A560A0854F4FCF10F16B1307F859F2C29BCF199E1FF4129C1389ED66G2C1D866BEF003AE928433F1F0828C33DDFD787E66717AC444E16E9E31FA7EB01712D11895F688F60E3F3EE5B896DFB4A220DDF959406099309DCF64AB4F54EB11BBC55C5EC128778326930FE
	GE8G30CDC71BFCFCF2C7A41B60BB92EE8C7E4ECE9E1FADEE7E4136E0FCEFD847770EF04BB879D65B0279A6CE8F54F9D6D019713D9E42D799D4CE8F62E373FCC38B99AF1D3DA64B0B44E6B42EBF5A9BFC0EC75E1BAC8519B9GECCFDAE2AF667A2C3D866768475DE44FDB8765AEGBF40E400E4008C005CEB509F4EBB79F944B9C2C21A41D58B3886172B2FB6789CD3586221F15A707108B6AC7BC9A09F6B777EDE6CF755CF707D43D97B78F438A404AFD8871B2E8E55CE54A1CA68303BF7FCBA540470856B102C
	2BC30F8BF5D8A2F4D8BECE9DDE0C28C371CF75F4385C0DBA147F94F51856B7BE9DEE88618B5621CDD70785832803DB68F04CB1BE9DCE0570856B10202B43CA8F6A10AAF4A86C0F941374F5589811AFB7C887B677934DE46E17469BCBF6G5481F481C48344832445635CFF74A4ACD13F575CE7A78321B31F4F5FF176EFF9C43EE571FA7D5BA1137E65C1DD59351063G9A815CGB1GF32F457EED6DB89829FF6235BE74915F776884B89EA31C637325E9A1FC413E14FE2D9E5F172E7135579A42975C5ED25D76A6
	7B46575E0C903E6076F2F55BBBF1FAFCFEE30B48D7BAC30FEF4B60785A5B95112FE2061E1F4E3192BFBDGF59DG8AC0ACC0924092001C9968277E0EAFFE081F0EB9976F9BE79FB3A272ED1A29370E790377EDC9FBC65C633343A2066ED88E597D328557B1163600F54CCC360EE90327E3A62EE3DE751358C94EE823E76259FAB9BD8FCB01F286404A19383EF90F3EFBAA1AE5ACA9872886B0G7881A683A44FC25BBE94B98608097FFDE2C02CFFFC767DC9C43EA5337478BA0746B7A77BC3785E89AA2F50EDEF7B
	70785A7BAC04AF38BD69BABD3E6746595ECBA1FC15C16532207658BD29E9747DCD95545581B01C1F3E2B36C1B96ABA4C59DB691EF89614D3GB284FEA745133395F21E19AB70F79E575EA74A88EA7B7BGE9E7D698BE56461200B6463A27948E581C552FE373651727425F2348BC25FF0F6249D37A77A8161D5A4870A0C3FBBB8F6BD71E9A7B1EC54E297077AC76F70657333ED3DF4F2FBB75757C26F3ECBD5BBB436979E7EA4F478A08CFF8FCCE455EAFD3514C4E321F5C290188536CF33270C324FB0377599266
	0BCEBBB98F5BBC9BE3D7F667AEA9B4F63173AE94E210CD1EE3D62B88DCADEE590CFAAF6F8AEF23673B74ED145325EF23BFF60DED23825358B6FA600D8D145F6B155999D91DC9B1713A658A392394F2BF66A8E4BC894FA7775B56F01AFA45D5CB6C42773EFD78BE36FAB6660CC27AAE498D6509G7133F10E16066C7188A44FE6F3EEB1BC174E8E1CFF7C1EF3F7F8BB9E6A5637E3E3B7F9576BF579BCE6B958D6AD6C5D872DD3934DE1C56EA7DDE6B49D14DFB1F9FA6490DB1BA9BF2B8B476F688EB79E4C1E85F62D
	BDF70B98D39B327FA310436DD2FCBD444C6B83ED44645D91A0EF0F1077F9485A17C09517D1874FB6DDF93D3DDAF99F8BF90E1035AD81871791854F493A725E69534A7BC4483B4A2DA7AF014BC8016792DDF93F8A1077291037F0C0CF5EB2AEA3971EEB937464DD68504A2B9672DE706849AB67B2764033C1D75E5EFE0D3CA3BB056F5CE18E6F4B8B4C7A3E7CF3F3F8DFFE509C59179FB5077765A74C11FD799973583EDC6AAC61FDED32FADC7DCDBB396CCF6470FD7DDC566FEB2D9C3E2FDF49117B5AA2076F6B89
	B9F2DF4DB269CB375DE277701BE62C0FE9CE2DFF28F7613EF3E9711F897CC33ED73474D5081FF4DA6B973B847D4941G9B9EC19BA6D9435BF03ED55F0677DA435BB05D9A59063F340637E126B5328D1F360E6DAFFF9AC49BB4A92EA6E283162FFB6AC9BE52B913EDB1FBDC8E873985A4E15E195E06FB150E844CBBDB693B3949D0CE84C8CB403C73A6DD7BE7C1B9BBA1F4FDD7F4030AAB035FBBG58FB1949F46FF3608676DE06G43DFC3710D9ABCFBDF73E1AF5EAFE9033A149BF09D315D96FE8CF7597447305A
	96FE8C3F30C59E4386DB78B1BCE60BBC06EDB67DF9F03FDFBBF66ABC38F2C86B57FB843EFBB8E09E6CC47C1611G7CAE445FE6A0F7EC9D26A117CF112418B9442E8F083B3465D02E866882D06630BDAC7F8E0C4E9FAB6C5241E568A24CF6A37DAE46083C99C03F8220987095G8DB7623B4995074938E93CECA743AF58C0E43297825AFB32CF7611BB5F6A3D897E821D126C647B6D25DE6DFEFB6E0DCC579CF896A722DF186D6B3473042F3778DAA9648C36BCD17553BD0921FE4CFCF382DDFBB5A4B25F5CA070D7
	31B519864FFCF68D4513F29C54ED3A8977E4FF216FB4E2219CAFF0B1F65C27A58ADCA245AD0172B201BBC15F8B64C23978E6445D530B3C1B846EA90A2B06F223409968F9FE97145D82F71793F9C7856EF1A7369BAFF0673B10B7C960163910B7D5600A5CC2E7013B590D3C398237E1GF90BEEC15C5F861037C2609E2738FDD0EE94387D9E64ED9338EFBD48BBAAF045DEECB7D6606CDE641DAEF097F911B775960CEB5736210FE4GEE85C05EAD2ADF144EB516EC199B68A74C972E26F132FAAE7305C2011FC971
	8D9ABC73116D624EC39B54254CC51DAA69F9D1792D4612FD370AB12038A6A8DB844EB6027D19A8F0D70C92DCA214B3856EB96ACBABEFD55F03EF142676CA6277CC4D6F9D3D5A3D78BF9AD89B7567348DE65B978C18B3B65D867EF39B46331DDD64FCAD68CE62179E3B6520292711C6C71687BFEBAD667C5137E19E6C215FB324C0B983E065EDF89F537FA15E47CC1CBDC3DA72DB7D7B30CB8DE78A0AEEE7FED3F13B66CE49C75D052C4DF9C6EC53827553EFA738E70C5A7BE644D7BA0B5819902BFF7D38361E2C8F
	48B7C5EA7D178175C45F892B3475C381752BB47C2CEF8FED453E65BDB4D7F24C0B8E7B0D71B25E2762A45E37072F4EDF995774B23F47677CC48A3DF36BF5B9CE4B1ECA17C5E6872C7A319F57A6E58AFEC7FCA0C91C2352EFC27DD00E85C8C892F1166E1FD3211CA1F04FBBF03E64A661DC3F041E37B64C035C8760870885C8GC885481C07F457D0BA724DG799E200C5F63A7F7744B795DFAC63741665658240B4B9A4BDF9B026AE3F9B9B382DFAA2F778B9DAB835AA6F60F74BDDF701999FE8738A1825FBAC91D
	4F7C9C031E71A830604A19C73E0F3F1128A2D55FC1631F3DAC028E30973393D932139C9CD924D84A77049D47BB6A8EDCD38C7486367370005A4EE33D115B790F5E50F65675613C4B906D74B9835BF94123363344B9D6BBCCE6018BE52E00B21331CDD1E53C640AAC632FEE1411A7E4347BD4999F8EC41651B600B2CA6F443EE58F8476ED7434AA335593D966848F4A5CF2A76A15B4284A0872C616F11B97E5D45F0973B3196EBD9A216C96387B69FD7FE8A8A78A5C7B743B01D4A8E789DCD5AB4E6DEC01AB20FF6B
	61C0B22C9512F10E7CF410745DA14AB750B27996031F038FEAF393217BC764A4845646D781ED04BB379EAF30B12E77135FA61F62EA163D76E1FADD94ED7F2D9F675544E4740929FE6D9DC536BF38550F63188AF4649B30CCF82ECCE65F2E30FD40599124A93A8B62A2C0B5C09B007FAE14BDE714FC27697648D6596311ADECBFC0BF5B324B03EE17C7E9818539EE775375CB9C704E927CC53DEF733E54126F52E1EF667075BB3D0748FE1EA94C6C7A1FA177873F5FF82CEA9D9F5F7E9F4C4756031F51EF8B175C95
	3C7FBDC871D9F7856FFF7BDBF1AD33826A9AFF066F8A2F2263B3974A0B8116816481EC1E8FE3B0DF5C9534D7C9915E939ADC6EC658C4BBB4475F7C3BDD638F6D9FDBB35C3B9A578CC9BF1BA3A5DFD897767B144679ECFEB978B3F971557964497A7751B0695FF428CB8648824881A8CE011C1B027DE3F712480D8E0B1D3C71B419ED744BA071B282DCCCC5138DAC6B5B1F15317A36A7A55CBEF0B2DDEFB424041CDDD03CA1A5646C420CE35785F5E9A968537B7BC277632DFDB817AA9698CB8EG58GC69730F7F2
	698B884D2A7E3AB17446EF2A8BB9CC861E840057B9BEE5012A9F53A3F3BF9933237220381F47704F2DA767884428D84170352D6CFBD377D0A0FE61FB241FF5747B8FD64370CF8D90794DBEE7107CD525C4CE0B62F2874A4921E7E47C0B8941437C6A11135AFB2425D39D855817E0BB145E4D3FEF38DBDDE7B3996927346B59726D47F457333927068B1AB82F2211D1E75D33065F71E66B9D0FCFE6F26387353AD53FF21E6EB7478492383C947E0C796F2B737959CD9F61311A9CDE757D50DB2ED0DC1DBB94679A88
	457D44930AFB409B0AFB240D1C3B40C2DC311BCD8E961E8F1ABCCDAE075DBC4463726FE8EEA6FB031C3B51071FECA7735466725807DDCEA51493734D9FE98EA777B1775C033C1F1DA0F6D7F38249GC85FCC733B9FE8236FC13F7F32B5125FEBE1AE70255D237AFD66BD41FE7FD6A109C97DF6A76CB87AB579E7A7DDB3AC837AEC216BA343E4BD2C9EE331EF0AD91D4FCF6E5FEB3F51E017C4AAEBDA8D47BA1B6BDB8E1F286FBCDA5FE6682CBD5658FA64A84605AF06F16F3FB955D8E2C8453BA29306F1DD94130A
	FB1CF914B68D4AD9GB902763A91FCC73876DE5CAF7E6BC870FEF1D23EF63F37E1B4F83FC85AD26BEBC603770B017C5E51607DA27233F5E04E28A4EC7C9BFB39C43F31AB97DF945459A18C13BF8CD33167632A9D65BB2B2A3631F87C8579E4076755864DEFEF6B39BF0F08B927ED30BC956F8FFFD6FD3DDF7F2C7AA63F7EBB6B5B7DE35FAF186F8FFF3FE021DF7BF79CD81ECDBBC346348EFC288BA09AA08EA071DE4C31CDC3643BA0F623A8B83F56DBFA48D9B979ABC48A363FFBB8FCDF778EC77E5BC53574EC3B
	5B85413F1F48C77C61E1B2DE10B2A812610E8E939BF96CBDB60511F23C014EA307EC25E8369F5823609EA17DDEDC6FCFB52B3EB6D373FB07791FF08EF6CE5BE0F166CC9F2EBF4A969ACB1A9662B9586EDD81777459B958A107EB3011AFC978194B22BE31D7D80871686F747B45D4A8E782E4833CCBEDAEC904167D4B7EA29407B162363364B74B1BCF2F45FC3C794619D25D2F6B8ABE107473E6714FD93EEC7B391A0F5BA09FB39F1ECD25717BB94364EF4498CD3D3AB7CB3E026E9E6B6E154D0A4154A3664002FC
	32760A855909GCBGD6G6481ECCA43F930ED24A21CDCC0B243513960B1591D8A133B3400743F82E45489B92E116DE348E972385C100A0798167EE58B9ECF6FECA76FFF82F20D1A14AA0D754D064335B50D1DAD354D479A9B8F56B4F75634C06A292F2B2FE37C9BCFFCB286FF43316643A70E9DB568CB4031CBA4AE4B75595ADAAE0D915FF7C3FC7F38EA5B8F730DC75A555F3FBF2136D5BCB8C6DB10C6343924BE648EDDB0D852746E5E35C4648B3E7B4773C7C43EE0E0FB06EDA3E49EC525856FA5F6D1FCCCDA
	70DEE27EB04E65B8284BCB4398B6E7C45BFE57A2E30932E86CF38B66070B4F040F530F1E099C27334E503F45G7155B06446F754E7483E46E535FAE5118F7E6DCC29409D76B6393CF6F2424B56F5DFAB382E1B2823B37E5DB2DC57CD871A390B583A0E14539629B6E236B8BF647D5E770B45134649776018FCCBF47A444678969AC317AD8A9E7BE40A4FDE94BC76B58E9C7BBC286B3A8F4FG161EA476CB0372D200227BE17F8FD08550G508160860886188E1084108E1085108DA025C3FC8328C84758741FE749
	9ABFDCEC62AFCA54A0A7FC693E7C703E740B7C483E74D83EFE4E4F4A8F4D79CF676B677C65795A1C4F7FD6D0813139D8AB31F00A77B4499FFBD53FA9E76F9D005059436E6F253E360E56BB6CCE39167D55C4B16EFF9B11446FEA4D6FFB074F7D3A6109F35A86DBAB5DCB77967B40AF487A1F616E23B883473175412802FB90F77A77DD87E4FF4162FB4D3712E86FE93A4EC8G994969985766B9D43B5E2D797D25635C5B386B1C36416F241E8CBFE7DEBA99F94E94F5684F19D7BAC2674C068E7DB973FAC7681CF9
	3B030CE78F4C75B6C1772E40358B5C96AA13600E5BAD0ACD60BF22FA91FC03CC9AC4FC9555EB10625AB86EE38D2E9D77889DA407C017ED647BF7FEB727031C37D86D8E875FA3D09A3B93D20A30C78355516EB6E974BEA2F06D921E0D19BE547CAD892ABA43EF762FD37128BA77079F851C97AE869FDD1ABEF60EE56B165CBE2277699E131B1DAF2CF7607D2A1CF4FCCF912760DCF39562FC38C6413976E921D8EFB9C87CA7F7C6595F2D7E056595C0A4BEA72DD22442237220A29511F08D8FF2BE27C8AF3363B828
	EC76C128AEA227ED0A341A9F2EF9BD522B21EB291ACB9F7FF5E2E75D268229603074320D9A613034EA10D849CF5E69D9A08BAD845E8D0277B09D042527F76FADFCE6E6E101D4840C4CA98FCBC54433005F4364940286D4CA40BF6F00749DB57315A0FB91AD2D89CBC37E8E37C51FC6FCEFFED6FB9617FB01B96CD9DC7A03EC8C339E8C7E5E3C2107583F6CA1E3491E07B0B746D97FAA055FD734741B9C8E4564682BF1DA489E636010A22BFB72A3BDC43E97E9F8A55F2B76903DFBB5BC7F8BD0CB8788728EADDFC5
	9EGG74DCGGD0CB818294G94G88G88G4D0171B4728EADDFC59EGG74DCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFF9EGGGG
**end of data**/
}
}