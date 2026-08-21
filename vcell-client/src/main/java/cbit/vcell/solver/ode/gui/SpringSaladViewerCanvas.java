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

import cbit.vcell.render.Affine;
import cbit.vcell.render.Camera;
import cbit.vcell.render.Trackball;
import cbit.vcell.render.Vect3d;
import cbit.vcell.simdata.SpringSaladTrajectory;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Java2D "impostor-sphere" renderer for one frame of a {@link SpringSaladTrajectory}.
 * <p>
 * Each molecule site is drawn as a pre-shaded sphere sprite scaled to its projected radius and
 * tinted by its color and depth (painter's algorithm, back-to-front). Rotation uses the existing
 * quaternion {@link Trackball}/{@link Camera}; the mouse drives rotate (drag), zoom (wheel) and
 * pan (shift- or right-drag). No native dependencies — see {@code docs/salad-3d-renderer-design.md}
 * Phase 1.
 */
@SuppressWarnings("serial")
public class SpringSaladViewerCanvas extends JPanel {

	private static final int SPRITE_SIZE = 96;      // base sprite resolution
	private static final int DEPTH_BUCKETS = 24;    // depth-brightness quantization for sprite cache
	private static final double MIN_BRIGHT = 0.45;  // farthest-glyph brightness
	private static final double SCREEN_FILL = 0.45; // fraction of min(w,h) the scene half-extent maps to

	// Default camera: an oblique 3/4 view looking DOWN onto the membrane (the z=0 plane) from above
	// and off to one side, rather than straight down the z axis — which would show the membrane
	// face-on, flattening the box and hiding all depth under the parallel projection.
	private static final double DEFAULT_ELEVATION_DEG = 35; // above the membrane plane
	private static final double DEFAULT_AZIMUTH_DEG = 30;   // turntable swing off a face-on view

	private SpringSaladTrajectory trajectory;
	private int frameIndex = 0;

	/**
	 * Right-handed, because this canvas draws with +z toward the viewer: {@link #project} keeps the
	 * rotated z as depth and the draw list is sorted far-first on it, so larger depth is nearer.
	 * The geometry and PDE surface viewers are left-handed, which is the default; asking for it here
	 * grabs the far side of the ball and turns the scene the wrong way on both axes.
	 */
	private final Trackball trackball = new Trackball(new Camera(), Trackball.Handedness.RIGHT_HANDED);
	private double zoom = 1.0;
	private double panX = 0, panY = 0;
	private boolean showLinks = true;
	private boolean showBox = true;
	private boolean showMembrane = true;
	/** Site types (see {@link SpringSaladTrajectory#siteTypeKey}) the user has switched off. */
	private final Set<String> hiddenSiteTypes = new HashSet<>();

	private static final Color MEMBRANE_GREEN = new Color(45, 175, 70);
	private static final int MEMBRANE_GRID = 12;      // NxN quad subdivision — only to depth-sort vs glyphs
	// light direction (upper-left, toward viewer), shared with the sphere sprite, for membrane lighting
	private static final double LIGHT_X = -0.5014, LIGHT_Y = -0.6017, LIGHT_Z = 0.6217;
	private static final Color BOX_COLOR = new Color(150, 150, 175);
	private static final BasicStroke BOX_STROKE = new BasicStroke(1f);
	private static final int BOX_EDGE_SEGMENTS = 20;   // dice edges so they depth-sort (hidden-line) vs the scene

	// world framing (computed from all frames so the box doesn't jitter during playback)
	private boolean boundsValid = false;
	private double cx, cy, cz;          // scene center
	/**
	 * Radius of the scene's bounding sphere. Framing on the sphere rather than the largest axis
	 * extent keeps the whole box on screen at ANY rotation — under the oblique default view a box
	 * framed by its longest axis has its diagonal run off the canvas.
	 */
	private double viewRadius = 1.0;

	// base white shaded sphere + tinted/depth-shaded sprite cache
	private static final BufferedImage baseSprite = makeBaseSprite();
	private final Map<Long, BufferedImage> spriteCache = new HashMap<>();

	// mouse drag state
	private int lastX, lastY;
	private boolean panning;

	public SpringSaladViewerCanvas() {
		setBackground(Color.black);
		MouseAdapter ma = new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				lastX = e.getX(); lastY = e.getY();
				panning = e.isShiftDown() || javax.swing.SwingUtilities.isRightMouseButton(e);
			}
			@Override public void mouseDragged(MouseEvent e) {
				int w = Math.max(1, getWidth()), h = Math.max(1, getHeight());
				if (panning) {
					panX += (e.getX() - lastX);
					panY += (e.getY() - lastY);
				} else {
					// normalized [-1,1] trackball coords (y flipped so drag feels natural)
					double p1x = 2.0 * lastX / w - 1.0, p1y = 1.0 - 2.0 * lastY / h;
					double p2x = 2.0 * e.getX() / w - 1.0, p2y = 1.0 - 2.0 * e.getY() / h;
					trackball.rotate_xy(p1x, p1y, p2x, p2y);
				}
				lastX = e.getX(); lastY = e.getY();
				repaint();
			}
			@Override public void mouseWheelMoved(MouseWheelEvent e) {
				zoom *= Math.pow(1.1, -e.getPreciseWheelRotation());
				zoom = Math.max(0.1, Math.min(zoom, 50.0));
				repaint();
			}
		};
		addMouseListener(ma);
		addMouseMotionListener(ma);
		addMouseWheelListener(ma);
		resetView();
	}

	public void setTrajectory(SpringSaladTrajectory t) {
		this.trajectory = t;
		this.frameIndex = 0;
		this.boundsValid = false;
		resetView();
	}

	public SpringSaladTrajectory getTrajectory() { return trajectory; }

	public int getFrameCount() { return trajectory == null ? 0 : trajectory.getFrameCount(); }

	public void setFrameIndex(int i) {
		int n = getFrameCount();
		if (n == 0) { frameIndex = 0; return; }
		frameIndex = Math.max(0, Math.min(i, n - 1));
		repaint();
	}

	public int getFrameIndex() { return frameIndex; }

	public void setShowLinks(boolean b) { showLinks = b; repaint(); }
	public void setShowBox(boolean b) { showBox = b; repaint(); }
	public void setShowMembrane(boolean b) { showMembrane = b; repaint(); }

	/**
	 * Show or hide every site of one site type. Hidden sites still count toward the scene bounds,
	 * so the framing does not jump as types are switched on and off.
	 */
	public void setSiteTypeVisible(String siteTypeKey, boolean visible) {
		if (visible ? hiddenSiteTypes.remove(siteTypeKey) : hiddenSiteTypes.add(siteTypeKey)) {
			repaint();
		}
	}

	public boolean isSiteTypeVisible(String siteTypeKey) { return !hiddenSiteTypes.contains(siteTypeKey); }

	public void showAllSiteTypes() {
		if (!hiddenSiteTypes.isEmpty()) {
			hiddenSiteTypes.clear();
			repaint();
		}
	}

	/**
	 * Restore the default oblique view. Note this resets the trackball rotation as well as
	 * zoom/pan — {@code camera.resetView()} alone does not, because the projection is driven by the
	 * trackball's quaternion, not the camera.
	 * <p>
	 * {@link Trackball#setRotation} composes its Euler angles as {@code Rz*Ry*Rx}, so the X angle
	 * is the pitch (applied first) and the <em>Y</em> angle acts as the turntable azimuth. The Z
	 * angle would be a roll about the view axis — leave it at zero so the horizon stays level.
	 */
	public void resetView() {
		trackball.getCamera().resetView();
		trackball.setRotation(Math.toRadians(DEFAULT_ELEVATION_DEG - 90), Math.toRadians(DEFAULT_AZIMUTH_DEG), 0);
		zoom = 1.0; panX = 0; panY = 0;
		repaint();
	}

	/** Apply a trackball rotation from normalized point (p1x,p1y) to (p2x,p2y), each in [-1,1]. */
	public void rotate(double p1x, double p1y, double p2x, double p2y) {
		trackball.rotate_xy(p1x, p1y, p2x, p2y);
		repaint();
	}

	/**
	 * Render the current frame to an offscreen image at the given size (no window/peer required;
	 * works headless).
	 */
	public BufferedImage renderToImage(int w, int h) {
		return renderFrameToImage(frameIndex, w, h);
	}

	/**
	 * Render any frame offscreen at the given size, in the current view, without disturbing the
	 * component — it neither resizes it nor changes the displayed frame. That makes it safe to call
	 * off the EDT (as the movie export does) as long as the user is not simultaneously changing the
	 * view.
	 */
	public BufferedImage renderFrameToImage(int frame, int w, int h) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);
		paintScene(g, w, h, frame);
		g.dispose();
		return img;
	}

	private void computeBounds() {
		double minX = Double.POSITIVE_INFINITY, minY = minX, minZ = minX;
		double maxX = Double.NEGATIVE_INFINITY, maxY = maxX, maxZ = maxX;
		boolean any = false;
		if (trajectory != null) {
			for (SpringSaladTrajectory.Frame f : trajectory.getFrames()) {
				for (SpringSaladTrajectory.Site s : f.getSites()) {
					any = true;
					minX = Math.min(minX, s.getX()); maxX = Math.max(maxX, s.getX());
					minY = Math.min(minY, s.getY()); maxY = Math.max(maxY, s.getY());
					minZ = Math.min(minZ, s.getZ()); maxZ = Math.max(maxZ, s.getZ());
				}
			}
		}
		if (trajectory != null) {
			// include the simulation box so the whole box (and membrane) is framed, not just the glyphs
			double xs = trajectory.getXSize(), ys = trajectory.getYSize();
			double zo = trajectory.getZOutside(), zi = trajectory.getZInside();
			if (xs > 0 && ys > 0) {
				any = true;
				minX = Math.min(minX, -xs); maxX = Math.max(maxX, xs);
				minY = Math.min(minY, -ys); maxY = Math.max(maxY, ys);
				minZ = Math.min(minZ, -zo); maxZ = Math.max(maxZ, zi);
			}
		}
		if (!any) { cx = cy = cz = 0; viewRadius = 1; }
		else {
			cx = (minX + maxX) / 2; cy = (minY + maxY) / 2; cz = (minZ + maxZ) / 2;
			double dx = maxX - minX, dy = maxY - minY, dz = maxZ - minZ;
			viewRadius = Math.max(Math.sqrt(dx * dx + dy * dy + dz * dz) / 2, 1e-9);
		}
		boundsValid = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintScene((Graphics2D) g, getWidth(), getHeight(), frameIndex);
	}

	/**
	 * Draw one frame of the scene into {@code g2} for a viewport of {@code w} x {@code h}. Takes the
	 * size and frame explicitly rather than reading the component, so the same code paints the
	 * component and renders offscreen frames for export.
	 */
	private void paintScene(Graphics2D g2, int w, int h, int frameToPaint) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		if (trajectory == null || trajectory.getFrameCount() == 0) {
			g2.setColor(Color.GRAY);
			g2.drawString("No trajectory data.", 12, 20);
			return;
		}
		if (!boundsValid) computeBounds();

		SpringSaladTrajectory.Frame frame =
				trajectory.getFrames().get(Math.max(0, Math.min(frameToPaint, trajectory.getFrameCount() - 1)));
		Affine rot = new Affine();
		trackball.getMatrixGL(rot);
		double pixelScale = SCREEN_FILL * Math.min(w, h) / viewRadius * zoom;
		double ox = w / 2.0 + panX, oy = h / 2.0 + panY;

		// project all sites for this frame
		List<Glyph> glyphs = new ArrayList<>(frame.getSites().size());
		double minD = Double.POSITIVE_INFINITY, maxD = Double.NEGATIVE_INFINITY;
		Map<Integer, Glyph> byId = new HashMap<>();
		for (SpringSaladTrajectory.Site s : frame.getSites()) {
			// Hidden sites are dropped before projection; links to them then find no glyph in byId
			// and are skipped too, so a bond never dangles into empty space.
			if (hiddenSiteTypes.contains(trajectory.siteTypeKey(s))) continue;
			double[] p = project(rot, s.getX(), s.getY(), s.getZ(), pixelScale, ox, oy);
			Glyph gl = new Glyph();
			gl.id = s.getId();
			gl.sx = p[0]; gl.sy = p[1]; gl.depth = p[2];
			gl.screenR = Math.max(1.0, s.getRadius() * pixelScale);
				gl.wx = s.getX(); gl.wy = s.getY(); gl.wz = s.getZ(); gl.radius = s.getRadius();
			gl.color = colorForName(s.getColor());
			glyphs.add(gl);
			byId.put(gl.id, gl);
			minD = Math.min(minD, gl.depth); maxD = Math.max(maxD, gl.depth);
		}
		if (glyphs.isEmpty()) { minD = 0; maxD = 1; } // every site type hidden: keep the box shading sane
		double span = (maxD - minD) < 1e-12 ? 1 : (maxD - minD);

		// unified depth-sorted draw list (painter's algorithm): membrane quads + links + glyph sprites,
		// so the membrane composites correctly with glyphs on either side of it.
		List<Drawable> drawables = new ArrayList<>();
		if (showMembrane) addMembrane(rot, pixelScale, ox, oy, drawables);
		if (showBox) addBox(rot, pixelScale, ox, oy, minD, span, drawables);
		if (showLinks) {
			for (int[] link : frame.getLinks()) {
				Glyph a = byId.get(link[0]), b = byId.get(link[1]);
				if (a == null || b == null) continue;
				// Truncate the bond in WORLD space by each ball's radius, then project — so the
				// segment stops where the edge actually exits the sphere surface, not center-to-
				// center. For a bond angled toward the camera this endpoint projects *inside* the
				// billboard's silhouette circle (it is foreshortened), which is correct: that is
				// where the edge exits the sphere. (Screen-space truncation by screenR would instead
				// stretch every bond out to the silhouette edge, which is wrong for angled bonds.)
				double dx = b.wx - a.wx, dy = b.wy - a.wy, dz = b.wz - a.wz;
				double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
				if (dist <= a.radius + b.radius) continue; // spheres touch/overlap: no visible bond
				double ux = dx / dist, uy = dy / dist, uz = dz / dist;
				double[] pa = project(rot, a.wx + ux * a.radius, a.wy + uy * a.radius, a.wz + uz * a.radius, pixelScale, ox, oy);
				double[] pb = project(rot, b.wx - ux * b.radius, b.wy - uy * b.radius, b.wz - uz * b.radius, pixelScale, ox, oy);
				Line2D ln = new Line2D.Double(pa[0], pa[1], pb[0], pb[1]);
				drawables.add(new Drawable((pa[2] + pb[2]) / 2, gg -> {
					gg.setColor(new Color(150, 150, 150));
					gg.setStroke(new BasicStroke(1f));
					gg.draw(ln);
				}));
			}
		}
		for (Glyph gl : glyphs) {
			double bright = MIN_BRIGHT + (1 - MIN_BRIGHT) * ((gl.depth - minD) / span); // nearer = brighter
			BufferedImage sprite = getSprite(gl.color, bright);
			int d = (int) Math.round(gl.screenR * 2);
			int px = (int) Math.round(gl.sx - gl.screenR), py = (int) Math.round(gl.sy - gl.screenR);
			drawables.add(new Drawable(gl.depth, gg -> gg.drawImage(sprite, px, py, d, d, null)));
		}

		drawables.sort((p, q) -> Double.compare(p.depth, q.depth)); // far first
		for (Drawable d : drawables) d.paint.accept(g2);
	}

	/** Project a world point to {screenX, screenY, cameraDepth} using the current rotation/zoom/pan. */
	private double[] project(Affine rot, double wx, double wy, double wz, double pixelScale, double ox, double oy) {
		Vect3d v = rot.mult(new Vect3d(wx - cx, wy - cy, wz - cz));
		return new double[] { ox + v.getX() * pixelScale, oy - v.getY() * pixelScale, v.getZ() };
	}

	/**
	 * Membrane = the z=0 plane across the box's x,y extent, as an OPAQUE green surface. It is diced into
	 * a grid only so each cell can be depth-sorted against the glyphs (so molecules composite correctly
	 * on either side of it). The fill is a single uniform shade computed from the plane's rotated normal
	 * vs the light — flat planes are uniformly lit — which avoids per-quad depth banding and, because all
	 * cells share one color, avoids anti-alias seams between them.
	 */
	private void addMembrane(Affine rot, double pixelScale, double ox, double oy, List<Drawable> out) {
		double xs = trajectory.getXSize(), ys = trajectory.getYSize();
		if (xs <= 0 || ys <= 0) return;
		Vect3d n = rot.mult(new Vect3d(0, 0, 1));
		double len = Math.sqrt(n.getX() * n.getX() + n.getY() * n.getY() + n.getZ() * n.getZ());
		double dot = len > 0 ? (n.getX() * LIGHT_X + n.getY() * LIGHT_Y + n.getZ() * LIGHT_Z) / len : 0;
		double shade = 0.35 + 0.65 * Math.abs(dot);   // face-on = bright, edge-on = dark
		final Color fill = new Color(
				clamp255(MEMBRANE_GREEN.getRed() * shade),
				clamp255(MEMBRANE_GREEN.getGreen() * shade),
				clamp255(MEMBRANE_GREEN.getBlue() * shade));   // opaque
		int G = MEMBRANE_GRID;
		for (int i = 0; i < G; i++) {
			double x0 = -xs + 2 * xs * i / G, x1 = -xs + 2 * xs * (i + 1) / G;
			for (int j = 0; j < G; j++) {
				double y0 = -ys + 2 * ys * j / G, y1 = -ys + 2 * ys * (j + 1) / G;
				double[] a = project(rot, x0, y0, 0, pixelScale, ox, oy);
				double[] b = project(rot, x1, y0, 0, pixelScale, ox, oy);
				double[] c = project(rot, x1, y1, 0, pixelScale, ox, oy);
				double[] d = project(rot, x0, y1, 0, pixelScale, ox, oy);
				double depth = (a[2] + b[2] + c[2] + d[2]) / 4;
				// grow each cell ~1px outward from its centroid so adjacent (same-color) cells overlap,
				// hiding the anti-alias seams that would otherwise show the grid.
				double gx = (a[0] + b[0] + c[0] + d[0]) / 4, gy = (a[1] + b[1] + c[1] + d[1]) / 4;
				Path2D.Double quad = new Path2D.Double();
				double[][] pts = { a, b, c, d };
				for (int p = 0; p < 4; p++) {
					double dx = pts[p][0] - gx, dy = pts[p][1] - gy, dl = Math.sqrt(dx * dx + dy * dy);
					double ex = pts[p][0], ey = pts[p][1];
					if (dl > 1e-6) { ex += dx / dl * 1.0; ey += dy / dl * 1.0; }
					if (p == 0) quad.moveTo(ex, ey); else quad.lineTo(ex, ey);
				}
				quad.closePath();
				out.add(new Drawable(depth, gg -> { gg.setColor(fill); gg.fill(quad); }));
			}
		}
	}

	/**
	 * Simulation box: the 12 edges of [-xSize,xSize] x [-ySize,ySize] x [-zOutside,zInside], diced into
	 * short segments and added to the depth-sorted draw list so painter's algorithm hides the parts
	 * behind the (opaque) membrane and the glyphs — essential under parallel projection, which has no
	 * perspective depth cue. Segments are also depth-dimmed (nearer = brighter) for extra perception.
	 */
	private void addBox(Affine rot, double pixelScale, double ox, double oy, double minD, double span, List<Drawable> out) {
		double xs = trajectory.getXSize(), ys = trajectory.getYSize();
		double zo = trajectory.getZOutside(), zi = trajectory.getZInside();
		if (xs <= 0 || ys <= 0) return;
		double[] X = { -xs, xs }, Y = { -ys, ys }, Z = { -zo, zi };
		double[][] cw = new double[8][];   // corners in world coords
		int k = 0;
		for (int a = 0; a < 2; a++)
			for (int b = 0; b < 2; b++)
				for (int cc = 0; cc < 2; cc++)
					cw[k++] = new double[] { X[a], Y[b], Z[cc] };
		int[][] edges = {
			{ 0, 1 }, { 2, 3 }, { 4, 5 }, { 6, 7 },   // along z
			{ 0, 2 }, { 1, 3 }, { 4, 6 }, { 5, 7 },   // along y
			{ 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }    // along x
		};
		for (int[] e : edges) {
			double[] p0 = cw[e[0]], p1 = cw[e[1]];
			double[] prev = project(rot, p0[0], p0[1], p0[2], pixelScale, ox, oy);
			for (int s = 1; s <= BOX_EDGE_SEGMENTS; s++) {
				double t = (double) s / BOX_EDGE_SEGMENTS;
				double[] cur = project(rot, p0[0] + (p1[0] - p0[0]) * t, p0[1] + (p1[1] - p0[1]) * t,
						p0[2] + (p1[2] - p0[2]) * t, pixelScale, ox, oy);
				double depth = (prev[2] + cur[2]) / 2;
				double bright = MIN_BRIGHT + (1 - MIN_BRIGHT) * clamp01((depth - minD) / span);
				Color col = new Color(clamp255(BOX_COLOR.getRed() * bright),
						clamp255(BOX_COLOR.getGreen() * bright), clamp255(BOX_COLOR.getBlue() * bright));
				double x0 = prev[0], y0 = prev[1], x1 = cur[0], y1 = cur[1];
				out.add(new Drawable(depth, gg -> { gg.setColor(col); gg.setStroke(BOX_STROKE); gg.draw(new Line2D.Double(x0, y0, x1, y1)); }));
				prev = cur;
			}
		}
	}

	private static double clamp01(double v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }

	private static final class Glyph {
		int id; double sx, sy, depth, screenR; Color color;
		double wx, wy, wz, radius;   // world-space center + radius (for world-space bond truncation)
	}

	/** A depth-sorted paint action (glyph sprite, membrane quad, or link). */
	private static final class Drawable {
		final double depth;
		final Consumer<Graphics2D> paint;
		Drawable(double depth, Consumer<Graphics2D> paint) { this.depth = depth; this.paint = paint; }
	}

	// ---- impostor sprite generation / cache ----

	/**
	 * A small shaded ball in the given color — the same impostor sprite the scene draws, so a
	 * legend entry looks like the molecules it selects.
	 */
	public static Icon ballIcon(Color color, int size) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(tint(color, 1.0), 0, 0, size, size, null);
		g.dispose();
		return new ImageIcon(img);
	}

	private BufferedImage getSprite(Color color, double bright) {
		int bucket = (int) Math.round(bright * (DEPTH_BUCKETS - 1));
		long key = (((long) (color.getRGB() & 0xFFFFFF)) << 8) | bucket;
		BufferedImage cached = spriteCache.get(key);
		if (cached != null) return cached;

		double b = MIN_BRIGHT + (1 - MIN_BRIGHT) * ((double) bucket / (DEPTH_BUCKETS - 1));
		BufferedImage out = tint(color, b);
		if (spriteCache.size() < 4096) spriteCache.put(key, out);
		return out;
	}

	/** Colorize the white base sphere, scaling it to overall brightness {@code b}. */
	private static BufferedImage tint(Color color, double b) {
		double cr = color.getRed() / 255.0 * b, cg = color.getGreen() / 255.0 * b, cb = color.getBlue() / 255.0 * b;
		BufferedImage out = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < SPRITE_SIZE; y++) {
			for (int x = 0; x < SPRITE_SIZE; x++) {
				int base = baseSprite.getRGB(x, y);
				int a = (base >>> 24) & 0xFF;
				if (a == 0) { out.setRGB(x, y, 0); continue; }
				double lum = (base & 0xFF) / 255.0; // base sprite is grayscale; luminance in blue channel
				int r = clamp255(cr * lum * 255), gg = clamp255(cg * lum * 255), bb = clamp255(cb * lum * 255);
				out.setRGB(x, y, (a << 24) | (r << 16) | (gg << 8) | bb);
			}
		}
		return out;
	}

	/** Base sphere: white, Lambert-ish shading with an offset highlight, transparent outside the disc. */
	private static BufferedImage makeBaseSprite() {
		int n = SPRITE_SIZE;
		BufferedImage img = new BufferedImage(n, n, BufferedImage.TYPE_INT_ARGB);
		double r = n / 2.0 - 1;
		double lx = -0.5, ly = -0.6, lz = 0.62; // light direction (upper-left, toward viewer)
		double ll = Math.sqrt(lx * lx + ly * ly + lz * lz); lx /= ll; ly /= ll; lz /= ll;
		for (int y = 0; y < n; y++) {
			for (int x = 0; x < n; x++) {
				double dx = (x - n / 2.0 + 0.5) / r, dy = (y - n / 2.0 + 0.5) / r;
				double d2 = dx * dx + dy * dy;
				if (d2 > 1.0) { img.setRGB(x, y, 0); continue; }
				double nz = Math.sqrt(1.0 - d2);         // sphere normal z (surface toward viewer)
				double diff = Math.max(0, -dx * lx + -dy * ly + nz * lz);
				double shade = 0.15 + 0.85 * diff;        // ambient + diffuse
				double spec = Math.pow(diff, 24) * 0.6;   // small highlight
				int v = clamp255((shade + spec) * 255);
				// anti-aliased edge alpha
				double edge = (1.0 - Math.sqrt(d2)) * r;  // pixels inside the rim
				int a = (int) Math.round(255 * Math.max(0, Math.min(1, edge)));
				img.setRGB(x, y, (a << 24) | (v << 16) | (v << 8) | v);
			}
		}
		return img;
	}

	private static int clamp255(double v) { return (int) Math.max(0, Math.min(255, Math.round(v))); }

	// ---- SpringSaLaD color-name palette ----

	/**
	 * The 28 SpringSaLaD color names, resolved from {@link Colors} — the same table VCell uses to
	 * write the solver input, so every name that can appear in a viewer file is covered. (An earlier
	 * hand-written subset here silently rendered LIME, LIME_GREEN, PURPLE, TEAL and a dozen others
	 * as gray.) Built once: the lookup runs per site per frame.
	 */
	private static final Map<String, Color> COLORS = new HashMap<>();
	static {
		for (NamedColor nc : Colors.COLORARRAY) {
			COLORS.put(nc.getName(), visible(nc.getColor()));
		}
		COLORS.put("GREY", COLORS.get(Colors.GRAYSTRING)); // tolerate the British spelling
	}

	/** Resolve a SpringSaLaD color name to a paint color; unknown/absent names render light gray. */
	public static Color colorForName(String name) {
		if (name == null) return Color.LIGHT_GRAY;
		Color c = COLORS.get(name.trim().toUpperCase(Locale.ROOT));
		return c != null ? c : Color.LIGHT_GRAY;
	}

	/**
	 * Lift a color that is too dark to survive the sphere shading (which multiplies by luminance) —
	 * BLACK would otherwise be an invisible disc on the black background.
	 */
	private static Color visible(Color c) {
		int floor = 40;
		if (c.getRed() >= floor || c.getGreen() >= floor || c.getBlue() >= floor) return c;
		return new Color(Math.max(c.getRed(), floor), Math.max(c.getGreen(), floor), Math.max(c.getBlue(), floor));
	}
}
