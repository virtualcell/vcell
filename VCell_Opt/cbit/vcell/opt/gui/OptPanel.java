package cbit.vcell.opt.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.SwingWorker;

import cbit.vcell.opt.*;
import cbit.vcell.opt.solvers.*;
/**
 * This type was created in VisualAge.
 */
public class OptPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private OptimizationSpec ivjOptimizationSpecFactory = null;
	private javax.swing.JTextArea ivjTextArea1 = null;
	private javax.swing.JTextArea ivjTextArea2 = null;
	private javax.swing.JButton ivjOptimizeButton = null;
	private javax.swing.JButton ivjReReadButton = null;
	private OptimizationService fieldOptimizationService = null;
	private OptimizationSpec fieldOptimizationSpec = null;
	private boolean ivjConnPtoP2Aligning = false;
	private OptimizationService ivjoptimizationService1 = null;
	private OptimizationSpec ivjoptimizationSpec1 = null;
	private javax.swing.JComboBox ivjSolverTypeComboBox = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JTextField ivjFToleranceTextField = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == OptPanel.this.getReReadButton()) 
				connEtoM5(e);
			if (e.getSource() == OptPanel.this.getOptimizeButton()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OptPanel.this && (evt.getPropertyName().equals("optimizationSpec"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == OptPanel.this && (evt.getPropertyName().equals("optimizationService"))) 
				connPtoP1SetTarget();
		};
	};

/**
 * OptPanel constructor comment.
 */
public OptPanel() {
	super();
	initialize();
}

/**
 * OptPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public OptPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * connEtoC1:  (OptPanel.initialize() --> OptPanel.optPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.optPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (OptimizeButton.action.actionPerformed(java.awt.event.ActionEvent) --> OptPanel.optimizeButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.optimizeButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  ( (ReReadButton,action.actionPerformed(java.awt.event.ActionEvent) --> optimizationSpec1,this).exceptionOccurred --> OptPanel.connEtoM5_ExceptionOccurred(Ljava.lang.Throwable;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		this.connEtoM5_ExceptionOccurred(exception);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (OptimizationSpec.this --> TextArea1.text)
 * @param value cbit.vcell.opt.OptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.opt.OptimizationSpec value) {
	try {
		// user code begin {1}
		// user code end
		if ((getoptimizationSpec1() != null)) {
			getTextArea1().setText(getoptimizationSpec1().getVCML());
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
 * connEtoM5:  (Button1.action.actionPerformed(java.awt.event.ActionEvent) --> OptimizationSpec.this)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	cbit.vcell.opt.OptimizationSpec localValue = null;
	try {
		// user code begin {1}
		// user code end
		setoptimizationSpec1(localValue = new cbit.vcell.opt.OptimizationSpec(getTextArea1().getText()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoC3(ivjExc);
	}
	setOptimizationSpecFactory(localValue);
}

/**
 * Comment
 */
private void connEtoM5_ExceptionOccurred(java.lang.Throwable arg1) {
	arg1.printStackTrace(System.out);
	DialogUtils.showErrorDialog(arg1.getMessage());
}


/**
 * connPtoP1SetSource:  (OptPanel.optimizationService <--> optimizationService1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getoptimizationService1() != null)) {
				this.setOptimizationService(getoptimizationService1());
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
 * connPtoP1SetTarget:  (OptPanel.optimizationService <--> optimizationService1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setoptimizationService1(this.getOptimizationService());
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
 * connPtoP2SetSource:  (OptPanel.optimizationSpec <--> optimizationSpec1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getoptimizationSpec1() != null)) {
				this.setOptimizationSpec(getoptimizationSpec1());
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
 * connPtoP2SetTarget:  (OptPanel.optimizationSpec <--> optimizationSpec1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setoptimizationSpec1(this.getOptimizationSpec());
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
private java.lang.String displayResults(OptimizationResultSet optResultSet, OptimizationSpec optSpec) {
	if (optResultSet==null || optResultSet.getParameterValues()==null){
		return "no results";
	}
	if (optSpec==null){
		return "no optimization specification";
	}
	if (optSpec.getNumParameters() != optResultSet.getParameterValues().length){
		return "there are "+optSpec.getNumParameters()+" opt variables and "+optResultSet.getParameterValues().length+" values";
	}

	StringBuffer buffer = new StringBuffer();

	Parameter[] parameters = optSpec.getParameters();
	for (int i = 0; i < parameters.length; i++){
		buffer.append(parameters[i].getName()+" = "+optResultSet.getParameterValues()[i]+"\n");
	}

	if (optResultSet.getObjectiveFunctionValue()!=null){
		buffer.append("final objective function value = "+optResultSet.getObjectiveFunctionValue());
	}else{
		buffer.append("no objective function returned");
	}
	return buffer.toString();
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFToleranceTextField() {
	if (ivjFToleranceTextField == null) {
		try {
			ivjFToleranceTextField = new javax.swing.JTextField();
			ivjFToleranceTextField.setName("FToleranceTextField");
			ivjFToleranceTextField.setPreferredSize(new java.awt.Dimension(70, 20));
			ivjFToleranceTextField.setText("1e-6");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFToleranceTextField;
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
			ivjJLabel1.setText("optimization problem specification");
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
			ivjJLabel2.setText("optimization results");
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
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("ftol=");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
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
			getJPanel1().add(getReReadButton(), getReReadButton().getName());
			ivjJPanel1.add(getOptimizeButton());
			ivjJPanel1.add(getSolverTypeComboBox());
			getJPanel1().add(getJLabel3(), getJLabel3().getName());
			getJPanel1().add(getFToleranceTextField(), getFToleranceTextField().getName());
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
 * Gets the optimizationService property (cbit.vcell.opt.OptimizationService) value.
 * @return The optimizationService property value.
 * @see #setOptimizationService
 */
