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
public class SiteAttributesSpec implements Identifiable {
		
	private MolecularComponentPattern fieldMolecularComponentPattern = null;
	private double fieldRadius = 1.0;
	private double fieldDiffusionRate = 1.0;
	private Structure fieldLocation = null;		// feature or membrane
	private Coordinate fieldCoordinate = null;	// double x,y,z; has distanceTo()
	private Color fieldColor = Color.GRAY;
	// the ComponentStatePattern must not be Any; can be recovered from the MolecularComponentPattern
	// the BondType must be None, can be recovered from the MolecularComponentPattern
	
	public SiteAttributesSpec(MolecularComponentPattern mcp, double radius, double diffusion, Structure location, Coordinate coordinate, Color color) {
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
	