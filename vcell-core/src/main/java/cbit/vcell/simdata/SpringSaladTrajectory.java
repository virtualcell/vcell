/*
 * Copyright (C) 1999-2026 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package cbit.vcell.simdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory model of a SpringSaLaD (Langevin) particle trajectory, parsed from the
 * solver's viewer file ({@code SimID_<key>_0__VIEW_Run0.txt}).
 * <p>
 * The viewer file is written by the {@code LangevinNoVis01} solver every {@code dt_image}
 * step (for Run 0 only). It is a tab-delimited text file: a small header followed by a
 * sequence of {@code SCENE} blocks, one per time point, each listing every site's id,
 * radius, color and xyz position plus the links/bonds present at that instant. See
 * {@code docs/salad-3d-renderer-design.md} (&sect;5) for the format specification.
 * <p>
 * This is the spatiotemporal data consumed by the SaLaD 3D renderer / movie player.
 */
public class SpringSaladTrajectory implements Serializable {

	private static final long serialVersionUID = 1L;

	/** A single site (glyph) snapshot within one frame. */
	public static class Site implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int id;
		private final double radius;
		private final String color;
		private final double x;
		private final double y;
		private final double z;

		public Site(int id, double radius, String color, double x, double y, double z) {
			this.id = id;
			this.radius = radius;
			this.color = color;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public int getId() { return id; }
		public double getRadius() { return radius; }
		public String getColor() { return color; }
		public double getX() { return x; }
		public double getY() { return y; }
		public double getZ() { return z; }
	}

	/** One time point: the sites' positions and the links/bonds present at {@link #getTime()}. */
	public static class Frame implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int sceneNumber;
		private final double time;
		private final List<Site> sites;
		/** Each link is an {@code int[]{idA, idB}}; includes both structural links and dynamic bonds. */
		private final List<int[]> links;

		public Frame(int sceneNumber, double time, List<Site> sites, List<int[]> links) {
			this.sceneNumber = sceneNumber;
			this.time = time;
			this.sites = Collections.unmodifiableList(sites);
			this.links = Collections.unmodifiableList(links);
		}
		public int getSceneNumber() { return sceneNumber; }
		public double getTime() { return time; }
		public List<Site> getSites() { return sites; }
		public List<int[]> getLinks() { return links; }
	}

	// header fields
	private final double totalTime;
	private final double dtImage;
	private final double xSize;
	private final double ySize;
	private final double zOutside;
	private final double zInside;
	private final List<Frame> frames;

	public SpringSaladTrajectory(double totalTime, double dtImage, double xSize, double ySize,
								 double zOutside, double zInside, List<Frame> frames) {
		this.totalTime = totalTime;
		this.dtImage = dtImage;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zOutside = zOutside;
		this.zInside = zInside;
		this.frames = Collections.unmodifiableList(frames);
	}

	public double getTotalTime() { return totalTime; }
	public double getDtImage() { return dtImage; }
	/** Bounding-box extents from the header; use these for the scene box (units are the model's). */
	public double getXSize() { return xSize; }
	public double getYSize() { return ySize; }
	public double getZOutside() { return zOutside; }
	public double getZInside() { return zInside; }
	public List<Frame> getFrames() { return frames; }
	public int getFrameCount() { return frames.size(); }

	/**
	 * Parse a SpringSaLaD viewer file into a {@link SpringSaladTrajectory}.
	 *
	 * @param reader a reader over the viewer file contents (caller closes it)
	 * @return the parsed trajectory
	 * @throws IOException on read error or malformed header
	 */
	public static SpringSaladTrajectory parse(Reader reader) throws IOException {
		BufferedReader br = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);

		double totalTime = 0, dtImage = 0, xSize = 0, ySize = 0, zOutside = 0, zInside = 0;
		boolean sawTotalTime = false;

		// ---- header: key\tvalue lines until the terminating blank line ----
		String line;
		while ((line = br.readLine()) != null) {
			if (line.trim().isEmpty()) {
				break; // blank line ends the header
			}
			String[] t = line.split("\t");
			if (t.length < 2) {
				continue;
			}
			String key = t[0].trim();
			double val = parseDouble(t[1]);
			switch (key) {
				case "TotalTime": totalTime = val; sawTotalTime = true; break;
				case "dtimage":   dtImage = val;   break;
				case "xsize":     xSize = val;     break;
				case "ysize":     ySize = val;     break;
				case "z_outside": zOutside = val;  break;
				case "z_inside":  zInside = val;   break;
				default: break; // tolerate unknown header keys
			}
		}
		if (!sawTotalTime) {
			throw new IOException("Not a SpringSaLaD viewer file: missing 'TotalTime' header");
		}

		// ---- frames: repeated SCENE blocks ----
		List<Frame> frames = new ArrayList<>();
		int sceneNumber = -1;
		double time = 0;
		List<Site> sites = new ArrayList<>();
		List<int[]> links = new ArrayList<>();
		boolean inScene = false;

		while ((line = br.readLine()) != null) {
			if (line.trim().isEmpty()) {
				continue; // blank lines separate frames; frame is flushed on next SCENE / EOF
			}
			String[] t = line.split("\t");
			String tag = t[0].trim();
			switch (tag) {
				case "SCENE":
					if (inScene) {
						frames.add(new Frame(sceneNumber, time, sites, links));
					}
					inScene = true;
					sceneNumber = -1;
					time = 0;
					sites = new ArrayList<>();
					links = new ArrayList<>();
					break;
				case "SceneNumber":
					// SceneNumber \t <n> \t CurrentTime \t <t>
					if (t.length >= 2) sceneNumber = (int) parseDouble(t[1]);
					if (t.length >= 4) time = parseDouble(t[3]);
					break;
				case "ID":
					// ID \t <id> \t <radius> \t <color> \t <x> \t <y> \t <z>
					if (t.length >= 7) {
						sites.add(new Site(
								(int) parseDouble(t[1]),
								parseDouble(t[2]),
								t[3].trim(),
								parseDouble(t[4]),
								parseDouble(t[5]),
								parseDouble(t[6])));
					}
					break;
				case "Link":
					// Link \t <idA> \t : \t <idB>
					if (t.length >= 4) {
						links.add(new int[] { (int) parseDouble(t[1]), (int) parseDouble(t[3]) });
					}
					break;
				default:
					break; // tolerate unknown lines
			}
		}
		if (inScene) {
			frames.add(new Frame(sceneNumber, time, sites, links));
		}

		return new SpringSaladTrajectory(totalTime, dtImage, xSize, ySize, zOutside, zInside, frames);
	}

	private static double parseDouble(String s) {
		return Double.parseDouble(s.trim());
	}
}
