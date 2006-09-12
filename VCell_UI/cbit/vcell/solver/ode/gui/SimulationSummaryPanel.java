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

class IvjEventHandler implements java.awt.event.KeyListener, java.beans.PropertyChangeListener {
		public void keyPressed(java.awt.event.KeyEvent e) {};
		public void keyReleased(java.awt.event.KeyEvent e) {};
		public void keyTyped(java.awt.event.KeyEvent e) {
			if (e.getSource() == SimulationSummaryPanel.this.getJTextAreaDescription()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SimulationSummaryPanel.this && (evt.getPropertyName().equals("simulation"))) 
				connEtoC1(evt);
		};
	};
	private JLabel ivjJLabelOutput = null;

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
 * connEtoC2:  (JTextAreaDescription.key.keyTyped(java.awt.event.KeyEvent) --> SimulationSummaryPanel.updateAnnotation()V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.KeyEvent arg1) {
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
 * Comment
 */
private void displayAnnotation() {
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
	getJTextAreaDescription().addKeyListener(ivjEventHandler);
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
		int caretPosition = getJTextAreaDescription().getCaretPosition();
		String text = getJTextAreaDescription().getText();
		getSimulation().setDescription(text);
		getJTextAreaDescription().setCaretPosition(caretPosition);
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
	D0CB838494G88G88G6E0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8BFCD45539AF5400D8E30D9AB4AA56D423469AB0FA2252CAB1E86421D151C6CBEB6AC9AB35F80A3D741A76F23CF85CF9G0170888F918FE8A3A0A61E1887E04508B50189A4A10118CCE200C032A733A7B3B21999E6F648A486F33F35567E565EB3FBCFB271165F2F4D4F0FBD6B3F3E577A56FB6D0742663F46AE4F4A5EAC88D94B85656F13ACC1D85AA788D76CE93F9331A8D7DB3E2079FB8FE80370
	E7DBAEDCE3A1AF7145367C82E12EA98F52C910EE7AC5DB7EDB70FB137004A94A8EBFC25831C35E6C741FCC98594E09BA34331226DFB934862E6B002683C62E39EEE2FF39E92B0227ABB8D2D636A0AC22BC05266D8AD68B69AE20B82069EE46BF9F2E2B286589157B953E0E2B62054C473C59EB099E0DCE24F28F13B5AB57C261352CE59EE22B4E34C6110904F48A90A9FC0E304575B7389EE8BA5B7377EE0BC3AEBD5FADB91D25BE37733C64ADF5DB25521EBEC7E5E50B23374FA94A8E372B252F37D7748EB409AE
	49A9887EF3C477D5B456B9C0D5104E75926C99AB31B78352E9C00BB9FE3DC470DA785D856477E299BF7F6000D20676EF9E909676BC3AEAFD08B2C6F9D95926FB55B296FEE849F91B56C7DCBD09FBA664ED75356517GB581F90022003651F690DB77A1DC2BAD72A1F7EF2F5BD56D719CF4DA3D0DD0DAA77C2E2C049C85EB163A5DDE2BA0303ABEEEAAB170E7A4E2F22747AC47E2923F976BBD21F91A107EEF57E62FB408C9FA7CB75971BE960BE41F9A93E67B5A1036B3FC016DB25076D702563670721E154B8DEC
	EFFFEE4F4ACCC5CF1E2C5A5EAF133ABECC6B3A88FEE7A96D0361CD949F52602C0DAE20ED0624A3A1EF090CED23FD9D7A5210FB0BD0D0F11DE19CE29E0E4CB677B19F1CFD413E2C35915DB1FD4C66AA6E4BFFD3BCC903B3DFEE26BE1274FC485BF73EAD1F352F298D244F46C0BA89A895E8A1D0EEFFDBFEA1503EFEECE319F5E5E13631A651AB9F6CF3B82D12D7E032D36B3740D5724BDE314E6513C5D73754A6FA9D2245A9798405677EFA9283CB005C03346DF7030DA312CB72527E8A5A45DEC9163CBE4613D23F
	0170F8A51AEBF674CAE02057A3205E547ACDA4FE22CFEE75D8C5D9D2B9D8FEDABD0949F9318DB40282784D62722BC692AFB3145F89B44463F07CA851F7D47242F8D1D95960F61F6D7318C9C90454FE926729F46C08025F717D9863670E912C405F16DF64C7BD930FEE5468B1B93A097BB090E19FEB104838FC400FED660F6D58E6CCEB278AA54FCEB6ECB3D150FE3B7C2C2D787D41FD270D6AA4FE31748B5676B17663F50ACF610F957F67BB594889130F455754C46AAA967C1A81144EE3BC6BE8A1EB3387C51F04
	2D7438979A18E4D5A34E7C5CE4AB006BDB0A4E15CA7BE1F905B4AF9374EE9FE84BB781B987B00637C94165DBB90A4FE8E3DB58B11CB9E69BA4FEE4FCDB95068D962B879A487CA41D17DC32C972C8AEAB7C70A9F9B39B089E257F5A5CDCA605629E686F07200379B87ED883691322ACFB9D16BED9EA11E459616AD1731F2136FCFD960FBAD4202DDFB6903F5D7D505FB97F8B8D242FF43BDDAE09D69A97D172B3E8DF151CD2AFB8FD485D6712997ED2A3E96FE72581E52C780F469582696B4EBEB26E3075C222C896
	C59E2F032D823C3230F0007463943E5EA1948D98D977CC07EB6AG76497E1D58A7F78D5EA9A4F7FA320D6614185B2E4CAE99E4FD26EAD06D134C27670F997B7E9BBAE6A8380F796A791A0C232156AE2CAFDC7812686B82BBB2D064A076055347C9BE0EB7CAA7B864F6BAD9C8F92C6B1BC8DB77C9677AA498264940F614476F4B260DC1F9EDCAACD97E09A692CB27E6CCAC78064452EC43F5E15CA00B638C651A2644A305722D6BC13E0EEFD83E8C57C82004EF985F01A1421741798A06D81FAC9AE263AD61ABA5B8
	5597EDC73EC9C3CCCF6C10E2F70869E3F1FD351854B5DDB3FEAD1E977D253EFEE82F151575B432ECCDD8DA132770E78E615ACF2AA531AE39GE382D04785EC97CF2F4535DF7459F8A17D650A15215637728566C74C8535DDA47D2F6A952CAE67B4931B8BA0EF51852C4734A66D7CD36D73393B9D34B1A8F5F049175AF64330C9DF2E062B4D617549F550317DD8DFBF781278CB66C685D6568A97689A0214F599585D77EDDBFE8E65DFE8A3FAFC3C7DE17C7ED8CAE26E127A5526D95420DFFD2B391F9537613A6C16
	CDD5CAACD2275DA29C98BE13ED34CE1E345E1E6D7C1645A87ADBB5D635D7DBF2BA00185F1F7460FAAB83F8368E235FBF24384365109D2253B1A89595A78E937E897E4D9A9BCD00790126F03939BD243C96317BEC0F97FA2695FD1E7A2E5667BCF102214F11AF77E4AF9DD6FC9D277A1C887E26F01FD75951E7BB70440E43FD4C663FAF4731CAC03BC3A7506E3854A4E16B26AB8C6D92DA205893462BF607E2ADB9D131E877E7F62C63A66019B49E4B3C4B713EC0474CD2A07300C7329611BED1AA764B25F4F6A805
	7120067CA8667D67333D24CD765175D5354B6516D99300CAE673575E1A60B5FB72F8F58D11B6DE3F1EE0EB59D9F54447C56359DAF6AF47DF24F87B77D41C1569AC6D0FA4BD97722C91387EFA4DCC7A37B091468A20FDC08DC0CE204809582F36B49339915737F5AED8F63AC4E7CB4044056B4C669C96B7A5BD3FD9DBFFAC8E1758026390BFB1306CA9C1E9A6F715CE6E5D207422A0B9D66E76B6B27E8917C239017681B58149C0D1C0C917E259CF36FCBEDA59E90D1B5D07G2B05717DD070DC4CFD7D3CE7343D21
	F1990F0EAA17F6E9209CEBFBCFDCA0E5B0983B4D30DE2906ED01B21F3B1B34737962CB59FC31F5926C29015898BF43CF46CEB2D61BA661D8FE6130F6AC1F317EC2F6FE08BE97BF0979B7F7125A67F6751AD7323A5856CE6AA26F32367CDDC087005A010600E200E6DC06F5F1D74BE7E156C5B50D3F3EA6D8BC575B3FDBBD3CB32ADC7AE5C6ED6D469324FC1910D770FD180F01CCC0F6208820786FE379DEEFB9B8DA7978968F4A08EBC80386477CFDC467EFF8FD6AE91DDCE9D0BA797B01F2ECFFD7CF57818BA0EF
	B19043769C217B3B4B215C17633EE0CE9DCC97C283243B00E420C9C0D300120066822583AD81DA9389F2C035C09DC0FE20A820B82069C029C00B01F22F00BDB950BE20DAA0BB505095E86FE5EAAF9A52C9C0E9C019D73079EC738FC086488CA483C583A581ADGDA8A34754A367C7DC09DC0C3C0D300E6G2583AD835A9E856D952883E8A88A6D1D2B0D02CE00BE93BFC2359762FFE1D07E3E20F411D25ED0BA26EB72C8BCC7EA1F6B143863EFB98C7E6FCA24CE528D747B034A0275B5A508F7C9983EADD6F4A5A9
	ED087C0E8A52BFD7E9C72CEE96503A41BD665E91F413397EFF02300F0252E43F7A5E88BAF6EAFEAF8FA3E65A3D7DE87B587F9FAA263150636F84254939D685D0FE903EA98C9B9FA8D7B2E760DE7B2FC1BCE40D762600EB6E1C56507B6C552D467B6C02D663FD7616568D8C879D5AD3B74CFF37F564FD786ED623FD78BB340E66EDD9CFF57BFC120FCEB8A6D196153C5F4B24BC3A7D78B19835157A5DA47C5436CA6129115D07AA38CDCBDB6872BB5A0C4B6FEEA37175397B3C5DD2F0994F37ED65F96AA4D1A7CB3D
	449D46B3CC65E5515BA369E2B4114A333CD072D736050AA3AB2B5C23A9EBE30500EB131E9A92AF0335C9CB3797F65EE4F7A960BD07354DF84FE1CA946EAF67771375EC9A2497936CAA6C5F337C5A73206DD7694710F2830C10A988EF073493E81C86E773F961A932B70A86BC91E8AED08610F0B54E63FB3F9AF53DDC639FE13D7CFEFB68F6D052EE5C8E763407EE871F360F5E8E8E3607EE87F56D2337036676115B41E1FB3E528E1AECDEF7EFD305227B3693214B1AF042382C771E88DD561F1C983D2C8F1F88DD
	56F9A7C6AF6BB3A7C8D9AC8E2B432B1C9031B23EE657766BBD3C6CE28DB9DBF30A836EBE99763ED713766B2F41B5F7A1247783F58149D73335B76BA3777493BBB6D871C1A8DBBCD237E03F065E5FBB0232E9403F88A88F70A2205AEBF02F5A3C1374077A8651A2B9AB94BFB61DD331BD8A3675280A6DD5303E73AA36CF41BE5ACA7A1B26F7D6B0BCB5DB657DD8614D7FCA457E26E03B73D56C9385BB3DFABD47DAA064B239812164B5652E65F9B5AEAB4D41F83ED4C8661FC6D1369FBFAFF93D8E2B642327D1D84E
	5BD76529E55F43308FCF2D63983B4FA1F9D05F1FDDEBD4BF68F98C437F77EA6AF3006E7CAF54F26CD7304ABE0DBDA5AEF55BB6F26C086406B53BF72045B1A8214DAB4F2D66790D124F0E78167FD14B5D629149298CAF776FB5317E94475737B5364B99F6754086D5076472B9E447F907BC40755C5D2111594B301395EA8C0E77491EBE191D836D29571E83A5DE435AE79ADCD9FEC50FD17EA21EDFE7B24ADF4673B7B69A6597C4E37EC133D17EAE1E7F52C9237C7DBC7FC722D1FE834F6F6BB24A6F6279138D73E5
	48E7FB67E9E734FB67C951ACFFAEDC53390E81C39D19BC3F3B53A8BFEFB266DFEAB34A5FBE99FD5831BB78BC1E6417C33E096B18B9EC242303676FB134616179AFD80D72A3F8FEE331D1FE8C4FBFD7EE141F40730F149A65A7737C169D066D1067378F9A36C31E7F25E19DAC63793FB1ECA78557623AE2E373C8FB7FDD5706BA27B379C93F5AFFED70FD71E3942F3DB6783E78F37DF866598EF9B32E45FD740B747C2570BA582FDE0718E510E06D1076F06C55AA02C5C0BA1AE3C34D38870F6758CDAD84CB05F486
	47FECF31CCC8674560DA2668D46879396C14717C7C71A963B579E727C6DE139FBA95EA8D41E2F3D78DBE57329D7CB34760BCD951C54ED778DA121C47D6D69E01F961205853C0E7DD360EFC151ECF5067E2E2F09D795EE122B3855269C019B1F836655F0DE7DB89375FA24C7F1D71FD0A8C73056CB557333AAF3FDEBD5B5A7721A50759F49FC11BCE480F3B1E467E08362D113A7EA4071D31307C278272C9CC2E923479D9355AFC622FBBC0FEEFC0BE09512F95F9D636475F4732ADFBBCD1F05E979172992345CA19
	8AEED04A766475D985B1CDAB14FBC33690770668AC424A6F2847FBC335B760D9223D8731F19C9BE7C2ECBA476A9A91DB423181B3E2C5B7A2764EA9BC1B6C62588211E0246D26F0AC218B790A26A0164A3148A9581E7E34FA0FB07AF3D27A75876ED7DEBA437D1E026DECF6A7FDBE6626367CAA20761B50FEE2977A6961588D1D68D39A47E65A72147A5B7FC344CE6EC6BB1E9F221D7CE1E2A79E5249C08BB86F1F0739CF9CF358F8ACEFC66CCD896DF4F1AC229831980E4517A3B61FE35F16920C0C17F93108FDFF
	875A2862581503A86B675824AE4492B93655C2B03286DE940BE3D4B19F4B8AFF04E3F33D1F5C4B767034192649BDCD16CE6F7FAE4FD90CC6445EBA814FCC7FFCD1EC64E82E303F7013FD04456D2503A65BC7EC984039BF8A7C0987CA83DA74A3F6CF17498D8C52FB1CEE2F04776ACAEE214F7251FA9060771ADB10F7F5F5AE65F5490C27301A70F8607790905BFFDC530CB693GCB864AG92EE05BA399575A45139C432BAE4F2331B69FA3C854742AA4E77DFADA4FE120BF0D9953F5EB463FE5FFCAB5661BD3A7B
	E4239113FB68BB4AAD9D339C1B57DF25730975D6B62FE7F1FC9945BD9A1C4DE99F9C46B9ED8872B2EE457BF2ABCE13981B62E0AD8A64870A82CA84CA0D43397917274A05D14E994CEECF83CC5ECE4DF44B4AA79F79EE4FA94EDA09F3C752CFFFACCC1FE88AF94FA7A3CE394FFA1B728CE76A75D9644A4AF7BDDDB717C3DE831087E892509420245B30FC6FF514307255C0B32A16E5315BCE6F5CE3615A9CD23F8A138DB2AB1B2B5E28ECF395BF52EF532F5518DC2F61FD36A58AFF6E6D2164A698BEF739D561AF89
	A9F72C51C82ECA61B707147B56ECA4E7D77807C24A6DB8E5B4C6C5A97CF186F2345E8C70D440B200B2B5F94C462FC5A39BF9F7B03E6D4A357C0EC01D26BB74B65A81F3820D3BA35846BDDDC6B622B5FC7170FB06014E79C158C2A53DD4E763E9C39BEB62D53EC2783DAFDEEF23B6886BD0521E78E09BFFB8B3D29F0CG7E18A0DDECAC296CA2E3C6C2FC7098F30862536303471854CE9CE3D2A02F68CE9CD36FB3ACE3641D4CE62CF2CD3AB368FE4A1D7AB227833684A8774741E53C39534846D68DDF897C2E7A31
	DE27B98833AB69A11D0DC7821E8108D27263146BF438268669E2F21D3B03631F21614BB4700945F9182E31726E8A0E7FA40AEF3EABB87E9B06B17E051097F197467F35E1237ADFF497333B6C2ED07D5825FB6601D0C182635F1590CA6E3FA52D5CFE052FC139F641D5CEB00A53E545DA39C98A4F94651A885794C3392965DA39858A4FE265AACCED4B5FBC55C86E12D2237295A93C8714EB535460F6F0658EA3B92B42671F9AAAAE5103DA39C805AFD639A641F52E211FD7F49959CBD7F817A8575CE9ED79DB27E9
	570A6FD80C64CA941EAA381A016CC0C38A36E09AE9C7D54DA6EC635374E549D1A8DF3946814FF420B715F46AB435DD3235570D1F12FDA2FF9A44A4793A3D8E8FB950E66D765381BC0BA83C9B76ACF7637EA8FF9DF946C65D9F516DD0CD2FC79EB8C8572687A55105FB21718338660B3A9B77A8F7D33DF3A11D81349428A0113CFFF339CD603E7D210A1C7172472B8E391DEEEF2905BCB14456A1317D5A77E4762F7E1E617325F49CC9E43132AB5748B7E08FCB6DA5B8D5FB1E669D82FDCE2F105BBC8C0B60229DD6
	0768F4776CA8A9ACAFA92C58D30C657AC9B316ABB69177753FEF8E5E577FC9506E4B5FEE895E57E785649FED895E57D7E7E973AF34846F6BD11E156137AD843309CE1FFAD6DEE2A66BF9595BA73136771F07855E8E330F283FBFBAFCB176AF017D6570C53541560C6BCF07BE2F5AF25A783CEA7BE963732A9D27C73EC77C6169504FEAD71C56BE2B4D563CC33D247E96C0FB49845AFACFDBFE89D055BD385EFD68B4E90364ED917DDA37465AC36EE711B7C4E434FF4B1950E53D734C686F155CFB06BE436916E5F7
	AF510F7874B3243EE43707028C7B599992A32F2347AEB3D685FF041EF5B8A59B05599EFD82BD736900F2B96F41FDFB6CD15C377BB9965103FCF3B9763B9E64CB63589493726D3A973157CD48D74EB129917946F1ECF6A372C5F2EC32997952B97697B372AD62186994726D7FB744EE231889529D9CFB261B6E11A19D4931CFC454974B31A768F94692C867A5A17699655B8E69839CFBC3C43BCD9C6B32F03B9CBBC8B18F2423B9F61D85FD096758C66E4B8C0ECD965117B577A1D65925776567DD68CBA1675BA762
	39F38947FEA06AFDF9C0443368AA4EF7C58E7AE26658C9833B2FF23BF11C4F4C6DA6F2EC1A015DDFF03BA91C6FDDEEF78147FCE774F633BB7973EA77A376B5457C100E3C9F57EED5DD6A58D927797D0C61FA779FBB0E7E61225AD0E248639F4B6330DF44B61840319B4F68631FF2865BE0B26733733826F1AC7E8C3641DD8FA0D654292F13D91D3C9FF23E9DDD5896CC9C9B6A527BF2DB973605F64EE7935097A7479AECFA3B3F32216C8C4E17D40A3E4C67581BE528AF1DE3773F4F7B48F4444E6F4667A32BB876
	4087A8EB1D0EEDEB42305A1EFE20797D567045EFDB65975586AB738681E31844636065F5124C3189B664AB7889E2AF5910EFBB47AAA42CA7A7479AE91D0C03F494476E6575B405E33FA942FADA4C3158E254A77C943197CC28EFB3473E2861E7519CDBDE4E6F49F1AC259C75C5F0EC96579743319DDCDF9C471E7F8875E5F26C865A86739E04B54403FC4EA8C559DD9CFB65BD0249100E66586DBBD0B61EE3ABF722EC92476E9AC41FF3E7A016174D6DF24C114D6DF26CF71D68334C31B85A8FA7C1BA06E32B783C
	9447315D944B0474661FA1B6C831A2C8B7F0EC1F8575F5F1EC320577EB0E8DD33BB3A01D46B117D5EF63BA2B5686DB631FEB46FB970BGA34FAE12BD89BDB327FFCB3E50E2B7DCCF7E66FD4176966B4972D2592B7CE68D1E19EE3F6BD641748BD3B6F9AE54E82FD2B8D3B9AB1A295ECB0C6858370A754DAF86C6BB330D1FE934CFED9CD5AE781D89A6B7F1D439E0E267A0BB8769BD55194167A3A5941FBFB378FC643E81BC9FC9073C2A07709E6CD32734FB3C029B1C59A897ECB76EA165AC62A1B5EEEC4D5D62A1
	F53390F0A199575973ED64395EB671EB65441C9CAF3B3DF4258DE013576D113C72GC369DBFC782E69084FG98913397519BFA1DFEF96F686B7468DEF6AF25C7A2AF9DB16C7ADEF2AF48ED3379A43E9F78E1EF81476AFCCDEE72600F5B457601D36A69BE908C317619F20EC4181DABBCF0944F958A1375678A98F3F2BF12685A87BC044839823D2F1B2C56A753DD62D3F5AF6BC15D66903AF3B4043A37A9F9F61D6E63322AFBF38D6A16C368DEA9286F7BEAF593FC12CE3737CF557D6BC6549D9DC27772917C0E57
	691EFCDE553D3695F5A705503DF6845DF3F53A27778B826E77E71CA267BF3D225F515B57CB9FB8E3775452683EA58DE4B312715E5B4B5D440E47AB59A42FD732324753947EECBA9E2E1985E3BD90737B1808FE1BE6991FBF2D54785DBEAB301DB8E7851FBFAD34D0DF9D2EA0DF1751F15C8F7C913378730CA299E76DEE2FE3506D12C5E735535163A2BBE4C546A962F9CC62ACBCCFFA1E3C362964DFC6F5E6C05EE22E7304E5CB00CEF290063D8665E48B6A5D7AB02C65A816CC31DAC81B9F46589473580CFBD89F
	1B65C13109FEB8B0B6718F2B31E1B6E65272A4811E7CB016676F03EAF9CE0BF86FF6B177AB03E2EB9E01BAFB847D0A4BC13FEA9FB12E33D586ED8D7D6AF8A4382D095F28BEDC60BE589FC19F84B6AFC3BA1A7B70B177A145400765237830C063835377F6A76ADBE8200F703D2969C3CB01E7CD0AFA8EDA18925C8E47F76A6FD547F4E23D1724E07CA30654320B346CD510571002E5FF056EE77C100EC841328FF3DF93D30C4F1F356DA2A5A5286CA9416D62E9EAE3A160CB38DFF7DCD07DBA5A05F5327DD1746BC9
	0A9D00F46D23685785817D72BFEA6C57C8F59279E830DFB9745CA6867038C7512F473E95F81D1514213DB483FBB9CAFD65073037C8E76F5F5F47F2E6C29EFB9662E67E9C646659D076594AFD0D596C9DAE6647139F70F24F567B119FD49F114093BBDBEDBBC933037B43AB43EA99AF63B1CDB650CDE4569B44944775B41D6E02ACD5776BB6541D91C2770691F4E74E8E0E5FC93AE79266C05F1803632135ACF00D30B7DB35BF0F575F01B9467537DED3FFCD40C3886B4FBAA7587E6AE22CBF4F9CEC2773A8968569
	A9F3308D356528BE347179BEB5048F9BC670A1C367C3E9B99FC3398F2FD0EC4DDC98C367228F83392A8F1FF29FEA679A7B20DD97E8FDA0F6BB66867B10DB4A475039D88F77148656037D38EAFF8B2F07C986763384369F4131EF8A7090FADB71ABF1EE7058173BE364F9755D9DB88E4E6071383CC975675D1D684F62907EE0FDE87D41F6A94C8B0EC79A5DF72E81FC6BBC34B76DCB555E38EC9E7FF946714F5758C4FBFCBE5359DB4747CB7BBC0C7F03DD0171FF3AD935DF486779C986764918299DBF26GCF62BC
	55FE4A3C6078FFBE4A3A66AB3E2E194F7D3BCA9CFD5D1240650447B00E19ADEAB992ACD80E72470C4B219D7F93034AE1FAAC389C57F699BFE3B60D46378978BB9E43732D73817B28A140238CFC50925B4B7D56CCFC0E835EE42005C04BC8599EEF4BAF82BAG5484E4859A820A860A871A81B49FA883A893A8AF957701E6B7F98FB254BED0F900185EF4710A8EF25BC5F947AFF99369DF8E175361128E31AFF08938E7AE26EBB476FB0F66770383977F6CAE6D225AE0F5D05BC64A4462F7037B7DD0712B36487562
	F9713865EB29DBB60BBDDC265DC3F471FBEAECFB2D64F5FAB68B1AEF11306F78G232B071D47ACBA0A67B15BD30D4EE3F05E0C7F0A7CE6B29F59D1461C0A7D6071F6B53F06FF3BA612677F71041A3F6E986627707C77CF2979FFEF457CE5BC3F36C375E55EE9017711B95D641CA5609E334951CBDE50F13B0A7ED6D1D2D8312F9865961FD165CA44F06418BF43A27A336F8963B3AB65FC2ACB75F10F285AAA8F4B96633DC4A46FBEEB9E7DAEEDA17F62EDCADF69211AE3661AE6EE2755225A7944923E1D4BADE333
	73FA37EAE76598E2F712474EB9E26C8AA5B5F607B44FAC14F505DF2609E228679B185E85E7D53D63F34676EC4233B9D80E38A730CD7EDF4D338E370FE27BAB374A3BF10C36ED5CF6BA37BD68D17BCBFDA766EFFEF274B35540385FF4CE756B6ECE6D338EACBF592B66C71D893F2F3D68D365CEF7065FDE92F9D99AC2140569DF5127361762732A2DF74232253C6757B5F21D958C28FACF8AE32BB3F1BD16A362C92C333D032A4FD78E293A63CB47267BF4996A1E41F55FFBC1551737BBD4395859701C5DF8B61C89
	72A5CF61B3C8BDBB485C431EC1F27716BAAC3D25FD67FBCBC50BDCCAB6721515477A1C4E866515DB01BDF7147B81FDB69BF48C81C5BF05677CEFFC00BEA6BCE5D40F3B2AC24FB54FFE2B366D1F8A28E761D3D8562543EAFEA9FF6FE57BFC4C5F16256A6A4FD163D2DDB636B83F4663EC1EBF525C585D225A58DBBCF21BFA31D565FD46B4B6FFAED567D5DE5695DF297A3EAA9F593665044AFB5F98EDFF4DED27F05B93CE2A7ABEAA8DE51B499F6131DCB6E2AC63CF29BABF5E916E9C45EC9C5F49676927793AC433
	EEA869D2F5AF4B9ADB796F4966739857FDDCB347FF92F2ACE172DF72F9B06E69F047E8743BCAB3773A42DE9FB07EC74266E7FE7E5A42679ADE4EEE2B6A4BBCC97545E68DD5E6653994FA2FEF73B3ECDF43302BA9D622E0EC0D7A97F368FBB92F1BC73F1733225578192BB52D7AE72EB6349ABFF37596DDF362B3D74AB3EC2DA48EBD30E7E967FC6FF16C8447F6D31D847B4AE1156D9CAF21FE913CD6A28679F7A528DF7D94EBD730CFB4588985BB50CA6EC7C111E1DB0A32552D74DDA207534952B514476172C9B2
	0FC7BD75516191B5FE9F679871DB79767AD16D1E31966A24631911770C4A9EAC60DBD69E100988CB2EBE607D1F9810C988CBEE53B12DDCB2482405A5F728D5AB3788E41605A597A1EA658AFE5E163F6B676148F586486D8719063064FE1523156B8299B9AC393381F213C0E6CAD8F247BB35F209A01392165CFD81F28BC0E6F1D8F23675DAB9A12DADFFF3DAB8F216B22DDC91489C88CB6E895DBB88239113FBFE4CF26C9E3C1F2E011A52026F4D0F27F8C7DA703D79559F607A4B8EF929E9F80613DB2435DF7EEC
	DB3E6959F04A7B102015EB8799E7D8F2FB835E4D9A87B251E1493D91D0AF71A0B3A3AC3948007EBF9FE49606A5678E10DB8AB2EB1E8BC72EA1404FC2105997161CF9E7403888B29DE1495D1F9DB0AE02CCC4D8F2ED8163C68C48A404A5775B00F79E12C1A6ED94B9364E97F7EB3ECFCB27AC06EF772FD5F11C3214F9BB37C1BF172FEA504E658C2B336B790E5975FCE20D1E4FD623677B70181E2F6C181E6FE42B1E2F33D54FF72328670B9575FC678D783EB1607BD30E1E6FAFB9FA3E6F9970DDEA40E76BC43E62
	9C444EF6A25F5AEC44D2BB757A1E6E546B3B30DE4F773D75FA3E01B2BD5F37E5DABE65BDC97289BA6577251A5F859FDC7C3358620BEA0315F9DB11BE8E05C57AF8BDA768791E9774FC75BDFA3E63BDFA3EAD65FA3EED65FA3EDB8D7ACD3CC13F19E840F73981DF37C13D5B4B74FCE7F76A79BCBB75FCCFE46B791E495673759A74AF2FC17F7ACF0B1E6F3FACDABE767FA63A59528C03DA42AB42F21FD7D89738A7493A6D2DCB8A2E5C5B17AD649D96D658C42747AE56894B6947129B04FC8B5D8DC8DE6015050286
	A2DB8632E5D4763386A13B5BA78B5BD43C1B60AC3745A3F6CB40324AAAFA4FD28D6704EF052CD342AA1B3747C28D48C27629432740ACCD116C1C86E135ECA7EF1E105BE13270162A3A095C93D1B2BE7353A33EB760BF8F7D03022D8D582CBE7A230FDFCAFD214D17A32C28935EA65F97E8113C6C1363D67279C4657FB1F2D8D865F103DE72B4D01D106B231765BEF94089255FECEBA65F5777491215FF7C960A7BA67329CFF6B84BEAC51F1DBE4581854DF511CF658A9BD936D3F47514313B894C5B2C6AAA6233C0
	6697817C5DC47DA5DACA3916CD8F2EFF7F3D7A0553F3046C4342EA27DB349E963BE53737CED8A55B3D124F6EF6DA7D1EAF18223CFED2557456A3285B460BCF8ADBCADF32C3274F7AEF0ADC3BE86DE198A17DDE210CF212B016F28E6BA13B6885D31237CEA67FF79737F7974F9B7F2E3C7B09A3EFF5E48BD9CAA1C9CD6579BDB28B9898DFCFD512A2179E862F65DF3DB26F16678FACD98CA207154684068BD42EE3FD3D9649BBB1E172BB79519917933FBCA4BCBE22F90DCC9AE22F43B9GBFCC74488171D53055F5
	79ECEEEFAF98AD8CAAB1E6F147CB2F19D7BE794E57CED247D5D1D9782B5A2AFAA4F7EFD9354D66F0C9DC81549728FF072BA7BC25C1BC9B7DD73DD97F6C9B96E8DFA6A127373C1C7EDBC17F5DC37E2D20C885C5AA76C025AD91C67C8B4E777BC9195F74736892CF6A68DEB6637C5E4A1C97EF4D4996F26B0495ECEF8F0D17EC502171529EDBA764405E1E363BDCAD494226163A465606EAF35D71E305AD2D0D0D554DA7AAA13064138E64E881FEAFA75F2B03EB1E72BDB8783912FD260EE47A480773E4A1C7F2D9E5
	A12B10D11E2CFC62CE055661D7688AC99BA1126433F315AA83210F547F05D0E13FC33697327FA1C8E1379D9A09E4ADA41FEEAC34CAB6314FC9DDDB23793C1C22EA051BFEBD0EA4E8ABF9A73848978D758A9B75676855BF7865AF92BACD1B3305EC289B3B44AAE3F9BFD9C9526AA0536967D4E3E5C04A58EA658892E317DF219B06883ADABF1E90F89D4DACA06C6447A65E99C9EA8B4D5CCA7F5D765D1A76E17F88FE7FD3F95AB81227E32F37FF391ABA71AFD3D3DDE32F2931207FD4E57D7ADF26D64E7D4BF86A7F
	C7351FFF2AD20D7FC7150A7E290BEEFC5F28F8D87B5E4C52895DA13F0D1079A2BB0349DB987C5E4CE23A6FB3815E35901F17BA50C33EFF9F6AF91F16DE516914C56759EA1715BC7BF3F0C01654F7657F54C3747B10C749E4F9CB69B7423F56487CBFD0CB8788D0CC51E9A8A4GGA0F6GGD0CB818294G94G88G88G6E0171B4D0CC51E9A8A4GGA0F6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE2A4GGGG
**end of data**/
}
}