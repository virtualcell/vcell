package cbit.vcell.graph;

import cbit.vcell.mapping.MolecularInternalLinkSpec;
import cbit.vcell.mapping.SiteAttributesSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import org.vcell.model.rbm.*;
import org.vcell.util.Coordinate;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;
import org.vcell.util.springsalad.NamedColor;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

public class SpeciesContextSpecLargeShape extends AbstractComponentShape implements HighlightableShapeInterface {

    public class CustomLine2D extends Line2D.Double {

        private static final double TOLERANCE = 3.0;    // tolerance for detecting clicks near the line
        public CustomLine2D(double x1, double y1, double x2, double y2) {
            super(x1, y1, x2, y2);
        }

        @Override
        public boolean contains(double x, double y) {
            double distance = ptSegDist(x, y);          // calculate distance to the line segment
            return distance <= TOLERANCE;
        }
    }

    private static final double NmToPixelRatio = 25;
    private static final double DEFAULT_UPPER_CORNER = 3;       // default screen coordinates where we want to display the first site
    private static final double DEFAULT_LEFT_CORNER = 10;       // in nm

    // x, y positions where we want to begin drawing the shape (nm from top and left of painting area)
    private double x_offset = DEFAULT_LEFT_CORNER;
    private double y_offset = DEFAULT_UPPER_CORNER;
    private double nmToPixelRatio = NmToPixelRatio;

//    private int nameOffset = 0;	// offset upwards from yPos where we may write some text, like the expression of the sp

    final LargeShapeCanvas shapePanel;

    private Displayable owner;      // it's the same scs

    private SpeciesContextSpec scs;
    private SpeciesContext sc = null;
    private SpeciesPattern sp = null;
    private MolecularTypePattern mtp = null;
    private Structure structure = null;

    private boolean hasAnchor = false;
    private MolecularComponentPattern mcpAnchor = null;
    private double membraneX = 0;
    private double membraneY = 0;
    private double membraneRadius = 0;
    private boolean hasExtracellularSite = false;
    private boolean hasMembraneSite = false;
    private boolean hasIntracellularSite = false;

    // we use these to compute some offset from top and left, so that the molecule will look nicely centered on screen
    private double minX = 0;            // coordinate of the leftmost site
    private MolecularComponentPattern leftmostSite = null;
    private double minY = 0;            // coordinate of the topmost site
    private MolecularComponentPattern topmostSite = null;   // if more sites qualify we just keep the first we find
    private double maxX = 0;            // coordinate of the rightmost site
    private double maxY = 0;            // coordinate of the bottommost site

    Map<Ellipse2D, SiteAttributesSpec> ellipseToSasMap = new LinkedHashMap<>();         // site oval to site attributes map
    Map<CustomLine2D, MolecularInternalLinkSpec> lineToMilsMap = new LinkedHashMap<>(); // link line to link spec

    private MolecularComponentPattern mcpSelected = null;   // any or both of these may be selected in their tables
    private MolecularInternalLinkSpec milsSelected = null;

