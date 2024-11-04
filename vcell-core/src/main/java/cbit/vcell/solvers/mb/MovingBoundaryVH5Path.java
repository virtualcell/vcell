package cbit.vcell.solvers.mb;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

import cbit.vcell.export.server.JhdfUtils;
import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.CastingUtils;
import org.vcell.util.VCAssert;


/**
 * Class to recursively parse HDF5 file seeking requested data
 * This fails quietly if path is invalid. It supports attributes as last element of the path,
 * and simple compound and scalar HDF5 types
 *
 * @author GWeatherby
 */
public class MovingBoundaryVH5Path {

    protected Object target;
    protected Exception exc;
    protected static final Logger lg = LogManager.getLogger(MovingBoundaryVH5Path.class);


    /**
     * @param g     staring point, not null
     * @param names path to search
     */
    public MovingBoundaryVH5Path(HdfFile hdfFile, Group g, String... names){
        target = null;
        exc = null;
        try {
            target = walk(hdfFile, g, names, 0);
        } catch(Exception e){
            exc = e;
            if(lg.isWarnEnabled()){
                lg.warn("Error retrieving " + concat(names), exc);
            }
        }
    }

    /**
     * @return located data or null if not found
     */
    public Object getData(){
        return target;
    }

    /**
     * @return description of type found, or "fail" if not found
     */
    public String foundType(){
        if(target != null){
            return target.getClass().getSimpleName();
        }
        return "fail";
    }

    /**
     * @return true if no exception occurred
     */
    public boolean isGood(){
        return exc == null;

    }

    /**
     * @return exception stored while processing, or null if none
     */
    public Exception getExc(){
        return exc;
    }


    /**
     * @param names not null
     * @return names as single path
     */
    public static String concat(String[] names){
        Objects.requireNonNull(names);
        return StringUtils.join(names, '/');
    }

    /**
     * concat names and indicate specific element
     *
     * @param names
     * @param current
     * @return concat(names) + current
     */
    protected static String concat(String[] names, String current){
        return concat(names) + ", element " + current;
    }

    protected static String className(Object obj){
        if(obj != null){
            return className(obj.getClass());
        }
        return "null";
    }

    protected static String className(Class<?> clzz){
        if(clzz != null){
            return clzz.getSimpleName();
        }
        return "null";
    }

    /**
     * @param index
     * @param steps non-null
     * @return true if index refers to last element in steps
     */
    private static boolean lastIndex(int index, String[] steps){
        return index + 1 == steps.length;
    }

    /**
     * find next object in sequence
     *
     * @param node  previous element in sequence
     * @param steps name of each step
     * @param index current step
     * @return next object path, if present
     * @throws HDF5Exception
     */
    private static Object walk(HdfFile hdfFile, Node node, String[] steps, int index) {
        final boolean isLastIndex = lastIndex(index, steps);
        final String finding = steps[index];
        if(node instanceof Group g){
            List<Node> nodes = JhdfUtils.getChildren(hdfFile, g);
            for(Node sub : nodes){
//				String p = sub.getPath();
//				String name = sub.getName();
//				String full = sub.getFullName();
                if(finding.equals(sub.getName())){
                    if(isLastIndex){
                        return sub;
                    }
                    return walk(hdfFile, sub, steps, index + 1);
                }
            }
        }
        if (node instanceof Dataset cds && cds.isCompound()) {
            String[] mn = JhdfUtils.getCompoundDatasetMemberNames(cds);

            for(int i = 0; i < mn.length; i++){
                if(finding.equals(mn[i])){
                    Object c = cds.getData();
                    Vector<?> vec = CastingUtils.downcast(Vector.class, c);
                    if(vec != null){
                        VCAssert.assertTrue(i < vec.size(), "Disconnect between H5CompoundDS.getMemberNames( )  and returned Vector");
                        Object child = vec.get(i);
                        if(isLastIndex){
                            return child;
                        }
                    } else {
                        throw new UnsupportedOperationException("Unsupported H5CompoundDS subtype " + className(c));
                    }
                }
            }

        }
        if(isLastIndex){
            DataFormat df = CastingUtils.downcast(DataFormat.class, hobj);
            if(df != null && df.hasAttribute()){
                try {
                    @SuppressWarnings("unchecked")
                    List<Object> meta = df.getMetadata();
                    for(Object o : meta){
                        Attribute a = CastingUtils.downcast(Attribute.class, o);
                        if(a != null){
                            if(finding.equals(a.getName())){
                                return a.getValue();
                            }
                        } else {
                            lg.warn(concat(steps, finding) + " fetching metadata unexpected type " + className(o));
                        }

                    }
                } catch(Exception e){
                    throw new RuntimeException(concat(steps, finding) + " fetching metadata", e);
                }
            }
        }

        return null;
    }

}
