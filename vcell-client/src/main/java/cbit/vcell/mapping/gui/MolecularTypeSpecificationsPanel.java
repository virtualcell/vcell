/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DefaultScrollTableActionManager;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable.ScrollTableBooleanCellRenderer;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.JSortTable;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.graph.SmallShapeManager;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.MolecularTypeSpec;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.gui.StructureMappingTableRenderer.TextIcon;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class MolecularTypeSpecificationsPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {
	private SimulationContext fieldSimulationContext = null;
	private JSortTable table = null;
	private SpeciesContextSpecsTableModel tableModel = null;
	private SmallShapeManager shapeManager = new SmallShapeManager(false, false, false, false);
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
// TODO: see InitialConditionsPanel for menu implementation, events, aso
//	private javax.swing.JMenuItem ivjJMenuItemPaste = null;

	private class InternalScrollTableActionManager extends DefaultScrollTableActionManager {

		InternalScrollTableActionManager(JTable table) {
			super(table);
			ApplicationSpecificationsPanel asp;
		}

		@Override
		protected void constructPopupMenu() {
			if (popupMenu == null) {
				super.constructPopupMenu();
				int pos = 0;
//				popupMenu.insert(getJMenuItemCopy(), pos ++);
				DocumentEditorSubPanel.addFieldDataMenuItem(getOwnerTable(), popupMenu, pos++);
				popupMenu.insert(new JSeparator(), pos++);
			}
			Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);	
			boolean bPastable = obj instanceof VCellTransferable.ResolvedValuesSelection;
			boolean bSomethingSelected = getScrollPaneTable().getSelectedRows() != null && getScrollPaneTable().getSelectedRows().length > 0;
//			getJMenuItemPaste().setEnabled(bPastable && bSomethingSelected);
		}
	}
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
//			if (e.getSource() == MolecularTypeSpecificationsPanel.this.getJMenuItemPaste()) 
//				jMenuItemPaste_ActionPerformed(e);
			}
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_USE_CONCENTRATION)) {
				updateTopScrollPanel();
			}
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) 
				setSelectedObjectsFromTable(getScrollPaneTable(), tableModel);
		};
	};

public MolecularTypeSpecificationsPanel() {
	super();
	initialize();
}

@Override
public ActiveViewID getActiveView() {
	return ActiveViewID.species_settings; 
}

public void setSearchText(String searchText){
	tableModel.setSearchText(searchText);
}



private void updateTopScrollPanel() {

}

