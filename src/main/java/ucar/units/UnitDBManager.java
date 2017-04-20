package ucar.units;

import java.io.Serializable;

/**
 * Provides support for managing a default unit database.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitDBManager.java,v 1.4 2000/07/18 20:15:31 steve Exp $
 */
public final class
UnitDBManager
    implements	Serializable
{
    /**
     * The singleton instance of the default unit database.
     * @serial
     */
    private static UnitDB	instance;

    /**
     * Gets the default unit database.
     * @return			The default unit database.
     * @throws UnitDBException	The default unit database couldn't be
     *				created.
     */
    public static final UnitDB
    instance()
	throws UnitDBException
    {
	if (instance == null)
	{
	    synchronized(UnitDBManager.class)
	    {
		if (instance == null)
		{
		    instance = StandardUnitDB.instance();
		}
	    }
	}
	return instance;
    }

    /**
     * Sets the default unit database.  You'd better know what you're doing
     * if you call this method!
     * @param instance		The unit database to be made the default one.
     */
    public static final synchronized void
    setInstance(UnitDB instance)
    {
	UnitDBManager.instance = instance;
    }
}
