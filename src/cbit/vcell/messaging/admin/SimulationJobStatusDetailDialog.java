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
			ivjSimIDTextField.setColumns(18);
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
			ivjTaskIDTextField.setColumns(18);
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
	D0CB838494G88G88GF5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFFDCD4D55EBF2DD4D4D454D2D1D131C5C6C5C9C5C5E505C5AD994F33E43A59AEDB764DB6F73F5463366E46531727AE43A0E0D8D4D436D1316596AA92AAA59296A8AAAAAA8AAAA54A0CB0B083730B190BA0BF1F4FB967FE4E3DF3674EB0126CDFDFDF2F0F1773BEBFBE3F4E6FF36E9D216833281CACDD11A0E46588523F0DD90290FE1DA0FC767A7CB508D5FDB5A7DFD07C7BA750BB423579FAF8B6C0
	5C12476664978AE37ABC8837C1B87A51B9799F425F6F89070F55DC83FF7860D384F1879F78EC11FFBEFFEEC7BE39B47CC4FF81BC5F82B2GC61E31DF917EFF6ADFA16141920E1425930408D212C6FF22D84296C31ABC40DA004C25ACFD90BC19GE7D6D7C9695AAE0A91164CF6691613F294E5A2AD10726AD731E70970DAD66CAA42EB6D0982A9CF8D04C5A022FC36D01FDBCB30265E6E6FBB0EDB4472939DA62B355CE6F23B0D5D96FBF7393153E631D7D7B7DBEC7DD623E8F158F7B90EB70BC6315FDDEF920D96
	EB3D45E8F5F4172EA6BC7ACF6451B2C19FA8BF1EE26B8442578AFF8783C52EC6BC036299646FB5F372CB57202E1B3F2A11F4E99D3EDB183B5E26FF5B072E35EB180E66B5322EA9BDFF50FDC47D325AC86C9F8DF149C0E9C0C2591C7C9220DA200FE9FDD8E7D88D4F3A6362D6074D663057B91DDB2C1D2EBD0ECE13957E2E2E069889BBE06AF038BA850179FC4309B28DF97C914BD7F74A7918CDE2B6207FE38F5CAA245DF129AED743A6E9B1433A56B2E68BE719EC9346FB48A76F30B24F7A69497B3020641D5CB7
	AFBBC7037742E373322325F292943C971591DFDFCFFDBD9F7E4E126A8743E7D03CEC2D0C333A3A1162A4DC83F1D1EB31EE343E0532B46A2F958A57DD26E9070807C2F57AF2A6C3F139DA96E796A93B211C71DC4EE5992738D501B3D9DE273210F09844E582317A35210334DD7357D02F01A20162016682AD8652D7E09D9BB2FC9DE09DEBB23A44AD7D96EB2749A5303C9B0C9F4053B4A83A0CBB6DEE51E86FB09DB23AAC4663D613DB10525498098D0EFB642B23F53F83F86CB059CDAE5AEC21F4234DA41ADCEE16
	E617719D1246E52231AD96E87122516694305C83467708BD0CEE71203353A81A6494AC7EA7A371498963A1A8918A003F19DD8C1D44DEABC17F9A20C6EE07F9343C5DA6175DE42D2EEEF4B8FA7B1DADC493A132025879A25AF798606F418A34F11B09E08BA01C414BA9B87E2E221CFACB87915F68BA09ED6CE601744F856B304E3C5C0AF526FE79ADC2593CCBB46BCCEB7B3C6C2AF52C2EB42DD3371DBBE919C4AE965EAC341EE2BB8E14085C0C4721A963114DFDB53B0B78EA90F80683452FC39B6FB8DE426A6C96
	235B0435F41F8BAA1829D336B81373C4568A491E24DC52B7901C587ECD81FDF2A6CD138265AF81AADEBFA73F9E488A943E9E7555E7997C14F33AF4E73CBE233C3478ACBF0D3C18ADB7F710714BF442E4976BCDCE133D937EF0CBF1759D042F543E3B9CBC4FAE0ABB21BF588A8D504D71268E52E60D2268329C6F97CD4DA6D104A1DA0EEF253C5C7D471DF2D7023C5ABB082E0E81688FF87A4E8E5216BA9CF63B09BA15E711623BE8DBB6D9CDB690FA2B235FAEB25C5AC95AC32F69245417B8BB178A24AF307613FE
	094DAB620C441ECE174586D505F4EDC26186524EE3CE2CD058E8A6781F4C0F9642B3E3BD365901AF304D2E9C3AD1C8ECF76A34461C086BAF5435EEE0ED553AC1EE33CCA623C9DB76EE5A27C8381B497AF46FE7701CE00AA33519ECB3A9B79478C581A5EC40B6733C0944E33FA4B5162D8E2B1519165BBC352B1078485457EF026E1CF4003B399D1F6AFAD795F7C832A90BFF360B58542A68BB43AA09CD6F4B42F9E4AA483388A82F12593521125905259B57E13AB488AF81AA7A0625EB79C619EEEBB6260B84BC7E
	9BB95DDC0FF42F72F4A55F3278AA6959742DB25DEDFACC978AF8B4D01214CE3FD1192EBF8753B5825E39D1CE97AC25E37EBA50C7ECCD672CBD46934641F277G34036A6ADD122784014DC75F9D48135AF74AC61CF70E1CA47E53D7C1BF84D4D905F56E0965B86F8C6F0D9122DDF333FD4D319B2B24B9D615DC674A8C83592CFE6C31901EF19017D805F56327EE6558D767F6BBBAAC3422C9FE4DB7AB6BA4435EB2AF03E717456596F7C227B108F52048CC64A5633284CB731405F47EC26CB3977896EC82BBEFA278
	3111A2056C7500D901420032E9F93BF56BC91FC2ABE9B9FBB419DCDD8E174D54D9CA2CDBEE9C904BE97FD5DEC72337113FD721AF16FFD0CD7A902B43ED5A52AF0A8EFBFB1D2A10C65F25FC376A904EB32FFD2FD612B16556EB051A71E3BA2DF9FF485BE6DD7AA6E67722EA597E09CF7CC1B7970859A2C60736B0C01A08EA3445F23B5A96299037249AED71F6764F33456FBF59C45A2F68F0CE529412AF2D59A8FF5EF730DE798E651FB620A57F60F738165B183B991EAE930CGB60B7873E4A851238C21D0FE52F7
	E82333A9EE31DBC4E8EB16A1D369AA463B3CFEAD515DFF9F5A3ED311CF6675BB4EAB9378D456203E0343DAF557DA033254657CBCFF993790FF59E1G7EF9363ABA87658F825992387C978FE9493F38867D754490616FF419CECC16BF1B4FFCB8C056F67A4D5A6B3895AA3C8C42EC4D33187605351B556B3257A85E34D93DAE7B45C9DC1799A0AEE9B34E5353FA4818DE7ABD4C6B005A00C420F020786F318F0CB4EF90A6D8876D34437244EE34B6FBCCE0F0BDE2A1FCE14EA1058FD9088D691C035B614FD96AB5EE
	6A771E3A272B42ACDF06D73ECFD561429FBC73B17F3D5D6CE39CE26EA479681C672181651C27944AEA84F282858331316A12919C2BA2FF40B1E9F43BF2CC1A7976286ECD0DBE1150DCC93EA5BF48FDE2615E3FCBE352D9D6B22ED65642388CE4868A860A81CA82DAD40BBEDAE059BC110FE85DEBF1EC852C8166A1DB55F3C7EE53DBF5137345CC2FFCEADF6455F966E3B62E6C5532F189242D2CC39B575B090D09ED9B6BF05D614A1E1C1CC1DE7954F2862B64E4B25CBB00B2C4F2993E54CFCE06CA2FFC656AFAD1
	2715EF41A4793D6C15CF2DF33A264EB3067324B6235F02BAC767CCCE065B3D7229E5285922A543E3C3A8C38B4433BD07F26A0BD08847F16CCC0AA5C1B80DE37A1384933642BCF8AB4A7F7713E48DD442434290895772F04849533DFEA73CBCED712B534EA3DB71B76B93DE68713DFECD6F51DE3F3E52233DFE4D68F90761D006F2378B6305DE7F6B5B3CDE2D756DA7D46E47BEF81B166DF61B5C34132AB70AC6A96EC571FDC1E31D3B57E8B3C9BA3FA77CD317AF2559A6BA362E93300F7F6BAA522FE87471CDC618
	4D333E275D2C6EFBC8BD68849A5C0AFD4FEB83D87F423662DA64E9BA1EA6C3F881D0862FBBD70CE86D559656B37F2F141ED575F2FDE07DBC1B5FB682EE868A820AGCA28477E7D6A5E894760ED03FE4660B02B6F3AF019D53BAEDCE5A56BA837235F55E1D27BBB5A3A024749A349CE51E4A32EE1E9EE2179C5232B5B64D5DF62E9FE9667AB7F7DD6DFF50A797D858F3F2F677D0535977DB53F9E7DF548C67CD5388DFC8AD43B8D7D3553CE7C553A4D335F31CA61A0F8062B62986F9B33153CB770F67DA01D4F4528
	724C14422970DC28D95ED13D323CCA0EFF1823443F61FC6207356AD906D4F641F66F79A4AB6F779E65FD4B4B7B4FA12D72CA24F2AAFD1637ED00344FCED317315F8ADD86ACA577B925EDEBE23F6D4476865EFE9A36637C793681B29F6AF299ED6052E62729C3E85DC152C67279D692241D8F14813482622AF6605A616313248C51A2DAC99F9035031C75AD6B9738CC555DE4AFC65AD1004AA552BE4BEE4279463AA1F51BA77B438B211C0C9D5866AFB3234C058DD807C6FB687983048DC0038DD8075E98D05BAE2C
	C136D7F403376D58DCFD4F90A9AF21014D55F3B97EA345D395B81B43BFCB7B9C1A8E629AF76298F79B1DFF46C1B811E39F0C62D8184231EFA9369842053B30DFF95F6E3BDFA836EB778B1F5B354708D23B7FB122426E2BBDB3BB76752A6D38F217EC3B2ADD3E6CB81B366D46DD4CDEB99C1FC7F10382E7F6EC33611ACF0438793B50BE86BA472C59BDA73FE5B7E23F26188142CE0E15B95036A39CEB24D8940493B8765B81ACAF05E39B87313C799C0B98447296F2ACE1904B4BEBC46CF1BA97AB06F099474AC6B0
	EF9547DE9842F4ED9C5BBE0C694C0D580E86E83DD58C0D6507AD76CE47GB499F2E2A53A4B6BF7B6B76DEB5E563EEFEF7B56C678032DED228614E75AD54BFE213947CBA8341179ACCAFAE63EFFC596EBDF8FD15B10BC73A12EF08F41768EAA575CC4469F33593C184587F944133AC476AD64785BBD62E9BF27D746AF7408A7B2EF1272B33D9E7F972E59B29F0F932CF7867BBC2BEF5D437499111EE18BAF4FDAB4E3A81B15731BA3CA7B94DEE155E579A8A7DE4A1F38C7DE7BB17BD8781CF7499E6CFFAAC769196B
	DED82B83356D457EA7522CEEB762DE392D046C7555EE7EC27B33483D6AF613C971383D6AF6B32D875BCD9244156E63FD8D2D2B21900E62588DA7313EA56DC3B91BE93F368842C2937469CD0837D31C6C99137DE532874E70973C6648A49FF92EE8525EAF71C7042FB24CF83C6E45A3C791FF2ABC32B5426F8949A3E4FDA1C2C74A46AAE10028D251C46FF60C2E18203C7704BD23E48F5A6CF0D906E0B0B3DA6B2C16EEBB59EF1646576ED1ECCB03CDE87F10DE74C98C47D65A781840ED1FCA3145904E5B0F586FA9
	C64E98487981430E5B50CEA45D295A65AD09072FB846E318734819C479DA76773665001D4CBBC407435AE2F13600CB2479C395590E1752BCE37774571113323F36BAA676D707036E7FBBDC26EE17235F5E097320B3E81BDE89762BGE2E5478D1132595FCF8E4BFCE68C7A63434AFB0E363BA6A82BEDBF76692D3D1E327FFDC4A17B083FB2D97A0F466474C7077D25E76B16C747088CA340BFE2BF4EA97675138BDF070CBD52AAE5DB27C5F43868B28540A61783E60862C90652D3B2497F7F9A1C5C9D1BD93958EF
	475FFB1310F0D63D4F3D3684296E4E171E254FDF1EA56BD2A190FD738E4C49DF8954GE4G9201C28F20EE1776939949EA8B442F93C5E307191E7C60A26C1045B4A043E4E24B4AEEA662C87320347EFF79328F14308B4663FD47FBE0C154E26C66ED436EB5BF0FBAA0372778833E7A7898BAE6A49FD04FB163A93E6000FA0ED9BD0AF34C8C08EBEB463E638EBA9FCE0470DC0E2D6F417EA423997B09D7A936328566A3AD0869E8DFC44EBB489EBA434E6989E47F0314238CD735845677B09E578644C35DE7845A77
	3076F6CDAFB9BF379ACFBA7AC528DFAD44AEBF70365B8861CE20D020289626BFEBDB8F1C1C389FF9E99F69C71A789983621FFFCD703D7C6C837106E5245EEF154F73247611FCD4B92FA9D933CFF3DE13F1F4C8A79C1476A98E4A731184DBC9B6ABFFEC145C23D44E971B497F72F4F16B363DAD5B8E88E8073B783EEE6BC1E687A7BC038F3233AFE6076FEC935B6148BF083E4D7CEC8E7142B5A44D810FB3B36C471AC9B7A825338C3CAB49CD4FE336BA2C8ED7394B5409E96F491153468C3C671D76B86923D85BFD
	FC0C7064EB9856FDC9F1F346487C121F2F51C5F0B3A434F733BE601C25A4BE9274CFGCA83DA8C24BF04FD4B21714F855A37E874AB5BBA3BCDE8E86437C9703DF62B95A63EFF36D3A0F6BD6EG3B59C879086F236D874CCDC10635503A6432F41BC516946F128924EDDBCDDD941676E169B8D782FAD59E423EC24757232D07308F381576B5A3908E830A66783D94CFB8444E8152B8DEBB2AEC4FA4BDF906C94F11C3131B17F87699BFFA7040B9A076897E6697017799334D5EFD46F3FC9FD2B88CF3E320DA201643
	4C86C107C91A1DBDA5FE4A369EE67AB39EDFFCC66C4DF6BA572D177A139F945886565FF78F625D62E448AF34627E4C06759E67E324B1D4D7EF37BA869AA53171FCE0906F8DD73562BAE589AD538961902028D6BC939B7C927B215869578A91F66DFB9A8BDAC6F5892DCC1734D6399FDA406F87FF12C7EC3562C728DFC02DC0CE20E020489F31AD4D4DAA95FC4FC1189234BD390C963B48EC71CCAF5AA28E4AD974A36E85FDE9C59DCBFEC29D7B2CA4EDAB041DC0A1BFA15F0B167AEE1391CBA7EE133FDACA6F13C0
	DBEAB9691476C127AFA57545515565B67136FF7352C20E6DF4B7B95C96525BB0BFC78EF9572FBB781ED794481AG34GE849CFEC3F3B6088C973D1GEB1103C7F265BAA4E16FFE2128D7958C4B6CD6E06B583E5BCB23DAFB6DAD405BCC7853B4111AE902A0BE9C0879A95F02FE0A85EC61912C333FE9572833BBDC16CED51D5DEF4175D2F19B7AF31C165982E1ABD0F09B4E273BFAC8594D105DAC60FCF34004753878666B04624FEAF56FE854E3C211ED2C7EA6374975D86EEBEC27BC5F1556C9134C976F11AF33
	ED627E124D954DF45F3170A81BABE6F13C0F62458A1C7967B55E2714C2DC68D13CEBF0DFBE716DE288979C03B9B6D08DD09B5060B1ECBF3539954284679CAD8EE7A3B4B02BE20B11657DB24FF75BDB1BB7F15B7BB6CFFBBC2C49739E8F6B723447439DF94A711059FD21573EE6E07EFAE5127956D81579C20F856A6797E81F96F5CC6D6717A99EFBCC6D6781AB7AB981620A5B31FDDEB40CF890E0896D58BE7FD8CEEC24EA1FDB9C643E2554C2D97BFCFA183F0B520E6DF35F88A93354G7D81D02B8147141EDEE5
	DBA46D4B571E1D53406C90EE70EC0B0C673F46F03FED2E81F906503BE885C6282BC0B5C69C8F660E7273E8A3265DCD5FB58805F0B450CCA34ED95E36CC4E071FCCB25F21D34E47FC7CD75A7F4DB52AFD1FC97185C6356F4F64FDF0BA44B59D47B67E1521841E7190CE85DA82D450B1A73F84288A28912833835B7AD3F97E46E8D6B164B1DA5A8327730D208ED6272435B15D1FE1FF8FA87E96CE7B993B373DBF1AD29E4CCEC9793E7B354773354FE81E4857BE23F9AA5F7FB97D33793E6F217FB1DFF98F1D79E30B
	957D017B886F887F57AA7BE009626FD16B547B6352FF830F4BB44FDEA74EF77DA473BD63B7DFC4072F3D1F07E91F995B21EE53A994CF68D0376903A3582613A02E34935B7405741D24D08847G25GAD844AGAAB4E1DB7E3E1D1C017BEA4B879C8EFA82CB8EE321ADAB1B3524636AB12F33BF13626C4F64CB47CC5AC7B71A54E79839749E2E4124BE4338005FAF96A1EE3E897B6927C7557CEB3AE41ECDDD3E78BFC87B7D4EAEB57F59949F6CD2735FAF703B399037388B797F37C5607E567548FF1FE7197A7A9C
	BD25BCD87DD8EF705D07EDB2CCBCB75BE2501E1BB5983C67E6FB8C5AF333E603F2EEA63DF7A3156B62170A98FE4CC06691EC8D3547E8B7F653BE0F2D89FE2477E50C7C94867B423F51F9CBCBB70CF15D685747C6E4DF4ED17CFDE4E46A7DAACEA98FE9CDEB746D57EB82F887B246286D57DB0D5EFE3D43286D57FB0C5EFBD0F5E33887B5580DFBD02FE5619ED4A4770FAE1BE04990CE6358F20AAD06F0119931F0DD2036A4E511E7DAF7607BD10C47AD817340FD28D3B9A7E3B6D8CC672ED520D723996DA288E897
	A75777910AC5C2B81EE32994CB0170E20E9D76F85711241FCCDD0A77110F71F0FA70186C9E1CAFDB31B6DDC04FE8CFF49B1D6CEC6DF3FA2F026C2797D9E08DC377D0FA46F0EFE225056D5155403309449F42BD3AF7F4A5D29A3345DB27910BF20FEE741F72DE4AFA897B13A8E39B247BCABFE42B77C4883FB0A8AB5A027BA65F5B35524C0478D48B6EE5640EF96F8BFD4AF5DAA469A474C03D6FE17B2ECC278F05924137DD4BFA14BABD311F0CEF3B1ABBDC8E2B15E8A16DBD161CD461521E10AECBA9F72D44578A
	4F209E546D0AE12DB491700C6DC15DB60D10F1165C0FE3636C45E3FCAE83E9B2FBF06D763BB13C2BDF590BEB379D6C7DF1888783C576E25ACDBADC674577A21FBEFA57E52F43C57703D83A01ECCC174E4BFCFBA9FD175A8AF58428450AE51E30E3DA2B9553BE6AA4E923A1BC93E82E9557515B398E69002D34E199977035EA1B8D4B58B3CE5206C3B896A8490672D68A4467DBCD647D8C3C975B3D0D5C2D553897ABFB076BD61D053C52B82FBFD2FB955967641781555BD12EB97C2EF81B1D5F1923F34AF0884782
	A559315F08701A6F8556C7CE1FE43EE7BD7225593D5B66E28526F760FBB677D1DDD7B854F368A40A2FF4286750FB3250E79590976EC0FD3F77F8B7A2837082277F3E12659B71FAD721C4112FD2238CA65FB4DA7F9A1CCC3EFC0E07D13CD301B3395F27B88987C15CC2A74ABDECD773EF6813F936757962DF5DC74FG7A547CF7D0BC34CF4DBF45097CE3A02E40057C9BA7696F1FA6196F179E791CAE7FBE8AF6E1BD19C55BDF04CBDDCFE6D3BC5A252EA74D637C2C81620A5C286F8F9E6FB38D3ABD79073AFD597B
	A73A2E09F22B5F876B24F81CDB7DBED8FA963E8F1688F1ABC5647F1447DAB3C4746497A97A627F975A4E63C4B57F7FC771C4D14DFF5A8872CF05384AFE1C0B7C1D76ED91900E61587763841B8961940E1DC5318590DE520F324F22F7AC585F3395FFB7E7CD7DDC5BBA25BC184E8769F814F7E2CE7EBDFA44FE22D889E01BF93ADFE6635C3766845A6B8FB9B847B31C60F7B06899D2B004A300E20192F95C25B4EEAE04B3000A876664578EE05C47CB092EA47DE475A17C02D57993253074FE9465B119BBF8C8B902
	77DCB15F431E975B51CE1D83E827F0A75AA9046BDB5BC7EC918B61C42039C00BF85C9E9A17896112C1988B014C0398B74709F5A2D64A67CB56C4BFF10BFC44C9FBED1CC77684767237D6487701B31B5CC45BE4A86895B908F6FA05E2F190CE627A7EFADC60F225AA7E7EF1FC6A5B1FEBCAF9B01D8F513950FC50B71D5BA11E2FA9B3399D8CBA7AFE59C98C7750F0AD8F3F72337B8AD236FFFB3C9970DA529779DBE7B15DDF2673BD8348A21EC4FB6C66775103393E2F5235D58C8FE70D12F08A8F1FF3DA76A9E2FC
	58C43A8BF6DAF87832891BEBAF9FA7FA399C83EE36067CE29C4F1953CF62FC7CA0CDE3666B4C5EF11CB3E772B4D70D9319BB9C56FE1B5DAD5D750C67698A0670BC4D78A599C3257BC607AD1D22196145034BE53C41C476CB84DC6F4D64EB4962A1365E2B04E78390596FE0F7321664CCFC8F608B0B3CCE2D1430B375B2760D045D5BABE35FCA586EBC455D00CA36461B3DD43DFEA57298C0AEF18857011B0EEA25890578A81E669FEB3452A4C0FC8ACFD36C754E9FC9339062B306F02DFF9FDFCF978EB3BB154233
	E6186D89B1BB5D6C1C58CE670F4A7AEF143052E6992B1230D93BE5EC13045DB3203053B7CC76B37A543293F9DAC0AE73B06A576A3597C05284C1FCB8CF7311D3ABCDAC44A772B467F9590924198F716943D8D7E70DF956792746304E6BC7B0CD3EC6FDAE9947B35EA21E4E16C5562AECAF96461B9112AEB80B143FF284ED7EF85644B63F76A7F25F2F59E1BDE1F2551B5C9D641AF71D4BE404F9B87D364315BA65FBA029D0B659534A60F2640CF84BD1CC7B3002D11463385F3B542C1CE3CD645DB876A90DBD742B
	0CA6D994C9167523EAD9485E1AF394E5299E27EBC9DE66AFE8F8BD8FBF4E435237E432F1DD918CE54C9C4535F9199E77BD160C625AFCB57D76C559984CDB005A46B02DAE977B9AF18C533E18C74F12A01C84144653769D656F297034CD6D345CF1A89728ED9C539ED8434B9D675F18DCCB4B05F092D09ACF5B425F3FDB4253165177CF4A0220DC2036A05CA7F95E0EBA0B848B42B256B8300C05C1D846F38EFAE7658C6877009A4F40347774E15A4EB3B0ED041B3E9FC342C0A9BCED985F43D94853BA687A357897
	D0AED063AFB06DFEA7AF7797183642C54BA5E120941E76E12E73C21EB60C6ADCBC8D4A85EA1C06756265B14FFA31115603F5BCFCC556CF70E4E7685B0D9D2243F5321CFEB15054C25E1B6CE4B125DA8936099DE61EE0D5D949672CA51510266432B849BD334FD761DD284DA6653BCE95430D3AECC17B7EC567B4B6BE0B5364F74158F8759F239E77C2647DA85EDA247DA59D6E49C6F3BB2C9FB9F47A7424AD35C4EA2AC4C769DDAC3AE7CE465434E9B8E77933573E4CC44472E5CCB25FBA0F6FB75486FB4EF5DAD4
	E1F3B06ECB306F5B0C842B77A5EE20F8C830FADF22A2875BD2B844E584233E3939EA395B4E14F9BA4F7435AFF0A41F1497FC267AFC340B6291E72A4FC7DF4A437351D808ABBC8B791FB86A750E5BD90AF75C4E7245BF0976C511E729797F0662F1E729799FEA676F38C1DC69597C1CE30D1AFF6859B24F2833FD713F1E76EF71E72B7947D1BC79ECB57F0DEB11FF9A445506A07F9F3D5E710B8E11F9A60478629FC57B0F1490B57F1BA93EA0C44DFFBDFF47EF8944351E037C7F64358F98D83DFDF51279AA9C4AFC
	B34F713F5710FA8E567703349FDEF80E3A3E9F237862F354757DC5875677CC08B31C0B7ABE502716BB69DCD90679677A32F701C84ACBBFD7ED6F8FA9AE1C27367755EE34F791448D1E077CAF7132DB5AF9B24F456779627FBC55D39F2A667FB245D7042A7977BB11FF9944050572EF18BA557C17044ABC734E7745BF1A0EED45672B794F20F84579EA7E5F3A10FFBD44451C0F7C133D6AFB4185B24F128BFC715753BB5C159728793FCD71068B547CAF65753D8D6292AF40BBAFFFBBCE671D97627D160663645D8C
	DF77DB48B715581B9ED8FF1F72586F2F02F29AAFE4B29B60A98205DC2875BE2A1D3EFF90A9454541B36942407659662B5225C3D888D36D5F52B5FA039E77D80B42F05D9ECA31D288D7F1EC2E9E77D89B4364EF7F88DC56DB94FFFF1BB375FBBC7B261487537953DC5C93B4F0BB5C194777B4389D0C79840B0170CC0ED9A9B69F42999C7BEB5E2948BCD372D7CCD8E0BEE7BC8A72CF0547293E4B42ECF07ED1344B0A0B50AE1F3623DDEAAEC2FDE7D34CGE111E373A8968661B80E35357FDCDF929EEACC3CC83907
	5C7F33F9E859886D47ECE0DF03F6C965F6F9238C6D32046BFB73DA022D38F8CEFE5945085DC531C688DBB936F92D164CA44F294A3EC2A30F5437EB72184CBB57DA76E1B6F05235CCA84890F5B13FFFE3615F84607ACE276F0BAE02F0A647EE23D871AF21DE7D92312A895F65DC94000DB2FD24E1BC76CF48A310B6634B0E4C86BB9C5877367D926DF21E837BD9A7577788791C3BA4D727626F179CD35F4FAEF5CEFDBFBB2F8F5BCDB037C3179B6BC78C37433BA23DF38461859C7B04E2C2781C7C12F0BEAE39DB25
	F427DBDE196F7D14474FFD8750D71DE1B60865E7D23561E8174D7CCC2A136B7BA70A0510B49CDBC2311990CE6358B075A5C9F33AE58E61BC42A90F49FE33419FE5F33B581CE817CCEE17577B50AEA51720ACB73988D68F61D60E5DC9B19142619C2BF1792B63A4BFF936DEB2B93BB19E877D72184C9DBE6FF674AC6FE763AFC13B9C636F654F677AAE236F60E7C23870D2445E27D899049BB99629598712BC014A156947E612BFB4F91C0A9D7C35C1B6DF7F6F63E48F57AD4D492573AA3AFFE485DDC6AE655F444A
	46B33DB00E8DD2AC8A42899C7BE408FFBB05E35F53732F459056DF06D8985DABDA8161B20EBDBD0ABCEAB976B15DA7EF023015E3DDE3A8DF90475859AB619B4E31F9F46FBC8142A9C0ECBF3DCF0F7B6E8BAE433D316B741EF79A6F61FB400BF93E7F5163BE3C9001E79FBBF27959C784FBEF3F941EB5C064BEG5B7FCE491BF87F7B7AF2FA2651E8BCAE3FAB1AE8A17BE8875DA697595C5BEEB1D9BB855C8B3FBADF39975EA2719B0C40EFB27F33CD999FAA4547F3396F6267C349125C8B6039A4025DE1E0F2FF53
	BE315C1F3DAF4B0D77B2D3F4A43E59E25BD92F901C4915EBD04BCD7895DC0EF2972CD64BCD62CBAEC739AF63E7D61517B3399B61E9381C5D3BE0F2FF30B6006FA4FCA84B3DC1425E5CC5F8EFF5581C7D22294161963974CC36C5EB5532931E03DC76373A55321378504B79B745B55EE53F1A1F9BC5C97AA440B365F256AFB0FD5E6919D81F0CBED99FBCBBBBEFB9F9FF295964BAE1F2F938C2724573BDEAFD483D1505DC1FAFEDEAFDC8FC4665589E1C8EECC785D7A0966F40B6D2FC857A6CF5FE8ED7F68553319E
	1E2DC0195CE7236E09F56C5EAD6B086728CF1413EFD0B4532FDA54131DEC2EA553E11F28561170F4DE01BA6E7252114487F3596771734188C976B8F8A6DD416E30B059DB82B85BCC4E12E547735E58D5746CCDB43AD422CB32FF5927161D70CC63323F69D04BCE6297F359AF65F2692FE43297433362CAF67706497E3EEBE259172F11E547F36973BA48DD1EED76CE8F4919DCAF3A54F293FE8DD7225C23DE6D044437DD09F267D9D0A627A4F788BCA32FD436097EG5AC4F825ACB71E9DE7E611F7B0DA0C6EDEE5
	0BE0F26D68D54BCD6EA745F33977FB49CD62132F64E721591E673B9FE7E37BC803B46CEC341D26C967F9AE4C266FF498BBFABD3FFFF5834D1B8169741150D66877BEAB79F789D682D6C5F020C12008C8FEE7E218FD9341A212D7C7D8D956E114A301273B010E33D60B5DF45805E35E832358F6D3A2D1DE9DBD33CE6379560F11B1E9G72B40BA72D2643746762A419F70FE17EC511589FDC484F38B3389E3378BD7A3C2B20CE8255G35810DDC05FC52473D754896302CC81EAEC46FF93617CA439BF838A8874F7A
	589E26A3175FA505B296DD056709F767226CA5D763F9220E1E2F34C2588994F2B55EA579EB170FEFA5531A464A1B170F67E0D1BC5F3FCA0B3573710A48723636615CC3FF8D1E5BBEF394652E81CC3C06FFEF63984A9DFB8D4A5DFB0C6E6BC3B89328A88A53FEEFC0194AB836F5B5728A0BC2DE8F2EC1DE4900893F423439E5482B6AD7482B310C5EDD06F088D094CF7BD9B972CA605836EE64D5FCAD72FA540C3CDAG8B3D96533EE5C1DEC957A22F9F69FB541990AE3A8E660C57E15AFF74A22FFA0E9D35A12F18
	6B78786B702CAB0FBBBC6B4617AE14E58164D9910DE559DDA8CBD3B44AF287CD9B8261A820841E76AC91E5C9655899DC164A6BD16F7F616770D640222F67B66263E75A75486BDCFA6ED2B49DF4862A1F4EF56CC3DEED9C5B62C0DE0953115783CE641581D8458D18F6E99F72B25F405F4D27F74E23A01CG144A53FE6AC6DE0BB8964F7D5998033CAA783B6E41004D0C4134035C1FCBE210577DB4ED590D2093D05B0D18F60C7BD364D88867B57FC65E97E5FB7A331FCE4D59F72A2EA65D12043FCFFF1FC4751D2A
	8133C5B43132FF9A42FE257026B95277D5C3BCFE53A4DB22F2813F0FFA2FC7BCFE53C40ECF77084F774A7F2947EF3E64A872B319278D932C4BE8F54B5FEE1FC76F820A2EFE938B2F9813F5BCBE6699B71D5FF9E0BA3E485F03E960BAFEB62E56B157C3467D63EA9DBDF518B66EDF47BB477D6B788FC12923348F6DF5F6BE9131FC53A619EF26C73E201B827D7E4789BAB6045F24BEBB9D27F854CD6A33533CBCEC9731447EB7639E426FB44FA3441B199CA1B7EB4B93290147811684B4DF9147F89CED73E71BF4C8
	AF444AF90AE2D13605DE7788823369AB134C376618B2DFE9EC203EF8160EF7B531EADFAC22F8C32C5A97FD47781883F189DC5F2D86ADDF945FA24DBBEF511627DE83EF854C8A94F40B5A978D2B7D59A69C5247A8724C3C053FE36AF527A2B01BBEB949FC85E54AFC29BEF47776C5A21D8FAC3CC56D0B47A83E789635AF8E1471BB8590E7389575DDD92E650B24DB25EF125E2AADCF3A86AE4C007982D0698C35AFF6F57B33CD8D24EFD264691C013225F81D3787E6535FCDB25FFB9E5F29991C9128AF7E030E2F21
	B3543E78AD45A3E628FD514E476EE8084B0BC3FDBF6B557205B50E49919427ADCF38869E8358CC2054B835AF0E5B7C59E6A1244FD064A9388DE5ABF3CD4E2655134C677648D7F2DB203E780A5EDD293CCD6D0B8A0A575E267645BDFCAE5A88F1F1DC5F73C4ADDF945DAEFD2377F6EDF9EAB470A640BA0186EFD77B62AC3F3E880574D10ABC713723EC590E4959741DC9665B6511AF59076E5E3EF81D4E2753EED77BA2174E13975D2E76C5983F77348462DA6F605F796E5372454CBB189C29F7E84B33D0834FG2C
	A0FECEFEC93C5A97BB7C5A269252B7A872344523ECC9DEF7128233691CC966FB53638E18B3BED0DFBCC8579B4171EADFA4D3BCACDE6D0B7DFD680BC808936EC4FD3FF0EB792273CEA647601D5A7204EA60D1004583A55F297645BDFE470BB4C83FD811C7FF974A766DA43FCFD6BF49FC239E79D65C9528AF5631BB63F729FDD1CD712A3B543EF8180F9775909743751D26B9DE945CADFDCB6EEEEDF9AAB57086405A001CF72BFDF13EDFDF84C37A88C51E583B655FCF181CCD53BD5EF5CE0472667A5083091DBFBD
	C97715965D2D7C6697FD6DCB7A7BCC455FEF1D527D27D3A546632BA96541F44E24FBDFC2827C1D0067F046E5741E8A04ABB9360C6E89B4C03813E38B5A708CEF10E3823DCF958961F80E4DBCC230E488AF6218C873E6923E77A076GAFEFA5471E21D883045BB8366518371FDEB8A67B66E5455F8347265ECFE1D34A0369ECB2201F1C5C8E0F9A50CFA39C3B31867D944631FBB621DD23B97674A674D3B2471ED8656D27765518B70D279BDF0D3E936EC5ACE9B57A2908E3DF14F97B6951B259B73FD17C5DDAB675
	FE6A1BD29E526F9115231FCA399DEE20D8A5049BB956C65FDBB7C0F804E39F195156E19C5BC431F88827F06C49AEEFBF993BB16F821E6E0CEE74137EBEDEE73A51CFABB8361A366D327B14FEFAD2715D36B4455F26FF43F75B46261407749DAD3AEFC9FECB2A1E5BE1BA451AA0EC6658ED74BB10A3900E60D8E3AF5ABA16E36C3779D2A13C08E333E85ECC88974FC46CA47F2D1A8A0EDDCE3196881BB976BC45C6A09C4631F3DD5E6DE92DDB764D370A3F9F74FBC7677478E97E1472E0BA5FA2E2FB0A66F6F82B9F
	7B3DB80EDDE447FEAF09E3C76C7C3E9047066D586FE5F26CED1BF7FB3A0A7B2970FE446E37E1FB2A64184E0E6D2901E3C607371F740A3B0E4B95FFDF7AEF3877B8E3CAF9B01D037B50CFED5C8EAF74211F4C9C7B4A09FE9A395F53A7345F6358155CCF719CCB35FB7B69987F4D3AE41EEE4C01FECA6758438E741370GE21F77F97B6911BE59B77F29787B30667A71747A49B925BC184E5B5C6827A2EE0769EE74D38947F67076D449318779785440B1F6CF0F78494A31AD5D5EFECA6563D390CF37180FCF519C7B
	460CFECA60D838467834D171AD51DA455F0F7D9B46277F73EF981F2E6763D38A3743D2AB0ECF73B9D6570363D3BA47A2CCE8EB61C17E3B0BA69C1FD6F26C03EE6F71A91A7B290A27CB6A4671290DE379E69C1F1C9C4B2677G48F7FC6A32D00FBC0A116F787CC5403324BF672A3FB34A707716F97E46D7CE1740638669EFG590C0396DB3F2D59B2E4126EB1045135CAB0489076A01EB57E135E45F33ACCDDA6174B54C9D3633D07EADA17E6C25A648771DE4D2BED935FCF7970A865EF312B78FFCD57BBCB20ACA1
	9179BFF9447F6F5A3DC457C34486E5BC4FF9ED5EF7A82EED43334E56C414F5DEG327E2D0D54D34F6F9294D0B9496FF3D99371B75ACA3B85EE177F22EB0991088BC9447BA0F368DAA29E42091CFF6AA6FF7CD9BEDDB549178EF9B2F83E932BA74E97339A75DDB98BE5F8E9B54ADDB58B655EED166B4583655A756237F44E5DGF9DAE6210D5DB93E6BC5A81D0F87C15A50D9A8F305F9E2DBB719356B0519360F84A8AB0917B758BD318DEE6AC69BAC66B6782FEE34017EA154254E22EEC34CB6F60BEC1B7BFB35ED
	F3040EFBC5D0D689AFAF596A5BB64F51FE27915236BE043AF474CEEC1BE93D5A36391276ED41D0D69867BF5F6A3F4D3CE245B6B3134B909640EFEC45593C5B552FEDE863C51C7FEBB66FB6730E9D6D1E7990563D0BFA64B633114EC38B9E06355443682BFBE91EFA88B7BD0CF23A6C937BBD1A669B01BCA1BCDF3CDF7DD83EBFF2DD62398CEBE8D92B00129FC63963FBE5395F226B087990371067E922733242A4988B12107F4581481D446689102705676B777BDB12521DB9FE9FB5A889E5C8F7205C61C9A877D3
	D6D96E5F50F9CAB4444571BCCE4ABF8D420BB87F5F7AED37ACDF9E9D870A9E01B6708866FBB10076495668742C67911461189965B6BC02F23FE613657E1A56DDA7440D70BCBDF45C0E01F0BC67BF43B4B17F6768B83EG722473FC7785A077CB3CDFD971A84A305E0C753B6CD114FBA7792985296D3CE17E941EF653C0893F0FD3EF01B66936B86C259BAB4ACA56D52E42FE476A14734DEB8BBC5F05FDF23E5B0E84120F2555371588FEFF336F404E9D8DAD5277065A3C6F67FCF1D4874FF5D5A84783AED90E2BEB
	82177FB9371C6F56CD016753ABFE577D51D5016B7D60EAEFDD74EBBCF5796A04DC767E1C40E5FAE8D04E7756A9787E0513F23E642E40F519556D2D4BD23327AEF943F259532D014B34E6C44ED7579BF83E3D23F23E3B8262A7FD8B4A96F8FDFC46662DF7295DD36FBE45FD31336C014BBFF7DC4E57F48A79DE9564FCFADB60FA7F0F06AEDB28AE6BAB306CFF6864320B1D014B34B9DB4E67BC85DD8C0A5F48FCA420FC52DC52612DCB3653D317E90A5F542CB9851926AF1573451FC25BFAACD74E3731BBF0DDAA4C
	5E3A741BBDF5396618DC7637BD2750CF344B794EB71DC2BFE110733DF98A3A68B5F459497B098FE9B8ACF78BC4443EA4640842BB4DB06134521FA9ADE15FBE92059C7A73312230FC3B43D5D2578913521203A42CA7BF3F6A8D679FB02DE5AF2AC092720B342210C7FE30566268F73BDD42DB1E97D4332E7FF0DA61059B7AF5C25EF6E1A9886AB49BF78AB9A7480F28B68AEF9E27BF6DC0FE9C46AA8A050DA46F571077EB1A773BC6C1578182FEAC639D84E7314DCEE307891264F79ADD3D3404BEE1CC48BFA264F7
	393A0FD3862220BB327D883025A1927DE62330CCB4539F252112FFA8975DC4FE1AC60A78EE100E59EF3C81F3937A4FCDB2536DA1005E7042609F43C4A1173C68D63233DE9416022D699F85523BD8A564E5ACB0A0FB1509C63D49ACD9C2DEBF9105E574A59AA910374D5EA97D19CBDE740074E05EF73D6BC45D85CFBF955BDEDF2493F4BBE110209FFF84938F102FC3D2BD48D4E7BB15F6457F8FCDBE045FBFDBB52E7C7ED946199DBA3C932C1EDFD64DE6E76B0D33655F1FE35F1A391C6E4D04G9EBD9B3FB77327
	7C35026F5F88EF36992DD6D87D755659BB496F05EFB9A91A645F703AFA99A95F0DE924C896F77DB2A2F70FA24F7F82D0CB8788CCEF96222DAFGG3C23GGD0CB818294G94G88G88GF5FBB0B6CCEF96222DAFGG3C23GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG67AFGGGG
**end of data**/
}
}