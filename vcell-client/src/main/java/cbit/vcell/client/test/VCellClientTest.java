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
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.ArrayUtils;
import org.jdom.Document;
import org.vcell.util.BeanUtils;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.logging.ConsoleCapture;
import org.vcell.util.logging.Logging;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.TranslationLogger;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.data.VCellClientDataServiceImpl;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.NetworkProxyPreferences;
import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.pyvcellproxy.VCellClientDataService;
import cbit.vcell.client.pyvcellproxy.VCellProxyServer;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.VCellBasicCellRenderer;
import cbit.vcell.message.server.bootstrap.client.RMIVCellConnectionFactory;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.ErrorUtils;
import cbit.vcell.resource.LibraryLoaderThread;
import cbit.vcell.resource.NetworkProxyUtils;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.ResourceUtil;
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
	
	public static ArrayBlockingQueue<String> recordedUserEvents = new ArrayBlockingQueue<>(50, true);
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	class ParseVCellUserEvents implements Runnable {
		AWTEvent event;
		public ParseVCellUserEvents(AWTEvent event){
			this.event = event;
		}
		@Override
		public void run() {
			if(event instanceof MouseEvent){
				MouseEvent mouseEvent = (MouseEvent)event;
				Object details = null;
				if(mouseEvent.getID() == MouseEvent.MOUSE_RELEASED){
					if(mouseEvent.getComponent() instanceof JTable){
						JTable comp = (JTable)mouseEvent.getComponent();
						int[] selRows = comp.getSelectedRows();
						if(selRows != null && selRows.length > 0){
							StringBuffer sb = new StringBuffer();
							for (int i = 0; i < selRows.length; i++) {
								for (int j = 0; j < comp.getColumnCount(); j++) {
									try{
										sb.append((j==0?"":",")+comp.getColumnName(j)+"='"+comp.getModel().getValueAt(selRows[i], j)+"'");
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
							details = sb.toString();
						}
					}else if(mouseEvent.getComponent() instanceof JTree){
						JTree comp = (JTree)mouseEvent.getComponent();
						TreePath treePath = comp.getSelectionPath();
						if(treePath != null){
							details = treePath.getLastPathComponent();
							// BioModel, MathModel, Geometry document tree selections
							if(details instanceof BioModelNode){
								//VCellBasicCellRenderer.VCDocumentInfoNode
								BioModelNode bioModelNode = (BioModelNode)details;
								boolean isVCDocumentInfo = bioModelNode.getUserObject() instanceof VCDocumentInfo;
								boolean isBioModelsNetModelInfo = bioModelNode.getUserObject() instanceof BioModelsNetModelInfo;
								if(!isVCDocumentInfo && bioModelNode.getChildCount() > 0 && bioModelNode.getUserObject() instanceof VCellBasicCellRenderer.VCDocumentInfoNode){
									TreeNode treeNode = bioModelNode.getFirstChild();
									if(treeNode instanceof BioModelNode && ((BioModelNode)treeNode).getUserObject() instanceof VCDocumentInfo){
										details = ((BioModelNode)treeNode).getUserObject();
									}
								}else if(isBioModelsNetModelInfo){
									details = BioModelsNetModelInfo.class.getSimpleName()+" '"+((BioModelsNetModelInfo)bioModelNode.getUserObject()).getName()+"'";
								}
							}
						}
					}else if(mouseEvent.getComponent() instanceof JTabbedPane){
						JTabbedPane comp = (JTabbedPane)mouseEvent.getComponent();
						details = "'"+comp.getTitleAt(comp.getSelectedIndex())+"'";
					}else if(mouseEvent.getComponent() instanceof JMenuItem){
						JMenuItem comp = (JMenuItem)mouseEvent.getComponent();
						details = "'"+comp.getText()+"'";
					}else if(mouseEvent.getComponent() instanceof AbstractButton){
						AbstractButton comp = (AbstractButton)mouseEvent.getComponent();
						Boolean bSelected = (comp instanceof JToggleButton?((JToggleButton)comp).isSelected():null);
						details = (bSelected != null?"("+(bSelected?"selected":"unselected")+")":"")+"'"+comp.getText()+"'";
					}else if(mouseEvent.getComponent() instanceof JComboBox<?>){
						JComboBox<?> comp = (JComboBox<?>)mouseEvent.getComponent();
						details = "'"+comp.getSelectedItem().toString()+"'";
					}else if(mouseEvent.getComponent() instanceof JList<?>){
						JList<?> comp = (JList<?>)mouseEvent.getComponent();
						details = "'"+comp.getSelectedValue()+"'";
					}else{
						details = "TBD "+mouseEvent.getComponent();
					}
					Component parentComponent = mouseEvent.getComponent();
					StringBuffer parentInfo = new StringBuffer();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BeanUtils.vcDateFormat, Locale.US);
					do{
						String title="";
						if(parentComponent instanceof Dialog){
							title = ((Dialog)parentComponent).getTitle();
						}else if(parentComponent instanceof Frame){
							title = ((Frame)parentComponent).getTitle();
						}
						parentInfo.append(parentComponent.getClass().getTypeName()+"("+parentComponent.getName()+(title!=null&& title.length()>0?",title='"+title+"'":"")+")");
						if(parentComponent instanceof DocumentWindow && ((DocumentWindow)parentComponent).getTopLevelWindowManager() instanceof DocumentWindowManager){
							VCDocument vcDocument = ((DocumentWindowManager)((DocumentWindow)parentComponent).getTopLevelWindowManager()).getVCDocument();
							if(vcDocument != null){
								String date = (vcDocument.getVersion() != null && vcDocument.getVersion().getDate()!=null?simpleDateFormat.format(vcDocument.getVersion().getDate()):"nodate");
								parentInfo.append("doc="+vcDocument.getDocumentType()+" '"+vcDocument.getName()+"' "+date);
							}
						}
						parentInfo.append(" -> ");
					}while((parentComponent = parentComponent.getParent()) != null);
					//try to add event, if full remove an event from the top
					while(!recordedUserEvents.offer(mouseEvent.getClickCount()+" "+(details==null?"null":details.toString())+BeanUtils.PLAINTEXT_EMAIL_NEWLINE+parentInfo.toString())){
						recordedUserEvents.poll();
					}
				}
			}
		}
	};
	
	
	AWTEventListener awtEventListener = new AWTEventListener() {
		@Override
		public void eventDispatched(final AWTEvent event) {
			if(event instanceof MouseEvent){
				if(((MouseEvent)event).getID() == MouseEvent.MOUSE_RELEASED){
					new Thread(new ParseVCellUserEvents(event)).start();
				}			
			}
		}
	};
	Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener,AWTEvent.MOUSE_EVENT_MASK);

	//check synchronize Proxy prefs, Proxy Properties
	Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
	Boolean bHttp =
		(System.getProperty(NetworkProxyUtils.PROXY_HTTP_HOST)==null && System.getProperty(NetworkProxyUtils.PROXY_SOCKS_HOST)==null?null:System.getProperty(NetworkProxyUtils.PROXY_HTTP_HOST) != null);
	String currentProxyHost =
		(bHttp==null?null:(bHttp?System.getProperty(NetworkProxyUtils.PROXY_HTTP_HOST):System.getProperty(NetworkProxyUtils.PROXY_SOCKS_HOST)));
	String currentProxyPort=
		(bHttp==null?null:(bHttp?System.getProperty(NetworkProxyUtils.PROXY_HTTP_PORT):System.getProperty(NetworkProxyUtils.PROXY_SOCKS_PORT)));
	NetworkProxyUtils.setProxyProperties(false,null,
		prefs.get(NetworkProxyPreferences.prefProxyType,NetworkProxyPreferences.prefProxyType),
		currentProxyHost,currentProxyPort,
		prefs.get(NetworkProxyPreferences.prefProxyType,NetworkProxyPreferences.prefProxyType),
		prefs.get(NetworkProxyPreferences.prefProxyHost,NetworkProxyPreferences.prefProxyHost),prefs.get(NetworkProxyPreferences.prefProxyPort,NetworkProxyPreferences.prefProxyPort));

	final boolean  IS_DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	if (!IS_DEBUG) {
		String siteName = VCellSoftwareVersion.fromSystemProperty().getSite().name().toLowerCase();
		ConsoleCapture.getInstance().captureStandardOutAndError(new File(ResourceUtil.getLogDir(),"vcellrun_"+siteName+".log"));
	}
	Logging.init();
	ErrorUtils.setDebug(IS_DEBUG);
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

		VCellClientDataService vcellClientDataService = new VCellClientDataServiceImpl(vcellClient);
		VCellProxyServer.startVCellVisitDataServerThread(vcellClientDataService);

		
		//starting loading libraries
		new LibraryLoaderThread(true).start( );
				
		try {
			PythonSupport.verifyInstallation();
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		//CondaSupport.installInBackground();


	} catch (Throwable exception) {
		ErrorUtils.sendRemoteLogMessage(csInfo.getUserLoginInfo(),csInfo.toString()+"\nvcell startup failed\n\n" + exception.getMessage());
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
