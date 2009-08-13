package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.opt.Parameter;

public class FRAPDiffOneParamPanel extends JPanel
{
	private final JSlider secondMobileFracSlider = null;
	private final JLabel immoFracValueLabel;
	private final JSlider diffusionRateSlider;
	private final JSlider mobileFractionSlider;
	private final JSlider bleachWhileMonitorSlider;
	private final JTextField diffusionRateTextField;
	
	private final JTextField mobileFractionTextField;
	private final JTextField bleachWhileMonitorRateTextField;
	private final JButton diffusionRateSetButton;
	private final JButton mobileFractionSetButton;
	private final JButton bleachWhileMonitorSetButton;
	
	private FRAPOptData frapOptData;

	private boolean B_HOLD_FIRE = false;
	private boolean isExecuting = false;//for control whether a paragraph should execute in OPTIMIZER_SLIDER_CHANGE_LISTENER or not, when getValueIsAdjusting() is false.
	public static final String PROPERTY_CHANGE_OPTIMIZER_VALUE = "PROPERTY_CHANGE_OPTIMIZER_VALUE";
	public static final String PROPERTY_CHANGE_RUNSIM = "PROPERTY_CHANGE_RUNSIM";
	public static final String PROPERTY_CHANGE_TO_REACTION_DIFFUTION = "PROPERTY_CHANGE_TO_REACTION_DIFFUTION";
	public static final String INI_SECOND_DIFF_RATE = "0";
	public static final String INI_SECOND_MOBILE_FRAC = "0";
	
