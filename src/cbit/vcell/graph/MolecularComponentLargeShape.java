package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RRQRDecomposition;
import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.RbmElementAbstract;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;

import cbit.vcell.client.desktop.biomodel.RbmTreeCellRenderer;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.SpeciesContext;

public class MolecularComponentLargeShape extends AbstractComponentShape implements LargeShape, HighlightableShapeInterface {
	
	public static final int ComponentSeparation = 6;
	public static final int BaseWidth = 24;				// was 16
	public static final int BaseHeight = 19;			// was 17
	public static final int CornerArc = 17;

	public final int componentSeparation;	// distance between components
	public final int baseWidth;
	public final int baseHeight;
	public final int cornerArc;

	final LargeShapePanel shapePanel;
	
	private boolean pattern;			// we draw component or we draw component pattern (and perhaps a bond)
	private int xPos = 0;
	private int yPos = 0;
	private int width;			// with no letters inside, it's an empty circle
	private final int height;
	
	private int textWidth = 0;			// we add this to componentDiameter to obtain the final width of the pill
	private final String displayName;
	private final MolecularComponent mc;
	private final MolecularComponentPattern mcp;
	private final Displayable owner;
	
	private boolean bMatchesSignature = false;
	
	private final List <ComponentStateLargeShape> stateShapes = new ArrayList<ComponentStateLargeShape> ();
	
	public static int calculateComponentSeparation(LargeShapePanel shapePanel) {
		if(shapePanel == null) {
			return ComponentSeparation;
		} else {
			int Ratio = 1;	// arbitrary factor, to be determined
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;	// negative if going smaller
			int cs = ComponentSeparation + zoomFactor;
			return cs < 1 ? 1 : cs;
		}
	}
	private static int calculateBaseWidth(LargeShapePanel shapePanel) {
		if(shapePanel == null) {
			return BaseWidth;
		} else {
			int Ratio = 2;
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;
			return BaseWidth + zoomFactor;
		}
	}
	public static int calculateBasHeight(LargeShapePanel shapePanel) {
		if(shapePanel == null) {
			return BaseHeight;
		} else {
			int Ratio = 2;
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;
			int bh = BaseHeight + zoomFactor;
			if(shapePanel.getZoomFactor() < LargeShapePanel.SmallestZoomFactorWithText) {
				bh += 1;
			}
			return bh;
		}
	}
	private static int calculateCornerArc(LargeShapePanel shapePanel) {
		if(shapePanel == null) {
			return CornerArc;
		} else {
			int Ratio = 1;
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;
			return CornerArc + zoomFactor;
		}
	}
	private static int calculateXOffsetWidth(LargeShapePanel shapePanel) {
		if(shapePanel == null) {
			return ComponentStateLargeShape.XOffsetWidth;
		} else {
			int Ratio = 1;
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;
			return ComponentStateLargeShape.XOffsetWidth + zoomFactor;
		}
	}

	//---------------------------------------------------------------------------------------------------
	public class ComponentStateLargeShape implements LargeShape, HighlightableShapeInterface {

		final static int XOffsetWidth = 16;
		final int xOffsetWidth;

		final LargeShapePanel shapePanel;
		private final Font font;
		private int xPos;
		private int yPos;
		private int width;
		private final int height;

		private MolecularComponentPattern mcp = null;
		private MolecularComponent mc = null;

		private ComponentStateDefinition csd = null;
		private ComponentStatePattern csp = null;
		private final Displayable owner;
		private String displayName;

		private boolean bMatchesSignature = false;

