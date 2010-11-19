package cbit.vcell.client.desktop.simulation;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.TableCellEditor;

import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.SubDomain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.solver.ode.gui.SimulationSummaryPanel;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 3:41:07 PM)
 * @author: Ion Moraru
 */
public class SimulationListPanel extends JPanel {
	private JPanel buttonsAndSimSummarypanel;
	private OutputFunctionsPanel outputFunctionsPanel;
	private JSplitPane outerSplitPane;
	private JScrollPane scrollPane;
	private JSplitPane innerSplitPane;
	private JPanel ivjButtonPanel = null;
	private JButton ivjEditButton = null;
	private JButton ivjNewButton = null;
	private JButton ivjResultsButton = null;
	private JButton ivjRunButton = null;
	private ScrollTable ivjScrollPaneTable = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationListTableModel ivjSimulationListTableModel1 = null;
	private SimulationWorkspace fieldSimulationWorkspace = null;
	private boolean ivjConnPtoP2Aligning = false;
	private ListSelectionModel ivjselectionModel1 = null;
	private SimulationSummaryPanel ivjSimulationSummaryPanel1 = null;
	private DefaultCellEditor ivjcellEditor1 = null;
	private java.awt.Component ivjComponent1 = null;
	private JButton moreActionsButton = null;
	private JPopupMenu popupMenuMoreAction = null;
	private JMenuItem menuItemCopy = new JMenuItem("Copy");
	private JMenuItem menuItemDelete = new JMenuItem("Delete");			
	private JMenuItem menuItemStop = new JMenuItem("Stop");
	private JMenuItem menuItemStatusDetails = new JMenuItem("Status Details...");

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, 
		java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener, javax.swing.event.TableModelListener, MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SimulationListPanel.this.getNewButton()) 
				connEtoC2(e);
			if (e.getSource() == SimulationListPanel.this.getEditButton()) 
				connEtoC3(e);
			if (e.getSource() == menuItemCopy) 
				connEtoC4(e);
			if (e.getSource() == menuItemDelete) 
				connEtoC5(e);
			if (e.getSource() == SimulationListPanel.this.getRunButton()) 
				connEtoC6(e);
			if (e.getSource() == SimulationListPanel.this.menuItemStop) 
				connEtoC7(e);
			if (e.getSource() == SimulationListPanel.this.getResultsButton()) 
				connEtoC8(e);
			if (e.getSource() == SimulationListPanel.this.menuItemStatusDetails) 
				connEtoC13(e);
			if (e.getSource() == moreActionsButton) {
				getPopupMenuMore().show(moreActionsButton, 0, moreActionsButton.getHeight());
			}
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == SimulationListPanel.this.getComponent1()) 
				connEtoC11(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimulationListPanel.this && (evt.getPropertyName().equals("simulationWorkspace"))) 
				onPropertyChange_SimulationWorkspace(evt);
			if (evt.getSource() == SimulationListPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM4(evt);
		};
		public void tableChanged(javax.swing.event.TableModelEvent e) {
			if (e.getSource() == SimulationListPanel.this.getSimulationListTableModel1()) 
				connEtoC10(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) 
				connEtoC9(e);
			
		}
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == getMoreActionsButton()) { 
				getPopupMenuMore().show(e.getComponent(), e.getX(), e.getY());
			}			
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
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
 * connEtoC10:  (SimulationListTableModel1.tableModel.tableChanged(javax.swing.event.TableModelEvent) --> SimulationListPanel.refreshButtonsAndSummary()V)
 * @param arg1 javax.swing.event.TableModelEvent
 */
