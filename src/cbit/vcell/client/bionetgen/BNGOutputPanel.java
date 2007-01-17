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
	private cbit.gui.LineNumberedTextPanel ivjlineNumberedTextPanel = null;

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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G32CDB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8FDCD4D57638ED14ECCBE51B969535D6D454D231AD15DBD436D1319B15956DE2D16BFE250DF6ED177D2EDB56DA7B78238E06080A7F4AF2D45C45C284A5852282C145C2C5C5C4C5E5E09E4C68B0B34EBC7EA96A6F5CFB5F396F4F3C99044D7D6E7E7CF81839671EFB7E5DF37F3EF76F88F94B474EC8CB4F93043499027CEFD31AA0A47DD6905EBA193296F193F6A7E6892AFFABG668937FE19891F13
	A0AFE17D442CDC21264B846929104EDEB4B1EBB1FCDFA09C9F480E05AFFE64A4BFA6889B9B3FFCB3301CFFFAD14EAC1A7E74E0B6FC66GCC839C792C5D138872B79FDCA263176DE1F80434F4C1B06FA5B4078FAE13F141C0B396A089A0D526B7818DD360220AB2192E650A88E16AAFBD69F3889F95CF845BDED6B6C47EB48B6F2615B692D997D3BDC919D8481B8EC00C4F90EED8D58C1F158D47BB3FEAEF33CBC5BD6D2243D1546E300BCE29284D6EF20AD227682C28583CF35B2EEE495DADB5D81C22A30A72FC66
	2009729004BEC8D76EA33815A5C44ED4C8E7C3BAFF9F62BF5FC070A360FBA8C054BE346D4BFFD64A3AB71F3ADF98DD37710339FEEC0B5B47ECCA5D275896EAFBA5FDA92D07CE09783B30A9B12B81408A908C9081908770912DFF2777D3782CEE13EADCDDDDAEE7355B3D59E1756CF0D9C587FC2F28009C9937C7ECF7F92C02406AB864E02101BE0100153BF6486518CFA2B6E0FDC76E39CBC87A69D569338CFC1294F1AAFDD293734534A645A7CC768BFEE5AF592F0DC72D6C7D02DAF6708B8999B38CE4070FCF48
	A81479D46DD7E48F6CA7F5FD950D01D6781EA673E5781BA9FE3C8A4FE2330062C9BA89722A1AB1B61AF3D0177A4C1B045C122B8D7D907A73107470E626C3CC33DE17FD2504F7CAB31339066B6221785A838A1E6972850DD3129E8BF9D38EA6E63178F2771036DA561218558860G88828887988F105002B1F63FF75DB946D80345A3ED6E36BB2C22C7E0E55D52A278947BA40FE53B53ABD91C6DE21345E33734B9C42FA05374CB4487ED1AF2E7E86C37030CED22D374D8A4E836405D52A5CA22474BE8C6CA73880D
	C724390D76AE9184F4398564FB3934GBE9D962F344FED35C822C241722F16C81D74D81A00A3B0006F4CAF577752768E76E79FCE4CDAF6987DB03F1B707BD674C0EFD1D1D16FF29D6FF6B792CB8447E162675F533E23983E579DC69FDF53C7F0D1100E63FC36F74FD7712935379375AD1EFEECE3E1A539A4EE00FE9A61FB847A1CA398BFFFEC4678291DF327D07862984378D972E8C2C6559196B74DC7746D68F64A1F68B8827246820CBF02FDFFD1C9331FB616FBC45B4608FE78BD7F08EF7B1BF6589747E4EC73
	ABE316AE1D21EB4BFA7A9986B86677D9A7C99DCE823D2683ACBB0A3E8F6AB633D85EEC710A983D3BBC90F822D52989266721528659378B85B6D692FD4AE91EA07CD3EED30BE519EB6538AB81789CE0974017G1F91F9G2B813E60BE68233CCBC12F9600A000C800840069G792D09D975G92C098C0AC409400BC8B0CBDGD6009000E800A4004C36442C42B62C436A8DD5E9CCEF357F74B0CF7EDC283261A3D57E8A79F3354A564FE4BBF3E4BBF3E4DBF3E43FFCA15B1CA35B6CCF762EGF97FA940FCB91A7AB2
	43CF7CA14CD14563825927CBF53CF2E4BF4E15FD3FCA76FB0E4A6F9F4B1F9F487ED65701BF594B7F83FCB5983036F7320758AE7640B42CD6F40BCEABFC714AF99774907F4A63C4070B1799C971EE98D7EA20A377F27C25BD440796C972585B3AA5F12FA8C9F6E72712BF064A72F6373915A189E5057510FAF27542384269EF6CA1FDF23B4B6994E9A7400B4879B753B1C1F408DD20F40D2B5BA9B17C6D3DE46EFCDC6C174724BBFBE78AE4CCF1F413710D4DC723AC24BE5D9EFB97F4ADE40894425B48F811F6E809
	4AC70D00A3736ABE788CE9437E3EF795767779A7EF97469FF5279B4DDDC2EF39BCFDCA9B6BEF05F6253FE7BA5D50E72CFBB89D1BE43C1769EAF711325A19B10B7F854205E53361334D594972CA903FF811096137BB814DF0FFD8CC46982B58E169F6C875F62FC4278B2CEF2EBAC574B1031E25G756D58B71B7BC9BE0E0BF227DC63F2B8D815C0DEC8BB715B1FCA72E43B2D109EE8E7FE4B6179D72E477C84C8CFD165B7DAC9FE5606BC156F722DCABE53E34EC9223BD7BC51AD42F4058C705F727A4DBBB9DF1757
	A457B54BDFF6125435C3B5B7C822B2E3CAF1DD94A64B0B363271B79EBEBF817CD8112EE9BEC73A821165D7090C3EC1E4BAB23A2FB6A25D94C8E7F640185141682C9D0C0E49CD5F04F4119D0CDFACFCCE8290BA19DCC67782274B6DE47381A237G3E57F52A753B218C69DAGAF810C6EE4F293BA557A3515A3DD268D72ED8ADD25CDCD37288269A49B1397E2E3F4099A3AD7B85DF4B9BF4FEEC4F77597C8D7E857722B352B693E2DC43AA040078144487C26EB6866FE09F4CB0EA5E69583B49CE3F4230E2969DE60
	F411000FD551A5EB68AEED64FEB98E79G1547657ABD2E265B3A8F699C008F8288176912B5F4331B383D8EE6EF2DC3564F4168DA3A885DF31CAE8A70F1GA9049E726A3B98DD2613507DF8BF52C5F431794578AE39FE3B547CF6B4A3DD031351591C0C5F88789CCB006A67BE00F451CE46A7DE7ECCF6B2FE2C5F794B9A529752B57BB1CB0F252F485B8B7DF9C545B72A357971C3A619BE5B05EB702B0F50B58F24DBGA497761B4F4D41B57868639102B561058CFF7B8BA1AE26C714CB69B7B387FAB2D8DFF549G
	11198AF9029B7B292BCF2A578155DE2F2B5DCE17A0F29F3075143ADFE538EFCF113E33436E71CA5BE1606B437EE277A922AFD92348E8F94D36E8FD36407A57BC10DB8F9064A6783D6B49F8C37A8EE6E77B8634F35449DB0456B15726FF60474EB8B733AF452D581977C1E986137740FA5CCBB11FC84CEABE01FBA9AF9C79D4705DCB710A1E9E51235BCB213DBF56678F44150675D92B9B91F00DFEFA2B3A3EE2661E76EB472893F2FD1DD06C98F76BDFE5BBDEABC53F25C2FE01A7B10B0DC77675C48E9F3B849C3B
	5C1FE7523E476EE5FA7CEFF515ACA77E2E1B04F133C2520D76EEEC37052637FA187CE00F22C75F2B2F2407GB0DD6E7EBCDB0E57C420497522AE0BB622CC662B5AAF48DC391B2ED119FADBDD1EAE0B64A5DEAA288F6077A2BED0FF3126C9C0DF5E34C0EDC36559A306B6844F352557F9196EEE2FE2C348332F240FFD96ED685C08B644G4DD4GB6060E6ED30F21059260817AA43471190D6B6153C2876EA27651A0FABA40AE51DAC0A2224852AB955139E0D1B54D5EC23E2F41783970GD9CBF83A1DE06166EEC9
	F2B90FD66B38547BE7C33C41747FEC9346C38868964B757B61A6529F40FC4F6BF208BCA618EDDEC1ED5BF4A8135F8D63E0B716FDE8932DBF182CB29953390C9620995D0DBEFA432D66938F78D4003C9E64B3320C585A451A012C6F13E5686F8220EB6DC1DECBC43D3FC3A1AF0673AAAA7F577C6DB0137508372D4B313D4B6DA2D3E6C31FF33F7E209C751CGBA18FAD14FC5B6A3BD2BFA31BFF959CC56B5DECB0F6C7441EA917D72F5854AEB84DEA13DE8772D95A4569D50D0760A72BC31084E3EEBEC96E727EC3B
	5C7B48926A55444C8BCC371752483ACDF412E91B5A8B85AABDE6FE01FA44028E53399EDD94EFF75AA53B45E1BFA9962CE17463B9FDE9DFE216238F7DF06FD725448F225488E1D46D91AD4C9103E98AAEE1FC25CA649B8EBC137A507F1B3CFA7F6775A7E61575231EAFD77EEBF1326C68DC560F56098EF7F51B8BEA1047C4591728D3BD488B6AC71D1E59E424D38C5769C7DF7EEBBA6D7064AABA6D30B83BAD0E8274D3FAA30FD310E7BA09BA5D50EC24D355C92C1F5FAC1B27705CEB69F2BBC42FBA8E6E63FC9B21
	CC8867BB295008EF6CC9347563FD3394DBE57DCFB54DD1E4ED2D5E2D16635A4747906011FB8A651C69B76AFF6BCE211CDF6D7B57FC3AE8A5990F3D124BBD5C8E18693F218975EF815DC6F37D2EED7A6E5A6DBDDD73D8DBE2DDCA3D25CDF428FD78EE93764F71A0BFF3GFDF8C72F118FAB87D0C74B7E617A102D6F6FB929DE5FB7G5F91G61G63380CB17BF1ACE1E3771D1FD4894A58ED32DCE4B8F6073C5119BEE1C0FECEB2200C5DD3E15CB6CD4031BBE1BF7A3E6AB40C4D27B136733EA21F545B8256771FB6
	1379DA375B6132D8B7BBBB9DDB6D8E6843D88DBB20C60A76CA9E280AAFF00E71370FD14F7C5AF021EC6AF1C3BD69BC6DB453AF7134226768EBCBB3A22E4539E7CFB39F734E4018F786757CEAB741BB49DC4831C774423411615FAE4FE375DDE37108D2034BEBA73EAF583E26092D935EED46E7F821400B493866G6F87GE7BA0B7E978E6A7BEDA6E362E232EEE9536C039C5D2EA2EBE27B036EC37AE7D2D5E715FD6FC655F784762C660323C42E6DACFBD61341718BA93ECF05E77ADFFF84E3B89872261C45678B
	371CA5B156B082469600A000B0009800249138DE19F8EA03B048731B6DCE8916B39647DE4D0699BEC799A0F20F0B7DF27A4A81F57CB0BFACDBCF7CB0ED0471F3B8A3101FB553F239971CFBB996FFEB8E2A1F9FE74E7BA6BD43CF7CD5493C9BAED0620F792EA90D4451A840C7824482A481CC3FG7D3664F461E0FE43E7D9324318CD63578F6F596AB3C3AE476C08A18F7E05E55F03F19E20814086907CBD34236D7420753F254F307E19DEFFA89DCC2F086FE96B0F15FBE750F2713AF24C1E7919246DA5C31EF0E1
	E2569200D200E6G89A074C2346B1E531B87332B9AB7089BDD7CEB0DFE1397753D77F33D3ED197EA75B33673B11FF21F686D3C50484E11B3281D10A784011DG25G4DG92C0E8905AB9634C1703569F99F89ADDB5002BF3390E9B18A86B5A35F1F8B61E72A92737F1DC10369C6BC7676EA5369A6C6B30B90500E325472B5F8BA7E3658460198C407A75277AF07FE7FA90BE6BAA59B43C587CFA50F2259799597374690F0C6D41F93CECCF7ED935BD75402B8FA08CA06AA234678E816D093B886DC9AF9B1EBD3903
	1693AEB6326729B50B0D6DA133AD591657CE6AEDA1EB3AA5402F70E23445FC0258C27A665A0B511627FDF4BA37D8FB5927DC11AEBD2033056939A0DF9FC32301EE9CC0BC57F31708BECF66FADED1BEBCBDFF64D3CE5FA6160D5416E3BA6C3019E49E25A3D107AD954353E12FCFB93D2F9CA30D4AE5FFB1BCF99F7A14534B8BB2147744B065A57814537BB84CD05E494A61498B76A92737AFFA24D16C6DF569E3AF9E68D281B203B1761CDE13401E7DAE8B46FA7F6A4B6169D96FD3CE2F6708E023581BD1C6F4A06B
	4DD02E0329F1F8BAAC72A92757E13A218EAFB6238EF95FC79DE2062943AF8755216F7BC6BAACDE0BBA0C62BAF46F9B1E8E977A14534768D4C39D6E6D47FEA07B9254213CE9F8BAEC73A92777C36DA5C665047D439BEFF286AD57F209114D3F6D457ED73A8457F9421AFCC17D0C21335BDED1D1EFF70ABB3B3B5A48B3E0B236529598EBEBAD96233696897C46DF02ED6D77A7F18D18F889BEAB5ACD1FBB677EA0B12BG206A873806FAFFA61907B5C3DA82888108G0887C881488E41F9D8539932A7FDAE73B0369B
	60BB8BE33EFB4947F767D647A9BE6574F5DC98E2B44FBCE3A27655C2DEABC09F40E800F1G89G5338FD91E72A87330F3F02F7AEF34D01666159F909CFB93D1DCBAEB5AA77F7DA2ED817978864F17C4070743C53271CCF1B5369425E939CC91FE336C01E9B00612C0789EEB424A3AF453EE05ED132B7B40127BF2269F8BEE73879087EBD3A89C3307D5CFC9363A3A350BBE5C38596F7F71D757FDE503DE70D5F8BFA702C71FBC10F1E1D477040C37DB6AA667F62EC6077061EBBEB745E5047546EDF25137E43536D
	146CDDA2954B7AAF310F3C27852DE3CF3713B59DA2592B5FA39030BDBE1CC56AB165D2ECF71D42FA41375DED315AA5178719B40A964F2E0E8E3E085D6B965BD167A3E97E7DA726997B6FF81A317FCE248576DFFFDAE07F45267B576529F4E3DD1ECBB75665576901F579CD3ABFDDD8FD3DAE1167B9BE6FF46D049ECC40673FABBF4F12E99AA4D7C389673DAC533F9D2BB20D6DD813C96693DED73727DD546BDA1C390467A9BD66F6C96CA26AB01AF2DADE32F8BAC59FDB2BE8F916672F7C76CC724C3C4DEE35FB64
	B726189FFE31E8AE752357ABFA69GD5EB11AC327DD3387DDBA4D70DE27F95B37C5B9FBA43587E9BE678377F1699035BFF578C7F765FBBE3F07B9F1E61AF9E182D619B5536EE94F0CEF2079767A4191761DC614FBD242DD7C23A9140F1997639937A0C66BDC11731FEB774B25F7E174936EDD24759FA81575F1DDBC89D992C3F7736FBDC8E871975883846489CB01C77004C7117613C2765AC1F77F0DB9EBECB67BD17433C87206AF23465EEC1EDCB73653A316AF2FF36141729ED59407D7871A914BD62F214DD6A
	A63247C1FA82C08A173D7A04DA36B0CAABAB8F5285AADC65A85C2F4FACD75761FA6E1BF74CC63EE900F256D1681BEF73311E87C621FE1F52774423A19D8F10BC8A75EB940D6AF93A2CD36E957EFC9327510F7806715BE3D3734B174B17491F75FE798DD4287915F2FC4D97EA7C679C7F1E863F0963FFD2294617F13F559FD57B2D758AF6F6A184BE47DE01FE7B018B57A9632E60B1F50278AD9552269FA6E619FF08FE73FA0D5E6DAF037CFA00D6G89A078073E760669F0D1F2BAD605E736F4FF2936251C7BF8F6
	19116CC4A8BFC566A1DC191815F7252F6C829D2E520006497E3AF136CAF6C9B14AFE2959C8F68370314A3C7A60B344006FD89DAE5A2F6C3FE8E52F674FDE569A490E83BEC9B22FD4784C9C6D4BF7098ED7E8C043E4C76E5348AEC1598F749B3513AA19CFA3FC5AF4BC65772E1AB47CB6A0FE53FED55D6EA865F57B3BDEA3B983B26FD186FA33589DED41FD497051983B8FFAC86CA6C1FAAAC076980C5DBF1D8C34264D9F636FD95E63F4DDD9B606BD33CB6378E7A83ED605E74F72761F42F76D9AA0AFFA8C4E83CD
	337C0F39F3E7990F398BE7799FF3174D9AFC4CDDBD4B7F18DBB8EB70B17773D901475CF94D9F4875595061F1F5C10F4D4A851B7C5BFA39495856B1A67F3606198637759613FFDBA3CD035BFA2FA9302DBFEDD647AE19CB31355D2283A48617DC85E35AD57C7D0C831A78AFC59EB79CA267879C16FE97F9A1AD62AA3ACF444F89D4C23A91E0GE054D52C2FE673081F7492BFF7406A9CD4220B03202BC959D30731ECAA50E782AE9F209640FAB56AF22C1A4463B70D1636B651CAE69CA54C5E1FE74DD15BAB4F1B65
	3903D75FD6622FD65A47242B7D3595111D2D3B1A3509D99CFF026233C395BCEBABEF53F998C967C3DEF0A82E17DF274F0226C0FABA475577639A5AF48D0EDD1583B8F695DE036D5FF5067034C2FAGE074B5587EADE783357F48EB7C5934DE20E7C02F51BF4B2F24788957681F65471E45E77913A02F76DA547F897ABCA8825263B8AEE6B95A94FFAD6A3972D4A0BDD32F7527E7841D0BE5DE2757739E0A4F3BCE2FE7059B754C073C606BD0274A932867940EAB6BC0BD05B04435F792DCBE242BB86EAE0AEB0434
	BB8C6B28E48556D1D098565141D5C4F68C2493G2604216D8D79FA5BB32FD76CDDF23DBF5BEF20733C426B7567F7FFCC71F5576B4F6F961F44733BD6480B3B9E75BFCE1F9F955EGFA5E0038F891ED2F63B83B8DFD944AF18FB6E159C80EB36F473263EE40367C19494F1AC0E921820EEFC1C7F14EED7A917A6E46D622FB95241B815CBFC25F0DF269FD977CA345DFE1BF524D3FA04DFC74BC9DAFE3FF44FC74CF6E3BBFD27C32318ADE0EEFBAB7A4E9897222EEC4DB4750F556141B204D5E043837A84EF4B34CA1
	EFC6DC340760CAA1DD4FF125746E03D6C877F15CE9CA978269880EFB0D6222A19D4BF1C91B5047099C779EFD8EB29D524B4291D7C8F145102E6238CBA92E9E52D60E7BBBFDF6508769D19C67B191DCB82447F35CCB94978F69A40EEBDC0B3A24F25CF47ABCA46F96681BEFC11C050E9945102E6338AF9CF45F905292473DAE91DCB02443B9EED7B7BDF38C69719C57D00BF293B86EAB2A4BB4C81BEEC55C4D94178769C20EDBC66D2802F4B3474DBCC6F08EC887F31C1362C6C3BA1C637E340E47B747BDC77B69A4
	C8CF61380DA711EE9A47B5D15C325B92330AEF6375560BF5D44BF1EDFDD83661B60C75EBAC986B52ED72FB7A3729E27DB6DD2C5F06313E4ECBE37DB69653B3F82CD7D0FC228A4FE27DD18F3ED7B685729AA238FFCE21CE51912853D3278401BF57D4FDBFFC4A5FF9686F82180C1E73AA034DA3569EA07B1AB005AAC90DA0BE2821B1CC7CB5A9825F43FB65904EFDCBEF87DF5D0EFD5E6F0AB46FD510B7ABABAAB63B489940FAB699E27D5D6A966499FEBB76F7FBDB68FC9E52D381326F4033B1FD7F40B3A511375E
	A4B4CFB0BE83B42971F4FA7E9DAC9E6A6ED05E910B343D124EE45EF898E586C1FE4C9D84776C61BC1D7DFBE7307DFF161FF6C41DCF660D643DF7A5FF03A61F68BBF926BADF54644FD015E736BD359AED1B7ED4146038B7486F7DA919BFE6B695481F23122FC99B7FD4FF06BC67207DEFAB64C9BF46583D3587E3B77447983B47697CAC965213G26F1FCAF451373E4648CA2B92746706F758C96EF13E4FDE2FFEC7C4CE21A9F3C92530BFA871301778378FBE6213FEF40B7265F6F4377F373A2495DB9CF6EA05EEF
	0E24731B7EA5814A4BE7718774E36E58C8452E68C82D0DF111588FCD26F35424C87DDC6DCD0A1F92291F2BF51F41395AF448EB3D936B326FAC56E56C1DD8B7E58231A91527B753B4B993AABFD3BFFB2EFD8561B13C7EE872B94B984E73A6D6F72FD03BBADCCE0975C7BF4BA03E483EABB1EB89GDB9F3D309C6B308EF0ADG83GE1GB1F7218F4BF9DF1EF4977A68AEB761DDF8B7264751B4B9FF4952D56E61763704673979704030E51C0BCF194FC4BA572F871D1A6FC61F6D6AC01F8583AE8CA086A089E07A5D
	3826BC59C9576A74F5FD510AE79B994FABBB31BFDD9605740F2B68E161CD9EBAF37AD7B9FDE1947AF7638AFD3B32C5A9FE9B08527AF0D494362BE7683AA2BCCA3F7EFC092DB722746B4F56D5387ECC003C321FE0CCF4523D64B0C8C7F15C6FD673B956CFD04FF0FA16BC7F9E182FG34GC877E05ECFE89EB993C64EC7133354A1BCEF63C92CDBC2BF1CBA4ED149A55FDBD4FC180CDDDCC6203BAD4EA516E62878EA5B6073CD5A7E736FBBA1A9CC3D0776F1E22DCC9BE9E0A35E3911A143F820C9GD8A547C24ABD
	4A3E9A0B139F52B5D765CFE14E8C4064EDEBD064ED5A0F727AFEEAACEF16CADE88500C7D29A2AF7A270ABC663F6F9F8D348E9FEFA0C3FE475FC2E2AA61277A3568CA0ACF7D29FEADFADBAB2EC53323E1BD970D71F69B5D17CF00F48A477D638446E0763D085BCAF145102E3D9763AB51D38F1F8D1036810CG888508860887C886188E3064BE18FB83548358G02G468244GA4GCC85483B9F568A77A35F2914AFB9CFDF87B85B7D6C4CFCB0FCC6G445D4F4E46274027E9DCE2D681C09D008DA098A082A08EA0
	854074B3F6BEDE9E13CE9427B19F464B3A7973BB511D682547939B9AE4BB17487A8FA54EEDFE68E306488701785196408EB5907F8649F22287A9D3AC574988DE57CBBD683BB902F26F0F9110BD59D58678452A76C84E3E2F88404388167A418F776E9A32BFE2F477139110FD883CDF6703C1E8497B82642E9D6C8796888F6F5776DF113B851EB7C7227721D85A0FF7FADC5DCEAB76C32B691A3CG623774E7B83EBD65A1323BACFD762E6E2E3D761322CCBB196ED7B482DDAB277D44C96F8AF0F9C4AD5F43F48FA4
	8468C2B96DE88F5D23155FB76B6A3290F201693AE22F222B2DC2BB974A21737EF140A776E7B82E7FE5930E6BD381E7FAG5AA7C09D006D811417D7CE6A5F667258CF82AF0B235AE16FF412FB8EE49D0B4AF12C8ED64A686650350D0ADE61E546BE0C217BAE6300C7AC67B3E72731DF8A0A88EDB25025F25A6F1B7C7B25F31FA27F69F22DDF564CA53CF29F043E67C1744B71356817FA4035820CG988BB07EC1143760005646397B9599FD870CED9CC55747130047944EE70F435846E3F46FA7BB867A2E9824FDDF
	72EF6336E6C5FEC7375646813ABFD486FCEAE350464EDA34D182DCB0C084C09CC08A1737EF2D5646E38794998F2CB53671F93A1FE4FA887A2407104F8613310D5F52FD26D2202B62349F9F73EF634D879579979D535AF89D5D0BEA85BE0E0750461945E8E3986022GA6G248264BE0C72D27A35B6BEF10848E06B3D9DA7832D7764F360A7C95F414E10DBE53B02CEE21BA8FE9867A5EF34A8FC7B7B8271E5E3F894FD0ED98B65DB9F46F1BD5C4D576E9C374A43576E9CD7C3F1B1101E70B00E891F53761F84E961
	913EE621B8F286165C6342F0CFFA54EB8CC2EB24A361B31CFE1C49182411612FDF9F6EFB07E50F909FFCFC4A9C00A6DF360B55614183342E845CEB995B2BBEEBD8B67BFB06F7F09198A17399ADFFCEDFF82DFC6762E41FF32267661BBF8F335C27ADC3AD4766202B8F1339E67CA37AE759C5941F78087ED97601965C4FCB063C3A7178CE7502A3A426A3A19D87B089E09A4012C7E1CCFC941FF7FFB31B78B8607B460DAEF73D58A3BAD42F7FB17BEEBDBCD47BD8DDBEBA8B774D229F38C398F7F12D5FFBE66A9EE5
	F5E813BF8BA6DF13C6BE19FD375277E6C2A1AF9AA091A095A07367B0EE7C9C6D2B5AA61F9FA52FF2D6CB12255DC6EF8462AF065A45DE85CD9E5A3278FB506D7B8C78CF7CFCDCB1702F83F083847D1C1DD9E7B17ED0CF20988F7B39BA4697FFC23E6FF20BCEF2529B8F63313CC97439659E555DAB72FBDFFBC9DF3CD7FDA7G776B330754EDA477DAC7BA4609DE0FA8D90F181FABFB2D0CC75CE1751E267933DD06FB1A538E1FCCCF154B66BD26703009E6F9CF3319BE732D033C1647308F7C75E96C97C7F05C5B94
	978E69F10FE1FF774699527F477274DFE81A5C7FA21F03BC73DD6E2347BEE674DE79686FD4063E5FE431F27959C031127C18BAD68AE712F5C18D3BAB071FF3E463587FF4881CAEEAB939C340A9761A796588DBE1C0AED8E935DB9C2E4E15056662C2F3C9699AFCF6794EF2FC9FEF5AE3382FDEE2566F2B7FA1C33DAF5EEE566F2B1378D0724718757B6A07B47B72CF1A757B6AD81E6D91B41C42BD0262D85FB18DE31A68C57A0DDA20E986D8A8639C64BBB0FDA89DFD52D4C0F4743955C940B94C776D0A7F8ABB06
	663F6E8E74DFDFAC7A6F4E8E3D7F2EC8D35B7F1B0E407E1B572977DF166639C4CD27BF7FB19B6E68443113257F5AC96E0ED6DF6621394603F676C5FB376F5CD63F65685EAD75DBEA9A376F5AA94FF9F62C40F69C950BED764915FC0F0D63DEDF096DF8DAAC3653843AEF1779F8E2D6BEC05563081FC471647CEAAB6002B83EE3153E4D11F278BD7371214FEF2A9E576F371D7511211E6B8CE7FE633B5FF6A65FF76C98434F450603CEB1G49G539EE767F8D95C3F373AB6009CF55C9348FD827C7A049277E5CFA8
	7382F98C591347631E56B8FF1DD9DE63E612B540DECB0FEE6CF90BBEE7D55F3EA42F43BF7BC061C7AEF8116F6A6A175471D87B84334976843EDB12475B48G60168BEC7F0260583F542F0DF072DA7F18A06078765BEC9C1B167D78E6217657356938AF2877D574932C5EB31FD4462681981B984FC3DD8277515DCDE49F45203DD7FB2041C3F3AF017F6B49F3BFE60BEDBF5AD27964D0EDB963A4A956DE469E45B6A4BD494F96D948DE74D8C847GA481CC65F985B40F1C47A7E7584919EAF22711FC862A555FD815
	746460EDE62CACC70D0BF9D25FEE1E70ABA35073BBF53B506FEF6B5B9D44174B375D3C484F74E647C13C83B483B862187DEC6D76650941570407697E2BE4E9EB34CB8E1155470DAEA58E566DA1ED01DC7E24ED8B8B3CA4663F2161CE2F5A42389195BCE5A963DF256FF24AB7AB30FB30380CE772E773B2C49262C33FD1F86D15485D447233FCC73696DF4270B9D20EDE863F87382768830D0C92993F25E60E0ED789576B52DAFD5EFA1E97D213234F633242FD7356E31E3DF62ECF9E6AF229DBE757FA065F5EBA4F
	370CCA4FCD42FC23FC2ECF0CABDBEB3B6C2F9D5F4E7571D7095CF735176B7BAE92D7C1F1F8BF553CB2237CD01EBF3248A8BF1267BFD7E714BF1E673F3A4BA8BF0167DF3845A8BF0567C7369865CF677969B5C679393F407CA7769B6567737C1F180C724B147C79C67975BCFF72BC237CD61EDFD2E814AF8D42BF18674FDFE0149F768B5C6369BF213471C88F21657DFC0D4B617294755A6C125C9F04F9945AB5DD249F570C8B35F6D8A2129F29A8A8DDDFDEE8DE3FE90DBCFFCC1714F25D1E853EB25A48ADCD0CF6
	F20FC2BBE153B90CBD254A5833348C773022FE01F34DEDE57A39A6B9EFD2A460DC313FCCBF573CD9137F63B27DDC33DCB357FF25CCBF57EC4FD04FB537142F9034F34B3D64AFD928C896D8E79755EC595938E50F00F36D1D830A7DEF2F8DDC9F5127955AE32640FEA523A352562D9D1ADF2FD80BFECD61FEFDE42D5E2F9BB4FE7B73DA3DDF6F554C71D795697DDA23716B3EA23DDF8F48794C1E1EB4451E8D0E40FECACAD7E8FFA38576933D6ED4262D2D9D1A1FAE2DC5BF19FF09FEFA30B6301F5E2C557B6971F4
	F57E72AD017D34FB0B5ECF07B5FE3AE126E2CF1EA9301FB2CD8A6D7B7640FEFAEB36C2BBF95D507CD4D80CFEEA66FEBAD89C58CFA145FABF7DA4D31D7FF0F1E0BF25967B0BA7E643F5A7892E436270AA67EE1F3410F15F6D91BBC40FC73452A7E2789CE0925D6B9E857A078130395873EE1CE7457F121D01C8064FE93FE477C43139D805E7703958C10F6FDCECE936626F2B691CEFCF37C374328358EC6476526BA764B17D6D81326EF198DDD04179D41CA241D1A312FB75F08D51AB2A57904B16D725637ADB2FEB
	6ED3EC8EDC79143206A01FEC6F7103B94486B76405BC05FB0DC50749EF33B4D90E791E9E87E403476596BDD2BF4352DFAC1067561F9DA61A316F252A6FC7DB4E7FBB29FD67D506FC866AC800F35CC77C1C63BEE2FC9EFD5D1140E75FB79D717F1BA3DF9DD17F66882B47B5B9245DC4C29D4681CC819062E16E900FF59AFF046C8731DB6C74FB47DB2C1DA28EF4A87F6E237EED3D6F68603F437548D17A8C4F85815FC578A37E7123243EA4171BA2996E17C7090FBC76CE1B44C8E57C843A36F4089D944D1E91AC16
	7099C1B158E60B47F7FEB72C575C8D475E795D6AF0754AEF7CE21FF6D442BEEDECBC3E73FBA965198869D4G5353787C6EA57A5BAEFB21380D7BF9ABF10B2CDFEB6BEAFF6D843C7A8D28312B6D1858AEB5DABAF979433924CFB0030CCA00D600BE0090007027312EEEE9AD88507E18C1343EBC963BD37E0D9457006F1A73E2FE46FB5C532C1F1BG1F49CF3335B76B677EF8FA70FEAE670CEFBF779689DFDCA36DA289F9CFD7F3561C65A79CD577DF2C3D3E3FDC7D4CE5BA68147BCC60B5BE2B3F8BCE613EC57EB3
	C35D37D8CA50F827D707DAFE8B70F28FA21F7934E7857AAB6899367F95861FD14F303D8C6653D7D78672A95325B2CF715FD315E4BD487623EAECAEFB3B68557CD64E7C42DC1E6F6543834B4BE8D64A6E37F81C500930ADAD79CCF6F31651BDF6F120EF52B33876F85FE76DC8E826BE036F59BF140FED4E74ECE2D655335866CADA17793639ED9E3BF5332513C529FCCF58EAECF3FD4FE21B5B301A700C02F49C4024E707DA17C74930093F7751616777BE28A7188ED1921E8B1B76ACFF17C9938345939233EAA784
	0E815606F7ACA27B1B4DC0AB810482C4814482A4812482E4BE171835842098208E2085408D9084B09AA082A066B9EC6FFFBEC666DC7E5A3B6CD0254173986E525C61B48178A5BF97D8FF763C667E2366GB45A67B56F6FA6F3ABB20721BF4951E069D45A755CCD2AF60FF79B2B72DBB64C663149B298FE64B7243FA7BB9BB84FE1F1761E8B63EC9A715D7398E71DABD7E37D363B3A0A6CEDDDC55DBDDDC516B629C884D7D5D46C6CF6B8F47D7C96B7461C6DF90CB9AF5D2F998B69F1G931E47FD5DEB4B71DCF78A
	475515A14E7C82629EAEC2DCB3471936A3EE9447ADECA0B8B2D044F15C83DB102EB081F113DB104E4AF165B53CAC473D379FF105AF727BA766A1EE9447254CC75CD40E7BF0BE5779D708339572323FB2A84BF15B9770323F46B6734920F7039A4F4F2EDABB3CF2BF2C9DDE3912622116E37D8A7D69A05C4FF47B2C17143D130DA56BE135D4DEDE3C06476F3BEE0C5FD1602F38DF239FB71F40D89492B1965D74DCE399249BGEC09782EC34ED252B6C7C0BA8CE0BCC0A24094004C095027G94CF44FE63F0F620F9
	82BB7024F49B2CCC6E9CE2AB1FF732693BBC1FD9BC070C837C7EF27AB3C46C2AF616DF1263FFCED816B3781C30BA0778C10439DCE33FDBBE873FB5074C8DDC9D9DDE114FBD3F4D4965386DDE3C131D556B475270626133E116338E399CFB4725131EB52F1F28FF7745CD714D93756F3EDC496735D6480B63F55C116B5F67DD39037B3CA757F89EBE106BBB8F3FA057F89EBEB257F79EFE57G6ADB78D2E21675A50C753786B05643DE42D84FBFCD5FD1077494004C1771D96BC2B3BD1389E972DC1D61BE2438D240
	11E7752C8EAE561CD1EF00BC6B4B0147B9466B0A8E64CF1EBBB35CD59D481FBC4BE6365C3082ED6983FC544BE84B6FD6707D341751163C1504561C1418D586501004342332F0DEEACBC25AD0110EB7109E87B00153FEDE41471BA46CD79F73B90BFEEE71795CB04B4DDEBD3C383E0D4E5B4C2F6863BA0A628BDF5147F579EA74ED9964053D02765E2BE9D7422B72BBB872E7412B3E754B6A2F1857E99A47FD4E6BB40D472CE5117FF6E3DBB4F83BB93148385D74AE72EDB7830B0C5B4D7796295B0D5C1696119869
	04B9DDB3278B6138839CF78565C9F07B6DD63A7E14FFAF0E6AC570F5A29108786B29DE3D9457AC63EED2618E48385B96117E96CC3639BC7C3E12C574FDD73B434152D114466E046E177B63DE2A235DEDD1697DG479D60F591345F37FE3E3FDFDDBF2C7EB35C430B5BF943AC774D104B3138FE1D0E69152F6A63BD15626BDE5547FBBF1F83B4C25E38D7B15E3577FBAF7B8D0B6FE2793396BE1BFFE3747E568E4DFC4BA1530C10BFC743E704E139470A5465E2E41A8972E7B2FCCEB3ACB7E73B3ADC76A4F94FD07E
	AC054F3AC9C665BEEC407D3796D53E9B3E87496950C946FDF6248EBF9E528906B29E592256ADC52619AEFF667EB6B1AB7F37C665D2785EE019AA3F9E3E374AE96937463A856B70E1100EB21471D58D4A08D565A74277A9F2DA789FE399F9BAFC8124AB7F47C8C6BA3F972CC115EF056FFDF2BA440F0C31BAFCB424638CE5B44CC399C92A7CD4781EB9192517CCB616D12843D7C13AF132110CB767238C1BAAFFG3E0F125361FEE40C536163A1BD49D04672796AD819AA5318125967B278ACCEB6AA57D5083A552A
	721B613BC3CE0FC8B656ED348E9F816998C3990A7D93D4794970FD1A1C4EFE4DD806D907AF05F45DEBC6B2F6AE506CEF49B4EE79B388BEC38D4B89A706573F668C335C36A117E37DEFB2DDBFC53E266F175F2478F12F697B651E9358AF47C2DE416FF0FE3DF4297FF902F969607304B5CB0D67896B167A4E93CA179A4F93AA167A4E933E1679FA78A5338C3FE5A9590FE17B3CBBACCECB277A77D868B3F9790E8E5577EB24737F7C651EFF030CE81F7B8F4E2D5C4F07D96EEFC3AE47E2B0142EF7C2FE270F4D9BA9
	BE74F77A587C74B446E6B86419DE47FDAF4BBF88DEDE0F53E9207C3B09FDF394BCCE836539567527FC67DFB71D721DFF057B1CF9BF47FA9EE639BF8C399C73D9B0DDEFB9DE577B728A0A6FFBDD6F4B8FD722AF03A0AF65F51CFF0D49524B2FCFD17A3F56945F7E1649496EA07C24947D9DBE0BA9BEB8C5FF074F8DA25E619386F9429BA83FC2735BAA56B7183CBE79B384BE473EE1549F5FC936BE656F77293EFF3D7A7C37376673AAC33E6F0C6E4BC7036D31EF70FB23CA88AE9152A99CE7DA0F6F19CE65BEFD53
	2D7863AD557733433E4B625CFD93FADEE5B01B6F237B52644E2F5A5F239F24FA3C332A1963DE2CC75FD8B96E2D9D84C76E958B66B8EB99728B6338B15FA23FA8256C3748AF066396F3FE13B86E25A264B715639237A2BF539F90D73B8D7965F11CD8077C4AB8AEFFBB722B6738549D482F1563F66FC4FE8E0E736CC2FE23B82E126E4307C3FA9C47DDCCF17110CE6238BFD0DC8A24B327A0EE53EE02DB8269E20E33507A2805F4A347AD633E37F1DC8D3D03AD8452E3B96ED7F47FBF9A52319C97FD1046B924D3B8
	EE69A19E679CD7C11F93ACF993F4F993F13369BDD23510EE6638C60AF3C0FA0063FEC6F141108E673899C768F9E8C8CF60384F687312E4C8CF65380B2BD197539F79BD911B892E9052D59C57CA1FD9B4C25A46F1F7D05CG24C3B8EEAEFD6FAA9452519CF7086262A0BD0963BE2DA23829104E7C93623EE9C2DD16F05CF13A2FD08969860E4B273E37C23A0F631C2CBEA09D46F1AFD0DC8424E3B9EEB9BDC31D8869D40E7BABFD87A1B395E2B795F11F4FC2DD8AB82E091EB72E07F4AB47F968B39A8952419C97C3
	F1E1100E64389574594BB8C8A7F0DC974525C0DA78B357051E5B4E03F4BE479DB321AEE59C57C44B36C05A4DF1F369331CA0C807F25C810A0B04F48C47BDCCF1F110CE663859F984B78D52397F4B7BA1A74165C33A14632E63FE296338982A1F8352A3B8EEA355EFB42443B96ED2FA375FB8C847F15C7F727B7E92B8EE819D9B26C1BA77AF083B1CBE4F4A07F49947CD2438FAC837F09C3B23508669A00E3B0462C2A19D45F12B294E3110CE6038A86E67143FE87D1CBDB5B14BBCD56B67D2C857CD557A39052427
	EA7D5C876951D335FE0E00747829DABFA7C0BA1963EE613ACC63387B29AE4B7E8AFD44DFF99FCBE554C23A1163C269BBFDB6C877F15CFB4548AF0863BE2163ED8424477F95475B5F53F71065330C2A6F83A74E7FF8BB665FA06306530ACD37293E971FBE7F32377F9BE4DC36D23169EA55F7734A73AF3B7C3C4AE0B19BC91F4110FB3492F89CDF12435B8A0F637974F9C15DDBB0DE3D05380FA9AE8852919C97AE7EABBAA37F40FEF968DF12C11ED3873A3BC61EAF5167CD649E5B783778B8CE47CEF2FF6EA40E3B
	135F3BBC0563E6517E075C15DB79B63E97B7D9525E0F718E1D43B7C0FE8B27F920C4FB7745FD9F12F8E36783172F477301A1EF9B1F0B7DC0D04E870E859A828B653C683775F7F5CD26733271000FFF9B4FCBCCA45BA5723D65D36A719D2869101FBB8D7AC80086GB700FC4F8E1DD30E02F49847317BFAE3A09DB78D79CEDF26701D4379CE037C6CF792338A816A819C6FA00FAABAFF8D01F4A847E530318E52316FA05F651FA8FC8FED63F767C03E69DD18E7G5481585EC59E1755213E419C37034E1FA3A09D7D
	AE72FDFD394257369379A6C37EB400A5FF03B1932079EF48633ADD28EF5F5F30AE77B650E774EE4B89722E00FC07CC83BE9B8D65F457B7E86BFCB6F9FDC2FE17A2FDB711EBF5F5539F386E68A0EFBAA32F0FF6A32FC84EEB67EE63FBDDBAA86DF8200B6734FD3AFBF7AE2371B08572277D8D6FEB59406B2AE0BA44B2C08B40GC058F464F33059773E15937C775FE3B85D1BDB3472B2681AA79E7293B94DD387353ED83BD22907E2FE873F693D442CE5G15GADG836FE1795DAD46765BDBF81CF25A9507FD6B27
	0C5FB39B43690E9F566A5430CA291F89077D574F9B1C57844EEB5991E37DCA695AA29968D2B96DE63756DF4DF42D117BBE587EBE564F75557CDEA44035820CG988BB07EFD647370E65F7AF9F1B3564FA4CE57DB636B0F4BEA5006D4CE77E80D569F5EFC459F7F28756F0F2F7979114C3FA3AFCF2D31BFAE2134CB00AE1F53D6D7E97D311F2EF7EAA13F616F688FCB937AE38460C683C483A4GCC65FC3E375F579F576CC7FF648993E53AAF1AFD7D312799ED4867F497B5EB7DF155EA459F3FEF766F0F8CFEB7F3
	996735710031BFF6D25AFA20EB6634592DDAFF2CEC25F3E3488FA2C07DD1BF8B7D918D38B80094GD35A442C02B46453E5B238AF49047E2863F45F1F676B0F31735006E6CE7742BC2DBFFE768F459FE5737D7BE377FC6465603C02669B7BA30C520EG3A90CE1B5D2573873D2BAD9272472521BFAEB021BFD2GB79DE0D97A442CCA0016F46E0F79867E180F7E9860F47FD8606B0FAF97208DA11C4E3DC06B0F24FFAA7EF8F101FFFF7C11739A4BF92D48B376C7B9DD83C5835DF8CE7B8327569FB752B949A4481F
	120E7E38074747320C09D945G8DGEE00D1993CBE8D62633EF9680FA8CE37E33ED6DEBB7559F8480F67B4616B343ED8C5DE83147B77ACFEB758F4204F4D84FDG9AG5C19D87E5392E37BB753756DA8208B633437967A564FC3053CFF67F42905DA1D36FE2A544F3E357E6B47494F8DCE603CEEDCEB2C5F4FA8EDB25025F25ACA1356DFFBE81B4B1D81B1B9836B67C99E2F7500EB859881B096E07C0CF30B57C9B34EADDED3E7780BD77BE7C30D574C1967962FCB00AEFFE660F82D057C0619680F986E0F91009B
	8D908D10GB01573F946409F2FF1FF644DC23A0185DAF9BF247A67C3FEB127393BD86B0B10C2A5DE73CAD017D62017GC683C483A470721F2FB736FF875DA7188AF402E922FC3E6EC5FE66408C38B200D6G09FCBE0EF7045C18B3589DA199AA880132E381700E10E8783E9402005C0F4374ABADD46E88A1C7A474F704ACB8C45EA9EDF3F6125F2BECF038543F812D5CC1CD6373A2F61F4BF81011G40E83ED7262619A25BD218B5B12B8A0079628CDDE7B5C35A8A407C3C199CE31657BD319DE873E848B732F906
	4A66882009491A4857BD9332184D643DC5A6EFA30D2BA44027F0F9B714ABB1FEF549504EFBB6503AA4BA18E6CFB4B80F016FC8BFDA21FCDF7915A24FD6FFAEF78E1571BB07666F40F6D6857258794F2A9D7A73254420A2814F07FAF6684F175E28498F5F21BFDF7A3A66BC732FF6684F176279D5E6CF70570ABDEFEF8AFC36761E4D8A6D773FBD875B57AB77ADED28C75B7B386D16FA3D6DB59A5B2E2C575B3EC2137FF83D5E762FB577D53DDD2F377D850D6D2BEA95FB22B7991CC1EEF3D85A0FB35A33DB955A78
	2DC32B778B36216DD15C763B3785367D375B74365F2E295785F5FA5BB3B4670A375469ED7FD2D01FAB3E3FAEF0DD3FDC27581B3C43CF3B720A9EFB074ADC595E4BF6223DA95C5E181DFAFB2BB5767EF1275E5E459AFB4D3B7476F6EA6E565A33AB303D313B7C55AF4B3F2041771C757A5DEA9C73CB75B70ADF8E14870E196B6B955A5C7A217970GEFAF668F5007A3FD5A0B362F7819CFFBA9511C597F73FE3D8F3BB5BEDC552C77A1569133A1182D17BC5D721948F7F6AA76C59C9CDA1BE8BA44579F5C3E20C301
	5B44FD077476BD2931AF65105E3E6BB4799F3684366F07AD7E5A844B5FF558B7C604A33EB1F234C1714BAAF76098F9F00FC2FB32FAE8B172D8B57AF09477611F2B833733D51B75BE94B5BE5A37B9F00C0C5C2C7761979A9FCE590CB142523754787A2C295657E7052AFB2DD7D685765915CD8A6DD77E6E8173633311CD6833B86E33FB1B82372B571B74BE5B2469EB178E522E3E5D9F38EF0A5A2F75193D5957E79F9E707559BB2AFB325FBC9A58E77BD477BD2F19B5B41FF9E6214F04EC745958D901FD767CAC3D
	4FCEE8FC16E98A6C330D2640F1F61DC96B337B67797A4CB65F57E78F9ED2794C68FE96154F969FD1E80F180666335BCCFC0F087B6CC553A0BE1B2777591DDA1F4D9F44E773877159FC2D4F9A97787AECC61E2F4FCE9FD57C60F0867659C40BC2FB799063AC1147191B7BEC66A0BEABB369FDB6D96333E30344D918CF1CE95B66537334E31ECDD46C3B405F7DB2FE6CDB476F170960769D7639DFC65BDFDF6EF33F4C371AF95123BE774BE867C5FF7139DF866DE376BC233A03F6F1E160B9D2C221EF3CDC3456B7DE
	CAED8A4F0F0D6E76D1454B28E38AED78905B18076FE1CED27A25F9C3EDE38BB37F35B67675F752465EBAFE6EEDEC0FD3213DE608BE7B9F5EAFAD1903BECB1B9F58E79B666BFD36CB63B3F1901FDD6D63B3644FEC18BEDF5B467E572D5897D2BC34F9E5C5895A57406D3315841ED7DEDB22376FC79A7B62CB7476E967156F17686D5B21E9E34D2A7B691EA874B747146F84484277F903C17F71GEC0F44741141FDAFB5960FD5FF8F40EC34FF9A14E567B2FFBD0770AC4E1918D587501AE3345791686E09D3641A9E
	197F9C876E477565A07FC3743E4BA8C84781CC4AC11D5F6ED90FBA37375925221EF65161A8EAF758C527D454E6F7B9C529D3F4D6D4EC5E39CD33CB05329EA8C3D953382CEF4B29ADF34196005639388F746BEE4547576DBF07FDA0555D30FFD56E021EBBD45F3CCB4F9D306F2DFD0A8E65AEFF754C5E035837DE7DFEF494480D1D6B3BC72686D6EE710E613D0FDED6BF3CF297EF9BDE39ABF78E2F5C11E16AF9E8183F7590D7BD3CF2A1CD43AB57BBEBF865A2CD43AB774AB065158E79BE89593EF943AB17B84CF2
	DF178C2FDC466CE11E479DE639EF06DC0E1D3B78B6075E5585FD0376BCC8A345A72B7072EF844F4171A395729AF3A762BBE00EC07247657A76BDCC4E3334EF0F4F1D287BAD5509941F2442B379960AA769A91057B08F65BF92709E1568F97E64BFC27B7B38F9FA7B63A8BEE11E5E7E5D65E87FA4482B1D0F725F555CFD9481781879017BDB76BE1803BEA3501EAFBAC1F15A73C574F59F797BC1557749F34E7F3B06534F2B8CE6739A873EABB7017B74C5871937CD65695F50F46E82CCF7BA3E8B3D267A29A7A6E3
	44F7A2A3A3206D3FAAC35B0B39ED0F17ABF57C0C6A7B7E7273DF5F6E73AA0359BC2F53B76E97F52A631E5D7DD665987CBE3D2F5B49F765D9A57D8994799E6F5CAE755CF93A1418C55EC9ED84BFB37EEF72671036853EF14066EF5B3AF07EB6GB48C37209CF1513C4E6A3B54FD40949DBFA14F17BFAB37C4F317C19E509598507A7A301D6A95F1EDE2D6A550B35C16CD98CB8D9C372E9CFDFDA2934B1616232FDF4B40671FA1141F8465C66461F375967E3B03D1000B85C88648DCB8B1ABFFA14E0DE3CE8A3CAE4A
	B78D6DF7D23E37897DD83B9057D38FEC522F277EAC2857C3D3B66957D3E91A7CCFFC6E2E2E566CE9B4785CDD0D65D9FD0C5C242E0F8150EB5442F3290F9BB6211F09BC796C5DA674B331E1B17551464F9FD8A68811EF8AB3A4E14E1EEE27F9335D35D3143611DFEA1CD78341AB09E68BC1F611DFE01104A56427B84CAC3E4D962F193CF5E06E30BBC4C9D8ED13A4F7453AF5EC696372F42E636B1EF5F672434EC5B6298BD82C1169FAFBFB0B8623AD8840D36A16DCE411B3F452FF86A075DA3A5C8E51BBE44AD581
	A8BBACA7E42A15012998D18E7969ACF5C54CA53F3FE5AEEAF3F6BA282F3D9EE10EA6249F3F71735F453F5A644D90E6EE97161237C5760A9EFA5117E8A54B4DA651C3AEB65BAAE439DDF648847CF6A153CBBFE6F825FE07D8AF64F56C91CFF40BDEC934D66395E2DEC9D8D8CFA42D6B166C0EF5F5962FCDA2EF2CFCD1AFE4BAADDDD06973D93643626CDC476E937B220FECE924D537120648FA9B50F7815517F0A962DC96BCB8F7750AEF1247E58869DB05598E17453A55528E9535DD4812EC9E51EBF3B92CFD6E2F
	2BA8A3D3AF89DE7A72B8307B081BCF0CAD22BDADAAFD3C6F7A10B9A9F33626439A57A32C2314A4CC0BB8053546E67100A851339D4082A273F95E851FC87FF8FA5B6216F4A1CDB67204F0C6185B671604199D16AE3B239F3E54528E44CBF296C8202D476274F638BCDD2019D92799E6F181C5D77E22F84C6D6F9E2482142212301C39920A2E6B94DDDD6B2ABBBA6CCE11B3G3F827B0FB9FBC2D32423195FF74542EFDE7CFB9B44C12D1051D5DCCC7F16503F6B495F920AA921181275605CD4A160BFFDFE9F5ACC03
	877419CB75A155DF3495EAD0FA694DDF5C74F2E56A9430EE2BB02307DCC18715102BD0312B30AF0E9C73718723A77D0054111B04CA9F613B18311F6393BF84BB4FB79088FAA12F8E12DAC2C9BF24FFBF9E32F12454F2DAF68557699FFD5CCA62756D746624C9BD9BAAB2A65F1C11AEE4EE97E6329BA220C111EB9E20C151DBA0368B99500AE9DB48D4034AC2E68F1D437B31B0D3EBA1BAFFA63B7709E9CA4AFC3726D74969A2D69D1F6ACD005AA797FC02DFF2341BBEE937AC3EB077728D5D6902E9ABE9AD8E374D
	32DD0E02FA6103B66A98F2EFA1F4F239CA34533250252437C3FF731102EFA7F8163B57EDE9A73DCE165562B9CEB9904D4A8E89D99D1E4EB6AAC092528FEDBD84E2E90AE4D7548B33A59B397E1AB4FFC9D82C30EEA037A64A992C43927E8E7F5C749F98B65F77D6486A4BA67EBA72E8EDDE3A108EF5EE93D9A54F68A5F7B7D08E9FC1BD5EC9BDC65F8B7BBB7C1779998575B9D6B9C92DA3FFCA4ECFF8EF6ED3E92CF8603F4C0AEF7E3F30E24F7F97D69C6C1304EC7A4388E6F6E929A418F608DE2F25937E7869CFE6
	4A97E0E336669F45FE899362A784A15DAA89E9E6C9D8C6FF67CA19A3D5B559BDD23745E12E01F915A4D4D6BB9CE6075DF95CEB6E354357B6516C02A914E8B55B1DE668924CB0F7934D5B492565B079B237F9DC3DDE518334CE2B2BD7920A2B4DB8E3B43B1CE61B2B57AC394C3D9EBBCC0639C8E0C56640E4BED6A4891D5506B9E63B97264FEE87F4B7EB79D499B430C8E62FDBEC37F758C5C2C0A757281B42078C185D60B7F341661D5B6A5718ADCEB07F4881D737395D62B4FB690FA372522021177C6014394363
	6A220C9CF6AF58A6BADC3D05E6304BC9E6D8E63BC4A87BDD5D9EF33B2B8B7494BD0504B161E26F6887D3BE5ECBA7360676F8A15FDA5D8105888BD6468EEE1515A824D8221CE8878DBC66EE37CAA8E7D8E8F691696E7E35E63785A61CA0G32D94C08B4E6C04C71C6175923DE10B06634BE1DA24804A2B2FB0B316FC188E38FB3ECA602F8C59D1AF20D0334E30DB614C054EFF7585B0F133AE7A51C66B67A739EC5E6B371FE17251F2B50EB3113BA6800D5E6175DC9664CE618098186A630EE974C96204C960F87D0
	DDF2A31048CFC60810CFFE3B1005A404084B3D365B0D7A1837F79049C0C5A81DAE3328612072C49BE1E435FBA09218BEC4E1ECEEB23BC2E8B01EDE4A0E17730AA2551168E0365046C4CA02525D8E49EBF6F5185D9ED7BB700199BD0B96D5381B4DFBE4BAA85385F158C32218E85BE331BB48541E46323047CEAEA86E263F60495CC2FEDE44C2FE8D83FD4BE2D7EE8B6D74DAE2F301D854D9C46A1E5CF88FDAFA6DDD5D8E1A350644055CCC64B5A8FA4C5C6090ADDE515CEE9321668841566A5DD07F50FA08E09BCC
	D84CC0AC59F43E243E86DBC169AE331B2C133C92B858CD39C942A76DB0C9002502399BAC0456841D0A2B272A3B5D56DEA4DA3BC11DBCD59FC4BA9FA23688E6907FB43A4E1CAD87409662A336DC12BB3934F4E146C1E181B4A607F52758A3FA1092D6E873A896DC8E5550CE6FD72E9796EBF01B3B1DD607389397AB75C22EA67BDB31DF46AF50601986727A062A6106E54E54CA328C7DEAFF751E1D5BF7EE2BB0C34BG3F5256AF37BE9769D6208F6DD1BAD4F57FCB434ACB5AD7BF548B3857AAFAA1C22DACECE1D9
	576D30525224A4E78D0CE81C59DC50B53308E68CE80BA333ADE8EBCE5A115032AE27231F0AD656450520D63B255BCB991BDD3487E24CC91B8615E119CD628F54261DA4D4486C4AF5545A7F6491355F4FBCDC2573A8DAFABE9FAE7F4D0B25D1F2FAC45FA0331571346C127FABBD474B6911DABD859FBD7FC37CF9D95F30F41AC1F6FFBA4E0FCA233FBB571DCFB52F9B1E674EEBED5E785F613A087F8EB56F7E6FD0733E7F8EB59F7A6FD0B376BF2FC37C25FF4F7D27FB73797F0ECA1F789F58177F467FCC47F883E3
	18DB3EA4F5BE6616D5F27A75C0F34BF359F57ABF5D1C7FD39F7EE6661A33E532C2ADD7C48853AEEACF47E7297A6723CBB63067270572A7794DCC767BBC57FFC5143781BE88005D497C6617EB857F3F8338374B62F0C8964771EA27157CA66066FEC9D4FE6771262F88FFAF524819AC6F762F085E47D4E57E9FD0CB878845B751F537C2GG8462GGD0CB818294G94G88G88G32CDB1B645B751F537C2GG8462GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586G
	GGG81G81GBAGGG71C2GGGG
**end of data**/
}
}