/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import org.vcell.util.logging.NoLogging;

import cbit.vcell.resource.PropertyLoader;

@SuppressWarnings("serial")
@NoLogging
public class VCellSoftwareVersion implements Serializable {
	
	public static enum VCellSite {
		alpha,
		beta,
		rel,
		other,
		unknown
	};
	
	private String softwareVersionString = null;
	private VCellSite vcellSite = VCellSite.unknown;
	private String buildNumber = "";
	private String versionNumber = "";
	private int majorVersion  = -1;
	private int minorVersion = -1;
	private int buildInt = -1; 
	/**
	 * lazy evaluate {@link #fromSystemProperty()} version
	 */
	private static WeakReference<VCellSoftwareVersion> sysPropVersion = null;

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
				}else if (VCellSite.rel.toString().equalsIgnoreCase(EDITION)){
					vcellSite = VCellSite.rel;
				}else{
					vcellSite = VCellSite.other;
				}
				
				
				if (!stk.nextToken().equalsIgnoreCase("Version")) throw new RuntimeException("Expecting 'Version'");
				versionNumber = stk.nextToken();
				
				if (!stk.nextToken().equalsIgnoreCase("build")) throw new RuntimeException("Expecting 'build'");
				buildNumber = stk.nextToken();
				buildInt = safeParse(buildNumber);
				int dot = versionNumber.indexOf('.');
				if (dot >= 0) {
					String major = versionNumber.substring(0, dot);
					String minor = versionNumber.substring(++dot);
					majorVersion = safeParse(major);
					minorVersion = safeParse(minor);
				}
			} catch (Exception exc) {
				// deliberately ignored to reduce clutter (10K's of "unknown" versions in database).
				vcellSite = VCellSite.unknown;
				buildNumber = "";
				versionNumber = "";
			}
		}
	}

	/**
	 * return value of in, if parseable
	 * @param in
	 * @return value of in or -1 if null or number a valid integer
	 */
	private static int safeParse(String in) {
		if (in != null) {
			try {
				return Integer.parseInt(in);
			}
			catch (NumberFormatException nfe) {
				return -1;
			}
		}
		return -1;
	}
	
	@NoLogging
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

	@NoLogging
	public static VCellSoftwareVersion fromSystemProperty() {
		VCellSoftwareVersion sysVers = sysPropVersion != null ? sysPropVersion.get() : null;
		if (sysVers == null) {
			String systemProperty = PropertyLoader.getProperty(PropertyLoader.vcellSoftwareVersion, null);
			if (systemProperty == null){
				sysVers = new VCellSoftwareVersion(null);
			}else{
				sysVers = fromString(systemProperty);
			}
			sysPropVersion = new WeakReference<VCellSoftwareVersion>(sysVers);
		}
		return sysVers; 
	}
	

	public String getVersionNumber() {
		return versionNumber;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getBuildInt() {
		return buildInt;
	}
}
