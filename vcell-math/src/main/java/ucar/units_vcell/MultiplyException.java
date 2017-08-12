package ucar.units_vcell;

/**
 * Provides support for unit multiplication failures.
 *
 * @author Steven R. Emmerson
 * @version $Id: MultiplyException.java,v 1.4 2000/07/18 20:15:21 steve Exp $
 */
public final class
MultiplyException
    extends	OperationException
{
    /**
     * Constructs from a unit that can't be multiplied.
     * @param unit		The unit that can't be multiplied.
     */
    public
    MultiplyException(Unit unit)
    {
	super("Can't multiply unit \"" + unit + '"');
    }

    /**
     * Constructs from two units.
     * @param A			A unit attempting to be multiplied.
     * @param B			The other unit attempting to be multiplied.
     */
    public
    MultiplyException(Unit A, Unit B)
    {
	super("Can't multiply unit \"" + A + "\" by unit \"" + B + '"');
    }
}
