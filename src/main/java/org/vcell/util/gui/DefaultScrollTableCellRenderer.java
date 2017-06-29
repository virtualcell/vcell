/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.vcell.util.Issue;
import org.vcell.util.NumberUtils;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.gui.ModelProcessEquation;
import cbit.vcell.client.desktop.biomodel.BioModelEditorApplicationsTableModel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.SpatialObjectTableModel;
import cbit.vcell.client.desktop.biomodel.SpatialProcessTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SurfaceRegionObject;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.mapping.spatial.processes.PointKinematics;
import cbit.vcell.mapping.spatial.processes.PointLocation;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.mapping.spatial.processes.SurfaceKinematics;

@SuppressWarnings("serial")
public class DefaultScrollTableCellRenderer extends DefaultTableCellRenderer {
	private static final int LEFT_ICON_MARGIN = 1;
	public static final Color hoverColor = new Color(0xFDFCDC);
	public static final Border DEFAULT_GAP = BorderFactory.createEmptyBorder(1,LEFT_ICON_MARGIN,1,1);
	static final Border focusHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
	public static final Color uneditableForeground = new Color(0x964B00/*0x967117*/)/*UIManager.getColor("TextField.inactiveForeground")*/;
	static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	public static final Color everyOtherRowColor = new Color(0xe8edff);
	private boolean bEnableUneditableForeground = true;
	public String defaultToolTipText = null;
	/**
	 * DefaultTableCellRendererEnhanced constructor comment.
	 */
	public DefaultScrollTableCellRenderer() {
		super();
		setOpaque(true);
	}
	
	public void disableUneditableForeground() {
		bEnableUneditableForeground = false;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 1:07:02 PM)
	 * @return java.awt.Component
	 * @param table javax.swing.JTable
	 * @param value java.lang.Object
	 * @param isSelected boolean
	 * @param hasFocus boolean
	 * @param row int
	 * @param column int
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		defaultToolTipText = null;
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		defaultToolTipText = getToolTipText();
		setBorder(DEFAULT_GAP);
		
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			if (table instanceof ScrollTable && ((ScrollTable)table).getHoverRow() == row) {
				setBackground(hoverColor);
			} else {
				setBackground(row % 2 == 0 ? table.getBackground() : everyOtherRowColor);				
			}
			
