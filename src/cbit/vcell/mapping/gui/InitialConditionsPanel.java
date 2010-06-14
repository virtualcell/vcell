package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.SPPRPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;

/**
 * This type was created in VisualAge.
 */
public class InitialConditionsPanel extends javax.swing.JPanel {
	private SpeciesContextSpecPanel ivjSpeciesContextSpecPanel = null;
	private SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP3Aligning = false;
	private SimulationContext ivjsimulationContext1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private SPPRPanel spprPanel = null;
	private JPanel scrollPanel = null; // added in July, 2008. Used to accommodate the radio buttons and the ivjJScrollPane1. 
	private JRadioButton conRadioButton = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JRadioButton amtRadioButton = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JPanel radioButtonPanel = null; //added in July, 2008. Used to accomodate the two radio buttons
	private ButtonGroup radioGroup = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecsTableModel ivjSpeciesContextSpecsTableModel = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JSplitPane ivjJSplitPane1 = null;
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JPopupMenu ivjJPopupMenuICP = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getJMenuItemPaste()) 
				connEtoC5(e);
			if (e.getSource() == InitialConditionsPanel.this.getJMenuItemCopy()) 
				connEtoC6(e);
			if (e.getSource() == InitialConditionsPanel.this.getJMenuItemCopyAll()) 
				connEtoC7(e);
			if (e.getSource() == InitialConditionsPanel.this.getJMenuItemPasteAll()) 
				connEtoC8(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getScrollPaneTable()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == InitialConditionsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
			{
				connPtoP3SetTarget();
				updateTopScrollPanel();
			}
			
			if (evt.getSource() == InitialConditionsPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("selectionModel"))) {
				connPtoP5SetTarget();
			}
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getselectionModel1()) 
				connEtoM3(e);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public InitialConditionsPanel() {
	super();
	spprPanel = null;
	initialize();
}

public InitialConditionsPanel(SPPRPanel aPanel) {
	super();
	spprPanel = aPanel;
	initialize();
}

private SPPRPanel getSPPRPanel() {
	return spprPanel;
}

