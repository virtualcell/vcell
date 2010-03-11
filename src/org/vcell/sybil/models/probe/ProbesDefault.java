package org.vcell.sybil.models.probe;

import org.vcell.sybil.models.specs.SybilSpecs;
import org.vcell.sybil.util.sbml.LibSBMLUtil;
import org.vcell.sybil.util.sbml.LibSBMLUtil.LibSBMLException;

/*   ProbesDefault  --- by Oliver Ruebenacker, UCHC --- February 2010
 *   Information about the platform and runtime environment
 */

public class ProbesDefault {

	public static class SystemPropertyProbe extends Probe.Element.Abstract implements Probe.Element {
		protected final String propertyKey;
		public SystemPropertyProbe(String key, String propertyKey) {
			super(key);
			this.propertyKey = propertyKey;
		}
		public String result() { return System.getProperty(propertyKey); }
	}
	
	public static final Probe.Element javaVersion = 
		new SystemPropertyProbe("Java Version", "java.version");
	public static final Probe.Element javaVendor = 
		new SystemPropertyProbe("Java Vendor", "java.vendor");
	public static final Probe.Element javaVendorURL = 
		new SystemPropertyProbe("Java Vendor URL", "java.vendor.url");
	
	public static final Probe.Group javaInfo =
		new Probe.Group.Default("Java Info", javaVersion, javaVendor, javaVendorURL);
	
	public static final Probe.Element osArch = 
		new SystemPropertyProbe("OS Architecture", "os.arch");
	public static final Probe.Element osName = 
		new SystemPropertyProbe("OS Name", "os.name");
	public static final Probe.Element osVersion = 
		new SystemPropertyProbe("OS Version", "os.version");

	public static final Probe.Group osInfo =
		new Probe.Group.Default("OS Info", osArch, osName, osVersion);
	
	public static final Probe.Element jreMemoryFree = 
		new Probe.Element.Abstract("JRE Free Memory") {			
		public String result() { return Long.toString(Runtime.getRuntime().freeMemory()); }
	};

	public static final Probe.Element jreMemoryTotal = 
		new Probe.Element.Abstract("JRE Total Memory") {			
		public String result() { return Long.toString(Runtime.getRuntime().totalMemory()); }
	};

	public static final Probe.Element jreMemoryMax = 
		new Probe.Element.Abstract("JRE Maximal Memory") {			
		public String result() { return Long.toString(Runtime.getRuntime().maxMemory()); }
	};

	public static final Probe.Element jreProcessors = 
		new Probe.Element.Abstract("JRE Available Processors") {			
		public String result() { return Integer.toString(Runtime.getRuntime().availableProcessors()); }
	};

	public static final Probe.Group jreResources =
		new Probe.Group.Default("JRE Resources", jreMemoryFree, jreMemoryTotal, jreMemoryMax, 
				jreProcessors);

	public static final Probe.Element sybilVersion = 
		new Probe.Element.Fixed("SyBiL Version", SybilSpecs.shortText);

	public static final Probe.Element libSBMLVersion = 
		new Probe.Element.Abstract("LibSBML Version") {			
		public String result() { 
			try { return LibSBMLUtil.shortDescription(); } 
			catch (LibSBMLException e) {
				e.printStackTrace();
				return e.getMessage();
			} 
		}
	};
	
	public static final Probe.Element libSBMLXMLWorking = 
		new Probe.Element.Abstract("LibSBML XML") {
			public String result() { return LibSBMLUtil.xmlIsWorking() ? "Working" : "Not Working"; }
	};

	public static final Probe.Group applicationInfo =
		new Probe.Group.Default("Application Info", sybilVersion, libSBMLVersion, libSBMLXMLWorking);

	public static final Probe.Group probesAll =
		new Probe.Group.Default("All Default Probes", javaInfo, osInfo, jreResources, applicationInfo);
	
}
