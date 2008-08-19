package cbit.vcell.solver.ode.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.*;

import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.*;
import cbit.vcell.solver.*;
import cbit.vcell.solver.stoch.StochSimOptions;

import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/2/2001 12:17:49 PM)
 * @author: Ion Moraru
 */
public class SimulationSummaryPanel extends JPanel {
	private static final String SIM_SUMMARY_LABEL = "SIMULATION SUMMARY:";
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
		StochSimOptions stochOpt = getSimulation().getSolverTaskDescription().getStochOpt();
		if(stochOpt != null && stochOpt.getNumOfTrials() > 1 )
		{
			getJLabelOutput().setText("Histogram (last time point only)");
		}
		else
		{
			getJLabelOutput().setText(String.valueOf(getSimulation().getSolverTaskDescription().getOutputTimeSpec().getShortDescription()));
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelOutput().setText("");
	}
	try {
		if(getSimulation().getSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson))//don't display time step for gibson solver only
			getJLabelTimestep().setText("");
		else
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
			ivjJLabel1.setText(SIM_SUMMARY_LABEL);
			ivjJLabel1.addMouseListener(
					new MouseAdapter(){				
						JPopupMenu jPopup;
						ActionListener copyAction =
							new ActionListener(){
								public void actionPerformed(ActionEvent e) {
									if(getSimulation() != null && getSimulation().getKey() != null){
										VCellTransferable.sendToClipboard(getSimulation().getKey().toString());
									}
								}
							};
						public void mouseClicked(MouseEvent e) {
							super.mouseClicked(e);
							checkMenu(e);
						}
						public void mousePressed(MouseEvent e) {
							super.mousePressed(e);
							checkMenu(e);
						}
						public void mouseReleased(MouseEvent e) {
							super.mouseReleased(e);
							checkMenu(e);
						}
						private void checkMenu(MouseEvent e){
							if(getSimulation() != null && e.isPopupTrigger()){
								if(jPopup == null){
									jPopup = new JPopupMenu();
									JMenuItem jMenu = new JMenuItem("Copy SimID");
									jMenu.addActionListener(copyAction);
									jPopup.add(jMenu);
								}
								jPopup.show(e.getComponent(), e.getX(), e.getY());
							}
						}
					}
			);
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
		getJLabel1().setText(SIM_SUMMARY_LABEL);
		return;
	}else{
		getJTextAreaDescription().setBackground(java.awt.Color.white);
		getJTextAreaDescription().setEditable(true);
		getJLabel1().setText(
			SIM_SUMMARY_LABEL+
			"(SimID="+(simulation.getKey() == null?"unknown":simulation.getKey())+
			(simulation.getSimulationVersion() != null &&
				simulation.getSimulationVersion().getParentSimulationReference() != null
				?", parentSimRef="+simulation.getSimulationVersion().getParentSimulationReference()
				:"")+
			")");
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
		if(getSimulation() != null){
			getSimulation().setDescription(text);
		}
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
	D0CB838494G88G88G43DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8DD8D5D53A3F161595951619154DD0D92122D1E393154D654EED1E51BBB4590CF7A22FCD5C095BB0B35AF5A6264BED3C5376GFA0E22E2E2514D0ACA8BC7C490B4C2CC90A4B5D2C6C1D148B8F0767900FDBEBCE7A3E0D87FF72D355F357759FB9FB8740C7FE74627373D77EF3D1F6BFB2D3D76C1A8F8A7AEF73925C09016678A4A3F0F168BC247D702D03C78768508E57DF521CD507CFB97E83D70F2
	EF8EDC2D47965A06D2965A7205E72DD6F85E844F1B12975ADE077B8D426041CCAF5CC4301382E9DF7F795A970637B329846D2C244FD2EF9EDC57829581C62E52B1E2FFDCEF2102B7A8B852F20BA0B49FA7BC5F6D5D24E00940B397A88F7022630C3F8A2E4C014B2A2B943E0E8971C2663F86ADEB089E0DCE24490A6C9C65DAA43C3C5C51C2EC4D25FE9219CCC8AB810C841FAD3C665B8D571A66DE57A75DDD9E3974DC37A8C925A13FF4CE8C167A9DE2292B4FD3DDFD5C636D136C32476FBB5E67755A03034DF61F
	A889C29A551DAFD929AEC10807E72915E07DBD445EE6382F81EAE9C57CB96AC7A25C4F85CAEB459877FED82344507655034250833BD62F0B90E3D62B925B89B546B937AE323CC14B63975BC93EF7C05A982009C0C9C029C0D9C0EF52FA70CC49DF60DA57A5577B3DDE3F2FAE9058AFB9021FC334925CD7D7C30A029D933B7DC107A030327EE9EF0909BF439113DBB4EAB916A77195D86E8947E6890B3EB7493252A4CF9645FFE549BF49722278241AA74C76C6A9126D03A7436BE5386D130256F66550026CDC935B
	4D1E85599D0A1E0046765413242CFFC76BC0AC5C93B9A24F7017A81E2141D99D3DC0F1723C8C52BA5B30EE342DC5DF1AF26E90724BEFB24D0749BF0C31BC5646FCD85426776524C4F4DBCFB11B2B39AFA2450BB5B87365B7341E1267031016G446A5733BBC81B4DBF8DED8828812883E888E8B2D042E92CE39FEC5F91E59DEB3687657DFD9E49A18685A67B6C765760AA8E48C17BA1DFC8367B3A45937620475EA509A1C1614958CE7220ABCCEE892D7B5DE063D351A786E9FB856DF62FA80B419063F9E97BFA42
	9394E9EA0B47AB0281EFC0C03DFF5E3E812E12BDA437869CF6D9D4B9D87A0AD2D2A6676CA7C0A3A800FB16AFF965A43FD2A07EF420CC1E8FE3CB093E4F44A074975555CDFEFFEFDF2005C4A2B43413FCAE21FD07956E0B5AB10F2DBB89B69E1E635AD1CFD2692B9ABD8D1EEE62BEF4C45846FE5FC3E4E683FFEABB561B975A305EB42C19A9147C5B0D267566B12843CB5BD9FD31F6685B4FB22A1778D65601FD7E25BDEDD1346154767076B53B5D3C3D197799D32232112BFB4E0EC2E6ADAF33F395244C1AA1AE89
	A82683737A4552A2D6F7775BC3A2565623C128E822C34DF9666797928C24426B0A4E15CA1BD94363682569D3C1F78AD0A65026AF205DFC01F979DF3D7AB8D7AAD75423779DEDC51F17D6A9128D1BE2C7AF17ED123711086559EFF61071CABCA77A6486B1A07A9CF093D252166EA0F614766C74F31917A89E0076DF8F8DAE4471E5BBC89B354BF2505355A70B47C5D97678DCEAFACE9931956A6B8A28DD875ADADDC67C7677C37B677C79E5246DF47BFDBE11969E97D152DF25EDD714C4AFB8DD6F6F73498CAFACA7
	F31DDEF1D069BBB6152F90C85B177AC8BF44668F09F612FF0120074D8A0232B0678B522E6B25C2CD9EC9244CE1FE938B5729DFE07B6CFF9F5B676673530464AF839633B1E672DD57D9F24E3036D3F8C6ED1F4C27B5BB4DFD5FC07B90858FB1DF83BD24DF0DB4174926F213BEA53EB97C5DFDC4301C3715FBAB091DAA305F8464B803ED653A1DA49D7BA52511547BA509E5B5297BE7C8FE547696A8718C417344B3ACBF547AB3D411CF4AD4BC5BA7C2F7CFBA484FF83EDFDA712AAE6D04D286AC7D6A8AD2861226EF
	ED7B12580C13F0FE39D8311773A53BEE7A12594E6BA4FC273D48B7D5C91F835785C0198A5FB84AD762C33E36CE561E831D2C5FA6FC25C41F1D7025F13E791DCCCFC6A76BBF174175EDA2CF7D1B6E677A6CACBDE0E776475B19DDD6EEAFEEA5F5094ED1FB6C676C832521FEE88F555507E94931B968CCBA0E907EF8BB4EB51F7613B2CB0367AC62D797563BA75760DCF3E2EF3C10FF61675911665345DD4C0F26AE355E25B777E533BA71BF3B084D98C81B520575E1C525F63C2B8B057C5D9EDA591432FA20D2DBAF
	9976F065AA38BABD4110FC88BA0E81AC5764CA62AF990B95D8191B38685C045457C4301B81D454CD7029F48DC4721A4559DD01F1461E3FCB683C71964B6A88F1BA3A15F26ED6639CF7E5C5B633B7CF42F9599CC84F84E2ED254CCB7C8E7176046575C2A9298BBE31DFEDEA45CD4655051A3AE76B891CF75E3121D671A9E5569DC24DB7E7ACE66B00716B5C169287733545217A1CE8F946729890737BD2AF66D39C7024BA506F5FD25C6373489E3B64B9AF96EF8D08043F313CC0E3A39F30AA208E91658AFCA45EAE
	FBF72FAB88BD0D83FD1E793656E72B7DB2D31FE3FE6332A4094C57B4D17539B07B99CBC9B67A5C6AC31F0B1DB087F662BA2D6093F225FD2F00F607CE215DB1A93305428D57185A25FD281359CBF6EA6AF17EB3162CFC34EB73235DFC57C25BC197467CF0AF292F589FB2EC221F54B3596D89B1BF7ED0270DBFE9E50C299F6EFBA6DB4638187DF8176A4714754FD89257239FBF77E3DD4B821EB2B77AD19D50764B25825635BD95243D755139EA1D4F6717D9F5038A45467E7B8D6B9F37DB1D078D390DF3B236AE58
	F5162EDD5CECDDD04171139497BCAA4E7CFE167ACD1E831016570BF35939D5246F0A17965A9E83DA841489D460DDE82B74E21FF1F097119BF62DF04887D3F81FDDBA9EB668631CFD9749A3982715E74BEEEDDDE1796025EB50E6EFF4F3D0F56E749515F30FC20E55092D2735FB96B96B8FDB32A355CDC5F73CD72D93AC6F862BC9FB4CGBC4787E396D0A5D00B8F736D0A5DA5A3659B2E1B148CE3B1BDE1289332AFBC3ED85DB313FB46A077366EF936CE0E4571449EB24E25C0DA86106007D800EA01BA004678B1
	1E853B770E944F81B241EC715783F690467BFA7D1C0E7BFA25F724BD877398EF9AD1AE4E9FAE475A616397C88CA6E3F48B4CFB6BE039294C8B4FDB357342A4BF1BBF2443B58B080D65974AC91FC346647CG0EE5979A35E3761CF597A20EE5F20079B7B1205983337ED199332F5FCB4AE28124AD85321EDDE85B8CF4904881B47EAC1645160F76C4D996F5B47F0DA54172335D776D4A41B322DC7CD9333A766ABE92DFB224AD824A844A8FC23B81EA86120398DF7FC77BC70A0FEF9DC00C3896B129F04C5FF57EEF
	9767DBA34A45844D623C221644B995521201D201B2018AC390ABD0DB88637C4948F17260220973E9033F51759D3FB648156A1E8321F0B936BFB20F4EEB47C35A94A0065D6DA3D8B2BC2706F09D3D67ACCCC704E5701CAFC31D862A82EA82F2G8DG45G45814D81CA854A824A6BDBE8AB862A85EA81F2838D81CD811A8DB49FA89DA88B28705CC2DB49B93477853557G4F8E20F1C0D34F317952E3F0CD874A6987BF009A009CC0630162016682E5G658D00BE20A6A089A886A881A885E8B1D05EA0248F223D7F
	7447C2E3C71F099F116A4B10623796CB57BD1378DB7AA26BF0EB52C8FE8ED7BF57AA790E771B23607F36C44AA446C4FF31AE96AC2FA69DEFDC943ECDD1F4B914BAC46EEBF57A8714FA444A26A9C04A0634CB5266AA06513D85E81B8E5B2EFBA67BBB6F8E2363FD4DFDB4FBB45ABD3115D170FFDB5ACA7342083F25FBA67B42E4DFC13F6F34A18A9B9FAAD7B2B65A147BF7F4BCE46E3DD140B56413D5117725D22B4C7725FED9E53EAF15DE351E6120C33BEB0D690B2B065F374A2CB25B37FA0B1651AFACE47E9C6C
	73499E2FC84D32F905B8C06A827452477AFC2C8BA716C37A791D0063420F6A485A24E9907B7F2BBE5EA9987B7F838E0F6C8F221296519EBC6AF4724E7FF8C06CC61F47D6C74E3F71556679F7ED35F97ECD2C9EBE7F26D4C74ABF16C773DEDBC765C3A1B1C4A79F8DF6592E247DDAA6E5EE5863BB82A33800FBDBAB3CB60567006C2FAF67F61F5E93B94634BD66B1BE3F075441103FAF58AD6A63D832271027298369A1D97492F7143DCFAAAF5B03AE51108F7FCB65D9DAA47995B511720A451A2C0D75F392AB2BCF
	2E83A43FCC6629473B03FEC9A2BBCA823E57ACDC056FB55D03382754E5A575B48E1E13006673716912F26D9EF34620311FDDE602914ABF9F0EEF0667AA20A68D4E6A761F771375D0A760C3C093019200D24EE3DD1FDEB362BA724040B06B48A9FBA35703BB771A5703E9FBA35703443DA357030776C62E8749FBC72E8773768EDF8F2E772DD66AC133B36877B617AB3AB76D0B9C6B7B7B4CE35D3AAFF22CE57BC60E7563FD11E32D5DB7F22C077611D83ABC8ECFD059DDE6B1B67A312EB74B7EE635DDCF280D9C63
	64DA73983FD39BB9463BEAC70EF1D6ED64989F289DB9469F541A47189F5076DD153CFC53A472CEC2328F7A7BE4F2F60034517F666F9E96C35B48812C842876AB3656E47D40FD7D440E93D6B8D0DDE807DFB9C4E49F7521EC9C704F86DAG1489D4B004FBD1475EA7ED7EF0133DCB144A95BFB61CD5311D8AD678190AD5A8D85FB9952BD4306D0524CF5174C0658CCF31283C3B945E55A7D5EC3702FD30DA45BED230AFD62D6358F148F2193C48D5521AF356703483BE87CD417CFC3E084CC3BE374B6E236744E050
	6390C3F4179D633C5BE6D5E35F49303F342F6598FBDFAC86D05F9F69FE2662875D77E5789FD6D11F43F42F5E2746D125E055FD9AFBCA3E9CFA73D50EFDAA7AE10D9A9CBC6EB9AF224D6B4E2E62691F0BA1B7622FEDD363BE9E30135DDF9E772FB5F97D310249EFE8EC17A97D4960FAD507688BF9E44FB90FBC48755C5B211129E05869F2B58F0E764981780F6EB75FFDD63B5FBCB4446AE79CDCD9FACE0BD97AEC1EDEF042ACFDAECFEF9CB4CBDF4453EFEBB7CBDF4A53BFF5182567DC4074E9CE3374C21EBED5B4
	CBAF6169C9266935104E760AFE5A2D5DABEA394052A742B51E6BA8B475A119273FE5EAE381CFAF104C52B3388F21ED7A7718A4FD992467FF0DBA2C39E6BA0AF97A77FB4D52EBF87A1FBCE6694DBC3DE007D93A0327A76FB6CB9F606963AA4D52E3F87A56E253FA4853CB8553FA4853CF1A6671DC1EAE3BCC6B6157B8EF19D7C75AA59F4315B92B32BF34EA387D2125DFC75A3F7F851D2765FC43766933B91EC671828D4E766F5DD6FC7F31995246FF03FBADD369DE64E2F89646A076FE8541B643F38D47DEBAC430
	E6F86E64D84AA95C2711B93664B441A643F382477637D3BEF8DEB0063FC72C0FBCF62F29B79F3BB7541B2FBB5E289FFE5D71EEFD24B9944B1BF73CF8F6B0837CAB9A0BE3E8F9A7590363F369D2FB3FDCDD7DA90C997B6D2EA6BAA233F9344BEBD5645B4662BC7AC62AB3961E6301124762BE6F4087384F1BF0779DC24E20793B5934168B1654312C6C170DD577F9675F3A4842ECBE67C31BD517C0BD3C04E06B7D5AFAC84A7A23EC368F47526B4352C91ECC903469FDE16944DFFF187CBDE169A40F1ED164D9ECBF
	590231AD7BC922A0BDB0AE62B94E24CB14F3EC4AB5B67D6665F90383594A7BF0E7047761F404E171BFF3965F0797DC0A7B6AFBDA90EB6258E7A7909B47318B03084D615802F644B2B97635837769CB46A11666A498293B319C7BF79179B2B976DF9CEB3B8C6B536FD76D94C6BE0BEA1C1B60FAED05037BFD9956B3D1A475AC931E73AEDFE85BFCB95ABF4A7D2C61D88B77A90EE31F517DD7D2FEB9D7A0B6E39B5A2939826D346690BBB2BC47GCD653C7BF239CF9CDB524B7312E3BBFB50CE49F8EEFB87E28E0E7D
	77EE4426F06C91FA260074178BB856D90CB6722EC4CC94D03601E3570A088DF1EC060BE0E4FEBC7BCA6C23AAD7E1DF3678CADC73AE32123D161A2B70F9A3BDAFC44EFB30E71FB51AB3EC23A5E2EF2D006343959745C636663AC1F8041EFB6A7287E927495698BFAB47F9C18778A38345814D3E0A1DF7D1662E9574DC07BFA862F9012C2B086CB1BAFE2440FD6AD5483B66F08E6575490C677D430427666A053603C0ECED725CA934B9GD88CD082D08A50622BD1CF8EED272243A313EDB1266B03F66C8B73E2106F
	D4BB49BF51C7389C0ADF01F65C6FA80A41B25CB2627BC0BDB1390AEFA9A70DDA0E0D6BAE3A4EA80BE1637AF20E87A8DE23415918F6AB9F538EC2DAC28C3E2FCFEFA0F91CFF8D4CD3019A00BA000600A6DF03E3730375E542887BACAD7EC0938C5E12E638E571E50C78DECFCFEC6C786715B8F64CFEF83A10F4F9C344771F895730761122DC0B1F3BF9B93932787A9BC9FC4BA02D70DA183FG35G390146DC0B713DD22F3CD7A75B24F532EC6FF65353CAFC5355A3762BB0D9BC3358FEF556AC360957AA6F772FB5
	4E5518DCED0B195C9C05FFFEC4396693E6F2698AFFD6C439CB864D64722ED3669157C512FB3A5DCC2ED261BF98D16ED207D99F55217087CC64880DB341A78316G142CC9D35E0FB94DECACD078B2146BB21D4E7CD8230D4D00D581B5456AED2409E6B6BAB5FCB25C0FB751B9C507A5AA4F0F99ECE41A5AC85570AD06FBE10251C6018EABD61EEBA668ED2CF68C57861B015F2153457A122769DCE2E0023E0F79954547CD5077B13DA276B1311016B9817B54965332EA3BDE79FEC8390E39BE5C0709579BE30E87EC
	8E507C6B75B11EB24D47F48DDF965C675DE054D92443AA156703B768EDB4071D0769D052834AF59CDCA76BF4B13944ED7A7CCF5070A51B784472F99E1DE3AD38C11F7FBF23F85A8D7A7CEF4F417CDF8CE94D93B17F9BF24D4AFF76C4E6F76E44C86D788F265F71ACD27817C6147B28C7AB17F323F216D93916403576C633FCEA5E2115EBD1F85C4AF5882E3126F24BF6EB6526AABCC94AB5852EE926F24915E671E5AA3C56C96C3AE9123E9E0845E6F2E58ADF4324C87952A3E8655A94BEC9390E016B44C9E6FE5E
	A01A590BD7F867A85779F0CD1F241DABA63A4C6432941E3C1BE0ED8BD489F470A606CD3D094E43D7B5E09D3F4998CB36C22B15EBGF84681EDD41EA75F2456CBB6773A75E332CE64A7239A44D0F75093A01B5D2C5E3EDC0EFB910BC1F673E4DC9F2DDECB4E1B296BA33A9CBA608D48037B695CF43FE87761DAE87FCE1C73F5CC46B5CA001E151F884F89C00F81AD1ACC3EF1749C9138EFAF9CA27B3F381CAF2D77CB7EE0E997B9C94866A1F17D5AF3FDD52BAEB1BD33CF7B111BD9DED5AA57E59BEFD94E6CDD5522
	5ACBBE751EC04F8A97F11B0DB089AEFE4F6131CBFE57FBA5C5E5A5C565BB37E2DC36539817F4B32E6BBFBA2DDF577FDE502E4BBD27756B7A65E169371E562F6B6B16EB539FBF2DDF5723BC0BE1EFBB411CF6A9246E23DF51CE4F48867BC4657C25CF6075701C667EF66F45D83F04371707AF2A8D651DEFC3647D2A4C86737D2A179B4C772B0483431FA3309E087C7D4B3A835A6FDF581C77F4AB7D86896ACBB2D0BAD096D05EADB85F3D6DG29036CA418FE2EFB4061A26F3A48D7F8B25A9F5398B956AB9AC77EF6
	AF36111EA3774B325FCB74A3BE291114176C8FD010E137B612BC8AFADCEE1931AA786D0D249DC8221342EC0D7EBFF44F2798622A3A85576D85A1DC37B7F06CE48B72CD64D8D28B7245F14CF5827916F2EC6E8964DB46311B87112F69D644DE9EC43EB60EADEDC73EF80E7D358379E6F36CAE877265CCC1EC855D27480767E20E75FB689A991E5BB8B64B097AA40EDD66A6589CF8DE4031FA4A17814F565B90DB69C23B1BB866F6F13B9CDBC4319AF86E64D8399BFD11B9564AFD994F31EC6ECBAA47BC2251970B93
	FDD94C31EEA76EBBE7F16CD1275117671D38971D779D44B65B5017A20EDDB0317B9A379B60FC7D5C6E90471EB4317BB2379B4B79622CE8F7AA47EE68B65AFD4F01FC191C4FC67DEB78AE146FF7F16E762C28761D991A7B3326735D3FEDBFFA55C53541E2BE4773DF7AAE6643D3CE2C03839C7B57EEE37E3F580DF5B00673E571FC0D63582F3A318EAED5B10731CC3E92F9BB0C63F5DF443A104F315DA6F561CBDE97B6F33E6BF35117AA0EBD5DEB346B12D0F6BC67E35FCD91DF26F06C35DD28AF1EE3AB3771B642
	319FEF43B334F937A3E65D0E32E537E35DFAA2C72DCF291AFBFB4E452FDB67AF2A8D96F3FBAE6661981E8FF3F81944F04CA6A15FA20E75C848174131A3BDD8CED5F7A0F6B9AD27A6F86E60586FAAD1564D3178D2AC27A40E39F6203E940E9DBE0C7A52B8B663B05F0B66D84BEE7ECE6ECE44B6FC047A1AB966647A9C9C7BB1579760D8E1A56ACB6658D394DBG4F699C2B61F5F0A9477E65C30255CE0576B0953121E21415B976CCB14A0E6158D881FD1E4F319BD7F23B9CFBE9A5374B315FB950675A3B901BCE5B
	EB8BBCBBB8360E0FCB810EBD44B0F8CE63589ABA4EE542F3615DBC2FDC282F04E345EE5E2EB9F68335BB9E1E63B8362147E82326C7EB034D710F14633B0B590011732DE4CDC2B076EF69BEADF64B4D645F3CFDECED31648461E0EB157F4843BD53CDB36E949A1EEA30103343E6EB1545F7ABFBC577286F92B7AFAF4AE6EDD358B9521E2DFC0F59BAF56D08F27A6F07185C27A34A69096D03245375DD6CBD7A7D11A5941FF20FFEFF645CCE5C9F0907343CF8FC877BC43BF60D17FF0BE4C1B93D5DC03C3297912F7F
	F66B03B6D2B60900274463BCFB5691F2767B043D47F88A97406620BFA08665C10652AF1CB19FCF0FB6BF183D2E564873F4CF6B487374E0ABFB176292DB86834A79347ED672AE486FF406C43E9E98EA4D675821D0331F9C8A727B14F3EBC13A8E84C32CFE5EF40A64B05BD7589642FD05457146FD854CF372BE12689226017CB4362FC05F6BCED34B1369FE65342A7B099654DDB44DDCF73606D0771BCADA25C177FB9D2A6EE7CF206E5A883AD78A6AEFA8E8F5933C45207B40972A6E7567D1F7E7845D394378AD9B
	F43B4F283A5D9D28FBCC845DEB0651BD51207B6AAF8581577BEF09E47F47EB9F70F87B3C74B09AFB27360B7603F1A01BB08D5F3DDD6BA1F682C151A9860322039DDDD3786F21EB1AD460CD87E2FE7F4809FE67CFB75FFFDA2971FB737470FAD2B5DD3F7FB445CDFD7578F43E4E267DF88370B7CFC7DFCFBBC9BF6B7687BD677DBE59AE55C99E170F2C10959959097BB1C353F1BFE9D627406319C0470B84C8CB62BADFF43D962613EC04E12BC13935EE541B8EF28CBBCD31029930061901F9A3713CE91AE14C1BDC
	DD5EF44E884F9BF9061AB74CC68935B186701899984F7AAEB51E98A73E3BCD1A01FE2D23B12642F3BA77EB0F8D7DAAC8B0AFB31BC9DDC33F0A9374F52D32DB75A10E7BD01900BE3C4F46E5F86ECCC09F9E34228F31A6BE640E6043D40D8FCC5F478E5417E8220F70ED5434214700A7B5C15D87DD1C202F07C52271DD75C7A216FBD682667FC9079A3BC0634C1B89730519987BA6BA6FE900676619987BBA91FD9D1AE93E7F2C2D9731B3F5314F54570B5554C6A260F3E622DF3459AA69393CCCB238DF63A9E61D35
	50D6B08B7D1A160BFEB54CB277EB38B2E91B2577AB164E4B9D0087E621DF37B985DEE65BF7213DB893FB59CAF92D0EE0EF3641DE59B60CB3995258D908257C0CE49AE0D90A1D12FB59F7FE4C0FF73673386FB57A31DAD79EED40A35D2B561DB1776A5BC332CB0D7167BCCFE3CCF49319F5A6F90A7DFA1CC177F3EED577D889F5A7C4503DFE985D49776A732F1F4EF7D3GCF3D977B435BAA436788ABFAD47B96DEFE56C47372DB27A93FCD40C3884B2FACD1EF7F49D2AC3F1AC42C2731F45D55814F6EC42CC3DBFB
	D59FFEF898FD189C410775437810E0706142EE5E07F29FEA68DAAD951E53398FCD126A43AF398F8577197B201D97E8FDA0F60B6F537BD0D7497B507B309CDE5015035F2B5ABF4E4B2145447EF2012DC7306FF3838F21B7943F066E5377FD8BBF9CFEDC4D7C907B417177E1FEDC6FD37D49ACC6FF12A27803652175876BE50AA1BF265335D4AA60695C5E83FE555E9ABE16945CEF1E7F2BB5B6519E9F4F6E575BAB90F97E5F0F797F4FE2F87E2786D47B9F70F13E4544BE69B3357D079BF8066ED76D475E2F4F7F79
	A34CEBFE4B67B5D338FF1B2318376CF321DC4A7D180FFFBA2B46314F0DF1AC0B9007367F9F5245117FBDFD9C53C573B3E6BF24793B89780B3F077BDB474B356B280300F7FCEF786F0159DA6EB7C162F3GF8E30092016682AD824A84324E063686D486F4902893C8869A8FB485A881A899E841ECDC876EBCC93E150D348ED48E905317AEC13B073CF6D13EF1CC5EC05A1747A7F9FCE2BD7B15C381574C3FA5C7C7157B97B577C3BBAF7E5E5D0D975586AB0323A49B1475664049AD11722F2ECBBEECBFE7BF5A55A3
	F64BADF697175956C6F471F7EAECF92D246DECAB90B43F0744FE9B8D98FDAE369F5391427D180C59E67BB1B8EE5E5BC76E190C500AB2C58FE0BB7859B9B5BD7FA426377174BF762B697B4FE3FAACCFAF99D453EF6E407439BC7D68F9551779DF893C0DC89E324F92760E394163A59F6F78FD453B4BCB0A4AAB37225C0BC32A5C1D4EE86418BFAFB9519F617B66FBD6CC7FEBDF2BBEFE7C0DEA2BAAAADB0C77EAA779A6DAF3743B74B879BF3E268C15569FB852F2609837C3176E0A6C132E686DECF10F4ECE0EC535
	F3F894F9F79B4F3B2AE1736E43ECB56F68F1D9455699B17A189EF0C6BA5F4074BEEBD375965AC6F7B6C132E19C016FE31D2447B495FD73C630FDE60D4ABB51BABA5B99D6349D4FED0FDD2B36179EFEB6BEADC29EC74E77296BD43F0E085A338EAC7D4779EA7ABFF5C75F56FE3BDE15DB62083E3E540A98CB6103435517B59B547AD2D6205AFAAAAADB0C37F404B2FB7DF5D56F653923AB337FAD40B81A9F44B22BAED4FD1E74FF2A6EE9152353FDE6976A9E4FF5BF74262A6F5436C8F1313D6142925C9BCE86792C
	87718C126BBDB27630B3C8FEEF29274BDB5AF74EDBEA6F12CB49C23E3A7AC81FA4B5A91F638A4A3791D4D7D9924C8F00BA13F01F3FF9BB7AB810E4D60E1BEBA30FB50BB629F5BBE7856AC9CC42D8DFF9CBCD3F1F7FB6E486CFFF6F9DD557576F2A79F2FE1479DC41733968216146465B5FD3ED3CDCBAFC1DFAF70B4A7B496151797362E1BE2EBE0431B6FC206A3BFB777036630AD55E5323343D6EB3BEE6F35B7F32D55557D39949B6139F4773F26E30F9797CDFD41D2B0B239D23180DADDB79B87DB07AE8ABD1F5
	776CD0F5DF39F2F471FF427B60AA2E7B1AB2D55FDEF17878CF72F1B070F034FDB42F43BBD5BB4BDD51668963BF91B5BF73B36066E38D0F73768A5517153BB4655393A9E6653DB2FD571776B0DB57B04CCF31AC85D3FE93A49879DDCECEE864F7B9EBC266E72EB6040CE72E8AC366E72E5E2673E0BCF3257CD6847D1D9A972CD95AB8DFB147CEF1ECBB55C930139E076C66F885750B6087C5E290710F29DF7D94EBD330CFB458A9852B8F1177D190B2ACCBD176E008FECB6411A4765CC4F9BC3E10A8737C78AB7551
	9330EB7CBE41B16237F276A124DDB396BC826B49C706DFB3B2395CD62DDC8D48B4C7A5377124D64E81B283D149FDFADEAB9783B2F1D149FD27C3ABB79BE466C6A577A727D6EE9148AC0DCA6E0EB0391CB98BED05F32211BBEB534A1500CCEDD4F2CB2CE1E748C1469D151CBF6C9B09A110090DCA6EC058596C29A01394151CE523D6AE85E45222126B5E25154B84996B2351483DDD726D3ECFAB99351C726D821D83EDFA54706D82450B9FB5FC3B4067DF1510B679D15C43E95D2E353F8C707C9FC4932FED05D6EE
	B348D4C5A577E058B79ECDA0539915DCD5D83948A0B3BEAA392C43DA39A9A0139815DC41E7DA3947C0A6B5AA39AB437CDC8CB242BFC5A377172DE17DA24894C7A5D79F569EEBC026B9AA390E30FE4381B283D1497539437AC510099BC10E4D736D9FE8FEA31B8ED98C5FB430C645F148D2466D0110F1AC3F90520E658C5B54EA64FB375548D7F25248D7FE5248F7663C112F7B3C116F018EA35F439DC63ED7DDC63E57DDC6BEF6E6AA1C6F87CEA35FF81B116F9A1B11AF47044FE642F739837936E6A3F635837956
	D8906B900D7A6C22D15F9B9B0DFCEFEFB4721D5FE5647BE61716CFB9C7C967534AB9CA4D3D3D647F43B94A0BEA0345ACEDB76643596D467CAADAE1645B32424877D3494877F34948F754A47FDBF6997958BAAC1C2F60B3A35FB6933EB2933EB8933BF719582D5BEA64EB5CEA64BBEDB572FDE9B57205CC5A5700C97B0AF19B79A63835FC6C6F3FCDF59D03CEADE109109B8A8AEB4357A44B6FFA7D527C6BAA7AAC0235D1D86134CB81377D1010CBFFC43DC9D85DC5D783E290F8E5A13F09486EG599DD4F6CF13E0
	698E49421BAA5ECDF016FABCE06F960145663087FB29063342574272F641668C3A3A2881D93034B73603D97AC412331B04D5321BFCF9C2DE0749426B2A6AE672CEC4C958B3C0DF113D827F8568BF88ECCD58E275C7376FFABE6517A7C259420AC3429B6477850E0BC176E78F9C6467A3153F6354A85882FE50CBCE839D0285953D640664C18922AFF09EA3FFFBA4A40B8E7E9B19906EC666D31F6C11F69C34075C749487841A63A3BF9BAD3C4A12A53B4F35033DCDE05EAE2F2BA5BE8B74F774415F8D54DF222514
	EB5970483AAD6F9ECECF4A96AC0D42AA49EFF7B45A3BE5FF7010E0135DC1B16476CB0E0140BE26485ACF0A1A3EFA84F5EF7270C9302574A3BBF43AF7603618B599EB9A21079C888ABBA8A74946D24E6128F75B03E0CA8C9E1249DF507287B7733431EF4B9FBC7E696B9D96E13992A4A9A96BC0C0A695466B1186612601EE8D04C8CA1E8C5E866D3E10539F7402E7C5BA4FB0099BA83DE1DE590D53DEBECD8D282232700EDA7AAE516F5DD167F4FAFCA2D7G798A6A5F626A89CF290E675501899B8F7F5BABDDD08F
	9A04ECEFD9997DFFB97D7FCE727FF20A14D324FCA7E46ED2E158FF7A74810C19D69E70E79D750794FFE9A314203CF0493CBB7E3DE669E2082ED1E9D8D08879AA5711BEEF17983CBC614637D6CFCC3B1A14D100D4158122B7FF00EBA4919E22EB5934F39555594F5D19ED91F28E89AB585A9EAAAFD920C36525AD7610108DEBFBDA6FF234A48B9B0E9F7A3C35292E655051A3C547DBBF7F3C6E5829EAA8B072138EE4EB816EF349EF5941552A7CD69C5C2EE4BFE1C792C364C775E4A1DB74B9E4E1F991A32B2C7C7C
	1D8A2D45DF28ABA279C2A449CF52D52B8C04362BFF262D285FA33B0B589FD9AA6AF6C346080EA2725315C58E51E96F1328EBF91A1F1ED3D42D7053DF16A38F34A45E5207FC515020702AF19F3D6E5A271FCA78322140A2D820EC5CA2AB0C5CFEB2132445C106533DD4E3F5581339556AE1A4C6AF3F42508D91F41531BFA170DA5E2A4953FA4A3A017E7F35D13791A2D5C8E57F0FD76D838343787DF765E953F01E0E3E5C7E61CA2A6D9F26243E9CFDC90D867D3B0A55738FD3AA01FF98CF7B7FD675676FAA2AB1FF
	2B2868BFF5524D7F6651B75A6FE632AE6B0E785B8849A9EC8FE6C10A7E3B195F53F5DF7E638BEDA50F63F929BA097C0D04C867FD0EFB6D12A45B255EBA1F031C7D59BFA80B6A3772AFC9C4FF88F914C4651D0AC47C6E51487CBFD0CB87887BC891D830A5GG04FBGGD0CB818294G94G88G88G43DAB1B67BC891D830A5GG04FBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6AA5GGGG
**end of data**/
}
}