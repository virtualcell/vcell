package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
import cbit.vcell.mapping.*; 
import java.beans.PropertyChangeListener;
/**
 * This type was created in VisualAge.
 */
public class InitialConditionsPanel extends javax.swing.JPanel {
	private SpeciesContextSpecPanel ivjSpeciesContextSpecPanel = null;
	private static final String ALL_SPECIES = "All Species";
	private static final String ALL_STRUCTURES = "All Structures";
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private boolean ivjConnPtoP3Aligning = false;
	private SimulationContext ivjsimulationContext1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private SpeciesContextSpecsTableModel ivjSpeciesContextSpecsTableModel = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JDialog ivjJDialog1 = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JTextArea ivjJTextArea1 = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOK = null;
	private javax.swing.JSplitPane ivjJSplitPane1 = null;
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JPopupMenu ivjJPopupMenuICP = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getJButtonCancel()) 
				connEtoM4(e);
			if (e.getSource() == InitialConditionsPanel.this.getJButtonOK()) 
				connEtoM5(e);
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
			if (e.getSource() == InitialConditionsPanel.this.getScrollPaneTable()) 
				connEtoC1(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getScrollPaneTable()) 
				connEtoC3(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getScrollPaneTable()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == InitialConditionsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == InitialConditionsPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP5SetTarget();
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
	initialize();
}


/**
 * connEtoC1:  (ScrollPaneTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> InitialConditionsPanel.showCustomEditor(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showCustomEditor(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  ( (JButtonOK,action.actionPerformed(java.awt.event.ActionEvent) --> ScrollPaneTable,setValueAt(Ljava.lang.Object;II)V).exceptionOccurred --> InitialConditionsPanel.connEtoM5_ExceptionOccurred(Ljava.lang.Throwable;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		this.connEtoM5_ExceptionOccurred(exception);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (ScrollPaneTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> InitialConditionsPanel.scrollPaneTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
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
 * connEtoM1:  ( (JButtonOK,action.actionPerformed(java.awt.event.ActionEvent) --> ScrollPaneTable,setValueAt(Ljava.lang.Object;II)V).normalResult --> JDialog1.dispose()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getJDialog1().dispose();
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
private void connEtoM2(cbit.vcell.mapping.SimulationContext value) {
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecPanel().setSpeciesContextSpec(this.getSelectedSpeciesContextSpec(getselectionModel1().getMinSelectionIndex()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> JDialog1.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJDialog1().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> ScrollPaneTable.setValueAt(Ljava.lang.Object;II)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable().setValueAt(getJTextArea1().getText(), getScrollPaneTable().getSelectedRow(), getScrollPaneTable().getSelectedColumn());
		connEtoM1();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoC2(ivjExc);
	}
}

/**
 * Comment
 */
private void connEtoM5_ExceptionOccurred(java.lang.Throwable arg1) {
	javax.swing.JOptionPane.showMessageDialog(getJDialog1(),arg1.getMessage(),"Error setting new expression",javax.swing.JOptionPane.ERROR_MESSAGE);
}


/**
 * connEtoM6:  (simulationContext1.this --> SpeciesContextSpecPanel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.mapping.SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		getSpeciesContextSpecPanel().setSimulationContext(getsimulationContext1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
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
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjJButtonCancel.setText("Cancel");
			ivjJButtonCancel.setMaximumSize(new java.awt.Dimension(75, 25));
			ivjJButtonCancel.setMinimumSize(new java.awt.Dimension(75, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}


/**
 * Return the JButtonOK property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOK() {
	if (ivjJButtonOK == null) {
		try {
			ivjJButtonOK = new javax.swing.JButton();
			ivjJButtonOK.setName("JButtonOK");
			ivjJButtonOK.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjJButtonOK.setText("OK");
			ivjJButtonOK.setMaximumSize(new java.awt.Dimension(75, 25));
			ivjJButtonOK.setMinimumSize(new java.awt.Dimension(75, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOK;
}


/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialog1() {
	if (ivjJDialog1 == null) {
		try {
			ivjJDialog1 = new javax.swing.JDialog();
			ivjJDialog1.setName("JDialog1");
			ivjJDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialog1.setBounds(484, 412, 485, 122);
			ivjJDialog1.setModal(true);
			ivjJDialog1.setTitle("Expression Editor");
			getJDialog1().setContentPane(getJDialogContentPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialog1;
}

/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 0;
			constraintsJScrollPane2.gridwidth = 2;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJScrollPane2(), constraintsJScrollPane2);

			java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
			constraintsJButtonOK.gridx = 0; constraintsJButtonOK.gridy = 1;
			constraintsJButtonOK.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJButtonOK.weightx = 1.0;
			constraintsJButtonOK.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJButtonOK(), constraintsJButtonOK);

			java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
			constraintsJButtonCancel.gridx = 1; constraintsJButtonCancel.gridy = 1;
			constraintsJButtonCancel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonCancel.weightx = 1.0;
			constraintsJButtonCancel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJButtonCancel(), constraintsJButtonCancel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
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
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			getJScrollPane2().setViewportView(getJTextArea1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
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
			getJSplitPane1().add(getJScrollPane1(), "top");
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


/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getJTextArea1() {
	if (ivjJTextArea1 == null) {
		try {
			cbit.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.BevelBorderBean();
			ivjLocalBorder.setColor(new java.awt.Color(160,160,255));
			ivjLocalBorder.setBevelType(1);
			ivjJTextArea1 = new javax.swing.JTextArea();
			ivjJTextArea1.setName("JTextArea1");
			ivjJTextArea1.setBorder(ivjLocalBorder);
			ivjJTextArea1.setBounds(0, 0, 233, 103);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextArea1;
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
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(true);
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
 * Comment
 */
