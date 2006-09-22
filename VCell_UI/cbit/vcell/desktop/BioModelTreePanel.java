package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.*;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.server.*;
import cbit.vcell.simulation.*;
import cbit.util.Matchable;
import cbit.util.Versionable;

import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class BioModelTreePanel extends JPanel {
	private JScrollPane ivjJScrollPane1 = null;
	private BioModelCellRenderer ivjBioModelCellRendererFactory = null;
	private cbit.gui.JTreeFancy ivjJTree2 = null;
	private boolean ivjConnPtoP1Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private BioModel ivjBioModel = null;
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemOpen = null;
	protected transient java.awt.event.ActionListener aActionListener = null;
	private JLabel ivjJLabel1 = null;
	private JMenuItem ivjJMenuItemNew = null;
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
	private JMenuItem ivjBioModelMenuItem = null;
	private JPopupMenu ivjBioModelPopupMenu = null;
	private JMenuItem ivjEditAnnotationMenuItem = null;
	private JPopupMenu ivjEditAnnotationPopupMenu = null;
	private JMenuItem ivjNewApplnMenuItem = null;
	private Versionable fieldSelectedVersionable = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemDelete()) 
				connEtoC4(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemNew()) 
				connEtoC5(e);
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemRename()) 
				connEtoC6(e);
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
				String newAnnotation = cbit.gui.DialogUtils.showAnnotationDialog(null, oldAnnotation);
				if (cbit.util.BeanUtils.triggersPropertyChangeEvent(oldAnnotation, newAnnotation)) {
					((BioModel)parentObject).setDescription(newAnnotation);
				}
			}catch(cbit.gui.UtilCancelException e){
				//Do Nothing
			}
		}else if(parentObject instanceof SimulationContext){
			String oldAnnotation = ((SimulationContext)parentObject).getDescription();
			try{
				String newAnnotation = cbit.gui.DialogUtils.showAnnotationDialog(null, oldAnnotation);
				if (cbit.util.BeanUtils.triggersPropertyChangeEvent(oldAnnotation, newAnnotation)) {
					((SimulationContext)parentObject).setDescription(newAnnotation);
				}
			}catch(cbit.gui.UtilCancelException e){
				//Do Nothing
			}
		}else if(parentObject instanceof Simulation){
			String oldAnnotation = ((Simulation)parentObject).getDescription();
			try{
				String newAnnotation = cbit.gui.DialogUtils.showAnnotationDialog(null, oldAnnotation);
				if (cbit.util.BeanUtils.triggersPropertyChangeEvent(oldAnnotation, newAnnotation)) {
					((Simulation)parentObject).setDescription(newAnnotation);
				}
			}catch(cbit.gui.UtilCancelException e){
				//Do Nothing
			}
		}else{
			throw new Exception("Enexpected Edit Annotation Target="+(parentObject != null?parentObject.getClass().getName():"null"));
		}
	}catch (Throwable exc) {
		exc.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog(this, "Failed to edit annotation!\n"+exc.getMessage());
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
			ivjAppPopupMenu.add(getJSeparator2());
			ivjAppPopupMenu.add(getJMenuItemCopy());
			ivjAppPopupMenu.add(getJMenuItemNew());
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
/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopy() {
	if (ivjJMenuItemCopy == null) {
		try {
			ivjJMenuItemCopy = new javax.swing.JMenuItem();
			ivjJMenuItemCopy.setName("JMenuItemCopy");
			ivjJMenuItemCopy.setMnemonic('c');
			ivjJMenuItemCopy.setText("Copy");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopy;
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
private javax.swing.JMenuItem getJMenuItemNew() {
	if (ivjJMenuItemNew == null) {
		try {
			ivjJMenuItemNew = new javax.swing.JMenuItem();
			ivjJMenuItemNew.setName("JMenuItemNew");
			ivjJMenuItemNew.setMnemonic('n');
			ivjJMenuItemNew.setText("New");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemNew;
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
private cbit.gui.JTreeFancy getJTree2() {
	if (ivjJTree2 == null) {
		try {
			javax.swing.tree.DefaultTreeSelectionModel ivjLocalSelectionModel;
			ivjLocalSelectionModel = new javax.swing.tree.DefaultTreeSelectionModel();
			ivjLocalSelectionModel.setRowMapper(getLocalSelectionModelVariableHeightLayoutCache());
			ivjJTree2 = new cbit.gui.JTreeFancy();
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
			ivjNewApplnMenuItem.setMnemonic('c');
			ivjNewApplnMenuItem.setText("Create New Application");
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
 * Gets the selectedVersionable property (Versionable) value.
 * @return The selectedVersionable property value.
 * @see #setSelectedVersionable
 */
public Versionable getSelectedVersionable() {
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
	getJMenuItemNew().addActionListener(ivjEventHandler);
	getJMenuItemRename().addActionListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJTree2().addPropertyChangeListener(ivjEventHandler);
	getJTree2().addMouseListener(ivjEventHandler);
	getEditAnnotationMenuItem().addActionListener(ivjEventHandler);
	getNewApplnMenuItem().addActionListener(ivjEventHandler);
	getJMenuItemAnnotation().addActionListener(ivjEventHandler);
	getJMenuItemEdit().addActionListener(ivjEventHandler);
	getBioModelMenuItem().addActionListener(ivjEventHandler);
	getJMenuItemOpen().addActionListener(ivjEventHandler);
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
 * Sets the selectedVersionable property (Versionable) value.
 * @param selectedVersionable The new value for the property.
 * @see #getSelectedVersionable
 */
private void setSelectedVersionable(Versionable selectedVersionable) {
	Versionable oldValue = fieldSelectedVersionable;
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
	if (object instanceof Versionable){
		setSelectedVersionable((Versionable)object);
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
	D0CB838494G88G88G4F0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135FD8DDCD5D53638753068C906E505C593199599260DBA6148CB1AF0726B1519E3B6431B21F25ED8BFDF631BF87FBF3D67CC8744A44154C423729523220008FC29178F150F8B0203C90AC88DA997B848554B3D374BC17123692D3577D97B1EFB6E39DE246257721C3D765EEB2D3D76DAEB2F334FBEB7A97D3330D509C969121438CAD27F76A7CA1233C712EE5EA06FA0DC303DA5D5527C7D95E003B426B1
	992EE1D0173D28A5F53DE4AFC8017294A857AFECC97D906EB7CAFD8501B25C7860538EF5769B326A2F4D6750D662331A15635253607ABEC0A460701A6CC07E896999AABED64593A4A6810E355938E90B0A2B07F2BBC088C00403370F016BFB0C7370B2135A2EEDD43894BF4715349669E8E892ECF7723E756AB5D37A6F4465CE64D531A9CD6DB38C4A11GB878151249148B570A2673E78FF6F6D8157C8B1D324D165FA5771DD79C4E3232EA2B632823CB36B53BE43949E217ED12E47B866985ECCAE17DA529844A
	898CB72D8AF9CC027BD9GF1823F5804788AD7CBEAB3004DC563BA3033C2153B754A13521233B35FDB67E3DC81AEBE1E8817FBDCCD399DABBFE2F3705DB65475D228CB6BEBC94D84B083F48184GFC42667E262DB84E2A8E2546515B6B30D7B91D5536AE979B9B5C1715C10D0ABBAEF7BADCDD12446737FAD31E01BC57825E2F693A7BF11D049753DCC79CFFDC0A1DFCD752EA831D4406DFC98A6D633A185267568967FD0BCF5E8B7BBCED511377A9C94BDBFA3DE875AA835EDB96952DDE2A52D921615D2860DC1F
	2C46395E827709CC6F124471E799BED80367F6B903598C1623212E2D1FEC2375FD1225B1F92C343E70EEC3BD047CA2A8A9211F4B307E02DE1604D2E6F797B84F9CA14B26B24407E870DC165FB2BB45F2BC54D58E3424F27B8A4CC2BF8D3A54129A8E908D3098A081A07DB2441B4BE4E34FEF5DB3C89BEB3238146AFE2B2DCBF6C93CEF60364DF01587941725415E27D86C1DF20B45E535F45864BEC9EDB3EA9B6A2043235FDD4C76BB0147E159AE3BAC0A55E1876A16DED911DDFD3C4D7D5BB6E09B174CEA1B2D3D
	B2B068F5CAC47711ED28FF1B25CFB9696C32A8323B852F1F388D6764022585A882813867FA193A8375D58F63EF87F08ABD64EEC7FAFF13DD90AF4A4A9A9D0E737D4EE69C0994FD99759C46E2C7G5C07DEA69DBF3193F149D7DAD237DCA1BA166D9FE868182D1DA83E45F509FC6C6F555B61DAG6D1BG86GC6830483C4834481A4GE4DCA51BFA3315EC4A3CF60214772BBB8DEDAA986C3B62AA3725662BFA5F6AE5BCD16E2BD0978A9085908BA0FD8B3187408C5073AD2D8FB32B02F574D1BE3CC6DF71744551
	2A3CFA5F0CD3F1836A3540200D91E4DC75EC57246947E5DB66A5DB2AFADDEB909FB69870D8272107571503106BFD555F3565D51AFBEE8F1F6520ED85019E4381E6FDCBB6913ABD137BD83525CFA62FBA668207103B5C964247771752A63822CFEE8328817892606FGFB81CE82E4816C87B88C30DB623EFF8A20902081ED8F608B74CB0083G17046EBEA9C35AF1A0D74ABFDAD28BGDA81068104834C87C8G487C8E728FGA7C088C0B440D2007543DAD2CDG6DG01G13G968FA31BD9E22ACC3C3E354397EC
	D347783EBA367755F16ED147783E2A0B7D1ABE87B477B56A7530C69FD47705FABD296A0CF42B17C19E242C17FE0071EA016BF2FF698F254BD5125B87BED22F18D7224D7F2F64769B2DED6F54490357CDG1FC9BC1F544E9379935AB97ADB2A64ADC316814E88F28659EE3040FD6F76AC14CF3EA05B95336C146DDDF05327560D49C21955752B5BA17A5C4F70CED86FEAE0016993780732D01796C5F1D9BB7A9579042CA8D67BD9F77D84462B2F3F4369DEAA095714AC1C8B47C5D86FC47BE9D938D6F4BA6CF61985
	8151C52D1F4E56AA59A6770250B50EFE3B4271CF6F40FC7E3CFCC9DDABE76EF8D742354E560F6BAE4F11A7D9F0AE1CAEEBAF0496DC3A25269BF09DFBF2D306C6C79260B0575F8ED7538D3466DC5CC6EB4E764B0FCA51E71CC9C6B9D548C337A5055C206638B738579CAE534FF69A4B9E4556CC955F47E5BDFC8675EA1036B3BB5DA83D5C7B09263E86127D63B811AE59D5E769D49C303872F6EFFD40748F4419BAD96BC2356E30ABC5C303632CFBD0C68848CD8EE73F7328EC6F67785F56FD2013C7A525525AF79C
	7B1D30766A7A3DDA229D07289463F84F043ED75BE5D52A6CF60742739E510A2FA12D39C8FB9E68B38EA06186DAC3CC59D8CFF9053AF854B8EC6AD8212E79C61C5F486AF44D9C2D3F114FEFA5DCE3D87D569A2A8F057294359E4B3C7ED9D11F76CFE0977F642E6776F3402C351FE6357EAADC0381388CD77234B204FB51F8A44F08865E4741BDEF931A6F2BCD02E873F0212FB66B83C8EFD9B96B19CDFC53AFC3FA0A895D5F045F6465FC202BEBD1FD08571764208F59B43960C0G72BDD3C64F405981BC1F2A8C60
	3C5B82B8FFE9B8367B381C5A458428B9D7G4F3916296DB83D57C53BC668578590B01C530B992EEDB721025AA55F84B9A7C045CDC66D3EB9C06D2EC27DE800A9066D769E24F671EAFD5A4DEABC38D95B6E0F22DDB360EDGC1B7FB524B8E44F6138FD13BF9B773F1AE01EBBCC0BA54FF0AB6475A5DD8C96D14C01EFB848572F658BEDF682F310A5AAD8D647C24DBF83B8C383681F03E8B2A291DD32D8F3C054F4B38DBB8DFBE0E509AEAB7C5EDB76F96CEB7651F3563BDE326F61500EF859878E75EAE52235D36DA
	21BF40270DE8C94D9B612997BE0EBF08F6B635BEE084679FBA02374B8BE2FB91F5C23E91BC5F1AB702676CF1F04D00B61C6FEF8ED33B81408583CC8AB2126F7EFAEA379C6AD3EE75556EC40330E7E8D369335D6AA3542E9D5A8CE85A055C4A5B71F87ED68E7AA55B1FB9E739E099486F3B886BE4D95991965679DE4CDFB7E3BC465CA36ED65AF359DA0B314FBC926696C099C96B518BEBE94FE57479F0A9767542553E761202C6F2B9A60DF42FC7BD4B2B5E6531F56CAE64399C6A245BA8BECC4C51BE77D57575B9
	BA2DACB02BB160F42EF63D62384E5CB5F06D363A7A1486D80786A8DED8F3D1DEFCA6D5512ABE3664EEA5FDF4BAFA732D9D3D797D97FA73AD9DCA3E8C0BC1D999ADE36ABE559F2BE91FAA7DB6524137F5DA9DC42D7BB6A955078E9AEF63E3375D6656C1183DF9B55FAFE828C9D3E9C7C07D9200AD41083F2986F38A749B4E2F33187885DFFEC8EA3F739E1F7C5A03B91FE141EEFEF97FC8D845793DA978C5C27D72E05A439B35532D93779EDE0755510BEB27FB930FF478EE46969FB6C58BA857DD3FB9C515B3FB94
	595503E614219D4A83G4123C80627F78EFA9FD1150553DB1807744201CEB440E241E7F6BE6217C3B965F6F80E3A1D70AF96A25E8465A6009EG2E1BC0362703328C83DCA4G4F937ED846E2A330BDC139CCBE69E16D45757A3A6C1C96BEE7E3B7D6D27CFDFC2CD4715D69A4233D4940F5BDC94BEF67F315F107FB4E86EE6FD8998440E52BA9A7593A20CD689DA45B78F2349325475A474B8B4AD15F9D2A2A389C93BE554A11E299EEA8C7506BE713962A7CD7E86408839962059C9FD4109C6651908BC7D30EB1EA
	07B64718B21A6FE7440EA6B9AFD5944155421547AFCD322B5B616A153B32510272AD97417D7039A03F0AD55762FD8E69790B0BA5F0F5496065F2152EFB236F7E65B9ADDC6EC5C26E65A0D3761DA4F74FAEA3395B6FA4398F9C982A5C1C6FBD87082F82B4A3845FFBF79A71DDA278A69E7CFEFCCF9FA43E89C03360AE22BB13612DF62BE235582C17656C9C2E5F583D38A7A55BB12DEAF6B8EC4DD6E79FD72C6A5A79C7506F04BEB38E917D9E209DF6970D6BE42E5138968B7E5F9D7AFE630ABDC471EB99505CFEB7
	512D29A4BF60F2FCD52A15238D5A85G040B7667ABD176DE9EC6543D6BBFD5E97DA4F2F510211F74BC921294F5B7770FA5F73B7D246B010E15838F101FBCD6C9F21604C00E96C2FCBF28C2B9AD2E33051C6755AF096730F9D3240C0D379A72C4989742F94D8BF1730C886BD8999DC6BC7B2BE8CE360C017998C3BCE32B0C67FCE4AEBED7BA7B61315A8ABAEEF2B91C32CB39247A134D8213FE2C639C8435F2EFCCCE8B0F6F9B585A08740246D09C7D1FEA9AFB8C60D688B9C2EBD0EE4F67C1FA96636B9D4E9F27B1
	330646D2F28F686F9E2211E75E4D662D3FCF4EE77F026C1D673DED67A856116928B1E4F3CE1E303D3EE3F65653ABF838BB8ADB8FB413AC63C00E58FB4856830B74360EFB3669770AF5437C7DE21E399447AAFBBC1F7A89F69279C9E3AD491C8D72345FCBB25FD1201799771AC3044C13EB3F1FFF2E2F93798C50DCA268DE2C535BA047BF40701D1ABD84CE6755BAE14B21A0FBA865886987714A76D6443C3AB6603C9A6DC7E4EB7208BE59F5C1F6651F04EB8E375D6E4314578505D21E2518489FA396CC15DA4F0D
	74690FB1215C8F17053A7DF1715DEFA4AD8360E3C82E27B1186EEBC9F55EC7F330B947A8C6065FA7E2D37D771B03D902EF94500C97FCEF592D670B6F9A72FEC2FEFB35017AD582EE60A754EF589EA31B19A47A959C21FEB100DBA17A5D18E5B44E127BE9AE6D4D18531E43E431C111FB0FB9E57B196BDB5B797E6C0E4D2D3A39293F5F7D1E22CDF32F85A76083F4F5A1D00E5061787B3E3B18BFC55F4F5F772D9478B0061F2F41F31DE457129F44C11DB90C5ED12D5B034FE261D00E8108850887C8FFG7472GBD
	837DE7EE3164679DE003DD01C7A40B6D044766A63D0B5B05FC4F4B175472E33B34BE43F596DF6D6B7DEE5383467A6272FF4B62C7973499869082B089E03E103DA84F2F6C35830632F3390E5520DC4BFD48E0843C5F572CDF4A584177637E1E731576DCC47206A3C9ABFD783BC925DDBF566D6FDCA7BB58BA908878F1GD1G8B81624712CE2E66657953893D27D515214E5190F511E43EDEDD70FE174D57FB5E007B40A1E66B698F6A5F57FF41778F9E543F2FFF54CC6F6B9528DBBF2EA515372F5F0D7808F1FA9F
	BB4E70D16374BE766BBC7231F9D0D7709051B9407611C7BF445B2F92F46A99BEDC0367F41E4F9771926AB29FA6BA070B909F7030DE1EAF98FE7443FAF9DEAEA4F946C1DD4AA374AEEEE419DE2F36C7BC67A6D0D7667DC23D7A656B4A913AFE5C9E2F966333F98C544581A4G240737241AG1A81864249AEE7679D70E717AC50B6BBEAGD76FF01C2F513F9B9132B6148FED0C273C7AFD2AAB07049B0D712D929C63A4281B873084C0FA941E87GAA813A9E25B1FE3C3BFC10E364AB0B7788391C9995C39B5F8E2F
	FE7A7185BC6A590F2F0D33D6217C867BAB34DA2AFBD333F6D07E9D7AA859DB6C90E5FD432F2C8BF532F2996E5FC5B2AC93B2049D981A8C0FFB75534B30FD3C118C8B58FBAC24D1B11EE468BAB8B499DCDE7D74FE270CB76A77692121714B736A279FF310611803F2C96F634418DF75222587E399167B6D97BB5E48CE1D6FFDA4E9776C4E76DB41C8F12B204EE26F4477717C5D50CB355AF7C3716379391D4C47A06F81606399D50A6341F53971B14A174E6DC3BF8F00F298C0A440C2008400748808E9916467365D
	55123FF3D3742298FC1DEE8D829A57479315C353638C3F7D1AA20C6259527DB84E9E288BG888508840885C8G58B20146996DFF1C626856E046F925EAE8B6FB0BD7BF3D0F14CCB06A3719651D0D9386171B34CFA03BCE2C995AFC2C735BCFB114F32ED73F416965E52FFEDE3EEC48EF24D76EB5489C412B1F1E5F3889C631E3C691BD7BC6899D9F2F9D1A8C6DDE7D74B2449B4AB0270062D75A63A443663A214D73F63F7D4C0F9B75DB6A55EFF0E37EFFDE7D74765FE6486F7CE16C27BCBEB87BC76071213B9463
	C3A0749B8790853098E085C0464416540A09949FB6669F122EA3BEDCB367D9D3BFB45DE4F875535BC35BC4A3FBB81AC37620CCA4FBF8EE08B2447A15A142D006685DA4C30C106146062149906C55CFAF438AC3991E5CC3B224CFA299769D991A8CE62FFEFA999AA79949B0AD0BE46802FAFE66361E3DA38E0572A4019B79A16296C2F91940B53333BD529330EEBDC138BF34A12EG4AE601CBE638D6A8BB85AE6BEB440D06F24493B47650DA5C9B0E966507D9997773F9B93FD63F7FB394C07AC6F84E63708F4243
	5759D66E3B99FBFC1F0D7BEC0F7159382CBD46E763F66D59407138312F59E5217A62BD57BEBBD73E47686C9C1F23549A1C2345202FE5CFD05C99DE60DB762082E359EFAFB016BD24605A32079578127DFF190E97A561F3382B5F2ED8FBE5C61A672A72G1E170488F83C5F4E43A3D26F536F41C894F39FAEC3BB4B7EA90DF1FFE111649DDB717C1843C5C41AE50B6BD8F737C83CCEB865CE12BB3F48370E7696996B283C48D8C7D5C55756D1C311AF9DF1FB2E2D4577FDE698DBAB0093A088A09CA086E089408A00
	0C4964E327D87B82A8B7CD66E7CD70DC941EE54273AE4E497C9C921E01626DA71A69DCB852C3BEC6F63FC4656D4BDFDAD519709EE539DEFF8BD24761694B0B04ECEB2F61136BA477F9DBA3C01B2273357A336803817DF9F8AE5B673B516602055EDBD9994F8A295F0A6599452160A16846F72CFB5B1047E04F658F0EC78DDB63A2C06ED9E22C1FB39C1EF74233DB9C772B7C6B0D3B3386210B88358D677166F573D8B508B624AF4E63E8914E639237BF31B21EE563655F958ED606A5C318EB4EA3E150BC06B257BC
	764E5E0CFE6304D0A577310DB533C53128F5FFD0705C3A57796A4FA19B1568BD704C327754B6350A23863FAFD03F3DAB769DBBEFADB60E1DF79423FF77B97ADD1D32BEBE5ED31CA16A5C79AFEE2C21B83C4D032C3FE2F11D153D626BF8561F57796ABF3958D78C66E39DD62E996BD113207D67925FE3CD5EEBBC56B5FBFD0FF543DE7FE37DF82F6F317EF52F7F31666E3D76D80FD5E86735C458643F6E506727A9D3E09D19625B46B93D0D873474768AFA63F6696995GAD33DFFAAFF9505BA7683D34D3CF2F95E8
	59F47406E94A1C5E3D873574768B7C49C3EB3476DDC8FCC6656A790C861A61FE657E281278B4A90EA6FC7746F31D333568F3867B47A7BADD8E1B8DCFFECA146B2F2B23DCBFEA8A1D11FB08FD47988F653429AD295B27D21C3A2B94652C18EAACD793603BG86B4755CBE14FD3EED7A4ABEE31B3EE11FEF1B8E5C675F26C76D73ED53A17B7C5B74837BF0FD6F30F6D9DD6A29FE2E732515292473EE172337E92FCA73CF7BFD0F716D7D46E3FCE73F6FB13EEB72BF46F5A65FE35CEC72BF464FCC46E37CE91546CE0F
	221D725CFFB7BB6F9384739BBA151E598698AE824AD1D3698C0329C6EB170542DE134CDA3FA8927819E68F3FA8A23FB8E2B25A571967437E5CFEF1CB2D873DE22277FC011EDE9C500A77CB6FE81DB6D63B657E6C3007ADC08D57490D6CECC94A4FA0077E99BDBFB8CBFD5B4745D2E37B78365437FD84147A370FDBCBFD5B4768D27F76F1DF693563FAD53D071ECBC84F47F374FA4E86BDD47C4C1F1ED7B5E86D8357C4759B585DFAFACDC02B4BAF3DE99E74CA853D28BDFAFA83C0AB48AF3D5EA39E63ADA0FACFE5
	696905812DA9FE691D590C5F0A59AC179C7D0AA4E5BE0936F3E1B33DAB19857D63D49AA9CF72F7A63C5FC39F225D75DA862C3D7D3DA72C17E5E9926B7BC9865ADD8F5CABCF0A73C4AE348127CB6E16DDAE390B3516547681EC2FA69E6E25C8EA7F17B614294351EF6F6A23F61B583ECC9E34B1C552791DA7AE228C5DAECBAF1884FF501D46E4E8A379ED50F698C098C0B4C01C60D171B5135FEA57495F4C7854831D66E954D612F0AD522CDC051CFFF08F6A7E0265DC73A5272C2EFB29AEBCB367778C3976101870
	0771E5EB35366E1117C69DC3DEC79AAD9D6EEF58DE2BC8E5F2497098EED19CAE4277FE09F40EE84FB589BA3FD156E96B4C32CDD6E4356E76EFBD6AC0BE14C06D1731D1DB67BE9028569FDA63A9CB110AFF5A69A1CB0D43F9C948B2A7B1CDDB7739FC111F5B8A2E551E5B9AB60D1F5B9AB70D4EF535F81C6B0AD46B711E5767647A2A67FCDFA9711089B7D0C45DA71B71BCBDCD93B5D14F3697E19D08D965F45A6C1EF5E5BB71378FBC3F65932F4A55183C043DAB0F83F96227515A154D5E9B277C9C62744F89B71D
	3D4F5E8EE51340A567A12E914AED8277A66B5B83652B8237BF9BF141D08E9338D0060B0072AC018BE76FEE97C2F929407DA7432D00F25AF44A375245FB060C695E31A44FG474F28FC44FE3722F23A7E6C4AB606EF1A2EBF3B7218094E2E34C3DD4CF412E982FBA71B9D8531BB0AF0674BE95F36D1606E2AC01C934A01829747F021D00E0CA2DFB6D753796795D16C1CB6AB470A7A5B283E0857BF444A157FC2B4ADA624598C65AE017BF7769E498965G015BD3083890A8077F8B519EF3581377C4D61EA27AD859
	7B0798A8AF96381FB25CB2A82F9038D8760EA065A9F8CEF9CA6CDB67337DEDA8D78A5C0BECBF23914A6D823705492AC0B9C060FE15C5F699ACF01F6EA03B9C77941D337C49E61CA31C37C900DB7A94456142F60FEFF17034E4D959E117352B5AF236112DACBC277F8D6B1FB9836C9A20E9861DF99D58C15F96C5BCBCD61A7FFB636F39621A3FCD6A19416DADE806665CFBEE474AD9B370F70EFED7A589BF2EF5654355AEDF4C9487B84D56DE590EBF7E11DDD4D8323F44D43C2FE4FFDE267BBE074E6405DD541E49
	B32D395170FBE70434E8D58E759A744E18C4AE0B3D46ADCBECDB66B5E4A9A8484BAC5EDF18437B3DE6F6772B7A7B8EFD3F6310A3D96CE7EDF2B674027F0ACA601F123D45B974ACD555C65FBB3502CC415108F334E963A1529E31123FAF64758FF854E33E054FF16E7A97BD6AD1C72FA4EA6BD7FD2D2DC7DD0DD07BF3FD4E4D2279DDB1F712E41B9A6073371F962BFADCC1FAFDEECCE2CCC8533BDCB7C76B5C3AD9D56BCF27700F7A1D41DB5DBE3EBFE42BA9575B2D75F4762364E971CD1A62639BB3314AF1D916B1
	7F69354B3D8E3B35136791EBD9EC1E8DE0FB1A7C3F5F443EB100A41B0F47F958BD1E853B3CE93C4EF07F86F0D55078A44F13469D759A1DE4FB535842F03FC75B94B4BE4F7624B13741CDE3F2B64BC15477323D3D96FB9714F93BB70F385B4D4875663508611E8588953CD2F6F9725A5908B4F80C59451E7BA320ED745394F714BA0A6D8B1F26B56833D224317D97D44EE1E57CDED6BDABE37AA15E0D99011B671C9F0D47CA215318D26DB7C1FC0D28E25F5719C08E1E5B5ED8CE39EDA060C28122GE281D67C02F4
	BE016505AE07C3E93176D9710D921F1F1995F466E24BB354767FD7A0AD7DB7B17CB9E07D816215876D4DG43GC6BF433FD5667D1D5598AF363293D779B62BDDBE21DC3249DBA5727D8332B61696DCE974F93EB962996E73D14F38E3A967B11DBD5FEF2D42385735156386F43EF46BD137ED7E39405BB6538B49B61709315795FA521874391B46CC36B65B7BED6AEFC38C4B7126F9B743BD03F3A5E84E4E7124595A64267956EEEF9AEB72C92E349871EC126FC9634A97EE9AEF6C7126114846BB9767D550A8AE70
	24711363C80337DF4956765F613CC2FBDE3F2A996B55BD4C4D57FB8E1B777B69F57763B961318FB1AF280861B9E1224037B2FC3D864F7310D7C49E528CF511B1F4D6EFE9B35A6B161910DFGB48174G84820C1BA93E8FA807C766EB1FCFE9F6B89B658B32CD73DA135B7273FF575A727AFBECC9A417FEDC11B3398D474C541F557F2285F51FG7874DF12CC5B2BF7413545F24E7B0DAEA06933B80EE53FC8216A3B2E6DFA754D477174EADA0F277CFC3C94F9135967375CA53FD41F2B54EB643CB109F865E319FE
	8A759DGF5E1GB1G31G4B81D21E2531E5572A5F9E30C7A3C531F47630AF6745CBEFABBCCE89B4BE4473315DAE4E4FECF9567D0CD1205E1B615A7A2C673387377B6DBB713A33EB27DA1672D859CE6562ECD6F6D179BFF2D939134A55F5387760F0DA3E410FD5383FFCD9C7F15386BCA7BD0B3891F566EB68799D750FE27BD568938C30C9AD0FFB56ED531CEF77C6644B42E89161CE15E3C20BB90ED96E36745B947165F93686F934DFE19F4DC25D67D0173D556630F4C11E32B52F5BE26B13F3707BC31E630417
	53BEFB4233149B46156BF343D112B6377B20C21F9BA6FA541BAB743961FD9E7597AA7439A175D7F3638A9627DC7DB277ED5381FA8EAC18857EBB0BF231455568377ADCAC9FBFFCAFAB5328670447CA4647BD6CG4DDF58ACFA3F904F3E0FDD8C6565GA933A9067C369565491CCD76FB2EF16BB5FC933FCBB82138C0929A4F2EC2CFFBFA1CFDE73853C2653F66FB56E7943032CC650FF73032E3276A63A771A702547B531A7BBFF8FDF376C38267A1FD78E372D03FB5EB763DA71D50EC3CA77D17E663B3A949A72F
	FD26A86D246F5F635AF8D27BFBDC5C961E431F03D36B9FBC19654B8EA00A9D8113649FA6B7DB4E0ABEADAD6FEB656121DB2D7B3A857DC2FC0F4A760B38A5F15E3F798A6BCB408E1BG9400C000D0E1172F3420DD72D3657A18DA5BF59677637197B8956297F34A371E671E727F3B1D8BCF613EF307C3D19C3DC81F703FBE053622B81C8C49F13FBB057363321E6DD1F8D3957F7BD36807B6391B21F93E7FA8FB6E0A04F1AD1CCD4FG27CD347730D4601EE138F8A8274DA15C2BACF74A0432E90E48C76BA81EFCA6
	713DDBF696107DADBFE404D31F575B30445747D752E8FD5C7258031279A5F352B64947EF2B4C61EBD0689C77BA5F5ED9729E771F899E5F96AD04B6CB67DC7B6C846FF7506B9B94BD2C5051617D9A7D76531F3D647D06FB744B186B7FFC874F4FFA58F77DF9F375F95BB78CEF1A2B4F5B7E3B1272B6B354054FA5DBFE3595573EBEB50EF25CEBDF793657B73E72EF2FEFFD45FE27876C8CB76755F87295DB5B3B3B7BE46197FF7EEA3D40B574B5B97A2C68095C16226BFD3F37F81959ED5A3C16540CF914C3AF7E9A
	2D07777D3549787D481F189D1B21CF53BC325937730D5FC5ECE0397ED5E897B80F5627DF1E463CE38A1467834483247DEBCBEAA600892091209DC081888298879089B08FA08EE0B9C072F3A073F3247B49ED189F78DA5F54CDBC96CCDC96AB06937559BD066592F9C067BE011B4DF0E640E18E414F5CC4EFC49DDA6D78E8D843FF61D7348FE56BF2BB349FF80E1E336DEC8F0F5FDF545CCBD5BF7EFA77510F4A035B4858F6370D1CB021BF7A58435A3F973730F2285FCC0B3B5FCB79036F3734435D2FB4EBB07D54
	38FE5A377F3DF25A3F7F3DF15AF83DD8FE5AFB3D782F53466B457F1C562E979CA71D41F57AAC58E52BE837CA603E9438B5E75ED3F1272CDDCA0F402FBF33C645574B48D01CD5B803F2DDE438D69577098677250A4BBC03B184065C63F009B350E7F04D69365AEC3C1C435AD86D90D204BE72190CD627C5A3F73140215CBC4F356C507CCE8593DD7D4D300135EEBC09CEFBF66C6C104BF171284569E4798B5F3F19AB76EFC2413F26GAC87C8FE1E47F79E135FBAC0B1F98B601B1F27FC7B390CEB645BB45BDC8E3E
	45DF83D9084C736D720374ADE4507314EFD731589F8365D8007867495F5F763BE6997B6AEA2FEFA487572F1275CB1B6FFB0D4B043A92CDBDDF5326B2796B676B573A27983EF93EFE2DBBAC563AF6280B19CFB94BA9261B6C97DAD2ABDEA05CC406EB04F217403D4DF083D09E7D8269EAFA25A446912339BFDA796347466E9F158737E37BC16F7D4F61875137A2DF20357217870D77B6FFFB101E71D296D05B75071036715EE64EA1720D0C85FCEF936F8B96E85F935FFB7007FA7E66747E6B2067336E970748C66A
	016F308564ABA399BE924A738116AE90E7F67DFEA3EBBCF772F57763361D44ECD8FAD1EF73298C1F76225E66C70B71E4C05D5597C96E940D2DEE505C5FF36847375B49BFAA8F7E7FE4F96D488298F444FFC82B7ADC52875EEBEB554817DF0AB8E3CECF12129A24F7FBD850EC10D6DD4468BE607C6E1DF75EC1687BC67A876AA6919FD8E7F9D092EF7D1E19F4EE5547F34EA9C9D232972586A9F1G60520093E84C44C91154BF22AC4DC2D024F779298FC5DA7939FC910A7C200766A6990FE9A852BACC86B35D6799
	94E9F38D7E8F9864CC68170907A02C1DEA45C65E546974E84E31A72C3D9E588C0625A3969ED549F8D685063ECE8C3D012D22F1970A4BD63E7660CABEEE1ED5B4C849189A4070DDDC8DABA12B4041CB49DAD02477B335321689ED6083765F2ED5923F51721DF65FB7E1F8274F774136C5BCFE8CDB245F778DBE0A71A98A700B97D16E9D5708E705FC6DA91C683558EC0A45F63E4A5E057B8B551794593D17F167D1965B280DDA496BC20F225C67B4FD7E8FD0CB878894F1EA1A9FA2GG18E8GGD0CB818294G94
	G88G88G4F0171B494F1EA1A9FA2GG18E8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD9A2GGGG
**end of data**/
}
}
