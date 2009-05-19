package org.vcell.util.document;


import org.vcell.util.Extent;
import org.vcell.util.ISize;

import cbit.image.VCImage;
import cbit.sql.*;
/**
 * Insert the type's description here.
 * Creation date: (5/6/2004 12:53:51 PM)
 * @author: Ion Moraru
 */
public interface VCDocument extends java.io.Serializable, org.vcell.util.Matchable {
	// document types
	public static final int BIOMODEL_DOC = 0;
		public static final int BIO_OPTION_DEFAULT = 0;
	public static final int MATHMODEL_DOC = 1;
		public static final int MATH_OPTION_NONSPATIAL = 0;
		public static final int MATH_OPTION_SPATIAL = 1;
		public static final int MATH_OPTION_FROMBIOMODELAPP = 2;

	public static final int GEOMETRY_DOC = 2;
		public static final int GEOM_OPTION_1D = 1;
		public static final int GEOM_OPTION_2D = 2;
		public static final int GEOM_OPTION_3D = 3;
		public static final int GEOM_OPTION_DBIMAGE = 4;
		public static final int GEOM_OPTION_FILE = 5;
		public static final int GEOM_OPTION_FIELDDATA = 6;
	public static final int XML_DOC = 3;

	//Document Creation Info
	public static class DocumentCreationInfo {
		private int documentType;
		private int option;
		public DocumentCreationInfo(int documentType,int option){
			this.documentType = documentType;
			this.option = option;
		}
		public int getDocumentType(){
			return documentType;
		}
		public int getOption(){
			return option;
		}
	};
	public static class GeomFromFieldDataCreationInfo extends DocumentCreationInfo{

		private ExternalDataIdentifier externalDataID = null;
		private String varName = null;
		private int timeIndex = 0;
		public GeomFromFieldDataCreationInfo(ExternalDataIdentifier edi, String v, int ti) {
			super(GEOMETRY_DOC,GEOM_OPTION_FIELDDATA);
			externalDataID = edi;
			varName = v;
			timeIndex = ti;
		}
		public ExternalDataIdentifier getExternalDataID() {
			return externalDataID;
		}
		public String getVarName() {
			return varName;
		}
		public int getTimeIndex() {
			return timeIndex;
		}
		
	}
//	public static class GeomFromFieldDataCreationInfo extends DocumentCreationInfo{
//		private byte[] imageData;
//		private ISize imageSize;
//		private Version imageVersion;
//		private Extent imageExtent;
//		private String imageDescription;
//		public GeomFromFieldDataCreationInfo(int documentType,int option,
//				byte[] imageData,ISize imageSize,Extent imageExtent,Version imageVersion,String imageDescription){
//			super(documentType,option);
//			this.imageData = imageData;
//			this.imageSize = imageSize;
//			this.imageVersion = imageVersion;
//			this.imageExtent = imageExtent;
//			this.imageDescription = imageDescription;
//		}
//		public byte[] getImageData() {
//			return imageData;
//		}
//		public ISize getImageSize() {
//			return imageSize;
//		}
//		public Version getImageVersion() {
//			return imageVersion;
//		}
//		public Extent getImageExtent() {
//			return imageExtent;
//		}
//		public String getImageDescription(){
//			return imageDescription;
//		}
//	};
/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 1:03:55 PM)
 * @return java.lang.String
 */
String getDescription();
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:10:46 PM)
 * @return int
 */
int getDocumentType();
/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 1:03:55 PM)
 * @return java.lang.String
 */
String getName();
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:18:55 PM)
 * @return cbit.sql.Version
 */
Version getVersion();
/**
 * Insert the method's description here.
 * Creation date: (9/16/2004 12:33:13 PM)
 */
void refreshDependencies();
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 1:07:43 AM)
 * @param newName java.lang.String
 */
void setDescription(String description) throws java.beans.PropertyVetoException;
/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 1:07:43 AM)
 * @param newName java.lang.String
 */
void setName(String newName) throws java.beans.PropertyVetoException;
}
