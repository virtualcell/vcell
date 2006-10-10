/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.modelopt.gui;
import org.vcell.expression.SymbolTableEntry;

import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.solvers.OptimizationService;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 5:21:08 PM)
 * @author: Jim Schaff
 */
public class OptTestPanel extends javax.swing.JPanel {
	private ParameterMappingPanel ivjparameterMappingPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private ReferenceDataPanel ivjreferenceDataPanel = null;
	private javax.swing.JTextPane ivjOptimizeResultsTextPane = null;
	private OptimizationService fieldOptimizationService = null;
	private javax.swing.JPanel ivjJPanel4 = null;
	private javax.swing.JPanel ivjJPanel5 = null;
	private javax.swing.JSplitPane ivjJSplitPane1 = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JPanel ivjJPanel6 = null;
	private javax.swing.JComboBox ivjSolverTypeComboBox = null;
	private javax.swing.JButton ivjPlotButton = null;
	private javax.swing.JButton ivjSaveSolutionAsNewSimButton = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JPanel ivjJPanel7 = null;
	private javax.swing.JPanel ivjJPanel8 = null;
	private ReferenceDataMappingSpecTableModel ivjreferenceDataMappingSpecTableModel = null;
	private javax.swing.JButton ivjMapButton = null;
	private boolean ivjConnPtoP3Aligning = false;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private javax.swing.JScrollPane ivjDataModelMappingScrollPane = null;
	private javax.swing.JTable ivjDataModelMappingTable = null;
	private javax.swing.JScrollPane ivjOptimizeResultsScrollPane = null;
	private javax.swing.JPanel ivjAnnotationPanel = null;
	private javax.swing.JEditorPane ivjJEditorPane1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JTextField ivjJTextField1 = null;
	private javax.swing.JPanel ivjJPanel9 = null;
	private javax.swing.JProgressBar ivjJProgressBar1 = null;
	private javax.swing.JButton ivjSolveButton = null;
	private javax.swing.JLabel ivjNumEvaluationsTitleLabel = null;
	private javax.swing.JLabel ivjNumEvaluationsValueLabel = null;
	private javax.swing.JLabel ivjObjectiveFunctionTitleLabel = null;
	private javax.swing.JLabel ivjObjectiveFunctionValueLabel = null;
	private javax.swing.JButton ivjStopButton = null;
	private javax.swing.JPanel ivjJPanel10 = null;
	private javax.swing.JPanel ivjJPanel11 = null;
	private cbit.vcell.modelopt.ParameterEstimationTask fieldParameterEstimationTask = null;
	private boolean ivjConnPtoP4Aligning = false;
	private cbit.vcell.modelopt.ParameterEstimationTask ivjparameterEstimationTask1 = null;
	private cbit.vcell.modelopt.ModelOptimizationSpec ivjmodelOptimizationSpec = null;
	private OptimizationController ivjOptimizationController = null;
	private OptimizationController ivjOptimizationControllerFactory = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == OptTestPanel.this.getMapButton()) 
				connEtoC9(e);
			if (e.getSource() == OptTestPanel.this.getSolverTypeComboBox()) 
				connEtoC1(e);
			if (e.getSource() == OptTestPanel.this.getPlotButton()) 
				connEtoM17(e);
			if (e.getSource() == OptTestPanel.this.getSaveSolutionAsNewSimButton()) 
				connEtoM18(e);
			if (e.getSource() == OptTestPanel.this.getSolveButton()) 
				connEtoM19(e);
			if (e.getSource() == OptTestPanel.this.getStopButton()) 
				connEtoM20(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == OptTestPanel.this.getJTextField1()) 
				connEtoM13(e);
			if (e.getSource() == OptTestPanel.this.getJEditorPane1()) 
				connEtoM14(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OptTestPanel.this.getmodelOptimizationSpec() && (evt.getPropertyName().equals("referenceData"))) 
				connEtoM2(evt);
			if (evt.getSource() == OptTestPanel.this.getreferenceDataPanel() && (evt.getPropertyName().equals("referenceData"))) 
				connEtoC2(evt);
			if (evt.getSource() == OptTestPanel.this.getparameterEstimationTask1() && (evt.getPropertyName().equals("optimizationResultSet"))) 
				connEtoC4(evt);
			if (evt.getSource() == OptTestPanel.this.getDataModelMappingTable() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == OptTestPanel.this.getparameterEstimationTask1() && (evt.getPropertyName().equals("name"))) 
				connEtoM10(evt);
			if (evt.getSource() == OptTestPanel.this.getparameterEstimationTask1() && (evt.getPropertyName().equals("annotation"))) 
				connEtoM11(evt);
			if (evt.getSource() == OptTestPanel.this && (evt.getPropertyName().equals("parameterEstimationTask"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == OptTestPanel.this.getparameterEstimationTask1() && (evt.getPropertyName().equals("optimizationSolverSpec"))) 
				connEtoC12(evt);
			if (evt.getSource() == OptTestPanel.this.getparameterEstimationTask1() && (evt.getPropertyName().equals("solverMessageText"))) 
				connEtoM16(evt);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == OptTestPanel.this.getselectionModel1()) 
				connEtoC8(e);
		};
	};

/**
 * OptTestPanel constructor comment.
 */
public OptTestPanel() {
	super();
	initialize();
}

