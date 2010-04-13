package cbit.vcell.client.desktop.biomodel;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import org.vcell.util.Issue;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.gui.MultiPurposeTextPanel;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StochMathMapping;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.mapping.gui.StructureMappingCartoonPanel;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.gui.MathDescEditor;
import cbit.vcell.math.gui.MathDescPanel;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel;
import cbit.vcell.modelopt.gui.OptTestPanel;
import cbit.vcell.opt.solvers.OptimizationService;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 3:16:22 PM)
 * @author: Ion Moraru
 */
public class ApplicationEditor extends JPanel {
	public static final int TAB_IDX_SPPR = 0;
	public static final int TAB_IDX_VIEW_MATH = 1;
	public static final int TAB_IDX_SIMULATION = 2;
	public static final int TAB_IDX_ANALYSIS = 3;
	
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP2Aligning = false;
	private SimulationWorkspace fieldSimulationWorkspace = null;
    protected transient ActionListener actionListener = null;
	private SimulationWorkspace ivjsimulationWorkspace1 = null;
	private InitialConditionsPanel ivjInitialConditionsPanel = null;
	private JTabbedPane ivjJTabbedPane1 = null;
	private ReactionSpecsPanel ivjReactionSpecsPanel = null;
	private SimulationListPanel ivjSimulationListPanel = null;
	private SPPRPanel ivjSPPRPanel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private boolean ivjConnPtoP6Aligning = false;
	private SimulationContext ivjsimulationContext = null;
	private GeometryContext ivjgeometryContext = null;
	private JPanel ivjJPanel3 = null;
	private JPanel ivjStructureMappingPanel = null;
	private JButton ivjViewModifyGeometryButton = null;
	private JRadioButton ivjViewEqunsRadioButton = null;
	private JPanel ivjViewMathPanel = null;
	private JRadioButton ivjViewVCMDLRadioButton = null;
	private MathDescPanel ivjMathDescPanel1 = null;
	private JPanel ivjMathViewerPanel = null;
	
	private MultiPurposeTextPanel ivjVCMLEditorPane = null;
	private JPanel ivjVCMLPanel = null;
	private MathDescription ivjmathDescription = null;
	private CardLayout ivjcardLayout = null;
	private ButtonGroup ivjbuttonGroup = null;
	private JPanel ivjButtonsPanel = null;
	private JButton ivjCreateMathModelButton = null;
	private JButton ivjRefreshMathButton = null;
	private JPanel ivjJPanel2 = null;
	private OptTestPanel ivjoptTestPanel = null;
	private JPanel ivjParameterEstimationPanel = null;
	private JComboBox ivjAnalysisTaskComboBox = null;
	private JButton ivjDeleteAnalysisTaskButton = null;
	private JButton ivjNewAnalysisTaskButton = null;
	private AnalysisTaskComboBoxModel ivjAnalysisTaskComboBoxModel = null;
	private boolean ivjConnPtoP8Aligning = false;
	private ComboBoxModel ivjmodel1 = null;
	private JButton ivjCopyButton = null;
	
	         
class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ApplicationEditor.this.getSPPRPanel()) 
				refireActionPerformed(e);
			if (e.getSource() == ApplicationEditor.this.getRefreshMathButton()) 
				connEtoC4(e);
			if (e.getSource() == ApplicationEditor.this.getCreateMathModelButton()) 
				connEtoC5(e);
			if (e.getSource() == ApplicationEditor.this.getAnalysisTaskComboBox()) 
				connEtoC6(e);
			if (e.getSource() == ApplicationEditor.this.getNewAnalysisTaskButton()) 
				connEtoC8(e);
			if (e.getSource() == ApplicationEditor.this.getDeleteAnalysisTaskButton()) 
				connEtoC9(e);
			if (e.getSource() == ApplicationEditor.this.getCopyButton()) 
				connEtoC10(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ApplicationEditor.this.getViewEqunsRadioButton()) 
				connEtoC1(e);
			if (e.getSource() == ApplicationEditor.this.getViewVCMDLRadioButton()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ApplicationEditor.this && (evt.getPropertyName().equals("simulationWorkspace"))) {
				connPtoP1SetTarget();
				refreshAnalysisTab();
				refreshSPPRTab();
			}
			if (evt.getSource() == ApplicationEditor.this.getSimulationListPanel() && (evt.getPropertyName().equals("simulationWorkspace"))) 
				connPtoP2SetSource();
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) {
				connPtoP4SetTarget();
				refreshSPPRTab();
			}
			if (evt.getSource() == ApplicationEditor.this.getInitialConditionsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
			{
				connPtoP4SetSource();
			}
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getReactionSpecsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
			{
				connPtoP5SetSource();
			}
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connEtoM1(evt);
			if (evt.getSource() == ApplicationEditor.this.getsimulationContext() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoM7(evt);
			if (evt.getSource() == ApplicationEditor.this.getsimulationContext() && (evt.getPropertyName().equals("geometry"))) { 
				refreshAnalysisTab();
			}
			if (evt.getSource() == ApplicationEditor.this.getAnalysisTaskComboBox() && (evt.getPropertyName().equals("model"))) 
				connPtoP8SetTarget();
		}
	};

