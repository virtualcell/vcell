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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;

import org.vcell.util.PropertyLoader;

@SuppressWarnings("serial")
public class UserLoginInfo implements Serializable {
	private String userName;
	private DigestedPassword digestedPassword;// obfuscate password
	private String os_name;// os.name Operating system name
	private String os_arch;// os.arch Operating system architecture
	private String os_version;// os.version Operating system version
	private String java_version;// java.version JRE version number
	private String vcellSoftwareVersion;// VCell client logging in from
	private User user;
	// clientId to indentify machine so that
	// same user can login at the same time.
	private final long clientId = System.currentTimeMillis();

	public UserLoginInfo(String userName, DigestedPassword digestedPassword) {
		super();
		this.userName = userName;
		this.digestedPassword = digestedPassword;
		os_name = System.getProperty("os.name");
		os_arch = getArchitecture(os_name);
		os_version = System.getProperty("os.version");
		java_version = System.getProperty("java.version");
		vcellSoftwareVersion = System
				.getProperty(PropertyLoader.vcellSoftwareVersion);
	}

	/**
	 * use os.arch System property to identify architecture. If Windows,
	 * supplement information with 32 or 64 bit
	 * 
	 * @return String identifying architecture
	 */
	private static String getArchitecture(String osName) {
		String arch = System.getProperty("os.arch");
		if (osName.contains("Windows")) {
			final boolean is64bit = (System.getenv("ProgramFiles(x86)") != null);
			if (is64bit) {
				arch += " - 64bit";
			} else {
				arch += " - 32bit";
			}
		} else if (osName.contains("Linux")) {
			try {
				BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
				try {
					String line = br.readLine();
					while (line != null) {
						if (line.startsWith("flags")) {
							for (String flag : line.split("\\s+")) {
								if (flag.equals("lm")) {
									arch += " - 64bit";
									break;
								}
								if (flag.equals("tm")) {
									arch += " - 32bit";
									break;
								}
								if (flag.equals("rm")) {
									arch += " - 16bit"; // unlikely, but for
														// completeness
									break;
								}
							}
							break; // there is a flags entry for each cpu, but
									// the
									// first one should be good enough
						}
						line = br.readLine();
					}
				} finally {
					br.close();
				}

			} catch (Exception e) {
				System.err
						.println("Exception trying to determine Linux bit size");
				e.printStackTrace();
			}
		}

		return arch;
	}

	public static class DigestedPassword implements Serializable {
		private String digestedPasswordStr;

		private DigestedPassword() {

		}

		public DigestedPassword(String clearTextPassword) {
			this.digestedPasswordStr = createdDigestPassword(clearTextPassword);
		}

		private static String createdDigestPassword(String clearTextPassword) {
			if (clearTextPassword == null || clearTextPassword.length() == 0) {
				throw new RuntimeException("Empty password not allowed");
			}
			java.security.MessageDigest messageDigest = null;
			try {
				messageDigest = java.security.MessageDigest
						.getInstance("SHA-1");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(
						"Error processing password, Couldn't get instance of MessageDigest "
								+ e.getMessage());
			}
			messageDigest.reset();
			messageDigest.update(clearTextPassword.getBytes());
			byte[] digestedPasswordBytes = messageDigest.digest();
			StringBuffer sb = new StringBuffer(digestedPasswordBytes.length * 2);
			for (int i = 0; i < digestedPasswordBytes.length; i++) {
				int v = digestedPasswordBytes[i] & 0xff;
				if (v < 16) {
					sb.append('0');
				}
				sb.append(Integer.toHexString(v));
			}
			return sb.toString().toUpperCase();
		}

		public static DigestedPassword createAlreadyDigested(
				String alreadyDigestedPassword) {
			DigestedPassword newDigestedPassword = new DigestedPassword();
			newDigestedPassword.digestedPasswordStr = alreadyDigestedPassword.toUpperCase();
			return newDigestedPassword;

		}

		public String getString() {
			return digestedPasswordStr;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DigestedPassword) {
				return ((DigestedPassword) obj).digestedPasswordStr
						.equals(this.digestedPasswordStr);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return digestedPasswordStr.hashCode();
		}

	}

	public String getOs_name() {
		return os_name;
	}

	public String getOs_arch() {
		return os_arch;
	}

	public String getOs_version() {
		return os_version;
	}

	public String getJava_version() {
		return java_version;
	}

	public String getUserName() {
		return userName;
	}

	public DigestedPassword getDigestedPassword() {
		return digestedPassword;
	}

	public String getVCellSoftwareVersion() {
		return vcellSoftwareVersion;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) throws Exception {
		if (user.compareEqual(this.user)) {
			// Happens during 'reconnect'
			return;
		}
		if (this.user != null) {
			// Different user never allowed to be reset
			// During 'login' or 'change user' a new UserLoginInfo should be
			// created
			throw new Exception(
					"UserLoginInfo: unexpected 'set' of different user.");
		}
		// set 'user' name must be the same as the 'login' name
		if (!user.getName().equals(this.userName)) {
			throw new Exception("UserLoginInfo: 'set' user " + user.getName()
					+ " does not equal login name " + this.userName);
		}
		this.user = user;
	}

	public final long getClientId() {
		return clientId;
	}
}
