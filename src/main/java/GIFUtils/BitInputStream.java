package GIFUtils;

/*
 * BitInputStream.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 22/08/98  Initial Version
 */

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * BitInputStream allows the reading of 1 to 32 bits at a time, on any
 * bit boundary.  Bits are read from bytes, least significant bit first.
 *
 * @author Benjamin E. Norman
 */

public class BitInputStream extends FilterInputStream 
{
	/** any cached bits which have been read in from the underlying stream
			but not yet returned to the user */
	protected long cachedBits;

	/** the number of valid bits in 'cachedBits'.  This should always be >= 32
			for efficiency, unless EOF has been reached */
	protected int numCachedBits;

	/**
	 * Create a BitInputStream, assuming that at least 5 bytes are avaliable 
	 * on the underlying stream.
	 */
	public BitInputStream(InputStream is) 
	{
		super(is);

		// fill the cache with 40 bits.
		int byt;
		cachedBits = 0;
		try
		{
			for (int i = 0; (i < 5) && ((byt = super.read()) != -1); i++)
			{
				cachedBits |= (long)byt << (i * 8);
				numCachedBits += 8;
			}
		}
		catch (IOException ex)
		{
			System.err.println(
				"BitInputStream.<init>:  IOException while reading intial 5 bytes:");
			ex.printStackTrace();
		}
	}
	/**
	 * Read a byte at the current bit boundary.
	 * Returns -1 if 8 bits are not available before EOF is reached.
	 */
	public int read() throws IOException
	{
		try
		{
			return readBits(8);
		}
		catch (EOFException ex)
		{
			return -1;
		}
	}
	/**
	 * Read the next bit from the underlying input stream.
	 * Returns -1 on EOF.
	 */
	public int readBit() throws IOException
	{
		int byt, retVal = 0;

		// fill the cache if we're down to 32 bits.
		if (numCachedBits == 32) // < 32 means we previously hit EOF
		{
			if ((byt = super.read()) != -1)
			{
				cachedBits |= (long)byt << 32;
				numCachedBits = 40;
			}
		}
		else if (numCachedBits == 0) // we're completely done
		{
			return -1;
		}
		
		// read the bit from the cache
		retVal = (int)cachedBits & 1;
		cachedBits >>>= 1;
		numCachedBits--;

		return retVal;
	}
	/**
	 * Read the given number of bits (1 to 32) from the underlying input stream.
	 *
	 * @exception EOFException if the given number of bits are not available
	 *                         before EOF.
	 */
	public int readBits(int numBits) throws IOException
	{
		int retVal = 0;
		
		if (numBits > 32 || numBits < 1)
		{
			throw new IllegalArgumentException(numBits + 
				" is not between 1 and 32.");
		}

		if (numBits > numCachedBits)
		{
			throw new EOFException(numBits + " bits are not available.");
		}
		
		for (int i = 0; i < numBits; i++)
		{
			retVal |= readBit() << i;
		}

		return retVal;
	}
} // BitInputStream
