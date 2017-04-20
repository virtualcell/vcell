/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Insert the type's description here.
 * Creation date: (5/10/2004 3:40:51 PM)
 * @author: Fei Gao
 *
 * Goal:
 * BigString is necessary to wrap strings bigger than 64K. We had exceptions when large Strings were serialized.
 * Now we compress/decompress strings when we serialize/deserialize BigString since model XML can be real big.
 * The ratio can be up to 20:1 and it saves time to transfer smaller strings over network.
 *
 * Implementation:
 * In order to do that, we override readObject() for deserialzation and writeObject() for serialization.
 * One trick is we only decompress strings when toString() is called. In this way, in the trip from client to RMI to JMS to server,
 * there is only one compression and one decompression.
 */
@SuppressWarnings("serial")
public class BigString implements Serializable {
	private byte[] compressedStrBytes = null;
	private transient String str = null;

/**
 * BigString constructor comment.
 */
public BigString(String arg_str) {
	super();
	str = arg_str;
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:46:15 AM)
 */
private void deflate() throws java.io.IOException {
	if (compressedStrBytes == null) {
		byte[] strBytes = str.getBytes();
		compressedStrBytes = BeanUtils.compress(strBytes);
//		System.out.println("Deflating big string: " + strBytes.length + "/" + compressedStrBytes.length);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:44:40 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	return toString().equals(obj);
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:44:13 PM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:46:15 AM)
 */
private void inflate() throws java.io.IOException {
	str = new String(BeanUtils.uncompress(compressedStrBytes));
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
	int compressedSize = s.readInt();
	compressedStrBytes = new byte[compressedSize];
	s.readFully(compressedStrBytes, 0, compressedSize);
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:42:08 PM)
 * @return java.lang.String
 */
public String toString() {
	try {
		if (str == null) {
			inflate();
		}
		return str;
	} catch (IOException ex) {
		throw new RuntimeException("BigString serialization: uncompressing error");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void writeObject(ObjectOutputStream s) throws IOException {
	deflate();
	s.writeInt(compressedStrBytes.length);
	s.write(compressedStrBytes);
}
}
