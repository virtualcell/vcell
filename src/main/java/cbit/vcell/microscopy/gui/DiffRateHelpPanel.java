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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.gui.ExpressionCanvas;

@SuppressWarnings("serial")
public class DiffRateHelpPanel extends JPanel
{
	private static final double gfpWeight = 27;
	private static final double gfpDiffRate = 15;
	
	public static final String effDiffExpString = "D_f/(1+(k_on_star/k_off))";
	private JTextField diffRateTextField = null;;
	private JTextField weightTextField = null;
	private ExpressionCanvas expressionCanvas1 = null;
	private ExpressionCanvas expressionCanvas2 = null;
	private JLabel effDiffValLabel = null;
	private JLabel rateRatioValLabel = null;
	
	public DiffRateHelpPanel() {
		super();
		setPreferredSize(new Dimension(520, 450));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7,7,7};
		gridBagLayout.rowHeights = new int[] {0,7,7,7,7,0,7,7,7,7,7};
		setLayout(gridBagLayout);

		JLabel label1 = new JLabel("<html>If the estimated  diffusion rate is identifiably  smaller than the expected rate, it may be " +
				"due to Effective Diffusion. Effective  diffusion arises when there is a binding reaction " +
				"and the reaction process is much faster than diffusion. Therefore, the recovery curve " +
				"can fit to a diffusion only model, but the estimated diff rate is smaller than expected. " +
				"The following analysis can help user figure out the ratio of reaction rates if expected " +
				"diffusion rate is available.</html>");
		
		GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridwidth = 7;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		add(label1, gridBagConstraints_1);

		expressionCanvas1 = new ExpressionCanvas();
		GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.anchor = GridBagConstraints.WEST;
		gridBagConstraints_16.gridwidth = 7;
		gridBagConstraints_16.gridy = 1;
		gridBagConstraints_16.gridx = 0;
		add(expressionCanvas1, gridBagConstraints_16);
		
		expressionCanvas2 = new ExpressionCanvas();
		GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.anchor = GridBagConstraints.WEST;
		gridBagConstraints_17.gridwidth = 7;
		gridBagConstraints_17.gridy = 2;
		gridBagConstraints_17.gridx = 0;
		add(expressionCanvas2, gridBagConstraints_17);
		initializeExpression();
		
