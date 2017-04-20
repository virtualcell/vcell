package GIFUtils;

/*
 * LZWStringTable.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 20/08/98  Inital version
 * 27/08/98  switched to manual hashtable instead of java.util.Hashtable:
 *           profound increase in speed
 * 10/09/98  fixed bug which let nextCodeSize increase beyond maximum
 */
/**
 * <p>The string table for LZW compression / decompression.
 * As each string is added, it is assigned a unique integral compression code
 * which is sequentially generated.  Strings may then be recalled by these
 * codes.</p>
 *
 * <p>Variable lenth codes are used, and the proper length of the codes is
 * maintained by the table.  Maximum code size can be set from 3 to 15 
 * bits.  Root code size should be <= 8 bits.</p>
 *
 * <p>Strings are recursively defined to be a character preceeded by some 
 * prefix, which is either another string or empty.  Through the external
 * interface, strings are represented by the compression code of this prefix
 * combined with the final character.  Returned strings are decompressed
 * into byte arrays.</p>
 *
 * <p>A manual hash table is implemented for efficiency.  (This reduces the
 * time to save a 20000 pixel image from 90 seconds to less than 1 
 * second).</p>
 *
 * @author Benjamin E. Norman
 */
class LZWStringTable 
{
	//
	// constants
	//

	/** indicates the absence of a compression code */
	protected static final short NO_CODE= -1;
	// hashing params
	protected int HASH_SIZE; // depends on max code size
	protected static final int HASH_STEP = 2039; // why not? :)
	
	protected static final int[] HASH_SIZES =
	{ -1, -1, -1, 11, 23, 47, 97, 193, 383, 769, 1531, 3067, 6143, 12889,
		24571, 49157 }; // prime hash size ~ 1.5 * max code size

	//
	// LZW parameters
	//

	/** the maximum number of bits in a compression code */
	protected int maxCodeSize;
	/** the maximum number of compression codes allowed */
	protected int maxCodes;
	/** the number of restricted (special use) codes */
	protected int numRestrictedCodes;
	/** the size in bits of the basic code elements */
	protected int rootCodeSize;

	//
	// instance data
	//

	/** the uncompressed length of each string, indexed by that string's
			compression code.  This is purely for ease of decompression */
	protected short[] stringLengthsByStringCode;
	/** the compression codes for the prefix of each stored string, indexed by 
			the full string's compression code */
	protected short[] prefixCodesByStringCode;
	/** the last characters of each stored string, indexed by
			the full string's compression code */
	protected short[] charactersByStringCode;
	/** the compression codes for each stored string, indexed by that 
			full string's hash code */
	protected short[] stringCodesByHashCode;

	/** the next compression code to generate == number of strings in table */
	protected short nextCompressionCode;
	/** the code size in bits of the last code in the table */
	protected int currentCodeSize;
	/** the code size in bits of the next code to generate */
	protected int nextCodeSize;

