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
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SpringSaladTrajectory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
 * There are two ways to get there, and which one applies depends on the run.
 * <p>
 * <b>Named.</b> The solver writes a {@code SiteIDs.csv} naming every site it created, and the data
 * service attaches it to the trajectory (see
 * {@link SpringSaladTrajectory#parseSiteIdentities}). Then each entry is a real (molecule, site
 * type) pair — exact, and it distinguishes molecules whose sites happen to look alike.
 * <p>
 * <b>Unnamed.</b> Older runs, and runs whose solver folder has been pruned, arrive without it. The
 * viewer file itself identifies a site only by {@code id}, {@code radius} and {@code color}, so
 * entries fall back to the distinct (color, radius) pairs present — the finest split the file
 * supports, since both properties come from the site's {@code TYPE} in the solver input. Labels are
 * then joined from the {@link MathDescription}, whose {@link LangevinParticleMolecularType}s carry
 * the same radius and color, matching radius at the 5 decimals VCell writes into the solver input.
 * This degrades in one visible way: two different molecules whose sites share a color and radius
 * cannot be told apart and collapse into a single entry.
 */
public class SpringSaladSpeciesLegend {

	private static final Logger lg = LogManager.getLogger(SpringSaladSpeciesLegend.class);

	/** Molecule name used for site types the model could not name. */
	public static final String UNNAMED_SPECIES = "Unidentified";

	/** One selectable entry: a distinct site type present in the trajectory. */
	public static class SiteType {
		private final String key;
		private final String colorName;
		private final double radius;
		private final Color color;
		private final Set<String> moleculeNames = new LinkedHashSet<>();
		private final Set<String> siteNames = new LinkedHashSet<>();
		private int siteCount;

		SiteType(String key, String colorName, double radius) {
			this.key = key;
			this.colorName = colorName;
			this.radius = radius;
			this.color = SpringSaladViewerCanvas.colorForName(colorName);
		}

		/** Matches {@link SpringSaladTrajectory#siteTypeKey}, which is what the canvas hides by. */
		public String getKey() { return key; }
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
	 * Build the legend for a trajectory.
	 * <p>
	 * When the run supplied {@code SiteIDs.csv} the solver has already told us each site's molecule
	 * and site type, and that is used directly. Otherwise site types are inferred from
	 * (color, radius) and named by joining against {@code math} — see the class comment.
	 *
	 * @param trajectory the trajectory to describe (null yields an empty legend)
	 * @param math       the math description of the run, for names; may be null
	 */
	public static SpringSaladSpeciesLegend build(SpringSaladTrajectory trajectory, MathDescription math) {
		Map<String, SiteType> byKey = new LinkedHashMap<>();
		if (trajectory != null) {
			// Walk every frame, not just the first: molecules created part-way through the run by
			// creation reactions introduce site types that are absent at t=0.
			Set<Integer> counted = new HashSet<>();
			for (SpringSaladTrajectory.Frame frame : trajectory.getFrames()) {
				for (SpringSaladTrajectory.Site s : frame.getSites()) {
					SiteType st = byKey.computeIfAbsent(trajectory.siteTypeKey(s),
							k -> new SiteType(k, s.getColor(), s.getRadius()));
					if (counted.add(s.getId())) {
						st.siteCount++;
					}
					SpringSaladTrajectory.SiteIdentity identity = trajectory.getSiteIdentity(s.getId());
					if (identity != null) {
						st.moleculeNames.add(identity.getMoleculeName());
						st.siteNames.add(identity.getSiteTypeName());
					}
				}
			}
		}
		if (math != null && (trajectory == null || !trajectory.hasSiteIdentities())) {
			joinNamesFromModel(byKey, math);
		}
		return new SpringSaladSpeciesLegend(new ArrayList<>(byKey.values()));
	}

	/**
	 * Fallback naming for runs with no {@code SiteIDs.csv}: match each model site type to the
	 * trajectory entry with the same color and radius. The radius is compared at the 5 decimals
	 * VCell writes into the solver input, so a model radius and the radius echoed back through the
	 * solver agree.
	 */
	private static void joinNamesFromModel(Map<String, SiteType> byKey, MathDescription math) {
		for (ParticleMolecularType pmt : math.getParticleMolecularTypes()) {
			if (!(pmt instanceof LangevinParticleMolecularType)) {
				continue;
			}
			for (ParticleMolecularComponent pmc : pmt.getComponentList()) {
				if (!(pmc instanceof LangevinParticleMolecularComponent lpmc) || lpmc.getColor() == null) {
					continue;
				}
				String color = lpmc.getColor().getName() == null
						? "" : lpmc.getColor().getName().trim().toUpperCase(Locale.ROOT);
				// The radius is an Expression here and a double on the trajectory side, so it has
				// to be evaluated before it can be formatted -- passing the Expression to "%.5f"
				// threw IllegalFormatConversionException and took the whole results viewer down.
				Double radius = constantRadius(lpmc);
				if (radius == null) {
					continue;   // a non-constant radius cannot match a key built from a number
				}
				SiteType st = byKey.get(String.format(Locale.ROOT, "color:%s@%.5f", color, radius));
				if (st != null) {
					st.moleculeNames.add(pmt.getName());
					st.siteNames.add(lpmc.getName());
				}
			}
		}
	}

	/**
	 * The site radius as a number, or null if it is missing or not a constant.
	 *
	 * Naming is a convenience: a radius that cannot be evaluated means this site simply goes
	 * unnamed in the legend, which is what happens today for any run without {@code SiteIDs.csv}.
	 * It must never abort building the legend, because the legend failing takes the results
	 * viewer with it.
	 */
	private static Double constantRadius(LangevinParticleMolecularComponent lpmc) {
		Expression radius = lpmc.getRadius();
		if (radius == null) {
			return null;
		}
		try {
			return radius.evaluateConstant();
		} catch (ExpressionException e) {
			// infix(), not toString(), is the readable form of an Expression
			lg.warn("site '" + lpmc.getName() + "' has a non-constant radius '" + radius.infix()
					+ "'; it will not be named in the SpringSaLaD legend", e);
			return null;
		}
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
