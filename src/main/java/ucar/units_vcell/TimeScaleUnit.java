package ucar.units_vcell;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Provides support for a reference time unit whose origin is at a certain 
 * time.
 *
 * Instances of this class are immutable.
 *
 * @author Steven R. Emmerson
 * @version $Id: TimeScaleUnit.java,v 1.4 2000/07/18 20:15:29 steve Exp $
 */
public final class
TimeScaleUnit
    extends	UnitImpl
{
    /**
     * The reference time unit.
     * @serial
     */
    private final Unit				_unit;

    /**
     * The time origin for this instance.
     * @serial
     */
    private final Date				_origin;

    /**
     * The date formatter.
     * @serial
     */
    private static final SimpleDateFormat	dateFormat;

    static
    {
	dateFormat =
	    (SimpleDateFormat)DateFormat.getDateInstance(
		DateFormat.SHORT, Locale.US);
	dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	dateFormat.applyPattern(" '@' yyyy-MM-dd HH:mm:ss.SSS 'UTC'");
    }

    /**
     * Provides support for Converter-s.
     */
    protected static final class
    MyConverter
	extends	ConverterImpl
    {
	private final double	offset;
	private final Converter	converter;
	protected
	MyConverter(TimeScaleUnit fromUnit, Unit toUnit)
	    throws ConversionException
	{
	    super(fromUnit, toUnit);
	    converter =
		fromUnit.getUnit().getConverterTo(
		    ((TimeScaleUnit)toUnit).getUnit());
	    offset =
		SI.SECOND.convertTo(
		    (fromUnit.getOrigin().getTime() - 
			((TimeScaleUnit)toUnit).getOrigin().getTime())/1000.0,
		    ((TimeScaleUnit)toUnit).getUnit());
	}
	public double
	convert(double amount)
	{
	    return converter.convert(amount) + offset;
	}
	public float[]
	convert(float[] input, float[] output)
	{
	    output = converter.convert(input, output);
	    for (int i = input.length; --i >= 0; )
		output[i] += offset;
	    return output;
	}
	public double[]
	convert(double[] input, double[] output)
	{
	    output = converter.convert(input, output);
	    for (int i = input.length; --i >= 0; )
		output[i] += offset;
	    return output;
	}
    }

    /**
     * Constructs from a reference unit and a time origin.
     * @param unit		The reference time unit.
     * @param origin		The time origin.
     * @throws BadUnitException	<code>unit</code> is not a unit of time.
     */
    public
    TimeScaleUnit(Unit unit, Date origin)
	throws BadUnitException, UnitSystemException
    {
	this(unit, origin, null);
    }


    /**
     * Constructs from a reference unit, a time origin, and an identifier.
     * @param unit		The reference time unit.
     * @param origin		The time origin.
     * @param id		The identifier.
     * @throws BadUnitException	<code>unit</code> is not a unit of time.
     */
    public
    TimeScaleUnit(Unit unit, Date origin, UnitName id)
	throws BadUnitException, UnitSystemException
    {
	super(id);
	if (!unit.isCompatible(
	    UnitSystemManager.instance().getBaseUnit(BaseQuantity.TIME)))
	{
	    throw new BadUnitException(
		"\"" + unit + "\" is not a unit of time");
	}
	_unit = unit;
	_origin = origin;
    }


    /*
     * From UnitImpl:
     */

    /**
     * Clones this unit, changing the identifier.
     * @param id		The new identifier.
     * @return			This unit with the new identifier.
     */
    public Unit
    clone(UnitName id)
    {
	Unit	clone;
	try
	{
	    clone = new TimeScaleUnit(getUnit(), getOrigin(), id);
	}
	catch (UnitException e)
	{
	    clone = null;	// can't happen
	}
	return clone;
    }


    /**
     * Indicates if this unit is semantically identical to an object.
     * @param object		The object.
     * @return			<code>true</code> if and only if this unit
     *				is semantically identical to <code>object
     *				</code>.
     */
    public boolean
    equals(Object object)
    {
	return
	    object instanceof TimeScaleUnit
		? super.equals(object) &&
		  getUnit().equals(((TimeScaleUnit)object).getUnit()) &&
		  getOrigin().equals(((TimeScaleUnit)object).getOrigin())
		: false;
    }


    /**
     * Returns a Converter for converting numeric values from this unit to
     * another unit.
     * @param outputUnit	The other unit.  Shall be a TimeScaleUnit.
     * @return			A Converter.
     * @throws ConversionException	<code>outputUnit</code> is not a
     *					TimeScaleUnit.
     */
    public Converter
    getConverterTo(Unit outputUnit)
	throws ConversionException
    {
	return new MyConverter(this, outputUnit);
    }


    /**
     * Returns the derived unit underlying the reference time unit.
     * @return			The derived unit underlying the reference
     *				time unit.
     */
    public DerivedUnit
    getDerivedUnit()
    {
	return getUnit().getDerivedUnit();
    }


    /**
     * Returns the time origin.
     * @return			The time origin.
     */
    public Date
    getOrigin()
    {
	return _origin;
    }


    /**
     * Returns the reference unit.
     * @return			The reference unit.
     */
    public Unit
    getUnit()
    {
	return _unit;
    }


    /**
     * Indicates if numeric values in this unit are convertible to another
     * unit.
     * @param that		The other unit.
     * @return			<code>true</code> if and only if numeric
     *				values in this unit are convertible to <code>
     *				that</code>.
     */
    public final boolean
    isCompatible(Unit that)
    {
	return that instanceof TimeScaleUnit;
    }


    /**
     * Indicates if this unit is dimensionless.  TimeScaleUnit-s are never
     * dimensionless.
     * @return			<code>false</code>.
     */
    public boolean
    isDimensionless()
    {
	return false;	// a TimeScaleUnit is never dimensionless by definition
    }


    /**
     * Tests this class.
     */
    public static void
    main(String[] args)
	throws	Exception
    {
	BaseUnit	second =
	    BaseUnit.getOrCreate(
		UnitName.newUnitName("second", null, "s"),
		BaseQuantity.TIME);
	TimeZone	tz = TimeZone.getTimeZone("UTC");
	Calendar	calendar = Calendar.getInstance(tz);
	calendar.clear();
	calendar.set(1970, 0, 1);
	TimeScaleUnit	epoch = new TimeScaleUnit(second, calendar.getTime());
    }


    /**
     * Divides this unit by another unit.  This operation is invalid.
     * @param that		The other unit.
     * @return			The quotient of dividing this unit by the
     *				other unit.
     * @throws DivideException	Illegal operation.  Always thrown.
     */
    protected Unit
    myDivideBy(Unit that)
	throws DivideException
    {
	throw new DivideException(this);
    }


    /**
     * Divides this unit into another unit.  This operation is invalid.
     * @param that		The other unit.
     * @return			The quotient of dividing this unit into the
     *				other unit.
     * @throws DivideException	Illegal operation.  Always thrown.
     */
    protected Unit
    myDivideInto(Unit that)
	throws DivideException
    {
	throw new DivideException(that, this);
    }


    /**
     * Multiplies this unit by another unit.  This operation is invalid.
     * @param that		The other unit.
     * @return			The product of multiplying this unit by
     *				the other unit.
     * @throws MultiplyException	Illegal operation.  Always thrown.
     */
    protected Unit
    myMultiplyBy(Unit that)
	throws MultiplyException
    {
	throw new MultiplyException(this);
    }


    /**
     * Raises this unit to a power.  This operation is invalid.
     * @param power		The power.
     * @return			The result of raising this unit to the power.
     * @throws RaiseException	Illegal operation.  Always thrown.
     */
    protected Unit
    myRaiseTo(RationalNumber power)
	throws RaiseException
    {
	throw new RaiseException(this);
    }


    /**
     * Returns the string representation of this unit.
     * @return			The string representation of this unit.
     */
    public String
    toString()
    {
	String	string = super.toString();	// get symbol or name
	return string != null
		? string
		: getUnit().toString() + dateFormat.format(getOrigin());
    }
}