package cbit.vcell.model.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Vector;

import javax.swing.JDesktopPane;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelQuantity;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;

/**
 * Insert the type's description here.
 * Creation date: (9/23/2003 12:23:30 PM)
 * @author: Jim Schaff
 */
public class ModelParameterPanel extends javax.swing.JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private ModelParameterTableModel ivjmodelParameterTableModel = null;
	private JSortTable ivjScrollPaneTable = null;
	private cbit.vcell.model.Model fieldModel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.vcell.model.Model ivjmodel1 = null;
	private javax.swing.JMenuItem ivjJMenuItemAdd = null;
	private javax.swing.JMenuItem ivjJMenuItemDelete = null;
	private javax.swing.JMenuItem ivjJMenuItemPromoteToGlobal = null;
	private javax.swing.JMenuItem ivjJMenuItemConvertToLocal = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;
	private javax.swing.JPopupMenu ivjJPopupMenuICP = null;
	private JSortTable ivjthis12 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemAdd()) 
					jMenuItemAdd_ActionPerformed(e);
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemDelete()) 
					jMenuItemDelete_ActionPerformed(e);
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemPromoteToGlobal()) 
					jMenuItemPromoteToGlobal_ActionPerformed(e);
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemConvertToLocal()) 
					jMenuItemConvertToLocal_ActionPerformed(e);
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemCopy()) 
					jMenuItemCopy_ActionPerformed(e);
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemCopyAll()) 
					jMenuItemCopy_ActionPerformed(e);
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemPaste()) 
					jMenuItemPaste_ActionPerformed(e);
				if (e.getSource() == ModelParameterPanel.this.getJMenuItemPasteAll()) 
					jMenuItemPaste_ActionPerformed(e);
			} catch (Throwable te) {
				handleException(te);
			}
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			showAnnotationDialog(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == ModelParameterPanel.this.getthis12() || e.getSource() == ModelParameterPanel.this.getJScrollPane1()) 
				popupCopyPaste(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == ModelParameterPanel.this.getthis12() || e.getSource() == ModelParameterPanel.this.getJScrollPane1()) 
				popupCopyPaste(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ModelParameterPanel.this && (evt.getPropertyName().equals("model"))) 
				connPtoP3SetTarget();
		};
	};
	
	PropertyChangeListener ModelParametersPropertyChangeListener =
		new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() instanceof Model && evt.getPropertyName().equals(Model.MODEL_PARAMETERS_PROPERTY_NAME)) {
					ModelParameterTableModel modelparamTableModel = (ModelParameterTableModel)getScrollPaneTable().getModel();
					modelparamTableModel.setData(modelparamTableModel.getUnsortedParameters());
					ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(), null, null);
					getScrollPaneTable().invalidate();
					getJScrollPane1().repaint();
				}
			}
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
	setModel(null);
	getmodelParameterTableModel().setModel(null);
}

/**
 * connPtoP2SetTarget:  (ScrollPaneTable.this <--> this12.this)
 */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		setthis12(getScrollPaneTable());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (ModelParameterPanel.model <--> model1.this)
 */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			if ((getmodel1() != null)) {
				this.setModel(getmodel1());
			}
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (ModelParameterPanel.model <--> model1.this)
 */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			setmodel1(this.getModel());
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}