public cbit.vcell.mapping.SpeciesContextSpec getSelectedSpeciesContextSpec(int index) {
	if (getSimulationContext()!=null && index >= 0){
		return getSimulationContext().getReactionContext().getSpeciesContextSpecs(index);
	}
	return null;
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
public cbit.vcell.mapping.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the simulationContext1 property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.SimulationContext getsimulationContext1() {
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
			ivjSpeciesContextSpecPanel = new cbit.vcell.mapping.gui.SpeciesContextSpecPanel();
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
			ivjSpeciesContextSpecsTableModel = new cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel();
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
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJButtonOK().addActionListener(ivjEventHandler);
	getJMenuItemPaste().addActionListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	getJMenuItemPasteAll().addActionListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
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
		setSize(456, 539);

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

			MathSymbolMapping msm = (new MathMapping(getSimulationContext())).getMathSymbolMapping();
			StringBuffer sb = new StringBuffer();
			sb.append("initial Conditions Parameters for (BioModel)"+getSimulationContext().getBioModel().getName()+" (App)"+getSimulationContext().getName()+"\n");
			java.util.Vector primarySymbolTableEntriesV = new java.util.Vector();
			java.util.Vector alternateSymbolTableEntriesV = new java.util.Vector();
			java.util.Vector resolvedValuesV = new java.util.Vector();
			for(int i=0;i<rows.length;i+= 1){
				SpeciesContextSpec scs = getSpeciesContextSpecsTableModel().getSimulationContext().getReactionContext().getSpeciesContextSpecs(rows[i]);
				if(scs.isConstant()){
					primarySymbolTableEntriesV.add(scs.getInitialConditionParameter());
					alternateSymbolTableEntriesV.add(msm.getVariable(scs.getSpeciesContext()));
					resolvedValuesV.add(new cbit.vcell.parser.Expression(scs.getInitialConditionParameter().getExpression()));
					sb.append(scs.getSpeciesContext().getName()+"\t"+scs.getInitialConditionParameter().getName()+"\t"+scs.getInitialConditionParameter().getExpression().infix()+"\n");
				}else{
					for(int j=0;j<scs.getParameters().length;j+= 1){
						SpeciesContextSpec.SpeciesContextSpecParameter scsp = (SpeciesContextSpec.SpeciesContextSpecParameter)scs.getParameters()[j];
						if(cbit.vcell.desktop.VCellCopyPasteHelper.isSCSRoleForDimension(scsp.getRole(),getSimulationContext().getGeometry().getDimension())){
							cbit.vcell.parser.Expression scspExpression = scsp.getExpression();
							sb.append(scs.getSpeciesContext().getName()+"\t"+scsp.getName()+"\t"+(scspExpression != null?scspExpression.infix():"")+"\n");
							if(scspExpression != null){// "Default" boundary conditions can't be copied
								primarySymbolTableEntriesV.add(scsp);
								alternateSymbolTableEntriesV.add(msm.getVariable(scsp));
								resolvedValuesV.add(new cbit.vcell.parser.Expression(scspExpression));
							}
						}
					}
				}
			}
			//
			//Send to clipboard
			//
			cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
				new cbit.gui.SimpleTransferable.ResolvedValuesSelection(
					(cbit.vcell.parser.SymbolTableEntry[])cbit.util.BeanUtils.getArray(primarySymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					(cbit.vcell.parser.SymbolTableEntry[])cbit.util.BeanUtils.getArray(alternateSymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					(cbit.vcell.parser.Expression[])cbit.util.BeanUtils.getArray(resolvedValuesV,cbit.vcell.parser.Expression.class),
					sb.toString());

			cbit.vcell.desktop.VCellTransferable.sendToClipboard(rvs);
		}catch(Throwable e){
			cbit.vcell.client.PopupGenerator.showErrorDialog("InitialConditionsPanel Copy failed.  "+e.getMessage());
		}
	}
}


/**
 * Comment
 */
private void jMenuItemPaste_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	java.util.Vector pasteDescriptionsV = new java.util.Vector();
	java.util.Vector newExpressionsV = new java.util.Vector();
	java.util.Vector changedParametersV = new java.util.Vector();
	try{
		if(actionEvent.getSource() == getJMenuItemPaste() || actionEvent.getSource() == getJMenuItemPasteAll()){
			Object pasteThis = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
			
			MathMapping mm = null;
			MathSymbolMapping msm = null;
			mm = new MathMapping(getSimulationContext());
			msm = mm.getMathSymbolMapping();
			
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
				SpeciesContextSpec scs = getSpeciesContextSpecsTableModel().getSimulationContext().getReactionContext().getSpeciesContextSpecs(rows[i]);
				try{
					if(pasteThis instanceof cbit.gui.SimpleTransferable.ResolvedValuesSelection){
						cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
							(cbit.gui.SimpleTransferable.ResolvedValuesSelection)pasteThis;
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
								cbit.vcell.math.Variable pastedMathVariable = null;
								if(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.math.Variable){
									pastedMathVariable = (cbit.vcell.math.Variable)rvs.getPrimarySymbolTableEntries()[j];
								}else if(rvs.getAlternateSymbolTableEntries() != null &&
										rvs.getAlternateSymbolTableEntries()[j] instanceof cbit.vcell.math.Variable){
									pastedMathVariable = (cbit.vcell.math.Variable)rvs.getAlternateSymbolTableEntries()[j];
								}
								if(pastedMathVariable != null){
									cbit.vcell.math.Variable localMathVariable = msm.findVariableByName(pastedMathVariable.getName());
									if(localMathVariable == null){
										localMathVariable = msm.findVariableByName(pastedMathVariable.getName()+"_init");
									}
									if(localMathVariable != null){
										cbit.vcell.parser.SymbolTableEntry[] localBiologicalSymbolArr =  msm.getBiologicalSymbol(localMathVariable);
										for(int k =0;k<localBiologicalSymbolArr.length;k+= 1){
											if(localBiologicalSymbolArr[k] instanceof SpeciesContext && scs.getSpeciesContext() == localBiologicalSymbolArr[k]){
												pasteDestination = scs.getInitialConditionParameter();
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
									cbit.vcell.desktop.VCellCopyPasteHelper.formatPasteList(
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
					errors.append(scs.getSpeciesContext().getName()+" ("+e.getClass().getName()+") "+e.getMessage()+"\n");
				}
			}
			if(errors != null){
				throw new Exception(errors.toString());
			}

		}
	}catch(Throwable e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Paste failed during pre-check (no changes made).\n"+e.getClass().getName()+" "+e.getMessage());
		return;
	}

	//Do paste
	try{
		if(pasteDescriptionsV.size() > 0){
			String[] pasteDescriptionArr = new String[pasteDescriptionsV.size()];
			pasteDescriptionsV.copyInto(pasteDescriptionArr);
			cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter[] changedParametersArr =
				new cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter[changedParametersV.size()];
			changedParametersV.copyInto(changedParametersArr);
			cbit.vcell.parser.Expression[] newExpressionsArr = new cbit.vcell.parser.Expression[newExpressionsV.size()];
			newExpressionsV.copyInto(newExpressionsArr);
			cbit.vcell.desktop.VCellCopyPasteHelper.chooseApplyPaste(pasteDescriptionArr,changedParametersArr,newExpressionsArr);
		}else{
			cbit.vcell.client.PopupGenerator.showInfoDialog("No paste items match the destination (no changes made).");
		}
	}catch(Throwable e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Error\n"+e.getClass().getName()+" "+e.getMessage());
	}

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
private void scrollPaneTable_MouseButton(java.awt.event.MouseEvent mouseEvent) {
	if(mouseEvent.isPopupTrigger()){
		Object obj = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);

		boolean bPastable =
			//obj instanceof cbit.vcell.desktop.VCellTransferable.SimulationParameterSelection ||
			//obj instanceof cbit.vcell.desktop.VCellTransferable.InitialConditionsSelection ||
			obj instanceof cbit.gui.SimpleTransferable.ResolvedValuesSelection;
			//||
			//obj instanceof cbit.vcell.desktop.VCellTransferable.OptimizationParametersSelection;
			
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
public void setSimulationContext(cbit.vcell.mapping.SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}


/**
 * Set the simulationContext1 to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext1(cbit.vcell.mapping.SimulationContext newValue) {
	if (ivjsimulationContext1 != newValue) {
		try {
			cbit.vcell.mapping.SimulationContext oldValue = getsimulationContext1();
			ivjsimulationContext1 = newValue;
			connPtoP3SetSource();
			connEtoM2(ivjsimulationContext1);
			connEtoM6(ivjsimulationContext1);
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

/**
 * Comment
 */
private void showCustomEditor(java.awt.event.MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2) {
		int row = getScrollPaneTable().getSelectedRow();
		int col = getScrollPaneTable().getSelectedColumn();
		switch (getScrollPaneTable().convertColumnIndexToModel(col)) {
			case SpeciesContextSpecsTableModel.COLUMN_INITIAL: {
				getJTextArea1().setText(getScrollPaneTable().getValueAt(row, col).toString());
				cbit.util.BeanUtils.centerOnComponent(getJDialog1(), this);
				getJDialog1().show();
				return;
			}
		}
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCDF8B1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DD814D53AD6DAD4E4D41AD4DA2CE119322D259515ED6C666E254DB6EEF90B5D4B96ED5EDD77DEEB59DD6E3D5CF25B0FBF85C5C585255592D7ADB0B294B0D2101F9994F4D484B2DCAD99188FE6E07E1879D0D04B7B1E1F774CB7B35FB0484DFD2E4F737A4DF94FFB4E7BF35EF34EFBFE3E8FA9EFF3C4E6EADA1EA425E6CA7C5FE72912145CAFC9D12F5E7BFB44459D0F4F16D47F36G2C1126FC1081
	4FC448DB7C63786CDCA971D2962413A15D98931F3D81FE2F137E6DD2239DFE8460D388F95F06AFB78C4FE776A564330C261F2D4A0167AA00944011273E0570FF3D2A0063B735B0BCC2EA1AA4EDEBA5B4CB2AB6F1DC8850C4GA482A4F37A9C20E1820C5BDB4969CE5FB2DDCA7E19ABEDA529C7D5A7023515158D634FC2696D544AB642EBFBF58EAF938BF992600872695264DDE5702CEA6E6B595F55E9D1F61E6D122D561DB62353E931776C6C99306C5D5BE437A89623D567301B6007436EEEB65AE52BB40756EB
	2B4E2275C852C5C857FEC1F0BFF690DEA9A49F52C5DFA07E923555D878BD99E04E9728DF75C7D5DC7E768B0FC9250FD42CD89DC03F79DFB03D123F7068B7F6B0A16DBD5A96FF12084DCBCF44E737811881C28122G6683EC24BE7076259D702C6BD4F48E1B4DE12FF3BA6B2DA6579107C936426F3DFBA10763DA64AE074BA4C92C1DBF2EAE5110E7B8E065BE3F6CF24CA6534B304D23DA9E1492E65F1E36CC43A68953AF24ADBC41EC31640447A60C37ABA06FCDDFFA7B24B76FAFA5B56FDC730274CC8D5EA5DFAC
	C8AF6575683F74708E79123475DEA7E9EB937CA665083FB2FC93454FD76119FF46D21FA169A448BB5D0E3E513E8AE5B1E4CC15F2F7CD5234C3780FC353E25B198C096D3E32BCC6FDBF67A46379091065E50AF72A70CC16F154CFC9FA8164551E0A4FE67E35A40D74572853715931G09GA9G85DF45E71783B4FE05BE2613BE9D210FB59BDDCA7D0045EA12DD92AB3BC4DA8FCFF9D0F1991B6CEE45E86F12CF98DD96E327D5F6CB1CA61336EF27D739652944773B0047C159AE3B0C2423C26DC61B2C48AEB723D9
	173A065038E41A5BE6314940406614305E0D296B60E9B53A15AF1CA623A2FBA8D87E56D452A6E70DA720C628GFEB33B94E790FB19C17FB1GE1428EF7537A8E4BAE98AE766EB5B89CFD834EB62209147895317375F46C08045FF33FC29BEF4FA438EDDF47E7D7FD0D757CCB6ADAD5BDFACB97915F689A42BED66BA43AB7833D82B081A086A081A069EB7423375A510F74AB3FAF157C4BC4CDBF0AG1F4EB9437CE75B995F7EF40C72A1329AA04F8A908A908590F78667031FBBBAF8DDA88B569D237A0D3229F9B7
	FF2D5DA7830FAD3F933CB2B9CE5D0F71778A5E2F546553C748E715E8635B1612B6CE86BDF3BB6233CBBB30EDF2538A192F579B5DB2FA77B197B826EC7234941377296AF67AE4FD0B145F8CC07A8A996F3783FC8E108F4066EB46DB8770BEG19278ECAEC9E5E8AF08460A300E2000F05CD92A80FC6104F89908E90833088A057981FDD8950819082B08BA09EC06A04B98A2099E090A082A096A08920208B7ABF0099A02C8B5B78462A0E5461ED361A6B39CE254FFBDC6777395E2847F69FBDBE61B6D945F546BABF
	576023634F03AA5C114BE85B7F4F40EC7D1B2036A63E1D2E7219FCEE6BE55C5659AABB2F7631B369B7ABF8BD1BD5F52E51603311BF3728F09F7DBF3053F7812CAFAFCDA776114F4AF6C5AFBBE53B89FE38F95E4AF4E2C7BEAFF5BBC419F5946F04F9CC879307DB605FCBA7B6B4AA0A4B52B920482D3222C0C069495FC2F93987BA1D1EA990F99551F843F18E66B1C17FC9BA19833A9CF63BCC8795D10467FFCA67A059AA5BC0E81DE3402EB0FCC58609577B64A1BE87FE1E31D4A2F318F5004C27AC861EE5A4BE60
	F4D9ECB0D411A9D94AB21179A9223AC0E523E8687FA416CF00E7D2974EAB6736623C326D7C83D24C99E71AD62C94BE6D6634E6939B5FCDA64F3C42E45A1129ADFBA91D8BB95E4DE42D58C06203GA1B96D876B241FEDA336F2DBEC83D6BA53831982AD378B736F12C93FE8F54ADD96594DB3C94ACDA7E79A27C8C8FBF7B9EDA790021A1DE66E62F9D5F6A25FA1BDC862685945F0530F137EF82859619CF09E116D83CD3AE6896710794BE87C8F7A07834CB2619C9215C972F1FE6713074EE165BCA1AF093647A4E7
	9E37E7AC2493CD2CBDC8FA0CCC722B7B73D4B62F14BD792C3CC56447C2DE22AA1F3577AA173A3D13F8FEC1F7FCF689G23B934DBCD236FE6B4D6F80E69C6FD6ED81ACBED56BFA0C378C6ECFAD878DF445235BEF9A738AF327C69CB09AFDA5531D2B7617B377D38D68C67BCE7F533B8A48E1E9FG1E51AD96F4A5BDACC65143338DE03007514D6BA1F4CF56A0DDE88F2BC74F69A3FBF89CCD6B3B3E9669F24D2C1EA2B30BFB74DE759D2BC3BABD4FBF8D4F69G734D2C3E8996C2373E9E698AACECDEAE35B0FDEAAD
	0C2E2817507DDC50A5DA585C1DE2E175E6C17ECE40E77451BE2AC3BA251749975A4B6A0B6DE575B1793A74C8578C654CGA1FDACF60B6DD353FD54A07882BE055311FA8B2CEA3AFF95F40DD696CBF4D83D6BE3FAFCF3G6986B9DD18155597E1E5FAB03AA38711AE1A5345F13AC4AF3A8F9A11EE89274B3345E7EF83505B54F6D9AC685AEC0C4EEAE3F689352975985F04F473ED4C8F965AD83BA5F3BAD6DF47A1244B33B339CA6C2CBE33DDCD3775B0524D62F4311AF43F91F4998E488728F2B03A0B8EB55D8C83
	524582BE91C0F2F2FFF12A696CC744DE8A60A3GE2B469F69FC5BA299F5A9420325F1BAF9BE77FD4CC7AB95D9769B51EB58E6EF41F0379EB6F5EC3F424E5FB9F4B77E7F17A41FE5C6358D3C74638D9100E85C86C47F9621FD7629E47043E6952E273CB690176F0D27A199C1BDC2ABD0EF714F4B6062E4FA6BCCFC31E5505634D56E56AF5D61D5B6D683250011F0FA9AFAED34FA38C774A3265706C36385CCA93CC7403B87E7CEA9911172C81B91A2F09FFC8576DE40C9F83FC238112DC847F0E134C2FE4ACE2FAF6
	15211EE16727C99D936FCCDB91C04FF2B7532F4D6D5133E3FFD9BA33FF75693DD210FD29A0939813671B83EA3B4FDD7DCDC0F9C2399CD1AAF9167CC7F2BA53BF2C9F75DF8C795B94547F79FE22BF9993195CAD7D6FE97B8D1BA5A10F794E8197E993A263E985FD67AE057018G69A800F98A563934E0D3GDF44D91655F975EE5C0BCB90F52EAFA3F5668DC0FF855083B0FD8E6FC7FDCE83EE82G1B3F577413384DAFF610B09EF8F9BF51D7B1DB5C4C3EFF2C2B4571634129D2743250B42D3DBB737D61E9738778
	5E5D00473E26379252868118CC79B5A8137EAC744733A853459A644966601ADE759CBC8768628116G641E43B211B544F6B69E4B307A97527A893E8868BA842D23B6104ECC476FFF2056B14BB8CED347505F7524051FE33A451C73681878DAC2DA72EB2863433528E356A03451A04A70C6BF21770E33D06E41BA14FB9068E709B22FD15BDBADEE25954B6D24112D4EEC34774826A215376C24E17DCE031A3801200A19CDBF312BED3A88F8948D41FCG50B104FE18F834064A2878773CA203C6AF75A75BD7FC026B
	D4D3AFF4027AE78D21CE77551336B63AFAF6B11E33171335ED0F4C2516CD9A3C1B0AC7481D3767BB6FE3FBEE53C7CA1549FD1A6DC96DB0445AB1773C2ABD1FAFCBCF86E0FA7CF7BD36E78750041FC7BD3E56219EACFD2D0E78184DE2975647711549F471A44AB4E67E9C29E05DCD81E50A63322428E40AFAB3A1AD66CD7431E7F5E85B468B9017DEC019CA751F50BEB16016F7527FF5D6CBD79F77131D46F38AF712A3A40FB907047361498A92DB384D0EF33A81376230B540424361AAB284ACCA1A1A6BB6F51D3A
	7FD4DD7ADAD33710556634688BBC763C6051ED907ACE2868BF21FA34F7494578EC93G0B7DA7B728E37F49001F873068A26A7EA77DEE529E2C1330C7336C6AF638EC7E7A5729FA876AFF5F86B2DF1AACEE2743AD97915D18BC4D8DA84F9260D57E8D4AB33EDFCB9E6BB758174EAC5F427A928D7F6B94EEC9ABF45C1D47BAFB41C576B5B591B61CCF4681B17FC29D51221E7D47895E8EFA982DAD32FB402AB07A7B84FD925096FD0BF6D8F9107B65EE162EB838C20CBB322945F10E73BBFBD078B5148D9765FF0565
	4BD8BA33F1152ABCAC5A86ECF61479E0A35691876533AEE19D8FB6E25FE0362A68D45B2A96684CG21027EE5CACFFAB11FA71A503FA300A67E925A625E95240F4903DD3213B460312E2E81972CA4058FDFCB4F3A489EC20362B8D2F1264117D25D88EDE617639C89A7BCE356E302EF6E18786C46B1A85F8707F0FEE2698307824DA72C1E6B8FE1BDE3200EE8D1CF52A11F7E596C125D6E11754F70F432BF686EF24132190428B492BAC3A96B879445E18F56D539EC0643A8DB9248D5F4954AB67B70CEEF59DA2049
	0D4104E3F52EB6E01D9DD0DF78D558C7648AF53B47DD457651D3844F33067FDB1F3D342A9A1E3DF49BC211ED4D90544AE76AFC6A71B50B2AA235DDDE92BA64DC8DF154552843BF17E969F071EA54E16F117F1B8E735E2BD26B20F3B807AED385A67F24A3A8FF984896AB643F2BDCCBFE699A147F5DD14B4F781EBA0AFC7320CE43B5487753BDDAFC478A3EBFBEBADA3EEC5FF875FE5F334F70EBBC67ABD12A5FE27E21E702B3E82C95F38DBB93CC9778B9949F2742B37D2A6AF00CCB04BC7DD8BC479A17C3622969
	101E8710G108C10B78E6608F138EE133348F8B46CB9E1930C9CAE3B515A6A35D10A67F554D6FD7290CFEF2663A00DGA53443BCA731C373384BBDDBFA1116B3DFC6B9B657977FDDFDD7A1E34D21B4A223D69C934E6B1EBE4EB357B35B6DD9C5FCE39160B32E0535B9C0B9C05B35E837486C12E0F643B3D3EEB0D66F645524DE856A8983188E908310A86AFDABBBE8FBB48CEA368733596D7D2333F5E47F655A1A15BB6CA265246B344B65F1FC91FFD6411379726A8162334D57797A78FB945FF11D2F0FFFCFC19F
	F7C25E026B709C6C3A5DA33D5BD09942E4E0FCEE20EBD2C3886313AD7013A85E2C42B37E1BA81E2447C25E229064FF5C2F0F7B0261FAEF5B30F2E70216F37A14E37E7347DC328E8F013CC80039G71GCBG8AEE084F2E3A817DE87F727AE0FED40787A3ED8E71D367FB380172EE2C991D1E45C14B355D60DD0E4DB93D97826CFD5120022D977F2BC73DDE345E40560B61700C82E07B9167FAF1FFEC5E8D389FF15EC65AF2892473EE0C4FAE39915B725F7DE4555F68AD1B360EA9FE65BE70C9F75C285516BDEBC9
	DB8EC25E8400A800D80045GD921A0D7A836E55872FD415A12CEBC500EDDBC5673E9C4A6E7F46D685A7027414B05EA157B36EEF4768C75AB3753A7ED8A5572194E4CF26932776B68DEAA73277F28D37B5345D0F6A7A4921E512168CF329D773762C2511FD653FBC3D9B745E79782D45E047E54D0BFBA7DDB7D4A796A5FFC1316BF7DF69D71A7B3640D85188C908D10G108230E9BC7A532F17578573A7F1ED274D2183FC23435127B1B4F0F97F6CA76F487CEA05AED8392A711A7DA61F685986F9CE009000C800D8
	0005GD9B7231EFBD6849D83D5FA869F83DF7013F7E46D79AB3FF23E63C3614DDAFAA62CA7FAD6C2DE9B0095A084A08AA096E03150737A955541746463C370363CD5BF3A360C88DAAEA7CC2B4F2EEE8D30D74DD60C921E0BB68F781E0313F32EEDD0E7E598764D179AC85FA4FB894DE15887CFB40CCE9FD350F2A1BE7AB099FE520FB2C48899169F981D8C85C14B4507E9150B9AA53FA70216CB5264F7716068785D9034DC56ADDA650EB40E0E5F2920658AEF517251673FA17DCCAB9601B9244EA59B390FEE3860
	9B0F107D36CA2853F08B7A283593639153AD689F9B1BC6B72E947B15739DD7A6EB5A6F29D172FBAEA83FB91A7CEEBEB4BAFED37C4A79721B2F492F65706878F57815736537D013DF3EE1F47C367A15736537C4539FE3B38378A36E507031E8F28546AAF937222F7DE214327EB6282C06DB3546432CF21441A4E418F2E4F4B24C74AB67676F1AB23CA8E418A3E4B09E9D1D8C8E3FF23EB2AC561421FB8F4A10F19B2C77695D638F6959FD95241B85AE3D166094C8075E06324E2A23675BA27D84CD13358ECB575735
	7B48B0D2A0F5862361E36728F90C641EAF0BE34667843E7BB7B1C77B6E5FDDB95AF77F3E17330661218E758DF74CFFA0E7783B018F65E85D8DFC1F6A73F29A0977DD83F645E213A9DB56AF65C1F21E8251D64B001D05E204335BF7BFC84278EED381E947585BB00EBB38F237649F4731D3AE2C24CDB63A0EF5F70BE015ACE1D0667D2B825B2FE115367D8E2F52365F71D5435B6F642A616D9739BA30ACB3D7EB4B7250EAEDD99EDBBD3CACCF2F8EA48B6BD31D741E43C230F356846CE70F3B301FED9238DF3A892E
	9652ED8237D5A1B8B324AFCE407E16BFC05AADC424B753B4196BD97AFEE534FDA59090DE5E7DB166BB67216E27CC773B69DD0E6940FB2E50B52C0C5CAB0C0574C2GE9A258EF229857E99E399FC56EBE117BD464EE934B7BDB99111714A1E5C919E072C8A320713649BE4AA3136B12BD42F2A365417A7EEF95F2375B6F8E70D1D8C5CAF8BFA5E4FFB627E9D69C4DE542CF1764866E9369EB347BC4769AF2C74B6D98F0F5493EFE1F3B26C064F9D6F4A4GA06270BB50343CE2F47548FE7D261016E7F9014A972FA1
	EB19CE0B496262B7E1190EADB5EA9D4B055DFB9A081DB4E213D6F1ACA8611C38531C455B20EAA26E255CE0A1E32B8269D000C861DB3F6E9BAEF61F6B63ABF193F11F79AD3A5737F0A25B4FA540707FCD71C9AABC5B679DE343F79417C0DE476D71592C5D1FDDCF5A4E8943126C260BE93DD1B1F2DBEC29C5DB10B35D5D423E7DEB83377760DA6D767EF6ED60769E37B6F8FB0FDF9B383DA72E8D5E5EF72F8DB416B2DDFFDF2BEE778A01FF24CEED03B2618FF76D8CF0EF2C55E935A8449D18AFEC2DC7DF18F7BB7A
	C2CD8F5D2705F45E24786C12C9688B3D7661FCC1BFA950395AB5F41F2EFD122FAF04D23CE9122FAF64BB50971C1037E012388BD63F82ED506DF258F43B38FD7F30AEF0FB272C53EE6FF75785EE6F4C3C606D3DAAAFF0FB676785EF6F8F7206EF6FF21D172E4D3B51377307716D7CG3E1DBF0CEF670F40377307716D7C9178F63E76D836D42F72DDE8CD2C73B76B836B7866FAED9D7F34BE300EEF2F8F2EE37A0640BAAE5F90DC47F59B06EF4FE7553A9EA163B66BFFC787F1DF22B29C7B5F5767693B29108E8108
	88477E97AF76462243BDFDEDBE7F1D884FE4002CBBBCF90C77CD8DEA5E95924E5BBF6B477AB6294A14725FFAF83683D87D6A2BBB202EEF17403FF1405BE74BB8FE76C1EFFCB9470FED7446D7F01B9C3B00EB36B1F720CD469809CD22A19D8730688E34C9FF27FACC126E749E03726E749F1378BC5A24763D5D821F2C469F59A370BFBD2426AF93786B8F2B716582EFB028719E1BFF2FC2EB8CAD521053DB5E5CA36A7A76087AD616EB55D795343E85DE75FDA66AFBC5333E662075059FD557D7A96A7B43BE2D7ACC
	C16B2B5ECF5E55329A079C838A4491F7929FE85B0F7BFC03108E83088618FFA7BBAFE2735EC36708CFF53B0CB668FEF41196F397A97B57DAACDB8269DA008E00B1G91F7617C322D1214754C10B83F94961031C174ED2F18F348C562D2135CED9C3042724961160FB979AB58002F1DCCF8CFF0E19F4F02B413A7CE3CEFB496F061G51G71GA913D11EBF3879BDE623D5541525E05FA818A26E99A904CE31A8F40DDBB405FC3BE2B1599E61B2EE6FA46F9D71DB6874884E2E30001047903BB3292DBB69FDE9D6FE
	DB21273C6E82FD2FCBFD8FC964B5E5647A6655D92DACAFE24BAAF59E3D030565EE6DD863178785055F141951EF2CA04F18A95E7EB2E1CA20D863DD9A47CE1F62FB3FA12B170E9FD3FC6FB704D970FEC3AC64155C0D6B4F7F6943F5EA18407D55C6F0F3A01DF0B70EBF49B6C9487CE7556FEBED5F75BA51F77D34C63A679F4023081EB1A65D2D5DDFB36E7146975C03717FEC9AC3165C436C1DA95A610794DF2B4233F6E868413BE2ED1097FD8F5A7CAA3B58AF08C01BDF6F705879D6556F0D0EABEF0F72FFG0F56
	C15FB133B142E367539101FC7FC596B7C4B05BAE9378D7A9BEC405E7B6B7D1BCC9C7C0DE467750663F2767F9E6C80F9538F89D4105C3FA3A40FD2E473619A3F045D58417B1955AEBAA6226517D70D2C8B78A5C6B7D48633440359EC79ECE010BBE0EBCAE8A5C16E664B1DF605EBF00BC9227221F34DD98EE3D12BCB5109D13E92C11F52F6F9872AE45975C6BBB06DCEB44B1248872C26FC519C26819D39224B3A291F75C097A9489DCB4350F81522785EE9B5DBBB7C3FAD0605E223831101EA0F0D9545E91101E95
	096F77DC5C0F6B273900CB0E4439F1D707571A105C225C3B77204BE22AB7769868CC4B622FF9F4FF1C14AF190671579FE91DA6C8DF8418B08D5F799A7C905F310A3AEF2A14E852FE37AD316D1B3428E94C5EF153BCF77E42879352984F5ADA6419F3DFFC367EBE0233572A6F09927BFE16416EE4307C882FFCB21F10BB511E7C973D72093C37242B73B36A5479192A72CC37673623EECB1E1BA5D99F999B705BCB2177B11DE671E75842BBD2473CBF184E663A19742E1E7A051CAFADF613639CCC4564ABAF0AFB27
	3E293579D8EB4319E3C74F688C7003710FB8273ECFD9397CEA4D774B89AC62FC1760B37F4ED4E6577FF221DD4B6FC73BEEF37B5A75BF3D6CF6426DEB574F3D6C966276356BF3DE79CF38FD6D7AAC4FE776582C50BB732E8119697942A9351E39F7DA53F0BF40D7CF6B7DCC3F417BD56F6450FEF0086EE9C581BE6EFE6C9B77D25CC2C867CCC75C1F4D587F8A2763B81151CB64B10874AA1AA66F2F723B1D967518CF683E7BB17F87DEBCAE679BADA38196F366DAB166B4038E9781A6834C85C8180E3164AE5BDF61
	E99C00A01EFCABC8E6EFF5388F407A0C3F17466250A3B60CC317885B56503D241C87E0ECFBGF135BA34F715407D321B60C2A19D79G5A78719E020B06F41C404D23B872FE9FF9A7195FFB6D79AE66DF42C71D0E63E9FE8765BB61615B06197C39E98631C1383D1027139F7077272C99FCCDCF6D55CB7788CC009B1C01B67C5D905AF5728C31BFC0677398C84783A4897CC20AA76F552470775F79FE5EF9351EB1B33E9B3FCE627C990FEB4EDF893FDEA6F94E9B56C9A97DE4DCF63A64EE193C4953EAB9CF56C14C
	CF8BE8DC10B3B3BE3BE0A67AF86FF1FC27082DF5CCFF1308BF4E24771D74FA4EC3BFD3FB6FC08D6DC0E385A05F18A1635518191E3DCC462B32192CA9ED46C10BED40C6A5E3F23550F9FB8150A78A394A697E89ACDCFDE8FFCE63031CA8508100615650F1DD8F69E6G2647F5DBBDFA3CF09C753898652FC72A0F9EE1D1CC0F0DBCAFB24A23876357CCE32ED9001FABE4B0D05BAE02F4F2947AE55F85B13EFE9FFD60EDA33137D324B3BAC91AFC37005FBBB5DE29185A43F35A956301B6DCA75D53C976ACBA9DAE3A
	F94876AC9E6D44B1B784E409G188F1078FD765D86B6AE1B1DC81382781C87E19D8AE0G30BE083E71C205374FA6EC13845FFD655EF9FF596E494B74A977370FBCF91B2B3C739A0AA48947402B035E13576A13EB2430D116FB76324B3118FC9EFD2FBB74C15F330567A9BE7CC15F330594919BC7C2DE568F701D1DA27A4D87AB24C3GA2G2281628112FF0077847A8ACB25A077BD5B9CCE03FCD6362A0E4B791D6420F739FD0145C6BFDA06B1681CA7E6C851576A83FE43A0EB96FF67831FEF5C11CA1ECC3FA7F7
	907D5AA1CF8188871885B08FA0E1966AB73B103FDBC32EBE54A90A314BCC3F9AA1AED2D864F39EB459DCE3F53F7AB1DD67C0BDB98F09EFED96126FEC1DB0767A5F26GE4334B6114DD4A90435257E0399F3C633ADCBBB199FEC1DF07E13CEB37EC8F449B34B884F1B8FB4B364D58A37A0251CC3EA1F6960432F4992D6C50345E68EAF6D8ADDDC3FC8C3CC8E3C7721EE649C358AF1FB0937EE6074BF25EE1D77CCB627879B2ADCB6EFFDB21AC0B8172AC05DC0728077C4768980EE3FCDCB6F2A6526A7D0EA75FAF5F
	E34598BF6EA10C71CDD65F987F16D4F50CFE3B55B746A765BD7931D63F3513D779372D3EB1BE16E7721AEC9863337409FAA23F49F25682DD426068E2BB244CB68E9AB71ECC839FFE98564FF47E7AD20773D741435A7317FA8EAB811ADA00FC1EEEFB58F79E7EC3B769B750CC6039EA5E59BDE83B0E0731CD8D74FB9F2A13F37E9EFF77FB44C7823C4A405BA2460EFB3D93C4FDA9F6B6AFA80A43067D3AB7977B75261977CA89C3A752308DFCF51D4F75CFF9583366717845D5037E7B646103B81F144EE67B640D70
	BCBD1B5D0B8D19CDE82473C8234C8EF4C672F4360F9D78FB6363BFA1BA701B62478E8B7A3853ABBDF89D79FC29E7BF791744B5B85D73BA32B9ED174F950A17B9759698D24807C50BBE2BA8A95CDDD10C67E3D769511EF1A067E6DA9E7CC442FFC9B57EB864390E8C575C5E2F67286D2DFFC51F06B11B5FB8BA075923FD0E475E052FEFCEE7F5FE334B23471D95A35103155BDE66A977496E1117EBAC7714EBBCBEF23BC9ADE83730B95AF6E37577D5F86C75629E8F2F3587C6AE63FFFE66A9F729E1A465D89F3F7A
	82767158B9017BB8467989C013B4475347B39E716D63B91D64BB8D62F678AE1C73DF6D447D39DCA85378886E077D53DF092C1E7716681BCA75E47A2E27B1DC3DEC3423BDBF6CC2FB0EF98477446E26779422A19D873068913CEBBE5E09345223B89E7F5469BB9E3FA029475394677063710EFE5F7178A52F7178F4FF2071181F75124F37705FAF29FE3F3B7F4A6FE7EF283912BC581C3F2BB8701D014AE26DBB837B0B356FBD6A0B073FE3E9A88E7C7D4A56E27577ABD9EC7213CA52B615608BEDG03G21G910F
	E22C74FAB1D90032B7C3FDE32E86D30F0C7B0448FF5E0E403ABE37A378B724976CA07D044DC724FE447FF287E9AF456124C806FBED8731114B52E3D698A9477F5B8E6263D6391B22996E0D0FD7296D4642BACE7F6F9F939BD85837ABAD6769945C8A04769E56F7EFB31301D53DC72A1AF7F61EE8E8E9EB5255994E346ADA0E998C75F5ADE76A8CDF561DEC15306F3FE9AE9B26FC633116263F9FBB5A26D5836B33D5966C33F39F45F9DB3124B3FB60BDC76B6546262C1EAA9DF9161516F2DEA1BA64157C68C867B4
	9C2F5B492751F83D05BD5B7DF44633EB15491A8DCDED22AF7E446B8ED749E3B016BDB67C3E8F73616AFD0570EC83DAA7408400A8009800F80045G9951684F27BE99AE7697A2D237F699AD6058EC1C0E994435D9A154559E0D63E9569046E8E15162DBFBC304B6965209GA902764373E85B5C47710C2237CFBD771379BC50BEFE63634C9656473DE3ADFE266B752EDD945044BCBE925B3DD3C5663985C01B841077842C6F81EA815A812CGE3GA6GC4814C830887D88CA0BD89BA81EC8328FC926D5BDEDABC0C
	FD79010E4A3AA8FFE60BDAFE83545771647072B31B4E3E00B6D51E3CDCFFFDE53FA478AF6C8C56FF22FC64E1657E98341C6F3BA02C5C47DDDA65E2FDF8A42852EC6FE0AD7D5EE152133EFB8A9BA9FE49133EFB8A11623EE256DCE84F393867F41BD34DBF8E708B678EEFEF563E4FD7869E5F97D4869F5FFFD5C93F798863F25B10130F4D3F2EA46B84C7F737DB967348EFABF3852E495D6CF0536F89B3596BACEA59D3C06E1C2706171D153B496B1D5BC2A8D39E249C5BB7CC206B26C6202DCEC75C6B945781382D
	99A8F3633E4036B936AF38ED5A77E94FFDDF6F731F7B3A76E94FFD16FD7EF31F1D566B12FB40026C029043AB7B48D84356E7C70CF6E30F6A1D5F1F7A1D738783D6EE41A84B2D9C9A5D9E5B8CBA56DEFC4A37BF4C26781027FC7BC355907607891017729476070F4F2B791F7EA14CA5BF9C497879C48DD92B0E855A8800F9G89GC9GD9CF631818DDC5667FC0E3E20B4301D30E82C3226CD20D0E4C572668517FD205CFCE55237F250A36DFDD9D587F56D787773F4D555A7E373D5A5F7FF6D4EB7BDFE9353F7FD5
	D413713987467EF6C137D760CE8ADC8D2D13603E3418943340B7D039883ED1A68C45FBCAD42EF394574EF14DAA5CC90E7B221A0CAF203259614232FF2FA6FBC05D962B1577B1CAE33143F0A36C51C5E534B80DAA394D82F7D2344526967F76595222EE9F7E2D8D2FBB9F1B40BFCA1F9E5E4758FB86B174BE3845EE3590AF21FFC9C5702927EBBFBD54537EB41EF354BBE89BC0FA6C53685F85644F29F0B93628FE4F78875C99FA660A72E0BA4FF0E29B1035A7437D4009ED700220EB37A0DD1F984B3F32A05D9401
	0B5479376983BAF51B32F53A71439CC92C9D285BB27C2641159EBC3AAD37472CA10F7D9FD77D2E9F3A72EDF16A0A72607EF79EED771C30674173E83B9FC958171E2989BCDE7D2CA678F875D20D76F8750B9A7F716A559A6D71EAE10DFA3CE2323EF0413F6D97DCD037BDDB631C6CA2ED5D653012E1056F59BAE9FF8C07FE9779B42E55CEBA886FCE32F9C3029EC15B6F905F0DF99A4FC9DF53C9227E4F9CA4F6755A2F5291E6C53B7614169058DDD1D10C6F17B908DCE41D4A7E5E48231B4253A0E6FDD34AD424B5
	8D03CE7245C6187E8B595C23C8EB791FEAA8747C2D86C5DA4AB6C695A96D58E1C54A2497DF95A903DC1CD524AC72A82C33C2F6B64DA13F5DAEE92D7FE6DA5D78DF3C92F5C61F17A625B5C9CB4D547B1B244CF324FB8C1241077EDD85A83BDA14ED22A3EF6259323D69EF5C1B1EA6E5C0C15632CDD286E99EA86FE27524C34B52DAB254606E173E25B1BE2BBBD8EA79E51757AE610FF5C92B3C1BABF55A06EBF2EFAE9BC813328EC8CB3B0DD62759885A506F619924951DD4ADB26D03E5F369B793BF05321F52327B
	8CD2DA97344CC68F1E7A9B4BEDF59A3BE4A049B699DDFD3486A2D27EA9A93B5B5553C999C0DB1EBAF08A585294495EE01016ABE6191F91A81096D5B7132003E76C23A6167E929171972AE89ECDADDC28CE41BF276A1FAA4769FC58ABFD7468C5B5A57FA65FA5755D2A14F1DD816F1015BE435F7BF8463376E57157044D241546GFE72B398EFAD7C001C0385BAE3EB359941070D563EBA3B091C37558FA932E77F6C0E4D24FEB75270CC16973119485D2BAA733FD0CB8788ABD400DFFAA4GG9CF2GGD0CB8182
	94G94G88G88GCDF8B1B6ABD400DFFAA4GG9CF2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG34A4GGGG
**end of data**/
}
}