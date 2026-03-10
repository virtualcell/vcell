package cbit.plot.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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

// -----------------------------------------------------------------------------

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

    private String formatNumber(double v) {
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
        g2.setColor(Color.white);       // background
        g2.fillRect(0, 0, w, h);

        int x0 = LEFT_INSET;            // plot area
        int x1 = w - RIGHT_INSET;
        int y0 = h - BOTTOM_INSET;
        int y1 = TOP_INSET;

        int plotWidth = x1 - x0;
        int plotHeight = y0 - y1;
        if (plotWidth <= 0 || plotHeight <= 0) {
            return;         // too small to draw
        }

        // determine max curve length
        int maxCurveLength = curves.stream()
                .mapToInt(c -> c.yRaw.length)
                .max()
                .orElse(0);

        if (maxCurveLength < 2) {
            return;     // nothing to draw
        }

        double yMaxRounded = roundUpNice(globalMax);    // rounded axis limits
        double xMax = dt * (maxCurveLength - 1);
        double xMaxRounded = roundUpNice(xMax);

        g2.setColor(Color.black);               // draw axes
        g2.setStroke(new BasicStroke(AXIS_STROKE));
        g2.drawLine(x0, y0, x1, y0);            // X axis
        g2.drawLine(x0, y0, x0, y1);            // Y axis

        FontMetrics fm = g2.getFontMetrics();   // tick labels
        int yTicks = 5;                                            // ------- Y ticks (5 ticks)
        double yStep = yMaxRounded / yTicks;
        for (int i = 0; i <= yTicks; i++) {
            double value = i * yStep;
            int yPix = y0 - (int) Math.round((value / yMaxRounded) * plotHeight);
            g2.drawLine(x0 - 5, yPix, x0, yPix);    // tick
            String label = formatNumber(value);         // label
            int sw = fm.stringWidth(label);
            g2.drawString(label, x0 - 10 - sw, yPix + fm.getAscent() / 2);
        }

        double[] xTickValues = {0, xMaxRounded / 2, xMaxRounded};   // ----- X ticks (0, mid, end)
        for (double xv : xTickValues) {
            int xPix = x0 + (int) Math.round((xv / xMaxRounded) * plotWidth);
            g2.drawLine(xPix, y0, xPix, y0 + 5);    // tick
            String label = formatNumber(xv);            // label
            int sw = fm.stringWidth(label);
            g2.drawString(label, xPix - sw / 2, y0 + fm.getAscent() + 5);
        }

        // draw curves
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
