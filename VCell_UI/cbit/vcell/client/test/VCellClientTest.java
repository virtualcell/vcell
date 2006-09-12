package cbit.vcell.client.test;
import cbit.gui.PropertyLoader;
import cbit.vcell.client.server.*;
import cbit.vcell.client.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 12:02:01 PM)
 * @author: Ion Moraru
 */
public class VCellClientTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	StringBuffer stringBuffer = new StringBuffer();
	for (int i = 0; i < args.length; i++){
		stringBuffer.append("arg"+i+"=\""+args[i]+"\" ");
	}
	System.out.println("starting with arguments ["+stringBuffer+"]");
	
	ClientServerInfo csInfo = null;
	String host = System.getProperty(PropertyLoader.vcellServerHost);
	String user = null;
	String password = null;
	cbit.util.VCDocument initialDocument = null;
	if (args.length == 3) {
		host = args[0];
		user = args[1];
		password = args[2];
	}else if (args.length==0){
		// this is ok
	}else if (args.length==1){
		host = args[0];
	}else if (args.length==2 && args[0].equals("-open")){
		String filename = args[1];
		try {
			String vcmlString = cbit.util.xml.XmlUtil.getXMLString(filename);
			java.awt.Component parent = null;
			cbit.util.xml.VCLogger vcLogger = new TranslationLogger(parent);
			initialDocument = cbit.vcell.xml.XmlHelper.XMLToDocument(vcLogger,vcmlString);
		}catch (Exception e){
			e.printStackTrace(System.out);
			JOptionPane.showMessageDialog(null,e.getMessage(),"vcell startup error",JOptionPane.ERROR_MESSAGE);
		}
	}else{
		System.out.println("usage: VCellClientTest ( ((-local|host[:port]) [userid password]) | (-open filename) )");
		System.exit(1);
	}
	if (host.equalsIgnoreCase("-local")) {
		csInfo = ClientServerInfo.createLocalServerInfo(user, password);
	} else {
		csInfo = ClientServerInfo.createRemoteServerInfo(host, user, password);
	}
	try {
		VCellClient.startClient(initialDocument, csInfo);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of VCellApplication");
		exception.printStackTrace(System.out);
	}
}
}