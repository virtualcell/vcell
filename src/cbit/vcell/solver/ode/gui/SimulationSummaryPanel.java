package cbit.vcell.solver.ode.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import cbit.vcell.math.*;
import cbit.vcell.solver.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/2/2001 12:17:49 PM)
 * @author: Ion Moraru
 */
public class SimulationSummaryPanel extends JPanel {
	private cbit.vcell.solver.Simulation fieldSimulation = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel11 = null;
	private JLabel ivjJLabel12 = null;
	private JLabel ivjJLabel2 = null;
	private JLabel ivjJLabel3 = null;
	private JLabel ivjJLabel4 = null;
	private JLabel ivjJLabel5 = null;
	private JLabel ivjJLabel6 = null;
	private JLabel ivjJLabel7 = null;
	private JLabel ivjJLabel8 = null;
	private JPanel ivjJPanel1 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private MathOverridesPanel ivjMathOverridesPanel1 = null;
	private JLabel ivjJLabelEndTime = null;
	private JLabel ivjJLabelGeometrySize = null;
	private JLabel ivjJLabelMesh = null;
	private JLabel ivjJLabelSolver = null;
	private JLabel ivjJLabelStartTime = null;
	private JLabel ivjJLabelTimestep = null;
	private JTextArea ivjJTextAreaDescription = null;
	private JLabel ivjJLabelSpatial = null;
	private JLabel ivjJLabel10 = null;
	private JLabel ivjJLabel9 = null;
	private JLabel ivjJLabelSensitivity = null;
	private JLabel ivjJLabel13 = null;
	private JLabel ivjJLabelOutput = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.text.Document ivjdocument1 = null;

class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.DocumentListener {
		public void changedUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == SimulationSummaryPanel.this.getdocument1()) 
				connEtoC4(e);
		};
		public void insertUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == SimulationSummaryPanel.this.getdocument1()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimulationSummaryPanel.this && (evt.getPropertyName().equals("simulation"))) 
				connEtoC1(evt);
			if (evt.getSource() == SimulationSummaryPanel.this.getJTextAreaDescription() && (evt.getPropertyName().equals("document"))) 
				connPtoP1SetTarget();
		};
		public void removeUpdate(javax.swing.event.DocumentEvent e) {
			if (e.getDocument() == SimulationSummaryPanel.this.getdocument1()) 
				connEtoC4(e);
		};
	};

/**
 * SimulationSummaryPanel constructor comment.
 */
public SimulationSummaryPanel() {
	super();
	initialize();
}


/**
 * connEtoC1:  (SimulationSummaryPanel.simulation --> SimulationSummaryPanel.newSimulation(Lcbit.vcell.solver.Simulation;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newSimulation(this.getSimulation());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (document1.document. --> SimulationSummaryPanel.updateAnnotation()V)
 * @param evt javax.swing.event.DocumentEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.DocumentEvent evt) {
	try {
		// user code begin {1}
		// user code end
		this.updateAnnotation();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (SimulationSummaryPanel.initialize() --> JTextAreaDescription.background)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getJTextAreaDescription().setBackground(this.getBackground());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (JTextAreaDescription.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getdocument1() != null)) {
				getJTextAreaDescription().setDocument(getdocument1());
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
 * connPtoP1SetTarget:  (JTextAreaDescription.document <--> document1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setdocument1(getJTextAreaDescription().getDocument());
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
 * Comment
 */
private void displayAnnotation() {
	if(cbit.util.Compare.isEqualOrNull(getJTextAreaDescription().getText(),getSimulation().getDescription())){
		return;
	}
	try {
		getJTextAreaDescription().setText(getSimulation().getDescription());
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJTextAreaDescription().setText("");
	}
}


/**
 * Comment
 */
private void displayMesh() {
    try {
        if (getSimulation()!=null && getSimulation().getMeshSpecification() != null) {
            cbit.util.ISize samplingSize =
                getSimulation().getMeshSpecification().getSamplingSize();
            String labelText = "";
            switch (getSimulation().getMathDescription().getGeometry().getDimension()) {
                case 0 :
                    {
                        labelText = "error: no mesh expected";
                        break;
                    }
                case 1 :
                    {
                        labelText = samplingSize.getX() + " elements";
                        break;
                    }
                case 2 :
                    {
                        // 06/12/2002 JMW Replaced this line...
                        //labelText = "("+samplingSize.getX()+","+samplingSize.getY()+") elements";
                        labelText = samplingSize.getX() + " x " + samplingSize.getY() + " = " +
							samplingSize.getX() * samplingSize.getY() + " elements";
                        break;
                    }
                case 3 :
                    {
                        // 06/12/2002 JMW Replaced this line...
                        //labelText = "("+samplingSize.getX()+","+samplingSize.getY()+","+samplingSize.getZ()+") elements";
                        labelText = samplingSize.getX() + " x " + samplingSize.getY() + " x " + samplingSize.getZ() + " = " +
							samplingSize.getX() * samplingSize.getY() * samplingSize.getZ() + " elements";
                        break;
                    }
            }
            getJLabelMesh().setText(labelText);
        } else {
            getJLabelMesh().setText("");
        }
    } catch (Exception exc) {
        exc.printStackTrace(System.out);
        getJLabelMesh().setText("");
    }
}


/**
 * Comment
 */
private void displayOther() {
	try {
		getJLabelSpatial().setText(getSimulation().getIsSpatial() ? "yes" : "no");
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelSpatial().setText("");
	}
	try {
		cbit.util.Extent extent = getSimulation().getMathDescription().getGeometry().getExtent();
		String labelText = "";
		switch (getSimulation().getMathDescription().getGeometry().getDimension()) {
			case 0: {
				break;
			}
			case 1: {
				labelText = extent.getX()+" microns";
				break;
			}
			case 2: {
				labelText = "("+extent.getX()+","+extent.getY()+") microns";
				break;
			}
			case 3: {
				labelText = "("+extent.getX()+","+extent.getY()+","+extent.getZ()+") microns";
				break;
			}
		}
		getJLabelGeometrySize().setText(labelText);
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelGeometrySize().setText("");
	}
}


/**
 * Comment
 */
private void displayOverrides() {
	try {
		getMathOverridesPanel1().setMathOverrides(getSimulation().getMathOverrides());
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getMathOverridesPanel1().setMathOverrides(null);
	}
}


/**
 * Comment
 */