		public ComponentStateLargeShape(int x, int y, RbmElementAbstract reaComponent, RbmElementAbstract reaState, LargeShapePanel shapePanel, Displayable owner) {
			this.xPos = x;
			this.yPos = y;
			
			this.owner = owner;
			if(owner instanceof MolecularType) {
				this.mc = (MolecularComponent)reaComponent;
				this.csd = (ComponentStateDefinition)reaState;
			} else {
				this.mcp = (MolecularComponentPattern)reaComponent;
				this.mc = mcp.getMolecularComponent();
				this.csp = (ComponentStatePattern)reaState;
				this.csd = csp.getComponentStateDefinition();	// may be null
			}
			this.shapePanel = shapePanel;
			xOffsetWidth = calculateXOffsetWidth(shapePanel);
			
			if(shapePanel.getZoomFactor() < LargeShapePanel.SmallestZoomFactorWithText) {
				// when we zoom to very small shapes we must stop writing the text
				this.displayName = "";
			} else {
				if(this.csd == null) {
					this.displayName = "?";
				} else {
					this.displayName = adjustForStateSize(csd.getDisplayName());
				}
			}

			Graphics gc = shapePanel.getGraphics();
			this.font = deriveStateFont(gc, shapePanel);
			this.height = computeStateHeight(gc, shapePanel);
			FontMetrics fm = gc.getFontMetrics(font);
			width = fm.stringWidth(displayName) + xOffsetWidth;		// pill will be wider than the text we show inside it
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
			final int xOffsetLeft = 0;
			final int yOffset = 0;
			return new Rectangle2D.Double(xPos-xOffsetLeft, yPos+yOffset, width, height);
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
		@Override
		public int getWidth() {
			return width;
		}
		@Override
		public int getHeight() {
			return height;
		}
		@Override
		public Rectangle getLabelOutline() {
			Graphics gc = shapePanel.getGraphics();
			Font font = getLabelFont();
			FontMetrics fm = gc.getFontMetrics(font);
			int stringWidth = fm.stringWidth(getFullName());
			Rectangle labelOutline = new Rectangle(xPos+2, yPos, stringWidth+8, fm.getHeight()+5);
			return labelOutline;
		}
		@Override
		public Font getLabelFont() {
			return font;
		}
		@Override
		public String getFullName() {
			if(csd != null) {
				return csd.getDisplayName();
			} else {
				return "?";
			}
		}
		public void forceDifferentWidth(int width) {
			// use this to force a different with, for instance if we want all the rectangles used
			// for contains() to be equal
			// we don't allow reducing the width below the width of the text
			Graphics gc = shapePanel.getGraphics();
			FontMetrics fm = gc.getFontMetrics(font);
			int minWidth = fm.stringWidth(displayName);
			if(width > minWidth) {
				// we compared the widths of the strings, but we set the width of the "pill", so we add xOffsetWidth
				this.width = width + xOffsetWidth;
			}
		}
		
		public void setMatchesSignature(boolean bMatchesSignature) {
			this.bMatchesSignature = bMatchesSignature;
		}
		
		@Override
		public void paintSelf(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			Font fontOld = g.getFont();
			Color colorOld = g.getColor();
			Paint paintOld = g2.getPaint();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			RoundRectangle2D normalRectangle = new RoundRectangle2D.Float(xPos, yPos, width, height, cornerArc, cornerArc);
			if(shapePanel instanceof ParticipantSignatureShapePanel) {
				ReactionRule reactionRule = (ReactionRule)owner;
				Color stateColor = componentHidden;
				ParticipantSignatureShapePanel ssp = (ParticipantSignatureShapePanel)shapePanel;
				if(ssp.isShowNonTrivialOnly() == true) {
					if(csd != null) {
						stateColor = componentPaleYellow;
					}
				}
				if(ssp.isShowDifferencesOnly()) {
					switch (ssp.hasStateChanged(reactionRule.getName(), mcp)) {
					case CHANGED:
						stateColor = Color.orange;
						break;
					case ANALYSISFAILED:
						ArrayList<Issue> issueList = new ArrayList<Issue>();
						reactionRule.gatherIssues(new IssueContext(), issueList);
						boolean bRuleHasErrorIssues = false;
						for (Issue issue : issueList){
							if (issue.getSeverity() == Severity.ERROR){
								bRuleHasErrorIssues = true;
								break;
							}
						}
						if (bRuleHasErrorIssues) {
							stateColor = componentHidden;
						}else{
							System.err.println("ReactionRule Analysis failed, but there are not Error Issues with ReactionRule "+reactionRule.getName());
							stateColor = Color.red.darker();
						}
						break;
					default:
						break;
					}
				}
				g2.setColor(stateColor);
			} else if(shapePanel instanceof RulesShapePanel) {
				ReactionRule reactionRule = (ReactionRule)owner;
				Color stateColor = componentHidden;
				RulesShapePanel rsp = (RulesShapePanel)shapePanel;
				if(rsp.isShowNonTrivialOnly() == true) {
					if(csd != null) {
						stateColor = componentPaleYellow;
					}
				}
				if(rsp.isShowDifferencesOnly()) {
					switch (rsp.hasStateChanged(mcp)) {
					case CHANGED:
						stateColor = Color.orange;
						break;
					case ANALYSISFAILED:
						ArrayList<Issue> issueList = new ArrayList<Issue>();
						reactionRule.gatherIssues(new IssueContext(), issueList);
						boolean bRuleHasErrorIssues = false;
						for (Issue issue : issueList){
							if (issue.getSeverity() == Severity.ERROR){
								bRuleHasErrorIssues = true;
								break;
							}
						}
						if (bRuleHasErrorIssues) {
							stateColor = componentHidden;
						}else{
							System.err.println("ReactionRule Analysis failed, but there are not Error Issues with ReactionRule "+reactionRule.getName());
							stateColor = Color.red.darker();
						}
						break;
					default:
						break;
					}
				}
				g2.setColor(stateColor);

			} else {
				if(!isHighlighted()) {
					if(csd == null) {
						g2.setColor(componentHidden);
					} else {
						g2.setColor(componentPaleYellow);
					}
				} else {
					g2.setColor(Color.white);
				}
				if(AbstractComponentShape.hasErrorIssues(owner, csp, csd)) {
					g2.setColor(isHighlighted() ? componentBad.brighter() : componentBad);
				}

			}
			g2.fill(normalRectangle);
			if(AbstractComponentShape.hasErrorIssues(owner, csp, csd)) {
				g2.setColor(Color.red);
			} else {
				g.setColor(Color.black);
			}
//			g.setColor(Color.black);
			g2.draw(normalRectangle);
			
			g.setFont(font);
			g.setColor(Color.black);
			g.drawString(displayName, xPos+7, yPos+computeStateHeight(g, shapePanel)-4);
			
			g2.setPaint(paintOld);
			g.setFont(fontOld);
			g.setColor(colorOld);
		}

		@Override
		public boolean isHighlighted() {
			
			if(owner instanceof RbmObservable) {
				return shapePanel.isHighlighted(csp);
			} else if(owner instanceof MolecularType) {
				return shapePanel.isHighlighted(csd);
			} else if(owner instanceof SpeciesContext) {
				return shapePanel.isHighlighted(csp);
			} else if(owner instanceof ReactionRule) {
				// we don't highlight when we display single row (view only, no edit)
				if(shapePanel == null) {
					return false;
				}
				if(shapePanel instanceof RulesShapePanel && ((RulesShapePanel)shapePanel).isViewSingleRow()) {
					return false;
				} else if(shapePanel instanceof ParticipantSignatureShapePanel) {
					return false;
				}
				return shapePanel.isHighlighted(csp);
			}
			return false;
		}
		@Override
		public void setHighlight(boolean b, boolean param) {
			// param is ignored
			if(owner instanceof RbmObservable) {
				shapePanel.csp = b ? csp : null;
			} else if(owner instanceof MolecularType) {
				shapePanel.csd = b ? csd : null;
			} else if(owner instanceof SpeciesContext) {
				shapePanel.csp = b ? csp : null;
			} else if(owner instanceof ReactionRule) {
				shapePanel.csp = b ? csp : null;
			}
		}
		@Override
		public void turnHighlightOffRecursive(Graphics g) {
			boolean oldHighlight = isHighlighted();
			setHighlight(false, false);
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
		public MolecularComponentPattern getMolecularComponentPattern() {
			return mcp;
		}
		
		@Override
		public String getDisplayName() {
			throw new UnsupportedOperationException();
		}
		@Override
		public String getDisplayType() {
			return ComponentStateDefinition.typeName;
		}

	}	// --- end class ComponentStateLargeShape ---------------------------------------------------------------

	// rightPos is rightmost corner of the ellipse, we compute the xPos based on the text width
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponent mc, LargeShapePanel shapePanel, Displayable owner) {
		this.owner = owner;
		this.pattern = false;
		this.mcp = null;
		this.mc = mc;
		this.shapePanel = shapePanel;
		displayName = adjustForComponentSize(mc.getDisplayName());
		
		componentSeparation = calculateComponentSeparation(shapePanel);
		baseWidth = calculateBaseWidth(shapePanel);
		baseHeight = calculateBasHeight(shapePanel);
		cornerArc = calculateCornerArc(shapePanel);
		
		int stateXOffsetWidth = calculateXOffsetWidth(shapePanel);

		height = baseHeight;
		width = baseWidth;
		
		Graphics gc = shapePanel.getGraphics();
		Font font = deriveStateFont(gc, shapePanel);
		int stateHeight = getStringHeight(font)+2;	// we reserve 2 extra pixels height as separation between states
		String longestStateName = getLongestStateName(mc);
		
		// we reserve enough space for component name or state name, whichever is longer
		// TODO: this is not an exact science because so far we checked just for the number of characters
		textWidth = getStringWidth(displayName) > getStateStringWidth(longestStateName, gc, shapePanel) ? getStringWidth(displayName) : getStateStringWidth(longestStateName, gc, shapePanel);
		width += textWidth;
		xPos = rightPos-width;
		yPos = y;
		for(int i=0; i<mc.getComponentStateDefinitions().size(); i++) {
			ComponentStateDefinition csd = mc.getComponentStateDefinitions().get(i);
			// align the end of the state shape with the end of the component shape
			int xPosState = xPos + width - getStateStringWidth(longestStateName, gc, shapePanel) - stateXOffsetWidth;
			ComponentStateLargeShape csls = new ComponentStateLargeShape(xPosState, yPos - computeStateHeight(gc, shapePanel)*(i+1)+1, mc, csd, shapePanel, owner);
			csls.forceDifferentWidth(getStateStringWidth(longestStateName, gc, shapePanel));
			stateShapes.add(csls);
		}
	}
	public MolecularComponentLargeShape(int rightPos, int y, MolecularComponentPattern mcp, LargeShapePanel shapePanel, Displayable owner) {
		this.owner = owner;
		this.pattern = true;
		this.mcp = mcp;
		this.mc = mcp.getMolecularComponent();
		this.shapePanel = shapePanel;
		
		componentSeparation = calculateComponentSeparation(shapePanel);
		baseWidth = calculateBaseWidth(shapePanel);
		baseHeight = calculateBasHeight(shapePanel);
		cornerArc = calculateCornerArc(shapePanel);

		int stateXOffsetWidth = calculateXOffsetWidth(shapePanel);

		height = baseHeight;
		width = baseWidth;

		displayName = adjustForComponentSize(mc.getDisplayName());
		String stateName = "";
		ComponentStatePattern csp = mcp.getComponentStatePattern();
		if(csp != null && !csp.isAny() && mcp.getComponentStatePattern().getComponentStateDefinition() != null) {
			ComponentStateDefinition csd = mcp.getComponentStatePattern().getComponentStateDefinition();
			stateName = adjustForStateSize(csd.getDisplayName());
		}
		Graphics gc = shapePanel.getGraphics();
		String longestStateName = getLongestStateName(mc);
		int displayNameWidth = getStringWidth(displayName);
		int longestStateNameWidth = getStateStringWidth(longestStateName, gc, shapePanel);
		
		textWidth = displayNameWidth > longestStateNameWidth ? displayNameWidth : longestStateNameWidth;
		width += textWidth;
		xPos = rightPos-width;
		yPos = y;
//		if(csp != null && !csp.isAny() && mcp.getComponentStatePattern().getComponentStateDefinition() != null) {
		if(csp != null) {
			// align the end of the state shape with the end of the component shape
			int xPosState = xPos + width - longestStateNameWidth - stateXOffsetWidth;
			ComponentStateLargeShape csls = new ComponentStateLargeShape(xPosState, yPos-computeStateHeight(gc, shapePanel)+1, mcp, csp, shapePanel, owner);
			csls.forceDifferentWidth(getStateStringWidth(longestStateName, gc, shapePanel));
			stateShapes.add(csls);
		}
	}
	private String getLongestStateName(MolecularComponent mc) {
		String longestStateName = "";
		if(shapePanel.getZoomFactor() < LargeShapePanel.SmallestZoomFactorWithText) {
			return "";
		}
		for(int i=0; i<mc.getComponentStateDefinitions().size(); i++) {
			ComponentStateDefinition csd = mc.getComponentStateDefinitions().get(i);
			String stateName = csd.getDisplayName();
			stateName = adjustForStateSize(stateName);
			longestStateName = stateName.length() > longestStateName.length() ? stateName : longestStateName;
		}
		return longestStateName;
	}

