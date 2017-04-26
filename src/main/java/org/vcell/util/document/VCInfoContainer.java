/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;
import cbit.vcell.geometry.GeometryInfo;
import cbit.image.VCImageInfo;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Insert the type's description here.
 * Creation date: (9/24/2003 12:30:17 PM)
 * @author: Frank Morgan
 */
public class VCInfoContainer implements java.io.Serializable{

	transient User user = null;
	transient VCImageInfo[] vcImageInfos = null;
	transient GeometryInfo[] geometryInfos = null;
	transient MathModelInfo[] mathModelInfos = null;
	transient BioModelInfo[] bioModelInfos = null;

	private byte[] compressedBytes = null;
	
/**
 * VCInfoContainer constructor comment.
 */
public VCInfoContainer(User argUser, VCImageInfo[] newVcImageInfos, GeometryInfo[] newGeometryInfos, MathModelInfo[] newMathModelInfos, BioModelInfo[] newBioModelInfos) {
		
	super();
	
	user=argUser;
	vcImageInfos = newVcImageInfos;
	geometryInfos = newGeometryInfos;
	mathModelInfos = newMathModelInfos;
	bioModelInfos = newBioModelInfos;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 12:43:24 PM)
 * @return cbit.vcell.biomodel.BioModelInfo[]
 */
public org.vcell.util.document.BioModelInfo[] getBioModelInfos() {
	if (bioModelInfos == null) {
		inflate();
	}
	return bioModelInfos;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 12:43:24 PM)
 * @return cbit.vcell.geometry.GeometryInfo[]
 */
public cbit.vcell.geometry.GeometryInfo[] getGeometryInfos() {
	if (geometryInfos == null) {
		inflate();
	}
	return geometryInfos;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 12:43:24 PM)
 * @return cbit.vcell.mathmodel.MathModelInfo[]
 */
public org.vcell.util.document.MathModelInfo[] getMathModelInfos() {
	if (mathModelInfos == null) {
		inflate();
	}
	return mathModelInfos;
}


/**
 * Insert the method's description here.
 * Creation date: (9/24/2003 12:50:51 PM)
 * @return cbit.vcell.server.User
 */
public org.vcell.util.document.User getUser() {
	if (user == null) {
		inflate();
	}
	return user;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 12:43:24 PM)
 * @return cbit.image.VCImageInfo[]
 */
public cbit.image.VCImageInfo[] getVCImageInfos() {
	if (vcImageInfos == null) {
		inflate();
	}
	return vcImageInfos;
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void inflate() {
	if (compressedBytes == null) {
		return;
	}

	try {
		//Object objArray[] =  { user, bioModelInfos, mathModelInfos, geometryInfos, vcImageInfos};
		Object objArray[] = (Object[])org.vcell.util.BeanUtils.fromCompressedSerialized(compressedBytes);
		user = (User)objArray[0];
		bioModelInfos = (BioModelInfo[])objArray[1];
		mathModelInfos = (MathModelInfo[])objArray[2];	
		geometryInfos = (GeometryInfo[])objArray[3];
		vcImageInfos = (VCImageInfo[])objArray[4];

		compressedBytes = null;
		
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		throw new RuntimeException(ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
	int compressedSize = s.readInt();
	compressedBytes = new byte[compressedSize];
	s.readFully(compressedBytes, 0, compressedSize);
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 9:33:11 AM)
 * @param out java.io.ObjectOutputStream
 * @exception java.io.IOException The exception description.
 */
private void writeObject(ObjectOutputStream s) throws IOException {
	Object objArray[] =  { user, bioModelInfos, mathModelInfos, geometryInfos, vcImageInfos};

	if (compressedBytes == null) {
		compressedBytes = org.vcell.util.BeanUtils.toCompressedSerialized(objArray);
	}
	s.writeInt(compressedBytes.length);
	s.write(compressedBytes);
}
}
