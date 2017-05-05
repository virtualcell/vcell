package cbit.vcell.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.vcell.util.gui.JToolBarToggleButton;

import cbit.vcell.graph.gui.ReactionCartoonEditorPanel;


public class ReactionToolShapeIcon implements Icon {

	private enum State { normal, selected };
	private enum Mode { plain, catalyst, flux };

	private final int diameter = 20;		// area occupied by the shape
	
	private final State state;
	private final Mode mode;

	public ReactionToolShapeIcon(State state, Mode mode) {
		super();
		this.state = state;
		this.mode = mode;
	}
	
	@Override
	public void paintIcon(Component comp, Graphics g, int x, int y) {
		
		if(comp == null) {
			return;
		}
		if(!(comp instanceof JToolBarToggleButton)) {
			return;
		}
		JToolBarToggleButton button = (JToolBarToggleButton)comp;
		
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
		Stroke strokeOld = g2.getStroke();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setStroke(new BasicStroke(2f));
		Color c1, c2, c3, c4;
		float x1, x2, y1, y2;		// for the arrowhead
		int xx = x-16;
		int yy = y-15;
		int w = diameter*8;			// we'll paint a small arc from the upper left part of a very large circle
		int h = diameter*8;
		float m1 = 9f;					// the start and end coordinates of the membrane (for the flux button)
		float m2 = 14f;
		if(state == State.normal) {			// paint yellow arc
			c1 = Color.yellow.darker();
			c2 = Color.yellow;
			c3 = Color.black;
			c4 = Color.yellow.darker();
			x1 = 0;
			x2 = 0;
			y1 = 0;
			y2 = 0;
		} else {
			c1 = Color.yellow.darker();
			c2 = Color.yellow.brighter();
			c3 = Color.black;
			c4 = Color.yellow.darker().darker();
			xx += 1;
			yy += 1;
			x1 = 1;
			x2 = 1;
			y1 = 1;
			y2 = 1;
			m1 += 1.0f;
			m2 += 1.0f;
		}
		Arc2D arc;
		if(mode == Mode.catalyst) {
			arc = new Arc2D.Double(xx, yy, w, h,
					125,
					18,
					Arc2D.OPEN);
		} else {
		arc = new Arc2D.Double(xx, yy, w, h,
					126,			// starting angle
					18,				// angular extent
					Arc2D.OPEN);
		}
		int a=0;
		int b=-10;
		int c=12;
		
		g2.setStroke(new BasicStroke(4.0f));		// the wide yellow halo
		GradientPaint gp = new GradientPaint(a, b, c1, a+c, b-c, c2, true);
		g2.setPaint(gp);
		g2.draw(arc);

		if(mode == Mode.flux) {
			g2.setStroke(new BasicStroke(5.0f));	// the "membrane" line, only for flux reactions
			g2.setPaint(Color.orange.darker());
			Line2D line = new Line2D.Float(m1, m1, m2, m2);
			g2.draw(line);
		}
		if(mode == Mode.catalyst) {
			float dash[] = {6.2f, 3.6f};			// dashed black line for catalyst
			Stroke dashed = new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0);
			g2.setStroke(dashed);
			g2.setPaint(Color.black);
//			gp = new GradientPaint(a, b, c3, a+c, b-c, c4, true);
//			g2.setPaint(gp);
			g2.draw(arc);
		} else {
			g2.setStroke(new BasicStroke(2f));		// the continuous black line for plain and flux reactions
			gp = new GradientPaint(a, b, c3, a+c, b-c, c4, true);
			g2.setPaint(gp);
			g2.draw(arc);
		}
		
//		TODO: draw arrow (maybe)
//		g2.setStroke(new BasicStroke(1.4f,				// Line width
//				BasicStroke.CAP_ROUND,					// End-cap style
//				BasicStroke.JOIN_ROUND));				// Vertex join style
//		Line2D l1 = new Line2D.Float(x1, y1, x2, y2);
//		Line2D l2 = new Line2D.Float(x1, y1, x2, y2);
//		g2.draw(l1);
//		g2.draw(l2);
		
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

	public static void setPlainReactionToolMod(JToolBarToggleButton button) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new ReactionToolShapeIcon(State.normal, Mode.plain);
		Icon iconSelected = new ReactionToolShapeIcon(State.selected, Mode.plain);
		button.setName("LineButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconSelected);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setToolTipText("RX Connection Tool");
	}
	public static void setCatalystToolMod(JToolBarToggleButton button) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new ReactionToolShapeIcon(State.normal, Mode.catalyst);
		Icon iconSelected = new ReactionToolShapeIcon(State.selected, Mode.catalyst);
		button.setName("LineCatalystButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconSelected);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setToolTipText("Set a catalyst");
	}
	public static void setFluxTransportToolMod(JToolBarToggleButton button) {
		ReactionCartoonEditorPanel.setToolBarButtonSizes(button);
		Icon iconNormal = new ReactionToolShapeIcon(State.normal, Mode.flux);
		Icon iconSelected = new ReactionToolShapeIcon(State.selected, Mode.flux);
		button.setName("FluxReactionButton");
		button.setIcon(iconNormal);
		button.setSelectedIcon(iconSelected);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setToolTipText("FluxReaction Tool");
	}
	
	public static void main(String[] args) {
		try {
			Frame frame = new Frame();
			Icon iconNormal = new ReactionToolShapeIcon(State.normal, Mode.plain);
			int diameter = 20;
			int x = 50;
			int y = 80;

			JPanel panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					Graphics2D g2 = (Graphics2D)g;
					Color colorOld = g2.getColor();
					Paint paintOld = g2.getPaint();
					Stroke strokeOld = g2.getStroke();
					
					// --------------------------------------------------------------
					
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					g2.setStroke(new BasicStroke(5f));
					g2.setColor(Color.black);
					
					Color c1, c2;
					int xx = x-16;				// -16
					int yy = y-15;				// -15
					int w = diameter*8;			// 8
					int h = diameter*8;
					
					c1 = Color.black;
					c2 = Color.lightGray;
					
					Arc2D arc = new Arc2D.Double(xx, yy, w, h,		// Line2D
							0,
							300,
							Arc2D.OPEN);
					
					GradientPaint gp = new GradientPaint(50, 50+200, c1, 50+5, 50+200-5, c2, true);
					g2.setPaint(gp);
					g2.draw(arc);
					
					// -------------------------------------------------------------
					
					g2.setPaint(paintOld);

					int a = 0;
					float dash[] = {10.0f};
					Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0);
					g2.setStroke(dashed);
					g2.drawLine(10, 10+a, 150, 10+a);

					a = 10;
					float dash1[] = {5.0f};
					Stroke dashed1 = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash1, 0);
					g2.setStroke(dashed1);
					g2.drawLine(10, 10+a, 150, 10+a);

					a = 20;
					float dash2[] = {12.0f, 10.0f, 8.0f};
					Stroke dashed2 = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash2, 0);
					g2.setStroke(dashed2);
					g2.drawLine(10, 10+a, 150, 10+a);

					a = 30;
					float dash3[] = {12.0f, 10.0f, 8.0f};
					Stroke dashed3 = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash3, 50);
					g2.setStroke(dashed3);
					g2.drawLine(10, 10+a, 150, 10+a);

// ----------------------------------------------------------------------------------
					
					g2.setStroke(strokeOld);
					g2.setColor(colorOld);
					g2.setPaint(paintOld);
				}
			};
			panel.setSize(500, 400);
			frame.add("Center", panel);
			frame.setSize(panel.getSize());
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
			
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Panel");
			exception.printStackTrace(System.out);
		}
	}

}
