package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.RbmElementAbstract;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;

import cbit.vcell.client.desktop.biomodel.RbmTreeCellRenderer;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;

public class MolecularComponentLargeShape extends AbstractComponentShape implements HighlightableShapeInterface {
	
	public static final int componentSeparation = 6;	// distance between components
	public static final int baseWidth = 16;
	public static final int baseHeight = 17;
	public static final int cornerArc = 17;
	
	final Graphics graphicsContext;
	
	private boolean pattern;					// we draw component or we draw component pattern (and perhaps a bond)
	private int xPos = 0;
	private int yPos = 0;
	private int width = baseWidth;		// with no letters inside, it's an empty circle
	private int height = baseHeight;
	
	private int textWidth = 0;			// we add this to componentDiameter to obtain the final width of the pill
	private final String displayName;
	private final MolecularComponent mc;
	private final MolecularComponentPattern mcp;
	private final Displayable owner;
	private final List <ComponentStateLargeShape> stateShapes = new ArrayList<ComponentStateLargeShape> ();
	
	//---------------------------------------------------------------------------------------------------
	public class ComponentStateLargeShape implements LargeShape, HighlightableShapeInterface {

		final Graphics graphicsContext;
		private final Font font;
		private int xPos;
		private int yPos;
		private int width;
		private final int height;

		private ComponentStateDefinition csd = null;
		private ComponentStatePattern csp = null;
		private final Displayable owner;
		private String displayName;
		
		public ComponentStateLargeShape(int x, int y, RbmElementAbstract rea, Graphics gc, Displayable owner) {
			this.xPos = x;
			this.yPos = y;
			
			this.owner = owner;
			if(owner instanceof MolecularType) {
			this.csd = (ComponentStateDefinition)rea;
			} else {
				this.csp = (ComponentStatePattern)rea;
				this.csd = csp.getComponentStateDefinition();	// we create a large shape only if the ComponentStatePattern has a ComponentStateDefinition
			}
			this.graphicsContext = gc;
			
			this.displayName = "~" + adjustForSize(csd.getDisplayName());
			this.font = deriveStateFont();
			FontMetrics fm = graphicsContext.getFontMetrics(font);
			height = fm.getHeight();
			width = fm.stringWidth(displayName);
		}
		
		public boolean contains(PointLocationInShapeContext locationContext) {
			Rectangle2D rect = makeStateRectangle();
			if(rect.contains(locationContext.point)) {
				locationContext.csls = this;
				return true;
			}
			return false;		// point not inside this component shape, locationContext.mcs remains null;
		}
		// the rectangle is a bit wider and somewhat centered around the text
		private Rectangle2D makeStateRectangle() {
			final int xOffsetLeft = 3;
			final int xOffsetWidth = 4;
			final int yOffset = 2;
			return new Rectangle2D.Double(xPos-xOffsetLeft, yPos-height+yOffset, width+xOffsetWidth, height-1);
		}

		@Override
		public void setX(int xPos) {
			this.xPos = xPos;
		}
		@Override
		public int getX() {
			return xPos;
		}
		@Override
		public void setY(int yPos) {
			this.yPos = yPos;
		}
		@Override
		public int getY() {
			return yPos;
		}
		public void forceDifferentWidth(int width) {
			// use this to force a different with, for instance if we want all the rectangles used
			// for contains() to be equal
			// we don't allow reducing the width below the width of the text
			FontMetrics fm = graphicsContext.getFontMetrics(font);
			int minWidth = fm.stringWidth(displayName);
			if(width > minWidth) {
				this.width = width;
			}
		}
		@Override
		public int getWidth() {
			return width;
		}
		@Override
		public int getHeight() {
			return height;
		}
		@Override
		public void paintSelf(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			Font fontOld = g.getFont();
			Color colorOld = g.getColor();
			Paint paintOld = g2.getPaint();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color paleBlue = Color.getHSBColor(0.6f, 0.03f, 1.0f);
			Color darkerBlue = Color.getHSBColor(0.6f, 0.18f, 1.0f);	// a bit darker for border
			Rectangle2D rect = makeStateRectangle();
			
			if(owner instanceof MolecularType) {
				// we deal with highlighting only for Molecular Type States - because only here we have more
				// than one and highlighting them individually makes sense
				if(isHighlighted()) {
					g2.setPaint(paleBlue);
					g2.fill(rect);
					g2.setColor(darkerBlue);
					g2.draw(rect);
				} else {
					g2.setPaint(Color.white);
					g2.fill(rect);
					g2.setColor(Color.white);
					g2.draw(rect);
				}
			}
			g.setFont(font);
			g.setColor(Color.black);
			g.drawString(displayName, xPos, yPos);
			
			g2.setPaint(paintOld);
			g.setFont(fontOld);
			g.setColor(colorOld);
		}

