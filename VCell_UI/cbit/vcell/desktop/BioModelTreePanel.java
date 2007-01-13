package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.simulation.Simulation;
import cbit.util.document.Versionable;

import javax.swing.tree.*;
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
			if (e.getSource() == BioModelTreePanel.this.getJMenuItemCreateStochApp()) 
				connEtoC3(e);
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
			ivjNewApplnMenuItem.setText("Create Non-stochastic Application");
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
	getJMenuItemCreateStochApp().addActionListener(ivjEventHandler);
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
}