package cbit.vcell.export.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.zip.DataFormatException;

import org.vcell.util.Coordinate;

import cbit.image.ImagePaneModel;
import cbit.vcell.export.gloworm.quicktime.VideoMediaSample;
import cbit.vcell.export.gloworm.quicktime.VideoMediaSampleJPEG;
import cbit.vcell.export.gloworm.quicktime.VideoMediaSampleRaw;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Dummy parent class.
 */
public abstract class FormatSpecificSpecs implements Serializable {
	public static final int PARTICLE_NONE = 0;
	public static final int PARTICLE_ALL = 1;
	public static final int PARTICLE_SELECT = 2;	
	
	public final static int CODEC_NONE = 0;
	public final static int CODEC_JPEG = 1;

	public static boolean isGrayScale(int[] argbData){
		for (int i = 0; i < argbData.length; i++) {
			if((argbData[i]&0x000000FF) != ((argbData[i]>>8)&0x000000FF) ||
				(argbData[i]&0x000000FF) != ((argbData[i]>>16)&0x000000FF)){
				return false;
			}
			
		}
		return true;
	}
	public static VideoMediaSample getVideoMediaSample(
			int width,int height,int sampleDuration,boolean isGrayScale,int compressionType,float compressionQuality,int[] argbData) throws Exception{
			
			if(isGrayScale){
				//convert 32bit to 8bit
				BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
				byte[] buffer = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();

				for (int i = 0; i < buffer.length; i++) {
					buffer[i] = (byte)(argbData[i]&0x000000FF);
				}
				if(compressionType == FormatSpecificSpecs.CODEC_JPEG){
					return FormatSpecificSpecs.encodeJPEG(bufferedImage, compressionQuality, width, height, sampleDuration, Byte.SIZE, true);
				}else{
					return new VideoMediaSampleRaw(width, height, sampleDuration,buffer,8, true);
				}
			}
			if(compressionType == FormatSpecificSpecs.CODEC_JPEG){
				BufferedImage bufferedImage = null;
				bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				System.arraycopy(argbData, 0, ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData(), 0, argbData.length);
				return FormatSpecificSpecs.encodeJPEG(bufferedImage, compressionQuality, width, height, sampleDuration, Integer.SIZE,false);
			}else{
				ByteArrayOutputStream sampleBytes = new ByteArrayOutputStream();
				DataOutputStream sampleData = new DataOutputStream(sampleBytes);
				for (int j=0;j<argbData.length;j++){
					sampleData.writeInt(argbData[j]);
				}
				sampleData.close();
				byte[] bytes = sampleBytes.toByteArray();
				return new VideoMediaSampleRaw(width, height, sampleDuration,bytes,Integer.SIZE, false);
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
	
		public static int getSliceCount(boolean bAllSlices,int normalAxis,CartesianMesh mesh){
		if (!bAllSlices){
			return 1;
		}
		switch (normalAxis){
			case Coordinate.X_AXIS:{
				// YZ plane
				return mesh.getSizeX();
			}
			case Coordinate.Y_AXIS:{
				// ZX plane
				return mesh.getSizeY();
			}
			case Coordinate.Z_AXIS:{
				// XY plane
				return mesh.getSizeZ();

			}
			default:{
				throw new IllegalArgumentException("unexpected normal axis "+normalAxis);
			}
		}
	}
		
		/**
		 * Insert the method's description here.
		 * Creation date: (3/2/2001 12:03:46 AM)
		 * @return java.awt.Dimension
		 */
		public static Dimension getMeshDimensionUnscaled(int normalAxis,CartesianMesh mesh) {
			switch (normalAxis){
				case Coordinate.X_AXIS:{
					//
					// YZ plane
					//
					return new Dimension(mesh.getSizeY(),mesh.getSizeZ());
				}
				case Coordinate.Y_AXIS:{
					//
					// ZX plane
					//
					return new Dimension(mesh.getSizeZ(),mesh.getSizeX());
				}
				case Coordinate.Z_AXIS:{
					//
					// XY plane
					//
					return new Dimension(mesh.getSizeX(),mesh.getSizeY());

				}
				default:{
					throw new IllegalArgumentException("unexpected normal axis "+normalAxis);
				}
			}
		}
		
		public static Dimension getImageDimension(int meshMode,int imageScale,CartesianMesh mesh,int normalAxis) {
			ImagePaneModel imagePaneModel = new ImagePaneModel();
			imagePaneModel.setMode(meshMode);
			imagePaneModel.setZoom(imageScale);
			Dimension dim = FormatSpecificSpecs.getMeshDimensionUnscaled(normalAxis,mesh);
			dim.width = imagePaneModel.getScaledLength(dim.width);
			dim.height = imagePaneModel.getScaledLength(dim.height);
			return dim;
		}

		public static Dimension getMirrorDimension(int mirroringType,int originalWidth,int originalHeight){
			Dimension mirrorDim = new Dimension(originalWidth,originalHeight);
			if ((mirroringType == ExportConstants.MIRROR_LEFT) || (mirroringType == ExportConstants.MIRROR_RIGHT)){
				mirrorDim.width = 2 * originalWidth;
			}
			if ((mirroringType == ExportConstants.MIRROR_TOP) || (mirroringType == ExportConstants.MIRROR_BOTTOM)){
				mirrorDim.height = 2 * originalHeight;
			}
			return mirrorDim;
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
