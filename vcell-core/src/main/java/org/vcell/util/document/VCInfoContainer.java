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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.vcell.util.document.VCDocument.VCDocumentType;

import cbit.image.VCImageInfo;
import cbit.vcell.geometry.GeometryInfo;

/**
 * Insert the type's description here.
 * Creation date: (9/24/2003 12:30:17 PM)
 * @author: Frank Morgan
 */
public class VCInfoContainer implements java.io.Serializable{

	public static class PublicationInfo implements Serializable{
		private KeyValue versionKey;
		private String title;
		private String[] authors;
		private String citation;
		private String pubmedid;
		private String doi;
		private String url;
		private VCDocumentType vcDocumentType;
		private User user;
		private int theHashCode;
		public PublicationInfo(KeyValue versionKey, String title, String[] authors, String citation, String pubmedid,
				String doi, String url, VCDocumentType vcDocumentType, User user) {
			super();
			this.versionKey = versionKey;
			this.title = title;
			this.authors = authors;
			this.citation = citation;
			this.pubmedid = pubmedid;
			this.doi = doi;
			this.url = url;
			this.vcDocumentType = vcDocumentType;
			this.user = user;
			theHashCode = (versionKey.toString()+(doi!=null && doi.length()>0?doi.toString():(pubmedid!=null && pubmedid.length()>0&& !pubmedid.equals("0")?pubmedid.toString():""))).hashCode();
		}
		public KeyValue getVersionKey() {
			return versionKey;
		}
		public String getTitle() {
			return title;
		}
		public String[] getAuthors() {
			return authors;
		}
		public String getCitation() {
			return citation;
		}
		public String getPubmedid() {
			return pubmedid;
		}
		public String getDoi() {
			return doi;
		}
		public String getUrl() {
			return url;
		}
		public VCDocumentType getVcDocumentType() {
			return vcDocumentType;
		}
		public User getUser() {
			return user;
		}
		public int hashCode() {
			return theHashCode;
		}
	}
	transient User user = null;
	transient VCImageInfo[] vcImageInfos = null;
	transient GeometryInfo[] geometryInfos = null;
	transient MathModelInfo[] mathModelInfos = null;
	transient BioModelInfo[] bioModelInfos = null;
	transient PublicationInfo[] publicationInfos = null;

	private byte[] compressedBytes = null;
	
/**
 * VCInfoContainer constructor comment.
 */
public VCInfoContainer(User argUser, VCImageInfo[] newVcImageInfos, GeometryInfo[] newGeometryInfos, MathModelInfo[] newMathModelInfos, BioModelInfo[] newBioModelInfos,PublicationInfo[] newPublicationInfos) {
		
	super();
	
	user=argUser;
	vcImageInfos = newVcImageInfos;
	geometryInfos = newGeometryInfos;
	mathModelInfos = newMathModelInfos;
	bioModelInfos = newBioModelInfos;
	publicationInfos = newPublicationInfos;
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

public PublicationInfo[] getPublicationInfos() {
	if (publicationInfos == null) {
		inflate();
	}
	return publicationInfos;	
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
		publicationInfos = (PublicationInfo[])objArray[5];
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
	Object objArray[] =  { user, bioModelInfos, mathModelInfos, geometryInfos, vcImageInfos,publicationInfos};

	if (compressedBytes == null) {
		compressedBytes = org.vcell.util.BeanUtils.toCompressedSerialized(objArray);
	}
	s.writeInt(compressedBytes.length);
	s.write(compressedBytes);
}
}
