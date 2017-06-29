package ucar.units_vcell;

import java.io.Serializable;

/**
 * Provides support for unit conversion exceptions.
 * @author Steven R. Emmerson
 * @version $Id: ConversionException.java,v 1.4 2000/07/18 20:15:18 steve Exp $
 */
public final class
ConversionException
    extends	UnitException
    implements	Serializable
{
    /**
     * Constructs from nothing.
     */
    public
    ConversionException()
    {}

    /**
     * Constructs from a message.
     * @param message		The error message.
     */
    private
    ConversionException(String message)
    { }

    /**
     * Constructs from a "from" unit and and "to" unit.
     * @param fromUnit		The unit from which a conversion was attempted.
     * @param toUnit		The unit to which a conversion was attempted.
     */
    public
    ConversionException(Unit fromUnit, Unit toUnit)
    {
	this("Can't convert from unit \"" +
	    fromUnit + "\" to unit \"" + toUnit + "\"");
    }
}
