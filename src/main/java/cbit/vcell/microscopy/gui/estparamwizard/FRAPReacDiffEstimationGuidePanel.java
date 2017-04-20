/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.microscopy.EstimatedParameter;
import cbit.vcell.microscopy.EstimatedParameterTableModel;
import cbit.vcell.microscopy.EstimatedParameterTableRenderer;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPUnitSystem;
import cbit.vcell.microscopy.gui.DiffOnRateEstimationPanel;
import cbit.vcell.microscopy.gui.DiffRateEstimationPanel;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;

@SuppressWarnings("serial")
public class FRAPReacDiffEstimationGuidePanel extends JPanel {
	private JTextField mobileTotalFracTextField;
	private JTextField diffEffectiveTextField;
	private JTextField freeDiffTextField;
	
	private DiffRateEstimationPanel diffRateEstPanel = null;
	private DiffOnRateEstimationPanel onRateEstPanel = null;
	
	private JPanel diffTypePanel = null;
	private JRadioButton koffButton = new JRadioButton("Off Rate (K_off)");
	private JRadioButton konButton = new JRadioButton("Pseudo On Rate (K_on*)");
	private JTextField koffTextField = new JTextField(8);
	private JTextField konTextField = new JTextField(8);
	private JButton estButton = new JButton("Estimate Reaction Diffusion Parameters");
	private JButton helpOnRateButton = null;
	
	// to get units for diffusion rate, etc. in 'updateTableParameterParameter()' 
	private FRAPUnitSystem frapUnitsystem = null;
	
	private JScrollPane tableScroll = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JTable paramTable = new JTable();
	private EstimatedParameterTableModel paramTableModel = new EstimatedParameterTableModel(FRAPReacDiffEstimationGuidePanel.this);
	
	public static final double PI = 3.14159;
	public static final double RELATIVE_TOTAL_CONCENTRATION = 1;
	//The following parameters are used to store parameters from pure diffusion
	double effDiffRate;
	double totMobileFraction;
	double bwmRate;
	double immFraction;
	
	EstimatedParameter[] estimatedParameters = new EstimatedParameter[paramNames.length];
	public static final int IDX_FreePartDiffRate	= 0;
	public static final int IDX_FreePartConc = 1;
	public static final int IDX_ComplexConc = 2;
	public static final int IDX_ReacPseudoOnRate   = 3;
	public static final int IDX_ReacOffRate    	= 4;
	public static final int IDX_BWMRate   = 5;
	public static final int IDX_Immobile 	= 6;
	
	public static final String[] paramNames = new String[]{"D_f",
		                                                   "F_f",
		                                                   "F_c",
		                                                   "K_on_pseudo",
		                                                   "K_off",
		                                                   "BWM",
		                                                   "C_imm"};
	
	public static final String[] paramDescriptions = new String[]{"Free Particle Diffusion Rate",
		                                                          "Free Particle Fraction",
		                                                          "Complex Fraction",
		                                                          "Pseudo Reaction On Rate",
		                                                          "Reaction Off Rate",
		                                                          "Bleach While Monitoring Rate",
		                                                          "Immobile Concentration"};
	
	public static final String freeDiffRateStr_eff = "D_prim*(1+(C_c/C_f))"; 
	public static final String complexFracStr_oneDiffComponent = "1-Frac_prim";
	//if we have two diffusion components, otherwise we'll use different exp for free diff rate, complex diff rate
	public static final String[] paramExpStr = new String[]{      "", //free diff rate, require estimated
															      "(Cm_tot*K_off)/(K_on_pseudo+K_off)", //free diff concentration
															      "(Cm_tot*K_on_pseudo)/(K_on_pseudo+K_off)", //complex concentration
															      "((D_f-D_eff)*K_off)/(D_eff)", //pseudo on rate
															      "(K_on_pseudo*D_eff)/(D_f-D_eff)", //off rate
															      "", //bwm rate
															      "" }; //imm 
	