private void displayTask() {
	try {
		getJLabelStartTime().setText(Double.toString(getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime()));
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelStartTime().setText("");
	}
	try {
		getJLabelEndTime().setText(Double.toString(getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime()));
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelEndTime().setText("");
	}
	try {
		getJLabelOutput().setText(String.valueOf(getSimulation().getSolverTaskDescription().getOutputTimeSpec().getShortDescription()));
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelOutput().setText("");
	}
	try {
		getJLabelTimestep().setText(Double.toString(getSimulation().getSolverTaskDescription().getTimeStep().getDefaultTimeStep()));
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelTimestep().setText("");
	}
	try {
		Constant param = getSimulation().getSolverTaskDescription().getSensitivityParameter();
		if (param == null) {
			getJLabelSensitivity().setText("no analysis");
		} else {
			getJLabelSensitivity().setText("analyse for parameter: " + param.getName());
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelSensitivity().setText("");
	}
	try {
		getJLabelSolver().setText(getSimulation().getSolverTaskDescription().getSolverDescription().getName());
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelSolver().setText("");
	}
}


/**
 * Return the document1 property value.
 * @return javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.text.Document getdocument1() {
	// user code begin {1}
	// user code end
	return ivjdocument1;
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
			ivjJLabel1.setText("SIMULATION SUMMARY:");
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
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel10() {
	if (ivjJLabel10 == null) {
		try {
			ivjJLabel10 = new javax.swing.JLabel();
			ivjJLabel10.setName("JLabel10");
			ivjJLabel10.setText("Sensitivity:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel10;
}


/**
 * Return the JLabel11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel11() {
	if (ivjJLabel11 == null) {
		try {
			ivjJLabel11 = new javax.swing.JLabel();
			ivjJLabel11.setName("JLabel11");
			ivjJLabel11.setText("Mesh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel11;
}


/**
 * Return the JLabel12 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel12() {
	if (ivjJLabel12 == null) {
		try {
			ivjJLabel12 = new javax.swing.JLabel();
			ivjJLabel12.setName("JLabel12");
			ivjJLabel12.setText("timestep");
			ivjJLabel12.setMaximumSize(new java.awt.Dimension(61, 14));
			ivjJLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel12.setPreferredSize(new java.awt.Dimension(61, 14));
			ivjJLabel12.setMinimumSize(new java.awt.Dimension(61, 14));
			ivjJLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel12;
}


/**
 * Return the JLabel13 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel13() {
	if (ivjJLabel13 == null) {
		try {
			ivjJLabel13 = new javax.swing.JLabel();
			ivjJLabel13.setName("JLabel13");
			ivjJLabel13.setText("output");
			ivjJLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel13;
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
			ivjJLabel2.setText("Comments:");
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
			ivjJLabel3.setText("Time:");
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
			ivjJLabel4.setText("Spatial:");
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
			ivjJLabel5.setText("Solver:");
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
			ivjJLabel6.setText("start");
			ivjJLabel6.setMaximumSize(new java.awt.Dimension(61, 14));
			ivjJLabel6.setPreferredSize(new java.awt.Dimension(61, 14));
			ivjJLabel6.setMinimumSize(new java.awt.Dimension(61, 14));
			ivjJLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
			ivjJLabel7.setText("end");
			ivjJLabel7.setMaximumSize(new java.awt.Dimension(61, 14));
			ivjJLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel7.setPreferredSize(new java.awt.Dimension(61, 14));
			ivjJLabel7.setMinimumSize(new java.awt.Dimension(61, 14));
			ivjJLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
			ivjJLabel8.setText("Geometry size:");
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
 * Return the JLabel9 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel9() {
	if (ivjJLabel9 == null) {
		try {
			ivjJLabel9 = new javax.swing.JLabel();
			ivjJLabel9.setName("JLabel9");
			ivjJLabel9.setText("Parameters with values changed from defaults:");
			ivjJLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel9;
}


/**
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelEndTime() {
	if (ivjJLabelEndTime == null) {
		try {
			ivjJLabelEndTime = new javax.swing.JLabel();
			ivjJLabelEndTime.setName("JLabelEndTime");
			ivjJLabelEndTime.setText(" ");
			ivjJLabelEndTime.setForeground(java.awt.Color.blue);
			ivjJLabelEndTime.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelEndTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelEndTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelEndTime;
}


/**
 * Return the JLabelGeometrySize property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGeometrySize() {
	if (ivjJLabelGeometrySize == null) {
		try {
			ivjJLabelGeometrySize = new javax.swing.JLabel();
			ivjJLabelGeometrySize.setName("JLabelGeometrySize");
			ivjJLabelGeometrySize.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelGeometrySize.setText(" ");
			ivjJLabelGeometrySize.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGeometrySize;
}


/**
 * Return the JLabelMesh property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMesh() {
	if (ivjJLabelMesh == null) {
		try {
			ivjJLabelMesh = new javax.swing.JLabel();
			ivjJLabelMesh.setName("JLabelMesh");
			ivjJLabelMesh.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMesh.setText(" ");
			ivjJLabelMesh.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMesh;
}


/**
 * Return the JLabelSaveEvery property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOutput() {
	if (ivjJLabelOutput == null) {
		try {
			ivjJLabelOutput = new javax.swing.JLabel();
			ivjJLabelOutput.setName("JLabelOutput");
			ivjJLabelOutput.setText(" ");
			ivjJLabelOutput.setForeground(java.awt.Color.blue);
			ivjJLabelOutput.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelOutput.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOutput;
}

/**
 * Return the JLabelSensitivity property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSensitivity() {
	if (ivjJLabelSensitivity == null) {
		try {
			ivjJLabelSensitivity = new javax.swing.JLabel();
			ivjJLabelSensitivity.setName("JLabelSensitivity");
			ivjJLabelSensitivity.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSensitivity.setText(" ");
			ivjJLabelSensitivity.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSensitivity;
}


/**
 * Return the JLabel14 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSolver() {
	if (ivjJLabelSolver == null) {
		try {
			ivjJLabelSolver = new javax.swing.JLabel();
			ivjJLabelSolver.setName("JLabelSolver");
			ivjJLabelSolver.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSolver.setText(" ");
			ivjJLabelSolver.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSolver;
}


/**
 * Return the JLabelSpatial property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSpatial() {
	if (ivjJLabelSpatial == null) {
		try {
			ivjJLabelSpatial = new javax.swing.JLabel();
			ivjJLabelSpatial.setName("JLabelSpatial");
			ivjJLabelSpatial.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSpatial.setText(" ");
			ivjJLabelSpatial.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSpatial;
}


/**
 * Return the JLabel9 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartTime() {
	if (ivjJLabelStartTime == null) {
		try {
			ivjJLabelStartTime = new javax.swing.JLabel();
			ivjJLabelStartTime.setName("JLabelStartTime");
			ivjJLabelStartTime.setText(" ");
			ivjJLabelStartTime.setForeground(java.awt.Color.blue);
			ivjJLabelStartTime.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelStartTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartTime;
}


/**
 * Return the JLabel13 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTimestep() {
	if (ivjJLabelTimestep == null) {
		try {
			ivjJLabelTimestep = new javax.swing.JLabel();
			ivjJLabelTimestep.setName("JLabelTimestep");
			ivjJLabelTimestep.setText(" ");
			ivjJLabelTimestep.setForeground(java.awt.Color.blue);
			ivjJLabelTimestep.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelTimestep.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelTimestep.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimestep;
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
			ivjJPanel1.setLayout(null);
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getJTextAreaDescription());
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
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getJTextAreaDescription() {
	if (ivjJTextAreaDescription == null) {
		try {
			ivjJTextAreaDescription = new javax.swing.JTextArea();
			ivjJTextAreaDescription.setName("JTextAreaDescription");
			ivjJTextAreaDescription.setBorder(new cbit.gui.EmptyBorderBean());
			ivjJTextAreaDescription.setForeground(java.awt.Color.blue);
			ivjJTextAreaDescription.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJTextAreaDescription.setBounds(0, 0, 160, 120);
			ivjJTextAreaDescription.setEditable(false);
			ivjJTextAreaDescription.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextAreaDescription;
}


/**
 * Return the MathOverridesPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.MathOverridesPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathOverridesPanel getMathOverridesPanel1() {
	if (ivjMathOverridesPanel1 == null) {
		try {
			ivjMathOverridesPanel1 = new cbit.vcell.solver.ode.gui.MathOverridesPanel();
			ivjMathOverridesPanel1.setName("MathOverridesPanel1");
			ivjMathOverridesPanel1.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathOverridesPanel1;
}


/**
 * Gets the simulation property (cbit.vcell.solver.Simulation) value.
 * @return The simulation property value.
 * @see #setSimulation
 */
public cbit.vcell.solver.Simulation getSimulation() {
	return fieldSimulation;
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
	this.addPropertyChangeListener(ivjEventHandler);
	getJTextAreaDescription().addPropertyChangeListener(ivjEventHandler);
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
		setName("SimulationSummaryPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(439, 411);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.gridwidth = 6;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
		constraintsJLabel2.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 3;
constraintsJLabel3.gridheight = 2;
		constraintsJLabel3.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel3(), constraintsJLabel3);

		java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
		constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 2;
		constraintsJLabel4.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel4(), constraintsJLabel4);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.gridwidth = 5;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 0.2;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
		constraintsJLabel5.gridx = 0; constraintsJLabel5.gridy = 6;
		constraintsJLabel5.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel5(), constraintsJLabel5);

		java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
		constraintsJLabel6.gridx = 1; constraintsJLabel6.gridy = 3;
		constraintsJLabel6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel6.weightx = 1.0;
		constraintsJLabel6.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel6(), constraintsJLabel6);

		java.awt.GridBagConstraints constraintsJLabel7 = new java.awt.GridBagConstraints();
		constraintsJLabel7.gridx = 2; constraintsJLabel7.gridy = 3;
		constraintsJLabel7.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel7.weightx = 1.0;
		constraintsJLabel7.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel7(), constraintsJLabel7);

		java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
		constraintsJLabelStartTime.gridx = 1; constraintsJLabelStartTime.gridy = 4;
		constraintsJLabelStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelStartTime.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelStartTime(), constraintsJLabelStartTime);

		java.awt.GridBagConstraints constraintsJLabelEndTime = new java.awt.GridBagConstraints();
		constraintsJLabelEndTime.gridx = 2; constraintsJLabelEndTime.gridy = 4;
		constraintsJLabelEndTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelEndTime.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelEndTime(), constraintsJLabelEndTime);

		java.awt.GridBagConstraints constraintsMathOverridesPanel1 = new java.awt.GridBagConstraints();
		constraintsMathOverridesPanel1.gridx = 0; constraintsMathOverridesPanel1.gridy = 10;
		constraintsMathOverridesPanel1.gridwidth = 6;
		constraintsMathOverridesPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMathOverridesPanel1.weightx = 1.0;
		constraintsMathOverridesPanel1.weighty = 1.0;
		constraintsMathOverridesPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMathOverridesPanel1(), constraintsMathOverridesPanel1);

		java.awt.GridBagConstraints constraintsJLabel12 = new java.awt.GridBagConstraints();
		constraintsJLabel12.gridx = 3; constraintsJLabel12.gridy = 3;
		constraintsJLabel12.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel12.weightx = 1.0;
		constraintsJLabel12.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel12(), constraintsJLabel12);

		java.awt.GridBagConstraints constraintsJLabelTimestep = new java.awt.GridBagConstraints();
		constraintsJLabelTimestep.gridx = 3; constraintsJLabelTimestep.gridy = 4;
		constraintsJLabelTimestep.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTimestep.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelTimestep(), constraintsJLabelTimestep);

		java.awt.GridBagConstraints constraintsJLabelSolver = new java.awt.GridBagConstraints();
		constraintsJLabelSolver.gridx = 1; constraintsJLabelSolver.gridy = 6;
		constraintsJLabelSolver.gridwidth = 5;
		constraintsJLabelSolver.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelSolver.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelSolver(), constraintsJLabelSolver);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 5; constraintsJPanel1.gridy = 3;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 10.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
		constraintsJLabel8.gridx = 0; constraintsJLabel8.gridy = 7;
		constraintsJLabel8.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel8(), constraintsJLabel8);

		java.awt.GridBagConstraints constraintsJLabel11 = new java.awt.GridBagConstraints();
		constraintsJLabel11.gridx = 0; constraintsJLabel11.gridy = 8;
		constraintsJLabel11.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel11.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel11(), constraintsJLabel11);

		java.awt.GridBagConstraints constraintsJLabelGeometrySize = new java.awt.GridBagConstraints();
		constraintsJLabelGeometrySize.gridx = 1; constraintsJLabelGeometrySize.gridy = 7;
		constraintsJLabelGeometrySize.gridwidth = 5;
		constraintsJLabelGeometrySize.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelGeometrySize.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelGeometrySize(), constraintsJLabelGeometrySize);

		java.awt.GridBagConstraints constraintsJLabelMesh = new java.awt.GridBagConstraints();
		constraintsJLabelMesh.gridx = 1; constraintsJLabelMesh.gridy = 8;
		constraintsJLabelMesh.gridwidth = 5;
		constraintsJLabelMesh.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelMesh.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelMesh(), constraintsJLabelMesh);

		java.awt.GridBagConstraints constraintsJLabelSpatial = new java.awt.GridBagConstraints();
		constraintsJLabelSpatial.gridx = 1; constraintsJLabelSpatial.gridy = 2;
		constraintsJLabelSpatial.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelSpatial(), constraintsJLabelSpatial);

		java.awt.GridBagConstraints constraintsJLabel9 = new java.awt.GridBagConstraints();
		constraintsJLabel9.gridx = 0; constraintsJLabel9.gridy = 9;
		constraintsJLabel9.gridwidth = 6;
		constraintsJLabel9.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel9.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJLabel9.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel9(), constraintsJLabel9);

		java.awt.GridBagConstraints constraintsJLabel10 = new java.awt.GridBagConstraints();
		constraintsJLabel10.gridx = 0; constraintsJLabel10.gridy = 5;
		constraintsJLabel10.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel10.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel10(), constraintsJLabel10);

		java.awt.GridBagConstraints constraintsJLabelSensitivity = new java.awt.GridBagConstraints();
		constraintsJLabelSensitivity.gridx = 1; constraintsJLabelSensitivity.gridy = 5;
		constraintsJLabelSensitivity.gridwidth = 5;
		constraintsJLabelSensitivity.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelSensitivity.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelSensitivity(), constraintsJLabelSensitivity);

		java.awt.GridBagConstraints constraintsJLabel13 = new java.awt.GridBagConstraints();
		constraintsJLabel13.gridx = 4; constraintsJLabel13.gridy = 3;
		constraintsJLabel13.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel13.weightx = 1.0;
		constraintsJLabel13.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel13(), constraintsJLabel13);

		java.awt.GridBagConstraints constraintsJLabelOutput = new java.awt.GridBagConstraints();
		constraintsJLabelOutput.gridx = 4; constraintsJLabelOutput.gridy = 4;
		constraintsJLabelOutput.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelOutput.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelOutput(), constraintsJLabelOutput);
		initConnections();
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
		SimulationSummaryPanel aSimulationSummaryPanel;
		aSimulationSummaryPanel = new SimulationSummaryPanel();
		frame.setContentPane(aSimulationSummaryPanel);
		frame.setSize(aSimulationSummaryPanel.getSize());
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
 * Comment
 */
