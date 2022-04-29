package ucar.units_vcell;

import java.math.BigDecimal;
import java.math.MathContext;
/**
 * Provides support for a unit that is a mutiplicative factor of a
 * reference unit.
 *
 * Instances of this class are immutable.
 *
 * @author Steven R. Emmerson
 * @version $Id: ScaledUnit.java,v 1.4 2000/07/18 20:15:27 steve Exp $
 */
public final class
ScaledUnit
    extends	UnitImpl
    implements	DerivableUnit
{
    /**
     * The multiplicative factor.
     * @serial
     */
    private final double	_scale;

    /**
     * The reference unit.
     * @serial
     */
    private final Unit		_unit;

    /**
     * Constructs from a multiplicative factor.  Returns a dimensionless
     * unit whose value is the multiplicative factor rather than unity.
     * @param scale		The multiplicative factor.
     */
    public
    ScaledUnit(double scale)
    {
	this(scale, DerivedUnitImpl.DIMENSIONLESS);
    }


    /**
     * Constructs from a multiplicative factor and a reference unit.
     * @param scale		The multiplicative factor.
     * @param unit		The reference unit.
     */
    public
    ScaledUnit(double scale, Unit unit)
    {
	this(scale, unit, null);
    }


/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 11:36:22 PM)
 * @param scale double
 * @param unit ucar.units_vcell.Unit
 * @param id ucar.units_vcell.UnitName2
 */
