package cbit.vcell.mapping.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable.CheckOption;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.desktop.biomodel.BioModelEditorSubPanel;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.ReactionStep;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:31:14 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ReactionSpecsPanel extends BioModelEditorSubPanel {
	public static final String PARAMETER_NAME_SELECTED_REACTION_STEP = "selectedReactionStep";
	private JSortTable ivjScrollPaneTable = null;
	private ReactionSpecsTableModel ivjReactionSpecsTableModel = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP2Aligning = false;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SimulationContext ivjsimulationContext1 = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private javax.swing.JMenuItem ivjJMenuItemCheckSelected = null;
	private javax.swing.JMenuItem ivjJMenuItemUncheckSelected = null;
	private JLabel fastLabel;
	private JLabel enabledLabel;
	private int selectedColumn = -1;
	private JPopupMenu ivjJPopupMenu;
	
class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener, java.awt.event.ActionListener, java.awt.event.MouseListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReactionSpecsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP2SetTarget();
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) {
				setSelectedObjectsFromTable(getScrollPaneTable(), getReactionSpecsTableModel());
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getJMenuItemCheckSelected()) 
				checkBooleanTableColumn(CheckOption.CheckSelected);
			if (e.getSource() == getJMenuItemUncheckSelected()) 
				checkBooleanTableColumn(CheckOption.UncheckSelected);			
		}
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == getScrollPaneTable()) {
				scrollPaneTable_MouseButton(e);
			}			
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		};
	};
	
public ReactionSpecsPanel() {
	super();
	initialize();
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ReactionSpecsPanel");
		setLayout(new BorderLayout());
		//setSize(456, 539);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getJPopupMenu() {
	if (ivjJPopupMenu == null) {
		try {
			ivjJPopupMenu = new javax.swing.JPopupMenu();
			ivjJPopupMenu.setName("JPopupMenu");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenu;
}

private javax.swing.JMenuItem getJMenuItemCheckSelected() {
	if (ivjJMenuItemCheckSelected == null) {
		try {
			ivjJMenuItemCheckSelected = new javax.swing.JMenuItem();
			ivjJMenuItemCheckSelected.setText(CheckOption.CheckSelected.getText());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCheckSelected;
}

private javax.swing.JMenuItem getJMenuItemUncheckSelected() {
	if (ivjJMenuItemUncheckSelected == null) {
		try {
			ivjJMenuItemUncheckSelected = new javax.swing.JMenuItem();
			ivjJMenuItemUncheckSelected.setText(CheckOption.UncheckSelected.getText());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemUncheckSelected;
}

private void scrollPaneTable_MouseButton(final java.awt.event.MouseEvent mouseEvent) {
	if (!SwingUtilities.isRightMouseButton(mouseEvent)) {
		return;
	}
	selectedColumn = -1;
	selectedColumn = getScrollPaneTable().columnAtPoint(mouseEvent.getPoint());
	if (selectedColumn != ReactionSpecsTableModel.COLUMN_ENABLED && selectedColumn != ReactionSpecsTableModel.COLUMN_FAST) {
		return;
	}
	boolean bRowSelected = getScrollPaneTable().getSelectedRow() != -1; 
	getJMenuItemCheckSelected().setEnabled(bRowSelected);
	getJMenuItemUncheckSelected().setEnabled(bRowSelected);
	if (selectedColumn == ReactionSpecsTableModel.COLUMN_ENABLED) {
		getJPopupMenu().removeAll();
		getJPopupMenu().add(getEnabledLabel());
		getJPopupMenu().add(new JSeparator());
		getJPopupMenu().add(getJMenuItemCheckSelected());
		getJPopupMenu().add(getJMenuItemUncheckSelected());
		getJPopupMenu().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
	} else if (selectedColumn == ReactionSpecsTableModel.COLUMN_FAST) {
		getJPopupMenu().removeAll();
		getJPopupMenu().add(getFastLabel());
		getJPopupMenu().add(new JSeparator());
		getJPopupMenu().add(getJMenuItemCheckSelected());
		getJPopupMenu().add(getJMenuItemUncheckSelected());
		getJPopupMenu().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
	}
}

public void checkBooleanTableColumn(CheckOption b) {
	boolean bCheck = CheckOption.CheckSelected.equals(b);
	int[] selectedRows = getScrollPaneTable().getSelectedRows();
	for (int r = 0; r < selectedRows.length; r ++) {
		ReactionSpec rs = getReactionSpecsTableModel().getValueAt(selectedRows[r]);
		try {
			if (selectedColumn == ReactionSpecsTableModel.COLUMN_ENABLED) {
				rs.setReactionMapping(bCheck ? ReactionSpec.INCLUDED : ReactionSpec.EXCLUDED);
			} else if (selectedColumn == ReactionSpecsTableModel.COLUMN_FAST) {
				rs.setReactionMapping(bCheck ? ReactionSpec.FAST : ReactionSpec.INCLUDED);
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, e.getMessage());
		}
	}
}

private JLabel getFastLabel() {
	if (fastLabel == null) {
		fastLabel = new JLabel(" Fast");
		fastLabel.setFont(fastLabel.getFont().deriveFont(Font.BOLD));
	}
	return fastLabel;
}
private JLabel getEnabledLabel() {
	if (enabledLabel == null) {
		enabledLabel = new JLabel(" Enabled");
		enabledLabel.setFont(enabledLabel.getFont().deriveFont(Font.BOLD));
	}
	return enabledLabel;
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

/*
 * Return the ReactionSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.ReactionSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionSpecsTableModel getReactionSpecsTableModel() {
	if (ivjReactionSpecsTableModel == null) {
		try {
			ivjReactionSpecsTableModel = new ReactionSpecsTableModel(getScrollPaneTable());
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
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
		} catch (java.lang.Throwable ivjExc) {
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
	getScrollPaneTable().addMouseListener(ivjEventHandler);
	getJMenuItemCheckSelected().addActionListener(ivjEventHandler);
	getJMenuItemUncheckSelected().addActionListener(ivjEventHandler);
	getScrollPaneTable().setDefaultRenderer(ReactionStep.class, new DefaultScrollTableCellRenderer() {
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

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReactionSpecsPanel aReactionSpecsPanel;
		aReactionSpecsPanel = new ReactionSpecsPanel();
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

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), getReactionSpecsTableModel());
}

}