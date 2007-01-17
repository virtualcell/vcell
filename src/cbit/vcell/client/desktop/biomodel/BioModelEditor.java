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
		}catch(cbit.vcell.client.task.UserCancelException e){
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
	D0CB838494G88G88GCEFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BD4DC5535290D163E15D531AF2D54266F71B42DD85146D75A26B52BC51B36D1D39BF9283145A7DD461A5726B6D55A05B5EDAFBF05A6A6C4C90D0A06275190038889B10490651704045F70898C9F89434C85A64C4C9DE66E4070093E3D4FB97B4EE53843A47425D9EB675E334FB97B6C33FFE71FF34FA0656D094ECACB4F132434ACC97CBB12A6C975DD1274F8E5698C614A77A564C83AFF7F8B30D3
	4A70E542339E6AE2AEA46464CAFB2733215C87650819041C17607DC5296B2302337092E21C548EC90A7941C729730FB358C263BC474A3FBE328D1E5B8186G074F58FDB8FEFA79EE01DFAC7084E9691294D908EDDEAB4F97384D50A69BF00DG83053C3D9F1E1C01ABAB4AC53B3EEBE224143BBD694FA39D9DCD0275226F9671AC107E1436FCBF0EF532FC1B68D386E587GCEBEC34A2BB8824FCA5368708756C13BDABCEE159D0EE22B43AE3B54E21B6C9DD595F77120DDF1AAB659D1D1D1E3D71A706D044D2EAA
	9E29609D247D3972ECC6CB12B6825DD8063B4903635960FD86E069BB04F7FB913F995E338FA464949C20B99E5FD7A966E01E7E0E14F8501939A3449C4B8F70398D9C884C314079F07A4BCC9F571BD06E4B20AE9EA091A095E0F7D1C2CEB9402B4C8EEEEAF9871E5503EA2D62F4AA2EEA373B46E17330F941FBC5855488DC1BECD5BCB6C9623AEEACAFB260E7BE607DFAAE399F17C94CA152FBEC5B2DD252B73F187E1C01CC12E22653CDC5DC96A3C5811970317FB5645891C5336DF27658DD12FE6C485F5D1F11E5
	B0F6EC527D994B841D953A31130B18FDB3DD2F0377B4E19F9C3F0F61773E9B40F39B7D863399AC17C1DD543BE49B666D44CBE36675D2EE693506F2083AF3C97A16F7B98FF945413C6CD611F6D5B19FF317464B8106D7F5F84E4BB863914BB1D05771DEC28E372F1B5B50E72512041CFC00B200C6G87C0844032923231A7DB5E3BC89BB3D9BCEA0D4F6E30499E09773D5974F7F84AFE55E3B96D722A9617D56E34F86C16C1076C15C41BB8934AE0F0D63F6FB25B37429827E4176C3128F6458554ADCED915BDDE5E
	66CE53CEEC6311D9ED075DA943GCE37C4F46FB63D88CF07452B1EF55BAC2A9CE8416B93CC2813F1CBA7D08482704E65323E9D65358A661F8830C113C3D3AB52EB10BDAE8C9F0D0AB26AF3F760CC2432D2147357D96CC805763925A463448E4479219CD9CAF496373E2023D3E737A27B964FA479581BDE0C4B4BCB49E61EB0134D54BDFF0BD4F47F978CED2660617BB362CB392D2CAF8D761DF799CD640B173F65B1DF229FDF8CAC976372B16E3DACE3E408A717EFCDA76ACABA88B68CD0F910E47C035682EE33B5
	962FCCD65A62818313ED8109F3BEDFD78B854FA86F2DG38A66DE0F4A4A9CB784EDBG7B34391532BE9DB05E8C40F20035GA9484721041CBAG8740D200D507C866B35EE85DBC32C20C39DD0C3BAB0C9C081F7FEF79F2DEEF1C456B8EA177E01E378B3EF70621F9B173D988F0DDFF2B8D791347E1192F135D324B86AFDED1372A8DF99571E7C8517A5C4970EE08D73590A03C9AFECD9B4E45222A9E7B20CF155BE5D5353B0683756B58D8DE5F20BB906AE82C875BD0BE4A8444AB2DFDD29B7A3AD5F139E4E6F4DA
	97D17F5FAC56488E5989CC57AABE174A719B5A51DFC765C9916BB636BFABE12CF278B0EE72FCE70585F56076581DE04A98FA2532B20CC3F7E8799A428630BF4C5B3221AE3F0CE24A441B94D376CE5DA4451FF3279B2D09D1DF3DBAFD260C7BF4E4D9A026F01E9E6AB0667D919673845E4BF9FDE5827B8625DC4207273B104E8A203F9AA0290CFC3877AC56D31C944EDB2BB89CDC141A0C6BBBF3D1A77218CF06658583F203A637164E97026ABA058CF9FDD7A74A50210B65CB8E238CC7D54AE7D3001FED00ABBA4C
	6568B84C65405B3D6723F66B213CD957AE77FDFD3BC735F6A5002FFFBF50EEE4D63BEB4729DD846023F56D62E735EB1820F64900CF55355BFD04370BBB026D7E2A35ABBF426BCD709C81D882900DCFC66F70A435134A0186C0E5B9EFBFD34E69659E45F6CFEB6D560A7A8D22FDAA406BDA3B3B2728DD5FD15ECE05E784C06CD1FD3B6B2629DD3CE837DE34CB916D387DBC359FED1A657667AD6396FF31F7827C3222620C49825641F37783C733C57B5C8A4A513BAA51366A21BCG602FA07B3F6FF94A51170E46C8
	0E4D09992176A11195C21E95817BDF93B31141ED77C7E69CB3856AB20F115DA6F46A7304EA2FD7315AD90AA2EC6ED3DDFA7F60382B3A7E864FA13B472B1E06G66A77BDC52057CE28EA350A227DB4AF2BD1C6BEE98578430719862FBDA72F43CC7GEE85C0A2C08623770CFA90639BF320E27EB0491EA1456314ED05A859E24B04DA4CE2E9F1B52BBE016F7BC98FCD9FE3FCF449933026BA6CD6B62D427D1D92655A573FD8C576F16B75D265A77D69C6FB1F089DA369A515DC1E664A00DC73929FCEAF8460F3B47B
	E80E4B21CDD2A54DF144E4B4473C63348F749E60B157BD994CA4273BED1C6896C21F0163C4775FDA0D68C6E9F4FFD30FF495D8E10C6946EBF4E3214F860DEECC1B915D7C8F086E1EEDD88FE191F25B5914B9DDF5026816C01F118F082E3F5308EE34C6B76AAD2C7748AE481A0D68967A09EE9C745974815948558CEFF759D53B45E11F128B7773F565CC03FEDD597BE1C20E99E086A072C36A9B6D44F5EAD041E0AD09BD7583FE5AD347FCC87EF74B6BFABB4936DCE9E8A7CB7EE7B8BD79C3EE9F59D581BB19819B
	09546C24FF1266E102B691G1C5F4F576B790D2F627C2642F3DBB5717B2349FF4C9F2EFE2D9A67AB56358E0FAC3300D4FA2EBA08D6E3E8E247187F70393CB1C57B463D40E31F4667E72732F563F05CFFCE615A250E48342C8A9FFC32DAAF5B154FADB1146D480DD169CB2B39CCD7D587E4ABBD7AF0FA5E23A45B27B4FE3655C0DEDBC37C74CC6B796172FEF9CCAF6F99E83B92A089E033566F53337A71B1FEB8CDE3E457A664D455D25B95E94837CD317AB0D0B6D9DC16E1B0AADE778B43BA1E3B169C8DBEA77057
	864E8716544EBDAB607B67FB58DA91DD4B774FD99A7E81060F5361B95F9FD5125D2D013A22BA5A732576209CA2219C8B309AA089E08BC07E89DAA3724C0724B0FB6A53AE586A3AAC0E76D949A66DED4DA887488FC579DEB3724172C309F2786B54E0B9141F18BD770E20B26F97B327DFF130BC037AF1DFD813053C982C51CD324BF7DA151DA2BF7DF4135ECEA201D69C40FA00CDGBCE6DC53C2EBF966C90A9997CE6A576CD5BBAE24EFB530EB044A131C3F3E13813BB62D7D23D8332F6F431CE399542D84D88730
	89A07794689040F40AF4E46EBE9ECEC74CFFBB14DA4055C38ED99B1C67EBB26D77ADCC970EB97D02F561BEB53B9F175F3F1BC8FE9127E87F79427842F8A81853AF1807F8C39E6EEFA59E56EBBC7CF801BCA40665A1375E0807C75A080742FA62E1EAE2E1BCC44C69974C4300A18F5F68A49E7C9A8F457E0571D0B927DFB08FAB6A0DFC33E5984F8B82E76343BEBBB8A6051813F830F642B50247F3B6E8477DF47B7160FDA41E9F2D817AC9756427D7B4605C50BFD36AA9677EA95B4F19CFA7643881A2CE531CFFB1
	07F7A308BE9D601F7B6B4B7D682FF100DF873091A073CCC2CEA1C09D4048996DAC3C7B18F4F17E4A975F395E4A7934CDAECCB71EB97DDE8FAAC71CB152CDF979BB525C6F966203C560BB0516DC10DD5DDDCFF9D57499127D35E7D0766B213C99A03B01E4DFB895CE7646F3FABFEC3F0206597D384EA24F214E4A212E8340819081908B3086E0D383696C196E1AF0BA2B264398509B3D9A84DA4E6F538B1C67F353617AE5B69A694E65A9B6505D3047629E29282805E5DB01A40F1DF6B0551D347B514138FF55759A
	79D7BE0CD352C87E55BAC6BA2EEFA49D7FF18CF53C884AD1GAB9AC947ADE98B1B7BC7E17B2D891ABBBF6FBE45765B49D0178240F1FB0FA1EEF7934C2109783A2F92F19DD09E51F0C98C0779B866089CE72D1C6F3C987B537BC8535C9CAE9C08B358F94748503DE7DDA2FD84EE53E93D214FA333FB0D4FA337779A1FC766756E64F820217F3AC275793D731FD76E6DB5BA2FFC0D4D752E3F6FE0343DDE594B82E31DC535083A47D5BC2F1DF3EE598C6B0410530B52FDEA0EE8F3C2D5EA8FEABE117352CBD248BCD0
	CAECC2BB31B7126D4FC0F92950672AA6FEB645ED7F76A6DAB7731B3578560C365F81E5874062E6321DAF36845B70B228DB519C5ACEF81C5AE541BD72EAE8178C108A105B82365BC27169FB3DE17374937EF97274583E5036704DBEE3DB78EE9F1EC5F9951F47AA876BBB3EEF37D697880218DC23EAF81B3BD8FF5562991667584BBA561F57056A7FD0DFA81B62FA7F02CF2F77C31AEFADB7856B4084F2343504568127F7FADCCF2FCC23777356B97BB82035A4AC3DBF4F22F7D8233721AD18DEB4500A8BCB6F3689
	BD3D77B5FAF11D417456G2D243074E43F0EDED329C46B794963E86F3827595CC2767E0BD3E86F05A658978318CDE46F1FE918AF0E39CD819E9643FB14A9B4CF3186F5FC5FDBED463163CD415F3C9B98FE1DA9781B77FFF551BEBE996A6ADAE9CDF8121D7F44C0F9D5AB79551B7D217D621DFEE33FA8698F6D97C77A437BC5D5FFE83FB8559F5EAFCC7D737B4517A775F6D22A59494F6A67DBDF573606127F568614E7F2AB17F31A064FE378CDBABC177FD74E503D18D4286BEB23F314F3D34417C9D5CC0734B9DF
	F7AE348E7E631C318EEEBC97DA87371E8B2F036F1C8B2D036F1F8B2F031F1CC37F98345B6C9E7195084F31E4DABF4700CF4D74527EA43E0DFCEA2B02F2CA05F2EEBB6CA35A4927BA476653D3D5FBA8BD1DE7F7BDBA5A037564E5780176E0BDED99A3BDB920EEDDBB69692AB47D9C8EEAF655FF944FD49D16C94527CAE27D749D2575332CA3A12791C085086860B9A4DF07EF1BC0D98D415E8E4C345DADDB253233587758B16A9B8D6D63G9281D2G724E92AFFFA8C519702FAB25020F8BC7833803E2AE57B452DC
	E620EF5CD99A7B0A232FC8A1F3009A8BA81D6B62DE56FF93744B6C045C37134EFA7CEF53D9CF6C572E1786B699FF1FC96E3810DE56A93EA3F4864EFA4A1C8F27F31D46B7856BF4A9340B698C9D8F396EF63430BBA01DDCF74FEB785799BED907973E477292ACE7F6413E370BE25FAD6C9CAB894A1BB55C6F99AE538C72B6D3BCFC4D925A9777DA0CFD719D0BF14ED8E219BFE7AC37040AE31C3F5BCD44F30119F87E2B09F8AE53F03FEF25F6D59A6EF09B356B50F04F34D13B818D77E1BB35D3B55CBD1D54EE1106
	5B51C56D161A49CF9B0E13CE2381978730469C50E7923CEFB64F56AF57DDCBBD6AA833BB78FC370761F7F7871F6F7E72941D6F96C1DDE4B771F493BB73C801F2F60F3646B1DCBE14CBB45C31C64455757073CD0E3B220B76C7669E1A4FA9B3FE6BDF2C15DBD9B9CEABBF54F52977E81056453665E3BCF949E3E4DCC2DBF1BF73837D9975469E14C74EA44EF53DA633DF5513FC0AFAE97EBB9BA40D4FD7F46FB71C399C770BE64BE555BFE10CCF7512BD1BFB395E9C3D817DAD0277253DC6767C89DB47E2FA036D79
	DF98FED5EF30BD6FD4481E57C1DDD99F495CB9C6B20F6AA3193BBD81B9CF6A5E53AF4B1D2E5972F875320EA16275D1DA37D6431C3774513AD5BA0067D65A3A059F6CAAAACEF96C369A4BF0A3DB01F9DE7198BB7BE0F710BF223C62458A24690072E200E59F51BE56F20C5A4681EE53C7083B7A187E5BAD7A52ED197CAC17575FB12B9E6375FEC9DF7F542CFADC872A1F55573FD5292F4F5275676B6BBDEF517A3A651E951263DB0BC35E43AE6B976B2BF8C6EE78D25A6254298C2E2BEB9B97F60F782B175C0F5BF6
	CD934AB832BFF8CF524870517D41FB1244C65A132C003A3CF3740D6E19C14C017CD0DE8A908B309AE08300B4C0EB723D1692A94C5E3FC3F1B742226D50AD335C279EEC2235ACEF007C2C2099F105D0AE9FA0BF5B5502FC54C1398FC085D882908330DAEB33173559A66E97A58F707BC351625E0F08054D8386B2C3FA740EB4E91C0B1579EADD5BA59ABF5FB79C6BB95D7BD6A9F0DF70E247FABE481EF94E7984132353A503826C56DC8B7B9E4B722707510CAD142FE61BF09DD1A1C96A3060EF9DB65A311A375F40
	F2171FGC4C11957EF9D8D543F427213C7D0C6DAFD31A3D03F1765AAF7812C546A071C017A941637BC8630C62B7F228B6B395E87593EFEBD60D3ACE48B1FF5A28E6FE0E58F92AEDBC1DC91142BB45CE75C941BED9A2EF6GF1F1D0DEBBC8BAB94B564DE4A8CBD6428D7035341C1F29F15C6327AEA636A20D10FEB8983A0E0FA155DF4C98735946FC398557F7FC830EB5EE775A714E9E5743038DF4D6DEE8A53B48E1B91897D2DD37DB3671F6850D542EDCEB5746F034BB6933E23BAFF5954C43C73D10916F3F64G
	7E264678039A6CE5D5160A3BB06F61E77FD718034F7ED1C74B0166CA0D27BDACB7F2799C8E713B92D97F5B19126946F4ECE7943357895E1245F34B2E6B52781C7EDCBF5F1CF2ED3CBD373328DE325B3DB6323D4E893251F28D374ECFB65A2761E2FCE42331B6323DAB19FD47C3B9D143C5B2DC0A0D5F79633817140B311FC4DBE8BB0C1F27CE5C913928B196EA23DC560B47C856F972DCFEC856A2FE4F3933928EF83F5B96586F57175C0F2F330F319C27D08EBE6BF802614B6460330E17ABA84F2A023A65B269F8
	685842FEC7F447F3143744FD6F6B524A2B6AC25E81499E6272AD246723DFCA43A7DF7F7F20207F1921CE850882D881309AA0E908567F3F8FA20FF842EF3363D7C80BF5045D5824453F53AECF8450F8404267D65F04FE3BF908665A464AF8B7CF1CEBB79A595E6621CB7F2695DA3FCF990EF1A9FB3679004BA7539A7A4CE22B55784CE22755784C62A56B7C5F3176D8C35F3BFF5B2A3FF74FE3FC964B35FE8830ED186269116659B9424DE312C471FB71AE3423E6F9C23B2B4471CD6C77A5350AFB12AAB87E604758
	7ECC3B8C7CD8D445C3E7E4699348E78B3FA39AC46B5367D0F6F56C1E27D6A56A5E1B413AB6F6D7B3D0476FF9FAE65DCB4D9F6677BC37EB3C7F36851F7BDC7B040FC4CF685709723FDDE1F8371F71B2ACF27B6140BA41E97C2CD7CFA377BAC7FADA889A11226F326140791B38EBECE2FCD97789BECB5BD8D9217231F6D636D1F9EB97ABFB28BCF99CF3C237C79E12BD9E5956EE1F12B57DFE1E65D46BE04CA4GFEA67A9DA72D2959A330778268G98996177BE451A2A4CBDCBFDDF21FE0B20ED94C0BCC02268D7E0
	E7F7035DF57378832D439982B6C1DF897A6D9265BCFBA0AE0A7B1383E8BFCE3B4B6E74B9594C783CBCAC379B01762A1DF87EF98D5EBB1EF38D2414E8E5CEE1CCBBBFAB77505D775546FC2201593F47A78BFFE81CDD7EF6975AC93362D1C7A40A3793B2455B7C1BEF107277D4256F8CE18751F62E7F3576009D30A75B83FF4FCC5FF2257334DF3E2687FD2B9C4AA600117334B75B4FFE2B3668BC456107DCEF403353F2DE6C4078FD8A96058109F76F5AA1EC3806E91E0B4E200EEBE51487594B9DAC17D98AB4234F
	133DFCB2C1FADFFB1E6BFD83BCD34F73BB89DC6F0F7BC96F3923F36333DE276C4EAE34A99F8D683DF1B420F74E47FDACAFB2835EB6CA7A3D638DF62E5EE89944F3F50EBB70AA7E06E06E8512D291936ED474B1A1F214739CB7CAE78E31E3A46FB523346E7C2682454F5FD3F46FFBAAAE7F19CE79E59D035BCD250AB2119C89B979GD5G9DGA38E3223D79534A3FE23A8F8ADBFE19B46EFD6788BD515561C3FA8215737ACF7785F35EEF763EFBA8695D5D51CC8DF3B5361C619280A1BA1B96EE5B77A1B47BEBC22
	7226346E317D1FC39EE2E8EE9B37325F6369BC1C5D2B60F5794DAC06DA7609B357EB54D967D9016FB05A4A45E5574D6CF2B148AADA135766318373781D492338E50FBA49316CD7D5C28F9D6312467F7B1E37C251G291F8116DA864F4BD6D81141FDA53EAEBFB4A1C9644B9FBB77C86C77A6855A7D79BABBE88EFF30DBF838242860D079617DA257FC44C9E7BD71B007D4871D69E5953CE5A003E27EA3050A0A932AF5C43655081FAC08F8354FC5E7F6A5CE0AD7E7DDC8FF844A0BG22G446F8F944AF1D7GEE23
	13467DD5712C7B507CAC110F430F9245D8DFD3E82C82970D1548E81A216C8608G58436481EB1BC47FB6FFE80463B26C1C44920F613F5C260F61F58F56253FA9050861AE9E63B63B82B1FC6FD8C186277940D4C0AF5FBF95C6AF87CB4B768BFB38FD3AG1E05CAC2CE9D408840A20025G31G71G6B953239E9CF61BCB64705477C54E3313BD49EDBFEE08E5ECB24A801D81D6D8E755DB62B97E51C6F8E5E4BECE778C2F770DE665A9E6D6FCEC0DD141BF8FE570B6B0DD72CC1DCD6568B81D9B5CC0411D5E949C1
	32619BA7E80CADC03FEC0C62796FA648C69CE3E4A32F315F69AC0772AA00C42D6D6EC90A7D1B460C6CA63BAA345DDC75312479E131941843932E0B1903782D5ED0085F6A31950E7378B433F9545FDE8F2DCF374D7A7E5E87F837E77EBD9117FF1D9ABACEB7286163F4074AFE0F877135E352AD6E99F42B28D3E5E848ABEB6BC13F1A2B61CEFBCD0A570E41D75479109FED7F42432168A77BF0DD52FECF45C2B64FD8783C9FF3065B8BAF8E12035863056D97FC7F1877ABF2ADEC6FFF970BFF511EE0FFC9E078D8CF
	303F743A4816D7C2DD3E1774ECD396F6E661D9E03F1BAF399F1FD73D1B5D3375864F37156117F803677BF3ED9D08023AADDE4AE77353B0079CD093F2E6G2281D6822C8558G108A304D873E8BD08EE0823081788116GAC83D881305AC771664B1378ADA7D40C949F2B82C1D273139FCF0776131FCE07771387260D7319246939794CAF270D7319472775798C47ED92F4BD5A85D371F7C726F17F4777EA748BAFBA1BFC006DA75603CCB679684CB13F074EA1F34749563676CA1A9D64695E3F52F379F35EEF7F93
	4638E54EEFD2AE2E5F6D8B6C17F2497D384FDC43562A3271E0DF3A0E612B4603FD69CDBF79D2A3542D5074795A2C5FD864CDA46494CE4C3FC670FEFFB10C8115222FC9BCEDBADA5C265630FD149F703F4FA45CCF99AE92F0E90063772563DFC4DB353B9CF617DC4B7FEA0DB6764FDC813BDB2FFB3738AE3FFDCC7D9346E8F687665425FBCF3E28B376FFEC6C27AF6B9862BED5DA6838F9A4ADFC5C2CCAB30E1BA75366464D06B46338591AB6B7EEF632BF7AB68C715E2C356B55F05D9AEE0051C4DC175D464EF6B8
	FE08710578FA9987A43C0371B541F0E6019B53613A85EEBC8DF3A41872887EAD8F113F23CBC8C3F6070317BFE1ED6CAEC819B4F9ACE6BC5A5D969D5F1F55F04837B857B44FBD2B4B6C26B31BD8703F5542DFD361097725767A314D219EEA136B1F9B9B443D85FF40CE23F56F7B7D175FE6EBAE6B98BCC615CDD25CCA53F0C7A735382561F671738510530C1F5651A96D1BF304067BA64345C1B951CF7BDB4B5B3A3F9340CC0B63737D4F8770E4DAA22E97F5D3DC5F88F48AA60956709B015FA7AA4EE27B203358B7
	6EAC368C2A45B224B795954DBE07C36C1A696E281D51B2838DB7C044A46DFF3AFBE87F93BDC977C94F0F6A7740382F8D79F787A745D978646CF3CCEE13C56E595F90EA91AD3E77DD63AE18C7AF15D37AB37B3B5D387F824731CFA9AE556228F65807DDF8A2C5F7965C345FB3CD515AF730FBE1EBF655A57763EB7343CC3E0B2602576CDFB17C122960B5DB51F49B85F5DBB43E7BFB74638FCCA7642853732F59BCEFFEBB937DB8825AAE87D883108C108230ED06F2617CF43C4F9EAA97EED39476599D3F40C3AA2C
	BFBA90FA38552DBF73965FAD5C78ADDFFFD4540E7F537120373876C4F34709B6CD3E0FE248926F1BF46F3E6E4B9FCBBEF7D947603E6F6B21D8F2C39A6126FAA8166CCC23987EDEE668F5727D4C706B640719466B64094C396B6419CC63F5521429DFA7795FCA3D60C59EE3FFABE501561B6589D54A448FFA70406FF42A74AC7FA487AF7C7B1BAAED539D2EFA3D9E690539079E551FFB684158F3F5F969D27AE96959919665CECBD99398867D108425FD8A46BE88FDF7E8FDCF33D5B0F97CD0C54623B7E424CB1950
	114F76341409ACC3FF8F27138133C5AAD2269E3CE35247922E098FB05AD49A52CA59A1FB30B62A3455B4B2895CBB1461C97CG40E3637ECF745F7AD22F34067CDE38698277DB6963C0EC643E7AA7F6FEDC89F87347641BE7FC6F06724D6AC1355DE9F1B8A0720DD63BECF88EDCB3294A016F3DFFE567A9DEEAA3AAF95D336C77DD67F5FD7E8FD0CB87883D3288FEFC9CGGF0D7GGD0CB818294G94G88G88GCEFBB0B63D3288FEFC9CGGF0D7GG8CGGGGGGGGGGGGGGGGGE2F5E9EC
	E4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG369CGGGG
**end of data**/
}
}