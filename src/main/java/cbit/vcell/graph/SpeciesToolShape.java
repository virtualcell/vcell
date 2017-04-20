package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.Icon;

import org.vcell.util.gui.JToolBarToggleButton;

public class SpeciesToolShape implements Icon {
	
	private enum State { normal, selected };

	private final State state;
	private final int diameter = 20;		// area occupied by the shape
	private final int circleDiameter = 14;	// radius of the real thing

	public SpeciesToolShape(State state) {
		super();
		this.state = state;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

		if(c == null) {
			return;
		}
		if(!(c instanceof JToolBarToggleButton)) {
			return;
		}
		JToolBarToggleButton b = (JToolBarToggleButton)c;
		
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
		Stroke strokeOld = g2.getStroke();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int xx = 2;			// (diameter-circleDiameter) / 2
		int yy = 2;
		Color exterior, interior;
		if(state == State.normal) {
			exterior = Color.green.darker().darker();
			interior = Color.white;
			xx += x;
			yy += y;
		} else {
			exterior = Color.green.darker();
			interior = Color.white;
			xx += x+1;		// button moves a little bit to simulate 3D pressing of a button
			yy += y+1;
		}
		
		Ellipse2D e = new Ellipse2D.Double(xx, yy, circleDiameter, circleDiameter);
		
		Point2D center = new Point2D.Float(xx+circleDiameter/2, yy+circleDiameter/2);
		float radius = circleDiameter*0.5f;
		Point2D focus = new Point2D.Float(xx+circleDiameter/2-2, yy+circleDiameter/2-2);
		float[] dist = {0.1f, 1.0f};
		Color[] colors = {interior, exterior};
		RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		
		g2.setPaint(p);
		g2.fill(e);
		g.setColor(Color.black);
		g2.draw(e);

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

	public static void setSpeciesToolMod(JToolBarToggleButton button) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new SpeciesToolShape(State.normal);
		Icon iconSelected = new SpeciesToolShape(State.selected);
		button.setName("SpeciesButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconSelected);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setToolTipText("Species Tool");
	}

}
