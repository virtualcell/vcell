package cbit.vcell.graph.gui;

import javax.swing.*;

public class SsldLargeShapePanel extends JPanel {

//    private boolean showDifferencesOnly = false;
//    private boolean bShowMoleculeColor = false;
//    private boolean bShowNonTrivialOnly = false;


    // zooming the shape, 0 means normal size, a negative number means smaller shape
    private static final int SmallestZoomFactor = -7;			// -7 is the smallest where the shapes scale decently well
    private static final int DefaultZoomFactor = 0;
    private static final int LargestZoomFactor = 0;
    private int zoomFactor = DefaultZoomFactor;

    // by default the shapes are editable and their border and text is black / gray, aso
    // otherwise they are a shade of brown, very much alike the DefaultScrollTableCellRenderer.uneditableForeground
    private boolean editable = true;



//    public void setShowMoleculeColor(boolean bShowMoleculeColor) {
//        this.bShowMoleculeColor = bShowMoleculeColor;
//    }
//    public void setShowNonTrivialOnly(boolean bShowNonTrivialOnly) {
//        this.bShowNonTrivialOnly = bShowNonTrivialOnly;
//    }


    public boolean zoomLarger() {	// returns false when upper limit was reached
        zoomFactor++;
        if(zoomFactor >= LargestZoomFactor) {
            zoomFactor = LargestZoomFactor;
            System.out.println("MAX. Factor is " + zoomFactor);
            return false;
        } else {
            System.out.println("Up. Factor is " + zoomFactor);
            return true;
        }
    }
    public void setZoomFactor(int newZoomFactor) {
        if(newZoomFactor > LargestZoomFactor || newZoomFactor < SmallestZoomFactor) {
            throw new RuntimeException("Large Shape Panel zoom factor is out of bounds");
        }
        zoomFactor = newZoomFactor;
    }
    public boolean zoomSmaller() {	// returns false when lower limit was reached
        zoomFactor--;
        if(zoomFactor <= SmallestZoomFactor) {
            zoomFactor = SmallestZoomFactor;
            System.out.println("MIN. Factor is " + zoomFactor);
            return false;
        } else {
            System.out.println("Down. Factor is " + zoomFactor);
            return true;
        }
    }
    public boolean isLargestZoomFactor() {
        return zoomFactor >= LargestZoomFactor ? true : false;
    }
    public boolean isSmallestZoomFactor() {
        return zoomFactor <= SmallestZoomFactor ? true : false;
    }
    public int getZoomFactor() {
        return zoomFactor;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public boolean isEditable() {
        return editable;
    }

}
