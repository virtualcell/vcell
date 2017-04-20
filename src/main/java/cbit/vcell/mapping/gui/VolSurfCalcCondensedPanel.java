package cbit.vcell.mapping.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class VolSurfCalcCondensedPanel extends JPanel {
	private JTextField attrTextField;
	private JComboBox shapeComboBox;
	private JLabel attrLabel;
	private JTextField volumeTextField;
	private JTextField surfaceTextField;
	private JButton calculateButton;
	public VolSurfCalcCondensedPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0, 0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		shapeComboBox = new JComboBox();
		GridBagConstraints gbc_shapeComboBox = new GridBagConstraints();
		gbc_shapeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_shapeComboBox.gridx = 1;
		gbc_shapeComboBox.gridy = 0;
		add(shapeComboBox, gbc_shapeComboBox);
		
		attrLabel = new JLabel("param:");
		GridBagConstraints gbc_attrLabel = new GridBagConstraints();
		gbc_attrLabel.insets = new Insets(0, 4, 0, 0);
		gbc_attrLabel.gridx = 2;
		gbc_attrLabel.gridy = 0;
		add(attrLabel, gbc_attrLabel);
		
		attrTextField = new JTextField();
		GridBagConstraints gbc_attrTextField = new GridBagConstraints();
		gbc_attrTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_attrTextField.gridx = 3;
		gbc_attrTextField.gridy = 0;
		add(attrTextField, gbc_attrTextField);
		attrTextField.setColumns(10);
		
		calculateButton = new JButton("calc->");
		GridBagConstraints gbc_calculateButton = new GridBagConstraints();
		gbc_calculateButton.gridx = 4;
		gbc_calculateButton.gridy = 0;
		add(calculateButton, gbc_calculateButton);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weightx = 1.0;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 5;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel_2 = new JLabel("Vol:");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 4, 0, 2);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 0;
		panel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		volumeTextField = new JTextField("VVal");
		volumeTextField.setEditable(false);
		GridBagConstraints gbc_volumeTextField = new GridBagConstraints();
		gbc_volumeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_volumeTextField.anchor = GridBagConstraints.WEST;
		gbc_volumeTextField.weightx = 1.0;
		gbc_volumeTextField.gridx = 1;
		gbc_volumeTextField.gridy = 0;
		panel.add(volumeTextField, gbc_volumeTextField);
		
		JLabel lblNewLabel_4 = new JLabel("Surf:");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 2, 0, 2);
		gbc_lblNewLabel_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_4.gridx = 2;
		gbc_lblNewLabel_4.gridy = 0;
		panel.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		surfaceTextField = new JTextField("SVal");
		surfaceTextField.setEditable(false);
		GridBagConstraints gbc_surfaceTextField = new GridBagConstraints();
		gbc_surfaceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_surfaceTextField.anchor = GridBagConstraints.WEST;
		gbc_surfaceTextField.weightx = 1.0;
		gbc_surfaceTextField.gridx = 3;
		gbc_surfaceTextField.gridy = 0;
		panel.add(surfaceTextField, gbc_surfaceTextField);
	}

	public JComboBox getShapeComboBox() {
		return shapeComboBox;
	}
	public JLabel getAttrLabel() {
		return attrLabel;
	}
	public JTextField getAttrTextField() {
		return attrTextField;
	}
	public JTextField getVolumeTextField() {
		return volumeTextField;
	}
	public JTextField getSurfaceTextField() {
		return surfaceTextField;
	}
	public JButton getCalculateButton() {
		return calculateButton;
	}
}
