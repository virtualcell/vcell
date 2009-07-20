package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.vcell.server.*;
import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import java.awt.event.*;
import javax.swing.*;

import org.vcell.util.Matchable;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.Versionable;

/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class BioModelTreePanel extends JPanel {
	private JScrollPane ivjJScrollPane1 = null;
	private BioModelCellRenderer ivjBioModelCellRendererFactory = null;
	private org.vcell.util.gui.JTreeFancy ivjJTree2 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private BioModel ivjBioModel = null;
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemOpen = null;
	protected transient java.awt.event.ActionListener aActionListener = null;
	private JLabel ivjJLabel1 = null;
	private JMenu ivjJMenuNew = null;
	private JMenuItem newStochApp = null;
	private JMenuItem newNonStochApp = null;
	private JSeparator ivjJSeparator1 = null;
	private JSeparator ivjJSeparator2 = null;
	private VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	private JPopupMenu ivjAppPopupMenu = null;
	private JMenuItem ivjJMenuItemRename = null;
	private BioModelTreeModel ivjBioModelTreeModel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JMenuItem ivjJMenuItemAnnotation = null;
	private JMenuItem ivjJMenuItemEdit = null;
	private JPopupMenu ivjSimPopupMenu = null;
	private JMenuItem ivjJMenuItemCopy = null;
	private JMenu ivjJMenuCopy = null;
	private JMenuItem copyStochApp = null;
	private JMenuItem copyNonStochApp = null;
	private JMenuItem ivjBioModelMenuItem = null;
	private JPopupMenu ivjBioModelPopupMenu = null;
	private JMenuItem ivjEditAnnotationMenuItem = null;
	private JPopupMenu ivjEditAnnotationPopupMenu = null;
	private JMenuItem ivjNewApplnMenuItem = null;
	private org.vcell.util.document.Versionable fieldSelectedVersionable = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemDelete()) 
				connEtoC4(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuNew()) 
				connEtoC5(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemRename()) 
				connEtoC6(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuCopy()) 
				connEtoC11(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemCopy()) 
				connEtoC11(e);
			if (e.getSource() == BioModelTreePanel.this.getEditAnnotationMenuItem()) 
				connEtoC12(e);
			if (e.getSource() == BioModelTreePanel.this.getNewApplnMenuItem()) 
				connEtoC13(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemAnnotation()) 
				connEtoC14(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemEdit()) 
				connEtoC8(e);
			if (e.getSource() == BioModelTreePanel.this.getBioModelMenuItem()) 
				connEtoC9(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemOpen()) 
				connEtoC10(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemCreateStochApp()) 
				connEtoC3(e);
			if (e.getSource() == BioModelTreePanel.this.newStochApp)
				BioModelTreePanel.this.refireActionPerformed(e);
			if (e.getSource() == BioModelTreePanel.this.newNonStochApp)
				BioModelTreePanel.this.refireActionPerformed(e);
			if (e.getSource() == BioModelTreePanel.this.copyStochApp)
				BioModelTreePanel.this.refireActionPerformed(e);
			if (e.getSource() == BioModelTreePanel.this.copyNonStochApp)
				BioModelTreePanel.this.refireActionPerformed(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == BioModelTreePanel.this.getJTree2()) 
				connEtoC2(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelTreePanel.this.getJTree2() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP1SetTarget();
		};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == BioModelTreePanel.this.getselectionModel1()) 
				connEtoC1();
		};
	};
	private JMenuItem ivjJMenuItemCreateStochApp = null;

/**
 * BioModelTreePanel constructor comment.
 */
public BioModelTreePanel() {
	super();
	initialize();
}

/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public BioModelTreePanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public BioModelTreePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * BioModelTreePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public BioModelTreePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Comment
 */
