package cbit.vcell.client.bionetgen;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.opt.SimpleReferenceData;
import java.io.PrintWriter;
import cbit.gui.VCFileChooser;
import java.io.File;
import java.util.Hashtable;
import cbit.vcell.model.Structure;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.ReactionParticipant;
import java.util.ArrayList;
import cbit.vcell.parser.Expression;
import java.util.Vector;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Species;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.util.FileFilters;
import javax.swing.JFileChooser;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.PopupGenerator;
import cbit.util.AsynchProgressPopup;

import cbit.util.BigString;
/**
 * Insert the type's description here.
 * Creation date: (7/1/2005 1:46:25 PM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGOutputPanel extends javax.swing.JPanel {
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private BNGOutput fieldBngOutput = null;
	private cbit.vcell.server.bionetgen.BNGOutput ivjbngOutput1 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private javax.swing.JPanel ivjConsoleOutputPage = null;
	private javax.swing.JTextArea ivjConsoleTextArea = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjConsoleScrollPane = null;
	private javax.swing.JPanel ivjRuleInputPage = null;
	private javax.swing.JTextArea ivjRuleInputTextArea = null;
	private cbit.vcell.server.bionetgen.BNGInput ivjbngInput = null;
	private javax.swing.JPanel ivjRulesEditorButtonsPanel1 = null;
	private javax.swing.JButton ivjRunBNGButton = null;
	private javax.swing.JPanel ivjOutputChoicesPanel = null;
	private javax.swing.JList ivjOutputFormatsList = null;
	private javax.swing.JScrollPane ivjOutputListScrollPane = null;
	private javax.swing.JPanel ivjOutputPage = null;
	private javax.swing.JPanel ivjOutputsTextPanel = null;
	private javax.swing.JButton ivjSaveButton = null;
	private javax.swing.DefaultListModel ivjdefaultListModel = null;
	private javax.swing.JButton ivjImportButton = null;
	private javax.swing.JTextArea ivjOutputTextArea = null;
	private javax.swing.JScrollPane ivjOutputTextScrollPane = null;
	private javax.swing.JScrollPane ivjInputScrollPane = null;
	private javax.swing.JPanel ivjHelpPanel = null;
	private javax.swing.JLabel ivjOutputWarningLabel = null;
	private cbit.vcell.client.BNGWindowManager fieldBngWindowManager = new cbit.vcell.client.BNGWindowManager(null, null);
	private cbit.vcell.server.bionetgen.BNGService fieldBngService = null;
	private javax.swing.JButton ivjHelpButton = null;
	private javax.swing.JButton ivjOpenFileButton = null;
	private javax.swing.JButton ivjJButtonManual = null;
	private javax.swing.JLabel ivjJLabelAbout = null;
	private javax.swing.JLabel ivjJLabelHelp = null;
	private javax.swing.JLabel ivjJLabelTitle = null;
	private javax.swing.JLabel ivjJLabelStart = null;
	private javax.swing.JLabel ivjJLabelStart1 = null;
	private javax.swing.JButton ivjJButtonManual1 = null;
	private javax.swing.JLabel ivjJLabelHelp1 = null;
	private javax.swing.JLabel ivjJLabelStart11 = null;
	private javax.swing.JLabel ivjJLabelStart12 = null;
	private javax.swing.JLabel ivjJLabelStart121 = null;
	private javax.swing.JLabel ivjJLabelStart122 = null;
	private javax.swing.JLabel ivjJLabelStart1221 = null;
	private javax.swing.JLabel ivjJLabelStart2 = null;
	private javax.swing.JButton ivjJButtonManual11 = null;
	private javax.swing.JLabel ivjJLabelHelp11 = null;
	private javax.swing.JLabel ivjJLabelStart122111 = null;
	private javax.swing.JLabel ivjJLabelStart1221111 = null;
	private javax.swing.JButton ivjStopBNGButton = null;
	private BNGDataPlotPanel ivjbngDataPlotPanel = null;
	private javax.swing.JLabel ivjOutputLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BNGOutputPanel.this.getRunBNGButton()) 
				connEtoC6(e);
			if (e.getSource() == BNGOutputPanel.this.getImportButton()) 
				connEtoC2(e);
			if (e.getSource() == BNGOutputPanel.this.getSaveButton()) 
				connEtoC3(e);
			if (e.getSource() == BNGOutputPanel.this.getHelpButton()) 
				connEtoC8(e);
			if (e.getSource() == BNGOutputPanel.this.getJButtonManual()) 
				connEtoC9(e);
			if (e.getSource() == BNGOutputPanel.this.getJButtonManual1()) 
				connEtoC10(e);
			if (e.getSource() == BNGOutputPanel.this.getJButtonManual11()) 
				connEtoC12();
			if (e.getSource() == BNGOutputPanel.this.getOpenFileButton()) 
				connEtoM1(e);
			if (e.getSource() == BNGOutputPanel.this.getStopBNGButton()) 
				connEtoC11(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BNGOutputPanel.this && (evt.getPropertyName().equals("bngOutput"))) 
				connPtoP1SetTarget();
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == BNGOutputPanel.this.getOutputFormatsList()) 
				connEtoC4(e);
			if (e.getSource() == BNGOutputPanel.this.getOutputFormatsList()) 
				connEtoC7(e);
			if (e.getSource() == BNGOutputPanel.this.getOutputFormatsList()) 
				connEtoC13(e);
		};
	};

/**
 * BNGOutputPanel constructor comment.
 */
public BNGOutputPanel() {
	super();
	initialize();
}

/**
 * BNGOutputPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public BNGOutputPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * BNGOutputPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public BNGOutputPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * BNGOutputPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public BNGOutputPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Comment
 */
private void bngHelp() {
	PopupGenerator.browserLauncher("http://www.ccam.uchc.edu/mblinov/bionetgen/index.html", 
								   "For Help using BioNetGen, please visit : http://www.ccam.uchc.edu/mblinov/bionetgen/index.html", 
								   false);
	// PopupGenerator.showErrorDialog(this.getClass().getName()+"\n"+"Cannot invoke BrowserLauncher when isApplet is null");
}


/**
 * Comment
 */
private void bngHelp1() {
	PopupGenerator.browserLauncher("http://www.ccam.uchc.edu", 
								   "please visit : http://www.ccam.uchc.edu", 
								   false);
	// PopupGenerator.showErrorDialog(this.getClass().getName()+"\n"+"Cannot invoke BrowserLauncher when isApplet is null");
}


/**
 * Comment
 */
private void bngHelpAbout() {
	PopupGenerator.browserLauncher("http://vcell.org/bionetgen/index.html", 
								   "For Help using BioNetGen, please visit : http://vcell.org/bionetgen/index.html", 
								   false);
	// PopupGenerator.showErrorDialog(this.getClass().getName()+"\n"+"Cannot invoke BrowserLauncher when isApplet is null");
}


/**
 * Comment
 */
private void bngHelpFAQ() {
	PopupGenerator.browserLauncher("http://vcell.org/bionetgen/faq.html", 
								   "please visit : http://vcell.org/bionetgen/faq.html", 
								   false);
	// PopupGenerator.showErrorDialog(this.getClass().getName()+"\n"+"Cannot invoke BrowserLauncher when isApplet is null");
}


/**
 * Comment
 */
private void bngHelpManual() {
	PopupGenerator.browserLauncher("http://vcell.org/bionetgen/tutorial.html", 
								   "please visit : http://vcell.org/bionetgen/tutorial.html", 
								   false);
	// PopupGenerator.showErrorDialog(this.getClass().getName()+"\n"+"Cannot invoke BrowserLauncher when isApplet is null");
}


/**
 * Comment
 */
private void bngHelpSamples() {
	PopupGenerator.browserLauncher("http://vcell.org/bionetgen/samples.html", 
								   "please visit : http://vcell.org/bionetgen/samples.html", 
								   false);
	// PopupGenerator.showErrorDialog(this.getClass().getName()+"\n"+"Cannot invoke BrowserLauncher when isApplet is null");
}


/**
 * Comment
 */
public void changeBNGPanelTab() {
	getJTabbedPane1().setSelectedIndex(getJTabbedPane1().getSelectedIndex() + 1);
}


/**
 * connEtoC1:  (bngOutput1.this --> BNGOutputPanel.updateOutputFormatsList(Lcbit.vcell.server.bionetgen.BNGOutput;)V)
 * @param value cbit.vcell.server.bionetgen.BNGOutput
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.server.bionetgen.BNGOutput value) {
	try {
		// user code begin {1}
		// user code end
		this.updateOutputFormatsList(getbngOutput1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (JButtonManual1.action.actionPerformed(java.awt.event.ActionEvent) --> BNGOutputPanel.bngHelpSamples()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.bngHelpSamples();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (StopBNGButton.action.actionPerformed(java.awt.event.ActionEvent) --> BNGOutputPanel.stopBNGButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stopBNGButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (JButtonManual11.action. --> BNGOutputPanel.bngHelpFAQ()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12() {
	try {
		// user code begin {1}
		// user code end
		this.bngHelpFAQ();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (OutputFormatsList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> BNGOutputPanel.setOutputLabel()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setOutputLabel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ImportButton.action.actionPerformed(java.awt.event.ActionEvent) --> BNGOutputPanel.sbmlImportButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.sbmlImportButton_ActionPerformed();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (SBMLImportButton.action.actionPerformed(java.awt.event.ActionEvent) --> BNGOutputPanel.sBMLImportButton_ActionPerformed()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.saveOutput(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (OutputFormatsList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> BNGOutputPanel.enableImportButton()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableImportButton();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (BNGOutputPanel.initialize() --> BNGOutputPanel.enableImportButton()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.enableImportButton();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (RunBNGButton.action.actionPerformed(java.awt.event.ActionEvent) --> BNGOutputPanel.runBNGButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.runBNGButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (OutputFormatsList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> BNGOutputPanel.setTextArea(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTextArea(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (HelpButton.action.actionPerformed(java.awt.event.ActionEvent) --> BNGOutputPanel.bngHelp()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.bngHelpAbout();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC9:  (JButtonManual.action.actionPerformed(java.awt.event.ActionEvent) --> BNGOutputPanel.bngHelp1()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.bngHelpManual();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (OpenFileButton.action.actionPerformed(java.awt.event.ActionEvent) --> RuleInputTextArea.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getRuleInputTextArea().setText(this.uploadBnglFile());
		connEtoM2();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public java.lang.String connEtoM1_Value() {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < getBngOutput().getNumBNGFiles(); i ++) {
		sb.append("---------------------------------------------\n");
		sb.append(getBngOutput().getBNGFilenames()[i] + "\n");
		sb.append(getBngOutput().getBNGFileContent(i));
		sb.append("\n");
	}
	return sb.toString();
}


/**
 * connEtoM2:  ( (OpenFileButton,action.actionPerformed(java.awt.event.ActionEvent) --> RuleInputTextArea,text).normalResult --> RuleInputTextArea.setCaretPosition(I)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getRuleInputTextArea().setCaretPosition(0);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (bngInput.this --> RuleInputTextArea.text)
 * @param value bngclientserverapi.BNGInput
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.server.bionetgen.BNGInput value) {
	try {
		// user code begin {1}
		// user code end
		if ((getbngInput() != null)) {
			getRuleInputTextArea().setText(getbngInput().getInputString());
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
 * connPtoP1SetSource:  (BNGOutputPanel.bngOutput <--> bngOutput1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getbngOutput1() != null)) {
				this.setBngOutput(getbngOutput1());
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
 * connPtoP1SetTarget:  (BNGOutputPanel.bngOutput <--> bngOutput1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setbngOutput1(this.getBngOutput());
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
 * connPtoP2SetTarget:  (bngOutput1.consoleOutput <--> ConsoleTextArea.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if ((getbngOutput1() != null)) {
			getConsoleTextArea().setText(getbngOutput1().getConsoleOutput());
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
 * connPtoP3SetTarget:  (defaultListModel.this <--> OutputFormatsList.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getOutputFormatsList().setModel(getdefaultListModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void enableImportButton() {
	String selectedValue = (String)getOutputFormatsList().getSelectedValue();

	if (selectedValue == null) {
		getImportButton().setEnabled(false);
		return;
	}

	String XML_SUFFIX = ".xml";
	if (selectedValue.endsWith(XML_SUFFIX)) {
		getImportButton().setEnabled(true);
	} else {
		getImportButton().setEnabled(false);
	}
}


/**
 * Return the bngDataPlotPanel property value.
 * @return cbit.vcell.client.bionetgen.BNGDataPlotPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BNGDataPlotPanel getbngDataPlotPanel() {
	if (ivjbngDataPlotPanel == null) {
		try {
			ivjbngDataPlotPanel = new cbit.vcell.client.bionetgen.BNGDataPlotPanel();
			ivjbngDataPlotPanel.setName("bngDataPlotPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbngDataPlotPanel;
}


/**
 * Return the bngInput property value.
 * @return bngclientserverapi.BNGInput
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.server.bionetgen.BNGInput getbngInput() {
	// user code begin {1}
	// user code end
	return ivjbngInput;
}

/**
 * Gets the bngOutput property (bngclientserverapi.BNGOutput) value.
 * @return The bngOutput property value.
 * @see #setBngOutput
 */
public BNGOutput getBngOutput() {
	return fieldBngOutput;
}


/**
 * Return the bngOutput1 property value.
 * @return bngclientserverapi.BNGOutput
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.server.bionetgen.BNGOutput getbngOutput1() {
	// user code begin {1}
	// user code end
	return ivjbngOutput1;
}

/**
 * Gets the bngService property (cbit.vcell.server.bionetgen.BNGService) value.
 * @return The bngService property value.
 * @see #setBngService
 */
public cbit.vcell.server.bionetgen.BNGService getBngService() {
	return fieldBngService;
}


/**
 * Gets the bngWindowManager property (cbit.vcell.client.BNGWindowManager) value.
 * @return The bngWindowManager property value.
 * @see #setBngWindowManager
 */
public cbit.vcell.client.BNGWindowManager getBngWindowManager() {
	return fieldBngWindowManager;
}


