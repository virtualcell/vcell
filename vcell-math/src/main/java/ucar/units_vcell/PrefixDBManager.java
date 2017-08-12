package ucar.units_vcell;

import java.io.Serializable;

/**
 * Provides support for managing a database of unit prefixes.
 *
 * @author Steven R. Emmerson
 * @version $Id: PrefixDBManager.java,v 1.4 2000/07/18 20:15:24 steve Exp $
 */
public final class
PrefixDBManager
    implements	Serializable
{
    /**
     * @serial
     */
    private static PrefixDB	instance;

    /**
     * Gets the current prefix database.
     * @return			The current prefix database.
     * @throws PrefixDBException	The current prefix database couldn't 
     *					be created.
     */
    public static final PrefixDB
    instance()
	throws PrefixDBException
    {
	if (instance == null)
	{
	    synchronized(PrefixDBManager.class)
	    {
		if (instance == null)
		{
		    instance = StandardPrefixDB.instance();
		}
	    }
	}
	return instance;
    }

    /**
     * Sets the current prefix database.
     * @param instance		The prefix database to be made the current one.
     */
    public static final synchronized void
    setInstance(PrefixDB instance)
    {
	PrefixDBManager.instance = instance;
    }
}
