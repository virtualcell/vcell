package cbit.vcell.solvers.mb;

import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.CastingUtils;
import org.vcell.util.VCAssert;

import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;

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
    public MovingBoundaryVH5Path(Group g, String... names){
        target = null;
        exc = null;
        try {
            target = walk(g, names, 0);
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
     * @param hobj  previous element in sequence
     * @param steps name of each step
     * @param index current step
     * @return next object path, if present
     */
    private static Object walk(Object hobj, String[] steps, int index) throws Exception{
        final boolean isLastIndex = lastIndex(index, steps);
        final String finding = steps[index];
        Group g = CastingUtils.downcast(Group.class, hobj);
        if(g != null){
            Node sub = g.getChildren().get(finding);
            if(sub != null){
                if(isLastIndex){
                    return sub;
                }
                return walk(sub, steps, index + 1);
            }
        }
        Dataset ds = CastingUtils.downcast(Dataset.class, hobj);
        if(ds != null && ds.isCompound() && isLastIndex){
            // a compound dataset's members are addressed by name, like a group's children
            Object data = ds.getData();
            Map<?, ?> members = CastingUtils.downcast(Map.class, data);
            if(members == null){
                throw new UnsupportedOperationException("Unsupported compound dataset subtype " + className(data));
            }
            if(members.containsKey(finding)){
                return members.get(finding);
            }
        }
        if(isLastIndex){
            Node node = CastingUtils.downcast(Node.class, hobj);
            if(node != null){
                Attribute a = node.getAttribute(finding);
                if(a != null){
                    return a.getData();
                }
            }
        }

        return null;
    }

}
