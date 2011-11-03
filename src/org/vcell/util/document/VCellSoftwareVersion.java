package org.vcell.util.document;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.vcell.util.PropertyLoader;

public class VCellSoftwareVersion implements Serializable {
	public static enum VCellSite {
		ALPHA,
		BETA,
		RELEASE,
		OTHER,
		UNKNOWN
	};
	
	private String softwareVersionString = null;

	private VCellSoftwareVersion(String softwareVersionString){
		this.softwareVersionString = softwareVersionString;
	}
	
	public static VCellSoftwareVersion fromString(String softwareVersion) {
		return new VCellSoftwareVersion(softwareVersion);
	}
	
	public String getVersionString(){
		if (softwareVersionString==null){
			return null;
		}
		final String versionString = "version_";
		if (softwareVersionString.toLowerCase().contains("version_")){
			StringTokenizer tokens = new StringTokenizer(softwareVersionString.toLowerCase(),"_",false);
			while (tokens.hasMoreTokens()){
				String token = tokens.nextToken();
				if (token.equals(versionString)){
					if (tokens.hasMoreTokens()){
						token = tokens.nextToken();
						return token;
					}
				}
			}
		}
		return null;
	}
	
	public VCellSite getSite(){
		if (softwareVersionString == null){
			return VCellSite.UNKNOWN;
		}
		if (softwareVersionString.toLowerCase().contains("alpha")){
			return VCellSite.ALPHA;
		}
		if (softwareVersionString.toLowerCase().contains("beta")){
			return VCellSite.BETA;
		}
		if (softwareVersionString.toLowerCase().contains("release")){
			return VCellSite.RELEASE;
		}
		return VCellSite.OTHER;
	}
	
	public String getSoftwareVersionString(){
		return softwareVersionString;
	}

	public static VCellSoftwareVersion fromSystemProperty() {
		String systemProperty = PropertyLoader.getProperty(PropertyLoader.vcellSoftwareVersion, null);
		if (systemProperty == null){
			return null;
		}else{
			return fromString(systemProperty);
		}
	}

}
