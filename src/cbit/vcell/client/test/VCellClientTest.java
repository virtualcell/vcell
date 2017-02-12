/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.test;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.ArrayUtils;
import org.jdom.Document;
import org.vcell.util.BeanUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.ConsoleCapture;
import org.vcell.util.logging.Logging;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.client.TranslationLogger;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.pyvcellproxy.VCellProxyServer;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.RMIVCellConnectionFactory;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 12:02:01 PM)
 * @author: Ion Moraru
 */
public class VCellClientTest {
	private static VCellClient vcellClient = null;
	
	public static VCellClient getVCellClient() {
		return vcellClient;
	}
	
	
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	//check synchronize Proxy prefs, Proxy Properties
	Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
	Boolean bHttp =
		(System.getProperty(RMIVCellConnectionFactory.PROXY_HTTP_HOST)==null && System.getProperty(RMIVCellConnectionFactory.PROXY_SOCKS_HOST)==null?null:System.getProperty(RMIVCellConnectionFactory.PROXY_HTTP_HOST) != null);
	String proxyHostPropFromSupplementalInstall4jVmoptions =
		(bHttp==null?null:(bHttp?System.getProperty(RMIVCellConnectionFactory.PROXY_HTTP_HOST):System.getProperty(RMIVCellConnectionFactory.PROXY_SOCKS_HOST)));
	String proxyPortPropFromSupplementalInstall4jVmoptions =
		(bHttp==null?null:(bHttp?System.getProperty(RMIVCellConnectionFactory.PROXY_HTTP_PORT):System.getProperty(RMIVCellConnectionFactory.PROXY_SOCKS_PORT)));
	RMIVCellConnectionFactory.writeProxyToSupplementalVMOptions(JOptionPane.getRootFrame(),false,
		prefs.get(RMIVCellConnectionFactory.prefProxyType,RMIVCellConnectionFactory.prefProxyType),
		proxyHostPropFromSupplementalInstall4jVmoptions,proxyPortPropFromSupplementalInstall4jVmoptions,
		prefs.get(RMIVCellConnectionFactory.prefProxyType,RMIVCellConnectionFactory.prefProxyType),
		prefs.get(RMIVCellConnectionFactory.prefProxyHost,RMIVCellConnectionFactory.prefProxyHost),prefs.get(RMIVCellConnectionFactory.prefProxyPort,RMIVCellConnectionFactory.prefProxyPort));

	final boolean  IS_DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	if (!IS_DEBUG) {
		String siteName = VCellSoftwareVersion.fromSystemProperty().getSite().name().toLowerCase();
		ConsoleCapture.getInstance().captureStandardOutAndError(new File(ResourceUtil.getLogDir(),"vcellrun_"+siteName+".log"));
	}
	Logging.init();
	BeanUtils.setDebug(IS_DEBUG);
	if(args != null &&  args.length >= 1 && args[0].equals("-console")){//remove install4j parameter
		List<String> newArgs = new ArrayList<String>();
		newArgs.addAll(Arrays.asList(args));
		newArgs.remove(0);
		args = newArgs.toArray(new String[0]);
	}
	StringBuffer stringBuffer = new StringBuffer();
	for (int i = 0; i < args.length; i++){
		stringBuffer.append("arg"+i+"=\""+args[i]+"\" ");
	}
	System.out.println("starting with arguments ["+stringBuffer+"]");
	System.out.println("Running under Java major version: ONE point "+ ResourceUtil.getJavaVersion().toString()+".  Specifically: Java "+(System.getProperty("java.version"))+
			", published by "+(System.getProperty("java.vendor"))+", on the "+ (System.getProperty("os.arch"))+" architecture running version "+(System.getProperty("os.version"))+
			" of the "+(System.getProperty("os.name"))+" operating system");
	
