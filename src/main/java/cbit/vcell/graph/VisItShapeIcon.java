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

import cbit.vcell.graph.gui.ReactionCartoonEditorPanel;

public class VisItShapeIcon implements Icon {
	
	public enum State { enabled, disabled };

	private final State state;
	private final int diameter = 20;		// area occupied by the shape
	private final int circleDiameter = 18;	// radius of the real thing

	public VisItShapeIcon(State state) {
		super();
		this.state = state;
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

		int xx = 1;			// (diameter-circleDiameter) / 2
		int yy = 1;
		Color out2, out1, exterior, middle, interior, pole;
		if(state == State.enabled) {
			out2 = Color.red.darker();
			out1 = Color.red;
			exterior = Color.orange;
			middle = Color.yellow;
			interior = Color.green;
			pole = Color.cyan.darker();
			xx += x;
			yy += y;
		} else {
			out2 = Color.darkGray;
			out1 = Color.gray;
			exterior = Color.lightGray;
			middle = Color.gray;
			interior = Color.gray;
			pole = Color.darkGray;
			xx += x;
			yy += y;
		}
		
		Ellipse2D e = new Ellipse2D.Double(xx, yy, circleDiameter, circleDiameter);
		
		Point2D center = new Point2D.Float(xx+circleDiameter/2, yy+circleDiameter/2);
		float radius = circleDiameter+2;		//*0.7f;
		Point2D focus = new Point2D.Float(xx+circleDiameter/2+7, yy+circleDiameter/2+5);
		float[] dist = {0.1f, 0.15f, 0.25f, 0.35f, 0.45f, 0.6f};
		Color[] colors = {pole, interior, middle, exterior, out1, out2};
		RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		
		g2.setPaint(p);
		g2.fill(e);
		g.setColor(Color.gray);
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

	public static void setVisItMod(JToolBarToggleButton button) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new VisItShapeIcon(State.enabled);
		Icon iconDisabled = new VisItShapeIcon(State.disabled);
		button.setName("VisItButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconNormal);
		button.setDisabledIcon(iconDisabled);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setToolTipText("VisIt Tool");
	}

}
