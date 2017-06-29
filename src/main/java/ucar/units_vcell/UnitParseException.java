package ucar.units_vcell;

/**
 * Provides support for errors in unit specifications.
 *
 * @author Steven R. Emmerson
 * @version $Id: UnitParseException.java,v 1.4 2000/07/18 20:15:36 steve Exp $
 */
public class
UnitParseException
    extends	SpecificationException
{
    /**
     * Constructs from a reason.
     * @param reason		The reason for the failure.
     */
    public
    UnitParseException(String reason)
    {
	super("Parse error: " + reason);
    }
}
