package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.client.desktop.biomodel.SPPRPanel;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This type was created in VisualAge.
 */
public class DataSymbolsPanel extends javax.swing.JPanel {
//	private SpeciesContextSpecPanel ivjSpeciesContextSpecPanel = null;
	private DataSymbolsSpecPanel ivjDataSymbolsSpecPanel = null;
	private SimulationContext fieldSimulationContext = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private SPPRPanel spprPanel = null;
	private JPanel scrollPanel = null; // added in July, 2008. Used to accommodate the radio buttons and the ivjJScrollPane1. 
	private JSortTable ivjScrollPaneTable = null;
	private NewDataSymbolPanel ivjNewDataSymbolPanel = null;
	private DataSymbolsTableModel ivjDataSymbolsTableModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JSplitPane ivjJSplitPane1 = null;
	private javax.swing.JMenuItem ivjJMenuItemAdd = null;
	private javax.swing.JPopupMenu ivjJPopupMenuICP = null;
	private javax.swing.JMenuItem ivjJMenuItemDelete = null;

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DataSymbolsPanel.this.getJMenuItemAdd()) 
				addDataSymbol();
			if (e.getSource() == DataSymbolsPanel.this.getJMenuItemDelete()){
				int selectedIndex = getScrollPaneTable().getSelectionModel().getMaxSelectionIndex();
				DataSymbol dataSymbol = getDataSymbolsTableModel().getDataSymbol(selectedIndex);
				removeDataSymbol(dataSymbol);
			}
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == DataSymbolsPanel.this.getScrollPaneTable()) 
				connEtoC4(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == DataSymbolsPanel.this.getScrollPaneTable().getSelectionModel()) 
				handleListEvent(e);
		}
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DataSymbolsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
			{
				setSimulationContext((SimulationContext)evt.getNewValue());
			}
		};
	};

public DataSymbolsPanel(SPPRPanel aPanel) {
	super();
	spprPanel = aPanel;
	initialize();
}

private SPPRPanel getSPPRPanel() {
	return spprPanel;
}

