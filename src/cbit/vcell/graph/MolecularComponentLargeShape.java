package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;

import cbit.vcell.client.desktop.biomodel.RbmTreeCellRenderer;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.SpeciesContext;

public class MolecularComponentLargeShape extends AbstractComponentShape {
	
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
	private final Displayable owner;

	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponent mc, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
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
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponentPattern mcp, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.pattern = true;
		this.mcp = mcp;
		this.mc = mcp.getMolecularComponent();
		this.graphicsContext = graphicsContext;
		
		// no more than 1 state possible, we show the component name and state on the same row, inside the shape
		// as componentName + "~" + stateName
		String componentName = adjustForSize(mc.getDisplayName());
		ComponentStatePattern csp = mcp.getComponentStatePattern();
		if(csp != null && !csp.isAny() && mcp.getComponentStatePattern().getComponentStateDefinition() != null) {
			ComponentStateDefinition csd = mcp.getComponentStatePattern().getComponentStateDefinition();
			String stateName = adjustForSize(csd.getDisplayName());
			displayName = componentName + "~" + stateName;
		} else {
			displayName = componentName;
		}
		textWidth = getStringWidth(displayName) + 5;	// we provide space for the component name and a bit extra
		width = width + textWidth;
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

	private String adjustForSize(String input) {
		int len = input.length();
		if(len > 8) {
			return(input.substring(0,4) + ".." + input.substring(len-2, len));
		} else {
			return(input);
		}
	}

	public static Font deriveComponentFontBold(Graphics gc) {
		Font fontOld = gc.getFont();
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
		Font font = deriveComponentFontBold(graphicsContext);
		FontMetrics fm = graphicsContext.getFontMetrics(font);
		int stringWidth = fm.stringWidth(s);
		return stringWidth;
	}
	private int getStringHeight(Font font) {
		FontMetrics fm = graphicsContext.getFontMetrics(font);
		int stringHeight = fm.getHeight();
		return stringHeight;
	}

	private Color setComponentColor() {
		if(owner == null) {
			return componentBad;
		}
		Color componentColor = componentBad;
		if(owner instanceof MolecularType) {
			componentColor = componentGreen;
		} else if(owner instanceof SpeciesContext) {
			componentColor = componentGreen;
		} else if(mcp != null && owner instanceof RbmObservable) {
			componentColor = componentHidden;
			if(mcp.isbVisible()) {
				componentColor = componentGreen;
			}
			ComponentStatePattern csp = mcp.getComponentStatePattern();
			if(csp != null && !csp.isAny()) {
				componentColor = componentGreen;
			}
		} else if(owner instanceof ReactionParticipant) {
			
		}
		if(AbstractComponentShape.hasIssues(owner, mcp, mc)) {
			componentColor = componentBad;
		}
		return componentColor;
	}

	// ----------------------------------------------------------------------------------------------
	public void paintSelf(Graphics g) {
		paintComponent(g);
	}
	private void paintComponent(Graphics g) {
		
		boolean hidden = AbstractComponentShape.isHidden(owner, mcp);
		Graphics2D g2 = (Graphics2D)g;
		
		Color componentColor = setComponentColor();
		g2.setColor(componentColor);
		
		g2.fillOval(xPos, yPos, componentDiameter + textWidth, componentDiameter);
//		g2.fillArc(xPos, yPos, componentDiameter + textWidth, componentDiameter,0,180);		// fill half of the oval
		if(hidden == false) {
			g.setColor(Color.black);
		} else {
			g.setColor(Color.gray);
		}
		if(AbstractComponentShape.hasIssues(owner, mcp, mc)) {
			g2.setColor(Color.red);
		}
		g2.drawOval(xPos, yPos, componentDiameter + textWidth, componentDiameter);
		
		Font fontOld = g.getFont();
		Color colorOld = g.getColor();
//		Font font = fontOld.deriveFont((float) (componentDiameter*3/5)).deriveFont(Font.BOLD);
		Font font = deriveComponentFontBold(graphicsContext);
		g.setFont(font);
		if(hidden == false) {
			g.setColor(Color.black);
		} else {
			g.setColor(Color.gray);
		}
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
		
		// the smaller "State" yellow circle
		if(mc.getComponentStateDefinitions().size()>0) {
			g2.setColor(componentYellow);
			g2.fillOval(xPos + componentDiameter + textWidth - 10, yPos-4, 10, 10);
			g.setColor(Color.gray);
			g2.drawOval(xPos + componentDiameter + textWidth - 10, yPos-4, 10, 10);
		}
		
		g.setFont(fontOld);
		g.setColor(colorOld);
	}

}
