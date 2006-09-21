package cbit.vcell.solver.ode.gui;
import java.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.DataAccessException;
import cbit.plot.*;
import cbit.vcell.simdata.ColumnDescription;
import cbit.vcell.simdata.FunctionColumnDescription;
import cbit.vcell.simdata.ODESolverResultSet;
import cbit.vcell.simdata.ODESolverResultSetColumnDescription;
import cbit.vcell.simulation.*;
import cbit.vcell.solver.ode.*;
import javax.swing.*;
import cbit.vcell.parser.Expression;

/**
 * Insert the type's description here.  What we want to do with this
 * is to pass in an ODESolverResultSet which contains everything needed
 * for this panel to run.  This necessitates being able to get variable
 * and sensitivity parameter info from the result set.  So, we need to
 * add that kind of interface to ODESolverResultSet.
 * Creation date: (8/13/2000 3:15:43 PM)
 * @author: John Wagner the Great
 */
public class ODESolverPlotSpecificationPanel extends JPanel {
	private JCheckBox ivjLogSensCheckbox = null;
	private JLabel ivjMaxLabel = null;
	private JLabel ivjMinLabel = null;
	private JPanel ivjSensitivityParameterPanel = null;
	private JLabel ivjXAxisLabel = null;
	private JList ivjYAxisChoice = null;
	private JLabel ivjYAxisLabel = null;
	private JLabel ivjCurLabel = null;
	private JSlider ivjSensitivityParameterSlider = null;
	private JScrollPane ivjJScrollPaneYAxis = null;
	private JLabel ivjJLabelSensitivityParameter = null;
	private JPanel ivjJPanelSensitivity = null;
	private cbit.vcell.simdata.ODESolverResultSet fieldOdeSolverResultSet = null;
	private DefaultListModel ivjDefaultListModelY = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private int[] plottableColumnIndices = new int[0];
	private String[] plottableNames = new String[0];
	private int fieldXIndex = -1;
	private int[] fieldYIndices = new int[0];
	private cbit.plot.SingleXPlot2D fieldSingleXPlot2D = null;
	private java.lang.String[] resultSetColumnNames = null;
	private DefaultComboBoxModel ivjComboBoxModelX = null;
	private JButton ivjDeleteFunctionButton = null;
	private JPanel ivjUserFunctionPanel = null;
	private JComboBox ivjXAxisComboBox = null;
	private JButton ivjAddFunctionButton = null;
	private boolean ivjConnPtoP2Aligning = false;
	private ODESolverResultSet ivjodeSolverResultSet1 = null;
	private JLabel ivjFunctionExprLabel = null;
	private JLabel ivjFunctionNameLabel = null;
	private JPanel ivjFunctionPanel = null;
	private JTextField ivjFunctionExpressionTextField = null;
	private JTextField ivjFunctionNameTextField = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getAddFunctionButton()) 
				connEtoC5(e);
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getDeleteFunctionButton()) 
				connEtoC8(e);
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getLogSensCheckbox()) 
				connEtoC10(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getXAxisComboBox()) 
				connEtoC6(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ODESolverPlotSpecificationPanel.this && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connEtoC1(evt);
			if (evt.getSource() == ODESolverPlotSpecificationPanel.this && (evt.getPropertyName().equals("xIndex"))) 
				connEtoC3(evt);
			if (evt.getSource() == ODESolverPlotSpecificationPanel.this && (evt.getPropertyName().equals("YIndices"))) 
				connEtoC4(evt);
			if (evt.getSource() == ODESolverPlotSpecificationPanel.this && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == ODESolverPlotSpecificationPanel.this.getodeSolverResultSet1() && (evt.getPropertyName().equals("columnDescriptions"))) 
				connEtoC7(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getSensitivityParameterSlider()) 
				connEtoC12(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getYAxisChoice()) 
				connEtoC2(e);
		};
	};

/**
 * ODESolverPlotSpecificationPanel constructor comment.
 */
public ODESolverPlotSpecificationPanel() {
	super();
	initialize();
}

/**
 * Comment
 */