public ScaledUnit(double scale, Unit unit, UnitName id) {
	super(id);
	this._scale = scale;
	this._unit = unit;
}


    /*
     * From UnitImpl:
     */

    /**
     * Clones this unit, changing the identifier.
     * @param id		The new identifier.
     * @return			A ScaledUnit with the new identifier.
     */
    public Unit
    clone(UnitName id)
    {
	return new ScaledUnit(_scale, getUnit(), id);
    }


    /**
     * Converts numeric values from the underlying derived unit to this unit.
     * @param input		The numeric values in the underlying derived
     *				unit.
     * @param output		The equivalent values in this unit.
     * @return			<code>output</code>.
     * @throws ConversionException	Can't convert values.
     */
    public double[]
    fromDerivedUnit(double[] input, double[] output)
	throws ConversionException
    {
	if (!(_unit instanceof DerivableUnit))
	    throw new ConversionException(getDerivedUnit(), this);
	((DerivableUnit)getUnit()).fromDerivedUnit(input, output);
	double	scale = getScale();
	for (int i = input.length; --i >= 0; )
	    output[i] /= scale;
	return output;
    }


    /**
     * Converts numeric values from the underlying derived unit to this unit.
     * @param input		The numeric values in the underlying derived
     *				unit.
     * @param output		The equivalent values in this unit.
     * @return			<code>output</code>.
     * @throws ConversionException	Can't convert values.
     */
    public float[]
    fromDerivedUnit(float[] input, float[] output)
	throws ConversionException
    {
	if (!(_unit instanceof DerivableUnit))
	    throw new ConversionException(getDerivedUnit(), this);
	((DerivableUnit)getUnit()).fromDerivedUnit(input, output);
	float	scale = (float)getScale();
	for (int i = input.length; --i >= 0; )
	    output[i] /= scale;
	return output;
    }


    /**
     * Converts a numeric value from the underlying derived unit to this unit.
     * @param amount		The numeric value in the underlying derived
     *				unit.
     * @return			The equivalent value in this unit.
     * @throws ConversionException	Can't convert value.
     */
    public double
    fromDerivedUnit(double amount)
	throws ConversionException
    {
	if (!(_unit instanceof DerivableUnit))
	    throw new ConversionException(getDerivedUnit(), this);
	double value = ((DerivableUnit)getUnit()).fromDerivedUnit(amount)/getScale();
    BigDecimal bd = new BigDecimal(value);
    bd = bd.round(new MathContext(15));
	return bd.doubleValue();
    }


    /**
     * Converts a numeric value from the underlying derived unit to this unit.
     * @param amount		The numeric value in the underlying derived
     *				unit.
     * @return			The equivalent value in this unit.
     * @throws ConversionException	Can't convert value.
     */
    public float
    fromDerivedUnit(float amount)
	throws ConversionException
    {
	return (float)fromDerivedUnit((double)amount);
    }


    /**
     * Gets the derived unit underlying this unit.
     * @return			The derived unit which underlies this unit.
     */
    public DerivedUnit
    getDerivedUnit()
    {
	return getUnit().getDerivedUnit();
    }


    /**
     * Returns the multiplicative factor.
     * @return			The multiplicative factor.
     */
    public double
    getScale()
    {
	return _scale;
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
     * Indicates if this unit is semantically identical to an object.
     * @param object		The object.
     * @return			<code>true</code> if an only if this unit
     *				is semantically identical to <code>object
     *				</code>.
    public boolean
    equals(Object object)
    {
	return
	    object instanceof ScaledUnit
		? super.equals(object) &&
		  getScale() == ((ScaledUnit)object).getScale() &&
		  getUnit().equals(((ScaledUnit)object).getUnit())
		: getScale() == 1.0 && getUnit().equals(object);
    }

    /**
     * Indicates if this unit is dimensionless.  A ScaledUnit is dimensionless
     * if and only if the reference unit is dimensionless.
     * @return			<code>true</code> if and only if this unit
     *				is dimensionless.
     */
    public boolean
    isDimensionless()
    {
	return getUnit().isDimensionless();
    }


    /**
     * Tests this class.
     */
    public static void
    main(String[] args)
	throws	Exception
    {
	BaseUnit	meter =
	    BaseUnit.getOrCreate(
		UnitName.newUnitName("meter", null, "m"),
		BaseQuantity.LENGTH);
	ScaledUnit	nauticalMile = new ScaledUnit(1852f, meter);
	System.out.println(
	    "nauticalMile.getUnit().equals(meter)=" +
	    nauticalMile.getUnit().equals(meter));
	ScaledUnit	nauticalMileMeter =
	    (ScaledUnit)nauticalMile.multiplyBy(meter);
	System.out.println("nauticalMileMeter.divideBy(nauticalMile)=" + 
	    nauticalMileMeter.divideBy(nauticalMile));
	System.out.println("meter.divideBy(nauticalMile)=" + 
	    meter.divideBy(nauticalMile));
	System.out.println("nauticalMile.raiseTo(2)=" + 
	    nauticalMile.raiseTo(new RationalNumber(2)));
	System.out.println("nauticalMile.toDerivedUnit(1.)=" + 
	    nauticalMile.toDerivedUnit(1.));
	System.out.println(
	    "nauticalMile.toDerivedUnit(new float[]{1,2,3}, new float[3])[1]="+ 
	    nauticalMile.toDerivedUnit(new float[]{1,2,3}, new float[3])[1]);
	System.out.println("nauticalMile.fromDerivedUnit(1852.)=" + 
	    nauticalMile.fromDerivedUnit(1852.));
	System.out.println(
	    "nauticalMile.fromDerivedUnit(new float[]{1852},new float[1])[0]="+ 
	    nauticalMile.fromDerivedUnit(new float[]{1852}, new float[1])[0]);
	System.out.println(
	    "nauticalMile.equals(nauticalMile)=" +
	    nauticalMile.equals(nauticalMile));
	ScaledUnit	nautical2Mile = new ScaledUnit(2, nauticalMile);
	System.out.println(
	    "nauticalMile.equals(nautical2Mile)=" +
	    nauticalMile.equals(nautical2Mile));
	System.out.println(
	    "nauticalMile.isDimensionless()=" +
	    nauticalMile.isDimensionless());
	BaseUnit	radian =
	    BaseUnit.getOrCreate(
		UnitName.newUnitName("radian", null, "rad"),
		BaseQuantity.PLANE_ANGLE);
	ScaledUnit	degree = new ScaledUnit(3.14159/180, radian);
	System.out.println("degree.isDimensionless()=" + 
	    degree.isDimensionless());;
    }


    /**
     * Divides this unit by another unit.
     * @param that		The other unit.
     * @return			The quotient of this unit divided by the
     *				other unit.
     * @throws OperationException	Can't divide these units.
     */
    protected Unit
    myDivideBy(Unit that)
	throws OperationException
    {
	return that instanceof ScaledUnit
		? new ScaledUnit(getScale()/((ScaledUnit)that).getScale(),
		    getUnit().divideBy(((ScaledUnit)that).getUnit()))
		: new ScaledUnit(getScale(), getUnit().divideBy(that));
    }


    /**
     * Divides this unit into another unit.
     * @param that		The other unit.
     * @return			The quotient of this unit divided into the
     *				other unit.
     * @throws OperationException	Can't divide these units.
     */
    protected Unit
    myDivideInto(Unit that)
	throws OperationException
    {
	return that instanceof ScaledUnit
		? new ScaledUnit(((ScaledUnit)that).getScale()/getScale(),
		    getUnit().divideInto(((ScaledUnit)that).getUnit()))
		: new ScaledUnit(1/getScale(), getUnit().divideInto(that));
    }


    /**
     * Multiplies this unit by another unit.
     * @param that		The other unit.
     * @return			The product of this unit and the other unit.
     * @throws MultiplyException	Can't multiply these units together.
     */
    protected Unit
    myMultiplyBy(Unit that)
	throws MultiplyException
    {
	return that instanceof ScaledUnit
		? new ScaledUnit(getScale()*((ScaledUnit)that).getScale(),
		    getUnit().multiplyBy(((ScaledUnit)that).getUnit()))
		: new ScaledUnit(getScale(), getUnit().multiplyBy(that));
    }


    /**
     * Raises this unit to a power.
     * @param that		The power.
     * @return			The result of raising this unit to the power.
     * @throws RaiseException	Can't raise this unit to a power.
     */
    protected Unit
    myRaiseTo(RationalNumber power)
	throws RaiseException
    {
	return new ScaledUnit(
	    Math.pow(getScale(), power.doubleValue()), getUnit().raiseTo(power));
    }


    /**
     * Converts numeric values from this unit to the underlying derived
     * unit.
     * @param input		The numeric values in this unit.
     * @param output		The equivalent values in the underlying
     *				derived unit.
     * @return			<code>output</code>.
     * @throws ConversionException	Can't convert values to the underlying
     *					derived unit.
     */
    public double[]
    toDerivedUnit(double[] input, double[] output)
	throws ConversionException
    {
	double	scale = getScale();
	for (int i = input.length; --i >= 0; )
	    output[i] = input[i]*scale;
	if (!(_unit instanceof DerivableUnit))
	    throw new ConversionException(this, getDerivedUnit());
	return ((DerivableUnit)getUnit()).toDerivedUnit(output, output);
    }


    /**
     * Converts numeric values from this unit to the underlying derived
     * unit.
     * @param input		The numeric values in this unit.
     * @param output		The equivalent values in the underlying
     *				derived unit.
     * @return			<code>output</code>.
     * @throws ConversionException	Can't convert values to the underlying
     *					derived unit.
     */
    public float[]
    toDerivedUnit(float[] input, float[] output)
	throws ConversionException
    {
	float	scale = (float)getScale();
	for (int i = input.length; --i >= 0; )
	    output[i] = input[i]*scale;
	if (!(_unit instanceof DerivableUnit))
	    throw new ConversionException(this, getDerivedUnit());
	return ((DerivableUnit)getUnit()).toDerivedUnit(output, output);
    }


    /**
     * Converts a numeric value from this unit to the underlying derived
     * unit.
     * @param amount		The numeric value in this unit.
     * @return			The equivalent value in the underlying
     *				derived unit.
     * @throws ConversionException	Can't convert value to the underlying
     *					derived unit.
     */
    public double
    toDerivedUnit(double amount)
	throws ConversionException
    {
	if (!(_unit instanceof DerivableUnit))
	    throw new ConversionException(this, getDerivedUnit());
		double value = ((DerivableUnit)_unit).toDerivedUnit(amount*getScale());
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.round(new MathContext(15));
		return bd.doubleValue();
    }


    /**
     * Converts a numeric value from this unit to the underlying derived
     * unit.
     * @param amount		The numeric value in this unit.
     * @return			The equivalent value in the underlying
     *				derived unit.
     * @throws ConversionException	Can't convert value to the underlying
     *					derived unit.
     */
    public float
    toDerivedUnit(float amount)
	throws ConversionException
    {
	return (float)toDerivedUnit((double)amount);
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
		: Double.toString(getScale()) + " " + getUnit().toString();
    }
}