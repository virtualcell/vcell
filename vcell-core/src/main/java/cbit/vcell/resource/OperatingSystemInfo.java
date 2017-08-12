/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.resource;

import java.util.regex.Pattern;

import cbit.vcell.util.NativeLoader;

/**
 * Singleton class / object which models operating system specific data.
 * initializes on first explicit access 
 * @author gweatherby
 */
public class OperatingSystemInfo {
	/**
	 * specify supported operating systems 
	 */
	public enum OsType {
		LINUX("Linux operating system", true),
		MAC("OS X",true),
		WINDOWS("Microsoft Windows",false);
		
		private final String desc;
		private final boolean unixLike; 

		private OsType(String desc, boolean unixLike) {
			this.desc = desc;
			this.unixLike = unixLike;
		}

		@Override
		public String toString() {
			return desc; 
		}

		/**
		 * @return true if linux or mac
		 */
		public boolean isUnixLike() {
			return unixLike;
		}
	}

	private final String osnamePrefix;
	private final String exeBitSuffix;
	private final String exeSuffix;
	private final String nativelibSuffix;
	private final Pattern sharedLibraryPattern; 
	private String description; 
	//private final boolean bWindows; 
	//private final boolean bMac;
	//private final boolean bLinux;
	private final OsType osType;
	
	private final boolean b64bit;
	/**
	 * name of native library directory (leaf only)
	 */
	private final String nativeLibDirectory; 
	
	/**
	 * @return Singleton instance
	 */
	public static OperatingSystemInfo getInstance( ) {
		return OSIHolder.INSTANCE;
	}

	/**
	 * @return exe suffix if any, decorated for 64 bit 
	 */
	public String getExeBitSuffix() {
		return exeBitSuffix;
	}

	/**
	 * @return exe suffix, if any 
	 */
	public String getExeSuffix() {
		return exeSuffix;
	}
	/**
	 * @return suffix for 64 bit systems, if applicable
	 */
	public String getNativelibSuffix() {
		return nativelibSuffix;
	}

	/**
	 * @return win, mac, linux
	 */
	public String getOsnamePrefix() {
		return osnamePrefix;
	}

	/**
	 * @return operating system and bit specific directory name with trailing /
	 */

	public String getNativeLibDirectory() {
		return nativeLibDirectory;
	}

	public OsType getOsType() {
		return osType;
	}

	/**
	 * specific OS query (use {@link #getOsType()} for more general usage
	 * @return true if Windows
	 */
	public boolean isWindows() {
		return osType == OsType.WINDOWS; 
	}

	/**
	 * specific OS query (use {@link #getOsType()} for more general usage
	 * @return true if Mac 
	 */
	public boolean isMac() {
		return osType == OsType.MAC; 
	}

	/**
	 * specific OS query (use {@link #getOsType()} for more general usage
	 * @return true if Linux 
	 */
	public boolean isLinux() {
		return osType == OsType.LINUX; 
	}
	
	public boolean is64bit() {
		return b64bit;
	}

	@Override
	public String toString() {
		return description; 
	}

	/**
	 * @return pattern which matches shared libraries on system
	 */
	public Pattern getSharedLibraryPattern() {
		return sharedLibraryPattern;
	}

	//	Implementation note: this was refactored from static data / initializer block in 
	//	ResourceUtil because information messages (e.g. log4j) were being lost due to occurring
	//	before redirection of standard out to user writable directory. 
	private OperatingSystemInfo( ) {
		final String system_osname = System.getProperty("os.name");
		final String system_osarch = System.getProperty("os.arch");
		b64bit = system_osarch.endsWith("64");
		boolean bWindows = system_osname.contains("Windows");
		boolean bMac = system_osname.contains("Mac");
		boolean bLinux = system_osname.contains("Linux");
		if (bWindows) {
			osType = OsType.WINDOWS;
			osnamePrefix = "win";
			exeSuffix = ".exe";
			sharedLibraryPattern = Pattern.compile(".dll$");
			NativeLoader.setOsType(OperatingSystemInfo.OsType.WINDOWS);
		} else if (bMac) {
			osType = OsType.MAC;
			osnamePrefix = "mac";
			exeSuffix = "";
			sharedLibraryPattern = Pattern.compile(".*[dylib|jnilib]$");
			NativeLoader.setOsType(OperatingSystemInfo.OsType.MAC);
		} else if (bLinux) {
			osType = OsType.LINUX;
			osnamePrefix = "linux";
			exeSuffix = "";
			sharedLibraryPattern = Pattern.compile(".+\\.so[.\\d]*$"); //libc.so, libc.so.1, libc.so.1.4
			NativeLoader.setOsType(OperatingSystemInfo.OsType.LINUX);
		} else {
			throw new RuntimeException(system_osname + " is not supported by the Virtual Cell.");
		}
		description = osnamePrefix;
		String BIT_SUFFIX = "";
		if (b64bit) {
			BIT_SUFFIX ="_x64";
			description += "64";
		}
		else {
			description += "32";
		}
		exeBitSuffix = BIT_SUFFIX + exeSuffix;
		nativeLibDirectory =   osnamePrefix + (b64bit ? "64/" : "32/");
		nativelibSuffix = BIT_SUFFIX;
	}
	
	/**
	 * Bill Pugh double-checked locking
	 */
	private static class OSIHolder {
		private static final OperatingSystemInfo INSTANCE = new OperatingSystemInfo();
	}

}