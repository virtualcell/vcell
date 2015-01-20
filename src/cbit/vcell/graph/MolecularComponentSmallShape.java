package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;

public class MolecularComponentSmallShape extends AbstractComponentShape {
	
	static final int componentSeparation = 1;		// distance between components
	static final int componentDiameter = 7;			// diameter of the component (circle)
	
	final Graphics graphicsContext;
	
	private int xPos = 0;
	private int yPos = 0;
	private int width = componentDiameter;
	private int height = componentDiameter;
	
	private final MolecularComponent mc;
	private final MolecularComponentPattern mcp;
	private final Displayable owner;


	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentSmallShape(int rightPos, int y, MolecularComponent mc, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.mcp = null;
		this.mc = mc;
		this.graphicsContext = graphicsContext;
		xPos = rightPos-width;
		yPos = y;
	}
	public MolecularComponentSmallShape(int rightPos, int y, MolecularComponentPattern mcp, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.mcp = mcp;
		this.mc = mcp.getMolecularComponent();
		this.graphicsContext = graphicsContext;
		xPos = rightPos-width;
		yPos = y;
	}

	public MolecularComponentPattern getMolecularComponentPattern() {
		return mcp;
	}
	
	public int getX(){
		return xPos;
	}
	public int getY(){
		return yPos;
	}
	public int getWidth(){
		return width;
	} 
	public int getHeight(){
		return height;
	}
	
	private Color setComponentColor() {

		Color componentColor = componentBad;
		if(mc.getComponentStateDefinitions().isEmpty()) {
			componentColor = componentGreen;
		} else {
			componentColor = componentYellow;
		}
		if(isHidden(owner, mcp)) {
			componentColor = componentHidden;
		}
		if(hasIssues(owner, mcp, mc)) {
			componentColor = componentBad;
		}
		return componentColor;
	}


	public void paintSelf(Graphics g) {
		paintComponent(g);
	}
	
	// ----------------------------------------------------------------------------------------------
	private void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Color componentColor = setComponentColor();
		
		g2.setColor(componentColor);
		g2.fillOval(xPos, yPos, componentDiameter, componentDiameter);			// g.fillRect(xPos, yPos, width, height);
		g2.setColor(Color.BLACK);
		g2.drawOval(xPos, yPos, componentDiameter, componentDiameter);
	}
}
