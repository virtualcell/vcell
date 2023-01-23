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
@Deprecated
public class MolecularTypeSpec implements Matchable, Serializable, IssueSource,
	Identifiable, Displayable {
@Deprecated
	public class InternalLink implements Identifiable {
		private MolecularComponentPattern fieldMolecularComponentPatternOne = null;
		private MolecularComponentPattern fieldMolecularComponentPatternTwo = null;
		private double linkLength = 0;
	
		public InternalLink(MolecularComponentPattern linkOne, MolecularComponentPattern linkTwo) throws IllegalArgumentException {
			SpeciesContext sc = MolecularTypeSpec.this.getSpeciesContextSpec().getSpeciesContext();
			SpeciesPattern sp = sc.getSpeciesPattern();
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
	}
	@Deprecated
	public class SiteAttributes implements Identifiable {
		
		private MolecularComponentPattern fieldMolecularComponentPattern = null;
		private double fieldRadius = 1.0;
		private double fieldDiffusionRate = 1.0;
		private Structure fieldLocation = null;		// feature or membrane
		private Coordinate fieldCoordinate = null;	// double x,y,z; has distanceTo()
		private Color fieldColor = Color.GRAY;
		// the ComponentStatePattern must not be Any; can be recovered from the MolecularComponentPattern
		// the BondType must be None, can be recovered from the MolecularComponentPattern
		
		public SiteAttributes(MolecularComponentPattern mcp, double radius, double diffusion, Structure location, Coordinate coordinate, Color color) {
			fieldMolecularComponentPattern = mcp;
			fieldRadius = radius;
			fieldDiffusionRate = diffusion;
			fieldLocation = location;
			fieldCoordinate = coordinate;
			fieldColor = color;
		}
		
		// TODO: hopefully we won't need setters, we just invoke the constructor 
		// from the MolecularStructuresPanel just in time, when component selection changes
		public MolecularComponentPattern getMolecularComponentPattern() {
			return fieldMolecularComponentPattern;
		}
		public double getRadius() {
			return fieldRadius;
		}
		public double getDiffusionRate() {
			return fieldDiffusionRate;
		}
		public Structure getLocation() {
			return fieldLocation;
		}
		public Coordinate getCoordinate() {
			return fieldCoordinate;
		}
		public Color getColor() {
			return fieldColor;
		}
	}
	
// ========================================================================================================
	
	private SimulationContext fieldSimulationContext = null;
	private SpeciesContextSpec fieldSpeciesContextSpec = null;
	private Set<InternalLink> internalLinkSet = new LinkedHashSet<> ();
	private Map<MolecularComponentPattern, SiteAttributes> siteAttributesMap = new LinkedHashMap<> ();

	
	
	
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}
	public void setSimulationContext(SimulationContext fieldSimulationContext) {
		this.fieldSimulationContext = fieldSimulationContext;
	}

	public SpeciesContextSpec getSpeciesContextSpec() {
		return fieldSpeciesContextSpec;
	}
	public void setSpeciesContextSpec(SpeciesContextSpec scs) {
		fieldSpeciesContextSpec = scs;
	}
	
	
	public Set<InternalLink> getInternalLinkSet() {
		return internalLinkSet;
	}
	public void setInternalLinkSet(Set<InternalLink> internalLinkSet) {
		this.internalLinkSet = internalLinkSet;
	}
	public Map<MolecularComponentPattern, SiteAttributes> getSiteAttributesMap() {
		return siteAttributesMap;
	}
	public void setSiteAttributesMap(Map<MolecularComponentPattern, SiteAttributes> siteAttributesMap) {
		this.siteAttributesMap = siteAttributesMap;
	}


	@Override
	public boolean compareEqual(Matchable obj) {
		return false;
	}

	@Override
	public String getDisplayName() {
		return null;
	}
	@Override
	public String getDisplayType() {
		return null;
	}
}
