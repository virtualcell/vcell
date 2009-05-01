package cbit.vcell.modelopt.gui;
/**
 * Insert the type's description here.
 * Creation date: (9/23/2003 12:23:30 PM)
 * @author: Jim Schaff
 */
public class ParameterMappingPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjScrollPaneTable = null;
	private ParameterMappingTableModel ivjparameterMappingTableModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.modelopt.ParameterEstimationTask fieldParameterEstimationTask = null;
	private ParameterMappingTableCellRenderer ivjParameterMappingTableCellRenderer1 = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;
	private javax.swing.JPopupMenu ivjJPopupMenuCopyPaste = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjJSortTableThis = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopy()) 
				connEtoC3(e);
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopyAll()) 
				connEtoC4(e);
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPaste()) 
				connEtoC5(e);
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPasteAll()) 
				connEtoC6(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == ParameterMappingPanel.this.getJSortTableThis()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == ParameterMappingPanel.this.getJSortTableThis()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ParameterMappingPanel.this && (evt.getPropertyName().equals("parameterEstimationTask"))) 
				connEtoM1(evt);
		};
	};

/**
 * ModelParameterPanel constructor comment.
 */
public ParameterMappingPanel() {
	super();
	initialize();
}

/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ParameterMappingPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ParameterMappingPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ModelParameterPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ParameterMappingPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (this12.mouse.mousePressed(java.awt.event.MouseEvent) --> ParameterMappingPanel.popupCopyPaste(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
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
 * connEtoC2:  (this12.mouse.mouseReleased(java.awt.event.MouseEvent) --> ParameterMappingPanel.popupCopyPaste(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
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
 * connEtoC3:  (JMenuItemCopy.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JMenuItemCopyAll.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JMenuItemPaste.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * connEtoC6:  (JMenuItemPasteAll.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * connEtoC7:  (ParameterMappingPanel.initialize() --> ParameterMappingPanel.parameterMappingPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.parameterMappingPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ParameterMappingPanel.parameterEstimationTask --> parameterMappingTableModel.parameterEstimationTask)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getparameterMappingTableModel().setParameterEstimationTask(this.getParameterEstimationTask());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (ParameterMappingPanel.initialize() --> ScrollPaneTable.setDefaultRenderer(Ljava.lang.Class;Ljavax.swing.table.TableCellRenderer;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getScrollPaneTable().setDefaultRenderer(Double.class, getParameterMappingTableCellRenderer1());
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
		getScrollPaneTable().setModel(getparameterMappingTableModel());
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
		setJSortTableThis(getScrollPaneTable());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
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
 * Return the JPopupMenuCopyPaste property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getJPopupMenuCopyPaste() {
	if (ivjJPopupMenuCopyPaste == null) {
		try {
			ivjJPopupMenuCopyPaste = new javax.swing.JPopupMenu();
			ivjJPopupMenuCopyPaste.setName("JPopupMenuCopyPaste");
			ivjJPopupMenuCopyPaste.add(getJMenuItemCopy());
			ivjJPopupMenuCopyPaste.add(getJMenuItemCopyAll());
			ivjJPopupMenuCopyPaste.add(getJMenuItemPaste());
			ivjJPopupMenuCopyPaste.add(getJMenuItemPasteAll());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuCopyPaste;
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
 * Return the this12 property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getJSortTableThis() {
	// user code begin {1}
	// user code end
	return ivjJSortTableThis;
}

/**
 * Gets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
 * @return The parameterEstimationTask property value.
 * @see #setParameterEstimationTask
 */
public cbit.vcell.modelopt.ParameterEstimationTask getParameterEstimationTask() {
	return fieldParameterEstimationTask;
}


/**
 * Return the ParameterMappingTableCellRenderer1 property value.
 * @return cbit.vcell.modelopt.gui.ParameterMappingTableCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ParameterMappingTableCellRenderer getParameterMappingTableCellRenderer1() {
	if (ivjParameterMappingTableCellRenderer1 == null) {
		try {
			ivjParameterMappingTableCellRenderer1 = new cbit.vcell.modelopt.gui.ParameterMappingTableCellRenderer();
			ivjParameterMappingTableCellRenderer1.setName("ParameterMappingTableCellRenderer1");
			ivjParameterMappingTableCellRenderer1.setText("ParameterMappingTableCellRenderer1");
			ivjParameterMappingTableCellRenderer1.setBounds(78, 491, 225, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterMappingTableCellRenderer1;
}

/**
 * Return the parameterMappingTableModel property value.
 * @return cbit.vcell.modelopt.gui.ParameterMappingTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ParameterMappingTableModel getparameterMappingTableModel() {
	if (ivjparameterMappingTableModel == null) {
		try {
			ivjparameterMappingTableModel = new cbit.vcell.modelopt.gui.ParameterMappingTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjparameterMappingTableModel;
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
		connEtoM2();
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
			//Copy Optimization Parameters (Initial Guess or Solution)
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

			cbit.vcell.mapping.SimulationContext sc = getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext();
			cbit.vcell.mapping.MathMapping mm = new cbit.vcell.mapping.MathMapping(sc);
			cbit.vcell.mapping.MathSymbolMapping msm = mm.getMathSymbolMapping();
			
			boolean bInitialGuess = (getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE);
			cbit.vcell.modelopt.ParameterMappingSpec[] parameterMappingSpecs = new cbit.vcell.modelopt.ParameterMappingSpec[rows.length];
			java.util.Vector primarySymbolTableEntriesV = new java.util.Vector();
			java.util.Vector alternateSymbolTableEntriesV = new java.util.Vector();
			java.util.Vector resolvedValuesV = new java.util.Vector();

			//
			//Create formatted string for text/spreadsheet pasting.
			//
			StringBuffer sb = new StringBuffer();
			sb.append("\"Parameters for (Optimization Task)"+getParameterEstimationTask().getName()+" -> "+
					"(BioModel)"+getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext().getBioModel().getName()+" -> "+
					"(App)"+getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext().getName()+"\"\n"
			);
			sb.append("\"Parameter Name\"\t\""+(bInitialGuess?"Initial Guess":"Solution")+"\"\n");
			for(int i=0;i<rows.length;i+= 1){
				cbit.vcell.modelopt.ParameterMappingSpec pms = (cbit.vcell.modelopt.ParameterMappingSpec)getparameterMappingTableModel().getData().get(rows[i]);
				parameterMappingSpecs[i] = pms;
				primarySymbolTableEntriesV.add(pms.getModelParameter());
				if(msm != null){
					alternateSymbolTableEntriesV.add(msm.getVariable(pms.getModelParameter()));
				}else{
					alternateSymbolTableEntriesV.add(null);
				}
				Double resolvedValue = null;
				if(!bInitialGuess){
					resolvedValue = ((Double)getparameterMappingTableModel().getValueAt(rows[i],ParameterMappingTableModel.COLUMN_SOLUTION));
					if(resolvedValue == null){
						resolvedValue = ((Double)getparameterMappingTableModel().getValueAt(rows[i],ParameterMappingTableModel.COLUMN_CURRENTVALUE));
					}
				}else{
					resolvedValue = (Double)getparameterMappingTableModel().getValueAt(rows[i],ParameterMappingTableModel.COLUMN_CURRENTVALUE);
				}
				resolvedValuesV.add(new cbit.vcell.parser.Expression(resolvedValue.doubleValue()));
				sb.append(
					"\""+parameterMappingSpecs[i].getModelParameter().getName()+"\"\t"+resolvedValue+"\n"
				);
			}
			
			//
			//Send to clipboard
			//
			cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
				new cbit.gui.SimpleTransferable.ResolvedValuesSelection(
					(cbit.vcell.parser.SymbolTableEntry[])org.vcell.util.BeanUtils.getArray(primarySymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					(cbit.vcell.parser.SymbolTableEntry[])org.vcell.util.BeanUtils.getArray(alternateSymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					(cbit.vcell.parser.Expression[])org.vcell.util.BeanUtils.getArray(resolvedValuesV,cbit.vcell.parser.Expression.class),
					sb.toString());

			cbit.vcell.desktop.VCellTransferable.sendToClipboard(rvs);
		}catch(Throwable e){
			cbit.vcell.client.PopupGenerator.showErrorDialog("ParameterMappingPanel copy failed.  "+e.getMessage());
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

			cbit.vcell.mapping.SimulationContext sc = getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext();
			cbit.vcell.mapping.MathMapping mm = new cbit.vcell.mapping.MathMapping(sc);
			cbit.vcell.mapping.MathSymbolMapping msm = mm.getMathSymbolMapping();
			//if(msm == null){
				//try{
					//getParameterEstimationTask().refreshMappings();
					//msm = getParameterEstimationTask().getMathSymbolMapping();
				//}catch(Exception e){
					//e.printStackTrace();
				//}
			//}
			
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
				cbit.vcell.modelopt.ParameterMappingSpec pms =
					(cbit.vcell.modelopt.ParameterMappingSpec)getparameterMappingTableModel().getData().get(rows[i]);
				try{
					if(pasteThis instanceof cbit.gui.SimpleTransferable.ResolvedValuesSelection){
						cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
							(cbit.gui.SimpleTransferable.ResolvedValuesSelection)pasteThis;
						for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
							cbit.vcell.modelopt.ParameterMappingSpec pasteDestination = null;
							cbit.vcell.model.Parameter clipboardBiologicalParameter = null;
							if(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.model.Parameter){
								clipboardBiologicalParameter = (cbit.vcell.model.Parameter)rvs.getPrimarySymbolTableEntries()[j];
							}else if(rvs.getAlternateSymbolTableEntries() != null &&
									rvs.getAlternateSymbolTableEntries()[j] instanceof cbit.vcell.model.Parameter){
								clipboardBiologicalParameter = (cbit.vcell.model.Parameter)rvs.getAlternateSymbolTableEntries()[j];
							}
							if(clipboardBiologicalParameter == null){
								cbit.vcell.math.Variable pastedMathVariable = null;
								if(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.math.Variable){
									pastedMathVariable = (cbit.vcell.math.Variable)rvs.getPrimarySymbolTableEntries()[j];
								}else if(rvs.getAlternateSymbolTableEntries() != null &&
										rvs.getAlternateSymbolTableEntries()[j] instanceof cbit.vcell.math.Variable){
									pastedMathVariable = (cbit.vcell.math.Variable)rvs.getAlternateSymbolTableEntries()[j];
								}
								if(pastedMathVariable != null){
									cbit.vcell.math.Variable localMathVariable = msm.findVariableByName(pastedMathVariable.getName());
									if(localMathVariable != null){
										cbit.vcell.parser.SymbolTableEntry[] localBiologicalSymbolArr =  msm.getBiologicalSymbol(localMathVariable);
										for(int k=0;k<localBiologicalSymbolArr.length;k+= 1){
											if(localBiologicalSymbolArr[k] == pms.getModelParameter()){
												pasteDestination = pms;
												break;
											}
										}
									}
								}
							}else{
								if(pms.getModelParameter().getName().equals(clipboardBiologicalParameter.getName()) &&
									pms.getModelParameter().getClass().equals(clipboardBiologicalParameter.getClass()) &&
									pms.getModelParameter().getNameScope().getName().equals(clipboardBiologicalParameter.getNameScope().getName())){
									pasteDestination = pms;
								}
							}

							if(pasteDestination != null){
								changedParametersV.add(pasteDestination);
								newExpressionsV.add(rvs.getExpressionValues()[j]);
								pasteDescriptionsV.add(
									cbit.vcell.desktop.VCellCopyPasteHelper.formatPasteList(
										pms.getModelParameter().getNameScope().getName(),
										pms.getModelParameter().getName(),
										pasteDestination.getCurrent()+"",
										rvs.getExpressionValues()[j].infix())
								);
							}
						}
					}
				}catch(Throwable e){
					if(errors == null){errors = new StringBuffer();}
					errors.append(pms.getModelParameter().getName()+" ("+e.getClass().getName()+") "+e.getMessage()+"\n");
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
			cbit.vcell.modelopt.ParameterMappingSpec[] changedParametersArr =
				new cbit.vcell.modelopt.ParameterMappingSpec[changedParametersV.size()];
			changedParametersV.copyInto(changedParametersArr);
			cbit.vcell.parser.Expression[] newExpressionsArr = new cbit.vcell.parser.Expression[newExpressionsV.size()];
			newExpressionsV.copyInto(newExpressionsArr);
			cbit.vcell.desktop.VCellCopyPasteHelper.chooseApplyPaste(pasteDescriptionArr,changedParametersArr,newExpressionsArr);
		}else{
			cbit.vcell.client.PopupGenerator.showInfoDialog("No paste items match the destination (no changes made).");
		}
	}catch(Throwable e){
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
		ParameterMappingPanel aParameterMappingPanel;
		aParameterMappingPanel = new ParameterMappingPanel();
		frame.setContentPane(aParameterMappingPanel);
		frame.setSize(aParameterMappingPanel.getSize());
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
private void parameterMappingPanel_Initialize() {
	
	getScrollPaneTable().setDefaultRenderer(cbit.vcell.parser.ScopedExpression.class,new cbit.vcell.model.gui.ScopedExpressionTableCellRenderer());
	
	getparameterMappingTableModel().addTableModelListener(
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
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

		boolean bInitGuessSelected = getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE;
		bPastable = bPastable && bInitGuessSelected;
		boolean bSolutionSelected = getScrollPaneTable().getSelectedColumn() ==  ParameterMappingTableModel.COLUMN_SOLUTION;
		boolean bSomethingSelected =
			//getScrollPaneTable().getSelectedRowCount() > 0 &&
			(bInitGuessSelected || bSolutionSelected);

		if(bInitGuessSelected){
			getJMenuItemPaste().setVisible(true);
			getJMenuItemPasteAll().setVisible(true);
			getJMenuItemCopy().setText("Copy 'Initial Guess'");
			getJMenuItemCopyAll().setText("Copy All 'Initial Guess'");
			getJMenuItemPaste().setText("Paste 'Initial Guess'");
			getJMenuItemPasteAll().setText("Paste All 'Initial Guess'");
		}else if(bSolutionSelected){
			getJMenuItemPaste().setVisible(false);
			getJMenuItemPasteAll().setVisible(false);
			getJMenuItemCopy().setText("Copy 'Solution'");
			getJMenuItemCopyAll().setText("Copy All 'Solution'");
			getJMenuItemPaste().setText("Paste");
			getJMenuItemPasteAll().setText("Paste All");
		}else{
			cbit.vcell.client.PopupGenerator.showInfoDialog("For Copy/Paste select a cell in the \"Initial Guess\" or \"Solution\" column");
			return;
		}
		
		getJMenuItemPaste().setEnabled(bPastable && bSomethingSelected);
		getJMenuItemPasteAll().setEnabled(bPastable);
		getJMenuItemCopy().setEnabled(bSomethingSelected);
		getJMenuItemCopyAll().setEnabled(bSomethingSelected);
		getJPopupMenuCopyPaste().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
	}
}


/**
 * Set the this12 to a new value.
 * @param newValue cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setJSortTableThis(cbit.vcell.messaging.admin.sorttable.JSortTable newValue) {
	if (ivjJSortTableThis != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjJSortTableThis != null) {
				ivjJSortTableThis.removeMouseListener(ivjEventHandler);
			}
			ivjJSortTableThis = newValue;

			/* Listen for events from the new object */
			if (ivjJSortTableThis != null) {
				ivjJSortTableThis.addMouseListener(ivjEventHandler);
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
 * Sets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
 * @param parameterEstimationTask The new value for the property.
 * @see #getParameterEstimationTask
 */
public void setParameterEstimationTask(cbit.vcell.modelopt.ParameterEstimationTask parameterEstimationTask) {
	cbit.vcell.modelopt.ParameterEstimationTask oldValue = fieldParameterEstimationTask;
	fieldParameterEstimationTask = parameterEstimationTask;
	firePropertyChange("parameterEstimationTask", oldValue, parameterEstimationTask);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DF4D455199C8F7664F858D36CC60D963BD9CAEDECA352B59E3152ADDAEA29262B2641E637F01AD556C58FF65915FA3007EEDFA6C1928D9094ED2C94D1828498A6BFA46487C8B219494FA4134C100C49648748CB66A5B3B21999E7DEA0A02E7BFD773E7B66654DFB99081AF33E5CF73FFB6FF73F3F7B5D5F6176FC103CAD47300763F236F152DFCD8E47251BB96E4554036FB3DC6D610C82CE7177A1
	40EE6EB74DF91036C319511BD1D0440D6464C3FE9872CFCFE4947C853E5F669EA977B64107CEBFCDD076E05E1D6BE66F47F10075331D649FB8D38869CE009140E11AFA987BFF72CC31041FAF61996498B8EEC1A956F9614CDE8937896A64832E9DE038145697A1258C5CDCDFAB559B3EB5055B7CC458308369A8E8B248145AEE11527D5C9FF3120FE0DF3B9A8A25B6651077822070395C455693101EF6DE1CE8989B750926CBE3025FEF1A8CFA84FFB0A41AA626FC7575CEBE4CCF8A22906E62C3A1DFE042498784
	BF3777A8526DEF48A7F4B8EEBD50CCA138A36D585788FCCF83ACBC4A706F1FC57CC6784EBB16D1307798136F4C6153927F6E4F9E62B2ABA773F66948D7F50C4AB5FCACAA5F3C49F5067708AD3ED902BACF023295GCF83EC8658E34CA82882F81F78406D870EC2EA9E95AD414949E0409C8AB57BBD61AE14973E6B6B21C4427588E341300763281D5FE9B0EA70B39B50F687EE389D55C94A89E673541E255C5A87EEB7EC5750495A144F8CF6A35505479855896DFBD8376F794619BEB9336FFECE59F75E0EE7F337
	E9743D7F0FEBF213A4BA290A3EB79A5156F7939FC807EFEC77A65A1D60FFC870654723F86A1FE5840FF9A714259DE73E615E49F83167AD660AAA6E5054C352E38B8C45A64AC339C94D4BEF095F8D1BE81F6F493CECA278A4851E7272A141E3BE934A124BB28A28FFA59D42716AAF4FA8C8GC886D88E10853099A03F0279586F8E14DD270F4148921B27FCFE0F9066E85B2412F7A1952645B0EF8BC4C4BEB0A67471E19FBF6A97A21CD4679FCBD08723B35A5DC3FCFF8C7AE89382C21897FD4100BCEEA3344E7DA5
	3B31CED8A025AE5F24G9DCC06B8C677211237D1BEBEA2FE9C727022902DC14BFFDA02B639447781C5A0A069E8B7777343282FD2103F8940A96B217EA0526B944290AE6A6B6D41604529108BA56112ABD04F9192BBBC70FD2D02697851D2442D077CD6194E27875FD250317A4610FDBEFC050D316F36C3B0670AAAB38A4A815CG89GA9154C07FE6FE6BEE45DF19FE7DCF31B268FA502BF2F2A243E3336D2BD16FEC47AC0BE73CEE49414GD8813CA758BCD05D6E1668A4D4EA0FAD621B92DFFA65C52AB2371427
	FDF44BFD946834C758A60DA33D72DCD5FE3B14D27D179DC5DBA600DCCBGD61DE0B6C8BD381F7AF4B39F911897BB42600002A7EA914A673F1FAD17F8447C5FB8BAF7D6836CA265C80F635E81780B546FB1D946D7C95B3550EFDE954C65GAE00EBGCBG5281B6836C2D867D837881922B198D8E1FDD29A11BD2D7FA7D6F14785CA9712A5659B18DDC75ACBA7EB2C0E5990C11E51B4AF6BBF464D15AD44BCF5E53404D66AFDF86286FACBE04FC8A170400E895C2C240839F91296C5EC34823946F4603F21B7B89BE
	8471518281A9A2639FBC0472722298760DCE09C22FA00A30A209167F047495199A8DC5C3AB6BEB55A154E170B244C7397EAF8FE1EC998B868282F1E23909D47EA409ED02DF188426AD412900C8714F9C46F560C5610A94DB579CFE1D4358681F42B8CDED350CC73D06423EC9989A986A3942139877EEE9A8D668E88578AF2E9133A15DDC4DE2566583ACE615DC3D17DBF9A1E4501A03133E77CD036BA40D915E135118C5F9FA3CD41B77F492E3A5FC0472FA179FFD084CD17234C157F939244DB73E78AB35470C15
	A009662491A755FB427091246A95A329E70115E58FDABFAC04ABD87D1DBB315F8EE7B0B4956A9282D316E088D4959185293C2483E56A688D06C5C2446575C5D82C729EA3318A64DE8410F612452A76A3D84E668BA9C8D902FEBFB5B1149555209D3610184EF419FE125AE127DCDEE2D3163BEBE87992C8299D276CCABBAE2F09364F066F7D35D8670A13515802B4EB23F5A82F55C70B50DF05CF2784186AD142CE5927CE9FFDCBD556A77997AD379EC57F72AB665555244F4A9E362F70565279EA9E248B8152G76
	81DE2C43FAB93D2C5E1EBA4A17310E56F756C9FB90D26FDF65FAAB25FAD9F5145EA6C85B6BA1865713F5D6BF2BB7DCCFE337D8CF674A79101A1057D3D86FDEB72B17DDCF6750AD7514EE55A95AAF1543BF406AADBFC57BFB7A942D0F69BE19DE6520CCCFAA5FF2EAA6BDDA6F8FF23D2A539985F6G7FE9DAAF7534325E43C32C5EE640971E512BB7B94C6A99210E9540F3C67211B34AFA956764FD9E60739B746A7D07DC2F340116B7C9293B015623E37515A368F7E44F7689FF091FB6C5AE43982B2F6FA083166E
	4BB613359556CFE8E07B2F7D4DB8DED6C2BE8BE0D3830BB54F6CE07B2F440BA9DC760EB539FA7B4B7CC64A0731B19AEB565EFFB9170E47FFB2E21FFEA83B56484662CF0FAA570066C8A4B866A34BCF4907834714310862A2475E00F45C970E08B68897534C5F2F9CC3FEF1FDAA21A5FD649755F2B73A070DC6AE2A034FDB15BAD8316BF3431BBABAD8A8493ECC2103D5AF3E1ACBF939E6EC1C9BAF4A68A86D2D9F6DE0B65C5448766B2F976F5531BD0B2054560B6DE4CD5E84BE84608620FB1BE4E7A1C7E3EF88F0
	A9G59CD08DF6744798C479555475889260F05D73F470D5CF6172EBECA4CD48FD6F3D49F29472BF3E9FFC3BD2C3FEBD01E86C0637AE3BD581F88511C4652EF35A8E369C628D75E8CEBBFGEFB3EB73D28F6AE012CEB752DE75FF4CCDD21B74250B39655B9798344E8E3C5FCFB24CEB267C25B4C7799CF9FE1DC184203C9E6DAD14743D856A14DBD83F3776623ACA1E7BDAA2A24C68E48B45C7AECA7354BF4FDA0772FA5FBEA52F797C4D1A3CAEF8F1426035D09E97DA9463ACF31DE1D3A6635560E23CE6C11DA2AB
	6355C7703E00CF7471FE5FD5217488751BCAFBA707F3892C404679A93F48DC2D540EEEE47273018993D9E91D32AB7C4AA422EB1AE29C745411BE49C73F63E267BF6DD666E3F9EEE673F900DBAE73F6510DB667439315B47F77EE6683D4BF578618FE662527F145EF7F1D26FEC89C35D23D9437C47513725CBA430A6718FEB25CCCBFDE28135C42F8289BB8CEFCE8AAA21848FFE7D808C484CFA9D183FFD9B411F52A298B4BDA7073089CE73C8770BCA50483CDDE26C87A53EA88CA22F2ADFEDBE97753DF1C5714AB
	E1175710A5494335C66592416689325DEF9BE03A35C31D792DCC2ED71CCC1752389BE4722F00BA1B65FA0F8D1AE64A5F8331170F27GCAF35F9023E9EC03B98C002E134EF5A957C90B5A685EF8D59B6B736621CA34BBD9FC19E8629442634170E4EC37E6D24C7A25FADF759E4622CFB03859C4E192F5FF412CA223B603028E5801727F1C4CFF96725E4E78BF5C2D45FFFBBB73675B07D93BE140A54A6DDE56EC175E4E647E537017137B0B1DE714F293FF3BC141B9E63B41F3CC06EC602F5846E4F84021A5035346
	E45FF91E3573GEE110D49B6F5DEBB4E7C757881AEF61FC096A697EC0A5AFD52BE773518B34ACCDB748CE403AD762C041E5DA512354CD69BBD3B4B15715FA178420EA81E4AF128190D1B3DD0B62F031DB77D2A8C671D751067BAC1AFG65GCE00D0A7DB436498710CFC5673BCDBG048D707E5E999BCFF62EE644FEE12FA8659FB1222E48DE11E3FB19D395E8078550E78A40CA00AC00ADB28F4D47637250B22D49835573639DDAE74145F6E9BDE75753733BDD2837A6BB55E7010CBFC070438ABC55F38AD9F7E0
	FE9A4A565A59594CBDCE6C3F29CB7F6CCB8D34DD9AE9673E01F6B4669E99D45E19646D6EB0208D3466128409F6D2D7B466D21B2C2FC41BE481FEB3C0E1B76CE5G2C5D4CA68B0E9B6359041D5DCA4620B271BD716E84BC5DB365256D02F15BA5285A51B179D08B1ED5452CC9E15E63A7F01267BD133E00A982DBECB25D43E2D25EEE63D8A26B33B54D4A75D9F2B7BD33CD07B49B002E851EE8E16B55AD5DEC4FF1290D1C77BBA01E820CB858382B2F42F56CB4649381D281D683EC8448F7425E5149F47CEF471B63
	69584C8E03DCC1795322BE50E1BAAC6FD56B506A1C29B3ED5DB744345B274A0F2868D0B96F2FA6F2C2D9A2C0AA40EA000DG79E7C14E33CCCE2369D45C710DB19DE4A4E2458AC8794CF74DCD3EF7E35A19D4F97BD92DF63EFEEC67B9FB23637A8D775C46C0F15CF653E7356CD0D603F6D888E529G2BG328172FAB28A7683D8FB189D7E4F94575F646D5B7578DBD68C3F57E70F97E25A297DED38C7CB4EA535A82788E58B81D2GD681ECG486B4DA8A86DE5F2BEE3BAF31D7EE681DCFBB0F8D1C3C40957EF8F4C
	4D167746ED575E2B550E9F1C1BCE03B16DE24630E6FFA5C3F36B2FB2261D3A3F69DE2DF83DFA9B5AC8E30F4FD602525EAB310B6D3D967632F9F6439CF9FDB5AE2F2BD53CD29E4EF7B39E32E59E968D4F0D079F4434D373D06C5262214441F8A8F7B19EC64E4D0D07C0CCBBB58FDECD9EDE12F918A77330677C5CF878A8261DC915DF2E62015E2796B5A28FCFC359FAG0AFB3989F1799FC31C7B1871F597F9ABE31573CBC89E4FDBE85E586CD675B797C07ADAF85AC74BD752077AAE13810DF14F16695FBB2DAB53
	3EF7FA3ECC7B5E6945325D948FB41437762C7C3F4BE63F17FA2DCC7FDE4AD2214FE7C705B61F4E8AEDBE7BAAE66773FC059E1F54DF5611B347E1301D7FE366C34D1D4C0726E51F595B0577960BA03F9CA093E013DCF60414592533F32BF466CD4B165A516E58E61154A61E0DB32F234ECC5F7A8569035D174EF6D72EECF73DFD7C0D50FD56006B5A70D4C074CD8A448434BFE19A6F0FE1766C198A5029952D90D16F87B8B6DF2F2F4331D754476665BFD4D4F23173F20B47A786430C08CB60430E71F1F971519B92
	4618FDB7D66A7B52AB155A3E74DA2536AF7D39F2F6DF5AFEC24F17280E7ED344BB69183B55EED8BDF16C7EAF2927C02A6394034E8A59EE93ADC8C3E38E6C9D8B877DFEBCD060D86C5DE14917ECE86CE37B0EF5D6340F877257G927B187FBD550231B535CF5B27D6EA6069FE77A5320F496C237B5D9C997F8A41E7AB70F49FFC2D0D3D375B88E54EFEE6DFEB153EFDBA2B346DF3368A6FE3A2412970182036013B2AD8AE0B2E50F01100EA26F5F852DE644393C20C8D3D24BDAD53EB7FE9951E7D0F7ABC3E30F403
	CA6D7B4BF7F7917F08C40488D9ACDAF911176CBA514B6C5AA2863BAA647E9E2E5617FFE535367C0FD76B4B7F2B6A7872BFDB2DAF7F3A6A78723FD02D6767D456C617D2D69C3FB4EE4E6BC73FCC835B2F6AE767E6E6771B4C5F474341C970F85AC7DD0D3EDE1AEA347552DA232F172E1A78FA692B51574B391A78FA992FD17B8515F15700C29F16286D17566A4B78E02D360C0F546A4B78D8EDFC991F2C5517B123B63E0C3F2D1D5D7699834A78D5990DEB03CA9DC471C3C3CA7C890E4DD9F7F6E96D05B27B7567BE
	CA6F03E1A53DAA195ED837963D8DF1697DFA863DEA195E9107963D2DF1695DF1CEC96F24CC6F3FB469953963516BBC2F24D7A3638FB7627B9BBFFFA5B8A542FE480D632F21111DD915C07EB40087E05ACD4F2E68BC73234B68436330238733120954BFC06E304C2CED96545F88D088F8A300FD000D657ABA0C710A99290262B7F7103B98F0663481FCC37EFC93073C913A831D0C6E9648978E82CDGBB00FF1051CDAEC61F6DD05EF748F22E7928C8DDE6767BE9591E311D4AB27A0CC6EA3768335DB1E550D09E8B
	9B6AF6A87DDC1A23292D5E32B0DBADG9E1387E75AA8EDD0EF4EBC5B027AD9BD28BE239FA2782CC175997D73D6F6C63F81F5B244561FAF10F345E510CF17F1F52DECCD1ABD446676CDED1C4C73EB0A6F1B5A3E0A3D466CEB4EEFFF2DFDC8E329DF3ABF2D58BB0CBA380F64730619CE16117300914807E45C55EE063BA6631A9C8C17A063DEF7B2DC62B03B7B99EFE4EB18E440E58D33B17302E9467984DEBE5557B787711D151D8EC03AAEDBCA7623583E689CDB172DA5B45BA1BF82B0FD0E3DB718BE445E9B245E
	33184BAC50FE03126D7A5C30709C753734F351737A4449F5865A67D1B36BF39314171EC71C5B3CC7254B1F6750BDB5AD3FE5C6B90E933C730D162F18D10E7CFE30CDD97E7B9965282383D2FBAA5B138719ECDB1FDC46799F1C2F7B7B0D50F9AAD32214AEDCFFE7CE521F2F66D2BACF8DA96FB40A6E729BF2F46824CB6D334EC7F5C47573C5A753CF6185261F7BBA557A79F0067C3F6BD46B67B7394A725DF635FEFEB6C33F963BDABFF7CB74296F9DA16BF7AB70633D407C31560A38EB10DFA2633EE5E5E3FE7985
	B6667BDBF08C2C1773BC4963DBB11AFF512A9C17D86F2B9F7B0696E59FFAE792DFEE6C7F29853F79A9B158A3C4FCD74953A50EAB9FA1F7F42D4C269C6469BC63ECE5730C88388400940055G9BC658BC7333FE9C0B5660943E7198432D1F74FB9CC169E7651F59F56F4E72F9699D0614EEFD672E9CCA7B97DDB367408FACC819FEB7DB235F3734C43FFF5C4A49BA75B74E6D77B6D767586E019BEEC7673225CDA41E716AFD61C3846F62557B42676418BA82E52BF8F69FF6F79B5A2BF4B423408AB08CA082A482AC
	9BE5FB4723E6FC9BBF6B193DAB983243825B2F582ED3794A4CF37B9D52235BD96CCA7B718F38655F306A3E83DBB5CAFDE03D1416BEFFE78E26D43EF29B4A37E7AC23209C40856087188710B446648B1B257BD4BC6EB00BA2BF66A52F826543931FF0B90A46459A255D5B011CD29A4B4D87A16D63BF51BCB96900694AB17A89AC74DD7C04EC7B791DB72A9B6A63CD96FC73F5C9880B3EB15ECF17014DFC5899747B462EC8E3F30844B6FCF314BA46461D430AFCFB03E15F55E0C00CED494675C5926F4C68D3503649
	C35E4EDA774F421751C3F5CF7BF96AAA6E37224BD3FA23CB4B0E3762BBED65D32F8ED2ECB2FF6CF2DC68E969358D36DCF03436B2BFC83E2C0C8935EF5C24799E9DA1C162E111A79A93685CF329155D8FE7FA585C3334D5BD775CBAE36E48EED54FBD58E7347C2DB6755CB3333D35CDBD7730766FC93647B8D5A8B09F5CEA7EC84FFFG09F7E13D30C48ECC486F931AB8590FFE22785E5078752FB9F31A3E4EBE245F1535696F596FEB535E33BF5026FD1E78F05B6CE7640F36691F112FEED37EF6035A6E8E322E58
	8F36BB8DB082A082A448366CEBC7DB5297826AD852621940B3837C051D487A2F334DF286E30B7F3BBC1B8D5FAD0F86C5B1B80974995EE1C3FB094190C1D21C4B06BA8A7BA63CA22DAA6187ED648D35B0CE50F42C16DA718CE546D80D8EEAD3DFCB0F4BE6B15BAF74DAFA9CF6FB33396702595EEF9E6865D89C3849FAE216766D0E9E5B20235B25C50136FF5C0AFBEB0B00EF1CD8BC38AC30F9E16F8F3F4B6D7D20494046265A0F1688B48EBCADC4572251F961B532E7499B4FA858BF4E6C36266358ACE350998E06
	A064DE21D8720B88E9BCFC562A0C63CDC04FB5BE7BFA0F7617500972C524F1CDF17FE0577705FB6C71FD61FEBBF9578EB6F4DD89C9E7C9E9F66CA7B8BE9E91E41FFB58DEA463EC91E7B06243B3A82A9B23E3BF24DE10E1BEC08A40CA00CC000DG5CC4C6C1718453D9E7D769ACBA231BB3626FE15E47FE87743FDDBB1563112AD262EB9BF95BA83F6FA3CF19E964135662A416D7C17F98EFA96E8E0233CBB8CA2746212F4786C7FCBD36B93447D417A3F6CC75B83447145B219CD3527926C4B7ACDFC8D07C188377
	E6749C290B8F7093A45E50FDAFFFC87166315F67913D943FF7FAC7945FAEE07F9C7DAD9E0CBD338352541739EDA277D17C1FC509DC9E9E6908DCBEA67B4DFE3F48EDA307B5A2D7C0D24431B7EEDFA86764ADB70F694E49A1AF9D87733D6AB7EE7FC256A7AB813F564BFCE91F65381EAFC1646C1D6401AB5EFF519C7060FA2A790AA8C4E3782FADC8BF426AC80552DBAECB019407D81B7F87D0CB8788781B24F4CE96GG74C1GGD0CB818294G94G88G88GC5FBB0B6781B24F4CE96GG74C1GG8CGGG
	GGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0896GGGG
**end of data**/
}
}