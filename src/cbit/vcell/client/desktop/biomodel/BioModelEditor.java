package cbit.vcell.client.desktop.biomodel;
import cbit.vcell.biomodel.*;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.sql.*;
import javax.swing.*;
import cbit.vcell.client.desktop.*;
import cbit.vcell.client.*;
import cbit.vcell.client.server.*;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
public class BioModelEditor extends JPanel {
	private cbit.vcell.desktop.BioModelTreePanel ivjBioModelTreePanel1 = null;
	private cbit.vcell.graph.CartoonEditorPanelFixed ivjCartoonEditorPanel1 = null;
	private JLabel ivjJLabel2 = null;
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanel2 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private BioModelWindowManager bioModelWindowManager = null;
	private JMenu ivjApplicationMenu = null;
	private JMenuItem ivjCopyMenuItem = null;
	private JMenuItem ivjDeleteMenuItem = null;
	private JPanel ivjJInternalFrameEnhancedContentPane = null;
	private JSeparator ivjJSeparator1 = null;
	private JMenuItem ivjOpenAppMenuItem = null;
	private JMenuItem ivjRenameMenuItem = null;
	private cbit.gui.JInternalFrameEnhanced ivjjInternalFrameApplication = null;
	private JMenuBar ivjjInternalFrameApplicationJMenuBar = null;
	private cbit.vcell.biomodel.BioModel fieldBioModel = new BioModel(null);
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private JMenuItem ivjJMenuItemNonStochApp = null;
	private JMenuItem ivjJMenuItemStochApp = null;
	private JMenu ivjJMenuNew = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BioModelEditor.this.getCopyMenuItem()) 
				connEtoC3(e);
			if (e.getSource() == BioModelEditor.this.getOpenAppMenuItem()) 
				connEtoC4(e);
			if (e.getSource() == BioModelEditor.this.getDeleteMenuItem()) 
				connEtoC5(e);
			if (e.getSource() == BioModelEditor.this.getRenameMenuItem()) 
				connEtoC6(e);
			if (e.getSource() == BioModelEditor.this.getBioModelTreePanel1()) 
				connEtoC1(e);
			if (e.getSource() == BioModelEditor.this.getJMenuItemNonStochApp()) 
				connEtoC7(e);
			if (e.getSource() == BioModelEditor.this.getJMenuItemStochApp()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditor.this && (evt.getPropertyName().equals("bioModel"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == BioModelEditor.this && (evt.getPropertyName().equals("bioModel"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == BioModelEditor.this.getCartoonEditorPanel1() && (evt.getPropertyName().equals("bioModel"))) 
				connPtoP1SetSource();
			if (evt.getSource() == BioModelEditor.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == BioModelEditor.this.getCartoonEditorPanel1() && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetSource();
		};
	};

/**
 * BioModelEditor constructor comment.
 */
public BioModelEditor() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:44:15 PM)
 */
private void bioModelTreePanel1_ActionPerformed(java.awt.event.ActionEvent e) {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	if (e.getActionCommand().equals("Open")) {
		openApplication();
	} else if (e.getActionCommand().equals("Create Non-stochastic Application")) {
		newApplication(e);
	}  else if (e.getActionCommand().equals("Create Stochastic Application")) {
		newApplication(e);
	} else if (e.getActionCommand().equals("Delete")) {
		deleteApplication();
	} else if (e.getActionCommand().equals("Rename")) {
		renameApplication();
	} else if (e.getActionCommand().equals("Copy")) {
		copyApplication();
	} else if (e.getActionCommand().equals("Create New Application")) {
		newApplication(e);
	} 
}


/**
 * connEtoC1:  (BioModelTreePanel1.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelEditor.bioModelTreePanel1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.bioModelTreePanel1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (NewMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelEditor.newApplication(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newApplication(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (CopyMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelEditor.copyApplication(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyApplication();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (OpenAppMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelEditor.openApplication(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.openApplication();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (DeleteMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelEditor.deleteApplication(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deleteApplication();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (RenameMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelEditor.renameApplication(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.renameApplication();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC7:  (JMenuItemNonStochApp.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelEditor.newApplication()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newApplication(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (BioModelEditor.initialize() --> jInternalFrameApplication.border)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getjInternalFrameApplication().setBorder(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (BioModelEditor.bioModel <--> CartoonEditorPanel1.bioModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setBioModel(getCartoonEditorPanel1().getBioModel());
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
 * connPtoP1SetTarget:  (BioModelEditor.bioModel <--> CartoonEditorPanel1.bioModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			getCartoonEditorPanel1().setBioModel(this.getBioModel());
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
 * connPtoP2SetSource:  (BioModelEditor.documentManager <--> CartoonEditorPanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setDocumentManager(getCartoonEditorPanel1().getDocumentManager());
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
 * connPtoP2SetTarget:  (BioModelEditor.documentManager <--> CartoonEditorPanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			getCartoonEditorPanel1().setDocumentManager(this.getDocumentManager());
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
 * connPtoP3SetTarget:  (BioModelEditor.bioModel <--> BioModelTreePanel1.theBioModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getBioModelTreePanel1().setTheBioModel(this.getBioModel());
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
private void copyApplication() {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	if (selection == null) {
		PopupGenerator.showErrorDialog(this, "There is no application currently selected to be copied!");
		return;
	}	
	try {
		String newApplicationName = null;
		try{
			newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the application copy:");
		}catch(cbit.vcell.client.task.UserCancelException e){
			return;
		}
		if (newApplicationName != null) {
			if (newApplicationName.equals("")) {
				PopupGenerator.showErrorDialog(this, "Blank name not allowed");
			} else {
				if (selection instanceof SimulationContext) {
					SimulationContext newSimulationContext = getBioModel().copySimulationContext((SimulationContext)selection, newApplicationName);
					getBioModelWindowManager().showApplicationFrame(newSimulationContext);
				}
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to Copy Application!\n"+exc.getMessage());
	}
}


/**
 * Comment
 */
private void deleteApplication() {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	if (selection == null) {
		PopupGenerator.showErrorDialog(this, "There is no application currently selected for deletion!");
		return;
	}	
	try {
		if (selection instanceof SimulationContext) {
		//
		// BioModel enforces that there be no orphaned Simulations in BioModel.vetoableChange(...)
		// Check for no Simulations in SimualtionContext that is to be removed
		// otherwise a nonsense error message will be generated by BioModel
		//
		if(((SimulationContext)selection).getSimulations() != null && ((SimulationContext)selection).getSimulations().length != 0){
			StringBuffer errorMessage = new StringBuffer();
			for(int j=0;j<((SimulationContext)selection).getSimulations().length;j+= 1){
				errorMessage.append("-- "+((SimulationContext)selection).getSimulations(j).getName()+"\n");
			}
			if(errorMessage.length() != 0){
				errorMessage.insert(0,"Application "+selection.getName()+" contains simulation(s):\n");
				errorMessage.append("Remove all simulations before deleteing");
				throw new Exception(errorMessage.toString());
			}
		}
				//
				//use this code in vetoableChange if BioModel is to enforce "no delete SimContext that has Sims"
				//
				//if(fieldSimulationContexts != null && fieldSimulationContexts.length != 0){
					//SimulationContext[] oldValue = (SimulationContext[])evt.getOldValue();
					//SimulationContext[] newValue = (SimulationContext[])evt.getNewValue();
					//if(oldValue != null && newValue != null){
						//if((oldValue.length - newValue.length) == 1){
							//for(int i=0;i<oldValue.length;i+= 1){
								//if(!cbit.util.BeanUtils.arrayContains(newValue,oldValue[i])){
									//StringBuffer errorMessage = new StringBuffer();
									//for(int j=0;j<oldValue[i].getSimulations().length;j+= 1){
										//errorMessage.append("-- "+oldValue[i].getSimulations(j).getName()+"\n");
									//}
									//if(errorMessage.length() != 0){
										//errorMessage.insert(0,"Application "+oldValue[i].getName()+" contains simulation(s):\n");
										//errorMessage.append("Remove all simulations before deleteing");
										//throw new PropertyVetoException(errorMessage.toString(),evt);
									//}
								//}
							//}
						//}
					//}
				//}

				
			String confirm = PopupGenerator.showWarningDialog(getBioModelWindowManager(), getBioModelWindowManager().getUserPreferences(), UserMessage.warn_DeleteSelectedApp, ((SimulationContext)selection).getName());
			if (!confirm.equals(UserMessage.OPTION_CANCEL)) {
				getBioModel().removeSimulationContext((SimulationContext)selection);
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to Delete!\n"+exc.getMessage());
	}
}


/**
 * Return the ApplicationMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getApplicationMenu() {
	if (ivjApplicationMenu == null) {
		try {
			ivjApplicationMenu = new javax.swing.JMenu();
			ivjApplicationMenu.setName("ApplicationMenu");
			ivjApplicationMenu.setMnemonic('a');
			ivjApplicationMenu.setText("Applications");
			ivjApplicationMenu.add(getJMenuNew());
			ivjApplicationMenu.add(getCopyMenuItem());
			ivjApplicationMenu.add(getJSeparator1());
			ivjApplicationMenu.add(getOpenAppMenuItem());
			ivjApplicationMenu.add(getDeleteMenuItem());
			ivjApplicationMenu.add(getRenameMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjApplicationMenu;
}

/**
 * Gets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @return The bioModel property value.
 * @see #setBioModel
 */
public cbit.vcell.biomodel.BioModel getBioModel() {
	return fieldBioModel;
}


/**
 * Return the BioModelTreePanel1 property value.
 * @return cbit.vcell.desktop.BioModelTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.desktop.BioModelTreePanel getBioModelTreePanel1() {
	if (ivjBioModelTreePanel1 == null) {
		try {
			ivjBioModelTreePanel1 = new cbit.vcell.desktop.BioModelTreePanel();
			ivjBioModelTreePanel1.setName("BioModelTreePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelTreePanel1;
}

/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 5:40:13 PM)
 * @return cbit.vcell.client.desktop.BioModelWindowManager
 */
public BioModelWindowManager getBioModelWindowManager() {
	return bioModelWindowManager;
}


/**
 * Return the CartoonEditorPanel1 property value.
 * @return cbit.vcell.graph.CartoonEditorPanelFixed
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.graph.CartoonEditorPanelFixed getCartoonEditorPanel1() {
	if (ivjCartoonEditorPanel1 == null) {
		try {
			ivjCartoonEditorPanel1 = new cbit.vcell.graph.CartoonEditorPanelFixed();
			ivjCartoonEditorPanel1.setName("CartoonEditorPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCartoonEditorPanel1;
}


/**
 * Return the CopyMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCopyMenuItem() {
	if (ivjCopyMenuItem == null) {
		try {
			ivjCopyMenuItem = new javax.swing.JMenuItem();
			ivjCopyMenuItem.setName("CopyMenuItem");
			ivjCopyMenuItem.setMnemonic('c');
			ivjCopyMenuItem.setText("Copy");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyMenuItem;
}

/**
 * Return the DeleteMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getDeleteMenuItem() {
	if (ivjDeleteMenuItem == null) {
		try {
			ivjDeleteMenuItem = new javax.swing.JMenuItem();
			ivjDeleteMenuItem.setName("DeleteMenuItem");
			ivjDeleteMenuItem.setMnemonic('d');
			ivjDeleteMenuItem.setText("Delete");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteMenuItem;
}

/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the jInternalFrameApplication property value.
 * @return cbit.gui.JInternalFrameEnhanced
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public cbit.gui.JInternalFrameEnhanced getjInternalFrameApplication() {
	if (ivjjInternalFrameApplication == null) {
		try {
			ivjjInternalFrameApplication = new cbit.gui.JInternalFrameEnhanced();
			ivjjInternalFrameApplication.setName("jInternalFrameApplication");
			ivjjInternalFrameApplication.setVisible(true);
			ivjjInternalFrameApplication.setStripped(true);
			ivjjInternalFrameApplication.setJMenuBar(getjInternalFrameApplicationJMenuBar());
			getjInternalFrameApplication().setContentPane(getJInternalFrameEnhancedContentPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjjInternalFrameApplication;
}

/**
 * Return the jInternalFrameApplicationJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getjInternalFrameApplicationJMenuBar() {
	if (ivjjInternalFrameApplicationJMenuBar == null) {
		try {
			ivjjInternalFrameApplicationJMenuBar = new javax.swing.JMenuBar();
			ivjjInternalFrameApplicationJMenuBar.setName("jInternalFrameApplicationJMenuBar");
			ivjjInternalFrameApplicationJMenuBar.add(getApplicationMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjjInternalFrameApplicationJMenuBar;
}


/**
 * Return the JInternalFrameEnhancedContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJInternalFrameEnhancedContentPane() {
	if (ivjJInternalFrameEnhancedContentPane == null) {
		try {
			ivjJInternalFrameEnhancedContentPane = new javax.swing.JPanel();
			ivjJInternalFrameEnhancedContentPane.setName("JInternalFrameEnhancedContentPane");
			ivjJInternalFrameEnhancedContentPane.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJInternalFrameEnhancedContentPane;
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
			ivjJLabel2.setText("Physiology:");
			ivjJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
 * Return the JMenuItemNonStochApp property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemNonStochApp() {
	if (ivjJMenuItemNonStochApp == null) {
		try {
			ivjJMenuItemNonStochApp = new javax.swing.JMenuItem();
			ivjJMenuItemNonStochApp.setName("JMenuItemNonStochApp");
			ivjJMenuItemNonStochApp.setText("Non-Stochastic Application");
			ivjJMenuItemNonStochApp.setActionCommand("Create Non-stochastic Application");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemNonStochApp;
}

/**
 * Return the NewMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemStochApp() {
	if (ivjJMenuItemStochApp == null) {
		try {
			ivjJMenuItemStochApp = new javax.swing.JMenuItem();
			ivjJMenuItemStochApp.setName("JMenuItemStochApp");
			ivjJMenuItemStochApp.setMnemonic('n');
			ivjJMenuItemStochApp.setText("Stochastic Application");
			ivjJMenuItemStochApp.setActionCommand("Create Stochastic Application");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemStochApp;
}

/**
 * Return the JMenuNew property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuNew() {
	if (ivjJMenuNew == null) {
		try {
			ivjJMenuNew = new javax.swing.JMenu();
			ivjJMenuNew.setName("JMenuNew");
			ivjJMenuNew.setMnemonic('N');
			ivjJMenuNew.setText("New");
			ivjJMenuNew.add(getJMenuItemNonStochApp());
			ivjJMenuNew.add(getJMenuItemStochApp());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuNew;
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
			ivjJPanel1.setPreferredSize(new java.awt.Dimension(50, 385));
			ivjJPanel1.setBorder(new javax.swing.border.EtchedBorder());
			ivjJPanel1.setLayout(new java.awt.BorderLayout());
			ivjJPanel1.setMinimumSize(new java.awt.Dimension(50, 214));
			getJPanel1().add(getBioModelTreePanel1(), "Center");
			getJPanel1().add(getjInternalFrameApplication(), "North");
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setPreferredSize(new java.awt.Dimension(50, 102));
			ivjJPanel2.setLayout(new java.awt.BorderLayout());
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(50, 102));
			getJPanel2().add(getJLabel2(), "North");
			getJPanel2().add(getCartoonEditorPanel1(), "Center");
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
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}


/**
 * Return the OpenAppMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getOpenAppMenuItem() {
	if (ivjOpenAppMenuItem == null) {
		try {
			ivjOpenAppMenuItem = new javax.swing.JMenuItem();
			ivjOpenAppMenuItem.setName("OpenAppMenuItem");
			ivjOpenAppMenuItem.setMnemonic('o');
			ivjOpenAppMenuItem.setText("Open");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpenAppMenuItem;
}

/**
 * Return the RenameMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRenameMenuItem() {
	if (ivjRenameMenuItem == null) {
		try {
			ivjRenameMenuItem = new javax.swing.JMenuItem();
			ivjRenameMenuItem.setName("RenameMenuItem");
			ivjRenameMenuItem.setMnemonic('r');
			ivjRenameMenuItem.setText("Rename");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRenameMenuItem;
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
	getCopyMenuItem().addActionListener(ivjEventHandler);
	getOpenAppMenuItem().addActionListener(ivjEventHandler);
	getDeleteMenuItem().addActionListener(ivjEventHandler);
	getRenameMenuItem().addActionListener(ivjEventHandler);
	getBioModelTreePanel1().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getCartoonEditorPanel1().addPropertyChangeListener(ivjEventHandler);
	getJMenuItemNonStochApp().addActionListener(ivjEventHandler);
	getJMenuItemStochApp().addActionListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BioModelEditor");
		setLayout(new java.awt.GridBagLayout());
		setSize(800, 600);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 0;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 3.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);
		initConnections();
		connEtoM1();
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
		JFrame frame = new javax.swing.JFrame();
		BioModelEditor aBioModelEditor;
		aBioModelEditor = new BioModelEditor();
		frame.setContentPane(aBioModelEditor);
		frame.setSize(aBioModelEditor.getSize());
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
private void newApplication(java.awt.event.ActionEvent event) {
	SimulationContext newSimulationContext = null;
	boolean isStoch = false;
	if (event.getActionCommand().equals("Create Stochastic Application"))
	{
		isStoch = true;
	}
	
	try {
		String newApplicationName = null;
		try{
			newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the new application:");
		}catch(cbit.vcell.client.task.UserCancelException e){
			return;
		}
		if (newApplicationName != null) {
			if (newApplicationName.equals("")) {
				PopupGenerator.showErrorDialog(this, "Blank name not allowed");
			} else {
				newSimulationContext = getBioModel().addNewSimulationContext(newApplicationName, isStoch);
				getBioModelWindowManager().showApplicationFrame(newSimulationContext);
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to create new Application!\n"+exc.getMessage());
	}
}


/**
 * Comment
 */
private void openApplication() {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	if (selection == null) {
		PopupGenerator.showErrorDialog(this, "There is no application currently selected to be opened!");
		return;
	}	
	if (selection instanceof SimulationContext) {
		getBioModelWindowManager().showApplicationFrame((SimulationContext)selection);
	}
}


/**
 * Comment
 */
private void renameApplication() {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	if (selection == null) {
		PopupGenerator.showErrorDialog(this, "There is no application currently selected to be renamed!");
		return;
	}	
	try {
		if (selection instanceof SimulationContext) {
			String oldApplicationName = selection.getName();
			String newApplicationName = null;
			try{
				newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "New name for the application:");
			}catch(cbit.vcell.client.task.UserCancelException e){
				return;
			}
			if (newApplicationName != null) {
				if (newApplicationName.equals("")) {
					PopupGenerator.showErrorDialog(this, "Blank name not allowed");
					return;
				} else if (newApplicationName.equals(oldApplicationName)) {
					PopupGenerator.showErrorDialog(this, "New name provided is the same with the existing name");
					return;
				} else {
					((SimulationContext)selection).setName(newApplicationName);
				}
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to change name!\n"+exc.getMessage());
	}	
}


/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setBioModel(cbit.vcell.biomodel.BioModel bioModel) {
	BioModel oldValue = fieldBioModel;
	fieldBioModel = bioModel;
	firePropertyChange("bioModel", oldValue, bioModel);
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 5:40:13 PM)
 * @param newBioModelWindowManager cbit.vcell.client.desktop.BioModelWindowManager
 */
public void setBioModelWindowManager(BioModelWindowManager newBioModelWindowManager) {
	bioModelWindowManager = newBioModelWindowManager;
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G42DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BD4DC5535E8C844088DD19AC902CF9251E0C59FBE5316E553555466352926ED7A0C9AA9BE290DB575C5CDB5DD4577D86DE5E6B0E008A69AB5D5D434C9C38CE2BE28112205B0A4444608B0A103FC6F4CDC18011939434C9D9882C95EBE1FFD67B25C81C2D7E42D4D3DE71F5FBEFB6F334FBE7B1CBB420E37D28A728DBB84A13FC060FF9F658BC2E13BA024BCF83F85F139DFE695891A3F3781368BBF
	8E1960D95814D5D4F6CBD651B6E15E79C2C817C0BA61662C22D7617DA5E1D5173395DE2274D383F965B56B8C9377F36917584FE61A4EBDD28C4F6781CA81C71E6EA6527F363A1D9CEF66F804FC682131191459D7D742F199D0E6A5C0B160CB1BD9794370E4844C2CBA4C4B354FCD93B65D65B7ECA56DE85AC4C862F51771E729703F798E8B694BD2D74C6BEC023CF24011419B059DD59F41333A313F77D33B4D25D48C5AA5373B426EF6C9DE2542A1857A9559D7E1F3499E59A1392B2A0E38644F49DB3D432548FE
	A107361DDCD7C85B9204B4C83BCF91DCDB0074378B5E2B81AC27907F0EC27099703E92A067940E7113BD55FC8C56116F8A235F7EE04B8BD14618FB0A0F2DA5BC46E58B338DFF2172383309703D9D72E2G9281B281568264823CCE756027DF3E8B4FDA1BD2A7FBBC32375667BB62F6786938603D2A8AF2B82EC9324BFE07A0B0D9FFD5D72EC34FC4406AB9AF389E63C95AA114FBFA532DC2766D738C1BF5F8121DB6E258F61A71226CF418A72C6FC5D17BEEB8BDD6AF4776FDDA5076DDB93A46D82053F723EB0D31
	1D376353741DF21A487A97D4879260BD1F6B8743BFC0716BB4F82623A2451374A64833DAD1B72C4FA3ADA7CC0B05ED872E5165C3521D7106E5D6C6439AEBA4AD77D13D93DAD91F2F2A34BCC67107B5F8C6CB994513348F724A3F4AAAE27AF537054C597510B635E59515GD482D8GFCG31ED28E3AFB63DBFC59DEB947D4A11204B6D107C822BFBF753AB7014C20ADFBC6E8DA82257AE3508FE17E8F3CB810117D953C4F8E09BD36F812A7BF668631864157C2262123D503A681194499FE0E59EEE5ACE4A78A51A
	EBF1F9A468406393305D471ADE0227DB8CA827FC8ED11142A5D87ED3CDC4A603E28B34888D40BB63CB6EA9422FD4987FB200D5AA9FBC4D243DFFCAFEAFB19FA7E439BF6833101188A56D044F1954F664C0795CB664712696026B067CD0BB36135A7C22269D334BCE48977D43B8476E8D903B9C5F0EBA730495F5463C7596217C5EEF6A6A4C8A505F34F626AB4B5BA3674EC334CDC297CB77862C97B80F2782713CDF56C74CC56943481F0C3FB6AB1155FA68575411D5342B83F97CD0F3A95359A3E2C0C2AD7D428F
	8AA6B9429CE7F4E6AB442E90DEEF81A06B518B348DC1A0F662E5005DGFBG7682FC220EEFA32DD78DFDBAG62811682E48318BAE11E81D8GC681D281D6F5A26FAB148C4DF88A04302D52767FBC276165C9782125EDAA7C7BE41AFCE734F70C21FD2BC6A6853CCC9147ED510CA39AEC1F629891DE1DA66DCCA76E3590FA24C1F0874C12CF72BA60A5407332AD04EEEE27FAE4354E43946F833BD68706A4206257DB886D222278DD3620A2B5CB0A6272760673B7523E82C11BAFEC92312FBFDA882F64A130EBEA79
	BC8B3189F6596B1528F22AD5F87E1F28CD125C1287082E1303DE0561CDADE4DE77CB435CA69636BCAB901B668E927B4A7C228C11484267F7F9C0650909964CDDF4CDD37DBA821BC0BF09FF3793724A3B50768C7D95ED4F2EB3B7894B3BFC863D35B36906B90678AEB677D33A4236075174CC0BBE6DFF26361163830C565706C85D88570C4F75052D241D65507E9A008DDDB857C7CE13FC3427FC125749EEB7E3254AE327F59B1109B49014E099A2067B1FAA5FE46B0B91F9AD1C07ACFF48CAF86856587C64EE6A77
	86516FB5C13A84203A1B71F1341B710115FBD2AD379E520298AED7AAEA4BDDBF0865EAGEF55148B0DA9F7FA884BA581BEC3D3EEB5AF673611F2DBC2D8EE2348728B81DF8AE08630823076EA0731DCA660D683EC32317215F6565ECABBA9D7F0864B455AD9FE321D154F00671BEA39F6814B6DF030F265702C81F0B834654A7331DC0817CB606552F9B9C61FE040F2AB811F83A0C82CFC0D24654B8A355CA86093257165181EBD3D1768BE5DAB740903E228A2B08473372A6A33C69134086D85BEB6977272CBA574
	7925E3C487B7C05A5483726E41F9724B2D6873A77627895B4E5EE30C362F296CE1F4D8FB4273E4DDE35048F47C112FC81FC9101756037A7D14D56BF7548682325DC5DD9E2E1B0BDA35730861EEECFD8E1EBDAEFFC0B98E06AE04FA1C5ECA68A5BE91C7F39F5192AC66E3DD867DEE82086FA578D30E9D9A5A4B81E781888198E9FB5F8B9EA4F610CE348A76E8147CBD325FA3B94A88E7AB44A125025A5C0ADA1ADDCF5E77229C827B185D738D43A26DF65969384A7636206F3E78259A5E772ADB978B5567BB8DFAFB
	2938971C06B53D0C1FF93DE13EE6989EB42CGE0E35CB908E3ECF0421E4A09E37C5229B746CCA76EAB7F270150A8430A91C9A3EBF7155A6EAA28E3F2E13BEDAE3DF62BDD586EDB45A49F4C9C7834E3DBE66D468CE13B0DD0A7CEEDD76A53EBF7395AEE52EE126F173C60AD6B35DB9942F657C01D62BE1465C20AF7F9DD0ACBF43B4EC8E5FB59BA71E01BF61DB0C37990C0B2C006DAF7370D2CBBB61998DF016F259F8E61DEFAE59F4E13DB5E544A33D01C29AB4F785F759A04FE3E8F6C8F4BF32771C1C339916559
	B70C6370C119B4GC66F6ED6AD3DB97D0C5E1DEE30C1EE247717437F1A5E4EF923160C172FD3963FA4D143F1202BB6222D93519B7BBBD1A12EC3CB4F63FE518CB40E2AF43EAE94EA7AE1B8B3752D9427044BA41FABCF55EAF91B39B9DE17374EA5C90625EE4653ECF71837495B9FB4E4ECC75E4E94101EF28F58EC8F5233E18CBD0C5F4F79357CCE0632EB8172G36F9315E4B795AFA2C0FD37958C799146BD64BAE4BA7F4BBE4FB1098344FC52F588BCA45729E554DE36D6DB520BEA4C1DB2B3DB8DFB2454879D2
	A803AE4858DF0261DF53052E836F4153AB8DE9A75B94657E38CAF38D50931211665F5A74E84E10F18F7129B9B29632C28E47C0564A6363A1BCDE4156AF1945888AD47CBA0A97FCE1BC232F7F9852378372C2BE5C576E6CA0B24F06F4AE4036012C22B20086G67G2EDB15DF9D92A6099B9C7742F65EAB3A1B47B84A38FF7F0A4887FCDB1EFE62ABC28775ED85B50E950C64C36C40583127C424F99CEBDC3D0AC8FEC654E332DED9C0E851719BBE173C41630A64613E75AB1D5AB9118DED89FE581F829C86E076B1
	5E017EC5239F6D635923DABFE2598BE78DDBF466B0DD737C0C3EB43FA6BEDA78876EC75C59C9E3BB10E78A001F82F098209DA084109CC099055ABE19CCC6D4E7ADF29D609A407F2D0B5C2328BCFDF3F0FA32A89FD7AFD2964B82E36BB17E45B911FFEB82B8AF7EEB1AB464CCCAC33122C743D5AE24E11702B44C9A1A9E8D09636AC552502ECBC3F29F522028B4D40526C7C34338FA11B424ABFA73620B5E5D02B6765E9BF441244069FD14846E6A3DCE927EF3C0B9B6C7CA9AA2771FA4BE35825ADF2B609CF9391D
	0C0D4C0D0D8A7A60F774931BE489C29AA0B608E3FEF49C6DFA109C8C534F664A475DE42EE4GFE9540BAGE19066AAC08D00E3D00D35377DFD0AF305997B71B30551699B1E1EEC4E0D2B77E6E4BB03FA32B9FC78DDE17C3988BF90891F0328CE8C6AD5C7AB7AEF4903487BF75B886F57C0FA8300E988F9FF477949F82FBF26151356AB999ADB0F49ACC3A4B22B043CC6GA7C0ACC09A408A0075C3A833575B0FCCA633DA8C62005C70D54748B1FA2F9026B74E79135693C2FA32737AABF4E4576B97FD4E2A2ABAD8
	B2E5F0AAE814040A6E28ABC4A6981BDF87E43D7935937AA98F617CBA62C7991BC3A863B4BF1171A824938152C3A863367C690D5DB5E93D95A13DFA6F9B26B7CFBE99D7AFF21EE4C774477875F8D7641ACD76D51B20EC61B0726BC991EDED69B0726569E95219EF1C0CCE6B301E3DFF43DE485B70C1BEBBFBB8C5E395E910DE26628E9E256791104ED5E9FD6898BDCBBE13D5347F8C629E23B832C7A2FEBB438D9C1BA8F6CF6A637B7EB3E3E91C8A30BEE6CF5807D173DEF0016D93E0737F6D0E68B15F328E7D18EF
	C507FE4C3732E3BB43C39B5A13AE4C2F6E18B8A6EC6E500B893FC1477A53D7DE20ED8782D200AEA2E6D191F95E238A090F0F0B8D1F04B5157369A5E1EE30081729D7643AC3825A0F22D7C94CBC0A3FAAA41FA1FA12572E1E6FC33A1D4BB3768C0B93B21DBB5909FA3F6E0C3A96F491BBD1B212D5D489F0E284F567F4EF24DEBBA0AFB492DDCF18CD7F40CE76BC71D0AE8DE0B940DA005C913465D99D136EA76AC3936CA7D6F5C65705FBBA75F56101CE92478B48C13FDD0A14F78E7571D9DEF841A01BGA29AD666
	F7343EA27AFB25F17A321156E7F9516ABF5399CD27185CCF8DEA65DE294E2DAB1D11B258B98AF6FFB43A8CD8FBC5C35A76BED05B3B469559DE8D34E519343D6F0FE96FC3353DC5FD116DB9212D51C95B9B88E95A7B7C0000B65152C074136C1592C6D1BF3D74ECF0A524F3GF2C7D1BFDFEB1F486EECBB9B26E1973C9FBE9B1D2693BAF9ECBFDD61A6FDF71F0D3CAFF01862FDE7A36F8BAC6F47D8C88C642DBB0BB67CCFB40656F08E6665B91C87F55D51757833EEFDBDEE6C0E2E47273BA757636E6E68FA5C5BBD
	399E7B3AA75663A543DABDB9206A092DF5A25F41FDAE9A7F7F0D7A29B167981F73D57C920A0F5760997F7737611D22A4484BBB0731215F1FC73A9A95397110BA66BB4468B2782128AF031F0851E570F3F1F2995CAFC61741FF0B134B60D1FA8EE8F3B9DCFEFEB24646F827209DE3F8CE39E45CFBF51F47B915A793BEA5C1BA83E065F91CD3477C9349A967FCB4B91550FBB21B4EC74AE9B7458BB111F23A550FF2AA06BCDF8C4A692AFC6D988E2AFA55E3506A55C76A58F6F72B3103989CDB3EC85AAE3E8476FEG
	55176058B609FAE333DCB2F6CCCECD1A757D2ED15B77C1D5275F31C736B78AF593A25ACB9D571E5BCCE26CEEF1D88EAA82DF3BE718F16D4E04722B81F2810AAFE53EBE7381EE9BA2F26F01BDB8CC39E61FE497B2AFA5F50F9F453A3BA0FD98408A9082C83A94797A078304E6F6AAF60053F17663B06EA0174B2E8E140BB0835A1A01FDDF7A71EBC2D47F6308888A4CF8AF527ABE28978710B28363E1213FE1BCAC7D464502E9D87FDCAD47F25610B9034734E70463E12B97E69B984F1BBB472D0731309E46C6376D
	CC8F97D39F27A6166961D695FF9B45BBB4F8EEC7A81E2463A0EFD3AC5AF1EFAF3D87B4B32BC81909387F2038B8C8A74DC45B7E09BD3ADDB15B756D4AE7F6FDFF3551BE313FEA35C733490C3ED4A7521C2A523C5E09B4E72A38DBDDD8EE050AFB4A0565562A384CBEAC37CE456575E1395C19B85FDA9BD0D626D930DF85A81F9516D38D3CDBE6C56C2F28CC9E266B3DF3D6E4EC7BF10A9F1D95995BD62C985BCE003CCD331026EA9AF332DE86F661B2444D243898C8A72A381FD3DC5AE5AC364BF0F2BF6E3916DD06
	6329F413BB9A6B557427B43DB38E53BF6E3F50FBD2242D2916657B3E8B6E43F881E5797D5BCF35717993F1B4FEB7CC46DA9D077C99EEC57EA42863CFED93D4BAEF513CFF58F6B16E0F0D654B97DFC39FDE9975F9D99C135B6A38307C5641FBEE1C1EBE7BFCF4BD3ABCD21FC7A83E7472C8FDB678D01FABA1AF69F264F9279FF91EF7B9727C1DC0184F7BB46FCB7D971F9FBF7B9A7AF837BB525E174D06B5FD76E476BEC8FD00465911BC0F21F8476CC81EEF961167A1484B1E0DBC5FE2A3BC2F39A22B28718A4439
	A92E9D52A19577319DEDE22C0ABBEBD757859577309D6B2682303EEFB6631A1B813875D7601AFB00463355B5179CC2D6D59D733B9CC7445E9354FBE0BE5053B4E6C46A1746238F74F6BDE9538AE99FC0DCBC6E7F6523D8B685F0EB63896E5A235AB3CAE2AFEEB3317382167F0BB179E42DA1F794437979473479E48D2BFDD61BFFE8CCFE0126BE738D6E5E0D3EC15E5D99027B5B31D13FA5A83D12495D4C1F896B6667978F070CDCD7BA26F7977E708557E33AF4AF75F5C62F0C5C9BBEC47171D7C66E8DFBBBF0EF
	188CF942B7708C76F50978EF8EC847GA481E4G2C8658708D74A71E306F97A60919D8E45F89F0B85C9A970169DCDFA76A26B0877570F60A5B8169DDF3501624513869FEC81B815A8194008400F4354CF7E999F206CD6E482D1C436E40D9795DB5EE6F3B3AF5F8C65A4377847EEE1E23BF0F776B6053B5B8C52567CB5D3EB6EB5E37E874CE2FAF3D75F12B663DC8C03F7D924AB30FD78281386C39F3889F2F213E5FCFG564FC1DFBB51CE56CA859C3C6A84723D4EF3EE22652C7C4F285F7590C08C64317C77BC61
	7C7B293F75D342A3B53F519B4E7FB575339EA13A2666479197127BFABE9ABF49G7C2A8414757354DE11FB02B9AA6ECAEA07CCF341AF1B0B38E2895717EA95676EA038F8C8274EC51E2F3792DCA624572A3827A9EEB53F1348F0E72CD3D99FC89B5172B2A748E3FDDCBD25BEA60ABBCF649FB1F9B637133E86DD819739AF4A6450570E67AD42D5A877E40A8B009B6E73C98ED66EF6355CB6355CA39407BB274A2BC839D97D259350D1FA9563832B9F3F0FFC7725FFA8DBA78316EC933C44F7E367A132BB727C0848
	E8945ACCD0E95AC77DBBEF506D665FBEC95A6F3B760F1CB010F2FAB6B90D5316491FF9AFAF48E7E3CAE81DE8CC6BF9F96E174B2837F9D7216E659AD1C737DD0DB8FF816AE8250ADBB108BA9AFBB56A5E200F5E3F04F43A0A3B0462D6DD4D6E25B2DC21EFAA7AC35A081617B4C19E6B636DA975B1DD9DE53C8E7AA76275062B353C2E25B113BC40EDCBC49E4E6EC6BFE7D7A272E6041E116D07F4230A1BC5F1645EAE39134BF0DBC56D78C8D9BD9A76C741CF862C0F3F0D6963C2EC79D4004D01C55DE36DF6530020
	664D3011B5C1647781BC9E51CB6EBF30F2C1329567F641C66D6A03G3EC4344FC954AFE76FC2D0D047F45B383BDFD363458F26D9AF77026BB1FF66E96ACB46A4C6467672A8BEBEB1B2363727DE0D41C2DE1E2ABB4A5169FDF378434D689FAE3D63DBC266ACF3543BD45D5FE4BA96431FE53F191FCF1E4C4FAA99A4F6B08372D681EC84A81C877A8FD0B98F7D2C8F9D04C6F28265F0119B85225DC9EFE323135562120642E89204E3E37BD587511D06F9B856C7E81A1CBB333427C34F8610725351D7FD794E526DE3
	3AF3A29298FFF6C95163DA7BA47D385681C9BF2E7511B471B96BBF24685F5E9C15345F5E303917C47D339F91BD1C076B5ADD1DE36774DDC30200F37ABBE6F59DBEA98DB15C67749B17BA59B70C9781997E60B922F31FB5CBC00B2848FE0C219A06880DDF30BB656A5DC1EEB33A885F4C74DE381A45735E9FA5F9CD74EEF7B80F5D995DBD669E3BB20F5DDF885B29DBFB49F30FFD8F1F9FA9C35A357A70F3176AFE5BC3A0196BDB463C70DA4D5A78C51B360DED8B5C067CA8ED2C67F5B74D8B47E799DD6B1C14AEF9
	8F27738997CDBBB01D5BC753FECCDF56C0EC2A4FAF75C8FE3F64E8F61D11D4D926D0BF366C9A181BGACC6DEEDC3BF269BF0A100F400655730FB612C4DD47B78587A776CD8EFAD145D8830B3A92B28BC09558BA511B2FF154C9368BE7ABEC68EE628E385F819275DC9E19B48E55FC1744563723ABCC18F9D999B57C26ACFAF07722B1210662CA3643B04F157378EE0DB26B344FE750D7177B4D71455BEAFE9273A6E8FCADC77A3525777933DB8A97B95270036F5C8C25BDAF27375C249DBB5066DD1641E1B44643D
	EBFED86E6449628ACB5CF88EE31D0FB108F3DE7A2DB52453G164F477DEE8375B5574EC71BFB377C8EBCDB44BE3E2BE57720284585A4392F5A8CA6425B0B63FC290D4838CEA27CC0FD39157A0F9B204D5C7928AFEF1B54F31185CC6E5570ECDC406E47B039DFDE00F2F7AE98EF0B35B2A5B08AE5929604651E3AA0ACF7C6476354974DG7C3285A85F9F3EC34FD9CE08B6F24E427D6D574937C063AFFE9D60B660F61F56862C67B497AFD44F497C040775E2C0E1E9EB77783D29CCF15792D9ADC4DF6456B3022AAF
	73BFA4FD28B71ECF4A5EE6C536BB0171AC3F533BDD1B0F19EA7DE1BB1D4F5EBDFC7DF9261E344D5E8DC7436F6F55DF7CB861510B5A87535BF3A304DF7B0117A7GFCG31GC98BD10F0F84099E331B0811FEC33D23171C8713AF6795DC5FF68FC6DFCB4B87A77F5E7E03C172ED18CDD69459C35AC77C5F8789CF9459C7118CD7C36F1778DD3DCE059545F5146A2BDB6A21E83E3E506FFDB596065EB1E2F97F5EC9E5AE6E61E785F315B1B14A7039203AD272EFBEC7C9BFCB01D72BD5FE3D905AB7413CEF744BBE49
	2F8CB3AC7D0A134BE15ED9C1257FD4E8F734B6006B1F81C9DF587AA43BE291FB8574410B4F076700517616C03F79A8D53F73B03BC0F26407844ABE58DFDEFA687087FB39DF6B30E17CEEA30C21AC99633485253BF5F8D041BE222A2A2AD76CCE49F104FFD2456DE53A8D63305D49E8AF33E87BC9104EGD8190C71615F58511F4E815C4EEB315F5FD60C7906014507D9BFACBC4C7B5A67403E9A2F453EDA9D244DB8C827GE4823CC579A1506F02585F46FF6861988F175294DBC3FEDD2CDDC34C771B8DFF957457
	1055577235635F42EB482E7CD2A3EB73CDC3D8AEDFDAA7114B418315FB39BEBCEFAC0527955AD4G12GB2GD6GE483EC84A83C8EF5EEC128EC821DE34C237354AF3A3C8A33AD696E48FD4B2E6B42EB45616B462F9BECFF7494DD7FCEDC9739EF7ABF0A37DE9739EF0A75221C9D1037D2257944B0D96F82FC8DE43C7A0EA94C2B470D13706A407E03284347CD58C7F98A68D78A1E8B4CB5210EA427200E7C0016DD83698DG26C5D8760E82DC6BCB966969CDE1CDF43DF9038C050F21E6F3F88C8F49D399837F66
	37A74AB73FF405E5B4BE66433B3516C538E64E6A26366B649EEEBBE60DC4373FD70ECCEEFF670D506FF541EEDA06FD7CEE4E4291B2C03927A7A029F6BEE5E41B0ABB9EE81483AEE2D4F99E5D8728FBA0E666F83DA523E43DD13F63242618F9C2CCAF7BEC136D276397695DD78ECDDAAF727EB20FD7CCDACFFF9E1C2476ABEDD164BCB8CD71CB97C54E039C9B6A68F2485B359877503F9BF75FE6EA3E419F27D9EF3FE3FA637D99332D0BA347FB9F45A7AE0E9CEF1783471B82F9266B51CF2EA8A43E29935231GE9
	G4B8156GECG90D2330AF6G148154GD8815CGB1G09G29G19G2BD2510EBCC34F5D2259BEFE3098B6FE826A61DF0C5167C929F172F93257286F273CEF9C6F27D49A757D14AA2356CF6171815E2EDF3DC44D707574EE915B83621725987B7C915D2764GCFB625E26C33480BB1651DB7283F216695D4BD4817436F333D975F173D7EEB68638B537466C447B46B4D3B60FAEC4E9CAAA0F3237A064839D4CD718DB7C44E257FD457B78B64E52A727CC101367F129B330A765FB8F16C0D55FB5227C737195735
	7227DB53963B6B3F7CA52203AE2F5B65156A582FDF29EDDA6CE1FD6A503C7F56F671657E672F210FFB9C61B1E5EB5EA547456F7B5CC56D036F738A235B436305135B4326C2FDFBE8AD9CEF8FBB8A756D213DF03CBDF496927F28976C38D5AD67D1F12DAA4ECF5BA43853AE870D8571EF08A9DD845FA0118E91FF0E52B5C4F1D60E1B214135F2DCDCA171FDE048CE72DBBF5C0FAAA40EEA0F4B6DE66939340C4B8B2ED0580F22B43AFC22066E05AA0E504D63E46E71313D5F2BE71BB1B0871378BCDC85CFF2AFAF77
	C6D226540BE556694CF926B79F9704757408667DEE53455759F597358F36465DC7FD761AA54CDEB25C2FA84EBA86776CG41A9000BDD026BE37F00FA57CF4525D25CB2C8E7AF41FD6ADFBCE8FBB7822EAC8D77293D6F043FA916BD95AE1B27A2B8682990EDCA0584AEC8D55549205B4DF72CF80F380536558DED04G9252F06FF1540BFB0F34B43CDB5C572F5DFF12BDE5347BC3B9E9CC7E261B4646B0991D625FB43F1BC22786431704360671B8B538CE3EB1B076FC619419C27CCC765201528964E23EC99B537F
	5EG59AF4144F11D113D0A682EF53BFA3DA40244630A778E607E4CFA932EE9EFF92637963FF74175581AFB9715C95CCD11EB71BD941FF8D364DA5C2E6AC38A64193E05F47F532B6D5F897851EFCD3C96B3FF782196B20F9320ECBA40EA0075GF9GBBEEC69F372208FC5B914D47ED12E5FAB5025C12G97D73B5567F238EEC09BA3673F15B2C06EDBE8C3BB4D64BF06738295F575A7AD75CDAAFFD7537BBC6C7D814D3B4DF371ED4948C56D034DFD1B97ED497579086B7522AD591E2F7E36DECB74F57249164957
	495C967DF572CFAD6357C963E97DF5326834F61DE43F397C4E5095C0E37A63C201A21848D19FBC48891EA2BC4B8E6B60051D4CA942EBA7E56FFBB472AD8694173DD49395D504376B48EF16CA2524CCA0CA19D722D5FE03D70E16DF2CC985C0BDCFCAC3813F70627870C86DB79E38BF3D4B3C43A0980E8B4FBA29FDBAAE948C9183967241843C84C6FD906A3E2056BDCE575F1C41C3D5465FDCEFB488A6284878FCDCB091E6C1FDBFEB4788FCA62D88A6AD848604F382114EFD34EDCC7528296228B5E899C5585268
	9C866A5DF26FF0950FA36EBD2FBD034C1DE90FFA0E693E051F735F9236634CC6FCFF182ECB00DFFB8B5A042D436FC5338935B62559A33A5DE0F17BEB3D8E92AFBEB22CC861B368BB07C97B81AC43B3796FC850EF5D7AB4F57E9FD0CB87885044BF5C609DGGB0DCGGD0CB818294G94G88G88G42DAB1B65044BF5C609DGGB0DCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9A9EGGGG
**end of data**/
}
}