public void newSimulation(cbit.vcell.solver.Simulation simulation) {
	refreshDisplay();
	if (simulation==null){
		getJTextAreaDescription().setBackground(getBackground());
		getJTextAreaDescription().setEditable(false);
		return;
	}else{
		getJTextAreaDescription().setBackground(java.awt.Color.white);
		getJTextAreaDescription().setEditable(true);
	}
	// also set up a listener that will refresh when simulation is edited in place
	PropertyChangeListener listener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			// name is not displayed by this panel so we only need to take care of the rest
			if (event.getPropertyName().equals("description")) {
				displayAnnotation();
			}
			if (event.getPropertyName().equals("solverTaskDescription")) {
				displayTask();
			}
			if (event.getPropertyName().equals("meshSpecification")) {
				displayMesh();
			}
			if (event.getPropertyName().equals("mathOverrides")) {
				displayOverrides();
			}
			// lots can happen here, so just do it all
			if (event.getPropertyName().equals("mathDescription")) {
				refreshDisplay();
			}
		}
	};
	simulation.addPropertyChangeListener(listener);
}


/**
 * Comment
 */
private void refreshDisplay() {
	if (getSimulation() == null){
		getJTextAreaDescription().setText("");
		getJLabelSpatial().setText("");
		getJLabelStartTime().setText("");
		getJLabelEndTime().setText("");
		getJLabelTimestep().setText("");
		getJLabelOutput().setText("");
		getJLabelSensitivity().setText("");
		getJLabelSolver().setText("");
		getJLabelGeometrySize().setText("");
		getJLabelMesh().setText("");
		getMathOverridesPanel1().setMathOverrides(null);
	} else {
		displayAnnotation();
		displayTask();
		displayMesh();
		displayOverrides();
		displayOther();
	}
}


