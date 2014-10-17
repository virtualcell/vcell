package cbit.vcell.message.server.console;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.server.UserMetaDbServer;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;

	/**
	 * minimal code to verify login into database
	 * */
public class LoginChecker {
	
	private static final String DOMAIN = "rmi-SITE.cam.uchc.edu";
	private static final String SERVICE = "/VCellBootstrapServer";
	private static class SiteInfo {
		final String name;
		final String host;
		final int port;
		public SiteInfo(String name, String host, int port) {
			super();
			this.name = name;
			this.host = DOMAIN.replace("SITE", host);
			this.port = port;
		}
		
		String bootStrapUrl( ) {
			return "//" + host   + ":" + port + SERVICE;
		}
	}
	
	private static Collection<SiteInfo>  sites = new ArrayList<LoginChecker.SiteInfo>();
	private static void add(SiteInfo si) {
		sites.add(si);
	}
	/*
	 <site name="REL"   RMI_HOSTNAME="rmi-rel.cam.uchc.edu"   RMI_PORT_HTTP="8080" RMI_PORT_HIGH="40105" JMX_PORT_HTTP="12300" JMX_PORT_HIGH="12301"></site>
			<site name="BETA"  RMI_HOSTNAME="rmi-beta.cam.uchc.edu"  RMI_PORT_HTTP="8080" RMI_PORT_HIGH="40105" JMX_PORT_HTTP="12302" JMX_PORT_HIGH="12303"></site>
			<site name="ALPHA" RMI_HOSTNAME="rmi-alpha.cam.uchc.edu" RMI_PORT_HTTP="-1"   RMI_PORT_HIGH="40106" JMX_PORT_HTTP="-1"    JMX_PORT_HIGH="12305"></site>
			<site name="TEST"  RMI_HOSTNAME="rmi-alpha.cam.uchc.edu" RMI_PORT_HTTP="-1"   RMI_PORT_HIGH="40110" JMX_PORT_HTTP="12306" JMX_PORT_HIGH="12307"></site>
			<site name="TEST2" RMI_HOSTNAME="rmi-alpha.cam.uchc.edu" RMI_PORT_HTTP="-1"   RMI_PORT_HIGH="40111" JMX_PORT_HTTP="-1"    JMX_PORT_HIGH="12309"></site>
			<site name="TEST3" RMI_HOSTNAME="rmi-alpha.cam.uchc.edu" RMI_PORT_HTTP="-1"   RMI_PORT_HIGH="40112" JMX_PORT_HTTP="-1"    JMX_PORT_HIGH="12311"></site> 
	 */
	static {
		add(new SiteInfo("rel", "rel",8080));
		add(new SiteInfo("rel", "rel",40105));
		add(new SiteInfo("beta", "beta",8080));
		add(new SiteInfo("beta", "beta",40105));
		add(new SiteInfo("alpha", "alpha",40106));
		add(new SiteInfo("test", "alpha",40110));
		add(new SiteInfo("test2", "alpha",40111));
		add(new SiteInfo("test3", "alpha",40112));
	}
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: site username password");
			System.exit(99);
		}
		//redirect standard error to standard out, easier for scripts to deal with
		System.setErr(System.out);
		String site = args[0];
		boolean match = false;
		boolean good = true;
		for (SiteInfo si : sites) {
			if (site.equals(si.name)) {
				match = true;
				good &= attemptLogin(si, args[1],args[2]);
			}
		}
		if (!match) {
			System.err.println("unrecognized site " + site);
			System.exit(2);
		}
		if (!good) {
			System.exit(1);
		}
		System.exit(0);
	}
	
	private static boolean attemptLogin(SiteInfo si, String user, String password) {
		try {
			String url = si.bootStrapUrl();
			VCellBootstrap vcellBootstrap = (VCellBootstrap) java.rmi.Naming.lookup(url);
			DigestedPassword dp = new UserLoginInfo.DigestedPassword(password);
			UserLoginInfo uli = new UserLoginInfo(user,dp);
			VCellConnection vcellConnection = vcellBootstrap.getVCellConnection(uli);
			if (vcellConnection==null){
				return false;
			}
			UserMetaDbServer dataServer = vcellConnection.getUserMetaDbServer();
			@SuppressWarnings("unused")
			BioModelInfo[] bmi = dataServer.getBioModelInfos(false);
			return true;
		} catch (MalformedURLException e) { 
			e.printStackTrace();
			throw new Error("bad code");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * no point in creating objects
	 */
	private LoginChecker( ) {}
}
