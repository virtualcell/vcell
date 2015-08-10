package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
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

import cbit.vcell.client.desktop.biomodel.RbmTreeCellRenderer;
import cbit.vcell.model.ReactionRule;

public class SpeciesPatternLargeShape extends AbstractComponentShape {

	public static final int yLetterOffset = 11;			// y position of Bond id and/or State name
	public static final int separationWidth = 1;		// width between 2 molecular type patterns
	
	private int xPos = 0;
	private int yPos = 0;
	private int height = -1;	// -1 means it doesn't matter or that we can compute it from the shape + "tallest" bond
	private List<MolecularTypeLargeShape> speciesShapes = new ArrayList<MolecularTypeLargeShape>();

	final Graphics graphicsContext;
	
	private Displayable owner;
	private SpeciesPattern sp;
	private String endText = new String();	// we display this after the Shape, it's position is outside "width"
	private boolean isError = false;
	
	List <BondSingle> bondSingles = new ArrayList <BondSingle>();	// component with no explicit bond
	List <BondPair> bondPairs = new ArrayList <BondPair>();

	// this is only used to display an error in the ViewGeneratedSpeciespanel
	public SpeciesPatternLargeShape(int xPos, int yPos, int height, Graphics graphicsContext, boolean isError) {
		this.owner = null;
		this.sp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.height = height;
		this.graphicsContext = graphicsContext;
		this.isError = true;

		int xPattern = xPos;
		MolecularTypeLargeShape stls = new MolecularTypeLargeShape(xPattern, yPos, graphicsContext, null);
		speciesShapes.add(stls);
	}
	public SpeciesPatternLargeShape(int xPos, int yPos, int height, SpeciesPattern sp, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.sp = sp;
		this.xPos = xPos;
		this.yPos = yPos;
		this.height = height;
		this.graphicsContext = graphicsContext;

		int xPattern = xPos;
		if(sp == null) {
			// plain species context, no pattern
			MolecularTypeLargeShape stls = new MolecularTypeLargeShape(xPattern, yPos, graphicsContext, owner);
			speciesShapes.add(stls);
			return;
		}
		
		int numPatterns = sp.getMolecularTypePatterns().size();
		for(int i = 0; i<numPatterns; i++) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(i);
			MolecularTypeLargeShape stls = new MolecularTypeLargeShape(xPattern, yPos, mtp, graphicsContext, owner);
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
	public int getRightEnd(){		// get the x of the right end of the species pattern
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
				// since point is inside one of our components it's also inside "this"
				locationContext.sps = this;
				return true;	// if the point is inside a MolecularTypeLargeShape there's no need to check others
			}
		}
		