	private final ActionListener OPTIMIZER_VALUE_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(e.getSource() == diffusionRateTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(diffusionRateSetButton,e.getID(),diffusionRateSetButton.getActionCommand()));
				}else if(e.getSource() == mobileFractionTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(mobileFractionSetButton,e.getID(),mobileFractionSetButton.getActionCommand()));
				}else if(e.getSource() == bleachWhileMonitorRateTextField){
					SET_ACTION_LISTENER.actionPerformed(new ActionEvent(bleachWhileMonitorSetButton,e.getID(),bleachWhileMonitorSetButton.getActionCommand()));
				}
			}
		};
		
	private final ChangeListener OPTIMIZER_SLIDER_CHANGE_LISTENER =
		new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				boolean isSetPrimaryMFrac = true;
				if(e.getSource() == diffusionRateSlider){
					double value =
						FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound()+
						(FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound()-FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound())*
						((double)diffusionRateSlider.getValue()/(double)diffusionRateSlider.getMaximum());
					diffusionRateTextField.setText(value+"");
					diffusionRateSetButton.setEnabled(false);
				}else if(e.getSource() == mobileFractionSlider){
					double value =
						FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound()+
						(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound()-FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound())*
						((double)mobileFractionSlider.getValue()/(double)mobileFractionSlider.getMaximum());
					mobileFractionTextField.setText(value+"");
					mobileFractionSetButton.setEnabled(false);
				}else if(e.getSource() == bleachWhileMonitorSlider){
					double value = FRAPOptData.REF_BWM_LOG_VAL_MIN + (FRAPOptData.REF_BWM_LOG_VAL_MAX - FRAPOptData.REF_BWM_LOG_VAL_MIN)* 
					               ((double)bleachWhileMonitorSlider.getValue()/(double)bleachWhileMonitorSlider.getMaximum());
					double realVal = Math.pow(10,value);
					if(realVal > (Math.pow(10, FRAPOptData.REF_BWM_LOG_VAL_MIN)-FRAPOptimization.epsilon) && realVal <(Math.pow(10, FRAPOptData.REF_BWM_LOG_VAL_MIN)+FRAPOptimization.epsilon))
					{
						realVal = 0;
					}
					bleachWhileMonitorRateTextField.setText(realVal+"");
					bleachWhileMonitorSetButton.setEnabled(false);
				}
							
				if(!((JSlider)e.getSource()).getValueIsAdjusting()){
					if(!isExecuting)
					{
						isExecuting = true;
						try{
							double primaryFrac = Double.parseDouble(mobileFractionTextField.getText());
							double immFrac = Double.parseDouble(immoFracValueLabel.getText());
							
							double[] adjustedVals = adjustMobileFractions(primaryFrac, isSetPrimaryMFrac);
							//primary				
							double value = adjustedVals[0];
							mobileFractionSetButton.setEnabled(false);
							int sliderValue = (int)
								(((value-FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound())*(double)mobileFractionSlider.getMaximum())/
								(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound()-FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound()));
							if(sliderValue < mobileFractionSlider.getMinimum()){
								sliderValue = mobileFractionSlider.getMinimum();
							}
							if(sliderValue > mobileFractionSlider.getMaximum()){
								sliderValue = mobileFractionSlider.getMaximum();
							}
							mobileFractionSlider.setValue(sliderValue);
							mobileFractionTextField.setText(value+"");
							//secondary
							value = adjustedVals[1];
							sliderValue = (int)
								(((value-FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound())*(double)secondMobileFracSlider.getMaximum())/
								(FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound()-FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound()));
							if(sliderValue < secondMobileFracSlider.getMinimum()){
								sliderValue = secondMobileFracSlider.getMinimum();
							}
							if(sliderValue > secondMobileFracSlider.getMaximum()){
								sliderValue = secondMobileFracSlider.getMaximum();
							}
							secondMobileFracSlider.setValue(sliderValue);
							
							//immobile
							immoFracValueLabel.setText(adjustedVals[2]+"");
						}finally{
							isExecuting = false;
						}
					}
					
					firePropertyChange(PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
				}
			}
		};
		
	private ActionListener SET_ACTION_LISTENER =
		new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try{
					diffusionRateSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					mobileFractionSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					bleachWhileMonitorSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					boolean isSetPrimaryMFrac = true;
					
					//get mobile fractions
					double primaryMFrac = Double.parseDouble(mobileFractionTextField.getText());
					double immMFrac = Double.parseDouble(immoFracValueLabel.getText());
					double[] adjustedVals = adjustMobileFractions(primaryMFrac, isSetPrimaryMFrac);
					
//					if(e.getSource() == diffusionRateSetButton){
						double value = Double.parseDouble(diffusionRateTextField.getText());
						if(value < FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound()){
							value = FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound();
						}
						if(value > FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound()){
							value = FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound();
						}
						diffusionRateTextField.setText(value+"");
						int sliderValue = (int)
							(((value-FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound())*(double)diffusionRateSlider.getMaximum())/
							(FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound()-FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound()));
						if(sliderValue < diffusionRateSlider.getMinimum()){
							sliderValue = diffusionRateSlider.getMinimum();
						}
						if(sliderValue > diffusionRateSlider.getMaximum()){
							sliderValue = diffusionRateSlider.getMaximum();
						}
						diffusionRateSlider.setValue(sliderValue);
						diffusionRateSetButton.setEnabled(false);
//					}else if(e.getSource() == mobileFractionSetButton){
						value = adjustedVals[0];
						mobileFractionTextField.setText(value+"");
						sliderValue = (int)
							(((value-FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound())*(double)mobileFractionSlider.getMaximum())/
							(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound()-FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound()));
						if(sliderValue < mobileFractionSlider.getMinimum()){
							sliderValue = mobileFractionSlider.getMinimum();
						}
						if(sliderValue > mobileFractionSlider.getMaximum()){
							sliderValue = mobileFractionSlider.getMaximum();
						}
						mobileFractionSlider.setValue(sliderValue);
						mobileFractionSetButton.setEnabled(false);
//					}else if(e.getSource() == bleachWhileMonitorSetButton){
						/*double*/ value = Double.parseDouble(bleachWhileMonitorRateTextField.getText());
						
						if(value <= (Math.pow(10, FRAPOptData.REF_BWM_LOG_VAL_MIN)+FRAPOptimization.epsilon)){
							value = FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
						}
						if(value > FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()){
							value = FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound();
						}
						bleachWhileMonitorRateTextField.setText(value+"");
						if(value == FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())
						{
							sliderValue = 0;
						}
						else
						{
							double tempVal = Math.log10(value);
							sliderValue = (int)
								(((tempVal-FRAPOptData.REF_BWM_LOG_VAL_MIN)*(double)bleachWhileMonitorSlider.getMaximum())/
								(FRAPOptData.REF_BWM_LOG_VAL_MAX-FRAPOptData.REF_BWM_LOG_VAL_MIN));
							if(sliderValue < bleachWhileMonitorSlider.getMinimum()){
								sliderValue = bleachWhileMonitorSlider.getMinimum();
							}
							if(sliderValue > bleachWhileMonitorSlider.getMaximum()){
								sliderValue = bleachWhileMonitorSlider.getMaximum();
							}
						}
						bleachWhileMonitorSlider.setValue(sliderValue);
						bleachWhileMonitorSetButton.setEnabled(false);
						//set immobile label
						value = adjustedVals[2];
						immoFracValueLabel.setText(value+"");
//					}
					if(!B_HOLD_FIRE){
						firePropertyChange(PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
					}
				}catch (Exception e2){
					e2.printStackTrace();
					DialogUtils.showErrorDialog(FRAPDiffOneParamPanel.this, "Error setting parameter value for "+
							(e.getSource() == diffusionRateSetButton?"diffusionRate":"")+
							(e.getSource() == mobileFractionSetButton?"mobileFraction":"")+
							(e.getSource() == bleachWhileMonitorSetButton?"bleachWhileMonitor":"")+
							"\n"+e2.getMessage());
				}finally{
					diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
					bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
				}
			}
		};
	private UndoableEditListener EDIT_LISTENER =
		new UndoableEditListener() {
			public void undoableEditHappened(final UndoableEditEvent e) {
				if(e.getSource() == diffusionRateTextField.getDocument()){
					diffusionRateSetButton.setEnabled(true);
				}else if(e.getSource() == mobileFractionTextField.getDocument()){
					mobileFractionSetButton.setEnabled(true);
				}else if(e.getSource() == bleachWhileMonitorRateTextField.getDocument()){
					bleachWhileMonitorSetButton.setEnabled(true);
				}
			}
		};
	//array inFractions stores 0:primary mobile fraction 1:secondary M F. 2:immobile fraction
	private double[] adjustMobileFractions(double primaryMF, boolean movingPrimaryMFrac)
	{
		double primaryMFrac = primaryMF;
		double secMFrac = 0;
		//constrain the upper and lower bound for primary and secondary mobile fractions
		primaryMFrac = Math.max(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(), primaryMFrac);
		primaryMFrac = Math.min(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(), primaryMFrac);
		secMFrac = Math.max(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(), secMFrac);
		secMFrac = Math.min(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(), secMFrac);
		double immFrac = 0;
		 //second diffusion is not applied
			
			immFrac = 1 - primaryMFrac;
			secMFrac = 0;
		
		return new double[]{primaryMFrac, secMFrac, immFrac};
	}
		
	public FRAPDiffOneParamPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {7,7,7,0};
		gridBagLayout.rowHeights = new int[] {7,7,7,7};
		setLayout(gridBagLayout);

		JPanel buttonPanel = new JPanel();
		final JButton createOptimalButton = new JButton();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.weightx = 1;
		gridBagConstraints_8.weighty = 1;
		gridBagConstraints_8.ipadx = -105;
		gridBagConstraints_8.anchor = GridBagConstraints.EAST;
		gridBagConstraints_8.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_8.fill = GridBagConstraints.BOTH;
		gridBagConstraints_8.gridy = 3;
		gridBagConstraints_8.gridx = 3;
		buttonPanel.add(createOptimalButton);
		add(buttonPanel, gridBagConstraints_8);
		createOptimalButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(checkParameters())
				{
//					final AsynchProgressPopup pp =
//						new AsynchProgressPopup(
//							FRAPPureDiffusionParamPanel.this,
//							"Finding Best Fit Parameters...",
//							"Working...",true,false);
//					pp.start();
//					new Thread(new Runnable(){public void run(){
					AsynchClientTask optTask = new AsynchClientTask("Running optimization ...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
					{
						public void run(Hashtable<String, Object> hashTable) throws Exception
						{
							try{
								frapOptData.setNumEstimatedParams(getCurrentParameters().length);
								final Parameter[] bestParameters = frapOptData.getBestParamters(getCurrentParameters(), FRAPDiffOneParamPanel.getErrorOfInterest());
								if(bestParameters.length == 3)
								{
									SwingUtilities.invokeLater(new Runnable(){public void run(){
										setParameterValues(
											new Double(bestParameters[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX].getInitialGuess()),
											new Double(bestParameters[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX].getInitialGuess()),
											null,
											null,
											new Double(bestParameters[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess())
											);
										firePropertyChange(PROPERTY_CHANGE_OPTIMIZER_VALUE, null,null);
									}});
								}
								else
								{
									SwingUtilities.invokeLater(new Runnable(){public void run(){ ////{diff, mobileFrac, secDiffRate, secMobileFrac, monitorRate}
										setParameterValues(
											new Double(bestParameters[FRAPOptData.TWODIFFRATES_FAST_DIFFUSION_RATE_INDEX].getInitialGuess()),
											new Double(bestParameters[FRAPOptData.TWODIFFRATES_FAST_MOBILE_FRACTION_INDEX].getInitialGuess()),
											new Double(bestParameters[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX].getInitialGuess()),
											new Double(bestParameters[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX].getInitialGuess()),
											new Double(bestParameters[FRAPOptData.TWODIFFRATES_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess()));
										firePropertyChange(PROPERTY_CHANGE_OPTIMIZER_VALUE, null,null);
									}});
								}
								
							}catch(final Exception e2){
	//							pp.stop();
								e2.printStackTrace();
								SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
									DialogUtils.showErrorDialog(FRAPDiffOneParamPanel.this, "Error setting Best Fit Parameters\n"+e2.getMessage());

								}});
							}finally{
	//							pp.stop();
							}
						}
					};
//					}}).start();
					//dispatch
					ClientTaskDispatcher.dispatch(FRAPDiffOneParamPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[]{optTask}, false);
				}
			}
		});
		createOptimalButton.setText("Get Best Fit Parameters");
		createOptimalButton.setToolTipText("Set best parameters through optimization with experimental data");
