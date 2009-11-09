package cbit.vcell.client.data;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.vcell.util.VCDataIdentifier;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.util.ColumnDescription;
/**
 * This type was created in VisualAge.
 */
public class NewODEExportPanel extends JPanel implements ExportConstants {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private JButton ivjJButtonExport = null;
	private JComboBox ivjJComboBox1 = null;
	private JLabel ivjJLabelFormat = null;
	private JPanel ivjJPanelExport = null;
	private cbit.vcell.export.ExportSettings ivjExportSettings1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.solver.ode.ODESolverResultSet fieldOdeSolverResultSet = null;
	private DefaultListModel ivjDefaultListModel1 = null;
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel2 = null;
	private JList ivjJList1 = null;
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanel2 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JSlider ivjJSlider1 = null;
	private JSlider ivjJSlider2 = null;
	private JTextField ivjJTextField1 = null;
	private JTextField ivjJTextField2 = null;
	private int[] plottableColumnIndices = new int[0];
	private String[] plottableNames = new String[0];
	private double[] times = new double[0];
	private javax.swing.JInternalFrame waitWindow = null;
	private org.vcell.util.VCDataIdentifier fieldVcDataIdentifier = null;
	private cbit.vcell.client.DataViewerManager fieldDataViewerManager = null;
	private boolean ivjConnPtoP2Aligning = false;
	private ODESolverResultSet ivjodeSolverResultSet1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == NewODEExportPanel.this.getJTextField1()) 
				connEtoC5(e);
			if (e.getSource() == NewODEExportPanel.this.getJTextField2()) 
				connEtoC6(e);
			if (e.getSource() == NewODEExportPanel.this.getJButtonExport()) 
				connEtoC9(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == NewODEExportPanel.this.getJTextField1()) 
				connEtoC7(e);
			if (e.getSource() == NewODEExportPanel.this.getJTextField2()) 
				connEtoC8(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == NewODEExportPanel.this.getJComboBox1()) 
				connEtoM6(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == NewODEExportPanel.this && (evt.getPropertyName().equals("odeSolverResultSet"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == NewODEExportPanel.this.getodeSolverResultSet1() && (evt.getPropertyName().equals("columnDescriptions"))) 
				connEtoC11(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == NewODEExportPanel.this.getJSlider1()) 
				connEtoC3(e);
			if (e.getSource() == NewODEExportPanel.this.getJSlider2()) 
				connEtoC4(e);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public NewODEExportPanel() {
	super();
	initialize();
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 * @param listener java.beans.PropertyChangeListener
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * connEtoC10:  (odeSolverResultSet1.this --> NewODEExportPanel.updateChoices(Lcbit.vcell.solver.ode.ODESolverResultSet;)V)
 * @param value cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(cbit.vcell.solver.ode.ODESolverResultSet value) {
	try {
		// user code begin {1}
		// user code end
		this.updateChoices(getodeSolverResultSet1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (odeSolverResultSet1.functionColumnDescriptions --> NewODEExportPanel.updateChoices(Lcbit.vcell.solver.ode.ODESolverResultSet;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getodeSolverResultSet1() != null)) {
			this.updateChoices(getodeSolverResultSet1());
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
 * connEtoC2:  (ODEExportPanel.initialize() --> ODEExportPanel.initFormatChoices()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.initFormatChoices();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JSlider1.change.stateChanged(javax.swing.event.ChangeEvent) --> ODEExportPanel.setTimeFromSlider(ILjavax.swing.JTextField;)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromSlider(getJSlider1().getValue(), getJTextField1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JSlider2.change.stateChanged(javax.swing.event.ChangeEvent) --> ODEExportPanel.setTimeFromSlider(ILjavax.swing.JTextField;)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromSlider(getJSlider2().getValue(), getJTextField2());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1(), getJSlider1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JTextField2.action.actionPerformed(java.awt.event.ActionEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField2(), getJSlider2());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JTextField1.focus.focusLost(java.awt.event.FocusEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1(), getJSlider1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (JTextField2.focus.focusLost(java.awt.event.FocusEvent) --> ODEExportPanel.setTimeFromTextField(Ljavax.swing.JTextField;Ljavax.swing.JSlider;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField2(), getJSlider2());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (JButtonExport.action.actionPerformed(java.awt.event.ActionEvent) --> ODEExportPanel.startExport()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.startExport();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ODEExportPanel.initialize() --> ExportSettings1.simDataType)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getExportSettings1().setSimDataType(this.dataType());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (JComboBox1.item.itemStateChanged(java.awt.event.ItemEvent) --> ExportSettings1.selectedFormat)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getExportSettings1().setSelectedFormat(getJComboBox1().getSelectedIndex());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (DefaultListModel1.this <--> JList1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJList1().setModel(getDefaultListModel1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (NewODEExportPanel.odeSolverResultSet <--> odeSolverResultSet1.this)
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
 * connPtoP2SetTarget:  (NewODEExportPanel.odeSolverResultSet <--> odeSolverResultSet1.this)
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
 * Comment
 */
public int dataType() {
	return ODE_SIMULATION;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 * @param propertyName java.lang.String
 * @param oldValue java.lang.Object
 * @param newValue java.lang.Object
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the dataViewerManager property (cbit.vcell.client.DataViewerManager) value.
 * @return The dataViewerManager property value.
 * @see #setDataViewerManager
 */
public cbit.vcell.client.DataViewerManager getDataViewerManager() {
	return fieldDataViewerManager;
}


/**
 * Return the DefaultListModel1 property value.
 * @return javax.swing.DefaultListModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultListModel getDefaultListModel1() {
	if (ivjDefaultListModel1 == null) {
		try {
			ivjDefaultListModel1 = new javax.swing.DefaultListModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModel1;
}


/**
 * Return the ExportSettings1 property value.
 * @return cbit.vcell.export.ExportSettings
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.export.ExportSettings getExportSettings1() {
	if (ivjExportSettings1 == null) {
		try {
			ivjExportSettings1 = new cbit.vcell.export.ExportSettings();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportSettings1;
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.ExportSpecs
 */
private ExportSpecs getExportSpecs() {
	Object[] selections = getJList1().getSelectedValues();
	String[] names = new String[selections.length];
	for (int i = 0; i < selections.length; i++){
		names[i] = (String)selections[i];
	}
	VariableSpecs variableSpecs = new VariableSpecs(names, ExportConstants.VARIABLE_MULTI);
	TimeSpecs timeSpecs = null;
	if(getOdeSolverResultSet().isMultiTrialData())
		timeSpecs = new TimeSpecs(0, (getOdeSolverResultSet().getRowCount()-1), times, ExportConstants.TIME_RANGE);
	else
		timeSpecs = new TimeSpecs(getJSlider1().getValue(), getJSlider2().getValue(), getTimes(), ExportConstants.TIME_RANGE);
	return new ExportSpecs(
		getVcDataIdentifier(),
		getExportSettings1().getSelectedFormat(),
		variableSpecs,
		timeSpecs,
		null,
		getExportSettings1().getFormatSpecificSpecs()
	);
}


/**
 * Return the JButtonExport property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonExport() {
	if (ivjJButtonExport == null) {
		try {
			ivjJButtonExport = new javax.swing.JButton();
			ivjJButtonExport.setName("JButtonExport");
			ivjJButtonExport.setText("Start Export");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonExport;
}


/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBox1() {
	if (ivjJComboBox1 == null) {
		try {
			ivjJComboBox1 = new javax.swing.JComboBox();
			ivjJComboBox1.setName("JComboBox1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBox1;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Variables:");
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Time interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}


/**
 * Return the JLabelFormat property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFormat() {
	if (ivjJLabelFormat == null) {
		try {
			ivjJLabelFormat = new javax.swing.JLabel();
			ivjJLabelFormat.setName("JLabelFormat");
			ivjJLabelFormat.setPreferredSize(new java.awt.Dimension(100, 15));
			ivjJLabelFormat.setText("Export Format:");
			ivjJLabelFormat.setMaximumSize(new java.awt.Dimension(100, 15));
			ivjJLabelFormat.setHorizontalAlignment(SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFormat;
}

/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJList1() {
	if (ivjJList1 == null) {
		try {
			ivjJList1 = new javax.swing.JList();
			ivjJList1.setName("JList1");
			ivjJList1.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJList1;
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
			ivjJPanel1.setPreferredSize(new java.awt.Dimension(200, 200));
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());
			ivjJPanel1.setMinimumSize(new java.awt.Dimension(50, 50));

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.gridwidth = 2;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 0; constraintsJTextField1.gridy = 1;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsJTextField2 = new java.awt.GridBagConstraints();
			constraintsJTextField2.gridx = 0; constraintsJTextField2.gridy = 2;
			constraintsJTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField2.weightx = 1.0;
			constraintsJTextField2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField2(), constraintsJTextField2);

			java.awt.GridBagConstraints constraintsJSlider1 = new java.awt.GridBagConstraints();
			constraintsJSlider1.gridx = 1; constraintsJSlider1.gridy = 1;
			constraintsJSlider1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJSlider1.weightx = 1.0;
			constraintsJSlider1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJSlider1(), constraintsJSlider1);

			java.awt.GridBagConstraints constraintsJSlider2 = new java.awt.GridBagConstraints();
			constraintsJSlider2.gridx = 1; constraintsJSlider2.gridy = 2;
			constraintsJSlider2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJSlider2.weightx = 1.0;
			constraintsJSlider2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJSlider2(), constraintsJSlider2);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setPreferredSize(new java.awt.Dimension(200, 200));
			ivjJPanel2.setLayout(new java.awt.BorderLayout());
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(50, 50));
			getJPanel2().add(getJLabel1(), "North");
			getJPanel2().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}


/**
 * Return the JPanelExport property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelExport() {
	if (ivjJPanelExport == null) {
		try {
			ivjJPanelExport = new javax.swing.JPanel();
			ivjJPanelExport.setName("JPanelExport");
			ivjJPanelExport.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonExport = new java.awt.GridBagConstraints();
			constraintsJButtonExport.gridx = 2; constraintsJButtonExport.gridy = 0;
			constraintsJButtonExport.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanelExport().add(getJButtonExport(), constraintsJButtonExport);

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = 0;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.weightx = 1.0;
			constraintsJComboBox1.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanelExport().add(getJComboBox1(), constraintsJComboBox1);

			java.awt.GridBagConstraints constraintsJLabelFormat = new java.awt.GridBagConstraints();
			constraintsJLabelFormat.gridx = 0; constraintsJLabelFormat.gridy = 0;
			constraintsJLabelFormat.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelFormat.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanelExport().add(getJLabelFormat(), constraintsJLabelFormat);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelExport;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getJList1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}


/**
 * Return the JSlider1 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSlider1() {
	if (ivjJSlider1 == null) {
		try {
			ivjJSlider1 = new javax.swing.JSlider();
			ivjJSlider1.setName("JSlider1");
			ivjJSlider1.setPaintTicks(true);
			ivjJSlider1.setValue(0);
			ivjJSlider1.setMajorTickSpacing(10);
			ivjJSlider1.setSnapToTicks(true);
			ivjJSlider1.setMinorTickSpacing(1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSlider1;
}

/**
 * Return the JSlider2 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSlider2() {
	if (ivjJSlider2 == null) {
		try {
			ivjJSlider2 = new javax.swing.JSlider();
			ivjJSlider2.setName("JSlider2");
			ivjJSlider2.setPaintTicks(true);
			ivjJSlider2.setValue(100);
			ivjJSlider2.setMajorTickSpacing(10);
			ivjJSlider2.setSnapToTicks(true);
			ivjJSlider2.setMinorTickSpacing(1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSlider2;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			ivjJTextField1.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjJTextField1.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField2() {
	if (ivjJTextField2 == null) {
		try {
			ivjJTextField2 = new javax.swing.JTextField();
			ivjJTextField2.setName("JTextField2");
			ivjJTextField2.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjJTextField2.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField2;
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
 * Accessor for the propertyChange field.
 * @return java.beans.PropertyChangeSupport
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2001 12:48:27 AM)
 * @return double[]
 */
private double[] getTimes() {
	return times;
}


/**
 * Gets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @return The vcDataIdentifier property value.
 * @see #setVcDataIdentifier
 */
public org.vcell.util.VCDataIdentifier getVcDataIdentifier() {
	return fieldVcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:09:09 PM)
 * @return javax.swing.JInternalFrame
 */
private javax.swing.JInternalFrame getWaitWindow() {
	return waitWindow;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBox1().addItemListener(ivjEventHandler);
	getJSlider1().addChangeListener(ivjEventHandler);
	getJSlider2().addChangeListener(ivjEventHandler);
	getJTextField1().addActionListener(ivjEventHandler);
	getJTextField2().addActionListener(ivjEventHandler);
	getJTextField1().addFocusListener(ivjEventHandler);
	getJTextField2().addFocusListener(ivjEventHandler);
	getJButtonExport().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Comment
 */
private void initFormatChoices() {
	if (getJComboBox1().getItemCount() > 0) getJComboBox1().removeAllItems();
	getJComboBox1().addItem("Comma delimited ASCII files (*.csv)");
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		org.vcell.util.gui.LineBorderBean ivjLocalBorder1;
		ivjLocalBorder1 = new org.vcell.util.gui.LineBorderBean();
		ivjLocalBorder1.setLineColor(java.awt.Color.blue);
		org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
		ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
		ivjLocalBorder.setBorder(ivjLocalBorder1);
		ivjLocalBorder.setTitle("Select data to be exported");
		setName("ODEExportPanel");
		setBorder(ivjLocalBorder);
		setLayout(new java.awt.GridBagLayout());
		setPreferredSize(new java.awt.Dimension(400, 200));
		setSize(638, 192);
		setMinimumSize(new java.awt.Dimension(200, 100));

		java.awt.GridBagConstraints constraintsJPanelExport = new java.awt.GridBagConstraints();
		constraintsJPanelExport.gridx = 2; constraintsJPanelExport.gridy = 1;
		constraintsJPanelExport.gridwidth = 2;
		constraintsJPanelExport.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelExport.weightx = 1.0;
		constraintsJPanelExport.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getJPanelExport(), constraintsJPanelExport);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 2; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 3; constraintsJPanel2.gridy = 0;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);
		initConnections();
		connEtoC2();
		connEtoM1();
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
		JFrame frame = new javax.swing.JFrame();
		NewODEExportPanel aNewODEExportPanel;
		aNewODEExportPanel = new NewODEExportPanel();
		frame.setContentPane(aNewODEExportPanel);
		frame.setSize(aNewODEExportPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 * @param listener java.beans.PropertyChangeListener
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Sets the dataViewerManager property (cbit.vcell.client.DataViewerManager) value.
 * @param dataViewerManager The new value for the property.
 * @see #getDataViewerManager
 */
public void setDataViewerManager(cbit.vcell.client.DataViewerManager dataViewerManager) {
	cbit.vcell.client.DataViewerManager oldValue = fieldDataViewerManager;
	fieldDataViewerManager = dataViewerManager;
	firePropertyChange("dataViewerManager", oldValue, dataViewerManager);
}


/**
 * Sets the odeSolverResultSet property (cbit.vcell.solver.ode.ODESolverResultSet) value.
 * @param odeSolverResultSet The new value for the property.
 * @see #getOdeSolverResultSet
 */
public void setOdeSolverResultSet(cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet) {
	cbit.vcell.solver.ode.ODESolverResultSet oldValue = fieldOdeSolverResultSet;
	fieldOdeSolverResultSet = odeSolverResultSet;
	firePropertyChange("odeSolverResultSet", oldValue, odeSolverResultSet);
	updateUI(odeSolverResultSet);
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
			connEtoC10(ivjodeSolverResultSet1);
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
 * Comment
 */
private void setTimeFromSlider(int sliderPosition, JTextField textField) {
	double timepoint = getTimes()[sliderPosition];
	textField.setText(Double.toString(timepoint));
	textField.setCaretPosition(0);
}


/**
 * Comment
 */
private void setTimeFromTextField(JTextField textField, JSlider slider) {
	int oldVal = slider.getValue();
	double time = 0;
	if(textField.isEnabled())
	{
		try {
			time = Double.parseDouble(textField.getText());
		} catch (NumberFormatException e) {
			// if typedTime is crap, put back old value
			textField.setText(Double.toString(times[oldVal]));
			return;
		}
		// we find neighboring time value; if out of bounds, it is set to corresponding extreme
		// we correct text, then adjust slider; change in slider will fire other updates
		int val = 0;
		if (time > times[0]) {
			if (time >= times[times.length - 1]) {
				val = times.length - 1;
			} else {
				for (int i=0;i<times.length;i++) {
					val = i;
					if ((time >= times[i]) && (time < times[i+1]))
						break;
				}
			}
		}
		textField.setText(Double.toString(times[val]));
		slider.setValue(val);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2001 12:48:27 AM)
 * @param newTimes double[]
 */
private void setTimes(double[] newTimes) {
	times = newTimes;
}


/**
 * Sets the vcDataIdentifier property (cbit.vcell.server.VCDataIdentifier) value.
 * @param vcDataIdentifier The new value for the property.
 * @see #getVcDataIdentifier
 */
public void setVcDataIdentifier(org.vcell.util.VCDataIdentifier vcDataIdentifier) {
	VCDataIdentifier oldValue = fieldVcDataIdentifier;
	fieldVcDataIdentifier = vcDataIdentifier;
	firePropertyChange("vcDataIdentifier", oldValue, vcDataIdentifier);
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2001 6:09:09 PM)
 * @param newWaitWindow javax.swing.JInternalFrame
 */
private void setWaitWindow(javax.swing.JInternalFrame newWaitWindow) {
	waitWindow = newWaitWindow;
}


/**
 * Comment
 */
private void startExport() {
	boolean okToExport = getExportSettings1().showFormatSpecificDialog(JOptionPane.getFrameForComponent(this));
			
	if (!okToExport) {
		return;
	}
	// pass the request down the line; non-blocking call
	getDataViewerManager().startExport(getExportSpecs());
}


/**
 * Insert the method's description here.
 * Creation date: (2/8/2001 4:56:15 PM)
 * @param cbit.vcell.solver.ode.ODESolverResultSet
 */
private synchronized void updateChoices(ODESolverResultSet odeSolverResultSet) {

	if (odeSolverResultSet == null) {
		return;
	}
	
	// find out how many plottable datasets we have
	int plottable = 0;
	for (int i=0;i<odeSolverResultSet.getColumnDescriptionsCount();i++) {
		ColumnDescription cd = (ColumnDescription)odeSolverResultSet.getColumnDescriptions(i);
		if(!cd.getName().equals("TrialNo"))
		{
			if (cd.getParameterName() == null) {
				plottable++; // not a parameter sensitivity
			}
		}
	}
	
	// now store their indices
	int[] indices = new int[plottable];
	plottable = 0;
	for (int i=0;i<odeSolverResultSet.getColumnDescriptionsCount();i++) {
		ColumnDescription cd = (ColumnDescription)odeSolverResultSet.getColumnDescriptions(i);
		if(!cd.getName().equals("TrialNo"))
		{
			if (cd.getParameterName() == null) {
				indices[plottable] = i;
				plottable++;
			}
		}
	}
	setPlottableColumnIndices(indices);
	
	// now store their names
	String[] names = new String[plottable];
	for (int i=0;i<plottable;i++) {
		ColumnDescription column = odeSolverResultSet.getColumnDescriptions(indices[i]);
		if (column instanceof ODESolverResultSetColumnDescription){
			names[i] = ((ODESolverResultSetColumnDescription)column).getVariableName();
		}else{
			names[i] = column.getName();
		}
	}
	setPlottableNames(names);

	// get and store times
	if(odeSolverResultSet.isMultiTrialData())
	{
		try {
			setTimes(odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn("TrialNo")));
		}catch (cbit.vcell.parser.ExpressionException e){
			e.printStackTrace(System.out);
		}
	}
	else
	{
		try {
			setTimes(odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn(ODESolverResultSet.TIME_COLUMN)));
		}catch (cbit.vcell.parser.ExpressionException e){
			e.printStackTrace(System.out);
		}
	}
	// finally, update widgets
	getDefaultListModel1().removeAllElements();
	for (int i=0;i<plottable;i++) {
		getDefaultListModel1().addElement(names[i]);
	}
	if(!odeSolverResultSet.isMultiTrialData())
	{
		getJSlider1().setMaximum(times.length - 1);
		getJSlider2().setMaximum(times.length - 1);
		getJSlider1().setValue(0);
		getJSlider2().setValue(times.length - 1);
		getJTextField1().setText(Double.toString(times[0]));
		getJTextField2().setText(Double.toString(times[times.length - 1]));
	}
}

private void updateUI(ODESolverResultSet resultSet)
{
	if(resultSet != null)
	{	
		if(resultSet.isMultiTrialData())
		{
			getJSlider1().setEnabled(false);
			getJSlider2().setEnabled(false);
			getJTextField1().setEnabled(false);
			getJTextField2().setEnabled(false);
		}
		else
		{
			getJSlider1().setEnabled(true);
			getJSlider2().setEnabled(true);
			getJTextField1().setEnabled(true);
			getJTextField2().setEnabled(true);
		}
	}
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC3FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8FDCD5D536B02596A916959516955350BC5E446B59BC261C49190FE9181E73726BB1BC1BEC32791CE2DEB64FDEFC0DFD1FD33E576182860A0AC9C90A0AC505C58802C528491F0BE1211210E1D2F1611EFB6F017B0FFB8F820A7A2D7DE76DFB6E3967F21177FC6F7779EBF56ED9FB2D3D7E6C3577DEFB1FFD8ED2510E785CACD311A4E565CA7C5FC7D912D4F1DE1272B78FFE02383AF75373A54D3F1D
	G9B256D9DB9F0ED05321804747CC2697D6ABC386F067B566F2567EF065F1B242A6A252370A30C1CF9D07603550DC9634B39EE8D4AF9055E37D98B603A9E2087F0641A74AE11EF3596F37CD40EC748B2C9D2F4B92139E0AD6138CC2049831C99202B1C51FB614A9418D6DF47693A2FCD14D67C034F340E54232993A10D732E6057D269452CB93B082CC42AA7612900FBABGB1BEDB4AA829046B7E76817BA7FD3D0ADAF93ACFF6BAAB7B1C0A6CD6AB2D9655D2DFFFD49EBEE6EEE99971FAFCEA3B45ADBBA52978BD
	D2E726B50F56A1C9CB213E840A6BF491B95D70DB8508F98F71BBBC843F8CFECB6F2767973F0F369DF8F7BF573D6B6CFD5222BD2E1C8DE1EC2BFA1F595475FE4036A8579326B7E8BB242FA57E0E0532F9G8B81B2818AAB52732BG365276FFEC4DFBF0ED6CD51BBDAE17475D6875B6B92D3EA39E2B6C045F7575D042F14765BE0F4FAAC92C0D6F36D69868B39EB03E79974D47FC12D8036D1DF47CEEE9495F5DE8FA4540A7CB924F1ADAAB18AFFAAA82BEE132DF88ABBB2AA2B89E03E5FFA5E9E5672C7BEDF62E01
	6C52979F490E6575A4EAE4E7D41036DEC0E3E0817C4E6275B27CA394DF7EC1G4FE273EB0AA777E6A8CB7CGE323EBBD6A5216F307D4D8FD13219F629E08B195D4B29D4AAA753A5C65A5F537D7B2197B04AE3F2178B48D1E6972B60DD3F25F3EBBBD3F22BABD1F4557636BC8DF4D2BC94FAF83588F508920824C82C8284198FBF36D6E8946D83B4527B68DA9CE2B6C13986F63EBDF072BBC227AAC075DFE55626E13CFD8FC0A2557A97BA5CE1331167820B7086F999A7BFDA063D359AD7BAC2A62F1C36D96172C4A
	BEBF23F9FE6DC6C26313E9E9276212C1004BABE13DAF2E5D84D727452FFE6905EEAF87A8D83929003449E94B892891AA005F4CAFF99B083FD2417EA5G4B059F4E95107ABE17FDB0DE545737F9BC83C35ECEE209343F167839C0A6E307843F0BEB5147B90584978577F135D8CFF2412B1AFA4CCA9FD15F629B45BE76302B1044AA50AFG48G48590B71732FDD98BF66F5FFA3D5BCF203E17C44C2AC176DE5F133FF2F3E9FBDCE6BA7BADA21ECAAC042DE9C7BFBDCDDA44EEB0D7B938251A9A9820D9EF2040CD1AA
	A3B782BD214D3E087A313FB33F7BB652B68419CB81248F51774F951432D8EE3278E50C5EE3BE88BC599AE8892667BDDE952EEF813C8DC0467035348EC95A4C65EC835881301734A740473C1F6D82F89F6083G52F72A81EAG8E83FC8AF08A609B0096G8F40A040C7GC7GAC92464123D4FEB1685EG60G1885B08FE0A9C05EC7B08FGF481C481A48224812C84A82D039C81408B90871082308C207063747CBA009E00E80039G0B81247A747CF200F60011FAEC2777BDE9D946FEDE4B2F9B78F5933F22DF56
	EBFC33DE6343756378E6AF77817AD0AF33057B6EB0777511F162G7D3B1E7BB39C9D7A7EE2E26C7FF7E0EDFBC758364D5750E95BFA1D263DB7EA5A1C74D16C4B5866BBB5ED7EAE40EB3CEDC9ECECB510C9E2004484090D4DBC8642690FF1C3FE9B4589824654FF373F7FAB010DF1FFDCC75AC9BE8D692ED9764AEEAB7C707332656BC87B7279586691BC4FD33C97666FE618B07D823FF29DE9EB0B2A7A145EA1D56E10D5D5F15B836552FAA24BBF546B8DCC7DA86B1575A4AEBC43B0FF8B7A0275E46E6B73385DB2
	9DEC858BAFDFCF67DE59A93BC069E64F10DBE578228DE40DB2A00F72393FF843EA094C5D4EA112C7303CFF2E05442A572738E088A7290814D9CF66653764E20D0F6281C756AF29F0DDDC0F736A70DBB82F161D79EBA965BB2F49A8C70C3B739AD35DBEB66F356D8B4C2BCC277CC2E35DB7509C00637D59947743AC422BDF0330323F2CA6BEE5856870EADEB6FD183407D936D9061CEA1B62D7E99AD6ADA16F7DB4C781DC0747F9DA06245F8FF4D0C9B51F976FFA156868845D23G6276613CB8231014E3CE42A744
	E60F53491A894AB27691DF6EF497E97CB1F79F7365FAD1FEA4283CE2FF20FC6AFED2BEAA4A3B61DE5514B3BDC6B6125C44AF8F8E4910AA12646AF3516697B63E2AABBB415B1F15DF3911343FD31317759CA0B24B86F1CD3A9064E5G649DE0390F79GDFDBD2BA4907F4C9F0CF665D54830CFEB1DCAB81BF6793C2F73F20EB780465CFDDF0F5GC483EC87FC5DC1C2B75D0FF44BBEE1F6CA87593578A0134B684CAA52CD057BB9G73B95D2A03DA7D7206102E2E0165816DF06DE9E072ABC5FD6902AE8A4A629A42
	55F745E9241B57407209B438AE8D292FFD986924C6A62F3811555755282D2FE88469929BD9DE1252982C9F237B3D20CB63F44B9A195D41F293C711EEE5A3534BD8EE779924ABECE2B9D0F9D3309D0C6E1D3342FF1C2E3D0979A7D8EE26206BE9E27A0FB499497D5198524DEDE2795602A6A3FB474EA15DE2CE1759E4E46F2173424F4DCCFFE339AF8A3A62E6165BD5B59B591BFAC144A9276BEAB63277EA89699CD09E8510504C64E6B47335B82D6FF816504F4C63584C7AD11519510D18E9FFB3A1DD2B19651A24
	9F917A9E33560E8785DD2A19457392B3734BF233560E9B32055C96962745AD4C8E24E37D723B9C24ABEFE1729ADA585A2627C55BAFF765A21DD7D3CE6823DB347D7289C117466916696818FE33D7A3DD5EA116C3179E8A54CB74E373531FF711F9086ECB75DBCEDBC6AA7D43B0345757FFC6C7FF367FB4132EA149B8D6F588771966E611B1BB966E13GD28F611C751BF5384F94BB10A8ADDD77C8F638BD34A507183E522701B9EB495F8EE733B96105CDC4E6831435FF0A730169D56DDA37516F7774A9F4194D47
	5AFBDE554EE98CF76F2BEB60EAD3FCFE75B0A49DA3B8AE4FFF1568CB56619C4D7D9135639DA9F41FCE2673DEFDFD707C07FBB9678EE9ED1E3F611CE9ED981B2D5C56299A1BD31F79BF59CC768C79CD4336504F2C3807926FAE10589C1B8A752D8228ED25B162A6F98909B326DFDF8D6AB76B4C1DD24F8DB70755EF24156995579A50AF69033D59CC5E11C114178665F907E14C2039CCD22DB617E9G3C83A086A09E0065G4903A4BFD6D459D5C97E5721DAD43959E1F15BE5EBB9B13652B22CD652743072B01437
	10DF3BF87E7074A02957AF13F9DF369E7278DC9695FD7FB75B9B385C05F75FA165D926190C76B5E31E311B669DE6B6ADBD2C0937C5CF18B28118FD3BFCE8DF79E750CFBE433D5B22CF481566F59266B4F77856E9F44298E2BA0D1DC41D2296A6CB451BE69A6AC47D7C9953A5653300CE09CFBEE11A7FA46A14A1F4AA7C9C46694F511F1F7BC99B2B8E454FF21C078734B9CE9450A5GAC9074FEBF31414542076B79FC2356F773DE09B1545371D7F126A51FB37DF25A82FA76BC7504C9FD8A757CBBFF816FC31DC0
	935306F23728842F3895D531B815B3F279AE6653473697F1BCEB4FE607C769137D653BCEB05D6E582455ED7F05EF8DF50B5E60B025F21D16EBF4D321CD23C53B3AD4542D6EC8FA3E9700794CE1577AAC9170E9GAB0E206E0B54BD247750A82D746BA316F7CF9E342C0407AD9FCB3EFF78839AA3AA5964BB647338BA1C8A643065075B347C1FF5424A6710A2BB2D7BF61D1030EFD78FE13B679D05B963A86A94B7C45A5D62336FE1B61474EBED98B94AEC0807AB2B6725A134BD99F04B0F226DC3B6A35B4B0EA15F
	5F1EC679F500739EC3792F8F874B7F394DC8FE2228E7F498654F875C8AD14F52E162DB8BCD64AB5925DD7659A096C28704C6DA9C6C5B02E366E05F8AAF160773EEB01E3585381C69F9E0846D2DE8077C219D75CC986176D6B33AD7C611EE8E50AC92F46ED1748B237B4DA85ADD70856474DF20BF261DC17EB140A5FF017C4B4F844BB9F696691683CD59F1243B77EC301CB746D0CEB750448A3A28B1728C4666699B72D7527F37F97C2A5E3107C8817A15557778984ADD88F565F4E0FDCD6702753B739C526D879A
	D550951C8F564FF39E75CB821A0C8E7443DB97103F24B3BD3F33937927DF8816732C200B811AD4C157A9854B39CFC2B92B002676CB1C1B1E2E514ECD4EAF595C948B5744AF312E6DD97F31D87C9FF45D8C8304CFE589C2B96948CC2F33D928FF8A48DBA9E4BE19259F97397F685C6CD7DCE60BEA699C754A3C1EA3A6342F6A8458F08267269BF3893D1593176FBA4C477617366178D9E6CE106AD68C041D0362CF30F1F341094078D971333D598DBF43F97FB9935AD07C9564E3DF218DA732312D587D79EC6DBC49
	F8FF1E0D3C514017A2F85F4E89EE67689C24DB89B4D5DDC877E7B7D9DF076E9D60BC3CA99767C1D64F71DC7413836A086FC2BFADAB29076B90FD6602F3CD1BA64373536AABC1D065B1F30BCE583EC0FB2EDE0D75AF063A8BCF221E2E55EC8F49B964F21BE5FF1FCF7112C071A35F832B513ED6600992FC3BDEC1BF30BD506B64AECD5B4C833A05A7A3BF53D8EAC03382F0F9DF876359B339B25A1EA5DF33E7F22F88FC954557EA70CC77FBE92C127BB6A81B7BB5BEC732EEA6FD2B68943497C0ABC08FC094C07CA9
	4C594D1B481E67384F698E3BD5C869AD4E0E208DBBFCDE360978E9C09E65777F3EC99BE37C19F4B1190366034C45G19GF95D305E6EC69D2695C55421E544D08756A6BFF013E73C2D5D93FF36447812A9DF4FC570313E3B6BEB6DB9041C0D1F193243745DD93C6E786EC05FE5BEB93B0578E4A960D78194FD131EDF8150768D7A649F0BAAA279841F09F2E7B01BBA86A3BDEBF7FE93ECAF6333C4641B2563E37D566EA47A9A2CCFC16FDECF13E7046F27AAA67DFEAAF9161888F54E87E06B580CDABCC731689BDC
	47BE340744F64137B047826C7F96E37BB7DB497A39936E3DG51G89G8BGB2G723EC3BFEEFDEDDF4458A213C527074D9C4D7ADDE66E1F5D3E497975C0C43E726F027918FDDFECA376B5C0D9B7008AB08BA099A08DE005304F75DAD3A47B44A3F030917764C34D647A7EE912F62E0C48D750E394BF8F6D5AEC9CBFE4B54DE367CC3FB6F64A201EF6G95A03A87E3A7570931935F03B17260F6624345F03F82204082E38EC09B0083A04602BE7CC5E49F8A47CD4407BF716BFD11E089365D5807FF9F42D7293BCF
	3198454A3ED2EAA7142DGA86885BB815AG9CGB13DE8E7516B87A658971A81576A718C981848F5752B1333713290BE3D0DC93DC6F1D272719B46F1425689D046E265A50751B8138AF5AE6E45D8395DCEE2056C8FE47662F36B0F06A69777AD9179BA7B0C6CF93B3C44589E319EE23AF67467F1D94EBE54B56B7464FC3FA104CF6F7B841D2ECC07A49B7AEB3E506147A1F5CD4CDF3F0C48D7EAB55261859B7A214E0ABADC981E1C9FE60670697D60359A71B50FCC4E66AFA272C59B4ADBB7BAB9F9DBA372459B4A
	FBB404EFE27E7CE3881F5E1F738C654DB8B3B97BE6C764CBB31477655949496B0B48374CD05E0E314979730390BE3DBFA559086F8FA1FC93336F3908FC4506726EBAB7B9F9BF0948D7E5A8EF687C647CB9A504CF6F4FD6C3F93BAECC4E3E0FA27275984A7B73A465E5C91178C68C657DAC04EFE27EFCA804CF6F4FD9BAF9EC4E5B5091E68EEF9AD2D5FADE000D75BB68BA0070A44A388E78CB8DD987AC057B15G05B61C83AE4B1A1C4F2E0948D7EEB3626BB0CD4EE71690BE3D4F9AF472D8EEF77FCE12A7F4C119
	8A908310883090E099C0019DF3BB7B6B0D5244578111733B97C2741D18DF3333A371D5580D78D2A372994BCB0F48E7B614F7F54E64644D094857EDA82FABF7F2F1E38B615B2E3BD78D65952C1E1C3CADA1FC7AB80DB11437A004EFE27E7CEDC43E841DBCF6EEBA19BE731E8FE5E9G8CF7131760D640FD1E83F12F7B882E826EDB852E6494FDA7866ED501BB50CDF0F1F0BF5701E34BFBF43FAD956E17GE48A7C0794CF76294933943296656FB9E4F50DE3872983FFE7BA02EDDA4F650C6787A66311F1E5646A68
	7565114EC673BD054D614F822A1B0D4F821E59ECFC96B0EA73C60607BA34A77D31BCFA7378E785E7ED8EFFD670F74561757CC331311ECF979B6B79EC7178FA3ED09CCE4FED542F3F25B1619BF22B0ACB26D533B9CF9EA167DAE174BDBE64E6C3B3295D2F5F4313F03CFF69CD325F1C2360389E73469EA9F4DCEF31AA2A470715F44A965FB11BCDECCCF4F865BE54FB4A1670BE1A3E4558C73336987B6846AD637B6836AD637B68E9151C698DB947F994E6A5891F97CC9F4C67B4AD2A6748079266845FB3E1CED02A
	E0CEF0EF9E71F9975CFB8122956C074F540EB72644AB217D0C6D2F9F24B9C63242765733843E1562D3B4F87E8CF18F3ECB36904A2A7A314D9E5F9A5E67CFEEB57679322D743C02E74857A76B7D7A5C56E2D196183149B18C62BAFE6616722B961FDD8EE91717A9BFAB8B471FD7922E6DD83B58FCEBF93B345BFC9ED77B073C6ED35B425B5A3B4D58D647367036FA36C536F5E4DBF8DBAFEC0BECEB74B6E2CB2FE2D5FC7C24AA33F1279FE32FDD7534D7CBE20F3B05442F515ED31F4F63F41213D8926EBF15041C9B
	A67BCF31909B097D387F7423FE5C2BCC6947D8FEE3004452CA38AF9CC84FAF9F40D8DE639CAF169B86C2E319591836BDFC3BBC3A5D38DD1E589E3EDDB236C7EE17E73707EF17955BA3374B3FED57378B9BB7FE7D7A86BA6E787DB2FB9ACE1E3D72B64B74EB460B66BDC2DECBE9F87B5BCA0D6DBFDE9A5E7E1325116D6FAD8DEF3FD29A59FEDF6978FD70072A56569AB1B656A8A41EC8D967G465343F692CFB1649EE03E0827847BF8798301C5C03BCC93D72BB43F8B1D015FE5703B4E999CFF7C2C45D070D83187
	F360FE0C65B6E7E85C76GCEB52C2F6E34566EDA89F321FBEC11EC09067A66E86A1C2B791D2A793D98FEAFB7143D7CF430AD35DC760BB634C5F2055AD28438F217D1FD778EEBED59AB70E7C602656C65F873E8B07EA30EDFFFC6DB4F07221E874E845357F1FC4C59E07C479CFFE2CCDB4FC7221EE2FAFEA4C0DFDD454FC568717CECC152F9EDBDF5221E1E8BBA7ABD8C7F1E244357B07C8AC9DB4F47222D4B8C67747D5C476D86ED4066EED7AD11E3F569677453946FF569677417EBF0CE9F033225AEBC87B0AFCB
	1B17845E1D18EE8A72EBF3B56F6F3F5A99FE7CD93853F87CC95F99FE7CF9ECE7647167491D61471FE7F6C69EFF1E5FB97E78D3119D9CC378FE49CBBAFC8D477FAAC7BB1F6E9175DC133BC6534655D508EFDE9D042FC67C4B2BF531D24B708F4964FD1EDE0F0F9A1A6CA66D772418FF4DF05F8D908DB0474D66E146F7371770B9AD231EA19579FE65C53ED5F0DF6401F5A1C03BC74B7781BDD36A7549B65967132D9D4A99B26F47FB68B9FC3A4EF440EF5503E7DA6E99A6FE3179ACAEF0A3CD4AC73CF43CBE0532B2
	00F6A5118178BA00AE2FB8BF55CDF801D1F18D3928A4A647C95729A9C037D050463FC97C43CE043383CE8C7F750ED7849E5B6A2EEF8238BD189F3E39CE5B6EBC9F677B88A17B9DAB3D013ED6B0982E5F5DC17BD169207E8C4B5D94DFB728BF43B2BF8F4F3034C3D972A02E4B253D38D6AF76619C56C226BE2E5FD14D6FC777C41A8F7EA340E4E4DED299ECBE6F14F1BEF778F0BE77BA08FF6261FE9EC01A7045FD7D6365074BFC615AE7A64DB7D779D8BBE48B7CCD94DF608F60D97B6C9890678121AC4A0F6D3353
	096D1361C71D6A9454A9CFE572CB54009ED5705B2C866B45E4B7D19B3BD4A6FB2D4037D37C08864FF4DAC4F39CF29F87E52BD45469C17AFC37E7A8BDFFE488F1058E020B017BF801BBC9F149F01FA6F089941781772B846EDF9DE8E361E94495536711BDF0BFA6F09E1A574F027BA401DBE1C35E94017BAD2D2FF098F204E1447D138DF55EAFF01B6D28F33B401D32234ED601E34F33094EA38277149D6546885C03D654F90940ED34234E19827718604D99C15C919B6A5CA9F07B68BE3D836EC784AE3E96F9270E
	E0BC78EB83FDF4CC73BB37765277576D17D4860B43B5B66C9B0B406602D19C7F3F9DA16FB5875ED5318FA975751D0A6A142DCDF452EA12ADEE5E670F5BF0FF43BC0AFD3E12FE03E38C6EE38112C671FD1111F770FD11249F5CA1AD4AB7FE07E7E967B9D36AA86BE74BC683674FE2DDF86E7ED9AF66BAE5E7E02EBC03FAD777841DCDA027EB6B6BBF75A956A60B3D0D4E1ACC673D03F91CBF768C6A5CC75FC1D9887799G2BG0A4E126F87D510634EFC8E5AF82A942EEEF938D49C5EB5ABAE59CDBE28D1DEDBD5D5
	D13A272AEA9733B5FED8FB562EEE4D65066F589268BA4BECF4726B2A57EE4EE2FA1EBC05FAA6C059123384F765A96D39F82257E5D9EC2F1B157FA4281C64C5BF1134657F92D4CEF47D379CED7936EEEDB95179D34E4F6CFA68EDEC43D58F4D151CBF1E9A76DBC6E5E34C1ED6FE1D15B1BBAB6E65B3594C2F74B18D776B75D96378F58F71EBB57815755337F268B7C6204E58B16C3BF764E25F1DB706FDB7114E57F96752734BG761FC3FCB24513777E483B2E1D827FC05EF87D0D54C12E7B4F0D3F571D97261CBF
	032B19489E38517E3796426D05A71D23E36FDEF384DE1C9F890CGCF8C40EBDCFE7C39403A0269FCD246B9F18537AD6FFC40469278DDF55EE8CE4C20F31F79BC2BAFD7601FE3F325864F7A74E9871EFFF6C2D95AF9EC5B077B31EDEBAFE0DBDDBFC0761AFB447D2D741E3C134C6E5F9E784F9ABB098C63367C64BFCD06D1FB32FC3B4E0979368AFAC483A482ACGD8F6817DF30C6E2164C5C11CC7A16E8C455541FD1B407DC57851ABF0DECA1782770B2350F72D8E527F17433DF499642EGD5G664B307C84AD6F
	3A0C3D7BCB5E43A56F301277D3D9796FC276E9C8BDC6F6D7996009BCA25B08BE20431FC3E4047B3ECE3E81EE3CEF6D2C15F0CCFA42CE56F4DE0B62D6BB153E81BF2E17DE30639E6848E5380EF95721BDBB4E68BEF160589DAD683C8E723E3F4B526F7111BABB3C16BED8E0B17A2BA8BD6907F1027EFE87FDE743ED71F6FA18920C761F9C28C3A25032FE1F2BE03F1FE76057EC9DAC841A2517857A7D0A4B827D1E495EED233AAAEE83DDDBEDA8DF3A9CF53D22BF7C9A73F69AFB05C0DBF2B96A3B560E7A36DE9E2A
	EF16CE5F6E4B037575DE2E5777D907713AB31746789850C783B05CAA21FF3240B550F67DB5C02A40CD35E31BAC923885227DB304ADAF8ADBD6985812AD61FEBCFF6FEECA30ADA5D38236B0994F096F7215CFC13975D4EE9D5C1B27205CCCA157BB455807DA39D3F5F263C2641E26FEC9G7C5CA9582E47EB69FBB072084A666D37492B07FC2E392FF63C3986739D149F2319EB88FE49947DDCD3E89BEFCDC8E0194ECE1EABC764531FBDE0FCDF857165CC9DFFEEAF06F2B607F5517C33E22AFEEF4EC271F5D375FB
	F3359E4CD74DD016B8954FF967D512713DE09A2C27G9AG3AGC6GE22761B339AB5F2A12A21C0F6974F85B645332D373E8130F135EC8FEB11E5B32A23ED3E14CD7FF51FC4CEF0F525CBBF11A5E1FCBA9BEF91A5E1F4AA07AB3854A4A2E407545BF1E52665C05B7BBCD4827179BFD85EB6B58AB746F378C6EA6FDE08160B32E40B698FC0FFC2F690425BF74A9B6A05BFD9E2F6CD3C79916FE25057B635BD3976B8FE647AFDE419CBB7927BF1466DDE18E7BDEFD4E15CC7FB2FEADFFEAB6FDE710FF73300A44D7A7
	14F981E28112GD2G96DF09364DFB0F3F2FC354EFD4D5CB1F03FE65C0BC68D76461G1AEC8632F121BEA7F4AF720B9C4CD716DF4976A2F322A10708E66FC632B163A739B8E6D4C50776B79C2F70EA861A2E684018650C560F7B5BBB493C43CF6231CDC4D1D666A6F338F823C76C4B466C237B0EED16DE59495E92E74FC04F873D53B942751E835739GBBA8AF0C5F927E4BBC980A13A4B6CEBEC8360DF9FB7E21G5B33642E6FCB66C74D2637423467826E13622B82F139F830B41B67EBB266FA6DD7E18E77D0AF
	66FA56GCE41DCAFDA60EC7DA2573B8A7328BF51BCE2B95CCB539177824595CFE76F79733D8F47E4F25D65D78537AF1191FE6C38E1D2B274F9DC38BC30EDBA71419F7BCB47292B0E6B47E2E433FBB50D91452FD6637E6637CE31EFB09D5763B1CE7DFA7CFFCB5A75744F1D7A75F8D6D0F926D33F9EEF8CDA4F3F69522F47111F1FEFD04267DD97E89CAC84DD97CF47797CDAA5345FA6AA9877B9B3D83FAD01EB558CF6CE0C755B4281112F4DB06E375A393E9BE83CB3D83F2539478CFD3F3DD2B14E57EE27BA4F82
	7AB9B344730B63F46F1F7657BD1CFFA6FDF693F86B841F939CBE970CC77ACFBEA2757237EE78333F3F9C556078F787E6B8023EBB904DFC30C068D260207910EFC8E677DFBA08CDD5422EF807892E01E7C3FF9274AC06BED4F03FEC490C0B9DD32A689EA42B77BB9B11DBAD4ABA76864A1E3111FCA76CBE9559265A05B13D5F0E7A9445986B436A1F71E1C00F3BBF8A487A5E04E471F72A6D413E7B4A1ECDFDD5454BEF31877B6EFF5903FD773E83F5ED9BD7D76747817DD64B41FE3A26BED07630FD62FE5AA9E4CF
	9DD7760F76857C74487E002C4FA7A40B712DA42F3B71B3A4A91BC87FD65CCE45ADB733AF84CBF81654CD73757920CFDA8C0E73773A69D9D2384F1B0963773764B341FC1C35EBFE57FBAE7DBEF647A51541F2180D15611FD997D79ABF335EDEE9FCD6302CF27CF31D9515614FF556D4EA3F81496CFFE2B0606FFF567C768CDEFA5FC7DFD299AC473C13FE571F597F4BCA728DD443BC99B2494FA06E0F75764BFDEA2745AEF8CE6FDE2F75B7CB1FF959395D78AD9F7AAD8B7AC92E8EA0F45B196C9F9DA465C59067D5
	G1DG8E003119185BE6D5116FDE3137E075F9ED0B55CE4EA3102FF42B622CD8D578B87A172A485F76DED1C5724DDE8F2422AED23F78B6F795E98F556325C87E6C3A0A441FCF31BBD4C64A7139746F9EB8E59BC573EF5D6460FAA78EECCB1B096771FE110379C85E55789C63B9CA5BG77DDG5E2B1176CF39B8FEC5DFFD3173CE8BF9C5833F6BE68B73DDB7BABBB3D9775660F75DD204DE2DF4FFF7A55C97DE836B5DEBF08DDC53C4720A86386F8608820883C882C885D88C309CC01A85F6GD48118813AG1CG
	E3G33G92E6E11B2F2D5995AE8681499F86510677113DBD89F32559B4371E87752482303CE8C5AF66C519004B3B966483182FE5E767D9DE742458F76B3E56F82FD81B97F901E66A35013CA86EDA7D9E4CB5B4CFCFG7C5CEBB16798FBB5107FE01E73DA81DD4384CEF2D6337C65DDC51F3F90DD17083ACC83A47FEB164937B1F0FDB2ACEB57A7A5BB9ACC18276A6D4964B616DC97D81F84461B3EF14FFC9A810B2FB862C2EE17C7A91546FF96C41E3131FE1126E03FE887FD222F43D8AF6E47FE11F29D465F21FE
	9AFFF0DFF8BD445F75C85BB8007D22617A09768B1C6B6FEF8974115F9F8A58F04FF8F37DDEB0E1EFA02FF8C06495A35763FC3DCF517AF1AE60D32F1F48BB960DF6AD5FE260D9BEA13EAA0716CF0A05B5C06CC4785E8962AB871E7D917818FF2659C24FBF3DE8433E568EF5B8GE28192E335EB327F69A6FED24903E861772CD622B26B7BBFBD98FE7C7E4541486377BF9C245FF904F117FC7308FF4F64A05155E33379E5B1CFACBAD8A8F0077D6D9E3FC2E6A67EEEF744772C0CFDB9F312FC8FDEB49F5BDB6B235F
	B3CA09556F39B9A9FEE12CFE4F6D85B187ADA66DFC83466AF3C16FF94583BE790609444E7DB54837C0C33F047F4E04EB4E0D50C7EF8C14B17D6653F92462C63D5E3F22783A9B75FA9FF5083DD7A8CB3C916709CF1B42474A5126483172D51371DC7FCDD368DC5F5BE4BC57BB1A34F3BDFF07290944389D6629AEC16793381382B7CC6BA4382F942B6A9078F3D4AF02EF1509C071BE8D55EB18623A709DA68D6EA4474DECA2718EA6BBBCBE643D2E092C43EC0A53495F2721B40A9B42DF7863962A236235E8740E97
	3813223F1611BFE9407BF3CAEF29B451313E248F5B3160A61803EF42B39F7FDC99749D9FF666031DD2E1C7BE78B8EB6D43B9E16CA61C932E23F5A6437DC2000C1BF0CE58540FE3722A1BAEF6CE7874B3C9585A7FF940568E47446704EF957CE6C4ED9C66C9475A082F1CF09F8D1084108A3098A093A0E7F6FAFE49EC0C6D2A66F2A9FC8E441C93C801787B0C4A64461F0CC972ED9B777CDF78F16B27F49E2F1B2D6F7F3F24F873ECFD7FEF6B97B98214A54F46F16B5000D6FE714D697995B7CFE45C7AFB07115E8D
	1C3753208E36AEBFCE5B54F1B3530F613E223828A0DC17BD146E9BFBA85D270ED03AB6C7A85D81833A26A0BA6627B76DF896EC8E60975DAC3E83F8DC3CC394B88B562638E54DC9B00C5BFE71EC3460966CE32F51EF891B613E9BC03D857D2F503F5D46FE7BB43FDF4A39746B62225CCBA90379F8756AD03F2FDD9D5ABE69F42D9483FEC93C8577F01E5EC077F560FE21403595621E7EE2017B187A3BFCCEFAFE439C447D1162BA61DE1DA34EB6B9827EFDC8733BF3D2FB6E9767EB57FF010CB77BA6B786D5CD12CF
	39E8BEB6C6455075CA749C7D58F58345474E510FDDF1E24E0B07321CDB31BD0B0346CE876047EE1D485855E7B8D64F623C897CBACFD3971BCF3EEE8B1FABD95AA267CA7DED46391237ADB4D79AEAB34E154E360566CAD13CDE1FF89917612FE8A3FB266C9947910B5BE2576C472574CB62772FB53F0F77DF7AB89D3824B278DCB190BA6E748E040EBB3F27F340C2E86BA537625851AC1E9BAE3B9547FF4BBBA486799E9CCDB579F74DC656857018EAF2BB4B6D117A0771B90E8FA372991F63708471555DB6FEDF88
	74472B685C54F61B3EBFDEC771DD37697B63D6B776C7AB14AD388D77D41E29897F8C4EC44F15D75C1E1EDFF7BB527F7B0660B3B57F177CF9AFFE26262FF03CB3B55AE7E87AB3B524DD2C376B1F33355B69F3B64B086EB91B034E93DE209F93FA2D32131C9692FF650C472DDA1C0DCE456EA63B261C67F5F126AD71F69C0B2ED3A4518E37E8FE5F56F7697B53FD17D4866B933B7BC37BD3EDFFE8FFE2E7AEC9FFDAF8BB762747C5FFDAACF0C514B7A7BEBD3FAC9E7D7E02A3B89EF634876261EBE5A2710067A516EA
	4E7591FCDDFCA09E187CABE83B1B815F9D0FEDF80DBB50EEF11A5FBB5D173E8D6BAF298C66CF779712A41E1B2EA1EB37B06B30EAFA56FC6FAE4EF76DF1C954115C912863F7760954411FDDBBC8DBF410CDF2FC5C602FECEEB95A59F2DC547DD4A7291B1DD1F81E6EFF0633479B2F5D8FFB3FFF7C7E5AA276C12746637B8C2BCF846C3855CE7FD689FD3268A44BFD4E77E3762E8E702408985D50CFE26C2847C7779A982E3BFFA7DC35DF3F9F5F4EFB3E8A48FE49350973D27FD185AAFB49439F76371EB7FCD18704
	C94FCA39FE1F74EA281F9B2FFE6C51246F4CC5A649F4D8DA6D209350E1A9F7184CD0A36499678230FC16FFD0BACFE4CAD602D125028E72B175D2F6CE88AA5EA0AABECCF7B3161E2E294FFE6A7B59A6A987EAE5994BE1A907249DD0390F8949060C050A10F84F234B97AA0AFE83C58BC28132ABDF7E5FC2E32CE52E968467828291EAA7871B762852168E7AB7CACA49A762CBD5CFE92FDC4A7E1A0DEC1DCCF56BD91B143273D87500A0FF0021D4A14FA9CEDB1C75021E242256A06350C0A918C6155608CC34BE0C92
	BEE9FDF088E65D39F9CA61B5B5C3A6A96F10345AE6F1FA9D96E8F9FA363BCDDA5BCBC3006C1EB8D529107E61075D403B1B726EEB13CCFDFED55A9A407791BCABA5A728E5A04937DAFC83348622520ED3D23E4DE76F2582D449F46A50A990CB6FC8714EB6E90D6AA08FAA496C20CA1B83D537133D9BDE300F05634B701F177E635F7B3E20BD8F39F2DADF58F71052925838121120BF8F79DAA7998BCA6F04796BCE5CDF12BB48A160F04FF9BBDC962793C62301C63715BC73ED9AD565407373AD1D24FEBF5270C2D6
	363313685D2F6179FFD0CB8788532398F26FA6GGD4FEGGD0CB818294G94G88G88GC3FBB0B6532398F26FA6GGD4FEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA9A7GGGG
**end of data**/
}
}