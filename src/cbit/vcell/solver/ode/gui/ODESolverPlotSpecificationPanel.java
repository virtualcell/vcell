package cbit.vcell.solver.ode.gui;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.util.*;
import cbit.plot.*;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.solver.ode.*;
import cbit.vcell.solver.*;
import javax.swing.*;

import cbit.vcell.mapping.MathMapping;
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
/**
 * Amended March 12, 2007 to generate plot2D(instead of SingleXPlot2D) when multiple trials
 * are conducted, the plot2D is used to display the histogram.
 **/
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
	private cbit.vcell.solver.ode.ODESolverResultSet fieldOdeSolverResultSet = null;
	private DefaultListModel ivjDefaultListModelY = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private int[] plottableColumnIndices = new int[0];
	private String[] plottableNames = new String[0];
	private int fieldXIndex = -1;
	private int[] fieldYIndices = new int[0];
	private cbit.plot.SingleXPlot2D fieldSingleXPlot2D = null;
	private cbit.plot.Plot2D fieldPlot2D = null;
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
	private cbit.vcell.parser.SymbolTable fieldSymbolTable = null;

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
private void addFunction(cbit.vcell.solver.ode.ODESolverResultSet odeRS) throws cbit.vcell.parser.ExpressionException {

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
private void connEtoC11(cbit.vcell.solver.ode.ODESolverResultSet value) {
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
private void connEtoC13(cbit.vcell.solver.ode.ODESolverResultSet value) {
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
		if(getOdeSolverResultSet().isMultiTrialData())
			this.refreshVisiblePlots(this.getPlot2D());
		else this.refreshVisiblePlots(this.getSingleXPlot2D());
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
private void connEtoC9(cbit.vcell.solver.ode.ODESolverResultSet value) {
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
			ivjFunctionNameTextField.setSize(new java.awt.Dimension(600, 30));
			ivjFunctionNameTextField.setPreferredSize(new java.awt.Dimension(600, 30));
			ivjFunctionNameTextField.setMaximumSize(new java.awt.Dimension(600, 30));
			ivjFunctionNameTextField.setMinimumSize(new java.awt.Dimension(600, 30));			
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
public cbit.vcell.solver.ode.ODESolverResultSet getOdeSolverResultSet() {
	return fieldOdeSolverResultSet;
}


/**
 * Return the odeSolverResultSet1 property value.
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.ODESolverResultSet getodeSolverResultSet1() {
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
 * Gets the symbolTable property (cbit.vcell.parser.SymbolTable) value.
 * @return The symbolTable property value.
 * @see #setSymbolTable
 */
public cbit.vcell.parser.SymbolTable getSymbolTable() {
	return fieldSymbolTable;
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
        //If the column is "TrialNo" from multiple trials, we don't put the column "TrialNo" in. amended March 12th, 2007
        //If the column is "_initConnt" generated when using concentration as initial condition, we dont' put the function in list. amended again in August, 2008.
        if ((!cd.getName().equals("TrialNo")) && (!cd.getName().contains(MathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT)) && (cd.getParameterName() == null)) {
            plottable++; // not a parameter
        }
    }
    // now store their indices
    int[] indices = new int[plottable];
    plottable = 0;
    for (int i = 0; i < odeSolverResultSet.getColumnDescriptionsCount(); i++) {
        ColumnDescription cd =
            (ColumnDescription) odeSolverResultSet.getColumnDescriptions(i);
        if ((!cd.getName().equals("TrialNo")) && (!cd.getName().contains(MathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT)) && (cd.getParameterName() == null)) {
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
public javax.swing.JComboBox getXAxisComboBox() {
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
		if((getOdeSolverResultSet()!= null) && getOdeSolverResultSet().isMultiTrialData())
			plot2D.setVisiblePlots(visiblePlots,true);
		else plot2D.setVisiblePlots(visiblePlots,false);
	}
}


/**
 * Comment
 */
private void regeneratePlot2D() throws cbit.vcell.parser.ExpressionException {
	if (getOdeSolverResultSet() == null) 
	{
		return;
	} 
	if(!getOdeSolverResultSet().isMultiTrialData())
	{
		if(getXAxisComboBox().getSelectedIndex() < 0)
		{
			return;
		}
		else 
		{
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
	
			cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = null;
			if(getSymbolTable() != null && yNames != null && yNames.length > 0){
				symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[yNames.length];
				for(int i=0;i<yNames.length;i+= 1){
					try{
						symbolTableEntries[i] = getSymbolTable().getEntry(yNames[i]);
					}catch(cbit.vcell.parser.ExpressionBindingException e){
						//Do Nothing
					}
				}
				
			}
			SingleXPlot2D plot2D = new SingleXPlot2D(symbolTableEntries,xLabel, yNames, allData, new String[] {title, xLabel, yLabel});
			refreshVisiblePlots(plot2D);
			setSingleXPlot2D(plot2D); //here fire "singleXPlot2D" event, ODEDataViewer's event handler listens to it.
		}
	}// end of none MultitrialData
	else // multitrial data
	{
		//a column of data get from ODESolverRestultSet, which is actually the results for a specific variable during multiple trials
		double[] rowData = new double[getOdeSolverResultSet().getRowCount()];
		int[] plottableColumnIndices =  getPlottableColumnIndices();
		PlotData[] plotData = new PlotData[plottableColumnIndices.length];
		String[] yNames = getPlottableNames();

		for (int i=0; i<plottableColumnIndices.length; i++)
		{
			ColumnDescription cd = getOdeSolverResultSet().getColumnDescriptions(getPlottableColumnIndices()[i]);
			rowData = getOdeSolverResultSet().extractColumn(getOdeSolverResultSet().findColumn(cd.getName()));
			Point2D[] histogram = generateHistogram(rowData);
			double[] x = new double[histogram.length];
			double[] y = new double[histogram.length];
			for (int j=0; j<histogram.length; j++)
			{
				x[j]= histogram[j].getX();
		        y[j]= histogram[j].getY();
		    }
			plotData[i] =  new PlotData(x,y);
		}
		
		cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = null;
		if(getSymbolTable() != null && yNames != null && yNames.length > 0){
			symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[yNames.length];
			for(int i=0;i<yNames.length;i+= 1){
				try{
					symbolTableEntries[i] = getSymbolTable().getEntry(yNames[i]);
				}catch(cbit.vcell.parser.ExpressionBindingException e){
					//Do Nothing
				}
			}
			
		}
		
		String title = "Probability Distribution of Species";
		String xLabel = "Number of Particles";
		String yLabel = "";
		Plot2D plot2D = new Plot2D(symbolTableEntries, yNames, plotData, new String[] {title, xLabel, yLabel});
		refreshVisiblePlots(plot2D);
		setPlot2D(plot2D);
	}
}


/**
 * Sets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @param odeSolverResultSet The new value for the property.
 * @see #getOdeSolverResultSet
 */
public void setOdeSolverResultSet(cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet) {
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
private void setodeSolverResultSet1(cbit.vcell.solver.ode.ODESolverResultSet newValue) {
	if (ivjodeSolverResultSet1 != newValue) {
		try {
			cbit.vcell.solver.ode.ODESolverResultSet oldValue = getodeSolverResultSet1();
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
 * Sets the symbolTable property (cbit.vcell.parser.SymbolTable) value.
 * @param symbolTable The new value for the property.
 * @see #getSymbolTable
 */
public void setSymbolTable(cbit.vcell.parser.SymbolTable symbolTable) {
	cbit.vcell.parser.SymbolTable oldValue = fieldSymbolTable;
	fieldSymbolTable = symbolTable;
	firePropertyChange("symbolTable", oldValue, symbolTable);
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
    int[] indices;
    if(!odeSolverResultSet.isMultiTrialData())
    	indices = new int[variableIndices.length + sensitivityIndices.length];
    else indices = new int[variableIndices.length];
    
    for (int i = 0; i < variableIndices.length; i++) {
        indices[i] = variableIndices[i];
    }
    if(!odeSolverResultSet.isMultiTrialData())
    {
	    for (int i = 0; i < sensitivityIndices.length; i++) {
	        indices[variableIndices.length + i] = sensitivityIndices[i];
	    }
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
    	// Don't put anything in X Axis, if the results of multifple trials are being displayed.
    	if((odeSolverResultSet != null) && (!odeSolverResultSet.isMultiTrialData()))
    		getComboBoxModelX().addElement(names[i]);
        getDefaultListModelY().addElement(names[i]);
    }
    
    if (indices.length > 0) {
    	//Don't put anything in X Axis, if the results of multifple trials are being displayed.
    	if((odeSolverResultSet != null) && (!odeSolverResultSet.isMultiTrialData()))
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

public cbit.plot.Plot2D getPlot2D() {
	return fieldPlot2D;
}

public void setPlot2D(cbit.plot.Plot2D plot2D) {
	//amended March 29, 2007. To fire event to ODEDataViewer.
	Plot2D oldValue = this.fieldPlot2D;
	this.fieldPlot2D = plot2D;
	firePropertyChange("Plot2D", oldValue, plot2D);
}
/*
 * To get a hash table with keys as possible results for a specific variable after certain time period
 * and the values as the frequency. It is sorted ascendantly.
 */
private Point2D[] generateHistogram(double[] rawData)
{
	Hashtable<Integer,Integer> temp = new Hashtable<Integer,Integer>();
	//sum the results for a specific variable after multiple trials.
	for(int i=0;i<rawData.length;i++)
	{
		int val = ((int)Math.round(rawData[i]));
		if(temp.get(new Integer(val))!= null)
		{
			int v = temp.get(new Integer(val)).intValue();
			temp.put(new Integer(val), new Integer(v+1));
		}
		else temp.put(new Integer(val), new Integer(1));
	}
	//sort the hashtable ascendantly and also calculate the frequency in terms of percentage.
	Vector keys = new Vector(temp.keySet());
	Collections.sort(keys);
	Point2D[] result = new Point2D[keys.size()];
	for (int i=0; i<keys.size(); i++)
	{
        Integer key = (Integer)keys.elementAt(i);
        Double valperc = new Double(((double)temp.get(key).intValue())/((double)rawData.length));
        result[i] = new Point2D.Double(key,valperc);
    }
	return result;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEBFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8DD8D45735A809A41204D41328A189E90D12DC1258045EF27BE8EB9B1AFADB5A50778C57D65BC7EE3D2D6F46765A56A636653634B97C98A1A1469F0C987FB0EA440808B0AA629F480F90D1090C080A4AB0F3E08EF3664719C1D0D15F5AFB1F354F19B3E798244D773E6777AD4F59EB2F3557DEEB2F3D775ABFE7908AB7CEDE1C15DDA888D90B85655F3EACC1A80C9404BF1DEADF02382AEDE9790266
	5FA600654246733970EC02BC7FF3E9794B040E3DF9106E04F44A53E9792B61FD25F0F1EF57ADF889A1278A72F20F4C378FAF675915A8E789CDA7DFAC0067D200AE40116734EDC47E1C0BC58ABED241A3E4E58BC2CC8921F97362BA853790E872GE7863017B07A88F8328A5CD3DD2550F5BE94AFAC7A21B77BBDD20E26CC04398AEF6EF676AC967E1CB5EDBB11356ED20142F39052FEG22FC0E903F79884135BAFA8FDB3BA5FF59D52BA84BE5BE37FCD5741639EDE2D9EF3FD4DDFD2A3E210D22DAE5373F4DA3DA
	259E49EA71CBEED72B45A54AC249A7C4066DD29EAD9374017293A9AE290F481561BD92A0769344FF3F1B60B3487B0E347C129D286B21ED8795DDBAEE7C8FE1D605B3777D903A566CE0BA5AF7283A26763D163D06364B2F8A097D63A0AF85A09DC0A8CD4BAF862881D8CB7DE1614ACF60D95B6D2FF3BB1DEED72D47F3D436F9CF0056B23CD7D7C30E02BBADDA5DDE1BA030B67F68D229C1FD068346D7FE47FC4CA67115587E53CE3FA024FFF5D27692831B24475F486EA8E53670142AB6E1322510324717867AE720
	6C3302D6F64A159F67ACB610BD6F520FF36214F212B432B3CAC9DB17BAC8DB4F01F74277AEE9F70A2F267803BBD5BC7355EFD31FA169CE48CB5E093E5131946B5212BBC5D8367BD1C3BB443C9C1D3D250C5561E0193EAEEFDAC95976B2A6F3872F4BF29B4127E8702CAEBF22FECA52A53B40074B537219FF3D7181693BF3F72765E78294D62465178234G588106AA504776976E9A210F35DA3C7E237D12EC933D8263FD237083F80A03FE2F25596573DBDCD6315D6215AC5D326893941A3F94929BF48770892B08
	6FDBC146B151A5FA15FE6A35B8C53F6875B11A7CD54B890DD72439E649A902G27C740F2172FDAC97A2D4567BF6331D97C22CA4172572CA2EDF255528EA5C281704E6C32310858AB9674CF8418416DB00116F7DC7442F0D1DD5D62F6BB7ABDE62209D0D8C96C9CC1470EB9C03F3082ED3CF68D41F5C03E27924BF9E5558ACDB9751215D45F623D06FD2C4281833B90897489GB38196G94185007FE5B01BED47F5E1705529FCFB474210E4BBF4EA9B1B15F29B1697BD28D15C16AE9073CA800F80094934E89B37A
	BAF46595180C7B9829A73E4F543CA7D49A5393DD180C5702E43C9B22FF67E85E17E85E9707204F67EDF95F0724AD97C05D737640983D875B606D8F0A19CF9F3578C47462D3DEF0C0512636882B67FFDB4D0AFD17A83E152F54F5B973D5E19540EA250F113E32DE11DF8730DBE0736E9E004DG5B8136819C92303DD75BC879B5D0B7BB40F800A40039GF9FB53724B81BAGA2G6281E682E4G946FCB4BEF8270G44GA4834C87D8D61516DF8550D505F67E376E562CC07FD8225181F1FADD56A83A2ED772B7A9
	75DF2A54FF3B226FD2C5675D4A7B9EDDDB9C523C5705E82F7F9F01597693AB312D56BF979B581644BA6FEBEC3CD661DF23316DD6659D7DE31512A6ED31D1F95F2C2B438E4D7BFA619F2B5F7FCBE0FDB47383E2B3712A6872570B9E51E503971F1217331A58D819D7FA5C1CA71F62BDB08F5541406F637877D7935BDB7CFE2F545D6F975BC43FDFF2752A792BD793D93E7EEE0FBA05212C8D2BC91B3A87E09E62741BD713B15C6AF639C4BAD8F096A5FFAB1DC3C4D9F4C2256B5C7DAEBF436FA8A23137C33C264CE1
	3B0A5E96489CA4771379104533899662BB9E2F6404A108CC29421CAAB23FBCFA29C8E323A8401138FC9ABC132BF0EE98580CF343166B4F8A4917BD59C631CE4C5363320B7733715934DF1D9BD81D0A5798577DE3BA17A9F89F2B6B1FEE915F8C97DB67D07ECF3710B62897FBAC7D323FC57279E988F1CE90147C350B699861F6F63B0F3A87E9E687666DECA27D86D0CC4EE95187E5C0AB6ED6721FADA3FD2C315FE5D525629C7072DA220F99743483C46C47B9E04A9A120F7330B2785739E519B5A964256CA7F69F
	CB67CAEE3B7D4C6ECBF97ED7837217D52B79F2B5497FB94F2F07F427A61F356BBD32D13BA682DDCAB556F55C074B08FF0AD77AC58821C850F11C7B50248FD768725A95FFE279DF7C1078132C09D78E9EA0F2BBED38F61BDB4D62008CF866C1DE894086224BC1C237C3C43A2883ACFF7281D6478587189E0C6E8DCE57F910657B15670403DA3A97FA10AEDD49DF24BC738EE969FAFB112E64904B2FD11EE605AE759021AB36737A9DE27366E405AE811EE500E77A7E1E53959DE6B1CFB9BCEBG3A8EEB75CD16104E
	2F64C79FE665259C564A9D5307F4738E33B9BAD321AFB82215EBF6F0BD0EA8FA9CE171D657912D5C8DB252F9147C28A30176E3F2FF4669620F30F8A0D9211F2948D56CBC485B977099GC5B52C3C0E9AEDF95EEBC897DB43620A449A23728EDEC73A99101F8E102914F730D6DB1EF0037BC1AD0BCF7C354CEFA2EB35653D4A69A657B2F9C9350A1DEB35FEF05F9052ADGFC6E51347CADC70D7C6544CD2433C17E90C02CA15D72DB5C7F0EB2390B943A5CBAAD5D8FB8DDE99D232B2F8BEC8FB6863E391D74DF3A7F
	50E739EA99AC738D405CD2DD7DA99D06583EC256E5B2F612B15ADC077B8772B1B2AEC5C3BA8EE0FA9D0E612F3E077B87939C71C21CF7D6CE283D12D9CAFDB36A54B13C34EBA0070DFB856B094C2AFA08936BF19CD933D63B062975795CD6098E544AD81132D6BB46B35C0F563E834F9E496B73B743A4BC08634A3F2DA575A56BAB852D58638171A3C3FB68C7FEDC1F5FEC546ABB7D7D1B596F065037331E69B9D42F6A3B6C649FF3185C6F74FFBC1C5C0009855746EF4B85821BE326C3190BGCA9B883ECEA673B4
	69272C0E56CA2C63786BCF8BDD939F8FD9C7CF832B5B7886CD9D7F5D14437C60DBF21EE22714867403875C248EC2A374CF00F2GD637FBECD837A640C5G3039EE010D544DEF17FCEC7EF9DD524EBFC9C0178EA09CC37AC21148F3B235951B3FD1DB2350273EB0C5C8DA921DED34C7E57F27186C42E34ABE44B14DFE5C5C57321381D8BD4DE20122D3B4504C60F23FA99299B2B4F71B284C29E5B4522933DBDC3D222DC453CAE5B44CABEB5192B79054F656369F2CA67B88BE51FF8EFCCD320A3EC62F5BC928CB36
	3723CED3D6EAF5BAF87B1221CED16F5B33B395DD36B429BAA53F7ADA762CD7D12735BD281387E8621BD0273BFB48DC9B9C07206F6F5FCA7C241F6EB7704C129663FDC99EB6F14A835B5B197C59DC7EDC10DD540C728F7712F8F1D069F0CC667F3EC47A34D76CE5BBA5A28942AA6A09E1D8D963FB312CD6A8A70A177527DE620BE8D0D6561317CBE8D9BDDE51E7EF17FC92048B24BC1F2203875E4B18GDE57E6BB7AE9B2141D414BCF3093BF35F8FBF733749CBB9947FC505A3258414A607EB8F48E5BAEA2B5D1A8
	DA7920E15B91A87F1435D94727EA5BAD7A79EB59F95C9FBF1430BE71C0B377D32C4FDDD2E03F79DEC05CD6F41C6D5B549CC77AE7A5ED3FE1E57FCAC2DB1A01AE1A53367611B53064971DE5643FB6BF3403564D4BAC83FE45471BA11F393652FE31675FE33E5DC11B3524193834A2EFDA9F6AB283E4E5F2F9AB9D0136BD60E0E5102EA35AF0BCE6E55CF4E099261634FC39854BB8A70772E5161FA7233DD8B947E454BD8E4AC867653CA03335105C6FF4550BBE2BD772106E6BC379B9CE14DFF8A2AD3F65847275B9
	83F508998C14B7FB9065C5824FF4G56EEE37A346D3688706BCEC23B1D64FD64DA85A9178DB86C51AAFAFB5CDEE7F0E354EAC69A6CCF768132063258EC381C98C57FFD629A6AEC06FAC5733AE5DFC71D991DF89DF51C81B419A7D1474FA4A39DCD27301C546BFF1F0ECBCE13B6834F967DA2D713BA9E2B57769BFC6E811991DC6E789B01ED16F4A3345F7D79866A16847C8BF99916A1ECE7D61706CA3247A13A485224455D5BA63AFC12DF3AAA792F29E358BF8FE1D9A52D90B737221DBA05003599601381663522
	2CB5B7C7EBA7A6776ACD141B81E516FF06F23F926003F6400F87C87C8C653EC665DA6988D066530F8601139E4B8914DBFD8B6526C01942E9AC7729DB01367BBABDFF13DCE0B00BACDDC77B5559C52B83E21D927447B7F8F9D5D0160717F76EB6164776335E3E0CE7AC71275978BA7D34E6FF975E67E952B47624E78F73E8AC1BF11A1DBD3C4771EFD27C3AB6954F6A6325F8122E013CE9ED38DFBEFBA309250B4C606BGADGB600C800B8B346309756D58AE14EA31ADDFE88F1ADF2DB40068E1E8B2CA73ABB44EB
	CAFA55FAE2979A198838E750300934EFB248CC87D884D0F086E243B3D8075875E16B50B0E8D887E66782195839650C719E3991B03E8FA91F6D8E78587C3A7D3C767CB5F77927594486C6736B84256C38B36A7C4AEC725B0F08CD6681DEE8071884209C20359DED721F6BCB4359844FFE94E3B01DDE167567FA1EF6D53F4876E0DD19EF3DC5636618F666DB799CDF60A178C48D1E795CC3141E24E7C21E69AC6E0FBFE08BF72699F3B6308E0C6F3130FC53F5FC2C4F3E321D0CF7866BC5924FAAFBC25F6E556E894D
	BA4BE29321A3AD3F90002DA5B67461DA2234837BCF7D96B29E58A09D8190831088B087E091C051B9ECAB530623615A2A96B7CB4DEE7EDA275F70C4DB1C977536A8BF97283B318D6DC1FCE53AF40B2E9C26675737D2BDA1AF82A086A091E08E40A20022732867588D074276D3B2701A5DF500EBF23B9D86AAAAF55D54B3BA9DF78571ED50252B4E9B713DB6CAF97F95442737E92B213CC93D23139797442717E7B714F74ABE3AFEF7B1ACDF44F9A31F593F0D78CC8C64A58224824C87A838G7D892065827A4C4F42
	778DFE94BD123ED1AC0DCE4F3230FC368BC663CB2FCC74B098DFF087C4396B72D7875EF5993A00E3616F02E40E4C877E9A44277701448BC6FC7F5CB7BA5BA40765CBB514B746B1BAF98F05651BE7A86FC4505CB6B2FBDE8862535BB353D0DE3EF3F47A2D8E4BD758E9B8C68D0EF20C8A62536BD75AE9645B6F37113EEC605BC77B7DFEFAE643664FE77A74E7AAE47E2C07B2BBBAF17E7C861DBFC98C6A69C47FFF6A5A686C77E2D83ED90676F3DC9F1D7D2E8571696D37C0A70F699CA565A9E564DEC41DF75D981D
	4E0742729D3CE844A78C0DCE6725C1FCFA1D4D067212C6A96FFBE16549970DFC74F0FB2871D7D915A9BE9AFD4648C7A3214C180B68230F8858DE893C3D06EE0ECE1F7B0378747A4CB754E75D7E90FD2ECD16D8FC447419DE2157072C4505CB9033DEC2FD6653BEC77676CAAF21BEB537C6274F49A0BE3DBE63AF9971E50CD25E5B374345F171BAF96CDE58FAFA76138CF9694496943717764305104E3D0C3815F4BEAE01F48D471968F9018D52C31751DE3FBEC66CBC9E5271G49GF381B2810A3A10663FA9CD95
	24BB3AD8BB10B381F2B6C97611984D6031AEDD7D8910F248B3D9A9DF1FBFDE11C7E48D577783ED7708212CF7B5B4C6F739C8ECC256EB6F0DC08E81963F4D5D987A5E486B9B0D6F0D7CE623713D11B7B7AEE7F8A8C3FB3B115FDD5CB87C3D121C625077CACEEC8ADDCF73A663FA1E5FE4DC4F2ECD4357533EA9D4BD57D3BB7FA41B2C0D3D7DAE3F6494E9512C6F0B0364EE92C43C277BDDAC9CA6257B746BF981E36C0192321FB458053174DB1BAA0460D83A41A6795DDEAC44ACDA3C27FAFAF8A0CDAE10E03D7F78
	D1E89B7D75A3E39B65ECB636D17E6661ED34FCF3A89B313E78D35A97A700EE71DD58BFBBDC58E7D3F95FDB6CA1F7B596C2FA19A5AD5F84E036E05E8A1AB78469D80069G69BC6FD13776BEA4720D446F75C064AE6CB29A439282E460BD405148D8AC845EFBDB4AFD69F5BF393B96F4B76AA42C1884BCDBF859162F50347A5D2D3BF9F93D8D248C2339466AF54BB2B9D197B01679E3AF2EDFB2ADF816DAC14FE7CC5D301E8330F523EDB78F3BDE986A8E345578EE745F13DBC27B5F19AD467EF7E18B5D93F777FB2D
	225E473A379471BCF541C80E0408F9980D03727BAD5EDEB148C73D141F6505623F31050C2B5D12CD72AAB719D83B7C005E2773C01790FDF453235E6237A86D7110086D5160F757D5F0F95F5D9ADA7F9FEEB556FF6656507A4F5E9ADE7F39DBC36B7F7A56707AAF5C9A2A9FB3DDBBFA343A6E667842DEAD3E1263FF9400B7F17C3D76F75107FB3CEEE7EB05D2473AED21ED7669B6E31B35EE8BED33335B425B2CEBDBE81B75EE8BEFB34FB63D4FB09D4FCA5AFEDA4947116C20752996623A0347ADD65E47FDDADB6E
	6578DF75855A32D241FF432115DF4169A3E5EDB97BB83E4699D80E895B761A16FE8F9FC37E1D2F81133AF18CC925E348BAAB0C5DGADD69CC3DE8A3A2FCE40867903G51D66331FA3281BE91F0A9BA3CF2C6FEAD307EF83FF14AF5ED7D2BF8BBBCE838C7142E143D901E39B6A3B9BDB782656CD5707B0634F276F37A5F8D3D236DB365E83F99E7F08F3A4806761BFA1B582F9552F600889B5AAFC6B02AEB0CD23FE970CCB62C6BD7EF86482EC0590BAAD076ACAEFBD1A911DDA02665EF81B8A8226C5F5036B30B21
	67D3BB648D697247EB52F14AFB92BCD3C523FA8E5C8A2867EE446F3A9D002FC43C69B25DFB700ABD2257AB5A5A246BE4EEAB6A21F759681AE1AE48D9A062D95D89095C7D10AD575C7DFEC1C821F4AFCB38666A04349FA096A021072DA5591C7A15815256BDDE0B9306C19ADC4575925ECF9C483B8C52DBG6A81ECG113DA87338CCF801D1F276BBE98DD97D56B8C97D96GDD8627ED24BE52D1BBA879DAAC5D64528443DF2578F3C1786DD9E41CE63764B4073E5C1E1D9F13D8C9B37353A2D8DEBEBDF72632782E
	A672ADB9C24ABD63933D7A7B406C6CF6D713766CF60B1D592A490EE73B3259A83F530EB1410D92A21B4FAF81F14DC2B9380D16B8514E9EBBEB23A8F8465A5977B62CAD5CAE24C9A4F80079GF9922C1BA534EFFDB1518BF5A2E295F3AA325F78E815AE5F8C93CD23A44AB6CE13E3551651B0687186167112F32DAEDF74110F235412180D9ABDFA9B91DD1AA43461C11BB63FD3E27911BCFF3A5508BF06673FAE9A71CF1330BF7FA5684CC18B49D228B343E76858BECBE2E703B99C1FC07173B4F8F6E6D8456F92AD
	023C0EBE0C7B2B7BB06E1F4EF197687AFDAE24B3B9EE0D0CF44B9CD877DF864DA3DD1067F730BAC7B90C4728D883FC8260E66870CC1F9AFAEFE80E03693318630FD37C828D1E6919466FB589B20C81B2567FBF281EA9101E43F10DD44FF9104EF5A2EEBF55338852659C77B53A47D18F69AE0EFB076FF178B96E930A0B01F4824745C9A8F78647AD66B61F49F1DB7B10F711936D7B03B3FA5F48F329B6DA678A659B2B85E203F297334D920E5FCC71CD9ABC335933F41EA369AE481B6142BA3DC8776CCA5CE979D5
	EE4495E6D1DBC03A0363AEE521BE321B6F5BD4686BBE5D2D39FF618ED5F77BCE6A3FEE7DD9F8BF458B9E7DD9781FCB71ACFC9D64C5F830CE6F333605F4A6471D2738E5D7E0AC3A425B07628EC23A1563FEC3F1B6C8C7F05C201D2E19A19D4FF15F31733E43F113E8FB97F853720B81CCDE725D7AA5F225CE9917F6C84570F40983457C52CE3D64A413065BD532FBCFF9E9715E72726D6C2E4364816DDD072AF76EB67C860640201759B3DAF9E62EFADC79567C5792FEEBBE8372B23DB8176E6EA2E708FCFDCAAAD2
	DDFD4CAB590EDAFADB68284FE2138A6A17045F644358240BFDC78E69C800D89F5EAD9E7C986F96CFFBE60A9063B23E6FBD57FCB3BB5127FCB762D36FE64C6DFBAD1B49ECF5204CC2BF442DFE023B95F0471158726B39EC1F1465A784641339617B02B67F978179243E3F4F5266974A5AFCE223BFA87CCC37D73622EE192FA488723FC4067C6EFF02DF092F146778F90FE54DFF71FA8E7305DB3BD5DFB8F5ECB8DF2858D3DADC893E205C4D6F89F1B71FCEFB4CEEBDCD98C7AE7463BE75D4B362CA7A71BE76AEA77A
	C49760E27A11F64009F61F590FED7DB4254D3B8A7E8CD0F595E9A73A103675AA3651F7DD7AB6FABE208D7E60523711142D4D5F665637512E9CED7EB9373E0DB6AB792CCEB7BD78EDC254D574732DA581FE2E5A0D87820C57E7C35EE42E631BD634DD8647ED68C13A72816C73AB78B8E09F4071EF9A3D231B4453DF23E9726D88CB9F16BA147CD0F37F5F83CC46D9AAC33F37750F82966FFD464FA7E702AE73GF287A18687289F44F84F2B39538B2ECDAEA08838BEF84001B636817D22C102AB176B15B2278D865B
	87639A04E420198530CAA9E77EA01BA368784166DBB646DF03B17E9A36D9A41D677D100E66381F7039FFB247FDAC635C1F48F117787C1D42F185B24E5FC557310D9BA9DDA924DBB86EB30AA35FB0116F0E98EE8EEDA7C2730FEC1FD225BCA66377D4C6286F4CC78BB91A67CAA1230F5E41040E45463A2748B60712F7110E3FD6375363F609AE7FA92F847F538FF6189F8C50B612214E035751EFFE4356EC16C15D1A2D1B5ABA816826F35AC3F268B5684F691CBC9FE89781945FA038CD0E22B0FA2D94B607D8B336
	513A5A219C7F8D1C63DFA5DBCA82ABFBDD5FF0E533B2CED2BF188C7453EE208F4D25FB90F3A03D00637E3503475DC368AF6BE99C578569A100B80E5FCE71643BB2F207B71563DF3C0DF1BA21677DD7F96F9A7A7B7C0E49C861B2966B7234FE72777859CA61FBC220FF6D5F237A578E7AFB906C6E35CD317151AC1C23968C6158521F05E30BF053F8EC4158185E73879A82B836145C540FAD4BAA098C93601BEEE21B0DB371F878A63601EBA75983188E6919G73GB2F85E8D1AD7FCABAD3F86206B967B66CF39E3
	3053E82FCE8F242C699AFD66996806B0D31347E42C99110CFCDDBBAE9906D66B834AFE2903FE8BEE11DCFE9A317814B6BA4CE33D08DB58172FB85EC1DA33E4F578304F5C476FF9CD60345FF4E86FBCB33AB4CAC7BAE49C274B9DE6FCD8CF4713C420CD6674636CC4BFE56CD1681E2173BC61DBF08B7DE90E9D7DA973D6F8FFDAF69B66465B2ABFD55DD67D09491DAD857B71CB926ADDFF9BE36D7FF5C13BB6D876381C0D65686BE03E2D74735B7AFB50BB686FF2CDGFC42ED5C931A3A0B7C96C33B25AF78049310
	2DDE37C7747A2FB1AC7DBADB712105E16F78990F9BFF9BA55F3E307766027A88DDC74E22EB17199A3BB37CEB941F2E4133D839278FE36579105792017713BB7711B1BE815229G73GB281565D85B172DDE84B853B4A05B077AF4DEECF0BF8D514B54738CC3FB3E16F699975CFC1784E92DC27A4FE63B9A1695E7A105FF7365C457CC1D61EA53FFCAC0BBC19FE3F2BA27A45C2DE92C0BA40A2003C3BE17E3C1B5F3DAED76E4993F728757BADD6BB7D5A169F2ACB62000AA69B364ADD31A6A35DCCF733FAB45D9D5C
	0F98DF3C59082FD3217704645B9376CE20B1DF5DA879066E180F79609A977135483B753E7991450F3FDB6F1B4F73F5DCAC6489E3F04EF85CB53AFA5F6DB162330DE1F69D04E7B44064B17A79E310FFBF10B8A6388DD2G178E30B008AF43EAA4AFB7125195C506EA4F4D7CDEF82981CD8D604CGF6CD9E9B2B377331FA4800CFBBCE93988FB4F111EC2CA66924C87DD87D05A1523768E6FE1D5DADD9C5E59DBA4077CCE6C4625A7869BE7D5A787702F6EDBB3BCF3FB64E8A484F8F5A3F288D583FB860502F0D11DF
	B9F7F3D443D37B4DD640A74AF4D0AABBF126455C5C5A52F03979243961F4FBED4B6536061606BAF3732913384636706F3EB3C1370D34EC016E9332FF8B0F846320EF53F8AD0AD4C199277E23G4729F54FCF956AE757E7EF960C47A953BD2C2DA26FD1672FD4E7F18E53EB1DA3781C221247765301E796C0A64032FB591DB1467704CCE28C0BAC753A4860F48EE3012F32FDEE20BD8840F00B693E088C69C10EEB27F4F110CE38174716FD68DF69779A7B9746D0849682CD6ED8553F0A462A7E45E49C20B236G3E
	FCAC56FB56BB44B7EA55EFBA79FDE1A54E7A1478C1FD4077100C0435C1B4B1BB464C728E45A7685ED31D41622C2C252151FC59FC6A726966E3CDE6259D7F7510DA467392E96F10FBD83BCB0BAB76ECD778669FD6795E6A9B899F7315140B5A3D37629D278C775EB2AEDE4F6E9C4B6C9EB1D67515A47095A63F35C615EF12B60416DFD18162AB36235F7AEAD53EA77AC64227FC17FEA658B71736737331316C8CAD951EF347323B0D4C0F52BB500FB2469A7B513BCAFDC8BFAC0802B9B9CA75A3D314FE1C7A6E4764
	9E07760B4AEE77A01E530BE43E571CDA72AFC2702CD1390F307AC3F8325DC6A3EABC13DCA0694F4308DEB5D17CBBCD7AC32FBD9659275EFFFAA68B4794F3145118026DB069285A8E5BAA03EDFB3892ED9B9D45EC9B874F24A8369EE636FD4604361D99E5ECDBD28F1C876681CD064636857769ED7B65C3241EA7A497358C438DB21CE5D0031BF21E606A7A3D9ADCC14E07A1EC0F971219CDDF5C2937A9512768BEBCE3FCC5B64AAF6579BB9CC679B577E11B1CF1E85B0461A6536F727D5EFED13957624806E7459E
	EC0B2F5729ED61348F5317AB4A4BCB0BF76F5D2E50FE4BCE4A59436FDD64CB3C6D6F3B53792438C12D43CFA42CAF0BDFFFC473D81CFAF38F79CDBA43B58DC4291FC21DCFF57741BCE83674F21E723D44D67CEC1CADF5143CFDFB09BD79374AF420EC83C2D7AF0BCFFE56A788982B7CD273AE9B5E3B79C78113F14F67AA0369AF6E8BFDAFCC5EE7FCAF4C3B4F78AE6B603E616F9DC76C8BFD6FF86CBE6D6F5931F67FD9A3E91BE87025F800D40039G0B6E43B54B2AAA721BBE6C2BD07DFA254156CB6E2511DFF775
	237C0C2A503A3ED5957EB7E1737613B8235B8DB33013140F78257BC9FB795D9E0AE438157B090D3CD22F5D4FC87177DD775378C36C21E83647F4B7BD6BD9F6BF0C7D77633E536BCD386FD44AF1AF51731FA1C8C7F35C16B3C897F3BF2E99B63AD4BFAD513CFF4579797B6C8FBFD799CC6747685950FC50B7035B61EF9E34C36E8308EB97894E8CE919631E243888C847F05CB8FADEB48D52A99C7730086525F35C17EC48DB9A0D38F2AE2306635E37E17D4C9C6731E1F9DD9CD75D03F293B86EEDCEB70363D2F91D
	8B9FC45CBE8B4AADF9904F334E5BF17CBD7860C8475F05C758786BECD27DE4680E77E3985F02ABDABEBF54A16A41617723D9BF3F50CC6671D820CD82C887D884107785688FGA600A600CEGBFC0B440E400C4009400B9G8BG04F1B8AE7C281A7CDEE928FDAF65E01C8E8EDE32AFA9609A7C0283576005D0D64BB8BCF72C2C887856081D2FB74A6E0196A5E4D1663F29FC7FB3F29C1E393ECA4B1C8E69D9G8B4699354B5CA3EC5E7B21CC56CD41B1FCD9DD83D943DD2EEDE9BEF672843CABF3663E630240FF27
	D29EB68607F9FBDFB9466E67DBD43E36E1E3775DFBCA0B77D6ED47580EFF5B1FBB5EB8368BBD3F5FBC213ECFBE294AFFF158B5874AE7FEF27D94BDB787599DGC3G93G62811247E35B179EA999266DD90329CD4F5ABD76AC367BACA82760A16CCF734EE2FB56BF046D392203508EC1FA8240B4CE7B5EB9344D0C076EB476D9791940FDE8F39FFEDB1779900E674FE7E97B1569E1688F8F8F5F2F147D4ECA542B9368A7BC0CF5FD219275CAFD9875CA2734390F403A9A407488524EB621DEAD0F9836F9CD68B165
	C01B2A57FF6EA479E42D93494B4EF6A2EEBA47F5BB706E7322C7D07F25817B4A9D93E0BC1FB03C7E4C97FE5198FA3E7FD5E378797E0DC6FAC6G7334791AC739137E27C632D6F1777478C49ED7E4B6AE6338E6DF2B1BAC815CAE665F0FB5851EF3DDA2BF2B2D6CE17D2D8957AF91867AE857A6741C84E8882CD2707193746B171F1883E5FD790CAA6B4099141598C25662E1E4258449CA70844A1A592E4ADAF585E54D9A01DE33B4E77EF4EFBEC816D3B4BE3B3D074E0B0B00DE18086B1FC451781C194D2125C0D7
	45E91F3105BEC7FA094E43B62075CC44756633A26A95BB5178DB822DDE8993836FB24C18285F93CA21B2E682BEFDA26EC51D23754C1B84737CA4BC0FFE732CEA5B31B62CC34D247077A97436B5CF525741CAE310AE404B13D04F75DCC60C010C9C9DCC1B9428E7F2100CBFD01DD281BFE7926A59C563175CC7617DD16C671783FEC363A060DB9F9D3E1F33D8A847CB5B6AD1A6176172A8EEC84131FE6AEE8EBD9EDCED8EBF9E5CEEB60E7F6FE98E0E7F6FEFB60E7F47B7EB63FF061B58CC6C568BF1C98727FB1C63
	4EF15C17E81984F7D632796D9C7FB42D9741B709C4A0621F27759A20388E851720411DD3F07F52CC46B4D0596E76A26FB71AC97F6E11E419253FCDE9A4978CF15C9E5F25F514BC96CD3DD3B80E541B352B650E4F3F981FFB14FC71F74C474EE69A68EF8946BC2ABF33B9C971F10F6A4FECE673F8B181728AE378FC9D90BF8F82BEBAE6A4735518D3217D737ED3617D7311D3467E795829E07F7C62A9E37F0CBB2575CF264F143323EB0704D1727D6A0E79D8BB5DCBE33349B17A769BC77153E2746DD754016D1784
	F96B9E635F431E534A0FG7C044706EFBFE52DED38EE0AD7F82743F3262E9CB6D63D1C95BCFEFDBFCBBBFEA9FBCE1523336775D172FD750E79183DDBCD745E77E37AF6B8C7718B9F533743AFF81C1A89F9DD0FE3BB241B3472E7GFE4E63A3E9077F5AE9D46F8540ABBC817D9420846060937AB34CA506E73D2D8A1D8D1E03G51G131F505E476BF5986DF9A482CD0AC2177E447075C6D888F43931B0C7462A744AB785D2300FFCA2E9FD04454F3B1AB1DEAF0D9DEE7F7DA3B2BCABBCB27FDE4E960BB1C743F9B5
	3F718A664772FC7B85352C87BA49BB0B299E9607B9D3B141F2EE37091F453CF9D1655BE89BB95F8717D53E31E2387D6A4AF2DC3F7ECCC4BDE6995A8657AD7B3BD45DBAAD2A2C2DA3124578EEF72BFCDF320E3C0EC9B62CE34113E86BA9D635BE0BC5355C3F5CC139A7F93975CF8E27FBC90FAA6BF82FAA2B54B2F25DDDF6156F396E1170B1FF3EEA8F76719BF62D0F33B50A65E3B2DEA8FB76B494D2FE93E670BD950F211052C70F1363AF655D2CF9FF4D306F7E230049786367AAC319C37A8C66103E60B9A43AC3
	55FD2266FDC34767EF07FD1F2B8C267376F341F6A8BB273583439DE136013E907DA47FFE085F850F6138967AFB8E73A1ADFC916725F613EA374B1A7779151F3F8D33CC1F3F8D5FB4855BB04394EC436CB202AB843BEC79A25A2B17624AA15D44F1F72DC03BF6F2DCB27F36EA9A4765D37BA7C3FA8E47EDE05F74C2BA136346B8313C3CAFA12E248F4B6B60B81962E4C8C7F35CCBDC46E40EE3F7D193A1BD0363FECBF1B3A1BD1F639E1511B71363FE2FFECBB999F1CBF8DD3AB86EB84579A1BDE1B27A1129D99BEF
	4483FE7A641144B7D7823EA51C89BC73C644F7BCE0FFBE83F88A1E9A895F4F83FE8F3598F8CCA3627BD640EF51B681CF6708785A8278BC409399060F4DB56D3AEF969E12A0296C1BFC135F6709B1A88B77CD702E40B420A14077B41E526FC9D9FB695F9FF1FB45DE2F3B5FE5433D0E9BF43CC8857AB9CF715F7BAE8B2CD7A2BDC7E6F9374A681A4DE2F58416B30976238CA8A3F78A6E4B442CC09DCA26045F7BB988B484F06F27F50AFEDF66377CAEF267942CEBF11F31DED5FD28174CE977B883751A6DD06D7D38
	936B9AEDD05745A16A3AD6491B9CD4D7762D8DED9BAEFFCC9F09E16CEE2FF45D6D72DB64DA3C812670BCC9F948BAF77A945CF71A4F75184F4BD16EC7F0BD7EAC2B6D33D8B6EE9FF64FBCEFAA746F29D8F6320CE5D7CD45B64B63F668189AFEAFCD861A08292A5FCD182A37C3B937436429D87F6EB0F69862F7989326727128B9F80CEFED8E9E63234E8453BDF8A6182E7BCAB0DD6F15E03ABF0A41F4FF910369E6590269FEEA8B266BB320F387503178F781BF3B1ABBF538F551FA177A9EF5C5750338DEF297C1F3
	B6A73B3DE55DB27885237D2DD7259DDCD194CCAB13F0741857F20D556787AB30BEC5F14355E739DF630BFD0177F83EA2072A9313710A93E5344661DA42542F16E757151758D7AC845EABEDA37F63B58ADF993DB848E817BB0637478BD7D5B9DF10073307F2A7A90CAE7B39AED106769A6EFCADFDC02D4BB11D4E0F74ED9ADEE7F676AA70EF6F8276F81221AE3362C6B2277D4F95DA3E794013B9A23E1981FB924B1E06D86B6911709D8E602B821E16B0FC4A3E39A338BF3D67886EF77FE7C5B05D2FD784533D6E8C
	26DB608C26BBE3C0F7BE000E7D2D792F1D7DA2B460345F880BFDDEE1C5701A32768BBF1DBD6DF2FDE136105DAC3CED270B40E6E171GD9A58EFA84A11B044F13FCD78472976B85FA30FA53AF94545AEC45F84556AF3C4F6E5CEAB1AD6E5EE24D6DBE907EBE975ECCF74567DE2D2C4E7965541CECA197A433DD58E6A117EC25C2853C2CA2B9FE3707D403FE7D4681CADBBF6C555D6F7D7A1F9A1E0A3526E68BD94D423BEEB5031675B62AC34F492F05C8117D638F8CD2EF875586E94829E68A27897897063391E010
	27488C688B4391C8B382D9FEE1C9C7B1392EDF8DEF67706DBDEC32E272D3B3FEE1A5CF2BBF9BB3CCCD6E84ABA87DE6E7085406G6B99732BB4F74EF1C7257A052CE200F2005DFEE1B17BDB1C4625F805250143F056532B47AC9BD7591FAD64B58AEF77D8E40F5D829ECD3F53EB915E6D262EADFA0156AFACEBA13C3B00F79765BD50A2E4DBFDFEE12D0A3792BC4BED73D82CA210645BACDE87AD01D4297C02105F636D6D26827CC27605468BA01626C8F6C50B700E5FCEAE7711084DAF2CD60BEEA5F7BA140C8334
	EB88EF81F868BF2AAE7D750E37121313B538508E970C9D91BF7F7BA637355F75E55CE38D791B9FC94F3131B975B97DF7FD9FDC20F16673B06EBF0FF7DCFA4E6F94C25F01EDF3DAE49922C0C72D4BC66E439E3D669755BB18EBAF1072FDC823E4323C0FAE10FA77E9F87EAFD0CB87889FF9C1B129A9GGBC04GGD0CB818294G94G88G88GEBFBB0B69FF9C1B129A9GGBC04GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG63A9G
	GGG
**end of data**/
}
}