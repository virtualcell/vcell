/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization.gui;
import java.io.File;
import java.text.ParseException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.exporter.FileFilters;

import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.math.CSV;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.modelopt.DataSource;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
/**
 * Insert the type's description here.
 * Creation date: (8/23/2005 4:26:30 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ReferenceDataPanel extends javax.swing.JPanel {
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private UserPreferences fieldUserPreferences = null;
	private javax.swing.JButton ivjImportButton = null;
	private javax.swing.JButton ivjSubsampleButton = null;
	private MultisourcePlotPane ivjmultisourcePlotPane = null;
	private javax.swing.JButton ivjeditButton = null;
	private javax.swing.JPanel ivjeditorPanel = null;
	private javax.swing.JLabel ivjeditorPanelHelpLabel = null;
	private javax.swing.JButton ivjhelpButton = null;
	private javax.swing.JTextArea ivjeditorTextArea = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private ParameterEstimationTask fieldParameterEstimationTask = null;
	private int timeIndex = -1;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ReferenceDataPanel.this.getImportButton()) 
				updateReferenceDataFromFile();
			if (e.getSource() == ReferenceDataPanel.this.getSubsampleButton()) 
				updateReferenceData(subsample());
			if (e.getSource() == ReferenceDataPanel.this.gethelpButton())
				showHelp(null);
			if (e.getSource() == ReferenceDataPanel.this.geteditButton())
				showEditor();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (fieldParameterEstimationTask != null 
					&& evt.getSource() == fieldParameterEstimationTask.getModelOptimizationSpec() 
					&& evt.getPropertyName().equals(ModelOptimizationSpec.PROPERTY_NAME_REFERENCE_DATA)) {
				updatePlot();
			}
		};
	};

/**
 * ReferenceDataPanel constructor comment.
 */
public ReferenceDataPanel() {
	super();
	initialize();
}

