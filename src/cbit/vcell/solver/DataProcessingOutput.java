package cbit.vcell.solver;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import cbit.util.BeanUtils;


/**
 * Insert the type's description here.
 * Creation date: (5/10/2004 3:40:51 PM)
 * @author: Jim Schaff
 *
 * copied from BigString for serialization of uncompressing/compressing bytes
 * 
 */
public class DataProcessingOutput implements Serializable {
	private byte[] compressedBytes = null;
	private transient byte[] uncompressedBytes = null;

/**
 * BigString constructor comment.
 */
public DataProcessingOutput(byte bytes[]) {
	super();
	uncompressedBytes = bytes;
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:46:15 AM)
 */
private void deflate() throws java.io.IOException {
	if (compressedBytes == null) {
		compressedBytes = BeanUtils.compress(uncompressedBytes);
		System.out.println("Deflating data: " + uncompressedBytes.length + "/" + compressedBytes.length);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:46:15 AM)
 */
private void inflate() throws java.io.IOException {
	uncompressedBytes = BeanUtils.uncompress(compressedBytes);
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
	int compressedSize = s.readInt();
	compressedBytes = new byte[compressedSize];
	s.readFully(compressedBytes, 0, compressedSize);
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 3:42:08 PM)
 * @return java.lang.String
 */
public byte[] toBytes() {
	try {
		if (uncompressedBytes == null) {
			inflate();
		}
		return uncompressedBytes;
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
	s.writeInt(compressedBytes.length);
	s.write(compressedBytes);
}
}