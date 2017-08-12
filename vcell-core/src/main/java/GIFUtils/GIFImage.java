package GIFUtils;

/*
 * GIFImage.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 16/08/98  Initial Version
 * 24/08/98  Decoding/decompression works
 * 29/08/98  Encoding/compression works, transparency and interlacing are
 *           working as well.
 */

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <h2>Description</h2>
 *
 * <p>GIFImage represents an image as stored in a .gif file.  It can be used
 * to load or save images from disk without using AWT components and can
 * thus be run from a text console.</p>
 *
 * <p>Before using this package, it may help to become familiar with the 
 * terminology used regarding gif images.  Try looking at
 * <a href="http://www.geocities.co.jp/SiliconValley/3453/gif_info/spec/GIF89M.htm">this
 * page</a> for more information.  Alternatively, do a web search for 
 * "gif89a specification".</p>
 *
 * <p>GIFImage is capable of reading all extensions and images in a gif89a
 * file, but only the graphic control extension (used for transparency and
 * time delay) is currently supported for image creation.  Multiple images are
 * available (as in an animated .gif).  The term 'primary image' is taken
 * to mean the first image in a file.</p>
 *
 * <p>This package was originally built around GIFEncoder, by Adam Doppelt,
 * which was in turn based on gifsave.c, by Sverre H. Huseby.  None of
 * the current incarnation bears much resemblance to these works, however.</p>
 *
 * <p>GIFImage is currently very alpha-ish, and you may have to do some 
 * hacking if you want to take advantage of advanced features like time 
 * delays and looping.  But it's free, so don't gripe too hard:)  The 
 * <a href="http://www.personal.kent.edu/~bnorman/java/GIFUtils.zip">ZIP 
 * file</a> contains all the classes and source code.  In all seriousness,
 * if you find a bug, please <a href="mailto:bnorman@kent.edu">let me 
 * know</a>.</p>
 *
 * <h2>Examples</h2>
 * 
 * <h3>To extract the RGB pixels from a .gif image file:</h3>
 * <pre>
 *   GIFInputStream gis = new GIFInputStream(
 *     new FileInputStream(myFileName) );
 *   GIFImage gi = new GIFImage(gis);
 *   int[] rgbPixels = gi.getRGBPixels();
 * </pre>
 *
 * <h3>To create a .gif file from an array of RGB pixel values:</h3>
 * <pre>
 *   int[] rgbPixels = myPixelGenerationMethod();  // get pixels somehow
 *   GIFImage gi = new GIFImage(rgbPixels, imageWidth);
 *   GIFOutputStream gos = new GIFOutputStream(
 *     new FileOutputStream(args[0]) );
 *   gi.write(gos);
 * </pre>
 *
 * <h3>To create a GIFImage which makes grey pixels transparent:</h3>
 * <pre>
 *   GIFImage gi = new GIFImage(rgbPixels, imageWidth);
 *   gi.setTransparentColor(Color.grey);
 * </pre>
 * 
 * <h3>To create an animated .gif file using 3 images:</h3>
 * <pre>
 *   GIFImage gi = new GIFImage(rgbPixels1, imageWidth);  // first image (0)
 *   gi.addImage(rgbPixels2, imageWidth);  // second image (1)
 *   gi.addImage(rgbPixels3, imageWidth);  // third image (2)
 *   gi.setDelay(50);     // delay in 100th sec - defaults to first image (0)
 *   gi.setDelay(1, 50);  // second image
 *   gi.setDelay(2, 50);  // third image
 *   gi.setIterationCount(0);  // infinite loop
 *   gi.write(myOutputStream);
 * </pre>
 *
 * @author Benjamin E. Norman
 */
public class GIFImage 
{
	// file versions
	public static final int GIF87a = 0;
	public static final int GIF89a = 1;

	// block labels
	public static final int IMAGE_SEPARATOR = 0x2c;
	public static final int EXTENSION_INTRODUCER = 0x21;
	public static final int TRAILER = 0x3B;

	// extension labels
	public static final int PLAIN_TEXT = 0x01;
	public static final int APPLICATION = 0xff;
	public static final int COMMENT = 0xfe;
	public static final int GRAPHIC_CONTROL = 0xf9;

	// application extension identifiers and versions
	public static final String NETSCAPE2_0 = "NETSCAPE2.0";

	// disposal methods
	public static final int NONE = 0x00;
	public static final int LEAVE_IN_PLACE = 0x01;
	public static final int RESTORE_TO_BACKGROUND = 0x02;
	public static final int RESTORE_TO_PREVIOUS = 0x03;

	protected Header header;
	protected LogicalScreen logicalScreen;
	protected NetscapeExtension netscapeExtension;
	protected Vector tableBasedImages = new Vector();
	protected GraphicControlExtension currentGraphicControlExtension;

	//
	// component classes
	//

	/**
	 * The 6-byte header of a GIF file.
	 */
	class Header
	{
		/** one of GIF87a or GIF89a */
		public int version;
		
		/**
		 * Create a header with default version 89a.
		 */
		public Header()
		{
			version = GIF89a;
		}

		public Header(GIFInputStream input) throws IOException
		{
			read(input);
		}

