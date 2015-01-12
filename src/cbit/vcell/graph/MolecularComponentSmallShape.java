package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;

public class MolecularComponentSmallShape extends AbstractComponentShape {
	
	static final int componentSeparation = 1;		// distance between components
	static final int componentDiameter = 8;			// diameter of the component (circle)
	
	final Graphics graphicsContext;
	
	private int xPos = 0;
	private int yPos = 0;
	private int width = componentDiameter;
	private int height = componentDiameter;
	
	private final MolecularComponent mc;
	private final Displayable owner;


	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentSmallShape(int rightPos, int y, MolecularComponent mc, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.mc = mc;
		this.graphicsContext = graphicsContext;
		xPos = rightPos-width;
		yPos = y;
	}

	public int getWidth() {
		return width;
	}

	public void paintSelf(Graphics g) {
		paintComponent(g);
	}
	
	// ----------------------------------------------------------------------------------------------
	private void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		if(mc.getComponentStateDefinitions().isEmpty()) {
			g2.setColor(componentGreen);
		} else {
			g2.setColor(componentYellow);
		}
		g2.fillOval(xPos, yPos, componentDiameter, componentDiameter);			// g.fillRect(xPos, yPos, width, height);
		g2.setColor(Color.BLACK);
		g2.drawOval(xPos, yPos, componentDiameter, componentDiameter);
	}
}
