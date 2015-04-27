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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.microscopy.gui.estparamwizard.FRAPReacDiffEstimationGuidePanel;

@SuppressWarnings("serial")
public class DiffOnRateEstimationPanel extends JPanel 
{
	private JPanel calculationPanel;
	private JPanel selectionPanel;
	private JRadioButton chooseRadioButton;
	private JRadioButton calculateRadioButton;
	
	private JTextField BsRadiusTextField;
	private JTextField FRadiusTextField;
	private JLabel onRateValLabel;
	
	private JRadioButton E6RadioButton;
	private JRadioButton E5RadioButton;
	private JRadioButton E4RadioButton;
	private JRadioButton E3RadioButton;
	private JRadioButton E2RadioButton;
	private JRadioButton E1RadioButton;
	private JRadioButton E0RadioButton;
	
	private Double freeDiffRate;
	
	public DiffOnRateEstimationPanel()
	{
		super();
		setPreferredSize(new Dimension(600, 320));
		final GridBagLayout gridBagLayout = new GridBagLayout();
		final GridBagLayout gridBagLayout_3 = new GridBagLayout();
		gridBagLayout_3.rowHeights = new int[] {0,0,0,0,7,7};
		this.setLayout(gridBagLayout_3);
		gridBagLayout.columnWidths = new int[] {7,0};
		gridBagLayout.rowHeights = new int[] {7,7,7,7,7,7,0,0,7,7};

		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {7,7,7,0,7,7};
		gridBagLayout_1.columnWidths = new int[] {0,0,0,7};
		selectionPanel = new JPanel(gridBagLayout_1);
		selectionPanel.setBorder(new LineBorder(Color.gray, 1, false));
		
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.gridy = 4;
		gridBagConstraints_11.gridx = 1;

		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.gridy = 5;
		gridBagConstraints_15.gridx = 1;
		

		final JLabel ctotTotalLabel = new JLabel();
		ctotTotalLabel.setText("   (Cm_tot = Ctot * Primay Mobile Fraction)");
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.anchor = GridBagConstraints.WEST;
		gridBagConstraints_16.gridwidth = 3;
		gridBagConstraints_16.gridy = 5;
		gridBagConstraints_16.gridx = 2;
		
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.gridx = 0;
		add(selectionPanel, gridBagConstraints2);

		chooseRadioButton = new JRadioButton();
		chooseRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				BeanUtils.enableComponents(calculationPanel, false);
				BeanUtils.enableComponents(selectionPanel, true);
				calculateRadioButton.setEnabled(true);
			}
		});
		chooseRadioButton.setText("Choose one of the following diffusion limited on rate (in 1/(uM.s)).");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.ipadx = 35;
		gridBagConstraints_1.gridwidth = 4;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		selectionPanel.add(chooseRadioButton, gridBagConstraints_1);

		
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 5;
		gridBagConstraints_4.gridx = 2;
		
		
		final JLabel smallerMoleculeLabel = new JLabel();
		smallerMoleculeLabel.setText("Small molecule");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.ipadx = 35;
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		selectionPanel.add(smallerMoleculeLabel, gridBagConstraints_2);

		final JLabel mediumLabel = new JLabel();
		mediumLabel.setText("Medium molecule");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.gridy = 2;
		gridBagConstraints_6.gridx = 2;
		selectionPanel.add(mediumLabel, gridBagConstraints_6);

		final JLabel largeMoleculeLabel = new JLabel();
		largeMoleculeLabel.setText("Large molecule");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 3;
		selectionPanel.add(largeMoleculeLabel, gridBagConstraints_5);

		E6RadioButton = new JRadioButton();
		E6RadioButton.setText("1E6");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.ipadx = 35;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridx = 0;
		selectionPanel.add(E6RadioButton, gridBagConstraints);

		E4RadioButton = new JRadioButton();
		E4RadioButton.setText("1E4");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridy = 3;
		gridBagConstraints_7.gridx = 2;
		selectionPanel.add(E4RadioButton, gridBagConstraints_7);

		E1RadioButton = new JRadioButton();
		E1RadioButton.setText("10");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.gridy = 3;
		gridBagConstraints_17.gridx = 3;
		selectionPanel.add(E1RadioButton, gridBagConstraints_17);

		E5RadioButton = new JRadioButton();
		E5RadioButton.setText("1E5");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.ipadx = 35;
		gridBagConstraints_8.gridy = 4;
		gridBagConstraints_8.gridx = 0;
		selectionPanel.add(E5RadioButton, gridBagConstraints_8);

		E3RadioButton = new JRadioButton();
		E3RadioButton.setText("1E3");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.gridy = 4;
		gridBagConstraints_9.gridx = 2;
		selectionPanel.add(E3RadioButton, gridBagConstraints_9);

		E0RadioButton = new JRadioButton();
		E0RadioButton.setText("1");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.ipadx = 5;
		gridBagConstraints_18.gridy = 4;
		gridBagConstraints_18.gridx = 3;
		selectionPanel.add(E0RadioButton, gridBagConstraints_18);

		E2RadioButton = new JRadioButton();
		E2RadioButton.setText("1E2");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.gridy = 5;
		gridBagConstraints_10.gridx = 2;
		selectionPanel.add(E2RadioButton, gridBagConstraints_10);
	
		calculationPanel = new JPanel();
		calculationPanel.setBorder(new LineBorder(Color.gray, 1, false));
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.columnWidths = new int[] {0,0,0,0,0,0,0,7,0,7,0,7};
		gridBagLayout_2.rowHeights = new int[] {7,7};
		calculationPanel.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
		gridBagConstraints_28.ipadx = 180;
		gridBagConstraints_28.gridy = 6;
		gridBagConstraints_28.gridx = 0;
		add(calculationPanel, gridBagConstraints_28);

		calculateRadioButton = new JRadioButton();
		calculateRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				BeanUtils.enableComponents(selectionPanel, false);
				BeanUtils.enableComponents(calculationPanel, true);
				chooseRadioButton.setEnabled(true);
			}
		});
		calculateRadioButton.setText("Calculate from particle radius");
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.ipadx = 35;
		gridBagConstraints_19.gridwidth = 3;
		gridBagConstraints_19.gridx = 6;
		gridBagConstraints_19.gridy = 0;
		calculationPanel.add(calculateRadioButton, gridBagConstraints_19);

		final JLabel fParticleRadiusLabel = new JLabel();
		fParticleRadiusLabel.setText("F particle radius (um)");
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.anchor = GridBagConstraints.EAST;
		gridBagConstraints_20.gridy = 1;
		gridBagConstraints_20.gridx = 6;
		calculationPanel.add(fParticleRadiusLabel, gridBagConstraints_20);

		FRadiusTextField = new JTextField();
		FRadiusTextField.setColumns(8);
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.anchor = GridBagConstraints.WEST;
		gridBagConstraints_21.gridy = 1;
		gridBagConstraints_21.gridx = 8;
		calculationPanel.add(FRadiusTextField, gridBagConstraints_21);

		final JLabel bsParticleRadiusLabel = new JLabel();
		bsParticleRadiusLabel.setText("Bs particle radius (um)");
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_22.gridy = 1;
		gridBagConstraints_22.gridx = 9;
		calculationPanel.add(bsParticleRadiusLabel, gridBagConstraints_22);

		BsRadiusTextField = new JTextField();
		BsRadiusTextField.setColumns(8);
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.gridy = 1;
		gridBagConstraints_23.gridx = 12;
		calculationPanel.add(BsRadiusTextField, gridBagConstraints_23);

		final JLabel inputbsAsLabel_1 = new JLabel();
		inputbsAsLabel_1.setText("Reaction on rate:");
		final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_27.gridwidth = 3;
		gridBagConstraints_27.gridy = 3;
		gridBagConstraints_27.gridx = 4;
		calculationPanel.add(inputbsAsLabel_1, gridBagConstraints_27);

		onRateValLabel = new JLabel();
		onRateValLabel.setText("");
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.gridy = 3;
		gridBagConstraints_24.gridx = 8;
		calculationPanel.add(onRateValLabel, gridBagConstraints_24);

		final JButton calculatButton = new JButton();
		calculatButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				calculateDiffLimitedOnRate();
			}
		});
		calculatButton.setMargin(new Insets(0, 14, 0, 14));
		calculatButton.setText("calculate");
		final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.gridy = 3;
		gridBagConstraints_25.gridx = 12;
		calculationPanel.add(calculatButton, gridBagConstraints_25);
		
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(calculateRadioButton);
		bg1.add(chooseRadioButton);
		chooseRadioButton.setSelected(true);
		BeanUtils.enableComponents(calculationPanel, false);
		calculateRadioButton.setEnabled(true);
		
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(E0RadioButton);
		bg2.add(E1RadioButton);
		bg2.add(E2RadioButton);
		bg2.add(E3RadioButton);
		bg2.add(E4RadioButton);
		bg2.add(E5RadioButton);
		bg2.add(E6RadioButton);
		E3RadioButton.setSelected(true);

		final JButton searchWebButton = new JButton();
		searchWebButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				final String url ="http://bionumbers.hms.harvard.edu/search.aspx?log=y&task=searchbytrmorg&trm=second+order+rate+constants%2c+diffusion+limited&org=%25";
				DialogUtils.browserLauncher(DiffOnRateEstimationPanel.this, url, null); 
			}
		});
		searchWebButton.setMargin(new Insets(0, 10, 0, 10));
		searchWebButton.setText("Search Web");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridy = 5;
		gridBagConstraints_3.gridx = 3;
		selectionPanel.add(searchWebButton, gridBagConstraints_3);
	}
	
	public Double getFreeDiffRate() {
		return freeDiffRate;
	}

	public void setFreeDiffRate(Double freeDiffRate) {
		this.freeDiffRate = freeDiffRate;
	}
	
	private void calculateDiffLimitedOnRate()
	{
		try{
			double fRadius = Double.parseDouble(FRadiusTextField.getText());
			double bsRadius = Double.parseDouble(BsRadiusTextField.getText());
			double freeDiffRate = getFreeDiffRate().doubleValue();
			double bsDiffRate = 0;
			//kon = 4*PI*D*R, D is sum of reactants' diff rates, R is sum of reactants' radius. 1/602 is a conversion rate from um3/s to 1/(uM.s)
			double kon = 4*FRAPReacDiffEstimationGuidePanel.PI*(freeDiffRate+bsDiffRate)*(fRadius+bsRadius)/602.0;
			onRateValLabel.setText(NumberUtils.formatNumber(kon, 10));
		}catch(NumberFormatException e)
		{
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(DiffOnRateEstimationPanel.this, "Free particle radius or Bindingsite particle radius is empty or in illegal form.");
		}
	}
	
	public Double getOnRate()
	{
		Double onRate = null;
		if(chooseRadioButton.isSelected())
		{
			if(E6RadioButton.isSelected())
			{
				onRate = new Double("1E6");
			}
			else if(E5RadioButton.isSelected())
			{
				onRate = new Double("1E5");
			}
			else if(E4RadioButton.isSelected())
			{
				onRate = new Double("1E4");
			}
			else if(E3RadioButton.isSelected())
			{
				onRate = new Double("1E3");
			}
			else if(E2RadioButton.isSelected())
			{
				onRate = new Double("1E2");
			}
			else if (E1RadioButton.isSelected())
			{
				onRate = new Double("10");
			}
			else if(E0RadioButton.isSelected())
			{
				onRate = new Double("1");
			}
		}
		else if(calculateRadioButton.isSelected())
		{
			if(onRateValLabel.getText()!=null && !onRateValLabel.getText().equals(""))
			{
				try{
					onRate = new Double(onRateValLabel.getText());
				}catch(NumberFormatException e)
				{
					e.printStackTrace(System.out);
				}
			}
		}
		return onRate;
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			DiffOnRateEstimationPanel aPanel = new DiffOnRateEstimationPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(580,300);
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
