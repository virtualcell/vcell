package cbit.vcell.opt.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Hashtable;

import javax.swing.JScrollPane;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.opt.solvers.OptimizationService;
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
	PopupGenerator.showErrorDialog(OptPanel.this, arg1.getMessage(), arg1);
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
	if (optResultSet==null || optResultSet.getOptSolverResultSet().getBestEstimates()==null){
		return "no results";
	}
	if (optSpec==null){
		return "no optimization specification";
	}
	if (optSpec.getNumParameters() != optResultSet.getOptSolverResultSet().getBestEstimates().length){
		return "there are "+optSpec.getNumParameters()+" opt variables and "+optResultSet.getOptSolverResultSet().getBestEstimates().length+" values";
	}

	StringBuffer buffer = new StringBuffer();

	Parameter[] parameters = optSpec.getParameters();
	for (int i = 0; i < parameters.length; i++){
		buffer.append(parameters[i].getName()+" = "+optResultSet.getOptSolverResultSet().getBestEstimates()[i]+"\n");
	}

	if (optResultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue()!=null){
		buffer.append("final objective function value = "+optResultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue());
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
		add(new JScrollPane(getTextArea1()), constraintsTextArea1);

		java.awt.GridBagConstraints constraintsTextArea2 = new java.awt.GridBagConstraints();
		constraintsTextArea2.gridx = 0; constraintsTextArea2.gridy = 3;
		constraintsTextArea2.gridwidth = 3;
		constraintsTextArea2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTextArea2.weightx = 1.0;
		constraintsTextArea2.weighty = 1.0;
		constraintsTextArea2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(new JScrollPane(getTextArea2()), constraintsTextArea2);

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
	AsynchClientTask task1 = new AsynchClientTask("optimizing", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			String message = null;
			try {
				String solverName = (String)getSolverTypeComboBox().getSelectedItem();
				double ftol = Double.parseDouble(getFToleranceTextField().getText());
				OptSolverCallbacks optSolverCallbacks = new OptSolverCallbacks();
				OptimizationResultSet optResultSet = getOptimizationService().solve(getOptimizationSpec(),new OptimizationSolverSpec(solverName,ftol),optSolverCallbacks);
				message = displayResults(optResultSet,getOptimizationSpec());
				hashTable.put("message", message);
			}catch (Exception e){
				e.printStackTrace(System.out);
				message = e.getMessage();
			}
			hashTable.put("message", message);
		}		
	};
	AsynchClientTask task2 = new AsynchClientTask("optimizing", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getTextArea2().setText((String)hashTable.get("message"));
			getOptimizeButton().setEnabled(true);
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
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

}