/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import org.sbml.jsbml.UnitDefinition;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.gui.CollapsiblePanel;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.SingleXPlot2D;
import cbit.vcell.client.data.ODEDataInterface;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo.BioModelCategoryType;
import cbit.vcell.mapping.AbstractMathMapping;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.DataSymbolMetadata;
import cbit.vcell.solver.SimulationModelInfo.DataSymbolMetadataResolver;
import cbit.vcell.solver.SimulationModelInfo.ModelCategoryType;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.util.ColumnDescription;
import ncsa.hdf.object.HObject;

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
	private CollapsiblePanel filterPanel = null;
	private JPanel speciesOptionsPanel = null;
	JCheckBox concentrationCheckBox = null;
	JCheckBox countCheckBox = null;
	private JLabel ivjCurLabel = null;
	private JSlider ivjSensitivityParameterSlider = null;
	private JScrollPane ivjJScrollPaneYAxis = null;
	private JLabel ivjJLabelSensitivityParameter = null;
	private JPanel ivjJPanelSensitivity = null;
	private DefaultListModel ivjDefaultListModelY = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Plot2D fieldPlot2D = null;
	private DefaultComboBoxModel ivjComboBoxModelX = null;
	private JComboBox ivjXAxisComboBox = null;
//	private boolean ivjConnPtoP2Aligning = false;
	private ODEDataInterface oDEDataInterface = null;
	
	private static ImageIcon function_icon = null;
	
	public static final String ODE_DATA_CHANGED = "ODE_DATA_CHANGED";
	public static final String ODE_FILTER_CHANGED = "ODE_FILTER_CHANGED";
	public static final String ODESOLVERRESULTSET_CHANGED = "ODESOLVERRESULTSET_CHANGED";
	
	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getLogSensCheckbox()){
				firePropertyChange(ODE_DATA_CHANGED, false, true);
			}
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getXAxisComboBox_frm() && e.getStateChange() == ItemEvent.SELECTED){
				firePropertyChange(ODE_DATA_CHANGED, false, true);
			}else if(filterSettings != null && filterSettings.containsKey(e.getSource())){
				processFilterSelection();
			} else if(e.getSource() == concentrationCheckBox || e.getSource() == countCheckBox) {
				try {
					updateChoices(getMyDataInterface());
				}catch(Exception exc){
					exc.printStackTrace();
				}
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
//			System.out.println("----- '"+evt.getPropertyName()+"' :: "+evt.getSource().getClass().getName());
			
			try{
				if(evt.getPropertyName().equals("columnDescriptions")){
					firePropertyChange(ODE_FILTER_CHANGED, false, true);
				}else if(evt.getPropertyName().equals(ODE_DATA_CHANGED)){
					regeneratePlot2D();
				}else if(evt.getPropertyName().equals(ODE_FILTER_CHANGED)){
					updateChoices(getMyDataInterface());
				}else if(evt.getPropertyName().equals(ODESOLVERRESULTSET_CHANGED)){
					ODEDataInterface oldMyDataInterface = (ODEDataInterface)evt.getOldValue();
					ODEDataInterface newMyDataInterface = (ODEDataInterface)evt.getNewValue();
					boolean bUnfilteredColumnsChanged = false;
					if(!((oldMyDataInterface == null && newMyDataInterface == null) || (oldMyDataInterface == newMyDataInterface))){
						if(((oldMyDataInterface == null && newMyDataInterface != null) || (oldMyDataInterface != null && newMyDataInterface == null))
						|| (oldMyDataInterface != null && newMyDataInterface != null && oldMyDataInterface.getAllColumnDescriptions().length != newMyDataInterface.getAllColumnDescriptions().length)){
							bUnfilteredColumnsChanged = true;
						}else{
							ColumnDescription[] oldcolColumnDescriptions = oldMyDataInterface.getAllColumnDescriptions();
							ColumnDescription[] newcolColumnDescriptions = newMyDataInterface.getAllColumnDescriptions();
							for (int i = 0; i < newcolColumnDescriptions.length; i++) {
								boolean bFound = false;
								for (int j = 0; j < oldcolColumnDescriptions.length; j++) {
									if(oldcolColumnDescriptions[j].getName().equals(newcolColumnDescriptions[i].getName())){
										bFound = true;
										break;
									}
								}
								if(!bFound){
									bUnfilteredColumnsChanged = true;
									break;
								}
							}
						}
					}
					if(bUnfilteredColumnsChanged || filterSettings == null){
						showFilterSettings();
						processFilterSelection();
					}else{
						processFilterSelection();
						firePropertyChange(ODE_DATA_CHANGED, false, true);
					}
					enableLogSensitivity();
					initializeLogSensCheckBox();
					if(evt.getOldValue() == null){
						getFilterPanel().expand(true);
					}
				}else if(evt.getSource() == getFilterPanel() && evt.getPropertyName().equals(CollapsiblePanel.SEARCHPPANEL_EXPANDED)){
					if(((Boolean)evt.getNewValue())){
						showFilterSettings();
					}else{
						if(filterSettings != null){
							boolean bNeedToShow = false;
							for(JCheckBox jcheckBox:filterSettings.keySet()){
								if(!jcheckBox.isSelected()){
									bNeedToShow = true;
									jcheckBox.removeItemListener(ivjEventHandler);
									jcheckBox.setSelected(true);
									jcheckBox.addItemListener(ivjEventHandler);
								}
							}
							if(bNeedToShow){
								processFilterSelection();
							}
						}
					}
				}
			}catch(Exception exc){
				exc.printStackTrace();
			}
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getSensitivityParameterSlider()){
				firePropertyChange(ODE_DATA_CHANGED, false, true);
			}
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == ODESolverPlotSpecificationPanel.this.getYAxisChoice() && !e.getValueIsAdjusting()){
				firePropertyChange(ODE_DATA_CHANGED, false, true);
			}
		};
	};
	private SymbolTable fieldSymbolTable = null;
	private JPanel panel;
