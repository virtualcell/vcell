package ucar.units;

/**
 * Provides support for failures due to attempts to redefine an existing
 * unit in a unit database.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitExistsException.java,v 1.4 2000/07/18 20:15:33 steve Exp $
 */
public class
UnitExistsException
    extends	UnitDBException
{
    /**
     * Constructs from the existing unit and the redefining unit.
     * @param oldUnit		The previously existing unit in the database.
     * @param newUnit		The unit attempting the redefinition.
     */
    public
    UnitExistsException(Unit oldUnit, Unit newUnit)
    {
	this("Attempt to replace \"" + oldUnit + "\" with \"" + newUnit +
	    "\" in unit database");
    }

    /**
     * Constructs from an error message.
     * @param msg		The error message.
     */
    public
    UnitExistsException(String msg)
    {
	super(msg);
    }
}
