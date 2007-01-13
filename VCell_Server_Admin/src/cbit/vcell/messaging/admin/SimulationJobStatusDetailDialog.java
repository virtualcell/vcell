package cbit.vcell.messaging.admin;
import javax.swing.BorderFactory;

import cbit.vcell.solvers.SimpleJobStatus;

/**
 * Insert the type's description here.
 * Creation date: (3/29/2004 1:36:39 PM)
 * @author: Fei Gao
 */
public class SimulationJobStatusDetailDialog extends javax.swing.JDialog {
	private static java.text.SimpleDateFormat dateTimeFormatter = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JLabel ivjJLabel6 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextField ivjComputeHostTextField = null;
	private javax.swing.JTextField ivjEndDateTextField = null;
	private javax.swing.JTextField ivjSimIDTextField = null;
	private javax.swing.JTextField ivjStartDateTextField = null;
	private javax.swing.JTextArea ivjStatusMessageTextArea = null;
	private javax.swing.JTextField ivjSubmitDateTextField = null;
	private javax.swing.JTextField ivjUserTextField = null;
	private javax.swing.JPanel ivjJPanel4 = null;
	private javax.swing.JPanel ivjJPanel5 = null;
	private javax.swing.JPanel ivjJPanel6 = null;
	private javax.swing.JPanel ivjJPanel7 = null;
	private javax.swing.JPanel ivjJPanel8 = null;
	private javax.swing.JPanel ivjJPanel9 = null;
	private javax.swing.JButton ivjCloseButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJPanel10 = null;
	private javax.swing.JPanel ivjJPanel11 = null;
	private java.awt.GridLayout ivjJPanel11GridLayout = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private javax.swing.JTextArea ivjSolverDescTextArea = null;
	private java.awt.BorderLayout ivjJDialogContentPaneBorderLayout = null;
	private java.awt.GridLayout ivjJPanel1GridLayout = null;
	private java.awt.GridLayout ivjJPanel2GridLayout = null;
	private java.awt.GridLayout ivjJPanel3GridLayout = null;
	private java.awt.FlowLayout ivjJPanel4FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel5FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel6FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel7FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel8FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel9FlowLayout = null;
	private javax.swing.JButton ivjResubmitButton = null;
	private javax.swing.JButton ivjStopButton = null;
	private SimpleJobStatus jobStatus = null;
	private ServerManageConsole smConsole = null;
	private javax.swing.JPanel ivjJPanel12 = null;
	private java.awt.FlowLayout ivjJPanel12FlowLayout = null;
	private javax.swing.JPanel ivjJPanel13 = null;
	private java.awt.FlowLayout ivjJPanel13FlowLayout = null;
	private int currentSelected = -1;
	private int totalNumber = 0;
	private javax.swing.JButton ivjNextButton = null;
	private javax.swing.JButton ivjPrevButton = null;
	private javax.swing.JLabel ivjJLabel7 = null;
	private javax.swing.JLabel ivjJLabel8 = null;
	private javax.swing.JPanel ivjJPanel14 = null;
	private java.awt.FlowLayout ivjJPanel14FlowLayout = null;
	private javax.swing.JPanel ivjJPanel15 = null;
	private java.awt.FlowLayout ivjJPanel15FlowLayout = null;
	private javax.swing.JTextField ivjServerIDTextField = null;
	private javax.swing.JTextField ivjTaskIDTextField = null;
	private javax.swing.JTextField ivjJobIndexTextField = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SimulationJobStatusDetailDialog.this.getCloseButton()) 
				connEtoC1(e);
			if (e.getSource() == SimulationJobStatusDetailDialog.this.getStopButton()) 
				connEtoC2(e);
			if (e.getSource() == SimulationJobStatusDetailDialog.this.getResubmitButton()) 
				connEtoC3(e);
			if (e.getSource() == SimulationJobStatusDetailDialog.this.getNextButton()) 
				connEtoC5(e);
			if (e.getSource() == SimulationJobStatusDetailDialog.this.getPrevButton()) 
				connEtoC6(e);
		};
	};

/**
 * SimulationJobStatusDetailDialog constructor comment.
 */
public SimulationJobStatusDetailDialog() {
	super();
	initialize();
}

/**
 * SimulationJobStatusDetailDialog constructor comment.
 * @param owner java.awt.Frame
 */
public SimulationJobStatusDetailDialog(ServerManageConsole console, int total, int selected) {
	super(console, true);
	smConsole = console;
	totalNumber = total;
	currentSelected = selected;
	setStatus();
	initialize();
}


/**
 * Comment
 */
public void closeButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	dispose();
	return;
}


/**
 * connEtoC1:  (CloseButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationJobStatusDetailDialog.closeButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.closeButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (StopButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationJobStatusDetailDialog.stopButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (ResubmitButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationJobStatusDetailDialog.resubmitButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.resubmitButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (SimulationJobStatusDetailDialog.initialize() --> SimulationJobStatusDetailDialog.simulationJobStatusDetailDialog_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.simulationJobStatusDetailDialog_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (NextButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationJobStatusDetailDialog.nextButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nextButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (PrevButton.action.actionPerformed(java.awt.event.ActionEvent) --> SimulationJobStatusDetailDialog.prevButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.prevButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the CloseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCloseButton() {
	if (ivjCloseButton == null) {
		try {
			ivjCloseButton = new javax.swing.JButton();
			ivjCloseButton.setName("CloseButton");
			ivjCloseButton.setText("Close");
			ivjCloseButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCloseButton;
}

/**
 * Return the JTextField5 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getComputeHostTextField() {
	if (ivjComputeHostTextField == null) {
		try {
			ivjComputeHostTextField = new javax.swing.JTextField();
			ivjComputeHostTextField.setName("ComputeHostTextField");
			ivjComputeHostTextField.setEditable(false);
			ivjComputeHostTextField.setColumns(18);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjComputeHostTextField;
}

/**
 * Return the JTextField6 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getEndDateTextField() {
	if (ivjEndDateTextField == null) {
		try {
			ivjEndDateTextField = new javax.swing.JTextField();
			ivjEndDateTextField.setName("EndDateTextField");
			ivjEndDateTextField.setEditable(false);
			ivjEndDateTextField.setColumns(20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndDateTextField;
}


/**
 * Return the JobIndexTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJobIndexTextField() {
	if (ivjJobIndexTextField == null) {
		try {
			ivjJobIndexTextField = new javax.swing.JTextField();
			ivjJobIndexTextField.setName("JobIndexTextField");
			ivjJobIndexTextField.setEditable(false);
			ivjJobIndexTextField.setColumns(4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJobIndexTextField;
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
			ivjJDialogContentPane.setLayout(getJDialogContentPaneBorderLayout());
			getJDialogContentPane().add(getJPanel1(), "North");
			getJDialogContentPane().add(getJPanel10(), "South");
			getJDialogContentPane().add(getJPanel11(), "Center");
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
 * Return the JDialogContentPaneBorderLayout property value.
 * @return java.awt.BorderLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.BorderLayout getJDialogContentPaneBorderLayout() {
	java.awt.BorderLayout ivjJDialogContentPaneBorderLayout = null;
	try {
		/* Create part */
		ivjJDialogContentPaneBorderLayout = new java.awt.BorderLayout();
		ivjJDialogContentPaneBorderLayout.setVgap(5);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJDialogContentPaneBorderLayout;
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
			ivjJLabel1.setText("User ID");
			ivjJLabel1.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel1.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel1.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
			ivjJLabel2.setText("Sim ID");
			ivjJLabel2.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel2.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel2.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
			ivjJLabel3.setText("Compute Host");
			ivjJLabel3.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel3.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel3.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("Submit Date");
			ivjJLabel4.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel4.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel4.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}

/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("Start Date");
			ivjJLabel5.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel5.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel5.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}