public OptimizationService getOptimizationService() {
	return fieldOptimizationService;
}


/**
 * Return the optimizationService1 property value.
 * @return cbit.vcell.opt.solvers.OptimizationService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.opt.solvers.OptimizationService getoptimizationService1() {
	// user code begin {1}
	// user code end
	return ivjoptimizationService1;
}


/**
 * Gets the optimizationSpec property (cbit.vcell.opt.OptimizationSpec) value.
 * @return The optimizationSpec property value.
 * @see #setOptimizationSpec
 */
public OptimizationSpec getOptimizationSpec() {
	return fieldOptimizationSpec;
}


/**
 * Return the optimizationSpec1 property value.
 * @return cbit.vcell.opt.OptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.opt.OptimizationSpec getoptimizationSpec1() {
	// user code begin {1}
	// user code end
	return ivjoptimizationSpec1;
}

/**
 * Return the OptimizationSpecFactory property value.
 * @return cbit.vcell.opt.OptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.opt.OptimizationSpec getOptimizationSpecFactory() {
	// user code begin {1}
	// user code end
	return ivjOptimizationSpecFactory;
}

/**
 * Return the Button3 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOptimizeButton() {
	if (ivjOptimizeButton == null) {
		try {
			ivjOptimizeButton = new javax.swing.JButton();
			ivjOptimizeButton.setName("OptimizeButton");
			ivjOptimizeButton.setText("optimize");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOptimizeButton;
}

/**
 * Return the Button1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getReReadButton() {
	if (ivjReReadButton == null) {
		try {
			ivjReReadButton = new javax.swing.JButton();
			ivjReReadButton.setName("ReReadButton");
			ivjReReadButton.setText("re-read");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReReadButton;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getSolverTypeComboBox() {
	if (ivjSolverTypeComboBox == null) {
		try {
			ivjSolverTypeComboBox = new javax.swing.JComboBox();
			ivjSolverTypeComboBox.setName("SolverTypeComboBox");
			ivjSolverTypeComboBox.setPreferredSize(new java.awt.Dimension(130, 23));
			ivjSolverTypeComboBox.setPopupVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverTypeComboBox;
}

/**
 * Return the TextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getTextArea1() {
	if (ivjTextArea1 == null) {
		try {
			ivjTextArea1 = new javax.swing.JTextArea();
			ivjTextArea1.setName("TextArea1");
			ivjTextArea1.setBorder(new org.vcell.util.gui.BevelBorderBean());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextArea1;
}


/**
 * Return the TextArea2 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getTextArea2() {
	if (ivjTextArea2 == null) {
		try {
			ivjTextArea2 = new javax.swing.JTextArea();
			ivjTextArea2.setName("TextArea2");
			ivjTextArea2.setBorder(new org.vcell.util.gui.BevelBorderBean());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextArea2;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getReReadButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getOptimizeButton().addActionListener(ivjEventHandler);
	connPtoP2SetTarget();
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
		setName("OptPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(516, 659);

		java.awt.GridBagConstraints constraintsTextArea1 = new java.awt.GridBagConstraints();
		constraintsTextArea1.gridx = 0; constraintsTextArea1.gridy = 1;
		constraintsTextArea1.gridwidth = 3;
		constraintsTextArea1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTextArea1.weightx = 1.0;
		constraintsTextArea1.weighty = 1.0;
		constraintsTextArea1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTextArea1(), constraintsTextArea1);

		java.awt.GridBagConstraints constraintsTextArea2 = new java.awt.GridBagConstraints();
		constraintsTextArea2.gridx = 0; constraintsTextArea2.gridy = 3;
		constraintsTextArea2.gridwidth = 3;
		constraintsTextArea2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTextArea2.weightx = 1.0;
		constraintsTextArea2.weighty = 1.0;
		constraintsTextArea2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTextArea2(), constraintsTextArea2);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 4;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 2;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);
		initConnections();
		connEtoC1();
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
		java.awt.Frame frame = new java.awt.Frame();
		OptPanel aOptPanel;
		aOptPanel = new OptPanel();
		frame.add("Center", aOptPanel);
		frame.setSize(aOptPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
public void optimizeButton_ActionPerformed() throws java.rmi.RemoteException, OptimizationException {
	getTextArea2().setText("working...");
	getOptimizeButton().setEnabled(false);
	SwingWorker swingWorker = new SwingWorker() {
		public Object construct() {
			try {
				String solverName = (String)getSolverTypeComboBox().getSelectedItem();
				double ftol = Double.parseDouble(getFToleranceTextField().getText());
				OptSolverCallbacks optSolverCallbacks = new OptSolverCallbacks();
				OptimizationResultSet optResultSet = getOptimizationService().solve(getOptimizationSpec(),new OptimizationSolverSpec(solverName,ftol),optSolverCallbacks);
				String message = displayResults(optResultSet,getOptimizationSpec());
				return message;
			}catch (Exception e){
				e.printStackTrace(System.out);
				return e.getMessage();
			}
		}
		public void finished() {
			getTextArea2().setText((String)getValue());
			getOptimizeButton().setEnabled(true);
		}
	};
	swingWorker.start();
}


/**
 * Comment
 */
