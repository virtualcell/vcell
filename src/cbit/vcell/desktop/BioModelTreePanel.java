package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.client.PopupGenerator;
import cbit.sql.VersionInfo;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.vcell.server.*;
import cbit.util.Matchable;
import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import java.awt.event.*;
import javax.swing.*;
import cbit.sql.Versionable;
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
	private cbit.sql.Versionable fieldSelectedVersionable = null;

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
 * Gets the selectedVersionable property (cbit.sql.Versionable) value.
 * @return The selectedVersionable property value.
 * @see #setSelectedVersionable
 */
public cbit.sql.Versionable getSelectedVersionable() {
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
 * Sets the selectedVersionable property (cbit.sql.Versionable) value.
 * @param selectedVersionable The new value for the property.
 * @see #getSelectedVersionable
 */
private void setSelectedVersionable(cbit.sql.Versionable selectedVersionable) {
	cbit.sql.Versionable oldValue = fieldSelectedVersionable;
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
	if (object instanceof cbit.sql.Versionable){
		setSelectedVersionable((cbit.sql.Versionable)object);
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
	D0CB838494G88G88GC6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BD8DD8DC5515D1D129099AF76396AB3BCB33E9975734E217EE5D4A6EE21B36D453B625E90DED2C34464F389B3729E6B53A6C7A82C4A166878D35384696A38AA40470B381C2A07C8D7FC340C09272E7981887CCB2FF4EBC82C4B4FB4E3D6F5CF973668DC35070F9725EBD775EF34FB9771CFB4F7DF923147367184D1B52F2A4E953E6C97D2B5AA4C91EB1C9F2BC396AB942D597A4E7C91A3FF7GDE138E
	9C4D00E7BB642D38A5B9ABDB5AD2198969B310DE3FA0B96B0F703ED35AD199A943CB08F686A1CFFEBEFFE466F6FED5C06D3C4252136FEF0567F600C14061F3E981363F707DDC959F216289B625812E904B5C7D7EAE95379E4AE4822E81203F1017F74013B3F0FDCD35DA6E4CED315206873DE95B100E06A641F2356EFA7519A73D38A92A885BDA497844BA05109E84C06153256A6ABD702C6D3DB0FAF8F8482E14DC9C169D0E921B6C3B2038BDB5B5CDF6F7375BA6BB7A3D325CEBF5498ED05F9E24372BA0135517
	2455C0AB06617E278D5B9800F78FC0649E42EF2CC37C9AF8CF854859CBF25595542AFC8FFC7CADE9E539B3E3C788390A77F2F97A777A651A67FCB46DCD5687EF95212E97C2DEBCC09240BA002D4549D945GEF313E5FDD08F2B68EA94DEE27536DEA74F81A9CB6AF138D5EEBEAA0C7459D15075DDE1BA4717E7D657B45867C4C843C5E5AAB2E47F592DBC1FD3D74685725557F78D7E92F9868E4F56C47E966E22E0BB345FE1D70365FA9885576F4F120AD8636FDC252361D316D97691B8D5A4EFB7167698BD5BACB
	B4EDEFA8463E3E239D7BFA993CEFE2FA17A40E3F1B612B7779715CAE53999E53E31017D1C236B1301DF831E4AC16324B3EE82807286FACC80BA961BC5CD72267A5371E59DD89EF33C870F200619B767B711C177FE5F60A69E848DBDB1A1C456DABFFAF7A2929BCB92B97E08CE09EC08CC082400AF232318B057BE7E9E33DD62F52B4EEF758E42F446B66973D81CFF9D271DABBDCBE456A9A160FDB3DF66B10C376C9EA19FDC528032100FA654C7607210DB659A5FB2D0A5D6D826AD6272C48DE9FAFF32868B5AC63
	15D9EE3F5DA9C383CE0FC4F41B0BD07F8E2BCFB96631D99559DF0267F795E11FDC349E870AC0G5E39DEBEA8C6FD2D8779B3AB1233F2ABC88F5FE7743AE4AF0C97B5B596377B4238279FA51146AAD04F55EC6CA806F7F3856938FB9F6216C2FA192073BFC52FEB68186D43483E55BBC5BE76376D7970DC8565B78264191233EA81FA8146GA281E2G92CDE4D37FB9C0B6E55E76B5297867379B5A54A2306FB5A6EECB29A63DEF5D4B5AC43E738FG7FGB6008800D800A400F5G5915B4BFE435AF54516FD5E90E
	1982FD31D665D76F1B1144037AC4F9E7B31EA46AE8ED5454633C158671762A7A5CE6B0BE3CE650468E8DBDFC264F022F6D2A3FEB531BB56F5C9E6EAFC15BB2019EFB81BC15E49355FB72380FB5D9FDB2F9D50F979CC23679AD044B77277ADEF822CF3E8B508CF09260B4408100E3G6F81D48134816C13386F1FGA88368C05B8378G7D92208EE0CA686E90239D89FC45812C82102A12338A81FA81A681E2G12G568364D6435C8EB086309060BE0094004C0349D925G83G738E124DE49CEE58F4E5F3C7A8F8
	D715F13BAA5BF6D54E7D2A0C5BD5DDD4E96A54E95E1B55E71BC69F1477017ABC266A0CF42B67C11EA52FD31F033CDA603ABC713969F233647701B755A74615E873EFCBFE3F515AF6010E9FFC7E81604F920FA7357DC47E04F60E7E762A944C43FB86B8A3A81AE5395982773D123D481FFCD1F6A9E659A33BEC7062D3738E6CC51E5579EB44AD6A9CE2F88F4CF74DB04178843EF1AF6A522AA8DE7B5038A277490AE2F70D7A73BBD8DB3E71A10FFF2A2436FA77E2DF38A7E03E93650F6D4539E2586DF249EC9090D5
	547C93EC2E129D329318EEF60F3B940EBFDB0C717C85F9CA1DAB2D45AFCBB857B946F15E65B1F21C957B426335BBE1E84129DB5AF89067311E02DC0D0EE28107317EF2F8269C24B9E762DD1AF372AF5DA5A51E73249945D4D1DF39B52D21C60DF1EB7CF38E676968BEE35E87581C2962FD1C5736F328D703301D5969CE699167DB1A7CE688760FE2C7FAE5EF2BF5D8F143644A4BBD7BBA53BF90E76AE4254B543CB6EF260686475977A30FB0A0773ABD631EEE59B54E713FEEFDDD470FCACA25D5F9946B75591D3A
	FA3FB5E9659019C20E2CEA743D961BDDE9F4395C8A0FFBC4A9BE07ECE0BCA9204FC800189A1AC3D66E43FC0AAB544923596DD0E5053C549A6C5F54769CCD9F5DD7437BF7ADBC9D07B0FF2403724D10BEF30867E31A673F5AC9797110DE2E496776F3CD2756FED2557C7C5A642CEAG4E431DE5DA9EFAEB75B4FED0E6C4835FA321ACAF13D0912ACC0CA8737D4AD0E56E2BA53DBDD0124DEC6223F19942D38C683A045FA4153C2E4BBB2E7A904FDFD102BE64504402F9F5586E65FADA832F28657154DAD5460CBA5E
	FEACABD753C0659CF5EA4CD547E32EC5EAB9CEAF2B114AED007456433086BC4C69B98EEB4B7DC814DB8A78E5GEB8C4B5D56C465726BA13E84B8D3EFD46ED8B3150BD67363556772FAED399DE6AA178A781C0664ACD3C3A03D958DD86EE7221C524065DCG4FE8008400BF214DB5E23945ADD4EED7A30FBDCC0D3C9C16AF917AF334D239050D3C3DD835DCA2BCB31AE09D435AFD260D4A65B6717C52A65EAF16A65EAE17A3211D4A1DD14BA9CD1CEEDC13D65E4B9DD4EEAD6025E608531BF93941E6ED3966CE21BF
	4047832CEC8E548B176315AEAA17E3667945E65E3E594C4B2DB4E339C7C439B3E69EEFA9E69E33C742B39100377B5F96AA175782FE87B050E2445F3FF5D339A8480F8BD94EF7C458B3642F8DD92E20074AE53682AF2D7EF28D2D3C9C9F4F1FE56BBB36BFF35EFA51BAD9621B00F9322626138D6BFCAF661F59FA8AE30F48D65AF379CE970EFD6BA01D51161C352B0D66231FEE23BD17C597E22514EDBFCF8F3517E4EA63FC8C347967235577CC24733175F099369985F931EDB4BEF46D572E7B9AFDBE77301D8D4C
	6A987062FE6DFC45F1AF6D5F824F913B5727F440BCB4C9634566D264975724AADA55472EBD3BC99F43EEE709FD48D9B2FE51D9E29DD2CAE4188CEAEAE89AD37729AABAE81FAAC1686013D62D8E92F6FC1276EA889DECD0E54FE9776BE059D34F25737D021BBB362A347BA1FF814032F644BF5801B1857A8DEFEF38025ADBF869AB52606DDF8A59DEE687EF2730435F5E52FD8754764ABB293DC1480F6A20BD3C5B8A7CBA716F618D595DCE1CBB7D1BF8244317F3F70530A91AC0D53B6A4CD479DC51C1F675A463A1
	3393EC96203A13F8F820E0567B08AAAF1C5E06B224578BF446G66F5D2BBCFD5A0BE8A52F1GC9827FECA562D3A03D91A03B8B7440F4934B76F41017C2408D8270B8E1C7839BC32159BE0AE5CAC88F5BA63275F905C547F91FAD5E59207649722FAF16EAAFFF18E634B79939E3ACAD2A0B77D5E2173F4F14150F26C582F05E069B08372D969827AC445B038DE8A74A185D47534FB4203E07D4D5F1BE3E76A7AD9F19566B8D79D87054E85A34C55D1333E87C95F8D8AF78A8EFA4BE56C1197CEE0AB16E5D2B0DB14E
	F4737D0C08EE62730E46F2F8DA39727823D7760E383DCE59D60896D4E21DG7743F5C1C9A34BEE4177A252738793A6F8FAE570F239D1D75D923A7E2122631C6F67845FD140538A41F7E429915F19C7086F33CDF3651B377B6DA6EAF797506CBFC26D5EDFEC546E82516EBB4D1F2D5DCF1B295D982039CA50FD02616DAE3BE237BA6C176442A22E5F5587F0CFCAF6E1D8556FF6BB7A6D9E9FD72C6A5AA51D68F7C21F55E6221F5D131C555EC3F20D6FB712EBDE8F357F37664FA6D72A19462FC5C0F33920BB5CC2FE
	4079781BFAAD9F993DB047837476D27979AD483B138FA36A5E75B30DDABF396F158506FEB27677D1E936DE6E9F8BFA7DFEB2787823E94A6364A78F35901F2B21CC4E8754EEF9AB72E9750E1671B627CFD21B731647CB39BBEFB6EC93417281EFCB79405FE66CEF9FCDCB78AD35F9FBAB7549B2A813AA5ACCEDB56E73DB76603A52B38E4BEABB6838576B7648DEE5CA75A70795BA3DE768BC8CEA0702B1C5477978DE56162936E9BACA6368DB6DA43B83F0CB0E929FA99D48F760FA1056E2DFED277E63B41EE8A7D9
	D6C37D1CBE22E16958477AED5CA71730FF0177618B4136530DF9E4BA6A9872C6D13130BDDF0F0B558C9ABC7C95052D47F690AF9660A3220FECBD36CAEF6B38E71BA0F85D51795946BC73C114D58ED81F06996CA47293C7A7713C8278496CA71E7F71001EE75CEBEE68A71EFFB6E71EF93B25DDA21E811A8B845DAFF669ED10639FE078E14D9E822713E691360C3C77D30C10F3981FECEFC5742B77B56CD7237D08C2CD9C61133D97E5EF49B1F896F15B3D41C2F11D699845D9CAB579635264EFCA8367EF89690F0E
	E35C8F979D737BE3767A9F3C128F40E5486FA699D220CC6EF16A034A9223B1327738981B3ABFDB9F2C956D5A00E634E8772EF2FD3BF8563072B879ED74912A3796F0F9A728DE0C4948E686CED03D5E9E2A67G5C92D1EF719EA3B9D71F203EF475E3CCFB9E03458EC5F676F8E45739AB1B5B797E6C3F968C687AE67D897FB9C5460071D9C7AE600BF5F98D106E576178F9DFA533473181FE5E17AE708D8CBF2941F31DA489BF08043CF583F4C6F5378957E23DA7E1CD8A9081908D10G307AA42D01EEAE2D10421C
	81F63894D8A2D99DFD811B1BF496D7066DDE102754F4DB1956E7384E2ABBC21D6FEEBCE92CAF4E7F1B155827DBCFA5E79582B4G8CGCC1EA25EFFDC96167716C9C35EB9DFFF49780ABAB53B73296DC21EAF33FAF1D7D00F7BFB51A96D3D080C57BA5352C378FB0ACAFB7DA93F3FF31DFC074D8325271333ACGB600E900685324135C7262F0BA21F3DAD599EA9FF54ECD972D1DD72A8B550FBA2F743E8177016B182DA71C561F575F4E778FCE6B4F6B1F6E24737ADDE712336E836065474A915FFFD66FE3DE0637
	1D557B580BE564E38A642DBACBF44E1A90DF7BA1AF3FD95099E378DE8D1E5379DD0598AFA1AF69C322B3DA057862F3FAFEBEE2785AF3FAFEDE28A4FEAC1097F70E4E623EDE2F57EB4EE0E05F146A523CDEC2D03D92DD3ADFD70F5BE36EC1DC1BBBA0AF92A086A081A085A095A04FCAF6A917570533CBB650763B1B81576EF6DFE8561F0D88DE6D8DF313F1A2285E1FF469862B110C3756200C831027GACG0885D88E3096E06B904978CF6507E6A9A31FD902A5D4FD31F1EE72358655534BD7BC94D80F4F0DC91B
	11FF037D951AAD553D2935FBA97EB68F113D254E1157ADCD61F81D56714AF97837D262E11160A1B108566CF878F1D89E169B7270CCF126CAE30D60A122F9EEBC7CC5D0BD3D5F6D9AB62AF758BC3776ACC17574B21B060DE4DE3A1F74EE99A61953DA420D6146BCE4072D97B1ECE4279E965778776CC6476DE02438D550EAF58D63F9BCBF9B1A30E84F062207793D1DA4F82E8660727C3C9E6541F9F943B045CBAF57221F975B12335A8186812681E2G92GD2EC64671995CDD238FBD3F4D08C3ECE2F86839A5747
	CF6728474742565BE8B39A4FA26AD04EEC99E2D2GB340A0C084C08C40B21964BC93DECEF1F5EBB6F25E59BAB71B3DAB281E5EC7D64BC675AA5B305E86F9F631C966885975BB6DF36B0F7DE16B6D9AB12A779FC175E6271797026A857932E1FBDF8F0A47E799A384555337E799B19ABB9E2B22352FCD68589B947F4D0E07F9C17574BCC49B72706F87E87C0A97BCD4F64D2D1F4DE16B2DB354FB26E5EEB23F91D4CFEF7F9923C6756633FA3BC6E7E77F88FCFCC8E3E75C25D04F82E08318873084A091E04DA80D8F
	77D454CBD7B0BE4C98736C691E1BEE2A036A696DA1E34C489E1CA5E48F3B46489E7EF30EBC240665215F100714F26241A1F8D8FCE4EEBC44855553733044100707CD44C3026061F84F5CF8308555537330411007C776908FDB6D49D97C4EED8ABBA3B6C3FAC0605EAFC25CB42497895CD313080B0574B201730CA0EE952457895CC29B62247349D9396789F72F0C38DAC8771FA759EBAD38B7BCA6522DAC0D7B79EA4C58255F7F198BA0FDA3BCEF63494F250DD0F7DB396F461BC25F0D3B5FE4FCB76E8113715D38
	9F303BCF00470DFD4DAE8B657F44B4735D3907CDC6F767F89F75307370F9202FC567E95CF927B2B46F0515463C17D49A73EE2A1C19775A4AD03C3F4DF47C0BB4DC07FB47DD0A5DA9B352BCD615A7713EA40C00C747DDFCF8C46ABE7D9E0CC4E3EE93BBDBDDA1E47CD9E53994BC36627DB1371708744BD6EF4F4808883C7ABC72B0717DE0D5E89D7D344AD8C78FD7996B68512A19F574C4D5A89DF1FB7EC2971E772D8359248B60FFGA600DEG87408200A5G099748476EE465D7C1FA6385FE57846FC561DDA63C
	6F126B607710708E94AF7FECA75D8BC7FA6BC2785682356DD07EA229BC61FB628C65C201C915A350177F2F0BF85BB603CF6E107C77ED0DGED0A6E576A6F224F86747761B9EF2EF23439EA0778B60C25712E90CFCB65C66390167F2CE3131F07B78D5B186D3D7C5935B14C6638FE605BA3E4F5B19C5EF7423BDB9C77DF95D7BA6EFAE6210BFE358CEFE33B69CA5B58BC0BB224AF5E06338A7BF10110F50225A5A167CB153365E1419C7A1A3711DBB55BB6665257FC6C7D619B68B79E982AE49F5BD8B3DB952B1A77
	24027756036ED79F01E8D422F360A76A3354B2AD0A3B993FAFD03F3DBB98FA6CACBAE8BCF66EBF087E6DF30FFB07E57D78D8F9B0D7647963DF5CD8C3F6F819FAD6DF31FAC765207135155567F9216A779C8CB586F3D9E39AB432F6D78B5AF7540416B52646D8562F56041675EBB561E57D271A50327ECBCDF8D93FDFB3332C6EC6ED3F1A04CDBE3DD79F1F4601FDAF1B4146B93D0AA6AD3D83025E32D2BD3DD5C0EBDDD8FA4F87502B94741EAF5653131CB057B9836995EA521C5E7D4DDAFAD582BFEE5E22356FB2
	EA675E7D7AF6EA01E62FB39C5F87DB281DDE455D0BE7EFBC5699EDC11FB358BF6E9B763A9D8E3C79A9D12CFF0F05E2FD1BD35C1DBB0CB1D4B424638116BBE91C7AA5BB13D893022F0D005F6A027840654F677611DD9B5A267FD8EBEC53EF570636697C5A70B63D2FB634CD1BEA435BF4DDAD4E6FC3F61B5D2B5E6A67BA4FECFD15F4BE62F5BBFB8F28B4EF298BAD636DF546B25ED997DA4645F561E53C3BAE340C5F288BAF637DF546B2A637EA6C349B6D14477E5D6C3E0F897A576C22B5DB944375C35A6622BB8C
	836DDA3BAC93763A3BC36B9765827FC4C700DF1413DF1C2FB35A57D1DC617C622E4EGFA95C46FF7877474A201D6F4D8FA4EAE6DD86D673B5E92E08B1043F5321879D49C50CDF4517AE1EBFDE87B58D9EFEC9FEF5607360F3C7A7076D1D49F5ABECA6B435B47417A1947F5EBF7001ECD24E7EF09DE4FABC08FEB426A793DA3DA7B40B91153FB22DCCFEFA3505A6A8EC76F11GFA87853D55A6BD3DBC20E58ACB6F661EGF9CB095E2F77686935832DB3E169BDDF005F0AB92CD36EF1C512125CE8BB5B8B692C4483
	69C815C61C1B1F1970FAF5C5E8F7CE6B245DB96E6C33DF1225818F3B5F4576E33261FD17074ACAD3E8839E2FBCA2FB3D320D15165472EFCEE179E8F80F9565EF9CC11E065C63AE1B0F4A7D8D5B3FD9896FA99E3A3FF36F8472B06235BA41A458C2B776A3AC7B77B662BF875205G6DGE3G119FD19BC9B2635F6E52711F4276FF56C339D4D1D612F0AE524CDCE53C7D05E3287B0B56737DD39ED91D77DE75621D3930F74835174404BF34B6EC535AFAC0DC1A5003EDF5DA2CC37EEF589EE773C3E71F8C4BF02B62
	76925EF99269F4EA6FB589BA3FD2F6E8734C32C3D6E4B56FAFBE8948837E108335DE6ECEED1E7FC2A00DC3DB82F9A9D7718FF882F8E9F6FB2684AF8FEE5A2A4DBBA2CF70FBDBAB3B3477368A3D7C5E1645CB773AAE865C6B9AD473711D67F76C8F154F5B7D0DA900A75CC091F9EF3D017769291B2808BAA715E39E30596871B8DC01F9B585785B87015F720923F2F5CC4EE0E7441140CF3417662EF6060B03F422407D0A1DE7AF07F40A406533EFD5B6C0BA43C7386D6C5B49ECC8678B5C89062B06F43B40A5B0DC
	BF24BD8217444EEE27A13DD060DEE36DAE01F43C0F622DF8F14E10688B9ECBD69A6054BBB06CDB0635BE7D5D15D1065F68535FDD795DE13A3B1229A4E7B9946269776CCCF6852457885CBD0D34EF3BC160FE5904385C71642C52F12133E636DF8C6941F172E5DB9B5DFFDEB20EE5AE31F404483F07724BF97E77D8FA2D20F9CD9D52CC0574560B04FB197DCECFAE240B852E07619AA05DFB11E87F33A5306D1F307499D1678B6C7C418169F982174CF00BA03DC460D25999C19C241789DCCB855B5F0674DA01FB16
	6DE7EC00F4668461EA591957AEC8978B5CFF6DA13B2C963843FB49AEAD93F44F3232907B887BEDGF08BA7E89CAE9B8C789687EFCB565434F96D36A66B28054DACBC2677317AC9D0AF85E063845DF91DFC1F3EADDA7A5545524AAC636F39D27ABFC94B1E6476E61A745F01DB64FCB44DB3093FF374B1FE622F7A71AFA7CB606912A7724485CE335DA93B7047BF8A4B4BCCD5266A0ACAD3D5F11E7F3D086E6445CCE86F64D5EF395670FBE704F8150FA475197A07AFED623CEC697073A20F644D40CBE9E9F1DEC5D5
	D9912FF73953DF6FC7436F6B6B9D0598496A9AF54805D08B7EABB741BF268395C534167A110D3EF7122620BF2790775FB66DF8083467277373C21E3FCF5666E33C056BB8FF7E3900FC5451EFB6E973EF8B48C7DD4DD76BF3FDBE749E75EF6AC3F112631B91A1FF7BE95E147A7D0F7ADC38660ECDD1AFDDCA673AF92A4B2F1B2FF40553A97C23FEE7706CC8086F8F59EC4A757653A3F477E3751478A6CD89710D191865B8AFDB192FB8DD32536D328F73B8E29F9B57FF8810F3097C7F76BA760D818459DC1E2D5DFE
	F91EA98D261145467CC7A57C6E1EE89CAA8B24317F081F46FB7B02E954B25C9368B30206EBDFA00D519EBF0D1F6DE3B108FAAE6BF4DADDB6C873F2B7757A4BBD36BF382D67986E87GE651D6E1E9E0DB5F451F5CD0674307589A259F4A0EDD22F1A7271B467669CBB4878D57A30D65A26DE4E97CDED65D23BD7CF91C0D99013F4D6D75D72B0DF421534C866DB7C1FC0E383E91E5CD81BEF8EC7B7DC60AEDCBBF06F89C408690813064E352796F1BB096733A5D4AF13B4F0EA7CA2ABDB5531D0BE5226C21E624257F
	A6062F837A1B292D15D0FE9DC061F4F2D66DB47FD61957DF58066345EEF6632A44E1F749FD4A14C35EAD116F57495A313474E3CB487B4D7D535C67ED537E31D40DBD597AFEF7A30EFB365D9C9725732522BE3FED3EDD99EC1B26CA324DC553447BC8E5A08DCB3F1F4693ECEEF60DBB545F0608A9892679AD067BAE76152079D4C9A04D8D477CB4F7D6845358D3C1FC458B9AE7AA82E97C71041F46168341B45EE1380730DF850D3E8301B4EA50FCD5BBF8170DD70FE13FC2F91E7FE57C699BF5ADD9DBF8257730F9
	3D4EAB2E47E342A7D9DC30E61A47041B84FEBD432F5760F99CB2A56210D4489B7C046E6AC51CC2FBDD866994000DG591FC2BC88E079D4FC9F50GCB66196F27743BBD9679226C509CEBF2DB7E49E92DADE7FF4911C6FC69659A7C145B3063D37DDD7D27CF236EE3G1FA0F8CAEC5B8B4F63567341A73A00244F62B8167DA2052A6FE45B156A1B4B7140ABB49F47FF7B9F247BEEB0077C16FB354AFF2A7AACFC7C0ECD7864321CB80B7AAE3E8CEB8DG87C084C094C05CE512EDD9277A6D81DB9AA90AF5F80CFDB9
	AF8E3D6D301C92E8DC44F359FEA16E2EAC3B6CDFE32CD25F5741D31A97387660F6EFAE46E7012DC0CD6731EFD18ADC146E5B4752DECAEFABE569E1CA7F5602FB8FEE0F75A37CD8057B4B8B62BB2D9CE8F3E09E6276F61BE75073CB6A9F0D6D79D0279A608FEA5AB24FEF536AD834935BE543E8B9616EECEC14F80CE316C72C638EC5FCF9DE28C1F60FAB6C23D948BB82F9053B9DEE2B8D62145D45A3D607CFAE426F8FF90C332E117659E366D1EC185F240F8DEF1334315D71A6FDEC38A9A0BF32C99F9B7EF5C07E
	371B7431A155D77B27190DD35EF1197B76F4B32D83D7211E2F21D8ECD5937A2DBE96AB418F5FEBEAB46A698B1849385C4B443C57FE8D1DAFD4335F791C8769A80038EBE88C3974A17213F48D596F4BC7F74F601B78DDC21F6285CEC81E2E4AC0FB7AF189CBDBA95DD89118DFFD0025E5CA576CE5E9F7811DCF952247727767B46F178B2FD6BC629F4F232EEA9B6A37E627C26FC9AFBAE53CA77D25D346F7D2E2CE4DFC27A876D4685F633A671476773838AD1CBFA7093A75275F8BE587B00AF502C9728F137B2D23
	224EB32737EB7961C3371A7742E9748B71BDAA5BAF6216445B1E98447C55E0079B81F6DD8B73980079DA324BCF4E20DD72DB657AB13545B60A7B71788B1C8A35B7F8B6341EC74F067F5DCE4FD95CF79EF2AB0A5B097489FF71AC5A0A6276B0A447FDFC967B47EB9F9DD3F8D195FF4DD9363FA10FB0B40F771FE66B2EC110EB7ADADA83FCDAC7FB8F8B856EF1060B06F43C40DDE47B9BC910CE3916621E6CEE9ACF7EAC713DDB361EE0FF6B6B0DF06AFAFD88D3FCFE7C4DD61A9FF75D7DF71279E1F35A3BD2085FD6
	39164FC1666B7C737C2A0F7252397F7CBE60BB18E9A833703A196FCE70FA5786FD7F2107A5BABA3C5E42307574F7AFF93D9F8654CB8C43A3131B45E7CB59983A72BAFD5C76CD06CF39CE9F379DEE2338ED9D6475C610ADFF7AA14EFDBEF59C65384B03216D7506417076FA6BA07B1D9E30B35C1CD74713C1B6370F0C78E46197F78CE68BDC072F576D3323A7F2DB4A6889FDEE71F68F520F879992A3A806D60651FAF85D976B0C4FC7DEBF0C7556C11D0D91E433B71B0C4FA23E426CBB7FFA707D6BE9FE9A32E15C
	F1865213G51G71GC9GA9G9BGB2EF0071824084E0819884D0GA28122G1682A45EC03A6F994278A0547C26EE62314144EB3563F0222E5DB359FE6BCA20735782370D6156818EE388FE67A6F1A76A506E4225E1B37F05DFD1BE054D4B19B7A6E7655DC86B6CEF30BDBC7EFE3F665D5CF675673B33D7358DEEA307C77CB6622BC3FF8C3107D5F5G37300A289E1BD654FA97CC332F97B1662FF7F24FEC6A29633AAD347FFDE28B6FFFB7580C670B8536607962B61B71FC71C51BF63E6038E89B4E5323E01783
	225C17856E2440FD1551C45C893BCD999378250CAF44374B5820382B40781AE038819577AD8D6E240ACB30619882A20F393D628E348D671C913B4341535FE3E56CAE98D204BE9EE4BC5ABDD68D5FBF91B8641B47395677B53FD341D8D7FFB3ECF21B9FCF2C5399AE3B6B63F5CFF4DBBD9E963F707D1B9DE65A3FB103FF1D810882D8FAA39F5F7918DCE526B1F99960D3EF24F87B4739B3445B545B1C8F3E455F8CD1084C636D4BE67A965294C97176756CB7E09C100EG080EA4FF2F8B7A76F0F63E5AB547FA912C
	DEFCE468B9AE8972D6EB72791C768263FFFD24FE2ECBE3F3DAEA24FE2E1B5FCAFA4D7C822C0D3FC0B14B1DCCB7ABA03DC6601EE5388D105EFA93616ADA901787695A1B449D4DD6C9481129999BEFEB3D7AE363BFDC55B6389DFF439C3C7F79C3B37A5660CDB4D7EEE9B15E5B5C5DC2EB3CB8D1363F85E99B6FEDBADB48B792EF62FB1B783E6AA66DB971E3664FEB7DCDF76582573A3737100D2C07F68B67132FA4B37CA024A781964E97F76043FE5BE95CF7F7DDF1BDEE5B2D4C06E3676BED3E076163676BEDFE15
	10A791727297905F960D5D9E533C7F3A656A5B6D73D7358D7E7FE4395C1B8CC2AFFDDA5A6C73CA2F874F2D0D37BC727052F3661CB4A92DC3FAF90C8D1A9D5266899C5DA7BD17DFFA69A5845FC75227281BCD38E0CD8A20A4CE7D3E9BF7FE737FBDF8DEC913B202A8F5C81BA68126A6BDC063FBE8D4127AC71425A484C5FA195F7AD02474A37284A479C58F4DCB86DE53D0249D988C66796FB3A8529B4D78BFE0107320DE9EDE02308F2B99BBF9D10FA720B84776591D8158DC0625AB9681D999F8D785C45FA1C46F
	E033E84A450A1A74477FAE1D4B4D230A8EA983C383905F4B55108ED1858AAFE5E8C1113667E9F92D915A4085F657CCA9719BAD17357B3E9B2F9F8EF99E1CF32B3ACFFA2BFE5F37388F47A79B60675DCA317774D13CAB94EACF214FE9F5B8942B63C2234B067B8BCDD30A6C5F4BA86BE3E39B15D1B3F9DEF59F72FDDED3677F81D0CB8788B0F607156EA1GG18E8GGD0CB818294G94G88G88GC6FBB0B6B0F607156EA1GG18E8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1
	F4E1D0CB8586GGGG81G81GBAGGGA8A2GGGG
**end of data**/
}
}
