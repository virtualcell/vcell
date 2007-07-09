package cbit.image;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.zip.ZipInputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.lang.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;


public class ImageFile
{
	private static final int IMAGENOTREADY		= 0;
	private static final int PIXELGRABBERERROR	= 1;
	private static final int PIXELGRABBEROK		= 2;

	public static final int BYTEORDER_PC = 1;
	public static final int BYTEORDER_UNIX = 2;
	
	private int[] originalRGB = null;
	private double[] originalDouble = null;
	private int sizeX = 0;
	private int sizeY = 0;
	private int sizeZ = 0;
	private boolean bValid = false;
	private String imageName = null;

/**
 * This method was created in VisualAge.
 * @param filename java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public ImageFile(byte data[]) throws ImageException, IOException {
	try {
		//
		// try native format
		//
		NativeImage nativeImage = new NativeImage(data);
		loadImage(nativeImage.getJavaImage());
	}catch (Exception e){
		System.out.println("ImageFile.ImageFile(byte[]), exception loading native format, trying TIFF, exception="+e.getMessage());
		try {				
			// try TIFF format
			loadTIFFFormat(new ByteArrayTiffInputSource(data));
			return;
		}catch (Exception e2){
			System.out.println("ImageFile:ImageFile(byte[]), exception loading TIFF, trying zip format, exception="+e2.getMessage());
			byte entries[][] = getZipFileEntries(data);
			if (entries!=null){
				VCImage vcImage = getVCImageFromZSeries(entries);
				createFromVCImage(vcImage);
				System.out.println("ImageFile.ImageFile(byte[]), read successfully");
			}else{
				throw new ImageException("Image format not recognized");
			}
		}
	}
	
}


/**
 * This method was created by a SmartGuide.
 * @param rgb int[]
 * @param x int
 * @param y int
 * @param z int
 * @exception java.lang.Exception The exception description.
 */
public ImageFile (DisplayImage displayImage) throws ImageException {
	originalRGB = displayImage.getRGB();
	sizeX = displayImage.getSizeX();
	sizeY = displayImage.getSizeY();
	sizeZ = displayImage.getSizeZ();
	bValid = true;
	imageName = displayImage.getName();
}


/**
 * This method was created in VisualAge.
 * @param image image.Image
 */
public ImageFile(VCImage vcImage) throws ImageException {
	createFromVCImage(vcImage);
}


/**
 * This method was created in VisualAge.
 * @param img java.awt.Image
 */
public ImageFile(java.awt.Image img) throws ImageException{
	loadImage(img);
}


/**
 * This method was created in VisualAge.
 * @param filename java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public ImageFile(String filename) throws ImageException, FileNotFoundException, IOException {
	File file = new File(filename);
	if (!file.exists()){
		throw new ImageException("image file '"+filename+"' not found");
	}
	try {
		//
		// try native format
		//
		loadNativeFormat(filename);
		System.out.println("ImageFile.ImageFile("+filename+"), read successfully as native format");
	}catch (Exception e){
		System.out.println("ImageFile.ImageFile("+filename+"), exception loading native format, trying TIFF, exception="+e.getMessage());
		try {				
			// try TIFF format
			loadTIFFFormat(new FileTiffInputSource(filename));
			System.out.println("ImageFile.ImageFile("+filename+"), read successfully as TIFF");
			return;
		}catch (Exception e2){
			System.out.println("ImageFile:ImageFile("+filename+"), exception loading TIFF, trying zip format, exception="+e2.getMessage());
			byte buffer[] = new byte[65536];
			FileInputStream fis = new FileInputStream(filename);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int numBytesRead = 0;
			while ((numBytesRead = fis.read(buffer)) > 0){
				bos.write(buffer,0,numBytesRead);
			}
			byte imageData[] = bos.toByteArray();
			byte entries[][] = getZipFileEntries(imageData);
			if (entries!=null){
				VCImage vcImage = getVCImageFromZSeries(entries);
				createFromVCImage(vcImage);
				System.out.println("ImageFile.ImageFile("+filename+"), read successfully");
			}else{
				throw new ImageException("Image format not recognized for file '"+filename+"'");
			}
		}
	}
	
}


/**
 * This method was created by a SmartGuide.
 * @param urlString java.lang.String
 */
private void createFromURL(java.net.URL url) throws InterruptedException {
System.out.print("creating image from url ...");
	java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(url);
System.out.println("done");
System.out.print("getting image width ...");
	ImageObserver imageObserver = new ImageObserver() {
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height){
			return false;
		}
	};
	while (image.getWidth(imageObserver)<0){}
System.out.println("done");
	while (image.getHeight(imageObserver)<0){}
	sizeX = image.getWidth(imageObserver);
	sizeY = image.getHeight(imageObserver);
System.out.println("width="+sizeX+" height="+sizeY);
	originalRGB = new int[sizeX*sizeY]; 
	PixelGrabber pg = new PixelGrabber(image,0,0,sizeX,sizeY,originalRGB,0,sizeX);
	pg.grabPixels();
	if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
		bValid=false;
		throw new InterruptedException("image fetch aborted or errored");
	}	
	bValid=true;
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (9/16/2002 1:46:40 PM)
 * @param vcImage cbit.image.VCImage
 */
