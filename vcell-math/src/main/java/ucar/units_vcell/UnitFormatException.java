package ucar.units_vcell;

/**
 * Provides support for general failures with unit format classes.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitFormatException.java,v 1.4 2000/07/18 20:15:34 steve Exp $
 */
public class
UnitFormatException
    extends	UnitException
{
    /**
     * Constructs from nothing.
     */
    public
    UnitFormatException()
    {}

    /**
     * Constructs from an error message.
     * @param message		The error message.
     */
    public
    UnitFormatException(String message)
    {
	super(message);
    }
}