/**
 * connEtoC4:  (ScrollPaneTable.mouse.mouseReleased(java.awt.event.MouseEvent) --> InitialConditionsPanel.scrollPaneTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scrollPaneTable_MouseButton(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (JMenuItemPaste.action.actionPerformed(java.awt.event.ActionEvent) --> InitialConditionsPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPaste_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JMenuItemCopy.action.actionPerformed(java.awt.event.ActionEvent) --> InitialConditionsPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemCopy_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JMenuItemCopyAll.action.actionPerformed(java.awt.event.ActionEvent) --> InitialConditionsPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemCopy_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (JMenuItemPasteAll.action.actionPerformed(java.awt.event.ActionEvent) --> InitialConditionsPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPaste_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (simulationContext1.this --> SpeciesContextSpecsTableModel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecsTableModel().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SpeciesContextSpecPanel.setSpeciesContextSpec(Lcbit.vcell.mapping.SpeciesContextSpec;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void connEtoM3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		int row = getselectionModel1().getMinSelectionIndex();
		if (row < 0) {
			getSpeciesContextSpecPanel().setSpeciesContextSpec(null);
		} else {
			getSpeciesContextSpecPanel().setSpeciesContextSpec(getSpeciesContextSpecsTableModel().getSpeciesContextSpec(row));
//			System.out.println("Initial condition selection changed");
			if(getSPPRPanel() != null) {
				getSPPRPanel().setScrollPaneTreeCurrentRow(getSpeciesContextSpecsTableModel().getSpeciesContextSpec(row).getSpeciesContext());
			}
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetSource:  (InitialConditionsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getsimulationContext1() != null)) {
				this.setSimulationContext(getsimulationContext1());
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
 * connPtoP3SetTarget:  (InitialConditionsPanel.simulationContext <--> simulationContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setsimulationContext1(this.getSimulationContext());
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
 * connPtoP4SetTarget:  (ScrollPaneTable.model <--> model2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getSpeciesContextSpecsTableModel());
		getScrollPaneTable().createDefaultColumnsFromModel();
		getScrollPaneTable().setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(getScrollPaneTable(), true));
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP5SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setselectionModel1(getScrollPaneTable().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopy() {
	if (ivjJMenuItemCopy == null) {
		try {
			ivjJMenuItemCopy = new javax.swing.JMenuItem();
			ivjJMenuItemCopy.setName("JMenuItemCopy");
			ivjJMenuItemCopy.setText("Copy");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopy;
}


/**
 * Return the JMenuItemCopyAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopyAll() {
	if (ivjJMenuItemCopyAll == null) {
		try {
			ivjJMenuItemCopyAll = new javax.swing.JMenuItem();
			ivjJMenuItemCopyAll.setName("JMenuItemCopyAll");
			ivjJMenuItemCopyAll.setText("Copy All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopyAll;
}


/**
 * Return the JMenuItemPaste property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPaste() {
	if (ivjJMenuItemPaste == null) {
		try {
			ivjJMenuItemPaste = new javax.swing.JMenuItem();
			ivjJMenuItemPaste.setName("JMenuItemPaste");
			ivjJMenuItemPaste.setText("Paste");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPaste;
}


/**
 * Return the JMenuItemPasteAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPasteAll() {
	if (ivjJMenuItemPasteAll == null) {
		try {
			ivjJMenuItemPasteAll = new javax.swing.JMenuItem();
			ivjJMenuItemPasteAll.setName("JMenuItemPasteAll");
			ivjJMenuItemPasteAll.setText("Paste All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPasteAll;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getJPopupMenuICP() {
	if (ivjJPopupMenuICP == null) {
		try {
			ivjJPopupMenuICP = new javax.swing.JPopupMenu();
			ivjJPopupMenuICP.setName("JPopupMenuICP");
			ivjJPopupMenuICP.setLabel("Initial Conditions");
			ivjJPopupMenuICP.add(getJMenuItemCopy());
			ivjJPopupMenuICP.add(getJMenuItemCopyAll());
			ivjJPopupMenuICP.add(getJMenuItemPaste());
			ivjJPopupMenuICP.add(getJMenuItemPasteAll());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuICP;
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
			getJSplitPane1().add(getSpeciesContextSpecPanel(), "bottom");
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

//added in july 2008, to accommodate two radio buttons with flow layout.
private JPanel getRadioButtonPanel()
{
	if(radioButtonPanel == null)
	{
		JLabel label = new JLabel("INITIAL CONDITION: ");
		radioButtonPanel = new JPanel(new FlowLayout());
		radioButtonPanel.add(label);
		getButtonGroup();
		radioButtonPanel.add(getConcentrationRadioButton());
		radioButtonPanel.add(getAmountRadioButton());
	}
	return radioButtonPanel;
}
//following functions are added in July 2008. To enable selection of concentration or particles as initial condition
//for deterministic method the selection should be disabled (use concentration only). 
//for stochastic it should be enabled.
private JRadioButton getConcentrationRadioButton()
{
	if(conRadioButton == null)
	{
		conRadioButton = new JRadioButton("Concentration", true);
		conRadioButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) {
						boolean oldSelection = getSimulationContext().isUsingConcentration();
						if(oldSelection == false)//was using amount, then it's going to change.
						{
							if (!getSimulationContext().getGeometryContext().isAllSizeSpecifiedPositive()){
								getConcentrationRadioButton().setSelected(false);
								getAmountRadioButton().setSelected(true);
								DialogUtils.showErrorDialog(InitialConditionsPanel.this, "\nStructure sizes are required to convert number of particles to concentration.\nPlease go to StructureMapping tab to set valid sizes.");
								return;
							}
							//set to use concentration
							try {
								getSimulationContext().setUsingConcentration(true);
								getSimulationContext().convertSpeciesIniCondition(true);
								// force propertyChange(by setting old value to null), inform other listeners that simulation contect has changed.
								firePropertyChange("simulationContext", null, getSimulationContext());
							} catch (MappingException e1) {
								e1.printStackTrace();
								DialogUtils.showErrorDialog(InitialConditionsPanel.this, e1.getMessage());
								return;
							} catch (PropertyVetoException e1) {
								e1.printStackTrace();
								DialogUtils.showErrorDialog(InitialConditionsPanel.this, e1.getMessage());
								return;
							}
							
						}
					}
				}
		);
	}
	return conRadioButton;
}

private JRadioButton getAmountRadioButton()
{
	if(amtRadioButton == null)
	{
		amtRadioButton = new JRadioButton("Number of Particles");
		amtRadioButton.addActionListener(new ActionListener()
		    {
				public void actionPerformed(ActionEvent arg0) {
					boolean oldSelection = getSimulationContext().isUsingConcentration();
					if(oldSelection == true)//was using concentration, then it's going to change.
					{
						if (!getSimulationContext().getGeometryContext().isAllSizeSpecifiedPositive()){
							getConcentrationRadioButton().setSelected(true);
							getAmountRadioButton().setSelected(false);
							DialogUtils.showErrorDialog(InitialConditionsPanel.this, "\nStructure sizes are required to convert concentration to number of paticles.\nPlease go to StructureMapping tab to set valid sizes.");
							return;
						}
						//set to use number of particles
						try {
							getSimulationContext().setUsingConcentration(false);
							getSimulationContext().convertSpeciesIniCondition(false);
							// force propertyChange(by setting old value to null), inform other listeners that simulation context has changed.
							firePropertyChange("simulationContext", null, getSimulationContext());
						} catch (MappingException e1) {
							e1.printStackTrace();
							DialogUtils.showErrorDialog(InitialConditionsPanel.this, e1.getMessage());
							return;
						} catch (PropertyVetoException e1) {
							e1.printStackTrace();
							DialogUtils.showErrorDialog(InitialConditionsPanel.this, e1.getMessage());
							return;
						}
						
					}
				}
			}
				
		);
	}
	return amtRadioButton;
}

private ButtonGroup getButtonGroup()
{
	if(radioGroup == null)
	{
		radioGroup = new ButtonGroup();
		radioGroup.add(getConcentrationRadioButton());
		radioGroup.add(getAmountRadioButton());
	}
	return radioGroup;
}

private void updateTopScrollPanel()
{
	if(getSimulationContext().isStoch())
	{
		getScrollPanel().add(getRadioButtonPanel(),BorderLayout.NORTH);
		if(getSimulationContext().isUsingConcentration())
		{
			getConcentrationRadioButton().setSelected(true);
		}
		else
		{
			getAmountRadioButton().setSelected(true);
		}
	}
	else
	{
		getScrollPanel().remove(getRadioButtonPanel());
	}
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
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(true);
			ivjScrollPaneTable.setRowHeight(ivjScrollPaneTable.getRowHeight() + 2);
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
 * Return the SpeciesContextSpecPanel property value.
 * @return cbit.vcell.mapping.SpeciesContextSpecPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesContextSpecPanel getSpeciesContextSpecPanel() {
	if (ivjSpeciesContextSpecPanel == null) {
		try {
			ivjSpeciesContextSpecPanel = new SpeciesContextSpecPanel();
			ivjSpeciesContextSpecPanel.setName("SpeciesContextSpecPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecPanel;
}

/**
 * Return the SpeciesContextSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesContextSpecsTableModel getSpeciesContextSpecsTableModel() {
	if (ivjSpeciesContextSpecsTableModel == null) {
		try {
			ivjSpeciesContextSpecsTableModel = new SpeciesContextSpecsTableModel(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecsTableModel;
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
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addMouseListener(ivjEventHandler);
	getJMenuItemPaste().addActionListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	getJMenuItemPasteAll().addActionListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	
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
		// user code begin {1}
		// user code end
		setName("InitialConditionsPanel");
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
	// user code begin {2}
	// user code end
}

public void setScrollPaneTableCurrentRow(SpeciesContext selection) {
	if (selection == null) {
		getScrollPaneTable().clearSelection();
		return;
	}
	int numRows = getScrollPaneTable().getRowCount();
	for(int i=0; i<numRows; i++) {
		SpeciesContext valueAt = (SpeciesContext)getScrollPaneTable().getValueAt(i, SpeciesContextSpecsTableModel.COLUMN_SPECIESCONTEXT);
		if(valueAt.equals(selection)) {
			getScrollPaneTable().changeSelection(i, 0, false, false);
			return;
		}
	}
}

/**
 * Comment
 */