	public FRAPReacDiffEstimationGuidePanel() {
		super();
		
		// create the FRAPUnitSystem
		this.frapUnitsystem = new FRAPUnitSystem();
		
		setPreferredSize(new Dimension(580, 450));
		this.setLayout(new GridBagLayout());

		//the top panel
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,7,7,7,7};
		gridBagLayout_2.columnWidths = new int[] {7,7,7,7};
		diffTypePanel = new JPanel(gridBagLayout_2);
		TitledBorder tb=new TitledBorder(new LineBorder(Color.gray, 1, false),"Available parameters", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
		diffTypePanel.setBorder(tb);

		final JLabel totalConcentrationLabel = new JLabel();
		totalConcentrationLabel.setText("Total Concentration (C_tot)");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.anchor = GridBagConstraints.WEST;
		gridBagConstraints_17.gridy = 3;
		gridBagConstraints_17.gridx = 0;
		diffTypePanel.add(totalConcentrationLabel, gridBagConstraints_17);

		final JLabel totalConcVallabel = new JLabel();
		totalConcVallabel.setText("1");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.gridy = 3;
		gridBagConstraints_18.gridx = 1;
		diffTypePanel.add(totalConcVallabel, gridBagConstraints_18);

		final JLabel effectiveDiffusionRateLabel = new JLabel();
		effectiveDiffusionRateLabel.setText("Effective Diffusion Rate (D_eff) ");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.anchor = GridBagConstraints.EAST;
		gridBagConstraints_10.gridy = 4;
		gridBagConstraints_10.gridx = 0;
		diffTypePanel.add(effectiveDiffusionRateLabel, gridBagConstraints_10);
		JLabel diffTypeLabel = new JLabel();
		diffTypeLabel.setText("                   The estimation is based on 'Effective Diffusion' assumption");
		final GridBagConstraints gridBagConstraints_dtl = new GridBagConstraints();
		gridBagConstraints_dtl.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_dtl.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_dtl.gridy = 0;
		gridBagConstraints_dtl.gridx = 0;
		gridBagConstraints_dtl.gridwidth = 5;
		diffTypePanel.add(diffTypeLabel, gridBagConstraints_dtl);
 		
		//the mid panel
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowHeights = new int[] {7,7,7};
		gridBagLayout_1.columnWidths = new int[] {0,7,7,0,0,7,0,7,7,7,0,7};
		JPanel buttonPanel = new JPanel(gridBagLayout_1);
		TitledBorder tb1=new TitledBorder(new LineBorder(Color.gray, 1, false),"Input requried parameters to estimate", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
		buttonPanel.setBorder(tb1);

		final JLabel koffUnitLabel = new JLabel();
		koffUnitLabel.setText("s-1");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 4;
		gridBagConstraints_5.gridx = 5;
		buttonPanel.add(koffUnitLabel, gridBagConstraints_5);
		JLabel freeDiffLabel = new JLabel("Free Diffusion Rate (D_f)");
		
		final GridBagConstraints gridBagConstraints_koffRadio = new GridBagConstraints();
		gridBagConstraints_koffRadio.anchor = GridBagConstraints.WEST;
		gridBagConstraints_koffRadio.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_koffRadio.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_koffRadio.gridy = 4;
		gridBagConstraints_koffRadio.gridx = 0;

		final JLabel bindingReactionLabel = new JLabel();
		bindingReactionLabel.setText("Binding Reaction: F + Bs = C");
		bindingReactionLabel.setBorder(new LineBorder(new Color(120,120,188), 1));
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.gridwidth = 13;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 0;
		buttonPanel.add(bindingReactionLabel, gridBagConstraints_1);
		buttonPanel.add(koffButton, gridBagConstraints_koffRadio);
		koffButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				koffTextField.setEditable(true);
				konTextField.setEditable(false);
				helpOnRateButton.setEnabled(false);
			}
		});
		
		final GridBagConstraints gridBagConstraints_koffTf = new GridBagConstraints();
		gridBagConstraints_koffTf.anchor = GridBagConstraints.WEST;
		gridBagConstraints_koffTf.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_koffTf.gridy = 4;
		gridBagConstraints_koffTf.gridx = 2;
		buttonPanel.add(koffTextField, gridBagConstraints_koffTf);
		
		final GridBagConstraints gridBagConstraints_konLabel = new GridBagConstraints();
		gridBagConstraints_konLabel.anchor = GridBagConstraints.WEST;
		gridBagConstraints_konLabel.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_konLabel.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_konLabel.gridy = 2;
		gridBagConstraints_konLabel.gridx = 0;
		buttonPanel.add(freeDiffLabel, gridBagConstraints_konLabel);
		
		final GridBagConstraints gridBagConstraints_bsRadio = new GridBagConstraints();
		gridBagConstraints_bsRadio.anchor = GridBagConstraints.WEST;
		gridBagConstraints_bsRadio.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_bsRadio.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_bsRadio.gridy = 3;
		gridBagConstraints_bsRadio.gridx = 0;

		freeDiffTextField = new JTextField();
		freeDiffTextField.setColumns(8);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 2;
		buttonPanel.add(freeDiffTextField, gridBagConstraints_3);

		final JLabel freeRadiumUnitLabel = new JLabel();
		freeRadiumUnitLabel.setText("um2/s");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 5;
		buttonPanel.add(freeRadiumUnitLabel, gridBagConstraints_7);

		helpOnRateButton = new JButton();
		helpOnRateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				estimateOnRate();
			}
		});
		helpOnRateButton.setMargin(new Insets(0, 14, 0, 14));
		helpOnRateButton.setText("Help On Decision");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 9;
		buttonPanel.add(helpOnRateButton, gridBagConstraints_8);
		buttonPanel.add(konButton, gridBagConstraints_bsRadio);
		konButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				konTextField.setEditable(true);
				helpOnRateButton.setEnabled(true);
				koffTextField.setEditable(false);
			}
		});
		
		final GridBagConstraints gridBagConstraints_bsTf = new GridBagConstraints();
		gridBagConstraints_bsTf.anchor = GridBagConstraints.WEST;
		gridBagConstraints_bsTf.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_bsTf.gridy = 3;
		gridBagConstraints_bsTf.gridx = 2;
		buttonPanel.add(konTextField, gridBagConstraints_bsTf);
		
		
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(koffButton);
		bg2.add(konButton);
		konButton.setSelected(true);
		konTextField.setEditable(true);
		koffTextField.setEditable(false);
		
		//bottom panel----the table panel
		paramTable.setModel(paramTableModel);//set table model
		final EstimatedParameterTableRenderer renderer = new EstimatedParameterTableRenderer(8); //set table renderer
		for(int i=0; i<paramTable.getModel().getColumnCount(); i++)
		{
			TableColumn column = paramTable.getColumnModel().getColumn(i);
			column.setCellRenderer(renderer);			
		}
