package ucar.units;

/**
 * Provides support for errors with the system of units.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitSystemException.java,v 1.4 2000/07/18 20:15:37 steve Exp $
 */
public class
UnitSystemException
    extends	UnitException
{
    /**
     * Constructs from an error message.
     * @param message		The error messsage.
     */
    public
    UnitSystemException(String message)
    {
	super(message);
    }

    /**
     * Constructs from an error message and the exception that caused the 
     * error.
     * @param message		The error messsage.
     * @param e			The exception that caused the problem.
     */
    public
    UnitSystemException(String message, Exception e)
    {
	super(message, e);
    }
}
