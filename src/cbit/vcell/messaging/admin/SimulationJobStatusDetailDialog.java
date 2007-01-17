package cbit.vcell.messaging.admin;
import javax.swing.BorderFactory;

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
	D0CB838494G88G88G2FECB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFDDCD4D57AB8159595A935E4D8D6D4545206C6C5C54696952595959655D8D4146CE605C545366CAE3FEF17B7854522240DEFCB49363461C604C9C5061BA69A969689AAA5A583B34C8C8CB3434C657D6D7B1CF36EF36E1DBBF70611343FFEFCBE8FF74EF31EF31E37737EF22FD032A1ACBFBB27C490327385696F53ECC1488A9704127C1CCF9017783D2ED0D07C7D9360D561D4FD9EBCD3A0AE63CF3A
	42E2E1794785904E00F078733A42B76077FAE1454761E170438B1FF108BBF4650733FD73E968C0BE2BE9F8EEFF91BCDF8648849CF9B66CA57C977617CA78323D8C0F101DA388B5AD0466367EB2899788B4E1GE9GC28B23AF05A7936078ADF592DD7BE991C2466D4E1CF5A49FC51E8843D25A795233DC78EBF6C3AB6175BC151324C90238A2409165F30506555B60D95F5CEB7AEFD727C52C9E6CB2DA2D55FDC617CBEF3258CC55FAC31F4536E5CB0B25EF402A97ADF65B57764E96D1AF8E389A0C225EE2ED3068
	2DF6D39C653145D9C07384FDA05C340F604A8442B78FFE1781546EC37CF20A8F065F91GF17BD0572D6F55CB3A340DDDA354DC75715AD73C682A5B47F494764B3A1685A4677CAF754BACBD31FFBD44F5G08G21GB1GBA00FF5072F0CA47FF60393DD35CE16F6B335B36BB9CDFDA8D4EBDF603518A3F37EC019889375758E5F79A840179BC333FCAC39EDF40522DF29EEEBAE613084D687F483D8B0564AB67662C5630C9F244D8CE3AE40B028332CD986FAF3C722EBC60DEBE5DF99F90143C1774A76566EB70CE
	7DB9A937DE4A27D941BB60G71F5B0753583FEE7CB6503614F2278F8851E1555F7A81E0413A02E2E8D4BC65B4BA8CBD35E85C27126B3B56D90FAD3D04E7CB6A6C3F41BDA160359A46F14B646F39D1725076273FE1071CC16A729ACA4DC89F1A1G2CFC1D5AC56A2E70A314EB00DA00A6GABC0A0407C9F310CA5F5FC68E799EB56BB45AF87ACD60351A93034276A7F8ECF633068546F3639C43D2D4B38DF6F3468BB2DC617A0514C55939BF43A25BB1B167DAE60711551E6F452EA8B396B7B0C225169E2B4617AD7
	890D53C8E3DBADD063C5FD1FC340FCA3756B61E9553B44FD8E03DEB44A94ACBEDACFFCB2285F8FB9C286701B59E5110158AB9674CF82C865F678301364770D51E9B3DA37ECE9325BFB879C2DC4932176A03173FE5AF6E482FD71C1347155C6029B06F070C14C273B73B5C5BE8D16AEA23E5EB902F5EC2EC05A677003D8E61EE943B25330EE01D0F55F991AE5266DD0D2EE62C1D6D6D69CD4571DB01AA7110B05AB0536432C477EC2B867317D287148653E2AB091DF95376B8A2BG1A5A514667F71633B27B255EE5
	44D27A1D938A1851A0DB1C49294FAE156CC972A5ED8341935B2F9550A78ECAE3007C03G2281128172FE823EBF213E4F8A9DBE72B9D23AB3DEEB32BB3C70D9F784F9B1DB1E59C57AAF632051A6B6989DC61B81FE382438F33B88DF29FEF75BF91AF00AF7C0FB3083AA200B63FF57C56A2CDE941D164E815158E294C568226578ABA8AF57C027C3EECA105735DDC4D77B9034871C7E7AAED2173A6CB61B11BA15A7116263E8DDB6DA0DFDA0748E7B00CDE478DB8D24BE749AC7243664F643AA013485568152AE31F1
	C5141E5853613474C1D1A1CD1B9071932967FD4ED2050DDA81C746C763708C7E896B6C503F304ED60CDEA2449DF264E875B921974D49C97F0955553C1F65BA4BE43A4E28AD7B0D34CD1170AEA66B325E8D701CE608A355191F4CA45F8A60D787507CB356192D5DA49E5BA529326C30DB2D4C345C667FB195939F997B870C501C1386709BEE47CD2657D4F17BA51B32784FCC4426D6C55BD9F908587462EC9CC71AC11EG00301F19DDD3A43BB03A1E9C2493A19C8490F90851259DD252D567A2DDED87348B9DB21D
	35C3C977B8270B69E0710952F3059B5D7CBC242B506B8A6B815A750CEE3EDEC957110FF429004FD25015F5B2BA662F3D7D4456F44C5A239F548FD73B06209EEC59723564A9C1E0635187E81FC26A372193471D5B87087F66C3B89AA021934B5C52F5B86E8C690D90421D7766FA9BE327CA3294F449E5AE696501DCD6BEFEEBA1BC9BA12E2D8B4BC634C95977EDF7396CDD96DA50A43FF61915E512614C66B5706C36B8DD62EEE8B406318C584CC4DE52AFCBE8E91CF2B79D3F907BD801EFB8C03D01605B46CB9432
	A783BE8F7015G39B43FCFF2BEA2ED8AAD24556C51ECF4F65B1DFDC6C3A531EE35FEC82C266DD775F69A3D137C5E083ED87777AD248D315ADD46AF87C451EEBB34DD15C91377DCBE5F389F4719972C5FA6491830708221FE6A679C2DF1FF60AB661CD986E677C803EC7F62A712F3AAG18ADFE13033648841A9AA35A22ECC4ED8BB34485995196FF7D0536F8682D4FC87D956D0E991AC2726501DC14BF8CE453F179CF9A52123F389B67F21F2E5E8ACF2791FA00BE0B784BE4D81B07B2D4C07E6D5DE8A3A745DBEC
	96916A1AE554D83911712EEE7800686E3B8DBD34DB11CE66F58B679582FC923AD1DF6718D6594DB321ACD5793F4CDF7AAF093FEC50817FB2DB1D140F721702EC4DA614BFE8D4CB7EC0937AEB69A8616FF09A87E74A1F0DE712695CEE3EC9FB9E97254247C3184DF92E22ED214E241E175DC071ABCC6AF959D783B8AF4B0438F6B30E53BF69A5FDFA9C041381B2G0AACB03683E832E09B7822F933B04DBCE8378D26A7B63D3545EDG0373918B618BE38EA99CE5A1B624E38EEE077B3255F3DC3345DD77D92AB0CB
	375CA35D5B2AF004AA9D735FABADDE7AA166CE120E0EF9E20714E31EB848AB952020877AB5G56D74D99473E2A36877B2409DD4ABEA97615091C359AEDA281EB8F13AF28C7EE938DFF49107A2494AB69D793A0EE8500508BFC81368134838474220F3674EC1D4EC7346C355AF7G2E9146A1BB54E3C7EE53F9B9B37345EFBD5229FD91566B1E0E59F8EB2F160DE300B62197ED5CB2C2ECCCEC1B5A0B730E1FF2E7A6E72FC7BA351CE5D677F4CC0638A11421560AB23C1AB7B399B6F8242BD6178B2BD63ABBE648EF
	19C7BA354E33B4F53EE42CC02AB373394EF372E7A643B99E6954B2A4E94AF073A84A108671EC4D6185BABE2C685395B676A16EEB974135C3D864385BC4028B05F0E49F4AFF5DG1983457070ADB42C6361BD83C7FA7ECEF8395B227B087348D57CE6ED42973D5E672F3BFA35672FCD3D5A73573D3D2FB2BC6421DC6D42789FFBFD4FEFBBFB356637EFD139EF7B7BABB4EF174B68220DD403DE54CBF1CF082F8B9A735CEF75FDC6C96775C2D1CE21C433D3346F58A4E09B7F42C63A0E6C5946B76BE1B44F5A9E33D9
	5D7610F21085D0EC4336A7E3884BDF258D67A2770E107E34834243G41B6ACBBA163DAEB3591B6667FD86919E8134B83EB67CDB6B25EC8853CE05795168254GB45B31FD4F6C1D368F5EB96C238FFE4A6A3DACBCE755AE8BFF3612F9144BBE606CB22A7D7DBF56D29EA777A43BC5E39FF18D23D95DC7520BFA274968D1DEDE6EAB65F15E523F51672DCCB13FA73A797DA35EDE8C74223F9CF67417F11878AB8242F1GBABB7A2BED0478AB5D6E5E6E64B924FD9CF8D6B95C63986F10DCA56F4D3CDE5FB2C4725B26
	CA53AA054D709C574C2FB1CF19DFAD476764AB719FF3BE8B47344AD93014F7380A074C67D6377CBE6179AD99554AAFC64AA741EBFEEBDC24FE9A0C5D7A81ABB499B0157C4EA1ADDB937B75935B3F6BC2FF24B8F07CFC7990998FF5BB75FD6052960731CBC82734FA975647F688BBG028122G927BF16E10A212BCC40BE8A5EDC01D136C75998684AE53963A96A32DA8C06192E91BE5B362F8634581F51DA76B436310CF30936BFC1899E50EF0E299BA21076EBFC0B893205805E568AFC3EA5BD53AE4FB553BBCED
	47466ADF0D127C1ADDEC2C3E1A63DBA85E2C4033B17CB25A66D0BA08CBF5E19FF7A99DFFB60A3A42B691F1459358979AB82E066282A19CA1E23BE237F9EF9786ED5A6D4238CD3B0FB856663B0FB859662DBEB3BB0E762A6D98AB4A36CB943D59F1992D5B29A233D7BE4727D2FC26824F6C58BB0CF33E2281A8E383E89FBB9DE3A6C1B80D636E2738CC88978CA26E73D134ED89479D2438BA88B7F3DC729066E760382D43181F03634E9D467C46B96E7AE14CAF0C63EE21E331E88847F35C3F47B1EDA247BDBA0AF4
	999C37ED8C6904A12CC775AEE2EFC557D8FD40E2B35807204A109DAB51D55D303B25793B161D073E7B76500EA6784166B6E1C34ABD6D3AB547EA2E71922898E2BE2B131ED92F1F154D6A570934CDA0E99C9097B1C4F06BC5651C1B4878C3AE9B9733781DEE7124AC11F58BB9FE48AD1E36F3F94A78CB5C62094C1FC96919DEF73C0BF3362CBB22846BD5333C6E5527CB7A148C33E7F06A3C6C2CCF06F3D9BEF79FD45A2778ACEBCE3617FC1A2474ED43725C0F5927FA905B1F20E1ECFF7EC24BEDBC041381B20631
	7D39482C2EB7C5A3F2DDA99F71D6EF7ECA5B335A91F53D496FA5784691F53D896A41FA538EF1F1A33C2D21E535E2D4D7D8B70A3873C7303C350F72F1B5ED5782A09C8A904571BFD0FC16343ECC5640997E519175981924A34F28D16D7592DF90B02AB5A7497460112F08BFDC9E399A617542CD63E4FEA1C2C34A7A2A9F07C8A9D9B2CA6C745EC469B4792D973E1AA0EB50E63B53B28A1D195E3A5DEAB15948FA33543F6AA730AE950F217DCF6BC51FEC63388F7AF81FB046EDCCFD978861B00EFB0A62489E8359BF
	E0387EE1349321BBDC3B3CAC71709647F81C4CF9644FA0FFAD7BFB5A720491B26E906DF6EB2B45518AAE11468F271365F809E665083B3F2E9A117D35F7F4FAFFB9C7697A3F5DE9B4B96D83B6830E03CE266DF2AC58E189G4BFB4D98491B7D7EE0DC66F35530AFBEAC3FA7281FD6C0DE99E358263F5A6FAEFB73041C6756F1DFF9B2FA5724CCFFE04C97BD1B3714CF9199CA46E13EB20EE30ACB865E0767FEFD0FB4CB59E93008F6A71D26G3259E9079122B84230F417CC727FD32E191D3139F1B5365B510BFEA7
	441C5060F52D2DF91C15C107742CDCBEAFDB5665E401689B86F131GA9G19GC5933A420A8954ADFD0048C8E6DBA07EF6D154F719694E8FCE4276DB0CC3B21A8CECD95E8B08B852B8681341F73D5987F2789A7A636FBAFBE0C2542AB7713A31F9D0ED1B3A8939BEB5CDF8EB637B58BCF5C2BD469C2178618975987325E19CE386C3DC4684369D31F4BC5CB629AB34CE72F1D08F36A7411358CE88345D0905F0A2472D25B8325FC15650996E749EFF56BFC8BE4AF06224FFED8F63F129DFBC54ED063FED8F2BEF91
	3DE47F5C2A9F318F08D03E2608DDBE63F5B795644D8228GFC5D94531F552D1811695B11155F11F624196FB1A07E1D8F897EDB3E77416710EBC8395FA16F67C975E349CF4AF1CD797B5FE90EEBB2FF9A4D891D1256A9266471C87D639BF2D97E4393649C25F23C58C27E4B43459DBB3FED5D39D7C0BB5C4057F55327189D8A82F405E581EC6F0B592121EFFABB9CFC13685B42776690DF7CBE21596B36E7066D5882528CCAF4762157A43969FE4C8E3B556E2CF69A8DC83BFE15CCFB4150FACF5ACE52C6313A7B6E
	94614967B02C7912622A264878126F2F51C9F08B905ACC2C8DD83E0A4457027E4DGA2C0A0407CGECDB22265E91E85B22512E6CB4180CE8E864779B417B5C6DACE17A73E7978A442E1DF630DB9F499F7117507A836626C806DBC84B1253E2B20B0C944F128924EEDB0D5D94AD2D435231EB8C68159000ED45E2BE47C98F40B640C55B2012E3F40515G7547A0FE0A621B0FE17BGA247673B0D5588BDED4325E749B1B39B17383799EF3B7140B1A036893E46977E3799771ABD5B0C2778BAE4A86891852083C813
	ECD0FAAC9DFB74147B48BB6FD826BF63712F8D445EEC25F353C7D2FB720582371935777AE1BCDB5C8169C30F4575194D9F396D0F114A30E54BAE2BFD28C9929B778706715CF06231B8CFF90666D9F01C2E309C206EB85C939B7EB736C31197DFA0045A344FE92434CE64B49F47F4910F135B219AFEBEF8280058AA8A6292G52818AE6C13B82D0BB8B6BD2F5CE2560FD8C42142075492937C075213ABC510B36E804FC82E661DA5007D654B1E6966AB8EAA5346990AEB89E74BC9E79BE3D4AFB1DFCFE5574F572EF
	2B68F99228CB2DA38EE99DD4D8CD4A0B3D3B5BE564F5BFFFF5B1476DF6B55BDD96525AB0BF6B87BD4B5770A0162FBA103599E098A068F8365E9DFEBC21F944671813653D6F60EA398CC938577E25A8D7B58C17E5D260B631F537ABC634565A530037F0022E10514C1D5022A90578AAG662757AC682786400D1F00E5766EC39AE576AB274520AA33BFDAF03E94FD827AB310661986613C9321AC1D0863E9FB8F493B85121B859CEF8E99319C17DDFA21D036E1DB4E2B9A6518C06D092C7CF61CA817E3392DB18D4F
	6CAC7A408C53ADF4CB97F262746DA59BABF65276B862C4B6D64C6678DE0A0FD66019FFB2F81B9287F19501F8566075B5443701908E870885C88248GA8BE896B4FD92BEB04E976B9DA6D0EA628E0D64592A3CB9BD0603D6E1DD8B0FD5D8BAE506E8F6796F87607E797E877076797A87BC3E677159E6B1A7E796BEFB3CCD7EBD5262BB849DFBFBFC35B343A1354FE7EB345B71C2476F300957D5C8CF151A7E17D5CBF04785213212DB9996B679F2B090DD47573CBBBB9EFA955D0D6BF4F9D62F7D1CE467AD9C24BD4
	9C04F5G69A7E31F52532B2C0B24FEF9DB33ABB80559216A94773A48F8BEB5026BED56D310671EC942B39C4231GC927E0FFF057845F0F6634BB69DD030AA09847G348661182554B2B39F7EFB06698E9DF6BA6663AC5A7ED903543E4F27EBFE43C1EA5F4F66ED70AC08DB9104F57C060EF2F8B61D8AE3FC00A000F0009800C400D4002CD3312EEFAA7455C73302A17751529AB89DEF144EE6E50A69EC266BB352BA156277DF077D197F4D94980F759E6B07C792181D5EDE633DDDFBF70D769E4D7BEB3477E8B62D
	713DCF7F599A6F6750372DD11EC3E77E78460A7E40F504D7058C2B6C037F2778FD0275687B633CDF01C73B47FE1FFF69FAE6186EC21F69EAE6FBDB7B8924EDE643ECF51DBE0D621BE72B6B741B3C5FEE07383859D8275F6BA47DF6459CDD61B6G8340B8C0B0C0449C2C4B779FA2FB605E6A72DE3B1D6E40124DD8284B4AEAAD69786C08475E5F9C455E5F9CEFBA5EC85B6854B96ABD0C840A4F1C235E43E81964FBFF413AC2C7B0365377CE2879A7854BBCD784FB63FFADED773302557C63A93E78B4B57FEF84FE
	B6976282CFC37EB9BD827777DA456F904B51AF4F8B0EAA8F691EC90777B6ECE94774E33387BB3447E6A99D1EE333BFF4E80F4D1E6ED00E4D2475DCA9DFA7BFD4A41DF96DA06388B6075A233769CD344DE3F302CEFADEC64FF7E130ADDCC447ADE96053AC6E57594332AF4FD47CAE7F957A264F0EAA8FE6271ACE6FFE7D34F3FA3FEE6B54766B2ECECF3FFE53296D576FBBBD5720D60D629AD471693886F583BF93D1FBBA7AE771A441F5C0D86438FBA8AE9042119C7743243F36A4F91127F83A7F6BD10C0753EF9E
	388EF5B87BE44C86CFD19BA402EC295CAEFF1DC23B947C86753D012E6B55C2380963EE27B8830483B96EFB373B0E04FEA6E52949CBBA295FF463B153B5B8EF36E2F5FA6990D9B09EB4699D525E9A2D4BE4BDBD92E4E3EBA89F723D3F585F30B53AA4F82EA07147609A5D7D9365920D906229D3C908F20DEE621F72DA4AC7926EC9D146ED164EAB65CD2957C4883FCA482BBE8457CD4E33E95134C23CB98457B232A7BD5705CAA7B1DDC08853A9941E11A1EC5D1569842D158F3B463B693474FB523FFD5D526534DB
	2DC48BE96D31FCC4051756043284255CBA09EF5E99407B8C546D78B1AD1A9A08EFB883F57B1FA1524F1273F152BD1CD1BE16811A10B3F06EF65EA81E55CFB88367EEC593747E78DCDDE199C06DDC247D9FBE4FEB1A4B6FDCCD127037F6A7DD8FE2F475D3C8B7EBAE6619C46FF445C2B889A00D6779008DE9734EC45A77687B9D6AA15C8AE0BD9367515BA6799C8BF031E7E29E011318C7864F632BA9C2DB958AE9815AC3D15EEA0178FC07115C4F40F331261D64EC2D4639D859BBDC375AEC64A506A22F942ADBA4
	04638112C3D12E5F733362991C56BCCE651A87F281344F63E777B54F9BCE5FC6764EB0DD38DBBAF11EE75D8CD460664F43FBB68134ECC44DD30F21CF227858F96AB174EB936833A590D7F5966A1BBB29649F8C787033FC371552DD72A9353EB10AF4899AF9B0799A69BD3A143318FC059C3F0F623394F8A677035427A4DCFAB60C6F4FC63913EDEA7EA9E74BBCB34E76467FECDA268B662B79C7D0FC45FCB57F8F7A117FB6088B1F0F7CF7CD4E4C5F2DB3CCF72ADB3A02F3FC7B286C9CACA757517AD7F30E3A1C44
	D3FC7DB96AF232E70A6FB5C0DC64B9286FE7EE77190A4FF567DFF12EB7FB37D23F561D2B3E8F76B345B71E2B3E8F7647EC3C8F5686F13167A27F1B5C4E95171F674E2F76BCEF7C13E9BDEFBCCF4D7F0F945FF61E1A3FED9C791BA1AE61BC9C0BDCC75B671AB018EB06A1AE1736AB2D90B6F05CB794B78C6120B0147D84FA56C53A232278DDBAF174475A1B0EAA8F26731B345D8F83FD2F4DC35C860A0B815CD6CE57B605E35FA4EE2F8B32F10C17496D75ED8E59C3AABB9F7AD20086003673B16EA00D33C2B898A0
	9AC04763D232092E04FE267A90FEE52A74EDD2D8BAD3CDF94C648C9EC23E60B9D6ACF433678BB634D35679E827BFB950CE6597203E677793DBB4C0388D408A904063965238908847GA4838897E25C66FEAC938DD2BAEF323679088B709247F8B4F19E3953584B57DC21508B1E59A41C16318A502B76C234D39A45B5C2381D6BFB631440653ADD713BE56A6857BF6BD16541F4EE26F548817A4E62F6D8486BD58837430FB974FE998F9BE8D84743E9593FD4D61237EFFB64677CD29E3E66D9CC77E5348D4E84D90A
	42519E1F707368E561286F63747C6BB69ECE23E1838F0B63C742F7A4FF6F76B875087070E693B6563EE7146865348F39589CF245A86EB34F8A4771781BE304464C6719B5FC4C9C42E9BA47084CDDF66BC01F4DA51D759C61F46161381F267FB769C32573C687AC86514C70E5436BE4FC23112C1788B85FBB064FA5234359FCAF811EA9GE43D031D493AB6FB7AF3G7F32487354DA89F7FC1E0C7BD842AD6A15F11FC838EF8A94E783EA599CEFCE0EFA7ECA644984390AAE42F92039D30B2682626BB84DEC1796CD
	B3449BB84DD60FBBFF04E69C6203AF4239FE199DE9A2AEE2F60A03E752C5ECCD08592928FFFABB1DBAA16B7F29042BEC11F1F5926E46EFE45CE7926E5AA1051DBEE632BF5E2F161D481386F2899723FEA7F82C85901AD2082F62B4175B35E89AA03E0D539C6BE1A7C2630078D997E3D93DEE523D4CAF1D44B2BF1F5364CEF91667B727F00FB71253193249DC152D45C2FF73DBC2B719CD720F3D98EDFE0B9FE573029F49F93F963BF55068ECB03A3A48B16F6DCE239E4661745D8EE764A86F0118018FD9538A7EAD
	4A11BE66A947EA5AE6057F9665586373ACB54B6767E6F2B70E3DCAE38FFDAB23D196C512652D8935ACE4ED2DA082E5F9E9145832066779559889FF444341BC4C46551770F9C59964519A01F3736726F05DA3A88267664F32F778C1B899A00353AE4E4136266892240D246F79E800F0BB00F8893FA757496F29F05AA97A6E29F888A783E4F05ADB79BD40225FA16DC5743D948D90EE8790FF0734837C7EDD90277D031D668B61E4000C5F61BAC9039DF5AE3A94F017E29EC1F64CE37CD24C63CB87BD3382618400D4
	CE7B2693E933B86DDFDC74FECCA404818C11C8FBBDDF43990FC45AF3D83E90CEGC86534CFF29932B86D92BA572DDDGE1G438224DD44F59EDF003473687EFBB40493GD297E039D8B169DEAEB6CD1170A69E8E497E911EEC8FFD173ECB34BBC72A699B830D2D645E240145D4EA916C943B4C1CE0E3D579BB2CA615132A6434B848B933F7B662D9282DC665DD271A31261CDCC17B7CC5568256BF97AD146F02317E6A4E8937F3A172FA942FAD4CBE89B938A6DB3F906D7056787EA327A72D2965D2D5A5BAB23E2D
	F49D105EE5DE08E3164BBC56E52683166E77B3CC7722DBBA5DC27731CE1AAAACDC066B92DDF41EDFF219FADD42CA7165172957A57EA4E0DD2A0238604BD05F840FF7FBE4DCA673AC087236AED0CE5F2FD396255E9FFD1F62EB22547B23D7707789B5C0DCC4943F635669F1476DF2459D374B3D71FF0136C535172B6FBF3CC471CD172B6FBF9CEF407B8F8608DBF2B9723F4363EECA5595B24FEDD7F8637F3F03A43F56AB547AD7D03C798A357E178E207E639017F285729F75386357F0254C336DCAEF7CFF206D12
	75CAB5FF834587DC29667F349D7907C0DC6615487FEB7B4C4AED5B8C531D66162EBD5A77DA03A39A4B7B55D42FD9516A72FE834587C72B4BFB23834B7BFC08932EC2FDB7F83CF357F015AC4370D55E6C5DA7127C022ED25BFB1862432ED25B7BA5975ABB9A62CA2FC67EB7FB2C3B0EDFAD738C3E5A9B7FD7E95F92FE351AFF994547DC2D66FF218379A7C0DC4DB5487F998F3F05DCA3730C38469B7FFAA7DD973FC64D7FAB0ACF3CC64D7F2EFE641F8AF10DB1FC7F51233CC74648BC63E23C71DFC74FF0A7452879
	17D27C0A98B57F73781D56AC086B38D6FEC7B0996F45DF0B67DBD2BA495D8CEF67DB48BB1558CD8FAC3FCB5D5ACB9D6413F6AD13D9D824ABAC8128DC24F59F75873A0ED8AF45B5435330483FF536E195DD900443D4B8B6C7DFA7609AEB74A21C37FFCFF1CBA02C6338F9822E3126ADC23FD868BDB6E93FDA717BEFC76D1D3732EDCAFE713A09AFE0BAAF4F41B5C161BA3443E89E2EE9D4DC07F678F7BEDD7F02F0BB47ED2238E18807F2DCCC5E6148CC72FAD94A57FF3BACBDAC9E07FB17C53A7F5709F60961F659
	E3C03BA4F3FD7FC27BCFA1964AB8C0CDAC3F3BC35F695A8CE187C0B0471FE218CEF612072FF8C1A33EA4D639169CBEAD0F69561935EC05F6E436F85C0576090AC57B388650BEC9DC5F8D74EED4A6040BFE0F387FD0DC95041BB86EF24D77C311B407DB2EB3B56CA32D51EA7218495DEBAD7BC87750691C468AF2877C9E6DF2259D6D924E75BDC4575D17C0B81963FAA8AE8B42E557A36EB97BF43EDC62070D123D50B09EEB9C537170276EF833A333C1049D5B607A6B791A3A9D5B5BE62EEFACF92DBB3EDBC8717B
	5BE9E57CA540F8180EAA8FE93D58097546406D30183F47AD105B41C55F63968961B80E1B243894888BB770F6C6EC13680E343CB25F37A90FDFFA9750DB19E1B6F8106FCD155F00F66965FBD38DDC5FB77A695DEA880FF35CBB94B79F42319CF78975A521B952B21BB90F1BA80F193E3B419764F23B64BB50AE495CAEF37B793BED62D016AF597B81A1DC47F1FBA82E9542A247A5757BAA63A4BDF956454D4CEE0C47CA1FBCE6F2164F339E5D4F5B5910B8340B095F4F0F617A2E27F77113A11C4EF1EFD3DC510D3A
	422A9B79FCDB736EFC7AE158A15987AD63F13DA60F4331032FBA48466D27F51135DC17B4B6E7F6D9C147F8CD20EB7B0D7C5DD83938376760B8A74585C0B814638A783B1FA2B9EEA35D1BC904F0AA470D51B523CC88975D0438788964D14EF12B68FAF98D041BB86E7BC9142F03634E18C43EA2474551B55DD088C78230F575CF84DCFF0F3B8957484C027B5946C981570293F93A1B84DC0FCF398977C00AF2797D7D1B247B7BB7C3DF8CC04E8530F570537226DF873F281A6EEDB469BB65BB23F196321E364FE5F4
	12C53EDD962355A0601AF8D73EF2CD3CD66257F6B33E1B39D92F0C37CA71A1B7235CFF607BC491B7B339636019F4B3BB4B4064B6982617FB436B325CF8BEB3A1074437D87AF6B7A884E7F1AF7C135C2D795A5EC9DF6F6E29D71DD12D9711A70D6BE56ED76BC56205F80C9FF4EA451744235EEBC55429B21E693D8D1E2D716C7C86537B64016975BE78062C77E6893776EB42FB073D4FB1A09A9B6DAE112B47E4EB9FD46BC6F8DA396C9F1A5432137800F8AC23919AF762EF607BCFA112BE11700C0BE7E7D118BE37
	5B2757A733DF56877760CED9C7FC55E2F48E9A1DEE2E14FCB55F2156077054F1FDCE6CD36BC362536231BEAD35E3BD94EEC15C6B8E2CE34537204F8E71EFEA145F42F42C03E793GB9D342F4FC44B53D0E26EFE49DF1BFF6E9B5F917C58BFDBBC683D99167DAB29D22C4350E0427619654F158C3C792BF4CE54F65368F12E48F07E74CAD6CAC8C137DD8477432AF4916E547FD63480DF48FCF54BBD522CB3237762BE5A7BC93396C975A54321378D4AEFBB517ABD3123D78D6185F5F4A4E70B0599778313F3B6EFD
	D9F65C5FBE250B1C895AE9B338C94E641AF22A65A67C6AEFC539C50FFAC2621BEFC539DF62F542F0AB13FB9C1E413732F3C7CC6E491E69658E29156546BD682CECF21723D56F6AD55688294D6DD54BCD78C5F039FF74101B44475E4A77B1F35C7709DF4F457A11C86CCE77D8FF48255F6763E9CE4A25F7C374DD3D6E6F51BA1F26CD833ACCG765E508D7CECFE69E29847GB481D8810296E3FE6C9D08C603C5A4D7D0D8DE8746D00EC8CEF7BA6D272D961B7100937B4CCBA6306E46ADC6F9D7CE90F993F93A37A6
	C91FB684E9DA44912B71G7D6C1CA473A71318BEF9B1368723FC2FBCEDB16A91494F63E7812EF8092E3086209940318479BCB0652947B3D318D7B0275BA3386F911EC1431BF9F81900FB06ECAD34B697574243A10F64A538AFB91E0B32175C067B120BA9ED93048DG6337611914973A3D3CF31916B4165FD979381F9642533DDBD9261D0E97C4693D11FA9C3BE45E067B3F6B3AD06E1A5BF505665BD16EFDDDA8F7586DA877D914D68761F40002BB1056EEC4194AB96E3CFE64B50B6396706F52445E017CB70B48
	BF83F0D589C8BBA6A2FFC382720F233473A19C8D1040E9FFB3087CD3B92E5B043C9A6FC4DECF1811D7G60E26E646FA831A02F74BB1117C077F2AA6F02F1GC05BDDC8FB2F83F9B9B86E7ABE6415F4975FE3363B171FAA07FBF9B95149775D6F86796E463C6E665F129C3F1B5F5F277B9C5190CEGC86534CF08A8CB9647DD46E569C8C43D5F67FB7C2100CBCCC4DA179DF995ACC5DEB114F69B045BG9CCB117614FE649548F1EC1ECDF825AD656FAF60FBC29577409C619E24BD17775FA17770EFE1D25A848827
	82E4F15A2B1D482B64DE44DD4A7DB9FCAF72FA0F5F230F825C8ACEEB677EACCFC2DE5114368D428E0040A424BD3197F905F15CE91CD75EFD7C1BAB396E7E341307748E2CCF48158F89AF50EF1F285E01B5E43608C61677CEFE2E2D7EBEFCF72B4B6DFBA93992D48B786E550B5C6271FDA9F27C83EE71059E698B5D3EA7132FC84FE4360D91DC375E6A125F8B9FC75BE751B9E0144EA1CD4ABA6E19F40FBB0D1F27E0BA5E436F5808DC47B5D3EA9DD73B49D8BF255651DD875B14EF9D4326FC6B18AFA8F5E4F368AD
	9E5F32188ED83A469926BB46ADDD64FD7E3EDB64F33A2F9FF71FFADF360162936ED36F4B5E497BA29D44555F0F6B926765EB2D99045D4F64083EDFDB1EA59A78E440258364BDA047B19EC1FADF36A9857AAAC51AED8F7035152E1959741FB3CC7723DB3A56877C75454BF40F467C005A97EFD03C78005A97E13CFF8D0038D42E2FE85472C5230E49512E53164721011F8538D000C81D5A9761BE3F579B8774C90AB4ABF4A85B95BE3F571B85F4454B64F4954B787D9BF1E63E78EA0669825C5255AD73570735741C
	C853B2358F37D2FC5BB2358FEF65E3948344AD617A068EEA79306AC1A64736873565E955401B81B78E907C205A073DA6DF3689877A98C51A0487D136159EE7G7C33695FE618EE115B190B94AF3AFB7AC224630D0C87553E1820F861A135AFD6496795A14E71906A2B535CEFC9FA08493162A1EDF932B4704549B0C78128CBD67B6266BEDF36E9827A8EC59AB199E5BB75303FFF4D521DBD43F4C9EE6982137D75C5A09DE74ECF76F8678945C7A42BFD71268BFD918DF1258F73BB8A2216AF469FE6F284BF2CADCF
	3886BE86F089GA98F2BFD916D539799C0DF70081C266C91FE4EE006E72A3E18E13AE137F4B50F786B0BAABAB6EEF8C46D0B0FA93E799135AF2E6775229D6262383E411A7BA7950FCAFBB30FEA4B5324016FG1C889078285A970FFA1C37D242FC200FD22409FF94E5EBF74C4C26BDB3CCF721DBBA1D975DBDFDF1001E33CAFBD46D0B8E0A4FFCD46D0BA7799CA8AF856654A97C5BD9CEADDFA424B0B9D2D2346549504097BC26ABAC8328F9CC6D0BA81F7DC5835037A952D89FC359BE1861BB53BE1FE1BA51ADDD
	40E37E7A22121EB98BF9CC6D0BCD949F76185A970B789CB192620A9FC7FD03FA35FCA1BE4E6488FCDCDB1E799A78A8404583689ED77BE22ECFDF24813D30DCCED3321C1F971D61BD7D875C6EDFD7C2FE754BFD5B156D050552B52A66654A7710ED276F001664D17C3EB777681F1DC8BB2ABC184EB752F5B4B368BC3C1CFF8F063EBFF6960467F35C07F40DB38A42899CF7219E7793D3B86EE60A4BCB05FA190AB8764E249A88B7273A27B5C0F81863D6727C02B8EE9DBDB3968561F80E6B6F7274D309C1764DDB0A
	5F97F79DFDBF5DF0D4F9C867930C68A79D37431F0D6827D40E3B34937D1449F10D7CECC3519F90E76EC7BF55F05C877D1EFE3A0E27ED60F48F75236F4C9CD76EC4BF0DF35CEDCECC9B4CF1CFD2DCB40417F05CE6FAD6A79942699CF726686963AD8332DFF7A8FEA7E80E330E2C0F7FF0D4F9C86F7E9AC49F677D916D708FFAFF2F9842159C1758CDF07590EE6338DEB35A5A4AF127D0DCB004A3B8AE235B5347471AB0EDAC273B4004BECE6138A7CC68638C0EDBCA57BE8B1ED07AE954AE7B66B8456F22E94F5D7D
	F2BF3DFBD4F9C86F3A22EB38645BD865CF209D8E51BAD18561860E4B206F35EC033048F176DEFEAE0D6366D0DCA40463B96E9D3AFE2D03F0A647BD483F3DD334023FF70B62EAA05C40F18D54A7ED9036F25C53CE4F7AB45BA57B66CC456FF2572F70DE39234A03697C72G5627GEE877BG3619C19CD75207EDE69847DDE3C3DBC7F39CFB6F92E9B3F59C37354F33BE5D417D14A65B3F8F6BD371137C3C108D6BD385474D31FB7A69A345194D4F953F63FF057A74702FD01F2E68C7BF55F1BB2CF7221F9AB86EFB
	877A290D63DE3472F60F637E5E07FE8A66B8334D53CFAFF29F074BE96D6827A59CF7500EFECA6638C58ECFBF7D54AF7B26DB717B59DF611CED014F7301C746CF8FBB51CF695C8E7FF5211F32B8EE0F8D7DD47C94622E657DD38547ADB3231F9AB96EB313271F96737E291D53BD417B27G0E2BB2231FC2B86E940BE77FD42DF8B76A270A5FF13F42FBD2179DD59E527BC4FA31FF0A60F6F8450A7DD3B4473D55037D53920E1BB4222D13B96EDCA376CF4253085BE0726C1F96F0BF15F03ABBCD58BF55F15CEBE66C
	1F1AB86E997EDE226D597C5DBA7C3DC44F8938FFF5D92E7A3D298C2F4BF77FE659B95D820F5B3A0A5C776D538FDB7A867ADAAC23C669BC45BEBA4F69G992CCF637E662EB13A276DB4F69B1DCE2301D263790BC3F48E948A3461CF63B919C57A694F49BC28277CADB6957F6769FCA79172CA667CF3743E3F53D7C653909B9424E11AC5FA4F339CCB75383FDA1B063266F5CEAFEB85FDE72A7BFB96BE27F38A723D3146B47C66DC1CC960F6A927E34AB608B324613914A41AA6984273B97FC3BE4FC231F427513EE0
	8924C962696E71A3DD1E937595D6228C3BA90E5C45ABD909F925B8273701607234679B7CDB2395AB516EDF3BBCED25F721FF6AD6222D161B6532785110F6D97C1A0E739BA1CDAB4F7F09EE6FE5D12075AAGE80338EE6E6F5A50566D8233F6D9E46F8D08013C62F9FE73CC535B7D8E935AA51D5B3D580C65A56F99FE8E44262E374CB62BFAE45B8C76EB5BE6B6AD93A510D7B94FEFED1FF75BD4507E3F89E85B1EC1DDCE681D5EB69777EA5B66FA5A1E4E023C02B97F4F7AFC575316BEAC8731DC06677C78CE592B
	B64F32D7E9C39B2F607C8F5ABC4B1E5306F64FFA864B5E46DE391E065231EF51336027E751D7EC7C5AG6166E7D14E3BEC537BDD20E322F1C8934853BD6ED3BF162E046B924DE5780162B612FA7FAC4A5DE415652E2775D887F129BC4DA89DFF95276B8A4B52117FBABF78FFC175ED04B42DF2BA1F67FF25F47CACEEC0BA4AB04B0EF20724235C76BED9EE93ED8B42A1AE0A2799243AA6C3F885671F68335E32F4B9F49CD3729C5401677819DEBF6AA72B0BC46E2667D006FDE6143B63B9147BF41BACF79D9DABB8
	A0EE1C2759C5478A91900E667C4FB0CE4FFFB19DBB24C01AB41EAE5E8F39DF646DCA6973A84367DC6E2A67D16E47687A0254F70D3D8DCF1BF1281C1FBBEA30C01DF4D96C364ACFEB2A4AB755EE44F6A72FDFCE972A77BF5DFBCEB9DDCA07BF69986DE6FD3960733B07FBF7FF5558AA3515FA4FF3C8FBBBF36039298E65586F1265886B74DF7E91D1CE37355F7FF4738764F46F78150E517E436929CB07136A7291062BDD1EB443AEBAF691107F8D03B27FDB3A7D177B4791B9DD07597FF49323F23AA77D6247E89F
	B7F96A721E595DF7E1E30A3C2D7E4BF473381C4E506BFF3ABFCE48695E6873DF171EBE7F4B6C97B6CF3DCF32396BDDB8A54BD168179CAC9D6D8A70CC3F4D7FF4F33265F49F9D065E6FEB6862203AFCD403F9DF11A3673D4B613FCC77654A691EBE8C5FE7AA3EC57A13DFB6E0347B6D1E3A1CEDF757659F0AEF17FEF998767D6FEAB95D424328DBF4EA02F55F643FAE6F19BDF5311A5DF569BAA4677DDA0F7FB21D50A127F3997DCFF709DECED7F2983A2C5350E5AF57658D9A9EBB74A5C4C42E9472C56155969814
	DA6967E04B59BB26C4A11FFE26D794566D32BB4B379BE060DA3E0F047348E7EEBD5105FB0D9F300BBCC0C23E7CAB8A85644340967B004B6594DEF6BF2C1BFD519B47954F59BC10A3946C92D601208E33FE3710BFC8BED65BA42C6D241F50A09F61310AC2F193C97BA1247D10267D3CC9486982817FA1633B881E4536B874DDC6A0A9B4681D3DB407FEE1D2A8BCA894F6BBCD1D1401A8649C5CF59058529009DE5BA42C914D7463BFD472B7642C1B49A70024084F07697A68CBAF4178057E397A252B0A8220DE7240
	419F430942EAF21130FCF703A82C82DB539FC552DD35F2F2D98D8C482EFA5128354C126564FA0EA82C21170C24C041CE1BC17A391ADC0481FAB06FEB1EE5E27B6CE58FC49EEAA84991F2F68B2B4C74A51BE062A17296CE2A87998E6D2252167E7F504CC3781E390DD34A774CE59E5F1503672355E3503A9759F9012697656F7C31F77A4C226B9A01008FFF915F6B13247FC0707EAD7616BE3D558AB3445E6DB68379AE7B17A322D17ED65AC91DA4FF9752C8115237A46937EAFA94E97E8FD0CB878895401B74BCB0
	GGA425GGD0CB818294G94G88G88G2FECB1B695401B74BCB0GGA425GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF6B0GGGG
**end of data**/
}
}