//		gridBagConstraints_13.gridwidth = 2;
//		gridBagConstraints_13.ipadx = 10;
//		gridBagConstraints_13.insets = new Insets(2, 2, 2, 0);
//		gridBagConstraints_13.gridy = 0;
//		gridBagConstraints_13.gridx = 1;
//		add(runSimbutton, gridBagConstraints_13);
////		runSimbutton.setFont(new Font("", Font.BOLD, 14));
//		runSimbutton.addActionListener(new ActionListener() {
//			public void actionPerformed(final ActionEvent e) {
//				if(checkParameters())
//				{
//					firePropertyChange(PROPERTY_CHANGE_RUNSIM, null,null);
//				}
//			}
//		});
//		runSimbutton.setText("Simulate with Parameters");
//		runSimbutton.setToolTipText("Create new FRAP simulation using current parameter settings");

		final JLabel diffusionRateLabel = new JLabel();
		diffusionRateLabel.setText("Primary  Diff.  Rate(um2/s):");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 0;
		gridBagConstraints_9.gridx = 0;
		add(diffusionRateLabel, gridBagConstraints_9);

		diffusionRateTextField = new JTextField();
		diffusionRateTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		diffusionRateTextField.setPreferredSize(new Dimension(125, 20));
		diffusionRateTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		diffusionRateTextField.setMinimumSize(new Dimension(125, 20));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		add(diffusionRateTextField, gridBagConstraints);

		diffusionRateSetButton = new JButton();
		diffusionRateSetButton.setMargin(new Insets(2, 1, 2, 1));
		diffusionRateSetButton.addActionListener(SET_ACTION_LISTENER);
		diffusionRateSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 2;
		add(diffusionRateSetButton, gridBagConstraints_1);

		diffusionRateSlider = new JSlider();
		diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		diffusionRateSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weighty = 1;
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.weightx = 1;
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 3;
		add(diffusionRateSlider, gridBagConstraints_4);

		final JLabel mobileFractionLabel = new JLabel();
		mobileFractionLabel.setText("Primary  Mobile Fraction:");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_11.anchor = GridBagConstraints.EAST;
		gridBagConstraints_11.gridy = 1;
		gridBagConstraints_11.gridx = 0;
		add(mobileFractionLabel, gridBagConstraints_11);

		mobileFractionTextField = new JTextField();
		mobileFractionTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		mobileFractionTextField.setPreferredSize(new Dimension(125, 20));
		mobileFractionTextField.setMinimumSize(new Dimension(125, 20));
		mobileFractionTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 1;
		add(mobileFractionTextField, gridBagConstraints_2);

		mobileFractionSetButton = new JButton();
		mobileFractionSetButton.addActionListener(SET_ACTION_LISTENER);
		mobileFractionSetButton.setMargin(new Insets(2, 1, 2, 1));
		mobileFractionSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_5.gridy = 1;
		gridBagConstraints_5.gridx = 2;
		add(mobileFractionSetButton, gridBagConstraints_5);

		mobileFractionSlider = new JSlider();
		mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		mobileFractionSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.weighty = 1;
		gridBagConstraints_6.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.weightx = 1;
		gridBagConstraints_6.gridy = 1;
		gridBagConstraints_6.gridx = 3;
		add(mobileFractionSlider, gridBagConstraints_6);

