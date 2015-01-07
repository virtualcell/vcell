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

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Displayable;

import cbit.vcell.client.desktop.biomodel.RbmTreeCellRenderer;

public class SpeciesPatternShape {

	private static final int separationWidth = 10;		// width between 2 molecular type patterns
	private int xPos = 0;
	private int yPos = 0;
	private List<SpeciesTypeLargeShape> speciesShapes = new ArrayList<SpeciesTypeLargeShape>();

	final Graphics graphicsContext;
	
	private Displayable owner;
	private SpeciesPattern sp;
	
	class BondPair implements Comparable {
		int id;
		Point from;
		Point to;
		public BondPair(int id, Point from, Point to) {
			this.id = id;
			this.from = from;
			this.to = to;
		}
		@Override
		public int compareTo(Object o) {
			if(o instanceof BondPair) {
				BondPair that = (BondPair)o;
				int thisLength = to.x - from.x;
				int thatLength = that.to.x - that.from.x;
				if(thisLength < thatLength) {
					return -1;
				} else {
					return 1;
				}
			}
			return 0;
		}
	}
	List <BondPair> bondPairs = new ArrayList <BondPair>();

	
	public SpeciesPatternShape(int xPos, int yPos, SpeciesPattern sp, Graphics graphicsContext, Displayable owner) {
		this.owner = owner;
		this.sp = sp;
		this.xPos = xPos;
		this.yPos = yPos;
		this.graphicsContext = graphicsContext;

		int xPattern = xPos;
		if(sp == null) {
			SpeciesTypeLargeShape stls = new SpeciesTypeLargeShape(xPattern, yPos, graphicsContext, owner);
			speciesShapes.add(stls);
			return;
		}
		
		int numPatterns = sp.getMolecularTypePatterns().size();
		for(int i = 0; i<numPatterns; i++) {
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(i);
			SpeciesTypeLargeShape stls = new SpeciesTypeLargeShape(xPattern, yPos, mtp, graphicsContext, owner);
			xPattern += stls.getWidth() + separationWidth; 
			speciesShapes.add(stls);
		}
		
		// bonds - we have to deal with them here because they may be cross-molecular type patterns
		// WARNING: we assume that the order of the Species Type Large Shapes in speciesShapes 
		// is the same as the order of the Molecular Type Patterns in the SpeciesPattern sp
		for(int i=0; i<numPatterns; i++) {
			SpeciesTypeLargeShape stlsFrom = speciesShapes.get(i);
			MolecularTypePattern mtpFrom = stlsFrom.getMolecularTypePattern();
			int numComponents = mtpFrom.getComponentPatternList().size();
			for(int j=0; j<numComponents; j++) {
				MolecularComponentLargeShape mclsFrom = stlsFrom.getComponentShape(j);
				MolecularComponentPattern mcpFrom = mtpFrom.getComponentPatternList().get(j);
				if(mcpFrom.getBondType().equals(BondType.Specified)) {
					Bond b = mcpFrom.getBond();
					if(b == null) {		// it's half of a bond at this time, we skip it for now
						System.out.println("Null bond for " + mcpFrom.getMolecularComponent().getDisplayName());
						break;
					}
					MolecularTypePattern mtpTo = b.molecularTypePattern;
					SpeciesTypeLargeShape stlsTo = getShape(mtpTo); 
					MolecularComponentPattern mcpTo = b.molecularComponentPattern;
					MolecularComponentLargeShape mclsTo = stlsTo.getShape(mcpTo);
					
					Point from = new Point(mclsFrom.getX()+mclsFrom.getWidth()/2, mclsFrom.getY()+mclsFrom.getHeight());
					Point to = new Point(mclsTo.getX()+mclsTo.getWidth()/2, mclsTo.getY()+mclsFrom.getHeight());
					if(from.x < to.x) {		// the bonds with from.x > to.x are duplicates
						BondPair bp = new BondPair(mcpFrom.getBondId(), from, to);
						bondPairs.add(bp);
					}
				}
			}
		}
		Collections.sort(bondPairs);
	}
	
	private SpeciesTypeLargeShape getShape(MolecularTypePattern mtpThat) {
		for(SpeciesTypeLargeShape stls : speciesShapes) {
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
	
	public void paintSelf(Graphics g) {
		for(SpeciesTypeLargeShape stls : speciesShapes) {
			stls.paintSelf(g);
		}
		for(int i=0; i<bondPairs.size(); i++) {
			BondPair bp = bondPairs.get(i);
			final int offset = 14;			// initial lenth of vertical bar
			final int separ = 6;			// y distance between 2 adjacent bars
			
			final int xOneLetterOffset = 7;	// offset of the bond id - we assume there will never be more than 99
			final int xTwoLetterOffset = 13;
			final int yLetterOffset = 10;
			
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
	}
	
}
