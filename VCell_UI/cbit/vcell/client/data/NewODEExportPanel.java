package cbit.vcell.client.data;
import cbit.util.VCDataIdentifier;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.ExportConstants;
import cbit.vcell.export.ExportSpecs;
import cbit.vcell.export.TimeSpecs;
import cbit.vcell.export.VariableSpecs;
import cbit.vcell.export.gui.ExportSettings;
import cbit.vcell.export.server.*;
import javax.swing.*;

import cbit.vcell.simdata.ColumnDescription;
import cbit.vcell.simdata.ODESolverResultSet;
import cbit.vcell.simdata.ODESolverResultSetColumnDescription;
/**
 * This type was created in VisualAge.
 */
public class NewODEExportPanel extends JPanel implements ExportConstants {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private JButton ivjJButtonExport = null;
	private JComboBox ivjJComboBox1 = null;
	private JLabel ivjJLabelFormat = null;
	private JPanel ivjJPanelExport = null;
	private ExportSettings ivjExportSettings1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.simdata.ODESolverResultSet fieldOdeSolverResultSet = null;
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
	private VCDataIdentifier fieldVcDataIdentifier = null;
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
private void connEtoC10(cbit.vcell.simdata.ODESolverResultSet value) {
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
private ExportSettings getExportSettings1() {
	if (ivjExportSettings1 == null) {
		try {
			ivjExportSettings1 = new ExportSettings();
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
	TimeSpecs timeSpecs = new TimeSpecs(getJSlider1().getValue(), getJSlider2().getValue(), getTimes(), ExportConstants.TIME_RANGE);
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
public cbit.vcell.simdata.ODESolverResultSet getOdeSolverResultSet() {
	return fieldOdeSolverResultSet;
}


/**
 * Return the odeSolverResultSet1 property value.
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.ODESolverResultSet getodeSolverResultSet1() {
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
public VCDataIdentifier getVcDataIdentifier() {
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
		cbit.gui.LineBorderBean ivjLocalBorder1;
		ivjLocalBorder1 = new cbit.gui.LineBorderBean();
		ivjLocalBorder1.setLineColor(java.awt.Color.blue);
		cbit.gui.TitledBorderBean ivjLocalBorder;
		ivjLocalBorder = new cbit.gui.TitledBorderBean();
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
public void setOdeSolverResultSet(cbit.vcell.simdata.ODESolverResultSet odeSolverResultSet) {
	cbit.vcell.simdata.ODESolverResultSet oldValue = fieldOdeSolverResultSet;
	fieldOdeSolverResultSet = odeSolverResultSet;
	firePropertyChange("odeSolverResultSet", oldValue, odeSolverResultSet);
}


/**
 * Set the odeSolverResultSet1 to a new value.
 * @param newValue cbit.vcell.solver.ode.ODESolverResultSet
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setodeSolverResultSet1(cbit.vcell.simdata.ODESolverResultSet newValue) {
	if (ivjodeSolverResultSet1 != newValue) {
		try {
			cbit.vcell.simdata.ODESolverResultSet oldValue = getodeSolverResultSet1();
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
public void setVcDataIdentifier(VCDataIdentifier vcDataIdentifier) {
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
	boolean okToExport = getExportSettings1().showFormatSpecificDialog(this);
			
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
		if (cd.getParameterName() == null) {
			plottable++; // not a parameter sensitivity
		}
	}
	
	// now store their indices
	int[] indices = new int[plottable];
	plottable = 0;
	for (int i=0;i<odeSolverResultSet.getColumnDescriptionsCount();i++) {
		ColumnDescription cd = (ColumnDescription)odeSolverResultSet.getColumnDescriptions(i);
		if (cd.getParameterName() == null) {
			indices[plottable] = i;
			plottable++;
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
	try {
		setTimes(odeSolverResultSet.extractColumn(odeSolverResultSet.findColumn(ODESolverResultSet.TIME_COLUMN)));
	}catch (org.vcell.expression.ExpressionException e){
		e.printStackTrace(System.out);
	}

	// finally, update widgets
	getDefaultListModel1().removeAllElements();
	for (int i=0;i<plottable;i++) {
		getDefaultListModel1().addElement(names[i]);
	}
	getJSlider1().setMaximum(times.length - 1);
	getJSlider2().setMaximum(times.length - 1);
	getJSlider1().setValue(0);
	getJSlider2().setValue(times.length - 1);
	getJTextField1().setText(Double.toString(times[0]));
	getJTextField2().setText(Double.toString(times[times.length - 1]));
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G4C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8DD8D5D53654D861CFC5C58D954DA3352222060A0A54E6B0ED8693139CAAEA2830B06DBD1AF1DE4EB7B4631BB97CA986AAAAAABE29E8069AB0C3B0B4A3A9FFD05048C810E852647E9C38D7AF77DE6FBD7CA96ADB7BE76DFB6E3967F211B74E7B1EDF2BF3775AEB6F35765AEB6F35764FB9C8C56FC664E6E695C9D2E62E447FFD12A9C91B4FC9D2E7E4F3AB62C26CED79126A5FFBG2B24CB3F4A01E7
	8464257DC7DBFE21F47507F9100E01F44402367C7570FB0D947AE16AG7C7043A7967266B4270C9E1A0FAB9779AC256916438570DC81908BB8724CF1907EA78F97F3FC8A47A3E4E6C9D2AA25397CF08947B5C23A83A09CA05A41686361390CF23E2CF68727BBFACD24346889D756F2D20F2ACE04F2A7AB5B441F25529FB397B9892F1FD2B9C919D1101E82C09A1FAD2515D442F3E773492E4FCDC62BD259EB12ED36CA134DAA5B15CA33C1B154569E127B3E2E5F372F5F69F0A94D863BEC13A467A9D2679F8E6751
	BAA4E9872497D35CFE8B6193833F93G5284FE5589025F65EA4BEF87F03A30EDF5FF5F49E5EFBF7330145675783215FE5A966CE2ED0AF6F95AB6EA13B1FB836D077B1790FD27C3DE213BAD3F8C20914082908C3011767FA3391B60395B286CF5F4F7BB6C3B1D4EBDB6336BAB07D936416F5ADA48613843324961B2CB926B63878FEF5611E7A8E06526DDF0B92613480F313F238E5FAB25BCF0C356D29D1D24C41E490AF0B3DD443ABDBAE13C977B651D6C7636C7EF5E5FCAEA5E52EBDB17666A70AEF9E66B52F4DE
	0F24F8F86FD4C8DFCF22B6D08A3FB3F93D8C7FCB0A8FD26119EDB6D1BCC907C31E540336513E82E5E94A99AF95D67FD8D78FE10F0D4BB277B09986FB34325CF212549D5943F8FEA6E419C671653D9EBC13E5BD35D3120E043CD17DED794C3EA66711315AF13AADFF90A094A08AA081E081406253E8E3D9CB368C53461A8DAEE5CF0F55E616DD92ABBBF949BAF84A7D0A4BF0406ED68CF6135CE6F0D98DC61B6C16B84D74A5C487C62FF2B3286D1B00C703EC17DD86456A30C36D06EED911DDEEC6B3E749AAC26312
	E9EE2B35DB86865DCE896BFDFE499AF85A8CEE650893063D6C21E079AFAFA1FD52EBE8039A2182784D7432E09951D74919367C9A007AB32807E3CBC9FDDF4AAE18AFEAEB1B9C0E13BD4ED65292A9748C51731A16BCBA66E1AEB803BACEFB0B6012A13DD054933AF4352A1EFA2B0908EFF08D60980B339442B3E730AD3F94E0A74051C1341F5F34237D54AF3FC75A7C74753A76938236BCB8486CA6F4D0BB0EE6517A090CD3A0AF85E071A04E7D07AD6D04F6D0FFBCA19019F28250E8612860F11272488DC0CFE833
	AF20FE9C6FCC6F078BE89F1EED4B5F8150FE96F5BFFBE9A93365BD86370C567B358B8CCFB6FBFA0249B96124824F3F813C8FC06670FCDA07A42D67FC5E86F897E09B69CF00CF79B82BG789060A3GB2F62A81BE86B8G50G70BD40B100FDG8E00D3G1FGFC85E010508EE6D07ECE10BD8CA08EE081C061B9E893C087C0B0C0B4C0B200F43EAD3F9C2099209FA082A09EE0A1C0F1D0DB7EAEG8BC088C0ACC0AAC05EA5ED79D5G6DGC1G11G0917E0BF3DFDA2B1D3DF4F797C3912BF5770A76AE505CAB7AB
	D4BADCB104EE36F19D208E35BC77F15D9D603A7EEA88BBC07D2E607A74C7073A3F909B7B7F8E2CEFC37D766DB2951D3A2F172B7AFB152A4F499845310CFD7E1E2A4F7F8E301677AD310D0DBABC098D901BA036311E5B00BF7951EE48EFBDBBC1C01B7A3F56773F92589C77F89E69A73997425DFA59A95B4D70434D739272C87FF2FF5C6990E566D03C937C77DEF018EE01CF4EA3FDEDD0941755582348AD3222D86DDD1E7CD44A4B5DE3F4FADCBF721A1FC76C4251877EDB502767915FE7F2586DB21DECC5911E7F
	9B6AFBE51B5C8DC26FF574589506DF340C2CD1CE4A835C77E7ACDBA2915FED6BA1F1840B7B238D44D61DAEEBB7CC61A494119AAFA5FEF9D3CB31CAC799B03F1175CB8964D5DD0AFE356FEF68D74BCE5FA9459DF7E6694508E137DD1D95F2A973FB93AF7578D5A653EBEF694B7EDB9A83F03CBB1B626E4AA4E535EB901677669222D316018A2F66F9E37AC8FF544B1D069E1B52E4F5ABB48C2B16306C6482B2AE81576230754A90743B018EAA2966FEF16CF2A263F410BD99E061256897CF52F885E3926E9077BAEC
	B656CD103773C7B446319629741177A3264B95A27F8B2F7CD12A7C941AFFD26447C0BAC1154F64E8AFA03109DBBE55A3C328C8022BAFC51F9FAFD82D49EB637D4F723B8AC87F5BD4F1D96CA842F31D8D5724E5105E8950B10A45BE6123785A72B2C2771F02AE8752446F16DC46682B60D98978C5146E1EEE248B3B0C45CF51700C83C885F88770A117933AC1BB526D3A1C35331DBF1D17B33E0CEE3B8369D2A03D8820688AC657FA05DA3EBF8A3A10ABD89C90894F58AB987FCAD15FF4A752A5C35E42AB7C5557FF
	8A698A03D9BCD18E4F9D415A7A7638102EBD1871F386337A22035575ADF1A31DB41A45A545233D65E3F409022E1C536D9A4D5A6D4D77A685691AC7B33974799E6AC1BA4BE89683850D76EE8723AB6E957A63F4112319FE3C793EAC68E2397C09237578CE6CC33A3CB1AC5EAA9D2357DEFBBF52D5F13A46B1FA6D5DB1A074BC06492F4FB7DD50B9473058AEF80CDEFB9FBAAD6C1453C50F51EBEF5099240B037CE40045E3985F1DE379DA1C0E237AC1A15FD8EE47E359B88A66F409E369F8BB0BF491E3D92CC94691
	210F9D2BEEC72C20AB99476C39E69C53CB7DB8F5BB2EB8A7780EE3F66A9C475A01F4ECDCFEF39E690246B1FEE163585AA6F61CFADCEE1410AEDE15CF68D347294765ECC1D7FEA5235BF525B79D13EFECA652F5DC49E2687EABBD75927918FFFA230278A13AAFF54250EB682FF4774154DEDBFB104E7EEC7FA9142EA149BC96FCA56EB37D36074C5969104E398AF4F9957A2C271663BED36849C8A965356A257E7650EA2EE27236DF6571D9A18BF7AFE1BE61699504E79864C5DE057E60658275DAF7375B6DB0D969
	B21B4F3557ACD77BB4063BE179DB706C343A5C4A8188BA7AF1DE8EDFCE64A56BF00E667A88FA778349F71FCE26FE2F36565B7F61DE4E597D6AB6CFD9F9B6AB5FCF1B27703626285A3C4B513A14719EAB7FD537AF341E9577D02E3596C84C4716DC8D73A840382B290DD8C9DCC26C0C49E77A98658BB9FD1B54F17DCDFE65CB3C1A493570EA0FFC1B4FE564B2FEDF5810DFF98858BCG0BE55EEDD047B2E1000F83C88348GE0B1406DB6929FDB95393B127C2FC5B1A872DE0B415EA51B4BC9E3AB8DFDCAA58D8FAB
	8FC07EBE722B02478F4F59C83DEE1978FD593C5F616AB6A8287BFB5E594579A65CBBDE4AB3DC1625372FB96E352E2C42EBD81BF6DC63E9DB51F5466CF2G563E0D5D583EA020093D8677EE0BBEA7CF9A57C9985304F6AFD74904B644E49A7C8EE58ACA08110A57DC29AB9355B3172578DA0FCC2991A0CF844A740C104982B4915722BE6B6C240F950B554DE21C38CEF50C138CF40B81CA2FC3FA59CE5A504D4C074B791F3B553A0BDDBACED7CE4B9DE1D9B557B1790EDE6711B36CD6E3F6542DA867ED7682BE0622
	00A6CD70ADF0903C55EED52C861B7534DCDE41F47A5CBBC59C4F7AF32F45E1B5496E720AB6A65B78B5EA59F61E7FC1D7366015162C12D0A6D3FD28C73628EB0D59F15722EC479DA8DB88504483B01D3DEED06BCC3A9E7CAFC05375A87BAF9CDB496821D6DA6956DAAC9F1E5CE8D98EB7DBBE17CCB87091359105EC726DF7B93ADBECD608E14B8FB4294B9FEC05154FFE2BECB3FFD651A661587E1B937B3D83648993B20DF112FEB7383A3632B6841955EDC864ED48GE0753C6E4436975CG3E6986EC3BDB376D03
	B7E039DBCFA17F9040455F007C73DC5E7C4F987478CBBF46FACE3A10FF91601AFE0C754CF1915D9AE8A0DF499E4D322B93EC41F7C258CD333DF5DB70F53D37EE05964B7DE9579B4F7A82D44E645C6246760E82992788B9EFF07376D6B33AB7855DA220599C06F49D8A6A0551CDD3305DE620898FC3FD7475E079A440955C0865DF6C7166333B97696A01E6D050C576FA73496FC3BEB1C013AE68DCFD648C2653E16AF1D7527FB7B95C0AD6317BC9866A1555B72B8F79165D5416FF74A62C2F265FDB3E9F77A3DDA8
	50A4883ABF77FB4BE798C079F2EE865B3B99753076B416D7G97F5B3169FBC6D4D67A5C11786B4A561C8377F0CB71FBB4FA01FD62099970E3E693ECA35EF1A9E4EFCD3BABC25DB302E42417F1DAD7E1C2E1BE102F0A9ACC0A8A783194955BD087297833FE641B3E9D0BBAFF27D8D925F6C36F6579B94C36B00D36675FCF1965B978CF5445D023E6906DCC2EF6644659587785C7E2737F17EAC2B1FA86DD8F4522F8F4A38054F1BBFD14D1F4B5F4C0D83E03C671E45B6B801A672A7580646F358D7AC6DBC2776132C
	EC74B9AC1B8A650A7F8D4B2EBF6F5D4F274FA3DDB350848B3AF9D6323E765DBBC0BF1CAB219FE47554CB2827B828A3635FD0CF8BCAEA6159C34FDC5057B429A2BCB72D3E9298D5FEDD3FCF436CB3CFFFE6087A2BA25A72AD91A86771CC3607E46B6936574BEE134B6AA4066246F2B119583E88A813AC4AED4CC2BD30BD509BDB5AD5FDD3F8EBDBFE592D014FB4F66850B4812EC303E7E7F2A6BA37A83732B33925826F2078F1AABC137D77F44EA269091017B79E4F116AD71331E503F4B0C084C0ACC0B2C046F80C
	594BD610BD4FA14F698E589588698D36962F8DBBBCAFDBC174F4D29E606997D728ED0C1FC92FA1BE28E8C2DBFE95C0A3C087C0508414C1DE99D006FD7D3AB230BE893312B35E088943BFDBE265EE2765E2AF209C9B3B957FD05FC348D9F5B0AB5B4F58DD406B4E1860993BCCA75F2FA5BA59B1915685GB600D1G93A722CEFE3AF2F3A01D6019A8D786EB53BEDB20337669933D5B4B6FEF84AC37C0D30E0D5BAE9B11D7E7FD8AF29B9DFB9C7DFCBFB532D73B1FCA4E92255B5A720BG58BA765B863CC73179B6DC
	47D656915BB6C3FA90A074B6346D7B561175F394246381D289BDC0696D6043GBAEEC7BD7EE965E781ED0BB80BD687739CFB353B4CDCBF6FF50FCC2FDB82168B3A5D3B9CEBDFF5B1E9DF986445GA4GACGA8388362B70026BB30FDDF956E8954BEF1848EED44BDF95FE6F2F9D3C6584EFF8FD84EFC071E7D4C2CD82FEFBFE4B54DED47A62BEDE7906A0984C8GC83D83ED678F1DE8BB99F7200D5C3E016830AA927485E086888218889087109609BA3CB5308E05620623433B6DDADDAC0E74EE3B3E8E9F71A9D7
	29C9975F29E7AB9BB752F6C2DE930099A088E0A2C09CC05A1D584EC505F5439C8BFB815768F01C54E9A217D5F60C2C0DEE1FF25AB6665C25E7A7A51FEE50379336CE00BCE6ABAB337466199228336AAE3415B2A3319532BF50F8971EDBD7B8C7E6771F84AC97255B1E774BCB745BA356C3CC56074DF91C57F4A16B9BBEBC07277B4CD301F43FD8A3AB1361AE836A2B68EE14618E1F3A06272F8783166B3FDBCF067B8D280790A143A9574874F05E271CD68F71F76B155B669ED91B7786AC172A4B6FAFA36437DC89
	D4AEC3175F8C1FF243536733BE65347AAC0C52ABF736E7E46D9B9B30DC39AE3F065E1171BB92305CAEDDFE2B7AC6264FF7FD4AE975592E4B6FD7BE6506573E39814BB9F5795D52BFB2FEF787AC972C4B2FEBE0E47A6C71A92755E704AE3F0D27C7563E0A0065E2F5794D9BA13FB78216CB54657753B3A3536723BE65347ADC206147FC5E4A96BFBEFCCF0F22507B82EC2EFF2DB20F17116E41F500EDBFD9876C00F4B300659E74816E4111696C120065026E51AB376F6C48F45662D3CE2B33B08DBF965B5DDCC262
	04E848CBGC883106EED4BAF835885E03E97E33B06D53B25612F838247F773FD649D1EDEFF9F305C28FB754ABDF4EEE47C2687AC972E4BAF687C4878DD9530DC0CAE3F0352486C265D275CBB1AF402AE3FBF0F10DFC1E6A0BBCD5365B749275C7074999F305CE28DBFF6EFFAB2BD73AE7AA92C0781986E39AE02EB02F4074035DB89EE9424A3842E609B7ACE8C2493846EA37A8E47C2C867C5635CE2257BEDA5102E81E894789E0AA77B5464AC052CC5997ECFBD6DC3340354013F9B233D5B34027399CA8F0CC7D1
	6FD0BCF2B5745A7CC0F723791E42EA7FF781DBD76B5F857C47EA7D3B009D2BD7B1BC54213E690F791655C35F95F42E76FFD7F0529A7FF2CEDD23AF67AF57684BB9EB4D50F2BE3D461F1CEFD33DBE13C5EC4255E3D72C5DB22D1A79BC391F5CEB055977F00F1DCD4D24F637F68FCF42797E57EF137D662351B82F5B0A36CA3E737ABE33D5F1383012D659607A3A33D3ECCC34B8E5934AEDDA6BDFC7A7566A6B4839D6DFC73DEB0756513935C36BE83EC26E747A5C63BC84DEC9427302C15BB2CE33CFF1FC35DD4218
	A03597E3027EE80C8946E7931DC7C3BA9EA0D50C4F238DC34DA999BA630C6D2F0F27FB0D8577317D75CC013F1B620BD5F8366FDED3076F1215C1DE70FD58E71357795779E36B74F59E3F0E5ED7F07438CC32D62F3357950BBC0F47A657B0086A781DDBDADEB1383AE41FFEF90916E7F97E4AAFD8672F6FD83F34F76773FEE96EF4B93A1B37733ABFAB765F56BD457AEDEDAC765F562F0B8337353D58FFDB0F9787EE2B25183445E8B5DBDD7C26AAEB639ABB5ADE3362E82E16449E77BEE23FFAFBCFA61743E6A3B7
	31A45CFF8ABA2E377F148E36A15D0F7BCF93E55C2BAC3E9FEDB93F1358D2B324ADGC17723ADAF6E9C4A16436E7735E55646688D7E7B65618D7A7D7268867F7DB2FDC360FE193541FF3FA4ED885CAFAFEC50768B1BB7E65071640429C8E62761646C1577594BF655FC31F72B60777EC67F6D5F3CD13F7DDBB77AEF7F279B8337FF4FC67F6DBF30B1F07B8FEF9CFA8C06BB54ED7DD84C0D8DD9441EC8DE143027F0A33127B41286A8FAC06CF39A070A9F88EC865ADD8FF86C2AD5755B227ABD883FC39E7036BFFE57
	4269BDD7EC65314B94B35A724487FC6DB696F0893A75EDBA25EEF70DA4562F06C0EDC9057A96296A4C0B717CAED17D2E025F75B1FA3CDFBC655D169A4E7BE1833625BD4637ADB6408569569769D237E51B405B5C5EFC36F17CF6459B7F8947CBBD6AFA360BFAE2FA3C69F7F07C39DEEF7C279C5F58272E6793D14FDB747E08073E3A0A5F0B5062795D021F8C286B59A16AB9FCDAC33F1561CB4EE8709FB37C2BE75475FCAA7A7AB715FA73E0A857F124CE9FB05F1DCA778D2644E8FD7AEB949F9F2375692E7D6853
	13A0EF4703F88F602EC1F5DC62F9F7E2702C17DE77D67371FEDB097F79A72AC4FF7E393F447F7C73C8C960796731927F734F0C1240734F1C1221671FF74EF95B903EDF723A867FB147BFFCDE6DCF370AFAAE115ED275F1F5956237E5FA612B917F3BCC0D2D5470BBEAAD64FD9E2343C59BDA70906DD7715ECEB874DD8CC0AA402287199FE66596F611F2B6430023C741F2CBADD82E9552B600D000480754659CD622DF27CB6E14DDAE595CE2BDCD7CFE86AD9FCC5719F170BB61A13C53F2DF9F51CB274B508DEA24
	C1F96243746E60B748EBA7241B811CGA1G518FA32F7A4324AC9434F677F4D3CE0C4FD13ACEAD0EED4BAF0BC55A083F927D309B616C0293437F635D25820FFDF577B18FEEAB4607EFAFD777BB0F4759387B52E72F36B956B3564C317E461D2D1248549F2B3D4352CF71A1315ABBAC1159F807A592728A9E41F5393B81576A4EC75007C512E3CCAE5FFD2A5F35F5017C417F8678BE5EC565417CF96CF17467F10F20BFB71A08FEB2A0DDB829AD3FFC926A62CEF92878F057A4FF7DF31E461B2D13D8BFE48B7CE80A
	B72B702CFFD6F50A7B001017BC897BE7CD97764F4E49A8D3D3964A54B11971D7A6FB6488065F61133D65E23C8BE99B23A7B35E79827F8E45A72A70CC2671B446A169051057BA85E57A121E6F46C2BAD1602E2738B4C8E7885C0B94D770A86873D144D51888EEA724DB854EED42B6DA84EE9DBD0F0C05F412407D0A1E55AE00F44E4F9017E040324582371556E701F41040BDE5C019C385EE2D10B9D2603E93B2CF9138DB0CA8F322404DB5A25FB4015BF49CE52E79B9621EB4204C0D82D7AD4A9E9538038614B9CA
	60F23750B98F520982F7E28B16CD79B95A432B7BBDE374372A5F7D7BAF7EF83D66227260E323954746663836FCF39C4E7FBF7413771ABD6F2AF475D8EBEBDB2D0ACDB66F21CEEB0FEC3073B17FDDAB6EEF04476118F752B1178469F4001C29783EC87F87783EC8546D632544F97A6F7024361E4DAA194A46592E292A7782B6613D7BAD960CF586A1BFE6AA4ADD5D61F5B7015C2E2D2DEDF0D94DFB8CDDCD54EBB2197BACF93CFC7AD41479B6DAE759E3B09FG3482589EA35F8FAAA02FF5F39F1479CDA9BC6DF2DF
	29383CDBEF6D166D64039A65B5D5D51BCB37D6D5D53036C674296F5A6DF86BD25DF7EC89C4BF465AB81DBF477D6546CCA66701EFD04E4510D7B30D4A7E0D7ADEBC116B12CC36574D726F744AA7F151A312BA7F57DE79C4567F4AD167AFBF2C4EA7B2B770722CDDB35F47BEDCBCB3DA32BDB84A6F370C86273176C470E76913B7E62607B5AFE1FAFD79830FDE67748C21572DC42F5520D7B6CE0D7D749BA3D0E77AB4B1E78C6058AD1C0EE357C57DF587248781C2857E9C4513777E483B2ED182FFEF76D0630D54C1
	1E2153075E6B6E70134F3FB1D1B91CBDF03D7DEFB5785B8B4FF91C68E2CCC3FD0032689F89A4023CE9GEBB97F0C691EF585BFB3BD0EBE3169F156360E47BDEDD460F77063FABE71996A7B429FE775658A7CDC66ABD5F8B626ADA63C7FBC9D724AFF01FD9BA7E35F0E7B857655E51DE42FB9D6242F26E972CEB2CB2F6F7CE74D1D0407FEDFD67D53F86875A70B37B7F5E13C1DGF22482C83FEC4BAF855875CB540F096E21F4C0FAD060946A4FC3A0BDD1605E90FA0C973829541F97473765D745236E561948782F
	07F4BB0093A098A0DC6417517C68F8766EAFF98F173C43CA5ECF65B12E4FBE8D29C72F5D41BAF8422F5E8F3DC70629BEBC7CFDDFE7198EEE28EF6D64CBB8A7990DE4CD67B4D86DCA2B55F4520D6B2553C65C83CD0C47F5CC14D1FDF71C51CDB3625C1DAA68FEEFA46F7BF79BCEB8DC244E962741848BACC63F02768599078B857DAE93FDE743EEF036BA18900C365D04B2C8B3403F32F1EF42F1DFB843D7FF599AA8831A9DB3BC633EE906E75CB35E9F99282CD63B0E2C7B8D483FFD864A7A6FE67FEB4CECEAFB96
	20D5043C2F98D05E889DF9B3B57246E8640D77117700C9FF5DE9203ACA827AD4G067B23103F608944FDCE7BEB86C00940DDEA44BE59ACF03F907D3F7389ECCB06915B5274043E6EF3D5ED69F8423BAD4A931E36B09EAD4711C710607B87AAF3882443855FC641B7DE07EF2606EF0A066FC29F3EEE4AE3B16073E6E23F16EC216F41487D8A735B3965446C987FCB368C65EBB05EC17EE9AADFC370B5B3353EE6FDEB407BEAB3754E625F8FD8CEFB7700157B412B5C5119C37BF6A764B39FF6D39771D523E6EA7766
	A6D0FC48CC6D5EDCEFA7462B6110A7A560BD6F8565E4FEB7C3BA88A08CA09AA091A0BD814F643A5E2912825C0FE9F5B81B64DE5926BA5A6477BEAD0174226F5B760E305C608517E3FA5BC05FB717E6E97579D70AAF182555675DA27EAF063C41D9383E187D3DBA66AE3C4916056534FCD3E7313ECE1F25FD3F651BCDE48C14BE89FD7DA47641B76F136FB53599CE781EE283325965F04AAEE500E169D7DA38BE8EFDF3217AE06D183A94E36C18C9F7C93117577BFD2F7E6813CC7EC17E0CCD38113EB3483FF978A1
	312FA8480B87C88748198D6BF9002A5958363077797BBAC47C5D0AE2B0D968D78E44C13FD56E732049E6A01B97867AFD77A2C37AB1DE291F4D76A20F4253B91B3D9B49674A811CB302E77B0EB71C2F70998EB45133BDF35674595AF97F1DD662F778CDBC3609A8724A6C44070BB7FA443E6C384F683EE31341A85B58DB626C8CF4EE1D7A8CB4114B3D881EF909ED796F52321214A7613F05DF7862C0A7F41E3C1D3C224D7BF3EE8176E7495D93247AE76B337E6627BFCB9319CE1C099E3BEC6F2CD9466235246398
	6BC5A6E28C97FE8CE33DA99E1C99E33DD4011BEC96315ED398C725D05FD88F69F6011BCFF14E2758FB7E8CE7B70DA456A5752A536DCF8D35B63AE2C4BC34F11C3FB8F062D3C4874F4A25C354954265636F115A17D09B313A15EA5C5FEC6E927B86CF61FA7CDC17F6BD7EBBC93D1E0E6E522E47B33D72DF6E522E47F7FB2D670BAC5A75B816E7F21E4E729FF7DDC75786E5F3204F6720BF9FEB769D37634DE877C76730F12B40B3F88E3BA746466DDB1DA2DE1B23BFEE553EBE86E8626730F1CBE30FB95AF11B6CA7
	DEDBC86DEF8150AF92B2FFF4186E7D53713A15173FB21B14773CF5026784874EFA63117E73CFC83D7C2D9BFE767766A1950EFFF760BB1357F787E6B39D147E8AE53946C863A1D70F4C524F9BC91B2AC43B5EB7E54153F3B664B0A1BD332149A65CAF2B7955054EA90564D3A73C5EE3864237DA64FDDC65491BEAA0710E5FFDAA32CDD501B6DDE4C4F9ECFE64E1751F2C7648F155D68F2F9F06450B512E52686E08B11B6A2A0A675FE874565D8BC6EF5DD5885DCD9CD256269A0FFCA70EFB6B49314D137701F178FA
	9A90FACA991277355BBDFAFA70938F2F44E1690915CBA6E18C3FC39237060CEF2B5DE6354BFB599702A53CCB5AC377DE0B1240C613F01E4F20EB2DA6C8F7A4617C9D51C56A6367AE2A5F037F34BD8C7F737A759715870BE1FED76E7F4CBA23DC7F4C7A4F657AF785F3AA063E57D9D0617FDE671A8A75B7A079BBB1D60F3E4BD53F6F33DCFC5DBFF1D1F9309873B2F2550137FFC28559AC570D13A112BC88F67F357104ECD2DA8DDD22CC5B2695EAFD337019679D5B045F72215F32201F64EA81C2FB9763FDEDAD49
	37011D8783C4814481A4A5E1ECBBF793796E95FB8BD69B576EB3F7117B8864AB5D0A38AB7621FFBB1A79E160EFFBA7FDC862CD2383C251EED23F78B6770724BF940713A279597507447EDC56AE0B42C8B9FE9E7D3B87B639132259FAA7369F57BB8B49D8FF9A6F63C983980FF4BCAD5EBB275FC58903F4B4C03C202DB80D73D76A53976AF76816BFFE572D534FF75D28F7E63CB6FC0E5FF5ABFE8665BACA6FC2B4C35A8290748C2E0157541138A28C52B1G49G8B81F21EED4BAF8128822887E887F08284830483
	C483CC87C882D8G3078D96C737F7828421F8D82129F86510EF7113DBD89E3257F2231F5E1B27085E0F1516347B0AEEA84DC87C0B0C0F8B23BBB4F6222874C989745A46B6F952B6322F820C9C97644C58B1335FBB0E9B4CEDF8C783C674437EBD6FB629F0CF35652EFFE2BEEF2D63378E503D99B3F90D9EBC4DD0BE4927F6D154937B1F0FD52A72B57A7A56F6E4A42B8D55B1E4667783978F31E7509E73EB952F5216BDDE6DF7DDFC8927AC92B39D49A7AAC081C3131F171049947C5A448137A9C5A7AEA9947C571
	73E8FF3BE4EAFF1036G84BD0F341FF5623888FBFE386382FD7D4D3BBCE364E73BBDEDB8BFD4DC318D1A304D93D7040818A671F97457F1A6359E73FE8DB6796B613CE3314128AED785E56A07D52E520BDFBB14318D2B5CB9AFFEC1D0A6B4C0B93E7EEE753D7F14F98447DAA4549187108EA0252857E4ABBB69FEA9B90896FA1FCBBE0140674E1BEA7D4F5F3756861E3F6F2A255FF904F917FC7308FF4F2416486A686CF44B42CF4454968A5C81F73343EDA51E096964E7815F915557E542884B657A3CA39A8878BB
	8354CF9427783CB3C071E5A9BE6F8C8C60D82B023C50943455CA2FF7B8B3GDF7042F0EC671D4F31DC290A3E067FEE0467D1G27AA0F4917C87D48289734F2BFC771A1AFE865B6E60A3DD74813DEC4BF7177BA7F36D2D99758D6BE29537775F5F53E3EFECF1D3E2FEF2CD37BFA7E8ED39D3171AE70D36D026E3040FDA7F05F52BA896EDB2BD931887CB1AA9741B74A0421F81F064A55C7F16D788E138A779D47F55791FB07A6DB9CAEAC6B2AA36B30CE2B4D465F2721B4D6BB183F504799AA2355E9D0499DA4F05F
	09711AC35CA59F4F579CAB1506BB573FFE8C7B518CFD987EA25E79F835526BBBBE6C4E873B25422EFC70F9F65B8F689312DEC41FE07A016EAF3F5416DF863073A574894C8753EF3E3DF421BE616F64CFB770369E5C6FE96BBE5370FDC23378F6463817B0CE5A52C0F4B59D5229GB9296093G2AG9A810E82A829E85B051F13C39AFFB190D30EA784E263B151BC327967459116DBBF647DBF7F73D68C7563A1295A717FA8450727EA477FFED9448810D7B097672DDDDE6F27B881BFEA6EF0662D6BCDFAF2077132
	D1BAF530F57933544F474DE572B19C5BFFCD7642D599FD693699FD69766954F7D0273E6BF56A3B492BBE26270AA3F897EC9160B73FAC3E83F8D83CC364398B56E4354B2A1BE0E837270E60392579E59CE35F32FE01F48CC0424B621EG79CBEE3896D67D2E9D78975C053D28BCF87F1C56691F533E7D330D2E1552C0AF52AB380713351D6E6BC03ACC603A36631EFE15403550332AA0C807895C1BF4CE0A02F442AB286BE7B2BD7AFDC9755B12F971F5FD493F00479B47C6B687E57EB032F23597DC0E4DD14768FA
	A575956D5C55C971692FE867AE6B8FB8F7E5C05E51B46C4F573C665CB840A7258DE76EBA22BBD7AF60E59773E761BCCFDD4C1F54B6780F15F6B7840E158EB66847CA4D8D3E315211867DD8697B865FD82903576B92AF63B2FCE78359B3E5E79CDF996C06AE55FE5CA4D2A57FBDCD753BC93E78F6AADFD49EEC7CFF5B69BB6F9C6D741DF722282FA8033E2E1907F347AAF1EE38EB9E4E7F068F088D72BDB89AEA726F1A75AF7760B15444EF879E9959BD0E3A0065746FF174FA158B19B774D8700C471954B7CD1C27
	9D0FC9949FBDCFBB9E0F09D8E28A64153E0AFBAACD1F7BBF0313697CBE8AE8C3847D066D5EF7EA1EEDA4F64876134EED9F6ACE0D7A8CCDFB2706744B14D73567ECA7B651F3B6C33F661C6DD26ABB6201BEC948D518C9E2DA887C2D279DF645E05BED33F659492EA9AF33A993E3E8E9BE4EC5A633A47A41267A6D7861620F27600B4A030D89C9769DCF39326FF83219F1BC154D4771B4CB0C27AA01DBC24B9E057460FC547BB41337BD7C6520479E4A4D4331873CAF31C3F5CF0E60C366FB6C01713F1246E861000F
	91FD7814EA9EFCDE755B74AF189387AFAA8F264FC3CD120463618F5B485A4D4FBA2C1A5EB55FD6414B1DA2FFF60F7F8EBD642963B752F06AE0343BB3C9DF3410CDF2BCEEF0D76E5DF728F55FE1D177342FC95D6C0EC29A5D7F74571E78796A7D30376521476BA7E29FB4E5BE3E4F70EA33279D930F503FD5C2CF96EDE4394F4BCD21B6D23020AD3FF8815AE826CCEC6C1043C5779A986E107C9EBC55DF3F9F3A1D579C76702E23328B7DD181AA0D6470077D2DE77717BB00B06AF5A9576D12D67B6AF977D54FBD9B
	F53C3EA8CB4ABAA0AD31D087F4C04A6DA39E2A1F7CD954F8E8F9087B14F40E7014484C942FC885AD64E36A256C1E90D43CD2D4FC006EE62476FEDC1B7D4A046CACA987EAE5914B81A907049DD0390BB1490608053210784823DF7C222C48B87682A1G5915AF7FBF215117B2D78D22E4BC01G3513437F2D0A745FAD74EF141412CF4417AA0ED223DC4A7E1A0DEC9EC9F5ABD81F14327BD83500A0FF002154CA4EA9FA8D36DAC1CFC2D133D763300112B70DA23DA5A251DABFC23824955EA618F95B7A9F95DE7DF1
	CF161437DFDA52E930B9AD8668F9FA373BC94AB7D293A03BA7B6C5AA24FF78E18B145DC24BFE56A4E5195C0A345103B791BC4BA5B728E5A0D9E6B638CE529A08C86FFEAFAD6BF4F599A983C54A7AFE7F77401626C876FBCD52DB0A059CD4926F20C86BBDD5B7133D9B1E7199B3473F40FFCE7A0FFF6F7B3C7ABEE446E5A63F6FA0152733F9E5E73A76BE64G3DA3538F7850F45CDF1A7195395C666F1C37255BE03341ECF4F2375DCC4EFC778CA832677C7C5CA1D23F9BE9F8A64B3B6C90117B042A4C7FGD0CB87
	8817F9B68E63A6GGD4FEGGD0CB818294G94G88G88G4C0171B417F9B68E63A6GGD4FEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9DA7GGGG
**end of data**/
}
}