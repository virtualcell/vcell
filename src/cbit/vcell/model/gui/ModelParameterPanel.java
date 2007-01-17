package cbit.vcell.model.gui;
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
		public void mouseClicked(java.awt.event.MouseEvent e) {};
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

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4DCE5F65612ADD55C45BD51CD32B916E6330A4A3AD94DEA3A4D76D00B5D2CE6F715A5B65D468DBDEB5B6CE9EC53B527A7DD716CE3G4398C308C1A59A95B7D193C346C0C8424F70B38C436FG83B38C20B170E09E4310474CF46691A09A6D3D5F773EB7EF9EEF180423651C4BFB5F3D5F7D6E7D6EF76F7D7E5EF0A5EF2794649ACAB8AE3700137F4E67F25C71A60E2BFF2C3D14612A5F4FB4F22A
	3FF7G8EF2E35EFCF83683ADF1A253D84C5DB7D088658BD0B6C379B5F8BF44BDB01055GAFB16424816D1EFB9FAEDFDC4E73FB191C3D24FC47791760D984B08AB8FC263D0F729FBCDFAA6393E4BC03DC8347A59E47BACF1CBFA263F6C11DC24035G8C9D27750370248AAC2B2D166BDD38B5155B7DE848309F5BD1354960F119F7377CAC63FE1F3B7A844A7ABD5193F94A216C81404E67F1BB4E1A60E9F6DC72B60C0F79A45365F1C194CD53810FA01A3CB33E5A5AEEFCF570A1FEDA10041003778BA24715FE00ED
	B6D79712B6B8AE9B5ADBCBF0A54DA867823CCBGC99FB03C399371BB601DBB19E9ACBD497AD67F3ED956FD685307382C4A697C83B17AF67AA46D535049C85FBCD3CF998E13F170D8515E4B013681E0B340AE006272CC63E900B748783794FEGCF4B18E48DCCCF877C16E030D97404C887613D3696A8B22EDF988F04BC9CC747182BAE5751E7B120FCC5574DC7ED12FA060DF7DA7FFD5C3687EEB76C5531493654CF8D6D6554962365911BD059EDB1E55FD09E6D0F513287B9356C041EADF985BA32D36A3664AD17
	5BC9D5495ED10EE31DC8FCE0933CA35FBE9CF7023F1D604BCFC57054B7DFA7F8AC5B01366E94730D21A2260BBDFF8DD7DC71ADDDBB2CF8B849D0E222BA141B343A94F6E15BC3A6AA738DC5173788FE058ACFF5E9A6FE0A652DC0DB7FE126117A57348DE3B579F426B18DA083E09BC08EC0E9C526313C027958AD059FDE230FC1CCC94DB3BE51A304B84ABBEDFD951E421C9462BB7CE109770F8BEEBE646347C4A14C49F5AEDB5186E3D1FC1F925F9F8799ED02DF887112AF60D7A2B6CC6BA4D88FE21D10C028AE5F
	34G8226039CEBB749FA881EA29F1686029EDE92A2B5A8FD39954764B26F06962181F827F6D9550A7632C37FBDG12E20783AD58DE179002FCD1DBEB8F84AE4D84DD5893AE23826DFC11640EC4F8CF29E0B63E3D8DF10515194632CA56CEEB4B4B2AF6DAFC6328BE9F1AE7B17675E6C864DC9554F7814C81A48124D4B29F7A0FA166C3AD7B3F4715EF39CD5707CA4CDB72B6D6D25F492A544652EAA283754C815A11B3B027GDCB84366003266A1CDFBCC0745E25D2521D7E94AF3F2194A28D9A043A8474CE2B28A
	B465BCCDF92F7C24764F6F40313C817A359AE06399B686FF52D2C6FD3A198F8B4C0BFBC3600002A7B2A2F24E6B2A12F5BBC04A484FF12FG9C85F89760FD0093G2F49F2D146A9251F4788FF96484E81B8D615E93483C8G2B81B2GF682141C05B1G70GA4812CBF4B46223D73F19DBB98D56FDA3D54FA28F9347A9649FADECFBE7F3200766D8B5D3E294734C02E4BFCC35DBFB54FFE0D9D0AF47A7F7F855447E6ED280FF0D970CBADC2D070FB60A5AC53BE37E1FF643CB891D0F89288BE88F9548A09AB2C60EF
	32E13FF8C98A7946E6A441A9C8124F6F0D503FC9E405E7460211944CE42D34218D833310C7157AF758B0870D877CFE01B83B42A253D3C88E94C4E19A143686E67C92455F590AEB45CB423C1C036FE9FD01439CAA4EE0BE276B2FF5BC0ED3B0641B069042A901ABBF0779F1263AD4E523ED605F380E6400D6F20E65365923AC379D3BF2B717BE92B4684D55AB3E7B8D03F40E661244F3115CC6F57AF61B3E6EFFC9F2310C8FD3DDD70968ABFA8B42BC4273189BF51E0E22131CCFD8E81D4C5197D81D8ACE667B556B
	0813A6FD610ACA06BB7ABA7AE827A3901C89F68B7E198E2B03E3F92A1E4C512950170DGD967D81EFA339D69EC2E109314B5A00AF45800D6FE9EED7B9B124F19FDF61E233645777504FE404A689214134EEB697F5E4268B9D0AE290E5069585DE2D30F5DB119EE0767E8B55375450EE274C161FFE784186651C8DD0A1F9C6CF8D9C3F34BBEC36907BB50E7C4551C3A359A6572DDECBFB1D7CD672AE4F826GE4G3C85F8DA6F341D55BBD6C3F5B35750F94DD1C36BA555E03DBFA875C6D5F4ECF72E06761756FB34
	1B555BAA3737CBD32F2A966BDD68E3758AEBE9DEAF2B0D34EBD274AB6EE775EE28257A2F2825F3AEBED13F49BA2C7724D2AF957069G5935D4FED99D15CB6B2DF232FA6B213C39AED63D9E972B37934A7966C83DE3E6F5BD43G2B9784FCE24CFA8FAA75D24DD47FF4B32D1793D5EF5E4D6AD55553FA76FA3DFA66C1D6AFD12627484F8D75349E0D53E7CF207F11BD5D94FF191FB305E7A17F5556F612E025FB376DE47D0575F75433BD5ABEB346D5D9C326319A405140724C937B599EED7925D4EE34B7ABAF567E
	F332C1760B06C81ED9B1B81BC763764F3AD066E620EDEBE0F1303CC33DCE34044301F19FD9224A3EEE6DD467A10AEB6FFC911E933ED0D86A0009E20E45C5CFA76A0BEBD899AD5B634DD74FF05731470D24ACF9CFF435D55D7F0D872E9A7645687FAE395F450D117EA7FF705FF9F4EF313A056D419DC0CFECE4FB70E704A3317413F523FA0C1FE1FAA4DF79AEB7FA5B2A18FA244B7233D5FA6C5ED2A56B71326DA50E665952A698E70044A644775AF03E429823FA3DD02A682571A3161529AFBDE2E367G1B1B18AF
	8D13B9B15F82718DD085C0E55F5445E45B8117GC0737DFFF521EC1C83E88E3D5A284E219B219E578C7185D05D4CF88E5BD1CFE2A70E6DDFFFE7E912F9B65F37065B30B74920F71EB0F9578A0323195AE62EB9E223166C278CC3GD457E1BB53F59354612CCCEEFA14DC0A7B2D1D69CFF90FF73FA47BDFB57085955E873A976A7C3D3754BA97724BF4F5CE7A2D5710EA253ABEEE0D681C7E44D3062CA7184E4FF6ABE35B82E35B42641675A15E6777C9BEDE74DD910E1F20751775317A09D0B73D050DFDC983BE49
	DAC5517333E126678D1B57F32507EE515593E1CF0B2C1FAD22E75276278CA95B191EB57DCCEE906A245A181E02AD5A0EFF68E7F64C06BA252D3057A6F3F0C91BFA8EF734527D58DCABEBE74289CB890EA7933F09BE9CC2E8A2901A96BC47511BCD7C2CE4A26BCD130510ED78FE02650C0C4398EBD3E469A1895356C0F0FE4422E9469E3B1D3A93EEEA33B50754E3EB7E62222E4D928FCC9A12DA65B5C6EB44E6C3B02E22B236FF67E436605AA08E5A18ADFE2DEB0BE09B0B39D99763CBG5CC605AF35DD0FEFF79B
	7301AF0A6A55B6F0C0E2952E53889CBBD7B9B940F4A8EC4FB4B635B39DC29D3A63584EF45F2E70A581AE231D0DEF2B7B9409231930E0A27F9DA1A19CDEB8365DC8E3C3AB0F6D64D1BC9B8BE29243F1A57DDA500F88A3F4036A32464DF44981BD2ABA18AE46C1D334AE7DB0DF71711421ED8A032CCD915ADB2B3439F9D09B2FD4F729689DCE3B46A6537AC8072AAE5A1D6E991FDEF08E382DA3F24E302BA37A4C81011E0FFDC15682791D74FCACCF417FB941172870349F874DAC1E0F81AD31131D692C6941BC3E93
	4A05DD305E8228869882186BE2EB00A7BBF1FEDE744C2C438F7D747322B3EA53464E2E3AD0AE6C3364F2F217BAF7D1BD0A9D68E74BC166BA00CDG59G1C1D69701ABD2E8E36B9DD9D281D5734AC6D0CF85DF573496711FD581FB2505F8C6082908112147E707672F87DE1E70BF2C728BE25B664CB33AFFEF635309F6F913E0C6B6023797144C76A3B0D7C031D869CCB3DB9E5375CF6FE3776FC6ED15B355A30C566253E7CB332C6996A263EECD4703FA2F8C90527BEBEC270D8CE865A6EEEF666145C95CF7ED0CF
	34DD643B2338FC1FE978E88EF848F61653D9AFC27E633D30C0B3711EE91F5F948E04A489479796ECCEF827BBC536C7CDB72B57D74BFB68B9ECBABC33G683A6E070DEC0F3033072D6BF88B5A60D8AF789D002B17457981324792211C8810823091A09BA087204C417C727B7666F8FEE9E187B72E0072EA559E3EB09B0E5817E67B6938FC558EBDBED337164F212927AF4F3C006FADCDD95434C36D1AC0762489C0CB8158G1085308720248FF2E99F33EBEEF7DDBC3B120987ECCA4C385020D44F7FD920673559F3
	EFCFDC3EBEBD3E1B7B16A6EFE5DC3EE01F1EBD7349DEBB91E8EB81B682EC854881A86D871F6EE776F4F64775534806719A7C3431FFE97D6C0E4B676A5763ABF0AE4DCF8FAD60DB60277DFAB9E9D3815ACDE72F48D60F721DF1D79B5BABA674335CB1CF4EB5B6C2F9AB40AE00171C194653GF60020130D491D5D75576863D64035878217F406C36E67CF17E81FFF08EB1FE4E7B49F6D77AB6D2C5F29CEB6EFDCF5ADCD071B9770E9F5485655E13B22439EC5070A012569503800CF2B43E9171E8EF7F7B09D5ADDCC
	07FFD9228E4F46556186179EDF2AFBE9729EDC40271537C2D75E254125491BDF40E7521457E964513B556748595EA620ED8320382D35086386A04F8DB03BAFB363DE5B2C143FC14AF84EC24B2F1935F70ECB81ECDF8FCFE51C7ED2E4E86FB599503CF1EFCF6C3B257BFB746F16B674685FAD7DCD4FC10A07B654B7780C7E77BD0B5FBD7D3CA7765DD329A3361EEFBB7475FC57212F67C94762FA1EF1AC2E67CCDFECDDBE6B53576546BEFDDD927B9657A539AF16AEEF1231FD5200EB4E500CDF72CD8B24E9BA6788
	F33897062C5FBF6327D382369E566EEFB8B6777F55907A37EB004DA76F77D7F28B67931B47A785C22C91174007FAA7A614895E999446D91C7DB3B9778CC2FB89CA8C7D32956FF512211C86308960E90516CDE8F8CF31D93EDF403B83CA93DAB18E10A7D9660967731B2E21CEF42CFDCEE4944871E23CC63EEB15C1476A5FA43CF3DEF0D75A83ABA50E1D157E244BA857F1C881C70522135706ED682CA11C632100A862B187476611BD66C2D93FFCB75B1BECEAC0BFB1C35985A03A19EDFF5408F9B4412DEF2F95BA
	F83A8F5CC276BAE9EE3A8F4CD5703FA6788DAABC5D9FFAAD6CFCBF83E84787191F9DF6460E25B227FEAC3D6744BB16F0E0A6B4AEE863651433D421C5D6B83848C2B353BA67883F44073C4202F82BA77C14960B3F5509F78BE3BE0FAFA45F1E52313B5D2E9E3BB34A387F745583A44643E1A1CC96E1AD3C444BBCC2B76331C9016E330A9EDFF745364BEDAEFD3BFC5B955BAEEBDC716DF22FAB36DD6EF745374BDF3BE265AA5A5753BD6A3E1ED3708F776DE3F61B8885269D15324CA2F7EC9B3C625637419B6E58B6
	7823BB3E8DCE3AE35B20529D5F86F5EEFD5F385C2F760DCA256FE6274AA6568AA59FB95A7476B6C90331739A157F270331EDF653203E4D1287E35B6CDB0371ED16B2985BE6F78D4637593A41457D06F329EDD429586875F6BD9B25ACE2A35A5E7A28764EA86DBD2D5B5E7A386DCD8E285B2BD25A3B2FC32F3DCDF15BBB62D637F7D641EF0F42C762E74D209AFFDE413FDC035F0E087CFCE0C602BD4590666193B56CECEB9B68319B608860CF8F51B3AEBA577CE096FDF882F670B08CE4D2BFB60C3C7FD94BF81321
	7EDA008C00278172075959FCED8D66F9552CD4C1477847C37B55E39CB5676DA15F95097C9839779307715B721BC7B805EE7D14FC6F223EEFD2E89D7945DA1AC5143F27C9F927C8CDA3F7990C6F1B2387975000D1993B674DEC6C5601BE994351E336F5B8563CE8EE447E6F9C565E8D3492FC4E3076EE60678D6CEE60250FE0BF7F915B13BCC14E9733203CCB419DEBE27B147C0F597C7DCF96AE623F2A77E94B1731F7D8FC4D74352FD4866D33171C099C077ED6FF4C6CD0C1766AADD0F6A9386788EE944AC18577
	8859CFFF8665240F591DD9DF8DDB072CG5C639FB31F7FD7D354998DDECC5556B68770BBA9BB8DA03A36DAD9D3A87397DEE0EB2B1F10B61B20BC84903C403E8D19FB0FFD1310F6679A4E55257FEDC8366B2AA171027C7B088B11737B3929278C7279C89D13398368E51FA04EDA273E9BC47FF9A4176E01A9FDA60A0E3E0EF7FC917ADDD1F454776D82B57D9F2368E82323B2BF6D5BE36F323E65BC360E93FF189073F797131F485F3D484F6427D7663A6E3A12C75B7945476ABB0E62D522A1B7C6BB99B27F631FC4
	ECC44739249E6D13FF9156159759589FEDC01C994A9E85F73501454F5CC596BF4D0D386FD92B146D240C5FD551F2D6035A47315E179FC74FC4490835DFDFAA50FC3723915F7999A950AF04FDD748B7BC9C376BA25AA5370979D7BA14E92E2FEBE2393EEAA453588EB089108810BA42F27D67ED64FB84D8D0501C1D40F30A3C7676E85A4631886D650EE85A767188ED1B06F6G278959CE03B5CB3BC7941648F747F57351DC3F32167C46E3C43BB7DAC370D9A35A3D514F141C148D3416D1F6767BBB8147A0954A99
	G5BGF6831470B0DF73EC7F14B9F21A0BF36E6B8A846D30A094D5478B727A29EEE93F41795B3DAC76577F689EEE4357DAE2FEEB554253F871484F633FD9190BCF5A3FE5DE723BA4202D8348825889400DE59ACB47D87FDE9F156FEA716841A2C97C78A4795ACDB94870893391B4AED6E85B291328A9EDE3F4943FD5F773D33A27981D106EFB4726E0E16A623D0A9F1E2C47EF43AF8BA149B74E0BF4B9534C079C8151B7BEAF47CDAB49BB782DC57918327EE9C0791301106FCA40AFAD64E4B167A63988A7EC91F8
	534790373C21EC915B270DD19BD2B93F38026B7C48B20B5EE0D2DAE9539F6129BAA6B0F5923249B2606A9D6937B9BBBE320D7436363271CC19D567606A97EF547DD69A21F01C6AF0C4FE663C32AA174ED1FC933B8FF50D33B9EAE513F60E3AB5EA0EFA2CC9BBC7214C887D79A66D9C954D7F21C5BBC7B1FE6A5FFFD14BA96F292A77DF56FC75EB231DDF298C6A574F893177FA06897D3DDE61047EB9E4514462E71E2FCE44BE1BFDF3C27D3BG9AFFB9BE9C1BA47005D4004DG59G3B47D9DC5FBB01BECAEF3435
	B1ED73F8F12F093F72121473906FA267A15E783F8D3B430B5F450E85A4A9B00D6DB37CDAAF0E17948892A4455D6DC59B05FC5EC909D615715F77220F0B4284C153D8BB54D0210D35C8D01A5C36FED70755E29FF1DA7BFB6D76E6CB7F0845BEE899F6F2AC0E279B4EAC425F5E5B5F71D1EF0FCB2F854A7F03865C63D9857C860845732C40727311FB3F439DF93B4940E2CB6BC7459E9A47E6CFE4CD95494F97AF11B3DC20A5F95838FD60BB4945482500F40482C1C8197394CB3E4C1763E122C9BD5724C1FB691E45
	57D2D4DE7ED46CF15FB795FF5CCB2648B752B0DE2E7920FC5EF0F88AED961818888B0AFF3DB5D52C60BA420EC058076794548E370AE5704C82FDF78114881946F200A600A1G91A0C1E076D9B7F5FC917B508D85716D906FE33FA7993FD4240EBDEAB6D92F29CB98C74A37EE6473EFA7D474FB65B5AD493BA9A09FF3A3455DC8F09BE49CEDE735985B0E5F91635BB1CD540F1F7B4505717310289FBFBF965571C3F18F4B6D061403F00A5FA462FE021EDDF473FE5ECBF28B5D2B716F293E7F9B74F924C90ABFB237
	BF02EF97D0BEC7FF63F508BBB85108FE778CD7A0417A13D6A903017010F18ECBDCBE9E9DC8DCA1BE4AAC22A8F18564D0C0620C6409B876DD56976A79F24F3271187365668979DB7309C8FCD11F9A1EC4DB9C71423E564BFC67EE6F29D83E83D951B94D03D63CF8496277601A25F9DE92A2797962A4369FE6F5E4A2258913C6B947B01E7F83D0CB87889D370F41F395GGD0C0GGD0CB818294G94G88G88GF8FBB0B69D370F41F395GGD0C0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4
	E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2D95GGGG
**end of data**/
}
}