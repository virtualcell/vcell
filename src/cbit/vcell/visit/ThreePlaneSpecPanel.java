package cbit.vcell.visit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ThreePlaneSpecPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public ThreePlaneSpecPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JCheckBox chckbxPlane = new JCheckBox("Plane 1");
		GridBagConstraints gbc_chckbxPlane = new GridBagConstraints();
		gbc_chckbxPlane.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxPlane.gridx = 0;
		gbc_chckbxPlane.gridy = 0;
		panel.add(chckbxPlane, gbc_chckbxPlane);
		
		JLabel lblOrigin = new JLabel("Origin");
		GridBagConstraints gbc_lblOrigin = new GridBagConstraints();
		gbc_lblOrigin.insets = new Insets(0, 0, 5, 5);
		gbc_lblOrigin.gridx = 0;
		gbc_lblOrigin.gridy = 1;
		panel.add(lblOrigin, gbc_lblOrigin);
		
		JFormattedTextField formattedTextField = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField = new GridBagConstraints();
		gbc_formattedTextField.insets = new Insets(0, 0, 5, 0);
		gbc_formattedTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField.gridx = 1;
		gbc_formattedTextField.gridy = 1;
		panel.add(formattedTextField, gbc_formattedTextField);
		
		JLabel lblNormal = new JLabel("Normal");
		GridBagConstraints gbc_lblNormal = new GridBagConstraints();
		gbc_lblNormal.insets = new Insets(0, 0, 5, 5);
		gbc_lblNormal.gridx = 0;
		gbc_lblNormal.gridy = 2;
		panel.add(lblNormal, gbc_lblNormal);
		
		JFormattedTextField formattedTextField_1 = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField_1 = new GridBagConstraints();
		gbc_formattedTextField_1.insets = new Insets(0, 0, 5, 0);
		gbc_formattedTextField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_1.gridx = 1;
		gbc_formattedTextField_1.gridy = 2;
		panel.add(formattedTextField_1, gbc_formattedTextField_1);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JCheckBox chckbxPlane_1 = new JCheckBox("Plane 2");
		GridBagConstraints gbc_chckbxPlane_1 = new GridBagConstraints();
		gbc_chckbxPlane_1.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxPlane_1.gridx = 0;
		gbc_chckbxPlane_1.gridy = 0;
		panel_1.add(chckbxPlane_1, gbc_chckbxPlane_1);
		
		JLabel lblOrigin_1 = new JLabel("Origin");
		GridBagConstraints gbc_lblOrigin_1 = new GridBagConstraints();
		gbc_lblOrigin_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblOrigin_1.gridx = 0;
		gbc_lblOrigin_1.gridy = 1;
		panel_1.add(lblOrigin_1, gbc_lblOrigin_1);
		
		JFormattedTextField formattedTextField_2 = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField_2 = new GridBagConstraints();
		gbc_formattedTextField_2.insets = new Insets(0, 0, 5, 0);
		gbc_formattedTextField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_2.gridx = 1;
		gbc_formattedTextField_2.gridy = 1;
		panel_1.add(formattedTextField_2, gbc_formattedTextField_2);
		
		JLabel lblNormal_1 = new JLabel("Normal");
		GridBagConstraints gbc_lblNormal_1 = new GridBagConstraints();
		gbc_lblNormal_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblNormal_1.gridx = 0;
		gbc_lblNormal_1.gridy = 2;
		panel_1.add(lblNormal_1, gbc_lblNormal_1);
		
		JFormattedTextField formattedTextField_3 = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField_3 = new GridBagConstraints();
		gbc_formattedTextField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_3.gridx = 1;
		gbc_formattedTextField_3.gridy = 2;
		panel_1.add(formattedTextField_3, gbc_formattedTextField_3);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JCheckBox chckbxPlane_2 = new JCheckBox("Plane 3");
		GridBagConstraints gbc_chckbxPlane_2 = new GridBagConstraints();
		gbc_chckbxPlane_2.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxPlane_2.gridx = 0;
		gbc_chckbxPlane_2.gridy = 0;
		panel_2.add(chckbxPlane_2, gbc_chckbxPlane_2);
		
		JLabel lblOrigin_2 = new JLabel("Origin");
		GridBagConstraints gbc_lblOrigin_2 = new GridBagConstraints();
		gbc_lblOrigin_2.anchor = GridBagConstraints.EAST;
		gbc_lblOrigin_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblOrigin_2.gridx = 0;
		gbc_lblOrigin_2.gridy = 1;
		panel_2.add(lblOrigin_2, gbc_lblOrigin_2);
		
		JFormattedTextField formattedTextField_4 = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField_4 = new GridBagConstraints();
		gbc_formattedTextField_4.insets = new Insets(0, 0, 5, 0);
		gbc_formattedTextField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_4.gridx = 1;
		gbc_formattedTextField_4.gridy = 1;
		panel_2.add(formattedTextField_4, gbc_formattedTextField_4);
		
		JLabel lblNormal_2 = new JLabel("Normal");
		GridBagConstraints gbc_lblNormal_2 = new GridBagConstraints();
		gbc_lblNormal_2.anchor = GridBagConstraints.EAST;
		gbc_lblNormal_2.insets = new Insets(0, 0, 0, 5);
		gbc_lblNormal_2.gridx = 0;
		gbc_lblNormal_2.gridy = 2;
		panel_2.add(lblNormal_2, gbc_lblNormal_2);
		
		JFormattedTextField formattedTextField_5 = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField_5 = new GridBagConstraints();
		gbc_formattedTextField_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_5.gridx = 1;
		gbc_formattedTextField_5.gridy = 2;
		panel_2.add(formattedTextField_5, gbc_formattedTextField_5);

	}

}
