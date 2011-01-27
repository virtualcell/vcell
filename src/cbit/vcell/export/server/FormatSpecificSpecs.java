package cbit.vcell.export.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.zip.DataFormatException;

import cbit.vcell.export.gloworm.quicktime.VideoMediaSample;
import cbit.vcell.export.gloworm.quicktime.VideoMediaSampleJPEG;
import cbit.vcell.export.gloworm.quicktime.VideoMediaSampleRaw;
/**
 * Dummy parent class.
 */
public abstract class FormatSpecificSpecs implements Serializable {
	public final static int CODEC_NONE = 0;
	public final static int CODEC_JPEG = 1;

	public static VideoMediaSample getVideoMediaSample(
			int width,int height,int sampleDuration,int bitsPerPixel,boolean isGrayscale,int compressionType,float compressionQuality,byte[] bytes) throws Exception{
			
			if(compressionType == FormatSpecificSpecs.CODEC_JPEG){
				BufferedImage bufferedImage = null;
				if(bitsPerPixel == 8){
					bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
					byte[] buffer = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
					System.arraycopy(bytes, 0, buffer, 0, bytes.length);
				}else if(bitsPerPixel == 32){
					bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							bufferedImage.setRGB(x, y, dataInputStream.readInt());
						}
					}
				}else{
					throw new DataFormatException("JPEG only implement 8 and 32 bits.");
				}
				return FormatSpecificSpecs.encodeJPEG(bufferedImage, compressionQuality, width, height, sampleDuration, bitsPerPixel, isGrayscale);
			}else{
				return new VideoMediaSampleRaw(width, height, sampleDuration,bytes,bitsPerPixel, isGrayscale);
			}
		}

	public static VideoMediaSampleJPEG encodeJPEG(BufferedImage bufferedImage,float compressionQuality,int width,int height,int sampleDuration,int bitsPerPixel,boolean isGrayscale) throws Exception{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		com.sun.image.codec.jpeg.JPEGImageEncoder enc = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(byteArrayOutputStream);
		com.sun.image.codec.jpeg.JPEGEncodeParam params = enc.getDefaultJPEGEncodeParam(bufferedImage);
		params.setQuality(compressionQuality, false);//quality 0(very compressed, lossy) -> 1.0(less compressed,loss-less)
		enc.setJPEGEncodeParam(params);
		enc.encode(bufferedImage);
		return new VideoMediaSampleJPEG(width, height,sampleDuration, byteArrayOutputStream.toByteArray(), bitsPerPixel, isGrayscale);
	}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 12:04:55 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public abstract boolean equals(Object object);
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:33:23 PM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 4:23:04 PM)
 * @return java.lang.String
 */
public abstract String toString();
}
