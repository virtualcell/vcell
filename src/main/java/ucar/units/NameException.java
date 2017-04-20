package ucar.units;

/**
 * Provides support for bad unit names.
 * @author Steven R. Emmerson
 * @version $Id: NameException.java,v 1.4 2000/07/18 20:15:21 steve Exp $
 */
public final class
NameException
    extends	UnitException
{
    /**
     * Constructs from a message.
     * @param msg		The message.
     */
    public
    NameException(String msg)
    {
	super(msg);
    }
}
