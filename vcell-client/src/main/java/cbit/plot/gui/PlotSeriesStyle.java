package cbit.plot.gui;

import org.vcell.util.ColorUtil;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Shared stroke and marker geometry for a visible plot series.
 * {@link ColorUtil} is the only dash catalog.
 */
final class PlotSeriesStyle {
	private static final BasicStroke SOLID_LINE = new BasicStroke(1.5f);
	private static final BasicStroke CROSS_STROKE = new BasicStroke(1.5f);

	private PlotSeriesStyle() {
	}

	static BasicStroke stroke(int seriesIndex, boolean varyLineStyles) {
		if (!varyLineStyles) {
			return new BasicStroke(1.5f);
		}
		return new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10f,
				ColorUtil.seriesDash(seriesIndex), 0f);
	}

	static boolean solidEqualsDisabledStroke(BasicStroke stroke) {
		return SOLID_LINE.equals(stroke);
	}

	static void paintMarker(Graphics2D g, int seriesIndex, double centerX, double centerY, double size) {
		Paint savedPaint = g.getPaint();
		Stroke savedStroke = g.getStroke();
		AffineTransform savedTransform = g.getTransform();
		try {
			g.translate(centerX, centerY);
			double half = size / 2.0;
			switch (Math.floorMod(seriesIndex, 5)) {
				case 0 -> g.fill(new Ellipse2D.Double(-half, -half, size, size));
				case 1 -> g.fill(new Rectangle2D.Double(-half, -half, size, size));
				case 2 -> {
					Path2D triangle = new Path2D.Double();
					triangle.moveTo(0, -half);
					triangle.lineTo(half, half);
					triangle.lineTo(-half, half);
					triangle.closePath();
					g.fill(triangle);
				}
				case 3 -> {
					Path2D diamond = new Path2D.Double();
					diamond.moveTo(0, -half);
					diamond.lineTo(half, 0);
					diamond.lineTo(0, half);
					diamond.lineTo(-half, 0);
					diamond.closePath();
					g.fill(diamond);
				}
				case 4 -> {
					g.setStroke(CROSS_STROKE);
					g.draw(new Line2D.Double(-half, -half, half, half));
					g.draw(new Line2D.Double(-half, half, half, -half));
				}
				default -> throw new IllegalStateException("marker index");
			}
		} finally {
			g.setPaint(savedPaint);
			g.setStroke(savedStroke);
			g.setTransform(savedTransform);
		}
	}

	static Path2D.Double curvePath(Point2D[] points, boolean stepMode) {
		Path2D.Double path = new Path2D.Double();
		boolean penDown = false;
		double previousY = 0;
		if (points == null) {
			return path;
		}
		for (Point2D point : points) {
			double x = point.getX();
			double y = point.getY();
			if (!isFinite(x) || !isFinite(y)) {
				penDown = false;
				continue;
			}
			if (!penDown) {
				path.moveTo(x, y);
				penDown = true;
			} else if (stepMode) {
				path.lineTo(x, previousY);
				path.lineTo(x, y);
			} else {
				path.lineTo(x, y);
			}
			previousY = y;
		}
		return path;
	}

	static boolean isFinite(double value) {
		return !Double.isNaN(value) && !Double.isInfinite(value);
	}
}
