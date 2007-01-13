package cbit.vcell.client.bionetgen;

import java.io.PrintWriter;

import cbit.gui.LineNumberedTextPanel;
import cbit.gui.VCFileChooser;
import java.io.File;
import java.util.Hashtable;
import cbit.vcell.model.Structure;
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.ReactionParticipant;
import java.util.ArrayList;
import java.util.Vector;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Species;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.simdata.ODESolverResultSet;
import cbit.vcell.simdata.ODESolverResultSetColumnDescription;

import javax.swing.JFileChooser;
import cbit.vcell.client.PopupGenerator;

import cbit.plot.DataSource;
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
	private LineNumberedTextPanel ivjlineNumberedTextPanel = null;

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
			if (e.getSource() == BNGOutputPanel.this.getStopBNGButton()) 
				connEtoC11(e);
			if (e.getSource() == BNGOutputPanel.this.getOpenFileButton()) 
				connEtoM4(e);
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
public void bNGOutputPanel_Initialize() {
	getlineNumberedTextPanel().setTextFont(new java.awt.Font("dialog", 0, 14));
	getlineNumberedTextPanel().setText("# All text following the occurence of \'#\' character in a line is ignored.\n \n# The model consists of a monovalent extracellular ligand, \n# a monovalent cell-surface receptor kinase, and a cytosolic adapter \n# protein. The receptor dimerizes through a receptor-receptor \n# interaction that depends on ligand binding. When two receptors \n# are juxtaposed through dimerization one of the receptor kinases \n# can transphosphorylate the second receptor kinase. \n# Apapter protein A can bind to phosphorylated receptor tyrosine. \n\n\nbegin parameters\n  1 L0   1\n  2 R0   1\n  3 A0   5\n  4 kp1  0.5\n  5 km1  0.1\n  6 kp2  1.1\n  7 km2  0.1\n  8 p1  10\n  9 d1   5\n 10 kpA  1e1\n 11 kmA  0.02\nend parameters\n\nbegin species\n  1  L(r)       L0  # Ligand has one site for binding to receptor. \n                    # L0 is initial concentration\n  2  R(l,d,Y~U) R0  # Dimer has three sites: l for binding to a ligand, \n                    # d for binding to another receptor, and\n                    # Y - tyrosine. Initially Y is unphosphorylated, Y~U.\n  3  A(SH2)     A0  # A has a single SH2 domain that binds phosphotyrosine\nend species\n\n\nbegin reaction rules\n\n# Ligand binding (L+R)\n# Note: specifying r in R here means that the r component must not \n#       be bound.  This prevents dissociation of ligand from R\n#       when R is in a dimer.\n  1  L(r) + R(l,d) <-> L(r!1).R(l!1,d) kp1, km1\n\n# Aggregation (R-L + R-L)\n# Note:  R must be bound to ligand to dimerize.\n  2  R(l!+,d) + R(l!+,d) <-> R(l!+,d!2).R(l!+,d!2) kp2, km2\n\n# Transphosphorylation\n# Note:  R must be bound to another R to be transphosphorylated.\n  3  R(d!+,Y~U) -> R(d!+,Y~P) p1 \n\n# Dephosphorylation\n# Note:  R can be in any complex, but tyrosine is not protected by bound A.\n  4  R(Y~P) -> R(Y~U) d1\n\n# Adaptor binding phosphotyrosine (reversible). \n# Note: Doesn\'t depend on whether R is bound to\n#       receptor, i.e. binding rate is same whether R is a monomer, is \n#       in association with a ligand, in a dimer, or in a complex.\n\n  5  R(Y~P) + A(SH2) <-> R(Y~P!1).A(SH2!1) kpA, kmA\nend reaction rules\n\nbegin observables\n  Molecules R_dim  R(d!+)      # All receptors in dimer\n  Molecules R_phos R(Y~P!?)    # Total of all phosphotyrosines\n  Molecules A_R    A(SH2!1).R(Y~P!1) # Total of all A\'s associated with phosphotyrosines\n  Molecules A_tot  A()     # Total of A. Should be a constant during simulation.\n  Molecules R_tot  R()     # Total of R. Should be a constant during simulation.\n  Molecules L_tot  L()     # Total of L. Should be a constant during simulation.\nend observables\n\n\ngenerate_network();\nwriteSBML();\nsimulate_ode({t_end=>50,n_steps=>20});\n\n# Print concentratons at unevenly spaced times (array-valued parameter)\n#simulate_ode({sample_times=>[1,10,100]});\n");
	return;
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
 * connEtoC14:  (BNGOutputPanel.initialize() --> BNGOutputPanel.bNGOutputPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14() {
	try {
		// user code begin {1}
		// user code end
		this.bNGOutputPanel_Initialize();
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
 * connEtoM1:  ( (OpenFileButton,action.actionPerformed(java.awt.event.ActionEvent) --> lineNumberedTextPanel,text).normalResult --> lineNumberedTextPanel.setCaretPosition(I)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getlineNumberedTextPanel().setCaretPosition(0);
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
 * connEtoM4:  (OpenFileButton.action.actionPerformed(java.awt.event.ActionEvent) --> lineNumberedTextPanel.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getlineNumberedTextPanel().setText(this.uploadBnglFile());
		connEtoM1();
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

	ODESolverResultSet odeResultSet = new ODESolverResultSet();
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

	DataSource dataSource = new RowColumnDataSource(odeResultSet, "");
		
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
 * Return the lineNumberedTextPanel property value.
 * @return cbit.gui.LineNumberedTextPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.LineNumberedTextPanel getlineNumberedTextPanel() {
	if (ivjlineNumberedTextPanel == null) {
		try {
			ivjlineNumberedTextPanel = new cbit.gui.LineNumberedTextPanel();
			ivjlineNumberedTextPanel.setName("lineNumberedTextPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjlineNumberedTextPanel;
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
			getRuleInputPage().add(getlineNumberedTextPanel(), "Center");
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
	getStopBNGButton().addActionListener(ivjEventHandler);
	getOpenFileButton().addActionListener(ivjEventHandler);
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
		connEtoC14();
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
	if (getlineNumberedTextPanel().getText() == null || getlineNumberedTextPanel().getText().equals("")) {
		cbit.vcell.client.PopupGenerator.showErrorDialog("No input; Cannot run BioNetGen");
		return;
	}
	setbngInput(new BNGInput(getlineNumberedTextPanel().getText()));
	
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
}