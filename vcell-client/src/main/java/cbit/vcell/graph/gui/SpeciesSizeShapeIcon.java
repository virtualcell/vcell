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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

import org.vcell.util.gui.JToolBarToggleButton;

import cbit.vcell.graph.SpeciesPatternLargeShape;

public class SpeciesSizeShapeIcon implements Icon {

	private enum State { normal, selected };
	public enum Kind { equal, weight, length };

	private final State state;
	private final Kind kind;
	private final int diameter;	// keep for historic reasons, we have no circle in this implementation

	public SpeciesSizeShapeIcon(State state, Kind kind) {
		super();
		this.state = state;
		this.kind = kind;
		this.diameter = 20;		// recommended value
	}
	
	public class DoubleArrow extends Path2D.Double {
		public DoubleArrow(int x, int y, double h) {
			moveTo(x, y);
			lineTo(x, y+h);
			moveTo(x, y);
			lineTo(x-3, y+4);
			moveTo(x, y);
			lineTo(x+3, y+4);
			moveTo(x, y+h);
			lineTo(x-3, y+h-4);
			moveTo(x, y+h);
			lineTo(x+3, y+h-4);
		}
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
		int offset;
		if(state == State.normal) {
			offset = 0;
		} else {
			offset = 1;
		}
		int xx = x+1+offset;
		int yy = y+1+offset;
		double h = diameter-5;

		g2.setStroke(new BasicStroke(1.6f));
		g2.setPaint(Color.darkGray);
		DoubleArrow arrow = new DoubleArrow(xx+13, yy, h);
		g2.draw(arrow);

		if(kind == Kind.equal) {
			g2.setStroke(new BasicStroke(2.4f));
			g2.setPaint(Color.magenta.darker().darker());
			Line2D upper = new Line2D.Double(xx, yy+9, xx+7, yy+9);
			g2.draw(upper);
			Line2D lower = new Line2D.Double(xx, yy+13, xx+7, yy+13);
			g2.draw(lower);
		} else if(kind == Kind.weight) {
			g2.setStroke(new BasicStroke(1.2f));
			RoundRectangle2D rr = new RoundRectangle2D.Double(xx, yy+9, 7, 7, 2, 2);
			g2.setPaint(Color.yellow);
			g2.fill(rr);
			g2.setPaint(Color.gray);
			g2.draw(rr);
		} else if(kind == Kind.length) {
			g2.setStroke(new BasicStroke(1.5f));
			g2.setPaint(Color.blue.darker());
			Line2D upper = new Line2D.Double(xx-2, yy+9, xx+1, yy+9);
			g2.draw(upper);
			Line2D mid = new Line2D.Double(xx-2, yy+12, xx+4, yy+12);
			g2.draw(mid);
			Line2D lower = new Line2D.Double(xx-2, yy+15, xx+7, yy+15);
			g2.draw(lower);
		}
	
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
	
	public static void setSpeciesSizeShapeMod(JToolBarToggleButton button, Kind kind) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new SpeciesSizeShapeIcon(State.normal, kind);
		Icon iconSelected = new SpeciesSizeShapeIcon(State.selected, kind);
		button.setName("SpeciesSizeButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconSelected);
		button.setFocusPainted(false);
		button.setFocusable(false);
		switch(kind) {
		case equal:
			button.setToolTipText("Equal size for all Species");
			break;
		case weight:
			button.setToolTipText("Size based on number of reactions where Species is present");
			break;
		case length:
			button.setToolTipText("Size based on number of molecules inside the Species");
			break;
		default:
			break;
		}
	}
}
