/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.math.Constant;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
/**
 * Insert the type's description here.
 * Creation date: (5/2/2001 12:17:49 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class SimulationSummaryPanel extends DocumentEditorSubPanel {
	private Simulation fieldSimulation = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
//	private JLabel labelSimKey = null;
	private JLabel ivjJLabel11 = null;
	private JLabel ivjJLabel12 = null;
	private JLabel ivjJLabel8 = null;
	private MathOverridesPanel ivjMathOverridesPanel1 = null;
	private JLabel ivjJLabelGeometrySize = null;
	private JLabel ivjJLabelMesh = null;
	private JLabel ivjJLabelTimestep = null;
	private JTextArea ivjJTextAreaDescription = null;
	private JLabel ivjJLabel10 = null;
	private JLabel ivjJLabelSensitivity = null;
	private JLabel ivjJLabelOutput = null;
	private JLabel labelRelTol = null;
	private JLabel labelAbsTol = null;
	private JLabel labelRelTolValue = null;
	private JLabel labelAbsTolValue = null;
	private JPanel settingsPanel;
	private JLabel meshRefinementLabel;
	private JLabel labelMeshRefinementTitle;
	private JLabel labelFinestMeshTitle;
	private JLabel labelFinestMesh;
	private JLabel labelRefinementRoiTitle;
	private JLabel labelRefinementRoi;
	private JLabel labelViewLevelMeshTitle;
	private JLabel labelViewLevelMesh;
	private JLabel jlabelNumProcessors;
	private JLabel jlabelTitleNumProcessors;
	
	private class IvjEventHandler implements java.beans.PropertyChangeListener, FocusListener {
		public void propertyChange(java.beans.PropertyChangeEvent event) {
			if (fieldSimulation == null) {
				return;
			}
			if (event.getSource() == fieldSimulation) {
				// name is not displayed by this panel so we only need to take care of the rest
				if (event.getPropertyName().equals("description")) {
					displayAnnotation();
				}
				if (event.getPropertyName().equals("solverTaskDescription")) {
					displayTask();
					displayMesh();
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
			} else if(event.getSource() == fieldSimulation.getMeshSpecification()){
				if(event.getPropertyName().equals("geometry")){
					displayMesh();
				}
			} else if(event.getSource() == fieldSimulation.getSolverTaskDescription()){
				refreshDisplay();				
			}
		};
		public void focusGained(FocusEvent e) {			
		}
		public void focusLost(FocusEvent e) {
			updateAnnotation();	
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
    	labelMeshRefinementTitle.setVisible(isSpatial);
			getJLabelMeshRefinement().setVisible(isSpatial);
			labelFinestMeshTitle.setVisible(isSpatial);
			labelFinestMesh.setVisible(isSpatial);
			labelRefinementRoiTitle.setVisible(isSpatial);
			labelRefinementRoi.setVisible(isSpatial);
			labelViewLevelMeshTitle.setVisible(isSpatial);
			labelViewLevelMesh.setVisible(isSpatial);
			
      if (getSimulation()!=null && getSimulation().getMeshSpecification() != null) {
				ISize samplingSize = getSimulation().getMeshSpecification().getSamplingSize();
				String labelText = "";
				int dimension = getSimulation().getMeshSpecification().getGeometry().getDimension();
				switch (dimension) {
					case 0 :
					{
				    labelText = "error: no mesh expected";
				    break;
					}
					default :
					{
				    labelText = GuiUtils.getMeshSizeText(dimension, samplingSize, true) + " elements";
				    break;
					}
				}
				getJLabelMesh().setText(labelText);

        ChomboSolverSpec chomboSolverSpec = getSimulation().getSolverTaskDescription().getChomboSolverSpec();
				if (getSimulation().getSolverTaskDescription().getSolverDescription().isChomboSolver()) {
					int numRefinementLevels = chomboSolverSpec.getNumRefinementLevels();				
					labelMeshRefinementTitle.setVisible(true);
					getJLabelMeshRefinement().setVisible(true);
					labelFinestMeshTitle.setVisible(true);
					labelFinestMesh.setVisible(true);
					
					ISize finestISize = chomboSolverSpec.getFinestSamplingSize(samplingSize);
					String text = GuiUtils.getMeshSizeText(dimension, finestISize, true);
					labelFinestMesh.setText(text);
					boolean bHasRefinement = numRefinementLevels > 0;
					if (bHasRefinement) {
						String refinementText = numRefinementLevels + " level(s)";
						getJLabelMeshRefinement().setText(refinementText);
						labelRefinementRoiTitle.setVisible(true);
						labelRefinementRoi.setVisible(true);
						String roiText = "Membrane ROI(s): " + chomboSolverSpec.getMembraneRefinementRois().size()
								+ "; Volume ROI(s): " + chomboSolverSpec.getVolumeRefinementRois().size() + ";";
						labelViewLevelMeshTitle.setVisible(true);
						labelViewLevelMesh.setVisible(true);
						labelViewLevelMesh.setText(GuiUtils.getMeshSizeText(dimension, chomboSolverSpec.getViewLevelSamplingSize(samplingSize), true));
						labelRefinementRoi.setText(roiText);
					} else {
						labelMeshRefinementTitle.setVisible(false);
						meshRefinementLabel.setVisible(false);
						labelFinestMeshTitle.setVisible(false);
						labelFinestMesh.setVisible(false);
						labelRefinementRoiTitle.setVisible(false);
						labelRefinementRoi.setVisible(false);
						labelViewLevelMeshTitle.setVisible(false);
						labelViewLevelMesh.setVisible(false);
					}
        } else {
        	labelMeshRefinementTitle.setVisible(false);
        	getJLabelMeshRefinement().setVisible(false);
        	labelFinestMesh.setVisible(false);
        	labelFinestMeshTitle.setVisible(false);
					labelRefinementRoiTitle.setVisible(false);
					labelRefinementRoi.setVisible(false);
					labelViewLevelMeshTitle.setVisible(false);
					labelViewLevelMesh.setVisible(false);
        }
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
	try {
		NonspatialStochSimOptions stochOpt = solverTaskDescription.getStochOpt();
		if(stochOpt != null && stochOpt.getNumOfTrials() > 1 )
		{
			if(stochOpt.isHistogram()) {
				getJLabelOutput().setText("Histogram with "+stochOpt.getNumOfTrials()+" Trials(@last time point)");
			}else {
				getJLabelOutput().setText("Average of "+stochOpt.getNumOfTrials()+" trajectories; "+solverTaskDescription.getOutputTimeSpec().getDescription());
			}
		}
		else
		{
			//gcwtodo
			String text = solverTaskDescription.getOutputTimeSpec().getShortDescription();
			if (solverTaskDescription.getSolverDescription().isChomboSolver()) {
				text = "Variable";
				if (solverTaskDescription.getChomboSolverSpec().getTimeIntervalList().size() == 1)
				{
					text = "Every " + solverTaskDescription.getChomboSolverSpec().getLastTimeInterval().getOutputTimeStep() + "s";
				}
			} else if (solverTaskDescription.getOutputTimeSpec().isDefault() && !solverTaskDescription.getSolverDescription().isSemiImplicitPdeSolver() 
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
		getJLabelRelTol().setText("Rel tol");
		getJLabelAbsTol().setText("Abs tol");
		getJLabel12().setText("Timestep");
		getJLabelRelTol().setEnabled(false);
		getJLabelAbsTol().setEnabled(false);
		getJLabel12().setEnabled(false);
		getJLabel10().setText("Sensitivity Analysis");
		getJLabel10().setEnabled(true);
		if (solverDescription.equals(SolverDescription.StochGibson)) {
			getJLabel12().setEnabled(false);
			getJLabelTimestep().setText("");
		} else if (solverDescription.equals(SolverDescription.NFSim)) {
			TimeBounds tb = solverTaskDescription.getTimeBounds();
			double dtime = tb.getEndingTime() - tb.getStartingTime();
			
			if(solverTaskDescription.getOutputTimeSpec() instanceof UniformOutputTimeSpec) {
				UniformOutputTimeSpec uots = (UniformOutputTimeSpec)solverTaskDescription.getOutputTimeSpec();
				double interval = uots.getOutputTimeStep();
				int steps = (int)Math.round(dtime/interval);
				getJLabel12().setEnabled(true);
				getJLabel12().setText("Timepoints");		
				getJLabelTimestep().setText(steps + "");
			} else if(solverTaskDescription.getOutputTimeSpec() instanceof DefaultOutputTimeSpec) {
				DefaultOutputTimeSpec uots = (DefaultOutputTimeSpec)solverTaskDescription.getOutputTimeSpec();
				getJLabel12().setEnabled(true);
				getJLabel12().setText("End Time");		
				getJLabelTimestep().setText(solverTaskDescription.getTimeBounds().getEndingTime()+"");
			} else {
				getJLabel12().setEnabled(false);
				getJLabel12().setText("End Time");		
				getJLabelTimestep().setText("na");
			}
			
			NFsimSimulationOptions nfsso = solverTaskDescription.getNFSimSimulationOptions();
			String utl = "default";
			Integer moleculeDistance = nfsso.getMoleculeDistance();
			if(moleculeDistance != null) {
				utl = moleculeDistance + "";
			}
			getJLabelRelTol().setEnabled(true);
			getJLabelRelTol().setText("Universal Transversal Limit");
			getJLabelRelTolValue().setText(utl);

			String gml = "default";
			Integer maxMoleculesPerType = nfsso.getMaxMoleculesPerType();
			if(maxMoleculesPerType != null) {
				gml = maxMoleculesPerType + "";
			}
			getJLabelAbsTol().setEnabled(true);
			getJLabelAbsTol().setText("Max # of each " + MolecularType.typeName);
			getJLabelAbsTolValue().setText(gml);
		} else if (solverDescription.isNonSpatialStochasticSolver()) {
			getJLabel12().setEnabled(true);
			getJLabel12().setText("Timestep");		
			getJLabelTimestep().setText(timeStep.getDefaultTimeStep()+ "s");
		} else if (solverDescription.hasVariableTimestep()) {
			getJLabel12().setEnabled(true);
			getJLabel12().setText("Max timestep");
			getJLabelTimestep().setText(timeStep.getMaximumTimeStep()+ "s");
			getJLabelRelTol().setEnabled(true);
			getJLabelRelTolValue().setText("" + errorTolerance.getRelativeErrorTolerance());
			getJLabelAbsTol().setEnabled(true);
			getJLabelAbsTolValue().setText("" + errorTolerance.getAbsoluteErrorTolerance());
		} else {
			getJLabel12().setEnabled(true);
			getJLabel12().setText("Timestep");
			if (solverDescription.isChomboSolver())
			{
				String text = "Variable";
				if (solverTaskDescription.getChomboSolverSpec().getTimeIntervalList().size() == 1)
				{
					text = solverTaskDescription.getChomboSolverSpec().getLastTimeInterval().getTimeStep() + "s";
				}
				getJLabelTimestep().setText(text);
			}
			else
			{
				getJLabelTimestep().setText(timeStep.getDefaultTimeStep() + "s");
			}
			if (solverDescription.isSemiImplicitPdeSolver()) {
				getJLabelRelTol().setEnabled(true);
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
		boolean bChomboSolver = solverDescription.isChomboSolver();
		getJLabelTitleNumProcessors().setVisible(bChomboSolver);
		getJLabelNumProcessors().setVisible(bChomboSolver);
		if (bChomboSolver) {
			getJLabelNumProcessors().setText(String.valueOf(solverTaskDescription.getNumProcessors()));
		}
		if (getSimulation().isSpatial() || solverDescription.isNonSpatialStochasticSolver()) {
			getJLabelSensitivity().setVisible(false);
			getJLabel10().setVisible(false);
		} else if(solverDescription.equals(SolverDescription.NFSim)) {
			getJLabel10().setText("On-the-fly observ comp.");
			NFsimSimulationOptions nfsso = solverTaskDescription.getNFSimSimulationOptions();
			boolean goc = nfsso.getObservableComputationOff();
			getJLabelSensitivity().setText(goc+"");
			
		} else {
			getJLabelSensitivity().setVisible(true);
			getJLabel10().setVisible(true);
			Constant param = solverTaskDescription.getSensitivityParameter();
			if (param == null) {
				getJLabelSensitivity().setText("no");
			} else {
				getJLabelSensitivity().setText(param.getName());
			}
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		getJLabelSensitivity().setText("");
	}
	if (solverDescription.isNonSpatialStochasticSolver()) {
		getJLabelRelTol().setVisible(false);
		getJLabelAbsTol().setVisible(false);
		getJLabelRelTolValue().setVisible(false);
		getJLabelAbsTolValue().setVisible(false);
	} else {
		getJLabelRelTol().setVisible(true);
		getJLabelAbsTol().setVisible(true);
	}
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JLabel getJLabelSimKey() {
//	if (labelSimKey == null) {
//		try {
//			labelSimKey = new javax.swing.JLabel();
//			labelSimKey.setName("JLabelSimKey");
//			labelSimKey.addMouseListener(
//					new MouseAdapter(){				
//						JPopupMenu jPopup;
//						ActionListener copyAction =
//							new ActionListener(){
//								public void actionPerformed(ActionEvent e) {
//									if(getSimulation() != null && getSimulation().getKey() != null){
//										VCellTransferable.sendToClipboard(getSimulation().getKey().toString());
//									}
//								}
//							};
//						public void mouseClicked(MouseEvent e) {
//							super.mouseClicked(e);
//							checkMenu(e);
//						}
//						public void mousePressed(MouseEvent e) {
//							super.mousePressed(e);
//							checkMenu(e);
//						}
//						public void mouseReleased(MouseEvent e) {
//							super.mouseReleased(e);
//							checkMenu(e);
//						}
//						private void checkMenu(MouseEvent e){
//							if(getSimulation() != null && e.isPopupTrigger()){
//								if(jPopup == null){
//									jPopup = new JPopupMenu();
//									JMenuItem jMenu = new JMenuItem("Copy SimID");
//									jMenu.addActionListener(copyAction);
//									jPopup.add(jMenu);
//								}
//								jPopup.show(e.getComponent(), e.getX(), e.getY());
//							}
//						}
//					}
//			);
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//	}
//	return labelSimKey;
//}


/**
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel10() {
	if (ivjJLabel10 == null) {
		try {
			ivjJLabel10 = new javax.swing.JLabel("Sensitivity Analysis");
			ivjJLabel10.setName("JLabel10");
			ivjJLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel10;
}


private javax.swing.JLabel getJLabelRelTol() {
	if (labelRelTol == null) {
		try {
			labelRelTol = new javax.swing.JLabel("Rel tol");
			labelRelTol.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return labelRelTol;
}

private javax.swing.JLabel getJLabelAbsTol() {
	if (labelAbsTol == null) {
		try {
			labelAbsTol = new javax.swing.JLabel("Abs tol");
			labelAbsTol.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
private javax.swing.JLabel getJLabel11() {
	if (ivjJLabel11 == null) {
		try {
			ivjJLabel11 = new javax.swing.JLabel("Mesh:");
			ivjJLabel11.setName("JLabel11");
			ivjJLabel11.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabel12 = new javax.swing.JLabel("Timestep");
			ivjJLabel12.setName("JLabel12");
			ivjJLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel12;
}

/**
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel8() {
	if (ivjJLabel8 == null) {
		try {
			ivjJLabel8 = new javax.swing.JLabel("Geometry size:");
			ivjJLabel8.setName("JLabel8");
			ivjJLabel8.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel8;
}

private javax.swing.JLabel getJLabelRelTolValue() {
	if (labelRelTolValue == null) {
		try {
			labelRelTolValue = new javax.swing.JLabel();
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
			labelAbsTolValue = new javax.swing.JLabel();
			labelAbsTolValue.setForeground(java.awt.Color.blue);
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
private javax.swing.JLabel getJLabelGeometrySize() {
	if (ivjJLabelGeometrySize == null) {
		try {
			ivjJLabelGeometrySize = new javax.swing.JLabel();
			ivjJLabelGeometrySize.setName("JLabelGeometrySize");
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
private javax.swing.JLabel getJLabelOutput() {
	if (ivjJLabelOutput == null) {
		try {
			ivjJLabelOutput = new javax.swing.JLabel();
			ivjJLabelOutput.setName("JLabelOutput");
			ivjJLabelOutput.setForeground(java.awt.Color.blue);
			ivjJLabelOutput.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabelSensitivity.setForeground(java.awt.Color.blue);
			ivjJLabelSensitivity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelSensitivity;
}

/**
 * Return the JLabel13 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelTimestep() {
	if (ivjJLabelTimestep == null) {
		try {
			ivjJLabelTimestep = new javax.swing.JLabel();
			ivjJLabelTimestep.setName("JLabelTimestep");
			ivjJLabelTimestep.setForeground(java.awt.Color.blue);
			ivjJLabelTimestep.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimestep;
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

private JPanel getSettingsPanel() {
	if (settingsPanel == null) {
		settingsPanel = new JPanel(new GridBagLayout());
    
		int gridy = 0;
		int gridx = 0;
		GridBagConstraints constraintsJLabel12 = new GridBagConstraints();
		constraintsJLabel12.gridx = gridx; 
		constraintsJLabel12.gridy = gridy;
		constraintsJLabel12.ipadx = 12;
		constraintsJLabel12.ipady = 4;
		constraintsJLabel12.fill = GridBagConstraints.BOTH;
		settingsPanel.add(getJLabel12(), constraintsJLabel12); // timestep
		getJLabel12().setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		GridBagConstraints constraintsJLabel13 = new GridBagConstraints();
		constraintsJLabel13.gridx = gridx; 
		constraintsJLabel13.gridy = gridy;
		constraintsJLabel13.fill = GridBagConstraints.BOTH;
		constraintsJLabel13.ipadx = 12;
		constraintsJLabel13.ipady = 4;
		JLabel label = new JLabel("Output", javax.swing.SwingConstants.CENTER);
		settingsPanel.add(label, constraintsJLabel13); // output
		label.setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx; 
		gbc.gridy = gridy;
		gbc.ipadx = 12;
		gbc.ipady = 4;
		gbc.fill = GridBagConstraints.BOTH;
		settingsPanel.add(getJLabelRelTol(), gbc); // rel tol
		getJLabelRelTol().setBorder(GuiConstants.TAB_PANEL_BORDER);

		++ gridx;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx; 
		gbc.gridy = gridy;
		gbc.ipadx = 12;
		gbc.ipady = 4;
		gbc.fill = GridBagConstraints.BOTH;
		settingsPanel.add(getJLabelAbsTol(), gbc); // abs tol	
		getJLabelAbsTol().setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		GridBagConstraints constraintsJLabel10 = new GridBagConstraints();
		constraintsJLabel10.gridx = gridx; 
		constraintsJLabel10.gridy = gridy;
		constraintsJLabel10.fill = GridBagConstraints.BOTH;
		constraintsJLabel10.ipadx = 12;
		constraintsJLabel10.ipady = 4;
		settingsPanel.add(getJLabel10(), constraintsJLabel10);  // sensitivity analysis
		getJLabel10().setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx; 
		gbc.gridy = gridy;
		gbc.ipadx = 12;
		gbc.ipady = 4;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		settingsPanel.add(getJLabelTitleNumProcessors(), gbc);
		getJLabelTitleNumProcessors().setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		settingsPanel.add(new JLabel(), gbc);
		
		gridy ++;	
		gridx = 0;
		GridBagConstraints constraintsJLabelTimestep = new GridBagConstraints();
		constraintsJLabelTimestep.gridx = gridx; 
		constraintsJLabelTimestep.gridy = gridy;
		constraintsJLabelTimestep.fill = GridBagConstraints.BOTH;
		constraintsJLabelTimestep.ipadx = 12;
		constraintsJLabelTimestep.ipady = 4;
		settingsPanel.add(getJLabelTimestep(), constraintsJLabelTimestep);
		getJLabelTimestep().setBorder(GuiConstants.TAB_PANEL_BORDER);

		++ gridx;
		GridBagConstraints constraintsJLabelOutput = new GridBagConstraints();
		constraintsJLabelOutput.gridx = gridx; 
		constraintsJLabelOutput.gridy = gridy;
		constraintsJLabelOutput.fill = GridBagConstraints.BOTH;
		constraintsJLabelOutput.ipadx = 12;
		constraintsJLabelOutput.ipady = 4;
		settingsPanel.add(getJLabelOutput(), constraintsJLabelOutput);
		getJLabelOutput().setBorder(GuiConstants.TAB_PANEL_BORDER);

		++ gridx;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx; 
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.ipadx = 12;
		gbc.ipady = 4;
		settingsPanel.add(getJLabelRelTolValue(), gbc);
		getJLabelRelTolValue().setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx; 
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.ipadx = 12;
		gbc.ipady = 4;
		settingsPanel.add(getJLabelAbsTolValue(), gbc);
		getJLabelAbsTolValue().setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		GridBagConstraints constraintsJLabelSensitivity = new GridBagConstraints();
		constraintsJLabelSensitivity.gridx = gridx; 
		constraintsJLabelSensitivity.gridy = gridy;
		constraintsJLabelSensitivity.fill = GridBagConstraints.BOTH;
		constraintsJLabelSensitivity.ipadx = 12;
		constraintsJLabelSensitivity.ipady = 4;
		settingsPanel.add(getJLabelSensitivity(), constraintsJLabelSensitivity);
		getJLabelSensitivity().setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		++ gridx;
		gbc = new GridBagConstraints();
		gbc.gridx = gridx; 
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.ipadx = 12;
		gbc.ipady = 4;
		settingsPanel.add(getJLabelNumProcessors(), gbc);
		getJLabelNumProcessors().setBorder(GuiConstants.TAB_PANEL_BORDER);
	}
	return settingsPanel;
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("SimulationSummaryPanel");
		setLayout(new java.awt.GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints constraintsJLabel2 = new GridBagConstraints();
		constraintsJLabel2.gridx = 0; 
		constraintsJLabel2.gridy = gridy;
		constraintsJLabel2.anchor = GridBagConstraints.LINE_END;
		constraintsJLabel2.insets = new Insets(4, 4, 4, 4);
		add(new JLabel("Annotation:"), constraintsJLabel2);

		GridBagConstraints constraintsJScrollPane1 = new GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; 
		constraintsJScrollPane1.gridy = gridy;
		constraintsJScrollPane1.gridwidth = GridBagConstraints.REMAINDER;
		constraintsJScrollPane1.fill = GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.insets = new Insets(4, 4, 4, 4);
		add(new JScrollPane(getJTextAreaDescription()), constraintsJScrollPane1);
				
		gridy ++;
		GridBagConstraints constraintsJLabel3 = new GridBagConstraints();
		constraintsJLabel3.gridx = 0; 
		constraintsJLabel3.gridy = gridy;
		constraintsJLabel3.anchor = GridBagConstraints.EAST;
		constraintsJLabel3.insets = new Insets(4, 4, 4, 4);
		add(new JLabel("Settings:"), constraintsJLabel3); // Time:

		GridBagConstraints  gbc = new GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(getSettingsPanel(), gbc); 	
		
		gridy ++;
		GridBagConstraints constraintsJLabel11 = new GridBagConstraints();
		constraintsJLabel11.gridx = 0; 
		constraintsJLabel11.gridy = gridy;
		constraintsJLabel11.anchor = GridBagConstraints.EAST;
		constraintsJLabel11.insets = new Insets(4, 4, 4, 4);
		add(getJLabel11(), constraintsJLabel11); // Mesh:

		GridBagConstraints constraintsJLabelMesh = new GridBagConstraints();
		constraintsJLabelMesh.gridx = 1; 
		constraintsJLabelMesh.gridy = gridy;
		constraintsJLabelMesh.weightx = 1.0;
		constraintsJLabelMesh.gridwidth = 2;
		constraintsJLabelMesh.fill = GridBagConstraints.HORIZONTAL;
		constraintsJLabelMesh.insets = new Insets(4, 4, 4, 4);
		add(getJLabelMesh(), constraintsJLabelMesh);

		GridBagConstraints constraintsJLabel8 = new GridBagConstraints();
		constraintsJLabel8.gridx = 3; 
		constraintsJLabel8.gridy = gridy;
		constraintsJLabel8.anchor = GridBagConstraints.EAST;
		constraintsJLabel8.insets = new Insets(4, 4, 4, 4);
		add(getJLabel8(), constraintsJLabel8); // Geometry Size

		GridBagConstraints constraintsJLabelGeometrySize = new GridBagConstraints();
		constraintsJLabelGeometrySize.gridx = 4; 
		constraintsJLabelGeometrySize.gridy = gridy;
		constraintsJLabelGeometrySize.weightx = 1.0;
		constraintsJLabelGeometrySize.fill = GridBagConstraints.HORIZONTAL;
		constraintsJLabelGeometrySize.insets = new Insets(4, 4, 4, 4);
		add(getJLabelGeometrySize(), constraintsJLabelGeometrySize);

		gridy ++;
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0; 
		constraints.gridy = gridy;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(4, 4, 4, 4);
		labelMeshRefinementTitle = new JLabel("Mesh Refinement:");
		add(labelMeshRefinementTitle, constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridx = 1; 
		constraints.gridy = gridy;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(4, 4, 4, 4);
		add(getJLabelMeshRefinement(), constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridx = 3; constraints.gridy = gridy;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(4, 4, 4, 4);
		labelFinestMeshTitle = new JLabel("Finest Level Mesh:");
		add(labelFinestMeshTitle, constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridx = 4; constraintsJLabelMesh.gridy = gridy;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(4, 4, 4, 4);
		labelFinestMesh = new JLabel();
		labelFinestMesh.setForeground(java.awt.Color.blue);
		add(labelFinestMesh, constraints);
		
		gridy ++;
		constraints = new GridBagConstraints();
		constraints.gridx = 0; constraints.gridy = gridy;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(4, 4, 4, 4);
		labelRefinementRoiTitle = new JLabel("Refinement ROI(s):");
		add(labelRefinementRoiTitle, constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridx = 1; 
		constraintsJLabelMesh.gridy = gridy;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(4, 4, 4, 4);
		add(getRefinementRoi(), constraints);

		gbc = new GridBagConstraints();
		gbc.gridx = 3; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 4, 4, 4);
		labelViewLevelMeshTitle = new JLabel("View Level Mesh:");
		add(labelViewLevelMeshTitle, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4; 
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(4, 4, 4, 4);
		labelViewLevelMesh = new JLabel();
		labelViewLevelMesh.setForeground(Color.blue);
		add(labelViewLevelMesh, gbc);
		
		gridy ++;
		GridBagConstraints constraintsMathOverridesPanel1 = new GridBagConstraints();
		constraintsMathOverridesPanel1.gridx = 0; 
		constraintsMathOverridesPanel1.gridy = gridy;
		constraintsMathOverridesPanel1.gridwidth = GridBagConstraints.REMAINDER;
		constraintsMathOverridesPanel1.fill = GridBagConstraints.BOTH;
		constraintsMathOverridesPanel1.weightx = 1.0;
		constraintsMathOverridesPanel1.weighty = 1.0;
		constraintsMathOverridesPanel1.insets = new Insets(4, 4, 4, 4);
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Parameters with values changed from defaults");
		collapsiblePanel.getContentPanel().setLayout(new BorderLayout());
		collapsiblePanel.getContentPanel().add(getMathOverridesPanel1(), BorderLayout.CENTER);
		add(collapsiblePanel, constraintsMathOverridesPanel1);

		getJTextAreaDescription().addFocusListener(ivjEventHandler);
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
		Insets insets = frame.getInsets();
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
private void refreshDisplay() {
	if (getSimulation() == null){
		getJTextAreaDescription().setBackground(getBackground());
		getJTextAreaDescription().setEditable(false);
//		getJLabelSimKey().setText("");
		getJTextAreaDescription().setText("");
		getJLabelTimestep().setText("");
		getJLabelOutput().setText("");
		getJLabelSensitivity().setText("");
		getJLabelGeometrySize().setText("");
		getJLabelMesh().setText("");
		getJLabelRelTolValue().setText("");
		getJLabelAbsTolValue().setText("");
		getMathOverridesPanel1().setMathOverrides(null);
		getJLabelMeshRefinement().setText("");
	} else {
		displayAnnotation();
		displayTask();
		displayMesh();
		displayOverrides();
		displayOther();
	
		getJTextAreaDescription().setBackground(java.awt.Color.white);
		getJTextAreaDescription().setEditable(true);
//		String key = "";
//		if (fieldSimulation.getKey() != null) {
//			key = "(SimID=" + fieldSimulation.getKey();
//			if (fieldSimulation.getSimulationVersion() != null && fieldSimulation.getSimulationVersion().getParentSimulationReference() != null) {
//				key += ", parentSimRef="+fieldSimulation.getSimulationVersion().getParentSimulationReference();
//			}
//			key += ")";
//		}
//		getJLabelSimKey().setText(key);
	}
}


/**
 * Sets the simulation property (cbit.vcell.solver.Simulation) value.
 * @param newValue The new value for the property.
 * @see #getSimulation
 */
