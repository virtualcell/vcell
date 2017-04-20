package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

import org.vcell.util.BeanUtils;

import cbit.gui.TextFieldAutoCompletion;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.TriggerType;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;

public class TriggerTemplatePanel extends JPanel {
	private TextFieldAutoCompletion textFieldSingleTime;
	private JTextField textFieldListTimes;
	private TextFieldAutoCompletion textFieldAboveVarVal;
	private TextFieldAutoCompletion textFieldBelowVarVal;
	
	private JPanel logTimeRangePanel;
	private JTextField textFieldLogMin;
	private JTextField textFieldLogMax;
	private JTextField textFieldLogNum;

	private JPanel linearTimeRangePanel;
	private JTextField textFieldLinearMin;
	private JTextField textFieldLinearMax;
	private JTextField textFieldLinearNum;
	
	private JComboBox<SymbolTableEntry> varAboveComboBox = null;
	private JComboBox<SymbolTableEntry> varBelowComboBox = null;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtnGeneral;
	private JRadioButton rdbtnVarAboveThreshold;
	private JRadioButton rdbtnVarBelowThreshold;
	private JRadioButton rdbtnSingleTime;
	private JRadioButton rdbtnListTimes;
	private JRadioButton rdbtnLinearTimeRange;
	private JRadioButton rdbtnLogTimeRange;
	private JPanel varAboveThresholdPanel;
	private JPanel varBelowThresholdPanel;
	private TextFieldAutoCompletion textFieldGeneral;
	