private cbit.vcell.model.ReactionStep[] getCollapsedReactionSteps(cbit.vcell.model.ReactionStep[] reactionSteps) {
	Vector collapsedRxnStepsVector = new Vector();

	Vector rxnStepsVector = new Vector();
	for (int i = 0; i < reactionSteps.length; i++){
		rxnStepsVector.addElement(reactionSteps[i]);
	}
	
	for (int i = 0; i < rxnStepsVector.size(); i++){
		cbit.vcell.model.ReactionStep fwdRStep = (cbit.vcell.model.ReactionStep)rxnStepsVector.elementAt(i);
		// Get the reactionParticipants and the corresponding reactants and products in an array
		ReactionParticipant[] rps = fwdRStep.getReactionParticipants();
		Vector fwdReactantsVector = new Vector();
		Vector fwdProductsVector = new Vector();
		for (int j = 0; j < rps.length; j++){
			if (rps[j] instanceof cbit.vcell.model.Reactant) {
				fwdReactantsVector.addElement(rps[j].getSpeciesContext());
			} else if (rps[j] instanceof cbit.vcell.model.Product) {
				fwdProductsVector.addElement(rps[j].getSpeciesContext());
			}
		}
		SpeciesContext[] fwdReactants = (SpeciesContext[])cbit.util.BeanUtils.getArray(fwdReactantsVector, SpeciesContext.class);
		SpeciesContext[] fwdProducts = (SpeciesContext[])cbit.util.BeanUtils.getArray(fwdProductsVector, SpeciesContext.class);

		boolean bReverseReactionFound = false;

		// Loop through all the reactions to find the corresponding reverse reaction
		for (int ii = 0; ii < reactionSteps.length; ii++){
			cbit.vcell.model.ReactionStep revRStep = reactionSteps[ii];
			// Get the reactionParticipants and the corresponding reactants and products in an array
			ReactionParticipant[] revRps = revRStep.getReactionParticipants();
			Vector revReactantsVector = new Vector();
			Vector revProductsVector = new Vector();
			for (int j = 0; j < revRps.length; j++){
				if (revRps[j] instanceof cbit.vcell.model.Reactant) {
					revReactantsVector.addElement(revRps[j].getSpeciesContext());
				} else if (revRps[j] instanceof cbit.vcell.model.Product) {
					revProductsVector.addElement(revRps[j].getSpeciesContext());
				}
			}
			SpeciesContext[] revReactants = (SpeciesContext[])cbit.util.BeanUtils.getArray(revReactantsVector, SpeciesContext.class);
			SpeciesContext[] revProducts = (SpeciesContext[])cbit.util.BeanUtils.getArray(revProductsVector, SpeciesContext.class);

			// Check if reactants of reaction in outer 'for' loop match products in inner 'for' loop and vice versa.
			if (cbit.util.BeanUtils.arrayEquals(fwdReactants, revProducts) && cbit.util.BeanUtils.arrayEquals(fwdProducts, revReactants)) {
				// Set the reverse kinetic rate expression for the reaction in outer loop with the forward rate from reactionStep in inner loop
				cbit.vcell.model.MassActionKinetics revMAKinetics = (cbit.vcell.model.MassActionKinetics)revRStep.getKinetics(); // inner 'for' loop
				cbit.vcell.model.MassActionKinetics fwdMAKinetics = (cbit.vcell.model.MassActionKinetics)fwdRStep.getKinetics();  // outer 'for' loop
				try {
					fwdMAKinetics.setParameterValue(fwdMAKinetics.getReverseRateParameter().getName(), revMAKinetics.getForwardRateParameter().getExpression().infix());
					cbit.vcell.model.Parameter param = revMAKinetics.getParameter(revMAKinetics.getForwardRateParameter().getExpression().infix());
					fwdMAKinetics.setParameterValue(param.getName(), param.getExpression().infix());
				} catch (Exception e) {
					e.printStackTrace(System.out);
					throw new RuntimeException(e.getMessage());
				}

				// Add this to the collapsedRxnStepsVector
				collapsedRxnStepsVector.addElement(fwdRStep);
				rxnStepsVector.removeElement(revRStep);
				bReverseReactionFound = true;	
				break;	
			}
		}

		// If 'bReverseReactionFound' is false after checking all reactions for the reverse, the reaction is probably an irreversible reaction
		// Add it as is to the 'collapsedRxnStepsVector'
		if (!bReverseReactionFound) {
			collapsedRxnStepsVector.addElement(fwdRStep);
		}
	}

	// Convert the vector into an array of reactionSteps and return
	cbit.vcell.model.ReactionStep[] collapsedRxnSteps = (cbit.vcell.model.ReactionStep[])cbit.util.BeanUtils.getArray(collapsedRxnStepsVector, cbit.vcell.model.ReactionStep.class);
	return collapsedRxnSteps;
}


/**
 * Return the ConsoleOutputPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConsoleOutputPage() {
	if (ivjConsoleOutputPage == null) {
		try {
			ivjConsoleOutputPage = new javax.swing.JPanel();
			ivjConsoleOutputPage.setName("ConsoleOutputPage");
			ivjConsoleOutputPage.setLayout(new java.awt.BorderLayout());
			getConsoleOutputPage().add(getConsoleScrollPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConsoleOutputPage;
}

/**
 * Return the ConsoleScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getConsoleScrollPane() {
	if (ivjConsoleScrollPane == null) {
		try {
			ivjConsoleScrollPane = new javax.swing.JScrollPane();
			ivjConsoleScrollPane.setName("ConsoleScrollPane");
			getConsoleScrollPane().setViewportView(getConsoleTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConsoleScrollPane;
}


/**
 * Return the ConsoleTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getConsoleTextArea() {
	if (ivjConsoleTextArea == null) {
		try {
			ivjConsoleTextArea = new javax.swing.JTextArea();
			ivjConsoleTextArea.setName("ConsoleTextArea");
			ivjConsoleTextArea.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConsoleTextArea.setBounds(0, 0, 685, 501);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConsoleTextArea;
}

/**
 * Comment
 */
private DataSource getDataSource(String fileContent) {
	java.util.StringTokenizer tokenizer1 = new java.util.StringTokenizer(fileContent, "\n");

	cbit.vcell.solver.ode.ODESolverResultSet odeResultSet = new cbit.vcell.solver.ode.ODESolverResultSet();
	double[] values = null;
	boolean bcolNamesRead = false;
	while (tokenizer1.hasMoreTokens()) {	
		java.util.StringTokenizer tokenizer2 = new java.util.StringTokenizer(tokenizer1.nextToken(), ", \t\n\r\f");
		if (!bcolNamesRead) {
			bcolNamesRead = true;
			while (tokenizer2.hasMoreTokens()) {				
				String token = tokenizer2.nextToken();
				if (token.equals("#")) {
					continue;
				}
				if (token.equalsIgnoreCase("time")) {
					token = "t";
				}
				odeResultSet.addDataColumn(new ODESolverResultSetColumnDescription(token));
			}			
		} else {
			int i = 0;
			values = new double[odeResultSet.getColumnDescriptionsCount()];
			while (tokenizer2.hasMoreTokens()) {
				values[i ++] = Double.parseDouble(tokenizer2.nextToken());
			}
			odeResultSet.addRow(values);
		}
	}

	DataSource dataSource = new DataSource(odeResultSet, "");
		
	return dataSource;
}


