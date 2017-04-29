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
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.IssueManager;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.sorttable.JSortTable;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorReactionTableModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.ObservableTableModel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.graph.ShapeModeInterface;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.mapping.ModelProcessSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.ModelProcess;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;

/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:31:14 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ModelProcessSpecsPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {
	private JSortTable ivjScrollPaneTable = null;
	private ModelProcessSpecsTableModel ivjModelProcessSpecsTableModel = null;
	private SimulationContext fieldSimulationContext = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SmallShapeManager shapeManager = new SmallShapeManager(false, false, false, false);

	private class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ModelProcessSpecsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
				getModelProcessSpecsTableModel().setSimulationContext(getSimulationContext());
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) {
				setSelectedObjectsFromTable(getScrollPaneTable(), getModelProcessSpecsTableModel());
			}
		}
	};
	
public ModelProcessSpecsPanel() {
	super();
	initialize();
}


@Override
public ActiveViewID getActiveView() {
	return ActiveViewID.reaction_setting; 
}


public void setSearchText(String searchText){
	ivjModelProcessSpecsTableModel.setSearchText(searchText);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ModelProcessSpecsPanel");
		setLayout(new BorderLayout());
		//setSize(456, 539);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/*
 * Return the ModelProcessSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.ModelProcessSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ModelProcessSpecsTableModel getModelProcessSpecsTableModel() {
	if (ivjModelProcessSpecsTableModel == null) {
		try {
			ivjModelProcessSpecsTableModel = new ModelProcessSpecsTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjModelProcessSpecsTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
	getScrollPaneTable().setModel(getModelProcessSpecsTableModel());
	getScrollPaneTable().setDefaultRenderer(ModelProcess.class, new DefaultScrollTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			defaultToolTipText = null;
			if (value instanceof ModelProcess) {
				setText(((ModelProcess)value).getName());
				defaultToolTipText = getText();
				setToolTipText(defaultToolTipText);
			}
			
			TableModel tableModel = table.getModel();
			if (tableModel instanceof SortTableModel) {
				DefaultScrollTableCellRenderer.issueRenderer(this, defaultToolTipText, table, row, column, (SortTableModel)tableModel);
			}
			return this;
		}
	});
	
	DefaultScrollTableCellRenderer rbmReactionShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
		List<SpeciesPatternSmallShape> spssList = new ArrayList<SpeciesPatternSmallShape>();
		SpeciesPatternSmallShape spss = null;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (table.getModel() instanceof VCellSortTableModel<?>) {
				Object selectedObject = null;
				if (table.getModel() == ivjModelProcessSpecsTableModel) {
					selectedObject = ivjModelProcessSpecsTableModel.getValueAt(row);
				}
				if (selectedObject != null) {
					if(selectedObject instanceof ModelProcessSpec) {
						ModelProcessSpec mps = (ModelProcessSpec)selectedObject;
						ModelProcess mp = (ModelProcess)mps.getModelProcess();
						if(mp instanceof ReactionRule) {
							ReactionRule rr = (ReactionRule)mp;
							Graphics panelContext = table.getGraphics();
	
							spssList.clear();
							List<ReactantPattern> rpList = rr.getReactantPatterns();
							int xPos = 4;
							for(int i = 0; i<rpList.size(); i++) {
								SpeciesPattern sp = rr.getReactantPattern(i).getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, shapeManager, panelContext, rr, isSelected);
								if(i < rpList.size()-1) {
									spss.addEndText("+");
								} else {
									if(rr.isReversible()) {
										spss.addEndText("<->");
										xPos += 7;
									} else {
										spss.addEndText("->");
									}
								}
								xPos += spss.getWidth() + 15;
								spssList.add(spss);
							}
							List<ProductPattern> ppList = rr.getProductPatterns();
							xPos+= 7;
							for(int i = 0; i<ppList.size(); i++) {
								SpeciesPattern sp = rr.getProductPattern(i).getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, shapeManager, panelContext, rr, isSelected);
								if(i < ppList.size()-1) {
									spss.addEndText("+");
								}
								xPos += spss.getWidth() + 15;
								spssList.add(spss);
							}
						} else {
							ReactionStep rs = (ReactionStep)mp;
							Graphics panelContext = table.getGraphics();
							spssList.clear();
							int xPos = 4;
							int extraSpace = 0;
							for(int i = 0; i<rs.getNumReactants(); i++) {
								SpeciesPattern sp = rs.getReactant(i).getSpeciesContext().getSpeciesPattern();
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, shapeManager, panelContext, rs, isSelected);
								if(i < rs.getNumReactants()-1) {
									spss.addEndText("+");
								} else {
									if(rs.isReversible()) {
										spss.addEndText("<->");
										extraSpace += 7;
									} else {
										spss.addEndText("->");
									}
								}
								int offset = sp == null ? 17 : 15;
								offset += extraSpace;
								int w = spss.getWidth();
								xPos += w + offset;
								spssList.add(spss);
							}
							xPos+= 8;
							for(int i = 0; i<rs.getNumProducts(); i++) {
								SpeciesPattern sp = rs.getProduct(i).getSpeciesContext().getSpeciesPattern();
								if(i==0 && rs.getNumReactants() == 0) {
									xPos += 14;
								}
								spss = new SpeciesPatternSmallShape(xPos, 2, sp, shapeManager, panelContext, rs, isSelected);
								if(i==0 && rs.getNumReactants() == 0) {
									spss.addStartText("->");
								}
								if(i < rs.getNumProducts()-1) {
									spss.addEndText("+");
								}
								int offset = sp == null ? 17 : 15;
								int w = spss.getWidth();
								xPos += w + offset;
								spssList.add(spss);
							}
						}
					}
				} else {
					spssList.clear();
				}
			}
			setText("");
			return this;
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			for(SpeciesPatternSmallShape spss : spssList) {
				if(spss == null) {
					continue;
				}
				spss.paintSelf(g);
			}
		}
	};
	
	
	getScrollPaneTable().setDefaultRenderer(SpeciesPattern.class, rbmReactionShapeDepictionCellRenderer);
	
//	ivjScrollPaneTable.getColumnModel().getColumn(ModelProcessSpecsTableModel.ColumnType.COLUMN_DEPICTION.ordinal()).setCellRenderer(rbmReactionShapeDepictionCellRenderer);
//	ivjScrollPaneTable.getColumnModel().getColumn(ModelProcessSpecsTableModel.ColumnType.COLUMN_DEPICTION.ordinal()).setPreferredWidth(180);
	
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
}

@Override
public void setIssueManager(IssueManager issueManager) {
	super.setIssueManager(issueManager);
	ivjModelProcessSpecsTableModel.setIssueManager(issueManager);
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ModelProcessSpecsPanel aModelProcessSpecsPanel;
		aModelProcessSpecsPanel = new ModelProcessSpecsPanel();
		frame.setContentPane(aModelProcessSpecsPanel);
		frame.setSize(aModelProcessSpecsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), getModelProcessSpecsTableModel());
}

}
