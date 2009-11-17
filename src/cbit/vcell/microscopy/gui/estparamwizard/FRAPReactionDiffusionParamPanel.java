package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vcell.util.Compare;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.task.RunSims;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.FRAPStudyPanel.FrapChangeInfo;
import cbit.vcell.microscopy.gui.FRAPStudyPanel.SavedFrapModelInfo;
import cbit.vcell.opt.Parameter;

public class FRAPReactionDiffusionParamPanel extends JPanel{

	private JTextField complexFractionTextField;
	private JTextField offRateTextField;
	private final JTextField onRateTextField;
	private final JTextField freeDiffRateTextField;
	private final JTextField freeFractionTextField;
	private final JTextField bleachWhileMonitorRateTextField;
	private final JLabel immobileValueLabel;
	
	private FRAPOptData frapOptData;

	public static String STR_FREE_DIFF_RATE = "Free particle diffusion rate";
	public static String STR_FREE_FRACTION = "Free particle fraction";
	public static String STR_BLEACH_MONITOR_RATE = "Bleach while monitoring rate";
	public static String STR_COMPLEX_DIFF_RATE = "Complex diffusion rate";
	public static String STR_COMPLEX_FRACTION = "Complex fraction";
	public static String STR_IMMOBILE_FRACTION = "Immobile fraction";
	public static String STR_BINDING_SITE_CONCENTRATION = "Binding site concentration";
	public static String STR_ON_RATE = "Reaction on rate";
	public static String STR_OFF_RATE = "Reaction off rate";