//		paramTable.setDefaultRenderer(paramTable.getColumnClass(EstimatedParameterTableModel.COLUMN_VALUE), renderer);
		tableScroll.setViewportView(paramTable);
//		tableScroll.setMinimumSize(new Dimension(0, 0));
		tableScroll.setName("");
		tableScroll.setPreferredSize(new Dimension(0, 0));
		paramTable.setName("");
//		paramTable.setMinimumSize(new Dimension(0, 0));
//		paramTable.setMaximumSize(new Dimension(0, 0));
				
		// The base panel
		final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.ipady = 50;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridx = 0;
		add(diffTypePanel, gridBagConstraints1);

		diffEffectiveTextField = new JTextField();
		diffEffectiveTextField.setColumns(8);
		diffEffectiveTextField.setEditable(false);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.gridy = 4;
		gridBagConstraints_11.gridx = 1;
		diffTypePanel.add(diffEffectiveTextField, gridBagConstraints_11);

		final JLabel um2sLabel = new JLabel();
		um2sLabel.setText("  um2/s");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.gridy = 4;
		gridBagConstraints_12.gridx = 2;
		diffTypePanel.add(um2sLabel, gridBagConstraints_12);

		final JLabel deffPrimaryLabel = new JLabel();
		deffPrimaryLabel.setText("(D_eff = Primary Diffusion Rate)");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.gridy = 4;
		gridBagConstraints_13.gridx = 4;
		diffTypePanel.add(deffPrimaryLabel, gridBagConstraints_13);

		final JLabel mobileFluorescenceLabel = new JLabel();
		mobileFluorescenceLabel.setText("Total Mobile Conc. (Cm_tot) ");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.anchor = GridBagConstraints.EAST;
		gridBagConstraints_14.fill = GridBagConstraints.BOTH;
		gridBagConstraints_14.gridy = 5;
		gridBagConstraints_14.gridx = 0;
		diffTypePanel.add(mobileFluorescenceLabel, gridBagConstraints_14);

		mobileTotalFracTextField = new JTextField();
		mobileTotalFracTextField.setColumns(8);
		mobileTotalFracTextField.setEditable(false);
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.gridy = 5;
		gridBagConstraints_15.gridx = 1;
		diffTypePanel.add(mobileTotalFracTextField, gridBagConstraints_15);

		final JLabel ctotTotalLabel = new JLabel();
		ctotTotalLabel.setText("   (Cm_tot = C_tot * Primay Mobile Fraction)");
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.anchor = GridBagConstraints.WEST;
		gridBagConstraints_16.gridwidth = 3;
		gridBagConstraints_16.gridy = 5;
		gridBagConstraints_16.gridx = 2;
		diffTypePanel.add(ctotTotalLabel, gridBagConstraints_16);
		
		final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.gridx = 0;
		add(buttonPanel, gridBagConstraints2);

		final JLabel konUnitLabel = new JLabel();
		konUnitLabel.setText("1/(uM.s)");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 5;
		buttonPanel.add(konUnitLabel, gridBagConstraints_6);

		final JButton helpDiffRateButton = new JButton();
		helpDiffRateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				estimateDiffRate();
			}
		});
		helpDiffRateButton.setMargin(new Insets(0, 14, 0, 14));
		helpDiffRateButton.setText("Help On Decision");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.gridy = 2;
		gridBagConstraints_9.gridx = 9;
		buttonPanel.add(helpDiffRateButton, gridBagConstraints_9);
		
		final GridBagConstraints gridBagConstraints_estButton = new GridBagConstraints();
		gridBagConstraints_estButton.gridwidth = 12;
		gridBagConstraints_estButton.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_estButton.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_estButton.gridy = 5;
		gridBagConstraints_estButton.gridx = 0;
		buttonPanel.add(estButton, gridBagConstraints_estButton);
		estButton.setMargin(new Insets(0, 14, 0, 14));
		estButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				autoEstimateReactionBindingParameters();
			}
		});
		
		final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints3.ipadx = 15;
		gridBagConstraints3.ipady = 100;
		gridBagConstraints3.gridheight = 5;
		gridBagConstraints3.gridy = 6;
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		add(tableScroll, gridBagConstraints3);
		
	}

	public void estimateDiffRate()
	{
		if(diffRateEstPanel == null)
		{
			diffRateEstPanel = new DiffRateEstimationPanel();
		}
		int choice = DialogUtils.showComponentOKCancelDialog(FRAPReacDiffEstimationGuidePanel.this, diffRateEstPanel, "Help on estimate diffusion rate");
		if (choice == JOptionPane.OK_OPTION)
		{
			if(diffRateEstPanel.getDiffRate()!= null)
			{
				freeDiffTextField.setText(diffRateEstPanel.getDiffRate());
				freeDiffTextField.setCaretPosition(0);
			}
		}
	}
	
	public void estimateOnRate()
	{
		if(onRateEstPanel == null)
		{
			onRateEstPanel = new DiffOnRateEstimationPanel();
		}
		try{
			Double freeDiffRate = new Double(freeDiffTextField.getText());
			onRateEstPanel.setFreeDiffRate(freeDiffRate);
			int choice = DialogUtils.showComponentOKCancelDialog(FRAPReacDiffEstimationGuidePanel.this, onRateEstPanel, "Help on estimate reaction on rate");
			if (choice == JOptionPane.OK_OPTION)
			{
				if(onRateEstPanel.getOnRate()!= null)
				{
					konTextField.setText(onRateEstPanel.getOnRate().toString());
					konTextField.setCaretPosition(0);
				}
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Free diffusion rate is empty or in illegal form." + e.getMessage());
		}
	}
	
	public void autoEstimateReactionBindingParameters()
	{
		try{
			//get free diffusion rate
			double df = Double.parseDouble(freeDiffTextField.getText());
			if(effDiffRate >= df )
			{
				DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Free diffusion rate should be greater than effective diffusion rate." );
				return;
			}
			if(konButton.isSelected()) // parameters have been set
			{	
				if(konTextField.getText() != null && freeDiffTextField.getText() != null) 
				{
					
					double kon = Double.parseDouble(konTextField.getText());
					double koff = (kon*effDiffRate)/(df-effDiffRate);
					double cf = (totMobileFraction*koff)/(kon+koff);
					double cc = (totMobileFraction*kon)/(kon+koff);
	
					try{
						updateTableParameters(df, cf, cc, kon, koff, bwmRate, immFraction, true);
					}catch(ExpressionException ee)
					{
						ee.printStackTrace(System.out);
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Error creating expression when trying to update table: " + ee.getMessage());
					}
					koffTextField.setText(koff + "");
					koffTextField.setCaretPosition(0);
				}
			}
			else if(koffButton.isSelected())
			{
				if(konTextField.getText() != null && freeDiffTextField.getText() != null)
				{
					double koff = Double.parseDouble(koffTextField.getText());
					double kon = ((df-effDiffRate)*koff)/(effDiffRate);
					double cf = (totMobileFraction*koff)/(kon+koff);
					double cc = (totMobileFraction*kon)/(kon+koff);
	
					try{
						updateTableParameters(df, cf, cc, kon, koff, bwmRate, immFraction, false);
					}catch(ExpressionException ee)
					{
						ee.printStackTrace(System.out);
						DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Error creating expression when trying to update table: " + ee.getMessage());
					}
					konTextField.setText(kon + "");
					konTextField.setCaretPosition(0);
				}
			}
		}catch(NumberFormatException ex)
		{
			ex.printStackTrace(System.out);
			DialogUtils.showErrorDialog(FRAPReacDiffEstimationGuidePanel.this, "Error in estimation parameters: numbers in textfield are empty or in illeagal forms. " + ex.getMessage());
		}
	}
	
	public void updateTableParameters(double d_f,  double C_f, double C_c, double K_on, double K_off, double bwmRate, double immConc, boolean isOnRateKnown) throws ExpressionException
	{
	
		//clear estimatedParameters list for table
		for (int i=0; i<estimatedParameters.length; i++)
		{
			estimatedParameters[i]=null;
		}
		
		estimatedParameters[IDX_FreePartDiffRate] = new EstimatedParameter(paramNames[IDX_FreePartDiffRate], paramDescriptions[IDX_FreePartDiffRate], null, new Double(d_f), frapUnitsystem.getDiffusionRateUnit());
		estimatedParameters[IDX_FreePartConc]	= new EstimatedParameter(paramNames[IDX_FreePartConc], paramDescriptions[IDX_FreePartConc], new Expression(paramExpStr[IDX_FreePartConc]), new Double(C_f), frapUnitsystem.getInstance_DIMENSIONLESS());
		estimatedParameters[IDX_ComplexConc] = new EstimatedParameter(paramNames[IDX_ComplexConc], paramDescriptions[IDX_ComplexConc], new Expression(paramExpStr[IDX_ComplexConc]), new Double(C_c), frapUnitsystem.getInstance_DIMENSIONLESS());
		if(isOnRateKnown)
		{
			estimatedParameters[IDX_ReacPseudoOnRate] = new EstimatedParameter(paramNames[IDX_ReacPseudoOnRate], paramDescriptions[IDX_ReacPseudoOnRate], null, new Double(K_on), frapUnitsystem.getReactionOffRateUnit());		// using reactionOffRate for 'PseudoOnRate' unit, since both are in '/s' 
			estimatedParameters[IDX_ReacOffRate]	= new EstimatedParameter(paramNames[IDX_ReacOffRate], paramDescriptions[IDX_ReacOffRate], new Expression(paramExpStr[IDX_ReacOffRate]), new Double(K_off), frapUnitsystem.getReactionOffRateUnit());
		}
		else
		{
			estimatedParameters[IDX_ReacPseudoOnRate] = new EstimatedParameter(paramNames[IDX_ReacPseudoOnRate], paramDescriptions[IDX_ReacPseudoOnRate], new Expression(paramExpStr[IDX_ReacPseudoOnRate]), new Double(K_on), frapUnitsystem.getReactionOffRateUnit()); // using reactionOffRate for 'PseudoOnRate' unit, since both are in '
			estimatedParameters[IDX_ReacOffRate]	= new EstimatedParameter(paramNames[IDX_ReacOffRate], paramDescriptions[IDX_ReacOffRate], null, new Double(K_off), frapUnitsystem.getReactionOffRateUnit());
		}
		estimatedParameters[IDX_BWMRate] = new EstimatedParameter(paramNames[IDX_BWMRate], paramDescriptions[IDX_BWMRate], null, new Double(bwmRate), frapUnitsystem.getBleachingWhileMonitoringRateUnit());
		estimatedParameters[IDX_Immobile] = new EstimatedParameter(paramNames[IDX_Immobile], paramDescriptions[IDX_Immobile], null, new Double(immConc), frapUnitsystem.getInstance_DIMENSIONLESS());
		
		paramTableModel.setEstimatedParameters(estimatedParameters);
		
	}
	
	public JTable getParamTable()
	{
		return paramTable;
	}
	public JPanel getDiffTypePanel()
	{
		return diffTypePanel;
	}

	public void setPrimaryParameters(Parameter[] params)
	{
		effDiffRate = params[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess();
		totMobileFraction = params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess();
		bwmRate = params[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess();
		immFraction = 1-totMobileFraction;
		diffEffectiveTextField.setText(params[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + "");
		diffEffectiveTextField.setCaretPosition(0);
		mobileTotalFracTextField.setText(params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() + "");
		mobileTotalFracTextField.setCaretPosition(0);
	}

	public void updateUIForReacDiffusion() 
	{
		koffButton.setEnabled(true);
		konButton.setEnabled(true);
		if(konButton.isSelected())
		{
			konTextField.setEditable(true);
			koffTextField.setEditable(false);
		}
		else
		{
			konTextField.setEditable(false);
			koffTextField.setEditable(true);
		}
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			FRAPReacDiffEstimationGuidePanel aPanel = new FRAPReacDiffEstimationGuidePanel();
			frame.setContentPane(aPanel);
//			frame.pack();
			frame.setSize(900,800);
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
