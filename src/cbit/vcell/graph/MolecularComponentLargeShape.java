package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;

public class MolecularComponentLargeShape {
	
	static final int componentSeparation = 4;		// distance between components
	static final int componentDiameter = 17;		// diameter of the component (circle 
	
	final Graphics graphicsContext;
	
	private boolean pattern;					// we draw component or we draw component pattern (and perhaps a bond)
	private int xPos = 0;
	private int yPos = 0;
	private int width = componentDiameter;		// with no letters inside, it's an empty circle
	private int height = componentDiameter;
	
	private int textWidth = 0;			// we add this to componentDiameter to obtain the final width of the eclipse
	private final String displayName;
	private final MolecularComponent mc;
	private final MolecularComponentPattern mcp;


	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponent mc, Graphics graphicsContext) {
		this.pattern = false;
		this.mcp = null;
		this.mc = mc;
		this.graphicsContext = graphicsContext;
		displayName = adjustForSize(mc.getDisplayName());
		
		String longestName = "";	// we find the longest State name
		for(int i=0; i<mc.getComponentStateDefinitions().size(); i++) {
			String stateName = mc.getComponentStateDefinitions().get(i).getDisplayName();
			stateName = adjustForSize(stateName);
			longestName = stateName.length() > longestName.length() ? stateName : longestName;
		}
		// we reserve enough space for component name or state name, whichever is longer
		// TODO: this is not an exact science because so far we checked just for the number of characters
		longestName = displayName.length() > longestName.length() ? displayName : longestName;
		// there's already space enough in the circle if the component name is 1 letter only
		// we add the +2 to slightly adjust width if that single letter is long (like "m")
		textWidth = getStringWidth(longestName.substring(1)) + 5;	// we provide space for the component name and a bit extra
		width = width + textWidth;
		xPos = rightPos-width;
		yPos = y;
	}
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponentPattern mcp, Graphics graphicsContext) {
		this.pattern = true;
		this.mcp = mcp;
		this.mc = mcp.getMolecularComponent();
		this.graphicsContext = graphicsContext;
		displayName = adjustForSize(mc.getDisplayName());
		
		String longestName = "";	// we find the longest State name
		for(int i=0; i<mc.getComponentStateDefinitions().size(); i++) {
			String stateName = mc.getComponentStateDefinitions().get(i).getDisplayName();
			stateName = adjustForSize(stateName);
			longestName = stateName.length() > longestName.length() ? stateName : longestName;
		}
		longestName = displayName.length() > longestName.length() ? displayName : longestName;
		textWidth = getStringWidth(longestName.substring(1)) + 5;	// we provide space for the component name and a bit extra
		width = width + textWidth;
		xPos = rightPos-width;
		yPos = y;
	}

	private String adjustForSize(String input) {
		int len = input.length();
		if(len > 8) {
			return(input.substring(0,4) + ".." + input.substring(len-2, len));
		} else {
			return(input);
		}
	}

	private Font deriveComponentFontBold() {
		Font fontOld = graphicsContext.getFont();
		Font font = fontOld.deriveFont((float) (componentDiameter*3/5)).deriveFont(Font.BOLD);
		return font;
	}
	private Font deriveStateFont() {
		Font fontOld = graphicsContext.getFont();
		Font font = fontOld.deriveFont((float) (componentDiameter*3/5));
		return font;
	}
	
	private int getStringWidth(String s) {
//		Font font = graphicsContext.getFont().deriveFont(Font.BOLD);
		Font font = deriveComponentFontBold();
		FontMetrics fm = graphicsContext.getFontMetrics(font);
		int stringWidth = fm.stringWidth(s);
		return stringWidth;
	}
	private int getStringHeight(Font font) {
		FontMetrics fm = graphicsContext.getFontMetrics(font);
		int stringHeight = fm.getHeight();
		return stringHeight;
	}

	public static void paintComponents(Graphics g, SpeciesTypeLargeShape parent, Graphics graphicsContext) {
		int size = parent.getSpeciesType().getComponentList().size();
		int fixedPart = parent.getX() + parent.getWidth();
		int offsetFromRight = 10;
		for(int i=size-1; i >=0; i--) {
			int rightPos = fixedPart - offsetFromRight;		// we compute distance from right end
			int y = parent.getY() + parent.getHeight() - componentDiameter;
			// we draw the components from left to right, so we start with the last
			
			if(parent.getPattern() == false) {
				MolecularComponent mc = parent.getSpeciesType().getComponentList().get(i);
				MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(rightPos, y, mc, graphicsContext);
				offsetFromRight += mlcls.getWidth() + componentSeparation;
				mlcls.paintSelf(g);
			} else {
				MolecularComponentPattern mcp = parent.getMolecularTypePattern().getComponentPatternList().get(i);
				MolecularComponentLargeShape mlcls = new MolecularComponentLargeShape(rightPos, y, mcp, graphicsContext);
				offsetFromRight += mlcls.getWidth() + componentSeparation;
				mlcls.paintSelf(g);
			}
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
		if(mc.getComponentStateDefinitions().isEmpty()) {
			g.setColor(Color.lightGray);
		} else {
			g.setColor(Color.yellow);
		}
		g.fillOval(xPos, yPos, componentDiameter + textWidth, componentDiameter);			// g.fillRect(xPos, yPos, width, height);
		g.setColor(Color.BLACK);
		g.drawOval(xPos, yPos, componentDiameter + textWidth, componentDiameter);
		
		Font fontOld = g.getFont();
		Color colorOld = g.getColor();
//		Font font = fontOld.deriveFont((float) (componentDiameter*3/5)).deriveFont(Font.BOLD);
		Font font = deriveComponentFontBold();
		g.setFont(font);
		g.setColor(Color.black);
		g2.drawString(displayName, xPos+3+componentDiameter/3, yPos+4+componentDiameter/2);
		g.setFont(fontOld);
		g.setColor(colorOld);
		
		if(pattern == false) {			// component of a molecular type, we display all its states
			font = deriveStateFont();
			g.setFont(font);
			g.setColor(Color.black);
			for(int i=0; i<mc.getComponentStateDefinitions().size(); i++) {
				ComponentStateDefinition csd = mc.getComponentStateDefinitions().get(i);
				String s = adjustForSize(csd.getDisplayName());
				s = "~" + s;
				g.drawString(s, xPos+7, yPos + componentDiameter + getStringHeight(font)*(i+1));
			}
		} else {						// component pattern, we display its bonds
			g.setColor(Color.black);
			
			
		}
		g.setFont(fontOld);
		g.setColor(colorOld);
	}

}