	ClientServerInfo csInfo = null;
	String hoststr = System.getProperty(PropertyLoader.vcellServerHost);
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
	VCDocument initialDocument = null;
	if (args.length == 3) {
		hosts[0] = args[0];
		user = args[1];
		password = args[2];
	}else if (args.length==0){
		// this is ok
	}else if (args.length==1){
		//Check if arg is drag-n-drop file or a 'hostname'
		try{
			//drag and drop file on install4j VCell launcher will pass filepath as single arg to VCell
			File openThisVCellFile = new File(args[0]);
			if(openThisVCellFile.exists() && openThisVCellFile.isFile()){
				initialDocument = startupWithOpen(args[0]);
			}
		}catch(Exception e){
			e.printStackTrace();
			//continue to hostname check
		}
		//If startup file not exist assume arg is a hostname
		if(initialDocument == null){
			hosts[0] = args[0];
		}
		//If install4j drag-n-drop, hosts[0] stays null and host is assumed to be loaded from a client property
		
	}else if (args.length==2 && args[0].equals("-open")){
//		hosts[0] = "-local";
		initialDocument = startupWithOpen(args[1]);
	}else{
		System.out.println("usage: VCellClientTest ( ((-local|host[:port]) [userid password]) | ([-open] filename) )");
		System.exit(1);
	}
	if (hosts[0]!=null && hosts[0].equalsIgnoreCase("-local")) {
		csInfo = ClientServerInfo.createLocalServerInfo(user, (password==null || password.length()==0?null:new UserLoginInfo.DigestedPassword(password)));
	} else {
		csInfo = ClientServerInfo.createRemoteServerInfo(hosts, user,(password==null || password.length()==0?null:new UserLoginInfo.DigestedPassword(password)));
	}
	try {
		String propertyFile = PropertyLoader.getProperty(PropertyLoader.propertyFileProperty, "");
		if (propertyFile.length()>0){
			PropertyLoader.loadProperties(ArrayUtils.addAll(REQUIRED_CLIENT_PROPERTIES,REQUIRED_LOCAL_PROPERTIES) );
			try {
				VCMongoMessage.enabled = true;
				VCMongoMessage.serviceStartup(ServiceName.client,null,null);
				PropertyLoader.sendErrorsToMongo();
			}catch (Exception e){
				System.out.println("failed to start Mongo logging");
			}
		}else{
			PropertyLoader.loadProperties(REQUIRED_CLIENT_PROPERTIES);
			VCMongoMessage.enabled = false;
		}

		//call in main thread, since it's quick and not necessarily thread safe
		ResourceUtil.setNativeLibraryDirectory();
		vcellClient = VCellClient.startClient(initialDocument, csInfo);

		VCellProxyServer.startVCellVisitDataServerThread(vcellClient);

		
		//starting loading libraries
		new LibraryLoaderThread(true).start( );
	} catch (Throwable exception) {
		BeanUtils.sendRemoteLogMessage(csInfo.getUserLoginInfo(),csInfo.toString()+"\nvcell startup failed\n\n" + exception.getMessage());
		JOptionPane.showMessageDialog(null, exception.getMessage(), "Fatal Error",JOptionPane.OK_OPTION);
		System.err.println("Exception occurred in main() of VCellApplication");
		exception.printStackTrace(System.out);
	}
}


private static VCDocument startupWithOpen(String fileName) {
	VCDocument initialDocument = null;
	try {
		Document xmlDoc = XmlUtil.readXML(new File(fileName));
		String vcmlString = XmlUtil.xmlToString(xmlDoc, false);
		java.awt.Component parent = null;
		VCLogger vcLogger = new TranslationLogger(parent);
		initialDocument = XmlHelper.XMLToDocument(vcLogger,vcmlString);
	}catch (Exception e){
		e.printStackTrace(System.out);
		JOptionPane.showMessageDialog(null,e.getMessage(),"vcell startup error",JOptionPane.ERROR_MESSAGE);
	}
	return initialDocument;
}


