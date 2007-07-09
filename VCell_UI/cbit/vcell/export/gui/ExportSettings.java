package cbit.vcell.export.gui;
import javax.swing.*;

import org.vcell.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.*;

import cbit.vcell.export.ExportConstants;
import cbit.vcell.export.server.*;
import cbit.vcell.simdata.DisplayPreferences;
/**
 * Insert the type's description here.
 * Creation date: (1/18/00 12:04:07 PM)
 * @author: Ion I. Moraru
 */
public class ExportSettings implements ASCIISettingsPanelListener, ImageSettingsPanelListener, MovieSettingsPanelListener, RasterSettingsPanelListener, ExportConstants, java.beans.PropertyChangeListener {
	private ASCIISettingsPanel ivjASCIISettingsPanel1 = null;
	private ImageSettingsPanel ivjImageSettingsPanel1 = null;
	private MovieSettingsPanel ivjMovieSettingsPanel1 = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldSelectedFormat = 0;
	protected transient cbit.vcell.export.gui.ExportSettingsListener fieldExportSettingsListenerEventMulticaster = null;
	private cbit.vcell.export.FormatSpecificSpecs fieldFormatSpecificSpecs = null;
	private int fieldSimDataType = 0;
	private JPanel ivjJDialogContentPane = null;
	private JPanel ivjJDialogContentPane1 = null;
	private JPanel ivjJDialogContentPane2 = null;
	private JDialog ivjJDialogASCIISettings = null;
	private JDialog ivjJDialogImageSettings = null;
	private JDialog ivjJDialogMovieSettings = null;
	private DisplayPreferences[] fieldDisplayPreferences = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean closedOK = false;
	private JPanel ivjJDialogContentPane3 = null;
	private JDialog ivjJDialogRasterSettings = null;
	private RasterSettingsPanel ivjRasterSettingsPanel1 = null;

/**
 * ExportSettings constructor comment.
 */
public ExportSettings() {
	super();
	initialize();
}

/**
 * 
 * @param newListener cbit.vcell.export.ExportSettingsListener
 */
public void addExportSettingsListener(cbit.vcell.export.gui.ExportSettingsListener newListener) {
	fieldExportSettingsListenerEventMulticaster = cbit.vcell.export.gui.ExportSettingsListenerEventMulticaster.add(fieldExportSettingsListenerEventMulticaster, newListener);
	return;
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
 * connEtoC1:  (ASCIISettingsPanel1.ASCIISettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.fireSettingsOK_ActionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireSettingsOK_ActionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (ASCIISettingsPanel1.ASCIISettingsPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ExportSettings.closeCancel(Ljavax.swing.JDialog;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeCancel(getJDialogASCIISettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (RasterSettingsPanel1.rasterSettingsPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ExportSettings.closeCancel(Ljavax.swing.JDialog;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeCancel(getJDialogRasterSettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ExportSettings.exportSettings.SettingsOK_ActionPerformed(java.util.EventObject) --> ExportSettings.test(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireSettingsOK_ActionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (ImageSettingsPanel1.imageSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.fireSettingsOK_ActionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireSettingsOK_ActionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  ( (ImageSettingsPanel1,imageSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings,formatSpecificSpecs).normalResult --> ExportSettings.closeOK(Ljavax.swing.JDialog;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.closeOK(getJDialogImageSettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  ( (MovieSettingsPanel1,movieSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings,formatSpecificSpecs).normalResult --> ExportSettings.closeOK(Ljavax.swing.JDialog;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.closeOK(getJDialogMovieSettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  ( (ASCIISettingsPanel1,ASCIISettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings,formatSpecificSpecs).normalResult --> ExportSettings.closeOK(Ljavax.swing.JDialog;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6() {
	try {
		// user code begin {1}
		// user code end
		this.closeOK(getJDialogASCIISettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  ( (RasterSettingsPanel1,rasterSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings,formatSpecificSpecs).normalResult --> ExportSettings.closeOK(Ljavax.swing.JDialog;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.closeOK(getJDialogRasterSettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (ImageSettingsPanel1.imageSettingsPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ExportSettings.closeCancel(Ljavax.swing.JDialog;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeCancel(getJDialogImageSettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (MovieSettingsPanel1.movieSettingsPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ExportSettings.closeCancel(Ljavax.swing.JDialog;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeCancel(getJDialogMovieSettings());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (RasterSettingsPanel1.rasterSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.formatSpecificSpecs)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setFormatSpecificSpecs(getRasterSettingsPanel1().getRasterSpecs());
		connEtoC7();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (ASCIISettingsPanel1.ASCIISettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.formatSpecificSpecs)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setFormatSpecificSpecs(getASCIISettingsPanel1().getAsciiSpecs());
		connEtoC6();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (ImageSettingsPanel1.imageSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.formatSpecificSpecs)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setFormatSpecificSpecs(getImageSettingsPanel1().getImageSpecs());
		connEtoC4();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (MovieSettingsPanel1.movieSettingsPanel.JButtonOKAction_actionPerformed(java.util.EventObject) --> ExportSettings.formatSpecificSpecs)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setFormatSpecificSpecs(getMovieSettingsPanel1().getMovieSpecs());
		connEtoC5();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (ExportSettings.displayPreferences <--> MovieSettingsPanel1.displayPreferences)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setDisplayPreferences(getMovieSettingsPanel1().getDisplayPreferences());
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
 * connPtoP1SetTarget:  (ExportSettings.autoMode <--> ImageSettingsPanel1.autoMode)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			getMovieSettingsPanel1().setDisplayPreferences(this.getDisplayPreferences());
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
 * connPtoP2SetSource:  (ExportSettings.displayPreferences <--> ImageSettingsPanel1.displayPreferences)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setDisplayPreferences(getImageSettingsPanel1().getDisplayPreferences());
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
 * connPtoP2SetTarget:  (ExportSettings.colorModeString <--> ImageSettingsPanel1.colorModeString)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			getImageSettingsPanel1().setDisplayPreferences(this.getDisplayPreferences());
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
 * connPtoP5SetTarget:  (ExportSettings.selectedFormat <--> ImageSettingsPanel1.exportFormat)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		getImageSettingsPanel1().setExportFormat(this.getSelectedFormat());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
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
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireSettingsOK_ActionPerformed(java.util.EventObject newEvent) {
	if (fieldExportSettingsListenerEventMulticaster == null) {
		return;
	};
	fieldExportSettingsListenerEventMulticaster.SettingsOK_ActionPerformed(newEvent);
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


/**
 * Gets the displayPreferences property (cbit.vcell.simdata.gui.DisplayPreferences[]) value.
 * @return The displayPreferences property value.
 * @see #setDisplayPreferences
 */
public DisplayPreferences[] getDisplayPreferences() {
	return fieldDisplayPreferences;
}


/**
 * Gets the formatSpecificSpecs property (cbit.vcell.export.server.FormatSpecificSpecs) value.
 * @return The formatSpecificSpecs property value.
 * @see #setFormatSpecificSpecs
 */
public cbit.vcell.export.FormatSpecificSpecs getFormatSpecificSpecs() {
	return fieldFormatSpecificSpecs;
}


/**
 * Return the ImageSettingsPanel1 property value.
 * @return cbit.vcell.export.ImageSettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImageSettingsPanel getImageSettingsPanel1() {
	if (ivjImageSettingsPanel1 == null) {
		try {
			ivjImageSettingsPanel1 = new cbit.vcell.export.gui.ImageSettingsPanel();
			ivjImageSettingsPanel1.setName("ImageSettingsPanel1");
			ivjImageSettingsPanel1.setPreferredSize(new java.awt.Dimension(240, 343));
			ivjImageSettingsPanel1.setMinimumSize(new java.awt.Dimension(240, 343));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImageSettingsPanel1;
}


/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogASCIISettings() {
	if (ivjJDialogASCIISettings == null) {
		try {
			ivjJDialogASCIISettings = new javax.swing.JDialog();
			ivjJDialogASCIISettings.setName("JDialogASCIISettings");
			ivjJDialogASCIISettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogASCIISettings.setBounds(123, 502, 176, 183);
			ivjJDialogASCIISettings.setModal(true);
			ivjJDialogASCIISettings.setTitle("ASCII Settings");
			getJDialogASCIISettings().setContentPane(getJDialogContentPane());
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
 * Return the JDialogContentPane1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane1() {
	if (ivjJDialogContentPane1 == null) {
		try {
			ivjJDialogContentPane1 = new javax.swing.JPanel();
			ivjJDialogContentPane1.setName("JDialogContentPane1");
			ivjJDialogContentPane1.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane1().add(getImageSettingsPanel1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane1;
}


/**
 * Return the JDialogContentPane2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane2() {
	if (ivjJDialogContentPane2 == null) {
		try {
			ivjJDialogContentPane2 = new javax.swing.JPanel();
			ivjJDialogContentPane2.setName("JDialogContentPane2");
			ivjJDialogContentPane2.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane2().add(getMovieSettingsPanel1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane2;
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
 * Return the JDialog2 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogImageSettings() {
	if (ivjJDialogImageSettings == null) {
		try {
			ivjJDialogImageSettings = new javax.swing.JDialog();
			ivjJDialogImageSettings.setName("JDialogImageSettings");
			ivjJDialogImageSettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogImageSettings.setBounds(117, 84, 244, 359);
			ivjJDialogImageSettings.setModal(true);
			ivjJDialogImageSettings.setTitle("Image Settings");
			getJDialogImageSettings().setContentPane(getJDialogContentPane1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogImageSettings;
}

/**
 * Return the JDialog3 property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogMovieSettings() {
	if (ivjJDialogMovieSettings == null) {
		try {
			ivjJDialogMovieSettings = new javax.swing.JDialog();
			ivjJDialogMovieSettings.setName("JDialogMovieSettings");
			ivjJDialogMovieSettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogMovieSettings.setBounds(422, 85, 234, 368);
			ivjJDialogMovieSettings.setModal(true);
			ivjJDialogMovieSettings.setTitle("Movie Settings");
			getJDialogMovieSettings().setContentPane(getJDialogContentPane2());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogMovieSettings;
}

/**
 * Return the JDialogRasterSettings property value.
 * @return javax.swing.JDialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JDialog getJDialogRasterSettings() {
	if (ivjJDialogRasterSettings == null) {
		try {
			ivjJDialogRasterSettings = new javax.swing.JDialog();
			ivjJDialogRasterSettings.setName("JDialogRasterSettings");
			ivjJDialogRasterSettings.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjJDialogRasterSettings.setBounds(393, 536, 180, 214);
			ivjJDialogRasterSettings.setModal(true);
			ivjJDialogRasterSettings.setTitle("Raster Settings");
			getJDialogRasterSettings().setContentPane(getJDialogContentPane3());
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
private MovieSettingsPanel getMovieSettingsPanel1() {
	if (ivjMovieSettingsPanel1 == null) {
		try {
			ivjMovieSettingsPanel1 = new cbit.vcell.export.gui.MovieSettingsPanel();
			ivjMovieSettingsPanel1.setName("MovieSettingsPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMovieSettingsPanel1;
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
public int getSelectedFormat() {
	return fieldSelectedFormat;
}


/**
 * Gets the simDataType property (int) value.
 * @return The simDataType property value.
 * @see #setSimDataType
 */
public int getSimDataType() {
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
	getMovieSettingsPanel1().addMovieSettingsPanelListener(this);
	getImageSettingsPanel1().addImageSettingsPanelListener(this);
	getMovieSettingsPanel1().addPropertyChangeListener(this);
	getImageSettingsPanel1().addPropertyChangeListener(this);
	getRasterSettingsPanel1().addRasterSettingsPanelListener(this);
	connPtoP7SetTarget();
	connPtoP5SetTarget();
	connPtoP1SetTarget();
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
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getImageSettingsPanel1()) 
		connEtoC8(newEvent);
	if (newEvent.getSource() == getMovieSettingsPanel1()) 
		connEtoC9(newEvent);
	if (newEvent.getSource() == getASCIISettingsPanel1()) 
		connEtoC10(newEvent);
	if (newEvent.getSource() == getRasterSettingsPanel1()) 
		connEtoC11(newEvent);
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the ASCIISettingsPanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getASCIISettingsPanel1()) 
		connEtoM4(newEvent);
	if (newEvent.getSource() == getMovieSettingsPanel1()) 
		connEtoM6(newEvent);
	if (newEvent.getSource() == getImageSettingsPanel1()) 
		connEtoC3(newEvent);
	if (newEvent.getSource() == getMovieSettingsPanel1()) 
		connEtoC2(newEvent);
	if (newEvent.getSource() == getASCIISettingsPanel1()) 
		connEtoC1(newEvent);
	if (newEvent.getSource() == getImageSettingsPanel1()) 
		connEtoM5(newEvent);
	if (newEvent.getSource() == getRasterSettingsPanel1()) 
		connEtoM1(newEvent);
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
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
	if (evt.getSource() == this && (evt.getPropertyName().equals("selectedFormat"))) 
		connPtoP5SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("displayPreferences"))) 
		connPtoP1SetTarget();
	if (evt.getSource() == getMovieSettingsPanel1() && (evt.getPropertyName().equals("displayPreferences"))) 
		connPtoP1SetSource();
	if (evt.getSource() == this && (evt.getPropertyName().equals("displayPreferences"))) 
		connPtoP2SetTarget();
	if (evt.getSource() == getImageSettingsPanel1() && (evt.getPropertyName().equals("displayPreferences"))) 
		connPtoP2SetSource();
	// user code begin {2}
	// user code end
}

/**
 * 
 * @param newListener cbit.vcell.export.ExportSettingsListener
 */
public void removeExportSettingsListener(cbit.vcell.export.gui.ExportSettingsListener newListener) {
	fieldExportSettingsListenerEventMulticaster = cbit.vcell.export.gui.ExportSettingsListenerEventMulticaster.remove(fieldExportSettingsListenerEventMulticaster, newListener);
	return;
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
 * @see #getDisplayPreferences
 */
public void setDisplayPreferences(DisplayPreferences[] displayPreferences) {
	DisplayPreferences[] oldValue = fieldDisplayPreferences;
	fieldDisplayPreferences = displayPreferences;
	firePropertyChange("displayPreferences", oldValue, displayPreferences);
}


/**
 * Sets the formatSpecificSpecs property (cbit.vcell.export.server.FormatSpecificSpecs) value.
 * @param formatSpecificSpecs The new value for the property.
 * @see #getFormatSpecificSpecs
 */
public void setFormatSpecificSpecs(cbit.vcell.export.FormatSpecificSpecs formatSpecificSpecs) {
	cbit.vcell.export.FormatSpecificSpecs oldValue = fieldFormatSpecificSpecs;
	fieldFormatSpecificSpecs = formatSpecificSpecs;
	firePropertyChange("formatSpecificSpecs", oldValue, formatSpecificSpecs);
}


/**
 * Sets the selectedFormat property (int) value.
 * @param selectedFormat The new value for the property.
 * @see #getSelectedFormat
 */
public void setSelectedFormat(int selectedFormat) {
	int oldValue = fieldSelectedFormat;
	fieldSelectedFormat = selectedFormat;
	firePropertyChange("selectedFormat", new Integer(oldValue), new Integer(selectedFormat));
}


/**
 * Sets the simDataType property (int) value.
 * @param simDataType The new value for the property.
 * @see #getSimDataType
 */
public void setSimDataType(int simDataType) {
	int oldValue = fieldSimDataType;
	fieldSimDataType = simDataType;
	firePropertyChange("simDataType", new Integer(oldValue), new Integer(simDataType));
}


/**
 * Comment
 */
public boolean showFormatSpecificDialog(Component reference) {
	JDialog dialogToShow = null;
	setClosedOK(false);
	switch (getSelectedFormat()) {
		case FORMAT_CSV:
			dialogToShow = getJDialogASCIISettings();
			break;
		case FORMAT_QUICKTIME:
			dialogToShow = getJDialogMovieSettings();
			break;
		case FORMAT_GIF:
		case FORMAT_ANIMATED_GIF:
			dialogToShow = getJDialogImageSettings();
			break;
		case FORMAT_NRRD:
			dialogToShow = getJDialogRasterSettings();
			break;
	}
	dialogToShow.pack();
	BeanUtils.centerOnComponent(dialogToShow, reference);
	dialogToShow.show();  // modal; blocks until OK'd or closed
	return isClosedOK();
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G440171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD4D455963015A6A69AAF2D70E56F11D9D212E159130A0CFAE6E4E418DAD4D414D6DA2474A2237275AE08028E9AA5A6A5AA7E039A012022212122C2227214A6AADEBA4285C687E61C997E84745B671C3B4F5C39F30701C9DA5F375647DAFB5DB97B6CFD766FBD675CF34F3988A9EBFC9644462588C26C82C17A5B95AB88E78688424D4652C644F9B5E8B5026CEFAD40974227E763616A83F5291FEA
	B549428BFB9321BC864AC51FE8B5AB6077B2A1FCEF6F7E7043091CCA280BFFBB292AF3B9BB77221C84DABEE9CC026BD200G4011EB3C1148BFE7CC157061929EA1B6CE9046D31ABE46B489D78C65B3C6C6B7D83A7A41F591155CE7CF3EC4D77997DFA1FA22B9EE89E9C756A6C2120971E6CB57F4611358B09311B50A6AC9F86A206C83C00C1FAF74DA33892E85E567EB77D51D55DB331A2AC403A1CBECB1994D56BDFB8E516BA951EA55B754DA84217A82E96BDD5AD69A7C4E83A885E0F83FB80277055FC1GE197
	D07F3D1B8BA47DAA5A468AB3EA1FD87439937D23AEB03DD34DB67D83361C1D7FB575F553BE6253F228EB817084708788850882D8C9E35C31F78BDC771F359EB456579B9B761BCC878C5566E346EA51G3F776C019A89F7D22CB21A2B850145F10CF11B0ABE1D8163FB3C5BFC4CA73E3998D33F132304707BEF0ACBD071C938EFDBDC22C57205456613CA8B7177E99371F7A17C0E155AE5F883457B4870AC875E27F1A365C028CB37E2FCAA1622AE25714304641C1BD5F571FE5CB32E371569B0542A5425AC1E349D
	ECE5B2FF603A34D0FCD9238D4FF4390F668AADC35D54E62D06453838105C93212DDACDA4C07CC52DA68D20G208240F09163BCE16FF7DD0CF3194EECBD5028B7D40BE60171DE5E7795DC45962BD9D752E0316A9A2AC42D4E2C571DB50810630C46E39F7141D9BB3E81B47F2AC0469131C1B46B2CFAE383342E2B972D225942E8EE5A778521B10B3436DCDFAF0200FA13006D7EED5FB2389AF4966BCF26EA1DD534D1307ABB771118B4693450A2B4G3F19DF9A7793FFF9027D438146F0BF2C7D0134F7D4B4B70806
	BDFBCA0D46730D26F2E209141F22A919448568E782A436E9B599ED98775995987722A577885B265D289AF72F6737A7143631F8D73759624E5AB75376BD816F8FB095A0268D7B460F0C955D38AFD0B7AC93FD6575410AB21311EC6A0E0C84A97F313CC0D1BFDFD1E63E1FF90076F76DDACDBEC0C5BB7A3E7207F4160387F49691336E049992C62CAE52D711845019DB191E5A78F6386E8658C8F985E13914932BGC8FF2811744B81D88330C212CF727C90C02E40462075G47GB683E482EC85789660A040AFGC7
	GF6G6C825081FC8F30177B6B92554384B6F88384814C84C86EG5BG4EGF8G788304818817A0A6GE5GADGBEG41GD1G2917351AC200BAGAF00G00880044DEDACDB6C085C0AFGDF00D0009800742BB0B7826222E29DE333D47243D249A7CBE53EC2FF204F56487C31D1459F5FC93E4C1579E3374C9FA87720645FA3B25CB1A779730B222C6BC66E7DFF409C5CB5DF997BE552758BC56C71FE4538135CA0F31F953238AFD009BB498F12B3EBE5F11FAF457D8719DC3C57F648F01B1D681E2BA8FF7FFF
	01BF7FDF8256E71EACA431951B4486EB11E8929B2A6107C52A7B251044DA9A17EB0C1C67B4451BE09CBF888327056345C212AFBA2B552CBF5BE895ED53D4293E1E4A32B41EB55926GA84BDAC87246588C63B827EFAD7CD2A063FBC303C8BBEF4EA2553751B1D8B408752074C1E3E30315612F5ACF1E894E0B2D529C204F7E058299438D0DE4BEB11F627E7BABBCF088BF9669F586E36D7ED387CBCAD0DDD65F7F97D9FDC93D2ED6342F7F65142C7E1831C92F28FFA6F3392D7EA4CCAFC4334C9FEC9CFB67A0513F
	987A60CAG6BD5B80E459EA075B80710863003C6030139816AA22EA2F336BBCD78CCC34093F06459E68CDC0381984D1ED6B51A0C2BE18EF7B552F8DED023A9077ABACE73FB1B9ACDAF289FFCB56ABD7BG19FBD8448B0DA2CC61482467A80F4107873ED454E925F8307AFF9FA0FD09C1B6DFCA3D0648FDA69E1F497C2EE6B24713AB00G752B814F687E3EG692A2FE1F46DF075868814682AFB93BA73C2241BFA8DEBA7D222AF6A4D66B35114CE1B00F47E3D59389C5C1B510743B50B37171C08F405FDE00C85E8
	6F2356DEA4271B5A070D6F24BDC29F59073597FDAD0D69A2240B3F16691F86D7AB40D00088GC65731014B6DEB13DB511751756A0BF4451B11EEF0DF1B3DFE925DD438B27FAD5B02F4B37BB27D858F56EE3A075C4F33B8DD21070D0E68D961A17773582DC857A255FBF92879E550B6AE57034DED08DF883D50CF6E17E35FA2DDEABFC6175D0F35D75CCF5E5E9CCEE76D4766C81E7D58DCF828DDFB0FE4713C6A4762215E5E696F10EEFCBFB6578A6F476655766DE5E6A3DDE4BF16AF6A6DBD4D69622FE3F336B438
	FA4A5AE37E3BB1876936C1FD91C05DF52C3D206B2476285C566DC897F19D1B7B294BDD190BF4B1573179E1F2FF76EC105EDF3203524D62F4597DD9FBA49EC4FEF1FF393D3767A15D197EACDEAD7D195C297D65F29BF6F0FBBD59DCB44D1345E31B273C3D021DC8D76049722E5CD32D3D04DD7C3E74E4F35AE0CF960FB03B7626F13A191ED2FFB1C02DBD1FFC242B9C4066465681AC9E766D1D5B0DF43D87B0FF8CD5ED6F7BC35C4F83589CBBF9A073B336477A3B28030936318B7B48030B61DA23B7DB2CA5B09037
	A06D278749F8CB1ED1A53440560ACEDEA06384DDABBA27EB5235E4D91AE1FC5933C79AF5202EE2A06D23AE90D9240D740138B674CF8B99FB06C2F98CC048C0FC861D3C845706861F779542E765A4B8DB5B1AB110591DF83D6D99B4A4EA7FC2A6535C0AB20B2FC7199F3693193D206C8D608F40E847F37D02B96D6BD4BF418B6E9100ECAF243D290D376B05346FF650F6216C8D608F4056B46652358E622BE04089FF51EA58587C3F85E42C31686B0BF4D6DDF92BC9144653194D72B1AE9F68CDG0381FCB9EFDF87
	DE6637FF6DAF1478C2C68D938292BC635456546AC6F847857D0579EB66DFE4FE3B6D6C7C88G26F7708254BB7F86506186147D5DC2C2EF9149BCC02CBEECB4576B2CB897B8AD48F577851E880078C1603FC1487F6BC2BADF22EB080C1B493B056289CDB65056F17A380492385ACD0E4AF4B0A7497A71C02355EAECB8F1F4BF1D101C5651CB19E82E01D6456A8C120DD90DD63DA16B9019D81EB8FB8EB44E94F06ED510C01EAFEA288627CCE2153EC6DFC52E964917772C167BB2D157C755171E33EA63FC86B19F86
	8F327932F85059791503501717935036644150078D46F55614FD64CA6704DC2F1884F27F69ACD5FABDD3C752277D975427D74898A1F559GD5FDE87DE026076FE01BBEA990578C9E5B5D0928CF8C50E45F08732B23F639D787F89F0071GE1B7E2BCFC92C9BC6A65134DBF988F265372C528D394484A3889658DDC34184BE37EE0744FF27AB3C06B4D69F7AEA67A69651365BF289FDB177FFD31F2DDBE94E44E384936AE4FE2BA782BFC56FFF271A70E1E5E2F10D52AAA6C7BCCAD6BDFE2B7A4CA364D3B897B44E1
	4B6479D8F079F75578FBFCDE9717F7B3532972E6DB7CA3EEB8BBBF7A860C7F1D9B50F73EC0938140ECDC33D1E9E32AB70C115EB69B19FEAFECC27D0A3C311F3B30995BEC875C98EFEC731D4D4AB6A321CE9822ECF3D8A63619BA845BAC5802ED16814EE388467876ADE49EDF2D37188C3A56B233D8834FA6B021674FD54FBA2DE76DFE39955B8D02B6E3F83B37EE6D1CAF0273E57F957A283FA25F11EDD7BA67183C40EDA84F87E405F1F9BB3EDD4C65496F0926EF314FCE3E857A19DBB0A7777D17609BC8DFE7B8
	A9DA9A8DD606CFDCCE56B92A8CC60BF862A8CBCAE97C4E1206EFC8C9296D54AC542597349BF08B6AF2B90BBC676A4C35B92CBC02162DF5FA8B63FBAB8BF50A821E0C2198DB0F6F787D8BB86F21585E1C6F30BDC6F7B29B79C301A671D6640F4EC67EC240355C0A7C3F6558730F4BC1BAFF2019F9AB76F53F464A7B3A743FC19E823483F87D8D5B5A387DC7381A6D9E1C7FD8E0D93B2737BB9BFB184E4336234D3E20CB445F506622DC34A5756FDACD595FD14F113976B11812EB9F03AF73104F83F802B8DF7FBC0D
	4DBE197C10BC149F833459BEC81F3B63D827F9FE103CB5B1F4B757D9AE1EBF319EF311B563A29FEF5E01B65501FEBEDC474FF6587BE253CEFBDF08BB5136B060C93E8D79C2F69E6BF4DCF34FB6A6B3FB976ADA8C72FAF119B76F32577551DD763AFE368BF58DG1EA84EA76693DD1D4F097E082ECF65232E9943203F9E06B233F75B5FD703F623EE5EC0934A69BE59FD3C73FB46AD65184C6AC328DB626DDACD616DA87369C3289BDBCBDEFE013CFFED395D7E5D901F33C9F89F69BA66F69CE3FF96646FDF05D95B
	9308FD4AB1B66D796D894192EF786D4AF71E3B2CC447F93733F71BC4BF065FCF71C943EDF8495F9634A98D6AFA8D47F71E7FBECC7A2799D0966E00F198A09B208C40F4872EF5DDD3C4561FBBFD5FD952GF1E85099CE59ADF8CAEB6E4D2D44CF1EF7287B49C7420F112E41F0E536686933CE789DCA9BAD949FF9075246CF5B5146F9D0F766CEFC5F73940D5378BB556507C978C869AA0CC079BFD1DF260CD04ABFCD719BC6A8653F4DFD1C87F55EA3D07EADEDC47E3C916A7213FD99FE03F44D77C57927AF1176CA
	FD15727594DF692B147FFE874AB7C0DD28AF4A1F976FEA1FC05EDDB0AE5DE5530F71ADDE600A2F9DF8864B78D89FBB6EB4598761ECDF07E3FF024FF5ADD679D8B97CAE36B6918C57703BF07E165E027337683B701938EF8B31FB5B5DDACD91C0655D185B3F9513B921954ADEG3EG6381E682440F04B942C84C6FA9C587DC6577FEDC68AFB7721F87150B756837C9AE7D266E6FF017FC05A3557CFD3831B3FF17B80C23686F3BBB147EA6EBD595A043B0927DFD3310A86957FBA47A7BC53A47A3904AD381A2C7E2
	1E5D3630ABF64B4B0C6FDE17FC71FE76FCAC3E21A5A43EE9D0178FD086E0G7082708388754378EEBC743D2B7852E1GE2CB436998D826E71F84776C9B6412AFCA553E3223443E44FB341A8DG85G95G6DG5EG017720FD4D07DC66AF5FAA52157C7DA151BDBB0F39641BFA0FBD9F4B5FF796F5163F479C66CA183FB7B97497E4EDB492E48823B0FFCBAE72673DD1183FA3AF52FDC1D02E8368B58A73F765A2776C4EF44967BDCA0DEF1A1B72263B6473D748136E6545680BE06E0B390B695A633DB07683E45F
	0B3ED8380148A83ED7FD4CD2831633CF14121C2D843E96GAFGBF00D000C800E4FF4C59090776F6711EBC8838E223713CCA3ACA7E705B601E9F9FF249176D2F66470A0D6847E2FF7463580DF4DD964A5EG7E7E684770CDC4C630FF577DA84D19B63BE7D39F17FC612AB62D4F6063AA37696A8CBA2E0E06F19520F2B45AB4A21348300E6E2ECD873638E7D319CBBE0F51EAFC29DB5D133749A51F0F2A3C375C14976D12AFC0D55E635B5C13976A12AFD4D5DE7B37CA3E198ABAF5F957B970E5A94A717729712D4E
	F24F3EEDAE7952D465C5BA70F54D3ECF9C78147665294A9B781DFB768DF549D72AAA2FA25BBD7BEA9C781476D52B4A7B44012FEB76A56638FC6ED015B75EA51F3A3C67DC728DD648E373961F4DE45F1733F94BC9473589E95E62F3C9B9EFA16FCC7DC0C650FDB8EF39A1967B63307B30BF7EB8167447497743338BC0617D589F7BEEF7AF2E7FF060D3463562FEB53E9FF35D73F305CBBE132A3C54BCB77BF117FC9E2A721EF3606B1ABFA39C78147E74D115F76B8E776C9B6912AFC0D55E791D6E59576A402734AF
	D4D55E4EDD6E59D76412EF262A3C78FC776C7B5201CFE91FB0C60D6FD9873E2E59770ACB3ED4D5F9C3F63BE75F8887BE25FD592A724E9DF24F3E9617FC458AF95EE3C8BF370D6E11B8833FFB83B05CB594978E3F23B9AE4EC2F0298FE8B55B9EC05CD10AAB04F27983388E3460B8ED8F4AAD8F6073C752E1728E8E779D5AF64C236EFB2DE42D5773816CE30F50B259A3A1455C521D3D7FDD81A24B5EFFB32F388C799983660705C71C6F09DDF2C4FDCF6C32A36AFBE23FBE728543C39B7213AFD83F76C867FBE6B3
	0F286D19E5F19D586A1893E3DBB1A77CF87C43AEE2CE04F25C556D1893B3F8BC0D9DA41ED13C5CCA4BE49F89AB47F6DCA95FC7BDE01F633678FEF345E4A84F0E586E258815FBE911997D167C8F44ED6177D29E47B570FB29789F68175F68FDD04E4BA2AD137D9B2C7C5E954BD9A2C35DEF8B2DBD6F377B685EABAB7185774722F61EEFB63FF590DCA814E7F01C0562E2201CB4167B699271D3AAAF9BE9193C8B164EABB96482213DD2773DF4DE6912D2061A7D6E42AA5A5E5BD632975CE19F7BF1DD3D48E926B710
	B394D61D39D6E40BBCA71AC433D9DFADD2AA699D7002C5D2BBE5D6E359AE4E3B083E03EBB0B608EC8E7EC1A619CB2B6C8B2493EEB6172EE9A47120FB4C4662DCBA37194C25CDD076G7061F159585259189520929376CE66D37A8EA2E4ACFBA7934B71712D849FA64333F7B583A8BDA94F003A62G9C1744E2677D3121D83DBFB69713FD0B96E32339CAD476392D45293C4E3602D6E2956B0933994D55141F45C3495F0F7233BAE77CB79413777BE77555FA3334EB1F45F041C2F98C49B997960FB7BAB09EFE8198
	0F3BAF93FF4C0072BC001487B19EC3850C4736879DFDDF8838F2851E797D588552DE5D034AF8E8A95E7A20B29E2F19B09E3D20EE4603784EB70C5BF048EABC16477D72D20973B83DDE229E2737CB1C47E9F6096BB8FDD462BCCE1F15380ED3420F4E46DFE6EBE0028663D5E3B6561765C8ED7FFA54392D3A236A36569EF5EEEB43D15736B69FF5EE6B25232EED6DFBD4BDA71B9265715C49F3F21295F33278A14C491B1A681AAE143D817C9F421CBCFE3133BEA278A1E7FD441AB67ABE7BA1E5CEE6D07C4C0714B9
	B97AA266E4B454D5BC0CB9F9F211FD1CF2A55B12964BECBBC85E59CB7750E2E77A86BE2CBE46B03D4EE9087C500715E73C1BA9BE72E165996FF974DD82A9A786EAB506C09C8FD751FD0621D00E8844FE6E59D267B975FC29FACE0517BA4F2969252EF3AA3254F9CEFDD06ABA276615F6FE7FCC5FA0777FF66E7F7FEDF4667F28404E7C3FF59371E77CA3CA7F67D3FC7AA3CA7FBF32917DDFGF5BE0F207FBF207BA86347C13FBA8EF3E854E6392EB9DC570C8CE73AEE9857192EF3693B027CF1CADD67E792FC79B8
	252E9E99282B896A260EC3DD47517D19790F421878A86A5A3AC5BE0E64F0FF1F58AA47EF6778D55B6477788E0E7FF81B7DFD33DD1AD3BC759549C593BC4A0896BA9FA17B5DA51E7B3F157BA90F3775D316FDDBF992FE7DF7F2FA1B6CA73E33275FA1612F4F16536F6474B9B97674BBA57C27B9F25B08AFD87F95F6897B2F0AC7317F3AC3A0BE760232AFC0502358FF5D9C5BD97F957628337E2B1EFEB7A172D1E57F55C471B10FAA7B2F7F44E27F959F24555485E1BCDF5BAE37F99737792ADCFB1BF7E11C736C71
	7992FE518EF9BB793C1D29BB6C69F7CB78E1BB65743BB9FDBD5D7FA69B7732997E7B7C4572FBA49B69976ED650CB7B6AC2F75B5167A07DED0794745B997E6085B2D72E96EBF40D86F814A65BE3CD52279D084F9E237B41AFE0DCC302F07FD873FA5CBF366162DDC250E9D31C5A7EB16FBB2E0FABFECC5A237D18ED7F981BB70FEEA6BE2AB16B6A218BA31BB2056447093C3F7273116D406385608F908A907598466FEB8BE9235ED82DB3F0BD33AD38DEDD71B8521D25F4D63D55C0667EF50F13EF5AAC7909D8407A
	53454709DF9C4F34A69F171F9712E237062C57C85BB321033652B50686E81869BD091F7D7403F290C0841177CF2DA69D0069985B06BA96820E69F3C8AB70BCA8383CCCD5C60EA4A35D0CB24A003F9A20B740F00071DC46A96E0799DCC65D4FB699D55F2B4B58AE55BF5566BAA7E634E1CEC4038CC937F654ED5BF82D2690608CC0AFG1F71980F7D9D0EF12B68C0FD03B85DF59D0EF1732F3059B065D7F59BF2F1FD39D1593F040E3775A9B346BB6BDFC650E73218714A7DEE23A9BE718965FE375D4D385FAD956A
	5A1F40B12420851F53A31E606BB92DC4FFE9DF1B6C772016AB3D46A687E9FF421FA0636D8E254F13A6587C1CBE41194FD753B1A2EF0252679994DFB4C169F37F4B6873F2288B1800BE3F1F6E93CBFBD22B49FF92F1E9ED9807D20EAB233896A8FBBD0931B9F541960FCA596FB73B75FD95777C76591FA0E30AD5991B61CF5A629170243358AC26799D7224B2B6A9949F7E24B2B6C31BB0B611D0D7960CBE97AFE29C468473F50EB61B1F33E43F47DE6CF9FF0476288CA99F69DE278830B7065BEC5210B37B29CFC1
	0E82D4G34BC05F59D344E9B4A01G91CF497962B45D511734A9AF27BE65985F8AC9871BCFD6F6CB46A2C5D9ED9D0F343BC0D63FCC485B007B5733A7929F7DC84B7193B1CF2C9B51E75609E8FB4EA667BE5B37C969B39B5FBFB7763C4F423BA543BD1FAD5EEC6F33744D76BE7BA183FDB6185BBEAF43394F96E5AAFDE6639B185173BE9B51AD996E79ECDA263D4F5E4DC41FC96FBDAFA97B43B193ED3A87CFF4569FFECD1FA742A7AA7B4375949FB9D1599F0E9430BF1C87F5E71E46180D0A457EF07C532813A5CE
	60BEEA177DDE9057737D616A9E1541E252C84FC0480FBA7D2CEF28B6B643740DFC054EEA49AAAAB9D5F662542153A70E1FBED88ABF583306CF337C7B0F790B2FD6FD56A09075B40BDBFC883BFA7EE7C8ACFB7FBA7C97626F82401706607A4FF547655F0E5BEF31982B74EC164B1EE70368F38F11F386F83CC3882E541C22304B984B5EE1337ACBF675E42D663D7972FAFF3BFAE21F4E0E7F8D3BFAE267C7822BE73EF8FAA3FEAFE05E537E0261015ECE3F0599A179A07E996939EB5210580CA1E58B25F31C96FA7E
	4A5CA832F61F7DCD6E63643F9A62E21D34DBA035D77C0C6DF90E79AB3A957D557E8C7AEB5DC5253F1E0D135BDBFED1692F3B84F97D3597157E1AE5672F000BCAFF3DAB71B3BBAF35231DCC4743E654F18C57516A9053576DF43C53A126A36DF48CF30869B4BB9D97DB14BA9E1BAF57719C0F05343E52565D7BC4322D9DED0B3459562E346D9EBB5D6EECD75AC68E154AECEBD75A2637372DC3E95BB2BB5B58BBEEE21B749E1C3E875BB0894604C9589746B6E3DFD8B9897B42FB1A89DFAFDE9ECB4B643BA2527900
	661E6A2FECB2ABFACC86714D18C974D93DA53D93BA1FC96C9EE3FE7A2C83FDB70F7B2E65B27AAE7DD954FB03C0FCD5444BD934CC3E99A36D393B5C19DD046F0F58A66DCD69D4469FFD674BF2EBA27D961E099EDEB70B5527749749DA81DBD318CE1F852B419693G73F5EEDBD7FC4D5A2EEDA3EBD3757A86FDFDE3BDED1935FB19BEBB8E05B6FD1F45983CE34538CC65386AA60CCB8C0FC3B24D6134D0ACFFC34B645BBB2CBC2C29C22A6F091CE3B29E26B22E64BBF7B9B0BF37DF6C0A1F599A203FCEC3E3282F57
	19DB49069D61CCA8E9E362E2F93FD2904A3E49E50B4F2B97697A4FE1F224486EB41FD49FA32B37BBFDA655FF59E62B37BB5DA369755081D22F170E14164974E37BE7C7EB6C3E7F92AAFD1B882EB3C3715C7D5095EAB442E42DA6E5B2520CDD25C6338D6A8BA763DA58198D645B858DE2F33A6A19618CF20ABAD31A9F6C956563F8F6DBE99CE9C3ED9C2F184C7CD9B759B6D7E23E6F3FD169FB721DB3721B55EBB31475FEFD657DD4B4FD8F608D6D7ACD667BD1843C9FC2A7E33E2F09A5799F4D4B1934CC3E75444A
	23856CC322A7DF79FB02491848E57444BD416E07A7E2BB3B9FD21EE33631F9EA5DEF44CF9900ABFA8E67299FD610BD5EDA5DB969045221EA3D55E826C714GD9E6B61AC4333515E169D77E24356D3BFEA1B61D97DB2572BD3F483F41436CDFEFF635BFD289AC4F9ECB40F96818076E96822EAD0A53B84933EA493E5E52B5A0E4C82C4D5E67AAC97E0F013AD000E80024A9B0168164CFC17BF3FEA5BA1253D8E062FE2BD5D7D5C7D765711016D6AFB65B5064C582EB7B437F91CD25F7793FEEF466C3E861C7382F58
	4DD42E2B653E7938353B3EE1FCC73B6DD3092F3D3BFC2CFF9852C83E37E950359A9B2D605FA974B923917B15D2A8D783F88278CCE15F589226921AD43BEFA029373D370174030E6725F324FAD38B11FDD0A49DA566C633083911B672F6A1EDCDE15C97CEF223F8AA4B896BD45B33844D91EA43E6FEC641F7AA33A1882ED32732EFEDB19B2E3F6C5A068B19448647B348B952FB05485382277D024E91F8DF2BEB36E69569A12D484748B33667E5EFCB4F4D4B4DE4FC1F6BECFC01262E70B13B167158C4CAF645CF53
	EAD22731EFD0B13B5A1ADC5B75947D360A6359549CE98C9377136F3C73FDE9D9869D591546729CE355F1D87E7D39404FBB1C5E475953D80C4A2659E295E0ACCFE032A6EFDF03322A0C75D9164606ACAA9337425101D6FA2E1E25A1FD9C1163B58D5F93FF19C256C902219C8190B38D4F20CCDD015F1FCBFE9EE93F7D065096C3798CC04B73C8BBE79552FAF15A62F45AAE14A3GE21E477D5809747FE5A43F00FD4C433F2E931C7731647C67A92B99CCC15FEBAEBB5EF36BF8AEEEF8014535G2EE5AF306F6D3038
	DEA7380E6B30D2B23E2A1C5D115E713E91AB3F6724EF7D7EC678793E90B68C6037FEFFA36D41D4562A57997497699287330679E2E595292F83BDFB837882848204F25F447CC6FC433EC0206CFB8FD5570A78DC0A72DE2EF43E2F660DCA577FFB605DCA728E7B2C51EAB5561376917FFEA569A72DC693C5B25C5CCA92EF333E364E4AC8A57C3CCA128B8631062259DADCD8A36E8508845BB60461B7235E4ADAE9F71F404D3BE74F81A3798EF0A98B30745D07A6FC3E2E8C437CBACE5FF78C06329F4078B0FCBF5E32
	896FAB3FBB078921EF2AFF57B1223CA3AEBC0C5DCF420B367BAAE44B59792C1DF36765FDA929F3365ED3A0711FF9513EAF154E9F0824FF72013AC000880098002417408FG79GE5AFE13C1BFE4F6864DEE00E21B1B76BE0024CFCFB62927A56G6D8CFF09FF3B61927AAC7CA574599314B665E5186782943D0C5FFC7A1E472792F003DF46B62EE944B6C2DE46B6C6D35A78F02DA68DA0AF9C6F6BCBB5A417CB21DC87508B409BA0A09CED7BD96772BBB207DABA790E4CEC8D992343203D48F047B929708A4C6DDF
	3161B3DE416F28DCCB7B3702D7146B46DE94DF7A0AF25DB8B585570DAB21AE68951C933F3802485F70AA446CD5C779258A5C19D7D17EED344F343E2A14BF1262FB3F2A141F7B8D4A9F8CF5B12F227C68D5C4FE65EBB00FF84DD1FEEF854E7BB5149FC87BE13F571472A7D0FC60EBCA794753D1FE8854E5C770B3B3E7C95FE7117AE2C67B63E54C1B41C09792414FAFDE463C09FF9D73A6085296C0399C4070BA363B3E4AF91F35254AF51F15DBC5770BC0DFC3BED24AF079D5C4D7E3CD0DC564FD635E2AE40EAB31
	1499ADFA529BB35F3E6A30E721EB7346C8B779B6B6F5170F456CF15A6775FEDD39876BE90A77FADD39872B3C8963B3946A04B770DE1DD2CB4642EAA8778270867087888508FC836372DCD5362B7B355CE8AA054099E447FDD87B5F6AC97B42F468A3G8A81AAG8CG3D27E37BE95524BFF056571DB49A692E923241847ABAF937A745B3D9F41EB7ABC457F913AE2A0FF51BC44731EE2B28BE56650872310E61F6CA6D1A79418706AF9049784166AD47F48DB0A536FD4362A707F7E2DD4B23AAB77906F61B0F6559
	EEFA8FFBCFD7665F810A9FBEDD19FFCF7331499F6AD2E6E0DF365F4D7B66041BFC9E5D66E3F6ED2477CF4B8C253DD96CBE1C2134F7AC9FC73D20AEFA86667A35B54EF3F5C00D7AFE67C1B56A676C0654F4FE4E6E369AE77B1459FA5002A4123395EFEAB56DEF629A516F946785E5DF0E3BAB89570D82B9EE62927A3E9B4A519C17321460925F02FB7DAD0C2FE78A1963E77072CD34DC4C4B2BD3FAEAFD5FA6F3D70F49184FFD99C51F118CA0536BAD74C7C9AA418D07F2A0478DCE65EBF09C77405784978D654437
	7937D53EA1FECA616551341C474B3B3F692ECD242D6E5DABA73BAD23BB6BF74CF64DEA1A0320DB7B5BFC5D1E623C206C4BF1772E66B948F121EBE88EC2B91A7B6571F462A7E1A616C3E8B91D174B53DD59C4F85D4D87A6235E250CBF324E49E414DDF62F7F7B5DCD3E1B3B4D477A47BC36FFF026325F5CCB71E5B3157D6684BE0F2C043AA09E3760A6016BB2D9767B57261E5F17E169D199521916DA67634274DA75F1E1D62D7A38B027367373559F55BA7F1FC41F55BA7ECF221DFA673A6D553B1EDF9D562B4F2F
	CA750E732B7F6A5567D7BF6B6573AB697D73E5DB9E944BFECFF6B85FFA65F3621DBFC146D6D91E6749FEBF525873321F6FD199EC8DADAD49759E77DDC93847FD43ACFC975718C4F3C43468AF527F1A0434831270D9339AE859FE7820A4DC6BEB811CA7009FC088C0A4EF6F75A54466A63DC5CFDAE3ED45AC4136125EC13A2DCB9D7743CFB8A7F0FB6E0B23E79368B22B5D16FE69FD6513A9F65FG07F6497E3FD2381E61B292D2DD7BE4DDAA7A2405731DC9D577C9CBAA5A91708E7AE4E4AA6A91823888G61DD2D
	A69D2070DD7EFD6A2F9DED7D6C3C4D56291BD5ED4DC1DB5F78C6E9AB5967F8865A37F2996BD63B36F56FEA34551373352DD637F550EA3EC675AE5ABAE1B56A918138E80014C82DA68F20BC925BFBFB0D232DEB6BED36E6AF7AC24DD681ED7DF73A52D63A87965A77084471EB0FE03BFF8F4AFE07F43AC7634A5C4BEF76288C662363C964995E7135034BEDD3927F70A492039A1D41C27C4A560E5E627733CFA46E25CAD9225CCBD5EF37976A709265DE2A8BF67B48DA16A877D2D559715F3DC439170A0C37D95C4E
	4F174A75E4EB573F27F57E5E05FD730B59741F944C6730C8DC8F1B75951973A43F2755E4G943E07F939BB750F79747AD4C71FCE667DC145FB68537F24AAFD7ACFBB1F65FC2D74E9FFC1DEDF753552279F587974462F15BEBDAF354FF47AE6A57A446BBD74C97CAA7A4E884A91GB15CA7652B7F18CF6EDE6D6813C85EEFA44FC61FFC3326737D0825EB14BE395E4EA71756A8FDB2454EA77EEB14BE9992AF77490E357C9C7AEC74491A75749CBA519140FFB676AD6FA639B7270FDF2E648B1EEDFB76891F6D789C
	44666E7FDBC174081A2D1C5357D07C3C594AB97D1C2FF0CE1FB8C72BB14CC13D1FCFF5CF6FB5AB9D4E154D111DAB1B63CC6F961AD3B1F314FADFC57171D1CA3D972FC23D13214E9A05FA3F3F5ABD3D772CD57205C559F40D0CF22677A09AF761FD255EC3A9BE69FD255E9B57235E29D0577EBEBEAF2FDCCE1F17219C4DF1E33F42676A54FF21FDD56433AC124E86596F8F3F6A7931EA690A1EBF93341376B785E0EF593F508F4F25219F2C5C8F17D659EC6FAB7B1D3C2A677D105923B2184D7F25FD0CB7586B477D
	705EBA3EBE457DB0E43D4D76E1325FDB57773C9F0AFAD4869B9336AFF75CC7D0329C67F4516007248FE04E88D0CC60AD5CC7B069AB577B880E2DF4EC3BFAA5721D7900355D8260C9E43C0DED4FCAF35DF65DDA4736AF2FC53E61D25B01G21GE48D0C359D3F3E3336257F2F359C7B101904B79A5F5367EE377B7F0B6CBD7DE10331D9FACB0F6357034B79B970E8BE2653F1408B4A3EGC151784E59F3A54A8A83DC7207FC5F49CA7EFD6D8F799818C647C0A8FB8378FF08ED0CD80BED8483CE184B77232C45B672
	67E29BFB5691DA9314BDGFC66E21EB7BB0C07DD4B51BEEE720D33638B186BFA5DD19A47289FC366BA0CE3949FB657E19C63636F8C28AB7E886D3D41E19C6D1A5EBEEE723DE8476777D1D76D9DC4E39E74114378C771A19FB90CFFE9BC1F202EA0061FE7F098FF3B267743EE72C55B717944F4555ED1B4BF4744A86D9DCB71C1B1CAFB8B57213DA1D0177DB176655368BDB878E3C6AF0DE9E4398E47B4596F64BFE1EC4F6CD19952FB985AE7785959BCA44DE667B0596F2DE9FF42785523B224F7A8F44C9EEFE773
	18F5B6BB9F117DBE3C2E67EDBE5D23B2585897391C1E5BB368EB9B48DEB80ECFC8D3472FDB27442F20F88F5D8412AF6F898B2C42D23A45B69D574B81C1772B4A91F423278C71B95BA148B1963370256366537D83DFFC416FF4D1CA1C90D7A2AC2C93496AF6093020D9DFED2DEBB1412DCBBE8C92E539A0DCA22704D8498DF5F0BFE966E579790318BED5CE4FEAD6FE4A7AA45327367DEAEC5F463F68DE9ABF4087FF0A6F2E8FD4FDABB85F3FFD2ADEE7B0D8F506737B9B2A49DE6E832DD6D1764E210A34EFC19A29
	D2FA67D0C574BEA76379BFD0CB8788A0F37AE839A7GG080DGGD0CB818294G94G88G88G440171B4A0F37AE839A7GG080DGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG73A7GGGG
**end of data**/
}
}