package cbit.vcell.microscopy.gui;

import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import cbit.gui.DialogUtils;
import cbit.util.AsynchProgressPopup;
import cbit.util.BeanUtils;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.opt.Parameter;

public class FRAPInterpolationPanel extends JPanel {
	
	
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
	
	public static final String PROPERTY_CHANGE_OPTIMIZER_VALUE = "PROPERTY_CHANGE_OPTIMIZER_VALUE";
	public static final String PROPERTY_CHANGE_RUNSIM = "PROPERTY_CHANGE_RUNSIM";
	
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
					double value =
						FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()+
						(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()-FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())*
						((double)bleachWhileMonitorSlider.getValue()/(double)bleachWhileMonitorSlider.getMaximum());
					bleachWhileMonitorRateTextField.setText(value+"");
					bleachWhileMonitorSetButton.setEnabled(false);
				}
				if(!((JSlider)e.getSource()).getValueIsAdjusting()){
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
						/*double*/ value = Double.parseDouble(mobileFractionTextField.getText());
						if(value < FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound()){
							value = FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound();
						}
						if(value > FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound()){
							value = FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound();
						}
						mobileFractionTextField.setText(value+"");
						/*int*/ sliderValue = (int)
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
						if(value < FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()){
							value = FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
						}
						if(value > FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()){
							value = FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound();
						}
						bleachWhileMonitorRateTextField.setText(value+"");
						/*int*/ sliderValue = (int)
							(((value-FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())*(double)bleachWhileMonitorSlider.getMaximum())/
							(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()-FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()));
						if(sliderValue < bleachWhileMonitorSlider.getMinimum()){
							sliderValue = bleachWhileMonitorSlider.getMinimum();
						}
						if(sliderValue > bleachWhileMonitorSlider.getMaximum()){
							sliderValue = bleachWhileMonitorSlider.getMaximum();
						}
						bleachWhileMonitorSlider.setValue(sliderValue);
						bleachWhileMonitorSetButton.setEnabled(false);
//					}
					if(!B_HOLD_FIRE){
						firePropertyChange(PROPERTY_CHANGE_OPTIMIZER_VALUE, null, null);
					}
				}catch(Exception e2){
					e2.printStackTrace();
					DialogUtils.showErrorDialog("Error setting parameter value for "+
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
	
	public FRAPInterpolationPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {7,7,7};
		gridBagLayout.rowHeights = new int[] {7,7,7,7};
		setLayout(gridBagLayout);

		final JButton createOptimalButton = new JButton();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_8.fill = GridBagConstraints.BOTH;
		gridBagConstraints_8.gridwidth = 3;
		gridBagConstraints_8.gridy = 0;
		gridBagConstraints_8.gridx = 0;
		add(createOptimalButton, gridBagConstraints_8);
		createOptimalButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final AsynchProgressPopup pp =
					new AsynchProgressPopup(
						FRAPInterpolationPanel.this,
						"Finding Best Fit Parameters...",
						"Working...",true,false);
				pp.start();
				new Thread(new Runnable(){public void run(){
					try{
						final Parameter[] bestParameters = frapOptData.getBestParamters(getCurrentParameters(), FRAPInterpolationPanel.getErrorOfInterest());
						SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
							setParameterValues(
								bestParameters[FRAPOptData.DIFFUSION_RATE_INDEX].getInitialGuess()+"",
								bestParameters[FRAPOptData.MOBILE_FRACTION_INDEX].getInitialGuess()+"",
								bestParameters[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX].getInitialGuess()+"");
							firePropertyChange(PROPERTY_CHANGE_OPTIMIZER_VALUE, null,null);
						}});
						
					}catch(final Exception e2){
						pp.stop();
						e2.printStackTrace();
						SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
							DialogUtils.showErrorDialog("Error setting Best Fit Parameters\n"+e2.getMessage());
						}});
					}finally{
						pp.stop();
					}
				}}).start();
			}
		});
		createOptimalButton.setText("Set Parameters for Best Fit with Experimental Data");

		final JButton runSimbutton = new JButton();
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.anchor = GridBagConstraints.EAST;
		gridBagConstraints_13.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_13.gridy = 0;
		gridBagConstraints_13.gridx = 3;
		add(runSimbutton, gridBagConstraints_13);
//		runSimbutton.setFont(new Font("", Font.BOLD, 14));
		runSimbutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				firePropertyChange(PROPERTY_CHANGE_RUNSIM, null,null);
			}
		});
		runSimbutton.setText("Create New FRAP Document using Current Parameter Settings...");

		final JLabel diffusionRateLabel = new JLabel();