		public void read(GIFInputStream input) throws IOException
		{
			char[] string = new char[3];

			// read signature
			for (int i = 0; i < 3; i++)
			{
				string[i] = (char)input.readByte();
			}
			if (! String.valueOf(string).equals("GIF"))
			{
				throw new GIFFormatException("Invalid GIF signature");
			}

			// read version
			for (int i = 0; i < 3; i++)
			{
				string[i] = (char)input.readByte();
			}
			if (String.valueOf(string).equals("89a"))
			{
				version = GIF89a;
			}
			else if (String.valueOf(string).equals("87a"))
			{
				version = GIF87a;
			}
			else
			{
				throw new GIFFormatException("Invalid GIF version");
			}
		}

		public void write(GIFOutputStream output) throws IOException
		{
			String hdr = "GIF";
			if (version == GIF89a)
			{
				hdr += "89a";
			}
			else if (version == GIF87a)
			{
				hdr += "87a";
			}
			else
			{
				throw new GIFFormatException("Invalid GIF version");
			}

			for (int i = 0; i < 6; i++)
			{
				output.write(hdr.charAt(i));
			}
		}

		public String toString()
		{
			String retVal = "Header:\n  version = ";
			if (version == GIF89a)
			{
				retVal += "89a\n";
			}
			else if (version == GIF87a)
			{
				retVal += "87a\n";
			}
			else
			{
				retVal += "???\n";
			}
			return retVal;
		}
	}

