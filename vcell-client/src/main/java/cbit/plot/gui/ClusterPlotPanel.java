package cbit.plot.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.*;

public class ClusterPlotPanel extends JPanel {

    private static final int LEFT_INSET = 50;           // insets for axes and labels
    private static final int RIGHT_INSET = 20;
    private static final int TOP_INSET = 20;
    private static final int BOTTOM_INSET = 30;

    private static final float AXIS_STROKE = 1.0f;      // stroke widths
    private static final float CURVE_STROKE = 1.5f;

    private static class CurveData {
        final String name;
        final double[] yRaw;
        final Color color;
        CurveData(String name, double[] yRaw, Color color) {
            this.name = name;
            this.yRaw = yRaw;
            this.color = color;
        }
    }
    private final List<CurveData> curves = new ArrayList<>();
    private double globalMin = 0;
    private double globalMax = 1;
    private double dt = 1;

    private Integer mouseX = null;      // mouse crosshair
    private Integer mouseY = null;
    private int lastX0, lastX1, lastY0, lastY1;
    private boolean crosshairEnabled = true;
    private Consumer<double[]> coordCallback;   // parent supplies this
    private double lastXMaxRounded;
    private double lastYMaxRounded;


    public ClusterPlotPanel() {

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                // Check if inside last-known plot area
                if (mx >= lastX0 && mx <= lastX1 && my >= lastY1 && my <= lastY0) {
                    mouseX = mx;
                    mouseY = my;
                } else {
                    mouseX = null;
                    mouseY = null;
                }
                if (crosshairEnabled && mouseX != null && mouseY != null) {
                    double xVal = (mouseX - lastX0) * lastXMaxRounded / (lastX1 - lastX0);
                    double yVal = (lastY0 - mouseY) * lastYMaxRounded / (lastY0 - lastY1);

                    if (coordCallback != null) {
                        coordCallback.accept(new double[]{xVal, yVal});
                    }
                } else {
                    if (coordCallback != null) {
                        coordCallback.accept(null);   // clear
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

// -----------------------------------------------------------------------------

    public void setCrosshairEnabled(boolean enabled) {
        this.crosshairEnabled = enabled;
    }
    public void setCoordinateCallback(Consumer<double[]> cb) {
        this.coordCallback = cb;
    }

    public void clear() {
        curves.clear();
    }
    public void addCurve(String name, double[] yRaw, Color color) {
        curves.add(new CurveData(name, yRaw, color));
    }
    public void setGlobalMinMax(double min, double max) {
        this.globalMin = min;
        this.globalMax = max;
    }
    public void setDt(double dt) {
        this.dt = dt;
    }
    private double roundUpNice(double value) {
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

    public static String formatNumber(double v) {
        if (v == 0) return "0";

        double abs = Math.abs(v);
        if (abs >= 1) {
            return String.format("%.0f", v);      // 0, 10, 20, 30, 40
        } else if (abs >= 0.01) {
            return String.format("%.3f", v);      // 0.123
        } else if (abs >= 0.0001) {
            return String.format("%.5f", v);      // 0.00012
        } else {
            return String.format("%.2E", v);      // 2.0E-5
        }
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

        // store plot area for mouse listeners
        lastX0 = x0;
        lastX1 = x1;
        lastY0 = y0;
        lastY1 = y1;

        int plotWidth = x1 - x0;
        int plotHeight = y0 - y1;
        if (plotWidth <= 0 || plotHeight <= 0) {
            return;
        }

        int maxCurveLength = curves.stream()
                .mapToInt(c -> c.yRaw.length)
                .max()
                .orElse(0);

        if (maxCurveLength < 2) {
            return;
        }

        double yMaxRounded = roundUpNice(globalMax);
        double xMax = dt * (maxCurveLength - 1);
        double xMaxRounded = roundUpNice(xMax);
        lastXMaxRounded = xMaxRounded;
        lastYMaxRounded = yMaxRounded;
        FontMetrics fm = g2.getFontMetrics();

        // ============================================================
        // GRIDLINES (major + mid)
        // ============================================================
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1f));

        // Y gridlines
        int yTicks = 5;
        double yStep = yMaxRounded / yTicks;

        for (int i = 0; i <= yTicks; i++) {
            double valueMajor = i * yStep;
            int yPixMajor = y0 - (int) Math.round((valueMajor / yMaxRounded) * plotHeight);

            g2.drawLine(x0, yPixMajor, x1, yPixMajor);

            if (i < yTicks) {
                double valueMid = (i + 0.5) * yStep;
                int yPixMid = y0 - (int) Math.round((valueMid / yMaxRounded) * plotHeight);
                g2.drawLine(x0, yPixMid, x1, yPixMid);
            }
        }

        // X gridlines
        double[] xMajor = {0, xMaxRounded / 2, xMaxRounded};

        for (int i = 0; i < xMajor.length; i++) {
            double xvMajor = xMajor[i];
            int xPixMajor = x0 + (int) Math.round((xvMajor / xMaxRounded) * plotWidth);

            g2.drawLine(xPixMajor, y1, xPixMajor, y0);

            if (i < xMajor.length - 1) {
                double xvMid = (xMajor[i] + xMajor[i + 1]) / 2.0;
                int xPixMid = x0 + (int) Math.round((xvMid / xMaxRounded) * plotWidth);
                g2.drawLine(xPixMid, y1, xPixMid, y0);
            }
        }

        // ============================================================
        // AXES + TICKS
        // ============================================================
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(AXIS_STROKE));

        g2.drawLine(x0, y0, x1, y0);
        g2.drawLine(x0, y0, x0, y1);

        // Y ticks
        for (int i = 0; i <= yTicks; i++) {
            double valueMajor = i * yStep;
            int yPixMajor = y0 - (int) Math.round((valueMajor / yMaxRounded) * plotHeight);

            g2.drawLine(x0 - 5, yPixMajor, x0, yPixMajor);

            String label = formatNumber(valueMajor);
            int sw = fm.stringWidth(label);
            g2.drawString(label, x0 - 10 - sw, yPixMajor + fm.getAscent() / 2);

            if (i < yTicks) {
                double valueMid = (i + 0.5) * yStep;
                int yPixMid = y0 - (int) Math.round((valueMid / yMaxRounded) * plotHeight);
                g2.drawLine(x0 - 3, yPixMid, x0, yPixMid);
            }
        }

        // X ticks
        for (int i = 0; i < xMajor.length; i++) {
            double xvMajor = xMajor[i];
            int xPixMajor = x0 + (int) Math.round((xvMajor / xMaxRounded) * plotWidth);

            g2.drawLine(xPixMajor, y0, xPixMajor, y0 + 5);

            String label = formatNumber(xvMajor);
            int sw = fm.stringWidth(label);
            g2.drawString(label, xPixMajor - sw / 2, y0 + fm.getAscent() + 5);

            if (i < xMajor.length - 1) {
                double xvMid = (xMajor[i] + xMajor[i + 1]) / 2.0;
                int xPixMid = x0 + (int) Math.round((xvMid / xMaxRounded) * plotWidth);
                g2.drawLine(xPixMid, y0, xPixMid, y0 + 3);
            }
        }

        // ============================================================
        // CROSSHAIR (drawn after gridlines, before curves)
        // ============================================================
        if (crosshairEnabled && mouseX != null && mouseY != null) {
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(mouseX, y1, mouseX, y0);
            g2.drawLine(x0, mouseY, x1, mouseY);
        }

        // ============================================================
        // CURVES
        // ============================================================
        g2.setStroke(new BasicStroke(CURVE_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (CurveData curve : curves) {
            int n = curve.yRaw.length;
            if (n < 2) continue;

            int[] x = new int[n];
            int[] y = new int[n];

            for (int i = 0; i < n; i++) {
                double t = i * dt;
                x[i] = x0 + (int) Math.round((t / xMaxRounded) * plotWidth);

                double norm = curve.yRaw[i] / yMaxRounded;
                y[i] = y0 - (int) Math.round(norm * plotHeight);
            }

            g2.setColor(curve.color);
            g2.drawPolyline(x, y, n);
        }
    }

}
