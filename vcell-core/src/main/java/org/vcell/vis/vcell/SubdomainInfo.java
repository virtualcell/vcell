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
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VCML;

public class SubdomainInfo {
	public record CompartmentSubdomainInfo(String name, int handle) {}

	public record MembraneSubdomainInfo(
			String name,
			CompartmentSubdomainInfo insideCompartmentSubdomainInfo,
			CompartmentSubdomainInfo outsideCompartmentSubdomainInfo
	) {}

	private final List<CompartmentSubdomainInfo> compartmentSubdomainInfos;
	private final List<MembraneSubdomainInfo> membraneSubdomainInfos;

	public SubdomainInfo(List<CompartmentSubdomainInfo> compartmentSubdomainInfos,
	                     List<MembraneSubdomainInfo> membraneSubdomainInfos) {
		super();
		this.compartmentSubdomainInfos = compartmentSubdomainInfos;
		this.membraneSubdomainInfos = membraneSubdomainInfos;
	}

	public List<String> getMembraneDomainNames() {
		return this.membraneSubdomainInfos.stream().map(MembraneSubdomainInfo::name).toList();
	}

	public String getCompartmentSubdomainName(int handle) throws MathException {
		return this.compartmentSubdomainInfos.stream()
				.filter(elem -> elem.handle == handle)
				.map(CompartmentSubdomainInfo::name)
				.findFirst().orElseThrow(MathException::new);
	}

	public String getMembraneSubdomainName(int firstHandle, int secondHandle) throws MathException {
		MembraneSubdomainInfo msi = this.getMembraneSubdomainInfo(firstHandle, secondHandle);
		if (msi != null) return msi.name();
		throw new MathException("Unexpected membrane subdomain between subdomains " + firstHandle + " and " + secondHandle + ".");
	}

	public int getInside(int firstHandle, int secondHandle) throws MathException {
		// Get the MSI related to the two handles
		MembraneSubdomainInfo msi = this.getMembraneSubdomainInfo(firstHandle, secondHandle);
		if (msi != null) return msi.insideCompartmentSubdomainInfo().handle();
		throw new MathException("Unexpected membrane between subdomains " + firstHandle + " and " + secondHandle + ".");
	}

	public MembraneSubdomainInfo getMembraneSubdomainInfo(String domainName) {
		return this.membraneSubdomainInfos.stream()
				.filter(elem -> elem.name().equals(domainName))
				.findFirst().orElseThrow();
	}

	private MembraneSubdomainInfo getMembraneSubdomainInfo(int firstHandle, int secondHandle) {
		for (MembraneSubdomainInfo memSubdomainInfo : this.membraneSubdomainInfos) {
			int insideHandle = memSubdomainInfo.insideCompartmentSubdomainInfo().handle();
			int outsideHandle = memSubdomainInfo.outsideCompartmentSubdomainInfo().handle();
			if (insideHandle == firstHandle && outsideHandle == secondHandle || insideHandle == secondHandle && outsideHandle == firstHandle) {
				return memSubdomainInfo;
			}
		}
		return null;
	}

	public static void write(File file, MathDescription mathDesc) throws IOException, MathException {
		try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
			pw.println("# " + VCML.CompartmentSubDomain + " name, handle");
			pw.println("# " + VCML.MembraneSubDomain + " name, inside compartment name, handle, outside compartment name, handle");
			Enumeration<SubDomain> subdomains = mathDesc.getSubDomains();
			while (subdomains.hasMoreElements()) {
				SubDomain sd = subdomains.nextElement();
				if (sd instanceof CompartmentSubDomain csd) {
					pw.println(VCML.CompartmentSubDomain + ", " + csd.getName() + ", " + mathDesc.getHandle(csd));
				} else if (sd instanceof MembraneSubDomain msd) {
					CompartmentSubDomain insideCompartment = msd.getInsideCompartment();
					CompartmentSubDomain outsideCompartment = msd.getOutsideCompartment();
					pw.println(VCML.MembraneSubDomain + ", " + msd.getName() + ", " + insideCompartment.getName() + ", " + mathDesc.getHandle(insideCompartment)
							+ ", " + outsideCompartment.getName() + ", " + mathDesc.getHandle(outsideCompartment));
				}
			}
		}
	}

	public static SubdomainInfo read(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			return SubdomainInfo.read(br);
		}
	}

	public static SubdomainInfo read(BufferedReader br) throws IOException {
		List<CompartmentSubdomainInfo> compList = new ArrayList<>();
		List<MembraneSubdomainInfo> memList = new ArrayList<>();
		while (true) {
			String line = br.readLine();
			if (line == null) break;
			line = line.trim();
			if (line.startsWith("#")) continue;

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
		return new SubdomainInfo(compList, memList);
	}
}
