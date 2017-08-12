package GIFUtils;

/*
 * LZWException.java
 *
 * Copyright 1998 by Benjamin E. Norman
 *
 * 24/08/98 initial version
 *
 */

/**
 * Indicates that an abnormal condition has occured during an
 * operation involving an LZWStringTable
 *
 * @author Benjamin E. Norman
 */
public class LZWException extends Exception 
{
	
	public LZWException()
	{
		super();
	}
	public LZWException(String msg)
	{
		super(msg);
	}
} // LZWException