		JPanel freeDiffRatePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.anchor = GridBagConstraints.WEST;
		gridBagConstraints_15.gridwidth = 7;
		gridBagConstraints_15.gridy = 3;
		gridBagConstraints_15.gridx = 0;
		add(freeDiffRatePanel, gridBagConstraints_15);
		freeDiffRatePanel.setBorder(new TitledBorder(new EtchedBorder(), "Expected free diffusion rate: ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("", Font.BOLD, 11), Color.blue));
		
		
		JLabel estimationBasedOnLabel = new JLabel("Estimation based on GFP (Diffusion rate of 15 um2/s,  molecular weight of 27 kD).");
		estimationBasedOnLabel.setFont(new Font("", Font.ITALIC, 12));
		estimationBasedOnLabel.setForeground(Color.blue);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridwidth = 7;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		freeDiffRatePanel.add(estimationBasedOnLabel, gridBagConstraints);

		JLabel label10= new JLabel(" ");
		GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.anchor = GridBagConstraints.WEST;
		gridBagConstraints_24.gridwidth = 7;
		gridBagConstraints_24.gridy = 1;
		gridBagConstraints_24.gridx = 0;
		freeDiffRatePanel.add(label10, gridBagConstraints_24);
		
		JLabel particleMolecularLabel = new JLabel("Particle molecular weight");
		GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		freeDiffRatePanel.add(particleMolecularLabel, gridBagConstraints_2);

		weightTextField = new JTextField(8);
		GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 2;
		freeDiffRatePanel.add(weightTextField, gridBagConstraints_3);

		JLabel kdLabel = new JLabel(" kD");
		GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.gridx = 3;
		freeDiffRatePanel.add(kdLabel, gridBagConstraints_8);

		JButton estimateButton = new JButton();
		estimateButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				estimateDiffRate();
			}
		});
		estimateButton.setMargin(new Insets(0, 16, 0, 18));
		estimateButton.setText(" Estimate");
		GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_6.ipadx = 10;
		gridBagConstraints_6.gridy = 2;
		gridBagConstraints_6.gridx = 6;
		freeDiffRatePanel.add(estimateButton, gridBagConstraints_6);

		JLabel label11= new JLabel(" ");
		GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.anchor = GridBagConstraints.WEST;
		gridBagConstraints_25.gridwidth = 7;
		gridBagConstraints_25.gridy = 3;
		gridBagConstraints_25.gridx = 0;
		freeDiffRatePanel.add(label11, gridBagConstraints_25);
		
		JLabel particleDiffLabel = new JLabel("Particle Diffusion Rate");
		GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 4;
		gridBagConstraints_4.gridx = 0;
		freeDiffRatePanel.add(particleDiffLabel, gridBagConstraints_4);

		diffRateTextField = new JTextField(8);
		GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 4;
		gridBagConstraints_5.gridx = 2;
		freeDiffRatePanel.add(diffRateTextField, gridBagConstraints_5);
		diffRateTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				refreshReactionRateRatio();
			}
			
			public void insertUpdate(DocumentEvent e) {
				refreshReactionRateRatio();
			}
			
			public void changedUpdate(DocumentEvent e) {
				refreshReactionRateRatio();
			}
		});

		JLabel um2sLabel = new JLabel(" um2/s");
		GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.gridy = 4;
		gridBagConstraints_9.gridx = 3;
		freeDiffRatePanel.add(um2sLabel, gridBagConstraints_9);

		JButton searchWebButton = new JButton();
		searchWebButton.setMargin(new Insets(0, 14, 0, 14));
		searchWebButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) 
			{
				final String url = "http://bionumbers.hms.harvard.edu/search.aspx?log=y&task=searchbytrmorg&trm=diffusion+rate&org=%25";
				DialogUtils.browserLauncher(DiffRateHelpPanel.this, url, null); 
			}
		});
		searchWebButton.setText("Search Web");
		GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridy = 4;
		gridBagConstraints_7.gridx = 6;
		freeDiffRatePanel.add(searchWebButton, gridBagConstraints_7);
		
		GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.gridwidth = 7;
		gridBagConstraints_20.gridy = 4;
		gridBagConstraints_20.gridx = 0;
		add(new JLabel(" "), gridBagConstraints_20);
		
		JLabel label8 = new JLabel("We have estimated effective diffusion rate(D_eff) as " );
		GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.gridwidth = 4;
		gridBagConstraints_21.gridy = 5;
		gridBagConstraints_21.gridx = 0;
		add(label8, gridBagConstraints_21);
		
		effDiffValLabel = new JLabel("");
		GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.gridwidth = 1;
		gridBagConstraints_22.gridy = 5;
		gridBagConstraints_22.gridx = 4;
		add(effDiffValLabel, gridBagConstraints_22);
		
		JLabel label9 = new JLabel(" ");
		GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.gridwidth = 7;
		gridBagConstraints_23.gridy = 6;
		gridBagConstraints_23.gridx = 0;
		add(label9, gridBagConstraints_23);
		
		JLabel label7 = new JLabel("The ratio of reaction pseudo on rate and off rate(k_on_star/k_off) is : ");
		GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.gridwidth = 5;
		gridBagConstraints_18.gridy = 7;
		gridBagConstraints_18.gridx = 0;
		add(label7, gridBagConstraints_18);
		
		rateRatioValLabel = new JLabel("");
		GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.gridwidth = 1;
		gridBagConstraints_19.gridy = 7;
		gridBagConstraints_19.gridx = 5;
		add(rateRatioValLabel, gridBagConstraints_19);
	}
	
	private void initializeExpression() 
	{
		try{
			String[] prefixes = new String[] {"D_eff = "};
			Expression[] expressions = new Expression[] {new Expression(effDiffExpString)};
			String[] suffixes = new String[] { "", "" };
			expressionCanvas1.setExpressions(expressions,prefixes,suffixes);
			expressionCanvas2.setStrings(new String[]{"D_eff : Slowed effective diffusion rate due to binding.", "D_f: Diffusion rate of the molecule in the absence of binding.", "k_on_star: Pseudo reaction on rate.  k_off: Reaction off rate."});
		}catch (ExpressionException e2){
			e2.printStackTrace(System.out);
		}		
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
			DialogUtils.showErrorDialog(DiffRateHelpPanel.this, "Particle molecular weight is empty or in illegal form." + e.getMessage());
		}
	}
	
	public void setEffectiveDiffRate(double effDiffRate)
	{
		effDiffValLabel.setText(NumberUtils.formatNumber(effDiffRate,6));
	}
	
	public void refreshReactionRateRatio()
	{
		try{
			double freeDiffRate = Double.parseDouble(diffRateTextField.getText());
			double effDiffRate = Double.parseDouble(effDiffValLabel.getText());
			double ratio = freeDiffRate/effDiffRate -1;
			rateRatioValLabel.setText(NumberUtils.formatNumber(ratio, 6));		
		}catch(NumberFormatException e)
		{
			rateRatioValLabel.setText("");
//			throw e;//ratio is upated by document listener, therefore, don't throw exception or error message
		}
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			DiffRateHelpPanel aPanel = new DiffRateHelpPanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(580,600);
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
