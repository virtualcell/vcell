package cbit.vcell.modelopt.gui;
import java.io.File;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.FileFilters;
import org.vcell.util.gui.TitledBorderBean;
import org.vcell.util.gui.UtilCancelException;
import org.vcell.util.gui.VCFileChooser;

import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.export.CSV;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.util.RowColumnResultSet;
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
	private ParameterEstimationTask fieldParameterEstimationTask = null;
	private int timeIndex = -1;

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
			if (evt.getPropertyName().equals("modelObject")) {
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
 * connEtoC1:  (referenceData1.this --> ReferenceDataPanel.updatePlot()V)
 * @param value cbit.vcell.opt.ReferenceData
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(ReferenceData value) {
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
			TitledBorderBean ivjLocalBorder = new TitledBorderBean();
			ivjLocalBorder.setTitleJustification(javax.swing.border.TitledBorder.CENTER);
			ivjLocalBorder.setTitle("Time Series Data");
			ivjmultisourcePlotPane = new MultisourcePlotPane();
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
public ReferenceData getReferenceData() {
	return fieldReferenceData;
}


/**
 * Return the referenceData1 property value.
 * @return cbit.vcell.opt.ReferenceData
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReferenceData getreferenceData1() {
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
private ReferenceData importDataFromFile() throws UserCancelException, Exception {
	VCFileChooser fileChooser = new VCFileChooser();
	fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_CSV);
	// Set the default file filter...
	fileChooser.setFileFilter(FileFilters.FILE_FILTER_CSV);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
	String defaultPath = null;
	if (getUserPreferences()!=null){
	    defaultPath = getUserPreferences().getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
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
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			if (getUserPreferences()!=null){
				String newPath = selectedFile.getParent();
				if (!newPath.equals(defaultPath)) {
					getUserPreferences().setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPath);
				}
			}
			CSV csv = new CSV();
			RowColumnResultSet rowColumnResultSet = csv.importFrom(new java.io.FileReader(selectedFile));
			double[] weights = new double[rowColumnResultSet.getDataColumnCount()];
			java.util.Arrays.fill(weights, 1.0);
			ReferenceData referenceData = new SimpleReferenceData(rowColumnResultSet, weights);
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
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		
		String dataString = "SimpleReferenceData { 3 2 t Ca_er 1 1 0 0.0 0.1 1 0.2 3 }";
		cbit.vcell.opt.SimpleReferenceData refData = cbit.vcell.opt.SimpleReferenceData.fromVCML(new org.vcell.util.CommentStringTokenizer(dataString));
		aReferenceDataPanel.setReferenceData(refData);
		System.out.println("setting 1");
		Thread.sleep(3000);
		aReferenceDataPanel.setReferenceData(null);
		System.out.println("setting 2");
		Thread.sleep(3000);
		aReferenceDataPanel.setReferenceData(refData);
		System.out.println("setting 3");
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
private void setReferenceData(ReferenceData referenceData) {
	ReferenceData oldValue = fieldReferenceData;
	fieldReferenceData = referenceData;
	firePropertyChange("referenceData", oldValue, referenceData);
}


/**
 * Set the referenceData1 to a new value.
 * @param newValue cbit.vcell.opt.ReferenceData
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setreferenceData1(ReferenceData newValue) {
	if (ivjreferenceData1 != newValue) {
		try {
			ReferenceData oldValue = getreferenceData1();
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
public void setUserPreferences(UserPreferences userPreferences) {
	UserPreferences oldValue = fieldUserPreferences;
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
		geteditorTextArea().setText(((SimpleReferenceData)getReferenceData()).getCSV());
	}else{
		geteditorTextArea().setText("t, data1, data2\n0.0, 0.1, 0.21\n0.1, 0.15, 0.31\n0.2, 0.16, 0.44");
	}
	geteditorPanel().setPreferredSize(new java.awt.Dimension(600,600));
	geteditorPanel().setMinimumSize(new java.awt.Dimension(600,600));
	try {
		int retVal = DialogUtils.showComponentOKCancelDialog(this,geteditorPanel(),"time series data editor");
		if (retVal == javax.swing.JOptionPane.OK_OPTION){
			RowColumnResultSet rc = (new CSV()).importFrom(new java.io.StringReader(geteditorTextArea().getText()));
			double weights[] = new double[rc.getDataColumnCount()];
			java.util.Arrays.fill(weights,1.0);
			SimpleReferenceData simpleRefData = new SimpleReferenceData(rc,weights);
			setReferenceData(simpleRefData);
		}
	}catch (UtilCancelException e){
	}catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this,e.getMessage());
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
	DialogUtils.showInfoDialog(this, message);
}


/**
 * Comment
 */
private ReferenceData subsample() {
	ReferenceData refData = getReferenceData();
	if (refData==null){
		return refData;
	}
	
	RowColumnResultSet rc = new RowColumnResultSet();
	String[] columnNames = refData.getColumnNames(); 
	for (int i = 0; i < columnNames.length; i++){
		rc.addDataColumn(new ODESolverResultSetColumnDescription(i == timeIndex ?  ReservedSymbol.TIME.getName() : columnNames[i]));		
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
		SimpleReferenceData srd = new SimpleReferenceData(rc,weights);
		return srd;
	}catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this, e.getMessage());
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
	dataSources[0] = new DataSource.DataSourceReferenceData("refData", timeIndex < 0 ? 0 : timeIndex, getReferenceData());
	getmultisourcePlotPane().setDataSources(dataSources);

	getmultisourcePlotPane().selectAll();

}

public void setParameterEstimationTask(ParameterEstimationTask parameterEstimationTask) {
	ParameterEstimationTask oldValue = fieldParameterEstimationTask;
	fieldParameterEstimationTask = parameterEstimationTask;
	
	if (oldValue!=null){
		ReferenceDataMappingSpec[] refDataMappingSpecs = oldValue.getModelOptimizationSpec().getReferenceDataMappingSpecs();
		if (refDataMappingSpecs != null) {
			for (ReferenceDataMappingSpec refDataMappingSpec : refDataMappingSpecs){
				refDataMappingSpec.removePropertyChangeListener(ivjEventHandler);
			}
		}
	}
	if (fieldParameterEstimationTask!=null){
		ReferenceDataMappingSpec[] refDataMappingSpecs = fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpecs();
		if (refDataMappingSpecs != null) {
			for (ReferenceDataMappingSpec refDataMappingSpec : refDataMappingSpecs){
				refDataMappingSpec.addPropertyChangeListener(ivjEventHandler);
			}
		}
	}
	
	timeIndex = fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceDataTimeColumnIndex();
	setReferenceData(fieldParameterEstimationTask.getModelOptimizationSpec().getReferenceData());
	updatePlot();
}


}