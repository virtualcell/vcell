package ucar.units;

/**
 * Provides support for prefix database access failures.  This is not used
 * in the default implementation but could be used for remote database
 * implementations, for example.
 *
 * @author Steven R. Emmerson
 * @version $Id: PrefixDBAccessException.java,v 1.4 2000/07/18 20:15:23 steve Exp $
 */
public class
PrefixDBAccessException
    extends	PrefixDBException
{
    /**
     * Constructs from an error message.
     * @param reason		The reason the database couldn't be accessed.
     */
    public
    PrefixDBAccessException(String reason)
    {
	super("Couldn't access unit-prefix database: " + reason);
    }
}