/**
 * Return the defaultListModel property value.
 * @return javax.swing.DefaultListModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultListModel getdefaultListModel() {
	if (ivjdefaultListModel == null) {
		try {
			ivjdefaultListModel = new javax.swing.DefaultListModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjdefaultListModel;
}


/**
 * Return the HelpButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getHelpButton() {
	if (ivjHelpButton == null) {
		try {
			ivjHelpButton = new javax.swing.JButton();
			ivjHelpButton.setName("HelpButton");
			ivjHelpButton.setText("http://vcell.org/bionetgen/index.html");
			ivjHelpButton.setBackground(java.awt.Color.white);
			ivjHelpButton.setMaximumSize(new java.awt.Dimension(429, 27));
			ivjHelpButton.setForeground(java.awt.Color.blue);
			ivjHelpButton.setActionCommand("http://www.vcell.org/bionetgen/index.html");
			ivjHelpButton.setFont(new java.awt.Font("Arial", 1, 14));
			ivjHelpButton.setBounds(433, 81, 412, 27);
			ivjHelpButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpButton;
}

/**
 * Return the HelpPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getHelpPanel() {
	if (ivjHelpPanel == null) {
		try {
			ivjHelpPanel = new javax.swing.JPanel();
			ivjHelpPanel.setName("HelpPanel");
			ivjHelpPanel.setLayout(null);
			getHelpPanel().add(getHelpButton(), getHelpButton().getName());
			getHelpPanel().add(getJLabelAbout(), getJLabelAbout().getName());
			getHelpPanel().add(getJLabelTitle(), getJLabelTitle().getName());
			getHelpPanel().add(getJButtonManual(), getJButtonManual().getName());
			getHelpPanel().add(getJLabelHelp(), getJLabelHelp().getName());
			getHelpPanel().add(getJLabelStart(), getJLabelStart().getName());
			getHelpPanel().add(getJLabelStart1(), getJLabelStart1().getName());
			getHelpPanel().add(getJButtonManual1(), getJButtonManual1().getName());
			getHelpPanel().add(getJLabelHelp1(), getJLabelHelp1().getName());
			getHelpPanel().add(getJLabelStart11(), getJLabelStart11().getName());
			getHelpPanel().add(getJLabelStart12(), getJLabelStart12().getName());
			getHelpPanel().add(getJLabelStart121(), getJLabelStart121().getName());
			getHelpPanel().add(getJLabelStart122(), getJLabelStart122().getName());
			getHelpPanel().add(getJLabelStart1221(), getJLabelStart1221().getName());
			getHelpPanel().add(getJLabelStart2(), getJLabelStart2().getName());
			getHelpPanel().add(getJLabelStart122111(), getJLabelStart122111().getName());
			getHelpPanel().add(getJLabelStart1221111(), getJLabelStart1221111().getName());
			getHelpPanel().add(getJLabelHelp11(), getJLabelHelp11().getName());
			getHelpPanel().add(getJButtonManual11(), getJButtonManual11().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpPanel;
}

/**
 * Return the ImportButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getImportButton() {
	if (ivjImportButton == null) {
		try {
			ivjImportButton = new javax.swing.JButton();
			ivjImportButton.setName("ImportButton");
			ivjImportButton.setText("Create a Biomodel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImportButton;
}

/**
 * Return the InputScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getInputScrollPane() {
	if (ivjInputScrollPane == null) {
		try {
			ivjInputScrollPane = new javax.swing.JScrollPane();
			ivjInputScrollPane.setName("InputScrollPane");
			getInputScrollPane().setViewportView(getRuleInputTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInputScrollPane;
}

/**
 * Return the JButtonManual property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonManual() {
	if (ivjJButtonManual == null) {
		try {
			ivjJButtonManual = new javax.swing.JButton();
			ivjJButtonManual.setName("JButtonManual");
			ivjJButtonManual.setText("http://vcell.org/bionetgen/tutorial.html");
			ivjJButtonManual.setBackground(java.awt.Color.white);
			ivjJButtonManual.setForeground(java.awt.Color.blue);
			ivjJButtonManual.setActionCommand("http://vcell.org/bionetgen/tutorial.html");
			ivjJButtonManual.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJButtonManual.setBounds(428, 218, 417, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonManual;
}

/**
 * Return the JButtonManual1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonManual1() {
	if (ivjJButtonManual1 == null) {
		try {
			ivjJButtonManual1 = new javax.swing.JButton();
			ivjJButtonManual1.setName("JButtonManual1");
			ivjJButtonManual1.setText("http://vcell.org/bionetgen/samples.html");
			ivjJButtonManual1.setBackground(java.awt.Color.white);
			ivjJButtonManual1.setForeground(java.awt.Color.blue);
			ivjJButtonManual1.setActionCommand("http://vcell.org/bionetgen/samples.html");
			ivjJButtonManual1.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJButtonManual1.setBounds(430, 354, 417, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonManual1;
}

/**
 * Return the JButtonManual11 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonManual11() {
	if (ivjJButtonManual11 == null) {
		try {
			ivjJButtonManual11 = new javax.swing.JButton();
			ivjJButtonManual11.setName("JButtonManual11");
			ivjJButtonManual11.setText("http://vcell.org/bionetgen/faq.html");
			ivjJButtonManual11.setBackground(java.awt.Color.white);
			ivjJButtonManual11.setForeground(java.awt.Color.blue);
			ivjJButtonManual11.setActionCommand("http://vcell.org/bionetgen/faqhtml");
			ivjJButtonManual11.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJButtonManual11.setBounds(428, 600, 417, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonManual11;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAbout() {
	if (ivjJLabelAbout == null) {
		try {
			ivjJLabelAbout = new javax.swing.JLabel();
			ivjJLabelAbout.setName("JLabelAbout");
			ivjJLabelAbout.setFont(new java.awt.Font("dialog", 0, 18));
			ivjJLabelAbout.setText("About BioNetGen@Virtual Cell");
			ivjJLabelAbout.setBounds(25, 80, 243, 28);
			ivjJLabelAbout.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAbout;
}

/**
 * Return the JLabelHelp property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHelp() {
	if (ivjJLabelHelp == null) {
		try {
			ivjJLabelHelp = new javax.swing.JLabel();
			ivjJLabelHelp.setName("JLabelHelp");
			ivjJLabelHelp.setFont(new java.awt.Font("dialog", 0, 18));
			ivjJLabelHelp.setText("A tutorial on how to write BioNetGen input file.");
			ivjJLabelHelp.setBounds(30, 212, 385, 33);
			ivjJLabelHelp.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHelp;
}

/**
 * Return the JLabelHelp1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHelp1() {
	if (ivjJLabelHelp1 == null) {
		try {
			ivjJLabelHelp1 = new javax.swing.JLabel();
			ivjJLabelHelp1.setName("JLabelHelp1");
			ivjJLabelHelp1.setFont(new java.awt.Font("dialog", 0, 18));
			ivjJLabelHelp1.setText("Sample BioNetGen input files.");
			ivjJLabelHelp1.setBounds(32, 355, 333, 33);
			ivjJLabelHelp1.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHelp1;
}

/**
 * Return the JLabelHelp11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHelp11() {
	if (ivjJLabelHelp11 == null) {
		try {
			ivjJLabelHelp11 = new javax.swing.JLabel();
			ivjJLabelHelp11.setName("JLabelHelp11");
			ivjJLabelHelp11.setFont(new java.awt.Font("dialog", 0, 18));
			ivjJLabelHelp11.setText("BioNetGen@VCell FAQ.");
			ivjJLabelHelp11.setBounds(38, 591, 333, 33);
			ivjJLabelHelp11.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHelp11;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart() {
	if (ivjJLabelStart == null) {
		try {
			ivjJLabelStart = new javax.swing.JLabel();
			ivjJLabelStart.setName("JLabelStart");
			ivjJLabelStart.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart.setText("A BioNetGen input file is a plain-text file that specifies a model in the BioNetGen language (BNGL) and");
			ivjJLabelStart.setBounds(30, 139, 819, 32);
			ivjJLabelStart.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart;
}

/**
 * Return the JLabelStart1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart1() {
	if (ivjJLabelStart1 == null) {
		try {
			ivjJLabelStart1 = new javax.swing.JLabel();
			ivjJLabelStart1.setName("JLabelStart1");
			ivjJLabelStart1.setOpaque(false);
			ivjJLabelStart1.setText("You can select a model to start from the list below, download it to your computer, and modify.");
			ivjJLabelStart1.setDoubleBuffered(false);
			ivjJLabelStart1.setForeground(java.awt.Color.black);
			ivjJLabelStart1.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart1.setBounds(32, 280, 826, 28);
			ivjJLabelStart1.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart1;
}

/**
 * Return the JLabelStart11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart11() {
	if (ivjJLabelStart11 == null) {
		try {
			ivjJLabelStart11 = new javax.swing.JLabel();
			ivjJLabelStart11.setName("JLabelStart11");
			ivjJLabelStart11.setOpaque(false);
			ivjJLabelStart11.setText("After you modified a model, you can either upload it to BioNetGen, or copy- paste into Rules editor.");
			ivjJLabelStart11.setDoubleBuffered(false);
			ivjJLabelStart11.setForeground(java.awt.Color.black);
			ivjJLabelStart11.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart11.setBounds(29, 306, 826, 32);
			ivjJLabelStart11.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart11;
}

/**
 * Return the JLabelStart12 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart12() {
	if (ivjJLabelStart12 == null) {
		try {
			ivjJLabelStart12 = new javax.swing.JLabel();
			ivjJLabelStart12.setName("JLabelStart12");
			ivjJLabelStart12.setOpaque(false);
			ivjJLabelStart12.setText("To run BioNetGen, you will need to upload a BioNetGen input file or paste one into the Rules Editor window.");
			ivjJLabelStart12.setDoubleBuffered(false);
			ivjJLabelStart12.setForeground(java.awt.Color.black);
			ivjJLabelStart12.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart12.setBounds(30, 411, 826, 28);
			ivjJLabelStart12.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart12;
}

/**
 * Return the JLabelStart121 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart121() {
	if (ivjJLabelStart121 == null) {
		try {
			ivjJLabelStart121 = new javax.swing.JLabel();
			ivjJLabelStart121.setName("JLabelStart121");
			ivjJLabelStart121.setOpaque(false);
			ivjJLabelStart121.setText("Then, you can click on the Run button.  You may need to wait a few minutes for a response.  Error messages");
			ivjJLabelStart121.setDoubleBuffered(false);
			ivjJLabelStart121.setForeground(java.awt.Color.black);
			ivjJLabelStart121.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart121.setBounds(29, 440, 826, 28);
			ivjJLabelStart121.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart121;
}

/**
 * Return the JLabelStart122 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart122() {
	if (ivjJLabelStart122 == null) {
		try {
			ivjJLabelStart122 = new javax.swing.JLabel();
			ivjJLabelStart122.setName("JLabelStart122");
			ivjJLabelStart122.setOpaque(false);
			ivjJLabelStart122.setText("are reported in a pop-up window. If there are no error messages, you will be redirected to the Messages window, ");
			ivjJLabelStart122.setDoubleBuffered(false);
			ivjJLabelStart122.setForeground(java.awt.Color.black);
			ivjJLabelStart122.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart122.setBounds(29, 470, 826, 28);
			ivjJLabelStart122.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart122;
}

/**
 * Return the JLabelStart1221 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart1221() {
	if (ivjJLabelStart1221 == null) {
		try {
			ivjJLabelStart1221 = new javax.swing.JLabel();
			ivjJLabelStart1221.setName("JLabelStart1221");
			ivjJLabelStart1221.setOpaque(false);
			ivjJLabelStart1221.setText("where you will see a report about the results of processing your input file.  Results themselves are available in ");
			ivjJLabelStart1221.setDoubleBuffered(false);
			ivjJLabelStart1221.setForeground(java.awt.Color.black);
			ivjJLabelStart1221.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart1221.setBounds(30, 497, 826, 28);
			ivjJLabelStart1221.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart1221;
}

/**
 * Return the JLabelStart122111 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart122111() {
	if (ivjJLabelStart122111 == null) {
		try {
			ivjJLabelStart122111 = new javax.swing.JLabel();
			ivjJLabelStart122111.setName("JLabelStart122111");
			ivjJLabelStart122111.setOpaque(false);
			ivjJLabelStart122111.setText("in the Output window.  Please check the FAQ page for help  with error messages. If problem persists, please");
			ivjJLabelStart122111.setDoubleBuffered(false);
			ivjJLabelStart122111.setForeground(java.awt.Color.black);
			ivjJLabelStart122111.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart122111.setBounds(31, 522, 826, 28);
			ivjJLabelStart122111.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart122111;
}

/**
 * Return the JLabelStart1221111 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart1221111() {
	if (ivjJLabelStart1221111 == null) {
		try {
			ivjJLabelStart1221111 = new javax.swing.JLabel();
			ivjJLabelStart1221111.setName("JLabelStart1221111");
			ivjJLabelStart1221111.setOpaque(false);
			ivjJLabelStart1221111.setText("contact us at blinov@uchc.edu.");
			ivjJLabelStart1221111.setDoubleBuffered(false);
			ivjJLabelStart1221111.setForeground(java.awt.Color.black);
			ivjJLabelStart1221111.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart1221111.setBounds(28, 551, 826, 28);
			ivjJLabelStart1221111.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart1221111;
}

/**
 * Return the JLabelStart2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStart2() {
	if (ivjJLabelStart2 == null) {
		try {
			ivjJLabelStart2 = new javax.swing.JLabel();
			ivjJLabelStart2.setName("JLabelStart2");
			ivjJLabelStart2.setFont(new java.awt.Font("serif", 0, 18));
			ivjJLabelStart2.setText("instructions for operating on the model specification (e.g. running a simulation).");
			ivjJLabelStart2.setBounds(30, 165, 819, 32);
			ivjJLabelStart2.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStart2;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setFont(new java.awt.Font("dialog", 0, 24));
			ivjJLabelTitle.setText("All links will be opened in the same Internet browser window");
			ivjJLabelTitle.setBounds(105, 16, 671, 33);
			ivjJLabelTitle.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
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
			ivjJTabbedPane1.insertTab("Rules Editor", null, getRuleInputPage(), null, 0);
			ivjJTabbedPane1.insertTab("Messages", null, getConsoleOutputPage(), null, 1);
			ivjJTabbedPane1.insertTab("Output", null, getOutputPage(), null, 2);
			ivjJTabbedPane1.insertTab("Help", null, getHelpPanel(), null, 3);
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
 * Return the OpenFileButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOpenFileButton() {
	if (ivjOpenFileButton == null) {
		try {
			ivjOpenFileButton = new javax.swing.JButton();
			ivjOpenFileButton.setName("OpenFileButton");
			ivjOpenFileButton.setText("Open .bngl file");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpenFileButton;
}

/**
 * Return the OutputChoicesPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOutputChoicesPanel() {
	if (ivjOutputChoicesPanel == null) {
		try {
			ivjOutputChoicesPanel = new javax.swing.JPanel();
			ivjOutputChoicesPanel.setName("OutputChoicesPanel");
			ivjOutputChoicesPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutputListScrollPane = new java.awt.GridBagConstraints();
			constraintsOutputListScrollPane.gridx = 0; constraintsOutputListScrollPane.gridy = 0;
constraintsOutputListScrollPane.gridheight = 5;
			constraintsOutputListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutputListScrollPane.weightx = 1.0;
			constraintsOutputListScrollPane.weighty = 1.0;
			constraintsOutputListScrollPane.ipadx = 650;
			constraintsOutputListScrollPane.ipady = 350;
			constraintsOutputListScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getOutputChoicesPanel().add(getOutputListScrollPane(), constraintsOutputListScrollPane);

			java.awt.GridBagConstraints constraintsSaveButton = new java.awt.GridBagConstraints();
			constraintsSaveButton.gridx = 1; constraintsSaveButton.gridy = 3;
			constraintsSaveButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSaveButton.weightx = 1.0;
			constraintsSaveButton.weighty = 1.0;
			constraintsSaveButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getOutputChoicesPanel().add(getSaveButton(), constraintsSaveButton);

			java.awt.GridBagConstraints constraintsImportButton = new java.awt.GridBagConstraints();
			constraintsImportButton.gridx = 1; constraintsImportButton.gridy = 4;
			constraintsImportButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsImportButton.weightx = 1.0;
			constraintsImportButton.weighty = 1.0;
			constraintsImportButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getOutputChoicesPanel().add(getImportButton(), constraintsImportButton);

			java.awt.GridBagConstraints constraintsOutputLabel = new java.awt.GridBagConstraints();
			constraintsOutputLabel.gridx = 0; constraintsOutputLabel.gridy = 5;
			constraintsOutputLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getOutputChoicesPanel().add(getOutputLabel(), constraintsOutputLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputChoicesPanel;
}

/**
 * Return the OutputFormatsList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getOutputFormatsList() {
	if (ivjOutputFormatsList == null) {
		try {
			ivjOutputFormatsList = new javax.swing.JList();
			ivjOutputFormatsList.setName("OutputFormatsList");
			ivjOutputFormatsList.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOutputFormatsList.setBounds(0, 0, 772, 200);
			ivjOutputFormatsList.setSelectedIndex(0);
			ivjOutputFormatsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputFormatsList;
}

/**
 * Return the OutputLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOutputLabel() {
	if (ivjOutputLabel == null) {
		try {
			ivjOutputLabel = new javax.swing.JLabel();
			ivjOutputLabel.setName("OutputLabel");
			ivjOutputLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjOutputLabel.setText(" ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputLabel;
}

/**
 * Return the OutputListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getOutputListScrollPane() {
	if (ivjOutputListScrollPane == null) {
		try {
			ivjOutputListScrollPane = new javax.swing.JScrollPane();
			ivjOutputListScrollPane.setName("OutputListScrollPane");
			getOutputListScrollPane().setViewportView(getOutputFormatsList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputListScrollPane;
}


/**
 * Return the NetworkOutputPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOutputPage() {
	if (ivjOutputPage == null) {
		try {
			ivjOutputPage = new javax.swing.JPanel();
			ivjOutputPage.setName("OutputPage");
			ivjOutputPage.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutputChoicesPanel = new java.awt.GridBagConstraints();
			constraintsOutputChoicesPanel.gridx = 0; constraintsOutputChoicesPanel.gridy = 0;
			constraintsOutputChoicesPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutputChoicesPanel.weightx = 1.0;
			constraintsOutputChoicesPanel.weighty = 1.0;
			getOutputPage().add(getOutputChoicesPanel(), constraintsOutputChoicesPanel);

			java.awt.GridBagConstraints constraintsOutputsTextPanel = new java.awt.GridBagConstraints();
			constraintsOutputsTextPanel.gridx = 0; constraintsOutputsTextPanel.gridy = 1;
			constraintsOutputsTextPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutputsTextPanel.weightx = 1.0;
			constraintsOutputsTextPanel.weighty = 1.0;
			constraintsOutputsTextPanel.ipadx = 780;
			constraintsOutputsTextPanel.ipady = 650;
			getOutputPage().add(getOutputsTextPanel(), constraintsOutputsTextPanel);

			java.awt.GridBagConstraints constraintsOutputWarningLabel = new java.awt.GridBagConstraints();
			constraintsOutputWarningLabel.gridx = 0; constraintsOutputWarningLabel.gridy = 2;
			constraintsOutputWarningLabel.weightx = 1.0;
			constraintsOutputWarningLabel.weighty = 1.0;
			constraintsOutputWarningLabel.ipadx = 780;
			constraintsOutputWarningLabel.ipady = 200;
			constraintsOutputWarningLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getOutputPage().add(getOutputWarningLabel(), constraintsOutputWarningLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputPage;
}

/**
 * Return the OutputsTextPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOutputsTextPanel() {
	if (ivjOutputsTextPanel == null) {
		try {
			ivjOutputsTextPanel = new javax.swing.JPanel();
			ivjOutputsTextPanel.setName("OutputsTextPanel");
			ivjOutputsTextPanel.setLayout(new java.awt.CardLayout());
			getOutputsTextPanel().add(getOutputTextScrollPane(), getOutputTextScrollPane().getName());
			getOutputsTextPanel().add(getbngDataPlotPanel(), getbngDataPlotPanel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputsTextPanel;
}

/**
 * Return the OutputTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getOutputTextArea() {
	if (ivjOutputTextArea == null) {
		try {
			ivjOutputTextArea = new javax.swing.JTextArea();
			ivjOutputTextArea.setName("OutputTextArea");
			ivjOutputTextArea.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOutputTextArea.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputTextArea;
}

/**
 * Return the OutputTextScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getOutputTextScrollPane() {
	if (ivjOutputTextScrollPane == null) {
		try {
			ivjOutputTextScrollPane = new javax.swing.JScrollPane();
			ivjOutputTextScrollPane.setName("OutputTextScrollPane");
			getOutputTextScrollPane().setViewportView(getOutputTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputTextScrollPane;
}


/**
 * Return the OutputWarningLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOutputWarningLabel() {
	if (ivjOutputWarningLabel == null) {
		try {
			ivjOutputWarningLabel = new javax.swing.JLabel();
			ivjOutputWarningLabel.setName("OutputWarningLabel");
			ivjOutputWarningLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjOutputWarningLabel.setText("<html>WARNING: These files will not be saved in the Virtual Cell repository.  If desired, you should save these files in your home directory.  You might want to save only the .bngl file, because the other files can be generated from it.\n</html>");
			ivjOutputWarningLabel.setForeground(java.awt.Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputWarningLabel;
}

/**
 * Return the RuleInputPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRuleInputPage() {
	if (ivjRuleInputPage == null) {
		try {
			ivjRuleInputPage = new javax.swing.JPanel();
			ivjRuleInputPage.setName("RuleInputPage");
			ivjRuleInputPage.setPreferredSize(new java.awt.Dimension(612, 1990));
			ivjRuleInputPage.setLayout(new java.awt.BorderLayout());
			getRuleInputPage().add(getRulesEditorButtonsPanel1(), "South");
			getRuleInputPage().add(getInputScrollPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRuleInputPage;
}

/**
 * Return the RuleInputTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getRuleInputTextArea() {
	if (ivjRuleInputTextArea == null) {
		try {
			ivjRuleInputTextArea = new javax.swing.JTextArea();
			ivjRuleInputTextArea.setName("RuleInputTextArea");
			ivjRuleInputTextArea.setFont(new java.awt.Font("monospaced", 0, 18));
			ivjRuleInputTextArea.setText("# All text following the occurence of \'#\' character in a line is ignored.\n \n# The model consists of a monovalent extracellular ligand, \n# a monovalent cell-surface receptor kinase, and a cytosolic adapter \n# protein. The receptor dimerizes through a receptor-receptor \n# interaction that depends on ligand binding. When two receptors \n# are juxtaposed through dimerization one of the receptor kinases \n# can transphosphorylate the second receptor kinase. \n# Apapter protein A can bind to phosphorylated receptor tyrosine. \n\n\nbegin parameters\n  1 L0   1\n  2 R0   1\n  3 A0   5\n  4 kp1  0.5\n  5 km1  0.1\n  6 kp2  1.1\n  7 km2  0.1\n  8 p1  10\n  9 d1   5\n 10 kpA  1e1\n 11 kmA  0.02\nend parameters\n\nbegin species\n  1  L(r)       L0  # Ligand has one site for binding to receptor. \n                    # L0 is initial concentration\n  2  R(l,d,Y~U) R0  # Dimer has three sites: l for binding to a ligand, \n                    # d for binding to another receptor, and\n                    # Y - tyrosine. Initially Y is unphosphorylated, Y~U.\n  3  A(SH2)     A0  # A has a single SH2 domain that binds phosphotyrosine\nend species\n\n\nbegin reaction rules\n\n# Ligand binding (L+R)\n# Note: specifying r in R here means that the r component must not \n#       be bound.  This prevents dissociation of ligand from R\n#       when R is in a dimer.\n  1  L(r) + R(l,d) <-> L(r!1).R(l!1,d) kp1, km1\n\n# Aggregation (R-L + R-L)\n# Note:  R must be bound to ligand to dimerize.\n  2  R(l!+,d) + R(l!+,d) <-> R(l!+,d!2).R(l!+,d!2) kp2, km2\n\n# Transphosphorylation\n# Note:  R must be bound to another R to be transphosphorylated.\n  3  R(d!+,Y~U) -> R(d!+,Y~P) p1 \n\n# Dephosphorylation\n# Note:  R can be in any complex, but tyrosine is not protected by bound A.\n  4  R(Y~P) -> R(Y~U) d1\n\n# Adaptor binding phosphotyrosine (reversible). \n# Note: Doesn\'t depend on whether R is bound to\n#       receptor, i.e. binding rate is same whether R is a monomer, is \n#       in association with a ligand, in a dimer, or in a complex.\n\n  5  R(Y~P) + A(SH2) <-> R(Y~P!1).A(SH2!1) kpA, kmA\nend reaction rules\n\nbegin observables\n  Molecules R_dim  R(d!+)      # All receptors in dimer\n  Molecules R_phos R(Y~P!?)    # Total of all phosphotyrosines\n  Molecules A_R    A(SH2!1).R(Y~P!1) # Total of all A\'s associated with phosphotyrosines\n  Molecules A_tot  A()     # Total of A. Should be a constant during simulation.\n  Molecules R_tot  R()     # Total of R. Should be a constant during simulation.\n  Molecules L_tot  L()     # Total of L. Should be a constant during simulation.\nend observables\n\n\ngenerate_network();\nwriteSBML();\nsimulate_ode({t_end=>50,n_steps=>20});\n\n# Print concentratons at unevenly spaced times (array-valued parameter)\n#simulate_ode({sample_times=>[1,10,100]});\n");
			ivjRuleInputTextArea.setBounds(0, 0, 873, 620);
			ivjRuleInputTextArea.setMinimumSize(new java.awt.Dimension(609, 1952));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRuleInputTextArea;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRulesEditorButtonsPanel1() {
	if (ivjRulesEditorButtonsPanel1 == null) {
		try {
			ivjRulesEditorButtonsPanel1 = new javax.swing.JPanel();
			ivjRulesEditorButtonsPanel1.setName("RulesEditorButtonsPanel1");
			ivjRulesEditorButtonsPanel1.setLayout(new java.awt.FlowLayout());
			getRulesEditorButtonsPanel1().add(getOpenFileButton(), getOpenFileButton().getName());
			getRulesEditorButtonsPanel1().add(getRunBNGButton(), getRunBNGButton().getName());
			getRulesEditorButtonsPanel1().add(getStopBNGButton(), getStopBNGButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRulesEditorButtonsPanel1;
}

/**
 * Return the RunBNGButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRunBNGButton() {
	if (ivjRunBNGButton == null) {
		try {
			ivjRunBNGButton = new javax.swing.JButton();
			ivjRunBNGButton.setName("RunBNGButton");
			ivjRunBNGButton.setText("Run BioNetGen");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunBNGButton;
}


/**
 * Return the SaveButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSaveButton() {
	if (ivjSaveButton == null) {
		try {
			ivjSaveButton = new javax.swing.JButton();
			ivjSaveButton.setName("SaveButton");
			ivjSaveButton.setFont(new java.awt.Font("Arial", 1, 12));
			ivjSaveButton.setText("Save Output as text file");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveButton;
}

/**
 * Comment
 */
