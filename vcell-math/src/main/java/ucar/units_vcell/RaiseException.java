package ucar.units_vcell;

/**
 * Provides support for failure to raise a unit to a power.
 *
 * @author Steven R. Emmerson
 * @version $Id: RaiseException.java,v 1.4 2000/07/18 20:15:26 steve Exp $
 */
public final class
RaiseException
    extends	OperationException
{
    /**
     * Constructs from the unit that couldn't be raised to a power.
     * @param unit		The unit that couldn't be raised to a power.
     */
    public
    RaiseException(Unit unit)
    {
	super("Can't exponentiate unit \"" + unit + "\"");
    }
}
