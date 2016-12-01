package cbit.vcell.client.configuration;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GeneralConfigurationPanel extends JPanel {

	public GeneralConfigurationPanel(){
		super();
		initialize();
	}
	
	private void initialize() {
		
		setLayout(new GridBagLayout());

		JLabel jl = new JLabel("General");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 4, 2, 4);
		add(jl, gbc);
	}

}
