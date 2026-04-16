package cbit.plot.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.Path2D;

import static cbit.plot.gui.AbstractPlotPanel.*;

public final class PlotRenderers {

    private static final Logger lg = LogManager.getLogger(PlotRenderers.class);

    // Generic renderer interface
    protected interface SeriesRenderer {
        void draw(Graphics2D g2,
                  int x0, int x1, int y0, int y1,
                  int plotWidth, int plotHeight,
                  double xMaxRounded, double yMaxRounded, double yMinRounded,
                  double dt);
        /**
         * Returns the closest pixel-space point to (mouseX, mouseY),
         * or null if this renderer does not support snapping.
         */
        default Point getClosestPoint(int mouseX, int mouseY) {
            return null;    // default: no snapping
        }
        String getSeriesName();
    }

    // -------------------------------------------------------------------------------------------------

    // AVG renderer: polyline
    protected static class AvgRenderer implements SeriesRenderer {
        final String seriesName;    // for legend and hover highlighting
        final double[] time;
        final double[] values;
        final Color color;
        final AbstractPlotPanel parent;

        private int[] xs;
        private int[] ys;

        AvgRenderer(String seriesName, double[] time, double[] values, Color color, AbstractPlotPanel parent) {
            this.seriesName = seriesName;
            this.time = time;
            this.values = values;
            this.color = color;
            this.parent = parent;
        }
        public String getSeriesName() { return seriesName; }