public String getSelectedOutputFileName() {
	return (String)getOutputFormatsList().getSelectedValue();
}


/**
 * Return the StopBNGButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStopBNGButton() {
	if (ivjStopBNGButton == null) {
		try {
			ivjStopBNGButton = new javax.swing.JButton();
			ivjStopBNGButton.setName("StopBNGButton");
			ivjStopBNGButton.setText("Stop BioNetGen");
			ivjStopBNGButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopBNGButton;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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
	this.addPropertyChangeListener(ivjEventHandler);
	getRunBNGButton().addActionListener(ivjEventHandler);
	getImportButton().addActionListener(ivjEventHandler);
	getSaveButton().addActionListener(ivjEventHandler);
	getOutputFormatsList().addListSelectionListener(ivjEventHandler);
	getHelpButton().addActionListener(ivjEventHandler);
	getJButtonManual().addActionListener(ivjEventHandler);
	getJButtonManual1().addActionListener(ivjEventHandler);
	getJButtonManual11().addActionListener(ivjEventHandler);
	getOpenFileButton().addActionListener(ivjEventHandler);
	getStopBNGButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BNGOutputPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(881, 681);
		add(getJTabbedPane1(), "Center");
		initConnections();
		connEtoC5();
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
		BNGOutputPanel aBNGOutputPanel;
		aBNGOutputPanel = new BNGOutputPanel();
		frame.setContentPane(aBNGOutputPanel);
		frame.setSize(aBNGOutputPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (9/12/2006 2:11:00 PM)
 */
public void refreshButton(boolean bRunning) {
	if (bRunning) {
		getOpenFileButton().setEnabled(false);
		getRunBNGButton().setEnabled(false);
		getStopBNGButton().setEnabled(true);
	} else {
		getOpenFileButton().setEnabled(true);
		getRunBNGButton().setEnabled(true);
		getStopBNGButton().setEnabled(false);		
	}
}


/**
 * Comment
 */
private void runBNGButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getConsoleTextArea().setText("");
	getOutputTextArea().setText("");
	getdefaultListModel().removeAllElements();

	refreshButton(true);
	
	// Do all the text checks here; and pop up warnings if needed, before going in to the BNGWindowManager to execute BNG.
	if (getRuleInputTextArea().getText() == null || getRuleInputTextArea().getText().equals("")) {
		cbit.vcell.client.PopupGenerator.showErrorDialog("No input; Cannot run BioNetGen");
		return;
	}
	setbngInput(new BNGInput(getRuleInputTextArea().getText()));
	
	if (getbngInput() == null) {
		cbit.vcell.client.PopupGenerator.showErrorDialog("No input; Cannot run BioNetGen");
		return;
	}

	// execute BNG thro' BNGWindowManager
	getBngWindowManager().runBioNetGen(getbngInput());

	// Clear the Output tab
	((java.awt.CardLayout)getOutputsTextPanel().getLayout()).show(getOutputsTextPanel(), getOutputTextScrollPane().getName());
	getOutputTextArea().setText("");
	getOutputLabel().setText("");
	
}


/**
 * Comment
 */
private void saveOutput(java.awt.event.ActionEvent actionEvent) {
	int listSelectionIndex = (int)getOutputFormatsList().getSelectedIndex();
	if (listSelectionIndex == -1) {
		return;
	}
	String outputTextStr = getbngOutput1().getBNGFileContent(listSelectionIndex);
	getBngWindowManager().saveOutput(outputTextStr);
}


/**
 * Comment
 */
private void sbmlImportButton_ActionPerformed() {
	String selectedValue = (String)getOutputFormatsList().getSelectedValue();
	if (selectedValue == null) {
		PopupGenerator.showErrorDialog("No output format selected.");
		return;
	}

	String XML_SUFFIX = ".xml";
	if (selectedValue.endsWith(XML_SUFFIX)) {
		String sbmlStr = getOutputTextArea().getText();
		getBngWindowManager().importSbml(sbmlStr);
	} else {
		PopupGenerator.showErrorDialog("SBML (.xml) file not selected; cannot import into the Virtual Cell.");
	}
}


/**
 * Set the bngInput to a new value.
 * @param newValue bngclientserverapi.BNGInput
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setbngInput(cbit.vcell.server.bionetgen.BNGInput newValue) {
	if (ivjbngInput != newValue) {
		try {
			ivjbngInput = newValue;
			connEtoM3(ivjbngInput);
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
 * Sets the bngOutput property (bngclientserverapi.BNGOutput) value.
 * @param bngOutput The new value for the property.
 * @see #getBngOutput
 */
public void setBngOutput(BNGOutput bngOutput) {
	BNGOutput oldValue = fieldBngOutput;
	fieldBngOutput = bngOutput;
	firePropertyChange("bngOutput", oldValue, bngOutput);
}


/**
 * Set the bngOutput1 to a new value.
 * @param newValue bngclientserverapi.BNGOutput
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setbngOutput1(cbit.vcell.server.bionetgen.BNGOutput newValue) {
	if (ivjbngOutput1 != newValue) {
		try {
			cbit.vcell.server.bionetgen.BNGOutput oldValue = getbngOutput1();
			ivjbngOutput1 = newValue;
			connPtoP1SetSource();
			connEtoC1(ivjbngOutput1);
			connPtoP2SetTarget();
			firePropertyChange("bngOutput", oldValue, newValue);
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
 * Sets the bngService property (cbit.vcell.server.bionetgen.BNGService) value.
 * @param bngService The new value for the property.
 * @see #getBngService
 */
public void setBngService(cbit.vcell.server.bionetgen.BNGService bngService) {
	cbit.vcell.server.bionetgen.BNGService oldValue = fieldBngService;
	fieldBngService = bngService;
	firePropertyChange("bngService", oldValue, bngService);
}


/**
 * Sets the bngWindowManager property (cbit.vcell.client.BNGWindowManager) value.
 * @param bngWindowManager The new value for the property.
 * @see #getBngWindowManager
 */
public void setBngWindowManager(cbit.vcell.client.BNGWindowManager bngWindowManager) {
	cbit.vcell.client.BNGWindowManager oldValue = fieldBngWindowManager;
	fieldBngWindowManager = bngWindowManager;
	firePropertyChange("bngWindowManager", oldValue, bngWindowManager);
}


/**
 * Comment
 */
private void setOutputLabel() {
	int listSelectionIndex = (int)getOutputFormatsList().getSelectedIndex();

	if (listSelectionIndex == -1) {
		return;
	}

	if (((String)getOutputFormatsList().getSelectedValue()).endsWith("bngl")) {
		getOutputLabel().setText("BioNetGen Input File");
	} else if (((String)getOutputFormatsList().getSelectedValue()).endsWith("cdat")) {
		getOutputLabel().setText("Concentration of Species");
	} else if (((String)getOutputFormatsList().getSelectedValue()).endsWith("net")) {
		getOutputLabel().setText("Biochemical Reactions Network File");
	} else if (((String)getOutputFormatsList().getSelectedValue()).endsWith("xml")) {
		getOutputLabel().setText("SBML Level 2 File");
	} else if (((String)getOutputFormatsList().getSelectedValue()).endsWith("gdat")) {
		getOutputLabel().setText("Concentration of Observables");
	} 
}


/**
 * Comment
 */
private void setTextArea(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	int listSelectionIndex = (int)getOutputFormatsList().getSelectedIndex();

	if (listSelectionIndex == -1) {
		return;
	}

	if (((String)getOutputFormatsList().getSelectedValue()).endsWith("dat")) {
		((java.awt.CardLayout)getOutputsTextPanel().getLayout()).show(getOutputsTextPanel(), getbngDataPlotPanel().getName());
		String fileContentStr = getbngOutput1().getBNGFileContent(listSelectionIndex);

		// Read the data from the cdat/gdat file contents to create a data source for the bngDataPlotPane
		DataSource dataSource = getDataSource(fileContentStr);
		getbngDataPlotPanel().setDataSource(dataSource);
		getbngDataPlotPanel().selectAll();
	} else {
		((java.awt.CardLayout)getOutputsTextPanel().getLayout()).show(getOutputsTextPanel(), getOutputTextScrollPane().getName());
		String fileContentStr = getbngOutput1().getBNGFileContent(listSelectionIndex);
		getOutputTextArea().setText(fileContentStr);
	}
}


/**
 * Comment
 */
public void stopBNGButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	// execute BNG thro' BNGWindowManager
	getBngWindowManager().stopBioNetGen();	

	refreshButton(false);	
}


/**
 * Comment
 */
private void updateOutputFormatsList(BNGOutput argBngO) {
	String[] bngFileNames = argBngO.getBNGFilenames();
	// remove elements from list, if any.
	getdefaultListModel().removeAllElements();

	// Add file names from BNGOutput
	for (int i = 0; i < bngFileNames.length; i++){
		getdefaultListModel().addElement(bngFileNames[i]);
	}	
}


/**
 * Comment
 */
