/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.rmi.server.RMISocketFactory;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import org.vcell.util.AuthenticationException;
import org.vcell.util.Compare;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.resource.ResourceUtil;
import sun.rmi.transport.proxy.RMIHttpToPortSocketFactory;


public class RMIVCellConnectionFactory implements VCellConnectionFactory {

	public static final String SERVICE_NAME = "VCellBootstrapServer";

//	private String connectString = null;
	UserLoginInfo userLoginInfo;
	private String host = null;

/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellConnectionFactory(String argHost,UserLoginInfo userLoginInfo) {
	this.host = argHost;
	this.userLoginInfo = userLoginInfo;	
//	this.connectString = "//"+host+"/"+SERVICE_NAME;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:03:11 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 */
public void changeUser(UserLoginInfo userLoginInfo) {
	this.userLoginInfo = userLoginInfo;	
}

public VCellConnection createVCellConnection() throws AuthenticationException, ConnectionException {
	return createVCellConnectionAskProxy(null);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 */
public VCellConnection createVCellConnectionAskProxy(Component requester) throws AuthenticationException, ConnectionException {
	VCellThreadChecker.checkRemoteInvocation();

	VCellBootstrap vcellBootstrap = null;
	VCellConnection vcellConnection = null;
	try {
		vcellBootstrap = getVCellBootstrap(requester,this.host);
	} catch (Throwable e){
		throw new ConnectionException(e.getMessage(),e);
	}
	try {
		vcellConnection = vcellBootstrap.getVCellConnection(userLoginInfo);
		if (vcellConnection==null){
			throw new AuthenticationException("cannot login to server, check userid and password");
		}
	}catch (AuthenticationException ae) {
		throw ae;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new ConnectionException(e.getMessage());
	}
	return vcellConnection;
}
/**
 * This method was created in VisualAge.
 */
public static String getVCellSoftwareVersion(Component requester,String host) {
	VCellThreadChecker.checkRemoteInvocation();
	
	try {
		VCellBootstrap vcellBootstrap = getVCellBootstrap(requester,host);
		if (vcellBootstrap != null){
			return vcellBootstrap.getVCellSoftwareVersion();
		}else{
			return null;
		}
	} catch (Throwable e){
		e.printStackTrace(System.out);
		return null;
	}			
}
/**
 * This method was created in VisualAge.
 */
public static boolean pingBootstrap(String host) {
	VCellThreadChecker.checkRemoteInvocation();
	
	try {
		VCellBootstrap vcellBootstrap = getVCellBootstrap(null,host);
		if (vcellBootstrap != null){
			return true;
		}else{
			return false;
		}
	} catch (Throwable e){
		e.printStackTrace(System.out);
		return false;
	}			
}

private static VCellBootstrap getVCellBootstrap0(String host) throws Exception{
	return (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup("//"+host+"/"+SERVICE_NAME);
}
public static final String prefProxyType = "proxyType";
public static final String prefProxyHost = "proxyHost";
public static final String prefProxyPort = "proxyPort";
public static VCellBootstrap getVCellBootstrap(Component requester,String host) throws Exception{
	//If requester != null (called from VCell client) and connection fails then we ask user to supply proxy info
	//If requester == null (called from VCell server) then we assume that all connection properties were set already
//	Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
	try {
		return getVCellBootstrap0(host);
	} catch (Exception e) {
		e.printStackTrace();
		throw createException(e);
////		Throwable parent = e;
////		do{
////			System.out.println(parent.getClass().getName()+" "+parent.getMessage());
////		}while((parent = parent.getCause()) != null);		
//		if(requester != null){// called from client, see if proxy prefs are set and try those
//			return getVCellBootstrap(requester, handleExceptionProxyPrfs(e));
//		}else{// called from server, requester == null, assume java properties for proxies (if necessary) are not set correctly at jvm startup
//			throw createException(e);
//		}
	}
}

private static final String PROXY_FORMAT = "[http,socks]:proxyHost:portNumber";
public static void setProxyPrefs(Component requester) throws UtilCancelException{
	Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
	String proxyType = prefs.get(prefProxyType,prefProxyType);
	String proxyHost = prefs.get(prefProxyHost,prefProxyHost);
	String proxyPort = prefs.get(prefProxyPort,prefProxyPort);
	do {
		try {
			String init = proxyType+":"+proxyHost+":"+proxyPort;
			String s = DialogUtils.showInputDialog0(requester, "Enter 3 : separated values, "+PROXY_FORMAT,init);
			String proxyTypeNew = null;
			String proxyHostNew = null;
			String proxyPortNew = null;
			if(s== null || s.trim().length()==0){
				//clear proxy info
				prefs.remove(prefProxyType);
				prefs.remove(prefProxyHost);
				prefs.remove(prefProxyPort);
			}else{
				StringTokenizer st = new StringTokenizer(s, ":");
				proxyTypeNew = st.nextToken().toLowerCase();
				if(!proxyTypeNew.equals("http") && !proxyTypeNew.equals("socks")){
					throw new IllegalArgumentException("Unkown proxyType='"+proxyTypeNew+"'");
				}
				proxyHostNew = st.nextToken();
				proxyPortNew = st.nextToken();
				prefs.put(prefProxyType, proxyTypeNew);
				prefs.put(prefProxyHost, proxyHostNew);
				prefs.put(prefProxyPort, Integer.parseInt(proxyPortNew)+"");
			}
			break;
		} catch (UtilCancelException uce) {
			// User didn't set proxy info
			throw uce;
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			DialogUtils.showErrorDialog(requester,nfe.getMessage() + "\nportNumber must be integer between 0 and 65535");
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			DialogUtils.showErrorDialog(requester,iae.getMessage() + "\nProxy type must be 'http' or 'socks'");
		} catch(Exception e){
			e.printStackTrace();
			DialogUtils.showErrorDialog(requester,e.getMessage() + "\nEntry must be of form ("+PROXY_FORMAT+")");
		}
	} while (true);
	
	setProxyProperties(requester,true,proxyType,proxyHost,proxyPort,prefs.get(prefProxyType,prefProxyType),prefs.get(prefProxyHost,prefProxyHost),prefs.get(prefProxyPort,prefProxyPort));
}

public static final String PROXY_HTTP_HOST = "http.proxyHost";
public static final String PROXY_HTTP_PORT = "http.proxyPort";
public static final String PROXY_SOCKS_HOST = "socksProxyHost";
public static final String PROXY_SOCKS_PORT = "socksProxyPort";
public static void setProxyProperties(Component requester,boolean bRestartWarn,String proxyType ,String proxyHost ,String proxyPort,String proxyTypeNew ,String proxyHostNew ,String proxyPortNew){
	String altVMOptionsFile = System.getProperty("user.home")+System.getProperty("file.separator")+ResourceUtil.VCELL_HOME_DIR_NAME+System.getProperty("file.separator")+ResourceUtil.VCELL_PROXY_VMOPTIONS;
	try /*(FileWriter fw = new FileWriter(new File(altVMOptionsFile),false))*/ {
		// Set or clear java proxy system properties
		if(proxyTypeNew != null && proxyTypeNew.equals("http")){
			System.setProperty(PROXY_HTTP_HOST, proxyHostNew);
			System.setProperty(PROXY_HTTP_PORT, proxyPortNew);
			System.clearProperty(PROXY_SOCKS_HOST);
			System.clearProperty(PROXY_SOCKS_PORT);							
		}else if(proxyTypeNew != null && proxyTypeNew.equals("socks")){
			System.setProperty(PROXY_SOCKS_HOST, proxyHostNew);
			System.setProperty(PROXY_SOCKS_PORT, proxyPortNew);				
			System.clearProperty(PROXY_HTTP_HOST);
			System.clearProperty(PROXY_HTTP_PORT);
		}else{
			System.clearProperty(PROXY_HTTP_HOST);
			System.clearProperty(PROXY_HTTP_PORT);
			System.clearProperty(PROXY_SOCKS_HOST);
			System.clearProperty(PROXY_SOCKS_PORT);							
		}
//		// Write out proxy to supplemental install4j proxy.vmoptions file
//		fw.write((proxyTypeNew != null && proxyTypeNew.equals("http")? PROXY_HTTP_HOST+"="+proxyHostNew+"\n"+PROXY_HTTP_PORT+"="+proxyPortNew+"\n":"\n"));
//		fw.write((proxyTypeNew != null && proxyTypeNew.equals("socks")?PROXY_SOCKS_HOST+"="+proxyHostNew+"\n"+PROXY_SOCKS_PORT+"="+proxyPortNew+"\n":"\n"));
//		fw.close();
		// Check if user should be told to restart
		boolean bChanged = !Compare.isEqualOrNull(proxyType, proxyTypeNew) || !Compare.isEqualOrNull(proxyHost, proxyHostNew) ||!Compare.isEqualOrNull(proxyPort, proxyPortNew);
		String oldProxy = proxyType+":"+proxyHost+":"+proxyPort;
		String newProxy = proxyTypeNew+":"+proxyHostNew+":"+proxyPortNew;
		if(bChanged){
			if(bRestartWarn){
				DialogUtils.showInfoDialog(requester, "Proxy settings have changed from '"+oldProxy+"' to '"+newProxy+"', please restart VCell");
			}else{
				new Exception("Error, not expecting old proxy "+oldProxy+" doesn't match new proxy "+newProxy).printStackTrace(System.out);
			}
		}
	}catch(Exception e){
		e.printStackTrace();
//		DialogUtils.showErrorDialog(requester,"Error writing proxyOptions file '"+altVMOptionsFile+"'\n"+ e.getMessage());
	}

}

//private static String handleExceptionProxyPrfs(Exception cause) throws Exception{
//	Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
//	String proxyType = prefs.get(prefProxyType,prefProxyType);
//	String proxyHost = prefs.get(prefProxyHost,prefProxyHost);
//	String proxyPort = prefs.get(prefProxyPort,prefProxyPort);
//	boolean bProxySet = true;
//	boolean bProxyTypeChanged = false;
//	boolean bProxyHostPortChanged = false;
//	System.out.println("Prefs= "+proxyType+":"+proxyHost+":"+proxyPort);
//	String combinedType = System.getProperty("socksProxyHost")+","+System.getProperty("http.proxyHost");
//	if(proxyType.equals("http")){
//		System.out.println("Props= "+combinedType+":"+System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort"));
//		bProxyTypeChanged = System.getProperty("socksProxyHost") != null;
//		bProxyHostPortChanged = 
//			!Compare.isEqualOrNull(System.getProperty("http.proxyHost"), proxyHost) ||
//			!Compare.isEqualOrNull(System.getProperty("http.proxyPort"), proxyPort);
//		System.setProperty("http.proxyHost", proxyHost);
//		System.setProperty("http.proxyPort", proxyPort + "");						
//		System.clearProperty("socksProxyHost");
//		System.clearProperty("socksProxyPort");						
//	}else if (proxyType.equals("socks")){
//		System.out.println("Props= "+combinedType+":"+System.getProperty("socksProxyHost")+":"+System.getProperty("socksProxyPort"));
//		bProxyTypeChanged = System.getProperty("http.proxyHost") != null;
//		bProxyHostPortChanged = 
//			!Compare.isEqualOrNull(System.getProperty("socksProxyHost"), proxyHost) ||
//			!Compare.isEqualOrNull(System.getProperty("socksProxyPort"), proxyPort);
//		System.setProperty("socksProxyHost", proxyHost);
//		System.setProperty("socksProxyPort", proxyPort+"");						
//		System.clearProperty("http.proxyHost");
//		System.clearProperty("http.proxyPort");						
//	}else{//No proxy set
//		System.out.println("Props= "+combinedType+":"+System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort"));
//		System.out.println("Props= "+combinedType+":"+System.getProperty("socksProxyHost")+":"+System.getProperty("socksProxyPort"));
//		bProxyTypeChanged = System.getProperty("socksProxyHost") != null || System.getProperty("http.proxyHost") != null;
//		bProxySet = false;
//		System.clearProperty("socksProxyHost");
//		System.clearProperty("socksProxyPort");						
//		System.clearProperty("http.proxyHost");
//		System.clearProperty("http.proxyPort");						
//		System.clearProperty("http.proxySet");						
//		System.clearProperty("java.rmi.server.disableHttp");						
//		System.clearProperty("sun.rmi.transport.proxy.eagerHttpFallback");						
//	}
//	if(bProxySet){
//		System.setProperty("http.proxySet", "true");
//		System.setProperty("java.rmi.server.disableHttp", "false");
//		System.setProperty("sun.rmi.transport.proxy.eagerHttpFallback", "true");
//	}
//	//now reset the socket mechanism to 'http' tunnel (will also try 'socks' if properties set)
//	//must be done because inital default rmifactory is not configured for tunneling if above properties were not set when jvm started
//	//this can only be set once and can't be changed, must restart jvm to clear
//	if(RMISocketFactory.getSocketFactory() == null){
//		if(bProxySet){
//			RMISocketFactory.setSocketFactory(new RMIHttpToPortSocketFactory());
//			return proxyHost;
//		}else{//clear an unset rmisocketfactory, nothing to do, os rethrow original error
//			throw createException(cause);
//		}
//	}else if(bProxySet && (bProxyHostPortChanged || bProxyTypeChanged)){//if rmisocketfactory is set we can change proxy parameters
//		return proxyHost;
//	}else{// nothing changed or can't unset if socketfactory has been set, must restart jvm
//		throw createException(cause);
//		
//	}
//
//}

private static Exception createException(Exception cause){
	String s =
			"http.proxySet="+System.getProperty("http.proxySet")+
			"\nhttp.proxyHost="+System.getProperty("http.proxyHost")+
			"\nhttp.proxyPort="+System.getProperty("http.proxyPort")+
			"\njava.rmi.server.disableHttp="+System.getProperty("java.rmi.server.disableHttp")+
			"\nsun.rmi.transport.proxy.eagerHttpFallback="+System.getProperty("sun.rmi.transport.proxy.eagerHttpFallback")+
			"\nsocksProxyHost="+System.getProperty("socksProxyHost")+
			"\nsocksProxyPort="+System.getProperty("socksProxyPort")+
			"\nRMISocketFactory="+(RMISocketFactory.getSocketFactory()==null?RMISocketFactory.getDefaultSocketFactory().getClass().getName():RMISocketFactory.getSocketFactory().getClass().getName());
	System.out.println(s);
	boolean bHttpProxy = System.getProperty(PROXY_HTTP_HOST) != null &&  System.getProperty(PROXY_HTTP_HOST).trim().length()>0;
	boolean bSockProxy = System.getProperty(PROXY_SOCKS_HOST) != null &&  System.getProperty(PROXY_SOCKS_HOST).trim().length()>0;
	boolean bNoProxy = !bHttpProxy && !bSockProxy;
	Exception newe = new Exception("Getting connection bootstrap failed: "+cause.getClass().getSimpleName()+
			" ("+
			(bNoProxy?"Local Proxy=None":(bHttpProxy?"HTTP Proxy="+System.getProperty(PROXY_HTTP_HOST)+":"+System.getProperty(PROXY_HTTP_PORT):"Socks Proxy="+System.getProperty(PROXY_SOCKS_HOST)+":"+System.getProperty(PROXY_SOCKS_PORT)))+
			")"+
			"\nIf problem persists see menu Server->'Set Proxy' to change if necessary and restart VCell",cause);
	return newe;
}


//private static void test(){
//		java.net.Proxy proxy = null;
//		String Proxy_Authorization_headerValue = null;
//		if (item.getProxyId() != null) {
//			final ProxyConf conf = MonitorDaemon.getInstance().getProxyConfById(item.getProxyId());
//			if (conf != null) {
//				SocketAddress sa = InetSocketAddress.createUnresolved(conf.getHost(), conf.getPort());
//				if (conf.getProxyType().equals("http")) {
//					proxy = new Proxy(Proxy.Type.HTTP, sa);
//					if (conf.getUserName() != null && conf.getUserName().length() > 0
//							&& conf.getPassword() != null && conf.getPassword().length() > 0) {
//						Proxy_Authorization_headerValue = "Basic "
//								+ Base64.encode((conf.getUserName() + ":" + conf.getPassword()).getBytes("UTF-8"));
//					}
//				} else {
//					proxy = new Proxy(Proxy.Type.SOCKS, sa);
//					if (conf.getUserName() != null && conf.getUserName().length() > 0
//							&& conf.getPassword() != null && conf.getPassword().length() > 0) {
//						java.net.Authenticator authenticator = new java.net.Authenticator() {
//							@Override
//							protected java.net.PasswordAuthentication getPasswordAuthentication() {
//								return new java.net.PasswordAuthentication(conf.getUserName(),
//										conf.getPassword().toCharArray());
//							}
//						};
//						java.net.Authenticator.setDefault(authenticator);
//					}
//				}
//			}
//		}
//		if (proxy == null) {
//			httpUrlConn = (java.net.HttpURLConnection) url.openConnection();
//		} else {
//			httpUrlConn = (java.net.HttpURLConnection) url.openConnection(proxy);
//			if (Proxy_Authorization_headerValue != null) {
//				String headerKey = "Proxy-Authorization";
//				httpUrlConn.setRequestProperty(headerKey, Proxy_Authorization_headerValue);
//			}
//		}
//		if (cookie != null) {
//			httpUrlConn.setRequestProperty("cookie", cookie);
//		}
//	}
}
