package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;

public class MolecularComponentLargeShape {
	
	static final int componentSeparation = 4;		// distance between components
	static final int componentDiameter = 17;		// diameter of the component (circle 
	
	private int xPos = 0;
	private int yPos = 0;
	private int width = componentDiameter;			// with no letters inside, it's an empty circle
	private int height = componentDiameter;
	
	private int textWidth = 0;			// we add this to componentDiameter to obtain the final width of the eclipse
	private final String name;
	private final MolecularComponent mc;


	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponent mc) {
		this.mc = mc;
		if(mc.getName().length() > 3) {
			name = mc.getName().substring(0,3) + "..";
		} else {
			name = mc.getName();
		}
		textWidth = 5*(name.length()-1);	// we provide space for the first letter from componentDiameter
		width = width + textWidth;
		xPos = rightPos-width;
		yPos = y;
	}

	public static void paintComponents(Graphics g, SpeciesTypeLargeShape parent) {
		int size = parent.getSpeciesType().getComponentList().size();
		int fixedPart = parent.getX() + parent.getWidth();
		int offsetFromRight = 10;
		for(int i=size-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = parent.getY() + parent.getHeight() - componentDiameter;
			// we draw the components from left to right, so we start with the last
			MolecularComponent mc = parent.getSpeciesType().getComponentList().get(i);
			MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(rightPos, y, mc);
			offsetFromRight += mlcls.getWidth() + componentSeparation;
			mlcls.paintSelf(g);
		}
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
		g.setColor(Color.YELLOW);
		
		g.fillOval(xPos, yPos, componentDiameter + textWidth, componentDiameter);			// g.fillRect(xPos, yPos, width, height);
		g.setColor(Color.BLACK);
		g.drawOval(xPos, yPos, componentDiameter + textWidth, componentDiameter);
		
		Font fontOld = g.getFont();
		Color colorOld = g.getColor();
		Font font = fontOld.deriveFont((float) (componentDiameter*3/5)).deriveFont(Font.BOLD);
		g.setFont(font);
		g.setColor(Color.black);
		g2.drawString(name, xPos+1+componentDiameter/3, yPos+4+componentDiameter/2);
		g.setFont(fontOld);
		g.setColor(colorOld);
	}

}
