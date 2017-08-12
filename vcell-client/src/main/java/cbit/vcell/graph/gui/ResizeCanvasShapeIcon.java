package cbit.vcell.graph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.JButton;

import cbit.vcell.graph.SpeciesPatternLargeShape;

public class ResizeCanvasShapeIcon implements Icon {

	public static final int Side = 16;
	public enum Sign { expand, shrink };
	private enum State { normal, pressed };

	private final Sign sign;
	private final State state;
	private final int side;

	public ResizeCanvasShapeIcon(Sign sign, State state) {
		super();
		this.sign = sign;
		this.state = state;
		this.side = Side;
	}
	public ResizeCanvasShapeIcon(Sign sign, State state, int side) {
		super();
		this.sign = sign;
		this.state = state;
		this.side = side;
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
		int cx = b.getWidth() / 2;	// button center
		int cy = b.getHeight() / 2;
		
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
		Stroke strokeOld = g2.getStroke();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color interior;			// the nock (base end, towards the fletching) of the arrow shaft
		Color exterior;			// the spine (tip end, connecting to the arrow head) of the shaft
		Color tip;				// the arrow head
		Color[] colors;
		float[] dist;
		
		Color c1, c2, c3;
		if(sign == Sign.expand) {
			c1 = SpeciesPatternLargeShape.componentGreen.darker();
			c2 = SpeciesPatternLargeShape.componentGreen.darker().darker();
			c3 = Color.green.darker().darker();
		} else {
			c1 = SpeciesPatternLargeShape.componentBad.darker();
			c2 = SpeciesPatternLargeShape.componentBad.darker().darker();
			c3 = Color.red.darker().darker();
		}
		
		Point2D center = new Point2D.Float(cx, cy);
		float radius = side/2+1;
		Point2D focus = new Point2D.Float(cx, cy);		// white mapped in the center
		
		if(b.isEnabled()) {
			if(state == State.normal) {
				interior = c1;				// Color.cyan;
				exterior = c2;				// Color.blue;
				tip = c3.darker();					// Color.blue.darker();
			} else {	// pressed! same colors, brighter when button is pressed
				interior = c1.brighter();				// Color.cyan.brighter();
				exterior = c2.brighter();				// Color.blue.brighter();
				tip = c3.brighter();					// Color.blue;
			}
			
		} else {	// disabled - uniform gray, regardless of pressed
			interior = Color.lightGray;
			exterior = Color.gray;
			tip = Color.gray;
		}
		
		if(sign == Sign.expand) {
			colors = new Color[] {interior, exterior};
			dist = new float[] {0.2f, 1.0f};
		} else {		// arrow points the other way, so the arrow colors are inverted
			colors = new Color[] {exterior, interior};
			dist = new float[] {0.9f, 1.0f};
		}

		RadialGradientPaint paint = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		g2.setPaint(paint);

		int o;		// starting offset (how far away from center starts the shaft of the arrow)
		int d;		// length of the 2 "wings" of the arrow head)
		int xx = side/2;
		int yy = side/2;
		float strokeWidth;
		if(sign == Sign.expand) {
			o = 2;
			d = 4;
			strokeWidth = 2.0f;
		} else {	// shrink
			o = 2;
			d = 4;
//			xx++;
//			yy++;
			strokeWidth = 1.6f;
		}
		g2.setStroke(new BasicStroke(strokeWidth,			// Line width
				BasicStroke.CAP_ROUND,						// End-cap style
				BasicStroke.JOIN_ROUND));					// Vertex join style

		g2.drawLine(cx-o, cy-o, cx-xx, cy-yy);			// upper left
		g2.drawLine(cx+o, cy-o, cx+xx, cy-yy);			// upper right
		
		g2.drawLine(cx-o, cy+o, cx-xx, cy+yy);			// lower left
		g2.drawLine(cx+o, cy+o, cx+xx, cy+yy);			// lower right
		
		g2.setPaint(paintOld);
		g2.setColor(tip);
		
		if(sign == Sign.expand) {
			g2.drawLine(cx-xx, cy-yy, cx-xx+d, cy-yy);			// upper left
			g2.drawLine(cx-xx, cy-yy, cx-xx, cy-yy+d);
	
			g2.drawLine(cx+xx, cy-yy, cx+xx-d, cy-yy);			// upper right
			g2.drawLine(cx+xx, cy-yy, cx+xx, cy-yy+d);
			
			g2.drawLine(cx-xx, cy+yy, cx-xx+d, cy+yy);			// lower left
			g2.drawLine(cx-xx, cy+yy, cx-xx, cy+yy-d);
	
			g2.drawLine(cx+xx, cy+yy, cx+xx-d, cy+yy);			// lower right
			g2.drawLine(cx+xx, cy+yy, cx+xx, cy+yy-d);
		} else {
			g2.drawLine(cx-o, cy-o, cx-o-d, cy-o);			// upper left
			g2.drawLine(cx-o, cy-o, cx-o, cy-o-d);
	
			g2.drawLine(cx+o, cy-o, cx+o+d, cy-o);			// upper right
			g2.drawLine(cx+o, cy-o, cx+o, cy-o-d);
			
			g2.drawLine(cx-o, cy+o, cx-o-d, cy+o);			// lower left
			g2.drawLine(cx-o, cy+o, cx-o, cy+o+d);
	
			g2.drawLine(cx+o, cy+o, cx+o+d, cy+o);			// lower right
			g2.drawLine(cx+o, cy+o, cx+o, cy+o+d);
		}
		g2.setStroke(strokeOld);
		g2.setColor(colorOld);
		g2.setPaint(paintOld);
	}

	@Override
	public int getIconWidth() {
		return side;
	}
	@Override
	public int getIconHeight() {
		return side;
	}
	
	public static void setCanvasNormalMod(JButton button, Sign sign) {
		Icon iconNormal = new ResizeCanvasShapeIcon(sign, State.normal);
		Icon iconPressed = new ResizeCanvasShapeIcon(sign, State.pressed);
		button.setIcon(iconNormal);
		button.setPressedIcon(iconPressed);
		button.setFocusPainted(false);
		button.setFocusable(false);
		if(sign == Sign.expand) {
			button.setToolTipText("Expand Canvas");
		} else {
			button.setToolTipText("Shrink Canvas");
		}
	}

	
}
