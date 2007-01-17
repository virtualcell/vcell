package cbit.image;

/**
 * Insert the type's description here.
 * Creation date: (1/11/2003 11:07:05 PM)
 * @author: Jim Schaff
 */
public interface TiffInputSource {
	
public void close() throws java.io.IOException;
public int read(byte buffer[]) throws java.io.IOException;
/**
 * Insert the method's description here.
 * Creation date: (1/11/2003 11:09:13 PM)
 * @return int
 * @param b byte[]
 * @param off int
 * @param len int
 * @exception java.io.IOException The exception description.
 */
int read(byte[] b, int off, int len) throws java.io.IOException;
public void seek(long position) throws java.io.IOException;

/**
 * Insert the method's description here.
 * Creation date: (1/11/2003 11:20:57 PM)
 * @return long
 */
long getFilePointer() throws java.io.IOException;
}