	public MolecularComponentPattern getMolecularComponentPattern() {
		return mcp;
	}
	public MolecularComponent getMolecularComponent() {
		return mc;
	}
	@Override
	public int getX(){
		return xPos;
	}
	@Override
	public void setX(int xPos) {
		this.xPos = xPos;
	}
	@Override
	public int getY(){
		return yPos;
	}
	@Override
	public void setY(int yPos) {
		this.yPos = yPos;
	}
	@Override
	public int getWidth(){
		return width;
	} 
	@Override
	public int getHeight(){
		return height;
	}
	@Override
	public Rectangle getLabelOutline() {
		Graphics gc = shapePanel.getGraphics();
		Font font = getLabelFont();
		FontMetrics fm = gc.getFontMetrics(font);
		int stringWidth = fm.stringWidth(getFullName());
		Rectangle labelOutline = new Rectangle(xPos+6, yPos+2, stringWidth+18, fm.getHeight()+2);
		return labelOutline;
	}
	@Override
	public Font getLabelFont() {
		Graphics gc = shapePanel.getGraphics();
		return MolecularComponentLargeShape.deriveComponentFontBold(gc, shapePanel);
	}
	@Override
	public String getFullName() {
		if(mc != null) {
			return mc.getDisplayName();
		} else {
			return "?";
		}
	}
	