public void optimizeButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}


/**
 * Comment
 */
public void optPanel_Initialize() {
	for (int i = 0; i < OptimizationSolverSpec.SOLVER_TYPES.length; i++){
		((javax.swing.DefaultComboBoxModel)getSolverTypeComboBox().getModel()).addElement(OptimizationSolverSpec.SOLVER_TYPES[i]);
	}
	javax.swing.DefaultComboBoxModel model = (javax.swing.DefaultComboBoxModel)getSolverTypeComboBox().getModel();
	getSolverTypeComboBox().setModel(model);
	getSolverTypeComboBox().setSelectedIndex(0);
	
}


/**
 * Comment
 */
public void printClicked(java.awt.event.MouseEvent mouseEvent) {
	System.out.println("mouse clicked");
	return;
}


/**
 * Comment
 */
public void printMoved(java.awt.event.MouseEvent mouseEvent) {
	System.out.println("mouse moved");
	return;
}


/**
 * Sets the optimizationService property (cbit.vcell.opt.OptimizationService) value.
 * @param optimizationService The new value for the property.
 * @see #getOptimizationService
 */
public void setOptimizationService(OptimizationService optimizationService) {
	OptimizationService oldValue = fieldOptimizationService;
	fieldOptimizationService = optimizationService;
	firePropertyChange("optimizationService", oldValue, optimizationService);
}


/**
 * Set the optimizationService1 to a new value.
 * @param newValue cbit.vcell.opt.solvers.OptimizationService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setoptimizationService1(cbit.vcell.opt.solvers.OptimizationService newValue) {
	if (ivjoptimizationService1 != newValue) {
		try {
			cbit.vcell.opt.solvers.OptimizationService oldValue = getoptimizationService1();
			ivjoptimizationService1 = newValue;
			connPtoP1SetSource();
			firePropertyChange("optimizationService", oldValue, newValue);
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
 * Sets the optimizationSpec property (cbit.vcell.opt.OptimizationSpec) value.
 * @param optimizationSpec The new value for the property.
 * @see #getOptimizationSpec
 */
public void setOptimizationSpec(OptimizationSpec optimizationSpec) {
	OptimizationSpec oldValue = fieldOptimizationSpec;
	fieldOptimizationSpec = optimizationSpec;
	firePropertyChange("optimizationSpec", oldValue, optimizationSpec);
}