//	private JButton btnYFilter;

/**
 * ODESolverPlotSpecificationPanel constructor comment.
 */
public ODESolverPlotSpecificationPanel() {
	super();
	initialize();
}

/**
 * connEtoC11:  (odeSolverResultSet1.this --> ODESolverPlotSpecificationPanel.initializeLogSensCheckBox()V)
 * @param value cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(ODEDataInterface value) {
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
 * connPtoP3SetTarget:  (ComboBoxModelX.this <--> XAxisComboBox.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getXAxisComboBox_frm().setModel(getComboBoxModelX_frm());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Method to enable the log sensitivity checkbox and slider depending on whether sensitivity analysis is enabled.
 */
private void enableLogSensitivity() throws ExpressionException {

	if (getSensitivityParameter() == null) {
		getLogSensCheckbox().setVisible(false);
		getSensitivityParameterPanel().setVisible(false);
		getJLabelSensitivityParameter().setVisible(false);
	}
}


/**
 * Return the ComboBoxModelX property value.
 * @return javax.swing.DefaultComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultComboBoxModel getComboBoxModelX_frm() {
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
			ivjJPanelSensitivity.add(getLogSensCheckbox(), constraintsLogSensCheckbox);

			java.awt.GridBagConstraints constraintsSensitivityParameterPanel = new java.awt.GridBagConstraints();
			constraintsSensitivityParameterPanel.gridx = 0; constraintsSensitivityParameterPanel.gridy = 2;
			constraintsSensitivityParameterPanel.gridwidth = 3;
			constraintsSensitivityParameterPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSensitivityParameterPanel.weightx = 2.0;
			constraintsSensitivityParameterPanel.weighty = 1.0;
			constraintsSensitivityParameterPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanelSensitivity.add(getSensitivityParameterPanel(), constraintsSensitivityParameterPanel);

			java.awt.GridBagConstraints constraintsJLabelSensitivityParameter = new java.awt.GridBagConstraints();
			constraintsJLabelSensitivityParameter.gridx = 0; constraintsJLabelSensitivityParameter.gridy = 1;
			constraintsJLabelSensitivityParameter.gridwidth = 3;
			constraintsJLabelSensitivityParameter.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelSensitivityParameter.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanelSensitivity.add(getJLabelSensitivityParameter(), constraintsJLabelSensitivityParameter);
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
public ODEDataInterface getMyDataInterface() {
	return oDEDataInterface;
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
private Constant getSensitivityParameter() throws ExpressionException {
	String sensParamName = "";
	ColumnDescription fcds[] = getMyDataInterface().getAllColumnDescriptions();

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
	try{
		sensParamValue = getMyDataInterface().extractColumn(sensParamName)[1];
	}catch(ObjectNotFoundException e){
		// System.out.println("REUSULT SET DOES NOT HAVE SENSITIVITY ANALYSIS");
		return null;
	}

	Constant sensParam = new Constant(sensParamName, new Expression(sensParamValue));
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
			ivjSensitivityParameterPanel.add(getMinLabel(), constraintsMinLabel);

			java.awt.GridBagConstraints constraintsMaxLabel = new java.awt.GridBagConstraints();
			constraintsMaxLabel.gridx = 2; constraintsMaxLabel.gridy = 1;
			constraintsMaxLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMaxLabel.anchor = java.awt.GridBagConstraints.EAST;
			ivjSensitivityParameterPanel.add(getMaxLabel(), constraintsMaxLabel);

			java.awt.GridBagConstraints constraintsCurLabel = new java.awt.GridBagConstraints();
			constraintsCurLabel.gridx = 1; constraintsCurLabel.gridy = 1;
			constraintsCurLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCurLabel.weightx = 1.0;
			ivjSensitivityParameterPanel.add(getCurLabel(), constraintsCurLabel);

			java.awt.GridBagConstraints constraintsSensitivityParameterSlider = new java.awt.GridBagConstraints();
			constraintsSensitivityParameterSlider.gridx = 0; constraintsSensitivityParameterSlider.gridy = 0;
			constraintsSensitivityParameterSlider.gridwidth = 3;
			constraintsSensitivityParameterSlider.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSensitivityParameterSlider.weightx = 1.0;
			ivjSensitivityParameterPanel.add(getSensitivityParameterSlider(), constraintsSensitivityParameterSlider);
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
private double[] getSensValues(ColumnDescription colDesc) throws ExpressionException,ObjectNotFoundException {
	if (getSensitivityParameter() != null) {
		double sens[] = null;
		int sensIndex = -1;
		
		for (int j = 0; j < ((DefaultListModel)getYAxisChoice().getModel()).size(); j++) {
			if (((DefaultListModel)getYAxisChoice().getModel()).elementAt(j).equals("sens_"+colDesc.getName()+"_wrt_"+getSensitivityParameter().getName())) {
				sensIndex = j;
			}
		}
		if (sensIndex > -1) {
			sens = getMyDataInterface().extractColumn((String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(sensIndex));
		}

		return sens;
	} else {
		return null;
	}
}

/**
 * Gets the symbolTable property (cbit.vcell.parser.SymbolTable) value.
 * @return The symbolTable property value.
 * @see #setSymbolTable
 */
