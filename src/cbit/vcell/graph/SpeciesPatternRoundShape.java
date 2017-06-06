package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;

import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model.RbmModelContainer;

public class SpeciesPatternRoundShape extends AbstractComponentShape implements HighlightableShapeInterface {

	class MolecularTypeRoundShape {
		
		private static final int radius = SpeciesPatternRoundShape.radius/2+1;
		private int xPos = 0;		// upper left corner of the square containing the shape - the x position
		private int yPos = 0;		// as above - the y position
		public final int index;		// index of this mtp inside the sp.
		
		final LargeShapePanel shapePanel;
		private Displayable owner;
		private MolecularTypePattern mtp;

		public MolecularTypeRoundShape(int xPos, int yPos, MolecularTypePattern mtp, LargeShapePanel shapePanel, Displayable owner, int index) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.shapePanel = shapePanel;
			this.owner = owner;
			this.mtp = mtp;
			this.index = index;
		}

		public int getWidth() {
			return xPos + 2*radius;
		}
		public int getX() {
			return xPos;
		}
		public MolecularTypePattern getMolecularTypePattern() {
			return mtp;
		}
		
		public void paintSelf(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			Font fontOld = g2.getFont();
			Color colorOld = g2.getColor();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Color primaryColor = null;
			MolecularType mt = mtp.getMolecularType();
			RbmModelContainer rbmmc = mt.getModel().getRbmModelContainer();
			List<MolecularType> mtList = rbmmc.getMolecularTypeList();
			int index = mtList.indexOf(mt);
			final Color colorTable[] = MolecularTypeLargeShape.colorTable;
			index = index%7;
			switch(index) {
//			case 0:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker(); break;
//			case 1:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker(); break;
//			case 2:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker(); break;
//			case 3:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker(); break;
//			case 4:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker(); break;
//			case 5:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker(); break;
//			case 6:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker(); break;
//			default: primaryColor = isHighlighted() == true ? Color.white : Color.blue.darker(); break;
			case 0:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker().darker(); break;
			case 1:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker().darker(); break;
			case 2:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker().darker(); break;
			case 3:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker().darker(); break;
			case 4:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker().darker(); break;
			case 5:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker().darker(); break;
			case 6:  primaryColor = isHighlighted() == true ? Color.white : colorTable[index].darker().darker(); break;
			default: primaryColor = isHighlighted() == true ? Color.white : Color.blue.darker().darker(); break;
			}
			
			
			Point2D center = new Point2D.Float(xPos+radius*2/3, yPos+radius*2/3);
			Point2D focus = new Point2D.Float(xPos+radius*2/3-1, yPos+radius*2/3-1);
			float[] dist = {0.1f, 1.0f};
			Color[] colors = {Color.white, primaryColor};
//			Color[] colors = {primaryColor.brighter().brighter().brighter(), primaryColor};
//			Color[] colors = {primaryColor.brighter().brighter().brighter(), primaryColor.darker()};
//			Color[] colors = {primaryColor.brighter().brighter(), Color.darkGray};
//			Color[] colors = {primaryColor.brighter().brighter().brighter(), Color.darkGray};
			RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
			g2.setPaint(p);
			Ellipse2D circle = new Ellipse2D.Double(xPos, yPos, 2*radius, 2*radius);
			g2.fill(circle);
			Ellipse2D circle2 = new Ellipse2D.Double(xPos-1, yPos-1, 2*radius, 2*radius);
			g2.setPaint(Color.DARK_GRAY);
			g2.draw(circle2);

			g.setFont(fontOld);
			g.setColor(colorOld);
			
		}
	}
	
	public static final int radius = 18;	// this is the only thing that needs scaling
	
	// left and right extension of the sp, used for reactions only
	// clicking within these limits still means we're inside that sp
	private static final int XExtent = 20;
	public final int xExtent;
	
	private int xPos = 0;
	private int yPos = 0;		// y position where we draw the square containing the shape
	
	private int nameOffset = 0;	// offset upwards from yPos where we may write some text, like the expression of the sp
	private int height = -1;	// -1 means it doesn't matter or that we can compute it from the shape + "tallest" bond
	private List<MolecularTypeRoundShape> speciesShapes = new ArrayList<MolecularTypeRoundShape>();
	private Set<Pair<Integer, Integer>> bondsSet = new HashSet<>();

	final LargeShapePanel shapePanel;
	
	private Displayable owner;
	private SpeciesPattern sp;
	private String endText = new String();	// we display this after the Shape, it's position is outside "width"
	private boolean isError = false;
	
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
	public SpeciesPatternRoundShape(int xPos, int yPos, int height, LargeShapePanel shapePanel, 
			boolean isError, IssueManager issueManager) {
		super(issueManager);
		
		this.owner = null;
		this.sp = null;
		this.xPos = xPos;
		this.yPos = yPos;
		this.height = height;
		this.shapePanel = shapePanel;
		this.isError = true;
		
		xExtent = calculateXExtent(shapePanel);

		int xPattern = xPos;
	}
		
	public SpeciesPatternRoundShape(int xPos, int yPos, int height, SpeciesPattern sp, LargeShapePanel shapePanel, 
			Displayable owner, IssueManager issueManager) {
		super(issueManager);
		
		this.owner = owner;
		this.sp = sp;
		this.xPos = xPos;
		this.yPos = yPos;
		this.shapePanel = shapePanel;
		xExtent = calculateXExtent(shapePanel);
		this.height = height;

		if(sp == null) {
			// plain species context, no pattern
			return;
		}
		int numPatterns = sp.getMolecularTypePatterns().size();
		double angle = 0;
		for(int i = 0; i<numPatterns; i++) {
			
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(i);
			if(i>0) {
				MolecularType mtPrev = sp.getMolecularTypePatterns().get(i-1).getMolecularType();
				MolecularType mtCur = mtp.getMolecularType();
				if(mtPrev == mtCur) {
					angle += 20;
				} else {
					angle += 45;
				}
			}
			double x = Math.cos(Math.toRadians(angle)) * radius + xPos + radius - MolecularTypeRoundShape.radius;
			double y = Math.sin(Math.toRadians(angle)) * radius + yPos + radius - MolecularTypeRoundShape.radius;
			MolecularTypeRoundShape mtrs = new MolecularTypeRoundShape((int)x, (int)y, mtp, shapePanel, owner, i);
			speciesShapes.add(mtrs);
		}
		//
		// bonds - we draw a simplified set
		// for any number of bonds uniting any 2 mtp we'll draw just one bond
		//
		for(int i=0; i<numPatterns; i++) {

			MolecularTypeRoundShape mtrsFrom = speciesShapes.get(i);
			MolecularTypePattern mtpFrom = mtrsFrom.getMolecularTypePattern();
			int numComponents = mtpFrom.getComponentPatternList().size();
			for(int j=0; j<numComponents; j++) {

				MolecularComponent mcFrom = mtpFrom.getMolecularType().getComponentList().get(j);
				MolecularComponentPattern mcpFrom = mtpFrom.getMolecularComponentPattern(mcFrom);
				if(mcpFrom.getBondType().equals(BondType.Specified)) {
					Bond b = mcpFrom.getBond();

					MolecularTypePattern mtpTo = b.molecularTypePattern;
					MolecularTypeRoundShape mtrsTo = getShape(mtpTo);

					if(mtrsFrom.index <= mtrsTo.index) {
						Pair<Integer, Integer> p = new Pair<>(mtrsFrom.index, mtrsTo.index);
						bondsSet.add(p);
					}
				}
			}
		}
	}
	
	
	private MolecularTypeRoundShape getShape(MolecularTypePattern mtpThat) {
		for(MolecularTypeRoundShape stls : speciesShapes) {
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
		for(MolecularTypeRoundShape stls : speciesShapes) {
			width += stls.getWidth();
		}
		return width;
	}
	public int getRightEnd(){		// get the x of the right end of the species pattern
		return xPos + 2 * radius + MolecularTypeRoundShape.radius;
	}

	public void addEndText(final String string) {
		this.endText = string;
	}
	
	@Override
	public boolean contains(PointLocationInShapeContext locationContext) {
		
//		// first we check if the point is inside a subcomponent of "this"
//		for(MolecularTypeLargeShape mts : speciesShapes) {
//			boolean found = mts.contains(locationContext);
//			if(found) {
//				if(owner instanceof SpeciesContext && !((SpeciesContext)owner).hasSpeciesPattern()) {
//					// special case: clicked inside plain species, we only want to allow the user to add a species pattern
//					// we'll behave as if the user clicked outside the shape, which will bring the Add Molecule menu
//					break;
//				}
//				// since point is inside one of our components it's also inside "this"
//				locationContext.sps = this;
//				return true;	// if the point is inside a MolecularTypeLargeShape there's no need to check others
//			}
//		}
//		// even if the point it's not inside one of our subcomponents it may still be inside "this"
//		int y = locationContext.point.y;
//		if(height > 0 && y > yPos-3-nameOffset && y < yPos + height-2) {
//			if(!(owner instanceof ReactionRule)) {
//				// most entities have just 1 sp per row, so it's enough to check the y
//				locationContext.sps = this;
//				return true;
//			} else {
//				int x = locationContext.point.x;
//				// for rules, more sp may be on the same row, so we need to also check x 
//				if(x > xPos-xExtent && x < xPos + getWidth() + xExtent) {
//					locationContext.sps = this;
//					return true;
//				}
//			}
//		}
//		// for species contexts we can only have one single species pattern
//		// anywhere you click inside the panel you select that species pattern
//		if(owner instanceof SpeciesContext) {
//			locationContext.sps = this;
//			return true;
//		}
		return false;
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
			height = radius*2;
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
		
		if(structure == null) {
			return;
		}
		// the fake flattened reactions we show in Specifications / Network Constraints / View
		// don't have a real structure, so we show them structureless (for now)
		if(structure.getName() == null) {
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
		Color lighter = new Color(224, 224, 224);
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
		Font fontOld = g2.getFont();
		Color colorOld = g2.getColor();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
//		if(bPaintContour && (owner instanceof RbmObservable || owner instanceof ReactionRule)) {
//			paintContour(g);
//		}
//		paintCompartment(g);	// TODO: bring this back once we add compartments to species patterns
//		
//		if(speciesShapes.isEmpty()) {		// paint empty dummy
//			MolecularTypeLargeShape.paintDummy(g, xPos, yPos, shapePanel);
//		}

		
//		g2.setPaint(Color.green.darker().darker());
//		Ellipse2D circle = new Ellipse2D.Double(xPos, yPos, 2*radius, 2*radius);
//		g2.fill(circle);
		
		
		// paint the bonds first as circles
		// as we keep painting other shapes over the bonds, all the unwanted parts of the bonds will get hidden
		//
//		for(Pair<Integer, Integer> p : bondsSet) {
//			
//			MolecularTypeRoundShape mtrsFrom = speciesShapes.get(p.one);
//			MolecularTypeRoundShape mtrsTo = speciesShapes.get(p.two);
//			
////			Point p1 = new Point(mtrsFrom.xPos + MolecularTypeRoundShape.radius, mtrsFrom.yPos + MolecularTypeRoundShape.radius);
////			Point p2 = new Point(mtrsTo.xPos + MolecularTypeRoundShape.radius, mtrsTo.yPos + MolecularTypeRoundShape.radius);
//			Point p1 = new Point(mtrsFrom.xPos, mtrsFrom.yPos);
//			Point p2 = new Point(mtrsTo.xPos, mtrsTo.yPos);
//			
//			int dx = Math.abs(p1.x - p2.x);
//			int dy = Math.abs(p1.y - p2.y);
//			double len = Math.sqrt(Math.pow((double)dx, 2) + Math.pow((double)dy, 2));
//			
//			Point m = new Point((p1.x+p2.x)/2, (p1.y+p2.y)/2);
////			Point c = new Point(m.x+dy-MolecularTypeRoundShape.radius, m.y+dx-MolecularTypeRoundShape.radius);
//			Point c = new Point(m.x+dy, m.y+dx);
//			
//			
////			Ellipse2D circle2 = new Ellipse2D.Double(c.x, c.y, len, len);
//			Ellipse2D circle2 = new Ellipse2D.Double(c.x-8, c.y-8, len+9, len+9);
//			g2.setPaint(Color.gray);
//			g2.draw(circle2);
//
//			
//		}
		
		
		
		Ellipse2D circle2 = new Ellipse2D.Double(xPos, yPos, 2*radius, 2*radius);
		g2.setPaint(Color.lightGray);
		g2.draw(circle2);

		g.setFont(fontOld);
		g.setColor(colorOld);

		
		for(MolecularTypeRoundShape mtls : speciesShapes) {
			mtls.paintSelf(g);
		}
		
		
		if(owner instanceof RbmObservable) {
//			endText = "Right click here to add a molecule.";
			endText = "";
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
