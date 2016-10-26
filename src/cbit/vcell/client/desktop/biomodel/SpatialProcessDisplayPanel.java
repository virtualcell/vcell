/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SurfaceRegionObject;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;


@SuppressWarnings("serial")
public class SpatialProcessDisplayPanel extends BioModelEditorApplicationRightSidePanel<SpatialProcess> {	
	public SpatialProcessDisplayPanel() {
		super();
		initialize();
	}
		
	private void initialize() {
		setName("SpatialProcessDisplayPanel");
		setLayout(new GridBagLayout());

		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,50,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(addNewButton, gbc);
		addNewButton.setIcon(new DownArrowIcon());
		addNewButton.setHorizontalTextPosition(SwingConstants.LEFT);
		addNewButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				JPopupMenu popup = new JPopupMenu();
				JMenuItem newPointLocationMenuItem = new JMenuItem(new AbstractAction("new Point Location") {
					public void actionPerformed(ActionEvent e) {  createNewPointLocation(); }});
				JMenuItem newPointKinematicsMenuItem = new JMenuItem(new AbstractAction("new Point Kinematics") {
					public void actionPerformed(ActionEvent e) {  createNewPointKinematics(); }});
				JMenuItem newSurfaceKinematicsMenuItem = new JMenuItem(new AbstractAction("new Surface Kinematics") {
					public void actionPerformed(ActionEvent e) {  createNewSurfaceKinematics(); }});
				newPointLocationMenuItem.setIcon(VCellIcons.spatialPointLocationIcon);
				newPointKinematicsMenuItem.setIcon(VCellIcons.spatialPointKinematicsIcon);
				newSurfaceKinematicsMenuItem.setIcon(VCellIcons.spatialMembraneKinematicsIcon);
				popup.add(newPointLocationMenuItem);
				popup.add(newPointKinematicsMenuItem);
				popup.add(newSurfaceKinematicsMenuItem);
				
				
				popup.show(addNewButton, 0, addNewButton.getHeight());
			}
		});
		// for now disable 'add' button
//		addNewButton.setEnabled(false);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
	}
	
	@Override
	protected void newButtonPressed() {
//		if (simulationContext == null) {
//			return;
//		}
//		try {
//			simulationContext.createPointObject();
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//			DialogUtils.showErrorDialog(this, "Error adding SpatialProcess : " + e.getMessage());
//		}		
	}

	@Override
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		ArrayList<SpatialProcess> deleteList = new ArrayList<SpatialProcess>();
		for (int r : rows) {
			if (r < tableModel.getRowCount()) {
				SpatialProcess SpatialProcess = tableModel.getValueAt(r);
				if (SpatialProcess != null) {
					deleteList.add(SpatialProcess);
				}
			}
		}
		try {
			for (SpatialProcess SpatialProcess : deleteList) {
				simulationContext.removeSpatialProcess(SpatialProcess);
			}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}		
	}
	
	private PointObject getOrCreateNextPointObject(){
		for (SpatialObject spatialObject : simulationContext.getSpatialObjects()){
			if (spatialObject instanceof PointObject){
				PointObject pointObject = (PointObject)spatialObject;
				boolean bPointObjectFree = true;
				for (SpatialProcess spatialProcess : simulationContext.getSpatialProcesses()){
					if (spatialProcess.getSpatialObjects().contains(pointObject)){
						bPointObjectFree = false;
						break;
					}
				}
				if (bPointObjectFree){
					return pointObject;
				}
			}
		}
		return simulationContext.createPointObject();
	}

	private SurfaceRegionObject getFreeSurfaceRegionObject(){
		for (SpatialObject spatialObject : simulationContext.getSpatialObjects()){
			if (spatialObject instanceof SurfaceRegionObject){
				SurfaceRegionObject surfaceRegionObject = (SurfaceRegionObject)spatialObject;
				boolean bSurfaceRegionObjectFree = true;
				for (SpatialProcess spatialProcess : simulationContext.getSpatialProcesses()){
					if (spatialProcess.getSpatialObjects().contains(surfaceRegionObject)){
						bSurfaceRegionObjectFree = false;
						break;
					}
				}
				if (bSurfaceRegionObjectFree){
					return surfaceRegionObject;
				}
			}
		}
		return null;
	}

	private void createNewPointLocation() {
		if (simulationContext!=null){
			PointObject pointObject = getOrCreateNextPointObject();
			pointObject.setQuantityCategoryEnabled(QuantityCategory.PointPosition, true);
			pointObject.setQuantityCategoryEnabled(QuantityCategory.PointVelocity, false);
			simulationContext.createPointLocation(getOrCreateNextPointObject());
		}
	}
	private void createNewPointKinematics() {
		if (simulationContext!=null){
			PointObject pointObject = getOrCreateNextPointObject();
			pointObject.setQuantityCategoryEnabled(QuantityCategory.PointPosition, true);
			pointObject.setQuantityCategoryEnabled(QuantityCategory.PointVelocity, true);
			simulationContext.createPointKinematics(pointObject);
		}
	}
	private void createNewSurfaceKinematics() {
		if (simulationContext!=null){
			SurfaceRegionObject surfaceRegionObject = getFreeSurfaceRegionObject();
			if (surfaceRegionObject!=null){
				surfaceRegionObject.setQuantityCategoryEnabled(QuantityCategory.SurfaceVelocity, true);
				simulationContext.createSurfaceKinematics(surfaceRegionObject);
			}
		}
	}

	@Override
	protected BioModelEditorApplicationRightSideTableModel<SpatialProcess> createTableModel() {
		return new SpatialProcessTableModel(table);
	}
	
}