//		diffusionRateLabel.setFont(new Font("", Font.BOLD, 14));
		diffusionRateLabel.setText("Diffusion Rate:");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 1;
		gridBagConstraints_9.gridx = 0;
		add(diffusionRateLabel, gridBagConstraints_9);

		diffusionRateTextField = new JTextField();
		diffusionRateTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		diffusionRateTextField.setPreferredSize(new Dimension(125, 20));
		diffusionRateTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
		diffusionRateTextField.setMinimumSize(new Dimension(125, 20));
//		diffusionRateTextField.setText("DiffusionRate");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 1;
		add(diffusionRateTextField, gridBagConstraints);

		diffusionRateSetButton = new JButton();
		diffusionRateSetButton.setMargin(new Insets(0, 2, 0, 2));
		diffusionRateSetButton.addActionListener(SET_ACTION_LISTENER);
		diffusionRateSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 2;
		add(diffusionRateSetButton, gridBagConstraints_1);

		diffusionRateSlider = new JSlider();
		diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		diffusionRateSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.weightx = 1;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 3;
		add(diffusionRateSlider, gridBagConstraints_4);

		final JLabel mobileFractionLabel = new JLabel();
//		mobileFractionLabel.setFont(new Font("", Font.BOLD, 14));
		mobileFractionLabel.setText("Mobile Fraction:");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_11.anchor = GridBagConstraints.EAST;
		gridBagConstraints_11.gridy = 2;
		gridBagConstraints_11.gridx = 0;
		add(mobileFractionLabel, gridBagConstraints_11);

		mobileFractionTextField = new JTextField();
		mobileFractionTextField.getDocument().addUndoableEditListener(EDIT_LISTENER);
		mobileFractionTextField.setPreferredSize(new Dimension(125, 20));
		mobileFractionTextField.setMinimumSize(new Dimension(125, 20));
		mobileFractionTextField.addActionListener(OPTIMIZER_VALUE_ACTION_LISTENER);
//		mobileFractionTextField.setText("mobileFraction");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(2, 2, 2, 0);
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 1;
		add(mobileFractionTextField, gridBagConstraints_2);

		mobileFractionSetButton = new JButton();
		mobileFractionSetButton.addActionListener(SET_ACTION_LISTENER);
		mobileFractionSetButton.setMargin(new Insets(0, 2, 0, 2));
		mobileFractionSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 2;
		add(mobileFractionSetButton, gridBagConstraints_5);

		mobileFractionSlider = new JSlider();
		mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		mobileFractionSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.weightx = 1;
		gridBagConstraints_6.gridy = 2;
		gridBagConstraints_6.gridx = 3;
		add(mobileFractionSlider, gridBagConstraints_6);

		final JLabel bleachWhileMonitorLabel = new JLabel();
