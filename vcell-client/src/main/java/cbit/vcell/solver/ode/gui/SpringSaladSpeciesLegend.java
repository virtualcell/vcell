/*
 * Copyright (C) 1999-2026 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package cbit.vcell.solver.ode.gui;

import cbit.vcell.math.LangevinParticleMolecularComponent;
import cbit.vcell.math.LangevinParticleMolecularType;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.ParticleMolecularComponent;
import cbit.vcell.math.ParticleMolecularType;
import cbit.vcell.simdata.SpringSaladTrajectory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The species/site-type legend for a {@link SpringSaladTrajectory} — what the viewer offers as
 * show/hide entries.
 * <p>
 * The solver's viewer file identifies each site only by {@code id}, {@code radius} and {@code color}
 * — it carries no species or site names. So the selectable groups are the distinct
 * (color, radius) pairs actually present in the trajectory, which is exactly the granularity the
 * data can distinguish: both properties come from the site's {@code TYPE} in the solver input, so
 * one pair is one site type.
 * <p>
 * Names are then joined on from the {@link MathDescription} that produced the run, whose
 * {@link LangevinParticleMolecularType}s hold the same radius and color per site. The join is on
 * radius rounded to 5 decimals, because that is the precision VCell writes into the solver input.
 * Where the model is unavailable, or a (color, radius) pair matches site types in more than one
 * molecule, the entry keeps its color-based label — the grouping stays correct either way, only
 * the naming degrades.
 */
public class SpringSaladSpeciesLegend {

	/** Molecule name used for site types the model could not name. */
	public static final String UNNAMED_SPECIES = "Unidentified";

	/** One selectable entry: a distinct site type present in the trajectory. */
	public static class SiteType {
		private final String colorName;
		private final double radius;
		private final Color color;
		private final Set<String> moleculeNames = new LinkedHashSet<>();
		private final Set<String> siteNames = new LinkedHashSet<>();
		private int siteCount;

		SiteType(String colorName, double radius) {
			this.colorName = colorName;
			this.radius = radius;
			this.color = SpringSaladViewerCanvas.colorForName(colorName);
		}

		public String getKey() { return keyOf(colorName, radius); }
		public String getColorName() { return colorName; }
		public double getRadius() { return radius; }
		public Color getColor() { return color; }
		/** How many sites of this type are in the trajectory's first frame. */
		public int getSiteCount() { return siteCount; }

		/** The molecule this site type belongs to, or {@link #UNNAMED_SPECIES} if not resolvable. */
		public String getSpeciesName() {
			return moleculeNames.size() == 1 ? moleculeNames.iterator().next() : UNNAMED_SPECIES;
		}

		/**
		 * Label for a row shown beneath its species header: the model's site name(s), or the color
		 * and radius when the model could not name this type.
		 */
		public String getSiteLabel() {
			if (siteNames.isEmpty()) {
				return prettyColorName(colorName) + String.format(Locale.ROOT, " (r=%.3g)", radius);
			}
			return String.join(" / ", siteNames);
		}

		/** Label for a row shown without a species header, so it carries the molecule name itself. */
		public String getLabel() {
			if (siteNames.isEmpty() || moleculeNames.size() != 1) {
				return getSiteLabel();
			}
			return moleculeNames.iterator().next() + " : " + getSiteLabel();
		}
	}

	private final List<SiteType> siteTypes;

	private SpringSaladSpeciesLegend(List<SiteType> siteTypes) {
		this.siteTypes = Collections.unmodifiableList(siteTypes);
	}

	/** Every selectable site type, in first-appearance order. */
	public List<SiteType> getSiteTypes() { return siteTypes; }

	public boolean isEmpty() { return siteTypes.isEmpty(); }

	/** True when the model supplied names, i.e. the entries are more than colors. */
	public boolean isNamed() {
		return siteTypes.stream().anyMatch(st -> !st.siteNames.isEmpty());
	}

	/** Site types grouped by species (molecule) name, in first-appearance order. */
	public Map<String, List<SiteType>> bySpecies() {
		Map<String, List<SiteType>> out = new LinkedHashMap<>();
		for (SiteType st : siteTypes) {
			out.computeIfAbsent(st.getSpeciesName(), k -> new ArrayList<>()).add(st);
		}
		return out;
	}

	/**
	 * Identity of a site type as the trajectory can express it. Radius is rounded to the 5 decimals
	 * VCell writes into the solver input, so a model radius and the radius echoed back through the
	 * solver produce the same key.
	 */
	public static String keyOf(String colorName, double radius) {
		String c = colorName == null ? "" : colorName.trim().toUpperCase(Locale.ROOT);
		return c + "@" + String.format(Locale.ROOT, "%.5f", radius);
	}

	public static String keyOf(SpringSaladTrajectory.Site site) {
		return keyOf(site.getColor(), site.getRadius());
	}

	/**
	 * Build the legend for a trajectory, naming its site types from {@code math} where possible.
	 *
	 * @param trajectory the trajectory to describe (null yields an empty legend)
	 * @param math       the math description of the run, for names; may be null
	 */
	public static SpringSaladSpeciesLegend build(SpringSaladTrajectory trajectory, MathDescription math) {
		Map<String, SiteType> byKey = new LinkedHashMap<>();
		if (trajectory != null && !trajectory.getFrames().isEmpty()) {
			// The first frame defines the palette; molecules created later reuse existing site
			// types, since a type is a property of the model, not of an individual molecule.
			for (SpringSaladTrajectory.Site s : trajectory.getFrames().get(0).getSites()) {
				SiteType st = byKey.computeIfAbsent(keyOf(s), k -> new SiteType(s.getColor(), s.getRadius()));
				st.siteCount++;
			}
		}
		if (math != null) {
			for (ParticleMolecularType pmt : math.getParticleMolecularTypes()) {
				if (!(pmt instanceof LangevinParticleMolecularType)) {
					continue;
				}
				for (ParticleMolecularComponent pmc : pmt.getComponentList()) {
					if (!(pmc instanceof LangevinParticleMolecularComponent lpmc) || lpmc.getColor() == null) {
						continue;
					}
					SiteType st = byKey.get(keyOf(lpmc.getColor().getName(), lpmc.getRadius()));
					if (st != null) {
						st.moleculeNames.add(pmt.getName());
						st.siteNames.add(lpmc.getName());
					}
				}
			}
		}
		return new SpringSaladSpeciesLegend(new ArrayList<>(byKey.values()));
	}

	/** {@code LIME_GREEN} -> {@code Lime green}. */
	private static String prettyColorName(String colorName) {
		if (colorName == null || colorName.isEmpty()) {
			return "Unknown";
		}
		String s = colorName.trim().toLowerCase(Locale.ROOT).replace('_', ' ');
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}
