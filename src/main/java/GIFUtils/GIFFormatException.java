package GIFUtils;

/*
 * GIFFormatException.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 23/08/98 initial version
 *
 */

import java.io.IOException;

/**
 * Indicates that a GIF file is not in the correct format.
 *
 * @author Benjamin E. Norman
 */
public class GIFFormatException extends IOException 
{
	
	public GIFFormatException()
	{
		super();
	}
	public GIFFormatException(String msg)
	{
		super(msg);
	}
} // GIFException
