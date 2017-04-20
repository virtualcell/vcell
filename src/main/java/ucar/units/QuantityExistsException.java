package ucar.units;

/**
 * Provides support for attempting to redefine a base quantity in a
 * database.
 *
 * @author Steven R. Emmerson
 * @version $Id: QuantityExistsException.java,v 1.4 2000/07/18 20:15:26 steve Exp $
 */
public class
QuantityExistsException
    extends	UnitException
{
    /**
     * Constructs from the base quantity being redefined.
     */
    public
    QuantityExistsException(BaseQuantity quantity)
    {
	this("Base quantity \"" + quantity + " already exists");
    }

    /**
     * Constructs from an error message.
     * @param msg		The error message.
     */
    private
    QuantityExistsException(String msg)
    {
	super(msg);
    }
}
