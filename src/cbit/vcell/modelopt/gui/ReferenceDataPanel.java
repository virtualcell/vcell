package cbit.vcell.modelopt.gui;
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
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
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
private cbit.vcell.opt.ReferenceData importDataFromFile() throws org.vcell.util.UserCancelException, Exception {
	org.vcell.util.gui.VCFileChooser fileChooser = new org.vcell.util.gui.VCFileChooser();
	fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	fileChooser.addChoosableFileFilter(org.vcell.util.gui.FileFilters.FILE_FILTER_CSV);
	// Set the default file filter...
	fileChooser.setFileFilter(org.vcell.util.gui.FileFilters.FILE_FILTER_CSV);
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
		throw org.vcell.util.UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw org.vcell.util.UserCancelException.CANCEL_FILE_SELECTION;
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
		int retVal = org.vcell.util.gui.DialogUtils.showComponentOKCancelDialog(this,geteditorPanel(),"time series data editor");
		if (retVal == javax.swing.JOptionPane.OK_OPTION){
			cbit.vcell.util.RowColumnResultSet rc = (new cbit.vcell.export.CSV()).importFrom(new java.io.StringReader(geteditorTextArea().getText()));
			double weights[] = new double[rc.getDataColumnCount()];
			java.util.Arrays.fill(weights,1.0);
			cbit.vcell.opt.SimpleReferenceData simpleRefData = new cbit.vcell.opt.SimpleReferenceData(rc,weights);
			setReferenceData(simpleRefData);
		}
	}catch (org.vcell.util.gui.UtilCancelException e){
	}catch (Exception e){
		e.printStackTrace(System.out);
		org.vcell.util.gui.DialogUtils.showErrorDialog(this,e.getMessage());
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
	org.vcell.util.gui.DialogUtils.showInfoDialog(message);
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
		org.vcell.util.gui.DialogUtils.showErrorDialog(e.getMessage());
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
	D0CB838494G88G88GC9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF8D4D715EC257D5895EDF6BF5454E595A92E5125ECDC315295576A7239292535CD2925FE41C6E995955DD831C60D6B4BCCC2B294CAA8D12952F6EA31869806CC92CA88BF4DBF89A4E4C8A6A4C0A8194C3CE4A63C19B71977C2421F6CB9773E73664D64CD86D8D93E6F705EBD77677C5EF34F3D77CD047257679597DA4A852130D8507E6DAF94045ACEC1587A7A4A1F922E764F59B641706F8D00AD
	429F25A2F836C1DD3A12EDAB935CA525D09E0032FDAC5B76BBF85FAAACB835249DDE1250C9033A514AC56E69694CE8A6BAA52C7CC845C6F83E84B088B8FCAE7CB352FF2EE21B061F2961898AAD02B02B825B88BB37EB38BCE8D38A38E6007E8A5EBE82CF4E401DF535DA3B013F4F9056FDB5EA5904639846A4F8CC6B3BCEFB5A05DF944E5D0934F66E5C2875F1C0598F0042DB057FF0D44273107B4248915FD0D0F5DE740912648C49FED112A32AF3E4BCD8D757AD8E0BD1B16C931B3D2A576D8D0B5236DDB86658
	4ED2B606A06442F88B986EDC91529900F795E076AE42D7C5903F865E055D5936ED3BC93643FFBE24715EFF65F3420A2AD05166A432D56E66B2756F0E49E68F2D323C426C70E08B6AFB8E54AD81D88E1087D0664836D5827C01597F3F1AF74133E1C8ED12C3A1B95C9009B4CA7EE8A74A8A6FF5F5D023613AC51F9C758B8237715E1D8E93FE26835E6F70CD7763BA4928A6FBAF6CDEA464FC66031692931D64E4DC3134B938AE869DB11DF05A9713521E61087747F85AFD0211F65AFFBFE9ADB6213DB067C96B9CED
	1C8C836DDC875A7AF16683D970DE28690263D7B17C0EBDB1BC77CD9F43E37990544D5BC33E517F927152D1B4DFA8F3FD48D48F69DF1EEDA9F2F29E36BB93F979B2733BB6A72739C567658906D78DF84ECBAD43E3F99E54B976E65B38FFBDF494676A92A82FGD88BD0D419ED5B81D08F6029A49FABEB59FB03BE6676C655467120649723826F7BD04BEF61A9CE2AD1EFFBD8D13DB063CEF923C16F10A4AA0256A63B85F5B094576FA9667BBE20F1D48C0BD12F9A1443B03AB7A42AE2D461EDF2DBD0A6DFD4E4351E
	E0C884822108C0632EE9598ACF492B283D913FD795E3ADF8FDDE8B5A642277940C888340BB574B8BEDCC0FA07FEC00393A9EAE3462F84745A8C40B3A3A8ED93EB09E7120A4425ACA54737BD86C4804772CCA5271736D082BF4E55B5ADCB44E7C56178D63B487FD483EB7FA096658D3BC5EC07B2BG69AE721D9F75136FB4EF3ACFF0BC7981D35F312FFA521A6962BE1365CA1CC32B399F83FFF9D0D7D6C5B1BFD854EFB2FF52DD7173EA40E5BE4F4C602A21AD27F107A90D120432758647C7A8B63CF3FD8F9EC779
	EAC0AEB7C0240AF43E2A554EFD38512B086435DDD1F0B8519F33G67B3ABE257784071DF84403589E3781B82DFB3776BB2BD4D5A4E82BA19GAB81046AECDB85009BE092E0DEB56978C8E406C1C6A36FC69A29E4FEC36363C67433BF093E37885E243CE03DCD63A955781BB53EEF0417ADA6B8EE1BA70E62B862C5B12CB60B91B16C0797C52B7B46D164C50B9F43325EA71761A390EF1AE002ABBA7EBBC7110ED7D5234121F1D56C91D5B5989E09557F0851D24607A231D0C534FEFA9475A0CFC03C515B7F62A84E
	D51F9C8E0B4CC974AEDAFD810B9522A4060069A6F9BC2CF23C259DF32A8B62A5ADD62DEFDFAFE02C1146B16E71BCA5530BBA0EC403A1F0BD8C1D42369A0CA38BF6EEB368A88B7C87732D357094EAA896CC3CC931E04765CF8A8F1E0FD84C5634740F5FED992C6173D02D0945824E534F5A4DF97F950BD99ADE613CAE2BC6BDCC4D1A387FEC95B6EE40312346FA97557D72A84AA97A032A9C651D78DC3D2783694F813EB2G9E2C2139FA699856D3FC54A6E913ACC95C84A87EBE54D3C5F139A667CA681BD7437504
	E52E47C70A4D745886FD8776912D6163E5688F6258B8320D417C38EE33507117936ACEE97663756347D1AE49308E94B03E360CD18E2CC2F9B64042FDBC5E2E5D4779DB7CB6367B015E2E8D4A98C3865F66BC2AEF73F6B57B593A9225F642FEDE3FEDFFEC3C57G4D6976A854EE86542783AC59EFC6F727CA6D727773F8C5F44BEAF93B2B35586EE13DDDDBEDEC9CECB798576E7AB8354B02F2AE00F000372BBFE0EC57F4115A7583BEF2A056AECDEB47FD6DA74C7FD97EBE6A3D681DF4AA93B0076B6A0E99727417
	F615EA6D979E20FC3CEE8F7A5333D0AEGA82F23397278A64A4767DC4890243C9556E4FB8DC79D6743DD971BABD99993D66E2F2BBB11E69A544D2BA37F49BBEE4C899A94C57685D9BA227948C7BB0CF3096396F4EC0067F0B02A286D906CA6491FBE55017CE23E2221357C2D2BF823406752E2203B96E047C1444FAE4698033E406574D5131CE917BFAE8CFE60431697134899B848651BF5B0A6E77ADB7B2CDC37456BC9378F9EA45DBED10ABC64A35DC3303E81F05E6A460837FA40CD82149DC67CAB056586FA19
	00DF89D0G40E34247462A6069E5935A499FEEB1BAACC7C3223F822D6E74CE28CE36A6B89BD8F58B3E6F24187264984A2E86028A177D3E576AB5DA4B974D97CA3DF7DA4C76DC333FB7E269BF4CE51EF1B8A67B55952BACE9GDC267268C6CD7EACE8A39C21FDE57991FC32982E7BEA6EF514A3980A48D19503DEEBD48E3586A531E2E707E16F095B4E3858F91076A63FFC15785E513CC028DDF74114EF045AA35A3E68C80C6FED776F33BAGB85F77C749961950E64D9152757CE274773858AC907D679B0CFADBD2
	B25B14FE609369967BBB1CEE5BBBB17A426AD516725524372B8A51CF07B60F3DC33E7008454C97CA6B093F554A2D7A82275B2D123D3643181EFA3257D5BBB44135DE011C7FC6CC220D37CE9FEFB60C35CC9F2F79986AF11C6DA55C122CD66CBC256BF17ED6239E8FDDFF57D40F33B687AC8575DCFFBB9AE2FAAC875F2B507DAFE31C68C720CDC6836971C3D6B3BD66B6109EB7DF3CD5BDF2795AFAB0DEA881F922CD14A2A89D67C73AC87C2C83DA0EC66247EAEAD7FFA3696B2EBFEE5246EBE1EBBF57974FBF376E
	CA5C4BCFB646768B339B276EB3789EF7910B71739A799E37D847BF40700B8DF84EFB7D9EFDAEC31D230976E57F6646B5A68D4A8B81168164G94GECEF2658DE55D9AD2458773607E1BB9A76CABDF189A56DBFBB51AE1083EA65351D4678417550DB0CFA28ED3E71FD956F67E37D5CB7510F7B694EB346F33722AD47ACD693BFE571D19BBB2DB96627DCF73EEE347BE300DF8BD05482BA83B854C2FA9BBB61C825B7BA83509446E55A30BE51A786DAE272C5DA127944D19B5AF8E6CB22CFF4B3FCFACB22CFBCDDCA
	BE31906A4ADAE9FFD6BF166AECCBED0D670177BB1132DFFAC2BFBEBFB67720BECD721C46F1D5E5F9AE4FE3AF96A5663138FF4F04B1178170B55BDDC8EBF6CEAB658357DA0DF94F524D5712668365C735FC73E84C566E65AFE8F9CF318773B6956A52GB2G16812C81A8EA03DC200D6C7E059387D3591D859C0F4C23CFD3622ECA53676F23097AECEB0B571F399DAA2674F3A6147D8963F0796E6CC5792EC2DDBAC0A640F200BC000D6D59364AF612EFF3D7C3AA7974A3AA0F5C8478B6589BCC151278DDBA055F9B
	73372F266C67EEB7133360944A99003A19GF38116G64G6483ECBFC6F27A3A9AD34959C09BEC30A53D1A882A1D7BA837A6E7DA4AFEB547627B7139D0E425B37C0EE3B4377B55DB732961A97D92FDEA66B133FE3F38C5FA256329684DBDE6E65B31FE346DE228FB8CE08DC051F1181BG4DG0163E45B7B3B8E27322DB6C7D37B6F22A93C5E180CCBD34AB86B3819DD9F556DBA6FB859B5FA7156F818B125DFA28FABCDF92833908FF9D04F4FFDF7313DE8C5872C019D04ABF1A0EE904A139D446B62BD081B8B65
	25BA6E0B8C77D8874F1DB82EF50F59B9A9817645B1261B97538127F1FEDA9A45424D1D43A68277354F3B131F6BFD59EDFE2E1765B6BF57FB543D0563E18C63AD8355FF5DBD7D395FB75DE667FE2FB2B93FEE41FCA0BA9ED603A1111165720B13EF703952BD9E665385A9AB09B90CBE87BFFD9A77246BBAE82E29A72BCC669A4F05E9900F680DF68D8F6BC134A7A27A08678B5D4975976DB6575FE437397E7E52BD3D7E6E6ACE26BF6E4FEF30B301324EEC5B0ECE7271F9A56463ED1D643B1DB644C9D01E2D637A99
	EEB114D76838E7CA27733F1B8524C56F12CEA37F2FCA43F8C60EF3037B4EF7555F88A66743A7A08B90682CE2E24C2635E9D165CE17C031E4D19165EC791DF44E722E003ED8F982F48AB0F802E469ADB4DB8BA7CFF039E71F189A8F38CFFE7F8C666A7320FEA9408A0035G9B3B48C7DF6DC93947E9191CE60F3359135CC7FF6BB177516D9E36AF144723BEB1518F5F74EC536BE2ABCF3BAA06D0253C0D0375D73D5191F10A9FD7337E3CAED97FC31EE43E4EED7A50EFB7333922A8224296C3F6E242EDF9B27A22E6
	CB77F0D48EB539B43AF777A6574387FB4D75B037B7399E6E6DCD2D07053D497570195E54FAD85A1BCC8FDC561FC449EF5D2A6CF669E32F6FCBAE6B26BEF3D93776A5177577FD29E5FD33AF392C3B7AD24BDA5D07F69B8A7A03D16D96014B7800E21C1BB89778FC33D94D661B23AB793A4B473BDB0D770FAA8D6F9AB750E90A6972CAFFF2DD5E51EF2E4BD97D49F5F9F7FFEADD2677A75765C77BD36BB223FFFA3FF9AECE56AADD27CF186A34BE25CEBFF951B8DE4CC66FD84C4673249C2FE1975E45C95ECB7238AA
	88AB3AB06E766F22BDF6G4AB3GB2G16F6713DB64F87BEBD01369D069D9E084B966ACCB776FD4DC1FD37C33992408D9081C8F353595CCB963CA7880DCBEA109B878F9071BEC363297CAD14095FF03834FD7F0F7798777D79EE4ECB6D49ECDB47C90A5F1FBD3D49E80B38F527F67DD4B93B5653187E13DC4E2B70CCBB493F856072FC2E045AE4GFEA9C08EC0814076EE12E7FB9F2EAB06BBBDBCB26C708E09922FBFF865953D5E83AE5C9A94A5FF0FAFAACB92139AF289F60678F7B663996221EE4E47A0BC398E
	8A94238ED42DFE9EBC97F7535A781922C4BFD85E9D73015C6EE467C659EC2DDD574D4F07ECBA7E9906AF6B09617939111F61316C003A349E4A03A4F676328E4AC59E42BDDEC839D11907EC757C1964F37D67E74C677A2F4F1867F345E7274F67B61D1DBE1F33333DD48D70562C737CFD06F3C3F9D0471D3290CE72501D4F0CDD745D5DA460B2BDB4BFDC033887554F337034392E6EE8B468EF740EF4B0AF64F9CF965BAFE17FB59E4AFBDEE0E35AFB413F815C3DF406B579961DE1AD7C67794260DA733B3BDC4FB5
	CB20D74B05FAE3E7D8B521D596CE3359C1B43320DEB80538284368FB6837DFA8647BC0DE3FA02E9E75097789317A2F4755A33F070B0D7525FB0C7545067EDC36077FC43295BC1CA9C81F1D19749B4836D3DCA6C9FB26BDFBCF614C7C4BD62E7713C3AEA155770E1DA6110772501DCE548D4E258527489E7FE9C5FB64C0F99D4046BE327D44FA327D8E408D74517963975713FE67745198BFE4ED17C379D900823D6D2A92EADB5ECF36F831B451965F339AF5F938B451967BAD46FA39B4519627637AFF2CB451966F
	AD62755A1D73F012BBE79653B85FDFE27B989C3B231FFCFE2465D573FEF1310E772FD1696CE0E63F2E23906AE8A914D7G2C6D27F977D536B7A8BA8DBABBCD38FF5577DB07CED36CDBA960778B33213CGE0A940CA3D6EB9DE370F5F356377GF8B74F6BC605443D917625778506771527EF6C2CE136219D27711EA9B40C5FFA9977D3369B981F7CA2FEADFFCFA13B87064019F79A3FED7E1BA1B4F94D9954571A42EDA9465AAA6C374677FF40A768F5A1CBFCDD0E9F6B787B2F512C1A1CF7CD394BCA853C5F1CDB
	6C7748CD77636BDA56EE74352BE7783AD628639FE778D986BC1F6B85FA1C1F83F579E7686E6C5BEC2E8C1C4D36CD822481E4GE481BCFB16563AFB4F62B758536EABBDF2248396C34930FCF1795A9D37760D7117CAA81EAE7E77CF89CB6EEACEFA5F11FFD67BCEE800BFABD65FD308CFAE5FF8G65EB03BABF40AC00F9G0B81168F10FCBFB82B5DF161F1CE032AFAFD81760507FEB89494A7E2E8CC9639ECCE27D136DC98EF5DC0EC5E141ECB162B6CE75FEDEEBF17E83BFA062FB817E83B072CE43B9A28CBBFC7
	737E5A14BB369B7339775EE23FE5B75D0F4B65ADC17E8B2648BB4A70256FA64A7B93FDDDAA073A49F7C95EEF175C9A5FDF31D13F1CF7E3B649535E0B4EC3DCBEEFF62EFCE04A393F919C671359579BC67E6B4FF3392CBA7E82433B8DF8CD5E90FDEBB188F54B4ED3DE7E2D2229B9FF3EFEFFD8B118ED2B8788GDC9D64E74EB9035866ABA54DDA1BB903D3F924BC582AC18634D98A709B2D7C58E04C9E1C0FBBA6103FF676DD8C3F4E647846BE7C76897785F4494971DFE9405C38073E5550AAF95D3A1731CFGB6
	9854074F772F1DB55EE517FDD832908F097C6F70F219AA3D315C100F11F54E180359F7F71966E07967AEDB7CDA5FD906B14A466CD6EE77C7F78B3A8FBCE5F8376C7EEB1EC11A7B6A9FEEAB8DAD5E8DA75F3BBCBFEC3EF7499FB65F3B7C4F7074E7756BC712FF233BE944780DAE0F57AD23185BAD853B2C84A8GA89B4A366D9822D87D0FA3FF94D82CB60953AD7E91BCA741DFA6287AD9F9A0392CF786D27F1E617D817457A199FCB5046393FECEG6D254A91066438FB822823E8F0A4207226F48E93E0676862B0
	C3737C2F05656975A017E708F242FDAE44CDC2B9CD474DF0D11E38E0086260612218CF369A5E3FE63D7D7E39667F0146C8C9CC2608617D676BEFBF6D17CBEEA78DEE53D5EC6F318E6CD966A3BB3F64233D798E9DA77B487615BA6E3E6B081B8165F49D67D6295DC29F7948C8989D23C0AF0F3172B6BF15FF9ECAA6A77679AB69B21C0C467F65BE12002F1D76E24A95EAC136081F76F255D5F15F6370730BD6C91E685016D3ED9F97A8267DEB061F76F19FE4E366C2B99F20CC243D6E4FCA680E2782F02F8BB837A1
	FF9168DF5EBBD3F134476812701D2FD35F5AC86B5476FF39D7E8FE2A597226E03E568D0A5C9619E2EC1D5A816B940F15CDA19C0D47183D41BF417314F75474E672982820EBE8D474299E6F08C03176CC98F309B5B0F651F0362D82209E4083A08D6B771E410AE4638212AB13455C28B7C83FB9689CCD9EF3BD2329E36E39D1761DB544CA4F2508F6CEBDB40A3910BCBC2C08FAEC9F99AD53F16D0ADBD602F83E4D730C81A7369F12236CA00D67CE0A13F22799A0DFBA40B20015GF8E74F7BF538586FG028AE47D
	CA90EF040BC658F9210B7CA4EF18722C8F39A84FEA9BB14F33A807C23B8FC01B48C8AC4F1AB91218E72D33E13E92E19F77C6C5FFCF70B22EA11C7E268D6CFBF5F81F8B40FDEA9017CD0173F2172FD9C8BED70CF490164398399AAF6CDB50A93CAC3B0EFA8885434150F808F1427948E171C788E45BB686C88FA3AA512E8D186B41EA008EE8B3980869C18DA4527ED97FCB02713C39A1AAFA690CF9FE58F876B3A3406F21B38294575EAA3E35BDC24DAD768B5DF4BF3E9708303D4F52C0621E689243E7859277C4F3
	75B837826AEA03A4EFC15C1EE8AE60B3035347EFBE77CFA8E833E550B69720E8B45B369D2092208DE0GA082B093E0AE40E200AC009C00BCG61C2362D9C2062824509B9921E07A60B935A41F1ACD088B407FD616431E2B41CBAD6A8E1737C6CD2F8EAFE761730F9FEB6B3EC4C4F383ECF6E25BD59A110D13DC06B4A3B139863E367336CC05693D4A5515F4802CD23688DEBEB4BBDFB755F35DC2035650ECA763B96A897208EA50ABBD7DC58EE58ABA94897DFA766CD9877C4359B6EB07DDD9802C362B62F571E85
	3F79F021F6BE6FA29EA6259BDD33725E6171E5EC1C15587B2289C11F1F969FFECB9396A76C7A07684DC14844710737952E7D55879C762A6ACA74071BDBA33FBB99FB5FF0B9C66F99657564745E2E5528F1F953D512B77326655DF3C590688C7911EBFF90CC4F0275004469BD9D26F360B5924D490B061C753A61FDFE716D4FDFBFFFDBE970FCF3EBA94AEC8F719845F15B982ED643E9EB3612FCEE871554F3BB2A184F6DC9E56A5C3E26184F6D77A846392D5D3933EF42C6A0A6756B6D5E2763CE6B38FFE0E3A22E
	AF68D783BABE1D710578B69189927EA30C2F89066B27EFBF8C38539AAEC3415C84C48E48D16AFB1F027979F0D012F879532CCDB08C290D2E0F7B990F410857407767F55CE95D877A7626DA13BCA13373AEB1E53F57CCFD6EA3C97BC5C2D35722190063EB5D8BAC962607925740DFB37C02D0629AB82B125640CC28AB8F539976B72E30FDB3146704E9BD6AD07160A659FA640E4A91B12ADE62D876CBD0BA0FF799E5D98C63E50527DFCFF93FFF0ABBAB4C01BEF9B7546F1B6169F4DEA4CF9D0347A76FDB9B19FFC0
	4A6B1518FBF27C7649CDB1BC392776AD5403DBF91D948C0BCD7C2FB188B4FFBF4B562AEDC02FD626FD6506CA44F5C0F9D056738D3C8A50F8AFF6455E5FDFF97BE35BA7EEAB8D6EB74FDC41F77DBC0B3B867D9E7DCAF9CC37414B6C2721BD50B0BC4275752C0B62A27E2011633E672238781C15F00B55297173815598BFB96E27E15A57CBB265894B77626F18D5CCB80E0DABEAF0B86863BFD15572736F68B9C71A5E670D4AE26AA3507AFA7C9A5A0EFFA332D126FD51B2683392A09FE0DB84FCA1C2E37CEA6FDB70
	B4AC05CE3E37F29AF39FE7D34B89CFCBB76D2D2BE46225BFC2F74323F2625D705F8A463B5D39F2625DF0E1DC7D23F2625D700F636E0E05C8625DB07567BE34DFB64E37B460EBC1E47AF94AEDF1C2A61BA1BD0E6B1149E6A8837F9BCE2D239254AF7C21D02C8A3F63C766F67CF84C5E54F34A8E61DDF4BA1D2AE0538F47D521843F36E158928CC77026C4054DFA26534E16005C0B55F5565577DAADC2D13B301EAF096DC2912EEB6DC2B1DB765A85ABAC091378A7D3627E2905C78E855410F48428F99551AE624F5C
	6CFE2F6A35ABE2449B752A225FBEF4496E13C3A12F62302B5EA178DF0E5A150857A7AACE3B3DC91646C3E13B4B2E8464F1498FAD43AAA471F6B5A05A71B3EBEC536275856CD1F9429E95E1832B60CA4FE9F8D5E8077FC143BD9199F2FFE86D011E6CB7532CCB889CD99F544709053DB86E06C3C351A36C3F16C9262E3A988687F4583996DD74D2452AAA1D15B617332A323A52D6FD40D9DDDBD52B21DD002E2E59E72BFE5BD9FD207A00062D4246082E29F1D6D757B0AC18E143213DCCEF4A18709781D767B8GA3
	3DBCB59DEDF87F53CFADBC5FDCEE91ACE02700488DD3BC01811A1966BB02307EE1FD44D2A3B0ECA9EFA3B41BD3D505B27C4BB7769EB19A9495BBFE4C2A7D96703AF12F10FF27AF69DEA1C7617E1E27A47E96701590D2F7283087D4E99D8DCAFB046467CABDA12FA429DE69C2C3580FE7CC0D17D4B1F6767FDA8847D7280DD6496B7E9432E971047A7CAFD0CB8788AC0A25B1A89AGGE0CDGGD0CB818294G94G88G88GC9FBB0B6AC0A25B1A89AGGE0CDGG8CGGGGGGGGGGGGGGGGG
	E2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE29AGGGG
**end of data**/
}
}