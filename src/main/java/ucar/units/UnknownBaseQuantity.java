package ucar.units;

/**
 * Provides support for an unknown base quantity.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnknownBaseQuantity.java,v 1.4 2000/07/18 20:15:38 steve Exp $
 */
public final class
UnknownBaseQuantity
    extends	BaseQuantity
{
    /**
     * Constructs from nothing.
     */
    protected
    UnknownBaseQuantity()
    {
	super("Unknown", "x", true);
    }

    /**
     * Indicates if this quantity is semantically the same as an object.
     * Unknown quantities are never equal by definition -- not even to itself.
     * @param object		The object.
     * @return			<code>false</code> always.
     */
    public boolean
    equals(Object object)
    {
	return false;
    }

    /**
     * Indicates if this quantity is dimensionless.
     * Unknown quantities are never dimensionless by definition.
     * @return			<code>false</code> always.
     */
    public boolean
    isDimensionless()
    {
	return false;
    }
}
