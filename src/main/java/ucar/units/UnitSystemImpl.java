package ucar.units;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Steven R. Emmerson
 * @version $Id: UnitSystemImpl.java,v 1.4 2000/07/18 20:15:37 steve Exp $
 */
public class
UnitSystemImpl
    implements	UnitSystem, Serializable
{
    /**
     * The quantity-to-base-unit map.
     * @serial
     */
    private final HashMap	quantityMap;

    /**
     * The base unit database;
     * @serial
     */
    private final UnitDB	baseUnitDB;

    /**
     * The complete database;
     * @serial
     */
    private final UnitDBImpl	acceptableUnitDB;

    /**
     * Constructs from a base unit database and a derived unit database.
     * @param baseUnitDB	The base unit database.  Shall only contain
     *				base units.
     * @param derivedUnitDB	The derived unit database.  Shall not contain
     *				any base units.
     * @throws UnitExistsException	A unit with the same identifier exists
     *					in both databases.
     */
    protected
    UnitSystemImpl(UnitDBImpl baseUnitDB, UnitDBImpl derivedUnitDB)
	throws UnitExistsException
    {
	quantityMap = new HashMap(baseUnitDB.nameCount());
	for (Iterator iter = baseUnitDB.getIterator(); iter.hasNext(); )
	{
	    Unit	unit = (Unit)iter.next();
	    BaseUnit	baseUnit = (BaseUnit)unit;
	    quantityMap.put(baseUnit.getBaseQuantity(), baseUnit);
	}
	this.baseUnitDB = baseUnitDB;
	acceptableUnitDB =
	    new UnitDBImpl(
		baseUnitDB.nameCount() + derivedUnitDB.nameCount(),
		baseUnitDB.symbolCount() + derivedUnitDB.symbolCount());
	acceptableUnitDB.add(baseUnitDB);
	acceptableUnitDB.add(derivedUnitDB);
    }

    /**
     * Returns the base unit database.
     * @return			The base unit database.
     */
    public final UnitDB
    getBaseUnitDB()
    {
	return baseUnitDB;
    }

    /**
     * Returns the complete unit database.
     * @return			The complete unit database (both base units
     *				and derived units).
     */
    public final UnitDB
    getUnitDB()
    {
	return acceptableUnitDB;
    }

    /**
     * Returns the base unit corresponding to a base quantity.
     * @param quantity		The base quantity.
     * @return			The base unit corresponding to the base
     *				quantity in this system of units or <code>
     *				null</code> if no such unit exists.
     */
    public final BaseUnit
    getBaseUnit(BaseQuantity quantity)
    {
	return (BaseUnit)quantityMap.get(quantity);
    }
}
