package cbit.vcell.graph.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

import cbit.vcell.graph.SpeciesPatternLargeShape;

public class ZoomShapeIcon implements Icon {

	public static final int Diameter = 20;
	public enum Sign { plus, minus };
	private enum State { normal, pressed };
	
	private final Sign sign;
	private final State state;
	private final int diameter;

	// one should invoke the static method setZoomMod below for a button using this icon
	// to make it perform and paint properly
	//
	public ZoomShapeIcon(Sign sign, State state) {
		super();
		this.sign = sign;
		this.state = state;
		if(sign == Sign.plus) {				// for unknown reason people want them the same size
			this.diameter = Diameter;
		} else {
			this.diameter = Diameter;
//			this.diameter = Diameter-6;		// personally I prefer the zoom out to be smaller
		}
	}
	public ZoomShapeIcon(Sign sign, State state, int diameter) {
		super();
		this.sign = sign;
		this.state = state;
		this.diameter = diameter;
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
		Paint paintOld = g2.getPaint();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color interior = Color.white;
		Color exterior;
		Color signColor;
		Color contour;
		
		Color c1, c2;
		if(sign == Sign.plus) {
			c1 = SpeciesPatternLargeShape.componentGreen;
			c2 = Color.green;
		} else {
			c1 = SpeciesPatternLargeShape.componentBad;
			c2 = Color.red;
		}
		
		if(b.isEnabled()) {
			if(state == State.normal) {
				exterior = c1.darker();
				signColor = c2.darker().darker().darker();
			} else {	// same colors, brighter when button is pressed
				exterior = c1;
				signColor = c2.darker().darker();
			}
			contour = c1.darker().darker().darker();
			
		} else {	// disabled
			exterior = Color.lightGray;
			signColor = Color.gray;
			contour = Color.gray;
		}
		
		Point2D center = new Point2D.Float(xPos+diameter/3, yPos+diameter/3);
		float radius = diameter*0.5f;
		Point2D focus = new Point2D.Float(xPos+diameter/3-1, yPos+diameter/3-1);
		float[] dist = {0.1f, 1.0f};
		Color[] colors = {interior, exterior};
		RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		g2.setPaint(p);
		Ellipse2D circle = new Ellipse2D.Double(xPos, yPos, diameter, diameter);
		g2.fill(circle);
		Ellipse2D circle2 = new Ellipse2D.Double(xPos-1, yPos-1, diameter, diameter);
		g2.setPaint(contour);
		g2.draw(circle2);
		
		// TODO: use diameter instead of hardcoded numbers for the horizontal and vertical lines
		
		int i = diameter / 4;	// offset left and right from center (for the vertical and orizontal lines)
		
		int cx = b.getWidth() / 2;		// center of circle
		int cy = b.getHeight() / 2;
		g2.setColor(signColor);
		g2.drawLine(cx-i-1, cy-1, cx+i-1, cy-1);	// horizontal bar of the '-' or '+' signs
		g2.drawLine(cx-i-1, cy, cx+i-1, cy);
		g2.drawLine(cx-i-1, cy+1, cx+i-1, cy+1);
		if(sign == Sign.plus) {					// draw the vertical bar of the '+' sign
			
			g2.drawLine(cx, cy-i, cx, cy+i);	// vertical
			g2.drawLine(cx-1, cy-i, cx-1, cy+i);
			g2.drawLine(cx-2, cy-i, cx-2, cy+i);
		}
		g2.setColor(colorOld);
		g2.setPaint(paintOld);
	}

	@Override
	public int getIconWidth() {
		return diameter+4;
	}
	@Override
	public int getIconHeight() {
		return diameter+4;
	}
	
	// zoom in / out in the Applications / Specifications / Network viewers
	public static void setZoomMod(JButton button, Sign sign) {
		Icon iconNormal = new ZoomShapeIcon(sign, State.normal);
		Icon iconPressed = new ZoomShapeIcon(sign, State.pressed);
		button.setIcon(iconNormal);
		button.setPressedIcon(iconPressed);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		if(sign == Sign.plus) {
			button.setToolTipText("Zoom In");
		} else {
			button.setToolTipText("Zoom Out");
		}
	}
	// zoom in / out in the reaction diagram toolbar
	public static void setZoomToolbarMod(JButton button, Sign sign) {
		Icon iconNormal = new ZoomShapeIcon(sign, State.normal, 16);
		Icon iconPressed = new ZoomShapeIcon(sign, State.pressed, 16);
		button.setIcon(iconNormal);
		button.setPressedIcon(iconPressed);
		button.setFocusPainted(false);
		button.setFocusable(false);
		if(sign == Sign.plus) {
			button.setToolTipText("Zoom In");
		} else {
			button.setToolTipText("Zoom Out");
		}
	}
	// zoom in / out button in the OverlayEditorPane for vCell and vFrap 
	public static void setZoomOverlayEditorMod(JButton button, Sign sign) {
		Icon iconNormal = new ZoomShapeIcon(sign, State.normal, 16);
		Icon iconPressed = new ZoomShapeIcon(sign, State.pressed, 18);
		button.setIcon(iconNormal);
		button.setPressedIcon(iconPressed);
//		button.setFocusPainted(false);
//		button.setFocusable(false);
		if(sign == Sign.plus) {
			button.setToolTipText("Zoom In");
		} else {
			button.setToolTipText("Zoom Out");
		}
	}
	

}