        @Override
        public void draw(Graphics2D g2,
                         int x0, int x1, int y0, int y1,
                         int plotWidth, int plotHeight,
                         double xMaxRounded, double yMaxRounded, double yMinRounded,
                         double dt) {

            int n = values.length;
            if (n < 2) return;

            xs = new int[n];
            ys = new int[n];
            for (int i = 0; i < n; i++) {
                double t = (time != null ? time[i] : i * dt);
                xs[i] = x0 + (int)Math.round((t / xMaxRounded) * plotWidth);
                double v = values[i];
                double norm = (v - yMinRounded) / (yMaxRounded - yMinRounded);
                ys[i] = y0 - (int)Math.round(norm * plotHeight);
            }

            Color c = color;
            // Dim this line if hovering another series
            if (parent.hoveredSeriesName != null) {
                if (!parent.hoveredSeriesName.equals(seriesName)) {
                    c = new Color(c.getRed(), c.getGreen(), c.getBlue(), DIMMED_LINE_ALPHA);
                }
            }

            // Unite the poins as a polyline or as steps, depending on the setting
            if(parent.getShowLines()) {
                if (!parent.stepAvg) {      // draw the polyline (unite the data points directly)
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(CURVE_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawPolyline(xs, ys, n);
                } else {                    // unite the data points as steps
                    // step function: horizontal line from (time[i], values[i]) to (time[i+1], values[i]),
                    // then vertical jump at time[i+1]
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(CURVE_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                    for (int i = 0; i < n - 1; i++) {
                        int xStart = xs[i];
                        int yStart = ys[i];
                        int xEnd = xs[i + 1];
                        int yEnd = ys[i + 1];
                        g2.drawLine(xStart, yStart, xEnd, yStart); // horizontal segment
                        g2.drawLine(xEnd, yStart, xEnd, yEnd);     // vertical jump
                    }
                }
            }

            // Draw nodes if enabled
            if (parent.getShowNodes()) {          // parent is the AbstractPlotPanel
                g2.setColor(c);
                int diameter = parent.nodeDiameter;                 // small, unobtrusive
                if(!parent.getShowLines()) {
                    diameter += 2;                // if no lines, make nodes bigger to be more visible
                }
                int radius = diameter / 2;
                for (int i = 0; i < n; i++) {
                    int cx = xs[i] - radius;
                    int cy = ys[i] - radius;
                    g2.fillOval(cx, cy, diameter, diameter);
                }
            }
        }

        @Override
        public Point getClosestPoint(int mouseX, int mouseY) {
            if (xs == null || ys == null) return null;
            int bestIndex = -1;
            double bestDist2 = Double.POSITIVE_INFINITY;
            for (int i = 0; i < xs.length; i++) {
                double dx = xs[i] - mouseX;
                double dy = ys[i] - mouseY;
                double d2 = dx*dx + dy*dy;

                if (d2 < bestDist2) {
                    bestDist2 = d2;
                    bestIndex = i;
                }
            }
            if (bestIndex < 0) return null;
            return new Point(xs[bestIndex], ys[bestIndex]);
        }
    }

    // -------------------------------------------------------------------------------------------------

    // Band renderer: min/max or sd (envelope)
    protected static class BandRenderer implements SeriesRenderer {
        final String seriesName;    // for legend and hover highlighting
        final double[] time;
        final double[] upper;
        final double[] lower;
        final Color fillColor;
        final AbstractPlotPanel parent;

        BandRenderer(String seriesName, double[] time, double[] lower, double[] upper, Color fillColor, AbstractPlotPanel parent) {
            this.seriesName = seriesName;
            this.time = time;
            this.upper = upper;
            this.lower = lower;
            this.fillColor = fillColor;
            this.parent = parent;
        }
        public String getSeriesName() { return seriesName; }

        @Override
        public void draw(Graphics2D g2,
                         int x0, int x1, int y0, int y1,
                         int plotWidth, int plotHeight,
                         double xMaxRounded, double yMaxRounded, double yMinRounded,
                         double dt) {

            int n = upper.length;
            if (n < 2) return;

            Path2D path = new Path2D.Double();

            double t0 = (time != null ? time[0] : 0.0);
            double v0 = upper[0];
            double norm0 = (v0 - yMinRounded) / (yMaxRounded - yMinRounded);
            int xStart = x0 + (int)Math.round((t0 / xMaxRounded) * plotWidth);
            int yStart = y0 - (int)Math.round(norm0 * plotHeight);
            path.moveTo(xStart, yStart);

            for (int i = 1; i < n; i++) {
                double t = (time != null ? time[i] : i * dt);
                double v = upper[i];
                double norm = (v - yMinRounded) / (yMaxRounded - yMinRounded);
                int x = x0 + (int)Math.round((t / xMaxRounded) * plotWidth);
                int y = y0 - (int)Math.round(norm * plotHeight);
                path.lineTo(x, y);
            }

            for (int i = n - 1; i >= 0; i--) {
                double t = (time != null ? time[i] : i * dt);
                double v = lower[i];
                double norm = (v - yMinRounded) / (yMaxRounded - yMinRounded);
                int x = x0 + (int)Math.round((t / xMaxRounded) * plotWidth);
                int y = y0 - (int)Math.round(norm * plotHeight);
                path.lineTo(x, y);
            }
            path.closePath();

            Color c = fillColor;
            // If hovering, dim all bands except the hovered one
            if (parent.hoveredSeriesName != null) {
                if (!parent.hoveredSeriesName.equals(parent.getSeriesNameForRenderer(this))) {
                    // Dim this band heavily
                    c = new Color(c.getRed(), c.getGreen(), c.getBlue(), DIMMED_BAND_ALPHA);
                }
            }
            if (!parent.stepBand) {
                // your existing Path2D polygon
                g2.setColor(c);
                g2.fill(path);
            } else {
                // step function: horizontal line from (time[i], value[i]) to (time[i+1], value[i]),
                // then vertical jump at time[i+1]; do this for both upper and lower, then connect the ends
                Path2D step = new Path2D.Double();

                // ---- UPPER BOUNDARY (left → right) ----
                // Start at first upper point
                double t0u = (time != null ? time[0] : 0.0);
                double v0u = upper[0];
                double norm0u = (v0u - yMinRounded) / (yMaxRounded - yMinRounded);
                int x0u = x0 + (int)Math.round((t0u / xMaxRounded) * plotWidth);
                int y0u = y0 - (int)Math.round(norm0u * plotHeight);
                step.moveTo(x0u, y0u);
                for (int i = 0; i < n - 1; i++) {
                    // current point
                    double tA = (time != null ? time[i] : i * dt);
                    double vA = upper[i];
                    double normA = (vA - yMinRounded) / (yMaxRounded - yMinRounded);
                    int xA = x0 + (int)Math.round((tA / xMaxRounded) * plotWidth);
                    int yA = y0 - (int)Math.round(normA * plotHeight);
                    // next point
                    double tB = (time != null ? time[i+1] : (i+1) * dt);
                    double vB = upper[i+1];
                    double normB = (vB - yMinRounded) / (yMaxRounded - yMinRounded);
                    int xB = x0 + (int)Math.round((tB / xMaxRounded) * plotWidth);
                    int yB = y0 - (int)Math.round(normB * plotHeight);
                    // horizontal segment: (xA, yA) → (xB, yA)
                    step.lineTo(xB, yA);
                    // vertical segment: (xB, yA) → (xB, yB)
                    step.lineTo(xB, yB);
                }
                // ---- LOWER BOUNDARY (right → left) ----
                for (int i = n - 1; i > 0; i--) {
                    // current point
                    double tA = (time != null ? time[i] : i * dt);
                    double vA = lower[i];
                    double normA = (vA - yMinRounded) / (yMaxRounded - yMinRounded);
                    int xA = x0 + (int)Math.round((tA / xMaxRounded) * plotWidth);
                    int yA = y0 - (int)Math.round(normA * plotHeight);
                    // previous point
                    double tB = (time != null ? time[i-1] : (i-1) * dt);
                    double vB = lower[i-1];
                    double normB = (vB - yMinRounded) / (yMaxRounded - yMinRounded);
                    int xB = x0 + (int)Math.round((tB / xMaxRounded) * plotWidth);
                    int yB = y0 - (int)Math.round(normB * plotHeight);
                    // horizontal segment: (xA, yA) → (xB, yA)
                    step.lineTo(xB, yA);
                    // vertical segment: (xB, yA) → (xB, yB)
                    step.lineTo(xB, yB);
                }
                step.closePath();
                g2.setColor(c);
                g2.fill(step);
            }
        }
    }

    // ------------------------------------------------------------------

    protected static class BarRenderer implements SeriesRenderer {

        final String seriesName;
        final double[] time;
        final double[] upper;
        final double[] lower;
        final Color color;
        final AbstractPlotPanel parent;

        public BarRenderer(String seriesName,
                           double[] time,
                           double[] lower,
                           double[] upper,
                           Color color,
                           AbstractPlotPanel parent) {
            this.seriesName = seriesName;
            this.time = time;
            this.upper = upper;
            this.lower = lower;
            this.color = color;
            this.parent = parent;
        }

        @Override
        public String getSeriesName() {
            return seriesName;
        }

        @Override
        public void draw(Graphics2D g2,
                         int x0, int x1, int y0, int y1,
                         int plotWidth, int plotHeight,
                         double xMaxRounded, double yMaxRounded, double yMinRounded,
                         double dt) {
            int n = upper.length;
            if (n == 0) return;
            Color c = color;

            if (parent.hoveredSeriesName != null &&         // dimming logic is the same as in BandRenderer
                    !parent.hoveredSeriesName.equals(parent.getSeriesNameForRenderer(this))) {
                c = new Color(c.getRed(), c.getGreen(), c.getBlue(), AbstractPlotPanel.DIMMED_BAND_ALPHA);
            }

            g2.setColor(c);
            g2.setStroke(new BasicStroke(2.0f));
            for (int i = 0; i < n; i++) {
                double t = (time != null ? time[i] : i * dt);
                double vLow = lower[i];
                double vHigh = upper[i];
                double vAvg = (vLow + vHigh) / 2.0;
                double normLow = (vLow - yMinRounded) / (yMaxRounded - yMinRounded);
                double normAvg = (vAvg - yMinRounded) / (yMaxRounded - yMinRounded);
                double normHigh = (vHigh - yMinRounded) / (yMaxRounded - yMinRounded);
                int x = x0 + (int)Math.round((t / xMaxRounded) * plotWidth);
                int yLowPix = y0 - (int)Math.round(normLow * plotHeight);
                int yAvgPix  = y0 - (int)Math.round(normAvg * plotHeight);
                int yHighPix = y0 - (int)Math.round(normHigh * plotHeight);

                if (parent.getShowLines()) {
                    g2.drawLine(x, yLowPix, x, yHighPix);                 // vertical SD bar
                    g2.drawLine(x - 3, yLowPix, x + 3, yLowPix);   // optional caps
                    g2.drawLine(x - 3, yHighPix, x + 3, yHighPix);
                } else {
                    // only upper half
                    g2.drawLine(x, yAvgPix, x, yHighPix);                 // vertical SD bar
                    // diamond at top
                    int d = 4; // diamond radius
                    int[] dx = { x, x + d, x, x - d };
                    int[] dy = { yHighPix - d, yHighPix, yHighPix + d, yHighPix };
                    g2.fillPolygon(dx, dy, 4);

                }
            }
        }
    }



}
