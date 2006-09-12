package cbit.vcell.client.desktop.biomodel;
import cbit.util.Versionable;
import cbit.vcell.biomodel.*;
import cbit.vcell.mapping.*;
import javax.swing.*;
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
	private JMenuItem ivjNewMenuItem = null;
	private JMenuItem ivjOpenAppMenuItem = null;
	private JMenuItem ivjRenameMenuItem = null;
	private cbit.gui.JInternalFrameEnhanced ivjjInternalFrameApplication = null;
	private JMenuBar ivjjInternalFrameApplicationJMenuBar = null;
	private cbit.vcell.biomodel.BioModel fieldBioModel = new BioModel(null);
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BioModelEditor.this.getNewMenuItem()) 
				connEtoC2(e);
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
	} else if (e.getActionCommand().equals("New")) {
		newApplication();
	} else if (e.getActionCommand().equals("Delete")) {
		deleteApplication();
	} else if (e.getActionCommand().equals("Rename")) {
		renameApplication();
	} else if (e.getActionCommand().equals("Copy")) {
		copyApplication();
	} else if (e.getActionCommand().equals("Create New Application")) {
		newApplication();
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
		this.newApplication();
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
		}catch(cbit.util.UserCancelException e){
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
			ivjApplicationMenu.add(getNewMenuItem());
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
 * Return the NewMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getNewMenuItem() {
	if (ivjNewMenuItem == null) {
		try {
			ivjNewMenuItem = new javax.swing.JMenuItem();
			ivjNewMenuItem.setName("NewMenuItem");
			ivjNewMenuItem.setMnemonic('n');
			ivjNewMenuItem.setText("New");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewMenuItem;
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
	getNewMenuItem().addActionListener(ivjEventHandler);
	getCopyMenuItem().addActionListener(ivjEventHandler);
	getOpenAppMenuItem().addActionListener(ivjEventHandler);
	getDeleteMenuItem().addActionListener(ivjEventHandler);
	getRenameMenuItem().addActionListener(ivjEventHandler);
	getBioModelTreePanel1().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getCartoonEditorPanel1().addPropertyChangeListener(ivjEventHandler);
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
private void newApplication() {
	SimulationContext newSimulationContext = null;
	try {
		String newApplicationName = null;
		try{
			newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the new application:");
		}catch(cbit.util.UserCancelException e){
			return;
		}
		if (newApplicationName != null) {
			if (newApplicationName.equals("")) {
				PopupGenerator.showErrorDialog(this, "Blank name not allowed");
			} else {
				newSimulationContext = getBioModel().addNewSimulationContext(newApplicationName);
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
			}catch(cbit.util.UserCancelException e){
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
	D0CB838494G88G88G540171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BF8945555C103A08688889AA8E8F8E9449483C68996EBD07CC424AD36D4D34B5F267D51E24BEF5316760BAD5649A601E042CBE21BDADA5396851446G312691AD01E0038D38104D92C8005D6CCE32134CBE581DCD82C47C4F39F74E6CE4B33B8BE9E93E6FE4661E7BBA77BC6FEBD6A8FDA339B04FD4AA88F905027A77FE1EA0541F94041B97AD9DC238C40F35D8507D7D89E00B703C37G1E491877
	2835F8337043CBC510CE07F476A356625F427BD661436672A6F80950CF2EC590626FFAE4C774FEB61B291FF5ACFDDAAC0167C6008C406133400B7D7B45B2953FDC4593641984E1A5ABB3D25C26626AA1ED83188E106165651742F3BD6BF9E8CD35DA2EF5CC0A30FA315F34815B5135C9D06963F5AD6A33DC78E55E9A9F76F5BF23936BA4C0FAA1G8EBEDFA82DF99F1E7B4DDD9D9FB95AA425225BA14AF205C316C40FD2619483DD0A57D751A6F95DDE27A857549C143CC7716D30D3D23CFE2177BC367DEB310835
	A58887A0DD60C75C8DEE6CEFBE2417812C5270C7BD082F07F79BC02F1F4678610E7D6A981AAF4D95D6F4BC3AFED304B1A67A7958B27C21B1163F5D167FBBA68F73F1647BEA48AB8BD80BF7831881FCG09G3FE7FAF0527CB6BCEB5B14C3DE375B6B2975798E4ACEBF9B973C5754C00E0ABBA1BA3CFE27A0F0D94F97F79B50938DF83D45D7DD0F73A4E59F49BD7544ACE1793D3718569970E4F94AA5D3CA0073A2B39062896FBB3FBDD25F59017EFA593F6F13023EEF61793D6B8A8D7A5E76545EF52B55F60A14D0
	5F558A4A7A17CC87F642FB1E2A9F9CDF40F420CF0767BA1A404AE3BA8172F294520D660DC4CBC341D4E1731EDB8D791074F002499264B44841F0DAFE4E7ACC8A72BEDF53E8A972A2FE098E4FE9E9E465B1ADF4DB0BD38138FE1DEAC41BED6E3196ABG89G53819682E4832C6EA19DCBB63FFB05BAE6367B1503C1C9F60AFE0157BDF57CB77094FB953F7D08A72058BD8E51EA77CB76B6D98C88EA193663480336FE75BA186EBB200FFF0A9E51EFD7A42F87DA373BC5C57487F8995F71ADD846AF32DC0B64962183
	37CF20F6FB0FEFC57E5983CA134FE9D744D0891E9FF79CE552ED37C20B50G3CF33E5CE0C1FE1576024D816C6FA5BE7C0A3577AF516FC177516075F685FD969C0910500BFCFE3D9DFD07995EDD3D44636B1A90379452AB35F6761EF8D557CE1D64C0726D7E8BE4E302877D721ADE5219971AC9E76AB6FCD1587D4D7106BA13887ADBF6016BCA6505F05BF9053509F47174D8F773D55A7115G529C7295F7DF13BE7255A767EF0E95E5558C63D5G46DDA09E3BCF14F31DBDE88F0824251F7AC141C4E708631C4EDF
	F9F62AB4A33FB1D0E2CC5A445A9104C255F65E8458210DED23976B24C2FF4BG56GEC3FE8ADEEGD0GA681AC84D885D0FA09F8FE4A132C9BC7E104BEB72A7D3E96038FC44FFF1A3F1C5678FE34EED2799ECE73C6156EADB15A3C12718C86382C9D0DC81F588DE13ECE74099EA73C84543C4EC624D575BF6DDE2D0E0F61FD602F8E010388E8786EC69C0BDDD17CD2DBD0919BC5C511BC9D217C4B2C2FC0304D97F2F5545750C6640F37877C15D6FEC4A35A3A436B7108CC6934AAEAFE8273B522AC3A0168C35E20
	C7617831963457AE71026A6BEE312C9550D749C1741BFC3E13E6C799787C129BD499DD2F10F0897D501E76B29D0F76838E67EDB6F8AA174827747C05FC4A760BF7891967FCA6231818B4E334E959A5EE53ABAF05FC8A27E9D813B16DA3194FD37181CE6B6BBDD8B7EC4A255A702EE6EC27284FDA3C8D202A0FECF83D957349CF2A46FB48AB4B1C159A0FFFE15D0CB2914F87C588AF68107F25716DA56B2BE1F9D61507BC3F70A472D056797295FD4843FAAF4DE79BA06D84086F63FCDC584779404B157828DC65E7
	56627A4FC265DC1F694BE5EA650683BED9D7AE33DFB965BC154B86FC2E2EDC59E5FD391DFEAAD78DF87365D0B91FDA2E64F3AC37DCAB17F819672740B393E085C0AEBED9FB2FAAD42E996A79G46FD4E4BAF7B1C37670A43F2CFE9653647717C7DF13C3C991EFF544A5D9924F269EA39A5704C86A89822AF5753CD654A0670F215C3F83986359C571F1F6DC21DE6F37BCEFB373D37A25083F6D9D37309598E5A41676EDFE071974B3B06509C7DB189F5AB995299GCB07107EBF39016668633AD204654F6FD997E9
	9D32F2084A4F6BC27A3FBB27F6AD575D40A96C338172DA2FA33D0DBB291FA7548682DE074426A82A4E3D5B2C378F0E2BEAFE851E6D12BF209C818756CB7AD9530C74629CC6C52BF33A73EC2E07E375C13FA9G872EC77C3166D29D6D5900AF827CEE00FC56DE16F7AF7AB7E6C0957CE1967D6DDE3FDBF46EC44ED658FB148A66CBABEAD976E1FC5FC5F2B8FA997D23C76C0118AACB8EB62C1D3B2C824D3527EEBD20763DE456D4E17F67E7CDC6EB1F611BDC266157F3FE26DD9F62EB7D58367CD6GBE4677FCB446
	B5D026AA1E4658FA4AE80CF2BC2D83836FF01F6B3B90CEA4EF77F92D5DB828132135BB7634D13BB9DA3BBF2A47F63D90E10C5B1DFC1E5AAD988AF6B1145A1D54E2542EB2145AFD23847341AD425C36FF4B3C5D63FEEAF7B85449545A154F99351B2B351B74A6667BC58F4C1A0D5AAD8CD03BA5B7D80B6BEEA09D1199DE72C80AE417250B624EDDBC2EFCE4514715BEA81F86308CE025D637C946B85566C5E7AD28EB6AFB8334269606117DFD710FFABDA9328FB554130467BBCC554338FE58060574A48DF4E42126
	A79FA8B40E94A8138D40693D61381E5E32611CDEB3BC1D43095E294A3FE78F237FD00B63D5631A45AF0A4CA16DB9D79B56D6C36446BEE07643477232C26B46BE20B1DD2333BDD8246B0763C687B1F6A9AE11422AEA03BF2D5573B6E3DD02A1EFDDF7A619D68D67BCAD3DB1445B2AA9ED7975D30837CB03C40F934ACC3A1168F93FDBCF8F6777AF3A747CDE86E5B70FG3B85289FC175EC7D6A71BE123B298F9B14CB524A0E6CC33A1DDEC7909D65D13B475E81CA4573B28C73785CB5F940BE415291217D0195A386
	6E95707573871DC8C76E883EFEAE54709FB3FC49CDA1BC277BAB9269DDB964455FC4EB3EBBDA108FABA1DD10GF39A00AAG8B0012C0B1626166FDC20CB575918FACF5BDF639315FE413563627108FB0BFD453FD27108EB6BF94E88E5F638D67C3E2C27F31270625F93D21836AD50473B32C9E3705C505C80BC10CBEAAFA02C794512D4ECFFB2CFABDD989ED150C04388FD08740FD4650E60A652DA349E7FC7631BEE64F5F74192958C02FD97EC8CEDF7A4810DEE739AD6B54FD35B3B867D88DF91BC7D90BF78254
	8138G6281D2C6110C561E7EB0160C187DDA3C07GD78FF348C36173FC0D279F7886A70BA3836A054BE251287E75B87FEEBEC57C4B9EC56B4F9F8F120617E252D0B65A080647CF938D15230906E96787C7436C817542E9F09952700D9622A1CE236124FFF0B408836A055310E1C84334F3C443D20D06624060E8785D00FA61B494A59A5966279D38DF905A9B6F88CAE01864E2BE460D35439E97EE4FB9219C37534B1D616BC85CBFAA07762B9249CE3BADB8B6344F06C41AF3AF3021DFCC0374A2006CC49AF346G
	5A0DA0D7C7BF3757474E223D160C81DB85B8G508A9087B089A0F30C3697FE7A83614A6C15875F01564A692CD586A71BCF87547BE3D8BAFB0C11EC2A2B5F96861EDB28879621F38AEDF2C1FA756563B42F4A9DC33C3F2991F9DFB996E2AE00EDAC713EB0980B7746E35A9A33DE6F587E75384CBE32214C92A1AF95E0A1C0B6C0414D20BFGF5B7134C6EBAFDB0164CEAE9B38664C62F860E16537B54A04739A2E63D561B0DE46771D798482E43EF7739EAEA8EC1587642A40F6DF6B051FDAC752201F17B2AF69A59
	17827D8C3F196C2BC2A699A75FCCB27E060CB24E02F48EC051B812716E3E410D3DA6E63D72F17D6B717D6E39EC3DDD8DF98DG9CB74E05B89F2407EBF455F6A2AE9552199A2E0161F0BE0EF3C40E7B0694ED3FB8D5C7C3663801F338D8407BD895350FFC5DFB61D5360F40F57A1E16487B117737986FC74EEFB15E0FFC38E58B47C39B7A53954ADF5C92FD3F726BADC67B15FFE0E3FD7CB71BD85B010098E00E314E2E58553C9FA838DFBBE05F7298448915CFDB05D95E75EA19430A7750DE4DA6567F7637C244F9
	2030FBBC6A4919A6527DE5A04BD52ABCCB47733DA92E7B77D8A9EEAA63B57FF692F5BF95528B81160FA75D99519C2E432BA12F681648FA427D54A3F6DCA3EF03F255GE6G974070DB48BF39DAE34E538F77C619271FBE93D9976CE70CF521638C6EC5853CC13FC38C173767CC191697F202B839C65170B2BD2C3EE277F708837465B22B4F73A2559FF6A612CEF1397BFDFA396F53ECAB69D4388CD2000F73234800377756F9FDFBD5DAFBDFBDBDE09D87ED2D08595EB37D5AFBCFEBAF2BA53C3DDCE82B6456D86D
	0D726B5B7B1B56DE4A3970764A212D2A186D9D8C685ABB3AC720F8FEBF73093826293F15747DEBE65477B8C8A781245DCA7AFE49924D0FAD5251309C5EF32250D41094E9BD7C1E9D7BAECB8ABF737E87436FCC8ABF73FE4CC66B78EA481B14C4B161EBEC7FC718E0ADAE1DC0F675645948F611F55658AE3EF3B632DDBCFBB636DD3CF0B632DD7C7CECEC3B78755968F6512B6875E40F26A70F9C0F96DF37CF08447F1759DC2EFA8267F31E06AFE2E7C6F5BABC677F18C63A97E3063C7489340F32BFC8F419952FF9
	1FB666FADBE499FCEAB316C113AD328CDAED31E5A05AA24BC0364516C150067651A6B9A53FFA8A44473821DBBF4610CD791C34BEA91BC8B635126D5B18A16D82081BC8B635CF0EA6272409116474CFF657A3F5E2381C4C8C1FB1B1DCCE5F12C9CE8BA1EF6797C8CE0EBE7D9876EAFA7555F65CD3156D973CC1C5D06367335A99DF82541F8E3084A07B8BFC8E4963706C9E64D5BB2C6DC0CD9BFD22C3C81804F56FF6D15DDCA8DF82385DG8DG72A4226567FB10A77CF4E50FCA47E7FF8F61762AE3995AC4E3D986
	F5CBA6D35F577D7DF5A1629C60209D044EE571B02BDF8775DA8114493457537B966D75245EB1D5D87A2C7179CC3665B3D342E4759CE132EE8F736D36FCAE53F4EB38CCD7C1B96136487E104BAE1F1DC9165E46E537C1433F4A7055BA3CEAFBECDE0269D648DBF49B793EA9EC9F2B6AF6D80F5DCE38E7992E9552726D648F1F30C736452F5B0DEDB14BEEBCE77C0EBD7A1C71B9FBA4BF46693B6794515C2B517C33D3C4F3C2E89C2729DC12065B55C265D2B55C0FDB28DC06065B57CA6516E838074ED139AC8D17EF
	23F22BEEA7BB7DD2A749B497F0A5493026CC8E49338A5E6B137B4B174B6E50F114D1EBF2787E6EB10677A5076F6FFE53CC7B3B711037B219E81A4C76BC9A26407AFA8A611EE3B8855243B55C3B6C9CFC52943E3F49F15DB6DA9F25CD2171FCE047337E65DA7AB0CB17CC2574D7ECD7FB0F865B3A52323C0F672E3A0F7C2BA82B5E4F7CC83FC7FDE0AA63DBG47DAB91578734CF162CF3CB67EC20B205139D177FEEB633538DF541FAFE97F05BE02CE52673429DCEE8B27066417856F2B269A69F3ABBB179026056B
	F3874317CE8B57679734730A1D1017B00DF87E09CCBC4F19C6BCBF6A8E71592AFB7F2EFC6D7911FBCD7B603C5850C1F1EB5BF4EB31E5BA452DBDEC5FCA0BDBF8E0D7D373CF3F64BCE86FE8E091184FAB1ABA0A547A8953E9DEF1BDEBF3A12417832C1ECE6B58A7DDD436E4063538EE861B5B3974E737E8CB338B78DEAE4F5FA3697351DF6F9274790EFE799887EA576A7347774BAF545567717509B7A93E2EF9A2CD106F0B0FF88FBBE1069ADF55E779D7A7648DCFB22F65323A29E9F07708A7DCF5BD2E5BFFE377
	7DD64E88DF139CE0785C9961EB12C5CD34A6A93A83626D9DF4C6F7178367C0CBA13D8A2060CEEB71B6007DG4DF7D2CC6EEB2B94E22C7DADDEDF8384EDD997E639CDBDE125D8A65FC9F6D6FA92F1F110CE3C136CECFDB352B1895269GCBGD6G88A9C0CF8A15A9E3E5708E925EAF2ACE61770770EE905E7BD1FD61C91B814F30BDFA47B6291FAB65B952C06FABB41A67987635CE77DEAC046E8BDEE9DF9B4274194FB93F4F78687608A0G4961CAC1BEAEE673277F81E8CE21796A1AE60CA38ACC12D2D370DB07
	DBBAF1483CFC961B3BBC861083E91EBF2FAB143F114DCF3E0B083BA8FF399C4AAFE3F3154781B6EB79251ED07ECAB6EFF996EDC34B2F43E91ABA1F7ABADB57D7C2DE43DD248B9FB11F0BF730EC9A6EC1B76262A11D246176FB49B74F57F09ED62EE42635F87BCC12499196B72BA15D2C61ACBC16FE4E7754B86E5B66AB712D58C6C4BB0C12477BD8FDC5FDC4530DE8F38BAE6FF48B7655AD85A43C334765700405764A63E612DE3C44F081186A7AFC221317ABED22F209DA390F990ED6A769B3315CC8DBF994BA12
	D5BE707A896F60B7B54687EA3016D558D45C03739E3E775FED8F5F7BC7992D41FE6F56F41A4D0DBCC1D9D63FAB91755F4ED4DEEAB0E1B9A31F3D73EECEDB357ACCF8F9C29E9F53CF0EC79B130B4AB3BDCBF0125E765DCD3AF7CEA29DCD54F05F6AA49DCD57F09FF6110E9624126E3531F2E5105E2D615C8C57104A6F7C297A93F50F1EG5B0814D796A50F77312173DA6AA86735DDA6DE4B8674902F55F3360E414551BF8D325E692B2E4763EC130B6DC726066FF51CE5780454703D0E9B35F9D692642D51647E14
	EBF05F91ADD8C773167487E68A99B754C53C83E27BA267EF1C7A4CD8B2A18F1FBC7ECF73217D25C15E92009C0022D990F3812AE6D17CDF5806B4628E3FD342D3C83B4345EEECD27037CAE2CF880D9BACFCEC7B2DE8377533E82C9F33B45E4DD377351B0CF48F4BDF8DBF224B778706FDDC4D1AAD9AF07E4CF1C45E331867B05E33F848E13CE77128A37AB956D79C116F5DAFF3686F5DF39F7FA21BB7BC82601CC57E741D137D67882730386A3F63DFC3BDBAA676E8F715B87EA81BD79E727AAED086476F3D0C65BF
	E994019E3B6275539E192987697C145F918DEB6B7AF3483BBAF64FD34BD2735E6D433C936C2EE6A80F5F73EC6FF7AFD519456FF9EE54E87FDEB3BEF7F8F628B6125C230F93552FDCE7F8379FE1122AF3E933C2F10237713596FD9B1BA74A263C88ED2CD46B2E1E955AFF63F47D185DD15B6158215279E68BCBFBA97DEEABCBBBA97D72B91676D37A79CE1C937A7CE23B68770B4EC6692228495744624B4EFB40DE81781E28ACD3CC35812E97A095A0739EFE6F1337394CBDF0AF75C7EE2A1785E5F3G4A52204DB4
	DE2FB78D4BB4FA6B2258834561FC956A20CEB340EBEADACE8B79C5CE47D0B76A0FDB72C86E201B0D0C0FEBB21B5BE5C279A5E9C473B387715E7100EBA0FB282D020B6853BA7B4DBDF477FD35BE3F4F3EA9D17CC1D13507267E6919B65413E3DE3F6292485F760864EF375DBDCD58764681531688FA101B46653FFDF6C88F70495740336CF4165BBC1B564BC39DE8DB0910CE81481CCDEB3327593CA7EBB67961E15EBF4353EA6FD4D7E07CBE85734200443BF70D60B6BC9DB4CE2591E5FCC8C4FE103E5C4766B22B
	204D5C5924AF43A5127B76B9DC6E7B61E91E436FA4F03967F4125CDDF3867AE73DCC591DDDA813B8A7A47769F3C2F267F434B33DCD837C7CB9A45F85FFE67B6A8D76B65CD7D7E573FB7C06E0608512BD2ACF383DD36F93D62AB4175CCBFB8EB74A446F72FBA96E3420D8557860543D0F6E3876FBBAA957348F2EB7BFD410A74DB0DE85A089A095A073DE522347199E719BC561317C3033834F2C708BD505E2CE2AAFF2FC3B57977B3B5679BE7C26234D2BA8DEB7362F5D6970A1CF942F0FA1B96EB1F6266E17BADC
	8AAFCAF14F07F21445F606D66FDD306F71F4964E6ED5703C4DA7198F356FD077DC47A87D7633C267B0DA64623CDB476EE5AF87DE65EA7C3A4D7FCE943BB37B3DBE512FDC60D876D515AA87D30F20513F427FE624B6006B1FGC91F36F50A8E45E2877395F8DC9E061F39286359A73FA1306FCD4A357B73F592C88EBF585D79DE656E72FD556F6DD2671A63E45A6BA9CB87BBCE27BD3D4272B78DF8D041BFD22829B92CB8DC2273207A49026A2F164B34E7B7BC1D7C55AB6CBCAF9352D9GB9G3C5F2D9E1A63965D
	E7ADBEF09F757BDCC53F7B50FCAF1177433792553EDABC54D76FFD54570D2C4DB4C8AF824886F80371837A9068AF679FC6B84E43020B1862BE7CFBA5FA9FDE77F41D69AFC2849FFE9F77F1757707FCF8262DEABDEFB37ED2C8AEF34CB16432F7CF55AED59F243EF2F846C11B13GB28132GD681944C05D88BD0B917F4EED7E0E7941D634CE3F66A37CB9E057B16FB6D61EB090639A1DFED1BBB50EF73B54B0BAC7EA8F34357B2A6A7DB634C8DDF4B5C6450FEF78272F2B41A3F9D40F893D0E3906755164BA1DED9
	24983C5AD33917F4B8CD22BEAC9930164EA0FF1EAB110EAC4CA09DF90B6D732F01F469BC08E973286C1F3A4877574DB3521B22831175266AF3C1334365C2E88CCFDC519854EF755AA3FC2B47A29C2771A9366EC779754D2378B42A5F79FBBA60974D0B3EA6627C7F299259CF3F28447653A676ED0D887E55F241275EB3D897C419FA5B5B8322968FCA021BB55C1100599B1050792AF95DC80F36FE616ED02D37359B631276BD95F359FC464247BDB16A3D830465E1FCD0E3EA4CFA61770FF93D6F452CE7ECAF1719
	7F4B1D97EEAF43983E6001F0FB596CA6DD5E8CF94A83A4675FF98637E731E31075ECD7DD0F0F6BA56603339E889F6FDA766D650A81639D2D45019C4833FC0966330B73F08E198169E5G39G1B67DB0B37836C87B083B881FA819281D2G6683AC85D881309A2068C1EB713687495F9C3908E7B911FC24FAD895F2121A1DA8FD116D64D2DFECBB096FB31E4F0C689BB81F995DE7BC1F995F271F4FF05CC435DD3FF641D47D5D11BEDC7F71359AFD61C5FB130B59FA2292F8D277A06DB9EEF650BE246BC15235179D
	022687C54E507B9847351F734E7CAF74F13FB4B81BD8B848FAAFDDF5BDEEB393D92CCAF8B05C1626B1FC52036136343B13ECE9BA6495FD196479D6173EFF9970F1DF0E9EA354F30FCEA33A4729F5D35467FCDDDBDC27E630F960D2407F2400F0B399EEA56072G476FCBE7EEC5DD15BC3264918F71DF2D517A0E571D1F0F543D7F661A7CBECA7FF13F7BDF68A35F9B9AD3096EFD3C675A77BD6B1A76215E274A0B6CB71F4D0B6DB7DF48B3761B2B73867A4D97730C7D664BF9837DE6815B576BGFF5F2C15DB2F61
	CEE9380D2675AA6E2464E4FBBB9CDFEAFAC545570B58A161DFE73FAF5443F04DAA6E8D9D6E140A5BEE42B9928C59053F65214E5FCD38F753AE49B2CF7F151511BCB0E55278311751A8796CBA3A5F57F0C8373A2FE99F38D7172B1D1B95BC84B171A1EE2FE6F862FD293E07304C8E8715F1BDB450B7285F88F7067474AE5D7B5EABBA977A77F476DF57348F7D799B7A2DBC8DF730CB73DB9A6E27EC9E3E9078346CA10A23A73BA80EE6EB38C4FE1E17896B0CCCDA5F5A5F527DCEGD3AD0E5F563BA104A755D27DFA
	F99B79758350CE2F56D6471FC35FA7FA5D95D21B3BA2586D2E3037A995A2CCEFEAEA0E85E5D9DDB5535D51B32C2DB4E8E391C0F6A62DFF0EBAE87D131BC977C9BB3B74EBE0DC57C67C5D41856A6F8EAE683F0F49F5F2FDD77FB3041F301FB56067FD1E2E72A8F29937C03FE7FFE7972E3F40F0240BDE0FE217EBE5294303BBD26ABE6102AEDA6F25AC50E2DF5B60E2F655D557633179C9465F2C8561B17B9906DF31A0BCE6BBB45966C01E65E1227B04C35FFF866017BC9CBDE673F9736B6C9B21ECA83B86207C91
	EBF1B5C0830073911A8BAFB661FD76C8F36193DEAFBBF64793F8188A6B378ED4B95C54255F73D6FFA3258B4F7275DBC50D781F36878395078E9F339CBE21717769B6C1FB4F563DCBED575E17DCF7CD7B6036AFB94817CC4BA31C47C13EE4CB9E79703FB4C50E13EFB7450E137B1A0C63645F1B8646490F1A0C63E4DD13BECE725FCAF5F81046547F938AC16A47449EC5A84083BDF860B91DA22C65C7F270424F5F9421C437399A887805D787EEFA540E7A565329676ACACD0269083056453C5C912130875DE02F8F
	4CE088743D976AEE526A9EE1D1B03BFBDFCD7E7FCE4BB78985D0110F7608D000A4C3FDBFEFA79FC60B2D8885FA881C97AE8B98931FE2EDD32ADDCB95C52C414AA8C23159F58128173D9D97EA845A9F5C75397E2CAFF728A362F9E15D235CEE1B9F8D79C6EE2B59EC1FFA9C60539EA55B2C747DB512ED5636A90DEE3BAC03676B2A75B8F19F786085C58C1D77BE6B437683D4C64D64F92B58DEF9272E4E7F83D0CB878816C8A866159CGGF0D7GGD0CB818294G94G88G88G540171B416C8A866159CGGF0
	D7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4F9CGGGG
**end of data**/
}
}