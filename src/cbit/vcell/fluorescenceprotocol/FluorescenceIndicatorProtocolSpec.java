package cbit.vcell.fluorescenceprotocol;

/**
 * Insert the type's description here.
 * Creation date: (8/31/2006 11:49:34 AM)
 * @author: Anuradha Lakshminarayana
 */

import cbit.vcell.parser.Expression;
import cbit.vcell.model.SpeciesContext;

public class FluorescenceIndicatorProtocolSpec extends FluorescenceProtocolSpec {
	private Expression onRate = null;
	private Expression offRate = null;
	private SpeciesContext indicatorSpeciesContext = null;
	private int indicatorType = -1;

	private static final int FLUORESCENT_INDICATOR_COMPETITIVE = 1;
	private static final int FLUORESCENT_INDICATOR_NON_COMPETITIVE = 2;
/**
 * FluorescenceIndicatorProtocolSpec constructor comment.
 * @param argModel cbit.vcell.model.Model
 * @param argSpContexts cbit.vcell.model.SpeciesContext[]
 * @param argType int
 */
public FluorescenceIndicatorProtocolSpec(cbit.vcell.model.Model argModel, cbit.vcell.model.SpeciesContext[] argSpContexts, SpeciesContext argIndicator) {
	super(argModel, argSpContexts, FLUORESCENT_INDICATOR_TYPE);
	indicatorSpeciesContext = argIndicator;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2006 2:57:56 PM)
 * @return int
 */
public SpeciesContext getIndicatorSpeciesContex() {
	return indicatorSpeciesContext;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2006 2:57:56 PM)
 * @return int
 */
public int getIndicatorType() {
	return indicatorType;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2006 11:53:40 AM)
 * @return Expression
 */
public Expression getOffRate() {
	return offRate;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2006 11:53:40 AM)
 * @return Expression
 */
public Expression getOnRate() {
	return onRate;
}
}