//		secondMobileFracSlider = new JSlider();
//		secondMobileFracSlider.setPreferredSize(new Dimension(0, 0));
//		secondMobileFracSlider.setEnabled(false);
//		secondMobileFracSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
//		secondMobileFracSlider.setPaintLabels(true);
//		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
//		gridBagConstraints_23.insets = new Insets(2, 2, 2, 2);
//		gridBagConstraints_23.weightx = 1;
//		gridBagConstraints_23.gridy = 2;
//		gridBagConstraints_23.gridx = 7;
//		add(secondMobileFracSlider, gridBagConstraints_23);

		final JLabel bleachWhileMonitorLabel = new JLabel();
//		bleachWhileMonitorLabel.setFont(new Font("", Font.BOLD, 14));
		bleachWhileMonitorLabel.setText("Bleach While Monitor Rate(1/s):");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 2;
		gridBagConstraints_12.gridx = 0;
		add(bleachWhileMonitorLabel, gridBagConstraints_12);

		bleachWhileMonitorRateTextField = new JTextField();
		bleachWhileMonitorRateTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		bleachWhileMonitorRateTextField.setPreferredSize(new Dimension(125, 20));
		bleachWhileMonitorRateTextField.setMinimumSize(new Dimension(125, 20));
		bleachWhileMonitorRateTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
