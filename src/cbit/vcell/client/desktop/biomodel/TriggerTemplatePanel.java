package cbit.vcell.client.desktop.biomodel;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JRadioButton;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.border.LineBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.desktop.simulation.ParameterScanPanel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Constant;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.model.GeneralLumpedKinetics;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.ConstantArraySpec;
import cbit.vcell.solver.ExplicitOutputTimeSpec;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class TriggerTemplatePanel extends JPanel {
	private JTextField textFieldSingleTime;
	private JTextField txtEventName;
	private JTextField textFieldOneVarVal;
//	private JTextField textFieldMultiTimes;
	private ParameterScanPanel multiTimesPanel;
	private JComboBox<String> varComboBox = null;
	private JComboBox<String> mathOpComboBox = null;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtnGeneral;
	private JRadioButton rdbtnOneVar;
	private JRadioButton rdbtnSingleTime;
	private JRadioButton rdbtnMultiTimes;
	private JLabel generalLabel;
	private JPanel varValuePanel;
	
	private ItemListener rdbtnLItemListener =
			new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.DESELECTED){
						return;
					}
					generalLabel.setEnabled(false);
					BeanUtils.enableComponents(varValuePanel, false);
					textFieldSingleTime.setEnabled(false);
					BeanUtils.enableComponents(multiTimesPanel, false);
					if(e.getSource() == rdbtnGeneral && rdbtnGeneral.isSelected()){
						generalLabel.setEnabled(true);
					}if(e.getSource() == rdbtnOneVar && rdbtnOneVar.isSelected()){
						BeanUtils.enableComponents(varValuePanel, true);
					}else if(e.getSource() == rdbtnSingleTime && rdbtnSingleTime.isSelected()){
						textFieldSingleTime.setEnabled(true);
					}else if(e.getSource() == rdbtnMultiTimes && rdbtnMultiTimes.isSelected()){
						BeanUtils.enableComponents(multiTimesPanel, true);
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
		
		JLabel lblNewLabel = new JLabel("Event Name:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(4, 4, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		txtEventName = new JTextField();
		txtEventName.setText("event0");
		GridBagConstraints gbc_txtEventName = new GridBagConstraints();
		gbc_txtEventName.gridwidth = 2;
		gbc_txtEventName.insets = new Insets(0, 4, 5, 4);
		gbc_txtEventName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEventName.gridx = 0;
		gbc_txtEventName.gridy = 1;
		add(txtEventName, gbc_txtEventName);
		txtEventName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Pre-Defined Triggers");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.gridwidth = 2;
		gbc_lblNewLabel_1.insets = new Insets(4, 4, 5, 4);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		rdbtnGeneral = new JRadioButton("general");
		rdbtnGeneral.setSelected(true);
		GridBagConstraints gbc_rdbtnGeneral = new GridBagConstraints();
		gbc_rdbtnGeneral.anchor = GridBagConstraints.WEST;
		gbc_rdbtnGeneral.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnGeneral.gridx = 0;
		gbc_rdbtnGeneral.gridy = 3;
		add(rdbtnGeneral, gbc_rdbtnGeneral);
		
		generalLabel = new JLabel("Arbitrary 'trigger' and 'delay' expressions");
		GridBagConstraints gbc_generalLabel = new GridBagConstraints();
		gbc_generalLabel.anchor = GridBagConstraints.WEST;
		gbc_generalLabel.insets = new Insets(4, 4, 5, 4);
		gbc_generalLabel.gridx = 1;
		gbc_generalLabel.gridy = 3;
		add(generalLabel, gbc_generalLabel);
		
		rdbtnOneVar = new JRadioButton("on variable value");
		GridBagConstraints gbc_rdbtnOneVar = new GridBagConstraints();
		gbc_rdbtnOneVar.anchor = GridBagConstraints.WEST;
		gbc_rdbtnOneVar.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnOneVar.gridx = 0;
		gbc_rdbtnOneVar.gridy = 4;
		add(rdbtnOneVar, gbc_rdbtnOneVar);
		
		varValuePanel = new JPanel();
		GridBagConstraints gbc_varValuePanel = new GridBagConstraints();
		gbc_varValuePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_varValuePanel.insets = new Insets(4, 4, 5, 4);
		gbc_varValuePanel.gridx = 1;
		gbc_varValuePanel.gridy = 4;
		add(varValuePanel, gbc_varValuePanel);
		GridBagLayout gbl_varValuePanel = new GridBagLayout();
		gbl_varValuePanel.columnWidths = new int[]{0, 0, 0};
		gbl_varValuePanel.rowHeights = new int[]{0};
		gbl_varValuePanel.columnWeights = new double[]{0, 0.0, 0};
		gbl_varValuePanel.rowWeights = new double[]{0.0};
		varValuePanel.setLayout(gbl_varValuePanel);
		
		varComboBox = new JComboBox<String>();
		GridBagConstraints gbc_varComboBox = new GridBagConstraints();
		gbc_varComboBox.weightx = 1.0;
		gbc_varComboBox.insets = new Insets(4, 4, 5, 5);
		gbc_varComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_varComboBox.gridx = 0;
		gbc_varComboBox.gridy = 0;
		varValuePanel.add(varComboBox, gbc_varComboBox);
		
		mathOpComboBox = new JComboBox<String>();
		GridBagConstraints gbc_mathOpComboBox = new GridBagConstraints();
		gbc_mathOpComboBox.insets = new Insets(4, 4, 4, 4);
		gbc_mathOpComboBox.weightx = 1.0;
		gbc_mathOpComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_mathOpComboBox.gridx = 1;
		gbc_mathOpComboBox.gridy = 0;
		varValuePanel.add(mathOpComboBox, gbc_mathOpComboBox);
		
		textFieldOneVarVal = new JTextField();
		GridBagConstraints gbc_textFieldOneVarVal = new GridBagConstraints();
		gbc_textFieldOneVarVal.weightx = 1.0;
		gbc_textFieldOneVarVal.insets = new Insets(4, 4, 4, 4);
		gbc_textFieldOneVarVal.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldOneVarVal.gridx = 2;
		gbc_textFieldOneVarVal.gridy = 0;
		varValuePanel.add(textFieldOneVarVal, gbc_textFieldOneVarVal);
		textFieldOneVarVal.setColumns(10);
		
		rdbtnSingleTime = new JRadioButton("at a single time");
		GridBagConstraints gbc_rdbtnSingleTime = new GridBagConstraints();
		gbc_rdbtnSingleTime.anchor = GridBagConstraints.WEST;
		gbc_rdbtnSingleTime.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnSingleTime.gridx = 0;
		gbc_rdbtnSingleTime.gridy = 5;
		add(rdbtnSingleTime, gbc_rdbtnSingleTime);
		
		textFieldSingleTime = new JTextField();
		textFieldSingleTime.setText("0.0");
		GridBagConstraints gbc_textFieldSingleTime = new GridBagConstraints();
		gbc_textFieldSingleTime.insets = new Insets(4, 4, 4, 4);
		gbc_textFieldSingleTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSingleTime.gridx = 1;
		gbc_textFieldSingleTime.gridy = 5;
		add(textFieldSingleTime, gbc_textFieldSingleTime);
		textFieldSingleTime.setColumns(10);
		
		rdbtnMultiTimes = new JRadioButton("at multiple times");
		GridBagConstraints gbc_rdbtnMultiTimes = new GridBagConstraints();
		gbc_rdbtnMultiTimes.insets = new Insets(4, 4, 4, 5);
		gbc_rdbtnMultiTimes.anchor = GridBagConstraints.WEST;
		gbc_rdbtnMultiTimes.gridx = 0;
		gbc_rdbtnMultiTimes.gridy = 6;
		add(rdbtnMultiTimes, gbc_rdbtnMultiTimes);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 6;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0};
		gbl_panel_1.rowHeights = new int[]{0};
		gbl_panel_1.columnWeights = new double[]{0};
		gbl_panel_1.rowWeights = new double[]{0.0};
		panel_1.setLayout(gbl_panel_1);

		multiTimesPanel = new ParameterScanPanel();
		multiTimesPanel.setBorder(new LineBorder(Color.black));
		GridBagConstraints gbc_textFieldMultiTimes = new GridBagConstraints();
		gbc_textFieldMultiTimes.weightx = 1.0;
		gbc_textFieldMultiTimes.insets = new Insets(4,4,4,4);
		gbc_textFieldMultiTimes.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMultiTimes.gridx = 0;
		gbc_textFieldMultiTimes.gridy = 0;
		panel_1.add(multiTimesPanel, gbc_textFieldMultiTimes);
		
		buttonGroup.add(rdbtnGeneral);
		buttonGroup.add(rdbtnOneVar);
		buttonGroup.add(rdbtnSingleTime);
		buttonGroup.add(rdbtnMultiTimes);
		
		rdbtnLItemListener.itemStateChanged(new ItemEvent(rdbtnGeneral, 0, rdbtnGeneral, ItemEvent.SELECTED));
		
		rdbtnGeneral.addItemListener(rdbtnLItemListener);
		rdbtnOneVar.addItemListener(rdbtnLItemListener);
		rdbtnSingleTime.addItemListener(rdbtnLItemListener);
		rdbtnMultiTimes.addItemListener(rdbtnLItemListener);
	}

	public String getEventPreferredName(){
		return txtEventName.getText();
	}
