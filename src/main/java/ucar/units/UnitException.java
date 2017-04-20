package ucar.units;

import java.io.Serializable;

/**
 * Provides support for general failures of this package.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitException.java,v 1.4 2000/07/18 20:15:33 steve Exp $
 */
public abstract class
UnitException
    extends	Exception
    implements	Serializable
{
    /**
     * Constructs from nothing.
     */
    public
    UnitException()
    {}

    /**
     * Constructs from an error message.
     * @param message		The error message.
     */
    public
    UnitException(String message)
    {
	super(message);
    }

    /**
     * Constructs from a message and the exception that caused the failure.
     * @param message		The message.
     * @param e			The exception that caused the failure.
     */
    public
    UnitException(String message, Exception e)
    {
	super(
	    message + ": " +
	    (e.getMessage() == null || e.getMessage().equals("")
		? ("Exception " + e.getClass().getName() + " thrown")
		: e.getMessage()));
    }
}
