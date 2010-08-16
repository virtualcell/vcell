package cbit.vcell.solver.ode.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.ISize;

import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.Constant;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.stoch.StochSimOptions;
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
	private JLabel ivjJLabel5 = null;
	private JLabel ivjJLabel6 = null;
	private JLabel ivjJLabel7 = null;
	private JLabel ivjJLabel8 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private MathOverridesPanel ivjMathOverridesPanel1 = null;
	private JLabel ivjJLabelEndTime = null;
	private JLabel ivjJLabelGeometrySize = null;
	private JLabel ivjJLabelMesh = null;
	private JLabel ivjJLabelSolver = null;
	private JLabel ivjJLabelStartTime = null;
	private JLabel ivjJLabelTimestep = null;
	private JTextArea ivjJTextAreaDescription = null;
	private JLabel ivjJLabel10 = null;
	private JLabel ivjJLabel9 = null;
	private JLabel ivjJLabelSensitivity = null;
	private JLabel ivjJLabel13 = null;
	private JLabel ivjJLabelOutput = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.text.Document ivjdocument1 = null;
	private JLabel labelRelTol = null;
	private JLabel labelAbsTol = null;
	private JLabel labelRelTolValue = null;
	private JLabel labelAbsTolValue = null;

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

	PropertyChangeListener simChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getSource() instanceof Simulation) {
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
			}else if(event.getSource() instanceof MeshSpecification){
				if(event.getPropertyName().equals("geometry")){
					displayMesh();
				}
			}
		}
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
	if(Compare.isEqualOrNull(getJTextAreaDescription().getText(),getSimulation().getDescription())){
		return;
	}
	try {
		getJTextAreaDescription().setText(getSimulation().getDescription());
		getJTextAreaDescription().setCaretPosition(0);
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
    	boolean isSpatial = getSimulation().isSpatial();
    	getJLabel11().setVisible(isSpatial);
    	getJLabelMesh().setVisible(isSpatial);
        if (getSimulation()!=null && getSimulation().getMeshSpecification() != null) {
            ISize samplingSize = getSimulation().getMeshSpecification().getSamplingSize();
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
	boolean isSpatial = getSimulation().isSpatial();

	try {
		getJLabel8().setVisible(isSpatial);
		getJLabelGeometrySize().setVisible(isSpatial);
		Extent extent = getSimulation().getMathDescription().getGeometry().getExtent();
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
	SolverTaskDescription solverTaskDescription = getSimulation().getSolverTaskDescription();
	TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
	try {
		getJLabelStartTime().setText(Double.toString(timeBounds.getStartingTime()));
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelStartTime().setText("");
	}
	try {
		getJLabelEndTime().setText(Double.toString(timeBounds.getEndingTime()));
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelEndTime().setText("");
	}
	try {
		StochSimOptions stochOpt = solverTaskDescription.getStochOpt();
		if(stochOpt != null && stochOpt.getNumOfTrials() > 1 )
		{
			getJLabelOutput().setText("Histogram with "+stochOpt.getNumOfTrials()+" Trials(@last time point)");
		}
		else
		{
			String text = solverTaskDescription.getOutputTimeSpec().getShortDescription();
			if (solverTaskDescription.getOutputTimeSpec().isDefault() && !solverTaskDescription.getSolverDescription().isSemiImplicitPdeSolver() 
					&& !solverTaskDescription.getSolverDescription().equals(SolverDescription.StochGibson)) {
				text += ", at most " + ((DefaultOutputTimeSpec)solverTaskDescription.getOutputTimeSpec()).getKeepAtMost();
			}
			getJLabelOutput().setText(text);
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelOutput().setText("");
	}
	SolverDescription solverDescription = solverTaskDescription.getSolverDescription();
	try {
		ErrorTolerance errorTolerance = solverTaskDescription.getErrorTolerance();
		TimeStep timeStep = solverTaskDescription.getTimeStep();
		if (solverDescription.equals(SolverDescription.StochGibson)) {
			getJLabel12().setEnabled(false);
			getJLabelTimestep().setText("");
		} else if (solverDescription.isNonSpatialStochasticSolver()) {
			getJLabel12().setEnabled(true);
			getJLabel12().setText("timestep");		
			getJLabelTimestep().setText(timeStep.getDefaultTimeStep()+ "");
		} else if (solverDescription.hasVariableTimestep()) {
			getJLabel12().setEnabled(true);
			getJLabel12().setText("max timestep");
			getJLabelTimestep().setText(timeStep.getMaximumTimeStep()+ "");
			getJLabelRelTol().setEnabled(true);
			getJLabelRelTolValue().setText("" + errorTolerance.getRelativeErrorTolerance());
			getJLabelAbsTol().setEnabled(true);
			getJLabelAbsTolValue().setText("" + errorTolerance.getAbsoluteErrorTolerance());
		} else {
			getJLabel12().setEnabled(true);
			getJLabel12().setText("timestep");
			getJLabelTimestep().setText(Double.toString(timeStep.getDefaultTimeStep()));
			if (solverDescription.isSemiImplicitPdeSolver()) {
				getJLabelRelTolValue().setText("" + errorTolerance.getRelativeErrorTolerance());
			} else {
				getJLabelRelTol().setEnabled(false);
				getJLabelRelTolValue().setText("");
			}
			getJLabelAbsTol().setEnabled(false);
			getJLabelAbsTolValue().setText("");			
		}			
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelTimestep().setText("");
		getJLabelRelTolValue().setText("");
		getJLabelAbsTolValue().setText("");
	}
	try {
		if (getSimulation().isSpatial() || solverDescription.isNonSpatialStochasticSolver()) {
			getJLabelSensitivity().setVisible(false);
			getJLabel10().setVisible(false);
		} else {
			Constant param = solverTaskDescription.getSensitivityParameter();
			if (param == null) {
				getJLabelSensitivity().setText("no analysis");
			} else {
				getJLabelSensitivity().setText("analysis for parameter: " + param.getName());
			}
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelSensitivity().setText("");
	}
	try {
		getJLabelSolver().setText(solverDescription.getDisplayLabel());
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelSolver().setText("");
	}
	if (solverDescription.isNonSpatialStochasticSolver()) {
		getJLabelRelTol().setVisible(false);
		getJLabelAbsTol().setVisible(false);
		getJLabelRelTolValue().setText("");
		getJLabelAbsTolValue().setText("");
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
		} catch (java.lang.Throwable ivjExc) {
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


private javax.swing.JLabel getJLabelRelTol() {
	if (labelRelTol == null) {
		try {
			labelRelTol = new javax.swing.JLabel("rel tol");
			labelRelTol.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			labelRelTol.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return labelRelTol;
}

private javax.swing.JLabel getJLabelAbsTol() {
	if (labelAbsTol == null) {
		try {
			labelAbsTol = new javax.swing.JLabel("abs tol");
			labelAbsTol.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			labelAbsTol.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return labelAbsTol;
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
			ivjJLabel11.setVisible(false);
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
private javax.swing.JLabel getJLabel12() {
	if (ivjJLabel12 == null) {
		try {
			ivjJLabel12 = new javax.swing.JLabel();
			ivjJLabel12.setName("JLabel12");
			ivjJLabel12.setText("timestep");
			ivjJLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabel2.setText("Annotation:");
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
			ivjJLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}


/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("end");
			ivjJLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabel8.setVisible(false);
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
			ivjJLabel9.setText("<html><b><u>Parameters with values changed from defaults</u></b></html>");
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
private javax.swing.JLabel getJLabelEndTime() {
	if (ivjJLabelEndTime == null) {
		try {
			ivjJLabelEndTime = new javax.swing.JLabel();
			ivjJLabelEndTime.setName("JLabelEndTime");
			ivjJLabelEndTime.setText(" ");
			ivjJLabelEndTime.setForeground(java.awt.Color.blue);
			ivjJLabelEndTime.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelEndTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelEndTime;
}

private javax.swing.JLabel getJLabelRelTolValue() {
	if (labelRelTolValue == null) {
		try {
			labelRelTolValue = new javax.swing.JLabel("");
			labelRelTolValue.setForeground(java.awt.Color.blue);
			labelRelTolValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			labelRelTolValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return labelRelTolValue;
}

private javax.swing.JLabel getJLabelAbsTolValue() {
	if (labelAbsTolValue == null) {
		try {
			labelAbsTolValue = new javax.swing.JLabel("");
			labelAbsTolValue.setForeground(java.awt.Color.blue);
			labelAbsTolValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			labelAbsTolValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return labelAbsTolValue;
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
			ivjJLabelGeometrySize.setText(" ");
			ivjJLabelGeometrySize.setForeground(java.awt.Color.blue);
			ivjJLabelGeometrySize.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelGeometrySize;
}


/**
 * Return the JLabelMesh property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelMesh() {
	if (ivjJLabelMesh == null) {
		try {
			ivjJLabelMesh = new javax.swing.JLabel();
			ivjJLabelMesh.setName("JLabelMesh");
			ivjJLabelMesh.setText(" ");
			ivjJLabelMesh.setForeground(java.awt.Color.blue);
			ivjJLabelMesh.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
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
private javax.swing.JLabel getJLabelSensitivity() {
	if (ivjJLabelSensitivity == null) {
		try {
			ivjJLabelSensitivity = new javax.swing.JLabel();
			ivjJLabelSensitivity.setName("JLabelSensitivity");
			ivjJLabelSensitivity.setText(" ");
			ivjJLabelSensitivity.setForeground(java.awt.Color.blue);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabelSolver.setText(" ");
			ivjJLabelSolver.setForeground(java.awt.Color.blue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelSolver;
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
			ivjJLabelStartTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabelTimestep.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimestep;
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
			ivjJTextAreaDescription.setForeground(java.awt.Color.blue);
			ivjJTextAreaDescription.setRows(3);
			ivjJTextAreaDescription.setLineWrap(true);
			ivjJTextAreaDescription.setWrapStyleWord(true);
			ivjJTextAreaDescription.setEditable(false);
			ivjJTextAreaDescription.setEnabled(true);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjMathOverridesPanel1 = new MathOverridesPanel();
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
public Simulation getSimulation() {
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
		setName("SimulationSummaryPanel");
		setLayout(new java.awt.GridBagLayout());
		
		// 0
		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
		constraintsJLabel1.gridwidth = 7;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel1(), constraintsJLabel1);

		// 1
		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
		constraintsJLabel2.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.gridwidth = 6;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 0.1;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);

		// 3
		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 3;
		constraintsJLabel3.gridheight = 2;
		constraintsJLabel3.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel3(), constraintsJLabel3); // Time:

		java.awt.GridBagConstraints constraintsJLabel6 = new java.awt.GridBagConstraints();
		constraintsJLabel6.gridx = 1; constraintsJLabel6.gridy = 3;
		constraintsJLabel6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel6.weightx = 1.0;
		constraintsJLabel6.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel6(), constraintsJLabel6); // start

		java.awt.GridBagConstraints constraintsJLabel7 = new java.awt.GridBagConstraints();
		constraintsJLabel7.gridx = 2; constraintsJLabel7.gridy = 3;
		constraintsJLabel7.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel7.weightx = 1.0;
		constraintsJLabel7.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel7(), constraintsJLabel7); // end
		
		java.awt.GridBagConstraints constraintsJLabel12 = new java.awt.GridBagConstraints();
		constraintsJLabel12.gridx = 3; constraintsJLabel12.gridy = 3;
		constraintsJLabel12.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel12.weightx = 1.0;
		constraintsJLabel12.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel12(), constraintsJLabel12); // timestep
		
		java.awt.GridBagConstraints constraintsJLabel13 = new java.awt.GridBagConstraints();
		constraintsJLabel13.gridx = 4; constraintsJLabel13.gridy = 3;
		constraintsJLabel13.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel13.weightx = 1.0;
		constraintsJLabel13.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel13(), constraintsJLabel13); // output
		
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 5; 
		gbc.gridy = 3;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelRelTol(), gbc); // rel tol

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 6; 
		gbc.gridy = 3;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelAbsTol(), gbc); // abs tol
	
		// 4
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
		
		java.awt.GridBagConstraints constraintsJLabelTimestep = new java.awt.GridBagConstraints();
		constraintsJLabelTimestep.gridx = 3; constraintsJLabelTimestep.gridy = 4;
		constraintsJLabelTimestep.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTimestep.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelTimestep(), constraintsJLabelTimestep);

		java.awt.GridBagConstraints constraintsJLabelOutput = new java.awt.GridBagConstraints();
		constraintsJLabelOutput.gridx = 4; constraintsJLabelOutput.gridy = 4;
		constraintsJLabelOutput.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelOutput.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelOutput(), constraintsJLabelOutput);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 5; gbc.gridy = 4;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelRelTolValue(), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 6; gbc.gridy = 4;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelAbsTolValue(), gbc);
		
		// 5
		java.awt.GridBagConstraints constraintsJLabel10 = new java.awt.GridBagConstraints();
		constraintsJLabel10.gridx = 0; constraintsJLabel10.gridy = 5;
		constraintsJLabel10.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel10.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel10(), constraintsJLabel10);

		java.awt.GridBagConstraints constraintsJLabelSensitivity = new java.awt.GridBagConstraints();
		constraintsJLabelSensitivity.gridx = 1; constraintsJLabelSensitivity.gridy = 5;
		constraintsJLabelSensitivity.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelSensitivity.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelSensitivity(), constraintsJLabelSensitivity);

		//6
		java.awt.GridBagConstraints constraintsJLabel11 = new java.awt.GridBagConstraints();
		constraintsJLabel11.gridx = 0; constraintsJLabel11.gridy = 6;
		constraintsJLabel11.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel11.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel11(), constraintsJLabel11);

		java.awt.GridBagConstraints constraintsJLabelMesh = new java.awt.GridBagConstraints();
		constraintsJLabelMesh.gridx = 1; constraintsJLabelMesh.gridy = 6;
		constraintsJLabelMesh.gridwidth = 3;
		constraintsJLabelMesh.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelMesh.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelMesh(), constraintsJLabelMesh);

		java.awt.GridBagConstraints constraintsJLabel8 = new java.awt.GridBagConstraints();
		constraintsJLabel8.gridx = 4; constraintsJLabel8.gridy = 6;
		constraintsJLabel8.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel8.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel8(), constraintsJLabel8);

		java.awt.GridBagConstraints constraintsJLabelGeometrySize = new java.awt.GridBagConstraints();
		constraintsJLabelGeometrySize.gridx = 5; constraintsJLabelGeometrySize.gridy = 6;
		constraintsJLabelGeometrySize.gridwidth = 2;
		constraintsJLabelGeometrySize.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelGeometrySize.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelGeometrySize(), constraintsJLabelGeometrySize);
		
		// 7
		java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
		constraintsJLabel5.gridx = 0; constraintsJLabel5.gridy = 7;
		constraintsJLabel5.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel5(), constraintsJLabel5);
		
		java.awt.GridBagConstraints constraintsJLabelSolver = new java.awt.GridBagConstraints();
		constraintsJLabelSolver.gridx = 1; constraintsJLabelSolver.gridy = 7;
		constraintsJLabelSolver.gridwidth = 6;
		constraintsJLabelSolver.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelSolver.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelSolver(), constraintsJLabelSolver);		

		// 8
		java.awt.GridBagConstraints constraintsJLabel9 = new java.awt.GridBagConstraints();
		constraintsJLabel9.gridx = 0; constraintsJLabel9.gridy = 8;
		constraintsJLabel9.gridwidth = 7;
		constraintsJLabel9.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel9.weightx = 1.0;
		constraintsJLabel9.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel9(), constraintsJLabel9);

		// 9
		java.awt.GridBagConstraints constraintsMathOverridesPanel1 = new java.awt.GridBagConstraints();
		constraintsMathOverridesPanel1.gridx = 0; constraintsMathOverridesPanel1.gridy = 9;
		constraintsMathOverridesPanel1.gridwidth = 7;
		constraintsMathOverridesPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMathOverridesPanel1.weightx = 1.0;
		constraintsMathOverridesPanel1.weighty = 1.0;
		constraintsMathOverridesPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMathOverridesPanel1(), constraintsMathOverridesPanel1);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
private void newSimulation(final Simulation simulation) {
	refreshDisplay();
	if (simulation==null){
		getJTextAreaDescription().setBackground(getBackground());
		getJTextAreaDescription().setEditable(false);
		getJLabel1().setText(SIM_SUMMARY_LABEL);
		return;
	}else{
		getJTextAreaDescription().setBackground(java.awt.Color.white);
		getJTextAreaDescription().setEditable(true);
		getJLabel1().setText(SIM_SUMMARY_LABEL + (simulation.getKey() == null ? "" : " (SimID=" + simulation.getKey()+
			(simulation.getSimulationVersion() != null && simulation.getSimulationVersion().getParentSimulationReference() != null 
					? ", parentSimRef="+simulation.getSimulationVersion().getParentSimulationReference() : "" ) + ")"));
	}
	// also set up a listener that will refresh when simulation is edited in place
	simulation.removePropertyChangeListener(simChangeListener);
	simulation.addPropertyChangeListener(simChangeListener);
	MeshSpecification meshSpecification = simulation.getMeshSpecification();
	if (meshSpecification != null) {
		meshSpecification.removePropertyChangeListener(simChangeListener);
		meshSpecification.addPropertyChangeListener(simChangeListener);
	}
}


/**
 * Comment
 */
private void refreshDisplay() {
	if (getSimulation() == null){
		getJTextAreaDescription().setText("");
		getJLabelStartTime().setText("");
		getJLabelEndTime().setText("");
		getJLabelTimestep().setText("");
		getJLabelOutput().setText("");
		getJLabelSensitivity().setText("");
		getJLabelSolver().setText("");
		getJLabelGeometrySize().setText("");
		getJLabelMesh().setText("");
		getJLabelRelTolValue().setText("");
		getJLabelAbsTolValue().setText("");
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
public void setSimulation(Simulation simulation) {
	Simulation oldValue = fieldSimulation;
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

}