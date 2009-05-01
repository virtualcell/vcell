package cbit.vcell.export;
import javax.swing.*;

import org.vcell.util.BeanUtils;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.*;
import cbit.vcell.export.server.*;
import cbit.util.*;
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
	protected transient cbit.vcell.export.ExportSettingsListener fieldExportSettingsListenerEventMulticaster = null;
	private cbit.vcell.export.server.FormatSpecificSpecs fieldFormatSpecificSpecs = null;
	private int fieldSimDataType = 0;
	private JPanel ivjJDialogContentPane = null;
	private JPanel ivjJDialogContentPane1 = null;
	private JPanel ivjJDialogContentPane2 = null;
	private JDialog ivjJDialogASCIISettings = null;
	private JDialog ivjJDialogImageSettings = null;
	private JDialog ivjJDialogMovieSettings = null;
	private cbit.vcell.simdata.gui.DisplayPreferences[] fieldDisplayPreferences = null;
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
public void addExportSettingsListener(cbit.vcell.export.ExportSettingsListener newListener) {
	fieldExportSettingsListenerEventMulticaster = cbit.vcell.export.ExportSettingsListenerEventMulticaster.add(fieldExportSettingsListenerEventMulticaster, newListener);
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
			ivjASCIISettingsPanel1 = new cbit.vcell.export.ASCIISettingsPanel();
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
public cbit.vcell.simdata.gui.DisplayPreferences[] getDisplayPreferences() {
	return fieldDisplayPreferences;
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
 * Return the ImageSettingsPanel1 property value.
 * @return cbit.vcell.export.ImageSettingsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImageSettingsPanel getImageSettingsPanel1() {
	if (ivjImageSettingsPanel1 == null) {
		try {
			ivjImageSettingsPanel1 = new cbit.vcell.export.ImageSettingsPanel();
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
			ivjMovieSettingsPanel1 = new cbit.vcell.export.MovieSettingsPanel();
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
			ivjRasterSettingsPanel1 = new cbit.vcell.export.RasterSettingsPanel();
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
public void removeExportSettingsListener(cbit.vcell.export.ExportSettingsListener newListener) {
	fieldExportSettingsListenerEventMulticaster = cbit.vcell.export.ExportSettingsListenerEventMulticaster.remove(fieldExportSettingsListenerEventMulticaster, newListener);
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
public void setDisplayPreferences(cbit.vcell.simdata.gui.DisplayPreferences[] displayPreferences) {
	cbit.vcell.simdata.gui.DisplayPreferences[] oldValue = fieldDisplayPreferences;
	fieldDisplayPreferences = displayPreferences;
	firePropertyChange("displayPreferences", oldValue, displayPreferences);
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
	D0CB838494G88G88GB6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD8D5D53AB0D7319835119AB4B2264834A8C9313CB3545844F41D4939B9B7A3AB668E4D6596F5ED46BB714DB8DF4E6474ED8EA09CC5C1C545C4451F920BD41492958D81958D1314941504A3E743B9F0B867F0FE60280877DDEB6DF71DFD76590783A769793E6779F81E7759E73D6BDD6F7FDEEB6D35565E88F91BA2B3D2B5F902101AA1C8FF7BD28521798EC1F8BBE153BB08AB5B9E2F95E4FF1B81
	D688E15D69F02D013A38A762353942243EACA8B7C2F97E63715A3570FB15507EF570A87861C3CEA3545956652C69DFCEF69B4A492465115D59F0DD8E508CB8F20D5ECE64CF684E177041929EA1D5A38826A2C273F4F70104CB811A2CA2C6D7AEDD6B614A9498F120CC22EB3CB3CAD8702FB64DB242C74693E13A54B6C13A968AFFCF8D5AC1E43DC575A4EDF221DC83C00CCF9302B6FDC4E456F5349DEE39E2F46C6CEE91CD261D224BEA31B98E9CB8CA2FE7C50743E8EE338BC276470457569E42EBA670C884D8G
	4070FBCC84DF873F8DGC19F237E073617CB7AB5DC7F29B0F7CFE7FA0E8F7D43BFE6FA47FD6C56BF2673B54DBA6A6BDA837169A228AB7CA4DEDB8A50G60828887D8CFE3FC366DE3389E39622832F4F6DA4CC72C56CA135EF64A2297CD707B40812811F0E744960BCDAF88AC0E733B0BD574698FD83B3F8F3A9D73C954DE0CE97419A9C2629363B419AABEC90C3A2E09790479E256A7EE1F249793FF5FCD63B08F702992DF061FC871B545EEBC4B019D94CF4AFA281BD50C71E9D80E3A5426CF90F2CB6ED655A57C
	59511A62CF198E551FAAF5F925137036FE4AE4FE41F5F91F62974A70CC17B21AAB343CB3DE5B3BBBDE4BE23C311D5C932EBD715AB000E80019G49G425EF8ED5EDE0CF3C45B2E814639CEE7F3D4BA0DA63DE893D85BEF8C64A697DD8E1B6E04596E5019DB44F3BA1BD1F745A4C20EB31A4B866203AB9E6DDAE87E35000C6322D934699CC60B99386BBAC507E833B39A13E18521310934365E58A90200CE2B00FC9D06D5F0B5696C0EEF2CFA1DC3F4D3307ADE8309C9376E9CF08486701B79654B8E622FD2303F96
	20117B61DD4A6FCB51E696CD878E54DAAC9DCEEBBD31C44A4F9F77641238GFDA8C08C401C3D9877BFB5E05C2B17BDA6943FBCD6B56E51252F24AD584B621DDD6A0EBB637FA865DF8A788600DE0048D26C9BF775B48C623EC05D309C3A57335F34AA4ACCC64D20E4E4CA790F658CC5FD1A224CFC7FF31A2BB3412EA4G61B3747D9EE3A14B41CA1DDD442CBBED0304917555469612GBA5BD526E7E2E7AFDC7783FCC85B8A42EAA9A7B6G107EF02924DF8940A6003512FC1267C781768AEC8C5A8AF08AE0BB408E
	00CFGBE85288238GF09C60B300FDGBA0003G07383F524DC40FFC30218240G908A908B10841075F93CF6B7C083C090C094409C0005G057BE05CG3082048344814C8348AD0B571681B48304G44GA4G887B6335C5GF5GAE00C800D97BB1B78E1BD2D23DE333DC7243F249A74BE53EC2FF204FB6497C71210ABFF6C93E5CAB7347FE19BFD0EE15645F63B25CA99F79F3C1D1568DA2777EBFE08EBE6D957BD552F505A276F83FE25CC9EE10394FDAD95CB3D462CE7203644CE6D95C532438FFA1130B775A
	E7B25CF69F3A6FD5148F7EDF604F7F17007519C56DA436E237E8F6D40BD6512C079FF629EED7BB0935B4AE37DAF81B4FA85E8A63F8958C1CF60EBF58CE72C567F0580CD71C8E51BDCD156A2B28AC3B730A55BD85C0D9A75BC95ED8FAE09C6774275BD78AE4FCB71BC55AF973A6D27D57F48C96CDE2A7A8DDE5F11A9D8CFF3E03BC93F408D725B940250E45829943CDCEB21FC8E3EDAF428307F0325A28B3D95A0E1C2DBAF18255E57523AE486ACFF46A5AC44F7A8BE7E57527AC5DC6C57DEFF62CF6571F016905E8
	1379030DE3F1F4AE1682FDF07A01F8ED41819C47DE6DA075B807108630AA0B49445CGF5A187481C6D77BD78CCC32094F0645926912ED6GC6E39354E86640B519532C3C24C63308549DC41AE43D9ACD9154179FC43DE71A485C43AEF6B9C5184211C94F17BC864F1BD6AA6A4EC971E075AF19C8DFE2124D17628E9239E74D78CCD6FF1049B411ABC09440C640B33A8FADC817DD4E6836413582C0DF4E68328F913A051C2E371C7189136867173379CC8425CB34A2DD43A1B6AEDB8FB17AE0386E647C46F5A15DBC
	A88B07C16EE1B5FEB69B17FB180D6FD609BE6CB06397718521AB36A3DD74E1267FCC3896C0DDB5C088G23DB371C4B7D42ADD728E0F4C595C8171C0BF46595EEFB6BA53ADE38B27FCDDD05F42195CC7F288A46F7D6055C4FA37238DC999D51D3B8A277735955C8D7F80455179DD1734B2EB5DC6E91B637A1FEA174D1C764FEF91B5345C9F489923F14A3F2FE2356A2DDC1A51BA315D63239F0F5251C5F05FC1ED715AC9E6A7C5E63F426CAB6578A2EE473EACFFE3FDC07F4E115ACDF547935FD4063DB4966ECB361
	DAAA63477CD7329E696682FEBEC0EE9563E7281278D1392B8B10AE240A4D7D54657E1453C5D63179E1EC95FBB618D5A559C169C6ECC03A84099F0987111FD2A537770B0DC817D54D62D5D84D6476D64B65AE59446D2DE6F3511955AC9EF32B657C92B8DDF2B54B3BC52A7C9EAC647765D1B627359EE57188BAAA675735196755D1293FB82A462FFC8B52259FE3F36302E3AC9E1E7C96EFC53A62E34C9F554754783D3A137B79981BE3479EE3FEC6FE2C3F7BD5E716FB6C42BE32F389DCDB0DB63B63848C44AE24FD
	31130C376499D5C28BEC2D68CC9799A768DAD13B2EDB675AE96F0171654081E95401BA61B8512946C5E4919E330E615A50FB574858D38D750DG0E63788C7A62B2DC9B8A6B0892969EFFB94D575A5668634C6E18636EE7505EE7FA52184CC7851419F29CE5FE1ACEE49655C03EGB4GB05A13171056DA0334ED5F925AA8A84781A4F05A8211736534DDED146F89608B50G4056B4FA4D59122F2C000B82E0E3739BE6B256580D1D55BA072E7E2AD5144653421B72B1AE896873CF427886D0F7925BAE33A85BB23F
	7D79C80554EE761489C2EC66E80D5A1A1A6161F00D61A473D768C9375FC25FF8CDB39100697D158575CE821A7CAFD176BFDB883DDDA473GD1FF4CE26B54B9F0AEF0F726DC77BAE89382908D90475BA7D13EECAD1135E672B6DB3385B6AFCAG5A5CDA248F311E002B476428CE87F3121DA7AB1D8E0745FC7A4BA3F4C252242317BA51568ADCC5FD9149461DCE07513473A819D81E3E528E9A6F90F06E752C15BCDF34D28D4EDA4596E32B3105DC6D12AF9F5BA877E516EE042AAFC73F5D262929E5BE34563AFD19
	7D72EB1A4217511799DDE8DBAC50245462BAEB5EE1F225F3C22ED7DF97397FF47696231129A36953FB8175891ABDDD48DFF5072ABE840ACEB1BD6ACE3975C9F87DB54D7C57D11FE738BE11C013F08A67D7AFB64B63177B95BC5B8318G023E42F8FCE4A3716814CFB63FE3BC18CE775AD027F010B5074B4B33AF617218BF987DB7F6244FBA8D775CE9241FE1A77A996513656F289FDB17CF66F3A2DC17F7014C51275D6B72AC26E1EB4AD87F49451F7E326908C2D62D2A3003BB4E317E652EE5D912ED93CFE31FB8
	E115BC9F4BEFDED60DFFC80EC113A86914DE670E7F44392FE9E267E27CAB17236F6A00A6840059783365CA9B6320EEEE1D5BC6261FBD97751BDF077D5C329548F35B57501FFF0DBCEFDB21649986F5D1DFABF99EDE05BC633EC61E892B1067C2406DBE03B17EA20F4C6375C63B55243BDAE793DB615984A6747C392A43E7BD63BBA58F799A00E7A4673BFFF57F6DFAD773BE8A5A641E65FD796ADB1DF3CCDE439A14D7833202383C9757AC61726477444A3558E74782ED4AD94C4943DF933C1974F526B3225DE9F2
	B0FC56EA324E51E232584553DF322414466F1D5270CDD212711E3E96F5A92A0F57B65723AE6B7349F32E4E56D6424AD5346CB0986D2C5D30FC54A99C5A4C294758EEDB476F5FEF607E7D86790DDA077C985D1F56E1FB975044FC036DC3BF407673GD7F88E5B3F7B01E77B0B6B112E81E8C24FE1DF1754AE6F6BE681DEB88F4FAFGE5671157AF561F042B4D6341793B851671FDEF3D2F310769FC38GED2E83DDC24E234DC985E8CB9C6096F2BD0FEF700C01E503E78C26EC40F63B9BE06E5E006D36EFDC6A36CF
	A65F309165C782ED8227FFE16329FE733C0A6C1A18861B6BAC97BBCEEF45DCE4EC7C64635ECDE8D36E859827AE200E63B7F97A6259CD1E3EC8ADC45B0220CDACEFE7A8BC556F389618EDCC666CC254B5856495DDC419FBB7FB6AFAE933272E63B7232E4D50A61C37CB5DC2F475BDA77AAE3A0ADBD057B9A0AF6B924A1C3D45733E5A3595F52BG9A97279B37752B7E6F190014E3B205DD28DB8C481B47E5366DC25D58DA72A99759FFADEC745C9B626BC6923EC63AB6B66298FBDE106F3F969CFFA50D58279CE34B
	CADFC933CAED039B15FB1E166BC447090DECEF13684770D7A9FE3A8CAF6563F534E9A65495FD0BFB1EAFD9C97FB49A4AD1GF1G89G8B8172AF63DA5785B3D9FF6EF73F73049962E056194EFAACF8CAEB6ED3856227524B6AFE2A11700D52558AD7E64B837499AA7832524649949FF6D9E9635EF434F1A254E5B561FE4F69CBC43E29C9DDFE10048F132ED1CDA87FF77439ECC613D27E1B94BF3BC9A93F73DB141F88F5954DA8FF33C864CFECD6179FAB61E7CB5724E6147FA6FD56DB502C147F8E45276B1472FB
	5AD0FE9E5439F4A8FF18595FB901C420952E3875E36DC27D365B86ED4AE56DD89F7B73A6F28E4257398E6F7E841F6BE22E4B474A5AABECED428A5760ABB8FFEB3809733708AB78CC7C4CCDE277DCA84F87C8EF415CAE216B1085D0AE832883B0810482C4834CE9417CFE44DC69AF3F0F60C2FF3D057F2CD2AE56235F6AAD01797B0A5FF673DA547CFD4C591F3FCFF80D23686F5753157EA6EBD502BEDE1B27C7FF3F1019A569D72CC7FFB7DDA47E56C33997A0CC0FF9365DBA903B65E5566EB33F6D22751E6DD8FC
	9F224FCAB321AE89E0A151DB04D883548338C40C6FFB66037E62CB078108AD8D27F7E0191ECB3B8233EF1D5FF661221AFD9FB909FDB1D0B79BA099C0E80539A3C085003E956DBBE5711B3F7C2848C0727717815A79125FF63D2D1E6DD87E7EC15BDF7E1E721AABE17E96DCD666AFD99B8D8399D12DBCFFDB7873DEAB66EF091E1E8B02F2EE9B0C05ED183F8F588233FB2A5FF695EDEA6DAE598313E7745B2EC1A10F79E20383FDE1EDC3DF74BA685AA31463G9238AFA6ACA3B2D25A5447ACB5E0B9FBAFBDD718EE
	00E7A500B200FAG97C098C02C81F3B652F2E80077E49560EAAC168E15F445F3B84B837363B93F6D928CEAFEDC309C7D18E2C0BF1E4D216B32C638B7819A0C68C7D7AE11E1B58E5C0F2C5D2A9501593459EF3BE0231ACD4F2C60632A91ED5A38120E2BD01E8F105E0EB6D52DA2B28A5A87EB536FD785E653FC3F6DF6372B351B1E97183CE77D362BD115B7ECF5E072C27D36EBD615F7F9CDE072CCFE5B39D465652FD5368BD550294B7B482B5DCEC5B9DAD55ED3DE6D86E65FAC3F6DE62A4A8B498F4C3EF1DE6D14
	76A52A4A7BF8DDE0761579ED37C0D55E822FF68333CF7301BF7B32BBD47D6255EEE076CD745BEE1B2A3C66750149B37BEDD72E104766AD115B4939AFDF7316B35EEB93523C65A303F25EC276CC6BC106218367AD799D589F87F5E0FFFC131E810A05729C00F99D589FD797849657B3DE6D14F195CCEA6D5E72EAB7B0BFAF705BAEDFD55E748D817663FE5B6DD615E75E9818BFFB3D5AA97DD923AAEF7F26406CBB6E37DD332A3C4C42406CDB6D55CEE91FCBD5DE3CD73B0159176437DD282A3C484D0159B7452B1D
	523EA8D5F99DDB82336F2A5FF6F12A72BE5B9A18FDD5DE6D1476A5284ADB3CABB07B727C36CBD1482B68A47D1C1E1E6148025F45G8C77B30A8B065F919CF7609A4145416F399CB71C6266416FC51D388E743B9E4A4F8C4FA4E6FC7EF85DC27660705C217B44BC6A6E3CCE56FACB4D58475E24E5F2C6C23A072E8F666C7FC000487274DF61AD17A1FF470079A1516A7BCC6C9BD675B331EFDB554F443EE3DD417040C37E668B563FEB6D7F4C6C7F312A1D19E5F17D15601D936F88189375E64465EAB0A7DC9C77D4
	9A6644E89E4F0753C9BC43F9F99AAD13F3A4D21F1CFE2BFC9FEE764CF1F7FC4F5CB2994AF7C75C77D26BA5EF3F0D3904FE0B65BE0AE9C43FA5F25C02C674DB8A7753FFFECB7C3408177FC84B647C86AB5B3F3DD5B6AD7269375137CC06EF3F155133D785967005057BCD647946F1B12D84670272E80E7B9B45C5C2F93A057B093E3B99474BFF25E532974C4AB75A14B6915ADBF55FCB73F1AF99EA76878A9BA83F7FF2103360DE67583F52F50A1C668DB3F9074221333509EC1167F437E8339975A225E2FBEDCF1A
	35921FBA0725EE9FEF2BBDCA7CE63618C5B6877F4B8EB217D6B99748A65CF4AEFDDFAF0987BDE3E64139F4C79F19CB67DB63353B81EA2C9807EFEF76B7E6B5DB3DE34276E4768A041F434A76E4D2B93E1C6203E4F836D773CB3AE7C14A2321AE450A6352C11B6F7E3852265E9F57584839C53B45E9EB9115FD6EE9DBBE2FF32F201DF0081D44590C260136E771D036EF226DD91D2F76869B595F3FE2549BED5229FD690C05C59EC3721E8B0B478799980F7AAE0C476B0B09BFC6C3F9A2400CAE0C475CCC0C475CAE
	EF5F4F835CA2851E797DA53AC711EBD346A311628BEC4AF80897B19EC5D0B75A06FB3E172DE843D10765D4A977CB0C5DF71CFEEAD70F5353F65FF11AE9779F275F58FD47E92E5DFF1CDE337B9AFF192D15DDCBB1DE2DB6CBE7DD0944FB3753372D7B1C6A369EF27A36355A695F56D3CE5F3656BB7D5B7A2DD3BDA797F5496379B94F497A4B1813A9B64C4902E63A26EB07FE9D20414EF332253FBE42EA7755C7CC227B8E41F6E5CECE2178D03BB2A77769B1A7A320CEF0E0CEA658BC6334D7326DAE3B4C36AA32E7
	4F745D6270252F5E21BE46B03DD2685E004B21FC477B9F949F66D03E63BD0C62C9B9866A721CB89EBEC94F993A209C6244FEEED2376F1C0A6ED64F29473BFD6754135D7EF36A59EE5FB9756BEE7FB9955F5D7F7DF3F3395C7FFB387F7F66F31FA15C591F7F1F23FB8A51CE257F67D27CAC27527F97F2507F49D0D7534D67AD749CE5B414E7F4E38E1DDCA15735046BBA53677E416C6E7EF43D0B6E95A4F5ABF53D1F6297F5ABF55D3692F54D6F09577676202E9769794CA4A82774202E2B736463C88977779FBD70
	FBB87E495572FB7CB30E9F3B46733E59A34DA9FE3D066422959EE5C4BB1D0F10736ED21B53EB65FEAA653C7E57DACFDE25927E19FCB93DDB36EE1DA77DE7927E138F64741FF37A5FFC60C97F39043FFB3D5CB6628B56FFDDB6E07FA538307F7A241D78388C4AF5G869776DF9BBA7A6B3F02DC3E7A2F3F507547B017327FDAC47111AEE57FB522837B2FE8284B3D0A713CDEA037F99F37F94386CF1B77E11C9578B289BFE9231CCF9967E35D64C93FDF429FAE14536F6774DAFA7ECDB66E6DE678D7B7AF115FA33B
	11FE4296853DF42E4E3455033E84698F6DD2506F117C60A2F3E53D582AF31A60D11A9C0F35CA1FF6A0BE3BCAFCB84E05F1F5DC4573E3BDDB717C5836EB0F88F1CDD60D5A793170C9E3B4A9D725B35A575C6747583CF9DA8F71D12BCD5789DD98B914A944DEA37262DD791CF73C368C2081408590FE8D637775B5422353225719381E06EB38DEADDC67E7A4A91D4368B01139FF6EF572CD1BB77A88896BCF1368B7C13C5FE9FD2BC77E3E106473CDE43DC6BA1E899D34032EB11801B153FBF1AA6ADD8FF28CGA1G
	D1G33G180E7BB52863BC40B1FD56D30FB29965B7D7294AA811E4BC445F9DDD88ED33FB61199A209640540BB2FE5B08B2C677220C4329EE99AD8755E56C417EC6741F936DA266C4844810F493D13739001B8710F5A3DEDB84D0F3836371DC2BF75CFE5F0A7A9AB8DDEE1BF75CEE5338EDF869223A8DFB25B16441DEE57F623A616ED3C65F70553F7C87FDA60B3C21BC6F76B645475CD01EF76B6A4373EEF1D0372D8F479047CDFCCE8F69636BB9B44E6CF7256C7773B7EF759A1B9C180C646FC146268C254F2777
	397DBC2B4F174F4FE5929FA676A9FD5ECC71737B14BE1F3798FD3E886A1AEF224F7FC84F094D04F2924715D9B08E8BB84EC4F105C1503784E1ECFEFB519D0F57E53FBBAE8E3D5F023F879997AEABE3D39B640EC7F3102F580452678FC710B2B663A9BEB8C8991BAD4D981BB028DB9844E35302F1E87CA77479E43D5B4F53E53F8F69075E9F6706D48633F9BFBD6B948276C6F21B7F37033C339F8765A4GE1D83C36F0985625B9C9DD851475GA143646DC29C03511770141709BCA58D111DA4612533E7031221D5
	14555671885F8CD97DAAE14EB2BC3F1EB00C7868ADDA0E9E46574EF350E7854351765F647876593F67A8FD66EE5714B374BEF38DCAC6E0BEFBA85753E7B3F2BDFD763B956833F2EEFB78CA5FBE1B34D269B3F73B0FD78E3D4F2A86A5A3B01FF52D7274596D2B50E7CC07960332BFEC9C6E565DBA5CD7FFF8BFFD1E899E2E6C8F9F217830614A7E30249D7B4309D017950CB1AB6D407E50940CBA7D2DC360BE52185C3FC7F58CFDFFB8F1C8E53018BCC65FD5113F6AF45EE856DBFAE07AC63EC267306F2CBEF1366E
	745923CD273FEA2A2A059F6CD9A332C77E7D4732A543D41FB58804873338C5CB57C52B4727327D57EBF47FAD99708B02F17D2759A57FF65C913B5D52E214E63974F9F6A1FD6EA1F232C6C05CC690DC7055BC05DD16D436074D6AFF62D1CF56EA7EBBCDDEBF4F231E582773E87F41B5F9BD3173DD0155B3DFBC7FA1FEAFE05173B102691F03FDFE8BB3E404648B699A1AFCCFEAD94E75B4E91D689AFD7F4A6694995F97AE49FD1CBB5E24C97541B7D96217B2427DBC477CD51C0A7E5AF69B7A6BE22A52DFAFE86476
	0ECED57A6B91C1DE7F73D4253F5E7670D7CA2A52DFFF145AB3BBFF1206F6B29DE7DFC29D9B390EFF3F245471BFBDF4ACEED46AB859C3474B0DCA9DDF7650B134D1296329B4390EA90D28A3AB7F13B8587BC432CDC45B425C3609CA5B9E73502D38D5E9DBC93A07ED2DCA5B0C1E3635AAEDDB65E15BDFDB51B6E99F1C6E034D867D12EE433EF0FF9F76056937E3DF78E69FE9D7444B6F50B2792E88AB77748DD5FF651699BAE4B208EF9AEFA7360F3AD9588FDD4D6D6C9EE3FE7ABC83FDB771F63E773B98FDB70B6B
	5D18C9FCB51F17DBE919FCB306157F3438BF3B665F7E5D76FC2533A9197D49782EFB3EAC37B4D9246F33521757ED227E2C719AD9ABE0EB8A6B3308DF32C3623579G4C5727AD8371B563BD1CFEF72453E8B6F6BABBA9E74677DE7A6CD88DBC6BC2B086564B9817DE0E7BC39346A5B2847DBE2E1944E1A6AFC752B279768EAB97B5B7C875C311F3CC4691AA63D66E394B0179D9239F081F599AE00CEEA90D2131D3E73BCA8E6C88D9BFA0BC6A9C72FEA5B904FD134B9D1F5FDF236BBF4748ABC59EEF73C9758BE575
	9EEF1FC975AB2F3B6BBD5E6E1174FA2A12549B25D7CA6BE47A3173337B1C9E5FFF790174EDA238067EG5F3B5F122AC6938557991C26A6D30DE6AEDC677DG574296ADA35FAEB00BBD052A6F8C97113728F7C87303C322FC9C5FFD3DD6C3F8280D6342C8664F5C116E3992737D769C256F49F74E48EFD67F67954A7A7A8AF9BFF50D6E83D4G5F7A117CBC0A916F8757C87E9C52CE72BF02172752B2795693AB6FEB47BEA4E2642D3FA7180CBAAEE3A86E89F6BFFC59515F7DB0C3320D4DD38F53BEFE8E60660F44
	F96A739AF2467B1C2EDDFAC369285E68305868ABCA002C33D92C224DF115E169D77E24356D1BF42E57A1DE154A4353645F60115E7F3A6A6FFC2492D81E7DA2936721531FFAD4083D2DDA3354C71EE50FE27695CB5722B76EC9F55B9B351864FFA35439GA2G26834C82C89A0576E726939D495BD8E0629107C357E2202B72781256B9235863C6130D84467BF9E222E47B29748FFD7990B81C047B0A5DCC753AB66E1B1254417A0635FB71D2006D444136E37D430B3D64FB1BA65DD50B53A1884D2369F3C6AF76AB
	8B400F5900AB8528994D3E3195C5E96A7A727D72BEE4A67D20777B52A5D2FD54CDA23BCAA49DA566C60F0839D1B079C121E0D305E6050F5CC8994DF222608E77338439B29B1AB2D03F3ABB188D8638765E413E3545ECF8EE31FF9B3AF6909B3C5FC1AE1176953AE81EB05ACFAD05022C2F5575B8F6D69BA12D4847480B76146EAEAE5CDB3AF787EB971665EED7F9F1A06D18DD7770F7C643A43B22619AF7877B869533AB35593FDD3F5EC36C72FEB735C49A4344A364BB6F7CDC5ACE130E1CCAE3F90E313AF1CC7E
	7D3999B9B7FC5E4789D20C964AE21538755DB4A66B45BD1BD0D60B25F3275DE95EC9E562D1B8BA50CA4F5596A7F19C11D376C35CA78E3FC156C92CD08E81087CA13E03F2B1953FBF974BE91FC8A334A9D04E9A932FAD9C0334C119C8DBB686E9FF2B25FC219C8290B9864FE35F21A5E3CD6C986CE36ECC5FA2786EE3497B1FE79DB6B085FDFF27417B1E0B626F544C9E43621A8C5705E35877F6D8DCF35B7D47F5C2AD99DFD55E5D1176F88D9D72FBCE7A56EF86E94F4F0530E1G3F751BC17841D456E154190C57
	689287330679A2F289294F8D0D57968354816881DC21681BFF4BA03EE1DFA0D0763DC775EDA2BE17223CE90BFD1F2B09DD6C7FFF8F7CE2B159433EE2F1B8AC1D04BF621FDBCCEED40745CA118CB7FBB109374D58E6F0B0D2899FCF1F0BCCE2ABC53335385C9BF896A08CEC1B9D0A5F0CFAEB67FA0F7B84EE5E8387AAAD64BB4035D20059F79FEE6073F57A1D18DF37D11E65D02E87B05D097B632E0F703E0AFEE802B03FCA7D3B0EC975B7B441F7327BA96ACE77FDB53373B58D63535EA16FCBC91D2F751EE429FD
	56DD1EFDA90B7148EC52BF55C01D9EA084A092E0BA40EC00A40005F7E13CEB330A7A399718E3E84CEDBA18A0B35F5E1E013E4D7BD13C3676C77C5D778C74D9700F50E77F2034B320BC97E07E0F701BCF3F380CBC5243623565E148C35B04BC9CE148E3DF93BD17846519G09E1F8DF27ADA7393CG4A39E36335C5G95G4DE3513644AC3F5F11B96A6A67BBB28EA799230300DF58D86FB9E99460E64870F34662F7D43269BEF172D8653A719A0ADFB0D639EEFC5F8DDCB7CE9F87635EB83E27C167A433211CB44E
	DB7E8285AE6BEE143F1DFE172660EE257C920AAF3EDBA97F49B414DF8EF511F7237C9174ACECFAB84CA3423D6597ABF09561A87F5492422FBEDCA97FBC456B4315727FC30B729DD017904E7BE9AD697B6CD2DF4CE86798B0EF4A6F0957BA6E616FAF9AB1EF226F413CB9E624F10072A2003C7148B7EA096FBEEB6A927FFDD66C92FADE847A9A7211D2692C6D92222B253555AE723E7159A5399CF742DEE7319BC9EF4CFC3B66C6E07346ED81367BF6506DD84C7ECEF333F83C728CD69A4D6532714AB3D823F93FDB
	8DF5D16371DEED48A1E3E1763DF01F82D4GB4G38G426E45381CDF3A5B5F7DDAEF3156C260CC3257FD987F47F3897FA860B593E09E009081B187A80EC07E8FACA57D012F3E6E0C45C2CF151083A65057493BBDA91EE333FD67CDC4367F3C19142DBE56CD4E769E6BE23255473A1FE44B47BA067B19445746DFFC104656ECB2FE30F94BA91D9926446EEFD85C61F5FEE3E0F9F4EF006D668E3A9D4B33AEFA7E23A2C219FF57A83EB6C219FFE90BB17F9A20EEC68476E54FF91D0998185EAF8758EEF9D3E076CE26
	E3DD610F1576CE2778629FAB6DBD54047616C1DD440FB15717AE771D2BAB172B1FF74EDF2E7E1E5D46657D3FE77751F2DF671459FA50A87A8CA05C972F5DF69F2E91A5D3DC99146BB8EED7B72E9B69B92E0E1E4D8B01F284470D273898A84F3C8F633B126E994C616582DACE616509B706EAFD5FAD7369A1131146FD59CD1F11726E0F57165D0F7EF80162EA212C67388FB4FC8D0E638E52FD5188A8475C0FBAFFCA5FC31C414B7BE8B911171FC99B2CCD0457606E1504C14B984C7A9D33BD02BEAF8A1110031168
	0F3FD2DC99146BB82EA20B67A0476934B487219C9109FEB93B0478A90A173F2565D93C7C26561FCD24ED207940E43C6FD746F7D967E4B2DE71BABBB130F64981365BE08C2C5FFC0A4E7566C7AA7B4DFF217805114AFE3356481FBF9E00B9778398B7011E97E63F35325FF78D593D6D36FD72104AE0FD35AE477738E048D19F974CB96A6302A3277F772BFBF3FC7FCF2261B95E7F1368E7393EF5FBB6577F7C6A79DC7579550B395E732BD7F25567D70939727915347F3C581D87C1325F5AFE77D6EFCDCEEC799EE4
	CCEBF25B74146CF7D553504BEE9CD299529A1B537F9977271DF846FD76833897779EFD5750A65A0D5768FFCDC25AD55D782C19BD814F439F637F833790F025G75G8E0030897CFB3EBD4466EE235DC838B1DEFDBD48EBBA271B5663FD9E7ECB72BA02E44F639A7AEE82DDE675B852AF6DD71E72D86B1F85FC49793F85F04DFA90E55C116A5FA7D1296813C25E6E2DD4F51F3C4F4F83B5BF08BEA951209E8E4005GC4814C821847791DC97336F578AA372DF3372B5ADA02369A52153612F30ED99363358593D146
	93197EED7D37CC343514374B4ED237F5DB96DF231A08361E4FC2BDC2G9781B083A091E091676752FA5B7AD01E5B565D5A95EA368AE86B8F17A8ED25E7E0A745EBF7CF4271AB4E683EFFFFAD7BFD46B87477F247104A107241C91E613D379D7C9F1BE26DBF6EA6B1E855196C442FEC6D48446F671AC9F816EAFC3772ACD52747D928595D4A33D4DD9E67485E6FD61E25EA71E83F3BC7F9160A0C37BB391DE3FB64FA32356B4B857D6F3BC89FAB25B60D6843FC8E1A046BE1967AED00D8A84F8118B789737219546F
	6653559AEF1F1E5720CF05075027A3B4CA1F7E0B074FFE2AD17AF414A02FFFDB237469DFBCFC3ABECD69538E09BF53A9B29DFDD276907AE4A4DDAF30C2B984A072A174491B195F4DA787333CFDF203779B315CA713337ABF0F78DA1652A7E3BCFC1223D57A64A58F1F9C51AAFDF2CF3A5CA73FDA425FC367BE0949266F21BF8CBA82B4BC0CFDCBD7007FB7FCE41F321D75E17733CF70435E4FC1EC6E7E864D57700715F37A7994BF71E1651C5EA9601CBE866A72227899B9CDE0FACFCA77FA2FACCA76DED914AF3D
	5F27B995992554BB133E63949D2554BB142FD945C2DD41A37CBB6AD901697D7C92E53B20C75C3A06BD62CB6FED4B089ED10FA8757E146227BF22547BA75928F79C54EDFB941F179F20FDCF8814A3B86E907FF6D25C23E85F5BA48BA51D7FAC7BFDDD987A31AAECC8E5B01BE7507EA6996CDD487D50404FF096CCC6BF24E73AEDDFA67BBD6EFB187BCF9DD2994C6657E99FD3817656CFC6BF74E57375A96E070F1739ED5FAB7BBDA3FB687D7072104AE0E3C2EC1F77B9029778DCBD827CB09DA091A00540FDF80E20
	DE70FF0E6025F4EF5EFF60F3632CE8463B902E25GE4FD0F71368C60CC4D5F16F8734EDD0A6DEAA55EFA383AG489A9863BDB63BBF5E2C8FF9378F7B10D052B69A7769776E71787F0BEC1F7E184952033B74527875857F7F8C42E3FCCC275FBAAA03F29D0061B15CF3DE49772D03G977B98BFF712413F2F4DF9CC2234C5D3A0DF819A26A00F322548438A3828A97CBC4AD26411B485F9BCCFE97327427C9C20E6AA6679BF3C464301656852FE3F95653BDD35C73B66297E579D25F10C7A51B155EB9CE35F181A6A
	B50E393FB185F5A95C5E7CG770CBE8A30DD13C73B7A18015A3B0E0E430698253DDBA85E912334F732BB1F20AEB9866DBD3CB4B03DEB83ECB74223DD4D34015A7B859D3F9B27A96D2D21F843B4253DAF725CF7C0DD42B46C4BAF51FB307CF1C6AF0DE90240FFAF137D9E77BD3C1BBEF5C8E5C87BB0344F287730F913ECED30C876FBDA46505B7C2FC3AAC35AC321FD1C4943662359EEBB3F127DFEF569505B1CB224B25858E73BC95F5BB3995B4C64AC9C475F11210EFFE229923F1662B7BBFF8557687F96B29C42
	F2FA443690574B81C14F2B4A917420278C1143CE88F20C5DA62C74BEFCFA64073FFDB53A29BACFA3E8CE880B8DA2D95DBEA1E47498758E034B8A376EF950A1455EA574117B131582D0874F136E38A9FF7FE06108961F6FEA26CFE7FDD27EF477F9B5F6EEE394DD7F2C873CF5BA6EDD7F2E7BD34177796D331DBA134921B3F59CB16B49D96E4A2B8E513D67905AC3785B11C62AE4F5F777903D5BE5ED7E87D0CB878807BA7C4E04A7GG080DGGD0CB818294G94G88G88GB6FBB0B607BA7C4E04A7GG080D
	GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3EA7GGGG
**end of data**/
}
}