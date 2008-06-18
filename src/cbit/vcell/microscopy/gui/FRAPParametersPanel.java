package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Dimension;

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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class FRAPParametersPanel extends JPanel {
	private JComboBox frapDataTimesComboBox;
	private JTextField slowerTextField;
	private JLabel startTimeRecoveryEstimateLabel;
	private FRAPData initFRAPData;
	private JLabel plotOfAverageLabel;
	private JLabel frapModelParameterLabel;
	private JLabel diffusionRateEstimateLabel;
	private JLabel mobileFractionEstimateLabel;
	private JLabel immobileFractionEstimateLabel;
	private JLabel immobileFractionValueJLabel;
	private MultisourcePlotPane multisourcePlotPane;
	private ExpressionCanvas expressionCanvas;
	private JComboBox bleachEstimationComboBox;
	private JTextField monitorBleachRateTextField;
	private JTextField mobileFractionTextField;
	private JTextField diffusionRateTextField;
	private static final String PARAM_EST_EQUATION_STRING = "FRAP Model Parameter Estimation Equation";
	private static final String PLOT_TITLE_STRING = "Plot of average data intensity at each timepoint within the 'bleach' ROI -and- Plot of estimation fit";
	
	public FRAPParametersPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7};
		gridBagLayout.columnWidths = new int[] {7,0,7};
		setLayout(gridBagLayout);

		final JLabel enterValuesUnderLabel = new JLabel();
		enterValuesUnderLabel.setFont(new Font("", Font.BOLD, 14));
		enterValuesUnderLabel.setText("Enter values manually under 'Initial FRAP Model Parameters' or 'Use' estimated parameter values");
		final GridBagConstraints gridBagConstraints_30 = new GridBagConstraints();
		gridBagConstraints_30.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_30.gridy = 0;
		gridBagConstraints_30.gridx = 0;
		gridBagConstraints_30.gridwidth = 2;
		add(enterValuesUnderLabel, gridBagConstraints_30);

		final JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.black, 1, false));
		final GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.columnWidths = new int[] {7,0,0,7,0,7};
		gridBagLayout_1.rowHeights = new int[] {0,7,7,7,7,0,7,7};
		panel.setLayout(gridBagLayout_1);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 1;
		add(panel, gridBagConstraints);

		final JLabel frapParameterEstimatesLabel = new JLabel();
		frapParameterEstimatesLabel.setBorder(new LineBorder(Color.black, 1, false));
		frapParameterEstimatesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frapParameterEstimatesLabel.setText("FRAP Model Parameter Estimation");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridwidth = 2;
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weightx = 1;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 0;
		panel.add(frapParameterEstimatesLabel, gridBagConstraints_1);

		final JLabel frapModelParametersLabel = new JLabel();
		frapModelParametersLabel.setBorder(new LineBorder(Color.black, 1, false));
		frapModelParametersLabel.setFont(new Font("", Font.BOLD, 18));
		frapModelParametersLabel.setHorizontalAlignment(SwingConstants.CENTER);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_3.fill = GridBagConstraints.BOTH;
		gridBagConstraints_3.gridwidth = 3;
		gridBagConstraints_3.weightx = 1;
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 3;
		panel.add(frapModelParametersLabel, gridBagConstraints_3);
		frapModelParametersLabel.setText("Initial FRAP Model Parameters");

		bleachEstimationComboBox = new JComboBox();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_2.weightx = 0;
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 0;
		panel.add(bleachEstimationComboBox, gridBagConstraints_2);

		final JLabel frapParameterLabel = new JLabel();
		frapParameterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frapParameterLabel.setBorder(new LineBorder(Color.black, 1, false));
		frapParameterLabel.setText("Estimate Value");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.weightx = 1;
		gridBagConstraints_17.anchor = GridBagConstraints.EAST;
		gridBagConstraints_17.ipadx = 8;
		gridBagConstraints_17.fill = GridBagConstraints.BOTH;
		gridBagConstraints_17.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_17.gridy = 1;
		gridBagConstraints_17.gridx = 1;
		panel.add(frapParameterLabel, gridBagConstraints_17);

		final JLabel parameterLabel = new JLabel();
		parameterLabel.setBorder(new LineBorder(Color.black, 1, false));
		parameterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		parameterLabel.setText("Parameter Type");
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_19.fill = GridBagConstraints.BOTH;
		gridBagConstraints_19.gridy = 1;
		gridBagConstraints_19.gridx = 3;
		panel.add(parameterLabel, gridBagConstraints_19);

		final JLabel valueLabel = new JLabel();
		valueLabel.setBorder(new LineBorder(Color.black, 1, false));
		valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		valueLabel.setText("Value");
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.weightx = 1;
		gridBagConstraints_20.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_20.fill = GridBagConstraints.BOTH;
		gridBagConstraints_20.gridy = 1;
		gridBagConstraints_20.gridx = 4;
		panel.add(valueLabel, gridBagConstraints_20);

		final JLabel unitsLabel = new JLabel();
		unitsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		unitsLabel.setBorder(new LineBorder(Color.black, 1, false));
		unitsLabel.setText("Units");
		final GridBagConstraints gridBagConstraints_31 = new GridBagConstraints();
		gridBagConstraints_31.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_31.fill = GridBagConstraints.BOTH;
		gridBagConstraints_31.gridy = 1;
		gridBagConstraints_31.gridx = 5;
		panel.add(unitsLabel, gridBagConstraints_31);

		final JLabel estimatedDiffusionRateLabel = new JLabel();
		estimatedDiffusionRateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		estimatedDiffusionRateLabel.setBorder(new LineBorder(Color.black, 1, false));
		estimatedDiffusionRateLabel.setText("Estimated Diffusion Rate:");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.BOTH;
		gridBagConstraints_7.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_7.anchor = GridBagConstraints.EAST;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 0;
		panel.add(estimatedDiffusionRateLabel, gridBagConstraints_7);

		diffusionRateEstimateLabel = new JLabel();
		diffusionRateEstimateLabel.setText("1.0");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.anchor = GridBagConstraints.EAST;
		gridBagConstraints_4.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_4.gridy = 2;
		gridBagConstraints_4.gridx = 1;
		panel.add(diffusionRateEstimateLabel, gridBagConstraints_4);

		final JButton diffusionRateUseButton = new JButton();
		diffusionRateUseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				diffusionRateTextField.setText(diffusionRateEstimateLabel.getText());
			}
		});
		diffusionRateUseButton.setMargin(new Insets(2, 2, 2, 2));
		diffusionRateUseButton.setText("Use");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(4, 0, 4, 10);
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.gridx = 2;
		panel.add(diffusionRateUseButton, gridBagConstraints_8);

		final JLabel diffusionRateLabel = new JLabel();
		diffusionRateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		diffusionRateLabel.setBorder(new LineBorder(Color.black, 2, false));
		diffusionRateLabel.setText("Diffusion Rate");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_18.fill = GridBagConstraints.BOTH;
		gridBagConstraints_18.gridy = 2;
		gridBagConstraints_18.gridx = 3;
		panel.add(diffusionRateLabel, gridBagConstraints_18);

		diffusionRateTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_12.weightx = 0;
		gridBagConstraints_12.gridy = 2;
		gridBagConstraints_12.gridx = 4;
		panel.add(diffusionRateTextField, gridBagConstraints_12);

		final JLabel umsecLabel = new JLabel();
		umsecLabel.setText("um2/s");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_14.gridy = 2;
		gridBagConstraints_14.gridx = 5;
		panel.add(umsecLabel, gridBagConstraints_14);

		final JLabel estimatedMobileFractionLabel = new JLabel();
		estimatedMobileFractionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		estimatedMobileFractionLabel.setBorder(new LineBorder(Color.black, 1, false));
		estimatedMobileFractionLabel.setText("Estimated Mobile Fraction:");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.BOTH;
		gridBagConstraints_11.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_11.anchor = GridBagConstraints.EAST;
		gridBagConstraints_11.gridy = 3;
		gridBagConstraints_11.gridx = 0;
		panel.add(estimatedMobileFractionLabel, gridBagConstraints_11);

		mobileFractionEstimateLabel = new JLabel();
		mobileFractionEstimateLabel.setText("1.0");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.EAST;
		gridBagConstraints_5.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_5.gridy = 3;
		gridBagConstraints_5.gridx = 1;
		panel.add(mobileFractionEstimateLabel, gridBagConstraints_5);

		final JButton copyMobileFractionUseButton = new JButton();
		copyMobileFractionUseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				mobileFractionTextField.setText(mobileFractionEstimateLabel.getText());
				immobileFractionValueJLabel.setText(immobileFractionEstimateLabel.getText());
			}
		});
		copyMobileFractionUseButton.setMargin(new Insets(2, 2, 2, 2));
		copyMobileFractionUseButton.setText("Use");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(4, 0, 4, 10);
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.gridy = 3;
		gridBagConstraints_9.gridx = 2;
		panel.add(copyMobileFractionUseButton, gridBagConstraints_9);

		final JLabel mobileFractionLabel = new JLabel();
		mobileFractionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mobileFractionLabel.setBorder(new LineBorder(Color.black, 2, false));
		mobileFractionLabel.setText("Mobile Fraction");
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_21.fill = GridBagConstraints.BOTH;
		gridBagConstraints_21.gridy = 3;
		gridBagConstraints_21.gridx = 3;
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
		gridBagConstraints_13.gridy = 3;
		gridBagConstraints_13.gridx = 4;
		panel.add(mobileFractionTextField, gridBagConstraints_13);

		final JLabel label = new JLabel();
		label.setText("1/s");
		final GridBagConstraints gridBagConstraints_32 = new GridBagConstraints();
		gridBagConstraints_32.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_32.gridy = 3;
		gridBagConstraints_32.gridx = 5;
		panel.add(label, gridBagConstraints_32);

		final JLabel estimatedImmobileFractionLabel = new JLabel();
		estimatedImmobileFractionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		estimatedImmobileFractionLabel.setBorder(new LineBorder(Color.black, 1, false));
		estimatedImmobileFractionLabel.setText("Estimated Immobile Fraction:");
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.fill = GridBagConstraints.BOTH;
		gridBagConstraints_16.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_16.anchor = GridBagConstraints.EAST;
		gridBagConstraints_16.gridy = 4;
		gridBagConstraints_16.gridx = 0;
		panel.add(estimatedImmobileFractionLabel, gridBagConstraints_16);

		immobileFractionEstimateLabel = new JLabel();
		immobileFractionEstimateLabel.setText("0.0");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.EAST;
		gridBagConstraints_6.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_6.gridy = 4;
		gridBagConstraints_6.gridx = 1;
		panel.add(immobileFractionEstimateLabel, gridBagConstraints_6);

		final JLabel immobileFractionLabel = new JLabel();
		immobileFractionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		immobileFractionLabel.setBorder(new LineBorder(Color.black, 2, false));
		immobileFractionLabel.setText("Immobile Fraction");
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_22.fill = GridBagConstraints.BOTH;
		gridBagConstraints_22.gridy = 4;
		gridBagConstraints_22.gridx = 3;
		panel.add(immobileFractionLabel, gridBagConstraints_22);

		immobileFractionValueJLabel = new JLabel();
		immobileFractionValueJLabel.setText("ImmobileFrac");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_10.gridy = 4;
		gridBagConstraints_10.gridx = 4;
		panel.add(immobileFractionValueJLabel, gridBagConstraints_10);

		final JLabel label_1 = new JLabel();
		label_1.setText("1/s");
		final GridBagConstraints gridBagConstraints_33 = new GridBagConstraints();
		gridBagConstraints_33.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_33.gridy = 4;
		gridBagConstraints_33.gridx = 5;
		panel.add(label_1, gridBagConstraints_33);

		final JLabel monitorBleachRateLabel = new JLabel();
		monitorBleachRateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		monitorBleachRateLabel.setBorder(new LineBorder(Color.black, 2, false));
		monitorBleachRateLabel.setText("Monitor Bleach Rate");
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.ipadx = 8;
		gridBagConstraints_23.fill = GridBagConstraints.BOTH;
		gridBagConstraints_23.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_23.gridy = 5;
		gridBagConstraints_23.gridx = 3;
		panel.add(monitorBleachRateLabel, gridBagConstraints_23);

		monitorBleachRateTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.gridy = 5;
		gridBagConstraints_15.gridx = 4;
		panel.add(monitorBleachRateTextField, gridBagConstraints_15);

		final JLabel um2sLabel = new JLabel();
		um2sLabel.setText("um2/s");
		final GridBagConstraints gridBagConstraints_34 = new GridBagConstraints();
		gridBagConstraints_34.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_34.gridy = 5;
		gridBagConstraints_34.gridx = 5;
		panel.add(um2sLabel, gridBagConstraints_34);

		final JLabel slowerDiffMobileLabel = new JLabel();
		slowerDiffMobileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		slowerDiffMobileLabel.setBorder(new LineBorder(Color.black, 2, false));
		slowerDiffMobileLabel.setText("Slower Diff, Mobile");
		final GridBagConstraints gridBagConstraints_35 = new GridBagConstraints();
		gridBagConstraints_35.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_35.fill = GridBagConstraints.BOTH;
		gridBagConstraints_35.gridy = 6;
		gridBagConstraints_35.gridx = 3;
		panel.add(slowerDiffMobileLabel, gridBagConstraints_35);

		slowerTextField = new JTextField();
		final GridBagConstraints gridBagConstraints_36 = new GridBagConstraints();
		gridBagConstraints_36.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_36.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_36.gridy = 6;
		gridBagConstraints_36.gridx = 4;
		panel.add(slowerTextField, gridBagConstraints_36);

		final JLabel um2sLabel_1 = new JLabel();
		um2sLabel_1.setText("um2/s");
		final GridBagConstraints gridBagConstraints_37 = new GridBagConstraints();
		gridBagConstraints_37.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_37.gridy = 6;
		gridBagConstraints_37.gridx = 5;
		panel.add(um2sLabel_1, gridBagConstraints_37);

		final JLabel estimatedStartIndexLabel = new JLabel();
		estimatedStartIndexLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		estimatedStartIndexLabel.setBorder(new LineBorder(Color.black, 1, false));
		estimatedStartIndexLabel.setText("Estimated Start Time Recovery:");
		final GridBagConstraints gridBagConstraints_38 = new GridBagConstraints();
		gridBagConstraints_38.fill = GridBagConstraints.BOTH;
		gridBagConstraints_38.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_38.gridy = 7;
		gridBagConstraints_38.gridx = 0;
		panel.add(estimatedStartIndexLabel, gridBagConstraints_38);

		startTimeRecoveryEstimateLabel = new JLabel();
		startTimeRecoveryEstimateLabel.setText("0.0");
		final GridBagConstraints gridBagConstraints_39 = new GridBagConstraints();
		gridBagConstraints_39.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_39.anchor = GridBagConstraints.EAST;
		gridBagConstraints_39.gridy = 7;
		gridBagConstraints_39.gridx = 1;
		panel.add(startTimeRecoveryEstimateLabel, gridBagConstraints_39);

		final JButton copyStartIndexRecoveryUseButton = new JButton();
		copyStartIndexRecoveryUseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				for (int i = 0; i < frapDataTimesComboBox.getItemCount(); i++) {
					if(frapDataTimesComboBox.getItemAt(i).toString().equals(startTimeRecoveryEstimateLabel.getText())){
						frapDataTimesComboBox.setSelectedIndex(i);
						break;
					}
				}
			}
		});
		copyStartIndexRecoveryUseButton.setMargin(new Insets(2, 2, 2, 2));
		copyStartIndexRecoveryUseButton.setText("Use");
		final GridBagConstraints gridBagConstraints_40 = new GridBagConstraints();
		gridBagConstraints_40.insets = new Insets(4, 0, 4, 10);
		gridBagConstraints_40.gridy = 7;
		gridBagConstraints_40.gridx = 2;
		panel.add(copyStartIndexRecoveryUseButton, gridBagConstraints_40);

		final JLabel startIndexRecoveryLabel = new JLabel();
		startIndexRecoveryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		startIndexRecoveryLabel.setBorder(new LineBorder(Color.black, 2, false));
		startIndexRecoveryLabel.setText("Start Time Recovery");
		final GridBagConstraints gridBagConstraints_41 = new GridBagConstraints();
		gridBagConstraints_41.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_41.fill = GridBagConstraints.BOTH;
		gridBagConstraints_41.gridy = 7;
		gridBagConstraints_41.gridx = 3;
		panel.add(startIndexRecoveryLabel, gridBagConstraints_41);

		frapDataTimesComboBox = new JComboBox();
		final GridBagConstraints gridBagConstraints_42 = new GridBagConstraints();
		gridBagConstraints_42.fill = GridBagConstraints.BOTH;
		gridBagConstraints_42.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_42.gridy = 7;
		gridBagConstraints_42.gridx = 4;
		panel.add(frapDataTimesComboBox, gridBagConstraints_42);

		final JLabel sLabel = new JLabel();
		sLabel.setText("s");
		final GridBagConstraints gridBagConstraints_43 = new GridBagConstraints();
		gridBagConstraints_43.gridy = 7;
		gridBagConstraints_43.gridx = 5;
		panel.add(sLabel, gridBagConstraints_43);

		final JPanel panel_2 = new JPanel();
		final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
		gridBagConstraints_28.fill = GridBagConstraints.BOTH;
		gridBagConstraints_28.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_28.weightx = 1;
		gridBagConstraints_28.weighty = 1;
		gridBagConstraints_28.gridwidth = 3;
		gridBagConstraints_28.gridy = 8;
		gridBagConstraints_28.gridx = 0;
		panel.add(panel_2, gridBagConstraints_28);

		final JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(Color.black, 1, false));
		panel_1.setPreferredSize(new Dimension(450, 250));
		panel_1.setMinimumSize(new Dimension(450, 250));
		final GridBagLayout gridBagLayout_2 = new GridBagLayout();
		gridBagLayout_2.rowHeights = new int[] {0,7};
		panel_1.setLayout(gridBagLayout_2);
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_24.gridy = 1;
		gridBagConstraints_24.gridx = 0;
		add(panel_1, gridBagConstraints_24);

		frapModelParameterLabel = new JLabel();
		frapModelParameterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frapModelParameterLabel.setBorder(new LineBorder(Color.black, 1, false));
		frapModelParameterLabel.setText(PARAM_EST_EQUATION_STRING);
		final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_25.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_25.weighty = 0;
		gridBagConstraints_25.gridy = 0;
		gridBagConstraints_25.gridx = 0;
		panel_1.add(frapModelParameterLabel, gridBagConstraints_25);

		expressionCanvas = new ExpressionCanvas();
		final GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
		gridBagConstraints_26.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_26.fill = GridBagConstraints.BOTH;
		gridBagConstraints_26.weighty = 1;
		gridBagConstraints_26.weightx = 1;
		gridBagConstraints_26.gridy = 1;
		gridBagConstraints_26.gridx = 0;
		panel_1.add(expressionCanvas, gridBagConstraints_26);

		plotOfAverageLabel = new JLabel();
		plotOfAverageLabel.setFont(new Font("", Font.BOLD, 14));
		plotOfAverageLabel.setText(PLOT_TITLE_STRING);
		final GridBagConstraints gridBagConstraints_29 = new GridBagConstraints();
		gridBagConstraints_29.insets = new Insets(20, 4, 4, 4);
		gridBagConstraints_29.gridwidth = 2;
		gridBagConstraints_29.gridy = 2;
		gridBagConstraints_29.gridx = 0;
		add(plotOfAverageLabel, gridBagConstraints_29);

		multisourcePlotPane = new MultisourcePlotPane();
		multisourcePlotPane.setBorder(new LineBorder(Color.black, 1, false));
		multisourcePlotPane.setListVisible(false);
		final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_27.fill = GridBagConstraints.BOTH;
		gridBagConstraints_27.weighty = 1;
		gridBagConstraints_27.weightx = 1;
		gridBagConstraints_27.gridwidth = 2;
		gridBagConstraints_27.gridy = 3;
		gridBagConstraints_27.gridx = 0;
		add(multisourcePlotPane, gridBagConstraints_27);
		
		initialize();
	}

	private void initialize(){
		for (int i = 0; i < FrapDataAnalysisResults.BLEACH_TYPE_NAMES.length; i++) {
			bleachEstimationComboBox.insertItemAt("Estimation method '"+FrapDataAnalysisResults.BLEACH_TYPE_NAMES[i]+"'", i);
		}
		bleachEstimationComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if(bleachEstimationComboBox.getSelectedIndex() == FrapDataAnalysisResults.BleachType_CirularDisk){
					//expression on canvas
					try{
						String[] prefixes = new String[] { "I(t) = ", "D = " };
						Expression[] expressions = new Expression[] { new Expression("If-(If-Io)*exp(-t/tau)"), new Expression("w^2/(4*tau)") };
						String[] suffixes = new String[] { "", "[um2.s-1]" };
						expressionCanvas.setExpressions(expressions,prefixes,suffixes);
					}catch (ExpressionException e2){
						e2.printStackTrace(System.out);
					}					
				}else if(bleachEstimationComboBox.getSelectedIndex() == FrapDataAnalysisResults.BleachType_GaussianSpot){
					//expression on canvas
					try{
						String[] prefixes = new String[] { "I(t) = ", "u(t)= ","D = " };
						Expression[] expressions = new Expression[] { new Expression("If*(1-fB)-(If*(1-fB)-Io)*(R*u+1-R)"), new Expression("(1+t/tau)^-1"), new Expression("w^2/(4*tau)") };
						String[] suffixes = new String[] { "", "", "[um2.s-1]" };
						expressionCanvas.setExpressions(expressions,prefixes,suffixes);
					}catch (ExpressionException e2){
						e2.printStackTrace(System.out);
					}
					
				}else if(bleachEstimationComboBox.getSelectedIndex() == FrapDataAnalysisResults.BleachType_HalfCell){
					//expression on canvas
					try{
						String[] prefixes = new String[] { "I(t) = ", "u(t)= ","D = " };
						Expression[] expressions = new Expression[] { new Expression("If*(1-fB)-(If*(1-fB)-Io)*(R*u+1-R)"), new Expression("exp(-t/tau)"), new Expression("tau*Pai^2/r^2") };
						String[] suffixes = new String[] { "", "", "[um2.s-1]" };
						expressionCanvas.setExpressions(expressions,prefixes,suffixes);
					}catch (ExpressionException e2){
						e2.printStackTrace(System.out);
					}
				}
				frapModelParameterLabel.setText(
					PARAM_EST_EQUATION_STRING+"  ('"+
					FrapDataAnalysisResults.BLEACH_TYPE_NAMES[bleachEstimationComboBox.getSelectedIndex()]+"')");
				plotOfAverageLabel.setText(
					PLOT_TITLE_STRING+"  ('"+
					FrapDataAnalysisResults.BLEACH_TYPE_NAMES[bleachEstimationComboBox.getSelectedIndex()]+"')");
				try{
					refreshFRAPModelParameterEstimates(initFRAPData);
				}catch (Exception e2){
					//ignore
				}
			}
		});
		bleachEstimationComboBox.setSelectedIndex(FrapDataAnalysisResults.BleachType_CirularDisk);
	}
	private int getBleachTypeMethod(){
		return bleachEstimationComboBox.getSelectedIndex();
	}
