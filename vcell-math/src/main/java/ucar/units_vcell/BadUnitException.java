package ucar.units_vcell;

/**
 * Provides support for failures due to attempts to redefine an existing
 * unit in a unit database.
 *
 * @author Steven R. Emmerson
 * @version $Id: BadUnitException.java,v 1.4 2000/07/18 20:15:16 steve Exp $
 */
public final class
BadUnitException
    extends	UnitDBException
{
    /**
     * Constructs from an error message.
     * @param msg		The error message.
     */
    public
    BadUnitException(String msg)
    {
	super(msg);
    }
}