/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel6() {
	if (ivjJLabel6 == null) {
		try {
			ivjJLabel6 = new javax.swing.JLabel();
			ivjJLabel6.setName("JLabel6");
			ivjJLabel6.setText("End Date");
			ivjJLabel6.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel6.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel6.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}

/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("Server ID");
			ivjJLabel7.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel7.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel7.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel7;
}

/**
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel8() {
	if (ivjJLabel8 == null) {
		try {
			ivjJLabel8 = new javax.swing.JLabel();
			ivjJLabel8.setName("JLabel8");
			ivjJLabel8.setText("Task ID");
			ivjJLabel8.setMaximumSize(new java.awt.Dimension(70, 14));
			ivjJLabel8.setPreferredSize(new java.awt.Dimension(70, 14));
			ivjJLabel8.setMinimumSize(new java.awt.Dimension(70, 14));
			ivjJLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel8;
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
			ivjJPanel1.setLayout(getJPanel1GridLayout());
			getJPanel1().add(getJPanel2(), getJPanel2().getName());
			getJPanel1().add(getJPanel3(), getJPanel3().getName());
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
 * Return the JPanel10 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel10() {
	if (ivjJPanel10 == null) {
		try {
			ivjJPanel10 = new javax.swing.JPanel();
			ivjJPanel10.setName("JPanel10");
			ivjJPanel10.setLayout(new java.awt.GridLayout());
			getJPanel10().add(getJPanel12(), getJPanel12().getName());
			getJPanel10().add(getJPanel13(), getJPanel13().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel10;
}

/**
 * Return the JPanel11 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel11() {
	if (ivjJPanel11 == null) {
		try {
			ivjJPanel11 = new javax.swing.JPanel();
			ivjJPanel11.setName("JPanel11");
			ivjJPanel11.setLayout(getJPanel11GridLayout());
			getJPanel11().add(getJScrollPane2(), getJScrollPane2().getName());
			getJPanel11().add(getJScrollPane1(), getJScrollPane1().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel11;
}


/**
 * Return the JPanel11GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getJPanel11GridLayout() {
	java.awt.GridLayout ivjJPanel11GridLayout = null;
	try {
		/* Create part */
		ivjJPanel11GridLayout = new java.awt.GridLayout(0, 1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel11GridLayout;
}


/**
 * Return the JPanel12 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel12() {
	if (ivjJPanel12 == null) {
		try {
			ivjJPanel12 = new javax.swing.JPanel();
			ivjJPanel12.setName("JPanel12");
			ivjJPanel12.setLayout(getJPanel12FlowLayout());
			getJPanel12().add(getStopButton(), getStopButton().getName());
			getJPanel12().add(getResubmitButton(), getResubmitButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel12;
}

/**
 * Return the JPanel12FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel12FlowLayout() {
	java.awt.FlowLayout ivjJPanel12FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel12FlowLayout = new java.awt.FlowLayout();
		ivjJPanel12FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel12FlowLayout;
}

/**
 * Return the JPanel13 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel13() {
	if (ivjJPanel13 == null) {
		try {
			ivjJPanel13 = new javax.swing.JPanel();
			ivjJPanel13.setName("JPanel13");
			ivjJPanel13.setLayout(getJPanel13FlowLayout());
			getJPanel13().add(getPrevButton(), getPrevButton().getName());
			getJPanel13().add(getNextButton(), getNextButton().getName());
			getJPanel13().add(getCloseButton(), getCloseButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel13;
}

/**
 * Return the JPanel13FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel13FlowLayout() {
	java.awt.FlowLayout ivjJPanel13FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel13FlowLayout = new java.awt.FlowLayout();
		ivjJPanel13FlowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel13FlowLayout;
}


/**
 * Return the JPanel14 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel14() {
	if (ivjJPanel14 == null) {
		try {
			ivjJPanel14 = new javax.swing.JPanel();
			ivjJPanel14.setName("JPanel14");
			ivjJPanel14.setLayout(getJPanel14FlowLayout());
			getJPanel14().add(getJLabel7(), getJLabel7().getName());
			getJPanel14().add(getServerIDTextField(), getServerIDTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel14;
}


/**
 * Return the JPanel14FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel14FlowLayout() {
	java.awt.FlowLayout ivjJPanel14FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel14FlowLayout = new java.awt.FlowLayout();
		ivjJPanel14FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel14FlowLayout;
}


/**
 * Return the JPanel15 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel15() {
	if (ivjJPanel15 == null) {
		try {
			ivjJPanel15 = new javax.swing.JPanel();
			ivjJPanel15.setName("JPanel15");
			ivjJPanel15.setLayout(getJPanel15FlowLayout());
			getJPanel15().add(getJLabel8(), getJLabel8().getName());
			getJPanel15().add(getTaskIDTextField(), getTaskIDTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel15;
}


/**
 * Return the JPanel15FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel15FlowLayout() {
	java.awt.FlowLayout ivjJPanel15FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel15FlowLayout = new java.awt.FlowLayout();
		ivjJPanel15FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel15FlowLayout;
}


/**
 * Return the JPanel1GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getJPanel1GridLayout() {
	java.awt.GridLayout ivjJPanel1GridLayout = null;
	try {
		/* Create part */
		ivjJPanel1GridLayout = new java.awt.GridLayout(0, 2);
		ivjJPanel1GridLayout.setHgap(2);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel1GridLayout;
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
			ivjJPanel2.setLayout(getJPanel2GridLayout());
			getJPanel2().add(getJPanel4(), getJPanel4().getName());
			getJPanel2().add(getJPanel5(), getJPanel5().getName());
			getJPanel2().add(getJPanel6(), getJPanel6().getName());
			getJPanel2().add(getJPanel14(), getJPanel14().getName());
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
 * Return the JPanel2GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getJPanel2GridLayout() {
	java.awt.GridLayout ivjJPanel2GridLayout = null;
	try {
		/* Create part */
		ivjJPanel2GridLayout = new java.awt.GridLayout(0, 1);
		ivjJPanel2GridLayout.setVgap(3);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel2GridLayout;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(getJPanel3GridLayout());
			getJPanel3().add(getJPanel7(), getJPanel7().getName());
			getJPanel3().add(getJPanel8(), getJPanel8().getName());
			getJPanel3().add(getJPanel9(), getJPanel9().getName());
			getJPanel3().add(getJPanel15(), getJPanel15().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}

/**
 * Return the JPanel3GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getJPanel3GridLayout() {
	java.awt.GridLayout ivjJPanel3GridLayout = null;
	try {
		/* Create part */
		ivjJPanel3GridLayout = new java.awt.GridLayout(0, 1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel3GridLayout;
}

/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(getJPanel4FlowLayout());
			getJPanel4().add(getJLabel1(), getJLabel1().getName());
			getJPanel4().add(getUserTextField(), getUserTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}


/**
 * Return the JPanel4FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel4FlowLayout() {
	java.awt.FlowLayout ivjJPanel4FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel4FlowLayout = new java.awt.FlowLayout();
		ivjJPanel4FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel4FlowLayout;
}


/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(getJPanel5FlowLayout());
			getJPanel5().add(getJLabel2(), getJLabel2().getName());
			getJPanel5().add(getSimIDTextField(), getSimIDTextField().getName());
			getJPanel5().add(getJobIndexTextField(), getJobIndexTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
}


/**
 * Return the JPanel5FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel5FlowLayout() {
	java.awt.FlowLayout ivjJPanel5FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel5FlowLayout = new java.awt.FlowLayout();
		ivjJPanel5FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel5FlowLayout;
}


/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel6() {
	if (ivjJPanel6 == null) {
		try {
			ivjJPanel6 = new javax.swing.JPanel();
			ivjJPanel6.setName("JPanel6");
			ivjJPanel6.setLayout(getJPanel6FlowLayout());
			getJPanel6().add(getJLabel3(), getJLabel3().getName());
			getJPanel6().add(getComputeHostTextField(), getComputeHostTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel6;
}


/**
 * Return the JPanel6FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel6FlowLayout() {
	java.awt.FlowLayout ivjJPanel6FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel6FlowLayout = new java.awt.FlowLayout();
		ivjJPanel6FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel6FlowLayout;
}


/**
 * Return the JPanel7 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel7() {
	if (ivjJPanel7 == null) {
		try {
			ivjJPanel7 = new javax.swing.JPanel();
			ivjJPanel7.setName("JPanel7");
			ivjJPanel7.setLayout(getJPanel7FlowLayout());
			getJPanel7().add(getJLabel4(), getJLabel4().getName());
			getJPanel7().add(getSubmitDateTextField(), getSubmitDateTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel7;
}


/**
 * Return the JPanel7FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel7FlowLayout() {
	java.awt.FlowLayout ivjJPanel7FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel7FlowLayout = new java.awt.FlowLayout();
		ivjJPanel7FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel7FlowLayout;
}


/**
 * Return the JPanel8 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel8() {
	if (ivjJPanel8 == null) {
		try {
			ivjJPanel8 = new javax.swing.JPanel();
			ivjJPanel8.setName("JPanel8");
			ivjJPanel8.setLayout(getJPanel8FlowLayout());
			getJPanel8().add(getJLabel5(), getJLabel5().getName());
			getJPanel8().add(getStartDateTextField(), getStartDateTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel8;
}


/**
 * Return the JPanel8FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel8FlowLayout() {
	java.awt.FlowLayout ivjJPanel8FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel8FlowLayout = new java.awt.FlowLayout();
		ivjJPanel8FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel8FlowLayout;
}


/**
 * Return the JPanel9 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel9() {
	if (ivjJPanel9 == null) {
		try {
			ivjJPanel9 = new javax.swing.JPanel();
			ivjJPanel9.setName("JPanel9");
			ivjJPanel9.setLayout(getJPanel9FlowLayout());
			getJPanel9().add(getJLabel6(), getJLabel6().getName());
			getJPanel9().add(getEndDateTextField(), getEndDateTextField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel9;
}


/**
 * Return the JPanel9FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel9FlowLayout() {
	java.awt.FlowLayout ivjJPanel9FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel9FlowLayout = new java.awt.FlowLayout();
		ivjJPanel9FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel9FlowLayout;
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
			ivjJScrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Status Message"));
			getJScrollPane1().setViewportView(getStatusMessageTextArea());
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
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Solver Description"));
			getJScrollPane2().setViewportView(getSolverDescTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}

/**
 * Return the NextButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNextButton() {
	if (ivjNextButton == null) {
		try {
			ivjNextButton = new javax.swing.JButton();
			ivjNextButton.setName("NextButton");
			ivjNextButton.setText("Next");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNextButton;
}


/**
 * Return the PrevButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getPrevButton() {
	if (ivjPrevButton == null) {
		try {
			ivjPrevButton = new javax.swing.JButton();
			ivjPrevButton.setName("PrevButton");
			ivjPrevButton.setText("Previous");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrevButton;
}


/**
 * Return the ResubmitButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getResubmitButton() {
	if (ivjResubmitButton == null) {
		try {
			ivjResubmitButton = new javax.swing.JButton();
			ivjResubmitButton.setName("ResubmitButton");
			ivjResubmitButton.setToolTipText("For Admin Use");
			ivjResubmitButton.setText("Re-Submit");
			ivjResubmitButton.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResubmitButton;
}

/**
 * Return the ServerIDTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getServerIDTextField() {
	if (ivjServerIDTextField == null) {
		try {
			ivjServerIDTextField = new javax.swing.JTextField();
			ivjServerIDTextField.setName("ServerIDTextField");
			ivjServerIDTextField.setEditable(false);
			ivjServerIDTextField.setColumns(18);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerIDTextField;
}


/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSimIDTextField() {
	if (ivjSimIDTextField == null) {
		try {
			ivjSimIDTextField = new javax.swing.JTextField();
			ivjSimIDTextField.setName("SimIDTextField");
			ivjSimIDTextField.setEditable(false);
			ivjSimIDTextField.setColumns(12);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimIDTextField;
}

/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getSolverDescTextArea() {
	if (ivjSolverDescTextArea == null) {
		try {
			ivjSolverDescTextArea = new javax.swing.JTextArea();
			ivjSolverDescTextArea.setName("SolverDescTextArea");
			ivjSolverDescTextArea.setRows(0);
			ivjSolverDescTextArea.setBackground(java.awt.Color.white);
			ivjSolverDescTextArea.setBounds(0, 0, 1, 1);
			ivjSolverDescTextArea.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverDescTextArea;
}

/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStartDateTextField() {
	if (ivjStartDateTextField == null) {
		try {
			ivjStartDateTextField = new javax.swing.JTextField();
			ivjStartDateTextField.setName("StartDateTextField");
			ivjStartDateTextField.setEditable(false);
			ivjStartDateTextField.setColumns(20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateTextField;
}

/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getStatusMessageTextArea() {
	if (ivjStatusMessageTextArea == null) {
		try {
			ivjStatusMessageTextArea = new javax.swing.JTextArea();
			ivjStatusMessageTextArea.setName("StatusMessageTextArea");
			ivjStatusMessageTextArea.setLineWrap(true);
			ivjStatusMessageTextArea.setWrapStyleWord(true);
			ivjStatusMessageTextArea.setRows(0);
			ivjStatusMessageTextArea.setBounds(0, 1, 1, 3);
			ivjStatusMessageTextArea.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusMessageTextArea;
}

/**
 * Return the StopButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStopButton() {
	if (ivjStopButton == null) {
		try {
			ivjStopButton = new javax.swing.JButton();
			ivjStopButton.setName("StopButton");
			ivjStopButton.setToolTipText("For Admin Use");
			ivjStopButton.setText("Stop");
			ivjStopButton.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopButton;
}

/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSubmitDateTextField() {
	if (ivjSubmitDateTextField == null) {
		try {
			ivjSubmitDateTextField = new javax.swing.JTextField();
			ivjSubmitDateTextField.setName("SubmitDateTextField");
			ivjSubmitDateTextField.setEditable(false);
			ivjSubmitDateTextField.setColumns(20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSubmitDateTextField;
}

/**
 * Return the TaskIDTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTaskIDTextField() {
	if (ivjTaskIDTextField == null) {
		try {
			ivjTaskIDTextField = new javax.swing.JTextField();
			ivjTaskIDTextField.setName("TaskIDTextField");
			ivjTaskIDTextField.setEditable(false);
			ivjTaskIDTextField.setColumns(20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTaskIDTextField;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getUserTextField() {
	if (ivjUserTextField == null) {
		try {
			ivjUserTextField = new javax.swing.JTextField();
			ivjUserTextField.setName("UserTextField");
			ivjUserTextField.setEditable(false);
			ivjUserTextField.setColumns(18);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserTextField;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	getCloseButton().addActionListener(ivjEventHandler);
	getStopButton().addActionListener(ivjEventHandler);
	getResubmitButton().addActionListener(ivjEventHandler);
	getNextButton().addActionListener(ivjEventHandler);
	getPrevButton().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimulationJobStatusDetailDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(561, 428);
		setTitle("Simulation Status");
		setContentPane(getJDialogContentPane());
		initConnections();
		connEtoC4();
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
		SimulationJobStatusDetailDialog aSimulationJobStatusDetailDialog;
		aSimulationJobStatusDetailDialog = new SimulationJobStatusDetailDialog();
		aSimulationJobStatusDetailDialog.setModal(true);
		aSimulationJobStatusDetailDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aSimulationJobStatusDetailDialog.show();
		java.awt.Insets insets = aSimulationJobStatusDetailDialog.getInsets();
		aSimulationJobStatusDetailDialog.setSize(aSimulationJobStatusDetailDialog.getWidth() + insets.left + insets.right, aSimulationJobStatusDetailDialog.getHeight() + insets.top + insets.bottom);
		aSimulationJobStatusDetailDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
public void nextButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	currentSelected ++;
	setStatus();
}


/**
 * Comment
 */
public void prevButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	currentSelected --;
	setStatus();
}


/**
 * Comment
 */
public void resubmitButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	int n = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you would like to resubmit this simulation?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
	if (n == javax.swing.JOptionPane.YES_OPTION) {	
		resubmitSimulation();
		getResubmitButton().setEnabled(false);
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:15:52 PM)
 */
public void resubmitSimulation() {
	smConsole.resubmitSimulation(jobStatus.getUserID(), jobStatus.getVCSimulationIdentifier().getSimulationKey());
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:05:24 PM)
 */
public void setStatus() {
	jobStatus = smConsole.getReturnedSimulationJobStatus(currentSelected);
	setTitle("Simulation Status [" + jobStatus.getUserID() + "," + jobStatus.getVCSimulationIdentifier().getSimulationKey() + "]");
	getUserTextField().setText(jobStatus.getUserID());
	getSimIDTextField().setText(jobStatus.getVCSimulationIdentifier().getSimulationKey() + "");
	getJobIndexTextField().setText(jobStatus.getJobIndex() + "");
	getComputeHostTextField().setText(jobStatus.getComputeHost());
	getSubmitDateTextField().setText(jobStatus.getSubmitDate() == null ? "" : dateTimeFormatter.format(jobStatus.getSubmitDate()));
	getStartDateTextField().setText(jobStatus.getStartDate() == null ? "" : dateTimeFormatter.format(jobStatus.getStartDate()));
	getEndDateTextField().setText(jobStatus.getEndDate() == null ? "" : dateTimeFormatter.format(jobStatus.getEndDate()));
	getServerIDTextField().setText(jobStatus.getEndDate() == null ? "" : jobStatus.getServerID());
	getTaskIDTextField().setText(jobStatus.getTaskID() == null ? "" : jobStatus.getTaskID() + "");
	getSolverDescTextArea().setText(jobStatus.getSolverDescriptionVCML());
	getSolverDescTextArea().setCaretPosition(0);
	getStatusMessageTextArea().setText(jobStatus.getStatusMessage());
	getStatusMessageTextArea().setCaretPosition(0);
	getStopButton().setEnabled(false);
	getResubmitButton().setEnabled(false);
	if (jobStatus.isDone()) {
		getResubmitButton().setEnabled(true);		
	} else {
		getStopButton().setEnabled(true);
	}
	
	if (currentSelected <= 0) {
		getPrevButton().setEnabled(false);
	} else {
		getPrevButton().setEnabled(true);
	}

	if (currentSelected >= totalNumber - 1) {
		getNextButton().setEnabled(false);
	} else {
		getNextButton().setEnabled(true);
	}
	smConsole.setSelectedReturnedSimulationJobStatus(currentSelected);
}


/**
 * Comment
 */
public void simulationJobStatusDetailDialog_Initialize() {
	return;
}


/**
 * Comment
 */
public void stopButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	int n = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you would like to stop this simulation?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
	if (n == javax.swing.JOptionPane.YES_OPTION) {	
		stopSimulation();
		getStopButton().setEnabled(false);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:15:42 PM)
 */
public void stopSimulation() {
	smConsole.stopSimulation(jobStatus.getUserID(), jobStatus.getVCSimulationIdentifier().getSimulationKey());
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G710171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFFDCD4D57ABF9535D61838E1D2E122D2D1D151C606669696EDD4D4D8E4D46CC6ADED34D1D912D1D2DE9093858B0BCF393B3431E5ADEEE624EC1951060A0546AA85818A16434C059998E646198B8C3F747B1CF36EF36E1DBBF7E024583F3E3EDE0F1773BE4FB94FF31E677CBE675E91CA5E89DF151BD7A288392B84795FE7390290B2C7909A16A7CDC6AC5856D4A8287E7D83E83DF05921FCF806C3DC
	5D7A6642E2217563828847C0B8E7DDF361DB70779B42110F2BE6429FBE6414C05CBF5F78A6F7F4B97B391C55B49C57D5844FD701E281A34FFCFB9348CF695AA06329B20E141BA788CBA84F522ED2992B05709120A820B8BB63CF01679AAA79549D5BE53E362911C256ED4E3CF5A49FD51EC85B9DACED1B7CAC93DE4C4DBFCAE415F69749E9C2A0AE910894FE25D0333A9A1ED5755D1D7FB136DB24ADFDC651EA5D52A33ADC06CE0B2DF30B4154E3316D5851E06969359AA40B5D364F5E5EA09924DED70DA899AC56
	9A0B41EA6F8C20B2CC5D85B4CFA88F040B1C84FBF210480D07F0AAD09647E30688DE8FFF1B01821CD856AF7FD9A51725E568DAA13D7366B52F79A8EB281315B14E2914B5F4D37B4A3FD03F1C55CA6C1F83F1E52E2642CA2096A0B7D0A8505FE8FD18F668DF705C59AE6D36777458EDBB9D0EDDD613733FF613E8053FF76C0098997BDEB45A1DA6C1E0BEFF37FB330EBE2391CBD7F1426918CDA237217F233E3FC2C83D7A1C3C55BAB6C90D9C4A0BF6B1DBA438941BB059961F32B3DC1E7553D3F6332016ADBC3EF5
	75AA9D59250B372E4E1173A916945955927135017A3A827E4E156B87433BA99E2842D9DDDDCA6B8C8907C0DC3604F52365D55425AEFF36D07C49F4DDBB045E981457564BF410FA353AEC9EA6F90777B2196B38AEDFD0BCDE05B3DD66D39DC9B883629A7B1A8AD97D1AFE18345DB8882782E5819575B795EE862A85EA6B47BA7607C39F7BD9476A8DCEE9D72F45EA921D82CBBB3D75CDF80AEE49E958EBF3C9861BD1ECB2B8AD06F62B6892E41E70D6E203F60FF497523AEF84995F08B651C91BAD64EE6891A55169
	E2BCD7342EA7BCCE1146B6DA2045CB069E0700794EEDFD831ED603CB7A41E1B2C8224241622FEFA5BE69B3B4C10E10817C4D6C927FA33157B014BF84A8025B6173B6125F372253A6DAF76C28335B3BFB9D0D24A4C2D6BF31F3G6DBBE2616F24FE34F15CCF845B6EEEAA2CF7E3BE0E3657D57954D80CC4FD03F3G5BD86790691FCDEE2CB3CF35E01D29D9F73930795EE93AF5A6986AEF301B5515C837366D9C25F9923DD87805411693EC477E92511B49F8FDE822E42C643E5AE2A03ECA821919C0C583E8634B5A
	4AD81D5DE5F009D8CB8FB8210209A64562CC4F2D439BE4FB12FCC95FC0F0E27BC281FD321372EC047C6B00A420B020F8204C81BE0E8E9D99A51FDF2A6CCC5603D4161E1CF53F20ACE64BD9074978A57609B629C6F408B6937C611263A28F9339F27B6E3073B4D7D05C817D41EEE800AE0E479CA6ED56A0C9CECBFB2FA4B6081284C334923F004AF27536BB142E84E55DF41814555E8F7D8167CFB8CC5A1251EE33095429BC099CFF9BED4B22D56C8125F75BFBED924313FEA46D21DB9C107B12649FDF91C8DFE06D
	A57D921BD7C49B08BD9DCECB8FD49552358966C1524E27F7EFD0592884B0B2BF2A02E77DA036597E77304DEE9C3C84FA81C71E5E1893FA61143C68C156D6638715B64BF43A79A7FD5DEF27FD0A0C3B182E77F73F834FB126B8F21B69ECA77926033CECD21EA1ECB35F9CA17158AF490DE5375DEAE52665B62FB494939F09C7FBC5684EC98778AD3763970657B5F1CD32CDD97CEEEAD32B2A6F4C98A2B66D9F46F9E4B9042B010E8CB13B068FB13BB03EAFC6102FF2985AA210F51871C58D2B79569EC33EAC400BC6
	943E4DA3EA3E6BB91FFB0445874B4FC88FBE77F164CB87BC8728749863EBBB26662B9210AF82708547943ED4190F796B7B234456F44E5AE568B33837387A219D6C5831CF7614A03079E82E35C0EE5F9B0F633C335BC67C5786E189A868B85639C56BF05E99529DA924BE7E49EADFF36C0863729C6B38D2670427F73E426A47D5C6A233A02029F0C3G560D6B8D6A31EF274BE5B7DAE8C5137D5A532E2E138CF3362F05E70745691276C2276146BA60EEA77A12F1D90665F9CAB81D3F907BEC8639A6201CG025F52
	D72252BD8C30F8208C2015B43F55A3DBC91FC2AB69967628971D9DF6E70FE8AAA7565DE2681736507EEB4BCE9A3D077C3D89FD316E4D9D248F315ADD622EDEC9325BFE5A2949244EF7AEDFECEA42F9666CB72AE59D932F18ADD49D7FB1CFEF5EBF69B5F3DE4DC94C6E5613947B47CDEDDF198C44ECE19AC1DB44824F121350965DE62DAD4ACF06F6F0B25AE2714F344503EFFFCE5A2FE4F70C539432AF379DC37D0F00EE21A7237E5FB97574CFBA9957F21F2D7E921ECE91C6009E0B7473F4F80C6B108E79171E02
	B65AFF1C60961BC50236E699944BB7B159DBEABEA2E59F3D8F7DE92FAA1DA2EB56F1145588F202CE4172BE5C2BD7F76339AE85424F7317E19771178D86601FE7ABD1C07DD3C03762C054FF392427FFEDA07AEB51A0116FF00AFD63154F66B32FD8495A2EADD0FF9D6750600190E6EB1E07E8DF989A28DD17BDCE7148C06D3A6C288D57E53190D7FAAA4E532BC5B22687C0B88CA886A891A8932868B46C838563B6E10CF550DE9BACCFEC86EB0347848657A3C6A297669CF2F83E1158104EB9389DAE9A562EF14BCF
	73ACFB0DA64C5245F8257B3BA6EC5624E37EFB2D4147B8445CC95251B94F07CE751CA7G720AGCAGCA85E2E3554BFDB8D6E51D06E35248576AB1E941EBA3F905BAFDA2214D3FE27A557DCA55A7DA9BD7B37F7C39030C2BC19097899487148AA4CC829F82D5CFC29F55183E9C4BC7346EB55AF783D68B73105D5A39A337E9574878FC6176CA27754511C91E69180DFFAD6A59588D3CC1135046334D444644369113F05D7179317169790DD7BA2D1E299ABD198E131C28C396576149F16A305CAB5D96ED3DB8DDAF
	5D454747A76F9A2FF45AB2571C2ED7668D34EE13B653F6BA1639DB981F8E83DE6934BA04686A303597F50802F8366770B61D9F26C338608C44A40A15C23812E3CBFB885682E16B99287F23B63286F2737053B49C4A437D36DFFA7DCEE4F95AA278971731D275B76B93760A3E572F75227E7A35C954DF3F36096B998EF928F73BB05EA40E3E3E350AFA6B5B37295E373E799A4D5B6592DD34132AB1C886B96EB1697F841DF56EFEC30FA817798D617743EBE41EBD12FD77A7827671CBB711FEC5270F2FB740EC1E75
	BD3FED5776BD249EACGCABA837B9E23936BDF4699389659A51171346C4C26426DC075E7E25DD9592F37D7EBBE1379FFD8FE861F295487564F9F25735B88406300D20016G9587E17F1E57B166983C47BD4A981C55613BAEAC6F502F8B391DE49D653277BA0D22565F851D9BF81CB2126C15449E629A46331E26178C4ECE512B3EEC2069D91C2F747F6874D527185FE70D287D3E1577973709682F0AA0FEE651C97CE506F0GD0E8907A6BE2B371D7F410E73F93AF07D360192909E3329D4754323771F6BD59C972
	93A6FB26A9114365702C1A2C175F46636A7CAAB97E878F7C5FDC4E5BFDFA752CDE4E5B2411214819AD28737B146777D92FDEFEEEB91FA01F79D5DAC97BB4099D06DEABF4993014BC6010372D097DA6935B9F3022BF42A7637C792AFEB29F6AF09AFA40258D8E51A8C41FC5F84F34E2FBAC05F085D0BD1083A878ACDCBB2C3511BCA40BE4A5FDC076D9642C4FC62A2D2C530ECE3297A36FA8C06512E81FE593F13ED1E65736F932BFDCB585E4CD41B67FDCBB6AEC1E02F568C193BDFF00F0ACD052942CC3E62756F6
	99D394FB65CC71369D1B2B4F25FDC8F1B01B2B2F66780D94AFD761EC8EDFCBF14A87F19141B8467D351F6EDBCCEDAA5CB09531637DB896EE6458CC0A55C258BC957B1581336FFEA1402C5FAF1CE6569FA3024C230F91A1E6DF6D195971AED1EB4761290A6D02277A32E3391958A5E2AA3357AA0EEF23F82C8A174F05BAF14D1788F1953FC67BDCCF6718A1900E62587B96FA368161840E1DE1C15BA6F36CD20AE5C33878EC44A21C185FC60E2DF2E1FE959C6BF0E1FED59CBB4D05799D61D8A51D0BC9908E645834
	BECC9B4C317D3D48974331ABFB11AF6EECECC79DF46D299A9A37B4DBECA6FBBFB499F2E2A53936546CED28BF50306727837BFF5ADD87FF3035CDF83F7ACCFB7B5A13F577F889251F4DFC16ADBF03D61C1B4B5A57D534CFA0E9AAC220BE06906C4B9E751A1B68F8F0A51B9733F83BC7BC29CBE45FC2091F6591CF7B39FCF57CBD9E71C4674F65742CDC373F0FEB361C5B23856BB501BE4F6A23C3D8F91265E7591D676636CD2FFF0565F3D72B5ABE4567D973F2FD64D3B40D255FB0CDD97BB17B3458317F291B067D
	4FFBCEFA668A61B02018E9587F3C582EEDB70953143612B64DD73BB9A0127C3226E95B4DA10A971C23EDB78F1B305D14C2DC40B93C2F2175BC9D42599CAB16303E15CEC7BD97507E2B9A42ADC08E0EA7D01C6C99137DE532874E707D12F60ECC521127E33A7EFE49E8D4BDDDEFCD5269A5E315AA7EC4E52C54893FA1FC52CF569792F424EC2CB29DA535E452F4E227F37AB70C115F9B425CFE3287ED36BBAD03B018992CBB2D16CE9B59EF16475724FEECCBC953517EFF9251A7C2A8E2A7F772B1A194ED3C0576F9
	3590BE42314FA9D6A71F9F48E7FC1DE8A742F722F6F9D51661AB0E49381F4BD8B50E7C756C6FED4BFB4DE45EA1596D56C60B2391DCA24F9F4248B0A573D4D9BC7DF503CB7157AC4B587EDAE8217B7FF627586934775ACCB88FFA083609E13043A4A016F7C9AF491B7D7DD91FA2E744B51A9C165F15F40C08043CE2C231CFFF2343D377C37DCA1ED1FD23654978C75C8A7F6FFAC763E76B16FF8D909D12C17E12D01CD3DCED7F901ECD06AEF9153247E4116CCE3ACC81305EE9079922B440D0FACAA67B3F24E7FCF7
	EC962E46FEBBE67E25C26CE9B5BE775A0A4FE5F530C2FE46A61E1B2B14A5E410147788448D8305834582A582251F0BE5FB51CEF4A42BADD0FF27A4990CE6FA72030B30A60B582F40E4E24B723EF608E84A7228F63C6F4BBE1043BE980F8F34F74102285150495B46E13B56B65967AA6D2968BCDFFD7CF4BAE6141D271DE34E2178767334F34CCDCE1CE356C3DC4CF958F72C27E7E39B4EEFAA5CFCBEE241A66CCF6A4F47FE62875A6F8CC3B818E3C7A8C64EBB489EBA439EB379337FC172D107034F772F6FE1B284
	519F995ABE435F3E07353717C4F2FEEEB58C58FBA528DFE144AE11FC4FB88274DDG148E14FDBEABBFEBDB7F14466EC71EBCC07A11FAFE4600783B9F93FCBFBF7BC03CF6AD29773B1573BC39FDA49CD64FEB4ABEBC20BB2F49BEBC185792A66FD304A973114A464AB5AC7F477A49BDCA75FC31017C2FCC97F76F595F3867FB816DD05201E50C8EE3F6C800E7EA98BB7BE2F6B837F3ECBB347E151437011F4DA1DE7CA161795E634C8C7B31EB0F89BC7DE74E57E53D69F94CEE3B556E5C6294CD487B3620702EF73E
	614D5BCE7AA856F6BF9EA0B2799A06F5DFF25C2783E4FE494F5768A2388198ED1D2C8FF8D2A071D944B6B31A8AAB01EA015AE6E05FB2FF60DD0176ADBA7D4A9ED3270806C6F9B386FD2F5DA286473EFFF679A031EB3B9D6C56C372C77C6AC152FE405C94E4D86CA029CBCECB27D9E22CF817EC1034ED2B58C1E1F99F164EDD5DD02E209958D7BCF3947B0A689958874C20FDCDB204B300F2B8FE89450BAFE067G1597A07E31D35D1E89BFF9E6484F6499631B17F87699359EB2F08E08FD42E8738B7F7B0CF223F7
	1F7115915BC18B145781948A94A55BA0658242F33129EC143C63AFE065E7B25EFB07581B6DF4FE32D56ECF3ED2E15BD8FF1F64443B45E5B3E1DEBE9377E736ED75B89FA30DE1470E2F2D767EBAD9EDBC9FF0613D61601938CEB96CA2F9A6C0B88DA8FBA61E0939BF40FEA86A2259C252237A77B4529AC7720A43D9D9AA4315FEA80A5F8FBEA90F584A81F1C1C051C089C029C0D9615816368F178B3E67A02C9034BDB98D961B44ECD1550136A818856B10D93897945801E5F44F42B25EC5F923A11CG14B68B65BE
	A778EE13B94258EDF2D5AE3DCF82ED29F140A16F032E4DA575455E516192F95BFFAD3718E3FBDD75F6170574B64C4FE75A3D6B57EC3E67158D3A964FEEAA5C8ED4B71B6DF71BE6931E826758FD718F744E2ADC07E46C7577D4752A02E1B91DAA6C93366F166AF516CAE4C7016CB8A046334A2D4713827119C04CCF1DC674D37E1C26422AB9D8E76F7AC9274EFE633418B4F576CAA32E1724B9684F5F53BC23A09C8F14BA87675371A612F783A4B78BB85F6C97319E17DEB6C7A8FD27BAEF3DCEBDA614B5C72E3F91
	CABDD67A1A45DE7B067E75D16963CC7736CB1D2EB1E26C7E124D95975176EC0EE0F345DC0EBFC0F1C905B37F98DD681FG08CB0F403B866F64935F56DE88758DE898A884A886A869C2ECBF9139954298679C0DF6C79DB4B02BEA0B1125BDA34FF75B1B1AB7F65BBBAFCFFFBC8C4F739E8FAF4C539F8FAF4BD30F074C6E3BBA46672F7AF126BB5DA3DD7A057E7AF9B75D974D3ED06B67BA0A67DF2475733D3C3FAC06B869A2EC1F818E44D3GAB3E985B671F36909BE95A67AEBB39EFA937D056BE939C7CDD140B31
	FD7E67A849B3G4221C05197631852552DEE0B24FD795A33CB381859A173E24F3648E41638F03FEDF3A44A6C6FA7B2CD909E868A09447160CBB7BF0F663CB1743D07F48867G15DC02F3169E6378FCB8B44EF4B1A71C0E7978BB3A373779922D6F8FD1FC7BA5DA5F7F117741B59097F9893671552DE570AC3A946678C0F5C0A6A0B7D0B0D0845002CB312DFF31F234B11AD58CE50C167740291FD2AEE5F50A1579BE3ABF437ECED37DEDF1CD54BDFC455EA7CF288CE62772FC5F7D5A47797AE7B47F4E57BF2379A2
	FF74F37A5D793E6F216F4BD75FC3E77E886FC0FF60BE42FAE1EF2762036FD5FFBF58B1717E487A9F48B84DB13E3699B24EF4F70C1AEE49253E76FEFEC37B4C7C4B34ED7ABA0A97DF26ED53B5C731CD17C2DC40E558266DEDE45CCE07309055D43891288A289E489C05ED796F074899382F367C3D5DCECFE049E1AC34E5F533164B789717360C43D12A333FA8DFE5FC02765191D15AB30CE5940F0D521EE10C747333BF082B389C7B69DDEE2D7C104B951911177B12DFCE5FB1DBF0395759A34513AE77BAFB9C64F7
	F3A12E768A145FEC92383F0F287EFE5CB87175B94F2F3D3871922B9F2F377A6E437E62473BF26F366A4F4D4ADB3D67E69F356A4F4D36352A67E672BAD14E5749AF95B17CBF2DE49E4156D07FB5588C1D344FE3EB02EB69FD9983BF05413EB01D4EDB2240278B38DFE30FAA3EDC287AFB5F5109772BF1C2E5486FB43479766B7136317DBA29CD5F2FE735F97B756CB6FD3F1E5B663D87D56142BD2824ABF08F6AF1FED61E457D738CBDBBAB3B32293072CA44F258BD8988DBB9F6CA3F3F36A4F911E765157E6FC7B1
	99E1FE4B40FD2893B9A7E3B6B8C8672E4120DB4415E8974BA05AA50117F74B904132A0DC944D4F85A9369142359C0B73F81FB26B844A2CA6A24337DD6E7710B15EBDB8DF36E2EDFA39031C1B75F59A9C6CECEDBD5D2F246FEC01EEEC8F65A0BF139B0EE6FBF4A1700CA47197609E1D50DFA673446914A9B9DA3DC7B7728FE5AFE52B0CBDAEA958B6793E52962FBD91A2AF83724A0946FD138B4DFABCA5D741BC7CAA5C4BF0383D7705FE45732E3E0A1529851E562B583EABABD340E019605B2E01D7294B3468BBB2
	3E6DEBB0BA6DD6ABA9053C77D8B6204165BD216EA1355E21325CF8F826DC05E5EB6A5363D9824F7C5FE0594A0F12F1165C0FE3636CC9FC8F2294F89AFF03EB37DBDDF8D7BF68B738F67B340F3EBF8E61D420AC4EDB5E0F6B3C222BD14E11FE925EEFF7527DA04657415703B5D7E31EAF53F1FE9842A1C0D1D7E31E3FB7A3EFBC677D08FE5FA18742A5B1CD051BE3F09DFD251B2F31G9B0E41BC1E6772E2E2B00F39543E19A46DB5CD052557203EB386094FF70B647D8C3C975B39071CF8685C0BD53C434BB6E788
	E5D5DE03328E5077552C908E848A3B8675BA0B5F950F613C1F5379D6A604736702DEF3315F781557FC4F3FBEF25AB8535D69112EF22EF75B2CD5E1EDF371BD1BCBE95DF04C554E212F267870DC6D9C3A2A8FFDB68962B2F9F9BF723897DCFFAD2C0B2F9D3D2F146F5AFB4DCB5D2AF4C1BAF9B07D1CF47E99FEAD53EF8D470F33792A8AE7FA67D31C04D3A02EAA9675FE442C159F9E2B480C0975A5FF962D538931DA79D7D0BCBDD6ABFFDBB74A976601ED66217CEB3C4A6F1F3FEF9AE73A9C0FF48973C677D16ABC
	2CA77F226DFD49BCEDBD59C6711CF95AFA32E0001FB54C077EF6BE16B7F2C8ADBFE93E277C74793E6CFD93ED135973356F035DC171026B346F037DB00C6F03ED0038616BD07E599E770A532E731417F51DAF791753F6DE30C0AB7FDA0AEFD820157FD5BF4AAF07382085B897F914764FCBA0AC7C96310F683D199288EF64D8AF4536C3386E3728FBB43DBF437E1E277AFBD75F444F35DBA7D486ABF38D9D0F0EC0F96765A33617E2EE403E64FCA73BF16E9B426D154867F8315CDE6EA1F206148A61A5C07957B795
	EE389E63CE25F11BA1DC8FA48105723835747B9604FF3C65A17208DCB536C14E0F49F80F4A984F9DBC24D50277DCF10D07BDDFB0231D96DC0FF6DA5E05F6CA63653D381B58A27F86308F50E6206A9BB0AE0646B5C2588D9486944763BE6F46BA11AF2773256B06D1622AFD44497BEBDC464AB16CB55ADAE10D8F1C59648DFA47AB9D74483A816D54CA3102B8985363303C950382576BF3555FB78ECCFC7BFBF0C2E530B25FC067C295D05E1AB87E9E5AB05617C6EE07DB07697BE5BCFC8F8D0772F06B704F551564
	BD3ABD241FADE334F516FC671D4E77E2C117C4EE0F557C3D03D4DE5E2B697DD7E1A106FFCB439BF9F80F5F6B5E5108646F5B9EBF7EA2B2FC59C4BE73F611F2B96D7DAE3606FCDD42F3661A05B89F2F11880F192FB38FC9B8E7EE64BCC1F48DE834DBFBFBECAE792E679C4EE7DA0867E9068F4898AA5FB7EA3618A4B343CB5D6B943CD6A47BA5822E77AE636BBDE9A1DB6F8541B39C086CF730BBD9FF9C9C7B9E40FB96E51DDAA9E32766AB583FE5ECFE3702FDAAE35F96286E86D432B55E3CA16D7A1568938BFAA5
	AE44F5E0C00B9ECFBA44E7F31EC42B9ECF710DB0F73891F96E73FA670F70D4C1FC7D0D3856FF175F73B55F486C94G4F109B591E9033536B5DE35BE97208D27E4F707ECF0302ED47BB435FAA5867B2B62FDFE527FFB35DB33A353A93FD22C02FB8DE3ED7ADFABCA9901F49F9DE5465493F8946381B10E71FD10F278262EBEE423A7A2F814FBADFBD00F53E0D73306FCDF956675387700C574A79968F1135AA5B0B057166A64277A47DFE5770CDE8734A6131EDBE7B90396F57E03776094E9A51E5A4573CF7BAC583
	4C4369379D72C7546F011447C35D08865F47239E8B7B3C75C869235FA60BC7BD7E6E473D7D9F6B493BF16CD39A7F25DFE59495D5E4DD1E6A576AC276569238AE1BA47A0E8C4F73A88DEF656179BC4C66557770BD03D4482364E6DC1B7FEDG77BD6AEE463579DF685AA49042E1C0B11C77993E66CF643C0B69F7D972FF8763BDD0656F10B736053F2772BB641DFE08668B61B020984E1B5F4373653CFF245F25483F8572852A3C85F9DFB372FCEFC15E4768D9F4A0044300E2EE41FD12FD96ACF3A24192B00FDFDB
	B00F2A844C2316FE73D302F090D08467AD636FA8AE603CABFA687BB1A4FCAB5411DB1177EBA55FDB1177FD3AFE15A09C849441F937F671FCB96FFAAB4D1704EF03FCEFC35E13F9192BEE63FBAD74DD9C8942C1C09137E13DE89E702C9761B47C898F2F9CBE84CFF6067E3541A8591D83DB68978345C6725E240945146BB16C110CE64E30E9F3593B2CA51511266434B848BD33F7B761DD28AFC5753BCE95C3F5F9AB857D7B978BEEE363F362ED4A3BE0EC3C3AE34463DE08329F45DB8B334F97A338A71B436D70D4
	DF53AFD7CE5AD24B6426CA4A48643ECF57D3E4CC2D3C9D67AC770FF3FF6131F126AB9BD0278B3D5DF32E932589475D0E7B9213E99BCF3EDD3BAFF18E4553EE576ECB3CB308EDA993626A93313C4B0634FA47A4AAB29392FD6D8B6C235FA2CACD541E0F7EC071A5095A7351D47E7D277CBBE09C3A034F99DB3C5EF13BC3750E5B9D3E6497365236F607D67E1B94AF38D3ABFF46A17E0E9B44855C097257F83D1B12FE27A2B37BCEDF721B6DA43F223B34725BA9DEF617D6BE7BFE8F5DEF0538503BD0FE09573D1E1C
	3B941945C93E6437527EE3E312D6FE874537A7E965BF495F712B033868A414DFEF99DF3D7DF11C6926FB24ABD9B47ADEC379A22C6F8BE87FDE35C8DB5FEF26F86DA2EDFD5F5F05753D9162E297E1F95F37EA75AE3DDB512162EEDF76F6D17F555C2D3577B10A375C2D3577AA6EEFAB44A55D0D723F7532DB65BD0A4C5AFBFC49BF170EC3ED77E865C7D05CF10FD67E075DA8BF9062B26EC1791FF5E96557A5ABB20FA47B12FFAA9D5B24E42D7CE00ACFCA564AFF4B0A7243A0CE38171F8FF855F7533D0ACC773DBE
	77C1E93D8E3AD7AB7FCE0A075F2B157F030872E3A0EE43E23C73F26FE1BA6FDC0C77DB4A8F13F7B3FC5DEFA15FD4E2EFFAE07D2D767896D2B0649331D8FE678C1E09C0E90B755EC77D171B2CD3336438027BE0DEF41FFF7BEC959A3E9A8837E8B036C67FECG77D82D77613AFD08E281908E6658CD8338479AF19F5F9FD66D6B04297EFEFF423E2B2B5866BF93AA0315F95DB06E8946F2BBC40C70BD8DEE077AE38493D2E00E1D4277F5A8D68161FA0EBDB2F2A2BA133CDE1573755FAE4BCFC846093E4B42EC105C
	02F6F12420DD44C3E81790DE5E75740ECDAC0493B9D6CA318C88975C0F581CC3BF571731BAB6CACCD16FA1AF7859B274EC0476E3B6A86BC13B145F0FF6394A06F6296365BDCC5F09F2C0B810E3E60AC5C0B81EE3CF6BFE970A24B9D15D9DBAE9180C823B1E0C713CF32DE79FE6038F69DAA69DF44866F639063FAFDA7AG1657C05F8D2D06F0A3472C9413A09C423125E35ED1A3F90C25EF238F9EA623E84C77C57DE9B33E6C48ECF005857B5E188750AE4FDA301FCD60653D31CB60FA5D2E7A3B31EB627B591E89
	15414ADCE545F6134A6DF0C78F568F61C13443GEDCB9BA13C1DE30194EB01301BE305345D903EDFDADFC56EC6AA63673E83682B4EB09B34DB50AE218F72EF5871B32985DC1719741B7BE90407E317D22CA4352930B29531CF6879D0568458A50D4B5DC7E50C771B8D2351CAEE177DDDE81746D4344BC35DE897B7AF6F1934DE05C1B81AE367D0AC9142199CFB5BBADA9DA76949B3BAF5FCF6E3B2BE9DD546F86E70F937238BF8BFDB74FBFEA6A522DDAAFE4F4F24A85688E1B34712A9968861880E356A7601A40D
	3FFAB50E42AB1FCD694AB891BB0C568659FC7D7E43E48F57A54F4919DD1A69BCAE9E74CB66361AB902E7FA999CFB18E25990AE7E83E277736F59EC64580B741BCA3590EE63D89D5DABF2C0B810E3535CA8A314E377D1AC9242719CFBF7G75CB61D847G4A4D6458D4FA16DF7C9068824476530F70FD776D8F615E582F87BC6FB4C672BD60DA1EAE136F4337BC04E79F1F72BDF673C36C3D7DGF806G117BGEC7F7B3A1131773FAF5CC24FB46A8C6D4A3B22F196320F7603CBF4124D3D2FAD2215DCF6E7FB6113
	0F29774223E4F9C98F61B7194D4D6A78F4B93EA88D75FE091F8F1525B13D374333AE0D5DE1E0FA8F9F9ADB6FF77ECF519B6FE5A666117886CB4F5E9A1562CC2F7D2DDA3D09BCD39A6AFDFD37D6EF926F66FA6F6DC11D02E43DA360991B466EDDB03D93ECFEFCA761ADC56FEDB2D6380F485EED6FF174CAE22D5DA5F16D19EE6759353A9319C9DC77DB8DDA5DC9FCFA9A5631DDBA6F329F6467C659F2F90A9F063A7AB06B97D8F992CDE317A77B28D29EBCBBBBF39DF9FF29C1F47609CE8FD7483E38C05416075CDB
	29FA984BB339D3DB9E92DF7FB036077BAC580ECC9C7BDB97369169E17419484F99839FE6E58C03E7B490398B424A185EB3F699BB3FD54A0867280B3610EFD0B4502FDA54101DECDECAD60639B6ED99094C84DEC6412CADA309CF653A1F494F7E16483A97BC82F372C7589D96267BDABF4EB693F2955D713CB7EA93BDFB138CCE0D6A326E295DDA5D094C4AC7D077CCAF5DC9FC6DA328FBBF7FED3636C7186E92BCA7BD426E5EB05D1779217B3A8F955D71DC7ACCA3394B3347E672501C6915E0556ACD6405F33D2B
	5A35FA1378982E77AD3CCDA448FA2741B36B91F51B28944756BB24D2519B4F0EF3F249BB980D86D7373AC5B03DDE69506ACD6EA7957D91753E522BFD137832BFE27B360DF81E6F860EE07B28849EF6B61AC0F9EAF81A6C917ACE0741586D797D2BE23ABFD28FFCEDC06CFB1FCB78F7F006818BA6B8D09250921E5F7ABE76CD840BC4DE9DE1F91577219E456948E724E32A55E2931B1DB8668D3A316DEECCC7FD2FF793FDABF93A2768FD09FEC853A08DD845E67AF3F1324E2B7978D81D0E7D4137FC2C2DCF47F20C
	8CE0B90EGA68105G45GA5F3B911035E6538E190734A62FC7D1A333D9BE8F89B8F2F9C4433BE36077963B03FCB7AE8D3E17523F81EF851B06A6EFE944F931F213C5190CEGCAFB946F12AC6D70712DE4DA53D8FE7147709CAC1B27FB3F3CD4BF9D2F08AC6D4CE61CFB347D894FED5FBF08FA07G1678A7543B67A06A1D7F986A7D9B4ADB8561C6A06BE348FBEAAB6A944831673AD1D646E3A86BE88F4AAA4BE8AAEC49C05EF9BDA8ABB8035F5D273C8990CE834A663C0B6CA82B78F14404F614A5BD0E32CE626F
	C6C781164EF9EFB5222C52BF232C92FA5E5288E1ABD0601F1177DE91E505F1AC3E93E589CF703AEB712CABDB3BBC6B46E4AB6A329D52B81E6079DBD11748A7D0173F52F227C1B89B28B893F91F3421AE659C7B4C4C5F234844F2DF4D4F6163814B613C4BADA82B72C914D5CD4FDD2C908E848AFB92F95F30222C980E3DE7C1D99B1EC2D9B37939CDBDE0131EC25E4778D89D77944A5ACC4FB832A1DC7CF4D3E17953483B024B2A66180B7BB362E91475B477E7AAE0A5CB388DCCA82BEE894A9AE6F794A09C869443
	F93F6CC0D9099C7B1381E5D5BC037EBCFF44531FFF98A16EE65F29BA76134071C57477C9B45F296AB7DBA411657DE6AF76AB66E7707B2AB59E3F4932D226AD82FE9F354FA39E3FE92244C7FA442F71CA7F00C77CAAD5FA2673D7FD846BB0D8DD4A375B27523ED8F2760AAC1C6CD64A7877814F38F65EAF4A6F268C60FD09F0DE46878734E5DC6D21633AC1ED99BD4B70556068E5BCBAB8FA99978F2A4B48563BA62F5FA8990BD8BA67B853DD6611AE6599FF3F7FA15233774CE734E727B60AE7BF23BDBBFD1E0FBB
	794F027D1F65FB8D47745677094FB2BD521E5557A7CB87AFD88A73D2200A25CA9C139151BC1AEDEA003FC515463A94F57B706078EC7A79B853B9BD5285AC7557979F50FB88A1CB353E78186261CB353E0865E3E9944495E7E1F94FE855730514A54FBB33747589534123810B87CA49527A6245D1FF9BB79378056714B4A54F715F59B8616FFD4A6FA60CB35D759E694A1F735797CEFAC6DF751C5697C3942FFDCE6B0BFC9B3FD3GF1313C3C0BF54FDDCA17495FA4DD262FCF0D8E5E821895A8E01956972B3C3E85
	2D2690600FD425D9304C77F93BFFB61DB64EF4F7FA24CB72D1F6EFDFCC2263EB7AB22DAF26D2FC49B22DAF7EEAC2DF64C05C1167313C8F686EC146BF4F74C8F9DEDF1FCC9DDC4806B981D0F936569737F60EE61BAA602FD725B1E573B3F62F3BA77E59F456B853BD60114E1D6D2FAF4E22734520EC2DAF26D3BCB4DB6B0BB2BE870F00380297303C4F6A1EF9DADFE0FA843C202FCF088E9E895882202497343E2872FA0FD1CD69401F2DCAD374A26AB6EF1C7717EE99E7BA41635ECD590B7E7AA2064E07ABDF547A
	6237942FFED16B0B2F797C398E6222F9F9B7681ED914ACE7FA14AF575727CA872F874C8464DE2E75C579283689827EF0D51A1865285B05DEF7127C33695563CC176911AE41C7593DFD91CE579B294B353E380462994B353E585A0D3E480238169CACEFFE0F1EAF9664B0BD12F27475C957413381ABFA2929306CA52DAF5C230E971540DF2BCA5376926A765CB87B7D152671252B72C867F849DFDFACE16B3117343EF8016241AFE9FDF1B29FAF42A0CEF819FF9FC8773BD22617199E6E17757589524143810B81CA
	F8D96B0BA78C2359A69578172852642FC05D7E267B5BBDE35BB45263CE718648EF730A516D4A4E1FEE247BCA55AB545F7C6A266BD479FCCF7577CBBF7BBD34314B7166895DFFBAD1E2E5FE026EFD35C0194DAB709C2E7EB83D278261A00EDDC177E842A13C00E30BDB708CAF09E34BA8168561A221D946D652BD32B288D7F36CBA0AB5C2584C31D5BC3FE10E3DCF31F08847F06C74035EFE5AFCC8714D3FD5FFDFFFF0627DF45F044AE0E50EEAC5BFA5F0BB2CEBC3BFA5F34C5602FE4A60588E7E3BBEB99CBBE4C1
	BF1565A2F6CA37371F56AB3FBD4879BE31226FDAB856E7C5BFD9B9F6EF0F371F5CB645B7A7297EDE7B0B7FEE20371F5E1BD0992C4C59F674D3G3743C17A7D2CA088C7F06CA73AC7968B61E40E0D34232DB3B8B603E2C5F94D059B7390BB4B606D27D21EF6BB672BEAC7BF35F16C24F6741303E30F527D5BC088ABFEBACB752D36692A3F4BA6741BEA721E44044AE0E54EE1F7714EEEAA8C63F6B0503D4CC88847F16CF90AA5C3F88947048E34F57ECA444AA8D68E61EA0E1553BE33914292475278EF55CC6218C0
	771C23A09C4731FD94CB06F086471E31FA3727109E45B7B3D4FFFF20BB477AE57D7455044AE0E5AE616DA9075B21570E7DDEC1BEE211FCFCAA65581DE6FEDF08E3CF19315FEB64584D1D5E6D290127B5F3BE17995BD39047E24D581E42B976D617371F662B6EBA5E247A3B6EFFF06FD17C9F5CFB5CE1C5BF45F0BB1CEFC5BF45F16CD0977AA919E33773B6114131A7BA79BCE295E2D7193D7D74959F5B4AB85FCF9674D38D47A2AC6827960E2D6C767653C55D0AEF2ED47D3D3DFB627D745D044AE0E57E20877DE4
	65F678D58F7A493D4A532E44CFC19CFB5B08368E675817C674D3BC47F25B3D7DB4050FCFA91C6FCABEBE65F0AC0B0FCF452F7036E8729E1F16294624952A3FA77D8F4627D97F03714954016353C6EE071B7978D4413149A69C1FEAB866BE0236EE61D868919C1F06B9A6993C4727BC6E27E04E77EEBB0ECFB19C6BEC4771A901E3B750F3AB729D1F1D39D80EF80A116F78BCA360D9527D435A6F0CB2BC6F3867EFFCBDFAC460F11D47497B31BD86372527372741B2A84A77980E5335CAAA68108144F8EF263F4F6B
	F00A9D2253A91AA8B75EFB78836DD7CAD6B7971681317BA9CF3678F12F66A015EF31E964D75035C89D6455329A657F35F9745F352B24EB9FE203C01E666EE66FBB940FB763D9E7B45735548FDD2BDAC8BD757CAEC183DD97105F670A073C18FD8B5A85EE17CF69DAA29962520098B61D26A9AAE8AE5CD000723F9B75F762D83AD3E85FD183E96AF93AF26B58698E715F4E9CAEC09DE6DAD16F6082547BC323D2AF9C8E7DFAB1094E3943A1CDD4815A384F603BDECC21EDA185F853394EA16DE35BBA26DD3FDEA452
	76D13C263930F48D6637ED547CD8BA319DEDD03B86EDF0E9BB5A20ED8D16A54728EDC34CB67BCD0AED5C9D7A36F9135A548AF939F9FE2F0E52E67EC95BCC8470C673323C55B136EDBE6B50374D815A0F25C2DE99DC7ED71D233719031D58E6CA56228E4F7B719BDBEF193D5B957BEDC83AE73696659B4C5EED46EDC63BB72E453AF7D4D45A4C85F49EEA02B847DA7455A22AE79804A3391E0F1A47767BDB344DA4C31AB41E2E4B0FF4E13CACC505284343945B84D4D608FA07F4A8FA47D37FD7C0DC95CFF38A1D17
	19A1AC95227CC7C635AFCBD742663F10A60A27EB72230F18446F2326F09D6E30205E19DC6FEBBB953D2369BCA587628A0AB00D19262904F0F5914A2FB40EAD3F034EDB2C10464D53DD64C77BCC616DB3026B4066C5C46F58A2547B81332277D3343EA7C0DCB2CFB3170E5B423A664222F5A8FF4A113165AF246378F6C8D343537D458F3D3F65FABB56218E413CDF89DC07FAEF22EB9356A666F47E9D1EB6313F0C5F472931C01BF4D96C36724FAAB617FDD239897B1D3AEEA55D6DAD7E2733DB15F459CD7E24133F
	3554DCA60C7A1BFD5F6F7D263611718AAD5E77F33ADA72607949F65463029EC58F6389683F5026247B5862FF3AF4556F3A3B3B7DAF77D12BF7D966DABD4B327A2812F72341FF1DE63B14F44EF67F535DACA969CE71CB9E639DEE77AE4B42F64F32E474AAF9E7F6782FD3E13F126EB9517FF4DBD477345E762B7E4B7310CE7F6B639E33F7392718BD4B7D5D0022470C93E807B103CA3A1B4C7E273BEFC8C9B77F844AFD35CED932E9D936D6E05EAF8EABF99F6872DF2737D53FF599F782B628BE2E243B48AF9BB05E
	19966F3224DBBC4BC2271EF2DC6589347753F215F42F9B7DCFF7C91E12EE5989342D27F55AD6255133ACAB8FAAF91FEC72DF278F8EA969FA0E781FEEEF2B124EF282E5E957A94B453C1FF80B064FED598591D1CF8A2BA4E1FD83CCB82D74E7CA4B58370FA4E1957D79D8C9D877355DD93653841352329FC8B81F7C7C2AB73C66FB71A376228A3010DF2415048272033596FB2F4B6594DE753C201AFB61DB2794CF59561BA794FCAD3C820ABA4C063D422ABE72A32AF5C2E1BB7DE9877263B0D6C9A82EA3E9BF0634
	9F5334DF5489F9C6D070EF8AEEA4B80BEDF0980CA2302CB1991C5DB4072342B1E1CD2B3026435959CE85C8C2DE6B572DA01606C8F4E11D30D6B2539F25211A3F25E4DDCFFE1AC60E78426DA07ED831826684741F0BA426539200D6F8E1700FE112301A3C68D63637C692DE81DB53BF0A64F7314A484BD8E0C076AA930DAAE416ACA32F1FC842DA7A920D9CA858E3B349FF2EA6AFFAGBF1877F56FBA31732C7B6F0B7A2926A4CF485BAB3CE2269FFF849377132FC352F210294E57D45B8D7FBFB47E90FE7FEC53F1
	75774F32CFB566611DE06D7CB2F8FDB3BDDB0F101F6477675837E6E653B34BB440F300587B2B4F9E7AC8707D9B618DBD862B95D6FF5DBBEDA672FB613B86A4D1798D2F0BDBC97EAE6411A3D95C957437F13AD4E97E9FD0CB8788127E9BED54AFGG3C23GGD0CB818294G94G88G88G710171B4127E9BED54AFGG3C23GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8EB0GGGG
**end of data**/
}
}