/**
 * connEtoC4:  (ScrollPaneTable.mouse.mouseReleased(java.awt.event.MouseEvent) --> DataSymbolsPanel.scrollPaneTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC4(java.awt.event.MouseEvent arg1) {
	try {
		this.scrollPaneTable_MouseButton(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void addDataSymbol() {
	String name = null;
	try {
		getNewDataSymbolPanel().setSymbolName("");
		getNewDataSymbolPanel().setSymbolExpression("vcField(dataset1,var1,0.0,Volume)");
		int newSettings = org.vcell.util.gui.DialogUtils.showComponentOKCancelDialog(this, getNewDataSymbolPanel(), "New DataSymbol");
		if (newSettings == JOptionPane.OK_OPTION) {
			name = getNewDataSymbolPanel().getSymbolName();
			String expression = getNewDataSymbolPanel().getSymbolExpression();
			Expression exp = new Expression(expression);
			FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(null);
			DataSymbol ds = new FieldDataSymbol(name, getSimulationContext().getDataContext(), VCUnitDefinition.UNIT_TBD, new FieldFunctionArguments(functionInvocations[0]));
			getSimulationContext().getDataContext().addDataSymbol(ds);
		}
	} catch (java.lang.Throwable ivjExc) {
		DialogUtils.showErrorDialog(this, "Data symbol " + name + " already exists");
	}
}
public NewDataSymbolPanel getNewDataSymbolPanel() {
	if (ivjNewDataSymbolPanel == null) {
		try {
			ivjNewDataSymbolPanel = new NewDataSymbolPanel();
			ivjNewDataSymbolPanel.setName("NewDataSymbolPanel");
			ivjNewDataSymbolPanel.setLocation(328, 460);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNewDataSymbolPanel;
}

private void removeDataSymbol(DataSymbol dataSymbol) {
	try {
		getSimulationContext().getDataContext().removeDataSymbol(dataSymbol);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SpeciesContextSpecPanel.setSpeciesContextSpec(Lcbit.vcell.mapping.SpeciesContextSpec;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void handleListEvent(javax.swing.event.ListSelectionEvent arg1) {
	try {
		int row = getScrollPaneTable().getSelectionModel().getMinSelectionIndex();
		if (row < 0) {
			getDataSymbolsSpecPanel().setDataSymbol(null);
		} else {
			getDataSymbolsSpecPanel().setDataSymbol(getDataSymbolsTableModel().getDataSymbol(row));
//			System.out.println("Initial condition selection changed");
			if(getSPPRPanel() != null) {
				getSPPRPanel().setScrollPaneTreeCurrentRow(getDataSymbolsTableModel().getDataSymbol(row));
			}
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private javax.swing.JMenuItem getJMenuItemAdd() {
	if (ivjJMenuItemAdd == null) {
		try {
			ivjJMenuItemAdd = new javax.swing.JMenuItem();
			ivjJMenuItemAdd.setName("JMenuItemAdd");
			ivjJMenuItemAdd.setText("Add");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAdd;
}

private javax.swing.JMenuItem getJMenuItemDelete() {
	if (ivjJMenuItemDelete == null) {
		try {
			ivjJMenuItemDelete = new javax.swing.JMenuItem();
			ivjJMenuItemDelete.setName("JMenuItemDelete");
			ivjJMenuItemDelete.setText("Delete");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemDelete;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getJPopupMenuICP() {
	if (ivjJPopupMenuICP == null) {
		try {
			ivjJPopupMenuICP = new javax.swing.JPopupMenu();
			ivjJPopupMenuICP.setName("JPopupMenuICP");
			ivjJPopupMenuICP.setLabel("DataSymbols");
			ivjJPopupMenuICP.add(getJMenuItemAdd());
			ivjJPopupMenuICP.add(getJMenuItemDelete());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuICP;
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
			getJScrollPane1().setViewportView(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setDividerLocation(300);
			getJSplitPane1().add(getScrollPanel(), "top");
			getJSplitPane1().add(getDataSymbolsSpecPanel(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}

// added in july 2008, to accommodate the radio buttons and the scrolltablepane when it is stochastic application.
private JPanel getScrollPanel()
{
	if(scrollPanel == null)
	{
		scrollPanel = new JPanel(new BorderLayout());
		scrollPanel.add(getJScrollPane1(), BorderLayout.CENTER);
	}
	
	return scrollPanel;
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
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(true);
			ivjScrollPaneTable.setRowHeight(ivjScrollPaneTable.getRowHeight() + 2);
			getScrollPaneTable().setSelectionModel(getScrollPaneTable().getSelectionModel());
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
 * Return the SpeciesContextSpecPanel property value.
 * @return cbit.vcell.mapping.SpeciesContextSpecPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DataSymbolsSpecPanel getDataSymbolsSpecPanel() {
	if (ivjDataSymbolsSpecPanel == null) {
		try {
			ivjDataSymbolsSpecPanel = new DataSymbolsSpecPanel();
			ivjDataSymbolsSpecPanel.setName("DataSymbolsSpecPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDataSymbolsSpecPanel;
}

/**
 * Return the SpeciesContextSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DataSymbolsTableModel getDataSymbolsTableModel() {
	if (ivjDataSymbolsTableModel == null) {
		try {
			ivjDataSymbolsTableModel = new DataSymbolsTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDataSymbolsTableModel;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.mapping.InitialConditionPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addMouseListener(ivjEventHandler);
	getJMenuItemAdd().addActionListener(ivjEventHandler);
	getJMenuItemDelete().addActionListener(ivjEventHandler);
	getScrollPaneTable().setModel(getDataSymbolsTableModel());
	getScrollPaneTable().createDefaultColumnsFromModel();
	getScrollPaneTable().setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(getScrollPaneTable(), true));
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);

	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof Species) {
				setText(((Species)value).getCommonName());
			} else if (value instanceof SpeciesContext) {
				setText(((SpeciesContext)value).getName());
			} else if (value instanceof Structure) {
				setText(((Structure)value).getName());
			}
			return this;
		}
	};
	getScrollPaneTable().setDefaultRenderer(SpeciesContext.class, renderer);
	getScrollPaneTable().setDefaultRenderer(Structure.class, renderer);
	getScrollPaneTable().setDefaultRenderer(Species.class, renderer);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("DataSymbolsPanel");
		setLayout(new java.awt.GridBagLayout());
		//setSize(456, 539);

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJSplitPane1(), constraintsJSplitPane1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public void setScrollPaneTableCurrentRow(DataSymbol selection) {
	if (selection == null) {
		return;
	}
	int numRows = getScrollPaneTable().getRowCount();
	for(int i=0; i<numRows; i++) {
		String valueAt = (String)getScrollPaneTable().getValueAt(i, DataSymbolsTableModel.COLUMN_NAME);
		DataSymbol dataSymbol = getSimulationContext().getDataContext().getDataSymbol(valueAt);
		if(dataSymbol!=null && dataSymbol.equals(selection)) {
			getScrollPaneTable().changeSelection(i, 0, false, false);
			return;
		}
	}
}
/*
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DataSymbolsPanel aDataSymbolsPanel;
		aDataSymbolsPanel = new DataSymbolsPanel(null);
		frame.setContentPane(aDataSymbolsPanel);
		frame.setSize(aDataSymbolsPanel.getSize());
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
*/

/**
 * Comment
 */
private void scrollPaneTable_MouseButton(final java.awt.event.MouseEvent mouseEvent) {
	if (!getScrollPaneTable().hasFocus()) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
					getScrollPaneTable().addFocusListener(new FocusListener() {
						
						public void focusLost(FocusEvent e) {
						}
						
						public void focusGained(FocusEvent e) {
							getScrollPaneTable().removeFocusListener(this);
							Robot robot;
							try {
								robot = new Robot();
								robot.mousePress(InputEvent.BUTTON1_MASK);
								robot.mouseRelease(InputEvent.BUTTON1_MASK);
							} catch (AWTException ex) {
								ex.printStackTrace();
							}													
						}
					});
				}
				getScrollPaneTable().requestFocus();
			}
		});	
	}
	if(mouseEvent.isPopupTrigger()){		
		boolean bSomethingSelected = getScrollPaneTable().getSelectedRows() != null && getScrollPaneTable().getSelectedRows().length > 0;
		getJMenuItemAdd().setEnabled(true);
		getJMenuItemDelete().setEnabled(bSomethingSelected);
		getJPopupMenuICP().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
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
	getDataSymbolsTableModel().setSimulationContext(simulationContext);
}


}
