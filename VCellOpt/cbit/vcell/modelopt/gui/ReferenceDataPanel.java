/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.modelopt.gui;
import cbit.vcell.desktop.controls.UserCancelException;
import cbit.vcell.opt.ReferenceData;
import java.io.File;
/**
 * Insert the type's description here.
 * Creation date: (8/23/2005 4:26:30 PM)
 * @author: Jim Schaff
 */
public class ReferenceDataPanel extends javax.swing.JPanel {
	private cbit.vcell.opt.ReferenceData fieldReferenceData = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.client.server.UserPreferences fieldUserPreferences = null;
	private javax.swing.JButton ivjImportButton = null;
	private javax.swing.JButton ivjSubsampleButton = null;
	private boolean ivjConnPtoP1Aligning = false;
	private MultisourcePlotPane ivjmultisourcePlotPane = null;
	private cbit.vcell.opt.ReferenceData ivjreferenceData1 = null;
	private javax.swing.JButton ivjeditButton = null;
	private javax.swing.JPanel ivjeditorPanel = null;
	private javax.swing.JLabel ivjeditorPanelHelpLabel = null;
	private javax.swing.JScrollPane ivjeditorTextFieldScrollPane = null;
	private javax.swing.JButton ivjhelpButton = null;
	private javax.swing.JTextArea ivjeditorTextArea = null;
	private javax.swing.JPanel ivjJPanel1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ReferenceDataPanel.this.getImportButton()) 
				connEtoM1(e);
			if (e.getSource() == ReferenceDataPanel.this.getSubsampleButton()) 
				connEtoM3(e);
			if (e.getSource() == ReferenceDataPanel.this.gethelpButton()) 
				connEtoC2(e);
			if (e.getSource() == ReferenceDataPanel.this.geteditButton()) 
				connEtoC3(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReferenceDataPanel.this && (evt.getPropertyName().equals("referenceData"))) 
				connPtoP1SetTarget();
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
 * ReferenceDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ReferenceDataPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ReferenceDataPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ReferenceDataPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ReferenceDataPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ReferenceDataPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (referenceData1.this --> ReferenceDataPanel.updatePlot()V)
 * @param value cbit.vcell.opt.ReferenceData
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.opt.ReferenceData value) {
	try {
		// user code begin {1}
		// user code end
		this.updatePlot();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> ReferenceDataPanel.jButton2_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showHelp();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (editButton.action.actionPerformed(java.awt.event.ActionEvent) --> ReferenceDataPanel.showEditor()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showEditor();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> referenceData1.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setreferenceData1(this.importDataFromFile());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (SubsampleButton.action.actionPerformed(java.awt.event.ActionEvent) --> referenceData1.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setreferenceData1(this.subsample());
		// user code begin {2}
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
public cbit.vcell.modelopt.gui.DataSource [] connEtoM4_Value() {
	DataSource[] dataSources = new DataSource[1];
	dataSources[0] = new DataSource(getReferenceData(),"data");
	return dataSources;
}


/**
 * connPtoP1SetSource:  (ReferenceDataPanel.referenceData <--> referenceData1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getreferenceData1() != null)) {
				this.setReferenceData(getreferenceData1());
			}
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
 * connPtoP1SetTarget:  (ReferenceDataPanel.referenceData <--> referenceData1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setreferenceData1(this.getReferenceData());
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
			ivjeditorPanel.setBounds(581, 283, 428, 500);

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
			geteditorPanel().add(geteditorTextFieldScrollPane(), constraintseditorTextFieldScrollPane);
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
			ivjeditorPanelHelpLabel.setPreferredSize(new java.awt.Dimension(1739, 230));
			ivjeditorPanelHelpLabel.setText("<html>please enter data separated by commas, tabs, or spaces.  Column 1 should contain the times.  Each row represents data at that time-point.  The first row must contain column names.\n<br>\n<br>Example:\n<br>\n<br>time, sample1, sample2\n<br>0.0\t1.2030\t39.3828\n<br>0.1\t1.345\t36.3939\n<br>0.2\t1.2345\t44.334\n<br>...\n</html>");
			ivjeditorPanelHelpLabel.setMinimumSize(new java.awt.Dimension(68, 230));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
			ivjeditorTextArea = new javax.swing.JTextArea();
			ivjeditorTextArea.setName("editorTextArea");
			ivjeditorTextArea.setBounds(0, 0, 376, 68);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjeditorTextArea;
}


/**
 * Return the editorTextFieldScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane geteditorTextFieldScrollPane() {
	if (ivjeditorTextFieldScrollPane == null) {
		try {
			ivjeditorTextFieldScrollPane = new javax.swing.JScrollPane();
			ivjeditorTextFieldScrollPane.setName("editorTextFieldScrollPane");
			geteditorTextFieldScrollPane().setViewportView(geteditorTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjeditorTextFieldScrollPane;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MultisourcePlotPane getmultisourcePlotPane() {
	if (ivjmultisourcePlotPane == null) {
		try {
			cbit.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.TitledBorderBean();
			ivjLocalBorder.setTitleJustification(javax.swing.border.TitledBorder.CENTER);
			ivjLocalBorder.setTitle("Time Series Data");
			ivjmultisourcePlotPane = new cbit.vcell.modelopt.gui.MultisourcePlotPane();
			ivjmultisourcePlotPane.setName("multisourcePlotPane");
			ivjmultisourcePlotPane.setBorder(ivjLocalBorder);
			ivjmultisourcePlotPane.setListVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmultisourcePlotPane;
}

/**
 * Gets the referenceData property (cbit.vcell.opt.ReferenceData) value.
 * @return The referenceData property value.
 * @see #setReferenceData
 */
public cbit.vcell.opt.ReferenceData getReferenceData() {
	return fieldReferenceData;
}


/**
 * Return the referenceData1 property value.
 * @return cbit.vcell.opt.ReferenceData
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.opt.ReferenceData getreferenceData1() {
	// user code begin {1}
	// user code end
	return ivjreferenceData1;
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
public cbit.vcell.client.server.UserPreferences getUserPreferences() {
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
private cbit.vcell.opt.ReferenceData importDataFromFile() throws UserCancelException, Exception {
	cbit.gui.VCFileChooser fileChooser = new cbit.gui.VCFileChooser();
	fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	fileChooser.addChoosableFileFilter(cbit.util.FileFilters.FILE_FILTER_CSV);
	// Set the default file filter...
	fileChooser.setFileFilter(cbit.util.FileFilters.FILE_FILTER_CSV);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
	String defaultPath = null;
	if (getUserPreferences()!=null){
	    defaultPath = getUserPreferences().getGenPref(cbit.vcell.client.server.UserPreferences.GENERAL_LAST_PATH_USED);
	    if (defaultPath!=null){
		    fileChooser.setCurrentDirectory(new File(defaultPath));
	    }
	}

	fileChooser.setDialogTitle("Import Data File");
	if (fileChooser.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			if (getUserPreferences()!=null){
				String newPath = selectedFile.getParent();
				if (!newPath.equals(defaultPath)) {
					getUserPreferences().setGenPref(cbit.vcell.client.server.UserPreferences.GENERAL_LAST_PATH_USED, newPath);
				}
			}
			cbit.vcell.export.CSV csv = new cbit.vcell.export.CSV();
			cbit.vcell.util.RowColumnResultSet rowColumnResultSet = csv.importFrom(new java.io.FileReader(selectedFile));
			double[] weights = new double[rowColumnResultSet.getDataColumnCount()];
			java.util.Arrays.fill(weights, 1.0);
			ReferenceData referenceData = new cbit.vcell.opt.SimpleReferenceData(rowColumnResultSet, weights);
			return referenceData;
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
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ReferenceDataPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(670, 222);

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
		ReferenceDataPanel aReferenceDataPanel;
		aReferenceDataPanel = new ReferenceDataPanel();
		frame.setContentPane(aReferenceDataPanel);
		frame.setSize(aReferenceDataPanel.getSize());
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
 * Sets the referenceData property (cbit.vcell.opt.ReferenceData) value.
 * @param referenceData The new value for the property.
 * @see #getReferenceData
 */
public void setReferenceData(cbit.vcell.opt.ReferenceData referenceData) {
	cbit.vcell.opt.ReferenceData oldValue = fieldReferenceData;
	fieldReferenceData = referenceData;
	firePropertyChange("referenceData", oldValue, referenceData);
}


/**
 * Set the referenceData1 to a new value.
 * @param newValue cbit.vcell.opt.ReferenceData
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setreferenceData1(cbit.vcell.opt.ReferenceData newValue) {
	if (ivjreferenceData1 != newValue) {
		try {
			cbit.vcell.opt.ReferenceData oldValue = getreferenceData1();
			ivjreferenceData1 = newValue;
			connPtoP1SetSource();
			connEtoC1(ivjreferenceData1);
			firePropertyChange("referenceData", oldValue, newValue);
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
 * Sets the userPreferences property (cbit.vcell.client.server.UserPreferences) value.
 * @param userPreferences The new value for the property.
 * @see #getUserPreferences
 */
public void setUserPreferences(cbit.vcell.client.server.UserPreferences userPreferences) {
	cbit.vcell.client.server.UserPreferences oldValue = fieldUserPreferences;
	fieldUserPreferences = userPreferences;
	firePropertyChange("userPreferences", oldValue, userPreferences);
}


/**
 * Comment
 */
private void showEditor() {
	//char[] newlines = new char[100];
	//java.util.Arrays.fill(newlines,'\n');
	if (getReferenceData()!=null){
		geteditorTextArea().setText(((cbit.vcell.opt.SimpleReferenceData)getReferenceData()).getCSV());
	}else{
		geteditorTextArea().setText("t, data1, data2\n0.0, 0.1, 0.21\n0.1, 0.15, 0.31\n0.2, 0.16, 0.44");
	}
	geteditorPanel().setPreferredSize(new java.awt.Dimension(600,600));
	geteditorPanel().setMinimumSize(new java.awt.Dimension(600,600));
	try {
		int retVal = cbit.gui.DialogUtils.showComponentOKCancelDialog(this,geteditorPanel(),"time series data editor");
		if (retVal == javax.swing.JOptionPane.OK_OPTION){
			cbit.vcell.util.RowColumnResultSet rc = (new cbit.vcell.export.CSV()).importFrom(new java.io.StringReader(geteditorTextArea().getText()));
			double weights[] = new double[rc.getDataColumnCount()];
			java.util.Arrays.fill(weights,1.0);
			cbit.vcell.opt.SimpleReferenceData simpleRefData = new cbit.vcell.opt.SimpleReferenceData(rc,weights);
			setReferenceData(simpleRefData);
		}
	}catch (cbit.gui.UtilCancelException e){
	}catch (Exception e){
		e.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog(this,e.getMessage());
	}
	return;
}


/**
 * Comment
 */
private void showHelp() {
	String message =
		"Time Series Data format\n"+
		"   Column 1 should contain the times.  \n"+
		"   The first row must contain column names (e.g. t, var1, var2).\n"+
		"   Each sucessive row represents data at a time-point.\n"+
		"	";
	cbit.gui.DialogUtils.showInfoDialog(message);
}


/**
 * Comment
 */
private ReferenceData subsample() {
	ReferenceData refData = getReferenceData();
	if (refData==null){
		return refData;
	}
	
	cbit.vcell.util.RowColumnResultSet rc = new cbit.vcell.util.RowColumnResultSet();
	String[] columnNames = refData.getColumnNames();
	for (int i = 0; i < columnNames.length; i++){
		rc.addDataColumn(new cbit.vcell.solver.ode.ODESolverResultSetColumnDescription(columnNames[i]));
	}
	for (int i = 0; i < refData.getNumRows(); i++){
		rc.addRow((double[])refData.getRowData(i).clone());
	}

	int desiredNumRows = refData.getNumRows() / 2;
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
		cbit.vcell.opt.SimpleReferenceData srd = new cbit.vcell.opt.SimpleReferenceData(rc,weights);
		return srd;
	}catch (Exception e){
		e.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog(e.getMessage());
		return refData;
	}
	
}


/**
 * Comment
 */
private void updatePlot() {
	if (getReferenceData()==null){
		getmultisourcePlotPane().setDataSources(null);
		return;
	}
	
	DataSource[] dataSources = new DataSource[1];
	dataSources[0] = new DataSource(getReferenceData(),"refData");
	getmultisourcePlotPane().setDataSources(dataSources);

	getmultisourcePlotPane().selectAll();

}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G500171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D45715E850E0A40AC693C49351E0441428C9D0799A371F6D5A2EE96C465DB50D5B387B592D4D122F369FED6956ED5D2F0F81A28A46BF524646A41346D8CD8865AF1190EBC4D1C1D1C7980751104401F9B083EFFEB273C68665E74F39771DB70F618D23ECDD3F6F785EBD77677C5EF34F3D778DC271DB4979B906E2C148499714FFC7F284217A3CA0CC392B7AC542A538AD0502665F5BG3B057D
	F9F970CC063A2C2FDB8AF789B35C85D0CE07F2B043D27887F85FABBC644EE8001788F49220EE72070DABC626F368B451594A4A4983C570FC85A083F0784C73A07D0C0157947CFA85CF10E390048D2C4D3F8C6CD7F075D03E86B08BE0310737DF854FED0C72381AEA25DD6B14D4A17B3BBE438E9CC7B3A641A1AF6F5B28BC0D42EFF2323DC8EB8B63937B44C2F9B9G8A1FABFC23248C1EC7CD5D1D475A5B1CF26975F6D112CADD9E3BA8F93CF2E9E740D9D353A4F608BE515DAE5659E41B496696A56FD7B8E65DE0
	819B83EC8465AD8C7747E624138E6F2B81B62A78CFBB91FF5CE7A93482F8FDA45BDF7FFCD4615DFAF3193031735B5BF6C610AD4E47E5DA6C8B49E6FA2FADF79F3303638C6AFB93546D72DB8A8FG5483B8G62GDEE776771CFE8F1E35ED72C90F4B65F157FA3DA7A43B6FBC4A8A6FB5B5D023601A44F60F4FAE885C460641929DFEC6825EEF57ED7763BAC92DA4FB27B5AD9456BFF93FE12B0ECE56275EB4A47B39AEB27CA11DF05A8D91E92F738F75472134DB84ADED61250A2D79BA3477FF3FE26BA6E59CC18E
	512E16515613188F9861BDC7518547A7B1FC1F864FFD534870D81E86F51BE5728D6BAB44CBE35EDCE1D779833AFAC87CD63C21B540F91083613CBC4B7CAEB940E96ED6F95940702BB5F84E4BA9064772E6280B3DEEA9647E75D5834E55DDBD16429200BA00D600BE00C400349E72310C53FF39C59FB359FC720900D3320BBE01777D6A4C6F61A986E51F2D416D17EDB063ACB61F5356A609FEC1E9F3638C6A20EDC83FC1667B6DC0630C6896FDB65969F14368B617A80BBEBFEF93F786E5EA77092C566CF409C040
	6595E85C04B3FB61A9597C72E52F5DA60B21963CBE718C5A643A4D82A342G704E757250D926C710FFA3C0362A073D75B85EB95187512226265163698EF84DA809D097C4BDDFE33123G5E0DC152F152B94445C1B9B9C8631C2D5F2399274E590E6C5BFC3DB4472671F8836D5782E48549F7FEE6A55F295B7118D0725CF4DD5FC9GBFAE68D5FC26B7FC8EA5F3BF06B16B214E51CBB17FE033D5E77EA0EDEDB9BD28BF4F74E02D26AD277129AE0D2DE1655CDB9C9FA1DF734E75DDF09E650B8739D281D675124E47
	559B398F1F3079C5725A0BBEF0B8519E32G677339CE2342870E0F8BA62ECD98435F91781AF9C41569C556F68350A938E1A9AC83308244G24822C8158FC03F4FC2EB3C6A32316F7AD0DE8B23F2D70F1AB7AB992C15F3B85F7C4DE30FE1B42D33471F7AAFC5F8AAF3BF5F05CB6B79BF09C713A68166BC42F6836430BDF299B5B003CA8712343237609E3F8AF441B13B0417DAAFED283523149324F59961045E6D1161D6E4ED07D7D0C16BF50668D05AA2275E0836A41538371C6EDBF378167EA3B476D96191328DD
	147A9496ABC4C9F48153A7BD81374C710F1E451C2ADB6CD5E2554233AF8B98EB24G46BD1E27AC36210E3DBE278BDC8FC327603D09F124E770B50D0E0C0043FC2B8E1E561B948BFA5E21D8F0604682E14517DE035E1A16B8EF32A163A61F072BEF06E28167E976B9FD5E67311825607D1C571515280761D993771F3DC251F69C5B272DAF273A5F1EC1B9C53BD37678F8A7BED7EBAFA07DCD401750E7A9FC2D0F666A5B0DD8CF71D11924A7BD1244CDGF556BE54530F1B0B95B94B20DC5F477504E52E47FB4CFAFA
	CC06F669AA2DD74E6FC2FF903F8AA05B984C4F29B6FB757C1E30BA0BE2BFDE7F46F914CB522C8366FE247B2603F26055C0E7A3C0DEBF0F37F57D1C3F2281EC37D9ED178CF80CA1997D1C4755CA3BF856AE49C96D2C833C5EBB909A6FCD40F33A573A285DBA284F423E03FAF4BF6A26F626C19E2F082EE31037DBBB086D56296D12B563E03B0CA16D62A5EAE70C3194D683D8E3F83B4498EDBB130B5AAD867CAACD3BCC259D7735DF9EC63B317C3D4BF65D96AC7577409C2E29B92B4953FF4856DAEC1FB70672719D
	3968CFC721EC86104650DC79279D140FCF6BCE95563FD43EB552DEA3F68C67A3F5CCE82EB832EADF667EBA71A2524C043A4DE348FF924FEBF302DA3F5F536EE4690862A3E7AEE8679247B5DE588E4F8E274FAFB7C0308B12BFB5DDC0FEB1DFD150CA7ED6E4AE9278DCAA9A8BB6G689B0B7886B346987485AEE7FBA54919F0E31EF0EDFA1221B0021CAB46F279B60C8D49D9B230B91F6B76DBE6526DEBF711EE93DA108793149DGB1G1C37B38E62AD91F0EB30BE96715F3FD82C21D7G38B2GB3G0F898B9D95
	70343189DD4A9FA65157617139C47BA134FA292DC7AEE5EBC2E9AD2BBE056F07A9267C0783E5179DCEBF177D31B70F531CDBB8D7A8300DB3686D3962DF6AB4AC0E65B22F0B8D493EE1EADBEEB6821369EDE711A23FF19C7868B85AD7969F43A70B612A2FEE98C4B91CAE2F47A7E350BB6D7338CEBBA5715061C64D5E93371DC3E267A734B7796D9B4477013A9421BA3BDB17EFE653F14A3EE8DC086F349D7F1D3F8200737D77CE32C541F8CB617171246BAF4D686FC3E233C074FFD12B55DB4656F8DD7A0E790906
	60F8CEB7F9FC083EFDCEDBEEF08E69EDC297514F02B6076EA6DF3867121EAFDC3B1B784B6A9A2DAFF03ADFF6133DE498B36DEE32D71F911A60DA6F071C7FD6CC220C17A351F89BE12C7DF1B4DE5DD954E3006DA5CC12C7BEF458226AF16EDE2D9E0F8EFE212B47381D8E03B90E6B2FAFAE2447A078DE3C6AFFCBD47A2B200DB00174E8EE5253E375845263FE57E87548652BEF46F861F7F8FA6AA14C22F41C1FDE9771538834E2D5FED6187478D9BE0174B57ECFBB1471CE31351F6BCB491B076D6557CC886D97B6
	CE983E4F60FB5CB1AC46EF1E40773879AABE1E610B6E8961B96FFBF355398CF53177503E6CEA932EB119D04E1B88B6862882B0834893A9367F60C22590E55F5B60066D285BA6B58FC9A8E97FF9916D82B920D21EF6D19BBF389E7618D18F8993EFFDDF457B99D93F545B68477D7470D56D39DB5E6E3306DC9DBFE571D199BBF3E248CF396E8A4DE877C371309681348248G5362C9EF7BCCA55174C6E7G0A4238CC7FE08E7709747810FC2B62A379C41E95ED3CBEBE5CA7F6B2FCD6FC38CF5C5FC2BE11F7AF7872
	3D34BFBB6F08F636357A5E21BC70FE1FC56D1795560F4F0F1D4D28CF1DBC67C4C016D91E4B7358363E70BC96776F851340EF81781A3D43C4EBF655A44A877ACFEB731E65BB7BA3668352A4A55F1C9432F5325B3C155B7B314B18372D063ACCGE1B25085B88E508A90B3196CAE1ABE09E6F796F04C9E9EFDCE066F2A94FD7E59992E4F6449C37527EF078F0775AB8DABAF8F9B074B77C18B4A37966A32G8A92AC0587G6A816CGF189A45FB2D3EDB47954A3AA3367A4606BE1EFB0DCCA627769E17C5E1A3FBD9F
	35DFEA021E1CA9D614F385542D87488658B585629E0089C01EC2F296DEBA91CD4EDA5AE003AD69D5C7D04E6F642E514979E054FE71D3067663F3611B17688CBFE58A4DED31FBF4BE659F56AF5C2756CF516B37D59A9D3DD707758B2717BDC54F367B2E22ED0B26C25E85F09C2095A086E096400A29E45BAFAE7DB51AED15B99A5DFF1F9A250CCFC715F143D4BD3BCEECA23BEE1ECAF69DF40D0E07FB07758B67216CBEBD9EFEEDA29E6A211E1F7B7E8F5B0B46C0F91A0A7BC18E62B2203C66BE62756EDC44E5C339
	F89A6126B25C21E9BCF76238FD39FA67248458B77B3EDBDF77430153289811C63EF0FB67306140FD2D3DA97239DED7137E391E37C97FDC6FFA53EE0E07B134378CD4BF50B472395F78A63DF33FB7181C5FB7E0BE608B38E527CBE4E4397CE270EDBED71A82EEBEDD1032BFBC07D16760154FF0CF5AB80D665A7E668A1D3946F3E19A44AC5AFC97BBBA54A05A6C955B09673D66487A7B23D9DFFFC6333E7E8E1AC756DF29B9127E38BF7FBBBB93F000ECFD534847BF37100FA72B7E3C5D0A3815D05E2862DEE53822
	6916421269041B55B2127F5DAEA0ADFADF2952DD72B72521BDA3473941FD67BF65D7851D73618B108588F4D6916B582634B9A5FB4E178B94CB127AA8E7B3CD27F31657AF22AF46C1B999A0C355579E135EDA38E6BA17FB63746171007B6454D64C55B7C3FD717D20FF00BAG7B7D64235FB6C75D631C8A0E3047D9F6B9320FFE63323E0F2E3C4C76051E002FDD8C7743E7AE3F265605D61E86D9F421CAF91B352C3FEC73F50A437C7885561F57C56A3F71F2A4DF67B6FD7A77BB59DC717BC5BFDB8C590989376597
	4EC24516268E1F47F532DC21FB24A5329E0E35686B212EA5329E4E35C4570339A5329EBEEB092E87FBCBA4BDF0D905AE72DB136CB1152BE3AF34C616F509D5DF56272C11E57D16B53A2C4FD8A34B7A4F5668323EE0C53B35B96DCE1FF20B40E57CCE17F6EE62DC60736DBB177466DB6C7D1157DDBE5E2C6E217ED121600FC99ABAA7C33AFC67EAE4DD3EF7D5DF17E5D7A36B724855683ABCFEB532AE4FDC0D2ECB5355117D6697C3E42DD0F51A502427534428BADD6A520E973251965D181896F53CE2816F62A4DB
	2FA7A08BC2498398F75F97E80F3D827A2F8390GDF7CG5FEB73FC60719E34ED876C70C0DC36D097A4E25F1F67D0DF995A478124822C8248FC004E66DEB160BD01ABA049CEEE9CBCC0447B8C6269A04A44EFB84A15FD7FF332F65FEFCA643CA440B3A5116277671F6D505AE2483A73AB73F0B90BAE53184B93391CEB611919483F8560724896EAA34CG9DGD481188164996AD9E38B2EAB1ABBBDBCB2EC343509922F7F6466BE355E8CAEFC5AA9CA7666F61FC712185410CB30B344B3D66D996234991C0F8CF8F2
	9DBC532D5541EA25FEB3BC0B12E8ED1C5D976E878712C2BED01D94695CE8B2DBEB9B13787950B695BF0B619D9A3CF2AF4070D80E053A4CA44A038E33BDC163CC58F34DA45CA49365C60E19E42B245648F3FDCE2B7EDC1F5F2A1F4FBD56BAF2BE3724F564FC6E5F589EB99EF81B2572BC1F6D2FD2211C216276DEA25C4A19F46753AC50F7F7EBGD7B00B66C779B55C032A67D9F85ADCD3F34667341F30F5B6B2AF64F94FCC21C069FFFC9665BDCF33B103D0CEGC81DC5E7D84103F406157648DCE14D0F756F6EB6
	187B8DABE6A93950AC4D7D44FBED391C669F8CC453782025507AA07BAE4A2075BD745B3F4B617BC0DE5FB3249E75097789217A85C36A115F3F66EB6BDF98D21F2F694FE5FB66DD12ED4BB30B85E9C9EC44EFA0139F64B22DD41E46FF1C1133B25174B2577B2536F2A15A770E67F5A28F6521BB82289B1CCBDB9EA4FBAC3A097628FA48D25888E0FF08ECDFF519EC5F87387407687C316FB269F753C3B446232C6D0159164223G665954B649C2ED2559E40BD516F0DB3C142B5565EFAC6136B8E250561734045B62
	4A107EDF34045BE2E29E2FD76E1CBBA25CB9331846799EE07B989CBBE5B679FC6729B7747B8D09F53CFF2EC4E7876BE7130EFEE3C79D95A7C3EE8ED017CC73EEA95B9B34C2592B62E68ED2CC1916CC31EF7AC57CFEE1A31437G944F3194164D213A393C2E0F5F356377GF8B72F1C31DE8C5F9BE1DFFA4731683DEC4E2D1DB5EC547461B40E8F23217D56CB3B1F5AF68B6313DF8CDD4B0F1B58BDB0844E7AB978ED73E1BB4E2C78B9282FF92657220C35D7483EB4343F8D4D25543D97D6975B01F57CFDFE27204A
	F9C5985D37341D236C17FC5B7D783AB6B387FDED6D9C3E2E65287894065F204173397E2C9A67B7C11DE9AE5D1D5D63C03D26C3F98DC0A60070B064A6GC79F26356E5C67788D76087BCA33475B880B2124D93E38FC7B8D237B46781BDBA91E26FF7DD1A1E3FCDD447B8E53434AF7C24AB3E3750C9CFCF2795E68C2791221EEB94086004DGC5A9164283A9A45FFD1FABF7DCF81CD3AB4B36F6877B42C3BD9CF20ABDA1B4A60B4ABEBE20152D9A46EBCC894D1BEBA911F215F77A5096F2CA386DCA98BEA6A55CF6CB
	EE126D6221AEAB0566FD4D655179DC7DA87B0D396DFEDC2EB7AD483FF9DE383CFFE678EB734265DD2C2ECB92542D19276606165171FD17157AD5BD9232C93D725E8ACF6FA3FA674A85434E3635907BC8A47B3AC564BF7191AED72E0A8FB2FC2A864F6595C47A56A2836A764F273C7CFBFD43F37E0D6A7DE18C34CB84D88130F6BEBFF32ECA45B6CF8C56A9EDB64D9F4EA36541398A8850279860D525FCA8B5E48F4E47589E642F01FD97432FB3B97EC48BFE7B047B823A64D4766135189BB753379ACA25922377E0
	9F87AC4354074F77E7BF535EE56FCA128C44C3B87FFD29DC263854D0EE4847D87539B687B33EFFD1B7875B7C798D43F2256F864D980D6DD55B385D67E09745879E553CBFA77CAD4FA075FD75E7F71406926F9C1177AEC98E7D3DCB32C3FF6F7208E364337A050E485F68AEF1E83F5165717A275D185B95AFGFF86B083B8G7A96D02CBE697C1340E235CE1CBEE56F44F3927CE5022C1E15F7C51675032E683FE778288B7D354D833E6A427189FF348B6DA5FB3C8C49F1356C7C49676CF4483CA91D43F4E12EA709
	9D8C2D6CB3D91E1E88F225AD201CF0DF90F1EB201C29626CC14A9337AC20B8F838AF6413951A7727EE5EF97FDC7B7FC06300A5A453871A7725176FBC6DE76FA88DEE53A43677E8FC94FC7BD132734A815A1B772938F755BDC21C0ADB500B38F5D04ED2F1EFC954AE6FEB64A33208792ED9AD8F3032D7AD3FACC612937B7CEDF479D6C49A7F177BC88265FB5AFE4A9592006FD5DF233DDCE545106FF1787945E949535328AC274ABE4E5CCF7BD7A10D76F1E3591855D0B681B852E82F7BA375BB6598403DA530F3F5
	F67E417FE5FDBA9CC7FB0CDF3B701D2FD3BFAC22F5EA7F579E966A1E2FB33CA3682FF599E95C96850F055629EB30CE71D87993B70E46E34C73DD6F4253E26B523D19BC8BAA385856A5364BE69BEEF6F8FF0397F30963B0F6ABC08CC0A2C09A404A4754FB4F6EC3114685A4D7A60B39BE1B13FEF3702B6E48B1F7CBF774181B4F7CD904D8E96E75AA67545BA54C05BC9D9DFED10D6DBB25DDAA2E41EF72781DF83E2D6CA583583E4D63E387E9BCF7EA89D06E348E644B82583F905699G3C33677D4A026CF7GCEBF
	E47DFEA75E8837AE443E75C172137A0514E75D8CD21E153CD0BF4F229C8A6D1E8EEDD6AD8C65D96B970667D90F33FB91AF7B3857A75A1B1DB7F08D61743F4BE2C6A63CE783F01FAAC0B796B8AFC7551CCF8F34F4908EAC3294D6AF623C30EFC19705733222977560F23A1D2E000BF14279D847620F955A5B97119EBE1208F642A2FDBD64EAA0855AE4AC8A69E175B05A3F363EA2E84F1BEBFD220D4E1897095A331FF50B78BD3430D83DDB6D9F5D9E41B84AFED7EE3B9F5F8B348E3233224561FBA211610D0B4377
	C4F1EA1CAB013A84D55E4CA15F5EE6833E60711163B71F7B7FA5234D76C35BEA00D6G99A08EA099A09DE09540FA00EC0022A7AC05C600AA00FAGAB0084907384450923EEBC8F0D94A7140363D02090E88E97FAA3470ABD5E683162F52FFEFE7636F7F8FEF6502B1F1F15F835799957770757E9CFB68DE4DC7D842DABDF84B146074EE759012C59A9CB227D848BB6A7C41BDBD9DB7A2F2B3FEBF91256168E3617BA8AE5B30074247ADDFF905BF558A4BF72455709649E6D1E28FA7BD85D5F05A144BE49ED1E28BC
	63FFB7A307731096A49E56BCF92BEBD65627BC3E6C6B4192FF37748A6A7C4C9840EFE95CE20FD17D903D4E8919B87E7076D079114A0FCB0C9515E5688F3737C6CE3E91FAFF62E6085E426E37A2537B28DA2146656D14C85E02745B1577B916CBF3BBFF2F7FF5C177ACD88DC81C5E0BA21D839FCF27B959B608637077EE4D7B247EBB1F3F4E3F23B4F83E79BB16878553F90C6238BC264F8485272C590148F3FBF7A07A5C7EE3C0FFEE9B834367763B817D397DFEC0BB3715BB77G5A3893E212D5ED77210A3B2262
	EA581808EBF15AE5070ABF4E78C2FC3D088489FF1A715543F0D67A76C3033B2260CCACB781119D9E9F753D9C407C3C43A9C93CFC0535F13AA135D175718563516935E978EED7F1D7D49F7864FA34B5A9ADDD6F3C2BA1EA3FB7F5FDEEEC44FE2B52072FC56B8147573A9F30D818159E3E867E08613724072F01D28F2D0185CBA0F6AE21B36C31AC07DF8365CDCBE8BD4A8E3CAFC4DE0FCCBE0FD774493D9C4BFE892A4872F8D0ABCB51D2D877160E3C1E72FE52103342AA68D3FFCB7DFEA20E2473D61DB1F8FC329D
	ACE27E81A92FCDE26E49717B03BBC2F8F2CF65DB2895FBF91D64F40BA779DFE390E87E4EE0EB159768A5AC25FD654F98AE854A99CBA936EDE0EB82FFFFC9736E6B397331ED629D25417D666EFEFCD74F3338EB506F517B0BC33AF55EE0BF8DED06066ECE2E2FD5C10A0BDF6511DE3F9724387863DC425DA58D0F1FF112B6FEF21C21037675AB17D21E3078BA7E0ED9460463EC40AFBBBB1C6D7CA72A34CFD0F30ECC35CFFECFBE7591E8FD7D65G5A0EFFA3322F0376C57B1741FE8840846085C8D8C6E37C707AC1
	F8EA1642D23E37AA5566BE25A7CFDDB01FEA223D75279D444B62E5F4B75C5391FEB7BCC198F28F5F91FEB71CB3247E050E703B611F8F39BB5E5199FEB7CC7D398F55F5E867DBA67035E5594873145B62338E329952D37EC6C887598CE560FF436938C1027A341F8A79327087FEE4EE440F470CA71BADC6886FE2E9E929ACECD38F47E5E1ABFEED4330DBB19C411B5FA76CD4B31D863684EC38DED9137B2307F38DC2DE0370B2DF929B04BCDC579A04FC366CB5883930A48671CF268C79A7679CBB6A10DD52B120E6
	730BC691FF66E6345BE41B51AFFAEDBE1BAC5A0DED3D46F60F4BE5731798E5DB9B7C6F71997DDEDB3B68AFB59ACFFA24004BEDACB77A9D1E00E40716EE9912F8236C900D7819B536B9E5EBF798FD1E9E23CF048D2C9FD7FACE43A6C3BB7C8F9AFE6075C06E8F2D4D50137DE61AF5F101A32B0336F3E2EE9B0E3B7DE81B6F987B6FD410292BA6044181CB0CDC0B6574D2412A4ACA4B361517D614D516ED2B7C3834323A22DAC117833A326A43ED159F15D6FEDC7931022D4046082E2AAA2D2C2CE2D8B0437623FFE1
	FA73FFA58C8838BA8F81B0521E6169E86D24FFFDBE6D4B3AE203E0GBBB9C4EE187C9E8C504CB4AF8A424B4F28A396E801E18BF89B21CE1F2AAC6C423FFCE3EC96FDCE51EF440FD9155F828EEA778A1B4735C75CABD4BD457D3D7E29705F82FE6CC06A3100CFFE0A5651763C8F044867CA4DAE1BA449B6293B56ED47B326933D3298BA7BBF6640717D54C62964F52758FDC217264F7F82D0CB8788F3D85AD0B59AGGE0CDGGD0CB818294G94G88G88G500171B4F3D85AD0B59AGGE0CDGG8CGGGG
	GGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEF9AGGGG
**end of data**/
}
}