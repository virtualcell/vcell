package cbit.image;

/**
 * Insert the type's description here.
 * Creation date: (1/11/2003 11:26:05 PM)
 * @author: Jim Schaff
 */
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteArrayTiffInputSource implements TiffInputSource {
	private byte buffer[] = null;
	private java.io.ByteArrayInputStream byteArrayInputStream = null;
	private java.io.DataInputStream dataInputStream = null;
	public ByteArrayTiffInputSource(byte data[]) {
		buffer = data;
		byteArrayInputStream = new ByteArrayInputStream(data);
		dataInputStream = new DataInputStream(byteArrayInputStream);
	}
	public void close() throws java.io.IOException {
		dataInputStream.close();
	}
	public long getFilePointer() throws java.io.IOException {
		return buffer.length - byteArrayInputStream.available();
	}
	public int read(byte buffer[]) throws java.io.IOException {
		return dataInputStream.read(buffer);
	}
	public int read(byte[] b, int off, int len) throws java.io.IOException {
		return dataInputStream.read(b,off,len);
	}
	public void seek(long position) throws java.io.IOException {
		byteArrayInputStream.reset();
		byteArrayInputStream.skip(position);
	}
}
