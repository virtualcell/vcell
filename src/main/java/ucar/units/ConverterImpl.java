package ucar.units;

/**
 * Provides support for implementations of unit converters.  A unit converter
 * class may be created by subclassing this class and implementing the methods
 * <code>convert(double)</code>, <code>convert(float[] input, float[] output)
 * </code>, and <code>convert(double[] input, double[] output)</code> of
 * interface <code>Converter</code>.
 * 
 * @author Steven R. Emmerson
 * @version $Id: ConverterImpl.java,v 1.4 2000/07/18 20:15:19 steve Exp $
 */
public abstract class
ConverterImpl
    implements	Converter
{
    /**
     * Constructs from a "from" unit and a "to" unit.
     * @param fromUnit		The unit from which to convert.
     * @param toUnit		The unit to which to convert.
     * @throws ConversionException	The units are not convertible.
     */
    protected
    ConverterImpl(Unit fromUnit, Unit toUnit)
	throws ConversionException
    {
	if (!fromUnit.isCompatible(toUnit))
	    throw new ConversionException(fromUnit, toUnit);
    }

    /**
     * Factory method for creating a unit converter.
     * @param fromUnit		The unit from which to convert.
     * @param toUnit            The unit to which to convert.
     * @throws ConversionException      The units are not convertible.
     */
    public static Converter
    create(Unit fromUnit, Unit toUnit)
	throws ConversionException
    {
	return fromUnit.getConverterTo(toUnit);
    }

    /**
     * Converts a numeric value.
     * @param amount		The numeric value to convert.
     * @return			The converted numeric value.
     */
    public final float
    convert(float amount)
    {
	return (float)convert((double)amount);
    }

    /**
     * Converts an array of numeric values.
     * @param amounts		The numeric values to convert.
     * @return			The converted numeric values in allocated
     *				space.
     */
    public final float[]
    convert(float[] amounts)
    {
	return convert(amounts, new float[amounts.length]);
    }

    /**
     * Converts an array of numeric values.
     * @param amounts		The numeric values to convert.
     * @return			The converted numeric values in allocated
     *				space.
     */
    public final double[]
    convert(double[] amounts)
    {
	return convert(amounts, new double[amounts.length]);
    }
}
