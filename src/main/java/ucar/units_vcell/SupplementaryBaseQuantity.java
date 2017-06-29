package ucar.units_vcell;

/**
 * Provides support for supplementary base quantities.  A supplementary 
 * base quantity is one that is dimensionless (e.g. solid angle).
 *
 * Instances of this class are immutable.
 *
 * @author Steven R. Emmerson
 * @version $Id: SupplementaryBaseQuantity.java,v 1.4 2000/07/18 20:15:28 steve Exp $
 */
public final class
SupplementaryBaseQuantity
    extends	BaseQuantity
{
    /**
     * Constructs from a name and symbol.
     * @param name		The name of the quantity.
     * @param symbol		The symbol for the quantity.
     * @throws NameException	Bad quantity name.
     */
    public
    SupplementaryBaseQuantity(String name, String symbol)
	throws NameException
    {
	super(name, symbol);
    }

    /**
     * Constructs from a name and symbol.  This is a trusted constructor for
     * use by the superclass only.
     * @param name		The name of the quantity.
     * @param symbol		The symbol for the quantity.
     */
    protected
    SupplementaryBaseQuantity(String name, String symbol, boolean trusted)
    {
	super(name, symbol, trusted);
    }

    /**
     * Indicates whether or not this quantity is dimensionless.  Supplementary
     * base quantities are dimensionless by definition.
     * @return			<code>true</code>.
     */
    public boolean
    isDimensionless()
    {
	/*
	 * These quantities are dimensionless by definition.
	 */
	return true;
    }
}
