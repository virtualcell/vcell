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
import org.vcell.model.rbm.ComponentStatePattern;
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
public class SiteAttributesSpec implements Serializable, Identifiable, Displayable, IssueSource, Matchable {
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
	public SiteAttributesSpec(SpeciesContextSpec scs, MolecularComponentPattern mcp, double radius, double diffusion, Structure structure, Coordinate coordinate, NamedColor color) {
		this(scs, mcp, structure);
		setRadius(radius);
		setDiffusionRate(diffusion);
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
	
	public void writeType(StringBuilder sb) {		// I/O the Site Type (mMlecularComponent)
		if(getMolecularComponentPattern() == null) {
			throw new RuntimeException("writeType(): MolecularComponentPattern is null");
		}
		MolecularComponent mc = getMolecularComponentPattern().getMolecularComponent();
		List<ComponentStateDefinition> csdList = mc.getComponentStateDefinitions();
		sb.append("TYPE: Name \"" + mc.getName() + "\"");
		sb.append(" Radius " + IOHelp.DF[5].format(getRadius()) + " D " + IOHelp.DF[3].format(getDiffusionRate()) + " Color " + getColor().getName());
		sb.append(" STATES ");
		for (ComponentStateDefinition state : csdList) {
			sb.append("\"" + state.getName() + "\"" + " ");
		}
		sb.append("\n");
	}
	public void writeSite(StringBuilder sb) {
		if(getMolecularComponentPattern() == null) {
			throw new RuntimeException("writeSite(): MolecularComponentPattern is null");
		}
		ComponentStatePattern csp = getMolecularComponentPattern().getComponentStatePattern();
		if(csp == null) {
			sb.append("SITE " + (this.getIndex()-1) + " : " + getLocation().getName() + " : Initial State '" + "ERROR: at least one State is needed"  + "'");
			sb.append("\n");
			return;
		}
		ComponentStateDefinition csd = csp.getComponentStateDefinition();
		if(csd == null) {
			throw new RuntimeException("writeSite(): csd is null");
		}
		String initialState = csd.getName();
		sb.append("SITE " + (this.getIndex()-1) + " : " + getLocation().getName() + " : Initial State '" + initialState + "'");
		sb.append("\n");
		sb.append("          ");
		this.writeType(sb);	// ex: TYPE: Name "Type2" Radius 1.00000 D 1.000 Color LIME STATES "State0" "State1" 
		sb.append("          " + "x " + IOHelp.DF[5].format(getX()) + " y " + IOHelp.DF[5].format(getY()) + 
				" z " + IOHelp.DF[5].format(getZ()) + " ");		// ex: x 4.00000 y 4.00000 z 20.00000
		sb.append("\n");
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SiteAttributesSpec)) {
			return false;
		}
		SiteAttributesSpec candidate = (SiteAttributesSpec) obj;

		if(!fieldSpeciesContextSpec.compareEqual(candidate.getSpeciesContextSpec())) {
			return false;
		}
		if(!fieldMolecularComponentPattern.compareEqual(candidate.getMolecularComponentPattern())) {
			return false;
		}
		if(fieldRadius != candidate.getRadius()) {
			return false;
		}
		if(fieldDiffusionRate != candidate.getDiffusionRate()) {
			return false;
		}
		if(!fieldLocation.compareEqual(candidate.getLocation())) {
			return false;
		}
		if(!fieldCoordinate.compareEqual(candidate.getCoordinate())) {
			return false;
		}
		if(fieldColor != candidate.getColor()) {
			return false;
		}
		return true;
	}

	public double computeReactionRadius() {		// in langevin solver notation is R
		// assumes radius in nanometers
		// Either 1.5x radius or radius+2 nm, whichever is smaller, but not smaller than radius+0.5 nm
		double minRadius = Math.max(0.5+fieldRadius,  1.5*fieldRadius);
		double R = Math.min(minRadius, fieldRadius+2);
		return R;
	}

	@Deprecated
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
	