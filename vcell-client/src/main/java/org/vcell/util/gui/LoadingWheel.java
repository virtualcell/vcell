package org.vcell.util.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * VCell's loading wheel, as an icon of any size: a gray ring, outlined, with a green arc that turns around it. It draws
 * only the ring, so whatever is behind it shows through. {@link #turn()} moves the arc on a step; {@link #animate}
 * turns it, repainting a component, every {@link #STEP_MILLIS}.
 */
public final class LoadingWheel implements Icon {
	public static final int STEP_MILLIS = 40;
	private static final double STEP = Math.PI / 12; // radians
	private static final double ARC_DEGREES = 80;
	// Proportions of the wheel's size, as drawn at 60 pixels
	private static final float INSET = 15 / 60f, RING_STROKE = 7 / 60f, ARC_STROKE = 5 / 60f, OUTLINE_OFFSET = 3 / 60f, GLOW_RADIUS = 55 / 60f;

	private final int size;
	private double radians = Math.PI; // where the arc starts

	/** A wheel {@code size} pixels square. */
	public LoadingWheel(int size) {
		this.size = size;
	}

	/** Moves the arc on a step. */
	public void turn() {
		this.radians -= STEP;
	}

	/** A timer (not yet started) that turns the wheel and repaints {@code shownOn} every step. */
	public Timer animate(Component shownOn) {
		Timer timer = new Timer(STEP_MILLIS, e -> {
			this.turn();
			shownOn.repaint();
		});
		timer.setRepeats(true);
		return timer;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(x, y);
		float inset = this.size * INSET;
		double diameter = this.size - 2 * inset;

		g2.setStroke(new BasicStroke(this.size * RING_STROKE)); // the ring
		g2.setPaint(Color.lightGray);
		g2.draw(new Arc2D.Double(inset, inset, diameter, diameter, 0, 360, Arc2D.OPEN));

		// the arc, brightest at its leading end
		double radius = diameter / 2;
		Point2D lead = new Point2D.Double(this.size / 2.0 + radius * Math.sin(this.radians), this.size / 2.0 + radius * Math.cos(this.radians));
		g2.setPaint(new RadialGradientPaint(lead, this.size * GLOW_RADIUS, lead, new float[] {0.1f, 0.2f, 0.7f},
				new Color[] {Color.green, Color.green, Color.lightGray}, CycleMethod.NO_CYCLE));
		g2.setStroke(new BasicStroke(this.size * ARC_STROKE));
		g2.draw(new Arc2D.Double(inset, inset, diameter, diameter, Math.toDegrees(this.radians), ARC_DEGREES, Arc2D.OPEN));

		// the ring's edges
		g2.setStroke(new BasicStroke(Math.max(1f, this.size / 60f)));
		g2.setColor(Color.darkGray);
		float offset = this.size * OUTLINE_OFFSET;
		g2.draw(new Ellipse2D.Double(inset - offset, inset - offset, diameter + 2 * offset, diameter + 2 * offset));
		g2.draw(new Ellipse2D.Double(inset + offset, inset + offset, diameter - 2 * offset, diameter - 2 * offset));
		g2.dispose();
	}

	@Override
	public int getIconWidth() {
		return this.size;
	}

	@Override
	public int getIconHeight() {
		return this.size;
	}
}