public SymbolTable getSymbolTable() {
	return fieldSymbolTable;
}


/**
 * Return the XAxisComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getXAxisComboBox_frm() {
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
private CollapsiblePanel getFilterPanel() {
	if (filterPanel == null) {
		try {
			filterPanel = new CollapsiblePanel("Display Options:",false);
			filterPanel.setName("filterPanel");
//			ivjYAxisLabel.setText("Y Axis:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return filterPanel;
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
@SuppressWarnings({ "serial", "unchecked" })
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getFilterPanel().addPropertyChangeListener(ivjEventHandler);
	getYAxisChoice().addListSelectionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getXAxisComboBox_frm().addItemListener(ivjEventHandler);
	getLogSensCheckbox().addActionListener(ivjEventHandler);
	getSensitivityParameterSlider().addChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP3SetTarget();
	
	getYAxisChoice().setCellRenderer(new DefaultListCellRenderer() {
		
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
			ODEDataInterface mdi = getMyDataInterface();
			if (mdi == null) {
				return this;
			}
			
			String varName = (String)value;
			ColumnDescription cd = null;
			try {
				cd = mdi.getColumnDescription(varName);
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
			}
			if (cd instanceof FunctionColumnDescription && ((FunctionColumnDescription)cd).getIsUserDefined()) {
				if (function_icon == null) {
					function_icon = new ImageIcon(getClass().getResource("/icons/function_icon.png"));
				}
				setIcon(function_icon);
			}
			
			if(mdi.getDataSymbolMetadataResolver() != null && mdi.getDataSymbolMetadataResolver().getDataSymbolMetadata(varName) != null) {
				DataSymbolMetadata dsm = mdi.getDataSymbolMetadataResolver().getDataSymbolMetadata(varName);
				String tooltipString = dsm.tooltipString;
				if(tooltipString == null) {
					tooltipString = varName;
				}
				setToolTipText(tooltipString);
			}
			return this;
		}
	});
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
		setPreferredSize(new Dimension(213, 600));
		setLayout(new java.awt.GridBagLayout());
		setSize(248, 604);
		setMinimumSize(new java.awt.Dimension(125, 300));

		java.awt.GridBagConstraints constraintsXAxisLabel = new java.awt.GridBagConstraints();
		constraintsXAxisLabel.anchor = GridBagConstraints.WEST;
		constraintsXAxisLabel.gridx = 0; constraintsXAxisLabel.gridy = 0;
		constraintsXAxisLabel.insets = new Insets(4, 4, 0, 4);
		add(getXAxisLabel(), constraintsXAxisLabel);
		
		java.awt.GridBagConstraints constraintsXAxisComboBox = new java.awt.GridBagConstraints();
		constraintsXAxisComboBox.gridx = 0; constraintsXAxisComboBox.gridy = 1;
		constraintsXAxisComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsXAxisComboBox.weightx = 1.0;
		constraintsXAxisComboBox.insets = new Insets(4, 4, 5, 4);
		add(getXAxisComboBox_frm(), constraintsXAxisComboBox);

		GridBagConstraints gbc_YAxisLabel = new GridBagConstraints();
		gbc_YAxisLabel.anchor = GridBagConstraints.WEST;
		gbc_YAxisLabel.insets = new Insets(4, 4, 0, 4);
		gbc_YAxisLabel.gridx = 0;
		gbc_YAxisLabel.gridy = 2;
		add(getYAxisLabel(), gbc_YAxisLabel);
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(4, 4, 5, 4);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		add(getPanel(), gbc_panel);

		java.awt.GridBagConstraints constraintsJScrollPaneYAxis = new java.awt.GridBagConstraints();
		constraintsJScrollPaneYAxis.gridx = 0; constraintsJScrollPaneYAxis.gridy = 4;
		constraintsJScrollPaneYAxis.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneYAxis.weightx = 1.0;
		constraintsJScrollPaneYAxis.weighty = 1.0;
		constraintsJScrollPaneYAxis.insets = new Insets(4, 4, 5, 4);
		add(getJScrollPaneYAxis(), constraintsJScrollPaneYAxis);

		java.awt.GridBagConstraints constraintsJPanelSensitivity = new java.awt.GridBagConstraints();
		constraintsJPanelSensitivity.gridx = 0; constraintsJPanelSensitivity.gridy = 5;
		constraintsJPanelSensitivity.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelSensitivity.weightx = 1.0;
		add(getJPanelSensitivity(), constraintsJPanelSensitivity);



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
private void initializeLogSensCheckBox() throws ExpressionException{
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
		frame.setVisible(true);
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
 * Comment
 */
