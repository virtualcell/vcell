package cbit.vcell.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;

import cbit.vcell.client.desktop.biomodel.ObservablePropertiesPanel;
import cbit.vcell.client.desktop.biomodel.RbmTreeCellRenderer;
import cbit.vcell.client.desktop.biomodel.ReactionRuleEditorPropertiesPanel;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

public class SpeciesPatternLargeShape extends AbstractComponentShape implements HighlightableShapeInterface {

	private static final int YLetterOffset = 11;		// y position of Bond id and/or State name
	public static final int separationWidth = 1;	// width between 2 molecular type patterns
	public static final int defaultHeight = 80;		// we actually always use this height, we never compute it as initially planned
	
	// left and right extension of the sp, used for reactions only
	// clicking within these limits still means we're inside that sp
	private static final int XExtent = 20;
	
	private final int yLetterOffset;
	public final int xExtent;
	
	private int xPos = 0;
	private int yPos = 0;		// y position where we draw the shape
	private int nameOffset = 0;	// offset upwards from yPos where we may write some text, like the expression of the sp
	private int height = -1;	// -1 means it doesn't matter or that we can compute it from the shape + "tallest" bond
	private List<MolecularTypeLargeShape> speciesShapes = new ArrayList<MolecularTypeLargeShape>();

	final LargeShapePanel shapePanel;
	
	private Displayable owner;
	private SpeciesPattern sp;
	private String endText = new String();	// we display this after the Shape, it's position is outside "width"
	private boolean isError = false;
	private boolean bMatchesSignature = false;	// true only if this species pattern comes from a rule participant
	// whose signature and compartment match the RuleParticipantSignatureDiagramShape selected in the Reaction Diagram
	
	List <BondSingle> bondSingles = new ArrayList <BondSingle>();	// component with no explicit bond
	List <BondPair> bondPairs = new ArrayList <BondPair>();

	
	public static int calculateYLetterOffset(LargeShapePanel shapePanel) {
		if(shapePanel == null) {
			return YLetterOffset;
		} else {
			int Ratio = 1;	// arbitrary factor, to be determined
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;	// negative if going smaller
			return YLetterOffset + zoomFactor;
		}
	}
	public static int calculateXExtent(LargeShapePanel shapePanel) {
		if(shapePanel == null) {
			return XExtent;
		} else {
			int Ratio = 1;	// arbitrary factor, to be determined
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;	// negative if going smaller
			return XExtent + zoomFactor;
		}
	}
	
	// this is only used to display an error in the ViewGeneratedSpeciespanel
	public SpeciesPatternLargeShape(int xPos, int yPos, int height, LargeShapePanel shapePanel, boolean isError) {
		this.owner = null;
		this.sp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.height = height;
		this.shapePanel = shapePanel;
		this.isError = true;
		
		yLetterOffset = calculateYLetterOffset(shapePanel);
		xExtent = calculateXExtent(shapePanel);

		int xPattern = xPos;
		MolecularTypeLargeShape stls = new MolecularTypeLargeShape(xPattern, yPos, shapePanel, null);
		speciesShapes.add(stls);
	}
		
	public SpeciesPatternLargeShape(int xPos, int yPos, int height, SpeciesPattern sp, LargeShapePanel shapePanel, Displayable owner) {
		this.owner = owner;
		this.sp = sp;
		this.xPos = xPos;
		if(owner instanceof RbmObservable) {
			nameOffset = ObservablePropertiesPanel.ReservedSpaceForNameOnYAxis;
			this.yPos = yPos+ObservablePropertiesPanel.ReservedSpaceForNameOnYAxis;
		} else if(owner instanceof ReactionRule) {
			nameOffset = ReactionRuleEditorPropertiesPanel.ReservedSpaceForNameOnYAxis;
			this.yPos = yPos+ReactionRuleEditorPropertiesPanel.ReservedSpaceForNameOnYAxis;

		} else {
			this.yPos = yPos;
		}
		this.shapePanel = shapePanel;
		
		yLetterOffset = calculateYLetterOffset(shapePanel);
		xExtent = calculateXExtent(shapePanel);

		this.height = height;

		int xPattern = xPos;
		if(sp == null) {
			// plain species context, no pattern
			MolecularTypeLargeShape stls = new MolecularTypeLargeShape(xPattern, this.yPos, shapePanel, owner);
			speciesShapes.add(stls);
			return;
		}
		
		int numPatterns = sp.getMolecularTypePatterns().size();
		for(int i = 0; i<numPatterns; i++) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(i);
			MolecularTypeLargeShape stls = new MolecularTypeLargeShape(xPattern, this.yPos, mtp, shapePanel, owner, i);
			xPattern += stls.getWidth() + separationWidth; 
			speciesShapes.add(stls);
		}
		
