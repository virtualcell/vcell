package ucar.units;

import java.io.Serializable;

/**
 * Provides support for unit identifiers.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitID.java,v 1.4 2000/07/18 20:15:35 steve Exp $
 */
public abstract class
UnitID
    implements	Serializable
{
    /**
     * Factory method for constructing an identifier from a name, plural,
     * and symbol.
     * @param name		The name for the unit.  May be <code>null
     *				</code>.
     * @param plural		The plural form of the name.  If <code>null
     *				</code> and <code>name</code> is non-<code>
     *				null</code>, then regular plural-forming rules
     *				are used on the name.
     * @param symbol		The symbol for the unit.  May be <code>null
     *				</code>.
     */
    public static UnitID
    newUnitID(String name, String plural, String symbol)
    {
	UnitID	id;
	try
	{
	    id = name == null
		    ? (UnitID)new UnitSymbol(symbol)
		    : (UnitID)UnitName.newUnitName(name, plural, symbol);
	}
	catch (NameException e)
	{
	    id = null;	// can't happen
	}
	return id;
    }

    /**
     * Returns the name of the unit.
     * @return			The name of the unit.  May be <code>null</code>.
     */
    public abstract String
    getName();

    /**
     * Returns the plural form of the name of the unit.
     * @return			The plural form of the name of the unit.
     *				May be <code>null</code>.
     */
    public abstract String
    getPlural();

    /**
     * Returns the symbol for the unit.
     * @return			The symbol for the unit.  May be
     *				<code>null</code>.
     */
    public abstract String
    getSymbol();

    /**
     * Returns the string representation of this identifier.
     * @return			The string representation of this identifier.
     */
    public abstract String
    toString();
}