private java.lang.String uploadBnglFile() {
	String bnglFileStr = null;
	try {
		bnglFileStr = getBngWindowManager().uploadBNGLFile();
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Could not get BNGL input string : " + e.getMessage());
	}
	return bnglFileStr;
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GACFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8DFCD44535F8D4D42AD13122C60BAD5A5407B6D634D831D20D1A5A2831E2C41B3628D1232695AD3EC7DFD39F2D17A4C09282840892AB6A2A34A6A504FC81A18910AFA0418D04A4C082C132495EE497EEF6175D3BC950287FB3B3774C7D583B9B1216775AFFFE9C6E4E19B3E74EB9F366734E4C95F2BF183AF8D1EA2EA0ACDAACA8FFDB9789C247FF8A423D8F1F3E88F1951F44E78A1A3F8F81D6898F
	FC1D8E4FC6081B1977C4E60EB0636B8C88F7C1F8627C784C3570FB0DF059D64BC370A3C8BEA9310290DB71529FC367B3E591663314062F5E178D4F95G5D00A34FA8F86688376E4BD3709394BC4222D4C11818CFE89E5E37CE414D871A8C40B5GF464B3FA8FBC19G67D7D6A8F4DD17CF9396BC6CCDDDCE78E8F8A24CD652AED01E96610DC5D38AC8DE7FC565A4E98AA1EC87A04A27895F7E38981E55ADA77BF77476B8652241DED1120AFAA52768120BFA1CEE17A8770B2E4A4A3AFDFB767BE50FDFEE3139C4A9
	6F6F04E7557E8C4AC3909201DFA445D94BC8BEDD70DB86887FBB620FEFA678E478ADEC084F4C5B003AED7F24DA113D630BBB05B9258369AB0368D63C01695431C1554D63FABA759DDA8EF3D3093DA7C35CCC00B80079GB905711945G6F5272FFE1515F61D95BA3573B87865C2EDA0F27CE32FB1B5DF6D1025F1515902360DA45DE3757AE882C0C7F382F50C41ED04052AD5DBF56F44CA6534A303C23DAFFA04C7D61D529CBCDECB2F75A97290D054C965D052ACDD85EB5C1738EAB547B23BE6F4302B66F588B1E
	CADBEC12F7126F2934498A1FE91A3C138AC9D9C7D39F08055F0B943E8C7FA84567EFD4714CB7DBA91E049BA0EE5AC6740D0E95A80BB57D86A12764EAD3BBC45C9F1E1ADD44E4D8DFE4146562AD04F7CB914BF3831765A60A1F2E41B3D9C46A27A41C33A9BED3AA0E4FE47E75E2BA292B31A57119C9G8B81F2CB63B34B81DAG6C2568E305291B4E5047DAECDE394E6F146C22D7E0E9DFCCFD9B1E62306C35B539FC324D55AB9E32F91D369EC974898A4DEB5486BD3AF47FC5FD3F977258A33AC42FCD06EA8B5CED
	8322ACFAFD0CE6E16AAAC26395E9EC3BF3C0048C86BC8272CDCFDB83CF4966138FFA6CB6D9D4A9D8FCF69AA913C15BA160888C60B7334B6AA544DE93C07FA9GD15C8E97D2FE1F0ADEE8ADAAAB2DEE77C93F271DE8A2ACACA5F6BECC5B0EE8781DD00AB6DE311460EA4A208DACC3BE77242D5670E9F0769271ED5E53D8479EAC4BA1F2837DC40029GB34A50FFDE6BC07FE9D8FE0BD0786415267E53A1BF15B63B0C79CDF219319EBDC1799399F34A21AD81E8A947363F232CA3C89D1BDA262FE312A6BC23AC307E
	CDB44131BCCE854DE329A11CE6284BC67A45A6B8E677118CD2069E50AB9CA02A9CED7F47B48B7365BA1BCFC46F5D6F8547936DEAC9B0B9AF5F522E5876AD0077GBE90D81FC9645AC6E984619300820035CA5E9B957FAB81588C70B140DF812AG7683EC67F63815720F8159668164EE0E4F2C86308304834CG188B103EA5BE3390209DE084A092A08EE081403A2D503F82C8G1381E681A4ED4532BC3939E59113DFEBC7A32CD21EEFE9F4F85720AF6A003A2ED074DC216839C251F505E217AAC54F517A067DE7
	C0732F864C3677D35B923BAEBF835BAE565876BD05877167E59A5B2ED14AE005E27F950A7DD7A8E5C01EAB15B258F086F2961C814D3F9A30BA7ABBBADE9287E13856A0FAC4179DFE7814387FCEA7F6D37A13BEB7CF331062BD507F54C3036F63785445248CEC326CF57678E531CD14E5272BDF0D4FDACC727279FBBCEA5705F92DDACC4A4DBD847D8F27FFFBB1E93BFB5DAE17C89B8B1EC4094F23FD07A80983A0F43D5B6F12997E7DA5E40CFDD2BC2D74DD9FADD9A2103EC77213FE100DDB275B08BFF83C4E81E8
	02C8D7AAF4D510FE4535BFCFE323EC401171F7B1BC9BAA30DF987A987B05751F5FA4C49F77241A0DF1A23EF7D9EAE485EB17E7D6287D8213E94DD2F3595F21FD180277B1D91DEE12D6BF02E675E10DF0DEC596BCFBDC7DAC2E8471EB5F4EE078A69720996E153524AF320BFDB63FA4DB1DBE198EABD89BFEC7A611A781641C87103E8D5B70AB1712F86CBF15463B5EADC92CC8A02EE19B315BBB65390A5E6BA0DC3E0D59ED850F7F890F8F07F024A6BE3B124417ED4E5558EE2EA61E49F1EE86115DA71E720BB02C
	A18301CFF979FEA3E335A16E10D256AC7E728CD25612E68CB1196639E08B4E1F5AABD9FED2A56B27A74073FD40F7D4913AFB36A2DD22921F2250E7D4B19997D23A418A240B2CE2FD7DACF846814CD368422B89DD43B624EB2CE6FDD1F7B5231761D944737DB327CBG7CFC12F6BB4B37E5BB634774383F9269A236B3792643B38EE05EF62687233B228A6996EEE7639022C76E8EA82F9DDAFDBF2B6672813E9BA0EC874BB7FA07B20722F479EDFC7E89780C1DAADD7ECEAD5DDCCE573D1365B73C13514568683E5B
	0EF449CA3CD0E3C6F76CA052655568799557E869D69DC2BA8F60A782CCD378A56B681E64F46935504F83D457B23A11DAAD5DB507391D81BFDDC3B7DBC7575C416D82780CBA30C79D232B2E537A4B12CE246BG3C87E0D29D7317598ADDFEBD21CB64F409F5EC8CC27C0570CB5171BB77885249750CAE3C1E710B04E78CG136FFBC739DF81EEBE4923C1712B862D9E4EAE2EEF833373F405EE21C2475A277FAAA0EDA6DD83B8E19B348D977906205D2F2C5C2B196BA75071BA21DF378B677469ED244DE9073084B0
	E197362F0FAF47B97D64135304488B1FCC8B36DEB1E59713A3FA175A3EAE7845D09AEB53BAE9DBAA6C063CF6E3FB161D291DD7547AFC6EDEA71D52A8ED455D195A76176122B3C99B5B67747A64A668A00731DDF9104ACB66BC8ADA19835614E78B2C9DDE8F79F681CCA6B288DF1613FE0934B1CC4F5EB254F352675F933A2F3CB6F5D990BD93F6B37DD2F62BFA469C5C1C4672CBAA47351962BD71190EBD38B67344E7FF978257E6FC22F7D0749A56E6E8AF0165F9317811E9F9B698FA8E1C73FF39DBDBDE33D6FE
	99D40F29FB14725A23D9DF6A7D032247FBDB51EEC2E3FCE685G6B372EA8A77970BECE40BEEE5AD6E2CF596174B1B97E3336C649A76E87B788B31706271A2D85B97EA3A2555348720FE8D465E8C9FABA558E40E4F9194AC26C118CB416A614E5DF856649EC55D0D58BCFBF1D73B271F63B3D83B659C72C14EF8DE177A25E21D7959C925016B72C516AD07D75E726BACCDC69C86DE8E23207B529BA581FFCBAF578C95461BB95E84FB820C95F0BBAAC5906BA303E7797595A3E379A686CG93B9FD91230701A01371
	167735B2E6584EB715B17CB77D29D376B259E2762AB24601FC09DC4601EDA8E3762750E6FD0A3E1B3B03BC69180D1777CB5B08DFB9092B36419853552F58EC249365890B1BA1642D3944D49E82A31FB2B9A2BFD56509F86169542897D01ECFAA315C97GCD21B5BE1359C84C545A488E78F00069D63451251525703451C1D191FB34085EBE7085511ECFEAD111EDC8AE22636C22DA9A3D0B7CAE40BAF7DEA71947F97DAE700ABA3FAC3BDD47EB8DDC2C4159908FE2723FD209760C815996F279DA2BC89B8AE3E91F
	DB92F9BDE23A353A0D65DF5E8CFDC5B3267D328A4B1F65B12B8A731000A63299ED94DB219B4381DE58971F39FE9F7259D3CDF49DE0CD07A26FA52DE86F8A20736CC3DEFF2EB45AFB9A4445F1DE8BDA7FB1FBCB96B29774758CC8CD839EB7190E185A1C5B75C09B4A1988B2646DC7B96FB9E4A6E74BFE7463DFDB485C50E79BD40CBEDAA922DD9637E3FEF660B5E5BF6AAD3593DF1720F1E99315B1F8912DA5758E1B2BDF51DDE931159C2CDAE2E685A65B2F9611B93068A2C3E22D9572B5F244F1B966901DDBD00E
	32035934AEBAE527CDF2FEAE6697B07A8B8EA2FD83500E34209D6E58D1CE6CA04A6D60C635DE51468CB11A24E09246377290721D81BC6735207DEF150D76DFFFA0BE3371G4AF97D21FF4CCF569DA7ED8974BD0D2264296DF1C389F21F78B317298B721BFCGE532EEB113A90E4BA49E7E47E4EA766628B2B55BDCFE1B140FF63A7FB077D348AF2F95E52A2CB3132925954B6759F52BD41EED36810FA47A34FEB058417D9152CC61FC3F58E046F7CEAB6A7AF447D2D5D7C57EAF8EADD7735ADDFBC01B4F26CEECDB
	120107250D6713E1567EF634E1BED7F50E5726AC7D431D7AB6AE73886AABC19E11EDE8275F551131115FA339ED76BAD73F345BA9016F33CAADC18DACE2BDD39576677F739E76CD6B9BA2050A85A70376CDF35BD81F145E2E76CD8DAFEECE6BF89147F95F67B23583CDF8BB4A3463G413B4838C3EA95FDB0C4E378BFEC4BE5F52E5E66956596374FC9744EEFAAB84446641B0E627B37986045727879D14CE3A1604A8F227D77E71959DFBA08765F5C750F79745B9F11B124CFF6FB465B81B2793F5905720701ECB3
	39FCFF6C7A67351B378F2CE2F6E5CD3A5556A3CA4C0759FA6553816F4E669FD45767538F852E5533F7CA43340F49BB445EA925F17C39941F2F41B3DD335A302ED4C05C14C3789EA4B60B4C470443B01785A8873082C8G6107F19E34AF2BCC9865BDD313CB06E912CDEA53AD58A9F31F4F1612FCCF0A2715F04FD26DF80D59E1F7B931433443666F8B4D0025EB27692247100E553502A35A775C692B7626268529EB73955EC207DA57186DCE5331F08D603BGE400F000288E345B4BCB8BC7339B3EF3D38C46F47A
	ED79785E81270CB99D53635965C40FB810BB99C068043EG20229375A84998357CF78D1B16BF136B632D2349555129AFBF162EE454F4A306F4CC1F4DAB49DA44E4081B8E908B108410F1847A9B0046A32857B999F52369D50B8B546DEE7E335E38480C723E9AA0EF77913DFC66FA7ECFC03A770DFA9EB15373669C2AA744CD870885C88248B88AFA82B49EC5BD67E4EC9F357CC813556E2E87DC235BFD52C4C5C5566FD50C2FACFFB8EABA47D1FDBA56FEBE7AA55141E43D8847064ADA7CF94B34EB71E140AB8AE0
	B6C092G6B43D6ACA7FD98297F8B0FE27B7761EAE24372AE985FGB8GA6GCC830881C86EC29B7EB5336A8CED48FA1AC08BB2BB78360D4FFE670C1AAE6318190FDC3F0668E70138EA00F6G8F40E40069GF30E21FEC3192356857E1A7FCC6A42464A7155052A00F4463AB06F18191F2CECFB475CCF5878G62181F54279B5F5990BFC97F8CFC64B37413A919387ED87C993E33FDBDC02EB3AB37B7C7CD6778CC1F0E491966B27AF39850C5824C64F2FE430DF24E66F25EDFB5BEB9E70F1AAE6338195D9FA9D8EB
	EEF7B2A2D2F4393552288B1977DA00DF79F154654D8A2C1B5663284B952D6373216B8352959942930E1B595CD7EF740DA9C0978DB0074B39398AEDBE0F4BF938EDFCF20A81690C3E3E3EDB1F0E49707BC3998A0F6AEE14E1D97B78E4F8A7A01D51D6F237D93A78F16617B8EAFE612679059F9CDFFE578624B35AB852B43F1AC3634BEFDFC0BA23FE333A4DFC6F910F517766GDD8AC0368DFD6FA5B9C3E0FB97565B305C538F0FCF4E5581690CF2CE3419795EF9DB088CE4CEBE154B90BBCE991E9AD506749EB399
	CE55218C6BFAD006110E7149F0D1C0BA238CE126B25C3E81E50860B2EC6E9CDF5BDBBFEA3A0566B2E4E0DB145B0BB22CBCB2BEBB3C97104EE887EB2F59786203374878428EF1E1G91GB3G92G96G6459F1FC71C8A6F977F126630B514721370DD34FFBC7553342EE162E7CA8C957E2088B85AC5D1C236313737900F446F64BEEB773098B3230EF983123CFC4F40DCF069B835299EDB543A0835BEF39003E3F0D0338C4G06FB7CG41250B71196BC4146B2BB6320ED6434393E9F88E6F53F25A0C7B91896D19
	5A7F4C4A27A0A00FD0FB72468A4C779F4F8A3EEFAAA14BFC5F54B3D9667B261232D6B1BC70506E6A4578F9D92177D5AD48B25BD7759E557B29D432C664753BE46700C833E5E3BCF1186CF3039A5A6AF7316ACBF2769957B084EC930E3FCD4A31C3443AFF5132D2A1306E6F32BBE5379719340BB66F7E3EBEBE31E873083DA873B959416DB7B15B5CFE17E61B5BEFF2F6E87BCD498EED3F171787176535656632ACD8EEAE4B9B4BC34B123AA218AC2C3CDE1649BB1900BDEF7B209595F02D7A4856CC0526C5F63714
	F05EBBD78657E377CAF3BD2CABC9FF67F37B3D3D22D1563615F9BCCEED351BE4F1000843E83AE8FA59666D9783F46D2569D9DC307452CA72CE3747E9F7FA159DE54C8E8F3D3D125A516793FDF4925EE013ED0A7E6BABD07FDD323BDE55FFC6CEF07D6F4EB1577F3E1C607A47660C2E7F63B94175CF4899DD7F67F202798353F52ED657E632E6476AEF7FAEE2A713B9DADB2F57ADC9E4670D006350078E1A0DC3E5284B93C59C075E3D8C5F85CC95F1FF4EC759F4CFA0046783E474E19B3EE639368F317431763BDC
	F9B642332BCF5F26B3DDB72C8EDEBE252B4D4B27E2F570725939FA7472E9DC9D3CFC762F9E3DFCBAD607AE1F1B691E84DA977B3C6E81280DAC5D775784577576B5663A4EDC93DC57FB570C2EEB6C1A603ABE36E6F4DD9356986BA25351DE292DEFDB849C17FC110EE3D5CF9F7A4C7DF4DF6BB488C783A4F01F39BE53ECBCBCDF7113747E40B1G4BBB3FCA5B5615F21FFE610419CF67811F42FE7469AA977AF4C3BF4AF71D0B48B7826149GD17DA85F25EE2DFCB186F992024A77C71DFCE55CB6AFD7E25E73F85E
	EB68FE1C750E784CEA00F6871F6F695681A507BE2FB088CF566026B9701D56432D5AF2D1ED53D9E3E61BD910EE3683ED737DFAAC3BE4874A37006E4531B863B3ABGDA1CA8DF2569BA124349E49AF186334D25EDDA7908ED983FB7F47369C9CA7AC865B9B3A83F3AF6AD3FF20E4FBC2845EF667887F4782D9CBFF4C80B2F60F63B2BCDEB37B8A7BBDB14824F6C93E83734D3B896DFFF826D3645C74FACC2D88688BF01F6FBC5B6BBFB938971B38162G9281961C8854B763249E37CE8997EB70CC172A43DADD36F1
	9BCF5CE216F783246FD0F8C8708CBB991877E483EE1A898D4B7B4D43D91A3CCB0AB16F41BA333CE7811F598A2FA4F82618704D1674B80B94AC6F9FF66872AE453C6F5CE016F7B970E9D4F8F54153E342F702819791B4EF29D35BA717F01B5F19E1D6CF2294BE51C1796DBE226BC36A318FA9F8ABF89FD27C16F99F32652D60FD487637C66FC3F63F953C8FE9FEEB74BE6450DB217B4B64A3DA5B11B60B4DA5BF25FB9166000D12A4EC5F16F5694A38846DFCF116191DD3947BE68F44E7DA867436E67C9E5373ABC3
	7C3296F2BEC4321DF613CDF1D387685827855B02F288DB81E40009832CCDE0E3305B06C83975F9ED8320A21D5C0C38C85A648318B6996897822C83FCB5C0178BF5BBD1CB5A0C3D6D361E9E51CEC6EBA54A18796D65DA7BA963FE5676ACBA046B69B1AEECFB4253881F05904EF5C35F674636A7FAD928752786F730BD88D751B1DF075B3887E1AA45BB5C46BD8879593887E19862663AF1ED606F743DDC0D877A918F623657901C9D42B247F93AF08DA10C633EF50C60A2A1BC4B037A04259B75196DD1F5C8728453
	E78BBD1B1862E1F2AF6578BA0A4FB9256219BE31F47CC242799097FE8AE51AC95F9F4D073060C55CFBD9A8FB36977B07F7CFE27FD06C45B25AB7C0ED8761B00088AF6AD4638AD5C6533D41F4DA65A67CE23C46B2FA176267F80DE574DD9716D19244B57AD07E6F39310C22B8EE5DCE54A95A07F24E8B793EAB4997CC4E8B69F8A745E71473F20A4F100DF23E4377AB67C15C080CB23D3B8D65CC643844F614F38147AD2375A34F9F1FD96EC75CA10AEB04305D0FE5F4F98D1651309F4B6847F574BCB704E381927D
	287B74FA236EA9FED55F6C41E03ABB68BEDC4B20718C3B1F622B870DE7585F5806E758BBA0EE56A04AFF003E1B328C45E7968FA16E76AA543D1A63D69DC69B059B68C85A299C37369A53C68DE1BB36A1A348DCD2ED1DF89B74FA9B0EEB978E216DD650BD1F654350CE82580751F699270C369B99D66DB5E9D8EF3BC888B39B6D15893F19434CC61FF05B75D1FC4EE9954FECD7CD47DFA46C0038484FD15765FEA2EB6297B0FF7D82F1AE0ADB88615C9144ED24E33AFC8857F0DC0713605AA1ACF15CFDA788AE8C42
	D3B82E005677E9901E2926ADC19B47F15C69D25AFEC0B867CB445DCC5F3B2C07F0B947AD5BCA4B91429D9CF75EA60213A0BC016372E85B93816199AABF5A66C6C3F8B647BD3989E5C96238887A2EA563AB288FDFA16ECF94379E42559C77DBDACFDAA06C6038EBF6905C8804A3B8EE91EDB3A2A1BC03633EBC0A7946F25CAF697B15F9A44CF1BB8A29ACDFC31F7DB5629ED8C2F0659036F25CB5544E5D909E6138C59241CD04F084473DD34A7D1B63FE1E01F61E4DF1E519C8B777EB6C13FB0E12B5CE985E146404
	517DF8D48762EBC298263B015A3D9D42F60EDB3A8CF9F9B86E930A1B8C61E9E13837F2EF8B1E5319853885E1D87FDEAA526D07A1BB9CABAB6B5C642C25158DCAD85D7BE68B4AD3F88E563DE84A538E6191004967605922613F61191C289BEF90A24EB3BFC315587EE5EA54B92C9E4EBEC75D8BB74175F42A323F2695734CBEB7BE3361DCDA5FDA35FBC1096D5A96337788AC5E250BA763C13287DE0D1F2A0BA7723E30C49B9F2F0BDF2CC94FF4FB642F285B42C7268B529D93025EE793FEAE53E93A721C14F44D22
	691FFC11465A27A5FC9E1A2C44AFBCD7ED13F24E5337CF6B4F4376C9C8A7B62FB80F35C30BF97B340C622D9A3C32A7B58D7724F6C3DC4CF968A33F60FE13BF815B4CA95944875BF978C69AA6E778D838A85BECFEC36803754FE38116479E53BC7ED96FD0D8DD2BDDCE746AF33BE4D6578E5336CB86BD42A6E0FDEC2F41BD59B3A6103B1426122916B2463E308B4758A99017FDBE0CFD812CG5279187E76E3E4FC8E03F36700FF204D7939A8303C9E26634BF0200BB89F4B62EA3A16958B61399CB734944BC738G
	6DDFCD470D0590EE84686678C6F687C69DBB27CB4ED4B27C4F17863BFF830170D92170B40B6F8E02D74B6B65D173403BBB02152171AE104072FAACD3DF96919534ACAEA0F6AA4F4A8B111E49E8B9E99C8B44E874CAB068B86F822CEB77533133F021F18C99C771B9979A47106D83B80634C05C048B31AC8F3B30AC13AF4432D96BA6BA65CE447007B4CC4ECD3370CD2E516C0AC0F80C2F2E5DFF46F90C277EB1FF7FB99D6797030C55G2C7E146D44B213G9786B095A09AA0F1A25AAC0D0FB305EF20CDAE21679F
	DBF878EA9AA6E703D978030AB3554788046719592CE25CF91C098D194D9A681C4383B20DFC83ED7607F634D9946022819281040BE06CF29136B99F5133ADBEFAECC0346339E046336A907625B51CFEC0C36FF43B486689CEFF8127EF39886DFBD50D319E05DF245AED6AC5FA9B4E38886BD1D1BD2997319799674115941FF811F19E9CDD07736094086B3A98FD62E7F45DB89A42F3B8EE77AE7413140BD1CED7A5B92354988EE3C700B00008F00C7B0246113301644EGF23F40F49EA7D4E259927A7114718ACD3E
	64F718060F32BF0E67E1F64F51D8FCE931062F963FC6F828CA5FDE5ECD16E6053CCB083D9E2AEAD0E8E71B681826481526C0925024GFC2C78C276A52C2C96F1BF2924F33FEE40CBGAC3FE7AA557C0A2AB13FA917186737D4135FF4200939C44DAFC1139F335FFF876CF154C212C99E4AB996BACFCC3944B8A73E0C62F3AFB54E09F38F621C389862A6DF0A7E76811D2F25C0B87BB244FD5B0BBED84CF177D0DCBB049D1721FF3548D6D2AE908E870884188510GB09FA0E39254E500EA00F6G8FC0B840B40098
	00C40094003C4B63B3ABGDAAEC73E9265CB6E1810G97FEB93BC7A28A1EF1G73AEE777C5E4FF9346A9GD6G89A09CA08AA08EE09EC0769590FF853BB302712D7395AFE2B6A47291590259BDC3114B08A7BA480A1E550A7CE3717370A07409E36403C06CC8ECFFA634443E5314FC92C6C9C34A0B14C9A4AF6B4B396D168B6A7DD0E6C05659BFB6412F5554C7F2EF4407A1F81841BBC1709F0C118F82D927B13B074D8C48FA885EED35EC94DA3267045C0B056D409AE153A1FD7B3508CC391438737D744EA1DB6F
	49FE2F5B6F32E3BBF49DDDDB31027FF6DC017DDB43A9BA86378D9B4660DD348E8F835D84CE6B1568BD9BEE2F2867FB8EDD0B198EF433B8ED42893ACE2E1C4B98983091F201493AED3BAAEB6BF67DBC23152EA34C85BE49D7E03F6EAE41FEBDEFB254BFGAB008490BE99731BD4CA4A5F6176BABF87DEB629D6F2763B489DA10A0CB717E2DF9D45533C182137E1578ED52E0F361A5B30122E7F4C859E491C4F14E6F33BC451351D0CAB63B3F32FC45ACDD9416DF265CEB57F26B23DDD1E20732FF260D3F3A55AE55E
	A6340B87F093GA281E2G12F8FE375669F51CD9236611DBEF2EE3A19DC324DF853F2FC2BE0BEA4DF5FC27160EDD012E1A53CE5F91DC47DF572A79676D546BD8C457293A000F63AA54F172D154B182F0D1GF1G73G322F467C66EF506B7867BAB50F030566BA8E50F52DC260D141797CEA09390E7FC9573BBA004E4EE9271F8C2E63078D9A9DA5030EF4CDEC827019F4B56AD8DC0ABAC683EE8E4082005C08784C72889CAFB46CA2BC59FCEEEFE628799C4B773223743E998157E4268EE94FDED6E41DEBFAA79A
	ED27A3D8BB6ED01E8B5F3AF69163F9D7165E4E7F39DB15697DE5234B34E399E93F58FD86F645368717E13D1C9A4147GAD63BB0B7959B853DDBD66F4ECDC74CDFA2EFBC604BA7EE278EFD1FC34864F46CB8F727545B808AB3E864F43BD39075EED8C6169G31G09G4235B0C6399677F33E72B6A94B10FB395B5D9E2BB8A8CA1AED0D4AFE300033B9238173197B1662BA5E0C9F5FAC4C3C20A1683DC14557B2DFE9D11E79AFDC33887B57D5022309689786F1D3G228192G668124FF8B75ABDB2B1C5BA5DBD4EB
	E559566B20B7BC718D2FCEF1C8C51317394ABBBE76CE897814FF8B4715EFE760DAD1A347ADC845712744F139E984B78942115FC25FDBC2571926C3F88E472D22B8F21E1E5C3B236C6FCE43B98A218BE64769A162C6774B5BF89E21469463DD77CB1FC2EC705AB2CB881A790A7C2C7EAEEEA6E334D63FA4526B2878E9D0EC7756B68B1C363866EF70F409C396FEA940EEE88473BF8267F57BBC36DE517ED121253850D2D2D1007E7658D1ED9B15F32D140A7547A817F58A132BE30A3AA64E5643EF6942FDD361D3F0
	BD7C45AE63FA78AF53356B59EB0F995743C91EEAFC53B163FA78933A7570AF0E995743B1BD334B577BD43B3C597F2151AE8DCEF0E9F269F27E1662EDE010ED1BCB8BB87D460C403D8F517CBCE58C68B897A07DBA18DBDC476EFCE065FA6952D065DAFC1D36DC573ECFFE6F7708AEF2DB869E5EE4F109C2B6ADF3755EA3065F56C67A3FB66DFD908216E36CB16D7A3FE543FE53757F14E31F27B6A8F2B82ED34BB145E7C9E375F3AB9D8BCE01386957E11D7D110475F88E47BDC5F1A9904E7EB6BF9FB5C07A2ABC9E
	0E27E1F2EF928B9F99986F9A159910FC826B2F6F1F1A07311E33F2F639C215F345373565DC38044C236A599DD33C76B29F359CD0FDB4E767C7C69FE5F5576E34C96EFE255E169760BB674E1DD8672C5F46BAF779CEE31DFBA5CDDBE79E58993A4E7DFE27314E9D553D437A24A6D81DE3EBAAFF61EBB8933E9D5886E33BC264A2FDDA8450C4813C25602249EFE07A13D4344921FCA2E3408DF282567111B655FEF35A47E63F778E227D62387D8E9EB45A6F72C5DA7DAFBC985AFEF79DB45AAFD357E63DF2B0187D14
	F959A19CBF3270F507483D775ACBD8F4572F5001C8D1DB533EBD56DD475BF6D9F75537B76D5F276C9D3B25866BF17AF7787B57BA7EEE0D637654E1BD36FE876BE9B9DD7F6C0070B040E40E2F21F8F226F9866092B87E41003D0DA49D7E6E784E587B63495FB12ECFBE9B100736FF9ECFFF9C38BE79CCFDE03B7FFBFED6BA85E44A198AF397G6BD4F6361B797D31DD8DA172517ABD81BB249D1E2A7AFD78D4F54C2A347F74DD2FF2118EA9F1FEFC01453F1FCE66CCED36C1C33F715FF44F077656B2063FE743B215
	9F3918C739636ED35D5DF4910ACE3326621E20B778997059007BC0E06BBD747ED07AB7FF27990E554D6F102B65153E6939EC6C1B56FD7F3BC243AF9AD2F19D55E82B0C6B15B14675EA5F14ADDB52984F68438237510DD581FDBA2B6F35DE2870D05DCB609F6D5319AE472BD117C9570FD597C7A789A97BE7789E2B586B79F93E03E46DFE8104F3EE00B986C0638D98F7A50D0BD26EB5A0676C49DDE0AC6EAF870375D50447E87ECB72EBB450655CE02CB7E5C1738875FED3DBAF0C6F830C75EE0D3065D4E03DE9BE
	0575269BE49A860882083E01694F662FF7F8C71F3FBEC9572BE5DBCF3BD316C4D69E979FD17DE0D3AB298B6452B4FDDDD863A3BE3F173ABB3D22CE415FAA2AF84A926B7ED2B2DED2EE51E0774771BCFE3EBE0B27A1B9A1BEE22F4A2BCDA67725AB63C7A9DB0FAFE17895728AE39E7CEE724122E53ABCCA947C2E7A6586DEA5DC2ECB9A0CF125BCAE3CFE05B10E67959998D70AF14E061581F1A84BA59E03DE254A3AD9772A40B49AB9378A2B4D6239BC335C59FA5D95FBB5FF3AB240DEA5CA5B75EC29316DA2FE15
	F0835E4DF67D963378F9BCBE2344ACDE78AE46BF3547ACBE17479FBEE0961F4F6327F71945D77378169A337896B53E53AC5E4E63F3324C6207F9FC3DE9FCB80F5F364AACFEAA0F771B464F60711FECB00B0F65719E5374F3F97C252671733F0BEBEF2F9FD56B78DFCF91DA5E4E573BA53737E84861141576A07718CA5B6CA46DF81039DEF9A94C75CA3796A863474FBFD3535DF5E2CDE09EBD6466B6C67BD437CAEBADB9033E27DC6DFB2EAC41753674C89CEBBED0E29CEB1233B8C5829F0B1798471A5F5545FFD2
	EA9CEBEE530D75BBCB0DE34D5EB46DD8733E528F857D58320D7CCFA6AA324DA57B0A6AF76DEB5F55AA60D83B3AC7557F0BCD214B6306DE15B6A7AB34DD7FAC2A348FED9A1BDDD79723DD2B39DDEB0A0DF6AD53594DDBEC346B9D3AB1FEE431512E75BA3BBED5EC34EBE71AF65D61F77D2ABE4F57043653C127CAFB790E50F63A6D24CAFB6A68586C74E33EE7D366F6CABE9A5ACEFF69B25A6927295AF8EBD7E8BBFD55E53453B11D1D568E287A5C3DA4341D3EF4E96CF4B2341DFE69D1E917170E4DCE075477D55C
	CE93CAC35B69C725C6BB5D26DB67CA8E2827FABB7D25AC18BF31F55C8B68BB41A410C7789E2E5BFEE397797E4EA15B09409B86G5962F5FBC42FFC1AE169D7BF700C56B2423F4FA679547BA8A3689A04F29ED573BB2E651F390EE2B676DCA53CF8D673D06E1758937C3C580BFB4C4F0B3D32477C9E035777043EB3610FFB02FF4BC7E854FE4B0715ED2417143D854A359A209BC08618484B7952C6322641EEE7B42E4D6F32770B58D8E37E271A026BBA5CB47AF7704EE9226F6D5CB05C9AA07C91FFE193A9AF596D
	21C8068BEFA2B672BA7B9DB2A3D5705FEC227B7245BE0AE6FED7CE77F3F93CE21F68750AF67A469B5F73B553F789112073F4GB6F7E84840F9C1627758B93394F8E65F486E30E3F307E5E9234F9DB624854E9D361265A3A57EEAB761C15656FD2C323011260FFE22C399034E6EA0B625EB6F1ADB84D85C32ECB26F2997491D30B8579D9235F35DF59F542462BA11D14675B7AA6FEFEED4673AAB38EF947885EE53234DE49F02E9FD87CF588BED5B7E1E93E22F5CEE6B67F56BF65D1DB693A00F089BC34FA9597B32
	64F4FCDF968574C9B762FE7C32D25D3D03ECBF7EEE49BD246C4647B634F8B136216B7E837763FF361870EC073084B061BF70FD6E77697D38ED105C41752E99A4E5B285E8E6824C85D8G10B1ADBE53B28D6B45837B7203598310CCA0DAB73CB627CB794E533E1301F3D471A4BF4FB80D7919951E5D5358DC1D7959235268FE762A9468E73F9CD27D4C391F780032862C3B0B02455736EB7DC8F92F3AD3DBFE43A0D378345065476C7FA9DF3319BAED2C6B9C8B4936CCA57F0FF47749A5G2FF923644FEC7A94BFE7
	AF5C44564B7260D9F893DB7BE0B65DD1974A26CC168FBED76D77C8B519BF32752BFA075B59AB7AF45F7BDADD184363FDE40D1D463238348EB56DE11B57858DB6DB82D34E37A777185D0558G72F65C04F31564EA63DC0550F8EE42B3AC6FB5E01D19G38D9B7E11DA969DE97D8E776F81D76BADBBF73D2D6E6E283561985B7E11D390272AC3C99E481683AF92CE5D97705A070EF917585799691358413615C8ABC7FA75F0C6BD3EF6A6EB80882FC744D21FD0055616291329EBA87E8678364FE9F4A9F20862083C0
	828883188C9085908310GB08FC00882D9G5683D4C4E1FDCFEAA7E334E075DDB128DA61398F5B2B3572DB01DFF7D4E879597B1D756D1690B47A77BBFFBEC07AB67EFE3645562F566B15DBB5759E3FD327096FAA4B623E49A2987E423D24AFA1ABA10A1BABED337F947A198CFAC4C5211F75FF74D7AC5FDE77C01133E72048BFB8D0E46B110BC4B0D5E565BE3FA4995A691F1CC21FCB0AC21FFB0572DFF7CBFCE6B9007596DC87FE3B0C5092196C9C173085F1D3B8EEE7896266F05C72C644E57FGF11752BB52C8
	3F5A40F1AFF7A15DB40E3B30866992D5DCA7CFFBAB6276E4A1EE9A477DE1A5623227A34E338AF19E0E0BDA45E566384FB6703437A16EBF54349C77D3B56DEDD8E72EAD995FFE18354563CBF7EF407DF9E7166EE869D853313AF871A9721B0F0159748247FEF41D06FF7F04FEC20CFD7A0145C7F9030FCFFF689DFDFCBA0B2E850BB02EECBF6DD16ED6384FCB7ACEF7DF1FCF646360876937C0982E49075FF8E06DE276D7822FFFD999217685141412FD815B0A8BF87D13B2307EE55F8E6D726D6887B3B230AE0D5C
	0EF569F9CABB8342F1GC937633E23573F3240F3A1C97BC39877833483B8G42GA683C47D905B3DAF64D0639CF698D2ED76587B0907E8BD0A859EE43F04F25613629281C776E03072FFAD606E00B3731B0563CC37F9717876FBD5503B87967E50385F2B1662B3E69877FB5997E39965C25C708C34E553E0F05FEB999C5D77BA874D67C647878367C676C173395109C16D5CC8795653C914F7AE481AFB877A5413A751272CF720CF2551778A93A0BC85E0469D788E573F934B1C3C2FE7384FF7E21913BDG4A9EBF
	DDFF9E8769936F885D9FB2DE5B8EA27FCC6ED3BB8EA2FF720EDC7976CF8D6A32G78967E88F559DD0B3A58FF043A48742E0FA9901E819047E9AD3DB8FECD62347D747D403ABB21DF82305E09349FEC677D521D587EFE5CB0BE7FAC99E73A93E3CE477CF769AE2A7F1DC63F4E2178E9F79A7D7A9ABE461CC1741F097ADE287B5E4048CC65CC2F720C1C99D83E4AFE3DEAAC53C59C77EEB51669A26E338DA3416B4D27A323571B769173FAF3E4A4305E9C9FB12FB76208365EA8F5E90478C8BF0C7DBAB81D1B63BAB9
	CE26BC896E3053CE67198C7FB9150B609BC512A1624328DCC3945721604E5760BA955CC5A3245F81159DEEAF263DEC046EBBF6CA928BDFC1E91CAE680638BDA2280CCE0FCDA377F59C57494B628B13728953150FF2EEAC60BE09B3735BEF0CB35D7DE3CE477C7A38175EDFB153686F8E0A0F19E974775779D8F7B644155F057E7EEE19B67F083B18FFC7A94FE8F84E394BECDF5853DB346912951A054AB36747B0677831D93A865DF82EC22131AA4FEEF84A2669D6B7EA53CDD4E826A84F69700CB1CDF74581DC93
	CD50444F035F425DAC1CFB37F91B1DEF40D7C33865EE33BC666956066C8A4D30728C0767D453F461B5A85B8CCDFCAC7C1E2B04678711ADFD169E1F876142D926F9F4E29EB51A78F6786DD042A3334C7318E440C7C2F826E99E56AC4CA3CE931F883F97A8610C1F1867314E00AF06F043CF4C72D8389273685044CB70BBCC89CF8E1247B483FE9604E71B6661DF05F9A4E962D360F776BD4A1A62BD66F9149B700D906E3A47AC0F5BD6E9FD452350CCD01E91700CB2CD574B6F1C0B56444F015F49CAF8E19059F26E
	B55CB181610AFB4D7208627ADBB5715D70DBD642936FB54FE30A81BF9D42B126F9BC2C53BFC1211927BC05E89833C51B25731A5EFB978A14BB3D46196E2EB127E36D6F9D74AE6C7CE8E33BFC8F4517C79B5B65BD2958AE57C0DCE4B40E2F97FE95FC1C1076756863046C2F4D47892B3F8E9CA72C7D5AFC1C705E570163040F943EDEFEE1B44367FFCD56ED58FAF0334DE56B276FE558FA0A6DEFC4C7E59ECB078D4AF7AF07172BF89CB6A8765FC19EF8D70866770945E77FBD5AF97F8BF9DC91F0D74519253BFE1C
	69FEB566F44CEF07689CA963BE23BF07D1FC6EFDC6FF4E14501FAD90B761BE1CD375D7850E3F242A4071D7FA6D7874DBBD4EF41F0EB99D53BF194E3712836C72BA45AF88304B6735E897617E78CC7B7DB87E7AEB2FB17F587B5576EF6E7D016DAD4B6757F48EB77FFE637DC0AFD1FC7A83467B01865CF8BFD09E440DBC00792F5F2E4DBF7181165F82651991931F39AE462CBDDECE4F72315FEFEBFEFF27616C571D19E7B58F652CBFDD3FAF865DEBE2783DDCF42D349D42F60EDBFA045F4B9503B625CD1FA22BCB
	737B8F274E3EEDF23CE75FB625F47D1A5C81974D6D10E6457B4F66F01C5D0A36C96438731BE91F82F469BF65675A36A03FBC0E4BEEC6FE059CB7500C7CAAB86E52FD482F0B630E16A0BF8F47A56FC2FE93B82EE9B7721B44F1A776A03F999CD750087CE2B94E399F794D65380777737BFFB86E159664177DA0626E206B75968817F35CE2FADFD4A304BBB86E844559A19C46F1BF223849900E623857695DBB51901E4DF1775420ACC99C77ACDDB74A789954691F71F2237C0AA1DC43F19D747E3DF6885BB96EA7BB
	381FF35CB3F4CDECB20423B86E87B45FE8884F6138940ACB06F08A479D2332244746E7DAE291F7934515C3584AF1D3E99E5D9076F05C1C43A84B840E7BFD8741CD0370AC0E7B812D2F33A11C44F1A9941782610C0738AC348E67C2381863EE22388688F7F15C414F884E8361B00E1B1C0132CC6638BB683DFFB3A19C47F10F5177A70990DE40F16F645072F8987C7EE144FDC5F16BA1DC43F18FD3DCBB04A50EBBC257B343A0BC0963FE46E5096438E4AACBAC0467F25C7B145FFC8827BF02B8874565C1B81F639E
	2538F28837F05CBB749D0D9D42A39C77CDAA4BA488CF6538157C5E5A999C778C6517G61F99CF7B565A7444138BF8EF14FD1DCBE04ABB8EE77EADA9E906E6638EFD21C8C61F00EDBBAC0F0D3A1BC1D638EF0D9E2B86EAFB40FE4926638D80A4BF994FC72D1449D27FB584AA15C40F1B17C7E47960EFB113609B20443B96EFC7A5EEBAA04E7F05CE394978B61840EAB27677813A0AC7C9CF1E194178B61C20EDBCC6F0A2B01F08B4755F05D6C9C779655E3A20427F05C4FA9EEBA04E3B8AE0F5AAF8142499C77A545
	AD00F04EE308FB086256C3381A636A281DDBA05C4DF105DC9619634AE83E91900EFACCDF3E51901E4DF1FF27B6CD0270820E3B0C5BD9180D3869544E6BA1DCBD9B7B5B3BD3855E7F45E8FEB7271E7D7E366FFFA10FAFA5D5278BB43FD7CAE7BF6F7C7F05BCDE2BD5F57A0366F7D86D594F7B1A331A877359EA7A8E065CE756427D78C7F6DED7389FBFC77B719888A7F15CBC0A939E0F4FDC7FB862FC6EFFC4E6649F5AAE617FD09E64FDF028737E4C86D574FD9339573871F15EA6517D0B64BE65AE0E4BF7202D9C
	9CF7146E91A3F7A74FFC9C773F5DD1213F276491BA060F037884CEF3D7277E7E0FEB08F51573366F9F41F30799716667ED17896A3943F5C0C360ADA52EB85EF8E75AF15AD6D5833EB19E4FE14C22D3C0F6E74A97D65CABB58C7161GD1GF1G73G980F99F44C1B7D840C911EC05CFB747E1E8A88B7BC01FC5D1AFBCBA7B4A3DF9962A782CC830885C866BC6E2463660CB9506F4EC15C84BAEEAE07F04D9C647B4DD215EF77EEFED78F44CFG08840881C862BC2E58037226BF0938D6BAFEDE8F6162A7116F9DE5
	AA5F67767333ED90AF83CCA6F4GF3B80F5C83A86F02A731AC638E503D8A9E5BA932E702513DF0G5F0D66BE05F46B0E694BBC01FC56C55933F04EB1122F5D6D6FA13786747509DE115F19F36DB164154FF9BDFA4C7CFE1D97A9EDB550B5F25A8C0F5E9F5F77909A87444BCF613DB9CF727DFD11001B8910G108210778B64735F3501F74EAC27670CC9FC85277B4BB1FDFE45ECCE81716D1C463DC3EF0B7F592C16431C9DA84B84200FG188910G1042537FFE27397E4BEB381FA6A06DCFEB824BE7EE8D5A3402
	532D2C554B747ED635FC645A6065F389FF8FEE653C22035C7114C0E93B01CE6234F79A6EEEFA144EB542A1BEA2814B671543E813D8404D85D888306E17B07E78A572496E8CAC1F0FBB31FC3AB85DCFBBCD6C5109BAC81CEE65913DBDEAABB476B892429EC710D7986795FDA408BDA86DE4201B4AE9254F7476B8174EF722A1BE6E17E80F6F733D648B811773AB2853GADG1EDF71FA1999E80F97B2519E13B85DCBAB836D7127152843D4CED71C233707FD1BEA0FABF3025B6387B948EB866775DC0E39BD7EC8E9
	E301EE8E27BDF5D2EF0F8B69BCE781448B3FC6FBBC436DD18C3886GBB40840048DFA31FB7CD6C71F6965AA30653ED4E893407156BB00753FDED304705152ABD12C25863CF1CD7B267D53C4A5C9E0DF40C3E9068B266A26DCD86FB5CCB6D118F7165F3519EFF6377B45B81B78C9081B093A0017379FDCE20BD32D721BDD2B85D8FD6855A036D91A4BAE4BC0DF4A92B74763839CA35473155416DB1389AF92D633CEEDAEDEE0F7BA8EDB150D5F35A77877476A827F322AE08F7BC0D76484961F73F81AE8AA08EE09E
	C076B34827E4D520BD9A39BD8AB95D332B74797D1E52D4C3FCA32799AC555B62D1FA27A21365577C0E36E1208F8708820883184753AFACB357BF371C366F4FC27B7EAC3FB3F0C3E0797CF083EF5FB95DCB063B6D66EB6ECE7C34B0F879588BF97B4EF9DDDDE8AE5F2D14369B68A4CE3BEE295EDE25F47E9C8E71914FE2799C667E9A8B3839G8B8156A5C2B9A7A21FC21372296765534569DEB47157055CDFA5CE37E9355E9EEBB4F7A6CE8E612F37F0FF8D633C920378EB8A251D8CF4D3B9ED2741DF5D54DF23A1
	BEAE916DD14F6D3190F0B94FC17B8E508260F98E796CB531070D5BE352F37AFA296637024AB4956223B8CD2D41DF3754287EFAA3775739C0BF9FA067F910872065F9CC7F33A07E7AAC9DFBFB00EE64F9BC87A576A23FA9001B81B097E07E736C7E95B687F854BE5A5DA3E99A48F881FC6585756E11E2787D8EB88139F7C7B92BD5285EBDC20ED2986F9ED9F3146C2D6DF17513EF4F36C8EEF656021D25F85F23BDCB91F61449D98DF934GB09A29CACB63F80151CC03672C97F06C6C20732CB990CEFC81475FAEBA
	4FE6BA6FF4204EB9C966731E459A1D5783CDC512BA6F31A6B11D493EC5165FC5F4EE5681F8FB9266F75DAE55472F6A9C5BB9527DF42CC2E41810E4F6EE834F38C46FD6FF2FEBD2737B4327E7F217D1853F4B68FAAB5633D9C9F8EE75B12B715CAA99049489F86EF4D133715C6A753A7832E663395517F5672445E66339D5BC974B74B9F72F2A4F05DBC21F59FD462A528A4DE720FB29FA0F534D4D287B822EFBC233D177FA1DEED97B0C3AFF280B2F5EE754FD277EDBAE7B0C3ABF21533DE11F2ACF699613334DBD
	122D7724727D0E9615F66E2E31157B853BD1774297D177993BC36B7E52EE236EB7694AB5EF0FD177B45DF9653DFB0C3AEF9734778A4C5A933A2C97362A7A9E5B9F24DE79C42F33CF232E226FD27E2D9FBB57F7F30BD15FDA1D3E7DADC6FD576A743D264528EF3F6E4E2EC7DAC26B3B7DC03072E571EF9C501E39E6385B0EE9F14CAEBD6D2ADDFE638E6DB3779CD2E9EF2F991B8D7F074FE727F09B965406EEAB0E569AEDD8223B8B6012DA238D7DBA9B5EDBEB34A1169153E1BD3BAF596BD74ECE7E25D3554F3AE3
	ECF5620D9DFC7E41754B8F3897CFDFA70E845C0B77B31DFE61817762FDCB97FF4F4E507AED2989D6A7D87CFF55847AC8E4ED200F04E96EBD0A58995AC766F6293431074766A32BBA500659AF218DF7F6042EE727BA0CB694F5B67AEEC7E89FF93243E843AA1D8DABBB51C7941BF51A586CC8204D6AB4F73CB4FF965AE65F6ED6E9A7861E8D8AE9334779992086EE33B7B3C2572B12CC234D366A5A5A5E4C50B63BB2B3F45B74C6265EE639B901B67BE9CE204D52B5F73D341E8CED33EE3BCAFB7798ED161E09B69B
	61B6AB4D8CEDB3FB004DCE69ECF655A8B60B8D30195E4FB6E669ED56EBE2335CD501B6FBD0F30F4DC52358CCFB6FCBDA5658ECE64D62EBC45CE679B921EDF6A447E833DBF4B68B4F89ED33FBF2C25B6C63D5FA1B4DDD95E833CBD6875A6C6E932A9D5E14C25BCCFBDF4C2EB15AEC8BDFC71A47EDE69F45E6D785586C851D4DE2C731599B81B653574D632B74FD1E4F2D6A57966C5E1AA07AADAAC37D563F0C7AEDAA8B5DDE5B4A0C7AFD2A9B97DDD1E654CFBFAE7AE9403DB5281F49BDBC9BC20F119E5E9068AFDF
	94867A4BCE2F4A73CFE6F786E97C65DB1AFBBE4A4768AFA55CDF3A38BD0F0F62AF1787784BDB3AFB009E9845DFFE9F60AFBBF57EF244D047562C8E34D90CC99DDBB4285A21F2A0344D2E9FD6E9ADE33459FEEE33495CE6DF0EE233DB83EC36DFE73367C631591AD5C61BA1FF264330210E7DC9F327447B25E39BD746713B216238FEEF845C8D259FD7968654316F68747BAC200E69471513826AD8332E0E750E287A58B6849BE3AA67CCFBF1BFEFFAF2FCE6F5B25E9D10712E493DAE75B62F5DF0DF40133D283F1C
	4C4F235A894FA88847G4CCD9E6B9D9597FCA9F07E07BC389E3700730F2477CD967E866C8D50759B14798F0325A8F3EF0FD3AE9A6C95A52928D7F20AAE39284769F609F23F682A2C2C5B37C737CA05F9C5D6E1DE72EFB02F4D55D4978847G4C7D8D2E83BD7F35EA636CC3E7308E2439F3566340F520853F992BED9653776E6C7749D4D5065BBC414A196D03E8BF225D9FDD785B784C1A5F06BE634B525DE89D5FFE74DB1B47176E625D63CB17BD4E6F0D88635CEF7F660E7125FB3BE3FC691E9A67791F4C4C7125
	BB10B53EF4D5B963CB3724ECFC69360CB33F76F126FBFC4C77E3A867E4824EC71CD93A6B47196ED7E3CE271C1321EDF77BEF834E49D0FC77EF834E4958317F10A0EE76EFF1EF57D1CF287C4BDF89F69EA40F1E6FEDFC45788D5B0FA83EC303E7795FCC7B8292F6C0DC5CAB7CFB34D52172AF1E97AC7F1BE8FB5FB04F287F6D945FB24F28FFD5B56A5F85F15173B0FF3FC31B7F7AD763B3ABDEBD13BB95CE52F7847A73C59E0A531FAF7A124E2F153D141A5FAB6D7F8BFBA94FEA9ECCE72F87774AD9DFC51B3EFF0A
	0C5BBCBC1CCF4361BCBC5D734F104B93241CD89E8F7DD372C88B29FBB47F16F094576D56EA350CE7EAFE57D61F7D72BEF2D673E0BA07BB827D7EF207566F599DF15F503DCBB4336B9AE1E7AF792D3E2BE49FC6E2F736F51E520E1D2B7742B86AF1E837DE45BB68FE415FC3A61978819B3FCDBA05633714D7714E11BF717B750ADF63F77C6B6EC4F03C2667B772DAA0FF16AED5F7BF59A4200BB4218D34E194152B46931FB973B55CDF3A2992FDA90E635E61F7C8108A241C2560F7C83C18066FBF1F247C6613F42F
	61FB755D15E81F425F419892209BA08CE06A6FF0EC7CB3D2ADF18D28F2EC5FDF11AA510E513F4379D4E425F1BE753A203BFB3952B81FDA240B4F28B24E27EAF5EB9A15D546799426E765F12AD2DB9EA9A0D7767CB3A90F0915E8E7129F43DDD209F6A6BA2C25B6BAAD2F4F07B9462B42E21FD778446C2A3622F62F4D6513E05252A3096C6AAC017DADCA95969F915674B9C5493ECF9C943DC8A9B6892BA836D574397D5EDEFAB716D5D82B435579DDF6C9A4671FE5E135151450A649666A5F443228328AB9BA72CF
	455327042F04C5D6A5C344B3F29AB5CC57F7840FC74E1A713125D612ACE4CF04254FADC9EEF23B20C5F60896F7EF2F5FAB3AFA61D71F65130FBF3174BAECDEDB2FACFAADCE1745E6111CAE516274D91C7DAE37D73497E5D932BE3634C3428172E99BCB2F5B65F37AE49FC9ED33100F188D5AA41863D968D1F2B26973CBB6AFF06937396C05A42D0E0A90EC74793DFDC0EA710A3D22C7F6FBADA71DAE1BCFAC34C092A06FBDAD3BFDEE4959EB3159ED9EA298F071F85D3268F495D1D1F8C23BF3C074BABF97FD201A
	576D6FF7C0F20C5C4829A039135C63493ED28924B659E2278762C18D17A22B2547693213BB57AD079DA2908D39B9A79FD543ABDACE7807E51B476D936DBCBFC582FA2F9AB023D6159D813AD1963DB660CBDC4463F093701EA63EC249FDA29856EECCD5C4D255FA1891948BD8EAA99FA22DC5F6DBF42CB48C6453DE378F0A92D8E4E575087D105083658C4EA6FAFDD996CB0945DAEC01877CAC3534624FB2CBAD793999FE16DBCEFACAAC1662A29258ECB9B9C08304E88B441482BD8DEC0518D20C293010A4A54570
	F31B45DE42B8159483FDAD20C5A0A9A9817ADAC2DFDC1A85852095CA91536791FB1DA21351E24D7796D8589F1177E30B1595164366235676B94100FD202FD2FA44A4E883D05DE27277B161C49C1C5D99C0BC3A97DC53CB0B101A43521AAF955A8BBB3FBAD8C0CD733105DCC1672519C2390BACDBDF25C5B2E6EDD39D5FBCEBFBC08A179B4A5F4B052615A0C862CE4BC6CD39B6B17925530087F57CAE3DAB94DAC07EA2DA26165A7C3646D2E647DA2ACFAD554586FA387AA5518231963BFB4066D4AA87114F072E05
	B9526242424142025E1DD5AAAF3931B9AB0B97902A18EF5D50DAG68FD603C15ACF95FE992C11B1BD68B688ECD0B881502E5CD6B8E144900870A979A0C813FCF36000DC8CDE0FFBD2225076C52AA32C0DBG0AFB3C74068B9FD4C51F4F5D6BD42AE29F566BBE2FFB40524A138F115A5D4A9CGACC06BEF114659B63052AF30D4EF5CC1B09F169494816643920203AAD1C82A82D134363F5FAB76335C72DBB7DAC94A0DD6D5D748048A0F721252D6C402DF58F295F10F7BF08349E1037A13882084BEACE5C2301F24
	7E91B1CA09986D468605F8F1A8995063DAC9GA282DBA43B62B62D79F64811D683AA8A8B359410DACE32EE90C364CB9BA9119A59F51A9628A48E97DAFA7CB277E1D28824E8E94B56CB1A2F1E530A1C35C5348902CCE906B4FBAA081D591EF48C1A0AE4F0D5CBBE71892F4F89BDF681EDC419D88DEE51677A849BFF527603B7A846GD950C45CD754CA69AC029A0739F9C92B8D74BEE839749CD8DF87A5DBC8C21C8FB10246B7071C32C353D428FED8E8F1AB3D30E22F22AC5A782299B6E0CD2ED68C52C23C136260
	87E9E609DB54521AEA2C192C3E3AFBFC22F7105E72821C1B5D1258CB22AD2D47C1G25448B303DA1A388358F0454D4C8C3BAE2FAC5161D85ACDD3BDB06F6158C90000121E87459569EEF250D1222C1916AE4E4D27B090FDB905C049AF09446B2B896B0AE3098E42AAD3234B95CFE49CE3C53C647B3643EFE0B5DCF46D150A28E7819B3979994258CDB8399360E0F2115B13486B2341EB9C36A451AA24D4A6A97DDA2714F63AE9106B05E137985D5D9C3DE682E5A6A1A2DA420A4970F43F0AE7F8B79B830583EE3F3
	F1216B38CF96BD3E6DBBCA0B3F84B2705D9648CE56F40FA00785DAE83F0B3437506D302F57DAE4F08A1FA55F66755ACEEF9CA4F74FEAFAF5E88E7599C21D8147BECE93ED5FF134243024987E959FA3F94A4272D63F4BD267F46F9365BD641B752B6A410DE5E285C0528128AC64110F6CD9588C53C2FAE4BA46F5CAB00C7E2BC316BD151BB631178FEEEF7FA67E66E19354DD28CF8EF9GD894A8F4C3C3C3C52351660760A97B21E6C09FBCF652CFC210B2937946CC79F1884ABE5BA9056A2350D40CE8A579882E25
	2887C6870AE9D710CF5C2AC5831325653AC965CF2F5F7CD25CB307FCE94212A6619D32DF3BCD7452AB07C5BBF961F308B447EE57EEA153638637A25F1BE89252FD743158A71F16E0FE145B57AA1E720B600776DA3C4C58A78BEF314911DFF6CA1B9AEDBE871D08417CA85D85BE26CEFC9655CE22539FF2373CG722D217211D4C5BC551A3BD77E75433DC9B35304545DC216643659F7C3AB6976B68919E4E067F338A57B30E7E78DE514B1C4260B7438A630FB172BCB14AB22EB9BA86449616B4217A7AF5F1DAA88
	43DEE19325A4EED944A96C75B84BEAA2939148F2BD0FBB67FD79EF0F6ED95B15CAA6F7DA25E4E1C91FED40A91D16052CE67E39E89F09FDDB8609C9478D434981104EE2108E23F8A6C5D7BCD4FC65CDEF9C211928C9E56183E6DDC83A29DFF48FEC2A6D6B035614B3289A76G7B77B8FBC2D3E420D9BDFC79DBFB1FFC33874A3EC1C89BA8AE267F17507FCB497FA594D3C2B1A525E06079C248BFE37CB06ACC9D8664D9C965A15365225DD00A722FDEFD6806DFD74F1F875A6D969653C6888AA2C7255A679F68913D
	97C4DD795E3249099713F27290F799A6FC7FB28C9CD787DEB7DDFB69AFFF91F53CA1B7D5C8ED9216B8685DE2602CC36472B162F1CB278AC22A0D0AC657BDEA3B2930AB287F91ECEE20A391F49E14EB83DD01A121F7A969FB74FF4B188DC3D2FDC4532EA75953700661FFBD19CA06F929919BB7517BD793874BAA53DE78EEDA2A108E86E6774CC123C0AE0B83BBFB193D53205D2175BBDD8B9A8D19BE74CDC0908D53759A22B3ADE13747B2C9C91AFF2E6AB5CAF89BAB0E4DC695401B49678440AEAB742F0E97FDEF
	6DF9B9171579D3050C5D248510BC8EDB136255D6E1D98FB58C39E59C9A6A9C3576523450AC2676C21B792E0A6FA5F8965BC69A90A049345B3CA7A987A2D97DD1A1334F5B5FC3B3100554233B0FC236B4C422F7D905AC986156C33B82CD1AAC2CD5D93710BB4E1508AA367A74A67CF9689F557A9301DE6E3768969A2A239470CFC3E1F453B3ACC4922AA47FD51F9D07358EEBA4566B74EF24C57B7F97DAF47DFF2105FDD8963269072DAC6C2381321051AC7AFC36FE78AFC88B31C479008EC66B7E3002ADFD907AA1
	3BACAC324842BA7ADDDDF51CD6F34869157DB02F2807511EACD413A91D64F41D74419C8AFE4284438DA3BD983153259F11CDE91B48E2A98C89ADBDDE7790CCAE0056E5F78F49C2F12D854731E4726CF08F11B584BA3DD03384D6E4E4CE060DC53250DFEB9A43A64B9E899A100DFCG4FD600589A11D3E433E932662C482672A1DD3A9F6CE6492F5B3747DAC0164CE46133CE371F2EBA78684758F9EA1050C7BEF04B960288A349C917C4A477D0218574F211F12045A9934A53EEBFDBFD02A193DB0BA3DC1CFD27C1
	15775A68F05BD49F9F445BEB7B48BAAD30E0E91C229D05A824D8A21C68242B85FE0FA6D34E104E7FFB5D1E539BE18665A30B8CAE08E6BEA3D21F01ECCE363BE1CE6F5226A24CE9F93AC4112E71A86CED663607CC98FB3202CA33A0D6513A26D262105B09F68766C0446F151C3DA7ADEE171242C556F3E0E2D8E431906B8F58CEF39106ECCED286FD6210E54069A2A3F93A74C95667FD9E18CA0A10E617578B28812592486473FEE4651CFCAB1D39A438085B33516FC1F9ACCDF4F59D2888254BED91F59CB41668A1
	0C6CCEAFDBD7D2D4446A2630AB048A639D226CF8BA1FA8D299098C969B2DCCECB95F671758DB8F0F575D8BFC48D41CFA0B465DAD16D6058E528C009F8E92AFA6528E5A1C921900D0DF96DA1D6483A1FE3ABE434CC2BE8FE9A3DFAEC45BB25FD56AC22F324AA89675971132A7DF4C222B373892D0C07CC229A64A4C98ADE6E911C41BCF3474BAC4A8B9C2303B76G14FFBFDBB7F74090444296D47436243686DDC168810B074C5EFCB29858C33949427B3D506D43044662270B82BD50283887EB7C3D0E5EA2516E87
	F1F2B5ED90E9FCC836C53449E2FF9E185EAC28A6534D9D07EBDB77B56D5BD3C95E6D002434BEA97E6C26EB17BE187173A6CA5B22510272910FBD8D1A0240F65187E5EEE70E60E32BA8A4B5C949D983A3DAF28EB7B4F64CC798836A43E4F4855E6B22D51326F513558E1235BAA1AE8431FAEDFE9FFBFB4396D499F3E55D9357E11475E727DC14D53D09EA7B2F5CC71D9BE42C2A11D9BB497EBF18D060246D9B4323747F51B4ED5E7F151C51CAF812DECEA1C04EFF91FBDEBDBCAE199613D51E3E33A352F57FBC531D
	CDB1A347E73933DA1AB77FFB186E36FF8FB1E77EFB08F96F3F07188F7EFB0879683FDE0378C4F04B7D2BDB73577F9E057E7C3FE0DB1E9CFC24E33EA4B04EE5D192BA9BE34B9AA5BCBF545872CC56F17ECF9730D3065937448B3E2ED044693AFC1A10F2FEEFAA6ED98C58071877847B0E55BBCF70EF09336FCD7E181EAD32825E81403EFD12A5EC9402FFCF3CED40A6C932CDBAD96B3213EF0B571D16C5759B7777527CFCC823C4AA5F5D24FBAECFE8527CBFD0CB878896A7B347B0C9GG0072GGD0CB818294G
	94G88G88GACFBB0B696A7B347B0C9GG0072GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEAC9GGGG
**end of data**/
}
}