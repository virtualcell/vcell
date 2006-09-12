package cbit.vcell.client.bionetgen;
import cbit.vcell.model.ReactionParticipant;
import java.util.Vector;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.server.bionetgen.BNGInput;

import cbit.vcell.client.PopupGenerator;

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
	private javax.swing.JLabel ivjHelpLinkLabel = null;
	private javax.swing.JLabel ivjOutputWarningLabel = null;
	private cbit.vcell.client.BNGWindowManager fieldBngWindowManager = new cbit.vcell.client.BNGWindowManager(null, null);
	private cbit.vcell.server.bionetgen.BNGService fieldBngService = null;
	private javax.swing.JButton ivjHelpButton = null;

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
		this.bngHelp();
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
			ivjHelpButton.setFont(new java.awt.Font("Arial", 1, 14));
			ivjHelpButton.setText("http://www.ccam.uchc.edu/mblinov/bionetgen/index.html");
			ivjHelpButton.setForeground(java.awt.Color.blue);
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
 * Return the HelpLinkLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHelpLinkLabel() {
	if (ivjHelpLinkLabel == null) {
		try {
			ivjHelpLinkLabel = new javax.swing.JLabel();
			ivjHelpLinkLabel.setName("HelpLinkLabel");
			ivjHelpLinkLabel.setFont(new java.awt.Font("Arial", 1, 14));
			ivjHelpLinkLabel.setText("<html>Help for BioNetGen can be found at : </html>");
			ivjHelpLinkLabel.setForeground(new java.awt.Color(102,91,153));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpLinkLabel;
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
			ivjHelpPanel.setLayout(new java.awt.FlowLayout());
			getHelpPanel().add(getHelpLinkLabel(), getHelpLinkLabel().getName());
			getHelpPanel().add(getHelpButton(), getHelpButton().getName());
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
			ivjImportButton.setText("Import SBML to Biomodel");
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
			ivjOutputsTextPanel.setLayout(new java.awt.BorderLayout());
			getOutputsTextPanel().add(getOutputTextScrollPane(), "Center");
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
			ivjOutputWarningLabel.setText("<html>Warning : These files will not be saved in Virtual Cell repository. Please save all required files (specially .bngl and .net) in your home directory, if needed.</html>");
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
			ivjRuleInputTextArea.setBounds(0, 0, 876, 617);
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
			getRulesEditorButtonsPanel1().add(getRunBNGButton(), getRunBNGButton().getName());
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
 * Comment
 */
private void runBNGButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

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
}


/**
 * Comment
 */
private void saveOutput(java.awt.event.ActionEvent actionEvent) {
	String outputTextStr = getOutputTextArea().getText();
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
private void setTextArea(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	int listSelectionIndex = (int)getOutputFormatsList().getSelectedIndex();

	if (listSelectionIndex == -1) {
		return;
	}

	String fileContentStr = getbngOutput1().getBNGFileContent(listSelectionIndex);
	getOutputTextArea().setText(fileContentStr);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G86DC75B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8DDCD4D57636E2D1322506CAC5ADA9951595A5959695151596959516966D52E6C5AB9599356E2E3FFA8C2003030A0A090AB6DBD61808DF2884E82060D7E8282CCDE53B8E4C83A60719F1662110287FF36FFD674E1BB7EF060F726F4F43FB775C0F73F14FBD777B0DD070C9C4CE262EC090B2F384795F56CCC158F3CE908E3664F6A0CEBF40E490947F7E8D30C0085C1F8D4FC2086B7C50E4489756FD
	2507F0890477FCE0B2AC0177C5C259D779A761458F1DD420F06B36721640F4AE1D0DF4E65370C8DB9EBC67812C879CF9F68FA074EF33954A78C6990F102993845385A44D6336A299978FE9A68394G3E7882163E9A1E391472200AF2B95D5121D1C24663AE5DDCD20E22CC0488B96FF879E9947E19E97B8321759E6513641989F1E500A342E78957FC369E1ED58D47DB37B7B7D92552134D224DD65AEC330AF62934496A300BD22BE82F2858F1E077F79D1233C3EAB05BC5DBAAAD3332DDCF4B9004E888CB83896E
	120D04CE893C57829C9D08782B4B88BE965E9381D28722EC5B3E2C12F9B71D3EDB185A7AC86EFCBF3289C1CC2662A00FEC0546262C25349E1E5293FDDBA0AE98A09CA09EA085C098E4B2AC2775BFE976D7702CE912F6BA5A5B9D769A27F3074D625A6F3008B6F82F28009899F7C8ECF638AC02406A789F36928DFE82814BB73B3D2F7918CE22B6E1FD0FB9F42B10BCF604EE3606CE1223CE6B8A87B1DD2C9F64518923DD6D17F643A0EFFB74267D3D2024AD3C39F1F60E866D2267B74E364865F4AAE8478CA2F57D
	A13501B0F84F144BE5F843A6021F2140B35B9CC25313F0410550FEAFC45BB04DC3DE6A33C789799BC6EA6AA16C2190DD72050C070C8B553CD8B713320D97B11A4BB9AF67A87E18824FF899CD6D1404A3A02EBB58E4E07675429C52D6CD9703BD8304GC4820C87C881483898EDEC25FEDDAFED2C416C12F6F4D8ED9651A5303CAF68BF062758A9394CFB6DEE49ECEF960F18DDD6F313CDF48BF21A94BD51C113D73E57286DB7830D5D22DDF419A5E836D03A39DD14C4171B25F9DB3F0024F109B436515AAE8201F6
	270065FE20DFC4F4E0F6CB7FF1DA4C1268C94162BF5413BAB9E9BE82A5C281704E74121DC774D5F00949D082D0F5896A21AB1714772D6882EFD1D1D16FF09C6FF0B692C90410CB081E3F273E23815E5BAEC19D8BF389AE9142293C1C685C050AF2EA2D4D04FD332B8B5BD87626FCF84E04740503CD0675G350351FE5EB6217D544E3DC5A819B4DC53FE0600AD9F9BAC5B72E0F5BBDAC04BA7BCC6C0DCBCC04AE0747DA3B61A7C343175033D5B98618F5FEB877B363FC28D9C23F11BDF9A33D561ACD5DBD6274F51
	40B13D5B6711BA94C2CC06A2002A90547D34DCA333659DE63708567B1D8B8CCF34F8EA02716958DCA66BF6B1408A00CF8456E7923E3EE675ABFC89309AE009CCFBAD17F3904D5F88343B81A28192GB288CFFF04BA85308104824481CC85483B947A31CB31AEEEACCB1469E7E9480930C0FEAED670B0CF45AF26C5DE6749FCEA1577F5GDA3F87B059D228ECC42E393D10ADC7A55BEC599E8C0A340BE49D107765FE4A1BFF1EE5E3B6764A9CC2C7BC89430DDA51A95AAD70621663D267903EE5FF586260F95E26F8
	A7784F1D60305C9C1FB1076840ACC9AEEBD307A49E96A549EAEF75444F24345C9DCDCE0F6BC5DAD9F4BC64B8857E1327579B086FE9F6586DA2B5F61EC50E1FC3FD1FE8935B01691D0E8E3B447079F9E40CF8DC6C12FDEFC15EAC0178CEDB877163EC5C95EDA67561F4D95B2189112EC098F3A9710B7676C2050E0E820E0C9F8740B374D274EB27BEC33F366A579B05787FB9F5DAFDF458F51769B2AEE5FEC5FF19472FB11EF266EA73EE20BED8463B992FD687496BBD82E47638C898D8BE871ECD76D696378171CB
	BE56B37CDEBB2099EE5A9262CBADE20B3943A655DB5D926D96198F3AE1BE612798782C82E83C8CFD50C5F3C9BC7AFF59796CF458EC2CCAA0AE74B25A1FECAA106536C1B868B22637F9BC7EDFBCBE9942990A7823C3C87C093282056ECA06F862999F52BC423BDBBC51A1C237CCBA32EFF97D1E1E37D095F7C42EEB96BFF09E29EB1B228F1CCAE9BE3499477F11B23D38A12C1FC90267CA40C78DA569C236E03A75C3D97C1E21AC3DE9A86331FE98C9D73B9553E58CE5FDD59E600BG2A0631F449B45D42F2CC97B6
	0C795A68E1AC7DF8F816F23A89BCDD7165A6C3B540314B995D084BD9F9CC0EA1DFE33A544B99FFC2A85085288AE5F230F4668ACCD79F4A7AD1A207855E03C2157216D4F27EG9F8D10984A68668FE7F4D93AF41E2E9A7026611EF41DDE69A22BB0DD74F0C6EF42F0162EE004B25D71ED182EFC840BEF9821156E736D182EED04F7F9833C52CD61696281BF99E006DCDE79C8160E352F77D613B6CF67E03F18CF1ABBCB5D2740EFD5D46CD34C35A6537192C95FB09267D459A7C81B8906F084C06CC8748F4F4C45B9
	D56871A8A1794D8D337D4D9793E5BEB2C6FA7CC34A241AD92CCD3E1BCFE81605C17F9B066D7143F94AF1DD0D5B6DE83652A125EC6B374CD77A8F063BE3BE7191ADD617DB5A8B8E3E935BC56CFCBA87B0F334BC86BFCB678944E718006E900089E184FF6FA662D7C99BE1F2B6EFC2B9077CFA1DF0EC7815BA039FB9732EE07215DC611133646C0C9CC66F53B21C9BDBA0BE7C8A1C9BBF775F2F845F3931DBF41D94DD2A39B175F2D81F0345CFB56B33D66579F04EF5E617323E62661F71ABC7124CFF06C20EE2EB63
	ECA647344D283732ABE13C84407C6EB31B889D6E23857451A73790FDCAEDD6B763E3FACD35CCA76156D1C26C6C901D56DC3C6D06B0DD64150CFE42158AFD0EEC4A2A86E03C5C39A5CF3657222B40662FC2DE0A36A2CD262B5A4A9AF8F650B987E3EF174355EE165CC4CB45758174DE4ABB244A55C784546528C5CA992A4E7DD7D3066079ED3A102B986FB1D7F9E4288EEE4AEA8CC6995A3722BE05F0E83361A8C3921701759DBF1FD1769DCEC897869047534FE469E1A043F83CE52512C73DF910A60FA1EF366A92
	4399EFB342BDBCC681FFF11C474AF264317AEA70FBD7236D96ECA7CFBA6660759DD3CE6C4ACACC75B00C116C2D324E3AFFC0FE86A44488050B7E28498F751FD7B3BEA6DF6D61C79A511495BC8279F93F9C6B3D789A68AFG180E46F5A9F5948678F8002957200E0EFE3D911EE65A291732C70368EA81DB90AD452495151ACFC925F41CD8DAC3236B487BEAECF383FF2804272B438ED63123C3129C767F5528CA2977DF8C31A0467F382FD11FB3003775FFC27E0C95440742D8506D30093C9DB1590EE6296B3F8D72
	05703C5FD6E07DB39A17D4A00D88C8B379CF28236FAD4AF272A3E0FE83E00A40F296D792D95B196B10797DCFA56A5B8669A2A3302CC40BDA5F4990A7DC0BE545D57EB6FD5B0C1F41535D54EE5B5B6EF4106134264E39DE17D6A11F8540C37D3548E7F00B961FC157229D3FE4A4F3B03779242C741EEA917572A4279786E5A5F2394B3691DB3701F3B9AC4AE348D25ACAF636196D2D326C324716A954AB93B3ADB05EDE4CA4F3A851CE06F4CAAD94AB78885D06FC68C74198EA947271AF0A375A2D1255EC337EAA96
	2FE6698FEC47741D10B6FA946A614E6DE4FE6C9625C6B0239A17E8E60A6809D3D089ABD7778D161B8AE59AC7237E279FD56B5F84F15D2311CF6BB73F4DCE76D96479419E5166240AE1F35A17FD56A7070C762C01C40E76DD8FE16BF61DF44C93BB1A2D5BE5F17C9FA8FE02824F641EF3825B5AE4082B0A4435263C82B2660A00F09CC092C09AC05EF5A6C349F5B856294E5FA47430163757AE41D048ECBB6CB52944B535FCC2976601F2781F79CA1F4C7490340968E14FF55AEB32DA40728D23790E75A19F73752B
	FFD46EA5E4AF5827A3BCEA797AA1F2599157F9FCBD535DFD0B094D24GFEA6C0417520B3007A6BD1EF6796147424B7DC57149546E4AAAE6B5FBA7B16BE67E3F2FC360448E1833E0381A2GE281A6F3B9A6AE68317E6BBAB56B1F71756E661E781AF13DF77D31FC9F75182F68866FFCCC1EEB8A49FC238C629AG5AG02G22G4683245E00F2ADDD3823A739EAF091255141DFF72A97C2105FFB37287995223C795316F322CF3E15EAB9233464DC340C4A89F18DGEDGC1GD1G6381D223D0CE5B42EDBD569FF1
	D60D0E1D005B8373F38D91E5DE5DDB7BD717977418CFFF23F7BE66B71FBCC3E4501893227F17570BF23B156BC5C6A82B9E4089908440660E21E7F01FAE7CC674FF17ACA7BACC02F0BA007EA613E195409EG8BC070CD28437B96D676D2074C7B7BEA106921263CFF7A7B2E47FCD1B7E959481C95C43EF808CB8648G483F9946D3G8DG524DA85F7CC5BD36853E15521B3670AF9FFEFB5796F23F6E29AD045C2CE5A7738FAF553693B69401B8E6A753CE2A5795091DC4C21931B7231D9CB90DEB8C09B7633A7A83
	BEFC752E5E1E6CB11FFE0CF7BE4667DC1D5A1E0D102E9640B486793CBC8B6D59B986799CD251BFBE2F69B1DF5498AD3DBF31FA09365E4998CC16656253EAD94858B69E4ACB6232DCEA514B3C24F1D9FE2A6C1F8DD9FD72152A422BEE5152F9429C35EDD4C13AE3G52AD4867EC91EDA364966473132A7E713956A71F5A56472B78E4BCBC5002BCA4F39E1E6BA78F2F7864D36B2A60D62DFCD7EE6B9F3DA81FFCEAFAA51A749A37770FDE13CFBE350EEBB569693F699F3D0FFD722965BBFA2B166DCDD92B36BD8952
	05810C3995EDEF754F245E4978F5BC60587EF186DD8F1D89617C5B90770C032ED7C2387EB6340F33A7485C5B494341B4CCE830F07E89757EAAC92B76817D85C6E3358F0DC0FB14FD85560FADAE703F0FD6D4203D0F76E90176BEDAF141820607B2142794B0FEFDC160FD3672822DFD3695D46E67F5E4BE666A30CB56F61112E57ED46CA4FB2F505B9E6A30332E18D0F62B678B82766FFBD612FA8C3E8D7B71278AB68A3E7DF81D45AAB9DCD8C823E8F6FD575242BB7143CE3199F9FEE431FF7DA5AC56565F330B35
	75B7F9F1E07D25AC8E2C3FB5CB7C7332F109B6AF65CB34F9593EA4B0AF3B1678630555579B92D963705983BDGA3A28157950B37644AE99AA4C74386DEF6D421FFB9EEAB5416634EC2324E66F6F4381AC5B52FF10505BC4EB3G5BAB096D049D399EE9FE496CEA95FDE4CD2079D91C3F7C138A499AE91355E2F549BB0CCC8F93BF1ECF7568F60BEEBA602DB5CBE6D97E69DC7EBA493153A37F0AE57E65DF35CCDB7E2F167917FF43321E652FD866DF7E1AE5BD4B3FEF19BFFBE0325E34D5A16BFEB2BFE66D37350E
	68C9E3BCF4385965305948CE1600BEFF22DB4B67C7C2DB0E3D8DFDFECDB70E7192EF437D2E28EE62BB7451B0A684A80FC69FFE4519C0E33806E8DFFF4EE48DDD6E3FFE2EDA2EDDBF57AE77DFBFD14BFB2E1F5B177B2F1F5865BD574F034B8357CFA7DD6327ED314565E80756A81FC1D861DF5665AB34E57D778A7F32962F68D9568DAB7C4BDA3E22E7D9EBD6285BA213F163574A7636C5407161E727F0FCE80966FB24DD44E642A19C831040ED662753DAB6B3D53615999AB643E8FF70355257ED64B67D56D9AD1B
	4E3B9DEC75F63469BA9D5AF4796D485F8D34BFF4C2B898A062F664AFACAB10CD475E6E0F3F47AA147CED623AF970B452CE6034C7B5935A59F7188CC5GE5F7A06D4BAD01E857C33AE3F7F868CBF760DA7130CAE53DF8F413162D251BE04897FE876AA65B00F597F387721778AB61AF8D4259E301473148DF1E2845DF59D846531E317EF4F324CA499F518DABEFFC0BD6F9C765F21CFE4B1B33CDD9DE9947BF63055F4C710337AB71DBB97E1BEF1478F22E37F1A714FA8B9A4B4ECAC643F37CD8545B02D21CBFA60F
	C53D4552FD60029813218420B68675D67A33D24E23B132FC72B3881EE1B15E32B27E8CF6F2FE49E66EF210CD2F42BB691E399DF99B83F94683248188F7B29ED91FF07BA952FEDBDC66F6F0D1F43015C17326BAB06FB188CBG21GB1GC9F722AD7DD2C3E45857E8EEEA92AD24775840FCC27DCA62D3387F1347A1CC9719926A226AAE54C559C9D20E84619000483BD0973B3A8359F85CDD7E769F0A4E1072926FD26FBFFCC171A9F729779FEE6D467D07F408EB3C9B67AA51E7F02E92FBB772D4FCCA4DD3725D9E
	BE526F7647D3A775F15931BEFBA294DF906B33A75205BC2D023860D86449C5FDC49A04E7F25C8B2711CF7DB834394F4F211ECB46211ECD1924CC8B043B81C24721CC7BF40174BCE61CBF1916E71172624729E57A1C62934729E53AC507B225C0DC6DBD487FEDD948FF54BD4853231A3E9FA1619EFFBCFDEC21F5FC0F1AA7A345275F2366A92A191F9F3857E4305C0BBC5DE4C11EA6F05CA43A3E1286E1FD9C620CF43D23884265F1287BD5B9287B7AB8547D22B904CE90044381E262D04EDC03DA4E04B80FECA9F1
	7E642CBDCD4A4B08E3724C6678860A4F3B4F03E7F2BECF7DB4892F0238607B107F4F18CC901E49F1C1A237277B9177D28B26B3F1DCF5AB26335C0F7EE40D5E4F985363A538AFF8778476F7B177231E6639E9FF82616C8720BFF9G7574F129DACFE58FF8F4336781EFBD9D05B053C789BD6362FC0069A3076BE9AB4587A970CCCF77EF66FB5290B7738114350E2EEF9C0BB7991C7108BB37196002A09C4AF19FEEC01DC4F05C4A0D84B79E4249G0C4EBEBB1EE7CA87DC7103283F57CB3D7694484EEDC5458E87B9
	D3D94F1CBB535DB03BDE4EFF6CC154DDBCADB38C4251G939E44B3D81DDF605925B1570F9292DF53BEEB1652F8C61772A053E376C30AB3C34626ACC6F31E93E9D6C3FC67C384D765D46ECD133ABC1C435615D83C5DAB1E0C0B49D981CFFC04D7BC617755D94A78E73D62F3947919ECCFFC0E324DFCA2DA305D9964775ED53CAC53D4146D29ABB2273A9AE6B17B1AA5296D2B68E10FCD2DFF585B3EEA9FC67B0A22FD1769E135FD0D25F85B43EA7B2AB80976B5G6226BE0CB672F3B75A4D1E71E8731F1E216BE0BC
	DCC243642CA88B47F4EB5DBBA0697D794D3E003CC720C96377DA9BE3E30D474E60D8A394F80F8218G108A20FF8475738D1DB794C3380A6346F5224E0EBD4257923B080EC2F8B818064939D8F9AD314B5FDD8D8412773769ECF50FB4FE0B7E58586B6253F8DEA97291F287F274679094D29FA1FAF95BEBCFC31B472F4E296DFD7D230A7BAF0FFA6B4074A85AFB829DC3589ED5772F4FD3FC77236A7E753F19583F06C0DC7A23D8F7E69D56DD63E3D8B77F4EA2B28D6061B5B4CC4EA83370D8DDCFFAC5A0E5742F6E
	92FACD23BFF5486C7D51AC2C1FF060B39AA091A09DA0FF826A67E5BE26DBBF8165AF32D07D707097B4EC617A393539373C7B68EBC24F7AE2B49E6BB70D5E680B6967C18B6AA782780A81C88248GA81808F30FB4112EFB5003D422854FA657CEA479F3DA301FAA63693736F852DB9DF632F94C53B770744193D1172BF3546DE37CC40F0EA6CF7456D75AC4EC9F9751F11E7038FADCF6B94597BE2E9E17AD1D0363327590977AB89FD75239768C8867BD0138BF65F11BF8020F5D69F8EFG04A3GE2B97E88455333
	09435979FC06CF7E353F7567E5AFB2BD729E7B04C7970C467B3F8B8DD2EFB90AF21578C5427496EF7F775297C46D0589C4CFD70935F25A44A7345785F265A7012910E68640E7328D64A530BAA2404A7E2C58D376772DD87631846D32E7AB4A16A0CDF002276CF0C559CCD79F1C88B44F095220A14F9B69B8B9AEC1FDFF77B20A1F1C203E3FDB60467B3B9990E7FA92EDEAADDD1B0803F0A24799B7231DE5F0DCB54595BEE5B214BC053634F18B4115C338116336D11C6DA9F617C2DE3B595C9BFBA0E560FB79D33D
	775F72D945DE51D05E2F6C0B7FE6B6D779A531399607DDE2FE43CE6784A140EB58D368E71E5A6FED178348758439DFDE3CB1D03F4C720F2C2477B09CAE3155656830DB50C76D26F30889C0A7096B3A415E3F33F87F6DE73E11FD4E47ECF218034ED51F7258BE43DFC57142539EBC3355C7795CA88F621CCF63F92866B5C4FFC990CE8750A71A8CC600EA0023093847183412FCABA160D921C60733DEBCA95A94DBEDCC3EC54E3E4A4766A08F4E46B9C84CBDB7893197567A3D9B62CCE4F6BDC4FE46A6DC11C91ECC
	3E8FCA08FC7190178CA0BCE3B2948214G54BE037295FDA21F5BA45B26B512E4EEEE2337F878A62CD5BC65C11385BD56FED7C8B8FEB6BD03EDBA3C835BF9B74745D1DC8404E31E61630A1384978F61299CB70E612EE177A8646F991C44B6C8527953D9FC00381EED7087CEC36B2EF4FF5A36928A1EA5BA103A0D8152884FB27ED9FB4D59CF663407BAECA23DDE44CF7E619A47DBE7F04D7C6833B8E77E7C2CFA4E7CDC36F24E7B7DD9751C195812A77E62336AB973942FB9733DE755F3E64CAF1F2F17FC570A5BCE
	71B99870B986A089A075D9F6870469A531B320DE1ED36AA5C54823FA715C75E0ED65691F14E71B732F3469306D7B5405DC1E71B94FDA84ABE3424F4AB93FF14DF71AF37E99BF7F2AEB1473F6AA4AC89838B61759FA8DDDA39D83F1714F217D478B58A626F2DC8A456913CC0622A43475E7B3091FAF6661E49AA6F7CAD8581279FB0C0190889D5F36F066F72521EEB32C4EBBF5016AFCCF12324ECBE6117D4B1D6C7E8DEF892C9D24E571F610046DE0D5363A9DCC4BD25A71116C406DA0B8DB5D8EFE72DAFB3AA75B
	DFBBE0E3390C53B8168BCD7275C9E81704AF624F23A0CD9C40E29917C85E21507BF4FC6F2A187068F3C3CEC0DF7006051F0D63BA78C4D46BE0E826D20603E2E09D8C9455BA48757285F70A01FDC1D18B892F9730AF0BB825EC1F65F37E20F9EF1D5A47A42633E27999725195192C4C569672FD956555972FCBAF34B3AEBD3C77406E7A3A7F9D2E2B2F5B5938773B8372B7C72AF330CD9E1B046DEFC0AE3649A00E0B4845B699B5895B5C9F69DCAA8642C9G699CFF854513F31D794F1B8CE54F733311F3546DA7E9
	12478EE2A6753D1FCA1F241E8B157B50686F98D4D92EB2BF4CED8D3E7E7C0001FF5B8A6435810482C4BD4F4E37B27B3FAB2FB6G9D257D9308073CC94FFB6CBF75F94F38CD7679078A387D539A6747CAD87C4AECB236BDECBEA96241E7F93F1E4ECD143733D8FBC96067D9B30056A7827BA606DE40FF695FE86158383B031820EC532F64E1FFD1F473E8217605DA9D4E4754B2173C406A2F7B8545DDEB68AF98BF412DFC7F7B053E7273975244E49B1F6BC2FBCEFB012FD33A49378B8AA703FD82B48258A6E35CF0
	9AD7AF1F33A667FE497DAF96374C6D2F8FA0E574E4CB05B29DA52EEC325A06B77925217E3E0D9660FCBBD09BD8A454167A5A70CF25E843C1135918A49C1E51B2FFECCE35F5F34FF32A6F695CD5B2B7B5DAA51B486A23109CFD1063DF1BCB468F6472DB3D55FE3C5E5CC46FEE3238F5076648F1E8336CCE1EE30B522FCE184C6AA48D1E2C7C8B4ABD653BB713F313F6711411DF465B85134862CFEBDCD6336D5392638678DFC676B5D85EAFABBCF91D9B09BD71FCBB9DB6072B34095CCE134F201023C0727B350A77
	847B6FB9B6502A778542EB671506FCBFE80D7FB3D16EB55AE7223A56E81F33BC37A670194E0B56787F764C25EB145F1EE173353FD7909B9026403890209A209120ED8A1F2B1510EF8C301BDA6AF9DA1D25151C49A2DF9013107EC7A57EE51D3536676F0E4DDFCB6FF1BA40CE5BC9797C5C71DAD2DF1243C9118C37ECAD51114B5A5AA63124B25E38162E5F08AD944D5A613792EF07D358B912F0F8C6CFE177D3D8BB9C5B51F3BBFC2E43379DDED265317511744C8919CB395921E056645C74939B9BD81AC4935191
	1DEBA9CEAD3238215D44076C9449FDCF747D27C4256FAF7A24DA07E3A8B50F9326485FED1862717D9EDB1F6EF5FE3178C57043AF86762F72F773CE61DAC683248FFE9177F9B7ED743A934476F9F7599C2764DDDE9CB3CEBE05EB5371AF62BE6F2CCED2668C88673FE4B2943F04EBCFEFF512328FC376B6EE235D64FA1DDC3751EB49A6519173AF1AA72F77819B5FB57D82E334C6F3AB4FFF79F6D2A755C063A840G00B000B1G63DFC2BBFFFFED313FF281498422366EB2DB6D72F706CC67FC7D3D639C5A506417
	181D2543B3BB1975FB4C4EDE90FA36333F8B3EF6D6D26D31B36BF7448664F90A5759D796DF63D25A11FCCF546B2CDF917034BEB9F07D4B67F03AF14CD01B5C57B143F764339BB27DB42F73FE5DD056109E68337A1BD9C34A0D04347149D8DF1525EB8254D7034B6194DDD29743522FA44975E34DC15D27A433B1658CF8663D4C46A42CFEC666862A9FA6D77BCECFDDBCD1C53E4B44460CBB5B9C56E6516D756D2A05A579BC5ECD6614B41645E519BCF93FB73B6C604C654E1B1D4D6BE8563AB3358A78ADFF996F56
	7F1C256E47C91A0617711C46C98336DF8B6042DF46763B61D811EF7B5D6D32DAF618DB1945337A3F5F006DB769E5EC3FE9344C3CBF83AFGD5FF6E2BDD8C29755845B3DE67AC9B212C36BF7746AE4E5611C23AA1ED98C09CC092C0AA0070971321G2098208A20914086B0GA094A08AA096A081A065AFE8D3772CDB9D4026E445F81CG677F0F2DCA7EB320BCFDCAE07E593A44AA17B1C09A6FF5098F8F12719DDF23EBB037FA5A7A7C2D8ADF005FCED3449F5DB4075B980BE0780B76117E050C8BE5F3157DFDAE
	7FCED19148511802E7428E16219D0424209D1C23E947C3B899A0A385ED6B3D96BF5F1A22564D4A3BFE8B5E374AFB856B60230AG63074ADE0C9FAA69F721205FEF6CF24A673B8BAA09AFF4343438C5BECED9DA194FF1FB5D8D8E3715CC6B197CEE89652F853E3ADFC1796F15D07E18D7D07EB7E95AB488E77F9546D0FF45F57E325D24EE4BA05CGA08104GC4G4482A4GCC7DAB3FD3D495286FE18727BCE64766DF39E7E91BFA55E4A06B268CB70F628A81C756E219ED0E70B96B593BF1F1E4BF733D5C67FCEC
	9F65D7BA9EA8FBD53D3FB210622BDFD56F2F64F5E29D55C3DC742B284B706D7E6DE774761E6DE74CF66D71679D5BFD471FF7EF579EFF5E37DDB97EE47C0EBC077C96CC05B167D434293F1CC31B8A1A0AB6B5172EBB47C1B889A0EDAA2E976933314E499A21BC8F4F46BAA76B0E7239982FB347592F198C052F8576C72C2CAC914B4F65B6259731FC322647644B5CC17270B9886BDE71DB16BBC8DF443FB7C3BF5946BE824574B0B48775D082BCD9DEC3BD7CC90FFA88FF8D7570241E24CD02F09AC0766B187616E6
	9C0F943D0EE9A7D27DB6C2588690445306E6210F89FF9D579B3AFD4E2B764E36AF69E73E077B1C0F597EB17A5D4F245755ED42CA71A92F2B5B44F45E4F27C1DC439BE8B7293CDEB3F9DD3F456B953FCB3DC898D6673F6DDCD157F35B99D5275DF6226AFC5B4EADF55AEDE7EC1D326D4877156AC8DD37C27FEB7234B10E7B01639E22E5925C77D68B1DBFB07C844A97416F9189C1443FCE79BAC5F1A699B7CD017BC1462557117E83C4EEF338B06F7BF5F49F5DEA333170BFE89A2B9D3A932E0F8FA90FD627D941F7
	56AE447D406BA2325537FEEEECD5568F3323797D7CEE4D0AFE66BB5267FC4CBE5F23E30218B754F6BB1362635FD05B6DA0BE5EC80038752958CEEF767AEECBE8AA73E1D172B38E1E0929DAFBCADDBE67E0FBA76FC57D4C975F67FCCC9FBFF7903927262A7554C2716929EABD3D4347A5B3A16E581B5867FD335BFF3B5D353B67F67B5DEE6DF67B1F5D3E6D76475D5A6D7658EE5FF6AB4A653A78C5DE06BF3E1B0CE5593CF93F598EA3D2329E45460D66AF080CF27FC11BB14397F54E756031994B7A9FC00E4D496F
	97AB5E97F61E7F7543B57F8FB42E76B9675A3BFCB777B35F54BE67E3F6FB819D370CFFD3ED4F17D0FC621BEAFBDEA420BDA7C3DC755F303FE2F7F23D7D612EEEDFFF78333EFF729D6FE73E48BE67E372576492B9A37F26564BBE0A0F7E1BDAAF4F69D1AFF190E71C067E700EE6B57D20E99E7F97B64DF70C4768EC23FA9EB3CDFD5EF9B79BE7CED31FF7FE7A9C1EF7CE003875E9FC8F52EB7DA8B4CD764772B38E1E09E9DA7E38059E11E26F8E457B8C4379EFBB8B724EBF0DFF14F9E452ED763C8FAEBB7F34A34F
	AB8D56769E22737429D03799E97C8E5FC13AF77D964CAD5E62F7788E621E60AA0E7B7BC13CB73467AD3423668E0F0EEC0A777725732F2FF99D671F46C502C726A10A77254279273D717F01869D82486F8D0A77A97A73CFFB7AF92541ECF6E2B31EDBBF4A6DF8A73FCFB8005B71CE014C8192F9F8BF8D27737024F33F05D7D2EE4F7AF87BB75168696C06FC07B88B6F8D963C0D7A38FBBF5E9BAC663822832823B20E9BD0096DBF68ED5C631932457B8C72BB543F04C1FCA4CFF371C16FB34849FB081D49F7038E86
	DA1FE7E59A8F11B213203C94G3676BF1D2F25673D4376E6D641337C9DF626061DA779D16C69BCC916829AA02F659D4FF912CEF8DF8A93C1F226CABE27D862B9CFC2167EBDDBB5ECFD7F2C57DE9079F69B61A5884A8983E0329C7C1248928D6138F7F0EFFE6E37C49F0C67CD87106734F7344F0065A8F89652219E53994FA42EB81D71CC56F7983D5BAA711CF6D9BA52336E9784142965E05F4E82BCFD4013F75251156A3C74BCC07106759BCB3E5ED032E1732655B88ED9E641F1D0A37091120EEBBACD96DCAB9A
	1F0E6BBFC3E85A19C436F7CD0612F77B3A9FB12B015A97AD7FAE816F74563E0B6597323BE1908E859873AE6AA57E10C7361B7777C2AF9BD7F3BA1683380EB53E4F7C3EFA58C3770135014FD79883FEE7433FEFA96DE73E56BE67E36351EC1156613B6AF172DC0A57CFD70F13C7F07BA800384E6968F76F958251CF1E6EEF1C9CC26BB8FD3AFA1CBC02620577546364157CDEE0BE44C96FA1FD6B81A57D4900CFFB2FB73EFC8E19CE4B6F0B946F6116735F2F5EF9DEE9303E60D9FEAED948C0DD8D23E70ECBF8B81C
	067770702A5F65FCB0A95F3F6C5BFE979A7E7AD2399FBC60BB279DF4C0B927E5FB02EF8A46GB43C4F6DFF53CC5EBDFD8BBB344F76041F4CD47FD6G991734E5706F6070EF84F5F3DD67867C06C60C068E653BD67C9E6C84C8436EBFEE655FD7CD831C70BE4CE3812A810E3D0FBC7C2CC37FB760FDBCCBBCDC27BECB7C2E20BC8B7C18AE70D962FF6854E709EB3C4EA22F4972F71618692049CB873140D7427B3D59BB6952E17D92FA8CF7D60775CBE4E03F8D7B633E4FGB966ADA1C792669E6A309BF7D89D87C4
	E937E81704A56C782871700E7D75C649C122680FB0C8C2A1B9F0EAE4B5EDB43B0DE4DCE4EC315AC437CB78325951DEEAEDEAAF6DB859DEEAEE12CAC50BD5AAEDF4196DEE1BD9A28BFD720FF030FF19BAA167C7E1D10BD534D98E08A7C5972694778A8BA8761048BE37C5B6D06B05A5DE389D9DF60B0DFE8DC7929652DF86D8E7B35BDB57B1921575C23ED772EF452E93B044492E1789A21EA527D11DCE4A1653A9891B5BA449D931EE5D29D327CA1B1B4DA0D1F3DBB30853312E3D49E635BBCE2E63BF4A324ECAEE
	4B1636C96DB6D041FC5E776D258B70A9A7B7D5E43DBABACBA7E46F95E631054D3DC2B6D91D5CAB6450454B3DC21664F0D26259CAF09326E62B311210CD36490154DC2F2E7D11EBB73F1E7067A36EACE156DEE1A999519E96DD6C0796AC6443FD722FD86E92F21D8EABC4829E78F053C70EDB6A3201028BDA8E11DF74F2CB2205FFB152AD890B19F6BBA42BED5D9E333B0D56A4A8B85B8E4A77E8AE336698559F3144CC60EF91650F64AA6539965DBB7F73FF6F1B9A1BA568F689F3EC8E33E51739D9F238768A39D2
	1BCBF437B9EC16CE67B75534A0BDBD2CE5203AF0894B3938C438D26AE51049631DD7074CCD1D3BCB87ED4BA52C23A909E1167294161DEDE6971092DDFBA572FB218E57AA9EF741CA690BA7F7AFB92A93B23D05121802F835927BAA5D85DCCBAF3EB5F154CBD569E910E51710C3FF5FG8867FBD29D68E8EF92DD970E993E42901AB2187065A46A21F51AAF8933DA4C6DD6DB973C54C29DB9DAB91EED7750E0815182C664CA24ED109F21GEE0CAAD5E09417287472096B075F784F9F29C41E2C1270892BCA483A2E
	D5F4342F2BE9E9315AC5DEC0E5A7B12C953CF81226D415E6E16750457BA6FD548406D6ABE4352FDFCF7FEE20FFB7123F9BA8E68345EC58883519AE847C270E6F6CA4B2AF6D84CE66F90FA4B32FDBB2B07F32CD9DBAC13F0B285066ECB36F1555DFAF981AE8F3A2879440CF65FBA425F941DEF54DE04C4BBD78E602E731071D66E6E2523996336BB8AD01707079CFC2EE0B2B35099210845DCF3BFE8232B4C4223F2C9766C8ED645CA931B5F01A1E229B487188B9221279150F603F137E1304BCFAAA5348CED7C802
	FE3F68F61BDB610F1FA6BFCBBE310551DE7FA4E1E39571BE5B09DFB0C295F99C393159ECB7B6090084E7E9B4CB468AE355BA1AD692EEE1196413DE905358A63AC5664A0D272CB61B516E10C8E672CBB4962355EEBCE2F5C99DE61BF1278811AE51C9CE2AB8DCDD2546861BE8F63304C6B30DBB51E1F5C1A6D6D8315BA9B603195B3A0C25CD76D61B518C4C140277DCCDCA6D82B7ECECF3340BC67651C3A83144E8EDB15AC551A2DACAE5FE2968869FAFC33073FC5D85C1AFE40D08A4A7AFCBF8DBA321E564CF9179
	3302A67B047E7D34FF26DA56C9F32F22355C7253952C1635ABCBA18763BA2F37F2D41FF7B92ABA356D8FFFC7651C72AE530CC14DFE6FB245FD4846A9099F2AFFC765AABA4E4C7B48E4A87E087F666EFE72DB337E4E519EEE8743114C3663B5F68BB9D33B23CB92BDE74CA38E10725D18C60EE4F1B79CA0FC7F224873FFD0CB87885A17655473A8GGF0FFGGD0CB818294G94G88G88G86DC75B45A17655473A8GGF0FFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0
	CB8586GGGG81G81GBAGGGADA9GGGG
**end of data**/
}
}