//	private SimulationContext simulationContext;
	public void init(SimulationContext simulationContext,String preferredEventName){
//		this.simulationContext = simulationContext;
		if(preferredEventName != null && preferredEventName.length() > 0){
			txtEventName.setText(preferredEventName);
		}
		if(simulationContext != null){
			mathOpComboBox.removeAllItems();
//			mathOpComboBox.addItem("==");
			mathOpComboBox.addItem(">");
			mathOpComboBox.addItem("<");
			mathOpComboBox.addItem(">=");
			mathOpComboBox.addItem("<=");
			
			varComboBox.removeAllItems();
			SymbolTableEntry[] triggerExprSymbols = ModelOptimizationSpec.calculateTimeDependentModelObjects(simulationContext);
			Arrays.sort(triggerExprSymbols, new Comparator<SymbolTableEntry>() {
				@Override
				public int compare(SymbolTableEntry o1, SymbolTableEntry o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			for(SymbolTableEntry ste:triggerExprSymbols){
				if(!(ste instanceof ReservedSymbol)){
					varComboBox.addItem(ste.getName());
				}
			}
//			EventPanel.populateVariableComboBoxModel((DefaultComboBoxModel<String>)varComboBox.getModel(), simulationContext,false);
		}
	}
	public String getTriggerExpr() throws Exception{
		String expr = null;
		if(rdbtnGeneral.isSelected()){
			expr = "0.0";
		}else if(rdbtnOneVar.isSelected()){
			expr = varComboBox.getSelectedItem().toString() + mathOpComboBox.getSelectedItem().toString() + textFieldOneVarVal.getText();
		}else if(rdbtnSingleTime.isSelected()){
			expr = ReservedVariable.TIME.getName()+">="+textFieldSingleTime.getText();
		}else if(rdbtnMultiTimes.isSelected()){
			multiTimesPanel.applyValues();
			Constant[] constantArray = multiTimesPanel.getConstantArraySpec().getConstants();
			// calc epsilon
			final Exception[] failFlag = new Exception[1];
			Arrays.sort(constantArray,new Comparator<Constant>() {
				@Override
				public int compare(Constant o1, Constant o2) {
					// TODO Auto-generated method stub
					try{
						return (int)Math.signum(o1.getConstantValue()-o2.getConstantValue());
					}catch(Exception e){
						if(failFlag[0] == null){
							failFlag[0] = e;
						}
						return 0;
					}
				}
			});
			if(failFlag[0] != null){
				throw failFlag[0];
			}
			double epsilon = Double.MAX_VALUE;
			Constant prevNum = constantArray[0];
			for(Constant sortNum:constantArray){
				if(sortNum != prevNum){
					if((sortNum.getConstantValue()-prevNum.getConstantValue())<epsilon){
						epsilon = sortNum.getConstantValue()-prevNum.getConstantValue();
					}
				}
			}
			epsilon/= 2.0;
			
			StringBuffer sb = new StringBuffer();
			for(Constant sortNum:constantArray){
				String subExpr = "(("+ReservedVariable.TIME.getName()+">="+sortNum.getExpression().infix()+") && ("+ReservedVariable.TIME.getName()+"<("+sortNum.getExpression().infix()+"+"+epsilon+")))";
				sb.append((sb.length()!=0?" || ":"")+subExpr);
			}
			expr = sb.toString();
		}
		return (expr==null || expr.length()==0?null:expr);
	}
}
