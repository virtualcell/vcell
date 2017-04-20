package GIFUtils;

/*
 * GIFInputStream.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 22/08/98  Initial Version
 * 10/09/98  fixed clear code handling bug
 */

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * GIFInputStream provides readXXX() methods useful for reading in
 * the contents of a GIF file.  Included are methods which handle
 * LZW decompression of the image data.
 *
 * @author Benjamin E. Norman
 */

public class GIFInputStream extends BitInputStream 
{
	/** the maximum code size in bits for GIF LZW decompression */
	public static final int MAXIMUM_CODE_LENGTH = 12;

	/** the string table for LZW decompression */
	protected LZWStringTable stringTable;

	public GIFInputStream(InputStream is) 
	{
		super(is);
	}
	/**
	 * Read an 8 bit byte at the current bit boundary.
	 *
	 * @exception EOFException if 8 bits are not available.
	 */
	public byte readByte() throws IOException
	{
		return (byte) readBits(8);
	}
	/**
	 * Read a code from the given BitInputStream using the code
	 * size from the instance LZW string table.  Therefore, this should
	 * only be called after (usually by) readImageData(), which instantiates
	 * the string table.
	 */
	protected short readCode(BitInputStream bis) throws IOException
	{
		int numBits = stringTable.getNextCodeSize();
		return (short) bis.readBits(numBits);
	}
	/**
	 * Reads 3 bytes and intereprets them as an RRGGBB hex color.
	 *
	 * @exception EOFException if EOF is reached before 3 bytes are read
	 */
	public Color readColor() throws IOException
	{
		int red = read();
		int green = read();
		int blue = read();
		return new Color( ((red & 0xff) << 16) | 
			((green & 0xff) << 8) | (blue & 0xff));
	}
	/**
	 * Read data sub-blocks from the underlying input stream until a block
	 * terminator (a single zero byte) is encountered.  All bytes read 
	 * except for the first byte of each sub-block are returned.
	 */
	public byte[] readDataBlock() throws IOException
	{
		Vector block = new Vector();
		byte[] subBlock = null;
		
		// read in the blocks
		int total = 0;
		while ((subBlock = readDataSubBlock()).length > 0)
		{
			total += subBlock.length;
			block.addElement(subBlock);
		}

		// assemble them into a single array
		byte[] retVal = new byte[total];
		total = 0;
		for (int i = 0; i < block.size(); i++)
		{
			subBlock = (byte[]) block.elementAt(i);
			System.arraycopy(subBlock, 0, retVal, total, subBlock.length);
			total += subBlock.length;
		}

		return retVal;
	}
	/**
	 * Read a data sub-block from the underlying stream.  The first byte read
	 * is taken to be the sub-block's length and is not returned in the array.
	 * 
	 * @exception EOFException if a complete block is not read before EOF
	 */
	public byte[] readDataSubBlock() throws IOException
	{
	  int len = readByte() & 0xff;
		
		byte[] subBlock = new byte[len];
		for (int i = 0; i < len; i++)
		{
			subBlock[i] = readByte();
		}

		return subBlock;
	}
	/** 
	 * Read the compressed data for a table-based image and return the 
	 * decompressed raster data.  Each byte in the return array will 
	 * correspond to one pixel, regardless of image bit depth.  The root
	 * code size is read as a single byte before the image data sub-blocks.
	 * The length of the uncompressed data is required so that the array may 
	 * be created; this should
	 * be simply the image width times the image height.
	 *
	 * @exception GIFFormatException if EOF is reached prematurely
	 *                               or the LZW decoding fails
	 */
	public byte[] readImageData(int length) throws IOException
	{
		byte[] pixels = new byte[length];
		int nextPixel = 0;
		
		// read the root code size and the compressed data block
		int rootCodeSize = readByte();
		BitInputStream bis = new BitInputStream( 
			new ByteArrayInputStream( readDataBlock() ) );

		// initialize the decompression parameters
		short clearCode = (short)(1 << rootCodeSize);
		short endCode = (short)(clearCode + 1);
		stringTable = new LZWStringTable(rootCodeSize, MAXIMUM_CODE_LENGTH, 2);

		short oldCode, code;
		byte[] string, prefix;
		short character;

		try
		{
			// keep getting codes until a non-clear code is found.
			//   according to spec, the first code SHOULD be a clear code,
			//   but we force it to be safe
			stringTable.clear();
			while ((oldCode = readCode(bis)) == clearCode){}
					
			// check for (unlikely) end code
			if (oldCode == endCode)
			{
				return pixels;
			}

			// get the string for the old code ( == old code, a root )
			prefix = new byte[1];
			prefix[0] = (byte)oldCode;

			// output old code's string
			pixels[nextPixel++] = prefix[0];

			// loop through all the codes until an end code is found
			while(true)
			{
				// get the next code
				code = readCode(bis);

				// check for clear code and clear the table if found
				if (code == clearCode)
				{
					stringTable.clear();
					// skip any extra clear codes
					while ((oldCode = readCode(bis)) == clearCode){}
					
					// check for (unlikely) end code
					if (oldCode == endCode)
					{
						return pixels;
					}

					// get the string for the old code ( == old code, a root )
					prefix = new byte[1];
					prefix[0] = (byte)oldCode;

					// output old code's string
					pixels[nextPixel++] = prefix[0];
					continue;
				}
				// check for end code and finish if found
				if (code == endCode)
				{
					break;
				}

				// get the string corresponding to the current code
				string = stringTable.stringForCode(code);

				// output that string
				if (string == null)
				{ // the string must look like K[...]K (same 1st & last chars)
					string = new byte[prefix.length + 1];
					System.arraycopy(prefix, 0, string, 0, prefix.length);
					string[string.length - 1] = prefix[0];
				}
				System.arraycopy(string, 0, pixels, nextPixel, string.length);
				nextPixel += string.length;

				// add the next string to the string table
				character = (short) string[0];
				stringTable.addString(oldCode, character);

				// save the old code and string
				oldCode = code;
				prefix = string;
			}
		}
		catch(LZWException ex)
		{
			ex.printStackTrace();
			throw new GIFFormatException("LZW decoding failed");
		}

		return pixels;
	}
	/**
	 * Reads an unsigned LSB-first 16 byte word from the underlying stream.
	 *
	 * @exception EOFException if EOF is reached before 2 bytes are read
	 */
	public int readWord() throws IOException
	{
		int lsByte = readByte();
		int msByte = readByte();
		return (lsByte & 0xff) | ((msByte & 0xff) << 8);
	}
} // GIFInputStream
