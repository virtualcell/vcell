package GIFUtils;

/*
 * GIFOutputStream.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 25/08/98  Initial Version
 * 10/09/98  Fixed clear code handling
 */

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * GIFOutputStream provides writeXXX() methods useful for writing out
 * a bitmap image to a GIF file.  Included are methods which handle
 * LZW compression of the image data.
 *
 * @author Benjamin E. Norman
 */

public class GIFOutputStream extends BitOutputStream 
{
	/** the maximum code size in bits for GIF LZW compression */
	public static final int MAXIMUM_CODE_LENGTH = 12;

	/** the string table for LZW compression */
	protected LZWStringTable stringTable;

	public GIFOutputStream(OutputStream os) 
	{
		super(os);
	}
	/**
	 * Write a code to the given BitOutputStream using the current bit
	 * length from the string table.  (The string table is instantiated by
	 * the writeImageData() method, so this should not be called directly.)
	 */
	protected void writeCode(short code, BitOutputStream bos) 
		throws IOException
	{
		bos.writeBits(code, stringTable.getCurrentCodeSize());
	}
	/**
	 * Writes a color as 3 bytes in RRGGBB hex format.
	 */
	public void writeColor(Color color) throws IOException
	{
		write(color.getRed());
		write(color.getGreen());
		write(color.getBlue());
	}
	/**
	 * Write the given bytes to the underlying output stream as a series of
	 * data sub-blocks.  As many sub-blocks of size 255 as possible will be 
	 * written, then one smaller block as necessary.  A final sub-block of
	 * a single zero byte is written to terminate the block.
	 */
	public void writeDataBlock(byte[] block) throws IOException
	{
		byte[] subBlock = new byte[255];

		// calculate the number of full-sized sub-blocks necessary
		int numFullBlocks = (int) ( block.length / 255 );
		int partialBlockSize = block.length % 255;

		// write out the full sub-blocks
		for (int blockNumber = 0; blockNumber < numFullBlocks; blockNumber++)
		{
			System.arraycopy(block, blockNumber * 255, subBlock, 0, 255);
			writeDataSubBlock(subBlock);
		}

		// write out partial sub-blocks
		if (partialBlockSize > 0)
		{
			subBlock = new byte[partialBlockSize];
			System.arraycopy(block, block.length - partialBlockSize, subBlock, 
				0, partialBlockSize);
			writeDataSubBlock(subBlock);
		}

		// write out block terminator
		subBlock = new byte[0];
		writeDataSubBlock(subBlock);
	}
	/**
	 * Write a data sub-block from the underlying stream.  Before the given 
	 * bytes, the length of the block (0-255) is written.
	 */
	public void writeDataSubBlock(byte[] subBlock) throws IOException
	{
		write(subBlock.length);
		for (int i = 0; i < subBlock.length; i++)
		{
			write(subBlock[i]);
		}
	}
	/** 
	 * LZW compresses the given pixel data and writes it to the underlying
	 * output stream as a data block.  Before the data is written, the
	 * root code size is output.  This code size is just the given color depth
	 * of the image, or 2 if the depth is one.
	 */
	public void writeImageData(byte[] pixels, int bpp) 
		throws IOException
	{
		// establish the root code size
		int rootCodeSize = bpp;
		if (rootCodeSize == 1)
		{
			rootCodeSize++;
		}
		
		// create an output stream into a byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitOutputStream bos = new BitOutputStream( baos );

		// write out the root code size (outside of the data block)
		write(rootCodeSize);
		
		// initialize the compression parameters
		short clearCode = (short)(1 << rootCodeSize);
		short endCode = (short)(clearCode + 1);
		stringTable = new LZWStringTable(rootCodeSize, MAXIMUM_CODE_LENGTH, 2);

		short stringCode;  // the code for the complete string
		short prefixCode;  // the code for the prefix string
		short character;  // the current character

		// compress the data
		try
		{
			// GIF spec says to output clear code at beginning
			stringTable.clear();
			writeCode(clearCode, bos);

			// get the first character
			prefixCode = (short)(pixels[0] & 0xff);

			// compress each subsequent string found
			for (int i = 1; i < pixels.length; i++)
			{
				// read the next character
				character = (short)(pixels[i] & 0xff);
				
				// try to get the code for our combined (prefix + character) string
				stringCode = stringTable.codeForString(prefixCode, character);

				// if the combined string is in the table, we can keep compressing
				if (stringCode != LZWStringTable.NO_CODE)
				{
					prefixCode = stringCode;
				}
				// otherwise, output the code for the previous string and start over
				else
				{
					writeCode(prefixCode, bos);

					// see if the table needs cleared
					if (stringTable.isFull())
					{
						// clear the table
						writeCode(clearCode, bos);
						stringTable.clear();

						// get the next prefix
						prefixCode = character;
						continue;
					}

					// add the new combined string to the table and reset the prefix
					stringTable.addString(prefixCode, character);
					prefixCode = character;
				}
			}
			
			// output the last code and end code
			writeCode(prefixCode, bos);
			writeCode(endCode, bos);
		}
		catch(LZWException ex)
		{
			ex.printStackTrace();
			throw new GIFFormatException("LZW encoding failed");
		} 


		// write out the byte array we created as a data block
		bos.flush();
		writeDataBlock(baos.toByteArray());
	}
	/**
	 * Writes an unsigned LSB-first 16 byte word to the underlying stream.
	 */
	public void writeWord(int word) throws IOException
	{
		write(word);
		write(word >>> 8);
	}
} // GIFOutputStream
