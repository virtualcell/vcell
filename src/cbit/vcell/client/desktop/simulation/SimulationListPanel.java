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
import javax.swing.table.TableCellEditor;

import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.BioModelEditor.BioModelEditorSelection;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
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
@SuppressWarnings("serial")
public class SimulationListPanel extends JPanel {
	private JPanel buttonsAndSimSummarypanel;
	private OutputFunctionsPanel outputFunctionsPanel;
	private JScrollPane scrollPane;
	private JSplitPane innerSplitPane;
	private JPanel ivjButtonPanel = null;
	private JButton ivjEditButton = null;
	private JButton ivjNewButton = null;
	private JButton ivjResultsButton = null;
	private JButton ivjRunButton = null;
	private JButton ivjDeleteButton = null;
	private ScrollTable ivjScrollPaneTable = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationListTableModel ivjSimulationListTableModel1 = null;
	private SimulationWorkspace fieldSimulationWorkspace = null;
	private SimulationSummaryPanel ivjSimulationSummaryPanel1 = null;
	private DefaultCellEditor ivjcellEditor1 = null;
	private java.awt.Component ivjComponent1 = null;
	private JButton moreActionsButton = null;
	private JPopupMenu popupMenuMoreAction = null;
	private JMenuItem menuItemCopy = new JMenuItem("Copy");
	private JMenuItem menuItemStop = new JMenuItem("Stop");
	private JMenuItem menuItemStatusDetails = new JMenuItem("Status Details...");
	private BioModelEditorSelection bioModelEditorSelection = null;
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, 
		java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener, MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getNewButton()) {
				newSimulation();
			} else if (e.getSource() == SimulationListPanel.this.getEditButton()) {
				editSimulation();
			} else if (e.getSource() == menuItemCopy) {
				copySimulations();
			} else if (e.getSource() == getDeleteButton()) { 
				deleteSimulations();
			} else if (e.getSource() == getRunButton()) {
				runSimulations();
			} else if (e.getSource() == menuItemStop) {
				stopSimulations();
			} else if (e.getSource() == getResultsButton()) {
				showSimulationResults();
			} else if (e.getSource() == menuItemStatusDetails) {
				showSimulationStatusDetails();
			} else if (e.getSource() == moreActionsButton) {
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
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) 
				tableSelectionChanged(e);
			
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
 * connEtoC9:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SimulationListPanel.refreshButtons()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void tableSelectionChanged(javax.swing.event.ListSelectionEvent arg1) {
	try {
		this.refreshButtonsAndSummary();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void setBioModelEditorSelection(BioModelEditorSelection newValue) {
	BioModelEditorSelection oldValue = bioModelEditorSelection;
	bioModelEditorSelection = newValue;
	firePropertyChange(BioModelEditor.PROPERTY_NAME_BIOMODEL_EDITOR_SELECTION, oldValue, newValue);
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
			getButtonPanel().add(getDeleteButton(), getDeleteButton().getName());
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
	getDeleteButton().addActionListener(ivjEventHandler);
	getRunButton().addActionListener(ivjEventHandler);
	menuItemStop.addActionListener(ivjEventHandler);
	getResultsButton().addActionListener(ivjEventHandler);
	getMoreActionsButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	menuItemStatusDetails.addActionListener(ivjEventHandler);
	
	getOutputFunctionsPanel().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
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
	Simulation newValue = null;
	if (selections.length == 1) {
		newValue = getSimulationWorkspace().getSimulations()[selections[0]];
	}
	getSimulationSummaryPanel1().setSimulation(newValue);	
	setBioModelEditorSelection(new BioModelEditorSelection(getSimulationWorkspace().getSimulationOwner(), newValue));	
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
	getDeleteButton().setEnabled(bDeletable);
	getRunButton().setEnabled(bRunnable);
	menuItemStatusDetails.setEnabled(bStatusDetails);
	menuItemStop.setEnabled(bStoppable);
	getResultsButton().setEnabled(bHasData);
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
				getScrollPaneTable().getSelectionModel().addSelectionInterval(indices[i], indices[i]);
			}	
		}
	}
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
private void showSimulationStatusDetails() {
	int[] selections = getScrollPaneTable().getSelectedRows();
	Vector<Simulation> v = new Vector<Simulation>();
	for (int i = 0; i < selections.length; i++){
		v.add(getSimulationWorkspace().getSimulations()[selections[i]]);
	}
	Simulation[] sims = (Simulation[])BeanUtils.getArray(v, Simulation.class);
	getSimulationWorkspace().showSimulationStatusDetails(sims);
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
	
	public void select(Simulation selection) {
		if (selection == null) {
			getScrollPaneTable().clearSelection();
			return;
		}
		int numRows = getScrollPaneTable().getRowCount();
		for(int i=0; i<numRows; i++) {
			Simulation simulation = getSimulationListTableModel1().getValueAt(i);
			if (simulation == selection) {
				getScrollPaneTable().setRowSelectionInterval(i, i);
				return;
			}
		}
	}

	private JPopupMenu getPopupMenuMore() {
		if (popupMenuMoreAction == null) {
			popupMenuMoreAction = new JPopupMenu();
			popupMenuMoreAction.add(menuItemCopy);
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
