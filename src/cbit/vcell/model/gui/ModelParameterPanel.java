package cbit.vcell.model.gui;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.graph.SimpleReactionShape;
import cbit.vcell.model.ReactionStep;

/**
 * Insert the type's description here.
 * Creation date: (9/23/2003 12:23:30 PM)
 * @author: Jim Schaff
 */
public class ModelParameterPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private ModelParameterTableModel ivjmodelParameterTableModel = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjScrollPaneTable = null;
	private cbit.vcell.model.Model fieldModel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.vcell.model.Model ivjmodel1 = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;
	private javax.swing.JPopupMenu ivjJPopupMenuICP = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjthis12 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ModelParameterPanel.this.getJMenuItemCopy()) 
				connEtoC1(e);
			if (e.getSource() == ModelParameterPanel.this.getJMenuItemCopyAll()) 
				connEtoC2(e);
			if (e.getSource() == ModelParameterPanel.this.getJMenuItemPaste()) 
				connEtoC3(e);
			if (e.getSource() == ModelParameterPanel.this.getJMenuItemPasteAll()) 
				connEtoC4(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			showAnnotationDialog(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == ModelParameterPanel.this.getthis12()) 
				connEtoC5(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == ModelParameterPanel.this.getthis12()) 
				connEtoC6(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ModelParameterPanel.this && (evt.getPropertyName().equals("model"))) 
				connPtoP3SetTarget();
		};
	};

/**
 * ModelParameterPanel constructor comment.
 */
public ModelParameterPanel() {
	super();
	initialize();
}

/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ModelParameterPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ModelParameterPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ModelParameterPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ModelParameterPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 9:10:39 PM)
 */
public void cleanupOnClose() {
	getmodelParameterTableModel().setModel(null);
}


