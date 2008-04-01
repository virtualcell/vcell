package cbit.vcell.client.desktop.biomodel;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import cbit.vcell.document.*;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 3:16:22 PM)
 * @author: Ion Moraru
 */
public class ApplicationEditor extends JPanel {
	public static final int TAB_IDX_ANALYSIS = 6;
	private boolean ivjConnPtoP1Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.client.desktop.simulation.SimulationWorkspace fieldSimulationWorkspace = null;
    protected transient ActionListener actionListener = null;
	private cbit.vcell.client.desktop.simulation.SimulationWorkspace ivjsimulationWorkspace1 = null;
	private cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel ivjElectricalMembraneMappingPanel = null;
	private cbit.vcell.mapping.gui.InitialConditionsPanel ivjInitialConditionsPanel = null;
	private JTabbedPane ivjJTabbedPane1 = null;
	private cbit.vcell.mapping.gui.ReactionSpecsPanel ivjReactionSpecsPanel = null;
	private cbit.vcell.client.desktop.simulation.SimulationListPanel ivjSimulationListPanel = null;
	private cbit.vcell.mapping.gui.StructureMappingCartoonPanel ivjStaticCartoonPanel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private boolean ivjConnPtoP6Aligning = false;
	private JPanel ivjSimulationContainerPanel = null;
	private SimulationContext ivjsimulationContext = null;
	private GeometryContext ivjgeometryContext = null;
	private JPanel ivjJPanel3 = null;
	private JPanel ivjStructureMappingPanel = null;
	private JButton ivjViewModifyGeometryButton = null;
	private JRadioButton ivjViewEqunsRadioButton = null;
	private JPanel ivjViewMathPanel = null;
	private JRadioButton ivjViewVCMDLRadioButton = null;
	private cbit.vcell.math.gui.MathDescPanel ivjMathDescPanel1 = null;
	private JPanel ivjMathViewerPanel = null;
	private JEditorPane ivjVCMLEditorPane = null;
	private JPanel ivjVCMLPanel = null;
	private JScrollPane ivjVCMLScrollPane = null;
	private cbit.vcell.math.MathDescription ivjmathDescription = null;
	private CardLayout ivjcardLayout = null;
	private ButtonGroup ivjbuttonGroup = null;
	private JPanel ivjButtonsPanel = null;
	private JButton ivjCreateMathModelButton = null;
	private JButton ivjRefreshMathButton = null;
	private JPanel ivjJPanel2 = null;
	private cbit.vcell.modelopt.gui.OptTestPanel ivjoptTestPanel = null;
	private JPanel ivjParameterEstimationPanel = null;
	private JComboBox ivjAnalysisTaskComboBox = null;
	private JButton ivjDeleteAnalysisTaskButton = null;
	private JButton ivjNewAnalysisTaskButton = null;
	private cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel ivjAnalysisTaskComboBoxModel = null;
	private boolean ivjConnPtoP8Aligning = false;
	private ComboBoxModel ivjmodel1 = null;
	private JButton ivjCopyButton = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ApplicationEditor.this.getViewModifyGeometryButton()) 
				connEtoC3(e);
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
			if (evt.getSource() == ApplicationEditor.this && (evt.getPropertyName().equals("simulationWorkspace"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getSimulationListPanel() && (evt.getPropertyName().equals("simulationWorkspace"))) 
				connPtoP2SetSource();
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getStaticCartoonPanel() && (evt.getPropertyName().equals("simulationContext")))
			{	
				connPtoP3SetSource();
				refreshAnalysisTab();
			}
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getInitialConditionsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
			{
				connPtoP4SetSource();
				refreshAnalysisTab();
			}
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getReactionSpecsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
			{
				connPtoP5SetSource();
				refreshAnalysisTab();
			}
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getElectricalMembraneMappingPanel() && (evt.getPropertyName().equals("simulationContext")))
			{
				connPtoP6SetSource();
				refreshAnalysisTab();
			}
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connEtoM1(evt);
			if (evt.getSource() == ApplicationEditor.this.getsimulationContext() && (evt.getPropertyName().equals("mathDescription"))) 
				connEtoM7(evt);
			if (evt.getSource() == ApplicationEditor.this.getAnalysisTaskComboBox() && (evt.getPropertyName().equals("model"))) 
				connPtoP8SetTarget();
		};
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
	getoptTestPanel().setParameterEstimationTask((cbit.vcell.modelopt.ParameterEstimationTask)getAnalysisTaskComboBoxModel().getSelectedItem());
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
private void connEtoC11(cbit.vcell.mapping.SimulationContext value) {
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
		this.updateMath();
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
private void connEtoC7(cbit.vcell.math.MathDescription value) {
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
		setsimulationContext((cbit.vcell.mapping.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
private void connEtoM2(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM3(cbit.vcell.client.desktop.simulation.SimulationWorkspace value) {
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
private void connEtoM6(cbit.vcell.mapping.SimulationContext value) {
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
private void connEtoM8(cbit.vcell.math.MathDescription value) {
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
private void connEtoM9(cbit.vcell.mapping.SimulationContext value) {
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
 * connPtoP3SetSource:  (simulationWorkspace1.simulationOwner <--> StaticCartoonPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getsimulationWorkspace1().setSimulationOwner(getStaticCartoonPanel().getSimulationContext());
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
 * connPtoP3SetTarget:  (simulationWorkspace1.simulationOwner <--> StaticCartoonPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getStaticCartoonPanel().setSimulationContext((cbit.vcell.mapping.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
				getInitialConditionsPanel().setSimulationContext((cbit.vcell.mapping.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
				getReactionSpecsPanel().setSimulationContext((cbit.vcell.mapping.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
				getsimulationWorkspace1().setSimulationOwner(getElectricalMembraneMappingPanel().getSimulationContext());
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
 * connPtoP6SetTarget:  (simulationWorkspace1.simulationOwner <--> ElectricalMembraneMappingPanel.simulationContext)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getsimulationWorkspace1() != null)) {
				getElectricalMembraneMappingPanel().setSimulationContext((cbit.vcell.mapping.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
		cbit.vcell.modelopt.AnalysisTask taskToCopy = (cbit.vcell.modelopt.AnalysisTask) getAnalysisTaskComboBoxModel().getSelectedItem();
		if (getSimulationContext() != null && taskToCopy != null){
			cbit.vcell.modelopt.AnalysisTask newAnalysisTask = getsimulationContext().copyAnalysisTask(taskToCopy);
			getAnalysisTaskComboBox().setSelectedItem(newAnalysisTask);
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog(this,e.getMessage());
	}
}


private void createMathModel(ActionEvent e) {
	// relays an action event with this as the source
	if (updateMath())
		refireActionPerformed(e);
}


/**
 * Comment
 */
private void deleteAnalysisTaskButton_ActionPerformed() throws java.beans.PropertyVetoException {
	cbit.vcell.modelopt.AnalysisTask taskToDelete = (cbit.vcell.modelopt.AnalysisTask) getAnalysisTaskComboBoxModel().getSelectedItem();
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
			ivjAnalysisTaskComboBox.setRenderer(new cbit.vcell.client.desktop.biomodel.AnalysisTaskListCellRenderer());
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
private cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel getAnalysisTaskComboBoxModel() {
	if (ivjAnalysisTaskComboBoxModel == null) {
		try {
			ivjAnalysisTaskComboBoxModel = new cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel();
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
			getButtonsPanel().add(getViewEqunsRadioButton(), getViewEqunsRadioButton().getName());
			getButtonsPanel().add(getViewVCMDLRadioButton(), getViewVCMDLRadioButton().getName());
			getButtonsPanel().add(getCreateMathModelButton(), getCreateMathModelButton().getName());
			getButtonsPanel().add(getRefreshMathButton(), getRefreshMathButton().getName());
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
 * Return the ElectricalMembraneMappingPanel property value.
 * @return cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel getElectricalMembraneMappingPanel() {
	if (ivjElectricalMembraneMappingPanel == null) {
		try {
			ivjElectricalMembraneMappingPanel = new cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel();
			ivjElectricalMembraneMappingPanel.setName("ElectricalMembraneMappingPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjElectricalMembraneMappingPanel;
}


/**
 * Return the geometryContext property value.
 * @return cbit.vcell.mapping.GeometryContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.GeometryContext getgeometryContext() {
	// user code begin {1}
	// user code end
	return ivjgeometryContext;
}


/**
 * Comment
 */
private cbit.vcell.mapping.GeometryContext getGeometryContext() {
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
private cbit.vcell.mapping.gui.InitialConditionsPanel getInitialConditionsPanel() {
	if (ivjInitialConditionsPanel == null) {
		try {
			ivjInitialConditionsPanel = new cbit.vcell.mapping.gui.InitialConditionsPanel();
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
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.FlowLayout());
			getJPanel3().add(getViewModifyGeometryButton(), getViewModifyGeometryButton().getName());
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
			ivjJTabbedPane1.insertTab("StructureMapping", null, getStructureMappingPanel(), null, 0);
			ivjJTabbedPane1.insertTab("Initial Conditions", null, getInitialConditionsPanel(), null, 1);
			ivjJTabbedPane1.insertTab("Reaction Mapping", null, getReactionSpecsPanel(), null, 2);
			ivjJTabbedPane1.insertTab("Electrical Mapping", null, getElectricalMembraneMappingPanel(), null, 3);
			ivjJTabbedPane1.insertTab("View Math", null, getViewMathPanel(), null, 4);
			ivjJTabbedPane1.insertTab("Simulation", null, getSimulationContainerPanel(), null, 5);
			ivjJTabbedPane1.insertTab("Analysis", null, getParameterEstimationPanel(), null, 6);
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
private cbit.vcell.math.gui.MathDescPanel getMathDescPanel1() {
	if (ivjMathDescPanel1 == null) {
		try {
			ivjMathDescPanel1 = new cbit.vcell.math.gui.MathDescPanel();
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
private cbit.vcell.math.MathDescription getmathDescription() {
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
public cbit.vcell.opt.solvers.OptimizationService getOptimizationService() {
	return getoptTestPanel().getOptimizationService();
}


/**
 * Return the optTestPanel property value.
 * @return cbit.vcell.modelopt.gui.OptTestPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelopt.gui.OptTestPanel getoptTestPanel() {
	if (ivjoptTestPanel == null) {
		try {
			ivjoptTestPanel = new cbit.vcell.modelopt.gui.OptTestPanel();
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
private cbit.vcell.mapping.gui.ReactionSpecsPanel getReactionSpecsPanel() {
	if (ivjReactionSpecsPanel == null) {
		try {
			ivjReactionSpecsPanel = new cbit.vcell.mapping.gui.ReactionSpecsPanel();
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
 * Return the SimulationContainerPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSimulationContainerPanel() {
	if (ivjSimulationContainerPanel == null) {
		try {
			ivjSimulationContainerPanel = new javax.swing.JPanel();
			ivjSimulationContainerPanel.setName("SimulationContainerPanel");
			ivjSimulationContainerPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjSimulationContainerPanel.setLayout(new java.awt.BorderLayout());
			ivjSimulationContainerPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			getSimulationContainerPanel().add(getSimulationListPanel(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimulationContainerPanel;
}

/**
 * Return the simulationContext property value.
 * @return cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.SimulationContext getsimulationContext() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext;
}


/**
 * Comment
 */
private cbit.vcell.mapping.SimulationContext getSimulationContext() {
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
private cbit.vcell.client.desktop.simulation.SimulationListPanel getSimulationListPanel() {
	if (ivjSimulationListPanel == null) {
		try {
			ivjSimulationListPanel = new cbit.vcell.client.desktop.simulation.SimulationListPanel();
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
 * Gets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @return The simulationWorkspace property value.
 * @see #setSimulationWorkspace
 */
public cbit.vcell.client.desktop.simulation.SimulationWorkspace getSimulationWorkspace() {
	return fieldSimulationWorkspace;
}


/**
 * Return the simulationWorkspace1 property value.
 * @return cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.client.desktop.simulation.SimulationWorkspace getsimulationWorkspace1() {
	// user code begin {1}
	// user code end
	return ivjsimulationWorkspace1;
}


/**
 * Return the StaticCartoonPanel property value.
 * @return cbit.vcell.mapping.gui.StructureMappingCartoonPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.gui.StructureMappingCartoonPanel getStaticCartoonPanel() {
	if (ivjStaticCartoonPanel == null) {
		try {
			ivjStaticCartoonPanel = new cbit.vcell.mapping.gui.StructureMappingCartoonPanel();
			ivjStaticCartoonPanel.setName("StaticCartoonPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStaticCartoonPanel;
}


/**
 * Return the StructureMappingPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getStructureMappingPanel() {
	if (ivjStructureMappingPanel == null) {
		try {
			ivjStructureMappingPanel = new javax.swing.JPanel();
			ivjStructureMappingPanel.setName("StructureMappingPanel");
			ivjStructureMappingPanel.setLayout(new java.awt.BorderLayout());
			getStructureMappingPanel().add(getStaticCartoonPanel(), "Center");
			getStructureMappingPanel().add(getJPanel3(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStructureMappingPanel;
}

/**
 * Method generated to support the promotion of the userPreferences attribute.
 * @return cbit.vcell.client.server.UserPreferences
 */
public cbit.vcell.client.server.UserPreferences getUserPreferences() {
	return getoptTestPanel().getUserPreferences();
}


/**
 * Return the VCMLEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getVCMLEditorPane() {
	if (ivjVCMLEditorPane == null) {
		try {
			ivjVCMLEditorPane = new javax.swing.JEditorPane();
			ivjVCMLEditorPane.setName("VCMLEditorPane");
			ivjVCMLEditorPane.setBounds(0, 0, 160, 120);
			ivjVCMLEditorPane.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
			getVCMLPanel().add(getVCMLScrollPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVCMLPanel;
}


/**
 * Return the VCMLScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getVCMLScrollPane() {
	if (ivjVCMLScrollPane == null) {
		try {
			ivjVCMLScrollPane = new javax.swing.JScrollPane();
			ivjVCMLScrollPane.setName("VCMLScrollPane");
			getVCMLScrollPane().setViewportView(getVCMLEditorPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVCMLScrollPane;
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
			ivjViewVCMDLRadioButton.setText("View Model Description Language");
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
	getStaticCartoonPanel().addPropertyChangeListener(ivjEventHandler);
	getInitialConditionsPanel().addPropertyChangeListener(ivjEventHandler);
	getReactionSpecsPanel().addPropertyChangeListener(ivjEventHandler);
	getElectricalMembraneMappingPanel().addPropertyChangeListener(ivjEventHandler);
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
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP6SetTarget();
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
	cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel analysisTaskCBModel = new cbit.vcell.modelopt.gui.AnalysisTaskComboBoxModel();
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
private void mathDescription_This() {
	if (getmathDescription()!=null){
		try {
			final int value = getVCMLScrollPane().getVerticalScrollBar().getValue();
			getVCMLEditorPane().setText(getmathDescription().getVCML_database());
			SwingUtilities.invokeLater( new Runnable()
				{
					public void run()
					{
						getVCMLScrollPane().getVerticalScrollBar().setValue(value);
					}
				});			
			
		}catch (Exception e){
			e.printStackTrace(System.out);
			getVCMLEditorPane().setText("error displaying math language: "+e.getMessage());
		}
	}else{
		getVCMLEditorPane().setText("");
	}
	return;
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

		cbit.vcell.modelopt.AnalysisTask analysisTasks[] = getSimulationContext().getAnalysisTasks();
		boolean found = true;
		while (found) {
			found = false;
			parameterEstimationName = cbit.util.TokenMangler.getNextEnumeratedToken(parameterEstimationName);
			for (int i = 0;analysisTasks!=null && i < analysisTasks.length; i++){
				if (analysisTasks[i].getName().equals(parameterEstimationName)){
					found = true;
					continue;
				}
			}
		}

		String newParameterEstimationName = null;
		try {
			newParameterEstimationName = cbit.gui.DialogUtils.showInputDialog0(this,"name for new parameter estimation set",parameterEstimationName);
		} catch (cbit.gui.UtilCancelException ex) {
			// user canceled; it's ok
		}

		if (newParameterEstimationName != null) {
			if (newParameterEstimationName.length() == 0) {
				cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error:\n name for new parameter estimation can't be empty" );
			} else {
				cbit.vcell.modelopt.AnalysisTask[] newModelOptSpecs = null;
				cbit.vcell.modelopt.ParameterEstimationTask newParameterEstimationTask = new cbit.vcell.modelopt.ParameterEstimationTask(getsimulationContext());
				newParameterEstimationTask.setName(newParameterEstimationName);
				getsimulationContext().addAnalysisTask(newParameterEstimationTask);
				getAnalysisTaskComboBoxModel().setSelectedItem(newParameterEstimationTask);
			}
		}
		refreshAnalysisTaskEnables();
	}catch (Exception e){
		e.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog(this,e.getMessage());
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
private void setgeometryContext(cbit.vcell.mapping.GeometryContext newValue) {
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
private void setmathDescription(cbit.vcell.math.MathDescription newValue) {
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
public void setOptimizationService(cbit.vcell.opt.solvers.OptimizationService arg1) {
	getoptTestPanel().setOptimizationService(arg1);
}


/**
 * Set the simulationContext to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext(cbit.vcell.mapping.SimulationContext newValue) {
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
public void setSimulationWorkspace(cbit.vcell.client.desktop.simulation.SimulationWorkspace simulationWorkspace) {
	cbit.vcell.client.desktop.simulation.SimulationWorkspace oldValue = fieldSimulationWorkspace;
	fieldSimulationWorkspace = simulationWorkspace;
	firePropertyChange("simulationWorkspace", oldValue, simulationWorkspace);
}


/**
 * Set the simulationWorkspace1 to a new value.
 * @param newValue cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationWorkspace1(cbit.vcell.client.desktop.simulation.SimulationWorkspace newValue) {
	if (ivjsimulationWorkspace1 != newValue) {
		try {
			cbit.vcell.client.desktop.simulation.SimulationWorkspace oldValue = getsimulationWorkspace1();
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
			connPtoP3SetTarget();
			connPtoP4SetTarget();
			connPtoP5SetTarget();
			connPtoP6SetTarget();
			connEtoM3(ivjsimulationWorkspace1);
			firePropertyChange("simulationWorkspace", oldValue, newValue);
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
public void setUserPreferences(cbit.vcell.client.server.UserPreferences arg1) {
	getoptTestPanel().setUserPreferences(arg1);
}


/**
 * Comment
 */
private boolean updateMath() {
	try {
		SimulationContext simContext = (SimulationContext)getSimulationWorkspace().getSimulationOwner();
		cbit.vcell.geometry.Geometry geometry = simContext.getGeometry();
		if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
			geometry.getGeometrySurfaceDescription().updateAll();
		}
		// Use differnt mathmapping for different applications (stoch or non-stoch)
		simContext.checkValidity();
		
		MathMapping mathMapping = null;
		cbit.vcell.math.MathDescription mathDesc = null;
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
		simContext.setMathDescription(mathDesc);
		//
		// inform user if any issues
		//
		cbit.util.Issue issues[] = mathMapping.getIssues();
		if (issues!=null && issues.length>0){
			StringBuffer messageBuffer = new StringBuffer("Issues encountered during Math Generation:\n");
			
			for (int i = 0; i < issues.length; i++){
				messageBuffer.append(issues[i].getCategory()+" "+issues[i].getSeverityName()+" : "+issues[i].getMessage()+"\n");
			}
			cbit.vcell.client.PopupGenerator.showWarningDialog(this,messageBuffer.toString(),new String[] { "Ok" }, "Ok");
		}
		return true;
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		cbit.vcell.client.PopupGenerator.showErrorDialog(this, "Failed to generate new Math:\n"+exc.getMessage());
		return false;
	}
}


/**
 * Comment
 */
private void viewMath_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
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

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G44DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FD8D4D596381515ED5452AE9639D4D4D4D4D4D434D159AE3538CBE59B1915919695959535EED9CBC53B0F7F0C022222E2E1D222E1920222225186820A8AC8CA7EA9CB86668923434CB873108131FE675E7B4E1DB7EF5EB0B81BFD3F6F7B7D783E43FB775CF36FBD675CF36FBD773EFB6F88054B023352528B85A1ADCB107FB62689C2652D029075462CBE4425FC93279394FF1FG4C937A3F4E04E7
	6E6EB8DDFB7A533A822132B797424590767FA0CE37985E9788BD853DB7420B07F26AA06E326FEF78E864F2CA66E2B9B9B45CDE1D874FB9GA500A3CF93BC8B045E6AA2995FA8639152528521E38F21B92B3AD8460583CD8CC9877852BD0C3E961E0C0133B75748F487AF8A95E6BEE8CB4FA779A872C4881253C6494F92613D345E8ED2D698651324C90138CA409161B3048DBBB613325A0F767C373B4BA8D59C6F96CD260AEE13D1B4CB958651FED432D8AB3A0C16BE0BC1B4ED5EDCEF351A0C5DFA49E8B1B7990C
	1245A68809B47F195539B4BFC188033075DB022B4BA5E51641FB9D407EEF91FF718A020F007749G095F221CDFFDD6AB4B317F441D42709D6BE74F75A0E76A37CC3E523DCEB9A77EA1BEFD892D134E82227BCE089B8B9088B091A09EA09560A3E68BF3BF07E7FD1754E0696B3318C12EADA6032D15C8896FA0E817A463F60B5D961BC190D8FD5FD6DD2E414FC84052C51DF2BA261350F5D877E13BEF9662EF3FB8BDC7C3A77121A7528B77B1DD146FF36A04157D2E4732DB76395A26EB597B84E5595543D3B332B4
	4AEEB7CE4D6814731194E5C76FA3F59DCFEDA0G5E53285E8501615F20785C7DCEBC33532D94CF42B590B7F5BF5A467EB948CBCB66D5C2C155A51AFA883C5FAF5DFA0071B06E001A17891FD13BBB404A5C45F9F910628FFE6744B3DE02281D12F06677F13AB1BF446918FD996713F61BFAA8CED75489ED8920992097E0ACC0E0A75AD8785CB5233431F63DCD5A52EFB499C41B40529A8B9641D3F4C8B67DF633DD521B3B453DFA1BD15FE5926D02CCE3ABA0BA68F2C9B7C0ED3F9B4A58A61AC59BED29103B3ECF14
	C41B1D510CA918C7E8ECA20D6DB07609D0C01FD540FC4795907D1B74F669DB2BC1AF09CE8A966FDFC06A6438FEAF64889940BB534B458B083EA2C17ED800842E075973C8FEBBC51B19F4A1AD964B51FEEB8711C4285493BD972CA7FD47CC204F53230EFFD7C8F092047D74184F16F9739579B49A3B897BFA5BA0363167F3CB61998C74D1G89GC2D71C2E98209A209D2097403F8BED6A0D7DE8D30D79B789650F0F573429E8306F10AEE6CB933B54EDEBA6AD13701D88F1195D50FF83B48298G468204GC4F763
	F851126B2F48BBCA4123377E20DAC5C364D35334FB4847DF05A7BA6AD56098EFF6975E3264FEE036973E88642B42B99AB4F3BD24DD208AE7D0DE1CE1E68F42A2E2DB0920C7419027ABB120CD1C1CD7425A58963DDD44D6354B868DC2B4B8AD0449977CD910AC53D200AA00A6D93EFC193F05F29BB8G30CCE0E37EEA00AFGC8DBA6CE48BA0075448E813683EC84788CE095408A017597A2408FG07G3EA67C81B4G9C83789260AB00EDG5F831C8D508A20677590CB792C8199BB8146810483CC839044B8DD99
	C0BB0083A098A086E086C051E198E781FA817C81A28192GF2FB200F82588FB086A094A096A0852024977AAEGABC0A0C094C092C001B1CED7835089B08EA09CE09A00F0844ABF0236FC558AEB3A76582154A96297AA5EBF126BGF5BCC7569D6AD12973AA054EB74A3428C325EE1BE41DEE176B26D52E8BB5FF87E43DE37D68E53C28226B131F47BC582D9A4E9EA55D7F077F8D186D552F5432BD52F7E8591E32BFA17D8D695B481CE0090CA3B606762634CDD256F2015966EA1996EDCDE9034466096D915BA5B6
	CCECEF1D866F248DE01F31C9F05A67FA959D36097AD16AE4677F8575727F8230B147BE1F5816F89C26C80D22D5B49B6045AE478D4DA736A07BED07ADBC4D980A37021F57G0E151D634F19CFECD3AFC9B6E3D73FA46E91A549E86EF146DFC84B3277F7D91DAEA216B5FEBE31E34BG78F91CFE42FC62A3F5DB4CE6118EFEBC099C9FC4FDB451A47681538D16FE334470D7ADA0735A2362206CA3DE3BA0DBA0BE1E291F781BEC2E982EA7ED4BEAB3764110CADCD6A17888715FBEDBDF2450D1BB60481CD7026738A3
	68EB8DACC7DF2BF468FAA16A10B5DDEBAE91F84DEF53130EB03FA475085357E2BCDDD2284D7BE554D71471F646EB48C222D70D69AA6DA3968833F696923D9A7B7ACDB4FA1F45F654EE55F70BD5AC3EEE45BC1778860BD902DA6569ABBFA67552A3DA4031368DE2AC0B3B7F905193D427E4B1EF33D97A2DB2FEE0AFC9532717FA9BC5FB374DE8A5994BF1E7AD1DC56ACDEFB3346887AD7D9216F373BA52C7551B7526C13B515E21379F0549C417E50B45C167911C1F24D9243E68AC3D8AFD26CD0B093E0A0F42788F
	50FC94FD26A58BC9BC7A51323354E0B1191849C05C382324DE3F56954A755289E147D1D62FF3F87C0BBCBE9642C9EE715F4F427892937893A6E7FC2009445F329C638918CC6A74CF73788408CBF10B7F2694634B7BA02ECF1D7F5FCADD726FD327FF0B4707C3DC0CA21E69A9FBD181318171D83F8853AFB2E159495BC77E22792A383DF2DBE1718B97919BB0A966BA7EE65AD64AF04DE79A14B78320404C4AEDB74BEB9A96C2779A270B1763E71A599C239062BF66F4F72CC2BA2B4599BF8EA04C056E2C4F11EE32
	CC1788CF418A73952B12EE7FEA24EB34BA698E427B98973A1565C8972C200B0477F8973A191CAED5C1D7F88C6C6198238BBAC6686E7F8269769FE3F456E3ECBE65F70C6905517D3E82691264F8414666DC64C970E3EC046E07B5C8D7E0E33EF2190D693156264C6FBF1CEE320D15CB7249F44BEFD2A552A55A184F0D79253A6457D34575E7E7799C343339A0F9D2FD5989DD55DA24B359196FDEA617BB964295BC3FE4CE17ECF7463B67F75BBA244B14589CC01D9F333F43FC4D31D8F266C364281614F2FC381E57
	07024E8A6F8112325C17B9DD3844669B24DC242F60F670078DC897C368GB27B597C1850B96D403485690A7A99DF157DECBE5D5C2F64EFE58352B9BC50B17E62B81DFF3FBC7F01E7E43F13BFC69752485BE5BF1BAB4D0027F05CC94774E7EEC23A1263AC1F1A6372F8F71C154B682A36A2DD64F1160FB65D0B1CAE9670C9G39830C2EF9C0C9F77DB624B3GFEF840C99764C277C3B3178370B3G8A9CF27F62D052FD329D694682BE88E022CC176AC277A2272B9D0C53F58CBA6924C1A55D25BB10AEEC1045C74B
	4F78C1E53D6D59496DF40859E77190DBA3A99FD2565B0A9624EB99E2F3D134E7C26F342B27B81DE10849BBBC4456DB7C0714F6F5F5AB6F2F0698DF118ABAE73916B6DE9FC3EC5ECB4A8D11691CF630FC976F07E4B90493AC5F0293CA3DBC4D69EA815F81A049F491B29D73D75EDAC5FCA83A8EFFC4FFDC6F28308F009F38F9730EF6BD0C52EC0D7DA23ACEC66813CF60DA7A3DB5E40CAF9D06FE8D20E3987D2DC773F1ADBD60E828D0F072318CCF5F8CCC43B23F43CEFFAB313DBF037990E79511B2E3A1AEE1987D
	877FC54A753CFA3B5D52ED24CE12BC76FD3DD8690FB1DC63E2625F9CB65A6C52F6F0189DB8CE6ED8CC78A5EB0DB2DADEFBBD2C4B9318EFB3934AADBF89FD4AC9DA0FBA622712311049593D8E65749F3AC6689CBFA1FD3687B9C3CFB279E2CEBA659CFB5E3A8CD6DE5EAC7CBEA27C84767693FE9F39297BF3417D7BC81F5EEA255533475DC1E479F5AE477C2C10D7A84F2F55D0B6D2FE5BDC9DCA1666030A251A3631C561E1621A7D492D4AFA1FB87724C7FDA47C4474A07C6C5407A177EDD99F692548FF9D44BBFE
	C67E6FECDD21493F543BF9F32B4A33C5DDB8F8DE6110CF924FAB7723A5CA39AA748352664D8D8A97D8EE9B621C0DDAE592DF57E215AAFA7A0DE0059EBDE4464307AB501ECA46C07FBE866DE95F8AE2CF244FE0F46DE5F9F2397EC0938340FC49148C25AFA91C817D82C08BG733F6FACDB8BCFBDF520AB5823DD349D36587AC4C399CAD7C1670C9575B43A093C2FC2993F99280627CD0496A2562B1237F8CE7F652A3D2C5E2FDAD0A773B679662B045A1FFFCC577AF6B6EEEEEFFA6799328FFC06335E43532727C7
	83B09D942EC2DDCD851A3CB3D1463BF4245DEB4C1964B948952B08AE25DE231D7174E63D1227489CBFCD1EFA2F8BCCEFBCD37EF6FA26132720F953534367A1CF4FF01EA6824DCCGB6778C4BD44EBD4B4F82DB85888288BF8B79B6FE6E09EF795B686718F78C249146E23A852B673A245BC5E75A2CDC5BCF4AF2EBA04D98009000C81E7E3F2BBD4DA1D939E72C46F2E3A1CD6E59A85373AEB25581FE8CC088C0645918F7DE7948F96FAA67F9C31A5CF3B06FC732DD72BE0765BD8CCF7FF3B06FB8AFF9AF7D82738E
	05B489BC6FDBF315F9971D4B726E0427F4AE669D7A4548F93F768536BFBFC8934D536D2E502A8B96F73242556EEE7AD8E9F7397A33B56D4E6F151E7454F365B957B8275D95E5CCCFAF4FC03B1BD801321A00A6E49C7233FC8D1649684ED903FC4783CD61F95847957E17BC69BA8A6F4F2EB8C656C2FAC4C9555B172DEAF19F93AAD4C4DFB2995FDF0AB216B60688B5B30FEA4AC87D277364EF3367B9E54CDCD21DD18440F8AF2BC4992301A675BC6C8B5F28C4192AFF9327B37D86E53AD028E57C3B0DFE5A923811
	FD4972772F42F2C3A06F785F20EE7FD5652A5B6F56A2DD617950671E0FF40F2D55DACFE17A99BE007A99B3B9C2A8DAF001C77D0CBD1F69A56CFC057FD3B0BDBD25GEDE061BAACBF95E82A7DD0BFCFF33C89F0A19C7F5BEAEE8B00AB3CG7516F7E431BB3F07BAC04E32D5FBE57BEFD80FF936C03A3197E05B4A1514EDEB6285EC8EB5831E199722BE7E3EDECB9FAC5F028D18EFB150F7DC083CC6F33C9F6022AF44723EBA26DE53CB0538125F027C3F4572D6EFA4EBCEA6F930E6790D5F4279873A311CB615620D
	E623E4541B0CC3E259AA2613CA732770549B64D1CE76EDAA6A3B6C128D06D1666240A8477B3586BE8EC05EB3FF0BF2E4B4A23E5C9F46F6FFAC77C8A331A53D2D270A0D63671B1463F8905045GA47BE3BE8F73FC040B408F3C887329EEF24DE725D9AB9F6BC5184FA5CDE883E381B71167736FA6720D41A809FD95645F9EC9AF098D3DFAF30F3B1B309D62E5A701696A0B0FB6103E44A88E90CF6B50F6F5FA351F40B3D0686F3B2D4857CC60297CF7486F1CED08B7GAE68F768776C332A7D9EA267545F21BC7D5B
	7EB73F6750A7E4DE52CF77999029C8CBC0DE27F01E12203C125FA3CFFD96AD1E767F9E5B5933F349BC0D6EEF20F9D2BF500B9705E51E530CE51AA03F90DE66BF7215E54683BEB3GFC40G54436B4D7F1B9E5E7D11FCB752EBF83107546EA06FABF6ECC7DEEB018FA9GF9F5E42A75C3F88D633C5E3A7DFF63B53006FC5BB20B83FCE3C913DDB276315DA440B9EB341E791E3383790E821ED2B85F9159DAFCD70E47FAED7C18FC03033A04A2157E3E37726E6765B5C2DE4363D18F2BF6EA77C99F96FC6802F719CF
	14B9EDE5F8A776959110E7B24F77051DB8FE31706C1611FD1E36964C277462B85D410B31CD2637A2BEGF013AF467C7BD690BE7BDC567FAFE8F59DB3A7733419174469EAAF41346B5A54B2B37A40B6241700B66C926421E5976AAE91F04501184F6D3B30BC263360A6B2C705790BCD3477AAAB2749CC77ACA95BF5E6BB665981797985E27DBF1825D57F513C4C236D7F1B5D0657136F335D966B60682C873FB5DF3CDE3DFFEEDA20F36FCC32629D016DAB2B5AC46494AEE57B4AB2B87ECB0AAFD06019DE7ED24367
	2890B776D25C8B7566C7E4ACCE02F06684288300EA008EGE9822E49CCD9CC5A6408FB4D36035559C070BDAE9F93F14FD791A97728B8A8077D0A14FEAB5303C9C77460BF417BDEA58416EE08268BBD05F44C07DB751DF25FEB663C9D6904C7AD9FEE1A1CF75284278F47F4F775D2E2D3E5FF0853B5829C8410G827E00FA1B37245C1B5EF05F1D2CB0A6537CD93E6D83AD71B15D61653E2513CEB99D531BEE9951DB8468A996A089C08802B1B88875767D922F7656645034B746D7EE29EF72AC72B15DCFBE267373
	B1DD498A5F5ACA450AD3EDABAC5D5EB2B51FF5C12EF99CD405D9BA03DB3A0FD561B12AF42CAF1D3B47E56D4E39DE4D3AD308E373084F5354738832CE9288F9068130F56DB519382E9D9D04FD5E21923A479642C517018FG5082E0857083883F8C6D7006A5DFF935C3B288F4D89AG57EC3100DF255A9B41F57161AAEFF52D2D43B22F69E2AFF3CD47E4FC6293A2E39244E5DE8E7DBAC08D40C1G87C05065A86387CB36F813319EB710F4D8782B06200C5FC49F65FC53EB3A084B35EC65564A7F8ADE4F98B85DA3
	652AFA0B51AE51AFA0E8CFDDA75C6C69EC700D205C044B511E92B371ACC24A65781DE4E79699DBAB2F0853B583F4DE01FB98EF7B5CB7DDC4F9CD6738C2CB97EFD6ED5250852E1450256CBD122D3FDB6A37415C05E29B20733410DD5CD48D329E5AB575608FE506DC01FA3861A7544344ABD08F83825143CC8867854769CA03D18F672E76CD8F633D262B8D5652C3D055BAEFFA584E1CD4B0E3031158021DE9C0DEFF5D24143FA3186D9D9F834F00E0147F43AC6CD74202D17E62EC3A0F984229G05D7227C5F167B
	A6FF3757F465D7EA497F533AB55E645FADB29FF70FD56C16E5E7F2656420DC0DD722DC7F4EA1F28DC3B8GA00C4B357C8B5F642A721AAEDAD32EFE47175E64EAA27B0CEC46EE3D29D56C6B3201E03221ABE5FCA597E50C67B2A650EF0205D745694A819A2FC2997F69466B68463E7F3825D30FFD872F526A37F7AEA77D3684F17EG21G51GC9G19D747694A2E46FEFB5707C49723991B5864433D43E6FC5EDF61DB9D46FACD57FC35D6BA3FB53E69F302DB3A8AD53873EAADFDBED4CA74690038G00B0009800
	E4005C90286B905467159F56FB53A7DF11984DF8DFD1691BDEEB3D26EB8951CAB753AD5D68741ADE65CD2F864D72AEF6CBB7BA79C23C269B56AC2FF32DEF65193D268B8851EC07AB095D04C1DC8CC082C0AAC049B5F13ADA004EEB50EE5E7E502B7F24309B6F7E53C76BFC13F31557F40EEB3452BD67E3F97F701A4EDF333C71EE6785C7E727D73B25D35BE928E6F95B5D56D7C6A75FBE2F69222E519A23BE2CA0EB2DDAFB9858G057B981463D11D043E5434EBF0BCFAD4226BB857C21F8FD0F7AD0EC70BB678A6
	CF2957F47B2F55CA77200F65CD731A4E2AAA0F79DE3F1F2565FB0E835A20EB51776ABF0663F74435282FEF6CC4DF49904E3C8E66BB57213E7E3045B77E2F771A2E7ABA2DF45FB678686BF9CD5722D91E2E51377296FBCDE738CE4B3E97EDA1FE0C465CFC375EE03460841D55E50CC96BBB5FB0646BFF9D56657DA6D217E4CDAE145759E3BE4A741C57F489AA19988FBF18F365BCD2B88F17B57956CF5D6816CE6D77D506EA0EEFDBFDAB4F6C16CEDDDE33E6F9055BFCAB6FD337F4952AF0E728160EAF34E21BF504
	220E27794843CBEE6954B207EB7290E2C19EE2B88FFE4DBE7A23EE6954FA902E57EABFBDA662B7E8349F7C90A4371DC53355ED2788722B85E83F9E5B4E4AB9C49E32872F77FAFEE744F65F647951AD1DDA1E606B35F4FACBA6361DC84EC3618E9FED4BAD1D1A07991ABC3C160DFDC86E8D48C30C0FBCBC6115073A9B34521D395337BE4B5FEB3A7D1A65F53478D6DE1757F4D64D72F2DBFDABEF1157F463B44B1B62E3F989DE5385EB16F7F91BEF6505F9CD9729D95E6EDD3E59E727DBBA35FD46EA16B72B5D3772
	0A5C52294BCB3AC12BCD26E5E3DFAB5C9827E3F7B15CC4771857C0380563FE3311602C909EF7A3365D0D1B48775FB09E2E27E16287327083B57BD5BC905A116A60D4C03E6F412D0C0CDFA8FF82EC6E787EC71E4FFA2697EB1F754CAD56BE6BB927F89E43C39E4A9BBFB0FED17148E7C1BFAE56BA8B3A144A7DC4BA712DED7DE64958A752E2653D538EF21698E6383B7B4DEC7ACBCA362B3FED8AB827BE6FB3D20F51B7625CB922F82D60BEF7E607A9B1138ED1EF5BF578B01FB81385CB647946251E75F75BD2ED7D
	C5AE55565F1F160E2C3F3FAE9DD9FF7316F96625E819B6AF9FAF536625F459483C14AF73448B2B2F87964D25696DF651CE97819B7512DE0EFBD9A267445D4E7036697BC4016F77A85349B4CD1225E19DEF53DDE9582693EE44F9D7C386310D22B0E84FG4DE158C62BB3357A454EB0F93FE818FBFB156F26A9712CC7EB09369E0F1710FDDEF6CB3F2DDBD46B6AE4C9910FF3AE3A105DEBC4E4791CB4CDAF69EDBD221B2EAF2069D91C27749717F82A8F264FEFD621BE5BA5CBFB954FBB61934F323E7C09362C2FFF
	62D956191FF8177503CFBC4B1A7929F7D973BF25670F0D8623CDBE514BE47C68F3250CEB394DCCB801BE6A38B03419FFD11B090270B400995CE6DE513499045C1BBC6D0579B37D7ED6FC935B73124671B194DF2640333DB05FD0BC89D7C3DC60CD3887689597991CF6BF6E847A3829B7218C771CA4F9D45F8CE3990061E614E121B0128C43B7FB1261B57A6D4C7FE6358CB3A9BE68E6358CA78514A1946272EEC1997E34DAA9C33500730E55395A67FCF6F4683B3AC483D90E62725E308965355E02727EBCC44A8B
	05F0944034DBD05EE5D9A349BB6396D7F9B34363F4CCAEFF7A2D2CA8DCAD6F2594DF9A2E167753EC143792628242D1DE3FF2253C6B859C3F73F2C662ABA55C152F3CDB11AF9B3DA32C64D6B5DFA7A83E7CD6B5DF193948D78D44855D0AFCFD7705122F8D1C2FB7F2C762CB384D152F425B102F8EFA062C6CB6B5DFBFD0FC75EDEA3EDE52A1DFF59097F29B72D5D621685BDB1DFD51374BBD77C58717EB77C5DD4BBD77C546655E7BA25BF24FFD5160F26FFD51194BC76EF75FF511F5AD477FE54DEC2C1B43B6CBDF
	C315DC6663251EF590DF2A2D0367CBBD6B6015D26FBAF83354338E5EAD752E037495A36BC02CD46AE09D472F2EF25541DA997FF01512FE83273FE42D8BBD740E325D2C9C41EED6FA301B15A3584D4AD1584D4A916CE665A86CE625769836FB1DD2760DDC7622EAD7592BE57C64EA251F64249F33DE19CF8DEF7BCD5208635B6D1E460626FEBA3E5D2EEE7BED94DFF63B3A6DBFA971718D6282EF47365F3A41D50E75B23FE99B15FD9511030D838F4D42F515545BF19C18E7227DCD840CFBG06889C87060E29E59B
	0EF04A639F61C936C9F45DBEA4024910457131949F2940B35976D1BC89CF06384ABBD036A9DB14BA2F66F5615720442F67783AC6D7DD2C15E5967AF06C8B3883E50EBDCA4A1C8C61C40054BBD0663F19084C05FFF4152DCC8E574A4FF6C53CFCBFCC13EB59D538B6E356328F031C5EA1BF7D5C721B5A243443AAE71B5D2A44BB7B3F1D5B1478EAEE1F3F37EA159FAC179771C777BAE4794DEDD666679C032F37E86597A367936FB13F3F3864C77AA036A67A6FAC2DEFC3B3A01F4CBBF1CD34EDB64ED10A6F447A2B
	4EA7755781E1934058BB317ED64FC17E026EF47292AE3FC743F39AC0721DEAFD1F3BDD491F33FDC7E87A2FC2A4CBDFA8BF4BA255796D5C214CEF934FEFC636D6FE35F27AF6F89AG9CEE797DE747AC25BD2CC57CDDBB15EDE19327BF3F45559EB748782FDB157DC28D275735A972EF2D2AC47C63ED2E79EC1471676DF261278A696B5BDD7015A8F736265CFE321C41111E6CA6FA3D724CD849DD749C9D3FBFAA8252CD86188910FB975B53A56FA19CA0FC9F36697BE068248B8C29B46D159BB1EDAF04078102GA6
	82445F057D4E4FC708EDA93C68AAF95E789999237838AC4F2F19FCBDE9EA799A6FF64A347FEECFFD651DF45EE43ADB3D677FBE0A9F3EDB3D67FFBB455373F790B763EEDCF35BC9576CBB6E817B3987F16119388EE73D875B4795A714FC86GBE94E062BD0C3F58FBD46BFE77202FFAB413141BF20F1A4FFE0A4F1D286673834EE791448DCFC41E04AC64A9E1A272B409AEA932773FAB5E77E529576BFEC9E0E5D8FF05B2AEB821360D3CFB1DFAAE3957D5675577224E577CC4F458FC2FFA9C5DCC71876FD50F2377
	1D44F1548AF1D36FC51D178B28731ABF71BDF002D34F6FA95E4F92CE3FBE02FE05B2EE5904BAB7004C0EBF396A586FCF1E5AE34689224360BFB15D4E657885949F2340B31D1FCD6750A4BC8362CCFFC61DEF60F61E44F1A19417979527AB0942FA38B85B297BAB946FE55927DFC77527358CA673729C54C3F9946A619C0AEB0730016B41116B147D8C457B2C5C532F076527358CA6F3160E7745DC8F3D9497G61D02E076FE6B9E56FD63CA76BCE3F9E32E61DFEBD9C14D08F91DC661D7DCEB9BB946F532553AF73
	BB3FC299317C8C5734A8566FA4C7B97B9F61BE557A50FD587F1FCD67E5E577297BFFFF0A2F3DCF5D7FAFB6E17F5F8EF19177211DDDC477D6955D8FED70FE444D36E3BD345F0F757090458DC3B810631EE0F4D21CAE1C63FE302BF5C652782A23C0D5DAD646F137B27E17EFF5D91A7512550775B2D566A1C941CB2A06CCCC7F0DD44F85FFE17A57F17CB794DF2E4033FAF9024EB5C9389962427E02F5708C5DA7D5909D27AB0DC65CE7F42EDA8361E60E736B433A6A6038B00AF3C0B80063864D18B61863624C18B6
	0C635EB3E35AD80E3B016E650907F0B2474DBE06E9D3B8AE4A02F445139037420AF4E513F04E36A19B3F9134CC42B9DBBEDD3F9C8B61A00008C9E8CFFBE62B7D2349131C3ACF1864299EC2699CF066A4F5BD5CC17185FFD5574366FC2C07F208737FAB723F056E39198961023FA16E4F147FD28857F05CFFF3D14E039C77A3450D01F0A04795D33FBB8C42D19C779045CD05F0A247D5F29FE1864735D3DCE18C04E390474EEF1034359C574B4755E60E9B1703651AB86E619C4CEF8C47AD253279C1B808634E4C46
	7CC2E3789D999BF04DE9B260B29F403955CB95AE7B1D58BDDCDBAC64DED879A6AEE683BBB7644A69EB9FC09B9824F9CA90768388F9G6FE5F32C44FB48422E3DCA489C543EFFAE2163E47A448718BDA448CFF2F6B7768F71692C4C3FED44B20B9F047672A0413D3DD1F9CE1C74A3D9D9EC4F830B7F2CC699CF56DE4939EFE77C8117F8422FBDDB99FF3ECBFC16A2BD136D2195A8DB6AC36102690E311EFF0F67C1A6D3147C74CF3CB4ADEF5011416A6827B46C47D39E44FACBC947FAABFE885B533D99641BFCB38F
	7F0D06495D31AC3CABC3EB4CA3743F5C18572BD946AF355F024D533BB2691D8222C3B2BDC4FEFB69CD22F2A17CA1EA87AEF7E22901696E0F749BE08A211F0C7A4C9D266D9E42B59CF76EC902BB88E109638EE570BDAE9C77B34505C1B8EAB26A7A9865F1B28F1F20E1F25FAE8BE7E4EA6909507FF2F5D12CD946AFD5974243547748AA9901A6D916C71E871DC0BBEEFE98F5377CA46A5344F18D94678F611007D1DF6B699945F08847F2DC9D45113B09499DFB324D1FBCD53F8E739C3DDE93CE398C5A771C12DE9B
	0411741A7830D22FFF255FF8D3GD778F754E144A04115C13806632A07D057ADFFC7FD69CE50B3D5908E820882C8G48FB84F48A50728852AD21F464CCB63963195C5FCC6EE6A6FB4049BDFAC5FC3F6879C3D68FBC137C3D49CE4ACD106922BC50A3DF8EC5BCAB7B860FE50FC67744E751A9725322415F0BD036A1566F347275EC6BA31E56B46F49E23FD55455E114CCA26B3BBE2F94F89E1BF3B00F319E7338AC47BD8FDB15B30FE29D669168B10F1FF56EF90CDF6B4C434E6F908B53480359524B335066229E61
	FED28F366F299C7753D16ECBF25C65C731FD9747F23B21BEF199046BB86EE20AA3F7B611BB4C98EE2949D35BA369C65B9E4BBC5032B2B6F8ACE334ED98F56D898F36F042FDFD7BEEFE175A41D8F6CF3D844FF131CCFE56AFFCEAAF9921CC6C97D8991979645D651492DF2FAF99A6363E37213545753C7858FE75F799D2F6B07090914B7D5CBE2C7B499CD7F0846BBE0163FE436BBE73D12C3F9DA6F2D7FEA5045B812CG81BCEE9F0D0B146F53A6F7D713FB2ED9DC7C51D36DF3C93E78EED53C37AB5EAB65F7D646
	5B27DC06CE712E6CB314F61221F8AE905E26B6ED974959E651907DA851D3DE9F1E990BF8145F895A676A63CCA21F7B6576F9F55FC8BE0E7C5D2D0FFE33104F8B7775694D86E2EFB42FAF48E7BD791DEEE711737D27F924FC993D1DFCD2164BF852624A630DB54E3CBEBD66BD2F3FEFF25257DAC722E77573254B37245CA950FFGD4CEE1F558B84535CD6F1F026BA9E7507927E90AFABD450F6247CCD12F27AC5061FACAG4425CCC1FBBEC23FC79EFC8C6C76B1443D1207B69E70985A6A4D79440E23A01CG10B9
	95781C0AF17750B8F27FB839CB1D5C73CD6ECA177D19FC357D11F41EF492A5676D4DC6898FCE9D9EF0ABC3B7C25A8C4173D82855AFB23BB8B007581E55A69E96EDB6513047B8C43E17B1DB3751B9F79D705DB295ED7D89BA8735118DA7B651467A4257797D338E207387C884C8194A6EEEE7B673ED2E77F6501FCB4675BE235958575FC7B9E1FCDCC0676F850FC35DBC4E6FBC4BA4320A74A2B203CC37A39367154D1C6EF9557CA367CB279DEF4B724ED3CF166736F98B19B24A6FD33EF2667BF34EC879B2DEEF4D
	C1DE8F822FAC0F374915AAF23955592EFC9755B9733FB45BFB7B53F9BD872A1DEE310F695ACFB99DEB57BBB71276EBFDDC3D47260362079FD76F3179192FF70C0338644771EE21776B08CE5A1FGDD820C8588821888B06D895C773E300CB8F0A31E996F30D8DB446322C9317D1B49B7F963294A4756956E4B41F505083BEF90A24FE974F8F7FB72932C6D67BDA96FD7F86152B472E472DD38057E16G446D87988688G888508FA92657B015E7BC4EE20B098496DC17A6EDEFADBBFBF98E094871CE872411D355F
	6B78FDCED3A12FE40042E9B027184666EA4C17191BD1B2022CF553143E4CA17A6D1F14863AB59E9E445B0E59A9D52617C73ED75E83D7B041141E66C1AF9DF25E1D531CEBC9AC0F189F14EBB6A52BF7E92E59247CB0149EA0270DD464D110D612A16F3DBBC1BE6268CD469EB3D14CFEEC23AF52B9F0B224911E427EE459303BAF38E19875D779945B6B508C4F03CF317996CBF779C917B28EE09935F4BDC2825AF1CFE1DFF9BC43FB3FF4F1067B3C6056C60147CFB8CE6EC7573AF4C96EDF48721A50E631C93D5CD6
	87C454E7710DD78B454B6A5267F928134827189E931FF26A13BE29BE2670FB0C0A6218BEAA615998476E891367CFA73D4B785DC9F799FF586A147107CD64771A48F53946EE578B15D87C23346D32DF5BD8A763BA85D2FF8DA239B7916926EF72BC863EC3676B8701775EB82C4B0786C95FFC58E216984DF3F47E9EG7141F158DEF68E0D54DEE263146DC57F7DC2AA076B95D1686B4BBCAEA03F9D6C617AA419A6A50768C3630AA1B95E34041C5775F2D50FCCDB76DDBE36E118133872923C30484536485D27FA23
	D934D1B2C6F3CEAB6D0F5DAF13D57C76091D5F651498075FA4BA79BDC845CFA3EE0B8EF19D9CF72904B8BF4031FAA820BEDFB404E38118AD7674201D25BE4DEC31B09E7A35F8369E42ECF17CD16F7E6F18EDCE3BDBF5080CC12CB733ABE41EC677B193FD913D496A10472D670F71FD61D036A39E65785C06FEC0F8BC2E3F6F37915AA4888B5363F4C55311F6E2BF4ADCB99DE9478E905ADE880F81881C0E3AE8227314F088CF1C0E3A58F48465CC1A4E7AF961995047B36C2EE5E637D79E9D49EEDD7B7974FD0532
	0DB49D6BB75B95B791307857F4988FF357469637787EE6E2778D2EF7BA733B8699CDDB75BCEA53748EDAC2240ADF4E66DE36FE1175E141834AF125E656191A3FC9C8207C99A6CB1D7CCCDDB8A10D4D93F6D39F5781F87FE7783E900A8B01F0A447AD27FEE9BC04E7F2DCBF7F7E147BAC6246D3DCB9046B1E65F733D11BEE01300163FEC7F10EE7599D3972DA5E28779410FC3C513428E8D899EBC7DD46E856D454EBA92C1E4E186DBEEE86723376C14FB2FB0C00E70CACBF33C7A1FFA4FBCCF8D6E90F011F91FB53
	7A559896FF6607445E5A4089F23BAA1867B1F9180C538D966BA0C648FBB269EF03B4FA38271A75912FB6BB7B0808EC72DB33E6F12004DFE25CE804111F7C38FA593A4A4A7212356BD649FD43429C5C07B79364A9CF40EFF84DEB48EF52FA39E3314BE82137C63B7EDC8E996E9B00FA37BCD193781D9BFC5EE1C840BE64FCDAFE9004A3GA6A760FCA4FF07D31EF9192311C79E4FB2897F0775A63B73ECE3CC0E92476C6E9B3ABEA16F29D13C1F3D61746F77387C3416A11F6B2B77FCC6E3F63D76990DF9755AE7D3
	97570FFC8EF6D93D675F14DDD92F7CCDD9B61F183E033415C428EB6139B8DDA9C08DC04BF37C0CF0BD798D87F68B29FA9E51E468A1E7C46C125EACE17917EC71AC6B65DB96B9E5A5293AC5BC234CFFDB733AAD245DF5D920B575117C91FF5396D2DFE06894A91FB126E78EEC461EDE0911629963AD4436CD62E10AE6ED6B11B49CF7FBC136606730ED2DDB2B71BBD5DBCD16817EBBD52C3DACCE4776B26DB9ECAFCD74DCF6417360C7G54BD0FFEE1F9065227E6FAFE0F7C0C334CDFF383791D2D3D7AA31AA72EF7
	G9B3B3A0E00F356216F616967D1BFEABF14A18184820483C48344BF0FF5F5C1C31927FC8149842275E583AFCDDAC65388F43FA97BCB781A711FFAE2B9BCF97B064E53EAB1133E2621292D23E977217A166D5B5ADA61DD6E37FE9CC65D4EA03AF8817D14EB07D1E71DAF204E26D25AC088078344GF866E3CF46E879E0BAB23515105410E7AAC0C9A258B2C08B00814081601F486F0DEB9CC9D7EC0304D3D7CC4E77CE201CE110CFE2A277CB391C45AF221CD6AAE78704CDGE3DF443E3430DD9030AF3CFE10706B
	21AF5DD033363CE4C3F565AA166E323D4EF4E5C31F2A53ED85573B6CD3834C89ACBD1F1617C0B7DCD53D8A69FB0714F39DF98F274B3951A060AF7C45117D8616CEF4B957998DE9260DAA5DB61DB2DDB22491DE9ACD3A4BDD4EEC95C21AB2AF69183E2276B975352EE7C4BDD7B2BD33BA7E768856F1AD14A33D04F5FC59912C633017300E9F22340990CE85A8FC99FD35A43733D8239B0B5E71B15D9A1BEFEBEE13697C227CE5751A5B6394DF7332FA4D6DC79B6A269162C2393C77753B6D19CFD26C19CFF22FAFD6
	CE8A1D33D4A7294FA924BB883EB1C9FDCE61B783689FF5C0DCE49236299776BB6BBA6B48887E49DAD257D055F2FEFADE57452F4469BADE413A3E1E57355FABD857CFD25AE88847834CFC85579B7D8F50FD622F02DD3E0AFD4B24ED6437CCBC75AD6DB60BD534C9038CCBFFD9DA665D182D3D7EF4861DF3B4C37E87DF45B1661EECF7BFFA8ADF8F76FB154DF1C3609979AA7B564F52ED4E55DEFF5ACD67A631C01B78AA4E077DC631D69F166B3EB613763D33BEBE9AA4FA33D8258ED1DEF8E278D20BF3CDE62D7C9D
	B3DA27AC2F70B546F78BBCBBDF43FDEED133314E9C008BFF8D6BACB78F6BAC69B52C338DF974ACF9B20C83G6D49280325794A71195970C7A31ED7A7501B2CB5E7DA65B51D7AEEB4792C2157F45AED66BF54C79F1B2CEE3339749C3CFF323A4DDE14037A8A02B861F5345197F7F8765312F7F877535E5EC174AD02FF55B1E8154F753E370358246570E13B487D417F6CA860386D76F60B1D2ED7B19DBC6EF68EEBF47D5D8BBE26ABCE77CD67F7D1BF4F703ADA6777D33C75F5354EF726234E07A1AE016B7C43D64F
	BA7F24553B4EBFEF55760DABDB5DFD63752D5A3E71172DCA5F186945B06C1BBEEDBE263B711453B1FDB753B3D30DEF286B619B0AEFFFC3DD8FF15CAFBD88F1D1EFE0BD8C34F92E071F5A3C574339ED5A75F0C11BFBBD7C2ECD3B9E825B147540F01749795A7805C08CFFF59BD903E5FDFD2B5E2C6FD15CF3AD78D887F3FCCC374D473ACB24F5D17A8FF55D3DCE71157FD0575DF1DEF73590977C8FDCC31BC0570AB3E74469AE4A40FDCDD7D0DCB16072B391B71C03F45FF33AB3F2106E59F4447D28C33AAC1E36DB
	07F4F725A16E8E89690C1C6EEE89696E4CC07DFCFF44B73D9AFDCCF755A927E3FA7F1A1EF32819212E0FE60AEF1C212E0FC7392F548EF191B350771BC2CCD16665E9453B41F67A57EECE1E56B2D89DCF6B47BDD2C57FC49B5CBD00EB3B357FC4BD7CFB002CBFF772F02E037E76B48F7B8F28F9A5343F2CBE2EF7AB63175AB76CAC2347C7BB6D77B15D4DBE5A77CEEA47417FF45BCFC071E17FD45B77F4EE5F1190D77CA60E95638F10BC79FA9BF35765FEB968GD9BB633F37C7BFEB6D81C2F30F67B50D406CD3DB
	DBF90F5F3BB486F80AFC132FB970755B99EF72B5876A6B153D95272B83587F56C8EBAB465C51724156496698BE973C7DCE7BAE05170F6BE629BA5CA7E7FD8B796C26F7A605C2B88AE05ADB58C67E6AF636E8F4B6B25547F479F93E59568DF4DEB163AD35ED5DCE7129EF29ED2BB68F6BB0F7A64CD98158DD00D18B72297D180CE6117C2245B6514CF923C39A0E550A77CBF2CEFF3F9A7EAB147167F427CCB10A77EF52CFFF59C7FE05B25ACEBAE55A27F8CF98BE7DE53F7BAB14716A30D32637946F433FC25963CF
	EB992C3F532FA4EDD66E5B77998DD2AF4397BB721D78E61178751C2F2623CEBD34A95E1FBCF27AF5B2633416415617520E637ACC6CCC363E14844F54196C3CB1DBB3B28EF8DFB332ADA76FE49F7B1EEE1B45E4E2179F32B520CD8E17B52037D95EF5706CF89B4709EF943A7DCE717E422F206754DF218C6114570664F14247F45BFCDC0BCA247E0669ED37B99D45BB5EF61B53713528319017446BB3ED445FCDEDF947537A7113540F68FCC7DDFE82451B5ED117EF1C45578EA1AE7E9DACBFBC4FE9C3F7A95E3764
	1D7E3A7E7E34164164DEF51C6FB97AD71C4E71AF74A10F9D67FB0E7E053E599514B68942C28A4CF9D270BBE9CF13077BCB15CDD876790EB950672BCC41FC9F23EB7A3D909E839018023AFFB547296F37946F433F028FB27E341641660AC533F02E984EE51E2E38AB64A5453B65345E69414A9877AB14316638EF7D50D7BE26BBFE4A6964FB026877247894F57FD1CC71C9A96A7E639A5E0EE6C25C7EF731BE0FBBC6AAFF623B1E7A2FED745BC86C3B6A6FDF5FD0FC423B6A6FDFCFB9707B578C086BF88F77C62D19
	D5B2C279116FA94F12AC99F05A5B270A779B0F1FFE3B08BE2DE53076F601835B5B647730FEDA69BE5D949E5EC743056FE378D947E878CAF94F37759AD6C64A284A70F53D0679D5AD33145F7919ED04B8C6328D5733076374641D78E56C5BA46B5483CDA41FF1034AFB932AC1F7E4ED2B73FD9C9F2A72BC0C8F4A2C6471614F0338FFD562F590BDB8D29B8A833A2877DD754F6A7B69C12C6F296F637ADCB63BABG42F929601FD25F754DC174ABCBGD78D5081A0818424726F02A758B7AD23C4167B59F7516A93B8
	C6C6246238D9B4047C4724623D956DC36AFBAB482DED95825EBBF166107A5E0AB4177808A1753D9575E94A7817065477D6E0FA798E4B936E7B6A168EA9F5AAFC9027AB7CE0E49BD61EB9A7EB5D75FC7DFB53902EF593399653B2F33E0A000694763A10A5890B48765E12B5A58D3DFAF30FD802C7BDA4E13E3CD73D4439D9DD928AF0F3FA093C679C681C7B511D4885B4D7325D372469D83FDECEBD4775F82CA4ACE3E4E4B12A0460EDC6AB4D3C85F86957770812206327E6A5A1CF5EC5CC731504F9EC63B1AB0566
	A189A5ED109F5F34DE625C35DEC2B6D5D6D4D4C8425CB6FA2CC5B46D3252A3B412104DF60502A86C45B5B6076CA525A9E7B9F74C03A6545B7D892FA8345DA64CF75F46D0FF61D3CF069DEAACCC9752378B593DF48E3ADD489AA013D4071555CEDA397D187013B0DF9DCA9F83F083641B6F32F5E852156BDF1A7C4CDEFB06103DDDD8C2CEB46C91EDF4D1D3B4109F2F58AB5AC849DB851D55E204C840EF97B26D7411E51786CDE20BD0F8F837F82CDF34CB22017FDA88D4564296D2521AFE49E8DA532C3777523675
	E50B10E9A64A956633E89354549A36F67AA515A12D3E1358AC33DC60F781651764D241F3D9F04F5C951F6CC80C4C9052378A33CC963DE1ABD8154536DD50C93D2406ADA603437AF59D4DA8F7GAFB94A006CBE626293E1ABBAC40773E7560FBAAE734BCF4A5F1A8E73841B3006D21283F8951C42GD6EE0322C45BF6GBD94D94A634E78D8DA79703645875305B4D9C8227AA207958C64303E4FE89AA416C2B7EA51CAA9068A17ECFA337D304556871C1528B843A8DEC0456F9F289CFF7DFB5F51821CC92195B0D5
	C252B5BD507C56549FBEECB40BBC8350ABE43F14E7CFE8AAD4B4739D97AD5C7178FF3A408E9A050C3E4ACA7A3F0A7EDFCB7ED7D1CC9545D42D85654E90C67CD347BBD0E6EABC404FDC4A8F297E0A2DD003D25C6B8FDC75F46D0CE410EE2B10F51CAC37C3A594B8295A7A7B3AC45BB9E163174E8EC8B81F541115180A0364DB62F03173D05E864A40200AA4A19737C633B3822BFED61E91C8B93B5B639901007427E93F99AABF49B9A7F94FE19DB12BC4408B99CF4B5F8CA6FE7D0560F9FF671EBE3D49A469CDC76B
	4D863257F34B20A4BA776F9E2BA3795B11C60EE4F10EBA427791C51A7F83D0CB878893E0E73004B3GGD831GGD0CB818294G94G88G88G44DAB1B693E0E73004B3GGD831GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3EB3GGGG
**end of data**/
}
}