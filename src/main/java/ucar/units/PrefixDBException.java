package ucar.units;

/**
 * Provides support for the general class of prefix database failures.
 *
 * @author Steven R. Emmerson
 * @version $Id: PrefixDBException.java,v 1.4 2000/07/18 20:15:23 steve Exp $
 */
public class
PrefixDBException
    extends	UnitException
{
    /**
     * Constructs from nothing.
     */
    public
    PrefixDBException()
    {
	super("Prefix database exception");
    }

    /**
     * Constructs from an error message.
     * @param message		The error message.
     */
    public
    PrefixDBException(String message)
    {
	super("Prefix database exception: " + message);
    }

    /**
     * Constructs from the exception that caused this exception to be thrown.
     * @param e			The exception that caused this exception 
     *				to be thrown.
     */
    public
    PrefixDBException(Exception e)
    {
	this("Prefix database exception", e);
    }

    /**
     * Constructs from an error message and the exception that caused
     * this exception to be thrown.
     * @param message		The error message.
     * @param e			The exception that caused this exception 
     *				to be thrown.
     */
    public
    PrefixDBException(String message, Exception e)
    {
	super(message, e);
    }
}
