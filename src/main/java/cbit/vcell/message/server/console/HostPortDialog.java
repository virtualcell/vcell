package cbit.vcell.message.server.console;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.VCAssert;


@SuppressWarnings("serial")
public class HostPortDialog extends JDialog {
	private static final String SITES[] = {
			"Release: rmi-rel.cam.uchc.edu:40105",
			"Beta: rmi-beta.cam.uchc.edu:40105",
			"Alpha: rmi-alpha.cam.uchc.edu:40106",
			"Test: rmi-alpha.cam.uchc.edu:40110",
			"Test2: rmi-alpha.cam.uchc.edu:40111",
			"Test3: rmi-alpha.cam.uchc.edu:40112",
		};
	private JTextField textField;
	private JPanel predefinedSites;
	private JRadioButton defButton;
	private String host;
	private int port;
		
	public HostPortDialog(Frame frame) {
		super(frame);
		setModalityType(ModalityType.DOCUMENT_MODAL);
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setAlignmentX( Component.LEFT_ALIGNMENT);
		setContentPane(content);
		
		predefinedSites = new JPanel();
		predefinedSites.setAlignmentX( Component.LEFT_ALIGNMENT);
		getContentPane().add(predefinedSites);
		addRadioButtons(predefinedSites);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		JLabel lblNewLabel = new JLabel("enter VCell RMI bootstrap host:port");
		panel.add(lblNewLabel);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setText("rmi-rel.cam.uchc.edu:40105");
		
		JPanel btnPanel = new JPanel();
		getContentPane().add(btnPanel);
		
		JButton ok = new JButton("OK");
		btnPanel.add(ok);
		ok.addActionListener( ae -> { okayPressed(ae); } );
	 	getRootPane().setDefaultButton(ok);
		pack( );
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	private void addRadioButtons(JPanel panel) {
		ButtonGroup bg = new ButtonGroup();
		for (String s: SITES) {
			String[] parts = StringUtils.split(s);
			VCAssert.assertTrue(parts.length == 2, "invalid format");
			SiteButton sb = new SiteButton(parts[0], parts[1],bg); 
			panel.add(sb);
			sb.addActionListener( ae -> { buttonSelected(ae); } );
			if (defButton == null) {
				defButton = sb;
				defButton.doClick();
			}
		}
		JRadioButton other = new JRadioButton("Other (enter below)");
		bg.add(other);
		other.addActionListener( ae -> { otherSelected(ae); } );
		predefinedSites.setLayout(new BoxLayout(predefinedSites, BoxLayout.Y_AXIS));
		panel.add(other);
	}
	
	private void buttonSelected(ActionEvent ae) {
		SiteButton sb = (SiteButton) ae.getSource();
		String parts[] = StringUtils.split(sb.siteData,':');
		host = parts[0];
		port = Integer.parseInt(parts[1]);
	}
	
	private void otherSelected(ActionEvent ae )  {
		String parts[] = StringUtils.split(textField.getText(),':');
		if (parts.length == 2) {
			try {
				host = parts[0];
				port = Integer.parseInt(parts[1]);
				return;
			} catch (NumberFormatException nfe) {} //just fall through
		}
		JOptionPane.showMessageDialog(this, "Must be of form host:port","Bad manul input", JOptionPane.ERROR_MESSAGE);
		defButton.doClick();
	}
	
	private void okayPressed(ActionEvent ae) {
		setVisible(false);
	}
	
	private static class SiteButton extends JRadioButton {
		private final String siteData;
		SiteButton(String label, String data, ButtonGroup group) {
			super(label);
			siteData = data;
			group.add(this);
		}
	}
}
