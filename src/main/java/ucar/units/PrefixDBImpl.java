package ucar.units;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Provides a concrete implementation of a database of unit prefixes.
 *
 * Instances of this class are modifiable.
 *
 * @author Steven R. Emmerson
 * @version $Id: PrefixDBImpl.java,v 1.4 2000/07/18 20:15:23 steve Exp $
 */
public class
PrefixDBImpl
    implements	PrefixDB, Serializable
{
    /**
     * The set of prefix names.
     * @serial
     */
    private final SortedSet	nameSet;

    /**
     * The set of prefix symbols.
     * @serial
     */
    private final SortedSet	symbolSet;

    /**
     * The mapping between prefix values and prefixes.
     * @serial
     */
    private final Map		valueMap;

    /**
     * Constructs from nothing.
     */
    public
    PrefixDBImpl()
    {
	nameSet = new TreeSet();
	symbolSet = new TreeSet();
	valueMap = new TreeMap();
    }

    /**
     * Adds a prefix to the database by name.
     * @param name		The name of the prefix to be added.
     * @param value		The value of the prefix.
     * @throws PrefixExistsException	Another prefix with the same name 
     *					or value already exists in the database.
     */
    public void
    addName(String name, double value)
	throws PrefixExistsException
    {
	Prefix	prefix = new PrefixName(name, value);
	nameSet.add(prefix);
    }

    /**
     * Adds a prefix symbol to the database.
     * @param symbol		The symbol of the prefix to be added.
     * @param value		The value of the prefix.
     * @throws PrefixExistsException	Another prefix with the same symbol 
     *					or value already exists in the database.
     */
    public void
    addSymbol(String symbol, double value)
	throws PrefixExistsException
    {
	Prefix	prefix = new PrefixSymbol(symbol, value);
	symbolSet.add(prefix);
	valueMap.put(new Double(value), prefix);
    }

    /**
     * Gets a prefix by name.
     * @param name		The name to be matched.
     * @return			The prefix whose name matches or null.
     */
    public Prefix
    getPrefixByName(String string)
    {
	return getPrefix(string, nameSet);
    }

    /**
     * Gets a prefix by symbol.
     * @param symbol		The symbol to be matched.
     * @return			The prefix whose symbol matches or null.
     */
    public Prefix
    getPrefixBySymbol(String string)
    {
	return getPrefix(string, symbolSet);
    }

    /**
     * Gets a prefix by value.
     * @param value		The value to be matched.
     * @return			The prefix whose value matches or null.
     */
    public Prefix
    getPrefixByValue(double value)
    {
	return (Prefix)valueMap.get(new Double(value));
    }

    /**
     * Returns the prefix from the given set with the given identifier.
     * @param string		The prefix identifier.
     * @param set		The set to search.
     */
    private static Prefix
    getPrefix(String string, Set set)
    {
	int	stringLen = string.length();
	for (Iterator iter = set.iterator(); iter.hasNext(); )
	{
	    Prefix	prefix = (Prefix)iter.next();
	    int		comp = prefix.compareTo(string);
	    if (comp == 0)
		return prefix;
	    if (comp > 0)
		break;
	}
	return null;
    }

    /**
     * Returns a string representation of this database.
     * @return			A string representation of this database.
     */
    public String
    toString()
    {
	return
	    "nameSet=" + nameSet + 
	    "symbolSet=" + symbolSet + 
	    "valueMap=" + valueMap;
    }

    /**
     * Gets an iterator over the prefixes in the database.
     * @return			An iterator over the entries in the database.
     *				The objects returned by the iterator will be of
     *				type <code>Prefix</code>.
     */
    public Iterator
    iterator()
    {
	return nameSet.iterator();
    }

    /** 
     * Tests this class.
     */
    public static void
    main(String[] args)
	throws Exception
    {
	PrefixDB	db = new PrefixDBImpl();
	db.addName("mega", 1e6);
	System.out.println("mega=" + db.getPrefixByName("mega").getValue());
	db.addSymbol("m", 1e-3);
	System.out.println("m=" + db.getPrefixBySymbol("m").getValue());
	System.out.println("1e-3=" + db.getPrefixByValue(1e-3).getID());
    }
}
