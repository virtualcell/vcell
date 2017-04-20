package ucar.units;

/**
 * Provides support for unit operation failures (e.g. multiplication).
 * @author Steven R. Emmerson
 * @version $Id: OperationException.java,v 1.4 2000/07/18 20:15:22 steve Exp $
 */
public abstract class
OperationException
    extends	UnitException
{
    /**
     * Constructs from nothing.
     */
    public
    OperationException()
    {}

    /**
     * Constructs from an error message.
     * @param message		The error message.
     */
    protected
    OperationException(String message)
    {
	super(message);
    }

    /**
     * Constructs from an error message and the exception that caused the
     * problem.
     * @param message		The error message.
     * @param e			The exception that caused the problem.
     */
    protected
    OperationException(String message, Exception e)
    {
	super(message, e);
    }
}