	private ItemListener rdbtnLItemListener =
			new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.DESELECTED){
						return;
					}
					textFieldGeneral.setEnabled(false);
					BeanUtils.enableComponents(varAboveThresholdPanel, false);
					BeanUtils.enableComponents(varBelowThresholdPanel, false);
					textFieldSingleTime.setEnabled(false);
					textFieldListTimes.setEnabled(false);
					BeanUtils.enableComponents(logTimeRangePanel, false);
					BeanUtils.enableComponents(linearTimeRangePanel, false);
					if(e.getSource() == rdbtnGeneral && rdbtnGeneral.isSelected()){
						textFieldGeneral.setEnabled(true);
					}else if(e.getSource() == rdbtnVarAboveThreshold && rdbtnVarAboveThreshold.isSelected()){
						BeanUtils.enableComponents(varAboveThresholdPanel, true);
					}else if(e.getSource() == rdbtnVarBelowThreshold && rdbtnVarBelowThreshold.isSelected()){
						BeanUtils.enableComponents(varBelowThresholdPanel, true);
					}else if(e.getSource() == rdbtnSingleTime && rdbtnSingleTime.isSelected()){
						textFieldSingleTime.setEnabled(true);
					}else if(e.getSource() == rdbtnListTimes && rdbtnListTimes.isSelected()){
						textFieldListTimes.setEnabled(true);
					}else if(e.getSource() == rdbtnLogTimeRange && rdbtnLogTimeRange.isSelected()){
						BeanUtils.enableComponents(logTimeRangePanel, true);
					}else if(e.getSource() == rdbtnLinearTimeRange && rdbtnLinearTimeRange.isSelected()){
						BeanUtils.enableComponents(linearTimeRangePanel, true);
					}
				}
			};
			
	public TriggerTemplatePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0, 0.0, 0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		int gridy = 0;
		
		//
		// Trigger Label
		//
		JLabel lblNewLabel_1 = new JLabel("Pre-Defined Trigger conditions");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.gridwidth = 2;
		gbc_lblNewLabel_1.insets = new Insets(4, 4, 5, 4);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = gridy;
		add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		gridy++;
		
		//==================================================================
		// at single time
		//==================================================================
		rdbtnSingleTime = new JRadioButton("at a single time");
		GridBagConstraints gbc_rdbtnSingleTime = new GridBagConstraints();
		gbc_rdbtnSingleTime.anchor = GridBagConstraints.WEST;
		gbc_rdbtnSingleTime.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnSingleTime.gridx = 0;
		gbc_rdbtnSingleTime.gridy = gridy;
		add(rdbtnSingleTime, gbc_rdbtnSingleTime);
		
		textFieldSingleTime = new TextFieldAutoCompletion();
		textFieldSingleTime.setText("0.0");
		GridBagConstraints gbc_textFieldSingleTime = new GridBagConstraints();
		gbc_textFieldSingleTime.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldSingleTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSingleTime.gridx = 1;
		gbc_textFieldSingleTime.gridy = gridy;
		add(textFieldSingleTime, gbc_textFieldSingleTime);
		textFieldSingleTime.setColumns(10);
		
		gridy++;
		
		//==================================================================
		// above upper limit
		//==================================================================
		rdbtnVarAboveThreshold = new JRadioButton("variable exceeds upper limit");
		GridBagConstraints gbc_rdbtnVarAboveThreshold = new GridBagConstraints();
		gbc_rdbtnVarAboveThreshold.anchor = GridBagConstraints.WEST;
		gbc_rdbtnVarAboveThreshold.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnVarAboveThreshold.gridx = 0;
		gbc_rdbtnVarAboveThreshold.gridy = gridy;
		add(rdbtnVarAboveThreshold, gbc_rdbtnVarAboveThreshold);
		
		varAboveThresholdPanel = new JPanel();
		GridBagConstraints gbc_varAboveThresholdPanel = new GridBagConstraints();
		gbc_varAboveThresholdPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_varAboveThresholdPanel.insets = new Insets(4, 4, 5, 4);
		gbc_varAboveThresholdPanel.gridx = 1;
		gbc_varAboveThresholdPanel.gridy = gridy;
		add(varAboveThresholdPanel, gbc_varAboveThresholdPanel);
		GridBagLayout gbl_varAboveThresholdPanel = new GridBagLayout();
		gbl_varAboveThresholdPanel.columnWidths = new int[]{0, 0, 0};
		gbl_varAboveThresholdPanel.rowHeights = new int[]{0};
		gbl_varAboveThresholdPanel.columnWeights = new double[]{0, 0.0, 0};
		gbl_varAboveThresholdPanel.rowWeights = new double[]{0.0};
		varAboveThresholdPanel.setLayout(gbl_varAboveThresholdPanel);

		varAboveComboBox = new JComboBox<SymbolTableEntry>();
		final ListCellRenderer origAboveListCellRenderer = varAboveComboBox.getRenderer();
		varAboveComboBox.setRenderer(new  ListCellRenderer<SymbolTableEntry>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends SymbolTableEntry> list, SymbolTableEntry value, int index,boolean isSelected, boolean cellHasFocus) {
				return origAboveListCellRenderer.getListCellRendererComponent(list, value.getName(), index, isSelected, cellHasFocus);
			}
		});
		GridBagConstraints gbc_varAboveComboBox = new GridBagConstraints();
		gbc_varAboveComboBox.insets = new Insets(4, 4, 5, 5);
		gbc_varAboveComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_varAboveComboBox.gridx = 0;
		gbc_varAboveComboBox.gridy = 0;
		varAboveThresholdPanel.add(varAboveComboBox, gbc_varAboveComboBox);
		
		textFieldAboveVarVal = new TextFieldAutoCompletion();
		GridBagConstraints gbc_textFieldAboveVarVal = new GridBagConstraints();
		gbc_textFieldAboveVarVal.weightx = 1.0;
		gbc_textFieldAboveVarVal.insets = new Insets(4, 4, 4, 4);
		gbc_textFieldAboveVarVal.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldAboveVarVal.gridx = 2;
		gbc_textFieldAboveVarVal.gridy = 0;
		varAboveThresholdPanel.add(textFieldAboveVarVal, gbc_textFieldAboveVarVal);
		textFieldAboveVarVal.setColumns(10);

		gridy++;
		
		//==================================================================
		// below lower limit
		//==================================================================
		rdbtnVarBelowThreshold = new JRadioButton("variable below lower limit");
		GridBagConstraints gbc_rdbtnVarBelowThreshold = new GridBagConstraints();
		gbc_rdbtnVarBelowThreshold.anchor = GridBagConstraints.WEST;
		gbc_rdbtnVarBelowThreshold.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnVarBelowThreshold.gridx = 0;
		gbc_rdbtnVarBelowThreshold.gridy = gridy;
		add(rdbtnVarBelowThreshold, gbc_rdbtnVarBelowThreshold);
		
		varBelowThresholdPanel = new JPanel();
		GridBagConstraints gbc_varBelowThresholdPanel = new GridBagConstraints();
		gbc_varBelowThresholdPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_varBelowThresholdPanel.insets = new Insets(4, 4, 5, 4);
		gbc_varBelowThresholdPanel.gridx = 1;
		gbc_varBelowThresholdPanel.gridy = gridy;
		add(varBelowThresholdPanel, gbc_varBelowThresholdPanel);
		GridBagLayout gbl_varBelowThresholdPanel = new GridBagLayout();
		gbl_varBelowThresholdPanel.columnWidths = new int[]{0, 0, 0};
		gbl_varBelowThresholdPanel.rowHeights = new int[]{0};
		gbl_varBelowThresholdPanel.columnWeights = new double[]{0, 0.0, 0};
		gbl_varBelowThresholdPanel.rowWeights = new double[]{0.0};
		varBelowThresholdPanel.setLayout(gbl_varBelowThresholdPanel);
		
		varBelowComboBox = new JComboBox<SymbolTableEntry>();
		final ListCellRenderer origListCellRenderer = varBelowComboBox.getRenderer();
		varBelowComboBox.setRenderer(new  ListCellRenderer<SymbolTableEntry>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends SymbolTableEntry> list, SymbolTableEntry value, int index,boolean isSelected, boolean cellHasFocus) {
				return origListCellRenderer.getListCellRendererComponent(list, value.getName(), index, isSelected, cellHasFocus);
			}
		});
		GridBagConstraints gbc_varComboBox = new GridBagConstraints();
		gbc_varComboBox.insets = new Insets(4, 4, 5, 5);
		gbc_varComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_varComboBox.gridx = 0;
		gbc_varComboBox.gridy = 0;
		varBelowThresholdPanel.add(varBelowComboBox, gbc_varComboBox);
		
		textFieldBelowVarVal = new TextFieldAutoCompletion();
		GridBagConstraints gbc_textFieldBelowVarVal = new GridBagConstraints();
		gbc_textFieldBelowVarVal.weightx = 1.0;
		gbc_textFieldBelowVarVal.insets = new Insets(4, 4, 4, 4);
		gbc_textFieldBelowVarVal.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldBelowVarVal.gridx = 2;
		gbc_textFieldBelowVarVal.gridy = 0;
		varBelowThresholdPanel.add(textFieldBelowVarVal, gbc_textFieldBelowVarVal);
		textFieldBelowVarVal.setColumns(10);
		
		gridy++;
		
		//==================================================================
		// at list of times
		//==================================================================
		rdbtnListTimes = new JRadioButton("list of times");
		GridBagConstraints gbc_rdbtnListTimes = new GridBagConstraints();
		gbc_rdbtnListTimes.anchor = GridBagConstraints.WEST;
		gbc_rdbtnListTimes.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnListTimes.gridx = 0;
		gbc_rdbtnListTimes.gridy = gridy;
		add(rdbtnListTimes, gbc_rdbtnListTimes);
		
		textFieldListTimes = new JTextField();
		textFieldListTimes.setText("0.0");
		GridBagConstraints gbc_textFieldListTimes = new GridBagConstraints();
		gbc_textFieldListTimes.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldListTimes.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldListTimes.gridx = 1;
		gbc_textFieldListTimes.gridy = gridy;
		add(textFieldListTimes, gbc_textFieldListTimes);
		textFieldListTimes.setColumns(10);
		
		
		{
		gridy++;
		
		//==================================================================
		// linear time range
		//==================================================================
		rdbtnLinearTimeRange = new JRadioButton("linear time range");
		GridBagConstraints gbc_rdbtnLinearTimeRange = new GridBagConstraints();
		gbc_rdbtnLinearTimeRange.insets = new Insets(4, 4, 4, 5);
		gbc_rdbtnLinearTimeRange.anchor = GridBagConstraints.WEST;
		gbc_rdbtnLinearTimeRange.gridx = 0;
		gbc_rdbtnLinearTimeRange.gridy = gridy;
		add(rdbtnLinearTimeRange, gbc_rdbtnLinearTimeRange);
		
		linearTimeRangePanel = new JPanel();
		linearTimeRangePanel.setBorder(new LineBorder(Color.black));
		GridBagConstraints gbc_LinearTimeRangePanel = new GridBagConstraints();
		gbc_LinearTimeRangePanel.fill = GridBagConstraints.BOTH;
		gbc_LinearTimeRangePanel.gridx = 1;
		gbc_LinearTimeRangePanel.gridy = gridy;
		add(linearTimeRangePanel, gbc_LinearTimeRangePanel);
		GridBagLayout gbl_linearTimeRangePanel = new GridBagLayout();
		gbl_linearTimeRangePanel.columnWidths = new int[]{0,0};
		gbl_linearTimeRangePanel.rowHeights = new int[]{0,0,0};
		gbl_linearTimeRangePanel.columnWeights = new double[]{Double.MIN_VALUE,0};
		gbl_linearTimeRangePanel.rowWeights = new double[]{0.0, 0.0, 0.0};
		linearTimeRangePanel.setLayout(gbl_linearTimeRangePanel);

		JLabel linearMinLabel = new JLabel("Min");
		GridBagConstraints gbc_linearMinLabel = new GridBagConstraints();
		gbc_linearMinLabel.fill = GridBagConstraints.NONE;
		gbc_linearMinLabel.gridx = 0;
		gbc_linearMinLabel.gridy = 0;
		linearTimeRangePanel.add(linearMinLabel, gbc_linearMinLabel);

		JLabel linearMaxLabel = new JLabel("Max");
		GridBagConstraints gbc_linearMaxLabel = new GridBagConstraints();
		gbc_linearMaxLabel.fill = GridBagConstraints.NONE;
		gbc_linearMaxLabel.gridx = 0;
		gbc_linearMaxLabel.gridy = 1;
		linearTimeRangePanel.add(linearMaxLabel, gbc_linearMaxLabel);

		JLabel linearNumLabel = new JLabel("# values");
		GridBagConstraints gbc_linearNumLabel = new GridBagConstraints();
		gbc_linearNumLabel.fill = GridBagConstraints.NONE;
		gbc_linearNumLabel.gridx = 0;
		gbc_linearNumLabel.gridy = 2;
		linearTimeRangePanel.add(linearNumLabel, gbc_linearNumLabel);
		
		textFieldLinearMin = new JTextField();
		textFieldLinearMin.setText("1.0");
		GridBagConstraints gbc_textFieldLinearMin = new GridBagConstraints();
		gbc_textFieldLinearMin.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldLinearMin.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldLinearMin.weightx = 1;
		gbc_textFieldLinearMin.gridx = 1;
		gbc_textFieldLinearMin.gridy = 0;
		linearTimeRangePanel.add(textFieldLinearMin, gbc_textFieldLinearMin);
		textFieldLinearMin.setColumns(10);

		textFieldLinearMax = new JTextField();
		textFieldLinearMax.setText("100.0");
		GridBagConstraints gbc_textFieldLinearMax = new GridBagConstraints();
		gbc_textFieldLinearMax.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldLinearMax.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldLinearMax.weightx = 1;
		gbc_textFieldLinearMax.gridx = 1;
		gbc_textFieldLinearMax.gridy = 1;
		linearTimeRangePanel.add(textFieldLinearMax, gbc_textFieldLinearMax);
		textFieldLinearMax.setColumns(10);

		textFieldLinearNum = new JTextField();
		textFieldLinearNum.setText("3");
		GridBagConstraints gbc_textFieldLinearNum = new GridBagConstraints();
		gbc_textFieldLinearNum.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldLinearNum.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldLinearNum.weightx = 1;
		gbc_textFieldLinearNum.gridx = 1;
		gbc_textFieldLinearNum.gridy = 2;
		linearTimeRangePanel.add(textFieldLinearNum, gbc_textFieldLinearNum);
		textFieldLinearNum.setColumns(10);
		}
		
		{	
		gridy++;
			
		//==================================================================
		// log time range
		//==================================================================
		rdbtnLogTimeRange = new JRadioButton("log time range");
		GridBagConstraints gbc_rdbtnLogTimeRange = new GridBagConstraints();
		gbc_rdbtnLogTimeRange.insets = new Insets(4, 4, 4, 5);
		gbc_rdbtnLogTimeRange.anchor = GridBagConstraints.WEST;
		gbc_rdbtnLogTimeRange.gridx = 0;
		gbc_rdbtnLogTimeRange.gridy = gridy;
		add(rdbtnLogTimeRange, gbc_rdbtnLogTimeRange);
		
		logTimeRangePanel = new JPanel();
		logTimeRangePanel.setBorder(new LineBorder(Color.black));
		GridBagConstraints gbc_logTimeRangePanel = new GridBagConstraints();
		gbc_logTimeRangePanel.fill = GridBagConstraints.BOTH;
		gbc_logTimeRangePanel.gridx = 1;
		gbc_logTimeRangePanel.gridy = gridy;
		add(logTimeRangePanel, gbc_logTimeRangePanel);
		GridBagLayout gbl_logTimeRangePanel = new GridBagLayout();
		gbl_logTimeRangePanel.columnWidths = new int[]{0,0};
		gbl_logTimeRangePanel.rowHeights = new int[]{0,0,0};
		gbl_logTimeRangePanel.columnWeights = new double[]{Double.MIN_VALUE,0};
		gbl_logTimeRangePanel.rowWeights = new double[]{0.0, 0.0, 0.0};
		logTimeRangePanel.setLayout(gbl_logTimeRangePanel);

		JLabel logMinLabel = new JLabel("Min");
		GridBagConstraints gbc_logMinLabel = new GridBagConstraints();
		gbc_logMinLabel.fill = GridBagConstraints.NONE;
		gbc_logMinLabel.gridx = 0;
		gbc_logMinLabel.gridy = 0;
		logTimeRangePanel.add(logMinLabel, gbc_logMinLabel);

		JLabel logMaxLabel = new JLabel("Max");
		GridBagConstraints gbc_logMaxLabel = new GridBagConstraints();
		gbc_logMaxLabel.fill = GridBagConstraints.NONE;
		gbc_logMaxLabel.gridx = 0;
		gbc_logMaxLabel.gridy = 1;
		logTimeRangePanel.add(logMaxLabel, gbc_logMaxLabel);

		JLabel logNumLabel = new JLabel("# values");
		GridBagConstraints gbc_logNumLabel = new GridBagConstraints();
		gbc_logNumLabel.fill = GridBagConstraints.NONE;
		gbc_logNumLabel.gridx = 0;
		gbc_logNumLabel.gridy = 2;
		logTimeRangePanel.add(logNumLabel, gbc_logNumLabel);
		
		textFieldLogMin = new JTextField();
		textFieldLogMin.setText("1.0");
		GridBagConstraints gbc_textFieldLogMin = new GridBagConstraints();
		gbc_textFieldLogMin.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldLogMin.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldLogMin.weightx = 1;
		gbc_textFieldLogMin.gridx = 1;
		gbc_textFieldLogMin.gridy = 0;
		logTimeRangePanel.add(textFieldLogMin, gbc_textFieldLogMin);
		textFieldLogMin.setColumns(10);

		textFieldLogMax = new JTextField();
		textFieldLogMax.setText("100.0");
		GridBagConstraints gbc_textFieldLogMax = new GridBagConstraints();
		gbc_textFieldLogMax.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldLogMax.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldLogMax.weightx = 1;
		gbc_textFieldLogMax.gridx = 1;
		gbc_textFieldLogMax.gridy = 1;
		logTimeRangePanel.add(textFieldLogMax, gbc_textFieldLogMax);
		textFieldLogMax.setColumns(10);

		textFieldLogNum = new JTextField();
		textFieldLogNum.setText("3");
		GridBagConstraints gbc_textFieldLogNum = new GridBagConstraints();
		gbc_textFieldLogNum.insets = new Insets(4, 4, 5, 4);
		gbc_textFieldLogNum.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldLogNum.weightx = 1;
		gbc_textFieldLogNum.gridx = 1;
		gbc_textFieldLogNum.gridy = 2;
		logTimeRangePanel.add(textFieldLogNum, gbc_textFieldLogNum);
		textFieldLogNum.setColumns(10);
		}
				
		gridy++;

		//==================================================================
		// GENERAL TRIGGER
		//==================================================================
		rdbtnGeneral = new JRadioButton("when condition becomes true");
		rdbtnGeneral.setSelected(true);
		GridBagConstraints gbc_rdbtnGeneral = new GridBagConstraints();
		gbc_rdbtnGeneral.anchor = GridBagConstraints.WEST;
		gbc_rdbtnGeneral.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnGeneral.gridx = 0;
		gbc_rdbtnGeneral.gridy = gridy;
		add(rdbtnGeneral, gbc_rdbtnGeneral);	
		textFieldGeneral = new TextFieldAutoCompletion();
