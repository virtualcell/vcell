package cbit.vcell.message.server.console;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
	private static final boolean VERBOSE = System.getProperty("LOGINCHECK_VERBOSE") != null;
	private static final long SOCKET_TIMEOUT_MINUTES = 1;
	public static class SiteInfo {
		final String name;
		final String host;
		final int port;
		public SiteInfo(String name, String host, int port) {
			super();
			this.name = name;
			this.host = DOMAIN.replace("SITE", host);
			this.port = port;
		}
		
		public String bootStrapUrl( ) {
			return "//" + host   + ":" + port + SERVICE;
		}
		
		@Override
		public String toString( ) {
			return name + " on " + bootStrapUrl( );
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
		add(new SiteInfo("beta", "beta",40105));
		add(new SiteInfo("beta", "beta",8080));
		add(new SiteInfo("alpha", "alpha",40106));
		add(new SiteInfo("test", "alpha",40110));
		add(new SiteInfo("test2", "alpha",40111));
		add(new SiteInfo("test3", "alpha",40112));
		add(new SiteInfo("test4", "alpha",40113));
	}
	
	
	/**
	 * get {@link SiteInfo} matching name
	 * @param siteName
	 * @return Collection of matching sites (could be empty)
	 */
	public static Collection<SiteInfo> getSiteInfos(String siteName) {
		ArrayList<SiteInfo> filtered = new ArrayList<>();
		for (SiteInfo si : sites) {
			if (Objects.equals(si.name, siteName)) {
				filtered.add(si);
			}
		}
		return filtered;
	}
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: site username password");
			System.exit(99);
		}
		final long timeOut = TimeUnit.MINUTES.toMillis(SOCKET_TIMEOUT_MINUTES);
		final String timeOutString = Long.toString(timeOut);
		System.setProperty("sun.rmi.transport.tcp.responseTimeout",timeOutString); 
		//redirect standard error to standard out, easier for scripts to deal with
		System.setErr(System.out);
		String site = args[0];
		boolean good = true;
		Collection<SiteInfo> matching = getSiteInfos(site);
		if (!matching.isEmpty()) {
			for (SiteInfo si : matching) {
				final boolean attempt =  attemptLogin(si, args[1],args[2]);
				if (!attempt) {
					System.out.println("login failed " + si);
				}
				good &= attempt;
			}
		}
		else {
			System.err.println("unrecognized site " + site);
			System.exit(2);
		}
		if (!good) {
			System.exit(1);
		}
	}
	
	/**
	 * @param start
	 * @return time between now and start in seconds
	 */
	private static double elapsed(long start) {
		double now = System.currentTimeMillis();
		return (now -start) / 1000;
	}
	
	
	private static boolean attemptLogin(SiteInfo si, String user, String password) {
		long start = 0;
		try {
			if (VERBOSE) {
				start = System.currentTimeMillis();
			}
			String url = si.bootStrapUrl();
			VCellBootstrap vcellBootstrap = (VCellBootstrap) java.rmi.Naming.lookup(url);
			DigestedPassword dp = new UserLoginInfo.DigestedPassword(password);
			UserLoginInfo uli = new UserLoginInfo(user,dp);
			VCellConnection vcellConnection = vcellBootstrap.getVCellConnection(uli);
			if (vcellConnection==null){
				if (VERBOSE) {
					System.out.println("no connection on " + si + " in " + elapsed(start) + " seconds");
				}
				return false;
			}
			UserMetaDbServer dataServer = vcellConnection.getUserMetaDbServer();
			@SuppressWarnings("unused")
			BioModelInfo[] bmi = dataServer.getBioModelInfos(true);
			if (VERBOSE) {
				System.out.println("success on " + si + " in " + elapsed(start) + " seconds");
			}
			return true;
		} catch (MalformedURLException e) { 
			e.printStackTrace();
			throw new Error("bad code");
		} catch (Exception e) {
			if (VERBOSE) {
				System.out.println("failed in " + elapsed(start) + " seconds");
			}
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * no point in creating objects
	 */
	private LoginChecker( ) {}
}