	private String adjustForComponentSize(String input) {
		
		if(shapePanel.getZoomFactor() < LargeShapePanel.SmallestZoomFactorWithText) {
			// when we zoom to very small shapes we must stop writing the text
			return "  ";
		}
		int len = input.length();
		if(len > 8) {
			return(input.substring(0,4) + ".." + input.substring(len-2, len));
		} else {
			return(input);
		}
	}
	private String adjustForStateSize(String input) {
		if(shapePanel.getZoomFactor() < LargeShapePanel.SmallestZoomFactorWithText) {
			// when we zoom to very small shapes we must stop writing the text
			return "";
		}
		int len = input.length();
		if(len > 8) {
			return(input.substring(0,4) + ".." + input.substring(len-2, len));
		} else {
			return(input);
		}
	}

	public static Font deriveComponentFontBold(Graphics gc, LargeShapePanel shapePanel) {
		Font fontOld = gc.getFont();
		int bh = calculateBasHeight(shapePanel);
		Font font = fontOld.deriveFont((float) (bh*3/5)).deriveFont(Font.BOLD);
		return font;
	}
	private static Font deriveStateFont(Graphics gc, LargeShapePanel shapePanel) {
		Font fontOld = gc.getFont();
		int bh = calculateBasHeight(shapePanel);
		Font font = fontOld.deriveFont((float) ((float)bh*3.0/6.0));
		return font;
	}
	public static int computeStateHeight(Graphics gc, LargeShapePanel shapePanel) {
		Font font = deriveStateFont(gc, shapePanel);
		FontMetrics fm = gc.getFontMetrics(font);
		if(shapePanel.getZoomFactor() < LargeShapePanel.SmallestZoomFactorWithText) {
			// when we zoom to very small shapes we stop showing text, hence no need to reserve pixels
			return fm.getHeight() + 1;
		} else {
			return fm.getHeight() + 4;		// we allow 2 pixels above and below the state text
		}
	}
	private static int getStateStringWidth(String s, Graphics gc, LargeShapePanel shapePanel) {
		Font font = deriveStateFont(gc, shapePanel);
		FontMetrics fm = gc.getFontMetrics(font);
		return fm.stringWidth(s);
	}
	
