package cbit.vcell.client.configuration;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.PropertyLoader;

import cbit.vcell.resource.VCellConfiguration;

public class GeneralConfigurationPanel extends JPanel {

	public GeneralConfigurationPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		
		setLayout(new BorderLayout());
		
		Border margin = new EmptyBorder(5,3,1,1);
		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder panelBorder = BorderFactory.createTitledBorder(loweredEtchedBorder, " General Properties ");
		panelBorder.setTitleJustification(TitledBorder.LEFT);
		panelBorder.setTitlePosition(TitledBorder.TOP);
		panelBorder.setTitleFont(getFont().deriveFont(Font.BOLD));
		
		JPanel jpanel = new JPanel();
		jpanel.setBorder(new CompoundBorder(margin, panelBorder));
		add(jpanel,BorderLayout.CENTER);
		
		jpanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridheight = 1;
		
		int gridy = 0;
		String title = "<b>Installation Directory</b>&nbsp;&nbsp;&nbsp;";
		JLabel l1 = new JLabel("<html>" + title + "</html>");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);			// top, left bottom, right
		jpanel.add(l1, gbc);

		String location = "C:\\install_dir";
		JLabel l2 = new JLabel("<html>" + location + "</html>");
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l2, gbc);

		gridy++;
		title = "<b>Working Directory</b>&nbsp;&nbsp;&nbsp;";
		JLabel l3 = new JLabel("<html>" + title + "</html>");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l3, gbc);

		location = "C:\\Users\\username\\.vcell";
		JLabel l4 = new JLabel("<html>" + location + "</html>");
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l4, gbc);

		gridy++;
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;		// fake cell used for filling all the vertical empty space
		gbc.anchor = GridBagConstraints.WEST;
		jpanel.add(new JLabel(""), gbc);
	}

}
