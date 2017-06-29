package ucar.units_vcell;

import java.io.Serializable;


/**
 * Provides support for classes that implement units.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitImpl.java,v 1.5 2000/07/18 20:15:35 steve Exp $
 */
public abstract class
UnitImpl
    implements	Unit, Serializable
{
    /**
     * The unit identifier.
     * @serial
     */
    private final UnitName	id;

    /**
     * Provides support for converting numeric values from this unit to
     * another unit.
     */
    protected static class
    MyConverter
	extends	ConverterImpl
    {
	private final DerivableUnit	fromUnit;
	private final DerivableUnit	toUnit;
	protected
	MyConverter(Unit fromUnit, Unit toUnit)
	    throws ConversionException
	{
	    super(fromUnit, toUnit);
	    if (!(fromUnit instanceof DerivableUnit) ||
	        !(toUnit instanceof DerivableUnit))
	    {
		throw new ConversionException(fromUnit, toUnit);
	    }
	    this.fromUnit = (DerivableUnit)fromUnit;
	    this.toUnit = (DerivableUnit)toUnit;
	}
	public double
	convert(double amount)
	{
	    double	output;
	    try
	    {
		output = toUnit.fromDerivedUnit(fromUnit.toDerivedUnit(amount));
	    }
	    catch (ConversionException e)
	    {
		output = 0;
	    }	// can't happen because isCompatible() vetted
	    return output;
	}
	public float[]
	convert(float[] input, float[] output)
	{
	    try
	    {
		toUnit.fromDerivedUnit(
		    fromUnit.toDerivedUnit(input, output),
		    output);
	    }
	    catch (ConversionException e)
	    {}	// can't happen because isCompatible() vetted
	    return output;
	}
	public double[]
	convert(double[] input, double[] output)
	{
	    try
	    {
		toUnit.fromDerivedUnit(
		    fromUnit.toDerivedUnit(input, output),
		    output);
	    }
	    catch (ConversionException e)
	    {}	// can't happen because isCompatible() vetted
	    return output;
	}
    }

    /**
     * Constructs with no ID.
     */
    protected
    UnitImpl()
    {
	this(null);
    }
    /**
     * Constructs with the given ID.
     * @param id		The id of the unit (e.g. "foot").  May be 
     *				null.
     */
    protected
    UnitImpl(UnitName id)
    {
	this.id = id;
    }
    /**
     * Converts numeric values from this unit to another unit.
     * @param amount		The numeric values.
     * @param outputUnit	The unit to which to convert the numeric
     *				values.
     * @return			The numeric values in the output unit
     *				in allocated space.
     * @throws ConversionException	The units aren't convertible.
     */
    public double[]
    convertTo(double[] amounts, Unit outputUnit)
	throws ConversionException
    {
	return convertTo(amounts, outputUnit, new double[amounts.length]);
    }
    /**
     * Converts numeric values from this unit to another unit.
     * @param input		The input numeric values.
     * @param outputUnit	The unit to which to convert the numeric
     *				values.
     * @param output		The output numeric values.  May be the same
     *				array as the input values.
     * @return			The numeric values in the output unit.
     * @throws ConversionException	The units aren't convertible.
     */
    public double[]
    convertTo(double[] input, Unit outputUnit, double[] output)
	throws ConversionException
    {
	return getConverterTo(outputUnit).convert(input, output);
    }
    /**
     * Converts numeric values from this unit to another unit.
     * @param amount		The numeric values.
     * @param outputUnit	The unit to which to convert the numeric
     *				values.
     * @return			The numeric values in the output unit
     *				in allocated space.
     * @throws ConversionException	The units aren't convertible.
     */
    public float[]
    convertTo(float[] amounts, Unit outputUnit)
	throws ConversionException
    {
	return convertTo(amounts, outputUnit, new float[amounts.length]);
    }
    /**
     * Converts numeric values from this unit to another unit.
     * @param input		The input numeric values.
     * @param outputUnit	The unit to which to convert the numeric
     *				values.
     * @param output		The output numeric values.  May be the same
     *				array as the input values.
     * @return			The numeric values in the output unit.
     * @throws ConversionException	The units aren't convertible.
     */
    public float[]
    convertTo(float[] input, Unit outputUnit, float[] output)
	throws ConversionException
    {
	return getConverterTo(outputUnit).convert(input, output);
    }
    /**
     * Converts a numeric value from this unit to another unit.
     * @param amount		The numeric value.
     * @param outputUnit	The unit to which to convert the numeric
     *				value.
     * @return			The numeric value in the output unit.
     * @throws ConversionException	The units aren't convertible.
     */
    public double
    convertTo(double amount, Unit outputUnit)
	throws ConversionException
    {
	return getConverterTo(outputUnit).convert(amount);
    }
    /**
     * Converts a numeric value from this unit to another unit.
     * @param amount		The numeric value.
     * @param outputUnit	The unit to which to convert the numeric
     *				value.
     * @return			The numeric value in the output unit.
     * @throws ConversionException	The units aren't convertible.
     */
    public float
    convertTo(float amount, Unit outputUnit)
	throws ConversionException
    {
	return (float)convertTo((double)amount, outputUnit);
    }
    /**
     * Divides this unit by another.
     * @param that		The other unit.
     * @return			The quotient of this unit divided by the
     *				other unit.
     * @throws OperationException	Can't divide these units.
     */
    public final Unit
    divideBy(Unit that)
	throws OperationException
    {
	return myDivideBy(that);
    }
    /**
     * Divides this unit into another.
     * @param that		The other unit.
     * @return			The quotient of this unit divided into the
     *				other unit.
     * @throws OperationException	Can't divide these units.
     */
    public final Unit
    divideInto(Unit that)
	throws OperationException
    {
	return myDivideInto(that);
    }
    /**
     * Indicates if this unit is semantically identical to an object.
     * @param object		The object.
     * @return			<code>true</code> if and only if this unit
     *				is semantically identical to the object.
     */
    public boolean
    equals(Object object)
    {
	boolean	equals;
	if (this == object)
	    equals = true;
	else
	if (!(object instanceof Unit))
	    equals = false;
	else
	{
	    Unit	that = (Unit)object;
	    equals =
		equalsIgnoreCase(getName(), that.getName()) &&
		equalsIgnoreCase(getPlural(), that.getPlural()) &&
		equals(getSymbol(), that.getSymbol());
	}
	return equals;
    }
    /**
     * Indicates if two string are equal.
     * @param s1		One string.  May be <code>null</code>.
     * @param s2		The other string.  May be <code>null</code>.
     * @return			<code>true</code> if an only if both strings
     *				are <code>null</code> or both strings are
     *				identical.
     */
    static private final boolean
    equals(String s1, String s2)
    {
	return
	    (s1 == null && s2 == null) ||
	    (s1 != null && s2 != null && s1.equals(s2));
    }
    /**
     * Indicates if two string are equal (ignoring case).
     * @param s1		One string.  May be <code>null</code>.
     * @param s2		The other string.  May be <code>null</code>.
     * @return			<code>true</code> if an only if both strings
     *				are <code>null</code> or both strings are
     *				identical (ignoring case).
     */
    static private final boolean
    equalsIgnoreCase(String s1, String s2)
    {
	return 
	    (s1 == null && s2 == null) ||
	    (s1 != null && s2 != null && s1.equalsIgnoreCase(s2));
    }
    /**
     * Gets a Converter for converting numeric values from this unit to
     * another, compatible unit.
     * @param outputUnit	The unit to which to convert the numeric
     *				values.
     * @return			A converter of values from this unit to
     *				the other unit.
     * @throws ConversionException	The units aren't convertible.
     */
    public Converter
    getConverterTo(Unit outputUnit)
	throws ConversionException
    {
	return new MyConverter(this, outputUnit);
    }
    /**
     * Gets the name of the unit.
     * @return			The name of the unit.  May be <code>null</code>.
     */
    public final String
    getName()
    {
	return id == null
		? null
		: id.getName();
    }
    /**
     * Gets the plural form of the name of the unit.
     * @return			The plural of the name of the unit.  May
     *				be <code>null</code>.
     */
    public final String
    getPlural()
    {
	return id == null
		? null
		: id.getPlural();
    }
    /**
     * Gets the symbol for the unit.
     * @return			The symbol of the unit.  May be 
     *				<code>null</code>.
     */
    public final String
    getSymbol()
    {
	return id == null
		? null
		: id.getSymbol();
    }
    /**
     * Gets the identifier of this unit.
     * @return			The ID of this unit.  May be <code>null</code>.
     */
    public final UnitName
    getUnitName()
    {
	return id;
    }
    /**
     * Indicates if numeric values in this unit are convertible with another
     * unit.
     * @param that		The other unit.
     * @return			<code>true</code> if and only if numeric
     *				values in this unit are convertible the other
     *				unit.
     */
    public boolean
    isCompatible(Unit that)
    {
	return getDerivedUnit().isCompatible(that.getDerivedUnit());
    }
    /**
     * Returns a label for a quantity in this unit.
     * @param quantityID	The identifier for the quantity (e.g.
     *				"altitude").
     * @return			The appropriate label (e.g. "altitude/m").
     */
    public String
    makeLabel(String quantityID)
    {
	StringBuffer	buf = new StringBuffer(quantityID);
	if (quantityID.indexOf(" ") != -1)
	    buf.insert(0, '(').append(')');
	buf.append('/');
	int	start = buf.length();
	buf.append(toString());
	if (buf.substring(start).indexOf(' ') != -1)
	    buf.insert(start, '(').append(')');
	return buf.toString();
    }
    /**
     * Multiplies this unit by another.
     * @param that		The other unit.
     * @return			The product of this unit multiplied by the
     *				other unit.
     * @throws MultiplyException	Can't multiply these units.
     */
    public final Unit
    multiplyBy(Unit that)
	throws MultiplyException
    {
	return myMultiplyBy(that);
    }
    /**
     * Divides this unit by another.
     * @param that		The other unit.
     * @return			The quotient of this unit divided by the
     *				other unit.
     * @throws OperationException	Can't divide these units.
     */
    protected abstract Unit
    myDivideBy(Unit unit)
	throws OperationException;
    /**
     * Divides this unit into another.
     * @param that		The other unit.
     * @return			The quotient of this unit divided into the
     *				other unit.
     * @throws OperationException	Can't divide these units.
     */
    protected abstract Unit
    myDivideInto(Unit unit)
	throws OperationException;
    /**
     * Multiplies this unit by another.
     * @param that		The other unit.
     * @return			The product of this unit multiplied by the
     *				other unit.
     * @throws MultiplyException	Can't multiply these units.
     */
    protected abstract Unit
    myMultiplyBy(Unit that)
	throws MultiplyException;
    /**
     * Raises this unit to a power.
     * @param that		The power.
     * @return			The result of raising this unit to the power.
     * @throws RaiseException	Can't raise this unit to a power.
     */
    protected abstract Unit
    myRaiseTo(RationalNumber power)
	throws RaiseException;
    /**
     * Raises this unit to a power.
     * @param that		The power.
     * @return			The result of raising this unit to the power.
     * @throws RaiseException	Can't raise this unit to a power.
     */
    public final Unit
    raiseTo(RationalNumber power)
	throws RaiseException
    {
	return myRaiseTo(power);
    }
    /**
     * Returns the string representation of this unit.
     * @return			The string representation of this unit.
     */
    public String
    toString()
    {
	String	string = getSymbol();
	return string != null
		? string
		: getName();
    }
}