/**
 * connEtoC1:  (JMenuItemCopy.action.actionPerformed(java.awt.event.ActionEvent) --> ModelParameterPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemCopy_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JMenuItemCopyAll.action.actionPerformed(java.awt.event.ActionEvent) --> ModelParameterPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemCopy_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JMenuItemPaste.action.actionPerformed(java.awt.event.ActionEvent) --> ModelParameterPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPaste_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JMenuItemPasteAll.action.actionPerformed(java.awt.event.ActionEvent) --> ModelParameterPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPaste_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (this12.mouse.mousePressed(java.awt.event.MouseEvent) --> ModelParameterPanel.popupCopyPaste(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.popupCopyPaste(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (this12.mouse.mouseReleased(java.awt.event.MouseEvent) --> ModelParameterPanel.popupCopyPaste(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.popupCopyPaste(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (ModelParameterPanel.initialize() --> ModelParameterPanel.modelParameterPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.modelParameterPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (model1.this --> modelParameterTableModel.model)
 * @param value cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.model.Model value) {
	try {
		// user code begin {1}
		// user code end
		getmodelParameterTableModel().setModel(getmodel1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (ModelParameterPanel.initialize() --> modelParameterTableModel.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6() {
	try {
		// user code begin {1}
		// user code end
		if ((getmodel1() != null)) {
			getmodelParameterTableModel().setModel(getmodel1());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (modelParameterTableModel.this <--> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getmodelParameterTableModel());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (ScrollPaneTable.this <--> this12.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		setthis12(getScrollPaneTable());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (ModelParameterPanel.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getmodel1() != null)) {
				this.setModel(getmodel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (ModelParameterPanel.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setmodel1(this.getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
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
 * Return the JMenuItemCopyAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopyAll() {
	if (ivjJMenuItemCopyAll == null) {
		try {
			ivjJMenuItemCopyAll = new javax.swing.JMenuItem();
			ivjJMenuItemCopyAll.setName("JMenuItemCopyAll");
			ivjJMenuItemCopyAll.setText("Copy All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopyAll;
}


/**
 * Return the JMenuItemPaste property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPaste() {
	if (ivjJMenuItemPaste == null) {
		try {
			ivjJMenuItemPaste = new javax.swing.JMenuItem();
			ivjJMenuItemPaste.setName("JMenuItemPaste");
			ivjJMenuItemPaste.setText("Paste");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPaste;
}


/**
 * Return the JMenuItemPasteAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPasteAll() {
	if (ivjJMenuItemPasteAll == null) {
		try {
			ivjJMenuItemPasteAll = new javax.swing.JMenuItem();
			ivjJMenuItemPasteAll.setName("JMenuItemPasteAll");
			ivjJMenuItemPasteAll.setText("Paste All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPasteAll;
}


/**
 * Return the JPopupMenuICP property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getJPopupMenuICP() {
	if (ivjJPopupMenuICP == null) {
		try {
			ivjJPopupMenuICP = new javax.swing.JPopupMenu();
			ivjJPopupMenuICP.setName("JPopupMenuICP");
			ivjJPopupMenuICP.setLabel("Initial Conditions");
			ivjJPopupMenuICP.add(getJMenuItemCopy());
			ivjJPopupMenuICP.add(getJMenuItemCopyAll());
			ivjJPopupMenuICP.add(getJMenuItemPaste());
			ivjJPopupMenuICP.add(getJMenuItemPasteAll());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuICP;
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
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getScrollPaneTable());
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
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}


/**
 * Return the model1 property value.
 * @return cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Model getmodel1() {
	// user code begin {1}
	// user code end
	return ivjmodel1;
}


/**
 * Return the modelParameterTableModel property value.
 * @return cbit.vcell.model.gui.ModelParameterTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ModelParameterTableModel getmodelParameterTableModel() {
	if (ivjmodelParameterTableModel == null) {
		try {
			ivjmodelParameterTableModel = new cbit.vcell.model.gui.ModelParameterTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmodelParameterTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

/**
 * Return the this12 property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getthis12() {
	// user code begin {1}
	// user code end
	return ivjthis12;
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
	this.addPropertyChangeListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	getJMenuItemPaste().addActionListener(ivjEventHandler);
	getJMenuItemPasteAll().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP3SetTarget();
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
		setName("ModelParameterPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(655, 226);
		add(getJScrollPane1(), "Center");
		initConnections();
		connEtoM6();
		connEtoC7();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void jMenuItemCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) throws Exception{
	if(actionEvent.getSource() == getJMenuItemCopy() || actionEvent.getSource() == getJMenuItemCopyAll()){
		try{
			//
			//Copy Model Parameters
			//
			int[] rows = null;
				if(actionEvent.getSource() == getJMenuItemCopyAll()){
					rows = new int[getScrollPaneTable().getRowCount()];
					for(int i=0;i<rows.length;i+= 1){
						rows[i] = i;
					}
				}else{
					rows = getScrollPaneTable().getSelectedRows();
				}
			
			java.util.Vector primarySymbolTableEntriesV = new java.util.Vector();
			java.util.Vector resolvedValuesV = new java.util.Vector();

			//
			//Create formatted string for text/spreadsheet pasting.
			//
			StringBuffer sb = new StringBuffer();
			sb.append("\"Context\"\t\"Description\"\t\"Parameter\"\t\"Expression\"\t\"Units\"\n");
			for(int i=0;i<rows.length;i+= 1){
				cbit.vcell.model.Parameter parameter = (cbit.vcell.model.Parameter)getmodelParameterTableModel().getData().get(rows[i]);
				primarySymbolTableEntriesV.add(parameter);
				resolvedValuesV.add(new cbit.vcell.parser.Expression(parameter.getExpression()));

				sb.append("\""+parameter.getNameScope().getName()+"\"\t\""+parameter.getDescription()+"\"\t\""+parameter.getName()+"\"\t\""+parameter.getExpression().infix()+"\"\t\""+parameter.getUnitDefinition().getSymbol()+"\"\n");
			}
			
			//
			//Send to clipboard
			//
			cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
				new cbit.gui.SimpleTransferable.ResolvedValuesSelection(
					(cbit.vcell.parser.SymbolTableEntry[])cbit.util.BeanUtils.getArray(primarySymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					null,
					(cbit.vcell.parser.Expression[])cbit.util.BeanUtils.getArray(resolvedValuesV,cbit.vcell.parser.Expression.class),
					sb.toString());

			cbit.vcell.desktop.VCellTransferable.sendToClipboard(rvs);
		}catch(Throwable e){
			cbit.vcell.client.PopupGenerator.showErrorDialog("ModelParametersPanel Copy failed.  "+e.getMessage());
		}
	}
}


/**
 * Comment
 */