//private static String handleExceptionProxyPrfs(Exception cause) throws Exception{
//Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
//String proxyType = prefs.get(prefProxyType,prefProxyType);
//String proxyHost = prefs.get(prefProxyHost,prefProxyHost);
//String proxyPort = prefs.get(prefProxyPort,prefProxyPort);
//boolean bProxySet = true;
//boolean bProxyTypeChanged = false;
//boolean bProxyHostPortChanged = false;
//System.out.println("Prefs= "+proxyType+":"+proxyHost+":"+proxyPort);
//String combinedType = System.getProperty("socksProxyHost")+","+System.getProperty("http.proxyHost");
//if(proxyType.equals("http")){
//	System.out.println("Props= "+combinedType+":"+System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort"));
//	bProxyTypeChanged = System.getProperty("socksProxyHost") != null;
//	bProxyHostPortChanged = 
//		!Compare.isEqualOrNull(System.getProperty("http.proxyHost"), proxyHost) ||
//		!Compare.isEqualOrNull(System.getProperty("http.proxyPort"), proxyPort);
//	System.setProperty("http.proxyHost", proxyHost);
//	System.setProperty("http.proxyPort", proxyPort + "");						
//	System.clearProperty("socksProxyHost");
//	System.clearProperty("socksProxyPort");						
//}else if (proxyType.equals("socks")){
//	System.out.println("Props= "+combinedType+":"+System.getProperty("socksProxyHost")+":"+System.getProperty("socksProxyPort"));
//	bProxyTypeChanged = System.getProperty("http.proxyHost") != null;
//	bProxyHostPortChanged = 
//		!Compare.isEqualOrNull(System.getProperty("socksProxyHost"), proxyHost) ||
//		!Compare.isEqualOrNull(System.getProperty("socksProxyPort"), proxyPort);
//	System.setProperty("socksProxyHost", proxyHost);
//	System.setProperty("socksProxyPort", proxyPort+"");						
//	System.clearProperty("http.proxyHost");
//	System.clearProperty("http.proxyPort");						
//}else{//No proxy set
//	System.out.println("Props= "+combinedType+":"+System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort"));
//	System.out.println("Props= "+combinedType+":"+System.getProperty("socksProxyHost")+":"+System.getProperty("socksProxyPort"));
//	bProxyTypeChanged = System.getProperty("socksProxyHost") != null || System.getProperty("http.proxyHost") != null;
//	bProxySet = false;
//	System.clearProperty("socksProxyHost");
//	System.clearProperty("socksProxyPort");						
//	System.clearProperty("http.proxyHost");
//	System.clearProperty("http.proxyPort");						
//	System.clearProperty("http.proxySet");						
//	System.clearProperty("java.rmi.server.disableHttp");						
//	System.clearProperty("sun.rmi.transport.proxy.eagerHttpFallback");						
//}
//if(bProxySet){
//	System.setProperty("http.proxySet", "true");
//	System.setProperty("java.rmi.server.disableHttp", "false");
//	System.setProperty("sun.rmi.transport.proxy.eagerHttpFallback", "true");
//}
////now reset the socket mechanism to 'http' tunnel (will also try 'socks' if properties set)
////must be done because inital default rmifactory is not configured for tunneling if above properties were not set when jvm started
////this can only be set once and can't be changed, must restart jvm to clear
//if(RMISocketFactory.getSocketFactory() == null){
//	if(bProxySet){
//		RMISocketFactory.setSocketFactory(new RMIHttpToPortSocketFactory());
//		return proxyHost;
//	}else{//clear an unset rmisocketfactory, nothing to do, os rethrow original error
//		throw createException(cause);
//	}
//}else if(bProxySet && (bProxyHostPortChanged || bProxyTypeChanged)){//if rmisocketfactory is set we can change proxy parameters
//	return proxyHost;
//}else{// nothing changed or can't unset if socketfactory has been set, must restart jvm
//	throw createException(cause);
//	
//}
//
//}

/**
 * array of properties required for correct operation
 */
private static final String REQUIRED_CLIENT_PROPERTIES[] = {
	PropertyLoader.installationRoot,
	PropertyLoader.vcellSoftwareVersion,
};

/**
 * array of properties required for correct local operation
 */
private static final String REQUIRED_LOCAL_PROPERTIES[] = {
	PropertyLoader.primarySimDataDirProperty,
	PropertyLoader.secondarySimDataDirProperty,
	PropertyLoader.dbPassword,
	PropertyLoader.dbUserid,
	PropertyLoader.dbDriverName,
	PropertyLoader.dbConnectURL,
	PropertyLoader.vcellServerIDProperty,
	PropertyLoader.mongodbDatabase,
	PropertyLoader.mongodbHost,
	PropertyLoader.mongodbLoggingCollection,
	PropertyLoader.mongodbPort
	
};
}
