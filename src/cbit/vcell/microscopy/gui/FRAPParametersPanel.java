package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Dimension;

import cbit.gui.DialogUtils;
import cbit.util.Compare;
import cbit.util.NumberUtils;
import cbit.vcell.math.gui.ExpressionCanvas;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FrapDataAnalysisResults;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.microscopy.gui.FRAPStudyPanel.FrapChangeInfo;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import cbit.vcell.microscopy.gui.FRAPEstimationPanel;

public class FRAPParametersPanel extends JPanel {
	private FRAPEstimationPanel estimationPanel;
	private JComboBox frapDataTimesComboBox;
	private JTextField slowerTextField;
	private FRAPData initFRAPData;
	private JLabel immobileFractionValueJLabel;
	private JTextField monitorBleachRateTextField;
	private JTextField mobileFractionTextField;
	private JTextField diffusionRateTextField;
	private static final String PARAM_EST_EQUATION_STRING = "FRAP Model Parameter Estimation Equation";
	private static final String PLOT_TITLE_STRING = "Plot of average data intensity at each timepoint within the 'bleach' ROI -and- Plot of estimation fit";
	
	public FRAPParametersPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0};
		gridBagLayout.columnWidths = new int[] {7,7};
		setLayout(gridBagLayout);

		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(Color.black, 2, false), "Initial FRAP Model Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("", Font.BOLD, 16), null));
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {7,0,0};
		gridBagLayout_1.rowHeights = new int[] {0,7,7,7,7,0,7,7};
		panel.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.weightx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;

		estimationPanel = new FRAPEstimationPanel();
		estimationPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 2, false), "FRAP Model Parameter Assistant (Select 'Estimation Method')", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("", Font.BOLD, 16), null));
		estimationPanel.addPropertyChangeListener(
			new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(FRAPEstimationPanel.FRAP_PARAMETER_ESTIMATE_VALUES_PROPERTY)){
						FRAPEstimationPanel.FRAPParameterEstimateValues frapParamEstVals =
							(FRAPEstimationPanel.FRAPParameterEstimateValues)evt.getNewValue();
						
						if(frapParamEstVals.startTimeRecovery != null){
							boolean bFound = false;
							for (int i = 0; i < frapDataTimesComboBox.getItemCount(); i++) {
								if(frapDataTimesComboBox.getItemAt(i).equals(frapParamEstVals.startTimeRecovery.toString())){
									bFound = true;
									frapDataTimesComboBox.setSelectedIndex(i);
									break;
								}
							}
							if(!bFound){
								throw new IllegalArgumentException("couldn't find time "+frapParamEstVals.startTimeRecovery.toString()+" in FRAP data while setting");
							}
						}
						if(frapParamEstVals.diffusionRate != null){
							diffusionRateTextField.setText(""+frapParamEstVals.diffusionRate);
						}
						if(frapParamEstVals.mobileFraction != null){
							mobileFractionTextField.setText(""+frapParamEstVals.mobileFraction);
							immobileFractionValueJLabel.setText(""+(1.0-frapParamEstVals.mobileFraction));
						}
						if(frapParamEstVals.bleachWhileMonitorRate != null){
							monitorBleachRateTextField.setText(""+frapParamEstVals.bleachWhileMonitorRate);
						}
					}
				}
			}
		);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.weighty = 1;
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 0;
		add(estimationPanel, gridBagConstraints_1);
		add(panel, gridBagConstraints);

		final JLabel parameterLabel = new JLabel();
		parameterLabel.setBorder(new LineBorder(Color.black, 1, false));
		parameterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		parameterLabel.setText("Parameter Type");
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_19.fill = GridBagConstraints.BOTH;
		gridBagConstraints_19.gridy = 0;
		gridBagConstraints_19.gridx = 0;
		panel.add(parameterLabel, gridBagConstraints_19);

		final JLabel valueLabel = new JLabel();
		valueLabel.setBorder(new LineBorder(Color.black, 1, false));
		valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		valueLabel.setText("Value");
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.weightx = 1;
		gridBagConstraints_20.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_20.fill = GridBagConstraints.BOTH;
		gridBagConstraints_20.gridy = 0;
		gridBagConstraints_20.gridx = 1;
		panel.add(valueLabel, gridBagConstraints_20);

		final JLabel unitsLabel = new JLabel();
		unitsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		unitsLabel.setBorder(new LineBorder(Color.black, 1, false));
		unitsLabel.setText("Units");
		final GridBagConstraints gridBagConstraints_31 = new GridBagConstraints();
		gridBagConstraints_31.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_31.fill = GridBagConstraints.BOTH;
		gridBagConstraints_31.gridy = 0;
		gridBagConstraints_31.gridx = 2;
		panel.add(unitsLabel, gridBagConstraints_31);

		final JLabel diffusionRateLabel = new JLabel();
		diffusionRateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		diffusionRateLabel.setText("Diffusion Rate");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_18.fill = GridBagConstraints.BOTH;
		gridBagConstraints_18.gridy = 1;
		gridBagConstraints_18.gridx = 0;
		panel.add(diffusionRateLabel, gridBagConstraints_18);

		diffusionRateTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_12.weightx = 0;
		gridBagConstraints_12.gridy = 1;
		gridBagConstraints_12.gridx = 1;
		panel.add(diffusionRateTextField, gridBagConstraints_12);

		final JLabel umsecLabel = new JLabel();
		umsecLabel.setText("um2/s");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_14.gridy = 1;
		gridBagConstraints_14.gridx = 2;
		panel.add(umsecLabel, gridBagConstraints_14);

		final JLabel mobileFractionLabel = new JLabel();
		mobileFractionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mobileFractionLabel.setText("Mobile Fraction");
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_21.fill = GridBagConstraints.BOTH;
		gridBagConstraints_21.gridy = 2;
		gridBagConstraints_21.gridx = 0;
		panel.add(mobileFractionLabel, gridBagConstraints_21);

		mobileFractionTextField = new JTextField();
		mobileFractionTextField.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(final UndoableEditEvent e) {
				updateImmobileFractionModelText();
			}
		});
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_13.gridy = 2;
		gridBagConstraints_13.gridx = 1;
		panel.add(mobileFractionTextField, gridBagConstraints_13);

		final JLabel label = new JLabel();
		label.setText("1/s");
		final GridBagConstraints gridBagConstraints_32 = new GridBagConstraints();
		gridBagConstraints_32.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_32.gridy = 2;
		gridBagConstraints_32.gridx = 2;
		panel.add(label, gridBagConstraints_32);

		final JLabel immobileFractionLabel = new JLabel();
		immobileFractionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		immobileFractionLabel.setText("Immobile Fraction");
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_22.fill = GridBagConstraints.BOTH;
		gridBagConstraints_22.gridy = 3;
		gridBagConstraints_22.gridx = 0;
		panel.add(immobileFractionLabel, gridBagConstraints_22);

		immobileFractionValueJLabel = new JLabel();
		immobileFractionValueJLabel.setText("ImmobileFrac");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_10.gridy = 3;
		gridBagConstraints_10.gridx = 1;
		panel.add(immobileFractionValueJLabel, gridBagConstraints_10);

		final JLabel label_1 = new JLabel();
		label_1.setText("1/s");
		final GridBagConstraints gridBagConstraints_33 = new GridBagConstraints();
		gridBagConstraints_33.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_33.gridy = 3;
		gridBagConstraints_33.gridx = 2;
		panel.add(label_1, gridBagConstraints_33);

		final JLabel monitorBleachRateLabel = new JLabel();
		monitorBleachRateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		monitorBleachRateLabel.setText("Monitor Bleach Rate");
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.ipadx = 8;
		gridBagConstraints_23.fill = GridBagConstraints.BOTH;
		gridBagConstraints_23.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_23.gridy = 4;
		gridBagConstraints_23.gridx = 0;
		panel.add(monitorBleachRateLabel, gridBagConstraints_23);

		monitorBleachRateTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.gridy = 4;
		gridBagConstraints_15.gridx = 1;
		panel.add(monitorBleachRateTextField, gridBagConstraints_15);

		final JLabel um2sLabel = new JLabel();
		um2sLabel.setText("um2/s");
		final GridBagConstraints gridBagConstraints_34 = new GridBagConstraints();
		gridBagConstraints_34.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_34.gridy = 4;
		gridBagConstraints_34.gridx = 2;
		panel.add(um2sLabel, gridBagConstraints_34);

		final JLabel slowerDiffMobileLabel = new JLabel();
		slowerDiffMobileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		slowerDiffMobileLabel.setText("Slower Diff, Mobile");
		final GridBagConstraints gridBagConstraints_35 = new GridBagConstraints();
		gridBagConstraints_35.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_35.fill = GridBagConstraints.BOTH;
		gridBagConstraints_35.gridy = 5;
		gridBagConstraints_35.gridx = 0;
		panel.add(slowerDiffMobileLabel, gridBagConstraints_35);

		slowerTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_36 = new GridBagConstraints();
		gridBagConstraints_36.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_36.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_36.gridy = 5;
		gridBagConstraints_36.gridx = 1;
		panel.add(slowerTextField, gridBagConstraints_36);

		final JLabel um2sLabel_1 = new JLabel();
		um2sLabel_1.setText("um2/s");
		final GridBagConstraints gridBagConstraints_37 = new GridBagConstraints();
		gridBagConstraints_37.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_37.gridy = 5;
		gridBagConstraints_37.gridx = 2;
		panel.add(um2sLabel_1, gridBagConstraints_37);

		final JLabel startIndexRecoveryLabel = new JLabel();
		startIndexRecoveryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		startIndexRecoveryLabel.setText("Start Time Recovery");
		final GridBagConstraints gridBagConstraints_41 = new GridBagConstraints();
		gridBagConstraints_41.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_41.fill = GridBagConstraints.BOTH;
		gridBagConstraints_41.gridy = 6;
		gridBagConstraints_41.gridx = 0;
		panel.add(startIndexRecoveryLabel, gridBagConstraints_41);

		frapDataTimesComboBox = new JComboBox();
		final GridBagConstraints gridBagConstraints_42 = new GridBagConstraints();
		gridBagConstraints_42.fill = GridBagConstraints.BOTH;
		gridBagConstraints_42.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_42.gridy = 6;
		gridBagConstraints_42.gridx = 1;
		panel.add(frapDataTimesComboBox, gridBagConstraints_42);

		final JLabel sLabel = new JLabel();
		sLabel.setText("s");
		final GridBagConstraints gridBagConstraints_43 = new GridBagConstraints();
		gridBagConstraints_43.gridy = 6;
		gridBagConstraints_43.gridx = 2;
		panel.add(sLabel, gridBagConstraints_43);
		
	}

	public FrapChangeInfo createCompleteFRAPChangeInfo(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo,
			boolean bCellROISame,boolean bBleachROISame,boolean bBackgroundROISame,boolean bROISameSize){
		return new FrapChangeInfo(
				!bCellROISame || !bBleachROISame || !bBackgroundROISame,
				!bROISameSize,
				isUserDiffusionRateChanged(savedFrapModelInfo),
				getUserDiffusionRateString(),
				isUserMonitorBleachRateChanged(savedFrapModelInfo),
				getUserMonitorBleachRateString(),
				isUserMobileFractionChanged(savedFrapModelInfo),
				getUserMobileFractionString(),
				isUserSlowerRateChanged(savedFrapModelInfo),
				getUserSlowerRateString(),
				isUserStartIndexForRecoveryChanged(savedFrapModelInfo),
				getUserStartIndexForRecoveryString());

	}
	public void initializeSavedFrapModelInfo(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo,double[] frapDataTimeStamps){

		diffusionRateTextField.setText((savedFrapModelInfo == null
				?""
				:(savedFrapModelInfo.lastBaseDiffusionrate == null
					?""
					:savedFrapModelInfo.lastBaseDiffusionrate.toString())));
		monitorBleachRateTextField.setText((savedFrapModelInfo == null
			?""
			:(savedFrapModelInfo.lastBleachWhileMonitoringRate == null
				?""
				:savedFrapModelInfo.lastBleachWhileMonitoringRate.toString())));
		mobileFractionTextField.setText((savedFrapModelInfo == null
				?""
				:(savedFrapModelInfo.lastMobileFraction == null
					?""
					:savedFrapModelInfo.lastMobileFraction.toString())));
		updateImmobileFractionModelText();
		slowerTextField.setText((savedFrapModelInfo == null
				?""
				:(savedFrapModelInfo.lastSlowerRate == null
					?""
					:savedFrapModelInfo.lastSlowerRate.toString())));

		frapDataTimesComboBox.removeAllItems();
		for (int i = 0; i < frapDataTimeStamps.length; i++) {
			frapDataTimesComboBox.insertItemAt(frapDataTimeStamps[i]+"", i);
		}
		frapDataTimesComboBox.setSelectedIndex(0);
		if(savedFrapModelInfo != null && savedFrapModelInfo.startingIndexForRecovery != null){
			frapDataTimesComboBox.setSelectedIndex(new Integer(savedFrapModelInfo.startingIndexForRecovery));
		}
		repaint();

	}
	private void updateImmobileFractionModelText(){
		try{
			double mobileFractionIntermediate = Double.parseDouble(mobileFractionTextField.getText());
			if(mobileFractionIntermediate <= 1.0){
				double immobileFractionIntermediate = 1.0-mobileFractionIntermediate;
				immobileFractionValueJLabel.setText(""+immobileFractionIntermediate/*Double.parseDouble(NumberUtils.formatNumber(immobileFractionIntermediate, 5))*/);
			}else{
				immobileFractionValueJLabel.setText("");
			}
		}catch(Exception e2){
			immobileFractionValueJLabel.setText("");
		}

	}
	
	public void refreshFRAPModelParameterEstimates(FRAPData frapData) throws Exception {
		estimationPanel.refreshFRAPModelParameterEstimates(frapData);
	}

	public void insertFRAPModelParametersIntoFRAPStudy(FRAPStudy frapStudy) throws Exception{
		if(frapStudy != null){
			frapStudy.setFrapModelParameters(null);
			try{
				String monitorBleachRateText =getUserMonitorBleachRateString();
				if(monitorBleachRateText != null && monitorBleachRateText.length()>0){
					//check validity
					Double.parseDouble(monitorBleachRateText);
				}
			}catch(Exception e){
				throw new Exception("Error parsing 'Monitor Bleach Rate', "+e.getMessage());
			}
			try{
				String mobileFractionText = getUserMobileFractionString();
				if(mobileFractionText != null && mobileFractionText.length()>0){
					//check validity
					Double.parseDouble(mobileFractionText);
				}
			}catch(Exception e){
				throw new Exception("Error parsing 'Mobile Fraction', "+e.getMessage());
			}
			try{
				String diffusionRateText = getUserDiffusionRateString();
				if(diffusionRateText != null && diffusionRateText.length()>0){
					//check validity
					Double.parseDouble(diffusionRateText);
				}
			}catch(Exception e){
				throw new Exception("Error parsing 'Diffusion Rate', "+e.getMessage());
			}
			try{
				String slowerRateText = getUserSlowerRateString();
				if(slowerRateText != null && slowerRateText.length()>0){
					//check validity
					Double.parseDouble(slowerRateText);
				}
			}catch(Exception e){
				throw new Exception("Error parsing 'Slower Rate', "+e.getMessage());
			}
			FRAPStudy.FRAPModelParameters frapModelParameters =
					new FRAPStudy.FRAPModelParameters(
							getUserStartIndexForRecoveryString(),
							getUserDiffusionRateString(),
							getUserMonitorBleachRateString(),
							getUserMobileFractionString(),
							getUserSlowerRateString()
					);
			frapStudy.setFrapModelParameters(frapModelParameters);
		}
	}
	private String getUserDiffusionRateString(){
		return
			(diffusionRateTextField.getText() == null || diffusionRateTextField.getText().length() == 0
				?null
				:diffusionRateTextField.getText());
	}
	private boolean isUserDiffusionRateChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastBaseDiffusionrate), getUserDiffusionRateString());
	}
	
	private String getUserMonitorBleachRateString(){
		return
			(monitorBleachRateTextField.getText() == null || monitorBleachRateTextField.getText().length() == 0
				?null
				:monitorBleachRateTextField.getText());
	}
	private boolean isUserMonitorBleachRateChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastBleachWhileMonitoringRate), getUserMonitorBleachRateString());
	}

	private String getUserMobileFractionString(){
		return
			(mobileFractionTextField.getText() == null || mobileFractionTextField.getText().length() == 0
				?null
				:mobileFractionTextField.getText());
	}
	private boolean isUserMobileFractionChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastMobileFraction), getUserMobileFractionString());
	}

	private String getUserSlowerRateString(){
		return
			(slowerTextField.getText() == null || slowerTextField.getText().length() == 0
				?null
				:slowerTextField.getText());
	}
	private boolean isUserSlowerRateChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.lastSlowerRate), getUserSlowerRateString());
	}

	private String getUserStartIndexForRecoveryString(){
		if(frapDataTimesComboBox.getItemCount() == 0){
			return null;
		}
		return frapDataTimesComboBox.getSelectedIndex()+"";
	}
	private boolean isUserStartIndexForRecoveryChanged(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo){
		return !Compare.isEqualOrNull((savedFrapModelInfo==null?null:savedFrapModelInfo.startingIndexForRecovery),getUserStartIndexForRecoveryString());
	}
}