private void refreshVisiblePlots(Plot2D plot2D) {
	if (plot2D == null || plot2D.getNumberOfPlots() == 0) {
		return;
	} else {
		boolean[] visiblePlots = new boolean[plot2D.getNumberOfPlots()];
		int count = 0;
		for (int i=0;i<getYAxisChoice().getModel().getSize();i++) {
			if(getYAxisChoice().isSelectedIndex(i)){
				visiblePlots[count] = true;
			}
			count++;
		}
		if((getMyDataInterface()!= null) && getMyDataInterface().isMultiTrialData()) {
			plot2D.setVisiblePlots(visiblePlots,true);
		}
		else {
			plot2D.setVisiblePlots(visiblePlots,false);
		}
	}
}


/**
 * Comment
 */
private void regeneratePlot2D() throws ExpressionException,ObjectNotFoundException {
	if (getMyDataInterface() == null) 
	{
		return;
	} 
	if(!getMyDataInterface().isMultiTrialData())
	{
		if(getXAxisComboBox_frm().getSelectedIndex() < 0)
		{
			return;
		}
		else 
		{
//			double[] xData = getOdeSolverResultSet().extractColumn(getPlottableColumnIndices()[getXIndex()]);
			//getUnfilteredSortedXAxisNames
			double[] xData = getMyDataInterface().extractColumn((String)getXAxisComboBox_frm().getSelectedItem());
			double[][] allData = new double[((DefaultListModel)getYAxisChoice().getModel()).size() + 1][xData.length];
			double[][] allDataMin = new double[((DefaultListModel)getYAxisChoice().getModel()).size() + 1][xData.length];
			double[][] allDataMax = new double[((DefaultListModel)getYAxisChoice().getModel()).size() + 1][xData.length];
			double[][] allDataStd = new double[((DefaultListModel)getYAxisChoice().getModel()).size() + 1][xData.length];
			String[] yNames = new String[((DefaultListModel)getYAxisChoice().getModel()).size()];
			allData[0] = xData;
			allDataMin[0] = xData;
			allDataMax[0] = xData;
			allDataStd[0] = xData;
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
			
			// map holding variable names and indexes in the hdf5 file result sets
//			LinkedHashMap<String, Integer> valueToIndexMap = getMyDataInterface().parseHDF5File();
			
			if (!getLogSensCheckbox().getModel().isSelected()) {
				// When log sensitivity check box is not selected.
				for (int i=0;i<allData.length-1;i++) {
					// If sensitivity analysis is enabled, extrapolate values for State vars and non-sensitivity functions
					if (getSensitivityParameter() != null) {
						ColumnDescription cd = getMyDataInterface().getColumnDescription((String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(i));
						double sens[] = getSensValues(cd);
						String columnName = cd.getName();
						yData = getMyDataInterface().extractColumn(columnName);
//						yDataMin = getMyDataInterface().extractColumnMin(columnName, hObjectList);
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
							allData[i+1] = getMyDataInterface().extractColumn((String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(i));
						} 
					} else {
						// No sensitivity analysis case, so do not alter the original values for any variable or function
						DefaultListModel model = (DefaultListModel)getYAxisChoice().getModel();
						String columnName = (String)model.elementAt(i);
						allData[i+1] = getMyDataInterface().extractColumn(columnName);
						String columnNameAsCount = columnName + AbstractMathMapping.MATH_VAR_SUFFIX_SPECIES_COUNT;
//						if(valueToIndexMap.containsKey(columnNameAsCount)) {
//							allDataMin[i+1] = getMyDataInterface().extractColumn(columnName, ODEDataInterface.PlotType.Min);
//							allDataMax[i+1] = getMyDataInterface().extractColumn(columnName, ODEDataInterface.PlotType.Max);
//							allDataStd[i+1] = getMyDataInterface().extractColumn(columnName, ODEDataInterface.PlotType.Std);
//						}
					}
					yNames[i] = (String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(i);
				}
			} else {
				// When log sensitivity checkbox is selected.
	
				// Get sensitivity parameter and its value to compute log sensitivity
				Constant sensParam = getSensitivityParameter();
				double sensParamValue = sensParam.getConstantValue();
				getJLabelSensitivityParameter().setText("Sensitivity wrt Parameter "+sensParam.getName());
	
				//
				// For each column (State vars and functions) in the result set, find the corresponding sensitivity var column
				// in the result set (a value > -1). If the sensitivity var column does not exist (for user defined functions or
				// sensitivity variables themselves), the column number returned is -1, so do not change that data column.
				//
				for (int i=0;i<allData.length-1;i++) {
					// Finding sensitivity var column for each column in result set.
					ColumnDescription cd = getMyDataInterface().getColumnDescription((String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(i));
					String sensVarName = null;
					ColumnDescription[] allColumnDescriptions = getMyDataInterface().getAllColumnDescriptions();
					for (int j = 0; j < allColumnDescriptions.length; j++) {
						String obj = "sens_"+cd.getName()+"_wrt_"+sensParam.getName();
						if (allColumnDescriptions[j].getName().equals(obj)) {
							sensVarName = obj;
							break;
						}
					}
					int sensIndex = -1;
					if(sensVarName != null){
						for (int j = 0; j < ((DefaultListModel)getYAxisChoice().getModel()).getSize(); j++) {
							if (((String)((DefaultListModel)getYAxisChoice().getModel()).get(j)).equals(sensVarName)) {
								sensIndex = j;
								break;
							}
						}
					}
					yData = getMyDataInterface().extractColumn(cd.getName());
					// If sensitivity var exists, compute log sensitivity
					if (sensVarName != null) {
						double[] sens = getMyDataInterface().extractColumn(sensVarName);
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
							if(sensIndex > -1){
								allData[sensIndex+1][k] = logSens;
							}
						}
					// If sensitivity var does not exist, retain  original value of column (var or function).
					} else {
						if (!cd.getName().startsWith("sens_")) {
							allData[i+1] = yData;
						}
					}
					yNames[i] = (String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(i);
				}
			}
				
			String title = "";
			String xLabel = (String)getXAxisComboBox_frm().getSelectedItem();
			String yLabel = "";
	
			if (yNames.length == 1) {
				yLabel = yNames[0];
			}
			// Update Sensitivity parameter label depending on whether Log sensitivity check box is checked or not.
			if (!getLogSensCheckbox().getModel().isSelected()) {
				getJLabelSensitivityParameter().setText("");
			}
	
			SymbolTableEntry[] symbolTableEntries = null;
			if(getSymbolTable() != null && yNames != null && yNames.length > 0){
				symbolTableEntries = new SymbolTableEntry[yNames.length];
				for(int i=0;i<yNames.length;i+= 1){
					SymbolTableEntry ste = getSymbolTable().getEntry(yNames[i]);
					symbolTableEntries[i] = ste;
				}
				
			}
			SingleXPlot2D plot2D = new SingleXPlot2D(symbolTableEntries,getMyDataInterface().getDataSymbolMetadataResolver(),xLabel, yNames, allData, new String[] {title, xLabel, yLabel});
			//
			// TODO: populate the extra plot2D fields (for min, max, stdev
			//
			refreshVisiblePlots(plot2D);
			//here fire "singleXPlot2D" event, ODEDataViewer's event handler listens to it.
			setPlot2D(plot2D);
		}
	}// end of none MultitrialData
	else // multitrial data
	{
		//a column of data get from ODESolverRestultSet, which is actually the results for a specific variable during multiple trials
		double[] rowData = new double[getMyDataInterface().getRowCount()];
		PlotData[] plotData = new PlotData[((DefaultListModel)getYAxisChoice().getModel()).size()];
		
		
		for (int i=0; i<plotData.length; i++)
		{
			ColumnDescription cd = getMyDataInterface().getColumnDescription((String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(i));
			rowData = getMyDataInterface().extractColumn(cd.getName());
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
		
		SymbolTableEntry[] symbolTableEntries = null;
		if(getSymbolTable() != null && ((DefaultListModel)getYAxisChoice().getModel()).size() > 0){
			symbolTableEntries = new SymbolTableEntry[((DefaultListModel)getYAxisChoice().getModel()).size()];
			for(int i=0;i<symbolTableEntries.length;i+= 1){
				symbolTableEntries[i] = getSymbolTable().getEntry((String)((DefaultListModel)getYAxisChoice().getModel()).elementAt(i));
			}
			
		}
		
		String title = "Probability Distribution of Species";
		String xLabel = "Number of Particles";
		String yLabel = "";
		
		String[] yNames = new String[((DefaultListModel)getYAxisChoice().getModel()).size()];
		((DefaultListModel)getYAxisChoice().getModel()).copyInto(yNames);
		Plot2D plot2D = new Plot2D(symbolTableEntries, getMyDataInterface().getDataSymbolMetadataResolver(), yNames, plotData, new String[] {title, xLabel, yLabel});
		refreshVisiblePlots(plot2D);
		setPlot2D(plot2D);
	}
}


/**
 * Sets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @param odeSolverResultSet The new value for the property.
 * @see #getOdeSolverResultSet
 */
public void setMyDataInterface(ODEDataInterface newMyDataInterface) {
	ODEDataInterface oldValue = oDEDataInterface;
	/* Stop listening for events from the current object */
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
	}
	oDEDataInterface = newMyDataInterface;
	if (oDEDataInterface != null) {
		oDEDataInterface.addPropertyChangeListener(ivjEventHandler);
	}
	firePropertyChange(ODESOLVERRESULTSET_CHANGED, oldValue, oDEDataInterface);
}

/**
 * Sets the symbolTable property (cbit.vcell.parser.SymbolTable) value.
 * @param symbolTable The new value for the property.
 * @see #getSymbolTable
 */
public void setSymbolTable(SymbolTable symbolTable) {
	SymbolTable oldValue = fieldSymbolTable;
	fieldSymbolTable = symbolTable;
	firePropertyChange("symbolTable", oldValue, symbolTable);
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 4:56:15 PM)
 * @param cbit.vcell.solver.ode.ODESolverResultSet
 */
private void sortColumnDescriptions(ArrayList<ColumnDescription> columnDescriptions) {
	Collections.sort(columnDescriptions, new Comparator<ColumnDescription>() {
		public int compare(ColumnDescription o1, ColumnDescription o2) {
	        return o1.getName().compareToIgnoreCase(o2.getName());
		}
	});
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 4:56:15 PM)
 * @param cbit.vcell.solver.ode.ODESolverResultSet
 */
private synchronized void updateChoices(ODEDataInterface odedi) throws ExpressionException,ObjectNotFoundException {
	if (odedi == null) {
		return;
	}
	Object xAxisSelection = getXAxisComboBox_frm().getSelectedItem();
	Object[] yAxisSelections = getYAxisChoice().getSelectedValues();
	
	ArrayList<ColumnDescription> variableColumnDescriptions = new ArrayList<ColumnDescription>();
	ArrayList<ColumnDescription> sensitivityColumnDescriptions = new ArrayList<ColumnDescription>();
	ColumnDescription timeColumnDescription = null;
	//find TIME columnDescription
	ColumnDescription[] columnDescriptions = odedi.getAllColumnDescriptions();
    for (int i = 0; i < columnDescriptions.length; i++) {
        if (columnDescriptions[i].getName().equals(ReservedVariable.TIME.getName())) {
        	timeColumnDescription = columnDescriptions[i];
        }
    }
	//find filtered columnDescriptions
	columnDescriptions = odedi.getFilteredColumnDescriptions();
	DataSymbolMetadataResolver damdr = odedi.getDataSymbolMetadataResolver();

    for (int i = 0; i < columnDescriptions.length; i++) {
        ColumnDescription cd = columnDescriptions[i];
        // or maybe anything that is measured in molecules??
        //If the column is "TrialNo" from multiple trials, we don't put the column "TrialNo" in. amended March 12th, 2007
        //If the column is "_initConnt" generated when using concentration as initial condition, we dont' put the function in list. amended again in August, 2008.
        if (cd.getParameterName() == null) {
        	String name = cd.getName();
        	DataSymbolMetadata damd = damdr.getDataSymbolMetadata(name);
        	
        	// filter entities measured as count vs concentration, based on the checkbox settings
			ModelCategoryType filterCategory = null;
			if (damd != null) {
				filterCategory = damd.filterCategory;
			}
			if(countCheckBox != null && concentrationCheckBox != null) {
				if(filterCategory instanceof BioModelCategoryType && filterCategory == BioModelCategoryType.Species && cd.getName().endsWith(AbstractMathMapping.MATH_VAR_SUFFIX_SPECIES_COUNT) && !countCheckBox.isSelected()) {
					continue;
				} else if(filterCategory instanceof BioModelCategoryType && filterCategory == BioModelCategoryType.Species && !cd.getName().endsWith(AbstractMathMapping.MATH_VAR_SUFFIX_SPECIES_COUNT) && !concentrationCheckBox.isSelected()) {
					continue;
				}
			}
			
			// filter out entities starting with "UnitFactor_" prefix
			if(filterCategory instanceof BioModelCategoryType && filterCategory == BioModelCategoryType.Other && cd.getName().startsWith(AbstractMathMapping.PARAMETER_K_UNITFACTOR_PREFIX)) {
				continue;
			}
        	
        	if (!cd.getName().equals(SimDataConstants.HISTOGRAM_INDEX_NAME) && !cd.getName().contains(DiffEquMathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT)) {
        		variableColumnDescriptions.add(cd);
        	}
        } else {
        	sensitivityColumnDescriptions.add(cd);
        }
    }
    sortColumnDescriptions(variableColumnDescriptions);
    sortColumnDescriptions(sensitivityColumnDescriptions); 
    
    //  Hack this here, Later we can use an array utility...
    ArrayList<ColumnDescription> sortedColumndDescriptions = new ArrayList<ColumnDescription>();
    if (timeColumnDescription != null) {
    	sortedColumndDescriptions.add(timeColumnDescription); // add time first
	}
    
    boolean bMultiTrialData = odedi.isMultiTrialData();
    
    sortedColumndDescriptions.addAll(variableColumnDescriptions);
    if(!bMultiTrialData)
    {
    	sortedColumndDescriptions.addAll(sensitivityColumnDescriptions);
    }
    //  End hack
//    setPlottableColumnIndices(sortedIndices);
   
    // finally, update widgets
    try {
    	getXAxisComboBox_frm().removeItemListener(ivjEventHandler);
    	getYAxisChoice().removeListSelectionListener(ivjEventHandler);
	    getComboBoxModelX_frm().removeAllElements();
	    if(!bMultiTrialData) {
	       	// Don't put anything in X Axis, if the results of multiple trials are being displayed.
	    	ArrayList<ColumnDescription> xColumnDescriptions = new ArrayList<ColumnDescription>(Arrays.asList(odedi.getAllColumnDescriptions()));
	    	sortColumnDescriptions(xColumnDescriptions);
	    	if(timeColumnDescription != null){
	    		getComboBoxModelX_frm().addElement(timeColumnDescription.getName());
	    	}
	    	for(ColumnDescription columnDescription:xColumnDescriptions) {
	    		if(!columnDescription.getName().equals((timeColumnDescription==null?null:timeColumnDescription.getName()))) {
	    			getComboBoxModelX_frm().addElement(columnDescription.getName());
	    		}
	    	}
		}
	    
	    getDefaultListModelY().removeAllElements();
	    for (int i = 0; i < sortedColumndDescriptions.size(); i++) {
	        if (sortedColumndDescriptions.get(i).getName().equals(ReservedVariable.TIME.getName())) {
	        	continue;
	        }
	        getDefaultListModelY().addElement(sortedColumndDescriptions.get(i).getName());
	    }
	    
	    
	    
	    if (sortedColumndDescriptions.size() > 0) {
	    	//Don't put anything in X Axis, if the results of multifple trials are being displayed.
	    	if(!bMultiTrialData) {
	    		getXAxisComboBox_frm().setSelectedItem(xAxisSelection);
		    	if(getXAxisComboBox_frm().getSelectedIndex() == -1) {
		    		getXAxisComboBox_frm().setSelectedIndex(0);
		    	}
	    	}
	    	if(yAxisSelections != null && yAxisSelections.length > 0) {
	    		ArrayList<Integer> carryoverSelections = new ArrayList<Integer>();
	    		for (int i = 0; i < getYAxisChoice().getModel().getSize(); i++) {
	    			for (int j = 0; j < yAxisSelections.length; j++) {
						if(getYAxisChoice().getModel().getElementAt(i).equals(yAxisSelections[j])) {
							carryoverSelections.add(i);
							break;
						}
					}
				}
	    		if(carryoverSelections.size() > 0) {
	    			int[] carryoverInts = new int[carryoverSelections.size()];
	    			for (int i = 0; i < carryoverInts.length; i++) {
						carryoverInts[i] = carryoverSelections.get(i);
					}
	    			getYAxisChoice().setSelectedIndices(carryoverInts);
	    		} else {
	    			getYAxisChoice().setSelectedIndex((getYAxisChoice().getModel().getSize()>1?1:0));
	    		}
	    	} else {
	    		getYAxisChoice().setSelectedIndex(sortedColumndDescriptions.size() > 1 ? 1 : 0);
	    	}
	    }
    } finally {
    	getXAxisComboBox_frm().addItemListener(ivjEventHandler);
    	getYAxisChoice().addListSelectionListener(ivjEventHandler);
    }
    
    regeneratePlot2D();
}

public Plot2D getPlot2D() {
	return fieldPlot2D;
}

private void setPlot2D(Plot2D plot2D) {
	//amended March 29, 2007. To fire event to ODEDataViewer.
	Plot2D oldValue = this.fieldPlot2D;
	this.fieldPlot2D = plot2D;
	firePropertyChange("Plot2D", oldValue, plot2D);
}
/*
 * To get a hash table with keys as possible results for a specific variable after certain time period
 * and the values as the frequency. It is sorted ascendantly.
 */
public static Point2D[] generateHistogram(double[] rawData)
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
	Vector<Integer> keys = new Vector<Integer>(temp.keySet());
	Collections.sort(keys);
	Point2D[] result = new Point2D[keys.size()];
	for (int i=0; i<keys.size(); i++)
	{
        Integer key = keys.elementAt(i);
        Double valperc = new Double(((double)temp.get(key).intValue())/((double)rawData.length));
        result[i] = new Point2D.Double(key,valperc);
    }
	return result;
}

public String[] getSelectedVariableNames() {
	Object[] selectedValues = getYAxisChoice().getSelectedValues();
	String[] selectedNames = new String[selectedValues.length];
	for (int i = 0; i < selectedNames.length; i++) {
		selectedNames[i] = selectedValues[i].toString();
	}
	return selectedNames;
}
private JPanel getPanel() {
	if (panel == null) {
		panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		GridBagConstraints gbc_filterPanel = new GridBagConstraints();
		gbc_filterPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_filterPanel.weightx = 1.0;
		gbc_filterPanel.gridx = 0;
		gbc_filterPanel.gridy = 0;
		panel.add(getFilterPanel(), gbc_filterPanel);
	}
	return panel;
}

private HashMap<JCheckBox, ModelCategoryType> filterSettings;
private JLabel yAxisJLabel;
private JCheckBox getFilterSetting(ModelCategoryType filterCategory){
	if(filterSettings == null){
		filterSettings = new HashMap<JCheckBox, ModelCategoryType>();
	}
	for(JCheckBox jCheckBox:filterSettings.keySet()){
		if(filterSettings.get(jCheckBox).equals(filterCategory)){
			return jCheckBox;
		}
	}
	JCheckBox newJCheckBox = new JCheckBox(filterCategory.toString()){
		@Override
		public Dimension getMaximumSize() {
			//reduce vertical space between checkboxes
			return new Dimension(super.getMaximumSize().width, 17);//super.getMaximumSize();
		}
		@Override
		public Dimension getPreferredSize() {
			// TODO Auto-generated method stub
			return  getMaximumSize();//super.getPreferredSize();
		}
		
	};
	newJCheckBox.setSelected(filterCategory.isInitialSelect());
	newJCheckBox.setEnabled(filterCategory.isEnabled());
	filterSettings.put(newJCheckBox,filterCategory);
	return newJCheckBox;		
}
private void showFilterSettings() {
	if(getMyDataInterface() != null &&
		getMyDataInterface().getSupportedFilterCategories() != null &&
		getMyDataInterface().getSupportedFilterCategories().length > 0){
		
		getFilterPanel().getContentPanel().removeAll();
		getFilterPanel().getContentPanel().setLayout(null);
		HashMap<JCheckBox, ModelCategoryType> oldFilterSettings = filterSettings;
		filterSettings = null;

		ModelCategoryType[] filterCategories = getMyDataInterface().getSupportedFilterCategories();
		for (int i = 0; i < filterCategories.length; i++) {
			JCheckBox newFiltersJCheckBox = getFilterSetting(filterCategories[i]);//generate filter set
			if(oldFilterSettings != null){
				for(JCheckBox jCheckBox:oldFilterSettings.keySet()){
					if(filterSettings.get(newFiltersJCheckBox).equals(oldFilterSettings.get(jCheckBox))){
						newFiltersJCheckBox.setSelected(jCheckBox.isSelected());
						break;
					}
				}
			}
		}
	} else {
//		DialogUtils.showWarningDialog(this, "Cannot display filter, no filter categories set.");
		getFilterPanel().getContentPanel().setLayout(new BorderLayout());
		getFilterPanel().getContentPanel().add(new JLabel("No Filters"));
		return;
	}
	
	boolean hasCount = false;
	ColumnDescription[] columnDescriptions = getMyDataInterface().getAllColumnDescriptions();
	if(columnDescriptions != null) {
		for(ColumnDescription cd : columnDescriptions) {
			if(cd.getName().endsWith(AbstractMathMapping.MATH_VAR_SUFFIX_SPECIES_COUNT)) {
				hasCount = true;
				break;
			}
		}
	}
	
//	filterPanel.setBorder(new LineBorder(Color.black));
	JCheckBox[] sortedJCheckBoxes = filterSettings.keySet().toArray(new JCheckBox[0]);
	Arrays.sort(sortedJCheckBoxes, new Comparator<JCheckBox>() {
		@Override
		public int compare(JCheckBox o1, JCheckBox o2) {
			// TODO Auto-generated method stub
			return o1.getText().compareToIgnoreCase(o2.getText());
		}
	});
	for (int i = 0; i < sortedJCheckBoxes.length; i++) {
		sortedJCheckBoxes[i].removeItemListener(ivjEventHandler);
		sortedJCheckBoxes[i].addItemListener(ivjEventHandler);
	}
	
	JPanel filterContentPanel = getFilterPanel().getContentPanel();
	filterContentPanel.setLayout(new GridBagLayout());
	int gridx = 0;
	int gridy = 0;
	for (int i = 0; i < sortedJCheckBoxes.length; i++) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		if(!hasCount && (i == sortedJCheckBoxes.length-1)) {
			gbc.insets = new Insets(4, 2, 4, 0);
		} else {
			gbc.insets = new Insets(4, 2, 0, 0);
		}
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		filterContentPanel.add(sortedJCheckBoxes[i], gbc);
		gridy++;
	}
	if(hasCount) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.insets = new Insets(1, 10, 3, 0);
		gbc.anchor = GridBagConstraints.WEST;
		filterContentPanel.add(getSpeciesOptionsPanel(), gbc);
	}
}

private JPanel getSpeciesOptionsPanel() {
	if (speciesOptionsPanel == null) {
		speciesOptionsPanel = new JPanel();
		speciesOptionsPanel.setName("speciesOptionsPanel");
		speciesOptionsPanel.setLayout(new GridBagLayout());
		
		int gridx = 0;
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		speciesOptionsPanel.add(new JLabel(""), gbc);

		gridx++;
		concentrationCheckBox = new JCheckBox("Concentration");
		concentrationCheckBox.setToolTipText("Show Species measured as concentration units.");
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 0, 0);
		gbc.weightx = 0;
		speciesOptionsPanel.add(concentrationCheckBox, gbc);

		gridx++;
		countCheckBox = new JCheckBox("Count");
		countCheckBox.setToolTipText("Show Species measured as number of molecules.");
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 0, 0);
		gbc.weightx = 0;
		speciesOptionsPanel.add(countCheckBox, gbc);
		
		concentrationCheckBox.addItemListener(ivjEventHandler);
		countCheckBox.addItemListener(ivjEventHandler);
		concentrationCheckBox.setSelected(true);
		countCheckBox.setSelected(true);
	}
	return speciesOptionsPanel;
}


private void processFilterSelection(){
	if(filterSettings != null){
		ArrayList<ModelCategoryType> selectedFilterCategories = new ArrayList<ModelCategoryType>();
		for(JCheckBox jCheckBox:filterSettings.keySet()){
			if(jCheckBox.isSelected()){
				selectedFilterCategories.add(filterSettings.get(jCheckBox));
			}
		}
		getMyDataInterface().selectCategory(selectedFilterCategories.toArray(new ModelCategoryType[0]));
	}else{
		getMyDataInterface().selectCategory(null);
		
	}
}

	private JLabel getYAxisLabel() {
		if (yAxisJLabel == null) {
			yAxisJLabel = new JLabel("Y Axis:");
		}
		return yAxisJLabel;
	}
}
