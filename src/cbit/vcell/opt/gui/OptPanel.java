package cbit.vcell.opt.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.gui.*;
import cbit.vcell.math.*;
import java.util.*;
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
	cbit.vcell.client.PopupGenerator.showErrorDialog(arg1.getMessage());
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
	swingthreads.SwingWorker swingWorker = new swingthreads.SwingWorker() {
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
	D0CB838494G88G88GD4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD4D46715EE56346ED9B7352736B5D9CF561324ADC9E86A2626F5D35B1216B634CDB616E2CBDAEDE8E29B3BB5295BF8FA58DD52BE87D0C83012C892333589AAAAC6C4FEF5C4D0C1A020030CB04270AB0F19878C3E79714D63C744F46F77F35FBCE65EE3142D1CF34F1BF73FFB3F7B735D6FFE777B5EFB88F96FAF4B5CE2499304AD19827FBB3EC59092EC0290BF7DC886622C879333855D5F9E00B7
	000790B4C1DBE1B0B1BBD7C871E741FDAF5CA7DCCD4CFE87FE6F94FE699FB887BFCC64D8212D6F9B5FF9E4F6B95EF3A8E79B3DDF131F8357BF818CG0EDCE38F9279AF656F6278799C0F3045A288530504A6EB4FEE0E5B84B4D9003382588A993D9B2ECC01BB2B2C1C2E7713B142666FAB166D249FDD1F88711CF7833F668B7F3DE551A1A22BF4CF8E674907FBA7GB1BEDDF8210158D5ED3FB2FC4AB568D10B47DD12AC977B83EA717018272A2AB5205AC51FA483CD4E87241FEBFB32A81FA0A4C19FCBA82E3218
	74ED075FA3G73BEC07CD7CB88FEAD7C5E8C10F3986D29B9D84D75F5DE7F2A10D4664D58E1E2CF41E1E6076DF0481E0D72BA4B3B5477DFBCCFFC3CG5AE28162G5683E494A5E697G7C190E790AF39F4035F6D0BD6B77FA7D3E5AC020CEF6ABADFE37A4436F2AAAE86138B64965D75C02404675781EA283FDE6834657F04BFC4CA7B165B84631ED0F88EB3F7C994BB6831F2C0D39EE29AEE23EF09405FC42E4FFE4AADBAD1A9903B3E5F78AFA59C54F2EC94FB4105D745DB5698BF8BFCBF532130B48D8BFC8E3E0
	957C5E427BE5782FD07C6EA3A1BC0B471394CF6E2D50367488460673CF280BAD63BEA137743306FED8722D0596211869302BB8DC174B25246F7AE2A673ADCD970562A7F5F8264B57E91C127BD8E8F39ECD4CE671751596B2BFB3CA923373812CGF600G40C200E5A598E32F1EBBFA13B1E6979535EE4CA33BA5C5E03CDFB97FB6DC25C9D5911BFDC1D57439248ED1710803329494B84DB74F939F8C4E600B27316F82999FCABEC991D50F5F873D0BDEC91514A023F97A7C9B04C611E82B436315C000B7A0E03FEB
	4E6F042BAC8655CB813728CAA18A563E6EBC9913F13183FA048E60B7734B8B97083F62407EE4008D1A9F1CB6525FF9C90104D1D5E5737B2F0C859C449241DACA7C7CE91ABB5200BE2F94FD7CDCAB41CD437D62D26C679336B7F57D54FBDCC4FDD13906F36CCB4539E446G7DAA0094G218C6367E5A746CF7D76AF8AC5EBBEED98BF2BA016734BD85CD84B426751AA5ABF51F1G5A6681ACAB43FCDFDB6CA47CBA3937B2574D0058924AA15DD4C6A63F4F7E9B74CFA01DDF195FDBAE52B1847D57822415216F3FEF
	4BE731DCA786A50C5ED68582CFF207C60269A9159672F9738E0F293FG3C4FE59C81A827F424DF34ED12726415A7E6D783388196822CGD88B10D11118DD8460G182EC01FFF3BE459CD5807F2D01FF743F4B263BB227BDD7E7F70AF5375651B529575CC67633CDDB7562F4F4237A34C5E396A4A46BA2E0574A70DCBBE35DE8AC8BEB77C887236A7DB08AEBCAF8D79B51E27A9BEGF96CACA40E20067FD18B51C7D4D545B3B826CA6D122AFAFC4321761FD1D9413141C0A885222C67DB088F7C931047B47A97DBC8
	8EF079FDBE09861D46425BFFCDF310A4CBDED07A2CFF4C27B27CEFAF107A6C0AF40D6740D7AEEC95C88E1347C8BEE5B54FF21178B820F83C904AA4A58B1BABC8FE0A5D33CB672387GC7EA3784382E2D407CB2318F73CB415403C25C650045E82DDC7239CFD82AAB591C36D706728B53E9FD2B316E7FC1F3A14787192E9B86898E4B30F4CA372D7F9D0673F8BDD3B4373787A4D72368D27D10A499CD5A89B29E7EB01AD29EF3BB053777660537CB4A3847A515327C7031B622E3GF41F8F3034927343101D34E3EE
	6609612CDF1659B0C1DBEAA5712565E81E4E9FABAA19AF5743F56498E9173576B3F06FBC465A49BDEBFF23845B1743FD022E1D6951FE11646820F4F5CC02A513ACB26735B16F3E78E6D8DB879FFF56BEF8110C3F2CDB1FCA0E93190F16E2BD1EFC0C657DCDF09520AD7FB8133D12524DABC33AE938A739EC71F1C69F8357E240337E2C65C817E1CD4C5E8DD0ED8D75CB7071D6C277278A2DBFAB4B0924BF3276AC366A65AE5768F2CF303CD9F8A2249F11BBFD0250CDD5A21D7584731B7D8413AB4375BD406F2EA2
	F4754710AE01532570EB6A89A617517DC1239B017BF9G4B2AB8FD9523E3F23FF5DC536FA4EBAFBC49564E2613FA398B2DC867BC496C8DF07AC5A7D9FFAC76DFB9C466A35D1B0C0A6362E4F1F082F2CAD555B9BA9758FE646D3DD91CFE75C95CF7546DA7311BD58DFE8130D6635C7D61F65CF7AC3E92A3E47DB4A95DEC9FE52FE6FA285521393B62B39369ECEE2CEEA7B2D7C0DBFCB54E0BF5977535CFEDB068F7F9683462B13730CDBF37996E13ED2F41F548238455E6C83E13981F1FEDA37A123A0C2339BFF6FF
	30977D6172FB0BBD035E6231F1EF31B82896CBEE0FDAD505A9006F55CE9C453DDA3266039B0DFA9F2C5AF143723A098FF2EB186DA5B53A3D57E33FCFE7B573270E66703E83509E8310D2C3702B0F127CCB620D49F315233CC5D31F9386BEFD0F293C02D3CCCE7D29103C79F915DC1E358465CDC37B0AD3A8EF2C0448A3F14668FED60AF49B012660F4E2B64BF7F5E504CE9D7184D9FE7A63FEFDFE6ABD4DEA52799A7DA823078510597145774EF05A279E39CF48926F34986DD9977EFA583264B453BF6EF4480E26
	24F516DEG266303E528E35619446CB3E7F0AF1EF70ADC69DAA5E01E5EDBCE7468B85BE2E3FACCF7219E731EDAA16C5A79CF06FA9018BC4364AFBB9352A36F79F5164267D18F319C755884B40535B06F29EFD23B743E99G7C820058DA74CDC2F999DCC51A700B5945AEA9C3FE45AB398B49CCAD96A7A0A4C9DDD1DCCB1B9B486FC3385FBADE01F263084CDA4D7EC9925F616B9A6BD196FD43456DD0DD78068F613ED53522AFE2939F931C23F7197A22240E7940D99772C56E26A73795GB01D1E55F4FAGE85656
	212DE7AA097FC5E53814515DDF09F4F9E79333EDE7116ED7C749FA943E5E323662E398FBCC6F5F556AE3E965360506FA0FFCE109E57ED9FE26F4B62477D3B0FEA95A98FA0F65707C1086B4957528CF9245FBFC9E55A34A1EA929709073F1407AB6531366504B4DBA028E746BFDBB757AD57F355FD03F85BBC6ACF2BD53EBF1FDC83F149F2D33EC7E916AF770B86A1782B43B9AB0461E6FB60AB1FB836AFF5771394698337346BBFBC263A1550D292A5FF739B62C9FE2B20F495F9C47F1F5038ECBB5BD7E31E4E778
	3832BA094F4FF1AB0EAD6B67B1AB5A1B84FD64B4E2EC5F7FBA294724C91794A01D343ADCE38AD4CE826A7B31D23277A075EE036AEF293C5C90CED9E88B4D834708621FA0054BC930006DBB7E3CB77C6C273EB134B7F5B6C66ED379798CDDAB65C6F6A6723A060F277805BABC332F096249FDAC3465FE087B781ACE32D62BF03F88A086A08EA085C0E842B5727DF6E2632C67A44DBE9516D0D1EE1F31D140730AF662F3286D797D336D7AFC497CF06A285999586EA6633DBA537FE1A70947922031813881268116EA
	3A8FB8226A5EB0E928BB536BEE2A57AA939D0C0071FD0172A55D829F1B43073A75E74699EF1C33249B4CE1DA1BB6333E731AC3F3187944C167288370810085G4BG621A51A78FB90A2279844F3238B37898154C4D973612DB7585637BC5E934B359146619FDB23E5FC6654BB8B7130F4D6527EF907F98542D8EA8A7EA95C96475593183D83B6EBE077368219ED29BBB60BEG30G608100840075GD96751773FF71C0C9A0FA4A1BA7CAC691D8D5FC963D9DA595CFC737528FC0567E772B17BBE68A3761D0136DE
	G95E091408A0055G1BB57B1AAE55C533CFBB6E849B715CA152CC2E6FF0795C6C9C0B4A17E3B30A019D6D6F9A47GDB0A200D1DB924D9424F9CC89DDAGFDDA81D81C9C6D46B8315B70CC2C26E2EE761C0F4A3740E6B4EEB17DE45C16C15BAA00A400CDGF9AD0959A5G0E969C37FF3EF47AD646ED5658FCEE0EB63E9415CFEDB162FB30F2EE723E9A15EFE10B11CF5F9FA0BEFDG5A62G1281B6836C3AGF59C0073827A746568F3C11BGB7B397024742758DDC18291F311DFF9741D79CEE6785A3BB970B54
	CEE80B83C886588C302B956C84F036221D359DB5516C64B96DAC601A7C7EAB86A6F2DD0F9C1F1B0DA7A37842ED1CEC1D494766683BBD3857DE540AF374BFA37A3A39F87AE3D43E04D6A33E2FD967E67377A2785E8B57B3CC9EFB1ED3C64FDED221AD478E790A62F69490DC895CB759518F2B68DEDB067B79GCB81168344EB6D5F276D649CAB594E768129F05DF5915BAF6CF79A780074C72ECB6D467540F20E1F4FE51979122F8F06B252F5B4C63C78EC066466594E6DD9FCB7F61A1F3737F49A1F3737F79A1F37
	F7F53E417050077E29A2360B1D331F47FBBA0D4E635998DE2267C6D6703F6DA24E4DAFBB4DF5FF5CE92CFB1C53D8776FBAE7577D5F1DE63A7F050E490FADE48F220C79E0D3A6512E591AABCD12BD9FE40936B19FCBA12477E0F8ADAAE0DECA9AA4FB37F91A0DD2D7199019FF9A5C9E552FE0A78EC9D4DA070634A4CBF67928F7EF37390FDC5D46BE72F49B7B485FBD3B0FA63AE777510BAAF93E94717C64822C3C821EAD6DAA4B96C2FB4E8AEDAE1CBC204F87CBAF4E1CBB4BAFC64EA57E2EC50F397DDF6BB1367F
	099EF2869B740FA9AEA95C46041EDDDADBA853B72B12171841E8FEC879D5D19916A2FC14CC79D91B997FAFFA4C7C484FF54ADE67BE328FA9FE2F3D0277DD51E7EE6B49BEE3DBEB7B4CEDED6A0BEE6B45BEF3DB1DFD51ED957B08AD039E37C761CFEB180D77166B6320D240B52DA29D6B4E780BF836FE320B4C3DB4384FEBCB4CAEEA439CDD51ED34E6553771F337364818E132550A997EEDA96776BC5AEF6E5FFF6BB7766FB77B4D7D7B5D7E687EFD265F5C3FBF6E0F6E5F677AE70F2537ABE74652B10ECF29C47F
	5BD53F3DCC13F9E8405C87A58346BEB8B6E06E031A0168BEE89CB077C14BC0F49FF48C0447984BC75FFBFB874DE741209424C5DC3D280A5C7E070E69626FEC29A66FDE515C7E4F094676BFAC1A5B7F28985D7E2F096676FFCB0CEE7F8F4459E340FDDCEFAB99EBB6FF5E6BB11ABF32413C19B9FF8A2D7A5889BD5F7D1DD5AF27DC43B76FA54F43E5711AFFCC95040DEDE4CE8F6C45BD68BC38DF8290871054467622ECED7D5789B22EC330F383F368221750CEF88F94A0EF855CB7810CGACG08E947B3D4E79651
	858FBFC2BA3E3CC30FAF537053BE629376142F1463BA36D31CCD9C8C614A25902E0CBFF7AE19B2FA2E136B40F3E74BA9FDFB010335B7E96D97AA0D5AFB9D6807C5A3C46638B86A38961078DEBFF5E23FE0788EA5FDFFF2468381AD161287376B63042F4B2C7EEA2367C28110B95F01F575478B312E0EF1E04EBD119E9EB3290ED03C64DCB2BB737D924D617917424FFC3FCE71E7AE051F7956F661196FG3445DFC21D4E53F309428E386FC05C72EE5433DE43D576A05DC887BE27DB30975FC7D581975B017EAD25
	FBE26D5C041C72D7D5FD28F85CF562300DC6ADDB1BD6533D8761DF5F01EB539FE81F3BBBE1DF89E06B44E71013877099E46C676F93525698BF77CDF15C3038BB194F96F4064EC2856FBA8B13E9ABC01971501E56C91F3597686305446541CC36E7E46D31B35AC9EEB8A4685BFFBE231D681B1D21EF4F1D711CB2D3474FEC7B41FE34AD6D874B85793179266F8657BB797B957C3AE87D5DDB823F1ACAE7368DCF61DA3F4C09E3714A104973F7BA6378B366D348176C44732ABBAB91175B05B940F300641DC13FC293
	AA4BBB738EE06EE8823A810005G4B3A58BB8DC2B7213937301E532C6C0A0C67AD5C675B7875A920C98140F72BB6F731D85E224DADBB2DE91A206F5EEEADBE37E24C4E6B46397598FD863A9A6EB7GE474A4E697GD8FB34F752E8BBF927033C8F61847CA440922D7D43AE237DA48152177E1E74EF1663E98E82FA2207D54747E48C184A885F2FE64E52BF469D7117FE8F4B46ED309B1F032F6CA16F439F98A5D11756C37C76E4772ED97AE4744DD784CD5F3BA21ED1C5834637F40EFCEBEE190F6541557B687B43
	3D2158E178E7A95E2E4333DC16318FF3D9AF34457562D9FE11CC7CD3501718FD86408990GD884905B27BDD7F21588D14E3C9C7E008D0AA0D9378DE476DDAC185B3B66CFEC431C3162710704159F2BB7FDA7A32E0F45DC8A3F963EF07796F2E576FD53CB6C4B6D07FDG009DE084E09AE0F1BF5A9763624F0848B69AF64D22EB043E8123ED4ABD52C488CD16C8E61BBC2537AD26FF669CD9559FB9E7783325D3C6BED94D69571B7259A34E408824F27A1C4B11FCEC6C7B8F62F363FC20F1DE465C79647F12B334D0
	CDC03F2628A343D8C7F3E01DA47AF01F757903382EAC3A0CEBD9926DB7816ED3GD2AFE3EEFD26D04B2D8309596F8B6CDD52AC817FB61DB642313998CF4AE49E833F484198587D707DC27DCF6AAD7BCCE220E900593EE4A034A626797353D9FE98CE0F2CA7BFCA473C309AF8B6G640A10BFC5F63647723AEDAB6675EA51B82FE3CEA2E0879A37984A6B13E2E88EB2BD7E65BC71D71B54A609EE760805619F6DA57D354EF88FGF9628A48FB026DFEF9DCD2C8B987D3F050DF671F4476747D217A321C63CEF7935F
	B6BA7C3224101775C9755A6811E4B72FBFE3FA746B75FCEEF32C08756593756175A5E90F535A9FEE5037A7F17EB491EB19E7FA744FF5F36F11AD6807F09F668E72F77886C363467AC86853578C7907DB8DEB06543EA90B1D732A3ABEB6405833B973E392B2782CC6777B35FDE66B525F82180CFD37D5864BDD1532797E33DAB65EFF56494667F9CD726C6731F6597C7D68CED97FFEB44B3D52D592A7B1B0AE71G9BG52GF2DD18F7FFA913F729581321701C5B609EA67BDF7235092A1D35F94DEDFD469B7D9B15
	E4AFF917F1508F734BCB7AC77C4F3DE43CD4FF00A2996E85AF711162999ED199A93E536DA5B9CB1606A81A55E6FFBF05FB21A29756EB2FDE47FA4D2E61AE51BAF4995CAF57F00F1FC23AD59AEE580AF4B9EE44A5D811EE179B6BB77741E8EB6B085B68D90A9215AF7C598A63FBB8AADFB8301A242910643D796E70DA65A245AFF20757AABFBD08354AD2E893A434B729D0AF5F8D78C9E9763A144561379512479782ED8C40D3G6B81D281F207A06783D881ECGEE00E900C5G8FG2C84C8GD88B30E98863B6BB
	C076B79D62E86469BBA079C610862FA2FAC878F23BBE1F0EF6E58CA7E6D78F63BADCDEB663B9BC5BCFB64A7E899BDF32789A7CF2BA2E416AB02E413BB3C81F31F09F8F30F69857609BDBF18D5EBCFC33EBB0564331C4A4BFB79816CD5E935196A2A66F399E14D7B402BA2456A34EBD02366EAF5CED5C1F36C8B15E679B32F85CAD9D41DA35EBCC5066E1EBE03F5998C076B887DD378E0ECAAE55A18EEBF19098A7EBFE92743789A04F837395608C005303E33BB8D0B84B583281898DAD6371DEB54FCB6A5568F969
	46D57A5EB564933242B35C9DD7C94D629F9A8ACADA7E7B7855DC8D579C347B039EF2E4436A1958A92CE7E48F5B274E076B928FFBDE496A193CE92CE716FB0C6B19EDC2E81F9ACFF8BD21FAE623A73C1E11CF4D5CF7FDB8C903084A7A61A91415EAA28B7BA110B38AB1B34AE4516FFFC7C332F8DDF8AD5477F3D66C3BE854386F4CD97ABE236B1B1D7B466DA4350547A7FBFC52D976352200B96F8289877E3BD377BB65606D2F9F7E6B364AE06B4AEFE82DEE63BEE138CD946766B896FBBFD14CE37C67CA7498FFD1
	B1DEFBDFD2A2575E5FA946EBEF2A22DFFB79F72D8A996BE14839CE0D4E92C4DC17065BC65FEBA638CE0FDB9D51705B032FF1FC13C484A2FEE71068B5C1F1CE0E5B2543F5F15CDF02E41E0249A3FE85F97786493C987248B23BBFC0E9BCBE18361ABF8ED39DBD81D127F709066B52E2A0B6627C7466E26771B9723DF24BFCECAD7FFDA6D907660D062F71FF2478052361EB7C5E8CDC8B17C0DB5AA82E71F7EC554B9F3812182DDE19FD0DE7FC6B35E7938B8C68179A60985F2B75C676AE67747126FCAFB6A0DF320E
	E6A37FAD48E64F1F2F0D1B4F23F963516751BF0C9B4F23CF0CC74E2345634673681EF17DBCE26311B425FDB3G3A5BE4DC43F7DF471AE33E0CB547396B74BBFA38CF82582851B6CC63FA1F613D55FD7F0BD3640E75E3B90D7DD49B7603EB427B53A11E5C934823828FEB2F3BA18838960887F45F9A5094403FD71A5C9E42E38A60E31CC67F77867B1D39B5743BB06376677E5A5BAA0365793B37C6667EFB376AF3BF437D8F2D6DE2412F89DE5C0F38E9ED37966EB7EB38BCDA33E579E00F62C35C35865C3794E838
	BFD0DC935C3BB55C159A021B047B859AEEF58D72AE76E1FE7869545C76BC9B2272996FF976DC1FDBBE7C861DA3ABFD617970C90A0F770567C37BF54C072B212D420F761E1B564BDF8278D87F4D6443ED2775FCF14013F4D3FCEF1D5073EDG1E34A8FC2C96FC33174C2300A28DC964CB10F64F9449B7CD81E25B61DEB20E3970FBF7G1F35DC2F275F6E7A83E3018ECF5083A90E51FE2A9E734DC20D76F0434CBA737AC7F04B4F7797B54CF63E4F785FEAB4572D38116E3561F72CA66F0E1A19726E7DEBC85E0BB5
	3349E3BE0C3FAEE83F1F567D6E38FE7B73067F364AE073D21C0E4C9BC35311F9633D5311F47BCEC752951D0824ABBD2127E3637084F1A59F532EFA32AE782409FC6DC32BFA0FD77211FF82D2D8DADED214DFD6FE081FA3AF1B501FC7DADF3B4370FBF8BAF783AC56B772EB5ADB77EC616FFF54931EA1D18ECA1ABE07ADA1FD86EA5EB35767B8D12764906EC313AD4436022B30773C0A7B4E2DDB8E8B667BCE3B628FC80AFA0DE1691762787FCFACC4CF6D4C0EB5713675649FC8053E2FE38F786967096CFFCDDDC9
	F70093E2DF92B2D5E11BA29DD1A4512D8AD978C1DAD091F6E846B4536AB5E53C3CAA7D057B53ADC2C6333015D5A14DC286A9A51A05CCDAE9B48B69D005CC127F6012210760D561A341920ED5053D7A6F5672830A9FD201B7BF9810DC1EA10F0BE2D5A1EF8611A28547E4B5E85C632DE0D5A1F3C8754BB52A10D1AA9DB986863F99B9FA35F7BD7B13584B75F9964182B60FC84C484C89D2C3D0B309B36F2082C8CAA3AEEF645FB77DD59FF729F73ACC63CE8E32F81B978C7F3EA9B513444AAA40A787356FCEB20F18
	45CA6D205A6E95E5D99565AB35BEB7B987293B26CA21B35CD7B3C97FC124610D2CADE3AB51FBD44773FFD0CB8788F2C54BA04899GG70CCGGD0CB818294G94G88G88GD4FBB0B6F2C54BA04899GG70CCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG829AGGGG
**end of data**/
}
}