//		textFieldGeneral.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				textFieldGeneral.getInputVerifier().verify(textFieldGeneral);
//			}
//		});
		GridBagConstraints gbc_textFieldGeneral = new GridBagConstraints();
		gbc_textFieldGeneral.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldGeneral.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldGeneral.gridx = 1;
		gbc_textFieldGeneral.gridy = gridy;
		add(textFieldGeneral, gbc_textFieldGeneral);
		textFieldGeneral.setColumns(10);
		
		gridy++;
		
		buttonGroup.add(rdbtnGeneral);
		buttonGroup.add(rdbtnVarAboveThreshold);
		buttonGroup.add(rdbtnVarBelowThreshold);
		buttonGroup.add(rdbtnSingleTime);
		buttonGroup.add(rdbtnListTimes);
		buttonGroup.add(rdbtnLinearTimeRange);
		buttonGroup.add(rdbtnLogTimeRange);
		
		rdbtnLItemListener.itemStateChanged(new ItemEvent(rdbtnGeneral, 0, rdbtnGeneral, ItemEvent.SELECTED));
		
		rdbtnGeneral.addItemListener(rdbtnLItemListener);
		rdbtnVarAboveThreshold.addItemListener(rdbtnLItemListener);
		rdbtnVarBelowThreshold.addItemListener(rdbtnLItemListener);
		rdbtnSingleTime.addItemListener(rdbtnLItemListener);
		rdbtnListTimes.addItemListener(rdbtnLItemListener);
		rdbtnLinearTimeRange.addItemListener(rdbtnLItemListener);
		rdbtnLogTimeRange.addItemListener(rdbtnLItemListener);
	}

	public void init(SimulationContext simulationContext,AutoCompleteSymbolFilter autoCompleteSymbolFilter,BioEvent existingBioEvent){
		if(simulationContext != null){
//			mathOpComboBox.removeAllItems();
//			mathOpComboBox.addItem(BioEvent.TriggerComparison.greaterThan);
//			mathOpComboBox.addItem(BioEvent.TriggerComparison.lessThan);
//			mathOpComboBox.addItem(BioEvent.TriggerComparison.greaterThanOrEqual);
//			mathOpComboBox.addItem(BioEvent.TriggerComparison.lessThanOrEqual);
			
			varAboveComboBox.removeAllItems();
			varBelowComboBox.removeAllItems();
			SymbolTableEntry[] triggerExprSymbols = ModelOptimizationSpec.calculateTimeDependentModelObjects(simulationContext);
			Arrays.sort(triggerExprSymbols, new Comparator<SymbolTableEntry>() {
				@Override
				public int compare(SymbolTableEntry o1, SymbolTableEntry o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			for(SymbolTableEntry ste:triggerExprSymbols){
				if(!(ste instanceof ReservedSymbol)){
					varAboveComboBox.addItem(ste);
					varBelowComboBox.addItem(ste);
				}
			}
//			setVerifier(simulationContext);
			
			Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
			simulationContext.getEntries(entryMap);
			textFieldAboveVarVal.setAutoCompleteSymbolFilter(autoCompleteSymbolFilter);
			textFieldAboveVarVal.setAutoCompletionWords(entryMap.keySet());
			textFieldBelowVarVal.setAutoCompleteSymbolFilter(autoCompleteSymbolFilter);
			textFieldBelowVarVal.setAutoCompletionWords(entryMap.keySet());
			textFieldSingleTime.setAutoCompleteSymbolFilter(autoCompleteSymbolFilter);
			textFieldSingleTime.setAutoCompletionWords(entryMap.keySet());
			textFieldGeneral.setAutoCompleteSymbolFilter(autoCompleteSymbolFilter);
			textFieldGeneral.setAutoCompletionWords(entryMap.keySet());
			
			if(existingBioEvent != null){//Edit Trigger mode
				switch (existingBioEvent.getTriggerType()){
				case GeneralTrigger:{
					textFieldGeneral.setText(existingBioEvent.getParameter(BioEventParameterType.GeneralTriggerFunction).getExpression().infix());
					rdbtnGeneral.setSelected(true);
					break;
				}
				case LogRangeTimes:{
					LocalParameter rangeMinParam = existingBioEvent.getParameter(BioEventParameterType.RangeMinTime);
					if (rangeMinParam!=null && rangeMinParam.getExpression()!=null){
						textFieldLogMin.setText(rangeMinParam.getExpression().infix());
					}else{
						textFieldLogMin.setText("");
					}
					LocalParameter rangeMaxParam = existingBioEvent.getParameter(BioEventParameterType.RangeMaxTime);
					if (rangeMaxParam!=null && rangeMaxParam.getExpression()!=null){
						textFieldLogMax.setText(rangeMaxParam.getExpression().infix());
					}else{
						textFieldLogMax.setText("");
					}
					LocalParameter rangeNumParam = existingBioEvent.getParameter(BioEventParameterType.RangeNumTimes);
					if (rangeNumParam!=null && rangeNumParam.getExpression()!=null){
						textFieldLogNum.setText(rangeNumParam.getExpression().infix());
					}else{
						textFieldLogNum.setText("");
					}
					rdbtnLogTimeRange.setSelected(true);
					break;
				}
				case LinearRangeTimes:{
					LocalParameter rangeMinParam = existingBioEvent.getParameter(BioEventParameterType.RangeMinTime);
					if (rangeMinParam!=null && rangeMinParam.getExpression()!=null){
						textFieldLinearMin.setText(rangeMinParam.getExpression().infix());
					}else{
						textFieldLinearMin.setText("");
					}
					LocalParameter rangeMaxParam = existingBioEvent.getParameter(BioEventParameterType.RangeMaxTime);
					if (rangeMaxParam!=null && rangeMaxParam.getExpression()!=null){
						textFieldLinearMax.setText(rangeMaxParam.getExpression().infix());
					}else{
						textFieldLinearMax.setText("");
					}
					LocalParameter rangeNumParam = existingBioEvent.getParameter(BioEventParameterType.RangeNumTimes);
					if (rangeNumParam!=null && rangeNumParam.getExpression()!=null){
						textFieldLinearNum.setText(rangeNumParam.getExpression().infix());
					}else{
						textFieldLinearNum.setText("");
					}
					rdbtnLinearTimeRange.setSelected(true);
					break;
				}
				case ListOfTimes: {
					StringBuffer sb = new StringBuffer();
					for (LocalParameter p : existingBioEvent.getEventParameters()){
						if (p.getRole() == BioEventParameterType.TimeListItem){
							if (sb.length()>0){
								sb.append(",");
							}
							sb.append(p.getExpression().infix());
						}
					}
					textFieldListTimes.setText(sb.toString());
					rdbtnListTimes.setSelected(true);
					break;
				}
				case ObservableAboveThreshold: {
					LocalParameter observableParameter = existingBioEvent.getParameter(BioEventParameterType.Observable);
					SymbolTableEntry ste = null;
					if (observableParameter!=null && observableParameter.getExpression()!=null && observableParameter.getExpression().isIdentifier()){
						ste = observableParameter.getExpression().getSymbolBinding(observableParameter.getExpression().getSymbols()[0]);
						varAboveComboBox.setSelectedItem(ste);
					}
					textFieldAboveVarVal.setText(existingBioEvent.getParameter(BioEventParameterType.Threshold).getExpression().infix());
					rdbtnVarAboveThreshold.setSelected(true);
					break;
				}
				case ObservableBelowThreshold: {
					LocalParameter observableParameter = existingBioEvent.getParameter(BioEventParameterType.Observable);
					SymbolTableEntry ste = null;
					if (observableParameter!=null && observableParameter.getExpression()!=null && observableParameter.getExpression().isIdentifier()){
						ste = observableParameter.getExpression().getSymbolBinding(observableParameter.getExpression().getSymbols()[0]);
						varBelowComboBox.setSelectedItem(ste);
					}
					textFieldBelowVarVal.setText(existingBioEvent.getParameter(BioEventParameterType.Threshold).getExpression().infix());
					rdbtnVarBelowThreshold.setSelected(true);
					break;
				}
				case SingleTriggerTime: {
					LocalParameter parameter = existingBioEvent.getParameter(BioEventParameterType.SingleTriggerTime);
					textFieldSingleTime.setText(parameter.getExpression().infix());
					rdbtnSingleTime.setSelected(true);
					break;
				}
				}
				
			}

		}
	}
//	private void setVerifier(final SimulationContext simulationContext){
//		textFieldGeneral.setInputVerifier(new InputVerifier() {
//			@Override
//			public boolean verify(JComponent input) {
//				boolean bValid = true;
//				if (textFieldGeneral.isEnabled()) {
//					String text = textFieldGeneral.getText();
//					String errorText = null;
//					if (text == null || text.trim().length() == 0) {
//						bValid = false;
//						errorText = "Trigger expression cannot be empty";
//					}else{
//						Expression expr = null;
//						try{
//							expr = EventPanel.bindTriggerExpression(textFieldGeneral.getText(),simulationContext);
//						}catch(Exception e){
//							bValid = false;
//							errorText = e.getMessage();
//						}
//					}
//					if (bValid) {
//						textFieldGeneral.setBorder(UIManager.getBorder("TextField.border"));
////						getBtnPlotTrigger().setEnabled(true);
//						textFieldGeneral.setToolTipText(null);
//					} else {
//						textFieldGeneral.setBorder(GuiConstants.ProblematicTextFieldBorder);
////						getBtnPlotTrigger().setEnabled(false);
//						textFieldGeneral.setToolTipText(errorText);
////						SwingUtilities.invokeLater(new Runnable() { 
////						    public void run() { 
////						    	getTriggerTextField().requestFocus();
////						    }
////						});
//					}
//				}
//				return bValid;
//			}
//		});
//
//	}
	public void setTrigger(BioEvent bioEvent) throws Exception{
		LocalParameter delayParam = bioEvent.getParameter(BioEventParameterType.TriggerDelay);
		if(rdbtnGeneral.isSelected()){
			bioEvent.setTriggerType(TriggerType.GeneralTrigger);
			bioEvent.setParameterValue(BioEventParameterType.GeneralTriggerFunction, new Expression(textFieldGeneral.getText()));
		}else if (rdbtnListTimes.isSelected()){
			bioEvent.setTriggerType(TriggerType.ListOfTimes);
			Expression[] listExps = new Expression("myFunc("+textFieldListTimes.getText()+")").getFunctionInvocations(null)[0].getArguments();
			bioEvent.setTimeList(listExps);
		}else if(rdbtnLinearTimeRange.isSelected()){
			bioEvent.setTriggerType(TriggerType.LinearRangeTimes);
			bioEvent.setParameterValue(BioEventParameterType.RangeMinTime, new Expression(textFieldLinearMin.getText()));
			bioEvent.setParameterValue(BioEventParameterType.RangeMaxTime, new Expression(textFieldLinearMax.getText()));
			bioEvent.setParameterValue(BioEventParameterType.RangeNumTimes, new Expression(textFieldLinearNum.getText()));
		}else if(rdbtnLogTimeRange.isSelected()){
			bioEvent.setTriggerType(TriggerType.LogRangeTimes);
			bioEvent.setParameterValue(BioEventParameterType.RangeMinTime, new Expression(textFieldLogMin.getText()));
			bioEvent.setParameterValue(BioEventParameterType.RangeMaxTime, new Expression(textFieldLogMax.getText()));
			bioEvent.setParameterValue(BioEventParameterType.RangeNumTimes, new Expression(textFieldLogNum.getText()));
		}else if (rdbtnSingleTime.isSelected()){
			bioEvent.setTriggerType(TriggerType.SingleTriggerTime);
			bioEvent.setParameterValue(BioEventParameterType.SingleTriggerTime,  new Expression(textFieldSingleTime.getText()));
		}else if (rdbtnVarAboveThreshold.isSelected()){
			bioEvent.setTriggerType(TriggerType.ObservableAboveThreshold);
			bioEvent.setParameterValue(BioEventParameterType.Observable, new Expression(((SymbolTableEntry)varAboveComboBox.getSelectedItem()).getName()));
			bioEvent.setParameterValue(BioEventParameterType.Threshold, new Expression(textFieldAboveVarVal.getText()));
		}else if (rdbtnVarBelowThreshold.isSelected()){
			bioEvent.setTriggerType(TriggerType.ObservableBelowThreshold);
			bioEvent.setParameterValue(BioEventParameterType.Observable, new Expression(((SymbolTableEntry)varBelowComboBox.getSelectedItem()).getName()));
			bioEvent.setParameterValue(BioEventParameterType.Threshold, new Expression(textFieldBelowVarVal.getText()));
		}
		if (delayParam.getExpression()!=null){
			bioEvent.setParameterValue(BioEventParameterType.TriggerDelay, delayParam.getExpression());
		}
	}
}