/**
 * OptTestPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public OptTestPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * OptTestPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public OptTestPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * OptTestPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public OptTestPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (SolverTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> OptTestPanel.solverTypeComboBox_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.solverTypeComboBox_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (parameterEstimationTask1.this --> OptTestPanel.setOptimizationSolverTypeSelection()V)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		this.setOptimizationSolverTypeSelection();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (parameterEstimationTask1.optimizationSolverSpec --> OptTestPanel.setOptimizationSolverTypeSelection()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setOptimizationSolverTypeSelection();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (referenceDataPanel.referenceData --> OptTestPanel.referenceDataPanel_ReferenceData(Lcbit.vcell.opt.ReferenceData;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.referenceDataPanel_ReferenceData(getreferenceDataPanel().getReferenceData());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (parameterEstimationTask1.optimizationResultSet --> OptTestPanel.optimizationResultSet_This(Lcbit.vcell.opt.OptimizationResultSet;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.optimizationResultSet_This(getparameterEstimationTask1().getOptimizationResultSet());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (OptTestPanel.initialize() --> OptTestPanel.optTestPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.optTestPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> OptTestPanel.selectionModel1_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectionModel1_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (MapButton.action.actionPerformed(java.awt.event.ActionEvent) --> OptTestPanel.mapButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.mapButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (parameterEstimationTask1.this --> parameterMappingPanel.modelOptimizationSpec)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		getparameterMappingPanel().setParameterEstimationTask(getparameterEstimationTask1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM10:  (parameterEstimationTask1.name --> JTextField1.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getparameterEstimationTask1() != null)) {
			getJTextField1().setText(getparameterEstimationTask1().getName());
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
 * connEtoM11:  (parameterEstimationTask1.annotation --> JEditorPane1.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getparameterEstimationTask1() != null)) {
			getJEditorPane1().setText(getparameterEstimationTask1().getAnnotation());
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
 * connEtoM12:  (parameterEstimationTask1.this --> JEditorPane1.text)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		if ((getparameterEstimationTask1() != null)) {
			getJEditorPane1().setText(getparameterEstimationTask1().getAnnotation());
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
 * connEtoM13:  (JTextField1.focus.focusLost(java.awt.event.FocusEvent) --> parameterEstimationTask1.name)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getparameterEstimationTask1().setName(getJTextField1().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM14:  (JEditorPane1.focus.focusLost(java.awt.event.FocusEvent) --> parameterEstimationTask1.annotation)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getparameterEstimationTask1().setAnnotation(getJEditorPane1().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM15:  (parameterEstimationTask1.this --> modelOptimizationSpec1.this)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		setmodelOptimizationSpec(this.getModelOptimizationSpec());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM16:  (parameterEstimationTask1.solverMessageText --> OptimizeResultsTextPane.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM16(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getOptimizeResultsTextPane().setText(String.valueOf(getparameterEstimationTask1().getSolverMessageText()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM17:  (PlotButton.action.actionPerformed(java.awt.event.ActionEvent) --> OptimizationController.plot()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM17(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getOptimizationController().plot();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM18:  (SaveSolutionAsNewSimButton.action.actionPerformed(java.awt.event.ActionEvent) --> OptimizationController.saveSolutionAsNewSimulation()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getOptimizationController().saveSolutionAsNewSimulation();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM19:  (SolveButton.action.actionPerformed(java.awt.event.ActionEvent) --> OptimizationController.solve()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM19(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getOptimizationController().solve();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (modelOptimizationSpec.referenceData --> referenceDataPanel.referenceData)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getreferenceDataPanel().setReferenceData(getmodelOptimizationSpec().getReferenceData());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM20:  (StopButton.action.actionPerformed(java.awt.event.ActionEvent) --> OptimizationController.stop()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM20(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getOptimizationController().stop();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM21:  (OptTestPanel.initialize() --> OptimizationController.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM21() {
	cbit.vcell.modelopt.gui.OptimizationController localValue = null;
	try {
		// user code begin {1}
		// user code end
		setOptimizationController(localValue = new cbit.vcell.modelopt.gui.OptimizationController(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setOptimizationControllerFactory(localValue);
}


/**
 * connEtoM22:  (parameterEstimationTask1.this --> OptimizationController.parameterEstimationTask)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM22(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		getOptimizationController().setParameterEstimationTask(getparameterEstimationTask1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (modelOptimizationSpec.this --> referenceDataPanel.referenceData)
 * @param value cbit.vcell.modelopt.ModelOptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.modelopt.ModelOptimizationSpec value) {
	try {
		// user code begin {1}
		// user code end
		getreferenceDataPanel().setReferenceData(this.getReferenceData());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (parameterEstimationTask1.this --> referenceDataPanel.referenceData)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		getreferenceDataPanel().setReferenceData(this.getReferenceData());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (parameterEstimationTask1.this --> OptimizeResultsTextPane.text)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		getOptimizeResultsTextPane().setText(this.getSolverMessageText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (modelOptimizationSpec1.this --> referenceDataMappingSpecTableModel.modelOptimizationSpec)
 * @param value cbit.vcell.modelopt.ModelOptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.modelopt.ModelOptimizationSpec value) {
	try {
		// user code begin {1}
		// user code end
		getreferenceDataMappingSpecTableModel().setModelOptimizationSpec(getmodelOptimizationSpec());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM8:  (OptTestPanel.initialize() --> ScrollPaneTable1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8() {
	try {
		// user code begin {1}
		// user code end
		getDataModelMappingTable().setModel(getreferenceDataMappingSpecTableModel());
		getDataModelMappingTable().createDefaultColumnsFromModel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM9:  (parameterEstimationTask1.this --> JTextField1.text)
 * @param value cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(cbit.vcell.modelopt.ParameterEstimationTask value) {
	try {
		// user code begin {1}
		// user code end
		if ((getparameterEstimationTask1() != null)) {
			getJTextField1().setText(getparameterEstimationTask1().getName());
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
 * connPtoP3SetSource:  (ScrollPaneTable1.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getselectionModel1() != null)) {
				getDataModelMappingTable().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetTarget:  (ScrollPaneTable1.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setselectionModel1(getDataModelMappingTable().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP4SetSource:  (OptTestPanel.parameterEstimationTask <--> parameterEstimationTask1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getparameterEstimationTask1() != null)) {
				this.setParameterEstimationTask(getparameterEstimationTask1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (OptTestPanel.parameterEstimationTask <--> parameterEstimationTask1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setparameterEstimationTask1(this.getParameterEstimationTask());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private java.lang.String displayResults(cbit.vcell.opt.OptimizationResultSet optResultSet) {
	if (optResultSet==null){
		return "no results";
	}
	StringBuffer buffer = new StringBuffer();
	
	buffer.append("\n-------------Optimizer Output-----------------\n");
	buffer.append(optResultSet.getOptimizationStatus() + "\n");
	buffer.append("objective function :"+optResultSet.getObjectiveFunctionValue()+"\n");
	buffer.append("num function evaluations :"+optResultSet.getObjFunctionEvaluations()+"\n");
	if (optResultSet.getOptimizationStatus().isNormal()){
		buffer.append("status: complete\n");
	}else{
		buffer.append("status: aborted\n");
	}
	for (int i = 0; optResultSet.getParameterNames()!=null && i < optResultSet.getParameterNames().length; i++){
		buffer.append(optResultSet.getParameterNames()[i]+" = "+optResultSet.getParameterValues()[i]+"\n");
	}
	
	//for (int i = 0; i < optResultSet.getSolutionNames().length; i++){
		//buffer.append(optResultSet.getSolutionNames()[i]+" = [ ");
		//double[] values = optResultSet.getSolutionValues(i);
		//for (int j = 0; j < values.length; j++){
			//buffer.append(values[j]+" ");
		//}
		//buffer.append("]\n");
	//}
	
	return buffer.toString();
}


/**
 * Return the AnnotationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAnnotationPanel() {
	if (ivjAnnotationPanel == null) {
		try {
			ivjAnnotationPanel = new javax.swing.JPanel();
			ivjAnnotationPanel.setName("AnnotationPanel");
			ivjAnnotationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJEditorPane1 = new java.awt.GridBagConstraints();
			constraintsJEditorPane1.gridx = 1; constraintsJEditorPane1.gridy = 1;
			constraintsJEditorPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJEditorPane1.weightx = 1.0;
			constraintsJEditorPane1.weighty = 1.0;
			constraintsJEditorPane1.insets = new java.awt.Insets(10, 0, 0, 0);
			getAnnotationPanel().add(getJEditorPane1(), constraintsJEditorPane1);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 1; constraintsJTextField1.gridy = 0;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(5, 0, 0, 0);
			getAnnotationPanel().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getAnnotationPanel().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 1;
			constraintsJLabel3.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJLabel3.insets = new java.awt.Insets(10, 4, 4, 4);
			getAnnotationPanel().add(getJLabel3(), constraintsJLabel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationPanel;
}

/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getDataModelMappingScrollPane() {
	if (ivjDataModelMappingScrollPane == null) {
		try {
			ivjDataModelMappingScrollPane = new javax.swing.JScrollPane();
			ivjDataModelMappingScrollPane.setName("DataModelMappingScrollPane");
			ivjDataModelMappingScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjDataModelMappingScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getDataModelMappingScrollPane().setViewportView(getDataModelMappingTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataModelMappingScrollPane;
}

/**
 * Return the ScrollPaneTable1 property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getDataModelMappingTable() {
	if (ivjDataModelMappingTable == null) {
		try {
			ivjDataModelMappingTable = new javax.swing.JTable();
			ivjDataModelMappingTable.setName("DataModelMappingTable");
			getDataModelMappingScrollPane().setColumnHeaderView(ivjDataModelMappingTable.getTableHeader());
			getDataModelMappingScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjDataModelMappingTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataModelMappingTable;
}

/**
 * Return the JEditorPane1 property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPane1() {
	if (ivjJEditorPane1 == null) {
		try {
			ivjJEditorPane1 = new javax.swing.JEditorPane();
			ivjJEditorPane1.setName("JEditorPane1");
			ivjJEditorPane1.setBorder(new cbit.gui.LineBorderBean());
			ivjJEditorPane1.setMargin(new java.awt.Insets(6, 3, 3, 3));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPane1;
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
			ivjJLabel1.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabel1.setText("Parameters");
			ivjJLabel1.setMaximumSize(new java.awt.Dimension(68, 30));
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel1.setPreferredSize(new java.awt.Dimension(68, 30));
			ivjJLabel1.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabel1.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabel1.setMinimumSize(new java.awt.Dimension(68, 30));
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
			ivjJLabel2.setText("Name");
			ivjJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			ivjJLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
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
			ivjJLabel3.setText("Description");
			ivjJLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJLabel3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
			ivjJLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
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
			cbit.gui.TitledBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder2.setTitleJustification(javax.swing.border.TitledBorder.CENTER);
			ivjLocalBorder2.setTitle("optimization solver");
			ivjJPanel10 = new javax.swing.JPanel();
			ivjJPanel10.setName("JPanel10");
			ivjJPanel10.setBorder(ivjLocalBorder2);
			ivjJPanel10.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSolverTypeComboBox = new java.awt.GridBagConstraints();
			constraintsSolverTypeComboBox.gridx = 0; constraintsSolverTypeComboBox.gridy = 0;
			constraintsSolverTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSolverTypeComboBox.weightx = 1.0;
			constraintsSolverTypeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel10().add(getSolverTypeComboBox(), constraintsSolverTypeComboBox);

			java.awt.GridBagConstraints constraintsJPanel11 = new java.awt.GridBagConstraints();
			constraintsJPanel11.gridx = 0; constraintsJPanel11.gridy = 1;
			constraintsJPanel11.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel11.weightx = 1.0;
			constraintsJPanel11.weighty = 1.0;
			constraintsJPanel11.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel10().add(getJPanel11(), constraintsJPanel11);
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
			ivjJPanel11.setLayout(new java.awt.FlowLayout());
			getJPanel11().add(getSolveButton(), getSolveButton().getName());
			ivjJPanel11.add(getStopButton());
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsreferenceDataPanel = new java.awt.GridBagConstraints();
			constraintsreferenceDataPanel.gridx = 0; constraintsreferenceDataPanel.gridy = 0;
			constraintsreferenceDataPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsreferenceDataPanel.weightx = 1.0;
			constraintsreferenceDataPanel.weighty = 1.0;
			getJPanel2().add(getreferenceDataPanel(), constraintsreferenceDataPanel);

			java.awt.GridBagConstraints constraintsJPanel8 = new java.awt.GridBagConstraints();
			constraintsJPanel8.gridx = 1; constraintsJPanel8.gridy = 0;
			constraintsJPanel8.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel8.weightx = 1.0;
			constraintsJPanel8.weighty = 1.0;
			constraintsJPanel8.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJPanel8(), constraintsJPanel8);
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
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel7 = new java.awt.GridBagConstraints();
			constraintsJPanel7.gridx = 1; constraintsJPanel7.gridy = 0;
			constraintsJPanel7.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel7.weightx = 1.0;
			constraintsJPanel7.weighty = 1.0;
			constraintsJPanel7.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJPanel7(), constraintsJPanel7);

			java.awt.GridBagConstraints constraintsJPanel10 = new java.awt.GridBagConstraints();
			constraintsJPanel10.gridx = 0; constraintsJPanel10.gridy = 0;
			constraintsJPanel10.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel10.weighty = 1.0;
			constraintsJPanel10.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJPanel10(), constraintsJPanel10);
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
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setPreferredSize(new java.awt.Dimension(500, 200));
			ivjJPanel4.setLayout(new java.awt.BorderLayout());
			getJPanel4().add(getparameterMappingPanel(), "Center");
			getJPanel4().add(getJPanel1(), "South");
			getJPanel4().add(getJLabel1(), "North");
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
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(new java.awt.GridBagLayout());
			ivjJPanel5.setMinimumSize(new java.awt.Dimension(500, 200));

			java.awt.GridBagConstraints constraintsJTabbedPane1 = new java.awt.GridBagConstraints();
			constraintsJTabbedPane1.gridx = -1; constraintsJTabbedPane1.gridy = -1;
			constraintsJTabbedPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJTabbedPane1.weightx = 1.0;
			constraintsJTabbedPane1.weighty = 1.0;
			constraintsJTabbedPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJTabbedPane1(), constraintsJTabbedPane1);
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
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel6() {
	if (ivjJPanel6 == null) {
		try {
			ivjJPanel6 = new javax.swing.JPanel();
			ivjJPanel6.setName("JPanel6");
			ivjJPanel6.setLayout(new java.awt.FlowLayout());
			ivjJPanel6.add(getPlotButton());
			getJPanel6().add(getSaveSolutionAsNewSimButton(), getSaveSolutionAsNewSimButton().getName());
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
 * Return the JPanel7 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel7() {
	if (ivjJPanel7 == null) {
		try {
			cbit.gui.TitledBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.TitledBorderBean();
			ivjLocalBorder1.setTitleJustification(javax.swing.border.TitledBorder.CENTER);
			ivjLocalBorder1.setTitle("solution");
			ivjJPanel7 = new javax.swing.JPanel();
			ivjJPanel7.setName("JPanel7");
			ivjJPanel7.setBorder(ivjLocalBorder1);
			ivjJPanel7.setLayout(new java.awt.GridBagLayout());
			ivjJPanel7.setMinimumSize(new java.awt.Dimension(400, 89));

			java.awt.GridBagConstraints constraintsJPanel6 = new java.awt.GridBagConstraints();
			constraintsJPanel6.gridx = 0; constraintsJPanel6.gridy = 1;
			constraintsJPanel6.gridwidth = 2;
			constraintsJPanel6.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel6.weightx = 1.0;
			constraintsJPanel6.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel7().add(getJPanel6(), constraintsJPanel6);

			java.awt.GridBagConstraints constraintsJPanel9 = new java.awt.GridBagConstraints();
			constraintsJPanel9.gridx = 1; constraintsJPanel9.gridy = 0;
			constraintsJPanel9.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel9.weightx = 0.3;
			constraintsJPanel9.weighty = 1.0;
			constraintsJPanel9.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel7().add(getJPanel9(), constraintsJPanel9);
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
 * Return the JPanel8 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel8() {
	if (ivjJPanel8 == null) {
		try {
			cbit.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.TitledBorderBean();
			ivjLocalBorder.setTitleJustification(javax.swing.border.TitledBorder.CENTER);
			ivjLocalBorder.setTitle("data/model mapping");
			ivjJPanel8 = new javax.swing.JPanel();
			ivjJPanel8.setName("JPanel8");
			ivjJPanel8.setPreferredSize(new java.awt.Dimension(250, 30));
			ivjJPanel8.setBorder(ivjLocalBorder);
			ivjJPanel8.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDataModelMappingScrollPane = new java.awt.GridBagConstraints();
			constraintsDataModelMappingScrollPane.gridx = 0; constraintsDataModelMappingScrollPane.gridy = 0;
			constraintsDataModelMappingScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDataModelMappingScrollPane.weightx = 1.0;
			constraintsDataModelMappingScrollPane.weighty = 1.0;
			constraintsDataModelMappingScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel8().add(getDataModelMappingScrollPane(), constraintsDataModelMappingScrollPane);

			java.awt.GridBagConstraints constraintsMapButton = new java.awt.GridBagConstraints();
			constraintsMapButton.gridx = 0; constraintsMapButton.gridy = 1;
			constraintsMapButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel8().add(getMapButton(), constraintsMapButton);
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
 * Return the JPanel9 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel9() {
	if (ivjJPanel9 == null) {
		try {
			ivjJPanel9 = new javax.swing.JPanel();
			ivjJPanel9.setName("JPanel9");
			ivjJPanel9.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNumEvaluationsTitleLabel = new java.awt.GridBagConstraints();
			constraintsNumEvaluationsTitleLabel.gridx = 0; constraintsNumEvaluationsTitleLabel.gridy = 1;
			constraintsNumEvaluationsTitleLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNumEvaluationsTitleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getNumEvaluationsTitleLabel(), constraintsNumEvaluationsTitleLabel);

			java.awt.GridBagConstraints constraintsNumEvaluationsValueLabel = new java.awt.GridBagConstraints();
			constraintsNumEvaluationsValueLabel.gridx = 1; constraintsNumEvaluationsValueLabel.gridy = 1;
			constraintsNumEvaluationsValueLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNumEvaluationsValueLabel.weightx = 1.0;
			constraintsNumEvaluationsValueLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getNumEvaluationsValueLabel(), constraintsNumEvaluationsValueLabel);

			java.awt.GridBagConstraints constraintsJProgressBar1 = new java.awt.GridBagConstraints();
			constraintsJProgressBar1.gridx = 0; constraintsJProgressBar1.gridy = 2;
			constraintsJProgressBar1.gridwidth = 2;
			constraintsJProgressBar1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJProgressBar1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getJProgressBar1(), constraintsJProgressBar1);

			java.awt.GridBagConstraints constraintsObjectiveFunctionTitleLabel = new java.awt.GridBagConstraints();
			constraintsObjectiveFunctionTitleLabel.gridx = 0; constraintsObjectiveFunctionTitleLabel.gridy = 0;
			constraintsObjectiveFunctionTitleLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsObjectiveFunctionTitleLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsObjectiveFunctionTitleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getObjectiveFunctionTitleLabel(), constraintsObjectiveFunctionTitleLabel);

			java.awt.GridBagConstraints constraintsObjectiveFunctionValueLabel = new java.awt.GridBagConstraints();
			constraintsObjectiveFunctionValueLabel.gridx = 1; constraintsObjectiveFunctionValueLabel.gridy = 0;
			constraintsObjectiveFunctionValueLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsObjectiveFunctionValueLabel.weightx = 1.0;
			constraintsObjectiveFunctionValueLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getObjectiveFunctionValueLabel(), constraintsObjectiveFunctionValueLabel);

			java.awt.GridBagConstraints constraintsOptimizeResultsScrollPane = new java.awt.GridBagConstraints();
			constraintsOptimizeResultsScrollPane.gridx = 0; constraintsOptimizeResultsScrollPane.gridy = 3;
			constraintsOptimizeResultsScrollPane.gridwidth = 2;
			constraintsOptimizeResultsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOptimizeResultsScrollPane.weightx = 1.0;
			constraintsOptimizeResultsScrollPane.weighty = 1.0;
			constraintsOptimizeResultsScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel9().add(getOptimizeResultsScrollPane(), constraintsOptimizeResultsScrollPane);
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
 * Return the JProgressBar1 property value.
 * @return javax.swing.JProgressBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JProgressBar getJProgressBar1() {
	if (ivjJProgressBar1 == null) {
		try {
			ivjJProgressBar1 = new javax.swing.JProgressBar();
			ivjJProgressBar1.setName("JProgressBar1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJProgressBar1;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setDividerLocation(200);
			ivjJSplitPane1.setLastDividerLocation(1);
			getJSplitPane1().add(getJPanel4(), "top");
			getJSplitPane1().add(getJPanel5(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}

/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("Reference Data", null, getJPanel2(), null, 0);
			ivjJTabbedPane1.insertTab("Optimization", null, getJPanel3(), null, 1);
			ivjJTabbedPane1.insertTab("Annotation", null, getAnnotationPanel(), null, 2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			ivjJTextField1.setMargin(new java.awt.Insets(0, 0, 6, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}


/**
 * Return the MapButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getMapButton() {
	if (ivjMapButton == null) {
		try {
			ivjMapButton = new javax.swing.JButton();
			ivjMapButton.setName("MapButton");
			ivjMapButton.setText("associate..");
			ivjMapButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMapButton;
}

/**
 * Return the modelOptimizationSpec1 property value.
 * @return cbit.vcell.modelopt.ModelOptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelopt.ModelOptimizationSpec getmodelOptimizationSpec() {
	// user code begin {1}
	// user code end
	return ivjmodelOptimizationSpec;
}

/**
 * Comment
 */
