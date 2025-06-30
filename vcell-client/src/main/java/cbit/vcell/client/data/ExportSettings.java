/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.data;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import cbit.vcell.export.gui.*;
import cbit.vcell.export.server.*;
import org.vcell.client.logicalwindow.transition.LWJDialogDecorator;
import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.VCAssert;

import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (1/18/00 12:04:07 PM)
 * @author: Ion I. Moraru
 */
public class ExportSettings implements ASCIISettingsPanelListener, RasterSettingsPanelListener, java.beans.PropertyChangeListener {
	private ASCIISettingsPanel ivjASCIISettingsPanel1 = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private ExportFormat fieldSelectedFormat;
	private cbit.vcell.export.server.FormatSpecificSpecs fieldFormatSpecificSpecs = null;
	private ExportSpecss.SimulationDataType fieldSimDataType = null;
	private JPanel ivjJDialogContentPane = null;
	private JDialog ivjJDialogASCIISettings = null;
	private JDialog ivjJDialogMediaSettings = null;
	private boolean closedOK = false;
	private JPanel ivjJDialogContentPane3 = null;
	private JDialog ivjJDialogRasterSettings = null;
	private RasterSettingsPanel ivjRasterSettingsPanel1 = null;

	private JDialog ivjJDialogN5Settings = null;
	private N5SettingsPanel ivjJDialogN5Panel = null;

/**
 * ExportSettings constructor comment.
 */
public ExportSettings() {
	super();
	initialize();
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Comment
 */
private void closeCancel(JDialog dialog) {
	setClosedOK(false);
	dialog.dispose();
}


/**
 * Comment
 */
private void closeOK(JDialog dialog) {
	setClosedOK(true);
	dialog.dispose();
}

	/**
 * connPtoP7SetTarget:  (ExportSettings.simDataType <--> ASCIISettingsPanel1.simDataType)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		getASCIISettingsPanel1().setSimDataType(this.getSimDataType());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

/**
 * Return the ASCIISettingsPanel1 property value.
 * @return cbit.vcell.export.ASCIISettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ASCIISettingsPanel getASCIISettingsPanel1() {
	if (ivjASCIISettingsPanel1 == null) {
		try {
			ivjASCIISettingsPanel1 = new cbit.vcell.export.gui.ASCIISettingsPanel();
			ivjASCIISettingsPanel1.setName("ASCIISettingsPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjASCIISettingsPanel1;
}

private N5SettingsPanel getN5SettingsPanel(){
	if (ivjJDialogN5Panel == null){
		try {
			ivjJDialogN5Panel = new cbit.vcell.export.gui.N5SettingsPanel();
			ivjJDialogN5Panel.setName("N5SettingsPnael");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogN5Panel;
}

/**
 * Gets the formatSpecificSpecs property (cbit.vcell.export.server.FormatSpecificSpecs) value.
 * @return The formatSpecificSpecs property value.
 * @see #setFormatSpecificSpecs
 */
public cbit.vcell.export.server.FormatSpecificSpecs getFormatSpecificSpecs() {
	return fieldFormatSpecificSpecs;
}


/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogASCIISettings(Frame reference) {
	if (ivjJDialogASCIISettings == null) {
		try {
			ivjJDialogASCIISettings = new javax.swing.JDialog(reference);
			ivjJDialogASCIISettings.setName("JDialogASCIISettings");
			ivjJDialogASCIISettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogASCIISettings.setBounds(123, 502, 176, 183);
			ivjJDialogASCIISettings.setModal(true);
			ivjJDialogASCIISettings.setTitle("ASCII Settings");
			ivjJDialogASCIISettings.add(getJDialogContentPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogASCIISettings;
}

/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogN5Settings(Frame reference){
	if (ivjJDialogN5Settings == null){
		try {
			ivjJDialogN5Settings = new javax.swing.JDialog(reference);
			ivjJDialogN5Settings.setName("JDialogN5Settings");
			ivjJDialogN5Settings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogN5Settings.setBounds(123, 502, 176, 183);
			ivjJDialogN5Settings.setModal(true);
			ivjJDialogN5Settings.setTitle("N5 Settings");
			ivjJDialogN5Settings.add(getN5SettingsPanel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogN5Settings;
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
			ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane().add(getASCIISettingsPanel1(), "Center");
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
 * Return the JDialogContentPane3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane3() {
	if (ivjJDialogContentPane3 == null) {
		try {
			ivjJDialogContentPane3 = new javax.swing.JPanel();
			ivjJDialogContentPane3.setName("JDialogContentPane3");
			ivjJDialogContentPane3.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane3().add(getRasterSettingsPanel1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane3;
}

/**
 * Return the JDialog3 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogMediaSettings(Frame reference,ExportFormat mediaType,boolean selectionHasVolumeVars,boolean selectionHasMembraneVars,String descriptor) {
	if (ivjJDialogMediaSettings == null) {
		try {
			ivjJDialogMediaSettings = new javax.swing.JDialog(reference);
			ivjJDialogMediaSettings.setName("JDialogMediaSettings");
			ivjJDialogMediaSettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogMediaSettings.setBounds(422, 85, 234, 368);
			ivjJDialogMediaSettings.setModal(true);
			ivjJDialogMediaSettings.setContentPane(getMediaSettingsPanel1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	getMediaSettingsPanel1().configure(mediaType, selectionHasVolumeVars,selectionHasMembraneVars);
	ivjJDialogMediaSettings.setTitle(descriptor+" Settings");
	return ivjJDialogMediaSettings;
}

/**
 * Return the JDialogRasterSettings property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogRasterSettings(Frame reference) {
	if (ivjJDialogRasterSettings == null) {
		try {
			ivjJDialogRasterSettings = new javax.swing.JDialog(reference);
			ivjJDialogRasterSettings.setName("JDialogRasterSettings");
			ivjJDialogRasterSettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogRasterSettings.setBounds(393, 536, 180, 214);
			ivjJDialogRasterSettings.setModal(true);
			ivjJDialogRasterSettings.setTitle("Raster Settings");
			ivjJDialogRasterSettings.add(getJDialogContentPane3());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogRasterSettings;
}

/**
 * Return the MovieSettingsPanel1 property value.
 * @return cbit.vcell.export.MovieSettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MediaSettingsPanel ivjMediaSettingsPanel1;
private MediaSettingsPanel getMediaSettingsPanel1() {
	if (ivjMediaSettingsPanel1 == null) {
		try {
			ivjMediaSettingsPanel1 = new MediaSettingsPanel();
			ivjMediaSettingsPanel1.setName("MediaSettingsPanel1");
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMediaSettingsPanel1;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Return the RasterSettingsPanel1 property value.
 * @return cbit.vcell.export.RasterSettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private RasterSettingsPanel getRasterSettingsPanel1() {
	if (ivjRasterSettingsPanel1 == null) {
		try {
			ivjRasterSettingsPanel1 = new cbit.vcell.export.gui.RasterSettingsPanel();
			ivjRasterSettingsPanel1.setName("RasterSettingsPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRasterSettingsPanel1;
}


/**
 * Gets the selectedFormat property (int) value.
 * @return The selectedFormat property value.
 * @see #setSelectedFormat
 */
public ExportFormat getSelectedFormat() {
	return fieldSelectedFormat;
}


/**
 * Gets the simDataType property (int) value.
 * @return The simDataType property value.
 * @see #setSimDataType
 */
public ExportSpecss.SimulationDataType getSimDataType() {
	return fieldSimDataType;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(this);
	getASCIISettingsPanel1().addASCIISettingsPanelListener(this);
	getMediaSettingsPanel1().addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals(MediaSettingsPanel.OK_ACTION_COMMAND)){
				setFormatSpecificSpecs(getMediaSettingsPanel1().getSpecs());
				closeOK(ivjJDialogMediaSettings);
			}else if(e.getActionCommand().equals(MediaSettingsPanel.CANCEL_ACTION_COMMAND)){
				closeCancel(ivjJDialogMediaSettings);
			}
		}
	});
	getRasterSettingsPanel1().addRasterSettingsPanelListener(this);
	getN5SettingsPanel().addN5SettingsPanelListener(this);

	connPtoP7SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 9:59:37 AM)
 * @return boolean
 */
private boolean isClosedOK() {
	return closedOK;
}


/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	try{
		if (newEvent.getSource() == getASCIISettingsPanel1())
			this.closeCancel(ivjJDialogASCIISettings);

		if (newEvent.getSource() == getRasterSettingsPanel1())
				this.closeCancel(ivjJDialogRasterSettings);

		if (newEvent.getSource() == getN5SettingsPanel())
			this.closeCancel(ivjJDialogN5Settings);
	}
	catch (Throwable ivjExc){
		handleException(ivjExc);
	}
}

/**
 * Method to handle events for the ASCIISettingsPanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	try{
		if (newEvent.getSource() == getASCIISettingsPanel1()){
			this.setFormatSpecificSpecs(getASCIISettingsPanel1().getAsciiSpecs());
			this.closeOK(ivjJDialogASCIISettings);
		}

		if (newEvent.getSource() == getRasterSettingsPanel1())
		{
			this.setFormatSpecificSpecs(getRasterSettingsPanel1().getRasterSpecs());
			this.closeOK(ivjJDialogRasterSettings);
		}

		if (newEvent.getSource() == getN5SettingsPanel()){
			this.setFormatSpecificSpecs(getN5SettingsPanel().getN5Specs());
			this.closeOK(ivjJDialogN5Settings);
		}
	}
	catch (Throwable ivjExc){
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		@SuppressWarnings("unused")
		ExportSettings aExportSettings;
		aExportSettings = new ExportSettings();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.lang.Object");
		exception.printStackTrace(System.out);
	}
}


/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("simDataType"))) 
		connPtoP7SetTarget();
	// user code begin {2}
	// user code end
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 9:59:37 AM)
 * @param newClosedOK boolean
 */
private void setClosedOK(boolean newClosedOK) {
	closedOK = newClosedOK;
}


/**
 * Sets the displayPreferences property (cbit.vcell.simdata.gui.DisplayPreferences[]) value.
 * @param displayPreferences The new value for the property.
 */
public void setDisplayPreferences(cbit.image.DisplayPreferences[] displayPreferences,String[] variableNames,int viewZoom) {
	getMediaSettingsPanel1().setDisplayPreferences(displayPreferences, variableNames,viewZoom);
}


/**
 * Sets the formatSpecificSpecs property (cbit.vcell.export.server.FormatSpecificSpecs) value.
 * @param formatSpecificSpecs The new value for the property.
 * @see #getFormatSpecificSpecs
 */
public void setFormatSpecificSpecs(cbit.vcell.export.server.FormatSpecificSpecs formatSpecificSpecs) {
	cbit.vcell.export.server.FormatSpecificSpecs oldValue = fieldFormatSpecificSpecs;
	fieldFormatSpecificSpecs = formatSpecificSpecs;
	firePropertyChange("formatSpecificSpecs", oldValue, formatSpecificSpecs);
}


/**
 * Sets the selectedFormat property (int) value.
 * @param selectedFormat The new value for the property.
 * @see #getSelectedFormat
 */
public void setSelectedFormat(ExportFormat selectedFormat) {
	ExportFormat oldValue = fieldSelectedFormat;
	fieldSelectedFormat = selectedFormat;
	firePropertyChange("selectedFormat", oldValue, selectedFormat);
}


/**
 * Sets the simDataType property (int) value.
 * @param simDataType The new value for the property.
 * @see #getSimDataType
 */
public void setSimDataType(ExportSpecss.SimulationDataType simDataType) {
	ExportSpecss.SimulationDataType oldValue = fieldSimDataType;
	fieldSimDataType = simDataType;
	firePropertyChange("simDataType", oldValue, simDataType);
}

public void setSliceCount(int sliceCount){
	getMediaSettingsPanel1().setSliceCount(sliceCount);
}
public void setTimeSpecs(TimeSpecs timeSpecs){
	getMediaSettingsPanel1().setTimeSpecs(timeSpecs);
}
public void setImageSizeCalculationInfo(CartesianMesh mesh,int normalAxis){
	getMediaSettingsPanel1().setImageSizeCalculationInfo(mesh, normalAxis);
}
public void setIsSmoldyn(boolean isSmoldyn){
	getMediaSettingsPanel1().setIsSmoldyn(isSmoldyn);
}
public void setSimulationSelector(ExportSpecs.SimulationSelector simulationSelector){
	getASCIISettingsPanel1().setSimulationSelector(simulationSelector);
}
public void setIsCSVExport(boolean isCSVExport){
	getASCIISettingsPanel1().setIsCSVExport(isCSVExport);
}
/**
 * Comment
 * Pop up dialog for data exported, specific to that format
 */
@SuppressWarnings("incomplete-switch")
public boolean showFormatSpecificDialog(Frame reference,boolean selectionHasVolumeVars,boolean selectionHasMembraneVars) {
	JDialog dialogToShow = null;
	setClosedOK(false);
	ExportFormat format = getSelectedFormat();
	VCAssert.assertTrue(format.requiresFollowOn(),"Follow on dialog required");
	switch (format) {
		case CSV:
		case HDF5:
			dialogToShow = getJDialogASCIISettings(reference);
			if(format == ExportFormat.HDF5) {
				dialogToShow.setTitle("HDF5 Settings");
				getASCIISettingsPanel1().getIvjJCheckBoxHDF5().setVisible(false);
				getASCIISettingsPanel1().getIvjJCheckBoxHDF5().setSelected(true);
			}else {
				dialogToShow.setTitle("Ascii Settings");
				getASCIISettingsPanel1().getIvjJCheckBoxHDF5().setVisible(false);
				getASCIISettingsPanel1().getIvjJCheckBoxHDF5().setSelected(false);
			}
			break;
		case QUICKTIME:
		case ANIMATED_GIF:
			dialogToShow = getJDialogMediaSettings(reference,getSelectedFormat(),selectionHasVolumeVars,selectionHasMembraneVars,"Movie");
			break;
		case GIF:
		case FORMAT_JPEG:
			dialogToShow = getJDialogMediaSettings(reference,getSelectedFormat(),selectionHasVolumeVars,selectionHasMembraneVars,"Image");
			break;
		case NRRD:
			dialogToShow = getJDialogRasterSettings(reference);
			break;
		case N5:
			dialogToShow = getJDialogN5Settings(reference);
			break;
	}
	dialogToShow.pack();
	GeneralGuiUtils.centerOnComponent(dialogToShow, reference);
	LWJDialogDecorator.decoratorFor(dialogToShow);
	dialogToShow.setVisible(true);  // modal; blocks until OK'd or closed
	return isClosedOK();
}

}