/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton geteditButton() {
	if (ivjeditButton == null) {
		try {
			ivjeditButton = new javax.swing.JButton();
			ivjeditButton.setName("editButton");
			ivjeditButton.setText("Edit...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjeditButton;
}

/**
 * Return the editorPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel geteditorPanel() {
	if (ivjeditorPanel == null) {
		try {
			ivjeditorPanel = new javax.swing.JPanel();
			ivjeditorPanel.setName("editorPanel");
			ivjeditorPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintseditorPanelHelpLabel = new java.awt.GridBagConstraints();
			constraintseditorPanelHelpLabel.gridx = 0; constraintseditorPanelHelpLabel.gridy = 0;
			constraintseditorPanelHelpLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintseditorPanelHelpLabel.weightx = 1.0;
			constraintseditorPanelHelpLabel.insets = new java.awt.Insets(8, 8, 8, 8);
			geteditorPanel().add(geteditorPanelHelpLabel(), constraintseditorPanelHelpLabel);

			java.awt.GridBagConstraints constraintseditorTextFieldScrollPane = new java.awt.GridBagConstraints();
			constraintseditorTextFieldScrollPane.gridx = 0; constraintseditorTextFieldScrollPane.gridy = 1;
			constraintseditorTextFieldScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintseditorTextFieldScrollPane.weightx = 1.0;
			constraintseditorTextFieldScrollPane.weighty = 1.0;
			constraintseditorTextFieldScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			geteditorPanel().add(new JScrollPane(geteditorTextArea()), constraintseditorTextFieldScrollPane);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjeditorPanel;
}


/**
 * Return the editorPanelHelpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel geteditorPanelHelpLabel() {
	if (ivjeditorPanelHelpLabel == null) {
		try {
			ivjeditorPanelHelpLabel = new javax.swing.JLabel();
			ivjeditorPanelHelpLabel.setName("editorPanelHelpLabel");
			ivjeditorPanelHelpLabel.setText("<html>Please enter data separated by commas, tabs, or spaces.  Column 1 should contain the times.  " +
					"Each row represents data at that time point.  The first row must contain column names." +
					"<br><br>Example:<br><br>" +
					"time, sample1, sample2<br>" +
					"0.0\t1.2030\t39.3828<br>" +
					"0.1\t1.345\t36.3939" +
					"<br>0.2\t1.2345\t44.334" +
					"<br>...</html>");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjeditorPanelHelpLabel;
}

/**
 * Return the editorTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea geteditorTextArea() {
	if (ivjeditorTextArea == null) {
		try {
			ivjeditorTextArea = new javax.swing.JTextArea(15, 40);
			ivjeditorTextArea.setName("editorTextArea");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjeditorTextArea;
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton gethelpButton() {
	if (ivjhelpButton == null) {
		try {
			ivjhelpButton = new javax.swing.JButton();
			ivjhelpButton.setName("helpButton");
			ivjhelpButton.setText("Help...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjhelpButton;
}

/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getImportButton() {
	if (ivjImportButton == null) {
		try {
			ivjImportButton = new javax.swing.JButton();
			ivjImportButton.setName("ImportButton");
			ivjImportButton.setText("Import from CSV file...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImportButton;
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
			ivjJPanel1.setLayout(new java.awt.FlowLayout());
			getJPanel1().add(getImportButton(), getImportButton().getName());
			ivjJPanel1.add(geteditButton());
			ivjJPanel1.add(getSubsampleButton());
			ivjJPanel1.add(gethelpButton());
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
 * Return the multisourcePlotPane property value.
 * @return cbit.vcell.modelopt.gui.MultisourcePlotPane
 */
private MultisourcePlotPane getmultisourcePlotPane() {
	if (ivjmultisourcePlotPane == null) {
		try {
			ivjmultisourcePlotPane = new MultisourcePlotPane();
			ivjmultisourcePlotPane.setName("multisourcePlotPane");
			ivjmultisourcePlotPane.setListVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjmultisourcePlotPane;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSubsampleButton() {
	if (ivjSubsampleButton == null) {
		try {
			ivjSubsampleButton = new javax.swing.JButton();
			ivjSubsampleButton.setName("SubsampleButton");
			ivjSubsampleButton.setText("Subsample");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSubsampleButton;
}

/**
 * Gets the userPreferences property (cbit.vcell.client.server.UserPreferences) value.
 * @return The userPreferences property value.
 * @see #setUserPreferences
 */
private UserPreferences getUserPreferences() {
	return fieldUserPreferences;
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
 * Comment
 */
private void updateReferenceDataFromFile() {
	try {
		VCFileChooser fileChooser = new VCFileChooser();
		fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_CSV);
		// Set the default file filter...
		fileChooser.setFileFilter(FileFilters.FILE_FILTER_CSV);
		// remove all selector
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		File defaultPath = null;
		if (getUserPreferences()!=null){
		    defaultPath = getUserPreferences().getCurrentDialogPath();
		    if (defaultPath!=null){
			    fileChooser.setCurrentDirectory(defaultPath);
		    }
		}
	
		fileChooser.setDialogTitle("Import Data File");
		if (fileChooser.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) {
			// user didn't choose save
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile == null) {
				// no file selected (no name given)
				throw UserCancelException.CANCEL_FILE_SELECTION;
			} else {
				if (getUserPreferences()!=null){
					File newPath = selectedFile.getParentFile();
					if (!newPath.equals(defaultPath)) {
						getUserPreferences().setCurrentDialogPath(newPath);
					}
				}
				CSV csv = new CSV();
				RowColumnResultSet rowColumnResultSet = csv.importFrom(new java.io.FileReader(selectedFile));
				double[] weights = new double[rowColumnResultSet.getDataColumnCount()];
				java.util.Arrays.fill(weights, 1.0);
				ReferenceData referenceData = new SimpleReferenceData(rowColumnResultSet, weights);
				updateReferenceData(referenceData);
			}
		}
	} catch (UserCancelException e) {
		//ignore
	} catch (Exception e) {
		e.printStackTrace();
		if(e instanceof ParseException){
			showHelp((ParseException) e);
		}else{
			DialogUtils.showErrorDialog(this, e.getMessage(), e);
		}
	}
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getImportButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getSubsampleButton().addActionListener(ivjEventHandler);
	gethelpButton().addActionListener(ivjEventHandler);
	geteditButton().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ReferenceDataPanel");
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsmultisourcePlotPane = new java.awt.GridBagConstraints();
		constraintsmultisourcePlotPane.gridx = 0; constraintsmultisourcePlotPane.gridy = 0;
		constraintsmultisourcePlotPane.gridheight = 4;
		constraintsmultisourcePlotPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsmultisourcePlotPane.weightx = 1.0;
		constraintsmultisourcePlotPane.weighty = 1.0;
		constraintsmultisourcePlotPane.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getmultisourcePlotPane(), constraintsmultisourcePlotPane);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 4;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Sets the userPreferences property (cbit.vcell.client.server.UserPreferences) value.
 * @param userPreferences The new value for the property.
 * @see #getUserPreferences
 */
public void setUserPreferences(UserPreferences userPreferences) {
	UserPreferences oldValue = fieldUserPreferences;
	fieldUserPreferences = userPreferences;
	firePropertyChange("userPreferences", oldValue, userPreferences);
}


/**
 * Comment
 */
private void showEditor() {
	ReferenceData referenceData = fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceData();
	if (referenceData!=null){
		geteditorTextArea().setText(((SimpleReferenceData)referenceData).getCSV());
	}else{
		geteditorTextArea().setText("t, data1, data2\n0.0, 0.1, 0.21\n0.1, 0.15, 0.31\n0.2, 0.16, 0.44");
	}
	geteditorTextArea().setCaretPosition(0);
	try {
		int retVal = DialogUtils.showComponentOKCancelDialog(this,geteditorPanel(),"time series data editor");
		if (retVal == javax.swing.JOptionPane.OK_OPTION){
			RowColumnResultSet rc = (new CSV()).importFrom(new java.io.StringReader(geteditorTextArea().getText()));
			double weights[] = new double[rc.getDataColumnCount()];
			java.util.Arrays.fill(weights,1.0);
			SimpleReferenceData simpleRefData = new SimpleReferenceData(rc,weights);
			updateReferenceData(simpleRefData);
		}
	}catch (ParseException e){
		e.printStackTrace();
		showHelp(e);
	}catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this,e.getMessage(), e);
	}
}


/**
 * Comment
 */
private void showHelp(ParseException e) {
	String message =
		"<html>"+
		(e!=null?e.getMessage() + "<br>" :"")+
		"Time Series Data format:" +
		"<ul>"+
		"<li>Column 1 should contain the times.</li>"+
		"<li>The first row must contain column names (e.g. t, var1, var2).</li>"+
		"<li>Each sucessive row represents data at a time point.</li>"+
		"</ul>" +
		"</html>";
	DialogUtils.showInfoDialog(this, message);
}


/**
 * Comment
 */
private ReferenceData subsample() {
	ReferenceData refData = fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceData();
	if (refData==null){
		return refData;
	}
	
	RowColumnResultSet rc = new RowColumnResultSet();
	String[] columnNames = refData.getColumnNames(); 
	for (int i = 0; i < columnNames.length; i++){
		rc.addDataColumn(new ODESolverResultSetColumnDescription(i == timeIndex ?  ReservedVariable.TIME.getName() : columnNames[i]));		
	}
	for (int i = 0; i < refData.getNumDataRows(); i++){
		rc.addRow((double[])refData.getDataByRow(i).clone());
	}

	int desiredNumRows = refData.getNumDataRows() / 2;
	if (desiredNumRows<3){
		return refData;
	}
	try {
		rc.trimRows(desiredNumRows);
		double weights[] = null;
		if (refData.getColumnWeights()!=null){
			weights = (double[])refData.getColumnWeights().clone();
		}else{
			weights = new double[refData.getColumnNames().length];
			java.util.Arrays.fill(weights,1.0);
		}
		SimpleReferenceData srd = new SimpleReferenceData(rc,weights);
		return srd;
	}catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this, e.getMessage(), e);
		return refData;
	}
	
}

private void updateReferenceData(ReferenceData refData) {
	if (fieldParameterEstimationTask != null) {
		fieldParameterEstimationTask.getModelOptimizationSpec().setReferenceData(refData);
	}
}

/**
 * Comment
 */
private void updatePlot() {
	if (fieldParameterEstimationTask == null || fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceData()==null){
		getmultisourcePlotPane().setDataSources(null);
		return;
	}
	
	DataSource[] dataSources = new DataSource[1];
	dataSources[0] = new DataSource.DataSourceReferenceData("refData", timeIndex < 0 ? 0 : timeIndex, fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceData());
	getmultisourcePlotPane().setDataSources(dataSources);

	getmultisourcePlotPane().selectAll();

}

public void setParameterEstimationTask(ParameterEstimationTask newValue) {
	ParameterEstimationTask oldValue = fieldParameterEstimationTask;
	fieldParameterEstimationTask = newValue;
	
	if (oldValue!=null){
		oldValue.getModelOptimizationSpec().removePropertyChangeListener(ivjEventHandler);
		ReferenceDataMappingSpec[] refDataMappingSpecs = oldValue.getModelOptimizationSpec().getReferenceDataMappingSpecs();
		if (refDataMappingSpecs != null) {
			for (ReferenceDataMappingSpec refDataMappingSpec : refDataMappingSpecs){
				refDataMappingSpec.removePropertyChangeListener(ivjEventHandler);
			}
		}
	}
	if (fieldParameterEstimationTask!=null){
		newValue.getModelOptimizationSpec().addPropertyChangeListener(ivjEventHandler);
		ReferenceDataMappingSpec[] refDataMappingSpecs = fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpecs();
		if (refDataMappingSpecs != null) {
			for (ReferenceDataMappingSpec refDataMappingSpec : refDataMappingSpecs){
				refDataMappingSpec.addPropertyChangeListener(ivjEventHandler);
			}
		}
		timeIndex = fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceDataTimeColumnIndex();
	} else {
		// fieldParameterEstimationTask is null, so referenceData cannot have any data, should be null too?
		timeIndex = 0;
	}
	updatePlot();
}


}
