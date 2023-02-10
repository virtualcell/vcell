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
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.Identifiable;

@SuppressWarnings("serial")
public class SiteAttributesSpec implements Identifiable, Displayable, IssueSource {
	private SpeciesContextSpec fieldSpeciesContextSpec = null;
	private MolecularComponentPattern fieldMolecularComponentPattern = null;
	private double fieldRadius = 1.0;
	private double fieldDiffusionRate = 1.0;
	private Structure fieldLocation = null;		// feature or membrane
	private Coordinate fieldCoordinate = new Coordinate(0,0,0);	// double x,y,z; has distanceTo()
	private Color fieldColor = Color.GRAY;
	// the ComponentStatePattern must not be Any; can be recovered from the MolecularComponentPattern
	// the BondType must be None, can be recovered from the MolecularComponentPattern
	
	public SiteAttributesSpec(SpeciesContextSpec scs, MolecularComponentPattern mcp, Structure structure) {
		fieldSpeciesContextSpec = scs;
		setMolecularComponentPattern(mcp);
		setLocation(structure);
	}
	public SiteAttributesSpec(SpeciesContextSpec scs, MolecularComponentPattern mcp, double radius, double diffusion, Structure location, Coordinate coordinate, Color color) {
		fieldSpeciesContextSpec = scs;
		setMolecularComponentPattern(mcp);
		setRadius(radius);
		setDiffusionRate(diffusion);
		setLocation(location);
		setCoordinate(coordinate);
		setColor(color);
	}

	public SpeciesContextSpec getSpeciesContextSpec() {
		return fieldSpeciesContextSpec;
	}
	public MolecularComponentPattern getMolecularComponentPattern() {
		return fieldMolecularComponentPattern;
	}
	public void setMolecularComponentPattern(MolecularComponentPattern molecularComponentPattern) {
		this.fieldMolecularComponentPattern = molecularComponentPattern;
	}

	public double getRadius() {
		return fieldRadius;
	}
	public void setRadius(double radius) {
		this.fieldRadius = radius;
	}

	public double getDiffusionRate() {
		return fieldDiffusionRate;
	}
	public void setDiffusionRate(double diffusionRate) {
		this.fieldDiffusionRate = diffusionRate;
	}

	public Structure getLocation() {
		return fieldLocation;
	}
	public void setLocation(Structure location) {
		this.fieldLocation = location;
	}

	public Coordinate getCoordinate() {
		return fieldCoordinate;
	}
	public void setCoordinate(Coordinate coordinate) {
		this.fieldCoordinate = coordinate;
	}

	public Color getColor() {
		return fieldColor;
	}
	public void setColor(Color color) {
		this.fieldColor = color;
	}
	public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {
		issueContext = issueContext.newChildContext(ContextType.SiteAttributesSpec, this);

		if(fieldLocation instanceof Membrane) {
			String tip = "A generic issue for SiteAttributesSpec entity.";
			String msg = "Location is a Membrane.";
			issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.WARNING));
		}
	}

	public static final String typeName = "SiteAttributesSpec";
	@Override
	public String getDisplayName() {
		if(fieldMolecularComponentPattern != null) {
			return fieldMolecularComponentPattern.getMolecularComponent().getDisplayName();
		}
		return("?");
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}

}
	