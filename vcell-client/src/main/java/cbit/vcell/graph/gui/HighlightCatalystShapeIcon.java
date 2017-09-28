package cbit.vcell.graph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.Icon;

import org.vcell.util.gui.JToolBarToggleButton;

import cbit.vcell.graph.SpeciesPatternLargeShape;

public class HighlightCatalystShapeIcon implements Icon {

	private enum State { normal, selected };

	private final State state;
	private final int diameter;

	public HighlightCatalystShapeIcon(State state) {
		super();
		this.state = state;
		this.diameter = 20;		// kept for historic reasons
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if(c == null) {
			return;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
		Stroke strokeOld = g2.getStroke();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color c1, c2, c3;
		int xx, yy;
		
		
		if(state == State.normal) {
			c1 = Color.green.darker();
			c2 = Color.green.darker();
			c3 = Color.black;
			xx = x+1;
			yy = y+1;
		} else {		// button pressed
			c1 = Color.green.darker();
			c2 = Color.magenta.darker();
			c3 = Color.black;
			xx = x+2;
			yy = y+2;
		}
		double w = diameter/2-2;
		double h = diameter/2-2;

		g2.setStroke(new BasicStroke(1.2f));
		Ellipse2D e1 = new Ellipse2D.Double(xx, yy, w, h);
		g2.setPaint(c3);
		g2.draw(e1);
		g2.setPaint(c1);
		g2.fill(e1);

		Ellipse2D e2 = new Ellipse2D.Double(xx+9, yy+8, w, h);
		g2.setPaint(c3);
		g2.draw(e2);
		g2.setPaint(c2);
		g2.fill(e2);

		g2.setStroke(new BasicStroke(1.2f));
		g2.setPaint(c3);
		Line2D line = new Line2D.Double(xx+w/2, yy+h+1, xx+w/2, yy+h+7);
		g2.draw(line);
		g2.setStroke(new BasicStroke(1.6f));
		line = new Line2D.Double(xx+9+w/2, yy+1, xx+9+w/2, yy+2);
		g2.draw(line);
		line = new Line2D.Double(xx+9+w/2, yy+6, xx+9+w/2, yy+7);
		g2.draw(line);
	
		g2.setStroke(strokeOld);
		g2.setColor(colorOld);
		g2.setPaint(paintOld);
	}

	@Override
	public int getIconWidth() {
		return diameter;
	}
	@Override
	public int getIconHeight() {
		return diameter;
	}
	
	public static void setStructureToolMod(JToolBarToggleButton button) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new HighlightCatalystShapeIcon(State.normal);
		Icon iconSelected = new HighlightCatalystShapeIcon(State.selected);
		button.setName("HighlightCatalystButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconSelected);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setToolTipText("Highlight catalysts");
	}
}