private void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2 && this.getSelectedVersionable() instanceof SimulationContext) {
		getJMenuItemOpen().doClick();
		return;
	}
	DefaultMutableTreeNode currentTreeSelection = (DefaultMutableTreeNode)getJTree2().getLastSelectedPathComponent();
	Object selectedObject = null;
	if(currentTreeSelection != null){
		selectedObject = currentTreeSelection.getUserObject();	
	}
	if (SwingUtilities.isRightMouseButton(mouseEvent) && selectedObject instanceof BioModel) {
		getBioModelPopupMenu().show(getJTree2(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}	
	if (SwingUtilities.isRightMouseButton(mouseEvent) && this.getSelectedVersionable() instanceof SimulationContext) {
		getAppPopupMenu().show(getJTree2(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
	if (SwingUtilities.isRightMouseButton(mouseEvent) && this.getSelectedVersionable() instanceof Simulation) {
		getSimPopupMenu().show(getJTree2(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
	if (SwingUtilities.isRightMouseButton(mouseEvent) && selectedObject instanceof Annotation) {
		getEditAnnotationPopupMenu().show(getJTree2(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
}


public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}


/**
 * connEtoC1:  (selectionModel1.treeSelection. --> BioModelTreePanel.TreeSelectionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.treeSelection();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (JMenuItemOpen.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.jMenuItemOpen_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemOpen_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (JMenuItemCopy.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
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
 * connEtoC12:  (EditAnnotationMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.editAnnotation(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.editAnnotation(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (NewApplnMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
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
 * connEtoC14:  (JMenuItemAnnotation.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.editAnnotation(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.editAnnotation(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JTree2.mouse.mouseClicked(java.awt.event.MouseEvent) --> BioModelTreePanel.showPopupMenu(Ljava.awt.event.MouseEvent;Ljavax.swing.JPopupMenu;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionsOnClick(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (JMenuItemCreateStochApp.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC4:  (JMenuItemDelete.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JMenuItem1.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * connEtoC6:  (JMenuItem1.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * connEtoC7:  (BioModelTreePanel.initialize() --> BioModelTreePanel.enableToolTips(Ljavax.swing.JTree;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.enableToolTips(getJTree2());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (JMenuItemEdit.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.editAnnotation(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.editAnnotation(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (BioModelMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelTreePanel.editAnnotation(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.editAnnotation(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM11:  (BioModelTreePanel.initialize() --> JTree2.putClientProperty(Ljava.lang.Object;Ljava.lang.Object;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11() {
	try {
		// user code begin {1}
		// user code end
		getJTree2().putClientProperty("JTree.lineStyle", "Angled");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (BioModelTreePanel.initialize() --> JTree2.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	cbit.vcell.desktop.BioModelCellRenderer localValue = null;
	try {
		// user code begin {1}
		// user code end
		getJTree2().setCellRenderer(localValue = new cbit.vcell.desktop.BioModelCellRenderer(null));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setBioModelCellRendererFactory(localValue);
}

/**
 * connEtoM8:  (BioModel.this --> BioModelTreeBuilder.bioModel)
 * @param value cbit.vcell.biomodel.BioModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.biomodel.BioModel value) {
	try {
		// user code begin {1}
		// user code end
		getBioModelTreeModel1().setBioModel(getBioModel());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (JTree2.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getselectionModel1() != null)) {
				getJTree2().setSelectionModel(getselectionModel1());
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
 * connPtoP1SetTarget:  (JTree2.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setselectionModel1(getJTree2().getSelectionModel());
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
 * connPtoP3SetTarget:  (BioModelTreeModel1.this <--> JTree2.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		getJTree2().setModel(getBioModelTreeModel1());
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
private void editAnnotation(java.awt.event.ActionEvent actionEvent) {
	
	try {
		DefaultMutableTreeNode currentTreeSelection = (DefaultMutableTreeNode)getJTree2().getLastSelectedPathComponent();
		Object selectedObject = null;
		Object parentObject = null;
		
		if(currentTreeSelection != null){
			selectedObject = currentTreeSelection.getUserObject();	
		}

		if(selectedObject instanceof BioModel || selectedObject instanceof SimulationContext || selectedObject instanceof Simulation){
			parentObject = selectedObject;
		}else if (selectedObject instanceof Annotation){
			parentObject = ((DefaultMutableTreeNode)currentTreeSelection.getParent()).getUserObject();
		}
			
		if(parentObject instanceof BioModel){
			String oldAnnotation = ((BioModel)parentObject).getDescription();
			try{
				String newAnnotation = org.vcell.util.gui.DialogUtils.showAnnotationDialog(this, oldAnnotation);
				if (org.vcell.util.BeanUtils.triggersPropertyChangeEvent(oldAnnotation, newAnnotation)) {
					((BioModel)parentObject).setDescription(newAnnotation);
				}
			}catch(org.vcell.util.gui.UtilCancelException e){
				//Do Nothing
			}
		}else if(parentObject instanceof SimulationContext){
			String oldAnnotation = ((SimulationContext)parentObject).getDescription();
			try{
				String newAnnotation = org.vcell.util.gui.DialogUtils.showAnnotationDialog(this, oldAnnotation);
				if (org.vcell.util.BeanUtils.triggersPropertyChangeEvent(oldAnnotation, newAnnotation)) {
					((SimulationContext)parentObject).setDescription(newAnnotation);
				}
			}catch(org.vcell.util.gui.UtilCancelException e){
				//Do Nothing
			}
		}else if(parentObject instanceof Simulation){
			String oldAnnotation = ((Simulation)parentObject).getDescription();
			try{
				String newAnnotation = org.vcell.util.gui.DialogUtils.showAnnotationDialog(this, oldAnnotation);
				if (org.vcell.util.BeanUtils.triggersPropertyChangeEvent(oldAnnotation, newAnnotation)) {
					((Simulation)parentObject).setDescription(newAnnotation);
				}
			}catch(org.vcell.util.gui.UtilCancelException e){
				//Do Nothing
			}
		}else{
			throw new Exception("Enexpected Edit Annotation Target="+(parentObject != null?parentObject.getClass().getName():"null"));
		}
	}catch (Throwable exc) {
		exc.printStackTrace(System.out);
		org.vcell.util.gui.DialogUtils.showErrorDialog(this, "Failed to edit annotation!\n"+exc.getMessage());
	}
}


/**
 * Comment
 */
public void enableToolTips(JTree tree) {
	ToolTipManager.sharedInstance().registerComponent(tree);
}


/**
 * Method to support listener events.
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getAppPopupMenu() {
	if (ivjAppPopupMenu == null) {
		try {
			ivjAppPopupMenu = new javax.swing.JPopupMenu();
			ivjAppPopupMenu.setName("AppPopupMenu");
			ivjAppPopupMenu.add(getJLabel1());
			ivjAppPopupMenu.add(getJSeparator1());
			ivjAppPopupMenu.add(getJMenuItemOpen());
			ivjAppPopupMenu.add(getJMenuItemDelete());
			ivjAppPopupMenu.add(getJMenuItemRename());
			ivjAppPopupMenu.add(getJMenuItemAnnotation());
			ivjAppPopupMenu.add(this.getJMenuItemCopy());
			ivjAppPopupMenu.add(getJSeparator2());
			ivjAppPopupMenu.add(getJMenuCopy());
			ivjAppPopupMenu.add(getJMenuNew());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAppPopupMenu;
}

/**
 * Return the BioModel property value.
 * @return cbit.vcell.biomodel.BioModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.biomodel.BioModel getBioModel() {
	// user code begin {1}
	// user code end
	return ivjBioModel;
}


/**
 * Return the BioModelCellRendererFactory property value.
 * @return cbit.vcell.desktop.BioModelCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelCellRenderer getBioModelCellRendererFactory() {
	// user code begin {1}
	// user code end
	return ivjBioModelCellRendererFactory;
}


/**
 * Return the BioModelMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getBioModelMenuItem() {
	if (ivjBioModelMenuItem == null) {
		try {
			ivjBioModelMenuItem = new javax.swing.JMenuItem();
			ivjBioModelMenuItem.setName("BioModelMenuItem");
			ivjBioModelMenuItem.setMnemonic('e');
			ivjBioModelMenuItem.setText("Edit BioModel Annotation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelMenuItem;
}

/**
 * Return the BioModelPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getBioModelPopupMenu() {
	if (ivjBioModelPopupMenu == null) {
		try {
			ivjBioModelPopupMenu = new javax.swing.JPopupMenu();
			ivjBioModelPopupMenu.setName("BioModelPopupMenu");
			ivjBioModelPopupMenu.add(getBioModelMenuItem());
			ivjBioModelPopupMenu.add(getNewApplnMenuItem());
			ivjBioModelPopupMenu.add(getJMenuItemCreateStochApp());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelPopupMenu;
}

/**
 * Return the BioModelTreeModel1 property value.
 * @return cbit.vcell.desktop.BioModelTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelTreeModel getBioModelTreeModel1() {
	if (ivjBioModelTreeModel1 == null) {
		try {
			ivjBioModelTreeModel1 = new cbit.vcell.desktop.BioModelTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelTreeModel1;
}


/**
 * Return the EditAnnotationMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getEditAnnotationMenuItem() {
	if (ivjEditAnnotationMenuItem == null) {
		try {
			ivjEditAnnotationMenuItem = new javax.swing.JMenuItem();
			ivjEditAnnotationMenuItem.setName("EditAnnotationMenuItem");
			ivjEditAnnotationMenuItem.setText("Edit Annotation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditAnnotationMenuItem;
}


/**
 * Return the EditAnnotationPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getEditAnnotationPopupMenu() {
	if (ivjEditAnnotationPopupMenu == null) {
		try {
			ivjEditAnnotationPopupMenu = new javax.swing.JPopupMenu();
			ivjEditAnnotationPopupMenu.setName("EditAnnotationPopupMenu");
			ivjEditAnnotationPopupMenu.add(getEditAnnotationMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditAnnotationPopupMenu;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setPreferredSize(new java.awt.Dimension(75, 20));
			ivjJLabel1.setText("  Application:");
			ivjJLabel1.setMaximumSize(new java.awt.Dimension(75, 20));
			ivjJLabel1.setMinimumSize(new java.awt.Dimension(75, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JMenuItemAnnotation property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemAnnotation() {
	if (ivjJMenuItemAnnotation == null) {
		try {
			ivjJMenuItemAnnotation = new javax.swing.JMenuItem();
			ivjJMenuItemAnnotation.setName("JMenuItemAnnotation");
			ivjJMenuItemAnnotation.setMnemonic('a');
			ivjJMenuItemAnnotation.setText("Edit App Annotation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAnnotation;
}

private javax.swing.JMenuItem getJMenuItemCopy() {
	if (ivjJMenuItemCopy == null) {
		try {
			ivjJMenuItemCopy = new javax.swing.JMenuItem();
			ivjJMenuItemCopy.setName("JMenuItemCopy");
			ivjJMenuItemCopy.setMnemonic('c');
			ivjJMenuItemCopy.setText("Copy");
			ivjJMenuItemCopy.setActionCommand("Copy");
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopy;
}


/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuCopy() {
	if (ivjJMenuCopy == null) {
		try {
			ivjJMenuCopy = new javax.swing.JMenu();
			ivjJMenuCopy.setName("JMenuCopy");
			ivjJMenuCopy.setText("Copy As");
			//Menu items in Menu-Copy
			copyStochApp=new JMenuItem();
			copyStochApp.setName("JMenuItemToStochApp");
			copyStochApp.setText("Stochastic Application");
			copyStochApp.setActionCommand("Copy To Stochastic Application");
			copyNonStochApp = new javax.swing.JMenuItem();
			copyNonStochApp.setName("JMenuItemToNonStochApp");
			copyNonStochApp.setText("Deterministic Application");
			copyNonStochApp.setActionCommand("Copy To Non-stochastic Application");
			//add menu items to menu
			ivjJMenuCopy.add(copyStochApp);
			ivjJMenuCopy.add(copyNonStochApp);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuCopy;
}


/**
 * Return the JMenuItemCreateStochApp property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCreateStochApp() {
	if (ivjJMenuItemCreateStochApp == null) {
		try {
			ivjJMenuItemCreateStochApp = new javax.swing.JMenuItem();
			ivjJMenuItemCreateStochApp.setName("JMenuItemCreateStochApp");
			ivjJMenuItemCreateStochApp.setText("Create Stochastic Application");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCreateStochApp;
}


/**
 * Return the JMenuItemDelete property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemDelete() {
	if (ivjJMenuItemDelete == null) {
		try {
			ivjJMenuItemDelete = new javax.swing.JMenuItem();
			ivjJMenuItemDelete.setName("JMenuItemDelete");
			ivjJMenuItemDelete.setMnemonic('d');
			ivjJMenuItemDelete.setText("Delete");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemDelete;
}


/**
 * Return the JMenuItemEdit property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemEdit() {
	if (ivjJMenuItemEdit == null) {
		try {
			ivjJMenuItemEdit = new javax.swing.JMenuItem();
			ivjJMenuItemEdit.setName("JMenuItemEdit");
			ivjJMenuItemEdit.setMnemonic('c');
			ivjJMenuItemEdit.setText("Edit Sim Annotation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemEdit;
}

/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuNew() {
	if (ivjJMenuNew == null) {
		try {
			ivjJMenuNew = new javax.swing.JMenu("New");
			ivjJMenuNew.setName("JMenuNew");
			ivjJMenuNew.setMnemonic('n');
			//Menu items in Menu-New
			newStochApp=new JMenuItem();
			newStochApp.setName("JMenuItemStochApp");
			newStochApp.setText("Stochastic Application");
			newStochApp.setActionCommand("Create Stochastic Application");
			newNonStochApp = new javax.swing.JMenuItem();
			newNonStochApp.setName("JMenuItemNonStochApp");
			newNonStochApp.setText("Deterministic Application");
			newNonStochApp.setActionCommand("Create Non-stochastic Application");
			//add menu items to menu
			ivjJMenuNew.add(newStochApp);
			ivjJMenuNew.add(newNonStochApp);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuNew;
}

/**
 * Return the JMenuItemOpen property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemOpen() {
	if (ivjJMenuItemOpen == null) {
		try {
			ivjJMenuItemOpen = new javax.swing.JMenuItem();
			ivjJMenuItemOpen.setName("JMenuItemOpen");
			ivjJMenuItemOpen.setMnemonic('o');
			ivjJMenuItemOpen.setText("Open");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemOpen;
}


/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemRename() {
	if (ivjJMenuItemRename == null) {
		try {
			ivjJMenuItemRename = new javax.swing.JMenuItem();
			ivjJMenuItemRename.setName("JMenuItemRename");
			ivjJMenuItemRename.setMnemonic('r');
			ivjJMenuItemRename.setText("Rename");
			ivjJMenuItemRename.setActionCommand("Rename");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemRename;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setOpaque(true);
			getJScrollPane1().setViewportView(getJTree2());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
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
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
}


/**
 * Return the JTree2 property value.
 * @return cbit.gui.JTreeFancy
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.JTreeFancy getJTree2() {
	if (ivjJTree2 == null) {
		try {
			javax.swing.tree.DefaultTreeSelectionModel ivjLocalSelectionModel;
			ivjLocalSelectionModel = new javax.swing.tree.DefaultTreeSelectionModel();
			ivjLocalSelectionModel.setRowMapper(getLocalSelectionModelVariableHeightLayoutCache());
			ivjJTree2 = new org.vcell.util.gui.JTreeFancy();
			ivjJTree2.setName("JTree2");
			ivjJTree2.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("loading",false)));
			ivjJTree2.setBounds(0, 0, 78, 72);
			ivjJTree2.setRootVisible(true);
			ivjJTree2.setSelectionModel(ivjLocalSelectionModel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree2;
}


/**
 * Return the LocalSelectionModelVariableHeightLayoutCache property value.
 * @return javax.swing.tree.VariableHeightLayoutCache
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.tree.VariableHeightLayoutCache getLocalSelectionModelVariableHeightLayoutCache() {
	javax.swing.tree.VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	try {
		/* Create part */
		ivjLocalSelectionModelVariableHeightLayoutCache = new javax.swing.tree.VariableHeightLayoutCache();
		ivjLocalSelectionModelVariableHeightLayoutCache.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("loading",false)));
		ivjLocalSelectionModelVariableHeightLayoutCache.setRootVisible(true);
		ivjLocalSelectionModelVariableHeightLayoutCache.setSelectionModel(new javax.swing.tree.DefaultTreeSelectionModel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjLocalSelectionModelVariableHeightLayoutCache;
}


/**
 * Return the NewApplnMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getNewApplnMenuItem() {
	if (ivjNewApplnMenuItem == null) {
		try {
			ivjNewApplnMenuItem = new javax.swing.JMenuItem();
			ivjNewApplnMenuItem.setName("NewApplnMenuItem");
			ivjNewApplnMenuItem.setText("Create Deterministic Application");
			ivjNewApplnMenuItem.setActionCommand("Create Non-stochastic Application");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewApplnMenuItem;
}

/**
 * Comment
 */
public Object getSelectedObject(BioModelNode selectedBioModelNode) {
	return selectedBioModelNode.getUserObject();
}


/**
 * Gets the selectedVersionable property (cbit.sql.Versionable) value.
 * @return The selectedVersionable property value.
 * @see #setSelectedVersionable
 */
public org.vcell.util.document.Versionable getSelectedVersionable() {
	return fieldSelectedVersionable;
}


/**
 * Return the selectionModel1 property value.
 * @return javax.swing.tree.TreeSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.tree.TreeSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Return the SimPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getSimPopupMenu() {
	if (ivjSimPopupMenu == null) {
		try {
			ivjSimPopupMenu = new javax.swing.JPopupMenu();
			ivjSimPopupMenu.setName("SimPopupMenu");
			ivjSimPopupMenu.add(getJMenuItemEdit());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSimPopupMenu;
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
	getJMenuItemDelete().addActionListener(ivjEventHandler);
	getJMenuNew().addActionListener(ivjEventHandler);
	getJMenuItemRename().addActionListener(ivjEventHandler);
	getJMenuCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJTree2().addPropertyChangeListener(ivjEventHandler);
	getJTree2().addMouseListener(ivjEventHandler);
	getEditAnnotationMenuItem().addActionListener(ivjEventHandler);
	getNewApplnMenuItem().addActionListener(ivjEventHandler);
	getJMenuItemAnnotation().addActionListener(ivjEventHandler);
	getJMenuItemEdit().addActionListener(ivjEventHandler);
	getBioModelMenuItem().addActionListener(ivjEventHandler);
	getJMenuItemOpen().addActionListener(ivjEventHandler);
	getJMenuItemCreateStochApp().addActionListener(ivjEventHandler);
	newStochApp.addActionListener(ivjEventHandler);
	newNonStochApp.addActionListener(ivjEventHandler);
	copyStochApp.addActionListener(ivjEventHandler);
	copyNonStochApp.addActionListener(ivjEventHandler);
	connPtoP3SetTarget();
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BioModelTreePanel");
		setLayout(new java.awt.GridBagLayout());
		setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
		setPreferredSize(new java.awt.Dimension(200, 371));
		setBounds(new java.awt.Rectangle(0, 0, 240, 453));
		setSize(240, 453);
		setMinimumSize(new java.awt.Dimension(200, 200));

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = -1; constraintsJScrollPane1.gridy = -1;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJScrollPane1(), constraintsJScrollPane1);
		initConnections();
		connEtoC7();
		connEtoM11();
		connEtoM2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void jMenuItemOpen_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	refireActionPerformed(actionEvent);
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelTreePanel aBioModelTreePanel;
		aBioModelTreePanel = new BioModelTreePanel();
		frame.setContentPane(aBioModelTreePanel);
		frame.setSize(aBioModelTreePanel.getSize());
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
 * Method to support listener events.
 */
private void refireActionPerformed(ActionEvent e) {
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}


/**
 * Set the BioModel to a new value.
 * @param newValue cbit.vcell.biomodel.BioModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setBioModel(cbit.vcell.biomodel.BioModel newValue) {
	if (ivjBioModel != newValue) {
		try {
			cbit.vcell.biomodel.BioModel oldValue = getBioModel();
			ivjBioModel = newValue;
			connEtoM8(ivjBioModel);
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
 * Set the BioModelCellRendererFactory to a new value.
 * @param newValue cbit.vcell.desktop.BioModelCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setBioModelCellRendererFactory(BioModelCellRenderer newValue) {
	if (ivjBioModelCellRendererFactory != newValue) {
		try {
			ivjBioModelCellRendererFactory = newValue;
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
 * Sets the selectedVersionable property (cbit.sql.Versionable) value.
 * @param selectedVersionable The new value for the property.
 * @see #getSelectedVersionable
 */
private void setSelectedVersionable(org.vcell.util.document.Versionable selectedVersionable) {
	org.vcell.util.document.Versionable oldValue = fieldSelectedVersionable;
	fieldSelectedVersionable = selectedVersionable;
	firePropertyChange("selectedVersionable", oldValue, selectedVersionable);
}


/**
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.tree.TreeSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.tree.TreeSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeTreeSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addTreeSelectionListener(ivjEventHandler);
			}
			connPtoP1SetSource();
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
 * Set the BioModel to a new value.
 * @param newValue cbit.vcell.biomodel.BioModel
 */
public void setTheBioModel(cbit.vcell.biomodel.BioModel newValue) {
	setBioModel(newValue);
}

/**
 * Comment
 */
private void showPopupMenu(MouseEvent mouseEvent, javax.swing.JPopupMenu menu) {
	if (SwingUtilities.isRightMouseButton(mouseEvent)) {
		menu.show(getJTree2(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
}


/**
 * Comment
 */
private Versionable treeSelection() {
	TreePath treePath = getselectionModel1().getSelectionPath();
	if (treePath == null){
		setSelectedVersionable(null);
		return null;
	}
	BioModelNode bioModelNode = (BioModelNode)treePath.getLastPathComponent();
	Object object = bioModelNode.getUserObject();
	if (object instanceof org.vcell.util.document.Versionable){
		setSelectedVersionable((org.vcell.util.document.Versionable)object);
		return (Versionable)object;
	}else{
		setSelectedVersionable(null);
		return null;
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G41DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8DD8D45715244144B446108493121814D812E003CA922CF417F6CDEA3FD2E337B6319BC3CDEBD2772B5D35DBF6EBF75956A60F9F05D190B4A8A828549885C164D791C5C1708FB122224156C68666812373674CC3C0D1771C7BB7EF5E3CE110A44B1763FB775CFB4FB9775CF34FBD7767CD246CAD214B931332A5A9F139447E76A6CAD25095C9DAEAC8BA4DF189E7628C126A6FAFGEB24644194F826
	1D0DB3C47EE31CA1CB9A1F168669FCC807FDA74E10836F9FC94D1706FA6045871FB6481BF2A53EF2F8BEBF6A67FCD2C97A6D32F4F82E86A8G9CBEADE7117F6F4AF2993E0961B9A4A6816E9C16D9D91E4FF011D0A696C082FC76B9DA3E841ED400B135B52C5C25C7422525331DC999C8C7C51343B8D6B712BD8B24BFA63637A12F4A72F4D6E7B16495G8E9B1FAC55546C046701562BBD873BBA4DCA6975AE59E2A9B5492E2B0A5DD1DBFB58ECBFE5B74916B627AC379AED32C5126691FAB74B53C8FDC91A88690E
	73089BECC49E3970DE86F07CBC477FE487624360BD86E05EF95E2E3A9D87185C6DB7E7C8C3532BD7E57AE857127334BD59975C6D0AF9FAC15286528706C154F5AB64A9GE381A2G66GAC8148A3FD1FB5006DEC68D49A6DD62B5D5660F09C36181C24ED70DEDB8BB98CF7D66E32BBCD12C47BF7DFF9090EBC43812DF76C2E6BD11D04D7723E0EB8BBD5DA706A63C929BABAD990FEB3A965B32A0B7C4F5CBA213C83FC72BE701927ADFA727ECCD2732E9A1A173CDC07F72BF9DEF2AB23E3D2710E7A8C7BFADD9376
	F5A03CA7923DCB92456FA07884951E5A659DE2B318AEEC0FB34CEA6736513E1A4B5212B2C94AAAFFC2D78FA12F0FCB6A38C8E59838281565CF05446EAED21E45C2168DC508CF792B9BCFE5799931D3CC1BA0EF665F628C543E565E44F11A7F77B8C38DC0ABC0AF40D800B000683FF39B3BB830FB04B656EAF4AA077B4C96136C14E85D35036B61A98FA8CE63891BCBB15A3A648BC6275958E911DD92AB13B708BA687428D7C0EC3F8BF89C13ED325328186DB620EE344A0A6CF451B2C503EB300CD3A639EDE62B8C
	8C2C8E0953AD9FC47DDB0CAE653C43E4D4E4F7891A3FEF907B643A7182D0848270CE75F2E88875B59F5A9F8FA0FD4E7590C5681D141D60AFEAEBDB6C762BFD0EB6EC09547AB96A798962BB32613D64F32E637D3790978C6988C1675F87572A68B4193BD0FC23F3100F313C26ED701C8965978224DD0EB3EC832881E885F0G0C85883F4CED6ABF5A39CDB5E5CC11CAFEB6C15726E601FD4F39CCEDE951E56D585AC5F8225C699DA0BFC08B0085A088A092E09EC0C2871F9F46B486E96857B01A2D17BD4762B6A62F
	F6ECBA98AE0DBD313DA371A7619ADA8BD57528EC91DE322DE24F8C9D7F30C607C7260A9EBE13C7A057EAB65E5569652AF7EA8F13EF23ED659B21BF815A0D5CA6365FA820E36C3051A573D1F55A8983C2B639AD0436AF31288D1EB8A6BF86E884388870B700EA0073G1FG6C85B886308B20C8C2AFA8C965GA75076G4E6038842883G53139E927A5BC268BBC036900019G0B8132BAC1E6008E0031G11G7381242EB8C3A1C0AB40GC0A8C0ACC0BCC02EA94ED08F508B90E4623673739D2D09F7B70778020F
	D9DBD733B62EE66D5D4D5A3A1A69E42F2ACE1D6A3D11BD0F297442734E3067F926BB2EE32D8C7288E59D9CE13907C6298B2A5B0A42AFCB374BA5775858401E98EF62D858A43947135A66F7E8644167BA00AD920DB3557D4647995ABF0E43D512378C1F68607420E804650AC7298BBAC673EF223C72F55926B4498E59E60297974B7B78A636014DF35DF6D12708609DB0AFB642C46492785DB7D137C6C5F11ABB7B94791C2CA8E6DB0FBBFFAF61656A6BF43827D44E6B60CD6C9BFBBF4C0B22FC63CD1CD33A6CB61B
	CC1C052842720F10B9CD3648D6903A515EE7D3A87E6490467DD765C1B6271E9EDAA1611CE869437919465211C66C9B0753EC859704D33C14A663FCB735BCD72523E8985F38A6D8G4FF8931F1B7ABF66F353369B132519179DC9FA31D748738FA7354AAC76125DF39315297E163E6CC7495C4A70AEAA6B314B28D71D701E586DC752BB56BCD5FEA3AC8A4EE2C7BAE567D1E317E207C918167B7DDA22FFA0CE54C9CA17333CE34EB4958D0AB36FC69941F1375A9DFD0ED3322D0F62FFF1F42DC69EC60A515AF3966B
	1DB3DBB5755E2FD237C3E40AF698EAF0AC9EB11915861B4D2E5078C81422F34D7BF7C84C8A7A8C81081479DC737AAD4C6771871BE49A6D9656D648CB6F467E9DE84AD675D12CCC7BB7811EC3A47FFD115F86E9CBB74D47B44D3F7F884F1F8569852AFCEABFFFBE22361F749E1ADF854F63GD40697B72AE568685152F8EB239E8DE2D370CE4B44667BAA93A94AFC3C5DD719589E2E37E9373308CDDC6B13A10C45406F2498B75F3E3DD613F7010DA11A7F3D5BB806AC2A18312C97791A0A78DAF9E18F0D3B92986F
	6CDE4ABF1A145B3213179B6A2531D9F0AF0D4D42D8B9CA6FBFC43994F31CA19F600019529BB22B4B3DD24C4B45GFE9E40D25DF257CBF8392AAB409740F2C52FDC6DAEDEAE02654FE24F85D75465160AF269D7A12E85E83A6AC9EF61D5AC97DD4A4B85DE256D1C884F8800B9G1B5166AC4446F673F2A5969A1FB4D9E8B9ACDFAA74F7260C178B35D0FE512C5CDCF8E6DBA156A6FC5F96FB9705D61ADFEF257DF249CA7952F6BCD7414BD9D839C0AB25BB532AEE2F291217CBGFC16AD4ED0E623659CB6F53942AA
	21BF404F82D8E473548BED4792D1EE1B1D669F30D37EEDF6965B5B315C772A05FCF69A13855AE9EC9F824F39G146F42BD3CDC1983469DC02FC3CF3E303D3CDCB8644F74D92E3DC658B364A778AC173E0F174B3D8632DCF317EB3D26AEB7D3148B81FCA4405CEB1EFA21FE7F77C5B8FE49FE4F9563F563C0292B9F66535A5AE6627E695ECEEE857AED0CD9321CFC8F67DCB37AC8855263G421CFC5E7AE9865F43893E9AAEE55DFAAB59575ED40C13592D53BDEFADEA6DCB26BE38A491F99638200DAE6EC7765DD1
	2FA39BDCAEFB1719B8F066AB965FD14FEB94775BBBAB6159EDF63A1493B0DF8DF03F727BBBA8AF2EF1991A69A3FF67D62E0FAE3B35545CE9AD6D3BEEADB5F6AA25B2CC9A3535FC3AE37BDE531A783ED7130B6B6056D1358EE2B2EFA52D722103A197ED7BC417DB872634BFA4537D874311F4C6FB8164E7AB60C794449FBD0231870EAF4A2F2B12738B3A713C54B161A91F7C0294663F95B73F4087AA993F29025FE248AF6863FB020F6CF06B443DA758E936DBF10EF5EF8AF29D2E484D77E1D3FC22253A2BBE1A46
	64EC6F63F655F594E58802F4B840CCA143EBBBC63CAF49E4217424BC32A781F46281522EF3BEFF48C7FC8124EBGDA853EF5BB62FBA19DG9082C0F5739059A3C2D9A2813798004693CB0BD613F1A0C3A84A6200D22E070C7EACEDDEE151855AE713BE2AE7FDB2E76AA469401D4F137476BA47E676A69574533EBA5E6F6E334824F713E681D0590E6F643285C319B97DDC36E9BB51CE14DE330B265F5E097A6EE42A22F2CC592C16A34DB8C6D70EF13F69C9CA99E07B5083EEB9F2135FCDAAC966F26494F3B994A8
	93B140E3116FE62BE311C583F4FFA4FD104BF93B38821EC62ABC7AE8151D5DF627D5B6952285159A7BE178617A213401E49F4177A22E67B37DD570F44AB04A6586CD75965F757797DD20F27FDC48DDGB2358FF23903B768499DA464BEDCB2DA39A95FEF14F03EE1C0F301607B43F5FAFC33EFF03EABF6FDB13EDD3BB85FC22059A1684EA6F8334D2C180D96738D3930086AF7C1B56EF149B68C3F5A6CF6CB1B5961220AE5C33B3499471D506726D2CEBF84E84F3D415BF5CF2EDE3B52EEF27E0125DF2CDD6F15F2
	7F158FB45B845D633B79B820F2EC5C2F16E39C140B8218AF4ABBF6236CD66AC658DE78FFB52847C9F46AB85DF1527BE2C852121B6CECE248BDCEA62EF9B7A9F28D9FA7113B391CA6A8B3F108734DA9C3B90D4E1EF24AF368A26799B0A7CA4A7D68A1DD1E8873982FE5AA1E0B325ECDCA4862BCEF1471BEE93985B14DAD4E733DB27DBE9F3F93571F0EBED8FE1BC1472DCE3BC3F6AA03ECBCD90C506927BB2F00D35B6F0DA93AC07D7BFA322F0B74E2EEF1BFAAD57036AF85DC49EDAE4725A3A837673A112F599E2C
	607DC7E94C2E60EDB1C17D0902C6F965AE52EFFDAE39147C8B32F7DD75361DD31847CD07791075C5A54276DC27ED24261773F0D794367ED0A517E59E4811FE075B7A729DDADB47BD6026BBE25E287CE2BE2FE99F36D576D8477AF1F6929FA7E72A384C6DA0CF101079354DDA19F16FBAD6489CDD754546673AEA914FG4D6CGCEF728DAEB03947F8241F729769AA81DDFD78BDB869A0181BCC648BE08CF3287A37A5539867BD5EF5F22D095C738E467F559D9FA9E1EC554F62F6E61F1DDCCG0F33149ABE9EA35E
	1CAE35DF996FF3BCAE8DE0E7F1773847E309EDCEDABDG0B6D7672B674C21950FBF89F9C4E517311736F913EE96F976B039F8B3ECB0066362FF13E537334FC716C62525778383D270657F3GAEDC541B3AC94FE6960AFA557BF83D2500AB3917574BCB53EB2769DE5E1736B60CE92FE030F8C2112D279D326D72E866765F887E83C0BBD270FFEB0B1E5C0B6F65FAEE2D9D2D1E69FE72260AF60DCDAC3B57FD5E129528FFE6338D70B51A3C96C8F7E8F0745C7207F48D96C84FAD13857E27849F2842D3DDDCEC6663
	AF9872928279D95B13A938863CB4A64EB0G9084908E908B30F88CDFFB5D1470A24430E719A7EC8AAC4D0C16F39E1B2F7CCCB19179DE1587D93ABAD1BDD6294E221AFC1DD3275C272FAFAAFF6A8A6C4BDCA8D3855082E082883C0F4B9E1368D776A3833A32D339B20F20DCE1BEE45083E68324DE4CDD5423FE26682F6A7B9DA9EB1A1312FD7819A50C76327B5CFE066AA4B295F5F260FE9807G8E0031G6177F31D2CC8AA712793FE5E4C14417AE8143AF87D2EF5C16B559E3D5BFB93F48CFCC856113177EB6F9D
	2CB9C66E9D5C2F3DF7E0BEAA6E9D0C0DB34C8220659532FE6CF8C0BB46EE933C63816D98DB114747D8G64ADFA0053B1EFC27C712F53722902CE9F41F72870144E1F487A9453831037606B1C4EAC32BE2DFFD0AB4FBF93FC6B03DAF9AEEC6772F4C05E2C8779D962E3C5DA3D960E736C1BA6CD1A56FB4E2BDE29A65D2B29C76DF10581778482A0EFA2C09440DC0025GD98F4138FD085B653924BAFFF6C99CED1B3D91F0476D762B0D5A339BA16B27BBC75746CBDE75B6EB52ED8F69357156CAEC23857246820482
	44GAC84D886D0B01E37B1ACE97F885BC8E7966F96B27FD2BC3A7695FB5553362FFE3CE7BDBAA747AEC779F576F578AC4D764416E673383FFDBC373777C6A96B6F7C4A9A2411154A704686AEC338106179125149702AD7BD2D8C8BF5E578E0DD9A2311A0E4B06F9A1D8C7DDE7534632E6CE13DFA9F170E0EDF39D7BDED1B0FBF2C576668DC2E770E07F91BFF6DC5CB8B7AB27C41EF3D1007756C54C1629A77DEE1CF1F990C9437A80E9AEDDDF81F001EDD251DD41FDDC5BECC6F9FAD00E7BCGED4F197D589E1C17
	5302F83C34F4950E73FAC8DF82D0G02G22G6682AC8D62637C3F138ECB7E6EFF7103EC986B7CD54721D1FDFCFB776874B84BEF3D74C7747C19B3835B398D726A815A8194009000A800790F70F6B6A77BED273802B612F686140D4EE647FB55530E1178C7746A65117B11E90F0EACB649FF145B750A0A5175C7365FFAE50F6A551B6BD5EFE4FA7905D7BD2F312C4B6F314AD14688DE75347CBA9E55739D87F670B577005071E7D52313C1762A2715A1D2D706044D5CFF458A99722AC7574FC5FE6BC90F69557B2D
	D73D113579CFDE7534761F2B4B4F31876B15BDB6B27BC7207EA1FEB57A07A6285781B0G908C908DB08FA061B16E9F1EC9BEA45D05FF98B666495CBBBA5DEC722A2735075CE0BDFB984C6176D0964C6D612DD14A701EDF99FAF5E5D81247E58890B25CDFB3BA99A6F855534A902DAB433FEE62B24C95B26C5FB7BA99CEF855534A10B6C1CF0633E9DC060289DC065F0DD206BF7B1521DDD70615855CC7B9A01F5EDFBEC44E4743A19DA3F0E3AA913790528982175F0F387447638C050FF3DCB1419D4034403D5609
	B8932407846E7DAE4405C2BA7AF15E769FB7633E789C11FE1B2471AC0326AFB7EB7720C683C8DF8FCFF98CFCA9BCFC5D93267EE3D20A6F7B0313D3746F8FCECB513FBFB8BDE58D45632106EA270767FFB7E5787B05BFC8513BDFC87B688DF297E09168AB61F16E7BB2D77816FD7D8AFD5973D7684B3EF54570326FDC61CB76CDC4477F1204FB814EBE1BE2364A04B40D1765813CE38ADE78EC1F0D3AE8246E526E83C95C6F17E5211DD5BD415B785A0A8A495B3F639DBB3B1393E9130D4E535D5DA278BB67103B38
	5C51293EF574CF297ABA7AC12A3E0E5EC89DDEC7EF257A529135676547702C33955AE682988288810882188B908F109692E758964247D88EA9FFG5217C268FD9C3CBB06773D70CE50D0883D2B057744E8F97BD1FE479E69A19FBD3B0FE73CFD0D979313895FD1163B9DEFA12C9D1EE379F1A1DB46B0E3B2D3F25FD1568334A9FEA7D9FB2FFFA4207D36004AF6A38FEDEE2250FBA0C963FDAA1ACE4F5373C393C7219B5F3E6E93DD9EA37D46E1E4BC6CE4BFB09A641E8790AF5AFB136071DE985EF1437BDC6C1B06
	CDEA1962C7505EF9A3A8934D4AD09E9BBDF88C5777EA9B70D7A6D9C5EF0DB4FBBB76E776133CDD73C89A6F7D31BD396DA31DE7104668F45FB7E29E2369F36A03FF349E470F83DC166CA21BFCCDC6454872FE2D601DFF2F3B681FC2E4AC71337059C586D66608E2EF44EFB558770C865FBEB447206FC3B799F01C3B6CFD4EAED96BA73799F2C51EBB9647CDBE94071659C56AABC6E70F6C65E72BC8FD1A672BFE1D4117AF26ED9D33D35556D3B502767DABFD37F57CCA7D36862F745D5627D77AEFEB58CA5FEDFDE9
	257F36C62D9C3E2DED456AFE2D92B6793F595AB835866C3BE5989B2774F2CB54742A853D399B3474BA001662175E828FFAFB843D6475DAFAE30156C48D3DC8D51A528B5D252637D7607FDA3AD2ED5F651C4FF7F335FCE6814D79FE655E3C1B73E9D56C2DF88EC8E31E1EA3B866F476324FF5B96D968B5EFE15F84CDFFF122FBB16BC496F894A35680F37BD95E7B8G50769477D39776231C162774658AGFCB0C038AA1F5A47872BFC5B7472F4FD1BDE156E5B2657267B37693CF45FB63DB55D3FCD972763BC5FE9
	B6191D6C8B882A735F6EDE45F55E6D34DBDB2B994D218C5FED3CB7C33F0D8FE478EEE3D0067FB6BE19613B0D21997E5BB8B9C33F0DB34AD4F6FA8A6D142E81AA481D2798685FB9CF71355BAD02DBG69A5CF717B85F595EA3BAC97761ADA299E979582BF3B52E3DCD470F15130C3EF0FE9198F7BF30F0B71D59E74AA453AF83316DE56539043BE6D0F5E59EA352FF64B3DED0F07ADC08E55497D64FECD8D50BD7EB4DFC77CF135EF7BC84C54370F54CC5F7631BA533FFD64E47A360F4D197E6DE3C76670FE7D68DE
	8FBDD7F1BD5F4E516A399D74E0712B6715B5EA7B40B911527B4FBCAD3DG20956C175E77BC686D93747EED1316DEB8500A71CB4F394F233DE51C5EE7E9DAFAF30156A23F7436572A652B9472E5966859FD02DFFA97AA70BBBD0BF1505E2740BAEAA25A2235021F8325C3FA5BC4CA23E5A2BD8F2275A6D6229DDB0D83E6EB1F751C7906ACADA5F557D6219DC740FB74C4DE36329FED4A61143BE527D3B6115292AB5FC276FBEA1E09B3B4BDA36E917723CC1D76BE1B49454BDDA4659CD0E66899FEA76A65FE14215B
	E93402091185F45333D8F6FEA717BF924A4E82D8841082F95B1E65BC7E370B48EF36E964DFE3C2BE6320DC08A88BFAGD04D046514FFD0AF6A7E3A71CA5B20C3E67368AAA75EC374FBAFDFFD71CE0C2F2A2C8C75587108F3E3CEA32F6696E3277B7B41DF9DD8C56412E1F9EFD46CCE0E37DEC4BA4D6A3BE2024E4F15CCF5DE13EC11951965BDFA4BA38F64C389D83D5C0F54F96ECB16AC7F50CACFD9AA987EB50707AC0DF6472010E5F6E23ABA6FD3391F5EC5EBBD263E0BB66BD9FA97EE71337C2E5A6FD255794B
	D8BE31D712FFBE57D7BE3B63DB65A193EE4C083C3C75780D826FA6DE04663DD901F9A0E60343E131F9665953F3BC5A09BFA6A11FD36CDD3DD018512E5D013FE161792D2538AA406601ACF2C63E6D9BF1061AEF7079320D60DAA0FDC960FECD4E73FBA1BDA4F0E59B91978469D0015B381E0CC7C84F9438AE021B8769C58297CBF07110CE8F6538F9646CBA9752A5823709702D47F4A80F710E1C643E67D228373FF16860689D1DF7C84C98982A3D3B73AB028F8E555E5D39DA4B6F6E04C1DE5AF3DCA6ABB91336C0
	BAC0600A6B781EF11040DDB80838A8C84F9138D002DB8869E54F7171FE221C5FBB2F1F04E5AE13F441A41E9F4C73ABE87E37C8BAF0922759CE4E6DC2A09DAEF059DB88EFC8478A5CA502DBG69A5027633FBBCF9C711F40228B3011C3F24FDB34E107FCD0E7B854115C13ADE605272094DC03AC3604E901C8269C001CBA467FBC1108E9338E6020B06F42C40595238DD4E933899E95CAE978B5CF6F29E309452A9E17C4E6B84F2BF9D7BB28BF04743387FAE6F7078AE8AEF1056569EF31ACD070DBDADE4C622EB0B
	B7C97DG2897829099466F9F8FEC675FF9C53CB0C9CA99547F36EEE15B2D2458B0EA03CB425C778267BE3DA02970F97C8DABB38EDDB6C6B37ACB61E9137B8B442556A633D536618F3B94D614D76D2D2A295CD3353724407DDE446FA906762B6FA9562C7C1A6EB76A881667299C816C39EC5DD309D41604DC37AC857D854348D2D6D6D2D039373C085633EEF4577B4038DDDB6FAC44EAC6DB0FC5AE04DA70DFC5957CD3D5DDD9A46E66F472EF4F2201DE7C7308AB69D27BD1247DE0B2BDC325791FFB64E35C077108
	BBBF48A39FF574DE22BA7FF50FFC5455032CBE55679B1F707EDD76C624E4199E6873F73DEADE20FA3C441EC10B1ECCCC9F9CC8263AF9BB4F2D1BBD4D7EF48A7F30EFBEFE5F6D63DBD0B28BD33D1DEA6677E102DF905F87AABE3E779333A35B4FA16347EA132DF61B390B469F7B082F7F91401C9738CFD06A48779E906C53767CB1DF55579B3CE92CA373403B68DB840DE39BBDE9EC5A6C26D13E5E1B46F1027BD79CB302460D751EB44EEFF1537865FA923B3033EA2B55E8B3C11A169BA8F0177B77DCEFDEC9248D
	BF44FE963C2AB6F872FA990FE4589CF9096CBF243F98E7A8F8117B1D67CFF2FFDF75A21F17FED9CB7C32C87F0624719BE71A3ED67BE51C5569011B67573FB29E49C227017B555FE751F9A31158CC8848C1E36243F5BCA6DE8C38E5G7961F10683G1742394E2D87B106F35A6D4A85334B0CA7DC34FF469C64E73CC3226C0C03C8CB7BFD92DDBF4CBD48F90D0372E1G8B811606536F4BE97D3EF27497DB49AD34D20B5986315220C55EAA7131DFA72BFDE9594D961FF73E53A733EFCBA63BFDA965B1136CB3ECED
	C03FE75A4A62349D1EF61573315BB60B37F85B6661AD5CAF36CC66323B36F8527827EDEE9A3349FCED6B33305F730848712679FD027BBEC03B2079079CCF1A6FFF622639A55F1B461EFCBEE6FA850D5EFCCF9A4BF738E964EE7626311B605EG9890B40C1BBDE99496A28DDAFEA271D73F84988B65E97E43C5EE9EF5851EF5DF41CF14597A74E93FF73C34C06BCD3E6BFAB406BCCA3EC38B19CCE348C401BFCD70E1AABC0DD1660B98A59272323F456FB6CEA8C6DB9E00F4B0C08440AC00C5G52CB62FB0AACD856
	8FFF1F274D6EE8112F4B965591AC35731F7CCDED67D9CFD91238DC5AF6E53F44E217175CB18C157103926C17B640BB04CCD3F370B70AAE982FF81FBE8312FF3EC8316497C6787EF5575D6A1B366335D4BED7C77D433724687B1AFCFFF34F640FE44F42DFBD1908CF5A165ED25477C248DB86D090816BA00096GD384EF5BAB9B59379AE4A925A8462EDE728B8762005E8CCBB401460D815A36334D3CED83916EB549B8769E8A4F28884F358A7B76E99DBEF718F630F449C6123671746575A46D64698D9BC83A0B27
	DDA7F1BF436EB0DE430FFB68D87A7AC96ED3E301E77A9444451CEC9AC64F9F32BF6E7767C33DA5G6BD8DA1A6236E94A377BA364CBDCEC85476554B5C8B47EE9123B0DFD96C57CC2C0218AF92ACFA19FB7C35E2710D73855E2B71AA00659DA52ED343864A29C74B47E692D63E781EDD3F85C98DA270D9B9F1154F15F1BF55A38B151A3BF6520B6EEFC46A3FF4FC1ED5C486B333D2903448FB97BE4BA36938E72F563B810F156949E274DBF0C63D69B2715628F9454562A54F34EE316E3E7A007F87FAD1B424FC022
	8EA31F2A29E02FG26295C07BCC83E379E1A4A6DF769DF368EB3B671BB0EF30A93A4616D39280A3362F208ED9939ED1D4A773435231BC9DA6669D753C85A3E030D6F96727B3D747D3C6A7D97DE5F67FD19C0F97C4FD74A036A7659E25FFB664F976B6F193FD42CFFF7666562616FBEFD275877EF2B4DACD67F369A35031BE8EEAC3F356493DFB6G9E2C994C11FEBC5EE66C91F512F72DD64BC35DB64BDB398B470478E6186CADD1AB223C4716E3FE9058E08440DC00C5G8942A6476FC21B24B770357E74082987
	4F8B70D7D7954E6FDA29EFBD8F147A7F2D56FB4839E627DDD16CD624AF4E32CB51D6943B03A0A9EEDCA9760F535C532B50228C7FE8A95977113B891A2E834CE4BD16BDAD4ED0B60D2F8D7EDE4777A91A846EF502EB05F42F405512F8F188524153F8BC745CC96ECB36C8F4AF984B503F78C3FAB89A7B641131C567467752795C187F52B72526371B12B068541B9B43275179A7E11AFB0E5F16D810CC470F35CEBDB717C5C2FBA2073F5BC16BFDF0505F1C5E2E21436E909D72D7CFFBC7146D87FA54D37C48C8E286
	921B95125FBA9E9B290D594A89BEB8D29B337D43E19E3305C2DE4A4B5C169F5E0D731E0B79D0F6B6DA6E5BDE1FA877EF2F2144B6E430B35C6CE77E241C4C6B5D5DAED90C0BEF15E7895C89D72B5DE54611C8EDA9205977B9481344EEFB218D4A4BBC369E03579E189FFF27CE7F3C65BF099D07C21D0817394D1EA957BF5BB013B64C07F20BDE66F3132B9AE30E2CD7628CA5G2DG3DGC3G41G61GB1GF3819683C82FC2EC8CD088F0G20852083C0F9156B3E833714FC4EEDECF30FB89327510C6E042D6983
	C9BB46821DE7846E815AB640E17CC06F844D7C88F5E836611231117E2A33A8FF094C49B1D0FE5E2BFC7DDD5AA8893B5C2BFA1FF9782B1F6F5E79CAF9D09BB93D57EDA3E76BF0BC7A585B5ADB0DDBDBC53C1E3346DDEFF779486BCD58672E77E66AC86AB13FDEBD4C9D25EA7F636F09EA7D796219EA6F79E2D2357EFC91DE2D1EAFA8EECAB54E53BDE0176D225CAB82F7D160E6901A087B4CECD2FA857E3BC4AE449F1711A147FF1F4855CFF06D8C972B42DDE4389FD7238F01A6775A1D3C6E1B55B867F41BAD961A
	7E97D246EC8317A27471F3A22359E1D4493DD060D0EE9A639A372BFECB04084EFE57EDA0430D672273754AA122BFFB7FA92343C162973A2F7360A13E2F13G63ABAB8A46BFC0C794756F54A73FFA087B648140C7C671D87B1FF3070935F9EFD3B96856FFA3C4A1B20D357FF408FFB73AB80A475A09447797CC0775A1C06BF4BE5E3F63F74E529F2BBF9CE5BD299E6B75CE77BD478DC1DE10AA1FFD67CE648F1B2E1D6BEE93FC64F46DDC17AA663A9848AB78B60FD9F26AD1B701108E91B8BB4105C3FA2640FD0760
	66C1BA7E5B628EADFE26485A9128FA4F78FF700D5B3FD29E540ECB6B3D77C50F5413FB35B378DCF9DF3D7E1E673375FCFDE792E56756A3ED7DBD4F5F547331214C20FB1E78BEEE067A5C79EF75DF565A1B521BDE6F394EDD5340EDA48C78AE1841474A27841F9D8DF3BCC0D334382B5FB0BA1B7F642E6BD15BFE1D48F1A9DAEB73EF90FCEF3456665BC4FB94481BAF640EEDF05B6ACFD56FFFED786A6D56751572207F9F1E437DFF06C6C77CD6DA6EF2CAEB3D675606716F3C9DF139A9BBC9CABAA12D68A5CE7304
	343C9F3D7B00634E079FFE08603AA65DC65DA46202B5560312B88D7CFE6415659BE7DFD1122494AFCAA724448100418187507881500817589F27AC45A2A8528AFA0BC41112BF157BA1C9AF0E28DED2705A07A2E5E2B0D8603E7B20C8FF2177A48ABE355BF6397032045125183B8A70FE05390B15790856F2B8BCEAD26CB93355831BCB307C7606C7D68ADE03D124CD0C63B9DDEE2025CC212593E442DDF83D32B679D75FCC26AA22814889A9852388501413EAAC9982905413142286C5DADD2026DEAB9407EB7113
	4325A4715BB7F7547B4389E33AFC1EA9AF7DC76AEB52E2347B43FF9CC4DF5684780E98710D7AA05ED372357FF04EEA34D8942365EA034D04FB91078795593D6F7161A071033C8C4BE43F23F383653E222A73FFD0CB878882F14A0809A2GG38EAGGD0CB818294G94G88G88G41DAB1B682F14A0809A2GG38EAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG43A2GGGG
**end of data**/
}
}