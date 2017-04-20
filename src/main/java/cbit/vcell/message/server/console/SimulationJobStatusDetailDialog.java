/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.console;
import javax.swing.BorderFactory;

import cbit.vcell.messaging.db.SimpleJobStatus;

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
	private javax.swing.JScrollPane statusMessageScrollPane = null;
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
	private javax.swing.JScrollPane solverDescriptionScrollPane = null;
	private javax.swing.JTextArea ivjSolverDescTextArea = null;
	private javax.swing.JButton ivjResubmitButton = null;
	private javax.swing.JButton ivjStopButton = null;
	private SimpleJobStatus simpleJobStatus = null;
	private ServerManageConsole smConsole = null;
	private javax.swing.JPanel ivjJPanel12 = null;
	private javax.swing.JPanel ivjJPanel13 = null;
	private int currentSelected = -1;
	private int totalNumber = 0;
	private javax.swing.JButton ivjNextButton = null;
	private javax.swing.JButton ivjPrevButton = null;
	private javax.swing.JLabel ivjJLabel7 = null;
	private javax.swing.JLabel ivjJLabel8 = null;
	private javax.swing.JPanel ivjJPanel14 = null;
	private javax.swing.JPanel ivjJPanel15 = null;
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
			getJPanel11().add(getSolverDescriptionScrollPane(), getSolverDescriptionScrollPane().getName());
			getJPanel11().add(getStatusMessageScrollPane(), getStatusMessageScrollPane().getName());
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
private javax.swing.JScrollPane getStatusMessageScrollPane() {
	if (statusMessageScrollPane == null) {
		try {
			statusMessageScrollPane = new javax.swing.JScrollPane();
			statusMessageScrollPane.setName("JScrollPane1");
			statusMessageScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Status Message"));
			statusMessageScrollPane.setViewportView(getStatusMessageTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return statusMessageScrollPane;
}

/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getSolverDescriptionScrollPane() {
	if (solverDescriptionScrollPane == null) {
		try {
			solverDescriptionScrollPane = new javax.swing.JScrollPane();
			solverDescriptionScrollPane.setName("JScrollPane2");
			solverDescriptionScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Solver Description"));
			solverDescriptionScrollPane.setViewportView(getSolverDescTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return solverDescriptionScrollPane;
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
		getStopButton().setEnabled(true);
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:15:52 PM)
 */
public void resubmitSimulation() {
	smConsole.resubmitSimulation(simpleJobStatus.simulationMetadata.vcSimID.getOwner().getName(), simpleJobStatus.simulationMetadata.vcSimID.getSimulationKey());
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:05:24 PM)
 */
public void setStatus() {
	simpleJobStatus = smConsole.getReturnedSimulationJobStatus(currentSelected);
	setTitle("Simulation Status [" + simpleJobStatus.simulationMetadata.vcSimID.getOwner().getName() + "," + simpleJobStatus.simulationMetadata.vcSimID.getOwner().getName() + "]");
	getUserTextField().setText(simpleJobStatus.simulationMetadata.vcSimID.getOwner().getName());
	getSimIDTextField().setText(simpleJobStatus.simulationMetadata.vcSimID.getSimulationKey() + "");
	getJobIndexTextField().setText(simpleJobStatus.jobStatus.getJobIndex() + "");
	getComputeHostTextField().setText(simpleJobStatus.jobStatus.getComputeHost());
	getSubmitDateTextField().setText(simpleJobStatus.jobStatus.getSubmitDate() == null ? "" : dateTimeFormatter.format(simpleJobStatus.jobStatus.getSubmitDate()));
	getStartDateTextField().setText(simpleJobStatus.jobStatus.getStartDate() == null ? "" : dateTimeFormatter.format(simpleJobStatus.jobStatus.getStartDate()));
	getEndDateTextField().setText(simpleJobStatus.jobStatus.getEndDate() == null ? "" : dateTimeFormatter.format(simpleJobStatus.jobStatus.getEndDate()));
	getServerIDTextField().setText(simpleJobStatus.jobStatus.getEndDate() == null ? "" : simpleJobStatus.jobStatus.getServerID().toString());
	getTaskIDTextField().setText("" + simpleJobStatus.jobStatus.getTaskID());
	getSolverDescTextArea().setText(simpleJobStatus.simulationMetadata.getSolverDescriptionVCML()+"\n"+simpleJobStatus.simulationMetadata.getMeshSampling());
	getSolverDescTextArea().setCaretPosition(0);
	getStatusMessageTextArea().setText(simpleJobStatus.jobStatus.getSimulationMessage().getDisplayMessage());
	getStatusMessageTextArea().setCaretPosition(0);
	getStopButton().setEnabled(false);
	getResubmitButton().setEnabled(false);
	if (simpleJobStatus.jobStatus.getSchedulerStatus().isDone()) {
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
		getResubmitButton().setEnabled(true);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:15:42 PM)
 */
public void stopSimulation() {
	smConsole.stopSimulation(simpleJobStatus.simulationMetadata.vcSimID.getOwner().getName(), simpleJobStatus.simulationMetadata.vcSimID.getSimulationKey());
}
}
