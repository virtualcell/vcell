package cbit.vcell.message.jms.activeMQ.monitor;
/* From:
 Instant Apache ActiveMQ Messaging Application Development How-to
Timothy Bish
Published 2013-05-23
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cbit.vcell.message.jms.VCJmsConfig;

public class VCActiveMQMonitor  implements VCJmsConfig{
	private static final Logger LG = LogManager.getLogger(VCActiveMQMonitor.class);
	private SimpleDateFormat dateFormat;
	private ScheduledExecutorService executorService;
	private List<SiteMonitor> siteMonitors = new ArrayList<>();
   
	public static void main(String[] args) {
		try {
			if (args.length > 0) {
				VCActiveMQMonitor mon = new VCActiveMQMonitor( );
				mon.startMonitors(args[0]);
			}
			else {
				System.out.println("Usage: [configuration xml file]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	public VCActiveMQMonitor() {
    	dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		executorService = new ScheduledThreadPoolExecutor(1);
	}

	public void startMonitors(String filename) throws Exception {
		File xml = new File(filename);
		if (!xml.canRead()) {
			throw new IOException("Can't read " + xml.getAbsolutePath());
		}
		SAXBuilder builder = new SAXBuilder();
		Document config = builder.build(xml);
		
		String deployFilename = null;
		List<SiteConfig> sites = new ArrayList<>();
		
		{
			Element r = config.getRootElement();
			Element f = requireElement(r, "deployXml");
			deployFilename = f.getText();
			if (LG.isInfoEnabled()) {
				LG.info("reading configuration from" + deployFilename);
			}
			Element s = requireElement(r, "sites");
			@SuppressWarnings("unchecked")
			List<Element> children = s.getChildren("site");;
			for (Element child :children) {
				SiteConfig sc = parseMonitor(child);
				LG.info(sc);
				sites.add(sc);
			}
		}
		
		Document doc = builder.build(deployFilename);
		Element root = doc.getRootElement();
		Element dp = requireElement(root,DEPLOYPROP);
		Element jmsProv = requireElement(dp,PROVIDER);
		
		Runnable sd = ( ) -> closeMonitors( );
		Runtime.getRuntime().addShutdownHook(new Thread(sd));
		String now = dateFormat.format(new Date( ));
		
		for (SiteConfig siteConfig: sites) {
			SiteUrl su = parseSite(jmsProv, siteConfig.name);
			if (LG.isDebugEnabled()) {
				LG.debug(jmsProv.getName() + " parsed to " + su);
			}
			if (su != null) {
				String logname = su.jmsName + ".log";
				if (LG.isInfoEnabled()){
					LG.debug("logging " + su.jmsName + " to " + logname);
				}
				PrintWriter pw = new PrintWriter(new FileWriter(logname,true)); //true -> append
				pw.println("Commencing log of " + su.jmsName + " at " + now);
				SiteMonitor sm = new SiteMonitor(su.jmsName,pw,su.url);
				siteMonitors.add(sm);
				sm.start();
				
				int tstamp = siteConfig.timestampSeconds;
				if (tstamp > 0 ) {
					Runnable r = ( ) -> timestamp(pw);
					executorService.scheduleAtFixedRate(r,tstamp,tstamp,TimeUnit.SECONDS);
				}
				int flush = siteConfig.flushSeconds;
				if (flush > 0 ) {
					Runnable r = ( ) -> flush(pw);
					executorService.scheduleAtFixedRate(r,flush,flush,TimeUnit.SECONDS);
				}
			}
		}
	}
	private void timestamp(PrintWriter pw) {
		pw.println(dateFormat.format(new Date( )));
	}

	private void flush(PrintWriter pw) {
		pw.flush( );
	}

	/**
	 * shutdown hook
	 */
	private void closeMonitors() {
		LG.info("Shutting down monitors");
		String msg = "Monitor shut down at " + dateFormat.format(new Date( ));
		for (SiteMonitor sm : siteMonitors) {
			try {
				sm.getWriter().println(msg);
				sm.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Element requireElement(Element e, String name) {
		Element rval = e.getChild(name);
		if (rval != null) return rval;
		throw new RuntimeException("Element " +e + " missing required " + name);
	}
	
	
	
	/**
	 * data from activemqmonitor XML file
	 */
	private static class SiteConfig {
		final String name;
		final int timestampSeconds;
		final int flushSeconds;
		public SiteConfig(String name, int timestampSeconds, int flushSeconds) {
			super();
			this.name = name;
			this.timestampSeconds = timestampSeconds;
			this.flushSeconds = flushSeconds;
		}
		@Override
		public String toString() {
			return "SiteConfig [name=" + name + ", timestampSeconds="
					+ timestampSeconds + ", flushSeconds=" + flushSeconds + "]";
		}
		
	}
	
	/**
	 * get integer value from attribute
	 * @param site
	 * @param name
	 * @return value or 0 if not present
	 * @throws IllegalArgumentException if invalid string in file
	 */
	private static int intValue(Element site, String name) {
		 Attribute e = site.getAttribute(name);
		 if (e != null) {
			 try {
				 return Integer.parseInt(e.getValue());
			 }
			 catch (NumberFormatException nfe) {
				 throw new IllegalArgumentException("Invalid integer " + e.getValue(  ) + " for " + name);
			 }
		 }
		 return 0;
	}
	
	private static SiteConfig parseMonitor(Element site) {
			String name = site.getAttributeValue("name");
			if (name == null) {
				throw new IllegalArgumentException("site element missing name");
			}
		try {
			int tstamp = intValue(site,"timestamp");
			int flush = intValue(site,"flush");
			return new SiteConfig(name,tstamp,flush);
		} catch (Exception e) {
			throw new RuntimeException("error parsing " + site, e);
		}
	}
	/**
	 * data from DeployVCell XML file
	 */
	private static class SiteUrl {
		final String jmsName;
		final String url;
		public SiteUrl(String jmsName, String url) {
			super();
			this.jmsName = jmsName;
			this.url = url;
		}
		
		@Override
		public String toString() {
			return "SiteUrl [jmsName=" + jmsName + ", url=" + url + "]";
		}
	}
	
	private static SiteUrl parseSite(Element jmsProv, String name) {
		String u = name.toUpperCase();
		String attr = jmsProv.getAttributeValue(u);
		if (attr != null) {
			 Element jmsLine = jmsProv.getChild(attr);
			 if (jmsLine != null) {
				 String type = jmsLine.getAttributeValue(PROVIDER_TYPE);
				 if (type.equals("ActiveMQ")) {
					 String url = jmsLine.getAttributeValue(PROVIDER_URL);
					 if (url != null) {
						 String sn = WordUtils.capitalize(name.toLowerCase());
						 return new SiteUrl(sn,url);
					 }
				 }
			 }
		}
		return null;
	}
	
	
}