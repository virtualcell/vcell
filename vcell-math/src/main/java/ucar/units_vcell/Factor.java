package ucar.units_vcell;
import java.io.Serializable;


/**
 * Provides support for a Base/exponent pair.  Instances of this class are
 * immutable.
 * @author Steven R. Emmerson
 * @version $Id: Factor.java,v 1.4 2000/07/18 20:15:20 steve Exp $
 */
public final class
Factor
    implements	Serializable
{
    /**
     * The Base entity.
     * @serial
     */
    private final Base	_base;

    /**
     * The exponent.
     * @serial
     */
    private final RationalNumber _exponent;

    /**
     * Constructs from a Base.  The exponent is set to unity.
     * @param base		The base entity.
     */
    public
    Factor(Base base)
    {
	this(base, new RationalNumber(1));
    }


    /**
     * Constructs from a Base and an exponent.
     * @param base		The base entity.
     * @param exponent		The exponent.
     */
    public
    Factor(Base base, RationalNumber exponent)
    {
	_base = base;
	_exponent = exponent;
    }


    /**
     * Constructs from a Factor and an exponent.
     * @param factor		The factor.
     * @param exponent		The exponent.
     */
    public
    Factor(Factor factor, RationalNumber exponent)
    {
	this(factor.getBase(), exponent);
    }


    /**
     * Indicates if this Factor is semantically identical to another object.
     * @param object		The object.
     * @return			<code>true</code> if and only if this Factor
     *				is semantically identical to <code>object<
     *				/code>.
     */
    public boolean
    equals(Object object)
    {
	boolean	equals;
	if (this == object)
	    equals = true;
	else
	if (!(object instanceof Factor))
	    equals = false;
	else
	{
	    Factor	that = (Factor)object;
	    equals = (!getExponent().equals(that.getExponent()))
			? false
			: getExponent().isZero() ||
			    getBase().equals(that.getBase());
	}
	return equals;
    }


    /**
     * Returns the Base entity.
     * @return			The Base entity.
     */
    public Base
    getBase()
    {
	return _base;
    }


    /**
     * Returns the exponent of the Base entity.
     * @return			The exponent of the Base entity.
     */
    public RationalNumber
    getExponent()
    {
	return _exponent;
    }


    /**
     * Returns the identifier of the Base entity.
     * @return			The identifier of the Base entity (symbol or
     *				name).
     */
    public String
    getID()
    {
	return getBase().getID();
    }


    /**
     * Indicates if this factor is dimensionless.  A Factor is
     * dimensionless if and only if the exponent is zero or the Base
     * entity is dimensionless.
     * @return			<code>true</code> if and only if this Factor is
     *				dimensionless.
     */
    public boolean
    isDimensionless()
    {
	return getExponent().isZero() || getBase().isDimensionless();
    }


    /**
     * Indicates if this Factor is the reciprocal of another Factor.
     * @param that		The other factor.
     * @return			<code>true</code> if and only if this Factor
     *				is the reciprocal of <code>that</code>.
     */
    public boolean
    isReciprocalOf(Factor that)
    {
	return
	    getBase().equals(that.getBase()) &&
	    getExponent().doubleValue() == -that.getExponent().doubleValue();
    }


    /**
     * Raises this Factor to a power.
     * @param power		The power by which to raise this Factor.
     * @return			The Factor resulting from raising this Factor
     *				to the given power.
     */
    public Factor
    pow(RationalNumber power)
    {
	return new Factor(getBase(), getExponent().mult(power));
    }


    /**
     * Returns the string representation of this Factor.
     * @return			The string representation of this Factor.
     */
    public final String
    toString()
    {
	return getExponent().isZero()
		? ""
		: getExponent().doubleValue() == 1
		    ? getBase().toString()
		    : getBase().toString() + getExponent();
    }
}