		// bonds - we have to deal with them here because they may be cross-molecular type patterns
		// WARNING: we assume that the order of the MolecularTypeLargeShapes in speciesShapes 
		// is the same as the order of the Molecular Type Patterns in the SpeciesPattern sp
		for(int i=0; i<numPatterns; i++) {
			MolecularTypeLargeShape stlsFrom = speciesShapes.get(i);
			MolecularTypePattern mtpFrom = stlsFrom.getMolecularTypePattern();
			int numComponents = mtpFrom.getComponentPatternList().size();
			for(int j=0; j<numComponents; j++) {
				MolecularComponentLargeShape mclsFrom = stlsFrom.getComponentShape(j);
				MolecularComponent mcFrom = mtpFrom.getMolecularType().getComponentList().get(j);
				MolecularComponentPattern mcpFrom = mtpFrom.getMolecularComponentPattern(mcFrom);
				if(mcpFrom.getBondType().equals(BondType.Specified)) {
					Bond b = mcpFrom.getBond();
					if(b == null) {		// it's half of a bond at this time, we skip it for now
						System.out.println("Null bond for " + mcpFrom.getMolecularComponent().getDisplayName());
						break;
					}
					MolecularTypePattern mtpTo = b.molecularTypePattern;
					MolecularTypeLargeShape stlsTo = getShape(mtpTo);
					if(stlsTo == null) {
						System.out.println("Null 'to' molecular type for bond of " + b.molecularTypePattern);
						break;
					}
					MolecularComponentPattern mcpTo = b.molecularComponentPattern;
					MolecularComponentLargeShape mclsTo = stlsTo.getShape(mcpTo);
					if(mclsTo == null) {
						Point from = new Point(mclsFrom.getX()+mclsFrom.baseWidth/2, mclsFrom.getY()+mclsFrom.getHeight());
						String symbol = mcpFrom.getBondType().symbol;
						BondSingle bs = new BondSingle(mcpFrom, from);
						bondSingles.add(bs);
						continue;
					}
					Point from = new Point(mclsFrom.getX()+mclsFrom.baseWidth/2, mclsFrom.getY()+mclsFrom.getHeight());
					Point to = new Point(mclsTo.getX()+mclsTo.baseWidth/2, mclsTo.getY()+mclsFrom.getHeight());
					if(from.x < to.x) {		// the bonds with from.x > to.x are duplicates
						BondPair bp = new BondPair(mcpFrom.getBondId(), from, to);
						bondPairs.add(bp);
					} 
				} else {
					Point from = new Point(mclsFrom.getX()+mclsFrom.baseWidth/2, mclsFrom.getY()+mclsFrom.getHeight());
					String symbol = mcpFrom.getBondType().symbol;
					BondSingle bs = new BondSingle(mcpFrom, from);
					bondSingles.add(bs);
				}
			}
		}
		Collections.sort(bondPairs);
	}
	
	private MolecularTypeLargeShape getShape(MolecularTypePattern mtpThat) {
		for(MolecularTypeLargeShape stls : speciesShapes) {
			MolecularTypePattern mtpThis = stls.getMolecularTypePattern();
			if(mtpThis == mtpThat) {
				return stls;
			}
		}
		return null;
	}
	
	public SpeciesPattern getSpeciesPattern() {
		return sp;
	}

	public int getX(){
		return xPos;
	}
	public int getY(){
		return yPos;
	}
	public int getWidth() {
		if(speciesShapes.isEmpty()) {
			return MolecularTypeLargeShape.getDummyWidth(shapePanel);
		}
		int width = 0;
		for(MolecularTypeLargeShape stls : speciesShapes) {
			width += stls.getWidth();
		}
		return width;
	}
	public int getRightEnd(){		// get the x of the right end of the species pattern
		if(speciesShapes.isEmpty()) {
			return xPos + MolecularTypeLargeShape.getDummyWidth(shapePanel);
		}
		int xRightmostMolecularType = 0;
		int widthRightmostMolecularType = 0;
		for(MolecularTypeLargeShape stls : speciesShapes) {
			int xCurrentMolecularType = stls.getX();
			if(xRightmostMolecularType < xCurrentMolecularType) {
				xRightmostMolecularType = xCurrentMolecularType;
				widthRightmostMolecularType = stls.getWidth();
			}
		}
		return xRightmostMolecularType + widthRightmostMolecularType;
	}

	public void addEndText(final String string) {
		this.endText = string;
	}
	
	@Override
	public boolean contains(PointLocationInShapeContext locationContext) {
		
		// first we check if the point is inside a subcomponent of "this"
		for(MolecularTypeLargeShape mts : speciesShapes) {
			boolean found = mts.contains(locationContext);
			if(found) {
				if(owner instanceof SpeciesContext && !((SpeciesContext)owner).hasSpeciesPattern()) {
					// special case: clicked inside plain species, we only want to allow the user to add a species pattern
					// we'll behave as if the user clicked outside the shape, which will bring the Add Molecule menu
					break;
				}
				// since point is inside one of our components it's also inside "this"
				locationContext.sps = this;
				return true;	// if the point is inside a MolecularTypeLargeShape there's no need to check others
			}
		}
		// even if the point it's not inside one of our subcomponents it may still be inside "this"
		int y = locationContext.point.y;
		if(height > 0 && y > yPos-3-nameOffset && y < yPos + height-2) {
			if(!(owner instanceof ReactionRule)) {
				// most entities have just 1 sp per row, so it's enough to check the y
				locationContext.sps = this;
				return true;
			} else {
				int x = locationContext.point.x;
				// for rules, more sp may be on the same row, so we need to also check x 
				if(x > xPos-xExtent && x < xPos + getWidth() + xExtent) {
					locationContext.sps = this;
					return true;
				}
			}
		}
		// for species contexts we can only have one single species pattern
		// anywhere you click inside the panel you select that species pattern
		if(owner instanceof SpeciesContext) {
			locationContext.sps = this;
			return true;
		}
		return false;
	}
	
	public void setMatchesSignature(boolean bMatchesSignature) {
		this.bMatchesSignature = bMatchesSignature;
		for(MolecularTypeLargeShape mtls : speciesShapes) {
			mtls.setMatchesSignature(bMatchesSignature);
		}
	}

	@Deprecated
	public void paintContour(Graphics g, Rectangle2D rect) {

		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
			
		Color paleBlue = Color.getHSBColor(0.6f, 0.05f, 1.0f);		// hue, saturation, brightness
		Color darkerBlue = Color.getHSBColor(0.6f, 0.12f, 1.0f);	// a bit darker for border

		g2.setPaint(paleBlue);
		g2.fill(rect);
		g2.setColor(darkerBlue);
		g2.draw(rect);

	    g2.setPaint(paintOld);
		g2.setColor(colorOld);
	}
	public void paintContour(Graphics g) {
		// we don't show contour when we display single row (view only, no edit)
		if(shapePanel == null) {
			return;
		}
		if(shapePanel instanceof RulesShapePanel && ((RulesShapePanel)shapePanel).isViewSingleRow()) {
			return;
		}
		
		if(height == -1) {
			height = defaultHeight;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
			
		Color paleBlue = Color.getHSBColor(0.6f, 0.05f, 1.0f);		// hue, saturation, brightness
		Color darkerBlue = Color.getHSBColor(0.6f, 0.12f, 1.0f);	// a bit darker for border
		Rectangle2D rect = new Rectangle2D.Double(xPos-xExtent, yPos-3-nameOffset, getWidth()+2*xExtent, height-2+nameOffset);
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
	    g2.setPaint(paintOld);
		g2.setColor(colorOld);
	}
	private void paintMatchesSignatureContour(Graphics g) {
		// we don't show contour when we display single row (view only, no edit)
		if(shapePanel == null) {
			return;
		}
		if(height == -1) {
			height = defaultHeight;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
			
		Color paleBlue = Color.getHSBColor(0.9f, 0.05f, 1.0f);		// hue, saturation, brightness
		Rectangle2D rect = new Rectangle2D.Double(xPos-11, yPos-6, getWidth()+xExtent+6, height-8);

		g2.setPaint(paleBlue);
		g2.fill(rect);
		g2.setColor(Color.red.darker());		// border
		g2.draw(rect);

	    g2.setPaint(paintOld);
		g2.setColor(colorOld);
	}
	public void paintCompartment(Graphics g) {

		Color structureColor = Color.black;
		Structure structure = null;
		if(owner instanceof ReactionRule && !speciesShapes.isEmpty()) {
			ReactionRule rr = (ReactionRule)owner;
			ReactantPattern rp = rr.getReactantPattern(sp);
			ProductPattern pp = rr.getProductPattern(sp);
			if(rp != null) {
				structure = rp.getStructure();
			} else if(pp != null) {
				structure = pp.getStructure();
			} else {
				structure = ((ReactionRule)owner).getStructure();
			}
		} else if(owner instanceof SpeciesContext && ((SpeciesContext)owner).hasSpeciesPattern()) {
				structure = ((SpeciesContext)owner).getStructure();
				structureColor = Color.gray;
		} else if(owner instanceof RbmObservable && !speciesShapes.isEmpty()) {
				structure = ((RbmObservable)owner).getStructure();			
		} else {
			return;		// other things don't have structure
		}
		
		// for whatever lacks a structure, we show it structureless
		if(structure == null || structure.getName() == null) {
			return;
		}
		
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
		Font fontOld = g2.getFont();
		
		Font font;
		int w;			// width of compartment shape, adjusted continuously based on zoom factor
		String name = structure.getName();
		int z = shapePanel.getZoomFactor();
		if(z > -3) {
			font = fontOld.deriveFont(Font.BOLD);
			g.setFont(font);
			w = 46+3*z;
			name = buildCompartmentName(g, name, "..", w);
		} else if(z < LargeShapePanel.SmallestZoomFactorWithText) {
			font = fontOld.deriveFont(fontOld.getSize2D()*0.8f);
			g.setFont(font);
			w = 20;
			name = buildCompartmentName(g, name, ".", w);
		} else {
			font = fontOld;
			g.setFont(font);
			w = 44+3*z;
			name = buildCompartmentName(g, name, "..", w);
		}
		
		Color darker = Color.gray;	// a bit darker for border
		Rectangle2D border = new Rectangle2D.Double(xPos-9, yPos-4, w, 58);
		g2.setColor(darker);
		g2.draw(border);
		
		Color lighter;
		if(structure.getTypeName().equals(Structure.TYPE_NAME_MEMBRANE)) {
			lighter = new Color(192, 192, 192);		// 192	208
		} else {
			lighter = new Color(240, 240, 240);		// 244
		}
		Rectangle2D filling = new Rectangle2D.Double(xPos-9, yPos-3, w, 57);
		g2.setPaint(lighter);
		g2.fill(filling);
		
		g.setColor(structureColor);
		g2.drawString(name, xPos-4, yPos+48);
		
		g2.setFont(fontOld);
	    g2.setPaint(paintOld);
		g2.setColor(colorOld);
	}
	private static String buildCompartmentName(Graphics g, String name, String fill, int w) {
		// we truncate and pad the compartment name at need so that it will fit inside the width of the compartment shape
		int effectiveWidth = g.getFontMetrics().stringWidth(name);
		int availableWidth = w-6;
		if(effectiveWidth < availableWidth) {
			return name;
		}
		String truncatedName;
		for(int i=1; i<name.length(); i++) {
			truncatedName = name.substring(0, name.length()-i) + fill;
			effectiveWidth = g.getFontMetrics().stringWidth(truncatedName);
			if(effectiveWidth < availableWidth) {
				return truncatedName;
			}
		}
		return ".";		// there may be some font which won't fit at all in which case we show just a dot
	}
	
	public void paintSelf(Graphics g) {
		paintSelf(g, true);
	}
	public void paintSelf(Graphics g, boolean bPaintContour) {
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(shapePanel instanceof ParticipantSignatureShapePanel) {
			if(bMatchesSignature == true) {
				paintMatchesSignatureContour(g);
			}
		} else if(bPaintContour && (owner instanceof RbmObservable || owner instanceof ReactionRule)) {
			paintContour(g);
		}
		
		paintCompartment(g);	// TODO: bring this back once we add compartments to species patterns
		
//		// type the expression of the species pattern right above the shape
//		if(owner instanceof RbmObservable) {
//			Color colorOld = g2.getColor();
//			Font fontOld = g.getFont();
//			Font font = MolecularComponentLargeShape.deriveComponentFontBold(graphicsContext);
//			Color fontColor = Color.gray;
//			g2.drawString("SpeciesPattern: " + sp.toString(), xPos, yPos-nameOffset/3);
//			g2.setFont(fontOld);
//			g2.setColor(colorOld);
//		} else if(owner instanceof ReactionRule) {
//			Color colorOld = g2.getColor();
//			Font fontOld = g.getFont();
//			Font font = MolecularComponentLargeShape.deriveComponentFontBold(graphicsContext);
//			Color fontColor = Color.gray;
//			g2.drawString("SpeciesPattern: " + sp.toString(), xPos+40, yPos-nameOffset/3);
//			g2.setFont(fontOld);
//			g2.setColor(colorOld);
//		}
		
		if(speciesShapes.isEmpty()) {		// paint empty dummy
			MolecularTypeLargeShape.paintDummy(g, xPos, yPos, shapePanel);
		}
		for(MolecularTypeLargeShape stls : speciesShapes) {
			stls.paintSelf(g);
		}
		if(owner instanceof RbmObservable) {
//			endText = "Right click here to add a molecule.";
			endText = "";
		}
		
//		// matches between molecular types - only within reaction rules
//		if(owner instanceof ReactionRule) {
//			Color colorOld = g2.getColor();
//			Font fontOld = g.getFont();
//
//			Font font = MolecularComponentLargeShape.deriveComponentFontBold(graphicsContext);
//			Color fontColor = Color.gray;
//			Color lineColor = Color.lightGray;
//			g2.setFont(font);
//			g2.setColor(fontColor);
//			for(MolecularTypeLargeShape mtls : speciesShapes) {
//				MolecularTypePattern mtp = mtls.getMolecularTypePattern();
//				if(!mtp.hasExplicitParticipantMatch()) {
//					continue;				// nothing to do if no explicit match
//				}
//				int x = mtls.getX()+10;
//				int y = mtls.getY()-7;
//				if(((ReactionRule)owner).isReactant(sp)) {
//					g2.drawLine(x, y, x, y+6);									// vertical line
//					g2.drawLine(x+1, y, x+1, y+6);
//					
//					g2.drawLine(x, y, x+10, y);									// line to right
//					g2.drawLine(x, y+1, x+10, y+1);
//					
//					g2.drawString(mtp.getParticipantMatchLabel(), x+12, y+4);
//				} else {
//					g2.drawLine(x, y, x, y+6);
//					g2.drawLine(x+1, y, x+1, y+6);
//
//					g2.drawLine(x, y, x-10, y);									// line to left
//					g2.drawLine(x, y+1, x-10, y+1);
//					
//					g2.drawString(mtp.getParticipantMatchLabel(), x+3, y+4);	// the match label
//				}
//			}
//			g2.setFont(fontOld);
//			g2.setColor(colorOld);
//		}
		
		// --- ------- Bonds between components ------------------------------------------------------------------------
		
		int yDouble = 18;					// initial height of vertical bar of matched bond
		if(shapePanel != null) {
			int Ratio = 1;	// arbitrary factor, to be determined
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;	// negative if going smaller
			yDouble += zoomFactor;
		}
		int yExists = 5;					// distance between 2 vertical line segments of "exists" bond
		int ysExists = 3;					// separation between 2 vertical line segments of "exists" bond
		if(shapePanel != null) {
			double zoomFactor = (double)(shapePanel.getZoomFactor())/3.0;	// negative if going smaller
			yExists += (int)zoomFactor;
			ysExists += (int)zoomFactor;
		}
		
		// bond related attributes
		final int xOneLetterOffset = 7;	// offset of the bond id - we assume there will never be more than 99
		final int xTwoLetterOffset = 13;
		int separ = 5;					// default y distance between 2 adjacent bars

		for(int i=0; i<bondSingles.size(); i++) {
			BondSingle bs = bondSingles.get(i);
			Color colorOld = g2.getColor();
			Font fontOld = g.getFont();
			
			Color fontColor = Color.red;
			Color lineColor = Color.red;
			if(AbstractComponentShape.isHidden(owner, bs.mcp)) {
				fontColor = Color.gray;
				lineColor = Color.lightGray;
			} else {
				fontColor = Color.black;
				lineColor = Color.gray;
			}
			
			if(bs.mcp.getBondType().equals(BondType.Possible)) {	//		?  (Possible)
				Graphics gc = shapePanel.getGraphics();
				
				if(shapePanel.getZoomFactor() >= LargeShapePanel.SmallestZoomFactorWithText) {
					Font font = MolecularComponentLargeShape.deriveComponentFontBold(gc, shapePanel);
					g2.setFont(font);
					g2.setColor(fontColor);
					g2.drawString(bs.mcp.getBondType().symbol, bs.from.x-xOneLetterOffset, bs.from.y+yLetterOffset);
				}
				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y, bs.from.x, bs.from.y+ysExists);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y, bs.from.x+1, bs.from.y+ysExists);

				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y+yExists, bs.from.x, bs.from.y+yExists+ysExists);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y+yExists, bs.from.x+1, bs.from.y+yExists+ysExists);

				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y+yExists*2, bs.from.x, bs.from.y+yExists*2+ysExists);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y+yExists*2, bs.from.x+1, bs.from.y+yExists*2+ysExists);

			} else if(bs.mcp.getBondType().equals(BondType.Exists)) {
//				g2.setColor(plusSignGreen);								// draw a green '+' sign
//				g2.drawLine(bs.from.x-8, bs.from.y+6, bs.from.x-3, bs.from.y+6);	// horizontal
//				g2.drawLine(bs.from.x-8, bs.from.y+7, bs.from.x-3, bs.from.y+7);
//				g2.drawLine(bs.from.x-6, bs.from.y+4, bs.from.x-6, bs.from.y+9);	// vertical
//				g2.drawLine(bs.from.x-5, bs.from.y+4, bs.from.x-5, bs.from.y+9);

				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y, bs.from.x, bs.from.y+yExists*2+ysExists);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y, bs.from.x+1, bs.from.y+yExists*2+ysExists);
			} else {
//				g2.setColor(Color.red.darker());									// draw a dark red '-' sign
//				g2.drawLine(bs.from.x-10, bs.from.y+5, bs.from.x-4, bs.from.y+5);	// horizontal
//				g2.drawLine(bs.from.x-10, bs.from.y+6, bs.from.x-4, bs.from.y+6);
				
//				g2.drawLine(bs.from.x-6, bs.from.y+4, bs.from.x-6, bs.from.y+9);	// vertical
//				g2.drawLine(bs.from.x-5, bs.from.y+4, bs.from.x-5, bs.from.y+9);

				// for BondType.None we show nothing at all
				// below small black vertical line ended in a red "x" (comment out if not wanted)
//				g2.setColor(lineColor);
//				g2.drawLine(bs.from.x, bs.from.y, bs.from.x, bs.from.y+7);
//				g2.setColor(Color.gray);
//				g2.drawLine(bs.from.x+1, bs.from.y, bs.from.x+1, bs.from.y+7);
//
//				int vo = 8;
//				g2.setColor(Color.red);
//				g2.drawLine(bs.from.x-3, bs.from.y+2+vo, bs.from.x+4, bs.from.y-2+vo);
//				g2.setColor(Color.gray);
//				g2.drawLine(bs.from.x-3, bs.from.y+3+vo, bs.from.x+4, bs.from.y-1+vo);
//				
//				g2.setColor(Color.red);
//				g2.drawLine(bs.from.x-3, bs.from.y-2+vo, bs.from.x+4, bs.from.y+2+vo);
//				g2.setColor(Color.gray);
//				g2.drawLine(bs.from.x-3, bs.from.y-1+vo, bs.from.x+4, bs.from.y+3+vo);
				
				// below small black vertical line (comment out if not wanted)
//				int vo = 10;
//				g2.setColor(lineColor);
//				g2.drawLine(bs.from.x, bs.from.y, bs.from.x, bs.from.y+vo);
//				g2.setColor(Color.gray);
//				g2.drawLine(bs.from.x+1, bs.from.y, bs.from.x+1, bs.from.y+vo);
//				
				// small horizontal red line at the end of the vertical one
//				g2.setColor(Color.gray);
//				g2.drawLine(bs.from.x-1, bs.from.y+vo-1, bs.from.x+2, bs.from.y+vo-1);
//				g2.setColor(Color.gray);
//				g2.drawLine(bs.from.x-1, bs.from.y+vo, bs.from.x+2, bs.from.y+vo);
			}
			g.setFont(fontOld);
			g2.setColor(colorOld);
		}
		
		switch(bondPairs.size()) {	// variable distance on y between bonds, we draw them closer when there are many of them
		case 1:
		case 2:
			separ = 5; 
			break;
		case 3:	
		case 4:
			separ = 4;
			break;
		case 5:	
		case 6:
			separ = 3;
			break;
		case 7:
		case 8:
		case 9:
			separ = 2;
			break;
		default:
			separ = 1;
		}
		for(int i=0; i<bondPairs.size(); i++) {
			BondPair bp = bondPairs.get(i);
			
			Color colorOld = g2.getColor();
			Font fontOld = g.getFont();
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if(shapePanel.isShowDifferencesOnly()) {
				g2.setColor(Color.gray);
			} else {
				g2.setColor(RbmTreeCellRenderer.bondHtmlColors[bp.id]);
			}
			g2.drawLine(bp.from.x, bp.from.y, bp.from.x, bp.from.y+yDouble+i*separ);
			g2.drawLine(bp.to.x, bp.to.y, bp.to.x, bp.to.y+yDouble+i*separ);
			g2.drawLine(bp.from.x, bp.from.y+yDouble+i*separ, bp.to.x, bp.to.y+yDouble+i*separ);
			
			Graphics gc = shapePanel.getGraphics();
			if(shapePanel.getZoomFactor() >= LargeShapePanel.SmallestZoomFactorWithText) {
				Font font = MolecularComponentLargeShape.deriveComponentFontBold(gc, shapePanel);
				g.setFont(font);
				String nr = bp.id+"";
				if(nr.length()<2) {
					g2.drawString(nr, bp.from.x-xOneLetterOffset, bp.from.y+yLetterOffset);
					g2.drawString(nr, bp.to.x-xOneLetterOffset, bp.to.y+yLetterOffset);
				} else {
					g2.drawString(nr, bp.from.x-xTwoLetterOffset, bp.from.y+yLetterOffset);
					g2.drawString(nr, bp.to.x-xTwoLetterOffset, bp.to.y+yLetterOffset);
				}
			}

			g2.setColor(Color.lightGray);
			g2.drawLine(bp.from.x+1, bp.from.y+1, bp.from.x+1, bp.from.y+yDouble+i*separ);
			g2.drawLine(bp.to.x+1, bp.to.y+1, bp.to.x+1, bp.to.y+yDouble+i*separ);
			g2.drawLine(bp.from.x, bp.from.y+yDouble+i*separ+1, bp.to.x+1, bp.to.y+yDouble+i*separ+1);
			
			g.setFont(fontOld);
			g2.setColor(colorOld);
		}
		if(!endText.isEmpty()) {
			g.drawString(endText, getRightEnd() + 20, yPos + 20);
		}
	}

	@Override
	public void setHighlight(boolean b, boolean param) {
		// param is always ignored
		// TODO: actually I need to look at the owner, sp may be null for a plain species (green circle)
		// or for errors (where we display a red circle)
		if(sp == null) {
			shapePanel.sp = null;
			return;
		}
		shapePanel.sp = b ? sp : null;
	}
	@Override
	public boolean isHighlighted() {
		if(sp == null) {
			// TODO:   ADD CODE HERE?, see above
			return false;
		}
		return shapePanel.isHighlighted(sp);
	}
	@Override
	public void turnHighlightOffRecursive(Graphics g) {
		shapePanel.resetSpeciesPattern();
			paintSelf(g);			// paint self not highlighted
	}

	public void flash(String matchKey) {
		for(MolecularTypeLargeShape mtls : speciesShapes) {
			mtls.flash(matchKey);
		}
	}
	
	@Override
	public String getDisplayName() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String getDisplayType() {
		return SpeciesPattern.typeName;
	}

}
