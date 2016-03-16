package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.JButton;

public class ZoomShape implements Icon {

	public static final int diameter = 20;
	public enum Sign { plus, minus };
	
	private Sign sign;

	public ZoomShape(Sign sign) {
		super();
		this.sign = sign;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

		if(c == null) {
			return;
		}
		if(!(c instanceof JButton)) {
			return;
		}
		JButton b = (JButton)c;
		int xPos = (b.getWidth() - diameter) / 2;
		int yPos = (b.getHeight() - diameter) / 2 + 1;
		
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color exterior;
		Color signColor;
		Color contour;
		if(b.isEnabled()) {
			exterior = SpeciesPatternLargeShape.componentGreen.darker();
			signColor = Color.green.darker().darker().darker();
			contour = SpeciesPatternLargeShape.componentGreen.darker().darker().darker();
			} else {
			exterior = Color.lightGray;
			signColor = Color.gray;
			contour = Color.gray;
		}
		
		Point2D center = new Point2D.Float(xPos+diameter/3, yPos+diameter/3);
		float radius = diameter*0.5f;
		Point2D focus = new Point2D.Float(xPos+diameter/3-1, yPos+diameter/3-1);
		float[] dist = {0.1f, 1.0f};
		Color[] colors = {Color.white, exterior};
		RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		g2.setPaint(p);
		Ellipse2D circle = new Ellipse2D.Double(xPos, yPos, diameter, diameter);
		g2.fill(circle);
		Ellipse2D circle2 = new Ellipse2D.Double(xPos-1, yPos-1, diameter, diameter);
		g2.setPaint(contour);
		g2.draw(circle2);
		
		int cx = b.getWidth() / 2;		// center of circle
		int cy = b.getHeight() / 2;
		g2.setColor(signColor);
		g2.drawLine(cx-6, cy-1, cx+4, cy-1);	// horizontal
		g2.drawLine(cx-6, cy, cx+4, cy);
		g2.drawLine(cx-6, cy+1, cx+4, cy+1);
		if(sign == Sign.plus) {					// draw a green '+' sign
			
			g2.drawLine(cx, cy-5, cx, cy+5);	// vertical
			g2.drawLine(cx-1, cy-5, cx-1, cy+5);
			g2.drawLine(cx-2, cy-5, cx-2, cy+5);
		}
		g.setColor(colorOld);
	}

	@Override
	public int getIconWidth() {
		return diameter+4;
	}
	@Override
	public int getIconHeight() {
		return diameter+4;
	}

}
