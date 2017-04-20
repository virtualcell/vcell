package ucar.units;

import java.io.Serializable;

/**
 * Provides support for managing a default UnitFormat.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitFormatManager.java,v 1.4 2000/07/18 20:15:35 steve Exp $
 */
public final class
UnitFormatManager
    implements	Serializable
{
    /**
     * The singleton instance of the default unit format.
     * @serial
     */
    private static UnitFormat	instance;

    /**
     * Returns an instance of the default unit format.
     * @return			An instance of the default unit format.
     */
    public static final UnitFormat
    instance()
    {
	if (instance == null)
	{
	    synchronized(UnitFormatManager.class)
	    {
		if (instance == null)
		    instance = StandardUnitFormat.instance();
	    }
	}
	return instance;
    }

    /**
     * Sets the instance of the default unit format.  You'd better know what
     * you're doing if you call this method.
     * @param instance		An instance of the new, default unit format.
     */
    public static final synchronized void
    setInstance(UnitFormat instance)
    {
	UnitFormatManager.instance = instance;
    }
}
