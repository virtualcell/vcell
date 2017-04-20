/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.vcell.util.BeanUtils;

import cbit.image.SourceDataInfo;
import cbit.image.gui.ImagePaneModel;
import cbit.image.gui.ImagePaneScrollerTest;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.export.gui.ExportMonitorPanel;
import cbit.vcell.parser.ExpressionException;

@SuppressWarnings("serial")
public class DataSymbolsSpecPanel extends DataViewer {
	@Override
	public ExportMonitorPanel getExportMonitorPanel() {
		// TODO Auto-generated method stub
		return null;
	}

//	private SimulationContext fieldSimulationContext = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
//	private JButton ivjJButtonCreateDataSymbol = null;
	private JButton ivjJButtonViewFieldData = null;
	private JCheckBox chckbxPointSpreadFunction = null;
	ImagePlaneManagerPanel ivjImagePlaneManagerPanel = null;
    
    DataSymbol ivjCurrentSymbol = null;
    int countW = 0;
    int countH = 0;

	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
//			if (e.getSource() == DataSymbolsSpecPanel.this.getJButtonCreateDataSymbol()) 
//				refireIt(e);
			if (e.getSource() == DataSymbolsSpecPanel.this.getJButtonViewFieldData()) {
				try {
					Component requesterComponent = DataSymbolsSpecPanel.this;
					DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(requesterComponent, DocumentWindow.class);
					documentWindow.getTopLevelWindowManager().getRequestManager().showFieldDataWindow(null);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DataSymbolsSpecPanel.this && (evt.getPropertyName().equals("EnableButtonsEvent"))) 
			{ 
				initButtons(evt);
			}
		};
	};

	private void initButtons(java.beans.PropertyChangeEvent arg1) {
		try {
			this.initButtons();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	private void initButtons() {
		boolean bSpatial = true;
//		getJButtonCreateDataSymbol().setEnabled(bSpatial);
		getJButtonViewFieldData().setEnabled(bSpatial);
	}
/**
 * Constructor
 */
	public DataSymbolsSpecPanel() {
		super();
		initialize();
	}

private ImagePlaneManagerPanel getImagePlaneManagerPanel() {
	if (ivjImagePlaneManagerPanel == null) {
		try {
			ivjImagePlaneManagerPanel = new ImagePlaneManagerPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjImagePlaneManagerPanel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	if (exception instanceof ExpressionException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in DataSymbolsSpecPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {	
//	getJButtonCreateDataSymbol().addActionListener(ivjEventHandler);
	getJButtonViewFieldData().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("DataSymbolsSpecPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(572, 196);
		
		GridBagConstraints constraintsDataSymbolImagePanel = new GridBagConstraints();
		constraintsDataSymbolImagePanel.gridx = 0; constraintsDataSymbolImagePanel.gridy = 0;
		constraintsDataSymbolImagePanel.gridwidth = 5;
		constraintsDataSymbolImagePanel.fill = GridBagConstraints.BOTH;
		constraintsDataSymbolImagePanel.weightx = 1.0;
		constraintsDataSymbolImagePanel.weighty = 1.0;
		constraintsDataSymbolImagePanel.insets = new Insets(4, 4, 5, 4);
		add(getImagePlaneManagerPanel(), constraintsDataSymbolImagePanel);

//		GridBagConstraints constraintsJButtonNewDataSymbol = new GridBagConstraints();
//		constraintsJButtonNewDataSymbol.gridx = 0; constraintsJButtonNewDataSymbol.gridy = 1;
//		constraintsJButtonNewDataSymbol.insets = new Insets(4, 4, 4, 5);
//		add(getJButtonCreateDataSymbol(), constraintsJButtonNewDataSymbol);
		
		GridBagConstraints gbc_chckbxPointSpreadFunction = new GridBagConstraints();
		gbc_chckbxPointSpreadFunction.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxPointSpreadFunction.gridx = 2;
		gbc_chckbxPointSpreadFunction.gridy = 1;
		add(getChckbxPointSpreadFunction(), gbc_chckbxPointSpreadFunction);
		getChckbxPointSpreadFunction().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxPointSpreadFunctionActionPerformed();
			}
		});

		GridBagConstraints constraintsJButtonViewFieldData = new GridBagConstraints();
		constraintsJButtonViewFieldData.anchor = GridBagConstraints.EAST;
		constraintsJButtonViewFieldData.weightx = 1.0;
		constraintsJButtonViewFieldData.gridx = 3; constraintsJButtonViewFieldData.gridy = 1;
		constraintsJButtonViewFieldData.insets = new Insets(4, 4, 4, 5);
		add(getJButtonViewFieldData(), constraintsJButtonViewFieldData);

		initConnections();
		
		getImagePlaneManagerPanel().setDisplayAdapterServicePanelVisible(false);
		getImagePlaneManagerPanel().setCurveEditorToolPanelVisible(false);

		//setModel(getImagePaneModel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public static final String NEW_DATA_SYMBOL_LABEL = "Create New Data Symbol...";
public static final String DELETE_DATA_SYMBOL_LABEL = "Delete Data Symbol";
public static final String VIEW_FIELD_DATA_LABEL = "Open Field Data Manager";
//private javax.swing.JButton getJButtonCreateDataSymbol() {
//	if (ivjJButtonCreateDataSymbol == null) {
//		try {
//			ivjJButtonCreateDataSymbol = new javax.swing.JButton();
//			ivjJButtonCreateDataSymbol.setName("JButtonCreateDataSymbol");
//			ivjJButtonCreateDataSymbol.setText(NEW_DATA_SYMBOL_LABEL);
//			ivjJButtonCreateDataSymbol.setActionCommand(GuiConstants.ACTIONCMD_CREATE_DATA_SYMBOL);
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//	}
//	return ivjJButtonCreateDataSymbol;
//}

private javax.swing.JButton getJButtonViewFieldData() {
	if (ivjJButtonViewFieldData == null) {
		try {
			ivjJButtonViewFieldData = new javax.swing.JButton();
			ivjJButtonViewFieldData.setName("JButtonViewFieldData");
			ivjJButtonViewFieldData.setText(VIEW_FIELD_DATA_LABEL);
			ivjJButtonViewFieldData.setActionCommand(GuiConstants.ACTIONCMD_VIEW_FIELD_DATA);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonViewFieldData;
}

//public void createDataSymbol() throws Exception, UserCancelException{
//	final int VFRAP_DATASET = 0;
//	final int VFRAP_SPECIALS = 1;
//	final int ASSOCIATE_EXISTING_FD = 2;
//	final int POINT_SPREAD_FUNCTION = 3;
//	final int IMAGE_FILE = 4;
//	final int COPY_FROM_BIOMODEL = 5;
//	int[] dataSymbolSource = null;
//
//	String[][] choices = new String[][] {{ADD_VFRAP_DATASET_MENU},{ADD_VFRAP_SPECIALS_MENU},{ADD_ASSOCIATE_EXISTING_FD_MENU},
//			{ADD_PSF_MENU},{ADD_IMAGE_FILE_MENU},{ADD_COPY_FROM_BIOMODEL_MENU} };
//
//	dataSymbolSource = DialogUtils.showComponentOKCancelTableList(
//			getComponent(), 
//			"Choose a source for the data symbol.",
//			new String[] {"Available Sources:"}, 
//			choices, ListSelectionModel.SINGLE_SELECTION);
//
//	if(dataSymbolSource[0] == VFRAP_DATASET){
//		getDataSymbolsPanel().addVFrapOriginalImages();
//	}else if(dataSymbolSource[0] == VFRAP_SPECIALS){
//		getDataSymbolsPanel().addVFrapDerivedImages();
//	}else if(dataSymbolSource[0] == ASSOCIATE_EXISTING_FD){
//		Component requesterComponent = DataSymbolsSpecPanel.this;
//		DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(requesterComponent, DocumentWindow.class);
//		documentWindow.getTopLevelWindowManager().getRequestManager().showFieldDataWindow(new FieldDataWindowManager.DataSymbolCallBack() {
//			public void createDataSymbol(ExternalDataIdentifier dataSetID,
//					String fieldDataVarName, VariableType fieldDataVarType,
//					double fieldDataVarTime) {
//				
//				System.out.println(dataSetID+" "+fieldDataVarName+" "+fieldDataVarType+" "+fieldDataVarTime);
//				// ex: incomplete 51780592 danv(26766043)      fluor     Volume_VariableType     23.680419921875
//				
//	   	        DecimalFormat df = new  DecimalFormat("###000.00");		// max time interval we can display is about 11 days
//	   			String fluorName = fieldDataVarName + "_" + df.format(fieldDataVarTime).substring(0, df.format(fieldDataVarTime).indexOf(".")) + "s" + df.format(fieldDataVarTime).substring(1+df.format(fieldDataVarTime).indexOf(".")) + "_" + dataSetID.getName();
//// TODO:  symbol names may not be unique, must check for unicity and prompt the user
//				FieldDataSymbol dataSymbol = new FieldDataSymbol(fluorName, DataSymbolType.GENERIC_SYMBOL,
//						getSimulationContext().getDataContext(), VCUnitDefinition.UNIT_TBD,
//						dataSetID, 
//						fieldDataVarName, fieldDataVarType.getTypeName(), fieldDataVarTime);
//				getSimulationContext().getDataContext().addDataSymbol(dataSymbol);
//
//			}
//		});
//	}else if(dataSymbolSource[0] == POINT_SPREAD_FUNCTION){
//		PointSpreadFunctionManagement psfManager = new PointSpreadFunctionManagement(DataSymbolsSpecPanel.this,
//				getSimulationContext());
//		psfManager.importPointSpreadFunction();
//	}else if(dataSymbolSource[0] == IMAGE_FILE){
//		throw new RuntimeException("Option not yet implemented."); 
//	}else if(dataSymbolSource[0] == COPY_FROM_BIOMODEL){
//		throw new RuntimeException("Option not yet implemented."); 
//	}else{
//		throw new IllegalArgumentException("Error selecting data symbol, Unknown Source type " + dataSymbolSource[0]);
//	}
//}

public void setDataSymbol(Object object) {
	if(object == null) {
		// TODO: display empty image
		return;
	}
	if(((DataSymbol)object).equals(ivjCurrentSymbol)) {
		return;
	} else {
		ivjCurrentSymbol = (DataSymbol)object;
	}
	
	// manage checkbox status depending on type of current data symbol
	DataSymbolType dsType = ivjCurrentSymbol.getDataSymbolType();
	switch(dsType) {
	case GENERIC_SYMBOL:
		getChckbxPointSpreadFunction().setEnabled(true);
		getChckbxPointSpreadFunction().setSelected(false);
		break;
	case POINT_SPREAD_FUNCTION:
		getChckbxPointSpreadFunction().setEnabled(true);
		getChckbxPointSpreadFunction().setSelected(true);
		break;
	default:
		getChckbxPointSpreadFunction().setEnabled(false);
		getChckbxPointSpreadFunction().setSelected(false);
		break;
	}
	
	// displays iconized image for the current (field?) data symbol
	getImagePlaneManagerPanel().setMode(ImagePaneModel.MESH_MODE);
	int w = Integer.valueOf(30).intValue() + 5*(countW%5);
	int h = Integer.valueOf(20).intValue() + 3*(countH%7);
	String type = "double";
	System.out.println("  " + w + ", " + h);
	SourceDataInfo sdi = ImagePaneScrollerTest.getExampleSDI(type, w, h);
	getImagePlaneManagerPanel().setSourceDataInfo(sdi);

	countW++;
	countH++;
}
private DataSymbol getDataSymbol() {
	return ivjCurrentSymbol;
}

//	public void setSimulationContext(SimulationContext simulationContext) {
//		SimulationContext oldValue = fieldSimulationContext;
//		fieldSimulationContext = simulationContext;
//		firePropertyChange("simulationContext", oldValue, simulationContext);
//	}
//	public SimulationContext getSimulationContext() {
//		return fieldSimulationContext;
//	}

	private JCheckBox getChckbxPointSpreadFunction() {
		if (chckbxPointSpreadFunction == null) {
			chckbxPointSpreadFunction = new JCheckBox("Point Spread Function");
		}
		return chckbxPointSpreadFunction;
	}
	
	// toggle between GENERIC_SYMBOL and POINT_SPREAD_FUNCTION
	protected void chckbxPointSpreadFunctionActionPerformed() {
		if(getDataSymbol().getDataSymbolType().equals(DataSymbolType.GENERIC_SYMBOL)) {
			getDataSymbol().setDataSymbolType(DataSymbolType.POINT_SPREAD_FUNCTION);
		} else if(getDataSymbol().getDataSymbolType().equals(DataSymbolType.POINT_SPREAD_FUNCTION)) {
			getDataSymbol().setDataSymbolType(DataSymbolType.GENERIC_SYMBOL);
		}
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		DataSymbol dataSymbol = null;
		if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof DataSymbol) {
			dataSymbol = (DataSymbol) selectedObjects[0];
		}
		setDataSymbol(dataSymbol);	
		
	}

}