private JSortTable getScrollPaneTable() {
	if (table == null) {
		try {
			table = new JSortTable();
			table.setName("spceciesContextSpecsTable");
			tableModel = new SpeciesContextSpecsTableModel(table);
			table.setModel(tableModel);
			table.setScrollTableActionManager(new InternalScrollTableActionManager(table));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return table;
}

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.mapping.InitialConditionPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("InitialConditionsPanel");
		setLayout(new BorderLayout());

		getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
			
		DefaultTableCellRenderer renderer = new DefaultScrollTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setIcon(null);
				defaultToolTipText = null;

				if (value instanceof MolecularType) {
					setText(((MolecularType)value).getName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof Structure) {
					setText(((Structure)value).getName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof ScopedExpression) {
					SpeciesContextSpec scSpec = tableModel.getValueAt(row);
					VCUnitDefinition unit = null;
					if (table.getColumnName(column).equals(SpeciesContextSpecsTableModel.ColumnType.COLUMN_INITIAL.label)) {
						SpeciesContextSpecParameter initialConditionParameter = scSpec.getInitialConditionParameter();
						unit = initialConditionParameter.getUnitDefinition();
					}
					int rgb = 0x00ffffff & DefaultScrollTableCellRenderer.uneditableForeground.getRGB();
					defaultToolTipText = "<html>" + StringEscapeUtils.escapeHtml4(getText()) + " <font color=#" + Integer.toHexString(rgb) + "> [" + unit.getSymbolUnicode() + "] </font></html>";
					setToolTipText(defaultToolTipText);
				}
				
				TableModel tableModel = table.getModel();
				if (tableModel instanceof SortTableModel) {
					DefaultScrollTableCellRenderer.issueRenderer(this, defaultToolTipText, table, row, column, (SortTableModel)tableModel);
					setHorizontalTextPosition(JLabel.TRAILING);
				}
				return this;
			}
		};
		DefaultTableCellRenderer rbmMolecularTypeShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			MolecularTypeSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == tableModel) {
						selectedObject = tableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof MolecularTypeSpec) {
							MolecularTypeSpec scs = (MolecularTypeSpec)selectedObject;
							MolecularType sc = scs.getMolecularType();
							// TODO: do it like in MolecularTypePanel
//							MolecularTypePattern sp = sc.getMolecularTypePattern();		// sp may be null for "plain" species contexts
							Graphics panelContext = table.getGraphics();
//							spss = new MolecularTypeSmallShape(4, 2, sc, shapeManager, panelContext, sc, isSelected, issueManager);
						}
					} else {
						spss = null;
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spss != null) {
					spss.paintSelf(g);
				}
			}
		};
		
		DefaultScrollTableCellRenderer rulesTableCellRenderer = new DefaultScrollTableCellRenderer() {
			final Color lightBlueBackground = new Color(214, 234, 248);
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if (table.getModel() instanceof SpeciesContextSpecsTableModel) {
					Icon icon = VCellIcons.issueGoodIcon;
					Object selectedObject = null;
					if (table.getModel() == tableModel) {
						selectedObject = tableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(isSelected) {
							setBackground(lightBlueBackground);
						}
						if(selectedObject instanceof SpeciesContextSpec) {
							SpeciesContextSpec scs = (SpeciesContextSpec)selectedObject;
							SpeciesContext sc = scs.getSpeciesContext();

							boolean foundRuleMatch = false;
							if(fieldSimulationContext.getRateRules() != null && fieldSimulationContext.getRateRules().length > 0) {
								for(RateRule rr : fieldSimulationContext.getRateRules()) {
									if(rr.getRateRuleVar() == null) {
										continue;
									}
									if(sc.getName().equals(rr.getRateRuleVar().getName())) {
										foundRuleMatch = true;
										icon = VCellIcons.ruleRateIcon;
										break;
									}
								}
							}
							if(!foundRuleMatch && fieldSimulationContext.getAssignmentRules() != null && fieldSimulationContext.getAssignmentRules().length > 0) {
								for(AssignmentRule rr : fieldSimulationContext.getAssignmentRules()) {
									if(rr.getAssignmentRuleVar() == null) {
										continue;
									}
									if(sc.getName().equals(rr.getAssignmentRuleVar().getName())) {
										icon = VCellIcons.ruleAssignIcon;
										break;
									}
								}
							}
						}
					}
					setIcon(icon);
				}
				return this;
			}
		};

		getScrollPaneTable().setDefaultRenderer(Structure.class, renderer);
		getScrollPaneTable().setDefaultRenderer(MolecularType.class, rbmMolecularTypeShapeDepictionCellRenderer);	// depiction icons
		getScrollPaneTable().setDefaultRenderer(ScopedExpression.class, renderer);
		getScrollPaneTable().setDefaultRenderer(Boolean.class, new ScrollTableBooleanCellRenderer());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

@Override
public void setIssueManager(IssueManager issueManager) {
	super.setIssueManager(issueManager);
	tableModel.setIssueManager(issueManager);
}

private void jMenuItemCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
}




/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext newValue) {
	SimulationContext oldValue = fieldSimulationContext;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
	}
	fieldSimulationContext = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(ivjEventHandler);
	}
	tableModel.setSimulationContext(fieldSimulationContext);
	updateTopScrollPanel();
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), tableModel);
}
}
