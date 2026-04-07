package cbit.plot.gui;

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
    }

    // AVG renderer: polyline
    protected static class AvgRenderer implements SeriesRenderer {
        final double[] time;
        final double[] values;
        final Color color;

        AvgRenderer(double[] time, double[] values, Color color) {
            this.time = time;
            this.values = values;
            this.color = color;
        }

        @Override
        public void draw(Graphics2D g2,
                         int x0, int x1, int y0, int y1,
                         int plotWidth, int plotHeight,
                         double xMaxRounded, double yMaxRounded, double yMinRounded,
                         double dt) {

            int n = values.length;
            if (n < 2) return;

            int[] xs = new int[n];
            int[] ys = new int[n];

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
        }
    }

    // Band renderer: min/max or sd (envelope)
    protected static class BandRenderer implements SeriesRenderer {
        final double[] time;
        final double[] upper;
        final double[] lower;
        final Color fillColor;

        BandRenderer(double[] time, double[] upper, double[] lower, Color fillColor) {
            this.time = time;
            this.upper = upper;
            this.lower = lower;
            this.fillColor = fillColor;
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

    // Renderers list
    protected final List<SeriesRenderer> renderers = new ArrayList<>();

    // Scaling state
    protected double globalMin = 0;
    protected double globalMax = 1;
    protected double dt = 1;

    // Crosshair state
    protected Integer mouseX = null;
    protected Integer mouseY = null;
    protected boolean crosshairEnabled = true;
    protected Consumer<double[]> coordCallback;

    // Cached plot area
    protected int lastX0, lastX1, lastY0, lastY1;
    protected double lastXMaxRounded;
    protected double lastYMaxRounded;
    protected double lastYMinRounded;

    public AbstractPlotPanel() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                if (mx >= lastX0 && mx <= lastX1 && my >= lastY1 && my <= lastY0) {
                    mouseX = mx;
                    mouseY = my;
                } else {
                    mouseX = null;
                    mouseY = null;
                }

                if (crosshairEnabled && mouseX != null && mouseY != null) {
                    double fracX = (mouseX - lastX0) / (double)(lastX1 - lastX0);
                    double xVal = fracX * lastXMaxRounded;

                    double fracY = (lastY0 - mouseY) / (double)(lastY0 - lastY1);
                    double yVal = lastYMinRounded + fracY * (lastYMaxRounded - lastYMinRounded);

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

    public void clear() {
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
        renderers.add(new AvgRenderer(time, avg, color));
    }

    public void addMinMaxRenderer(double[] time, double[] min, double[] max, Color color, String name, Object statTag) {
        renderers.add(new BandRenderer(time, max, min, color));
    }

    public void addSDRenderer(double[] time, double[] low, double[] high, Color color, String name, Object statTag) {
        renderers.add(new BandRenderer(time, high, low, color));
    }

    // Utilities

    protected double roundUpNice(double value) {
        if (value <= 0) return 1;
        double exp = Math.pow(10, Math.floor(Math.log10(value)));
        double n = value / exp;
        double rounded;
        if (n <= 1) rounded = 1;
        else if (n <= 2) rounded = 2;
        else if (n <= 5) rounded = 5;
        else rounded = 10;
        return rounded * exp;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, w, h);

        int x0 = LEFT_INSET;
        int x1 = w - RIGHT_INSET;
        int y0 = h - BOTTOM_INSET;
        int y1 = TOP_INSET;

        lastX0 = x0;
        lastX1 = x1;
        lastY0 = y0;
        lastY1 = y1;

        int plotWidth = x1 - x0;
        int plotHeight = y0 - y1;
        if (plotWidth <= 0 || plotHeight <= 0) return;

        // Determine max length from all renderers that use arrays
        int maxLength = 0;
        for (SeriesRenderer r : renderers) {
            if (r instanceof AvgRenderer ar) {
                maxLength = Math.max(maxLength, ar.values.length);
            } else if (r instanceof BandRenderer br) {
                maxLength = Math.max(maxLength, br.upper.length);
            }
        }
        if (maxLength < 2) return;

        double yMaxRounded = roundUpNice(globalMax);
        double yMinRounded = (globalMin < 0) ? -roundUpNice(-globalMin) : 0;
        double xMax = dt * (maxLength - 1);
        double xMaxRounded = roundUpNice(xMax);

        lastXMaxRounded = xMaxRounded;
        lastYMaxRounded = yMaxRounded;
        lastYMinRounded = yMinRounded;

        FontMetrics fm = g2.getFontMetrics();

        // Gridlines
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1f));

        int yTicks = 5;
        double yRange = yMaxRounded - yMinRounded;
        double yStep = yRange / yTicks;

        for (int i = 0; i <= yTicks; i++) {
            double valueMajor = yMinRounded + i * yStep;
            double norm = (valueMajor - yMinRounded) / (yMaxRounded - yMinRounded);
            int yPixMajor = y0 - (int)Math.round(norm * plotHeight);
            g2.drawLine(x0, yPixMajor, x1, yPixMajor);

            if (i < yTicks) {
                double valueMid = valueMajor + yStep / 2.0;
                double normMid = (valueMid - yMinRounded) / (yMaxRounded - yMinRounded);
                int yPixMid = y0 - (int)Math.round(normMid * plotHeight);
                g2.drawLine(x0, yPixMid, x1, yPixMid);
            }
        }

        double[] xMajor = {0, xMaxRounded / 2, xMaxRounded};
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

        // Axes + ticks
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(AXIS_STROKE));

        g2.drawLine(x0, y0, x1, y0);
        g2.drawLine(x0, y0, x0, y1);

        for (int i = 0; i <= yTicks; i++) {
            double valueMajor = i * yStep;
            double norm = valueMajor / yMaxRounded;
            int yPixMajor = y0 - (int)Math.round(norm * plotHeight);

            g2.drawLine(x0 - 5, yPixMajor, x0, yPixMajor);

            String label = formatTick(valueMajor, yStep);
            int sw = fm.stringWidth(label);
            g2.drawString(label, x0 - 10 - sw, yPixMajor + fm.getAscent() / 2);

            if (i < yTicks) {
                double valueMid = (i + 0.5) * yStep;
                double normMid = valueMid / yMaxRounded;
                int yPixMid = y0 - (int)Math.round(normMid * plotHeight);
                g2.drawLine(x0 - 3, yPixMid, x0, yPixMid);
            }
        }

        double xStep = xMajor[1] - xMajor[0];
        for (int i = 0; i < xMajor.length; i++) {
            double xvMajor = xMajor[i];
            int xPixMajor = x0 + (int)Math.round((xvMajor / xMaxRounded) * plotWidth);

            g2.drawLine(xPixMajor, y0, xPixMajor, y0 + 5);

            String label = formatTick(xvMajor, xStep);
            int sw = fm.stringWidth(label);
            g2.drawString(label, xPixMajor - sw / 2, y0 + fm.getAscent() + 5);

            if (i < xMajor.length - 1) {
                double xvMid = (xMajor[i] + xMajor[i + 1]) / 2.0;
                int xPixMid = x0 + (int)Math.round((xvMid / xMaxRounded) * plotWidth);
                g2.drawLine(xPixMid, y0, xPixMid, y0 + 3);
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