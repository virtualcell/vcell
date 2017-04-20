package ucar.units;

import java.io.Serializable;

/**
 * Provides support for managing a UnitSystem.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitSystemManager.java,v 1.4 2000/07/18 20:15:37 steve Exp $
 */
public final class
UnitSystemManager
    implements	Serializable
{
    /**
     * The singleton instance of the system of units.
     * @serial
     */
    private static UnitSystem	instance;

    /**
     * Returns an instance of the system of units.
     * @return			An instance of the system of units.
     */
    public static final UnitSystem
    instance()
	throws UnitSystemException
    {
	if (instance == null)
	{
	    synchronized(UnitSystemManager.class)
	    {
		if (instance == null)
		    instance = SI.instance();
	    }
	}
	return instance;
    }

    /**
     * Sets the system of units.  This must be called before any
     * call to <code>instance()</code>.
     * @param instance		The system of units.
     * @throws UnitSystemException	<code>instance()</code> was called
     *					earlier.
     */
    public static final synchronized void
    setInstance(UnitSystem instance)
	throws UnitSystemException
    {
	if (instance != null)
	    throw new UnitSystemException("Unit system already used");
	UnitSystemManager.instance = instance;
    }
}
