package org.vcell.util;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2004 11:50:57 AM)
 * @author: Jim Schaff
 */
public class BigStringTest {
/**
 * BigStringTest constructor comment.
 */
public BigStringTest() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 11:51:12 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		if (args.length!=1){
			System.out.println("usage: cbit.util.BigStringTest <text filename>");
			System.exit(0);
		}
		String filename = args[0];
	    java.io.FileReader reader = new java.io.FileReader(filename);
	    StringBuffer stringBuffer = new StringBuffer();
	    final int bufferSize = 65536;
	    char charBuffer[] = new char[bufferSize];
	    int numCharsRead;
	    while ((numCharsRead = reader.read(charBuffer,0,bufferSize)) > 0) {
		    stringBuffer.append(charBuffer,0,numCharsRead);
	    }
	    String str = stringBuffer.toString();
	    sun.io.CharToByteConverter charToByteConverter = sun.io.CharToByteConverter.getDefault();
	    System.out.println("platform's default character encoding is "+charToByteConverter.getCharacterEncoding()+" with maxBytesPerChar of "+charToByteConverter.getMaxBytesPerChar());
	    System.out.println("read file '"+filename+"', with "+str.length()+" characters, and "+str.getBytes().length+" bytes as encoded without compression");
	    org.vcell.util.BigString bs = new org.vcell.util.BigString(str);
	    for (int i = 0; i < 15; i++) {
	        long t1 = System.currentTimeMillis();
		    bs = new org.vcell.util.BigString(str);
	        org.vcell.util.BigString bs2 = (org.vcell.util.BigString)org.vcell.util.BeanUtils.cloneSerializable(bs);
	        //bs2.toString();
	        if (i==14){
		        bs2.toString();
	        }
	        long t2 = System.currentTimeMillis();
	        //if (i==14){
		        //System.out.println("last Time");
	        //}
	        System.out.println("!!!!!!!!!!!!!Time =" + (t2 - t1));
	    }
	} catch (Exception ex) {
		ex.printStackTrace();
	}	
}
}