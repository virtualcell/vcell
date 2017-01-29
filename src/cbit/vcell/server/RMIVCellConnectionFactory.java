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
import java.rmi.server.RMISocketFactory;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import org.vcell.util.AuthenticationException;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.UtilCancelException;

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
private static final String prefProxyType = "proxyType";
private static final String prefProxyHost = "proxyHost";
private static final String prefProxyPort = "proxyPort";
public static VCellBootstrap getVCellBootstrap(Component requester,String host) throws Exception{
	//If requester != null (called from VCell client) and connection fails then we ask user to supply proxy info
	//If requester == null (called from VCell server) then we assume that all connection properties were set already
	try {
		return getVCellBootstrap0(host);
	} catch (Exception e) {
		e.printStackTrace();
		Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
		String proxyType = prefs.get(prefProxyType,prefProxyType);
		String proxyHost = prefs.get(prefProxyHost,prefProxyHost);
		String proxyPort = prefs.get(prefProxyPort,prefProxyPort);

		//See if we already set the rmihttpsocketfactory, if so we already asked user and tried their info to create connection
		boolean bHttpAlreadySet = RMISocketFactory.getSocketFactory() != null;
		if(requester != null && !bHttpAlreadySet){// if we have requester then ask user about proxy info
			do {
				try {
//					String proxyHost = prefs.get(prefProxyHost,prefProxyHost);
//					int proxyPort = -1;
//					boolean bPrefsOK = false;
//					if(!proxyHost.equals(prefProxyHost)){
//						// try HTTP tunnel connection with current prefs
//						proxyPort = Integer.parseInt(prefs.get(prefProxyPort,prefProxyPort));
//						SocketAddress proxyAddress = InetSocketAddress.createUnresolved(proxyHost,proxyPort);
//						Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
////						String vcrmihostStr = System.getProperty("vcell.serverHost");
////						StringTokenizer st = new StringTokenizer("http://"+vcrmihostStr, ";");
//						URL url = new URL("http://"+host);
//						HttpURLConnection httpUrlConn = null;
//						try {
//							httpUrlConn = (HttpURLConnection)url.openConnection(proxy);
//							httpUrlConn.connect();
//							assert HttpURLConnection.HTTP_OK == httpUrlConn.getResponseCode();
//							long clength = httpUrlConn.getContentLength();
//							System.out.println("-----Test content leng ="+clength);
//							DataInputStream dis = new DataInputStream(httpUrlConn.getInputStream());
//							byte[] byteArr = new byte[(int)clength];
//							dis.readFully(byteArr);
//							dis.close();
//							System.out.println(new String(byteArr));
//							bPrefsOK = true;
//						} catch (Exception e1) {
//							e1.printStackTrace();
//							bPrefsOK = false;
//						}finally{
//							if(httpUrlConn != null){
//								httpUrlConn.disconnect();
//							}
//						}
//					}
//					if(!bPrefsOK){
						//Ask for proxy since direct didn't work, preset proxyType, proxyHost and proxyPort if already in preferences
						String init = prefs.get(prefProxyType,prefProxyType)+":"+prefs.get(prefProxyHost,prefProxyHost)+":"+prefs.get(prefProxyPort, prefProxyPort);
						String s = DialogUtils.showInputDialog0(requester, "Enter Proxy: [http,socks]:proxyHost:portNumber",init);
						StringTokenizer st = new StringTokenizer(s, ":");
						proxyType = st.nextToken().toLowerCase();
						if(!proxyType.equals("http") && !proxyType.equals("socks")){
							throw new IllegalArgumentException("Unkown proxyType='"+proxyType+"'");
						}
						proxyHost = st.nextToken();
						proxyPort = st.nextToken();
						prefs.put("proxyType", proxyType);
						prefs.put("proxyHost", proxyHost);
						prefs.put("proxyPort", Integer.parseInt(proxyPort)+"");
//					}
					if(proxyType.equals("http")){
						System.setProperty("http.proxyHost", proxyHost);
						System.setProperty("http.proxyPort", proxyPort + "");						
						System.clearProperty("socksProxyHost");
						System.clearProperty("socksPproxyPort");						
					}else{
						System.setProperty("socksProxyHost", proxyHost);
						System.setProperty("socksPproxyPort", proxyPort+"");						
						System.clearProperty("http.proxyHost");
						System.clearProperty("http.proxyPort");						
					}
					System.setProperty("http.proxySet", "true");
					System.setProperty("java.rmi.server.disableHttp", "false");
					System.setProperty("sun.rmi.transport.proxy.eagerHttpFallback", "true");
					//now reset the socket mechanism to 'http' tunnel (will also try 'socks' if properties set)
					//must be done because inital default rmifactory is not configured for tunneling if above properties were not set when jvm started
					//this can only be set once and can't be changed, must restart jvm to clear
					RMISocketFactory.setSocketFactory(new RMIHttpToPortSocketFactory());
					break;
				} catch (UtilCancelException uce) {
					// User didn't set proxy info, throw exception
					throw e;
				} catch (NumberFormatException nfe) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(requester,nfe.getMessage() + "\nportNumber must be integer between 0 and 65535");
				} catch (IllegalArgumentException iae) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(requester,iae.getMessage() + "\nProxy type must be 'http' or 'socks'");
				}
			} while (true);
			//try to get bootstrap again after setting user proxy info
			return getVCellBootstrap0(host);
		}else{
			String s =
				"http.proxySet="+System.getProperty("http.proxySet")+
				"\nhttp.proxyHost="+System.getProperty("http.proxyHost")+
				"\nhttp.proxyPort="+System.getProperty("http.proxyPort")+
				"\njava.rmi.server.disableHttp="+System.getProperty("java.rmi.server.disableHttp")+
				"\nsun.rmi.transport.proxy.eagerHttpFallback="+System.getProperty("sun.rmi.transport.proxy.eagerHttpFallback")+
				"\nsocksProxyHost="+System.getProperty("socksProxyHost")+
				"\nsocksProxyPort="+System.getProperty("socksProxyPort")+
				"\nRMISocketFactory="+(RMISocketFactory.getSocketFactory()==null?RMISocketFactory.getDefaultSocketFactory().getClass().getName():RMISocketFactory.getSocketFactory().getClass().getName());
					
			Exception newe = new Exception("Getting bootstrap failed because: "+
				(requester==null?"requester is null":"")+" "+(requester==null && 
				bHttpAlreadySet?"\n":"")+" "+(bHttpAlreadySet?"following settings didn't work:":"")+
				"\n"+s+
				"\n"+e.getMessage()+"\nRestart VCell to set new Proxy info.",e);
			if(requester != null){
				DialogUtils.showErrorDialog(requester, newe.getMessage());
			}
			throw newe;
		}
	}
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
