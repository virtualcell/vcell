/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cbit.vcell.model.*;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.document.Identifiable;

@SuppressWarnings("serial")
public class MolecularInternalLinkSpec implements Identifiable {
	private MolecularComponentPattern fieldMolecularComponentPatternOne = null;
	private MolecularComponentPattern fieldMolecularComponentPatternTwo = null;
	private double linkLength = 0;

	public MolecularInternalLinkSpec(SpeciesContextSpec scs, MolecularComponentPattern linkOne, MolecularComponentPattern linkTwo) throws IllegalArgumentException {
		SpeciesContext sc = scs.getSpeciesContext();
		SpeciesPattern sp = sc.getSpeciesPattern();
		if(sp == null || sp.getMolecularTypePatterns().size() != 1) {
			throw new IllegalArgumentException("The species pattern must contain exactly one molecule.");
		}
		MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);	// the one and only
		// sanity check
		if(linkOne == null || linkOne.getMolecularComponent() == null) {
			throw new IllegalArgumentException("A link component doesn't exist");
		}
		if(linkTwo == null || linkTwo.getMolecularComponent() == null) {
			throw new IllegalArgumentException("A link component doesn't exist");
		}
		boolean foundOne = false;
		boolean foundTwo = false;
		for(MolecularComponentPattern mpc : mtp.getComponentPatternList()) {
			if(mpc == linkOne) {
				foundOne = true;
			}
		}
		for(MolecularComponentPattern mpc : mtp.getComponentPatternList()) {
			if(mpc == linkTwo) {
				foundTwo = true;
			}
		}
		if(!foundOne || !foundTwo) {
			throw new IllegalArgumentException("A link component doesn't match any molecule component");
		}
		// order them based on position in the Molecule
		// we may only have one molecule in the species pattern for the current version of springsalad impl, but who knows in the future
		for(MolecularComponentPattern mpc : mtp.getComponentPatternList()) {
			if(mpc == linkOne) {					// linkOne comes first
				fieldMolecularComponentPatternOne = linkOne;
				fieldMolecularComponentPatternTwo = linkTwo;
				break;
			} else if(mpc == linkTwo) {				// linkTwo comes first
				fieldMolecularComponentPatternOne = linkTwo;
				fieldMolecularComponentPatternTwo = linkOne;
				break;
			}
		}
	}
	public MolecularComponentPattern getMolecularComponentPatternOne() {
		return fieldMolecularComponentPatternOne;
	}
	public MolecularComponentPattern getMolecularComponentPatternTwo() {
		return fieldMolecularComponentPatternTwo;
	}

	public double getLinkLength() {
		return linkLength;
	}
	public void setLinkLength(double linkLength) {
		this.linkLength = linkLength;
	}
	
	public Pair<MolecularComponentPattern, MolecularComponentPattern> getLink() {
		return new Pair<MolecularComponentPattern, MolecularComponentPattern>(fieldMolecularComponentPatternOne, fieldMolecularComponentPatternTwo);
	}
	public static Pair<MolecularComponentPattern, MolecularComponentPattern> getLink(MolecularInternalLinkSpec internalLink) {
		if(internalLink == null) {
			return null;
		}
		return new Pair<MolecularComponentPattern, MolecularComponentPattern>(internalLink.fieldMolecularComponentPatternOne, internalLink.fieldMolecularComponentPatternTwo);
	}
	public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {
		// TODO Auto-generated method stub
		
	}
}
