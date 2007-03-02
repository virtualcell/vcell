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
		String message = getBioModel().isValidForStochApp();
		if(message.compareTo("ok") != 0)
		{
			PopupGenerator.showErrorDialog(message);
			return;
		}
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
	D0CB838494G88G88GCD01B8B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D4571524A52DED5D5624EEBF5A1804AF21A9E909EB2BCDCD565DA5295B5A46FCF537ECC333D4ECC3D35B5814CDB41206A40FE1D0E0067F9F09221288820A481F8A0806DF85030A409A3275CB9860498C8EB313190700226EB9773E73664D708610FE466FBB3EF74F3D775CF34FB9775CF34F3B03103DB7B0A1CE17AD88F189027C6FD81CA0A4DE92040F07532389B771BF6B8D026A5F3BG99C2
	E1079E1E8B20AE38B35210AE0CD4A4C2F9891453BF08B46C0077AC61C00579A3F871B1CEA354FDA1E2574D1947392823F13633F2CA06911E29G01004367A69CD3A84A4813712BE5BCC11CCE90563D02ED9AB372E5DC85145B819682843E425B2F04E7929B796E7ADA395D25FB02054DBF9F5525A09D95CD023CD7F95F8679D9A0EC090BFA954732B0BE310F846590G1CFC3C10DDFF8C5BF7DD99BEB9B8E0114A2F8E0AD6EB792055A25A2472A151F5C532BB4A87AC76D17B10E82D2FEF36584F62DB5B10C532BB
	057E4DC87B7B99090C16A014C0F99343FD3E85478B0277D5GE18A7ECCAB62AB3640FC817A37509CCF94B748F3683B7EA321B06D4124B49FF31C584267963845BD477465415B5EE17A78C6BC4ABD826A0457569B3281EA817AGA6GF6B1BB38DFF7G1ECD83D20BFDF454EEEBF2B81A2DC3CEB6AFF82F2F079A9957AD8E5A1DC30240F5ED4FA85360E7A6607DEE5CF6BFAE1360EA52FBC877D2A1E279BD3A6D9AB209883E2ADB749A17C570EBEE1970319F77B9761A57BC6D52F36C8F8575589B04A53A840D31E3
	7FFC1FAEC226932D9A3BE0AB6AFAAB3301F4F80F136D036363188D8C29705CC63F445AE3D9023A702DE49BFD2944CB277EA1A13D72DECDB984BC7DC5DDC38C6721B7461B17375818FEB1FC4C9D8AAF19ED08DF2942F3DE3AD8FBACC7C3DD486B6B8D5C3E9ECC40B5EB7E6BFA03BF40920095GE1G51G7AB74846DE549D1E230DF5191CD2731845BAA4BA855E774178DCF80A93125354E1F3C9A65B20F851643418862C22CB105B8447238C86BC7ABD46ECFF9046B8AD5AC427C93258EDC05DB4AACA2253455BBC
	9E1F01ED1CA22B6D350C0AB04028C3A03A7F9A1F85CF2B49A57D2FE348A4096E963C7EA77128132B260BC0918840BB174B5AEDA82FC218FF83C027A207A6BD527BC0F45A50FDF45A6DD7469C3DB893E1499BA867E7196F68077709B7C846EB36A3EE8314E394BA937ACC951DD64BA032EFF2CE529AFB33857D32B116EC6665BE321956144704327FDA24E9B30E176F531544F2DBE90C75DEBB5BDB11A672454BDFEB693B4DF5BC97B04A6372B1026F48987172134B77E8926A4A8C637A838445120C176B8B384DB6
	1BDCA2D969F9A79818B86416B867D35F06FE85E50D1BA46EC7E90C06A0201F4881A882A886A885B8214CAF0F75DB85E3C681985FDCEF28813884608F90829086908BD078965851DBA47B0B2D4BD473C9905C3ECABDFE2A4CC34EAC72D073B697791D18275CB96FF7FB701E2252C9025CA6C94699D473708599F31CA3410EF9724EED62518464C73C8A61C02B6890EDC37062126B16A5A05F321F3AECD77ABC4E708E70EBAD60C8DC8AFED58272EE12A427E5E0CC92FBC4C93258065D752B59D82E3181075BA552D8
	4FA4202C6C63605714766B9250A78C5AEDB611992752C52E7FCF6613C42BB88ACC375847EC924707EF43F5FDC51C14FDE24436ED827AB46B987AD79E97ADB321AE9CCE4BA818BC3AE8E175DB682F2214388EC1829C46F781708CF90BFC4F78BE72BD05579E95C2BF392239F786FC7B4BBA635B324FF95B6DFBB8CF4FEE57667D174CB74AF89767F567B876758A4D6435EECFC6BA0DC03F97407AB62D757309D8CF7ED4DE64ADF62B150BD211F1C3D2BA6AC47CF4CC04ED889D77870A5CDA12B23D6AAE4AB264759F
	A4218C2DAA1F1FAFE0EC5E53C6F12FBF703384E0555BDC0E31329CF83B9D27281D994AFEG54AE905E5D6D1ED15A05C2B9DC55AE46235D2D5354AEAFAE52D09167EE5795475BED0A43F6556D544E81E52CDFG4FC0FC8788876074AC9D542ED697E9688210F43C7D8A9D27579E0F6D8E1D21F67AF8DE1F9F4F5BD740F30F524E3F0B5AAD165B0540B394A04A23DD6F85EA97A337CB5773F6E5FA5E0E73D752CD6D5A815F8F6027676DC375EA39ECD25A4582BEBBE1FABBEEE72F16226D33334208692AE9225CB58E
	6B373E7ECC17892C081F854EE5A64A6DEB008E0F792F65208DDA216C8F3084002F137FC82118FF7115E0E1D55CFDBE4FB5AB92B89F617293570975554B715C461FB06018F95BA28DA5GF21C12240EBB1ADCAE7B200505BC32ED0EA52B579147DDCBCE0667E50B53A5F500231BA0BB76CBC6FEB1A61251F20CB845E2C71CEB830CAB81983723BC576B33D53C07GBE8CA086A01E51FB212D8A7DA0DBE8657C51A5BAAF5B1D2362D089CA365CB4AE15B31FDB5E442A5B703D147460BA487D1EE392B6E92BE5104D2B
	2474A2456E8FE5B54AE32FDD7A1050F06BA31D56D9EAC15A202EF7BB176754F637DC7D2D8FA486G70B9B61E22B92E01B6C2A24D7179C42DB956C2BD677125F664518EBB06B70F1C6EE605EEA77471D768C6A5E951DD25505DEB44FAF0F39053FAD266F403CF935DF550A7B109683E1C2CC537B109688694E13DD334C1342CC577D2BB516D05BE8B13C817BFE6F80B4DA2D9CCD64BB53124146F931727547B44EAE89F83101F8CEBBE197A768F633EB3E0C767AB48E769648EBACB37A753BAF9EC0FDA1F09A6BFCD
	FDAE7C63004EAF196BB1B8592DCF47C3C5097EGFC9E77F650BC2221CD098156B963F769241A5FFE8367B7G1EA1866277750E3F4FEE3F363B8967AB6FD33DCED1E40E237213A6AFDA1D3E099DC79312EDA862BC1D97D7830F318A1FADDD092AF1B84E5A05FB11E496E91B14574AABCDEA592E5C7EC5CD591A3F73F5DD0D114B741251AD5B95ABFE238B6BDB154479F92C0B788901B6D1C662A77B021A9FAE6F2AE6353C73D3A28DFDGD3G0BD2285F890FFEFC0C9B97E80CE0E837C1E99BF581799E328F0E21C3
	BBEB3219064128F8DDDA37D69D27F7211B6CA1AF957624D4DAAFCDBA6F7532G6AC2D2E93C155DFF1FADFCD2F9881EB6F1DC3D586628771C9E62B9947809D1F83647EB71DC1196A91FA1FAB23DF3A12DE911CA8E24CF75CE40F384AF737D8B6A6364F1B97EF50677D361B9FFFEB9445FE2280B81607B54269454792574C8438440A200E000B5G9B53E95FCAB4D48B33648DBAECF01C37192CBD9E01B21D5F19FEA036154BCFB37F46E2DB014EA31FEB731603BE43F36E85DEE55EEF5134FE655E72746A47F53DA6
	81F951089B4E0A36318EC99C15E36B87EF2A5744A520658790843092007B471C840AAF56E510FF3CF14AA30EC81B5099B456B0C2E2A6672FA4533D0643FE20648E53D8EE876A7C8116G2C8408G0881484FA29DF59ACF4C26A3E6333D76964035C37C5B62FDC6D1E4FA6A547CF451BB2D1F37AE9A32BC7BF179E5A4117CFA33E8DD7CF51EBCA41E1E0D07051ABC6CCAA69E02949E16CE23B5B79ED64D4AC304A68FC58662E1334243C07B7CF8F0CC6B674DC3D936563AB8BFDCA428F36F43E396D894343CCFE162
	2E4DE646745F903463EB24B2577B7C097929D6205F17CDEB6401A91C9BBB03E7D38C5EE2C41F9406E5GFD8E4DB92DE3AE39697C9CB77FFC2D2430EF9795006F849882708388828885084A2135F23F71789C578AF77653D78A67B3E09A1FF3534DA3537A6D7126132B251B5A5A8342746FA272879177F790A508A13BFAF11262377CDC127D726BA87BDEA8DB817CF3C9763FBBBF1B6C356794BDEB3FA5DEF362BA734FC41D2D003AF5G9B8174BBA08684E885B06FA01D7D4558BC1B4E1AA80983FA23D78DA74779
	DD5EB53FF9BEB5EBBF3F9DDA3A33B94BB5F4B76CB4B94C7575AD30E55AA128E0D992263AD31689DCE0FCFDBD1926353E82E01C109D343EC61AC9472BF7100E57B5230EE3211C1D876770BC527167AF4CEF6E774C5A2FB5CF2B5F075D73DBA76234FE5E6B641257F8DCDED7856FBD9B4FD59234DD10C7721A50112F8DD464322BE7FEFC16CE6B674DE7F81E163FFFA4BED126918D757C5B43884BD5143C93E9E8F807F02733597788A8CF3CC33CEE4EE15F12213CCC413D4DF0F8C642381D633E10B3D36E9E7B537B
	32F7BCF91C8B70B19E1CF10CF855FB42ED52C7606BFFCB0A6F1CEFEC0AF64ED7172A1D73CDCC4D60F820217E52C5756929B3670473D235F2423B59DCFF161B46E83BDC220BEDA22DA649A4577DC1427C78345C70B958D3E5B9E589CF34A549ED5AA4FBCB35C07EA3E987664CFD442BC27ECEBCEB5CFDC3793E8F3A0C1075295F4973045C661E3BC9F6BF34D3598BEE211FD8826595GEBF7126DEC586EED57D1D0973353371DF01F1E140DE79E63AE7063G0DGFDG933B4817AFCD1D75BC51B6B143F9A2B04D37
	ADFCA7CD5B969ECB43BC1E4BBE669C943D753D0279D7DE675EB07090002A61ED7E057517CC4EE1F11A3D2CE67DF91D2F7E4F267932A92E779B2754FA2FD156D6EE12378E82C00EA13BFC6B00532BBC2D26F7C421D7106CCDAF94E8054DCA6F779E740EAA748E983C69ED84DA3133527BE63B0A5E59CA01FC22A5976D934FCA6979E41FDBA750BE5B215C8FB011CF76797054CCFEE7D13E1B07A0F8DF196F1B27359AF57CBC7DA39623EE480F743AAF709443C72B707C1C5DE124DC08309BE2505D644377339C5A9A
	A8C76C26F530A343379D6F49503663428C5FF6FCB0E3F6BB2E4970ED4747B3E63763560C196D78979DEABB29D46C64254919E207CD3BFD493FF88A65A96C61F20ED370958CEFD461397C1F38CEF70A7220EEEA8F65065E69A23E3AA4FBD735B2672FE67A5641BFE6EA6B603ECC5FBAF8A8F3F69DFCAF53378EFE10B93B8E7EB993574700E54862143F0C71B97E49E30E6EB575CA9A1D3DB66C21B5D55808F24A5B9BE928GE85FCBEB4A5EBC131E7A777A52D3AB3BA7A36D755653D906775B6B2D27DFB5131E96C2
	DD74DE5253139754F328D26C6A2B5DEA3BBA264CED2920648E8AE8EEDF0EC35A8B219C84302A0066F6C327B53730824FB9EDD41579585DBD6A312B941B7EDE3CB73DD8681B7E2EA73D42F73D695D1509B9F62BE952BEA689725E9D10C9FBF7AD346F8218GD8084FB768DE5C7747D16F17618C8ECB2E47A18E8A356FE15F0F3329EF9034DF89908E908310779E49F5CBA5724C3F0AD5521ED36746D549FAF96C8669458F7A2EFC0F3EC5FC2EEE2760B37EE8B60181F3597F09750F06FE097BA28D857BA89FB6311F
	72E1A10FBCA4ACFB73DB1A5F55B674CE68EA7749B96DFD6EFCD8617260EDDC6691B7276D0750AEE41FEF5F4E6D702000FC056E63F61822606B99BECA05177D880B09301CD888E707C27263DB79BDA0A8EFD6F047939117D88472AEA25F1E116D5B2F64E5EB7B15BD595A71EAE1764C71EAD936AF1F4C79BB14C4BC9796914F1FA6914F358A6ED8B235EBD5F0571329DD1F02EBB2D03BA185F737015ACD9451FA3365122E7C8197G90D26456D3A83C0795F91D2F18CE2ECC226CB796F967362FB2FCEC11F7EE7B4D
	894AED276F87DD6DA71E7E1D651C42213CC941255EC01CD08CE762E242FD48F0A545BC374BF1B186BAF3B59453FC7E4508F7B44CCA79272C9C20144F9BEF771E94521AEBDBBE06793647083F0D36727D5B136A7C7C5AE274B1A758DD66D50A4CF457C8BE69A5B47FC3D3024267D1557B3FDD3F937747BC65927E990C31B50D6C3921046B2D2B442D3FA1F81FA8513267G3657AEAC7136670799BE30445B1E8FB412BD2F003A3CD21279971AC966D325A473B3ADEEB977285EA31A6F3CBC36FC86E3AC973C7DFDB0
	4CFDD569EC7E7EAD96832CAB7516F9120E797BD2EF19AF0AA31947C05D2583A47345ACCF948A65F58AEE8B43C5C0B9C641BD9DCFBED1FF10F05B744A3E20602E6829EFA160785821193467D6G4EFC10765C4AFE4C0DAAFBAEFE042C2FBF6D348CB51B06BBD97440E320442CC439FF60C10A010EE6A14DF0A8C783A41651795FBF1B5A96G2E2F8C57768F32555FA851DFFCDF4F3F97707AA80FFA5CEB700E223BBEB7C7DD0FFBD853B6F5FD3BC7FD022ABF0F8D1EA92258A07619E502750737FC5E3989AC63FADF
	ADBFC39FD99C97245FA147864BEF4C6FAE7CD3375D0F5B5247B759FED466FDB6B4B33C71107759B07A861D8D73214E6F90FD037DCB8E46EFD1D0964A41D6GAAG3AG2C6594CFBC1DDDA14C12B36935BBBAA160302AC284EEF32F5EA45B74ABA7BBEC38053845D08EAAA7DFD24972264B203C9AA082E0B3C07A61C8C359E1EA53485A60B7EC3CA357FE185F01437BEDF8F7CD7677377AB5E40674689DE9B2BD16EB2F63E59A783243EE5CE6056767B5475A2EFAB7286CCEEBAC2D7DB1C5751EA4D05C1E4CE4B6EA
	93C18116410943E85B85AC767BA90079B0455A3B7538D7CA90602D821CA02C47E5AC707697D95C9509088A70E32CBEEE0F3B3E1F45DBBFC399A97505FB5D75A3ACAE79BD5A1AD2FF29G6B65B80C65CFAA202E3302F45DB10CB83CA7582F60561891678F65G85F7FA047617D58AEED3AA620C15602FAAC9661F326FC935D06ED3F0D38C0777878395BB081F1C4B7E00B4FC55554ED04747481F5398B3651DE70A0F38BEA326F02C2B9617856F0BF2BD3CBAC55FDB7CAAC96FC5AC0EF4C1986EF008C33CDD438DEA
	37C8E9E7E3B8BAB92D2844F6E946029978881465407BAFBC083F7B527EA85BE287AC9E93EC983B716FA1B1C66F6FC72823D89C370AF86AE1711DED4CEA15FF7BA42AFF5FD5F17D348E5BE9796412AA4EDB2D7CAC8BDF9C4767744E351966E4165B4BF1F9BA596DD495595E4AAE3251C58A2E2A1BECF405027B079232D1FDB5595E830D6C7EA5144B945C238C57D94D6F25F2DCC563DC6C87E9782A4B1B210E0F51B127B166EB23DC5677B74FA4EBEB35DA56BFE7B913A940AD2AA699E645D11C932448CB4F621CE5
	D0DE276052982EDF3E134BF15F08D34FEF1D8FF9AC1BC1CEB3819F631FBC46389DDFBE9760EB60C01C275F5E7F1E20549963F1CFB0497781C6C7CDB63C7F405BF56C43F6FC3D3F4462405FGC4D713FF36968ACA5BB03CDEA14F69A7536EFE4DCD96E1736C1712B53FB8A7B11B65E0EB3CF3FB998CEF2C714E6DB5E4A9B9D8281B2AA1BB79D2767CFEF37854F60A8FD7BC79DDE165975E77F917EAC38D37B16188FF76FDFBF19CBEF91C953D9F7DE08554F582B8G96GAC81D8F104622C649C64913FC08DD970C6
	01E9504CEEE3D310F551A20E3B51180463F39B3A0936336608127BE0E57C6E4C4B2F5D507281EB0E4CEFCDE86BB76D26569873DD935E4065931363BB2F75F60EF6DEABA1D7BB2FE5481D79BBEBD62E6F5F5E6C4CD57F76062F3DF7D9FC76E33443A34A3EF64BF3CDBFD0A28834269FEFD57661F362B8471D3D08365BE2F7CC52C5C00E2F3A09B6F726C784DECC125DC9B9D45DB872F81E5FA9D76E8E723A4FFF02F2EBE5774215AA396E709456F5333B5D6EBAFEE7F42947BD764DC7787D85371FBA16084F6241E2
	F9FD840E2B776A5A649B1A3F6DC148BF4A69D49CF56F551C46AF7E2626117E2DE1DD1C8F9AE6392FF4541D1F65FC0DA6B13E6C45B21F63492CBCC4653B18CE0A1DD4DE120BBE556194AF0BCE27B854E339A6AA3A7CA10BE303E10C95GBCC7BEBECCF14C864045G149D0BB4B49E637742B94DC873745C7AEBE66A5787ED9DG81GA1F23F18E3AC07BE52BA036DD36C93AF43EA68938E10A317B79DF37BC04EC7FC9A5A4B2845E6999D9BE5B363735A453E45B456028F2AA51E1FEF465FA5CC3B3ED5C934745750
	FF0DF844FB2AAB4A4A1849B7182DBB47C459763D4AE5C6340BF3F627E49648370E0B64DB733F7730103F7738AE43075EA7EA393E03EA5DFA47A74FABD42450F71870DA4AC1ECCFE33F352E0BB414GB45651F9B77FG7255D7C7BE7741825CE8AF1AC664D3AD3F8745BCAEA0713EEA8F38885BB04DF349F554F10B0872A0FBB94662C7AB501C28A3FB79ED1772DD240E6BFD95BC5755717BB1DC6F9D5D24770DF553FD31DA27883150A63D5E2D7742FA375EB99F0F33D83482708D75245F275EE35FD9BACD83781D
	05638E6E425F82CD3F78D5A97B007FEED47BGF3BD67F921C27349E614E11B49A57172EF0569E75398063B9FBDC4BD45A21FBBA8A876724D23B806F263791C5D56A359874DA0F8DE7F31ADC3DDCF15CAFF1D1E2DE7DB313C7F24A2B979FD076A3DB96B4E67897FF6C74760F65BDD0613DA8632DC8B908D20BF9EE948BBCEF61C4B82C6FE9351BBEEE89B9A466F41784BF909763757777B5ECB056259FFEF1FD40C3F8D9B30CB12FD946993BE2D98E5A2599D8C49F15945385E1D16E133441B52BEDA4C560DF81921
	39DE17325F7B2ABC8C3BE3446BEAEFB11D1B0A65EF8577C89EB9CA77F7C1E52714FF73F988472981D9F5A972FA2164608C6B3E4BE9F708CEE912E3592FB8E5BD0C148B8A7F2525C53EE800544FGCB6787C644C12957846EC30E41B79CF62F01134318DC310963854A6FBCDAAD20B97CC382A5C7AA4A8A2AEB0F144AF16D0343143FF3409C020FD31EB62120C8C386657CC7D47575ED5220D99CEA16FFD2A57B4BBF8CD39EF643F17217B9E676CD3EA152D081505EC07961BD960A277B819750C0633ED86E719B86
	1E9F6663707430BC56E58B0D352E0146DA4CE8A61E08B494G54826C65F647FC867F977D3E960E4BF081DEAF1077105F9A55FBC86BF36F6B76895AFBC857896EE383CF387710F547DECB64B4DF29F26B657909D974D2D5D9D3AA5B43F35585708C871A1B8172CE027CGDA81AE81B8G961CA41B3315164CE0F3DCF8EC1DBACD961B44FD4B11946FF3CB50C977DE31726474FD031F0F2E337DEF6DC96FF313BF43071F74BEB72524111E23202E7DFD623960G6EF7AEF98F64323AF544AD2B473AE611D5E5C59559
	F052859AA38468C73DCF5F85AEDCA09B49EFA49B39455A76C2598A605FC8ED2FF553DE3F24D14BEE929BFD5B4D0BF8F5CC1EC3D22DFB8E5B9B67B28779B73F17FD7C66176D301C474CA63ADB9B56C8FBE69A0BD30B4F954B3E637F4AFC7BDFD3596C7EF7240C7DDE977CE66F24C33E1BE3AFC3DD59AFDFF6090A1F174A5295DC072B4B6E3220D3156B8E213DA8E7A06E6664FE578F61FE237C0E1339E29E89F13B7C7670EC67E9E31356FD65653376733E3F4C7BED1A351F76BA789273EBA5CD5E6B60DE062FE972
	DE873B4CE4230DD09754C4E768C24B7C729ED5736CF765367B4979129166DB1B2765CB98BE3B59FB3E5FD27CFB815479B7D31C3C3596E3530DD05637C08C8D5088508BE0857083D88C908C908A908E3089C0E80DB4E48394825482F436129FF936923F3B7972FD7207C5377393488EFFD76DFB1D7C31FA76F572CA35F61C92D3BDBDCEF933DABBCE092FD147A972BD6E9ACE5729DC226678549ABCD772B3A07D3214F21F1D6C1C528FB211DAA977F9EFAA651483DA49566E42A94BF670E555FBD65A1D0FE54BBE03
	B152E67DAD07F63F5D736CF771367B71B573AA5B03D635FA2F25D806DF536A3D166ED67637B0347FB65267AD0F5F12AC817C323619F3EF72B926C90B6F55F25FF07939C9C50B5F758F4DC29B34582C961B58427F7A15C273EBE637BD85285E5FB55FF93D57FF86E3240E386734C3759EE83973E3BFF1C747107316F53E7DE1E65D6C7EF0D71D36BFFC37EE3ABF5CDF276D8F4B6A267B434ABA0C0F06410F77A96D0EAA380F945CF1C693F19FDA06D8AEC87E3BE80CAF44370BB8A061CFB33E46992ECF46F52AF09F
	49388BF5987B40144D7837FEB86EE29D66A9AEDB2CD6B966E2EDACB6880594F9FC42F834B8CCAA3E07949C724D6337411469393DEBA9146391CEC16CFF0A2F43CEF8623D3C09D3586651D4EAB3F4EA7A1A67F6737A85371D6A3A5D6FDF39F067EDB6680E0E417738EF3018BD749477179CF79F4305FB604AD95C3C99F07A5334BFFE3DC9396B276056B2DC8314AF1D26F36AC3A964FB9D008BBECD675461775C3FA9360F16DB86C64B472E0E161B8624F291C2107A7AF3E3D62BFCE225FB44110C5686209183105E
	CEE70FDF24525923241D6E960FDCD11FBF71CC696B7ED0FFBB573FFF3BE78E1373E95A2F7A3BA9ECE9F0FC7EC40A9BCFCBC3364936A64F6F8B4F6DC70E78B7596FB6954C2017556D6A1C7E8BCDF8DE0205E339E637C9A6EB1355B2EC438C121CD7FC33094EE7616D342785CFBB174E4D5E9E3F6DFEFC4F6DC9E5F1ED0777DEFC096133BB3C77625FAB76D0GF57E9D44F7D82AFA7C0D000F6D18F9AF6671701345380E534FC49A4AG3AG4CGD3G0B4FD00C3B358EFF5B61AB466D365B5955883CA581A12E7A28
	AF6B6127CD6A9C397C37D21A713E05BA3553037FD3BA4FD55E52F62E372DDB116FC74C9D493F5FD73D3F14F267FD09BE754E7B1217D24917BC9CC73868D472A599F16443FFDC6CFB1F7C799C721B3F74115F7C35C6FEF33D0F7CE614C7FE137F4DE53F53DF819EC37EACA4C8029EBF7541833F60C942B67E318EDE7817B9C958F94EEEBB44B25FA617E499ACD0E5C5A5615D967C1B25E28136F179E8136B2B73EE39332FFA232A648273BCA70E3B1CC2667474C853D7FF7DDC48A72D59BAC157A1ECB3B37F54A1A4
	0C23831BF040823C8BE6DD85FD53143E9DEC7F5DF0353ABE7E050763F5029EBAF2B9F788FA949674F7F2BA71A0E724A26855607AD438A920F6FE45E8D36932D2B27A6C415AC802214BBC895CDB6D431375F29E3174167A9BE44C5D9F7B7C0E3929D37E4EFF566D47390F58F5066DCB006FBBCBBE61CA47A1DFBE21E9C06A99B5D92D60F12FB45906B0DF5CBCA9096EEF5085E7103E0B5A48153C2E78CC126C97284F7F83D0CB878865A34EA9AA9EGGB0DCGGD0CB818294G94G88G88GCD01B8B665A34EA9
	AA9EGGB0DCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE49EGGGG
**end of data**/
}
}