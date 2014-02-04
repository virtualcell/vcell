/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vis.vcell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VCML;

public class SubdomainInfo implements Serializable {
	public static class CompartmentSubdomainInfo implements Serializable {
		private String name;
		private int handle;
		public CompartmentSubdomainInfo(String name, int handle) {
			super();
			this.name = name;
			this.handle = handle;
		}
		public final String getName() {
			return name;
		}
		public final int getHandle() {
			return handle;
		}
	}
	public static class MembraneSubdomainInfo implements Serializable {
		private String name;
		private CompartmentSubdomainInfo insideCompartmentSubdomainInfo;
		private CompartmentSubdomainInfo outsideCompartmentSubdomainInfo;
		public MembraneSubdomainInfo(String name,
				CompartmentSubdomainInfo insideCompartmentSubdomainInfo,
				CompartmentSubdomainInfo outsideCompartmentSubdomainInfo) {
			super();
			this.name = name;
			this.insideCompartmentSubdomainInfo = insideCompartmentSubdomainInfo;
			this.outsideCompartmentSubdomainInfo = outsideCompartmentSubdomainInfo;
		}
		public final CompartmentSubdomainInfo getInsideCompartmentSubdomainInfo() {
			return insideCompartmentSubdomainInfo;
		}
		public final CompartmentSubdomainInfo getOutsideCompartmentSubdomainInfo() {
			return outsideCompartmentSubdomainInfo;
		}
		public final String getName() {
			return name;
		}		
	}
	
	private CompartmentSubdomainInfo[] compartmentSubdomainInfos;
	private MembraneSubdomainInfo[] membraneSubdomainInfos;
	
	public SubdomainInfo(CompartmentSubdomainInfo[] compartmentSubdomainInfos,
			MembraneSubdomainInfo[] membraneSubdomainInfos) {
		super();
		this.compartmentSubdomainInfos = compartmentSubdomainInfos;
		this.membraneSubdomainInfos = membraneSubdomainInfos;
	}
	
	public List<String> getMembraneDomainNames(){
		ArrayList<String> membraneNames = new ArrayList<String>();
		for (MembraneSubdomainInfo msi : membraneSubdomainInfos) {
			membraneNames.add(msi.name);
		}
		return membraneNames;
	}
	
	public String getCompartmentSubdomainName(int handle) throws MathException {
		for (CompartmentSubdomainInfo csi : compartmentSubdomainInfos) {
			if (csi.getHandle() == handle) {
				return csi.getName();
			}
		}
		throw new MathException("Unexpected compartment subdomain " + handle + ".");
	}
	
	public MembraneSubdomainInfo getMembraneSubdomainInfo(String domainName){
		for (MembraneSubdomainInfo memInfo : membraneSubdomainInfos){
			if (memInfo.name.equals(domainName)){
				return memInfo;
			}
		}
		throw new RuntimeException("membrane subdomain info not found for domainName "+domainName);
	}
	
	public String getMembraneSubdomainName(int handle0, int handle1) throws MathException {
		for (MembraneSubdomainInfo msi : membraneSubdomainInfos) {
			int in = msi.getInsideCompartmentSubdomainInfo().getHandle();
			int out = msi.getOutsideCompartmentSubdomainInfo().getHandle();
			if (in == handle0 && out == handle1 || in == handle1 && out == handle0) {
				return msi.getName();
			}
		}
		throw new MathException("Unexpected membrane subdomain between subdomains " + handle0 + " and " + handle1 + "."); 
	}
	
	public int getInside(int handle0, int handle1) throws MathException {
		for (MembraneSubdomainInfo memSubdomainInfo : membraneSubdomainInfos) {
			int insideHandle = memSubdomainInfo.getInsideCompartmentSubdomainInfo().getHandle();
			int outsideHandle = memSubdomainInfo.getOutsideCompartmentSubdomainInfo().getHandle();
			if (insideHandle == handle0 && outsideHandle == handle1 || insideHandle == handle1 && outsideHandle == handle0) {
				return insideHandle;
			}
		}
		throw new MathException("Unexpected membrane between subdomains " + handle0 + " and " + handle1 + "."); 			
	}
	
	public static void write(File file, MathDescription mathDesc) throws IOException, MathException {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(file));
			pw.println("# " + VCML.CompartmentSubDomain + " name, handle");
			pw.println("# " + VCML.MembraneSubDomain + " name, inside compartment name, handle, outside compartment name, handle");
			Enumeration<SubDomain> subdomains = mathDesc.getSubDomains();
			while (subdomains.hasMoreElements()) {
				SubDomain sd = subdomains.nextElement();
				if (sd instanceof CompartmentSubDomain) {
					CompartmentSubDomain csd = (CompartmentSubDomain)sd;
					pw.println(VCML.CompartmentSubDomain + ", " + csd.getName() + ", " + mathDesc.getHandle(csd));
					
				} else if (sd instanceof MembraneSubDomain) {
					MembraneSubDomain msd = (MembraneSubDomain)sd;
					CompartmentSubDomain insideCompartment = msd.getInsideCompartment();
					CompartmentSubDomain outsideCompartment = msd.getOutsideCompartment();
					pw.println(VCML.MembraneSubDomain + ", " + msd.getName() + ", " + insideCompartment.getName() + ", " + mathDesc.getHandle(insideCompartment) 
							+ ", " + outsideCompartment.getName() + ", " + mathDesc.getHandle(outsideCompartment)); 
				}
			}
			pw.close();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
	
	public static SubdomainInfo read(File file) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			ArrayList<CompartmentSubdomainInfo> compList = new ArrayList<CompartmentSubdomainInfo>();
			ArrayList<MembraneSubdomainInfo> memList = new ArrayList<MembraneSubdomainInfo>();
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				
				if (line.startsWith("#")) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(line, " ,");
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (token.equals(VCML.CompartmentSubDomain)) {
						String name = st.nextToken();
						int handle = Integer.parseInt(st.nextToken());
						compList.add(new CompartmentSubdomainInfo(name, handle));
					} else if (token.equals(VCML.MembraneSubDomain)) {
						String name = st.nextToken();
						String insideCompartmentName = st.nextToken();
						int insideCompartmentHandle = Integer.parseInt(st.nextToken());					
						String outsideCompartmentName = st.nextToken();
						int outsideCompartmentHandle = Integer.parseInt(st.nextToken());
						memList.add(new MembraneSubdomainInfo(name, new CompartmentSubdomainInfo(insideCompartmentName, insideCompartmentHandle), new CompartmentSubdomainInfo(outsideCompartmentName, outsideCompartmentHandle)));
					}
				}
			}
			return new SubdomainInfo(compList.toArray(new CompartmentSubdomainInfo[0]), memList.toArray(new MembraneSubdomainInfo[0]));
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
}
