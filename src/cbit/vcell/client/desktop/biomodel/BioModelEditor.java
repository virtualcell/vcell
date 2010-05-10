package cbit.vcell.client.desktop.biomodel;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.Versionable;
import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelTreePanel;
import cbit.vcell.graph.CartoonEditorPanelFixed;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
public class BioModelEditor extends JPanel {
	private BioModelTreePanel ivjBioModelTreePanel1 = null;
	private CartoonEditorPanelFixed ivjCartoonEditorPanel1 = null;
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
	private JInternalFrameEnhanced ivjjInternalFrameApplication = null;
	private JMenuBar ivjjInternalFrameApplicationJMenuBar = null;
	private BioModel fieldBioModel = new BioModel(null);
	private DocumentManager fieldDocumentManager = null;
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
public void bioModelTreePanel1_ActionPerformed(java.awt.event.ActionEvent e) {
	String actionCommand = e.getActionCommand();
	if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION)) {
		openApplication();
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_MATH)) {
		openApplication(ApplicationEditor.TAB_IDX_VIEW_MATH);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_GEOMETRY)) {
		openApplication(ApplicationEditor.TAB_IDX_SPPR);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_SIMULATION)) {
		openApplication(ApplicationEditor.TAB_IDX_SIMULATION);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_DETSTOCH)) {
		openApplication();
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_NON_STOCHASTIC_APPLICATION)) {
		newApplication(e);
	}  else if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION)) {
		newApplication(e);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_DELETE_APPLICATION)) {
		deleteApplication();
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_RENAME_APPLICATION)) {
		renameApplication();
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_COPY_APPLICATION)) {
		copyApplication(e);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_COPY_TO_STOCHASTIC_APPLICATION)) {
		copyApplication(e);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_COPY_TO_NON_STOCHASTIC_APPLICATION)) {
		copyApplication(e);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_NEW_APPLICATION)) {
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
		this.copyApplication(arg1);
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
private void copyApplication(ActionEvent evt) {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	SimulationContext selectedParent = getBioModelTreePanel1().getSelectedApplicationParent();
	if (selection == null && selectedParent == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}	
	if(!(selection instanceof SimulationContext)) {
		PopupGenerator.showWarningDialog(this, "This will copy the whole application.");
		selection = selectedParent;
	}
	try {
		String newApplicationName = null;
		
		if (selection instanceof SimulationContext) {
			String actionCommand = evt.getActionCommand();
			if(actionCommand.equals(GuiConstants.ACTIONCMD_COPY_APPLICATION))
			{
				//check validity if selected application is a stochastic application
				if(((SimulationContext)selection).isStoch())
				{
					String message = getBioModel().getModel().isValidForStochApp();
					if(!message.equals(""))
					{
						throw new Exception(message);
					}
				}
				//get valid application name
				try{
					newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the application copy:");
				}catch(UserCancelException e){
					return;
				}
				if (newApplicationName != null) {
					if (newApplicationName.equals("")) {
						PopupGenerator.showErrorDialog(this, "Blank name not allowed");
					} else {
						SimulationContext newSimulationContext = getBioModel().copySimulationContext((SimulationContext)selection, newApplicationName, ((SimulationContext)selection).isStoch());
						getBioModelWindowManager().showApplicationFrame(newSimulationContext);
					}
				}
			}
			else if(actionCommand.equals(GuiConstants.ACTIONCMD_COPY_TO_STOCHASTIC_APPLICATION))
			{
				//check validity if copy to stochastic application
				String message = getBioModel().getModel().isValidForStochApp();
				if(!message.equals(""))
				{
					throw new Exception(message);
				}
				//get valid application name
				try{
					newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the application copy:");
				}catch(UserCancelException e){
					return;
				}
				if (newApplicationName != null) {
					if (newApplicationName.equals("")) {
						PopupGenerator.showErrorDialog(this, "Blank name not allowed");
					} else {
						SimulationContext newSimulationContext = getBioModel().copySimulationContext((SimulationContext)selection, newApplicationName, true);
						getBioModelWindowManager().showApplicationFrame(newSimulationContext);
					}
				}
			}
			else if (actionCommand.equals(GuiConstants.ACTIONCMD_COPY_TO_NON_STOCHASTIC_APPLICATION))
			{
				//get valid application name
				try{
					newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the application copy:");
				}catch(UserCancelException e){
					return;
				}
				if (newApplicationName != null) {
					if (newApplicationName.equals("")) {
						PopupGenerator.showErrorDialog(this, "Blank name not allowed");
					} else {
						SimulationContext newSimulationContext = getBioModel().copySimulationContext((SimulationContext)selection, newApplicationName, false);
						getBioModelWindowManager().showApplicationFrame(newSimulationContext);
					}
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
	SimulationContext selectedParent = getBioModelTreePanel1().getSelectedApplicationParent();
	if (selection == null && selectedParent == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}	
	if(!(selection instanceof SimulationContext)) {
		PopupGenerator.showWarningDialog(this, "This will delete the whole application.");
		selection = selectedParent;
	}
	try {
		if (selection instanceof SimulationContext) {
			//
			// BioModel enforces that there be no orphaned Simulations in BioModel.vetoableChange(...)
			// Check for no Simulations in SimualtionContext that is to be removed
			// otherwise a nonsense error message will be generated by BioModel
			//
			boolean bHasSims = false;
			Simulation[] simulations = ((SimulationContext)selection).getSimulations();
			if(simulations != null && simulations.length != 0){
				bHasSims = true;
			}
	
			if (bHasSims) {
				String confirm = PopupGenerator.showWarningDialog(getBioModelWindowManager(), getBioModelWindowManager().getUserPreferences(), UserMessage.warn_DeleteSelectedAppWithSims, ((SimulationContext)selection).getName());
				if (!confirm.equals(UserMessage.OPTION_CANCEL)) {
					for (Simulation simulation : simulations) {
						getBioModel().removeSimulation(simulation);
					}
					getBioModel().removeSimulationContext((SimulationContext)selection);
				}
			} else {
				String confirm = PopupGenerator.showWarningDialog(getBioModelWindowManager(), getBioModelWindowManager().getUserPreferences(), UserMessage.warn_DeleteSelectedApp, ((SimulationContext)selection).getName());		
				if (!confirm.equals(UserMessage.OPTION_CANCEL)) {
					getBioModel().removeSimulationContext((SimulationContext)selection);
				}
			}
		}
	} catch (Exception exc) {
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
			ivjApplicationMenu.setIcon(new Icon() {
				public void paintIcon(Component c, Graphics g, int x, int y) {
				    Polygon p = new Polygon();
				    p.addPoint(x, y);
				    p.addPoint(x+getIconWidth(), y);
				    p.addPoint(x+getIconWidth()/2, y+getIconHeight());
				    g.fillPolygon(p);

				}
				public int getIconWidth() { return 8; }
				public int getIconHeight() { return 4; }
			    } );
			ivjApplicationMenu.setText("Applications");
			ivjApplicationMenu.setHorizontalTextPosition(SwingConstants.LEFT);
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
public BioModel getBioModel() {
	return fieldBioModel;
}


/**
 * Return the BioModelTreePanel1 property value.
 * @return cbit.vcell.desktop.BioModelTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelTreePanel getBioModelTreePanel1() {
	if (ivjBioModelTreePanel1 == null) {
		try {
			ivjBioModelTreePanel1 = new BioModelTreePanel();
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
private CartoonEditorPanelFixed getCartoonEditorPanel1() {
	if (ivjCartoonEditorPanel1 == null) {
		try {
			ivjCartoonEditorPanel1 = new CartoonEditorPanelFixed();
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
			ivjCopyMenuItem.setActionCommand(GuiConstants.ACTIONCMD_COPY_APPLICATION);
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
			ivjDeleteMenuItem.setActionCommand(GuiConstants.ACTIONCMD_DELETE_APPLICATION);
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
public DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the jInternalFrameApplication property value.
 * @return cbit.gui.JInternalFrameEnhanced
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public JInternalFrameEnhanced getjInternalFrameApplication() {
	if (ivjjInternalFrameApplication == null) {
		try {
			ivjjInternalFrameApplication = new JInternalFrameEnhanced();
			ivjjInternalFrameApplication.setName("jInternalFrameApplication");
			ivjjInternalFrameApplication.setVisible(true);
			ivjjInternalFrameApplication.setStripped(true);
			ivjjInternalFrameApplication.setJMenuBar(getjInternalFrameApplicationJMenuBar());
			getjInternalFrameApplication().add(getJInternalFrameEnhancedContentPane());
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
			ivjJLabel2.setText("Physiology");
			ivjJLabel2.setFont(ivjJLabel2.getFont().deriveFont(Font.BOLD));
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
			ivjJMenuItemNonStochApp.setText("Deterministic Application");
			ivjJMenuItemNonStochApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_NON_STOCHASTIC_APPLICATION);
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
			ivjJMenuItemStochApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION);
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
			ivjOpenAppMenuItem.setActionCommand(GuiConstants.ACTIONCMD_OPEN_APPLICATION);
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
			ivjRenameMenuItem.setActionCommand(GuiConstants.ACTIONCMD_RENAME_APPLICATION);
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
	boolean isStoch = false;
	if (event.getActionCommand().equals(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION))
	{
		isStoch = true;
		String message = getBioModel().getModel().isValidForStochApp();
		if(!message.equals(""))
		{
			PopupGenerator.showErrorDialog(this, "Error creating stochastic application:\n" + message);
			return;
		}
	}
	
	try {
		String newApplicationName = null;
		try{
			newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the new application:");
		}catch(UserCancelException e){
			return;
		}
		if (newApplicationName != null) {
			if (newApplicationName.equals("")) {
				PopupGenerator.showErrorDialog(this, "Blank name not allowed");
			} else {
				final String finalNewAppName = newApplicationName; 
				final boolean finalIsStoch = isStoch;
				AsynchClientTask task0 = new AsynchClientTask("create application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = getBioModel().addNewSimulationContext(finalNewAppName, finalIsStoch);
						hashTable.put("newSimulationContext", newSimulationContext);
					}
				};
				AsynchClientTask task1 = new AsynchClientTask("process geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
						newSimulationContext.getGeometry().precomputeAll();
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("show application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
						getBioModelWindowManager().showApplicationFrame(newSimulationContext);
					}
				};
				ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task0, task1, task2});
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to create new Application!\n"+exc.getMessage());
	}
}

private void openApplication() {
	openApplication(-1);
}

/**
 * Comment
 */
private void openApplication(int tabIndex) {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	SimulationContext selectedParent = getBioModelTreePanel1().getSelectedApplicationParent();
	if (selection == null && selectedParent == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}	
	if(!(selection instanceof SimulationContext)) {
//		PopupGenerator.showWarningDialog(this, "This will open the whole application.");
		selection = selectedParent;
	}
	if (selection instanceof SimulationContext) {
		getBioModelWindowManager().showApplicationFrame((SimulationContext)selection, tabIndex);
	}
}

/**
 * Comment
 */
private void renameApplication() {
	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
	SimulationContext selectedParent = getBioModelTreePanel1().getSelectedApplicationParent();
	if (selection == null && selectedParent == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}	
	if(!(selection instanceof SimulationContext)) {
		PopupGenerator.showWarningDialog(this, "This will rename the application.");
		selection = selectedParent;
	}	
	try {
		if (selection instanceof SimulationContext) {
			String oldApplicationName = selection.getName();
			String newApplicationName = null;
			try{
				newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "New name for the application:");
			}catch(UserCancelException e){
				return;
			}
			if (newApplicationName != null) {
				if (newApplicationName.equals(oldApplicationName)) {
					PopupGenerator.showErrorDialog(this, "New name provided is the same with the existing name");
					return;
				} else {
					((SimulationContext)selection).setName(newApplicationName);
				}
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, exc.getMessage());
	}	
}


/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setBioModel(BioModel bioModel) {
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
public void setDocumentManager(DocumentManager documentManager) {
	DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
}

}