package cbit.plot.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

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
        default BubbleHit getClosestBubble(int mouseX, int mouseY) {
            return null;    // default: not a bubble renderer
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
        final int seriesIndex;
        final AbstractPlotPanel parent;

        private int[] xs;
        private int[] ys;

        AvgRenderer(String seriesName, double[] time, double[] values, Color color, int seriesIndex, AbstractPlotPanel parent) {
            this.seriesName = seriesName;
            this.time = time;
            this.values = values;
            this.color = color;
            this.seriesIndex = seriesIndex;
            this.parent = parent;
        }
        public String getSeriesName() { return seriesName; }

        BasicStroke stroke() {
            return parent.strokeForSeriesIndex(seriesIndex);
        }

        @Override
        public void draw(Graphics2D g2,
                         int x0, int x1, int y0, int y1,
                         int plotWidth, int plotHeight,
                         double xMaxRounded, double yMaxRounded, double yMinRounded,
                         double dt) {

            if (!parent.isSeriesVisible(seriesName)) {
                xs = null;
                ys = null;
                return;
            }

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
            if (parent.isSeriesDimmed(seriesName)) {
                c = new Color(c.getRed(), c.getGreen(), c.getBlue(), DIMMED_LINE_ALPHA);
            }

            // One path keeps the dash phase continuous, including step mode.
            if(parent.getShowLines()) {
                Path2D path = new Path2D.Double();
                path.moveTo(xs[0], ys[0]);
                if (!parent.isShowAvgAsStep()) {
                    for (int i = 1; i < n; i++) {
                        path.lineTo(xs[i], ys[i]);
                    }
                } else {
                    for (int i = 0; i < n - 1; i++) {
                        path.lineTo(xs[i + 1], ys[i]);
                        path.lineTo(xs[i + 1], ys[i + 1]);
                    }
                }
                g2.setColor(c);
                g2.setStroke(stroke());
                g2.draw(path);
            }

            // Draw nodes if enabled. Marker shape follows the stable series index.
            if (parent.getShowNodes()) {
                g2.setColor(c);
                int diameter = Math.max(6, parent.getNodeDiameter());
                if(!parent.getShowLines()) {
                    diameter += 2;
                }
                // Dense samples would merge the markers into a solid band that hides the dash.
                // With lines on, keep 4 diameters between markers: at the 6 px minimum that leaves
                // 18 px of line, more than the longest dash period (16 px). Without lines the
                // markers are the data, so every sample keeps one.
                int markerSpacing = parent.getShowLines() ? 4 * diameter : 0;
                int nextMarkerX = Integer.MIN_VALUE;
                for (int i = 0; i < n; i++) {
                    if (xs[i] < nextMarkerX) {
                        continue;
                    }
                    PlotSeriesStyle.paintMarker(g2, seriesIndex, xs[i], ys[i], diameter);
                    nextMarkerX = xs[i] + markerSpacing;
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

            if (!parent.isSeriesVisible(seriesName)) {
                return;
            }

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
            if (parent.isSeriesDimmed(seriesName)) {
                c = new Color(c.getRed(), c.getGreen(), c.getBlue(), DIMMED_BAND_ALPHA);
            }
            if (!parent.isShowBandAsStep()) {
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
                // VERTICAL SEGMENT from last upper point to first lower point
                // compute first lower point at the rightmost x
                double tLast = (time != null ? time[n - 1] : (n - 1) * dt);
                double vLastLower = lower[n - 1];
                double normLastLower = (vLastLower - yMinRounded) / (yMaxRounded - yMinRounded);
                int xLast = x0 + (int)Math.round((tLast / xMaxRounded) * plotWidth);
                int yLastLower = y0 - (int)Math.round(normLastLower * plotHeight);
                // vertical segment from last upper point to first lower point
                step.lineTo(xLast, yLastLower);

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
            if (!parent.isSeriesVisible(seriesName)) {
                return;
            }
            int n = upper.length;
            if (n == 0) return;
            Color c = color;

            if (parent.isSeriesDimmed(seriesName)) {
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

    // -------------------------------------------------------------------------------------------------
// BUBBLE renderer: y-position = cluster size, bubble diameter = count
// -------------------------------------------------------------------------------------------------
    protected static class BubbleRenderer implements SeriesRenderer {
        final String seriesName;      // e.g. "C3" or "Cluster_3"
        final double[] time;          // timepoints
        final double[] values;        // counts or mass
        final int clusterSize;        // extracted from seriesName, used for Y-position
        final Color color;
        final int seriesIndex;
        final AbstractPlotPanel parent;

        private int[] xs;             // pixel x positions
        private int[] ys;             // pixel y positions
        private int[] diameters;      // bubble diameters in pixels

        BubbleRenderer(String seriesName, double[] time, double[] values,
                       Color color, int seriesIndex, AbstractPlotPanel parent) {
            this.seriesName = seriesName;
            this.time = time;
            this.values = values;
            this.color = color;
            this.seriesIndex = seriesIndex;
            this.clusterSize = extractClusterSize(seriesName);
            this.parent = parent;
        }

        @Override
        public String getSeriesName() {
            return seriesName;
        }

        int getClusterSize() {
            return clusterSize;
        }

        // extract cluster size from series name (e.g. "C3", "Cluster_3", "Size 3")
        // right now the name is numeric-only, but this is more flexible in case we want to add prefixes later
        private int extractClusterSize(String name) {
            String digits = name.replaceAll("\\D+", "");
            if (digits.isEmpty()) {
                return 0;   // fallback, shouldn't happen
            }
            return Integer.parseInt(digits);
        }

        @Override
        public void draw(Graphics2D g2,
                         int x0, int x1, int y0, int y1,
                         int plotWidth, int plotHeight,
                         double xMaxRounded, double yMaxRounded, double yMinRounded,
                         double dt) {

            if (!parent.isSeriesVisible(seriesName)) {
                xs = null;
                ys = null;
                diameters = null;
                return;
            }

            int n = values.length;
            if (n == 0) return;

            xs = new int[n];
            ys = new int[n];
            diameters = new int[n];

            // 1. Determine the vertical pixel position for this cluster size
            //    In bubble mode, Y-axis = cluster size (a discrete category).
            //    All bubbles for this series lie on one horizontal line.
            double yNorm;
            if (!parent.options.showOnlySelectedSeries) {
                // original behavior
                yNorm = (clusterSize - yMinRounded) / (yMaxRounded - yMinRounded);
            } else {
                // compressed mode: map clusterSize → ordinal index
                int index = parent.getOrdinalIndexForClusterSize(clusterSize);
                int row = index + 1;    // shift up, keep 0 empty
                yNorm = (row - yMinRounded) / (yMaxRounded - yMinRounded);
            }
            int yPix = y0 - (int)Math.round(yNorm * plotHeight);

            // 2. Bubble size scaling
            //    Diameter encodes the count (or mass).
            //    Scaling is GLOBAL across all cluster sizes, not per-series.
            //    This ensures correct proportionality (e.g., 15.4 vs 0.05).
            final double minDiam = 4.0;     // smallest visible bubble
            final double maxDiam = 36.0;    // largest bubble
            double globalMax;
            if (parent instanceof ClusterPlotPanel) {
                globalMax = ((ClusterPlotPanel) parent).getMaxClusterOverall();
            } else {
                lg.warn("Unexpected plot panel type for bubble renderer: " + parent.getClass().getName());
                globalMax = 1.0;   // fallback, shouldn't happen
            }
            if (globalMax <= 0) return;     // nothing to draw

            // 3. Compute pixel coordinates for each timepoint
            for (int i = 0; i < n; i++) {

                double count = values[i];

                // Skip zero counts entirely — no bubble should be drawn
                if (count <= 0) {
                    xs[i] = ys[i] = diameters[i] = 0;
                    continue;
                }

                // X-position from time
                double t = (time != null ? time[i] : i * dt);
                xs[i] = x0 + (int) Math.round((t / xMaxRounded) * plotWidth);

                // Y-position is constant for this cluster size
                ys[i] = yPix;

                // Perceptual bubble scaling: diameter ∝ sqrt(count)
                double d;
                if (parent.isShowBubbleSizeByCount()) {
                    // normal perceptual scaling
                    double frac = Math.sqrt(count / globalMax);
                    d = minDiam + frac * (maxDiam - minDiam);
                } else {
                    // all bubbles same size
                    d = (minDiam + maxDiam) * 0.5;
                }
                diameters[i] = (int) Math.round(d);
            }

            Color c;
            // 4. single-color mode overrides series color
            if (parent.isShowBubbleSingleColor()) {
                c = new Color(220, 30, 30);   // a strong red for maximum visibility of bubbles
            } else {
                c = color;
            }

            if (parent.isSeriesDimmed(seriesName)) {
                c = new Color(c.getRed(), c.getGreen(), c.getBlue(), DIMMED_SOLID_BUBBLE_ALPHA);
                if(parent.isShowBubbleFading()) {
                    c = new Color(c.getRed(), c.getGreen(), c.getBlue(), DIMMED_FADING_BUBBLE_ALPHA);
                }
            }
            g2.setColor(c);

            // 6. Draw bubbles (mutually exclusive modes)
            if (parent.isShowBubbleAsEmptyCircles()) {  // --- Contour Bubble ---
                for (int i = 0; i < n; i++) {
                    int d = diameters[i];
                    if (d <= 0) continue;
                    drawEmptyCircle(g2, xs[i], ys[i], d, c);
                }
            } else if (parent.isShowBubbleFading()) {   // --- Fading Bubble ---
                for (int i = 0; i < n; i++) {
                    int d = diameters[i];
                    if (d <= 0) continue;
                    drawFadingBubble(g2, xs[i], ys[i], d, c);
                }
            } else if(parent.isShowBubbleSolid()) {   // --- Solid Bubble (default) ---
                for (int i = 0; i < n; i++) {
                    int d = diameters[i];
                    if (d <= 0) continue;
                    drawSolidBubble(g2, xs[i], ys[i], d, c);
                }
            } else {
                lg.warn("Inconsistent bubble style setting. Defaulting to solid bubbles.");
                for (int i = 0; i < n; i++) {
                    int d = diameters[i];
                    if (d <= 0) continue;
                    drawSolidBubble(g2, xs[i], ys[i], d, c);
                }
            }
        }

        private void drawFadingBubble(Graphics2D g2, int xCenter, int yCenter, int d, Color base) {
            int r = d / 2;
            int cx = xCenter - r;
            int cy = yCenter - r;
            float centerX = xCenter;
            float centerY = yCenter;
            float radius  = r;
            // fading colors
            Color edge = new Color(base.getRed(), base.getGreen(), base.getBlue(), 40);     // 60
            Color mid  = new Color(base.getRed(), base.getGreen(), base.getBlue(), 160);    // 128
            if (parent.isSeriesDimmed(seriesName)) {
                edge = new Color(base.getRed(), base.getGreen(), base.getBlue(), 10);
                mid  = new Color(base.getRed(), base.getGreen(), base.getBlue(), 40);
            }
            // abrupt drop near center, slow fade to edge
            float[] dist = {0.0f, 0.35f, 1.0f};         // 0.0f, 0.55f, 1.0f
            Color[] cols = {base, mid, edge};
            RadialGradientPaint paint = new RadialGradientPaint(
                    new Point2D.Float(centerX, centerY),
                    radius,
                    dist,
                    cols
            );
            Paint old = g2.getPaint();
            Composite oldComp = g2.getComposite();
            // normalize opacity so overlapping bubbles don't saturate
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            g2.setPaint(paint);
            g2.fillOval(cx, cy, d, d);
            g2.setComposite(oldComp);
            g2.setPaint(old);
        }
        private void drawEmptyCircle(Graphics2D g2, int xCenter, int yCenter, int d, Color c) {
            int r = d / 2;
            int cx = xCenter - r;
            int cy = yCenter - r;
            // center dot
            int dot = Math.max(2, d / 6);
            int dotR = dot / 2;
            g2.setColor(c);
            g2.fillOval(xCenter - dotR, yCenter - dotR, dot, dot);
            // contour
            g2.setStroke(parent.strokeForSeriesIndex(seriesIndex));
            g2.drawOval(cx, cy, d, d);
        }

        private void drawSolidBubble(Graphics2D g2, int xCenter, int yCenter, int d, Color base) {
            int r = d / 2;
            int cx = xCenter - r;
            int cy = yCenter - r;
            Paint oldPaint = g2.getPaint();
            Composite oldComp = g2.getComposite();
            // Slight transparency so overlapping bubbles show through
            // 0.55f is a good balance: mostly solid, but not opaque
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
            g2.setPaint(base);
            g2.fillOval(cx, cy, d, d);
            g2.setComposite(oldComp);
            g2.setPaint(oldPaint);
        }

        @Override
        public BubbleHit getClosestBubble(int mouseX, int mouseY) {
            if (xs == null || ys == null) return null;
            int bestIndex = -1;
            double bestDist2 = Double.POSITIVE_INFINITY;
            for (int i = 0; i < xs.length; i++) {
                int bx = xs[i];
                int by = ys[i];
                double dx = bx - mouseX;
                double dy = by - mouseY;
                double d2 = dx*dx + dy*dy;
                if (d2 < bestDist2) {
                    bestDist2 = d2;
                    bestIndex = i;
                }
            }
            if (bestIndex < 0) return null;
            double maxDist = diameters[bestIndex] / 2.0 + 6.0;  // threshold: bubble radius + some padding
            if (bestDist2 > maxDist * maxDist) return null;
            return new BubbleHit(
                    time[bestIndex],
                    clusterSize,          // or ColumnDescription label later
                    values[bestIndex],
                    xs[bestIndex],
                    ys[bestIndex]
            );
        }
    }

}
