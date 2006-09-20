package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
public class TiffTester
{

public static void main(String argv[]) {
	if (argv.length!=1 && argv.length!=2){
//         System.out.println("usage:");
//	 System.out.println("   java cbit.image.TiffTester filename [outfilename]");
//         return;
		int xsize = 100;
		int ysize = 100;
		int zsize = 1;
		byte data[] = new byte[xsize*ysize*zsize];
		for (int i=0;i<xsize;i++){ 
			for (int j=0;j<ysize;j++){
				for (int k=0;k<1;k++){
					byte pixel = (byte)(64.0 + 63*Math.sin(((float)(i*j))/((xsize*ysize)/10)));
					data[i+j*xsize+k*xsize*ysize] = pixel;
				}
			}
		}
		TiffImage img = new TiffImage(xsize, ysize, zsize, data);
		try {
			img.write("special.tif",ByteOrder.Unix);
			System.out.println("generating test file <special.tif>");
			System.out.println("normal usage:");
			System.out.println("   java cbit.image.TiffTester filename [outfilename]");
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		return;
	}
	//
	// test reading via filename
	//
	TiffImage imgFile = new TiffImage();
	try {
		imgFile.read(new FileTiffInputSource(argv[0]));
	}catch (Exception e){
		e.printStackTrace();
		System.out.println("failure reading Tiff file: " + argv[0]);
		return;
	}
	//
	// test reading via byte array
	//
	TiffImage imgByteArray = new TiffImage();
	try {
		java.io.File inputFile = new java.io.File(argv[0]);
		java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(inputFile));
		byte buffer[] = new byte[(int)inputFile.length()];
		dis.readFully(buffer);
		imgByteArray.read(new ByteArrayTiffInputSource(buffer));
	}catch (Exception e){
		e.printStackTrace();
		System.out.println("failure reading Tiff file: " + argv[0]);
		return;
	}
	if (argv.length==2){
		try {
			imgFile.write(argv[1]+".file.tif",ByteOrder.Unix);
			imgByteArray.write(argv[1]+".byteArray.tif",ByteOrder.Unix);
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("failure writing Tiff file: " + argv[1]);
			return;	    
		}
	}
}                  
}
