package ucar.units_vcell;

/**
 * Provides support for failure to access unit database (e.g. RMI
 * failure).
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitDBAccessException.java,v 1.4 2000/07/18 20:15:30 steve Exp $
 */
public class
UnitDBAccessException
    extends	UnitDBException
{
    /**
     * Constructs from a string.
     * @param reason		The reason for the failure.
     */
    public
    UnitDBAccessException(String reason)
    {
	super("Couldn't access unit database: " + reason);
    }
}
