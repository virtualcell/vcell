package ucar.units;

/**
 * Provides support for general unit database failures.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitDBException.java,v 1.4 2000/07/18 20:15:30 steve Exp $
 */
public class
UnitDBException
    extends	UnitException
{
    /**
     * Constructs from nothing.
     */
    protected
    UnitDBException()
    {}

    /**
     * Constructs from an error message.
     * @param message		The error message.
     */
    public
    UnitDBException(String message)
    {
	super(message);
    }

    /**
     * Constructs from a message and the exception that caused the failure.
     * @param message		The message.
     * @param e			The exeception that cause the the failure.
     */
    public
    UnitDBException(String message, Exception e)
    {
	super(message, e);
    }
}