private javax.swing.JMenuItem getJMenuItemAdd() {
	if (ivjJMenuItemAdd == null) {
		try {
			ivjJMenuItemAdd = new javax.swing.JMenuItem();
			ivjJMenuItemAdd.setName("JMenuItemAdd");
			ivjJMenuItemAdd.setText("Add Global Parameter");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAdd;
}

private javax.swing.JMenuItem getJMenuItemDelete() {
	if (ivjJMenuItemDelete == null) {
		try {
			ivjJMenuItemDelete = new javax.swing.JMenuItem();
			ivjJMenuItemDelete.setName("JMenuItemDelete");
			ivjJMenuItemDelete.setText("Delete Global Parameter");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemDelete;
}

private javax.swing.JMenuItem getJMenuItemPromoteToGlobal() {
	if (ivjJMenuItemPromoteToGlobal == null) {
		try {
			ivjJMenuItemPromoteToGlobal = new javax.swing.JMenuItem();
			ivjJMenuItemPromoteToGlobal.setName("JMenuItemPromoteToGlobal");
			ivjJMenuItemPromoteToGlobal.setText("Merge to Single Global Parameter");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPromoteToGlobal;
}

private javax.swing.JMenuItem getJMenuItemConvertToLocal() {
	if (ivjJMenuItemConvertToLocal == null) {
		try {
			ivjJMenuItemConvertToLocal = new javax.swing.JMenuItem();
			ivjJMenuItemConvertToLocal.setName("JMenuItemConvertToLocal");
			ivjJMenuItemConvertToLocal.setText("Convert to Local Parameter");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemConvertToLocal;
}

/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCopy() {
	if (ivjJMenuItemCopy == null) {
		try {
			ivjJMenuItemCopy = new javax.swing.JMenuItem();
			ivjJMenuItemCopy.setName("JMenuItemCopy");
			ivjJMenuItemCopy.setText("Copy");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopy;
}


/**
 * Return the JMenuItemCopyAll property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCopyAll() {
	if (ivjJMenuItemCopyAll == null) {
		try {
			ivjJMenuItemCopyAll = new javax.swing.JMenuItem();
			ivjJMenuItemCopyAll.setName("JMenuItemCopyAll");
			ivjJMenuItemCopyAll.setText("Copy All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopyAll;
}


/**
 * Return the JMenuItemPaste property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemPaste() {
	if (ivjJMenuItemPaste == null) {
		try {
			ivjJMenuItemPaste = new javax.swing.JMenuItem();
			ivjJMenuItemPaste.setName("JMenuItemPaste");
			ivjJMenuItemPaste.setText("Paste");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPaste;
}


/**
 * Return the JMenuItemPasteAll property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemPasteAll() {
	if (ivjJMenuItemPasteAll == null) {
		try {
			ivjJMenuItemPasteAll = new javax.swing.JMenuItem();
			ivjJMenuItemPasteAll.setName("JMenuItemPasteAll");
			ivjJMenuItemPasteAll.setText("Paste All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPasteAll;
}


/**
 * Return the JPopupMenuICP property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getJPopupMenuICP() {
	if (ivjJPopupMenuICP == null) {
		try {
			ivjJPopupMenuICP = new javax.swing.JPopupMenu();
			ivjJPopupMenuICP.setName("JPopupMenuICP");
			ivjJPopupMenuICP.setLabel("Add/Delete/Copy/Paste Menu");
			ivjJPopupMenuICP.add(getJMenuItemAdd());
			ivjJPopupMenuICP.add(getJMenuItemDelete());
			ivjJPopupMenuICP.add(getJMenuItemPromoteToGlobal());
			ivjJPopupMenuICP.add(getJMenuItemConvertToLocal());
			ivjJPopupMenuICP.add(getJMenuItemCopy());
			ivjJPopupMenuICP.add(getJMenuItemCopyAll());
			ivjJPopupMenuICP.add(getJMenuItemPaste());
			ivjJPopupMenuICP.add(getJMenuItemPasteAll());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuICP;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			if (ivjJScrollPane1 != null) {
				ivjJScrollPane1.removeMouseListener(ivjEventHandler);
				ivjJScrollPane1.addMouseListener(ivjEventHandler);
			}
			getJScrollPane1().setViewportView(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
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
private cbit.vcell.model.Model getmodel1() {
	return ivjmodel1;
}


/**
 * Return the modelParameterTableModel property value.
 * @return cbit.vcell.model.gui.ModelParameterTableModel
 */
private ModelParameterTableModel getmodelParameterTableModel() {
	if (ivjmodelParameterTableModel == null) {
		try {
			ivjmodelParameterTableModel = new cbit.vcell.model.gui.ModelParameterTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjmodelParameterTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private org.vcell.util.gui.sorttable.JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new org.vcell.util.gui.sorttable.JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

/**
 * Return the this12 property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
 
private org.vcell.util.gui.sorttable.JSortTable getthis12() {
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
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getJMenuItemAdd().addActionListener(ivjEventHandler);
	getJMenuItemDelete().addActionListener(ivjEventHandler);
	getJMenuItemPromoteToGlobal().addActionListener(ivjEventHandler);
	getJMenuItemConvertToLocal().addActionListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	getJMenuItemPaste().addActionListener(ivjEventHandler);
	getJMenuItemPasteAll().addActionListener(ivjEventHandler);
	
	// for scrollPaneTable, set tableModel and create default columns
	getScrollPaneTable().setModel(getmodelParameterTableModel());
	getScrollPaneTable().createDefaultColumnsFromModel();

	connPtoP3SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ModelParameterPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(655, 226);
		add(getJScrollPane1(), "Center");
		initConnections();
		if ((getmodel1() != null)) {
			getmodelParameterTableModel().setModel(getmodel1());
		}
		this.modelParameterPanel_Initialize();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void jMenuItemAdd_ActionPerformed(ActionEvent actionEvent) throws Exception {
	if(actionEvent.getSource() == getJMenuItemAdd()){
		AddModelParamDialog createGlobalParamDialog = new AddModelParamDialog();
		createGlobalParamDialog.initAddModelParam(fieldModel);
		createGlobalParamDialog.setLocation(getJScrollPane1().getLocation());
		Container container = (Container)BeanUtils.findTypeParentOfComponent(getJScrollPane1(), JDesktopPane.class);
		container.remove(createGlobalParamDialog);
		container.add(createGlobalParamDialog, JDesktopPane.MODAL_LAYER);
		org.vcell.util.BeanUtils.centerOnComponent(createGlobalParamDialog, this);
		createGlobalParamDialog.setVisible(true);
	}
}

private void jMenuItemDelete_ActionPerformed(ActionEvent actionEvent) {
	if(actionEvent.getSource() == getJMenuItemDelete()){
		int[] rows = getScrollPaneTable().getSelectedRows();
		if (rows.length < 1) {
			PopupGenerator.showErrorDialog("No Global parameter selected for deletion.");
			return;
		} else if (rows.length > 1) {
			PopupGenerator.showErrorDialog("Cannot delete more than one global parameter at a time!");
			return;
		}
		// delete the parameter and update the tablemodel.
		Parameter param = (Parameter)getmodelParameterTableModel().getData().get(rows[0]);
		if (param instanceof ModelParameter) {
			try {
				getModel().removeModelParameter((ModelParameter)param);
			} catch (PropertyVetoException e) {
				//e.printStackTrace();
				PopupGenerator.showErrorDialog(e.getMessage());
			}
		}
	}
}

private void jMenuItemPromoteToGlobal_ActionPerformed(ActionEvent actionEvent) throws Exception {
	if(actionEvent.getSource() == getJMenuItemPromoteToGlobal()){
		int[] rows = getScrollPaneTable().getSelectedRows();
		if (rows.length < 1) {
			PopupGenerator.showErrorDialog("No parameter selected for conversion to global.");
			return;
		}
		// At this point, all selected rows should be local, user-defined kinetic parameters, so proceed with promoting them.
		Parameter[] selectedParams = new Parameter[rows.length];
		for (int i = 0; i < rows.length; i++) {
			selectedParams[i] = (Parameter)getmodelParameterTableModel().getData().get(rows[i]);
		}
		
		// choose the first selected parameter
		Parameter param = selectedParams[0];
		// first check if param name is used as model quantity : structure size/speciesContext/other name. 
		SymbolTableEntry ste = getModel().getEntry(param.getName());
		if (ste != null) {
			String msg = "Parameter name conflicts with ";
			String mqStr = "";
			if (ste instanceof ReservedSymbol) {
				mqStr = "ReservedSymbol";
			} else if (ste instanceof SpeciesContext) {
				mqStr = "SpeciesContext";
			} else if (ste instanceof ModelQuantity) {
				mqStr = ((ste instanceof StructureSize) ? "StructureSize" : "MembraneVoltage");
			} else {
				mqStr = ste.getClass().getName()+"' in context '"+ste.getNameScope().getName();
			}
			
			msg = msg + mqStr + " '" + param.getName() + "' : name already used in model. " + 
				"Was this the intent of using \'" + param.getName() + "\' in kinetics?" +
				"\n\nPress 'Rename Parameter and Continue Merge' to rename all occurances of the selected parameter in the model and continue merging to single global parameter." + 
				"\n\nPress 'Replace with " + mqStr + "' to change '"+ param.getName() + "' to refer to the " + mqStr + " '" + param.getName() + "' instead of the local parameter (" + 
				"local parameter will be lost)." + 
				"\n\nPress 'Quit' to quit this operation and modify parameters manually.";

			String choice = PopupGenerator.showWarningDialog(this, msg, new String[] {"Rename Parameter and Continue Merge", "Replace with " + mqStr, "Quit"}, "Quit");
			if (choice.equals("Rename Parameter and Continue Merge")) {
				String newName = PopupGenerator.showInputDialog(this,"Parameter name:", param.getName());
				for (int i = 0; i < selectedParams.length; i++) {
					while (newName.equals(selectedParams[i].getName())) {
						// if new name that user input is same as old for selected parameter, force user to give new name
						// (kinetics.renameParameter() throws RuntimeException, but it is consumed and user has no feedback.
						newName = PopupGenerator.showInputDialog(this,"Parameter name:", newName);
					}
					Kinetics kinetics = ((ReactionStep) selectedParams[i].getNameScope().getScopedSymbolTable()).getKinetics();
					kinetics.renameParameter(selectedParams[i].getName(), newName);
				}
				// need to change paramName to newName in order to continue with merging
				param.setName(newName);
			} else if (choice.equals("Replace with " + mqStr)) {
				for (int i = 0; i < selectedParams.length; i++) {
					Kinetics kinetics = ((ReactionStep) selectedParams[i].getNameScope().getScopedSymbolTable()).getKinetics();
					kinetics.removeUserDefinedKineticParam(selectedParams[i]);
				}
				// return - since the selected parameters referred to a model quantity and has been resolved; nothing more to do!
				return;
			} else if (choice.equals("Quit")){
				// User wants to manually change things, return from this operation.
				return;
			}
		} 

		// then check if a model param with that name exists. If not, add it to the model.
		ModelParameter mp = getModel().getModelParameter(param.getName());
		if (mp == null) {
			// if model parameter with same name as param doesn't exist, add it to model 
			Expression newExpr = new Expression(param.getExpression());
			try {
				newExpr.bindExpression(getModel());
			} catch (ExpressionBindingException ebe) {
				// ebe.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(ebe.getMessage() + ". '" + ebe.getIdentifier() + "' is probably a local parameter. Unable to convert '" + param.getName() + "' to global parameter.");
				return;
			}
			getModel().addModelParameter(getModel().new ModelParameter(param.getName(), newExpr, Model.ROLE_UserDefined, param.getUnitDefinition()));
			mp = getModel().getModelParameter(param.getName());
		}

		Vector<String> ignoredParamsVector = new Vector<String>(); 
		for (int i = 0; i < selectedParams.length; i++) {
			Kinetics kinetics = ((ReactionStep) selectedParams[i].getNameScope().getScopedSymbolTable()).getKinetics();
			if (selectedParams[i].getName().equals(mp.getName()) && selectedParams[i].getExpression().compareEqual(mp.getExpression())) {
				// if the selected kinetics param has same name and expr as model param, remove it from its kinetics
				kinetics.removeUserDefinedKineticParam(selectedParams[i]);
			} else {
				String msgStr = null;
				if (selectedParams[i].getName().equals(mp.getName()) && !selectedParams[i].getExpression().compareEqual(mp.getExpression())) {
					// if the selected kinetics param has same name as model param, but not same expr, warn user
					msgStr = "Model already has a global parameter named : '" + mp.getName() + "'; with value = '" 
						+ mp.getExpression().infix() + "'; The selected local parameter '" + selectedParams[i].getName() + "' with value = '" + 
						selectedParams[i].getExpression().infix() + "' in reaction '" + kinetics.getReactionStep().getName() + "' will be " + 
						"overridden by the global value." +
						"\n\nPress 'Convert to Global' to override local value with global value of '" + selectedParams[i].getName() + "'." +
						"\n\nPress 'Skip' to cancel local parameter's merge with the global.";
				} else if (!selectedParams[i].getName().equals(mp.getName())) {
					// if names of selected param and model param are different (expression doesn't matter now), ask user if this local should
					// be replaced by model param or not.
					msgStr = "Selected kinetic parameter '" + selectedParams[i].getName() + "' in reaction '" + kinetics.getReactionStep().getName() + 
						"' does not match the model parameter that was promoted ('" + mp.getName() + "'). "+
						"Do you want to replace the kinetic parameter with the model parameter?" +
						"\n\nPress 'Convert to Global' to replace '" + selectedParams[i].getName() + "' with '" + mp.getName() + "'." +  
						"\n\nPress 'Skip' to ignore converting this parameter to global.";
				}
				// warn user with appropriate message
				String choice = PopupGenerator.showWarningDialog(this, msgStr, new String[] {"Convert to Global", "Skip"}, "Convert to Global");
				if (choice.equals("Convert to Global")) {
					// if selected local and promoted global param names are not same, at this point, user has chosen to override local
					// with global, so rename selected local with global name
					if (!selectedParams[i].getName().equals(mp.getName())) {
						kinetics.renameParameter(selectedParams[i].getName(), mp.getName());
						kinetics.removeUserDefinedKineticParam(kinetics.getKineticsParameter(mp.getName()));
					} else {
						// remove local param from kinetics
						kinetics.removeUserDefinedKineticParam(selectedParams[i]);
					}
				} else if (choice.equals("Skip")) {
					// Store rxnName and param name to display consolidated list of skipped params at the end.
					String rxnType = (kinetics.getReactionStep() instanceof SimpleReaction) ? "Reaction " : "Flux Reaction ";
					ignoredParamsVector.add(rxnType + kinetics.getReactionStep().getName() + " : " + selectedParams[i].getName());
					// Cancel merge/conversion to global. Proceed with next selected parameter
					continue;
				}
			} 
		} 
		if (ignoredParamsVector.size() > 0) {
			String[] ignoreParamsArr = (String[])BeanUtils.getArray(ignoredParamsVector, String.class);
			PopupGenerator.showListDialog(this, ignoreParamsArr, "List of local kinetic parameters NOT converted to global");
		}
	}
}

private void jMenuItemConvertToLocal_ActionPerformed(ActionEvent actionEvent) {
	if(actionEvent.getSource() == getJMenuItemConvertToLocal()){
		int[] rows = getScrollPaneTable().getSelectedRows();
		if (rows.length < 1) {
			PopupGenerator.showErrorDialog("No parameter selected for conversion to local.");
			return;
		}
		// At this point, all selected rows should be model parameters, so proceed with converting them to locals
		Parameter[] selectedParams = new Parameter[rows.length];
		for (int i = 0; i < rows.length; i++) {
			selectedParams[i] = (Parameter)getmodelParameterTableModel().getData().get(rows[i]);
		}
		
		// For each selected model parameter, check which reaction kinetics it occurs in, add it as a local;
		// once done with all reactions, delete model param.
		ReactionStep[] rsArr = getModel().getReactionSteps();
		for (int i = 0; i < selectedParams.length; i++) {
			if (selectedParams[i] != null) {
				boolean bSuccessfullyConverted = false;
				Vector<String> rsNamesVector = new Vector<String>();
				for (int j = 0; j < rsArr.length; j++){
					KineticsParameter[] kParams = rsArr[j].getKinetics().getKineticsParameters();
					boolean bUsed = false;
					for (int k = 0; k < kParams.length; k++) {
						if (kParams[k].getExpression().hasSymbol(selectedParams[i].getName())) {
							if ((rsArr[j].getKinetics().getKineticsParameter(selectedParams[i].getName()) != null)) {
								rsNamesVector.add(rsArr[j].getName());
							} else if ((rsArr[j].getKinetics().getProxyParameter(selectedParams[i].getName()) != null)) {
								bUsed = true;
							}
						}
					}
					// if 'bUsed' is <true> at this point, model param is used in reactionStep; 
					// check if is it not already a local param - then local value overrides global, hence cannot convert
					if (bUsed){
						try {
							KineticsProxyParameter kpp = rsArr[j].getKinetics().getProxyParameter(selectedParams[i].getName());
							if (kpp != null) {
								rsArr[j].getKinetics().convertParameterType(kpp, false);
								bSuccessfullyConverted = true;
							} else {
								PopupGenerator.showErrorDialog("Unable to convert parameter : \'" + selectedParams[i].getName() + "\' to local kinetics parameter." );
							}
						} catch (Exception e) {
							e.printStackTrace(System.out);
							PopupGenerator.showErrorDialog("Unable to convert parameter : \'" + selectedParams[i].getName() + "\' to local kinetics parameter : " + e.getMessage());
						}
					} 
				}
				// If 'bSuccessfullyConverted' is <true>, selected parameter can be deleted from model parameters list
				if (bSuccessfullyConverted) {
					try {
						getModel().removeModelParameter((ModelParameter)selectedParams[i]);
					} catch (PropertyVetoException e) {
						//e.printStackTrace();
						PopupGenerator.showErrorDialog(e.getMessage());
					}
				} 
				// if there were some reactions that had locals of same name as globals, those were not converted, so list them
				if (rsNamesVector.size() > 0) {
					String msg = "The following reaction(s): ";
					for (int jj = 0; jj < rsNamesVector.size(); jj++) {
						msg = msg + "\'" + rsNamesVector.elementAt(jj) + "\'";
						if (jj < rsNamesVector.size()-1) {
							msg = msg + ", "; 
						}
					}
					msg = msg + " in the model have a local parameter that has the same name as the selected global parameter '" + selectedParams[i].getName() +
						"'. The existing local value overrides the global value of the parameter.";
					PopupGenerator.showInfoDialog(msg);
					return;
				}
				// if there were no reactions with local param of same name and 'bSuccessfullyConverted' is <false>, show info
				if (rsNamesVector.size() == 0 && !bSuccessfullyConverted) {
					PopupGenerator.showInfoDialog("Did not convert model parameter \'" + selectedParams[i].getName() + "\' to local kinetic parameter : no reaction kinetics references the parameter.");
				}
			}
		}
	}
}

/**
 * Comment
 */
private void jMenuItemCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) throws Exception {
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
			VCellTransferable.ResolvedValuesSelection rvs =
				new VCellTransferable.ResolvedValuesSelection(
					(cbit.vcell.parser.SymbolTableEntry[])org.vcell.util.BeanUtils.getArray(primarySymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					null,
					(cbit.vcell.parser.Expression[])org.vcell.util.BeanUtils.getArray(resolvedValuesV,cbit.vcell.parser.Expression.class),
					sb.toString());

			VCellTransferable.sendToClipboard(rvs);
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
					if(pasteThis instanceof VCellTransferable.ResolvedValuesSelection){
						VCellTransferable.ResolvedValuesSelection rvs =
							(VCellTransferable.ResolvedValuesSelection)pasteThis;
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
	getScrollPaneTable().setDefaultEditor(cbit.vcell.parser.ScopedExpression.class,new TableCellEditorAutoCompletion(getScrollPaneTable()));
	
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
				try {
					ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
				} catch (Exception e1) {
					e1.printStackTrace(System.out);
				}
			}
		}
	);
}


/**
 * Comment
 */
private void popupCopyPaste(java.awt.event.MouseEvent mouseEvent) {
	if(mouseEvent.isPopupTrigger()){
		Object obj = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
		boolean bPastable = obj instanceof VCellTransferable.ResolvedValuesSelection;
		boolean bSomethingSelected = getScrollPaneTable().getSelectedRowCount() > 0;
		getJMenuItemPaste().setEnabled(bPastable && bSomethingSelected);
		getJMenuItemPasteAll().setEnabled(bPastable);
		getJMenuItemCopy().setEnabled(bSomethingSelected);
		//getJMenuItemCopyAll().setEnabled(bSomethingSelected);

		// to enable 'Delete' & 'ConvertToLocal' buttons - only if all selections are Global parameters
		boolean bIsModelParam = true;
		int[] rows = getScrollPaneTable().getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			if (getmodelParameterTableModel().getData().get(rows[i]) instanceof ModelParameter) {
				bIsModelParam = bIsModelParam && true; 
			} else {
				bIsModelParam = bIsModelParam && false;
			}
		}
		getJMenuItemDelete().setEnabled(bIsModelParam);
		getJMenuItemConvertToLocal().setEnabled(bIsModelParam);

		// to enable 'PromoteToGlobal' button - only if all selections are local (kinetic, only user-defined) parameters
		boolean bIsUserDefinedKinParam = true;
		for (int i = 0; i < rows.length; i++) {
			if (getmodelParameterTableModel().getData().get(rows[i]) instanceof KineticsParameter) {
				KineticsParameter kp = (KineticsParameter)getmodelParameterTableModel().getData().get(rows[i]);
				if (kp.getRole() == Kinetics.ROLE_UserDefined) {
					bIsUserDefinedKinParam = bIsUserDefinedKinParam && true; 
				} else {
					bIsUserDefinedKinParam = bIsUserDefinedKinParam && false;
				}
			} else {
				bIsUserDefinedKinParam = bIsUserDefinedKinParam && false;
			}
		}
		getJMenuItemPromoteToGlobal().setEnabled(bIsUserDefinedKinParam);

		getJPopupMenuICP().show(getJScrollPane1(),mouseEvent.getX(),mouseEvent.getY());
	}
}


/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	if(oldValue != null){
		oldValue.removePropertyChangeListener(ModelParametersPropertyChangeListener);
	}
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}


/**
 * Set the model1 to a new value.
 * @param newValue cbit.vcell.model.Model
 */
private void setmodel1(cbit.vcell.model.Model newValue) {
	if (ivjmodel1 != newValue) {
		try {
			cbit.vcell.model.Model oldValue = getmodel1();
			ivjmodel1 = newValue;
			connPtoP3SetSource();
			getmodelParameterTableModel().setModel(getmodel1());
			if (getmodel1() != null) {
				getmodel1().addPropertyChangeListener(ModelParametersPropertyChangeListener);
			}
			firePropertyChange("model", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}


/**
 * Set the this12 to a new value.
 * @param newValue cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private void setthis12(org.vcell.util.gui.sorttable.JSortTable newValue) {
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
		
		// if it is a reaction parameter (authoritative parameter, i.e., reaction rate), it is editable
		ReactionStep rs = getmodelParameterTableModel().getEditableAnnotationReactionStep(getScrollPaneTable().getSelectedRow());
		if(rs != null){
			String newAnnotation = org.vcell.util.gui.DialogUtils.showAnnotationDialog(this, rs.getAnnotation());
			if(newAnnotation != null && newAnnotation.length() == 0){
				newAnnotation = null;
			}
			rs.setAnnotation(newAnnotation);
			getmodelParameterTableModel().fireTableRowsUpdated(getScrollPaneTable().getSelectedRow(), getScrollPaneTable().getSelectedRow());
		}
		// if it is a model (global) parameter - annotation is editable
		Parameter param = (Parameter)getmodelParameterTableModel().getData().get(getScrollPaneTable().getSelectedRow());
		if (param != null && param instanceof ModelParameter) {
			ModelParameter modelParameter = (ModelParameter)param;
			String newAnnotation = DialogUtils.showAnnotationDialog(this, modelParameter.getModelParameterAnnotation());
			if(newAnnotation != null && newAnnotation.length() == 0){
				newAnnotation = null;
			}
			modelParameter.setModelParameterAnnotation(newAnnotation);
			getmodelParameterTableModel().fireTableRowsUpdated(getScrollPaneTable().getSelectedRow(), getScrollPaneTable().getSelectedRow());
		}
	}catch(org.vcell.util.gui.UtilCancelException e){
		//Do Nothing
	}catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to edit annotation!\n"+exc.getMessage());
	}
}
}