/**
 * Set the optimizationSpec1 to a new value.
 * @param newValue cbit.vcell.opt.OptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setoptimizationSpec1(cbit.vcell.opt.OptimizationSpec newValue) {
	if (ivjoptimizationSpec1 != newValue) {
		try {
			cbit.vcell.opt.OptimizationSpec oldValue = getoptimizationSpec1();
			ivjoptimizationSpec1 = newValue;
			connEtoM4(ivjoptimizationSpec1);
			connPtoP2SetSource();
			firePropertyChange("optimizationSpec", oldValue, newValue);
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
 * Set the OptimizationSpecFactory to a new value.
 * @param newValue cbit.vcell.opt.OptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setOptimizationSpecFactory(cbit.vcell.opt.OptimizationSpec newValue) {
	if (ivjOptimizationSpecFactory != newValue) {
		try {
			cbit.vcell.opt.OptimizationSpec oldValue = getOptimizationSpecFactory();
			ivjOptimizationSpecFactory = newValue;
			firePropertyChange("optimizationSpec", oldValue, newValue);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G590171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF89455359183459AB0CA10A02861C70892B188E81E2695AD7D9AEB74210F2734CDDB5A4696FDD429463EBC4B73CDB60989AE9410BC0D2F290D2D5A20A1729395A3D6A0A1A690E4A14B1250A8EC32136C065D1DE5F6B65904A5745C1FB3BB3B1949C2DE49771DEFF64EBD671E1FFB6E39675E1909D07627146282D319A094948B7C6F6382C148EA91046076EF56A3AE51EBB38B1A3F37GB68BB7BA
	0B601A82EDF177584C1B047A02D238DF8477157FE2B33F8E3F3788D7341EBE88BF8C646C063635BB125E9AD9CEFCBD4AA92177F57D6B617AFB008C4011EB11C4649F6BAF67789C0EC7A8B08942F2CA63692F6038A638BF8D1088B0DBE27419F0DDC7A50F2D5B4D69BA2ECD95723EAF1BB610FEB4FDA2D478986FDEFE2D94DEACD865A3322E27FA921EA05C278390638B05DF9CA0F66D311C6979DBD727CB29696B925D6E9A4927547484DCF5F5C7FC0A456E955DC063B8CB7A79C9FFA9659304EA38DFC3F1F73AC9
	5F2970FB89408A95DFAB91FC2DECB337GB8E43467333F6E617A361D3BDB486D796E3A0D867604E4E647ECB9ECCF65FB1D05EFD05FAFEDA0BEDE89EDC5FE1B399CE08FC087C088608FF44C9F2AFF8F2E7BBA15FA4963113C7BFC3E7DEE07FCD8F208EE78DDD787AD9CF7CC6C12E407A030F11D5EDF2D234FC84078EE3BE4BE661354DDB846E947668BB98B2FB7156878A4A7751CA9494FFC11668F7B04497E05216CECFFE48CC64ABEA1E8E58BCF6DACA95611DD71784E12153C1FBC0D6C6D8A996BF7BDE42C4B60
	F7816F1761F7D13C224133F84C20B1C36E9321ADCF4158E87BBD6A52D2B4D35834E30A2E9F129FC8B03585188E3EC034AEB9B46E2687184C7FD5F5F9166217E970CC17BA0AA777C5FDB0B7GD8FCBD5CC866E7C73F4D9C84C884C88548824885486F4798ABEB7860A2E34CE217157D81175BA14A8263FD3861B5380AC1C536B7FB7D0A5D5BA55A6C324B5E69967D8227F93C017820B3026FC7B4763BC0469722D7146D0ACB72C26FF60F2808321F5164B6ECA6B432C8DB2DAE0F8882BCBE817BDD5D30852EEE3BDF
	B96EF35895B1CC415A739A48187459ED50A3F4G3F19DFDE7C02782BBCE8B3EF87589BC4BFC887C8FF07C49992C6DDDD0BA41D89782C4492A1B1C87C7CA24D9DD6786D8E220FFF5BC4F04B61FE355A4F1C832FEA7AE9F0F591756D72G4E31EA4FA692AA83B6F39940EE0036810C1FE75AB0FE9AB65CAED47F7BE45D78C904D88E8E3038C99C081EC71F507E090E99503682A0FFG737D375DEDA44EB5F2AFE52E9B01407BE1B21E20B20A797D3AFFC27F848A7915795DF1100E61204DDC8BE09DC45F7F6CC0A50B
	657DF63F0851FBC40640939D6111E0FA7EDC2A627366F59ED3FF84789317318DE09725A37D22EDAFD09EB748CB8248844885D8FF8E74G68G0887C883D8FE8EFD3ED7CA39887BD08E6A73C614CEC6FC5BB43FF77DBF7C4BF4BDF3D13A221E05FC1CB7E8465ABC825F46A8FBC72BAB9B6BA79AC9FFE21F68D59AC41F68F5408FBFEF7BC9A351056725EEC9654925F89F6431FAC89CFE957FF4A351472EA8322BB320082D2222383CBD61766728ACFF205397CE01A86B3F9A098F24FE48E3AA7DCB0DA487F4C9DE2F
	C803CEE5616DC29309DF51ADFAC069FAA960D5987E65A6D21F1D9187F88EFC2569E50164B0F70064D3D673245B090FFD324B8321CCD2325092A279E9F330DC63233590FF24F62B04365A9066177E3FE0FEF9FB70D6E149A91FC9EF2DCC3E659AD3D2084D6954D0B83FB01DFEE720FBC1F3E9986FE73A2E6AA4B8AC438AA9DD49572FB31C4B639A24393D55A7F6B55A3B948912A423D97B89998FA90AE6870F39AD42EBFFAE0BEE9765BED717380365079B8F919D33C0779C003C906607734D249DF3B3CF8C75125B
	4D06895AAC6709AFAFF84AB47ED8FF1E79F28FDC175076275476E438CF676D641E35CF74E2FB69900C41D0381D6951FB1064E83FF8B6A04212C99619C36A181FBD78EAD41B0D0FBFEB9FB8C8465F2DD91F468F91199F78309E5FBE4472FE93DC5BG02C3CC76268B046E3FD53A6500A739ECB52797203D86702C3F45E7112E83708AC05205F03F84DF91C7682E16557EAE301CC87AA3EB4F6A8BDA395B7CC8670CE3F9B3AEAE2C9F113B1C76771FAADDE29C73DBEA9C133B942EEF82DE3902505D2FA0DD65950CEE
	B73FDA2EE0F2995D39G52AD017B95G79C8BF0651B1391F77297A0DE16DF1E3585A19B2C6ABF71D3A6FC99F436C4D62742BF8FFAC761F5FCA66A35D1B745A7B6C419AFFBF64143A3A03F4AE307D4839E0A9272F3A92779DF34F1158BD8D77C10044ABF16EBE3A81779DC9E7D2051C27F614986D23D22FE4FAE4DF991E3B55EB763D4C66462FDA084C7571B6F3C5BC4E0B3C035A5AE71F5FAFF539683462B1B76B10F6EEB3DC6A21D7605A6D127DCAB3A45FA046677CC3C4DFD217F1B477C745FBFFC6FFF4C91E9A
	D72727A650672931F7AAB52243255455E18A60FB353BBC38D75B9E0FBEB85F2875C1664673A633018F1C714C767171E19F2C70D8CBD84D3C493B1E771D856D42D808053184FF58CB72AF09B7A62FEB974ACB9C3CC5B8BD7986C3F92131CC4E74319A1F8F6597B3F90BD5F94B21FD7DB814779C15C7620C513579102E81E8C2GAC5F5D67A3F40A5365E7794991526627C563D8CD1A23527F0A514382486C387D4D3D1CB6FB7ECC2154BE562437E7CDF82A4734E69C533F7C9BE1BB522F6BACDC8A40F4FC77AC6AF8
	9AE8123F01FB71323F11ABDD2B844C53B71FA5FA586A8F37B0BDC26D28C7DC76A221FC4B84DDBD88AC637273B5FA04D2BA8B93D3D00F1F28FAB40D07398A40FCB3E4527AA68370AB810A2EC25F9C10F74255CE93FE8D3BD8C439DB12BD22230A4C549AFBBF04A429ABEA765166836477D65CEFAD12D1EEB974E939CA35BFC862BBFADDE3BD3A6D5E1E1AD628AE3CBDDBF15F2A6CC6DF24BD36D8E86B1DE8680B71D7B19F24DF9576C57C1427CD49GCCA71B9FF5DA8BB4355FC4DB3F65A77E374BBDBB98DD2502F4
	EE20192D52F5F949FA943D5E3236F98A469E537B39FD5AD84AA8C950555BB9B7591473CDFE26F4F5D86F5A991D05CDB3F08C1F267A907FD901A661EA5467E80060DDDE1762323BDD03E255D666E35F6E57181EB4071EEA569058502FB337E8755BF361EBDD7D46EFF41A16F23DD6EB74EB02B86FD0E3FDF6G755B1DE0B37B92B0461C05FAB1161A007A1B7BC69BE34C4E732F3F959E8FF1FFC0D1A46F29FDD17D9013F9CCBA7BF0DCB3C107BCD50FD2E9CB743832BA094F4F677BF0ECD9BFBB7B515E6A89B633E3
	824676ACB3294744E01768A31D9C696A8A48D0B989286F389DE46FC16A5D830AF47843D38722A92BDA4273406A1425FED238FC8A9630FDC7DCB07A6CE77A04705EB4FD4270FDAABB9379C308683BF482BB93B12B782D941F2B41B37BEE27EBAB39AF1AE8B3BBA762BE5EEAA1EBF5B65C2F82902E31194B81F68334DD03EB64C7AD4446914FC91A3D8AAC21F6F7EB44C6814FABDA084F21366777EBDA3479127961DE0F519918F20D7EDE1D697F40D1920F6301E6B6C0A640F200BCD5777E43B1F5BF9054551D6975
	1A1768D516F87167850C6FED4AD7FD89FCEC8EEFBD29BDB3AE5AFC50D428B307E9BB6F5B1D981E434CA7E7DA09CF52G1F853092A09F207CDA74497D072BE379844F3238B378980D52978FDE32AF985F573ED8E7323B2F0D6C137139E372F5C47131397C70F9629F1D3A558A6544BED934737AECDA88EBD765DA1CC777DBC9ED1C8677D9GAB815682D4DE87FBA7005357216FB7FC79E94CF8A48951AA3124D79F3D13463334332373CDDDCC3E386BA27918FD7BEC443EE4E8DB84108D308AE07DA41B398A20E592
	5AF76A4B7D316CD30FBB41C6BCF798EEA657B7F714F6BE97134FB1C9AF86B6363E219F83ECA902B6F66610F5AC7A4C015421A168B391004549E29346C96AA4BC933BC79E1DBDD9B179D6CE529B377BCE10F14B073632A41B399A2089408DB09EA0AD89476D4EAFBF3F14F19BB1B6BB7C233331B7A6DFF6129E5F3B4A68645546644BCD527369C76D4427EB212DFC324D3C9D2085408710G10BE99FDDAFCA466DCD0A740454C05B543744D1A9C291F3E1DAF8D632B0936F3321E1D73CED2BB212D7CFA3093208540
	8710G10FEBD5A59F66433D8F6721CD68F38A6C9BA23E3A2BE078B0C4E460CE1FC51B6AE3BBE120F4D511B8BF12F3D6AFA1C235E3E5145D3A8A6DF6594BD3E5D431E7FDD1C4D0D43785E0C56B3CA9EFB1EB3051E3DD8204D8140F04AA0410D077B14A968073768DEFBA95C67G648114A65B4C9549587E81EDA767D85B1359BE4082D71F5A3E70DC1B0E8F16F21D7226685783C486399219954946B58313112DAB23D0C323470B4FE6C8EE9E695C1E4577D7966373F607C57F3C3D5722FF5E6E33ECE6F868C37BD4
	915B0316114F6363ACFA6771EC8C9F2367C60960335949B8B73FFF54D877C70F6A6B7E44D1FD5DFFFCF4E45D1FBCEA247B9F6918BCEEA2FB90B96005CD19C83BE6EB2E98A4FBBE48924782DE16C2C86F7E68DAD4403C749FFFA7FB37952A0D21E3BB05617967004325C8B2F6E2956D72116EEEB54912DDBE6A6DEFB57651E02B3E0F62DA75FDB42EF5E49FCDEC9D59C73FD2487325E14FCF3E0415D7403325493EF5C2F84FD92B4E051410B69F64C54D1D5229436792FF5742EAEC7FE3D6FD7BD7D849992CDF8A48
	DDE2340DBF35162BED61CC5F2C089EE28623D9C579953B5CA38E735133141F35997167DB0D7C484FF54E1A390FAC5D32643154723E8F590CEDBDE65337354DE6EC6BA9DBECDB1DB6E3DBA5DBECDB03740526CE1743A57327B54C46BF485AB878D040B5CDE8453A33E2AA1E2D5FDFC0661E956E5DG71D355FA542437E6CD6731122E93B3CC76EF64887F42AE1B697A6089E37FAEBB216F5F47CF987B7747A7E27B7717A70C7D7B6B93317D7B5B93A34752A4FFE4ACFD4471DFA968FF0BA2D9F62AB277369B7B600B
	F6FD9F9CEEB7760135BD368F3EEAB7760123BD368FBC6D51B1467251032FED24794C6F977D3408EB30ABF6EE7FDF831A782B5F214ADBFC52587E4C137A76BFF052587E87CF46367F311346767F60E4EC7BFFFEF2649878D9042DE42C597C19DD28B7FF166A4C1B4879F3CB1FB6F6424FF725FE2D1CDDAAFEFE10BC8FF75B872400A2888DB710B97DDD7539778AB24F814A81DFFD835B0B3235F5C1BF9957EE583901B9F4512B1CC61FDD8EA2EF825027GAC81D889A0CC43B35436D2228B9EFE04F5FCE623963FD3
	45073C44A76CA95F8E0E33ED203896FBE79837CB8C63F672674ED3943D67BA4EE9F86E3CBE20ED8FCDE36DA9EAFB36EE7B22E96807C4A71159E76F358E78C43E574F6FFFC750FD07123EBF9971C0C00D25E77F3EC19BA7FCDDE675575D74DCA88BE466CC433AFA7590565542F44C39452D51B1E3199E0E9747746131434EF6BF2475D3F0FA7419EFBD45A7CF0FBE737DDE811E79E6C0DB450D28532374DCA20E5C2B380F8AD14F69AA6E5EC224DBF2A3BE277BF5905FC74D86DC51CD685F9D27AB844D3989B9652F
	2B7BC2F6B9765BFBDAE8543235694301D24E3F67A6DC1B3A86683B06F01FGB07BA6FC8699FC971FC1264D19A9ACFBD27F396FCA6BF9D366CD4CE7ABEF52BCEFF92F331049DCB008B2ABEE36192DB7935CCF873571C262722F45EC4F485AB71F5336135C30D5503657C73493FD5795E95B83916D459AFEE65BC36F20EDEB9FCA975C0B638D5F8D1EFEB3FF3F02DFAB9F19DAB0BE5972B2336D9185577A7C1BF1AC1E6FB6F87ECEE79C632B8FA05F7694BC2FAA6CC31CB385F340A3A112F7BAA519A6D416F71E8CE1
	6EC8813A8C00DC007C9476CEC35B8C5A570586CE33E94670F8AE60BEAF61573701E6B7G3EDB55B20345F201BA37AAE8CD138278C5B3B0BE8BEC9833ABE6605C7A14BE832D1AE9B36F8568G8881A44E4476E65ACE5E69A06FC32483FE99409A357D61823D7DA481521776FE6FCC639CCFF39034D7F15911FB56DF9A4A085E2F960F50BF469D7117F68F4B46ED15891F03EF1AC54A474FC88A932C3368337E4272917AE4742749A3AE2E6F73431ED1458246D7B0B0BA3E3ACB666379F1103EBFBCAB9CBB8C7FB70A
	CF5560D9AEF38DE0AEDB84ED653371AC3FDEA47E8941FDB2C0BAC09640AA0022DB5467CA9D5B0598E7DED6495782C510DB338DE4765DB5B83AF74D6FAF411C31681E5B040CF18D066FE4145F42E2EEB73FE6E4CFADA0D7E65F63B4BFB821AD9EA095E08940F200552AFD77F570E7C4E49B8D3BE6FB17133E8123EE4ADDE2FF98CD16C8E65B3FC51CB58AF3A267C8591C61F30671B58DBB3FA3D04569779872BD229B1B96CE6F5061E3E37F1D73785CB888B469F3B0F7FE6F7F4899DA38A620DFD36CA743381F6640
	7D225D0B7BAC73902EAB2B6660DA7649906937F2AE64B3G4BDC4C2D3597545C8A38BF896CDD52D2817F56FC2E07E3F371C7CEC10D011F2D47982818B7CBE8F8224174970398C8194BECDFB3B73CA6AEB9DD3B0E57E02D436B49032D18972AD2A1FF82B881C2296CEC0F6575F29B667524D47D3C0EB909C0AA50E42606737A3254709C646F495077950F0947C43B03BDE2E178BBBBC8FFC7A25E83C01EA5EF137784DBA5F71FA813F28E26E02734DF8AE2FB61BB617AF29747FDFE12783651AA39C5193C2CCF2A57
	C6972C063C7EEC7FD83BDE67F01B0BEE457A725B56687A1234172B6D2947356D553732F66B2DD84B7C6B5F354FF5B75D60B6219F22FD683C153F43F7EBF85CD89FD9DFE9EB064A770F68568C79DF8D1AD2B9EF36260FB498FB3EC68E8A6A7CE9507C16860C5625FF86B09963AF2B8C163BDAC4637DE72B283F7FEC97754F73CE09A31F477608466FC7FBC56D7B51AC7706FAC91C8837C1BD8E3097408A603C8D736EDA113CCB451E8CC5675C830E9E327FA5DF1BA86AD9DB37312D3F6C0E7D0D4A335D64DD46CE89
	661707740F78973A49F8A9120FA2996E45EE62A3595563D498A93E535DC3F216DB6C26E8D61BBD2D60DEA87EB62C579CFDD82F252A38D3348E4D077B52F908DB9AC03AB29577E490699CAA2EF1G69FC7330FEFB70FC2C35F549BC3DE7A9CF44640BFE364278B68D0D2ED6391D2E93B97322EB153BA8FE553C68DA6573A12CD572202DAD8D6D7D6402D6FEA66017250DDC1732B87C61991247F301D6385DE6FE9BE08F008540899082C884188D1089309CE09540DA00CD73ED66CA00DA002679983715AE323F3159
	FB071F3E8312EF84E9704AF69789DFEE175C0AF6F5C0DFC973F19D5E35B362B9BC5BCFB63A257E963EE471B5781ED6DC033367639A3C0A76D9F4874CB1005ABBF08DEE36619A5CF247452E41D88FFF47A388780EC00F5B60BD91F5A1E27292BFC6F9712A8EAFD89117F9875A7ACED505FEFF6AA245F8FFFA3C1447DD5E9DD82B7A3C02BA8F3BDD6F980D81E40F035075114EDE31CB315AFB54B87806C4567C6AF498CBGB7C0BCC0B2C0FABA0E6DBCD75588E34B86A4BC340CA73E57B8AFDD5D9BBBAFA57552772E
	A11F10951E612676121AC56A6E760BEA7E3B29F7130AEB76DBA43F0B9C5930FAE6CBG6B192569EC1F1A8357B5696CF9A52BE74E75E1BDD33AC03F1EA991427B548A20593EA0DC4FB4AC082EE77A82117B2E2F4F1271E132FE94C0D99683D9588F818750B838AC7A7D2FC6962F8B65F05FDF8DE05F7186FD970F50F73226EFF66E3BE48B29ADDCDE374BAB56332F95854CF98B078475F726667701214BDFBFFCFDD9E530F545C2EB75595CA78CF71C62B2B90E455EB3E70CE37C05B331E37C25B37AEBEF11FB785A
	3B4E2D3F76EEF4EB57DE7EDD2B1B0CF58F645CB6156E8D95572E625E24FD925C8917C3F12A78375D2FF0FC13C884A27EBD2AD7BF4535F15C879ADCBB47553A49BC85131D120C3C1F38493C68F6395D6C7EB3CA63724234D57D311F6A687259B5FAFF21625A5598888EBBBF3D385899B7CA3E7BAE190F2D658F9CA76B500A8551EB7CC3941F3BA0FA0DFFC6DD8B57C01B75CEDC633F34E965E7GBE7B4E1157F846B779E364DB29C31F2703E3FCAFDA756CADDD483FFDDAE84417FB9C7936EBE89A786FB69DBEB646
	091271BC1AA2451EC7A9127EBC1AA38D1FC773A47DF934C0524EA3B69E1F8654EF86C077598BF18D3FB200B5C74EC22CB996D05A32C5B6F3B5C043A224CDD34FD4BB96DD6A3E7FA5324D603544FA351FA45DFEF0CD78A8906619B800BC5940435A5B7B848157827B3B1AEF8DE88A605FAB85B70471188270BD4463027A7BD14D6F3C564B1F7BB7D8AFFF6E97ED43F3FF2FCD1B7B99AE0D56F6C50B2196DE0C7B91BB2D6DEA613EC545ED26B55BE9387729383E6338EF8929385FD1DC8A5CE72A38G5DC3AD037B15
	AA6E078A722EDE0C79E11F6E5919967477BCC7E272696FF9464764534F076F50EFB5B65D951D8FEBA83E622E68FCF8F7G73E19534A55C0576AE08F84FEE8D600B6E3E18FC38B9022F9CF82AAF0AEFD244F974DE60314660E33560F7BEA67348A70B5DA279922455B5C872CD4A5D44369C9ABFCE782D5C0D4FDA02D6E22FCF7285FCB6175F85A90E51A6DAB15F642A34D547A36B4C6B7A61161F6FCFB8BE5279BE63FF55E62C5B7BB45E7361F7D1864ACBD0A265AD8E06657DC699C99E73617BB46F7077EEB53F3F
	95387CF963714BAA034D4B2C3E61F963613E61F923CA07EE1B8E5DED8343696E98505231F1D8B1A028E3EA3312F541AB76D72A9FDAB538BC22177C93102A9D3B36D7D76E5C35151FA32774EB4FA3F73FB2C677FBF8BAF7B3D82CB770EB427FCCADE07266DA89CF375D6D97D5FD761F8B6B532B3CE92C4F47C41D6DDBF19F7A6CB9E2DB88E4A4E7603E730F036F8B467BCE0BAC79C4D999E0D87A25B87E7F13F3C4CF754C0EB571365F5167326A77F56C81BF7DBC117D2F29129EABB8A16D57C231A2144862B6D934
	BB942194BFC8734B42C65518E6DA3D2E6C5BD5D7780BD905A62128D9F819D5A14DD043C3A951AC9453CA23D9A804AAA4C87E03CB11967CE705A141940DD504BFEB3FDB2B7449922482CF255FA7F6393ADDDD942B88E591C432688F38953FFE0F1702D50462EEC5F2FF2688C5BB44ED9F01412F8E9F3DFD93FF70C45A2906B213E0821B1DA2B332381F549054CC624CB1D4GAD0BG9A7977CD9734F117BF364BB06E165E4362ED45BD515FB7A539C82C145D8BFB71FBB1D6F21D5B0CE2E5DF27526A313B5D0A5DFD
	E61F57C14EC1768FA8E2788CF72A0B746FC79A5E48EBCB97513BD743738FD0CB8788993DBEFD3C99GG70CCGGD0CB818294G94G88G88G590171B4993DBEFD3C99GG70CCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7699GGGG
**end of data**/
}
}