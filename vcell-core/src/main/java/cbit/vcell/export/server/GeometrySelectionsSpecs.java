package cbit.vcell.export.server;

import cbit.vcell.simdata.SpatialSelection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GeometrySelectionsSpecs  extends GeometrySpecs {
    private final static Logger lg = LogManager.getLogger(GeometrySelectionsSpecs.class);
    public GeometrySelectionsSpecs(SpatialSelection[] selections, int axis, int sliceNumber) {
        super(selections, axis, sliceNumber);
    }

    /**
     * Insert the method's description here.
     * Creation date: (4/2/2001 12:04:55 AM)
     * @return boolean
     * @param object java.lang.Object
     */
    public boolean equals(Object object) {
        return object instanceof GeometrySelectionsSpecs && super.equals(object);
    }


    /**
     * Insert the method's description here.
     * Creation date: (3/2/2001 9:38:49 PM)
     * @return cbit.vcell.geometry.CoordinateIndex[]
     */
    public int[] getPointIndexes() {
        List<Integer> points = new ArrayList<>();
        for (SpatialSelection selection : this.getSelections()) {
            if (!GeometrySpecs.isSinglePoint(selection)) continue;
            points.add(selection.getIndex(0));
        }
        return points.stream().mapToInt(Integer::intValue).toArray();
    }
    public int getPointCount(){
        int count = 0;
        for (SpatialSelection selection : this.getSelections()) {
            if (!GeometrySpecs.isSinglePoint(selection)) continue;
            count++;
        }
        return count;
    }
    public SpatialSelection[] getPointSpatialSelections(){
        List<SpatialSelection> pointSpatialSelections = new LinkedList<>();
        for (SpatialSelection selection : this.getSelections()){
            if (!GeometrySpecs.isSinglePoint(selection)) continue;
            pointSpatialSelections.add(selection);
        }
        return pointSpatialSelections.toArray(new SpatialSelection[0]);
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
        StringBuilder buf = new StringBuilder();
        buf.append("GeometrySelectionsSpecs [");
        buf.append("axis: ").append(this.axis).append(", ");
        buf.append("sliceNumber: ").append(this.sliceNumber).append(", ");
        buf.append("spatialSelections: ");
        if(this.serializedSelections == null) return buf.append("null").append("]").toString();

        List<String> stringSelections = new LinkedList<>();
        for (SpatialSelection selection : this.getSelections()){
            stringSelections.add(selection.toString());
        }
        return buf.append("{").append(String.join(", ", stringSelections)).append("}").append("]").toString();
    }
}