//	public void setFRAPStudy(FRAPStudy frapStudy){
//		this.frapStudy = frapStudy;
//		multisourcePlotPane.setDataSources(null);
//	}
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
	public void initializeSavedFrapModelInfo(FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfo,FRAPData initFRAPData){
//		this.savedFrapModelInfo = savedFrapModelInfo;
		this.initFRAPData = initFRAPData;
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
		double[] frapDataTimeStamps = initFRAPData.getImageDataset().getImageTimeStamps();
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
				immobileFractionValueJLabel.setText(""+Double.parseDouble(NumberUtils.formatNumber(immobileFractionIntermediate, 5)));
			}else{
				immobileFractionValueJLabel.setText("");
			}
		}catch(Exception e2){
			immobileFractionValueJLabel.setText("");
		}

	}
	private void displayFit(FrapDataAnalysisResults frapDataAnalysisResults,double[] frapDataTimeStamps){
		if (frapDataAnalysisResults == null){
			diffusionRateEstimateLabel.setText("");
			mobileFractionEstimateLabel.setText("");
			immobileFractionEstimateLabel.setText("");
			multisourcePlotPane.setDataSources(null);
		}else{
//			FrapDataAnalysisResults frapDataAnalysisResults = frapStudy.getFrapDataAnalysisResults();
			diffusionRateEstimateLabel.setText(
				(frapDataAnalysisResults.getRecoveryDiffusionRate() == null
					?""
					:frapDataAnalysisResults.getRecoveryDiffusionRate().toString()));
			mobileFractionEstimateLabel.setText(
					(frapDataAnalysisResults.getMobilefraction() == null
						?""
						:frapDataAnalysisResults.getMobilefraction().toString()));
			if(mobileFractionEstimateLabel.getText().length() > 0){
				immobileFractionEstimateLabel.setText(""+(1.0-new Double(mobileFractionEstimateLabel.getText()).doubleValue()));
			}
//			double[] frapDataTimeStamps = frapStudy.getFrapData().getImageDataset().getImageTimeStamps();
			double[] bleachRegionData = frapDataAnalysisResults.getBleachRegionData();
			int startIndexForRecovery = frapDataAnalysisResults.getStartingIndexForRecovery();
			Expression fittedCurve = frapDataAnalysisResults.getFitExpression();
			ReferenceData expRefData = new SimpleReferenceData(new String[] { "t", "UserDataBleachROIAvg" }, new double[] { 1.0, 1.0 }, new double[][] { frapDataTimeStamps, bleachRegionData });
			DataSource expDataSource = new DataSource(expRefData,"experiment");
			ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
			try {
				fitOdeSolverResultSet.addFunctionColumn(
					new FunctionColumnDescription(
						fittedCurve,
						"fit ('"+FrapDataAnalysisResults.BLEACH_TYPE_NAMES[bleachEstimationComboBox.getSelectedIndex()]+"')",
						null,"recoveryFit",true));
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
			for (int i = startIndexForRecovery; i < frapDataTimeStamps.length; i++) {
				fitOdeSolverResultSet.addRow(new double[] { frapDataTimeStamps[i] });
			}
			//
			// extend if necessary to plot theoretical curve to 4*tau
			//
			double T = frapDataTimeStamps[frapDataTimeStamps.length-1];
			double deltaT = frapDataTimeStamps[frapDataTimeStamps.length-1]-frapDataTimeStamps[frapDataTimeStamps.length-2];
			while (T+deltaT < 6*frapDataAnalysisResults.getRecoveryTau()){
				fitOdeSolverResultSet.addRow(new double[] { T } );
				T += deltaT;
			}
			DataSource fitDataSource = new DataSource(fitOdeSolverResultSet, "fit");
			multisourcePlotPane.setDataSources(new DataSource[] {  expDataSource, fitDataSource } );
			multisourcePlotPane.selectAll();		
		}
	}
	
	public void refreshFRAPModelParameterEstimates(FRAPData frapData) throws Exception {
		FrapDataAnalysisResults frapDataAnalysisResults = null;
		double[] frapDataTimeStamps = null;
		bleachEstimationComboBox.setEnabled(false);
		if(frapData != null){
			if(frapData.getRoi(RoiType.ROI_BLEACHED).isAllPixelsZero()){
				displayFit(null,null);
				throw new Exception(
					OverlayEditorPanelJAI.INITIAL_BLEACH_AREA_TEXT+" ROI not defined.\n"+
					"Use ROI tools under '"+FRAPStudyPanel.FRAPDATAPANEL_TABNAME+"' tab to define.");
			}
			frapDataTimeStamps = frapData.getImageDataset().getImageTimeStamps();
			frapDataAnalysisResults =
				FRAPDataAnalysis.fitRecovery2(frapData, getBleachTypeMethod());
			startTimeRecoveryEstimateLabel.setText(frapDataTimeStamps[frapDataAnalysisResults.getStartingIndexForRecovery()]+"");
			bleachEstimationComboBox.setEnabled(true);
		}

		displayFit(frapDataAnalysisResults,frapDataTimeStamps);
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