			if(table.getModel() instanceof SpatialProcessTableModel /* && column == SpatialProcessTableModel.COLUMN_SpatialProcess_SPATIALOBJECTS */) {
				boolean found = isMatchWithSelectedObject(table, row);
				if(found == true) {
					setBackground(Color.yellow);
				}
			} else if(table.getModel() instanceof SpatialObjectTableModel /* && column == SpatialObjectTableModel.COLUMN_SpatialObject_NAME */) {
				boolean found = isMatchWithSelectedProcess(table, row);
				if(found == true) {
					setBackground(Color.yellow);
				}
			}
			setForeground(table.getForeground());
		}
		
		TableModel tableModel = table.getModel();
		if (bEnableUneditableForeground && (!table.isEnabled() || !tableModel.isCellEditable(row, column))) {
			if (!isSelected) {
				setForeground(uneditableForeground);
			}
		}
		if (value instanceof Double) {
			Double doubleValue = (Double)value;
			setText(nicelyFormattedDouble(doubleValue));
		} else if (value instanceof JComponent) {
			JComponent jc = (JComponent)value;
			if (hasFocus) {
			    jc.setBorder(focusHighlightBorder );
			} else {
			    jc.setBorder(noFocusBorder);
			}
			return jc;
		}
		if (BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT.equals(value)) {
			setText(BioModelEditorRightSideTableModel.ADD_NEW_HERE_HTML);
		} else if (value instanceof ModelProcessEquation && BioModelEditorRightSideTableModel.ADD_NEW_HERE_REACTION_TEXT.equals(((ModelProcessEquation)value).toString())) {
			setText(BioModelEditorRightSideTableModel.ADD_NEW_REACTION_OR_RULE_HTML);
		}
		if(tableModel instanceof BioModelEditorApplicationsTableModel) {	// for the applications table we show the icons with the app type
			Icon icon = null;
			String toolTipSuffix = "";
			
			BioModelEditorApplicationsTableModel bmeatm = (BioModelEditorApplicationsTableModel)tableModel;
			SimulationContext simContext = (SimulationContext)(bmeatm.getValueAt(row));
			if(simContext.isRuleBased()) {
				if(simContext.getGeometry().getDimension() == 0) {
					icon = VCellIcons.appRbmNonspIcon;
		    		toolTipSuffix = "Rule Based / Non spatial";
				}
			} else if(simContext.isStoch()) {
				if(simContext.getGeometry().getDimension() == 0) {
					icon = VCellIcons.appStoNonspIcon;
		    		toolTipSuffix = "Stochastic / Non spatial";
				} else {
					icon = VCellIcons.appStoSpatialIcon;
		    		toolTipSuffix =  "Stochastic / Spatial";
				}
			} else {		// deterministic
				if(simContext.getGeometry().getDimension() == 0) {
					icon = VCellIcons.appDetNonspIcon;
		    		toolTipSuffix =  "Deterministic / Non spatial";
				} else {
					icon = VCellIcons.appDetSpatialIcon;
		    		toolTipSuffix =  "Deterministic / Spatial";
				}
			}
    		String toolTipPrefix = "Application: ";
    		setToolTipText(toolTipPrefix + toolTipSuffix);
    		if(column == 0) {
    			setIcon(icon);
    		} else {
    			setIcon(null);
    		}
		} else if (column == 2 && tableModel instanceof SpatialProcessTableModel) {
			Icon icon = null;
			SpatialProcess spatialProcess = (SpatialProcess)(((SpatialProcessTableModel) tableModel).getValueAt(row));
			if(spatialProcess instanceof PointLocation)  {
				icon = VCellIcons.spatialPointIcon;
			} else if(spatialProcess instanceof PointKinematics) {
				icon = VCellIcons.spatialPointIcon;
			} else if(spatialProcess instanceof SurfaceKinematics) {
				icon = VCellIcons.spatialMembraneIcon;
			} else {
				icon = VCellIcons.spatialVolumeIcon;
			}
			setIcon(icon);
		} else if (tableModel instanceof SortTableModel) {	// for most other tables we reserve the icon spot to display issues
			DefaultScrollTableCellRenderer.issueRenderer(this, defaultToolTipText, table, row, column, (SortTableModel)tableModel);
		}
		return this;
	}

	private boolean isMatchWithSelectedObject(JTable table, int row) {
		SpatialProcessTableModel tm = (SpatialProcessTableModel)table.getModel();
		SelectionManager selectionManager = tm.getSelectionManager();
		boolean found = false;
		if(selectionManager != null) {
			for(Object ob : selectionManager.getSelectedObjects()) {
				if(ob instanceof SpatialObject) {		// a SpatialObject selected
					List<SpatialProcess> spList = ((SpatialObject)ob).getRelatedSpatialProcesses();
					for(SpatialProcess theirs : spList) {
						SpatialProcess ours = tm.getValueAt(row);
						if(ours == theirs) {
							found = true;
							break;
						}
					}
				}
				if(found == true) {
					break;
				}
			}
		}
		return found;
	}
	private boolean isMatchWithSelectedProcess(JTable table, int row) {
		SpatialObjectTableModel tm = (SpatialObjectTableModel)table.getModel();
		SelectionManager selectionManager = tm.getSelectionManager();
		boolean found = false;
		if(selectionManager != null) {
			for(Object ob : selectionManager.getSelectedObjects()) {
				if(ob instanceof SpatialProcess) {		// a SpatialProcess selected
					List<SpatialObject> soList = ((SpatialProcess)ob).getSpatialObjects();
					for(SpatialObject theirs : soList) {
						SpatialObject ours = tm.getValueAt(row);
						if(ours == theirs) {
							found = true;
							break;
						}
					}
				}
				if(found == true) {
					break;
				}
			}
		}
		return found;
	}
	
	/**
	 * format double nicely for Nan, infinite and typical values
	 * @param doubleValue not null
	 * @return String
	 */
	public static String nicelyFormattedDouble(Double doubleValue) {
		if (doubleValue.isNaN() || doubleValue.isInfinite()) {
			return java.text.NumberFormat.getInstance().format(doubleValue.doubleValue());
		} else {
			return NumberUtils.formatNumber(doubleValue.doubleValue() );
		}
		
	}
	
	public static void issueRenderer(JLabel renderer, String defaultToolTipText, JTable table, int row, int column, SortTableModel tableModel) {
		List<Issue> issueListError = tableModel.getIssues(row, column, Issue.Severity.ERROR);
		List<Issue> issueListWarning = tableModel.getIssues(row, column, Issue.Severity.WARNING);
		Icon icon = null;
		Point mousePosition = table.getMousePosition();
		Color red = Color.getHSBColor(0f, 0.4f, 1.0f);		// hue, saturation, brightness
		
		if (issueListError.size() > 0) {
			if (column == 0) {
				icon = VCellIcons.issueErrorIcon;
				if(mousePosition !=null && mousePosition.getX()>LEFT_ICON_MARGIN && mousePosition.getX()<=(icon.getIconWidth()+LEFT_ICON_MARGIN)) {
					String tt = Issue.getHtmlIssueMessage(issueListError);
					renderer.setToolTipText(tt);
				} else {
					renderer.setToolTipText(defaultToolTipText);
				}
				renderer.setBorder(new MatteBorder(1,1,1,0, red));	// Color.red
			} else if (column == table.getColumnCount() - 1) {
				renderer.setBorder(new MatteBorder(1,0,1,1, red));
			} else {
				renderer.setBorder(new MatteBorder(1,0,1,0, red));
			}
		} else if(issueListWarning.size() > 0) {
			if (column == 0) {
				icon = VCellIcons.issueWarningIcon;
				if(mousePosition !=null && mousePosition.getX()>LEFT_ICON_MARGIN && mousePosition.getX()<=(icon.getIconWidth()+LEFT_ICON_MARGIN)) {
					renderer.setToolTipText(Issue.getHtmlIssueMessage(issueListWarning));
				} else {
					renderer.setToolTipText(defaultToolTipText);
				}
				renderer.setBorder(new MatteBorder(1,1,1,0,Color.orange));
			} else if (column == table.getColumnCount() - 1) {
				renderer.setBorder(new MatteBorder(1,0,1,1,Color.orange));
			} else {
				renderer.setBorder(new MatteBorder(1,0,1,0,Color.orange));
			}
		} else {
			if (column == 0) {
				icon = VCellIcons.issueGoodIcon;
				renderer.setToolTipText(null);	// no tooltip for column 0 when we have no issues on that line
			}
			if (column != 0 && defaultToolTipText != null && !defaultToolTipText.isEmpty()) {
				renderer.setToolTipText(defaultToolTipText);
			} else {
				renderer.setToolTipText(null);
			}
			renderer.setBorder(DEFAULT_GAP);
		}
		
		if(column == 0 && icon != null) {
			// for some tables we combine (concatenate) the issue icon with an entity icon 
			if (tableModel instanceof SpatialProcessTableModel) {
				Icon icon2 = null;
				SpatialProcess spatialProcess = (SpatialProcess)(((SpatialProcessTableModel) tableModel).getValueAt(row));
				if (spatialProcess instanceof PointLocation)  {
					icon2 = VCellIcons.spatialLocationIcon;
				} else if(spatialProcess instanceof PointKinematics) {
					icon2 = VCellIcons.spatialKinematicsIcon;
				} else if(spatialProcess instanceof SurfaceKinematics) {
					icon2 = VCellIcons.spatialKinematicsIcon;
				} else {
					icon2 = VCellIcons.spatialKinematicsIcon;
				}
				icon = VCellIcons.addIcon(icon, icon2);
			} else if (tableModel instanceof SpatialObjectTableModel) {
				Icon icon2 = null;
				SpatialObject spatialObject =	(SpatialObject)(((SpatialObjectTableModel) tableModel).getValueAt(row));
				if (spatialObject instanceof PointObject) {
					icon2 = VCellIcons.spatialPointIcon;
				} else if (spatialObject instanceof SurfaceRegionObject) {
					icon2 = VCellIcons.spatialMembraneIcon;
				} else if (spatialObject instanceof VolumeRegionObject) {
					icon2 = VCellIcons.spatialVolumeIcon;
				} else {
					icon2 = VCellIcons.spatialVolumeIcon;
				}
				icon = VCellIcons.addIcon(icon, icon2);
			}
		}
		renderer.setIcon(icon);
	}
}
