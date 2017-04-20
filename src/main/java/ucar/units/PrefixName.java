package ucar.units;

/**
 * Provides support for prefix names and numeric values.
 *
 * Instances of this class are immutable.
 *
 * @author Steven R. Emmerson
 * @version $Id: PrefixName.java,v 1.4 2000/07/18 20:15:24 steve Exp $
 */
public final class
PrefixName
    extends	Prefix
{
    /**
     * Constructs from a name and a numeric value.
     * @param name		The name for the prefix.
     * @param value		The numeric value for the prefix.
     */
    public
    PrefixName(String name, double value)
    {
	super(name, value);
    }

    /**
     * Compares this PrefixName with another PrefixName.
     * @param obj		The other PrefixName.
     * @return			A negative value, zero, or a positive value
     *				depending on whether this PrefixName is less
     *				than, equal to, or greater than <code>
     *				obj</code>, respectively.
     */
    public final int
    compareTo(Object obj)
    {
	return getID().compareToIgnoreCase(((PrefixName)obj).getID());
    }

    /**
     * Compares this PrefixName with a string.
     * @param string		The string to compare this PrefixName against.
     * @return			A negative value, zero, or a positive value
     *				depending on whether this PrefixName is less
     *				than, equal to, or greater than <code>
     *				string</code>, respectively.
     */
    public final int
    compareTo(String string)
    {
	return getID().length() >= string.length()
		? getID().compareToIgnoreCase(string)
		: getID().compareToIgnoreCase(
		    string.substring(0, getID().length()));
    }
}
