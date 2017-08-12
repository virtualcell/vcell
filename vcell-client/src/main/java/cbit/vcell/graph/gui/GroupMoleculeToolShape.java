package cbit.vcell.graph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import org.vcell.util.gui.JToolBarToggleButton;

import cbit.vcell.graph.AbstractComponentShape;

public class GroupMoleculeToolShape implements Icon {
	
	private enum State { normal, selected, disabled };

	private final State state;
	private final int diameter = 20;	// area occupied by the shape

	public GroupMoleculeToolShape(State state) {
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

		Color c1;
		Color c2;
		Color c3;
		Color c4;
		int init;	// offset from upper left corner
		switch(state) {
		case normal:
			init = 0;
			c1 = Color.red.darker();
			c2 = Color.darkGray;
			c3 = Color.gray;
			c4 = AbstractComponentShape.componentMediumPalePink;
			break;
		case selected:
			init = 1;
			c1 = Color.red.darker().darker();
			c2 = Color.darkGray;
			c3 = Color.gray;
			c4 = AbstractComponentShape.componentPalePink;
			break;
		case disabled:
		default:
			init = 0;
			c1 = Color.lightGray;
			c2 = Color.lightGray;
			c3 = Color.lightGray;
			c4 = AbstractComponentShape.componentMediumPalePink;
			break;
		}
		
		g2.setStroke(new BasicStroke(1.2f));
		g2.setPaint(c2);
		final int delta = 15;	// the large square
		int xx = x+init+1;
		int yy = y+init+1;
		Rectangle2D rect = new Rectangle2D.Double(xx, yy, delta+1, delta);
		g2.draw(rect);

		g2.setStroke(new BasicStroke(0.8f));
		final int delta2 = 7;
		xx += 5;				// the lower small rectangle
		yy += 5;
		rect = new Rectangle2D.Double(xx, yy, delta2+1, delta2);
		g2.setPaint(c3);
		g2.draw(rect);

		xx -= 2;				// the upper small rectangle
		yy -= 2;
		rect = new Rectangle2D.Double(xx, yy, delta2+1, delta2);
		g2.setPaint(c4);
		g2.fill(rect);			// overwrite some of the smaller rectangle
		g2.setPaint(c3);
		g2.draw(rect);

// ---------------------------------------------------------------------
		int m = 3;
		g2.setStroke(new BasicStroke(1.0f));	// upper left corner small red square
		g2.setPaint(c1);
		xx = x+init;
		yy = y+init;
		rect = new Rectangle2D.Double(xx, yy, m, m);
		g2.fill(rect);	// g2.draw(rect);

		xx = x+init+delta+1;
		yy = y+init;
		rect = new Rectangle2D.Double(xx, yy, m, m);
		g2.fill(rect);

		xx = x+init;
		yy = y+init+delta;
		rect = new Rectangle2D.Double(xx, yy, m, m);
		g2.fill(rect);

		xx = x+init+delta+1;
		yy = y+init+delta;
		rect = new Rectangle2D.Double(xx, yy, m, m);
		g2.fill(rect);

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

	public static void setMod(JToolBarToggleButton button) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new GroupMoleculeToolShape(State.normal);
		Icon iconSelected = new GroupMoleculeToolShape(State.selected);
		Icon iconDisabled = new GroupMoleculeToolShape(State.disabled);
		button.setName("GroupMoleculeButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconSelected);
		button.setDisabledIcon(iconDisabled);
		button.setDisabledSelectedIcon(iconDisabled);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setToolTipText("Show rule Participants grouped by Molecules");
	}

}
