package org.vcell.util;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class Hex {
	private static final char hexdigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

/**
 * Insert the method's description here.
 * Creation date: (5/3/2001 2:55:00 PM)
 * @return byte[]
 * @param hexString java.lang.String
 */
public static byte[] toBytes(String hexString) throws NumberFormatException {
	if (hexString.length() % 2 > 0) {
		throw new NumberFormatException("String must have even number of characters");
	}
	byte[] bytes = new byte[hexString.length() / 2];
	for (int i = 0; i < bytes.length; i++){
		String s = hexString.substring(2 * i, 2 * i + 2);
		bytes[i] = (byte)(Integer.parseInt(s, 16));
	}
	return bytes;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String toString(byte data[]) {
	
	char chars[] = new char[data.length*2];
	int charIndex = 0;
	for (int i=0;i<data.length;i++){
		chars[charIndex] = hexdigits[(((int)data[i])>>4)&0x0000000F];
		charIndex++;
		chars[charIndex] = hexdigits[((int)data[i])&0x0000000F];
		charIndex++;
	}
	return new String(chars);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String toString(int intData) {
	
	byte data[] = new byte[4];
	data[0] = (byte) ((intData >> 24) & 0x000000ff);
	data[1] = (byte) ((intData >> 16) & 0x000000ff);
	data[2] = (byte) ((intData >> 8) & 0x000000ff);
	data[3] = (byte) ((intData >> 0) & 0x000000ff);
	return "0x"+toString(data);
}
}