	private JButton runSimbutton = null;
	private JButton estFromDiffParamButton = null; 
	public FRAPReactionDiffusionParamPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {7,7,7,7,7,7,7,0,7,7,0,0,7,7};
		gridBagLayout.rowHeights = new int[] {7,7,7,7,0,7,0,0,7};
		setLayout(gridBagLayout);
		runSimbutton = new JButton();
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.anchor = GridBagConstraints.EAST;
		gridBagConstraints_13.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_13.gridy = 1;
		gridBagConstraints_13.gridx = 0;
		add(runSimbutton, gridBagConstraints_13);
		runSimbutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(checkParameters())
				{
					firePropertyChange(FRAPWorkspace.PROPERTY_CHANGE_RUN_BINDING_SIMULATION, null,null);
				}
			}
		});
		runSimbutton.setText("Simulate with Params");
		runSimbutton.setToolTipText("Create new FRAP simulation using current parameter settings");

		estFromDiffParamButton = new JButton();
		estFromDiffParamButton.setText("Help on Setting Params");
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.anchor = GridBagConstraints.WEST;
		gridBagConstraints_20.gridwidth = 5;
		gridBagConstraints_20.gridy = 1;
		gridBagConstraints_20.gridx = 1;
		add(estFromDiffParamButton, gridBagConstraints_20);
		estFromDiffParamButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				firePropertyChange(FRAPWorkspace.PROPERTY_CHANGE_EST_BINDING_PARAMETERS, null, null);				
			}
		});

		final JLabel freeDiffRateLabel = new JLabel();
		freeDiffRateLabel.setText("Free  Diffusion  Rate(um2/s):");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 3;
		gridBagConstraints_9.gridx = 0;

		final JButton parameterScanButton = new JButton();
		parameterScanButton.setEnabled(false);
		parameterScanButton.setText("Parameter Scan on Current param set");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		gridBagConstraints_1.gridwidth = 2;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 10;
		add(parameterScanButton, gridBagConstraints_1);
		add(freeDiffRateLabel, gridBagConstraints_9);

		freeDiffRateTextField = new JTextField();
		freeDiffRateTextField.setPreferredSize(new Dimension(125, 20));
		freeDiffRateTextField.setMinimumSize(new Dimension(125, 20));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridx = 1;
		add(freeDiffRateTextField, gridBagConstraints);

		final JLabel freeMobileFractionLabel = new JLabel();
		freeMobileFractionLabel.setText("Free Particle fraction:");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_11.anchor = GridBagConstraints.EAST;
		gridBagConstraints_11.gridy = 6;
		gridBagConstraints_11.gridx = 0;
		add(freeMobileFractionLabel, gridBagConstraints_11);

		freeFractionTextField = new JTextField();
		freeFractionTextField.setPreferredSize(new Dimension(125, 20));
		freeFractionTextField.setMinimumSize(new Dimension(125, 20));
		freeFractionTextField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					double freeFraction = Double.parseDouble(freeFractionTextField.getText());
					double complexFraction = Double.parseDouble(complexFractionTextField.getText());
					updateFractions(true, freeFraction, complexFraction);
				}catch(NumberFormatException ex)
				{
					immobileValueLabel.setText("");
					DialogUtils.showErrorDialog(FRAPReactionDiffusionParamPanel.this, "Free particle/complex fraction is empty or in illegal form, please correct it.");
				}
				
			}
		});
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 6;
		gridBagConstraints_2.gridx = 1;
		add(freeFractionTextField, gridBagConstraints_2);

		final JLabel complexFractionLabel = new JLabel();
		complexFractionLabel.setText("Complex fraction :");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.EAST;
		gridBagConstraints_5.gridy = 6;
		gridBagConstraints_5.gridx = 5;
		add(complexFractionLabel, gridBagConstraints_5);

		complexFractionTextField = new JTextField();
		complexFractionTextField.setPreferredSize(new Dimension(125, 20));
		complexFractionTextField.setMinimumSize(new Dimension(125, 20));
		complexFractionTextField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try{
					double freeFraction = Double.parseDouble(freeFractionTextField.getText());
					double complexFraction = Double.parseDouble(complexFractionTextField.getText());
					updateFractions(false, freeFraction, complexFraction);
				}catch(NumberFormatException ex)
				{
					immobileValueLabel.setText("");
					DialogUtils.showErrorDialog(FRAPReactionDiffusionParamPanel.this, "Free particle/complex fraction is empty or in illegal form, please correct it.");
				}
			}
		});
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.gridy = 6;
		gridBagConstraints_6.gridx = 6;
		add(complexFractionTextField, gridBagConstraints_6);

		final JLabel onRateLabel = new JLabel();
		onRateLabel.setText("Psedo Reaction on Rate(1/s):");
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.anchor = GridBagConstraints.EAST;
		gridBagConstraints_15.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_15.gridy = 6;
		gridBagConstraints_15.gridx = 10;
		add(onRateLabel, gridBagConstraints_15);

		onRateTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.gridy = 6;
		gridBagConstraints_18.gridx = 11;
		add(onRateTextField, gridBagConstraints_18);
		
		final JLabel bleachWhileMonitorLabel = new JLabel();
		bleachWhileMonitorLabel.setText("Bleach While Monitor Rate(1/s): ");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(2, 2, 0, 2);
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 9;
		gridBagConstraints_12.gridx = 0;
		add(bleachWhileMonitorLabel, gridBagConstraints_12);

		bleachWhileMonitorRateTextField = new JTextField();
		bleachWhileMonitorRateTextField.setPreferredSize(new Dimension(125, 20));
		bleachWhileMonitorRateTextField.setMinimumSize(new Dimension(125, 20));
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(2, 2, 0, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 9;
		gridBagConstraints_3.gridx = 1;
		add(bleachWhileMonitorRateTextField, gridBagConstraints_3);

		final JLabel immobileLabel = new JLabel();
		immobileLabel.setText("Immobile Fraction:");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_8.anchor = GridBagConstraints.EAST;
		gridBagConstraints_8.gridy = 9;
		gridBagConstraints_8.gridx = 5;
		add(immobileLabel, gridBagConstraints_8);

		immobileValueLabel = new JLabel();
		immobileValueLabel.setText("");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_7.gridy = 9;
		gridBagConstraints_7.gridx = 6;
		add(immobileValueLabel, gridBagConstraints_7);

		final JLabel offRateLabel = new JLabel();
//		immboileFractionLabel.setFont(new Font("", Font.BOLD, 14));
		offRateLabel.setText("Reaction  off  Rate(1/s):");
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.anchor = GridBagConstraints.EAST;
		gridBagConstraints_16.insets = new Insets(2, 2, 0, 2);
		gridBagConstraints_16.gridy = 9;
		gridBagConstraints_16.gridx = 10;
		add(offRateLabel, gridBagConstraints_16);

		offRateTextField = new JTextField();
		offRateTextField.setPreferredSize(new Dimension(125, 20));
		offRateTextField.setMinimumSize(new Dimension(125, 20));
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.gridy = 9;
		gridBagConstraints_19.gridx = 11;
		add(offRateTextField, gridBagConstraints_19);
	}

	private void initialize(){
		try{
			Hashtable<Integer, JComponent> diffusionSliderLabelTable = new Hashtable<Integer, JComponent>();
			diffusionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound()+""));
			diffusionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound()+""));
			
			Hashtable<Integer, JComponent> secondDiffusionSliderLabelTable = new Hashtable<Integer, JComponent>();
			secondDiffusionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound()+""));
			secondDiffusionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound()+""));
			
			Hashtable<Integer, JComponent> mobileFractionSliderLabelTable = new Hashtable<Integer, JComponent>();
			mobileFractionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound()+""));
			mobileFractionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound()+""));
			
			Hashtable<Integer, JComponent> secondMobileFractionSliderLabelTable = new Hashtable<Integer, JComponent>();
			secondMobileFractionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound()+""));
			secondMobileFractionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound()+""));
			
			Hashtable<Integer, JComponent> bleachWhileMonitorSliderLabelTable = new Hashtable<Integer, JComponent>();
			bleachWhileMonitorSliderLabelTable.put(new Integer(0), new JLabel(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()+""));
			bleachWhileMonitorSliderLabelTable.put(new Integer(20), new JLabel("1e-4"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(40), new JLabel("1e-3"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(60), new JLabel("1e-2"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(80), new JLabel("1e-1"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(100),new JLabel(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()+""));
		}finally{
	
		}
	}
	
	private boolean checkParameters()
	{
		Parameter[] params = getCurrentParameters();
		if (params == null)
		{
			DialogUtils.showErrorDialog(this, "Some of the editable parameters are empty or in illegal forms!");
			throw new RuntimeException("Some of the editable parameters are empty or in illegal forms!");
		}
		if(params.length ==5)
		{
			double fastRate = params[frapOptData.TWODIFFRATES_FAST_DIFFUSION_RATE_INDEX].getInitialGuess();
			double fastMobileFrac = params[frapOptData.TWODIFFRATES_FAST_MOBILE_FRACTION_INDEX].getInitialGuess();
			double slowRate = params[frapOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX].getInitialGuess();
			double slowMobileFrac = params[frapOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX].getInitialGuess();
			String msg = "";
			if(slowRate > fastRate)
			{
				msg = msg + "* Secondary (slower) diffusion rate is already greater than the primary diffusion rate.\n";
			}
			else if((fastRate * 0.9) <= slowRate)
			{
				msg = msg + "* Secondary diffusion rate and primary diffusion rate have less then 10% difference, please consider estimating one(primary) diffusion rate only.\n";
			}
			if((fastMobileFrac + slowMobileFrac) > 1)
			{
				msg = msg + "* The sum up of primary mobile fraction, secondary mobile fraction and immobile fraction exceeds 1.\n";
			}
			if(!msg.equals(""))
			{
				msg = msg + "\nContinue?";
				String choice = DialogUtils.showWarningDialog(this, msg, new String[]{"Yes", "No"}, "Yes");
				if(choice.equals("No"))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public void init(FRAPOptData frapOptData) throws Exception{
		
		initialize();
		
	}

	private static Parameter[] createParameterArray(double freeDiffRate, double freeFraction, double monitorBleachRate, double complexDifffRate, double complexFraction, double bsConc, double onRate, double offRate)
	{
		Parameter[] params = null;
		
		Parameter primaryDiff = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE],
					                     FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
					                     FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
					                     FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(),
					                     freeDiffRate);
		Parameter primaryFrac = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION],
					                     FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
					                     FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
					                     FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(),
					                     freeFraction);
		Parameter bleachWhileMonitoringRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
					                     FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
					                     FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
					                     FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
					                     monitorBleachRate);
		Parameter secondaryDiff = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE], 
      			                         FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
				                         FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
				                         FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), 
				                         complexDifffRate);
		Parameter secondaryFrac = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION],
				   						 FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
                                         FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
                                         FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getScale(), 
                                         complexFraction);
		Parameter bsConcentration = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION],
                                         0,
                                         1,
                                         1, 
                                         bsConc);
		Parameter onReacRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_ON_RATE], 
                                         0,
                                         1e6,
                                         1, 
                                         onRate);
		Parameter offReacRate = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE], 
						                 0,
						                 1e6,
						                 1, 
						                 offRate);

		params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_BINDING];
		params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = primaryDiff;
		params[FRAPModel.INDEX_PRIMARY_FRACTION] = primaryFrac;
		params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = bleachWhileMonitoringRate;
		params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = secondaryDiff;
		params[FRAPModel.INDEX_SECONDARY_FRACTION] = secondaryFrac;
		params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION] = bsConcentration;
		params[FRAPModel.INDEX_ON_RATE] = onReacRate;
		params[FRAPModel.INDEX_OFF_RATE] = offReacRate;
		
		return params;
	}
	
	public Parameter[] getCurrentParameters(){
		double fr, ff, bwmr, cr, cf, imf, bs, on, off;
		try
		{
			fr = Double.parseDouble(freeDiffRateTextField.getText());
			ff = Double.parseDouble(freeFractionTextField.getText());
			bwmr = Double.parseDouble(bleachWhileMonitorRateTextField.getText());
			cr = 0; //complex diffusion is 0.
			cf = Double.parseDouble(complexFractionTextField.getText());
			bs = 1;//binding site concentration
			on = Double.parseDouble(onRateTextField.getText());
			off = Double.parseDouble(offRateTextField.getText());
			
		}catch(NumberFormatException e)
		{
			return null;
		}
		
		return
			createParameterArray(fr, ff, bwmr, cr, cf, bs, on, off);
	}
	
	public boolean isAllTextFieldEmpty()
	{
		if(freeDiffRateTextField != null && !freeDiffRateTextField.getText().equals(""))
		{
			return false;
		}
		if(freeFractionTextField != null && !freeFractionTextField.getText().equals(""))
		{
			return false;
		}
		if(bleachWhileMonitorRateTextField != null && !bleachWhileMonitorRateTextField.getText().equals(""))
		{
			return false;
		}
		if(complexFractionTextField != null && !complexFractionTextField.getText().equals(""))
		{
			return false;
		}
		if(onRateTextField != null && !onRateTextField.getText().equals(""))
		{
			return false;
		}
		if(offRateTextField != null && !offRateTextField.getText().equals(""))
		{
			return false;
		}
		return true;
	}


	public void updateFractions(boolean isSetFreeFraction, double freeFraction, double complexFraction)
	{
		if(isSetFreeFraction)
		{
			if((freeFraction + complexFraction) >= 1)
			{
				if(freeFraction < 1)
				{
					complexFractionTextField.setText((1-freeFraction)+"");
				}
				else
				{
					freeFractionTextField.setText(1+"");
					complexFractionTextField.setText(0+"");
				}
				immobileValueLabel.setText("0");
			}
			else
			{
				double immobileFrac = 1 - freeFraction - complexFraction;
				if(immobileFrac > (1-FRAPOptimization.epsilon) && immobileFrac < (1+FRAPOptimization.epsilon))
				{
					immobileValueLabel.setText("0");
				}
				else
				{
					immobileValueLabel.setText(immobileFrac+"");
				}
			}
		}
		else
		{
			if((complexFraction + freeFraction) >= 1)
			{
				if(complexFraction < 1)
				{
					freeFractionTextField.setText((1-complexFraction)+"");
				}
				else
				{
					complexFractionTextField.setText(1+"");
					freeFractionTextField.setText(0+"");
				}
				immobileValueLabel.setText("0");
			}
			else
			{
				double immobileFrac = 1 - freeFraction - complexFraction;
				if(immobileFrac > (1-FRAPOptimization.epsilon) && immobileFrac < (1+FRAPOptimization.epsilon))
				{
					immobileValueLabel.setText("0");
				}
				else
				{
					immobileValueLabel.setText(immobileFrac+"");
				}
			}
		}
	}

	public void calBSConcentration(double prebleachAvg) 
	{
		//normalized prebleachAvg should be 1.
		prebleachAvg = 1;
		if(freeFractionTextField == null || freeFractionTextField.getText().equals(""))
		{
			DialogUtils.showErrorDialog(this, "Free particle fraction is required to calculate Binding Site's concentration!");
			return;
		}
		if(complexFractionTextField == null || complexFractionTextField.getText().equals(""))
		{
			DialogUtils.showErrorDialog(this, "Complex fraction is required to calculate Binding Site's concentration!");
			return;
		}
		if(onRateTextField == null || onRateTextField.getText().equals(""))
		{
			DialogUtils.showErrorDialog(this, "Reaction on rate is required to calculate Binding Site's concentration!");
			return;
		}
		if(offRateTextField == null || offRateTextField.getText().equals(""))
		{
			DialogUtils.showErrorDialog(this, "Reaction off rate is required to calculate Binding Site's concentration!");
			return;
		}
		double freeFrac, complexFrac, fi, ci, kon, koff, BS_conc;
		try
		{
			freeFrac = Double.parseDouble(freeFractionTextField.getText());
			complexFrac = Double.parseDouble(complexFractionTextField.getText());
			fi = prebleachAvg * freeFrac;
			ci = prebleachAvg * complexFrac;
			kon = Double.parseDouble(onRateTextField.getText());
			koff = Double.parseDouble(offRateTextField.getText());
			if(kon == 0 || fi == 0)
			{
				DialogUtils.showErrorDialog(this, "Divided by 0 error! Kon and Free particle concentration should not be 0. !");
				return;
			}
			BS_conc = ((koff*ci)/(kon*fi))/prebleachAvg;
		}catch(Exception e)
		{
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "Required numbers are in illegal forms!");
			return;
		}
	}

	public void setFreeDiffRate(String freeDiffStr)
	{
		freeDiffRateTextField.setText(freeDiffStr);
	}
	public String getFreeFraction()
	{
		return freeFractionTextField.getText();
	}
	public void setFreeFraction(String freeFracStr)
	{
		freeFractionTextField.setText(freeFracStr);
	}
	public void setBleachMonitorRate(String bwmStr)
	{
		bleachWhileMonitorRateTextField.setText(bwmStr);
	}
	public String getComplexFraction()
	{
		return complexFractionTextField.getText();
	}
	public void setComplexFraction(String complexFracStr)
	{
		complexFractionTextField.setText(complexFracStr);
	}
	public void setImmobileFraction(String immFracStr)
	{
		immobileValueLabel.setText(immFracStr);
	}
	public void setOnRate(String onRateStr)
	{
		onRateTextField.setText(onRateStr);
	}
	public void setOffRate(String offRateStr)
	{
		offRateTextField.setText(offRateStr);
	}
	
	public FrapChangeInfo createCompleteFRAPChangeInfo(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo,
			boolean bCellROISame,boolean bBleachROISame,boolean bBackgroundROISame,boolean bROISameSize, String idxForRecovery)
	{
		return new FrapChangeInfo(
				!bCellROISame || !bBleachROISame || !bBackgroundROISame,
				!bROISameSize,
				isFreeDiffusionRateChanged(savedFrapModelInfo),
				getFreeDiffusionRateString(),
				isFreeMobileFractionChanged(savedFrapModelInfo),
				getFreeMobileFractionString(),
				false/*isComplexDiffusionRateChanged(savedFrapModelInfo)*/,
				""/*getComplexDiffusionRateString()*/,
				isComplexMobileFractionChanged(savedFrapModelInfo),
				getComplexMobileFractionString(),
				isMonitorBleachRateChanged(savedFrapModelInfo),
				getMonitorBleachRateString(),
				false/*isBSConcentrationChanged(savedFrapModelInfo)*/,
				""/*getBSConcentrationString()*/,
				isOnRateChanged(savedFrapModelInfo),
				getOnRateString(),
				isOffRateChanged(savedFrapModelInfo),
				getOffRateString(),
				isUserStartIndexForRecoveryChanged(savedFrapModelInfo, idxForRecovery),
				idxForRecovery);

	}
	
		
	public String getFullParamDescritpion()
	{
		String des = "";
		Parameter[] params = getCurrentParameters();
		if(params != null)
		{
			des = des + "Primary Diffusion Rate: " +params[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + ", Primary Fraction: " + params[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() +"\n";
			des = des + "Complex Diffusion Rate: " +params[FRAPModel.INDEX_SECONDARY_DIFF_RATE].getInitialGuess() + ", Complex Fraction: " + params[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess() +"\n";
			des = des + "Bleach While Monitoring Rate: " +params[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + ", Binding site concentration: " + params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess() +"\n";
			des = des + "Reaction On Rate: " +params[FRAPModel.INDEX_ON_RATE].getInitialGuess() + ", Reaction Off Rate: " + params[FRAPModel.INDEX_OFF_RATE].getInitialGuess() +".";
		}
		return des;
	}
	
	public void setParameters(Parameter[] displayParameters)
	{
		if(displayParameters != null)
		{
			freeDiffRateTextField.setText(displayParameters[FRAPModel.INDEX_PRIMARY_DIFF_RATE].getInitialGuess() + "");
			freeFractionTextField.setText(displayParameters[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess() + "");
			bleachWhileMonitorRateTextField.setText(displayParameters[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() + "");
			complexFractionTextField.setText(displayParameters[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess() + "");
			onRateTextField.setText(displayParameters[FRAPModel.INDEX_ON_RATE].getInitialGuess() + "");
			offRateTextField.setText(displayParameters[FRAPModel.INDEX_OFF_RATE].getInitialGuess() + "");
			updateFractions(true, displayParameters[FRAPModel.INDEX_PRIMARY_FRACTION].getInitialGuess(), displayParameters[FRAPModel.INDEX_SECONDARY_FRACTION].getInitialGuess());
		}
		else
		{
			freeDiffRateTextField.setText("");
			freeFractionTextField.setText("");
			bleachWhileMonitorRateTextField.setText("");
			
//			complexDiffRateTextField.setText("");
			complexFractionTextField.setText("");
			
//			bsConcentrationTextField.setText("");
			onRateTextField.setText("");
			offRateTextField.setText("");
			immobileValueLabel.setText("");
		}
		repaint();
	}
	
	private boolean isFreeDiffusionRateChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastFreeDiffusionrate), getFreeDiffusionRateString());
	}
	
	private boolean isFreeMobileFractionChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastFreeMobileFraction), getFreeMobileFractionString());
	}
	
	private boolean isComplexMobileFractionChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastComplexMobileFraction), getComplexMobileFractionString());
	}
	
	private boolean isMonitorBleachRateChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastBleachWhileMonitoringRate), getMonitorBleachRateString());
	}
	
	private boolean isOnRateChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.reactionOnRate), getOnRateString());
	}
	
	private boolean isOffRateChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.reactionOffRate), getOffRateString());
	}
	
	private boolean isUserStartIndexForRecoveryChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo, String userStartingIndexForRecovery){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.startingIndexForRecovery),userStartingIndexForRecovery);
	}
	
	private String getFreeDiffusionRateString(){
		return
			(freeDiffRateTextField.getText() == null || freeDiffRateTextField.getText().length() == 0
				?null:freeDiffRateTextField.getText());
	}
	
	private String getFreeMobileFractionString(){
		return
			(freeFractionTextField.getText() == null || freeFractionTextField.getText().length() == 0
				?null:freeFractionTextField.getText());
	}
	
	private String getComplexMobileFractionString(){
		return
			(complexFractionTextField.getText() == null || complexFractionTextField.getText().length() == 0
				?null:complexFractionTextField.getText());
	}
	
	private String getMonitorBleachRateString(){
		return
			(bleachWhileMonitorRateTextField.getText() == null || bleachWhileMonitorRateTextField.getText().length() == 0
				?null:bleachWhileMonitorRateTextField.getText());
	}
	
	private String getOnRateString(){
		return
			(onRateTextField.getText() == null || onRateTextField.getText().length() == 0
				?null:onRateTextField.getText());
	}
	private String getOffRateString(){
		return
			(offRateTextField.getText() == null || offRateTextField.getText().length() == 0
				?null:offRateTextField.getText());
	}
	

}
