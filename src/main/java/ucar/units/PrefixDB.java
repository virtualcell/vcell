package ucar.units;

import java.util.Iterator;

/**
 * Interface for a database of unit prefixes.
 * @author Steven R. Emmerson
 * @version $Id: PrefixDB.java,v 1.4 2000/07/18 20:15:22 steve Exp $
 */
public interface
PrefixDB
{
    /**
     * Adds a prefix to the database by name.
     * @param name		The name of the prefix.
     * @param value		The value of the prefix.
     * @throws PrefixExistsException	A prefix with the same name already
     *					exists in the database.
     * @throws PrefixDBAccessException	Prefix database access failure.
     */
    public void
    addName(String name, double value)
	throws PrefixExistsException, PrefixDBAccessException;

    /**
     * Adds a prefix to the database by symbol.
     * @param name		The symbol for the prefix.
     * @param value		The value of the prefix.
     * @throws PrefixExistsException	A prefix with the same symbol already
     *					exists in the database.
     * @throws PrefixDBAccessException	Prefix database access failure.
     */
    public void
    addSymbol(String symbol, double value)
	throws PrefixExistsException, PrefixDBAccessException;

    /**
     * Gets a prefix from the database by name.
     * @param name		The name of the prefix.
     * @return prefix		The prefix or null.
     * @throws PrefixDBAccessException	Prefix database access failure.
     */
    public Prefix
    getPrefixByName(String name)
	throws PrefixDBAccessException;

    /**
     * Gets a prefix from the database by symbol.
     * @param symbol		The symbol for the prefix.
     * @return prefix		The prefix or null.
     * @throws PrefixDBAccessException	Prefix database access failure.
     */
    public Prefix
    getPrefixBySymbol(String symbol)
	throws PrefixDBAccessException;

    /**
     * Gets a prefix from the database by value.
     * @param value		The value for the prefix.
     * @return prefix		The prefix or null.
     * @throws PrefixDBAccessException	Prefix database access failure.
     */
    public Prefix
    getPrefixByValue(double value)
	throws PrefixDBAccessException;

    /**
     * Gets a string representation of this database.
     * @return			A string representation of this database.
     */
    public String
    toString();

    /**
     * Gets an iterator over the entries in the database.
     * @return			An iterator over the database.
     */
    public Iterator
    iterator();
}