public ApplicationEditor() {
	super();
	initialize();
}

public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
}


/**
 * Comment
 */
private void analysisTaskComboBox_ActionPerformed() {
	getoptTestPanel().setParameterEstimationTask((ParameterEstimationTask)getAnalysisTaskComboBoxModel().getSelectedItem());
	return;
}


/**
 * connEtoC1:  (ViewEqunsRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ApplicationEditor.viewMath_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.viewMath_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (CopyButton.action.actionPerformed(java.awt.event.ActionEvent) --> ApplicationEditor.copyAnalysisTaskButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyAnalysisTaskButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (simulationContext.this --> ApplicationEditor.refreshAnalysisTaskEnables()V)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshAnalysisTaskEnables();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (ApplicationEditor.initialize() --> ApplicationEditor.initializeComboBoxModel()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12() {
	try {
		// user code begin {1}
		// user code end
		this.initializeComboBoxModel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ViewVCMDLRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ApplicationEditor.viewMath_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.viewMath_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> ApplicationEditor.showGeometryViewer()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (RefreshButton.action.actionPerformed(java.awt.event.ActionEvent) --> ApplicationEditor.updateMath()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), updateMath(), false);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (CreateMathModelButton.action.actionPerformed(java.awt.event.ActionEvent) --> ApplicationEditor.createNewMathModel(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.createMathModel(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (ModelOptSpecComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> ApplicationEditor.modelOptSpecComboBox_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.analysisTaskComboBox_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC7:  (mathDescription.this --> ApplicationEditor.mathDescription_This()V)
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(MathDescription value) {
	try {
		// user code begin {1}
		// user code end
		this.mathDescription_This();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (NewModelOptSpecButton.action.actionPerformed(java.awt.event.ActionEvent) --> ApplicationEditor.newModelOptSpecButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newParameterEstimationTaskButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC9:  (DeleteModelOptSpecButton.action.actionPerformed(java.awt.event.ActionEvent) --> ApplicationEditor.deleteModelOptSpecButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deleteAnalysisTaskButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (simulationWorkspace1.simulationOwner --> simulationContext.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setsimulationContext((SimulationContext)getsimulationWorkspace1().getSimulationOwner());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (ApplicationEditor.initialize() --> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10() {
	try {
		// user code begin {1}
		// user code end
		setmodel1(getAnalysisTaskComboBoxModel());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (simulationContext.this --> geometryContext.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		setgeometryContext(this.getGeometryContext());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (simulationWorkspace1.this --> simulationContext.this)
 * @param value cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(SimulationWorkspace value) {
	try {
		// user code begin {1}
		// user code end
		setsimulationContext(this.getSimulationContext());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (ApplicationEditor.initialize() --> buttonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getViewVCMDLRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (ApplicationEditor.initialize() --> buttonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getViewEqunsRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (simulationContext.this --> mathDescription.this)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext() != null)) {
			setmathDescription(getsimulationContext().getMathDescription());
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
 * connEtoM7:  (simulationContext.mathDescription --> mathDescription.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext() != null)) {
			setmathDescription(getsimulationContext().getMathDescription());
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
 * connEtoM8:  (mathDescription.this --> MathDescPanel1.mathDescription)
 * @param value cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(MathDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getmathDescription() != null)) {
			getMathDescPanel1().setMathDescription(getmathDescription());
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
 * connEtoM9:  (simulationContext.this --> modelOptSpecComboBoxModel.simulationContext)
 * @param value cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(SimulationContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsimulationContext() != null)) {
			getAnalysisTaskComboBoxModel().setSimulationContext(getsimulationContext());
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
 * connPtoP1SetSource:  (ApplicationEditor.simulationContext <--> MappingEditorPanel1.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				this.setSimulationWorkspace(getsimulationWorkspace1());
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
 * connPtoP1SetTarget:  (ApplicationEditor.simulationContext <--> MappingEditorPanel1.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setsimulationWorkspace1(this.getSimulationWorkspace());
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
 * connPtoP2SetSource:  (ApplicationEditor.simulationContext <--> SimulationListPanel1.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setsimulationWorkspace1(getSimulationListPanel().getSimulationWorkspace());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (ApplicationEditor.simulationContext <--> SimulationListPanel1.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getSimulationListPanel().setSimulationWorkspace(getsimulationWorkspace1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP4SetSource:  (simulationWorkspace1.simulationOwner <--> InitialConditionsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getsimulationWorkspace1().setSimulationOwner(getInitialConditionsPanel().getSimulationContext());
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
 * connPtoP4SetTarget:  (simulationWorkspace1.simulationOwner <--> InitialConditionsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getInitialConditionsPanel().setSimulationContext((SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
 * connPtoP5SetSource:  (simulationWorkspace1.simulationOwner <--> ReactionSpecsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getsimulationWorkspace1().setSimulationOwner(getReactionSpecsPanel().getSimulationContext());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (simulationWorkspace1.simulationOwner <--> ReactionSpecsPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getReactionSpecsPanel().setSimulationContext((SimulationContext)getsimulationWorkspace1().getSimulationOwner());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP6SetSource:  (simulationWorkspace1.simulationOwner <--> ElectricalMembraneMappingPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getsimulationWorkspace1().setSimulationOwner(getSimulationContext());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP7SetSource:  (MathViewerPanel.layout <--> cardLayout.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetSource() {
	/* Set the source from the target */
	try {
		if ((getcardLayout() != null)) {
			getMathViewerPanel().setLayout(getcardLayout());
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
 * connPtoP7SetTarget:  (MathViewerPanel.layout <--> cardLayout.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		setcardLayout((java.awt.CardLayout)getMathViewerPanel().getLayout());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP8SetSource:  (AnalysisTaskComboBox.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			if ((getmodel1() != null)) {
				getAnalysisTaskComboBox().setModel(getmodel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP8SetTarget:  (AnalysisTaskComboBox.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			setmodel1(getAnalysisTaskComboBox().getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void copyAnalysisTaskButton_ActionPerformed() {
	try {
		AnalysisTask taskToCopy = (AnalysisTask) getAnalysisTaskComboBoxModel().getSelectedItem();
		if (getSimulationContext() != null && taskToCopy != null){
			AnalysisTask newAnalysisTask = getsimulationContext().copyAnalysisTask(taskToCopy);
			getAnalysisTaskComboBox().setSelectedItem(newAnalysisTask);
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		org.vcell.util.gui.DialogUtils.showErrorDialog(this,e.getMessage());
	}
}


private void createMathModel(final ActionEvent e) {
	// relays an action event with this as the source
	AsynchClientTask[] updateTasks = updateMath();
	AsynchClientTask[] tasks = new AsynchClientTask[updateTasks.length + 1];
	System.arraycopy(updateTasks, 0, tasks, 0, updateTasks.length);
	
	tasks[updateTasks.length] = new AsynchClientTask("creating math model", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			refireActionPerformed(e);
		}
	};
	
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), tasks, false);
}


/**
 * Comment
 */
private void deleteAnalysisTaskButton_ActionPerformed() throws java.beans.PropertyVetoException {
	AnalysisTask taskToDelete = (AnalysisTask) getAnalysisTaskComboBoxModel().getSelectedItem();
	if (taskToDelete != null && getSimulationContext() != null) {
		getSimulationContext().removeAnalysisTask(taskToDelete);
	}
	return;
}


protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}


/**
 * Return the ModelOptSpecComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAnalysisTaskComboBox() {
	if (ivjAnalysisTaskComboBox == null) {
		try {
			ivjAnalysisTaskComboBox = new javax.swing.JComboBox();
			ivjAnalysisTaskComboBox.setName("AnalysisTaskComboBox");
			ivjAnalysisTaskComboBox.setPreferredSize(new java.awt.Dimension(300, 23));
			ivjAnalysisTaskComboBox.setRenderer(new AnalysisTaskListCellRenderer());
			ivjAnalysisTaskComboBox.setMinimumSize(new java.awt.Dimension(300, 23));
			ivjAnalysisTaskComboBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalysisTaskComboBox;
}

/**
 * Return the AnalysisTaskComboBoxModel property value.
 * @return cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private AnalysisTaskComboBoxModel getAnalysisTaskComboBoxModel() {
	if (ivjAnalysisTaskComboBoxModel == null) {
		try {
			ivjAnalysisTaskComboBoxModel = new AnalysisTaskComboBoxModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalysisTaskComboBoxModel;
}


/**
 * Return the buttonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getbuttonGroup() {
	if (ivjbuttonGroup == null) {
		try {
			ivjbuttonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbuttonGroup;
}


/**
 * Return the RadioButtonsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonsPanel() {
	if (ivjButtonsPanel == null) {
		try {
			ivjButtonsPanel = new javax.swing.JPanel();
			ivjButtonsPanel.setName("ButtonsPanel");
			ivjButtonsPanel.setPreferredSize(new java.awt.Dimension(280, 40));
			ivjButtonsPanel.setLayout(new java.awt.FlowLayout());
			ivjButtonsPanel.add(getViewEqunsRadioButton(), getViewEqunsRadioButton().getName());
			ivjButtonsPanel.add(getViewVCMDLRadioButton(), getViewVCMDLRadioButton().getName());
			//ivjButtonsPanel.add(getSearchLabel(), getCreateMathModelButton().getName());
			//ivjButtonsPanel.add(getSearchTextField(), getRefreshMathButton().getName());
			ivjButtonsPanel.add(getCreateMathModelButton(), getCreateMathModelButton().getName());
			ivjButtonsPanel.add(getRefreshMathButton(), getRefreshMathButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonsPanel;
}

/**
 * Return the cardLayout property value.
 * @return java.awt.CardLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.CardLayout getcardLayout() {
	// user code begin {1}
	// user code end
	return ivjcardLayout;
}


/**
 * Return the CopyButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCopyButton() {
	if (ivjCopyButton == null) {
		try {
			ivjCopyButton = new javax.swing.JButton();
			ivjCopyButton.setName("CopyButton");
			ivjCopyButton.setText("Copy...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyButton;
}


/**
 * Return the CreateMathModelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCreateMathModelButton() {
	if (ivjCreateMathModelButton == null) {
		try {
			ivjCreateMathModelButton = new javax.swing.JButton();
			ivjCreateMathModelButton.setName("CreateMathModelButton");
			ivjCreateMathModelButton.setText("Create Math Model");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCreateMathModelButton;
}


/**
 * Return the DeleteModelOptSpecButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteAnalysisTaskButton() {
	if (ivjDeleteAnalysisTaskButton == null) {
		try {
			ivjDeleteAnalysisTaskButton = new javax.swing.JButton();
			ivjDeleteAnalysisTaskButton.setName("DeleteAnalysisTaskButton");
			ivjDeleteAnalysisTaskButton.setText("Delete");
			ivjDeleteAnalysisTaskButton.setEnabled(false);
			ivjDeleteAnalysisTaskButton.setActionCommand("DeleteModelOptSpec");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteAnalysisTaskButton;
}

/**
 * Comment
 */
private GeometryContext getGeometryContext() {
	if (getsimulationContext()==null){
		return null;
	}else{
		return getsimulationContext().getGeometryContext();
	}
}


/**
 * Return the InitialConditionsPanel property value.
 * @return cbit.vcell.mapping.gui.InitialConditionsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private InitialConditionsPanel getInitialConditionsPanel() {
	if (ivjInitialConditionsPanel == null) {
		try {
			ivjInitialConditionsPanel = new InitialConditionsPanel();
			ivjInitialConditionsPanel.setName("InitialConditionsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitialConditionsPanel;
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
			ivjJPanel2.setLayout(new java.awt.FlowLayout());
			getJPanel2().add(getAnalysisTaskComboBox(), getAnalysisTaskComboBox().getName());
			getJPanel2().add(getNewAnalysisTaskButton(), getNewAnalysisTaskButton().getName());
			getJPanel2().add(getCopyButton(), getCopyButton().getName());
			getJPanel2().add(getDeleteAnalysisTaskButton(), getDeleteAnalysisTaskButton().getName());
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
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.setPreferredSize(new java.awt.Dimension(682, 640));
			ivjJTabbedPane1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJTabbedPane1.insertTab("Specifications", null, getSPPRPanel(), null, TAB_IDX_SPPR);
			ivjJTabbedPane1.insertTab("View Math", null, getViewMathPanel(), null, TAB_IDX_VIEW_MATH);
			ivjJTabbedPane1.insertTab("Simulation", null, getSimulationListPanel(), null, TAB_IDX_SIMULATION);
			ivjJTabbedPane1.insertTab("Analysis", null, getParameterEstimationPanel(), null, TAB_IDX_ANALYSIS);
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
 * Return the MathDescPanel1 property value.
 * @return cbit.vcell.math.gui.MathDescPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescPanel getMathDescPanel1() {
	if (ivjMathDescPanel1 == null) {
		try {
			ivjMathDescPanel1 = new MathDescPanel();
			ivjMathDescPanel1.setName("MathDescPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathDescPanel1;
}


/**
 * Return the mathDescription property value.
 * @return cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathDescription getmathDescription() {
	// user code begin {1}
	// user code end
	return ivjmathDescription;
}


/**
 * Return the MathViewerPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMathViewerPanel() {
	if (ivjMathViewerPanel == null) {
		try {
			ivjMathViewerPanel = new javax.swing.JPanel();
			ivjMathViewerPanel.setName("MathViewerPanel");
			ivjMathViewerPanel.setLayout(new java.awt.CardLayout());
			getMathViewerPanel().add(getMathDescPanel1(), getMathDescPanel1().getName());
			getMathViewerPanel().add(getVCMLPanel(), getVCMLPanel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathViewerPanel;
}


/**
 * Return the model1 property value.
 * @return javax.swing.ComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ComboBoxModel getmodel1() {
	// user code begin {1}
	// user code end
	return ivjmodel1;
}


/**
 * Return the NewModelOptSpecButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNewAnalysisTaskButton() {
	if (ivjNewAnalysisTaskButton == null) {
		try {
			ivjNewAnalysisTaskButton = new javax.swing.JButton();
			ivjNewAnalysisTaskButton.setName("NewAnalysisTaskButton");
			ivjNewAnalysisTaskButton.setText("New Parameter Estimation Task...");
			ivjNewAnalysisTaskButton.setActionCommand("NewModelOptSpec");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewAnalysisTaskButton;
}

/**
 * Method generated to support the promotion of the optimizationService attribute.
 * @return cbit.vcell.opt.solvers.OptimizationService
 */
public OptimizationService getOptimizationService() {
	return getoptTestPanel().getOptimizationService();
}


/**
 * Return the optTestPanel property value.
 * @return cbit.vcell.modelopt.gui.OptTestPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private OptTestPanel getoptTestPanel() {
	if (ivjoptTestPanel == null) {
		try {
			ivjoptTestPanel = new OptTestPanel();
			ivjoptTestPanel.setName("optTestPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjoptTestPanel;
}


/**
 * Return the ParameterEstimationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getParameterEstimationPanel() {
	if (ivjParameterEstimationPanel == null) {
		try {
			ivjParameterEstimationPanel = new javax.swing.JPanel();
			ivjParameterEstimationPanel.setName("ParameterEstimationPanel");
			ivjParameterEstimationPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjParameterEstimationPanel.setLayout(new java.awt.BorderLayout());
			ivjParameterEstimationPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			getParameterEstimationPanel().add(getoptTestPanel(), "Center");
			getParameterEstimationPanel().add(getJPanel2(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterEstimationPanel;
}


/**
 * Return the ReactionSpecsPanel property value.
 * @return cbit.vcell.mapping.gui.ReactionSpecsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ReactionSpecsPanel getReactionSpecsPanel() {
	if (ivjReactionSpecsPanel == null) {
		try {
			ivjReactionSpecsPanel = new ReactionSpecsPanel();
			ivjReactionSpecsPanel.setName("ReactionSpecsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReactionSpecsPanel;
}


/**
 * Return the RefreshMathButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRefreshMathButton() {
	if (ivjRefreshMathButton == null) {
		try {
			ivjRefreshMathButton = new javax.swing.JButton();
			ivjRefreshMathButton.setName("RefreshMathButton");
			ivjRefreshMathButton.setText("Refresh Math");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshMathButton;
}


/**
 * Return the simulationContext property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationContext getsimulationContext() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext;
}


/**
 * Comment
 */
private SimulationContext getSimulationContext() {
	if (getSimulationWorkspace()==null || getSimulationWorkspace().getSimulationOwner()==null){
		return null;
	}else{
		return (SimulationContext)getSimulationWorkspace().getSimulationOwner();
	}
}


/**
 * Return the SimulationListPanel1 property value.
 * @return cbit.vcell.client.desktop.simulation.SimulationListPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationListPanel getSimulationListPanel() {
	if (ivjSimulationListPanel == null) {
		try {
			ivjSimulationListPanel = new SimulationListPanel();
			ivjSimulationListPanel.setName("SimulationListPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimulationListPanel;
}

/**
 * Return the SPPRPanel1 property value.
 * @return cbit.vcell.client.desktop.simulation.SimulationListPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SPPRPanel getSPPRPanel() {
	if (ivjSPPRPanel == null) {
		try {
			ivjSPPRPanel = new SPPRPanel();
			ivjSPPRPanel.setName("SpeciesParamsReactionsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSPPRPanel;
}

/**
 * Gets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @return The simulationWorkspace property value.
 * @see #setSimulationWorkspace
 */
public SimulationWorkspace getSimulationWorkspace() {
	return fieldSimulationWorkspace;
}


/**
 * Return the simulationWorkspace1 property value.
 * @return cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SimulationWorkspace getsimulationWorkspace1() {
	// user code begin {1}
	// user code end
	return ivjsimulationWorkspace1;
}

/**
 * Method generated to support the promotion of the userPreferences attribute.
 * @return cbit.vcell.client.server.UserPreferences
 */
public UserPreferences getUserPreferences() {
	return getoptTestPanel().getUserPreferences();
}


/**
 * Return the VCMLEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MultiPurposeTextPanel getVCMLEditorPane() {
	if (ivjVCMLEditorPane == null) {
		try {
			ivjVCMLEditorPane = new MultiPurposeTextPanel(false);
			ivjVCMLEditorPane.setKeywords(MathDescEditor.getkeywords());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjVCMLEditorPane;
}

/**
 * Return the VCMLPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getVCMLPanel() {
	if (ivjVCMLPanel == null) {
		try {
			ivjVCMLPanel = new javax.swing.JPanel();
			ivjVCMLPanel.setName("VCMLPanel");
			ivjVCMLPanel.setLayout(new java.awt.BorderLayout());
			ivjVCMLPanel.add(getVCMLEditorPane(), BorderLayout.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVCMLPanel;
}

/**
 * Return the ViewEqunsRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getViewEqunsRadioButton() {
	if (ivjViewEqunsRadioButton == null) {
		try {
			ivjViewEqunsRadioButton = new javax.swing.JRadioButton();
			ivjViewEqunsRadioButton.setName("ViewEqunsRadioButton");
			ivjViewEqunsRadioButton.setSelected(true);
			ivjViewEqunsRadioButton.setText("View Math Equations");
			ivjViewEqunsRadioButton.setActionCommand("MathDescPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewEqunsRadioButton;
}

/**
 * Return the ViewMathPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getViewMathPanel() {
	if (ivjViewMathPanel == null) {
		try {
			ivjViewMathPanel = new javax.swing.JPanel();
			ivjViewMathPanel.setName("ViewMathPanel");
			ivjViewMathPanel.setPreferredSize(new java.awt.Dimension(680, 640));
			ivjViewMathPanel.setLayout(new java.awt.BorderLayout());
			getViewMathPanel().add(getButtonsPanel(), "North");
			getViewMathPanel().add(getMathViewerPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewMathPanel;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getViewModifyGeometryButton() {
	if (ivjViewModifyGeometryButton == null) {
		try {
			ivjViewModifyGeometryButton = new javax.swing.JButton();
			ivjViewModifyGeometryButton.setName("ViewModifyGeometryButton");
			ivjViewModifyGeometryButton.setText("View / Change Geometry");
			ivjViewModifyGeometryButton.setActionCommand(GuiConstants.ACTIONCMD_VIEW_CHANGE_GEOMETRY);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewModifyGeometryButton;
}

/**
 * Return the ViewVCMDLRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getViewVCMDLRadioButton() {
	if (ivjViewVCMDLRadioButton == null) {
		try {
			ivjViewVCMDLRadioButton = new javax.swing.JRadioButton();
			ivjViewVCMDLRadioButton.setName("ViewVCMDLRadioButton");
			ivjViewVCMDLRadioButton.setText("View Math Description Language");
			ivjViewVCMDLRadioButton.setActionCommand("VCMLPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewVCMDLRadioButton;
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
	getViewModifyGeometryButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getSimulationListPanel().addPropertyChangeListener(ivjEventHandler);
	getSPPRPanel().addPropertyChangeListener(ivjEventHandler);
	getSPPRPanel().addCommandActionListener(ivjEventHandler);
	getInitialConditionsPanel().addPropertyChangeListener(ivjEventHandler);
	getReactionSpecsPanel().addPropertyChangeListener(ivjEventHandler);
	getViewEqunsRadioButton().addItemListener(ivjEventHandler);
	getViewVCMDLRadioButton().addItemListener(ivjEventHandler);
	getRefreshMathButton().addActionListener(ivjEventHandler);
	getCreateMathModelButton().addActionListener(ivjEventHandler);
	getAnalysisTaskComboBox().addActionListener(ivjEventHandler);
	getNewAnalysisTaskButton().addActionListener(ivjEventHandler);
	getDeleteAnalysisTaskButton().addActionListener(ivjEventHandler);
	getAnalysisTaskComboBox().addPropertyChangeListener(ivjEventHandler);
	getCopyButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP7SetTarget();
	connPtoP8SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ApplicationEditor");
		setLayout(new java.awt.BorderLayout());
		setSize(825, 642);
		add(getJTabbedPane1(), "Center");
		initConnections();
		connEtoM4();
		connEtoM5();
		connEtoC12();
		connEtoM10();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

//added in March 2008, to disable or enable the analysis tab
private void refreshAnalysisTab()
{
	if(getJTabbedPane1() != null)
	{
		if(getSimulationContext().isStoch()) //stochastic
		{
			ivjJTabbedPane1.setEnabledAt(ApplicationEditor.TAB_IDX_ANALYSIS, false);
		}
		else
		{
			if(getSimulationContext().getGeometryContext().getGeometry().getDimension() != 0)//pde
			{
				ivjJTabbedPane1.setEnabledAt(ApplicationEditor.TAB_IDX_ANALYSIS, false);
			}
			else //ode
			{
				ivjJTabbedPane1.setEnabledAt(ApplicationEditor.TAB_IDX_ANALYSIS, true);
			}
		}
	}
}
/**
 * Comment
 */
private void initializeComboBoxModel() {
	AnalysisTaskComboBoxModel analysisTaskCBModel = new AnalysisTaskComboBoxModel();
	analysisTaskCBModel.setSimulationContext(getSimulationContext());
	getAnalysisTaskComboBox().setModel(analysisTaskCBModel);
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		ApplicationEditor aApplicationEditor;
		aApplicationEditor = new ApplicationEditor();
		frame.setContentPane(aApplicationEditor);
		frame.setSize(aApplicationEditor.getSize());
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
private void mathDescription_This() {
	if (getmathDescription()!=null){
		try {
			getVCMLEditorPane().setText(getmathDescription().getVCML_database());
			getVCMLEditorPane().setCaretPosition(0);
		}catch (Exception e){
			e.printStackTrace(System.out);
			getVCMLEditorPane().setText("error displaying math language: "+e.getMessage());
		}
	}else{
		getVCMLEditorPane().setText("");
	}
}


/**
 * Comment
 */
private void newParameterEstimationTaskButton_ActionPerformed() {
	try {
		String parameterEstimationName = "task0";
		if (getsimulationContext()==null){
			return;
		}

		AnalysisTask analysisTasks[] = getSimulationContext().getAnalysisTasks();
		boolean found = true;
		while (found) {
			found = false;
			parameterEstimationName = TokenMangler.getNextEnumeratedToken(parameterEstimationName);
			for (int i = 0;analysisTasks!=null && i < analysisTasks.length; i++){
				if (analysisTasks[i].getName().equals(parameterEstimationName)){
					found = true;
					continue;
				}
			}
		}

		String newParameterEstimationName = null;
		try {
			newParameterEstimationName = DialogUtils.showInputDialog0(this,"name for new parameter estimation set",parameterEstimationName);
		} catch (UtilCancelException ex) {
			// user canceled; it's ok
		}

		if (newParameterEstimationName != null) {
			if (newParameterEstimationName.length() == 0) {
				PopupGenerator.showErrorDialog(this,"Error:\n name for new parameter estimation can't be empty" );
			} else {
				final String finalname = newParameterEstimationName;
				AsynchClientTask task1 = new AsynchClientTask("init task", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						ParameterEstimationTask newParameterEstimationTask = new ParameterEstimationTask(getsimulationContext());
						newParameterEstimationTask.setName(finalname);
						hashTable.put("newParameterEstimationTask", newParameterEstimationTask);
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("add task", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						ParameterEstimationTask newParameterEstimationTask = (ParameterEstimationTask)hashTable.get("newParameterEstimationTask");
						getsimulationContext().addAnalysisTask(newParameterEstimationTask);
						getAnalysisTaskComboBoxModel().setSelectedItem(newParameterEstimationTask);
					}
				};
				
				ClientTaskDispatcher.dispatch(ApplicationEditor.this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
			}
		}
		refreshAnalysisTaskEnables();
	}catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this,e.getMessage());
	}
}


private void refireActionPerformed(ActionEvent e) {
	// relays an action event with this as the source
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


/**
 * Insert the method's description here.
 * Creation date: (12/19/2005 5:32:20 PM)
 */
private void refreshAnalysisTaskEnables() {
	getAnalysisTaskComboBox().setEnabled(getAnalysisTaskComboBoxModel().getSize() > 0);
	getDeleteAnalysisTaskButton().setEnabled(getAnalysisTaskComboBoxModel().getSize() > 0);
}


public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 2:27:03 PM)
 * @param simOwner cbit.vcell.document.SimulationOwner
 */
void resetSimContext(SimulationContext simContext) {
	/* most likely we got the same thing back (e.g. during document reset after save), so keep current selection in simulation panel */
	// check whether it looks like same old simcontext; if so, save current selection list
	int[] selections = null;
	if (getSimulationWorkspace().getSimulationOwner() != null && simContext != null) {
		Simulation[] oldValue = getSimulationWorkspace().getSimulationOwner().getSimulations();
		Simulation[] simulations = simContext.getSimulations();
		if (oldValue != null && simulations != null && oldValue.length == simulations.length) {
			boolean sameNames = true;
			for (int i = 0; i < oldValue.length; i++){
				if(!oldValue[i].getName().equals(simulations[i].getName())) {
					sameNames = false;
					break;
				}
			}
			if (sameNames) {
				selections = getSimulationListPanel().getSelectedRows();
			}
		}
	}
	// reset the thing
	getSimulationWorkspace().setSimulationOwner(simContext);
	// now set the selection back if appropriate
	if (selections != null) {
		getSimulationListPanel().setSelectedRows(selections);
	}
}

/**
 * Set the cardLayout to a new value.
 * @param newValue java.awt.CardLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setcardLayout(java.awt.CardLayout newValue) {
	if (ivjcardLayout != newValue) {
		try {
			ivjcardLayout = newValue;
			connPtoP7SetSource();
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
 * Set the geometryContext to a new value.
 * @param newValue cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryContext(GeometryContext newValue) {
	if (ivjgeometryContext != newValue) {
		try {
			ivjgeometryContext = newValue;
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
 * Set the mathDescription to a new value.
 * @param newValue cbit.vcell.math.MathDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmathDescription(MathDescription newValue) {
	if (ivjmathDescription != newValue) {
		try {
			ivjmathDescription = newValue;
			connEtoC7(ivjmathDescription);
			connEtoM8(ivjmathDescription);
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
 * Set the model1 to a new value.
 * @param newValue javax.swing.ComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodel1(javax.swing.ComboBoxModel newValue) {
	if (ivjmodel1 != newValue) {
		try {
			ivjmodel1 = newValue;
			connPtoP8SetSource();
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
 * Method generated to support the promotion of the optimizationService attribute.
 * @param arg1 cbit.vcell.opt.solvers.OptimizationService
 */
public void setOptimizationService(OptimizationService arg1) {
	getoptTestPanel().setOptimizationService(arg1);
}


/**
 * Set the simulationContext to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext(SimulationContext newValue) {
	if (ivjsimulationContext != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjsimulationContext != null) {
				ivjsimulationContext.removePropertyChangeListener(ivjEventHandler);
			}
			ivjsimulationContext = newValue;

			/* Listen for events from the new object */
			if (ivjsimulationContext != null) {
				ivjsimulationContext.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM2(ivjsimulationContext);
			connEtoM6(ivjsimulationContext);
			connEtoM9(ivjsimulationContext);
			connEtoC11(ivjsimulationContext);
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
 * Sets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @param simulationWorkspace The new value for the property.
 * @see #getSimulationWorkspace
 */
public void setSimulationWorkspace(SimulationWorkspace simulationWorkspace) {
	SimulationWorkspace oldValue = fieldSimulationWorkspace;
	fieldSimulationWorkspace = simulationWorkspace;
	firePropertyChange("simulationWorkspace", oldValue, simulationWorkspace);
}


/**
 * Set the simulationWorkspace1 to a new value.
 * @param newValue cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationWorkspace1(SimulationWorkspace newValue) {
	if (ivjsimulationWorkspace1 != newValue) {
		try {
			SimulationWorkspace oldValue = getsimulationWorkspace1();
			/* Stop listening for events from the current object */
			if (ivjsimulationWorkspace1 != null) {
				ivjsimulationWorkspace1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjsimulationWorkspace1 = newValue;

			/* Listen for events from the new object */
			if (ivjsimulationWorkspace1 != null) {
				ivjsimulationWorkspace1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connPtoP2SetTarget();
			connPtoP4SetTarget();
			connPtoP5SetTarget();
			connEtoM3(ivjsimulationWorkspace1);
//			firePropertyChange("simulationWorkspace", oldValue, newValue);
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
 * Method generated to support the promotion of the userPreferences attribute.
 * @param arg1 cbit.vcell.client.server.UserPreferences
 */
public void setUserPreferences(UserPreferences arg1) {
	getoptTestPanel().setUserPreferences(arg1);
}


/**
 * Comment
 */
private AsynchClientTask[] updateMath() {
	final SimulationContext simContext = (SimulationContext)getSimulationWorkspace().getSimulationOwner();
	AsynchClientTask task1 = new AsynchClientTask("generating math", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Geometry geometry = simContext.getGeometry();
			if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
				geometry.getGeometrySurfaceDescription().updateAll();
			}
			// Use differnt mathmapping for different applications (stoch or non-stoch)
			simContext.checkValidity();
			
			MathMapping mathMapping = null;
			MathDescription mathDesc = null;
			if (!simContext.isStoch())
			{
				mathMapping = new MathMapping(simContext);
				mathDesc = ((MathMapping)mathMapping).getMathDescription();
			}
			else
			{
				mathMapping = new StochMathMapping(simContext);
				mathDesc = ((StochMathMapping)mathMapping).getMathDescription();
			}
			hashTable.put("mathMapping", mathMapping);
			hashTable.put("mathDesc", mathDesc);
		}			
	};
	AsynchClientTask task2 = new AsynchClientTask("formating math", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathDescription mathDesc = (MathDescription)hashTable.get("mathDesc");
			if (mathDesc != null) {
				simContext.setMathDescription(mathDesc);
			}
		}
	};
	AsynchClientTask task3 = new AsynchClientTask("showing issues", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathMapping mathMapping = (MathMapping)hashTable.get("mathMapping");
			MathDescription mathDesc = (MathDescription)hashTable.get("mathDesc");
			if (mathDesc != null) {
				//
				// inform user if any issues
				//					
				Issue issues[] = mathMapping.getIssues();
				if (issues!=null && issues.length>0){
					StringBuffer messageBuffer = new StringBuffer("Issues encountered during Math Generation:\n");
					int issueCount=0;
					for (int i = 0; i < issues.length; i++){
						if (issues[i].getSeverity()==Issue.SEVERITY_ERROR || issues[i].getSeverity()==Issue.SEVERITY_WARNING){
							messageBuffer.append(issues[i].getCategory()+" "+issues[i].getSeverityName()+" : "+issues[i].getMessage()+"\n");
							issueCount++;
						}
					}
					if (issueCount>0){
						PopupGenerator.showWarningDialog(ApplicationEditor.this,messageBuffer.toString(),new String[] { "Ok" }, "Ok");
					}
				}
			}
		}
	};
	return new AsynchClientTask[] { task1, task2, task3};
}

private void viewMath_ItemStateChanged(ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
		if (itemEvent.getSource() == getViewEqunsRadioButton()) {
			JRadioButton source = (JRadioButton)getViewEqunsRadioButton();
			getcardLayout().show(getMathViewerPanel(), source.getActionCommand());
			return;
		}else if (itemEvent.getSource() == getViewVCMDLRadioButton()) {
			JRadioButton source = (JRadioButton)getViewVCMDLRadioButton();
			getcardLayout().show(getMathViewerPanel(), source.getActionCommand());
			return;			
		}
	}
}


public void setTabIndex(int tabIndex) {
	getJTabbedPane1().setSelectedIndex(tabIndex);	
}

private void refreshSPPRTab() {
	getSPPRPanel().setSimulationContext((SimulationContext)getSimulationWorkspace().getSimulationOwner());
}

}