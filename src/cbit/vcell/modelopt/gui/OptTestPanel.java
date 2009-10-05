package cbit.vcell.modelopt.gui;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.solvers.OptimizationService;
import cbit.vcell.parser.SymbolTableEntry;
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
	private cbit.vcell.client.server.UserPreferences fieldUserPreferences = null;
	private cbit.vcell.client.server.UserPreferences ivjuserPreferences1 = null;
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
 * connEtoM6:  (userPreferences1.this --> referenceDataPanel.userPreferences)
 * @param value cbit.vcell.client.server.UserPreferences
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.client.server.UserPreferences value) {
	try {
		// user code begin {1}
		// user code end
		getreferenceDataPanel().setUserPreferences(getuserPreferences1());
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
 * connPtoP2SetSource:  (OptTestPanel.userPreferences <--> userPreferences1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if ((getuserPreferences1() != null)) {
			this.setUserPreferences(getuserPreferences1());
		}
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (OptTestPanel.userPreferences <--> userPreferences1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		setuserPreferences1(this.getUserPreferences());
		// user code begin {1}
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
			ivjJEditorPane1.setBorder(new org.vcell.util.gui.LineBorderBean());
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
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder2;
			ivjLocalBorder2 = new org.vcell.util.gui.TitledBorderBean();
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
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new org.vcell.util.gui.TitledBorderBean();
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
			org.vcell.util.gui.TitledBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.TitledBorderBean();
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
			ivjparameterMappingPanel = new ParameterMappingPanel();
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
 * Gets the userPreferences property (cbit.vcell.client.server.UserPreferences) value.
 * @return The userPreferences property value.
 * @see #setUserPreferences
 */
public cbit.vcell.client.server.UserPreferences getUserPreferences() {
	return fieldUserPreferences;
}


/**
 * Return the userPreferences1 property value.
 * @return cbit.vcell.client.server.UserPreferences
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.client.server.UserPreferences getuserPreferences1() {
	// user code begin {1}
	// user code end
	return ivjuserPreferences1;
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
	connPtoP2SetTarget();
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
	
	cbit.vcell.parser.SymbolTableEntry symbolTableEntries[] = getParameterEstimationTask().getModelOptimizationSpec().calculateTimeDependentModelObjects();

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
	
	SymbolTableEntry ste = (SymbolTableEntry)org.vcell.util.gui.DialogUtils.showListDialog(this,symbolTableEntries,"select data association",new SymbolTableEntryListCellRenderer());

	if (ste!=null && getselectionModel1().getMaxSelectionIndex()>=0){
		cbit.vcell.modelopt.ReferenceDataMappingSpec refDataMappingSpec = getParameterEstimationTask().getModelOptimizationSpec().getReferenceDataMappingSpecs()[getselectionModel1().getMaxSelectionIndex()];
		try {
			refDataMappingSpec.setModelObject(ste);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			org.vcell.util.gui.DialogUtils.showErrorDialog(this,e.getMessage());
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
 * Sets the userPreferences property (cbit.vcell.client.server.UserPreferences) value.
 * @param userPreferences The new value for the property.
 * @see #getUserPreferences
 */
public void setUserPreferences(cbit.vcell.client.server.UserPreferences userPreferences) {
	cbit.vcell.client.server.UserPreferences oldValue = fieldUserPreferences;
	fieldUserPreferences = userPreferences;
	firePropertyChange("userPreferences", oldValue, userPreferences);
}


/**
 * Set the userPreferences1 to a new value.
 * @param newValue cbit.vcell.client.server.UserPreferences
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setuserPreferences1(cbit.vcell.client.server.UserPreferences newValue) {
	if (ivjuserPreferences1 != newValue) {
		try {
			cbit.vcell.client.server.UserPreferences oldValue = getuserPreferences1();
			ivjuserPreferences1 = newValue;
			connPtoP2SetSource();
			connEtoM6(ivjuserPreferences1);
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
	D0CB838494G88G88GB0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FDCD4D596B0ED5632C5C5AD95159595EDD4645AC6ADEEECD2D9E1D1CBC8E59B969535D4D4D4D6EE51767823B226E5C9ADEDD654EAE1A2022022D972DFD4D4144930C899E00401E1E61CF94070477C4E3D771D7B5E3CF9B38333596F7B7D3E0F1F47B7775CF36FBD675CF34F7D7B6E9372BF8D1B1F151DAF88D9738569EFE316A04CFDD690AE7E53570B90D779E522CED07CFD86702EF079E8AEBC9B
	A1CE1FBFCB37D438F7A88F42ED902EDBB2CB77EF783DCCC85C127C847C70D0CE944405ADFBF01F77F25E79864BD9C843BDE30B6179B6C0BB6048B3921ECB0500318289BFC942A3E4E5832E0850DCB036DC42E5GCD9E602AG74C50C5E8CCF4640C91BABA53A36B3A30439F75A331710FC94F9A244CBE9B324E72170DAD668AAD2568C4AA7C9D38461F6GA2FC0E70F657D7705C525C577DEDE707C9AC996CB41A4DA57D56AE2359EA93CB3A87CC1BB76F3109FA23C3ECB6D80CE6D05FD7A43F45C773E8FAC1C806
	3C42A86E3281D2C6AB7C368184FE057805A3841F8A3FB38172D723DC5FFC39C5623BF5744FC2523A7E5CF7BC48D53C1A4923DFAD4BD5E719137DA12D03BD5F92DD87C3DC94C09CC0BA406262C4DDB140C7346E77FFC3642C6E90EB2D7D7DD6CB354DD6E36E326FA2F2426F4D1BA1C6426DB3F6DA6DDD02406A774E31E28DFE3C81CBB7FB426918CEA24A312EA377CD91122FBBA7FB2106CE12A3C6336B0A19AE5A0AE51D30325FBD6A296C31E2D7DBF4AD7BFBC1D93653F2FF4EFC0D32C3FE3CBFA7D84AA7DCD1F6
	C6B1296B72C1D257B360F7966585EC11626BA83EE20D0CE7F6F9A5359992EE073874B5E89B2DEFA3AFCD39178ACB4B4E5554C368ADC1594E9246C3F0091A17C35466E3CAD819BFF1DE4E21780035B21E7132045AA9895715A66ACA4B93F54C3EDA2BC8BBDD3EA1D1D789508C50839088908E903D81ED6C62EF560E53461A8DF631E640E46EB25A851636755B8F60E9F40AF643F60BC3B4D8BA0D7B8DF6132143ECF488920D61DB22038E17F407296DF7C2995B0C96235DA01A2C96485D50EF940DF6872331FECB74
	5BE9B752D83D299F9A33215FA6E03EC35FAE03275960903F33F599C423CC41620FFDCB6AE450309FF2048C60B7534BC955C4DF33C17E8CGE1A36A61B51A5FCE239D7C45664DCDD6EB5F00CDCFA4911AB792BD97D15F118F3F0BB7220ECF28A1389088C772FC4A363E2748274E54C958B75807310DED9AAC04E7AC504F864884A828CC54B582F4818482C4D622BDBD570A76D437649A21783E33B56DA92F6D7E1C5915128F2DD4372B9D34BC42F341A6308500D6GA7C0A8C0B4406CCD58AF7CF1A0C04A13700679
	87D63A373F8285AEF623F67BD4C223C69E8811126C64B7518763A51173B2DFC17B0E87BF003FF3FC70C160ADAFF14466168832CFE4757CFCAD3119F920277C4D093A0A4DD857662D052C6D54989CC6ECADFB6CE06846AE396619BC4631E8A9CFA2437BG9F831481FC8A3092E0934017G2BGD6832C81A0ED339E209CE0BD40C600DDGDF83FC8330DDE0ED7FC700FE006F846291856184GAB4091E20BG8600BDGFB81DAGCCGC7814E846886389C208F60EC00938126819C83B8GF02E0036BB194A508472
	0BG1381E6G248364FF1D285B82508590849085108C103B855A8B009EE08CA09CA09EE0AE4072EFE01C83E08688810881C885D87CAD6898208DE092C0A4C092C0A6400A2D093AA6G91E0B2408C00F4007CAAA89F208BA088A08AA099A0379A4A8750D723DDAD9BC949F66F7B14E1250D3D2F0A7BD82AAF8C2F74D0E7E52ABA5B2428B3E5FE7552F33BD4CF3BD4750475F9C02A631FA4FA03A20FEE29BE7BA49B40FA57326F23D23D1EA0553327F6F02674BCDB2AFFCFF417FB09D34234F1527DFF78E500597ABD
	EE362E760F73D5E914F1442FAED104C93BA0FDA4E993E4EEC2FCA37103441F12B6C1669A7F95188F45B6214C6FE369C95A89E987246DD483548A2EBE0E58B9E9C3EBA47A2D0ABCC87B58A63076C15A9836ABAD9D10B6C55A8DE9FF5FF95155CE69C95A5BCFDE68760CD3775F7FDFD07F7FAF816B0BBB2A08FD9A870D96314EE8B3DA3A6007C30AB3D591DB164629072DBC0D1562EDB02E2D0501240363872A088D9BC451EE6A98900DADC6D1B4D93A65782334AC47C007CD9E92E3D9132AC81B328E413816537F2E
	0A0C89BB2D960B118E8AF892A97ED4BAA6B51A0D7D40F42DF540A2B27C1955E45E5EE79C164644E7D7AF90481856BCC04657ECAEBC55C05A1F4DEE6A07218699228B7155E43C3A75E801C2C7E300A3F37AB0F8C6D56378F2683FB83EDCB1F22590FB48162DB5F78A3D7C0C6C7C9AB6BEAB2A1147170C2713EB34F98F22E3E3896FE03CB6E593DA654CBC07527EC4FD8A1D3FC324295FB4C2877DADB6E32740FC56B221A2FFA9BC879CC6FB335DF898C6D8B059F01431389F054F614951F58651304BE033C11D11AC
	68A81ECEE8B02F1BC6E87D81F334BAE8DC1994F756DB854CA658EC245E817C30A9084151A71557117581130333DAEB3508F62B59CCAA1D5134FF7F31C71A86C327E8A5D382E9FCD9D4CF7452883AED8390EBF0FC19D7CB62F1AEA18DACEBC99614EF08CB29A1F59DB218AF55D5900443EBD8DD3F4D63978DE1FCD1ED222E2AD60E1FDCCB627F486389B4572A53BF4463D3A1AE53ADBE4009710B6BE00CD6270E7FF29863ABA02E29CEDD7E05434A7203247818BA54434B35CBE9FD9D99A055CB2AF3A7EF9319356F
	296276CB6D034567561176E1D64C676A6AC9192F71F52C04BAB6B7CA03E7AE449555B35E66D13AFB0EA2DDF03D9CCF780BF0217B4D4FC8D7574066D6ED8D0CAE20C1C9377598AFD70ACF131E42B6E98DED9B217BA727AB5A46622B363179DB9BBCBFA165507CEE9510AEDA91CF78CC1072E3F441D9C8173E0D491B5B486602790DCA3A4EEC242BECE465B6433387A0C8220BEFA4F495B9C8975648467D51D2FEA45F92AEC79A27AB584E72A95D4E661F046E932E17A939C857A445F7EDE765CF5D2E545F40FC244B
	5D81F38800ADBB985D588E259C1B96A0DD788ED69FB1700C87C85F2114E361C2244B5C4166AAC48EC21F3F134941721B4E69D66C14635D73BBA98F69366CE4F39E6D7CB66910CEAF65E70667241D6A7C66AF62F26CE4F3A74C4F6C125F0C95C89733D31657BD3FD3D6A2DD52CEB687D367476A6D5BAF383D6CE47A4DEDE2F4C5CDCA7B4B7B125B0BA23EC0FA4A75F18F27EB1668FA60698418AC51956FA2F41794A1DDEC931B97C6CBF6B85BA53F5AD5C8675C454A8B5E456CBEEC17B23F7C2F38DEF63139A56617
	34CB195F8D1CEE69EE461FF6FEE32BF93B5C4D66286A7C181DFED20CF4D5C0578A605C4D6822F7AB699E63F4690AF2293E77A8694ED943753C0751B56FF125E3F23414A01D38C74EAF88FEC76ED14A7171DA2E17BDECDE4D65F049EFBA274BEBE67995B63B664768CEAB6572C2FCAB0033192DAFC5B7AB697616A1DDBC60D395F439FB15F47357A1DDA9609B774AF4BDAEF453B85D9860C394F4D1AEF47D65C897371749193217514D1368FA76913AEFAA383C7B581A82518BD9A3AB5C27ECC70FF03A28FDEC5DA1
	FE9F4B1764A7374BAB57A3DD72BE263FF9D239F377A979F3EE60FE320579155296291D34A84BED5E486D3E052DF1907E88FDF00B325C3CCA6E1FDB189F407C22DD72CB62F489ADEC2D045A41CEB9BFB6F6FBE995999751BD17DE432041D96298027155664DBB68B80E6D2FDCC957C84958B22D8577D166B8485826D08FFD93C0339E471E77AE41FD14103E88C1EF3DAF47537ED00F1E719B2417471E3971C3B9EC6CE4EFA0E546C3DC329E478B476A14EB39558E073553C407E052182024DEB9B6E53872FA32D9F7
	58E4F7085BE1726044714326FA42AFD9E71650523A7B0DF4EF000C75B2205C226FF0EF6A9FCB568A1E7726B6EF5E25B94AE5BA69ACC71D840FDCAE341FFDFE76DB9EF4527E9D53C540F732CE12362F4FE13C158DA1EF51901F81D0341F60ADC3E4BEC146910C57F75F2F7242AB5BC75B67F3D44DF8BF5A202C4F18F70EFA645D3CDF2A4F7DB26FCDBBDF15F84FF6A26F71901F7BBD723E55C9F8A7E387C6F785275B82B4A227CB25F4342F22F43B0711AE92E8D23FC759430AAAFC4A2EBDFAE76917FF75B9CFEF6D
	AFB1F574178C8C761798BA4492E31789AA1A8F72D95BC89A463D47652D485BFF0FAE165A179EF0C1GEC6EF04AD15296DC66B3C49FF4DEF74DDA653CEE8624918E007F8628BC00692F57CC4F6A691ACF2A507FCE39D448B31C142D35FF9974D4F7F67381D6BF4E83F2BD055DB7A7BB8A00717FAA67BF96E8669DC05DCE7B3CDADDBE759207DC0C2928C921F822F317582FD76D67BCDF3ACC497316E387B5F98EFC27A73B748746EB6B8FB24F1540EF9367796A1F1167F020C97E81F5765EB1B28735AAEAFC1F51B1
	E096E1724FFD6033FD351EE88E69FBCC8EB70976E811924998AFBD4710177C9FE1DE74A37272B70AB7D9CC2249E0B60D980BD631729B8E7D07154F674F0736AB087623EF7ADCC01B1F84F94670FCA78D913D29665052FC759A01349B9124E0BAFF31DA29736805C11ABA6F39A2B4FB6E0FCC574B5BE41D2FC81D13DD190ABACF97D0CEB3500437A1BFC3D9DA7C30B45332B0CDB250677F04E98A3311CF96EE4C7636F640728A4AC6FDB4C1BE13F8DEAFE52B754C5A5879034AB6968374A9G3987E11CF99053FECC
	77197B593202D44ECFB9D8CEA950759C4476107FADF952F59BEE53E387D03F8171D1C2413253B475CB7B6F03CC2FF187E57D1A9F1F139D70B86A775DDCAC37609074310710476F1C045EF51D83F9FDA097759B887431BC4D377389DFE61383CCD5CAD7C2B92F6DB1D83A0DDDC50A5E3F04AE3715B4A9096B89CA325773DA6B5D4AAFBB34DF19DB53F832E33E0071FD51FC64FB9E70DC5A8E73873A061064D42EA174GBE98A02A9D651A3FE09DBC8DCCA076E8B65A8FDB6D7D12CCA506A1D16220DAA189367D591D
	E41C54EF30558C082255F228DA15452A7D0C3F0E85D897F1D036E04072E7AEC03BE57C1EB620643792685A81A6F17A97983D51A932FCCB97E23E21C01340692EDCC868AC06FEA323CB63F4F99D093A2A8E6CEB2664A15E8938299C5F21C3FC9A608ABBB93FBAB29EB2D8ACD6118D1D24351A8BC615FC37F6CAFCF7E2FEA50BB81F00CB6078E7D7F03E3A002FAE44DF3712738538299C5F7D85678BF005C6646BB5A759FB7030BEA77932973FC0DDCB6321AFB12FD648A75808E53C7AA55AD29C600443D84641A2CC
	4F7C412355CAFFD089F4CE0049GD3F91A130B3C71454A3B330817876904EE748F2D75241DF49BC54D41E1512AA62D910AA66D575C3F3C7EB17A1795F561C2655CBE0F7E25321B791576EE593F689FDC1F5373A00EA74D2BD0FFD301A62D9BE51E354A55973F3ECAEB5C41727866AB143D30876607BDA87B1C62D2A63B6B58E0157741814B73112F102FE048AF2E876B757255D816E002F11189715B0A91EF83DC0489FDC7EB2F5226D3G1F578BF321DE14F3CA717F66BB12D7113DAE0755BCE8346B07ED46DAEB
	FF0735466A54F0A2DCDFEF2DC1DE2B008FE7AF6AABA00766E594DD2A1EE74D9D27B22F69BC2F2910CF9A176B0B92320EEDF54B08F51EAC6DD8896A38308F6A2D8FF5D66C2233E040C783A477E15E8B573A7A2F063548C386509419D11E41AF0BD87D3322F7999D8EC337D18F69408628EE4D50D3143408F668A124FACF63F935C3BE21E6ACF3CF693BDC4F0A0C389EEEA8C5D992A0CDDEBF4A7258003A6F2802382EFE5E57177DEF75FF7BE13237EAB3DB45A2BE769BA963FE8F4A194AF9099C5462A52D9F75151A
	CB565E9C06C1A3A8EC0014D56D58ED9CEAB1758F188D3C62A5DF3C8E4B4804749596AC634EDEF599E6088B36E099F7ED4DC3BD72FE6DB11ED7B850A5733CBE9F524AAB5F0AF93D3210486E902DB6D976F34AB12FA220EB37221E5FA8D70F47987E4B8AD7FFFE380277B110B60117B53A1C4CC135E7EDC59EE6F9A54A6D28D54CF70A1568BBA3932F876FF03AC75F19E7E3BE3354A67B4E460CBB963681B0DE7356A32FE3C093E5C3D92D6B09CC86FBF79923CBDC0FB61D8EB4AB0EA05DCF9BFC77AB17EC40B25AA0
	DD88CF1B37111C9FBDEC6D9CF014507F1B2C8ED1ED308DA4826D15255B3C51F5CCB13C114F49A16FDCBB667F62C657B6DED6496764C0A3F23A18CA75D802513F4169A301B6550EE7F4267033204B9D12FE9D727931BA785D2A8893E0E7A4770AA4BF33031D114C61789FA9FECC01E765BF6E407203A0AE4301677A4AF7107EDEAFA66AECG01GE1G31GA9A22EB15DDCCF4EABF8BDB7395D8295E6B118DBDCB60A717CE283A9374FB8AC05C79B14F38426071B87099EB2C55F676B90D83A3F51F4798363CF4764
	8E5DC95AF29D24EB87F0828483CC9DC019218375A5B31E7F1404E57CEC9C72CF0E7A21094A415A6F2A9F14E74FF35F5D111D63217DA6C8F9278C486D1769E247AE220B4241C45D96GBD0099A0E890F531E31B4F7A2FF7EA56BF13AD4F691FCEBEF0CED4A7AC5DB5FE16F7231F6569077DAB2FE3583F729E9C76F4FEBCF2D0BBAF16EE155B3973D895FD32AAAC5934DB3ACFD4E1E148B59D6B23EE22635D892C39520E094DC3F6952B4FF494C099DBG1A8158BA6393EBF11D31E78857609753B9E1980423819206
	504F056DA6F35AB48867BA93F54B81AA815AG1CG131DE86B215B3E76E96B248351DB596827D6FD52C552599BFE6A7A2D1FFD69BA46691A0E49776FE6A2DF9244E5G648DC33F8A5088508590B80C72BD356D1BF14AD78B38C62B354FDDC26475DAB7DE47A763F41FB2C68CEB25339C732F3CB137F4A56AFCB44BAB92FC3DA322DD5E861F69E68FEB556169FBC99DE6C05C629128BF00AA00AE00G0070912C43AD0DB53E6A309A8FF4011D62CF0D0AE47C3E68466F7874AAE4793455914D7AF7CBB73E726E76D9
	DE12E6F9E7E57BD70FE1BE53250FE8793D5ED18F7B07F4A3097937CDD625FF4B9DE5EBF825702C9BC57F16B8087E2DED947D5BE7F4AFA8844211GB3C6F19C5919631F0CC7FC279B55325545AD44D667C25C52B1186382548174GCC820898C3DB8D76ED2B7CF51871586A3FF37D13F3254FF431E3DA696638259B1F2DBE6316CEED2B33B54B8B1F6FDFF9FFF2CBD7228AE70CE9596ABBAD9FEA5BAADB7B95F0AFEE7AC2F5FFCC7662720EC2DFFA946D75AD3A6FC77829B84A5F3FDC60DFFD8D7ACCE7BE2AA54FCA
	FA26C62B6D41542D41E4B4F7B1F956B935469713A04FD0AE4F0C01BCA95F29DC1E8D8B7D13274EE73A3423DA691E73333CCC1F69041F3552C56579D75EADBE5395E81657237353BEFC26AB7DD94BBE4A96FF2CED9F75DDA651EAA7A3D081773193C7511E9BFF467A2FD8649F3FB5BE5385E86A6725957E15173552D73AD04D722679CC27DD5E9DBE53CD55AC2F7F8B7F4A9B7519AECE333C0DDF7AD7DE3D4FF4A91A653D6116EE1C7300A2DF7D47BCCD7B6EEBA0F3F56FF3AE5E61CAE7DC580B634CAFEE302B7D62
	52E3093A8A0046E36897BF2C41F1C97BB19C178450337121901E8A90F78C5BCB021B9C63137FE137F4A5EAFD9F539A171C22A77EFF9E61B5A0D1D78C5088E0868884088C40F1C9EAA3B937B15EF1096F3910FD15FFF69560B3DDFC00D63A5A2F7C536BDE37F4EA3D26EA167704DB3A714977124FF4191A65DD3D5ABF79263925D34B17FF02D63ADD457E157723DBBAF5F945A7E83553EEB331C30DFE88B74C2476985327EE0FF510DF8F405889589E4F68457698F282363BF75778A74F27EE69547244EB6A6FB6BF
	4B1B65333CD44D72FED3621FBD867BCC1729D9DF3D239E6A0B0CAB6928015557C9C754733A7C5FA46AAA811AFE037555496B2B6BB7D8DFCDEB7D535F8FEE69547A1B7C9B4DF1251F65E53A25D377CBD11A6545147AD7DFF77ACC972FAA0F5543CDEE7DD8AA50651D88731593F9BF46675795A7E2BD841479476769BE5319CFF4CD47F8F8FC90471813B88F956B7C9CE37ACC97F322162E8413DAD764CCC0925026F1DDE575A11F42A464B34DCFBEDF71192EF212162E8E8C61FC2C1973B0395CBF9E2E7219AEF012
	D6BAF305FF650D78CC9726AA0F0DE7966DA76319E808CB82C88748BB8946B5G0DG6613F0BC33F1BBF9F7F85C6BACDE572E5FDE6F1F0C4BFD268BBAC9AB5DEC37F46373C5CF3A25D37B22F04D722E58601FFC577BCC9723D95E310D7E15F71A4FF4C91A65ED2C74F30E63B3DD3AE6F9B91B7CAB6FDD1F69F2CFF6CD476E27390D3EFB319C62AA8198EE36C8F0CD90EE67386B29EF1D8461499CF7BF75A131901EFDB27A106FC532371F4E43869ADE4A67B0CFB9DAFD7069D1DFAA7E35001511AD7ADB46F86E0111
	768DF6F8FEEF3CF60776FB635BF7E83FB73EE7473B8C8FF9A8EFCB42786FF7F8FF2F7C508E2D774ABF26725CCF4FAA5B87AC22295FC80BE5E3B023135C3BG1EEC5F000539B9D232C33D9F4F4B885B69D95EABF6EA4BFB4DCEEDF92F5B69DD5E9BF7FA12D75AE37F1E3E2B71DB74577B1A56896E7E1AADC221C0FA23413E6770E13EA84E4E2AC969F7F9166D40AEED598E6D52164D384B3BEC7D3BBC49465AD486DD77AD7CADFB5F1955614C8F5E21F9BA9CC6F662061E10E4F17F9049B9F637BB82F6C36FA470F7
	4A0F6AA41AFA513A0B680A0DD50A4B15E3158AA8339DE092C068EFF12CF2D2B10ED5267E9657A7DE21E77B52A0AC84A66A8A8231EDBF6DB61ED5C2E920FB7BE567EC8268BEF3DDA0BBE71345713F2378E6851E1D3FF90F62C9388D62E283518E2E5A6D399E2F5D2DDD0FFF5ECD4E1EBA2C8376CE233A2EEE5ADD4063647EFD3BE86CA76AE5B4F7507422415EEDF42B6BFBE8FA9667A97D833BBD5903F4FF414F0A3A2BDD47737E2C59332CC54D5A3216B4FB16F5FD33EFD93FED76ACEBFD33EFD9F7B7FB17B54DC5
	568A0E3F7C18923F11638F8908EF962D4D32EEC277FA564D45FB35F5F379DE4F3A095C6BDBB7576F752C1B183D3EF5F35BDE725EDB07294BE417EEE9E0B22E4AD2365D8DDC76D7DC7032CEE6E6AB7164BC1334C6D26FE12D3E25139CF4F4D92BBFE245B63F62F758668F59C81BEB0330889074BBEC738F8CFAEB73613F7354664F26FB2F513FD337790BA8FE466F54ED7ECBA7367984082BB8854F881E1F73965A40E13B353F39DC527987AD1EED606396ED9B78EF0BE79B782A45378D1437F836014DAD3EED2036
	C55B86F667AA6D3C1C57F57EFCE53B28647807DC701BF83D3E3A905F338DBD856B75B7962257848827818827E23DAE55BC97D5F0AA2B4FE2F8D61D6ADA372C6C0B97A86DCFAE3B5709B625BF954BCE21F7D086C1B89CA00617BDF3401BCDA581DD1AE6597D8BDDED21C2422F4CDB2460292C94691F49F325DF0FB268DC684BF0DC794028122FCC958FCB0392F5AB0234789ADC61DA4E86897F55CA17F256A1FD46CAD77A0D92BE7C8B259D487E517225CBBE6BF95FD1242C8BF26F956B774F92DD5ECB89E2F7D336
	43D38C42FE5FE2477A8A8A427AAA22778D4D00F0B2405CA02C2FCFEA3C55576253BC790039F41F2370B4358FF803620BCFD37BGF6E79089D7C25C645378BB832BDC74D5DBA63539903DE79FF001DE5B87DC22776C83A2743EFD4035FA4FBEA0DA6F5B875C2C775EC7DE39CAD92732AF98794AD5876BA47C7255CAFA59E69EDB6DCADFAE61A795ABFD07ECB38D8E2DBDFE61F430175351E66672F52D625351E6AEB111BA6B027098C04869E8B3A13D5EECA672F4F71B115E5BDC33C849DF9962B3DD713CADCDABD1
	4A3F1E4B13B2243526998B65A6F1F97EFA8457B45339BCC3D41E95E7A46A36G684F60E7B33C4AE3BE4313BCA72EF52D876E33CADD5A748624FF49953F916539E4D0EBEFA3G4A8DB98365F9450EEBFE11E720BC2B691D4429904EA4F4413CCDEB766B45411E67600C3FBF173970D7093EB3CDB33FAAA9BFBDBCFB0235FCE75FBA177CB6E1FFD2E9C2D94602D116E53DC416A888478324F2D996EB161DA9153774CC76DCF126DCBEAB7B53F2E5596BCAD11603C3DA79D5CA691B4E74D45733DC736336FBD9050B8D
	6CC27F153F5F337F7AE83F367F7AEC3FE77FD5345F377FAA5B6F59FFD56E776D3F2A77FB77DF756BDD7BB63CBF6D1D8DAE3A616D77E197FCD9B962EF58681A8F7ABBCB258BFD85FFB7E513ABFD2504CF26F79E58684B07F6E3D70BE9040C03C34FA47674B2DD9BEB075F66B3312FF9DEA4F769198D435681D1909A4FA2F4DD8358761201EEAE40F240171E457AD5B67E3EF60814FF58EE6817AE4290D67C1E240D676DB698684381E6G2482647E9E4BFC16260504267E01FE4AA1636FC6BA26B2831D1353660A04
	F6C70B4DEC22DBE9E8CB3B3FDF222C97E9FD0071965CC36CFA5050CB5E8414F02B73BC4C9774060E8EE317B4DFE03E6732CA2DF38E6140D3746F5177F41523AFCD78BD3657907A3EE5DEC8222E9020B2845B6BA9EE7B404DA1F21B6A8AF1EDDFCE8833F1C3AFFD3FAFA8040D9B68BB0394BFC071E18ABC9BCFE452F58C920E02380233F1DDB32B9457BA474EC61ECE265BB56C77990A5F858B7DDDEB9C8F30B24AFE05B2862D2873F810B965ECD79D4FBD5B53986EG3D4FA66F9C753BD31D94DFF00E7A5DA9A31D
	C752F778A0AE689C5479F3FCFDB9639C547985CED94F610A5FEB1C47DF9F8D3FC2997239427CF393F5C5GDB4E15755CFC2EA71D3FC267ECDD672A6DBC4BCA704EF355F69EE8C1BB8F02380CF3D167DF5235C6FDE8222EAB94F1C143D88FB60EFB1A0E9BE2A11C940AF5E3F52A5BE8DA284CEFE628EAEEF49E36D17DA89DBF1D47F854F15E8D94DF2540B35E1F24F3C492EE033858731027148164F34579083B5BC1F0A20483B96EB9FA6EB08442919C57FF945346F35C1FC5CC3B7C8274599714F8B87F4A3DAB73
	EDF70AB82FAE3AGFD5B31A3046FF688BB8102AFC03DB96C2837088B180EE2AE10F515F00127BA1FCC67DE2997B07DAC6478880A4FD46025F7F8A91E0473A7838F13D14E75B5A8FB72E46469359A39ED65544A3F03EA0EFF9B387C3816416A67E7874ECB72AE447AF91A0EFB2BA05C8AE03B90F5714FBE25DD8782FE7205F21DCC3D50D37D3CCF471EB3AED43749C20A1FFD213ACDDEE142B6198EF14D97E17DDC5D0B7593F591761D85CE9C6B66DE0C7C1FC55FCD5D82E1BD0079E2647F16A35E66A113AE76447F
	B3141F500B55FE7CE50A0F38D86D470F1A500FC7C35C72B0647F9867BFA08C79FFF0905BC7F8987FCE0E1D24CF0670DC004517A07F2F5B557C2F38C466397292CF7CDFCC6B336992357E2F227876CB547AAF9BC07D0B90B77B925EBEA82FDBAECD54B5DD0A38B7A92E9542BD9C57B504F20A9CB70962C2A19CF9A94A730D49EDBF7ED2D90624CBBD4973DBEAB7E9172A65B90B62B3AFD54B73E1AF4A3374B26069B264693F14775990CE6338C05E574E6538880AAB88CF54D504A3EE004AD3C542612EFB3EED9036
	81CC8AA75F0DAABBA888B84E3EDBAC042745B8D4485F972FB3759BAD64E3AAC515252545059BCBCB2577654306146F5BD6AE7A0D66BD52846242191E1225E7667B67CB5FFA8A9A40B5217C4B214EAEC75FDD566E623B89A31BB7EF331B3AEA8C5DCDF474AE5D2FC67B9112FE5265E81B59F46C928361A400744B716EC16797F8D7DD649FAE959A7B346F9ECC519F4D4E7D836333C8FA127703DBADF332D919AF8EE219DD90BF798F54CF385C07C0F4F9A8171DBFE3717BDC62499C0B5C13AF47CFF209A77C6EDD28
	0C3F41A5FE3EA2BD136D2E15A8DB66DDD385737513BCFEF7ABD512A96F8A768CCEBDAFAB7623519CE68BFB5BE5DB08F5F831050DE88BCCA78FD5220F2E3A022F35503BA31C908E860838825B14496BF92358ABBC3529BCFA0FCB52956ABEB41F62532FD07721672F46BEB4B7825AFE843601CB681EE8BC04D3B86E2392ECD339D7A26EE70AAB027016AB11F79BFD4F369142BD9CB7CAF1644ED8725E1AF406E16DF87BC5128F7EEE3C52DDF64FFD6FC763AEC3793D07711C1120FD6515346FAA2D73C2036B3BB992
	0CC11AE0007725F07815729AB05369E9F4EFEA9E6017DF05FAFE1262CAA15C4AF1D5B62C8F73D52853C303645CC08C8F1BE8185CAD4D42FD36716A0364B19EBAF71DCF9AF79963553392581A42FC3E361982FC4E85483F9A7CBA007EEA544F32BCBA5E05F0B047AD3190DCB80423B9EE9B55E3B204B3B86E76A3285B7CC854DDA6CD5B8561A00EDBC86B055C01C76E5B164EF6DA7C996F11FC71F7D024E75DCBE7527CAAC3296F79BE6816895F8E639DA64C6F51AB2BA41B3FEC98EDBEDC03D7253D5371935090F8
	DF0A0B0F14ED1E151565A0F299AD64C0CE173476D36040BEE4F6A4564B49F41C188EE1619A444D9F6173A40E6B2138C68837DD03757243A839AB3F874281G21G11BC2E1B46CD126EAD0F85FC8A0FCB9FD16B1A245752DB0F87FC28040F5408E7E54C9FD5176129FEBC35190552D32791476A6C5581EFFE8A6F8141BA9326006DCFE1F5C6F8A91A222E33FB68BAE13F415EED32006E280DBC55A1707879E3A43E43EA278B2CEC9DF1B97F0EE6856457846084881A426ED5E6FCCEBA6A0BCFA504C35A68A932BF0D
	D77049F8DA5CA5F0BD7FCDA47D6AEC20C91F62DE97429FD567087F08F37A1768DC37680F6A316A3FA8FE4B9F55E355130E60D8359962227E08F6F98ADDE7A8188A3EEAAA621653BB259AA15C46F197D471B946D43443CB69FCB7924289G299CBF056249FD7173G377CDAFEAE3C5A1B0FA0F93CAD65274427CE1DC87F3A4BEB99E843EA1BDDB80E7C89B4DFCBF451D4D36805E64B350CDF464F098E7538A974DAD91E48EBDDE50B3D966B77A09D8FA7DD2B2EDF934527DD2B2E5F8CBE374A043876BFE13DADB513
	FA1B8161599C3703769B1990DEFA9D62E6DA31FEAB2E437A7A084DABA19C4EF1DFD09C39A31F5C9D46F0571814BAA73478FB52F5936F33356B75F617B27C999BF983564EEF6945F69E2D4137FA5C948FB4A957497EE86EF5F2BBE7BCFF6C36DEA7C4497966C7F99AB3AF22F5D9944572DB42719F327936824F6ABE142ECB50BD9508CB0E42BAFD100F999A2F473AFA41C47A50D09EFE1D0649B782A4FBB57D52EBC324AC573A1C7C0B17213481B69EBB27977DFA8C141FG1081307406C4DD698DA86B43AEFB144D
	006F3AC16655893F03EEF02DA3D68F23F49DA56C86F55BBC196223EED0374DA5F6EC1BF190D77CE72C1F7768F8A29842619CF72608F59677E76473F2BADFAE08865E2391B71562DA256FB2C8676687A622D7121F2738D20DB8D6C673044A7056B63546932C1FEE9AF49D672DA55BDED25C87C32EF15D07C9DCEBB42D971773855AF9DFB064BE2E7B53900E6B4222319E74A67411D19CF78FDFE71B4D6B61B40AAB1A16282B1A0638D00AEB143EF141F01F76EA690D249B2FAE2B3C5032B2AAB54B1808FF74BD865F
	2C2A1BD33AE57DDFBC68DB7F5387691E28747ECEFF3F41C286C3AC2FDBFB64FA1EBE60DACEDF2F9C772CC69D66BA318EDB27615A515FFED42EF5ADBD5F1C0D7ED94D1BD9522DF31A3CF6444E3D7FC44F57C482BEEE9A1EF99EBCC83E0B385F506BFE6A9D104DF62B4DE8970799165E200D7778F8BDE36739DE437CCC77600453B1BFB64846985354E7440ED17CDC85DE3AB7F3905783053F008F7D8B5ED9F63A1958C39C04D381B28172EF047185C0730D284B6987499DEBDE5F7B52DBEDCD46C123D9F1341F4977
	2A57F330DA406C626605386E9675172B0468136BBCFE7F226BC6E68F1324E7516367E511A7136FB38B11AF8A6292G6682AC0EC9549582D44620FC8B8F95B37908B9D40B22213307FE49043F74E1B28E49E8F2C80149166276AED3D30C5C7E5B6137982355B7BDC3576F82E35C7694A8BEB446EDCF015F959B81F10B7F0A3EAE201C6FC97E95FD585BF44DAE9642C9GE99C7F9F0AA777691073CF640EF149EED723BFA2345E6AA5D60ACF72C167EE6353F8993E467068873D79B8ED1FF5D12D7B7913BB797DFE39
	B7C967CF60D9FA933BD7087943F5A5C853F8132773C9E75375CA515021B709E6A373F5FFA1C70B2478868379E61E7B77B9F87A1F085C6C4BBE15745B2E64FBBAF9827EE5EC554231F26610CF34CBED62514558A616DFFD19D077C0DD76FFBD3409B6C916481BE4DF196AA84C114E4BDA499D380360GCD1D86B3BB68DEE330B7DB4D264EE149D7CF27F38D724D58541B78FD42D65226FA2CF6530855A23A274473C19F5235B1F277450A69B8BF7F5E0663762E693E476DCE20891AAE4F4F43262B5791AABFA2F174
	A3BA448FC9370C6825F733585A79EABEE6081C0E7B89875C76934E4CD26E871C6A361FC0524B71FFF55BCFF0CD7F3C5BFE8226174EB667F9BED7D5CB579ED301570C69287395967775102FADE8334BE341D78334830831EC1D1C25BBE8F3EF8F83B6CC97944B5AC3B8BC2325F42C1E669C41FACA0855EE8F4AFACA851AF931F2BDAD3ED9DDCF9F9D7C0FC6BDA9DF2E104E3FEDA03A5C055F0390DC3F73C374D8GF93B37939C17ECA34771243C5652F7798E9B4C8E79DD2F8B1D6E7A086277F3B55F4C7451834F31
	1B19FFE47EE1798852045C6C49BF9C9BF1778F3D8E191FDB68FD4F0A5B1F246F1EFE5EC74F2471DB43704C5F21CA6AAF1A8C9DC6B31E5F3BEA250CAB173E9D710A28343588096F781B71AE71B51A71A91AFA5472C1285BFB49156FD2DFD4B706FD51DC482778965CD7BCE79FBF97C76F842217GB519AC469AEA3AB5C603DC27C1E3FC1F6F965C537A8B4DF7B20423G62EF99AF0F68A7678E1010346FC167E7291007F0AB7615E5F4AE168F618A0EBB4A4F4AB4F25CFF68FCBC9442D3EF65E79FA8EE8604E7F3DC
	AEDD13A35F2DA06F86B25C29C77E57B9B3491F146339AF8D7F1F4B18AFF81EEFB0DB3B446E59C795D25D26830F73EEC5DFD33E553DED6D580A6DE6450CC45D9600AEG678C762D0F2899F4CED7D5A851844FF017B9DC4231B2EE7B1678981B662DD2C8F3C2015C4FE2DB4A7BC0EEB7E552394C72DA7539CC42D3A66169B6ECBB398D6A36C3E80AEEC319CDB5C466DAA339139C7B67A12332FFDE7EE9D5B6769FEA59365C46E46B39CD6E1F5F6676FB3E8377D1A75F06F6194FEDB516630686F09FB575B63441AE3A
	071E81615C5B7919B40A23F71374A26E953EC610F11B2CFB1266FF31ADD7DBFDE7904B986F9E52C400554317E6777E71DBB356EF416D2CFFAC05E75D6DCCFE164EE0F137D9A76FD75BEEE77D01884FC0A99D6B9FD32C58BF06E968CABD0E09821A385B657EB179F6F57F38370F0C9DDB5C3EE50271CF53BDF049264B187FFC29977B44345BA76A4B5B0684EEE795F4BEDEF88768688E34296B78B94756BB78BBEF749CF784046381B2G9647C19A0036B83EF6C2E9A6C1B89CE086C0AAC06ECC769D93C6F311582E
	529749036ABBCE56A5A987FFE75C615B9E63A5FEDCEDEF1ADBD9EFC94FA5AA9BD2269B0F6FD46E15B11B4860E32695B335EDE221A07B5DCA20E91AA95BC47BCC35CDDC7D83991FCB5F5DB0328F5DB9789DD04C86E2388D18E76258F6EE2FFAEC7B00209C1BFE5167FDEC5B5A279E5B6ADC62CF6973B436E5367372A06E13074FC4FB7AEA98774963B8AE1C6F3D26F35CAB5467ED39B3D1273F936D65845A4776C0F80C6302A80EFC43A51F5FEB354C5EAA51D1FFFE67AF6343DC6D291816614F7CF47C3EEC15435D
	A7D571774B02C12E8800840054BB59F7EC187DC508E8FF199A3223DD217DAD3DABD13762AE597EAA6FD25BDFD2B7F9D72459EC95F1B04E7059A6B216EF5178D68E5231FEB06EA76519294255FBB44FCC4D7BE9A43B6EAE299F3CCB6E879378BCF56610F29C4F74F1145FA3B579AE260FA8F8465D45763A18BE1E60FBC4A9F779BEE7B097E87262E5FDAC0FD76B43C95F4D21BEDA92D4DAE76DA1FAEA912DB6D7BDFD64D4724D5621F653F37AEC3D690F3D641BD31AEB13559D628E4320E1CFC72F31D3549B3AF91A
	0EBE42BBFFCF05ADD9CAF1A65AAE79F7FE58172768871E186D0E8D8A5C0ECFD27CFEFB70FF9DCB7AEEB7DF9E57B2187C671ABD3F8FF611D97BFD30F03376BDA5D7193D5FA973A733561DB2ACEE1A19543BF548C25F8DE3757E63B0291BA23029BA009E00B10060F8DCFB5C56CF3EFB4BEE53D72FBB56F7F51377519C2241A272776DAD1EE5AD33FCA04BCAD2F59A713E9A87FF5F5EC2EC36438A765ACF72C7FC1505549718B2C5CAF7D7D8080E6C266E9E1111CA781DF4EF5AECBCCC514CFF3FDA096791A2635127
	9F590CFEBE01631E2538CC88675F4D77BB5711FD04A29EFE1D0649FB3452FE67BA358D915ADF56CEA73B1571CB6CD9A3B03FF4EEB97A25AA8D7E5F147E50AF69012667EE59AF0D5D2D76CB377DC71E6F60BA41F52BE49C3E3B1C77953119EA7E3DA8B6EBE27379970BF1DFA090720F3D9B4F26EFDE03F374F9F7639C3D07FE772E749E18AF83345F03342DA5B896F15E037B8A53C6082CD3A19C8F1081107737C45DF2008A00C600B6G9BC0A0C098C094C09CC0B2C0BA0010G79A7E0BB797C88B9176BE9BFC7BA
	40CC9B0B5DE0A24DC5127DFEABF9D7DB79D5D4454D9EA57B6B77693757D6B79DEA295D3727292926FA5F216A266F2B8F3470742536F2AF699B776C5B7E431E5DFA2D9C18EE82ED281B2204092EE91CBDA6F07BAC55FC37569B30BC220F8ADC16EC8BA94B433963F56BCB0B8B4BCBD7C9F651E941F51EF660BBB48157F98EBAC99E2AF59E3A6E5525D869E132DFEDC1599392783B0D74DD2C3CFB93F505G15776237A4AF34219DB683AE70DE7E7D9D9B5AE16C3D7CFDE6FACEEA9E0417A6421CBC91E95F666FF1D7
	A5CED4578F7DAC6B5A755BC7CE48ABA8513B3FE1B2A4733BD84200FEB667EB45B04A30F4964A300FFEC73D91426DG4ED9C8DBB302B2844FC21D3FF4584337BE682A86CB37E6G5FA50963F9F551359AB276921250176E9873E5C7DDC9DA7768357ACC273E971125BB53E7BAB5303D30927AAD7400A4757E66C60A8FCAD26FEF460E216EC3A1AEB3096FD9084477F51986CDE01BCD56CE7641BC361675815599D93FEF3F8F699B8708FD4010C02C53CC53C15390DA5BFDB85E8C61E7A4836E733DA69082B4917749
	FEBD66BE35DFAFBBC2461BD63B897AF6E5595B0F209D45F1FE27544A7E9E6FC838F5070CDBAF7901FF9D93F87E6FEEAD94BC7A0175647D03F525252B18DE6DDBD12F19D0E6457DE813CF16385CD54C5EA3E19E003DC6A279003FECC51E4D77236D2751BC43A19C8310F4BF5A6B5F2B5076536F675F112FA6CF9817C8F30B5E254AB53664617D9E5758D6BC406A22798157B5B6D6D6DFAD169578G7235371E6E1DC2F8B6C046834857578D041654D75EEC6C5F3E4AA2BAB4C3B890A092E08640EC008C005C87212F
	FB907B2E4A41A241F35F4514A6F7DDEC1EF3AB1F77D5BE4856221A615975A05B3FE5FB932B797B75E38F3A5B9D2B7744EC395E8B0E48F601F7675C7C1DE2FD29D47A9E2899FDFFB0649B77A06AE10A99F5A67C9DF5F6EEBF212D04F0B3C04F5F511F07D950EE82G977DF74CA34D027610417378007689C58F411C962075A124FD17771D36073477FEBC7B731769F1E4660366395CC19281F945BC645D1FB35D6C5458B36A60FBC6898F31FAC903E766C3ECBD027903BB79B9777CE45FFBE8C5C033A5D976874D49
	EAFFB065EBD2666E017EFA72BDCB6A919C344B257E1F517CE33EBB8D1DA4AAE87A0EAC21F5ED37F65B0D8EC70D41DE464B18D3C7EC154DBBCD034606818BBD7EA397A351656F5424130B12685E2EA16DC4350665F2279ADBE77FEEC03D4ECE7458160C6B70978EEA455BF87C538EE57C24E4299EF8FC1DC3AB7D8C9E7F3CCDABFEF6B25A60E7FCCC111EBC51B1457685328D5E6CE203AB664058E70EF79BE4767689BFAF57B4072DE5F74153B9075D4B43E8DA06799A519CE61391700C11E818CD5EB802B61924D1
	2E5AA65300A6F30EEC13CB9FD65B6492D17B6E0D15F44C319C680B9EC69FBE53A47B9D3C636B5C3DB2EE13540777B850E768A1ED50435857142FF379FE946BEB9A4C56A12927C11FBE153F7BB163E174AD8F53BCB3A11C7FC8222E78917E6E2D0875DA7748C457504D04F5E9FE5F10C7F4BF86F9048244GA481248188A9687F43063C79FFA6046C7F25EF2E1B787B1B10CFD38A727D2C093F171902B2FEC45FF10D01F092C0BA277D208FE54CFDF422367B886DEE997F1D431E579CFA07FD2FB9B86878575855ED
	A4BB9C8C679CA60D43FA7830434857B60E8EAF65386D0EE62B43C4DC8B337142CDE863DB9EE5B62E07E74F23EC7F156DA786EEAE14E8829EF5377110C7157B0959FAED7B7D60EB42EB8450C6BD0A761B19C3776F659BDE24FD1838F254EF4A0475FB96E90E58BF395D6B628BD83A85EE5F3598DF3A2D93CE47465E49F47EDF7018FACC7E84452FF8CCBDA6372CC19BAE05381047F08E72134B77B16681FE71635EFDA1330F3BC7BC5BE15208EFBB1CB3223D767578087B5AD75A08765A57F3A34A35AF06FBE90458
	C0375D54554A6966F15C810EFB0366C9F05F1B3A449E0E4F99DDA4619B0D24C0446BC689DFC39457AA615ED6608EC838E52324AD0148BDD6BB26AD98A5EBE807CDE6B38BAF27B4A68BB4AD2E0F4FC6890FA61BC14177979CF7005B4BE9B6E5FD95C2DDD57828AF69BDD20BFFF34DA11F6934671AD3BD26EB5460376DF1FC0FA70F2EBD589ED75B753B94BF76385A2E4F3772B5B308CBFF9C6D7AB21BFF6D71DABF53BDBD61F4529D8AF47D24B9D5ADEFB04537252A657D70884A5B83F17129723E1B327C52A7E0EE
	7204F73BD05E655D7A84ABC75ABB24B85B93723B666F8A0F393D1BB3BE3DE47819EE1547EFDEFB57676DF4ADA7688935BE7FC67121CF287559BA0C7A8C07383CA7D11F5BC614651B819F7064F85A59BA4DB364A1D25A886999834F04A7156F3D19B43F87102AAAF31E94DE7C8FF7DE8AA5DC0574ECD45030FB58E31711B90749E2B6D90C648BE75B0C96DEFE85DD1BE43F3FD17C3EFD6C786FCBBDF2DC4BE06306B58364392E34D48A8F8F10BAA7F3AEB6FE380DFF7B336D9F939DBFDCDFAF70F25A0F107DC457B5
	EA3C09C931445D5C34DD4F65F7EE752F0D1C6CE73A6993CE475AD0DB95E9ABA17FD037ADA345077DC35D361E636BD5119037B48D5B56A3D54A72C5408726F9EFDBECFCFFFEC3A1BCA783EDB4C0B240DC003C2792F505G95GCDCF61B83FD670764E892F9245AE85634DD62B56CD4FD3B2EFE3CF397349F4F09BDD2F8AFECA2D1BFBA8FE72D3EA5DFCDF0F3A09003845CF23EE2A9A54ED4186F1131EE66506CA4F4827D56F41C2B889A0CD014F045FCB1FC9542DF8C646C97768E7F99E3BED4972BDF62B4F529E3B
	6D4CF29F3BB5E7E90F5D76E7390F5DFE147235730FC3B03CA10B78AD3676334BE0B1F453BDDD2647E778BC35926434BD03732DF29167E6914F605C4CC05F7BCC0170BC002569C83B17377D22F42C8733593F7152084FF45A63256BBC26EBCFF737B9B19D47CBEF51353D40F4355D65D3FCC83A5A6EAE6C677BA290177BAC4A7B871F63BCEDDF9265E73AE7A71C0E49F58A9D6F75BC2B16777794AFBE2B16F7B99F9F86C0DCAA1777831771DA63F3093A366746B3DEFA274FFD3C145F279CAF31F2CF617BCFB648B7
	62B97E0DF77E5EDD4AF3E817829D0394BC1F28AB85E8FC9EE9DF67676D5B1FF76D134A695FACAFFDD2CDA389C9E73C79FED4A84FBBE9106F87BE0FEBBBCB56925B54585BD22CD2E21E1F093877121701F90E52B59FB2172F6238AF0E707D688C7ECE3C4BD8F5AA60E7E40CC77733FBD177BA0E1B530B3AFF006B3EE188574CE7C33ECBDFC0DECE675F13EBFC015F5FC46BA9G4221GD3DF40B573D1BEAE0C831C70A266F1AB5FBF2CFC9173F801525AA09CC8F01CB61D6F9FC63FB851B105F1BB89315FF76F9084
	FE86750817FD638A32DFD439E9155446ACEEF7A90C2FED0E79196E7A8927E3EDF79F754B99AF2A5B740F941F79223ACD27F21F1F77D2224E7C92FFBFDDD4161FG78541746331631A74FF3FF38BF4FF7FFF8A8CF3BBFB4663977073DF95A7D21ADCF599FB2F92EF63B17EFFC75B04D4FF4AFCEB89D2B27B3681A673C17547597CA7139F3557577D9AF565FD2089367727BB3DC6E351C8D78743963596BEF5C445F27FDB9D1D779B2363F60CD58D6EDAFE3DB3D0952C6C0B896E0B6271D3A995BEA464B38561836F9
	812FD7EC0B99BBC9B1AC7E07F505026F3DDBA65B5DAE7741953D12285B724AF866130F11577E245F4FA8FE0BEB0E7F3C6C3463DA86735FE2897A74C73390B7DC02BE7DE20E8B3739773BD75A147DAE435DC177B41BC13736D7787DBC6C2E9688073E425F4D35493A7C3762F71875786BF55A2FD046E9C7E4194ED17C7E74FFFEE749F7591547358C69AC3B9D6D6127EC442D3523BDFC1F0DE72C8DDF1076AE1D4725CB3D8C3F5C39C44663D22F44FF7C302C2F7B943F8F8E9FFF5D8D9D57B2181E8E0E206E7E3B90
	F11DA36EEDEEBB9DB706C31B0962EDE90A7C6E934709F45C3774D5983F3E0A38BBA82E8E42BD9CF72A08790DF1DC3A68CE77BEADA39042919C57C3F1B190CE66389BB9AF99AABA64056974CE17B5392AF909BA7D3C71785E0EAA396E7B943F67EEBD7EF630246A785B41ABD5E8876F73BAFF3D8A6D6056AC7E5D39864F632348865F63236B9B3447C7B7B6380F0FE29B3447C737B7382F975CC573359B3B213FD52E975C5B2035DE40647BEDAD3E4F57B38FEDE6C1BD5AD150BC34193B6A657ACED27C7E297ED778
	4E41F1AD03495CC175998D72A6F0BDC4D1DCAA0467F25C075B685C641F093A02FF7277F269B932E29266B8AB4511FB713AB82E3502601C908E66382BA8AE9C42D19C77279654FF9C47BD5302E75367F25CB774DCFB5EEB093A225778F9F5BF67AAEF7A192ED274EF0CFC9F3B8B7DB5759879E10AEFFCCDBDC66E9679798B080B66726EF771E54BDF87BF777AF8664EF75273E2F5C05B75BA6A7353FEDABF908EFA9D73FF57A25B7AFF943FAF6BBF7EF69F73AB1491EC15E5BACF717B73DFE1CC7675F1AD0355699A
	0DF172BA17F1322487BF7765AE72B35D8393CE475A0748766DDED7371B23949F7E3A3A5DAC606BB6D3A1AEBF936DDAF0594FF382BEA8F3BCE300B3B57773422434D152B38E1E49194A7D3C87B56FDA4A10E8F25F70F4DF5C97DE7DCB0147F481AE7E2CD8222B121EFA6959834F31B714FC2E567C96C330C4932ED11E640B6D6EF6D6E3F73733A61F6FCAE82F237F60B31D76BA7A6FBD260B5610A57E8DDCC7FF062E83263C21363339941F7E065A4E0E72755DF9905776AF7E1E6C04772DD93AB37CCC976767BE39
	11FE33A676DFEAF9AD949F7FAF353C2F72F6B59B62363C497BDF17F695867828B747532EDA69BDA66C37C1717BE947717743B91AFB6D3F94C8E7A8C4EC9F6766A26EB5915B47CC4E4B593D326C97A9FEFF29F9B761AF2B072A63DA065477E86C17EC6CF377933BB779E77FEDFE26BBFB4269D87BD83719340338B7556D66EB0ACFFAD35DEEE670354994082B93E6C96D667A4DAE6760819F8360DB1F2965284F2D739177D4B96AF3D6962ECDBEECA1F7D9086448780E8107E8BAEC12DE1D114ED64E135F8703F225
	3BE32C73B10D006B1D4BF6911B144EB6286E185C301B4431B34D377177EEF2335C65D03FF7132F3A1B3CG52381EE9BE55C176C5647B4D2A4D26EE8BF9A3D7627FF2876EE92D48C27E9FF6FC601206DE53A01DDCC5397F4953B5C3BAA64701BD320C45748CBC5E47264C0125EF606F9C0D71F20F8D3C2BC8A3F30AE51E4753C4C09A262FC42E2FA48F7AD23E972F5497BD932E50973301C7875C5B5913834AF6464E6EDE4F6F5B198BF910F77697E74F529582103B4759FD5D9F5797FA31434A6CD90A333B43F48E
	EEC0198F204CE75239E4935036E7231E6EEAF0351F31E6D96F255B6899E003D3F58638114EBF03A10F49BC1F322D3E6B39093F6B94876998AE146266C1B8B7877332D638705F0A69FFCB672925C0D747E9EF2E2067B821CCC673G2591A1BEG0069EEE60BB75DCD4DE13AE3697F23A769E7GAE0117212F74EE7716CA14ABB387FD49C30A7EB3D5713B7F3876F3529924DF21AF6D6736AD2FD75A39EDDF4CFD5E23E66FBE6FF57E4ED4F1AE6AFBC33F3B4FFBE42F2C4391457A6F090A5F0BFF053560153F42DA70
	3FC75C7D467291255F103E3DC575CD3E33558E3A133E45423F51E56638D436CE8B61E40E5B486E4102F07EFC44E58CE2FEC59C3717FF2F2C02633E6177FBCF6238F3298FCF02F0BA47BD65407C0485085B4153AE6638D3ED6E7CC5595C792BB9027935713417717B61FBB86EAA4AF3B404E7F35C1B94178EE1E1A1365197945F86FDCD717B44DF619B1E13FF05B27AC6E419C4456FB7FE05F672015B77DDFE79F612B76A5ECE160C3A0FE3ABB74B32D7ABFE5F3969786B617163DA8613B9FEB36AE1995FB338F733
	F24F007955F72CDF882E67E059393E92657D84A535753B75757B70CC51AB7C9C5E6205B8B64BA657BFC8F94650EF35A973ECA17F6335AC0E12FD5B37B56A996DD2F17C34BF8DFC621DD63F2799F918BAAEDAA6CF8EBFE3D54765D9C90E7DCAF303B935B8B7980378A8G16EEE59D76716900E36796DA6A68FBBDF933F4DBG74F958FFED2EA77937D88768BBAC6E6709D236B23A08067FCAFC53F38A3556FE1B5582438A298A8ED5B7ED5F36FB977C167A40466FE43D3F3315486C611C4306CAF6D505C45B3975B3
	6F7A41AA174A695BAF17735B32711463D9460BAAA6A6E3D51BDC769FDABED3175DG8330224F2B6DA60379736242B278D7310A53BF59C2743E5BEA27FAE779F57EA467372EF27CBABB29F23CBA135E436F1FC85BE23C1DCC3FFF49669EAB86F16EE14E73BD7788G1A90G1C2BC5642967EA2F8FF89F333E4F3F2D9A456DF677206FB13C0927CB494376F365A1D90E1E2109FCE7EA314ED50EC21DAC874BCF4F6FEEAB56E96BE53EA05F5DD6254ACF2FD36BE50AC3FBEEF1BB9D2B34833D28C339625BE5398C1C0F
	F08FFCA86BA8C42A1F0FA4BE2295FC303C1FB748F97F055F0FB8C3A3EF753B4733D5B2263B49F8731077B177C37C4C65BC9D567D87CE77B177FB1D04C7560F3D6E3266DE3CE8162EEA1177359116AE4FE54DDD8FE9FA46156EF137B37F636BEFDF70B35DD7F57E2DBD5DC17B0031C533D4EBCF89949F2840337E65C09D76AFA190B7EF9176AF5F71FE296D2DD9633C4B816BE9BD7D961B34D6B2D8A80C370F8D98972D6773302D741BB60C7F13787B62A19E78E7E9C68FEB4B12A17597F39C1F8B1A7D8565C551DD
	C8651E454B0DFF8B5B6DC55DB2DF4F70334E390B3578C2D9EE6E11F5786250C4FC39749D24C17731683F875D67EC1FEA502D98F49F335E2DF87F79FE456FC3F547FF4C6A7C954A784145AFEC013AE9DEBC9E3FF00A4BDEDC9724F10EAB5D39AE6902A0CD583852E5386C45C5C11AB89F69243B7DB46AF9D0439EAE915D697EA03A53CD51203BCE03EE21469E60E217BD40FFD39B3F4CC9DE8C0AFCD6186F308B6F390F072ACFFF700148C3F57959C276F6E1C18FBD0C37DD18BFC4CE6BB9EDB087080156D268B8A2
	50FB41C8A8BBC6945EB2B89C56CE13C1B41614C03E6F70FC3753972BD3864BB767BCFED9CE36108B193243C75B05DCF20288723633B2F2C42B0D14A0505BC1700F16C4167A144307852210CB2EE195054F492D2B058E695A55C20323108C73DACC7D83E65A751514140842FCFAC5297C4CA5D7130A425B7B704BA40564BB8C3CA4C133FC979005C57235011A8967EBE297E9E76785CB7E486875B59FB4F150394C950545524DB22E0C0D37307F9B30646FG88932C966D3DAE2824357D64EB99057D6C1AFB28CAF2
	568C6C2F4E6868341BECC4EA281BE63C8E5FA18A4B2C8A0D90CB9984747819DA64D28BF52A10DDD862B2C5396D1275CF46BF3C5F11A3AC58AEFCC8D627DB0CF6FA792C310B5858FEA89B58E890F4B62B89A2818F6D40C19F739D62305958A4649F5EE7BCB2E0F408462EEA3CEE9564F83F091434966C5C3C365160689149313E2F1B04DC8B5540FBAC5AEC30F42FE5F75CFECD9BD3D6F5BB6517F828AC60F799651764D242F3D9F663BBABBF5B119A1DA3E4B7880B4CD6C3D70321D3345A378BBA3147EEF474D84D
	DDCE5B56AA1AD19E9DC43EC595E697BE626293E1CB484C01B35D673CB0E8C95A1206ECC1F05A05351412ECB814F00A2E5A9E2829CE28286DG86A8F2850FBB6193710B3B377D3BADDB481204A42ADF6304063B6030215FE49E069FF520DBEBB72D14B288326B9F3883A24AA8E9GF9443FBFBB73520736E424C3E68D42FCF2931491D8DAAAD36D9E686FB05ACF0EBC7B6337C2D2CEA59C5B2A3075C34EBA51EE30B88EDB6D7D10F921CAE60C622C177CFEE66959D73E7683E5DDCEAA8A1F32CA0224EB3B0D567E35
	55078F1BACC61E815498E47FB14F1E501428E85EF31E797E0E7B5E6CG8B2B93F27ACBCB697FE5747FF5647FB20AA9231832F5D0ED990257BFF53C93E526E6C9831544061C6457FA27EB83G3334D04D95DA8F979A659B36C4A12BD094BE6EG43AD34621DD8050725CB318AE985B01ABC745D3F2E2B9170CE6ED547145F591BF7D2E7B65E5F2E6E5FC373D97F9FA9BD656F6CCD265FEDC983FC6E7B3324F71E53C7498BA21E6E32EE69B7184DB0536E2B36F411FB2DEB06C523FC1772A5E3A4FF8752C811ACAEE2
	0C705D2BC873FFGD0CB8788384255081EC4GG9C6EGGD0CB818294G94G88G88GB0FBB0B6384255081EC4GG9C6EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG58C4GGGG
**end of data**/
}
}