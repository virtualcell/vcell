package cbit.vcell.modelopt.gui;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.optimization.ProfileSummaryData;
import org.vcell.optimization.gui.ConfidenceIntervalPlotPanel;
import org.vcell.optimization.gui.ProfileDataPanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.solvers.OptimizationService;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 5:21:08 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
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
	private UserPreferences fieldUserPreferences = null;
	private UserPreferences ivjuserPreferences1 = null;
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
	private ParameterEstimationTask fieldParameterEstimationTask = null;
	private boolean ivjConnPtoP4Aligning = false;
	private ParameterEstimationTask ivjparameterEstimationTask1 = null;
	private ModelOptimizationSpec ivjmodelOptimizationSpec = null;
	private OptimizationController ivjOptimizationController = null;
	private OptimizationController ivjOptimizationControllerFactory = null;
	private JCheckBox computeProfileDistributionsCheckBox = null;
	private JButton ivjEvaluateConfidenceIntervalButton = null;

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
				solve();
			if (e.getSource() == OptTestPanel.this.getStopButton()) 
				connEtoM20(e);
			else if (e.getSource() == OptTestPanel.this.getEvaluateConfidenceIntervalButton()) 
				evaluateConfidenceInterval(); 
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
	private void connEtoC11(ParameterEstimationTask value) {
		try {
			this.setOptimizationSolverTypeSelection();
		} catch (java.lang.Throwable ivjExc) {
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
	private void connEtoM1(ParameterEstimationTask value) {
		try {
			getparameterMappingPanel().setParameterEstimationTask(getparameterEstimationTask1());
		} catch (java.lang.Throwable ivjExc) {
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
	private void connEtoM12(ParameterEstimationTask value) {
		try {
			if ((getparameterEstimationTask1() != null)) {
				getJEditorPane1().setText(getparameterEstimationTask1().getAnnotation());
			}
		} catch (java.lang.Throwable ivjExc) {
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
	private void connEtoM15(ParameterEstimationTask value) {
		try {
			setmodelOptimizationSpec(this.getModelOptimizationSpec());
		} catch (java.lang.Throwable ivjExc) {
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
	private void solve() {
		try {
			// user code begin {1}
			// user code end
			getParameterEstimationTask().getModelOptimizationSpec().setComputeProfileDistributions(computeProfileDistributionsCheckBox.isSelected());
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
			getreferenceDataPanel().setParameterEstimationTask(getParameterEstimationTask());
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
	private void connEtoM20(java.awt.event.ActionEvent arg1) {
		try {
			getOptimizationController().stop();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM21:  (OptTestPanel.initialize() --> OptimizationController.this)
	 */
	private void connEtoM21() {
		OptimizationController localValue = null;
		try {
			setOptimizationController(localValue = new OptimizationController(this));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		setOptimizationControllerFactory(localValue);
	}


	/**
	 * connEtoM22:  (parameterEstimationTask1.this --> OptimizationController.parameterEstimationTask)
	 * @param value cbit.vcell.modelopt.ParameterEstimationTask
	 */
	private void connEtoM22(ParameterEstimationTask value) {
		try {
			getOptimizationController().setParameterEstimationTask(getparameterEstimationTask1());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM3:  (modelOptimizationSpec.this --> referenceDataPanel.referenceData)
	 * @param value cbit.vcell.modelopt.ModelOptimizationSpec
	 */
	private void connEtoM3(ModelOptimizationSpec value) {
		try {
			getreferenceDataPanel().setParameterEstimationTask(getParameterEstimationTask());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM4:  (parameterEstimationTask1.this --> referenceDataPanel.referenceData)
	 * @param value cbit.vcell.modelopt.ParameterEstimationTask
	 */
	private void connEtoM4(ParameterEstimationTask value) {
		try {
			getreferenceDataPanel().setParameterEstimationTask(getParameterEstimationTask());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM5:  (parameterEstimationTask1.this --> OptimizeResultsTextPane.text)
	 * @param value cbit.vcell.modelopt.ParameterEstimationTask
	 */
	private void connEtoM5(ParameterEstimationTask value) {
		try {
			getOptimizeResultsTextPane().setText(this.getSolverMessageText());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM6:  (userPreferences1.this --> referenceDataPanel.userPreferences)
	 * @param value cbit.vcell.client.server.UserPreferences
	 */
	private void connEtoM6(UserPreferences value) {
		try {
			getreferenceDataPanel().setUserPreferences(getuserPreferences1());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoM7:  (modelOptimizationSpec1.this --> referenceDataMappingSpecTableModel.modelOptimizationSpec)
	 * @param value cbit.vcell.modelopt.ModelOptimizationSpec
	 */
	private void connEtoM7(ModelOptimizationSpec value) {
		try {
			getreferenceDataMappingSpecTableModel().setModelOptimizationSpec(getmodelOptimizationSpec());
		} catch (java.lang.Throwable ivjExc) {
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
	private void connEtoM9(ParameterEstimationTask value) {
		try {
			if ((getparameterEstimationTask1() != null)) {
				getJTextField1().setText(getparameterEstimationTask1().getName());
			}
		} catch (java.lang.Throwable ivjExc) {
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
	private java.lang.String displayResults(OptimizationResultSet optResultSet) {
		if (optResultSet==null){
			return "no results";
		}
		StringBuffer buffer = new StringBuffer();

		buffer.append("\n-------------Optimizer Output-----------------\n");
		buffer.append(optResultSet.getOptSolverResultSet().getOptimizationStatus() + "\n");
		buffer.append("best objective function :"+optResultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue()+"\n");
		buffer.append("num function evaluations :"+optResultSet.getOptSolverResultSet().getObjFunctionEvaluations()+"\n");
		if (optResultSet.getOptSolverResultSet().getOptimizationStatus().isNormal()){
			buffer.append("status: complete\n");
		}else{
			buffer.append("status: aborted\n");
		}
		for (int i = 0; optResultSet.getOptSolverResultSet().getParameterNames()!=null && i < optResultSet.getOptSolverResultSet().getParameterNames().length; i++){
			buffer.append(optResultSet.getOptSolverResultSet().getParameterNames()[i]+" = "+optResultSet.getOptSolverResultSet().getBestEstimates()[i]+"\n");
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
				TitledBorder ivjLocalBorder2 = new TitledBorder("Optimization Solver");
				ivjLocalBorder2.setTitleJustification(javax.swing.border.TitledBorder.CENTER);
				ivjJPanel10 = new javax.swing.JPanel();
				ivjJPanel10.setName("JPanel10");
				ivjJPanel10.setBorder(ivjLocalBorder2);
				ivjJPanel10.setLayout(new java.awt.GridBagLayout());

				computeProfileDistributionsCheckBox = new JCheckBox("Compute Profile Distributions");
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = 0;
				gbc.weightx = 1.0;
				gbc.gridwidth = 2;
				gbc.insets = new java.awt.Insets(4, 4, 4, 0);
				ivjJPanel10.add(computeProfileDistributionsCheckBox, gbc);

				java.awt.GridBagConstraints constraintsSolverTypeComboBox = new java.awt.GridBagConstraints();
				constraintsSolverTypeComboBox.gridx = 0; 
				constraintsSolverTypeComboBox.gridy = 1;
				constraintsSolverTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsSolverTypeComboBox.weightx = 1.0;
				constraintsSolverTypeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
				constraintsSolverTypeComboBox.gridwidth = 2;
				ivjJPanel10.add(getSolverTypeComboBox(), constraintsSolverTypeComboBox);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 0; 
				gbc.gridy = 2;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.weighty = 1.0;
				gbc.weightx = 1.0;
				//			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.anchor = GridBagConstraints.PAGE_START;
				ivjJPanel10.add(getSolveButton(), gbc);

				gbc = new java.awt.GridBagConstraints();
				gbc.gridx = 1; 
				gbc.gridy = 2;
				gbc.insets = new java.awt.Insets(4, 4, 4, 4);
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				//			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
				gbc.anchor = GridBagConstraints.PAGE_START;
				ivjJPanel10.add(getStopButton(), gbc);
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
				ivjJPanel6.add(getSaveSolutionAsNewSimButton(), getSaveSolutionAsNewSimButton().getName());
				ivjJPanel6.add(getEvaluateConfidenceIntervalButton());
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
				TitledBorder ivjLocalBorder1 = new TitledBorder("Solution");
				ivjLocalBorder1.setTitleJustification(javax.swing.border.TitledBorder.CENTER);
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
	private javax.swing.JProgressBar getJProgressBar1() {
		if (ivjJProgressBar1 == null) {
			try {
				ivjJProgressBar1 = new javax.swing.JProgressBar();
				ivjJProgressBar1.setName("JProgressBar1");
			} catch (java.lang.Throwable ivjExc) {
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
	private ModelOptimizationSpec getmodelOptimizationSpec() {
		// user code begin {1}
		// user code end
		return ivjmodelOptimizationSpec;
	}

	/**
	 * Comment
	 */
	private ModelOptimizationSpec getModelOptimizationSpec() {
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
				ivjNumEvaluationsTitleLabel = new javax.swing.JLabel("Number of Evaluations: ");
				ivjNumEvaluationsTitleLabel.setName("NumEvaluationsTitleLabel");
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
	private javax.swing.JLabel getNumEvaluationsValueLabel() {
		if (ivjNumEvaluationsValueLabel == null) {
			try {
				ivjNumEvaluationsValueLabel = new javax.swing.JLabel();
				ivjNumEvaluationsValueLabel.setName("NumEvaluationsValueLabel");
				ivjNumEvaluationsValueLabel.setText(" ");
			} catch (java.lang.Throwable ivjExc) {
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
				ivjObjectiveFunctionTitleLabel = new javax.swing.JLabel("Best Objective Function Value: ");
				ivjObjectiveFunctionTitleLabel.setName("ObjectiveFunctionTitleLabel");
				ivjObjectiveFunctionTitleLabel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
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
	private javax.swing.JLabel getObjectiveFunctionValueLabel() {
		if (ivjObjectiveFunctionValueLabel == null) {
			try {
				ivjObjectiveFunctionValueLabel = new javax.swing.JLabel();
				ivjObjectiveFunctionValueLabel.setName("ObjectiveFunctionValueLabel");
				ivjObjectiveFunctionValueLabel.setText(" ");
			} catch (java.lang.Throwable ivjExc) {
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
	private javax.swing.JTextPane getOptimizeResultsTextPane() {
		if (ivjOptimizeResultsTextPane == null) {
			try {
				ivjOptimizeResultsTextPane = new javax.swing.JTextPane();
				ivjOptimizeResultsTextPane.setName("OptimizeResultsTextPane");
				ivjOptimizeResultsTextPane.setBounds(0, 0, 4, 107);
			} catch (java.lang.Throwable ivjExc) {
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
	public ParameterEstimationTask getParameterEstimationTask() {
		return fieldParameterEstimationTask;
	}


	/**
	 * Return the parameterEstimationTask1 property value.
	 * @return cbit.vcell.modelopt.ParameterEstimationTask
	 */
	private ParameterEstimationTask getparameterEstimationTask1() {
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

	private javax.swing.JButton getEvaluateConfidenceIntervalButton() {
		if ( ivjEvaluateConfidenceIntervalButton == null) {
			try {
				ivjEvaluateConfidenceIntervalButton = new javax.swing.JButton("Evaluate Confidence Interval");
				ivjEvaluateConfidenceIntervalButton.setName("EvaluateConfidenceIntervalButton");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjEvaluateConfidenceIntervalButton;
	}

	/**
	 * Return the referenceDataMappingSpecTableModel property value.
	 * @return cbit.vcell.modelopt.gui.ReferenceDataMappingSpecTableModel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private ReferenceDataMappingSpecTableModel getreferenceDataMappingSpecTableModel() {
		if (ivjreferenceDataMappingSpecTableModel == null) {
			try {
				ivjreferenceDataMappingSpecTableModel = new ReferenceDataMappingSpecTableModel();
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
				ivjreferenceDataPanel = new ReferenceDataPanel();
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
	private javax.swing.JButton getSaveSolutionAsNewSimButton() {
		if (ivjSaveSolutionAsNewSimButton == null) {
			try {
				ivjSaveSolutionAsNewSimButton = new javax.swing.JButton();
				ivjSaveSolutionAsNewSimButton.setName("SaveSolutionAsNewSimButton");
				ivjSaveSolutionAsNewSimButton.setText("Save Solution as New Simulation...");
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
	private javax.swing.JButton getSolveButton() {
		if (ivjSolveButton == null) {
			try {
				ivjSolveButton = new javax.swing.JButton();
				ivjSolveButton.setName("SolveButton");
				ivjSolveButton.setText("Solve");
				ivjSolveButton.setActionCommand("Solve");
			} catch (java.lang.Throwable ivjExc) {
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
	private javax.swing.JComboBox getSolverTypeComboBox() {
		if (ivjSolverTypeComboBox == null) {
			try {
				ivjSolverTypeComboBox = new javax.swing.JComboBox();
				ivjSolverTypeComboBox.setName("SolverTypeComboBox");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjSolverTypeComboBox;
	}

	/**
	 * Return the JButton1 property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getStopButton() {
		if (ivjStopButton == null) {
			try {
				ivjStopButton = new javax.swing.JButton();
				ivjStopButton.setName("StopButton");
				ivjStopButton.setText("Stop");
				ivjStopButton.setEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
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
	public UserPreferences getUserPreferences() {
		return fieldUserPreferences;
	}


	/**
	 * Return the userPreferences1 property value.
	 * @return cbit.vcell.client.server.UserPreferences
	 */
	private UserPreferences getuserPreferences1() {
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
		getEvaluateConfidenceIntervalButton().addActionListener(ivjEventHandler);
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
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
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
			frame.pack();
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

		SymbolTableEntry symbolTableEntries[] = getParameterEstimationTask().getModelOptimizationSpec().calculateTimeDependentModelObjects();

		java.util.Comparator<SymbolTableEntry> steComparator = new java.util.Comparator<SymbolTableEntry>() {
			private Class<?>[] classOrder = new Class<?>[] { ReservedSymbol.class, SpeciesContext.class, Model.ModelParameter.class, Kinetics.KineticsParameter.class };
			public int compare(SymbolTableEntry ste1, SymbolTableEntry ste2){
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

		SymbolTableEntry ste = (SymbolTableEntry)DialogUtils.showListDialog(this,symbolTableEntries,"select data association",new SymbolTableEntryListCellRenderer());

		if (ste!=null && getselectionModel1().getMaxSelectionIndex()>=0){
			ReferenceDataMappingSpec refDataMappingSpec = getParameterEstimationTask().getModelOptimizationSpec().getReferenceDataMappingSpecs()[getselectionModel1().getMaxSelectionIndex()];
			try {
				refDataMappingSpec.setModelObject(ste);
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this,e.getMessage());
			}
		}
		return;
	}


	/**
	 * Comment
	 */
	private void optimizationResultSet_This(OptimizationResultSet optResultSet) {
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
	public void setParameterEstimationTask(ParameterEstimationTask parameterEstimationTask) {
		ParameterEstimationTask oldValue = fieldParameterEstimationTask;
		fieldParameterEstimationTask = parameterEstimationTask;
		firePropertyChange("parameterEstimationTask", oldValue, parameterEstimationTask);
	}


	/**
	 * Set the parameterEstimationTask1 to a new value.
	 * @param newValue cbit.vcell.modelopt.ParameterEstimationTask
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void setparameterEstimationTask1(ParameterEstimationTask newValue) {
		if (ivjparameterEstimationTask1 != newValue) {
			try {
				ParameterEstimationTask oldValue = getparameterEstimationTask1();
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
	public void setUserPreferences(UserPreferences userPreferences) {
		UserPreferences oldValue = fieldUserPreferences;
		fieldUserPreferences = userPreferences;
		firePropertyChange("userPreferences", oldValue, userPreferences);
	}


	/**
	 * Set the userPreferences1 to a new value.
	 * @param newValue cbit.vcell.client.server.UserPreferences
	 */
	private void setuserPreferences1(UserPreferences newValue) {
		if (ivjuserPreferences1 != newValue) {
			try {
				ivjuserPreferences1 = newValue;
				connPtoP2SetSource();
				connEtoM6(ivjuserPreferences1);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		};
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
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(solverName);
		getParameterEstimationTask().setOptimizationSolverSpec(optSolverSpec);
	}

	public void updateInterface(boolean bSolving) {
		getSaveSolutionAsNewSimButton().setEnabled(!bSolving);
		getPlotButton().setEnabled(!bSolving);	
		getSolveButton().setEnabled(!bSolving);
		getStopButton().setEnabled(bSolving);

		getJProgressBar1().setValue(bSolving ? 0 : 100);
		getSolverTypeComboBox().setEnabled(!bSolving);
	}

	public void setProgress(int p) {
		getJProgressBar1().setValue(p);
	}

	public void setNumEvaluations(String num) {
		getNumEvaluationsValueLabel().setText(num);
	}

	public void setObjectFunctionValue(String d) {
		getObjectiveFunctionValueLabel().setText(d);
	}

	public void evaluateConfidenceInterval() 
	{
		ProfileSummaryData[] summaryData = null;
		try {
			summaryData = getOptimizationController().evaluateConfidenceInterval();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			DialogUtils.showErrorDialog(this, e.getMessage());
			e.printStackTrace();
		}
		//put plotpanes of different parameters' profile likelihoods into a base panel
		JPanel basePanel= new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		for(ProfileSummaryData aSumData : summaryData)
		{
			ConfidenceIntervalPlotPanel plotPanel = new ConfidenceIntervalPlotPanel();
			plotPanel.setProfileSummaryData(aSumData);
			plotPanel.setBorder(new EtchedBorder());
			
			ProfileDataPanel profileDataPanel = new ProfileDataPanel(plotPanel, aSumData.getParamName());
			basePanel.add(profileDataPanel);
		}
		JScrollPane scrollPane = new JScrollPane(basePanel);
		scrollPane.setAutoscrolls(true);
		scrollPane.setPreferredSize(new Dimension(620, 600));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//show plots in a dialog
		DialogUtils.showComponentCloseDialog(OptTestPanel.this, scrollPane, "Profile Likelihood of Parameters");
	}

}