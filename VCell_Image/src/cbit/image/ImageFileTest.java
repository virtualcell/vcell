package cbit.image;

/**
 * Insert the type's description here.
 * Creation date: (1/11/2003 12:19:51 PM)
 * @author: Jim Schaff
 */
public class ImageFileTest {
/**
 * Insert the method's description here.
 * Creation date: (1/11/2003 12:20:09 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		if (args.length!=1){
			System.out.println("usage: ImageFileTest infile");
			System.exit(1);
		}
		String filename = args[0];
		System.out.println("Reading file "+filename);
		ImageFile imageFile = new ImageFile(filename);
		System.out.println("Writing Image "+filename+".tif");
		imageFile.writeAsTIFF(filename+".tif");
		System.out.println("read and write complete");
	} catch (Throwable e) {
		e.printStackTrace(System.out);
		System.exit(1);
	}
}
}