		@Override
		public boolean isHighlighted() {
			
			if(owner instanceof RbmObservable) {
				return csp.isHighlighted();
			} else if(owner instanceof MolecularType) {
				return csd.isHighlighted();
			} else if(owner instanceof SpeciesContext) {
				return csp.isHighlighted();
			} else if(owner instanceof ReactionRule) {
				return csp.isHighlighted();
			}
			return false;
		}
		@Override
		public void setHighlight(boolean b) {
			if(owner instanceof RbmObservable) {
				csp.setHighlighted(b);
			} else if(owner instanceof MolecularType) {
				csd.setHighlighted(b);
			} else if(owner instanceof SpeciesContext) {
				csp.setHighlighted(b);
			} else if(owner instanceof ReactionRule) {
				csp.setHighlighted(b);
			}
		}
		@Override
		public void turnHighlightOffRecursive(Graphics g) {
			boolean oldHighlight = isHighlighted();
			setHighlight(false);
			if(oldHighlight == true) {
				paintSelf(g);			// paint self not highlighted if previously highlighted
			}
		}

		public Displayable getOwner() {
			return owner;
		}
		public ComponentStateDefinition getComponentStateDefinition() {
			return csd;
		}
		public ComponentStatePattern getComponentStatePattern() {
			return csp;
		}
	}	// --- end class ComponentStateLargeShape ---------------------------------------------------------------

	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponent mc, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.pattern = false;
		this.mcp = null;
		this.mc = mc;
		this.graphicsContext = graphicsContext;
		displayName = adjustForSize(mc.getDisplayName());
		
