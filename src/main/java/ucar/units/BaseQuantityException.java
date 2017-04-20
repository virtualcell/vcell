package ucar.units;

import java.io.Serializable;

/**
 * Provides support for base quantity exceptions.
 * @author Steven R. Emmerson
 * @version $Id: BaseQuantityException.java,v 1.4 2000/07/18 20:15:17 steve Exp $
 */
public abstract class
BaseQuantityException
    extends	UnitException
    implements	Serializable
{
    /**
     * Constructs from nothing.
     */
    public
    BaseQuantityException()
    {}

    /**
     * Constructs from a message.
     */
    public
    BaseQuantityException(String message)
    { }
}