	/**
	 * Provides basic utility methods for ImageDescriptor, ScreenDescriptor,
	 * GraphicControl, etc.
	 */
	class Block
	{
		/**
		 * Return a string representation of the block's fields
		 */
		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			int index = getClass().getName().lastIndexOf("$"); // check inner class
			if (index < 0)
			{ // check regular class name
				index = getClass().getName().lastIndexOf(".");
			}
			sb.append(getClass().getName().substring(index + 1) + ":\n");
			try
			{
				Field[] fields = getClass().getFields();
				for (int i = 0; i < fields.length; i++)
				{
					sb.append("  " + fields[i].getName() + " = " + 
						fields[i].get(this) + "\n");
				}
			}
			catch (Exception ex)
			{
				sb.append("****error reading fields***");
			}
			return sb.toString();
		}		
	}

	/**
	 * Generic application extension block.
	 */
	class ApplicationExtension extends Block
	{
		public String identifier;
		public String authenticationCode;
		public byte[] data;

		public ApplicationExtension()
		{
			identifier = "        ";
			authenticationCode = "   ";
			data = new byte[0];
		}
		
		public ApplicationExtension(GIFInputStream input) throws IOException
		{
			read(input);
		}

		public void read(GIFInputStream input) throws IOException
		{
			// read the data sub-blocks
			data = input.readDataBlock();
		}
		
		public void write(GIFOutputStream output) throws IOException
		{
			// write the extension introducer and label
			output.write(EXTENSION_INTRODUCER);
			output.write(APPLICATION);

			// write the header
			byte[] header = (identifier + authenticationCode).getBytes();
			output.writeDataSubBlock(header);

			// write the data
			output.writeDataBlock(data);
		}
	}

	/**
	 * Extension block for looping animations.
	 */
	public class NetscapeExtension extends ApplicationExtension
	{
		public NetscapeExtension(GIFInputStream input) throws IOException
		{
			super(input);
			identifier = "NETSCAPE";
			authenticationCode = "2.0";
		}

		/**
		 * iterationCount should be 0 for infinite loop.
		 */
		public NetscapeExtension(int iterationCount)
		{
			identifier = "NETSCAPE";
			authenticationCode = "2.0";
			data = new byte[3];
			data[0] = 0x1;
			setIterationCount(iterationCount);
		}
		
		/**
		 * iterationCount should be 0 for infinite loop.
		 */
		public void setIterationCount(int iterationCount)
		{
			data[1] = (byte) (iterationCount & 0xff);
			data[2] = (byte) ((iterationCount >>> 8) & 0xff);
		}

		public int getIterationCount()
		{
			return ((data[1] & 0xff) | ((data[2] & 0xff) << 8));
		}
	}

	/**
	 * Encapsulates the logical screen descriptor and global color table,
	 * if any.
	 */
	class LogicalScreen
	{
		public LogicalScreenDescriptor descriptor;
		public ColorTable globalColorTable;

		/**
		 * Construct a logical screen with default descriptor
		 * and global color table.
		 */
		public LogicalScreen(int width, int height)
		{
			descriptor = new LogicalScreenDescriptor(width, height);
			globalColorTable = new ColorTable();
		}
		
		public LogicalScreen(GIFInputStream input) throws IOException
		{
			read(input);
		}

		public void read(GIFInputStream input) throws IOException
		{
			descriptor = new LogicalScreenDescriptor(input);
			if (descriptor.globalColorTablePresent)
			{
				globalColorTable = new ColorTable(input, 
					1 << (descriptor.globalColorTableBppMinus1 + 1) );
			}
		}

		public void write(GIFOutputStream output) throws IOException
		{
			descriptor.write(output);
			if (descriptor.globalColorTablePresent)
			{
				globalColorTable.write(output);
			}
		}

		public String toString()
		{
			return descriptor.toString() + (descriptor.globalColorTablePresent ? 
				globalColorTable.toString() : "");
		}
	}

	/**
	 * Represents the file block which contains a description of the 
	 * GIF logical screen layout.
	 */
	class LogicalScreenDescriptor extends Block
	{
		public int logicalScreenWidth;
		public int logicalScreenHeight;
		public int globalColorTableBppMinus1;
		public boolean globalColorTableSorted;
		public int originalImageBppMinus1;
		public boolean globalColorTablePresent;
		/** index of background color in global color table */
		public int backgroundColorIndex;
		/** measure of pixel width/height */
		public int pixelAspectRatio;

		/**
		 * Creates a LogicalScreenDescriptor by reading it in from the 
		 * given stream.
		 */
		public LogicalScreenDescriptor(GIFInputStream input) throws IOException
		{
			read(input);
		}

		/**
		 * Create a logical screen descriptor having  
		 * color depth == 255 bpp and indicating the presence of an unsorted
		 * global color table with background index 0.
		 */
		public LogicalScreenDescriptor(int width, int height)
		{
			logicalScreenWidth = width;
			logicalScreenHeight = height;
			globalColorTableBppMinus1 = 7;
			globalColorTableSorted = false;
			originalImageBppMinus1 = 7;
			globalColorTablePresent = true;
			backgroundColorIndex = 0;
			pixelAspectRatio = 0;
		}

		/**
		 * Write the GIF-formatted screen descriptor to the given
		 * stream.
		 */
		public void write(GIFOutputStream output) throws IOException
		{
			output.writeWord(logicalScreenWidth);
			output.writeWord(logicalScreenHeight);
			output.writeBits(globalColorTableBppMinus1, 3);
			output.writeBit(globalColorTableSorted ? 1 : 0);
			output.writeBits(originalImageBppMinus1, 3);
			output.writeBit(globalColorTablePresent ? 1 : 0);
			output.write(backgroundColorIndex);
			output.write(pixelAspectRatio);
		}

		/**
		 * Read in the screen descriptor's state from the given stream,
		 * which should contain the screen descriptor in GIF-file format.
		 */
		public void read(GIFInputStream input) throws IOException 
		{
			logicalScreenWidth = input.readWord();
			logicalScreenHeight = input.readWord();
			globalColorTableBppMinus1 = input.readBits(3);
			globalColorTableSorted = (input.readBit() > 0);
			originalImageBppMinus1 = input.readBits(3);
			globalColorTablePresent = (input.readBit() > 0);
			backgroundColorIndex = input.readByte();
			pixelAspectRatio = input.readByte();
		}
	}

	/**
	 * Represents a color table (global or local), which holds the actual
	 * color values for the pixels.  The pixels values in the file are 
	 * indexes into this table.
	 */
	class ColorTable
	{
		Color[] colors;

		/**
		 * Construct a color table from the given stream, which should
		 * contain 3 * numColors bytes of hex RRGGBB color info.
		 */
		public ColorTable(GIFInputStream input, int numColors)
			throws IOException
		{
			colors = new Color[numColors];
			read(input);
		}

		public ColorTable(Color[] colors)
		{
			this.colors = colors;
		}

		/**
		 * Construct an empty color table
		 */
		public ColorTable()
		{
			colors = new Color[0];
		}

		/**
		 * Read in this color table from the given stream, which should contain
		 * getNumColors() * 3 bytes of hex RRGGBB color info.
		 */
		public void read(GIFInputStream input) throws IOException
		{
			for (int i = 0; i < colors.length; i++)
			{
				colors[i] = input.readColor();
			}
		}

		/**
		 * Write the color table to the given stream in the gif format,
		 * triples of bytes, red first.
		 */
		public void write(GIFOutputStream output) throws IOException
		{
			byte[] color = new byte[3]; 
			for (int i = 0; i < colors.length; i++)
			{
				output.writeColor(colors[i]);
			}
		}

		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append("ColorTable:\n");
			for (int i = 0; i < colors.length; i++)
			{
				sb.append("  ");
				String hexString = Integer.toHexString(colors[i].getRGB() & 0xffffff);
				for (int j = hexString.length(); j < 6; j++)
				{
					sb.append("0");
				}
				sb.append(hexString);
				if ( ( i != 0 && (i % 8) == 7) || i == (colors.length - 1) )
				{
					sb.append("\n");
				}
			}
			return sb.toString();
		}

	}

	/**
	 * Encapsulates an image descriptor, optional local color table, and
	 * the table-based image data.  An optional graphicControlExtension is
	 * also provided.
	 */
	class TableBasedImage
	{
		public GraphicControlExtension graphicControlExtension;
		public ImageDescriptor descriptor;
		public ColorTable localColorTable;
		public ImageData data;

		/**
		 * Construct an image with default descriptor and data.  No
		 * local color table or graphic control extension are used.
		 */
		public TableBasedImage(int width, int height)
		{
			descriptor = new ImageDescriptor(width, height);
			data = new ImageData(width * height);
		}

		public TableBasedImage(byte[] pixels, int width)
		{
			descriptor = new ImageDescriptor(width, (int)(pixels.length/width));
			data = new ImageData(pixels);
		}

		public TableBasedImage(GIFInputStream input) throws IOException
		{
			read(input);
		}
		
		public void write(GIFOutputStream output) throws IOException
		{
			// write any control extension
			if (graphicControlExtension != null)
			{
				output.write(EXTENSION_INTRODUCER);
				output.write(GRAPHIC_CONTROL);
				graphicControlExtension.write(output);
			}

			// write screen info
			output.write(IMAGE_SEPARATOR);
			descriptor.write(output);
			if (descriptor.localColorTablePresent)
			{
				localColorTable.write(output);
			}

			// write compressed image data
			ImageData outputData = data;
			if (descriptor.imageInterlaced)
			{
				outputData = interlace(data);
			}
			outputData.write(output, getBppForImage(TableBasedImage.this));
		}

		public void read(GIFInputStream input) throws IOException
		{
			// read header info
			descriptor = new ImageDescriptor(input);
			if (descriptor.localColorTablePresent)
			{
				localColorTable = new ColorTable(input, 
					1 << (descriptor.localColorTableBppMinus1 + 1) );
			}

			// read compressed image data
			data = new ImageData(input, descriptor.imageWidth *
				descriptor.imageHeight);
			if (descriptor.imageInterlaced)
			{
			 	data = deInterlace(data);
			}
		}

		public String toString()
		{
			return ((graphicControlExtension == null ? "" : 
				graphicControlExtension.toString()) +
				descriptor.toString() + (descriptor.localColorTablePresent ? 
					localColorTable.toString() : "") +
							data.toString());			
		}

		public int[] getRGBPixels()
		{
			byte[] indexedPixels = data.pixels;
			int[] rgbPixels = new int[indexedPixels.length];
			Color[] colors = getColorTableForImage(this).colors;

			for (int i = 0; i < rgbPixels.length; i++)
			{
				rgbPixels[i] = colors[ indexedPixels[i] & 0xff ].getRGB() & 0xffffff;
			}

			return rgbPixels;
		}
		
		public Color getTransparentColor()
		{
			if (graphicControlExtension != null &&
					graphicControlExtension.transparentColorFlag)
			{
				return getColorTableForImage(this).colors[
					graphicControlExtension.transparentColorIndex];
			}
			else 
			{
		 		return null;
			}
		}

		public void setTransparentColor(Color trans)
		{
			if (graphicControlExtension == null)
			{
				graphicControlExtension = new GraphicControlExtension();
			}

			graphicControlExtension.transparentColorFlag = true;
			graphicControlExtension.transparentColorIndex = getIndexForColor(trans);
		}

		/**
		 * Return an interlaced version of the given (non-interlaced)
		 * image data object.
		 */
		protected ImageData interlace(ImageData nonInterlaced)
		{
			ImageData interlaced = new ImageData(nonInterlaced.pixels.length);
			int iRow = -1, niRow;
			int width = descriptor.imageWidth;
			int height = descriptor.imageHeight;
			
			// write out first pass (every 8th row, starting w/0)
			for (niRow = 0; niRow < height; niRow += 8)
			{
				iRow++;
				System.arraycopy(nonInterlaced.pixels, niRow * width,
					interlaced.pixels, iRow * width, width);
			}

			// second pass (every 8th row, starting w/4)
			for (niRow = 4; niRow < height; niRow += 8)
			{
				iRow++;
				System.arraycopy(nonInterlaced.pixels, niRow * width,
					interlaced.pixels, iRow * width, width);
			}
			
			// third pass (every 4th row, starting w/2)
			for (niRow = 2; niRow < height; niRow += 4)
			{
				iRow++;
				System.arraycopy(nonInterlaced.pixels, niRow * width,
					interlaced.pixels, iRow * width, width);
			}
			
			// fourth pass (every 2nd row, starting w/1)
			for (niRow = 1; niRow < height; niRow += 2)
			{
				iRow++;
				System.arraycopy(nonInterlaced.pixels, niRow * width,
					interlaced.pixels, iRow * width, width);
			}

			return interlaced;
		}

		/**
		 * Return a de-interlaced version of the given (interlaced)
		 * image data object.
		 */
		protected ImageData deInterlace(ImageData interlaced)
		{
			ImageData nonInterlaced = new ImageData(interlaced.pixels.length);
			int iRow = -1, niRow;
			int width = descriptor.imageWidth;
			int height = descriptor.imageHeight;
			
			// read in first pass (every 8th row, starting w/0)
			for (niRow = 0; niRow < height; niRow += 8)
			{
				iRow++;
				System.arraycopy(interlaced.pixels, iRow * width,
					nonInterlaced.pixels, niRow * width, width);
			}

			// second pass (every 8th row, starting w/4)
			for (niRow = 4; niRow < height; niRow += 8)
			{
				iRow++;
				System.arraycopy(interlaced.pixels, iRow * width,
					nonInterlaced.pixels, niRow * width, width);
			}
			
			// third pass (every 4th row, starting w/2)
			for (niRow = 2; niRow < height; niRow += 4)
			{
				iRow++;
				System.arraycopy(interlaced.pixels, iRow * width,
					nonInterlaced.pixels, niRow * width, width);
			}
			
			// fourth pass (every 2nd row, starting w/1)
			for (niRow = 1; niRow < height; niRow += 2)
			{
				iRow++;
				System.arraycopy(interlaced.pixels, iRow * width,
					nonInterlaced.pixels, niRow * width, width);
			}

			return nonInterlaced;
		}
		
		/**
		 * Returns color index or -1 if color not in table.
		 */
		public int getIndexForColor(Color color)
		{
			ColorTable ct = getColorTableForImage(this);
			for (int i = 0; i < ct.colors.length; i++)
			{
				if (ct.colors[i].equals(color))
				{
					return i;
				}
			}
			return -1;
		}
	}

	/**
	 * Represents the raster data in a table-based image.
	 */
	class ImageData 
	{
		public byte[] pixels;

		/**
		 * Create a data object with the given indexed pixels.
		 */
		public ImageData(byte[] pixels)
		{
			this.pixels = pixels;
		}

		/**
		 * Create data for a blank image with the given number of pixels.
		 */
		public ImageData(int numPixels)
		{
			pixels = new byte[numPixels];
			for (int i = 0; i < numPixels; i++)
			{
				pixels[i] = 0;
			}
		}

		public ImageData(GIFInputStream input, int numPixels) throws IOException
		{
			read(input, numPixels);
		}

		public void read(GIFInputStream input, int numPixels) throws IOException
		{
			// decompress and read image data
			pixels = input.readImageData(numPixels);
		}

		public void write(GIFOutputStream output, int imageBpp) throws IOException
		{
			output.writeImageData(pixels, imageBpp);
		}

		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append("ImageData:\n");
			for (int i = 0; i < pixels.length; i++)
			{
				if (i % 24 == 0)
				{
					sb.append("  ");
				}
				else
				{
					sb.append(" ");
				}
				String hexString = Integer.toHexString(pixels[i] & 0xff);
				if (hexString.length() < 2)
				{
					sb.append("0");
				}
				sb.append(hexString);
				if ( ( i != 0 && (i % 24) == 23) || i == (pixels.length - 1) )
				{
					sb.append("\n");
				}
			}
			return sb.toString();
		}
	}
	/**
	 * Represents a graphic control extension which contains transparency
	 * information, etc.
	 */
	class GraphicControlExtension extends Block
	{
		public int reserved;
		public int disposalMethod;
		public boolean userInputFlag;
		public boolean transparentColorFlag;
		public int delayTime;
		public int transparentColorIndex;

		/**
		 * create a graphic control extension with all zero fields.
		 */
		public GraphicControlExtension()
		{
			reserved = 0;
			disposalMethod = NONE;
			userInputFlag = false;
			transparentColorFlag = false;
			delayTime = 0;
			transparentColorIndex = 0;
		}

		public GraphicControlExtension(GIFInputStream input) throws IOException
		{
			read(input);
		}

		public void write(GIFOutputStream output) throws IOException
		{
			// write block length
			output.write(4);
			
			// write fields
			output.writeBit(transparentColorFlag ? 1 : 0);
			output.writeBit(userInputFlag ? 1 : 0);
			output.writeBits(disposalMethod, 3);
			output.writeBits(reserved, 3);
			output.writeWord(delayTime);
			output.write(transparentColorIndex);

			// write block terminator
			output.write(0);
		}

		public void read(GIFInputStream input) throws IOException 
		{
			// read the block length (always 4)
			input.readByte();

			// read the fields
			transparentColorFlag = (input.readBit() > 0);
			userInputFlag = (input.readBit() > 0);
			disposalMethod = input.readBits(3);
			reserved = input.readBits(3);
			delayTime = input.readWord();
			transparentColorIndex = input.readByte();

			// read the block terminator
			input.readByte();
		}
	}
		
	/**
	 * Represents the image descriptor which preceeds each image in the
	 * GIF file.
	 */	
	class ImageDescriptor extends Block
	{
		/** the left position of the image within the logical screen */
		public int imageLeftPosition;
		/** the top position of the image within the logical screen */
		public int imageTopPosition;
		public int imageHeight;
		public int imageWidth;
		public boolean localColorTablePresent;
		public boolean imageInterlaced;
		public boolean localColorTableSorted;
		public int reserved;
		public int localColorTableBppMinus1;

		/**
		 * Create an image descriptor at (0,0) with no local color table
		 * and no interlacing.
		 */
		public ImageDescriptor(int width, int height)
		{
			imageLeftPosition = 0;
			imageTopPosition = 0;
			imageWidth = width;
			imageHeight = height;
			localColorTablePresent = false;
			imageInterlaced = false;
			localColorTableSorted = false;
			reserved = 0;
			localColorTableBppMinus1 = 0;
		}

		/**
		 * Creates an ImageDescriptor by reading it in from the 
		 * given stream.
		 */
		public ImageDescriptor(GIFInputStream input) throws IOException
		{
			read(input);
		}

		/**
		 * Write the GIF-formatted image descriptor to the given
		 * stream.
		 */
		public void write(GIFOutputStream output) throws IOException
		{
			output.writeWord(imageLeftPosition);
			output.writeWord(imageTopPosition);
			output.writeWord(imageWidth);
			output.writeWord(imageHeight);
			output.writeBits(localColorTableBppMinus1, 3);
			output.writeBits(reserved, 2);
			output.writeBit(localColorTableSorted ? 1 : 0);
			output.writeBit(imageInterlaced ? 1 : 0);
			output.writeBit(localColorTablePresent ? 1 : 0);
		}

		/**
		 * Read in the screen descriptor's state from the given stream,
		 * which should contain the screen descriptor in GIF-file format.
		 */
		public void read(GIFInputStream input) throws IOException 
		{
			imageLeftPosition = input.readWord();
			imageTopPosition = input.readWord();
			imageWidth = input.readWord();
			imageHeight = input.readWord();
			localColorTableBppMinus1 = input.readBits(3);
			reserved = input.readBits(2);
			localColorTableSorted = (input.readBit() > 0);
			imageInterlaced = (input.readBit() > 0);
			localColorTablePresent = (input.readBit() > 0);
		}
	}	
	/**
	 * As GIFImage(int[], int, boolean), using a global color table.
	 */
	public GIFImage(int[] pixels, int width) throws GIFFormatException
	{
		this(pixels, width, false);
	}
	/**
	 * Create a GIFImage containing one table-based image formed from the 
	 * given array of integer RGB raster data
	 * and the image width.  (The width should divide the length of the
	 * data array.)  The first 256 colors encountered are added to the
	 * color table.  The flag indicates whether this image should use a local
	 * color table (unique to the primary image) or the global color table, 
   * which is shared by all table-based images in this GIFImage.
	 *
	 * @exception GIFFormatException if more than 256 colors are encountered
	 */
	public GIFImage(int[] pixels, int width, boolean localColorTable) 
		throws GIFFormatException
	{
		int height = (int) (pixels.length / width);
		header = new Header();
		logicalScreen = new LogicalScreen(width, height);

		addImage(pixels, width, localColorTable);
	}
	/**
	 * Create a GIFImage from .gif image data on the given stream.
	 */
	public GIFImage(GIFInputStream input) throws IOException
	{	
		read(input);
	}
	/**
	 * Adds a new table-based image to this GIFImage (at the end of the list).
	 * The RGB pixels and image width are required.  The global color
	 * table is used.
	 *
	 * @exception GIFFormatException if more than 256 colors are encountered
	 */
	public void addImage(int[] pixels, int width) throws GIFFormatException
	{
		addImage(pixels, width, false);
	}
	/**
	 * Adds a new table-based image to this GIFImage (at the end of the list).
	 * The RGB pixels and image width are required.  The flag indicates whether
	 * a local color table is used for this image.  Otherwise, the image colors
	 * are added to the global color table, up to its capacity of 256.
	 *
	 * @exception GIFFormatException if more than 256 colors are encountered
	 */
	public void addImage(int[] pixels, int width, boolean localColorTable) 
		throws GIFFormatException
	{
		int height = (int) (pixels.length / width);

		// convert RGB pixels to indexed color
		byte[] indexedPixels = new byte[pixels.length];
		ColorTable ct = makeIndexedColor(pixels, indexedPixels, localColorTable);

		// set up image
		TableBasedImage tbi = new TableBasedImage(indexedPixels, width);
		tableBasedImages.addElement(tbi);

		// set up color table
		int bpp = bppForNumColors(ct.colors.length);
		if (localColorTable)
		{
			tbi.localColorTable = ct;
			tbi.descriptor.localColorTablePresent = true;
			tbi.descriptor.localColorTableBppMinus1 = bpp - 1;
		}
		else
		{
			logicalScreen.globalColorTable = ct;
			logicalScreen.descriptor.globalColorTableBppMinus1 = bpp - 1;
			logicalScreen.descriptor.globalColorTablePresent = true;
			logicalScreen.descriptor.originalImageBppMinus1 = bpp - 1;
		}
	}
	/**
	 * Calculates the minimum BPP for the given number of colors.
	 */
	protected int bppForNumColors(int numColors)
	{
		int bpp = 1;
		for (; bpp < 8; bpp++)
		{
			if (numColors <= (1 << bpp))
			{
				break;
			}
		}
		
		return bpp;
	}
	/**
	 * Return the number of table-based images present.
	 */
	public int countImages()
	{
		return tableBasedImages.size();
	}
	/**
	 * Return the color depth (bits/pixel) of the given table-based image.
	 * This is necessary for images not having a local color table.
	 */
	protected int getBppForImage(TableBasedImage tbi)
	{
		if (tbi.descriptor.localColorTablePresent)
		{
			return tbi.descriptor.localColorTableBppMinus1 + 1;
		}
		else // return global color table bpp
		{
			return logicalScreen.descriptor.globalColorTableBppMinus1 + 1;
		}
	}
	/**
	 * Return the color table for the given table-based image.
	 * This is necessary for images not having a local color table.
	 */
	protected ColorTable getColorTableForImage(TableBasedImage tbi)
	{
		if (tbi.descriptor.localColorTablePresent)
		{
			return tbi.localColorTable;
		}
		else // return global color table
		{
			return logicalScreen.globalColorTable;
		}
	}
	/**
	 * Get the time delay for the primary image in 1/100 s
	 */
	public int getDelay()
	{
		return getDelay(0);
	}
	/**
	 * Get the time delay for the image at the given index in 1/100 s
	 */
	public int getDelay(int imageIndex)
	{
		TableBasedImage tbi = (
			TableBasedImage) tableBasedImages.elementAt(imageIndex);

		if (tbi.graphicControlExtension == null)
		{
			return 0;
		}
		else 
		{
			return tbi.graphicControlExtension.delayTime;
		}
	}
	/**
	 * Get the pixel height of the primary image.
	 */
	public int getHeight()
	{
		return getHeight(0);
	}
	/**
	 * Get the pixel height of the image at the given index.
	 */
	public int getHeight(int imageIndex)
	{
		TableBasedImage tbi = (
			TableBasedImage) tableBasedImages.elementAt(imageIndex);
		return tbi.descriptor.imageHeight;
	}
	/**
	 * Return the number of times the images in this GIFImage will iterate, or 
	 * 0 if they repeat indefinitely.
	 */
	public int getIterationCount()
	{
		if (netscapeExtension == null)
		{
			return 0;
		}
		else
		{
			return netscapeExtension.getIterationCount();
		}
	}
	/**
	 * Return the RGB pixels of the primary image.
	 */
	public int[] getRGBPixels()
	{
		return getRGBPixels(0);
	}
	/**
	 * Return the RGB pixels of the image at the given index.
	 */
	public int[] getRGBPixels(int imageIndex)
	{
		TableBasedImage tbi = (
			TableBasedImage) tableBasedImages.elementAt(imageIndex);
		return tbi.getRGBPixels();
	}
	/**
	 * Get the RGB transparent color of the primary image.
	 */
	public Color getTransparentColor()
	{
		return getTransparentColor(0);
	}
	/**
	 * Get the RGB transparent color of the image at the given index.
	 */
	public Color getTransparentColor(int imageIndex)
	{
		TableBasedImage tbi = (
			TableBasedImage)tableBasedImages.elementAt(imageIndex);
		return tbi.getTransparentColor();
	}
	/**
	 * Get the pixel width of the primary image.
	 */
	public int getWidth()
	{
		return getWidth(0);
	}
	/**
	 * Get the pixel width of the image at the given index.
	 */
	public int getWidth(int imageIndex)
	{
		TableBasedImage tbi = (
			TableBasedImage) tableBasedImages.elementAt(imageIndex);
		return tbi.descriptor.imageWidth;
	}
	/**
	 * Return whether or not the primary image is interlaced.
	 */
	public boolean isImageInterlaced()
	{
		return isImageInterlaced(1);
	}
	/**
	 * Return whether or not the image at the given index is interlaced.
	 */
	public boolean isImageInterlaced(int imageIndex)
	{
		TableBasedImage tbi = (
			TableBasedImage) tableBasedImages.elementAt(imageIndex);
		return tbi.descriptor.imageInterlaced;
	}
	/**
	 * Extract indexed color info from the given RGB pixels.  The size of the 
	 * resulting color table will
	 * be a power of 2, up to a maximum of 256.  The color indices
	 * corresponding to the given RGB color values are placed in the
	 * byte array given.  The final color table is returned.  The flag 
	 * indicates whether the colors in this image are unique to it (local)
	 * or should be added to the color table for the entire image (global).
	 * Note that this method can "grow" the global color table if it is 
	 * less than 8 bits/pixel.
	 *
	 * @exception GIFFormatException if more than 256 colors are found
	 */
	protected ColorTable makeIndexedColor(int[] rgbPixels, byte[] indexedPixels,
		boolean localColorTable) throws GIFFormatException
	{
		System.err.println("Enter makeIndexedColor at " + new Date());

		Vector colorsByIndex = new Vector(256); // for indexing colors
		Hashtable indexesByColor = new Hashtable(256); // for looking up index
		Color color;
		Integer index;
		int numColors = 0;

		// get the global color table if necessary
		if (!localColorTable)
		{
			numColors = logicalScreen.globalColorTable.colors.length;
			for (int i = 0; i < numColors; i++)
			{
				color = logicalScreen.globalColorTable.colors[i];
				index = new Integer(i);
				indexesByColor.put(color, index);
				colorsByIndex.addElement(color);
			}
		}

		// build color table and generate indexed pixels
		for (int i = 0; i < rgbPixels.length; i++)
		{
			color = new Color(rgbPixels[i]);
			index = (Integer) indexesByColor.get(color);

			// if the color has not been seen before, add it to the table
			if (index == null)
			{
				if (numColors > 255)
				{
					throw new GIFFormatException("Image has more than 256 colors");
				}
				index = new Integer(numColors++);
				indexesByColor.put(color, index);
				colorsByIndex.addElement(color);
			}
		
			// record the pixel's color index
			indexedPixels[i] = index.byteValue();
		}

		// create the color table object
		Color[] colors = new Color[1 << bppForNumColors(numColors)];
		for (int i = 0; i < colorsByIndex.size(); i++)
		{
			colors[i] = (Color) colorsByIndex.elementAt(i);
		}
		for (int i = colorsByIndex.size(); i < colors.length; i++)
		{
			colors[i] = Color.black;
		}

		System.err.println("Leave makeIndexedColor at " + new Date());

		return new ColorTable(colors);
	}
	/**
	 * Replace the current contents of this GIFImage with data from a 
	 * .gif file present on the given input stream.
	 */
	public void read(GIFInputStream input) throws IOException
	{
		System.err.println("GIFImage.read() entering");
		// read header
		header = new Header(input);
		
		// read logical screen
		logicalScreen = new LogicalScreen(input);

		// read data blocks until trailer is found
		int blockType, extensionType;
		while ((blockType = input.read()) != TRAILER)
		{
			switch(blockType)
			{
				case EXTENSION_INTRODUCER:
					extensionType = input.read();
					switch(extensionType)
					{
						case PLAIN_TEXT:
							System.err.println("Got plain text extension.");
							input.readDataBlock(); // skip block
							break;
						case GRAPHIC_CONTROL:
							System.err.println("Got graphic control extension.");
							// save this graphic control block for use with the
							// next table-based image
							currentGraphicControlExtension = 
							  new GraphicControlExtension(input);
							break;
						case COMMENT:
							System.err.println("Got comment extension.");
							input.readDataBlock(); // skip block
							break;
						case APPLICATION:
							System.err.println("Got application extension.");
							// read the application id & version
							String id = new String(input.readDataSubBlock() );
							// handle Netscape loop extension
							if ( id.equals("NETSCAPE2.0") )
							{
								System.err.println("  [ ^ was Netscape loop extension ]");
								netscapeExtension = new NetscapeExtension(input);
							}
							// skip any other blocks
							else
							{
								input.readDataBlock();
							}
							break;
						default:
							throw new GIFFormatException("Unknown extension type: 0x" +
								Integer.toHexString(extensionType));
					}
					break;

				case IMAGE_SEPARATOR:
					System.err.println("Got table based image.");
					// create the image and hand it the current graphic control
					// block, if any.
					TableBasedImage tbi = new TableBasedImage(input);
					tbi.graphicControlExtension = currentGraphicControlExtension;
					currentGraphicControlExtension = null;
					tableBasedImages.addElement(tbi);
					break;
				default:
					throw new GIFFormatException("Unknown block type: 0x" +
						Integer.toHexString(blockType));	
			}
		}
		System.err.println("GIFImage.read() leaving");
	}
	/**
	 * Set the time delay for the primary image in 1/100 s
	 */
	public void setDelay(int delay)
	{
		setDelay(0, delay);
	}
	/**
	 * Set the time delay for the image at the given index in 1/100 s
	 */
	public void setDelay(int imageIndex, int delay)
	{
		TableBasedImage tbi = (
			TableBasedImage) tableBasedImages.elementAt(imageIndex);

		if (tbi.graphicControlExtension == null)
		{
			tbi.graphicControlExtension = new GraphicControlExtension();
		}
		tbi.graphicControlExtension.delayTime = delay;
		tbi.graphicControlExtension.disposalMethod = LEAVE_IN_PLACE;
	}
	/**
	 * Set or clear the interlaced state for the image at the given index.
	 */
	public void setImageInterlaced(int imageIndex, boolean interlaced)
	{
		TableBasedImage tbi = (
			TableBasedImage) tableBasedImages.elementAt(imageIndex);
		tbi.descriptor.imageInterlaced = interlaced;
	}
	/**
	 * Set the primary image as interlaced or not.
	 */
	public void setImageInterlaced(boolean interlaced)
	{
		setImageInterlaced(0, interlaced);
	}
	/**
	 * Make the series of images in this file iterate the given number 
	 * of times, or indefinitely if 0 is given.
	 */
	public void setIterationCount(int iterationCount)
	{
		if (netscapeExtension == null)
		{
			netscapeExtension = new NetscapeExtension(iterationCount);
		}
		else
		{
			netscapeExtension.setIterationCount(iterationCount);
		}
	}
	/**
	 * Get the RGB transparent color of the image at the given index.
	 */
	public void setTransparentColor(int imageIndex, Color trans)
	{
		TableBasedImage tbi = (
			TableBasedImage)tableBasedImages.elementAt(imageIndex);
		tbi.setTransparentColor(trans);
	}
	/**
	 * Set the RGB transparent color of the primary image.
	 */
	public void setTransparentColor(Color trans)
	{
		setTransparentColor(0, trans);
	}
	/**
	 * Print the string representation of the gif file - includes all
	 * image data including actual pixels.  (Careful!)
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(header.toString());
		sb.append(logicalScreen.toString());
		for (int i = 0; i < tableBasedImages.size(); i++)
		{
			sb.append(tableBasedImages.elementAt(i).toString());
		}
		return sb.toString();
	}
	/**
	 * Write this GIFImage as a .gif file to the given stream.
	 */
	public void write(GIFOutputStream output) throws IOException
	{
		System.err.println("GIFImage.write() entering");
		header.write(output);
		logicalScreen.write(output);

		// write any netscape extension
		if (netscapeExtension != null)
		{
			netscapeExtension.write(output);
		}

		// write any images
		for (int i = 0; i < tableBasedImages.size(); i++)
		{
			((TableBasedImage)tableBasedImages.elementAt(i)).write(output);
		}
		
		output.write(TRAILER);
		System.err.println("GIFImage.write() leaving");
	}
}
