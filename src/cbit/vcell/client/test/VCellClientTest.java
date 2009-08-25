package cbit.vcell.client.test;
import java.io.File;
import java.util.StringTokenizer;

import cbit.util.xml.XmlUtil;
import cbit.vcell.client.server.*;
import cbit.vcell.client.*;
import javax.swing.*;

import org.jdom.Document;
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
	String hoststr = System.getProperty(org.vcell.util.PropertyLoader.vcellServerHost);
	String[] hosts = null;
	if (hoststr != null) {
		StringTokenizer st = new StringTokenizer(hoststr," ,;");
		if (st.countTokens() >= 1) {
			hosts = new String[st.countTokens()];
			int count = 0;
			while (st.hasMoreTokens()) {
				hosts[count ++] = st.nextToken();
			}
		}
	}
	if (hosts == null) {
		hosts = new String[1];
	}
	String user = null;
	String password = null;
	org.vcell.util.document.VCDocument initialDocument = null;
	if (args.length == 3) {
		hosts[0] = args[0];
		user = args[1];
		password = args[2];
	}else if (args.length==0){
		// this is ok
	}else if (args.length==1){
		hosts[0] = args[0];
	}else if (args.length==2 && args[0].equals("-open")){
		String filename = args[1];
		try {
			Document xmlDoc = XmlUtil.readXML(new File(filename));
			String vcmlString = XmlUtil.xmlToString(xmlDoc, false);
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
	if (hosts[0].equalsIgnoreCase("-local")) {
		csInfo = ClientServerInfo.createLocalServerInfo(user, password);
	} else {
		csInfo = ClientServerInfo.createRemoteServerInfo(hosts, user, password);
	}
	try {
		VCellClient.startClient(initialDocument, csInfo);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of VCellApplication");
		exception.printStackTrace(System.out);
	}
}
}