//		bleachWhileMonitorRateTextField.setText("bleachWhileMonitorRate");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 1;
		add(bleachWhileMonitorRateTextField, gridBagConstraints_3);

		bleachWhileMonitorSetButton = new JButton();
		bleachWhileMonitorSetButton.addActionListener(SET_ACTION_LISTENER);
		bleachWhileMonitorSetButton.setMargin(new Insets(2, 1, 2, 1));
		bleachWhileMonitorSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_10.gridy = 2;
		gridBagConstraints_10.gridx = 2;
		add(bleachWhileMonitorSetButton, gridBagConstraints_10);

		bleachWhileMonitorSlider = new JSlider();
		bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		bleachWhileMonitorSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_7.weightx = 1;
		gridBagConstraints_7.weighty = 1.5;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 3;
		add(bleachWhileMonitorSlider, gridBagConstraints_7);		

		final JLabel immboileFractionLabel = new JLabel();
//		immboileFractionLabel.setFont(new Font("", Font.BOLD, 14));
		immboileFractionLabel.setText("Model    Immobile    Fraction:");
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.anchor = GridBagConstraints.WEST;
		gridBagConstraints_16.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_16.gridy = 3;
		gridBagConstraints_16.gridx = 0;
		add(immboileFractionLabel, gridBagConstraints_16);

		immoFracValueLabel = new JLabel();
		immoFracValueLabel.setIconTextGap(0);
		immoFracValueLabel.setText("       ");
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.anchor = GridBagConstraints.WEST;
		gridBagConstraints_19.gridy = 3;
		gridBagConstraints_19.gridx = 1;
		add(immoFracValueLabel, gridBagConstraints_19);
		
		initialize();
	}

	private void initialize(){
		
		diffusionRateSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		mobileFractionSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
//		secondMobileFracSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		bleachWhileMonitorSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		try{
			Hashtable<Integer, JComponent> diffusionSliderLabelTable = new Hashtable<Integer, JComponent>();
			diffusionRateSlider.setMinimum(0);
			diffusionRateSlider.setMaximum(100);
			diffusionRateSlider.setValue(50);
			diffusionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound()+""));
			diffusionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound()+""));
			diffusionRateSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			diffusionRateSlider.setLabelTable(diffusionSliderLabelTable);
			
			Hashtable<Integer, JComponent> secondDiffusionSliderLabelTable = new Hashtable<Integer, JComponent>();
			secondDiffusionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound()+""));
			secondDiffusionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound()+""));
			
			Hashtable<Integer, JComponent> mobileFractionSliderLabelTable = new Hashtable<Integer, JComponent>();
			mobileFractionSlider.setMinimum(0);
			mobileFractionSlider.setMaximum(100);
			mobileFractionSlider.setValue(50);
			mobileFractionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound()+""));
			mobileFractionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound()+""));
			mobileFractionSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			mobileFractionSlider.setLabelTable(mobileFractionSliderLabelTable);
			
