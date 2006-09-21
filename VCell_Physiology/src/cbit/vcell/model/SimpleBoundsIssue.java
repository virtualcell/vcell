package cbit.vcell.model;
import cbit.util.Issue;
import net.sourceforge.interval.ia_math.RealInterval;
import cbit.vcell.model.Parameter;
/**
 * Insert the type's description here.
 * Creation date: (11/1/2005 9:32:59 AM)
 * @author: Jim Schaff
 */
public class SimpleBoundsIssue extends Issue {
	private RealInterval bounds = null;

/**
 * SimpleBoundsIssue constructor comment.
 * @param argSource java.lang.Object
 * @param argCategory java.lang.String
 * @param argMessage java.lang.String
 * @param argSeverity int
 */
public SimpleBoundsIssue(Parameter argParameter, RealInterval argBounds, String argMessage) {
	super(argParameter, "Parameter Bounds", argMessage, Issue.SEVERITY_BUILTIN_CONSTRAINT);
	if (argBounds==null){
		throw new IllegalArgumentException("parameter bounds cannot be null");
	}
	this.bounds = argBounds;
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:37:13 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 */
public RealInterval getBounds() {
	return bounds;
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:36:23 AM)
 * @return cbit.vcell.model.Parameter
 */
public cbit.vcell.model.Parameter getParameter() {
	return (Parameter)getSource();
}
}