private void jMenuItemCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) throws Exception{
	
	if(actionEvent.getSource() == getJMenuItemCopy() || actionEvent.getSource() == getJMenuItemCopyAll()){
		
		try{
			//
			//Copy Symbols and Values Init Conditions
			//
			int[] rows = null;
				if(actionEvent.getSource() == getJMenuItemCopyAll()){
					rows = new int[getScrollPaneTable().getRowCount()];
					for(int i=0;i<rows.length;i+= 1){
						rows[i] = i;
					}
				}else{
					rows = getScrollPaneTable().getSelectedRows();
				}

			MathSymbolMapping msm = null;
			try {
				msm = (new MathMapping(getSimulationContext())).getMathSymbolMapping();
			}catch (Exception e){
				e.printStackTrace(System.out);
				DialogUtils.showWarningDialog(this, "current math not valid, some paste operations will be limited\n\nreason: "+e.getMessage());
			}
			StringBuffer sb = new StringBuffer();
			sb.append("initial Conditions Parameters for (BioModel)"+getSimulationContext().getBioModel().getName()+" (App)"+getSimulationContext().getName()+"\n");
			java.util.Vector<SymbolTableEntry> primarySymbolTableEntriesV = new java.util.Vector<SymbolTableEntry>();
			java.util.Vector<SymbolTableEntry> alternateSymbolTableEntriesV = new java.util.Vector<SymbolTableEntry>();
			java.util.Vector<Expression> resolvedValuesV = new java.util.Vector<Expression>();
			for(int i=0;i<rows.length;i+= 1){
				SpeciesContextSpec scs = getSpeciesContextSpecsTableModel().getSpeciesContextSpec(rows[i]);
				if(scs.isConstant()){
					primarySymbolTableEntriesV.add(scs.getInitialConditionParameter());//need to change
					if (msm!=null){
						alternateSymbolTableEntriesV.add(msm.getVariable(scs.getSpeciesContext()));
					}else{
						alternateSymbolTableEntriesV.add(null);
					}
					resolvedValuesV.add(new Expression(scs.getInitialConditionParameter().getExpression()));
					sb.append(scs.getSpeciesContext().getName()+"\t"+scs.getInitialConditionParameter().getName()+"\t"+scs.getInitialConditionParameter().getExpression().infix()+"\n");
				}else{
					for(int j=0;j<scs.getParameters().length;j+= 1){
						SpeciesContextSpec.SpeciesContextSpecParameter scsp = (SpeciesContextSpec.SpeciesContextSpecParameter)scs.getParameters()[j];
						if(VCellCopyPasteHelper.isSCSRoleForDimension(scsp.getRole(),getSimulationContext().getGeometry().getDimension())){
							Expression scspExpression = scsp.getExpression();
							sb.append(scs.getSpeciesContext().getName()+"\t"+scsp.getName()+"\t"+(scspExpression != null?scspExpression.infix():"")+"\n");
							if(scspExpression != null){// "Default" boundary conditions can't be copied
								primarySymbolTableEntriesV.add(scsp);
								if (msm != null){
									alternateSymbolTableEntriesV.add(msm.getVariable(scsp));
								}else{
									alternateSymbolTableEntriesV.add(null);
								}
								resolvedValuesV.add(new Expression(scspExpression));
							}
						}
					}
				}
			}
			//
			//Send to clipboard
			//
			VCellTransferable.ResolvedValuesSelection rvs =
				new VCellTransferable.ResolvedValuesSelection(
					(SymbolTableEntry[])BeanUtils.getArray(primarySymbolTableEntriesV,SymbolTableEntry.class),
					(SymbolTableEntry[])BeanUtils.getArray(alternateSymbolTableEntriesV,SymbolTableEntry.class),
					(Expression[])BeanUtils.getArray(resolvedValuesV,Expression.class),
					sb.toString());

			VCellTransferable.sendToClipboard(rvs);
		}catch(Throwable e){
			PopupGenerator.showErrorDialog(InitialConditionsPanel.this, "InitialConditionsPanel Copy failed.  "+e.getMessage(), e);
		}
	}
}