//			Hashtable<Integer, JComponent> secondMobileFractionSliderLabelTable = new Hashtable<Integer, JComponent>();
//			secondMobileFracSlider.setMinimum(0);
//			secondMobileFracSlider.setMaximum(100);
//			secondMobileFracSlider.setValue(0);
//			secondMobileFractionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound()+""));
//			secondMobileFractionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound()+""));
//			secondMobileFracSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
//			secondMobileFracSlider.setLabelTable(secondMobileFractionSliderLabelTable);
			
			Hashtable<Integer, JComponent> bleachWhileMonitorSliderLabelTable = new Hashtable<Integer, JComponent>();
			bleachWhileMonitorSlider.setMinimum(0);
			bleachWhileMonitorSlider.setMaximum(100);
			bleachWhileMonitorSlider.setValue(0);
			bleachWhileMonitorSlider.setMajorTickSpacing(20);
			bleachWhileMonitorSlider.setPaintTicks(true);
			bleachWhileMonitorSliderLabelTable.put(new Integer(0), new JLabel(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()+""));
			bleachWhileMonitorSliderLabelTable.put(new Integer(20), new JLabel("1e-4"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(40), new JLabel("1e-3"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(60), new JLabel("1e-2"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(80), new JLabel("1e-1"));
			bleachWhileMonitorSliderLabelTable.put(new Integer(100),new JLabel(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()+""));
			bleachWhileMonitorSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			bleachWhileMonitorSlider.setLabelTable(bleachWhileMonitorSliderLabelTable);
		}finally{
			diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
//			secondMobileFracSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
	
		}
	}
	
	private boolean checkParameters()
	{
		Parameter[] params = getCurrentParameters();
		if (params == null)
		{
			DialogUtils.showErrorDialog(FRAPDiffOneParamPanel.this, "Some of the editable parameters are empty or in illegal forms!");
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
		
//		initialize();
		
		this.frapOptData = frapOptData;

		String secondRate = INI_SECOND_DIFF_RATE;
		String secondFraction = INI_SECOND_MOBILE_FRAC;
//		String secondRate = null;
//		String secondFraction = null;
		if(frapOptData.getExpFrapStudy().getFrapModelParameters().getPureDiffModelParameters() != null &&
		   frapOptData.getExpFrapStudy().getFrapModelParameters().getPureDiffModelParameters().arePureDiffParametesAllNotNull())
		{
			setParameterValues(
					new Double(frapOptData.getExpFrapStudy().getFrapModelParameters().getPureDiffModelParameters().primaryDiffusionRate),
					new Double(frapOptData.getExpFrapStudy().getFrapModelParameters().getPureDiffModelParameters().primaryMobileFraction),
					new Double(secondRate),
					new Double(secondFraction),
					new Double(frapOptData.getExpFrapStudy().getFrapModelParameters().getPureDiffModelParameters().monitorBleachRate));
		}
		else // pure diffusion params are not filled yet. take initial model parameters
		{
			setParameterValues(
					new Double(frapOptData.getExpFrapStudy().getFrapModelParameters().getIniModelParameters().diffusionRate),
					new Double(frapOptData.getExpFrapStudy().getFrapModelParameters().getIniModelParameters().mobileFraction),
					new Double(secondRate),
					new Double(secondFraction),
					new Double(frapOptData.getExpFrapStudy().getFrapModelParameters().getIniModelParameters().monitorBleachRate));
		}
	}
	//set text fields, update slider values and plot.
	private void setParameterValues(Double diffusionRate,Double mobileFraction,Double secondDiffRate,Double secondMobileFrac,Double monitorBleachRate){
		diffusionRateTextField.setText(diffusionRate.doubleValue()+"");
		mobileFractionTextField.setText((mobileFraction != null
				?mobileFraction.doubleValue()+""
				:"1.0"));
		bleachWhileMonitorRateTextField.setText((monitorBleachRate != null
				?monitorBleachRate.doubleValue()+""
				:"0"));
		String immFrac = "";
		if(mobileFraction != null)
		{
			if(secondMobileFrac != null)
			{
				immFrac = (1 - mobileFraction.doubleValue() - secondMobileFrac.doubleValue())+"";
			}
			else
			{
				immFrac = (1 - mobileFraction.doubleValue())+"";
			}
		}
		immoFracValueLabel.setText(immFrac);
		B_HOLD_FIRE = true;
		//one click is enough, The set action listener goes through the whole textfields and sliders
		diffusionRateSetButton.doClick();
//		mobileFractionSetButton.doClick();
//		bleachWhileMonitorSetButton.doClick();
//		secondDiffSetButton.doClick();
//		secondMobileFracSetButton.doClick();
		B_HOLD_FIRE = false;
	}
	private static Parameter[] createParameterArray(Double diffusionRate,Double mobileFraction,Double monitorBleachRate, Double secondDiffRate, Double secondMobileFrac)
	{
		Parameter[] params = null;
		
		if(secondDiffRate == null && secondMobileFrac == null)
		{
			Parameter diff =
				new cbit.vcell.opt.Parameter(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX],
						                     FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
						                     FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
						                     FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(),diffusionRate.doubleValue());
			Parameter mobileFrac =
				new cbit.vcell.opt.Parameter(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX],
						                     FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
						                     FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
						                     FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(),mobileFraction.doubleValue());
			Parameter bleachWhileMonitoringRate =
				new cbit.vcell.opt.Parameter(FRAPOptData.ONEDIFFRATE_PARAMETER_NAMES[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX],
						                     FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
						                     FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
						                     FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),monitorBleachRate.doubleValue());
			
			params = new Parameter[]{diff, mobileFrac, bleachWhileMonitoringRate};
		}
		else
		{
			Parameter diff = new Parameter(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_FAST_DIFFUSION_RATE_INDEX], 
					                       FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
					                       FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
					                       FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(), diffusionRate.doubleValue());
			Parameter mobileFrac = new Parameter(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_FAST_MOBILE_FRACTION_INDEX],
					                                                                      FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
					                                                                      FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
					                                                                      FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(), mobileFraction.doubleValue());
			Parameter secDiffRate = new Parameter(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX],
					                                                                       FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
					                                                                       FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
					                                                                       FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), secondDiffRate.doubleValue());
			Parameter secMobileFrac = new Parameter(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX],
					                                                                         FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
					                                                                         FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
					                                                                         FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getScale(), secondMobileFrac.doubleValue());
			Parameter monitorRate = new Parameter(FRAPOptData.TWODIFFRATES_PARAMETER_NAMES[FRAPOptData.TWODIFFRATES_BLEACH_WHILE_MONITOR_INDEX], 
					                                                                       FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
					                                                                       FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
					                                                                       FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), monitorBleachRate.doubleValue());
			
			params = new Parameter[]{diff, mobileFrac, monitorRate, secDiffRate, secMobileFrac};
		}
		return params;
	}
	public Parameter[] getCurrentParameters(){
		if(diffusionRateTextField == null || diffusionRateTextField.getText().equals("")||
		   mobileFractionTextField == null || mobileFractionTextField.getText().equals("")||
		   bleachWhileMonitorRateTextField == null || bleachWhileMonitorRateTextField.equals(""))
		{
			return null;
		}
		try
		{
			double dr = Double.parseDouble(diffusionRateTextField.getText());
			double mf = Double.parseDouble(mobileFractionTextField.getText());
			double bwmr = Double.parseDouble(bleachWhileMonitorRateTextField.getText());
		}catch(NumberFormatException e)
		{
			return null;
		}
		Double diffusionRate = new Double(diffusionRateTextField.getText());
		Double mobileFraction = new Double(mobileFractionTextField.getText());
		Double bleachWhileMonitorRate = new Double(bleachWhileMonitorRateTextField.getText());
		Double secondDiffRate = null;
		Double secondMobileFrac = null;
		return
			createParameterArray(diffusionRate, mobileFraction, bleachWhileMonitorRate, secondDiffRate, secondMobileFrac);
	}
	private double[][] getFitData(Parameter[] userParams) throws Exception{
		if(userParams == null || userParams.length <= 0)
		{
			return null;
		}
		frapOptData.setNumEstimatedParams(userParams.length);
		double[][] fitData = frapOptData.getFitData(userParams, FRAPDiffOneParamPanel.getErrorOfInterest()); 

		return fitData;
	}
	
	private static boolean[] getErrorOfInterest()
	{
		boolean[] errorOfInterest = new boolean[FRAPData.VFRAP_ROI_ENUM.values().length];
		
		for(int i=0; i<FRAPData.VFRAP_ROI_ENUM.values().length; i++)
		{
			if(FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED) || 
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1) ||
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2) ||
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3) ||
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4) ||
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5) ||
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6) ||
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7) ||
			   FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8))
			{
				errorOfInterest[i] = true;
			}
			else
			{
				errorOfInterest[i] = false;
			}
		}
		return errorOfInterest;
	}
	
	
	public double[][] getCurrentFitData() throws Exception{
		return getFitData(getCurrentParameters());
	}
	
	private void enableSecondDiffComponents()
	{
		mobileFractionSlider.setEnabled(true);
		mobileFractionTextField.setEnabled(true);
		secondMobileFracSlider.setEnabled(true);
	}
	
	private void disableSecondDiffComponents()
	{
		secondMobileFracSlider.setEnabled(false);
		isExecuting = true; //control not to execute the paragraph when sliderBar is not changing.
		isExecuting = false; 
		secondMobileFracSlider.setValue(Integer.parseInt(INI_SECOND_MOBILE_FRAC));
	}
	
	public void insertPureDiffusionParametersIntoFRAPStudy(FRAPStudy fStudy) throws Exception
	{
		if(fStudy != null)
		{ 
			if(getCurrentParameters()!=null)
			{
				try{
					String primaryDiffRateText = diffusionRateTextField.getText();
					if(primaryDiffRateText != null && primaryDiffRateText.length()>0){
						//check validity
						double diffusionRateDouble = Double.parseDouble(primaryDiffRateText);
						if(diffusionRateDouble < 0){
							throw new Exception("'Primary Diffusion Rate' must be >= 0.0");
						}
	
					}
				}catch(NumberFormatException e){
					throw new Exception("Error parsing 'Primary Diffusion Rate', "+e.getMessage());
				}
				try{
					String mobileFractionText = mobileFractionTextField.getText();
					if(mobileFractionText != null && mobileFractionText.length()>0){
						//check validity
						double mobileFractionDouble = Double.parseDouble(mobileFractionText);
						if(mobileFractionDouble < 0 || mobileFractionDouble > 1.0){
							throw new Exception("'Primary Mobile Fraction' must be between 0.0 and 1.0");
						}
	
					}
				}catch(NumberFormatException e){
					throw new Exception("Error parsing 'Primary Mobile Fraction', "+e.getMessage());
				}
				try{
					String monitorBleachRateText = bleachWhileMonitorRateTextField.getText();
					if(monitorBleachRateText != null && monitorBleachRateText.length()>0){
						//check validity
						double monitorBleadchRateDouble = Double.parseDouble(monitorBleachRateText);
						if(monitorBleadchRateDouble < 0){
							throw new Exception("'Bleach while monitoring rate' must be >= 0.0");
						}
					}
				}catch(NumberFormatException e){
					throw new Exception("Error parsing 'Bleach while monitoring rate', "+e.getMessage());
				}
				FRAPStudy.PureDiffusionModelParameters pureDiffParameters = null;
				if(fStudy.getFrapModelParameters() == null)
				{
					FRAPStudy.FRAPModelParameters modelParams = new FRAPStudy.FRAPModelParameters(null, pureDiffParameters, null);
					fStudy.setFrapModelParameters(modelParams);
				}
				else //modelParameters is not null
				{
					fStudy.getFrapModelParameters().setPureDiffModelParameters(pureDiffParameters);
				}
			}//all textfields are filled in legal forms
		}
	}
	
	public void updateSavedParameters(FRAPStudy.PureDiffusionModelParameters savedPureDiffParameters)
	{
		double immFraction;
		if(savedPureDiffParameters != null)
		{
			diffusionRateTextField.setText(savedPureDiffParameters.primaryDiffusionRate == null?"":savedPureDiffParameters.primaryDiffusionRate);
			mobileFractionTextField.setText(savedPureDiffParameters.primaryMobileFraction == null?"":savedPureDiffParameters.primaryMobileFraction);
			bleachWhileMonitorRateTextField.setText(savedPureDiffParameters.monitorBleachRate == null?"":savedPureDiffParameters.monitorBleachRate);
			if(savedPureDiffParameters.isSecondaryDiffusionApplied)
			{
				immFraction =1-Double.parseDouble(savedPureDiffParameters.primaryMobileFraction)-Double.parseDouble(savedPureDiffParameters.secondaryMobileFraction);
			}
			else
			{
				immoFracValueLabel.setText("");
				if(savedPureDiffParameters.primaryMobileFraction != null){
					immFraction =1-Double.parseDouble(savedPureDiffParameters.primaryMobileFraction);
					immoFracValueLabel.setText(immFraction+"");
				}
			}
//			diffusionRateSetButton.doClick();
		}
		else
		{
			diffusionRateTextField.setText("");
			mobileFractionTextField.setText("");
			bleachWhileMonitorRateTextField.setText("");
			immoFracValueLabel.setText("");
		}
		//The set function actually go through everything (every slider). 
		repaint();
	}
	
	public String getDiffusionRateString() {
		return diffusionRateTextField.getText();
	}

	public String getMobileFractionString() {
		return mobileFractionTextField.getText();
	}

	public String getImmoFracString() {
		return immoFracValueLabel.getText();
	}
	
	public String getBleachWhileMonitorRateString() {
		return bleachWhileMonitorRateTextField.getText();
	}
	
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			FRAPDiffOneParamPanel aPanel = new FRAPDiffOneParamPanel();
			frame.setContentPane(aPanel);
			frame.pack();
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