private void jMenuItemPaste_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	java.util.Vector pasteDescriptionsV = new java.util.Vector();
	java.util.Vector newExpressionsV = new java.util.Vector();
	java.util.Vector changedParametersV = new java.util.Vector();
	try{
		if(actionEvent.getSource() == getJMenuItemPaste() || actionEvent.getSource() == getJMenuItemPasteAll()){
			Object pasteThis = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
			
			int[] rows = null;
			if(actionEvent.getSource() == getJMenuItemPasteAll()){
				rows = new int[getScrollPaneTable().getRowCount()];
				for(int i=0;i<rows.length;i+= 1){
					rows[i] = i;
				}
			}else{
				rows = getScrollPaneTable().getSelectedRows();
			}

		
			//
			//Check paste
			//
			StringBuffer errors = null;
			for(int i=0;i<rows.length;i+= 1){
				cbit.vcell.model.Parameter parameter = (cbit.vcell.model.Parameter)getmodelParameterTableModel().getData().get(rows[i]);
				try{
					if(pasteThis instanceof cbit.gui.SimpleTransferable.ResolvedValuesSelection){
						cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
							(cbit.gui.SimpleTransferable.ResolvedValuesSelection)pasteThis;
						for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
							cbit.vcell.model.Parameter pasteDestination = null;
							cbit.vcell.model.Parameter clipboardBiologicalParameter = null;
							if(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.model.Parameter){
								clipboardBiologicalParameter = (cbit.vcell.model.Parameter)rvs.getPrimarySymbolTableEntries()[j];
							}else if(rvs.getAlternateSymbolTableEntries() != null &&
									rvs.getAlternateSymbolTableEntries()[j] instanceof cbit.vcell.model.Parameter){
								clipboardBiologicalParameter = (cbit.vcell.model.Parameter)rvs.getAlternateSymbolTableEntries()[j];
							}
							if(clipboardBiologicalParameter != null){
								if(parameter.getName().equals(clipboardBiologicalParameter.getName()) &&
									parameter.getClass().equals(clipboardBiologicalParameter.getClass()) &&
									parameter.getNameScope().getName().equals(clipboardBiologicalParameter.getNameScope().getName())){
									pasteDestination = parameter;
								}
							}

							if(pasteDestination != null){
								changedParametersV.add(pasteDestination);
								newExpressionsV.add(rvs.getExpressionValues()[j]);
								pasteDescriptionsV.add(
									cbit.vcell.desktop.VCellCopyPasteHelper.formatPasteList(
										parameter.getNameScope().getName(),
										parameter.getName(),
										pasteDestination.getExpression().infix()+"",
										rvs.getExpressionValues()[j].infix())
								);
							}
						}
					}
				}catch(Throwable e){
					if(errors == null){errors = new StringBuffer();}
					errors.append(parameter.getNameScope().getName()+" "+parameter.getName()+" ("+e.getClass().getName()+") "+e.getMessage()+"\n");
				}
			}
			if(errors != null){
				throw new Exception(errors.toString());
			}

		}
	}catch(Throwable e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Paste failed during pre-check (no changes made).\n"+e.getClass().getName()+" "+e.getMessage());
		return;
	}

	//Do paste
	try{
		if(pasteDescriptionsV.size() > 0){
			String[] pasteDescriptionArr = new String[pasteDescriptionsV.size()];
			pasteDescriptionsV.copyInto(pasteDescriptionArr);
			cbit.vcell.model.Parameter[] changedParametersArr =
				new cbit.vcell.model.Parameter[changedParametersV.size()];
			changedParametersV.copyInto(changedParametersArr);
			cbit.vcell.parser.Expression[] newExpressionsArr = new cbit.vcell.parser.Expression[newExpressionsV.size()];
			newExpressionsV.copyInto(newExpressionsArr);
			cbit.vcell.desktop.VCellCopyPasteHelper.chooseApplyPaste(pasteDescriptionArr,changedParametersArr,newExpressionsArr);
		}else{
			cbit.vcell.client.PopupGenerator.showInfoDialog("No paste items match the destination (no changes made).");
		}
	}catch(Throwable e){
		e.printStackTrace();
		cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Error\n"+e.getClass().getName()+" "+e.getMessage());
	}

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ModelParameterPanel aModelParameterPanel;
		aModelParameterPanel = new ModelParameterPanel();
		frame.setContentPane(aModelParameterPanel);
		frame.setSize(aModelParameterPanel.getSize());
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
private void modelParameterPanel_Initialize() {
	
	getScrollPaneTable().setDefaultRenderer(cbit.vcell.parser.ScopedExpression.class,new ScopedExpressionTableCellRenderer());
	
	getmodelParameterTableModel().addPropertyChangeListener(
		new java.beans.PropertyChangeListener(){
			public void propertyChange(java.beans.PropertyChangeEvent evt){
				if(evt.getPropertyName().equals("model")){
					ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
				}
			}
		}
	);
	getmodelParameterTableModel().addTableModelListener(
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				//System.out.println((
					//e.getType() == javax.swing.event.TableModelEvent.INSERT?"INSERT":"")+
					//(e.getType() == javax.swing.event.TableModelEvent.UPDATE?"UPDATE":"")+
					//(e.getType() == javax.swing.event.TableModelEvent.DELETE?"DELETE":""));
				//if(e.getType() == javax.swing.event.TableModelEvent.UPDATE){
					ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
				//}
			}
		}
	);
}


