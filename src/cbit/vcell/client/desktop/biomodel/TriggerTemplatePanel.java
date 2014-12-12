package cbit.vcell.client.desktop.biomodel;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JRadioButton;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.solver.ExplicitOutputTimeSpec;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TriggerTemplatePanel extends JPanel {
	private JTextField textFieldSingleTime;
	private JTextField txtEventName;
	private JTextField textFieldOneVarVal;
	private JTextField textFieldMultiTimes;
	private JComboBox<String> varComboBox = null;
	private JComboBox<String> mathOpComboBox = null;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtnGeneral;
	private JRadioButton rdbtnOneVar;
	private JRadioButton rdbtnSingleTime;
	private JRadioButton rdbtnMultiTimes;

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
		
		JLabel lblNewLabel_2 = new JLabel("Arbitrary 'trigger' and 'delay' expressions");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(4, 4, 5, 4);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 3;
		add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		rdbtnOneVar = new JRadioButton("on variable value");
		GridBagConstraints gbc_rdbtnOneVar = new GridBagConstraints();
		gbc_rdbtnOneVar.anchor = GridBagConstraints.WEST;
		gbc_rdbtnOneVar.insets = new Insets(4, 4, 5, 5);
		gbc_rdbtnOneVar.gridx = 0;
		gbc_rdbtnOneVar.gridy = 4;
		add(rdbtnOneVar, gbc_rdbtnOneVar);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(4, 4, 5, 4);
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 4;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0};
		gbl_panel.columnWeights = new double[]{0, 0.0, 0};
		gbl_panel.rowWeights = new double[]{0.0};
		panel.setLayout(gbl_panel);
		
		varComboBox = new JComboBox<String>();
		GridBagConstraints gbc_varComboBox = new GridBagConstraints();
		gbc_varComboBox.weightx = 1.0;
		gbc_varComboBox.insets = new Insets(4, 4, 5, 5);
		gbc_varComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_varComboBox.gridx = 0;
		gbc_varComboBox.gridy = 0;
		panel.add(varComboBox, gbc_varComboBox);
		
		mathOpComboBox = new JComboBox<String>();
		GridBagConstraints gbc_mathOpComboBox = new GridBagConstraints();
		gbc_mathOpComboBox.insets = new Insets(4, 4, 4, 4);
		gbc_mathOpComboBox.weightx = 1.0;
		gbc_mathOpComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_mathOpComboBox.gridx = 1;
		gbc_mathOpComboBox.gridy = 0;
		panel.add(mathOpComboBox, gbc_mathOpComboBox);
		
		textFieldOneVarVal = new JTextField();
		GridBagConstraints gbc_textFieldOneVarVal = new GridBagConstraints();
		gbc_textFieldOneVarVal.weightx = 1.0;
		gbc_textFieldOneVarVal.insets = new Insets(4, 4, 4, 4);
		gbc_textFieldOneVarVal.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldOneVarVal.gridx = 2;
		gbc_textFieldOneVarVal.gridy = 0;
		panel.add(textFieldOneVarVal, gbc_textFieldOneVarVal);
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
		
		textFieldMultiTimes = new JTextField();
		textFieldMultiTimes.setText(".5, 1.0, 1.5, 2.0");
		GridBagConstraints gbc_textFieldMultiTimes = new GridBagConstraints();
		gbc_textFieldMultiTimes.weightx = 1.0;
		gbc_textFieldMultiTimes.insets = new Insets(4,4,4,4);
		gbc_textFieldMultiTimes.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMultiTimes.gridx = 0;
		gbc_textFieldMultiTimes.gridy = 0;
		panel_1.add(textFieldMultiTimes, gbc_textFieldMultiTimes);
		textFieldMultiTimes.setColumns(10);
		
		buttonGroup.add(rdbtnGeneral);
		buttonGroup.add(rdbtnOneVar);
		buttonGroup.add(rdbtnSingleTime);
		buttonGroup.add(rdbtnMultiTimes);
	}

	public String getEventPreferredName(){
		return txtEventName.getText();
	}
//	private SimulationContext simulationContext;
	public void setSimulationContext(SimulationContext simulationContext){
//		this.simulationContext = simulationContext;
		if(simulationContext != null){
			mathOpComboBox.removeAllItems();
			mathOpComboBox.addItem("==");
			mathOpComboBox.addItem(">");
			mathOpComboBox.addItem("<");
			mathOpComboBox.addItem(">=");
			mathOpComboBox.addItem("<=");
			
			varComboBox.removeAllItems();
			EventPanel.populateVariableComboBoxModel((DefaultComboBoxModel<String>)varComboBox.getModel(), simulationContext/*,false*/);
		}
	}
	public String getTriggerExpr(){
		String expr = null;
		if(rdbtnGeneral.isSelected()){
			expr = null;
		}else if(rdbtnOneVar.isSelected()){
			expr = varComboBox.getSelectedItem().toString() + mathOpComboBox.getSelectedItem().toString() + textFieldOneVarVal.getText();
		}else if(rdbtnSingleTime.isSelected()){
			expr = ReservedVariable.TIME.getName()+">="+textFieldSingleTime.getText();
		}else if(rdbtnMultiTimes.isSelected()){
			StringBuffer sb = new StringBuffer();
			java.util.StringTokenizer st = new java.util.StringTokenizer(textFieldMultiTimes.getText(), ", \n");
//			double[] times = new double[st.countTokens()];
//			int count = 0;
			HashMap<String, Double> numMap = new HashMap<>();
			while (st.hasMoreTokens()) {
				try {
					String token = st.nextToken();
					double num = Double.parseDouble(token);// check number
					numMap.put(token, num);
//					times[count ++] = time;
				} catch (Exception ex) {
					throw new RuntimeException("Times must be numbers seperated by commas.");
				}			
			}
			// calc epsilon
			Double[] sortNums = numMap.values().toArray(new Double[0]);
			Arrays.sort(sortNums);
			double epsilon = Double.MAX_VALUE;
			double prevNum = sortNums[0];
			for(double sortNum:sortNums){
				if(sortNum != prevNum){
					if((sortNum-prevNum)<epsilon){
						epsilon = sortNum-prevNum;
					}
				}
			}
			epsilon/= 2.0;
			
			for(String numStr:numMap.keySet()){
				String subExpr = "(("+ReservedVariable.TIME.getName()+">="+numStr+") && ("+ReservedVariable.TIME.getName()+"<("+numStr+"+"+epsilon+")))";
				sb.append((sb.length()!=0?" || ":"")+subExpr);
			}
			expr = sb.toString();
		}
		return (expr==null || expr.length()==0?null:expr);
	}
}
