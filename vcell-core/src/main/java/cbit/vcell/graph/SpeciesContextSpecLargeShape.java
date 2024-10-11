package cbit.vcell.graph;

import cbit.vcell.mapping.SiteAttributesSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.SpeciesContext;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Coordinate;
import org.vcell.util.Displayable;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpeciesContextSpecLargeShape extends AbstractComponentShape implements HighlightableShapeInterface {


    private int xPos = 0;
    private int yPos = 0;		// y position where we draw the shape
    private int nameOffset = 0;	// offset upwards from yPos where we may write some text, like the expression of the sp
    private int height = -1;	// -1 means it doesn't matter or that we can compute it from the shape + "tallest" bond

    final LargeShapeCanvas shapePanel;

    private Displayable owner;      // it's the same scs

    private SpeciesContextSpec scs;
    private SpeciesContext sc = null;
    private SpeciesPattern sp = null;
    private MolecularTypePattern mtp = null;

    boolean isPlanarYZ = true;  // we only show entities that are 2D in the YZ plane



    public SpeciesContextSpecLargeShape(int xPos, int yPos, int height, SpeciesContextSpec scs,
                LargeShapeCanvas shapePanel, Displayable owner, IssueListProvider issueListProvider) {
        super(issueListProvider);

        this.owner = owner;
        this.scs = scs;
        this.xPos = xPos;
        this.yPos = yPos;
        this.height = height;
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

        if(!isPlanarYZ) {
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

        Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = scs.getSiteAttributesMap();
        for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : siteAttributesMap.entrySet()) {
            MolecularComponentPattern mcp = entry.getKey();
            SiteAttributesSpec sas = entry.getValue();
            Coordinate coord = sas.getCoordinate();
            double radius = sas.getRadius();
            Color color = sas.getColor().getColor();
            g.setColor(color);
            drawCenteredCircle((Graphics2D)g, 10*(int)coord.getZ(), 10*(int)coord.getY(), 10*(int)radius);
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
