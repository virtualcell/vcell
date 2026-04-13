package cbit.plot.gui;

import cbit.vcell.solver.ode.gui.MoleculeVisualizationPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

public abstract class AbstractPlotPanel extends JPanel {

    private static final Logger lg = LogManager.getLogger(AbstractPlotPanel.class);

    // Insets and strokes
    protected static final int LEFT_INSET   = 50;
    protected static final int RIGHT_INSET  = 20;
    protected static final int TOP_INSET    = 20;
    protected static final int BOTTOM_INSET = 30;

    protected static final float AXIS_STROKE  = 1.0f;
    protected static final float CURVE_STROKE = 1.5f;

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
    }

    // AVG renderer: polyline
    protected static class AvgRenderer implements SeriesRenderer {
        final double[] time;
        final double[] values;
        final Color color;
        final AbstractPlotPanel parent;

        private int[] xs;
        private int[] ys;

        AvgRenderer(double[] time, double[] values, Color color, AbstractPlotPanel parent) {
            this.time = time;
            this.values = values;
            this.color = color;
            this.parent = parent;
        }
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

            g2.setColor(color);
            g2.setStroke(new BasicStroke(CURVE_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawPolyline(xs, ys, n);

            // Draw the polyline
            g2.setColor(color);
            g2.setStroke(new BasicStroke(CURVE_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawPolyline(xs, ys, n);

            // Draw nodes if enabled
            if (parent.getShowNodes()) {          // parent is the AbstractPlotPanel
                g2.setColor(color);
                int diameter = 4;                 // small, unobtrusive
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

    // Band renderer: min/max or sd (envelope)
    protected static class BandRenderer implements SeriesRenderer {
        final double[] time;
        final double[] upper;
        final double[] lower;
        final Color fillColor;
        final AbstractPlotPanel parent;

        BandRenderer(double[] time, double[] upper, double[] lower, Color fillColor, AbstractPlotPanel parent) {
            this.time = time;
            this.upper = upper;
            this.lower = lower;
            this.fillColor = fillColor;
            this.parent = parent;
        }

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
            g2.setColor(fillColor);
            g2.fill(path);
        }
    }

    // Renderer options list
    private boolean fieldShowNodes = true;      // whether to draw small circles at the data points (nodes)
    private boolean fieldSnapToNodes = true;    // whether the crosshair snaps to the nearest node (if false, it shows exact mouse coordinates)
    private boolean fieldShowCrosshair = true;  // whether to show the crosshair at all (if false, mouse coordinates are still tracked and sent to the callback, but no crosshair is drawn)

    // Renderers list
    protected final List<SeriesRenderer> renderers = new ArrayList<>();

    // Scaling state
    protected double globalMin = 0;         // on the-y axis; x-axis is always 0 to dt*(N-1)
    protected double globalMax = 1;
    protected double dt = 1;

    // Crosshair state
    protected Integer mouseX = null;    // mouse coordinates in pixels, relative to the panel; null if mouse is outside the plot area
    protected Integer mouseY = null;
    protected boolean crosshairEnabled = true;
    protected Consumer<double[]> coordCallback;

    // Cached plot area
    protected int lastX0, lastX1, lastY0, lastY1; // pixel coordinates of the plot area (insets from the panel edges)
    protected double lastXMaxRounded;   // in seconds
    protected double lastYMaxRounded;   // in molecules
    protected double lastYMinRounded;   // in molecules (could be negative if avg-sd<0

    public AbstractPlotPanel() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mx = e.getX();      // in pixels, relative to the panel
                int my = e.getY();

                if (mx >= lastX0 && mx <= lastX1 && my >= lastY1 && my <= lastY0) {
                    mouseX = mx;
                    mouseY = my;
                } else {
                    mouseX = null;
                    mouseY = null;
                }

                if (crosshairEnabled && mouseX != null && mouseY != null) {
                    mx = mouseX;
                    my = mouseY;
                    if (fieldSnapToNodes) {
                        Point snapped = findClosestNode(mx, my);
                        if (snapped != null) {
                            mx = snapped.x;     // mx and my are now snapped to the nearest node's pixel coordinates
                            my = snapped.y;
                            mouseX = mx;        // update mouseX and mouseY to the snapped coordinates for crosshair drawing
                            mouseY = my;
                        }
                    }
                    double fracX = (mx - lastX0) / (double)(lastX1 - lastX0);
                    double xVal = fracX * lastXMaxRounded;      // mouse coord on x-axis in seconds
                    double fracY = (lastY0 - my) / (double)(lastY0 - lastY1);
                    double yVal = lastYMinRounded + fracY * (lastYMaxRounded - lastYMinRounded); // mouse coord on y-axis in molecules

                    if (coordCallback != null) {
                        coordCallback.accept(new double[]{xVal, yVal});
                    }
                } else {
                    if (coordCallback != null) {
                        coordCallback.accept(null);
                    }
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                mouseX = null;
                mouseY = null;
                repaint();
            }
        });
    }

    // Public API

    public void setCrosshairEnabled(boolean enabled) {
        this.crosshairEnabled = enabled;
        repaint();
    }

    public void setCoordinateCallback(Consumer<double[]> cb) {
        this.coordCallback = cb;
    }

    public void clearAllRenderers() {
        renderers.clear();
    }

    public void setGlobalMinMax(double min, double max) {
        this.globalMin = min;
        this.globalMax = max;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    // High-level, stat-aware renderers

    public void addAvgRenderer(double[] time, double[] avg, Color color, String name, Object statTag) {
        renderers.add(new AvgRenderer(time, avg, color, this));
    }

    public void addMinMaxRenderer(double[] time, double[] min, double[] max, Color color, String name, Object statTag) {
        renderers.add(new BandRenderer(time, max, min, color, this));
    }

    public void addSDRenderer(double[] time, double[] low, double[] high, Color color, String name, Object statTag) {
        renderers.add(new BandRenderer(time, high, low, color, this));
    }

    // Utilities

    protected double roundUpNice(double value) {
        if (value <= 0) return 1;
        double exp = Math.pow(10, Math.floor(Math.log10(value)));
        double n = value / exp;
        double[] steps = {1,2,3,4,5,6,7,8,9,10};    // 1–10 sequence
        for (double s : steps) {
            if (n <= s) return s * exp;
        }
        return 10 * exp;
    }

    public static String formatTick(double value, double step) {
        double absStep = Math.abs(step);
        String s;
        if (absStep >= 1.0)       s = String.format("%.0f", value);
        else if (absStep >= 0.1)  s = String.format("%.1f", value);
        else if (absStep >= 0.01) s = String.format("%.2f", value);
        else if (absStep >= 0.001)s = String.format("%.3f", value);
        else if (absStep >= 0.0001)s = String.format("%.4f", value);
        else return String.format("%.2E", value);

        while (s.contains(".") && s.endsWith("0")) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public boolean getShowNodes() {
        return fieldShowNodes;
    }
    public boolean getSnapToNodes() {
        return fieldSnapToNodes;
    }
    private Point findClosestNode(int mouseX, int mouseY) {     // use for SnapToNodes feature
        Point best = null;
        double bestDist2 = Double.POSITIVE_INFINITY;
        for (SeriesRenderer r : renderers) {
            Point p = r.getClosestPoint(mouseX, mouseY);
            if (p != null) {
                double dx = p.x - mouseX;
                double dy = p.y - mouseY;
                double d2 = dx*dx + dy*dy;
                if (d2 < bestDist2) {
                    bestDist2 = d2;
                    best = p;
                }
            }
        }
        // Snap only if within a threshold (e.g., 10px)
        if (best != null && bestDist2 <= 400) { // 10px radius
            return best;
        }
        return null;
    }

    // -------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();     // width of the component (in pixels)
        int h = getHeight();    // height of the component
        g2.setColor(Color.white);
        g2.fillRect(0, 0, w, h);

        int x0 = LEFT_INSET;
        int x1 = w - RIGHT_INSET;
        int y0 = h - BOTTOM_INSET;
        int y1 = TOP_INSET;
        lastX0 = x0;    // in pixels
        lastX1 = x1;
        lastY0 = y0;
        lastY1 = y1;

        int plotWidth = x1 - x0;
        int plotHeight = y0 - y1;
        if (plotWidth <= 0 || plotHeight <= 0) return;

        // --- determine max length from all renderers that use arrays -----
        int maxLength = 0;      // number of timepoints
        for (SeriesRenderer r : renderers) {
            if (r instanceof AvgRenderer ar) {
                maxLength = Math.max(maxLength, ar.values.length);
            } else if (r instanceof BandRenderer br) {
                maxLength = Math.max(maxLength, br.upper.length);
            }
        }
        if (maxLength < 2) return;

        // --- compute axis scaling -----------------------------------------
        double yMaxRounded = roundUpNice(globalMax);    // in molecules
        double yMinRounded = (globalMin < 0) ? -roundUpNice(-globalMin) : 0;    // may be negative if avg-sd<0
        double xMax = dt * (maxLength - 1);             // in seconds
        double xMaxRounded = roundUpNice(xMax);
        lastXMaxRounded = xMaxRounded;  // seconds
        lastYMaxRounded = yMaxRounded;  // molecules
        lastYMinRounded = yMinRounded;  // molecules

        // --- compute pixel location of value zero on the y-axis, to draw the horizontal axis there
        double normZero = (0 - yMinRounded) / (yMaxRounded - yMinRounded);
        int yZeroPix = y0 - (int)Math.round(normZero * plotHeight);

        FontMetrics fm = g2.getFontMetrics();

        // --- grid lines ----------------------------------------------
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1f));

        int yTicks = 5;     // number of major horizontal ticks (above 0)
        double yStep = yMaxRounded / yTicks;
        // k runs over all integer multiples of yStep that fall inside the range
        int kMin = (int)Math.floor(yMinRounded / yStep);
        int kMax = (int)Math.ceil(yMaxRounded / yStep);
        for (int k = kMin; k <= kMax; k++) {
            double valueMajor = k * yStep;                  // ----- major gridline -----
            if (valueMajor >= yMinRounded - 1e-9 && valueMajor <= yMaxRounded + 1e-9) {
                double normMajor = (valueMajor - yMinRounded) / (yMaxRounded - yMinRounded);
                int yPixMajor = y0 - (int)Math.round(normMajor * plotHeight);
                g2.drawLine(x0, yPixMajor, x1, yPixMajor);
            }
            double valueMid = valueMajor + yStep / 2.0;     // ----- mid gridline -----
            if (valueMid >= yMinRounded && valueMid <= yMaxRounded) {
                double normMid = (valueMid - yMinRounded) / (yMaxRounded - yMinRounded);
                int yPixMid = y0 - (int)Math.round(normMid * plotHeight);
                g2.drawLine(x0, yPixMid, x1, yPixMid);
            }
        }

        double[] xMajor = {0, xMaxRounded / 2, xMaxRounded};    // vertical grid lines
        for (int i = 0; i < xMajor.length; i++) {
            double xvMajor = xMajor[i];
            int xPixMajor = x0 + (int)Math.round((xvMajor / xMaxRounded) * plotWidth);
            g2.drawLine(xPixMajor, y1, xPixMajor, y0);

            if (i < xMajor.length - 1) {
                double xvMid = (xMajor[i] + xMajor[i + 1]) / 2.0;
                int xPixMid = x0 + (int)Math.round((xvMid / xMaxRounded) * plotWidth);
                g2.drawLine(xPixMid, y1, xPixMid, y0);
            }
        }

        // --- draw axes ------------------------------------------------
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(AXIS_STROKE));

        g2.drawLine(x0, yZeroPix, x1, yZeroPix); // horizontal axis, going through the "0 molecules" point
        g2.drawLine(x0, y0, x0, y1);             // vertical axis

        // --- ticks ---------------------------------------------------
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(AXIS_STROKE));

        // yStep was computed as: yStep = yMaxRounded / yTicks;
        // and gridlines were drawn at k * yStep for k in [floor(min/step), ceil(max/step)]
        kMin = (int)Math.floor(yMinRounded / yStep);        // y-axis ticks (on the vertical axis)
        kMax = (int)Math.ceil(yMaxRounded / yStep);
        for (int k = kMin; k <= kMax; k++) {
            double value = k * yStep;
            if (value < yMinRounded - 1e-9 || value > yMaxRounded + 1e-9) {
                continue;   // skip values outside the rounded range (floating‑point guard)
            }
            // convert to pixel using the SAME normalization as gridlines and renderer
            double norm = (value - yMinRounded) / (yMaxRounded - yMinRounded);
            int yPix = y0 - (int)Math.round(norm * plotHeight);
            g2.drawLine(x0 - 5, yPix, x0, yPix);     // major tick (little horizontal line on the vertical axis)
            String label = formatTick(value, yStep);    // label
            int sw = fm.stringWidth(label);
            g2.drawString(label, x0 - 10 - sw, yPix + fm.getAscent() / 2);

            if (k < kMax) {     // mid tick (between this and next)
                double midValue = value + yStep / 2.0;
                if (midValue >= yMinRounded && midValue <= yMaxRounded) {
                    double normMid = (midValue - yMinRounded) / (yMaxRounded - yMinRounded);
                    int yPixMid = y0 - (int)Math.round(normMid * plotHeight);
                    g2.drawLine(x0 - 3, yPixMid, x0, yPixMid);
                }
            }
        }
        double xStep = xMajor[1] - xMajor[0];       // x-axis ticks (on the horizontal axis)
        for (int i = 0; i < xMajor.length; i++) {
            double xvMajor = xMajor[i];
            int xPixMajor = x0 + (int)Math.round((xvMajor / xMaxRounded) * plotWidth);
            g2.drawLine(xPixMajor, yZeroPix, xPixMajor, yZeroPix + 5);  // draw major tick on the x-axis (yZeroPix), not at y0

            String label = formatTick(xvMajor, xStep);  // label stays at the bottom
            int sw = fm.stringWidth(label);
            g2.drawString(label, xPixMajor - sw / 2, y0 + fm.getAscent() + 5);

            if (i < xMajor.length - 1) {
                double xvMid = (xMajor[i] + xMajor[i + 1]) / 2.0;
                int xPixMid = x0 + (int)Math.round((xvMid / xMaxRounded) * plotWidth);
                g2.drawLine(xPixMid, yZeroPix, xPixMid, yZeroPix + 3);  // mid tick also on the x‑axis
            }
        }

        // Crosshair
        if (crosshairEnabled && mouseX != null && mouseY != null) {
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(mouseX, y1, mouseX, y0);
            g2.drawLine(x0, mouseY, x1, mouseY);
        }

        // Renderers (bands first, then lines)
        for (SeriesRenderer r : renderers) {
            if (r instanceof BandRenderer) {
                r.draw(g2, x0, x1, y0, y1, plotWidth, plotHeight, xMaxRounded, yMaxRounded, yMinRounded, dt);
            }
        }
        for (SeriesRenderer r : renderers) {
            if (r instanceof AvgRenderer) {
                r.draw(g2, x0, x1, y0, y1, plotWidth, plotHeight, xMaxRounded, yMaxRounded, yMinRounded, dt);
            }
        }
    }
}