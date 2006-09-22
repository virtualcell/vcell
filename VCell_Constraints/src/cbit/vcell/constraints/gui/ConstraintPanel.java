package cbit.vcell.constraints.gui;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.constraints.SimpleBounds;
import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (5/14/2003 2:00:45 PM)
 * @author: Jim Schaff
 */
public class ConstraintPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private javax.swing.JSplitPane ivjJSplitPane1 = null;
	private javax.swing.JTable ivjBoundsTable = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JTable ivjConstraintTable = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.constraints.ConstraintContainerImpl ivjconstraintContainerImpl1 = null;
	private javax.swing.JButton ivjJButton1 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.JButton ivjJButton2 = null;
	private cbit.vcell.constraints.GeneralConstraint ivjselectedGeneralConstraint = null;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private javax.swing.JButton ivjJButton3 = null;
	private javax.swing.JButton ivjJButton4 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private ConstraintSolverTableModel ivjconstraintSolverTableModel = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private GeneralConstraintsTableModel ivjgeneralConstraintsTableModel = null;
	private boolean ivjConnPtoP5Aligning = false;
	private javax.swing.JScrollPane ivjJScrollPane3 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private SimpleBoundsTableModel ivjsimpleBoundsTableModel = null;
	private boolean ivjConnPtoP6Aligning = false;
	private javax.swing.JButton ivjJButton5 = null;
	private javax.swing.JButton ivjJButton6 = null;
	private javax.swing.JPanel ivjJPanel4 = null;
	private javax.swing.JPanel ivjJPanel5 = null;
	private cbit.vcell.constraints.SimpleBounds ivjselectedSimpleBounds = null;
	private javax.swing.ListSelectionModel ivjselectionModel2 = null;
	private ConstraintTableCellRenderer ivjConstraintTableCellRenderer = null;
	private cbit.vcell.constraints.ConstraintSolver fieldConstraintSolver = null;
	private boolean ivjConnPtoP7Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.constraints.ConstraintSolver ivjconstraintSolver1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ConstraintPanel.this.getJButton1()) 
				connEtoM2(e);
			if (e.getSource() == ConstraintPanel.this.getJButton2()) 
				connEtoM3(e);
			if (e.getSource() == ConstraintPanel.this.getJButton4()) 
				connEtoM8(e);
			if (e.getSource() == ConstraintPanel.this.getJButton3()) 
				connEtoM9(e);
			if (e.getSource() == ConstraintPanel.this.getJButton5()) 
				connEtoM14(e);
			if (e.getSource() == ConstraintPanel.this.getJButton6()) 
				connEtoM16(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ConstraintPanel.this.getConstraintTable() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == ConstraintPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == ConstraintPanel.this.getConstraintTable() && (evt.getPropertyName().equals("model"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == ConstraintPanel.this.getBoundsTable() && (evt.getPropertyName().equals("model"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == ConstraintPanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("model"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == ConstraintPanel.this.getConstraintTableCellRenderer() && (evt.getPropertyName().equals("generalConstraintsTableModel"))) 
				connPtoP7SetSource();
			if (evt.getSource() == ConstraintPanel.this && (evt.getPropertyName().equals("constraintSolver"))) 
				connPtoP2SetTarget();
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == ConstraintPanel.this.getselectionModel1()) 
				connEtoM4(e);
			if (e.getSource() == ConstraintPanel.this.getselectionModel2()) 
				connEtoM15(e);
		};
	};

/**
 * ConstraintPanel constructor comment.
 */
public ConstraintPanel() {
	super();
	initialize();
}

/**
 * ConstraintPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ConstraintPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ConstraintPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ConstraintPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ConstraintPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ConstraintPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoM1:  (constraintContainerImpl1.this --> constraintsTableModel.constraintContainerImpl)
 * @param value cbit.vcell.constraints.ConstraintContainerImpl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.constraints.ConstraintContainerImpl value) {
	try {
		// user code begin {1}
		// user code end
		getgeneralConstraintsTableModel().setConstraintContainerImpl(getconstraintContainerImpl1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM10:  (constraintSolver.this --> constraintSolverTableModel.this)
 * @param value cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.constraints.ConstraintSolver value) {
	try {
		// user code begin {1}
		// user code end
		getconstraintSolverTableModel().setConstraintSolver(getconstraintSolver1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM11:  ( (JButton3,action.actionPerformed(java.awt.event.ActionEvent) --> constraintSolver,narrow()Z).normalResult --> JLabel2.text)
 * @param result boolean
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(boolean result) {
	try {
		// user code begin {1}
		// user code end
		getJLabel2().setText(String.valueOf(result));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM12:  ( (JButton4,action.actionPerformed(java.awt.event.ActionEvent) --> constraintSolver,resetIntervals()V).normalResult --> JLabel2.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12() {
	try {
		// user code begin {1}
		// user code end
		getJLabel2().setText("ready");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM13:  ( (JButton3,action.actionPerformed(java.awt.event.ActionEvent) --> constraintSolver,narrow()Z).exceptionOccurred --> JLabel2.text)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		getJLabel2().setText(String.valueOf(exception));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM14:  (JButton5.action.actionPerformed(java.awt.event.ActionEvent) --> constraintContainerImpl1.addSimpleBound(Lcbit.vcell.constraints.SimpleBounds;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getconstraintContainerImpl1().addSimpleBound(this.createNewBounds());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM15:  (selectionModel2.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> selectedSimpleBounds.this)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setselectedSimpleBounds(this.getSelectedSimpleBounds());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM16:  (JButton6.action.actionPerformed(java.awt.event.ActionEvent) --> constraintContainerImpl1.removeSimpleBound(Lcbit.vcell.constraints.SimpleBounds;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getselectedSimpleBounds() != null)) {
			getconstraintContainerImpl1().removeSimpleBound(getselectedSimpleBounds());
		}
		connEtoM17();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM17:  ( (JButton6,action.actionPerformed(java.awt.event.ActionEvent) --> constraintContainerImpl1,removeSimpleBound(Lcbit.vcell.constraints.SimpleBounds;)V).normalResult --> selectedSimpleBounds.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM17() {
	try {
		// user code begin {1}
		// user code end
		setselectedSimpleBounds(this.getSelectedSimpleBounds());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM18:  (ConstraintPanel.initialize() --> ConstraintTable.setDefaultRenderer(Ljava.lang.Class;Ljavax.swing.table.TableCellRenderer;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM18() {
	try {
		// user code begin {1}
		// user code end
		getConstraintTable().setDefaultRenderer(Object.class, getConstraintTableCellRenderer());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> constraintContainerImpl1.addGeneralConstraint(Lcbit.vcell.constraints.GeneralConstraint;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getconstraintContainerImpl1().addGeneralConstraint(this.createNewGeneralConstraint());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> constraintContainerImpl1.removeGeneralConstraint(Lcbit.vcell.constraints.GeneralConstraint;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getselectedGeneralConstraint() != null)) {
			getconstraintContainerImpl1().removeGeneralConstraint(getselectedGeneralConstraint());
		}
		connEtoM6();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> selectedGeneralConstraint.this)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setselectedGeneralConstraint(this.getSelectedGeneralConstraint());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (constraintContainerImpl1.this --> simpleBoundsTableModel.constraintContainerImpl)
 * @param value cbit.vcell.constraints.ConstraintContainerImpl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.constraints.ConstraintContainerImpl value) {
	try {
		// user code begin {1}
		// user code end
		getsimpleBoundsTableModel().setConstraintContainerImpl(getconstraintContainerImpl1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  ( (JButton2,action.actionPerformed(java.awt.event.ActionEvent) --> constraintContainerImpl1,removeGeneralConstraint(Lcbit.vcell.constraints.GeneralConstraint;)V).normalResult --> selectedGeneralConstraint.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6() {
	try {
		// user code begin {1}
		// user code end
		setselectedGeneralConstraint(this.getSelectedGeneralConstraint());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (constraintSolver1.this --> constraintContainerImpl1.this)
 * @param value cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.constraints.ConstraintSolver value) {
	try {
		// user code begin {1}
		// user code end
		if ((getconstraintSolver1() != null)) {
			setconstraintContainerImpl1(getconstraintSolver1().getConstraintContainerImpl());
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
 * connEtoM8:  (JButton4.action.actionPerformed(java.awt.event.ActionEvent) --> constraintSolver.resetIntervals()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getconstraintSolver1().resetIntervals();
		connEtoM12();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM9:  (JButton3.action.actionPerformed(java.awt.event.ActionEvent) --> constraintSolver.narrow()Z)
 * @return boolean
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private boolean connEtoM9(java.awt.event.ActionEvent arg1) {
	boolean connEtoM9Result = false;
	try {
		// user code begin {1}
		// user code end
		connEtoM9Result = getconstraintSolver1().narrow();
		connEtoM11(connEtoM9Result);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoM13(ivjExc);
	}
	return connEtoM9Result;
}

/**
 * connPtoP1SetSource:  (ConstraintTable.model <--> constraintsTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getgeneralConstraintsTableModel() != null)) {
				getConstraintTable().setModel(getgeneralConstraintsTableModel());
			}
			getConstraintTable().createDefaultColumnsFromModel();
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (ConstraintTable.model <--> constraintsTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setgeneralConstraintsTableModel((cbit.vcell.constraints.gui.GeneralConstraintsTableModel)getConstraintTable().getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (ConstraintPanel.constraintSolver <--> constraintSolver1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getconstraintSolver1() != null)) {
				this.setConstraintSolver(getconstraintSolver1());
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
 * connPtoP2SetTarget:  (ConstraintPanel.constraintSolver <--> constraintSolver1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setconstraintSolver1(this.getConstraintSolver());
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
 * connPtoP3SetSource:  (ConstraintTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getselectionModel1() != null)) {
				getConstraintTable().setSelectionModel(getselectionModel1());
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
 * connPtoP3SetTarget:  (ConstraintTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setselectionModel1(getConstraintTable().getSelectionModel());
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
 * connPtoP4SetSource:  (BoundsTable.model <--> constraintSolverTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getconstraintSolverTableModel() != null)) {
				getBoundsTable().setModel(getconstraintSolverTableModel());
			}
			getBoundsTable().createDefaultColumnsFromModel();
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (BoundsTable.model <--> constraintSolverTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setconstraintSolverTableModel((cbit.vcell.constraints.gui.ConstraintSolverTableModel)getBoundsTable().getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP5SetSource:  (ScrollPaneTable.model <--> simpleBoundsTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getsimpleBoundsTableModel() != null)) {
				getScrollPaneTable().setModel(getsimpleBoundsTableModel());
			}
			getScrollPaneTable().createDefaultColumnsFromModel();
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
 * connPtoP5SetTarget:  (ScrollPaneTable.model <--> simpleBoundsTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setsimpleBoundsTableModel((cbit.vcell.constraints.gui.SimpleBoundsTableModel)getScrollPaneTable().getModel());
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
 * connPtoP6SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getselectionModel2() != null)) {
				getScrollPaneTable().setSelectionModel(getselectionModel2());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetTarget:  (ScrollPaneTable.selectionModel <--> selectionModel2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			setselectionModel2(getScrollPaneTable().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP7SetSource:  (generalConstraintsTableModel.this <--> ConstraintTableCellRenderer.generalConstraintsTableModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			setgeneralConstraintsTableModel(getConstraintTableCellRenderer().getGeneralConstraintsTableModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP7SetTarget:  (generalConstraintsTableModel.this <--> ConstraintTableCellRenderer.generalConstraintsTableModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			if ((getgeneralConstraintsTableModel() != null)) {
				getConstraintTableCellRenderer().setGeneralConstraintsTableModel(getgeneralConstraintsTableModel());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
public SimpleBounds createNewBounds() throws cbit.vcell.parser.ExpressionException {
	String identifier = javax.swing.JOptionPane.showInputDialog(this,null,"enter identifier",javax.swing.JOptionPane.QUESTION_MESSAGE);
	return new SimpleBounds(identifier,new net.sourceforge.interval.ia_math.RealInterval(),SimpleBounds.MODELING_ASSUMPTION,"no comment");
}


/**
 * Comment
 */
public cbit.vcell.constraints.GeneralConstraint createNewGeneralConstraint() throws cbit.vcell.parser.ExpressionException {
	return new GeneralConstraint(new Expression("1 < 2"),GeneralConstraint.MODELING_ASSUMPTION,"no comment");
}


/**
 * Return the ScrollPaneTable1 property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getBoundsTable() {
	if (ivjBoundsTable == null) {
		try {
			ivjBoundsTable = new javax.swing.JTable();
			ivjBoundsTable.setName("BoundsTable");
			getJScrollPane2().setColumnHeaderView(ivjBoundsTable.getTableHeader());
			getJScrollPane2().getViewport().setBackingStoreEnabled(true);
			ivjBoundsTable.setModel(new cbit.vcell.constraints.gui.ConstraintSolverTableModel());
			ivjBoundsTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoundsTable;
}

/**
 * Return the constraintContainerImpl1 property value.
 * @return cbit.vcell.constraints.ConstraintContainerImpl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.constraints.ConstraintContainerImpl getconstraintContainerImpl1() {
	// user code begin {1}
	// user code end
	return ivjconstraintContainerImpl1;
}

/**
 * Gets the constraintSolver property (cbit.vcell.constraints.ConstraintSolver) value.
 * @return The constraintSolver property value.
 * @see #setConstraintSolver
 */
public cbit.vcell.constraints.ConstraintSolver getConstraintSolver() {
	return fieldConstraintSolver;
}


/**
 * Return the constraintSolver1 property value.
 * @return cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.constraints.ConstraintSolver getconstraintSolver1() {
	// user code begin {1}
	// user code end
	return ivjconstraintSolver1;
}


/**
 * Return the constraintSolverTableModel property value.
 * @return cbit.vcell.constraints.ConstraintSolverTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ConstraintSolverTableModel getconstraintSolverTableModel() {
	// user code begin {1}
	// user code end
	return ivjconstraintSolverTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getConstraintTable() {
	if (ivjConstraintTable == null) {
		try {
			ivjConstraintTable = new javax.swing.JTable();
			ivjConstraintTable.setName("ConstraintTable");
			getJScrollPane1().setColumnHeaderView(ivjConstraintTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjConstraintTable.setModel(new cbit.vcell.constraints.gui.GeneralConstraintsTableModel());
			ivjConstraintTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConstraintTable;
}

/**
 * Return the ConstraintTableCellRenderer property value.
 * @return cbit.vcell.constraints.gui.ConstraintTableCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ConstraintTableCellRenderer getConstraintTableCellRenderer() {
	if (ivjConstraintTableCellRenderer == null) {
		try {
			ivjConstraintTableCellRenderer = new cbit.vcell.constraints.gui.ConstraintTableCellRenderer();
			ivjConstraintTableCellRenderer.setName("ConstraintTableCellRenderer");
			ivjConstraintTableCellRenderer.setText("ConstraintTableCellRenderer");
			ivjConstraintTableCellRenderer.setBounds(781, 80, 168, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConstraintTableCellRenderer;
}


/**
 * Return the generalConstraintsTableModel property value.
 * @return cbit.vcell.constraints.GeneralConstraintsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeneralConstraintsTableModel getgeneralConstraintsTableModel() {
	// user code begin {1}
	// user code end
	return ivjgeneralConstraintsTableModel;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton2() {
	if (ivjJButton2 == null) {
		try {
			ivjJButton2 = new javax.swing.JButton();
			ivjJButton2.setName("JButton2");
			ivjJButton2.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton2;
}


/**
 * Return the JButton3 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton3() {
	if (ivjJButton3 == null) {
		try {
			ivjJButton3 = new javax.swing.JButton();
			ivjJButton3.setName("JButton3");
			ivjJButton3.setText("Narrow");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton3;
}


/**
 * Return the JButton4 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton4() {
	if (ivjJButton4 == null) {
		try {
			ivjJButton4 = new javax.swing.JButton();
			ivjJButton4.setName("JButton4");
			ivjJButton4.setText("Reset");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton4;
}


/**
 * Return the JButton5 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton5() {
	if (ivjJButton5 == null) {
		try {
			ivjJButton5 = new javax.swing.JButton();
			ivjJButton5.setName("JButton5");
			ivjJButton5.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton5;
}


/**
 * Return the JButton6 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton6() {
	if (ivjJButton6 == null) {
		try {
			ivjJButton6 = new javax.swing.JButton();
			ivjJButton6.setName("JButton6");
			ivjJButton6.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton6;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabel2.setText("status");
			ivjJLabel2.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			getJPanel1().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 1;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJPanel2(), constraintsJPanel2);

			java.awt.GridBagConstraints constraintsJScrollPane3 = new java.awt.GridBagConstraints();
			constraintsJScrollPane3.gridx = 0; constraintsJScrollPane3.gridy = 2;
			constraintsJScrollPane3.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane3.weightx = 1.0;
			constraintsJScrollPane3.weighty = 1.0;
			constraintsJScrollPane3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJScrollPane3(), constraintsJScrollPane3);

			java.awt.GridBagConstraints constraintsJPanel5 = new java.awt.GridBagConstraints();
			constraintsJPanel5.gridx = 0; constraintsJPanel5.gridy = 3;
			constraintsJPanel5.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel5.weightx = 1.0;
			constraintsJPanel5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJPanel5(), constraintsJPanel5);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButton2 = new java.awt.GridBagConstraints();
			constraintsJButton2.gridx = 1; constraintsJButton2.gridy = 0;
			getJPanel2().add(getJButton2(), constraintsJButton2);

			java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
			constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 0;
			constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJButton1(), constraintsJButton1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 0;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			getJPanel3().add(getJScrollPane2(), constraintsJScrollPane2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}

/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButton4 = new java.awt.GridBagConstraints();
			constraintsJButton4.gridx = 0; constraintsJButton4.gridy = 0;
			constraintsJButton4.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJButton4(), constraintsJButton4);

			java.awt.GridBagConstraints constraintsJButton3 = new java.awt.GridBagConstraints();
			constraintsJButton3.gridx = 1; constraintsJButton3.gridy = 0;
			constraintsJButton3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJButton3(), constraintsJButton3);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 2; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel2.weightx = 1.0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJLabel2(), constraintsJLabel2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}


/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButton5 = new java.awt.GridBagConstraints();
			constraintsJButton5.gridx = 0; constraintsJButton5.gridy = 0;
			constraintsJButton5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJButton5(), constraintsJButton5);

			java.awt.GridBagConstraints constraintsJButton6 = new java.awt.GridBagConstraints();
			constraintsJButton6.gridx = 1; constraintsJButton6.gridy = 0;
			constraintsJButton6.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJButton6(), constraintsJButton6);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
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
			getJScrollPane1().setViewportView(getConstraintTable());
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
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane2().setViewportView(getBoundsTable());
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
 * Return the JScrollPane3 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane3() {
	if (ivjJScrollPane3 == null) {
		try {
			ivjJScrollPane3 = new javax.swing.JScrollPane();
			ivjJScrollPane3.setName("JScrollPane3");
			ivjJScrollPane3.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane3.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane3().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane3;
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
			ivjJSplitPane1.setDividerLocation(250);
			getJSplitPane1().add(getJPanel3(), "bottom");
			getJSplitPane1().add(getJPanel1(), "top");
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
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane3().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane3().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setModel(new cbit.vcell.constraints.gui.SimpleBoundsTableModel());
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
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
 * Return the selectedGeneralConstraint property value.
 * @return cbit.vcell.constraints.GeneralConstraint
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.constraints.GeneralConstraint getselectedGeneralConstraint() {
	// user code begin {1}
	// user code end
	return ivjselectedGeneralConstraint;
}

/**
 * Comment
 */
private cbit.vcell.constraints.GeneralConstraint getSelectedGeneralConstraint() {
	int selectedIndex = getselectionModel1().getMinSelectionIndex();
	if (selectedIndex >= 0 && selectedIndex < getconstraintContainerImpl1().getGeneralConstraints().length){
		return getconstraintContainerImpl1().getGeneralConstraints(selectedIndex);
	}else{
		return null;
	}
}


/**
 * Return the selectedSimpleBounds property value.
 * @return cbit.vcell.constraints.SimpleBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.constraints.SimpleBounds getselectedSimpleBounds() {
	// user code begin {1}
	// user code end
	return ivjselectedSimpleBounds;
}

/**
 * Comment
 */
private SimpleBounds getSelectedSimpleBounds() {
	int selectedIndex = getselectionModel2().getMinSelectionIndex();
	if (selectedIndex >= 0 && selectedIndex < getconstraintContainerImpl1().getSimpleBounds().length){
		return getconstraintContainerImpl1().getSimpleBounds(selectedIndex);
	}else{
		return null;
	}
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
 * Return the selectionModel2 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel2() {
	// user code begin {1}
	// user code end
	return ivjselectionModel2;
}


/**
 * Return the simpleBoundsTableModel property value.
 * @return cbit.vcell.constraints.SimpleBoundsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimpleBoundsTableModel getsimpleBoundsTableModel() {
	// user code begin {1}
	// user code end
	return ivjsimpleBoundsTableModel;
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
	getJButton1().addActionListener(ivjEventHandler);
	getJButton2().addActionListener(ivjEventHandler);
	getConstraintTable().addPropertyChangeListener(ivjEventHandler);
	getJButton4().addActionListener(ivjEventHandler);
	getJButton3().addActionListener(ivjEventHandler);
	getJButton5().addActionListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getJButton6().addActionListener(ivjEventHandler);
	getBoundsTable().addPropertyChangeListener(ivjEventHandler);
	getConstraintTableCellRenderer().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP6SetTarget();
	connPtoP1SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP7SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ConstraintPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(519, 627);

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJSplitPane1(), constraintsJSplitPane1);

		java.awt.GridBagConstraints constraintsJPanel4 = new java.awt.GridBagConstraints();
		constraintsJPanel4.gridx = 0; constraintsJPanel4.gridy = 1;
		constraintsJPanel4.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel4.weightx = 1.0;
		constraintsJPanel4.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel4(), constraintsJPanel4);
		initConnections();
		connEtoM18();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ConstraintPanel aConstraintPanel;
		aConstraintPanel = new ConstraintPanel();
		frame.setContentPane(aConstraintPanel);
		frame.setSize(aConstraintPanel.getSize());
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
 * Set the constraintContainerImpl1 to a new value.
 * @param newValue cbit.vcell.constraints.ConstraintContainerImpl
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setconstraintContainerImpl1(cbit.vcell.constraints.ConstraintContainerImpl newValue) {
	if (ivjconstraintContainerImpl1 != newValue) {
		try {
			ivjconstraintContainerImpl1 = newValue;
			connEtoM1(ivjconstraintContainerImpl1);
			connEtoM5(ivjconstraintContainerImpl1);
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
 * Sets the constraintSolver property (cbit.vcell.constraints.ConstraintSolver) value.
 * @param constraintSolver The new value for the property.
 * @see #getConstraintSolver
 */
public void setConstraintSolver(cbit.vcell.constraints.ConstraintSolver constraintSolver) {
	cbit.vcell.constraints.ConstraintSolver oldValue = fieldConstraintSolver;
	fieldConstraintSolver = constraintSolver;
	firePropertyChange("constraintSolver", oldValue, constraintSolver);
}


/**
 * Set the constraintSolver1 to a new value.
 * @param newValue cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setconstraintSolver1(cbit.vcell.constraints.ConstraintSolver newValue) {
	if (ivjconstraintSolver1 != newValue) {
		try {
			cbit.vcell.constraints.ConstraintSolver oldValue = getconstraintSolver1();
			ivjconstraintSolver1 = newValue;
			connEtoM10(ivjconstraintSolver1);
			connPtoP2SetSource();
			connEtoM7(ivjconstraintSolver1);
			firePropertyChange("constraintSolver", oldValue, newValue);
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
 * Set the constraintSolverTableModel to a new value.
 * @param newValue cbit.vcell.constraints.ConstraintSolverTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setconstraintSolverTableModel(ConstraintSolverTableModel newValue) {
	if (ivjconstraintSolverTableModel != newValue) {
		try {
			ivjconstraintSolverTableModel = newValue;
			connPtoP4SetSource();
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
 * Set the generalConstraintsTableModel to a new value.
 * @param newValue cbit.vcell.constraints.GeneralConstraintsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeneralConstraintsTableModel(GeneralConstraintsTableModel newValue) {
	if (ivjgeneralConstraintsTableModel != newValue) {
		try {
			ivjgeneralConstraintsTableModel = newValue;
			connPtoP1SetSource();
			connPtoP7SetTarget();
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
 * Set the selectedGeneralConstraint to a new value.
 * @param newValue cbit.vcell.constraints.GeneralConstraint
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectedGeneralConstraint(cbit.vcell.constraints.GeneralConstraint newValue) {
	if (ivjselectedGeneralConstraint != newValue) {
		try {
			ivjselectedGeneralConstraint = newValue;
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
 * Set the selectedSimpleBounds to a new value.
 * @param newValue cbit.vcell.constraints.SimpleBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectedSimpleBounds(cbit.vcell.constraints.SimpleBounds newValue) {
	if (ivjselectedSimpleBounds != newValue) {
		try {
			ivjselectedSimpleBounds = newValue;
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
			connPtoP3SetSource();
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
 * Set the selectionModel2 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel2(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel2 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel2 != null) {
				ivjselectionModel2.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel2 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel2 != null) {
				ivjselectionModel2.addListSelectionListener(ivjEventHandler);
			}
			connPtoP6SetSource();
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
 * Set the simpleBoundsTableModel to a new value.
 * @param newValue cbit.vcell.constraints.SimpleBoundsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimpleBoundsTableModel(SimpleBoundsTableModel newValue) {
	if (ivjsimpleBoundsTableModel != newValue) {
		try {
			ivjsimpleBoundsTableModel = newValue;
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G59FC71B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FDCD4D57638EDD6EC51E60605C90505C5C5AD3BE1D1D13165169615EE36DAD1D1D159C6C5C96536D4CF4581859CD4CCAAAA5A2C5090C1C191D0C1C831C891D17927B2B08FE6EAE046198131E56BFB6E3D6F5C77664D9B86262F3F4F6F73797D78F81CF9671EF36F39671CFB5E39775D7BC6487EACF84942C5590230F009A07DEDDDA888A1D789423C3EEB67A0EE26DD279794FF1F832C906C4DA970
	9987E5C12BD375D9C2CBC19ADCA741F519BED53F863E2F920E95148541978F6DE4C15923EBEABF9F3E1D8B3F46F6D26975247C8C78DC8E30GF0643356210376EF4A4F1170398E06C7D838C890721C0466217CDC896787B441G31G0992FD9A50AC25AD1FD5DEA65135DF98AA4C3EBFBAB513542328934164E43C7E5227A05425167413365E26F2921EE9D0168CC0BA3FD828DBDD841F158DBF756C686AB4BB8B873AC40B25304B5A67F05A8D66BE272330275FDCDEDE43918D06BE5192CE6B5C39AE0D56A18803
	F0DDB9C0F06FB512F61260BA832E578EA07E16D6029F835F8381428730EF5B3F2E14E4EF7E4FAD423C8FECAB749E7AB6FD0075A9F1C06EDB550E6B97FDC86DB00B6A3B6024CE5FGE08470838885188E70B1357F5C2F5743E7F52733465A5BEB6D2B3659F6D90C769F2CC651825F4B4B21C442B50ADDD63BD190180D5F4DAF5010E7B8E0FC696BC64B47F4923A996D9D56F82390F5D3FCEA2A06CE22C2E724461DE43AD8F0D25689EB3B56E35BB9032E7E685A7691C15976741E866AA76A36631B9A7485D2BDD503
	F25B01034456FFBEC8EC5D815F899F99FB8CFFAF4547AA704CB7D3A81EDCE71F5269E71EC25FE8DE0E325427DCA9A496BD2F2907413B9FCF757F1949907233DA16735AC95DB3FFE6ED6E61325CC271267F48F8A64B9D54CF4935B0843E7B0BCE4F7C6B7175E42C8E0E516983G42G2681448388E7687459E7200FFD1E3FF104BE56E030BBF7751BADC651AEB05E4773BF00CFF190865ADE98EE063EAE713041EEB6F4DAC407A051BC17CFF4506942B70F7AFE973431C76C936D8627595A87359BFAC527E8F7B01A
	5773D7909A3BC8CB1B4C3DA2B450EB93305EFF662F02CF0B4161BCE4B39A1C22CC414A972EA3B699B09C069A2182784E7412DAC074D5827D2F87E867FA98DAC76A7BDE34C334A8AF2F37DAFF6A37B5111E88E1E790BD375158E1036FFEE7200E97EFA038C438CE7B9D56F3533A150AFAEA4DDDC4FC037D940E314803444FF3013E8C2083E0ACC0B040F400C4006CB3F57AA200860041B35137DEEDC65F2A4D3CC148F874B9CD5FEABE54208FBC1379D478196AB196C35BA6724F053285GB9E3C18E00E600C100C0
	00C80039G8B4662BDE35941F00D71D7A6492E46A7CA3841B33547ADC2070AF73AC6DD888D2A3A32256BDCC9F7C59AEDB15937E94ACEE244D28D3CDE7ADCB30C5CD83772BAF3985A552A6B9552A7398F25D31FF167E17EF5221078EA4ED9BAFD89C053D968E32F2E4BE3E3F61741A162A85DEF0781A69AE50FE37D3F305DDF12F72954DE26544F95D25BEB2476BE81788CE09D40B7GA4AE9483EC86A885588A308D2082E0BB40C1A2036403C41FDF83FC893081E0A7C08D4037820BCDED8209540250426DF28715
	4B82FD8AG0882088748B89B7A8A508EB096A08CE0B6C0B2405AF3E04C82B88102G2681A4GE47BC15C83B0827883C4G4482247CDE27AFGE882988288810881C88248BDD7272F8530G84GC41D0B3EFECFEB51A2F9DCAD7122BB74158F256F28C334631712DED0279BC620571D12DE157A53722B0352A76A354D0B4F7EFFB8FD40FC27310D78CE3104A376A5E35C138FE1ACC0BFA2F1C299F708DF907FA03EC546BD719174AF125BFFA1387B9771A162DFCABF5212F70374097ED573FF018E7FDF8596536357
	931F908744BEE72DE8937B0C7045A1153D3C1E780E14E7F5DBB94F6B94EF033C2C8692A187473F3D1E78154169341BBB7B1D62C15169B4777548656F53369C7D1DB6B9254336F45F90FF351E043C0C5327FFC3F29A18E37509746642D92472CC1AD309963197042E317677B999FED58119FB7EA41E12F23AB585BA0164E416FE129F3279DC38010C891B5D5C8B37AC12E28AA56711FC4B3EAEC72123F8084FE4DE1A86E53967E1CEF4728B4C09567EFC2D90FC7C4ED42D5CFFF04A1329C367317CE05CF9F2CE44E4
	CA5B20AD7BF21A5BC9F88713750EB422B75554120EF168DBB64937FBE8BEEB11C99CB4072469B423DB360D653EC8G24CE78946DFBFBED16E2C663D8CE6D830DD3F5D36EE2291D232BBF64E522F10F3ABDC110685ED87911CBBB872D168151AECB03F413CBC98E6FB0C36B62AE683E5161CE734696F7F9B6C9E57B36E5AB64B9282807157B15FC6C22B3DAFB8D4C599B090F5B414B98DD1EE305063CA41632DC683A0D44CE61E03FE9G3167E1AEF46E86D20E7936148455D8AD122CD0566CCF7C694326EC499F12
	00B7431F7953F2DEFE81AF8F066BC83772A50730FC8139BEDFDDFE9DAF4F0332B237723C43D86E04B2FF37720BF979ECA8CBF0AB4FB80265C5FF50696B7E20AE7F93AFEF06B21B22BC6582D27EF5B3169388154A936113712FEC4572FA20B7DEA073B3BD1FAC4C22F6BE51AF42B40F9871FBBE2EFFA9DC29AABBAC0DF1D6FEF6A1996396451C2AF29CE9B36BA82EA7B921BDFF00308B583CA46182C9768B895DECCE6787FCA4E78C9E47648B9F4768D8FD7E47102E8178CC974A7579DDA84D7D47933A2A63C8B797
	7009G596359DC2888BEBF6575AD6BC0BAFF29BCE4BC1BAF91BAD93E67B8DD62F8164326DD44644B3D085185DEC4684EB2A01D6DA216737ADDA457D7485B5D5F09F47192DD72C5DA6DAE6EC23A6CG16AF2B5BE575CD61F485C0D78BE08CE0733B10GA51D5908F453G9F2B20CBF6217BC4646DCEE039B9112FG3E57CFD052BD4A695AA7C8F93C024E8940684E58426DAB5597BC0151C53B5437EDAB524D847CBC00340B19FD6BAED66A65DDCE67FFB11BAB04DC4C7492F131528F2616A15D8240E7DD82F30FCB
	589C386192259F7C3A8D69BA247241CB58BC1950496DB617F37DDD4266A5E80F58CB147DF8075365876A74D501F2BFBA82157EF2E3855205863279CDF4205C2E6CAF0315DCAF929D51333BFC855B11EEDEA01BA72965E3FAF9085389935978481948568CDC47C7408E6ED793197C241C5055CED47637EBA75219A6B2FF9AB3112DB90C1B28EC7763AA240B85FC92C04E250C0E504B6DBE43698AAEE56D12FE12750B5ACB157505D773F6AFE5769DF3A923F3356F40AE248B381469A36CD2566FE964FA92581C5AA3
	F29F5295CDE273483AC9EC2D24F9923387EB776C7D3C5DC9CC9F4D925D18C92CDDC6F738015BF792EBB7FA9253CF6CA425FCF98710AEC1D1CE7ACDBEE5FFF90153258431F9AD31AF214F8DE27231BCE6BE4D2D689A7A0F068143E02163A4640765655FA9564AE369FA97212F8A42B5F116AB8D4275B80050A04C43E6E5621AF840CF21C208E93F5E53FAFFF49013B7BEC84E436217D6ADE5777824A252665A4BE09CDE067777059B156BF2558E07354BCC178425FBCB78C6E51E46F0D3B7AE034FEE335D615C8BC9
	46A05E07EE5FC864A5EB0692DADAC32DEA42E78DF5506E184B70D9437C935B0591BCEBF0CB42DCD2A8E91D3AEBB36AE85C4FD3042A8973D253BD68A872B2261B391749BAAA78652DB4A66B4DD4D612B3E5DD8E7EF7B94AFA0E29E4F8D9154FC4DC12CED6EF41A12CF78C54998910F8B93DA79CA2B9BC39BFB23AD91C2E648A3897DF016DBF1FD3682DFD0DE415553967B06ABF846A0B65F5BE373DE2A47A2F711C64CAEB643C7E6CE0387784E37D3527360E247E035A49B12BFB73915447D828B7AA9875E6BBC274
	C66EA70CEE6E9114A189E88AA6238CD7E42D7326B7E57BFC8CDF52D2B9BA5D38A55EEC1C9FEB46F1EE1C0C63793F3B1563B9C87F00C7DF8D1A4CFCB4EA326C2BC32F1D107A3C2D95F5138C65A5D722EE86DA09EEC82C13680EA21D85E8C2GD84E7A66074A1CB516602F52697381581CA17868A678B450C43F10FDB40876EE2B3DD7B4661348D6E8B869AC24F36A42EADA7CAD793E8EF57869CA32BEE2B03ACF23726BBDA8550D32E25DE1BE46EF7834CA12B56646AB05F99D0F24EABD9B343CB4B53572AA26AF63
	D5325EE6DEFD7D22F8G2613F729CE081CE1C0934F7BECBCC6F4E730779433BEC42C5AADC84FC944FD62C9771E2C9BF9D7309F7F7A847B3136B6C448C93AD5339F84F2C324E748A1F2BF42B25FCE0B86E07D18FA8CED9BGB4B1A1E85B834734EC1BB28547C5535ADA78340B3D56817137190749E1BE0EF264C09B8DGEC1DA126D5394E608778B00019G71D3D0673F9EA2E52E73DF1C9BA79DA776F01A4C8E263BB72A15BE90187A38266E6A2EF9A1B5F98A53595A2BE55DB5CD3DFE11E9AA7A40668E145989B4
	E1D723BC17F610B6ACE60773A04AD4C8D7D5EACC063E9E4977253B3A647E75CAE2B68AD89B99867433F828BF779A54FF768E72C9EB65B1E75F46BA012CF1C8B5E9ACBF0C465BD87BB79A300FCD50367FB55847551D6867CC2FC3AD2857B1B191420215CFF87449E957B0FDA6DCA36BB5A8627AC56191285741CEEC33B29466A12158679D87883E8F6207812224235F62146E0DDDFCAC82EDFCA8462D073A301E5CEBF57A26EB51375F4BD07B36BF1405DF0BFDABB57E3638B52FFC859D9B8E91929927E887ABB972
	E99CE2F20DB722DC53204DE4AED7F30F16DCA557E17FF505C457FD863B5DFAB2FFDDAB2FEF37087555816D186B303EEF521575C5DE476A0B3D8E7BF917C84706B8A86971BD91751888B4795703CEA8BE209B77E5F4DC0F3A3DEE8B528682EE6675A863D569246F62E017E8A30A595F5555EF370BC681476397DBDD5B6C5C0A32270541DCAB8CE5DFEFD26BE2904A0242D07699DBFF1B0D8EFD3CC5207796455D5BE358D25E61698D05497ED5996AA09C641A47E5BB3F4C759EB079AB52963F8730FABC8FC5175630
	2D274AD0CFF9B740FC65860C0F7FCDD74747F1D09681B07D8614676CA31E6323F99B46C756CE68B6ECA78E78F3EE44BA74657FFBB16D0CF254DB83546FF7A37A4E272BF63846AF25A6C6292FAFAA309F51D07F029B512F9253357C2A680FDC06AF3789F8DF7349B52476677076EB216E21BF229EF7D422EFB03A4915C89781B489FF4471F5E8BB6AE8ED38CE5F9E0E783737737BB560E242315E2DED0436D71A53C8F9758E2CA365CFB0C77C9352566DA44F31BC2CF1CB3C7EBB115789FCE11CF77ECE7497262F21
	5E1D54371C6AAC5F23465404C4EB2C4D5AAA6CDBBC34177BE774715CE6350FB7C159A0007F1FD12EF31A5D7B3F2C9A7B9082F4311CF6C235777EBFD60D32E45F84F3121B10F7DE3AFBBBD76C42F64682DD94275555903C394F6CB49BAC661F457CF5CCE71B6A3FE7BA2B953B8DF097430CBF3F1E86AE8B8C28C27A442822DEB92A1CE406D068B6CFA07AE3F13A228663F452CD68AF4F6C43FE94C4687446886E837BA5BF5C442EED7BDD63409F77A3DF90704C66FC658D5E1E23B07EDF9B10BFE32ACEDFBB95797D
	DB3384776788523317832EB26CBD00F58C81FF44D46C133E91718900DBFBB3566D57B81CDD59B360E75C7631355FAC6FCD31A93EA3307DDDC3F4EDE56C4DECFF57920EBF07628395F8A663728DBCA701322CDBF0CF520A92624BCE389E87908A908D9087A0C4621ACACD91F9B6BD6C1EAF1AD77499AC87DD9E92625E2BA2220F1F44D352753BC54ADC1169E1F713EFFB901BC64D477ABDD0CA62EEAE74318C2081408460477B7C3B6282EFFD463DEBD2E719BCD1D41E50486177B2397763FE4A97BDEA3ECD07FCEB
	2F6A50E85BE3F9753AD665DE5D782C40D4E2DB2D3CBAD12ADB38D53D2FEC0E0FB2BFBFEA19995F8F07FD732DB69F796E7111EF160FFC5B0E78264FBDC7FC5367D3C7FC13B341C73E2EE6AD3EAA492F1AEEF5171F0D717C2DE40C1B20FCACC090C084406CDBF10C4794FB0DEB5F8EEA46B5A6574ED65F747EC32BEFFA5FF9541B7E12D43A105A734A17F31BAB9F4B8B7487099EB5567DF775BB1D74D9B94B078E9954FBAF0A203EA6G53EDB8E7AB7888578D475C06771EDF687334C8381E8D10G10F63BCEDFGD0
	87E0399D6D7451268A2FF6A269FC13154D2CEA54BBCFA4BDACBE2656037F6D2E7D56565F8AB73EC255F5282A9E563F7768F340E8A80B85C882480A021C8120814099057D33EC2A7656BF3E5D3949DA8378BA2B75A777DE223C373B49BBB2BF196115AFA04A150F59566F985AB6AC8A77C5DA0F7BA643FF3D7245C7E9796A0F7F71700C0AA6024CCF930FA97DB4B90A79E97E9DB09739837DB4258D7B52F487BE473A122E2B7943F588C0549D683F7D74F96BEC38CE84C83B93F2BF00BAGA3005F1DE85F68CD3B3C
	59379AB7DF018F63D78D83CB713E43B75D6E714A97FC27965FCA03EF63A64F0DCFBDEEA2B55B0BF0639BD97F6E764AB773CE2DF1BA1FBE8F1F87E5A9F741BC8F208C209DE090A068AE34E376E62FF6D40CD36FF67C255337FE7AFB650B38CB0BEFD317EFF62CF263D35BB1C6333D59EEFCA36B5F33DE7962D56D31715D582D3E8F11338B49C01B950D633BB49347F7FEB4462AC023EFF2DE6D152FA3DAABDEDE5003B28CF2996AC55FE4E8754A979E2D95AF67F7FB08177506CEFC265F19CE64A4F97FF4AEE7CA37
	EFBE34520DCF6DC342DF3478A63A71E9C10E02177105EF71CD4EBB5D78BED5DD97E84AE95A6A1B7D863C72557EC54B57376835F22EF620357D85FD5DE0C2BF737B8B5A2F204CB73D143B71296D972D2917F8B73E1169658D2FFCF3B57592ED553A47A782ED4A5D2817296928175C3B719EFF66F2F20FEF02EB8B40583BD1DFB7EE73CD7EA82FFCC1F7EB711DD36E1BFDA63871296D9321595E3A8A5F7A3755ABDF4C5DDA316F6B8C54FDBC577113BE4A7032D73E5CFB34782E29742D3D29DE79CAB45BEB5B6E1BCD
	4DEEFCEA1B56EB36772E9B5F487A173E439B1FD1333DFB5C78C6563FD9EEFC6AD8BB24595E781D3E35976C4627EEAFC0333D6F2AFC53E733D73E30FB34E258F11BBA06CD833AF9G4977E08CEB627BF03227E18C7BF79B09E175F0ED84981A0663AB2B5AB7FDFD6A4667262FE9DAFCCF3B710DCCDF2FF8658B53ECEF4AAE5F7AF7139BDF216AFA1AE6FB3B77913ED8D559F04078DE5C671BDE5E744A1728A967547D3E35F70FD73E343FEA711D5560DBFB97F9654B53ECEF7F815F5ABB6A152FCC333D550D3E3577
	05D73E86D5FB6C8C79823A275684E5C3G8C7768B784978C5711FF45717DBBFA76A8962E13B86EFC0ACB3B176D31E0386C8D4DDE647084318A79127E2A6D772C0D8DBE3731D871FD09879AE9BD30447359374FCB344F3EFDDD22FD76EDC3498A06974979ACF905974BCBCB06BF9B373D44735938DFCABD4BF9D629361C6715EA4BF9E16970F2CEAC75A467A7D437FFDFC46E8D767EBE2739D724D533F920B8C84E06D6F7BA9B7B7B583286295D21FE46A560DAC9718E72CC3460DEDC93A95A32C9F0DF937956E8F6
	DA6DD8C913E8306F6F6E668BA287EDE2977A784F74BED68775B55F0BBE7EF4B37A38D1EEE72BE7DDEE5D2A2D4B6DDB35F5D933F5F8DDFE3FF5F8DD3E68A4E7EF5D4EB76EB3740A826EE399B83AD4227956E97D0168085D5B9FEDD34F5B48987583883A976F6D679B705E9EF1AF5E5BAF257BBB9360BA65BE1DBE77BE9C53459FE94516127B58982D3B4FFD2C32BE7A15F9566785E55A7A1CD0C67669BB2C7D76AED12D3320329CDEA6AFF86DF50A3DC4AD0CE68A65F79A6CBD221B4E43A8BFAB7344BF35ECF83B4C
	78C0CF6D6AF0088E7AC0205660B4C876A8BF16AE5923215BEE6D2DA9165ACDAB772C87FD39369ED617FB56C3EE39F7BDFCD96ED98F85655E75D0DA6EC98F52794BE3CA5FA34F3824FB02667AFE3B64A3B68DDFE175DDFD9C6BEBF0DA9BB68B38065473AD513B469A5241AE3B55E2A127C3841C77659FCF136498FB9F7A749A3A5FBB8A2EE783A4F01FDE5AC664CC192EFD1F495140333D8F9F503D218553595E07059C7F994517A970525EB23A2ECF2EEB21ACF4BA461ADFABBC7B4459955ABE61DF6159A7AE2A70
	6E93C1951EFD622A8A6FBE91D6C17C3B53ECB45B25132C52BE1A8E25AF14F01B6EB5380E87BC1F7B32AB3E8147493B151EF51232DDDBA74B36FB56490A6D5EF57251F64FBA79FC3BF71D2C5F2E2D13291DCA1DEC61BAB1F7396AE43304CFB7AA694B38CF1F5B0DF1BAE3867A7437994427EA613A9D40B983FD3ABCD3EB6C794FE03E9CBC435358FB58256DEDFCAC8774E85597A155B35DE3FDE70A4B94759597E3FDBD694A7AE2A57EC469B365FE1DBE67FE57BAD9FD955DAE75ED427BFAB8558DB9CFD6F0BF6E07
	FAF10B8B6DE62C630A2DAE789254EF1F9E75DBF6BF6A5751C374EB03EBBF00607BD13F5DA6ADFDC4CAB2475C6FC99FDDE52EB6AF11709FEFD3463CAD5C668B2C98476238CC53179119F29E50690BG6A9EC019EE23BA6DF8C0BB0EB9B570ACDE3DCD57A27D9ED047B1C1CF70818F2863586F17E39C8B063214985477735BDC74DA0AFD3E335C354F25923E234205FE8B7AC6C10616EECDB1CC7E2198CF3A4D2EF40DA5B81E1E2AF4E9E7AB520F5F6EAA579689DF32C3E90BE2DE7FBBBB14E3C3464F5869D2FF9162
	4F28D256C3E2A14B497676EA65E46320DFA1B11813DDE2C33BC745205DFB68992DA4384EF8D027DF7BA05A3D056ED729FCD05B6E8D9AF8E65F0B697A0D71C1355D2F20F85B03EA3B6737215D0720AC6EC1347B1EEA25DECA39DEBE58256C3F0C0F5D65222FB29C43C77711BE57BE2453B7BD04F5FF3A5F5526B8DE9276AB6B5E4A6B0EEAD062B7F17C598714B24A74750D2E7563FDC93F1E3C8B44E2B8E56DF78A424C0768FE7775E8B7A3DC8F8104GC4BE441E7B319C65CFA7C9FD5DF6C3AF24A6F4629176B061
	CD7886F9F3603AG209E4082607FB05F537FEF427B5DC11B45CC5F5D016F5418738B31B3BB7DDAA27950DD9B14BE1474B013A1F7A644041918D7146EA077B31E37394C97AEEC46F388F5B349FBB93FA42F5A11E4FD0B6FB7760732E000E800B8C2B78B7DB3339DE3E55EAC744FD2FA162A992EEDGFE3350BF473BBDA78F1EA57BE444ACCF7E391C56B7FD16FA4F6907949FBBCB3D67B4181FC9CB0432263F617C2E0E6E850A046B699CF76DC7B8671B4DF1A14710EE6DA308DBFA9C690A9E417EEC60FBC646GAE
	6091D779C39F71541F73D87E798813FBB147CF207898851E756793FEBEB1964A2AE623CCF9F44F88B9739E42F1A217F37AEC14B357A08898D33FD4FC3F314357B51311GEB633E7F83EDDC60762CB6E1362CFB61D1CFF6381566CC590FB2FD27F27CDFA93EC001E7F6781E6249F5AD1405BE0ABABFC01F6DE77C9D465D5F91F7F3A65AA11F632E6FC13F9A64388C93520D6538DF68F34259F09D4FF1D3282D9740F55A9C44ADEBC75EEC0E0B22674C0DF0BD844046747ABD324D9DFA352E464D117593B247132EDE
	2479CF649C7598FC0362E366284760299E9C03F1D0D6BB97657BAF6DEFA8DCC7F25C33DC875366224F3EEBC5B9339F839DBE66AAD765E31E642CCCA76DB6BC269EDB3BA93E63B17558FAE8990EAD9B144DFE8CE51AC5EDC05E43D076B8629A38CD5B9F67F2AE17F54B5EEBAC796472536F772127358D56671A8C3457982E07108C54037F63F89FB823976F83310F63FD60BAAB9DDFCF40FD8E20768954D9108DED5B8EB85B932E369C7B04A75BBEC1731E40A75436FD1E62C31FD05B36171FF90F04325CD8145FC6
	577948BBBB46F05CEBAD582710D8AEE71BEC4FA90A6F05ED275F36FBCEEB9B6C5E6F5C07E73062E2693C12DECF0F4573A57B56E31E3776C97079A7B137A9266F91626BAF6404E1F9799E3B59384B50D3CFB3A56693972FCF1378831EC41F18C66B0C016BF80064A771AC7F60D7F816BF6C6AAB05C023763BB9221B9EC84DFE0A79C71974C94FA36F38FE91EBF3E581366904723027882E2AA0DB557797D358B3B7D6EEF7A9A7EBDC64DD87F2F908CBB911D76F427FF71772A58AFE5637873E443EA5BF90AE54CF7D
	1B47770CA7C9FD4AF91AFDC6AECE56855F36EFA9537B24C2973DAB563DB0D3E4BAF8FB834E999A1EC63D0F3750399DDC8783C4BE0DFAFF38CDF92EA2F162ED298BBD48B6D312A976E9D96F2C3D5C9654F9D69C6A7C3B96354EBFF75169D00BDA6713DD4AEFECD16B7C40C2E5F9DC0BDA679F6BD839346F2BDDFD1F2B0B1363CAFB1C2798F3AD4DA9EDF1AC16AC65781BA95EDF01E7E324026249F5A814E5BC4373CA3A16EE036B319C77C597461850E77833B11AEBAE006B0CE79177994511770211F70DC9FB7E0E
	FA0B8704FFB4B10034699AF36E725A062767E34345995718F37BB157794697FB09A7B5BDCB77D0FDD4AB5166E974857DF23184A5C0C3E035042FFBD61E27323AF7FFA7571DF73C560B1C4A3A0300A7C8D1F73822EEE64BDA1AE7C781BE6ED934EFAF45A541F56EF3083BD81EB7BC07363C103ED7E48C3F1EC42F49FB1C2467362DA3097D045F17584F5A289CD19B0BFD281F468ABACF5E471FB985BD475EE598811F531FE36F2CE2F34C2163C8938778A4006CF830ABC0D3BC6A70EA3A266704EBBF0E4B63B9E3D8
	BC6A6DAE1A1FA743F55673B0F7FB9E718F3035D169DDF164FDED8CFFA083F5C0F8FC69A7E9C38B1F455B7E1137612BAED3BD601788B826CC192EE34A7C3D6C778FE6225F17E9486AEDCC55BF2F9ED3136B653A3FB0E15D4D9AF52B47143AEE0B22EE69BCF2BA69C737354F9971BC1DAF519CFC903E0FFD9E57C6225ADC7BBA6D87D91E4F5BD11E859A72ACD149137102AC8FDD3FF8C1AD4FA36C7DDCD63B58E3A7875B85014975B21DE395GFD598B680F676A51C71BB9EE698A74512197509FDE21F34098380E63
	388594C75E93C85E3147F03F76F89A1F04EFB8FF0971D2AE4700C00FEDF80B73A371E52643G132B3D5E5DA75B6B1F5CFF12B4E476661BE9AF2AE3F2B6DDAB4D86FC590BBCA6D3DCBDDCBBB96EBE9B5A456FC55477AE6AFBF379F5BD3DA66F62134E94ACD36A0A5079B216073747ABAEED789AAFBC810B496F7377E4A43D485E5F1AB1CF274F83A0FDE5B139A793E94AGDF8FE0817087881B07EB8AF98D329DEF4FC0BB464C9BFD9C081BA75B1169616CDE37759CC53D42CB1EF237CFEDF43E7912FAFE18CF71EB
	DFD24F8F432DB8BFAC0132401750BFEE32395F5B6DF677FBFB0C9D7DA879A57413E7687B546A7F0157AF53EB720EC8E97D6548EF1D57113A0777A37F5F5C46F03E477C68F23EB6EC7C87FBCF7090FC0E7B87FB9FA673A39DFFE7D5A86023GE2811281F2DF666FC6236FE5585F79235865AC6CA25BE5245FAF921573122C7FDC6A713CF7654BCC9F7552675A52E49D53C3537A111C61F057DF270FFC13C64DA7BDFF790668E068E53767AF946F77327A794B7DFCDE9EGE58BDE46F3013B8F90FD37A7687403G63
	GC281268344A760330093FB486FD28DFB162C49EA2B9787C40BE20B926B5F1A0251760F5972AF29B87F0D386DBAA1706C719E5F5337A0C11A7F3E42BE9B96A56B48A76B5FE30D24FFF5D0E6847083888608G08F9857BF76B1E8256BF32952C5A69B4F419689BB77946B233F8D2C61387B32CEF499B88DF5CAB5AE3ABC905CFFB95628535112E1758A277D5753A688A0AAFFAD53DAE9AE841F551DAA88BFD95ED17CCFEA6835FFB3F077CDE42E1430F1A3B5A3E0329BF9BAFCD069E6EFB1F370C56B6AC062DEB47
	F9EA46EB9857C20F72B94BEB98377EF80C3C2F249D2E1DGFEG413CAC0A1611775111F7631177AE11F78931326AE34345A2D28FFEF73E262DFB52CE3B07B269AC5A30EDA873E7CFF98D71D3CFB10F45292AF6776700FF63674FA3DEE34F81E360B36EB52687F6EFAC6F62B929C69F1477C51A13A6423DB8D14EC9CB9255B969A5A709ED24137052BBEB0C6B9538E236A673BABF77DE85F5FCC66940EF85790F5D475B8F89820E53E7B2F01C66DEFF1530F64E7854AF846DF1BA18A86D77C8145702D2CA9FDB6539
	1DFB49AB2124BE5CEFA36F2799906DCEF3174142B6ED6DB2589B2C96F357A9A9477E1E5EDB496FFF882F633D6277F6B2D6CCD63B79E772C69BB7CE4C4F27D0DEF2363D6AF534438D434E7F55396698608BF81D5901DC073E2E36C34B81575FD8E06F0BE7E3FC208D730A8860CBF89D57486FEC43F53235EF603A5C1F697AD1BBDCBB817C5FC05AAB0F237DC25E40B535470F2B5754AEF4D9934B68D02F29113664729D9D6AB5B5D7FEDB07FACD8D79D91FA76B5D4740437C6CEB6C9BEC8CA441E75AFC367F1B69FE
	AB4F737366FB9F83A5C0D3B7DF56FD7BFC356EDB0B957EDE42F057FE2E40EDE6384BCD74F9B9BDD93AC95AC7739EBF07EB1A0F396CCB6427092472A6FEF6B11817B73448FE7C7DF1590FA73611B54DBE71E49EFF515EEEF04C7CFFD75B4D864B3F8B720A61DF49BABE3E02CFAAF31A1CE5B3B5FF870440DCC9CF8973651C064957481F372FCD529A5709BB191CD3BBE419BF6E14E50EEFA43F0140652D31DA2C76C27AE2C2E64FC8FE3E33BD095953891FFEC9AC26CBEBE9AB501E41C95A76CCD558B382E826A749
	760CCDD25B33F81942F6258C77588685EE8B6B7BFAB376FDDE925AE6C7175C37EFC690D7F65BB02E643D09F1E52CEDE4F1A55806F12563CD5445D3B62E0BB735F531C4298B20197E26C297EF2AF571704FA42E489BD915F1656B8C0CAB7300AF6FAD0C95568C0CAB4DEFE1DC390C520E036BD000E8CEFBFEA6524EFD8B634A6D19434715D7B2070FAB1FAD9FBE2E9CD86EA92EB0199278594C2C37512E86722ACCA9C7390CBEDB2C03327637B1C74935E20EB266ED4CB5565A48DCAB0CDF9752EB720EBCF6FD33D5
	19A7903A5164CB5E0035713075F44E61194FBDE79F4D3DEC9A74F36E5B324FA53C2D7639AB5712FCDC7162F0495F6EEDC6FFDBGBCC58B5007167367BD268568EF15AD04B6982EA381E6F25A02D6241D37G7D2D35F5F8FF73EF9D5E5F6EEC9D5E5F92DBBD799B4B351753773D667D93678CDD8D7F96BC645B00A46F3DB968341B7BFA5046C164A74C703930627B4C75277F3970CB27358D260F7D87BC6F39BEFCC0FB4FF57B816DB3AF1D8706BF477463814F6718CE9CD07E4697335D675D4436A5E03B86GA700
	9FC09037657B8708AD591BED54F33FEF0DBDA24D588C641D7E0C7E59C64FFDFD29517B6F463E51C85608BB2DF01B6AA575A3FEC1A3311753EA23C8E9AFFBA351115D5CE3F2B2D2893F78A099A396311B22D9FCDBD5C8778AC13FE67F93E3DE76C60CF973B8EE0305604A5E81FD3C03F1AE0346CBA3DC8FF25C9A0AA367F949FB89986EF24BF03EC4F855B82386EEF8FF3DE558B63466E3230919ACE7B9524E77A93EA36D47044F48F7D8DF135EA1B4F79D4DC39E0DBE44CBB8968F873EA431CCF11AC47A3D3317F5
	727ECB5C9F1AD82A2215F24FEE13CCDBAA4553F78C38EED379AF705DFFE10C5C5509B1B26CDF98CFED745D5F71F01D8C101D0C34BFF5613D32A0D9BB7F73BC870BA04B8512DD5EF2B93FE5043A86130737AD9BFFD752DC5E9FE8C381E2G628196GE43D2B532F85A8832887B0828C810C8388810884188E908B1078AE0E574907C8FE6CA976CA1BCAB4FEB72E137E661A7235308A9B949E7E3631E9EFCDF57D7103B50D7B6B6BF7D5B79E2F2EBFD25DF21073C759B78F43DF373FF1EF6B7EFDCDDAB50CCC671807
	7F72232C7321B6EFEBB4A96FE91D63BD57AB1F7AFCBC637B3D57772A29012DDDFDC5FF03A06FBD75FA64C60AAFF8CF3D9E99F98C7D3A8C4A025E4378F20D4BBB550477410F5F9F5E4724F7BBF7E3FD79C05F71BE7A7E4E9E9CA7016F63B869257B8CE742753C77093ED3A5DA931F4765GEEF463E472CF324D96AFD77621816AB282F80FFBADAE67C28601475F8B9F9BDF7BE8BC8F865AA8GB6C6469DA7C90627B152E0375AE04AF30AE169AF5AC9B29CF15983938B75A5FA110169BFCE0F7ACFD9182A2FDC08BA
	4D4F62FB2B881E6A7FF096218D036BE9G311C76408A54FF5242516A7F2EDED97F9B4D4ABE94ACCA55D7AD9A3E8FECFC9F357D3678F00B4D5778407A2FBF415F6737E8247D471871EC1F5C7F8D1AE7E03C470C8A2FFC5AB1436915CFBBE62C48247B23202F2EB163A30A8FD460190FDD1501BE9682E5E90BD3710C0066F9B56F636DF59739ADD05F1845A3711308DE77B52D8778391CG28036493E170998DC072A716CFEC3666C9B4F3B55AC910F02C0D1BB728F204CD2373078FED32BF44E83E13563BBFBC6E15
	CF5B9FD60D7A398233FBB81D8F8AA9EAFF381562B3D2547E302B857DA1874A06D2509F0ADBC75BBE0B19ABC5E2D707B4EFE338F55D1EF37D22EE6F397EB63A8FC9049C3D6914CDBA6B39231B34E36D6EF608FCCED1531D45F1FB9D8DD607199C82E53E303E1048E0B48F18E122D3EF6DE23F91475617EAE84E7FA6A8A18E7A4F667A5FEC4439FEE20A3B1F29677AA9CBD275B9GB857AFD8122A1A6B57DA48DC5FEAB7C35F15ED37DBB03617818F23ED5BA36730F8062E20C24695CB367B359DEDE7815E10A5F88F
	382F9D6B0CDD02770BB7A9ED36AED5DFGD02BC35A970FE23CEC57E17B1F9DA31FB0071256CFFF4CD2BE1F08BA95120AEB83EA3D8468182E2225CF72FCC276E9416BFBAC35E34CF29F79760C1A0F0D15F8BA87D820D30F21D7A8BEA5D5BD0686BA518ED9D066CC45B1E4F3F92F65DC40A7248E9F9B595CB273E39A0B52D8BB8C371262F2A49C73771BCD1E47559DA66F636ADE13769CBA4664BE071EE5521EC34FB1A967508C97EBA27E5483F308E6CE778C4735F05C8B34CE02BBE2B6BACD9C1FC065A278BA11B4
	48677CD42E1394570C3FE52E4035C838E493098D50E513550E3C8B4DF40F1B59E2E157CB4C04465C8721026BE3191948E8369994F26BB90E484D5EBB913DAA1372DA4CFDA2D9255AA376F11FAB204FA95877D24577BB07FDD67ADB0135B1673436417C704E63681BE46D0F616EB90E3EC9F052791C913D47533D8F691A675C3D73550E1A0F0D69E768BC26A4CDBD567FC171D5E96A314E4E2353BD1AD0961E06E3FD3E4B5E296C25297A7C25A3996B9FADF79F6B1FADD70EF5696C0C491D2E5F649E938EDAC85DFC
	5D0FCDCB2432D68BD943633F87C3EFC3EC05155557576E5E0623DD59866B6B8BEEE759C6E623A49F79B6E479E65B7B699C29F2295A360FD0FC5DD235ED5B335036CDD0963994EDFBF805327D5CE5297A22E5A3316DEE333BBE3FB73B6BB37B04327EDA283B59CB7D525AA289C35277BC4577EBB3CEFF2C397B343641F41516692E3F4CCC257E185D667772BC887496328CF31B0ABE1E87AD43BC485CC77320F44803GEA5311364D4A7320F44C03AE3212FB843BBFE23FEFEE1175FC1762FBDD4B69573961343641
	7A1C3511FCEEAAAD1266536D9BD1AF8169231DCF1FF9C26075BAACE4EF156B3C9ECFCFA916859A6A77B6717E1E65F616E8E4FA3A48C73E18D17331F8E226675EE35255F146CE71F35355F166DFBCEF0F07323A8C0CB389AE3F5B918678680C112CDD196CF970B99BE8938132B34137812AG1A81ACGE3B3F1CD6B18ED38B5ADEE92454AAF136D9FF4AF03F42FD3FC9F6ABC7DFEBF61343641624AB9DD6E31463FCB99EB58FE8243D744F652BD14262D8C1FBB18A963B1ED457700AA567F93955FCFF9BC87703F27
	3B7127358D69BCFC1F3B6E74FD6E773919D9F25F9FD3FC6F4CBA7DFA784FE9EDC3BA733042DD8F76956E395CB7A75C690ACE386B6B1FEE7B71C7565754BE5F782AC64D47625C13741E9A1C290EFFAFD0FCD826BA7ED97BB07EC5C2D96EF20CFF9DD6E57BE3G9F30FCA4F910E12312AF94F822C644E7BDAA706F030A6F8B3D1E477C6D7E7851E9ED03795462E368E7F396731C6A987A59798BF1DE6030FBDEC778596EFD9D61CC3B76BA426F6D6E6B88FF30EB2FA384585D5791A652FA6DE28F4CD3584BF3987E8A
	BB0935EC6D7887C31F21C771DBC84F7549F6CCD0FC6F6FBB7DB67D43E9ED03596F24555D26C3D634E94DA2448DEEF40FB17F5D280CB1EB28AEA79DBA84C9EE58AB4292277031EADB2D72D7BE9DF6616BAEEBEF2139333730FF2037505069AC94A1F7A8EC329B7A9C96837DF1D05C2EC37F96AE9216348A2B3A4D2245388F1C4B0E1462DEE18545B60A6CFD00E4D537DED863025B555FE73450D7A6B9051574E7C8B7125FA55D481A28289732DC483F97CF1D90FE919655CB8DA21E115322C13A1AAF58ECCEE109DD
	B498CFB9054555C623D350B552DF5B85F4A37949ECC06C23BFF18DDF40611D7D8E95A128C24FB7F26E25ABC8F1831B4B97BFF75562C5C24ADEC147C642DEA1053873DEE80DF87BDEE1B10C84AAC40A929CC4388CC17547315A342ED83B10693291762F13CBDF08F96A30E331205BABFCC84EF59E946D6CA7E00D64952907411CB024F68BCBEDD67293280087E19D74E30943F94A8256486EEE94CF740B8E27E8646F7185C52CE62668F71AAD9B6B8C8E93B5BBD8A3258FACA52BF9E1F5876D86715B05A05FAAAA9F
	61AA64DC2BEE57FF7979F771110B05C53B05E5962B41385B50853968DEE12953E4979DA62B45B8E85BD9C5ABCA235B6F53256EFE4C3BCBBAD7588461098B795360E57E1973B2F743C898348B9BA9A5B986D948A90CB5A6039D1A926DFB818C5064DADEF646274E2F9E5C3326FD11305035D3CE26A0EEFB7AA3393BC1EA67632F4C3872094A4484E059ADAC213FF28A8DE749D47B7AFBBBC57B59E193BEC98F08BB0F48E5A36A21467CG3C2A5B50EB3600B76AEA41C6569E0EE70B1A747223AAC1E6D86AA483097C
	54B8C81327D28596719E95DEB423E842356F34529E492CCE61B3E6CAE05D58A3DAFBB7D6F7F71B7BC4DEC145208D2A7F04D7CFE88AD5B4AB87AFDC7D5D236FF70223558A0BFB0B0A687F45747FCD647FE20AA9261862CDE04DC435233B7E294B8787C91FBF9B7468784BDDF7D4AF1C32664C2C8BB677AF9252F69345DAECA643DE49A875C2FAA79D09229DE81DC2165CFF4A8BDE3C288BDC7CE3995FC5702C74204D50C59CFD2951E07F0956C0C4AAE89316F65BFBBAE983CEE1D15B6EB6E816DE116242FAE11953
	C4A6D344831D429A396A867C01E0A828E021697D77C9F4A2FF239A7D43E3FD0FAA3F39DE017F066C2F4A7D77F34F7A13477D77F12BD95E1634DAFEFE446638E63A77356803D4FD7D87B8173D35196CA777F4DE6BE02F41E2F19AACBFD5779949592DDD271C223C7F312F05546FC09A29101575379039FFD4707C8FD0CB87885665971C79AFGG4C23GGD0CB818294G94G88G88G59FC71B45665971C79AFGG4C23GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB85
	86GGGG81G81GBAGGGB3B0GGGG
**end of data**/
}
}