	private int getStringWidth(String s) {
//		Font font = graphicsContext.getFont().deriveFont(Font.BOLD);
		Graphics gc = shapePanel.getGraphics();
		Font font = deriveComponentFontBold(gc, shapePanel);
		FontMetrics fm = gc.getFontMetrics(font);
		int stringWidth = fm.stringWidth(s);
		return stringWidth;
	}
	private int getStringHeight(Font font) {
		Graphics gc = shapePanel.getGraphics();
		FontMetrics fm = gc.getFontMetrics(font);
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
			componentColor = highlight == true ? componentPaleYellow.brighter() : componentPaleYellow;
		} else if(owner instanceof SpeciesContext) {
			componentColor = highlight == true ? componentPaleYellow.brighter() : componentPaleYellow;
		} else if(mcp != null && owner instanceof RbmObservable) {
			componentColor = highlight == true ? componentHidden.brighter() : componentHidden;
			if(mcp.getBondType() != BondType.Possible) {
				componentColor = highlight == true ? componentPaleYellow.brighter() : componentPaleYellow;
			}
		} else if(owner instanceof ReactionRule) {
			
			ReactionRule reactionRule = (ReactionRule)owner;
			if(shapePanel instanceof ParticipantSignatureShapePanel) {
				
				ParticipantSignatureShapePanel ssp = (ParticipantSignatureShapePanel)shapePanel;
				componentColor = componentHidden;
				if(ssp.isShowNonTrivialOnly() == true) {
					if(mcp.getBondType() != BondType.Possible) {
						componentColor = componentPaleYellow;
					} else {
						componentColor = componentHidden;
					}
				}
				if(ssp.isShowDifferencesOnly()) {
					switch (ssp.hasBondChanged(reactionRule.getName(), mcp)) {
					case CHANGED:
						componentColor = Color.orange;
						break;
					case ANALYSISFAILED:
						ArrayList<Issue> issueList = new ArrayList<Issue>();
						reactionRule.gatherIssues(new IssueContext(), issueList);
						boolean bRuleHasErrorIssues = false;
						for (Issue issue : issueList) {
							if (issue.getSeverity() == Severity.ERROR){
								bRuleHasErrorIssues = true;
								break;
							}
						}
						if (bRuleHasErrorIssues) {
							componentColor = componentHidden;
						} else {
							System.err.println("ReactionRule Analysis failed, but there are not Error Issues with ReactionRule "+reactionRule.getName());
							componentColor = Color.red.darker();
						}
						break;
					default:
						break;
					}
				}
			} else if(shapePanel instanceof RulesShapePanel) {
				RulesShapePanel rsp = (RulesShapePanel)shapePanel;
				componentColor = componentHidden;
				if(rsp.isShowNonTrivialOnly() == true) {
					if(mcp.getBondType() != BondType.Possible) {
						componentColor = componentPaleYellow;
					} else {
						componentColor = componentHidden;
					}
				}
				if(rsp.isShowDifferencesOnly()) {
					switch (rsp.hasBondChanged(mcp)) {
					case CHANGED:
						componentColor = Color.orange;
						break;
					case ANALYSISFAILED:
						ArrayList<Issue> issueList = new ArrayList<Issue>();
						reactionRule.gatherIssues(new IssueContext(), issueList);
						boolean bRuleHasErrorIssues = false;
						for (Issue issue : issueList) {
							if (issue.getSeverity() == Severity.ERROR){
								bRuleHasErrorIssues = true;
								break;
							}
						}
						if (bRuleHasErrorIssues) {
							componentColor = componentHidden;
						} else {
							System.err.println("ReactionRule Analysis failed, but there are not Error Issues with ReactionRule "+reactionRule.getName());
							componentColor = Color.red.darker();
						}
						break;
					default:
						break;
					}
				}
			} else {
				componentColor = highlight == true ? componentHidden.brighter() : componentHidden;
				if(mcp.getBondType() != BondType.Possible) {
					componentColor = highlight == true ? componentPaleYellow.brighter() : componentPaleYellow;
				}
			}
		}
		if(AbstractComponentShape.hasErrorIssues(owner, mcp, mc)) {
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
		// the bond area is also considered to belong to the component shape (except for molecular type which has no bond)
		if(!(owner instanceof MolecularType)) {
			Rectangle2D bondRect = new Rectangle2D.Double(xPos, yPos+height, 15, 15);
			if(bondRect.contains(locationContext.point)) {
				locationContext.mcs = this;
				return true;
			}
		}
		return false;		// point not inside this component shape, locationContext.mcs remains null;
	}
	