private cbit.vcell.modelopt.ModelOptimizationSpec getModelOptimizationSpec() {
	if (getParameterEstimationTask()==null){
		return null;
	}else{
		return getParameterEstimationTask().getModelOptimizationSpec();
	}
}


/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNumEvaluationsTitleLabel() {
	if (ivjNumEvaluationsTitleLabel == null) {
		try {
			ivjNumEvaluationsTitleLabel = new javax.swing.JLabel();
			ivjNumEvaluationsTitleLabel.setName("NumEvaluationsTitleLabel");
			ivjNumEvaluationsTitleLabel.setText("number of evaluations");
			ivjNumEvaluationsTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			ivjNumEvaluationsTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumEvaluationsTitleLabel;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getNumEvaluationsValueLabel() {
	if (ivjNumEvaluationsValueLabel == null) {
		try {
			ivjNumEvaluationsValueLabel = new javax.swing.JLabel();
			ivjNumEvaluationsValueLabel.setName("NumEvaluationsValueLabel");
			ivjNumEvaluationsValueLabel.setText(" ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumEvaluationsValueLabel;
}

/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getObjectiveFunctionTitleLabel() {
	if (ivjObjectiveFunctionTitleLabel == null) {
		try {
			ivjObjectiveFunctionTitleLabel = new javax.swing.JLabel();
			ivjObjectiveFunctionTitleLabel.setName("ObjectiveFunctionTitleLabel");
			ivjObjectiveFunctionTitleLabel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjObjectiveFunctionTitleLabel.setText("best objective function value");
			ivjObjectiveFunctionTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			ivjObjectiveFunctionTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjObjectiveFunctionTitleLabel;
}

/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getObjectiveFunctionValueLabel() {
	if (ivjObjectiveFunctionValueLabel == null) {
		try {
			ivjObjectiveFunctionValueLabel = new javax.swing.JLabel();
			ivjObjectiveFunctionValueLabel.setName("ObjectiveFunctionValueLabel");
			ivjObjectiveFunctionValueLabel.setText(" ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjObjectiveFunctionValueLabel;
}

/**
 * Return the OptimizationController property value.
 * @return cbit.vcell.modelopt.gui.OptimizationController
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private OptimizationController getOptimizationController() {
	// user code begin {1}
	// user code end
	return ivjOptimizationController;
}


/**
 * Return the OptimizationControllerFactory property value.
 * @return cbit.vcell.modelopt.gui.OptimizationController
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private OptimizationController getOptimizationControllerFactory() {
	// user code begin {1}
	// user code end
	return ivjOptimizationControllerFactory;
}


/**
 * Method generated to support the promotion of the optimizationControllerThis attribute.
 * @return cbit.vcell.modelopt.gui.OptimizationController
 */
public OptimizationController getOptimizationControllerThis() {
	return getOptimizationController();
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getOptimizeResultsScrollPane() {
	if (ivjOptimizeResultsScrollPane == null) {
		try {
			ivjOptimizeResultsScrollPane = new javax.swing.JScrollPane();
			ivjOptimizeResultsScrollPane.setName("OptimizeResultsScrollPane");
			getOptimizeResultsScrollPane().setViewportView(getOptimizeResultsTextPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOptimizeResultsScrollPane;
}

/**
 * Return the JTextPane1 property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextPane getOptimizeResultsTextPane() {
	if (ivjOptimizeResultsTextPane == null) {
		try {
			ivjOptimizeResultsTextPane = new javax.swing.JTextPane();
			ivjOptimizeResultsTextPane.setName("OptimizeResultsTextPane");
			ivjOptimizeResultsTextPane.setBounds(0, 0, 4, 107);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOptimizeResultsTextPane;
}

/**
 * Gets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
 * @return The parameterEstimationTask property value.
 * @see #setParameterEstimationTask
 */
public cbit.vcell.modelopt.ParameterEstimationTask getParameterEstimationTask() {
	return fieldParameterEstimationTask;
}


/**
 * Return the parameterEstimationTask1 property value.
 * @return cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelopt.ParameterEstimationTask getparameterEstimationTask1() {
	// user code begin {1}
	// user code end
	return ivjparameterEstimationTask1;
}


/**
 * Return the parameterMappingPanel property value.
 * @return cbit.vcell.modelopt.gui.ParameterMappingPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ParameterMappingPanel getparameterMappingPanel() {
	if (ivjparameterMappingPanel == null) {
		try {
			ivjparameterMappingPanel = new cbit.vcell.modelopt.gui.ParameterMappingPanel();
			ivjparameterMappingPanel.setName("parameterMappingPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjparameterMappingPanel;
}


/**
 * Return the PlotButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getPlotButton() {
	if (ivjPlotButton == null) {
		try {
			ivjPlotButton = new javax.swing.JButton();
			ivjPlotButton.setName("PlotButton");
			ivjPlotButton.setText("Plot");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotButton;
}


/**
 * Comment
 */
private cbit.vcell.opt.ReferenceData getReferenceData() {
	if (getParameterEstimationTask()==null){
		return null;
	}else{
		return getParameterEstimationTask().getModelOptimizationSpec().getReferenceData();
	}
}


/**
 * Return the referenceDataMappingSpecTableModel property value.
 * @return cbit.vcell.modelopt.gui.ReferenceDataMappingSpecTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReferenceDataMappingSpecTableModel getreferenceDataMappingSpecTableModel() {
	if (ivjreferenceDataMappingSpecTableModel == null) {
		try {
			ivjreferenceDataMappingSpecTableModel = new cbit.vcell.modelopt.gui.ReferenceDataMappingSpecTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjreferenceDataMappingSpecTableModel;
}


/**
 * Return the referenceDataPanel property value.
 * @return cbit.vcell.modelopt.gui.ReferenceDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReferenceDataPanel getreferenceDataPanel() {
	if (ivjreferenceDataPanel == null) {
		try {
			ivjreferenceDataPanel = new cbit.vcell.modelopt.gui.ReferenceDataPanel();
			ivjreferenceDataPanel.setName("referenceDataPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjreferenceDataPanel;
}


/**
 * Return the SaveAsNewSimulationButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getSaveSolutionAsNewSimButton() {
	if (ivjSaveSolutionAsNewSimButton == null) {
		try {
			ivjSaveSolutionAsNewSimButton = new javax.swing.JButton();
			ivjSaveSolutionAsNewSimButton.setName("SaveSolutionAsNewSimButton");
			ivjSaveSolutionAsNewSimButton.setText("Save solution as new Simulation...");
			ivjSaveSolutionAsNewSimButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveSolutionAsNewSimButton;
}

/**
 * Return the selectionModel1 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getSolveButton() {
	if (ivjSolveButton == null) {
		try {
			ivjSolveButton = new javax.swing.JButton();
			ivjSolveButton.setName("SolveButton");
			ivjSolveButton.setText("Solve");
			ivjSolveButton.setActionCommand("Solve");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolveButton;
}

/**
 * Comment
 */
private java.lang.String getSolverMessageText() {
	if (getParameterEstimationTask()!=null){
		return getParameterEstimationTask().getSolverMessageText();
	}else{
		return "";
	}
}


/**
 * Return the SolverTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getSolverTypeComboBox() {
	if (ivjSolverTypeComboBox == null) {
		try {
			ivjSolverTypeComboBox = new javax.swing.JComboBox();
			ivjSolverTypeComboBox.setName("SolverTypeComboBox");
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
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getStopButton() {
	if (ivjStopButton == null) {
		try {
			ivjStopButton = new javax.swing.JButton();
			ivjStopButton.setName("StopButton");
			ivjStopButton.setText("Stop");
			ivjStopButton.setEnabled(false);
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
	getreferenceDataPanel().addPropertyChangeListener(ivjEventHandler);
	getDataModelMappingTable().addPropertyChangeListener(ivjEventHandler);
	getMapButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getSolverTypeComboBox().addActionListener(ivjEventHandler);
	getPlotButton().addActionListener(ivjEventHandler);
	getSaveSolutionAsNewSimButton().addActionListener(ivjEventHandler);
	getSolveButton().addActionListener(ivjEventHandler);
	getStopButton().addActionListener(ivjEventHandler);
	getJTextField1().addFocusListener(ivjEventHandler);
	getJEditorPane1().addFocusListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP4SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("OptTestPanel");
		setPreferredSize(new java.awt.Dimension(800, 700));
		setLayout(new java.awt.GridBagLayout());
		setSize(936, 559);
		setMinimumSize(new java.awt.Dimension(700, 700));

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJSplitPane1(), constraintsJSplitPane1);
		initConnections();
		connEtoC7();
		connEtoM8();
		connEtoM21();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		OptTestPanel aOptTestPanel;
		aOptTestPanel = new OptTestPanel();
		frame.setContentPane(aOptTestPanel);
		frame.setSize(aOptTestPanel.getSize());
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
private void mapButton_ActionPerformed() {

	if (getParameterEstimationTask()==null){
		return;
	}
	
	org.vcell.expression.SymbolTableEntry symbolTableEntries[] = getParameterEstimationTask().getModelOptimizationSpec().calculateTimeDependentModelObjects();

	java.util.Comparator steComparator = new java.util.Comparator() {
		private Class[] classOrder = new Class[] { cbit.vcell.model.ReservedSymbol.class, cbit.vcell.model.SpeciesContext.class, cbit.vcell.model.Kinetics.KineticsParameter.class };
		public int compare(Object obj1, Object obj2){
			SymbolTableEntry ste1 = (SymbolTableEntry)obj1;
			SymbolTableEntry ste2 = (SymbolTableEntry)obj2;
			int ste1Category = 100;
			int ste2Category = 100;
			for (int i=0;i<classOrder.length;i++){
				if (ste1.getClass().equals(classOrder[i])){
					ste1Category = i;
				}
				if (ste2.getClass().equals(classOrder[i])){
					ste2Category = i;
				}
			}
			if (ste1Category < ste2Category){
				return 1;
			}else if (ste1Category > ste2Category){
				return -1;
			}else{
				return ste1.getName().compareTo(ste2.getName());
			}
		}
	};
	
	java.util.Arrays.sort(symbolTableEntries,steComparator);
	
	SymbolTableEntry ste = (SymbolTableEntry)cbit.gui.DialogUtils.showListDialog(this,symbolTableEntries,"select data association",new SymbolTableEntryListCellRenderer());

	if (ste!=null && getselectionModel1().getMaxSelectionIndex()>=0){
		cbit.vcell.modelopt.ReferenceDataMappingSpec refDataMappingSpec = getParameterEstimationTask().getModelOptimizationSpec().getReferenceDataMappingSpecs()[getselectionModel1().getMaxSelectionIndex()];
		try {
			refDataMappingSpec.setModelObject(ste);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			cbit.gui.DialogUtils.showErrorDialog(this,e.getMessage());
		}
	}
	return;
}


/**
 * Comment
 */
private void optimizationResultSet_This(cbit.vcell.opt.OptimizationResultSet optResultSet) {
	String message = displayResults(optResultSet);
	getParameterEstimationTask().appendSolverMessageText("\n"+message);
	if (optResultSet!=null){
		getSaveSolutionAsNewSimButton().setEnabled(true);
	}else{
		getSaveSolutionAsNewSimButton().setEnabled(false);
	}
}


/**
 * Comment
 */
private void optTestPanel_Initialize() {
	for (int i = 0; i < OptimizationSolverSpec.SOLVER_TYPES.length; i++){
		((javax.swing.DefaultComboBoxModel)getSolverTypeComboBox().getModel()).addElement(OptimizationSolverSpec.SOLVER_TYPES[i]);
	}
	javax.swing.DefaultComboBoxModel model = (javax.swing.DefaultComboBoxModel)getSolverTypeComboBox().getModel();
	getSolverTypeComboBox().setModel(model);
	getSolverTypeComboBox().setSelectedIndex(0);


	//
	// set cell renderer for type SymbolTableEntry
	//
	getDataModelMappingTable().setDefaultRenderer(SymbolTableEntry.class,new SymbolTableEntryTableCellRenderer());
}


/**
 * Comment
 */
private void referenceDataPanel_ReferenceData(cbit.vcell.opt.ReferenceData arg1) {
	if (getParameterEstimationTask()!=null && getParameterEstimationTask().getModelOptimizationSpec().getReferenceData()!=arg1){
		getParameterEstimationTask().getModelOptimizationSpec().setReferenceData(arg1);
	}
}


/**
 * Comment
 */
private void selectionModel1_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	if (getselectionModel1().getMinSelectionIndex()>=0){
		getMapButton().setEnabled(true);
	}else{
		getMapButton().setEnabled(false);
	}
}


/**
 * Set the modelOptimizationSpec1 to a new value.
 * @param newValue cbit.vcell.modelopt.ModelOptimizationSpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodelOptimizationSpec(cbit.vcell.modelopt.ModelOptimizationSpec newValue) {
	if (ivjmodelOptimizationSpec != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjmodelOptimizationSpec != null) {
				ivjmodelOptimizationSpec.removePropertyChangeListener(ivjEventHandler);
			}
			ivjmodelOptimizationSpec = newValue;

			/* Listen for events from the new object */
			if (ivjmodelOptimizationSpec != null) {
				ivjmodelOptimizationSpec.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM7(ivjmodelOptimizationSpec);
			connEtoM3(ivjmodelOptimizationSpec);
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
 * Set the OptimizationController to a new value.
 * @param newValue cbit.vcell.modelopt.gui.OptimizationController
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setOptimizationController(OptimizationController newValue) {
	if (ivjOptimizationController != newValue) {
		try {
			cbit.vcell.modelopt.gui.OptimizationController oldValue = getOptimizationController();
			ivjOptimizationController = newValue;
			firePropertyChange("optimizationControllerThis", oldValue, newValue);
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
 * Set the OptimizationControllerFactory to a new value.
 * @param newValue cbit.vcell.modelopt.gui.OptimizationController
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setOptimizationControllerFactory(OptimizationController newValue) {
	if (ivjOptimizationControllerFactory != newValue) {
		try {
			cbit.vcell.modelopt.gui.OptimizationController oldValue = getOptimizationControllerFactory();
			ivjOptimizationControllerFactory = newValue;
			firePropertyChange("optimizationControllerThis", oldValue, newValue);
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
 * Method generated to support the promotion of the optimizationControllerThis attribute.
 * @param arg1 cbit.vcell.modelopt.gui.OptimizationController
 */
public void setOptimizationControllerThis(OptimizationController arg1) {
	setOptimizationController(arg1);
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
 * Comment
 */
private void setOptimizationSolverTypeSelection() {
	if (getParameterEstimationTask()!=null){
		String previouslySelectedItem = (String)getSolverTypeComboBox().getSelectedItem();
		if (!getParameterEstimationTask().getOptimizationSolverSpec().getSolverType().equals(previouslySelectedItem)){
			getSolverTypeComboBox().setSelectedItem(getParameterEstimationTask().getOptimizationSolverSpec().getSolverType());
		}
	}else{
		getSolverTypeComboBox().setSelectedItem(OptimizationSolverSpec.SOLVERTYPE_CFSQP);
	}
	return;
}


/**
 * Sets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
 * @param parameterEstimationTask The new value for the property.
 * @see #getParameterEstimationTask
 */
public void setParameterEstimationTask(cbit.vcell.modelopt.ParameterEstimationTask parameterEstimationTask) {
	cbit.vcell.modelopt.ParameterEstimationTask oldValue = fieldParameterEstimationTask;
	fieldParameterEstimationTask = parameterEstimationTask;
	firePropertyChange("parameterEstimationTask", oldValue, parameterEstimationTask);
}


/**
 * Set the parameterEstimationTask1 to a new value.
 * @param newValue cbit.vcell.modelopt.ParameterEstimationTask
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setparameterEstimationTask1(cbit.vcell.modelopt.ParameterEstimationTask newValue) {
	if (ivjparameterEstimationTask1 != newValue) {
		try {
			cbit.vcell.modelopt.ParameterEstimationTask oldValue = getparameterEstimationTask1();
			/* Stop listening for events from the current object */
			if (ivjparameterEstimationTask1 != null) {
				ivjparameterEstimationTask1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjparameterEstimationTask1 = newValue;

			/* Listen for events from the new object */
			if (ivjparameterEstimationTask1 != null) {
				ivjparameterEstimationTask1.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM9(ivjparameterEstimationTask1);
			connEtoM12(ivjparameterEstimationTask1);
			connPtoP4SetSource();
			connEtoM15(ivjparameterEstimationTask1);
			connEtoM1(ivjparameterEstimationTask1);
			connEtoM4(ivjparameterEstimationTask1);
			connEtoC11(ivjparameterEstimationTask1);
			connEtoM5(ivjparameterEstimationTask1);
			connEtoM22(ivjparameterEstimationTask1);
			firePropertyChange("parameterEstimationTask", oldValue, newValue);
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
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addListSelectionListener(ivjEventHandler);
			}
			connPtoP3SetSource();
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
 * Comment
 */
private void solverTypeComboBox_ActionPerformed() {
	if (getParameterEstimationTask()==null){
		return;
	}
	//
	// if running, shouldn't change solver type.
	//
	if (getOptimizationController().isRunning()){
		throw new RuntimeException("can't change solver types while running");
	}
	
	String solverName = (String)getSolverTypeComboBox().getSelectedItem();
	OptimizationSolverSpec optSolverSpec = new cbit.vcell.opt.OptimizationSolverSpec(solverName);
	getParameterEstimationTask().setOptimizationSolverSpec(optSolverSpec);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G400171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD8D45556B095F52D302830482C302C3845ADAB3AD1D1170ACA0B0A124A0A3AD4D45C329B15372C288EBFA68AAAAA9515BF24E82008202828287CAAA88AAA81F5A999E00011E1E618B9A0AA5A3B765EE76DF3664C9986664D1E77F93E0F4765193D765AFB2F35765AEB7F1EFD0474E5FEB363935285A1FE26A07DED0E9704696F88420631777F0CB8EFD3C30A20787B81E001905F1784CFBF088B78
	EFE3CA1AF017AD994201909ED75F18728D7CDEA464ED0AFC9DFEB8A9A790627A77DCB2EF68F23AB7E1B933E8B834AF951EA4D1906048B3495CG65C776E5C878C8890F901FGB8CA73D15FE289D7856196GDF00GB3238F0567ECDA7259DB0BA43A660B7C05690FDA936612FC94F9A2E4D9D85AAA6919A9FC929FEBA1E5E57627CAE9BCA09C84C004CF9466352F06E7F1DDF7670E36D60318575F26B79A73FA4C6DFA2359A266F5769936EE2D350875FA1BD827B3690D02E06CA579F575A7537402D08861999477
	050914B191FECF82086278B69B4197DB9BD26A810CD614EB7B0F45925FCDA76E9422BB9F1ABD5F09DC1ED6A6CF00D516ABE8F5EB6237348EEE5ACCF49D83F129360614CC008A00F6GCF006FE95D5F360948D95AAA161BFBFA4C26D20B254C58EE5DC764045FDB37C20C04BB28EFB3DB5B8501556F2A7EDC8DFE0682162EF0446918CE7CB7E0DD879C3CD9083C7D5204D99ABA0974BF11B04E46F491E813F5424A6EF1DAF6044D5E966D4BEE9414E58B7FDABFEB26C6590B1FDDBFABC64AA7CED1F615C86A7AC2B3
	296B4570BB1E728236C871FE946F2540B33B1CC971A49C84F195FDE89BCD7310171A24EB0434024BB475607B00D7C2F89F63A126CF4D4BBD34ED267733B2FF653C3CCF71D395F8460B015AA9890FBB5A10B26AF8C38A332F40AD241D0AA79BD23C817C81C2G22G62G52CE210DFD3AE95DB0ED2CCEE7954B7A8C46F63DD5E0E983B7FF8DCF7D00E855D51BEC224E5426EF50D98D3AD6235EA6C8B477EEA6BAE835CB77G357DB6A8E3375E24376AC4035984396BFA74225EEAE3B40FEDA67AED336AE9EC3D2187
	9A332E47A2E03ED3B6AF0227D1E793FF32346BC43DCC4162DF5CCC6A24DF57GB9C286701B6965152DC4DF79A0FF95C09357C3C39149EF2F5E8A7EE26B569A33393B4FD2CFA4917CCF91BDFBD25FE1045F1E27D047AFEDA338E990CE7A9D7399DD34D011CF05210D302F339E43B6B6461C894F8C204F87288730G78818483C481A4F920BD7D3B896D29E26E5F045CE746E85A13B758F63E0764C3BD546D6AFADA9E615982F15EG93814281E281524EGBE4E40FEA1456CA165C9F8437C23FEF7ECFF9685AEC3A3
	DE8DFEC35090F90325F8228F46CB9E67E52602F63E93BF003F93DD70C1A0E508B8E2F3F38559A732FA5E3B1D58CC9D68498860F58656752422CC56F64AF4B6BD3616DAAB983A3EDD2EF9A64F7DFDC1D21EC4062FG16G94GAC83D889308560C7009C00B5GEB81C85B2C8458G3091E0B340BE00EDG5B812A8556767F8B50837013C0BC22A01C81E08668A5368820832885B8GF08840GF09260A200CE006BG3A8146G1C85F097406FGE4GF3190036BB034AB081648F83188E3078CC70FFGC6G9F00E0
	00E80054339AD28A811A81BC8182GA2G62G32BC9BD2EAGC400F1G21G53G524FEEC8A986E887708288840884C8BA27A1A597209EE090E082C09840F40045FF0172810CGBEG41G51G292320FC00E6GCF0000D1E8D7E6DBD402E35F278CABED6CABD55C92293EB03C52C91D95286AEC0B224E1479D5CA4FEA291E76296A896B73E7290EFF1568F50ABCBA257A6C16ECG6BDD4B3ECFCA75FA06D44F4E5A41C552F30CD47F4E682E9BA2CE89F78D136E7F439F834C56D70BEADBD77B47192AB44AB862D7
	67AA4224DD10BE123489B2B7A13E1178C162CFC91BA0F30D9582732158A61479AD111E241D10F6C05ACEA9C039E06F63081D13B634D6225F294803340F5D82EB9F240DE13B525281E9D3245D10767753903A5AABBDC9FB7BF5883A5AE16A3E717FC07D7F3F842CAF3EEF8B31CFFD3F5EA4D668ADFAD3BB7C30C9F18FEDA136AC0DD3BB4CBC4DE394EF01F1EDB98CA4ED9C7F5496E243BAD1349ADA7BC47DA13DA89ACC1DF27C7334ACDBDF2BC59E92E3D9D1DBC81BB29F05F1AD27FFFD8B99933619CDA6BD9D9470
	A4D27C1BF4CC2AB76AFB0069F2F31FC9E478F73612F9FB377E18B4A6FEEF6B178299539A7B48781A4D05A76AC87B33D88DBDB054A0C3F4A16BDCB2DE3D22BFC3212388687F481CFE86BCD34F4571655195B83E4CBA7ED7A16430A5C1EB6E64FB5D058946F359784C63DCF9FC49F8FAF51BB66FB1F4ECAC61ED0C571A84C22B1C19A7D25ADF29CF2173F708B474980E53C17FA10B3ECDE0BEEB11D0181E864FBE1B5EDAE755F740888BA69B36829677DFE1B9BCB93AC2A76A7669AC9628B312859D4553898D66F55F
	F1DAFF409C2D8E9AD7A045DD1C1241EC024DC6AAED408F1B0268EC5DD2F92D71DFB3B9B82B65E613E8B59B0D245299CDCB639227B43BF4ED2219CC89247125678E2297BF50EDA0C05839B83EECAFA671B81710861665A48B4AB744951DC76ABA561CAE55D5B4240DBB175575BC9E5FE141F88F887B1EA747CF2771A93D98CF407FBCF57A4DBC3E78FC98071D2F0E0F64716D90B76890FF1695633DA0EE42796A72BF36AA4B0F1662533DD08F873627517A6A6DA355CB2AF3AFEF93CD5B972A629A24764162DB3613
	76E1D44C6746F911B29B7AF19DAB5B0B4D0DCA60598C606145F82B9BCD68D69FC53A98C5BC61CF982D24FB19530D9B4D66D60123995DD4BB3AAB86F839973078926959F4013406F681217B699852F9C8713E973079DBA0BC1712F2289C4B0EA3DD5A05F2BC61B37BC2169F231B4669AAAEE472B6DF486602C6BB3AFF1CC0BA6F8BD9397E708C81081668323C891D77A0524D38100D7B533CD9FEA45FBCAEC745C92433F833FCC6F93379A721DB4A75B2679452CD1062033DD9794997A975779427EB863C8860F391
	230B38C8A947185F11AE6EA2D69F6997435C832062E2259CED825255DF4C66AAC48EC2EF3C184941727BBE9E6986AF16639D73FB055379DC4C66BC5A790DC9C03AGA93FD0F8C6BA6427CB64F2DC4C66CE18DF28DDFE8119C817FE09AC2FE3FE569F10AE7792B687D367476AED55F2EEAF17B07DB6CB79FADC22343FD8CE672D0837C8CF39BE7E3E82697CA53A90F80683CC1768BCFD885D39D9C81761436605E9129D647BA873DB33926942FDD8F9B1BE4C6EE778A8730B63F469E3585C92734B9D234C6F2AD5C8
	57B50671271DDF7B0F3CDD0EE1F3D4F5FE4CCE3F4CC6BADF40CF84889F436852AED552BD4A69AAAE154B257A3623BB154375AC5179DFEACF476458369A69429479C5436F244B14F24CDC4375F2991BD7F3B9AED366F78327EB390C65B7F019FDFE0C2EAF174B8B7809G6117317525B4DFA55D1635C8178578E2DF192E590E6E9DCEB78A70FE8A3A90BB3A71F9C8978178E98A3A544B15F4BF2DC33A4C4B191CC517B33ABA09AE0404051579DC5E4B591A82518BD9A39B38DC590E9E61F429E3593AC356D816AF49
	CFEE179795A0DD61D8263FBA295C1A31CA7EDA56F3BFB9167915D192FD48D8E539C59B385D0FE5EB9C04BFC29FB3D6D9EEAC271BB11679814CAF6D8AE5FE41053CBF3A022D15D0BBD0644746EE9F6410F1915DF3B9226B578D64590E4278EA6B56BDF49C4776D7E69EA5E3BEB236AC398277D17EE2A1E31B8188FB8378DF01E34F276662BE0ACF373F90792F02D94E7607C2A4FE232F10471EE13125DF32315393BBC919D963C09763F03C7062F665DAEE294DE6EEB350A118B4A6B8EF07F2EC4AF0976E18834F8E
	0355A6D64364E1G478FE3F690FE49BA330416565D3F21F21131DE95146BB18E77265E1C3BCEF03EB735F56BBE4DD1AE53C95B86540977716B0416B1E393D21C68A4E89C534554F132CEF2CF4D1849F89B4DF9CB3B32A1258A4063CA6AEBACE4BEC146910C5785DF158C41AB5BC7BB68F2D44DF8BF39CBD91F4173CFBA65BD74CA29BE2FD46C0B196BE7B15EFF6BC55E332E821FF09572FEC5AF611D0C9D98DD229569FC00A60C5315D8899D6D2BA85D0D1CAE69EA70B7D7236CFE5905AEE5579E3D33740BD7AF67
	694DBDF906561E3C3E7E1EBCDD2B1827EFB7C0C573C1BEEB9B79B65CFB942FC65EFE277B1BA43FG40C5833039439B7D24AD584DE708BE683CEE4C9A653CEE319F0C9B8186813C7DB07D0223DA69D9BD7DEDE90914B66C66EB04E45D5989DA7B17DE7F6ACC7077E375936E2758233A24B531990071FF68A8721FB19E7C5AF8546DDD4BCB5565D3AFF1584E1832EB940AA7BA370B5D165340F93EE6111267625FFF5364F9547C2E04D163992F93474BBCC7DD5C1AB87DE264F956G729C87B40557204ECC83E48EEA
	D65478C13D2D4FA842641F7B40F73A4B1D519C2E6FB2589CA42A55A2A512B1DEC2B9AFC660E38267E54DB102B7188C22C1E7B49C57E7673072F79D7E0E154F674F072B95C48D681BCE9DC31B0F04BC532F45FC23ACC4EF2AB934B4DF1DFD1C349B9124E0BAFF3FD4297320D9DE1ABA6F3A41B72166DA26EB71DA05FF9E5F1A98B59EF5DEF59C658C851AB84E4F1463DA7C30B4DF1D40B40593E06CBC81538C1EC0BED9F8420921568ED8DEB1A7D09F93A01FC81ED76D20DA4F2C0DFDE8D63631746BE02CG508CE0
	398E539E9B1C096572F26E9A44F2C681DD48F5589E52F710A7DD3761B6BD78B36A57A3ACD048D8B4DAD33F347F3ECE526B7532FE53C13759DC3FBDA731DC8B5078DF0FBC5E5ACB686D57B910578D27D03FD1C01FF1832699FB0A70E5B45840D425F4F9147372AE1D29D35F1E2D687D7368F2DBDE0D12381220A4FB3D3C2952217C02438D4A5CEA06139D73850C6FCFB85FF54073A8G360610EF37069082789800D4FF14CB777BFAF86A18C06CD12737F6182DBD12CCF93A232244C129C292EC7BD35B48B829C7E7
	A96B93C53369F029AA0B1C86465FFD3FE3DDE4C259CD3C7C15825AAD6377C91312DFEF208B820864747B983DFEC0E479FA8918EFAC50E47F9569E64693BA132EC74F68AA62112E85E8FC7F0AFD4D1C84440783AE79C6445F4771A5009B3811731BC846C3BA1349AC32211334D693A3AA791EF82344B74F6F3CC44EA76032EFC27CB3191CAF4079F27C95BFF03EG179C00785A651CAF408D84A0DF8DD6327760E4FDCE72E5AFACC7DD333C3EDD01F9CD04FCE2F8997FDC01361479B7282B3FE1993B32B0BD738723
	F7AA7D01B7500583CC87C83E19779FD9C371454A3BAD0B1787691AEEC67F50D4C95AC927DE549C9CE66754E80DD0B4E937F17F72699274AFD995930422695DCE7D0B774D4C2F845DAC7B1700399F4F8C81E0FC572FC47DA55F5210D2F28B4AFC4FCAFBDF7C5ACA2DF1874BE365AA14FDG52CF3C85E5FFA9B71F49EEBFB64899FAF04072FCE49572958379E5CE44FA9D7DA3165584B8EF0EDF170D78C90093EEC55F9158263469A24037GF85E0AF2DE1A7D3F739D11B9E42F4BE6B6766B2D7547AC7AF2F3CF2B39
	4CBC2061C4383EBE603C7A829F6137223EBC92E9DEFA512E6AF9565CF1AA733AA18773CA3E8D6A6FB614EB5EEA320EEDF64808F51EACED7BEA5471G241BF89B6AECC507D2E7B100CF3B9D46D337E35E6F2E3677DFF9EB1007AA2071389D65697FB11B55BFABFA1F5EE653F56A6BA19D58G552D91FA0A3CC32295FA88295E43F9DEC110CFACAFF3F36E822EE7C5C6DC8FD76522AC5901D04701A8CB29D95DF778C2DCF0A07731EB7FF7757FF08759DB35984DE2B69F7B352E65FE8F4AC93E83F9C931E871D2F287
	6AABBA092C3D59F47DFAD0D89FA92B5436DFFF741021274F2863954F4A78BA8F4B280774DE3C0CEF5A55E504C2DC8CAF63B1BA2726FA647D5A23BC2FB820AB7CBB66E5F6601764E57CBB667561ACA23BCDB4DBE459CF2D433CBC00AE686F286757572947E38CBFBF5F5E1F6F4F67FD8C244D3E934BBA31184CC135E7ED59CEE6F9F94A6D289C66BB45A2741D81CF5FA1B49D3940296FEC3913794CD1F74A3ED33C7C2D04D1G0C57D84EEB84502486212C0D85C4A61D353300515DDD00B6DD81B4031C2EF23D6BFE
	655C75D8C6A0241B4653462EA767C7BB4CEDFD36BC7AFF0D59A62A8DF69709C0FBE56916EF309FD368B670B979DDB08E3E8B73FFE103FD9B7F36104F4901A60C53CDA8D40FA5987D6B1CBE69EE181B5F0DE7F4669C453320625D12FE6F164F0F0D035F9395E1826C0C6427F45DA374EEF6C6B21163E752B5058885DE3DCE928DF1D57760393E8B4BC9FF9FG61C9GD1GB3GB202E19E900CEBCCCBF612738AC31E1B2CB6C10519F446C3F69B45F8FEF1A7A937DBFFCC8ABF37D3B9C7E0FAD8E2A1FA288FF6FD3E
	8E0125DBC35399C7100E49BD3D023465F110AE88A09CA086A079DE143939A457154CF87ED3921671F3091BF20C9F319C2C7D667C22BCFB1E34E0CFC22213761BFD2F34CFF92F5CFE19AE2E5FC5F4B1GF89F00G00D000E82E0B6BCBDC56FF6500E67DB3595AFB5D530935F724BAE169E6DB5DAB6F9B2BFB65CDF4333C7B5CACEF234D597971247B3473E2694EF6B8F71E2122AFD405A51BF6C837D495EED225E3FD54FDF43CBB02B5D75AB131F94846EC75198E8B146183608F4056999FDF036B0CA177619A7C5B
	F40EB78342E97F007E729F686762F613B9ED89041B81C4GEF00C000F000697FC0DB1FDE324D252D138E245E4CC6BF656A13AE124EFEBE6A1E2EBB9D5229F51D9EE21F0E49E72BA67265C2DC95C08B40A0009FC0B0C0D4884AD7D93AFD187215832E4AEC6EF61490F91DBB601E0C5FBB24D34BA85C2F156ED1B74B1B6A10AECF1D0FE6F91E47DC3DA322DD5E45AE53655F2FD507EF6DA1F5D885F16DG03G3EG41GD381626E47BA74ADADF3D50725F8208B6C94FFEAD4A463F77FF177745A6C104E41D69F502C
	7F936E15176310CEDDDE2EE6F97FF6C8B73CFA0CF3192E6281AD3FF76404137DC33A11447C5B1CEE25FFEBFE002D610D02673887503F65D9503F85BE007E6DA49D5FCD03F052038DA90B9F44F164BF865D1371F157699E5432552E9AE22BB59057856081B08EA084A092C088C5DB1D665AD6796BB04331D55BC97764BC43E53A0CD02DF4DBCE39E72B3B9D5229EDB5DF33BC61F7774A1B6710AECF952E8A55325579073E5536D53676AB60DE5CD5096A7E186C4535C01EE2A85AEB27B5D96247AB143FFF69405770
	6A6BE91769C2B565D9C94F18E835BD183A6DB2680D6DCC1E5FFB3546971110E7AC1767CBF33214EF72C3A84F4502FB727839CCD77210D63ABD716E155764B2DD13E6F9E9896E153754E5BA0BE6F921EE1677344BF4239E52320F0254A55A76D159EE904DD6B28295F09FFBD99F5A339F2FFF6FC477783D4AE53A291A7AF9B1533D727E65B2DD2CE6F963FFF02F3CDBDC26CBFED8AB5DCF4B5DAB2F4DE53ACC4D72162DF02F3C35AE5395E916773AC33A6179738F9C52297B0F3A07356C3BFB97192B8FBD67629D2E
	F446053DB84E7C62FF9D7CE2971463856077B07A4507F66238A468E19C17641610F1C9AC04139FE9C849FC845BCBD016FB72BF6C10AECF2D6FC73446A5EF6CA37E3F8E620CG1EGFEG21GD1GC913F0DCD2D2C64EED8CF7DC62FAAE744BCA776C2A4BE53A2CC9DA69562CF2CF2FDB9C522975DA2CD95E638E6906A75F0BAE5355EB16F7490F6E49B75EA11DDABE23E6F99B335DAB2F5CA11D3ABC4FC9DA6D3453C86CD0239F428DB329BD5E5E29EE0F63A03F900008C9589E5FEE4376B8ED92363B0F5D14E7D6
	0EABF932A6EB257B1BC33A6115F70F4B720AB54BBB325ABDFB9CF0192EFE32D6FD9DB961243E4838120E9AD8FDBDF3C4BD2FB3C2FE5EG93A6E3FD7D52017595BC996B2BF00DFB7AABF3C827565FF4CD7DBD6BE6F9518E69547DD26A23DA69A6643AD7DF3739CC1725AA0F5543E5A6F53BA9863A96G71D12C0781B356035723D88FDDEB5D63335FE53AD0951F0C07B28B0EB1A3B98F0B735C63E1354BF4690FE969AA26DD2DABF2A6A097E8CB9EC3DD6DEFC7BE1B9EC3BE435D6473E517693C9F53525575961C0F
	79F39EBC573939C667B2DD54E3DA696A735DAB2F55E53A992A7258F82623160CE7524220BEGAAGDAGBC817CGC243F0BCF3C999F9F7F8586BACC32EDDFF68260CC9852E52C507E9258BF1C8B7BCDF7404C3BA35AF0A53AC6F42756E49F7254BF4690FEB2E69EFF02F3CBE1769F2B54BDBD668669C47E53A8A4D725EF6333C0FDD26EBD615476E2779013EFBA1C21CB7G435D5ACDF093A09C44F1977490DCA40427F35CADA6024BF822A1A57F8974A171743CFD858F2750B01977601EEE138BBE1D6A6B8957FB
	FB2C0CG374B984EBDB0523EC1397377462FAE57FEEF7C3AF26D7746EFAADF407010077236A40C3F3DFC68774A6FA957FA2FFC891567D93A07E76DB309069EBDAD160D417483645E8570E4877BCC4C4D1112ED6A7DF8DEC6DC05F3F993AB35651DDD29AD6F7C4A21657D2652193C529EFB9DFDD763897457B7D72E979C7DB5DB04C2016A75BAEBEDC787DF94E7E75524743B1C4BF647AEED596E5925AD5B7D3B0616ED72AEE73231B6D5C35F7798F8023D6F4C6AF0725773E91EB61B1E1D3821A7A4D95C1BA2B947
	6EF0C740FE681D84BC17935CBFDB2229944D7B082E58D8E5D42EF22C6285E58681C48244BE01E3151EEC9C2BA4BF096B93AF5333FDA590EE8230BC09ED7BE907712C92C6BD6958FE59B91B43746C6F38A759B91BF80E6F22F8FF851E1D3FF90F5AB48987C2DCC6B85A4117D54E6BF1EE15F6BDAE2AA2E7CFED66BEEB1BDEDDD75FD5E570B839FF2F9675BDC43D0CE685CDAF6A2C1DFA073ADECD5333B8E769B7D4B93387D6F75EC795F5D73E1E67FD325A392C1E555A321ED76DDC560B2ADD4BBA365A392C632BDD
	4BFAE3755032D68CA8E5AD647878E3CA7CE60E3F07636BC4F31D2C1B69FB1C6B6653BD5A3A095F6BDCB749FBDD6BE641DE673A49586BDAB7BF6CA56F3D359A5A8DD6691686A663594715EDF793177D6089A5DE56494AC1A51E1CE71256C8AA1D2C559FEAA3879D6D566A4B3A314D8F06E31B3F47C05ADCA00443G2243314DEF308C556663421D357977685EEB5AD36AB67F8945AFFECA5D664F6A45B61F8DF1DECF6199418F87D35086BA2C661E3A8D124E2DB54EED60D80D368D7CDE635C864E29F1ED839756B8
	3701CBEBDC5B4055B55AB6F063C9251DEF60F5EDB925EC97C59C3F79F7A5FE8B2F57E79371BD5B582730DE8F9DA1FA4DFE9A6627GCDCFE33D9A2DDA75EAF91A5527A7BCFD1F362FDBD676274A327749E55F590BB69570B4163DD6A4E5C7C3B88EA0FD0AFC1F60D0B6158BF4A5D3344A1EAC585BC22104BFA3FE0E022702FC245F1DE0CF3F91E5C83023AF40F1E5D61F122FFA958FDD909E54646B37CC7BF2B6C97805BF5815339E69277EE0CF3FD942FBADD75A016C9F9BD658653391710B3314F5C16E3DE27DFE
	315D3E047794F6B7E590BC4326E03FFF892F2F68A9D8DF91743E1945918DA905GB591D8DF0F6E9C2A3E5AA31C79GBF3A4FB190217681FF25F84F88358FD8DF02BE409B6226C770F786326C74D5DEA0353958FD4EFD40C77B34FD40E77B1C7B00247D2EFD405C7D4EFDC07AFE57BEE0497E217B480BD6AA6BD476852D2B6CF530DE42A77D282417ED66519F6D69B7C8F8E33652F748B6334C2C35475F748C584BB3FC6D3C93ED46739934194FDAC91D85C3B882E05AB3E8B331EDC359CC52330EB643784B4F1923
	642FG715176F85E16462FD64A3F114B3353E1EF0E2CE9E6C039394F22BCF33BF1CD3362D9144724A7728CC25887A060D91467F047D07204BA1527FB0DFDBD204F1A1BEB5726B7A17D0B76784DA84FF12B565E46D4A8F79A1767C9932E79A5BD07721CC76F31AE06F0BD0071B9146724268F76FC4E799C1C71F775DABB7E0A78999E4DF1022F14DFGBCC31E53721D07726C725B027DC9269EE5096032343491D9D227B624E48194CFC5D9FE561425FEAAAB2FCBFA8ECE154BE7E54FDA27ACFBFDBE4AF21326AC5E
	D27A89D31D5575BD7679F15BBDBF5F4E8676217F3254BA77DFC7EB357D5749DA677E4B3356357F9ADD6B5CFF79543A76DFD7568E6D3F56965877EDF8FF5AC785F63A616D776175F6753F8171D7ED304F877DDDC3219DFDA1FFB7E523BDFD1104AF22F79ED8684B07D6FD7BA143F1B28E0E1DCA6C69GDD9B8B025F21D3312F1954CD6E52B36A0E197BC4C170FB1E50EDB7E25BAB04F08D0088B06AF9562F3271772DC7C9799DD6DD0FF49106B07882C9FBDD8F260D817AB8004500AF86E8FE814BFC58C452C2C2C3
	CFDF8F65107167C377DCC201AE1C53A60904F64FA10B51C037525016EE2C1B2B2C97E9FD0071665DC56C3ADFF7043C89A86156A4BB19AF546BDADB756D52FC01791E338A354EB9449179DEC45F93120B3EB47BC5EC2F0374FD4B96888FGF83F086D556A305F6A7F225C2602DF34EFDF6190E6630603747D3E68975938013EB3C8713FD27C8C851E0DA72269BA868927C6C2FF96096B1A7FDA03EB1D911148131E30AE35FF136277C7896E2EB58E8724F3E409273F0C5D5D28732C179AD20ADE3257F14DCB4E46F0
	F750BB6DDBDED23FBB758F0A373C24FEF76AFEBA0F246F70C1DC74CB2873BD9654F9554B28730FFAE5BD8BD67977393D27DF9F577C89E53C484F3199C1E68FG1F17E5BD7B3F6CCC670F50B9DB704BEABBFF1A6243DFD65B794FC7504E23A12E6A1F287333689953G8887F3DC0C956BE19247BD48567923E0EC9405F5935C2BEE23A5D1B23F75D12A39D194365171F44EB79845F81C4DF93F1962FD95F8693E890AA761C0084BF885F9DAEEC6BE87B96ED60A8B03F09447ED23678E27C1D8F895F1F777E35AAC0E
	3B4602E945D751E7DF1167647CAB772E4C37093DB82F76F895FD5B847A0EE090044381E2DEC53D79F23D892FB19D253FA66BAA7BB5E7F5DE35130E53DEE37A1945717594DF2F40B33D3DC067EAA4EC04387057D04ED7F6226C0551485375A5F25B3AC5713B28647437018327358CD6BF0DE61C1734C4E37DBCC85713FCA1BC91E09257C5EC27522E23GBFBDDA2E1364571D55CFAF9DFBAEFEDD5DA687A9BE7FF5F51BCCEC43B6D981F17E2FE37D4CEA477AC9FD837B4E233DB856EDFE8379BFC56D4B874281G21
	EFA07F8B3B3546268811EFB8637FD5BA2F09FDC36D4727D13C70265A0F0BFA7463E990A73E097C77F7A07FD35FC47EEF33E27B08FB93790F6AA1698BE3E03C84509E037CC718547C8F4648BCFB47B8637FF3BA0F199023567FEC5A8F854528756FEDC27D07C1DC7E3F10FFB626720170840E7B0D7A0609908E613887F9FF9546F12FD1DCAC04135EC2F9D66A9D76635F12E548FD4B19BC9154BECA5ED24B73CF0A2FFFCBADCF873727AE088BFB8BF932D0F9725FEEC8A9F99BF14FF05FD943F17F21B88B043DB86E
	81AA0FAF89F35C8AEA0B01901E849079B679EE54E8F2F539B41E3E3EBB931EA67D51CC7E3EF80521C7EFA29FD349AE4A4F4F4D5C1C1FAF3DAF6FF7D4793EED511CB3B56F11A610B90D6929D0FAFAFDF139742DA73D9157040C90B7E19A7A6E0296BB5FCD98593AF5375550DE266B2C2123F7E61BFFB3A6CB69A32721ED3EC873CCFF877ABB000AF7706E4101D5F8D7DD40755788612FEB5FBD98D5FFB221799D4627077424F7DB2DEECDE4E5BE5603E586C37C74F7886E639E65FD88C41707135879B3963F466E5E
	9FB247A277644B713F5A4593FE8F4CD246DFE897BFD3111E497658CA14AD6E310902718ECF275F5DAA7EB71329C5FAE6BEF1F9FC7665F5DFB2DB785B4F32AD9C318CE18B1B519618CE6EA9C49F6D7BEF2C07A9F45C9E8E6198G61DDECD38F8DF90EAD63DDE7EDAA164EB7F25FD57721F194DF712E3A8FBD73C76CC31BA1AE64DDEC836752BD512CD898A747A2AEF1B53629E60E6B20B88F887B44A26FCDF4EE618761900EEB23B8F2E7ACF9EF0D615EDABD5CFE1164033F7DE29DE5F75E77A62E99EE994A6FBD8C
	670C846DAB5FA3BA90F2AB062041755DC489A2007F98002F24F0DC2C3C86AC79E23AA6D787F90B6F211E6F2438D1901E48F18B0CD89F216F21CE6FB113F383696FF3BB22E1F237B48BBFE2982EBEC89E4321F354F954304B982E1E15405694F4961C0F97811FB5GC6GAF000077D1BF6F533D20B08847F01C112E5F46C1B86D8344DDEFA038C288D7F15C4AEE542D7183545D63C788AE9842519CB71562489DF8643EEDE91D7408BB63BD12AF7E0E7E40396E25F349EE15215477CC97F40B0431D63C4304793D70
	DFA0A8597C97D63479B88DDE1576CE47CF53A14DF4E673A4AEEB3AEC7352FDA9A6A2175EC48E6434CBEBBF6F19308F491F0E75B205764D9590EE623803B6BECF6238BBA84E8F42015331DEEE9049DD79A1901E8A908B10749F0C8B24F111523D659900AF62F1DBC4352EC9FAAD3D05B84147CAF8D21EF6FD9EF4A843D97DB8EBB3332467EC0DB8D6E7DB4CC379A93C87846B2C89F8B57E075599614563BF6ABA3B4BC25689FBF456CE0389F4C7ED2432D96071877BC8FC2B59CA97D859BA2201FFC7538B721BG90
	8E907D9FF62FB2637329FED7FCAAA18E5226FDA87B532C8FE5BE99CFDD6453B5121E0F5139E9BE50D4FC68D897CDAA1C71C31C53E750391447076A316A8A0A7779D0BDD63D5102E3D5FF08CB7D081FA72476EB01309747CDAA26368A61C00E5B3D034FB1BEC2BB5CCF6741C9B39AD232810AE7A07E970AA777455781CE6478289DC379881247BCA9BFA53EF846C87A570F07AC83EDD8ED33330611BF817F99C497B3F6E68EC163A3714B78E9F6588709D5481374313DEC999FE37DEE26F7A167FE2C2E5FB20AAF79
	D8DD3F13FB31FE6BA1AE68E3BE376DA27536789318EFFD0238D25AC754C3380B63C2F87F6275895657D17ADECEA40463B86ECC0AA3F764133B4398EEF61BD2670496FFC7FEB272BEDB3BDE175B15614E58E8A8E06DFCE9BB367334CF357B9025BF4A821A22CFE5FFD473295C4E994F038E6BF5CD0AFC0D9AE530BA7E1036D58FA93F39027D5DD89394F8D6771E141E04A7C1DCE19C56E98E2FD33FB82C2B98BD69C3E3F978FD9AA65F88E061637ABFFAED0814E5DF17177C61E5A8ED000D47C6F720DFCF7F8C7C93
	C095C09740284FD056BB6D4E5B7883BE78B31957F0789D7D19FD9DC96BFFF49DE346E76A36F91862D3BFD7374DB7FA30EDE6C21C6767D8BFEF5371C48C0463B86E88DF63487C8279CCE26B84909E45F173A90EFC3B01FC170161AEB50DC42FA4BFE7F123B46224354A9115B1D45B549ACF30FE7AA6337DB86F7C5F849E57E7310FFB70B0091B7885DDFF35E43860E511700945F1DC37209747F5B33E40FAA856230FCC959AA55C5F79BADBBE6018BEDE207E5083423E9C77BA4579C95F38104EF635E9690D249B2E
	AEFD0720E5E51C2DD946C87C236BB178DFD4F573C60B2C7FE9E6577ACF3550BDD1697D1D1E9E1D29DD40F15132D6391EEF6A35AF6711B6B9EEEB2FE39D9E60F5B891744456D71E7C2FF22DABED2CB1817D331A37D0C9376152D33EDB6EAE3AB61C945F1812894076F51FEEA45FC5EC509DF1BC758E48BA2B5922370A4798165E20AD55CF3751557BA25A75FA4C4DF4370DB89D73E33554DF15003C76E7441AA83EC601E77E6D953E9E5884F141G52FDD0FAE28F19890DA945G75GC6GAFG7F84546577CD640E
	35A15F7B2AB7DBEA747DFA2362E83F747EDE4FC865E3F6F17FACDCF78B3C7BC6A1681C8A275F3F88CEE07690A9BD0342AE0FA7CFA65FC97AEEF3EAE2E3CAB6C08DC0BB40G00F7A24A5756144B64A366D0AA0A3A36AE7AA5937E5207C1FFD4C613C38A52BBE58E6F96CDC8145BFF907C8ECBEC54681BF653FBF323929BD5FB8A87A8BED601E7F5777C9A5CD39012007FA474F51139FCCFB2897D5A07F4EDAEE3E6E3CAAEC049CC44A7507B0F49FDBA647C9339E31C61C77D087E08508ED5AF99D2FC2E8BBAC79B9F
	4F4BF0B506C7BFB8140F5376D9FBCA9D4F1F5C466F77EB861E697993F80E82A0FAE07E703B55C86327A1974B7BA4DD2F94F52D75865128E73E6EEB7209E0A9FE170EFCB34F717B9C98BF66D7A2B77B32CF917D36AB791ECE3200FF31BB35F02C1CADC7845EA6DEC945B6317826EB050A67AA92D6B8E9930112ACC9DF4A3EB25BD0B8DB6A1F3B499D387D60G8DEDBAA3BB68DE263356190D0636E3122F762663D2724D58E2480325FB221B3429AE3355F05CEC929DD362792037E8DAF27745A024E56376ECA30E5B
	033FF45433FA5C9E8EB451GB8BF1F71E523EA9D2168FB92C7BF22C37C10F44BC83D74EE16D17AD69276B7C9339A257D04BBAC6A7D040B62157B81EFDA547B89A43D9C7FED2FFABF41BE7D5EDE75FE8226E7FC4FCEF2FE2EEAF992613798F82D1A05BABF2B53F1BDE4AC3F2FC184BAAF00A000B000987E5D09FB8D0E6DA142006922E7317690874F34E4160E55D3F1B756D3F632F6FBD056D3B15054A54B75541E2C2E276FFF7BCE231E14AFD7B03A4ACDC4177B70FB90027DF7FE089EAD1037E3BB41F1493564F3
	5DD2DE49E6C25F21B35A64F73D5E6AF554C7BCBF9B6A1F4C7491824F88G629F19FFB05810E61A93FDAC9242EC0E7E61E1234C4F831B096FD55C7EA4FD77F4F9B7BD1346EF8D43B3FF070B283F2851356A0DF8FE6F4615B2EE03746D08DA3B3DABE1B663BB8B1EACFE0EC5AB3EE836169E35FC906AF62D09D2503E6822BE6C0BEAA09FCFG362FF869C1FEAE0E5E89C4AF812AB11874E554F44B74BA39CE1F6F637BFC101E6DE94D225FEE1A8E6154B90DA9D9F3064BA37A49BAF254D35A8B7126634CE24823E98E
	7615AFD21C9142DE9C77A3BF536347F1BB59DEB50413D3306FDCCA53AE06F0BE4765D09C79EEC590FF27F7225B6F57A2107CC9B94E7B52497F6BB2E68A4E679B52394ADE67BE2A125AFA8570D81702BE6A1F450EED6B43E2ECB703C06783908C90CEGF4161A5AC8762CF6E4CAB4B19AB247C9B8D646C3BB080F31E85EAA05B4E7E4487DAC3625642F65F6D3A01D4B7CE7097ADCA661299EF86ACA4536B331DC5DF6880DC7AA4A7CD5891139DCCF6EA4477E7928DE59BFAFDED612007D07DAB61FD4A6DBC82A5CBF
	4F6376EBB363BE6A74D4344BF7781AC346DC44557562BEEA71DC344B6D54262BA05C4CF1D594C76FA63995ED75C9FE0E0F50A2DF4DF307BF369B8A24F5ACDE46F07710C6822C9E16F6B9760F9B3B30FEADF3D97FB88A1E63GDA78B7C81A0EB85A2C195F2B93B8177587E1700C1252317E71B95EBF4E505015FA9C13BA8F662473647E31F01E3AFFBC50CD460E079C3EE5427B2DF54932CD97B07FD95B0EFDE2493C117A72A00B406D0C3D4BB4GF9F84DC31BDA487745A64EC37B0923EBB642FC704BGD5G6DG
	DEG017311A6053EB7928961B80045E9B01F84E886A05FB1E1B467D9DAD47AA2F9D0FD4F17F5A9A8FED74DF7ED0FD992BF7636F71DC3D9A952F32E4A0614690663BB15FBE54CA6CA38CD8C26E95B44ACC1763B5EC0B3A1CD360920B435CD5C748B991FCB5F5D50338F5D59789DD04C86EE6FC09B88CD43316D5B9D6A316DF302F2EC3A24F36831EDED27FAECBB5BAE7E0CCEE7E3DBE6BBCFD8F11FBCAE8D6DA9318F7749B397A06EA4BFE7DA41F15B68599F9F8887ACC01B7A052E3D06C0B802637494C73E61E264
	77DACD6BE9126828BFDF704778B0FBFB1AC957D45D191F8E5F17A5189DFD5237FC3DAE86649296B626E4839483909DB07BBB430276D73FD05B7E9295765785B4038BE57B73DE28363F08CE722EC81D51AC62E0DC624FC0467207B43E1503F42C9F1C742B724CD4661ADA4DB3D3B3FEBD1EB0CE62B7E4215C8F2673F9EAF62FF29C4F74215723BE26AFE47AC8DD84BE96006CF5B1FDBC5E017AA8DA64DC9F88B5C05332C8560738C82D0F817A0E8C75511220529AE7975153A151EC3157D3179D5FEC9D6AC6FAE61F
	2DB7253413EFCEE92ECD16360AFBF47D3A5A56A37AB631DE5749535C57CEF8676F2930A5CBA96EA17A7DA17E1D9F7665A97A01A7E63BBF75885C0EFFD37CFE53F87A4FB18BBD27338CA67F87FA676F03FD2257FE9FCC68503E27644B0E216F14195721F5278C0B7B2A03543B7928093E9B466A7DCE7A9DC58F3029F1GA1G91GB10BF06D715A8E725DDBF61B3EFA5D31323D133C0FE693F5A6113FEF5F69DC56511DDF4B3212D4EDFA3C2F46465F3727ED3B558C765AC372C77CB8EAABE04A94A95DDD51C9F4E4
	B5F4F6090CD4425FC04FC399759D944D7C77F305F89EA1A99DFD7A01CD686733B96EF10A2B0730B19DFD7A3EDC320F6041430DB4CC5E23E561FFBA1C47A334FF2C1D4EF0A8630F5833C6E0FE6983FE664FD7037FAF24BF74CB81C09312AE7B2508F435DFFA68BBF93E036B84376748B8FCF7B9F9B53119D27E3DA8B6EB125E074846FD01A848BF632BC669EC7A729C1C2357FD05F374BA7A5D3BD1907683886234A52BF1AC927E956EABFCA592D9133F06B1A3C095C08B0088608560879088B089A08AE086C06AB7
	6047818A81AAG1AG0C5FE0BBB9F5041C4BF5361FA39DE0260D452AB31066A249FEC3B7F9D7DB79D5D4454D9EF98D15876B2B4BCBEB8E9FAABFD8DBD3D3D6FA70F0E9CDE3694F07F87AF86306A152D7559E2C7E25F6FF3DD68ECCB7BF9BD1B79E5F0CF4CD633DBE015B2757107709E8814BA37D2840E551F512321C1CBBDE3FB1BFB7F3C3FE0EE4978FF462BACF90709D7B8D2E737CB6C072D02D7350F52FF645CA8F13FDCE974A1E1D01B674C197FDAF8E4283G5EG2C3CB8835A21BF6022B25036AA0E209DE6
	FC0BF9D869FBDBF5906E82707896E9FB3A31BC5FEFC72A6B4D83322E6D3FFD948EF9C5FFBB343FE1B264D8D10699C01F7F9D7255EBC5993A3EC3992E2634FE908E828867349772754E186FD0679FF4B8795687DD55E0694EB1613BA4195FE3DE25F44D000C3D1A3EC75FFAD91FABBB8A7E5EDED6E9BE65B21D7ADEC4166EE5176954407642CE507540295F2B77B7BDA9BE7AFB757E665CFE54FDAC4455AF46F51A9BFA096FEBB7749B40B6EB4CED6C03F9ECADAB044EFF497AFD9027FF3F1758878C89448A4DB429
	D6FCDFF752E25C5B58414F35CEDD6CFACDE09A5088CBE43F1E3EC46D579BACE43CE9369A20EFD716ED627B9919CB105F1B4BE5FF0FF7A4BC38C746ED147C40AFE4FBC60A3F37B8D3F06A87B612778F56676767B03DAE5D0EFA2D07B23D1620CD3E11E7F7D7B1FB0F04F9G769A096483BA36A34F21CB5076FDE81EF190CEDF5A18123B946D751C9DE87B95CBD13E7BF650B13959A24DAD0E24A95758A20FB5B8DDE39BDC4A6A42FF297D9A9BAB6B1BD2ACABEAA97275D9995DBBDD86ED9720EA997275E6B921A575
	55328C7B37F30E939D06C2B88AA0A933B1E5B1C0BEC095C0B3001809FD170FB9DBF05EF7B12549DD971B670C6373BE6FCC3696658F4FE0G32FF4B76A61278DD9D91190EF6476ABDEFD02E770CDE59AE706E1C7BFFD22CAF6533BA7F358BFDFF8C641B79836A61BA6E4F1BFEC01DDDC07D36B7047D81C2FEC0FFFE5991341B2900CBDB0EF97C137B732A6518475CEEFA168E423EG93B9EDB26FBBA7AD575E7BF16E4F8F1CA4A166036A5B15BEC8D881F637E2E8FFAE6D39E86C995D4FF51E3D0255CB89BC6B8148
	FA847387B9B67487C60DF254FBE89EC06333C276877EAB547E6066ED244C7DFDBD1564FB1654A358E817CB7DBF23F9F326A38D1DA4AAE83A693A7E1EBA2B39532A37594AF45682DE46CB9544D6593C53502F5F55E72247FF64E2A43A743D1AF4F2D1925D3CB252CED4EBD8F6F72AC95F8A6ED52F3393BD862E40F5784F4DDA7113F87CA3F67111D88FD9986FE354CA3F1847CF510C4F4F62FDB79FD3D4E40DF4CCF15D6F328D86595D03B388F9F9E78DED034C5EBE62FB14933258DAF6B0BC438148FABE23496567
	63E33218CD8AAB4146D7B29AE613BE7CFCFC6ECA57B6D982B475ABE51B6CDA29364903BD5AF7EF9C216BF8A250FB2CC49FBE59A07B9D3C636B3283B2EE0B5407171A51E784C05A68155857ECD8EF777DA856576CB21A0FCABD8D7A74F34D68DB962FC25FF2A74D339E42C6G4FD5D82F3771FB3E472D9A699A7AA47A1B75835744935DC7C09E31G69BFC2FF86D0825074A37A7FB84BD07E1F89A17BFF69DB3AFA7E7EA664B361C7647B99BDFFAF73C714B131033E17198DE583D4E4A36D67FC0D38B9FB24363B0D
	9EB7E27C7F436AFC4D61E12B6BB507A76918C95F5E29A7BB9C8C97E1A53E555C51E15373350D672DE99CD7ED2BB35B8C4435B09B97B6220D7BE4B39B8F00E788G59FFE57B09DD1BB2A51A29590EB6BEADDB391FF8E21F367DFEC857A7049C180F66207D46517BD695B73CC87BB0D9EBD13FC5B9A3556F719292125E39F03857459530F47FF6B35D0F8E5F43985E18BC144E7FADB96AB1796394BF18239E13B764208D0F0238E9B9B88729347BBEC65D6A46147655C37BC2E69FB922F3BB4C97DD5B6196D1FB6DEB
	27683876D5A1EA2FFD6D9115EBDF8CD7AB929B68349A5A1BB8DDBD477D4CF1BF53BC892E5150AEF6F17CE14A9741D769C9010857D33E0ED2DC1304BB22407DAC614CA2E9CBA0F217590AE9C5112C21F5980CC6969E20B48693B4AD2E0F1314C703C527607BCC0E7B195B4BEFF6E7D487202E3CDC5497CBB729533D396633AE53E94FB567F7B9CB672741EFA060187DF652B16C2455EA3B36D0FC44EA35DDFF5C45574CA02EE28DBFE3F9443D761866E63A6A9127E3F23DC147667EEB54723ECB7101EB54728E7031
	FC8844E565223CD63BBBB5C681FEDC6E50F6213C4BFBE2AEABC7DAAB2638C9920E653FCE732ED557FA596EE63A7E9127E3FADBE423EB9239EAFDAE267858DC35BE2F636BC4F1905732967569E3376E948A7829EB07534E4EF7389B0440B4A92D10471E69704C16FE33F43BB44F9295675917D9A7055B739CF99910F0DE5253CFC1436EE18FD9C4669C861351E05213AF1C6D561BF879E7748B7C77F90A5F0B7ACF7F3ED46EE9AD030D9B1E6BA54F757979D2F8051554B919F3317103590A6307403C110E9F2EA447
	EB25FC4F36107DC47BB5EA3C09C93144DDD7D3DD4F654F5A6EDE9BD96FE63A6E9127E3EDA8195E57B4ADCF5D3696D07C0CBCF55B9A4B572B1256B526F42D4336F57E8EE579E1000FDAB7F45BE2637BB34BB261B99DE853729BD38A81EAGDAG86G3CGA66463B87F6AE3C33DF3422BC431CB41F8DBDC2A56CDC83E4CDBC43EA31F525DCA65C45618FC35EE0ED37C74FC35EE924BD1B7C2810C398AD0B7534A55EDE19244C59630F2E325E752FAFBBEB2A01C8BD0224057436FAE00C185CE3AC77F3873311B6FF1
	57E33771473547EEB79CF79C3B859C579E3B5DF65CF16CF627142F15FF9C02616FBDCE7C96DB7B5927B369BA691EAE53E3A81F27FA031C135663FCAB152F4F8A9BF0EE36153E77D98461BA002E8DC83B1A5FDD6531816B214D697885C1FB3C546BB21D76F869C6276902B6B85ADC58869CAFA55071C05486355D25D07C348DEA3B7361FBBBB3A02E3990653D524DF14FCDEE260BF6F33CF486DD1F8CA9D44BFB9E4507952A65DD544D57BEA12EF8A34ABB57EE9C6D8778400D4399AFF537BB0E17AC6D4A7192AB77
	DF7CDBG13A0DFE1935ADA815FFFAA5A04F6F9180EC1AC909E856047E9FF617BCFC11B6C7B248D74EF4A90FD52EA3244A475ED837C6E3FD81E77C63E66123D9957F6662EA33629313725D82544BCBF675F52EB590CF9BEC6570849DC5E17636674707D684D287BFE3B31EAF2D1E34A622261687E498E547DEC0E0B68C05DBF47F53F4C06EB667910EFD7917252497569D7447751695D0DD3A19C8B103C85574C6B7B307EB2815734857398474F4BFBEF41BC9E253413A09CC5F01C7621FEACAFED6BC847947BAAC9
	087D3EE317A0607862411EA1760D8B49FED151169C3CAFF548EF94B8EF1BBF39196E029127E3EDF78D754BD5DB55ED3A10626B372A5B743D5C6737C0DC68D6342BBB6D76653337C1FB5FB61C350C1B621D770737453B6E8F6F09576E8F6F0FF76C8F9F0E576E8F4362157DA13E776FDEBD0CF7B35D8BA3CE476A69A8DD732C5B262EBF8F0AEF5E262E3F599DD8FFDD9097368D6BAF51EE2DA13F9866734543596B4F5E485F2785FA6FE2ECFF160D58D6A795E3DB3D0A528A5B9BD3B2G7237A32D77A6EC2BD55BF1
	2DF15226AFF93DE2DB1C3A1BA4E771FFDF13A9385E3B155ED1317BDE2887146133FDB87349C749D19B697794456F661C53BFAF6BBB2DE5B07F5D3C9AFD7AAB7108532DC61FFEB54775999D7B5D63C6E53F4BF02B683E28BF68F652F6BC67F60262A2A19C3B9DED2D47A06B32DF717B0B2E532F570CE15F2F66FE99530E48B27DC7717B14DB770E0D2C6C4BCEEB99527D065DE88F3FA6A06E02EE34074684BCE32DDBC55A3BF49E17AE75B27C620139B29E17FAA57E3F35497ADA2E78FD1375746BAE7434164174F4
	93DF73DBB18BF193ED0EED6E9AFA0EA98E5ACC6A8EECCB33783D2C999CF700623AA0BC0A63A6D05CB88807F0DC1785730B60384996C73A77A9AE8A4242CE44D552B1ECBA048BB94E3797F9296238D013129726D351EEAD5197E883F68E47774E5BA1577DB70A5FFE5BCF3F9D5CF5DA4B106AF7875A41D73C4EEF5801F670E0BC0ECDC617B99F9F7914399E9FDDD926BDBE3A364CF1FC64DF26BDBE3A394CF13D60F61A2FD55F897D2DF23D602EB22D7582A65F06D2FC1FAF045B4CEDE5E8C751BB51E60E164B756D
	2178BD5BE1FD660F2F7B6527358CA6733C8AA2F3DAC9E3CAF6896A2105620AA1DC43F18FD1DC9304AD9CF72A0860BCC9186326D09C3997AF1863FE4AA738F08847F05C8D9497876154D2442D59077A4F643876FDF8B63D06631650F36DAD9076A845FAFA544D394AD4B753AD987159DF695B816CAE74D2759879CE0A77ABD50F11CBAC7C7C85442515213C6B6C6EC89381BF2AECB8F3677B6998E89C50861721BE3F26772606C3B81A671F2C98ABACD07C9E7BA70CD9EE7F934A78CB37AC13376277375D273F6C75
	27358CD627D99A63649C3BF1B26365DD073B7606A743E7EE265B6466FE5E943AB696DB26EEB711949FD726EEB7FA3E1F17DC8EF33BF2346B43766748819FDDBE1CB140C9CDF9E7C8E9D3AB24732C70AC2CD06E67ED557493D592CDF305E359529CF8C87FE2F11AAE5CEEBD53D32273151E8152B3841E91F6FC4E573C93AAC6220953A80F595468EEC7BB3B385B514ED65ADC553B76BA7A8617693457510DCE5325D5BA4A12D5096B6821F49D30285261BC8D45D7D42A6D2CC1C4BB2B033840CA7E1E6C084F5833F4
	DDEE269B6F66197E9D740CFE46AE353C3BA9BEEB17DA5EF03E9E1A8FF1BE3BF87F5B2FACFF8660D3F78F27DD9550F75F312D483F9FB41FFEBF9CF9DA4B105E3F36E07B38AC89F1D3AC58BEA6F3DECEF648321F2378BD3F63746BA17B341641E47E2E5351CFAC6BF47493F99B5D337FE2B753750DB89DEB9F8BB711F61039DB5DEE3E27785C5D6AF6B3012FCD96C15C385D58EE46EC32BB87DF551812DEB51CF1DC49DA54674E19082BDC0B7A1C920FEB135B3B48DD96A2B9B23E274FA69ABA8C522BB352594AC679
	FD30AADC6F3C182E0D50B4822EF7FEDAC558E371F7196C6F18DC3A0744C9F781733B491BB564D03FF7E3D45DCDEE29D21FE96EB213FD9179FE33D2232153C45E481578BF530CFBDA031C7FFB4CDF5B252157B4C8A7D7D16EE7F9BAFFC84764A85DAB4BB81B1E01477B5814B930746C5BB2749DD0DEEEF36F82C59A19D3AC331F27912A71BD25DBFA799970EA576F45AB75C54F04D74B7AE2B6F03E55311DF9DB156D0C1D5D551562595D1AEA764EFEBBBC87G485D636C3E6E49741C05B3BB742ED61E5DDDD9C166
	60BA1479E714F9BB1DB7CEG5A20EA5453518AFB7BE92F11759ED9C14FG6B86D4E7002753FCE2A00F69BC1FB1C52E6BF9E291FFBFEBCFE38A4345D1DC9D041B77E0DE46FCBB7E1BB03DA71D270E823AF11C763EFCFA0E934AE4B49114A68C622782B05D156D9BCAF749FB25F39CB47DA5F40E3B98F059FB310C4242216DFEFFA14AD53F97FDC920227F3CD771DB7BCC599F5B079CBE2DE5B0BB5E556B38DE3937D739DE49F4B755054FFB1F7BBCCF2E6F3586C71F7748BED9074FAB3E69782A62F7771F30966C79
	A72C8577EB1C7FBCE1F77ED37A669855B7794ED650DEDCBF78182F3F06F2DCA5752F69B5B09F2A616B3494D785E1A3479D605F6D7260385B783DDADE9C779D47C5F25C1BF4BE1B3B2FB125E29F623674E0FECD9CB736875336F31C5928411F51113FD5BC3FC01E76AFBC3F900EBB0B6AAAEDFFE3CA7EFE44CDE57732C138E9BF9FA70B326DCCD17CEEF678EE489FEFC7FDFFC299950AB3237B943F1F6DBB7DE53FFBDA4B103EC9596F58CE5E68F79C47E6EC12E57FC1717B460D27DF8F8F1E56B2184C811BD08F0B
	781E412D1B14FB864C2F1A3AD6897667E059393EBC657D84F965157B6BAB8F621922DAB37A67767DB8B6FBEBBF51A14B73F2F326AA4FC364FF3C164516F730FAF7D5BD23FDE78434DBFB178ECDDBDFDB07BC0C9E96AD13A70A1FAB9C476519DBC764E1F303F3CBF1EE9081712935F8966129D26C63ABEA715C429714F69042BEG813558FF3DD1C672BFE46E236F30B81EA70A5A49686AABD6C8FC53F38A65669E0B5984438A298A8E1756D46F5E3F8FFECBFDE07E81D96F36A2A23313F38E1B0A58D595926DA3C5
	BF8C2D9F2CF2291CC38765F2BC578E279C67B2DE11BFB29917B449E5AF5B77033A6CDDB0G4BDEDEEAB5680C4BF3B38B60DFE18E27BF3C0F68FD3F59CA754E72ABD1FCD7F0E16170F526AF9C2E4E24B90AE1A4ED0B7156267876D79A3FA3A13456755CE3AA50CC2BED64F3B521CEBDD7EBF6B1D76B67F32E54BA345BDB06B1D7FB1427AB2A4376F34EFFE5B9A6733B5634A0D1856DF576F28CA864E07995707CBC6B3475B2D3106FEE73D565976020174F0D5AF30B2F69B8A288684338DCFFED16653A3507FF1B4D
	899F4ABA1AA6554F77929FE987E4BED85EE13F4AF9DF4573DEFC40753B4779876CE52CB82016F12CED68B1F7A0BFF3D9F7G6B7EDD1B63187BDD7A4D92560F555B6D4DF9C2BADF8DDE9D7B3F576D566A83A0CD483052DD68F066FFF87D6D15EE26FBF64469589A130D5E0991F1C03D76B4C871D18754EBCFB34B30FF1986F1F58779DDB4723BAE8707FB17835653429601577D689174312F8C0B5679F9580DBA92E27C3772334B531C704F52B435EA4B92AB7597EBFB168B1A7D8565C551DDC8653E4A4FB2E79D42
	F6FB22CD666BC99E5FFCC87B3CAFCBF3D53B2C4347ECA371655299F43363D814FDC359FE4E96576BC897EFF59C339EAB93786FB3953FD34ACE7F18B56BCFA8E3175D5E238F540D7F21617805CE3B3D38E0C893BE2CF4EF5A6D29C5C31A9943CA97E117AE353EB1A533FEB8EB423FEA2C7D35DA9D6D611445116EAC0BA35D67BD0EF409A6C73A37B568E2FB14F45FD09B7F22179C048EF8C718E9338A8B9D47C32597BC7FDC40610A7484A121DA78320B9E462B96E69EA5277586AC0210908C2DA55356AB1CA236C9
	C2894122102233594CED861D284F4B03FC6773FC2B690B55D17D9B36A63EF6EDE2021084193243C755C292B9C184F9DBD999092259C2CA9048C1DF7EC7CBA2CBFD4A6143172210C42EE1950565645655CC1BF46DEA264E16C906F9078CBDFDC65A756565650942CCFAC5297CCCA2D7130A423C0378E512CC729D86DE1220D93E9D08429C795AC04D04B3B531F334339B82CB7E4868759397B41320F319AE8A29524DB2760C8D37307F8BD87287E377F8EF35E88B5B21125675102FE5E47630EB6E21AA49D9B3303F
	8A3D2D4DEA30902921EE6A70BAFC1BA8ACB2ABB4C2AC259F5043E7EA0EDDADD428C2D6E12E5D946521719B5F88FB394116A8FCD9ADFCCBD6278F692D7472D9FDBB313186A89B5858A54C36188D9089F8E887B67A18E9930F9975B5C2FA47C1FDEF1F5EA66A5BCB713AD510632B9AD252BA30F3633AAA1D2DCBA4477A365588C9A62A0105AC5A28B3F52EE3F75CEE230DA93E340572CBBCD4BC703B0872CBF2496339AC3AE77E4A9F76C487A58A893B04B9C6332EFD172ECDB4DB2B0559E217D5EF6BB29B5B87ACBB
	CBE8C649C709D3C9218A338A5FF3710930F9E46640196E9E3852EBEE4C5CDD89B03E378A6BA8A559F04863946D65DDD0D3EDD0D155GBAA8B20B471D31D4DC75786EEF1A9304F8C9C8227A358350F02942381BA1A2676D822E45975E19FC4D0B453153A049AEE1A6396FC98F852749D47B7BFADA7556F38246ACC97109BA1F70E5A16A2115DAA08ADFF668FA8C46E37023826A4C5C49716CDA899A5CD8A24889E60BD61D4956E13676GB719AAD5E09417A86F124979E37E7A49AFD4A2B929A8ACE3F587C957F56A
	4DBD6BCABBBA8CA6BD4FGAA9232DF4233A7B4F9AA1A058397FD356719AFDA4170AA04441E7CFC7AFF817DFFBD793F00E28AA826E0BD54E62CB0641FBAFEE000483CF5403E8100D91A28CEB34D9D19FA7906ADD1084F9405A52DE03819E63C93AB33C33A94AB13D68D23C9C65F7D673A9A816F644E79DD791D3D99E737A5607D6D6A7EBD763F527B3E4D6A6F6C4D206F2115G3E39995FF92E6AA35F08F2F61775219E1D5188B36D6ED2D3BB39573A6C1828176FD27E2C0F64EFC39AA91245A575933E0FA8527C8F
	D0CB8788DA00C61416C4GG9C6EGGD0CB818294G94G88G88G400171B4DA00C61416C4GG9C6EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG50C4GGGG
**end of data**/
}
}