		// even if the point it's not inside one of our subcomponents it may still be inside "this"
		int y = locationContext.point.y;
		if(height > 0 && y > yPos && y < yPos + height) {
			locationContext.sps = this;
			return true;
		}
		return false;		// for the case of SpeciesPatternLargeShape, it actually can't
	}

	public void paintSelf(Graphics g) {
		final int offset = 18;			// initial height of vertical bar
		final int separ = 5;			// y distance between 2 adjacent bars
		
		final int xOneLetterOffset = 7;	// offset of the bond id - we assume there will never be more than 99
		final int xTwoLetterOffset = 13;

		for(MolecularTypeLargeShape stls : speciesShapes) {
			stls.paintSelf(g);
		}
		
		// matches between molecular types - only within reaction rules
		if(owner instanceof ReactionRule) {
			Graphics2D g2 = (Graphics2D)g;
			Color colorOld = g2.getColor();
			Font fontOld = g.getFont();
			Font font = MolecularComponentLargeShape.deriveComponentFontBold(graphicsContext);
			Color fontColor = Color.gray;
			Color lineColor = Color.lightGray;
			g2.setFont(font);
			g2.setColor(fontColor);
			for(MolecularTypeLargeShape mtls : speciesShapes) {
				MolecularTypePattern mtp = mtls.getMolecularTypePattern();
				if(!mtp.hasExplicitParticipantMatch()) {
					continue;				// nothing to do if no explicit match
				}
				int x = mtls.getX()+10;
				int y = mtls.getY()-7;
				if(((ReactionRule)owner).isReactant(sp)) {					// line to right
					g2.drawLine(x, y, x, y+6);
					g2.drawLine(x+1, y, x+1, y+6);
					
					g2.drawLine(x, y, x+10, y);
					g2.drawLine(x, y+1, x+10, y+1);
					
					g2.drawString(mtp.getParticipantMatchLabel(), x+12, y+4);
				} else {
					g2.drawLine(x, y, x, y+6);
					g2.drawLine(x+1, y, x+1, y+6);

					g2.drawLine(x, y, x-10, y);
					g2.drawLine(x, y+1, x-10, y+1);
					
					g2.drawString(mtp.getParticipantMatchLabel(), x+3, y+4);
				}
			}
			g2.setFont(fontOld);
			g2.setColor(colorOld);
		}

		// bonds between components
		for(int i=0; i<bondSingles.size(); i++) {
			BondSingle bs = bondSingles.get(i);
			Graphics2D g2 = (Graphics2D)g;
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
			
			if(bs.mcp.getBondType().equals(BondType.Possible)) {
				Font font = MolecularComponentLargeShape.deriveComponentFontBold(graphicsContext);
				g2.setFont(font);
				g2.setColor(fontColor);
				g2.drawString(bs.mcp.getBondType().symbol, bs.from.x-xOneLetterOffset, bs.from.y+yLetterOffset);
				
				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y, bs.from.x, bs.from.y+3);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y, bs.from.x+1, bs.from.y+3);

				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y+5, bs.from.x, bs.from.y+8);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y+5, bs.from.x+1, bs.from.y+8);

				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y+10, bs.from.x, bs.from.y+13);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y+10, bs.from.x+1, bs.from.y+13);

			} else if(bs.mcp.getBondType().equals(BondType.Exists)) {
				g2.setColor(plusSignGreen);								// draw a green '+' sign
				g2.drawLine(bs.from.x-8, bs.from.y+6, bs.from.x-3, bs.from.y+6);	// horizontal
				g2.drawLine(bs.from.x-8, bs.from.y+7, bs.from.x-3, bs.from.y+7);
				g2.drawLine(bs.from.x-6, bs.from.y+4, bs.from.x-6, bs.from.y+9);	// vertical
				g2.drawLine(bs.from.x-5, bs.from.y+4, bs.from.x-5, bs.from.y+9);

				g2.setColor(lineColor);
				g2.drawLine(bs.from.x, bs.from.y, bs.from.x, bs.from.y+13);
				g2.setColor(Color.gray);
				g2.drawLine(bs.from.x+1, bs.from.y, bs.from.x+1, bs.from.y+13);
			} else {
				// for BondType.None we show nothing at all
				// below commented out: small line ended in a red "x"
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
			}
			g.setFont(fontOld);
			g2.setColor(colorOld);
		}
		for(int i=0; i<bondPairs.size(); i++) {
			BondPair bp = bondPairs.get(i);
			
			Graphics2D g2 = (Graphics2D)g;
			Color colorOld = g2.getColor();
			Font fontOld = g.getFont();
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2.setColor(RbmTreeCellRenderer.bondHtmlColors[bp.id]);
			g2.drawLine(bp.from.x, bp.from.y, bp.from.x, bp.from.y+offset+i*separ);
			g2.drawLine(bp.to.x, bp.to.y, bp.to.x, bp.to.y+offset+i*separ);
			g2.drawLine(bp.from.x, bp.from.y+offset+i*separ, bp.to.x, bp.to.y+offset+i*separ);
			
			Font font = MolecularComponentLargeShape.deriveComponentFontBold(graphicsContext);
//			Font font = fontOld.deriveFont((float) (MolecularComponentLargeShape.componentDiameter/2));
			g.setFont(font);
			String nr = bp.id+"";
			if(nr.length()<2) {
				g2.drawString(nr, bp.from.x-xOneLetterOffset, bp.from.y+yLetterOffset);
				g2.drawString(nr, bp.to.x-xOneLetterOffset, bp.to.y+yLetterOffset);
			} else {
				g2.drawString(nr, bp.from.x-xTwoLetterOffset, bp.from.y+yLetterOffset);
				g2.drawString(nr, bp.to.x-xTwoLetterOffset, bp.to.y+yLetterOffset);
			}

			g2.setColor(Color.lightGray);
			g2.drawLine(bp.from.x+1, bp.from.y+1, bp.from.x+1, bp.from.y+offset+i*separ);
			g2.drawLine(bp.to.x+1, bp.to.y+1, bp.to.x+1, bp.to.y+offset+i*separ);
			g2.drawLine(bp.from.x, bp.from.y+offset+i*separ+1, bp.to.x+1, bp.to.y+offset+i*separ+1);
			
			g.setFont(fontOld);
			g2.setColor(colorOld);
		}
		if(!endText.isEmpty()) {
			g.drawString(endText, getRightEnd() + 15, yPos + 20);
		}
	}
}
