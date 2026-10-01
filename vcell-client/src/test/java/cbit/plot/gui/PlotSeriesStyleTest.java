package cbit.plot.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.ColorUtil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class PlotSeriesStyleTest {

	@Test
	public void disabledStylesUseThePlainStroke() {
		BasicStroke stroke = PlotSeriesStyle.stroke(3, false);
		assertTrue(PlotSeriesStyle.solidEqualsDisabledStroke(stroke));
		assertNull(stroke.getDashArray());
	}

	@Test
	public void enabledStylesFollowTheSharedDashContract() {
		assertNull(PlotSeriesStyle.stroke(0, true).getDashArray());
		assertArrayEquals(ColorUtil.seriesDash(1), PlotSeriesStyle.stroke(1, true).getDashArray());
		assertArrayEquals(new float[]{6f, 3f}, PlotSeriesStyle.stroke(1, true).getDashArray());
		assertEquals(BasicStroke.CAP_BUTT, PlotSeriesStyle.stroke(1, true).getEndCap());
		assertEquals(BasicStroke.JOIN_ROUND, PlotSeriesStyle.stroke(1, true).getLineJoin());
	}

	@Test
	public void stepPathInsertsElbowsAndBreaksOnNonFinitePoints() {
		Point2D[] points = new Point2D[]{
				new Point2D.Double(0, 0),
				new Point2D.Double(2, 4),
				new Point2D.Double(Double.NaN, 1),
				new Point2D.Double(6, 1)
		};
		Path2D.Double stepped = PlotSeriesStyle.curvePath(points, true);
		assertTrue(containsPoint(stepped, 2, 0), "step mode should insert the horizontal elbow");
		assertFalse(containsSegmentAcrossNaN(stepped));
		Path2D.Double straight = PlotSeriesStyle.curvePath(points, false);
		assertFalse(straight.getBounds2D().isEmpty());
	}

	@Test
	public void markerShapesDiffer() {
		int[] ink = new int[5];
		for (int series = 0; series < 5; series++) {
			BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();
			graphics.setColor(Color.white);
			graphics.fillRect(0, 0, 16, 16);
			graphics.setColor(Color.black);
			PlotSeriesStyle.paintMarker(graphics, series, 8, 8, 6);
			graphics.dispose();
			ink[series] = countDark(image);
			assertTrue(ink[series] > 0);
		}
		assertTrue(ink[0] != ink[4]);
		assertTrue(ink[1] != ink[2]);
	}

	private static boolean containsPoint(Path2D.Double path, double x, double y) {
		double[] coords = new double[6];
		for (java.awt.geom.PathIterator iterator = path.getPathIterator(null); !iterator.isDone(); iterator.next()) {
			int type = iterator.currentSegment(coords);
			if (type != java.awt.geom.PathIterator.SEG_MOVETO && type != java.awt.geom.PathIterator.SEG_LINETO) {
				continue;
			}
			if (Math.abs(coords[0] - x) < 0.001 && Math.abs(coords[1] - y) < 0.001) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsSegmentAcrossNaN(Path2D.Double path) {
		double[] coords = new double[6];
		double previousX = Double.NaN;
		for (java.awt.geom.PathIterator iterator = path.getPathIterator(null); !iterator.isDone(); iterator.next()) {
			int type = iterator.currentSegment(coords);
			if (type == java.awt.geom.PathIterator.SEG_LINETO && previousX == 2 && coords[0] == 6) {
				return true;
			}
			if (type == java.awt.geom.PathIterator.SEG_MOVETO || type == java.awt.geom.PathIterator.SEG_LINETO) {
				previousX = coords[0];
			}
		}
		return false;
	}

	private static int countDark(BufferedImage image) {
		int count = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if ((image.getRGB(x, y) & 0xffffff) != 0xffffff) {
					count++;
				}
			}
		}
		return count;
	}
}
