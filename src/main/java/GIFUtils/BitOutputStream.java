package GIFUtils;

/*
 * BitOutputStream.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 25/08/98  Initial Version
 * 26/08/98  Made writeBits the workhorse method, as opposed to writeBit:
 *           this should give a speed increase.
 */

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * BitOutputStream allows the writing of 1 to 32 bits at a time, on any
 * bit boundary.  Bits are written to bytes, least significant bit first.
 *
 * @author Benjamin E. Norman
 */

public class BitOutputStream extends FilterOutputStream 
{
	/** any cached bits which need to be written to the underlying stream */
	protected int cachedBits;

	/** the number of valid bits in 'cachedBits'.  This is never > 7;
			only parital bytes are cached.  */
	protected int numCachedBits;

	public BitOutputStream(OutputStream os) 
	{
		super(os);

		// initialize the cache
		cachedBits = 0;
		numCachedBits = 0;
	}
	/**
	 * Flush any cached bits and bytes.
	 */
	public void flush() throws IOException
	{
		// write any cached bits
		if (numCachedBits > 0)
		{
			super.write(cachedBits);
		}

		// flush the stream
		super.flush();
	}
	/**
	 * Write a byte at the current bit boundary.
	 */
	public void write(int byteValue) throws IOException
	{
		writeBits(byteValue, 8);
	}
	/**
	 * Write a single bit (1 or 0) to the underlying output stream.
	 */
	public void writeBit(int bit) throws IOException
	{
		writeBits(bit, 1);
	}
	/**
	 * Write the given number of bits (1 to 32) from the given value to the 
	 * underlying output stream.
	 */
	public void writeBits(int value, int numBits) throws IOException
	{
		// check for valid bit length
		if (numBits > 32 || numBits < 1)
		{
			throw new IllegalArgumentException(numBits + 
				" is not between 1 and 32.");
		}

		// fill and write out the cache if necessary & possible
		if (numCachedBits > 0 && (numCachedBits + numBits) >= 8)
		{
			// transfer bits to the cache to make an even 8
			cachedBits = ( cachedBits | (value << numCachedBits) ) & 0xff;
			value >>>= 8 - numCachedBits;
			numBits -= 8 - numCachedBits;

			// write out the cache
			super.write(cachedBits);
			cachedBits = 0;
			numCachedBits = 0;
		}

		// write out any full bytes possible
		while ((int) (numBits / 8) > 0)
		{
			super.write (value & 0xff);
			value >>>= 8;
			numBits -= 8;
		}

		// cache any remaining bits
		if (numBits > 0)
		{
			cachedBits = (cachedBits | (value << numCachedBits) ) & 0xff;
			numCachedBits += numBits;
		}
	}
} // BitOutputStream
