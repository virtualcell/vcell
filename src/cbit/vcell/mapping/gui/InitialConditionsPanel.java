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
	D0CB838494G88G88GABFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD8D45719B0091A180498E2C8C33A6EAE1B1AADCDE916B42635DDB7B52DEDCDE392CAC9EA3F076C1AAF740B7D9EB765E9C96BB6363D8C18C0C294A3D9CDE39352EA4408045F087C49GAAA88A81D490225C19394C8C4C9FB3F7E078D177BDBF6F1DBBB3F798E463BE6B733C5EB96FF94FF9FF4EFB4EF94F4F3D88056F47EF4D52958AC256D6017FBB1CA58819E30270D86676B5082B591F12A7287E
	FDG30C3B8A16640338D72FA63D2728A04DB1BF3A1BD8869243BD372F6416F1D429D4DED8E789106CF91645DECF0A44E4E277EA364739ACD5FF8A49F1E5BG06GC71E097B897FAF9E594D718BB99EA1CBA7885145046611A3FBB8AE83E8F2815786B0D84C68E5F8B281965556F03A41BB93044C9F39F5EF12FAD4F5A22472321B7933C878DDD67C81426BDF291C24CC9924ADGC479ECE1D9F9A5BC6B3BC64D0DC603D5AE9DB7CAB6DB29DDF4392C8EF32959EB2D2DEDF7D8E52BE8EBF1BACC70436970F4098E49
	A66C7908547BE1EDAE2DC79052214E840A4BF091DEC3705B8790739162AFBA88FE937C4EB918123767A06A57303F1E4B5FB77DCDA135421E33BD0CFED58719DE03877D7A399C4F695E21EDB1F31058BC8E72D681A483E48294162464D5813CCBFDE041C79F41D3EF10DB1CF63B5321F7391AEDA677E927C932416F5ADA486138EE4968F41B840135F37A11928DF9E683D66ED757DC0E59A421925BBC317BC1A16D61AF68DE5330C9DA42342E23045942D462378963FD30B69C6F05A501BE99487B3C20663D7626E7
	3337EA70CE77BC1B9D476BC9D4714EA8A1EDED99A3ED3D8EFE13F244DF99FE02622B8E79714CBFFFC3FD06247BA0EF4DA1740D3EEDA8CBC74EFDC2C179BD1AF6087BEE342E2C144950D69AAC4B971C24EE391471BC24487288452FD76119ACA66A27A4DD7FB178EED9CA9E732FC507C8FFAD2CC849AB83E88398820882D88610D401BE366E230F6768E3DD22DBEE76DAEDA649AD30320B8E7EA7BCA51F6C965B9D9ED9F49825F3225BAA9AEC12C760B4CB8E929B98824A5DC5FD5F88BCCEC88E49AD120E8A350BF6
	C9165C9EC6336C608EC26316E8EE2F55AE81833BCB40FA179F5C89CF1B68114F3ACC22AC79A9D8FE62C152A66362B92891AA005F4CAEDFAFA576CA867DB782ECD66C303F0454F7CAF243F0D1DB5B61F40EFADD3DC493212D125839190E9D7970FBEFA55A78410F89AE9A524BAB311E0BA5EF296AE9359A097822FB927B18E30C683E926853G36G94D72564358274D5219F3D5407FE547A66570412E76E567423BE7959EC390A79CFF4D5F0FF3ACC7990D9D7C0DEAAC0A6C0E1F5CADECDB54E87DBDCC358B739AC
	D8F7F1157FB74A26663D32D23BCF069FDBF60DA12F2D9C276E47787B8D5E2F5465336748E71B5246FB4AC99B8F021EBE0038EAEC1BFBCB0A182FB70B9E893D3B538D0EA9197CAD4564DD666C23CF5637C8797781C8DFA1637DBE00A3GEF8330791A71EE8178B3G1927CE88EC9E7EAB40E9007DG87G8EAAB679E74AA38964DB8FA0FC823EG508560830887D88B1081307BF0CADE930085A086E0A54086005C9A685FGFDGD1G89G49G1BEB300DC79CC3D9335BECBB57F327CA1FF7384EFF66FA239E7B02
	74B844ED320D6B0CF59E516053421FA7D438535750367F1B01597A7B91EDCDFCBBDB65B3EFF3DB3F46ED1D27327376A0BB13FE73862F67FDD51DBBB4783C4B1F9F28F07B7F97586973G56176FB8C46CA30DCB8E39D5F2C98E937C70703CAF9CA2F664735230D3A9330C62DDB00F35404461D170779DA2B694E559EDB5F8E529C712E588A87D798FD0DE9E2F41651F821157C307C83BB8A7E09ED3683FF1084C81C62743A151C1C5A94273D752B9C832C9F6903A4569F5488C7FE8A90957C725C9BE87FE3774D501
	4CE1B6AF19CFD98C1CA4929FF0392DF6982A4814AC380E1079293FF6374AC6C500A331FCABBC7B0E603CB271D71CD776CEFDC5D8FD4925530A15626E3FC33772889B5F579D714FABCC2607BF56167DDBF4AE64F88F1335FA97098F420464349F6C94FE3417584AE335FBEDF42687B299DA2E9C737FC6A27D2247A5992D1207E7121407CE4EB4CE9110766F2AE8BB0190546CB4331C6755BB08FCA7DBC19227391C6192BEA57D71E4175365F51D169C5E7616AE8167103395B47E877D05DA98FFEBF18E29AEA379B8
	3F73492345E963BCA12F2F16344777478A39BD4BA05DD64B5A0324B7527CD1F7214A66712AFCD67E9EA53F2C8E4A577973D9FB57FB546D5D47732360998B40E88E1854B4091CA6951E9B6BD01F3F1497D01B0DF9A5885F08CDCFA97EF7207C2D203CF35C97D9FEF9B971C51BBAD62AA7FC33DC38D6946A994F5D75AC8E290167FB00E6F4CFA8F43175ACC6C90467AA008D1C2E24012ECF4710AE1357134869739BF89CCD6B3338114E57406A09E9E0F1CFE2C0FD259E24CB6479EB883E9156DE0D2C3EAD0D046EE5
	05AE2A114D4BF10DCC1F84CE97F31450FDCDC63A3623EC6E9EBA4A6AF541331458280950DD75A2DD5AD1A6DF66D1D6DFD9932B0F4957BC0EF4AB811F8C105144E237B23D1A6E558905AF6007740C0E549B95C077ED05AEC94FE209357A407A189E03BE245B4069B66BD9FD394DCC8FC6D7B209F4C54D0C2E2619513585507DDE21B3F13A99F8C683A4B62B6D724494522D62F4294D4CAE194DEABD6C53C8D755427C202305355BE08B23E3759D1DC13A1996A6D7EC8B2BAFB900CEF79969F637B23A32D6AD3A6FAB
	F4B6128F301C5325372A69EE3902F4E5474096G16E35CDF0E2969CEDFD576D2G1FFB9C623E63DAF49049F2BA8B64C781449F8F644B4659DF9FA07D1C6E0B0C0863222F54B38173D7ED6DC9BA52323D0F6F51758B215FF09C77B8FE59C046385DA7C0EF003693B8CF7C78CD5C63089DCD90165F7CCCF638BD1C2193CC0E05A77C73C466CFA73259987A771504679A48CBBD0163CDE239FA1D2577F81CC6AB9D787918B2D0211EC798EE2862F5F88EDB5D9E399DA6FA9F0EBF4395C4DE328664E83EA65EC4576DE4
	0C5F88FC0B5AE08EEC2363211B4C2FE4ACE2FA9AABD14F18297B05213B3F28FBA30C1E4B5A18FE2B5A7CFA2EB9DB1D4D6C5FB0D8ABC458170AB001B1F9AE9FD75BFD55764BE1654964F294367B65D9E67CEDB6537FD1B76A5F8B79516D287FABD4FFB2A6B2393B475E51769BB6CBC29E739D59C35A044838269DFD67F1AF6131855205A7D372CACEE21D2F6E5E934697F116E5F50AA65C0BEBBD09F53EECA2F54EC0BA96A091006973118B75D98338ADGEC7EBE6AA6F1DBC86CA0E0BC70080B68ABDB2C9EE65FDF
	691B387D56BDF81F307235E81D565E1D654BF13A2AD3FC6F6E145F3EDD694F69CCGCC26CCC526C420C9BF05B28D0CA1CFB6875BC755F3701E8ED8478118GFC9DD8E64198311D1D47B22C7E6FD19FA6789820DB2B501E892BB35371EB6F29F54C9597E96A987D8B33CEB84DF4ABBE6D57F15DB34F69B6BC03BA5E2978118BE892CE238CBBBC04BEB04EC2394FFBD06E8DC03F7B8C16F954C36432D9BDF28F16AB2511ED0BC5F418A5D3314ADBCAE9D8DF5A21A6BEC6D08718CD871CEA1BF6810F980095GEB4F20
	9FEEB8F3144AA807763C628E0DDE9ACAD6F7609C5769BDD951896A5F5D09BAAD12C9DB0BEEF3B96379706BE4EDEB16385412C903F77B01B9F2676D794A1F31BD7736AE97EAB2C7B55B135A211335232F535F1E2DBF2F4E6E83E0FABCABE3FB2E859A218B75E870229EACADF9090F592D8E457AB83EB219E67AD12628F5AB045DBBEF8FABD3CD9713E5284BAFD35C8B4F6992DFC09FCB7022ED1300E623A2537671C334CFF8BDD2A97D3F45E6B50EF2BFA995A7E46EA427C99EF38E8167437EEA92DBF8AC4E09962F
	C7F65A0F41424369AE6E88DB14B4B5576D3E1D6A7ED3FF752226EE0B37DBF4C51F725873D33FEEA668BBBE257F0C0E233DE301E69DG0B7DF75B5531FFCEB70C1FGDD5D28FB4AF885E98F56C95823CBF28FBB5D76D07D752A5E017A7F63AEB2DF1AE088F2FA24E2229B136763891447843C16F523BC9F8CEA49135A0DFD69526B9F303EC443FF3D4CADE9030EDB5AE998819729EBEFA7ECB81F9794BE9B210E229E2C2771D302F7009E222DDB72F8EDB223DF64C37ABE200D69C1BBEC72F13F2CE0694249B714F1
	C7B2F5BBA7B83F33130ADFC3D921974B2F1A6465ABD9BAFDF21B2ABCAC5A3CF6874A7C619456D18365DDCA9D37CFE15FE036DAAA2AED15GF449G198A7DA3141E74E2BECFCC21FF671E05317FAC5A62CBEF10BEA6790C120BB4E0275168F543CAD271611B68D99759C3B8A6BBCFD7DFBA96CC29EE04DE0B5BB9C142897F187505E9646B831EC9E7D13E5FCF637C44527BE6424DA72C9E4B8C5633916AA8BA0775BCBD9354BF3B5C1247B3377E9917CD7687BDC6B7AC1BC908CAA321CB143259AB4BCEC7242E4AE5
	BBF899E56B8339E2945916DEAE8D142D9B1ADC0CA49C2B73A5254E35D01FF09E7BC82BD95D6EB56759BE7A50F964F9764AFF2F4FDE5D56G4F913A8DA1CB76AE88EA25CB7A20FA024D222AC8ED172FDFC19DE410AFD151A1C95252A1DD51611D2B7FBD9D563CD32F562145691A3CC695187C63D7D17E4DA0DBD99F4ABFB52CA53F258F65FFEE5E72B33E3582721D01BAD7A8FC37D93478EED2787E03B0DF3EEC5FF8ED485927506FBFDFA9D47DC6E0E70246C6A2EFF1BFBB934CD670F60A2FD16119FEAFB7609857
	86F9097DF80E356DB009278AAE40388F508AB088B083306C822E5BDE2AA063512C67046DB0F2389D222DA7E023944F6B6898BCAACD72F4C2A531B30DG85657CF90C58E16505EBBDDB925C24DC72B514E3F37D018B6A3B8AB9BBCE6A080CDAF10CB0406AAE98704F754CF65F29A13E5185F89B40C200E5G2B8650EE072ACAA2598D4FCC3941D83D7BEAC93DE9D04FE6000241143CE20036C12CF722AAE2FB9C73E9368733599A777CEC1DF24D36E665A6BD241CE5D03B5C8C47477067F2F8B2DFEE20B1544A41E0
	9FBFC171EB8703FD7CE9AF7A78FA482B7F8C4F4106CDF33D5B907F9913017131CA243E951FB1BEF98AFE1C6213D5F8467F953A06A569CD1057F591791739A271DFF1B150B62CDCE344F26B034AB17F192EA36B708C484B3F14123797208940849085307C927A518F2B1AA379119E8FC6FA1D4A4F1660438D1477F77354B3F7ACD239D517824B31B9E7E4BA4C5E978DAA58FAF12FD53DDECC3D44560B42D0CADEA1G5B0F38FF947747CA06F0BFA2015E2DB0C1FA86A0F6885B727190D9930782E553567127A1655E
	8BCA2F9D52EA4B0CFA52169BA0EF8B11D904B9912097408590ABE2DB3ED3DD9729AD6944836DE86431DED0A3B2B96FF14F2F8D6F0FDCCE54AA7719E7FE769C8DA9D79A14DEA7EA790CE1EB15F04D7BF5F4AF15795372C635BF250B6CCEC83E8162E3837A53DDCE5C5F2AB120BFB550BDA397249783449B501F764A73533FA224DC307EAB8DDA7E64EAA47E148CF91BGF20CA0B7C0AB409040C2A37A53E835BE12BFA957F6FA1DAD00EFF3BAC7B50686AEEFDA083CF3732B7F9F315CF223E63FB9CA74DC85F96B81
	B2G72CD50F7GBAGDCA65473BB559147C0151E114740873D73EB4FD5A16502470745A6ADBDA5BD51B39E72D6812482E4G94CA20ABC02F04FA3E75C9C3A4BD79789039AD4746675716D7A31613A52DBE3B3DA74CDEB5DBB18AF8AEFAE5BC789C1C1CF3C5C31D7192764DCEBB691BE4AFE12504FD30FAE2FE7A34C4AC1791248F1321EF90E5489DC699FEB1CF99FE9B31DC5330D6395BFC7363F7EF44F2FD1A7C86A667474F9A311CCB13DF49547C7855C6AC37F8D84BC71F3CCC7A19D6AC82F3085EAD095CC70F
	4D8447A3E43FAD9E6ADCB10CBE7A38087148BA45BFFE97A2575C46155C69C863CA0ED92B5C3210F2F36377C0C4FEFBB479B9E66647EFA624DCB03FAACDFE651767476FE8C839E0FE9D1A7CB26749CFF7A592BF13D94B9F57EE8D630F38C3434722E9B346AAB3E6743515A1BC67A66B8FA24A3A422CB59E3EA8218C6B9499E62E4ECF06DBC34A05783BC5CB060706D106BD961421C9181F8CE7C24A854B502BA943B18B4AE003FCF677785F68597DF2C82FD4F02B6A89AE8D52190A2C56C6FA3EED4534072649DA07
	25DFE9688B12E12EC06A0CC443F894B64E1747DC6E7932B8E6576170F77F5EBB2CFD77EF6FE16D3BFF9F9D5E4170D0077A06BB66D79C1E7DEEE06DE12D3B01FF267ABC2BA3713E5B6B102DF60932E57DD27211739408363A3D8E960A914E1E607DA081633B7B1BC9BB16D9B10ECBBEDCA10446F16C148BAB6915C4F7677030922C12A58C4A7C431A7076FB2AC65BFE29B55A767BBFB5335B6F7F564CEE3FC33561E5292A55162536D6DB16265A59E5E92B8DA78B6BD3B1741EC3875859E545FE7622877B59C28517
	6BA53884C82FD2F01FD2DCB22453157E57B2CE5AADC3C91F26E9B2573374B35E797615F010216A270C47CB1FBB8FF5BFE53ADF26F7B98AC6E09DBC023AB9A5F22F308C529DG960005CA5E8C4DEB63F71F48FDAAF2370965E5C9C4DED2260C1709A40FE58EB401B679936531156B12B747F2F365417A7E0BB2395B9DF287788C2CA2853C1FE2F665F11AAE5959D5297869223A70FDA23ACE3BCFAC2DA3F734BCCE2F5BA8857B7D3DF53B15BC7F0A0E84GC49CFE871A1617C537D98A69B7DF2165D9DE3872DF2FA3
	EB990355E4F5731B30CC4772B1350ED50A5D4D47081DB4E2139E65D8D040B9F1501A4B5BE079886E252C9CA1E3EB9A24B38172C751FF8A6C33456EFBC783FD25E69477197750772BBAC659FEAE81065FC771FDAABC5B67FD50016FA81AA0EFADGEB77477F13341D8B06A549C397532D22ACF2DB88EE3485B9532DD06C7B3B7A706D1D55205D5E2FB504EF6FED8D115BFBD7C37876FE2FA1F2FB6FEF88B716B2DD1FF22B5B3DDA4147FA54B628D47C61EFCA435C9B6BF1592CB2F187668BE90D688BA5B674853B05
	6E53C2FA86A056063E90671C4D9792ED614E559E227BF42BED413E70ED0ADFE78B760596977A42FA482B37ABF74164B7508643EE273D251C5BF72AB1FCFB5F50285D5EB7B706EF6FA50D115B7B1E46706D7DF70D115B7B4B0D333777CEEF002EDD95685BC7E7716D26B03E5DB40BEFB74D41371BE6716D26B978F61376D876F32F4AF721B531CE27BE3C0E63FAED9DE77461F5DC200F2CE334BE3C0EF769A36B38CCBFFBFBA60C2B7BA99937D97F1B1944FD09F8BB763FE8FA57E4BD24B3GF29D587FFA15313848
	616FEBD57CF79BBC8781DC2ABC465BB62166DDADA8678403D85FC2D519B87EBB911E2B81D2C36AAB72296BABD7705F75857AECA547AF1D8C44D7F13CF1AA90DF4DEDF2F98657EC9B9DE813670608CD0A1CA9F9B5GDDCE3449D322FACC32B883472099E76818446751E9356FD5A8781F287127BFD170774F2869AB953C65329ADF25608FDED1637DB60FB2EB0D21B19AF2864A7B4B007ABED16A5BA4E95537BCE2FD8FDFD557F7D8296F9B43DA752D0CD85F04202E2FC629AFD9B3FED897313E5F5412F735EC6224
	53ABC39C61A2BE70E69D6E73ED00721B810AGDF65E267C5EC5EFBE802785430DB34C377230B3462B1D276A775D8B6966893G5682EC84489D4379E5EF8DA96B1FA1F1FE79CAB3999B143E9D90F3FE59C362D213B4ACFAED30FCF2FA24CE97FF859B70891477F7BD5847DD10E6727CC1F95FE8932485B768825084B064C6F95E7772FB4C22CD296B93AF760DA8054EC269E42BCC5738B1EE726D8AEF3B2048384FC05EBB62375069911CC3E681A10FA1CCDB292D8D743EB4AB7F35137E72AD53743DAE75BDA4A52F
	BD27A0B8CFEF33313C788F362973689DACAC37F4E8C7C89E94D47CC634225F2402BC9B5D017E32459DAE56A899A563C401A778FE43A794DF64893E5F7068885EEFA8033CD88F2EBF775BF11D3AD9413551F75E7648A9F92DB20EBFFFA1D3901779206A778A47673DCE8CDEBF6D901E7C9F6071FCA3E907BED93B3F5A0270D1B2467F4F5398B2D6E6765E2A3443A60ACFD061D9BBF8ACF8D7EC956495F951667F64D4768B3CE873EF3A7CF6DE2D7A5D6E3A7E76B07C8F7038A164FCA6496B37739AEFB85F97685D52
	75DEE65B5794FCBE45E728704C66CBE91CC1523963A9F936F13479A8757DE4C8EFD2F00974BE07B0813FA79077268D5BE60F02FB021635C1FA2102339A88AE8E52C98ACE9EC49EEB94DCE2B672D82F60F664A00FF485775694722872A1AE4D03BC5AFC4A77F9C24EB154B0688BE747CFC5FA2F4097BC068CD0FC14AFF88CF9E188471098484BD4E47AB9BDF36A1B845BCEA2AE7D826A9323606E216785ABA03DC641E5533D73E4C8EFD0F0CFD25CA6C8EFD1F0AFD2FB67CE25646D1E4277FB2A6BF07D3497F003D3
	B8B7168F852C8949AD4A5A5A93EE2B29D9B4F750191647DFF5393CFC6C9446DFDF25F52E03F4BA4016A9FC674B77A13EE315780F7789F18B345FED5B50FBD9D7B84D6CDDB36D3F733750711C0E717C0FFA64A9C3FE62B49D039B5477C409FD8F6730BB99AC3FBFA01F4CA764CE34BF7F3600FCA26F1D596A7CFF89485F2AAA4FF4FBE29F6A366509A441764842305FDE4A64BA6D1E61776E526F4DCA5ABF1D4D663A1F51FBB56A97F24EDB9DA66784CC4564ABAF322734353D272B336758254EB317DABA6087639F
	BF213ECFD9737A0D1A6F17936862FCCD7C39656DAFE6B13B6E15512E4BE6502E5DF230DDDF8E305BC2B9582EC7826C768DB9582ECF8464EF14036D7AB84FE776B8CD5F0F115DDE0969797400DA4F02AF5AF4389F902CE7AA57EF430C5F07D8BF7051BD2D424B30BE380CFD6347945781E9D9419D9C417E3778B20E939523C41E95CA3A1E26497B2B7CEE6708FA4CA7F41F7F187FDCG0FEB79C64BDC00451C0DF60CB913C107F4001CAB90BB8334DE41D8721263CF70943D904413EF85C96C2D8E4FF1D81F717752
	D89CBA6D40B854F485ED3B196EA549100ED1F07FF1946D3DDC416552B339CCC867DFC59B3FC8F1C5102ED1F03F22B872FE9FF9A7195FFB35FC9E73AF6123CE5770B4633174F361915C06DB79F361D5E2039FB80B50672F067A130B63183D62689E41BA40ED380AB65CB50DF64D09C25B3CCA6779E2C8B78174A97837A81E3CD7C35EE9A76F3F737D3CE9351E245C6761EBFD1CBF6371507475706B57847FF943CE6136C1B2AE3B5C5230C45E646931CE11F5907353D8BABF4AA0CFD4947A787732711DA236567977
	8E0178E3949DAFED2D1CC72206BD3203E0B550248210EF4C1071EAE314FFAF1371BAC077B86C224FEA775A29E4CC2ECCAAD77D8D501EB720DC3F76D0DA2BA308F6A90D1FE4200B82E03865B4FECA0474CAG26C741993F9E45B928C77A8D21FAE4856931798626473BBCAF7FC63F9E0C1F1E46DC3B813F77C614618DBA37F4C1FA70C6744BFBE7147175C6740173747B0C6B1534C853643B857C1E5650750A297DBCF562756201B65CA9EC91491E054169261B07EC4FE22781475C8C10A5F781442CGED8B58F79B
	58387C4A852499823C8C908B3082A0F5817A460366C01F15BA85056F1F06837396F779735203723E7A29BF6F8F76403C273A858147403A3A395E6FD777C9D8034D335C0D57DC0E4564176B49581639A078EC61A60A9796861FADBC244446791067DA086F6CBC54C35AA0955299G390BE08D8A5084B038886F89E41EAC93A25C776CF53ABA24F14926BAAEE77A7D217EDA75E33151E32FE18C3A625B8F88ABEFEA8D7B8D8357A29EFB72E7718B77E611A7532F75AC51EFB564258188B741FA8120842075A6542F7E
	94FF3706DCFD504B32E834502FC6A897A92C52049FCDB657D85D66F3F41D837548CADD8BCE116FEC1D93C7C2EFD3G324B6DF4C9EEF912E169EB305C8F8EC43C1B9C8CCC8643F9E2AD467B71D37B427186ADCEC29C4E5E326D954DCADF3843CA3EA1B68EC2D90D220D9D1AB60B6EAE274DEA1C64E36083B41EA46FE9465E047DF223157037B85D56A927C38EAD0963E78E0DBB497D6FD4A84BE2006611A22EC3614D21E3F4B147B1BEEEBBB9936989FC47136F171B6C9863575C0CB17ED2FBF00CFFE716BAC6DFE3
	8F0E71C9F9FF7EEF6CA1EB270072450E60989F4B737DBD8746787C7DF3FAEEE5320EDB21CBF4B80DEC071459261846087DBA70E1101F4DDF85CDB8FFC5E958C6BD4752774101A68160ED1EDEF5F370BC7C3E05749BE8A670DCB56F4396345D5A1B31CDBB6837A2D4A7677CBD7E61F7080F04F915013745CBCE5C6B5D225477210B4D8B326C34E33F9EA940FE3D672BDF9252A64F69308D02F52DDA4C5F79DF6CDF73787D621FA6C37749FFB00973C95CE236CF1E844FB50B593D580C450426F49AE9529607BBA379
	CEDE109D78FB63CB68F75F78CD714ED38AFDB20D43B83E05FC3E543F1F6C6897943AFF98A5F7779C52C41172B2E72B95069472E151624355A5C595558770FC6C2C8D6DD9F3CBCA5E7B343CA050FDA27AAF63E8A88EF9FE7902A0203D1F4FD75B3B75272DBA0C59C24651DB18BDD65F6237F73EDC144D6A5CB168576322E1AEFA30F25F7A4CDFAE5FB477F2E997D5F64B1E3B5D7AF350EE1B43580D557F47CBFEFB8D0BFEDEBF764CDD46DB8C7EF22F5866D20E757124996C63E537066F6398673782CD5F2D7EBEEE
	3BB5380F679B48F79A145B6165B8675799F0FF4E87E512EE457D309F7E09486AFFEF093E2954CC266FE69A43B5CB22836D394000765CF8AB6E0915DF20E78A37411A95206BB63CEB7E41G52DAEE437138FFA0F8BCFEDA88582B9A18FDBC7E1701607178A7816371AF87420D47ECDEDDC05C1F7F0ED67D7EE65D755F4FCE3E2EBC581C7F6D1E70F7869E6B513EB370439E6DFB0FCF764CFE4772191E705F2FCC6BD1FF3F1245A67DC652B671608B2BGB6GE48264C6E32CB4527B970146DD9AB157B113D942FDC2
	64FF7AECF8DDFB4FC67E0674E7E7C9BFE17391299F714633243DE4270BA2994EF2165848EDB5DBE4C64A710E33F48FDE9A26E806331D5B26369B8B6BB83D679C310115FD3B52BAC527609EA0F418D95FFD4CCA6E912877C8D573CE6939E35D3D6DAD7A0ECBBDAD5D1D9D9D4D7A6ECB7A0E737A7E9E256F3FBBD2B9CB79364E6E768B1DE7FA35EAE0FDF6E4847B6C5EE81C376F9B49E6764077E6E63D8B27852C1E170F12E7E5D9996725EFC2DE0351F31D53F03C5EC4BEE9426BBDE3599722B31EDD2BCC5655515E
	2B7445DF875C610A857E8951336F7BB09F7E31D4844FD5C03B9EE08BC0616D30FE85E8826885305D0E7E1CFEFE3658DF91113A35DB3402E373EF05CC625AECB154357AF69CCFBF194298ED736DB8761A68F7E04A1640DA9AE0E8895276CE23EDFDCB700CE2E4D4BD771379BC5CBEFE5292E60B54A5013196335D851B5AF605F7007EF74C45F6F71849BCD78F34FDGB3G31G89G2B81D281B682EC81A80C01988D20892097408260830886080FC17BEE3AF0E0967B72839D15F5D17E0BB9EA79D7C0FDEBE3E617
	1F5974799934E9DA4C357A6B28D5D0786B8D117ACF611DDA6FFEF4C6AC977CAE88AB37D833DCD9900FD6D51A6D9D5CCF67753EBB0377949E24F8531D41FB8A07AF205F3AA0AF75CE5CF37AF0C04D3FE6E9CADE4752596D4D5AD7B4069F5FAD4648637B1811FE739146655EC9979F1B470DE41D609C9E76C84ABCB2EDACD0F06D1EAE2707FECF185FB79FD14BBE84F24B91E4E7650EB7294BAD06B24BA214E37B062F3B084CC9C05B100D386D943796F0FF4DC11953247036F9C10AEC1BCD12765C77ABA9F46E7B2D
	24BD777DDE8A1D7BF44324DE37E4868B328BC28C1FBBCC469A36BEBBADBAC4336A1D5F4C086FE1EA5BD89771FDCA6DF2F557DC0E797DFABA5626AF8D6E8F4FD3FC4652607EB0B2057DE18B648D5D057D21BF60BD46B500DFFF57DC464FB3A3E42D3A89E8F3E3E1FC84E8856883F0456218783743E47E8FB7A6F6BB1DB86548B0A4CAEE556848FCED4C067E17257864388D7DAFCBE97BAF19437B5F8366487E7730D95B7F3EE58E753FC74D5A7E37469C6AFF0F1B4978EC06313FCF21FBCA4175AB381F50BA896E3C
	55A4DB947C4F28DC845FA69106087FB7AA57844575F1DC3A8A574FF13FB0137185D436B85DD876A53AE7BDEC3559F89F23B4D6878CB70ABDFECBE5343AC4155CDB94DC3F52965E1C5076194AD1378F73317F97F067E3A178C7DC6C6CBE465EB3D83D73CDAA1B4DCA3C047EA595054F5F5335DFA25433BA964FB91E69A738D4C8EF0AC57F7EAA5DEF60773FD43F9BAEDC7FB556396B4A03695CB000EDC0561E7C6F388CE09BBC2D505DB402F4234AD8FE5B88527D2D027B2CA934CD8DCD6AB6E56BF47143FCC1D9BB
	D037E578BD3EB77DF8F4DBEE0FFF1D725B7FC555EF575475EF0BDB2EAB8FE6A757B45A6E89451E5EE9345DE382762573A361472B0BA31147AB730876F8E59F899D2F5CA35A6315EFC4BDDEB1D9FFBF935A763A4B6A36E7EB1CEF98C8DB9B1DB6B22C70BD5BA7E9FF946ECE494B3FDB7936E5BF61EDA01BB7A468D1E8F3AEA85F0D399B4FC9BD24EB737AFF54CFE257007D2A96422C387C1332928295555587F00DF90148C556296C6F0D8CDD0A534138706F42D6D958F14C67A2DFEC04693F084DBD3270967FD38D
	C57E3F55A08B2F320DD1D950F51E12052D74622BAC64100B333210CB9EC5FA9BE467519C725B63965E8A5DCC53AF7959CF93AF35966A84DD3B702A05FAFF3B30F502F48F9F3994C97F2E82145D2E14ED27A36F06714A5A6C973E142D93F220A0EB59F6A107B48F14F733FA3221E5E9ADB9EA700C89D7E80C4F6A0E14FA7D1A4BEB1788453A05ED014D15F57F2E8585F7D4FAF5C26EF16155E151663208208D7D9EDE077006012AC526FD30EC817DE66247D076E3DA362EC3509921E55E7563293F315C9E17E81400
	A44FA43AC7E98DC4243787043CE13759C099C0DB8E9C9FG36B4C532F7F5882F4B96091F914810D62A6EA2C1874F2823A6967E989F7FC72AE8A1CD25272BD3704F257A274AF1393E9E10BEF3E6C6CD493F49F7D5FD37EA73A2E358BBE4F1F17C3D0FB87F5A1745DF0D343FED84FC4E3D98EFAD9149B9D838B3369E3B88BEAC5AC6758E93B9EFEB1E14A57F7ED90B01546FC19A1E4972CE9A085CA32AB27F85D0CB878826516B1CE5A4GG9CF2GGD0CB818294G94G88G88GABFBB0B626516B1CE5A4GG9C
	F2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1FA4GGGG
**end of data**/
}
}