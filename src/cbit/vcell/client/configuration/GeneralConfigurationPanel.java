package cbit.vcell.client.configuration;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

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

import cbit.vcell.resource.ResourceUtil;
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
		
		String s = ResourceUtil.getJavaVersion().toString();					// EIGHT
		String s6 = ResourceUtil.getUserHomeDir().getAbsolutePath();			// C:\Users\vasilescu
		String sn  = ResourceUtil.getSiteName();								// other
		
		JPanel jpanel = new JPanel();
		jpanel.setBorder(new CompoundBorder(margin, panelBorder));
		add(jpanel,BorderLayout.CENTER);
		
		String redhome = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"#880000\">(home)</font>";

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

		String location = ResourceUtil.getVCellInstall().getAbsolutePath();			// C:\dan\projects\VCell_trunk2
		JLabel l2 = new JLabel("<html>" + location + "</html>");
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l2, gbc);

		// ---------------------------------------------------------
		gridy++;
		title = "<b>VCell Home Directory</b>&nbsp;&nbsp;&nbsp;";
		JLabel l3 = new JLabel("<html>" + title + "</html>");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l3, gbc);

		location = ResourceUtil.getVcellHome().getAbsolutePath() + redhome;			// C:\Users\vasilescu\.vcell
		JLabel l4 = new JLabel("<html>" + location + "</html>");
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l4, gbc);
		
		// -----------------------------------------------------
		String ddap  = ResourceUtil.getDownloadDirectory().getAbsolutePath();	// C:\Users\vasilescu\.vcell\download
		String s2 = ResourceUtil.getLocalVisDataDir().getAbsolutePath();		// C:\Users\vasilescu\.vcell\visdata
		String s4 = ResourceUtil.getSolversDirectory().getAbsolutePath();		// C:\Users\vasilescu\.vcell\solvers_DanDev_Version_5_3_build_99

		gridy++;
		title = "&nbsp;&nbsp;&nbsp;Simulation Data&nbsp;&nbsp;&nbsp;";
		JLabel l5 = new JLabel("<html>" + title + "</html>");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l5, gbc);

		String lrdap = ResourceUtil.getLocalRootDir().getAbsolutePath();		// C:\Users\vasilescu\.vcell\simdata
		int where = lrdap.lastIndexOf(".vcell");
		if(where > 0) {
			lrdap = lrdap.substring(where + ".vcell".length());
			lrdap = redhome + lrdap;
		}
		JLabel l6 = new JLabel("<html>" + lrdap + "</html>");
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l6, gbc);

		gridy++;
		title = "&nbsp;&nbsp;&nbsp;Log Data&nbsp;&nbsp;&nbsp;";
		JLabel l7 = new JLabel("<html>" + title + "</html>");
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l7, gbc);

		String ldap = ResourceUtil.getLogDir().getAbsolutePath();					// C:\Users\vasilescu\.vcell\logs
		where = ldap.lastIndexOf(".vcell");
		if(where > 0) {
			ldap = ldap.substring(where + ".vcell".length());
			ldap = redhome + ldap;
		}

		JLabel l8 = new JLabel("<html>" + ldap + "</html>");
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,2,4);
		jpanel.add(l8, gbc);

		// ---------------------------------------------------------
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
