package ucar.units;

/**
 * Provides support for a base quantity that is dimensionfull.
 *
 * Instances of this class are immutable.
 *
 * @author Steven R. Emmerson
 * @version $Id: RegularBaseQuantity.java,v 1.4 2000/07/18 20:15:26 steve Exp $
 */
public final class
RegularBaseQuantity
    extends	BaseQuantity
{
    /**
     * Constructs from a name and symbol.
     * @param name		The name of the base unit.
     * @param symbol		The symbol of the base unit.
     */
    public
    RegularBaseQuantity(String name, String symbol)
	throws NameException
    {
	super(name, symbol);
    }

    /**
     * Constructs from a name and a symbol.  This is a trusted constructor
     * for use by the parent class only.
     * @param name		The name of the base unit.
     * @param symbol		The symbol of the base unit.
     */
    protected
    RegularBaseQuantity(String name, String symbol, boolean trusted)
    {
	super(name, symbol, trusted);
    }

    /**
     * Indicates if this base quantity is dimensionless.  Regular base
     * quantities are always dimensionfull.
     * @return			<code>false</code>.
     */
    public boolean
    isDimensionless()
    {
	return false;
    }
}