private void createFromVCImage(VCImage vcImage) throws ImageException {
	sizeX = vcImage.getNumX();
	sizeY = vcImage.getNumY();
	sizeZ = vcImage.getNumZ();
	if (sizeX*sizeY*sizeZ!=vcImage.getPixels().length){
		throw new ImageException("pixelData not properly formed");
	}
	originalRGB = new int[sizeX*sizeY*sizeZ];
	for (int i=0;i<vcImage.getPixels().length;i++){
		int pixel = ((int)vcImage.getPixels()[i])&0xff;
		originalRGB[i] = new java.awt.Color(pixel,pixel,pixel).getRGB();
	}
	bValid = true;
	imageName = (vcImage.getVersion()!=null)?vcImage.getVersion().getName():"unnamedImage";	
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.DisplayImage
 */
public DisplayImage getDisplayImage() throws ImageException {
	if (originalDouble!=null){
		return new DisplayImage(originalDouble,sizeX,sizeY,sizeZ);
	}else{
		return new DisplayImage(originalRGB,sizeX,sizeY,sizeZ);
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getFilename() throws ImageException {
	if (!bValid){
		throw new ImageException("image not valid");
	}	
	return imageName;
}


	private int getRawPixelsFromImage(Image fileImage)
	{
		sizeX = 0;
		sizeY = 0;
		sizeZ = 1;
		if((sizeX = fileImage.getWidth(null)) != -1)
		{
			if((sizeY = fileImage.getHeight(null)) != -1)
			{
				originalRGB = null;
				originalRGB = new int[sizeX*sizeY];
				java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(fileImage,0,0,sizeX,sizeY,originalRGB,0,sizeX);
				try
				{
					if(pg.grabPixels() != true)
					{
						System.out.println("ImageProcess: ERROR Grabbing Pixels");
						return(PIXELGRABBERERROR);
					}
					else return(PIXELGRABBEROK);
				}
				catch(InterruptedException e) {return(PIXELGRABBERERROR);}
			}
		}
		return(IMAGENOTREADY);
	}


/**
 * This method was created by a SmartGuide.
 * @return int[]
 */
public int[] getRGB() throws ImageException {
	if (!bValid){
		throw new ImageException("image not valid");
	}	
	return originalRGB;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getSizeX() throws ImageException {
	if (!bValid){
		throw new ImageException("image not valid");
	}	
	return sizeX;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getSizeY() throws ImageException {
	if (!bValid){
		throw new ImageException("image not valid");
	}	
	return sizeY;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getSizeZ() throws ImageException {
	if (!bValid){
		throw new ImageException("image not valid");
	}	
	return sizeZ;
}


/**
 * This method was created in VisualAge.
 * @return int[]
 */
public int[] getUniquePixelValues() {
	int imageLength = originalRGB.length;
	if (imageLength==0){
		return null;
	}
	
	int pixelValueArray[] = new int[1];
	pixelValueArray[0] = originalRGB[0];

	for (int i=0;i<imageLength;i++){
		int currPixel = originalRGB[i];

		//
		// look for current pixel in list
		//
		boolean found = false;
		for (int j=0;j<pixelValueArray.length;j++){
			if (pixelValueArray[j]==currPixel){
				found = true;
			}
		}
		//
		// if current pixel not found, extend list and add pixel to end
		//
		if (!found){
			int newArray[] = new int[pixelValueArray.length+1];
			for (int j=0;j<pixelValueArray.length;j++){
				newArray[j] = pixelValueArray[j];
			}
			newArray[pixelValueArray.length] = currPixel;
			pixelValueArray = newArray;
		}
	}
	return pixelValueArray;
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 */
public VCImage getVCImage() throws ImageException {
	if (originalRGB==null && originalDouble==null){
		throw new ImageException("data is not loaded");
	}
	
	byte bytepix[] = null;
	
	
	if (originalRGB!=null){
		int[] uniquePixelValues = getUniquePixelValues();
		if(uniquePixelValues.length >256){
			throw new ImageException("VCImage can't have more than 256 pixel values");
		}
		bytepix = new byte[originalRGB.length];
		for (int i=0;i<originalRGB.length;i++){
			int oRGBIndex = -1;
			for(int j=0;j<uniquePixelValues.length;j+= 1){
				if(uniquePixelValues[j] == originalRGB[i]){
					oRGBIndex = j;
					break;
				}
			}
			if(oRGBIndex >= 0){
				bytepix[i] = ((byte)oRGBIndex);
			}else{
				throw new ImageException("Unique Pixel value missing in originalRGB array");
			}
			//int intPix = 0xffffff&originalRGB[i];
			//int red = intPix&0xff;
			//int green = (intPix>>8)&0xff;
			//int blue = (intPix>>16)&0xff;
			//byte pix = (byte)Math.max(red,Math.max(green,blue));
			//bytepix[i] = pix;
		}
	}else{
		bytepix = new byte[originalDouble.length];
		for (int i=0;i<originalDouble.length;i++){
			bytepix[i] = (byte)(0xff&(int)originalDouble[i]);
		}
	}
	VCImageUncompressed vci = new VCImageUncompressed(null,bytepix,new org.vcell.util.Extent(getSizeX(),getSizeY(),getSizeZ()),getSizeX(),getSizeY(),getSizeZ());
	return vci;
}


/**
 * This method was created in VisualAge.
 * @return VCImage
 * @param zSections java.util.Vector
 */
private cbit.image.VCImage getVCImageFromZSeries(byte[][] zSections) throws ImageException {
	Vector images = new Vector();
	try {
		for (int i = 0; i < zSections.length; i++){
			byte imageData[] = zSections[i];
			ImageFile imageFile = new cbit.image.ImageFile(imageData);
			images.addElement(imageFile.getVCImage());
		}
	} catch (Exception e) {
		throw new ImageException("Error reading images from archive: " + e.getMessage());
	}
	cbit.image.VCImage imagesArray[] = new cbit.image.VCImage[images.size()];
	images.copyInto(imagesArray);
	cbit.image.VCImage newVCImage;
	try {
		newVCImage = cbit.image.VCImage.concatenateZSeries(imagesArray);
	} catch (Exception e) {
		throw new ImageException("Error constructing 3D image from Z series: " + e.getMessage());
	}
	return newVCImage;
}


/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 * @param imageData byte[]
 */
private byte[][] getZipFileEntries(byte[] imageData) throws ImageException {
	if ((imageData == null) || (imageData.length == 0)) {
		throw new IllegalArgumentException("imageData length = 0");
	}
	try {
		Vector keyVector = new Vector();
		Hashtable hash = new Hashtable();
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(imageData));
		ZipEntry ze = null;
		while ((ze = zis.getNextEntry()) != null) {
			//log("Zip entry Name =" + ze.getName() + " Size =" + ze.getSize());
			if (ze.isDirectory() == false) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int count;
				byte b[] = new byte[65536];
				while ((count = zis.read(b, 0, b.length)) != -1) {
					bos.write(b, 0, count);
				}
				//log("Zip entry Name =" + ze.getName() + " MSiz =" + ze.getSize());
				keyVector.addElement(ze.getName());
				hash.put(ze.getName(), bos.toByteArray());
			}
		}
		if (hash.size() > 0) {
			//
			// do a bubble sort of the keys to sort the entries
			//
			for (int i=0;i<keyVector.size();i++){
				for (int j=i+1;j<keyVector.size();j++){
					//
					// if string[i] > string[j] then swap
					//
					if (((String)keyVector.elementAt(i)).compareTo((String)keyVector.elementAt(i)) > 0){
						Object temp = keyVector.elementAt(i);
						keyVector.setElementAt(keyVector.elementAt(j),i);
						keyVector.setElementAt(temp,j);
					}
				}
			}
			//
			// now that keyVector is sorted, use it to populate the data array
			//
			byte entries[][] = new byte[keyVector.size()][];
			
			for (int i=0;i<keyVector.size();i++){
				entries[i] = (byte[])hash.get(keyVector.elementAt(i));
			}

			return entries;
		}else{
			return null; // not a zip file
		}
	} catch (ZipException zex) {
		throw new ImageException("Zip file corrupt " + zex.getMessage());
	} catch (Exception e) {
		throw new ImageException("Error Reading Zip Data " + e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param image java.awt.Image
 * @param infoflags int
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 */
public final boolean imageUpdate(java.awt.Image image, int infoflags, int x, int y, int width, int height) {
	return false;
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isValid() {
	return bValid;
}


private void loadImage(java.awt.Image fileImage) throws ImageException {

//************************ Franks stuff ********************************

	MediaTracker mt = new MediaTracker(new Frame());
	mt.addImage(fileImage,0);
	try {
		mt.waitForID(0);
	} catch (InterruptedException e) {
		e.printStackTrace(System.out);
	} catch (NullPointerException e){
		throw new ImageException("error reading native image file "+imageName);
	}
	if(mt.isErrorID(0) == true){
		throw new ImageException("error reading native image file "+imageName);
	}

//************************ Franks stuff ********************************

	int result;
	do{ 
		result = getRawPixelsFromImage(fileImage); 
	} while(result == IMAGENOTREADY);
	
	if (result == PIXELGRABBERERROR){
		throw new ImageException("error interpreting parsing image file "+imageName);
	}	
	bValid = true;
}


void loadNativeFormat(String imageName) throws ImageException, FileNotFoundException {
	bValid = false;
	this.imageName = imageName;
	if(imageName == null){
		throw new ImageException("image filename null");
	}	
	File nf = new File(imageName);
	if(nf.exists() == false) throw new FileNotFoundException("file "+imageName+" not found");
	
	Image fileImage = Toolkit.getDefaultToolkit().getImage(imageName);
	loadImage(fileImage);
}


void loadRAWFormat(String imageName, int width, int height, int depth, int offset, 	boolean bColor) throws IOException {
	bValid = false;
	this.imageName = imageName;
	
	int channelsPerPixel = 1;
	if (bColor){
		channelsPerPixel = 3;
	}	
	
	//
	// assume only 8-bit grayscale or RGB images
	//
	int bytesPerChannel = 1;
	int byteOrder = BYTEORDER_PC;

	int size = width*height*depth;

	int temp[] = new int[size];
	FileInputStream fis = new FileInputStream(imageName);
	DataInputStream dis = new DataInputStream(fis);
	dis.skip(offset);

	if (bytesPerChannel == 1){
		for(int c = 0;c < size;c+= 1) temp[c] = dis.readUnsignedByte();
	}else if (bytesPerChannel == 2)	{
		for(int c = 0;c < size;c+= 1) temp[c] = dis.readShort();
	}
	
	fis.close();
		
	if(bytesPerChannel == 1){
		originalRGB = new int[size];
	}else if (bytesPerChannel == 2){
		originalRGB = temp;
	}

	if(channelsPerPixel == 1){
		if(bytesPerChannel == 1){
			for(int c = 0;c < size;c+= 1)	{
				temp[c] &= 0x000000ff;
				originalRGB[c] = 0x000000ff;originalRGB[c]<<= 8;
				originalRGB[c] |= temp[c];originalRGB[c]<<= 8;
				originalRGB[c] |= temp[c];originalRGB[c]<<= 8;
				originalRGB[c] |= temp[c];
				if((c%2000) == 1000){
					try{
						Thread.sleep(1);
					}catch(InterruptedException e){}
				}
			}
		}else if ((bytesPerChannel == 2) && (byteOrder == BYTEORDER_PC)){ //SHORT bytes need switching
			int low,hi;
			for(int c = 0;c < size;c+= 1)	{
				low = originalRGB[c]%256;
				hi = originalRGB[c]/256;
				originalRGB[c] = low*256 + hi;
			}
		}
	}else if(channelsPerPixel == 3){
		for(int c = 0;c < size;c+= 1)	{
			temp[c] &= 0x000000ff;
			int d = c*3;
			originalRGB[c] = 0x000000ff;originalRGB[c]<<= 8;
			originalRGB[c] |= temp[d];originalRGB[c]<<= 8;
			originalRGB[c] |= temp[d+1];originalRGB[c]<<= 8;
			originalRGB[c] |= temp[d+2];
			if((c%2000) == 1000){
				try{
					Thread.sleep(1);
				} catch(InterruptedException e){}
			}
		}
	}
	sizeX = width;
	sizeY = height;
	sizeZ = depth;
	bValid = true;
}


/**
 * This method was created by a SmartGuide.
 * @param imageName java.lang.String
 * @exception java.lang.Exception The exception description.
 */
void loadTIFFFormat(TiffInputSource tiffInputSource) throws TiffException, IOException {
	bValid = false;
	this.imageName = "unknown.tif";
	TiffImage tiffImage = new TiffImage();
	try {
		tiffImage.read(tiffInputSource);
		sizeX = tiffImage.getSizeX();
		sizeY = tiffImage.getSizeY();
		sizeZ = tiffImage.getSizeZ();
		originalDouble = tiffImage.getDoublePixelData();
		bValid = true;
	} catch (TiffException e) {
		throw new TiffException(e.getMessage());
	} finally {
		tiffImage.close();
	}
}


/**
 * This method was created in VisualAge.
 * @param filename java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void writeAsTIFF(String filename) throws IOException, ImageException {
	byte byteData[] = new byte[originalRGB.length];
	for (int i=0;i<originalRGB.length;i++){
		byteData[i] = (byte)(new Color(originalRGB[i])).getBlue();
	}
	TiffImage tiffImage = new TiffImage(getSizeX(),getSizeY(),getSizeZ(),byteData);
	tiffImage.write(filename,ByteOrder.Unix);
}
}