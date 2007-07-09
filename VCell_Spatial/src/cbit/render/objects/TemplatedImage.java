package cbit.render.objects;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.Serializable;
import java.util.ArrayList;
/**
 * This type was created in VisualAge.
 */
public class TemplatedImage<datatype extends Number> extends Image implements Serializable {
	private datatype pixels[] = null;

	public TemplatedImage(TemplatedImage<datatype> image) throws ImageException {
		super(image);
		this.pixels = image.getPixels();
	}
/**
 * This method was created in VisualAge.
 * @param pix datatype[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public TemplatedImage(datatype pixels[], org.vcell.util.Extent aExtent, int aNumX, int aNumY, int aNumZ) throws ImageException {
	super(aExtent, aNumX, aNumY, aNumZ);
	if (aNumX*aNumY*aNumZ != pixels.length){
		throw new IllegalArgumentException("size ("+aNumX+","+aNumY+","+aNumZ+") not consistent with "+pixels.length+" pixels");
	}
	this.pixels = pixels;
}
public datatype getPixel(int x, int y, int z) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	return (datatype) getPixels()[index];
}
/**
 * getPixels method comment.
 */
public datatype[] getPixels() {
	return pixels;
}
///**
// * This method was created in VisualAge.
// * @return datatype[]
// */
//public datatype[] getPixelsCompressed() throws ImageException {
//	try {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		DeflaterOutputStream dos = new DeflaterOutputStream(bos);
//		//DeflaterOutputStream dos = new DeflaterOutputStream(bos,new Deflater(5,false));
//		dos.write(pixels,0,pixels.length);
//		dos.close();
//		return bos.toByteArray();
//	}catch (IOException e){
//		e.printStackTrace(System.out);
//		throw new ImageException(e.getMessage());
//	}
//}
/**
 * This method was created in VisualAge.
 * @return datatype
 * @param x int
 * @param y int
 * @param z int
 */
public void setPixel(int x, int y, int z, datatype newValue) throws ImageException {
	if (x<0||x>=getNumX()||y<0||y>=getNumY()||z<0||z>=getNumZ()){
		throw new IllegalArgumentException("("+x+","+y+","+z+") is not inside (0,0,0) and ("+(getNumX()-1)+","+(getNumY()-1)+","+(getNumZ()-1)+")");
	}
	int index = x + getNumX()*(y + z*getNumY()); 
	getPixels()[index] = newValue;
}


/**
 * This method was created in VisualAge.
 * @return int[]
 */
public datatype[] getUniquePixelValues() throws ImageException{
	datatype pixels[] = getPixels();
	int imageLength = pixels.length;
	if (imageLength==0){
		return null;
	}
	
	ArrayList<datatype> pixelValueArray = new ArrayList<datatype>();
	pixelValueArray.add(pixels[0]);

	for (int i=0;i<imageLength;i++){
		datatype currPixel = pixels[i];

		//
		// look for current pixel in list
		//
		boolean found = false;
		for (int j=0;j<pixelValueArray.size();j++){
			if (pixelValueArray.get(j)==currPixel){
				found = true;
			}
		}
		//
		// if current pixel not found, extend list and add pixel to end
		//
		if (!found){
			pixelValueArray.add(currPixel);
		}
	}
	return (datatype[])pixelValueArray.toArray();
}


///**
// * This method was created by a SmartGuide.
// * @return cbit.image.FileImage
// * @param images cbit.image.FileImage[]
// * @exception java.lang.Exception The exception description.
// */
//public TemplatedImage(TemplatedImage<datatype> images[], Extent newExtent) throws ImageException {
//	super(newExtent,images[0].getNumX(),images[0].getNumY(),images[0].getNumZ()*images.length);
//	for (int i=1;i<images.length;i++){
//		if (images[i].getNumX() != nX){
//			throw new ImageException("image "+(i+1)+" x dimension doesn't match the first image");
//		}	
//		if (images[i].getNumY() != nY){
//			throw new ImageException("image "+(i+1)+" y dimension doesn't match the first image");
//		}	
//		if (images[i].getNumZ() < 1){
//			throw new ImageException("image "+(i+1)+" z dimension must be at least 1");
//		}	
//		nZ += images[i].getNumZ();
//	}
//	int nTotal = nX*nY*nZ;
//	datatype bigBuffer[] = new datatype[nTotal];
//	int index = 0;
//	for (int i=0;i<images.length;i++){
//		datatype currPix[] = images[i].getPixels();
//		int currTotal = images[i].getNumX()*images[i].getNumY()*images[i].getNumZ();
//		for (int j=0;j<currTotal;j++){
//			bigBuffer[index++] = currPix[j];
//		}
//	}		
//	TemplatedImage<datatype> byteImage = new TemplatedImage<datatype>(bigBuffer,new cbit.util.Extent(extent0.getX(),extent0.getY(),extent0.getZ()*images.length),nX,nY,nZ);
//	return byteImage;
//}
}
