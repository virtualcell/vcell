package cbit.vcell.export.quicktime.atoms;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.quicktime.*;
import java.io.*;
/**
 * This type was created in VisualAge.
 */
public class MediaAtom extends Atoms {

	public static final String type = "mdia";
	protected MediaHeader mediaHeader;
	protected HandlerReference handlerReference;
	protected MediaInformation mediaInformation;
	protected UserData userData;
	
/**
 * This method was created in VisualAge.
 * @param dReference cbit.vcell.export.quicktime.DataReference
 */
public MediaAtom(MediaHeader mdhd, HandlerReference hdlr, MediaInformation minf) {
	this(mdhd, hdlr, minf, null);
}
/**
 * This method was created in VisualAge.
 * @param dReference cbit.vcell.export.quicktime.DataReference
 */
public MediaAtom(MediaHeader mdhd, HandlerReference hdlr, MediaInformation minf, UserData udta) {
	mediaHeader = mdhd;
	handlerReference = hdlr;
	mediaInformation = minf;
	userData = udta;
	size = 8 + mdhd.size + hdlr.size;
	if (minf != null) size += minf.size;
	if (udta != null) size += udta.size;
}
/**
 * writeData method comment.
 */
public boolean writeData(DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeBytes(type);
		mediaHeader.writeData(out);
		handlerReference.writeData(out);
		if (mediaInformation != null) mediaInformation.writeData(out);
		if (userData != null) userData.writeData(out);
		return true;
	} catch (IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}
