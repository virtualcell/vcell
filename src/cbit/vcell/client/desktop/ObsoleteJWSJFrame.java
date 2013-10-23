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

public class ObsoleteJWSJFrame extends JFrame {

	public ObsoleteJWSJFrame(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JEditorPane dtrpnmyFirstHeading = new JEditorPane();
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
					"<h1>VCell has a new installer method!</h1>"+
					"<p>VCell no longer uses 'Java Web Start'.  Please reinstall VCell by visiting our software installation page here: <a href=\"http://vcell.org/vcell_software\" >Run VCell Software</a>.</p>"+
					"<p>You can also visit our main website at <a href=\"http://vcell.org\">http://vcell.org</a></p>"+
					"<p>If you have any questions or problems installing or using VCell, email <b><i>VCell_Support@vcell.org</b></i></p>"+
					"<p>Please note: If you have any prior installed VCell launchers or web shortcuts on your computer desktops or similar links delete them to avoid conflicts with the new VCell installer.</p>"+
					"<p>VCell automatically checks for new software updates when launched.  If new software updates are available a dialog will appear asking you to permit the update.<p>"+
				"</body>"+
			"</html>"
		);
		GridBagConstraints gbc_dtrpnmyFirstHeading = new GridBagConstraints();
		gbc_dtrpnmyFirstHeading.fill = GridBagConstraints.BOTH;
		gbc_dtrpnmyFirstHeading.gridx = 0;
		gbc_dtrpnmyFirstHeading.gridy = 0;
		getContentPane().add(dtrpnmyFirstHeading, gbc_dtrpnmyFirstHeading);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		//making signed jar file for obsolete jws message.
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
		obsoleteJWSJFrame.setTitle("VCell Improved Installer");
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