private void connEtoC10(javax.swing.event.TableModelEvent arg1) {
	try {
		this.refreshButtonsAndSummary();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> SimulationListPanel.component1_FocusLost(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
private void connEtoC11(java.awt.event.FocusEvent arg1) {
	try {
		this.component1_FocusLost(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  ( (EditButton,action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel,editSimulation()V).normalResult --> SimulationListPanel.refreshButtonsAndSummary()V)
 */
private void connEtoC12() {
	try {
		this.refreshSimListTable();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC13:  (StatusDetailsButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.showStatusDetails(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		this.showSimulationStatusDetails(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC2:  (NewButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.newSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		this.newSimulation();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (EditButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.editSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		this.editSimulation();
		connEtoC12();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (CopyButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.copySimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		this.copySimulations();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (DeleteButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.deleteSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		this.deleteSimulations();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (RunButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.runSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		this.runSimulations();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC7:  (StopButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.stopSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		this.stopSimulations();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC8:  (ResultsButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationListPanel.showSimulationResults()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		this.showSimulationResults();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SimulationListPanel.refreshButtons()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void connEtoC9(javax.swing.event.ListSelectionEvent arg1) {
	try {
		this.refreshButtonsAndSummary();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (SimulationListPanel.simulationWorkspace --> SimulationListTableModel1.simulationWorkspace)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void onPropertyChange_SimulationWorkspace(java.beans.PropertyChangeEvent arg1) {
	try {
		getSimulationListTableModel1().setSimulationWorkspace(this.getSimulationWorkspace());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (cellEditor1.this --> Component1.this)
 * @param value javax.swing.table.TableCellEditor
 */
private void connEtoM2(javax.swing.DefaultCellEditor value) {
	try {
		if ((getcellEditor1() != null)) {
			setComponent1(getcellEditor1().getComponent());
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM4:  (ScrollPaneTable.cellEditor --> cellEditor1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void connEtoM4(java.beans.PropertyChangeEvent arg1) {
	try {
		setcellEditor1((javax.swing.DefaultCellEditor)getScrollPaneTable().getCellEditor());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable().setSelectionModel(getselectionModel1());
			}
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void copySimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] toCopy = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	int index = -1;
	try {
		index = getSimulationWorkspace().copySimulations(toCopy, this);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Could not copy all simulations\n"+exc.getMessage(), exc);
	}
	// set selection to the last copied one
	getScrollPaneTable().getSelectionModel().setSelectionInterval(index, index);
	getScrollPaneTable().scrollRectToVisible(getScrollPaneTable().getCellRect(index, 0, true));
}


/**
 * Comment
 */
private void deleteSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(getSimulationWorkspace().getSimulations()[selections[i]]);
		if (!simStatus.isRunning()){
			v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
		}
	}
	Simulation[] toDelete = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	try {
		getSimulationWorkspace().deleteSimulations(toDelete);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Could not delete all simulations\n"+exc.getMessage(), exc);
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
			getSimulationWorkspace().editSimulation(this, getSimulationWorkspace().getSimulations()[selectedRows[0]]); // just the first one if more than one selected...
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
			ivjButtonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			ivjButtonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
			ivjButtonPanel.setPreferredSize(new Dimension(750, 40));
			ivjButtonPanel.setName("ButtonPanel");
			FlowLayout fl = new java.awt.FlowLayout();
			fl.setVgap(5);
			ivjButtonPanel.setLayout(fl);
			getButtonPanel().add(getNewButton(), getNewButton().getName());
			getButtonPanel().add(getEditButton(), getEditButton().getName());
			getButtonPanel().add(getRunButton(), getRunButton().getName());
			getButtonPanel().add(getResultsButton(), getResultsButton().getName());
			getButtonPanel().add(getMoreActionsButton(), getMoreActionsButton().getName());
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
private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjScrollPaneTable.setModel(getSimulationListTableModel1());
		} catch (java.lang.Throwable ivjExc) {
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
			ivjSimulationListTableModel1 = new SimulationListTableModel(ivjScrollPaneTable);
		} catch (java.lang.Throwable ivjExc) {
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
private SimulationSummaryPanel getSimulationSummaryPanel1() {
	if (ivjSimulationSummaryPanel1 == null) {
		try {
			ivjSimulationSummaryPanel1 = new SimulationSummaryPanel();
			ivjSimulationSummaryPanel1.setName("SimulationSummaryPanel1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimulationSummaryPanel1;
}

private OutputFunctionsPanel getOutputFunctionsPanel() {
	if (outputFunctionsPanel == null) {
		try {
			outputFunctionsPanel = new OutputFunctionsPanel();
			outputFunctionsPanel.setName("ObservablesPanel");
			addPropertyChangeListener(ivjEventHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return outputFunctionsPanel;
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
	menuItemCopy.addActionListener(ivjEventHandler);
	menuItemDelete.addActionListener(ivjEventHandler);
	getRunButton().addActionListener(ivjEventHandler);
	menuItemStop.addActionListener(ivjEventHandler);
	getResultsButton().addActionListener(ivjEventHandler);
	getMoreActionsButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getSimulationListTableModel1().addTableModelListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	menuItemStatusDetails.addActionListener(ivjEventHandler);
	
	getOutputFunctionsPanel().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
//	simulationListTree.addTreeSelectionListener(ivjEventHandler);
//	simulationListTree.addTreeExpansionListener(simulationListTreeModel);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimulationListPanel");
		setSize(750, 560);
						
		setLayout(new BorderLayout());
		add(getInnerSplitPane(), BorderLayout.CENTER);	
		
		initConnections();
		getScrollPaneTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame("SimulationListPanel");
		SimulationListPanel aSimulationListPanel;
		aSimulationListPanel = new SimulationListPanel();
		frame.setContentPane(aSimulationListPanel);
		frame.setSize(aSimulationListPanel.getSize());
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
private void newSimulation() {
	AsynchClientTask task1 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getSimulationWorkspace().getSimulationOwner().refreshMathDescription();
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			int newSimIndex = getSimulationWorkspace().newSimulation();
			getScrollPaneTable().getSelectionModel().setSelectionInterval(newSimIndex, newSimIndex);
			getScrollPaneTable().scrollRectToVisible(getScrollPaneTable().getCellRect(newSimIndex, 0, true));
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
}


/**
 * Comment
 */
private void refreshButtonsAndSummary() {
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
	menuItemCopy.setEnabled(selections.length > 0);
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
	menuItemDelete.setEnabled(bDeletable);
	getRunButton().setEnabled(bRunnable);
	menuItemStatusDetails.setEnabled(bStatusDetails);
	menuItemStop.setEnabled(bStoppable);
	getResultsButton().setEnabled(bHasData);
}


/**
 * Comment
 */
private void refreshSimListTable() {
	getScrollPaneTable().repaint();
}

private boolean isSmoldynTimeStepOK(Simulation sim) {
	for (int jobIndex = 0; jobIndex < sim.getScanCount(); jobIndex ++) {
		SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(sim, jobIndex);
		double Dmax = 0;
		MathDescription mathDesc = sim.getMathDescription();
		
		Enumeration<SubDomain> subDomainEnumeration = mathDesc.getSubDomains();
		while (subDomainEnumeration.hasMoreElements()) {
			SubDomain subDomain = subDomainEnumeration.nextElement();
			
			if (!(subDomain instanceof CompartmentSubDomain)) {
				continue;
			}
			for (ParticleProperties particleProperties : subDomain.getParticleProperties()) {
				try {
					Expression newExp = new Expression(particleProperties.getDiffusion());
					newExp.bindExpression(simSymbolTable);
					newExp = simSymbolTable.substituteFunctions(newExp).flatten();
					try {
						double diffConstant = newExp.evaluateConstant();
						Dmax = Math.max(Dmax, diffConstant);
					} catch (ExpressionException ex) {
						throw new ExpressionException("diffusion coefficient for variable " 
								+ particleProperties.getVariable().getQualifiedName() 
								+ " is not a constant. Constants are required for all diffusion coefficients");
					}
				} catch (Exception ex) {
					
				}
			}
		}
		
		double s = sim.getMeshSpecification().getDx();
		double dt = sim.getSolverTaskDescription().getTimeStep().getDefaultTimeStep();
		if (dt >= s * s / (2 * Dmax)) {
			return false;
		}
	}
	return true;
}
/**
 * Comment
 */
private void runSimulations() {
	final ArrayList<Simulation> simList = new ArrayList<Simulation>();
	AsynchClientTask task1 = new AsynchClientTask("checking", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			int[] selections = getScrollPaneTable().getSelectedRows();
			int dimension = getSimulationWorkspace().getSimulationOwner().getGeometry().getDimension();
			
			for (int i = 0; i < selections.length; i++){
				Simulation sim = getSimulationWorkspace().getSimulations()[selections[i]];

				if (dimension > 0) {
					MeshSpecification meshSpecification = sim.getMeshSpecification();
					if (meshSpecification != null && !meshSpecification.isAspectRatioOK()) {
						String warningMessage =  "Simulation '" + sim.getName() + "' has non-uniform spatial step. " +
								"This might affect the accuracy of the solution.\n\n"
						+ "\u0394x=" + meshSpecification.getDx() + "\n" 
						+ "\u0394y=" + meshSpecification.getDy()
						+ (dimension < 3 ? "" : "\n\u0394z=" + meshSpecification.getDz())
						 + "\n\nDo you want to continue anyway?";
						String result = DialogUtils.showWarningDialog(SimulationListPanel.this, warningMessage, 
								new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
						if (result == null || !result.equals(UserMessage.OPTION_OK)) {
							throw UserCancelException.CANCEL_GENERIC;
						}
					}
					if (sim.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.Smoldyn)) {
						if (!isSmoldynTimeStepOK(sim)) {
							String warningMessage =  "<html>The time step for " + SolverDescription.Smoldyn.getDisplayLabel()
								+ " needs to satisfy stability constraint" 
								+ "<dl><dd><i>\t\u0394t &lt; s<sup>2</sup> / ( 2D<sub>max</sub> )</i></dd></dl>" 
								+ "Where <i>s</i> is spatial resolution and <i>D<sub>max</sub></i> is the diffusion " +
										"coefficient of the fastest diffusing species. </html>";
							DialogUtils.showErrorDialog(SimulationListPanel.this, warningMessage);
							throw UserCancelException.CANCEL_GENERIC;
						}
					}
	
					// check the number of regions if the simulation mesh is coarser.
					Geometry mathGeometry = sim.getMathDescription().getGeometry();
					ISize newSize = meshSpecification.getSamplingSize();
					ISize defaultSize = mathGeometry.getGeometrySpec().getDefaultSampledImageSize();
					int defaultTotalVolumeElements = mathGeometry.getGeometrySurfaceDescription().getVolumeSampleSize().getXYZ();
					int newTotalVolumeElements = meshSpecification.getSamplingSize().getXYZ();
					if (defaultTotalVolumeElements > newTotalVolumeElements) { // coarser
						Geometry resampledGeometry = (Geometry) BeanUtils.cloneSerializable(mathGeometry);
						GeometrySurfaceDescription geoSurfaceDesc = resampledGeometry.getGeometrySurfaceDescription();
						geoSurfaceDesc.setVolumeSampleSize(newSize);
						geoSurfaceDesc.updateAll();
						
						if (mathGeometry.getGeometrySurfaceDescription().getGeometricRegions() == null) {
							mathGeometry.getGeometrySurfaceDescription().updateAll();
						}
						int defaultNumGeometricRegions = mathGeometry.getGeometrySurfaceDescription().getGeometricRegions().length;
						int numGeometricRegions = geoSurfaceDesc.getGeometricRegions().length;
						if (numGeometricRegions != defaultNumGeometricRegions) {
							String warningMessage =  "The simulation mesh size (" + newSize.getX() 
							+ (dimension > 1 ? " x " + newSize.getY() : "") + (dimension > 2 ? " x " + newSize.getZ() : "") + ")" +
							" for '" + sim.getName() + "' results in different number of geometric regions [" + numGeometricRegions + "] than " +
									"the number of geometric regions [" + defaultNumGeometricRegions + "] resolved in the Geometry Viewer." +
									"\n\nThis can affect the accuracy of the solution. Finer simulation mesh is recommended."
									 + "\n\nDo you want to continue anyway?";
							String result = DialogUtils.showWarningDialog(SimulationListPanel.this, warningMessage, new String[] {"OK", "Cancel"}, "OK");
							if (result == null || result != "OK") {
								throw UserCancelException.CANCEL_GENERIC;
							}
						}
					} 
					
					if (mathGeometry.getGeometrySpec().hasImage()) { // if it's an image.
						if (defaultSize.getX() + 1 < newSize.getX() 
								|| defaultSize.getY() + 1 < newSize.getY()
								|| defaultSize.getZ() + 1 < newSize.getZ()) { // finer
							String warningMessage =  "The mesh size (" + newSize.getX() 
							+ (dimension > 1 ? " x " + newSize.getY() : "") + (dimension > 2 ? " x " + newSize.getZ() : "") + ")" +
							" for simulation '" + sim.getName() + "' is finer than the original image resolution (" 
									+ (defaultSize.getX() + 1) 
									+ (dimension > 1 ? " x " + (defaultSize.getY() + 1) : "") 
									+ (dimension > 2 ? " x " + (defaultSize.getZ() + 1): "") + ")" +
									".\n\nThis will not improve the accuracy of the solution and can take longer to run. Original resolution (" 
									+ (defaultSize.getX() + 1) + (dimension > 1 ? " x " + (defaultSize.getY() + 1) : "") 
									+ (dimension > 2 ? " x " + (defaultSize.getZ() + 1) : "") 
									+ ") or coarser mesh is recommended."  + "\n\nDo you want to continue anyway?";
							String result = DialogUtils.showWarningDialog(SimulationListPanel.this, warningMessage, new String[] {"OK", "Cancel"}, "OK");
							if (result == null || result != "OK") {
								throw UserCancelException.CANCEL_GENERIC;
							}
						}
					}
				}
				simList.add(sim);
			}
		}
	};
			
	AsynchClientTask task2 = new AsynchClientTask("running", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// run simulations
			Simulation[] toRun = simList.toArray(new Simulation[0]);
			getSimulationWorkspace().runSimulations(toRun);
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
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
private void setcellEditor1(javax.swing.DefaultCellEditor newValue) {
	if (ivjcellEditor1 != newValue) {
		try {
			ivjcellEditor1 = newValue;
			connEtoM2(ivjcellEditor1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] toShow = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().showSimulationResults(toShow);
}


/**
 * Comment
 */
public void showSimulationStatusDetails(java.awt.event.ActionEvent actionEvent) {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] sims = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().showSimulationStatusDetails(sims);
	return;
}


/**
 * Comment
 */
private void stopSimulations() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] toStop = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().stopSimulations(toStop);
}

	/**
	 * @return
	 */
	private JSplitPane getInnerSplitPane() {
		if (innerSplitPane == null) {
			innerSplitPane = new JSplitPane();
			innerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			innerSplitPane.setLeftComponent(getScrollPaneTable().getEnclosingScrollPane());
			innerSplitPane.setRightComponent(getButtonsAndSimSummarypanel());
			innerSplitPane.setDividerLocation(180);
		}
		return innerSplitPane;
	}

	/**
	 * @return
	 */
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getSimulationSummaryPanel1());
		}
		return scrollPane;
	}
	
	/**
	 * @return
	 */
	private JPanel getButtonsAndSimSummarypanel() {
		if (buttonsAndSimSummarypanel == null) {
			buttonsAndSimSummarypanel = new JPanel();
			buttonsAndSimSummarypanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			buttonsAndSimSummarypanel.add(getButtonPanel(), gridBagConstraints);
			
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			buttonsAndSimSummarypanel.add(getScrollPane(), gridBagConstraints);
		}
		return buttonsAndSimSummarypanel;
	}
	
	public void setScrollPaneTableCurrentRow(Simulation selection) {
		if (selection == null) {
			getScrollPaneTable().clearSelection();
			return;
		}
		int numRows = getScrollPaneTable().getRowCount();
		for(int i=0; i<numRows; i++) {
			String valueAt = (String)getScrollPaneTable().getValueAt(i, SimulationListTableModel.COLUMN_NAME);
			if(selection.getName().equals(valueAt)) {
				getScrollPaneTable().changeSelection(i, 0, false, false);
				return;
			}
		}
	}

	private JPopupMenu getPopupMenuMore() {
		if (popupMenuMoreAction == null) {
			popupMenuMoreAction = new JPopupMenu();
			popupMenuMoreAction.add(menuItemCopy);
			popupMenuMoreAction.add(menuItemDelete);
			popupMenuMoreAction.add(new JSeparator());
			popupMenuMoreAction.add(menuItemStop);
			popupMenuMoreAction.add(menuItemStatusDetails);
		}
		
		return popupMenuMoreAction;
	}
	
	private javax.swing.JButton getMoreActionsButton() {
		if (moreActionsButton == null) {
			try {
				moreActionsButton = new JButton("More Actions", new DownArrowIcon());
				moreActionsButton.setHorizontalTextPosition(SwingConstants.LEFT);
				moreActionsButton.setName("MoreActionsButton");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return moreActionsButton;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