//		bleachWhileMonitorLabel.setFont(new Font("", Font.BOLD, 14));
		bleachWhileMonitorLabel.setText("Bleach While Monitor Rate:");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 3;
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
		gridBagConstraints_3.gridy = 3;
		gridBagConstraints_3.gridx = 1;
		add(bleachWhileMonitorRateTextField, gridBagConstraints_3);

		bleachWhileMonitorSetButton = new JButton();
		bleachWhileMonitorSetButton.addActionListener(SET_ACTION_LISTENER);
		bleachWhileMonitorSetButton.setMargin(new Insets(0, 2, 0, 2));
		bleachWhileMonitorSetButton.setText("Set");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(2, 0, 2, 2);
		gridBagConstraints_10.gridy = 3;
		gridBagConstraints_10.gridx = 2;
		add(bleachWhileMonitorSetButton, gridBagConstraints_10);

		bleachWhileMonitorSlider = new JSlider();
		bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		bleachWhileMonitorSlider.setPaintLabels(true);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.weightx = 1;
		gridBagConstraints_7.gridy = 3;
		gridBagConstraints_7.gridx = 3;
		add(bleachWhileMonitorSlider, gridBagConstraints_7);		
	}

	private void initialize(){
		
		diffusionRateSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
		mobileFractionSlider.removeChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
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
			
			Hashtable<Integer, JComponent> mobileFractionSliderLabelTable = new Hashtable<Integer, JComponent>();
			mobileFractionSlider.setMinimum(0);
			mobileFractionSlider.setMaximum(100);
			mobileFractionSlider.setValue(50);
			mobileFractionSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound()+""));
			mobileFractionSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound()+""));
			mobileFractionSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			mobileFractionSlider.setLabelTable(mobileFractionSliderLabelTable);
			
			
			Hashtable<Integer, JComponent> bleachWhileMonitorSliderLabelTable = new Hashtable<Integer, JComponent>();
			bleachWhileMonitorSlider.setMinimum(0);
			bleachWhileMonitorSlider.setMaximum(100);
			bleachWhileMonitorSlider.setValue(50);
			bleachWhileMonitorSliderLabelTable.put(0, new JLabel(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound()+""));
			bleachWhileMonitorSliderLabelTable.put(100,new JLabel(FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound()+""));
			bleachWhileMonitorSlider.setLabelTable(null);//Kludge for WindowBuilder otherwise not display correctly
			bleachWhileMonitorSlider.setLabelTable(bleachWhileMonitorSliderLabelTable);
		}finally{
			diffusionRateSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			mobileFractionSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
			bleachWhileMonitorSlider.addChangeListener(OPTIMIZER_SLIDER_CHANGE_LISTENER);
	
		}
	}
	
	public void init(FRAPOptData frapOptData) throws Exception{
		
		initialize();
		
		this.frapOptData = frapOptData;

		setParameterValues(
				frapOptData.getExpFrapStudy().getFrapModelParameters().diffusionRate,
				frapOptData.getExpFrapStudy().getFrapModelParameters().mobileFraction,
				frapOptData.getExpFrapStudy().getFrapModelParameters().monitorBleachRate);

	}
	private void setParameterValues(String diffusionRate,String mobileFraction, String monitorBleachRate){
		diffusionRateTextField.setText(diffusionRate);
		mobileFractionTextField.setText((mobileFraction != null
				?mobileFraction
				:"1.0"));
		bleachWhileMonitorRateTextField.setText((monitorBleachRate != null
				?monitorBleachRate
				:"0"));
		
		B_HOLD_FIRE = true;
		diffusionRateSetButton.doClick();
		mobileFractionSetButton.doClick();
		bleachWhileMonitorSetButton.doClick();
		B_HOLD_FIRE = false;
	}
	private static Parameter[] createParameterArray(double diffusionRate,double mobileFraction,double monitorBleachRate){
		Parameter diff =
			new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX], 0, 100, 1.0,diffusionRate);
		Parameter mobileFrac =
			new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX], 0, 1, 1.0,mobileFraction);
		Parameter bleachWhileMonitoringRate =
			new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX], 0, 10, 1.0,monitorBleachRate);
		
		return new Parameter[]{diff, mobileFrac, bleachWhileMonitoringRate};
	}
	public Parameter[] getCurrentParameters(){
		return
			createParameterArray(
				Double.parseDouble(diffusionRateTextField.getText()),
				Double.parseDouble(mobileFractionTextField.getText()),
				Double.parseDouble(bleachWhileMonitorRateTextField.getText()));
	}
	private double[][] getFitData(Parameter[] userParams) throws Exception{
		
		double[][] fitData = frapOptData.getFitData(userParams, FRAPInterpolationPanel.getErrorOfInterest()); // double[roiLen][timePoints]

		return fitData;

	}
	
	private static boolean[] getErrorOfInterest()
	{
		boolean[] errorOfInterest = new boolean[RoiType.values().length];
		
		for(int i=0; i<RoiType.values().length; i++)
		{
			if(RoiType.values()[i].equals(RoiType.ROI_BLEACHED) || 
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING1) ||
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING2) ||
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING3) ||
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING4) ||
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING5) ||
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING6) ||
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING7) ||
			   RoiType.values()[i].equals(RoiType.ROI_BLEACHED_RING8))
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
//	public double[][] getBestFitData() throws Exception{
//		
//		Parameter[] arbitraryParams = createParameterArray(0, 0, 0);
//		Parameter[] bestParams = frapOptData.getBestParamters(arbitraryParams);
//					
//		double[][] bestFitData = frapOptData.getFitData(bestParams); // double[roiLen][timePoints]
//
//		return bestFitData;
//	}

	public void enableAllButSetButtons(){
		BeanUtils.enableComponents(this, true);
		diffusionRateSetButton.setEnabled(false);
		mobileFractionSetButton.setEnabled(false);
		bleachWhileMonitorSetButton.setEnabled(false);
		B_HOLD_FIRE = true;
		SET_ACTION_LISTENER.actionPerformed(new ActionEvent(this,0,null));
		B_HOLD_FIRE = false;
	}
}
