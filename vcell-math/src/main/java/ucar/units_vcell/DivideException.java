package ucar.units_vcell;

/**
 * Provides support for unit division failures.
 * @author Steven R. Emmerson
 * @version $Id: DivideException.java,v 1.4 2000/07/18 20:15:20 steve Exp $
 */
public final class
DivideException
    extends	OperationException
{
    /**
     * Constructs from a unit that can't be divided.
     * @param unit		The unit that can't be divided.
     */
    public
    DivideException(Unit unit)
    {
	super("Can't divide unit \"" + unit + "\"");
    }

    /**
     * Constructs from dividend and divisor units.
     * @param numerator		The unit attempting to be divided.
     * @param denominator	The unit attempting to divide.
     */
    public
    DivideException(Unit numerator, Unit denominator)
    {
	super("Can't divide unit \"" + numerator + "\" by unit \"" + 
	    denominator + "\"");
    }
}
