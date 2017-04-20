package ucar.units;

import java.io.Serializable;

/**
 * Provides support for classes that parse and format unit specifications.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitFormatImpl.java,v 1.4 2000/07/18 20:15:34 steve Exp $
 */
public abstract class
UnitFormatImpl
    implements	UnitFormat, Serializable
{
    /**
     * Parses a unit specification.
     * @param spec              The unit specification (e.g. "m/s");
     * @return                  The unit corresponding to the specification.
     * @throws NoSuchUnitException      A unit in the specification couldn't be
     *                                  found (e.g. the "m" in the example).
     * @throws UnitParseException       The specification is grammatically
     *                                  incorrect.
     * @throws SpecificationException   The specification is incorrect somehow.
     * @throws UnitDBException          Problem with the unit database.
     * @throws PrefixDBException        Problem with the unit-prefix database.
     * @throws UnitSystemException      Problem with the system of units.
     */
    public final Unit
    parse(String spec)
	throws NoSuchUnitException,
	    UnitParseException,
	    SpecificationException,
	    UnitDBException,
	    PrefixDBException,
	    UnitSystemException
    {
	return parse(spec, UnitDBManager.instance());
    }

    /**
     * Formats a Factor (a base unit/exponent pair).
     * @param factor            The base unit/exponent pair.
     * @return                  The formatted factor.
     */
    public final String
    format(Factor factor)
    {
	return format(factor, new StringBuffer(8)).toString();
    }

    /**
     * Formats a unit.  If the unit has a symbol or name, then one of them
     * will be used; otherwise, a specification of the unit in terms of
     * underlying units will be returned.
     * @param unit              The unit.
     * @return                  The formatted unit.
     */
    public final String
    format(Unit unit)
	throws UnitClassException
    {
	return format(unit, new StringBuffer(80)).toString();
    }

    /**
     * Formats a unit using a long form.  This always returns a specification
     * for the unit in terms of underlying units: it doesn't return the name
     * or symbol of the unit unless the unit is a base unit.
     * @param unit              The unit.
     * @return                  The formatted unit.
     */
    public final String
    longFormat(Unit unit)
	throws UnitClassException
    {
	return longFormat(unit, new StringBuffer(80)).toString();
    }
}