private void addFunction(cbit.vcell.simdata.ODESolverResultSet odeRS) throws cbit.vcell.parser.ExpressionException {

	//
	// Assign a default name for the new function. Check if any other existing function has the same name.
	// 
	String[] existingFunctionsNames = new String[odeRS.getFunctionColumnCount()];
	for (int i = 0; i < odeRS.getFunctionColumnCount(); i++) {
		existingFunctionsNames[i] = odeRS.getColumnDescriptions(i+odeRS.getDataColumnCount()).getName();
	}
	
	String defaultName = null;
	int count = 0;
	boolean nameUsed = true;
	while (nameUsed) {
		boolean matchFound = false;
		count++;
		defaultName = "Function" + count;
		for (int i = 0; existingFunctionsNames != null && i < existingFunctionsNames.length; i++){
			if (existingFunctionsNames[i].equals(defaultName)) {
				matchFound = true;
			}
		}
		nameUsed = matchFound;
	}

	//
	// Initialize fields
	//

	javax.swing.JPanel FnPanel = getFunctionPanel();
	
	getFunctionNameTextField().setText(defaultName);
	getFunctionExpressionTextField().setText("0.0");

	//
	// Show the editor with a default name and default expression for the function
	// If the OK option is chosen, get the new name and expression for the function and create a new
	// functioncolumndescription, check is function is valid. If it is, add it to the list of columns 
	// in the ODEResultSet. Else, pop-up an error dialog indicating that function cannot be added.
	//
	FunctionColumnDescription fcd = null;
	int ok = JOptionPane.showOptionDialog(this, FnPanel, "Add Function" , 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
	if (ok == javax.swing.JOptionPane.OK_OPTION) {
		String funcName = getFunctionNameTextField().getText();
		cbit.vcell.parser.Expression funcExp = new Expression(getFunctionExpressionTextField().getText());
		fcd = new FunctionColumnDescription(funcExp, funcName, null, funcName+" : "+funcExp.infix(), true);

		try {
			odeRS.checkFunctionValidity(fcd);
		} catch (cbit.vcell.parser.ExpressionException e) {
			javax.swing.JOptionPane.showMessageDialog(this, e.getMessage()+". "+funcName+" not added.", "Error Adding Function ", javax.swing.JOptionPane.ERROR_MESSAGE);
			// Commenting the Stack trace for exception .... annoying to have the exception thrown after dealing with pop-up error message!
			// e.printStackTrace(System.out);
			return;
		}
		try {
			odeRS.addFunctionColumn(fcd);
		} catch (cbit.vcell.parser.ExpressionException e) {
			javax.swing.JOptionPane.showMessageDialog(this, e.getMessage()+". "+funcName+" not added.", "Error Adding Function ", javax.swing.JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(System.out);
		}

	}
}


/**
 * connEtoC1:  (ODESolverPlotSpecificationPanel.odeSolverResultSet --> ODESolverPlotSpecificationPanel.setPlottableColumns(Lcbit.vcell.solver.ode.ODESolverResultSet;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateResultSet(this.getOdeSolverResultSet());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (LogSensCheckbox.action.actionPerformed(java.awt.event.ActionEvent) --> ODESolverPlotSpecificationPanel.regeneratePlot2D()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.regeneratePlot2D();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (odeSolverResultSet1.this --> ODESolverPlotSpecificationPanel.initializeLogSensCheckBox()V)
 * @param value cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(cbit.vcell.simdata.ODESolverResultSet value) {
	try {
		// user code begin {1}
		// user code end
		this.initializeLogSensCheckBox();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (SensitivityParameterSlider.change.stateChanged(javax.swing.event.ChangeEvent) --> ODESolverPlotSpecificationPanel.regeneratePlot2D()V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.regeneratePlot2D();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (odeSolverResultSet1.this --> ODESolverPlotSpecificationPanel.updateResultSet(Lcbit.vcell.solver.ode.ODESolverResultSet;)V)
 * @param value cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(cbit.vcell.simdata.ODESolverResultSet value) {
	try {
		// user code begin {1}
		// user code end
		this.updateResultSet(getodeSolverResultSet1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (YAxisChoice.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> ODESolverPlotSpecificationPanel.setYNamesFromList()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setYIndicesFromList();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (ODESolverPlotSpecificationPanel.yIndices --> ODESolverPlotSpecificationPanel.refreshPlotData()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.regeneratePlot2D();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (ODESolverPlotSpecificationPanel.YIndices --> ODESolverPlotSpecificationPanel.refreshPlotData()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refreshVisiblePlots(this.getSingleXPlot2D());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (AddFunctionButton.action.actionPerformed(java.awt.event.ActionEvent) --> ODESolverPlotSpecificationPanel.addFunction(Lcbit.vcell.solver.ode.ODESolverResultSet;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getodeSolverResultSet1() != null)) {
			this.addFunction(getodeSolverResultSet1());
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
 * connEtoC6:  (XAxisComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> ODESolverPlotSpecificationPanel.setXIndex(I)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setXIndex(getXAxisComboBox().getSelectedIndex());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (odeSolverResultSet1.columnDescriptions --> ODESolverPlotSpecificationPanel.updateResultSet(Lcbit.vcell.solver.ode.ODESolverResultSet;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getodeSolverResultSet1() != null)) {
			this.updateResultSet(getodeSolverResultSet1());
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
 * connEtoC8:  (DeleteFunctionButton.action.actionPerformed(java.awt.event.ActionEvent) --> ODESolverPlotSpecificationPanel.deleteFunction(Lcbit.vcell.math.Function;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deleteFunction(getYAxisChoice().getSelectedIndex());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC9:  (odeSolverResultSet1.this --> ODESolverPlotSpecificationPanel.enableLogSensitivity(Lcbit.vcell.solver.ode.ODESolverResultSet;)V)
 * @param value cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(cbit.vcell.simdata.ODESolverResultSet value) {
	try {
		// user code begin {1}
		// user code end
		this.enableLogSensitivity();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (DefaultListModelY.this <--> YAxisChoice.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getYAxisChoice().setModel(getDefaultListModelY());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (ODESolverPlotSpecificationPanel.odeSolverResultSet <--> odeSolverResultSet1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getodeSolverResultSet1() != null)) {
				this.setOdeSolverResultSet(getodeSolverResultSet1());
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
 * connPtoP2SetTarget:  (ODESolverPlotSpecificationPanel.odeSolverResultSet <--> odeSolverResultSet1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setodeSolverResultSet1(this.getOdeSolverResultSet());
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
 * connPtoP3SetTarget:  (ComboBoxModelX.this <--> XAxisComboBox.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getXAxisComboBox().setModel(getComboBoxModelX());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void deleteFunction(int ySelection) {
	ODESolverResultSet odeRS = getOdeSolverResultSet();

	//
	// Check to see if the selected option to be deleted is a state variable or
	// a function generated by the model. If so, the selection cannot be deleted.
	//
	
	String yChoice = getDefaultListModelY().getElementAt(ySelection).toString();

	for (int i = 0; i < odeRS.getColumnDescriptionsCount(); i++) {
		ColumnDescription colDesc = odeRS.getColumnDescriptions(i);
		if (colDesc instanceof ODESolverResultSetColumnDescription){
			if (yChoice.equals(colDesc.getDisplayName())) {
				javax.swing.JOptionPane.showMessageDialog(this,"Cannot Delete selected data. "+yChoice+" is a state variable.", "Error Deleting Selection",JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else if (colDesc instanceof FunctionColumnDescription){
			FunctionColumnDescription funcColDesc = (FunctionColumnDescription)colDesc;
			if ( (yChoice.equals(funcColDesc.getDisplayName())) && !(funcColDesc.getIsUserDefined()) ) {
				javax.swing.JOptionPane.showMessageDialog(this,"Cannot Delete selected function. "+funcColDesc.getDisplayName()+" is not a user-defined function.", "Error Deleting Function",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

	//
	// If the function that is to be deleted is a user-defined function, remove the corresponding
	// functionColumnDescription from the odesolver result set and call updateRestultSet to update
	// the plot specification panel, etc.
	//

	// Remove functionColumnDescription from odeRS
	for (int i=0;i<odeRS.getColumnDescriptionsCount();i++) {
		ColumnDescription colDesc = odeRS.getColumnDescriptions(i);
		if (colDesc instanceof FunctionColumnDescription){
			FunctionColumnDescription funcColDesc = (FunctionColumnDescription)colDesc;
			if ( yChoice.equals(funcColDesc.getDisplayName()) ) {
				try {
					odeRS.removeFunctionColumn(funcColDesc);
				} catch (cbit.vcell.parser.ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Cannot remove function column from result set."+e.getMessage());
				}
			}
		}
	}

	// updateResultSet
	try {
		updateResultSet(odeRS);
	} catch (cbit.vcell.parser.ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Cannot update result set."+e.getMessage());
	}
}
	
/**
 * Method to enable the log sensitivity checkbox and slider depending on whether sensitivity analysis is enabled.
 */
private void enableLogSensitivity() throws cbit.vcell.parser.ExpressionException {

	boolean bEnabled = true;
	
	if (getSensitivityParameter() != null) {
		getLogSensCheckbox().setEnabled(bEnabled);
		getMaxLabel().setEnabled(bEnabled);		
		getMinLabel().setEnabled(bEnabled);
		getCurLabel().setEnabled(bEnabled);		
		getSensitivityParameterSlider().setEnabled(bEnabled);
		getJLabelSensitivityParameter().setEnabled(bEnabled);	
	} else {
		getLogSensCheckbox().setEnabled(!bEnabled);
		getMaxLabel().setEnabled(!bEnabled);		
		getMinLabel().setEnabled(!bEnabled);
		getCurLabel().setEnabled(!bEnabled);		
		getSensitivityParameterSlider().setEnabled(!bEnabled);
		getJLabelSensitivityParameter().setEnabled(!bEnabled);
	}
}


/**
 * Return the AddFuntionButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddFunctionButton() {
	if (ivjAddFunctionButton == null) {
		try {
			ivjAddFunctionButton = new javax.swing.JButton();
			ivjAddFunctionButton.setName("AddFunctionButton");
			ivjAddFunctionButton.setText("Add Function");
			ivjAddFunctionButton.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			ivjAddFunctionButton.setMaximumSize(new java.awt.Dimension(121, 25));
			ivjAddFunctionButton.setPreferredSize(new java.awt.Dimension(121, 25));
			ivjAddFunctionButton.setMinimumSize(new java.awt.Dimension(22, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddFunctionButton;
}

/**
 * Return the ComboBoxModelX property value.
 * @return javax.swing.DefaultComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultComboBoxModel getComboBoxModelX() {
	if (ivjComboBoxModelX == null) {
		try {
			ivjComboBoxModelX = new javax.swing.DefaultComboBoxModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjComboBoxModelX;
}


/**
 * Return the ConstantTextField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCurLabel() {
	if (ivjCurLabel == null) {
		try {
			ivjCurLabel = new javax.swing.JLabel();
			ivjCurLabel.setName("CurLabel");
			ivjCurLabel.setText("Value");
			ivjCurLabel.setBackground(java.awt.Color.lightGray);
			ivjCurLabel.setForeground(java.awt.Color.black);
			ivjCurLabel.setEnabled(true);
			ivjCurLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurLabel;
}

/**
 * Return the DefaultListModelY property value.
 * @return javax.swing.DefaultListModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultListModel getDefaultListModelY() {
	if (ivjDefaultListModelY == null) {
		try {
			ivjDefaultListModelY = new javax.swing.DefaultListModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModelY;
}


/**
 * Return the DeleteFunctionButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteFunctionButton() {
	if (ivjDeleteFunctionButton == null) {
		try {
			ivjDeleteFunctionButton = new javax.swing.JButton();
			ivjDeleteFunctionButton.setName("DeleteFunctionButton");
			ivjDeleteFunctionButton.setText("Delete Function");
			ivjDeleteFunctionButton.setMinimumSize(new java.awt.Dimension(22, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteFunctionButton;
}


/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFunctionExpressionTextField() {
	if (ivjFunctionExpressionTextField == null) {
		try {
			ivjFunctionExpressionTextField = new javax.swing.JTextField();
			ivjFunctionExpressionTextField.setName("FunctionExpressionTextField");
			ivjFunctionExpressionTextField.setPreferredSize(new java.awt.Dimension(200, 30));
			ivjFunctionExpressionTextField.setMaximumSize(new java.awt.Dimension(200, 30));
			ivjFunctionExpressionTextField.setMinimumSize(new java.awt.Dimension(200, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionExpressionTextField;
}

/**
 * Return the FunctionExprLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFunctionExprLabel() {
	if (ivjFunctionExprLabel == null) {
		try {
			ivjFunctionExprLabel = new javax.swing.JLabel();
			ivjFunctionExprLabel.setName("FunctionExprLabel");
			ivjFunctionExprLabel.setText("Function Expression");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionExprLabel;
}


/**
 * Return the FunctionNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFunctionNameLabel() {
	if (ivjFunctionNameLabel == null) {
		try {
			ivjFunctionNameLabel = new javax.swing.JLabel();
			ivjFunctionNameLabel.setName("FunctionNameLabel");
			ivjFunctionNameLabel.setText("Function Name");
			ivjFunctionNameLabel.setMinimumSize(new java.awt.Dimension(45, 14));
			ivjFunctionNameLabel.setMaximumSize(new java.awt.Dimension(45, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionNameLabel;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFunctionNameTextField() {
	if (ivjFunctionNameTextField == null) {
		try {
			ivjFunctionNameTextField = new javax.swing.JTextField();
			ivjFunctionNameTextField.setName("FunctionNameTextField");
			ivjFunctionNameTextField.setPreferredSize(new java.awt.Dimension(200, 30));
			ivjFunctionNameTextField.setMaximumSize(new java.awt.Dimension(200, 30));
			ivjFunctionNameTextField.setMinimumSize(new java.awt.Dimension(200, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionNameTextField;
}

/**
 * Return the FunctionPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getFunctionPanel() {
	if (ivjFunctionPanel == null) {
		try {
			ivjFunctionPanel = new javax.swing.JPanel();
			ivjFunctionPanel.setName("FunctionPanel");
			ivjFunctionPanel.setLayout(new java.awt.GridBagLayout());
			ivjFunctionPanel.setBounds(401, 308, 407, 85);

			java.awt.GridBagConstraints constraintsFunctionNameLabel = new java.awt.GridBagConstraints();
			constraintsFunctionNameLabel.gridx = 0; constraintsFunctionNameLabel.gridy = 0;
			constraintsFunctionNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionNameLabel(), constraintsFunctionNameLabel);

			java.awt.GridBagConstraints constraintsFunctionNameTextField = new java.awt.GridBagConstraints();
			constraintsFunctionNameTextField.gridx = 1; constraintsFunctionNameTextField.gridy = 0;
			constraintsFunctionNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFunctionNameTextField.weightx = 1.0;
			constraintsFunctionNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionNameTextField(), constraintsFunctionNameTextField);

			java.awt.GridBagConstraints constraintsFunctionExprLabel = new java.awt.GridBagConstraints();
			constraintsFunctionExprLabel.gridx = 0; constraintsFunctionExprLabel.gridy = 1;
			constraintsFunctionExprLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionExprLabel(), constraintsFunctionExprLabel);

			java.awt.GridBagConstraints constraintsFunctionExpressionTextField = new java.awt.GridBagConstraints();
			constraintsFunctionExpressionTextField.gridx = 1; constraintsFunctionExpressionTextField.gridy = 1;
			constraintsFunctionExpressionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFunctionExpressionTextField.weightx = 1.0;
			constraintsFunctionExpressionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionExpressionTextField(), constraintsFunctionExpressionTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionPanel;
}

/**
 * Return the JLabelSensitivityParameter property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSensitivityParameter() {
	if (ivjJLabelSensitivityParameter == null) {
		try {
			ivjJLabelSensitivityParameter = new javax.swing.JLabel();
			ivjJLabelSensitivityParameter.setName("JLabelSensitivityParameter");
			ivjJLabelSensitivityParameter.setText(" ");
			ivjJLabelSensitivityParameter.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSensitivityParameter;
}

/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelSensitivity() {
	if (ivjJPanelSensitivity == null) {
		try {
			ivjJPanelSensitivity = new javax.swing.JPanel();
			ivjJPanelSensitivity.setName("JPanelSensitivity");
			ivjJPanelSensitivity.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLogSensCheckbox = new java.awt.GridBagConstraints();
			constraintsLogSensCheckbox.gridx = 0; constraintsLogSensCheckbox.gridy = 0;
			constraintsLogSensCheckbox.gridwidth = 3;
			constraintsLogSensCheckbox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLogSensCheckbox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSensitivity().add(getLogSensCheckbox(), constraintsLogSensCheckbox);

			java.awt.GridBagConstraints constraintsSensitivityParameterPanel = new java.awt.GridBagConstraints();
			constraintsSensitivityParameterPanel.gridx = 0; constraintsSensitivityParameterPanel.gridy = 2;
			constraintsSensitivityParameterPanel.gridwidth = 3;
			constraintsSensitivityParameterPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSensitivityParameterPanel.weightx = 2.0;
			constraintsSensitivityParameterPanel.weighty = 1.0;
			constraintsSensitivityParameterPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSensitivity().add(getSensitivityParameterPanel(), constraintsSensitivityParameterPanel);

			java.awt.GridBagConstraints constraintsJLabelSensitivityParameter = new java.awt.GridBagConstraints();
			constraintsJLabelSensitivityParameter.gridx = 0; constraintsJLabelSensitivityParameter.gridy = 1;
			constraintsJLabelSensitivityParameter.gridwidth = 3;
			constraintsJLabelSensitivityParameter.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelSensitivityParameter.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelSensitivity().add(getJLabelSensitivityParameter(), constraintsJLabelSensitivityParameter);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelSensitivity;
}

/**
 * Return the JScrollPaneYAxis property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneYAxis() {
	if (ivjJScrollPaneYAxis == null) {
		try {
			ivjJScrollPaneYAxis = new javax.swing.JScrollPane();
			ivjJScrollPaneYAxis.setName("JScrollPaneYAxis");
			getJScrollPaneYAxis().setViewportView(getYAxisChoice());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneYAxis;
}


/**
 * Return the LogSensCheckbox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getLogSensCheckbox() {
	if (ivjLogSensCheckbox == null) {
		try {
			ivjLogSensCheckbox = new javax.swing.JCheckBox();
			ivjLogSensCheckbox.setName("LogSensCheckbox");
			ivjLogSensCheckbox.setText("Log Sensitivity");
			ivjLogSensCheckbox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLogSensCheckbox;
}

/**
 * Return the MaxLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaxLabel() {
	if (ivjMaxLabel == null) {
		try {
			ivjMaxLabel = new javax.swing.JLabel();
			ivjMaxLabel.setName("MaxLabel");
			ivjMaxLabel.setText("1");
			ivjMaxLabel.setEnabled(true);
			ivjMaxLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			ivjMaxLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxLabel;
}

/**
 * Return the MinLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinLabel() {
	if (ivjMinLabel == null) {
		try {
			ivjMinLabel = new javax.swing.JLabel();
			ivjMinLabel.setName("MinLabel");
			ivjMinLabel.setText("0");
			ivjMinLabel.setBackground(java.awt.Color.lightGray);
			ivjMinLabel.setEnabled(true);
			ivjMinLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinLabel;
}

/**
 * Gets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @return The odeSolverResultSet property value.
 * @see #setOdeSolverResultSet
 */
public cbit.vcell.simdata.ODESolverResultSet getOdeSolverResultSet() {
	return fieldOdeSolverResultSet;
}


/**
 * Return the odeSolverResultSet1 property value.
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.ODESolverResultSet getodeSolverResultSet1() {
	// user code begin {1}
	// user code end
	return ivjodeSolverResultSet1;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 5:27:49 PM)
 * @return int[]
 */
private int[] getPlottableColumnIndices() {
	return plottableColumnIndices;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 5:27:49 PM)
 * @return java.lang.String[]
 */
private java.lang.String[] getPlottableNames() {
	return plottableNames;
}


/**
 * Insert the method's description here.
 * Creation date: (5/18/2001 10:42:41 PM)
 * @return java.lang.String[]
 */
private java.lang.String[] getResultSetColumnNames() {
	return resultSetColumnNames;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 4:56:15 PM)
 * @param cbit.vcell.solver.ode.ODESolverResultSet
 */
private int[] getSensitivityIndices(ODESolverResultSet odeSolverResultSet) {
    // find out how many plottable datasets we have
    int plottable = 0;
    for (int i = 0; i < odeSolverResultSet.getColumnDescriptionsCount(); i++) {
        ColumnDescription cd =
            (ColumnDescription) odeSolverResultSet.getColumnDescriptions(i);
        if (cd.getParameterName() != null) {
            plottable++; // not a parameter
        }
    }
    // now store their indices
    int[] indices = new int[plottable];
    plottable = 0;
    for (int i = 0; i < odeSolverResultSet.getColumnDescriptionsCount(); i++) {
        ColumnDescription cd =
            (ColumnDescription) odeSolverResultSet.getColumnDescriptions(i);
        if (cd.getParameterName() != null) {
            indices[plottable] = i;
            plottable++;
        }
    }
    return (sortIndices(odeSolverResultSet, indices));
}


/**
 // Method to obtain the sensitivity parameter (if applicable). The method checks the column description names in the
 // result set to find any column description that begins with the substring "sens" and contains the substring "wrt_".
 // If there is, then the last portion of that column description name is the parameter name. The sensitivity parameter
 // is also stored as a function column description in the result set (as a constant function). The value is extracted
 // from the result set, and a new Constant is created (with the name and value of the parameter) and returned. If no
 // column description starts with the substring "sens" or if the column for the parameter does not exist in the result
 // set, the method returns null.
 */
private cbit.vcell.math.Constant getSensitivityParameter() throws cbit.vcell.parser.ExpressionException {
	String sensParamName = "";
	FunctionColumnDescription fcds[] = getOdeSolverResultSet().getFunctionColumnDescriptions();

	// Check for any column description name that starts with the substring "sens" and contains "wrt_".
	for (int i = 0; i < fcds.length; i++){
		if (fcds[i].getName().startsWith("sens_")) {
			int c = fcds[i].getName().indexOf("wrt_");
			sensParamName = fcds[i].getName().substring(c+4);
			if (!sensParamName.equals(null) || !sensParamName.equals("")) {
				break;
			} 
		}
	}

	double sensParamValue = 0.0;

	if (sensParamName.equals("")) {
		return null;
	}

	// If the sens param column exists in the result set, create a Constant and return it, else return null.
	if (getOdeSolverResultSet().findColumn(sensParamName) > -1) {
		double[] tempValues = getOdeSolverResultSet().extractColumn(getOdeSolverResultSet().findColumn(sensParamName));
		sensParamValue = tempValues[1];
	} else {
		// System.out.println("REUSULT SET DOES NOT HAVE SENSITIVITY ANALYSIS");
		return null;
	}

	cbit.vcell.math.Constant sensParam = new cbit.vcell.math.Constant(sensParamName, new Expression(sensParamValue));
	return sensParam;
}


/**
 * Return the SensitivityParameterPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSensitivityParameterPanel() {
	if (ivjSensitivityParameterPanel == null) {
		try {
			ivjSensitivityParameterPanel = new javax.swing.JPanel();
			ivjSensitivityParameterPanel.setName("SensitivityParameterPanel");
			ivjSensitivityParameterPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMinLabel = new java.awt.GridBagConstraints();
			constraintsMinLabel.gridx = 0; constraintsMinLabel.gridy = 1;
			getSensitivityParameterPanel().add(getMinLabel(), constraintsMinLabel);

			java.awt.GridBagConstraints constraintsMaxLabel = new java.awt.GridBagConstraints();
			constraintsMaxLabel.gridx = 2; constraintsMaxLabel.gridy = 1;
			constraintsMaxLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMaxLabel.anchor = java.awt.GridBagConstraints.EAST;
			getSensitivityParameterPanel().add(getMaxLabel(), constraintsMaxLabel);

			java.awt.GridBagConstraints constraintsCurLabel = new java.awt.GridBagConstraints();
			constraintsCurLabel.gridx = 1; constraintsCurLabel.gridy = 1;
			constraintsCurLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCurLabel.weightx = 1.0;
			getSensitivityParameterPanel().add(getCurLabel(), constraintsCurLabel);

			java.awt.GridBagConstraints constraintsSensitivityParameterSlider = new java.awt.GridBagConstraints();
			constraintsSensitivityParameterSlider.gridx = 0; constraintsSensitivityParameterSlider.gridy = 0;
			constraintsSensitivityParameterSlider.gridwidth = 3;
			constraintsSensitivityParameterSlider.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSensitivityParameterSlider.weightx = 1.0;
			getSensitivityParameterPanel().add(getSensitivityParameterSlider(), constraintsSensitivityParameterSlider);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSensitivityParameterPanel;
}

/**
 * Return the ConstantScrollbar property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getSensitivityParameterSlider() {
	if (ivjSensitivityParameterSlider == null) {
		try {
			ivjSensitivityParameterSlider = new javax.swing.JSlider();
			ivjSensitivityParameterSlider.setName("SensitivityParameterSlider");
			ivjSensitivityParameterSlider.setPaintLabels(false);
			ivjSensitivityParameterSlider.setPaintTicks(true);
			ivjSensitivityParameterSlider.setValue(25);
			ivjSensitivityParameterSlider.setPreferredSize(new java.awt.Dimension(200, 16));
			ivjSensitivityParameterSlider.setMaximum(50);
			ivjSensitivityParameterSlider.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSensitivityParameterSlider;
}

/**
 * Comment
 */
private double[] getSensValues(ColumnDescription colDesc) throws cbit.vcell.parser.ExpressionException {
	if (getSensitivityParameter() != null) {
		double sens[] = null;
		String[] rsetColNames = getPlottableNames();
		int sensIndex = -1;
		
		for (int j = 0; j < rsetColNames.length; j++) {
			if (rsetColNames[j].equals("sens_"+colDesc.getName()+"_wrt_"+getSensitivityParameter().getName())) {
				sensIndex = j;
			}
		}
		if (sensIndex > -1) {
			sens = getOdeSolverResultSet().extractColumn(getOdeSolverResultSet().findColumn(rsetColNames[sensIndex]));
		}

		return sens;
	} else {
		return null;
	}
}


/**
 * Gets the singleXPlot2D property (cbit.plot.SingleXPlot2D) value.
 * @return The singleXPlot2D property value.
 * @see #setSingleXPlot2D
 */
public cbit.plot.SingleXPlot2D getSingleXPlot2D() {
	return fieldSingleXPlot2D;
}


/**
 * Return the UserFunctionPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getUserFunctionPanel() {
	if (ivjUserFunctionPanel == null) {
		try {
			ivjUserFunctionPanel = new javax.swing.JPanel();
			ivjUserFunctionPanel.setName("UserFunctionPanel");
			ivjUserFunctionPanel.setLayout(new java.awt.FlowLayout());
			ivjUserFunctionPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjUserFunctionPanel.setMaximumSize(new java.awt.Dimension(120, 70));
			ivjUserFunctionPanel.setPreferredSize(new java.awt.Dimension(115, 68));
			ivjUserFunctionPanel.setMinimumSize(new java.awt.Dimension(115, 68));
			getUserFunctionPanel().add(getAddFunctionButton(), getAddFunctionButton().getName());
			getUserFunctionPanel().add(getDeleteFunctionButton(), getDeleteFunctionButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserFunctionPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 4:56:15 PM)
 * @param cbit.vcell.solver.ode.ODESolverResultSet
 */
private int[] getVariableIndices(ODESolverResultSet odeSolverResultSet) {
    // find out how many plottable datasets we have
    int plottable = 0;
    for (int i = 0; i < odeSolverResultSet.getColumnDescriptionsCount(); i++) {
        ColumnDescription cd =
            (ColumnDescription) odeSolverResultSet.getColumnDescriptions(i);
        if (cd.getParameterName() == null) {
            plottable++; // not a parameter
        }
    }
    // now store their indices
    int[] indices = new int[plottable];
    plottable = 0;
    for (int i = 0; i < odeSolverResultSet.getColumnDescriptionsCount(); i++) {
        ColumnDescription cd =
            (ColumnDescription) odeSolverResultSet.getColumnDescriptions(i);
        if (cd.getParameterName() == null) {
            indices[plottable] = i;
            plottable++;
        }
    }
    return (sortIndices(odeSolverResultSet, indices));
}


/**
 * Return the XAxisComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getXAxisComboBox() {
	if (ivjXAxisComboBox == null) {
		try {
			ivjXAxisComboBox = new javax.swing.JComboBox();
			ivjXAxisComboBox.setName("XAxisComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXAxisComboBox;
}


/**
 * Return the XAxisLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXAxisLabel() {
	if (ivjXAxisLabel == null) {
		try {
			ivjXAxisLabel = new javax.swing.JLabel();
			ivjXAxisLabel.setName("XAxisLabel");
			ivjXAxisLabel.setText("X Axis:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXAxisLabel;
}

/**
 * Gets the xIndex property (int) value.
 * @return The xIndex property value.
 * @see #setXIndex
 */
public int getXIndex() {
	return fieldXIndex;
}


/**
 * Return the YAxisChoice property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getYAxisChoice() {
	if (ivjYAxisChoice == null) {
		try {
			ivjYAxisChoice = new javax.swing.JList();
			ivjYAxisChoice.setName("YAxisChoice");
			ivjYAxisChoice.setBounds(0, 0, 160, 120);
			ivjYAxisChoice.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYAxisChoice;
}

/**
 * Return the YAxisLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYAxisLabel() {
	if (ivjYAxisLabel == null) {
		try {
			ivjYAxisLabel = new javax.swing.JLabel();
			ivjYAxisLabel.setName("YAxisLabel");
			ivjYAxisLabel.setText("Y Axis:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYAxisLabel;
}

/**
 * Gets the yIndices property (int[]) value.
 * @return The yIndices property value.
 * @see #setYIndices
 */
public int[] getYIndices() {
	return fieldYIndices;
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
	getYAxisChoice().addListSelectionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getXAxisComboBox().addItemListener(ivjEventHandler);
	getAddFunctionButton().addActionListener(ivjEventHandler);
	getDeleteFunctionButton().addActionListener(ivjEventHandler);
	getLogSensCheckbox().addActionListener(ivjEventHandler);
	getSensitivityParameterSlider().addChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP3SetTarget();
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
		setName("ODESolverPlotSpecificationPanel");
		setPreferredSize(new java.awt.Dimension(150, 600));
		setLayout(new java.awt.GridBagLayout());
		setSize(248, 604);
		setMinimumSize(new java.awt.Dimension(125, 300));

		java.awt.GridBagConstraints constraintsXAxisLabel = new java.awt.GridBagConstraints();
		constraintsXAxisLabel.gridx = 0; constraintsXAxisLabel.gridy = 0;
		constraintsXAxisLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getXAxisLabel(), constraintsXAxisLabel);

		java.awt.GridBagConstraints constraintsYAxisLabel = new java.awt.GridBagConstraints();
		constraintsYAxisLabel.gridx = 0; constraintsYAxisLabel.gridy = 2;
		constraintsYAxisLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getYAxisLabel(), constraintsYAxisLabel);

		java.awt.GridBagConstraints constraintsJPanelSensitivity = new java.awt.GridBagConstraints();
		constraintsJPanelSensitivity.gridx = 0; constraintsJPanelSensitivity.gridy = 5;
constraintsJPanelSensitivity.gridheight = 2;
		constraintsJPanelSensitivity.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelSensitivity.weightx = 1.0;
		add(getJPanelSensitivity(), constraintsJPanelSensitivity);

		java.awt.GridBagConstraints constraintsJScrollPaneYAxis = new java.awt.GridBagConstraints();
		constraintsJScrollPaneYAxis.gridx = 0; constraintsJScrollPaneYAxis.gridy = 3;
		constraintsJScrollPaneYAxis.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneYAxis.weightx = 1.0;
		constraintsJScrollPaneYAxis.weighty = 1.0;
		constraintsJScrollPaneYAxis.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPaneYAxis(), constraintsJScrollPaneYAxis);

		java.awt.GridBagConstraints constraintsXAxisComboBox = new java.awt.GridBagConstraints();
		constraintsXAxisComboBox.gridx = 0; constraintsXAxisComboBox.gridy = 1;
		constraintsXAxisComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsXAxisComboBox.weightx = 1.0;
		constraintsXAxisComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getXAxisComboBox(), constraintsXAxisComboBox);

		java.awt.GridBagConstraints constraintsUserFunctionPanel = new java.awt.GridBagConstraints();
		constraintsUserFunctionPanel.gridx = 0; constraintsUserFunctionPanel.gridy = 4;
		constraintsUserFunctionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsUserFunctionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getUserFunctionPanel(), constraintsUserFunctionPanel);
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
private void initializeLogSensCheckBox() throws cbit.vcell.parser.ExpressionException{
	if (getSensitivityParameter() != null) {
		getLogSensCheckbox().setSelected(true);
	} else {
		getLogSensCheckbox().setSelected(false);
	}
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ODESolverPlotSpecificationPanel aODESolverPlotSpecificationPanel;
		aODESolverPlotSpecificationPanel = new ODESolverPlotSpecificationPanel();
		frame.setContentPane(aODESolverPlotSpecificationPanel);
		frame.setSize(aODESolverPlotSpecificationPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		
		String[] list = {"name1", "name2", "name3", "name4", "name5"};
		aODESolverPlotSpecificationPanel.getDefaultListModelY().removeAllElements();
		for (int i=0;i<list.length;i++) {
			aODESolverPlotSpecificationPanel.getDefaultListModelY().addElement(list[i]);
		}
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * This method was created by a SmartGuide.
 */
private void oldRefreshRegularPlot() {
/*	try {
		ODESolverResultSet resultSet = getSimulationManager().getODESolverResultSet();
		SolverTaskDescription taskDescription = getSimulation().getSolverTaskDescription();
		MathDescription mathDescription = getMathDescription();
		//
		double currTime = getODESolver().getCurrentTime();
		double endTime = taskDescription.getTimeBounds().getEndingTime();
		//
		double[] yAxisData = null;
		double[] xAxisData = null;
		String xAxisLabel = getODESolverPlotSpecificationPanel().getXAxis();
		String yAxisLabel = getODESolverPlotSpecificationPanel().getYAxis();
		String selectedXAxis = getODESolverPlotSpecificationPanel().getXAxis();
		if (selectedXAxis != null) {
			int c = resultSet.findColumn(selectedXAxis);
			//Assertion.assert(c >= 0);
			if (c >= 0) {
				xAxisData = resultSet.extractColumn(c);
				xAxisLabel = resultSet.getDisplayName(c);
			}
		}
		String selectedYAxis = getODESolverPlotSpecificationPanel().getYAxis();
		if (selectedYAxis != null) {
			int c = resultSet.findColumn(selectedYAxis);
			//Assertion.assert(c >= 0);
			if (c >= 0) {
				yAxisData = resultSet.extractColumn(c);
				yAxisLabel = resultSet.getDisplayName(c);
			}
		}
		if (yAxisData == null || xAxisData == null) {
			getPlot2DCanvas().setPlotData(null);
			//  This is what I would RATHER do...but things don't work correctly
			//  if I do, because Jim uses null to indicate no data...
			//getPlot2DCanvas().plot2D(new cbit.plot.PlotData(new double[0], new double[0]));
			return;
		}
		//
		//
		//getPlot2DCanvas().setTitle(getYAxisChoice().getSelectedItem() + " vs. " + getXAxisChoice().getSelectedItem());
		//getPlot2DCanvas().setXLabel("time (seconds)");
		//getPlot2DCanvas().setYLabel("Conc");
		getPlot2DCanvas().setTitle(yAxisLabel + " vs. " + xAxisLabel);
		getPlot2DCanvas().setXLabel(xAxisLabel);
		getPlot2DCanvas().setYLabel(yAxisLabel);
		try {
			getPlot2DCanvas().setPlotData(new cbit.plot.PlotData(xAxisData, yAxisData));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	} catch (Throwable throwable) {
		handleException(throwable);
	}
*/}


/**
 * This method was created by a SmartGuide.
 */
private void oldRefreshSensitivityPlot() {
/*	try {
		ODESolverResultSet resultSet = getSimulationManager().getODESolverResultSet();
		SolverTaskDescription taskDescription = getSimulation().getSolverTaskDescription();
		MathDescription mathDescription = getMathDescription();
		//
		double currTime = getODESolver().getCurrentTime();
		double endTime = taskDescription.getTimeBounds().getEndingTime();
		//
		double[] yAxisData = null;
		double[] xAxisData = null;
		double sens[] = null;
		String xAxisLabel = null;
		String yAxisLabel = null;
		synchronized (resultSet) {
			int c = resultSet.findColumn("t");
			Assertion.assert(c >= 0);
			if (c >= 0) {
				xAxisData = resultSet.extractColumn(c);
				xAxisLabel = resultSet.getDisplayName(c);
			}
			c = resultSet.findColumn(getODESolverPlotSpecificationPanel().getSensitivityVariable());
			Assertion.assert(c >= 0);
			if (c >= 0) {
				Assertion.assert(c >= 0);
				yAxisData = resultSet.extractColumn(c);
				yAxisLabel = resultSet.getDisplayName(c);
			}
			//
			Variable var = getMathDescription().getVariable(getODESolverPlotSpecificationPanel().getSensitivityVariable());
			if (var instanceof VolVariable || var instanceof Function) {
				try {
					if (var instanceof VolVariable && getSensitivityParameter() != null) {
						c = resultSet.findColumn(SensVariable.getSensName((VolVariable) var, getSensitivityParameter()));
						if (c >= 0)
							sens = resultSet.extractColumn(c);
					} else
						if (var instanceof Function && getSensitivityParameter() != null) {
							sens = getODESolver().getFunctionSensitivity((Function) var, getSensitivityParameter());
						}
				} catch (cbit.vcell.parser.ExpressionException e) {
					System.out.println("refreshSensitivityPlot() : THE FOLLOWING EXCEPTION SHOULD PROBABLY NOT HAPPEN!");
					handleException(e);
				}
			}
		}
		if (yAxisData == null || xAxisData == null) {
			getPlot2DCanvas().setPlotData(null);
			//  This is what I would RATHER do...but things don't work correctly
			//  if I do, because Jim uses null to indicate no data...
			//getPlot2DCanvas().plot2D(new cbit.plot.PlotData(new double[0], new double[0]));
			return;
		}
		//
		//  JMW : should we Assertion.assert(sens != null)???
		Assertion.assertNotNull(sens);
		try {
			if (getODESolverPlotSpecificationPanel().getLogSensitivity()) {
				getPlot2DCanvas().setXLabel("time (seconds)");
				getPlot2DCanvas().setYLabel("sens");
				getPlot2DCanvas().setTitle("log sensitivity of " + getSensitivityParameter().getName() + " to " + getODESolverPlotSpecificationPanel().getSensitivityVariable() + " vs. time");
				double paramValue = getSensitivityParameter().getOldValue();
				for (int i = 0; i < yAxisData.length; i++) {
					//  dataArray[i] = data[i] + sens[i]*deltaParameter;
					if (Math.abs(yAxisData[i]) > 10e-8) {
						yAxisData[i] = sens[i] * paramValue / yAxisData[i];
					} else {
						yAxisData[i] = 0.0;
					}
				}
			} else {
				getPlot2DCanvas().setXLabel("time (seconds)");
				getPlot2DCanvas().setYLabel("Conc");
				getPlot2DCanvas().setTitle(getODESolverPlotSpecificationPanel().getSensitivityVariable() + " vs. time  (at " + getSensitivityParameter().getName() + " = " + getSensitivityParameter().getCurrValue() + ")");
				//
				double deltaParameter = getSensitivityParameter().getCurrValue() - getSensitivityParameter().getOldValue();
				for (int i = 0; i < yAxisData.length; i++) {
					if (Math.abs(yAxisData[i]) > 10e-8) {
						// away from zero, exponential extrapolation
						yAxisData[i] = yAxisData[i] * Math.exp(deltaParameter / yAxisData[i] * sens[i]);
					} else {
						// around zero - linear extrapolation
						yAxisData[i] = yAxisData[i] + sens[i] * deltaParameter;
					}
				}
			}
		} catch (Exception e) {
			handleException(e);
			return;
		}
		//
		try {
			getPlot2DCanvas().setPlotData(new cbit.plot.PlotData(xAxisData, yAxisData));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	} catch (Throwable throwable) {
		handleException(throwable);
	}
*/}


/**
 * Comment
 */
private void refreshVisiblePlots(Plot2D plot2D) {
	if (plot2D == null) {
		return;
	} else {
		boolean[] visiblePlots = new boolean[plot2D.getNumberOfPlots()];
		for (int i=0;i<getYIndices().length;i++) {
			visiblePlots[getYIndices()[i]] = true;
		}
		plot2D.setVisiblePlots(visiblePlots);
	}
}


/**
 * Comment
 */
private void regeneratePlot2D() throws cbit.vcell.parser.ExpressionException {
	if (getOdeSolverResultSet() == null || getXAxisComboBox().getSelectedIndex() < 0) {
		return;
	} else {
		double[] xData = getOdeSolverResultSet().extractColumn(getPlottableColumnIndices()[getXIndex()]);
		double[][] allData = new double[getPlottableColumnIndices().length + 1][xData.length];
		String[] yNames = new String[getPlottableColumnIndices().length];
		allData[0] = xData;
		double[] yData = new double[xData.length];

		double currParamValue = 0.0;
		double deltaParamValue = 0.0;
		// Extrapolation calculations!
		if (getSensitivityParameter() != null) {
			int val = getSensitivityParameterSlider().getValue();
			double nominalParamValue = getSensitivityParameter().getConstantValue();
			double pMax = nominalParamValue*1.1;
			double pMin = nominalParamValue*0.9;
			int iMax = getSensitivityParameterSlider().getMaximum();
			int iMin = getSensitivityParameterSlider().getMinimum();
			double slope = (pMax-pMin)/(iMax-iMin);
			currParamValue = slope*val + pMin;
			deltaParamValue = currParamValue - nominalParamValue;

			getMaxLabel().setText(Double.toString(pMax));
			getMinLabel().setText(Double.toString(pMin));
			getCurLabel().setText(Double.toString(currParamValue));
		}
		
		if (!getLogSensCheckbox().getModel().isSelected()) {
			// When log sensitivity check box is not selected.
			for (int i=0;i<allData.length-1;i++) {
				// If sensitivity analysis is enabled, extrapolate values for State vars and non-sensitivity functions
				if (getSensitivityParameter() != null) {
					ColumnDescription cd = getOdeSolverResultSet().getColumnDescriptions(getPlottableColumnIndices()[i]);
					double sens[] = getSensValues(cd);
					yData = getOdeSolverResultSet().extractColumn(getOdeSolverResultSet().findColumn(cd.getName()));
					// sens array != null for non-sensitivity state vars and functions, so extrapolate
					if (sens != null) {
						for (int j = 0; j < sens.length; j++) {
							if (Math.abs(yData[j]) > 1e-6) {
								// away from zero, exponential extrapolation
								allData[i+1][j] = yData[j] * Math.exp(deltaParamValue * sens[j]/ yData[j] );
							} else {
								// around zero - linear extrapolation
								allData[i+1][j] = yData[j] + sens[j] * deltaParamValue;
							}						
						} 
					// sens array == null for sensitivity state vars and functions, so don't change their original values
					} else {
						allData[i+1] = getOdeSolverResultSet().extractColumn(getPlottableColumnIndices()[i]);
					} 
				} else {
					// No sensitivity analysis case, so do not alter the original values for any variable or function
					allData[i+1] = getOdeSolverResultSet().extractColumn(getPlottableColumnIndices()[i]);
				}
				yNames[i] = getPlottableNames()[i];
			}
		} else {
			// When log sensitivity checkbox is selected.

			// Get sensitivity parameter and its value to compute log sensitivity
			cbit.vcell.math.Constant sensParam = getSensitivityParameter();
			double sensParamValue = sensParam.getConstantValue();
			getJLabelSensitivityParameter().setText("Sensitivity wrt Parameter "+sensParam.getName());

			//
			// For each column (State vars and functions) in the result set, find the corresponding sensitivity var column
			// in the result set (a value > -1). If the sensitivity var column does not exist (for user defined functions or
			// sensitivity variables themselves), the column number returned is -1, so do not change that data column.
			//
			String[] rsetColNames = getPlottableNames();
			for (int i=0;i<allData.length-1;i++) {
				// Finding sensitivity var column for each column in result set.
				ColumnDescription cd = getOdeSolverResultSet().getColumnDescriptions(getPlottableColumnIndices()[i]);
				int sensIndex = -1;
				for (int j = 0; j < rsetColNames.length; j++) {
					if (rsetColNames[j].equals("sens_"+cd.getName()+"_wrt_"+sensParam.getName())) {
						sensIndex = j;
					}
				}
				yData = getOdeSolverResultSet().extractColumn(getOdeSolverResultSet().findColumn(cd.getName()));
				// If sensitivity var exists, compute log sensitivity
				if (sensIndex > -1) {
					double[] sens = getOdeSolverResultSet().extractColumn(getOdeSolverResultSet().findColumn(rsetColNames[sensIndex]));
					for (int k = 0; k < yData.length; k++) {
						// Extrapolated statevars and functions
						if (Math.abs(yData[k]) > 1e-6) {
							// away from zero, exponential extrapolation
							allData[i+1][k] = yData[k] * Math.exp(deltaParamValue * sens[k]/ yData[k] );
						} else {
							// around zero - linear extrapolation
							allData[i+1][k] = yData[k] + sens[k] * deltaParamValue;
						}						
						// Log sensitivity for the state variables and functions
						double logSens = 0.0;  // default if floating point problems
						if (Math.abs(yData[k]) > 0){
							double tempLogSens = sens[k] * sensParamValue / yData[k];
							if (tempLogSens != Double.NEGATIVE_INFINITY &&
								tempLogSens != Double.POSITIVE_INFINITY &&
								tempLogSens != Double.NaN) {
									
								logSens = tempLogSens;
							}
						}
						allData[sensIndex+1][k] = logSens;
					}
				// If sensitivity var does not exist, retain  original value of column (var or function).
				} else {
					if (!cd.getName().startsWith("sens_")) {
						allData[i+1] = yData;
					}
				}
				yNames[i] = getPlottableNames()[i];
			}
		}
			
		String title = "";
		String xLabel = getPlottableNames()[getXIndex()];
		String yLabel = "";

		if (yNames.length == 1) {
			yLabel = yNames[0];
		}
		// Update Sensitivity parameter label depending on whether Log sensitivity check box is checked or not.
		if (!getLogSensCheckbox().getModel().isSelected()) {
			getJLabelSensitivityParameter().setText("");
		}
		
		SingleXPlot2D plot2D = new SingleXPlot2D(xLabel, yNames, allData, new String[] {title, xLabel, yLabel});
		refreshVisiblePlots(plot2D);
		setSingleXPlot2D(plot2D);
	} 
}


/**
 * Sets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @param odeSolverResultSet The new value for the property.
 * @see #getOdeSolverResultSet
 */
public void setOdeSolverResultSet(cbit.vcell.simdata.ODESolverResultSet odeSolverResultSet) {
	ODESolverResultSet oldValue = fieldOdeSolverResultSet;
	fieldOdeSolverResultSet = odeSolverResultSet;
	if (odeSolverResultSet==null){
		setSingleXPlot2D(null);
		return;
	}
	firePropertyChange("odeSolverResultSet", oldValue, odeSolverResultSet);
}


/**
 * Set the odeSolverResultSet1 to a new value.
 * @param newValue cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setodeSolverResultSet1(cbit.vcell.simdata.ODESolverResultSet newValue) {
	if (ivjodeSolverResultSet1 != newValue) {
		try {
			cbit.vcell.simdata.ODESolverResultSet oldValue = getodeSolverResultSet1();
			/* Stop listening for events from the current object */
			if (ivjodeSolverResultSet1 != null) {
				ivjodeSolverResultSet1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjodeSolverResultSet1 = newValue;

			/* Listen for events from the new object */
			if (ivjodeSolverResultSet1 != null) {
				ivjodeSolverResultSet1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP2SetSource();
			connEtoC9(ivjodeSolverResultSet1);
			connEtoC11(ivjodeSolverResultSet1);
			connEtoC13(ivjodeSolverResultSet1);
			firePropertyChange("odeSolverResultSet", oldValue, newValue);
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
 * Creation date: (2/8/2001 5:27:49 PM)
 * @param newPlottableColumnIndices int[]
 */
private void setPlottableColumnIndices(int[] newPlottableColumnIndices) {
	plottableColumnIndices = newPlottableColumnIndices;
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 5:27:49 PM)
 * @param newPlottableNames java.lang.String[]
 */
private void setPlottableNames(java.lang.String[] newPlottableNames) {
	plottableNames = newPlottableNames;
}


/**
 * Insert the method's description here.
 * Creation date: (5/18/2001 10:42:41 PM)
 * @param newResultSetColumnNames java.lang.String[]
 */
private void setResultSetColumnNames(java.lang.String[] newResultSetColumnNames) {
	resultSetColumnNames = newResultSetColumnNames;
}


/**
 * Sets the singleXPlot2D property (cbit.plot.SingleXPlot2D) value.
 * @param singleXPlot2D The new value for the property.
 * @see #getSingleXPlot2D
 */
public void setSingleXPlot2D(cbit.plot.SingleXPlot2D singleXPlot2D) {
	SingleXPlot2D oldValue = fieldSingleXPlot2D;
	fieldSingleXPlot2D = singleXPlot2D;
	firePropertyChange("singleXPlot2D", oldValue, singleXPlot2D);
}


/**
 * Sets the xIndex property (int) value.
 * @param xIndex The new value for the property.
 * @see #getXIndex
 */
private void setXIndex(int xIndex) {
	int oldValue = fieldXIndex;
	fieldXIndex = xIndex;
	firePropertyChange("xIndex", new Integer(oldValue), new Integer(xIndex));
}


/**
 * Sets the yIndices property (int[]) value.
 * @param yIndices The new value for the property.
 * @see #getYIndices
 */
private void setYIndices(int[] yIndices) {
	int[] oldValue = fieldYIndices;
	fieldYIndices = yIndices;
	firePropertyChange("YIndices", oldValue, yIndices);
}


/**
 * Comment
 */
private synchronized void setYIndicesFromList() {
/*
 * ONE SHOULDN'T HAVE TO DEAL WITH THIS CRAP !!
 *	
 * this is a workaround for Swing double event firing (which would result in double plotData building)
 */
	boolean different = false;
	// we build the new array
	int[] indices = new int[getYAxisChoice().getSelectedIndices().length];
	for (int i=0;i<getYAxisChoice().getSelectedIndices().length;i++) {
		indices[i] = getYAxisChoice().getSelectedIndices()[i];
		// while building, we also check the stored (maybe new) value
		if (i < fieldYIndices.length) {
			different = fieldYIndices[i] != indices[i];
		} else {
			// old array is smaller size
			different = true;
		}
	}
	// while equal so far, old could be larger
	if (fieldYIndices.length > indices.length) {
		different = true;
	}
	if (different) {
		setYIndices(indices);
	} else {
		// do nothing, we are dealing with the duplicate event
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 4:56:15 PM)
 * @param cbit.vcell.solver.ode.ODESolverResultSet
 */
private int[] sortIndices(ODESolverResultSet odeSolverResultSet, int[] indices) {
	//  VERY PRIMITIVE SORTING ALGORITHM...
	//  REPLACE WITH ELEGANT SORTING ALGORITHM LATER...
	for (int i = 0; i < indices.length; i++) {
		for (int j = i + 1; j < indices.length; j++) {
			ColumnDescription columnDescriptionI =
	            (ColumnDescription) odeSolverResultSet.getColumnDescriptions(indices[i]);
	        ColumnDescription columnDescriptionJ =
	            (ColumnDescription) odeSolverResultSet.getColumnDescriptions(indices[j]);
	        if (columnDescriptionI.getName().compareTo(columnDescriptionJ.getName()) > 0 && !columnDescriptionI.getName().equals("t")) {
		        int temporaryIndex = indices[i];
		        indices[i] = indices[j];
		        indices[j] = temporaryIndex;
	        }	
		}
	}
	return (indices);
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 4:56:15 PM)
 * @param cbit.vcell.solver.ode.ODESolverResultSet
 */
private synchronized void updateChoices(ODESolverResultSet odeSolverResultSet) throws cbit.vcell.parser.ExpressionException {
    int[] variableIndices = getVariableIndices(odeSolverResultSet);
    int[] sensitivityIndices = getSensitivityIndices(odeSolverResultSet);
    //  Hack this here, Later we can use an array utility...
    int[] indices = new int[variableIndices.length + sensitivityIndices.length];
    for (int i = 0; i < variableIndices.length; i++) {
        indices[i] = variableIndices[i];
    }
    for (int i = 0; i < sensitivityIndices.length; i++) {
        indices[variableIndices.length + i] = sensitivityIndices[i];
    }
    //  End hack
    setPlottableColumnIndices(indices);
    // now store their names
    String[] names = new String[indices.length];
    for (int i = 0; i < indices.length; i++) {
        ColumnDescription column = odeSolverResultSet.getColumnDescriptions(indices[i]);
        if (column instanceof ODESolverResultSetColumnDescription) {
            names[i] = ((ODESolverResultSetColumnDescription) column).getDisplayName();
        } else {
            names[i] = column.getDisplayName();
        }
    }
    setPlottableNames(names);

    // finally, update widgets
    getComboBoxModelX().removeAllElements();
    getDefaultListModelY().removeAllElements();
    for (int i = 0; i < indices.length; i++) {
        getComboBoxModelX().addElement(names[i]);
        getDefaultListModelY().addElement(names[i]);
    }
    
    if (indices.length > 0) {
        getXAxisComboBox().setSelectedIndex(0);
        getYAxisChoice().setSelectedIndex(indices.length > 1 ? 1 : 0);
    }
    regeneratePlot2D();
}


/**
 * Comment
 */
private void updateResultSet(ODESolverResultSet odeSolverResultSet) throws cbit.vcell.parser.ExpressionException {
	String[] columnNames = new String[odeSolverResultSet.getColumnDescriptionsCount()];

	for (int i = 0; i < columnNames.length; i++){
		columnNames[i] = odeSolverResultSet.getColumnDescriptions(i).getDisplayName();
	}
	if (cbit.util.BeanUtils.arrayEquals(columnNames, getResultSetColumnNames())) {
		// same stuff, maybe more/different data - keep axis choices
		regeneratePlot2D();
	} else {
		// axis choices are different, so update everything
		setResultSetColumnNames(columnNames);
		updateChoices(odeSolverResultSet);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G690171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8FDCD4D576B895991BFC0B0A12DAABB2CCACAAAACCAA36252242E255B636E823520DCA4DB6F25D7D52FE5DFA7CB1B1D0B1D1D1B1E93396F7898644B223CD858CDCCA120930D098E65EB02383B34DBC7EA965775CFB5F396F4D1BB7006C767DFDFEFEBE47776E39675CF34F39675EFB6E1FB788055B4217E7E6958AC266F2C17E77D126A094868A42511099C64405743666892AFF6FG2C9516FD1D83
	4FF048CBFA25B52FC058D5158B6998C8173C541A37915E5789FB2ABAFF0497BFF2F6C35EA7CF04FDB5321C652BD04E8A1ABE3DA99F1E2B81E281C71EB9CEA2FF5226A2991FAA6391B23384A11D525C3A29D846B5C03A93A08AA05E4968D3603912CABEF74FEE192E7D22C8E169C36E2CD5249CD519883BDD0C37DDFE1688FF4E4C791E48FA1256137004C2DE92GD1BEDB08FC77B3F856B41F687E27394BAE15771BC507235C63F4740B6EF227C5AC6F6E336F59F3282E3E05221A9DCE2945A51A6DD63B59A4591D
	3D4D26DE51A184D0996FEE4E25E502BE104EF7935CDFADC4EE8224D38116F2FCC5BB41B7433B8DA0400D3AFE7A379AD91736D3B305346E7BD72E7123EB181B69986FD6F4AD79FBD776A65AAE752B097D17C1DE0927B52F9A208DE090A08CE08B750726D5FF07673EAE29565953636C5D67F26DF7D85CDF00568EF85F3387F2E45CE1516CF4DB840135797CCDE5BA759989985F4BE74C47EC9239935BBF6A70CDC26AED17E52D5031C9EA64292CE88F33C522C7318913DD3A591F6C859EEF7F74167D0D2016ADAC2C
	DA31DCC7F671E3D5AB16496594C80A6CE6093475E554878C70CE78C827E278880A8FD561192F16D2BCC947C0DED19F7AC65BEA2CCBD34E942120F2122E9D426E8D4E9A6EE3F5886D5356653B23246C78BEA6739F3CAEC394DF522F60D9DDF6D1BFA569G48CB9DE84DE37E75E581693B3B07DA730CG8E00C0008800F80064A17431CBD7D50C51471ACDEEE9FF1F5DE1915D82637DF27586F80A03125B54586B11CC3DE6315564361B3A9C22C710E93ED9CDEC50654557CEFD5F8CB2BE97FBC5375CCF5D269ED192
	5D9EC6A32EDECBE85CA24DB55AFBC490506392305C1E556B6069B0F9242FDD9613A4AA94AC3FEFB5E913FED3AB14888540BB334B293544DECBC17F7C132DF945A7518E69343CFF09EE98AE766CE9F2BACF74390CC49341F1125879B3BAF698603D61A45AF860AD020B06F4A2AF67EFEB5ED215D3E7B7136A1B5CC35847A6DB8AC9DFG7A5CD32DF925GF5G16D368C33FEFC39F2ADBF523D0761BCBF5FDA8847CB96094731D30D35A3EF4BD15C16A998FF973G04E198E706F1CEA83734E94A331C526FE3241E78
	DE2A224975C3CFF4E1B2BE7711116727FFE72B5ED7285E177B21DF495B7239F524AD6BC02FCE0000E1EC032EB5A54C27771BBCA2FA71A1B7B820E8D1DA045553F454A85BF7056CDBAB653A2E25E58942FA000DF29FA3FDE52BAC3F9620D2E0736EAE00F781F6G7C8D60D3815B7BB4ADBF8C6A968F30G206007563C5DG1DGC1G51GA9G420F608FG4DG03G61G09G69GC527DB73768258GC2GE2CF231DBFE9EF4E74760F95AA9D902755E5132C6BD6B97F9D397E2B657A6F1075DDAD6BDCA93F6F52
	3445272A77DABF6D757FA3B05BDECCED2B764F65BA36A5314E9A150D37487C1BD436FDCFFEC77FD8AF27C9DBEC135F5F5554619F2A772D42FFD63F7F17407AE8DB81311958AF76CAF522CB6C35400BC74E6BA8A0B61667952B137308946F02F9289686FE8F471FA8A036B7C9125B5E55A709AD22A459FB3B15FC8F1565696BF2A9D3984ABAD9C05A54B9G739027BFDDC046F0333337D7240385E711734F22F3086890FB2052354E3EDE09614FDBCBE26F936210BC07CDDC7B06C066A0C79F198FD9BC9BEDA23E63
	F25BFBE088A2D32AE088A073CB6B66A2150D66C17FA6F1F98E649585605CB070AE4E8D5BCFCE9762BBDCD9FA31CE58548B33868358789C9C204C8D2CCE43EF69577DECBA1749F88F2B6BFF7FC8FCF33458BA1B729F5CCE5A20CE341A7A9CD2135DA351906208A04879DB1653B1425953655C6F9C2419ED1877C18369B700E2F28E0B9EA883DA31D24E1FDECE7A58013EDE33A2956700375793FD22C04FF80014G1C83DEFB0B6463BCAC8F7E35CE0703B5A93149D9446E2F503992ED179E406C3E1A67EF95557936
	33147C841A5F4873A7C3BAC6154F5A75E5D12FDD734F067978EC2C6B0B6B8A08FF0A5F770990C2112063DF5C07162CFBCB1357AA7B934B7F73BA62CF8ED53C92FAB6111BF09457EE3B4FE6F1C0B3BCBB8182G5EA63A1CC368AEB906F473647C0C33D99D6B4EE1FAB03AEFBB10AE669C161FA4BF97FB51ED68C43A6AC0165FA4BFBB8355F4F3B8DD001C9FA6BF23E43A6D67923AA0932FDFA01BB7B3E43ADC48AF87BC5377C89752394EE5B1CF90BC43GE24FD56BDBEAC63AA4B9BF6DDCD6DE4984355C97B9DD4D
	84B6C79BA7B0FA4B84355CB88B57E3022C478496EF45CED04B1DA0A2DD221CBFEF02377D185C7DD624934EE371C051F90C3E74BCA6D736331B37AF601B81DC673172224FD31777DBCE3774BC96D7648769159761C13AE2482F86B0863172C203546559A46E87C1ACBEC98AE2FE139A24AE6F63BE244B1065957CCC36734F54FE708A272B83FCBB40704F747CE5C6BF5245C1FEB240D2DD3A21816EFF67B339CD67B33A767355F41503C897A853CDBE5F3BBD589878079D247F527D076326FE53E039E7G6616BDFB
	8E52E1086DAB445335819923234E477D030C86B2AE25C1FA99C061C49C439FD9057B8721A7A205540515AB7C6D1514CDE475E81E280C61B1AF6FFB030DFB368DC4E6886405CF44F1242FD03D06596771B84DF6BAD04BE34576756AB11E61F62CFF931ED63B5BA3B542A4BC0863CA45FAD2DF323E125132BDA60AFF553507F6644775798F87547A462D79A1AB4F0F3EB1321E49AAFD377798D7B03977743DBF12DC2F09855746AEB1DFE0F3CCE1B07881C0E0B0415FA812F91A74D3D6C773CE2CE3484929C26725D7
	782DE3E2B02B5B02E0250EE5BFE6ACE7FE70B7EB2EEC27127FC2BF78230D54218D52AE00A0GD637170EE25D428117824066BA63B1D2B749E677307947E8D64FBF8597C05F84E83BG69070F91F9BDCCED5966CB766D45B16E26A9C26C0A60AC3DBDAA5B3430AC478572BE44850ABE5557F4E5B7G30FA469D4B17F5CA831A628BD16E3B9DC406831A3BC5146754F29A69545ACC3D5D2225D455CA65B44CABEFD2935793548E56369BB612FD840FA89D81DF331BC54F81373307D0176EE8C51D262CD36BD4F37A18
	2ECEC1EBECD9468B19AE4397AABAB5DC5C155DFEB16AF4CE876A1488B4C2886A3428034C353EF1887A7E476F91BF69237B8DBC3334C9FFDF1207CD1C72139D2DCC7EE71DA8FFB748F6F17957F512F8F1D06EF0CC661347C81FF60B5DEC27C4A4C1D8D59DB18CABAB03179589654C63E5F51808AF22C1D9D9D7F61452322CEE51E3EB35FB6C90AE1072BC328EAEF8AFE782F8DDEFB1211F96DD84734CC5D8FED19771D3133B3B122577F611F14C832D6D905BD8995C9F070FE05B85A44588C56B7ECB376DE87BDD44
	5AAC7AA2256D06433B32C342316D4E6E427A8897C3BCF0B156E7E117F73F5969953739AEE67BB6E11CFE2DD95DEFD859DF195116D1C01746E9E34DE48DEC17441EF272DF0B846D20F673F25300A47BF8A364B357165BEF72372B18EF3751E6ADEDA4AEAD4BAB3420AE451740FCFB894ABBE571366D3596D68669BA228547E3D646F33C0CE060CF60E55CA77A73E5166F9251DE2C1C1BC554FD9914D19D0A65EC30323510232F2737CE74185DF697693E9E14FF420A729D40934179FEED7556C1F0FB4B7B4C0D72D2
	012770D2088BE83B1D342A5B2D897092C058253C0F382BC839EC40E10FE651EDF53AFBFC9BE31FEA24417EE49BA0EBA81345024B09F1745FEC8F6A9C8575CA63F5BB6EC11D995D2FBC28E371E5503E17210E75221E0E4117E1B955523F27630A43244D4033C5C964EAD247E3753AC862F38F48CC61F2B3A46FB6AB69736FF7DDFD28DB41A468DB13300CD9FD584E2CAE75BB499E0758CB16A6CD4E6E963157E3176C7DF6E9C89943B677E3D981D0CE6CA4345324F22F3599600B42DA73EA42D056D97D633593137B
	B4175B8CE58605215C573C665CF840AFG483F9C65568F9039E6BA8214FB342301772447F23C65CE99C039A5D0E69BAFF76520376D6E20EBB8FBAF984C64301FC47B555AC4738908F5CA519F3F9D4472C2202CC4DE5EBD3CBC361FF5BFBFE3912EE063EB61952A7DDDF82FD125E96CC94F9EDC0D74AC6C8AF676300A63CFD33C2442337A3CC6E3DF128E033C1C1F63FE79CEFA666802F4B0C084C09CC0AA40321FE38CBBF37DCEE114730846DE89C2DC132345EBC3874F85B6905DCF08C3F27A7B8D44AEB4B291F0
	4F601A4D24FD0BA643B8875084E081881C0CF54858B0EA9D6A87F56B406C5CAF92BBC7CC565FA357834697C879624E000F4D2FBB3ED51F3F662CBD18C5EC20B73FAE164BDEB6D919DF19CD8E96931B54DC897E8860828882083C92ED32FFE359E8B641339F5998CC27B22B76DCAF71CAC53F54ABFDF5E53EE537939FCA3F127956CA0E6F27787C2B94BC73397F21F1B6C917C2DE70D538BF7E0723231DE926DF65DD874617F9ECB43E422B3D79D81FFDF887996FF4560BA41E15770442AC6ABD21322BD9EC5286CF
	87GDBCBCCB463DAA270EA6CBF61DB49F89087699400F4007C7056BC83C09300AB9C5BAAE4637E515AEA9FEE169A1D7C35D63B61093638574796C1615E3A6B5BF02E8FDF39A69D21A9076939FE9B559372D2G5281722F81BD811AGDC57201E4FEE7CF454FECA86DE233396F08DCE67899D95653A7E2CE3FCBACE7261FBDB138E39C60F2F3EF3FC72DAFC7834B60D5415775AB865651BC613972FABEF268F5F587A5D7D237225DC2367B31716901FC9073C7CA92DF95B817682D8G82GA226204FFCD8B4EA5F60
	C751E3699BC1DD63537332D1796226680DAF5D8E22074E7802BBA072DD1763A25EF5C91E02E361B76671790068432775017CEB7578B6DB46E71B52D179366B4AFBF11C72DE9D152FC6D75E8CF1FC763C4F07CFEBCF232E3C6FFD66523169F7FAD4BE072E3CFA77B847A89FBE2DFE01576A79761A965217F5FCFBFF1FA451B39BB6FFCE35EA4FD4487CB9994A0C3E96674FDB68DE9C09C1932FC57FDF69991F7DB60E4AD796214777288F5F586CB75F07CFEB3FBA0DBC2673B06D73240C768854F912B4BE1D270E4A
	972A2B332DEFFCBA777B70E9F50E5215D752BFBEF965BEFCDAF98991FABE7A4FD6FF632F3CAA13FD74CF7DFABE1A8AE526C7200F4EAE47764A1D0A6D15B6CEFD160C2A4F5E29FA7A94FF6C274F35B86CACBEE27A4CAF576AC35662EDD026E3AA6A334BC274A1FBFB01DC1F48017169F33B8F1FD61F85D37578BA8747A72F5707CF9B478957F97331FBE129746C2788722A8198EEA60D5D9BA05D4EF12134EF86C0BA0C63E650B1AA8E524957213D7A9B091D97C0DA1886B686588DE084F0CDC31AF3A9CD88242327
	31F6A0E78264EC126CA3B11A1C46CECD7D8910F2481314ABCC73DDFB91393BE5D9A375FDEF5BED5315152722513B4BC5E29332DEDBB586B984D87CD633497F3D115ACD7A77C69AB7695F9BB934E9AD43C3996A5B0D7C6E6226116F15F4EC72FF2FE47AE67F750C5E2CDF4F5BB76B57B3EE734875CC586C2F1EDB291D9F4BA2EBE3F7DF2FE46F91E9512C6F0B0364EE92C43C077BFAD9B8CCCA77E857738246580F3EC3761366CE43D87A3BADD502EFACDDEF31CBCEB796E294CD6EC3D6AB8F244985922C7761E27F
	B6BAD22CEF230EE2FD9B0945A35B2827581F0DD8DF2C25FDF1B169971158BF6F6D46BE3BBD927B1553CE6EEAB4C05A86908C90457306E9DEB224178294CE07FEB99D73B26D6A7B1048B7963F5782115B9029B706ED76120177G47A3E33960FD6FEDB5772597A4F2F74D67EE541730E29270EC616F47D64AB44D123339121757DDCF4A501BEB4CEE2743C1CE54850CE566DAF07DE21C0EE729D753331FE0C8C7G44F15B06782CCF5410BC5D5BD68B26237FDE3F553F7F5D32D55F7FE6EE25FB624EBE37D9547A58
	5DDB0BF81E32E0A4C7C244BC0C66C14AAF195C5D220F0F4E267CAC4F9F7F63DB49385AE537585D72CDA656AE33B62C217E6F71089E3A69D1E712CCF2FB3C4A5B23DEF256D6F1F95B3679577F7DED7A7A17ED732F7F4EED236BDF334D3F7E355BC6573FE91B3FFE4CF40D6FD06BDA4971431DEA7CCE0E5FE5D2632BB97E77741E9E75E12B5B5953DCA557716A927FB63B2EC45FE6D1A57EEDF6DB4968B60BAB71EF337BCAC637D9D20956E7180E3F6CD277531DFC9CF17A2CCF5530EC3A6F38454A3B402C3665079C
	7F15455B16BBE57CDFC5357CAACE3FD8D417739147C7DA3D4B294636F52B69F771B164A93E86AC389E47107B68B9278469E000086BF18C395D2A3D2FCEA08E72678224DD2FBFD6E7686073EFE84DAB39419BAF1F11FB3C6B0F779B5F1454755F4D5B61AC5DF628164BEE00E73B2E1C5912371C8FE57C55FDEAB99FF37AEF7A5FD47799835A2FFCG7720DDB7207D1E7E0058AF925271GA9B7207D9EAD572BEB3ADC3F1CA808D323746A3AE540CBF6954A5ED401324B2278B7D995C436855243G21D1A8FB8F7D56
	A0AA4A7FFC9A8FF9491A7C852A74B2793D60C6186BEF542B67B35E752CC47C24C1AF7CCE447FE1B371BB17DB340AEE37E8E9311FA4F31B6BC622C77E96225BEEF82F3B914F6AE61B495D8F07E94859A789C249CDB4A6616784B1C017843094A077A63616E4F36AAD83242D2DEED38F8C03B438DAC6F9EF6077CEED109E86188C908710FA934ACC227D89986DBDFDBD34062CFED75038276E66563C661B117640B05133ED5F205D53E46AA217A6983E1F620F7860F7E412F11A5D12D39D7AF2FB363FCFE2A5554CCF
	0B104FC1CF2DC2D9FCD79379D6FCC64A7D5AA33A35770159596DDD0D6A335B611B192D42EF46335D42E63D7C181BB1A6F87C9DA21B4FAFDEF14D37D65FB67A1E6FC5A55E4C5AE89EBC175E4C3E37E1ED71C4B75264C7839EE0AFC0A7C0C0B45A372E046805BA913132B9E559CB7E3ADE13EF04096600DDF4D8B8CD36D9DDC67D204B6DDD46AFFB36E872C58F79B8CAA90959A85A2E359151A5BC9AED78CE27BABFA61A6527727CCBBB747853F97E579DFA7CB937E0FF7E4D087B31C53778BBB3DCC76F6F145D424E
	8633B9FEAB455728706C4CF0AA3FCB5484F95137E05CBF4D0CF1FF612D08CB247168EEC89BB9EE82DF535BEE453A7F4EE79E09053C24DBD91D675D2ABFC6AD5541675E561AD7FC1B5EF97CC7744E0D61B6264FF20E5FCF71F5AABC53F3963F5B5386F9F137E17DD3687CD892531AE708C15C27346F57C03A1D63FE24FA3AA09D44F1D7D15CE4C847F21C590276C86238F3A8AE9D5239B39097A6225CE20EFB155B321463B6D91137E9865AF7274F3ED0678C45C6528CFF3EF1381C58A0E886334D8A0EBFC67161AA
	BC33D99A1D67C8BA96720AEF47BAAD22FBF681108E6138C3F42E1A8C69E80E3B3C8275C9389D6B7EF605366E05B3D577AFE67A2B7B35F45E2B1929BD8B0F267836195A3370DD95F896AEC1DE4ACC2CD3BBED0B1AD87061D86E6B94E703F4G473DC2F121100E6438BD34FD62A01D42F11D14EEB12405BB90B71D7BC0A147AD69A2B88B248781026FA05F2D5FC92EA24A63D21439841E3D62C0893F34D3E76FA11306333734F217212C64C303E1873B6B90BE203E6B307B4D33F53F01A1B0778EE64FB479997C7A65
	723766A7AC782DF9711DE00BBBF1AE2D245FA47075A929481EBD1F3B6D167D266EA6BA6A335864B62AA3618F3E93E313796CBBF2C82782AC3D936F968F3E0FF70B232E1BA24CFDCE7F3E77BC638FD979F7495F0D5C253A0B7B772EECA6739191E5BAA0BF62AE02FB5D6B9E9931659DB9EC1F1465D7FA5D25A3F3430302BA5F6C15CF6A7BCA26BA7FD22FFCE223BF4A7CCC37075FC35D16BD9CADB8E6847A7D6EFF312CD3C11C2C5B6C4BB38B421A5FE03E705B3FAB3E70E443C83ED0352B2CE4A77802FCB75F6A67
	EEBE1D76185D16B4E29C5990077B547FEAC6DCC09C5E473E5C0ABE918B38F4CEBB5F0AF6AF7D8536751AEEC25B8969C10010DFA06D6B5DC89B798BEC238FEC5AB63A412B8DBAEC5AB632E72973AF32E95B28A2DB1DFF1FCD5BC66F4A792CCEAF58715B04F93FC0BFFF2F544B4F953B71C0007136F5A2EF515D28E3D2875A2E196302BB10AE68EE6C730B79B890FFB70EFF1B4CE47F30601718FE17264937A3ACFDC3D71B1C6FEF6E7FF70049C820B234FBDB7FA9E0715E5DE60C77CAC1179A00F600E100493F44F8
	6F99551DDEF0EDF281C14075C10685ED9C8774CD7184F725D8A71719936FEB9F0CEB900A00268CE03DDC4E5EF8B6C750710336CF8E7555D040C74673395AC6F0C910CE63385F58F06E4F6038C9FC6E4F3F074F8FD6EC6F920E3309B8FF3B6E41B67E145285C2BA02639AA80EFC43C43EBBE238B9A2E9A7C2731FEC1FC039BCA6E38115616FBB7371C2366A39CEF8195E031620E331316EAF6433DEB94F58CD7A2E595963F2760A3D52A1379D7E279F6CB0BF6825ED14GF51EFB8F7A4DE29BDD331986B5EB36E954
	563977423CFBAF525EA47ADF03BEC8697782ED9340603D746E35573DB4BD3D568927C57DB5E3947559F8A8A769DE1C63BF7800684A4AAE300ED4B6ABE3BF75038C204F398FFD68A05D03B0C03A0E63AACE72387BBE7417B69A4745C2BA99E09947DBA81EFCD7C66E70EECFC07C228FB0CEA7743C7F4A6F31777DFBFE47E4E417230C651ABC351F7CBBFE36CE482C70762FCB4B957F32D690D96C6E35C536718F95B8C755A5605892DD01E3CBDB027E580231B13D678FB484F0EC89C8500EAD3B280CE04007A7E01B
	4598F8BC4C5BE0FA855983A83C9FE2FE009A00667B79FE9C4D9B04F498C06C7D6C1BBF79B78FAA74766A34C04AA2E5E33A66FE7FEDDA2A4AE3B20CE31231D2530EABC620D57BGEB9F8F5D97F5196C3D920DD8BCF29BCD6471E54A7D58176F363C0934C63B7904877B4CE37C1CE4B1274D9555F71E995DB65A5F499E6FB2CE77558863833BBF167FC0EBDE5183C8BFE0A17A49E30FCCF78115CF786A9EC0FF0A3720BF999F985D1FECC0B37C0062CFA18FA87EA4FFD7F15A570F3718D16F498FE02CBD67BB755A20
	608AC79616232DC314DC2F58873477204F233E3B9870398F621E506B25E450EEB59D77BD6184E4335B69925D529043522F33713E5628774A74470D5363640BBDE3BE360E7C1A6E8F14BE2858DDDE9BD3FC358A4FE26597787AE9AF64C5BC087713E38DE40C4FCD04F19620864088A08104A422AD9BF69804D16EDF9A1D2EA631DFF4280EF119FE0F0DF8AE612FFF8A42BDABF01D92F36775C26C04BA3F5FF7C6A4B2FFC8101F31C917E712A7536FDFD5C43F2510D7B08BE29500A600CE0041D97C6E758E791EBCF1
	07FD12E4B25B6857367CD05DAE8EA8E832E14BF47B6FC6BD5D02E731FA044F726DC70C2F39D90FAFC626CF744BF77528F7827579EE9CA75F73E74C47FC709C3A56CA1D25754DE00ADFB0CB6B1B6B3B51B717C2DE5BC37CBE0CEDFC75DEEC57630BFB0859F5AEBC53GB29E524E9FC779FD6B7C07FD5B2084F055G8D8FEB799E7139CFCE20DD26F369144578AE62FC01BAB4E1000B8208D7653131BA1B2FAB12F5785463B40185C0336CE1B6D613F4C112F62C3EE01874AD3A19DFEBF35A4D223C8ECDB3631CD61C
	04EB6337AC5A3571AB02FAEDDBE7512E0DB33D727BAD5A3571BE2F7D0BC80BF6ED0C7C2C2EEB44BD70D4FF33657509B29D144A3F783A4958585CD45F517825317EF06B3E260E167A267ADAE36321AFF10DBDDB44751A91F45BC64B06F1D040FF691F796220EF53F8AD0DCC4972B8B5BF9F4729629B2E956A9E2F4BFAD7509F270213D8DB25A6A973D7E0FB75CA79CC2D5B771C220A1FC1947E8A5606GC6G5B2F581DB146F7050D4498A6073D3B178CCEC7B09638152E818300B6940061F2683EC2822467F29CDB
	D3AC03F46EEC0C853E3222FFD54F5677AF0C2188B4GCD7BEC453FDC33957FE2B2585AF39870C133315E49EF925F5827FC53496F8B337C778F92BF28737A9E121130B6D8C00E0C79F7317F10FD026EBD556AAC4E4A1B6A8F98BB0C07BA8EB7FE5EE0145B711D0F14B2AE91C9FB7B5D432AACAB295A35C3662B5B2D7035D9464247FCA57128FA6F2D649F07F4775EB20E1E4C0A116D1EB2DB719597788A139F7531A23FD4FC5B3F7C2AAA90DF35837DF64E9E05EF39E5ACFC0C56B4606B1B03837CFCEC8EBBC35B8E
	4F5DF3585DC666C787CE229FB54F517723BC39BE249FDA00E6F00E62C741F3346354FD6F13FB9C6AAFAA3B1C03F2DE0AC866FB5529A57FA2844F9A65737C0D1B6149F69B752871CC32412CBD8FA3FA054DC17F8D2DA0F5361A9C9E657E53FCFE7E9DB5C7EFCC41F6D87209528ECD953E36B5D720ED53E45BAE03E741DC369EE636FD580036AD1D2BEFDBD28F1C87EA0026F92EE2DB4BDC2DEDEF7C1454738BFBAF358C438DB21CE9D0051B72AD41557639D5387C6C4DFEEC0F971219CD97D5E8EDCA74F14D45B346
	FBEDFA7901BC7FBC0BDEFE58DCEC13A996F51BB05CCB743BFC495DA7327407E296BC2BF6E1DB2C7FD4E90B2FC76A4BD586C3D9C96507BBE45A2996D24EAEFE6F62953E7F97B577CC671361FFAAF528B2E3FDD97C3A037C142EFCEF6809F2721BF43AEB9A08D28FC21D8FF59D07F950E86A66BC419544D67CEC1CADF5643CCBE8FB70EF1569C05982043D5DACBE790E18D10ED5AC2A7717FCFEA772BF89CC469B62CFA90369FF0F417F3D30C4037E3D3059867D3B2C4906116F9D279A7C5FBBFE56207EBDBB566E9F
	6EA5ED13863EA4BC82E3AB40EE0026C7F04D72FDA579CD9F76D528F63DD2EF69A67752482F3BCAA85FD865DF576F2AC67FCDD8CB9509B33A1CB08377107291FF3C0A341764F4D1A443B92B080D5C76EE1B44C87177DD696FE7BBC4ABC533BD26558769D9A668B578886EBBF98E623ED3A047F553731FE4C827F1DCD2B35225BF02EB061FF5ABFEFA316AFDCB77CF6F3355BF298C26F376F13A877CEB984FFF4D776C0F239D5AB92E027E16CC942493B8AE171E7924C0BA1D63A4FADE1473A80C310FA26EAF47303C
	EA0E1B50093C019C97FC8CE504F15C028E2CDF9447B5F6E2F9319C975901F2F31379B9FD875295F35C073C4E8E0E5BF0946586A463F9D6B50F67C31347BA7E26FF46465FD97B94BFD9B8623DE47FEDFF64041AAF896AB0AFF9647DE8564FE76EA77378D220AD788D589B20892093E090A098A09CA086A089A08DA083A07FB1E8AFG83C09DC05BE3B8AE6C28A63FD76AEF5FCBBE982703031B6CCB8A38867F162F419DD0D644E3F86E38334A6BDBA3F63EFE40619CE812C396F97E1B424FD1D39F43B35739344C42
	47E16D82D07738DE3B4C7BCC7E8D929BD9B7794670653575E48D57312F297173AF3F00F7F94E3C0418CD1EB303C70E5DE15E7E4800317B6D758ADF05F5A43E4ADDE5A59F6E5EA16BF7B67F1D30F6DDBD58EF426A4F6FCF9CD05E17FD2E483FEE44FA23FC66A70FB51018B094E4C783A483AC8690D2A0AECD41368F5ADDBAC25B3386D31A1E357B9BC3586EE5D00EA5857B5317C3581E13D330BD07A9EDB224978364BC013483A751B645CF1CE96C63BCA8F09FBAED46EF6B0CCF60F87E3C577D1BE04007BFB1F23F
	E2FACD2CC03DE200FEB12F6B237CACE57BEFD12FCC83BDEF023484907CDB24DDE6C03DA2FE2B5B66FB7D0FA9131A943D5E21651035CEAAAF7BD59B628A1FC45C5D963C7B5C74A46A7FA72F7D61E840A7BCB9327E4C977E39577FFCDF3FF774797E4B3D740CG66E96310CB3E135E32172CD51CD62BC764F1C55B5E820EEB74B4BB499240594B7C7B3D035E67DCF7FEC1EC4256AF679F4475CB0A0EBE6A3589BDA7819A826BE53C102ADD3F7C586CADEB65A1C556334DA8ABBF557FDA491F2C929FD91B0FFB4BAAEB
	D6E41DBE0E324A7C48D26BD526BA7327FB73BE328E18744FEE0F51793D89685BD2F17D332DC37F1C79A3BA078682DD88271D58697F9CA9024E57F1C01B180A6B4D428E54EB290EDE599A3DF21F723E4BD07C14F6CF68E6AA23947055CF61DE548C9ACFF4C25A71941EC79F7ACA31EDE7A756A16C29516FD3E8ED9B65D307C728DDE2811F70946A1949E5246B484856C04E535EFA96BD2D1551C1E5EC873C61E95473B39A3F34C35A76B476730F3D4EC9C2819F7974487D1C45C22E93342DE4398C57C7F149B20E75
	5307777B9F8FFE3DFF747160497D7A717F7C7D3E717F737B75637F457B5571BF433D321F582D9B6212B6CE77A70EBB42F17FC34BA438EF6C964946711935EF4A78861188C47C0ADAD22F810AEB13F179AA5C91993736160CE920324D69C65E8D3524FFDB6D8E87CBEF26B476DE986238BD365512BA5ADDA6D53D37F39C29377CEDF3777862495EF172CDBBE3BEF6B6D3C7FF0BA97DE96D194D97943F6CE96D194DE3BCDE4CFD867A6BB3684767F91D994D85FC5AB3E3192F1EE9706F1F4FB70C6E1FAFB7687B679F
	9BFC7D734F8D7A7EA9B42A7DD37EAD1D2171354366F172351CB19FEB27F49A1BE5BC23ED3FD7A9BEE71E367D4E66F1DF8164C973787730A75572D3G3FF85E486D477882F557CD42FC46DB884F5279FA5F627F2542F77C4AB22847AF792C4C679E76586CF975B879D24E180F593B0F4612B573356D70A345B74C5736438A8336039172E26773EF0FAB55720BFF8773476F46528E6F9B746ADD873CEDG8E00G00505FE94FB0DF53BDEB0D14696260B997A08DA063F76A7BF877696E8566BF8B6B53E799DD7533E3
	3B1755GF46DGAE95BD7307D5222F0F3CA52AFD04454FF79D44F8BD705911765FFFBCACF01E422F783D4DE7B1663826C5490FBE0E79CBF9FE02D1A96B052F49BB0B297EF2EC04B315EAD84ED5D6733318C35FA8FCB91DE367F337A9FCCB0E0D36DF3D53006B57FAFE8738ACCDFF0DA65FC5BB2268967B2DA26B2CB149E2FCCFFE27708DF60C3D0E97F5E29DADE9E86B575A157A180EA965BEF38665D671F2A70F28FBE007A2EBDA27A2AB6F6858F5FF4824703D55BE96BE664F4DBABE7E35170F33B50A69FDB2DE
	48FB76B49412FF93E6F0150247D0C86E2387C93804730B6ABD757FE0CF740F7F87FB22D7D9FDEDF72D55F78E795310227B6B2A7720211F5E8E577C24B2184E171C7435C358C9359D986E67F41FA5997AC2DA9A6E298A7C6EFFBA473DCFF1FB1F033963B91C17CE99943B852A5ED799FEFA9B3E7F134AE0BA97D57A5AF0EB252F8DBF25B10A836CB27C9C5AEB8A4585C1BA1C63561622DDE3B82E65B44111EF2BF21EC75CE40AAB0234016356537B9AF510B6F25CC9BB165749F101E6ACAF1A6392A9AE8152E99C37
	4502B2B2B86EB90A4BFF21B52F7885FEFF1B0EBB25105E4BF121B664B5F25C6FED7CCEB74799F9DDE2B9AE0C6212A03D7885742378036AF8C3D8501AD738E0AC714D262F54FC2540D3B3A63E994755FC4D40E399935FC12F3BDD034093BCA63E4B3CFE3BB39CF8E24644771197DFA270240E4247661ADB4D5EFBD2AFF5913332BD0D0D7CEEEF3ACED938EF02F785F29602DFAED46DE9AC546EC9FDCA6FB5DB1DEE315B6D6C6B3560DE4757541736833DE1A17F5B98866FFA9510CF1F65F26EB050B51B49FC423B1C
	C7E8BFEA06B25A97623ECCE9A96A9030F0743D1FD02089DD286C7DC4AE546E4B1CF59A6FA247703A5EF6DADF2F07CF23DE891C364C6E2D57CED13177C5F62CEB1ACEDD177B296B96B9AF43272EB772FB53B90B7837C8E69243581CEE7BC9E72FE4F26C439BE0B24F2B14072CF38B97613ED39DEF1F3D3C1CE596EFBDDA2DCA7B18C47D76992263C3A7146160E57F4206E507AC42B6DB4E6D903DE8743D348420C9D924785D62C5DABB7C406D10416BFF73A8F618456FB0663E0863516387FD477854033EE33C61AB
	DF3ADDDF7952A59D7725FB6438AF1D2943174E5A61CBF7E327AF5D2D1D3EF4DF6A5035F851317837015FCD597D62C86B2221EE65BD6D386287BF52FBB72A33B90753DD5E65G3FE034879D8AEDEEE911AF2D0304231F3BCDC32CBEC7CA31BE2E916B535323149BF65A7B9ECF244DDF1D180CFB6DA8A372C5DCCB0474AA65A5183D4BAB3014885E77CADB487FF80D42D3CEAF8EB25A1ED16C7126CB11B351B612BD984D73236872A157E51E2E3DC6BADF7B60FB25AE219A1D67195F99D9E7F676AA605C740557D9D4
	5162563C3245E319530E152A7976820FF1CCFC8FFA5D6336814F701878BE71628B811E08D178D85F31D8FC7B135D625B6F7ACAFD69CE167A52BDED7725FB566ECBF7D8076E9BAFBA763766AFE9398A9AB06AA5E13947AD3C653B265CF741930FC7F55495E689D90D429BB63A88EC94168F10D562A078E496891FAF73FCAF103FD8AFE492A37DA089797BAC16923CE2AB89EB581DDBB5264959DD223A5D874257F0610DF4D7FCDE7F4EBD594FDE1B1DA56400E4368B5BA864102DD428001BD5A4DBF23AC8B5049CB5
	C0E9DBC73C3A7B4022E97557CCB6A7E589190DC21ED34920E53D016A50F372A1BFA932FF3CC1A77506CFED10061CEAA6F29A2FFF23F012437EC11EA2B320E7948EEF1AB148120495EDA5643A7E9EF8BB02EF2B3049CA48CF4DC842BA1ED6FEB6E6041A1C89D6107B4D87FED2EFFBD9CF1FDF21B9F30EB3AAD592B2CBG8CG1512301C7DADCE7DD25C42EA6FE1B8F36A46F38AAE5C5917A5649E905E301A9CAE1B89BC1AFE2757A464F5D157965DC0AB8985CD043782F8ABA86FA7CDC21659A389DB943C1960D9EE
	0B4BE49601E4254564BEC1CBA0D5B2FCA72C343A3B3B28GC9487A6E40F7A01626C8F6D5137026E4A3177BC844A6899B15221B491D8EB963935AB5045781DC749FD5370262626363D5B87F8E670B9D93BF7F7BA627555F75E51CEB767B1B9F85CB58583CFD09763B3EBF3452B8937043CB700ECBEDAB79C0595F9D58169E134381D16009FD3D96F29FF67F10A4AAF7B0B3DAC9799E2411B3656F85DBC93D0F2BF87E97D0CB8788A002E52F61A9GGBC04GGD0CB818294G94G88G88G690171B4A002E52F61
	A9GGBC04GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9BAAGGGG
**end of data**/
}
}