/**
 * Comment
 */
private void jMenuItemPaste_ActionPerformed(final java.awt.event.ActionEvent actionEvent) {
	
	final Vector<String> pasteDescriptionsV = new Vector<String>();
	final Vector<Expression> newExpressionsV = new Vector<Expression>();
	final Vector<SpeciesContextSpec.SpeciesContextSpecParameter> changedParametersV = new Vector<SpeciesContextSpec.SpeciesContextSpecParameter>();
	AsynchClientTask task1 = new AsynchClientTask("validating", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			
			if(actionEvent.getSource() == getJMenuItemPaste() || actionEvent.getSource() == getJMenuItemPasteAll()){
				Object pasteThis = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
				
				MathSymbolMapping msm = null;
				Exception mathMappingException = null;
				try {
					MathMapping mm = null;
					mm = new MathMapping(getSimulationContext());
					msm = mm.getMathSymbolMapping();
				}catch (Exception e){
					mathMappingException = e;
					e.printStackTrace(System.out);
				}
				
				int[] rows = null;
				if(actionEvent.getSource() == getJMenuItemPasteAll()){
					rows = new int[getScrollPaneTable().getRowCount()];
					for(int i=0;i<rows.length;i+= 1){
						rows[i] = i;
					}
				}else{
					rows = getScrollPaneTable().getSelectedRows();
				}
	
			
				//
				//Check paste
				//
				StringBuffer errors = null;
				for(int i=0;i<rows.length;i+= 1){
					SpeciesContextSpec scs = getSpeciesContextSpecsTableModel().getSpeciesContextSpec(rows[i]);
					try{
						if(pasteThis instanceof VCellTransferable.ResolvedValuesSelection){
							VCellTransferable.ResolvedValuesSelection rvs = (VCellTransferable.ResolvedValuesSelection)pasteThis;
							for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
								SpeciesContextSpec.SpeciesContextSpecParameter pasteDestination = null;
								SpeciesContextSpec.SpeciesContextSpecParameter clipboardBiologicalParameter = null;
								if(rvs.getPrimarySymbolTableEntries()[j] instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
									clipboardBiologicalParameter = (SpeciesContextSpec.SpeciesContextSpecParameter)rvs.getPrimarySymbolTableEntries()[j];
								}else if(rvs.getAlternateSymbolTableEntries() != null &&
										rvs.getAlternateSymbolTableEntries()[j] instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
									clipboardBiologicalParameter = (SpeciesContextSpec.SpeciesContextSpecParameter)rvs.getAlternateSymbolTableEntries()[j];
								}
								if(clipboardBiologicalParameter == null){
									Variable pastedMathVariable = null;
									if(rvs.getPrimarySymbolTableEntries()[j] instanceof Variable){
										pastedMathVariable = (Variable)rvs.getPrimarySymbolTableEntries()[j];
									}else if(rvs.getAlternateSymbolTableEntries() != null &&
											rvs.getAlternateSymbolTableEntries()[j] instanceof Variable){
										pastedMathVariable = (Variable)rvs.getAlternateSymbolTableEntries()[j];
									}
									if(pastedMathVariable != null){
										if (msm == null){
											throw mathMappingException;
										}
										Variable localMathVariable = msm.findVariableByName(pastedMathVariable.getName());
										if(localMathVariable == null){
											localMathVariable = msm.findVariableByName(pastedMathVariable.getName()+"_init");
										}
										if(localMathVariable != null){
											SymbolTableEntry[] localBiologicalSymbolArr =  msm.getBiologicalSymbol(localMathVariable);
											for(int k =0;k<localBiologicalSymbolArr.length;k+= 1){
												if(localBiologicalSymbolArr[k] instanceof SpeciesContext && scs.getSpeciesContext() == localBiologicalSymbolArr[k]){
													pasteDestination = scs.getInitialConditionParameter();//need to change
												}else if(localBiologicalSymbolArr[k] instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
													for(int l=0;l<scs.getParameters().length;l+= 1){
														if(scs.getParameters()[l] == localBiologicalSymbolArr[k]){
															pasteDestination = (SpeciesContextSpec.SpeciesContextSpecParameter)localBiologicalSymbolArr[k];
															break;
														}
													}
												}
												if(pasteDestination != null){
													break;
												}
											}
										}
									}
								}else{
									for(int k=0;k<scs.getParameters().length;k+= 1){
										SpeciesContextSpec.SpeciesContextSpecParameter scsp =
											(SpeciesContextSpec.SpeciesContextSpecParameter)scs.getParameters()[k];
										if(scsp.getRole() == clipboardBiologicalParameter.getRole() &&
											scs.getSpeciesContext().compareEqual(
											((SpeciesContextSpec)clipboardBiologicalParameter.getNameScope().getScopedSymbolTable()).getSpeciesContext())){
											pasteDestination = (SpeciesContextSpec.SpeciesContextSpecParameter)scsp;
										}
									}
								}
	
								if(pasteDestination != null){
									changedParametersV.add(pasteDestination);
									newExpressionsV.add(rvs.getExpressionValues()[j]);
									pasteDescriptionsV.add(
										VCellCopyPasteHelper.formatPasteList(
											scs.getSpeciesContext().getName(),
											pasteDestination.getName(),
											pasteDestination.getExpression().infix(),
											rvs.getExpressionValues()[j].infix())
									);
								}
							}
						}
					}catch(Throwable e){
						if(errors == null){errors = new StringBuffer();}
						errors.append(scs.getSpeciesContext().getName()+" ("+e.getClass().getName()+") "+e.getMessage()+"\n\n");
					}
				}
				if(errors != null){
					throw new Exception(errors.toString());
				}
	
			}
		}
	};
	
	AsynchClientTask task2 = new AsynchClientTask("pasting", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			//Do paste
			if(pasteDescriptionsV.size() > 0){
				String[] pasteDescriptionArr = new String[pasteDescriptionsV.size()];
				pasteDescriptionsV.copyInto(pasteDescriptionArr);
				SpeciesContextSpec.SpeciesContextSpecParameter[] changedParametersArr = 
					new SpeciesContextSpec.SpeciesContextSpecParameter[changedParametersV.size()];
				changedParametersV.copyInto(changedParametersArr);
				Expression[] newExpressionsArr = new Expression[newExpressionsV.size()];
				newExpressionsV.copyInto(newExpressionsArr);
				VCellCopyPasteHelper.chooseApplyPaste(InitialConditionsPanel.this, pasteDescriptionArr,changedParametersArr,newExpressionsArr);
			}else{
				PopupGenerator.showInfoDialog(InitialConditionsPanel.this, "No paste items match the destination (no changes made).");
			}
		}
	};
	
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		InitialConditionsPanel aInitialConditionsPanel;
		aInitialConditionsPanel = new InitialConditionsPanel();
		frame.setContentPane(aInitialConditionsPanel);
		frame.setSize(aInitialConditionsPanel.getSize());
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
		Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);

		boolean bPastable = obj instanceof VCellTransferable.ResolvedValuesSelection;
			
		boolean bSomethingSelected = getScrollPaneTable().getSelectedRows() != null && getScrollPaneTable().getSelectedRows().length > 0;
		getJMenuItemPaste().setEnabled(bPastable && bSomethingSelected);
		getJMenuItemPasteAll().setEnabled(bPastable);
		getJMenuItemCopy().setEnabled(bSomethingSelected);
		getJPopupMenuICP().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
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
			connPtoP5SetSource();
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
			connPtoP3SetSource();
			connEtoM2(ivjsimulationContext1);
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