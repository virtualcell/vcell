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
				connPtoP3SetSource();
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getInitialConditionsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP4SetSource();
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getReactionSpecsPanel() && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP5SetSource();
			if (evt.getSource() == ApplicationEditor.this.getsimulationWorkspace1() && (evt.getPropertyName().equals("simulationOwner"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == ApplicationEditor.this.getElectricalMembraneMappingPanel() && (evt.getPropertyName().equals("simulationContext"))) 
				connPtoP6SetSource();
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
	updateMath();
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
			getSimulationContainerPanel().add(getSimulationListPanel(), "Center");
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
private void updateMath() {
	try {
		SimulationContext simContext = (SimulationContext)getSimulationWorkspace().getSimulationOwner();
		cbit.vcell.geometry.Geometry geometry = simContext.getGeometry();
		if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
			geometry.getGeometrySurfaceDescription().updateAll();
		}
		MathMapping mathMapping = new MathMapping(simContext);
		cbit.vcell.math.MathDescription mathDesc = mathMapping.getMathDescription();
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
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		cbit.vcell.client.PopupGenerator.showErrorDialog(this, "Failed to generate new Math:\n"+exc.getMessage());
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
	D0CB838494G88G88GFBFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FD8D4D55AB8159695A596969935ECE119CBC69B3BCBAD951535D6D4446266D774C53BFEFD6E33361F3B6B36563A7BDD7EA9231828E828181824280800282814A020208828E424140CF0E1C60619F166A203227D5EF34EFD4F5C39F30741A9FDFE4F737B71BCAF771E773C67BD6F7B1E777C3D671C9132570426A727E48BC2F23AA07F6DCC960462C785A16C712AB908AB5F902F9394FFEB81968BFF
	79A28D1EF5909778C63CAECB08B4E6C0388D42F171713ACF60FD2970070F8DBF03978F79D4C2DC628D0F7CF168FC4232B01FF9B4BCEEC7A6BCBF86E8879CF906EFA079BF39A3C7460F10718849A90290D4C0E8266E4815F1B301A683F04DG1D850CFE901ECC00113B4BE53A36516305D9AF59D29692BE8A1E8809F2DAE1A3FB66899FA50F5FC872CA5E19A927A907309500A81FAA6CB8389B1E954DBD5DDFF61CB6CAC567BAC413292843E4944DD2D127E86F11AC562253C6CB2F25D3B46D5EDDE5351A0C9DFA49
	E8B157F69AA50BCD9072B7917E75BBB3A8BFC118813CA3A86E09F9A4CF833C7B8184EFC27C6FB6907CACF84F5C9C2F4B5F0CFAFE3121C2562375422F0404525E34C59E742C584C746B5C6C5433734C5BA9ABE819CCDBCCEC9F8AF113G26824C8148AD0C57D5G2C223E70272CCD702CBAADD5DBFAFBADE650EB1F2953F618E8896F2068E9C9469D95BBAC36CEC1E065BDEFE72106BCC381CB376C3253B11B0C5F06E59FFEF4221078589DA973B4EC12B87EC2CACBA133052950E99316F723473C838ADDFD53B56F
	2F85E55EA39A272426EB649D3AE7CAEA284CA7D211F75A96D25647B3C8D9A7GBE195ADD90985EC071F5DB1CF86627B154E7C85881F159C5689B2D9F23AC8DE97789D9A5F7EA5AA17859001408A2A64364A235ACEFEE24FED74472EC62324C5FC470A1DB1DF8A6CBA275D3922EA90E574514466B18FF3D3F14545B0A6D713AD6GAB00BF40F800C9GD337230F15AD5EBACC9FEB565B24FDFDC6D327E893D85A7797AF07276810EC7AFA33DD521BBB4463FA1BD1FF5AA45A8519660345448627DD527D137AFE8764
	F1C0B40BB6DAD3013B3ED714C41B1D5124ADD9CCE8ECA20DEDB1760A10C12FD5C03E19CB087DCDFA3B741535D3AF09CE8A961F3D041449B97DF160888C601D59E565B2E22F5920FF560EF8DD5E8E3443A84A6F10E8B313A62441E26969333690CD846B8EE2E711369DE5705E3083ED3CFCB941C5C2B81673F9E14992851F9AE38791DFEF9B40BA5616118F4FA42097F6C27EG15GEDG92C0A040F800381D68D3FFEEC51F2AD97830D0B8E50C26CFE5D7CCC91D361379521C1D6A3AE526F99239734B63F5B5G1D
	GA3G42GA681CC83C85B057D458B7382953C85050C5E5A83C9C5132711A650831FB8951E58E83C8247E4FB5FC536F4399DD860C5AE828BD561F99AB40BBC24DD2A8A27D2D91CE1668FA3BEA13E158FF62C84E85F05BE113CB40F5531FDFA3B08352A498695C26CF4FA8853AFE9E3082C53EA009200DAD93F0532FC4B64BA708D409A0175791B8136G103A5C8B308DE0BB71C300FAD204G9BGB682FCAE3076C2847896609440DEA29FC0B540D900BDGDFG9CGB889B09260B0001E17C356A6A22783F48C85
	188CB093A0E7B7688EE0G88840882C8824858932FAB86E8857083988F9087B09BA02FA2DED787E08588860881188E1075C53C2E9C209D409FA082E0AA0070E53C2EG20994081908AB099E0A6C04EDE489F403097FD79779B2CA95AFD07522608DF26F8DFA517815A78E359F6E8C7254DCB94B65FA953228D15362D15EDDAAF174DE139AC5472FDA35B9D4BC7AF63C595DD2F7CBC6B41EF55B0F218F47F9FFE98B05F3B26C04B77C85B2165FB4A760434B724EDA3F30295B20E78987A1B52B7C9DE6B84661B1BE5
	DA74B5258F921FA73EC7FC1778B0713DED9A3213BA00ED46AE41691F5BD5F4D8A72A06E913C37F97144B7F8B407A1CFFACA53EA51E03A9D20DE8954D1D70E217637E3D147802BCEE6B3270B4E95984EF05F1DEB58C2C6C9CBFBF1B7826DE12EC4653FD12F8CC14A423395B993FA41B64E56FBBEDF58E91B12F9559440FAD7DB04E63741FE613B1D20745EC96E96747134871F9F40CA61A44DE903A5A52E71698FE7DB2B22F6D918764B16226E5F385B246B3751171A61BABC668C95D325A0C3D502512A12B103417
	0C5F86F766A8EC9488B8B2670D0467643DB8566ADF07E32D7C738F89B1272CA9DAF30960F123D28AAA5938242252B956E2B22DDA2EAD7BDABAD61471F6A6EB58B2E2D70D69AAEDA3406E073209DD0D3DFDA69A7D354556E3376ABB449296DF7979E21778EA0BD902D265690BBFA56552ADDAE0E0ED9B40D896776CA9E2A7A8CE49E2BEE03374D9E5FC7FF11226D7AF99EAC4FB074DE8A50C65386BD64FA7652637F5B66887ACFD9266B3F19BE9232A4CFA53005DE8EF515BFBE0B2F15A324F62207388AE4F747924
	3C68AC3D8447CCCF6690FB35019D2DG7E15B8E63AE7B90947F134BCD82A3618CC4CE5A0EEF2A5A95747B33265F28905F0D4A5AB570FF9FC6A67981FD5856306AAF57CG0FEF07B807A2FEEA95097F9F9ECFA05CAD7D2A75981F378F46AB7B547123F93C09446DD3731F3741053FDB7AEDBCFEA644E5D4BB63191DEE7AA40B7800F836CF04699719309C6275E374A7CBD4F147653A42626F7A047800C9B157092BA6F9F6EF42B51F9C889783B44B7986D64BEB9AB504EE73E6244B2DE171E5B5EC0E51824FCFB95D
	5FB9DD04A2FEB2408C973A678A11AE331651654333922033D6C9973C8569C6A868C2GE2DC683A0A10AEC9C1B79BA0F73F122EECAB52D56CF75235403BC326CB5CCF68D20A11AED8260B584F66D3317B19DD985DEFB9DD418196DFF9004D39481360E38E903AFBCA102E79G9BAB9B8EB0BB8E9ED0725BDB4A6DD247722DD470F572CB5F06F479F5EC4C0D7CAA6A147C9E64F4A36A981F10BAB6978C1169126A885D59B2248B2FE3E3F7031C6FA48897F1FE055B112E305E996F4E6F2F1C2E261E4D81547C187FBD
	40579C5B6A1DFC089ED23DD20F93BBF9F9A868A260BD21DE196F06F23E06D94F669BA4DF24AF62FE7096274BB888F4GB587597C1850B97DA03C8669DA8FB2392C8759FC5A7F20D23E2EDA240B72C04764DB399F69628E4A739FF84EBE68140F51CD63F4B90758DC29981E15071CF44CFE938FA0DD7BA14647F1C86E6F8E31FC995D59BA241BAD7351267B3C9E69329A208D8528EBE0F47E8DCA3ABFF23AB040C7AB6892DD686EB94875B88CBAGB49F165B1743CABA7DA1241B8478C400B9B2DDC5230BFC8DC8B7
	887820C6A7DD248B5DFFF03A990DACBE2D09BDF31B1465F67BE16E27CD4CBF5B1A589A0929C9D9EEE2A35285B43139A87AB321F77A550AA6EE17A626EFF493DBEF09EBD27A559FB8DD12AC57EC851DB35FC71AF9F9B433F9AF49174897572474030EA33C9DEA165B0BE6463739D9E917D5C7397DG9F8490A9534D126958F865EF9B49980A2E431F511F53BB0A6C7DB08E5C3D7BE0339EFAE936467E9EDDA7A37405C7F0ADFD499E5247F7C2F890A068880E37DEDF08EB69C1BD6305762604D4CF5F8C420F48729E
	F10E37023F6ECFE5E308F9ABC91ED9C7214E9E4571C3F60EF2BD2F4AEE37F49869A0C96E7B1E4BD10E4798EEF28E995FF499EDF6299E864C8E6CA7DF4DA1721235C699AD2F3DA65175E1B236A903FCCDGE1C7897E19FCB2CEA5FDA15333E39B6A99F8FE1C50BEE6EC4A828FFACEBF4A744BB86654F3561432D4165F264F717BC8A544BB0E61771107BBB6896E5FC7FA75D6ABAD1EE36E83C446AF14730BGDE53B93F431D85C371BB60BA20E4E97ED534DA53B776A9C61838E6FFF13F325C2397DD74E80F3C96E6
	074A9627BD82B7FDA05BE35FFA145F8F6223DAD07EDF9D7EDCD3FE4930FB77E1554896ED9145F94D84BE85DFA12F0CD5AB14FA95697B255D3B2B95C3E039EE089F6F544A130CF5ADD629283B4F88DE68F1044CE4B831817D299D728F7E8A7D694E8D441FC81B4168826937G12EF9C50E49C0F573131647DE94A31E4A5608DG81GEC7C7D0FCD2570545381F491FBB40B36AE0B2DD76CACC06D0A681C31280AC65712770D2863117EB2F85AC428A1E215AAF9036774FBB69EE765FE5F52CAD93658097789955FFF
	1722756D4CFF11A1A57438BC86BE6EAC7756575FCEB181B09B34ECC6DBE5FF9D2FEB789AF5FCA31F54FB0DB913BC87F9E7B33125E4B05A19CCFF2DD24A94B5AFC0D3A6430341A9A33E163F1DFE6D1429A6696D1456A4146953C214E98E501435466B585C33B8D5B977B481BE8AA091E0E6AB4A7DD021A739996F991CF746B7500EFF03690EEDD964122E094E34D93E2B3FD7666B00B4B1G53GE673741736F81AC332FC1F590279E61D007E7C846AF4CB3A12379F60E3G26814CBE013C9B0A0666BD2A0873BE89
	3CCFA2EF4BBC975EA7996FE8F8461DC45EAB378E4D7B5B2D48FBBA2449EBC35EC7F4CA5E2DED0CF7A8BCA35B1077F4AF3CB797E37D0B05B4E95FE23A5B0A354A0245BDD96C6AF78FFF2A743B8C7DC8CD3F8B78E3F7CA4537721C6BDB275F45C0BDC860F5E1CEB16A9A8EB45338BC9DA518A723FB2184654E7D8E66B75FE19B177DA5F952F5945E1E7D64ACD98B6996A5D5EBDF3031413DCFA8D2916DE1BA7EF3B56A18DF93A6144F6A5154116A791D7CED76BB270E1238BD559F0049EEA8C59D53CE41786F943605
	C55BD0A789F06127D027DB058AA63FDB6F272D019B599E46BFEE9B66BB8DF8673623EDF716395AF6EC995235GCDG27DB3ADDEBBD0559E7709B340FDFEC24103374960F7619544E6CB2235DE91F00F75ECE89FD87FD602B6D18FF059E66C9FA344F2A9D5CB7GB70D63DF5B41FD61B47842E934DB6619CF5C65BD5582FA96ECBCAE7B7FF5BB11E7G241BF49A6BD600CDD937661CE6F328628E98FBF620BDBEAE573287635BDC0EFC5B00BE2883E5CD5B0578D84025F5E2FE4FD854EBFA9590578EA0F5E2FEBD3B
	481A13C96E2C993FA9B5DCFE201B44E9ABEA895EE8B6CAC63D49F8DEAC5848ECD2EC7E8C1E7ACE39171347B6C5D5276D128D3AD1B64401DE0E37EB353C9FG5EE5A26AD13D9F71A6400509186F047D441774366E92560FAF6AD1766309C01751952FAB6CC2BE8B8FA01FCA40B93A100FF440154F8413961F884E670D83688313GB7077379320EFCE3B0CAE2EF9179F7CC52CBE235C1EF6EF69FA654C33CBCC8E0365A32EA87E9CB0CE2BF99E91D2AD727D70F93B883057D465621DCE55DB02E6EC6F99B6B919F86
	3844EE9C773CE5D60FFB081E598654679775BFEC5CF3EAAD19177451FD86C4ABD293D056A587D12682482F5D00B27D25D7CB26E0835633F79611F99A5D5FC0F952F12017D1946679824FB39C78CD63F95AB315F9669A215E81980CE807AD07FE189DBE7C0EFCB752EB0CE2CF2907033C2D3869904ABA88F2C49AD1563F262A6DC3E41D41E57D4B8F14B5381CFC5BB20B7DFCE3C92DDDB276325DA4A0B92B341E65EEECC03905B3713A72B3A8F751DCAD392DE730DCEBBEA55F6020ACA1CB65F85FDBFEA907B13F91
	40AB7A8C5A41F8D83BCDDA193552856FB21FA8F07ACA74E1ECABE6814F429E643B2E917BAF96BE54B874186756C6645389BCC2FA30CE6EEBC2FC8260B2CD487F618DC44EDE17757FD71ADC7B4C4CE6CCDB836986F9DAFB33DAE7C6BF1553C7826D8C934A90F084ED175F9B2FEB6BC5BEFFBB0279B11B055612B9AA4CDFEC225D20AC1CDAB35D33242C57B5C711E790700B6D45722FC957AA7FB4B366793323BF4CEFA32A4877598E0BF5E0F85E035F1A7F6436FFAE476C5CBBD328F8C7E07B4A2E27EBB315E6362F
	AC156383A93ED901E7F6495943672890B7490CFB214E2DA6FDF101856C815086A0818481C4DAF0CD66788AD2A7075CEBD68FDEE783450F39FCCC44BDDFABC93EBD62009C4EDA299C37B2BB5C14CF6C90E7713ED78901258B266926DFC6BAB6065BF8C2396FB5ED714194A2235698AE474AF897D81DE3B8E63B8DEB08CF99GBF82A084A092A0410AF69B3332501B5DF05F1DECB026D371673E6D837D5247F48F7818AE723253B13B5D12C76CB68B6C14F5B6DED7GD08950FE966D764ECA2F7ED66B5074B7A6D75D
	FA5F74B96EE33A27FCCC976BE33A768D3E559573064B2DABAC5D9D1B54F27A1DF56591228A33F4E1EE69BED505E3D469D8DB3A68184B5A1DF33D1AB52790476691D31255738832CEB295F84EGE06B5AEF26613AF61A8D5B3CBF2C25FBECA15C8AE08588G08G088518E9C3BFACDA7505D7BFA41DC00B259AF0F5968B0C2BD4FBA338ADCEEC76D6565AB6B4F8CD17E5F7CD47F4BC310E68D8GF1B5GEDG8E009000A800C4BB6AF84D2AFD5EF42C428DA4AD967E2A21A813B73F50B7BDCB3D261BE5577215C70B
	3F943C1EB1F08E0F142B6A8DC63BC43F0020BF3DB76066CFA7E0ECA441FCD8C2FFB22462D904F2893F133C16CA7AD6AB047D81C2A55C4378D79FEDA1EC7116AECA523245DFCBF6E95882D7CA68D276B1495657A17559E06EC2315550F8DA48AEEEEA86598E7F3C20E507B848F39A3743D75F239D66F0BBFC16CC6CD056972FEBG686CC3BB7C462BBE5AF61862B55DE01F169DC2CA36F933C3BD9B24029BF79A09AF581985187E3F592354BF280F6D9D0F01E7C29F6A7FFCBA36ABB37AD07F6839F49F7139F8DD85
	C04BB954FFCC11EF7A5F6FB51D691C167E1736ED75267FD1110DF10FD9458ED9F726570373D02F9167D02FFB697732E888A7G4C60FAF5EC75CD2F33DE532575EB6955675863CD2FDA324F48E66C501B8E0B3D27ED20186C68CA9DEF54210E397D28235FFC22E38B04CDGA37AD14712E25F7A3E3DEE6954FDDFC83FD63B7DDABEE937A3A1AE8EE0BAC01A8346C8GB5G8687365B3FDDC5ECB11C3E09CDBE5C9BECA6E7CA09EFE51865B51D3FC3ABDD2CDB3A615973CD37F4C52AF028C34B1ECD6B09BD23A0AE81
	E086C046G4C27816AGCC83E84F4FF32B3C59132FC88C273FB7177AE657C12F698286345215ED734D2E7B5C52296D9A26195F6F5C528DCF3FE9DE53C5EB6697DA66DBFE933D26CB98502C079B085F4C00380C73507F83D4G34838C82041EC73F994875BAFED2780D777153496D3E69E9741AAE6A3CD63A35BBFC4BEF2B57F4F11A79CDF1CBB7BCBF7D03DBBA351FCE574C6FC62FE7A335733B53EBBA6102D69F3532082C35EA6DE1E09D946EE3D076C713ECB8164A39007D51D7F63A0E83E183005F856C0F0E17
	7B26CF2757F4419734524D5F65DBFEB9DE53C52872E3E32FED8B34461E1301B671820E3DBE34E07FBD0B5BEB4AD9E22F42C168A7815A8651DEEF5578A67F9F3D26138635520D2975F12C67B5DD00E6FE757BFD4B6FEB2F694286357CFB79BEB20E51181B9F55F79AADB8C1E7E5D95B23751DAF9A78468DE2D96E6FA1E5C956642673B2DBFA40B71D56FACD17F751B59D1361DDD3064C237CA24A70369B2F6135D3FFF2CB279E77D9AFEA76EFF53E65B751AD1DBABFFF4D7CDA6AFD4B6F34DB3AA2D5B8D45346CBF8
	1D0D62B64EB9681B8C6B5D5229F51E29A9C30199E5483804B2447AA8431BEE6954F6283C24D5FF3ACDE45C20D1FF70C310DCF7466A54F52795788D8284DE423AB3A11368C376600D677A849E72CD1FFB5D522975C93A24E5530DE9D8F7E6F399DA9AFC74AD37F4EA990A3F5712C11C0BEDC85D77A8C346E15FE4D86E16CEAD035F77DA691EF5CB27866D7C623C268B564CAF2851377C6E731AAEC2B33F3AA65F72BB6EB55DE44D7C16B47B16DF1E57F4C91A797D270F794D701AEE36E6FE379D714DBFC35D52297D
	B34BCFAB5D41233E65576A16CE1DDF011FD61D3C405747AAA11E5D4510D2CEF08E8887F05CBB3B88AE8242137D306E866CA15FFFE770709DB4CC4601AC7C691ED6158CB3D4B27C90E0F96CF44BA375C762CF004D9D3FDF6D792C67752B354FFA862C56BE6BF95B6A458C8FBC14B7FEE07C5855C31F853DEF3556D95055D46F5F251031352D4FAC99FBC51A2D3CF75AC14E92438C77E81F19CDFFC94EF67537CD8167548BB712F2CC3B86674E651F168A6EF3E7F61892193408FADBD3D7971FB81385CB14F96B9A4F
	765B31C65BFE95EB346D37EF4D5076BB38E6E87B0D4973ACCBC81E36AC7765E94BB2BEEFE8D9A272BC49424A6B45650BE8FA3BDD3453C5409A3D2417637EDBA267445D4E70B66AFBC5016F77582413E9EAA5CB75B6DE279726E01D4E3B8667DDFF265FBEDAA1EC85703F866B68BBE9DA6DE268B572FE51EB5C6B2BFCB74DDA4FF61C35D65B0E9F51FDDEF6CB1F2DC3D45BAA79339C9E67DCF4A13B57084A72B9694FC8FAC9EF6B965DEC3D1826E7F11E522F7C4CD3F9B0FB0E5E0C76EC16AC4DA51CF767BA4F3A76
	2C5356756CBA4F3ABA56F957751AF51EF575DF67DD57516B6879E3E32751A61F68E5BA1EACD46AD84AFD665373B8C61D4CFD6682EDCF04EB63F5B9G4557224F74268E551756DD6BE9AFCCF59A6157F6AD5B73124C718D94EFD06065FD2454E7C9D802382957629E200DDB14BAB87DFE5BA00EF1AB2EC39D3AAE919E920483G422EC39DE20421F4083E4E138EF750EFE7F15729F5788945A7DE275621A099F5188EF18DA3D007FF3B68D0A6603CE3F30676B91F03AD7A532745CE329C45757D5FBD28EF448854F7
	5385125FF4888BA3215CC6223EAF258F25EF71C8D7FDEBA04C74AA26F7F1350ED46B3B0B62BBC72A75FDF6AE6AEB0538041128EFEC11D25F6D82765F617306122B7CFAD7399A2EC7391668C8BE6D572B65DAC571266B55F205E520DC8E08CB3C9E659A3BD5A9578EAE572D3A21642A3C41D52E169BD02ED9747B14618635DC7F22F8698635DC574FC7397CA0EE5A8DA81721D851369FF636C57F116F39ADCA4A57EE0B5E4B775C963D1F6F3DAD7AA05FF3DB742FFC6FED515C4F07EEF7F715A8F5AD6578545285D8
	B6DDB6CBEFF5091C67B76BBD5B20FD3D368D3A56FB36C16FFA6FB6B8375E338DAE2E77EE036B578FED03F125CA9BEC63781EED2EB6A815718B4B1474BBB87D9BE5AE7450BA4AFE33E1883F596041EFB68C61B79B0661B79B06701B8D43701B826DBE6C366DCA5DF7F25DDBF7386ADEA663B3F7AA47C9CE7A98A57EF0B92F7BAF5A066C5F7CBD758DAF52EF81ED7E6A3A9FCF7186FFF55DBFE9677D9B44CD75473AFFCB392B9E5BE5F92BF6A95BAA22876B87FA96603ACA450D588F5CFA0636B7908EG883B917B01
	FFDB543AC55F6854A76EC6CF3A555B883FE9B7B29D52B93E0562E7AB70CC373768BABF89E75E84E34E1BD037A535CA1B17713208F541EF67F83F7D2E36A815F53E5803FDDF42CD28F38B55B97366F8DDBEC0454D2873219E22F34B4D2E3A996470207C8CD444333C8F9EF04D3BC4760F77CCDA7E91A6270F121F31EE7C326B14FED8622C33AEF8E77BF7F33D92DF467D739337B2A410A467B76BE677B2E47C1A8EAA79B97B60ADE6ADFE99810CCFEE00A7FE2907147CC89B4456C42FCD537AB6D48CFCEA82F0CDB4
	1E6F6DE88B40720BDAC04AAF884261G1382307C9E49C4799295324C145F53EE01F18EC061ADEAFB7F46C5BEE77DAE521C73D44A69DB6427410D5F4D8DCAFE3BB83FB3F335788D4A6983619986906546EF6F6179CAFFA8C57C070715F5E19727FF3951559FF748786F1B146DC2B9272FEFD670BFDCD20C78E5C7DC796C1471AF9EF1112784692FB96A02AFC63D7DB4471431321EC937F8721B95654AB3E36D3752F1E6B97A4CACC8178938B2003ADB591EAEF98FE1BF113B4B266F052E13AEB0D40CA2E9BF5805E9
	4783FDB4C0A2409C005CD1586EFCCF5B8645A83AC41EB7EEA4FD946F176579353C36192256EF44A827CE4123BC35152769FCA6FC14FA4F3F096223C729777C4F22739CFA7E8E620A83F14D6D833AE69F8461B00EBB190E6BF09101D8BF561DD74A19G7869GF38219FCD923D56BFE23F12C7ABFF4FED7BEDAAD670794DFB7DAAD67B5A9A8E7AB44C50FC619C62422CCF93721CC75E489C93663D1457BDB296A753A9F93D89E332EC29E6503EA5FE8384DE967765BDCEDAE5D06B6FF134EC77DEFD377237FC571A1
	37297BD153A56CC7A3A0AE7BF634799B825A5CF1BB5ABCA459E967F10A772264ABEF0F83D7A10F348A34F998689CF53B2B0DE3EF77D49FA752B9F1526D4C360BB87EA90A4F88F262194D0BA91E040BA1AEBC88ED7EAE2FFB85E390D7C33FE9B5C038FD8C16430EF9CE5B6FD53CCF1EFB65ED746E954D03697C5CBC3403095BE1FD864185C2B80C5B01DE6B025F7C956F93B22E3C9D1E3B22F9B01D4769F8DB4C6D10B11F6092A0BC1D5B612379CE5DD397B85F8366DFF9BB0C3B22F9B01D13ECE807D9DC67576CCE
	BD27AA5E7576AB2F73792B1007F58136C5B9F730F627708EE77BD3F907EBDB54F287367FA9BDF4AEF8073A7DDFC87103F7285B7F20B3587E87C25C2CBB504F16D9094DDB6F04BAF8A762C61D45F2883C934B210916CDB40427F25C89D6DE75713A199C776ED9354DC89ADFEDB4D515D62E87EEF97C10EFF5691A65F2ED8F16CBF6B01321A058A9CBC53027BEE2B235F3F3B0333F0E6327D03CC901176F9222F8927607389941D88627693C34792EF8DD67DD087B9945B9A06C4FF1D9A6AC2BA00EAB2638A888A7F0
	5C3FCC18B609633E6B4534B3B86ED2AF264D9A0B38A23A17A79742059CB74A02E94BB92E460CF4ED9C771099698CE3F14E76C49AFEA388980BF33651F49DF892049381E60DC5FFFA43ED7DB473EE276D736E76D48E051904DF595D6AF258C5714DF72B4B612985D88EA6080B3B9B65FF016E39A98B0957B507A02E1F76D11D90F6F05C873C8D8F613865348D0F0170D40E3B030EF1E7C0D83887F135549E59904E673889E9482F1863DE223896926638EB66E25AC10E7B7DDCCC6B4FF1A973B05FF00EBBB08F7945
	F05C1DF4EC928B61C40E1B4347AB536F41335C2D65386614F9AF4C476F453955FBC5AE7B1D58BDDC7BAC64DED879A6AE668321F4EFGC9BFF8AF7A402B14E7A404E381265D0B7732B95663BDE4618F5CA7349C523EFFAE2965E24A1CFB19BF647DC4FE8A9FA5BB4E3C1D42724C5D0DF936C1FC50CF886E60EE65B9F1520E242733BD8FACFE50A51E2C3D12F35F4E7809AE71C4DE7BDCE5FC12CBFC3AA2BD536D654FD137B9AFC78826DF0C707CFBBC32CEC2283C9EB06D2E6416874F2732B25A18026DF8F9A8165B
	7DFCBE55960A754910C6A6D17EBCEC23E1F2F72C5C36EA4EF97CC3FF1C7D9672FE4FB42DBCFE2C7D96723AF6BA3DD3C0F4C861216437174E9593534D8CA5F631395C092586E63B5D74BBE679CF215E7F946D99C0F11D90F6F05C827A2DB08442119C7707E43E470563D2E9794C0030F09F5A7A6FA9C446CC9E7EDF9AA6776D32708DA9DAF6A274BFDED904EA6671E315C565FDC4773BD3730620A91475E1F6EA9AC4BF763F8FED776AC534E7B8477D196262A0BC0D5BAF6992414DA4F95E0F38BFD19C391B185C31
	A77B7C254B9D57A14F615B75664B4E0336BD17E557BF8BC359B57FFE25DDD7D5D0FF86DC4B7DE8431F8F901C81428E0EFB67BC5ABA005BB07282BDD38561C4GA1ACDE178750GE0G8888C33AC9140E1C498E116FEFA6F7B313BD60649E3DD63EDFF447852B87199307E16386B9FF72AE04E95323DCD18AFEAC6F2FBC66BD9C5B13B10BCE41CF0B86FFAFC2D907D83BB31B37BB919AF24B7BAAE85FAD69CF3798A513485A2E09E409DF6671A63FF3EF12C79EDFE4387378205449A31A6FB51E6A11471A796EBCD6
	14B9F97C035FA1B6C3038773259B96204F8963503FFEADE27D4E6638FF9A79D81263D61D417A5DB60E6F712563DF83047DB8EEA94511BB1B489DE68CB7760C273AC7528D37BE9ABC5032BC9E731847F06BB05A5A93FE2950B5683E3E7D25956D9EB20E5DD39F894F4963187E2CDDF860EC5E90F9E23B4072C8DBC85EDDCEA971757A3CC1626B472B8FB7381E974F10545FE5C85EC9A043ACDEFEF775E059E7BE0038A08316FD9E47FDFC864B3E6681AC3FD74F103B722D908E8408GC860F1EF51B8F2D7B63987BB
	DA3E671A4535FBAC93CFC07862FB0462BDD071EE155FD99E83171D07CE712EECB314FE122AF8AE9586286D6CA2B95BACF626BDC86C945803E746E6BD005FA21E6DF19D635413CFF1F27D5CE09AEA0C43529FB051EF9672F9615EDE3D3993420C572364B31C7C1E305349372FF7A83E0C7E9F64539136E7E6D799376EF672FA40621D5751BDCE7A9873D0742CFC1EF679A6D78736EB8330BE484AF044032EF5BA78C1DCCFC9277BFA429FD42F27E4D1FC4C036A7514BB6761FACA8244150F676392BA8F8C01F08447
	75AEC09FCF980F3EDA1AC97CD8F8886A82C08DC05BC398371746954BF72913FB3E49DD69F29F3BC06DFFA41DA71B88B2EFEFBE1A771052475FF64BC3B7C45AD441F3DF2855AEB23FF8BB13781E55A6F609B61B58F94CF81EFCAFE33E7E811DF37B01CC818F212F5FCA7BB49B59F0E293ED2CAD9C17017E9085F4F1G799320ACA6303B5B194FAC1C6F3D9EEC19CF7A75DE2359585B57CBA5E1F25452BE30997835CD6077252592DDC5FA91D927CCB7A99567157E1C2EAD4D556FEF2EF07A71B3E95EE5FAB74DF35D
	5CC62EC0115F0F6FF5727DF06ED0FC192C1B67212CA1A0AB63B1D0A970FCA72A66CDA1D5CE7ECB07644FFC67BB2F67CE355319FCCCB76E325331FA9DCA6FAA0D18205EE3739045C7CFD06F31C9606B9D13A12E70E7F83750777B08CD82A19C8EB089A091E08EC0CEB86EFB3FEBA319FC8EF9E63C45E2ED904F09A64576EF79FB476E4B550F2DABBCB38F5795A21F18A0C4DDDF63716E7642F0D6779B64E7419F6ECAA6CF265F129A225FA0448583C483A4GCC87909EC67D5E5DD84874A3DB792BA4C95FE1203775
	730381C6315F09A69F5CD97DBD1A027E148D3C8A81DAG8C8F33399A9B4BBCB1645C516FE165D86654F6420F648636B5F68D60ED476C14AA334BEBA7157740E50DB525A4FB30CB104CBB74E167DA9263B179DB651ACD5E66A64DB51B595F1ECFC910534ED67090FA73D2193CC74E138FBAFA13315BCC8C530AF554C867E0059341869331BD397F3C7BD8700973E8BF6BC43657419F1EA19359FC0B257BEC50A50FEFB00F983AC691893413A7E2DB79D13277F6E9E50A7B3CE07BFE01470FBDC76EC7573AF40945C7
	2592BD2AC5F2FFA07AEA3F08764C7D597DC26E1A4A14459E4AE476C4E6477CC71C76A4CFE60F0B03A8FF6BA34C9ED6F80EF8045D9346F47C2F0B5EF5CC3F642E633B871CBAFE3B0B7CDE9339AE575861FA21920BFF1D56DD76EB9B5BE45C02E4255E8CD7DA61398FBCC4676BA1A07B78C730ACDF9CA0EDF31745ACB11AF7687CBD8162139E417A724179216ACBD604323E68CFAE23FA38DE9105E3FD391FDECAFEBB584375C9B24D6CF9449E9AD78C49712695643C2E172BFAE45A02938B318E431C44D51650E5B9
	AE3EC56EBE559B4D220D12B11A6B8F939A0D4BE4953FFD7269DC5CB3169F015FA4048C3E0F0C63FE4F771685F15CEABB62E281474AA1020E6F529E05F7G660B53C474330AC719AF36405370A8DB8FE13E38640C7771EFDA3D536FB61EA2FD90EB4D6C8A1D27523D5C44DE44EE32B964FE2B4B4A7705C35ED10F229E932DB88E1879A82E3FA7D25A021F03AF81347E9CE9ABA554597AF3244DB8C7E847C3B886E06A4F51964F52F94ACC884F79B95A62C9836AD97098EB67AB615972983BEB19796D67E70672DB57
	F6BE656BEC59C7EA4F76196D0A1B88D87C1FF4988FF3571A8637783EBA6277552EF7BA733B8699CDE359E26A53748EDAC2240AFF104D3DECFDA2EB43C27B157DCA797CEBB5FF13100069B1260B1F7C1C33ECECB21BA77C0EFE5F0982FC5CE3B8F7A8205FDE26C1F8B647BDC87DB7B7B2DED79609388F79B71FBA0EDBC147FAA6887BC5627CE1817573G8807F15CF20A0B0AE4F764B25CBD7306BB4FA5FC3C518428E8D89E3F9CF69E43D9D3D12F2548ED79FC77FEB3174F8992A319BF4E02E746AF187E4C9FC7AC
	984A9F73FE217447E07A5BFFDA3F9A43622FDDC97C2D9186C1EED785F39E31341F2E36D887B0C25E5BF90948DA63611EEA56C6188FBA5B88D386792DD933581F47AFB12EB1C24FCFFEDC3DE0DBF1F1E1DE69360DF25B700A73F711C09F53AF709BDE5DD6721B34DE6ED8BCED3450DB23DDFFAE07F47755C0FDD41E28893837FC198E5B1B30DFE09BD2CD4F58A4C2F896C066AFF1BEB27A10D31F1BD307238F23FDAB15485F25B7591DE79B774DD362185FBDC036424A6F0FA85E5FAC3F727BBD7EF6C573104F756D
	73FCC6E354BE6DB39AE376E91FCD3DFB5F5067E06F5F6779B7E5A76CD37E26AC1BCF689BC8DD4907322E846884F0G847C121F912EA63F6140EEA1D54FA3EABB3B4999913B24B7CB187F2A9A4F3AFED6335C29ABC955A16299E57E5B1A1BEBC83DBBED015A54CB78A33E24061497B8BAC54AE70CEB080DEC46EE0344C8710CF18DEDE745AE0AE6F56BFC8A763B63C13724DFE25D5AD62A71BBD57BCD16FE7EBBD52C3EBC1B0275A567D7D8DFE6D21E4D90B6G787D0A7F1692757BC60BCDB2F0BBDFA2BF9BAD4B97
	DBC3FEE76B387E0C66096B03A0C65369B3B0B8EB51F77374E39AC93B938CF9C482CC8518891096952F4B0D4232DADCDB6009AFA019C2343CECB0CA135650B4825DEF4A7E12764AE7B34E2F03A72F5F50F8DAADE65256D457B636549EBDD555D0FF2071B03C4B6D566F4F23ED0BC196C3940ED3B6DDC01B05C6214DDA699E4C29901E891071F83C4E339CEFA48FD78EE62319CDF9704C831E95G6DG8E00G00B000A8003847793DF17B0732955BA061349553F35CA06AB9837864BF01FA3EBF4877B1BC01FA2E3A
	C84FBCC0B89CE052935816869F9184BEE699A072FAE8CBF7141796666DA8AB5E48522DEDF5267B6D794F54697643503B6033CE1893D83ABFAB4C03E63824ECA352AF3D201C6B48FB4BDD4EB6A402FCB31F98FA5C405225E7A853257DBADE17736B61247BEF17FDC3051026F2D869BE33AB5335C09A0317F44CDEB55FB86D95A18EE967E2E6E7D6467186AC63C148A77257D846AB0DD846B3FE0DE5DCE7A434795160EFGAD51B8D6B3D9FDDBBBBB6FE33A47AEBB9DDBDBEB26E739CC516AB537EFA85E912DDEF37B
	BD1F3B0C003869DC5FDA49ED4F7C130ABD73CF3A17974BE7104ED924A75567942E277891CF2A4FA9FCFC8E47C7C190B77BC92CD35DA71CE5FD23E10871C9A9A9EBA8EA195FFF7132EEFBAADE977494167596DE5631CFE1D91F24E51D76B43437GE5CF63FAE376C93ACF9C42062731ED292FA73FE56229EDE936D92C22CD9AE0D87A4B52326C6F27E92FBF2553B907BF708FF99A7B183D696E6368637CFBF66C53EC0EBB8D1E331FE65F7AD93A2766EA2FBF7D0E4ED732E24086B1B89F4E1A67FDED26F81E7B5A4C
	48B6E7F92C9AA0F633D82596D1DEF8E278FC0BF3CD26D47E0ED917214C2FA506499DG4F50985C67D6434FD5C791B9E3304C8266E31995BC03E5768425ED073083A070993441CF97A87BE766433F4D70D60746BF23B5E7FA57EBBA755DE8AC5D4AA14F21FB2E333F22E374C94F286BEC8C4547BD232E3375BA34D7A244D5BE0BBEEAEC70BCCE33B6F89F278DB490FB0BB03EEA99304A67FAAFB5901F34F4F559C5BE9E3C2EA10B636A6D4D96BBDD2F124F19391D439ADEFBF72B0F69FEF3596918EDBB69B92E30E7
	55B66F257808E755B67F000FB523A1AE6FB934793D0D1EEDBE2E513B4DA7B6EA0F0DA39B5D47460FB7EA0F0D1FEED40E0D19DD261F774D1E9F7818EE6B855F4AA1160E9DC7BC27AE07A9949F781C3A9C3E3B00659082F142A4AC07FFB6F9AE0714E66F653020D93B9C96B73B174372E66DF248EDD61683432D15795A7805C08C3F21192C4132367E305E2C6FD65CF37DB33773EA43AB0347FDCC77574BCE474AE8949D8FF7CED217DDB045DBA7294BEE6DC5BE8E033824C93806361B76D3B54F476BC627623E263D
	9457863805E9084B4DC03A131CEEED86523D130238FF6B10AE1D27CD1D0FF40FA7A3EE179D690C1C6ECBBB527DAA151F09B178E65777FCCC374668DBF9C453710C63F9F5F9BCCF71A31ED717C723914BA39062E6BD4F6FC3BEABF0D9CEA95E27DB2F7C5ACD325B39139F93D89937C938C72A758574415F75615A6E608BE807EB4F117567D09E8E2061E89E4EBE271615507E3876586216470F35EF5819471FFC74538FFDCCD76A23FF3FC67DB5698535FFCF257899AF287DDB4F7DFBB644357D867B0A95A789CF3E
	5E46066BF23B1CF7122C1D715F5B231F350E8121395B731AC6EE7A652D2DDCCAC739E2C026593F41716E8B69B85EAD1E0C635DD91456GE1BF006049C32D2D3CBFF738F230F5320FBBB7895EFE273DC9B14A47F5B33F8C5CA79741657CEF3A4EBD9D42420B713A1C97310E9C1C6F1B0F347A18EE74E527E3BED4C44FA6953F28762D1D94DF71225A37E2781C258E62428158DD00B1CB97D27FB1994DA279C50B8322194B762462DC6D8B0A77A65D15EFD78DD7A10FB329CE1D6C0A77B91A679FFE5C3C17DD05FB84
	62AFB8F5FACB715EF9614A67FD71AA64B16622D327FB956F9BAEDE793CAB2FE89E2C3D532FA7F5D6EE5B3FB6F6CA86064FF5ACF4626BC4B22E67F2BDEBF45A61E545FB0B714A5B24770A66415617C672FD66D9AF31752582F8D63C444E9B33B523777B3C2F99595611F7320F7DD8074DE2B2314B8F599A501367DC5600E45EFE708CFA897B09A98A5B7E27623D7BAA58596FAA64B1E248BB9DBD273B4D47F4AFDFF6BA565F99681D1061AF297B4133949F75123A9F7C080FE5A6C1DC414BD81EA307DCC38BF85953
	7A71C93A36957A32BA7FCE0A8FFFD91D7F7B7C2E48A8084B0D457C4B94F7B7FC21F8FF61AA5C63704E954D03697D3044779C014ED131B8067C3B44779C456258ECDD9F5DF374CA3C2E922075957CCE5ADD6B617ED2E5951647FCFFBF07E3BE6BAB48776BFE3ABF8942B1GD3DFC15B1B67BA6D5D2FF8972E421DAAAB2E421DAAA199B8D71C49F556AB46F3E7946FFF3B8A7727E4DE05315DE3EE5F0306176E999F53FDF45969D8BBF1A775755CD7556DC7A82B832F2A5B0F0DFDD80F4AA0AE78D5FEC67B5CD0794F
	F955D37B75B22D93D92F293FFFCD21783C57545F3F3E6D476FDF459097749A6E0DFAACA3EF087CE73F26BCCBF2CF1F535F9ED03CEF6D3B72FED1F7C573E075EDF1BF56374CB8AC1F577B49DACCB98F3FC543ADBC5C51BF9C39C8FA5FF59F9CD69E3E2E5730F1D5DC0672BBBF730D027E21FC43756C213F1E3C13F1997BB6499A7529D504CF26C3F9EF028436A0EBDB21F158BF14E4FA689F142C647E215A017BD7A3F99954B9062AC3B300CEF85D557E2C3CCFB9303C33DF4775391B68BE6CF288B7G303B844FB9
	F0DC598EB889A088A092A061F57EDDF000FD53B2CAE4391FFD970D9A40BEF2566B58EF06F079B362715E0A84077A5E0A3F884AFBA7668E286F2DC8F609AF9FD05FDBD1152C0CBFB3203E378253B359DF9BF05FD777D3971BD6022CAD71C37BB033DF1483573A2B787A77138EDC6BA6FAFDC2733CEECFA4D424707705F4C9D8CE367766ED4D2BB6684D5DE29E9E751004A572DE75BC67E6F5C948424D69F9721EF320F36EC7F7A217D22EE43BEFDE6D59BE3D1C7AE357633112300611114528BC0237992D14F98348
	52276F96A5C147CF4DCAC2263C0B1872150445EC63B14B057210043CC66047B72D67B9F72D6711CD15C5C5C5123028119EEB91CDCDD6FA04C6926632DD21208AFBF10D1DC776125214731DBB6641926A6D7EC4D6D45AEE931638EFE3283A75BF5E88BFD5131DA224548BF38DF48EDAAF247713C92A434ACAA73950FED638A4ACD107D27CGA6G5F05AEDB071E7F697677E27F7338BDD518DBAF2CA0A79A0E09B63A28A9F6129F2FB8AE5AC84E7B851D55E204C840578BE9F67AC837CB83A631C1486EBAAA1E6D93
	6D125849FFAD848AEBD98349E9EB1FE4B4ED2D535B8D34EE6DE990524C4438429296ED02125A4A56CE77D09D122B5A85C176CA8164DDCA65A5DC0AB817253FDE74795A035322D205147D42FC13C55F399F3C4AE22B97F412011430455469306E2D240CB27A711223D4E0370A2BCF14ADEA919D4E1FD96FF15C9330F07A427DA902603089DBA9A5B900D744A9BA414BED1015E82B8750C31679BC6E1ACF25752F9C7824ADC5C81615A4264FF1D841C13A743DC653G71943AD10B96CAAE9438E4531B6DDD96DBAFC8
	1627128C23F886C5373FD8BC66210FCE508C1CC92196B0D3C2522D5DD07D36D6F5F5994DA2E7GF685762BB9FBC2D32422D962983D6C60147FBD8DFED0A324769697537FA574FFA979DFC2B1A594D3D28A461DA98C79270EF7204E54F9C01EC5D49ED27CC57B218425B75FFF713E37AAE64EG6D768B696748F2BB94C216132A312F7734E83BBEFC4C6A85C1C9B713B23292D7F1903EF98E97BF9F4F6BC081B8D40EA4E46056E8F6C6E063774AB38233C7F6F8BCA3107086EBB7273F61BC6744767FCC7F1238D57E
	D4980BCD65FBE63F5CA2F85E5FF92CD7EFB2C9FAD3CF1539136C755CB7A0094E7D3B6FFFC9785B11C60E14FFFF65CBA27799C51A7F83D0CB87887305995B1CB3GGD831GGD0CB818294G94G88G88GFBFBB0B67305995B1CB3GGD831GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG56B3GGGG
**end of data**/
}
}