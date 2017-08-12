package ucar.units_vcell;

/**
 * Provides support for prefix database failures.
 *
 * @author Steven R. Emmerson
 * @version $Id: PrefixExistsException.java,v 1.4 2000/07/18 20:15:24 steve Exp $
 */
public class
PrefixExistsException
    extends	PrefixDBException
{
    /**
     * Constructs from an old prefix and a new prefix.
     * @param oldPrefix		The previously-existing prefix.
     * @param newPrefix		The replacement prefix.
     */
    public
    PrefixExistsException(Prefix oldPrefix, Prefix newPrefix)
    {
	super("Attempt to replace \"" + oldPrefix + "\" with \"" +
	    newPrefix + "\" in prefix database");
    }
}
