package cbit.vcell.client.desktop;

import java.awt.Component;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import org.vcell.util.UtilCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.message.server.bootstrap.client.RMIVCellConnectionFactory;
import cbit.vcell.resource.NetworkProxyUtils;
import cbit.vcell.resource.NetworkProxyUtils.RestartWarningProvider;

public class NetworkProxyPreferences {

	public static final String prefProxyType = "proxyType";
	public static final String prefProxyHost = "proxyHost";
	public static final String prefProxyPort = "proxyPort";
	public static final String PROXY_FORMAT = "[http,socks]:proxyHost:portNumber";
	public static void setProxyPrefs(Component requester, RestartWarningProvider restartWarningProvider) throws UtilCancelException{
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
		
		NetworkProxyUtils.setProxyProperties(true,restartWarningProvider,proxyType,proxyHost,proxyPort,prefs.get(prefProxyType,prefProxyType),prefs.get(prefProxyHost,prefProxyHost),prefs.get(prefProxyPort,prefProxyPort));
	}

}
