package ucar.units;

/**
 * Provides support for a Unit that is an instance of an unknown class.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitClassException.java,v 1.4 2000/07/18 20:15:30 steve Exp $
 */
public final class
UnitClassException
    extends	UnitFormatException
{
    /**
     * Constructs from an error message.
     * @param msg		The error message.
     */
    private
    UnitClassException(String msg)
    {
	super(msg);
    }

    /**
     * Constructs from the unit that's an instance of an unknown class.
     * @param unit		The unknown unit.
     */
    public
    UnitClassException(Unit unit)
    {
	this("\"" + unit.getClass().getName() + "\" is an unknown unit class");
    }
}
