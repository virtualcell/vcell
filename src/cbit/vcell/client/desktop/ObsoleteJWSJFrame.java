package cbit.vcell.client.desktop;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ObsoleteJWSJFrame extends JFrame {

	public ObsoleteJWSJFrame(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JEditorPane dtrpnmyFirstHeading = new JEditorPane();
		GridBagConstraints gbc_dtrpnmyFirstHeading = new GridBagConstraints();
		gbc_dtrpnmyFirstHeading.insets = new Insets(4, 4, 4, 4);
		gbc_dtrpnmyFirstHeading.fill = GridBagConstraints.BOTH;
		gbc_dtrpnmyFirstHeading.gridx = 0;
		gbc_dtrpnmyFirstHeading.gridy = 0;
		panel.add(dtrpnmyFirstHeading, gbc_dtrpnmyFirstHeading);
		dtrpnmyFirstHeading.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		dtrpnmyFirstHeading.setEditable(false);
		
		dtrpnmyFirstHeading.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		           try{
			           if(Desktop.isDesktopSupported()) {
			        	   Desktop.getDesktop().browse(e.getURL().toURI());
			           }else{
			        	   throw new Exception("Desktop.isDesktopSupported()=false");
			           }
	        	   }catch(Exception e2){
	        		   e2.printStackTrace();
	        		   JOptionPane.showMessageDialog(ObsoleteJWSJFrame.this,"Web browser not accessible.  Please visit http://vcell.org to install VCell software.");
	        	   }
		        }
		    }
		});
		dtrpnmyFirstHeading.setText(
			"<!DOCTYPE html>\r\n"+"" +
			"<html>\r\n"+
				"<body>\r\n"+
					"<h1><center>VCell has a new installer method!</center></h1>"+
					"<p>VCell no longer uses 'Java Web Start'.  Please reinstall VCell by visiting our installation page here: <a href=\"http://vcell.org/vcell_software\" >Run VCell Software</a>.</p>"+
					"<p>You can also visit our main website at <a href=\"http://vcell.org\">http://vcell.org</a></p>"+
					"<p>If you have any questions or problems installing or using VCell, email <b><i>VCell_Support@vcell.org</b></i></p>"+
					"<p>Please delete any prior installed VCell launchers or web shortcuts to avoid conflicts with the new VCell installer.</p>"+
					"<p>VCell will check for software updates when launched, please allow updates to occur if prompted.<p>"+
				"</body>"+
			"</html>"
		);
		
		JButton btnNewButton = new JButton("Quit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(4, 0, 4, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 1;
		panel.add(btnNewButton, gbc_btnNewButton);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		//making and deploying signed jar file for obsolete jws message.
//		//open cmd.exe and cd to work area C:\temp\ObsoleteJWSJFrame.
//		//copy cbit.vcell.client.desktop.ObsoleteJWSJFrame* classes from eclipse "bin" dir to cbit/vcell/client/desktop directory.
		
//		//Create mymanifest.txt containing following text:
//		Permissions: sandbox
//		Codebase: vcell.org
//		Application-Name: VCell JWS Installer Obsolete

//		//delete any existing old jar file in work area.  Create new Jar file
//		jar cvfm  cbit_vcell_client_desktop_ObsoleteJWSJFrame.jar  mymanifest.txt classes
//		//Look up signing parameters in DeployVCell/Configuration/deployProperties.xml (<keystore ...>)
//		//get keystore file from DeployVCell svn archive and put in work area.
//		jarsigner -keystore paramName(KeystoreFilename) -storepass paramPassword cbit_vcell_client_desktop_ObsoleteJWSJFrame.jar paramAlias
		
//		//Create jnlp file with the following text, replace codebase Site and href site with VCellDeploySite name (e.g. Alpha-alpha,Beta-beta,Rel-rel(special,omit),Test-test)
//		<?xml version="1.0" encoding="UTF-8"?>
//		<jnlp spec="1.0+" 
//		        codebase="http://vcell.org/webstart/Site"
//		        href="vcellsite.jnlp">
//		    <information>
//		        <title>New VCell Installer Available</title>
//		        <vendor>UCHC/NRCAM</vendor>
//		    </information>
//		    <resources>
//		        <!-- Application Resources -->
//		        <j2se version="1.6+"
//		              href="http://java.sun.com/products/autodl/j2se"/>
//		        <jar href="cbit_vcell_client_desktop_ObsoleteJWSJFrame.jar" main="true" />
//
//		    </resources>
//		    <application-desc
//		         name="VCell JWS Installer Obsolete"
//		         main-class="cbit.vcell.client.desktop.ObsoleteJWSJFrame">
//		     </application-desc>
//		     <update check="background"/>
//		</jnlp>

//		//copy jnlp file to apache jws site naming file as defined in jnlp file
//		//copy cbit_vcell_client_desktop_ObsoleteJWSJFrame.jar to apache jws site

		ObsoleteJWSJFrame obsoleteJWSJFrame = new ObsoleteJWSJFrame();
		obsoleteJWSJFrame.setTitle("VCell Improved Installer beginning Oct 23, 2013");
		obsoleteJWSJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obsoleteJWSJFrame.pack();
		obsoleteJWSJFrame.setSize(400,400);
		//center on screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = obsoleteJWSJFrame.getSize();
		if (size.height > screenSize.height)
			size.height = screenSize.height;
		if (size.width > screenSize.width)
			size.width = screenSize.width;
		obsoleteJWSJFrame.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
		//show
		obsoleteJWSJFrame.setVisible(true);
	}

}
