package ucar.units_vcell;

/**
 * Interface for a system of units.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitSystem.java,v 1.4 2000/07/18 20:15:36 steve Exp $
 */
public interface
UnitSystem
{
    /**
     * Returns the database of base units.
     * @return			The database of base units.
     */
    public UnitDB
    getBaseUnitDB();

    /**
     * Returns the complete database of units (base units and 
     * derived units acceptable for use in the system of units.
     * @return			The complete database of units.
     */
    public UnitDB
    getUnitDB();

    /**
     * Returns the base unit corresponding to a base quantity.
     * @param quantity		A base quantity.
     * @return			The base unit corresponding to the base
     *				quantity or <code>null</code> if no such
     *				unit exists.
     */
    public BaseUnit
    getBaseUnit(BaseQuantity quantity);
}
