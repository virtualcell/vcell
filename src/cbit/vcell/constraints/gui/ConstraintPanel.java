package cbit.vcell.constraints.gui;
import cbit.vcell.parser.Expression;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.constraints.ConstraintSolver;
import cbit.vcell.constraints.SimpleBounds;
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
	D0CB838494G88G88GCCFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FD8D5D536B8CDD8B6E1E9D111D1D1D2E1D2B1C5C6C5E5451B31061971114DB8AF1AE10AE6F8731CB7FC454CB813EF66881810282818643F3031C68D91949190655F15BFFAD592D4B49A39F08FF075F26F755E03A2227D565E7B2CFD4EBD77DCAE5C1E737D3E6F7763F3F94ED9FB6D3D56DEEB6DF5565EE71FF3053CCF4296AEC84F9304858B8579EF7782C1881FA2882B13DE7A84F1657FC84896D4
	FF9B81168BDBBAB3615884E5192FA6E4678A3925D9F05D8157E1AFA7E42F067315422A5272D0B87142A7974ADE7C7E6E101179BC7FA172D9C42F67146740F1A9C0A76048B1729F04FFEAF93E0C8F1471888B5285E17CE6C23338E21D0CCB851AAC4099G5AB7B3FA879C19G63AA4BE53A0EC991C25A2B4E74A5249DD51B8833643AE97231C0784B0250AD04D7E9C50EDC27902E4DG247399C2532AE2B8D6351C6D5D575DE5110A4EF70BD6EBD1375D66121CA60BCDF295758ED8AAAB6BB92245E4932D79DBC91B
	4395D9348DC1C8027642A96E0BB24227834EA500202D08CF5ECB70A9F0AEFC11101D7F8576ED6FBF2AE4595BAFBDA54C5E511F394CCB5F0A3FE0FDEA7FC269DBC17F1B699FD3BB7CF5B151F7B01445G448324826496A6E497832C2576DF38F8AB9CEB3B24FAFBFF3F5DD66BF054D94D4EC3F633E805734ACAA811F1C745EE3B53AC884C46159505BA720C842CDE53186BB11DC46CC4FBC79EFDD4C8FC627674C5BABAC90C38145ED448F451D9286804713E46AB6F00C2F7FFF467FDC2D0731E787B57B3966A700E
	CCFCBDA3D8EEA7C245DB58C6EC7DCB6A83B381CF6A113147706FD13CE11B02E73ED9C27164FA984A4A0B50B75A1722AC464C7B055C12BBF4759072D2D0FADA9113A1E73BD61618EA52F679F64633114B12CC7133D4F8A64BEF291F126B3EE2705D9D89594C3F96ADA5E3B529B4A1FBBEC05E2E046CC2GA3C09F4070AE7431430B370F5247DACCCE29EE40E2B50BCE0155DD1473919C45C198E84DB05CCC36EE713849E9B1F5D9C517A0536466909DF43955DB15C3FC3F9BF8B40AB651E912ACF69B34EE6A97A551
	69E2B49BF216939A27C8CB5BAC7DA2B068F788586E67B9AB61E8B53924E38E33C992958AD6DE14C3ECF25EF49CDA0486601C6925F49951D7A874BF9AA00E6BE1AAED6F206804E8D1D9E9345B4F8EB85AC8CF043CB22267909ABB66GFD6AAE54F1F1AE4135C339238C5BC94AD921EA47E069A6621B1C97F10C3DD3C67CBC906843GE6824C85485A8D639E209DE098A084A086A0E9B77A56FF3723EF9916BCA2943EFE1B2EEF953C79FA06D04EFCAA3FDCBB467ECCF99379EB202C93A0GA08CE086C09200D01110
	DDGD08350D901770C5DE5D1BA63AFCC16DD0BA7FDA047245D7A6396E12626EE718874B11A32E179BAD056DD08CEDDA6FB0F2E6C8B65782145AF130F2BC7109B5BD6DFAF9901F61566FA39FCA477214554E7BC6BB07F3AE7B97155G30C3A8C0EC857A585AA585EC4C5619DCA20E52A3CE98E022D971B8567F672B03E4F933E5FECB64FEAE17F92F16792D87A0C94B9600AFGC8DCA8815889308BE0B7C085409E003DG2DC486598709BE7F81709940B600EA00FA007D820BCDDF89A4D28B42C9EE175FD239E6C3
	1F6683EC2A045884A081048244814C85485B1310DD85E08688820886C884482C02F8865086B08C908E908F108630EEAF64A5GD600E000D800E4001CFD895925G9DG01G11G33816683EC2AC6DFCF5DDB1C2E0C2B05BEF4073E7231FC0EBAC4BBFEA66B85F53AED94FA2D16752A561F1EDF354AC7546BD7BEFC767F43558366BB5F6CA33ED3A26308FD49987766C3988B500FC81CD0479D629744BF08EF11F1CFFC847D0B6476FF97BC7D0B789071AF359F69493BCDBE22FF557FDF20437FD701457465CB09CF
	0867C51BE4909D224D8CA7AE39ACFFA9719DB94F6A3173BA9BA85E81F9D9BDA4C2AE0E5F34147815C9121C162E81C9EC95A549E26BD54A8BA9AF57C017C3C96910574E2544DF6D97A0AF637465CBC9CE83F3AC1BC8EFAE3C0ADCDEC9F3AA51AA7603507576811B4470B54B485C732CF8D14E698C4BBE90C8CEE69DA079A11B4FC5194818F0B8AD7DF04BA2A92690DACD722D47AA72D5BAB2820E4CCB9D64D80DB951053FE3CE34E9E82AB06334A3DDAF778FF960667464EA169F4C2BD6F2A2A6532EDCFD5977505C
	CE463B182C4FE5913DE92616F40C2F94D66711FC3B176633D60544C5F3C81ACEB33A8FABD86E0B84C0AA41D1F4B677BB2CA50C46456C834C293AE96D92194F572BBE66E52239D14BCF90E43AF7D72CF16353EA371E971D0AB4C8F75FAE12433BAC40DD2C036E1BDD1EB46F16F94A33C3AEBBDC11271227D5558EAB9FDF3A56CDE73475FA18339FA5BE6E84AFE3F4852E65BA7212D84AF2211F2DA0F64A2F017BBE002186F32127F2C9B9665BF292D4EF374A32C2590C9A62CF8D3B73E4FF6800EB29067953D2DE9E
	4B4B33EAA1872E55166FA84772CEA89B74A87F99AF9F8FE5E19E6507AA30BC914A52BC4ADF6665D5F5105754E94BABAA31BC844A22BC4AFF414BE7C0599CD539351E14376D41F28239752C3C9D0E2CFE75DEAC0F86DCFC3DD21F69B9FCF9AE357339819126F944088779381E36FC05266C38BC46D979934B4998372A66D4618642F3EF8D2EA7A5823FB400BC831B173499E45977933A8535FC5D8970A4E74C5A4F644B5F4F68D8FBCFF33A98404F5A2F3417AA53C5B4903A7EBA242B016BF6006186B6978A016386
	5E5E6EFA24CB134BF39A59FC0950A972659A102E3D1165300EC6A6DFE0A323931A885D631CEECEA34BF9D3D56D95F13E437B114E5844684CCDFAFC0B9B10EE3809654BDA3E2C3D84CE978CF411G71CDECFE17532C263B3D91698A81EFE8D6684CEEF4871AB85FE6161B93790261BC5A0DAE3D9969621A65BCDEC517884068366D6376155B4BBA4068B69FD037778E27AB87FC8B0063G33EF548135DE26D6F33B9DE0F3051C03CCAF6B8E2A7D40447D2F937003G2187599CB866205A8F3E60FEB5D3AECFBA4866
	49B18755FCB339DF959A593C846DE1B02A7B71A0271B88788823520F19C6353F1C627E17FB084DEFB69FD278AA7E32097BD539CCC7746CA95F9CCE57F2084D133472B13D8CF17F6BBB4446C740A136E660BEBEEA943FBA4464A76504AE72103A3F4B38DF4DBA447CA96590DBF318F7C84D77871C4EF09866E9G8107999D21D778CE607E97FC1871A57DA46B971107556DF5F07F1BF51859B765B023F3376F278710EE7EE1260F3CA32C5F05F08C84E076B09FC73A10A3EC9E99F5042D154CB8426C41786EB84179
	9EE17A18A153259CE1FC99DDFABB37EF8B633B39056947502216AF1E5335294AC93F49D17117DBCEA21D35054DEB09FD89FDE08B130F65B16FEDA1B988DDC3BFE3BAEF9AACF2DD007C30327200EA2D7C634AAC19BE2285574477533546A4381E87107BA566E17F3E845744034FC688565459995E567BB7FF4964B5FE2964E1B3A3AEE430FB7C46BC42B3884AC23F447BFB518A753ADC2D4BE56F3650A5C1795E72668AF51E46F0EF2D78900EBD9627CBEA06A4E3906FC3A9AB083CE44DD0C64BEB287D3B71D9C394
	70CD79921FB53CF7EE2FB00AE78D9EC918DB8AA52FD3F76FC49DCD9CFAC0683CEDF27AE2AFBAAAB84AF4D3F3D451D148673BB2182CEFD3D9C94EB48865D1C7D1566B7BCAC716D57DCC44AD69E46D1EAC47F6D3204D02D6707FD6FACFA8A7B9BC39BFB23A3FF23AD0A81F5D0A7C5F49AF7245DFA7D9E5EDCA7CD9CFCE9B78F49B3679765EBD2351FF3D77A4D7DEA3676D8FC35B513CFD43455D23E93FD5BFB9E6EDF7D622BE66C23B1B0E215E6E2EA4FAA37793C637F08F4A5081B44147D006FBF33778521B1ABF9F
	43F71C2C9A1BEEBC92EFB64EEF5C0363BC7E980E674B8D6A719C3B6C32D7DF4DBC4EFCF453F145D7DB627FA87BEA4FDE540D994AC30F23EE266CA53AA131CE262BC13A59C013F3A2A11B652C37DCD6672C860287188840668CBF2A5981C793CD740B5821C5F476581D7D22F9B309ECC5268BD2911DD3975552627D64FC8B6AF0438A32BEE2B2FBCE23B69B3DA85503F24F1663FC0CBF3221C616B57E517B05AC5338F43DE703C17F591B9EFE0269AB7E0422376159EF26CF84E0BA59DA1BA35BA52FBDA15B580EFD
	1ED4CBF4E7F2761630BEC42FEC906467A462E17102E7CF360C3EAB580F7757E3BFB69942057234333A7DA09058AEBFC3EED77A11BFFDD7C661F40CD7EF5522ED6783CD69C9346DDFC4BD5BDACF6238E85BE40023D36C371F973F1DF9189C3757219C8140A386002DB3B83A546B8C29004FBB1510DD8EE0BC05BA0FACA7E56E73DF1C9BEF2CA776107AACAE263BF7EB55BE9033A8C8D7F7FD8F05241BCFB11D851DD2BD83FC7B4D743C375187BA6BD166C420497B8A6579C1BD61E1353824D61429082E2A547719EC
	3D326F4BF7F5597D0DEAE2B68A980F8A837A19915A8F7C8A751F370F9CE92BBC669C5E5EA410B58E39A51D650731F89B637F06817B988B3C53F89F6B76231FB33D8E1FC43D8644C78B79ABA7F87549428E264F368EC52FCDEF3D195E71966A75017D48B39CE892BB304F7B3EA4F89B448F93C4C95700D5126F0D8DFCACFE8DFE71B5462D779B301DC04045FE0D3E3D4F21756DB4A84B7FA7762D2B715B452D144A65F4EC38C4C8E4A45189D6F2ED26F1084975C2A34AD588BC4D7FC4397EEA5313AB741F587F8F0A
	082EEDA62753FEE17316D33CBDD79336978534A93C3D8637768ACE33768C27311FF31B68589087E5BDFE510CFAEC871A09G6C9E74E2B36E4B18F99AF5FBFC9F528A1DB09EBBD146A90BC95F4541EE51C194F3243BFB4069944D820E471F6CF36739349AE5F7C09B911DA87B4BF62DAE1220AC5304328FD4FFBB9B9DDBDBA650FB0B6A6E6DB5EC296F70740642E4CF28C69D6403DCADDC360A9A77FB40FD1F93DE7C9E405A71BE945D38A12F6F72FB70F860B343047171E11BB6BE4E0332F5DD895945DDA84F9315
	5E63632ADA0C0F0CCF9B3F9FB4C17DG5E4633357FFBB1EDDB9D6AAD865ACF6DC25F5930F21FFB7CD2EBE20C7A7A893F1FEC6E06B9D6B77A55BADD3F8A6966B2FCD6A160FD4DAF571079DF4B6F8D1150F6F2B76A713775689B0C6E900F2F6B4C8959EDE69CDF8B76230E02G974771D3F8EC1C8F38D2915BFD230B50764BF39AC6EB61F1518AF4611CB62D01BC47723246AD57ADEF443A09D0AF2F876B5E5B087E4274B55CDFCDFDCB52E679DEB526A5A4DAE3BCD3F9DCB4823F409E7471451DDA9F0F0532A400B4
	AE5779CE4F7ECFEF42BE6474C25C6BC55A5AE65F7D9F570C328CC33DE8DEF7155D134F418348E7AE50ED6AC35A47A95EE233C8961355B2A4EE5E42F43643F810694CA0761860AE06997FE6A38DDCD698D0C57409519E23FAD4C9E406D064B1CFA07AE3F17AB787B0CEF774213FCCB80E7D8886DCBC17AD6D386C07BB58753A936EF1602B93D8AF531210DDE541FA3FBE616BB90AFCAFE9477A92540D64751F5E1BABF8BEC7E0E5535B5DE57883EFA3996A2FBB03FDFA76A4625BG97F4865BAEBDB512DD59B3608D
	155AFDECF1E7143DA9F3D46788ECFF5753F42EB4778C5B5F35106363A8DEB82B60E51F584BF392A89BBC0BFB12762DA63E1C885773GF22D89591B811AG7A2C382662D8C91ECD0F38670B6695B61335556DA1A16E3D4AA37AB8ABDE142F376629F3C5268757EE7F76A0DEB366FA2C5FD372C95C8D04BE068144G4C82C865FDFEAC2F50D71FF16F1A5CD9A64FFFD2F9F27BC75E4B64590FBF51FA1B47DC6FF439FF7C7A4B474A0F6555DBCE29776AE6AEBF10CEEC2B17D7374BED7775EB7715E5D678A7730A8A7F
	F4B464677E5671FE56CB75335EDF7D2C57D3691FBE25CA7F7439F80FFFF2FE6CE73D093A75A2E439E3F564E7E37C589AB246E7C179DC00CC9B44EC00AA9B0E71BF67790CEB7B87F5639A134B3A57BF3D8F6D75CF6F569ADF7A6B303937A97373D9AFC0D30F65854BDA099EF5567D6B86A409BEABE779501F7B34FBAFC2203DD800D9B61C333DF299578DD3ECF86F1936165C3B8B6CE09300B6G87C0B0C09440ECBB5A29F1559E1FF6A269FC1B1D4D2C6A35BBCFE4BD9457EA7510E6F76F373E7E76F954AB52DC67
	BA5C6B317EFD311E74EFB31499GBAG8681C281E2G929D583FBBD7557A6A9F5F6E5CE62F87FC135DFE563317A8EF0A073C237313BF7A2CB7DF53CFE65B129E34ED5EB95C9799DA671F8C8F7B2CE7BC27672BE7AEF9F9C6C593C166273F6CD17B29799C735309F08CBF07FE9A5105FD09BD074F319A7AC86E148657B9CE48271D683F77EFA0762D026BF6G87C090C094C0BCC02A936D7B3BD5F53E6CDB0B1B2F400771D44740F23C2F77CF37124FFAD9AE3DFA55867F464DA10FFA5AF1D3204B6FD79E75C6573F
	5F7B2CD76E529B278585440EADD0E685888488830883C8824814500E359F793423EA1C7A3663347D7E7573E91F7556C9FA75CEB778E747FE0FFADABB166A72DB5068DF7F16792CE75470E3637B0F7D5A7B90F9F7418C3403920E6F81870E6F0992462AFF73D34E57FC561BA969454BDDF614A1094BF03149BF992E77D9AFFFC0AFDE3E5763A5DE9ACDDD78CC7F320D48C9727E628114332459BF9F2A76282775213E813DFA5F77282787812A3A2CDE47BE7F64BC63D1EF0366BAD8D74E3CEA7F6C77294FFA1183FA
	3E5E6D544B396200F64EG7A7ADF381F25F27B4D7160B7BA3D7C5A231E56FE1B4F6B551B68E74EFA0F4FFAB5677574726F97756E716DC0EBBD0FFAF94B06FA89BC0F777828F3649E9F8B573381661EC7FD1D72B3176A73D9AF7302DE3D1D9E794F686CD36BD1CFEB1FF53A7CDE77B3CFFB5BE73D528BFA312F157B2471826A7886BF7319BBFC568B546DF32B1F790549E73DD0DDFE0BBC6E57233369AA0FFADA1BC66B721B6AE7DE71144FFA713A7C6CFE669517BD6AE9E3ED32AE3FFD9E775D5171B3FA5453721B
	2F4B6F0FFE668719BE6F4DF903FAB16C23CB5A98D688F4ADG66C10CE1751DB80E0687B106AD6FA6B1AC9A2E63811287F1FC45FA48B1BAFD3D6AD14FC3DF03FA75EE74F94F5667F7274FFAF997756AFDF9403F7E1D7628D72439AE5465776EB1D24F20A99B89D83DDB0F7B27177BFC56EB571553F442BFFEF61F759C3A7CCA5A7D63D76D335EF8DDFE6F7B49AF7B242FFAE13A7CDE74D9CF1F5FCFFC560B5170E36F106F22FBEAE7C1D9B2G432D28A2382C21046C02A19C5FAF6CA5B883DCF7F05C2B946798E2FB
	AC64F132375D079C5E00344D7BB8246F77F27E6CB70F8C5579C2AFB472FA60EA6F6F3E9DDE2D7F6EDB5BEA7DF75FCE2DDE4E70A2F9BFCBD96145722E55A33F9BF7E63577F7632665FB177371FCFDB9E3727565FCBEFFE4B93F1F6FCD4E75D437BFCDA777866700CD32740B34E9B68F9487493B2135DD5251819BDB56A02D3B344F3884DCAB7967C672CCB4F88857C43E7EF8076039A6325FEC116CCEEC24CDB4B90F7474708511D607580DBE7E905DCF9A856D4D98C29F4F61BE9E2F70D963DD1766B57A3ABC33C6
	DF170EB5A36BF2F04D483A7C0DC45E3D75F83F713029DF94F09F4B141AEC19E63FE4BFC4F4446E6DF15D5AF99B992329G1917705EDEE241FB7B3ACBF8EF2F97097D5A605A8A90F88947740FAF6B451650CBEC0CC6DD729C2B2C0FCF2E752E4F58357A7AFCE9AD59276F328FB83BC52D4EDED91B4F4B14852FE6C96CA7EAE1B489343EE4F2760A9EBA7FB92D4F4A3C55FFEB6D48F6F96523E5542EAE1768228F848CA649A45B2337F631EC0F169E273D3F3EC4663BEB3DF7BDD42D5757C35DFA6FFAE8DE6FDB8FC7
	57FB574349753E7550355E1B9ED8DFBF29D37B9EF94645FC252CC74FD762E49F192363AB2C3D57F8FBAD123DE52700EBC83D7B095EF55610DA3B1DF62B153C9DA2603CEFF5EF16AC47DC6E534F1A08CFEF9ACE482E82E89BC61FFE240B48E99D563F4F846860595E07E768BAFD70B05B7B300063DF2678B0951E6D09B0D2BC390E04325C4B98EBA2B7F87709A7B6687B44539B3C7B440C8D3EFD62158D5EFDE256865FBE71738D443F3BACE60BD3FE1315596E6EFA35AF14F21B1EB7380F87FCBFB7FF3F9B3E8547
	4956826FBAA9A95057496E826FBA59D760DBA70D855EF5F2384037CECE94686B642D7DEA1D14F11D5C5E602E131DB23E3CD1CDDF4EFD3A082FA3C9175127475909CFC542F59CC062E57469A1075E58CB3B4CFCB96B0A3731B75F0DF7859F4B15F63D7656496D94FBEDAF2A69C3D5FBA5A5585EF7EC6A768CF27DF679E805E30026CD56DEDF33DBFBBB703E7EA65D23CB5EA78B3E027B21EE5D67C63B935BB8D86D06AFC57D5E65C47D06DDC17D66D07D4E016BD4002CEFD03FFF5355C741B7CC6652EF3C69E3D90D
	3B4DCBE57C8FEA54B12F0C5BBCFB8863D853B7A8539F7A09CC81F09D829045E57A8D5569CC9D5E8492F570ACDEEDF21276D23F514631ED94BF7F9BED9C1BFE8E63D8D6G6487814A3B9FEEFA5D05FDBED3675E67DDB2BE275E0D3E8CFD23DDD7373382187C49815EF4BB235EBD1660F87A2E410D4FEE245F679EFB6A4BE4FC52FE35ADCAF87B8FB6284706029FE8F4EB3F98715B1A546D10D848F2B2E1D0AFA71B877D4A3986F3324317506E1B2EC13B9FBFCD745E815792C050B5E87726CE222F70EB746D9E2303
	E7769DC757EF622F515A7DA60A1FF30D566E2B3B516E49D056749D347BFBCDEA3D6C62FAF929D95DFF85BF7E001B3E4AF18CE79FA7FD0E04F6E3F95B2F9EF737A90E1710936A36F7AB7121DD0D5F4171BBCE2AE5D468671DF4EF9F6FCB46CA72AD902B6922FDC0920472EB69B310CA34DBBC48178C10837802EB59F3BF1623FC6F82E92F47E96A075404CEBC7282C95DADD5D8B7G6803812281E68324DD0BFDFD65D3D277C02B43EA215F6E40EFEA7C6C8A31B3FB7B35D4762179FB55BE549148E48804E3C8A066
	95DD9B497D0C67EDEE7305677760FB88D101643B1CC7B79373B3D97F4EE5CD036B2CF189591B811AG7A4621FF4E6F46D8B9FE9C7A67269E629FB360FA8EC06AB8744FB20BF6BCE7DD2778643A6B3C7967D3F48ED2FC1DF64F69CB94EF38CE3B6734D944BD276DD096FB9D4E6F5E21FB018A2ECF48AE3E9EF18DD7F04ED745F107FA10AE08639E6FC13A106B31BF6BFA31BFA9001BFF3D3B7C39633D75E7FB974DBF47B339B3B83E0262CBD5F8561F97E93ECA2E8DD096B19EE50A23FBC648BB6FB9B7A0AE3B9765
	AC3E81651C51A78898D3DFD11D9F6D73F74DE4B440F81C7D977058E9537AD55B8D0A6E7BEE70E607BF53FB7A708DCC5F0BB8FE21036673AABC33430D141EDCC7C2D96EF7D167136833FD892E83B96E0F8E3443C40E7B520EFE1544F10F5B11EEAE475DC9733B2A9B93320DB7A2EED1B541F5423503632C55D8F71863EE5BCDF071F01D8C404634F12BE273B027D6D773EED474139364CDD781346F85C15AB148F4D29A249D03AB6CB8861B20ACB288657B086AA5F782444B89083B44F5D5B881FDF669904AB98C38
	09935C658A1F60CDCE0B0D700D19209DDBAE0A1FB9C1BB365E6F4731B5874A2AEEC2197ECAEDC03E439046F117391CF1B7211CDB1D0AEEF72A4EE34FDDFD3FFF7D2A72E0FD365B51DEA9DC8F33384F265D04770172C13C8F98EE467B00E9080EAF388E86083C99F556FA89ED9B8738B9B73B5BF26E4D5EEC7BAA4DFB0409DA5B4E26785C09DA5BF61CC65B96C0D960C4147FB4FD06C53E591142F13FB0E11FF2A6211CB7F6AB768CD61D2F6F3E7A36AD3B2ABC64EFD9613BAFA5CD13088E4A68F571A4FC3F6446CA
	4C7302G97B789F31B12CE3A7F9057DF489B0615150DCE0B394E54EB24199273095768F384D2FF7EA4740985747B822537C0EC83B05F026F728FFE0E6F72C7BEF83F50B9D77F5B9CC9ED175307EFE17E91A69F497E7E527EB753994FA3D548B3914A73EE256336AACF53775FE432E7EE2C7CB137F23246C53EF520147F4A2D1C483B4C2D7E4A3D6A72052A7A2CEFBF7C8C7BB67F07D1027549C02F5F996F381575A9C0BECECC3EF3C1603C218C26773B0A5C742EDA7742CC116920F49F4E99E2EEC53DB7DF20F3BB
	384E8A06589B0CFA7F49D76A77AAF2A7DB5397F8112DBC1849E488D6744E78B5F31D8F86234EAFF868FC231BCE27FA687CBE3772B7BCF47E6582F579928F1D2F7D00154BFBG4D5A7BDCD43092D762023D4518901A3B4E89E631A41B639F2078B495DE7E7E8145136B5C5BE0BEF69B4FA169DA7A9C381E4BF1BFB803B1A677F60CB14F09743E8E5792474D24B872DDB0722DB106BBA27A0A87247ED8E2C0270ADE5EBB6413073767E3A345997718E36EF11FEF502D9FC26C6DC4BFDFDFB1483463F57A02FE19A1C3
	A85090D8A56323EED766292C6D3EEDCA5B2F7498FC4829EEBB730E04EC8258F67E9DCA5B4C16BBE91E3D8970CDF720FDBBA82E832E83B9EE9C4F47C36EC0DB6E6AA2BAC8615755741AFC4709DD7F40B41A58CF6A7B937B990F24D171487023FD9AAB68BC79DD7E4D0A4C9076AD43F5F0AC8EE15F2CE2F34C02DE24E982FC8740B0C0A8C0EC886A70G4D459261BA156362F88E12F7273296C74622992E878102B8FEB14D6749776448376248775A98FE129DF5C06A78534FA0AF7586B96F07B98FFFF539488BFE21
	00E3AA4261BE26EEACD27CFE49B9747BB09DD9FD0D2968BB35E36A656DCA5B2F58316D99BAEDEB4714366D592A3665B5E29B69C70F5DA62D1BCC5A7FBE35FB92504D3D9357C6B6D43B777557450ABC57D623BC1D13BD65D92811C71A2C48C357AFA6EB65095CCD3F4FE5F70A3DCE72E23BA0B03962689CAB984E43A623BFBE65C49F1D41F1E5927AE872E474076B285F165E85F163AE458F890EFCA710FCE30F61726D5E46A729B7123F147AA8D7E2C009D79E3E627CE8FCD9DE7B547866A7BB94FB5D62C0FBF568
	486C4BB79DF7E9E372E7F40DFC9870E1F7218D2ED0DCB4DCA7F25C1CA134CBAA37011D7ADECDA85F97CF2F49377864FC422656952173E7AC0FEC0FB5EEBC7C0D975E004564C2BB465B0ED0767DD6890E63C359F787D9CC8EF6A0CD98602381E6832481645D0DEB8AF5BB95BB7E064701523B479E871A6ED66C487430F750E3BDC755EE1F8E8F16233D4E661BF7EB67073F2478203B3573435EA11C9F06C219F08F7A4719CB1E7776AD17BD6F6D57DEC6BFB25F03FEB26BB4711BE8FE7D9F741AFCA312DDF71D7E36
	73BA527648FEF469DB73984977189FCD605F22083F07FDA7B8990E736EE15F43E4FE74879352645E1B103D8940GE086883C97633911FE1761C8579931DBAA6AA65BE5645FAF9255731262CBC62F6FFB075F4B7491AD9F672F1AAC3F6FBD49639B87239B07777AD9EF4E186BB17FFC0D4E09136F55BEFFC9247854FB354FDF9671EF744D07324EB0FCAF302F1468BB8E2E13G6681645E8779960071BEFC96F06BD6F2439C71DD32B63B43A81E972D2AADCA2CFFEDD5E36D9F33650B0BF07E9B7D4C43C24CF5862F
	5F696B3CCF1E7F4A474DEF5F39009CD97F5E5FC57A9785E571G29GD977C3CE89D0FABF766F0D2D052CFFE4ABD82DA4193A7B6897B77946B20BF8C1C11387B32CEF95747D1B267B7547D607866F00EBE62367683CBA70FE6D3A68CC0A8F39DF3BAEFA64822E0BC6C2D96E94345D8672A8CA5EE330718B72FB8947CDE7F4F7359D0029BF9BAFED26DE6EFB0747EC9B9643724DB8CF1526E0DC3BC364F316A998376E69A15FAB09036BC400D4002CF0AC1BC64B48776848377148F717483704D8D9F14FC831083403
	6709D374F5CF7844F9A9E3BC1AC764214E1F3D65B544CF3D45BC9627361BBD1F831E37E0FCDB974E1E83164231A91C69015D9BFFF1066724617AB9CE068AA420999F2E642421615A1C740E8B44B6721B7072B7EB4CDBD5389236A6F353957EDDDCE86393CA873A9414BFF69F3FC2422CBCCEFF150363F45D34A90261E706743F8B7A63B4C96ECB71834ADA5026F385995E792410ADA4F29F2E39C43ECFF3DEF4CA16EE1315ED5A2AB3B9DB6CD6CB77C5B947CE2777D6B2016FFBG6F95B51749D86933BBADC3640B
	B69EB5B1BF6F20F5493B6D918F209DC2FBC71A7FEBF34D9428B77F81E687F21D7B20568EA73FF47F0D85763DF8B64657F4E3DE318E6A35BD08EB64DD5D38CE9674A0FF16CF570F6260BA91A00D53B674227DF29E42B535AB3D5AB535C9EEEBE2D13D5AB5B542CBA9FF2BD73B2666DEBF2FCF3B260675D91FFF6C749C83AE7E5DB543C3EC8CF4405171905B7F4DF47F864F8F47BF64FB8C0482CD54C30A6E639E526A7ED4094A5FCB99EE6AC695EEA7435D53C71F1753B7CBF7487BE8761DC33D4EFA88F3594564B3
	8F02765D452C88ACF7D6A9FE5CD52778717B7B481A26CD3CD040BF3457G0E397953DA2745E47D343020847E15EE6163AB6C02BA27A97F70BB3A3F83C220A6026929ADC249E918FC5155A8DFD0045E38CE2DE6F24657ABB26F582F483C3C0C7C868217375EEE35BB0B68078919BD5F616FF746C5B0FBA642B1B50245F4E64FC3925AB3EB2A3EBD97296C398EE80A27AA76B4CC555A3364C3156DF6B15C4F3729F0E52C6FAF73F728DB2622ED0EB5A8FD2B9A729DD7CE8CE1DC99BF95634AEDC3230BAB5F9B4238B2
	F3AA6A6277C3DC978F6B6BE221DA97C0D37C30CA978FEBF57113A192D7140D2C6A3852E6473852827546BF0C31A2540EF1E5464398D7DE25347360BAF7DAC27666E9C87B0C83E9EB26E1DCC9F10C9CD7561C9BB92E984F0D9CD72E1C7396D718CC795C2E0353502E030D442EAC9FE9234F962320ACEE9A66A82FDD449CA5E59A669AEF8C1139D6DEA4DE3FC32F49B77258F57F10BACFA0F4E34917FC013C3EB3F4B567704C67A6DF9E4B3D2C907AD9932978DCDB2456676E5FC472F1550743E5FFFB32937D2D936A
	04C4228F7DCFA77A502CC87E7B34F40FCA56A3895985G650FA06D5AAE24EDF9847D2D39EBE4FF3B54B5323FBD52B5323F7D324B1B3F31DC7BB37A3D57710F601CE15C4ECF85AF79B6A049F7EFDAA52745560BB67EB93DDF307337D467CB76DC7D67421FDFD59ECC9F67CA3D6F399EAC555FF3FD25D47F1D174052115FE38AAA757E9E532D256A5F78E236ABEFA6368D855B45GA4822482E4BE0A365C380B5812FD59C6BB775BEF6E95E946E6A25F74E77419E55E7B3A384C776F46661511B562AEBB5C267AC97B
	08DFD3C66CA5599D9449F01F14919DB9AD3DFD92A315711F1511B1E295FBA81A4537967A8C37G7AD575A85FBF44777B34F0DC72F9028B036B18C7B14E3DCC678871F01D44F1BF26B872BEAF79AE81439D3BB012AF113ADADC3C8EEEE4FF350D48C3EFBEB6161849F21686B35F2778283C9F73B108C30F313EF6BCC6E89E958B304EE31EFDB04AB8968F4FFFC6E2196AED9279774E7E6B83E57FA56E8FCD5D252115F34F1EBE05F6179CCF6F31603ACDB870CBFC8CE3642FAD98A37322B01E661DA134C638B6838C
	C7A16D3233F82F8C0E524F7F3C4F41FEF8C060F639750AFA3DA09E5ACA0A9A5936EC7CBDFD105CFB5200B677FBB05F85B082F4828C8284810481C4834483A4834C8348F99C6C81D08CE0GE8FF9C476B4B65A4BF7696FB65CDA5BA3F9B7771E5326FDA7DD9D8158D0A0E6FBF5A56DCDFEBBC5DDAFF740851D8D7FB74F42D71C46D49D6DE5FF5F96788751B0E9CEDBEF564F01BDE8B2353B96661379DD2F4DE506DEB0D467A385EFB3CC5BE6BE95F0FE77536693EF7B592487B2C681CF47C635A754857A8BE78F16D
	FAE4C78F7AF59814E5BE017165E02F1AFF9F60071F985947D8FBF759303D09C0BF7389747D37EDB8CE04E87E6D811A671643F58BC0DFB45266701CB2A0FA2C63A46630E2337AF36ABE44C0DB7151A377C16F3D10A4281366239E9BDF62919A631FCC485E74A40E11076A48EF26FA9BA3ADCE3B8326BC9799167E229DFE2F4B6D5BB986E82F7D495168FF098B75EF857A70A7D12747DCFCEF5513287FEBA9ED5E74046CC2G43F4243DA2217EBB260FD57FBFEDD174BF536D7D25E0E8ABE27A48FDE0635BB6746D62
	434CA1FF63836BFF41A57EBD3FD1779FE3461F3FD47AFFCA779D985FB1234FE7BD7D18F11F4FFA7AB1E39F5DE311B6DD9BB39AA9DEF8CA9BB3E6595147F2204C71947F26006E7BEA3E475BFA37FA33213D1427C663A7E787BD57343E435F4B197F944BA772E2E08E9D433E074272095FDEAC10E9EAE2BC793445287309695BB4B9420E3179C3F12B628F4FF67A678F2F782C276F8F69A67F7C61DEBA9F6C0B517AC38445CBB1DAFFA8B121BF84C0D9F28C7AC3C157D87933183925095855A54F5B645F79ED761E6B
	B7B67B4E75DB683B05A26468ED979D723B1E471A899FFBCF0FCB64F30AD34D399C576CEA313BAC64A5D0668B3D7B088CE64BF98BCCF40C76EE769BF1ECFDA900667CFFGA5B4BD0D6BCBFDFCCFCB7B533E77C8D801A660E9E52E9F7C34F62E7F7EF9B25737BBAD50F7B56F1597B03607BD0D6BE2DFB5AAB9AC3EC3D738C741154836331951F63321EE4EB3F88F18EE46B68D4F607DA219528E43F5B0C0A4277D290871B26E99643F3407BE9331BB647553B3396A679309970F27635A00D6AF731F111F9FBE2BBC1F
	D0FCBA5C63BD24514518477D2C77DFE32E47464AF8BA8768FCD6BB06A6D13C75D96D98DAF3866DB088E5094F62984ABB2B66DF931B105D96BBF2ECE4F34B0769B3243ED84607619E2338G99477C7DC7873D0F2B5987FD0F2BB78F6A4F215FBE68B907CEB928BF077E6F036AB9B4433DF71078D3AF4CA15AB95DBC0EBB49F17FC35BA43893963354477199468FE5FC13C898A2BE5BC8643AC0F16DB2EE298AF7D2462DB41258GDD6E33BB31EE3E116EF133D82D6CFA9D253158A0D4F0FDECB49299AD8E13CA6E4F
	B90E484D3EBBB1E36592DA57EA3109E4152AD134F11F1B55A370731F2B4E3BC6FCD67AEDC15EFFF2D5F9B0BF6C6AC55FA4EBFF8C575B0B3EC9F0F2BE6371EE58687A70181F75D246DCCFFE871766C321319E6F60D2FCC42CF62C2F6679D3B41465BF07E37D0E81B57FE140CFFCEEB4E37D54B94F31FE7A1CFA2C2B5F5FF227DB6B700C89E70E1036793A9F1B16607A6291320647FF8F065E06588A2B7CFED135A70F572B55BCD8DFD73A7C3351C6BF6B1D9AF3BDE643BF51B9D278F3DA5B8A9241C7BD27356D8DFC
	7E958BE5854F23ED2F15547C83819F727CE8EC7BCB9D1BA5BBBC757959A5F57B1150768C9F6D33FA8607404F8F294EFF6AF5FF697F86B09E3F3F2ABC182ECAF574D76126BFE6370787F99E84FA4BF981F31BF787F99E748266C18597E99E845741G111CF645904F03DE40BC68409039C7307BA3767B81132267C7D5673BC7352F7F5B693C652A72E0FDBED4CD0EBBF6954B7369BB79F36F79AF0CF5BEBD39CD606DEE3DC076D6394F6B716DA95532C00B31390D77D772F817E8F4FA7A0E1F751E9DF3BD96CF5A69
	FB6F25B334F146C471B5B334F16637BCEFB7C2D9548C7E9CD5D4734FFB91663EAF0EE66D6A1BE385F02C825AF600E100E000880099G3381663E08EBDA832DA32DE9F11328D6FE19ECB71D91381E6ED01D971C397AFEDFF1D5F930387245D94FD8D3FCD69DEB58FE82536744F672BD14262D8C3FEEF009024734953F0FEAD774952CBADF7FAF0853E57F02B87D6800276E1E98703C4F7D0FCB697B8712F2FE336B6A6BA1622A72E0FD1EACF96A61DE49B3172B3A64C9D7FB49D3DFD19E7B71C75757677D2C7727B1
	57E3F1AE185ED333DE52463F3BA9BE6FA5ED7C7B7CA2463F82A88BFC89635F6AA1B57F94404FFFE9B4F950BDEE6B6D39AFA7E4EFFAF9B4757EC9263172F92F6ABC55FC757DB15B67BB1F5F5E9F5F95514FFE160138BF0968E79396603C603B473C2FA3CCBA66FB9DE172B17DF50430E31E6B888F9C53DFC718F64CF39DA10A366B94FBE11E42BE1E4370530F11D84B560E8F19EC26DE55EFA1CD3E2858F10A6A3C70625537697E2B4A03592FE8485326BB075026756908FBD2E7AE770C5BDCEEB55565512F0FC112
	9B79BBE121A42C55EC2BD57F4A274BA97C235B5EDFE4696AAF9AB85FDFE46A120AC4489D0A5A1CA61B4BEA22BF8E0A5BF5685F02F4E161A9E1E50FC5341A8F03F3B911D2EC9616D36CD111FD8F102C6A9A0555EE383A811B59CABF19A489AB684F10EEA73FCB3A1D3158E394F25D488F0A974F89D704F4234C90710C1C968D52991260F0C842C227E8B2DF14040CDA33D992BEB8CAFFED9750C749CFE68362B07D09EBB881071786DC9AC2D045B23E1133192EA0A51D5FD9197176140CF4A133D978000D04E6A113
	38F3B3F0A35E5EACE440C820C2E42A41C5044B91B4FF9C2BCF6B0EF58ACB5CB6427E5BFD3BFE9D7F0B632E8C6103E661E372DEE72B68E4BF81EBA61FC8BD8E6604A155A0E4BB6C64A7D0818F423A68E121CB3AE885EB64759C954F8D08AEC9B4736F7802A2D6B1D38CC8966B76A6132B0F1A9D2C11E983CBA9EADED05BC93BC17CF6814837124AC7EA9571DAAB1FDD7659468349B199C2FA03702155EEB2B7183AA197ED963225BE27686A33DB4D030E6A9A5AD096DDA6DFACF7F7AD6FAE69DCD19B04A7AE645941
	3B031624ACE90011B0689436D3CA729AE49127B05777191C40CAF4B683180065A6DEF64D8669739FB52E6EC8979638F78A2C5FE36A37D841EBBEB000AE6D3D54989F41A5DBFC64DEC1FFBE37817AA37D476FDE397F0D2A543950D80330107E7EA908142BD09D9E686F921D57C55E36FEF1F0520DC4E2C78D9A9FDA4E164808A13FA98E0D97E87A0CC5DC7422DBDFA93EED6ADFCED151152A1270893399D45D5EAB5A7B37577674D8ECA2EFE04F20831ADF4F1BA7B4C59A1A950313D69DF87DEFDD60D186A1233F38
	187EDFC27F5FC17EAF2118920AA95981E6CB55FA347B1F36FC907BCC5D10DE2CA7BEB4C84E568CFAF57C256EBB2A97BC307A5A5C1BF78E248BD98D44E0D6C71F29D9D63DD1D85CC5C7226884DAC948D554C26B029727F7030B2FD5705D844FCADB9D26EE626859E61373ACED0108D47815105D636C6D228CA4A17D2B062F00AD3DA245C5C661C3290FCC260887CA42EA2569967C01E0A85843C2535F7EC62293799B53689F996BFFD479566D8A7CB7E43FD16F3F1FB72E5B6B7E7B26D7D95E56712A727CC87E4DB6
	7A1D209040C73F4AFF2738136CA777763ED6EB3F49EA15CC563335B6B3F9F72B6E22A4AA7B9FBFB31176DDC8A3973232C293117B0C2A4E7F81D0CB878819017CB337AFGG4C23GGD0CB818294G94G88G88GCCFBB0B619017CB337AFGG4C23GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG71AFGGGG
**end of data**/
}
}