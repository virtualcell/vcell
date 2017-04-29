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

import javax.swing.JTable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.IssueManager;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;

/**
 * {@link ApplicationSpecificationsPanel} subpanel 
 */
@SuppressWarnings("serial")
public class MembraneConditionsPanel extends DocumentEditorSubPanel implements  ApplicationSpecificationsPanel.Specifier {
	private SimulationContext fieldSimulationContext = null;
	private JSortTable table = null;
	private MembraneConditionTableModel tableModel = null;
	private static Logger lg = Logger.getLogger(MembraneConditionsPanel.class);
	
public MembraneConditionsPanel() {
	super();
	initialize();
}

@Override
public ActiveViewID getActiveView() {
	return ActiveViewID.membrane_setting; 
}

/**
 * not implemented yet
 */
@Override
public void setSearchText(String s) {
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
private JSortTable getScrollPaneTable() {
	if (table == null) {
		try {
			table = new JSortTable();
			table.setName("membraneSpecsTable");
			tableModel = new MembraneConditionTableModel(table) ;
			table.setModel(tableModel);
			//table.setScrollTableActionManager(new InternalScrollTableActionManager(table));
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
	if (lg.isEnabledFor(Level.WARN)) {
		lg.warn("uncaught exception " + getClass( ).getName(),exception);
	}
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("MembraneSpecsPanel");
		setLayout(new BorderLayout());
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		/*

		DefaultTableCellRenderer renderer = new DefaultScrollTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				/*
				setIcon(null);
				defaultToolTipText = null;

				if (value instanceof Species) {
					setText(((Species)value).getCommonName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof SpeciesContext) {
					setText(((SpeciesContext)value).getName());
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
					} else if (table.getColumnName(column).equals(SpeciesContextSpecsTableModel.ColumnType.COLUMN_DIFFUSION.label)) {
						SpeciesContextSpecParameter diffusionParameter = scSpec.getDiffusionParameter();
						unit = diffusionParameter.getUnitDefinition();
					}
					if (unit != null) {
						setHorizontalTextPosition(JLabel.LEFT);
						setIcon(new TextIcon("[" + unit.getSymbolUnicode() + "]", DefaultScrollTableCellRenderer.uneditableForeground));
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
		getScrollPaneTable().setDefaultRenderer(Expression.class, renderer);
		getScrollPaneTable().setDefaultRenderer(Structure.class, renderer);
		getScrollPaneTable().setDefaultRenderer(Species.class, renderer);
		getScrollPaneTable().setDefaultRenderer(ScopedExpression.class, renderer);
		getScrollPaneTable().setDefaultRenderer(Boolean.class, new ScrollTableBooleanCellRenderer());
	*/
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

@Override
public void setIssueManager(IssueManager issueManager) {
	super.setIssueManager(issueManager);
	tableModel.setIssueManager(issueManager);
}


/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 */
public void setSimulationContext(SimulationContext newValue) {
	fieldSimulationContext = newValue;
	tableModel.setSimulationContext(fieldSimulationContext);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), tableModel);
}

}