    public SpeciesContextSpecLargeShape(SpeciesContextSpec scs, LargeShapeCanvas shapePanel, Displayable owner,
                                        MolecularComponentPattern mcpSelected, MolecularInternalLinkSpec milsSelected,
                                        IssueListProvider issueListProvider) {
        super(issueListProvider);

        this.owner = owner;
        this.scs = scs;
        this.shapePanel = shapePanel;

        this.mcpSelected = mcpSelected;
        this.milsSelected = milsSelected;

        if(scs != null) {
            this.sc = scs.getSpeciesContext();
        }
        if(sc != null) {
            this.sp = sc.getSpeciesPattern();
            this.structure = sc.getStructure();
        }
        if(sp != null) {
            if(sp.getMolecularTypePatterns().size() != 1) {
                return;
            }
            this.mtp = sp.getMolecularTypePatterns().get(0);
        } else {
            return;
        }

        Map<MolecularComponentPattern, SiteAttributesSpec> sasMap = scs.getSiteAttributesMap();
        Set<MolecularInternalLinkSpec> ilSet = scs.getInternalLinkSet();
        MolecularType mt = mtp.getMolecularType();
        int counter = 0;    // site counter
        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
            MolecularComponent mc = mcp.getMolecularComponent();
            SiteAttributesSpec sas = sasMap.get(mcp);
            Structure structure = sas.getLocation();
            if(structure.getName().equals(Structure.SpringStructureEnum.Extracellular.columnName)) {
                hasExtracellularSite = true;
            } else if(structure.getName().equals(Structure.SpringStructureEnum.Intracellular.columnName)) {
                hasIntracellularSite = true;
            }
            if(mc.getName().equals(SpeciesContextSpec.AnchorSiteString)) {
                hasAnchor = true;
                mcpAnchor = mcp;
                hasMembraneSite = true;
                membraneX = sasMap.get(mcp).getZ();
                membraneY = sasMap.get(mcp).getY();
                membraneRadius = sasMap.get(mcp).getRadius();
            }
            Coordinate coordinate = sas.getCoordinate();
            double x = coordinate.getZ();
            double y = coordinate.getY();
            if(counter == 0) {
                minX = x;
                minY = y;
                leftmostSite = mcp;
                topmostSite = mcp;
                maxX = x;
                maxY = y;
            } else {
                if(x < minX) {
                    minX = x;
                    leftmostSite = mcp;
                }
                if(y < minY) {
                    minY = y;
                    topmostSite = mcp;
                }
                if(x > maxX) {
                    maxX = x;
                }
                if(y > maxY) {
                    maxY = y;
                }
            }
            counter++;
        }
        // now compute the offsets, we want the sites nicely centered on screen
        // UPPER_CORNER, LEFT_CORNER
        x_offset = DEFAULT_LEFT_CORNER - minX;
        y_offset = DEFAULT_UPPER_CORNER - minY;
    }

    private boolean isPlanarYZ() {    // we only show entities that are 2D in the YZ plane
        Map<MolecularComponentPattern, SiteAttributesSpec> sasMap = scs.getSiteAttributesMap();
        // here we could either iterate through the sasMap, or through the components of the mtp
        // we use the second method because it provides a sanity check between the components in the sasMap (application level)
        // and the physiology (which is authoritative)
        double oldX = 0;    // dummy value to indulge the compiler
        for(int i=0; i< mtp.getComponentPatternList().size(); i++) {
            MolecularComponentPattern mcp = mtp.getComponentPatternList().get(i);
            SiteAttributesSpec sas = sasMap.get(mcp);
            Structure structure = sas.getLocation();
            Coordinate coordinate = sas.getCoordinate();
            // to be planar in YZ, the X coordinates of all sites must be equal
            double x = coordinate.getX();   // here x means x
            if(i==0) {
                oldX = x;   // first pass
            }
            if(oldX != x) {
                return false;
            }
        }
        return true;
    }

    public Dimension getMaxSize() {
        int z = shapePanel.getZoomFactor();
        nmToPixelRatio = NmToPixelRatio + z;
        int width = (int) (nmToPixelRatio * (x_offset+maxX+10));
        int height = (int) (nmToPixelRatio * (y_offset+maxY+6));
        return new Dimension(width, height);
    }

    private void paintDummy(Graphics g) {
        return;     // implement later
    }
    private void paintCompartments(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;
        Color colorOld = g2.getColor();
        Paint paintOld = g2.getPaint();
        Font fontOld = g2.getFont();
        RenderingHints hintsOld = g2.getRenderingHints();
        Stroke strokeOld = g2.getStroke();

        Font font;
        int z = shapePanel.getZoomFactor();
        nmToPixelRatio = NmToPixelRatio + z;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String name = structure.getName();
        if(z > -3) {
            font = fontOld.deriveFont(Font.BOLD);
            g.setFont(font);

        } else {
            font = fontOld;
            g.setFont(font);
        }

        g.setColor(Color.black);
        if(!hasMembraneSite) {
            double x = minX+x_offset;
            double y = 1;
            g2.drawString(name, (int)(nmToPixelRatio * x), (int)(nmToPixelRatio * y));
        } else {
            double x = membraneX+x_offset-5;
            double y = 1;
            g2.drawString("Extracellular", (int)(nmToPixelRatio * x), (int)(nmToPixelRatio * y));

            x = membraneX+x_offset;
            y = 1;
            g2.drawString("Membrane       Intracellular",
                    (int)(nmToPixelRatio * x),
//                    (int)(nmToPixelRatio * (x-membraneRadius)),     // move it slightly to the left
                    (int)(nmToPixelRatio * y));

            g2.setColor(NamedColor.darker(Color.orange, 0.8));
            float thickness = 4.0f;
            g2.setStroke(new BasicStroke(thickness));
            g2.drawLine((int)(nmToPixelRatio * x),      // centered on the Anchor
                    (int)(nmToPixelRatio * (y+1)),
                    (int)(nmToPixelRatio * x),
                    (int)(nmToPixelRatio * (maxY+y_offset+6)));
        }
        g2.setStroke(strokeOld);
        g2.setRenderingHints(hintsOld);
        g2.setFont(fontOld);
        g2.setPaint(paintOld);
        g2.setColor(colorOld);
    }

    private void paintAxes(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        Color colorOld = g2.getColor();
        Paint paintOld = g2.getPaint();
        Font fontOld = g2.getFont();
        RenderingHints hintsOld = g2.getRenderingHints();
        Stroke strokeOld = g2.getStroke();

        Font font;
        int z = shapePanel.getZoomFactor();
        nmToPixelRatio = NmToPixelRatio + z;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(z > -3) {
            font = fontOld.deriveFont(Font.BOLD);
            g.setFont(font);
        } else {
            font = fontOld;
            g.setFont(font);
        }

        int startX = 15;                            // coordinates for the arrow line (z-axis)
        int startY = 15;
        int endX = 60;
        int endY = 15;
        g2.setColor(Color.black);
        g2.drawString("Z", endX+5, endY+3);    // coord name
        g2.setColor(Color.gray);
        g2.drawLine(startX, startY, endX, endY);    // draw the arrow line
        int arrowSize = 10;                         // draw the arrow head
        int[] xPoints = {endX, endX - arrowSize, endX - arrowSize};
        int[] yPoints = {endY, endY - arrowSize, endY + arrowSize};
        g2.fillPolygon(xPoints, yPoints, 3);

        startX = 15;                                // coordinates for the arrow line (Y-axis)
        startY = 15;
        endX = 15;
        endY = 60;
        g2.setColor(Color.black);
        g2.drawString("Y", endX-2, endY+12);
        g2.setColor(Color.gray);
        g2.drawLine(startX, startY, endX, endY);
        int[] xPoints2 = {endX, endX - arrowSize, endX + arrowSize};
        int[] yPoints2 = {endY, endY - arrowSize, endY - arrowSize};
        g2.fillPolygon(xPoints2, yPoints2, 3);

        startX = 15;                                // coordinates for the arrow line (Y-axis)
        startY = 100;
        endX = 15 + (int)nmToPixelRatio;
        int offset = 3;
        g2.setColor(Color.black);
        float thickness = 2.0f;
        g2.setStroke(new BasicStroke(thickness));
        g2.drawLine(startX, startY, endX, startY);
        g2.drawLine(startX, startY-offset, startX, startY+offset);
        g2.drawLine(endX, startY-offset, endX, startY+offset);
        g2.setStroke(strokeOld);
        g2.drawString("1 nm", endX+10, startY+offset);

        if(isPlanarYZ() == false) {
            startX = 15;                                // coordinates for the arrow line (Y-axis)
            startY = 130;
            g2.setColor(Color.red.darker());
            g2.setStroke(strokeOld);
            g2.setFont(fontOld);
            FontMetrics fm = g2.getFontMetrics();
            String regularText = "The molecule is ";
            g2.drawString(regularText, startX, startY);
            int regularTextWidth = fm.stringWidth(regularText);
            font = fontOld.deriveFont(Font.BOLD);
            g2.setFont(font);
            g2.drawString("3D", startX + regularTextWidth, startY);
        }

        g2.setStroke(strokeOld);
        g2.setRenderingHints(hintsOld);
        g2.setFont(fontOld);
        g2.setPaint(paintOld);
        g2.setColor(colorOld);
    }

    public Ellipse2D drawCenteredCircle(Graphics g, NamedColor namedColor, double xd, double yd, double rd,
                                        boolean isSelected) {
        Graphics2D g2 = (Graphics2D)g;
        Color oldColor = g.getColor();
        Stroke strokeOld = g2.getStroke();
        RenderingHints hintsOld = g2.getRenderingHints();

        int z = shapePanel.getZoomFactor();
        nmToPixelRatio = NmToPixelRatio + z;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        xd = xd-(rd/2);
        yd = yd-(rd/2);

        int x = (int)(nmToPixelRatio * xd);
        int y = (int)(nmToPixelRatio * yd);
        int r = (int)(nmToPixelRatio * rd);

        Ellipse2D oval = new Ellipse2D.Double(x, y, r, r);
        g2.setColor(namedColor.getColor());
        g2.fill(oval);

        if(isSelected) {
            g2.setStroke(new BasicStroke(3.6f));
            g2.setColor(Color.darkGray);
        } else {
            g2.setStroke(new BasicStroke(1.4f));
            g2.setColor(Color.black);
        }
        g2.draw(oval);     // black contour

        g2.setStroke(strokeOld);
        g2.setRenderingHints(hintsOld);
        g.setColor(oldColor);

        return oval;
    }

    public CustomLine2D drawLink(Graphics g, double x1d, double y1d, double x2d, double y2d, boolean isSelected) {

        Graphics2D g2 = (Graphics2D)g;
        Color oldColor = g.getColor();
        RenderingHints hintsOld = g2.getRenderingHints();
        Stroke strokeOld = g2.getStroke();

        int z = shapePanel.getZoomFactor();
        nmToPixelRatio = NmToPixelRatio + z;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x1 = (int)(nmToPixelRatio * x1d);
        int y1 = (int)(nmToPixelRatio * y1d);
        int x2 = (int)(nmToPixelRatio * x2d);
        int y2 = (int)(nmToPixelRatio * y2d);

        g.setColor(Color.darkGray);
        if(isSelected) {
            g2.setStroke(new BasicStroke(3.6f));
        } else {
            g2.setStroke(new BasicStroke(1.4f));
        }

        CustomLine2D line = new CustomLine2D(x1, y1, x2, y2);
        g2.draw(line);

        g2.setStroke(strokeOld);
        g2.setRenderingHints(hintsOld);
        g.setColor(oldColor);

        return line;
    }

    public void paintSelf(Graphics g, boolean bPaintContour) {
        // activate the following 3 lines to prevent display if the object is 3D (x coordinates differ from each other)
//        if(isPlanarYZ() == false) {
//            return;
//        }
        paintCompartments(g);
        paintAxes(g);
        if(mtp == null || mtp.getComponentPatternList().size() == 0) {		// paint empty dummy
            paintDummy(g);
            return;
        }
        Map<MolecularComponentPattern, SiteAttributesSpec> sasMap = scs.getSiteAttributesMap();
        Set<MolecularInternalLinkSpec> internalLinkSet = scs.getInternalLinkSet();
        for(MolecularInternalLinkSpec mils : internalLinkSet) {
            Pair<MolecularComponentPattern, MolecularComponentPattern> link = mils.getLink();
            SiteAttributesSpec sas1 = sasMap.get(link.one);
            SiteAttributesSpec sas2 = sasMap.get(link.two);
            double x1 = x_offset + sas1.getCoordinate().getZ();
            double x2 = x_offset + sas2.getCoordinate().getZ();
            double y1 = y_offset + sas1.getCoordinate().getY();
            double y2 = y_offset + sas2.getCoordinate().getY();
            boolean isSelected = mils == milsSelected ? true : false;
            CustomLine2D line = drawLink(g, x1, y1, x2, y2, isSelected);
            lineToMilsMap.put(line, mils);
        }
        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
            SiteAttributesSpec sas = sasMap.get(mcp);
            Coordinate coord = sas.getCoordinate();
            double radius = sas.getRadius();
            NamedColor color = sas.getColor();
            double x = x_offset + coord.getZ();
            double y = y_offset + coord.getY();
            boolean isSelected = mcp == mcpSelected ? true : false;
            Ellipse2D oval = drawCenteredCircle(g, color, x, y, radius, isSelected);
            ellipseToSasMap.put(oval, sas);
        }
    }



    public Object contains(Point point) {

        for (Map.Entry<Ellipse2D, SiteAttributesSpec> entry : ellipseToSasMap.entrySet()) {
            Ellipse2D oval = entry.getKey();
            SiteAttributesSpec sas = entry.getValue();
            if(oval.contains(point)) {
                return sas;
            }
        }
        for (Map.Entry<CustomLine2D, MolecularInternalLinkSpec> entry : lineToMilsMap.entrySet()) {
            CustomLine2D line = entry.getKey();
            MolecularInternalLinkSpec mils = entry.getValue();
            if(line.contains(point.getX(), point.getY())) {
                return mils;
            }
        }
        return null;
    }

    @Override
    public void paintSelf(Graphics g) {
        paintSelf(g, true);
    }
    @Override
    public boolean isHighlighted() {
        return false;
    }
    @Override
    public void setHighlight(boolean highlight, boolean param) {
    }
    @Override
    public void turnHighlightOffRecursive(Graphics g) {
    }

    @Override
    public String getDisplayName() {
        return null;
    }
    @Override
    public String getDisplayType() {
        return null;
    }

}