/**
 * Comment
 */
private void popupCopyPaste(java.awt.event.MouseEvent mouseEvent) {

	//if(mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED){
		//int selectRow = getScrollPaneTable().rowAtPoint(mouseEvent.getPoint());
	//}
	if(mouseEvent.isPopupTrigger()){
		Object obj = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
		boolean bPastable =
			obj instanceof cbit.gui.SimpleTransferable.ResolvedValuesSelection;

		boolean bSomethingSelected =
			getScrollPaneTable().getSelectedRowCount() > 0;
		
		getJMenuItemPaste().setEnabled(bPastable && bSomethingSelected);
		getJMenuItemPasteAll().setEnabled(bPastable);
		getJMenuItemCopy().setEnabled(bSomethingSelected);
		//getJMenuItemCopyAll().setEnabled(bSomethingSelected);
		getJPopupMenuICP().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
	}
}


/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


/**
 * Set the model1 to a new value.
 * @param newValue cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodel1(cbit.vcell.model.Model newValue) {
	if (ivjmodel1 != newValue) {
		try {
			cbit.vcell.model.Model oldValue = getmodel1();
			ivjmodel1 = newValue;
			connPtoP3SetSource();
			connEtoM5(ivjmodel1);
			firePropertyChange("model", oldValue, newValue);
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
 * Set the this12 to a new value.
 * @param newValue cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setthis12(cbit.vcell.messaging.admin.sorttable.JSortTable newValue) {
	if (ivjthis12 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjthis12 != null) {
				ivjthis12.removeMouseListener(ivjEventHandler);
			}
			ivjthis12 = newValue;

			/* Listen for events from the new object */
			if (ivjthis12 != null) {
				ivjthis12.addMouseListener(ivjEventHandler);
			}
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

private void showAnnotationDialog(java.awt.event.MouseEvent me){
	try{
		if(me.getClickCount() != 2){
			return;
		}
		int viewSelectedColIndex = getScrollPaneTable().getSelectedColumn();
		int modelSelectedColIndex= getScrollPaneTable().convertColumnIndexToModel(viewSelectedColIndex);
		if(modelSelectedColIndex != ModelParameterTableModel.COLUMN_ANNOTATION){
			return;
		}
		ReactionStep rs =
			getmodelParameterTableModel().getEditableAnnotationReactionStep(getScrollPaneTable().getSelectedRow());
		if(rs != null){
			String newAnnotation = cbit.gui.DialogUtils.showAnnotationDialog(this, rs.getAnnotation());
			if(newAnnotation != null && newAnnotation.length() == 0){
				newAnnotation = null;
			}
			rs.setAnnotation(newAnnotation);
			getmodelParameterTableModel().fireTableRowsUpdated(getScrollPaneTable().getSelectedRow(), getScrollPaneTable().getSelectedRow());
		}
	}catch(cbit.gui.UtilCancelException e){
		//Do Nothing
	}catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to edit annotation!\n"+exc.getMessage());
	}
}
}