/**
 * Set the document1 to a new value.
 * @param newValue javax.swing.text.Document
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdocument1(javax.swing.text.Document newValue) {
	if (ivjdocument1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdocument1 != null) {
				ivjdocument1.removeDocumentListener(ivjEventHandler);
			}
			ivjdocument1 = newValue;

			/* Listen for events from the new object */
			if (ivjdocument1 != null) {
				ivjdocument1.addDocumentListener(ivjEventHandler);
			}
			connPtoP1SetSource();
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
 * Sets the simulation property (cbit.vcell.solver.Simulation) value.
 * @param simulation The new value for the property.
 * @see #getSimulation
 */
public void setSimulation(cbit.vcell.solver.Simulation simulation) {
	cbit.vcell.solver.Simulation oldValue = fieldSimulation;
	fieldSimulation = simulation;
	firePropertyChange("simulation", oldValue, simulation);
}


/**
 * Comment
 */
private void updateAnnotation() {
	try {
		//int caretPosition = getJTextAreaDescription().getCaretPosition();
		String text = getJTextAreaDescription().getText();
		getSimulation().setDescription(text);
		//getJTextAreaDescription().setCaretPosition(caretPosition);
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		getJTextAreaDescription().setText(getSimulation().getDescription());
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8BF4D55519F0407098C50D088E35B8ABAAF630C3B56D60081D2C797337F69547B8E286ABED31667F0BED7C0D8E5654218E5543CDC2EEA0C000003102C6C4891204BCAE90C21004BCC8A0C28A81C288121B5CF39F49398F6EBDF9C1007F5BFB1FEF1FF36FB9B739F11535DA161F671CEFFF6F7D6D675967C6487BB8B6F325A9CF90D6E68A4A3F7DAB8521732AA03C775316BFA22EF2E7D23620797789
	4086611D3E8C38B6C1D97C83C95939422F4CE6F86E04674EFB12325F077BCD42F0D31A9BEE4268C904325D8F7D4CBB3A1EFB0BD0CF96FD3E9E48016BBA00AE40116B5C1DC47F3D01FC859F2D6091D61A84A1261050FCBF30D541AD839AB360DAG3A8A997D90DC1981132AAA953A4E3B6688E97F6EB72DA5F2B4B29196AB3CAB14EB01704E4A593B082EDFD0BB890F851EA5G62FC3A3045338F2E552DFD0EAFFB3ADDF249C00FA8C9A5812FB4A07ACB3CB63144516F2A2ABA6DF277CBD65965751C6EF73B2D7E61
	D62BC714C07E97C476F659CCE589C28A481DCBF1433DC41F8D6EC7GE6FC0178D77A88FE995C1BF7A7E5976CC69F8F6D2CD6FCE83F7A0430285C1D31BE0C0F153B19EFDD3BD59F93FBDFB2FDC06B63E745A46E3320AC81E09140F2007C22246CCA008FE99E3CD47485DCEB3B65BA2F5B6D75547AFCC7A41B7FAB70D6027B2AAAA8D1F027449E2F5FA6882C2E1383C586760C860C6F0DF17331184C2940FA1FFB6AD1E149BF5FEB4AB2084912B9D7CD2DC5AC964EA2B5A6CC77CEB91C6E29C541F9992C7B1C20553D
	60728B6919863A17FE7DC27AACC5CE1CC67752A2D2572F539CD88877040F70B37CF20AAFACD671ACC7EFD0BCF92E063258E24C0D76F5E8CBCB4603C2EE595F9B46E1660F2619B2CA188DDBCBC2EDB9AF9159CDA5CC679AEE0B03621D9ABC3365B51A2764F9AA142582307CFAF98FE9332DFB208D81C4834C820887D88C103A87F36C7362BD9166D82B55AF9F69F7C9B651AFB05E170B3740D59C127D56E6CFC036FAFA4433D63F4B5AAD0981C121C9A9A6B1688E62FB1D66FE8F68B8AAFAC4BFED2FA05D6A96E551
	9FE0B4EF97EFA0B4FE111636395CA2A8F07B84147BEE71A638CA5600FC46E7334A22CA414AD71510BA99301E8509A0G6ED9DCF24AC83CF2CA1332F7GD8CAB18E93CA083C63229F7A0B2A2A962F372F5F57C6BC9122CBC91C3F24FDC7935CF715E20C4D658437G1E13391C79A59BB5F26ADDBD44FC6808300D3D55CBF85200BE3F8C73660DF64C1B7A355F930ADE3847B0EF5AAF3D10DEDA46722529AC347D3CCB65925BFCE55867CF6AED0F208D67178537AFE2DB64FD46BFC424A3B364B9BD821EF53C4E86AA
	C81D45001DF381924BB056EF1594305CBDE28D08182DA77D10E822CD0DBC337312848329703EA2B3CBE9B3EB399FEEDA3E94E46714C3DEGF482C417E3AC5F6C8B75B3CB39221CD05BD1D76431DCAD0553112D688965CBB708EDB8E0B1FBF58F992F448151A3570BBE51E3031B00D236EC8F512334E73B17733CCD71BEE87FF55060829C3FE28FE923D6D9763B3A7BE57134A84BAE0FC3AD4FA8A53A827D5DBE356BC0DDEBCA095D5EC1E87F1CBE3714341D9E2F47A3524A63ACCA79C65AF6C5C9F40351F55EFE0F
	4C7079E5E42E53A78EABFD475632D582E97BD2BF6907587CA14ECA6267733B582C40AF8BAB4AC93BBEA367EBE2B41754B94CF19642F5E9B9364F41CF31FD6E38720810F049E7B29AE3E6BEFC27293E02351D768A35FDB21B56169B5B3E0976A18ABE40EC757512FEB55CDCA61D725DFB1458E67376749346B25ED69E3390BDC320FF9A406C8AECABF71613F26C1714C6D26715A496EA127B95A49EC77A7294FF6261F9D1850B071ABFA3953924CE454B7DA2F4772403BC4E63FECB4546103233CA9D30725BAAC89D
	C81A3EF5260568FCD04279E51105692BD72E1D96263BE5AF216BF4A35DD225FC85DC73204C321751BDC569CABCC8B7F3AFEB4FF1FBD93FCD68CA083CFD046E7FF23A5CFDCCCE61BE56FFD64375A342CF6D1B6B6572147238FDCC7F02FDCCAF2B3737F611DC22F354DE6B00F528A4B0886D212A6A982DB9B6877DDFF49CA174A97BF02E795AA02933027DB07783E85D0FF9777CDA1CEB4E681BA34C76AFCA8FB71FF66EE7F6CC5B2F66DDD157E0BA4B09BF6CA5BA9321EC49FE4C07D5966DF8D79B88F8FBDCB45914
	3AFA5C224DCB067B21E5B5DC6DAEFFC0EE060EE3886BB541C26CA5E33102D666A6AEBAB7A1793A8C7496D6C29DD4927CF7689A08441A7959D301FE46DCF9D8683A67BE531AB0FE4E2ED46A39D275B377D716F4266F998967E5AB205CF2A0A91B3515F2B731BB405B9356579BA5A497BC62205A548ADB742B8B3574602E33B86FFCF0D30DE2D362238F8A55B73EB6992D83262EF71A24834C569987D41B931EFF4934880059BD490DF1CA861A7CAA34BB1562DD9E176C32CA2EABE2612E38AAC2FF3CACCF232395F0
	C3G33B85F968F71375B5A536770C3CFE3C31B3F771156E633F512214D53DEF51816D7A9369ED4EDAECCFA49D41D04B61F73204DCE2009BD086B343CAF4915763D826A9DB90FFA23926789791BEEB754CBE03122CF28D675263D7C12491C0CFA57F8D1EFAB50CC2DC61F637BC83EE2FF287411DE12E7325395E0F67C2ED66B7F7C2CE906F6B87FF126A9C1511F2231E3AE5810406DF8410B39D6F9887A04C3E8C735CF5BAF178818EB87ABC8FB6B27F355DA0F47AB33F40304E2E37FD33A754F1CC36ABCAC7E10FE
	CE465685C707695A6590DB97D4F03C0B62EB3ED4714C6EB7E9DFC31E63202C258667328BAAC95F15D21B141DF1A4A9FBAB0085208DC0BE02FDC653DE42B76ADA215983D3F80FD5BA9DB468631CFDAF09910C534A33E91FB6D7D89C3CFDA48EB1C7A21B032AF327EB14EF4EB878D8CE6C6A506ED9E4ECB8E6CA8F171B0A6C14A3EACE30588DD71176D8D8876396C0A7008CB0238E63B6E5DF51D8F143F51392B0665373D2E8CE4C2B8B76EFE148B363FBD94777D148F3DA889F73633903E41C4B29073E9A20862093
	A08AE096C0C2BD7A33E85F21317CE9A0934CB6EF9D601AE03C2F8B1D53F1DBEFF30F3567E06C63374664CB2E8F66E36D7059EB448703B13A8D663D35305CD466058F193573426575EC7E30238146E8GB616CFACA3FD8E9913DB9BF0AC3B56289D336357DF8BBB164DEBE076ADEA500CE5A75ED6466C6907C8DD64B5A6E51782B48158G26824C86D85008F571597E039156C5AD0D3F3EA6D8BCAFFA3ED9BD740D491752E814EB9B8F937F04235057GD8GDA81E4009800F9C7513F417DC746720FEF9D000F3896
	B1C8B8E66F866FB7737363B179920F9A79B925067839944A04A6985B81ACG6DGB2404CA6747319317D644EC56267AFF576C656F73C2A63AB89F90EEB8A66E37BA33F24735A85D036840061668F901C508CFEB763BA7A2BE19BE9FF705C8AE08398821886B09BE0BEC0A2C0B2408A42F78C5A9BC08B0093A08AE086409C00F800A5GE9G394721ED82D4823483C847D11F176A0B066759GCF81ACBD4E66CB99AD4083D08FE0830886188D30GA085407C957403GADG9240B40039G09G29GB9A712320BG
	DAG2493286F3F87E32031234D440EF079922F582D456D88F94ED77C8DA7E30E260C44F3347CDC27449D6FED9150FFD3A0F512E8A05F99628B565734905A6488ECDB22481A2D6490390F8A11BFDF49A3D6B7DF8F113AA16D1234390AD1E4FF8630BB84D79C72CC76F7BE99C546271A7BC876E834FBE2D99150FFD358C5E32147EF8BF9A67B42E4DFA1F45FE9D384BAF6AAD7B2B6E6AB779F0750103977E68157104FD7065F17DADCE93CAF752BCA63FD2925159B989EE4E8F72D31BC35F274FD2B34CA23FD2BED34
	0EFEE6A273E7FF3FC7F639C52A164DAB44A1128B50CB1F6A7730AE1CE88E044E6F849C97FED2CB56A653CEE07FFF6B01F2C15F7FB758DC32570FC25AC42B7F245D4EBB7F53BE3187ED1ED095BEFED32B0C63F7C715F17CE6D40D9E3FD9D5616247E27474167514BF909083F472D1EF152DCA59EFE5D2673ABD3E93B0028B3837E5F6E7ABB48D32372E0C6B7D794170BEA69FB47671B787C98E863C7D7E9EB1540F578F6673B2F5A0ED16C5B7B1C7597B247C32556F90F5F178A365E7E56178D7D5070B957375C7DA
	DF3FA23E32FCF2B410F8994CD3CF77783D12C4F61484FC2FD93B9A5FEB4EB901FBCAD6B34953E4F8DE8E10FB927B40A9E55ABD6642137AFE365AGC720B584EF0367A100E99ABC4B6DF70F1075D0AC606381968124826434E22EFF37FA4CF5E44350A86B48D907426741C3070C736011C36173A06E5058F970642170F910F0E86CBCF87A5068F9B04333C64903D63B5F6BEEADD3E4EFBD9C5E57CF8F9B7B3A6BF0F8DFCB8F0F6D6B014361FD2DB9BC362F4D0709AF5DAE1B4B2F6CAEB39F1B3C186B2D3237D5ED57
	F7550477F1E60D310F7FD0935E4707EB467671511A70BEBEDEB3360F7FD6E36C63C61F366F32707A7D1DCC5EC9C856E1EF3FAC88B3DBC99B4D6E47B5E691BC5783C8GD12DEC2D497A016F8F92BDF6D861C03A508EDF7EB361FDEEGF91301BE8DA08F70960036BF63DE5429CFC91BBF56E26D9625B2450ECD17D5DC39024BBF2E62AA94DC7F000A33A83862FC5227E8FA20B206CFB42934FB955AB567D45CBE8577799A9537DF41DDDC3D1E63CEC348E572A2D7A9EB4DD84B4B9ABCB6DA02717CCD811907FCE515
	1DA787C43F5FE59383F4179D7D7CCE36D9753D1C613E38300E63587BE2510772FECF77B395BB683E2FD2EF2B294DC132579CD67D28D4F0D57D9AFDCADC1ABF5C48F1C7C5AF2CD17D4327DDD7C454F96765553C7CABB160C47C165D2A5F27FDD6327B4B7D7E2DA65687941C7C01C6F7A943CD9F5E204A90BD81176C9AF04943DC4EE31D9A1E8A066BA8D3E3F032DF76754BEC3FF97E30F63FB97E4FACBF13614A4A6FB9EFD41E464B0B2E9B151BCFE17902D5C665DBF9F9DE27D1F9A9AFBFE7B3AA2F67650F580D4A
	5BF9798FC423F20917BFE7D89E8565EC2F68BFFA34FBC5B3CE3172C5F0CD61B2F6985AA01C4672F506BA72F8F901E4D4DEF89AED886C8EFD0FC94A2B213C154B4849B412616465BF6AB3AA9F61652BFA0D4AE3F8F95E9E2372593C7C11FDC66573F9F9344528BC1117276EB44CC3DE1EF643B08FF939E898E3F39B16DFF39866E19B4EDB1E2EA56D120F614A1C154D2B9AD70F36BFD45A96EE7F7E85BACF2BEFE37B74699C7FB24537E970EC7F5EE146779FB6A8DB5006FBAD0F503D4822B3C959B5E790D7D8C15F
	0940738847EDBBC6F0B1709C4BF1A7BAF01FE69E47F9AF905CE2F8CE6538A7A9CEB88BF9F9163FC72C8BBFF62F2DB39E3BB755992FBBBE289BFD5D71C9DD38B9940BCDD9BF1E9DAC847B3A4E6298DA56C5766078DC3A44BAA8D7D59D05B163085551C2C7E4B60F9E26FDB8611FF99667517FC8E5AE046794G619C6E738EFD0E7B3CF33F7320B053E37CEEB6396D1AA97F9C2B7B6AF36ABEEFF26FCBA62673778328F3884A634E915C27035ABCA4F53DBF1D6D433172B3C165A4A6F7895A72A9C16544DEEF907F13
	C165A4C6AFA97C4C37E7BEC35FD6BC93A7C80FC707BD4739DC71E9EBBB3B46AC7D564A14472E24AB6F436DE15E07539106797FDF4378BE3C2D9D7755934FA3EE9A4775DCC75CD39C775EAA442D6038631D08331CC75C9DB65C2717B8AE59CEF0A4F797F25C2BA252D9BA90374A0E38199D18CFEF2EAE9746BE0B2A1F1B60FAED150D5B5D01F9F6D9A4F9E63910145D82E03B007AAFF0BBC7B82E015B194CF167A433D2FF751D087B275D28E72493751C4FA0FA6641F3A240D2CEDB1749ED6238BF7471D8DEC45C21
	DE54A3F15C71BD081B4DF1497B903704637E151EA9A07DE55E57087B3490F534F05CEE81F923B9EE160838F80E1B67A0B8B2BFCE7B9A7B280B2B31AFAB3A04EB5EDF1A49DE4B88FFFE05BE13739E4A59AFF3A4E758468BC45FBA81470749B7C5C73A668A6BD0FA6E295B6B271DA6DBE33CD8067302D9E04FBC00E40034CB6C3C8B63BBDCC14FF5F87DA21E97286C22B9C5470F9C384F6FC2DA4B318CCA6B11994DB1BA1E0C407DD4G36B691BBD067FC40A582248264D821CE2CA8E7B29DCBC41BCBA65BE2CC567D
	9730AFEC61F48BAF1078099EC2E5D36C7A6D855C6F6832E29D6E6F7FE6E71E9B3EA15F75F17331F1FDF80034BF1F150D6BABB9FEA2450FE870ECCC7B9E9F5326C2D92A955F57AF2DA7B1EE6D06F9AAC0B440AC00F800455DB8B6BFD1D7AA0C314F526675354060ADE906DB665FBB835F6CAC787F4E4231E35E8F3FAB4C1FDC9F767DE7EAB7EB9FB9BD6CDA786BEF2DA4D7665FE0A39DBB202C9DE084E086409C00049E746F3DBA653DBA59A62D15E5EB0F131ED6621B2EAEF1D0C51345B373ADED584837C50A9DA9
	BD7A391A72BE7E3C915F8A05BE57960E4FF95D08EF07C2DF9916AFA34B082FC5216F8A4B575AE944A7AB74D343725DEDB36A23E6A974F186FC841EB240AF86DCAA00A02AE5CA5FEBB75211275095AA57EAB1D8E62B2857E1835C9040B41D0EB7C4A39D319A3AF9F03F40C06612905CB265B9439E2AE30D219F799A3AA2382F316BF53405601C4A7308DE07ED34B698837433C3E4313E6495BA1718EF8F6DE35E2078276C21FD4C2008FD4CC2A833B830CF6DB274F126C379FEC839A6B802EDD864507B1C823895G
	394ED09F0F9956558E8DDDA55C37B875B23BC2F03272BCD52723B368BC4CAC25BCCE39BE855745A13298DF5C6E507827EA6884D738B55D33F40E15678A0D7F4FA83E40959A7FCB99987FA2A80BF1E17C1BB20D6ABFCD51EB6E8D570E5F6DB3625B2A50170665BB522B652BD7685A152B845728DE23B8356E51724DD0E866A857F838AEB464FBE51F16EF29C233DC39667441BC2F4F08AF41E2641FC521EDD22E1DFD21F9D0D8E84467D368227B424525D450724DD46866AA578438AEB234738151C8DF0AC23BC239
	66CAC959BBA46DDC71C90791DF25C2538257AEG99E02A02DBAA11BC2AA82B479C17743E24AB30C63946814DD3G1B156745121A17EC6E756D83E41D48CFC6550B019E3F4BC7B63BD95E3ED706FB91C5EED83338F1FD34E69DB9EF262E0F68F228416D13070F50396991516A41355051F21C734DF2639A251F1E15DF844F29G991E246C2D9E720D236B0440ED5BFC0C6C7F62F23E244EABF97DA55D64A4A11B07448EEA4F75D52E1EE8F8E61F76A39E96ABD9392E58FC5FCA266F56B6D55F110E6D82BDABDC40F5
	B642A438F03B4DE5153C0E6DC58525C585E5653B502F5BBA502F399EDC57BF59913A2EFFD3502E4BDF6B88DD572F8CAA5FF6A1F4DDDF3BD2DBFE72C2683A9E79198F73AE901C5DAA8554FD74F7AF50B3327EFE11655E0D81016761D44D7DBF775F0C75CBF0FBC93C29BA14F73E7561772B526A0D772B5E29B75E2F929AC6BFC7E0EE887F7D4B7A866D77AFEC4E5BF106FE03C4F24F8BFD82C0A5C00B97673B77B7109CE4A741C2673A8DB687F957C53E4213D1FFD4E3F8DF27B40E7D6DDECCA3BDC76E15E52F1B48
	C77C3D0D243EE42F0FA2996E5B0DA4C6FE1743A9B3D2857FC0A3E98712E827E836C6DFF305601C60571097576D1FDF45F5FBB447251CC73AC59C574C77AA13B96E46F524AB75A1AE79BA52D5F35C07D9C8B70D63C63210EEA647B9BA11AE056396F4A2DD9A47BDEEC33A7A4B085BC277A9DA615949F1D7DCF40D8C4FB3B96E07F614B71763FA1C8437821E737C082B23F405705C44F1D98E545B49F1CE87574BF18BA9EE841EE3B96EB3A75AB20F63DC5C16859C773A83ED498FA0EED0545B12EBC7DB0AB85DE5BB
	6EBBD7F25CB3F63DADE9F65C0BEE61F41B335116AE0E1BE12037186B0D63F4F7F03D719C17E220F7038375AE64F47FE2C63DCBB96E6E9E3D5E6DB62433485CE6EADFB4BC4F14F16E769AE982CA3F1626393FE6B85F7D4B7623775ED49D4C6709BC7EF3F99CFEED479C1C4FF1BF6D5147FFD98F66E0A2277B9F9E57E4D5DE8F66E0E9BF627E53262F93C9646D1053BDABE2AE34F25CCE03DCE865B9E863F453B35116A10EFB25CF2FB7A0A16F82CE473E1BA236AC6138ADFBD1DE8A47E56D66EDE40047EAB71E21ED
	61B8F3B1727A86B0377EB3C34D27A51AFBB163666756751B2A0379FCA993E318406310406BA41163B6CBC837F590F183925295F25C79DE2C27A10E1BCC6BE99ABC4F623857AC48BB07636614E0BDAD67B8479E1417B304384347D0DE8147459E63FB519CF7E89FFFA747F1AB76213C980EEB6372E6F35C63DCDE9C4765DBD01EB00C3897A9AE8F1EF7F0DCB54F41D20E1B3C13602260B916638E97A26FBC0E0BADC45E840EBBAA204D39D79077C096574BF1A65E2F16F25C9FECE8F394474D276DF586BC4F6638ED
	FCDC0A63389FB15CD50873D55E3EE8FFEE0167F60EE3E39F11A7F1DC191337EB0E7BB6553BG1E13B9EEF32FDE47173DDA9DEC0EEFAB43F797E900A367DB491A047252FF4B8EEBF177FD0B7CFB7AB0DBDB3CFE16D030354A7F4941BD532D7F7410D07FE23D091C9DB6DA2B940DA8FBC5A36A3B44C4F7C1BAEB1B1965E36D594E9BB1DA27EE9A13AF747BA14657B2A6DFA830FD10D7697AEE61C8687E486FA8FE49C8687E4855F25C9FC9013216EB788E76398B5AB5DE6EFD128979C27546DDD376A22E05FE3B75F9
	BB291BE5004F390E736CC7CF10335FE72D3D7AD33800EC75FBFD22DF9EE6D87A0533920F180E714603696BBE93FE1E6EBAB376BC5DFF063DCBF108ED43BE65FC5A609972AE48EB3787C43E9E98B9134BF14D01D6AFB994647530F56076ABF49D880AD8FE7E7DF992E1362FF068AA6EAB94DD576FABE04C497BC8A22B86E88810FD857ADE773ADA1FCC76FB9D2A6C2E8E145D95C6F63A86D07607CA192C133D3DD3157D5F57D1F6D49859D9027A9B8ADA5984BFC3A73B6122AA7B6EAC149D9BC6F666A8F64F5349F6
	FE2D4AFE3993E5A704113DF694590BF432EF3BA488385EAF96497E0F5BBA64F2773B69E1B476CE0D3DD3CE865E546B786E6DAE975163730BF6516F97ED6C681AC27FDD3A26493F81FDBDG337BF9BB5A5DFA43F87FA9CBE3376DC6F01E8C5D885DFF3A6224363ABCA136CE277DF8B450475CC0DBBB6C241FF5FA7D2EABDE0FEC15EAA51743C3D6488A0FEC477D18789B381F74E817407D390B0E9729D0361C4B7C2DE3CB10CC3291062D8679CCCE143BA38A66F294F702625A6039AB8AE39345E3B3ADCA9F1B4C10
	5844C68547E6DE149A9B26E38B5511G7844A874E7C3376A4FFDF6FCF73B1C5B350D7A18BF816AEC825AD5140DF635CDB02E33EC03DCC33B1C93C2F34D52235A708437C11E00B6D431F1991EE3398D4F1A51060586B6E40EE143D20D8DCC5E819B4ADBE6A00F50ED5634210C0930461D286E0396CD8C4D43A2D17F2E3ACE44FA2F1C0871BFE7D3FD4F21FE36C0D97BC4747D83BA6F0906671809687BC691ED0D1FE83C7F2C4D0B0593C3FC1F981A97B9D447B2402F60F651E62B143FADE21D945E02F68D53FAEA02
	6736DB502EC7B2512E68DB0C6D9A2DCEE65E92EA57ACBAAF1F8D7838DB502E7B6D822F33623D28AF59C0DF3AD2DFEB4268CB5369AB5D0DFE8A5130AE27E7A11671B31285002B0CD65EEBC4336F7C189D9F97F33F2375F62C89290F19C0B3B7DA4D1D046850761060D0FDFC1147B451C0B661D9EF90D36C5713F532FF6DD4E55FAE216C54B032B70CA2DB18949A3F09F43E1B83787CC9589F5EEF891EA32C6AD5751BF87DB5CDB22E3F751A7A6B849A82D8FFBE1D7E67CB307EC6A6E11E4650F557ACF81EB389F3E8
	D71FEA439347500645E1EC58B00A8D29BA9B6C7BF89F4AED78ECBF6DC3A7C39FBA99EDE811D49B92398DED130DED504E8B34B6903D4E4921B654DAF89FBA996B610D10FA703AD57D27F9BD4CB0503FD2E06B916C7B66G8D018F943B62A70776FD776F9CFDDCFDE2A776038BF8BC26FBB4F5D20876AC8FE38F5607569E4C4B1CA92171705135D4BE60F7CCC1FD0FFBD5FD9F703124ED0AF17C57E8F422BEBE1E6974ED677D25BC85637F1C989C7F45BED53F050F73B38C7413BED35BFF4C811A78A92A7E05D3C263
	7F73B166B5EF71F94D92EEDFF1847316A6877265CC45B87E4FE5550FA6A77AD1BD55588FED7F9F9F62C76B54D0BFFEA09A1FB17BF79A5FCE20F7CE457DADD719F69DB59570338CEC5082DB4B3D6AA7B64781EDA240B2G735F01EFG9600A600CEG9F40D400D800F9G8BG16G24828837A6E5675D0A6B4072F364DB59F06BC065GB1FD6962373A48EB97659B4704CD24FD39BC124BA35631DFB994F04D7CDF646828F23FDCF3BF217C666F5D5DF7D3F530BAB8C942202CB7074EFD96AEFE355D72B16B0075E4
	F72F58A337D99D1CE7F7BB11455F29316535D2D65E1EA7E8FE8F097DB69A90FA9CECBF46F39577E38AEFB55A0F41F17331FEF24FF86E6AC01E2EDB319DA48D286505B730FCA6AF7F7D20DA7ED49616AF6465C543EAF96EC5ACB75F0665A72F2836AC3CAA70B662F511FD1620F74C75AEB779F8476BA95CD7D6D4D0E65905FCEF0D28FC0F59A361E376189CE8CF4DED46FBD6CC7E166B2A0D87EE283AAAA35245E8EF3313EF22B5C73FCBCE137F63EB4AC0C9DD43093606D3DC8FDD3AAB3CBFF1C42E6783677874E4
	18D4BDDF0FA3F65F37E36C06C60D5D4EF4B5F674382C22CB92A377694776F0679B185C1733D539595963BB1BF0B19B7D0863B9C90FE9AA721247507D75DA15763B667169FE570C3AD3386E896B5476524B4F4697CC9BFBCFB5B86E33572BF6F5085A338E2C7C2739EAF9FCCF64ED6D7FEDD0791659A24F9736C6A4F63407710549DF3BC94D1752BCD5570208F4B15A0F472833775FD765CE4E9CDF1D7DB18F7D081906F5D6152F5AFC6F1FD4590FD846A77B6B3DA8FB81177D64072A3C733B4379457606730BF0EF
	D8389D6668376399A447F6B27630B3C8DEF7092B5BDD52BF60AE31F64BA5E4A1DFD5F522DF12DA144FF185F66E688C15658399D381E2EF47FD7E56E234F17E6DC6753823A67CD833E42B1A5BE67E3D4A325B5157773629657FA671716B8EAC5F7E31AA6B7AA7EADC2E0CB34E95BC4EDDF70CB6B6BE30DD5571CE4968B97549E7AAED4531715913F20C0F2B5C577A4FD5F9F76E9BDDF7EC21CA5BB4CE5DFF643A97F25DBF5E254A6B3504534D7823F9AC4DF70E964B5FFC214A7C0F1D110ED1CAFC78F7B1B5F7220D
	59C52A6C5EBD2A6CFB3246677FB15E878FF15937172A720E0B237BAF72F1B04E50FF23BE1A67F0392AE735A35218B07ACB9153AB5F73B979D8437DFC20C235A5EB2FEA4B5A5EF0BE3377FFD174DDDFC18CDB57B05CA40A2BD4F04AEF0278433F4B49880C7DAEE7ED40784C5526007E4CD5FE40784C55C781E2971E39D2FEAB027ECE0D8356AC6D1C2E10634EF3DCB115C9F067DCB65949719554AE02EF9209C2449F20F68DD2DC3B027BD203BB2F606A8264FD94388C4BD264ED8A50EF09DC12441EDBA80D4B9390
	E59E0FBFD39BDDBE2B466E339CC76CE6EB1B40D56D1A318D6A4499B37A1AD1B9AB93744EEE84F8E2A262AB3E21651B8DBC73A362FBBA681B0EC460C90E08EF73C5ADDF9A70186F0A04AFAB68AC71D660A90D086FDF6CC1E7410127BDA23E6EECAD1F84BCD191712587714DG1EB99171F9033E1108871E059171B5851D4DDE8ABC4BA362B3ED567265CC07B94274C878FA76EA79AC405394915FC7C55F6C7B3422F17331F770CF53B9D0677450F773C9946F1C9E7AEE7E821FFF49D036F8BA6E611CA9566A2F3EBB
	A93B756EC87CDD3BCA4BE7831E210878FE9C740D47B460090D082FB228DE66814F020878DEBF2665DB82BC4BA262B39D5772E54CC84A4E1F91895F5F8559D984BCB591713D9E744DC39B70B8A362BBE98E6A9701A7A6A23E5E20FEE3B6704C0F086F3AB328DF841E64B17858BC5F7A3966B73269104570DB07562AF89C3214F17B562B7A317C4E2B5A311C613E5721277BC1071E2E7E061E2E79061EAEB9CBCF77CA161E2E68221E2E6C221E6EA3071E6ED3071E6ED93B1EAE492E279B4E5653DD4F56537D49006E
	E3833A49B6245B150E385BECC83756444F7B09FAF9B6D1AF6F034DFA3A0FB66B692E6C55535D582B25E3B9779C1DCF337B451AFBEB514D5F0B3DF2D3F5A867E20B75F138DC2C0F570ED5FA3ADD2B74F43F1074F4AFCBFA3A138671EF5B2B274BB82E274BBEEED06F86F49F9950459A68FD58C86FAE833D3B74F4F6331E2E4F2C271BE0503EA69B342FFB1CFA3A7B1CDABA76775FE6BBCEC127B677F5A1B3609756852FC9D6BE7C7EAD39F7D6741B84F323304AEE15FCCEEB3310C9FFC43DC5D853CDD783229FE8E5
	A13705706E815EBD1477E00BE06A89484207AA3E0760D969E91F35C78412EC1B555FC7A5DC962E8BABAF8859F63F231BAA10855305468B2016BE11627496E1356CA4DF1E1057E132703EAA3A153C93D18A8E8E51D7E46F41FFBE7A8F9CDB9B34D87D4983FBFF13782B3301F4E1D533708179FD0153221F7D59839B7979C8656FB8B58A59BEAF48A527011AE1C1C5AF1981F9D8826F736C27485F9E8948220D7FC6A6383B195954AF3B24BDCD5600131E62GC7B3BC64E723050D2CD832FA9CFB585B84E66D4A5A9A
	E233C04697816C5DC46DA5D2CA3814CD7F3A7E33CF0EAD1D1FAE189A05551257EAEB347648DEFF3310ADBB7DE2406915ECC33E43CC10F910D4B5FD758862BE646E93E7CB68C7F6E8F45F507D5356262CED049EF248AF6C2114A40CA51C42D667347AC11568EF1649DF50727AF77032899F491FBFFB747DCE1330D2F1125414F948A71304F13B24E1382927DB8381D212A30335FE2BA7E0777A5DE0D9C108E5D84495145C7DF469BD0F3C53C195282C327031DA7B8E516B5ED3EB373BBCA297GF185715B38F8C2D3
	92C233F1682E4D47DEF82F9B7220DEC8F71716527F17517F17137F17D1CC99451415C3F01789237E8BAD9FC21FE97200BD6B29BD247ACB9A218665DF3C7E74033F2CDE168A5EB5AA8D8BAAA1D725BA516F6E967D13675E33ED4D0C645BC89D79C82A8C913939C3DCA27130192EE51387AA2A52FF7DD03AC948E896D63135BDA4AFD920C37252965BAC24435A1E66DD0696E4E15369662F4E345436B51FBCD1F07A4CD7DF551EBADF8595C6FE5201ECAD40FDA679ADBB381A155F0A035BAC7693F624B0C0FED4CF96
	52C50FCD96D696B0B04B4A4F5F2928F5788BF585A4AE04137CA4DD15CAC020D87DB3ED8503AE59D9407E48D2C10F9382A35A8A48CFD79658C43B35DF222665E8FEFACE91354ACBFFD90EBC501A58966A72CD437A050D7AFD745ABBFE7E625CCB75F9A64184F56394D9E5E48E1219A42D8EB21C9E22922B021E0C35D60D42B1FE7ED53AEE08E0D76B7B9302DE47DBB5F95AC0C9B7517FEF99F79BA1DC791477CFBC359B06C6317B2F4A521651AC9DFF3D7D4D55D47B5FCCCDDD9AFFCD0D877BD7652B6BEF26D6FCFF
	B3168E7E2572672F4A2B283F14D7741FBA6966FF736806763B196513FA427EB60270A05B0349FBB0743B19B7693A2F95705203F8DE2AD6A2FFA3A15CF91F53EE2BA449D6292F56E3A3E7FF0E8C4B227A2D7C5B92119FC09A25D0F927A2913BFBB5BC7F9FD0CB878865E2471D3AA5GG04FBGGD0CB818294G94G88G88GF0FBB0B665E2471D3AA5GG04FBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG74A5GGGG
**end of data**/
}
}