	public void setMatchesSignature(boolean bMatchesSignature) {
		this.bMatchesSignature = bMatchesSignature;
		for(ComponentStateLargeShape csls : stateShapes) {
			csls.setMatchesSignature(bMatchesSignature);
		}
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
		if(AbstractComponentShape.hasErrorIssues(owner, mcp, mc)) {
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
		
		Graphics gc = shapePanel.getGraphics();
		Font font = deriveComponentFontBold(gc, shapePanel);
		g.setFont(font);
		if(hidden == false) {
			g.setColor(Color.black);
		} else {
			g.setColor(Color.gray);
		}
		int fontSize = font.getSize();
		int textX = xPos+1+baseWidth/2;
		int textY =  yPos + baseHeight - (baseHeight - fontSize)/2;		// yPos+5+baseHeight/2
		g2.drawString(displayName, textX, textY);

		for(ComponentStateLargeShape csls : stateShapes) {	// text of the State(s), if any
			csls.paintSelf(g);
		}
		g.setFont(fontOld);
		g.setColor(colorOld);
	}
	
	@Override
	public void setHighlight(boolean b, boolean param) {
		// param is ignored
		if(owner instanceof RbmObservable) {
			shapePanel.mcp = b ? mcp : null;
		} else if(owner instanceof MolecularType) {
			shapePanel.mc = b ? mc : null;
		} else if(owner instanceof SpeciesContext) {
			shapePanel.mcp = b ? mcp : null;
		} else if(owner instanceof ReactionRule) {
			shapePanel.mcp = b ? mcp : null;
		}
	}
	@Override
	public boolean isHighlighted() {
		if(owner instanceof RbmObservable) {
			return shapePanel.isHighlighted(mcp);
		} else if(owner instanceof MolecularType) {
			return shapePanel.isHighlighted(mc);
		} else if(owner instanceof SpeciesContext) {
			return shapePanel.isHighlighted(mcp);
		} else if(owner instanceof ReactionRule) {
			// we don't highlight when we display single row (view only, no edit)
			if(shapePanel == null) {
				return false;
			}
			if(shapePanel instanceof RulesShapePanel && ((RulesShapePanel)shapePanel).isViewSingleRow()) {
				return false;
			}
			return shapePanel.isHighlighted(mcp);
		}
		return false;
	}
	@Override
	public void turnHighlightOffRecursive(Graphics g) {
		boolean oldHighlight = isHighlighted();
		setHighlight(false, false);
		if(oldHighlight == true) {
			paintSelf(g);			// paint self not highlighted if previously highlighted
		}
		for(ComponentStateLargeShape css : stateShapes) {
			css.turnHighlightOffRecursive(g);
		}
	}

	@Override
	public String getDisplayName() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String getDisplayType() {
		return MolecularComponent.typeName;
	}

}
