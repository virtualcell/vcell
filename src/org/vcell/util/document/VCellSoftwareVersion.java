package org.vcell.util.document;

import java.io.Serializable;

import org.vcell.util.PropertyLoader;

public class VCellSoftwareVersion implements Serializable {
	
	public static enum VCellSite {
		alpha,
		beta,
		release,
		other,
		unknown
	};
	
	private String softwareVersionString = null;
	private VCellSite vcellSite = VCellSite.unknown;
	private String buildNumber = "";
	private String versionNumber = "";

	private VCellSoftwareVersion(String softwareVersionString){
		this.softwareVersionString = softwareVersionString;
		if (softwareVersionString!=null){
			try {
				java.util.StringTokenizer stk = new java.util.StringTokenizer(softwareVersionString, "_");
				//
				// parse edition (alpha, beta, etc).
				//
				String EDITION = stk.nextToken();
				if (VCellSite.alpha.toString().equalsIgnoreCase(EDITION)){
					vcellSite = VCellSite.alpha;
				}else if (VCellSite.beta.toString().equalsIgnoreCase(EDITION)){
					vcellSite = VCellSite.beta;
				}else if (VCellSite.release.toString().equalsIgnoreCase(EDITION)){
					vcellSite = VCellSite.release;
				}else{
					vcellSite = VCellSite.other;
				}
				
				
				if (!stk.nextToken().equalsIgnoreCase("Version")) throw new RuntimeException("Expecting 'Version'");
				versionNumber = stk.nextToken();
				
				if (!stk.nextToken().equalsIgnoreCase("build")) throw new RuntimeException("Expecting 'build'");
				buildNumber = stk.nextToken();
				
			} catch (Exception exc) {
				// deliberately ignored to reduce clutter (10K's of "unknown" versions in database).
				vcellSite = VCellSite.unknown;
				buildNumber = "";
				versionNumber = "";
			}
		}
	}
	
	public static VCellSoftwareVersion fromString(String softwareVersion) {
		return new VCellSoftwareVersion(softwareVersion);
	}
	
	public VCellSite getSite(){
		return vcellSite;
	}
	
	public String getSoftwareVersionString(){
		return softwareVersionString;
	}
	
	public String getDescription(){
		if (getSite().equals(VCellSite.unknown)){
			if (softwareVersionString==null){
				return "unknown VCell version";
			}else{
				return softwareVersionString;
			}
		}
		return "VCell "+getSite().name()+" version "+versionNumber+" build "+buildNumber;
	}

	public static VCellSoftwareVersion fromSystemProperty() {
		String systemProperty = PropertyLoader.getProperty(PropertyLoader.vcellSoftwareVersion, null);
		if (systemProperty == null){
			return new VCellSoftwareVersion(null);
		}else{
			return fromString(systemProperty);
		}
	}
	

	public String getVersionNumber() {
		return versionNumber;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

}
