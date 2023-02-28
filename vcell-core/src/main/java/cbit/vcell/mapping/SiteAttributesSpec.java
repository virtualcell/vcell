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
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cbit.vcell.model.*;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.document.Identifiable;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.IOHelp;
import org.vcell.util.springsalad.NamedColor;

@SuppressWarnings("serial")
public class SiteAttributesSpec implements Identifiable, Displayable, IssueSource {
	private final SpeciesContextSpec fieldSpeciesContextSpec;
	private MolecularComponentPattern fieldMolecularComponentPattern = null;
	private double fieldRadius = 1.0;
	private double fieldDiffusionRate = 1.0;
	private Structure fieldLocation = null;		// feature or membrane
	private Coordinate fieldCoordinate = new Coordinate(0,0,0);	// double x,y,z; has distanceTo()
	private NamedColor fieldColor = Colors.RED;
	// the ComponentStatePattern must not be Any; can be recovered from the MolecularComponentPattern
	// the BondType must be None, can be recovered from the MolecularComponentPattern
	
	public SiteAttributesSpec(SpeciesContextSpec scs, MolecularComponentPattern mcp, Structure structure) {
		fieldSpeciesContextSpec = scs;
		setMolecularComponentPattern(mcp);
		setLocation(structure);
	}
	public SiteAttributesSpec(SpeciesContextSpec scs, MolecularComponentPattern mcp, double radius, double diffusion, Structure location, Coordinate coordinate, NamedColor color) {
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

	public NamedColor getColor() {
		return fieldColor;
	}
	public void setColor(NamedColor color) {
		this.fieldColor = color;
	}
	public double getX() {
		return getCoordinate().getX();
	}
	public double getY() {
		return getCoordinate().getY();
	}
	public double getZ() {
		return getCoordinate().getZ();
	}

	public int getIndex() {
		MolecularComponent mc = getMolecularComponentPattern().getMolecularComponent();
		int index = mc.getIndex();
		return index;
	}
	
	public void writeType(PrintWriter p) {		// I/O the MolecularType
		MolecularTypePattern mtp = getSpeciesContextSpec().getSpeciesContext().getSpeciesPattern().getMolecularTypePatterns().get(0);
		MolecularType mt = mtp.getMolecularType();
		List<ComponentStateDefinition> csdList = getMolecularComponentPattern().getMolecularComponent().getComponentStateDefinitions();
		p.print("TYPE: Name \"" + mt.getName() + "\"");
		p.print(" Radius " + IOHelp.DF[5].format(getRadius()) + " D " + IOHelp.DF[3].format(getDiffusionRate()) + " Color " + getColor().getName());
		p.print(" STATES ");
		for (ComponentStateDefinition state : csdList) {
			p.print("\"" + state.getName() + "\"" + " ");
		}
		p.println();
	}
	public void writeSite(PrintWriter p) {
		String initialState = getMolecularComponentPattern().getComponentStatePattern().getComponentStateDefinition().getName();

		p.println("SITE " + this.getIndex() + " : " + getLocation().getName() + " : Initial State '" + initialState + "'");
		p.print("          ");
		this.writeType(p);
		p.println("          " + "x " + IOHelp.DF[5].format(getX()) + " y " + IOHelp.DF[5].format(getY()) + " z " + IOHelp.DF[5].format(getZ()) + " ");
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
	