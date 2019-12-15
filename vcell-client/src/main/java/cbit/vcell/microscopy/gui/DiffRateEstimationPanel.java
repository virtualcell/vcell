/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.resource.PropertyLoader;

@SuppressWarnings("serial")
public class DiffRateEstimationPanel extends JPanel
{
	private static final double gfpWeight = 27;
	private static final double gfpDiffRate = 15;
	
	private JTextField diffRateTextField;
	private JTextField weightTextField;
	
	public DiffRateEstimationPanel() {
		super();
		setPreferredSize(new Dimension(580, 200));
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7,7,7};
		gridBagLayout.rowHeights = new int[] {0,7,7,0,7,0,7};
		setLayout(gridBagLayout);

		final JLabel calculateDiffusionRateLabel = new JLabel();
		calculateDiffusionRateLabel.setText("Estimate diffusion rate by molecular weight");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridwidth = 7;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		add(calculateDiffusionRateLabel, gridBagConstraints_1);

		final JLabel estimationBasedOnLabel = new JLabel();
		estimationBasedOnLabel.setText("Estimation based on GFP (Diffusion rate of 15 um2/s,  molecular weight of 27 kD).");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 7;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridx = 0;
		add(estimationBasedOnLabel, gridBagConstraints);

		final JLabel particleMolecularLabel = new JLabel();
		particleMolecularLabel.setText("Particle molecular weight");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 5;
		gridBagConstraints_2.gridx = 0;
		add(particleMolecularLabel, gridBagConstraints_2);

		weightTextField = new JTextField();
		weightTextField.setColumns(8);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridy = 5;
		gridBagConstraints_3.gridx = 2;
		add(weightTextField, gridBagConstraints_3);

		final JLabel kdLabel = new JLabel();
		kdLabel.setText(" kD");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 5;
		gridBagConstraints_8.gridx = 3;
		add(kdLabel, gridBagConstraints_8);

		final JButton estimateButton = new JButton();
		estimateButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				estimateDiffRate();
			}
		});
		estimateButton.setMargin(new Insets(0, 16, 0, 18));
		estimateButton.setText("Estimate");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_6.ipadx = 10;
		gridBagConstraints_6.gridy = 5;
		gridBagConstraints_6.gridx = 6;
		add(estimateButton, gridBagConstraints_6);

		final JLabel particleDiffLabel = new JLabel();
		particleDiffLabel.setText("Particle Diffusion Rate");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 7;
		gridBagConstraints_4.gridx = 0;
		add(particleDiffLabel, gridBagConstraints_4);

		diffRateTextField = new JTextField();
		diffRateTextField.setColumns(8);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 7;
		gridBagConstraints_5.gridx = 2;
		add(diffRateTextField, gridBagConstraints_5);

		final JLabel um2sLabel = new JLabel();
		um2sLabel.setText(" um2/s");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.gridy = 7;
		gridBagConstraints_9.gridx = 3;
		add(um2sLabel, gridBagConstraints_9);


		final JButton searchWebButton = new JButton();
		searchWebButton.setMargin(new Insets(0, 14, 0, 14));
		searchWebButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				final String url = BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.BIONUMBERS_SRCH2_URL);
				DialogUtils.browserLauncher(DiffRateEstimationPanel.this, url, null);
			}
		});
		searchWebButton.setText("Search Web");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridy = 7;
		gridBagConstraints_7.gridx = 6;
		add(searchWebButton, gridBagConstraints_7);
	}
	
	private void estimateDiffRate()
	{
		try{
			double weight = Double.parseDouble(weightTextField.getText());
			double diffRate = (Math.pow(weight, -0.333)*gfpDiffRate)/Math.pow(gfpWeight, -0.333);
			diffRateTextField.setText(NumberUtils.formatNumber(diffRate,10));
		}catch(NumberFormatException e)
		{
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(DiffRateEstimationPanel.this, "Particle molecular weight is empty or in illegal form." + e.getMessage());
		}
	}
	
	public String getDiffRate() 
	{
		String diffRate = null;
		
		diffRate = diffRateTextField.getText();

		return diffRate;
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			DiffRateEstimationPanel aPanel = new DiffRateEstimationPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(580,200);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	
}
