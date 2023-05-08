package cbit.vcell.export.server;

import cbit.vcell.simdata.SpatialSelection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.BeanUtils;

import java.util.LinkedList;
import java.util.List;

public class GeometrySliceSpecs extends GeometrySpecs {
    private final static Logger lg = LogManager.getLogger(GeometrySliceSpecs.class);
    public GeometrySliceSpecs(SpatialSelection[] selections, int axis, int sliceNumber) {
        super(selections, axis, sliceNumber);
    }

    /**
     * Insert the method's description here.
     * Creation date: (4/2/2001 12:04:55 AM)
     * @return boolean
     * @param object java.lang.Object
     */
    public boolean equals(Object object) {
        return object instanceof GeometrySliceSpecs && super.equals(object);
    }

    /**
     * This method was created in VisualAge.
     * @return cbit.vcell.simdata.gui.SpatialSelection[]
     */
    public SpatialSelection[] getCurves() {
        int count = 0;
        List<SpatialSelection> curves = new LinkedList<>();
        SpatialSelection[] selections = this.getSelections();
        // Note: I changed this method to no longer call this.getSelections() constantly, and to just call it once.
        // 		It looks to be a safe change; If this is, in fact, problematic...***revert*** it back!

        for (SpatialSelection selection : selections){
            if (isSinglePoint(selection)) continue;
            curves.add(selection);
        }

        return curves.toArray(new SpatialSelection[0]);
    }


    /**
     * Insert the method's description here.
     * Creation date: (3/2/2001 9:38:49 PM)
     * @return cbit.vcell.geometry.CoordinateIndex[]
     */
    public int[] getPointIndexes() {
        throw new RuntimeException("GeometrySpecs.getPointIndexes() shouldn't be called for a Geometry Slice");
    }
    public int getPointCount(){
        throw new RuntimeException("GeometrySpecs.getPointCount() shouldn't be called for a Geometry Slice");
    }
    public SpatialSelection[] getPointSpatialSelections(){
        throw new RuntimeException("GeometrySpecs.getPoints() shouldn't be called for a Geometry Slice");
    }

    /**
     * This method was created in VisualAge.
     * @return cbit.vcell.simdata.gui.SpatialSelection[]
     */
    public SpatialSelection[] getSelections() {
        List<SpatialSelection> spatialSelections = new LinkedList<>();
        // If we're already good to go, don't repeat
        if (this.serializedSelections == null || this.spatialSelections != null) return this.spatialSelections;
        // Otherwise, let's get deserializing
        for (byte[] selection : this.serializedSelections){
            try {
                spatialSelections.add((SpatialSelection)BeanUtils.fromSerialized(selection));
            } catch (Exception exc) {
                lg.error("Error deserializing selections in Geometry Specs:", exc);
                return null;
            }
        }
        this.spatialSelections = spatialSelections.toArray(new SpatialSelection[0]);
        return this.spatialSelections;
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/2/2001 4:33:23 PM)
     * @return int
     */
    public int hashCode() {
        return this.toString().hashCode();
    }


    /**
     * Insert the method's description here.
     * Creation date: (4/2/2001 4:23:04 PM)
     * @return java.lang.String
     */
    public String toString() {
        int modeID = ExportConstants.GEOMETRY_SLICE;
        StringBuilder buf = new StringBuilder();
        buf.append("GeometrySliceSpecs [");
        buf.append("axis: ").append(this.axis).append(", ");
        buf.append("sliceNumber: ").append(this.sliceNumber).append(", ");
        buf.append("spatialSelections: ");
        if (this.serializedSelections != null) {
            buf.append("{");
            SpatialSelection[] selections = this.getSelections();
            for (int i = 0; i < selections.length; i++){
                buf.append(selections);
                if (i < selections.length - 1) buf.append(",");
            }
            buf.append("}");
        } else {
            buf.append("null");
        }
        buf.append(", modeID: ").append(modeID).append("]");
        return buf.toString();
    }
}