	/**
	 * Construct a string table with the given root and maximum code sizes 
	 * (in bits) and number of restricted codes.  'maxCodeSize' should be
	 * 3 to 15 bits.  'rootCodeSize' should be between 2 and 8 bits.
	 * The 'clear' method should be called before use.
	 */
	public LZWStringTable(int rootCodeSize, int maxCodeSize, 
		int numRestrictedCodes)
	{
		// determine hash parameters
		this.maxCodeSize = maxCodeSize;
		maxCodes = 1 << maxCodeSize;
		this.numRestrictedCodes = numRestrictedCodes;
		this.rootCodeSize = rootCodeSize;
		HASH_SIZE = HASH_SIZES[maxCodeSize];

		stringLengthsByStringCode = new short[maxCodes];
		prefixCodesByStringCode = new short[maxCodes];
		charactersByStringCode = new short[maxCodes];
		stringCodesByHashCode = new short[HASH_SIZE];
	}
	/**
	 * Adds the string represented by the string for the compressed prefix
	 * plus the final character given.
	 *
	 * @exception LZWException if the table is full or the string is already
	 *                         in the table.
	 */
	public void addString(short prefixCode, short character) throws LZWException
	{
		// check for full table
		if (isFull())
		{
			throw new LZWException("Table is full");
		}

		// check to see if the string is already in the table
		int hashCode = hashCodeForString(prefixCode, character);
		if ( stringCodesByHashCode[hashCode] != NO_CODE )
		{
			throw new LZWException("String " + prefixCode + "." + character +
				" is already in table");
		}

		// add the string to the table with the next compression code
		short stringCode = nextCompressionCode++;
		prefixCodesByStringCode[stringCode] = prefixCode;
		charactersByStringCode[stringCode] = character;
		stringLengthsByStringCode[stringCode] = (short)((prefixCode == NO_CODE) ?
			1 : 1 + stringLengthsByStringCode[prefixCode]);
		stringCodesByHashCode[hashCode] = stringCode;

		//System.err.println("Added string " + prefixCode + "." + character +
		//	" with code " + stringCode + " and byte length " + 
		//	stringLengthsByStringCode[stringCode]);

		// make sure the compression code size is up to date
		currentCodeSize = nextCodeSize;
		if ( ( (nextCompressionCode & ~((1 << nextCodeSize) - 1)) > 0) // extra bit
			&& (nextCompressionCode < maxCodes) ) // don't increase past valid codes
		{
			//System.err.println("increasing compression code size from " + nextCodeSize);
			nextCodeSize++;
		}
	}
	/**
	 * Reset the string table by emptying it and then adding the root codes,
	 * which are the strings from 0 to (2 ^ rootCodeSize) - 1, and the
	 * restricted codes.  
	 */
	public void clear()
	{
		nextCompressionCode = 0;
		currentCodeSize = rootCodeSize;
		nextCodeSize = rootCodeSize;
		
		// clear the compression code array
		for (int i = 0; i < HASH_SIZE; i++)
		{
			stringCodesByHashCode[i] = NO_CODE;
		}

		// clear the length array (used to get strings from codes)
		for (int i = 0; i < maxCodes; i++)
		{
			stringLengthsByStringCode[i] = 0;
		}
		
		// add the root codes (single characters) and restricted codes
		try
		{
			int codes = (1 << rootCodeSize) + numRestrictedCodes;
			for (short i = 0; i < codes; i++)
			{
				addString(NO_CODE, i);
			}
		}
		catch (LZWException e)
		{
			System.err.println(
				"LZWStringTable.clear(): error adding codes to table!");
		}
	}
	/**
	 * Return compression code for a string, or NO_CODE if it is not found.
	 */
	public short codeForString(short prefixCode, short character)
	{
		return stringCodesByHashCode[ hashCodeForString(prefixCode, character) ];
	}
	/**
	 * Return the code size in bits of the last code added.
	 */
	public int getCurrentCodeSize()
	{
		return currentCodeSize;
	}
	/**
	 * Return the code size in bits of the next code to be added.
	 */
	public int getNextCodeSize()
	{
		return nextCodeSize;
	}
	/**
	 * Return the hash code for the given string, or the hash code corresponding
	 * to the first empty spot if the string is not in the table.
	 */
	protected int hashCodeForString(short prefixCode, short character)
	{
		// generate simple XOR hashcode
		int hashCode =  (((character & 0xff) << 8) ^ 
			(prefixCode & 0xffff)) % HASH_SIZE;
		short compressionCode;

		// perform secondary probes to find the string or an empty slot
		while ( ((compressionCode = stringCodesByHashCode[hashCode]) != NO_CODE) 
			&& ! (prefixCodesByStringCode[compressionCode] == prefixCode && 
				charactersByStringCode[compressionCode] == character) )
		{
			hashCode = (short)((hashCode + HASH_STEP) % HASH_SIZE);
		}

		return hashCode;
	}
	/**
	 * Return whether or not the maximum number of strings for the
	 * maximum code size have been added to the table.
	 */
	public boolean isFull()
	{
		return nextCompressionCode >= maxCodes;
	}
	/**
	 * Return the uncompressed string for the given code, or null if none.
	 */
	public byte[] stringForCode(short code)
	{
		short length = stringLengthsByStringCode[code];
		if (length == 0)
		{
			//System.err.println("LZWStringTable.stringForCode(" + code + "): length is 0");
			return null;
		}
		byte[] string = new byte[length];
		
		string[--length] = (byte) charactersByStringCode[code];
		// decompress each prefix
		while (--length >= 0) 
		{
			code = prefixCodesByStringCode[code];
			string[length] = (byte) charactersByStringCode[code];
		}

		return string;
	}
}
