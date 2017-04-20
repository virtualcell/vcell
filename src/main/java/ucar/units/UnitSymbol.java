package ucar.units;

/**
 * Provides support for symbols for units.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitSymbol.java,v 1.4 2000/07/18 20:15:36 steve Exp $
 */
public final class
UnitSymbol
    extends	UnitID
{
    /**
     * The symbol for the unit.
     * @serial
     */
    private final String	symbol;

    /**
     * Constructs from a symbol.
     * @param symbol		The symbol for the unit.  Shall not be <code>
     *				null</code>.
     */
    public
    UnitSymbol(String symbol)
	throws NameException
    {
	if (symbol == null)
	    throw new NameException("Symbol can't be null");
	this.symbol = symbol;
    }

    /**
     * Returns the name of the unit.  Always returns <code>null</code>.
     * @return			<code>null</code>.
     */
    public String
    getName()
    {
	return null;
    }

    /**
     * Returns the plural form of the name of the unit.  Always returns
     * <code>null</code>.
     * @return			<code>null</code>.
     */
    public String
    getPlural()
    {
	return null;
    }

    /**
     * Returns the symbol for the unit.
     * @return			The symbol for the unit.  Never <code>null
     *				</code>.
     */
    public String
    getSymbol()
    {
	return symbol;
    }

    /**
     * Returns the string representation of this identifier.
     * @return			The string representation of this identifier.
     */
    public String
    toString()
    {
	return getSymbol();
    }
}
