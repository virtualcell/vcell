package cbit.plot.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.*;

public class ClusterPlotPanel extends JPanel {

    private final float lineBS_10 = 1.0f;
    private final float lineBS_15 = 1.5f;
    private final float lineBS_20 = 2.0f;

    private static class CurveData {
        // uniform time course, we compute the x on the fly
        final String name;
        final int[] y;      // scaled pixel y-values
        final Color color;
        CurveData(String name, int[] y, Color color) {
            this.name = name;
            this.y = y;
            this.color = color;
        }
    }
    private final java.util.List<CurveData> curves = new java.util.ArrayList<>();

// -----------------------------------------------------------------------------

    public void clear() {
        curves.clear();
    }
    public void addCurve(String name, int[] y, Color color) {
        curves.add(new CurveData(name, y, color));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        System.out.println("ClusterPlotPanel.paintComponent: w=" + w + ", h=" + h);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, w, h);
        g2.setStroke(new BasicStroke(lineBS_15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (CurveData curve : curves) {
            int n = curve.y.length;
            if (n < 2) continue;

            int[] x = new int[n];
            for (int i = 0; i < n; i++) {
                x[i] = (int) Math.round((i / (double)(n - 1)) * w);
            }
            g2.setColor(curve.color);
            g2.drawPolyline(x, curve.y, n);
        }
    }

}
