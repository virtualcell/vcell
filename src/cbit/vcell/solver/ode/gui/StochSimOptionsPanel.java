package cbit.vcell.solver.ode.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.stoch.StochHybridOptions;
import cbit.vcell.solver.stoch.StochSimOptions;

public class StochSimOptionsPanel extends JPanel {
	
	private SolverTaskDescription solverTaskDescription = null;	

	private javax.swing.JRadioButton trajectoryRadioButton = null;
	private javax.swing.JRadioButton histogramRadioButton = null;
	private javax.swing.ButtonGroup buttonGroupTrials = null;
	private javax.swing.JLabel numOfTrialsLabel = null;
	private javax.swing.JTextField ivjJTextFieldNumOfTrials = null;
	private javax.swing.JRadioButton ivjCustomizedSeedRadioButton = null;
	private javax.swing.JRadioButton ivjRandomSeedRadioButton = null;
	private javax.swing.ButtonGroup ivjButtonGroupSeed = null;
	private javax.swing.JTextField ivjJTextFieldCustomSeed = null;
		
	private javax.swing.JLabel ivjEpsilonLabel = null;
	private javax.swing.JTextField ivjEpsilonTextField = null;
	private javax.swing.JLabel ivjLambdaLabel = null;
	private javax.swing.JTextField ivjLambdaTextField = null;
	private javax.swing.JLabel ivjMSRToleranceLabel = null;
	private javax.swing.JTextField ivjMSRToleranceTextField = null;
	private javax.swing.JLabel ivjSDEToleranceLabel = null;
	private javax.swing.JTextField ivjSDEToleranceTextField = null;	
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == StochSimOptionsPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				refresh();
			}
			if (evt.getSource() == getSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION))) {
				refresh();
			}
			if (evt.getSource() == getSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_OUTPUT_TIME_SPEC))) {
				refresh();
			}
			if (evt.getSource() == getSolverTaskDescription() && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_STOCH_SIM_OPTIONS)) {
				refresh();
			}
		}
		
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getCustomizedSeedRadioButton() || e.getSource() == getRandomSeedRadioButton()) {
				setNewOptions();
			}  else if (e.getSource() == getTrajectoryButton() || e.getSource() == getHistogramButton()){				
				setNewOptions();
			}
		}
		
		public void focusGained(java.awt.event.FocusEvent e) {
		}

		/**
		 * Method to handle events for the FocusListener interface.
		 * @param e java.awt.event.FocusEvent
		 */
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == getJTextFieldCustomSeed() 
					|| e.getSource() == getJTextFieldNumOfTrials()
					|| e.getSource() == getEpsilonTextField() 
					|| e.getSource() == getLambdaTextField()
					|| e.getSource() == getMSRToleranceTextField()
					|| e.getSource() == getSDEToleranceTextField()) { 
				setNewOptions();
			}
		}
	}
	
	public StochSimOptionsPanel() {
		super();
		addPropertyChangeListener(ivjEventHandler);
		initialize();		
	}
	
	private void initialize() {
		try {			
			setLayout(new java.awt.GridLayout(0,1));
			TitledBorder tb = new TitledBorder(new EtchedBorder(),"Stochastic Options", 
					TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, getFont());
			setBorder(tb);

			// 1
			JPanel trialPanel = new JPanel(new GridLayout(0,1));
			trialPanel.add(getTrajectoryButton());
			trialPanel.add(getHistogramButton());	
			JPanel panela = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panela.add(getNumOfTrialsLabel());
			panela.add(getJTextFieldNumOfTrials());
			trialPanel.add(panela);
			trialPanel.setBorder(new EtchedBorder());
			
			// 2
			JPanel seedPanel = new JPanel(new GridLayout(0,1));
			JPanel panelb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelb.add(getRandomSeedRadioButton());
			seedPanel.add(panelb);
			panelb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelb.add(getCustomizedSeedRadioButton());
			panelb.add(getJTextFieldCustomSeed());
			seedPanel.add(panelb);
			seedPanel.setBorder(new EtchedBorder());
			
			//combine 1 and 2
			JPanel paneld=new JPanel(new GridLayout(0,2));
			paneld.add(trialPanel);
			paneld.add(seedPanel);
			add(paneld);
			
			// 3
			JPanel panelc = new JPanel(new GridLayout(0,2));
			JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));			
			panel1.add(getEpsilonLabel());
			panel1.add(getEpsilonTextField());
			panelc.add(panel1);
			panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel1.add(getLambdaLabel());
			panel1.add(getLambdaTextField());
			panelc.add(panel1);
			
			panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel1.add(getMSRToleranceLabel());
			panel1.add(getMSRToleranceTextField());
			panelc.add(panel1);
			panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel1.add(getSDEToleranceLabel());
			panel1.add(getSDEToleranceTextField());
			panelc.add(panel1);
			panelc.setBorder(new EtchedBorder());
			add(panelc);		
		    
			getButtonGroupSeed().add(getRandomSeedRadioButton());
			getButtonGroupSeed().add(getCustomizedSeedRadioButton());
			
			//trial radio button group
			getButtonGroupTrials().add(getTrajectoryButton());
			getButtonGroupTrials().add(getHistogramButton());						
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);		
		}
	}
	
	private void initConnections() {		
		getCustomizedSeedRadioButton().addActionListener(ivjEventHandler);
		getRandomSeedRadioButton().addActionListener(ivjEventHandler);
		getTrajectoryButton().addActionListener(ivjEventHandler);
		getHistogramButton().addActionListener(ivjEventHandler);
		getJTextFieldCustomSeed().addFocusListener(ivjEventHandler);
		getJTextFieldNumOfTrials().addFocusListener(ivjEventHandler);
		getEpsilonTextField().addFocusListener(ivjEventHandler);
		getLambdaTextField().addFocusListener(ivjEventHandler);
		getMSRToleranceTextField().addFocusListener(ivjEventHandler);
		getSDEToleranceTextField().addFocusListener(ivjEventHandler);
		
		InputVerifier inputVerifier = new InputVerifier() {
			
			@Override
			public boolean verify(JComponent input) {
				return false;				
			}

			@Override
			public boolean shouldYieldFocus(final JComponent input) {
				String text = ((JTextField)input).getText();
				boolean bValid = true;
				try {
					Double.parseDouble(text);
				} catch (Exception ex) {
					DialogUtils.showErrorDialog(StochSimOptionsPanel.this, ex.getMessage() + ", wrong number format.");
					bValid = false;
				}
				if (bValid) {
					input.setBorder(UIManager.getBorder("TextField.border"));
				} else {
					input.setBorder(BorderFactory.createLineBorder(Color.red));
					SwingUtilities.invokeLater(new Runnable() { 
					    public void run() { 
					    	input.requestFocus();
					    }
					});
				}
				return bValid;
			}
		};
		
		getJTextFieldCustomSeed().setInputVerifier(inputVerifier);
		getJTextFieldNumOfTrials().setInputVerifier(inputVerifier);
		getEpsilonTextField().setInputVerifier(inputVerifier);
		getLambdaTextField().setInputVerifier(inputVerifier);
		getMSRToleranceTextField().setInputVerifier(inputVerifier);
		getSDEToleranceTextField().setInputVerifier(inputVerifier);

	}
	
	/**
	 * Return the JTextField1 property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldCustomSeed() {
		if (ivjJTextFieldCustomSeed == null) {
			try {
				ivjJTextFieldCustomSeed = new javax.swing.JTextField();
				ivjJTextFieldCustomSeed.setName("JTextFieldCustomSeed");
				ivjJTextFieldCustomSeed.setEnabled(false);
				ivjJTextFieldCustomSeed.setColumns(9);
				
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldCustomSeed;
	}

	/**
	 * Return the JTextField2 property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldNumOfTrials() {
		if (ivjJTextFieldNumOfTrials == null) {
			try {
				ivjJTextFieldNumOfTrials = new javax.swing.JTextField();
				ivjJTextFieldNumOfTrials.setName("JTextFieldNumOfTrials");
				ivjJTextFieldNumOfTrials.setColumns(9);
				ivjJTextFieldNumOfTrials.setText("100");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldNumOfTrials;
	}



	/**
	 * Return the NumOfTrials property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getNumOfTrialsLabel() {
		if (numOfTrialsLabel == null) {
			try {
				numOfTrialsLabel = new javax.swing.JLabel();
				numOfTrialsLabel.setName("NumOfTrials");
				numOfTrialsLabel.setText("Num. Of Trials");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numOfTrialsLabel;
	}

	/**
	 * Return the RandomSeed property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRandomSeedRadioButton() {
		if (ivjRandomSeedRadioButton == null) {
			try {
				ivjRandomSeedRadioButton = new javax.swing.JRadioButton();
				ivjRandomSeedRadioButton.setName("RandomSeed");
				ivjRandomSeedRadioButton.setText("Random Seed");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjRandomSeedRadioButton;
	}
	

	private javax.swing.JLabel getEpsilonLabel() {
		if (ivjEpsilonLabel == null) {
			try {
				ivjEpsilonLabel = new javax.swing.JLabel();
				ivjEpsilonLabel.setName("EpsilonLabel");
				ivjEpsilonLabel.setText("Epsilon");
				ivjEpsilonLabel.setPreferredSize(new Dimension(75, 20));			
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjEpsilonLabel;
	}

	private javax.swing.JTextField getEpsilonTextField() {
		if (ivjEpsilonTextField == null) {
			try {
				ivjEpsilonTextField = new javax.swing.JTextField();
				ivjEpsilonTextField.setName("JTextFieldEpsilon");
				ivjEpsilonTextField.setColumns(10);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjEpsilonTextField;
	}

	private javax.swing.JLabel getLambdaLabel() {
		if (ivjLambdaLabel == null) {
			try {
				ivjLambdaLabel = new javax.swing.JLabel();
				ivjLambdaLabel.setName("LambdaLabel");
				ivjLambdaLabel.setText("Lambda");
				ivjLambdaLabel.setPreferredSize(new java.awt.Dimension(75, 20));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLambdaLabel;
	}

	private javax.swing.JTextField getLambdaTextField() {
		if (ivjLambdaTextField == null) {
			try {
				ivjLambdaTextField = new javax.swing.JTextField();
				ivjLambdaTextField.setName("JTextFieldLambda");
				ivjLambdaTextField.setColumns(10);
				
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjLambdaTextField;
	}

	private javax.swing.JLabel getMSRToleranceLabel() {
		if (ivjMSRToleranceLabel == null) {
			try {
				ivjMSRToleranceLabel = new javax.swing.JLabel();
				ivjMSRToleranceLabel.setName("MSRLabel");
				ivjMSRToleranceLabel.setText("MSR Tolerance");
				ivjMSRToleranceLabel.setPreferredSize(new java.awt.Dimension(75, 20));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjMSRToleranceLabel;
	}

	private javax.swing.JTextField getMSRToleranceTextField() {
		if (ivjMSRToleranceTextField == null) {
			try {
				ivjMSRToleranceTextField = new javax.swing.JTextField();
				ivjMSRToleranceTextField.setName("JTextFieldMSRTolerance");
				ivjMSRToleranceTextField.setColumns(10);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjMSRToleranceTextField;
	}

	private javax.swing.JLabel getSDEToleranceLabel() {
		if (ivjSDEToleranceLabel == null) {
			try {
				ivjSDEToleranceLabel = new javax.swing.JLabel();
				ivjSDEToleranceLabel.setName("SDELabel");
				ivjSDEToleranceLabel.setText("SDE Tolerance");
				ivjSDEToleranceLabel.setPreferredSize(new java.awt.Dimension(75, 20));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjSDEToleranceLabel;
	}

	private javax.swing.JTextField getSDEToleranceTextField() {
		if (ivjSDEToleranceTextField == null) {
			try {
				ivjSDEToleranceTextField = new javax.swing.JTextField();
				ivjSDEToleranceTextField.setName("JTextFieldSDETolerance");
				ivjSDEToleranceTextField.setColumns(10);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjSDEToleranceTextField;
	}

	/**
	 * Return the ButtonGroupSeed property value.
	 * @return javax.swing.ButtonGroup
	 */
	private javax.swing.ButtonGroup getButtonGroupTrials() {
		if (buttonGroupTrials == null) {
			try {
				buttonGroupTrials = new javax.swing.ButtonGroup();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return buttonGroupTrials;
	}

	/**
	 * Return the CustomizedSeed property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getTrajectoryButton() {
		if (trajectoryRadioButton == null) {
			try {
				trajectoryRadioButton = new javax.swing.JRadioButton();
				trajectoryRadioButton.setName("Trajectory");
				trajectoryRadioButton.setText("Single Trajectory");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return trajectoryRadioButton;
	}

	/**
	 * Return the CustomizedSeed property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getHistogramButton() {
		if (histogramRadioButton == null) {
			try {
				histogramRadioButton = new javax.swing.JRadioButton();
				histogramRadioButton.setName("Histogram");
				histogramRadioButton.setText("Histogram (last time point only)");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return histogramRadioButton;
	}


	/**
	 * Return the ButtonGroupSeed property value.
	 * @return javax.swing.ButtonGroup
	 */
	private javax.swing.ButtonGroup getButtonGroupSeed() {
		if (ivjButtonGroupSeed == null) {
			try {
				ivjButtonGroupSeed = new javax.swing.ButtonGroup();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjButtonGroupSeed;
	}


	/**
	 * Return the CustomizedSeed property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getCustomizedSeedRadioButton() {
		if (ivjCustomizedSeedRadioButton == null) {
			try {
				ivjCustomizedSeedRadioButton = new javax.swing.JRadioButton();
				ivjCustomizedSeedRadioButton.setName("CustomizedSeed");
				ivjCustomizedSeedRadioButton.setText("Customized Seed");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCustomizedSeedRadioButton;
	}	

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}
	
	public final SolverTaskDescription getSolverTaskDescription() {
		return solverTaskDescription;
	}
	
	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		SolverTaskDescription oldValue = solverTaskDescription;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(ivjEventHandler);
		}
		solverTaskDescription = newValue;

		/* Listen for events from the new object */
		if (newValue != null) {
			newValue.addPropertyChangeListener(ivjEventHandler);
		}		
		solverTaskDescription = newValue;
		firePropertyChange("solverTaskDescription", oldValue, newValue);
		
		initConnections();
	}
	
	/**
	 * Update parameters for stochastic simulations,
	 * including using customized seed or not, customized seed, using tractory or histogram, number of trials (for all, and below four paras for hybrid only)
	 * Epsilon : minimum number of molecus required for approximation as a continuous Markow process,
	 * Lambda : minimum rate of reaction required for approximation to a continuous Markov process,
	 * MSR Tolerance : Maximum allowed effect of slow reactions per numerical integration of the SDEs,
	 * SDE Tolerance : Maximum allowed value of the drift and diffusion errors
	 */
	private void setNewOptions(){
		if(!isVisible()){
			return;
		}
		try{
			StochSimOptions stochOpt = getSolverTaskDescription().getStochOpt();
			long numTrials = 1;
			if (getHistogramButton().isSelected()) {
				numTrials = Integer.parseInt(getJTextFieldNumOfTrials().getText());				
			}
			boolean bUseCustomSeed = getCustomizedSeedRadioButton().isSelected();
			int customSeed = stochOpt.getCustomSeed();
			if (bUseCustomSeed) {
				customSeed = (Integer.parseInt(getJTextFieldCustomSeed().getText()));
			}
		
			if(getSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson))	{				
				getSolverTaskDescription().setStochOpt(new StochSimOptions(bUseCustomSeed, customSeed, numTrials));
			}
			else {
				StochHybridOptions stochHybridOptions = (StochHybridOptions)stochOpt;
				double epsilon = stochHybridOptions.getEpsilon();
				if(getEpsilonTextField().isEnabled() && getEpsilonTextField().getText().length() > 0) {
					epsilon = Double.parseDouble(getEpsilonTextField().getText());
				}
				double lambda = stochHybridOptions.getLambda();
				if(getLambdaTextField().isEnabled() && getLambdaTextField().getText().length() > 0) {
					lambda = Double.parseDouble(getLambdaTextField().getText());
				}
				double MSRTolerance = stochHybridOptions.getMSRTolerance();		
				if(getMSRToleranceTextField().isEnabled() && getMSRToleranceTextField().getText().length() > 0) {
					MSRTolerance = Double.parseDouble(getMSRToleranceTextField().getText());
				}
				double SDETolerance = stochHybridOptions.getSDETolerance();
				if(getSDEToleranceTextField().isEnabled() && getSDEToleranceTextField().getText().length() > 0) {
					SDETolerance = Double.parseDouble(getSDEToleranceTextField().getText());
				}
				getSolverTaskDescription().setStochOpt(new StochHybridOptions(bUseCustomSeed, 
						customSeed, numTrials, epsilon, lambda, MSRTolerance, SDETolerance));
			}
		} catch(Exception e){
			PopupGenerator.showErrorDialog(this, e.getMessage());
		}
	}
	
	private void refresh() {
		
		if (getSolverTaskDescription() != null) {
			if (!getSolverTaskDescription().getSolverDescription().isStochasticNonSpatialSolver()) {
				setVisible(false);
				return;
			}
		}
			
		setVisible(true);		
		StochSimOptions sso = getSolverTaskDescription().getStochOpt();	
		
		long numTrials = sso.getNumOfTrials();		
		if(numTrials == 1){ // 1 trial
			getJTextFieldNumOfTrials().setEnabled(false);
			getTrajectoryButton().setSelected(true);
		}else{//more than 1 trial
			getJTextFieldNumOfTrials().setEnabled(true);
			getHistogramButton().setSelected(true);
			getJTextFieldNumOfTrials().setText(numTrials+"");
		}
		boolean isUseCustomSeed = sso.isUseCustomSeed();
		int customSeed = sso.getCustomSeed();
		
		getJTextFieldCustomSeed().setEnabled(isUseCustomSeed);
		if(isUseCustomSeed){
			getCustomizedSeedRadioButton().setSelected(true);
			getJTextFieldCustomSeed().setEnabled(true);		
		}else{
			getRandomSeedRadioButton().setSelected(true);
			getJTextFieldCustomSeed().setEnabled(false);
		}
		getJTextFieldCustomSeed().setText(customSeed+"");

		boolean bHybrid = sso instanceof StochHybridOptions;
		
		getEpsilonLabel().setEnabled(bHybrid);
		getEpsilonTextField().setEnabled(bHybrid);
		getLambdaLabel().setEnabled(bHybrid);
		getLambdaTextField().setEnabled(bHybrid);
		getMSRToleranceLabel().setEnabled(bHybrid);
		getMSRToleranceTextField().setEnabled(bHybrid);
		getSDEToleranceLabel().setEnabled(bHybrid);
		getSDEToleranceTextField().setEnabled(bHybrid);
		
		if (bHybrid)
		{
			StochHybridOptions sho = (StochHybridOptions)sso;
			getEpsilonTextField().setText(sho.getEpsilon()+"");
			getLambdaTextField().setText(sho.getLambda()+"");
			getMSRToleranceTextField().setText(sho.getMSRTolerance()+"");
			getSDEToleranceTextField().setText(sho.getSDETolerance()+"");
			if(!getSolverTaskDescription().getSolverDescription().equals(SolverDescription.HybridMilAdaptive))
			{
				getSDEToleranceTextField().setEnabled(false);
			}
		}
	}
}
