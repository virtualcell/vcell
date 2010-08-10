package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.util.zip.*;
/**
 * This type was created in VisualAge.
 */
public class VCImageCompressed extends VCImage {
	private byte compressedPixels[] = null;
	private transient byte uncompressed[] = null;
/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 */
public VCImageCompressed(VCImage vcimage) throws ImageException {
	super(vcimage);
	this.compressedPixels = vcimage.getPixelsCompressed();
}
/**
 * This method was created in VisualAge.
 * @param pix byte[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public VCImageCompressed(org.vcell.util.document.Version aVersion, byte pixels[], org.vcell.util.Extent extent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aVersion,extent,aNumX,aNumY,aNumZ);
	this.compressedPixels = pixels;
	initPixelClasses();
}
public void nullifyUncompressedPixels(){
	uncompressed = null;
}

/**
 * getPixels method comment.
 */
public byte[] getPixels() throws ImageException {
	try {
		if (uncompressed == null){
	System.out.println("VCImageCompressed.getPixels()  <<<<<<UNCOMPRESSING>>>>>>");
			ByteArrayInputStream bis = new ByteArrayInputStream(compressedPixels);
			InflaterInputStream iis = new InflaterInputStream(bis);
			int temp;
			byte buf[] = new byte[65536];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while((temp = iis.read(buf,0,buf.length)) != -1){
				bos.write(buf,0,temp);
			}
			//byte uncompressed[] = new byte[getSizeX()*getSizeY()*getSizeZ()];
			//int result = iis.read(uncompressed,0,getSizeX()*getSizeY()*getSizeZ());
			//return uncompressed;
			uncompressed = bos.toByteArray();
		}
		return uncompressed;
	} catch (IOException e){
		e.printStackTrace(System.out);
		throw new ImageException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getPixelsCompressed() {
	return compressedPixels;
}
}
