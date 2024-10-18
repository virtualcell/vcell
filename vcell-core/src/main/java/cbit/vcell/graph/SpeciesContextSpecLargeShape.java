package cbit.vcell.graph;

import cbit.vcell.mapping.MolecularInternalLinkSpec;
import cbit.vcell.mapping.SiteAttributesSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import org.vcell.model.rbm.*;
import org.vcell.util.Coordinate;
import org.vcell.util.Displayable;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SpeciesContextSpecLargeShape extends AbstractComponentShape implements HighlightableShapeInterface {

    private static final double nmToPixelRatio = 10;

    private int xPos = 0;       // TODO: we may not need these since we compute aitomatically offset, to nicely center the molecule
    private int yPos = 0;		// y position where we draw the shape (pixels from top and left of painting area)

//    private int nameOffset = 0;	// offset upwards from yPos where we may write some text, like the expression of the sp

    final LargeShapeCanvas shapePanel;

    private Displayable owner;      // it's the same scs

    private SpeciesContextSpec scs;
    private SpeciesContext sc = null;
    private SpeciesPattern sp = null;
    private MolecularTypePattern mtp = null;

    private boolean hasAnchor = false;
    private MolecularComponentPattern mcpAnchor = null;
    private boolean hasExtracellularSite = false;
    private boolean hasMembraneSite = false;
    private boolean hasIntracellularSite = false;

    // we use these to compute some offset from top and left, so that the molecule will look nicely centered on screen
    private double minX = 0;            // coodrdinate of the leftmost site
    private MolecularComponentPattern leftmostSite = null;
    private double minY = 0;            // coordinate of the topmost site
    private MolecularComponentPattern topmostSite = null;   // if more sites qualify we just keep the first we find


    public SpeciesContextSpecLargeShape(int xPos, int yPos,
                                        int height,     // we may not need this
                                        SpeciesContextSpec scs,
                LargeShapeCanvas shapePanel, Displayable owner, IssueListProvider issueListProvider) {
        super(issueListProvider);

        this.owner = owner;
        this.scs = scs;
        this.xPos = xPos;
        this.yPos = yPos;
//        this.height = height;
        this.shapePanel = shapePanel;

        if(scs != null) {
            this.sc = scs.getSpeciesContext();
        }
        if(this.sc != null) {
            this.sp = this.sc.getSpeciesPattern();
        }
        if(this.sp != null) {
            if(this.sp.getMolecularTypePatterns().size() != 1) {
                throw new RuntimeException("Number of Molecules must be exactly 1");
            }
            this.mtp = this.sp.getMolecularTypePatterns().get(0);
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
            }
            Coordinate coordinate = sas.getCoordinate();
            double x = coordinate.getZ();
            double y = coordinate.getY();
            if(counter == 0) {
                minX = x;
                minY = y;
                leftmostSite = mcp;
                topmostSite = mcp;
            } else {
                if(x < minX) {
                    minX = x;
                    leftmostSite = mcp;
                }
                if(y < minY) {
                    minY = y;
                    topmostSite = mcp;
                }
            }
            counter++;
        }
    }

    private boolean isPlanarYZ() {    // we only show entities that are 2D in the YZ plane
        return true;    // TODO: check
    }


    private void paintDummy(Graphics g, int xPos, int yPos) {
        return;     // implement later
    }
    private void paintCompartments(Graphics g) {
        return;     // implement later
    }
    private void paintAxes(Graphics g) {
        return;     // implement later
    }
    public void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);

        Color oldColor = g.getColor();
        g.setColor(Color.black);
        g.drawOval(x, y, r, r);
        g.setColor(oldColor);
    }
    public void paintSelf(Graphics g, boolean bPaintContour) {

        if(isPlanarYZ() == false) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();

        paintCompartments(g);
        paintAxes(g);
        if(mtp == null || mtp.getComponentPatternList().size() == 0) {		// paint empty dummy
            paintDummy(g, xPos, yPos);
        }

        Map<MolecularComponentPattern, SiteAttributesSpec> sasMap = scs.getSiteAttributesMap();
        MolecularType mt = mtp.getMolecularType();
        for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
//        for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMap.entrySet()) {
//            MolecularComponentPattern mcp = entry.getKey();
//            SiteAttributesSpec sas = entry.getValue();
            MolecularComponent mc = mcp.getMolecularComponent();
            SiteAttributesSpec sas = sasMap.get(mcp);
            Coordinate coord = sas.getCoordinate();
            double radius = sas.getRadius();
            Color color = sas.getColor().getColor();
            g.setColor(color);
            drawCenteredCircle((Graphics2D)g,
                    (int)(nmToPixelRatio * coord.getZ()),   // transform nm to pixels
                    (int)(nmToPixelRatio * coord.getY()),
                    (int)(nmToPixelRatio * radius));
        }
        g.setColor(oldColor);
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
