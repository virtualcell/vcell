package ucar.units_vcell;

/**
 * Provides support for errors in unit string specifications.
 *
 * @author Steven R. Emmerson
 * @version $Id: SpecificationException.java,v 1.4 2000/07/18 20:15:27 steve Exp $
 */
public class
SpecificationException
    extends	UnitException
{
    /**
     * Constructs from an error message.
     * @param reason		The error message.
     */
    public
    SpecificationException(String reason)
    {
	super("Specification error: " + reason);
    }
}


