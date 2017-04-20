package ucar.units;

/**
 * Interface for converting numeric values from one unit to another.
 * @author Steven R. Emmerson
 * @version $Id: Converter.java,v 1.4 2000/07/18 20:15:18 steve Exp $
 * @see Unit#getConverterTo(Unit)
 */
public interface
Converter
{
    /**
     * Converts a numeric value.
     * @param amount		The numeric value to convert.
     * @return			The converted numeric value.
     */
    public float
    convert(float amount);

    /**
     * Converts a numeric value.
     * @param amount		The numeric value to convert.
     * @return			The converted numeric value.
     */
    public double
    convert(double amount);

    /**
     * Converts an array of numeric values.
     * @param amounts		The numeric values to convert.
     * @return			The converted numeric values in allocated
     *				space.
     */
    public float[]
    convert(float[] amounts);

    /**
     * Converts an array of numeric values.
     * @param amounts		The numeric values to convert.
     * @return			The converted numeric values in allocated
     *				space.
     */
    public double[]
    convert(double[] amounts);

    /**
     * Converts an array of numeric values.
     * @param input		The numeric values to convert.
     * @param output		The converted numeric values.  May be
     *				same array as <code>input</code>.
     * @return			<code>output</code>.
     */
    public float[]
    convert(float[] input, float[] output);

    /**
     * Converts an array of numeric values.
     * @param input		The numeric values to convert.
     * @param output		The converted numeric values.  May be
     *				same array as <code>input</code>.
     * @return			<code>output</code>.
     */
    public double[]
    convert(double[] input, double[] output);
}
