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

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

	private SpringSaladTrajectory trajectory;
	private int frameIndex = 0;

	private final Trackball trackball = new Trackball(new Camera());
	private double zoom = 1.0;
	private double panX = 0, panY = 0;
	private boolean showLinks = true;

	// world framing (computed from all frames so the box doesn't jitter during playback)
	private boolean boundsValid = false;
	private double cx, cy, cz;          // scene center
	private double halfExtent = 1.0;    // half of the largest axis extent

	// base white shaded sphere + tinted/depth-shaded sprite cache
	private final BufferedImage baseSprite = makeBaseSprite();
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

	public void resetView() {
		trackball.getCamera().resetView();
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
	 * works headless). Also the basis for future frame/movie export.
	 */
	public BufferedImage renderToImage(int w, int h) {
		setSize(w, h);
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, w, h);
		paintComponent(g);
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
		if (!any) { cx = cy = cz = 0; halfExtent = 1; }
		else {
			cx = (minX + maxX) / 2; cy = (minY + maxY) / 2; cz = (minZ + maxZ) / 2;
			double ext = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
			halfExtent = Math.max(ext / 2, 1e-9);
		}
		boundsValid = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		int w = getWidth(), h = getHeight();
		if (trajectory == null || trajectory.getFrameCount() == 0) {
			g2.setColor(Color.GRAY);
			g2.drawString("No trajectory data.", 12, 20);
			return;
		}
		if (!boundsValid) computeBounds();

		SpringSaladTrajectory.Frame frame = trajectory.getFrames().get(frameIndex);
		Affine rot = new Affine();
		trackball.getMatrixGL(rot);
		double pixelScale = SCREEN_FILL * Math.min(w, h) / halfExtent * zoom;
		double ox = w / 2.0 + panX, oy = h / 2.0 + panY;

		// project all sites for this frame
		List<Glyph> glyphs = new ArrayList<>(frame.getSites().size());
		double minD = Double.POSITIVE_INFINITY, maxD = Double.NEGATIVE_INFINITY;
		Map<Integer, Glyph> byId = new HashMap<>();
		for (SpringSaladTrajectory.Site s : frame.getSites()) {
			Vect3d v = rot.mult(new Vect3d(s.getX() - cx, s.getY() - cy, s.getZ() - cz));
			Glyph gl = new Glyph();
			gl.id = s.getId();
			gl.sx = ox + v.getX() * pixelScale;
			gl.sy = oy - v.getY() * pixelScale;
			gl.depth = v.getZ();
			gl.screenR = Math.max(1.0, s.getRadius() * pixelScale);
			gl.color = colorForName(s.getColor());
			glyphs.add(gl);
			byId.put(gl.id, gl);
			minD = Math.min(minD, gl.depth); maxD = Math.max(maxD, gl.depth);
		}

		// links behind the spheres (nearer sphere then overpaints)
		if (showLinks) {
			g2.setColor(new Color(120, 120, 120));
			for (int[] link : frame.getLinks()) {
				Glyph a = byId.get(link[0]), b = byId.get(link[1]);
				if (a != null && b != null) g2.draw(new Line2D.Double(a.sx, a.sy, b.sx, b.sy));
			}
		}

		// painter's algorithm: far (smaller depth) first
		glyphs.sort((p, q) -> Double.compare(p.depth, q.depth));
		double span = (maxD - minD) < 1e-12 ? 1 : (maxD - minD);
		for (Glyph gl : glyphs) {
			double bright = MIN_BRIGHT + (1 - MIN_BRIGHT) * ((gl.depth - minD) / span); // nearer = brighter
			BufferedImage sprite = getSprite(gl.color, bright);
			int d = (int) Math.round(gl.screenR * 2);
			g2.drawImage(sprite, (int) Math.round(gl.sx - gl.screenR), (int) Math.round(gl.sy - gl.screenR), d, d, null);
		}
	}

	private static final class Glyph {
		int id; double sx, sy, depth, screenR; Color color;
	}

	// ---- impostor sprite generation / cache ----

	private BufferedImage getSprite(Color color, double bright) {
		int bucket = (int) Math.round(bright * (DEPTH_BUCKETS - 1));
		long key = (((long) (color.getRGB() & 0xFFFFFF)) << 8) | bucket;
		BufferedImage cached = spriteCache.get(key);
		if (cached != null) return cached;

		double b = MIN_BRIGHT + (1 - MIN_BRIGHT) * ((double) bucket / (DEPTH_BUCKETS - 1));
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
		if (spriteCache.size() < 4096) spriteCache.put(key, out);
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
	private static final Map<String, Color> COLORS = new HashMap<>();
	static {
		COLORS.put("RED", Color.RED); COLORS.put("GREEN", new Color(0, 200, 0));
		COLORS.put("BLUE", new Color(60, 90, 255)); COLORS.put("YELLOW", Color.YELLOW);
		COLORS.put("CYAN", Color.CYAN); COLORS.put("MAGENTA", Color.MAGENTA);
		COLORS.put("ORANGE", Color.ORANGE); COLORS.put("PINK", Color.PINK);
		COLORS.put("GRAY", Color.GRAY); COLORS.put("GREY", Color.GRAY);
		COLORS.put("LIGHT_GRAY", Color.LIGHT_GRAY); COLORS.put("DARK_GRAY", Color.DARK_GRAY);
		COLORS.put("WHITE", Color.WHITE); COLORS.put("BLACK", new Color(40, 40, 40));
	}

	private static Color colorForName(String name) {
		if (name == null) return Color.LIGHT_GRAY;
		Color c = COLORS.get(name.trim().toUpperCase(Locale.ROOT));
		return c != null ? c : Color.LIGHT_GRAY;
	}
}