public void setSimulation(Simulation newValue) {
	Simulation oldValue = fieldSimulation;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
		oldValue.getSolverTaskDescription().removePropertyChangeListener(ivjEventHandler);
		MeshSpecification meshSpecification = oldValue.getMeshSpecification();
		if (meshSpecification != null) {
			meshSpecification.removePropertyChangeListener(ivjEventHandler);
		}
	}
	fieldSimulation = newValue;	
	if (newValue != null) {
		// also set up a listener that will refresh when simulation is edited in place
		newValue.addPropertyChangeListener(ivjEventHandler);
		newValue.getSolverTaskDescription().addPropertyChangeListener(ivjEventHandler);
		MeshSpecification meshSpecification = newValue.getMeshSpecification();
		if (meshSpecification != null) {
			meshSpecification.addPropertyChangeListener(ivjEventHandler);
		}
	}
	
	refreshDisplay();
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

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	Simulation selectedSimulation = null;
	if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof Simulation) {
		selectedSimulation = (Simulation) selectedObjects[0];
	}
	setSimulation(selectedSimulation);	
}

private javax.swing.JLabel getJLabelMeshRefinement() {
	if (meshRefinementLabel == null) {
		try {
			meshRefinementLabel = new javax.swing.JLabel();
			meshRefinementLabel.setText(" ");
			meshRefinementLabel.setForeground(java.awt.Color.blue);
			meshRefinementLabel.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return meshRefinementLabel;
}

private javax.swing.JLabel getRefinementRoi() {
	if (labelRefinementRoi == null) {
		try {
			labelRefinementRoi = new javax.swing.JLabel();
			labelRefinementRoi.setText(" ");
			labelRefinementRoi.setForeground(java.awt.Color.blue);
			labelRefinementRoi.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return labelRefinementRoi;
}

private javax.swing.JLabel getJLabelTitleNumProcessors() {
	if (jlabelTitleNumProcessors == null) {
		try {
			jlabelTitleNumProcessors = new javax.swing.JLabel("# processors");
			jlabelTitleNumProcessors.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jlabelTitleNumProcessors.setName("JLabelTitleNumProcessors");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return jlabelTitleNumProcessors;
}

private javax.swing.JLabel getJLabelNumProcessors() {
	if (jlabelNumProcessors == null) {
		try {
			jlabelNumProcessors = new javax.swing.JLabel();
			jlabelNumProcessors.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jlabelNumProcessors.setName("JLabelNumProcessors");
			jlabelNumProcessors.setForeground(java.awt.Color.blue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return jlabelNumProcessors;
}


}