		String longestName = "";	// we find the longest State name
		Font font = deriveStateFont();
		int stateHeight = getStringHeight(font);
		for(int i=0; i<mc.getComponentStateDefinitions().size(); i++) {
			ComponentStateDefinition csd = mc.getComponentStateDefinitions().get(i);
			String stateName = csd.getDisplayName();
			stateName = adjustForSize(stateName);
			longestName = stateName.length() > longestName.length() ? stateName : longestName;
		}
		// we reserve enough space for component name or state name, whichever is longer
		// TODO: this is not an exact science because so far we checked just for the number of characters
		longestName = displayName.length() > longestName.length() ? displayName : longestName;
		// there's already space enough in the circle if the component name is 1 letter only
		// we add the +2 to slightly adjust width if that single letter is long (like "m")
		textWidth = getStringWidth(longestName);	// we provide space for the component name  longestName.substring(1)
		width = width + textWidth;
		xPos = rightPos-width;
		yPos = y;
		for(int i=0; i<mc.getComponentStateDefinitions().size(); i++) {
			ComponentStateDefinition csd = mc.getComponentStateDefinitions().get(i);
			ComponentStateLargeShape csls = new ComponentStateLargeShape(xPos+8, yPos + height + stateHeight*(i+1), csd, graphicsContext, owner);
			stateShapes.add(csls);
		}
	}
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponentPattern mcp, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.pattern = true;
		this.mcp = mcp;
		this.mc = mcp.getMolecularComponent();
		this.graphicsContext = graphicsContext;
		
		displayName = adjustForSize(mc.getDisplayName());
		String stateName = "";
		ComponentStatePattern csp = mcp.getComponentStatePattern();
		if(csp != null && !csp.isAny() && mcp.getComponentStatePattern().getComponentStateDefinition() != null) {
			ComponentStateDefinition csd = mcp.getComponentStatePattern().getComponentStateDefinition();
			stateName = adjustForSize(csd.getDisplayName());
			stateName = "~" + stateName;
		}
		int displayNameWidth = getStringWidth(displayName);
		int stateNameWidth = getStringWidth(stateName);
		
		textWidth = displayNameWidth > stateNameWidth ? displayNameWidth : stateNameWidth;
		width = width + textWidth;
		xPos = rightPos-width;
		yPos = y;
		if(csp != null && !csp.isAny() && mcp.getComponentStatePattern().getComponentStateDefinition() != null) {
			ComponentStateLargeShape csls = new ComponentStateLargeShape(xPos + baseWidth/2 +6, yPos + height + SpeciesPatternLargeShape.yLetterOffset, mcp.getComponentStatePattern(), graphicsContext, owner);
			stateShapes.add(csls);
		}
	}

	public MolecularComponentPattern getMolecularComponentPattern() {
		return mcp;
	}
	public MolecularComponent getMolecularComponent() {
		return mc;
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
		Font font = fontOld.deriveFont((float) (baseHeight*3/5)).deriveFont(Font.BOLD);
		return font;
	}
	private Font deriveStateFont() {
		Font fontOld = graphicsContext.getFont();
		Font font = fontOld.deriveFont((float) (height*3/5));
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
		boolean highlight = isHighlighted();
		if(owner == null) {
			return highlight == true ? componentBad.brighter() : componentBad;
		}
		Color componentColor = highlight == true ? componentBad.brighter() : componentBad;
		if(owner instanceof MolecularType) {
			componentColor = highlight == true ? componentGreen.brighter() : componentGreen;
		} else if(owner instanceof SpeciesContext) {
			componentColor = highlight == true ? componentGreen.brighter() : componentGreen;
		} else if(mcp != null && owner instanceof RbmObservable) {
			componentColor = highlight == true ? componentHidden.brighter() : componentHidden;
			if(mcp.isbVisible()) {
				componentColor = highlight == true ? componentGreen.brighter() : componentGreen;
			}
			ComponentStatePattern csp = mcp.getComponentStatePattern();
			if(csp != null && !csp.isAny()) {
				componentColor = highlight == true ? componentGreen.brighter() : componentGreen;
			}
		} else if(owner instanceof ReactionRule) {
			componentColor = highlight == true ? componentHidden.brighter() : componentHidden;
			if(mcp.isbVisible()) {
				componentColor = highlight == true ? componentGreen.brighter() : componentGreen;
			}
			ComponentStatePattern csp = mcp.getComponentStatePattern();
			if(csp != null && !csp.isAny()) {
				componentColor = highlight == true ? componentGreen.brighter() : componentGreen;
			}
		}
		if(AbstractComponentShape.hasIssues(owner, mcp, mc)) {
			componentColor = highlight == true ? componentBad.brighter() : componentBad;
		}
		return componentColor;
	}

	// ----------------------------------------------------------------------------------------------
	@Override
	public boolean contains(PointLocationInShapeContext locationContext) {
		
		// first we check if the point is inside a subcomponent of "this"
		for(ComponentStateLargeShape csls : stateShapes) {
			boolean found = csls.contains(locationContext);
			if(found) {
				// since point is inside one of our components it's also inside "this"
				locationContext.mcs = this;
				return true;	// if the point is inside a ComponentStateLargeShape there's no need to check others
			}
		}
		// even if the point it's not inside one of our subcomponents it may still be inside "this"
		RoundRectangle2D rect = new RoundRectangle2D.Float(xPos, yPos, width, baseHeight, cornerArc, cornerArc);
		if(rect.contains(locationContext.point)) {
			locationContext.mcs = this;
			return true;
		}
		return false;		// point not inside this component shape, locationContext.mcs remains null;
	}
	
	public void paintSelf(Graphics g) {
		paintComponent(g);
	}
	private void paintComponent(Graphics g) {
		
		boolean hidden = AbstractComponentShape.isHidden(owner, mcp);
		Graphics2D g2 = (Graphics2D)g;
		Font fontOld = g.getFont();
		Color colorOld = g.getColor();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color componentColor = setComponentColor();
		g2.setColor(componentColor);
		
		RoundRectangle2D normalRectangle = new RoundRectangle2D.Float(xPos, yPos, width, baseHeight, cornerArc, cornerArc);
		RoundRectangle2D innerRectangle = new RoundRectangle2D.Float(xPos+1, yPos+1, width-2, baseHeight-2, cornerArc-2, cornerArc-2);
		g2.fill(normalRectangle);
		if(AbstractComponentShape.hasIssues(owner, mcp, mc)) {
			g2.setColor(Color.red);
		} else {
			if(hidden == false) {
				g.setColor(Color.black);
			} else {
				g.setColor(Color.gray);
			}
		}
		g2.draw(normalRectangle);
		if(isHighlighted()) {
			g2.setColor(Color.black);
			g2.draw(innerRectangle);
			g2.setColor(componentColor);
			}

		Font font = deriveComponentFontBold(graphicsContext);
		g.setFont(font);
		if(hidden == false) {
			g.setColor(Color.black);
		} else {
			g.setColor(Color.gray);
		}
		g2.drawString(displayName, xPos+1+baseWidth/2, yPos+5+baseHeight/2);

		for(ComponentStateLargeShape csls : stateShapes) {	// text of the State(s), if any
			csls.paintSelf(g);
		}
		
		// the smaller "State" yellow circle
		if(mc.getComponentStateDefinitions().size()>0) {
			g2.setColor(componentYellow);
			g2.fillOval(xPos + width - 12, yPos-6, 10, 10);
			g.setColor(Color.gray);
			g2.drawOval(xPos + width - 12, yPos-6, 10, 10);
		}
		
		g.setFont(fontOld);
		g.setColor(colorOld);
	}
	
	@Override
	public void setHighlight(boolean b) {
		if(owner instanceof RbmObservable) {
			mcp.setHighlighted(b);
		} else if(owner instanceof MolecularType) {
			mc.setHighlighted(b);
		} else if(owner instanceof SpeciesContext) {
			mcp.setHighlighted(b);
		} else if(owner instanceof ReactionRule) {
			mcp.setHighlighted(b);
		}
	}
	@Override
	public boolean isHighlighted() {
		if(owner instanceof RbmObservable) {
			return mcp.isHighlighted();
		} else if(owner instanceof MolecularType) {
			return mc.isHighlighted();
		} else if(owner instanceof SpeciesContext) {
			return mcp.isHighlighted();
		} else if(owner instanceof ReactionRule) {
			return mcp.isHighlighted();
		}
		return false;
	}
	@Override
	public void turnHighlightOffRecursive(Graphics g) {	// not really recursive, no subchildren (for now)
		boolean oldHighlight = isHighlighted();
		setHighlight(false);
		if(oldHighlight == true) {
			paintSelf(g);			// paint self not highlighted if previously highlighted
		}
		for(ComponentStateLargeShape css : stateShapes) {
			css.turnHighlightOffRecursive(g);
		}
	}

}
