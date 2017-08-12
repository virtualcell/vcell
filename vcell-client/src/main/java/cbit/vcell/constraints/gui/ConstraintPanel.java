/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints.gui;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.ConstraintSolver;
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
			ivjConstraintTable.setModel(new GeneralConstraintsTableModel(ivjConstraintTable));
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
			ivjScrollPaneTable.setModel(new SimpleBoundsTableModel(ivjScrollPaneTable));
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
private GeneralConstraint getSelectedGeneralConstraint() {
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
private SimpleBounds getselectedSimpleBounds() {
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
private void setconstraintContainerImpl1(ConstraintContainerImpl newValue) {
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
public void setConstraintSolver(ConstraintSolver constraintSolver) {
	ConstraintSolver oldValue = fieldConstraintSolver;
	fieldConstraintSolver = constraintSolver;
	firePropertyChange("constraintSolver", oldValue, constraintSolver);
}


/**
 * Set the constraintSolver1 to a new value.
 * @param newValue cbit.vcell.constraints.ConstraintSolver
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setconstraintSolver1(ConstraintSolver newValue) {
	if (ivjconstraintSolver1 != newValue) {
		try {
			ConstraintSolver oldValue = getconstraintSolver1();
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
private void setselectedGeneralConstraint(GeneralConstraint newValue) {
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
private void setselectedSimpleBounds(SimpleBounds newValue) {
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

}
