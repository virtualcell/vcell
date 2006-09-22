package cbit.vcell.client.desktop.biomodel;
import cbit.gui.DialogUtils;
import cbit.vcell.simulation.*;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.gui.AnalysisTaskComboBoxModel;
import cbit.vcell.modelapp.GeometryContext;
import cbit.vcell.modelapp.SimulationContext;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.vcell.modelapp.Activator;
import org.vcell.modelapp.analysis.IAnalysisTask;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;
import org.vcell.modelapp.analysis.IAnalysisTaskView;
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
	private JPanel ivjParameterEstimationPanel = null;
	private JComboBox ivjAnalysisTaskComboBox = null;
	private JButton ivjDeleteAnalysisTaskButton = null;
	private JButton ivjNewAnalysisTaskButton = null;
	private AnalysisTaskComboBoxModel ivjAnalysisTaskComboBoxModel = null;
	private boolean ivjConnPtoP8Aligning = false;
	private ComboBoxModel ivjmodel1 = null;
	private JButton ivjCopyButton = null;
	private JPanel optTestPanel;

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
	showAnalysisTask((IAnalysisTask)getAnalysisTaskComboBoxModel().getSelectedItem());
	return;
}

private JPanel getoptTestPanel() {
	if (optTestPanel==null){
		optTestPanel = new JPanel();
	}
	return optTestPanel;
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
private void connEtoC11(cbit.vcell.modelapp.SimulationContext value) {
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
		this.newAnalysisTaskButton_ActionPerformed();
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
		setsimulationContext((cbit.vcell.modelapp.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
private void connEtoM2(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM6(cbit.vcell.modelapp.SimulationContext value) {
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
private void connEtoM9(cbit.vcell.modelapp.SimulationContext value) {
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
				getStaticCartoonPanel().setSimulationContext((cbit.vcell.modelapp.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
				getInitialConditionsPanel().setSimulationContext((cbit.vcell.modelapp.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
				getReactionSpecsPanel().setSimulationContext((cbit.vcell.modelapp.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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
				getElectricalMembraneMappingPanel().setSimulationContext((cbit.vcell.modelapp.SimulationContext)getsimulationWorkspace1().getSimulationOwner());
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


private void showAnalysisTask(IAnalysisTask analysisTask){
	JPanel newPanel = null;
	if (analysisTask!=null){
		IAnalysisTaskView analysisTaskView = analysisTask.getAnalysisTaskFactory().getView();
		analysisTaskView.setAnalysisTask(analysisTask);
		newPanel = analysisTaskView.getPanel();
	}else{
		newPanel = new JPanel();
	}
	Component previousCenterComponent = ((BorderLayout)getParameterEstimationPanel().getLayout()).getLayoutComponent(BorderLayout.CENTER);
	if (previousCenterComponent!=newPanel){
		if (previousCenterComponent!=null){
			getParameterEstimationPanel().remove(previousCenterComponent);
		}
	}
	if (newPanel!=null){
		getParameterEstimationPanel().add(newPanel,BorderLayout.CENTER);
		getParameterEstimationPanel().invalidate();
		getParameterEstimationPanel().repaint();
	}

}

/**
 * Comment
 */
private void copyAnalysisTaskButton_ActionPerformed() {
	try {
		IAnalysisTask taskToCopy = (IAnalysisTask) getAnalysisTaskComboBoxModel().getSelectedItem();
		if (getSimulationContext() != null && taskToCopy != null){
			IAnalysisTask newAnalysisTask = getsimulationContext().copyAnalysisTask(taskToCopy);
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
	IAnalysisTask taskToDelete = (IAnalysisTask) getAnalysisTaskComboBoxModel().getSelectedItem();
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
private cbit.vcell.modelapp.GeometryContext getgeometryContext() {
	// user code begin {1}
	// user code end
	return ivjgeometryContext;
}


/**
 * Comment
 */
private cbit.vcell.modelapp.GeometryContext getGeometryContext() {
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
			ivjNewAnalysisTaskButton.setText("New Analysis Task...");
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
private cbit.vcell.modelapp.SimulationContext getsimulationContext() {
	// user code begin {1}
	// user code end
	return ivjsimulationContext;
}


/**
 * Comment
 */
private cbit.vcell.modelapp.SimulationContext getSimulationContext() {
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
private void newAnalysisTaskButton_ActionPerformed() {
	try {
		IAnalysisTask[] newModelOptSpecs = null;
		// TODO: instead of taking first AnalysisTask, give user option
		IAnalysisTaskFactory[] analysisTaskFactories = Activator.getDefault().getAnalysisTaskFactories("*");
		if (analysisTaskFactories!=null){
			IAnalysisTaskFactory analysisTaskFactory = (IAnalysisTaskFactory)DialogUtils.showListDialog(
					this, 
					analysisTaskFactories, 
					"Choose analysis type", 
					new DefaultListCellRenderer(){
						public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
							Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
							((JLabel)component).setText(((IAnalysisTaskFactory)value).getDisplayName());
							return component;
						}
					}
			);
			if (analysisTaskFactory!=null){
				
				// get name for new task
				String analysisName = "task0";
				if (getsimulationContext()==null){
					return;
				}

				IAnalysisTask analysisTasks[] = getSimulationContext().getAnalysisTasks();
				boolean found = true;
				while (found) {
					found = false;
					analysisName = cbit.util.TokenMangler.getNextEnumeratedToken(analysisName);
					for (int i = 0;analysisTasks!=null && i < analysisTasks.length; i++){
						if (analysisTasks[i].getName().equals(analysisName)){
							found = true;
							continue;
						}
					}
				}

				String newAnalysisName = null;
				try {
					newAnalysisName = cbit.gui.DialogUtils.showInputDialog0(this,"name for new "+analysisTaskFactory.getDisplayName()+" set",analysisName);
				} catch (cbit.gui.UtilCancelException ex) {
					// user canceled; it's ok
				}

				if (newAnalysisName != null) {
					if (newAnalysisName.length() == 0) {
						cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error:\n name for new parameter estimation can't be empty" );
					} else {
						IAnalysisTask newAnalysisTask = analysisTaskFactory.createNew(getsimulationContext());
						newAnalysisTask.setName(newAnalysisName);
						getsimulationContext().addAnalysisTask(newAnalysisTask);
						getAnalysisTaskComboBoxModel().setSelectedItem(newAnalysisTask);
						showAnalysisTask(newAnalysisTask);
					}
				}
			}
		}else{
			DialogUtils.showWarningDialog(this, "no analysis plugins found", null, null);
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
private void setgeometryContext(cbit.vcell.modelapp.GeometryContext newValue) {
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
 * Set the simulationContext to a new value.
 * @param newValue cbit.vcell.mapping.SimulationContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsimulationContext(cbit.vcell.modelapp.SimulationContext newValue) {
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
	D0CB838494G88G88G760171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FDCD4D576B8DBD636D9E111D14B9679E71712DAAB5A58321612F6A929282828ECA33FD4D4D4D6D4D4D40FE1D094D0D4B4CCD2ACCCCCC2C0AC72DFA020E0E82C32C82D2D8E4C83C687E61CF940D8283FF36FFD674E1BB7EFE6F0B67B7CBE1F6F174F67705EBD775CF34FB9775C3F6F5EBBC2414A502C8CDD01A0E4E489725FD79902D0F13DA0AC1C70168DF1017D6DB90262EF9540C261312F75708C
	053864957BF372057C2EEC88C7C03874237DB99F427BE2E17A3C5E497062A11FDA08AB5FFB430B5E739166E1BEF3E8784E72DCF84E8708849CF96AAD6D107F636505B2FE060CC7485089C28A25F9233CC846B5C17890C09840D48B230F0767DC1A73D91BEAE43A83E3430454FBEC3ABC42C74193215A4A52F6484FE2611D8C61A849EB457ADCB9CDA04445G9065B305AFF7ED02671656A3BD5B3ABACDD279E017E8B617F719CDE23FD4EE946DC7A40B353C53E4693398C57326CDF5D62B5954E510CC167EC623C9
	32C0298CD37E5D6B33A9BFC1E800F0360D6022F5A44FA888A7G4C627841E202EF01F7A340308D755C3AE60B2CC7474FB78BC9BDF74DDD60C14FA09B53EF2A4D29E7705A4E4CE534CCEAF3086D53A02E485E1ED3815086A0818481AC273E30F35E67702C6B146AADFDFD16FE50EB3B59E85BCD3404F7D034D312F1FB45AE0B4DA8882C3CCB5617E948638DD83A2AD3CE47EC9236814BBEFC6FF5420C9B476966E858E4C6584F3AF0BB33C5345DE9931677910FF9A759DDFD53B56F7D02B2EF61792AB9D99AF997BD
	D2B5A7CD6613ABB973BEA01132BE14C94A3A8C5EB3285D4107A95EC671218ABC7353FB284F10F09C44198750B7BA6623ACAD7A71C2FE65251AF6883EF30CAEFBD016E1D0AD4BCF1FD03F9BE4F9FE47E5390662D395F8A64BB075D3928E99EA4FA97C29BD0779D7F39E2937C1436DB9D3GE2G9281046360EFG9547514782672D9F210F359AEC5276811359A85A841636B9E7A9BCC507E4B3B4775BA5C3FF1758EE30198C1DE651AE48B4FBE99B546912EEBF757DAE48E32758AF5AE8CD856E06BED192EDF6C6F3
	A8E7A1213109B4364D54A7C286FDD681797666907B1B8DF669DFD623C1921D94AC5ECA4BE450508E9C01813CB33B8C4EA776EA857DFB819C5C8EAFD2FE5F0A36FE520434D8ACC7862CEDC493A166B8317399346D98836F930F230DED8B88AE77C4FBCE49896473C96EA2851F86D397915FE0BB06F52CBB33841EB5C05F8160G888608G0885C88690CEC2FFF092FD6A658E7429063CEB053207AF517429C0706FADA719AF351DD457AD874D1348BD8CF1A1GD381E6G248394853467EC81B890007DC51CAED041
	3BC3A1232F76A0D6C56350C8136C01CF69C9D7FC0DEC8B57B66AAD97593264F6A04707DC8472D461B99AB48BBC24DD2C8AE7D2D91CE1668F0979443706410E41G11816893BF669633BA365DE09731D6FDE7038AA19A1D9E42748B78A4C45669E300CA00C6D93FBCD93EA5F29D789EE025407A7CF5GDFG103A5C873081E0A371C300E6002FG56G2C85D8AD3076C284789160A040B7C4BE00FA0023G1B8136826C8478B740D9G3B818C3C9C26D0B963C047B400625F3467B481D881028122G1281724FE84F
	2981B884B09AE08AC08200F0A678B8C0AB0083A094A086E096C06128761CDA00DE00C000C800C4006C33205D8268G88G88830883C883A8BE9B728730828483C41D0D3EFCC631D5275DF7A8ED0A78A50A7765F299200D674B36C3BBAAEDDE293079D7B2AD5AD0E95BC659264DF2596C164BC2AD5F77325D31FC8CB2DED45175494F239E7CD68DE70D106E7F603F8366FB2BD6E9799EE9BB34FCCF591E10760634EDE4CE30CC46919FC3FFD37AA6496BD3017966BA1996FDCD69034467096F915FA5BECCFCEF0306
	6C248EE01B713560744F0DAABA2C93F5A33449377F9F144B7F86E0FD4E3EBC62DB62A0CC119AC42B58EF04973B9C77C39E7185F95C56ED61E98ED13C9546F975B03032F3FCF79E71CD03A459CC1D831238CF14A4D3FF0FB33E1F66E59F6834BA07081857C09E71E34B900C73B87DCFF9E40C54E5696F97E9674713487143F40CA61A45BE903A5EB250AFB17C9973493C7608F8CC9EA31EBDFF36C046F86681B25EE4F345A986D2372CB6D39FF429E448AA541CC346EF172DAFD458A8997A87B2674D053862F3F02C
	B574A90E35CAFE3ADA08BAE855E94DA502A7DD288BB8070DCB024EF10E3518CCC38B34E5BFCE470AB25E4EE41D380458D5E33ACA5B0845423CEF8B08DDCDFD83E69A3D5FE2BBE2379A3A44CA96DF3BFA21CBFC3D25DF02D26569ABD610F26991ADB030369D43D896F767C1E2A7A8CE49523F53E699304A7821F612264FA075B60876AE1B49CA984BF1E7FEBC0F141B41E6ECB19C338CC8184FF59BC89BD557EFB09F331B6CED867B9118CCF4DA36DB9CF49E4165C91EC74A0B4E52ABF14C34EC9131D78458B186A0
	719C9CB33D3D004463B8DA9EAC55DB4CE666B2446623C939CE4BAC104BA50D1473E8D62E73F93CC90F713D908EF00BDF368263A3A19C27082FB81744DF486389684FD5273F1547BBA0AE50AD3EF2A546C71338F3557C7F3852057FEF55699F6271CD90F7C8914F6C74ECFEBE71817168008853AFB2E17916570F17729729625A653A42625F48A7BEE0D64CF5CA4FA3F94E799457FC2C105FE800B0B95F645F4AEB9A14EEBA27134EE371E34EE3F30CF0F82E60F41714A0DD7698E7FCB13CB70CD152F52DC6BA23CC
	B78C4FE00029AEF4B51FA15D8C85DDAAC06179CA3A59EB102E66FCA7DDAB3CCBAEF4711CAEC8C1978E6FF1B2DD758504EEFCA9524D126932AFE07329128B18DD981DFDAD52854871419730B9D730CCD7F8A1215B7AB95205DD40464AD197B0BB463B70FB0A5399AFE47986AB78BA79CDDA07F4439732B1B7728B3AD049EFE799375F050CCF6A05ECAE18AA535584923A05DFA01DBE100D5D2364FC0BA0DC4E7945F23AD1014EF8F7FE9771354410C0B687D073E37E37E3BD52C5A87890BDE28315FA3C4369E6A968
	32473667140DD5667B778A3E06B9164DB7C83EC8DF4E7D6067CA24BBC468GC2463279B121F37AC14BD6241BB2164995B3164D279347AA651B3D8D6972AF5226E3725D4269CAAF1267BF70EC3D48A99F239B758D2F1797313952E8F886AB68187D7654A2DD244CA7CE4EBF77E216AF23DBD807F42D97B3BE5AF4F7F13ADE4087G04DE4C6892DD684E5D0EF46900AF88F252D587A9693656F3BDGBF9AA0ACC8EEDFDC68B29A102E6892608150F6096C4F173848476962811F22204B9D2724BB5608F48D63D87C81
	79A90DD3165B069D5CCF47B17F0C984756C82247A94BADEBA752A50DE3F3D174E7C26F742B3FF03A74F1CC5F02CB59FACB6925CA3FBA330937D717B239DA95F44EFC3FEB6665F1A91B7712FC3748F4CEBF485C455B21CB6576C2669BF62952AE37F23AF84027G6486B33A16E0C64746AB2F2FA5E3A83A8EFF58B0E8F0145B07E09C38E9532ED6837452EC0DBD33A2DB269F950CEB696BB710BEFEAA046381D202F13C75C09E2E25879D8993E6BCDFB94753B7837DE5323C17A93E993CD2B71B0DA13A9613BCFBA1
	4EF1990E9F2C8B156BF9F5F63B254BC487C9F25F37F211F2BC46F0A50B4878265BE433CB4DB0E0F6E0BF7979A2A2AFD9EB1451725A6B8BF4FD180CED46C03E51G6917917C9ABD9927123E1069593581758C7CE912F068124BF5B99E742C3D1C69F768F2271EE5A75232D8FE01FA7CBE928C71F117637711EB3BBE975C3F0F74992CD6DABC7BB4860814DF6D8A64177DBBE8F3FE077CF69BCB3D715B69BA20E4E95EAB7FD853B736ABC61838E6FFFC07325C27AEB86E519E0E5FB1BB847F4EE90FA8F35B9CA67FCC
	AEFF82446707207CB76FDE2DA93F543BE953EE554896ED513192F9B5811FG4EABFB79B225DE6506A1E95326FA4590D82E9B627C2F3472A4E3DD0BD5AA6F99B00197FA9EA1D3995ED9097E9489794F8AC1FF7A2A187893E9B398DD452739F23E253F07FE9E000DA5D71FD40EA503819F85108440465F17FEDA85CF839DC01733C72BE86B36587AC4E3A9EAD7CE670C65F5B43A113C2FC59D7FB9D48DCF1B88B5C42CD3A5EF711CFE735AF6D66E6397574A3245DEB7DE58F272BFBA2DEFE72397746A52FEAF177795
	0AF29F5B19998F40EC107AA95A4A8CB4932FC09D376AC93D5718B349F31013A54416D22F494EE4FA2DCEA9D3641CB11AB2757EB1D8B7C316C5FF25D32670504E4C28D01469769214298DE846G3039676C134A39E7B46073C321BF83E88AC5391BD7FB121B719E339AF99F02B441BC5D6B2B9738247B0E4E34D93ED79ED3669B87E98A2F02FE822075AACC3F62B3CFF3C8162F61B34C37975204DE05BAAD90143C93GDFB89EF883340EC75EA9BEF82FDF43F9C31A5071487B558C975E63996F0289503FCDC05E37
	2C714E7BBD4E3B9652B8A6A06F07B2153C27CCE03C536019BB91F90FAE754EFBFAA9563F92C8F300272BDA2BD596AC6E47352EFEF76D8A255FE59B4E52743BB14F776802A64AF32E09CE3FAB3D28B3F34BC568F7A1EBD1D77DA4A803C9A8CF666718A7236B7E9C651600A6FC9236F1855B48132E2370766C4A23E4AD24C714D42DFD695A9677BE21DCC5341969786E4728E3C943C421A67508260EF47CB4C97EB67B87C55D4AFBBBAB8A00491E3D8EF5BCGB4C1FF4036703EF528D3AC6074FFC41DAE9036B0795D
	FABFED8D5C48B6B37E56B24CF78B70167E0836FD344C55361B3EC03AF020C962F4B7FE21351E426CB37CBD5AA7A0B6C2A8DCFC3EC77B9405B13BB404B96D13FAE5E7E6B6EFFB5E60798781CDEC985A6756F26E9BD703EFDC0DF84BFA6E8B008B3F9A6D16FB78C3F7F98F36011E25EB5BE57F7F3482F9A6C13A22EB30EEF576AB6BD65BB5EC8EB59A1EA15720BD2E2B50328763BB0B730DG7A14EBD056091508AF198C75EDB266F76991751ADE9044C58244CE467CF22A481A1399BBEB4AEFE0AB179F680A4211F6
	86451B7ACD1249E0B67DA4162EE5B629687F841E86235C4B49E31B723ACE3BE403EE148DF1B69373486D5AB65E8FG6FB16128477FF0FCB46052F93E3B3EA13EE43075D432FE7C19EEE5BFDEFDAD7471G232EC5BE57FE03FC028197F7AD72A928F56593502B45A77BBA64B3D40BBED08438366B104FBFEA49B78613A4761513FF7BA403A456779A7AFB5C07894D90AF8F92182D3ED87EA5E9CBCC629099E99DECD627D70F93B883057DB65521DCE3C026E8AE6F4B9C1F8E386ABF6138677A436AF18F51537CA754
	67DF5B7F3BF14F41D5E4DEB2C07799902DE8CD10E53DE1BB4A94G79C5F2197EEE5212E9561F301EBD35004C53687E864A130E83FD0C22B04F6EFA4CD3BF856A74944C33B9DB19278478900028A9E807FB6B7FBBBB3C7D9F725D4820B10ABD289E8E723662738614B59E64483D9EE5CDBF21368F113561FA14F5EC43FFA7EBF08D793655AF8E710DA50DF6495447F61300642C52FA167BE5AEF787489448652E495012BB66FAAC570695649B9C14A5E4299C6F7B4AEFFCA366B783F8955C00F6183BC33BCD7AA87F
	A3973C4BFC225469AB87F6E0DB5182BCC7F13E53F6E07F4542AF6E70BE66A95F09FC26820F549B30CE4E6478329B5BF30CB7A27F5D45C44EBE17757F3EA657BEB33C895306C03AF81EF6F133DAE7C66FE8C67A5C887015881421EC975AEE98F09191486762DD189F33D9E8A31923427C45A65AFB15055358CF77ACA96BF5926719827CCAEE42725FA3E8157F011BB04FEF3F7D6F7CF6CA9D79BE5BE5319E9B2177485F1A33AB547B672CB7B9774E0CD23CA330FDE593685ACC70CDECDFD9A647DFCB71E18ABC334B
	468DFC0E8AF1C5FF463DD08FAEA5FDF1G04C3GA2GE281D2GF2EF46B5199B9611BA69F52FD9B3F81D8D945F6772B19177FCADA6799E910F49E173E265381559E1D6A631C3694D3E77AAA130F46F53F43527100E0D61567E205C772ADF38CBC7E4549A43D9E55E81B7BB47F04CF6E796921F0A827C8C00D4005C48761C32C8345B2B0B4BFC598D775D4986E3BA0D537B378FF4221F69F62C70AFDD5BA927E3F6FBFE9931DB8B582997A0GA098A0125BEDF301CFFFEBF4E87A9B13AB59CFFD5E74B35DFFD67A17
	4E6CE7BA21583F3AB23F78D46B8ACB776527EAB993A2DDF9242A42ACDD03DB3A952AF061DFDC533136F441BE1735BB67FAB5EBCEA10E4DA3768F2B6791E41D2482F8B6G30F56D83A7F1DD7B40DF304D7BE6B95DE38B61A9GB1GC9G5937C0FF8150F48B7A61EF8B367A74C3528934D96A8157E4314038CA35B7025B629DB7DB28C15B0659A53E52755E621A0E697867A222E3G4405GC4G44812482645F8AE370DBD147A78B367B52318EB71034D9782B0622CC5EE81F72EA6B79204FF4AD37EA794A75955B
	841FE78C1C43A3652AFA0B49AE51AFA068CFD3875C7CE9BC0C0DA0DF472D68CF5BCE62D90440DB71BB49241324EF0D01F0A2C05A2D38077122557E59A254E73A7C29DA36F82D72EB8DDB60CA89DD4A5EA759863A24819B4CDDA8369E9ACF8B5945CD4DA05B617820169DCAA14FADD3518E0F9EC3BB34CDC5BBCC7C1958E18C04A782CC1D0AF668794CBFBB8C78CC972FE907104A8D3E6C504C86296046C6937185BB33G53BFE903D27F1429EC6FF861ED50375F067A6F9430DDE9388D755F16C17791C3B888A07C
	B654FF639A7F742F7719AE7AB6AD7DCFECD86FCB7F3DA29B636E330ADD326ECC2F35BA54EB86572B18FEAFAB389D7487E8389D754AAC75CF2F05BE539D38DDCB2F8147E6DFFAB512FDC6B6D317413CDB6C6B3401E23223ABF54C51230E526D2823AE0B68988E61E80099DC47875CE49DD95F7714DBBAF55F17FA3BD63B3DEE85E937F37F8A758D209660GC0GC088C054DF315DDE3F0458E2A4FD931BFC38B758CC4E71EB7DAB43A9BE53A57ED5AB1D79F37F6C7913DB3AF2D5B86D2FDA761C3C1258B3BF8AFC97
	2081609040A800D00068A83467EF16547932A7DF1198C9FFBFFF1DFFF6AD7219AEA9CAABDD3CDB3A1159F526DBBA35DD53B5739BAC73CF3FD1BE53955C21152E768B7F725B63B3DD599D1A75F095711B8608BB8460G888208840887C83B837D6661A5BE47CF8A3F71BDFEFA57CFBD67167B9CB35C29156E8E1F6934733B5FE73AD24D7C865C4E8B0E4CCF4FF4CB277653DA4D7C563A2D2F0CCC3F2FFC266B38D32B0F7A281C2C35EA6DE1E09D946EE3D076C7177763D84AFAA776C76BACF49D8742D1G89F7E2FF
	74261F7A68ABFD251B25E93FEBFC26534EAF52E73A6CE92AF3E3F46CF5D31656583398E82B2761586B7EA358FF37CCC3FBCD6EA3769A8561900008E9E82F1F377AA77F39BE5345CE53CA37F11B1FE3BD1F6912B473FB5E4F7C5E7219AEFD1A16FFAF5DCE46B19AF3733D8623490293F4D6161F74E8FD67AB080631CDB416E5D18FA9CB32A6D79B0DE5F663B77E69F4074FF40EE857F4CC061BFB33E59E01DC0663357E35D3673925D30F7BE222B57B37BA7F725B6316CE1DDF22E6FE29FE66275B6EF39C26E96364
	43D8E7736FC29BC73A719A198CFFF3CB27563969AEAD995EB7218C07388C66FABF4723EE6954F6883ECB2B7E74184938C1237E6007A0396ED85D5A41A940AF9EA079AE2CBB8159C49FFA11445FD01F0A867F74596616CE2DCF4D5F34EC3A61A4561DD6AEC32A1FB2689AFD49B0DAD386CB8636A121DC06B0B7DEA313614FBEE5C8781BD63A4E9D7E35D9D61F69E6E966D73D533F7C367BCC177DF72DF449FE6677264FF4451A795D5064DFFED1BE5355E866F722593F7C4678CC572A19DF55AE7F7C3356AD1D5ABF
	FBB573FB414F7C52FC669770F72DBAF996DF9F8B06F8F69743CA3A47BC8E42C99C17DEC9F059B1B0460B413AFB7586727D370107EF24E1B28EE4614D9BBAD4B290DAEFE5F0AAA0FF5BF24BA3739762CF004D9D1FD86A792C674C255AE7BD1317EA1F75FCF969C206879E4A9BBFB07E4D255E4F023E3FD46BAC6847D46FC7683DA93601FE4954A752EC653D538EF21698E638FB877A59741764ECD7FF5B94F0CEED7A04146301981CBB87FDD8A5384F1D59E1CAE452A69AEC5FF5F77309B3D930C4194FAB74EC3F0B
	8A356D97DC28ED3FAB8A3D5B6F8F055E6D776AB24F3224AD5316657DE55A3268BF72AECB6EC71EE4E165F5B7ADCBAB30946DF4913041A099643867A4F2CE5C6D8C6F9EC31FA8707D9E1F4E15E99AA5CB7D86DE27AF9C46BA6D0841F9177584710DA9900E81C864F556F0D22BDDCC136BE96E5D6E75D53E1BE639E7BB36AE5736E37BF2324F4BEE9930F509EADB7D383C1047B997DD486EB52232FCCE1A26178C369E514D56FDB4BD0B7314FEF0392772E0762CACC1FB36CA1656CA4EBB2A48332EFFAF5256755EA2
	4F3A469779567549A24F3AA69579567525A2FA7E58E4B45964933DCC47F7DDF42C62BE13BA00E354623B51E77EC9FD268342D60051F7234F54EB7A8CC228064F30BDAF4B8442AF62EE3667A50363BF2178A8851E6D0519CC3F5711F0AC44D55C03FB006EDE2D54416977B78D6298B7689E54A159C1F844C2B889A07D9E54E17B4F5EF4A8186EC90742E1422FF43ADA07D594DFBDDD2D43B5BF238E3590B7F1BA6AF045E7CA9D2A851CF72C4B56BE67332B4D5059A99A49F2945737289A754D0EC5FD4387C8FE3590
	6EG3046223EAD02B7FDC7473A6A9B82E126578D74DB5914D8353E37D27C54D8353E8D9928EF8C44155D0B7A1A57A8755DA8E07FDD2E73A6D7603D2EF2CD3C97658A225F3AA26FD54B75FB0A0F3ED7AD57E719A8D79C44D55F07F2EDAAD54A75A517AB5F6B9E1D607BDC658A3F8F659A245F27226ED34BF51262E36FD34B151D05F2A5C05C167BD12E6C350A36FD3733AD5A3842F3DB34E505F6DB34FD05673628F9056F36E86F8A4FED5177ABFC37C51DAB3C373B0F3968DA457193BE4F41326936D97A6AAB65BC
	EFDA695986D3D7EA5B604E151EEDF077CA5FB6F8F025E79BBC3652378DE62E74EE0346F5CA9BEC60781CB2D79BD44978EB4B1474DFF27A21AFDC6821F5147DE615973FD96541EFD6F9711BD5A3701BD5DE7CE655887CE615F69FD6D52E547DAB2EFBDA392B6E55B2BEFC3DF21C6424BFD42164D3436B7E157DDE7B377BBD758DD752EF8191772B6BFE9845C75D2F2E7B15965E3FC1DCC59C567DAFAADD7558A84B7BF425322DA2FA30FE6025ACDCD7890A43FE60C8B7EDEFA01C84109E077DC07C9135EE858FB875
	A9FD4013EEAB7B893FAD8FB09D32B87EF30AEFD56019EE5753F5FE92B6C2DC4C83285B8DDB15B62F66E5E15E2644EF647862EFDCEDD1A56B7CC38F76FDE58F224E1FD31D0D909E8688FA90F5DE55C3F48EFF50D537A8B99CAFBF139571AC6F97D4F9D74A7EB135D74BBF526574798F31E749C3EAFE9135CABF2CF45659BAA55E597E2D5B2E44D7F37FFC6930D67EB5F2FEAD8F3917A163770A8BBFE79F1C29496F104CC77248EFC23D129FE903581AE8072075EDE8B47089F98857C44332F00E927190165F85F3C8
	7925C0D89F5F1ED3940F65F7D6B64AD79D6F1425C9FEBFGCFAB4028F8353DFB9A1472B96BF70D6678B5D8CE9FAEBF235C782DEBD4727B1A7313B23478454B691361198E107F301A5FD30D73147ED005784BF6A86B42571C7E48CED7FF7CD2462FECD2368BB51C7E79A6857F5D1595083F2959154FD7B25E344BC51ECA24DF752D8B3E82757E2D661832C4563366E1CFFE73E505724CD86443246CF7D7204F34C05888B086A074E13627CB5EC3B8C4646E36997A206B248B8CC10F10340B2BB02DG618200EA00B6
	G6991ECF7CE9EA63E2598C5D74A7346CFC89F457BE5F9FE4D748B9AD66BB7639127CE339E7154D6A651F9137ED1751E7F17A93E60D1751E7FC10A27676FA0EE742338666620EB76A990CE6738A7CF62BADCF682560F378714F216813E9620AD0149571B20DA77CB40316ADDB45F4084351C77D3FCE802DA4E7DDC4EA990D770984A7483174971984AC4EE4EC3BB16AA5E2FBF29DE2F7BA50165F14F2F1047AD03EA5F187818534E110F395ABC76B1347901E34406090F297B519E0ACFFDCC5D0F3E6C40FEB47B71
	769C7363E8736FFFC61B47BD0EB6DFB16C34739A457B141FCF3FBD6E7E9572D8D50DB6CF871D731FF035F149931E6AE345A031E14D934C368BB83E16628FA9707219C3BA07A6615190271F01B66F925066819C77B70A1B886148997CFBE9065376F7A85EF7E51CFE9BF51D56BC184E0DBA34C3B437C39845A5C3B81D5B6172CC276E93946FE519275F8E0D27B58F26739ABDEF0B1FC4BB7C16624AA0DC7BA45AE1B84B297BD90A7779D9275F8EEBCEEB9ECC67BF77239DDA384E13ACCEBD2FD33CFFE5B97DBA377E
	8AF93C1605ED1175C9566E0CFA52597E84BF695A9605BF096D7FD3F40E9275243A7DFF0162631FD4377FC73B317DCF063816FF201F3DC877D6CD01F0B44709FDD88E497F40F2D8CD4B2660A918DFBE45779E30729A5B1E5344F1B77729EDC65278EB238AD5DA16C71CDB9E7F4D373AAC4DF2B9580365E216E588D04892242193337F95544EE1CFB17B4F657849949F2D404BF784D1BC89A7C2DCC3A216C18D1D1706C1F8AA477D066262A01C48F14F76E0D925F05CEC3A3FA97F69761C32279177D0AF262D6138EA
	9326ED6038B693266D65B8BD7591894223B8CEBC02E983B96E2343C89741F1A78FA3DD5453B8E78B94709BC15253B8E76B216B2AC5B3E14C8D50B2937D695AAC757848B853E9FB47CCCF65204BA67C464CD41743820A8F1B29AE0720B9D88E5190D77ABFA8FF885DF3B38642E19CF7835DFFB29542F19CD71909FA26F25CBFA92EB0896ACC9262C2CF90DC8304BBB86EBBBA6EB5C3F81863A278D8F8B44745D3DCB889F35CF99918B61E63D2B2B0EDA247656AB0DF7DB3DCBE9D72AB64382BE85914C0381A63E6F3
	FE354F60D96E018ADCF3B282AE64991CDBBDDB6E325F095D43355DC26E0515EF62E2BEF09B5D9BC05247BF03BE70A265197BECFBCE894016E771DEB647E7F88FD9789F468BF14FE85FBF1758F6DC5776AC7387077C2477532C6D4CE4F9FEDD05F9C6C0FC4A33546F2A146744C9BB12154576BC30784BDC62495AABB9776D0C1F6E92CF64354FD6463F67921F25C84FF41B3E9AF5CB1FBEC5B05FB44A736F71BC47F46A101F4577DD16F1605256592C0CA68FE3BB9E789C165B6A93D8EE914FE1FDFA618479A61F48
	4329B4CC6E0E154798A7347A3C4467FE197D96AC0FCBB473782576DB30F97A05A7691D8222C352A7135FDEFA1018DDE8CA265FA8DD6E79D28333DDBE7D86988874E149E84FCE0A1B8A61B80EFBFA0860D2A11C7BBC627EF21C6FF1613847A82E81429D4F232D27D35B9BF978E19AA6776D32700F1AF6A2743FDCD98C1E563288FE1E0EBDCE96FB2199A56B436C7440A07AF1A237DD2B836D29FF81F1D60AAB0570169750DE1D740CE29304FBB92E0F62485D44640EBD59679D27BA2EC31EA337EB5E31D34D0336BD
	27E4D76B4F5E6CBA7C02522EB5749BEFA06042398D3F1488AE8A42F19CE798C0DBA7F19BFEB9C84FD43D88FD82C0870083E0A2C094C0520BC837035211B35929727D4D64EEE632871C5C23B745398F185EB129A5B3616FCB7709F27E643D4383BD4A152F08E7F9BF6AB16F11581E0CD966AA78E951606FC5A86B90EBF7FE626B5959A91E56B41F247D2FE468ECB3C9E61135DD7348D5B5B20F83BA64D1641147DFF56EBC7616B9F9EC53A30F8A0FBC2E523B739878424963DEFE07D803868F66CBF3E7234FF52420
	FF859B30FE1BB9AEC164E3C90ECB9331FEC724205FBC53CD7D9542899CB70B62481DCD648EB3069B6A76D47792B4E47584D19EE8D99E019E7398E99DC6DBFB42AF9612875D57378B4CE8775494F6CFFD6ECB504F3C4474E76D42F0DF3117BC31DDE0F96873483B4BA9A53EDEDFBCCCFC3D3DFEF70B6BF971FFD8545FE5C85EB5A0C34BCBD8FE03BDD876C60EBBEC4432F7F0DC92AF7B1017307CA6F6133B72E3A01C8C107DB24CE3DE46386BE9DC2BFC1FB6393B1A5CF3AD37D39E4B4493903E781E2DF8A779F2DF
	165FD99E5F1DF29EF3956F4AB6C369A7190A67E261BBEABB3BC84EB60B4683AF93BBF577601931161771DBC4F00F6B98E7D2B984657A79DE0F37B18ECB7FF1AF7DE6A11F976E6BB3749BA14CF865D5887C3D22524977DB13B73E0CFE9F797408FB618E3B4AF85E86A72F61A33EF9DDD76D249FF7589B3DBC36F47916948A360AG08114BF6464B2EF5FA564B381E72841DFF6ADFD12F27BCCB71052F2857D37AB2F1BD258C6282DFC1FF0E265FA3D3A11CBD8BF175F350474BE6212FE6E793BF6E003083A084A002
	47AD21F1647EF1F217BA39671B5C15AE2F054ED17B9FC96749A69DB2EFDFBE6A182574719B5D72186BA5ED266039AF54EA97657BB633096FD9EDE237E8330946FD261F4877B2666B31F44E1DGB2A54D62FB75689C54C6B61C58C49BEB8B93797D33792F421C9DE098A070D5F6F7BB731926CC5F7520B713746BFD26FED35FC09F15044991C0676FE140AF62D514636693C4D711DEC4E614696EBB01734AC4CE572D1AFFBC37516947533D4EBF64FB5ECEFA2E1B9712AD9B727B8DB5CE3E19BAEFFC192C9BF5A8EB
	AA484AF8BCCCBE8D4B79C68A2EF227EDF2727FB843F77D7B5967F9D56DF4677A196E4ED3CE476A75ED742E526C5754FBEC7EC671852F2977583C4A57BB0AA1EE54EBF83750931B09CD12A12CFF3DBD2788209A208D407ABA6EFBFF63935218FABDB35EE63136080322D9317DDBBE27D5F52A7A31F505BB66603AC244AD578811E7B7F83C3BFD546B2C6ECF141F113117E510A7532FFFAB51AF9E62E6819424C29B86D08B50110A7AED25F79D119BA80CA6F2FB10212B175E564F8F861844A1A71AFCF0E775B71E5F
	67E486DE23G428122D259DC0D0DE5361CA87622EBC22AF2ACF3F0A361C7F2835B1A3A0F61ED476C14AA334B7D7FD65E8317FF39D9176141AEA9B26F34D467DA9263917323F24D26F85DF71AEBB6E9BF7E24ABFB0325EDFD4349233A2DFAAE13F74DA0598FE1B01BFA7A09E1BA300EFEC3674023A0CD709B581E9C53988B8672FBFBE25EE0FB9D9261197A861BEF49FB5906DC72789E7398C757A3F25F0431631B58D6469D775DAE4DBA6EBEAF98CBFE36CE0E3FFC105C0F2EF569920B3F60A45123DEA47787222F
	8E09E84F224993042215353A059E4A2475CDE64761B71D7624BECB6D7181374714B719BDE260B963CDF6CF9853314761DB47F39C6EBAEE2DF36A78635764771A48F53926AE578B15D87C83346E32DF5B58A0634E7FD92937BC4E2B765C87765279FAAA48AE3C456F6A14C85B5CED691798CD971D3F17C1FC4DDBD8DF9C835E6ACB6FDB4A7AE27877922A076B95D1B8561767230B49EF87FB38BE8977CB4EA1765038E2887764ADA367F5FDDC55A353167E1007F59866A42E3204AEA9F471ADF277294154AF5AA899
	23B9FBB7215138CCD6715BA7217C2C5A70DB78CD628CFE8FD2844799793DC1A99C77FABF62CA52E06CC84B210A4EF38FC038970079629D86743320B4660B61700CCAE36BA14C97DF91FD0FFF8FEDF77A5D5A03248FE22D19DD21F3C289D9EFA076A2F6134DA177DBDB4CFCDFB8641D7FB66AB14A0C63002637F17D7D86CA9BG61E000A91CF62995F50E61347FB8CAE805F720EE81D43C033638124ED31AA05C768E5A62E2A36A99708EEB670361997E8E3BEB19796D3B22B73FF5ED67F57B8BE49FE9BCBA50EFD7
	5CC4A00F1D66E2BC4CDD9BDA5C62871A085F573B5E694C6F9AE4B4FB2A97D21F26F75092A2D57CF1B677328D082C8D8B9DD276ABB5734E507CCDC282516FB0DD926467188FAE4BE07304F06A17796F42D876DD1CBB3CCFF1DBA05C4AF147597A8E0447F05C7DFCAFC5A8473DC2F15190CEF897678FCF53B1ED920453B9AE05627253599D398CE79F713E87424797CD120AC6BECBBB62BCC6322622DECBE16574C416FB3F799ABFEBDF1D4E7C31851E07E47D19BF7EB0471BBFBA52157E983C06781B562F463078B3
	BEA27E368786C1EED785F39E3143241F2E37D80FE18443FFF302485A60611EEA56C65CD3AFF0BEFF4CA0BB667A4521E2FE09F10389FAFE72636A259BAAAA4A0A2BB62C45B3A3BA5C07B7867409CE47EFF8CD6B49EF527A38E33153E42137C63B7EDC8E696E6B01FA2FBCD193F0EFF9AB1FF72427E39BF20266DF7D9E589F40789E4EC7DEEAF06AF34909116843E81FB9C1646FB6186D4E330DADBAA50E795DB43A1E415EE3956FEFD51C7E7D9E791527B38F79DC5FE64FE7B4DE5C2CFDC663554D5AE7D35F5A6C7D
	9C6C871BBD7F266C6CAD4A5F14E573095B9AC9DD9906328E86188A908710749EBFA33C057C06833B05D4BD0FE8B47610B3A2F64950AFE17EC3DBBD6BFAE26BD2272EA4D51708E71479EFEB1E3315543BCE8B5426BE429F7167EFA565850ECE117299E3FA6640E66A691598A91EB15ECA3F410B5D8CCD7DBDFD987BDD61FD981F3D0FF5EBC315466FD46DB0DB06786FD4317A52F09C6B0B75FD2CAF3663F46F820423G925E47F16177546F77D8ECD2AF377363BB84AEDF69B672BBDB6D06431AA72EF701985FF59E
	0641D91B210727FFF5A7E9F7E6C19E399FC07F8E5084F0GC07AG4B2AEFDB29A73E00E48A51723241A8CDDAC9538834BFE3FF095FB0795B87BF05A72F5F50F8DAAD7D242D29EF5C53563877E0DDCB734EBD3B61DDEE37A287513623C116288FF01CA28C214D52BEC01B158F915A8AE1FFCE9340A1G4FF2DCFBFC24F2B09B35B497435381BC03GA28162G12G528172B376671482487746FD634DD6EC030453D6CC4FB5C328E7837099A6406EBF61FAC6G0E6979FBCA1B82E13DEEFFCE91GEBCBD3F78982BF
	F7A091F9BD3425DF56D41595FFD9DD311625BB7ECFE73AFF8DFC22CE3783065E251F98E1CEE06979242C981A614A6A353C4D9ED04EF564FDFCAE6B3255A0DF93006F76729C17F31D87A00DF5C469CCAE67AEC7C11A609125CBF3B933958EE922FC24E3768A5D6B345779862FF62EE0F6E6E57CA7A316F1BC64131B09E57C16914B38A1934BF835C8E807A19C84908EA01FC1B67B37F6566AE73AF3CFB99DDBDB7B0C1E650A8639DD575C56D3FC1C82AF57F3BEF71981F135FA54F73955ED4F3C5ED9C611FA7772E2
	796C355133EC7A7D2AF38A87A8FE0682AF7F3644D19C9F25C0DCEB965629E97B1CE5FD5068E5FCD2C54A1A94357C9BCF3C2CA300D7CA9616F52608E5DDB29B4B3A1216758188CBGE3GD81BE3ED237B44A19CB59B5B16C95B4987E2CFEDCB2B4DE295ED52B106253FACAD4BBECD50DEFFFA02360D0940BF950051AE49F09FC71771756012B97B699CF78BBCDB814837FE16AEC8273D7E94CE47033DC0BB8C4066434FFADD3FE67CE6E73A2F4DBC7DAFE7F9AC27679FACD629CD14971E983E4462DC1329123FE32E
	50AB738B1765CE02E79AGAB330FE6E31965E7031C59D8E61D33314C8232314C82A9EDA40463G123351F6C3F3147D333CE75B6BF9F582425C7D9AF326A21F6954F72331F47F7219CE3B4E3CC64768C5F355F576DD0AAF1D2B2E3323F4E82FEA088B1E0BBEFAD72367F1DAEC236FF15A430D445EA20C2F5A0ED965F33D0FB7921F34F4F75BC5BE9EFC2AB11F631A6D2D96BBDD2FE2B6509D772F3D4B77B3DD73A927133FB952F1DE3A1B4DDF21786CF9EA1BBB0E234D8BA04EB18FED7E4ECE4FB64FE872ED73F9CD
	5AE3637CA677317112A66D317172A66558185965F637B3CFA333E72C1F6932067CAB0771F4CCB5E31E3A9CA6D3FC32DBB9D47371DAAA44F564E0B96CEF76DC8E7FE976DD8EBD4D5A6550576CDE8E36E66DF2F0B4AB4B01610E4BFCED7CC2A006BF33192C413236FE37215F502338677A0BD33625FC07171F69A4BF4BEEA1BD6FB6B5C7DDF69FD1FCCC0E3A6CA670320B07381A5C7D729A5AD45A9706C0F8ECA66EEB3A0362A2G1727C75C95BA247BB7271B20C33A27F4088B50A3DD96CFFB169E697E1201388596
	24B3F13A828B525D1C09763949681FDD277919AE7D1453B13BDFC647B3F1396A7298CF71B3F2556531CE4472C8063816BC9C7B157589DC160D0A775B4D27FF6D6651531A87AB63AAAB6E111A1207BE98FE9457F663399DBAECE47DB90D07BBE938E0BE06DFB82A1615507E327648341D4EBB7364F30FFE7A697DFE264B967D736FC954DFEB662B7D7BBA0AEF182F766F2F38FF37C2DC44FC6CAB865A88CF3E5E46066BF23BBC5CC6564E786F6D514FDA7B00303F47731A46EB42292D2D3471FB178A9740D8FE810E
	F7C3B2F03CBBFA810EF76F2134D190CEG1835405B5A4AB45DC865E06BE4730D1F8B3EFE277DBB45A89F574D5A7538CFAEBB9F653CCDCF642C05F08700B59F6BC871EC7FFC245C4FF4BD271C0E79101E1ECD9A1D2F762DBC0A8F4AD77B56B8BEE78905B87D427DB96CAE402845F954FF4C26FE117C2245CE311F4B16269378FB06620C6DEFF5273FDD1D70AB6411F25C29539B0A779347CFFF5E177E8AF9048DB9F53ADE713EE96874673D6FD74863C307D327E2457B553FC25EF71E56BCD8FBE7780C54D939ED5F
	EFB2CA3D8CDF6448F3621BC4B22E67F2DDA9BA6D9026F82F94CF3FCDF61D56BC587A523F2D38BE533B102DAF8540B388001CB7E6EBC6530E7ADEB332FDCA5E49BE76FDDDB60B594CAEBFE4EBC0975BDC5600E45E8970CCD9087D44E405EDA3946FDBFF85BB377F8AF97C20F96F106FF43DFE263B6A1453317EEE9BDDDB52AFD27703BBA9BEFF113A9F0C63E319A2088BD8046579EF2FF7B2A6AD7234FEDCC96FD8CCF34BFF93456B972B731F2667EB0790A7AD467C674DF17A50E245FB486C53DF56B71D56BC185E
	E7DA791EA350B93FG4710532DFC4FD1810E4D5E263481908E861882A07FEEC223077BCB15D5D89E73CD376118AF06737D42CE77A7AD591FD388D03184ED5F2098676CD63CBF72AB5C29724A2FF0270A0D5FA55244F57ECAF1D7C82D627DEE7D6957F9662F104739EE5F03C6166ED2BF53451DF2BA56CE74516FC95292F57BB1C87181CB546D47873C9E0D0138D93CBC63EC5E72EFDB6A297D3A0A56095E256A6FDF13A95E31D47D7DEB039D3FFF0D063894G36B76ADCFD31177CDBBF5C2FB8CBE2BFAA7077E145
	FB165B7A44AF6F97ABCEEB9E2C3EBDE7437AE67C904B670FF632969348435753F0B88FFFED9F09DC019F7A37DE437258BB22BC7CDD2FE1632ABF68155F7919EF3CEF73669B2EE78FC79B48BB991731EF132CD12FB8177019E9D75E1B908B36A0EBDBE99FE27FD0196B21FFD032127B07E5926EDF4DAD44B2D8A1F92BC38DC057D168EAFFD65E9BA5ACEFF3A12E4F9D22E72683A1BC91005DA558A26138B292F031GA9G394B766714AD635F858758B7AD13C4167B59F7518B86300FECD9067D260D4BFF88F06CDE
	09EBA4753D952F8A4AFBA7E6C86AFBABB2DC62678F286F2D284BD046578E286F2D4074CC76C9836E7B6A06DCEC9A8C3206AF736E434CFE97C83856DD47573FAF16F02D1B6875A14D7348D791D01142DF9232A4E1A9595EDB3C3E383E57505FA39663D18FC9D8A46FD5AFF6EED61704FC5C1CDEAC6FB987BA67FEF4A7F2B165CA367B96B79E9DB04829673B9E0F1504150C0CACC695933C4DE4254CDBC01681C30FA889F37929D9C94815F791D33E123010EDBCE639D09E12D03C877871CD6B454EDD6B45E4D3E5F9
	F939A4AC58C30F3508666F2C74880DA44CE63BC2C19576629ABB076CA525A967B9F74C03A5545B7D092C28345DA6ACF25F46D0F741E30F069FECA850893AE6E1F6AF1D03B68BD9C3E4126A30325249A803067E0430C89D5285GDC83FC73DC368E5DF55546E7E37F51EE4F94E6B78B4B480906FD220DAEEA0AC67263956D220D643CC318EB3518A092704D025ECE9FD9F66918D9EC918A3A770AC787C43BA49A792F05C0E1ADE9A1B92D9F10CC6675CD86FBAF2DDB1BDB84FDBFB12E3008C51B2124563335534DD4
	070C3AC302A0FB25G72AE267292AE651C4B62DB972CDE35ABA9B2D3506D906619AD86638E70AA0B2DD918AB75129236180D8E6BB73514D17690DEF214896C16F375093265ED224379B36BC79C3F9F13171C3783C6238E1B301ED21283F8651C4288DEEE032CC4DBB3008132AC61F13FD9A1FDF66F4E8F8F68048CD9C9E27AC207D5E286034C97D016C465729DA0357478CBF70FFFE24B2C94C832C3489AA4CB4C10F13E13EA4FC0DF27E8BBBB7C120FF302924FA3F2D909F9E821960163F59B7ACC66E344836986
	B0444F15EC06FEFB3745568719962BB446A8AEF87945F7D7DCF275BBBFD0411DC921F631A202246BFB20DA2F2F6B6EB6750B1C811497307F1833A7B465AA1AC50E31CBF6BD7CC1A778D7031059D7D1C17FD7527FD564FFA545D4D2CCE595945AAC416B1FBA5E61A0BA2FF63878F9982F832560D00512100DDB2359990135A715E784524E6A72F8C620ECA5EBB7EB65A7B96744767F44EDA2EEB58C786015784D20F893391841537E4EFDFD8633D9B2180F54759B49DE4F6D47A45139FF7761CD043F9DE964C896B7
	E393117B30A24D7F83D0CB87887170E2BBEFB3GGD831GGD0CB818294G94G88G88G760171B47170E2BBEFB3GGD831GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG29B3GGGG
**end of data**/
}
}