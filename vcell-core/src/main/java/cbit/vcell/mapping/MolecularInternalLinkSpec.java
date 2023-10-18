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
import java.awt.Graphics2D;
import java.io.PrintWriter;
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
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.VersionFlag;

@SuppressWarnings("serial")
public class MolecularInternalLinkSpec implements Identifiable, IssueSource, Matchable, Serializable {
	private final SpeciesContextSpec fieldSpeciesContextSpec;
	private MolecularComponentPattern fieldMolecularComponentPatternOne = null;
	private MolecularComponentPattern fieldMolecularComponentPatternTwo = null;
//	private double linkLength = 0;		// it's a derived value which we don't store, we just compute it at need

	public MolecularInternalLinkSpec(SpeciesContextSpec scs, MolecularComponentPattern linkOne, MolecularComponentPattern linkTwo) throws IllegalArgumentException {
		fieldSpeciesContextSpec = scs;
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
	public SpeciesContextSpec getSpeciesContextSpec() {
		return fieldSpeciesContextSpec;
	}
	public MolecularComponentPattern getMolecularComponentPatternOne() {
		return fieldMolecularComponentPatternOne;
	}
	public MolecularComponentPattern getMolecularComponentPatternTwo() {
		return fieldMolecularComponentPatternTwo;
	}
	public SiteAttributesSpec getSite1() {
		return getSpeciesContextSpec().getSiteAttributesMap().get(fieldMolecularComponentPatternOne);
	}
	public SiteAttributesSpec getSite2() {
		return getSpeciesContextSpec().getSiteAttributesMap().get(fieldMolecularComponentPatternTwo);
	}

	public double getX1() {
		return getSite1().getX();
	}
	public double getY1() {
		return getSite1().getY();
	}
	public double getZ1() {
		return getSite1().getZ();
	}
	public double getX2() {
		return getSite2().getX();
	}
	public double getY2() {
		return getSite2().getY();
	}
	public double getZ2() {
		return getSite2().getZ();
	}
	public double getLinkLength() {
		double dx = getX2() - getX1();
		double dy = getY2() - getY1();
		double dz = getZ2() - getZ1();
		double linkLength = Math.sqrt(dx*dx + dy*dy + dz*dz);
		return linkLength;
	}
	public double [] unitVector() {
		double dx = getX2() - getX1();
		double dy = getY2() - getY1();
		double dz = getZ2() - getZ1();
		double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
		return new double[]{dx/length, dy/length, dz/length};
	}
	
	// The conversion factor between pixels and nanometers
	// TODO: move this to some other class if appropriate
	public static final int PIXELS_PER_NM = 20;
	public static final double NM_PER_PIXEL = 0.05;
	/**
	* Given a line defined by the formula Ax + By + C = 0 and a point
	* (x0, y0), the shortest distance between the point and the line 
	* is given by r = (A x0 + B y0 + C) / sqrt(A^2 + B^2). Also, given
	* two points (x1, y1) and (x2, y2), they define a line with 
	* A = y1-y2, B = x2-x1, and C = x1 y2 - x2 y1 . So we'll take our sites
	* to find A, B, and C, and then use these in combination with the 
	* mouse click to find the distance between the mouse click and 
	* the line.
	*/
	public boolean contains(int px, int py, int pz) {
		boolean in = false;
		int pixelsPerNm = PIXELS_PER_NM;
		int z1 = (int)(pixelsPerNm*getZ1());
		int z2 = (int)(pixelsPerNm*getZ2());
		int y1 = (int)(pixelsPerNm*getY1());
		int y2 = (int)(pixelsPerNm*getY2());
		if(z1 > z2) {
			int tempx = z2;
			int tempy = y2;
			z2 = z1;
			y2 = y1;
			z1 = tempx;
			y1 = tempy;
		}
		int A = y1 - y2;
		int B = z2 - z1;
		int C = z1 * y2 - z2 * y1;
		// Check to make sure we're in the right x range
		if(z1 < pz && pz < z2) {
		// Calculate distance to line
			double r = Math.abs(A * pz + B * py + C)/Math.sqrt(A*A + B*B);
			if(r < 3) {
				in = true;
			}
		}
		return in;
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
	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof MolecularInternalLinkSpec)) {
			return false;
		}
		MolecularInternalLinkSpec candidate = (MolecularInternalLinkSpec)obj;
		if(fieldSpeciesContextSpec != candidate.getSpeciesContextSpec()) {
			return false;
		}
		if((fieldMolecularComponentPatternOne == candidate.fieldMolecularComponentPatternOne) && 
				(fieldMolecularComponentPatternTwo == candidate.fieldMolecularComponentPatternTwo)) {
			return true;
		}
		return false;
	}
	
	public void draw(Graphics2D g2){
		g2.setColor(Color.black);
		int pixelsPerNm = PIXELS_PER_NM;
		// Map site z to drawPanel x
		int x1 = (int)(pixelsPerNm*getZ1());
		int x2 = (int)(pixelsPerNm*getZ2());
		int y1 = (int)(pixelsPerNm*getY1());
		int y2 = (int)(pixelsPerNm*getY2());
		g2.drawLine(x1, y1, x2, y2);
    }
    
	public void writeLink(StringBuilder sb) {
		if(getSite1() == null) {
			System.out.println("Site 1 is null.");
		}
		if(getSite2() == null) {
			System.out.println("Site 2 is null.");
		}
		sb.append("LINK: Site " + getSite1().getIndex() + " ::: Site " + getSite2().getIndex());
		sb.append("\n");
	}


	
//	// TODO: not working properly, will use direct calls from SpeciesContextSpec
//	public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {
//		issueContext = issueContext.newChildContext(ContextType.MolecularInternalLinkSpec, this);
//		if(fieldMolecularComponentPatternOne == fieldMolecularComponentPatternTwo) {
//			String msg = "A generic issue for MolecularInternalLinkSpec entity.";
//			String tip = "Both sites of the Link are identical.";
//			issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
//		}
//	}
	
}
