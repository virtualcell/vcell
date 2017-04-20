package ucar.units;

/**
 * Interface for "base" entities like base units or base quantities.
 * @author Steven R. Emmerson
 * @version $Id: Base.java,v 1.4 2000/07/18 20:15:17 steve Exp $
 */
public interface
Base
{
    /**
     * Indicates if this base entity is dimensionless.
     * @return			<code>true</code> if and only if the base
     *				entity is dimensionless (e.g. 
     *				(BaseQuantity.SOLID_ANGLE</code>).
     */
    public boolean
    isDimensionless();

    /**
     * Returns the identifier for the base entity.
     * @return			The base entity's identifier (i.e. symbol or
     *				name).
     */
    public String
    getID();

    /**
     * Indicates if this base entity is semantically the same as another object.
     * @param object		The other object.
     * @return			<code>true</code> if and only if this base
     *				entity is semantically the same as
     *				<code>object</code>.
     */
    public boolean
    equals(Object object);
}
