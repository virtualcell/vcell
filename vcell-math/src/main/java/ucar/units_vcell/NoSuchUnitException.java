package ucar.units_vcell;

/**
 * Provides support for failure to find a unit.
 * @author Steven R. Emmerson
 * @version $Id: NoSuchUnitException.java,v 1.4 2000/07/18 20:15:21 steve Exp $
 */
public class
NoSuchUnitException
    extends	SpecificationException
{
    /**
     * Constructs from a unit identifier.
     * @param id		The identifier of the unit that couldn't be
     *				found.
     */
    public
    NoSuchUnitException(UnitID id)
    {
	this(id.toString());
    }

    /**
     * Constructs from a unit identifier.
     * @param id		The identifier of the unit that couldn't be
     *				found.
     */
    public
    NoSuchUnitException(String id)
    {
	super("Unit \"" + id + "\" not in database");
    }
}
