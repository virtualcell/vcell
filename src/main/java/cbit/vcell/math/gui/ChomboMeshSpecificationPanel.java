/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math.gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.vcell.chombo.ChomboMeshValidator;
import org.vcell.chombo.ChomboMeshValidator.ChomboMeshRecommendation;
import org.vcell.chombo.ChomboMeshValidator.ChomboMeshSpec;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.geometry.ChomboGeometryException;
import cbit.vcell.geometry.ChomboInvalidGeometryException;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.solver.Simulation;

public class ChomboMeshSpecificationPanel extends CollapsiblePanel {
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField geometrySizeTextField = null;
	private Simulation simulation = null;
	private JComboBox<Float> HComboBox;
	private JComboBox<Integer> NxComboBox;
	private JComboBox<Integer> NyComboBox;
	private JComboBox<Integer> NzComboBox;
	private JLabel NxLabel;
	private JLabel NyLabel;
	private JLabel NzLabel;

	private class IvjEventHandler implements ActionListener, PropertyChangeListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == HComboBox || e.getSource() == NxComboBox || e.getSource() == NyComboBox
					|| e.getSource() == NzComboBox) {
				int selectedIndex = ((JComboBox<?>) e.getSource()).getSelectedIndex();
				updateMesh(selectedIndex);
			}

		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			try {
				updateDisplay(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * MeshSpecificationPanel constructor comment.
	 */
	public ChomboMeshSpecificationPanel() {
		super("Mesh Size");
		initialize();
	}

	/**
	 * Return the GeometrySizeLabel property value.
	 * 
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getGeometrySizeTextField() {
		if (geometrySizeTextField == null) {
			try {
				geometrySizeTextField = new javax.swing.JTextField();
				geometrySizeTextField.setName("GeometrySizeTextField");
				geometrySizeTextField.setForeground(java.awt.Color.blue);
				geometrySizeTextField.setEditable(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return geometrySizeTextField;
	}

	/**
	 * Called whenever the part throws an exception.
	 * 
	 * @param exception
	 *          java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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
			setName("MeshSpecificationPanel");
			getContentPanel().setLayout(new java.awt.GridBagLayout());
			setSize(324, 310);
			setEnabled(false);

			HComboBox = new JComboBox<Float>();
			HComboBox.setName("HComboBox");
			HComboBox.addActionListener(ivjEventHandler);
			
			NxComboBox = new JComboBox<Integer>();
			NxComboBox.setName("NxComboBox");
			NxComboBox.addActionListener(ivjEventHandler);
			
			NyComboBox = new JComboBox<Integer>();
			NyComboBox.setName("NyComboBox");
			NyComboBox.addActionListener(ivjEventHandler);
			
			NzComboBox = new JComboBox<Integer>();
			NzComboBox.setName("NzComboBox");
			NzComboBox.addActionListener(ivjEventHandler);

			// 0
			int gridy = 0;
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			getContentPanel().add(new JLabel("Geometry Size (um)"), gbc);

			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			getContentPanel().add(getGeometrySizeTextField(), gbc);

			//
			gridy++;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.gridwidth = 3;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			JLabel lbl = new JLabel(
					"<html>Enter one of the following: either \u0394x(spatial step for discretization), or the number of points in one dimension, x, y or z.</html>");
			getContentPanel().add(lbl, gbc);

			gridy++;
			int gridx = -1;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			lbl = new JLabel("\u0394x");
			getContentPanel().add(lbl, gbc);
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			NxLabel = new JLabel("Nx");
			getContentPanel().add(NxLabel, gbc);

			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			NyLabel = new JLabel("Ny");
			getContentPanel().add(NyLabel, gbc);

			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			NzLabel = new JLabel("Nz");
			getContentPanel().add(NzLabel, gbc);

			// H
			gridy++;
			gridx = -1;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			getContentPanel().add(HComboBox, gbc);

			// Nx
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			getContentPanel().add(NxComboBox, gbc);

			// Ny
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			getContentPanel().add(NyComboBox, gbc);

			// Nz
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = ++gridx;
			gbc.gridy = gridy;
			gbc.anchor = java.awt.GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(4, 4, 1, 4);
			getContentPanel().add(NzComboBox, gbc);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	public void setSimulation(Simulation newValue) throws ChomboGeometryException, ChomboInvalidGeometryException {
		Simulation oldValue = simulation;
		if (oldValue != null)
		{
			oldValue.getSolverTaskDescription().removePropertyChangeListener(ivjEventHandler);
		}
		simulation = newValue;
		if (newValue != null)
		{
			newValue.getSolverTaskDescription().addPropertyChangeListener(ivjEventHandler);
		}
		updateDisplay(false);
	}

	private void updateDisplay(boolean bSolverChanged) throws ChomboGeometryException, ChomboInvalidGeometryException {
		if (!simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver()) {
			setVisible(false);
			return;
		}

		Geometry geometry = simulation.getMathDescription().getGeometry();
		Extent extent = geometry.getExtent();
		int dimension = geometry.getDimension();
		switch (dimension) {
		case 0:
			setVisible(false);
			break;
		case 1:
			getGeometrySizeTextField().setText("" + extent.getX());
			NyLabel.setVisible(false);
			NyComboBox.setVisible(false);
			NzLabel.setVisible(false);
			NzComboBox.setVisible(false);
			break;
		case 2:
			getGeometrySizeTextField().setText("(" + extent.getX() + ", " + extent.getY() + ")");
			NzLabel.setVisible(false);
			NzComboBox.setVisible(false);
			break;
		case 3:
			getGeometrySizeTextField().setText("(" + extent.getX() + ", " + extent.getY() + ", " + extent.getZ() + ")");
			break;
		}

		String error;
		ChomboMeshRecommendation meshRecommendation = new ChomboMeshValidator(geometry.getDimension(), geometry.getExtent(),
				simulation.getSolverTaskDescription().getChomboSolverSpec().getBlockFactor()).computeMeshSpecs();
		if (meshRecommendation.validate()) {
			// remove ActionListener, here we only want to set values
			removeComboBoxListener();
			HComboBox.removeAll();
			NxComboBox.removeAll();
			NyComboBox.removeAll();
			NzComboBox.removeAll();
			for (ChomboMeshSpec meshSpec : meshRecommendation.validMeshSpecList) {
				HComboBox.addItem((float)meshSpec.H);
				NxComboBox.addItem(meshSpec.Nx[0]);
				if (geometry.getDimension() > 1) {
					NyComboBox.addItem(meshSpec.Nx[1]);
					if (geometry.getDimension() == 3) {
						NzComboBox.addItem(meshSpec.Nx[2]);
					}
				}
			}
			addComboBoxListener();
			if (bSolverChanged)
			{
				NxComboBox.setSelectedIndex(0);
			}
			else 
			{
				ISize samplingSize = simulation.getMeshSpecification().getSamplingSize();
				NxComboBox.setSelectedItem(samplingSize.getX());
				// double check if existing mesh size is an option in drop down
				Integer selectedNx = (Integer) NxComboBox.getSelectedItem();
				Integer selectedNy = geometry.getDimension() > 1 ? (Integer) NyComboBox.getSelectedItem() : 1;
				Integer selectedNz = geometry.getDimension() > 2 ? (Integer) NzComboBox.getSelectedItem() : 1;
				boolean bMatchFound = selectedNx == samplingSize.getX()
						&& (dimension < 2 || selectedNy == samplingSize.getY())
						&& (dimension < 3 || selectedNz == samplingSize.getZ());
						
				if (!bMatchFound)
				{
					NxComboBox.setSelectedIndex(0);
					throw new ChomboGeometryException(ChomboMeshValidator.ERROR_MESSAGE_INCOMPATIBLE_MESH_SIZE);
				}
			}
		}
		else
		{
			throw new ChomboInvalidGeometryException(meshRecommendation);
		}
	}

	private void removeComboBoxListener()
	{
		HComboBox.removeActionListener(ivjEventHandler);
		NxComboBox.removeActionListener(ivjEventHandler);
		NyComboBox.removeActionListener(ivjEventHandler);
		NzComboBox.removeActionListener(ivjEventHandler);
	}
	
	private void addComboBoxListener()
	{
		HComboBox.addActionListener(ivjEventHandler);
		NxComboBox.addActionListener(ivjEventHandler);
		NyComboBox.addActionListener(ivjEventHandler);
		NzComboBox.addActionListener(ivjEventHandler);
	}
	
	private void updateMesh(int selectedIndex) {
		Geometry geometry = simulation.getMathDescription().getGeometry();
		if (selectedIndex >= HComboBox.getItemCount() || selectedIndex >= NxComboBox.getItemCount()
				|| geometry.getDimension() > 1 && selectedIndex >= NyComboBox.getItemCount()
				|| geometry.getDimension() > 2 && selectedIndex >= NzComboBox.getItemCount()) {
			return;
		}
		String error = null;
		// remove ActionListener, here we only want to set values
		removeComboBoxListener();
		HComboBox.setSelectedIndex(selectedIndex);
		NxComboBox.setSelectedIndex(selectedIndex);
		if (geometry.getDimension() > 1)
		{
			NyComboBox.setSelectedIndex(selectedIndex);
			if (geometry.getDimension() > 2)
			{
				NzComboBox.setSelectedIndex(selectedIndex);
			}
		}
		addComboBoxListener();
		
		Integer Nx = (Integer) NxComboBox.getSelectedItem();
		Integer Ny = geometry.getDimension() > 1 ? (Integer) NyComboBox.getSelectedItem() : 1;
		Integer Nz = geometry.getDimension() > 2 ? (Integer) NzComboBox.getSelectedItem() : 1;
		try {
			ISize iSize = new ISize(Nx, Ny, Nz);
			simulation.getMeshSpecification().setSamplingSize(iSize);
		} catch (NumberFormatException nexc) {
			error = "NumberFormatException " + nexc.getMessage();
		} catch (java.beans.PropertyVetoException pexc) {
			error = pexc.getMessage();
		}
		if (error != null) {
			DialogUtils.showErrorDialog(this, "Error setting mesh size : " + error);
		}
	}
}
